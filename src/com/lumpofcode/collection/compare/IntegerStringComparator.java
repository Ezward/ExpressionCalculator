package com.lumpofcode.collection.compare;

import java.util.Comparator;

/**
 * Created by emurphy on 10/20/15.
 */
public class IntegerStringComparator implements Comparator<String>
{
    /**
     * Compare integer strings.
     * The strings are converted to Integer using Integer.valueOf()
     * then compared.  If the Strings are not numbers, then
     * a NumberFormatException is thrown.
     *
     * @param theValue null or the String form of an Integer
     * @param theOtherValue null or the String form of an Integer
     * @return 0 if the values are both null or equal as Integer,
     *         1 if theValue > theOtherValue as Integer
     *         -1 if theValue < theOtherValue as Integer
     * @throws NumberFormatException if either value is non-null and not numeric.
     */
    @Override
    public int compare(String theValue, String theOtherValue) throws NumberFormatException
    {
        if(null != theValue)
        {
            if(null != theOtherValue)
            {
                return Integer.valueOf(theValue).compareTo(Integer.valueOf(theOtherValue));
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
