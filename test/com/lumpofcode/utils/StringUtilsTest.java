package com.lumpofcode.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest
{

	@Test
	public final void testIsWhiteSpace()
	{
		assertTrue(StringUtils.isWhiteSpace(' '));
		assertTrue(StringUtils.isWhiteSpace(StringUtils.CHAR_TAB));
		assertTrue(StringUtils.isWhiteSpace(StringUtils.CHAR_LF));
		assertTrue(StringUtils.isWhiteSpace(StringUtils.CHAR_CR));
		
		for(char i = '!'; i <= '~'; i += 1)
		{
			assertFalse(StringUtils.isWhiteSpace(i));
		}
	}

	@Test
	public final void testIsQuote()
	{
		assertTrue(StringUtils.isQuote(StringUtils.CHAR_DOUBLE_QUOTE));
		assertTrue(StringUtils.isQuote(StringUtils.CHAR_SINGLE_QUOTE));
		for(char i = 0; i <= 127; i += 1)
		{
			if((i != StringUtils.CHAR_DOUBLE_QUOTE) && (i != StringUtils.CHAR_SINGLE_QUOTE))
			{
				assertFalse(StringUtils.isQuote(i));
			}
		}
	}

	@Test
	public final void testIsEndOfLine()
	{
		assertTrue(StringUtils.isEndOfLine(StringUtils.CHAR_LF));
		assertTrue(StringUtils.isEndOfLine(StringUtils.CHAR_CR));
		for(char i = 0; i <= 127; i += 1)
		{
			if((i != StringUtils.CHAR_LF) && (i != StringUtils.CHAR_CR))
			{
				assertFalse(StringUtils.isEndOfLine(i));
			}
		}
	}

	@Test
	public final void testIsDigit()
	{
		for(char i = '0'; i <= '9'; i += 1)
		{
			assertTrue(StringUtils.isDigit(i));
		}
		for(char i = 0; i <= 127; i += 1)
		{
			if((i < '0') || (i > '9'))
			{
				assertFalse(StringUtils.isDigit(i));
			}
		}

	}

	@Test
	public final void testIsUpper()
	{
		for(char i = 'A'; i <= 'Z'; i += 1)
		{
			assertTrue(StringUtils.isUpper(i));
		}
		for(char i = 0; i <= 127; i += 1)
		{
			if((i < 'A') || (i > 'Z'))
			{
				assertFalse(StringUtils.isUpper(i));
			}
		}
	}

	@Test
	public final void testIsLower()
	{
		for(char i = 'a'; i <= 'z'; i += 1)
		{
			assertTrue(StringUtils.isLower(i));
		}
		for(char i = 0; i <= 127; i += 1)
		{
			if((i < 'a') || (i > 'z'))
			{
				assertFalse(StringUtils.isLower(i));
			}
		}
	}

	@Test
	public final void testIsAlphabetic()
	{
		for(char i = 'a'; i <= 'z'; i += 1)
		{
			assertTrue(StringUtils.isAlphabetic(i));
		}
		for(char i = 'A'; i <= 'Z'; i += 1)
		{
			assertTrue(StringUtils.isAlphabetic(i));
		}
		for(char i = 0; i <= 127; i += 1)
		{
			if(((i < 'a') || (i > 'z')) && ((i < 'A') || (i > 'Z')))
			{
				assertFalse(StringUtils.isAlphabetic(i));
			}
		}
	}

	@Test
	public final void testIsAlphaOrDigit()
	{
		for(char i = '0'; i <= '9'; i += 1)
		{
			assertTrue(StringUtils.isAlphaOrDigit(i));
		}
		for(char i = 'a'; i <= 'z'; i += 1)
		{
			assertTrue(StringUtils.isAlphaOrDigit(i));
		}
		for(char i = 'A'; i <= 'Z'; i += 1)
		{
			assertTrue(StringUtils.isAlphaOrDigit(i));
		}
		for(char i = 0; i <= 127; i += 1)
		{
			if(((i < 'a') || (i > 'z')) && ((i < 'A') || (i > 'Z')) && ((i < '0') || (i > '9')))
			{
				assertFalse(StringUtils.isAlphaOrDigit(i));
			}
		}
	}

	@Test
	public final void testIsAlphaOrDigitOrHyphen()
	{
		assertTrue(StringUtils.isAlphaOrDigitOrHyphen('-'));
		assertTrue(StringUtils.isAlphaOrDigitOrHyphen('_'));	// underscore
		for(char i = '0'; i <= '9'; i += 1)
		{
			assertTrue(StringUtils.isAlphaOrDigitOrHyphen(i));
		}
		for(char i = 'a'; i <= 'z'; i += 1)
		{
			assertTrue(StringUtils.isAlphaOrDigitOrHyphen(i));
		}
		for(char i = 'A'; i <= 'Z'; i += 1)
		{
			assertTrue(StringUtils.isAlphaOrDigitOrHyphen(i));
		}
		for(char i = 0; i <= 127; i += 1)
		{
			if((i != '-') && (i != '_') && ((i < 'a') || (i > 'z')) && ((i < 'A') || (i > 'Z')) && ((i < '0') || (i > '9')))
			{
				assertFalse(StringUtils.isAlphaOrDigitOrHyphen(i));
			}
		}
	}

	@Test
	public final void testTrim()
	{
		assertEquals(StringUtils.trim("nottrimmed"), "nottrimmed");
		assertEquals(StringUtils.trim("  trimmed"), "trimmed");
		assertEquals(StringUtils.trim("trimmed  "), "trimmed");
		assertEquals(StringUtils.trim("  trimmed  "), "trimmed");
		assertEquals(StringUtils.trim("  "), "");
		assertEquals(StringUtils.trim(""), "");
	}

	@Test
	public final void testTrimLeft()
	{
		assertEquals(StringUtils.trimLeft("nottrimmed"), "nottrimmed");
		assertEquals(StringUtils.trimLeft("  trimmed"), "trimmed");
		assertEquals(StringUtils.trimLeft("trimmed  "), "trimmed  ");
		assertEquals(StringUtils.trimLeft("  trimmed  "), "trimmed  ");
		assertEquals(StringUtils.trimLeft("  "), "");
		assertEquals(StringUtils.trimLeft(""), "");
	}

	@Test
	public final void testTrimRight()
	{
		assertEquals(StringUtils.trimRight("nottrimmed"), "nottrimmed");
		assertEquals(StringUtils.trimRight("  trimmed"), "  trimmed");
		assertEquals(StringUtils.trimRight("trimmed  "), "trimmed");
		assertEquals(StringUtils.trimRight("  trimmed  "), "  trimmed");
		assertEquals(StringUtils.trimRight("  "), "");
		assertEquals(StringUtils.trimRight(""), "");
	}

	public static final String digits = "0123456789";
	public static final String lower = "abcdefghijklmnopqrstuvwxyz";
	public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String alphabetic = lower + upper;
	public static final String alphaAndNumeric = digits + alphabetic;
	
	@Test
	public final void testKeepAlphaAndNumeric()
	{
		//
		// should keep all alpha and numeric
		//
		assertEquals(StringUtils.keepAlphaAndNumeric(alphaAndNumeric), alphaAndNumeric);
		
		//
		// should not keep anything else
		//
		final StringBuilder theBuilder = new StringBuilder();
		for(char i = 0; i <= 127; i += 1)
		{
			if(alphaAndNumeric.indexOf(i) < 0)
			{
				theBuilder.append(i);
			}
		}
		final String theNonAlphaOrNumeric = theBuilder.toString();
		assertTrue(StringUtils.keepAlphaAndNumeric(theNonAlphaOrNumeric).isEmpty());
	}

	@Test
	public final void testHasDanglingWhitespace()
	{
		assertTrue(StringUtils.hasDanglingWhitespace("  hasdangles  "));
		assertTrue(StringUtils.hasDanglingWhitespace("hasdangles  "));
		assertTrue(StringUtils.hasDanglingWhitespace("  hasdangles"));
		assertFalse(StringUtils.hasDanglingWhitespace("no   dangles"));
	}

	@Test
	public final void testScanWhitespaceString()
	{
		assertTrue(2 == StringUtils.scanWhitespace("  foo  "));
		assertTrue(0 == StringUtils.scanWhitespace("foo   "));
	}

	@Test
	public final void testScanWhitespaceStringInt()
	{
		assertTrue(2 == StringUtils.scanWhitespace("  foo  ", 0));
		assertTrue(2 == StringUtils.scanWhitespace("  foo  ", 1));
		assertTrue(3 == StringUtils.scanWhitespace("  foo  ", 3));
		assertTrue(7 == StringUtils.scanWhitespace("  foo  ", 5));
		
		assertTrue(2 == StringUtils.scanWhitespace("  foo  ", -1));		// out of range, less than zero starts at zero
		assertTrue(5000 == StringUtils.scanWhitespace("  foo  ", 5000));// out of ranger, greater than length returns given index

	}

	@Test
	public final void testScanNonWhitespaceString()
	{
		assertTrue(3 == StringUtils.scanNonWhitespace("foo  "));
		assertTrue(0 == StringUtils.scanNonWhitespace("  foo  "));
	}

	@Test
	public final void testScanNonWhitespaceStringInt()
	{
		assertTrue(3 == StringUtils.scanNonWhitespace("foo  ", 0));
		assertTrue(3 == StringUtils.scanNonWhitespace("foo  ", 1));
		assertTrue(3 == StringUtils.scanNonWhitespace("foo  ", 3));
		assertTrue(5 == StringUtils.scanNonWhitespace("  foo", 2));
		
		assertTrue(3 == StringUtils.scanNonWhitespace("foo  ", -1));		// out of range, less than zero starts at zero.
		assertTrue(5000 == StringUtils.scanNonWhitespace("  foo", 5000));	// out of range, greater than length returns given index

	}

	@Test
	public final void testScanAlphabeticString()
	{
		assertTrue(0 == StringUtils.scanAlphabetic("01abc  "));
		assertTrue(0 == StringUtils.scanAlphabetic("  01abc"));
		assertTrue(3 == StringUtils.scanAlphabetic("abc01  "));
		assertTrue(3 == StringUtils.scanAlphabetic("abc  01"));
	}

	@Test
	public final void testScanAlphabeticStringInt()
	{
		assertTrue(1 == StringUtils.scanAlphabetic("01abc  ", 1));
		assertTrue(1 == StringUtils.scanAlphabetic("  01abc", 1));
		assertTrue(3 == StringUtils.scanAlphabetic("abc01  ", 1));
		assertTrue(3 == StringUtils.scanAlphabetic("abc  01", 1));
		assertTrue(5 == StringUtils.scanAlphabetic("  abc01", 2));
		assertTrue(5 == StringUtils.scanAlphabetic("01abc  ", 2));
		assertTrue(5 == StringUtils.scanAlphabetic("01abc", 2));
		assertTrue(5 == StringUtils.scanAlphabetic("  abc", 2));
		
		assertTrue(3 == StringUtils.scanAlphabetic("abc123", -1));		// out of range, less than zero starts at zero
		assertTrue(5000 == StringUtils.scanAlphabetic("  abc", 5000));	// out of range, greater than length returns index
		
	}

	@Test
	public final void testScanNumericString()
	{
		assertTrue(0 == StringUtils.scanNumeric("   123abc"));
		assertTrue(0 == StringUtils.scanNumeric("abc123   "));
		assertTrue(3 == StringUtils.scanNumeric("123   "));
		assertTrue(3 == StringUtils.scanNumeric("123abc"));
	}

	@Test
	public final void testScanNumericStringInt()
	{
		assertTrue(1 == StringUtils.scanNumeric("   123abc", 1));
		assertTrue(1 == StringUtils.scanNumeric("abc123   ", 1));
		assertTrue(6 == StringUtils.scanNumeric("   123abc", 3));
		assertTrue(6 == StringUtils.scanNumeric("abc123   ", 3));
		assertTrue(6 == StringUtils.scanNumeric("   123", 3));
		assertTrue(6 == StringUtils.scanNumeric("abc123", 3));
		
		assertTrue(3 == StringUtils.scanNumeric("123abc", -1));			// out of range, less than zero starts at zero
		assertTrue(5000 == StringUtils.scanNumeric("abc123", 5000));	// out of range, greater than length
	}

	@Test
	public final void testScanAlphaAndNumericString()
	{
		assertTrue(0 == StringUtils.scanAlphaAndNumeric("   123abc  "));
		assertTrue(0 == StringUtils.scanAlphaAndNumeric("   abc123   "));
		assertTrue(6 == StringUtils.scanAlphaAndNumeric("123abc   "));
		assertTrue(6 == StringUtils.scanAlphaAndNumeric("abc123   "));
	}

	@Test
	public final void testScanAlphaAndNumericStringInt()
	{
		assertTrue(1 == StringUtils.scanAlphaAndNumeric("   123abc  ", 1));
		assertTrue(1 == StringUtils.scanAlphaAndNumeric("   abc123   ", 1));
		assertTrue(6 == StringUtils.scanAlphaAndNumeric("123abc   ", 1));
		assertTrue(6 == StringUtils.scanAlphaAndNumeric("abc123   ", 1));
		assertTrue(9 == StringUtils.scanAlphaAndNumeric("   123abc  ", 3));
		assertTrue(9 == StringUtils.scanAlphaAndNumeric("   abc123", 3));
		
		assertTrue(6 == StringUtils.scanAlphaAndNumeric("123abc  ", -1));			// out of range, less than zero starts at zero
		assertTrue(5000 == StringUtils.scanAlphaAndNumeric("abc123  ", 5000));	// out of range, greater than length
	}

	@Test
	public final void testScanAlphaNumericString()
	{
		//
		// must begin with letter
		//
		assertTrue(4 == StringUtils.scanAlphaNumeric("a123   "));
		assertTrue(6 == StringUtils.scanAlphaNumeric("abc123   "));
		assertTrue(0 == StringUtils.scanAlphaNumeric("123abc   "));
		assertTrue(0 == StringUtils.scanAlphaNumeric(" abc123"));
	}

	@Test
	public final void testScanAlphaNumericStringInt()
	{
		//
		// must begin with letter
		//
		assertTrue(1 == StringUtils.scanAlphaNumeric("a123   ", 1));
		assertTrue(6 == StringUtils.scanAlphaNumeric("abc123   ", 1));
		assertTrue(1 == StringUtils.scanAlphaNumeric("123abc   ", 1));
		assertTrue(9 == StringUtils.scanAlphaNumeric("   abc123", 3));
		
		assertTrue(6 == StringUtils.scanAlphaNumeric("abc123", -1));		// out of range, less than zero starts at zero
		assertTrue(5000 == StringUtils.scanAlphaNumeric(" abc123", 5000));	// out of ranger, greater than length returns given index
		
	}

	@Test
	public final void testScanAlphaNumericAndHyphenString()
	{
		//
		// must begin with letter
		//
		assertTrue(9 == StringUtils.scanAlphaNumericAndHyphen("a-123-abc   "));
		assertTrue(10 == StringUtils.scanAlphaNumericAndHyphen("a-123-abc-   "));	// hyphen at end is ok
		assertTrue(7 == StringUtils.scanAlphaNumericAndHyphen("abc-123   "));
		assertTrue(0 == StringUtils.scanAlphaNumericAndHyphen("123-abc   "));
		assertTrue(0 == StringUtils.scanAlphaNumericAndHyphen(" abc-123"));
		assertTrue(0 == StringUtils.scanAlphaNumericAndHyphen("-abc-123"));
		
	}

	@Test
	public final void testScanAlphaNumericAndHyphenStringInt()
	{
		//
		// must begin with letter
		//
		assertTrue(1 == StringUtils.scanAlphaNumericAndHyphen("a-123   ", 1));
		assertTrue(7 == StringUtils.scanAlphaNumericAndHyphen("abc-123   ", 1));
		assertTrue(8 == StringUtils.scanAlphaNumericAndHyphen("abc-123-   ", 1));
		assertTrue(1 == StringUtils.scanAlphaNumericAndHyphen("123-abc   ", 1));
		assertTrue(10 == StringUtils.scanAlphaNumericAndHyphen("   abc-123", 3));
		
		assertTrue(7 == StringUtils.scanAlphaNumericAndHyphen("abc-123", -1));		// out of range, less than zero starts at zero
		assertTrue(5000 == StringUtils.scanAlphaNumericAndHyphen(" abc-123", 5000));	// out of ranger, greater than length returns given index
	}

	@Test
	public final void testScanNonAlphaString()
	{
		assertTrue(0 == StringUtils.scanNonAlpha("abc-123 "));
		assertTrue(5 == StringUtils.scanNonAlpha(" 123-abc"));
	}

	@Test
	public final void testScanNonAlphaStringInt()
	{
		assertTrue(1 == StringUtils.scanNonAlpha("abc-123 ", 1));
		assertTrue(5 == StringUtils.scanNonAlpha(" 123-abc", 1));
		assertTrue(5 == StringUtils.scanNonAlpha(" 123-abc", -1));		// out of range, less than zero starts scanning at zero
		assertTrue(5000 == StringUtils.scanNonAlpha(" 123-abc", 5000));	// out of range, greater than length returns given index
	}

	@Test
	public final void testScanNonAlphaAndNumericString()
	{
		assertTrue(0 == StringUtils.scanNonAlphaAndNumeric("abc-123 "));
		assertTrue(1 == StringUtils.scanNonAlphaAndNumeric(" abc-123"));
		assertTrue(1 == StringUtils.scanNonAlphaAndNumeric(" 123-abc"));
	}

	@Test
	public final void testScanNonAlphaAndNumericStringInt()
	{
		assertTrue(1 == StringUtils.scanNonAlphaAndNumeric("abc-123 ", 1));
		assertTrue(1 == StringUtils.scanNonAlphaAndNumeric("123-abc ", 1));
		assertTrue(4 == StringUtils.scanNonAlphaAndNumeric(" -!@abc-123", 1));
		assertTrue(4 == StringUtils.scanNonAlphaAndNumeric(" -!@123-abc", 1));
		assertTrue(4 == StringUtils.scanNonAlphaAndNumeric(" -!@123-abc", -1));		// out of range, less than zero starts scanning at zero
		assertTrue(5000 == StringUtils.scanNonAlphaAndNumeric(" -!@123-abc", 5000));// out of range, greater than length returns given index.
	}

	@Test
	public final void testScanToDelimiter()
	{
		assertTrue(3 == StringUtils.scanToDelimiter("abc,123", 0, ','));
		assertTrue(3 == StringUtils.scanToDelimiter("abc,123", 1, ','));
		assertTrue(3 == StringUtils.scanToDelimiter("abc,123", 3, ','));
		assertTrue(7 == StringUtils.scanToDelimiter("abc,123", 4, ','));
		assertTrue(3 == StringUtils.scanToDelimiter("abc,123", -1, ','));		// out of range, less than zero starts scanning at zero
		assertTrue(5000 == StringUtils.scanToDelimiter("abc,123", 5000, ','));	// out of range, greater than length returns given index.
	}
	
	@Test
	public final void testScanDecimalString()
	{
		assertTrue(3 == StringUtils.scanUnsignedDecimal("123", 0));		// will scan a valid unsigned integer.
		assertTrue(7 == StringUtils.scanUnsignedDecimal("123.456", 0));		// will scan a valid unsigned integer.
		
		// does NOT scan a negation sign (use scanSignedDecimal() for that).
		assertTrue(0 == StringUtils.scanNumeric("-123", 0));		// will scan a valid unsigned integer.
		assertTrue(0 == StringUtils.scanNumeric("-123.456", 0));		// will scan a valid unsigned integer.
		
		assertTrue(0 == StringUtils.scanUnsignedDecimal("   123.456abc", 0));
		assertTrue(10 == StringUtils.scanUnsignedDecimal("   123.456abc", 3));
		assertTrue(0 == StringUtils.scanUnsignedDecimal("abc123.456   ", 0));
		assertTrue(10 == StringUtils.scanUnsignedDecimal("abc123.456   ", 3));
		assertTrue(7 == StringUtils.scanUnsignedDecimal("123.456   ", 0));
		assertTrue(7 == StringUtils.scanUnsignedDecimal("123.456abc", 0));
		
		assertTrue(5 == StringUtils.scanUnsignedDecimal("0.456   ", 0));
		assertTrue(0 == StringUtils.scanUnsignedDecimal(".456   ", 0));		// integer portion must always be present
		assertTrue(3 == StringUtils.scanUnsignedDecimal("123.   ", 0));		// hanging decimal is treated as end of number and not included in character count.
		assertTrue(3 == StringUtils.scanUnsignedDecimal("123.abc", 0));		// hanging decimal is treated as end of number and not included in character count.
		
		assertTrue(7 == StringUtils.scanUnsignedDecimal("123.456.789", 0));	// the .789 is not part of a valid decimal number

	}

	

	@Test
	public final void testParseInteger()
	{
		assertTrue(0 == StringUtils.parseInteger("0", 0, 1));
		assertTrue(1 == StringUtils.parseInteger("01", 1, 2));
		assertTrue(123 == StringUtils.parseInteger(" 123", 1, 4));
		
		//
		// no checking is done on the elements while parsing
		//
		assertFalse(123 == StringUtils.parseInteger("123abc", 0, 6));
		
		//
		// you should can the integer first, then pass the correct range
		//
		final String theString = "123abc";
		assertTrue(123 == StringUtils.parseInteger(theString, 0, StringUtils.scanNumeric(theString)));
		
		try
		{
			// out of range throws an exception
			assertTrue(0 == StringUtils.parseInteger("0", 0, 1000));
			assertTrue(false);		// should not get here
		}
		catch(Exception e)
		{
			assertTrue(true);	// out of range exception
		}
		
		try
		{
			// out of range throws an exception
			assertTrue(0 == StringUtils.parseInteger("0", -1, 1));
			assertTrue(false);		// should not get here
		}
		catch(Exception e)
		{
			assertTrue(true);	// out of range exception
		}
	}

	@Test
	public final void testCompareIgnoreWhitespace()
	{
		assertTrue(0 == StringUtils.compareIgnoreWhitespace("The Quick Brown Fox.", "TheQuickBrownFox."));
		assertTrue(0 == StringUtils.compareIgnoreWhitespace("  The Quick Brown Fox.  ", "TheQuickBrownFox."));
	}

	@Test
	public final void testMergeStringArrayString()
	{
		{
			String[] theArray = {"One", "Two", "Three"};
			assertEquals("One,Two,Three", StringUtils.merge(theArray, ","));
		}
		
		{
			String[] theArray = {"One"};
			assertEquals("One", StringUtils.merge(theArray, ","));
		}
		
		{
			String[] theArray = {};
			assertEquals("", StringUtils.merge(theArray, ","));
		}

		
	}

	@Test
	public final void testMergeStringArrayStringInt()
	{
		{
			String[] theArray = {"One", "Two", "Three"};
			assertEquals("One,Two,Three", StringUtils.merge(theArray, ",", 3));
			assertEquals("One,Two", StringUtils.merge(theArray, ",", 2));
			assertEquals("One", StringUtils.merge(theArray, ",", 1));
			assertEquals("", StringUtils.merge(theArray, ",", 0));
		}
		
		{
			String[] theArray = {"One"};
			assertEquals("One", StringUtils.merge(theArray, ",", 2));	// out of range is handled
		}
		
		{
			String[] theArray = {};
			assertEquals("", StringUtils.merge(theArray, ",", 2));		// out of range is handled
		}
	}

}
