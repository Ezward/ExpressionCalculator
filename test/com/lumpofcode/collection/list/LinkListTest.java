package com.lumpofcode.collection.list;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by emurphy on 5/19/15.
 */
public class LinkListTest
{
    @Test
    public void testLinkList()
    {
        //
        // passing head as null to constructor is not allowed
        //
        try
        {
            new LinkList(null);
            fail("Passing null for head should throw");
        }
        catch(IllegalArgumentException e)
        {
            assertTrue(true);
        }
        catch(Exception e)
        {
            fail("Unexpected exception.");
        }

        try
        {
            new LinkList("foo", null);
            fail("Passing null for tail should throw an IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            assertTrue(true);
        }
        catch(Exception e)
        {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testEmptyList()
    {
        assertNotNull("Nil is not null.", LinkList.Nil);
        assertTrue("Nil is empty.", LinkList.Nil.isEmpty());
        assertFalse("Nil is not isNotEmpty.", LinkList.Nil.isNotEmpty());
        assertTrue("Length of Nil is zero.", 0 == LinkList.Nil.size());
        assertNull("head of Nil is null.", LinkList.Nil.head);
        assertNull("tail of Nil is null.", LinkList.Nil.tail);
    }

    @Test
    public void testLinkListOfOne()
    {
        final LinkList<String> listOfOne = new LinkList<>("one");

        assertTrue("The list is not empty.", listOfOne.isNotEmpty());
        assertFalse("The list not isEmpty.", listOfOne.isEmpty());
        assertTrue("The list is one long.", 1 == listOfOne.size());
        assertTrue("The head is 'one'.", "one".equals(listOfOne.head));
        assertTrue("The tail is Nil.", LinkList.Nil == listOfOne.tail);
    }

    @Test
    public void testLinkListOfTwo()
    {
        final LinkList<String> listOfOne = new LinkList<>("one");
        final LinkList<String> listOfTwo = new LinkList("two", listOfOne);

        assertTrue("The list is not empty.", listOfTwo.isNotEmpty());
        assertFalse("The list not isEmpty.", listOfTwo.isEmpty());
        assertTrue("The list is two long.", 2 == listOfTwo.size());
        assertTrue("The head is 'two'.", "two".equals(listOfTwo.head));
        assertTrue("The tail is listOfOne.", listOfOne == listOfTwo.tail);
    }

    @Test
    public void testLinkListLast()
    {
        final LinkList<String> listOfOne = new LinkList<>("one");
        final LinkList<String> listOfTwo = listOfOne.append("two");

        assertTrue("Last of one is 'one'", listOfOne == listOfOne.last());
        assertTrue("Last of two is 'two'", listOfTwo.last().equals(new LinkList("two")));
        assertTrue("Last of Nil is Nil.", LinkList.Nil == LinkList.Nil.last());
    }

    @Test
    public void testLinkListInsertElement()
    {
        final LinkList<String> listOfOne = new LinkList<>("one");
        final LinkList<String> listOfTwo = listOfOne.insert("two");

        assertTrue("Insert creates new list with new element in head.", listOfTwo.equals(new LinkList<>("two", new LinkList("one", LinkList.Nil))));
        assertTrue("Inserting null returns the original list.", listOfTwo == listOfTwo.insert((String) null));
    }

    @Test
    public void testLinkListInsertList()
    {
        final LinkList<String> listOfOne = new LinkList<>("one");
        final LinkList<String> listOfTwo = listOfOne.insert(new LinkList("two"));
        final LinkList<String> listOfThree = listOfTwo.insert(new LinkList("three"));

        assertTrue("Insert creates new list with another list before it.", listOfTwo.equals(new LinkList<>("two", new LinkList("one", LinkList.Nil))));
        assertTrue("Insert creates new list with another list before it.", listOfThree.equals(new LinkList<>("three", new LinkList<>("two", new LinkList("one", LinkList.Nil)))));

        assertTrue("Inserting Nil returns the original list.", listOfThree == listOfThree.insert(LinkList.Nil));
        assertTrue("Inserting into Nil returns inserted list.", listOfThree == LinkList.Nil.insert(listOfThree));

        //
        // attempt to insert null throws IllegalArgumentException
        //
        try
        {
            listOfThree.insert((LinkList)null);
            fail("Expected an IllegalArgumentException.");
        }
        catch(IllegalArgumentException e)
        {
            assert(true);
        }
        catch(Exception e)
        {
            fail("Unexpected Exception.");
        }
    }

    @Test
    public void testLinkListAppendElement()
    {
        final LinkList<String> listOfOne = new LinkList<>("two");
        final LinkList<String> listOfTwo = listOfOne.append("one");

        assertTrue("Append creates new list with another element added to the end of the tail.", listOfTwo.equals(new LinkList<>("two", new LinkList("one", LinkList.Nil))));

        assertTrue("Appending to Nil returns a list of the element.", LinkList.Nil.append("one").equals(new LinkList("one")));

    }

    @Test
    public void testLinkListAppendList()
    {
        final LinkList<String> listOfOne = new LinkList<>("three");
        final LinkList<String> listOfTwo = listOfOne.append(new LinkList("two"));
        final LinkList<String> listOfThree = listOfTwo.append(new LinkList("one"));

        assertTrue("Append creates new list with another list added to the end of the tail.", listOfTwo.equals(new LinkList<>("three", new LinkList("two", LinkList.Nil))));
        assertTrue("Append creates new list with another list added to the end of the tail.", listOfThree.equals(new LinkList<>("three", new LinkList("two", new LinkList("one", LinkList.Nil)))));

        assertTrue("Appending Nil returns the original list.", listOfThree == listOfThree.append(LinkList.Nil));
        assertTrue("Appending to Nil returns the appended list.", listOfThree == LinkList.Nil.append(listOfThree));

        //
        // attempt to append null throws IllegalArgumentException
        //
        try
        {
            listOfThree.append((LinkList)null);
            fail("Expected an IllegalArgumentException.");
        }
        catch(IllegalArgumentException e)
        {
            assert(true);
        }
        catch(Exception e)
        {
            fail("Unexpected Exception.");
        }
    }

    @Test
    public void testLinkListEquals()
    {
        final LinkList<String> listOfOne = new LinkList<>("one");
        final LinkList<String> listOfTwo = new LinkList("two", listOfOne);

        assertTrue("A list is equal to itself.", listOfTwo.equals(listOfTwo));
        assertTrue("Unique instances with same elements are equal.", listOfTwo.equals(new LinkList<>("two", new LinkList("one", LinkList.Nil))));
        assertFalse("Lists of same elements in different order are not equal.", listOfTwo.equals(new LinkList<>("one", new LinkList("two", LinkList.Nil))));
    }

    @Test
    public void testReverse()
    {
        LinkList<String> list = new LinkList<>("two");
        list = list.insert("one");

        final LinkList<String> reverseList = list.reverse();

        assertTrue("head is two.", reverseList.head.equals("two"));
        assertTrue("tail is one.", reverseList.tail.head.equals("one"));
        assertTrue("length is two.", reverseList.size() == 2);

        assertTrue("reverse.reverse is not reversed.", list.equals(list.reverse().reverse()));

        assertTrue("Nil reversed is Nil", LinkList.Nil == LinkList.Nil.reverse());
    }

    @Test public void testMap()
    {
        LinkList<Integer> list = new LinkList<>(1).append(2).append(3);

        LinkList<String> mappedList = list.map((x) -> String.valueOf(x + x));

        assertTrue("head is 2", "2".equals(mappedList.head));
        assertTrue("next is 4", "4".equals(mappedList.tail.head));
        assertTrue("tail is 6", "6".equals(mappedList.tail.tail.head));

        assertTrue("Nil is Nil", LinkList.Nil == LinkList.Nil.map((x) -> String.valueOf(x)));
    }

    @Test
    public void testEqualsHashCode()
    {
        LinkList<Integer> list = new LinkList<>(1).append(2).append(3);
        LinkList<Integer> otherList = new LinkList<>(1).append(2).append(3);
        LinkList<Integer> reverseList = list.reverse();

        assertTrue("list and otherList should be equal.", list.equals(otherList));
        assertTrue("list.hashCode and otherList.hashCode should be the same.", list.hashCode() == otherList.hashCode());

        assertTrue("list and reverseList should NOT be equal.", !list.equals(reverseList));
        assertTrue("list.hashCode and reverseList.hashCode should NOT be the same.", list.hashCode() != reverseList.hashCode());
    }

    @Test
    public void testFilter()
    {
        LinkList<Integer> list = new LinkList<>(1).append(2).append(3);

        // filter out the even values, leaving the odd
        LinkList<Integer> filtered = list.filter(i -> 1 == i % 2);

        assertTrue("filtered list has two elements", 2 == filtered.size());
        assertTrue("first item in filtered list is 1", 1 == filtered.head);
        assertTrue("second item in filtered list is 3", 3 == filtered.tail.head);

        // filter out the odd values, leaving the event
        filtered = list.filter(i -> 0 == i % 2);
        assertTrue("filtered list has two elements", 1 == filtered.size());
        assertTrue("first item in filtered list is 2", 2 == filtered.head);
    }

    @Test
    public void testRemoveAt()
    {
        LinkList<Integer> list = new LinkList<>(1).append(2).append(3);

        LinkList<Integer> removed = list.removeAt(1);
        assertTrue("list has two elements", 2 == removed.size());
        assertTrue("first item in filtered list is 1", 1 == removed.head);
        assertTrue("second item in filtered list is 3", 3 == removed.tail.head);
    }
}
