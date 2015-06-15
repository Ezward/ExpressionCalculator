package com.lumpofcode.collection.binarytree;

import com.lumpofcode.annotation.Immutable;
import com.lumpofcode.annotation.NotNull;

import java.util.Comparator;

/**
 * Created by emurphy on 6/14/15.
 */
@Immutable
public final class BinaryTree<T>
{
    public final T value;
    public final BinaryTree<T> left;
    public final BinaryTree<T> right;

    /**
     * Singleton sentinal for leaf node.
     */
    public static final BinaryTree Nil = new BinaryTree();

    /**
     * Construct a tree with one node.
     *
     * This is equivalent to Nil.insert(value).
     *
     * @param value
     */
    public BinaryTree(final @NotNull T value)
    {
        if(null == value) throw new IllegalArgumentException();

        this.value = value;
        this.left = Nil;
        this.right = Nil;
    }

    /**
     * private used to create Nil node singleton.
     */
    private BinaryTree()
    {
        this.value = null;
        this.left = null;
        this.right = null;
    }

    /**
     * Construct a tree given the value and left and right subtrees.
     *
     * NOTE: not check is made that left < right, so the caller
     *       must enforce this constraint.
     *
     * @param value
     * @param left
     * @param right
     */
    private BinaryTree(final @NotNull T value, final @NotNull BinaryTree<T> left, final @NotNull BinaryTree<T> right)
    {
        if(null == value) throw new IllegalArgumentException();
        if(null == left) throw new IllegalArgumentException();
        if(null == right) throw new IllegalArgumentException();

        this.value = value;
        this.left = left;
        this.right = right;
    }

    /**
     * Binary search using a comparator.
     *
     * @param value
     * @param comparator
     * @return the node with the value or Nil if not found.
     */
    public BinaryTree<T> find(final @NotNull T value, final @NotNull Comparator<T> comparator)
    {
        if(null == value) throw new IllegalArgumentException();
        if(null == comparator) throw new IllegalArgumentException();

        // if we are at a Nil node, we did not find the value
        if(this == Nil) return Nil;

        final int comparison = comparator.compare(value, this.value);
        if(comparison < 0) return this.left.find(value, comparator);
        if(comparison > 0) return this.right.find(value, comparator);
        return this;    // found it
    }

    /**
     * Insert the value in a sorted order based on the given Comparator.
     *
     * NOTE: Duplicate values are not inserted again.
     * If you want to avoid the memory allocations associated with
     * this call when inserting a duplicate value, call find() first.
     *
     * @param value NotNull
     * @param comparator values that compare < 0 insert on left
     * @return a new tree
     */
    public BinaryTree<T> insert(final @NotNull T value, final @NotNull Comparator<T> comparator)
    {
        // if value is null, don't insert it
        if(null == value) return this;

        // inserting into Nil returns binary try with value and not children.
        if(this == Nil) return new BinaryTree<>(value, Nil, Nil);

        //
        // if value is less than this node, recursively insert on left
        //
        final int comparison = comparator.compare(value, this.value);
        if(comparison < 0)
        {
            return new BinaryTree<>(this.value, this.left.insert(value, comparator), this.right);
        }

        //
        // if value is greater than this node, recursively insert on right
        //
        if(comparison > 0)
        {
            return new BinaryTree<>(this.value, this.left, this.right.insert(value, comparator));
        }

        //
        // value is the same as this node, so just return this node
        //
        return this;
    }

    /**
     * Find and remove the node with the given value,
     * the promote the left side when rebalancing the tree.
     *
     * NOTE: this is a recursive algorithm that reallocates
     *       nodes in the tree to create a new tree.  This
     *       happens even if the node that is to be removed
     *       does not exist (actually, that results in worst-case
     *       behavior).  If you wish to avoid the extra memory
     *       allocations is the value does not exist, then
     *       do a find() for the value first.
     *
     * @param value
     * @param comparator
     * @return a new tree with the value removed and left side promoted.
     */
    public BinaryTree<T> removePromoteLeft(final @NotNull T value, final @NotNull Comparator<T> comparator)
    {
        //
        // find it, then fix up the tree
        //
        if(null == value) throw new IllegalArgumentException();
        if(null == comparator) throw new IllegalArgumentException();

        // if we are at a Nil node, we did not find the value
        if(this == Nil) return Nil;

        final int comparison = comparator.compare(value, this.value);
        if(comparison < 0) return new BinaryTree<>(this.value, this.left.removePromoteLeft(value, comparator), this.right);
        if(comparison > 0) return new BinaryTree<>(this.value, this.left, this.right.removePromoteLeft(value, comparator));
        return this.removePromoteLeft();    // found it, now remove it
    }

    /**
     * Find and remove the node with the given value,
     * the promote the right side when rebalancing the tree.
     *
     * NOTE: this is a recursive algorithm that reallocates
     *       nodes in the tree to create a new tree.  This
     *       happens even if the node that is to be removed
     *       does not exist (actually, that results in worst-case
     *       behavior).  If you wish to avoid the extra memory
     *       allocations is the value does not exist, then
     *       do a find() for the value first.
     *
     * @param value
     * @param comparator
     * @return a new tree with the value removed and right side promoted.
     */
    public BinaryTree<T> removePromoteRight(final @NotNull T value, final @NotNull Comparator<T> comparator)
    {
        //
        // find it, then fix up the tree
        //
        if(null == value) throw new IllegalArgumentException();
        if(null == comparator) throw new IllegalArgumentException();

        // if we are at a Nil node, we did not find the value
        if(this == Nil) return Nil;

        final int comparison = comparator.compare(value, this.value);
        if(comparison < 0) return new BinaryTree<>(this.value, this.left.removePromoteRight(value, comparator), this.right);
        if(comparison > 0) return new BinaryTree<>(this.value, this.left, this.right.removePromoteRight(value, comparator));
        return this.removePromoteRight();    // found it, now remove it
    }

    /**
     * Insert the given tree as the left-most node.
     * This is used when rebalancing the tree.
     *
     * @param left NotNull
     * @return new binary tree with the given tree as the left-most node.
     */
    private BinaryTree<T> insertLeft(final @NotNull BinaryTree<T> left)
    {
        if(null == left) throw new IllegalArgumentException();

        if(this == Nil) return left;

        if(this.left == Nil) return new BinaryTree<>(this.value, left, this.right);

        return new BinaryTree<>(this.value, this.left.insertLeft(left), this.right);
    }

    /**
     * Insert the given tree as the right-most leaf node.
     * This is used when rebalancing the tree.
     *
     * @param right
     * @return new tree with right as the right-most leaf node.
     */
    private BinaryTree<T> insertRight(final @NotNull BinaryTree<T> right)
    {
        if(null == right) throw new IllegalArgumentException();

        if(this == Nil) return right;

        if(this.right == Nil) return new BinaryTree<>(this.value, this.left, right);

        return new BinaryTree<>(this.value, this.left, this.right.insertRight(right));
    }


    /**
     * Remove this value and return the tree
     * resulting from promoting the left side.
     * This makes the left branch's value the root
     * of the new subtree (if it is not Nil).
     *
     * @return the tree after removing this value
     *         and promoting the left value to root
     */
    private BinaryTree<T> removePromoteLeft()
    {
        if(this == Nil) return Nil;
        if(this.left == Nil) return this.right;
        if(this.right == Nil) return this.left;

        //
        // left becomes new root.
        // move the left's right to the left side of the right (!yes)
        //
        return new BinaryTree<>(
                this.left.value,
                this.left.left,
                this.right.insertLeft(this.left.right));
    }

    /**
     * Remove this value and return the tree
     * resulting from promoting the right side.
     * This makes the right branch's value the root
     * of the new subtree (if it is not Nil).
     *
     * @return the tree after removing this value
     *         and promoting the right value to root.
     */
    private BinaryTree<T> removePromoteRight()
    {
        if (this == Nil) return Nil;
        if (this.left == Nil) return this.right;
        if (this.right == Nil) return this.left;

        //
        // right becomes new root.
        // move the rights's left to the right side of the left (!yes)
        //
        return new BinaryTree<>(
                this.right.value,
                this.left.insertRight(this.right.left),
                this.right.right);
    }
}
