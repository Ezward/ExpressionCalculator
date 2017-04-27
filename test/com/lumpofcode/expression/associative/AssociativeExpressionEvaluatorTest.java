package com.lumpofcode.expression.associative;

import com.lumpofcode.expression.associative.AssociativeExpressionEvaluator;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssociativeExpressionEvaluatorTest
{
	@Test
	public void testParseEmpty()
	{
		final AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("");
		assertTrue(AssociativeExpressionEvaluator.nil == theExpression);
	}
	
	@Test
	public void testParseNumber()
	{
		final AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("123 ");
		assertNotNull(theExpression);
		
		assertTrue(123 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("123", theBuilder.toString());
		
		final AssociativeExpressionEvaluator.Expression theNegativeExpression = AssociativeExpressionEvaluator.parse("-123");
		assertNotNull(theNegativeExpression);
		
		assertTrue(-123 == theNegativeExpression.evaluate());
		theBuilder = new StringBuilder();
		theNegativeExpression.format(theBuilder);
		assertEquals("-123", theBuilder.toString());
		
		try
		{
			// extra decimal point, invalid number
			AssociativeExpressionEvaluator.parse("1.2.3");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
		
	}
	
	@Test
	public void testParseAddition()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("123 + 456 ");
		assertNotNull(theExpression);
		
		assertTrue(579 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("123 + 456", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-123 + 456");
		assertNotNull(theExpression);
		
		assertTrue(333 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-123 + 456", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-123 + -456");
		assertNotNull(theExpression);
		
		assertTrue(-579 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-123 + -456", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("1 + 2 + 3 + 4 + 5 ");
		assertNotNull(theExpression);
		
		assertTrue(15 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("1 + 2 + 3 + 4 + 5", theBuilder.toString());
		
		try
		{
			// hanging addition
			AssociativeExpressionEvaluator.parse("1 + 2 +");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
	}
	
	@Test
	public void testParseSubtraction()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("456 - 123 ");
		assertNotNull(theExpression);
		
		assertTrue(333 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("456 - 123", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-456 - 123");
		assertNotNull(theExpression);
		
		assertTrue(-579 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-456 - 123", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-456 - -123");
		assertNotNull(theExpression);
		
		assertTrue(-333 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-456 - -123", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("5 - 4 - 3 - 2 - 1 ");
		assertNotNull(theExpression);
		
		assertTrue(-5 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("5 - 4 - 3 - 2 - 1", theBuilder.toString());
		
		try
		{
			// hanging subtraction
			AssociativeExpressionEvaluator.parse("1 - 2 -");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
	}
	
	@Test
	public void testParseMultiplication()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("3 * 4 ");
		assertNotNull(theExpression);
		
		assertTrue(12 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("3 * 4", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-3 * 4");
		assertNotNull(theExpression);
		
		assertTrue(-12 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-3 * 4", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-3 * -4");
		assertNotNull(theExpression);
		
		assertTrue(12 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-3 * -4", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("1 * 2 * 3 * 4 * 5 ");
		assertNotNull(theExpression);
		
		assertTrue(120 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("1 * 2 * 3 * 4 * 5", theBuilder.toString());
		
		try
		{
			// hanging multiplication
			AssociativeExpressionEvaluator.parse("1 * 2 *");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
	}
	
	@Test
	public void testParseDivision()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("12 / 4 ");
		assertNotNull(theExpression);
		
		assertTrue(3 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("12 / 4", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-12 / 4");
		assertNotNull(theExpression);
		
		assertTrue(-3 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-12 / 4", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-12 / -4");
		assertNotNull(theExpression);
		
		assertTrue(3 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-12 / -4", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("24 / 4 / 3 / 2 / 1 ");
		assertNotNull(theExpression);
		
		assertTrue(1 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("24 / 4 / 3 / 2 / 1", theBuilder.toString());
		
		try
		{
			// hanging division
			AssociativeExpressionEvaluator.parse("1 / 2 /");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
	}
	
	@Test
	public void testParseTerms()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("123 + 456 - 456 - 123");
		assertNotNull(theExpression);
		
		assertTrue(0 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("123 + 456 - 456 - 123", theBuilder.toString());
		
	}
		
		@Test
	public void testParseParenthesis()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("(100) ");
		assertNotNull(theExpression);
		
		assertTrue(100 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("(100)", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-(100)");
		assertNotNull(theExpression);
		
		assertTrue(-100 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-(100)", theBuilder.toString());
		
		theExpression = AssociativeExpressionEvaluator.parse("-(((10 + 5) * 6) - 20 / 2 * 3 + 100 - 50) ");
		assertNotNull(theExpression);
		
		assertTrue(-110 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-(((10 + 5) * 6) - 20 / 2 * 3 + 100 - 50)", theBuilder.toString());
		
		try
		{
			// unclosed parenthesis
			AssociativeExpressionEvaluator.parse("(1 - 2");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
		
		try
		{
			// unopened parenthesis
			AssociativeExpressionEvaluator.parse("1 - 2)");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
		
		try
		{
			// empty parenthesis
			AssociativeExpressionEvaluator.parse("()");
			fail("Expected a ParseExpection.");
		}
		catch (AssociativeExpressionEvaluator.ParseException e)
		{
			assert(true);
		}
		catch (Exception e)
		{
			fail("Unexpected exception.");
		}
		
	}
	
	@Test
	public void testParseParenthesisWithNegativeNumber()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse(" (((10 + 5) * -6) - -20 / -2 * 3 + -100 - 50)");
		assertNotNull(theExpression);
		
		assertTrue(-270 == theExpression.evaluate());
		assertEquals("(((10 + 5) * -6) - -20 / -2 * 3 + -100 - 50)", theExpression.format());
	}
	
	@Test
	public void testParseParenthesisWithDecimalNumber()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("(((10.0e0 + 5.0e0) * -6.0e0) - -20e0 / -2.000e000 * 3e0 + -1.0e02 - 5.0e1)");
		assertNotNull(theExpression);
		
		System.out.println(theExpression.formatFullParenthesis());
		
		assertTrue(-270 == theExpression.evaluate());
		assertEquals("(((10.0e0 + 5.0e0) * -6.0e0) - -20e0 / -2.000e000 * 3e0 + -1.0e02 - 5.0e1)", theExpression.format());
	}
	
	@Test
	public void testParseExponentiation()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse("2.0^3.0");
		assertNotNull(theExpression);
		
		assertTrue(8.0 == theExpression.evaluate());
		
		theExpression = AssociativeExpressionEvaluator.parse(" 1 + 5 ^ 2 * 10");
		assertNotNull(theExpression);
		
		assertTrue(251 == theExpression.evaluate());
		assertEquals("1 + 5^2 * 10", theExpression.format());
		assertEquals("(1 + ((5^2) * 10))", theExpression.formatFullParenthesis());
		
		//
		// negative exponents result in inverse
		//
		theExpression = AssociativeExpressionEvaluator.parse(" 1 + 10^-2 * 5 ");
		assertNotNull(theExpression);
		
		assertTrue(1.05 == theExpression.evaluate());
		assertEquals("1 + 10^-2 * 5", theExpression.format());
		assertEquals("(1 + ((10^-2) * 5))", theExpression.formatFullParenthesis());
		
		//
		// exponents that are expressions
		//
		theExpression = AssociativeExpressionEvaluator.parse(" 1 + 10^(2 * 3) * 5");
		assertNotNull(theExpression);
		
		System.out.println(theExpression.evaluate());
		
		assertTrue(5000001 == theExpression.evaluate());
		assertEquals("1 + 10^(2 * 3) * 5", theExpression.format());
		assertEquals("(1 + ((10^(2 * 3)) * 5))", theExpression.formatFullParenthesis());
		
	}
	
	@Test
	public void testFormatFullParenthesis()
	{
		AssociativeExpressionEvaluator.Expression theExpression = AssociativeExpressionEvaluator.parse(" 10 + 5 * -6 - -20 / -2 * 3 + -100 - 50 ");
		assertNotNull(theExpression);
		
		System.out.println(theExpression.evaluate());
		System.out.println(theExpression.format());
		System.out.println(theExpression.formatFullParenthesis());
		
		assertTrue(-200 == theExpression.evaluate());
		assertEquals("10 + 5 * -6 - -20 / -2 * 3 + -100 - 50", theExpression.format());

		//
		// full parenthesis means parenthesize all chained expressions.
		// so parenthesis around series of additons, series of subtractions,
		// series of divisions and series of multiplications.
		//
		// assertEquals("(10 + (5 * -6) - (-20 / -2 * 3) + -100 - 50)", theExpression.formatFullParenthesis());
		assertEquals("(10 + ((5 * -6) - ((-20 / -2) * 3)) + (-100 - 50))", theExpression.formatFullParenthesis());
	}
}
