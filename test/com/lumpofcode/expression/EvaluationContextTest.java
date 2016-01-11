package com.lumpofcode.expression;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by emurphy on 1/10/16.
 */
public class EvaluationContextTest
{
    @Test
    public void testEvaluateNil()
    {
        // nil expression produces NaN
        assertTrue(new EvaluationContext(ExpressionParser.nil).evaluate().isNaN());
    }

    @Test
    public void testEvaluateNumber()
    {
        assertTrue(123 == new EvaluationContext(ExpressionParser.parse("123 ")).evaluate());
        assertTrue(-123 == new EvaluationContext(ExpressionParser.parse(" -123 ")).evaluate());
    }

    @Test
    public void testEvaluateAddition()
    {
        ExpressionParser.Expression theExpression = ExpressionParser.parse("123 + 456 ");
        assertNotNull(theExpression);

        assertTrue(579 == new EvaluationContext(ExpressionParser.parse("123 + 456 ")).evaluate());

        assertTrue(333 == new EvaluationContext(ExpressionParser.parse("-123 + 456")).evaluate());

        assertTrue(-579 == new EvaluationContext(ExpressionParser.parse("-123 + -456")).evaluate());

        assertTrue(15 == new EvaluationContext(ExpressionParser.parse(" 1 + 2 + 3 + 4 + 5 ")).evaluate());
    }

    @Test
    public void testEvaluateSubtraction()
    {
        assertTrue(333 == new EvaluationContext(ExpressionParser.parse(" 456 - 123 ")).evaluate());

        assertTrue(-579 == new EvaluationContext(ExpressionParser.parse(" -456 - 123")).evaluate());

        assertTrue(-333 == new EvaluationContext(ExpressionParser.parse(" -456 - -123 ")).evaluate());

        assertTrue(-5 == new EvaluationContext(ExpressionParser.parse(" 5 - 4 - 3 - 2 - 1 ")).evaluate());
    }

    @Test
    public void testEvaluateMultiplication()
    {
        assertTrue(12 == new EvaluationContext(ExpressionParser.parse("3 * 4 ")).evaluate());

        assertTrue(-12 == new EvaluationContext(ExpressionParser.parse("-3 * 4")).evaluate());

        assertTrue(12 == new EvaluationContext(ExpressionParser.parse("-3 * -4")).evaluate());

        assertTrue(120 == new EvaluationContext(ExpressionParser.parse("1 * 2 * 3 * 4 * 5 ")).evaluate());
    }

    @Test
    public void testEvaluateDivision()
    {
        assertTrue(3 == new EvaluationContext(ExpressionParser.parse("12 / 4 ")).evaluate());

        assertTrue(-3 == new EvaluationContext(ExpressionParser.parse("-12 / 4")).evaluate());

        assertTrue(3 == new EvaluationContext(ExpressionParser.parse("-12 / -4")).evaluate());

        assertTrue(1 == new EvaluationContext(ExpressionParser.parse("24 / 4 / 3 / 2 / 1 ")).evaluate());
    }

    @Test
    public void testEvaluateExponentiation()
    {
        assertTrue(8 == new EvaluationContext(ExpressionParser.parse("2.0^3.0")).evaluate());
        assertTrue(0.01 == new EvaluationContext(ExpressionParser.parse("10^-2")).evaluate());
        assertTrue(251 == new EvaluationContext(ExpressionParser.parse("1 + 5^2 * 10")).evaluate());
        assertTrue(1.05 == new EvaluationContext(ExpressionParser.parse("1 + 10^-2 * 5")).evaluate());
    }

    @Test
    public void testEvaluateParenthesis()
    {
        assertTrue(100 == new EvaluationContext(ExpressionParser.parse("(100) ")).evaluate());

        assertTrue(-100 == new EvaluationContext(ExpressionParser.parse("-(100)")).evaluate());

        assertTrue(-9 == new EvaluationContext(ExpressionParser.parse("1-2*(3+4/2)")).evaluate());

        assertTrue(-110 == new EvaluationContext(ExpressionParser.parse("-(((10 + 5) * 6) - 20 / 2 * 3 + 100 - 50) ")).evaluate());
    }

    @Test
    public void testEvaluateParenthesisWithNegativeNumber()
    {
        assertTrue(-270 == new EvaluationContext(ExpressionParser.parse(" (((10 + 5) * -6) - -20 / -2 * 3 + -100 - 50)")).evaluate());
    }

}
