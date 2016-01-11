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
        while(!this.isEvaluationStackEmpty())
        {
            this.evaluateStep();
        }

        //
        // the value stack should have one value to return.
        //
        return popResult();
    }

    /**
     * Pop the result value from the top of the value stack
     * and return it. When the evaluation stack becomes empty, then the value
     * stack should have a single value on it, which is the
     * result.  If the stack is empty or has more than one value
     * on it, then the evaluation is in error and an exception
     * is thrown.
     *
     * @return if the evaluation completed normally, the result of the evaluation.
     * @throws IllegalStateException if the evaluation stack is not empty, so not all steps
     *         in the evaluation have been completed.
     *         IllegalStateException if the value stack does not contain exactly one value.
     */
    public Double popResult()
    {
        if(!this.isEvaluationStackEmpty()) throw new IllegalStateException("Evaluation is not complete.");

        //
        // the value stack should have one value to return.
        //
        final Double theValue = this.popValue();
        if(!this.isValueStackEmpty()) throw new IllegalStateException("Evaluation did not clean up the value stack.");

        return theValue;
    }

    /**
     * Get a copy of the value stack for debugging purposes.
     *
     * @return the value stack
     */
    public LinkList<Double> values()
    {
        return thisValueStack;
    }

    /**
     * Get a copy of the evaluation stack for debugging purposes.
     *
     * @return the evaluation stack.
     */
    public LinkList<EvaluationStep> steps()
    {
        return thisEvaluationStack;
    }

    /**
     * Peek at the value on the top of the value stack.
     * This throws if the stack is empty, so you should
     * first call isValueStackEmpty().
     *
     * @return the value at the top of the value stack
     *         or IllegalStateException if the stack
     *         is empty.
     */
     /* package private */ Double peekValue()
    {
        if(thisValueStack.isEmpty()) throw new IllegalStateException("Attempt to pop from the value stack when the stack is empty.");

        return thisValueStack.head;
    }

    /**
     * Push a value onto the value stack.
     *
     * @param theValue
     * @throws IllegalStateException the value stack is empty.
     */
    /* package private */ void pushValue(final Double theValue)
    {
        thisValueStack = thisValueStack.insert(theValue);
    }

    /**
     * Pop a value from the value stack.
     *
     * @return the value from the top of the value stack.
     * @throws IllegalStateException the value stack is empty.
     */
    /* package private */ Double popValue()
    {
        final Double theValue = peekValue();
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
     * Run one step in the evaluation.
     * If the evaluation stack is empty, this
     * will throw, so you should call isEvaluationStackEmpty()
     * first.  Once the evaluation stack is empty, the value stack
     * should have a single value which is the result.
     *
     * @throws IllegalStateException if the evaluation stack is empty.
     */
    public void evaluateStep()
    {
        this.popStep().step(this);
    }

    /**
     * Peek at the next step in the evaluation.
     * This will throw if the evaluation stack is empty,
     * so you should call isEvaluationStackEmpty() first.
     * Once the evaluation stack becomes empty, there
     * should be a single value on the stack, which
     * is the result of the evaluation.
     *
     * @return the next step in the evaluation
     * @throws IllegalStateException the evaluation stack is empty.
     */
     /* package private */ EvaluationStep peekStep()
    {
        if(thisEvaluationStack.isEmpty()) throw new IllegalStateException("Attempt to pop from the evaluation stack when the  stack is empty.");

        return thisEvaluationStack.head;
    }

    /**
     * Push a step onto the evaluation stack.
     *
     * @param theEvaluationStep
     */
    /* package private */ void pushStep(final EvaluationStep theEvaluationStep)
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
     * @throws RuntimeException the evaluation stack is empty.
     */
    /* package private */ EvaluationStep popStep()
    {
        final EvaluationStep theExpression = peekStep();
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
