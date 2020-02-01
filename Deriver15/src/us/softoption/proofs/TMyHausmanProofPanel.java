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

import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.TPreferences;
import us.softoption.interpretation.TTestNode;
import us.softoption.parser.TFormula;
import us.softoption.parser.THausmanParser;

public class TMyHausmanProofPanel extends TMyCopiProofPanel{

  public TMyHausmanProofPanel(TDeriverDocument itsDeriverDocument){
  super(itsDeriverDocument);

  fNegIJustification=" IP";
}


void initializeParser(){
  fParser=new THausmanParser();
};

/************* Factory ************/

/* we want this to subclass for other types of proof eg Copi */

public TProofline supplyProofline(){
  return
      new THausmanProofline(fParser);
}


TReAssemble supplyTReAssemble (TTestNode root){
  return
      new THausmanReAssemble(fParser, root, null, 0);
}


/************* End of Factory ************/


void alterRulesMenu(){
fRulesMenu.removeAll();

orIMenuItem.setText("Add");
//addMenuItem.addActionListener(new TCopiProofPanel_addMenuItem_actionAdapter(this));
cDMenuItem.setText("CD");
cDMenuItem.addActionListener(new TCopiProofPanel_cDMenuItem_actionAdapter(this));
andIMenuItem.setText("Conj");//conjMenuItem.setText("Conj.");
//conjMenuItem.addActionListener(new TCopiProofPanel_conjMenuItem_actionAdapter(this));
dSMenuItem.setText("DS");
dSMenuItem.addActionListener(new TCopiProofPanel_dSMenuItem_actionAdapter(this));
hSMenuItem.setText("HS");
hSMenuItem.addActionListener(new TCopiProofPanel_hSMenuItem_actionAdapter(this));
//mPMenuItem.setText("M.P.");
//mPMenuItem.addActionListener(new TCopiProofPanel_mPMenuItem_actionAdapter(this));
mTMenuItem.setText("MT");
mTMenuItem.addActionListener(new TCopiProofPanel_mTMenuItem_actionAdapter(this));
simpMenuItem.setText("Simp");
simpMenuItem.addActionListener(new TCopiProofPanel_simpMenuItem_actionAdapter(this));


implicIMenuItem.setText("CP");
implicEMenuItem.setText("MP");
negIMenuItem.setText("IP");

fRulesMenu.add(implicEMenuItem); // fRulesMenu.add(mPMenuItem);
fRulesMenu.add(mTMenuItem);
fRulesMenu.add(dSMenuItem);
fRulesMenu.add(simpMenuItem);
fRulesMenu.add(andIMenuItem);//fRulesMenu.add(conjMenuItem);
fRulesMenu.add(hSMenuItem);
fRulesMenu.add(orIMenuItem);//fRulesMenu.add(addMenuItem);
fRulesMenu.add(cDMenuItem);



fRulesMenu.add(tIMenuItem);
fRulesMenu.add(implicIMenuItem);
fRulesMenu.add(negIMenuItem);

fRulesMenu.add(uGMenuItem);
fRulesMenu.add(uIMenuItem);
fRulesMenu.add(eGMenuItem);
fRulesMenu.add(eIMenuItem);
fRulesMenu.add(rAMenuItem);
  if (TPreferences.fUseAbsurd)
      fRulesMenu.add(absurdMenuItem);

rewriteMenuItem.setText("Replacement Rules");


}














void doSimp(){   // we want to bypass Copi is does this only one way
doAndE();
}


boolean cDPossible(TFormula first, TFormula second, TFormula third){
/* one has to be V the other two -> */

 TFormula temp;

 for (int i=0;i<3;i++){
   temp=first;first=second;second=third;third=temp;

   if (fParser.isOr(first) && // pVq
       fParser.isImplic(second) && // p->r
       fParser.isImplic(third) && // q->s
       (first.equalFormulas(first.fLLink, second.fLLink) && //p
        first.equalFormulas(first.fRLink, third.fLLink)) //q
       ||
       (first.equalFormulas(first.fRLink, second.fLLink) && // second is q->s
        first.equalFormulas(first.fLLink, third.fLLink))) // third is p->r

     return
         true;
 }
    return
        false;
}

  void doCD(){
    TProofline newline, firstline, secondline, thirdline, temp;
    TProofline[] selections = fProofListView.exactlyNLinesSelected(3);

    if (selections != null) {

      firstline = selections[0];
      secondline = selections[1];
      thirdline = selections[2];

      if (!fParser.isOr(firstline.fFormula)){
        temp=firstline;firstline=secondline;secondline=thirdline;thirdline=temp;
        if (!fParser.isOr(firstline.fFormula)){
        temp=firstline;firstline=secondline;secondline=thirdline;thirdline=temp;
        }
      }

      if (fParser.isOr(firstline.fFormula)&&
          fParser.isImplic(secondline.fFormula)&&
          fParser.isImplic(thirdline.fFormula)) {

        if (!(firstline.getFormula().equalFormulas(firstline.getFormula().fLLink, secondline.getFormula().fLLink) && //p
            firstline.getFormula().equalFormulas(firstline.getFormula().fRLink, thirdline.getFormula().fLLink))){     //q

           temp=secondline;secondline=thirdline;thirdline=temp;
        }

        // first is pVQ second is P->r, third is q->s

        }

        if (fParser.isOr(firstline.fFormula) &&       //pvr
            fParser.isImplic(secondline.fFormula) && //(p->q)
            fParser.isImplic(thirdline.fFormula) && //(r->s)

            firstline.getFormula().equalFormulas(firstline.getFormula().fLLink, secondline.getFormula().fLLink) && //p
            firstline.getFormula().equalFormulas(firstline.getFormula().fRLink, thirdline.getFormula().fLLink)) {


          TFormula qVs=new TFormula(TFormula.binary,
                                    String.valueOf(chOr),
                                    secondline.fFormula.fRLink.copyFormula(),
                                    thirdline.fFormula.fRLink.copyFormula());

          newline = supplyProofline();
          int level = fModel.getHeadLastLine().fSubprooflevel;

          newline.fFormula = qVs; //q
          newline.fFirstjustno = firstline.fLineno;
          newline.fSecondjustno = secondline.fLineno;
          newline.fJustification = cDJustification;
          newline.fSubprooflevel = level;

          TUndoableProofEdit newEdit = new TUndoableProofEdit();
          newEdit.fNewLines.add(newline);
          newEdit.doEdit();

      }
    }
}



boolean dSPossible(TFormula selected, TFormula secondSelected){  //either way


  if ( (fParser.isNegation(selected) &&
        fParser.isOr(secondSelected) &&
        (selected.equalFormulas(selected.fRLink,
                                          secondSelected.fLLink)||
         selected.equalFormulas(selected.fRLink,
                                          secondSelected.fRLink)))
        ||
        (fParser.isNegation(secondSelected) &&
        fParser.isOr(selected) &&
        (selected.equalFormulas(selected.fLLink,
                                          secondSelected.fRLink)||
         selected.equalFormulas(selected.fRLink,
                                          secondSelected.fRLink))))

    return
        true;
  else
    return
        false;
}


void doSetUpRulesMenu(){
  super.doSetUpRulesMenu();

  if (fTemplate){

    TFormula conclusion = findNextConclusion();

    if (conclusion != null)
      negIMenuItem.setEnabled(true);  // with Hausman conclusion can be positive
  }
  else{  // no template

    TProofline headLastLine = fModel.getHeadLastLine();
    TProofline lastAssumption = fModel.findLastAssumption();



  }
}

/********************* IP or neg I */


/*For IP or negI, but Hausman does not add double negation */

/*creates and returns the negation of the assumption, but if the assumption itself
 is a negation just returns the assumption ie the 'opposite' */

public boolean negIPossible(TProofline lastAssumption,
                     boolean oneSelected,
                     boolean twoSelected,
                     TFormula selectedFormula,
                     TFormula secondSelectedFormula,
                     int totalSelected){
  if ((lastAssumption!=null)&&
  (lastAssumption.fFormula!=null)&&
  fParser.isNegation(lastAssumption.fFormula)   // Hausman starts only from a negation
  &&((oneSelected
      &&totalSelected==1
      &&fParser.isContradiction(selectedFormula))
    ||(twoSelected
       &&totalSelected==2
       &&
       (TFormula.formulasContradict(selectedFormula,secondSelectedFormula)) ) ))
 return
     true;
else
 return
     false;
 }


TProofline addNegAssumption(TFormula whichone, int level, int posHorn, int negHorn){
  TProofline newline=supplyProofline();
  TFormula formulanode = null;

  if (fParser.isNegation(whichone))
    formulanode =whichone.fRLink.copyFormula();
  else{

    formulanode = new TFormula(TFormula.unary,
                               String.valueOf(chNeg),
                               null,
                               whichone.copyFormula());

 //   formulanode.fKind = TFormula.unary;
 //   formulanode.fInfo = String.valueOf(chNeg);
 //   formulanode.fRLink = whichone.copyFormula();
  }

  newline.fSubprooflevel = level;
  newline.fFormula = formulanode;
  newline.fFirstjustno = posHorn;
  newline.fSecondjustno = negHorn;
  newline.fJustification = fNegIJustification;

  return
      newline;
 }


 /****************** End of IP ***************************/


}
