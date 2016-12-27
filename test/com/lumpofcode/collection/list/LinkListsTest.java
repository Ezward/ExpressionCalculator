package com.lumpofcode.collection.list;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ed Murphy on 12/24/2016.
 */
public class LinkListsTest
{
    @Test
    public void testPermutations()
    {
        final LinkList<String> list = LinkLists.linkList("A", "B", "C", "D");

        final LinkList<LinkList<String>> permutations = LinkLists.permutations(list);

        for(LinkList<LinkList<String>> permutation = permutations; permutation.isNotEmpty(); permutation = permutation.tail)
        {
            System.out.println(permutation.head.toString());
        }

        assertEquals("there should be 24 permutations", 24, permutations.size());
        
        LinkList<String> values = LinkList.Nil
                .insert("[A:B:C:D]")
                .insert("[A:B:D:C]")
                .insert("[A:C:B:D]")
                .insert("[A:C:D:B]")
                .insert("[A:D:B:C]")
                .insert("[A:D:C:B]")

                .insert("[B:A:C:D]")
                .insert("[B:A:D:C]")
                .insert("[B:C:A:D]")
                .insert("[B:C:D:A]")
                .insert("[B:D:A:C]")
                .insert("[B:D:C:A]")

                .insert("[C:A:B:D]")
                .insert("[C:A:D:B]")
                .insert("[C:B:A:D]")
                .insert("[C:B:D:A]")
                .insert("[C:D:A:B]")
                .insert("[C:D:B:A]")

                .insert("[D:A:B:C]")
                .insert("[D:A:C:B]")
                .insert("[D:B:A:C]")
                .insert("[D:B:C:A]")
                .insert("[D:C:A:B]")
                .insert("[D:C:B:A]");

        for(LinkList<LinkList<String>> permutation = permutations; permutation.isNotEmpty(); permutation = permutation.tail)
        {
            LinkList<String> first = values.find(permutation.head.toString());
            assertTrue("There should be one entry in values for each permutation.", first.isNotEmpty());

            LinkList<String> second = first.tail.find(permutation.head.toString());
            assertTrue("There should NOT be more than one entry in values for each permutation.", second.isEmpty());
        }
    }
}
