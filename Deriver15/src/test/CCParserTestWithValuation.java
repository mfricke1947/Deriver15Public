package test;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chIntersection;
import static us.softoption.infrastructure.Symbols.chMemberOf;
import static us.softoption.infrastructure.Symbols.chMinus;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chNotMemberOf;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chPowerSet;
import static us.softoption.infrastructure.Symbols.chSuperscript1;
import static us.softoption.infrastructure.Symbols.chUnion;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.chXProd;
import static us.softoption.infrastructure.Symbols.strDownTack;
import static us.softoption.infrastructure.Symbols.strEmptySet;
import static us.softoption.infrastructure.Symbols.strUniverseSet;
import static us.softoption.infrastructure.Symbols.strUpTack;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import us.softoption.parser.CCParser;
import us.softoption.parser.ParseException;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

public class CCParserTestWithValuation extends TestCase {
	
	TFormula fRoot;
	TParser fParser;
	ArrayList fValuation=new ArrayList();
	Reader fReader = new StringReader("L");
	CCParser fCCParser;
	
	String notAbandBc= "("+chNeg+"Ab"+ chAnd +"Bc)";


	String allxNotAyandBcOrChookDfhzOrCfofgofdEquivnotd= "("+chUniquant+   "x)"+
	   "(("+chNeg+"Ay"+ chAnd +"Bc)"+
	   chOr +
	   "((C"+ chImplic +"Df(hz))"+
	   chOr +
	   "(Cf(g(d))"+ chEquiv +chNeg+"D)))";
		
	String FZero= "F0";
	String oneEqualsTwo= "1=2";
	String oneEqualsSuccTwo= "1=2'";
	String oneEqualsSuccSuccTwo= "1=2''''";
	String FZeroTimes1= "F(0.1)";
	String oneEquals2Times3= "1=2.3";
	String oneEquals2Times3Bracket= "1=(2.3)";
	String oneEqualsSuccTwoTimesThree= "1=(2'.3)";

	String xEpsilony= "x" +chMemberOf + "y";
	String xEpsilonyEpsilonz= "x" +chMemberOf + "y"+chMemberOf + "z";
	String xplusEpsilony= "x+1" +chMemberOf + "y";
	String xplusEpsilonyBracket= "(x+1)" +chMemberOf + "y";
	String xEpsilonyPlus= "x" +chMemberOf + "(y+1)";
	String xEpsilonyUniony= "x" +chMemberOf + "(y"+chUnion+"z)";
	String xEpsilonyIntersectiony= "x" +chMemberOf + "(y"+chIntersection+"z)";
	String xEpsilonEmpty= "x" +chMemberOf + strEmptySet;
	String xEpsilonUniverse= "x" +chMemberOf + strUniverseSet;
	String xEpsilonEmptyComp= "x" +chMemberOf + "{}";
	String xEpsilonComp1= "x" +chMemberOf + "{1}";
	String xEpsilonComp123= "x" +chMemberOf + "{1,2,3}";
	String Fx = "Fx";
	String xEpsilonCompFx= "x" +chMemberOf + "{x|Fx}";
	String xEpsilonCompFx2= "x" +chMemberOf + "{x:(Fx" +chAnd+ "Gy)}";
	String xNotEpsilonCompFx2= "x" +chNotMemberOf + "{x:(Fx" +chAnd+ "Gy)}";

	String EmptyEpsilonEmpty= strEmptySet +chMemberOf + strEmptySet;
	String CurlyEpsilonEmpty= "{}" +chMemberOf + strEmptySet;
	String CompEpsilonCompFx= "{x|Fx}" +chMemberOf + "{x|Fx}";
	String CompEpsilonCompFx2= "{x:(Fx" +chAnd+ "Gy)}" +chMemberOf + "{x:(Fx" +chAnd+ "Gy)}";
	String CompNotEpsilonCompFx2= "{x:(Fx" +chAnd+ "Gy)}" +chNotMemberOf + "{x:(Fx" +chAnd+ "Gy)}";

	String Extensionality= "("+ chUniquant + "x)(" + chUniquant + "y)((x=y)"+
	                        chEquiv+"("+ chUniquant +"z)((z"+chMemberOf +"x)"+chEquiv+"(z"+chMemberOf +"y)))";
		

	String aMinusb= "(a"+chMinus +"c)=c";

	String xEpsilonPowery= "x" +chMemberOf + chPowerSet + "(y)";
	String xEpsilonPoweryz= "x" +chMemberOf + chPowerSet + "(yz)";

	String FSimpleOrderedPair= "F<p,q>";
	String xEpsilonSimpleOrderedPair= "x" +chMemberOf + "<p,q>";
	String xEpsilonOrderedPair= "x" +chMemberOf + chPowerSet + "(<p,q>)";

	String orderedEpsilonX= "<p,q>" +chMemberOf + "(a" +chXProd+ "b)";

	String copi= "(x)Fx";

	String xEpsilonX= "x" +chMemberOf + "(a" +chXProd+ "b)";

	String upDown= strUpTack+ chEquiv + strDownTack;

	String xSuperscript= "Fx" + chSuperscript1;
	
	String emptyComprehension = "{}";
	String nonEmptyComprehension = "{a,b,c}";
	
	
	
	public void generic(String before){
		fParser=new TParser();		
		String beforeStr= before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		
		try {
			fRoot= fCCParser.wffCheck();
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);
		
	}
	
	public void genericWithValuation(String before){
		fParser=new TParser();		
		String beforeStr= before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		ArrayList<TFormula> valuation= new ArrayList();
		
		try {
			fRoot= fCCParser.wffCheckWithValuation(valuation);
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed with Valuation");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);
		
	}
	
public void genericTerm(String before){
		
		fParser=new TParser();		
		String beforeStr=before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		
		try {
			fRoot= fCCParser.term();
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);	
	}
	

	public void testConstant(){
		
		fParser=new TParser();		
		String beforeStr="5";
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		
		try {
			fRoot= fCCParser.term();
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);	
	}
	
	public void testConstant2(){
		
		fParser=new TParser();		
		String beforeStr="\u2205";
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		
		try {
			fRoot= fCCParser.term();
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);	
	}	
	
	public void testFunctor(){
		
		fParser=new TParser();		
		String beforeStr="v";
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		
		try {
			fRoot= fCCParser.term();
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);	
	}		
	
	public void testInnerTerm(){
		
		fParser=new TParser();		
		String beforeStr="(z)";
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		
		try {
			fRoot= fCCParser.term();
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, "("+afterStr+")");	
	}
	
public void testSubscript(){
		
		fParser=new TParser();		
		String beforeStr="z"+ "\u2081"+ "\u2082";
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr);
		
		try {
			fRoot= fCCParser.term();
	    } catch (ParseException e) {
	        fRoot=null;
	        System.out.println("Not parsed");
	    }
				
		if (fRoot!=null)
			afterStr=fParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);
	    
	  //  System.out.println(afterStr);
	}

public void testFunctions(){
	
	fParser=new TParser();		
	String beforeStr="f(gh)";  //works with comma
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) { 
        fRoot=null;
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
}

public void testOrderedPair(){
	
	fParser=new TParser();		
	String beforeStr="<g,h>";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
    assertEquals(beforeStr, afterStr);
    
}



public void testPowerSet(){
	
	fParser=new TParser();		
	String beforeStr= ""+"\u2118"+"(a)";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
  //      System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
    //System.out.println("After: "+afterStr);
    //System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
    //System.out.println("After: "+afterStr);
}

public void testComprehension1(){
	
	fParser=new TParser();		
	String beforeStr= emptyComprehension;
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
       // System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
    //System.out.println("After: "+afterStr);
    //System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
    //System.out.println("After: "+afterStr);
}



public void testComprehension2(){
	
	fParser=new TParser();		
	String beforeStr= nonEmptyComprehension;
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}

public void testSuccessor(){
	
	fParser=new TParser();		
	String beforeStr= "a'";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
	
	
  // System.out.println("AfterSucc: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}

public void testMult(){
	
	fParser=new TParser();		
	String beforeStr= "(a''.b)";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}

public void testPlusMinus(){
	
	fParser=new TParser();		
	String beforeStr= "((a''.b)+(0-1))";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}



public void testIntersection(){
	
	fParser=new TParser();		
	String beforeStr= "(a"+"\u2229"+"b)";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.term();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}




// ********************  testing WFF************

public void testAtomicTopBottom(){
	
	fParser=new TParser();		
	String beforeStr= "\u22A4";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.wffCheck();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}

public void testPredicate(){
	
	fParser=new TParser();		
	String beforeStr= "Qabcd";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.wffCheck();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}

public void testPredicate2(){
	
	fParser=new TParser();		
	String beforeStr= "Q"+"\u2118"+"(d)";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.wffCheck();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}

public void testPredicate3(){
	
	fParser=new TParser();		
	String beforeStr= "Q(1+1)<b,c>"+"\u2118"+"(d)";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParser(sr);
	
	//System.out.println("Before Str "+beforeStr);
	
	try {
		fRoot= fCCParser.wffCheck();
    } catch (ParseException e) {
        fRoot=null;
        System.out.println("Not parsed");
    }
			
	if (fRoot!=null)
		afterStr=fParser.writeFormulaToString(fRoot);
	
   // System.out.println("After: "+afterStr);
   // System.out.println(fRoot.toString());

    assertEquals(beforeStr, afterStr);
    
  //  System.out.println("After: "+afterStr);
}

public void testInfixPredicate(){
	//  /*<term1> =<term2> or whatever
	//  /*<term1> epsilon <setTheoryterm2> or whatever/
	//  /*<term1> not epsilon <setTheoryterm2> SPECIAL CASE
	
	genericWithValuation("1=2");
	genericWithValuation("1"+"\u2208"+"2");
	genericWithValuation("1"+"\u2209"+"2");
	genericWithValuation("1<2");
	genericWithValuation("1>2");

}


 
public void testSecondary(){

	
	genericWithValuation("\u223C"+"P");
	genericWithValuation("\u25CA"+"P");
	genericWithValuation("\u25A1"+"P");
	genericWithValuation("Fx");
	genericWithValuation("("+"\u2203"+"x)Fx");
	genericWithValuation("("+"\u2203"+"x:a)Fx");  //typed
	genericWithValuation("("+"\u2203!"+"x)Fx");   // unique
	genericWithValuation("("+"\u2203!"+"x:a)Fx");  //unique typed
	genericWithValuation("("+"\u2200"+"x)Fx");
	genericWithValuation("("+"\u2200"+"x:a)Fx");  //typed
	//genericWithValuation("(x)Fx"); // uniquant no quant
	//genericWithValuation("(x:a)Fx");  //typed
	genericWithValuation("1>2");
//	genericWithValuation("P&D");  OK
//	genericWithValuation("P.D");  OK
	genericWithValuation("P"+"\u2261"+"D");
	genericWithValuation("P"+"\u2227"+"D");
//	genericWithValuation("P"+"\u2194"+"D");  OK

}  

public void testSubscript2(){
	
	genericWithValuation("C"+ "\u2081" +"\u2082"+"\u2083");


} 

public void testMore1(){
	
//	genericTerm("(a+1)");
	//genericTerm("a+1"); OK
//	genericWithValuation("a=y");
	genericWithValuation("(a+1)=y");
//	genericWithValuation("(x+1)=y");
	
	genericWithValuation("C"+ chEquiv +chNeg+"D");
	genericWithValuation("Cf"+ chEquiv +chNeg+"D");
	genericWithValuation("Cf(d)"+ chEquiv +chNeg+"D");
//	genericWithValuation("(C)");  OK
//	genericWithValuation("(Cf(d))"+ chEquiv +chNeg+"D");  OK
	genericWithValuation("A"+ "\u2261" +"B");
//	genericWithValuation("(A"+ "\u2261" +"B)");  OK
	genericWithValuation("A"+ "\u2261"+chNeg +"B");
	genericWithValuation("Ad"+ "\u2261"+chNeg +"B");
	genericWithValuation("Ag(d)"+ "\u2261"+chNeg +"B");
//	genericWithValuation("(Ag(d)"+ "\u2261"+chNeg +"B)");  OK
	genericWithValuation("Cf(g(d))"+ chEquiv +chNeg+"D");
	genericWithValuation(allxNotAyandBcOrChookDfhzOrCfofgofdEquivnotd);
	genericWithValuation(oneEqualsTwo);
	genericWithValuation(oneEqualsSuccTwo);
	genericWithValuation(oneEqualsSuccSuccTwo);
	genericWithValuation(oneEquals2Times3Bracket);
	genericWithValuation(oneEqualsSuccTwoTimesThree); 
	genericWithValuation(xEpsilony);   // unique
	//genericWithValuation(xplusEpsilony); OK
	//genericWithValuation(xEpsilonyEpsilonz);  NOT a WFF
	//genericWithValuation(xplusEpsilony);  OK
	genericWithValuation("1>2");
	//genericWithValuation("P&D");  OK
	//genericWithValuation("P.D");  OK
	genericWithValuation("P"+"\u2261"+"D");
//	genericWithValuation("P"+"\u2194"+"D"); OK
	genericWithValuation("(x+1)=y");
	genericWithValuation(xplusEpsilonyBracket);
	genericWithValuation(xEpsilonyPlus);
	genericWithValuation(xEpsilonyUniony);
	genericWithValuation(xEpsilonyIntersectiony);
	genericWithValuation(xEpsilonEmpty); 

}

public void testMore2(){
	genericWithValuation("("+chUniquant+"x)Fx");
	genericWithValuation("("+chUniquant+"x:a)Fx");
//	genericWithValuation("(x)Fx"); // OK
//	genericWithValuation("(x:a)Fx");  //OK
	genericWithValuation(xplusEpsilonyBracket); 
	genericWithValuation(xEpsilonyPlus); 
	genericWithValuation(xEpsilonyUniony); 
	genericWithValuation(xEpsilonyIntersectiony); 
	genericWithValuation(xEpsilonEmpty); 
	
	genericWithValuation("("+chUniquant+"x)Fx"); 
	//genericWithValuation("(x)Fx"); OK
	
	genericWithValuation("("+chUniquant+"x:a)Fx"); 
	genericWithValuation("Fx"); 
	 
	
}

public void testTop(){
	genericWithValuation("Fxyf(s)"+chEquiv+"Gx");
	genericWithValuation("(Fx"+chEquiv+"Gx)"+chEquiv+"Hx");
	genericWithValuation("Fx"+chEquiv+"(Gx"+chEquiv+"Hx)");
	genericWithValuation("(Fx"+chAnd+"Gx)"+chOr+"Hx");
	genericWithValuation("Fx"+chImplic+"(Gx"+chEquiv+"Hx)");
}	
	
	
public void testNegation(){
	genericWithValuation(chNeg+"F");
	genericWithValuation(chNeg+""+chNeg+"F");
	//genericWithValuation("~~~~~F");
	genericWithValuation(chNeg+(chNeg+"F"));
	genericWithValuation("\u25CA"+"\u25CA"+chNeg+chNeg+"F");
	genericWithValuation("\u25CA"+"\u25CA"+"\u223C"+"\u223C"+"F");
	genericWithValuation("\u223C"+"\u223C"+"F");
	

	
	
	
}	
	
public void testTypeComprehension(){
	genericWithValuation("("+chExiquant+"x)F");
	genericWithValuation("("+chExiquant+"x:t)Fx");  //CompEpsilonCompFx
	//genericWithValuation("("+chExiquant+"x:"+"{}"+")Fx");
	//genericWithValuation("("+chExiquant+"x:"+"{a,b,c}"+")Fx");
	genericWithValuation("("+chExiquant+"x:"+"{x:Fx}"+")Fx"); //{x:(Fx" +chAnd+ "Gy)}
	genericWithValuation("("+chExiquant+"x:"+"{x:(Fx" +chAnd+ "Gy)}"+")Fx");

	genericWithValuation("("+chUniquant+"x)F");
	genericWithValuation("("+chUniquant+"x:t)Fx");  //CompEpsilonCompFx
	//genericWithValuation("("+chUniquant+"x:"+"{}"+")Fx");
	genericWithValuation("("+chUniquant+"x:"+"{x:Fx}"+")Fx"); //{x:(Fx" +chAnd+ "Gy)}
	genericWithValuation("("+chUniquant+"x:"+"{x:(Fx" +chAnd+ "Gy)}"+")Fx");	

	
	
	
}		
	
public void testWithVal(){
//	genericWithValuation("F[a/x,b/y,c/z]");  This is good.


	
	
	
}	

}
