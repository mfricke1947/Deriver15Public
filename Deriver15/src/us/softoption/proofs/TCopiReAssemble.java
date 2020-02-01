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

package us.softoption.proofs;

//import javax.swing.UIManager;
//import java.awt.*;
//import javax.swing.*;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strCR;

import java.util.ArrayList;

import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;
//import javax.swing.tree.*;
import us.softoption.infrastructure.FunctionalParameter;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TUtilities;

import us.softoption.interpretation.TTestNode;
//import us.softoption.editor.*;
//import java.awt.Toolkit;

/*This is for reassembling a proof from closed TestNode*/

/******* in Copi To do
 *
 * never have Absurd
 *
 * */




class TCopiReAssemble extends THausmanReAssemble/*TReAssemble*/{


  String fAbsJustification=TCopiProofPanel.absJustification;  //Absorption




  TCopiReAssemble(TParser aParser, TTestNode aTestNode, ArrayList aHead,
            int aLastAssIndex) {
  super(aParser,aTestNode,aHead,aLastAssIndex);




}



    /************* Proofline factory ************/

  /* we want this to subclass for other types of proof eg Copi */

  public TProofline supplyProofline(){
     return
         new TCopiProofline(fParser);
  }

  public TReAssemble supplyTReAssemble(TTestNode aTestNode, ArrayList aHead,
              int aLastAssIndex){
return
   new TCopiReAssemble(fParser, aTestNode, aHead, aLastAssIndex);
}


  /************* End of Proofline factory ************/



  /****************** AtomicS (Node type 3) *************************************/




  /****************** End of AtomicS (Node type 3) ******************************/






  /****************** Equivv (Node type 20) *************************************/


  /******************   EquivI ***************************
    *
    *  Similar to super but we extra lines
    *
    *
    *
    * Equiv, Commute, Simp.

     */


   void doEquivv(TReAssemble leftReAss) {
       fHead = leftReAss.fHead;
       fLastAssIndex = leftReAss.fLastAssIndex;

       TFormula AarrowB = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));
       TFormula BarrowA = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));

       TFormula AarrowBAndBarrowA = new TFormula(TFormula.binary,
                                                 String.valueOf(chAnd),
                                                 AarrowB,
                                                 BarrowA);
       TFormula BarrowAAndAarrowB = new TFormula(TFormula.binary,
                                                 String.valueOf(chAnd),
                                                 BarrowA,
                                                 AarrowB);
       TFormula AequivB = new TFormula(TFormula.binary,
                                                 String.valueOf(chEquiv),
                                                 AarrowB.fLLink,
                                                 AarrowB.fRLink);

       int dummy1 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                          leftReAss.fLastAssIndex, AarrowB); //not sure
       int dummy2 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                          leftReAss.fLastAssIndex, BarrowA);

       if (dummy1 != -1) {                        //A->B

         prependToHead(AarrowBAndBarrowA.copyFormula());

         if (transfer(TTestNode.aand, AarrowB)) // {Moves the left implic it justifies into body of proof}
           ;

          numberLines();

         prependToHead(AequivB.copyFormula());


         if (transfer(copiEquiv, AarrowBAndBarrowA))
           ;

         numberLines();

       }


       if (dummy2 != -1) {                        //B->A

         prependToHead(BarrowAAndAarrowB.copyFormula());

         if (transfer(TTestNode.aand, BarrowA)) // {Moves the left implic it justifies into body of proof}
           ;

         numberLines();

         prependToHead(AarrowBAndBarrowA.copyFormula());

         if (transfer(copiComm, BarrowAAndAarrowB)) // {Moves the left implic it justifies into body of proof}
           ;

         numberLines();


         prependToHead(AequivB.copyFormula());


         if (transfer(copiEquiv, AarrowBAndBarrowA))
           ;

         numberLines();

       }

  }






 /****************** End of Equivv (Node type 20) *************************************/













  class SimplificationCommTest implements FunctionalParameter{   //nextInList(ArrayList list,FunctionalParameter object,Object parameter, int index)

TFormula fConclusion;

public SimplificationCommTest(TFormula conclusion){
  fConclusion=conclusion;
}

public void execute (Object parameter){
  };


//the test is that the conclusion is the left conjunct of an and

public boolean testIt(Object parameter){

  if (fParser.isAnd((TFormula)parameter)&&
      fConclusion.equalFormulas(fConclusion,((TFormula)parameter).fRLink))  // (F^G) equal to G
         return
             true;

  return
      false;
}
}

  boolean tryAbs(ArrayList antecedents, TFormula conclusion){
      //trying Absoption (CD).   need to find (F->G)therefore (F->F.G)

    if (fParser.isImplic(conclusion)&&
        fParser.isAnd(conclusion.fRLink)) {



       TFormula implic = new TFormula(TFormula.binary,
                                        String.valueOf(chImplic),
                                        conclusion.fLLink,
                                        conclusion.fRLink.fRLink);

       if (implic.formulaInList(antecedents)) {

          doAbsDirect(implic, conclusion);
             return
                true;
       }

}
     return
           false;
  }


  boolean tryDeMorgan(ArrayList antecedents, TFormula conclusion){
      //need to find ~(F^G)therefore (~Fv~G) or
      // ~(FvG)therefore (~F.~G)

    if (fParser.isOr(conclusion)&&
        fParser.isNegation(conclusion.fLLink)&&
        fParser.isNegation(conclusion.fRLink)) {

       TFormula neg = new TFormula(TFormula.unary,
                                        String.valueOf(chNeg),
                                        null,
                                        new TFormula(TFormula.binary,
                                                     String.valueOf(chAnd),
                                                     conclusion.fLLink.fRLink,
                                                     conclusion.fRLink.fRLink));

       if (neg.formulaInList(antecedents)) {

          doDirect(neg, conclusion,fDeMo);
             return
                true;
       }

     }


     if (fParser.isAnd(conclusion)&&
         fParser.isNegation(conclusion.fLLink)&&
         fParser.isNegation(conclusion.fRLink)) {

        TFormula neg = new TFormula(TFormula.unary,
                                         String.valueOf(chNeg),
                                         null,
                                         new TFormula(TFormula.binary,
                                                      String.valueOf(chOr),
                                                      conclusion.fLLink.fRLink,
                                                      conclusion.fRLink.fRLink));

        if (neg.formulaInList(antecedents)) {

           doDirect(neg, conclusion,fDeMo);
              return
                 true;
        }

     }


// now right to left

     if (fParser.isNegation(conclusion)&&
         fParser.isAnd(conclusion.getRLink())) {

        TFormula or = new TFormula(TFormula.binary,
                                         String.valueOf(chOr),
                                         new TFormula(TFormula.unary,
                                                      String.valueOf(chNeg),
                                                      null,
                                                      conclusion.fRLink.fLLink),
                                         new TFormula(TFormula.unary,
                                                      String.valueOf(chNeg),
                                                      null,
                                                      conclusion.fRLink.fRLink));

        if (or.formulaInList(antecedents)) {

           doDirect(or, conclusion,fDeMo);
              return
                 true;
        }

     }

     if (fParser.isNegation(conclusion)&&
    fParser.isOr(conclusion.getRLink())) {

   TFormula and = new TFormula(TFormula.binary,
                                    String.valueOf(chAnd),
                                    new TFormula(TFormula.unary,
                                                 String.valueOf(chNeg),
                                                 null,
                                                 conclusion.fRLink.fLLink),
                                    new TFormula(TFormula.unary,
                                                 String.valueOf(chNeg),
                                                 null,
                                                 conclusion.fRLink.fRLink));

   if (and.formulaInList(antecedents)) {

      doDirect(and, conclusion,fDeMo);
         return
            true;
   }

}








     return
           false;
  }


  boolean tryDS(ArrayList antecedents, TFormula conclusion){
      //trying Disjunctive Syllogism.   need to find both FvG and ~F

       boolean leftToRight=true;

       if (tryDSInner(antecedents, conclusion,leftToRight))
          return
             true;
 /*
       if (tryDSInner(antecedents, conclusion,!leftToRight))
          return
             true;  with COpi we don't go R to L */
  return
           false;
}

boolean tryCD(ArrayList antecedents, TFormula conclusion){
    //trying Constructive Dilemma (CD).   need to find both (F->G)&(R->S), (F vR) therefore GVS

  if (fParser.isOr(conclusion)) {
  CDTest cdTest = new CDTest(conclusion); // will find FvG
  Object conjunct = null;
  Object disjunctObj = null;

  TFormula disjunctF = null;

  conjunct = TUtilities.nextInList(antecedents, cdTest, 0);

  boolean bothFound = false;

  while ( (conjunct != null) && !bothFound) { //now we look for F

    disjunctF = new TFormula(
        TFormula.binary,
        String.valueOf(chOr),
        ( (TFormula) conjunct).fLLink.fLLink,
        ( (TFormula) conjunct).fRLink.fLLink);

    ThereTest thereTest = new ThereTest(disjunctF);

    disjunctObj = TUtilities.nextInList(antecedents, thereTest, 0);

    if (disjunctObj != null)
      bothFound = true;
    else {
      int start = antecedents.indexOf(conjunct) + 1;
      conjunct = TUtilities.nextInList(antecedents, cdTest, start); // try further along the list
    }
  }

  if (bothFound) {
    doCDDirect( (TFormula) conjunct, disjunctF,conclusion);
    return
        true;
  }
}
return
    false;

}


  void doAbsDirect(TFormula disjunct,TFormula conclusion){
     TProofline newline = supplyProofline();

   newline.fLineno = 1;
   newline.fFormula = disjunct.copyFormula();
   newline.fJustification = fAssJustification;

   if (fHead==null)
      fHead=new ArrayList();

   fHead.add(0,newline);
   fLastAssIndex=0;


   newline = supplyProofline();

 newline.fLineno = 2;
 newline.fFirstjustno = 1;
 newline.fFormula = conclusion.copyFormula();

 newline.fJustification = fAbsJustification;

 fHead.add(newline);

 //    Toolkit.getDefaultToolkit().beep();  //debug


}

void doDirect(TFormula antecedent,TFormula conclusion,String justification){
     TProofline newline = supplyProofline();

   newline.fLineno = 1;
   newline.fFormula = antecedent.copyFormula();
   newline.fJustification = fAssJustification;

   if (fHead==null)
      fHead=new ArrayList();

   fHead.add(0,newline);
   fLastAssIndex=0;

   newline = supplyProofline();

 newline.fLineno = 2;
 newline.fFirstjustno = 1;
 newline.fFormula = conclusion.copyFormula();

 newline.fJustification = justification;

 fHead.add(newline);

}

  void doCDDirect(TFormula conjunct, TFormula disjunct,TFormula conclusion){
     TProofline newline = supplyProofline();

   newline.fLineno = 1;
   newline.fFormula = conjunct.copyFormula();
   newline.fJustification = fAssJustification;

   if (fHead==null)
      fHead=new ArrayList();

   fHead.add(0,newline);
   fLastAssIndex=0;

   newline = supplyProofline();

   newline.fLineno = 2;
   newline.fFormula = disjunct.copyFormula();
   newline.fJustification = fAssJustification;

   fHead.add(newline);
   fLastAssIndex+=1;

   newline = supplyProofline();

 newline.fLineno = 3;
 newline.fFirstjustno = 1;
 newline.fSecondjustno = 2;
 newline.fFormula = conclusion.copyFormula();

 newline.fJustification = fCDJustification;

 fHead.add(newline);

 //    Toolkit.getDefaultToolkit().beep();  //debug


}


  void doHSDirect(TFormula implic, TFormula implic2){
   TProofline newline = supplyProofline();

 newline.fLineno = 1;
 newline.fFormula = implic.copyFormula();
 newline.fJustification = fAssJustification;

 if (fHead==null)
    fHead=new ArrayList();

 fHead.add(0,newline);
 fLastAssIndex=0;

 newline = supplyProofline();

 newline.fLineno = 2;
 newline.fFormula = implic2.copyFormula();
 newline.fJustification = fAssJustification;

 fHead.add(newline);
 fLastAssIndex+=1;

 newline = supplyProofline();

newline.fLineno = 3;
newline.fFirstjustno = 1;
newline.fSecondjustno = 2;
newline.fFormula = new TFormula( TFormula.binary,
                                 String.valueOf(chImplic),
                                 ((TFormula) implic).fLLink.copyFormula(),
                                 ((TFormula) implic2).fRLink.copyFormula());

newline.fJustification = fHSJustification;

fHead.add(newline);

 //  Toolkit.getDefaultToolkit().beep();  //debug


}

  void doDSDirect(TFormula disjunct, TFormula negF){
     TProofline newline = supplyProofline();

   newline.fLineno = 1;
   newline.fFormula = disjunct.copyFormula();
   newline.fJustification = fAssJustification;

   if (fHead==null)
      fHead=new ArrayList();

   fHead.add(0,newline);
   fLastAssIndex=0;

   newline = supplyProofline();

   newline.fLineno = 2;
   newline.fFormula = negF.copyFormula();
   newline.fJustification = fAssJustification;

   fHead.add(newline);
   fLastAssIndex+=1;

   newline = supplyProofline();

 newline.fLineno = 3;
 newline.fFirstjustno = 1;
 newline.fSecondjustno = 2;
 newline.fFormula = ((TFormula) disjunct).fRLink.copyFormula();

 newline.fJustification = fDSJustification;

 fHead.add(newline);

 //    Toolkit.getDefaultToolkit().beep();  //debug


}



  void doMTDirect(TFormula implic, TFormula negG){
     TProofline newline = supplyProofline();

   newline.fLineno = 1;
   newline.fFormula = implic.copyFormula();
   newline.fJustification = fAssJustification;

   if (fHead==null)
      fHead=new ArrayList();

   fHead.add(0,newline);
   fLastAssIndex=0;

   newline = supplyProofline();

   newline.fLineno = 2;
   newline.fFormula = negG.copyFormula();
   newline.fJustification = fAssJustification;

   fHead.add(newline);
   fLastAssIndex+=1;

   newline = supplyProofline();

 newline.fLineno = 3;
 newline.fFirstjustno = 1;
 newline.fSecondjustno = 2;
 newline.fFormula = new TFormula(TFormula.unary,
                                 String.valueOf(chNeg),
                                 null,
                                 ((TFormula) implic).fLLink.copyFormula());

 newline.fJustification = fMTJustification;

 fHead.add(newline);

 //    Toolkit.getDefaultToolkit().beep();  //debug


}

  void doSimpCommDirect(TFormula conj, TFormula conclusion){
    TProofline newline = supplyProofline();

  newline.fLineno = 1;
  newline.fFormula = conj.copyFormula();          //  (F^G)
  newline.fJustification = fAssJustification;

  if (fHead==null)
     fHead=new ArrayList();

  fHead.add(0,newline);
  fLastAssIndex=0;

  newline = supplyProofline();

newline.fLineno = 2;
newline.fFirstjustno = 1;
newline.fFormula = new TFormula(TFormula.binary,     //(G^F)
                                  conj.fInfo,
                                  conj.fRLink.copyFormula(),
                                  conj.fLLink.copyFormula());
newline.fJustification = fCommJustification;

fHead.add(newline);


  newline = supplyProofline();                    //G

  newline.fLineno = 3;
  newline.fFirstjustno = 2;
  newline.fFormula = conclusion.copyFormula();
  newline.fJustification = fAndEJustification;

  fHead.add(newline);



}


/* An optimization on the arrow split from super */

void optimizeImplicTollens(TTestNode proveNotBTest,TFormula arrowFormula,TReAssemble leftReAss){  //I think this is done
     /*
      The branches split with ~A on left and B on right, and we can derive ~B

      */

     TReAssemble proveNotB = supplyTReAssemble( proveNotBTest, null, 0);

     proveNotB.reAssembleProof(); //proof of notB, notB is last line}

     fHead = proveNotB.fHead; //transfer it to this, our main proof
     fLastAssIndex = proveNotB.fLastAssIndex;

     prependToHead(arrowFormula);

     numberLines();           //now we have a proof with A->B at line 1 and ~B as last line

     TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 1));

     TFormula A=arrowFormula.fLLink;
     TFormula B=arrowFormula.fRLink;
     TFormula notA= new TFormula(TFormula.unary,
                                String.valueOf(chNeg),
                                null,
                                A);



     TProofline notALine = supplyProofline();
     notALine.fLineno = templateLine.fLineno+1;
     notALine.fFormula = notA.copyFormula();
     notALine.fSubprooflevel = templateLine.fSubprooflevel;
     notALine.fJustification = fMTJustification;
     notALine.fFirstjustno = 1;
     notALine.fSecondjustno=templateLine.fLineno;

     fHead.add(notALine);


  //////////////  the first ends in notA the second starts with notA



     leftReAss.prependToHead(arrowFormula);

     if (leftReAss.transfer(TTestNode.atomic, notA)){

        TMergeData mergeData = new TMergeData(this, leftReAss);

        mergeData.merge();

        fHead = mergeData.firstLocalHead;
        fLastAssIndex = mergeData.firstLastAssIndex;

        numberLines();

        int secondNotAIndex=fHead.indexOf(notALine) +1;

        TProofListModel.reNumSingleLine (fHead, secondNotAIndex, mergeData.firstLineNum);

        fHead.remove(secondNotAIndex);

        numberLines();

        }
    }



class AbsTest implements FunctionalParameter{  // (F->G) from conclusion F->F.G

TFormula fDisjunct;
TFormula fConjunct=null;
int index=-1;

public AbsTest(TFormula disjunct){
  fDisjunct=disjunct;
}

public void execute (Object parameter){
  };


//the test is that G=G and S=S in (F->G)&(R->S) from conclusion GVS

public boolean testIt(Object parameter){

  if (fParser.isAnd((TFormula)parameter)&&
      fDisjunct.equalFormulas(fDisjunct.fLLink,((TFormula)parameter).fLLink.fRLink)&&
      fDisjunct.equalFormulas(fDisjunct.fRLink,((TFormula)parameter).fRLink.fRLink)){
      fConjunct=(TFormula)parameter;

         return
             true;}

  return
      false;
}
}


/*check super to improve this */
class CDTest implements FunctionalParameter{  // (F->G)&(R->S) from conclusion GVS

TFormula fDisjunct;
TFormula fConjunct=null;
int index=-1;

public CDTest(TFormula disjunct){
  fDisjunct=disjunct;
}

public void execute (Object parameter){
  };


//the test is that G=G and S=S in (F->G)&(R->S) from conclusion GVS

public boolean testIt(Object parameter){

  if (fParser.isAnd((TFormula)parameter)&&
      fDisjunct.equalFormulas(fDisjunct.fLLink,((TFormula)parameter).fLLink.fRLink)&&
      fDisjunct.equalFormulas(fDisjunct.fRLink,((TFormula)parameter).fRLink.fRLink)){
      fConjunct=(TFormula)parameter;

         return
             true;}

  return
      false;
}
}



class DSTest implements FunctionalParameter{

TFormula fGClause;
TFormula fDisjunct=null;
int index=-1;

public DSTest(TFormula g){
  fGClause=g;
}

public void execute (Object parameter){
  };


//the test is that the ifClause is the right part of an implic

public boolean testIt(Object parameter){

  if (fParser.isOr((TFormula)parameter)&&
      fGClause.equalFormulas(fGClause,((TFormula)parameter).fRLink)){
      fDisjunct=(TFormula)parameter;

         return
             true;}

  return
      false;
}
}

/*
class MTTest implements FunctionalParameter{

TFormula fIfClause;
TFormula fImplic=null;
int index=-1;

public MTTest(TFormula ifC){
  fIfClause=ifC;
}

public void execute (Object parameter){
  };


//the test is that the ifClause is the right part of an implic

public boolean testIt(Object parameter){

  if (fParser.isImplic((TFormula)parameter)&&
      fIfClause.equalFormulas(fIfClause,((TFormula)parameter).fLLink)){
      fImplic=(TFormula)parameter;

         return
             true;}

  return
      false;
}
}

 */

  class HSTest implements FunctionalParameter{  // has to find an implic F->?

  TFormula fIfClause;
  TFormula fImplic=null;
  int index=-1;

  public HSTest(TFormula ifC){
    fIfClause=ifC;
  }

  public void execute (Object parameter){
    };


//the test is that the ifClause is the right part of an implic

  public boolean testIt(Object parameter){

    if (fParser.isImplic((TFormula)parameter)&&
        fIfClause.equalFormulas(fIfClause,((TFormula)parameter).fLLink)){
        fImplic=(TFormula)parameter;

           return
               true;}

    return
        false;
  }
}


boolean doDirectStepOptimization(){
  if (super.doDirectStepOptimization()){

    if (TConstants.DEBUG){
      System.out.print(strCR + "Optimized by super  " + fTestNode.fStepType +
                   strCR);
      System.out.print(strCR + "Steptype: " + fTestNode.fStepType +
                   strCR);
      }


    return
        true;
  }
  else{


    ArrayList antecedents = fTestNode.fAntecedents;
    ArrayList succedent = fTestNode.fSuccedent;

    if ( (antecedents.size() > 0) && (succedent.size() > 0)) {
      TFormula conclusion = (TFormula) succedent.get(0);


      //trying Simp.Commute

     SimplificationCommTest test = new SimplificationCommTest(conclusion);
     Object found;

     found= TUtilities.nextInList(antecedents,test,0);

    if (found!=null){
      doSimpCommDirect( (TFormula) found, conclusion);
      return
          true;
    }


    //trying ModusTollens.   need to find both F->G and ~G

     if (fParser.isNegation(conclusion)) {

       ImplicIfTest mtTest = new ImplicIfTest(conclusion.fRLink);  // will find F->G
       Object implic = null;
       Object negGObj = null;

       TFormula negG=null;

       implic = TUtilities.nextInList(antecedents, mtTest, 0);

       boolean bothFound = false;

       while ( (implic != null) && !bothFound) { //now we look for F

         negG=new TFormula(
                                                TFormula.unary,
                                                String.valueOf(chNeg),
                                                null,
                                               ((TFormula) implic).fRLink);


         ThereTest thereTest = new ThereTest(negG);

         negGObj= TUtilities.nextInList(antecedents,thereTest,0);

         if (negGObj != null)
           bothFound = true;
         else {
           int start = antecedents.indexOf(implic) + 1;
           implic = TUtilities.nextInList(antecedents, mtTest, start); // try further along the list
         }
       }

       if (bothFound) {
         doMTDirect( (TFormula) implic, negG);
         return
             true;
       }
     }


     //trying HS.   need to find both F->G and G->H

          if (fParser.isImplic(conclusion)) {

            TFormula F = conclusion.fLLink;
            TFormula H= conclusion.fRLink;

            HSTest hsTest = new HSTest(conclusion.fLLink);  // will find F->G, actually F->? because we don't know the G
            Object implic = null;
            Object gAObj = null;

            TFormula gImplicH=null;   //g->H

            implic = TUtilities.nextInList(antecedents, hsTest, 0);

            boolean bothFound = false;

            while ( (implic != null) && !bothFound) { //now we look for F

              gImplicH=new TFormula(
                                                     TFormula.binary,
                                                     String.valueOf(chImplic),
                                                     ((TFormula) implic).fRLink,  //G
                                                     H);  //H


              ThereTest thereTest = new ThereTest(gImplicH);

              gAObj= TUtilities.nextInList(antecedents,thereTest,0);

              if (gAObj != null)
                bothFound = true;
              else {
                int start = antecedents.indexOf(implic) + 1;
                implic = TUtilities.nextInList(antecedents, hsTest, start); // try further along the list
              }
            }

            if (bothFound) {
              doHSDirect( (TFormula) implic, gImplicH);
              return
                  true;
            }
     }


        if (tryDS(antecedents,conclusion))
          return
              true;

        if (tryCD(antecedents,conclusion))
          return
              true;

        if (tryAbs(antecedents,conclusion))
                 return
                     true;

        if (tryDeMorgan(antecedents,conclusion))
          return
              true;

    }
  }

  if (TConstants.DEBUG){
System.out.print(strCR + "Not successfully optimized  " + fTestNode.fStepType +
               strCR);
System.out.print(strCR + "Steptype: " + fTestNode.fStepType +
               strCR);
}



   return
       false;
}



  void doAand(TReAssemble leftReAss) {
    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;

    TFormula leftFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));
    TFormula rightFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));

    int dummy1 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                       leftReAss.fLastAssIndex, leftFormula); //not sure
    int dummy2 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                       leftReAss.fLastAssIndex, rightFormula);

    if (dummy1 != -1) {                      // if it is the left formula
      TFormula formulanode = new TFormula();

      formulanode.fKind = TFormula.binary;
      formulanode.fInfo = String.valueOf(chAnd);
      formulanode.fLLink = leftFormula;
      formulanode.fRLink = rightFormula;

      prependToHead(formulanode);

      if (transfer(fTestNode.fStepType, leftFormula)) // {Moves the left conjunct it justifies into body of proof}
        ;
      numberLines();
    }

    if (dummy2 != -1) {     //right conjunct
  TFormula commute = new TFormula();

  commute.fKind = TFormula.binary;
  commute.fInfo = String.valueOf(chAnd);
  commute.fLLink = rightFormula;
  commute.fRLink = leftFormula;

  prependToHead(commute);                        //R^L

  if (transfer(fTestNode.fStepType, rightFormula))
    ;

  numberLines();

  TFormula formulanode = new TFormula();       //L^R

formulanode.fKind = TFormula.binary;
formulanode.fInfo = String.valueOf(chAnd);
formulanode.fLLink = leftFormula;
formulanode.fRLink = rightFormula;

prependToHead(formulanode);

if (transfer(copiComm, commute)) // {Moves the commute And it justifies into body of proof}
  ;

numberLines();

}


  }



    /*  I don't think we need break dummy because there are no subproofs in Copi


      procedure BreakDummy (var localHead: TList);

       var
        newline, templateline: TProofline;
               {The dummy contradiction has to be removed and its just nos used}
               {but sometime it occurs directly after a subproof in  which case it}
               {has to be split up and put together again first.The last line is usually a blank.}

      begin
       templateline := TProofline(localHead.At(localHead.fSize - 1));
       newline := nil;

       SupplyProofline(newline); {new A}
       with newline do
        begin
         fLineno := templateline.fLineno + 1;
         fFormula := templateline.fFormula.fLlink.CopyFormula;
         fFirstjustno := templateline.fLineno;
         fJustification := ' ^E';
         fSubprooflevel := templateline.fSubprooflevel;
        end;

       localHead.InsertBefore(localHead.fSize, newline);

       newline := nil;

       SupplyProofline(newline); {new not A}
       with newline do
        begin
         fLineno := templateline.fLineno + 2;
         fFormula := templateline.fFormula.fRlink.CopyFormula;
         fFirstjustno := templateline.fLineno;
         fJustification := ' ^E';
         fSubprooflevel := templateline.fSubprooflevel;
        end;

       localHead.InsertBefore(localHead.fSize, newline);

       newline := nil;

       SupplyProofline(newline); {new duplicate line (A ^ notA)}
       with newline do
        begin
         fLineno := templateline.fLineno + 3;
         fFormula := templateline.fFormula.CopyFormula;
         fJustification := ' ^I';
         fFirstjustno := templateline.fLineno + 1;
         fSecondjustno := templateline.fLineno + 2;
         fthirdjustno := 0;
         fSubprooflevel := templateline.fSubprooflevel;
        end;

       localHead.InsertBefore(localHead.fSize, newline);

       newline := nil;

 end;


   */






  /*

      procedure RemoveDummy (var localHead: TList; newformula: TFormula);
     {When enters last line is a blank, due to closing subproof. Line before is dummy}

     {This does one of two things depending on compiler options.  It comes with a contradiction}
     {say, A^~A as its last line.  This really is only a place holder for the line numbers of the}
     {formulas which compose.  In one version that uses Absurd, the contradition is just}
     {replaced by absured; otherwise the lineonos are retriefed}

       var
        searchline, newline: TProofline;
        newformulanode: TFormula;

     {$IFC useAbsurd}

      begin

       SupplyFormula(newformulanode);
       with newformulanode do
        begin
         fKind := Unary;
         fInfo := chNeg;
         fRlink := newformula.CopyFormula;
        end;

       searchline := TProofline(localHead.Last); (*now pointing at blankline*)

       SupplyProofline(newline);
       with newline do
        begin
         fLineno := searchline.fLineno + 1;
         fFormula := newformulanode;
         fSubprooflevel := searchline.fSubprooflevel;
         fFirstjustno := searchline.fLineno;
         fJustification := ' ~I';
        end;

       localHead.InsertLast(newline);

      end;

     {$ELSEC}

     begin
      searchline := TProofline(localHead.At(localHead.fSize - 2));

      if searchline.fBlankline then {this means that the dummy comes immediately after}
               {a subproof and so needs extra lines}
       begin
        BreakDummy(localHead);
       end;

      searchline := TProofline(localHead.At(localHead.fSize - 1)); { newline points at}
     {                                                                        dummy,removes it}

      localHead.Delete(searchline);
      searchline.fFormula.DismantleFormula;   (*remove contradiction*)

      SupplyFormula(newformulanode);
      with newformulanode do
       begin
        fKind := Unary;
        fInfo := chNeg;
        fRlink := newformula.CopyFormula;
       end;

               {with newline^ do}

      begin
       searchline.fSubprooflevel := searchline.fSubprooflevel - 1; {its jusnos stay same}
       searchline.fFormula := newformulanode;
       searchline.fJustification := ' ~I';
      end;

      localHead.InsertLast(searchline);

     end;

     {$ENDC}



  */

 /*Here we have a proof with AvB as extra first line, then the actual proof
  is a proof of A to C, we will convert it to a proof from A to A->C */


 void convertToConditional(TFormula A)/*convertToSubProof()*/ {


  TProofline template=(TProofline)fHead.get(fHead.size()-1);    //C

  TFormula notA,C,CorNotA,notAorC,ifAthenC;

  C=template.getFormula();

  notA = new TFormula(
                     TFormula.unary,
                     String.valueOf(chNeg),
                     null,
                     A.copyFormula());

  CorNotA = new TFormula(
                       TFormula.binary,
                       String.valueOf(chOr),
                       C.copyFormula(),
                       notA.copyFormula());

                   notAorC = new TFormula(
                                      TFormula.binary,
                                      String.valueOf(chOr),
                                      notA.copyFormula(),
                                      C.copyFormula());

                                        ifAthenC = new TFormula(
                                                           TFormula.binary,
                                                           String.valueOf(chImplic),
                                                           A.copyFormula(),
                                                           C.copyFormula());


  TProofline newline = supplyProofline();


  newline.fFirstjustno=template.fLineno;
  newline.fFormula = CorNotA;
  newline.fHeadlevel = template.fHeadlevel;
  newline.fJustification = fOrIJustification;
  newline.fLineno = template.fLineno+1;
  newline.fSubprooflevel=template.fSubprooflevel;
  newline.fRightMargin=template.fRightMargin;

  fHead.add(newline);

  newline = supplyProofline();

  newline.fFirstjustno=template.fLineno+1;
  newline.fFormula = notAorC;
  newline.fHeadlevel = template.fHeadlevel;
  newline.fJustification = fCommJustification;
  newline.fLineno = template.fLineno+2;
  newline.fSubprooflevel=template.fSubprooflevel;
  newline.fRightMargin=template.fRightMargin;

  fHead.add(newline);

  newline = supplyProofline();

  newline.fFirstjustno=template.fLineno+2;
  newline.fFormula = ifAthenC;
  newline.fHeadlevel = template.fHeadlevel;
  newline.fJustification = fImpJustification;
  newline.fLineno = template.fLineno+3;
  newline.fSubprooflevel=template.fSubprooflevel;
  newline.fRightMargin=template.fRightMargin;

  fHead.add(newline);


}

/*
 procedure ConvertToSubProof (var localHead: TList; var lastAssumption: integer);
 {makes the entire of a proof into a subproof with first line as ass}



 /* on left we have A |- C

  we will go
  Cv~A Add
  ~AvC Comm
  A->C Imp

  same on right

  then add
  (A->C.(B->C) Conj
  CvC          Cons Dilemma
  C Taut

  */



void createLemma1A(TFormula orFormula) {      // this can be overridden eg by Copi
     /*
        {We have a proof of B from premises including ~A, and we}
      {are trying to convert this into a proof of AVB.  We have pushed ~A as first genuine line}
      {of proof}

      |Premises
      |~A
      |Perhaps more premises
      |_______
      |~A
      |<other lines>
      |B

      convert this to a conditional proof

      |Premises
      |~A
      |_______
      |~A->B
      |~~AvB   Trans
        AvB DN

      */

    TFormula B = orFormula.getRLink();

     TFormula notA= new TFormula(TFormula.unary,
                            String.valueOf(chNeg),
                            null,
                            orFormula.getLLink());

TFormula notnotA= new TFormula(TFormula.unary,
                       String.valueOf(chNeg),
                       null,
                       notA);
TFormula notAimplicB= new TFormula(TFormula.binary,
                      String.valueOf(chImplic),
                      notA,
                      B);
  TFormula notnotAorB= new TFormula(TFormula.binary,
                        String.valueOf(chOr),
                        notnotA,
                      B);


  convertToSubProof();
  addConditionalLine(notA);     // ~A->B


  TProofline templateLine = (TProofline)(fHead.get(fHead.size()-1));

  {TProofline notnotAorBLine = supplyProofline();
    notnotAorBLine.fLineno = templateLine.fLineno+1;
    notnotAorBLine.fFormula = notnotAorB.copyFormula();
    notnotAorBLine.fSubprooflevel = templateLine.fSubprooflevel;
    notnotAorBLine.fJustification = fTransJustification;
    notnotAorBLine.fFirstjustno = templateLine.fLineno;

  fHead.add(notnotAorBLine);}

{TProofline orLine = supplyProofline();
  orLine.fLineno = templateLine.fLineno+1;
  orLine.fFormula = orFormula.copyFormula();        //AvB
  orLine.fSubprooflevel = templateLine.fSubprooflevel;
  orLine.fJustification = fDN;
  orLine.fFirstjustno = templateLine.fLineno+1;

  fHead.add(orLine);}


}





 void addCopiOrConc(int firstTailIndex,int secondTailIndex,int orJustNum){
  /*
 This takes A->C and B->C and AvB

goes A->C).(B->C  conj
     CvC consdillemma
     c taut

  */

  TFormula firstTailFormula =  ((TProofline)(fHead.get(firstTailIndex))).fFormula;
  TFormula secondTailFormula =  ((TProofline)(fHead.get(secondTailIndex))).fFormula;
  TFormula C=firstTailFormula.getRLink();

  TFormula conjunction = new TFormula(
                       TFormula.binary,
                       String.valueOf(chAnd),
                       firstTailFormula.copyFormula(),
                       secondTailFormula.copyFormula());

                   TFormula disjunction = new TFormula(
                                        TFormula.binary,
                                        String.valueOf(chOr),
                                        C.copyFormula(),
                                        C.copyFormula());


  if (!TFormula.equalFormulas(firstTailFormula, secondTailFormula)){
    //  {really funny. This is the case where there are two different dummies}
    //NEEDS WRITING
  }

  TProofline newline=supplyProofline();
    newline.fLineno = ((TProofline)(fHead.get(secondTailIndex))).fLineno+1;
    newline.fSubprooflevel= ((TProofline)(fHead.get(secondTailIndex))).fSubprooflevel;  //check?
    newline.fFormula = conjunction;
    newline.fJustification = fAndIJustification;
    newline.fFirstjustno=((TProofline)(fHead.get(firstTailIndex))).fLineno;
    newline.fSecondjustno=((TProofline)(fHead.get(secondTailIndex))).fLineno;

    fHead.add(newline);

    newline=supplyProofline();

    newline.fLineno = ((TProofline)(fHead.get(secondTailIndex))).fLineno+2;
    newline.fSubprooflevel= ((TProofline)(fHead.get(secondTailIndex))).fSubprooflevel;  //check?
    newline.fFormula = disjunction;              //CvC
    newline.fJustification = fCDJustification;
    newline.fFirstjustno=((TProofline)(fHead.get(secondTailIndex))).fLineno+1;
    newline.fSecondjustno=1;

    fHead.add(newline);

    newline=supplyProofline();
      newline.fLineno = ((TProofline)(fHead.get(secondTailIndex))).fLineno+3;
      newline.fSubprooflevel= ((TProofline)(fHead.get(secondTailIndex))).fSubprooflevel;  //check?
      newline.fFormula = C.copyFormula();                //C
      newline.fJustification = fTautJustification;
      newline.fFirstjustno=((TProofline)(fHead.get(secondTailIndex))).fLineno+2;

    fHead.add(newline);


}

 void addConditionalLine(TFormula ifFormula){


   TProofline template=(TProofline)fHead.get(fHead.size()-2);    //C, blankline is last

   TFormula C,ifAthenC;

   C=template.getFormula();

   ifAthenC = new TFormula(
      TFormula.binary,
      String.valueOf(chImplic),
      ifFormula.copyFormula(),
      C.copyFormula());


   TProofline newline = supplyProofline();


   newline.fFirstjustno=template.fLineno;
   newline.fFormula = ifAthenC;
   newline.fHeadlevel = template.fHeadlevel;
   newline.fJustification = fImplicIJustification;
   newline.fLineno = template.fLineno+1;
   newline.fSubprooflevel=template.fSubprooflevel-1;
   newline.fRightMargin=template.fRightMargin;

   fHead.add(newline);
 }


/*for negAnd we'll just do Or then add (~(A^B) on the front by De Morgan */

 void doNegAnd(TReAssemble leftReAss, TReAssemble rightReAss){
   TFormula notA, notB,notAandB,notAornotB;
TProofline firstConc, secondConc;
int firstTailIndex,secondTailIndex;

notA = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(0)); //~A
notB = (TFormula) (fTestNode.getRightChild().fAntecedents.get(0)); //~B

   notAandB= new TFormula(TFormula.unary,
                          String.valueOf(chNeg),
                          null,
                          new TFormula(TFormula.binary,
                                       String.valueOf(chAnd),
                                       notA.getRLink(),
                                       notB.getRLink()
                          ));

   notAornotB= new TFormula(TFormula.binary,
                         String.valueOf(chOr),
                         notA,
                         notB);


   doOre(leftReAss,rightReAss);    //has notAornotB as first line

   prependToHead(notAandB);

   if (transfer(copiDeMo, notAornotB))
     ;

   numberLines();

 }



 void doNegExi(TReAssemble leftReAss){

   TFormula allNot=(TFormula)leftReAss.fTestNode.fAntecedents.get(0);

   fHead=leftReAss.fHead;
   fLastAssIndex=leftReAss.fLastAssIndex;

   int dummy1 = TMergeData.inPremises(fTestNode, fHead,
                                        fLastAssIndex, allNot);
     if (dummy1 != -1) {
       TFormula negExi = new TFormula(TFormula.unary,
                                           String.valueOf(chNeg),
                                           null,
                                           new TFormula(TFormula.quantifier,
                                                        String.valueOf(chExiquant),
                                                        allNot.quantVarForm().copyFormula(),
                                                        (allNot.fRLink.fRLink).copyFormula()));

       prependToHead(negExi);

       if (transfer(fTestNode.fStepType, allNot)){

       }

       numberLines();
     }
}



   void doNegUni(TReAssemble leftReAss){

    TFormula exiNot=(TFormula)leftReAss.fTestNode.fAntecedents.get(0);

    fHead=leftReAss.fHead;
    fLastAssIndex=leftReAss.fLastAssIndex;

    int dummy1 = TMergeData.inPremises(fTestNode, fHead,
                                         fLastAssIndex, exiNot);
      if (dummy1 != -1) {
        TFormula negUni = new TFormula(TFormula.unary,
                                            String.valueOf(chNeg),
                                            null,
                                            new TFormula(TFormula.quantifier,
                                                         String.valueOf(chUniquant),
                                                         exiNot.quantVarForm().copyFormula(),
                                                         (exiNot.fRLink.fRLink).copyFormula()));

        prependToHead(negUni);

        if (transfer(fTestNode.fStepType, exiNot)){

        }
        numberLines();
      }
}



boolean dSRLPermitted(){
      return
          false;
    }


  /******************   EquivI ***************************
   *
   *  Similar to super but we convert subproofs to conditional then join
   */

  void doEquivvS(TReAssemble leftReAss,TReAssemble rightReAss) {
   TProofline templateLine,newline, AimplicB,BimplicA;
   TProofline firstcon=(TProofline)leftReAss.fHead.get(leftReAss.fHead.size()-1);
   TProofline secondcon=(TProofline)rightReAss.fHead.get(rightReAss.fHead.size()-1);

   TFormula AequivB = (TFormula) (fTestNode.fSuccedent.get(0));
   TFormula A=AequivB.fLLink;
   TFormula B=AequivB.fRLink;

   fHead = leftReAss.fHead;
   fLastAssIndex = leftReAss.fLastAssIndex;

   if (!transfer(TTestNode.atomic, A))
    createAssLine(A, fLastAssIndex+1);

   numberLines();

   convertToSubProof();

   templateLine=(TProofline)fHead.get(fHead.size()-1);

   AimplicB=supplyProofline();
   AimplicB.fLineno = templateLine.fLineno+1;
   AimplicB.fSubprooflevel= templateLine.fSubprooflevel;
   AimplicB.fFormula = new TFormula(TFormula.binary,
                                    String.valueOf(chImplic),
                                    A.copyFormula(),
                                    B.copyFormula());
   AimplicB.fJustification = fImplicIJustification;
   AimplicB.fFirstjustno=firstcon.fLineno;
   AimplicB.fHeadlevel=templateLine.fHeadlevel;


   fHead.add(AimplicB);




   if (!rightReAss.transfer(TTestNode.atomic, B))
    rightReAss.createAssLine(B, rightReAss.fLastAssIndex+1);

   rightReAss.numberLines();

   rightReAss.convertToSubProof();

   templateLine=(TProofline)rightReAss.fHead.get(rightReAss.fHead.size()-1);

BimplicA=supplyProofline();
   BimplicA.fLineno = templateLine.fLineno+1;
   BimplicA.fSubprooflevel= templateLine.fSubprooflevel;
   BimplicA.fFormula = new TFormula(TFormula.binary,
                                 String.valueOf(chImplic),
                                 B.copyFormula(),
                                 A.copyFormula());
BimplicA.fJustification = fImplicIJustification;
BimplicA.fFirstjustno=secondcon.fLineno;
BimplicA.fHeadlevel=templateLine.fHeadlevel;


rightReAss.fHead.add(BimplicA);


   TMergeData mergeData = new TMergeData(this, rightReAss);

      mergeData.merge();

      fHead = mergeData.firstLocalHead;
      fLastAssIndex = mergeData.firstLastAssIndex;

      numberLines();

      templateLine=(TProofline)fHead.get(fHead.size()-1);

   newline=supplyProofline();
   newline.fLineno = templateLine.fLineno+1;
   newline.fSubprooflevel= templateLine.fSubprooflevel;
   newline.fFormula = new TFormula(TFormula.binary,
                                 String.valueOf(chAnd),
                                 AimplicB.fFormula.copyFormula(),
                                 BimplicA.fFormula.copyFormula());

   newline.fJustification = fAndIJustification;
   newline.fFirstjustno=AimplicB.fLineno;
   newline.fSecondjustno=BimplicA.fLineno;
   newline.fHeadlevel=templateLine.fHeadlevel;

   fHead.add(newline);

   newline=supplyProofline();
newline.fLineno = templateLine.fLineno+2;
newline.fSubprooflevel= templateLine.fSubprooflevel;
newline.fFormula = AequivB.copyFormula();
newline.fJustification = fMaterialEquiv;
newline.fFirstjustno=templateLine.fLineno+1;
   newline.fHeadlevel=templateLine.fHeadlevel;


fHead.add(newline);




 }



/********************   EI **************************/

  void doExi(TReAssemble leftReAss) {
  fHead = leftReAss.fHead;
  fLastAssIndex = leftReAss.fLastAssIndex;

  TFormula scope = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));
  TFormula variable = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));

  int dummy1 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                     leftReAss.fLastAssIndex, scope);

  if ( (dummy1 != -1)) {


    TFormula formulanode = new TFormula();

    formulanode.fKind = TFormula.quantifier;
    formulanode.fInfo = String.valueOf(chExiquant);
    formulanode.fLLink = variable.copyFormula();
    formulanode.fRLink = scope.copyFormula();

    prependToHead(formulanode);

    if (transfer(fTestNode.fStepType, scope)) // {Moves the left conjunct it justifies into body of proof}
      ;


    numberLines();

  }

}

void doExiCV(TReAssemble leftReAss) {
fHead = leftReAss.fHead;
fLastAssIndex = leftReAss.fLastAssIndex;

TFormula scope = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));
TFormula variable = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));
TFormula oldvariable = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));

int dummy1 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                   leftReAss.fLastAssIndex, scope);

if ( (dummy1 != -1)) {

  scope.subTermVar(scope,oldvariable,variable);


  TFormula formulanode = new TFormula();

  formulanode.fKind = TFormula.quantifier;
  formulanode.fInfo = String.valueOf(chExiquant);
  formulanode.fLLink = oldvariable.copyFormula();
  formulanode.fRLink = scope.copyFormula();

  prependToHead(formulanode);

  if (transfer(fTestNode.fStepType, scope)) // {Moves the left conjunct it justifies into body of proof}
    ;


  numberLines();

}

}


  /* reductio

AVOID THIS IN COPI AS IT IS LONG And complicate

   IN


     /*

        {We have a proof of a contrdiction from premises , and we}
      {are trying to convert this into a proof of G->F. and G->~F

      |Premises
      |_______
      |<other lines>
      |Absurd or F^~F

   we add G after last index

      |Premises
      |_______
      |G
      |<other lines>
      |Absurd or F^~F

   convert the last bit to a subproof, and add G ->F

      |Premises
      |_______
      ||G
      || _
      ||<other lines>
      ||F
      |G->F











This comes in with a derivation whose last line is a contradiction Absurd or F^~F. This is a dummy
which we do not need. But we do need the justnos from it which identify the lines F,~F

   mf can be more complicated, Absurd may have come from elsewhere (ie one line) No Absurds in Copi!


   G |- F^~F

   1. G  Ass
   2. G->(F^~F)
   3 ~(F^~F)-~G

   4  F
   5  F ->  F
   6. ~Fv F
   7. ~Fv ~~F
   8. ~(F^~F)
   9. ~G




   G |- Absurd or F^~F

   1. G  Ass
   2. G->F
   3 ~F-~G
   4 G   Ass
   5 G->~F
   6 ~~F->~G
   7 ~F                  could shorten with ~F->~F then omit 10
   8 ~F -> ~F
   9. ~~Fv~F
   10. ~~F->~G.~F-~G
   11. ~G v ~G
   12. ~G


  BELOW IS NO GOOD (I THINK BECAUSE WE HAVE THE EXTRA ASSUMPTION G, WHICH WE MUST GET RID OF)

  1. F
  2. ~F
  3. F v ? 1 Add
  4. ?     2,3 DS



   */

  void doReductio(TReAssemble leftReAss){
    fHead=leftReAss.fHead;
    fLastAssIndex=leftReAss.fLastAssIndex;

    TFormula notG=(TFormula)(this.fTestNode.fSuccedent.get(0));
    TFormula G=notG.getRLink();




   int lastIndex=fHead.size()-1;

   TProofline contradictionLine=(TProofline)fHead.get(lastIndex);  //usually a contradiction F^~F

   TFormula contra=contradictionLine.getFormula();
   TFormula notContra= new TFormula(TFormula.unary,
                              String.valueOf(chNeg),
                              null,
                              contra);

   TFormula F=contradictionLine.getFormula().getLLink();
   TFormula notF=contradictionLine.getFormula().getRLink();
   TFormula notnotF=new TFormula(TFormula.unary,
                              String.valueOf(chNeg),
                              null,
                              notF);


   TFormula FimplicF= new TFormula(TFormula.binary,
                              String.valueOf(chImplic),
                              F,
                              F);

  TFormula notForF= new TFormula(TFormula.binary,
                              String.valueOf(chOr),
                              notF,
                              F);
  TFormula notFornotnotF= new TFormula(TFormula.binary,
                              String.valueOf(chOr),
                              notF,
                              notnotF);

   TFormula GimplicContra= new TFormula(TFormula.binary,
                                 String.valueOf(chImplic),
                                 G,
                                 contra);
   TFormula notContraimplicnotG= new TFormula(TFormula.binary,
                                String.valueOf(chImplic),
                                notContra,
                                notG);



    TProofline templateLine = (TProofline)(fHead.get(fHead.size()-1));

  if (!transfer(TTestNode.atomic, G)) //
       createAssLine(G, fLastAssIndex+1);

  numberLines();

  int justNo=templateLine.fLineno;

   convertToSubProof();

      numberLines();

      templateLine = (TProofline)(fHead.get(fHead.size()-1));

      TProofline newline=supplyProofline();
        newline.fLineno = templateLine.fLineno+1;
        newline.fSubprooflevel=templateLine.fSubprooflevel;
        newline.fFormula = GimplicContra.copyFormula();
        newline.fFirstjustno=justNo;
        newline.fJustification = fImplicIJustification;

        fHead.add(newline);


     {TProofline notContraimplicnotGLine = supplyProofline();
       notContraimplicnotGLine.fLineno = templateLine.fLineno+2;
       notContraimplicnotGLine.fFormula = notContraimplicnotG.copyFormula();
       notContraimplicnotGLine.fSubprooflevel = templateLine.fSubprooflevel;
       notContraimplicnotGLine.fJustification = fTransJustification;
       notContraimplicnotGLine.fFirstjustno = templateLine.fLineno+1;

       fHead.add(notContraimplicnotGLine);}


     {TProofline assFLine = supplyProofline();
       assFLine.fLineno = templateLine.fLineno+3;
       assFLine.fFormula = F.copyFormula();
       assFLine.fSubprooflevel = templateLine.fSubprooflevel+1;
       assFLine.fLastassumption = true;
       assFLine.fJustification = fAssJustification;

       fHead.add(assFLine);}

     endSubProof();

     {TProofline FimplicFLine = supplyProofline();
       FimplicFLine.fLineno = templateLine.fLineno+4;
       FimplicFLine.fFormula = FimplicF.copyFormula();
       FimplicFLine.fSubprooflevel = templateLine.fSubprooflevel;
       FimplicFLine.fJustification = fImplicIJustification;
       FimplicFLine.fFirstjustno = templateLine.fLineno+3;

       fHead.add(FimplicFLine);}

     {TProofline notForFLine = supplyProofline();
      notForFLine.fLineno = templateLine.fLineno+5;
      notForFLine.fFormula = notForF.copyFormula();
      notForFLine.fSubprooflevel = templateLine.fSubprooflevel;
      notForFLine.fJustification = fMI;
      notForFLine.fFirstjustno = templateLine.fLineno+4;

      fHead.add(notForFLine);}


    {TProofline notFornotnotFLine = supplyProofline();
     notFornotnotFLine.fLineno = templateLine.fLineno+6;
     notFornotnotFLine.fFormula = notFornotnotF.copyFormula();
     notFornotnotFLine.fSubprooflevel = templateLine.fSubprooflevel;
     notFornotnotFLine.fJustification = fDN;
     notFornotnotFLine.fFirstjustno = templateLine.fLineno+5;

      fHead.add(notFornotnotFLine);}


    {TProofline notContraLine = supplyProofline();
     notContraLine.fLineno = templateLine.fLineno+7;
     notContraLine.fFormula = notContra.copyFormula();
     notContraLine.fSubprooflevel = templateLine.fSubprooflevel;
     notContraLine.fJustification = fDeMo;
     notContraLine.fFirstjustno = templateLine.fLineno+6;

      fHead.add(notContraLine);}

    {TProofline notGLine = supplyProofline();
     notGLine.fLineno = templateLine.fLineno+8;
     notGLine.fFormula = notG.copyFormula();
     notGLine.fSubprooflevel = templateLine.fSubprooflevel;
     notGLine.fJustification = fImplicEJustification;
     notGLine.fFirstjustno = templateLine.fLineno+2;
     notGLine.fSecondjustno = templateLine.fLineno+7;

      fHead.add(notGLine);}



/*    int lastIndex=fHead.size()-1;

   TProofline dummy=(TProofline)fHead.get(lastIndex);  //usually a contradiction F^~F

   fHead.remove(lastIndex);

   int firstJustNo,secondJustNo;

   firstJustNo=dummy.fFirstjustno;
   secondJustNo=dummy.fSecondjustno;


 TProofline FLine = TProofListModel.lineFromLineNo(fHead,firstJustNo);
 TProofline notFLine = TProofListModel.lineFromLineNo(fHead,secondJustNo);

 if ((FLine==null)|| (notFLine==null)){  //error condition

  if (TConstants.DEBUG){
    System.out.print("Error in reductio  " + fTestNode.fStepType +
                  strCR);}
  return;
}



 if (fParser.isNegation(FLine.getFormula()) && //we need them the right way round
   FLine.getFormula().getRLink().equalFormulas(FLine.getFormula().getRLink(), notFLine.getFormula())) {
 TProofline temp=FLine;
 FLine=notFLine;
 notFLine=temp;
}




 TFormula F,notF;
 F = FLine.getFormula();
 notF = notFLine.getFormula();

 if (fParser.isNegation(F) && //we need them the right way round
    F.getRLink().equalFormulas(F.getRLink(), notF)) {
  F = notFLine.getFormula();
  notF = FLine.getFormula();
  firstJustNo = dummy.fSecondjustno;
  secondJustNo = dummy.fFirstjustno;

}


 TFormula notG=(TFormula)(this.fTestNode.fSuccedent.get(0));
 TFormula G=notG.getRLink();
 TFormula GimplicF= new TFormula(TFormula.binary,
                                     String.valueOf(chImplic),
                                     G,
                                     F);

 TFormula notFimplicnotG= new TFormula(TFormula.binary,
                                      String.valueOf(chImplic),
                                      notF,
                                      notG);
 TFormula GimplicnotF= new TFormula(TFormula.binary,
                                     String.valueOf(chImplic),
                                     notG,
                                     notF);
  TFormula notnotF= new TFormula(TFormula.unary,
                                     String.valueOf(chNeg),
                                     null,
                                     notF);
  TFormula notnotFimplicnotG= new TFormula(TFormula.binary,
                                    String.valueOf(chImplic),
                                    notnotF,
                                    notG);

  TFormula notFimplicnotF= new TFormula(TFormula.binary,
                                    String.valueOf(chImplic),
                                    notF,
                                    notF);

TFormula notnotFornotF= new TFormula(TFormula.binary,
                                      String.valueOf(chOr),
                                      notnotF,
                                      notF);
TFormula notFornotnotF= new TFormula(TFormula.binary,
                                         String.valueOf(chOr),
                                         notF,
                                    notnotF);
  TFormula notnotFimplicnotGandnotFimplicnotG= new TFormula(TFormula.binary,
                                   String.valueOf(chAnd),
                                   notnotFimplicnotG,
                                   notFimplicnotG);
TFormula notGornotG= new TFormula(TFormula.binary,
                                  String.valueOf(chOr),
                                  notG,
                                  notG);





    if ((FLine!=null)&& (notFLine!=null)){    // have already checked for this

// we started from a proof of Absurd or F^~F with the extra assumption G
// and we have removed the last line ie F^~F


      if (!transfer(TTestNode.atomic, G))
         createAssLine(G, fLastAssIndex+1);




      numberLines();

      endSubProof();

      TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 1));


       {TProofline GimplicFLine = supplyProofline();
       GimplicFLine.fLineno = templateLine.fLineno+1;
       GimplicFLine.fFormula = GimplicF.copyFormula();
       GimplicFLine.fSubprooflevel = templateLine.fSubprooflevel;
       GimplicFLine.fJustification = fArrowIJustification;
       GimplicFLine.fFirstjustno = FLine.getLineNo();
       GimplicFLine.fHeadlevel = templateLine.fHeadlevel;

       fHead.add(GimplicFLine);}

       {TProofline notFimplicnotGLine = supplyProofline();
       notFimplicnotGLine.fLineno = templateLine.fLineno+2;
       notFimplicnotGLine.fFormula = notFimplicnotG.copyFormula();
       notFimplicnotGLine.fSubprooflevel = templateLine.fSubprooflevel;
       notFimplicnotGLine.fJustification = fTransJustification;
       notFimplicnotGLine.fFirstjustno = templateLine.fLineno+1;

       fHead.add(notFimplicnotGLine);}

       {TProofline assGLine = supplyProofline();
       assGLine.fLineno = templateLine.fLineno+3;
       assGLine.fFormula = G.copyFormula();
       assGLine.fSubprooflevel = templateLine.fSubprooflevel+1;
       assGLine.fJustification = fAssJustification;
       assGLine.fLastassumption = true;

       fHead.add(assGLine);}

       endSubProof();

       {TProofline GimplicnotFLine = supplyProofline();
       GimplicnotFLine.fLineno = templateLine.fLineno+4;
       GimplicnotFLine.fFormula = GimplicnotF.copyFormula();
       GimplicnotFLine.fSubprooflevel = templateLine.fSubprooflevel+1;
       GimplicnotFLine.fJustification = fAssJustification;
       GimplicnotFLine.fFirstjustno = notFLine.getLineNo();

       fHead.add(GimplicnotFLine);}


     {TProofline notnotFimplicnotGLine = supplyProofline();
notnotFimplicnotGLine.fLineno = templateLine.fLineno+5;
notnotFimplicnotGLine.fFormula = notnotFimplicnotG.copyFormula();
notnotFimplicnotGLine.fSubprooflevel = templateLine.fSubprooflevel;
notnotFimplicnotGLine.fJustification = fTransJustification;
notnotFimplicnotGLine.fFirstjustno = templateLine.fLineno+4;

fHead.add(notnotFimplicnotGLine);}

     {TProofline assnotFLine = supplyProofline();
       assnotFLine.fLineno = templateLine.fLineno+6;
       assnotFLine.fFormula = G.copyFormula();
       assnotFLine.fSubprooflevel = templateLine.fSubprooflevel+1;
       assnotFLine.fLastassumption = true;
       assnotFLine.fJustification = fAssJustification;

       fHead.add(assnotFLine);}

     endSubProof();

     {TProofline notFimplicnotFLine = supplyProofline();
       notFimplicnotFLine.fLineno = templateLine.fLineno+7;
       notFimplicnotFLine.fFormula = notFimplicnotF.copyFormula();
       notFimplicnotFLine.fSubprooflevel = templateLine.fSubprooflevel;
       notFimplicnotFLine.fJustification = fArrowIJustification;
       notFimplicnotFLine.fFirstjustno = templateLine.fLineno+6;

       fHead.add(notFimplicnotFLine);}

     {TProofline notnotFornotFLine = supplyProofline();
      notnotFornotFLine.fLineno = templateLine.fLineno+8;
      notnotFornotFLine.fFormula = notnotFornotF.copyFormula();
      notnotFornotFLine.fSubprooflevel = templateLine.fSubprooflevel;
      notnotFornotFLine.fJustification = fMI;
      notnotFornotFLine.fFirstjustno = templateLine.fLineno+7;

      fHead.add(notnotFornotFLine);}

    {TProofline andLine = supplyProofline();
 andLine.fLineno = templateLine.fLineno+9;
 andLine.fFormula = notnotFimplicnotGandnotFimplicnotG.copyFormula();
 andLine.fSubprooflevel = templateLine.fSubprooflevel;
 andLine.fJustification = fAndIJustification;
 andLine.fFirstjustno = templateLine.fLineno+2;
 andLine.fSecondjustno = templateLine.fLineno+5;

 fHead.add(andLine);}

  {TProofline notGornotGLine = supplyProofline();
    notGornotGLine.fLineno = templateLine.fLineno+10;
    notGornotGLine.fFormula = notGornotG.copyFormula();
    notGornotGLine.fSubprooflevel = templateLine.fSubprooflevel;
    notGornotGLine.fJustification = fCDJustification;
    notGornotGLine.fFirstjustno = templateLine.fLineno+9;
    notGornotGLine.fSecondjustno = templateLine.fLineno+8;

fHead.add(notGornotGLine);}

  {TProofline notGLine = supplyProofline();
  notGLine.fLineno = templateLine.fLineno+11;
  notGLine.fFormula = notG.copyFormula();
  notGLine.fSubprooflevel = templateLine.fSubprooflevel;
  notGLine.fJustification = fTautJustification;
  notGLine.fFirstjustno = templateLine.fLineno+10;


fHead.add(notGLine);} */



       //  convertToSubProof();
      //removeDummy(refutedFormula);

      //numberLines();





/*





      TFormula disjunctF = new TFormula(       // F v ~G
          TFormula.binary,
          String.valueOf(chOr),
          F.copyFormula(),
          notG.copyFormula());

      TProofline newline = supplyProofline();

      newline.fFirstjustno = firstJustNo;
      newline.fFormula = disjunctF;
      newline.fHeadlevel = dummy.fHeadlevel;
      newline.fJustification = fOrIJustification;
      newline.fLineno = dummy.fLineno;            //dummy has been removed
      newline.fSubprooflevel = dummy.fSubprooflevel;
      newline.fRightMargin = dummy.fRightMargin;

      fHead.add(newline);

      newline = supplyProofline();

      newline.fFirstjustno = dummy.fLineno;      //the disjunct
      newline.fSecondjustno = secondJustNo;      //~F
      newline.fFormula = notG.copyFormula();  //~G
      newline.fHeadlevel = dummy.fHeadlevel;
      newline.fJustification = fDSJustification;
      newline.fLineno = dummy.fLineno + 1;
      newline.fSubprooflevel = dummy.fSubprooflevel;
      newline.fRightMargin = dummy.fRightMargin;

      fHead.add(newline);

 */

}

  /* adding double neg */

  void doDoubleNegS(TReAssemble leftReAss) {
    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;


      TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 1));

          TFormula A=templateLine.fFormula;
          TFormula notA= new TFormula(TFormula.unary,
                                     String.valueOf(chNeg),
                                     null,
                                     A);
         TFormula notNotA= new TFormula(TFormula.unary,
                                           String.valueOf(chNeg),
                                           null,
                                           notA);



        TProofline newline = supplyProofline();
        newline.fLineno = templateLine.fLineno+1;
        newline.fFormula = notNotA.copyFormula();
        newline.fSubprooflevel = templateLine.fSubprooflevel;
       newline.fJustification = fDN;
       newline.fFirstjustno = templateLine.fLineno;

       fHead.add(newline);
}





  boolean transfer(int steptype, TFormula thisFormula){
    /*
       {Finds this formula among the displayed Ass steps if it is there, justifies it and removes it to}
       {body of proof. The found formula will not normally be the head. But if it is the singleton}
       {Head then a blankstart is created and lastassumption removed}

    */

   boolean found=false;
   TProofline searchLine,foundLine=null;

   if ((fHead!=null)&&fHead.size()>0){

     removeDuplicateAss();   // new Dec 06-- rare case, two identical assumptions, we don't want to transfer just one

     for(int i=0;(i<=fLastAssIndex)&&(foundLine==null);i++){
       searchLine=(TProofline)fHead.get(i);
       if (!searchLine.fBlankline&&TFormula.equalFormulas(thisFormula,searchLine.fFormula))
         foundLine=searchLine;
     }

    if (foundLine!=null){
      if ((fLastAssIndex==0)&&(!((TProofline)fHead.get(0)).fBlankline)){     //single premise
        TProofListModel.increaseSubProofLevels(fHead,-1);
        createBlankStart(); // increments lastAss
      }
      fHead.remove(foundLine);
      fLastAssIndex-=1;

      foundLine.fFirstjustno=1000; // this is the number of the Head
      foundLine.fSubprooflevel=((TProofline)fHead.get(fLastAssIndex)).fSubprooflevel;

      switch (steptype) {
        case (TTestNode.atomic):
          foundLine.fJustification=fAssJustification;
          foundLine.fFirstjustno=0;

          /*
      TProofline(searchObject).fJustification := 'Ass';
             TProofline(searchObject).fFirstjustno := 0; {no justno needed}

      */
          break;
        case (TTestNode.doubleneg):
          foundLine.fJustification=fNegEJustification;
          /*
      doubleneg:
            TProofline(searchObject).fJustification := ' ~E';
      */
          break;
        case (TTestNode.aand):
          foundLine.fJustification= fAndEJustification;
          break;
        case (TTestNode.ore):
          foundLine.fJustification=fAssJustification;
          foundLine.fFirstjustno=0;

          /*ore:
            begin
             TProofline(searchObject).fJustification := 'Ass';
             TProofline(searchObject).fFirstjustno := 0; {no justno needed}
            end;
   */
          break;
        case (TTestNode.equivv):
          foundLine.fJustification=TProofPanel.equivEJustification;
          /*equivv:
            TProofline(searchObject).fJustification := ' �E';*/
          break;
        case (TTestNode.uni):
          foundLine.fJustification=TProofPanel.UIJustification;
          /*TProofline(searchObject).fJustification := ' UI'; */
          break;
        case (TTestNode.exi):
        case (TTestNode.exiCV):
          foundLine.fJustification=fEIJustification;
          //foundLine.fFirstjustno=0;
          break;


        case (TTestNode.neguni):
        case (TTestNode.negexi):
          foundLine.fJustification=fQN;
          break;







        case (copiComm):
            foundLine.fJustification=TCopiProofPanel.commJustification;
            break;

        case (copiEquiv):
            foundLine.fJustification=fMaterialEquiv;
            break;

          case (copiDeMo):
     foundLine.fJustification=fDeMo;
     break;



      }

      fHead.add(fLastAssIndex+1,foundLine);

    }
   }

    return
        foundLine!=null;
}

/*****************  Type of Override  WE NO LONGER USE THIS, JAN 07 Leaving code temporarily*************/

class CopiAtomicS{
  TFormula conclusionFormula;
  boolean shortcut=false;
  boolean absurdity=false;

void addDummy(){
  TProofline newline;

{   // MORE CODE NEEDED IF NOT USING ABSURD

    TProofline FLine=(TProofline)fHead.get(0);
    TProofline notFLine=(TProofline)fHead.get(1);

    TFormula FandnotF= new TFormula(TFormula.binary,
                                 String.valueOf(chAnd),
                                 FLine.getFormula().copyFormula(),
                                 notFLine.getFormula().copyFormula());

    newline = supplyProofline();   // not not C

newline.fLineno = 3;
newline.fFormula = FandnotF.copyFormula();
newline.fFirstjustno=1;
newline.fSecondjustno=2;
newline.fJustification = fAndIJustification;
newline.fSubprooflevel =0;

fHead.add(newline);




  }

}

/*
   {$IFC useAbsurd}
    procedure AddDummy;

    begin
     SupplyProofline(newline);
     with newline do
      begin
       fLineno := 3;
       fFormula := gAbsurdFormula.CopyFormula;
       fFirstjustno := 1;
       fSecondjustno := 2;
       fJustification := concat(chBlank, 'AbsI');
      end;

     localHead.InsertLast(newline);
     newformulanode := nil;
     newline := nil;
    end;

   {$ELSEC}

    procedure AddDummy;

    begin
     SupplyFormula(newformulanode);
     with newformulanode do
      begin
       fKind := Binary;
       fInfo := chAnd;
       fLlink := TProofline(localHead.At(1)).fFormula.CopyFormula;
       fRlink := TProofline(localHead.At(2)).fFormula.CopyFormula;
      end;

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := 3;
       fFormula := newformulanode;
       fFirstjustno := 1;
       fSecondjustno := 2;
       fJustification := concat(chBlank, chAnd, 'I');
      end;

     localHead.InsertLast(newline);
     newformulanode := nil;
     newline := nil;
    end;
{$ENDC}

*/

/* Dec06 COpi does not want

void doAbsurd(int first, int second){
TProofline newline=supplyProofline();

 newline.fLineno = 3;
 newline.fSubprooflevel=0;
 newline.fFormula = conclusionFormula.copyFormula();
 newline.fFirstjustno=first;
 newline.fSecondjustno=second;
 newline.fJustification = TProofPanel.absIJustification;

 fHead.add(newline);

 absurdity=true;
}

 */

/*
 procedure DoAbsurd (First, second: integer);

  begin
   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 3;
     fFormula := newFormula;
     fFirstjustno := First;
     fSecondjustno := second;
     fJustification := concat(chBlank, 'AbsI');
    end;

   localHead.InsertLast(newline);
   newline := nil;
 end;

*/


void doShortcut(int first, int second){
TProofline newline=supplyProofline();

 newline.fLineno = 3;
 newline.fSubprooflevel=0;
 newline.fFormula = conclusionFormula.copyFormula();
 newline.fFirstjustno=first;
 newline.fSecondjustno=second;
 newline.fJustification = fAndIJustification;

 fHead.add(newline);
}
  /*
 procedure DoShortcut (First, second: integer);

  begin
   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 3;
     fFormula := newFormula;
     fFirstjustno := First;
     fSecondjustno := second;
     fJustification := concat(chBlank, chAnd, 'I');
    end;

   localHead.InsertLast(newline);
   newline := nil;
 end;

*/


void doAndSpecialCase(){
  TProofline newLine;
  TFormula firstFormula=((TProofline)fHead.get(0)).getFormula();
  TFormula secondFormula=((TProofline)fHead.get(1)).getFormula();

  if (TFormula.formulasContradict(firstFormula,secondFormula)){ //{this is A, not A therefore A^notA}

     if (TFormula.equalFormulas(firstFormula,conclusionFormula.fLLink))
       doShortcut(1,2);
     else
       doShortcut(2,1);

     shortcut=true;

  }

}

/*
 begin
         firstformula := TProofline(localHead.At(1)).fFormula;
         secondformula := TProofline(localHead.At(2)).fFormula;

         if (EqualFormulas(newFormula.fLlink, firstformula) and EqualFormulas(newFormula.fRlink, secondformula)) then
          begin
           DoShortcut(1, 2); {this is A, not A therefore A^notA}
           shortcut := true;
          end;
         if not shortcut then
          if (EqualFormulas(newFormula.fRlink, firstformula) and EqualFormulas(newFormula.fLlink, secondformula)) then
           begin
           DoShortcut(2, 1);
           shortcut := true;
           end;
       end;

*/




void doAtomicS(){
  /*

  we are trying to get from A,~A to C, usually this will be


   |A
   |~A
   |A v ?
   |?


  however, if there can be some shorter cases  NOT PROGRAMMED YET APRIL 24 2006


   A,~A, C to C is

   |C
   |__
   |C

   A,~A, C to CvD is

   |C
   |__
   |CvD

   A,~A, C, D to C^D is

   |C
   |D
   |__
   |C^D




   */


TFormula firstFormula,secondFormula;

 if (fHead==null)
    fHead=new ArrayList();

 twoPremiseStart();
 fLastAssIndex=1;

/*
     TwoPremiseStart(thisnode, localHead);
     lastAssumption := 2;
     shortcut := false;
     absurdity := false;

 if (thisnode.fsucceedent.fSize = 0) then {this puts in a dummy contradiction to be picked}
{                                                    up by a reductio later}
  AddDummy
 else
  begin

 */


if(fTestNode.fSuccedent.size()<1){
   addDummy();
}
else{
   conclusionFormula=((TFormula)fTestNode.fSuccedent.get(0)).copyFormula();

   if (conclusionFormula.fKind==TFormula.quantifier)
     ;//REMOvE instantiating, I think not needed as we now do not store on formula;
   if (conclusionFormula.fInfo.equals(String.valueOf(chAnd))){
     doAndSpecialCase();

/*
   newFormula := TFormula(thisnode.fsucceedent.First).CopyFormula;
      if newFormula.fKind = quantifier then
       RemoveInstantiatingInfo(newFormula);
                      { newFormula.fInfo := COPY(newFormula.fInfo, 1, 2); remove instantiating info}

      if newFormula.fInfo = chAnd then
       DoAndSpecialCase

*/
   }


 /* Dec06 don't want

if (TFormula.equalFormulas(conclusionFormula, TFormula.fAbsurd)){
      doAbsurd(1,2);
     }
     /*
          if EqualFormulas(newFormula, gAbsurdFormula) then
           begin
            DoAbsurd(1, 2); {this is A, not A therefore Absurd}
            absurdity := true;
           end;
     */



if (!shortcut&&!absurdity){


        TFormula F,notF,temp;
        int firstJustNo,secondJustNo;

        firstJustNo=1;
        secondJustNo=2;

        F=((TProofline)fHead.get(0)).fFormula;
        notF=((TProofline)fHead.get(1)).fFormula;

        if (fParser.isNegation(F)&&                              //we need them the right way round
            F.getRLink().equalFormulas(F.getRLink(),notF)){
          temp=F;
          F=notF;
          notF=temp;
          firstJustNo=2;
          secondJustNo=1;
        }

        TFormula disjunctF = new TFormula(
                                 TFormula.binary,
                                 String.valueOf(chOr),
                                 F.copyFormula(),
                                 conclusionFormula.copyFormula());

        TProofline template=(TProofline)fHead.get(1);

        TProofline newline = supplyProofline();


        newline.fFirstjustno=firstJustNo;
        newline.fFormula = disjunctF;       //not a copy!!
        newline.fHeadlevel = template.fHeadlevel;
        newline.fJustification = fOrIJustification;
        newline.fLineno = 3;
        newline.fSubprooflevel=template.fSubprooflevel;
        newline.fRightMargin=template.fRightMargin;

        fHead.add(newline);

        newline = supplyProofline();

        newline.fFirstjustno=3;  //the disjunct
        newline.fSecondjustno=secondJustNo;
        newline.fFormula = conclusionFormula.copyFormula();
        newline.fHeadlevel = template.fHeadlevel;
        newline.fJustification = fDSJustification;
        newline.fLineno = 4;
        newline.fSubprooflevel=template.fSubprooflevel;
        newline.fRightMargin=template.fRightMargin;

        fHead.add(newline);

}
   }
}

 void twoPremiseStart(){
      /*
             {This is the case when two antecedents contradict} {I used to say that they are at the Head of the list,
       but they do not have to be}
         {In which case all that is needed is the two premise start justified by Ass}

      */


  TFormula firstone= new TFormula();
  TFormula secondone= new TFormula();

  if (TFormula.twoInListContradict(fTestNode.fAntecedents,firstone, secondone)){


    if (fParser.isNegation(firstone) && //we need them the right way round
        firstone.getRLink().equalFormulas(firstone.getRLink(), secondone)) {
      TFormula temp=firstone;
      firstone=secondone;
      secondone=temp;
    }


  TProofline newline=supplyProofline();
  newline.fLineno = 1;
  newline.fFormula = firstone.copyFormula();
  newline.fJustification = fAssJustification;

  fHead.add(newline);

  newline=supplyProofline();
  newline.fLineno = 2;
  newline.fFormula = secondone.copyFormula();
  newline.fJustification = fAssJustification;

  fHead.add(newline);

  }

 }

    /*
      procedure TwoPremiseStart (thisnode: TTestnode; var localHead: TList);

      var
       firstone, secondone: TFormula;
       newline: TProofline;

               {This is the case when two antecedents contradict}
               {They are at the Head of the list}
               {In which case all that is needed is the two premise start justified by Ass}

     begin

      if thisnode.TwoContradict(firstone, secondone) then
               {This identifies the contradictory formulas}

       begin

        SupplyProofline(newline);
        with newline do
         begin
          fLineno := 1;
          fFormula := firstone.CopyFormula;
          fJustification := 'Ass';
         end;

        localHead.InsertLast(newline);
        newline := nil;

        SupplyProofline(newline);
        with newline do
         begin
          fLineno := 2;
          fFormula := secondone.CopyFormula;
          fJustification := 'Ass';
         end;

        localHead.InsertLast(newline);
        newline := nil;
       end;
     end;



 */




}    /// end of class AtomicS


/*****************  End of Type ******************/



  void reAssembleProof(){
    int dummy;
    TReAssemble leftReAss=null;
    TReAssemble rightReAss=null;

     if (doDirectStepOptimization()){
       if (TConstants.DEBUG){
         System.out.print(strCR + "Direct optimization  " + fTestNode.fStepType +
                          strCR);
         System.out.print(strCR + "Steptype: " + fTestNode.fStepType +
                          strCR);
       }
       return;
     }

    TTestNode leftChild=fTestNode.getLeftChild();
    TTestNode rightChild=fTestNode.getRightChild();

    if (leftChild!=null){
      leftReAss=supplyTReAssemble(leftChild,null,0);
      //leftReAss=new TCopiReAssemble(fParser,leftChild,null,0);
      leftReAss.reAssembleProof();
    }
    if (rightChild!=null){
      rightReAss=supplyTReAssemble(rightChild,null,0);
     // rightReAss=new TCopiReAssemble(fParser,rightChild,null,0);
      rightReAss.reAssembleProof();
    }


    if (TConstants.DEBUG){
      System.out.print(strCR +"Steptype: " + fTestNode.fStepType+strCR);
      if (leftReAss!=null)
      System.out.print("Copi Before Left" + proofToString(leftReAss.fHead));
    if (rightReAss!=null)
      System.out.print("Copi Before Right" + proofToString(rightReAss.fHead));

   }


/* Dec 06 reductio not good in Copi also we don't dabble with absurd

    if ((fTestNode.fStepType!=TTestNode.atomicS)  //can we prove a complex reductio?
        &&(fTestNode.fSuccedent.size()==0)

   //March 06 This next or clause is an attempt to make a shortcut for the same thing when the conclusion is Absurd
   // whether the repeated tests will bog anything down I don't know

        || ((fTestNode.fSuccedent.size()==1)
        && (TFormula.equalFormulas((TFormula)(fTestNode.fSuccedent.get(0)),
                                             TFormula.fAbsurd)))

        ){
           OptimizeR doer= new OptimizeR();

          if (doer.doOptimizeR())
            return;                              //bale out completely
    }

*/

      switch (fTestNode.fStepType) {

        case (TTestNode.atomic):
           doAtomic();
           break;

        case (TTestNode.atomicS):{
          // CopiAtomicS doer = new CopiAtomicS();
           AtomicS doer = new AtomicS();
           doer.doAtomicS();
           }
           break;

        case (TTestNode.negatomicS):
        case (TTestNode.negandS):
        case (TTestNode.noreS):
        case (TTestNode.negarrowS):
        case (TTestNode.nequivS):
        case (TTestNode.neguniS):
        case (TTestNode.negexiS):
           doReductio(leftReAss);
           break;


        case (TTestNode.aand):

          doAand(leftReAss);

          break;

        case (TTestNode.aandS):

          doAandS(leftReAss,rightReAss);


          break;


        case (TTestNode.negand):

          doNegAnd(leftReAss,rightReAss);


            break;

          case (TTestNode.doubleneg):

          doDoubleNeg(leftReAss);

          break;


          /*
              doubleneg:
                  begin
                   ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
                   DoDoubleNeg(thisnode, localHead, lastAssumption);
                  end;

      */

   case (TTestNode.doublenegS):

        doDoubleNegS(leftReAss);

        break;

        case (TTestNode.implic):

          {

          Implic doer= new Implic(leftReAss,rightReAss);

          doer.doImplic(leftReAss,rightReAss);

           break;}



   case (TTestNode.arrowS):

        doImplicS(leftReAss);

        break;

      case (TTestNode.negarrow):

           doNegImplic(leftReAss);

        break;


   case (TTestNode.equivv):

      doEquivv(leftReAss);

      break;

    case (TTestNode.equivvS):

       doEquivvS(leftReAss,rightReAss);

      break;



   case (TTestNode.uni):

    doUni(leftReAss);

    break;


         case (TTestNode.uniS):

         doUniS(leftReAss);

         break;

       case (TTestNode.exiS):

       doExiS(leftReAss);

       break;

     case (TTestNode.negexi):

     doNegExi(leftReAss);

     break;

   case (TTestNode.neguni):

   doNegUni(leftReAss);

   break;



     case (TTestNode.exi):{

      doExi(leftReAss);

       break;
      }

      case (TTestNode.exiCV):{

      DoExiCV doer= new DoExiCV(leftReAss);

           doer.doExiCV(leftReAss);

        break;
      }


    case (TTestNode.ore):


                doOre(leftReAss,rightReAss);
  break;

  /*ore:
         begin
          ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
          ReAssembleProof(thisnode.frlink, rightHead, rightLastAss);
          DoOre(thisnode, localHead, lastAssumption, rightHead, rightLastAss);
         end;

      */
   case (TTestNode.oreS):
   {

        OreS doer= new OreS();


            doer.doOreS(leftReAss);
            break;}

  /*
      oreS:
         DoOreS(thisnode, localHead, lastAssumption);
      */

     case (TTestNode.nore):


                doNore(leftReAss);
  break;


     /*
          nore:
         begin
          ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
          DoNore(thisnode, localHead, lastAssumption);
         end;

            */

    default:
      System.out.print("In Reasemble, no case called " + fTestNode.fStepType);
  }



  if (TConstants.DEBUG){
  System.out.print(strCR +"Copi Steptype, after: " + fTestNode.fStepType+ "<br>");
  if (leftReAss!=null)
  System.out.print("Copi After Left"+ fTestNode.fStepType+ "<br>" + proofToString(leftReAss.fHead) + "<br>");
 }




}


}
