package com.lumpofcode.expression;

import com.lumpofcode.collection.list.LinkList;

/**
 * Context for step-wise evaluation of an ExpressionParser.Expression
 * (the result of ExpressionParser.parse()).
 *
 * The expression to be evaluated is passed to the constructor.
 * Each step is accomplished by popping the top of the evaluation stack,
 * and then processing that step.  The EvaluationStep itself is passed
 * the context so it can push it results onto the evaluation stack and/or
 * the value stack appropriately.  When the evaluation stack is empty,
 * then the evaluation is completed and the value stack should have
 * a single entry which is the result.
 *
 * Created by emurphy on 5/20/15.
 */
public final class EvaluationContext
{
    private LinkList<EvaluationStep> thisEvaluationStack = LinkList.Nil;
    private LinkList<Double> thisValueStack = LinkList.Nil;

    public EvaluationContext(final EvaluationStep expression)
    {
        // push initial expression onto the stack
        thisEvaluationStack = thisEvaluationStack.insert(expression);
    }

    /**
     * Push a value onto the value stack.
     *
     * @param theValue
     */
    public void pushValue(final Double theValue)
    {
        thisValueStack = thisValueStack.insert(theValue);
    }

    /**
     * Pop a value from the value stack.
     *
     * @return the value from the top of the value stack.
     */
    public Double popValue()
    {
        if(thisValueStack.isEmpty()) throw new RuntimeException("Attempt to pop from the value stack when the stack is empty.");

        final Double theValue = thisValueStack.head;
        thisValueStack = thisValueStack.tail;
        return theValue;
    }

    /**
     * Determine if the value stack is empty.
     *
     * @return true if value stack is empty, false otherwise.
     */
    public boolean isValueStackEmpty()
    {
        return thisValueStack.isEmpty();
    }

    /**
     * Push a step onto the evaluation stack.
     *
     * @param theEvaluationStep
     */
    public void pushEvaluationStep(final EvaluationStep theEvaluationStep)
    {
        //
        // don't bother pushing noops
        //
        if(EvaluationOperation.NoOp != theEvaluationStep)
        {
            thisEvaluationStack = thisEvaluationStack.insert(theEvaluationStep);
        }
    }

    /**
     * Pop a step from the evaluation stack in order
     * for it to be processed.
     *
     * @return the EvaluationStep from the top of the stack.
     */
    public EvaluationStep popEvaluationStep()
    {
        if(thisEvaluationStack.isEmpty()) throw new RuntimeException("Attempt to pop from the evaluation stack when the  stack is empty.");

        final EvaluationStep theExpression = thisEvaluationStack.head;
        thisEvaluationStack = thisEvaluationStack.tail;
        return theExpression;
    }

    /**
     * Determine if the evaluation stack is empty.
     * If it is empty after processing a popped EvaluationStep,
     * then the evaluation is complete.
     *
     * @return true if the evaluation is complete, false if not.
     */
    public boolean isEvaluationStackEmpty()
    {
        return thisEvaluationStack.isEmpty();
    }
}
