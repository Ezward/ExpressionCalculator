package com.lumpofcode.collection.list;

import static org.junit.Assert.*;
import org.junit.Test;

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
            fail("Passing null for tail should throw");
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
        assertTrue("Inserting null returns the original list.", listOfTwo == listOfTwo.insert((String)null));
    }

    @Test
    public void testLinkListInsertList()
    {
        final LinkList<String> listOfOne = new LinkList<>("one");
        final LinkList<String> listOfTwo = listOfOne.insert(new LinkList("two"));
        final LinkList<String> listOfThree = listOfTwo.insert(new LinkList("three"));

        assertTrue("Insert creates new list with another list before it.", listOfTwo.equals(new LinkList<>("two", new LinkList("one", LinkList.Nil))));
        assertTrue("Insert creates new list with another list before it.", listOfThree.equals(new LinkList<>("three", new LinkList<>("two", new LinkList("one", LinkList.Nil)))));

        assertTrue("Inserting null returns the original list.", listOfThree == listOfThree.insert((LinkList)null));
        assertTrue("Inserting Nil returns the original list.", listOfThree == listOfThree.insert(LinkList.Nil));
        assertTrue("Inserting into Nil returns inserted list.", listOfThree == LinkList.Nil.insert(listOfThree));
    }

    @Test
    public void testLinkListAppendElement()
    {
        final LinkList<String> listOfOne = new LinkList<>("two");
        final LinkList<String> listOfTwo = listOfOne.append("one");

        assertTrue("Append creates new list with another element added to the end of the tail.", listOfTwo.equals(new LinkList<>("two", new LinkList("one", LinkList.Nil))));

        assertTrue("Appending null returns the original list.", listOfOne == listOfOne.append((String)null));
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

        assertTrue("Appending null returns the original list.", listOfThree == listOfThree.append((LinkList)null));
        assertTrue("Appending Nil returns the original list.", listOfThree == listOfThree.append(LinkList.Nil));
        assertTrue("Appending to Nil returns the appended list.", listOfThree == LinkList.Nil.append(listOfThree));
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


}
