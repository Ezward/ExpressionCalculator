package com.lumpofcode.expression;

import com.lumpofcode.utils.NumberFormatter;

/**
 * NOTE: this code ignores exponentiation.
 *
 * Created by emurphy on 11/30/16.
 */
public final class ExpressionTreeHelper
{
	private ExpressionTreeHelper() { throw new RuntimeException("Construction is not allowed."); }

	/**
	 * Evaluate the highest order operation and return the resulting expression.
	 *
	 * @param theExpressionText
	 * @param formatter formats calculated numeric values
	 * @return expression resulting from evaluating the highest order operation
	 */
	public static String evaluateNextOperation(final String theExpressionText, final NumberFormatter formatter)
	{
		//
		// parse the incoming expression text
		//
		final ExpressionEvaluator.Expression expression = ExpressionEvaluator.parse(theExpressionText);

		//
		// determine the highest order operation to evaluate
		//
		ExpressionEvaluator.Expression operation = nextOperation(expression);
		if(null == operation)
		{
			// nothing to evaluate
			return theExpressionText;
		}

		//
		// if it is parenthesis, then the operation is inside the parenthesis
		//
		ExpressionEvaluator.ParenthesisExpression outerParenthesis = null;
		if(operation instanceof ExpressionEvaluator.ParenthesisExpression)
		{
			outerParenthesis = (ExpressionEvaluator.ParenthesisExpression)operation;
			operation = nextOperation(outerParenthesis.innerExpression());
		}

		//
		// this should be either a literal value or a chained expression
		//
		if(operation instanceof ExpressionEvaluator.ChainedExpression)
		{
			//
			// evaluate the first operation in the chain of operations
			//
			final ExpressionEvaluator.ChainedExpression chainedExpression = (ExpressionEvaluator.ChainedExpression)operation;
			final double leftOperand = chainedExpression.left().evaluate();
			final double rightOperand = chainedExpression.right().expression().evaluate();
			final String operator = chainedExpression.right().operator();
			final double theResult = evaluateBinaryOperation(leftOperand, operator, rightOperand);

			// If the chained expression results in a literal and we are inside parenthesis, remove the parenthesis.
			if((null != outerParenthesis) && (null == chainedExpression.right().next()))
			{
				return insertWithin(
					theExpressionText,
					outerParenthesis.startIndex(),
					outerParenthesis.endIndex(),
					formatter.format(theResult));
			}

			//
			// replace the first operation in the chained expression
			//
			return insertWithin(
				theExpressionText,
				chainedExpression.left().startIndex(),
				chainedExpression.right().endIndex(),
				formatter.format(theResult));
		}
		else if(operation instanceof ExpressionEvaluator.NumberExpression)   // it is a literal value
		{
			final ExpressionEvaluator.NumberExpression numberExpression = (ExpressionEvaluator.NumberExpression)operation;

			// If the operation is a literal inside parenthesis, then replace parenthesis with the literal
			if(null != outerParenthesis)
			{
				return insertWithin(
					theExpressionText,
					outerParenthesis.startIndex(),
					outerParenthesis.endIndex(),
					numberExpression.number()); // this maintains the number as input
			}
		}
		else
		{
			throw new RuntimeException("Unexpected expression enountered in evaluateNextOperation()");
		}

		return theExpressionText;
	}


	/**
	 * Find the next expression to evaluate using left to right ordering and PEDMAS rules.
	 *
	 * @param expression
	 * @return the expression to evaluate or null if there are no more operations.
	 */
	public static ExpressionEvaluator.Expression nextOperation(final ExpressionEvaluator.Expression expression)
	{
		//
		// 1. find the left most set of parenthesis
		//    a. recursively apply rules 1..3 to contents of parenthesis
		// 2. find the left most multiplication operation
		// 3. if there are no multiplications, find the left most addition operations
		// 4. if there is only a number, then evaluation is complete
		//

		ExpressionEvaluator.Expression operation = findParenthesis(expression);
		if(null == operation)
		{
			operation = findMultiplication(expression);
		}
		if(null == operation)
		{
			operation = findAddition(expression);
		}
		return operation;
	}

	private static ExpressionEvaluator.ParenthesisExpression findParenthesis(final ExpressionEvaluator.Expression expression)
	{
		if(expression instanceof ExpressionEvaluator.ParenthesisExpression)
		{
			//
			// look for parenthesis within the parenthesis
			//
			ExpressionEvaluator.ParenthesisExpression parenthesis = (ExpressionEvaluator.ParenthesisExpression)expression;
			ExpressionEvaluator.ParenthesisExpression innerParenthesis = findParenthesis(parenthesis.innerExpression());
			while(null != innerParenthesis)
			{
				parenthesis = innerParenthesis;
				innerParenthesis = findParenthesis(parenthesis.innerExpression());
			}

			return parenthesis;
		}

		//
		// if it is a chained expression (series of additons or multiplications)
		// then look in each operand, left to right, for parenthesis
		//
		if(expression instanceof ExpressionEvaluator.ChainedExpression)
		{
			//
			// look in each operand for a parenthesis expression
			//
			ExpressionEvaluator.ChainedExpression chainedExpression = (ExpressionEvaluator.ChainedExpression)expression;
			ExpressionEvaluator.ParenthesisExpression parenthesisExpression = findParenthesis(chainedExpression.left());
			if(null != parenthesisExpression)
			{
				return parenthesisExpression;
			}

			ExpressionEvaluator.RightExpression rightExpression = chainedExpression.right();
			while (null != rightExpression)
			{
				parenthesisExpression = findParenthesis(rightExpression.expression());
				if(null != parenthesisExpression)
				{
					return parenthesisExpression;
				}
				rightExpression = rightExpression.next();
			}
		}

		//
		// can't find any parenthesis
		//
		return null;
	}

	private static ExpressionEvaluator.MultiplicationExpression findMultiplication(final ExpressionEvaluator.Expression expression)
	{
		if(expression instanceof ExpressionEvaluator.MultiplicationExpression)
		{
			return (ExpressionEvaluator.MultiplicationExpression)expression;
		}

		//
		// recursively look inside parenthesis
		//
		if(expression instanceof ExpressionEvaluator.ParenthesisExpression)
		{
			ExpressionEvaluator.ParenthesisExpression parenthesisExpression = (ExpressionEvaluator.ParenthesisExpression)expression;
			return findMultiplication(parenthesisExpression.innerExpression());
		}

		if(expression instanceof ExpressionEvaluator.AdditionExpression)
		{
			//
			// look in each operator for a multiplication
			//
			ExpressionEvaluator.AdditionExpression additionExpression = (ExpressionEvaluator.AdditionExpression)expression;
			ExpressionEvaluator.MultiplicationExpression multiplicationExpression = findMultiplication(additionExpression.left());
			if(null != multiplicationExpression)
			{
				return multiplicationExpression;
			}
			ExpressionEvaluator.RightExpression rightExpression = additionExpression.right();
			while(null != rightExpression)
			{
				multiplicationExpression = findMultiplication(rightExpression.expression());
				if(null != multiplicationExpression)
				{
					return multiplicationExpression;
				}
				rightExpression = rightExpression.next();
			}
		}

		//
		// no mulitplications
		//
		return null;
	}

	private static ExpressionEvaluator.AdditionExpression findAddition(final ExpressionEvaluator.Expression expression)
	{
		if(expression instanceof ExpressionEvaluator.AdditionExpression)
		{
			return (ExpressionEvaluator.AdditionExpression)expression;
		}

		//
		// recursively look inside parenthesis
		//
		if(expression instanceof ExpressionEvaluator.ParenthesisExpression)
		{
			ExpressionEvaluator.ParenthesisExpression parenthesisExpression = (ExpressionEvaluator.ParenthesisExpression)expression;
			return findAddition(parenthesisExpression.innerExpression());
		}

		if(expression instanceof ExpressionEvaluator.MultiplicationExpression)
		{
			//
			// look in each operator for an addition
			//
			ExpressionEvaluator.MultiplicationExpression multiplicationExpression = (ExpressionEvaluator.MultiplicationExpression)expression;
			ExpressionEvaluator.AdditionExpression additionExpression = findAddition(multiplicationExpression.left());
			if(null != additionExpression)
			{
				return additionExpression;
			}
			ExpressionEvaluator.RightExpression rightExpression = multiplicationExpression.right();
			while(null != rightExpression)
			{
				additionExpression = findAddition(rightExpression.expression());
				if(null != additionExpression)
				{
					return additionExpression;
				}
				rightExpression = rightExpression.next();
			}
		}

		//
		// no additions
		//
		return null;
	}


	/**
	 * evaluate an operation and return the result.
	 *
	 * @param theLeftOperand
	 * @param theOperator +, -, *, /, or ^
	 * @param theRightOperand
	 * @return the result of the operation
	 */
	private static double evaluateBinaryOperation(double theLeftOperand, String theOperator, double theRightOperand)
	{
		if("+".equals(theOperator)) return theLeftOperand + theRightOperand;
		if("-".equals(theOperator)) return theLeftOperand - theRightOperand;
		if("*".equals(theOperator)) return theLeftOperand * theRightOperand;
		if("/".equals(theOperator)) return theLeftOperand / theRightOperand;
		if("^".equals(theOperator)) return Math.pow(theLeftOperand, theRightOperand);
		throw new IllegalStateException("Invalid binary operator ($operator).".replace("$operator", (null != theOperator) ? theOperator : "null"));
	}

	/**
	 * splice the insertion text into the given string, replacing the given character range.
	 *
	 * @param theSourceText the string to modify
	 * @param leftIndex the start of the range to replace
	 * @param rightIndex the end of the range to replace
	 * @param theInsertText the text to insert
	 * @return
	 */
	private static String insertWithin(final String theSourceText, final int leftIndex, final int rightIndex, final String theInsertText)
	{
		final String theResultExpressionText =
			theSourceText.substring(0, leftIndex)
				+ theInsertText
				+ theSourceText.substring(rightIndex);

		//
		// parse and reformat in a regular way
		//
		return ExpressionEvaluator.parse(theResultExpressionText).format();
	}

}
