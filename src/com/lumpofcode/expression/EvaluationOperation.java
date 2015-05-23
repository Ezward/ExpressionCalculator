package com.lumpofcode.expression;

/**
 * Class to expose operation as EvaluationSteps.
 *
 * Created by emurphy on 5/21/15.
 */
public final class EvaluationOperation
{
    private EvaluationOperation() {} // don't allow construction of an instance

    /**
     * Get the operational step for given binary operator.
     *
     * @param theOperator
     * @return the EvaluationStep for the given operator
     */
    public static EvaluationStep getBinaryOperation(final String theOperator)
    {
        if("+".equals(theOperator)) return Addition;
        if("-".equals(theOperator)) return Subtraction;
        if("*".equals(theOperator)) return Mulitplication;
        if("/".equals(theOperator)) return Division;
        throw new IllegalStateException("Invalid binary operator.");
    }

    /**
     * Get the operational step for a given unary operator.
     *
     * @param theOperator
     * @return the EvaluationStep for the given operator.
     */
    public static EvaluationStep getUnaryOperation(final String theOperator)
    {
        if("+".equals(theOperator)) return NoOp;
        if("-".equals(theOperator)) return Negation;
        throw new IllegalStateException("Invalid unary operator.");
    }

    /**
     * Singleton for an addition operation
     */
    public static EvaluationStep Addition = new EvaluationStep()
    {
        @Override
        public void step(final EvaluationContext theContext)
        {
            //
            // pop the top 2 values and add them
            // and push the result onto the value stack
            //
            final Double theSecondOperand = theContext.popValue();
            final Double theFirstOperand = theContext.popValue();
            theContext.pushValue(theFirstOperand + theSecondOperand);
        }
    };

    /**
     * Singleton for a subtraction operation
     */
    public static EvaluationStep Subtraction = new EvaluationStep()
    {
        @Override
        public void step(final EvaluationContext theContext)
        {
            //
            // pop the top 2 values and add them
            // and push the result onto the value stack
            //
            final Double theSecondOperand = theContext.popValue();
            final Double theFirstOperand = theContext.popValue();
            theContext.pushValue(theFirstOperand - theSecondOperand);
        }
    };

    /**
     * Singleton for a multiplication operation
     */
    public static EvaluationStep Mulitplication = new EvaluationStep()
    {
        @Override
        public void step(final EvaluationContext theContext)
        {
            //
            // pop the top 2 values and add them
            // and push the result onto the value stack
            //
            final Double theSecondOperand = theContext.popValue();
            final Double theFirstOperand = theContext.popValue();
            theContext.pushValue(theFirstOperand * theSecondOperand);
        }
    };

    /**
     * Singleton for a division operation
     */
    public static EvaluationStep Division = new EvaluationStep()
    {
        @Override
        public void step(final EvaluationContext theContext)
        {
            //
            // pop the top 2 values and add them
            // and push the result onto the value stack
            //
            final Double theSecondOperand = theContext.popValue();
            final Double theFirstOperand = theContext.popValue();
            theContext.pushValue(theFirstOperand / theSecondOperand);
        }
    };

    /**
     * Singletop for a unary negation operation
     */
    public static EvaluationStep Negation = new EvaluationStep()
    {
        @Override
        public void step(final EvaluationContext theContext)
        {
            //
            // pop the value, negate, push the value
            //
            final Double theOperand = theContext.popValue();
            theContext.pushValue(-theOperand);
        }
    };

    /**
     * Singleton for no operation
     */
    public static EvaluationStep NoOp = new EvaluationStep()
    {
        @Override
        public void step(final EvaluationContext theContext)
        {
            //
            // do nothing
            //
        }
    };
}
