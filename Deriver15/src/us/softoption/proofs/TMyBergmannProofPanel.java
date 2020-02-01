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

import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import us.softoption.editor.TDeriverDocument;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.interpretation.TBergmannTestNode;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;


public class TMyBergmannProofPanel extends TMyProofPanel {

  final static String dSJustification=" DS";
  final static String hSJustification=" HS";
  final static String mTJustification=" MT";

  final static String UEJustification=" "+ chUniquant + "E";


  JMenuItem dSMenuItem = new JMenuItem();
  JMenuItem hSMenuItem = new JMenuItem();
  JMenuItem mTMenuItem = new JMenuItem();



public TMyBergmannProofPanel(TDeriverDocument itsDeriverDocument){
super(itsDeriverDocument);

  fAndEJustification=" "+ TBergmannParser.chBergmannAnd + "E";
  fAndIJustification=" "+ TBergmannParser.chBergmannAnd + "I";

  fEIJustification=" "+ chExiquant + "E";

  UGJustification=" "+ chUniquant + "I";

  EGJustification=" "+ chExiquant + "I";
 // EIJustification=" "+ chExiquant + "E";


  fTIInput = "Doing Assumption";

  andIMenuItem.setText("&I");   // super uses ^I
  andEMenuItem.setText("&E");

  dSMenuItem.setText("DS");
  dSMenuItem.addActionListener(new TMyBergmannProofPanel_dSMenuItem_actionAdapter(this));

  hSMenuItem.setText("HS");
  hSMenuItem.addActionListener(new TMyBergmannProofPanel_hSMenuItem_actionAdapter(this));

  mTMenuItem.setText("MT");
  mTMenuItem.addActionListener(new TMyBergmannProofPanel_mTMenuItem_actionAdapter(this));

  uIMenuItem.setText(chUniquant + "E");
  uGMenuItem.setText(chUniquant + "I");

  eIMenuItem.setText(chExiquant + "E");
  eGMenuItem.setText(chExiquant + "I");


  fRulesMenu.add(dSMenuItem);
  fRulesMenu.add(hSMenuItem);
  fRulesMenu.add(mTMenuItem);


}







void initializeParser(){
fParser=new TBergmannParser();
};

/************* Factory ************/

/* we want this to subclass for other types of proof eg Copi */

public TProofline supplyProofline(){
return
    new TProofline(fParser);
}


TReAssemble supplyTReAssemble (TTestNode root){
return
    new TBergmannReAssemble(fParser, root, null, 0);
}



TTestNode supplyTTestNode (TParser aParser,TTreeModel aTreeModel){         // so we can subclass
    return
        new TBergmannTestNode (aParser,aTreeModel);
  }



/************* End of Factory ************/


/*************  Menu *********************/

  void doSetUpRulesMenu(){

  super.doSetUpRulesMenu();   // we'll just override a few of these


  dSMenuItem.setEnabled(false);
  mTMenuItem.setEnabled(false);
  hSMenuItem.setEnabled(false);



  TFormula selectedFormula=null, secondSelectedFormula=null,thirdSelectedFormula=null;

TProofline selection = fProofListView.oneSelected();
TProofline [] twoSelections = fProofListView.exactlyNLinesSelected(2);
TProofline [] threeSelections = fProofListView.exactlyNLinesSelected(3);

boolean noneSelected,oneSelected, twoSelected,threeSelected;

int totalSelected=fProofListView.totalSelected();

  TProofline headLastLine=fModel.getHeadLastLine();
    TProofline lastAssumption=fModel.findLastAssumption();


noneSelected=(fProofListView.exactlyNLinesSelected(0))!=null;
oneSelected=(selection!=null);
twoSelected=(twoSelections!=null);
threeSelected=(threeSelections!=null);

if (oneSelected)
  selectedFormula=selection.fFormula;

if (twoSelected){
  selectedFormula = twoSelections[0].fFormula;
  secondSelectedFormula = twoSelections[1].fFormula;
}

if (threeSelected){
  selectedFormula = threeSelections[0].fFormula;
  secondSelectedFormula = threeSelections[1].fFormula;
  thirdSelectedFormula = threeSelections[2].fFormula;
  }

  negEMenuItem.setEnabled(false);             //default

  /********** independent of tactics *************/

  if (twoSelected
    &&totalSelected==2
    &&( (fParser.isEquiv(selectedFormula)) &&
            ((selectedFormula.equalFormulas(selectedFormula.fLLink,
                                              secondSelectedFormula))||
               (selectedFormula.equalFormulas(selectedFormula.fRLink,
                                              secondSelectedFormula)))
      || (fParser.isEquiv(secondSelectedFormula)) &&
                  ((selectedFormula.equalFormulas(secondSelectedFormula.fLLink,
                                                    selectedFormula)))||
                    (selectedFormula.equalFormulas(selectedFormula.fRLink,
                                              secondSelectedFormula)))
         )
          equivEMenuItem.setEnabled(true);
       else
       equivEMenuItem.setEnabled(false);


     if (twoSelected){

       //   if (mPPossible(selectedFormula,secondSelectedFormula))
       //      mPMenuItem.setEnabled(true);

       if (mTPossible(selectedFormula, secondSelectedFormula))
         mTMenuItem.setEnabled(true);

       if (hSPossible(selectedFormula,secondSelectedFormula))
       hSMenuItem.setEnabled(true);

     if (dSPossible(selectedFormula,secondSelectedFormula))
       dSMenuItem.setEnabled(true);

     }


     if (twoSelected
         &&totalSelected==2
         && iEPossible(selectedFormula,secondSelectedFormula))
       iEMenuItem.setEnabled(true);
     else
  iEMenuItem.setEnabled(false);




  /******** end ***********/



  if (fTemplate){                             //with Tactics
    TFormula conclusion =findNextConclusion();

    if ((conclusion!=null)&&(noneSelected))   // we are going to allow ~E as a tactic
        negEMenuItem.setEnabled(true);

  }
  else {                                     // without Tactis


    if ((lastAssumption!=null)&&
        (lastAssumption.fFormula!=null)&&                // may be a blankline start
        fParser.isNegation(lastAssumption.fFormula)      // like reductio from ~ assumption
      &&((oneSelected
          &&totalSelected==1
          &&(TFormula.equalFormulas(selectedFormula,TFormula.fAbsurd)))
        ||(twoSelected
           &&totalSelected==2
           &&
           (TFormula.formulasContradict(selectedFormula,secondSelectedFormula)) ) ))
     negEMenuItem.setEnabled(true);
   else
     negEMenuItem.setEnabled(false);




   if (twoSelected
       &&(lastAssumption!=null)
       &&(totalSelected==2)
       &&(TParser.isExiquant(selectedFormula)))
        eIMenuItem.setEnabled(true);
     else
          eIMenuItem.setEnabled(false);





  }


  }


/************* Rules ************/

  TProofline addAssumption(TFormula whichone, int level, int posHorn, int negHorn){

  /*Typically used by subclasses to add assumption after a subproof*/

 TProofline newline=supplyProofline();

 newline.fSubprooflevel = level;
 newline.fFormula = whichone.copyFormula();
 newline.fFirstjustno = posHorn;
 newline.fSecondjustno = negHorn;
 newline.fJustification = fNegEJustification;

 return
     newline;
}



  void doEquivE(){  //like MP

     TProofline newline, firstline, secondline;
     TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

       if (selections != null){

         firstline = selections[0];
         secondline = selections[1];

         if ( (fParser.isEquiv(secondline.fFormula)) &&
             (firstline.fFormula.equalFormulas(secondline.fFormula.fLLink,
                                               firstline.fFormula)) ||
               (firstline.fFormula.equalFormulas(secondline.fFormula.fRLink,
                                               firstline.fFormula)))

            {
           newline = firstline;
           firstline = secondline;
           secondline = newline; //{to make line 1 contain the equiv}
           newline = null;
         }

         if ( (fParser.isEquiv(firstline.fFormula)) &&
             (firstline.fFormula.equalFormulas(firstline.fFormula.fLLink,
                                               secondline.fFormula)||
             (firstline.fFormula.equalFormulas(firstline.fFormula.fRLink,
                                               secondline.fFormula))))
             {

             TFormula target;

             if (firstline.fFormula.equalFormulas(firstline.fFormula.fLLink,
                                               secondline.fFormula))
               target=firstline.fFormula.fRLink;
             else
               target=firstline.fFormula.fLLink;


            newline = supplyProofline();
            int level=fModel.getHeadLastLine().fSubprooflevel;


            newline.fFormula = target.copyFormula();
            newline.fFirstjustno = firstline.fLineno;
            newline.fSecondjustno = secondline.fLineno;
            newline.fJustification = equivEJustification;
            newline.fSubprooflevel = level;

            TUndoableProofEdit newEdit = new TUndoableProofEdit();
            newEdit.fNewLines.add(newline);
            newEdit.doEdit();


         }
       }


 }

 boolean mTPossible(TFormula selected, TFormula secondSelected){


   if ( (fParser.isImplic(selected)) &&
        (fParser.isNegation(secondSelected)) &&
        (selected.equalFormulas(selected.fRLink,
                                           secondSelected.fRLink))
         ||
         (fParser.isImplic(secondSelected)) &&
        (fParser.isNegation(selected)) &&
        (selected.equalFormulas(secondSelected.fRLink,
                                           selected.fRLink)))
     return
         true;
   else
     return
         false;
}

 boolean dSPossible(TFormula selected, TFormula secondSelected){


  if ( (fParser.isNegation(selected) &&
        fParser.isOr(secondSelected) &&
        selected.equalFormulas(selected.fRLink,
                                          secondSelected.fLLink))
        ||
        (fParser.isNegation(secondSelected) &&
        fParser.isOr(selected) &&
        selected.equalFormulas(selected.fLLink,
                                          secondSelected.fRLink)))

    return
        true;
  else
    return
        false;
}



void doDS(){
  /*copi permits this only one way, other systems both ways. Control this by ensuring
  that dsPossible only enable this when it is permitted*/
  TProofline newline, firstline, secondline;
  TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

  if (selections != null) {

    firstline = selections[0];
    secondline = selections[1];

    if ((fParser.isNegation(firstline.fFormula)) &&
        (fParser.isOr(secondline.fFormula))) {

        newline = firstline;
        firstline = secondline;
        secondline = newline; //{to make line 1 contain the or}
        newline = null;
      }

      if ((fParser.isOr(firstline.fFormula)) &&       //pvq
          (fParser.isNegation(secondline.fFormula))&& //~p
          (firstline.fFormula.equalFormulas(secondline.fFormula.fRLink, //p
                                           firstline.fFormula.fLLink)||
          firstline.fFormula.equalFormulas(secondline.fFormula.fRLink, //q
                                           firstline.fFormula.fRLink))
         ) {

        TFormula target;

        if (firstline.fFormula.equalFormulas(secondline.fFormula.fRLink, //p
                                           firstline.fFormula.fLLink))
           target=firstline.fFormula.fRLink; //q
        else
           target=firstline.fFormula.fLLink; //p



        newline = supplyProofline();
        int level = fModel.getHeadLastLine().fSubprooflevel;

        newline.fFormula = target.copyFormula(); //q for Copi but could be p in subclasses
        newline.fFirstjustno = firstline.fLineno;
        newline.fSecondjustno = secondline.fLineno;
        newline.fJustification = dSJustification;
        newline.fSubprooflevel = level;

        TUndoableProofEdit newEdit = new TUndoableProofEdit();
        newEdit.fNewLines.add(newline);
        newEdit.doEdit();

    }
  }
}


 boolean hSPossible(TFormula selected, TFormula secondSelected){


  if ( (fParser.isImplic(selected)) &&
       (fParser.isImplic(secondSelected)) &&
       ((selected.equalFormulas(selected.fRLink,secondSelected.fLLink))  ////p->q q->r
        ||
       (selected.equalFormulas(secondSelected.fRLink,selected.fLLink)))) ////q->r p->q
    return
        true;
  else
    return
        false;
}


 void doHS(){
  TProofline newline, firstline, secondline;
  TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

  if (selections != null) {

    firstline = selections[0];
    secondline = selections[1];

    if ( (fParser.isImplic(firstline.fFormula)) &&
        (fParser.isImplic(secondline.fFormula))) {

      if (firstline.fFormula.equalFormulas(secondline.fFormula.fRLink, //p->q
                                           firstline.fFormula.fLLink)) { //q->r
        newline = firstline;
        firstline = secondline;
        secondline = newline; //{to make line 1 contain the arrow}
        newline = null;
      }

      if (firstline.fFormula.equalFormulas(firstline.fFormula.fRLink, //p->q
                                           secondline.fFormula.fLLink)) { //q->r

        newline = supplyProofline();
        int level = fModel.getHeadLastLine().fSubprooflevel;

        newline.fFormula = (firstline.fFormula).copyFormula(); //p->q
        newline.fFormula.fRLink = secondline.fFormula.fRLink.copyFormula(); // newline now //p->r
        newline.fFirstjustno = firstline.fLineno;
        newline.fSecondjustno = secondline.fLineno;
        newline.fJustification = hSJustification;
        newline.fSubprooflevel = level;

        TUndoableProofEdit newEdit = new TUndoableProofEdit();
        newEdit.fNewLines.add(newline);
        newEdit.doEdit();
      }

    }
  }
}



 void doMT(){

  TProofline newline, firstline, secondline;
  TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

    if (selections != null){

      firstline = selections[0];
      secondline = selections[1];

      if ((fParser.isImplic(secondline.fFormula)) &&
          (fParser.isNegation(firstline.fFormula))) {
        newline = firstline;
        firstline = secondline;
        secondline = newline; //{to make line 1 contain the arrow}
        newline = null;
      }




      if ( (fParser.isImplic(firstline.fFormula)) &&
            (fParser.isNegation(secondline.fFormula))&&

          (firstline.fFormula.fRLink.equalFormulas(firstline.fFormula.fRLink,
                                            secondline.fFormula.fRLink))) {

         newline = supplyProofline();
         int level=fModel.getHeadLastLine().fSubprooflevel;

         TFormula notP=new TFormula(TFormula.unary,
                                    String.valueOf(chNeg),
                                    null,
                                    firstline.fFormula.fLLink.copyFormula());


         newline.fFormula = notP;
         newline.fFirstjustno = firstline.fLineno;
         newline.fSecondjustno = secondline.fLineno;
         newline.fJustification = mTJustification;
         newline.fSubprooflevel = level;

         TUndoableProofEdit newEdit = new TUndoableProofEdit();
         newEdit.fNewLines.add(newline);
         newEdit.doEdit();


      }
    }


 }










  /*In Bergmann negE is like reductio from ~Ass straight to Ass */

  void doNegE(){
   if (fTemplate)
     doHintNegI();
    else{
      TProofline lastAssumption=fModel.findLastAssumption();

      if ((lastAssumption!=null)&& fParser.isNegation(lastAssumption.fFormula)){  // if we haven't got a last assumption we cannot drop it
         TProofline firstLine=fProofListView.oneSelected();

         if (firstLine!=null)
           negEFromAbsurdity(lastAssumption,firstLine);
         else{
            TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

            if (selections != null)
               negEFromContradictoryLines(lastAssumption,selections);
      }

      }
    }
 }

void negEFromContradictoryLines(TProofline lastAssumption,TProofline[] selections){
   if ((lastAssumption!=null)&&
       fParser.isNegation(lastAssumption.fFormula)
       &&TFormula.formulasContradict(selections[0].fFormula,selections[1].fFormula)){
      int level=fModel.getHeadLastLine().fSubprooflevel;
      TUndoableProofEdit  newEdit = new TUndoableProofEdit();

      TFormula conclusion=lastAssumption.fFormula.getRLink();  //F (assumption was ~F)

      newEdit.fNewLines.add(endSubProof(level));
      newEdit.fNewLines.add(addAssumption(conclusion, level-1, selections[0].fLineno, selections[1].fLineno));
      newEdit.doEdit();
      }
   }


String traceAssOverride(){     //negE is like NegI and we need the assumption to be retained.
     return
         fNegEJustification;
}

 void negEFromAbsurdity(TProofline lastAssumption, TProofline firstLine){

   if ((lastAssumption!=null)&&(TFormula.equalFormulas(firstLine.fFormula,TFormula.fAbsurd))) {
    int level=fModel.getHeadLastLine().fSubprooflevel;
    TUndoableProofEdit  newEdit = new TUndoableProofEdit();

    newEdit.fNewLines.add(endSubProof(level));
    newEdit.fNewLines.add(addAssumption(lastAssumption.fFormula, level-1, firstLine.fLineno, 0));
    newEdit.doEdit();
    }
 }

 void doHintNegE(){
   doHintNegI();    // the super routine is written for this.

 }


 /************************ UI ********************************/

 public class UIAction extends AbstractAction{
      JTextComponent fText;
      TProofline fFirstline=null;




       public UIAction(JTextComponent text, String label, TProofline firstline){
         putValue(NAME, label);

         fText=text;
         fFirstline=firstline;
       }

        public void actionPerformed(ActionEvent ae){


          /*********************/


          boolean useFilter = true;
          ArrayList dummy = new ArrayList();

          String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

          TFormula term = new TFormula();
          StringReader aReader = new StringReader(aString);
          boolean wellformed=false;

          wellformed=fParser.term(term,aReader);

          if ((!wellformed)||(!term.isClosedTerm()/*fParser.isAtomicConstant(term)*/)) {
            String message = "The string is neither a constant nor a closed term." +
                (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

            //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

            fText.setText(message);
            fText.selectAll();
            fText.requestFocus();
          }

          else {   // we're good

            TFormula scope = fFirstline.fFormula.fRLink.copyFormula();

            {

            	scope.subTermVar(scope,term,fFirstline.fFormula.quantVarForm());

              TProofline newline = supplyProofline();

              int level = fModel.getHeadLastLine().fSubprooflevel;

              newline.fFormula = scope;
              newline.fJustification = UEJustification;
              newline.fFirstjustno = fFirstline.fLineno;
              newline.fSubprooflevel = level;

              TUndoableProofEdit newEdit = new TUndoableProofEdit();
              newEdit.fNewLines.add(newline);
              newEdit.doEdit();

              removeInputPane();
            }
          }

      }

    }











 void doUI(){
  TProofline firstline;
  JButton defaultButton;
   JButton dropLastButton;
   TProofInputPanel inputPane;


  firstline=fProofListView.oneSelected();

  if ((firstline != null)&&fParser.isUniquant(firstline.fFormula)) {

    JTextField text = new JTextField("Constant, or closed term, to instantiate with?");
       text.selectAll();

       defaultButton = new JButton(new UIAction(text,"Go", firstline));

       JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
       inputPane = new TProofInputPanel("Doing UE", text, buttons,fInputPalette);


       addInputPane(inputPane);

       inputPane.getRootPane().setDefaultButton(defaultButton);
       fInputPane.setVisible(true); // need this
       text.requestFocus();         // so selected text shows


     }


}



 /*********************** End of UI *******************************/


/***********************  UG ************************************/


 //NEED TO FIX TEMPLATE


 public class HintUGAction extends AbstractAction{
    JTextComponent fText;
    TFormula fConclusion=null;
    TFormula fVariable=null,fConstant=null;
    int fStage=1;


     public HintUGAction(JTextComponent text, String label, TFormula conclusion, TFormula variable){
       putValue(NAME, label);

       fText=text;
       fConclusion=conclusion.copyFormula();
       fVariable=variable.copyFormula();
     }

      public void actionPerformed(ActionEvent ae){


        switch (fStage){

case 1:
  findConstant();
  break;

case 2:

 // goodFinish();
  break;

default: ;
}
      }



 void findConstant(){
    String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

    if ((aString!=null)&& (aString.length()>0)){

    fConstant = new TFormula();
StringReader aReader = new StringReader(aString);
boolean wellformed=false;

wellformed=fParser.term(fConstant,aReader);

if ((!wellformed)||(!fParser.isAtomicConstant(fConstant))) {
String message = "The string is not a constant." +
   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

//      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

fText.setText(message);
fText.selectAll();
fText.requestFocus();
}
else {   //must not occur in assumptions or target line (but the latter cannot occur because we substitute for all


// test for assumptions

TFormula freeFormula=fModel.firstAssumptionWithVariableFree(fConstant);

if (freeFormula!=null){
  String message = aString + " occurs in assumption " + fParser.writeFormulaToString(freeFormula);

   fText.setText(message);
   fText.selectAll();
   fText.requestFocus();
}
else{

 fStage=2;
 goodFinish();
}


  }
 }
 }



 private void goodFinish(){

   {

     TUndoableProofEdit newEdit = new TUndoableProofEdit();

TProofline headLastLine=fModel.getHeadLastLine();

int level = headLastLine.fSubprooflevel;
int lastlineno = headLastLine.fLineno;

 TFormula scope = fConclusion.getRLink().copyFormula();

 scope.subTermVar(scope,fConstant,fVariable);

int scopelineno=addIfNotThere(scope, level, newEdit.fNewLines);

if (scopelineno==-1){   // not there
   scopelineno = lastlineno+2;
   lastlineno += 2;
}

     TProofline newline = supplyProofline();

     newline.fFormula = fConclusion;
     newline.fFirstjustno=scopelineno;

     newline.fJustification = UGJustification;
     newline.fSubprooflevel = level;

     newEdit.fNewLines.add(newline);

   newEdit.doEdit();


            removeInputPane();
          }

 }





        }


 public class UGAction extends AbstractAction{
     JTextComponent fText;
     TProofline fFirstline=null;
     TFormula fVariable=null,fConstant=null;
     int fStage=1;


      public UGAction(JTextComponent text, String label, TProofline firstline){
        putValue(NAME, label);

        fText=text;
        fFirstline=firstline;
      }

       public void actionPerformed(ActionEvent ae){


         switch (fStage){

 case 1:
   findVariable();
   break;

 case 2:
   findConstant();
   break;






 default: ;
}
       }







  void findConstant(){
     String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

     if ((aString!=null)&& (aString.length()>0)){

     fConstant = new TFormula();
StringReader aReader = new StringReader(aString);
boolean wellformed=false;

wellformed=fParser.term(fConstant,aReader);

if ((!wellformed)||(!fParser.isAtomicConstant(fConstant))) {
String message = "The string is not a constant." +
    (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

//      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

fText.setText(message);
fText.selectAll();
fText.requestFocus();
}
 else {   //must not occur in assumptions or target line (but the latter cannot occur because we substitute for all


 // test for assumptions

 TFormula freeFormula=fModel.firstAssumptionWithVariableFree(fConstant);

 if (freeFormula!=null){
   String message = aString + " occurs in assumption " + fParser.writeFormulaToString(freeFormula);

    fText.setText(message);
    fText.selectAll();
    fText.requestFocus();
 }
 else{

  fStage=3;
  goodFinish();
 }


   }
  }
  }





  void findVariable(){
    String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

if ((aString==null)||
    (aString.length()!=1)||
    !fParser.isVariable(aString.charAt(0))){

  String message = aString + " is not a variable.";

  fText.setText(message);
  fText.selectAll();
  fText.requestFocus();
}
else {

  fVariable= new TFormula();

  fVariable.fKind = TFormula.variable;
  fVariable.fInfo = aString;

  fStage=2;

  String message = "Constant to generalize on?";

fText.setText(message);
fText.selectAll();
fText.requestFocus();


  ((TProofInputPanel)fInputPane).setLabel1("Doing UI-- Stage2, identifying constant");


  }
  }

  private void goodFinish(){

    {

             TFormula formulanode = new TFormula();

             formulanode.fKind = TFormula.quantifier;
             formulanode.fInfo = String.valueOf(chUniquant);
             formulanode.fLLink = fVariable;


             formulanode.fRLink = fFirstline.fFormula.copyFormula();


             formulanode.fRLink.subTermVar(formulanode.fRLink,fVariable,fConstant);



             TProofline newline = supplyProofline();

             int level = fModel.getHeadLastLine().fSubprooflevel;

             newline.fFormula = formulanode;
             newline.fJustification = UGJustification;
             newline.fFirstjustno = fFirstline.fLineno;
             newline.fSubprooflevel = level;

             TUndoableProofEdit newEdit = new TUndoableProofEdit();
             newEdit.fNewLines.add(newline);
             newEdit.doEdit();

             removeInputPane();
           }

  }





         }


         void doHintUG(){   // enabled only if appropriate



            TFormula conclusion =findNextConclusion();

            TFormula variForm = conclusion.quantVarForm();
            TFormula scope = conclusion.scope();
            JButton defaultButton;
            TProofInputPanel inputPane;


            if (conclusion != null) {


      JTextField text = new JTextField("Constant to generalize on?");
      text.selectAll();

      defaultButton = new JButton(new HintUGAction(text,"Go", conclusion,variForm));

      JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
      inputPane = new TProofInputPanel("Doing UI-- identifying constant", text, buttons,fInputPalette);


      addInputPane(inputPane);

      inputPane.getRootPane().setDefaultButton(defaultButton);
      fInputPane.setVisible(true); // need this
      text.requestFocus();         // so selected text shows

    }

 }




void doUG(){
  TProofline firstline;
  JButton defaultButton;
  JButton dropLastButton;
  TProofInputPanel inputPane;


  if (fTemplate)
    doHintUG();
  else{

    firstline = fProofListView.oneSelected();

    if (firstline != null) {


      JTextField text = new JTextField("Variable of quantification?");
      text.selectAll();

      defaultButton = new JButton(new UGAction(text,"Go", firstline));

      JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
      inputPane = new TProofInputPanel("Doing UI-- Stage 1, identifying variable", text, buttons);


      addInputPane(inputPane);

      inputPane.getRootPane().setDefaultButton(defaultButton);
      fInputPane.setVisible(true); // need this
      text.requestFocus();         // so selected text shows

    }



  }

}



/*********************** End of UG ******************************/


/*********************** EG **************************************/


public class EGYesNoAction extends AbstractAction{


   EGAction fParent;
   boolean fYes;

   public EGYesNoAction(EGAction parent,boolean yes){

     if (yes)
       putValue(NAME, "Yes");
     else
       putValue(NAME, "No");

     fParent=parent;
     fYes=yes;

   }

   public void actionPerformed(ActionEvent ae){

      TFormula surgeryTerm;

     if (fParent.fNumTreated<fParent.fNumOccurrences){

        surgeryTerm= fParent.fTerms[fParent.fNumTreated];

        surgeryTerm.fInfo=surgeryTerm.fInfo.substring(1);  // surgically omits the marker which is leading


        if (fYes){
           surgeryTerm.fKind = TFormula.variable;
           surgeryTerm.fInfo = fParent.fVariable.fInfo; // (*surgery*)
           surgeryTerm.fRLink = null;  // important becuase there might be the rest of a term there
        }

       // if they have pressed the No button, fYes is false and we do nothing

       fParent.fNumTreated+=1;

   }

     if (fParent.fNumTreated<fParent.fNumOccurrences){
                   // put the marker in the next one

       fParent.fTerms[fParent.fNumTreated].fInfo= chInsertMarker+
                                                    fParent.fTerms[fParent.fNumTreated].fInfo;


         String message= fParser.writeFormulaToString(fParent.fCopy);


         fParent.fText.setText(message);

         fParent.fText.requestFocus();

     }
     else{                                        //  last one, return to parent

      JButton defaultButton = new JButton(fParent);

      JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
      TProofInputPanel inputPane = new TProofInputPanel("Doing EG-- Stage4,"+
            " displaying scope. " +
            "If suitable, press Go.", fParent.fText, buttons);


      addInputPane(inputPane);

      String message= fParser.writeFormulaToString(fParent.fCopy);

       fParent.fText.setEditable(true);
       fParent.fText.setText(message);
       fParent.fText.selectAll();




      inputPane.getRootPane().setDefaultButton(defaultButton);
      fInputPane.setVisible(true); // need this
      fParent.fText.requestFocus();         // so selected text shows



/********/





   fParent.fStage=4;


      /************/






     }


   }

 }




public class EGAction extends AbstractAction{
  //    JTextComponent fText;

  JTextField fText;


      TProofline fFirstline=null;
      TFormula fTerm=null, fVariable=null, fScope=null, fCopy=null,
          fCurrentNode=null,fCurrentCopyNode=null;
      int fNumOccurrences=0; //of term
      int fNumTreated=0;
      int fStage=1;
      TFormula.MarkerData markerData;

      TFormula [] fTerms; // the occurrences of the (same) term in the intended scope


      boolean useFilter=true;

     /*We have here to get three things out of the User:- the constant to generalize on,
      the variable to generalize with, and the occurrences. Since we might enter
      this several times, we initialize fTerm to null. Then, when we get the
      term it is set to a value, and so on. And we do only one of these things per pass through */


       public EGAction(JTextField text, String label, TProofline firstline){
         putValue(NAME, label);


         fText=text;
         fFirstline=firstline;

         fCopy = fFirstline.fFormula.copyFormula();



       }

        public void actionPerformed(ActionEvent ae){
          // typically this will be called 3 times for the 3 stages


         /* if (fTerm==null)   // First stage, trying to find the constant
             find
          ();
          else{
            if (fVariable == null)  // Second stage, we have the term trying for variable
                  findVariable();
            else{

              fNumOccurrences = (fFirstline.fFormula).numOfFreeOccurrences(fTerm);
              if (fNumOccurrences < 2) {
                fLastStage = true;
                //  doLastStage

                doOccurrences();
              }
            }
          }  */

          switch (fStage){

            case 1:
              findConstant();
              break;

            case 2:
              findVariable();
              break;

            case 3:
              displayScope();
              break;

            case 4:
              readScope();
              break;




            default: ;
          }
          }


private void displayScope(){

   String message= fParser.writeFormulaToString(fCopy);

   fText.setText(message);
   fText.selectAll();
   fText.requestFocus();

   fStage=4;

}


private void readScope(){


    if (fScope==null){
      boolean useFilter = true;
      ArrayList dummy = new ArrayList();

      String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

      TFormula root = new TFormula();
      StringReader aReader = new StringReader(aString);
      boolean wellformed;

      wellformed = fParser.wffCheck(root, /*dummy,*/ aReader);

      if (!wellformed) {
        String message = "The string is illformed." +
            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");

        fText.setText(message);
        fText.selectAll();
        fText.requestFocus();
      }
      else {
        fScope = root;

        testScope();
      }
    }


}

private void testScope(){

	fScope.subTermVar(fScope,fTerm,fVariable);


  if (! fScope.equalFormulas(fScope,fFirstline.fFormula)){

    fTerm=null;
    fVariable=null;
    fScope=null;


    String message = "That cannot be the scope of your generalization-- please start again. " +
                     "Term to generalize on? " +
                     "Sub term var does not give original";
    fText.setText(message);
    fText.selectAll();
    fText.requestFocus();

    fStage=1;
  }
  else{

    TFormula temp = fScope.copyFormula();

    if (!temp.freeForTest(fTerm, fVariable)){

      String message = fParser.writeFormulaToString(fTerm)+
                      " for " +
                       fParser.writeFormulaToString(fVariable)+
                       " in " +
                       fParser.writeFormulaToString(temp)+


                      "leads to capture-- please start again. " +
                      "Term to generalize on? " ;
       fText.setText(message);
       fText.selectAll();
       fText.requestFocus();

       fStage=1;

    }
    else
      goodFinish();


  }

}


private void goodFinish(){

  TFormula formulanode = new TFormula();

  formulanode.fKind = TFormula.quantifier;
  formulanode.fInfo = String.valueOf(chExiquant);
  formulanode.fLLink = fVariable;
  formulanode.fRLink = fCopy;


                        TProofline newline = supplyProofline();

                        int level = fModel.getHeadLastLine().fSubprooflevel;

                        newline.fFormula = formulanode;
                        newline.fJustification = EGJustification;
                        newline.fFirstjustno = fFirstline.fLineno;
                        newline.fSubprooflevel = level;

                        TUndoableProofEdit newEdit = new TUndoableProofEdit();
                        newEdit.fNewLines.add(newline);
                        newEdit.doEdit();

                        removeInputPane();



          }


private void alterCopy (TFormula termPart, TFormula variable){

  termPart.fKind=TFormula.variable;     /*surgery*/
  termPart.fInfo=variable.fInfo;
  termPart.fRLink=null;       // need this to get rid of any subterms

}

private void removeMarker(boolean alterCopy){
  /* {removes marker and alters copy if needed}                 */

   fCurrentNode.fInfo=fCurrentNode.fInfo.substring(1);  // omits the marker which is leading

   if (alterCopy){
     fCurrentCopyNode.fKind = TFormula.variable;
     fCurrentCopyNode.fInfo = fVariable.fInfo; // (*surgery*)

     fCurrentCopyNode.fRLink = null;  // important becuase there might be the rest of a term there


   }



}





          private void findConstant(){   // in Gentzen a term will do, but Bergmann requires constant
    String message;


    String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

            TFormula term = new TFormula();
            StringReader aReader = new StringReader(aString);
            boolean wellformed = false;

            wellformed = fParser.term(term, aReader);

            if ((!wellformed)||(!fParser.isAtomicConstant(term))) {
              message = "The string is not a constant." +
                  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns


              fText.setText(message);
              fText.selectAll();
              fText.requestFocus();
            }

            else {
              fTerm = term; // term found, end of first stage

              message = "Variable to quantify with?";
              fText.setText(message);
              fText.selectAll();
              fText.requestFocus();

              fStage=2;

              ((TProofInputPanel)fInputPane).setLabel1("Doing EG-- Stage2, identifying variable");

            }
  }

 private void findVariable(){
   String aString;
   String message;

   aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

   if ((aString==null)||
     (aString.length()!=1)||
     !fParser.isVariable(aString.charAt(0))){

     message = aString +
         " is not a variable. " +
         "Variable to quantify with?";

     fText.setText(message);
     fText.selectAll();
     fText.requestFocus();
   }
   else { // variable found, end of second stage

     fVariable = new TFormula();

     fVariable.fKind = TFormula.variable;
     fVariable.fInfo = aString;

     fNumOccurrences = (fFirstline.fFormula).numOfFreeOccurrences(fTerm);



   /*  if ((fNumOccurrences ==0)||
        (fNumOccurrences ==1) ){          nov 11 2007 vacuous quantification*/
   if (fNumOccurrences ==0){

   /*     if (fNumOccurrences ==1) {
          TFormula surgeryTerm = fCopy.nthFreeOccurence(fTerm, 1);

          if (surgeryTerm != null)
            alterCopy(surgeryTerm, fVariable);
           }  nov 11 2007 vacuous quantification */


        ((TProofInputPanel)fInputPane).setLabel1("Doing EG-- Stage4,"+
            " displaying scope. " +
            "If suitable, press Go.");


     message= fParser.writeFormulaToString(fCopy);

   fText.setText(message);
   fText.selectAll();
   fText.requestFocus();

   fStage=4;
     }
     else{
       if (fNumOccurrences >0) {  //used to be 1


         ((TProofInputPanel)fInputPane).setLabel1("Doing EG-- Stage3,"+
            " Occurrences. " +
            "Generalize on this one?");

         fTerms = new TFormula[fNumOccurrences];

         for (int i=0;i<fNumOccurrences;i++){             // initialize

           fTerms[i] = fCopy.nthFreeOccurence(fTerm, i + 1);   // one uses zero based index, other 1 based
         }



         fTerms[0].fInfo= chInsertMarker+ fTerms[0].fInfo;


          /********* going to yes/no subroutine *****/

          boolean yes=true;

       JButton yesButton = new JButton(new EGYesNoAction(this,yes/*text,"Go", firstline*/));
       JButton noButton = new JButton(new EGYesNoAction(this,!yes/*text,"Go", firstline*/));


       message= fParser.writeFormulaToString(fCopy);

      //JTextField text = new JTextField(message);

      fText.setText(message);

      JButton[]buttons = {noButton, yesButton };  // put cancel on left
      TProofInputPanel inputPane = new TProofInputPanel("Doing EG-- Stage3, generalize on this occurrence?", fText, buttons);


      addInputPane(inputPane);



//fText.setText(message);
//fText.selectAll();
//fText.requestFocus();





 //     inputPane.getRootPane().setDefaultButton(defaultButton);
            fInputPane.setVisible(true); // need this
            fText.setEditable(false);
           fText.requestFocus();         // so selected text shows








     //


         message= fParser.writeFormulaToString(fCopy);

fText.setText(message);
fText.selectAll();
fText.requestFocus();

fStage=4;




     /*    markerData= firstFormula.supplyMarkerData(fTerm,
                                      fNumOccurrences,
                                      metSoFar,
                                      firstFormula,
                                      fCopy,
                                      done,
                                      currentNode,
                                      currentCopyNode
                                      );

         /* NewInsertMarker(termForm, 1, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode); */


    /*     firstFormula.newInsertMarker(markerData);

         boolean alterCopy=true;

         removeMarker(alterCopy);  */


     //    System.out.print("run through insert");

       }

     }

  }

 }



      }


public void doEG(){



/*

 {This is quite complicated because generalization is done on individual occurrences of a }
             {term.  I take a copy.  Then I insert and remove markers in the}
             {original and display it and alter the copy if the user indicates.}

*/

  TProofline firstline;
  JButton defaultButton;
 // JButton dropLastButton;
  TProofInputPanel inputPane;

  if (fTemplate)
    doHintEG();
  else{
     firstline=fProofListView.oneSelected();

     if (firstline != null) {

        JTextField text = new JTextField("Constant to generalize on?");
        text.selectAll();

     defaultButton = new JButton(new EGAction(text,"Go", firstline));

     JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
     inputPane = new TProofInputPanel("Doing EG-- Stage1, identifying constant", text, buttons);


     addInputPane(inputPane);




     inputPane.getRootPane().setDefaultButton(defaultButton);
     fInputPane.setVisible(true); // need this
     text.requestFocus();         // so selected text shows


   }

  }



 }



/*********************** End of EG *******************************/


/************************ EI ************************************/

 TFormula equalPwithAforX(TFormula variable,TFormula scope, TFormula assumption){
   //  scope is Px, assumption needs to be P[a/x], this finds and returns a

   TFormula constant= new TFormula(TFormula.functor,"dummy",null,null);
   TFormula result;
   char searchCh;

   for (int i=0;i<fParser.gConstants.length();i++){

     constant.fInfo=String.valueOf(fParser.gConstants.charAt(i));

    result=scope.copyFormula();

    result.subTermVar(result,constant,variable);

    if (result.equalFormulas(result,assumption))
       return
          constant;
   }

   return
       null;
 }

 void doEI(){
   TFormula variForm, scope,exiForm;

   TProofline subhead, subtail;
   String exiFormStr;
   String scopeStr;

   if (fTemplate)
     doHintEI();
   else {

     TProofline firstline;
     TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

     if (selections != null) {

       firstline = selections[0];
       subtail = selections[1];

       if (fParser.isExiquant(firstline.fFormula)) {

         variForm = firstline.fFormula.quantVarForm();
         scope = firstline.fFormula.scope();
         scopeStr = fParser.writeFormulaToString(scope);
         exiForm=firstline.fFormula;
         exiFormStr = fParser.writeFormulaToString(exiForm);

         subhead = fModel.findLastAssumption();

         if (subhead != null) {

           //  scope is Px, assumption needs to be P[a/x]

           TFormula constant = equalPwithAforX(variForm, scope,
                                               subhead.getFormula());

           if (constant == null) {
              bugAlert("DoingEI. Warning.", fParser.writeFormulaToString(subhead.fFormula) +
                      " is not a substitution instance of " +
                      scopeStr +
                      ".");
              return; // leave at that point
           }

           if ((constant != null)&&
              (exiForm.freeTest(constant))) { //occurs in exiform

               String outPutStr = fParser.writeFormulaToString(exiForm);

               //  BugAlert(concat(firstline.fFormula.QuantVarForm.fInfo, ' is free in ', outputStr, '.'));

               bugAlert("DoingEI. Warning.",
                        fParser.writeFormulaToString(constant) +
                        " occurs in " +
                        outPutStr +
                        ".");
               return; // leave at that point
             }


             if ((constant != null)&&
                (subtail.fFormula.freeTest(constant))) { //free in target

                 String outPutStr = fParser.writeFormulaToString(subtail.
                     fFormula);

                 bugAlert("DoingEI. Warning.",
                          fParser.writeFormulaToString(constant) +
                          " occurs in " +
                          outPutStr +
                          ".");
                 return; // leave at that point
               }


               if (constant != null){ // looking for free in premises

                 // test for free

                 TFormula freeFormula = fModel.firstAssumptionWithVariableFree(
                     constant); //check this, pasted from UG

                 /* now, it is allowed to be free in the assumption of the instantion, so .... */

                 if ( (freeFormula != null) &&
                     freeFormula.equalFormulas(freeFormula, subhead.fFormula))
                   freeFormula = null; // this one does not count

                 if (freeFormula != null) {
                   bugAlert("DoingEI. Warning.",
                            fParser.writeFormulaToString(constant) +
                            " occurs in assumption " +
                            fParser.writeFormulaToString(freeFormula) +
                            ".");
                    return; // leave at that point
                 }

                // all good

                   int level = fModel.getHeadLastLine().fSubprooflevel;

                   TUndoableProofEdit newEdit = new TUndoableProofEdit();

                   newEdit.fNewLines.add(endSubProof(level));
                   newEdit.fNewLines.add(addExTarget(subtail.fFormula, level - 1,
                       firstline.fLineno, subtail.fLineno));
                   newEdit.doEdit();

                 }
               }
             }

           }
         }
       }


       void doHintEI(){   //menu enabled only if existential selected and conclusion exists



       TProofline selection = fProofListView.oneSelected();
       TFormula selectedFormula=selection.fFormula;

       TFormula conclusion =findNextConclusion();

        TFormula variForm = selectedFormula.quantVarForm();
        TFormula scope = selectedFormula.scope();

        TFormula exiForm=selectedFormula;




        /*what we need to do is to find a suitable instantiating constant ,*/

        TFormula constant= new TFormula(TFormula.functor,"dummy",null,null);
        TFormula result;
        char searchCh;
        boolean found=false;

        for (int i=0;(i<fParser.gConstants.length())&&!found;i++){

          constant.fInfo=String.valueOf(fParser.gConstants.charAt(i));

          if (!exiForm.freeTest(constant)&&         // not in Exi
              !conclusion.freeTest(constant)&&      // not in Target
              fModel.firstAssumptionWithVariableFree(constant)==null)// not in assumptions
                found=true;
        }

        if (!found)
           {
                 bugAlert("DoingEI. Warning.", "There is no suitable constant to instantiate with.");
                  return; // leave at that point
               }

         else {  // we're good

              // make the new assumption

              scope=scope.copyFormula();

              scope.subTermVar(scope,constant,variForm);
{                     // everything ok

            TUndoableProofEdit newEdit = new TUndoableProofEdit();

            TProofline headLastLine = fModel.getHeadLastLine();



            int level = headLastLine.fSubprooflevel;
            int lastlineno = headLastLine.fLineno;

            TProofline newline = supplyProofline();


            newline.fFormula=scope;
        newline.fJustification= fAssJustification;
        newline.fSubprooflevel= level+1;
        newline.fLastassumption=true;

        newEdit.fNewLines.add(newline);

        newline = supplyProofline();




            int conclusionlineno = addIfNotThere(conclusion, level+1, newEdit.fNewLines);

            if (conclusionlineno == -1) { // not there
              conclusionlineno = lastlineno + 3;   // the assumption and the ?
              lastlineno += 3;
            }

            newEdit.fNewLines.add(endSubProof(level+1));

            newEdit.fNewLines.add(addExTarget(conclusion, level, selection.fLineno,conclusionlineno));

            newEdit.doEdit();

          }
        }


     }





/************************ End of EI *****************************/



/************************ Rule of Do=I **********************************/



     public class IIAction extends AbstractAction{
       JTextComponent fText;
       TFormula fRoot=null;

      /*We just need to get the term  */


        public IIAction(JTextComponent text, String label){
          putValue(NAME, label);

          fText=text;
        }

         public void actionPerformed(ActionEvent ae){

           boolean useFilter=true;


           String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

           if ((aString==null)||
               (aString.length()!=1)||
               !fParser.isVariable(aString.charAt(0))){

             String message = aString + " is not a variable.";

             fText.setText(message);
             fText.selectAll();
             fText.requestFocus();
           }
           else {                       //got the variable

             TFormula variablenode= new TFormula();

             variablenode.fKind = TFormula.variable;
             variablenode.fInfo = aString;



               TFormula formulanode = new TFormula();

               formulanode.fKind = TFormula.quantifier;
               formulanode.fInfo = String.valueOf(chUniquant);
               formulanode.fLLink = variablenode;
               formulanode.fRLink = TFormula.equateTerms(variablenode,variablenode.copyFormula());

               TProofline newline = supplyProofline();

               int level = fModel.getHeadLastLine().fSubprooflevel;

               newline.fFormula = formulanode;
               newline.fJustification = IIJustification;
               newline.fSubprooflevel = level;

               TUndoableProofEdit newEdit = new TUndoableProofEdit();
               newEdit.fNewLines.add(newline);
               newEdit.doEdit();

               removeInputPane();
             }

   /*        boolean wellformed;

           TFormula term = new TFormula();

          wellformed=getTheTerm(fText,term);

          if (wellformed){
            fRoot=term;

           TProofline newline = supplyProofline();

            newline.fFormula = TFormula.equateTerms(fRoot,fRoot.copyFormula());
            newline.fJustification = IIJustification;
             newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

             TUndoableProofEdit newEdit = new TUndoableProofEdit();
             newEdit.fNewLines.add(newline);
             newEdit.doEdit();

             removeInputPane();
           }
 */
       }

     }


     public void doII(){
    JButton defaultButton;
    JButton dropLastButton;
    TProofInputPanel inputPane;


    JTextField text = new JTextField("Variable?");

       text.setDragEnabled(true);
       text.selectAll();

       defaultButton = new JButton(new IIAction(text,"Go"));

       JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
       inputPane = new TProofInputPanel("Doing Identity Introduction", text, buttons);


            addInputPane(inputPane);

            inputPane.getRootPane().setDefaultButton(defaultButton);
            fInputPane.setVisible(true); // need this
            text.requestFocus();         // so selected text shows
    }




/************************ End of Do=I **********************************/

/************************ Rule of Do=E **********************************/

boolean iEPossible(TFormula first,TFormula second){  //need two, at least one an identity with only closed terms

  int inFirst=0;
  int inSecond=0;  //we need to have an identity of closed terms a=b and then one of the terms in the other formula

  if (TParser.isEquality(first)&&
      first.firstTerm().isClosedTerm() &&
      first.secondTerm().isClosedTerm())
         inSecond= second.numOfFreeOccurrences(first.firstTerm()) + second.numOfFreeOccurrences(first.secondTerm());

       if (TParser.isEquality(second)&&
         second.firstTerm().isClosedTerm() &&
         second.secondTerm().isClosedTerm())
            inFirst= first.numOfFreeOccurrences(second.firstTerm()) + first.numOfFreeOccurrences(second.secondTerm());

  if ((inFirst+inSecond)>0)
    return
        true;

return
      false;
}

public void doIE(){
 TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

   if (selections != null) {
    TProofline firstline =  selections[0];
    TProofline secondline =  selections[1];

    if (iEPossible(firstline.getFormula(),secondline.getFormula()))
           orderForSwap(firstline, secondline); // this launches or puts up a prelim dialog which launches
        //{we allow substitution is any formula provided only closed terms in the equality
  }
}


private void orderForSwap(TProofline firstline, TProofline secondline){
/*{this determines which we are going to subs in-- they could both be identities}
// this launches or puts up a prelim dialog which itself launches
we want the identity as the second line and the formula it is substituted in as the first line */

   int dispatcher=0;
   int inFirst=0;
   int inSecond=0;

   TFormula first=firstline.getFormula();
   TFormula second=secondline.getFormula();

   if (TParser.isEquality(first)&&
       first.firstTerm().isClosedTerm() &&
       first.secondTerm().isClosedTerm())
          inSecond= second.numOfFreeOccurrences(first.firstTerm()) + second.numOfFreeOccurrences(first.secondTerm());

        if (TParser.isEquality(second)&&
          second.firstTerm().isClosedTerm() &&
          second.secondTerm().isClosedTerm())
             inFirst= first.numOfFreeOccurrences(second.firstTerm()) + first.numOfFreeOccurrences(second.secondTerm());

  if ((inFirst+inSecond)==0)
    return; //cannot do it



   if (fParser.isEquality(first)&&
      first.firstTerm().isClosedTerm() &&
      first.secondTerm().isClosedTerm())
    {
    if (!  //NOT! DON'T MISS IT
        (fParser.isEquality(second)&&
        second.firstTerm().isClosedTerm() &&
        second.secondTerm().isClosedTerm())
       )
       dispatcher=2;
     else
       dispatcher=3;    // both
   }
   else
     dispatcher=1;     //first not, second is

   switch (dispatcher){
     case 0: break;   // neither an identity cannot happen because orderForSwap called only if at least one is
     case 1:          // what we want first not identity second is
       launchIEAction(firstline,secondline);
       break;
     case 2: {        // wrong way round so we swap
       TProofline temp=firstline;
       firstline=secondline;
       secondline=temp;  // now the secondline is the identity
       launchIEAction(firstline,secondline);
       break;
     }
     case 3: {               // both identities

       /*{now, if neither of the second terms appear in the first, we want to subs in the second}
          {if neeither of the first terms appear in the second, we want to subs in the first}
        {otherwise we have to ask} Don't fully understand the logic of this Jan06
        oh, I suppose it is this a=b and f(a)=c, can only subs in second etc.*/

       if (inFirst == 0) {
         TProofline temp = firstline;
         firstline = secondline;
         secondline = temp; // now the secondline is the identity
         launchIEAction(firstline,secondline);
       }
       else {
         if (inSecond == 0) { // leave them as they are, both identities some in first none in second
           launchIEAction(firstline,secondline);
         }
         else { // we ask

           TProofInputPanel inputPane;
           JTextField text = new JTextField(
               "Do you wish to substitute in the first or in the second?");

           text.setDragEnabled(true);
           text.selectAll();

           boolean isFirst = true;

           JButton firstButton = new JButton(new FirstSecondAction(isFirst,
               firstline, secondline));
           JButton secondButton = new JButton(new FirstSecondAction(!isFirst,
               firstline, secondline));

           JButton[] buttons = {
               new JButton(new CancelAction()), firstButton, secondButton}; // put cancel on left
           inputPane = new TProofInputPanel("Doing Identity Elimination", text,
                                            buttons);

           addInputPane(inputPane);

           //inputPane.getRootPane().setDefaultButton(firstButton);    //I don't think we want a default
           fInputPane.setVisible(true); // need this
           text.requestFocus(); // so selected text shows

         }
       }
     break;}
   }
 }




/************************ End of Do=E **********************************/


/**********************   Actions *********************/

 void dSMenuItem_actionPerformed(ActionEvent e) {
        doDS();
    }

    class TMyBergmannProofPanel_dSMenuItem_actionAdapter implements java.awt.event.ActionListener {
          TMyBergmannProofPanel adaptee;

          TMyBergmannProofPanel_dSMenuItem_actionAdapter(TMyBergmannProofPanel adaptee) {
            this.adaptee = adaptee;
          }
          public void actionPerformed(ActionEvent e) {
            adaptee.dSMenuItem_actionPerformed(e);
          }
    }



 void hSMenuItem_actionPerformed(ActionEvent e) {
    doHS();
}


 class TMyBergmannProofPanel_hSMenuItem_actionAdapter implements java.awt.event.ActionListener {
      TMyBergmannProofPanel adaptee;

      TMyBergmannProofPanel_hSMenuItem_actionAdapter(TMyBergmannProofPanel adaptee) {
        this.adaptee = adaptee;
      }
      public void actionPerformed(ActionEvent e) {
        adaptee.hSMenuItem_actionPerformed(e);
      }
}




 void mTMenuItem_actionPerformed(ActionEvent e) {
    doMT();
    }

    class TMyBergmannProofPanel_mTMenuItem_actionAdapter implements java.awt.event.ActionListener {
      TMyBergmannProofPanel adaptee;

      TMyBergmannProofPanel_mTMenuItem_actionAdapter(TMyBergmannProofPanel adaptee) {
        this.adaptee = adaptee;
      }
      public void actionPerformed(ActionEvent e) {
        adaptee.mTMenuItem_actionPerformed(e);
      }
}


}
