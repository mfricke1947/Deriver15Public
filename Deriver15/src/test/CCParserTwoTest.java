package test;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chArrow;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chHArr;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chIntersection;
import static us.softoption.infrastructure.Symbols.chMemberOf;
import static us.softoption.infrastructure.Symbols.chMinus;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chNotMemberOf;
import static us.softoption.infrastructure.Symbols.chNotSign;
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
import us.softoption.parser.CCParserTwo;
import us.softoption.parser.ParseException;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.TFormula;
import us.softoption.parser.THowsonParser;
import us.softoption.parser.TParser;
import us.softoption.parser.TPriestParser;

/*should run*/

/*tests*/

public class CCParserTwoTest extends TestCase {
	
	TFormula fRoot;
	TParser fParser;
	ArrayList fValuation=new ArrayList();
	Reader fReader = new StringReader("L");
	CCParserTwo fCCParser;
	
	String notAbandBc= "("+chNotSign+"Ab"+ chAnd +"Bc)";


	String allxNotAyandBcOrChookDfhzOrCfofgofdEquivnotd= chUniquant+   "x"+
	   "(("+chNotSign+"Ay"+ chAnd +"Bc)"+
	   chOr +
	   "((C"+ chImplic +"Df(hz))"+
	   chOr +
	   "(Cf(g(d))"+ chEquiv +chNotSign+"D)))";
		
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
	
	String subset = "⊂";
	
	String aSubsetB = "a⊂b";
	String aSubsetU = "a⊂U";
	
	
	
	public void genericPriest(String before){
		fParser=new TPriestParser();		
		String beforeStr= before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParserTwo(sr);
		
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
	
	public void genericOne(String before){
		fParser=new THowsonParser();		
		String beforeStr= before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParserTwo(sr);
		
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
	
public void genericTerm(String before){
		
		fParser=new TDefaultParser();		
		String beforeStr=before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParserTwo(sr);
		
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
	
/******************* parser type 0 ************/

public void testConstant(){
	
	fParser=new TDefaultParser();		
	String beforeStr="5";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParserTwo(sr);
	
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
	
	fParser=new TDefaultParser();		
	String beforeStr="\u2205";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParserTwo(sr);
	
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
	
	fParser=new TDefaultParser();		
	String beforeStr="v";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParserTwo(sr);
	
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
	
	fParser=new TDefaultParser();		
	String beforeStr="(z)";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParserTwo(sr);
	
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
	
	fParser=new TDefaultParser();		
	String beforeStr="z"+ "\u2081"+ "\u2082";
	String afterStr="";		
	java.io.StringReader sr = new java.io.StringReader( beforeStr );
	java.io.Reader r = new java.io.BufferedReader( sr );		
	fCCParser = new CCParserTwo(sr);
	
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

fParser=new TDefaultParser();		
String beforeStr="f(gh)";  //works with comma
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr="<g,h>";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= ""+"\u2118"+"(a)";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= emptyComprehension;
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= nonEmptyComprehension;
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

public void testSubset(){

fParser=new TDefaultParser();		
String beforeStr= aSubsetB;
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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



public void testSubset2(){

fParser=new TParser();		
String beforeStr= "x⊂"+strUniverseSet;
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

public void testSuccessor(){

fParser=new TDefaultParser();		
String beforeStr= "a'";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= "(a''.b)";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= "((a''.b)+(0-1))";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= "(a"+"\u2229"+"b)";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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




//********************  testing WFF************

public void testAtomicTopBottom(){

fParser=new TDefaultParser();		
String beforeStr= "\u22A4";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= "Qabcd";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= "Q"+"(\u2118"+"(d))";   //Power set
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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

fParser=new TDefaultParser();		
String beforeStr= "Q((1+1),<b,c>,"+"\u2118"+"(d))";
String afterStr="";		
java.io.StringReader sr = new java.io.StringReader( beforeStr );
java.io.Reader r = new java.io.BufferedReader( sr );		
fCCParser = new CCParserTwo(sr);

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




/**********************************************/

/******************Parse One****************************/

/*********** This is Howson *******/


public void testOneInfixPredicate(){
	//  /*<term1> =<term2> or whatever
	//  /*<term1> epsilon <setTheoryterm2> or whatever/
	//  /*<term1> not epsilon <setTheoryterm2> SPECIAL CASE
	
	genericOne("1=2");
	genericOne("1"+"\u2208"+"2");
	genericOne("1"+"\u2209"+"2");
	genericOne("1<2");
	genericOne("1>2");

}


 
public void testOneSecondary(){

	genericOne("\u25CA"+"P");
	genericOne("\u25A1"+"P");
	genericOne("F(x)");
	genericOne("\u2203"+"xF(x)");
	genericOne("\u2203"+"x:a(F(x))");  //typed
	genericOne("\u2203!"+"xF(x)");   // unique
	genericOne("\u2203!"+"x:a(F(x))");  //unique typed
	genericOne("\u2200"+"xF(x)");
	genericOne("\u2200"+"x:a(F(x))");  //typed
	genericOne("1>2");
	genericOne("P"+chHArr+"D");
	genericOne("P"+"\u2227"+"D");
	genericOne("P"+"\u2194"+"D"); // OK

}  


public void testOneSubscript2(){
	
	genericOne("C"+ "\u2081" +"\u2082"+"\u2083");


} 



public void testOneMore1(){
	
//	genericOneTerm("(a+1)");
	//genericOneTerm("a+1"); OK
//	genericOne("a=y");
	genericOne("(a+1)=y");
//	genericOne("(x+1)=y");
	
	genericOne("C"+ chHArr +chNotSign+"D");
	genericOne("C(f)"+ chHArr +chNotSign+"D");
	genericOne("C(f(d))"+ chHArr +chNotSign+"D");
//	genericOne("(C)");  OK
//	genericOne("(Cf(d))"+ chHArr +chNotSign+"D");  OK
	genericOne("A"+ chHArr +"B");
//	genericOne("(A"+ chHArr +"B)");  OK
	genericOne("A"+ chHArr+chNotSign +"B");
	genericOne("A(d)"+ chHArr+chNotSign +"B");
	genericOne("A(g(d))"+ chHArr+chNotSign +"B");
//	genericOne("(Ag(d)"+ chHArr+chNotSign +"B)");  OK
	genericOne("C(f(g(d)))"+ chHArr +chNotSign+"D");


	genericOne(oneEqualsTwo);
	genericOne(oneEqualsSuccTwo);
	genericOne(oneEqualsSuccSuccTwo);
	genericOne(oneEquals2Times3Bracket);
	genericOne(oneEqualsSuccTwoTimesThree); 
	genericOne(xEpsilony);   // unique
	//genericOne(xplusEpsilony); OK
	//genericOne(xEpsilonyEpsilonz);  NOT a WFF
	//genericOne(xplusEpsilony);  OK
	genericOne("1>2");
	//genericOne("P&D");  OK
	//genericOne("P.D");  OK
	genericOne("P"+chHArr+"D");
//	genericOne("P"+"\u2194"+"D"); OK
	genericOne("(x+1)=y");
	genericOne(xplusEpsilonyBracket);
	genericOne(xEpsilonyPlus);
	genericOne(xEpsilonyUniony);
	genericOne(xEpsilonyIntersectiony);
	genericOne(xEpsilonEmpty); 
	genericOne(xEpsilonUniverse);

}



public void testOneMore2(){
	genericOne(chUniquant+"xF(x)");
	genericOne(chUniquant+"x:a(F(x))");
	genericOne(xplusEpsilonyBracket); 
	genericOne(xEpsilonyPlus); 
	genericOne(xEpsilonyUniony); 
	genericOne(xEpsilonyIntersectiony); 
	genericOne(xEpsilonEmpty); 
 
	 
	
}



public void testOneTop(){
	genericOne("F(x,y,f(s))"+chHArr+"G(x)");
	genericOne("(F(x)"+chHArr+"G(x))"+chHArr+"H(x)");
	genericOne("F(x)"+chHArr+"(G(x)"+chHArr+"H(x))");
	genericOne("(F(x)"+chAnd+"G(x))"+chOr+"H(x)");
	genericOne("F(x)"+chArrow+"(G(x)"+chHArr+"H(x))");
}



public void testOneNegation(){
	genericOne(chNotSign+"F");
	//genericOne("~~F");  //ok

//	genericOne(chNotSign+chNotSign+"F");  NOT OK!
	genericOne(chNotSign+(chNotSign+"F"));
	
}

public void testOneQuant(){
	genericOne(chUniquant+"x"+chUniquant+"yF(x,y)");
	
	genericOne("F(f(z))");
	genericOne(chUniquant+"x"+chExiquant+"yF(x,g(z))");

//	genericOne(chNotSign+chNotSign+"F");  NOT OK!
	genericOne(chNotSign+(chNotSign+"F"));
	
	genericOne("F"+chArrow+"G");
	
	genericOne("F"+chArrow+"G(y)");
	
	genericOne("F(x)"+chArrow+chUniquant+"yG(y)");
	
	genericOne(chUniquant+"xF(x)"+chArrow+chUniquant+"yG(y)");  //ok
	
	//genericOne(chUniquant+"x(F(x))"+chArrow+chUniquant+"yG(y)");  ok
	
	genericOne("F"+chArrow+chExiquant+"yG"); //no
	
	genericOne("F"+chArrow+chUniquant+"yG"); //no
	
	genericOne(chUniquant+"x(F"+chArrow+chUniquant+"yG)"); //no
	
	genericOne(chUniquant+"x(F(x)"+chArrow+chUniquant+"yG)"); //no
	
	genericOne(chUniquant+"x(F(x)"+chArrow+chUniquant+"yG(y))");
	
	genericPriest("((w+s)+t)=u");
	
	genericOne("((y+s)+t)=u");
	
}

public void testOneModal(){
	genericOne(chModalNecessary+"F(x,y)");
	genericOne(chModalPossible+"G(x,y)");
	genericOne(""+chModalPossible+chModalNecessary+"H(x,y)");
	
	
	//∴∀x□F(x) → □∀xF(x)
	
	genericOne(chUniquant+"x"+chModalNecessary+"F(x)");
	genericOne(chUniquant+"x"+chModalNecessary+"F(x)"+chArrow+chModalNecessary+chUniquant+"x"+"F(x)");
	
	
	

	genericOne("F(f(z))");
	genericOne(chUniquant+"x"+chExiquant+"yF(x,g(z))");

//	genericOne(chNotSign+chNotSign+"F");  NOT OK!
	genericOne(chNotSign+(chNotSign+"F"));
	
	genericOne("F"+chArrow+"G");
	
	genericOne("F"+chArrow+"G(y)");
	
	genericOne("F(x)"+chArrow+chUniquant+"yG(y)");
	
	genericOne(chUniquant+"xF(x)"+chArrow+chUniquant+"yG(y)");  //ok
	
	//genericOne(chUniquant+"x(F(x))"+chArrow+chUniquant+"yG(y)");  ok
	
	genericOne("F"+chArrow+chExiquant+"yG"); //no
	
	genericOne("F"+chArrow+chUniquant+"yG"); //no
	
	genericOne(chUniquant+"x(F"+chArrow+chUniquant+"yG)"); //no
	
	genericOne(chUniquant+"x(F(x)"+chArrow+chUniquant+"yG)"); //no
	
	genericOne(chUniquant+"x(F(x)"+chArrow+chUniquant+"yG(y))");
	
	genericPriest("((w+s)+t)=u");
	
	genericOne("((y+s)+t)=u");
	
}



public void bugs1(){
	
	genericTerm("(a+b)");
	genericTerm("(a-b)");
	genericTerm("(a.b)");
	genericTerm("(a" +chXProd+ "b)");
	
	
	genericPriest("<p,q>" +chMemberOf+ "x");
	genericPriest("<p,q>" +chMemberOf+ "(x+1)");
	
	genericTerm("{}");  //ok
	
//	genericPriest(orderedEpsilonX);  "(a" +chXProd+ "b)"

	
	genericTerm("{}");  //ok
	genericTerm("{a}");  //ok
	genericTerm("{a,b}");  //ok
	
	genericPriest("x=a");
	
	genericPriest("x={a}");

	genericPriest("x" +chMemberOf + "{a}");
	genericOne("x" +chMemberOf + "{a}");
	genericOne("x" +chMemberOf + "{1}");
	genericPriest("x" +chMemberOf + "{1}");
	genericOne(chExiquant+"xF(x)");
	genericOne(chExiquant+"!xF(x)");
	
	genericPriest("("+chExiquant+"x)Fx");
	genericPriest("("+chExiquant+"!x)Fx");
	

	
}


public void testParserTwo(){
	
	genericOne("Hello");
	
	genericOne("Hello(s)");
	
	genericOne("Hell9o(s)");
	
	genericOne("Hell9o(sailor)");
	
	genericOne("x" +chMemberOf + "{aRthur}");
	
	genericOne("Hell9o(sailor,h9)");
	
}

public void testOneAdditional(){


	genericOne("\u2203"+"x:a(F(x))");  //typed
//	genericOne("(\u2203"+"x:a)(F(x))");  //typed


}  

}

