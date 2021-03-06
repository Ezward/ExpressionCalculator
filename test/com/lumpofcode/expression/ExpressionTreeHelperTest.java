package com.lumpofcode.expression;

import com.lumpofcode.utils.NumberFormatter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by emurphy on 11/30/16.
 */
public class ExpressionTreeHelperTest
{
	@Test
	public void testEvaluateNextOperation()
	{
		// formatter truncates doubles to ints
		final NumberFormatter numberFormatter = (d) -> String.valueOf((int)d);

		assertEquals("1 + 2 evaluates to 3", "3", ExpressionTreeHelper.evaluateNextOperation("1 + 2", numberFormatter));
		assertEquals("(1 + 2) evaluates to 3", "3", ExpressionTreeHelper.evaluateNextOperation("1 + 2", numberFormatter));
		assertEquals("(3) evaluates to 3", "3", ExpressionTreeHelper.evaluateNextOperation("1 + 2", numberFormatter));

		assertEquals("1 + 2 + 3 evaluates to 3 + 3", "3 + 3", ExpressionTreeHelper.evaluateNextOperation("1 + 2 + 3", numberFormatter));
		assertEquals("1 + 2 * 3 evaluates to 1 + 6", "1 + 6", ExpressionTreeHelper.evaluateNextOperation("1 + 2 * 3", numberFormatter));
		assertEquals("(1 + 2) * 3 evaluates to 3 * 3", "3 * 3", ExpressionTreeHelper.evaluateNextOperation("(1 + 2) * 3", numberFormatter));
		assertEquals("1 + (2 * 3) evaluates to 1 + 6", "1 + 6", ExpressionTreeHelper.evaluateNextOperation("1 + (2 * 3)", numberFormatter));
		assertEquals("(1 + (2 * 3)) evaluates to (1 + 6)", "(1 + 6)", ExpressionTreeHelper.evaluateNextOperation("(1 + (2 * 3))", numberFormatter));

		//
		// the process reformats with space around operators
		//
		assertEquals("1+2+3 evaluates to 3 + 3", "3 + 3", ExpressionTreeHelper.evaluateNextOperation("1+2+3", numberFormatter));

		//
		// shows how to evaluate to a final answer one step at a time
		// 4 + ((1 + 2) * 3) * 5
		// 4 + (3 * 3) * 5
		// 4 + 9 * 5
		// 4 + 45
		// 49
		//
		assertEquals("4 + ((1 + 2) * 3) * 5 evaluates to 4 + (3 * 3) * 5", "4 + (3 * 3) * 5", ExpressionTreeHelper.evaluateNextOperation("4 + ((1 + 2) * 3) * 5", numberFormatter));
		assertEquals("4 + (3 * 3) * 5 evaluates to 4 + 9 * 5", "4 + 9 * 5", ExpressionTreeHelper.evaluateNextOperation("4 + (3 * 3) * 5", numberFormatter));
		assertEquals("4 + 9 * 5 evaluates to 4 + 45", "4 + 45", ExpressionTreeHelper.evaluateNextOperation("4 + 9 * 5", numberFormatter));
		assertEquals("4 + 45 evaluates to 49", "49", ExpressionTreeHelper.evaluateNextOperation("4 + 45", numberFormatter));
		assertEquals("49 evaluates to 49", "49", ExpressionTreeHelper.evaluateNextOperation("49", numberFormatter));

		String theExpression = "4 + ((1 + 2) * 3) * 5";
		String theSimplifiedExpression = ExpressionTreeHelper.evaluateNextOperation(theExpression, numberFormatter);

		//
		// simplify until we have a literal value
		//
		System.out.println(theExpression);
		while(!theExpression.equals(theSimplifiedExpression))
		{
			theExpression = theSimplifiedExpression;
			System.out.println(theExpression);

			theSimplifiedExpression = ExpressionTreeHelper.evaluateNextOperation(theExpression, numberFormatter);
		}

		//
		// illegal expressions will throw a parse error
		//
		try
		{
			ExpressionTreeHelper.evaluateNextOperation("2 + 3 +", numberFormatter);
			fail("A ParseException should be thrown, we should not get here.");
		}
		catch(ExpressionParser.ParseException e)
		{
			assertTrue(true);
		}
		catch(Throwable e)
		{
			fail("Unexpected exception thrown.");
		}
	}

	@Test
	public void testNextParenthesisOperation()
	{
		final String expressionText = "10 + 5 * -6 - (-20 / (-2 + 3)) + -100 - 50";
		final ExpressionParser.Expression expressionTree = ExpressionParser.parse(expressionText);
		final ExpressionParser.Expression operation = ExpressionTreeHelper.nextOperation(expressionTree);

		assertNotNull("There should be an operation", operation);
		assertTrue("It should be a parenthesis operation", operation instanceof ExpressionParser.ParenthesisExpression);
		assertEquals("It should be at index 21 of input", 21, operation.startIndex());
		assertEquals("It should extent up to (not including) index 29 of input", 29, operation.endIndex());
		assertEquals("It should format to (-2 + 3)", "(-2 + 3)", operation.format());
	}

	@Test
	public void testNextMultiplicationOperation()
	{
		final String expressionText = "10 + 5 * -6 - -20 / -2 + 3 + -100 - 50";
		final ExpressionParser.Expression expressionTree = ExpressionParser.parse(expressionText);
		final ExpressionParser.Expression operation = ExpressionTreeHelper.nextOperation(expressionTree);

		assertNotNull("There should be an operation", operation);
		assertTrue("It should be a multiplication operation", operation instanceof ExpressionParser.MultiplicationExpression);
		assertEquals("It should start at index 5 of input", 5, operation.startIndex());
		assertEquals("It should extend to index 11 of input.", 11, ((ExpressionParser.MultiplicationExpression)operation).right().endIndex());
		assertEquals("Operator should be at index 7 of input", 7, ((ExpressionParser.MultiplicationExpression)operation).right().startIndex());
		assertEquals("Operator is a multiplication", "*", ((ExpressionParser.MultiplicationExpression)operation).right().operator());
	}

	@Test
	public void testNextAdditionOperation()
	{
		final String expressionText = "10 - 5 + -6 - -20";
		final ExpressionParser.Expression expressionTree = ExpressionParser.parse(expressionText);
		final ExpressionParser.Expression operation = ExpressionTreeHelper.nextOperation(expressionTree);

		assertNotNull("There should be an operation", operation);
		assertTrue("It should be an addition operation", operation instanceof ExpressionParser.AdditionExpression);
		assertEquals("It should start at index 0 of input", 0, operation.startIndex());
		assertEquals("It should extend to index 6 of input.", 6, ((ExpressionParser.AdditionExpression)operation).right().endIndex());
		assertEquals("Operator should be at index 3 of input", 3, ((ExpressionParser.AdditionExpression)operation).right().startIndex());
		assertEquals("Operator is a subtraction", "-", ((ExpressionParser.AdditionExpression)operation).right().operator());
	}
	
	@Test
	public void removeParenthesisTest()
	{
		assertEquals("2 + 3", ExpressionTreeHelper.removeParenthesis("(((2)+(3)))"));
		assertEquals("1 + 2 + 3", ExpressionTreeHelper.removeParenthesis("1 + (((2)+(3)))"));

		assertEquals("1 + 2 + 3", ExpressionTreeHelper.removeParenthesis("(1+2+3)"));
		assertEquals("1 + 2 + 3", ExpressionTreeHelper.removeParenthesis("((1+2+3))"));
		assertEquals("1 + 2 + 3", ExpressionTreeHelper.removeParenthesis("((1+2)+3)"));
		assertEquals("1 + 2 + 3", ExpressionTreeHelper.removeParenthesis("(1+(2)+3)"));
		assertEquals("1 + 2 + 3", ExpressionTreeHelper.removeParenthesis("(1+(2+3))"));
		assertEquals("1 + 2 + 3", ExpressionTreeHelper.removeParenthesis("(((1)+(((2)+(3)))))"));
		
		
		assertEquals("1 * 2 * 3 / 6", ExpressionTreeHelper.removeParenthesis("1 * 2 * (3 / 6)"));
		
	}

}
