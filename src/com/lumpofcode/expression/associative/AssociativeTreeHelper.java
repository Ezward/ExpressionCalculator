package com.lumpofcode.expression.associative;

import com.lumpofcode.collection.list.LinkList;
import com.lumpofcode.collection.list.LinkLists;
import com.lumpofcode.utils.NumberFormatter;

import java.util.HashSet;
import java.util.Set;

import static com.lumpofcode.expression.associative.AssociativeExpressionEvaluator.parse;

/**
 * Created by Ed Murphy on 12/24/2016.
 */
public class AssociativeTreeHelper
{
    /**
     * Given an expression, generate all equivalent expressions based on
     * the rules of commutivity.
     *
     * @param theExpressionText
     * @param formatter
     * @return
     */
    public static Set<String> generateCommutedExpressions(
            final String theExpressionText,
            final NumberFormatter formatter)
    {
        final AssociativeExpressionEvaluator.Expression expression = parse(theExpressionText);
        final Set<String> result = new HashSet<>();

        if(expression instanceof AssociativeExpressionEvaluator.ParenthesisExpression)
        {
            final AssociativeExpressionEvaluator.ParenthesisExpression parenthesisExpression = (AssociativeExpressionEvaluator.ParenthesisExpression)expression;
            final Set<String> innerExpressions = generateCommutedExpressions(parenthesisExpression.innerExpression().format(), formatter);

            //
            // the result is the commuted expressions inside parenthesis
            //
            for(String s : innerExpressions)
            {
                result.add("(" + s + ")");
            }
        }
        else if((expression instanceof AssociativeExpressionEvaluator.MultiplicationExpression)
            || (expression instanceof AssociativeExpressionEvaluator.AdditionExpression))
        {
            /*
                Here is a simple example, 1 * 2 * 3.  This has factorial(3) = 6 permutations
                1 * 2 * 3
                1 * 3 * 2
                2 * 1 * 3
                2 * 3 * 1
                3 * 1 * 2
                3 * 2 * 1

                We want to generate all the unique permutatinos of the operands and return them.
            */


            //
            // we can commute around multiplication and addition
            //
            final AssociativeExpressionEvaluator.ChainedExpression chainedExpression = (AssociativeExpressionEvaluator.ChainedExpression)expression;


            // 1. Get all possible permutations of the operands
            final Set<LinkList<AssociativeExpressionEvaluator.Expression>> permutedOperands = LinkLists.permutations(chainedExpression.operands());

            //
            // 2. convert to string and add to result
            //
            for(LinkList<AssociativeExpressionEvaluator.Expression> operands : permutedOperands)
            {
                result.add(AssociativeTreeHelper.formatOperands(operands, chainedExpression.operator(), formatter));
            }
        }
        else
        {
            //
            // expression is not commutable (number, subtraction or division), return as is
            //
            result.add(expression.format());
        }

        return result;
    }

    /**
     * splice the insertion text into the given string, replacing the given character range.
     *
     * @param theSourceText the string to modify
     * @param leftIndex the start of the range to replace
     * @param rightIndex the end of the range to replace
     * @param theInsertText the text to insert
     * @return
     */
    private static String spliceResult(final String theSourceText, final int leftIndex, final int rightIndex, final String theInsertText)
    {
        final String theResultExpressionText =
                theSourceText.substring(0, leftIndex)
                + theInsertText
                + theSourceText.substring(rightIndex);

        //
        // parse and reformat in a regular way
        //
        return parse(theResultExpressionText).format();
    }

    /**
     * format chained operands into a string expression.
     *
     * @param operands
     * @param operator
     * @param formatter
     * @return
     */
    private static String formatOperands(LinkList<AssociativeExpressionEvaluator.Expression> operands, final String operator, final NumberFormatter formatter)
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(operands.head.format()); // leftmost operand
        for(operands = operands.tail; operands.isNotEmpty(); operands = operands.tail)
        {
            // operator
            builder.append(' ').append(operator).append(' ');

            // operand
            builder.append(operands.head.format());
        }

        return builder.toString();
    }

}
