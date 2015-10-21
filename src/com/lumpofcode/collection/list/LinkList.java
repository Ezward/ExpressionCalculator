package com.lumpofcode.collection.list;

import com.lumpofcode.annotation.NotNull;

import java.util.function.Function;

/**
 * Persistent linked list.
 *
 * The head is the value of the node.
 * The tail is a recursive set of LinkList nodes which ends
 * in a final node with a Nil sentinel node.
 *
 * Created by emurphy on 4/20/15.
 *
 */
public final class LinkList<T>
{
    public final T head;            // non-null value of this node
    public final LinkList<T> tail;  // the rest of the list
                                    // or null if this is the end of the list
    /**
     * empty list as a singleton (a sentinel node for end of list)
     */
    public static final LinkList Nil = new LinkList();

    /**
     * Construct a new list using Cons operation
     *
     * @param head non-null element
     * @param tail the rest of the list; may be null at end of list
     */
    public LinkList(final @NotNull T head, final @NotNull LinkList<T> tail)
    {
        if(null == head) throw new IllegalArgumentException();
        if(null == tail) throw new IllegalArgumentException();

        this.head = head;
        this.tail = tail;
    }

    /**
     * Construct a new list with a single node
     *
     * @param head non-null element
     */
    public LinkList(final T head)
    {
        this(head, Nil);
    }

    /**
     * construct an empty list
     */
    private LinkList()
    {
        this.head = null;
        this.tail = null;
    }

    /**
     * Determine if the list is empty.
     *
     * @return true if empty, false if not empty.
     */
    public boolean isEmpty()
    {
        return Nil == this;
    }

    /**
     * Determine if the list is not empty.
     *
     * @return true if not empty, false if this list is empty.
     */
    public boolean isNotEmpty()
    {
        return Nil != this;
    }

    /**
     * Size of the list.
     *
     * Note: This walks the list to do the count,
     * so use isEmpty or isNotEmpty if you only need
     * to know if the list is empty or not empty.
     *
     * @return number of nodes in the list.
     */
    public int size()
    {
        return (Nil == this) ? 0 : 1 + tail.size();
    }

    /**
     * Last non-Nil node in the list.
     *
     * Note: This walks the list to find the last node,
     *       so this can be an expensive operation.
     *
     * @return the last non-Nil node in the list or Nil if list is empty
     */
    public LinkList<T> last()
    {
        if (Nil == this) return Nil;
        if (Nil == tail) return this;
        return tail.last();
    }

    /**
     * Insert element at head of this list.
     *
     * @param element element to insert at head of this list.
     * @return new list with element as the head OR this list if element is null.
     */
    public LinkList<T> insert(final T element)
    {
        return (null == element) ? this : new LinkList(element, this);
    }


    /**
     * Insert another list before this list.
     *
     * @param list the list to insert before this list.
     * @return new list with the list inserted at head OR this list if passes list is null.
     */
    public LinkList<T> insert(final LinkList<T> list)
    {
        if (null == list) throw new IllegalArgumentException();

        return list.append(this); // reverse the order to turn this into an append
    }

    /**
     * Append an element to this list.
     *
     * @param element to append to this list
     * @return a new list with element as the tail or this list if element is null.
     */
    public LinkList<T> append(final T element)
    {
        if (null == element) throw new IllegalArgumentException();

        if (Nil == this) return new LinkList(element);
        return new LinkList(head, tail.append(element));
    }

    /**
     * Append another list to this list.
     *
     * @param list to append to this
     * @return a new list with element as the tail or this list if element is null.
     */
    public LinkList<T> append(final LinkList<T> list)
    {
        if (null == list) throw new IllegalArgumentException();

        if (Nil == list) return this;
        if (Nil == this) return list;
        if (Nil == tail) return new LinkList(head, list);   // optimization to avoid an extra recursive call
        return new LinkList<>(head, tail.append(list));
    }

    /**
     * Reverse the elements of the list.
     *
     * @return a list with the element order reversed.
     */
    public LinkList<T> reverse()
    {
        if(this == Nil) return Nil;
        return this.tail.reverse().append(this.head);
    }

    /**
     * Map the values in the list using the mapper function
     * and return a new list.
     *
     * @param mapper function that maps a T to an R
     * @param <R> the result type
     * @return list of elements mapped from T to R
     */
    <R> LinkList<R> map(Function<? super T, ? extends R> mapper)
    {
        if(this == Nil) return Nil;
        return new LinkList<>(mapper.apply(head), tail.map(mapper));
    }

    /**
     * Determine if two trees are equal.
     * Two trees are equal if they have the same structure
     * and all elements are equal.
     *
     * @param that the tree to test against this for equality
     * @return true of all elements are in the same order and are equal
     */
    public boolean isEqual(LinkList<T> that)
    {
        if(null == that) return false;
        if(this == that) return true; // handles this == Nil or that == Nil

        return this.head.equals(that.head) && this.tail.isEqual(that.tail);
    }

    @Override
    public boolean equals(Object that)
    {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        return isEqual((LinkList)that);
    }

    /**
     * Hash the list.
     * This uses an algorithm that takes position into
     * account, so that reversed lists will hash to
     * different values (unless they are symmetrical).
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return innerHash(0);
    }

    /**
     * inner hash algorithm that uses compounded hash to
     * make it so that order matters in the hash.
     *
     * @param hash previous hash carried so it is compounded.
     * @return the hash.
     */
    private final int innerHash(final int hash)
    {
        final int result = hash + 31 * ((head != null) ? head.hashCode() : 0);
        return result + (tail != null ? tail.innerHash(result) : 0);
    }
}
