package com.lumpofcode.collection.list;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by emurphy on 1/10/16.
 */
public class LinkListIterator<T> implements Iterator<T>
{
    private LinkList<T> thisLinkList;

    public LinkListIterator(final LinkList<T> theLinkList)
    {
        thisLinkList = theLinkList;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext()
    {
        return !thisLinkList.isEmpty();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public T next()
    {
        if(!hasNext()) throw new NoSuchElementException();

        // get the value
        // pop the node from the list
        // return the value
        final T theValue = thisLinkList.head;
        thisLinkList = thisLinkList.tail;
        return theValue;
    }
}
