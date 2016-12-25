package com.lumpofcode.expression.associative;

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
    public void generateCommutedExpressionsTest()
    {
        Set<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 + 3 + 4", new IntegerTruncateFormatter());

        for(String commutation : commutations)
        {
            System.out.println(commutation);
        }

        assertEquals("There should be 2 commutations.", 6, commutations.size());
        assertTrue("2 + 3 + 4 should be generated", commutations.contains("2 + 3 + 4"));
        assertTrue("2 + 4 + 3 should be generated", commutations.contains("2 + 4 + 3"));
        assertTrue("3 + 2 + 4 should be generated", commutations.contains("3 + 2 + 4"));
        assertTrue("3 + 4 + 2 should be generated", commutations.contains("3 + 4 + 2"));
        assertTrue("4 + 2 + 3 should be generated", commutations.contains("4 + 2 + 3"));
        assertTrue("4 + 3 + 2 should be generated", commutations.contains("4 + 3 + 2"));

    }
}
