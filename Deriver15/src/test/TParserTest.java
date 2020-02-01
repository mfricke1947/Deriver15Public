package test;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chIntersection;
import static us.softoption.infrastructure.Symbols.chKappa;
import static us.softoption.infrastructure.Symbols.chLambda;
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
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

public class TParserTest extends TestCase {
	
ArrayList fValuation=new ArrayList();
TFormula fRoot = new TFormula();
Reader fReader = new StringReader("L");


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

/*	protected void setUp() {

    }  */
	
public void testParse(){
	
	TParser aParser=new TParser();
	
	String beforeStr="("+chNeg+"A"+ chAnd +"B)"+
						chOr +
	                 "((C"+ chImplic +"D)"+
	                 chOr +
	                 "(C"+ chEquiv +chNeg+"D))";
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
	
}
	
public void testParse2(){
	
	TParser aParser=new TParser();
	
	String beforeStr="("+chNeg+"Ab"+ chAnd +"Bc)"+
	chOr +
    "((C"+ chImplic +"Df(hc))"+
    chOr +
    "(Cf(g(d))"+ chEquiv +chNeg+"D))";
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
	
}

public void testParse3(){
	
	TParser aParser=new TParser();
	
	String beforeStr= "("+chUniquant+   "x)"+
		"(("+chNeg+"Ab"+ chAnd +"Bc)"+
	chOr +
    "((C"+ chImplic +"Df(hc))"+
    chOr +
    "(Cf(g(d))"+ chEquiv +chNeg+"D)))";
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
	
}

public void testParse4(){
	
	TParser aParser=new TParser();
	
	String beforeStr= "("+chUniquant+   "x)"+
		"(("+chNeg+"Ab"+ chAnd +"Bc)"+
	chOr +
    "((C"+ chImplic +"Df(hc))"+
    chOr +
    "(Cf(g(d))"+ chEquiv +chNeg+"D)))";
	
	String bareBeforeStr= "("+chUniquant+   "x)"+
	"(("+chNeg+"Ab"+ chAnd +"Bc)"+
chOr +
"((C"+ chImplic +"Df(hc))"+
chOr +
"(Cf(g(d))"+ chEquiv +chNeg+"D)))";
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(bareBeforeStr, afterStr);
	
}

public void testVariablesInFormula(){
	
	TParser aParser=new TParser();
	
	String beforeStr= "("+chUniquant+   "x)"+
		"(("+chNeg+"Ay"+ chAnd +"Bc)"+
	chOr +
    "((C"+ chImplic +"Df(hz))"+
    chOr +
    "(Cf(g(d))"+ chEquiv +chNeg+"D)))";
	
/*	String bareBeforeStr= "("+chUniquant+   "x)"+
	"(("+chNeg+"Ab"+ chAnd +"Bc)"+
chOr +
"((C"+ chImplic +"Df(hc))"+
chOr +
"(Cf(g(d))"+ chEquiv +chNeg+"D)))"; */
	
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader)){

	
	Set <String> variables=aParser.variablesInFormula(fRoot);
	
	Iterator<String> iter=variables.iterator();
	
	while (iter.hasNext())
		afterStr+=iter.next();
	
	//afterStr=aParser.variablesInFormula(fRoot);
	
}

    assertEquals("xyz", afterStr);
	
}

public void testConstantsInFormula(){
	
	TParser aParser=new TParser();
	
	String beforeStr= allxNotAyandBcOrChookDfhzOrCfofgofdEquivnotd;
	
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader)){

	
	afterStr=aParser.constantsInFormula(fRoot);
	
}

    assertEquals("chd", afterStr);
	
}

public void testConstantsInListOfFormulas(){
	TParser aParser=new TParser();
	
	ArrayList list=new ArrayList();
	
	assertEquals("", aParser.constantsInListOfFormulas(list));
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(allxNotAyandBcOrChookDfhzOrCfofgofdEquivnotd);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader)){
	
	list.add(fRoot);
}
	
	assertEquals("cdh", aParser.constantsInListOfFormulas(list));
	
	list.add(fRoot);
	
	assertEquals("cdh", aParser.constantsInListOfFormulas(list));
	
	fRoot = new TFormula();
	fReader = new StringReader(notAbandBc);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader)){
	
	list.add(fRoot);
}
	
	
assertEquals("bcdh", aParser.constantsInListOfFormulas(list));
	
	
}

public void testLambdaNamesInFormula(){
	
	TParser aParser=new TParser();
	
	
	// (&lambda;x.(&lambda;y.x a) b)
	
	String beforeStr= "("+chLambda+   "x.("+chLambda+   "y.2 a) b)";
/*		"(("+chNeg+"Ay"+ chAnd +"Bc)"+
	chOr +
    "((C"+ chImplic +"Df(hz))"+
    chOr +
    "(Cf(g(d))"+ chEquiv +chNeg+"D)))"; */
	
/*	String bareBeforeStr= "("+chUniquant+   "x)"+
	"(("+chNeg+"Ab"+ chAnd +"Bc)"+
chOr +
"((C"+ chImplic +"Df(hc))"+
chOr +
"(Cf(g(d))"+ chEquiv +chNeg+"D)))"; */
	
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.lambdaWffCheck(fRoot,fValuation, fReader)){

	
	afterStr=aParser.lambdaNamesInFormula(fRoot);
	
}

    assertEquals("xyab", afterStr);  //omits the 2
	
}

/******************* First Order Theories *******************/

public void testFirstOrder(){
	
	TParser aParser=new TParser();
	
	String beforeStr= FZero;
	
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /******************/
    
	beforeStr= oneEqualsTwo;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
	
	beforeStr= oneEqualsSuccTwo;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    

	
	/************/

	beforeStr= oneEqualsSuccSuccTwo;
	
	afterStr="";
    
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /*****************/
    
	beforeStr= FZeroTimes1;
	
	afterStr="";

	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /**************/
    
   beforeStr= oneEquals2Times3;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals("1=(2.3)", afterStr);
    
	beforeStr= oneEqualsSuccTwoTimesThree;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    
  
    
    
}

/***************** Set Theory *******************/

public void testSetTheory(){
	
	TParser aParser=new TParser();
	
	String beforeStr= xEpsilony;
	
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
  /***************/  
   
	aParser=new TParser();
	
	beforeStr= xEpsilonyEpsilonz;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals("", afterStr);  //HERE
   
    /******************/
    
	beforeStr= xplusEpsilony;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(xplusEpsilonyBracket, afterStr);
    
    /******************/
    
	beforeStr= xEpsilonyPlus;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /*******************/

    
    
	beforeStr= xEpsilonyUniony;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(xEpsilonyUniony, afterStr);
    
    /*******************/


    
	beforeStr= xEpsilonyIntersectiony;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(xEpsilonyIntersectiony, afterStr);
    
    /*******************/

    
    
	beforeStr= xEpsilonEmpty;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(xEpsilonEmpty, afterStr);
    
    /*******************/
    
	beforeStr= xEpsilonUniverse;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /*******************/   
    
	beforeStr= xEpsilonEmptyComp;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /*******************/   
    
	beforeStr= xEpsilonComp1;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /****************************/
    
	beforeStr= xEpsilonComp123;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
   
    /****************************/
    
	beforeStr= Fx;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /****************************/
    
	beforeStr= xEpsilonCompFx;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr); 
    
    /****************************/
    
	beforeStr= xEpsilonCompFx2;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr); 
    
    /****************************/
    
	beforeStr= xNotEpsilonCompFx2;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr); 
    

   
    /****************************/
 
	beforeStr= Extensionality;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

//System.out.print(beforeStr+ strCR);
//System.out.print(afterStr);

    assertEquals(beforeStr, afterStr);  
    /****************************/
    
  	beforeStr= EmptyEpsilonEmpty;
  	
  	afterStr="";
  	
  	fValuation=new ArrayList();
  	fRoot = new TFormula();
  	fReader = new StringReader(beforeStr);
  	
  if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
  	afterStr=aParser.writeFormulaToString(fRoot);



      assertEquals(beforeStr, afterStr);
      /****************************/
      
    	beforeStr= CurlyEpsilonEmpty;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);


        assertEquals(beforeStr, afterStr);
        
       // String CompEpsilonCompFx= "{x|Fx}" +chMemberOf + "{x|Fx}";
      //  String CompEpsilonCompFx2= "{x:(Fx" +chAnd+ "Gy)}" +chMemberOf + "{x:(Fx" +chAnd+ "Gy)}";
      //  String CompNotEpsilonCompFx2= "{x:(Fx" +chAnd+ "Gy)}" +chNotMemberOf + "{x:(Fx" +chAnd+ "Gy)}";
        /****************************/
        
    	beforeStr= CompEpsilonCompFx;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);
 
        /****************************/
        
    	beforeStr= CompEpsilonCompFx2;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);      

        /****************************/
        
    	beforeStr= CompNotEpsilonCompFx2;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);   
        
        /****************************/
        
    	beforeStr= aMinusb;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);
        
        /****************************/  
        
    	beforeStr= xEpsilonPowery;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);
        
    	beforeStr= xEpsilonPoweryz;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals("", afterStr);  //illformed  
        
        /****************************/  
        
    	beforeStr= FSimpleOrderedPair;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);
  /****************************/  
        
    	beforeStr= xEpsilonSimpleOrderedPair;
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);
        
        /****************************/
    	beforeStr= xEpsilonOrderedPair; 
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);
        
/****************************/
        
    	beforeStr= orderedEpsilonX; 
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals(beforeStr, afterStr);
        
/****************************/
        
    	beforeStr= copi; 
    	
    	afterStr="";
    	
    	fValuation=new ArrayList();
    	fRoot = new TFormula();
    	fReader = new StringReader(beforeStr);
    	
    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
    	afterStr=aParser.writeFormulaToString(fRoot);



        assertEquals("("+chUniquant+"x)Fx", afterStr);
}

public void testSetTheoryFail(){
	
	TParser aParser=new TParser();
	
	/****************************/
	String beforeStr= xEpsilonOrderedPair; 
	    	
	String afterStr="";
	    	
	    	fValuation=new ArrayList();
	    	fRoot = new TFormula();
	    	fReader = new StringReader(beforeStr);
	    	
	    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	    	afterStr=aParser.writeFormulaToString(fRoot);



	        assertEquals(beforeStr, afterStr);
    
  /***************/ 
	        /****************************/
	        
	    	beforeStr= xEpsilonX; 
	    	
	    	afterStr="";
	    	
	    	fValuation=new ArrayList();
	    	fRoot = new TFormula();
	    	fReader = new StringReader(beforeStr);
	    	
	    if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	    	afterStr=aParser.writeFormulaToString(fRoot);



	        assertEquals(beforeStr, afterStr);
}


/***************** Epistemic *******************/

public void testEpistemic(){
	
	TParser aParser=new TParser();
	
	String beforeStr= chKappa+"a"+"P";
	
	String afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
  /***************/  
   
	aParser=new TParser();
	
	beforeStr= "";
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /***************/  
    
	aParser=new TParser();
	
/*	beforeStr= chKappa+
	           "a"+
	           "(P)";
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);  */

    /***************/  
    
	aParser=new TParser();
	
	beforeStr= chKappa+
	           "a"+
	           "("+chUniquant+   "x)"+
	   		"(("+chNeg+"Ab"+ chAnd +"Bc)"+
	   	chOr +
	       "((C"+ chImplic +"Df(hc))"+
	       chOr +
	       "(Cf(g(d))"+ chEquiv +chNeg+"D)))";
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
    /***************/  
    
	aParser=new TParser();
	
	beforeStr= chKappa+
	           "a("+
	           chKappa+"bP)";
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);   

    /***************/  
    
/*	aParser=new TParser();
	
	beforeStr= chKappa+
	           "a"+
	           chKappa+"bP";
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);  */
    
    /***************/  
    
	aParser=new TParser();
	
	beforeStr= "Eaccess(abc)";
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr); 
    
/***************/  
    
	aParser=new TParser();
	
	beforeStr= "Eaccess(abc)";
	
	afterStr="";
	
	afterStr=aParser.writeFormulaToString(aParser.makeAnEAccessRelation("a","b","c"));

    assertEquals(beforeStr, afterStr); 

/***************/  
    
	aParser=new TParser();
	
	beforeStr= upDown;
	
	afterStr="Fail";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, */fReader))
	   afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr); 


/***************/  
    
	/*aParser=new TParser();
	
	beforeStr= xSuperscript;
	
	afterStr="";
	
	fValuation=new ArrayList();
	fRoot = new TFormula();
	fReader = new StringReader(beforeStr);
	
if	(aParser.wffCheck(fRoot,/*fValuation, fReader))
	   afterStr=aParser.writeFormulaToString(fRoot);

    assertEquals(beforeStr, afterStr);
    
  System.out.print(beforeStr+ strCR);
  System.out.print(afterStr); */

}




	
}
