package com.lumpofcode.expression.associative;

import com.lumpofcode.collection.list.LinkList;
import com.lumpofcode.expression.ExpressionTreeHelper;
import com.lumpofcode.utils.IntegerTruncateFormatter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ed Murphy on 12/24/2016.
 */
public class AssociativeTreeHelperTest
{
    @Test
    public void generateCommutedAdditionTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 + 3 + 4");

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 6 commutations.", 6, commutations.size());
        assertTrue("2 + 3 + 4 should be generated", commutations.find("2 + 3 + 4").isNotEmpty());
        assertTrue("2 + 4 + 3 should be generated", commutations.find("2 + 4 + 3").isNotEmpty());
        assertTrue("3 + 2 + 4 should be generated", commutations.find("3 + 2 + 4").isNotEmpty());
        assertTrue("3 + 4 + 2 should be generated", commutations.find("3 + 4 + 2").isNotEmpty());
        assertTrue("4 + 2 + 3 should be generated", commutations.find("4 + 2 + 3").isNotEmpty());
        assertTrue("4 + 3 + 2 should be generated", commutations.find("4 + 3 + 2").isNotEmpty());

    }

    @Test
    public void generateCommutedMultiplicationTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 * 3 * 4");

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 2 commutations.", 6, commutations.size());
        assertTrue("2 * 3 * 4 should be generated", commutations.find("2 * 3 * 4").isNotEmpty());
        assertTrue("2 * 4 * 3 should be generated", commutations.find("2 * 4 * 3").isNotEmpty());
        assertTrue("3 * 2 * 4 should be generated", commutations.find("3 * 2 * 4").isNotEmpty());
        assertTrue("3 * 4 * 2 should be generated", commutations.find("3 * 4 * 2").isNotEmpty());
        assertTrue("4 * 2 * 3 should be generated", commutations.find("4 * 2 * 3").isNotEmpty());
        assertTrue("4 * 3 * 2 should be generated", commutations.find("4 * 3 * 2").isNotEmpty());

    }

    @Test
    public void generateCommutedMixedExpressionsTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 * 3 + 4 * 5");

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 8 commutations.", 8, commutations.size());
        assertTrue("2 * 3 + 4 * 5 should be generated", commutations.find("2 * 3 + 4 * 5").isNotEmpty());
        assertTrue("2 * 3 + 5 * 4 should be generated", commutations.find("2 * 3 + 5 * 4").isNotEmpty());
        assertTrue("3 * 2 + 4 * 5 should be generated", commutations.find("3 * 2 + 4 * 5").isNotEmpty());
        assertTrue("3 * 2 + 5 * 4 should be generated", commutations.find("3 * 2 + 5 * 4").isNotEmpty());
        assertTrue("4 * 5 + 2 * 3 should be generated", commutations.find("4 * 5 + 2 * 3").isNotEmpty());
        assertTrue("4 * 5 + 3 * 2 should be generated", commutations.find("4 * 5 + 3 * 2").isNotEmpty());
        assertTrue("5 * 4 + 2 * 3 should be generated", commutations.find("5 * 4 + 2 * 3").isNotEmpty());
        assertTrue("5 * 4 + 3 * 2 should be generated", commutations.find("5 * 4 + 3 * 2").isNotEmpty());

    }

    @Test
    public void generateCommutedParenthesisExpressionsTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 * (3 + 4) * 5");

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 12 commutations.", 12, commutations.size());
        assertTrue("2 * (3 + 4) * 5 should be generated", commutations.find("2 * (3 + 4) * 5").isNotEmpty());
        assertTrue("2 * 5 * (3 + 4) should be generated", commutations.find("2 * 5 * (3 + 4)").isNotEmpty());
        assertTrue("(3 + 4) * 2 * 5 should be generated", commutations.find("(3 + 4) * 2 * 5").isNotEmpty());
        assertTrue("(3 + 4) * 5 * 2 should be generated", commutations.find("(3 + 4) * 5 * 2").isNotEmpty());
        assertTrue("5 * (3 + 4) * 2 should be generated", commutations.find("5 * (3 + 4) * 2").isNotEmpty());
        assertTrue("5 * 2 * (3 + 4) should be generated", commutations.find("5 * 2 * (3 + 4)").isNotEmpty());
        assertTrue("2 * (4 + 3) * 5 should be generated", commutations.find("2 * (4 + 3) * 5").isNotEmpty());
        assertTrue("2 * 5 * (4 + 3) should be generated", commutations.find("2 * 5 * (4 + 3)").isNotEmpty());
        assertTrue("(4 + 3) * 2 * 5 should be generated", commutations.find("(4 + 3) * 2 * 5").isNotEmpty());
        assertTrue("(4 + 3) * 5 * 2 should be generated", commutations.find("(4 + 3) * 5 * 2").isNotEmpty());
        assertTrue("5 * (4 + 3) * 2 should be generated", commutations.find("5 * (4 + 3) * 2").isNotEmpty());
        assertTrue("5 * 2 * (4 + 3) should be generated", commutations.find("5 * 2 * (4 + 3)").isNotEmpty());
    }

    @Test
    public void generateCommutedSubtractionTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 - 3");

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 1 commutations.", 1, commutations.size());
        assertTrue("2 - 3 should be generated", commutations.find("2 - 3").isNotEmpty());
    }

    @Test
    public void generateCommutedDivisionTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 / 3");

        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }

        assertEquals("There should be 1 commutations.", 1, commutations.size());
        assertTrue("2 / 3 should be generated", commutations.find("2 / 3").isNotEmpty());
    }


    //
    // students may or may not parenthesize their expression, so the most general way to check
    // for an equivalent expression is to fully parenthesize the target (correct expression) and
    // the student's expression, then generate all equivalent target expressions and check
    // that the student's expression is one of them.
    // NOTE: this can still fail if the student has parenthesized in a non-standard way.
    //
    @Test
    public void testAreExpressionsEquivalent()
    {
        assertTrue("1 and 1 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1", "1"));
        assertTrue("1 + 2 and 2 + 1 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 + 2", "2 + 1"));
        assertTrue("1 * 2 and 2 * 1 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 + 2", "2 + 1"));
        assertFalse("1 - 2 and 2 - 1 are NOT equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 - 2", "2 - 1"));
        assertFalse("1 / 2 and 2 / 1 are NOT equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 / 2", "2 / 1"));
        assertTrue("(1 + 2) * 3 + 4 and (4 + 3 * (2 + 1)) are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("(1 + 2) * 3 + 4", "(4 + 3 * (2 + 1))"));

        // TODO: write code that can handle unnecessary parenthesis.
        // assertTrue("1 + 2 + 3 + 4 and 1 + (2 + 3) + 4 are equivalent", AssociativeTreeHelper.areExpressionsEquivalent("1 + 2 + 3 + 4", "1 + (2 + 3) + 4"));
    }
    
    @Test
    public void generateCommutedMixedMinusTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 * 3 - 4 * 5");
        
        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }
        
        assertEquals("There should be 4 permutations.", 4, commutations.size());
        assertTrue("2 * 3 - 4 * 5 should be generated", commutations.find("2 * 3 - 4 * 5").isNotEmpty());
        assertTrue("2 * 3 - 5 * 4 should be generated", commutations.find("2 * 3 - 5 * 4").isNotEmpty());
        assertTrue("3 * 2 - 4 * 5 should be generated", commutations.find("3 * 2 - 4 * 5").isNotEmpty());
        assertTrue("3 * 2 - 5 * 4 should be generated", commutations.find("3 * 2 - 5 * 4").isNotEmpty());
    }
    
    @Test
    public void generateCommutedMixedDivideTest()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("2 + 3 / 4 - 5");
        
        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }
        
        assertEquals("There should be 2 permutations.", 2, commutations.size());
        assertTrue("2 + 3 / 4 - 5 should be generated", commutations.find("2 + 3 / 4 - 5").isNotEmpty());
        assertTrue("3 / 4 - 5 + 2 should be generated", commutations.find("3 / 4 - 5 + 2").isNotEmpty());
    }
    
    
    
    @Test
    public void generateCommutedMixedTest_EXC1172()
    {
        LinkList<String> commutations = AssociativeTreeHelper.generateCommutedExpressions("10 * 3 - (2 + 20 * 2)");
        
        for(LinkList<String> commutation = commutations; commutation.isNotEmpty(); commutation = commutation.tail)
        {
            System.out.println(commutation.head);
        }
        
        assertEquals("There should be 8 permutations.", 8, commutations.size());
        assertTrue("10 * 3 - (2 + 20 * 2) should be generated", commutations.find("10 * 3 - (2 + 20 * 2)").isNotEmpty());
        assertTrue("10 * 3 - (2 + 2 * 20) should be generated", commutations.find("10 * 3 - (2 + 2 * 20)").isNotEmpty());
        assertTrue("10 * 3 - (20 * 2 + 2) should be generated", commutations.find("10 * 3 - (20 * 2 + 2)").isNotEmpty());
        assertTrue("10 * 3 - (2 * 20 + 2) should be generated", commutations.find("10 * 3 - (2 * 20 + 2)").isNotEmpty());
        assertTrue("3 * 10 - (2 + 20 * 2) should be generated", commutations.find("10 * 3 - (2 + 20 * 2)").isNotEmpty());
        assertTrue("3 * 10 - (2 + 2 * 20) should be generated", commutations.find("10 * 3 - (2 + 2 * 20)").isNotEmpty());
        assertTrue("3 * 10 - (20 * 2 + 2) should be generated", commutations.find("10 * 3 - (20 * 2 + 2)").isNotEmpty());
        assertTrue("3 * 10 - (2 * 20 + 2) should be generated", commutations.find("10 * 3 - (2 * 20 + 2)").isNotEmpty());
    }
    
    
    /**
     * This test simulates the situation where we have an expression that we want to check
     * against student input.  The student is correct if their expression is
     * equivalent to the checked expression.  We handle this by generating a set of target
     * expressions from the student's expression.
     *
     * This version uses string parsing to remove the extra parenthesis.
     */
    @Test
    public void checkExpressionTest_EXC1294()
    {
        final String theCheckedExpression = "1*2*3/6";
        String theStudentExpression = "2*1*(3/6)";
        
        //
        // generate all permutations of the target expression.
        // these will be fully parenthesized.
        //
        final LinkList<String> theTargetExpressions = AssociativeTreeHelper.generateCommutedExpressions(theStudentExpression);
        for(LinkList<String> theTargetExpression = theTargetExpressions; theTargetExpression.isNotEmpty(); theTargetExpression = theTargetExpression.tail)
        {
            System.out.println(theTargetExpression.head);
        }
        
        //
        // generate a fully parenthesized version of the checked expression.
        // remove the outer parenthesis if they are there.
        //
        theStudentExpression = AssociativeExpressionEvaluator.parse(theCheckedExpression).formatFullParenthesis();
        if('(' == theStudentExpression.charAt(0) && ')' == theStudentExpression.charAt(theStudentExpression.length()- 1))
        {
            theStudentExpression = theStudentExpression.substring(1, theStudentExpression.length() - 1);
        }
        System.out.println();
        System.out.println(theStudentExpression);
        
        //
        // the expressions are equivalent if the fully parenthesized checked expression
        // is in the set of permutations.
        //
        assertTrue("The fully parenthesized checked expression should be one of the permutations", theTargetExpressions.find(theStudentExpression).isNotEmpty());
    }
	
	/**
	 * This test simulates the situation where we have an expression that we want to check
	 * against student input.  The student is correct if their expression is
	 * equivalent to the checked expression.  We handle this by generating a set of target
	 * expressions from the student's expression.
	 *
	 * This version uses the tree structure to remove the extra parenthsis.
	 */
	@Test
	public void checkExpressionTest2_EXC1294()
	{
		final String theCheckedExpression = "1*2*3/6";
		String theStudentExpression = "2*1*(3/6)";
		
		//
		// generate all permutations of the target expression.
		// these will be fully parenthesized.
		//
		final LinkList<String> theTargetExpressions = AssociativeTreeHelper.generateCommutedExpressions(theStudentExpression);
		for(LinkList<String> theTargetExpression = theTargetExpressions; theTargetExpression.isNotEmpty(); theTargetExpression = theTargetExpression.tail)
		{
			System.out.println(theTargetExpression.head);
		}
		
		//
		// generate a fully parenthesized version of the checked expression.
		// remove the outer parenthesis if they are there.
		//
		AssociativeExpressionEvaluator.Expression theParenthesisExpression =
			AssociativeExpressionEvaluator.parse(AssociativeExpressionEvaluator.parse(theCheckedExpression).formatFullParenthesis());
		if(theParenthesisExpression instanceof AssociativeExpressionEvaluator.ParenthesisExpression)
		{
			theParenthesisExpression = ((AssociativeExpressionEvaluator.ParenthesisExpression) theParenthesisExpression).innerExpression();
		}
		theStudentExpression = theParenthesisExpression.format();
		System.out.println();
		System.out.println(theStudentExpression);
		
		//
		// the expressions are equivalent if the fully parenthesized checked expression
		// is in the set of permutations.
		//
		assertTrue("The fully parenthesized checked expression should be one of the permutations", theTargetExpressions.find(theStudentExpression).isNotEmpty());
	}
    
    @Test
    public void checkExpressionTest3_EXC1294()
    {
        final String theCheckedExpression = "1*2*3/5";
        String theStudentExpression = "1*2*(3/5)";
        
        //
        // generate all permutations of the target expression.
        // these will be fully parenthesized.
        //
        final LinkList<String> theTargetExpressions = AssociativeTreeHelper.generateCommutedExpressions(theStudentExpression);
        for(LinkList<String> theTargetExpression = theTargetExpressions; theTargetExpression.isNotEmpty(); theTargetExpression = theTargetExpression.tail)
        {
            System.out.println(theTargetExpression.head);
        }
        
        //
        // generate a fully parenthesized version of the checked expression.
        // remove the outer parenthesis if they are there.
        //
        AssociativeExpressionEvaluator.Expression theParenthesisExpression =
            AssociativeExpressionEvaluator.parse(AssociativeExpressionEvaluator.parse(theCheckedExpression).formatFullParenthesis());
        if(theParenthesisExpression instanceof AssociativeExpressionEvaluator.ParenthesisExpression)
        {
            theParenthesisExpression = ((AssociativeExpressionEvaluator.ParenthesisExpression) theParenthesisExpression).innerExpression();
        }
        theStudentExpression = theParenthesisExpression.format();
        System.out.println();
        System.out.println(theStudentExpression);
        
        //
        // the expressions are equivalent if the fully parenthesized checked expression
        // is in the set of permutations.
        //
        assertTrue("The fully parenthesized checked expression should be one of the permutations", theTargetExpressions.find(theStudentExpression).isNotEmpty());
        
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1*2*3/6", "1*2*(3/6)"));
    }
    
    @Test
    public void checkExpressionTest2b_EXC1294()
    {
        final String theTargetExpression = "3*2*1/6";   // the 'authored' correct target expression
        String theStudentExpression = "2*1*(3/6)";      // the student's answer
    
        assertFalse(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, theStudentExpression));
    
    
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "1/6*3*2"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "1/6*2*3"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "3*2*1/6"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "2*3*1/6"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "3*1/6*2"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "2*1/6*3"));
    
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "(3*2)*1/6"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "(2*3)*1/6"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "1/6*(3*2)"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "1/6*(2*3)"));
    
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "(1/6*3)*2"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "(1/6*2)*3"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "3*(1/6*2)"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "2*(1/6*3)"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "3*(2*1/6)"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "2*(3*1/6)"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "(3*1/6)*2"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent(theTargetExpression, "(2*1/6)*3"));

    }
    
    @Test
    public void areExpressionsEquivalentTest()
    {
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1", "1"));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1", " ( 1 ) "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1", " ((1)) "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2", " 2 + 1 "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2", " (2 + 1) "));
        
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 3 + 2 + 1 "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 1 + 3 + 2  "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 2 + 3 + 1 "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 2 + 1 + 3 "));
    
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " ((2) + ((1) + (3))) "));
    
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " (3 + 2) + 1 "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " (1 + 3) + 2  "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " (2 + 3) + 1 "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " (2 + 1) + 3 "));
    
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 3 + (2 + 1) "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 1 + (3 + 2)  "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 2 + (3 + 1) "));
        assertTrue(AssociativeTreeHelper.areExpressionsEquivalent("1+2+3", " 2 + (1 + 3) "));
    
    
    }
    
}
