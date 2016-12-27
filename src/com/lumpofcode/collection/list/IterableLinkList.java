package com.lumpofcode.collection.list;

import java.util.Iterator;

/**
 * Wrap a LinkList in an Iterable so it easy to use for() loops.
 *
 * Created by Ed Murphy on 12/26/2016.
 */
public final class IterableLinkList<T> implements Iterable<T>
{
    public final LinkList<T> list;

    public IterableLinkList(final LinkList<T> linkList)
    {
        this.list = linkList;
    }

    @Override
    public Iterator<T> iterator()
    {
        return new LinkListIterator(this.list);
    }
}
