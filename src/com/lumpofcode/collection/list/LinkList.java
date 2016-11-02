package com.lumpofcode.collection.list;

import com.lumpofcode.annotation.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

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
        // return tail.last();

        LinkList<T> thisList = this;
        while(thisList.tail != Nil)
        {
            thisList = thisList.tail;
        }
        return thisList;
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
        // return new LinkList(head, tail.append(element));

        // iterate to avoid recursion
        LinkList<T> theReturnList = new LinkList(element);
        for(LinkList<T> thisList = this.reverse(); thisList != Nil; thisList = thisList.tail)
        {
            theReturnList = theReturnList.insert(thisList.head);
        }
        return theReturnList;
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
//        if (Nil == tail) return new LinkList(head, list);   // optimization to avoid an extra recursive call
//        return new LinkList<>(head, tail.append(list));

        //
        // iterate to avoid recursive calls.
        // we first reverse this list,
        // then insert those elements before the given list.
        //
        LinkList<T> theReturnList = list;
        for (LinkList<T> theStack = this.reverse(); theStack != Nil; theStack = theStack.tail)
        {
            theReturnList = theReturnList.insert(theStack.head);
        }
        return theReturnList;
    }

    /**
     * Remove the element at the given index.
     * If the index is past the end of the list,
     * then the list is returned.
     *
     * @param list
     * @param index
     * @return list without element at index
     */
    public LinkList<T> removeAt(int index)
    {
        if(Nil == this) return this;
//        if(index < 0) throw new IndexOutOfBoundsException();
//        if(0 == index) return tail;
//        return new LinkList(this.head, this.tail.removeAt(index - 1));

        //
        // iterate to avoid recursive calls
        // loop will use insert to build intermediate list to avoid many calls to append.
        // the result is then reversed.
        //
        LinkList<T> theReturnList = LinkList.Nil;
        for(LinkList<T> theList = this; theList.isNotEmpty(); theList = theList.tail)
        {
            if(index != 0)
            {
                theReturnList = theReturnList.insert(theList.head);
            }
            index -= 1;
        }
        return theReturnList.reverse();
    }

    /**
     * Reverse the elements of the list.
     *
     * @return a list with the element order reversed.
     */
    public LinkList<T> reverse()
    {
        if(this == Nil) return Nil;
        // return this.tail.reverse().append(this.head);

        // iterate to avoid recursion.
        LinkList<T> theReverseList = Nil;
        for(LinkList<T> thisList = this; thisList != Nil; thisList = thisList.tail)
        {
            theReverseList = theReverseList.insert(thisList.head);
        }
        return theReverseList;
    }

    /**
     * Map the values in the list using the mapper function
     * and return a new list.
     *
     * @param mapper function that maps a T to an R
     * @param <R> the result type
     * @return list of elements mapped from T to R
     */
    public <R> LinkList<R> map(Function<T, R> mapper)
    {
        if(this == Nil) return Nil;
        // return new LinkList<>(mapper.apply(head), tail.map(mapper));

        //
        // iterate to avoid recursion.
        // we build a reversed list of mapped elements
        // using insertion to avoid the extra list scans
        // that append would incur.
        // then we just un-reverse the mapped list at the end.
        //
        LinkList<R> theMappedList = Nil;
        for(LinkList<T> thisList = this; thisList != Nil; thisList = thisList.tail)
        {
            // inserts mapped values in reverse order
            theMappedList = theMappedList.insert(mapper.apply(thisList.head));
        }
        return theMappedList.reverse(); // un-reverse it.
    }

    /**
     * Map the values in the list using the mapper function
     * and flatten the resulting list of lists.
     *
     * @param mapper maps a T to a list of R
     * @param <R> the result type of the list
     * @return flattened list of R
     */
    public <R> LinkList<R> flatmap(Function<T, LinkList<R>> mapper)
    {
        if(this == Nil) return Nil;

        // return mapper.apply(this.head).append(this.tail.flatmap(mapper));

        // iterate to avoid recursive calls
        LinkList<R> theReturnList = LinkList.Nil;
        for(LinkList<T> theList = this; theList.isNotEmpty(); theList = theList.tail)
        {
            theReturnList = theReturnList.append(mapper.apply(theList.head));
        }
        return theReturnList;
    }

	/**
     * Filter a list given a predicate.
     *
     * @param predicate
     * @return a new list with those elements where predicate.test() returns true.
     */
    public LinkList<T> filter(Predicate<T> predicate)
    {
        if(this == Nil) return Nil;
//        if(predicate.test(head)) return new LinkList<>(head, tail.filter(predicate));
//        return tail.filter(predicate);

        //
        // iterate to avoid recursive calls
        // loop will use insert to build intermediate list to avoid many calls to append.
        // the result is then reversed.
        //
        LinkList<T> theReturnList = LinkList.Nil;
        for(LinkList<T> theList = this; theList.isNotEmpty(); theList = theList.tail)
        {
            if(predicate.test(theList.head))
            {
                theReturnList = theReturnList.insert(theList.head);
            }
        }
        return theReturnList.reverse();
    }

    /**
     * Determine if two lists are equal.
     * Two lists if all elements in the same order and are equal.
     *
     * @param that the tree to test against this for equality
     * @return true of all elements are in the same order and are equal
     */
    public boolean isEqual(LinkList<T> that)
    {
        if(null == that) return false;
        if(this == that) return true; // handles this == Nil or that == Nil

        // return this.head.equals(that.head) && this.tail.isEqual(that.tail);
        LinkList<T> thisList = this;
        LinkList<T> thatList = that;
        while(thisList != Nil)
        {
            if(!thisList.head.equals(thatList.head)) return false;

            thisList = thisList.tail;
            thatList = thatList.tail;
        }
        return true;
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
        // final int result = hash + 31 * ((head != null) ? head.hashCode() : 0);
        // return result + (tail != null ? tail.innerHash(result) : 0);

        // iterate to avoid recursion
        int result = 0;
        for(LinkList<T> thisList = this; thisList != Nil; thisList = thisList.tail)
        {
            // compound result so we get different has for reversed lists
            result += result + (hash + 31 * thisList.head.hashCode());
        }
        return result;
    }
}
