package com.lumpofcode.expression;

import com.lumpofcode.collection.list.LinkList;

/**
 * Created by emurphy on 5/20/15.
 */
public class EvaluationContext
{
    LinkList<EvaluationStep> thisEvaluationStack = LinkList.Nil;
    LinkList<Double> thisValueStack = LinkList.Nil;

    public EvaluationContext(final EvaluationStep expression)
    {
        // push initial expression onto the stack
        thisEvaluationStack = thisEvaluationStack.insert(expression);
    }

    public void pushValue(final Double theValue)
    {
        thisValueStack = thisValueStack.insert(theValue);
    }
    public Double popValue()
    {
        final Double theValue = thisValueStack.head;
        thisValueStack = thisValueStack.tail;
        return theValue;
    }
    public boolean isValueStackEmpty()
    {
        return thisValueStack.isEmpty();
    }

    public void pushStep(final EvaluationStep theExpression)
    {
        //
        // don't bother pushing noops
        //
        if(EvaluationOperation.NoOp != theExpression)
        {
            thisEvaluationStack = thisEvaluationStack.insert(theExpression);
        }
    }
    public EvaluationStep popStep()
    {
        final EvaluationStep theExpression = thisEvaluationStack.head;
        thisEvaluationStack = thisEvaluationStack.tail;
        return theExpression;
    }
    public boolean isEvaluationComplete()
    {
        return thisEvaluationStack.isEmpty();
    }
}
