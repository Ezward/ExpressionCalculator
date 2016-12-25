package com.lumpofcode.expression.associative;

import com.lumpofcode.collection.list.LinkList;
import com.lumpofcode.collection.list.LinkLists;
import com.lumpofcode.collection.tuple.Tuple2;
import com.lumpofcode.utils.NumberFormatter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
        return generateCommutedExpressions(theExpressionText, formatter, new HashSet<>());
    }

    public static Set<String> generateCommutedExpressions(
            final String theExpressionText,
            final NumberFormatter formatter,
            final Set<String> accumulator)
    {
        final AssociativeExpressionEvaluator.Expression expression = parse(theExpressionText);

        if(expression instanceof AssociativeExpressionEvaluator.ParenthesisExpression)
        {
            final AssociativeExpressionEvaluator.ParenthesisExpression parenthesisExpression = (AssociativeExpressionEvaluator.ParenthesisExpression)expression;
            final Set<String> innerExpressions = generateCommutedExpressions(parenthesisExpression.format(), formatter, new HashSet<>());

            //
            // the result is the commuted expressions inside parenthesis
            //
            final Set<String> result = new HashSet<>(accumulator); // copy
            for(String s : innerExpressions)
            {
                result.add("(" + s + ")");
            }

            return result;
        }

		/*
		    Here is a simple example, 1 * 2 * 3.  This has factorial(3) = 6 permutations
		    1 * 2 * 3
		    1 * 3 * 2
		    2 * 1 * 3
		    2 * 3 * 1
		    3 * 1 * 2
		    3 * 2 * 1

		    The initial expression parses to
		    - MultiplicationExpression(1 * 2 * 3)
		      - NumberExpression(1)
		      - RightExpression(* 2 * 3)
		        - *
		        - NumberExpression(2)
		        - RightExpression(* 3)
		          - *
		          - NumberExpression(3)
		          - null

		    How about 1 * 2 * (3 + 4).  Again, there are factorial(3) = 6 permutations, now with (3 + 4) as one of the operarands.
		    1 * 2 * (3 + 4)
		    1 * (3 + 4) * 2
		    2 * 1 * (3 + 4)
		    2 * (3 + 4) * 1
		    (3 + 4) * 1 * 2
		    (3 + 4) * 2 * 1

			1 * 2 * (3 + 4) parses to:
		    - MultiplicationExpression(1 * 2 * (3 + 4))
		      - NumberExpression(1)
		      - RightExpression(* 2 * (3 + 4))
		        - *
		        - NumberExpression(2)
		        - RightExpression(* (3 + 4))
		          - *
		          - AdditionExpression(3 + 4)
		            - NumberExpression(3)
		            - RightExpression(+ 4)
		              - +
		              - NumberExpression(4)
		              - null
		          - null


		    We need to generate permutations of the 3 operands connected with the operation.
		    In addition, we want to generate permutations within each operator (if the operand is a commutable chained expression)

		 */

        if((expression instanceof AssociativeExpressionEvaluator.MultiplicationExpression)
            || (expression instanceof AssociativeExpressionEvaluator.MultiplicationExpression))
        {
            //
            // we can commute around multiplication and addition
            //
            final AssociativeExpressionEvaluator.ChainedExpression chainedExpression = (AssociativeExpressionEvaluator.ChainedExpression)expression;

            // TODO: we have a type issue in the evaluator; here we need to use Object and case.  This must be fixed.  Don't put ? extends Expression is puplic return types


            //
            // get all the possible permutations of the operands
            //
            final Set permutations
                    = chainedExpression.operands().permutations();

            //
            // add all purmuations to the accumulator
            //
            Set<String> result = new HashSet<>(accumulator);
            for(Object permutation : permutations)
            {
                LinkList<AssociativeExpressionEvaluator.Expression> operands = (LinkList<AssociativeExpressionEvaluator.Expression>)permutation;

                final StringBuilder builder = new StringBuilder();
                builder.append(operands.head.format());
                for(operands = operands.tail; operands.isNotEmpty(); operands = operands.tail)
                {
                    final AssociativeExpressionEvaluator.Expression operand = operands.head;
                    builder.append(operand.format()).append(' ').append(chainedExpression.operator()).append(' ');
                }
                result.add(builder.toString());
            }

            return result;
        }

        //
        // expression is not commutable (number, subtraction or division), return as is
        //
        Set<String> result = new HashSet<>(accumulator);
        result.add(expression.format());
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


}
