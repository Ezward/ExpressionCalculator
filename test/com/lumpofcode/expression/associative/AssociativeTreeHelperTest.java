package com.lumpofcode.expression.associative;

import com.lumpofcode.collection.list.LinkList;
import com.lumpofcode.utils.IntegerTruncateFormatter;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ed Murphy on 12/24/2016.
 */
public class AssociativeTreeHelperTest
{
    @Test
    public void generateCommutedAdditionTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 + 3 + 4", new IntegerTruncateFormatter());

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 6 commutations.", 6, commutations.size());
        assertTrue("2 + 3 + 4 should be generated", commutations.find("2 + 3 + 4").isNotEmpty());
        assertTrue("2 + 4 + 3 should be generated", commutations.find("2 + 4 + 3").isNotEmpty());
        assertTrue("3 + 2 + 4 should be generated", commutations.find("3 + 2 + 4").isNotEmpty());
        assertTrue("3 + 4 + 2 should be generated", commutations.find("3 + 4 + 2").isNotEmpty());
        assertTrue("4 + 2 + 3 should be generated", commutations.find("4 + 2 + 3").isNotEmpty());
        assertTrue("4 + 3 + 2 should be generated", commutations.find("4 + 3 + 2").isNotEmpty());

    }

    @Test
    public void generateCommutedMultiplicationTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 * 3 * 4", new IntegerTruncateFormatter());

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 2 commutations.", 6, commutations.size());
        assertTrue("2 * 3 * 4 should be generated", commutations.find("2 * 3 * 4").isNotEmpty());
        assertTrue("2 * 4 * 3 should be generated", commutations.find("2 * 4 * 3").isNotEmpty());
        assertTrue("3 * 2 * 4 should be generated", commutations.find("3 * 2 * 4").isNotEmpty());
        assertTrue("3 * 4 * 2 should be generated", commutations.find("3 * 4 * 2").isNotEmpty());
        assertTrue("4 * 2 * 3 should be generated", commutations.find("4 * 2 * 3").isNotEmpty());
        assertTrue("4 * 3 * 2 should be generated", commutations.find("4 * 3 * 2").isNotEmpty());

    }

    @Test
    public void generateCommutedMixedExpressionsTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 * 3 + 4 * 5", new IntegerTruncateFormatter());

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 8 commutations.", 8, commutations.size());
        assertTrue("2 * 3 + 4 * 5 should be generated", commutations.find("2 * 3 + 4 * 5").isNotEmpty());
        assertTrue("2 * 3 + 5 * 4 should be generated", commutations.find("2 * 3 + 5 * 4").isNotEmpty());
        assertTrue("3 * 2 + 4 * 5 should be generated", commutations.find("3 * 2 + 4 * 5").isNotEmpty());
        assertTrue("3 * 2 + 5 * 4 should be generated", commutations.find("3 * 2 + 5 * 4").isNotEmpty());
        assertTrue("4 * 5 + 2 * 3 should be generated", commutations.find("4 * 5 + 2 * 3").isNotEmpty());
        assertTrue("4 * 5 + 3 * 2 should be generated", commutations.find("4 * 5 + 3 * 2").isNotEmpty());
        assertTrue("5 * 4 + 2 * 3 should be generated", commutations.find("5 * 4 + 2 * 3").isNotEmpty());
        assertTrue("5 * 4 + 3 * 2 should be generated", commutations.find("5 * 4 + 3 * 2").isNotEmpty());

    }

    @Test
    public void generateCommutedParenthesisExpressionsTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 * (3 + 4) * 5", new IntegerTruncateFormatter());

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 12 commutations.", 12, commutations.size());
        assertTrue("2 * (3 + 4) * 5 should be generated", commutations.find("2 * (3 + 4) * 5").isNotEmpty());
        assertTrue("2 * 5 * (3 + 4) should be generated", commutations.find("2 * 5 * (3 + 4)").isNotEmpty());
        assertTrue("(3 + 4) * 2 * 5 should be generated", commutations.find("(3 + 4) * 2 * 5").isNotEmpty());
        assertTrue("(3 + 4) * 5 * 2 should be generated", commutations.find("(3 + 4) * 5 * 2").isNotEmpty());
        assertTrue("5 * (3 + 4) * 2 should be generated", commutations.find("5 * (3 + 4) * 2").isNotEmpty());
        assertTrue("5 * 2 * (3 + 4) should be generated", commutations.find("5 * 2 * (3 + 4)").isNotEmpty());
        assertTrue("2 * (4 + 3) * 5 should be generated", commutations.find("2 * (4 + 3) * 5").isNotEmpty());
        assertTrue("2 * 5 * (4 + 3) should be generated", commutations.find("2 * 5 * (4 + 3)").isNotEmpty());
        assertTrue("(4 + 3) * 2 * 5 should be generated", commutations.find("(4 + 3) * 2 * 5").isNotEmpty());
        assertTrue("(4 + 3) * 5 * 2 should be generated", commutations.find("(4 + 3) * 5 * 2").isNotEmpty());
        assertTrue("5 * (4 + 3) * 2 should be generated", commutations.find("5 * (4 + 3) * 2").isNotEmpty());
        assertTrue("5 * 2 * (4 + 3) should be generated", commutations.find("5 * 2 * (4 + 3)").isNotEmpty());
    }

    @Test
    public void generateCommutedSubtractionTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 - 3", new IntegerTruncateFormatter());

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 1 commutations.", 1, commutations.size());
        assertTrue("2 - 3 should be generated", commutations.find("2 - 3").isNotEmpty());
    }

    @Test
    public void generateCommutedDivisionTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 / 3", new IntegerTruncateFormatter());

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 1 commutations.", 1, commutations.size());
        assertTrue("2 / 3 should be generated", commutations.find("2 / 3").isNotEmpty());
    }


    //
    // students may or may not parenthesize their expression, so the most general way to check
    // for an equivalent expression is to fully parenthesize the target (correct expression) and
    // the student's expression, then generate all equivalent target expressions and check
    // that the student's expression is one of them.
    // NOTE: this can still fail if the student has parenthesized in a non-standard way.
    //
    @Test
    public void testAreExpressionsEquivalent()
    {
        assertTrue("1 and 1 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1", "1"));
        assertTrue("1 + 2 and 2 + 1 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 + 2", "2 + 1"));
        assertTrue("1 * 2 and 2 * 1 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 + 2", "2 + 1"));
        assertFalse("1 - 2 and 2 - 1 are NOT equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 - 2", "2 - 1"));
        assertFalse("1 / 2 and 2 / 1 are NOT equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 / 2", "2 / 1"));
        assertTrue("(1 + 2) * 3 + 4 and (4 + 3 * (2 + 1)) are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("(1 + 2) * 3 + 4", "(4 + 3 * (2 + 1))"));

        // TODO: write code that can handle unnecessary parenthesis.
        // assertTrue("1 + 2 + 3 + 4 and 1 + (2 + 3) + 4 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 + 2 + 3 + 4", "1 + (2 + 3) + 4"));
    }

}
