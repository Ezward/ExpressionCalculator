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

    // TODO: implement persistent sets so we don't use the java mutable sets (use BinaryTree)
    public static <T> Set<LinkList<T>> permutations(final LinkList<T> list)
    {
        Set<LinkList<T>> results = new HashSet<>();

        if(LinkList.Nil == list)
        {
            // empty list has one permutation
            results.add(list);
        }
        else if(LinkList.Nil == list.tail)
        {
            // single element list has one permutation
            results.add(list);
        }
        else
        {
            final Set<LinkList<T>> tails = permutations(list.tail);

            //
            // the head and tail have two permutations
            // add all head + tails
            // add all tails + head
            //
            for (LinkList<T> permutation : tails)
            {
                results.add(permutation.insert(list.head));
            }
            for (LinkList<T> permutation : tails)
            {
                results.add(permutation.append(list.head));
            }
        }
        return results;
    }


}
