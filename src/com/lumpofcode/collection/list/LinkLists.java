package com.lumpofcode.collection.list;

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

    /**
     * Given a list, create a new list with two elements swapped.
     *
     * @param list original list
     * @param i ith element is moved to jth position
     * @param j jth element is move to ith position
     * @param <T> type of elements
     * @return new list with ith and jth elements swapped
     */
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
                left = left.insert(list.head);
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
                    middle = middle.insert(list.head);
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

    /**
     * Generate factorial(n) permutations of n length list.
     *
     * NOTE: this only maintains unique permutations, so
     *       the output my be less than factorial(n) in size.
     *       
     * for instance, given [a b c d] it produces;
     *     a b c d
     *     a b d c
     *     a c b d
     *     a c d b
     *     a d b c
     *     a d c b
     *     b a c d
     *     b a d c
     *     b c a d
     *     b c d a
     *     c a b d
     *     c a d b
     *     c b a d
     *     c b d a
     *     c d a b
     *     c d b a
     *     d a b c
     *     d a c b
     *     d b a c
     *     d b c a
     *     d c a b
     *     d c b a
     *
     * @param list the list to permute
     * @param <T> the type of elements in the list
     * @return a set of lists which represent permutations of the input list
     */
    public static final <T> LinkList<LinkList<T>> permutations(LinkList<T> list)
    {
        LinkList<LinkList<T>> results = LinkList.Nil;

        if(LinkList.Nil == list)
        {
            // empty list has zero permutations
        }
        else if(LinkList.Nil == list.tail)
        {
            // single element list has one permutation
            results = results.insert(list);
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
                LinkList<LinkList<T>> right = permutations(list.removeAt(i));

                //
                // add all head + tails
                //
                while (right.isNotEmpty())
                {
                    results = results.insert(right.head.insert(left.head));
                    right = right.tail;
                }

                i = i + 1;
                left = left.tail;
            }



        }
        return results;
    }

    /**
     * Create all combinations of the elements of two lists and
     * return the set of combinations
     *
     * so combine([a, b], [c, d], [e, f]) produces 2^3 = 8 combinations
     * [[a, c, e], [a, c, f], [a, d, e], [a, d, f], [b, c, e], [b, c, f], [b, d, e] [b, d, f]]
     *
     * @param <T>
     * @return
     */
    public static final <T>  LinkList<LinkList<T>> combinations(final LinkList<LinkList<T>> list)
    {
        return innerCombine(list, LinkList.Nil);
    }

    //
    // private recursive helper that takes an accumulator
    //
    private static final <T>  LinkList<LinkList<T>> innerCombine(final LinkList<LinkList<T>> list, final LinkList<LinkList<T>> accumulator)
    {
        if(list.isEmpty())
        {
            return accumulator;
        }

        // we have at least two lists to combine
        return innerCombine(list.tail, combineAppend(accumulator, list.head));
    }

	/**
     * Create all combinations of appending the list elements to
     * the lists in the accumulator.
     *
     * So combineAppend([['a', 'b', 'c'], ['d', 'e', 'f']], ['1', '2', '3'])
     * produces [['a', 'b', 'c', '1'], ['a', 'b', 'c', '2'], ['a', 'b', 'c', '3'],
     *           ['d', 'e', 'f', '1'], ['d', 'e', 'f', '2'], ['d', 'e', 'f', '3']]
     *
     * @param accumulator
     * @param list
     * @param <T>
     * @return
     */
    public static final <T>  LinkList<LinkList<T>> combineAppend(final LinkList<LinkList<T>> accumulator, final LinkList<T> list)
    {
        if(list.isEmpty())
        {
            return accumulator;
        }

        if(accumulator.isEmpty())
        {
            return LinkLists.split(list);
        }

        //
        // generate all the combinations of appending elements of list
        // to the lists in accumulator.
        //
        LinkList<LinkList<T>> combinations = LinkList.Nil;
        for(LinkList<LinkList<T>> a = accumulator; a.isNotEmpty(); a = a.tail)
        {
            for(LinkList<T> l = list; l.isNotEmpty(); l = l.tail)
            {
                combinations = combinations.insert(a.head.append(l.head));
            }
        }

        return combinations;
    }

	/**
     * Split a list into a list of lists by element.
     *
     * split([1, 2, 3]) yields [[1], [2], [3]]
     *
     * @param list
     * @param <T>
     * @return
     */
    public static final <T> LinkList<LinkList<T>> split(LinkList<T> list)
    {
        LinkList<LinkList<T>> result = LinkList.Nil;
        while(list.isNotEmpty())
        {
            result = result.insert(new LinkList<T>(list.head));
            list = list.tail;
        }

        return result.reverse();
    }


    /**
     * Combine the elements of two lists.
     * this is like 'zip' but the output is a list of lists
     * rather than a list of Tuple2, so the lists
     * must be of the same type.
     *
     * zip(['a', 'b', 'c'], ['d', 'e', 'f'])
     * produces [['a', 'd'], ['b', 'e'], ['c', 'f']]
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    public static final <T> LinkList<LinkList<T>> zip(final LinkList<T> left, final LinkList<T> right)
    {
        LinkList<LinkList<T>> result = LinkList.Nil;
        while(left.isNotEmpty() && right.isNotEmpty())
        {
            result = result.insert(LinkLists.linkList(left, right));
        }

        return result.reverse();
    }

}
