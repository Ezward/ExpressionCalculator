package com.lumpofcode.expression;

/**
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
    abstract void step(final EvaluationContext theContext);
}
