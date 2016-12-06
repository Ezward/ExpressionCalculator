package com.lumpofcode.expression;

import com.lumpofcode.collection.tuple.Tuple2;
import com.lumpofcode.utils.NumberFormatter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
		final ExpressionParser.Expression expression = ExpressionParser.parse(theExpressionText);

		//
		// determine the highest order operation to evaluate
		//
		ExpressionParser.Expression operation = nextOperation(expression);
		if(null == operation)
		{
			// nothing to evaluate
			return theExpressionText;
		}

		//
		// if it is parenthesis, then the operation is inside the parenthesis
		//
		ExpressionParser.ParenthesisExpression outerParenthesis = null;
		if(operation instanceof ExpressionParser.ParenthesisExpression)
		{
			outerParenthesis = (ExpressionParser.ParenthesisExpression)operation;
			operation = nextOperation(outerParenthesis.innerExpression());
		}

		//
		// this should be either a literal value or a chained expression
		//
		if(operation instanceof ExpressionParser.ChainedExpression)
		{
			//
			// evaluate the first operation in the chain of operations
			//
			final ExpressionParser.ChainedExpression chainedExpression = (ExpressionParser.ChainedExpression)operation;
			final double leftOperand = chainedExpression.left().evaluate();
			final double rightOperand = chainedExpression.right().expression().evaluate();
			final String operator = chainedExpression.right().operator();
			final double theResult = evaluateBinaryOperation(leftOperand, operator, rightOperand);

			// If the chained expression results in a literal and we are inside parenthesis,
			// then remove the parenthesis.
			if((null != outerParenthesis) && (null == chainedExpression.right().next()))
			{
				return spliceResult(
					theExpressionText,
					outerParenthesis.startIndex(),
					outerParenthesis.endIndex(),
					formatter.format(theResult));
			}

			//
			// replace the first operation in the chained expression
			//
			return spliceResult(
				theExpressionText,
				chainedExpression.left().startIndex(),
				chainedExpression.right().endIndex(),
				formatter.format(theResult));
		}
		else if(operation instanceof ExpressionParser.NumberExpression)   // it is a literal value
		{
			final ExpressionParser.NumberExpression numberExpression = (ExpressionParser.NumberExpression)operation;

			// If the operation is a literal inside parenthesis,
			// then replace parenthesis with the literal
			if(null != outerParenthesis)
			{
				return spliceResult(
					theExpressionText,
					outerParenthesis.startIndex(),
					outerParenthesis.endIndex(),
					numberExpression.number()); // this maintains the number as input
			}

			// fall through - literal does not need to be evaluated
		}
		else
		{
			// this code does not yet support exponentiations
			throw new RuntimeException("Unexpected expression encountered in evaluateNextOperation()");
		}

		return theExpressionText;
	}


	/**
	 * Find the next expression to evaluate using left to right ordering and PEDMAS rules.
	 *
	 * @param expression
	 * @return the expression to evaluate or null if there are no more operations.
	 */
	public static ExpressionParser.Expression nextOperation(final ExpressionParser.Expression expression)
	{
		//
		// 1. find the left most set of parenthesis
		//    a. recursively apply rules 1..3 to contents of parenthesis
		// 2. find the left most multiplication operation
		// 3. if there are no multiplications, find the left most addition operations
		// 4. if there is only a number, then evaluation is complete
		//

		ExpressionParser.Expression operation = findParenthesis(expression);
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

	//
	// find the left-most, inner-most parenthesis
	//
	private static ExpressionParser.ParenthesisExpression findParenthesis(final ExpressionParser.Expression expression)
	{
		if(expression instanceof ExpressionParser.ParenthesisExpression)
		{
			//
			// look for parenthesis within the parenthesis
			//
			ExpressionParser.ParenthesisExpression parenthesis = (ExpressionParser.ParenthesisExpression)expression;
			ExpressionParser.ParenthesisExpression innerParenthesis = findParenthesis(parenthesis.innerExpression());
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
		if(expression instanceof ExpressionParser.ChainedExpression)
		{
			//
			// look in each operand for a parenthesis expression
			//
			ExpressionParser.ChainedExpression chainedExpression = (ExpressionParser.ChainedExpression)expression;
			ExpressionParser.ParenthesisExpression parenthesisExpression = findParenthesis(chainedExpression.left());
			if(null != parenthesisExpression)
			{
				return parenthesisExpression;
			}

			ExpressionParser.RightExpression rightExpression = chainedExpression.right();
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

	//
	// find the left most multiplication
	//
	private static ExpressionParser.MultiplicationExpression findMultiplication(final ExpressionParser.Expression expression)
	{
		if(expression instanceof ExpressionParser.MultiplicationExpression)
		{
			return (ExpressionParser.MultiplicationExpression)expression;
		}

		//
		// recursively look inside parenthesis
		//
		if(expression instanceof ExpressionParser.ParenthesisExpression)
		{
			ExpressionParser.ParenthesisExpression parenthesisExpression = (ExpressionParser.ParenthesisExpression)expression;
			return findMultiplication(parenthesisExpression.innerExpression());
		}

		if(expression instanceof ExpressionParser.AdditionExpression)
		{
			//
			// look in each operator for a multiplication
			//
			ExpressionParser.AdditionExpression additionExpression = (ExpressionParser.AdditionExpression)expression;
			ExpressionParser.MultiplicationExpression multiplicationExpression = findMultiplication(additionExpression.left());
			if(null != multiplicationExpression)
			{
				return multiplicationExpression;
			}
			ExpressionParser.RightExpression rightExpression = additionExpression.right();
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

	//
	// find the left-most addition
	//
	private static ExpressionParser.AdditionExpression findAddition(final ExpressionParser.Expression expression)
	{
		if(expression instanceof ExpressionParser.AdditionExpression)
		{
			return (ExpressionParser.AdditionExpression)expression;
		}

		//
		// recursively look inside parenthesis
		//
		if(expression instanceof ExpressionParser.ParenthesisExpression)
		{
			ExpressionParser.ParenthesisExpression parenthesisExpression = (ExpressionParser.ParenthesisExpression)expression;
			return findAddition(parenthesisExpression.innerExpression());
		}

		if(expression instanceof ExpressionParser.MultiplicationExpression)
		{
			//
			// look in each operator for an addition
			//
			ExpressionParser.MultiplicationExpression multiplicationExpression = (ExpressionParser.MultiplicationExpression)expression;
			ExpressionParser.AdditionExpression additionExpression = findAddition(multiplicationExpression.left());
			if(null != additionExpression)
			{
				return additionExpression;
			}
			ExpressionParser.RightExpression rightExpression = multiplicationExpression.right();
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
	private static String spliceResult(final String theSourceText, final int leftIndex, final int rightIndex, final String theInsertText)
	{
		final String theResultExpressionText =
			theSourceText.substring(0, leftIndex)
				+ theInsertText
				+ theSourceText.substring(rightIndex);

		//
		// parse and reformat in a regular way
		//
		return ExpressionParser.parse(theResultExpressionText).format();
	}

	/**
	 * Given an expression, generate all equivalent expressions based on
	 * the rules of commutivity.
	 *
	 * @param theExpressionText
	 * @param formatter
	 * @return
	 */
/*
	public static Set<String> generateCommutedExpressions(
		final String theExpressionText,
		final NumberFormatter formatter)
	{
		return generateCommutedExpressions(theExpressionText, formatter, new HashSet<>());
	}

	public static Set<String> generateCommutedExpressions(
		final String theExpressionText,
		final NumberFormatter formatter,
		final Set<String> accumulator)
	{
		final ExpressionParser.Expression expression = ExpressionParser.parse(theExpressionText);

		//
		// once we are down to a literal number, we are done
		//
		if(expression instanceof ExpressionParser.NumberExpression)
		{
			accumulator.add(expression.format());
			return accumulator;
		}

		if(expression instanceof ExpressionParser.ParenthesisExpression)
		{
			final ExpressionParser.ParenthesisExpression parenthesisExpression = (ExpressionParser.ParenthesisExpression)expression;
			final Set<String> innerExpressions = generateCommutedExpressions(parenthesisExpression.format(), formatter, new HashSet<>());

			//
			// the result is the commuted expressions inside parenthesis
			//
			final Set<String> result = new HashSet<>(); // copy
			for(String s : innerExpressions)
			{
				result.add("(" + s + ")");
			}

			return result;
		}

		if(expression instanceof ExpressionParser.ChainedExpression)
		{
			//
			// we can commute around multiplication and addition
			//
			final ExpressionParser.ChainedExpression chainedExpression = (ExpressionParser.ChainedExpression)expression;

			//
			// get all the possible commutations of the left expression
			//
			final Set<String> leftCommuted = generateCommutedExpressions(chainedExpression.left().format(), formatter, new HashSet<>());

			//
			// add all commutations of the left operand
			//
			Set<String> leftCommutations = leftCommuted;
			for(String s : leftCommutations)
			{
				accumulator.add(spliceResult(
					theExpressionText,
					chainedExpression.startIndex(),
					chainedExpression.endIndex(),
					s);
			}


			final List<Tuple2<ExpressionParser.RightExpression, Set<String>>> rightCommuted = new LinkedList<>();
			ExpressionParser.RightExpression rightExpression = chainedExpression.right();
			while(null != rightExpression)
			{
				//
				// generate all possible commutations for this part of chained expression
				//
				rightCommuted.add(
					new Tuple2<ExpressionParser.RightExpression, Set<String>>(
						rightExpression,
						generateCommutedExpressions(rightExpression.expression().format(), formatter, new HashSet<>())));

				rightExpression = rightExpression.next();
			}

			//
			// now walk the list of operations and decide which ones can commute
			//
			rightExpression = chainedExpression.right();
			while(null != rightExpression)
			{
				//
				// add all commutations of right operand
				//
				Set<String> commutations = new HashSet<>();
				for(String s : leftCommutations)
				{
					commutations.add(s + " " + rightExpression.operator() + " " )
				}
			}
		}

	}
*/

}
