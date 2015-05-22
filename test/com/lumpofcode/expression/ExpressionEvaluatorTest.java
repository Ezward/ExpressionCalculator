package com.lumpofcode.expression;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by emurphy on 5/21/15.
 */
public class ExpressionEvaluatorTest
{
    @Test
    public void testEvaluateNumber()
    {
        assertTrue(123 == new ExpressionEvaluator(ExpressionParser.parse("123 ")).evaluate());
        assertTrue(-123 == new ExpressionEvaluator(ExpressionParser.parse(" -123 ")).evaluate());
    }

    @Test
    public void testEvaluateAddition()
    {
        ExpressionParser.Expression theExpression = ExpressionParser.parse("123 + 456 ");
        assertNotNull(theExpression);

        assertTrue(579 == new ExpressionEvaluator(ExpressionParser.parse("123 + 456 ")).evaluate());

        assertTrue(333 == new ExpressionEvaluator(ExpressionParser.parse("-123 + 456")).evaluate());

        assertTrue(-579 == new ExpressionEvaluator(ExpressionParser.parse("-123 + -456")).evaluate());

        assertTrue(15 == new ExpressionEvaluator(ExpressionParser.parse(" 1 + 2 + 3 + 4 + 5 ")).evaluate());
    }

    @Test
    public void testEvaluateSubtraction()
    {
        assertTrue(333 == new ExpressionEvaluator(ExpressionParser.parse(" 456 - 123 ")).evaluate());

        assertTrue(-579 == new ExpressionEvaluator(ExpressionParser.parse(" -456 - 123")).evaluate());

        assertTrue(-333 == new ExpressionEvaluator(ExpressionParser.parse(" -456 - -123 ")).evaluate());

        assertTrue(-5 == new ExpressionEvaluator(ExpressionParser.parse(" 5 - 4 - 3 - 2 - 1 ")).evaluate());
    }

    @Test
    public void testEvaluateMultiplication()
    {
        assertTrue(12 == new ExpressionEvaluator(ExpressionParser.parse("3 * 4 ")).evaluate());

        assertTrue(-12 == new ExpressionEvaluator(ExpressionParser.parse("-3 * 4")).evaluate());

        assertTrue(12 == new ExpressionEvaluator(ExpressionParser.parse("-3 * -4")).evaluate());

        assertTrue(120 == new ExpressionEvaluator(ExpressionParser.parse("1 * 2 * 3 * 4 * 5 ")).evaluate());
    }

    @Test
    public void testEvaluateDivision()
    {
        assertTrue(3 == new ExpressionEvaluator(ExpressionParser.parse("12 / 4 ")).evaluate());

        assertTrue(-3 == new ExpressionEvaluator(ExpressionParser.parse("-12 / 4")).evaluate());

        assertTrue(3 == new ExpressionEvaluator(ExpressionParser.parse("-12 / -4")).evaluate());

        assertTrue(1 == new ExpressionEvaluator(ExpressionParser.parse("24 / 4 / 3 / 2 / 1 ")).evaluate());
    }

    @Test
    public void testEvaluateParenthesis()
    {
        assertTrue(100 == new ExpressionEvaluator(ExpressionParser.parse("(100) ")).evaluate());

        assertTrue(-100 == new ExpressionEvaluator(ExpressionParser.parse("-(100)")).evaluate());

        assertTrue(-9 == new ExpressionEvaluator(ExpressionParser.parse("1-2*(3+4/2)")).evaluate());

        assertTrue(-110 == new ExpressionEvaluator(ExpressionParser.parse("-(((10 + 5) * 6) - 20 / 2 * 3 + 100 - 50) ")).evaluate());
    }

    @Test
    public void testEvaluateParenthesisWithNegativeNumber()
    {
        assertTrue(-270 == new ExpressionEvaluator(ExpressionParser.parse(" (((10 + 5) * -6) - -20 / -2 * 3 + -100 - 50)")).evaluate());
    }

}
