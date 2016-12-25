package com.lumpofcode.expression.associative;

import com.lumpofcode.annotation.Immutable;
import com.lumpofcode.collection.list.LinkList;
import com.lumpofcode.collection.list.LinkLists;
import com.lumpofcode.utils.StringUtils;

/**
 * @author Ezward
 *
 * NOTE: this grammar separates out sums from differences and products from quotients.
 *       Thus, it is not a traditional factor/term grammar.  The grammar is
 *       designed to separate out operations that are subject to the associative
 *       and commutative properties with the notion that the parse tree can
 *       then be more easily queried or manipulated using those mathematical properties.
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
 * quotient ::= power {[ '*' | '/' ] power}*
 * product ::= quotient { '/'  quotient}
 * difference ::= product  {['+' | '-'] product}*
 * sum ::= difference {'+' difference}*
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
 */
@Immutable
public final class AssociativeExpressionEvaluator
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
	public interface Expression
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
		Expression innerExpression();
		
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
		/**
		 * get the operator that chains the operands.
		 *
		 * @return the operator that chains the operands
		 */
		String operator();

		/**
		 * get the chained operands.
		 *
		 * @return the chained operands
		 */
		LinkList<Expression> operands();

		/**
		 * Get the leftmost operand in the chained expression.
		 *
		 * @return the leftmost expression in the chain of operations
		 */
		Expression left();

		/**
		 * Get the list of operands to the right of the first operator
		 *
		 * @return immutable list of operands to the right of the first operator
		 */
		LinkList<Expression> right();

	}
	
	/**
	 * @author Ezward
	 *
	 * A series of chained divisions.
	 *
	 */
	public interface DivisionExpression extends ChainedExpression
	{
	}
	
	/**
	 * @author Ezward
	 *
	 * A series of chained multiplications.
	 *
	 */
	public interface MultiplicationExpression extends ChainedExpression
	{
	}

	/**
	 * @author Ezward
	 *
	 * A series of chained subtractions.
	 *
	 */
	public interface SubtractionExpression extends ChainedExpression
	{
	}
	
	/**
	 * @author Ezward
	 *
	 * A series of chained additions.
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
	
	private AssociativeExpressionEvaluator() {}	// private class to enforce singleton
	
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
	};
	
	
	/**
	 * Parse the input string and generate a parse tree from it.
	 *
	 * The input is parsed completely; extra non-whitespace characters
	 * that appear after a valid expression are treated as an error.
	 *
	 * @param theInput the buffer to parse
	 * @return an Expression tree.  This is ExpressionEvaluator.nil if
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
	 * @return an Expression tree.  This is ExpressionEvaluator.nil if
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
	 * @return an Expression tree.  This is ExpressionEvaluator.nil if
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
		if(theStartIndex >= theLength) throw new ParseException("Unexpected end of input at $index. Expected sign, parenthesis or number.", theStartIndex);	// unexpected end of input: expected sign, parenthesis or number.
		
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

	private static final LinkList<Character> divisionOperators = LinkLists.linkList('/', '÷');

	private static ExpressionNode parseQuotient(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parsePower(theInput, theIndex);

		final int theLength = theInput.length();
		int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
		if((theOperatorIndex < theLength) && divisionOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
		{
			//
			// collect sequential divisions
			//
			LinkList<Expression> theOperands = new LinkList<>(theLeftExpression);
			while((theOperatorIndex < theLength) && divisionOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
			{
				// parse the right side
				final ExpressionNode theRightExpression = parsePower(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));

				// append to list of operations
				theOperands = theOperands.append(theRightExpression);

				// skip trailing whitespace
				theOperatorIndex = StringUtils.scanWhitespace(theInput, theRightExpression.endIndex());
			}

			final DivisionNode theExpression = new DivisionNode(theOperands);
			return theExpression;
		}

		//
		// only left side
		//
		return theLeftExpression;
	}

	private static final LinkList<Character> multiplicationOperators = LinkLists.linkList('*', '×');

	private static ExpressionNode parseProduct(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parseQuotient(theInput, theIndex);
		
		final int theLength = theInput.length();
		int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
		if((theOperatorIndex < theLength) && multiplicationOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
		{
			//
			// collect sequential multiplications
			//
			LinkList<Expression> theOperands = new LinkList<>(theLeftExpression);
			while((theOperatorIndex < theLength) && multiplicationOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
			{
				// parse the right side
				final ExpressionNode theRightExpression = parseQuotient(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));

				// append to list of multiplications
				theOperands = theOperands.append(theRightExpression);

				// skip trailing whitespace
				theOperatorIndex = StringUtils.scanWhitespace(theInput, theRightExpression.endIndex());
			}

			final MultiplicationNode theExpression = new MultiplicationNode(theOperands);
			return theExpression;
		}
		
		//
		// only left side
		//
		return theLeftExpression;
	}

	private static final LinkList<Character> subtractionOperators = LinkLists.linkList('-', '−');

	private static ExpressionNode parseDifference(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parseProduct(theInput, theIndex);
		
		final int theLength = theInput.length();
		int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
		if((theOperatorIndex < theLength) && subtractionOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
		{
			//
			// parse sequential additions
			//
			LinkList<Expression> theOperands = new LinkList(theLeftExpression);
			while((theOperatorIndex < theLength) && subtractionOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
			{
				// parse the right side
				final ExpressionNode theRightExpression = parseProduct(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));

				// append to list of additions
				theOperands = theOperands.append(theRightExpression);

				// skip to past the trailing whitespace
				theOperatorIndex = StringUtils.scanWhitespace(theInput, theRightExpression.endIndex());
			}

			final SubtractionNode theExpression = new SubtractionNode(theOperands);
			return theExpression;
		}

		//
		// only left side
		//
		return theLeftExpression;
	}

	private static final LinkList<Character> additionOperators = LinkLists.linkList('+', '+');

	private static ExpressionNode parseSum(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parseDifference(theInput, theIndex);

		final int theLength = theInput.length();
		int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
		if((theOperatorIndex < theLength) && additionOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
		{
			//
			// parse sequential additions
			//
			LinkList<Expression> theOperands = new LinkList(theLeftExpression);
			while((theOperatorIndex < theLength) && additionOperators.find(theInput.charAt(theOperatorIndex)).isNotEmpty())
			{
				// parse the right side
				final ExpressionNode theRightExpression = parseDifference(theInput, StringUtils.scanWhitespace(theInput, theOperatorIndex + 1));

				// append to list of additions
				theOperands = theOperands.append(theRightExpression);

				// skip to past the trailing whitespace
				theOperatorIndex = StringUtils.scanWhitespace(theInput, theRightExpression.endIndex());
			}

			final AdditionNode theExpression = new AdditionNode(theOperands);
			return theExpression;
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
	private static abstract class ExpressionNode implements AssociativeExpressionEvaluator.Expression
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
	private static abstract class ChainedExpressionNode extends ExpressionNode implements AssociativeExpressionEvaluator.ChainedExpression
	{
		private final LinkList<Expression> operands;

		public ChainedExpressionNode(LinkList<Expression> theOperands)
		{
			super(theOperands.head.startIndex(), theOperands.last().head.endIndex());

			if(LinkList.Nil == theOperands.tail)
			{
				throw new IllegalArgumentException("ChainedExpressionNode constructor requires at least two operands.");
			}

			this.operands = theOperands;
		}


		@Override
		public LinkList<Expression> operands() {
			return this.operands;
		}

		@Override
		public final Expression left() { return operands.head; }

		@Override
		public final LinkList<Expression> right() { return operands.tail; }


		@Override
		public final void format(StringBuilder theBuilder)
		{
			left().format(theBuilder);
			for(LinkList<? extends Expression> theRight = right(); !theRight.isEmpty(); theRight = theRight.tail)
			{
				theBuilder.append(' ').append(this.operator()).append(' ');
				theRight.head.format(theBuilder);
			}
		}

		@Override
		public void formatFullParenthesis(StringBuilder theBuilder)
		{
			theBuilder.append('(');
			left().formatFullParenthesis(theBuilder);
			for(LinkList<? extends Expression> theRight = right(); !theRight.isEmpty(); theRight = theRight.tail)
			{
				theBuilder.append(' ').append(this.operator()).append(' ');
				theRight.head.formatFullParenthesis(theBuilder);
			}
			theBuilder.append(')');
		}

		@Override
		abstract public double evaluate();
	}


	@Immutable
	private static final class SubtractionNode extends ChainedExpressionNode implements SubtractionExpression
	{
		public SubtractionNode(LinkList<Expression> theOperands)
		{
			super(theOperands);
		}

		@Override
		public final String operator() {
			return "-";
		}

		@Override
		public double evaluate()
		{
			//
			// evaluate multiplication/division expressions from left to right
			//
			double theLeftValue = left().evaluate();
			for(LinkList<? extends Expression> theRight = right(); !theRight.isEmpty(); theRight = theRight.tail)
			{
				theLeftValue -= theRight.head.evaluate();
			}
			return theLeftValue;
		}
	}
	
	@Immutable
	private static final class AdditionNode extends ChainedExpressionNode implements AdditionExpression
	{
		public AdditionNode(LinkList<Expression> theOperands)
		{
			super(theOperands);
		}

		@Override
		public final String operator() {
			return "+";
		}

		@Override
		public double evaluate()
		{
			//
			// evaluate multiplication/division expressions from left to right
			//
			double theLeftValue = left().evaluate();
			for(LinkList<? extends Expression> theRight = right(); !theRight.isEmpty(); theRight = theRight.tail)
			{
				theLeftValue += theRight.head.evaluate();
			}
			return theLeftValue;
		}
	}


	/**
	 * expression node for a chain of division operations
	 */
	@Immutable
	private static final class DivisionNode extends ChainedExpressionNode implements MultiplicationExpression
	{
		public DivisionNode(LinkList<Expression> theOperands)
		{
			super(theOperands);
		}

		@Override
		public final String operator() {
			return "/";
		}

		@Override
		public double evaluate()
		{
			//
			// evaluate multiplication/division expressions from left to right
			//
			double theLeftValue = left().evaluate();
			for(LinkList<? extends Expression> theRight = right(); !theRight.isEmpty(); theRight = theRight.tail)
			{
				theLeftValue /= theRight.head.evaluate();
			}
			return theLeftValue;
		}
	}

	/**
	 * expression node for a chain of multiplication/division operations
	 */
	@Immutable
	private static final class MultiplicationNode extends ChainedExpressionNode implements MultiplicationExpression
	{
		public MultiplicationNode(LinkList<Expression> theOperands)
		{
			super(theOperands);
		}

		@Override
		public String operator()
		{
			return "*";
		}

		@Override
		public double evaluate()
		{
			//
			// evaluate multiplication/division expressions from left to right
			//
			double theLeftValue = left().evaluate();
			for(LinkList<? extends Expression> theRight = right(); !theRight.isEmpty(); theRight = theRight.tail)
			{
				theLeftValue *= theRight.head.evaluate();
			}
			return theLeftValue;
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
	}
}
