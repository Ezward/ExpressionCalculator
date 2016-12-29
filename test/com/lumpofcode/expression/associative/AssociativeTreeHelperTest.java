package com.lumpofcode.expression.associative;

import com.lumpofcode.collection.list.LinkList;
import com.lumpofcode.utils.IntegerTruncateFormatter;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
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
//        assertTrue("2 * 3 + 4 should be generated", commutations.contains("2 * 3 * 4"));
//        assertTrue("2 * 4 * 3 should be generated", commutations.contains("2 * 4 * 3"));
//        assertTrue("3 * 2 * 4 should be generated", commutations.contains("3 * 2 * 4"));
//        assertTrue("3 * 4 * 2 should be generated", commutations.contains("3 * 4 * 2"));
//        assertTrue("4 * 2 * 3 should be generated", commutations.contains("4 * 2 * 3"));
//        assertTrue("4 * 3 * 2 should be generated", commutations.contains("4 * 3 * 2"));

    }
}