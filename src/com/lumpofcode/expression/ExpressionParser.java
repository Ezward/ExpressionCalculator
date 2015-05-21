package com.lumpofcode.expression;

import com.lumpofcode.utils.StringUtils;
import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * @author Ezward
 * 
 * Singleton class to parse, evaluate and pretty-print simple 4-operator expressions
 * that use the following PEG grammar;
 *
 * NUMBER ::= [0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9]*
 * value ::= ('-') ['(' expression ')' | NUMBER ]
 * product ::= value ([ '*' | 'x' | '/' ] value)*
 * sum ::= product  (['+' | '-'] product)*
 * expression ::= sum
 * 
 * Key to PEG notation:
 * () = choose zero or one
 * ()* = 0 or more
 * [] = choose one
 * []* = 1 or more
 * 
 * Usage:
 * 		ExpressionEvaluator.Expression theExpression = ExpressionEvaluator.parse("(((10 + 5) x -6) - -20 / -2 x 3 + -100 - 50)");
 *		if(null == theExpression) throw new RuntimeException("Parse error");
 *		double theValue = theExpression.evaluate();
 *		String thePrettyPrint = theExpression.format();
 * 
 * TODO: support stack-based evaluation (rather than recursive evaluation)
 * TODO: support decimal numbers.
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
	public interface Expression
	{
		public abstract double evaluate();

		public abstract String format();
		public abstract void format(StringBuilder theBuilder);
		
		public abstract String formatFullParenthesis();
		public abstract void formatFullParenthesis(StringBuilder theBuilder);
		
		// Expression's position in original input stream
		public abstract int startIndex();
		public abstract int endIndex();

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
		public abstract String number();
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
		public abstract boolean sign();
		
		/**
		 * @return the Expression between the parenthesis.
		 */
		public abstract ExpressionNode innerExpression();

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
		public abstract char operator();
		public abstract Expression expression();
		public abstract RightExpression next();
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
		public abstract Expression left();
		public abstract RightExpression right();
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
	public static final Expression parse(final String theInput)
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
	
	private static final ExpressionNode parseValue(final String theInput, final int theIndex)
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
			final int theEndIndex = StringUtils.scanNumeric(theInput, theExpressionIndex);

			// include the sign with the digits (rather than a separate negation operation)
			final String theNumber = theInput.substring(theStartIndex, theEndIndex);
			
			return new NumberNode(theStartIndex, theEndIndex, theNumber);
		}
			
		throw new RuntimeException();	// Expected parenthesis or number.
	}
	
	private static final ExpressionNode parseProduct(final String theInput, int theIndex)
	{
		final ExpressionNode theLeftExpression = parseValue(theInput, theIndex);
		if(null != theLeftExpression)
		{
			final int theLength = theInput.length();
			int theOperatorIndex = StringUtils.scanWhitespace(theInput, theLeftExpression.endIndex());
			if(theOperatorIndex < theLength)
			{
				char theOperator = theInput.charAt(theOperatorIndex);
				if(('*' == theOperator) || ('x' == theOperator) || ('/' == theOperator))
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

	private static final ExpressionNode parseSum(final String theInput, int theIndex)
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
		public final double evaluate()
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
				else
				{
					theLeftValue -= theRight.evaluate();
				}
			}
			return theLeftValue;
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
		public final char operator() { return thisOperator; }
		
		@Override
		public final ExpressionNode expression() { return thisExpression; }

		@Override
		public final RightExpression next() { return thisNext; }

		@Override
		public final double evaluate() { return thisExpression.evaluate(); }

		@Override
		public String format()
		{
			final StringBuilder theBuilder = new StringBuilder();
			this.format(theBuilder);
			return theBuilder.toString();
		}

		@Override
		public final void format(StringBuilder theBuilder)
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
		public final void formatFullParenthesis(StringBuilder theBuilder)
		{
			theBuilder.append(' ').append(thisOperator).append(' ');
			thisExpression.formatFullParenthesis(theBuilder);
		}

		/**
		 * Last RightExpression in the list.
		 *
		 * Note: This walks the list to find the last node,
		 *       so this can be an expensive operation.
		 *
		 * @return the last non-Nil node in the list or Nil if list is empty
		 */
		public final RightExpressionNode last()
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
		public final double evaluate()
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
				else
				{
					theLeftValue *= theRight.evaluate();
				}
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
		private ExpressionNode thisInnerExpression;
		private boolean thisSign;
		
		public ParenthesisNode(final int theStartIndex, final int theEndIndex, final boolean theSign, final ExpressionNode theInnerExpression)
		{
			super(theStartIndex, theEndIndex);
			
			if(null == theInnerExpression) throw new RuntimeException();
			
			thisSign = theSign;
			thisInnerExpression = theInnerExpression;
		}
		
		@Override
		public final boolean sign() { return thisSign; }
		
		@Override
		public final ExpressionNode innerExpression() { return thisInnerExpression; }
		
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
		public final double evaluate()
		{
			final double theValue = thisInnerExpression.evaluate();
			return thisSign ? theValue : -theValue;
		}
	}

	/**
	 * Immutable expression node for a number.
	 */
	@Immutable
	private static final class NumberNode extends ExpressionNode implements NumberExpression
	{
		private final String thisNumber;
		
		public NumberNode(final int theStartIndex, final int theEndIndex, final String theNumber)
		{
			super(theStartIndex, theEndIndex);
			
			if((null == theNumber) || theNumber.isEmpty()) throw new RuntimeException();
			
			thisNumber = theNumber;
			
		}
		
		@Override
		public final String number() { return thisNumber; }
		
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
		public final double evaluate()
		{
			if('-' == thisNumber.charAt(0))
			{
				return -(StringUtils.parseInteger(thisNumber, 1, thisNumber.length()));
			}
			return StringUtils.parseInteger(thisNumber, 0, thisNumber.length());
		}
	}
}
