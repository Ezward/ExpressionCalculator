package com.lumpofcode.expression;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionParserTest
{
	@Test
	public void testParseEmpty()
	{
		final ExpressionParser.Expression theExpression = ExpressionParser.parse("");
		assertNull(theExpression);
	}

	@Test
	public void testParseNumber()
	{
		final ExpressionParser.Expression theExpression = ExpressionParser.parse("123 ");
		assertNotNull(theExpression);
		
		assertTrue(123 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("123", theBuilder.toString());
		
		final ExpressionParser.Expression theNegativeExpression = ExpressionParser.parse("-123");
		assertNotNull(theNegativeExpression);
		
		assertTrue(-123 == theNegativeExpression.evaluate());
		theBuilder = new StringBuilder();
		theNegativeExpression.format(theBuilder);
		assertEquals("-123", theBuilder.toString());

	}
	
	@Test
	public void testParseAddition()
	{
		ExpressionParser.Expression theExpression = ExpressionParser.parse("123 + 456 ");
		assertNotNull(theExpression);
		
		assertTrue(579 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("123 + 456", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-123 + 456");
		assertNotNull(theExpression);
		
		assertTrue(333 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-123 + 456", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-123 + -456");
		assertNotNull(theExpression);
		
		assertTrue(-579 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-123 + -456", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("1 + 2 + 3 + 4 + 5 ");
		assertNotNull(theExpression);
		
		assertTrue(15 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("1 + 2 + 3 + 4 + 5", theBuilder.toString());
	}

	@Test
	public void testParseSubtraction()
	{
		ExpressionParser.Expression theExpression = ExpressionParser.parse("456 - 123 ");
		assertNotNull(theExpression);
		
		assertTrue(333 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("456 - 123", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-456 - 123");
		assertNotNull(theExpression);
		
		assertTrue(-579 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-456 - 123", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-456 - -123");
		assertNotNull(theExpression);
		
		assertTrue(-333 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-456 - -123", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("5 - 4 - 3 - 2 - 1 ");
		assertNotNull(theExpression);
		
		assertTrue(-5 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("5 - 4 - 3 - 2 - 1", theBuilder.toString());
	}
	
	@Test
	public void testParseMultiplication()
	{		
		ExpressionParser.Expression theExpression = ExpressionParser.parse("3 * 4 ");
		assertNotNull(theExpression);
		
		assertTrue(12 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("3 * 4", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-3 * 4");
		assertNotNull(theExpression);
		
		assertTrue(-12 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-3 * 4", theBuilder.toString());

		theExpression = ExpressionParser.parse("-3 * -4");
		assertNotNull(theExpression);
		
		assertTrue(12 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-3 * -4", theBuilder.toString());

		theExpression = ExpressionParser.parse("1 * 2 * 3 * 4 * 5 ");
		assertNotNull(theExpression);
		
		assertTrue(120 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("1 * 2 * 3 * 4 * 5", theBuilder.toString());
		
	}
	
	@Test
	public void testParseDivision()
	{
		ExpressionParser.Expression theExpression = ExpressionParser.parse("12 / 4 ");
		assertNotNull(theExpression);
		
		assertTrue(3 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("12 / 4", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-12 / 4");
		assertNotNull(theExpression);
		
		assertTrue(-3 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-12 / 4", theBuilder.toString());

		theExpression = ExpressionParser.parse("-12 / -4");
		assertNotNull(theExpression);
		
		assertTrue(3 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-12 / -4", theBuilder.toString());

		theExpression = ExpressionParser.parse("24 / 4 / 3 / 2 / 1 ");
		assertNotNull(theExpression);
		
		assertTrue(1 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("24 / 4 / 3 / 2 / 1", theBuilder.toString());

	}
	
	@Test
	public void testParseParenthesis()
	{
		ExpressionParser.Expression theExpression = ExpressionParser.parse("(100) ");
		assertNotNull(theExpression);
		
		assertTrue(100 == theExpression.evaluate());
		StringBuilder theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("(100)", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-(100)");
		assertNotNull(theExpression);
		
		assertTrue(-100 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-(100)", theBuilder.toString());
		
		theExpression = ExpressionParser.parse("-(((10 + 5) * 6) - 20 / 2 * 3 + 100 - 50) ");
		assertNotNull(theExpression);
		
		assertTrue(-110 == theExpression.evaluate());
		theBuilder = new StringBuilder();
		theExpression.format(theBuilder);
		assertEquals("-(((10 + 5) * 6) - 20 / 2 * 3 + 100 - 50)", theBuilder.toString());
	}

	@Test
	public void testParseParenthesisWithNegativeNumber()
	{
		ExpressionParser.Expression theExpression = ExpressionParser.parse(" (((10 + 5) * -6) - -20 / -2 * 3 + -100 - 50)");
		assertNotNull(theExpression);

		assertTrue(-270 == theExpression.evaluate());
		assertEquals("(((10 + 5) * -6) - -20 / -2 * 3 + -100 - 50)", theExpression.format());
	}

	@Test
	public void testParseParenthesisWithDecimalNumber()
	{
		ExpressionParser.Expression theExpression = ExpressionParser.parse("(((10.0e0 + 5.0e0) * -6.0e0) - -20e0 / -2.000e000 * 3e0 + -1.0e02 - 5.0e1)");
		assertNotNull(theExpression);

		assertTrue(-270 == theExpression.evaluate());
		assertEquals("(((10.0e0 + 5.0e0) * -6.0e0) - -20e0 / -2.000e000 * 3e0 + -1.0e02 - 5.0e1)", theExpression.format());
	}

	@Test
	public void testFormatFullParenthesis()
	{
		ExpressionParser.Expression theExpression = ExpressionParser.parse(" 10 + 5 * -6 - -20 / -2 * 3 + -100 - 50 ");
		assertNotNull(theExpression);
		
		System.out.println(theExpression.evaluate());
		System.out.println(theExpression.format());
		System.out.println(theExpression.formatFullParenthesis());
		
		assertTrue(-200 == theExpression.evaluate());
		assertEquals("10 + 5 * -6 - -20 / -2 * 3 + -100 - 50", theExpression.format());
		assertEquals("(10 + (5 * -6) - (-20 / -2 * 3) + -100 - 50)", theExpression.formatFullParenthesis());
	}
}
