package com.lumpofcode.collection.binarytree;

import com.lumpofcode.collection.compare.IntegerComparator;
import org.junit.Test;

import java.util.Comparator;
import java.util.Random;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
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
    public void testRandomInsert()
    {
        //
        // insert 1000 random integers.
        // then check that the tree is a valid binary search tree
        //
        final IntegerComparator theComparator = new IntegerComparator();
        BinaryTree<Integer> theBinaryTree = BinaryTree.Nil;
        final int n = 100;

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
        fail("tbd");
    }

    @Test
    public void testRemovePromoteRight()
    {
        fail("tdb");
    }
}
