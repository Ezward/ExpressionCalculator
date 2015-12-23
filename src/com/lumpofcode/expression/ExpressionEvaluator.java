package com.lumpofcode.expression;

/**
 * Created by emurphy on 5/20/15.
 */
public final class ExpressionEvaluator
{
    private final EvaluationContext theContext;

    public ExpressionEvaluator(final ExpressionParser.Expression theExpression)
    {
        theContext = new EvaluationContext(theExpression);
    }

    /**
     * Use stack-based, stepwise evaluation of a context
     * to calculate a number.
     *
     * @return the result value
     */
    public Double evaluate()
    {
        //
        // while the evaluation stack is not empty
        // evaluate the top of the stack
        // when the stack is empty, return the value
        // on the stack (should be the only value)
        //
        while(!theContext.isEvaluationStackEmpty())
        {
            final EvaluationStep theStep = theContext.popEvaluationStep();
            theStep.step(theContext);
        }

        //
        // the value stack should have one value to return.
        //
        final Double theValue = theContext.popValue();
        if(!theContext.isValueStackEmpty()) throw new IllegalStateException("Evaluation did not clean up the stack.");

        return theValue;
    }

}
