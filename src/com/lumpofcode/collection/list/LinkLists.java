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


}
