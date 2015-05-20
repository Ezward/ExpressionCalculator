package com.lumpofcode.utils;

public final class StringUtils 
{
	public static final char CHAR_TAB = 9;
	public static final char CHAR_SPACE = 32;
	public static final char CHAR_LINEFEED = 10;
	public static final char CHAR_LF = 10;
	public static final char CHAR_CARRIAGE_RETURN = 13;
	public static final char CHAR_CR = 13;
	public static final char CHAR_SINGLE_QUOTE = 39;	// single quote (apostrophe)
	public static final char CHAR_DOUBLE_QUOTE = 34;
	public static final char CHAR_BACKSPACE = 8;
	public static final char CHAR_DELETE = 127;
	public static final char CHAR_ESCAPE = 27;
	
	private StringUtils() {} // prevent this from being constructed

	/**
	 * Determine if a character is whitespace.
	 * Whitespace is a tab, line-feed, carriage-return or space.
	 * 
	 * @param theCharCode
	 * @return true if theCharCode is for a whitespace character, 
	 *         false if not.
	 */
	public static final boolean isWhiteSpace(final char theCharCode)
	{
		switch(theCharCode)
		{
			case CHAR_TAB: return true;
			case CHAR_LF: return true;
			case CHAR_CR: return true;
			case CHAR_SPACE: return true;
		}
		return false;
	}
	
	/**
	 * @param theCharCode
	 * @return true if theCharCode is for a single-quote or double-quote character,
	 *         false otherwise.
	 */
	public static final boolean isQuote(final char theCharCode)
	{
		return (theCharCode == CHAR_SINGLE_QUOTE) || (theCharCode == CHAR_DOUBLE_QUOTE);
	}
	
	public static final boolean isEndOfLine(final char theCharCode)
	{
		return (theCharCode == CHAR_LINEFEED) || (theCharCode == CHAR_CARRIAGE_RETURN);
	}
	
	/**
	 * @param theCharCode
	 * @return true if theCharCode is for a digit, false otherwise
	 */
	public static final boolean isDigit(final char theCharCode)
	{
		return (theCharCode >= '0') && (theCharCode <= '9');
	}
	
	/**
	 * @param theCharCode
	 * @return true if theCharCode if for an upper-case alphabetic character,
	 *         false otherwise.
	 */
	public static final boolean isUpper(final char theCharCode)
	{
		return (theCharCode >= 'A') && (theCharCode <= 'Z');
	}
	
	/**
	 * @param theCharCode
	 * @return true if theCharCode of for a lower-case alphabetic character,
	 *         false otherwise.
	 */
	public static final boolean isLower(final char theCharCode)
	{
		return (theCharCode >= 'a') && (theCharCode <= 'z');
	}
	
	/**
	 * @param theCharCode
	 * @return true if theCharCode is for an alphabetic character,
	 *         false otherwise.
	 */
	public static final boolean isAlphabetic(final char theCharCode)
	{
		return isLower(theCharCode) || isUpper(theCharCode);
	}
	
	/**
	 * @param theCharCode
	 * @return true if theCharCode is for an alphabetic character or a digit,
	 *         false otherwise.
	 */
	public static final boolean isAlphaOrDigit(final char theCharCode)
	{
		return isAlphabetic(theCharCode) || isDigit(theCharCode);
	}
	
	/**
	 * @param theCharCode
	 * @return true if theCharCode is for an alphabetic character or a digit or a hyphen (minus) character.
	 *         false otherwise.
	 */
	public static final boolean isAlphaOrDigitOrHyphen(final char theCharCode)
	{
		return ('-' == theCharCode) || ('_' == theCharCode) || isAlphaOrDigit(theCharCode);
	}
	
	/**
	 * Trim whitespace characters from both ends of theString
	 * 
	 * @param theString
	 * @return the string without whitespace characters at either end.
	 */
	public static final String trim(final String theString)
	{
		final int theLeft = scanWhitespace(theString);
		final int theRight = scanNonWhitespace(theString, theLeft);
		return theString.substring(theLeft, theRight);
	}
	
	/**
	 * Trim whitespace characters from the beginning of theString
	 * 
	 * @param theString
	 * @return the string without whitespace characters at the start.
	 */
	public static final String trimLeft(final String theString)
	{
		return theString.substring(scanWhitespace(theString));
	}
	
	/**
	 * Trim trailing whitespace characters from theString.
	 * @param theString
	 * @return the string without whitespace characters on the end.
	 */
	public static final String trimRight(final String theString)
	{
		//
		// handle a string of all whitespace
		//
		final int theLeft = scanWhitespace(theString);
		if(theLeft < theString.length())
		{
			return theString.substring(0, scanNonWhitespace(theString, theLeft));
		}
		return "";
	}
	
	/**
	 * Find the first span of alphabetic characters and digits in theString.
	 * 
	 * @param theString
	 * @return the first span of alphabetic characters and digits in theString.
	 */
	public static final String keepAlphaAndNumeric(final String theString)
	{
		final int theLeft = scanNonAlphaAndNumeric(theString);
		final int theRight = scanAlphaAndNumeric(theString, theLeft);
		return theString.substring(theLeft, theRight);
	}
	
	
	/**
	 * Determine if a string has whitespace at the beginning or end. 
	 * 
	 * @param theString
	 * @return true if string has whitespace at the start or end.
	 * 
	 */
	public static final boolean hasDanglingWhitespace(final String theString)
	{
		if(null == theString) return false;
		if(theString.length() == 0) return false;
		
		//
		// cannot start with whitespace or end with whitespace
		//
		if(StringUtils.isWhiteSpace(theString.charAt(0))) return true;
		if(StringUtils.isWhiteSpace(theString.charAt(theString.length() - 1))) return true;
		
		return false;
	}
	
	/**
	 * Scan whitespace at the start of theString.
	 * 
	 * @param theString
	 * @return the index of the first non-whitespace character in the string
	 *         or theString.length() if there are no non-whitespace characters.
	 */
	public static final int scanWhitespace(final String theString)
	{
		return scanWhitespace(theString, 0);
	}
	
	/**
	 * Scan a span of whitespace within theString.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return the index of the first non-whitespace character at or after theStartIndex, 
	 *         or theString.length() if there are no non-whitespace characters.
	 */
	public static final int scanWhitespace(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		while((i < theLength) && isWhiteSpace(theString.charAt(i)))
		{
			i += 1;
		}
		return i;
	}
	
	/**
	 * Scan non-whitespace characters at the start of theString.
	 * 
	 * @param theString
	 * @return the index of the first whitespace character in the string
	 *         or theString.length() if there are no whitespace characters.
	 */
	public static final int scanNonWhitespace(final String theString)
	{
		return scanNonWhitespace(theString, 0);
	}
	
	/**
	 * Scan a span of non-whitespace within theString.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return the index of the first whitespace character at or after theStartIndex, 
	 *         or theString.length() if there are no whitespace characters.
	 */
	public static final int scanNonWhitespace(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		while((i < theLength) && !isWhiteSpace(theString.charAt(i)))
		{
			i += 1;
		}
		return i;
	}
	
	/**
	 * Scan alphabetic characters at the start of theString.
	 * 
	 * @param theString
	 * @return the index of the first alphabetic character in the string
	 *         or theString.length() if there are no alphabetic characters.
	 */
	public static final int scanAlphabetic(final String theString)
	{
		return scanAlphabetic(theString, 0);
	}
	
	/**
	 * Scan a span of alphabetic characters within theString.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return the index of the first alphabetic character at or after theStartIndex, 
	 *         or theString.length() if there are no alphabetic characters.
	 */
	public static final int scanAlphabetic(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		while((i < theLength) && isAlphabetic(theString.charAt(i)))
		{
			i += 1;
		}
		return i;
	}
	
	/**
	 * Scan a span of digits at the start of theString.
	 * 
	 * @param theString
	 * @return the index of the first non-digit character in the string
	 *         or theString.length() if there are no non-digit characters.
	 */
	public static final int scanNumeric(final String theString)
	{
		return scanNumeric(theString, 0);
	}
	
	/**
	 * Scan a span of digits within theString.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return the index of the first non-digit at or after theStartIndex, 
	 *         or theString.length() if there are no non-digit characters.
	 */
	public static final int scanNumeric(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		while((i < theLength) && isDigit(theString.charAt(i)))
		{
			i += 1;
		}
		return i;
	}
	
	/**
	 * Scan alphabetic characters and digits at the start of theString.
	 * 
	 * @param theString
	 * @return the index of the first alphabetic character or digit in the string
	 *         or theString.length() if there are no alphabetic characters or digits.
	 */
	public static final int scanAlphaAndNumeric(final String theString)
	{
		return scanAlphaAndNumeric(theString, 0);
	}
	
	/**
	 * Scan a span of alphabetic characters and digits within theString.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return the index of the first alphabetic character or digit at or after theStartIndex, 
	 *         or theString.length() if there are no alphabetic characters or digits.
	 */
	public static final int scanAlphaAndNumeric(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		while((i < theLength) && isAlphaOrDigit(theString.charAt(i)))
		{
			i += 1;
		}
		return i;
	}
	
	/**
	 * Scan the alphabetic character at start of theString, and the
	 * span of alphabetic characters and digits that follow it.
	 * 
	 * @param theString
	 * @return
	 */
	public static final int scanAlphaNumeric(final String theString)
	{
		return scanAlphaNumeric(theString, 0);
	}

	/**
	 * Scan the alphabetic character at theStartIndex, and the 
	 * span of alphabetic characters and digits that follow it.
	 * 
	 * @param theString
	 * @param theStartIndex
	 * @return
	 */
	public static final int scanAlphaNumeric(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();	
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		if((i < theLength) && isAlphabetic(theString.charAt(i)))
		{
			while((i < theLength) && isAlphaOrDigit(theString.charAt(i)))
			{
				i += 1;
			}
		}
		return i;
	}

	/**
	 * Scan the alphabetic character at start of theString, and the 
	 * span of alphabetic characters, digits and hyphens and underscores that follow it.
	 * 
	 * @param theString
	 * @return
	 */
	public static final int scanAlphaNumericAndHyphen(final String theString)
	{
		return scanAlphaNumericAndHyphen(theString, 0);
	}

	/**
	 * Scan the alphabetic character at theStartIndex, and the span
	 * alphabetic characters, digits and hyphens that follow it.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return
	 */
	public static final int scanAlphaNumericAndHyphen(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();	
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		if((i < theLength) && isAlphabetic(theString.charAt(i)))
		{
			while((i < theLength) && isAlphaOrDigitOrHyphen(theString.charAt(i)))
			{
				i += 1;
			}
		}
		return i;
	}

	
	/**
	 * Scan a span of non-alphabetic characters at the start of theString.
	 * 
	 * @param theString
	 * @return the index of the first alphabetic character in theString,
	 *         or theString.length() if there are no alphabetic characters.
	 */
	public static final int scanNonAlpha(final String theString)
	{
		return scanNonAlpha(theString, 0);
	}
	
	/**
	 * Scan a span of non-alphabetic characters within theString.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return the index of the first alphabetic character at or after theStartIndex, 
	 *         or theString.length() if there are no alphabetic characters.
	 */
	public static final int scanNonAlpha(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		while((i < theLength) && !isAlphabetic(theString.charAt(i)))
		{
			i += 1;
		}
		return i;
	}

	/**
	 * Scan a span of non-alphabetic and non-digit characters 
	 * at the start of theString.
	 * 
	 * @param theString
	 * @return the index of the first alphabetic character or digit in theString, 
	 *         or theString.length() if there are no alphabetic characters or digits.
	 */
	public static final int scanNonAlphaAndNumeric(final String theString)
	{
		return scanNonAlphaAndNumeric(theString, 0);
	}
	
	/**
	 * Scan a span of non-alphabetic and non-digit characters within theString.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @return the index of the first alphabetic character or digit at or after theStartIndex, 
	 *         or theString.length() if there are no alphabetic characters or digits.
	 */
	public static final int scanNonAlphaAndNumeric(final String theString, final int theStartIndex)
	{
		final int theLength = theString.length();
		int i = (theStartIndex >= 0) ? theStartIndex : 0;
		while((i < theLength) && !isAlphaOrDigit(theString.charAt(i)))
		{
			++i;
		}
		return i;
	}
	
	/**
	 * Scan theString until the first delimiter character is found.
	 * 
	 * @param theString
	 * @param theStartIndex the index to start scanning.
	 *        negative numbers are treated like zero.
	 * @param theDelimiter the delimiter character to find
	 * @return the index of the delimiter character in theString,
	 *         or theString.length() if it is not found.
	 */
	public static final int scanToDelimiter(final String theString, final int theStartIndex, final char theDelimiter)
	{
		final int theLength = theString.length();
		int i = theStartIndex >= 0 ? theStartIndex : 0;
		while((i < theLength) && (theDelimiter != theString.charAt(i)))
		{
			i += 1;
		}
		return i;
	}
		
	/**
	 * Scan the string for an unsigned integer or unsigned decimal number.
	 * 
	 * @param theText
	 * @param theStartIndex
	 * @return the ending index of the scan.  This is equal theStartIndex if a number could
	 *         not be scanned, otherwise it is the index of the first character after the valid number.
	 */
	public static final int scanUnsignedDecimal(final String theText, final int theStartIndex)
	{
		if(null != theText)
		{
			final double length = theText.length();
			if ((length > theStartIndex) && (theText.charAt(theStartIndex) >= '0') && (theText.charAt(theStartIndex) <= '9'))
			{
				int i = theStartIndex;		// this index within the string
				
				// parse the integer portion
				while((i < length) && (theText.charAt(i) >= '0') && (theText.charAt(i) <= '9'))
				{
					i += 1;
				}

				// parse optional decimal portion
				if(((i+1) < length) && (theText.charAt(i) == '.') && (theText.charAt(i+1) >= '0') && (theText.charAt(i+1) <= '9'))
				{
					i += 1;	// skip decimal point
					while((i < length) && (theText.charAt(i) >= '0') && (theText.charAt(i) <= '9'))
					{
						i += 1;
					}
				}
				
				return i;
			}
			
		}
		return theStartIndex;
	}

	/**
	 * Scan the string for an integer or decimal number with an optional negation sign.
	 * 
	 * @param theText
	 * @param theStartIndex
	 * @return the ending index of the scan.  This is equal theStartIndex if a number could
	 *         not be scanned, otherwise it is the index of the first character after the valid number.
	 */
	public static final int scanSignedDecimal(final String theText, final int theStartIndex)
	{
		if(null != theText)
		{
			final int theLength = theText.length();
			if(theLength > theStartIndex)
			{
				// parse optional negation sign
				if(theText.charAt(theStartIndex) == '-')
				{
					final int theEndIndex = scanUnsignedDecimal(theText, theStartIndex + 1);
					if(theEndIndex > (theStartIndex + 1))
					{
						return theEndIndex;
					}
				}
				else	// no negation sign
				{
					return scanUnsignedDecimal(theText, theStartIndex);
				}
			}
		}
		return theStartIndex;
	}

	
	/**
	 * Parse the characters from theStartIndex (inclusive) up to theEndIndex (not-inclusive)
	 * and convert them into an integer.  All the characters must be digits for the 
	 * returned integer to be correct.  However, no error checking is done in this regard,
	 * so the string should first be scanned with theEndIndex = scanNumeric(theString, theStartIndex).
	 * 
	 * @param theString
	 * @param theStartIndex no checking is done on this value
	 * @param theEndIndex no checking is done of this value
	 * @return An integer value.  The value is only correct if all characters from
	 *         theStartIndex up to theEndIndex are digits.  The value is zero 
	 *         if the string is empty.
	 */
	public static final int parseInteger(final String theString, final int theStartIndex, final int theEndIndex)
	{
		int theValue = 0;
		int i = theStartIndex; 
		while(i < theEndIndex)
		{
			theValue = (theValue * 10) + (theString.charAt(i) - '0');
			i += 1;
		}
		return theValue;
	}
	

	/**
	 * Compare two strings, but ignore whitespace in the comparison.
	 * 
	 * for instance "( ( 10 + 1 ) * 5 )" equals "((10+1)*5)".
	 * 
	 * @param theString
	 * @param theOtherString
	 * @return 0 if equal, -1 if theString sorts less than theOtherString, 1 if theString sorts more than theOtherString.
	 */
	public static final int compareIgnoreWhitespace(final String theString, final String theOtherString)
	{
		if(null != theString)
		{
			if(null != theOtherString)
			{
				final int theLength = theString.length();
				final int theOtherLength = theOtherString.length();
				
				int theIndex = StringUtils.scanWhitespace(theString, 0);
				int theOtherIndex = StringUtils.scanWhitespace(theOtherString, 0);
				while((theIndex < theLength) && (theOtherIndex < theOtherLength))
				{
					final int theDifference = theString.charAt(theIndex) - theOtherString.charAt(theOtherIndex);
					if(0 != theDifference)
					{
						return theDifference;
					}
					
					theIndex = StringUtils.scanWhitespace(theString, theIndex + 1);
					theOtherIndex = StringUtils.scanWhitespace(theOtherString, theOtherIndex + 1);
				}
				
				if(theIndex < theLength)
				{
					return 1;
				}
				if(theOtherIndex < theOtherLength)
				{
					return -1;
				}
				return 0;
			}
			else
			{
				return 0;	// both are null;
			}
		}
		else
		{
			return (null != theOtherString) ? 1 : 0;
		}
	}
	
	/**
	 * Merge the strings in a string array.
	 * Null strings are treated as empty strings.
	 * 
	 * @param theStringArray
	 * @param theDelimiter the delimiter to write between merged strings, null is treated as empty string
	 * @return null if theStringArray is null, otherwise non-null concatenations of strings.
	 */
	public static final String merge(final String[] theStringArray, final String theDelimiter)
	{
		if(null != theStringArray)
		{
			return merge(theStringArray, theDelimiter, theStringArray.length);
		}
		return null;
	}
	
	/**
	 * Merge a subset of strings in a string array.
	 * Null strings are treated as empty strings.
	 * 
	 * @param theStringArray
	 * @param theDelimiter the delimiter to write between merged strings, null is treated as empty string
	 * @param theCount the number of strings to merge (will use minimum of theCount and the array length)
	 * @return null if theStringArray is null, otherwise non-null concatenations of strings.
	 */
	public static final String merge(final String[] theStringArray, final String theDelimiter, final int theCount)
	{
		if(null != theStringArray)
		{
			final int theLength = (theCount > 0) ? ((theCount < theStringArray.length) ? theCount : theStringArray.length) : 0;
			if(theLength > 0)
			{
				if(theLength > 1)
				{
					final String theBuildDelimiter = (null != theDelimiter) ? theDelimiter : "";
					final StringBuilder theBuilder = new StringBuilder();
					theBuilder.append(theStringArray[0]);
					for(int i = 1; i < theLength; i += 1)
					{
						theBuilder.append(theBuildDelimiter);
						
						final String theString = theStringArray[i];
						if(null != theString)
						{
							theBuilder.append(theString);
						}
					}
					return theBuilder.toString();
				}
				else
				{
					return (null != theStringArray[0]) ? theStringArray[0] : "";
				}
			}
			else
			{
				return "";
			}
		}
		return null;
	}

	/**
	 * Convert a string to a boolean value.
	 *
	 * @param value the String to convert
	 * @param defaultVal default returned if value is null or empty
	 * @return true if value is "true" case insensitive,
	 *         defaultVal if value is null or empty,
	 *         false otherwise
	 */
	public static boolean asBoolean (String value, boolean defaultVal)
	{
		if ((null == value) || (value.isEmpty()))
		{
			return defaultVal;
		}

		return value.compareToIgnoreCase("true")==0;
	}

	/**
	 * Convert a string to a boolean value.
	 *
	 * @param value the String to convert
	 * @return true if value is "true" case insensitive, false otherwise
	 */
	public static boolean asBoolean (String value)
	{
		return asBoolean(value, false);
	}

}
