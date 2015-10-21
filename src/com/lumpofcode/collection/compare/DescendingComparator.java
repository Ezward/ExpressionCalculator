package com.lumpofcode.collection.compare;

import java.util.Comparator;

/**
 * Created by emurphy on 10/21/15.
 */
public class DescendingComparator extends AscendingComparator implements Comparator<Comparable<Object>>
{
    @Override
    public int compare(Comparable<Object> theValue, Comparable<Object> theOtherValue)
    {
        return super.compare(theValue, theOtherValue) * -1; // invert
    }
}
