package com.lumpofcode.expression;

/**
 * A single step in the evaluation of an expression.
 *
 * This corresponds to an Expression that can be decomposed
 * into sub-Expressions and pushed onto the evaluation stack,
 * a value Expression that can be converted to it's value
 * and pushed onto the value stack, or an operation that
 * can be applied to the values on the value stack.
 *
 * Created by emurphy on 5/20/15.
 */
public interface EvaluationStep
{
    /**
     * Execute one step in the interpretation of an expression
     * using the given context.
     *
     * @param theContext
     */
    void step(final EvaluationContext theContext);
}
