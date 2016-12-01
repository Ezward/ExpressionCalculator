package com.lumpofcode.utils;

/**
 * Created by emurphy on 11/30/16.
 */
public final class IntegerTruncateFormatter implements NumberFormatter
{
	@Override
	public String format(final double number)
	{
		return String.valueOf((int)number);
	}
}
