package com.lumpofcode.collection.list;

import com.lumpofcode.annotation.NotNull;

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

    public LinkList<T> reverse()
    {
        if(this == Nil) return Nil;
        return this.tail.reverse().append(this.head);
    }

    @Override
    public boolean equals(Object that)
    {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        LinkList<?> linkList = (LinkList<?>) that;

        if (head != null ? !head.equals(linkList.head) : linkList.head != null) return false;
        return !(tail != null ? !tail.equals(linkList.tail) : linkList.tail != null);
    }

    @Override
    public int hashCode()
    {
        int result = head != null ? head.hashCode() : 0;
        result = 31 * result + (tail != null ? tail.hashCode() : 0);
        return result;
    }
}
