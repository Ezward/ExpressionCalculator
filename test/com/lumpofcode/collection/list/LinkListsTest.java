package com.lumpofcode.collection.list;

import org.junit.Test;

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


    @Test
    public void testCombineAppend()
    {
        final LinkList<LinkList<Character>> accumulator = LinkLists.linkList(
            LinkLists.linkList('a', 'b', 'c'), LinkLists.linkList('d', 'e', 'f'));
        final LinkList<Character> list = LinkLists.linkList('1', '2');

        final LinkList<LinkList<Character>> combinations = LinkLists.combineAppend(accumulator, list);

        for(LinkList<Character> combination : new IterableLinkList<>(combinations))
        {
            System.out.println(combination.toString());
        }

        final LinkList correct = LinkList.Nil
            .append("[a:b:c:1]")
            .append("[a:b:c:2]")
            .append("[d:e:f:1]")
            .append("[d:e:f:2]");

        assertEquals("There should be 4 combinations.", 4, combinations.size());
        for(LinkList<Character> combination : new IterableLinkList<>(combinations))
        {
            assertTrue("Each combination should be in the output", correct.find(combination.toString()).isNotEmpty());
        }
    }

    @Test
    public void testCombineElements()
    {
        // so combine([a, b], [c, d], [e, f]) produces 2^3 = 8 combinations
        // [[a, c, e], [a, c, f], [a, d, e], [a, d, f], [b, c, e], [b, c, f], [b, d, e] [b, d, f]]

        final LinkList<LinkList<Character>> lists = LinkLists.linkList(
            LinkLists.linkList('a', 'b'), LinkLists.linkList('c', 'd'), LinkLists.linkList('e', 'f'));

        final LinkList<LinkList<Character>> combinations = LinkLists.combinations(lists);

        for(LinkList<Character> combination : new IterableLinkList<>(combinations))
        {
            System.out.println(combination.toString());
        }

        final LinkList correct = LinkList.Nil
            .append("[a:c:e]")
            .append("[a:c:f]")
            .append("[a:d:e]")
            .append("[a:d:f]")
            .append("[b:c:e]")
            .append("[b:c:f]")
            .append("[b:d:e]")
            .append("[b:d:f]");

        assertEquals("There should be 8 combinations.", 8, combinations.size());
        for(LinkList<Character> combination : new IterableLinkList<>(combinations))
        {
            assertTrue("Each combination should be in the output", correct.find(combination.toString()).isNotEmpty());
        }


    }
}
