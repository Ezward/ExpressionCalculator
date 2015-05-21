package com.lumpofcode;

import com.lumpofcode.expression.ExpressionParser;

public class Main {

    public static void main(String[] args)
    {
        final String theInput = String.join(" ", args);
        System.out.println(theInput);

	    //
        // parse the expression given on the command line and print the evaluation
        //
        ExpressionParser.Expression theExpression = ExpressionParser.parse(theInput);

        System.out.print(theExpression.format());
        System.out.print(" = ");
        System.out.println(theExpression.evaluate());
    }
}
