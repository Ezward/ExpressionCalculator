package com.lumpofcode.expression.associative;

import com.lumpofcode.collection.list.LinkList;
import com.lumpofcode.collection.list.LinkLists;
import com.lumpofcode.expression.ExpressionParser;
import com.lumpofcode.expression.ExpressionTreeHelper;
import com.lumpofcode.utils.NumberFormatter;

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
     * @return
     */
    public static LinkList<String> generateCommutedExpressions(
            final String theExpressionText)
    {
        final AssociativeExpressionEvaluator.Expression expression = parse(theExpressionText);
        LinkList<String> result = LinkList.Nil;

        if(expression instanceof AssociativeExpressionEvaluator.ParenthesisExpression)
        {
            final AssociativeExpressionEvaluator.ParenthesisExpression parenthesisExpression = (AssociativeExpressionEvaluator.ParenthesisExpression)expression;
            final LinkList<String> innerExpressions = generateCommutedExpressions(parenthesisExpression.innerExpression().format());

            //
            // the result is the commuted expressions inside parenthesis
            //
            for(LinkList e = innerExpressions; e.isNotEmpty(); e = e.tail)
            {
                result = result.append("(" + e.head + ")");
            }
        }
        else if((expression instanceof AssociativeExpressionEvaluator.MultiplicationExpression)
            || (expression instanceof AssociativeExpressionEvaluator.AdditionExpression))
        {
            //
            // we can commute around multiplication and addition
            //
            final AssociativeExpressionEvaluator.ChainedExpression chainedExpression = (AssociativeExpressionEvaluator.ChainedExpression)expression;

            /*
                Here is a simple example, 1 * 2 * 3.  This has factorial(3) = 6 permutations
                1 * 2 * 3
                1 * 3 * 2
                2 * 1 * 3
                2 * 3 * 1
                3 * 1 * 2
                3 * 2 * 1

                We want to generate all the unique permutations of the operands and return them.
            */
    
    
            //
            // 1. recursively get permutations of each operand
            //
            // for 2 * 3 + 4 * 5 we end up with a list of two sets permuted operands
            //     [["2 * 3", "3 * 2"], ["4 * 5", "5 * 4"]]
            // the first element of the list is the list of the first operand permutations
            // the second element of the list is the list of the second operand permutations
            //
            LinkList<LinkList<String>> permutedOperands = LinkList.Nil;
            for(LinkList<AssociativeExpressionEvaluator.Expression> operand = chainedExpression.operands(); operand.isNotEmpty(); operand = operand.tail)
            {
                permutedOperands = permutedOperands.append(generateCommutedExpressions(operand.head.format()));
            }
    
            //
            // 2. Get all possible permutations of the operands
            //
            // for the example, input is [["2 * 3", "3 * 2"], ["4 * 5", "5 * 4"]]
            // the result is a set of two linked lists each with a set of two strings;
            //     [[["2 * 3", "3 * 2"], ["4 * 5", "5 * 4"]], [["4 * 5", "5 * 4"], ["2 * 3", "3 * 2"]]]
            //
            LinkList<LinkList<LinkList<String>>> permutedChains = LinkLists.permutations(permutedOperands);
    
            //
            // 3. combine operands to produce all possible combinations of permuted operands
            //    we do this in each set link list and the output is a linked list of operands
            //    so for  [["2 * 3", "3 * 2"], ["4 * 5", "5 * 4"]]
            //    we get  [["2 * 3", "4 * 5"], ["2 * 3", "5 * 4"], ["3 * 2", "4 * 5"], ["3 * 2", "5 * 4"]]
            //    and for [["4 * 5", "5 * 4"], ["2 * 3", "3 * 2"]]
            //    we get  [["4 * 5", "2 * 3"], ["4 * 5", "3 * 2"], ["5 * 4", "2 * 3"], ["5 * 4", "3 * 2"]]
            //
            while(permutedChains.isNotEmpty())
            {
                //
                // each element contains a set of permutations for that operand.
                // they must be combined with all the other oprands to produce
                // all possible combinations of permuted operands in all possible permuted orders
                //
                final LinkList<LinkList<String>> listOfOperandPurmutations = permutedChains.head;
                LinkList<LinkList<String>> listOfOperandCombinations =
                    LinkLists.combinations(listOfOperandPurmutations);
        
                while(listOfOperandCombinations.isNotEmpty())
                {
                    result = result.insert(
                        AssociativeTreeHelper.formatStringOperands(
                            listOfOperandCombinations.head,
                            chainedExpression.operator()));
            
                    listOfOperandCombinations = listOfOperandCombinations.tail;
                }
        
                permutedChains = permutedChains.tail;
            }
        }
        else if((expression instanceof AssociativeExpressionEvaluator.DivisionExpression)
            || (expression instanceof AssociativeExpressionEvaluator.SubtractionExpression))
        {
            //
            // we can't commute around division or subtraction, but we
            // still need to recursively commute the operands
            //
            final AssociativeExpressionEvaluator.ChainedExpression chainedExpression = (AssociativeExpressionEvaluator.ChainedExpression)expression;

            /*
                Here is a simple example, 2 * 3 - 4 * 5.  This has factorial(2) + factorial(2) = 4 permutations
                2 * 3 - 4 * 5
                2 * 3 - 5 * 4
                3 * 2 - 4 * 5
                3 * 2 - 5 * 4

                We want to generate all the unique permutations of the operands and return them.
            */
    
    
            //
            // 1. recursively get permutations of each operand
            //
            // for 2 * 3 - 4 * 5 we end up with a list of two sets permuted operands
            //     [["2 * 3", "3 * 2"], ["4 * 5", "5 * 4"]]
            // the first element of the list is the list of the first operand permutations
            // the second element of the list is the list of the second operand permutations
            //
            LinkList<LinkList<String>> permutedOperands = LinkList.Nil;
            for(LinkList<AssociativeExpressionEvaluator.Expression> operand = chainedExpression.operands(); operand.isNotEmpty(); operand = operand.tail)
            {
                permutedOperands = permutedOperands.append(generateCommutedExpressions(operand.head.format()));
            }
    
            //
            // 3. combine operands to produce all possible combinations of permuted operands
            //    we do this in each set link list and the output is a linked list of operands
            //    so for  [["2 * 3", "3 * 2"], ["4 * 5", "5 * 4"]]
            //    we get  [["2 * 3", "4 * 5"], ["2 * 3", "5 * 4"], ["3 * 2", "4 * 5"], ["3 * 2", "5 * 4"]]
            //
            if(permutedOperands.isNotEmpty())
            {
                //
                // each element contains a set of permutations for that operand.
                // they must be combined with all the other oprands to produce
                // all possible combinations of permuted operands in all possible permuted orders
                //
                LinkList<LinkList<String>> listOfOperandCombinations =
                    LinkLists.combinations(permutedOperands);
        
                while(listOfOperandCombinations.isNotEmpty())
                {
                    result = result.insert(
                        AssociativeTreeHelper.formatStringOperands(
                            listOfOperandCombinations.head,
                            chainedExpression.operator()));
            
                    listOfOperandCombinations = listOfOperandCombinations.tail;
                }
            }
        }
        else
        {
            //
            // expression is not commutable (number, subtraction or division), return as is
            //
            result = result.insert(expression.format());
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

    private static String formatStringOperands(LinkList<String> operands, final String operator)
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(operands.head); // leftmost operand
        for(operands = operands.tail; operands.isNotEmpty(); operands = operands.tail)
        {
            // operator
            builder.append(' ').append(operator).append(' ');

            // operand
            builder.append(operands.head);
        }

        return builder.toString();
    }

	/**
     * Check that an expression is equivalent to another expression.
     *
     *
     * @param targetExpression
     * @param checkedExpression
     * @return
     */
    public static final boolean areExpressionsEquivalent(final String targetExpression, final String checkedExpression)
    {
        //
        // students may or may not parenthesize their expression, so the most general way to check
        // for an equivalent expression is to fully parenthesize the target (correct expression) and
        // the student's expression, then generate all equivalent target expressions and check
        // that the student's expression is one of them.
        //

        //
        // 1. remove unnecessary parenthesis from  the target and check expressions
        // 2. fully parenthesize the target and checked expressions
        // 3. generate all possible equivalent target expressions
        // 4. if the checkExpression is in the permuted target expressions, it is equivalent.
        //
    
        // 1. remove unnecessary parenthesis from  the target and check expressions
        final String simpleTarget = ExpressionTreeHelper.removeParenthesis(targetExpression);
        final String simpleCheck = ExpressionTreeHelper.removeParenthesis(checkedExpression);
        
        // 2. fully parenthesize the target and checked expressions
        final String parenthesizedTarget = ExpressionParser.parse(simpleTarget).formatFullParenthesis();
        final String parenthesizedCheck = ExpressionParser.parse(simpleCheck).formatFullParenthesis();

        // 3. generate all possible equivalent target expressions
        final LinkList<String> targetExpressions = AssociativeTreeHelper.generateCommutedExpressions(parenthesizedTarget);

        // 4. if the checkExpression is in the permuted target expressions, it is equivalent.
        return targetExpressions.find(parenthesizedCheck).isNotEmpty();
    }

}
