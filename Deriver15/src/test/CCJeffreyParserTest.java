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
import us.softoption.parser.CCParser;
import us.softoption.parser.ParseException;
import us.softoption.parser.TFormula;
import us.softoption.parser.TJeffreyParser;
import us.softoption.parser.TParser;

/*should run*/

/*tests*/

public class CCJeffreyParserTest extends TestCase {
	
	TFormula fRoot;
	TParser fParser;
	ArrayList fValuation=new ArrayList();
	Reader fReader = new StringReader("L");
	CCParser fCCParser;
	
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
	
	
	public void generic(String before){
		genericJeffrey(before);
	}
	
	
	public void genericJeffrey(String before){
		fParser=new TJeffreyParser();		
		String beforeStr= before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
		fCCParser = new CCParser(sr,TJeffreyParser.JEFFREY);
		
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
	
/******************* parser type 0 ************/

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




//********************  testing WFF************

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

genericJeffrey("1=2");
genericJeffrey("1"+"\u2208"+"2");
genericJeffrey("1"+"\u2209"+"2");
genericJeffrey("1<2");
genericJeffrey("1>2");

}



public void testSecondary(){


genericJeffrey(chNotSign+"P");
genericJeffrey("\u25CA"+"P");
genericJeffrey("\u25A1"+"P");
genericJeffrey("Fx");
genericJeffrey("\u2203"+"xFx");
genericJeffrey("\u2203"+"x:aFx");  //typed
genericJeffrey("\u2203"+"xFx");   // unique
genericJeffrey("\u2203"+"x:aFx");  //unique typed
genericJeffrey("\u2200"+"xFx");
genericJeffrey("\u2200"+"x:aFx");  //typed
//genericJeffrey("(x)Fx"); // uniquant no quant
//genericJeffrey("(x:a)Fx");  //typed
genericJeffrey("1>2");
//genericJeffrey("P&D");  OK
//genericJeffrey("P.D");  OK
genericJeffrey("P"+chHArr+"D");
//genericJeffrey("P"+"\u2227"+"D");
//genericJeffrey("P"+"\u2194"+"D");  OK

}  

public void testSubscript2(){

genericJeffrey("C"+ "\u2081" +"\u2082"+"\u2083");


} 

public void testMore1(){

//genericTerm("(a+1)");
//genericTerm("a+1"); OK
//genericJeffrey("a=y");
genericJeffrey("(a+1)=y");
//genericJeffrey("(x+1)=y");

genericJeffrey("C"+ chHArr +chNotSign+"D");
genericJeffrey("Cf"+ chHArr +chNotSign+"D");
genericJeffrey("Cf(d)"+ chHArr +chNotSign+"D");
//genericJeffrey("(C)");  OK
//genericJeffrey("(Cf(d))"+ chHArr +chNotSign+"D");  OK
genericJeffrey("A"+ chHArr +"B");
//genericJeffrey("(A"+ chHArr +"B)");  OK
genericJeffrey("A"+ chHArr+chNotSign +"B");
genericJeffrey("Ad"+ chHArr+chNotSign +"B");
genericJeffrey("Ag(d)"+ chHArr+chNotSign +"B");
//genericJeffrey("(Ag(d)"+ chHArr+chNotSign +"B)");  OK
genericJeffrey("Cf(g(d))"+ chHArr +chNotSign+"D");
//genericJeffrey(allxNotAyandBcOrChookDfhzOrCfofgofdEquivnotd);
genericJeffrey(oneEqualsTwo);
genericJeffrey(oneEqualsSuccTwo);
genericJeffrey(oneEqualsSuccSuccTwo);
genericJeffrey(oneEquals2Times3Bracket);
genericJeffrey(oneEqualsSuccTwoTimesThree); 
genericJeffrey(xEpsilony);   // unique
//genericJeffrey(xplusEpsilony); OK
//genericJeffrey(xEpsilonyEpsilonz);  NOT a WFF
//genericJeffrey(xplusEpsilony);  OK
genericJeffrey("1>2");
//genericJeffrey("P&D");  OK
//genericJeffrey("P.D");  OK
genericJeffrey("P"+chHArr+"D");
//genericJeffrey("P"+"\u2194"+"D"); OK
genericJeffrey("(x+1)=y");
genericJeffrey(xplusEpsilonyBracket);
genericJeffrey(xEpsilonyPlus);
genericJeffrey(xEpsilonyUniony);
genericJeffrey(xEpsilonyIntersectiony);
genericJeffrey(xEpsilonEmpty); 

}

public void testMore2(){
genericJeffrey(chUniquant+"xFx");
genericJeffrey(""+chUniquant+"x:aFx");
//genericJeffrey("(x)Fx"); // OK
//genericJeffrey("(x:a)Fx");  //OK
genericJeffrey(xplusEpsilonyBracket); 
genericJeffrey(xEpsilonyPlus); 
genericJeffrey(xEpsilonyUniony); 
genericJeffrey(xEpsilonyIntersectiony); 
genericJeffrey(xEpsilonEmpty); 

genericJeffrey(chUniquant+"xFx"); 
//genericJeffrey("(x)Fx"); OK

genericJeffrey(chUniquant+"x:aFx"); 
genericJeffrey("Fx"); 
 

}

public void testTop(){
genericJeffrey("Fxyf(s)"+chHArr+"Gx");
genericJeffrey("(Fx"+chHArr+"Gx)"+chHArr+"Hx");
genericJeffrey("Fx"+chHArr+"(Gx"+chHArr+"Hx)");
genericJeffrey("(Fx"+chAnd+"Gx)"+chOr+"Hx");
genericJeffrey("Fx"+chArrow+"(Gx"+chHArr+"Hx)");
}


public void testNegation(){
genericJeffrey(chNotSign+"F");
//genericJeffrey("~~F");  //ok
//genericJeffrey("\u223C"+"\u223C"+"F");
//genericJeffrey(chNotSign+chNotSign+"F");  NOT OK!
genericJeffrey(chNotSign+(chNotSign+"F"));


genericJeffrey("F"+chArrow+"G");

genericJeffrey("F"+chArrow+"Gy");

genericJeffrey("F"+chArrow+chUniquant+"yGy");

genericJeffrey(chUniquant+"x(Fx"+chArrow+chUniquant+"yGy)");

}


/**********************************************/

/******************Parse One****************************/

/*********** This is Howson *******/


public void testOneInfixPredicate(){
	//  /*<term1> =<term2> or whatever
	//  /*<term1> epsilon <setTheoryterm2> or whatever/
	//  /*<term1> not epsilon <setTheoryterm2> SPECIAL CASE
	
	generic("1=2");
	generic("1"+"\u2208"+"2");
	generic("1"+"\u2209"+"2");
	generic("1<2");
	generic("1>2");

}


 
public void testOneSecondary(){

	generic("\u25CA"+"P");
	generic("\u25A1"+"P");
	generic("Fx");
	generic("\u2203"+"xFx");
	generic("\u2203"+"x:aFx");  //typed
	generic("\u2203!"+"xFx");   // unique
	generic("\u2203!"+"x:aFx");  //unique typed
	generic("\u2200"+"xFx");
	generic("\u2200"+"x:aFx");  //typed
	generic("1>2");
	generic("P"+chHArr+"D");
	generic("P"+chAnd+"D");
	generic("P"+"\u2194"+"D"); // OK

}  


public void testOneSubscript2(){
	
	generic("C"+ "\u2081" +"\u2082"+"\u2083");


} 



public void testOneMore1(){
	
//	genericTerm("(a+1)");
	//genericTerm("a+1"); OK
//	generic("a=y");
	generic("(a+1)=y");
//	generic("(x+1)=y");
	
	generic(""+chNotSign+"M");
	
	generic("C"+ chHArr +chNotSign+"D");
	generic("Cf"+ chHArr +chNotSign+"D");
	generic("Cf(d)"+ chHArr +chNotSign+"D");
//	generic("(C)");  OK
//	generic("(Cf(d))"+ chHArr +chNotSign+"D");  OK
	generic("A"+ chHArr +"B");
//	generic("(A"+ chHArr +"B)");  OK
	generic("A"+ chHArr+chNotSign +"B");
	generic("Ad"+ chHArr+chNotSign +"B");
	generic("Ag(d)"+ chHArr+chNotSign +"B");
//	generic("(Ag(d)"+ chHArr+chNotSign +"B)");  OK
	generic("Cf(g(d))"+ chHArr +chNotSign+"D");


	generic(oneEqualsTwo);
	generic(oneEqualsSuccTwo);
	generic(oneEqualsSuccSuccTwo);
	generic(oneEquals2Times3Bracket);
	generic(oneEqualsSuccTwoTimesThree); 
	generic(xEpsilony);   // unique
	//generic(xplusEpsilony); OK
	//generic(xEpsilonyEpsilonz);  NOT a WFF
	//generic(xplusEpsilony);  OK
	generic("1>2");
	//generic("P&D");  OK
	//generic("P.D");  OK
	generic("P"+chHArr+"D");
//	generic("P"+"\u2194"+"D"); OK
	generic("(x+1)=y");
	generic(xplusEpsilonyBracket);
	generic(xEpsilonyPlus);
	generic(xEpsilonyUniony);
	generic(xEpsilonyIntersectiony);
	generic(xEpsilonEmpty); 

}



public void testOneMore2(){
	generic(chUniquant+"xFx");
	generic(chUniquant+"x:aFx");
	generic(xplusEpsilonyBracket); 
	generic(xEpsilonyPlus); 
	generic(xEpsilonyUniony); 
	generic(xEpsilonyIntersectiony); 
	generic(xEpsilonEmpty); 
 
	 
	
}



public void testOneTop(){
	generic("Fxyf(s)"+chHArr+"Gx");
	generic("(Fx"+chHArr+"Gx)"+chHArr+"Hx");
	generic("Fx"+chHArr+"(Gx"+chHArr+"Hx)");
	generic("(Fx"+chAnd+"Gx)"+chOr+"Hx");
	generic("Fx"+chArrow+"(Gx"+chHArr+"Hx)");
}



public void testOneNegation(){
	generic(chNotSign+"F");
	//generic("~~F");  //ok

//	generic(chNotSign+chNotSign+"F");  NOT OK!
	generic(chNotSign+(chNotSign+"F"));
	
}

public void testOneQuant(){
	generic(chUniquant+"x"+chUniquant+"yFxy");
	genericJeffrey("Gf(z)");
	generic("Ff(z)");
	generic(chUniquant+"x"+chExiquant+"yFxg(z)");

//	generic(chNotSign+chNotSign+"F");  NOT OK!
	generic(chNotSign+(chNotSign+"F"));
	
	generic("F"+chArrow+"G");
	
	generic("F"+chArrow+"Gy");
	
	generic("Fx"+chArrow+chUniquant+"yGy");
	
	generic(chUniquant+"xFx"+chArrow+chUniquant+"yGy");  //ok
	
	//generic(chUniquant+"x(Fx)"+chArrow+chUniquant+"yG(y)");  ok
	
	generic("F"+chArrow+chExiquant+"yG"); //no
	
	generic("F"+chArrow+chUniquant+"yG"); //no
	
	generic(chUniquant+"x(F"+chArrow+chUniquant+"yG)"); //no
	
	generic(chUniquant+"x(Fx"+chArrow+chUniquant+"yG)"); //no
	
	generic(chUniquant+"x(Fx"+chArrow+chUniquant+"yGy)");
	
	genericJeffrey("((w+s)+t)=u");
	
	generic("((y+s)+t)=u");
	
}

public void testOneModal(){
	generic(chModalNecessary+"Fxy");
	generic(chModalPossible+"Gxy");
	generic(""+chModalPossible+chModalNecessary+"Hxy");
	
	
	//∴∀x□Fx → □∀xFx
	
	generic(chUniquant+"x"+chModalNecessary+"Fx");
	generic(chUniquant+"x"+chModalNecessary+"Fx"+chArrow+chModalNecessary+chUniquant+"x"+"Fx");
	
	
	
	genericJeffrey("Gf(z)");
	generic("Ff(z)");
	generic(chUniquant+"x"+chExiquant+"yFxg(z)");

//	generic(chNotSign+chNotSign+"F");  NOT OK!
	generic(chNotSign+(chNotSign+"F"));
	
	generic("F"+chArrow+"G");

	
	//generic(chUniquant+"x(Fx)"+chArrow+chUniquant+"yG(y)");  ok
	
	generic("F"+chArrow+chExiquant+"yG"); //no
	
	generic("F"+chArrow+chUniquant+"yG"); //no
	
	generic(chUniquant+"x(F"+chArrow+chUniquant+"yG)"); //no
	
	generic(chUniquant+"x(Fx"+chArrow+chUniquant+"yG)"); //no
	
	generic(chUniquant+"x(Fx"+chArrow+chUniquant+"yGy)");
	
	genericJeffrey("((w+s)+t)=u");
	
	generic("((y+s)+t)=u");
	
}



public void bugs1(){
	
	genericTerm("(a+b)");
	genericTerm("(a-b)");
	genericTerm("(a.b)");
	genericTerm("(a" +chXProd+ "b)");
	
	
	genericJeffrey("<p,q>" +chMemberOf+ "x");
	genericJeffrey("<p,q>" +chMemberOf+ "(x+1)");
	
	genericTerm("{}");  //ok
	
//	genericJeffrey(orderedEpsilonX);  "(a" +chXProd+ "b)"

	
	genericTerm("{}");  //ok
	genericTerm("{a}");  //ok
	genericTerm("{a,b}");  //ok
	
	genericJeffrey("x=a");
	
	genericJeffrey("x={a}");

	genericJeffrey("x" +chMemberOf + "{a}");
	generic("x" +chMemberOf + "{a}");
	generic("x" +chMemberOf + "{1}");
	genericJeffrey("x" +chMemberOf + "{1}");
	generic(chExiquant+"xFx");
	generic(chExiquant+"!xFx");
	
	genericJeffrey("("+chExiquant+"x)Fx");
	genericJeffrey("("+chExiquant+"!x)Fx");
	

	
}

}

