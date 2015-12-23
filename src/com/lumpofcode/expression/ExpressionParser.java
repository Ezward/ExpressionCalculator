package com.lumpofcode.expression;

import com.lumpofcode.annotation.Immutable;
import com.lumpofcode.utils.StringUtils;

/**
 * @author Ezward
 * 
 * Singleton class to parse, evaluate and pretty-print simple 4-operator expressions
 * that use the following PEG grammar;
 *
 * digit ::= [0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9]
 * sign ::= '-'
 * integer ::= {sign} [digit]*
 * decimal ::= {sign} [digit]* '.' [digit]*
 * scientific ::= {sign} [digit]* {'.' [digit]*} ['e' | 'E'] {sign} [digit]*
 * number ::= [integer | decimal | scientific]
 * parenthesis ::= {sign} '(' expression ')'
 * value ::= [parenthesis | number ]
 * power ::= value{'^'value}
 * product ::= power {[ '*' | '/' ] power}*
 * sum ::= product  {['+' | '-'] product}*
 * expression ::= sum
 * 
 * Key to PEG notation:
 * {} = optional, choose zero or one
 * {}* = optional, 0 or more
 * [] = required, choose one
 * []* = required, 1 or more
 * 
 * Usage:
 * 		ExpressionEvaluator.Expression theExpression = ExpressionEvaluator.parse("(((10 + 5) x -6) - -20 / -2 x 3 + -100 - 50)");
 *		if(null == theExpression) throw new RuntimeException("Parse error");
 *		double theValue = theExpression.evaluate();
 *		String thePrettyPrint = theExpression.format();
 * 
 * DONE: support stack-based evaluation (rather than recursive evaluation)
 * DONE: support decimal numbers.
 * DONE: support exponential operator;
 * TODO: support variables and a context for evaluation; Expression.evaluate(ExpressionContext theContext);
 * TODO: support functions
 */
@Immutable
public final class ExpressionParser
{
	/**
	 * Exception thrown by parse().
	 */
	public static class ParseException extends RuntimeException
	{
		public final int index;

		public ParseException(final String theMessage, final int theIndex)
		{
			super(theMessage.replace("$index", String.valueOf(theIndex)));
			this.index = theIndex;
		}
	}

	/**
	 * @author Ezward
	 * 
	 * Generic expression that can be evaluated and formatted.
	 *
	 */
	public interface Expression extends EvaluationStep
	{
		/**
		 * Evaluate the expression and return the result.
		 *
		 * @return the value
		 */
		double evaluate();

		/**
		 * Format the expression into a human readable string.
		 *
		 * @return human readable string
		 */
		String format();

		/**
		 * Format the expression into a human readable string
		 * using a StringBuilder.
		 *
		 * @param theBuilder
		 */
		void format(StringBuilder theBuilder);

		/**
		 * Format the expression into a fully parenthesized
		 * form.
		 *
		 * @return fully parenthesized form of expression.
		 */
		String formatFullParenthesis();

		/**
		 * Format the expression into a fully parenthesized
		 * form using a StringBuilder.
		 *
		 * @param theBuilder
		 */
		void formatFullParenthesis(StringBuilder theBuilder);

		/**
		 * @return the offset of the start of the expression
		 *         in the original input stream.
		 */
		int startIndex();

		/**
		 *
		 * @return the offset of the end of the expression
		 *         in the original input stream.
		 */
		int endIndex();

		/**
		 * Execute this expression as one step in the
		 * evaluation of a context.
		 *
		 * @param theContext
		 */
		void step(final EvaluationContext theContext);
	}
	
	/**
	 * @author Ezward
	 *
	 * Expression for a literal number.
	 */
	public interface NumberExpression extends Expression
	{
		/**
		 * @return the number as parsed from the input.
		 */
		String number();

		/**
		 * @return true if the number is an integer, otherwise it is a double.
		 */
		boolean isInteger();
	}
	
	/**
	 * @author Ezward
	 *
	 * Expression for a parenthesized expression with an optional negation.
	 */
	public interface ParenthesisExpression extends Expression
	{
		/**
		 * If false, there is a negation operator before the opening parenthesis,
		 * otherwise there is no operator before the parenthesis.
		 * 
		 * @return true if positive, false if negated.
		 */
		boolean sign();
		
		/**
		 * @return the Expression between the parenthesis.
		 */
		ExpressionNode innerExpression();

	}
	
	/**
	 * @author Ezward
	 * 
	 * An operator and the Expression on the right side of an operator.
	 * This if this is part of a series of operations, then next() is not null.
	 *
	 */
	public interface RightExpression extends Expression
	{
		String operator();
		Expression expression();
		RightExpression next();
	}
	
	/**
	 * @author Ezward
	 *
	 * A series of binary operators.  In practice, the operators will either be a series of 
	 * additions and subtractions or a series of multiplications and divisions, that is,
	 * terms and factors are not mixed.
	 */
	public interface ChainedExpression extends Expression
	{
		Expression left();
		RightExpression right();
	}
	
	/**
	 * @author Ezward
	 *
	 * A series of chained multiplications and/or divisions.
	 * 
	 */
	public interface MultiplicationExpression extends ChainedExpression
	{
	}
	
	/**
	 * @author Ezward
	 *
	 * A series of chained additions and/or subtractions.
	 * 
	 */
	public interface AdditionExpression extends ChainedExpression
	{
	}

	/**
	 * @author Ezward
	 *
	 * An exponentiation; value ^ value.
	 *
	 */
	public interface PowerExpression extends Expression
	{
		Expression base();
		Expression exponent();
	}

	private ExpressionParser() {}	// private class to enforce singleton

	/**
	 * Empty expression
	 */
	public static final Expression nil = new Expression()
	{
		@Override public double evaluate() { return Double.NaN; }
		@Override public String format() { return ""; }
		@Override public void format(StringBuilder theBuilder) { return; }
		@Override public String formatFullParenthesis() { return ""; }
		@Override public void formatFullParenthesis(StringBuilder theBuilder) { return; }
		@Override public int startIndex() { return 0; }
		@Override public int endIndex() { return 0; }
		@Override public void step(EvaluationContext theContext) { return; }
	};


	/**
	 * Parse the input string and generate a parse tree from it.
	 *
	 * The input is parsed completely; extra non-whitespace characters
	 * that appear after a valid expression are treated as an error.
	 *
	 * @param theInput the buffer to parse
	 * @return an Expression tree.  This is ExpressionParser.nil if
	 *         the input is empty or all whitespace.
	 * @throws ParseException if parsing fails due to syntax error.
	 * @throws NullPointerException if theInput is null.
	 *
	 */
	public static Expression parse(final String theInput) throws ParseException
	{
		//
		// otherwise there must be a valid expression
		//
		final Expression theExpression = innerParse(theInput, 0);
		if(StringUtils.scanWhitespace(theInput, theExpression.endIndex()) < theInput.length())
		{
			// extra characters at end of input
			throw new ParseException("Unexpected characters at index $index", theExpression.endIndex());
		}
		return theExpression;
	}

	/**
	 * Parse an expression starting at the given index, until a complete
	 * expression is parsed. This ignores any characters after the
	 * expression. The last character parsed can be retrieved from results'
	 * Expression.endIndex() method.
	 *
	 * @param theInput the buffer to parse
	 * @param theIndex the character offset that parsing should start at
	 * @return an Expression tree.  This is ExpressionParser.nil if
	 *         theIndex is passed the end of theInput or
	 *         theInput is empty or theInput is all whitespace.
	 * @throws ParseException if parsing fails due to syntax error.
	 * @throws NullPointerException if theInput is null.
	 * @throws IndexOutOfBoundsException if theIndex is negative.
	 */
	public static Expression parse(final String theInput, final int theIndex) throws ParseException
	{
		return innerParse(theInput, theIndex);
	}

	//
	// *********** the private methods for parsing sub-Expressions from the input ***************
	//

	/**
	 * Parse an expression starting at the given index, until a complete
	 * expression is parsed. This ignores any characters after the
	 * expression. The last character parsed can be retrieved from results'
	 * Expression.endIndex() method.
	 *
	 * @param theInput the buffer to parse
	 * @param theIndex the character offset that parsing should start at
	 * @return an Expression tree.  This is ExpressionParser.nil if
	 *         theIndex is passed the end of theInput or
	 *         theInput is empty or theInput is all whitespace.
	 * @throws ParseException if parsing fails due to syntax error.
	 * @throws NullPointerException if theInput is null.
	 * @throws IndexOutOfBoundsException if theIndex is negative.
	 */
	private static Expression innerParse(final String theInput, final int theIndex )
	{
		if(null == theInput)
		{
			throw new NullPointerException();
		}

		if(theIndex < 0)
		{
			throw new IndexOutOfBoundsException();
		}

		//
		// if the input is empty, return the empty Expression
		//
		final int theStartIndex = StringUtils.scanWhitespace(theInput, theIndex);
		if(theStartIndex >= theInput.length())
		{
			return nil;
		}

		//
		// otherwise there must be a valid expression
		//
		return parseSum(theInput, theStartIndex);
	}

	private static ExpressionNode parseValue(final String theInput, final int theIndex)
	{
		final int theLength = theInput.length();
		final int theStartIndex = StringUtils.scanWhitespace(theInput, theIndex);		
		if(theStartIndex >= theLength) throw new ParseException("Unexpected end of input at %index. Expected sign, parenthesis or number.", theStartIndex);	// unexpected end of input: expected sign, parenthesis or number.
		
		//
		// parse the optional negation
		//
		int theExpressionIndex = theStartIndex;
		boolean theSign = true;
		if('-' == theInput.charAt(theStartIndex))
		{
			theSign = false;
			theExpressionIndex = theStartIndex + 1;	// skip the sign
		}
		
		if(theExpressionIndex >= theLength) throw new ParseException("Unexpected end of input at $index. Expected a number or opening parenthesis", theExpressionIndex);	// unexpected end of input: expecting number or parenthesis
		final char theChar = theInput.charAt(theExpressionIndex);
		if('(' == theChar)
		{
			final ExpressionNode theAdditionExpression = parseSum(theInput, StringUtils.scanWhitespace(theInput, theExpressionIndex + 1));
			final int theEndIndex = StringUtils.scanWhitespace(theInput, theAdditionExpression.endIndex());
			if(theEndIndex >= theLength)
			{
				throw new ParseException("Unexpected end of input at $index while parsing a value.", theEndIndex);
			}

			if(')' != theInput.charAt(theEndIndex)) throw new ParseException("Unexpected character at $index.  Expected closing parenthesis.", theEndIndex);

			return new ParenthesisNode(theStartIndex, theEndIndex + 1, theSign, theAdditionExpression);
		}
		else if(StringUtils.isDigit(theChar))
		{
			// include the sign with the digits (rather than a separate negation operation)
			return parseNumber(theInput, theStartIndex);
		}
			
		throw new ParseException("Unexpected character at $index.  Expected parenthesis or number.", theExpressionIndex);	// Expected parenthesis or number.
	}

	private static ExpressionNode parseNumber(final String theInput, int theIndex)
	{
		final int theLength = theInput.length();
		final int theStartIndex = StringUtils.scanWhitespace(theInput, theIndex);
		if(theStartIndex >= theLength) throw new ParseException("Unexpected end of input at $index. Expected a sign, opening parenthesis or a number.", theStartIndex);	// unexpected end of input: expected sign, parenthesis or number.

		//
		// parse the optional negation
		//
		int theExpressionIndex = theStartIndex;
		boolean theSign = true;
		if('-' == theInput.charAt(theStartIndex))
		{
			theSign = false;
			theExpressionIndex = theStartIndex + 1;	// skip the sign
		}

		if(theExpressionIndex >= theLength) throw new ParseException("Unexpected end of input at $index.  Expected a digit.", theExpressionIndex);
		if(!StringUtils.isDigit(theInput.charAt(theExpressionIndex))) throw new ParseException("Unexpected character at $index. Expected a digit.", theExpressionIndex);

		//
		// scan the required integer part
		//
		int theEndIndex = StringUtils.scanNumeric(theInput, theExpressionIndex);

		// include the sign with the digits (rather than a separate negation operation)
		final String theWholePart = theInput.substring(theStartIndex, theEndIndex);

		//
		// scan optional decimal part
		//
		String theDecimalPart = "";
		if((theEndIndex < theLength) && ('.' == theInput.charAt(theEndIndex)))
		{
			final int theDecimalIndex = theEndIndex;
			theEndIndex += 1;	// count the decimal

			// scan the decimal part
			if(theEndIndex >= theLength) throw new ParseException("Unexpected end of input at $index. Expected a digit after the decimal point.", theEndIndex);
			if(!StringUtils.isDigit(theInput.charAt(theEndIndex))) throw new ParseException("Unexpected character at $index. Expected a digit after the decimal point.", theEndIndex);

			theEndIndex = StringUtils.scanNumeric(theInput, theEndIndex);
			theDecimalPart = theInput.substring(theDecimalIndex, theEndIndex);
		}

		//
		// scan optional exponent
		//
		String theExponentPart = "";
		if((theEndIndex < theLength) && ('e' == theInput.charAt(theEndIndex)))
		{
			final int theExponentIndex = theEndIndex;
			theEndIndex += 1;	// count the exponent char

			// scan the exponent part
			if(theEndIndex >= theLength) throw new ParseException("Unexpected end of input at $index. Expected a digit following the exponent character.", theEndIndex);
			if(!StringUtils.isDigit(theInput.charAt(theEndIndex))) throw new ParseException("Unexpected character at $index. Expected a digit following the exponent character.", theEndIndex);

			theEndIndex = StringUtils.scanNumeric(theInput, theEndIndex);
			theExponentPart = theInput.substring(theExponentIndex, theEndIndex);
		}

		//
		// if there is no decimal part and no exponent, it is an integer
		//
		final boolean isInteger = (theDecimalPart.isEmpty() && theExponentPart.isEmpty());

		return new NumberNode(theStartIndex, theEndIndex, theInput.substring(theStartIndex, theEndIndex), isInteger);
	}

	private static ExpressionNode parsePower(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parseValue(theInput, theIndex);

		final int theLength = theInput.length();
		int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
		if(theOperatorIndex < theLength)
		{
			char theOperator = theInput.charAt(theOperatorIndex);
			if('^' == theOperator)
			{
				// parse the right side
				final ExpressionNode theRightExpression = parseValue(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));

				final PowerNode thePowerExpression = new PowerNode(theLeftExpression, theRightExpression);
				return thePowerExpression;
			}
		}

		//
		// only left side
		//
		return theLeftExpression;
	}


	private static ExpressionNode parseProduct(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parsePower(theInput, theIndex);

		final int theLength = theInput.length();
		int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
		if(theOperatorIndex < theLength)
		{
			char theOperator = theInput.charAt(theOperatorIndex);
			if(('*' == theOperator) || ('/' == theOperator))
			{
				//
				// collect sequential multiplications
				//
				RightExpressionNode theRightExpressions = null;
				while(theOperatorIndex < theLength)
				{
					theOperator = theInput.charAt(theOperatorIndex);
					if(false == (('*' == theOperator) || ('x' == theOperator) || ('/' == theOperator))) break;

					// parse the right side
					final ExpressionNode theRightExpression = parsePower(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));

					// append to list of multiplications
					final RightExpressionNode theRightExpressionNode = new RightExpressionNode(theOperatorIndex, String.valueOf(theOperator), theRightExpression);
					theRightExpressions = (null == theRightExpressions) ? theRightExpressionNode : theRightExpressions.append(theRightExpressionNode);

					// skip trailing whitespace
					theOperatorIndex = StringUtils.scanWhitespace(theInput, theRightExpression.endIndex());
				}

				final MultiplicationNode theMultiplicationExpression = new MultiplicationNode(theLeftExpression, theRightExpressions);
				return theMultiplicationExpression;
			}
		}

		//
		// only left side
		//
		return theLeftExpression;
	}

	private static ExpressionNode parseSum(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parseProduct(theInput, theIndex);

		final int theLength = theInput.length();
		int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
		if(theOperatorIndex < theLength)
		{
			char theOperator = theInput.charAt(theOperatorIndex);
			if(('+' == theOperator) || ('-' == theOperator))
			{
				//
				// parse sequential additions
				//
				RightExpressionNode theRightExpressions = null;
				while(theOperatorIndex < theLength)
				{
					theOperator = theInput.charAt(theOperatorIndex);
					if(false == (('+' == theOperator) || ('-' == theOperator))) break;

					// parse the right side
					final ExpressionNode theRightExpression = parseProduct(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));

					// append to list of additions
					final RightExpressionNode theRightExpressionNode = new RightExpressionNode(theOperatorIndex, String.valueOf(theOperator), theRightExpression);
					theRightExpressions = (null == theRightExpressions) ? theRightExpressionNode : theRightExpressions.append(theRightExpressionNode);

					// skip to past the trailing whitespace
					theOperatorIndex = StringUtils.scanWhitespace(theInput, theRightExpression.endIndex());
				}

				final AdditionNode theAdditionExpression = new AdditionNode(theLeftExpression, theRightExpressions);
				return theAdditionExpression;
			}
		}

		//
		// only left side
		//
		return theLeftExpression;
	}


	//
	//********** the private implementation classes for Expression and derivatives **************
	//
	
	/**
	 * @author Ezward
	 * 
	 * Generic immutable parse tree node for expressions.
	 *
	 */
	@Immutable
	private static abstract class ExpressionNode implements Expression
	{
		private final int thisStart;
		private final int thisEnd;
		
		public ExpressionNode(final int theStartIndex, final int theEndIndex)
		{
			if(theStartIndex < 0) throw new RuntimeException();
			if(theStartIndex > theEndIndex) throw new RuntimeException();
			
			thisStart = theStartIndex;
			thisEnd = theEndIndex;
		}
		
		@Override
		public final int startIndex() { return thisStart; }
		
		@Override
		public final int endIndex() { return thisEnd; }

		@Override
		public abstract double evaluate();

		@Override
		public abstract void format(StringBuilder theBuilder);
		
		@Override
		public String format()
		{
			final StringBuilder theBuilder = new StringBuilder();
			this.format(theBuilder);
			return theBuilder.toString();
		}
		
		@Override
		public abstract void formatFullParenthesis(StringBuilder theBuilder);
		
		@Override
		public String formatFullParenthesis()
		{
			final StringBuilder theBuilder = new StringBuilder();
			this.formatFullParenthesis(theBuilder);
			return theBuilder.toString();
		}
	}

	@Immutable
	private static final class AdditionNode extends ExpressionNode implements AdditionExpression
	{
		private final ExpressionNode thisLeft;
		private final RightExpression thisRight;
		
		public AdditionNode(ExpressionNode theLeft, final RightExpressionNode theRight)
		{
			super(theLeft.startIndex(), theRight.last().endIndex());

			thisLeft = theLeft;
			thisRight = theRight;		// defensive copy.
		}
		
		@Override
		public Expression left() { return thisLeft; }

		/**
		 * Get the list of consecutive additions and subtractions
		 *
		 * @return immutable list of consecutive additions and subtractions
		 */
		@Override
		public RightExpression right() { return thisRight; }
		

		@Override
		public void format(StringBuilder theBuilder)
		{
			thisLeft.format(theBuilder);
			for(RightExpression theRight = thisRight; null != theRight; theRight = theRight.next())
			{
				theRight.format(theBuilder);
			}
		}
		
		@Override
		public void formatFullParenthesis(StringBuilder theBuilder)
		{
			theBuilder.append('(');
			thisLeft.formatFullParenthesis(theBuilder);
			for(RightExpression theRight = thisRight; null != theRight; theRight = theRight.next())
			{
				theRight.formatFullParenthesis(theBuilder);
			}
			theBuilder.append(')');
		}
		
		@Override
		public double evaluate()
		{
			//
			// evaluate multiplication/division expressions from left to right
			//
			double theLeftValue = thisLeft.evaluate();
			for(RightExpression theRight = thisRight; null != theRight; theRight = theRight.next())
			{
				if("+".equals(theRight.operator()))
				{
					theLeftValue += theRight.evaluate();
				}
				else if("-".equals(theRight.operator()))
				{
					theLeftValue -= theRight.evaluate();
				}
				else
				{
					throw new IllegalStateException("Unexpected operator ($operator) encountered while evaluating an AdditionNode.".replace("$operator", String.valueOf(theRight.operator())));
				}
			}
			return theLeftValue;
		}


		/**
		 * Execute one step in the interpretation of this expression
		 * using the given evaluation context.
		 *
		 * @param theContext
		 */
		@Override
		public void step(final EvaluationContext theContext)
		{
			//
			// push the right, then push the left
			//
			theContext.pushEvaluationStep(thisRight);
			theContext.pushEvaluationStep(thisLeft);
		}

	}

	@Immutable
	private static final class RightExpressionNode extends ExpressionNode implements RightExpression
	{
		private final String thisOperator;
		private final ExpressionNode thisExpression;
		private final RightExpressionNode thisNext;

		public RightExpressionNode(final int theStartIndex, final String theOperator, final ExpressionNode theRight, final RightExpressionNode theNext)
		{
			super(theStartIndex, theRight.endIndex());

			if(null == theRight) throw new RuntimeException();

			thisOperator = theOperator;
			thisExpression = theRight;
			thisNext = theNext;
		}

		public RightExpressionNode(final int theStartIndex, final String theOperator, final ExpressionNode theRight)
		{
			this(theStartIndex, theOperator, theRight, null);
		}

		@Override
		public String operator() { return thisOperator; }
		
		@Override
		public ExpressionNode expression() { return thisExpression; }

		@Override
		public RightExpression next() { return thisNext; }

		@Override
		public double evaluate() { return thisExpression.evaluate(); }

		@Override
		public String format()
		{
			final StringBuilder theBuilder = new StringBuilder();
			this.format(theBuilder);
			return theBuilder.toString();
		}

		@Override
		public void format(StringBuilder theBuilder)
		{
			theBuilder.append(' ').append(thisOperator).append(' ');
			thisExpression.format(theBuilder);
		}
		
		@Override
		public String formatFullParenthesis()
		{
			final StringBuilder theBuilder = new StringBuilder();
			this.formatFullParenthesis(theBuilder);
			return theBuilder.toString();
		}

		@Override
		public void formatFullParenthesis(StringBuilder theBuilder)
		{
			theBuilder.append(' ').append(thisOperator).append(' ');
			thisExpression.formatFullParenthesis(theBuilder);
		}

		/**
		 * Execute one step in the interpretation of this expression
		 * using the given evaluation context.
		 *
		 * @param theContext
		 */
		@Override
		public void step(final EvaluationContext theContext)
		{
			//
			// push the operator, push the operand, then push the next
			//
			if(null != thisNext) theContext.pushEvaluationStep(thisNext);
			theContext.pushEvaluationStep(EvaluationOperation.getBinaryOperation(String.valueOf(thisOperator)));
			theContext.pushEvaluationStep(thisExpression);
		}


		/**
		 * Last RightExpression in the list.
		 *
		 * Note: This walks the list to find the last node,
		 *       so this can be an expensive operation.
		 *
		 * @return the last non-Nil node in the list or Nil if list is empty
		 */
		public RightExpressionNode last()
		{
			RightExpressionNode theNode = this;
			while(null != theNode.thisNext)
			{
				theNode = theNode.thisNext;
			}
			return theNode;
		}

		/**
		 * Append another list or right expression to this right expression.
		 *
		 * Note: since our expressions are immutable data structures, this
		 *       returns a newly constructed list.  It does NOT update
		 *       the list in place.
		 *
		 * @param list to append to this
		 * @return a new list with element as the tail or this list if element is null.
		 */
		public RightExpressionNode append(final RightExpressionNode list)
		{
			if (null == list) return this;
			if (null == thisNext) return new RightExpressionNode(startIndex(), thisOperator, thisExpression, list);   // optimization to avoid an extra recursive call
			return new RightExpressionNode(startIndex(), thisOperator, thisExpression, thisNext.append(list));
		}
	}

	/**
	 * expression node for a chain of multiplication/division operations
	 */
	@Immutable
	private static final class MultiplicationNode extends ExpressionNode implements MultiplicationExpression
	{
		private final ExpressionNode thisLeft;
		private final RightExpression thisRight;

		public MultiplicationNode(ExpressionNode theLeft, final RightExpressionNode theRight)
		{
			super(theLeft.startIndex(), theRight.last().endIndex());

			thisLeft = theLeft;
			thisRight = theRight;
		}
		
		@Override
		public Expression left() { return thisLeft; }

		/**
		 * Get the list of consecutive multiplications and divisions
		 *
		 * @return immutable list of consecutive multiplications and divisions
		 */
		@Override
		public RightExpression right() { return thisRight; }

		@Override
		public void format(StringBuilder theBuilder)
		{
			thisLeft.format(theBuilder);
			for(RightExpression theRight = thisRight; null != theRight; theRight = theRight.next())
			{
				theRight.format(theBuilder);
			}
		}
		
		@Override
		public void formatFullParenthesis(StringBuilder theBuilder)
		{
			theBuilder.append('(');
			thisLeft.formatFullParenthesis(theBuilder);
			for(RightExpression theRight = thisRight; null != theRight; theRight = theRight.next())
			{
				theRight.formatFullParenthesis(theBuilder);
			}
			theBuilder.append(')');
		}

		@Override
		public double evaluate()
		{
			//
			// evaluate multiplication/division expressions from left to right
			//
			double theLeftValue = thisLeft.evaluate();
			for(RightExpression theRight = thisRight; null != theRight; theRight = theRight.next())
			{
				if("/".equals(theRight.operator()))
				{
					theLeftValue /= theRight.evaluate();
				}
				else if("*".equals(theRight.operator()))
				{
					theLeftValue *= theRight.evaluate();
				}
				else
				{
					throw new IllegalStateException("Unexpected operator ($operator) encountered while evaluating a MultiplicationNode.".replace("$operator", String.valueOf(theRight.operator())));
				}
			}
			return theLeftValue;
		}

		/**
		 * Execute one step in the interpretation of this expression
		 * using the given evaluation context.
		 *
		 * @param theContext
		 */
		@Override
		public void step(final EvaluationContext theContext)
		{
			//
			// push the right operand, then the left
			// so that the left operand is at the top of
			// the stack and so is evaluated first.
			//
			theContext.pushEvaluationStep(thisRight);
			theContext.pushEvaluationStep(thisLeft);
		}
	}

	/**
	 * Immutable expression node for a parenthesized expression.
	 */
	@Immutable
	private static final class ParenthesisNode extends ExpressionNode implements ParenthesisExpression
	{
		private final ExpressionNode thisInnerExpression;
		private final boolean thisSign;
		
		public ParenthesisNode(final int theStartIndex, final int theEndIndex, final boolean theSign, final ExpressionNode theInnerExpression)
		{
			super(theStartIndex, theEndIndex);
			
			if(null == theInnerExpression) throw new RuntimeException();
			
			thisSign = theSign;
			thisInnerExpression = theInnerExpression;
		}
		
		@Override
		public boolean sign() { return thisSign; }
		
		@Override
		public ExpressionNode innerExpression() { return thisInnerExpression; }
		
		@Override
		public void format(StringBuilder theBuilder)
		{
			if(!thisSign)
			{
				// unary negation
				theBuilder.append('-');
			}
			theBuilder.append('(');
			thisInnerExpression.format(theBuilder);
			theBuilder.append(')');
		}
		
		@Override
		public void formatFullParenthesis(StringBuilder theBuilder)
		{
			if(!thisSign)
			{
				// unary negation
				theBuilder.append('(');
				theBuilder.append('-');
			}
			theBuilder.append('(');
			thisInnerExpression.formatFullParenthesis(theBuilder);
			theBuilder.append(')');
			if(!thisSign)
			{
				theBuilder.append(')');
			}
		}
		
		@Override
		public double evaluate()
		{
			final double theValue = thisInnerExpression.evaluate();
			return thisSign ? theValue : -theValue;
		}

		/**
		 * Execute one step in the interpretation of this expression
		 * using the given evaluation context.
		 *
		 * @param theContext
		 */
		@Override
		public void step(final EvaluationContext theContext)
		{
			//
			// push the right operand, then the left
			// so that the left operand is at the top of
			// the stack and so is evaluated first.
			//
			if(!thisSign) theContext.pushEvaluationStep(EvaluationOperation.Negation);
			theContext.pushEvaluationStep(thisInnerExpression);
		}
	}

	/**
	 * Immutable expression node for a number.
	 */
	@Immutable
	private static final class PowerNode extends ExpressionNode implements PowerExpression
	{
		private final ExpressionNode thisBase;
		private final ExpressionNode thisExponent;

		public PowerNode(final ExpressionNode theBase, final ExpressionNode theExponent)
		{
			super(theBase.startIndex(), theExponent.endIndex());

			thisBase = theBase;
			thisExponent = theExponent;
		}

		@Override
		public ExpressionNode base() { return thisBase; }

		@Override
		public ExpressionNode exponent() { return thisExponent; }

		@Override
		public void format(StringBuilder theBuilder)
		{
			thisBase.format(theBuilder);
			theBuilder.append('^');
			thisExponent.format(theBuilder);
		}

		@Override
		public void formatFullParenthesis(StringBuilder theBuilder)
		{
			theBuilder.append('(');
			format(theBuilder);
			theBuilder.append(')');
		}

		@Override
		public double evaluate()
		{
			final Double theBase = thisBase.evaluate();
			final Double theExponent = thisExponent.evaluate();
			return Math.pow(theBase, theExponent);
		}

		/**
		 * Execute one step in the interpretation of this expression
		 * Using the given context.
		 *
		 * @param theContext
		 */
		@Override
		public void step(final EvaluationContext theContext)
		{
			//
			// push the operation and the arguments onto the stack.
			//
			theContext.pushEvaluationStep(EvaluationOperation.Exponentiation);
			theContext.pushEvaluationStep(thisExponent);
			theContext.pushEvaluationStep(thisBase);
		}
	}

	/**
	 * Immutable expression node for a number.
	 */
	@Immutable
	private static final class NumberNode extends ExpressionNode implements NumberExpression
	{
		private final String thisNumber;
		private final Double thisValue;
		private final boolean thisIsInteger;
		
		public NumberNode(final int theStartIndex, final int theEndIndex, final String theNumber, final boolean isInteger)
		{
			super(theStartIndex, theEndIndex);
			
			if((null == theNumber) || theNumber.isEmpty()) throw new RuntimeException();
			
			thisNumber = theNumber;
			thisIsInteger = isInteger;
			thisValue = Double.valueOf(theNumber);
		}
		
		@Override
		public String number() { return thisNumber; }

		public boolean isInteger() { return thisIsInteger; }
		
		@Override
		public void format(StringBuilder theBuilder)
		{
			theBuilder.append(thisNumber);
		}

		@Override
		public void formatFullParenthesis(StringBuilder theBuilder)
		{
			format(theBuilder);
		}

		@Override
		public double evaluate()
		{
			return thisValue;
		}

		/**
		 * Execute one step in the interpretation of this expression
		 * Using the given context.
		 *
		 * @param theContext
		 */
		@Override
		public void step(final EvaluationContext theContext)
		{
			//
			// push the number onto the value stack.
			//
			theContext.pushValue(thisValue);
		}
	}
}
