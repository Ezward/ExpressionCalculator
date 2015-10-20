package com.lumpofcode.collection.binarytree;

import com.lumpofcode.collection.compare.IntegerComparator;
import com.lumpofcode.collection.compare.IntegerStringComparator;
import com.lumpofcode.collection.compare.StringComparator;
import com.lumpofcode.collection.compare.ObjectComparator;
import com.lumpofcode.collection.list.LinkList;
import org.junit.Test;

import java.util.Comparator;
import java.util.Random;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by emurphy on 6/14/15.
 */
public class BinaryTreeTest
{
    @Test
    public void testBinaryTree()
    {
        BinaryTree<Integer> theBinaryTree = new BinaryTree<>(10);

        assertEquals("Value should be 10.", Integer.valueOf(10), theBinaryTree.value);
        assertTrue("Left is Nil", BinaryTree.Nil == theBinaryTree.left);
        assertTrue("Right is Nil", BinaryTree.Nil == theBinaryTree.right);

    }

    @Test
    public void testInsert()
    {
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = new BinaryTree<>(10);

        theBinaryTree = theBinaryTree.insert(5, theComparator);
        assertEquals("Value should be 10.", Integer.valueOf(10), theBinaryTree.value);
        assertTrue("Left is non Nil", BinaryTree.Nil != theBinaryTree.left);
        assertEquals("Left value is 5.", Integer.valueOf(5), theBinaryTree.left.value);
        assertTrue("Left.left is Nil", BinaryTree.Nil == theBinaryTree.left.left);
        assertTrue("left.right is Nil", BinaryTree.Nil == theBinaryTree.left.right);
        assertTrue("Right is Nil", BinaryTree.Nil == theBinaryTree.right);

        theBinaryTree = theBinaryTree.insert(15, theComparator);
        assertEquals("Value should be 10.", Integer.valueOf(10), theBinaryTree.value);
        assertTrue("Left is non Nil", BinaryTree.Nil != theBinaryTree.left);
        assertEquals("Left value is 5.", Integer.valueOf(5), theBinaryTree.left.value);
        assertTrue("Left.left is Nil", BinaryTree.Nil == theBinaryTree.left.left);
        assertTrue("left.right is Nil", BinaryTree.Nil == theBinaryTree.left.right);
        assertTrue("Right is non Nil", BinaryTree.Nil != theBinaryTree.right);
        assertEquals("Right value is 15.", Integer.valueOf(15), theBinaryTree.right.value);
        assertTrue("Right.left is Nil", BinaryTree.Nil == theBinaryTree.right.left);
        assertTrue("Right.right is Nil", BinaryTree.Nil == theBinaryTree.right.right);

    }

    @Test
    public void testUpdate()
    {
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = new BinaryTree<>(10);
        theBinaryTree = theBinaryTree.insert(5, theComparator);
        theBinaryTree = theBinaryTree.insert(15, theComparator);

        //
        // if we 'update' a value that does not exist, nothing is changed.
        //
        assertTrue("Updating a value that does not exists does not change the tree.",
                   theBinaryTree == theBinaryTree.update(25, theComparator));

        //
        // if we insert same exact value, the tree should not change
        //
        assertTrue("If we update the exact same value instance, the tree should not change.",
                   theBinaryTree == theBinaryTree.update(
                           theBinaryTree.find(10, theComparator).value, theComparator));
        assertTrue("If we update the exact same value instance, the tree should not change.",
                   theBinaryTree == theBinaryTree.update(
                           theBinaryTree.find(5, theComparator).value, theComparator));
        assertTrue("If we update the exact same value instance, the tree should not change.",
                   theBinaryTree == theBinaryTree.update(
                           theBinaryTree.find(15, theComparator).value, theComparator));
    }

    @Test
    public void testRandomInsert()
    {
        //
        // insert 1000 random integers.
        // then check that the tree is a valid binary search tree
        //
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = BinaryTree.Nil;
        final int n = 10000;

        final Random random = new Random();
        for(int i = 0; i < n; i += 1)
        {
            final int theRandomInt = random.nextInt();
            theBinaryTree = theBinaryTree.insert(theRandomInt, theComparator);
        }

        //
        // now recursively walk the tree to see if it is a valid binary search tree
        //
        assertIntegerBinarySearchTree(theBinaryTree);
        assertBinarySearchTree(theBinaryTree, theComparator);
    }

    @Test
    public void testFind()
    {
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = new BinaryTree<>(10);
        theBinaryTree = theBinaryTree.insert(5, theComparator);
        theBinaryTree = theBinaryTree.insert(15, theComparator);

        assertTrue("Should find 10", 10 == theBinaryTree.find(10, theComparator).value.intValue());
        assertTrue("Should find 5",   5 == theBinaryTree.find( 5, theComparator).value.intValue());
        assertTrue("Should find 10", 15 == theBinaryTree.find(15, theComparator).value.intValue());

        assertTrue("Should not find 7.", theBinaryTree.Nil == theBinaryTree.find(7, theComparator));

    }

    @Test
    public void testRandomFind()
    {
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = BinaryTree.Nil;
        final int n = 10000;
        LinkList<Integer> theIntegerList = LinkList.Nil;    // list will hold the inserted numbers

        final Random random = new Random();
        for(int i = 0; i < n; i += 1)
        {
            final Integer theRandomInt = random.nextInt();
            theIntegerList = theIntegerList.insert(theRandomInt);
            theBinaryTree = theBinaryTree.insert(theRandomInt, theComparator);
        }

        //
        // Each number that we stashed in the list should also be in the tree.
        //
        for(LinkList<Integer> theList = theIntegerList; theList != LinkList.Nil; theList = theList.tail)
        {
            final Integer theInteger = theList.head;
            assertTrue("We should find the integer in the tree.",
                       BinaryTree.Nil != theBinaryTree.find(theInteger, theComparator));
        }
    }


    /**
     * Helper to test an Integer binary search tree without the comparator.
     * This was used to confirm that the comparator was working.
     *
     * @param theBinaryTree
     */
    public void assertIntegerBinarySearchTree(final BinaryTree<Integer> theBinaryTree)
    {
        assertNotNull("The node can never be null.", theBinaryTree);

        //
        // if we are at a Nil node, terminate the recursion
        //
        if(theBinaryTree == BinaryTree.Nil) return;

        assertNotNull("The value can never be null on a nonNil node.", theBinaryTree.value);
        assertNotNull("The left can never be null on a nonNil node.", theBinaryTree.left);
        assertNotNull("The right can never be null on a nonNil node.", theBinaryTree.right);

        assertTrue("The left can be nil, or it's value must be less than the root value.",
                   (theBinaryTree.left == BinaryTree.Nil)
                   || (theBinaryTree.left.value.intValue() < theBinaryTree.value.intValue()));

        assertTrue("The right can be nil, or it's value must be greater than the root value.",
                   (theBinaryTree.right == BinaryTree.Nil)
                   || (theBinaryTree.right.value.intValue() > theBinaryTree.value.intValue()));

        //
        // continue recursing the entire tree
        //
        assertIntegerBinarySearchTree(theBinaryTree.left);
        assertIntegerBinarySearchTree(theBinaryTree.right);
    }

    /**
     * Walk a binary tree and confirm that it is a binary search tree.
     *
     * @param theBinaryTree
     * @param theComparator
     */
    public void assertBinarySearchTree(final BinaryTree theBinaryTree, final Comparator theComparator)
    {
        assertNotNull("The comparator can never be null", theComparator);
        assertNotNull("The node can never be null.", theBinaryTree);

        //
        // if we are at a Nil node, terminate the recursion
        //
        if(theBinaryTree == BinaryTree.Nil) return;

        assertNotNull("The value can never be null on a nonNil node.", theBinaryTree.value);
        assertNotNull("The left can never be null on a nonNil node.", theBinaryTree.left);
        assertNotNull("The right can never be null on a nonNil node.", theBinaryTree.right);

        assertTrue("The left can be nil, or it's value must be less than the root value.",
                   (theBinaryTree.left == BinaryTree.Nil)
                   || theComparator.compare(theBinaryTree.left.value, theBinaryTree.value) < 0);

        assertTrue("The right can be nil, or it's value must be greater than the root value.",
                   (theBinaryTree.right == BinaryTree.Nil)
                   || theComparator.compare(theBinaryTree.right.value, theBinaryTree.value) > 0);

        //
        // continue recursing the entire tree
        //
        assertBinarySearchTree(theBinaryTree.left, theComparator);
        assertBinarySearchTree(theBinaryTree.right, theComparator);
    }


    @Test
    public void testRemovePromoteLeft()
    {
        final IntegerComparator theComparator = new IntegerComparator();

        //
        //          10
        //          |
        //      5---+---15
        //      |        |
        //   3--+--8  13-+-18
        //   |     |
        // 2-+-4 6-+-9
        //
        BinaryTree<Integer> theBinaryTree = new BinaryTree<>(10);
        theBinaryTree = theBinaryTree.insert(5, theComparator);
        theBinaryTree = theBinaryTree.insert(15, theComparator);
        theBinaryTree = theBinaryTree.insert(3, theComparator);
        theBinaryTree = theBinaryTree.insert(8, theComparator);
        theBinaryTree = theBinaryTree.insert(2, theComparator);
        theBinaryTree = theBinaryTree.insert(4, theComparator);
        theBinaryTree = theBinaryTree.insert(6, theComparator);
        theBinaryTree = theBinaryTree.insert(9, theComparator);
        theBinaryTree = theBinaryTree.insert(13, theComparator);
        theBinaryTree = theBinaryTree.insert(18, theComparator);

        //
        //            10
        //            |
        //        3---+---15
        //        |        |
        //     2--+--8  13-+-18
        //           |
        //         6-+-9
        //         |
        //       4-+-Nil
        //

        final BinaryTree<Integer> theNewTree = theBinaryTree.removePromoteLeft(5, theComparator);
        assertTrue("Root is 10.", 10 == theNewTree.value.intValue());

        // right is unchanged
        assertTrue("Root.right is 15", 15 == theNewTree.right.value.intValue());
        assertTrue("Root.right.left is 13", 13 == theNewTree.right.left.value.intValue());
        assertTrue("Root.right.left.left is Nil", BinaryTree.Nil == theNewTree.right.left.left);
        assertTrue("Root.right.left.right is Nil", BinaryTree.Nil == theNewTree.right.left.right);
        assertTrue("Root.right.right is 18", 18 == theNewTree.right.right.value.intValue());
        assertTrue("Root.right.right.left is Nil", BinaryTree.Nil == theNewTree.right.right.left);
        assertTrue("Root.right.right.right is Nil", BinaryTree.Nil == theNewTree.right.right.right);

        // left is promoted
        assertTrue("Root.left is 3.", 3 == theNewTree.left.value.intValue());
        assertTrue("Root.left.left is 2.", 2 == theNewTree.left.left.value.intValue());
        assertTrue("Root.left.left.left is Nil.", BinaryTree.Nil == theNewTree.left.left.left);
        assertTrue("Root.left.left.right is Nil.", BinaryTree.Nil == theNewTree.left.left.left);

        assertTrue("Root.left.right is 8.", 8 == theNewTree.left.right.value.intValue());
        assertTrue("Root.left.right.left is 6.", 6 == theNewTree.left.right.left.value.intValue());
        assertTrue("Root.left.right.left.left is 4.", 4 == theNewTree.left.right.left.left.value.intValue());
        assertTrue("Root.left.right.left.right is Nil.", BinaryTree.Nil == theNewTree.left.right.left.right);
        assertTrue("Root.left.right.right is 9.", 9 == theNewTree.left.right.right.value.intValue());

        assertTrue("Should not find 5.", BinaryTree.Nil == theNewTree.find(5, theComparator));

        //
        // now recursively walk the tree to see if it is a valid binary search tree
        //
        assertIntegerBinarySearchTree(theNewTree);
    }

    @Test
    public void testRemovePromoteRight()
    {
        final IntegerComparator theComparator = new IntegerComparator();

        //
        //          10
        //          |
        //      5---+---15
        //      |        |
        //   3--+--8  13-+-18
        //   |     |
        // 2-+-4 6-+-9
        //
        BinaryTree<Integer> theBinaryTree = new BinaryTree<>(10);
        theBinaryTree = theBinaryTree.insert(5, theComparator);
        theBinaryTree = theBinaryTree.insert(15, theComparator);
        theBinaryTree = theBinaryTree.insert(3, theComparator);
        theBinaryTree = theBinaryTree.insert(8, theComparator);
        theBinaryTree = theBinaryTree.insert(2, theComparator);
        theBinaryTree = theBinaryTree.insert(4, theComparator);
        theBinaryTree = theBinaryTree.insert(6, theComparator);
        theBinaryTree = theBinaryTree.insert(9, theComparator);
        theBinaryTree = theBinaryTree.insert(13, theComparator);
        theBinaryTree = theBinaryTree.insert(18, theComparator);

        //
        //          10
        //          |
        //      8---+---15
        //      |        |
        //   3--+--9  13-+-18
        //   |
        // 2-+-4
        //     |
        // Nil-+-6
        //

        final BinaryTree<Integer> theNewTree = theBinaryTree.removePromoteRight(5, theComparator);
        assertTrue("Root is 10.", 10 == theNewTree.value.intValue());

        // right is unchanged
        assertTrue("Root.right is 15", 15 == theNewTree.right.value.intValue());
        assertTrue("Root.right.left is 13", 13 == theNewTree.right.left.value.intValue());
        assertTrue("Root.right.left.left is Nil", BinaryTree.Nil == theNewTree.right.left.left);
        assertTrue("Root.right.left.right is Nil", BinaryTree.Nil == theNewTree.right.left.right);
        assertTrue("Root.right.right is 18", 18 == theNewTree.right.right.value.intValue());
        assertTrue("Root.right.right.left is Nil", BinaryTree.Nil == theNewTree.right.right.left);
        assertTrue("Root.right.right.right is Nil", BinaryTree.Nil == theNewTree.right.right.right);

        // right is promoted
        assertTrue("Root.left is 8.", 8 == theNewTree.left.value.intValue());
        assertTrue("Root.left.left is 3.", 3 == theNewTree.left.left.value.intValue());
        assertTrue("Root.left.left.left is 2.", 2 == theNewTree.left.left.left.value.intValue());
        assertTrue("Root.left.left.right is 4.", 4 == theNewTree.left.left.right.value.intValue());
        assertTrue("Root.left.left.right.right is 6.", 6 == theNewTree.left.left.right.right.value.intValue());
        assertTrue("Root.left.left.right.left is Nil.", BinaryTree.Nil == theNewTree.left.left.right.left);
        assertTrue("Root.left.right is 9.", 9 == theNewTree.left.right.value.intValue());

        assertTrue("Should not find 5.", BinaryTree.Nil == theNewTree.find(5, theComparator));

        //
        // now recursively walk the tree to see if it is a valid binary search tree
        //
        assertIntegerBinarySearchTree(theNewTree);
    }

    @Test
    public void testRandomtestRemovePromoteRight()
    {
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = BinaryTree.Nil;
        final int n = 10000;
        LinkList<Integer> theIntegerList = LinkList.Nil;    // list will hold the inserted numbers

        final Random random = new Random();
        for(int i = 0; i < n; i += 1)
        {
            final Integer theRandomInt = random.nextInt();
            theIntegerList = theIntegerList.insert(theRandomInt);
            theBinaryTree = theBinaryTree.insert(theRandomInt, theComparator);
        }

        //
        // Each number that we stashed in the list should also be in the tree.
        //
        for(LinkList<Integer> theList = theIntegerList.reverse(); theList != LinkList.Nil; theList = theList.tail)
        {
            final Integer theInteger = theList.head;
            theBinaryTree = theBinaryTree.removePromoteRight(theInteger, theComparator);

            //
            // now recursively walk the tree to see if it is a valid binary search tree
            //
            assertIntegerBinarySearchTree(theBinaryTree);
        }

        assertTrue("tree should be empty.", BinaryTree.Nil == theBinaryTree);
    }

    @Test
    public void testRandomtestRemovePromoteLeft()
    {
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = BinaryTree.Nil;
        final int n = 10000;
        LinkList<Integer> theIntegerList = LinkList.Nil;    // list will hold the inserted numbers

        final Random random = new Random();
        for(int i = 0; i < n; i += 1)
        {
            final Integer theRandomInt = random.nextInt();
            theIntegerList = theIntegerList.insert(theRandomInt);
            theBinaryTree = theBinaryTree.insert(theRandomInt, theComparator);
        }

        //
        // Each number that we stashed in the list should also be in the tree.
        //
        for(LinkList<Integer> theList = theIntegerList.reverse(); theList != LinkList.Nil; theList = theList.tail)
        {
            final Integer theInteger = theList.head;
            theBinaryTree = theBinaryTree.removePromoteLeft(theInteger, theComparator);

            //
            // now recursively walk the tree to see if it is a valid binary search tree
            //
            assertIntegerBinarySearchTree(theBinaryTree);
        }

        assertTrue("tree should be empty.", BinaryTree.Nil == theBinaryTree);
    }

    @Test
    public void testMap()
    {
        //
        // insert 1000 random integers.
        // then check that the tree is a valid binary search tree
        //
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = BinaryTree.Nil;
        final int n = 1000;

        final Random random = new Random();
        for(int i = 0; i < n; i += 1)
        {
            final int theRandomInt = random.nextInt();
            theBinaryTree = theBinaryTree.insert(theRandomInt, theComparator);
        }

        //
        // the mapped tree will have the same structure,
        // but elements mapped from Integer to String.
        //
        final BinaryTree<String> theMappedTree = theBinaryTree.map((x) -> String.valueOf(x));

        //
        // now recursively walk the tree to see if it is a valid binary search tree.
        // we use a comparator that takes the integer strings can compares them as numbers.
        //
        assertBinarySearchTree(theMappedTree, new IntegerStringComparator());

        //
        // reconvert the mapped tree back to the Integer and see
        // if it is equal to the original tree.
        final BinaryTree<Integer> theReformedTree = theMappedTree.map((x) -> Integer.valueOf(x));

        assertTrue("Original tree and remapped tree should be the same.", theBinaryTree.isEqual(theReformedTree));

    }
}
