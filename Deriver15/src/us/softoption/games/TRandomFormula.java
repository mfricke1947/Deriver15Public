/*
Copyright (C) 2015 Martin Frické (mfricke@email.arizona.edu https://softoption.us mfricke@softoption.us mfricke1947@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package us.softoption.games;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chUniquant;

import java.io.StringReader;
import java.util.ArrayList;

import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;


/*  We want to produce a random formula (in prop or pred or mixed). We are happy to use A,B,C,a,b,c,
Fx,Gx,Hx,Rxy,Sxy,Txy,w,x,y.

The algorithm might decide the main connective at random, then call itself recursively.

 TFormula has

 public static final short binary=1; // this and following used to identify the kind of node
   public static final short kons=2;
   public static final short equality=3;
   public static final short functor=4;
   public static final short predicator=5;
   public static final short quantifier=6;
   public static final short unary=7;
   public static final short variable=8;

 and we don't want kons which is for LISP


*/

public class TRandomFormula{

 static public TFormula randomPropFormula(int maxConnectives, boolean atomicAllowed){  // we don't usually want atomic at the top level

   /*must be predicator,unary, or binary*/

  short kind;
  String info;

  double random=Math.random();

  if (atomicAllowed&&((maxConnectives<1)||(random<0.2)))   // we want less atomic unless we recurse out
    kind=TFormula.predicator;
  else if (random<0.35)               // there are 5 connectives, want near equal chance
     kind=TFormula.unary;
   else
      kind=TFormula.binary;

  switch (kind) {
    case TFormula.predicator:
      random=Math.random();

      if (random<0.33)
        info="A";
      else if (random<0.66)
        info="B";
      else
        info="C";

      return
          new TFormula(kind,info,null,null);
    case TFormula.unary:
      return
          new TFormula(TFormula.unary,String.valueOf(chNeg),null,randomPropFormula(maxConnectives-1,true));
    case TFormula.binary:

      random=Math.random();

      if (random<0.25)
        info=String.valueOf(chAnd);
      else if (random<0.50)
        info=String.valueOf(chOr);
      else if (random<0.75)
        info=String.valueOf(chImplic);
      else
        info=String.valueOf(chEquiv);

      return
          new TFormula(TFormula.binary,info,randomPropFormula((maxConnectives-1)/2,true),randomPropFormula((maxConnectives-1)/2,true));
  }

   return
       null;  //cannot happen
 }

 static public TFormula randomTruthTableFormula(int maxConnectives, boolean atomicAllowed){  // we don't usually want atomic at the top level

   /*must be predicator,unary, or binary*/

  short kind;
  String info;

  double random=Math.random();

  if (atomicAllowed&&((maxConnectives<1)||(random<0.2)))   // we want less atomic unless we recurse out
    kind=TFormula.predicator;
  else if (random<0.35)           // there are 5 connectives, want near equal chance
     kind=TFormula.unary;
   else
      kind=TFormula.binary;

  switch (kind) {
    case TFormula.predicator:
      random=Math.random();

      if (random<0.5)
        info="T";
      else
        info="F";
      return
          new TFormula(kind,info,null,null);

    case TFormula.unary:
      return
          new TFormula(TFormula.unary,String.valueOf(chNeg),null,randomTruthTableFormula(maxConnectives-1,true));
    case TFormula.binary:

      random=Math.random();

      if (random<0.25)
        info=String.valueOf(chAnd);
      else if (random<0.50)
        info=String.valueOf(chOr);
      else if (random<0.75)
        info=String.valueOf(chImplic);
      else
        info=String.valueOf(chEquiv);

      return
          new TFormula(TFormula.binary,info,randomTruthTableFormula((maxConnectives-1)/2,true),randomTruthTableFormula((maxConnectives-1)/2,true));
  }

   return
       null;  //cannot happen
 }

 static public TFormula randomModalProp(int maxConnectives, boolean atomicAllowed){  // we don't usually want atomic at the top level

	   /*must be predicator,unary, or binary*/

	  short kind;
	  String info;

	  double random=Math.random();

	  if (atomicAllowed&&((maxConnectives<1)||(random<0.2)))   // we want less atomic unless we recurse out
	    kind=TFormula.predicator;
	  else if (random<0.5)  //was 0.35         // there are 7 connectives, want near equal chance
	     kind=TFormula.unary;
	   else
	      kind=TFormula.binary;

	  switch (kind) {
	    case TFormula.predicator:
	      random=Math.random();

	      if (random<0.5)
	        info="T";
	      else
	        info="F";
	      return
	          new TFormula(kind,info,null,null);

	    case TFormula.unary:
	    	random=Math.random();
	    	
	    	if (random<0.33)
	    		return
	    			new TFormula(TFormula.unary,String.valueOf(chNeg),null,randomModalProp(maxConnectives-1,true));
	    	else 
		    	if (random<0.66)
		    		return
		    			new TFormula(TFormula.unary,String.valueOf(chModalPossible),null,randomModalProp(maxConnectives-1,true));
    		
	    	return
    			new TFormula(TFormula.unary,String.valueOf(chModalNecessary),null,randomModalProp(maxConnectives-1,true));

	    case TFormula.binary:

	      random=Math.random();

	      if (random<0.25)
	        info=String.valueOf(chAnd);
	      else if (random<0.50)
	        info=String.valueOf(chOr);
	      else if (random<0.75)
	        info=String.valueOf(chImplic);
	      else
	        info=String.valueOf(chEquiv);

	      return
	          new TFormula(TFormula.binary,info,randomModalProp((maxConnectives-1)/2,true),
	        		  randomModalProp((maxConnectives-1)/2,true));
	  }

	   return
	       null;  //cannot happen
	 }

 static TFormula randomAtomicTerm(boolean constantsOnly,String preferredVariable){

   String info;

   double random=Math.random();

   random=Math.random();

   if (constantsOnly){
     if (random < 0.33)
       info = "a";
     else if (random < 0.67)
       info = "b";
     else
       info = "c";

     return
         new TFormula(TFormula.functor, info, null, null);
   }


   random=Math.random();

   if (!preferredVariable.equals("")&&random<0.91){   // we'll return it 9 out of 10 (+ more chances below
     return
           new TFormula(TFormula.variable,preferredVariable,null,null);

   }

       if (random<0.17)
         info="x";
       else if (random<0.34)
         info="y";
       else if (random<0.50)
         info="z";
       else if (random<0.67)
         info="a";
       else if (random<0.84)
         info="b";
       else
         info="c";

       if (random<0.5)

       return
           new TFormula(TFormula.variable,info,null,null);
     else
       return
           new TFormula(TFormula.functor,info,null,null);

 }



 static TFormula randomPredicator(boolean noPropositions,
                                  boolean unaryPredsOnly,
                                  boolean constantsOnly,
                                  String preferredVariable){

   String info;


   double random=Math.random();

   random=Math.random();

       if (random<0.33)
         info="F";
       else if (random<0.66)
         info="G";
       else
         info="H";

   // now it can be 0-ary, unary or binary


   TFormula outFormula=new TFormula(TFormula.predicator,info,null,null);

   // what we have so far is F,G or H


   random=Math.random();

       if (random<0.2){
         if (!noPropositions)
           ; // do nothing and return F,G or H
         else
           outFormula.appendToFormulaList(randomAtomicTerm(constantsOnly,preferredVariable)); // make it unary eg Fa
       }
       else if ((random<0.60)||unaryPredsOnly)
        outFormula.appendToFormulaList(randomAtomicTerm(constantsOnly,preferredVariable));
       else{
         outFormula.appendToFormulaList(randomAtomicTerm(constantsOnly,preferredVariable));
         outFormula.appendToFormulaList(randomAtomicTerm(constantsOnly,preferredVariable));
       }



     return
           outFormula;

 }

 static TFormula randomQuantifier(int maxConnectives,
                                  boolean noPropositions,
                                  boolean unaryPredsOnly,
                                  boolean noQuantifiers,
                                  boolean constantsOnly,
                                  String preferredVariable){

   String info;


   double random=Math.random();

   random=Math.random();

       if (random<0.5)
         info=String.valueOf(chExiquant);
       else
         info=String.valueOf(chUniquant);

       String variable="";

       random=Math.random();


       if (random<0.33)
             variable="x";
           else if (random<0.66)
             variable="y";
           else
             variable="z";


       while (variable.equals(preferredVariable)){  // if we come in with preferred variable set ie
         random=Math.random();                      // in the scope of another quantifier, we don't want to  use the same variable


       if (random<0.33)
             variable="x";
           else if (random<0.66)
             variable="y";
           else
             variable="z";

       }



       TFormula variableNode = new TFormula(TFormula.variable,
                                               variable, null, null);

       preferredVariable=variable;   // trying to get the scope to use the same variable

      // next bit new Nov 08

       TFormula scope = randomPredFormula((maxConnectives-1)/2,
                                                             true,
                                                             noPropositions,
                                                             unaryPredsOnly,
                                                             noQuantifiers,
                                                             constantsOnly,
                                                             preferredVariable);


     while (!scope.freeTest(variableNode)){  // we don't want vacuous quant, so we'll check that the variable is free
       scope = randomPredFormula((maxConnectives-1)/2,
                                                             true,
                                                             noPropositions,
                                                             unaryPredsOnly,
                                                             noQuantifiers,
                                                             constantsOnly,
                                                             preferredVariable);


     }


return
              new TFormula(TFormula.quantifier, info, variableNode,scope);  //this whole method does not get called if noQuantifiers


 }




 static public TFormula randomPredFormula(int maxConnectives,
                                          boolean atomicAllowed,  // we don't usually want atomic at the top level
                                          boolean noPropositions,
                                          boolean unaryPredsOnly,
                                          boolean noQuantifiers,
                                          boolean constantsOnly,
                                          String preferredVariable){  // once a quantifier is used we prefer the same variable in the scope

    /*must be predicator,unary, or binary, or quantifier*/

   short kind;
   String info;

   double random=Math.random();

   if (atomicAllowed&&((maxConnectives<1)||(random<0.15)))   // we want less atomic unless we recurse out
     kind=TFormula.predicator;
   else if ((random<0.25)               // there are 7 connectives, want slightly less negs or near equal chance
      ||noQuantifiers&&(random<0.30))
     kind=TFormula.unary;
   else if ((random<0.75)               // there are 5 connectives, want near equal chance
         ||noQuantifiers)
     kind=TFormula.binary;
   else
     kind=TFormula.quantifier;           // if noQuantifiers we don't call this


  switch (kind) {
     case TFormula.predicator:
       return
           randomPredicator(noPropositions,unaryPredsOnly,constantsOnly,preferredVariable);

     case TFormula.unary:
       return
           new TFormula(TFormula.unary,String.valueOf(chNeg),null,
                        randomPredFormula(maxConnectives-1,
                                          true,
                                          noPropositions,
                                          unaryPredsOnly,
                                          noQuantifiers,
                                          constantsOnly,
                                          preferredVariable));
     case TFormula.binary:

       random=Math.random();

       if (random<0.25)
         info=String.valueOf(chAnd);
       else if (random<0.50)
         info=String.valueOf(chOr);
       else if (random<0.75)
         info=String.valueOf(chImplic);
       else
         info=String.valueOf(chEquiv);

       return
           new TFormula(TFormula.binary,info,randomPredFormula((maxConnectives-1)/2,
                                                                true,
                                                                noPropositions,
                                                                unaryPredsOnly,
                                                                noQuantifiers,
                                                                constantsOnly,
                                                                preferredVariable),
                                             randomPredFormula((maxConnectives-1)/2,
                                                               true,
                                                               noPropositions,
                                                               unaryPredsOnly,
                                                               noQuantifiers,
                                                               constantsOnly,
                                                               preferredVariable));


     case TFormula.quantifier:
        return
           randomQuantifier(maxConnectives,noPropositions,unaryPredsOnly,noQuantifiers,constantsOnly,preferredVariable);
   }

    return
        null;  //cannot happen
 }



 public static void testStringArray (String[] stringArray){

// use this to test whether any statics you produce are well formed


 boolean wellFormed=false;

 TParser aParser=new TParser();

  int available=stringArray.length;


  for (int i=0;i<available;i++){
    String inStr=stringArray[i];

    TFormula root = new TFormula();
ArrayList dummy = new ArrayList();
StringReader aReader = new StringReader(inStr);
wellFormed = aParser.wffCheck(root, /*dummy,*/aReader);

    if (wellFormed)
  System.out.print("Strings OK");
else
  System.out.print(inStr + " " +"No Good"+ i);

  }

}



 //Library of satisfiable Identity Formulas

 public static String fIdentity []={"a=b",
"(Ha\u2227Pa)\u2227(\u2200x)((Px\u2227Hx)\u2283x=a)",
"(\u2203x)(Px\u2227Hx)",
"(\u2200x)(\u2200y)(((Px\u2227Hx)\u2227(Py\u2227Hy))\u2283x=y)",
"(\u2203x)((Px\u2227Hx)\u2227(\u2200y)((Py\u2227Hy)\u2283y=x))",
"(\u2203x)(\u2203y)((Px\u2227Hx)\u2227(Py\u2227Hy)\u2227\u223C(x=y))",
"(\u2203x)(\u2203y)(((Px\u2227Hx)\u2227(Py\u2227Hy)\u2227\u223C(x=y))\u2227(\u2200z)((Pz\u2227Hz)\u2283((z=x)\u2228(z=y))))" ,
"Ha\u2227(\u2200x)((Px\u2227~(x=a))\u2283Hx)",
"(\u2203x)(((Px\u2227Hx)\u2227(\u2200y)((Py\u2227Hy)\u2283y=x))\u2227Cx)",
"Hf(a)"
};


public static void initializeIdentity(){
  fIdentity= new String[]{"a=b",
"(Ha\u2227Pa)\u2227(\u2200x)((Px\u2227Hx)\u2283x=a)",
"(\u2203x)(Px\u2227Hx)",
"(\u2200x)(\u2200y)(((Px\u2227Hx)\u2227(Py\u2227Hy))\u2283x=y)",
"(\u2203x)((Px\u2227Hx)\u2227(\u2200y)((Py\u2227Hy)\u2283y=x))",
"(\u2203x)(\u2203y)((Px\u2227Hx)\u2227(Py\u2227Hy)\u2227\u223C(x=y))",
"(\u2203x)(\u2203y)(((Px\u2227Hx)\u2227(Py\u2227Hy)\u2227\u223C(x=y))\u2227(\u2200z)((Pz\u2227Hz)\u2283((z=x)\u2228(z=y))))" ,
"Ha\u2227(\u2200x)((Px\u2227~(x=a))\u2283Hx)",
"(\u2203x)(((Px\u2227Hx)\u2227(\u2200y)((Py\u2227Hy)\u2283y=x))\u2227Cx)",
"Hf(a)"
};

}

static public String aRandomSelection(){

      String output="";

      int available=fIdentity.length;

      if (available>0){
        int index=(int)(Math.floor(Math.random() * (available)));

        output=fIdentity[index];  //floor is largest int not larger than

        String [] temp = new String[available-1];

        for (int i=0;i<available;i++){
          if (i<index)
            temp[i]=fIdentity[i];
          if (i>index)
            temp[i-1]=fIdentity[i];    // we'll omit index
        }

        fIdentity=temp;     // remove those already supplied
      }

      return
          output;
  }





 static public TFormula randomSatisfiableIdentityFormula(){
   String formulaStr = aRandomSelection();

   // temporary hack





   boolean wellFormed=false;

   TParser aParser=new TParser();


     TFormula root = new TFormula();
     ArrayList dummy = new ArrayList();
     StringReader aReader = new StringReader(formulaStr);
     wellFormed = aParser.wffCheck(root, /*dummy,*/ aReader);

   if (wellFormed)
     return
         root;
   else
     return
         null;

}


static public int treeBranching(TFormula root){

  // so we can judge the max width of a tree for the root


  switch (TParser.typeOfFormula(root)){


        case TParser.atomic:
        case TParser.negatomic:
          return
              1;


        case TParser.doubleneg: //straight on
          return

           treeBranching(root.fRLink.fRLink);


        case TParser.negarrow: //straight on
        case TParser.nore:

          return
              (treeBranching(root.fRLink.fLLink) * treeBranching(root.fRLink.fRLink));


        case TParser.aand: //straight on
          return
              (treeBranching(root.fLLink) * treeBranching(root.fRLink));

        case TParser.neguni: //straight on
        case TParser.negexi:
        case TParser.notNecessary:
        case TParser.notPossible:

        case TParser.uni:
        case TParser.exi:
        case TParser.typedUni:
        case TParser.typedExi:
        case TParser.unique:

          return

             treeBranching(root.fRLink);



        case TParser.negand:        //split
        case TParser.nequiv:
          return

         (treeBranching(root.fRLink.fLLink) +treeBranching(root.fRLink.fRLink));



        case TParser.implic:       //split
        case TParser.ore:
        case TParser.equivv:

       return

         (treeBranching(root.fLLink) +treeBranching(root.fRLink));


        default:
          ;

      }

      return
          1;

}


}

