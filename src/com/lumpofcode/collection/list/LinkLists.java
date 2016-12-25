package com.lumpofcode.collection.list;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Static helper methods for LinkList.
 *
 * Created by emurphy on 10/24/15.
 */
public final class LinkLists
{
    private LinkLists() {}  // don't allow instatiation

    /*
     * factory methods that take elements
     */
    public static <T> LinkList<T> linkList() { return LinkList.Nil; }
    public static <T> LinkList<T> linkList(final T x) { return new LinkList(x); }
    public static <T> LinkList<T> linkList(final T x1, final T x2) { return new LinkList(x1, new LinkList(x2)); }
    public static <T> LinkList<T> linkList(final T x1, final T x2, final T x3) { return new LinkList(x1, new LinkList(x2, new LinkList(x3))); }
    public static <T> LinkList<T> linkList(final T x1, final T x2, final T x3, final T x4) { return new LinkList(x1, new LinkList(x2, new LinkList(x3, new LinkList(x4)))); }

    /**
     * flatten nested lists into on concatenated list.
     *
     * @param theList list of lists
     * @param <T> type of element in the nested lists
     * @return concatenated list using nested list elements
     */
    public static <T> LinkList<T> flatten(LinkList<LinkList<T>> theList)
    {
        LinkList<T> theReturnList = LinkList.Nil;
        while(theList.isNotEmpty())
        {
            theReturnList = theReturnList.append(theList.head);
            theList = theList.tail;
        }
        return theReturnList;

//        if(theList == LinkList.Nil) return LinkList.Nil;
//        return theList.head.append(flatten(theList.tail));
    }


    /**
     * Map the elements of a nested lists and flatten them
     * into a concatenated list of return type elements.
     *
     * @param theList list of lists
     * @param theMapper maps T elements to R elements
     * @param <T> source element
     * @param <R> mapped element
     * @return flattened list of mapped elements.
     */
    public static <T, R> LinkList<R> flatmap(LinkList<LinkList<T>> theList, Function<T, R> theMapper)
    {
        LinkList<R> theReturnList = LinkList.Nil;
        while(theList.isNotEmpty())
        {
            theReturnList = theReturnList.append(theList.head.map(theMapper));
            theList = theList.tail;
        }
        return theReturnList;

//        if(theList == LinkList.Nil) return LinkList.Nil;
//        return theList.head.map(mapper).append(flatmap(theList.tail, theMapper));
    }

    public static <T> LinkList<T> swap(LinkList<T> list, int i, int j)
    {
        if(i == j) return list; // nothing to swap
        if(i > j) return swap(list, j, i);  // want i < j

        //
        // 1. get list up to ith element
        // 2. get ith element
        // 3. get list up to jth element
        // 4. get jth element
        // 5. get list to tail
        // 6. piece it back together, swapping ith and jth element
        //

        LinkList<T> left = LinkList.Nil;
        if(list.isNotEmpty())
        {
            // 1. get list up to ith element
            int k = 0;
            while ((k < i) && list.isNotEmpty())
            {
                left.insert(list.head);
                list = list.tail;
            }
            if (list.isNotEmpty())
            {
                // 2. get ith element
                final T ith = list.head;
                list = list.tail;

                // 3. get list up to jth element
                LinkList<T> middle = LinkList.Nil;
                while((k < j) && list.isNotEmpty())
                {
                    middle.insert(list.head);
                    list = list.tail;
                }

                if(list.isNotEmpty())
                {
                    // 4. get jth element
                    final T jth = list.head;

                    // 5. get list to tail
                    list = list.tail;

                    //
                    // 6. piece it back together, swapping ith and jth element
                    //
                    list = list.insert(ith);    // swap ith and jth
                    while(middle.isNotEmpty())
                    {
                        list = list.insert(middle.head);
                        middle = middle.tail;
                    }

                    list = list.insert(jth);    // swap ith and jth
                    while(left.isNotEmpty())
                    {
                        list = list.insert(left.head);
                        left = left.tail;
                    }

                    return list;
                }
            }
        }

        //
        // if we got here, i or j is out of range
        //
        throw new IndexOutOfBoundsException("swap received an out of bounds index");
    }

    // TODO: implement persistent sets using BinaryTree so we don't need Java mutable Set
    //
    // Generates factorial(n) permutations of an n length list
    //
    // a b c d
    // a b d c
    // a c b d
    // a c d b
    // a d b c
    // a d c b
    // b a c d
    // b a d c
    // b c a d
    // b c d a
    // c a b d
    // c a d b
    // c b a d
    // c b d a
    // c d a b
    // c d b a
    // d a b c
    // d a c b
    // d b a c
    // d b c a
    // d c a b
    // d c b a
    //
    public static final <T> Set<LinkList<T>> permutations(LinkList<T> list)
    {
        Set<LinkList<T>> results = new HashSet<>();

        if(LinkList.Nil == list)
        {
            // empty list has zero permutations
        }
        else if(LinkList.Nil == list.tail)
        {
            // single element list has one permutation
            results.add(list);
        }
        else
        {
            //
            // 1. i = i;
            // 2. left = ith, right = removeAt(i);
            // 2. get all permutations of the right
            // 3. combine left + right permutations
            // 4. combine right permutations + left.
            // 6. i += 1
            //
            int i = 0;
            LinkList<T> left = list;
            while(left.isNotEmpty())
            {
                final Set<LinkList<T>> right = permutations(list.removeAt(i));

                //
                // the head and tail have two permutations
                // add all head + tails
                // add all tails + head
                //
                for (LinkList<T> permutation : right)
                {
                    results.add(permutation.insert(left.head));
                }
                for (LinkList<T> permutation : right)
                {
                    results.add(permutation.append(left.head));
                }

                i = i + 1;
                left = left.tail;
            }



        }
        return results;
    }


}
