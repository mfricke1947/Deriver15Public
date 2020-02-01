package test;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chIntersection;
import static us.softoption.infrastructure.Symbols.chKappa;
import static us.softoption.infrastructure.Symbols.chMemberOf;
import static us.softoption.infrastructure.Symbols.chMinus;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalNecessary2;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chModalPossible2;
import static us.softoption.infrastructure.Symbols.chModalPossible3;
import static us.softoption.infrastructure.Symbols.chModalPossible4;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chNotMemberOf;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chPowerSet;
import static us.softoption.infrastructure.Symbols.chRho;
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

import junit.framework.TestCase;
import us.softoption.parser.TFormula;
import us.softoption.parser.TGirleParser;

public class TGirleParserTest extends TestCase {
	
	TFormula fRoot;
	TGirleParser fTGirleParser;
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
	
	String emptyComprehension = "{}";
	String nonEmptyComprehension = "{a,b,c}";
	
	/************* Modal *********/
	
	String necessP = chModalNecessary +"P";
	String necessnecessP = chModalNecessary +""+chModalNecessary2 +"P";
	String nec2P = chModalNecessary2 +"P";
	String necnecP = chModalNecessary +""+chModalNecessary2 +"P";
	String possP = chModalNecessary +""+chModalNecessary2 +"P";
	String notnotP = chNeg+""+chNeg+"P";
	String possMnayP = chModalPossible +""+
	chModalPossible2 +""+
	chModalPossible3 +""+
	chModalPossible4+""+
	chModalNecessary2 +"P";
	
	/************* Kappa *********/
	
	String kappaXP = chKappa +"xP";
	String rhoXP = chRho +"xP";
	String rhoTP = chRho +"tP";

	
	
	public void generic(String before){
		fTGirleParser=new TGirleParser();		
		String beforeStr= before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
	//	fCCParser = new CCParser(sr);
		
		fRoot= new TFormula();
		

			
			if (fTGirleParser.wffCheck (fRoot,/* new ArrayList(),*/r))
				;
			else
	        System.out.println("Not parsed");
	
				
		if (fRoot!=null)
			afterStr=fTGirleParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);
		
	}
	
	public void genericTerm(String before){
		fTGirleParser=new TGirleParser();		
		String beforeStr= before;
		String afterStr="";		
		java.io.StringReader sr = new java.io.StringReader( beforeStr );
		java.io.Reader r = new java.io.BufferedReader( sr );		
	//	fCCParser = new CCParser(sr);
		
		fRoot= new TFormula();
		

			
			if (fTGirleParser.term (fRoot, r))
				;
			else
	        System.out.println("Not parsed");
	
				
		if (fRoot!=null)
			afterStr=fTGirleParser.writeFormulaToString(fRoot);

	    assertEquals(beforeStr, afterStr);
		
	}
	
	
	public void testMore1(){
	/*	
		generic("A");
	
		
		
//		genericTerm("(a+1)");
		//genericTerm("a+1"); OK
//		generic("a=y");
		generic("(a+1)=y");
//		generic("(x+1)=y");
		
		generic("C"+ chEquiv +chNeg+"D");
		generic("Cf"+ chEquiv +chNeg+"D");
		generic("Cf(d)"+ chEquiv +chNeg+"D");
//		generic("(C)");  OK
//		generic("(Cf(d))"+ chEquiv +chNeg+"D");  OK
		generic("A"+ "\u2261" +"B");
//		generic("(A"+ "\u2261" +"B)");  OK
		generic("A"+ "\u2261"+chNeg +"B");
		generic("Ad"+ "\u2261"+chNeg +"B");
		generic("Ag(d)"+ "\u2261"+chNeg +"B");
//		generic("(Ag(d)"+ "\u2261"+chNeg +"B)");  OK
		generic("Cf(g(d))"+ chEquiv +chNeg+"D");
//		generic(allxNotAyandBcOrChookDfhzOrCfofgofdEquivnotd); OK
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
		generic("P"+"\u2261"+"D");
//		generic("P"+"\u2194"+"D"); OK
		generic("(x+1)=y");
		generic(xplusEpsilonyBracket);
		generic(xEpsilonyPlus);
		generic(xEpsilonyUniony);
		generic(xEpsilonyIntersectiony);
		generic(xEpsilonEmpty); 
		*/
	}
	
	public void testModal(){
		
		generic("H");
		
		generic("Q");
	
		generic(necessP);
		generic(necessnecessP);
		generic(necnecP);
		generic(nec2P);
		generic(possP);
		
		generic(notnotP);
		
		generic(possMnayP);
	
	}
	
	public void testKappa(){
		
		generic(kappaXP);
		generic(rhoXP);
		//generic(rhoTP);
		
		generic(chRho+""+"aP");
		
	generic(chRho+""+"tP");
		generic(chRho+"t("+chRho+"tP)");
		
		//generic("~(P)"); OK
		
		//generic(chRho+"t("+chRho+"tP)");
		
		generic("Taa");
		
		//generic("Ta(");
	//	generic("TaT");
		
	//	generic("T(");
		
		generic(chNeg+"("+chKappa+"mP)");
		
		generic("P{x|Px}");
		
		generic("1=2");
		
		generic("1>2");
		
		generic("1<2");
	
	}
	
	public void testTer(){
		
		//For example, 5, ∅, a, w, b₁₂, z₁₂₁₂, (5),  5''' , 1+2, 2-3, x.2, a∪b, x∩y, are all terms.
	
		genericTerm("a");
		genericTerm("x");
	//	genericTerm("a+x");  ok
		genericTerm("(a+x)"); 
		
		genericTerm("0"); 
		genericTerm("5"); 
		genericTerm("\u2205"); 
		genericTerm("Ü"); 
		genericTerm("0'"); 
		genericTerm("b₁₂'"); 
		genericTerm("z₁₂₁₂'"); 
		//genericTerm("(5)"); ok
	//	genericTerm("1+2'"); ok
		genericTerm("(a∪b)'"); 
		genericTerm("(x∩y')");
		genericTerm("f(abc)");
	//	genericTerm("f(a,b,c)"); ok
		
		genericTerm("℘(x)");
		
		genericTerm("<x,y>");
		genericTerm("{x:Pa}");
		genericTerm("{x|Px}");
		
		
	}


}
