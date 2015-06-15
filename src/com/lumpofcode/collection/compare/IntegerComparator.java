package com.lumpofcode.collection.compare;

import java.util.Comparator;

/**
 * Created by emurphy on 6/15/15.
 */
public class IntegerComparator implements Comparator<Integer>
{
    @Override
    public int compare(Integer theValue, Integer theOtherValue)
    {
        if(null != theValue)
        {
            if(null != theOtherValue)
            {
                return theValue.compareTo(theOtherValue);
            }
            return 1;
        }
        if(null != theOtherValue)
        {
            return -1;
        }
        return 0;
    }
}
