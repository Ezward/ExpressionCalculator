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
        if("^".equals(theOperator)) return Exponentiation;
        throw new IllegalStateException("Invalid binary operator ($operator).".replace("$operator", (null != theOperator) ? theOperator : "null"));
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
        throw new IllegalStateException("Invalid unary operator ($operator).".replace("$operator", (null != theOperator) ? theOperator : "null"));
    }

    /**
     * Singleton for an addition operation
     */
    public static final EvaluationStep Addition = new EvaluationStep()
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
    public static final EvaluationStep Subtraction = new EvaluationStep()
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
    public static final EvaluationStep Mulitplication = new EvaluationStep()
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
    public static final EvaluationStep Division = new EvaluationStep()
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
     * Singleton to raise a value to a power (exponentiation)
     */
    public static final EvaluationStep Exponentiation = new EvaluationStep()
    {
        @Override
        public void step(EvaluationContext theContext)
        {
            //
            // pop the top 2 values and do a power calculation
            // and push the result onto the value stack
            //
            final Double theSecondOperand = theContext.popValue();
            final Double theFirstOperand = theContext.popValue();
            theContext.pushValue(Math.pow(theFirstOperand, theSecondOperand));
        }
    };

    /**
     * Singleton for a unary negation operation
     */
    public static final EvaluationStep Negation = new EvaluationStep()
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
    public static final EvaluationStep NoOp = new EvaluationStep()
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
