package com.lumpofcode.collection.compare;

import java.util.Comparator;

/**
 * Created by emurphy on 10/20/15.
 */
public class AscendingComparator implements Comparator<Comparable<Object>>
{
    @Override
    public int compare(Comparable<Object> theValue, Comparable<Object> theOtherValue)
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
