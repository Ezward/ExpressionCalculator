package com.lumpofcode.collection.compare;

import java.util.Comparator;

/**
 * Created by emurphy on 10/20/15.
 */
public class StringComparator implements Comparator<String>
{
    @Override
    public int compare(String theValue, String theOtherValue)
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
