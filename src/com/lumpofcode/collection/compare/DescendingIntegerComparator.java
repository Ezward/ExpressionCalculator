package com.lumpofcode.collection.compare;

/**
 * Created by emurphy on 10/21/15.
 */
public class DescendingIntegerComparator extends IntegerComparator
{
    @Override
    public int compare(Integer theValue, Integer theOtherValue)
    {
        return super.compare(theValue, theOtherValue) * -1; // invert
    }

}
