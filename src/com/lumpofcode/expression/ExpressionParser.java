package com.lumpofcode.expression;

import com.lumpofcode.utils.StringUtils;
import jdk.nashorn.internal.ir.annotations.Immutable;

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
 * product ::= value {[ '*' | '/' ] value}*
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
 * TODO: support exponential and root operators;
 * TODO: support variables and a context for evaluation; Expression.evaluate(ExpressionContext theContext);
 * TODO: support functions
 */
@Immutable
public final class ExpressionParser
{
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
		char operator();
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
	
	private ExpressionParser() {}	// private class to enforce singleton
	
	/**
	 * Parse the input string and generate a parse tree from it.
	 * 
	 * @param theInput
	 * @return an Expression tree
	 * @throws RuntimeException if parsing fails.
	 * 
	 */
	public static Expression parse(final String theInput)
	{
		if((null != theInput) && (theInput.length() > 0))
		{
			final int theLength = theInput.length();
			if(theLength > 0)
			{
				final ExpressionNode theExpression = parseSum(theInput, 0);
				
				if(null != theExpression)
				{
					if(StringUtils.scanWhitespace(theInput, theExpression.endIndex()) < theLength)
					{
						// extra characters at end of input
						throw new RuntimeException("Unexpected characters at index $i".replace("$i", String.valueOf(theExpression.endIndex())));
					}
					return theExpression;
				}
			}
		}
		
		return null;
	}

	//
	// *********** the private methods for parsing sub-Expressions from the input ***************
	//
	
	private static ExpressionNode parseValue(final String theInput, final int theIndex)
	{
		final int theLength = theInput.length();
		final int theStartIndex = StringUtils.scanWhitespace(theInput, theIndex);		
		if(theStartIndex >= theLength) return null;	// unexpected end of input: expected sign, parenthesis or number.
		
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
		
		if(theExpressionIndex >= theLength) return null;	// unexpected end of input: expecting number or parenthesis
		final char theChar = theInput.charAt(theExpressionIndex);
		if('(' == theChar)
		{
			final ExpressionNode theAdditionExpression = parseSum(theInput, StringUtils.scanWhitespace(theInput, theExpressionIndex + 1));
			if(null != theAdditionExpression)
			{
				final int theEndIndex = StringUtils.scanWhitespace(theInput, theAdditionExpression.endIndex());
				if(theEndIndex >= theLength)
				{
					throw new RuntimeException("Unexpected end of input.");
				}
				
				if(')' != theInput.charAt(theEndIndex)) throw new RuntimeException("Expected closing parenthesis.");
				
				return new ParenthesisNode(theStartIndex, theEndIndex + 1, theSign, theAdditionExpression);
			}

		}
		else if(StringUtils.isDigit(theChar))
		{
			// include the sign with the digits (rather than a separate negation operation)
			return parseNumber(theInput, theStartIndex);
		}
			
		throw new RuntimeException();	// Expected parenthesis or number.
	}

	private static ExpressionNode parseNumber(final String theInput, int theIndex)
	{
		final int theLength = theInput.length();
		final int theStartIndex = StringUtils.scanWhitespace(theInput, theIndex);
		if(theStartIndex >= theLength) return null;	// unexpected end of input: expected sign, parenthesis or number.

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

		if(theExpressionIndex >= theLength) throw new RuntimeException("Unexpected end of input; expected a digit.");
		if(!StringUtils.isDigit(theInput.charAt(theExpressionIndex))) throw new RuntimeException("Unexpected character; expected a digit.");

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
			if(theEndIndex >= theLength) throw new RuntimeException("Unexpected end of input; expected digit after decimal point.");
			if(!StringUtils.isDigit(theInput.charAt(theEndIndex))) throw new RuntimeException("Unexpected character; expected digit after decimal point.");

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
			if(theEndIndex >= theLength) throw new RuntimeException("Unexpected end of input, expected digit after exponent character.");
			if(!StringUtils.isDigit(theInput.charAt(theEndIndex))) throw new RuntimeException("Unexpected character; expected digit after exponent character.");

			theEndIndex = StringUtils.scanNumeric(theInput, theEndIndex);
			theExponentPart = theInput.substring(theExponentIndex, theEndIndex);
		}

		//
		// if there is no decimal part and no exponent, it is an integer
		//
		final boolean isInteger = (theDecimalPart.isEmpty() && theExponentPart.isEmpty());

		return new NumberNode(theStartIndex, theEndIndex, theInput.substring(theStartIndex, theEndIndex), isInteger);
	}

	private static ExpressionNode parseProduct(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parseValue(theInput, theIndex);
		if(null != theLeftExpression)
		{
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
						final ExpressionNode theRightExpression = parseValue(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));
						if(null == theRightExpression) throw new RuntimeException();	// error; we found operator, but no right side.  We should throw.
						
						// append to list of multiplications
						final RightExpressionNode theRightExpressionNode = new RightExpressionNode(theOperatorIndex, theOperator, theRightExpression);
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
		
		return null;

	}

	private static ExpressionNode parseSum(final String theInput, int theIndex)
	{
		final int theLength = theInput.length();
		final int theStartIndex = StringUtils.scanWhitespace(theInput, theIndex);
		if(theStartIndex < theLength)
		{
			final ExpressionNode theLeftExpression = parseProduct(theInput, theStartIndex);
			if(null != theLeftExpression)
			{
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
							if(null == theRightExpression) throw new RuntimeException();	// TODO: create ParseException(theInput, theStartIndex, theEndIndex, theMessage);
							
							// append to list of additions
							final RightExpressionNode theRightExpressionNode = new RightExpressionNode(theOperatorIndex, theOperator, theRightExpression);
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
		}
		
		return null;

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
				if('+' == theRight.operator())
				{
					theLeftValue += theRight.evaluate();
				}
				else if('-' == theRight.operator())
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
		private final char thisOperator;
		private final ExpressionNode thisExpression;
		private final RightExpressionNode thisNext;

		public RightExpressionNode(final int theStartIndex, final char theOperator, final ExpressionNode theRight, final RightExpressionNode theNext)
		{
			super(theStartIndex, theRight.endIndex());

			if(null == theRight) throw new RuntimeException();

			thisOperator = theOperator;
			thisExpression = theRight;
			thisNext = theNext;
		}

		public RightExpressionNode(final int theStartIndex, final char theOperator, final ExpressionNode theRight)
		{
			this(theStartIndex, theOperator, theRight, null);
		}

		@Override
		public char operator() { return thisOperator; }
		
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
				if('/' == theRight.operator())
				{
					theLeftValue /= theRight.evaluate();
				}
				else if('*' == theRight.operator())
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
