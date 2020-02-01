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

/* To do

1. enable menu items

 */

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
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
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TFormula;
//import java.util.Iterator;

public class TCopiProofPanel extends TProofPanel{

JMenuItem absMenuItem = new JMenuItem();
//JMenuItem addMenuItem = new JMenuItem();
JMenuItem cDMenuItem = new JMenuItem();
//JMenuItem conjMenuItem = new JMenuItem();   we'll use inherited ^I for this
JMenuItem dSMenuItem = new JMenuItem();
JMenuItem hSMenuItem = new JMenuItem();
//JMenuItem mPMenuItem = new JMenuItem();    we'll use inherited implicE for this
JMenuItem mTMenuItem = new JMenuItem();
JMenuItem simpMenuItem = new JMenuItem();

//final static String copiAssJustification="";
final static String cPJustification=" CP";
final static String absJustification=" Abs";
final static String addJustification=" Add";
final static String cDJustification=" CD";
final static String conjJustification=" Conj";
final static String dSJustification=" DS";
final static String hSJustification=" HS";
final static String mPJustification=" MP";
final static String mTJustification=" MT";
final static String simpJustification=" Simp";

final static String commJustification=" Com";

  /* This almost always wants to have its Deriver document because the undoable proof
edits set the fDirty field of the document */

/*public TCopiProofPanel(){

 } */

 public TCopiProofPanel(TDeriverDocument itsDeriverDocument){
  super(itsDeriverDocument);

  fAndIJustification= conjJustification;   //andIMenuItem
  fAndEJustification= simpJustification;   //andEMenuItem  Copi does not use this but subclasses do
  //fAssJustification= copiAssJustification;   //ass
  fOrIJustification= addJustification;   //orIMenuItem

  fImplicIJustification=cPJustification;
  fImplicEJustification=mPJustification;

  alterRulesMenu();
  
  // NO RENDERER, sometimes have commented out (don't know why) 11/29/11

 //fProofListView.setCellRenderer(new TCopiProofListCellRenderer());   // to draw the prooflines correctly

  fProofListView.setColumnCellRenderers(new TCopiProofTableColumnRenderer(TProofTableModel.fProofColIndex),
		  new TCopiProofTableColumnRenderer(TProofTableModel.fJustColIndex));
 
 //col.setCellRenderer(new TProofTableColumnRenderer(TProofTableModel.fProofColIndex));
 //col = getColumnModel().getColumn(TProofTableModel.fJustColIndex);
 //col.setCellRenderer(new TProofTableColumnRenderer(TProofTableModel.fJustColIndex));
 
 
 
 
 }

void assembleAdvancedMenu(){     //override

  fAdvancedRulesMenu.add(rewriteMenuItem);

  fAdvancedRulesMenu.add(theoremMenuItem);
}

void initializeParser(){
  fParser=new TCopiParser();
};



/************************* Menus *********************************/


void alterRulesMenu(){
fRulesMenu.removeAll();

absMenuItem.setText("Abs");
absMenuItem.addActionListener(new TCopiProofPanel_absMenuItem_actionAdapter(this));
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

fRulesMenu.add(implicEMenuItem); // fRulesMenu.add(mPMenuItem);
fRulesMenu.add(mTMenuItem);
fRulesMenu.add(hSMenuItem);
fRulesMenu.add(dSMenuItem);
fRulesMenu.add(cDMenuItem);
fRulesMenu.add(absMenuItem);
fRulesMenu.add(simpMenuItem);
fRulesMenu.add(andIMenuItem);//fRulesMenu.add(conjMenuItem);
fRulesMenu.add(orIMenuItem);//fRulesMenu.add(addMenuItem);
fRulesMenu.add(tIMenuItem);
fRulesMenu.add(implicIMenuItem);

fRulesMenu.add(uGMenuItem);
fRulesMenu.add(uIMenuItem);
fRulesMenu.add(eGMenuItem);
fRulesMenu.add(eIMenuItem);
fRulesMenu.add(rAMenuItem);

rewriteMenuItem.setText("Replacement Rules");


}


void doSetUpRulesMenu(){


  super.doSetUpRulesMenu();    // for AndI etc.





  TFormula selectedFormula=null, secondSelectedFormula=null, thirdSelectedFormula=null;

  TProofline selection = fProofListView.oneSelected();
  TProofline [] twoSelections = fProofListView.exactlyNLinesSelected(2);
  TProofline [] threeSelections = fProofListView.exactlyNLinesSelected(3);

  boolean noneSelected,oneSelected, twoSelected,threeSelected;

  int totalSelected=fProofListView.totalSelected();

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

  simpMenuItem.setEnabled(false);
  absMenuItem.setEnabled(false);
//  conjMenuItem.setEnabled(false);
//  mPMenuItem.setEnabled(false);
  mTMenuItem.setEnabled(false);
  hSMenuItem.setEnabled(false);
  dSMenuItem.setEnabled(false);

  cDMenuItem.setEnabled(false);

  eIMenuItem.setEnabled(false);


  if (oneSelected){
//    conjMenuItem.setEnabled(true);

    if (fParser.isAnd(selectedFormula))
      simpMenuItem.setEnabled(true);

    if (absPossible(selectedFormula))
       absMenuItem.setEnabled(true);

    if (fParser.isExiquant(selectedFormula))
      eIMenuItem.setEnabled(true);
  }

  if (twoSelected){

 //   if (mPPossible(selectedFormula,secondSelectedFormula))
 //      mPMenuItem.setEnabled(true);

     if (mTPossible(selectedFormula,secondSelectedFormula))
       mTMenuItem.setEnabled(true);

     if (hSPossible(selectedFormula,secondSelectedFormula))
       hSMenuItem.setEnabled(true);

     if (dSPossible(selectedFormula,secondSelectedFormula))
       dSMenuItem.setEnabled(true);

     {TFormula dummy= new TFormula();
     if (cDPossible(selectedFormula,secondSelectedFormula,dummy))
       cDMenuItem.setEnabled(true);}
  }

  if (threeSelected){    //some systems use 2 selected CD others use 3 selected CD

   if (cDPossible(selectedFormula,secondSelectedFormula,thirdSelectedFormula))
     cDMenuItem.setEnabled(true);
}


//  mTMenuItem.setEnabled(true);
 // hSMenuItem.setEnabled(true);
 // dSMenuItem.setEnabled(true);
 // cDMenuItem.setEnabled(true);
//  absMenuItem.setEnabled(true);



  if ((oneSelected)
    &&totalSelected==1)
  rewriteMenuItem.setEnabled(true);
else
  rewriteMenuItem.setEnabled(false);

}

void absMenuItem_actionPerformed(ActionEvent e) {
    doAbs();
    }

void cDMenuItem_actionPerformed(ActionEvent e) {
        doCD();
    }




void dSMenuItem_actionPerformed(ActionEvent e) {
        doDS();
    }
    void hSMenuItem_actionPerformed(ActionEvent e) {
        doHS();
    }

/*
void mPMenuItem_actionPerformed(ActionEvent e) {
  doMP();
  }
 */

void mTMenuItem_actionPerformed(ActionEvent e) {
    doMT();
    }
    void simpMenuItem_actionPerformed(ActionEvent e) {
            doSimp();
    }


void orIMenuItem_actionPerformed(ActionEvent e) {
      boolean rightOnly=true;
      dovI(rightOnly);

  }




    /************* Proofline factory ************/

   /* we want this to subclass for other types of proof eg Copi */

   public TProofline supplyProofline(){
      return
          new TCopiProofline(fParser);
   }

/************* End of Proofline factory ************/




/********************** Rules [Action Classes] ************************************/

  public class AssAction extends AbstractAction{
    JTextComponent fText;

     public AssAction(JTextComponent text, String label){
       putValue(NAME, label);

       fText=text;
     }

      public void actionPerformed(ActionEvent ae){
     boolean useFilter =true;
     ArrayList dummy = new ArrayList();

     //String aString= TUtilities.readTextToString(fText, TUtilities.defaultFilter);
     String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);
     
     TFormula root = new TFormula();
     StringReader aReader = new StringReader(aString);
     boolean wellformed;

     wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

     if (!wellformed){
         String message = "The string is illformed."+
                           (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

       //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

         fText.setText(message);
         fText.selectAll();
         fText.requestFocus();

          }
    else {
     TProofline newline = supplyProofline();

     newline.fFormula=root;
     newline.fJustification= fAssJustification;
     newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel+1;
     newline.fLastassumption=true;

     TUndoableProofEdit  newEdit = new TUndoableProofEdit();
     newEdit.fNewLines.add(newline);
     newEdit.doEdit();

     removeInputPane();};
    }

  }

  /********************** Rules ************************************/

  void doImplicI(){
    TProofline subhead,subtail;
    int level;

    if (fTemplate)
      doHintImplicI();
    else{
      subtail=fProofListView.oneSelected();

      if (subtail!=null){
        subhead=fModel.findLastAssumption();

        if ((subhead!=null)&&
            (subtail.fLineno>=subhead.fLineno)){  // their cp insists that the conc comes later

          level=fModel.getHeadLastLine().fSubprooflevel;

          TUndoableProofEdit  newEdit = new TUndoableProofEdit();

          newEdit.fNewLines.add(endSubProof(level));
          newEdit.fNewLines.add(addImplication(subhead.fFormula, subtail.fFormula,level-1, subtail.fLineno));
          newEdit.doEdit();

        }
      }
    }
 }


/*

      function TCopiProofWindow.DoCP: TCommand;

     var
      subhead, subtail: TProofline;
      newline: TCopiProofline;
      aLineCommand: TCopiLineCommand;
      level: integer;
      formulanode: TFormula;

    begin
     DoCP := gNoChanges;
     if fTemplate then {gTemplate}
      DoCP := DoHintCP
     else
      begin

       if (fTextList.TotalSelected = 1) then
        if fTextList.OneSelected(subtail) then
         if FindLastAssumption(subhead) then
          if (subtail.fLineno >= subhead.fLineno) then {their cp insists that the}
   {                                                                       conc comes later}
          begin
          level := TCopiProofline(fHead.Last).fSubprooflevel;

          New(aLineCommand);
          FailNil(aLineCommand);
          aLineCommand.ICopiLineCommand(cAddLine, SELF);

          SupplyCopiProofline(newline); {newline points to new proofline}

          with newline do
          begin
          fSubprooflevel := level - 1; {checkthis}
          fBlankline := TRUE;
          fjustification := '';
          fSelectable := FALSE;
          end;

          aLineCommand.fNewlines.InsertLast(newline);

          newline := nil;

          SupplyFormula(formulanode); {creates implication }
          with formulanode do
          begin
          fKind := binary;
          fInfo := chImplic;
          fLlink := subhead.fFormula.CopyFormula;
          fRlink := subtail.fFormula.CopyFormula;
          end;

          SupplyCopiProofline(newline); {newline points to new proofline}

          with newline do
          begin
          fSubprooflevel := level - 1; {checkthis}
          fFormula := formulanode;
          ffirstjustno := subhead.fLineno;
          fsecondjustno := subtail.fLineno;
          fjustification := ' CP';
          end;  (*This is special, it has to read 3-4 CP, do this in draw method*)

          aLineCommand.fNewlines.InsertLast(newline);

          DoCP := aLineCommand;
          end;
      end;
    end;


  */

  public void doTI(){
     JButton defaultButton;
     JButton dropLastButton;
     TProofInputPanel inputPane;

      if (fModel.getHeadLastLine().fSubprooflevel > kMaxNesting) {
        bugAlert("DoingTI. Warning.", "Phew... no more assumptions, please.");
      }
      else {

        if (fTemplate) {
          bugAlert("DoingAss. Warning.", "Cancel! Ass. is not usual with Tactics on-- you will never be able to drop a new assumption.");
        }
        else {

          JTextField text = new JTextField("New assumption?");
          text.setDragEnabled(true);
          text.selectAll();
{
         defaultButton = new JButton(new AssAction(text,"Go"));

        JButton[]buttons = {new JButton(new CancelAction()),defaultButton };  // put cancel on left
        inputPane = new TProofInputPanel("Doing Assumption",
           text, buttons,fInputPalette);

       }

          addInputPane(inputPane);

        //  inputPane.getRootPane().setDefaultButton(defaultButton);
          fInputPane.setVisible(true); // need this
          text.requestFocus();         // so selected text shows
        }
      }
    }


/*

      function TCopiProofWindow.DoAss: TCommand;

      var
       newline: TCopiProofline;
       aLineCommand: TCopiLineCommand;
       prompt, rad1, rad2: str255;
       root: TFormula;
       cancel: boolean;

     begin
      DoAss := gNoChanges;
      cancel := false;

      if TCopiProofline(fHead.Last).fSubprooflevel > kCopiMaxnesting then
       begin
        GetIndString(prompt, kStringRSRCID, 1);
        BugAlert(prompt); {'phew... no more please.')}
       end
      else
       begin
        if fTemplate then {gTemplate}
         begin
          prompt := 'Cancel! Ass is not usual with Tactics-- you will never be able to drop a new assumption.';

          cancel := not GetTheChoice(strNull, strNull, prompt);
         end;


        if not cancel then
         begin
          GetIndString(prompt, kStringRSRCID, 2); { prompt := 'New Antecedent?';}
          GetIndString(rad1, kStringRSRCID, 3); { 'Keep Last'}
          GetIndString(rad2, kStringRSRCID, 4); { Drop Last'}

          rad1 := strNull;
          rad2 := strNull;
          prompt := 'New Assumption?';

          GetTheRoot(rad1, rad2, prompt, root, cancel);
          if not cancel then
           begin
           New(aLineCommand);
           FailNil(aLineCommand);
           aLineCommand.ICopiLineCommand(cAddLine, SELF);

           SupplyCopiProofline(newline); {newline points to new proofline}

           with newline do
           begin
           fSubprooflevel := TCopiProofline(fHead.Last).fSubprooflevel + 1;
           fFormula := root;
           fjustification := strNull;
           fLastassumption := TRUE;
           end;

           aLineCommand.fNewlines.InsertLast(newline);

           DoAss := aLineCommand;

           end;

         end;
       end;

     end;


 */


boolean absPossible(TFormula selected){


  if (fParser.isImplic(selected))
    return
        true;
  else
    return
        false;
}



void doAbs(){
  TProofline newline, firstline;
  TProofline[] selections = fProofListView.exactlyNLinesSelected(1);

  if (selections != null) {

    firstline = selections[0];

      if (fParser.isImplic(firstline.fFormula)) { //p->q

        TFormula p=firstline.fFormula.fLLink.copyFormula();
        TFormula q=firstline.fFormula.fRLink.copyFormula();


        TFormula pimplicpandq=new TFormula(TFormula.binary,
                                  String.valueOf(chImplic),
                                  p,
                                  new TFormula(TFormula.binary,
                                     String.valueOf(chAnd),
                                     p.copyFormula(),
                                     q));

        newline = supplyProofline();
        int level = fModel.getHeadLastLine().fSubprooflevel;

        newline.fFormula = pimplicpandq; //q
        newline.fFirstjustno = firstline.fLineno;
        newline.fJustification = absJustification;
        newline.fSubprooflevel = level;

        TUndoableProofEdit newEdit = new TUndoableProofEdit();
        newEdit.fNewLines.add(newline);
        newEdit.doEdit();

    }
  }
}

boolean cDPossible(TFormula selected, TFormula secondSelected, TFormula third){
// the third selected is a dummy for subclasses to override

  if ( (fParser.isAnd(selected) &&
       fParser.isImplic(selected.fLLink) &&
       fParser.isImplic(selected.fRLink) &&
       fParser.isOr(secondSelected) &&
       selected.equalFormulas(selected.fLLink.fLLink,secondSelected.fLLink)&&
       selected.equalFormulas(selected.fRLink.fLLink,secondSelected.fRLink))  ////p
        ||
        (fParser.isAnd(secondSelected) &&
        fParser.isImplic(secondSelected.fLLink) &&
        fParser.isImplic(secondSelected.fRLink) &&
        fParser.isOr(selected) &&
        selected.equalFormulas(secondSelected.fLLink.fLLink,selected.fLLink)&&
        selected.equalFormulas(secondSelected.fRLink.fLLink,selected.fRLink)))

    return
        true;
  else
    return
        false;
}



void doCD(){
  TProofline newline, firstline, secondline;
  TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

  if (selections != null) {

    firstline = selections[0];
    secondline = selections[1];

    if ((fParser.isOr(firstline.fFormula)) &&
        (fParser.isAnd(secondline.fFormula))) {

        newline = firstline;
        firstline = secondline;
        secondline = newline; //{to make line 1 contain the and}
        newline = null;
      }

      if ((fParser.isAnd(firstline.fFormula)) &&       //(p->q).(r->s)
          (fParser.isImplic(firstline.fFormula.fLLink)) && //(p->q)
          (fParser.isImplic(firstline.fFormula.fRLink)) && //(r->s)
          (fParser.isOr(secondline.fFormula))&&            //pvr
          firstline.fFormula.equalFormulas(secondline.fFormula.fLLink, //p
                                           firstline.fFormula.fLLink.fLLink)&& //p
          firstline.fFormula.equalFormulas(secondline.fFormula.fRLink, //r
                                           firstline.fFormula.fRLink.fLLink)
) {


        TFormula qVs=new TFormula(TFormula.binary,
                                  String.valueOf(chOr),
                                  firstline.fFormula.fLLink.fRLink.copyFormula(),
                                  firstline.fFormula.fRLink.fRLink.copyFormula());

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

/*

  function TCopiProofWindow.DoCD: TCommand;

   var
    level: integer;
    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}

    aLineCommand: TCopiLineCommand;
    formulanode, q, s: TFormula;

  begin

   DoCD := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then



     begin
      if secondline.fFormula.fInfo = chAnd then
       begin
        templine := firstline;
        firstline := secondline;
        secondline := templine;
        templine := nil;
       end; {make line1 contain the and}


      if firstline.fFormula.fInfo = chAnd then
       if firstline.fFormula.fLlink.fInfo = chImplic then
        if firstline.fFormula.fRlink.fInfo = chImplic then
        if secondline.fFormula.fInfo = chOr then

        begin
        if Equalformulas(firstline.fFormula.fLlink.fLlink, secondline.fFormula.fLlink) then
        if Equalformulas(firstline.fFormula.fRlink.fLlink, secondline.fFormula.fRlink) then
        begin
        q := firstline.fFormula.fLlink.fRlink;
        s := firstline.fFormula.fRlink.fRlink;

        SupplyFormula(formulanode);
        formulanode.fKind := binary; {formulanode is the new formula}
 {                                                                      node}
        formulanode.fInfo := chOr;
        formulanode.fLlink := q.CopyFormula;
        formulanode.fRlink := s.CopyFormula;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new}
 {                                                                      proofline}
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' C.D.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;

        DoCD := aLineCommand;

        end;

        end;
     end;

  end;

*/


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

/*

  function TCopiProofWindow.DoDS: TCommand;

   var

    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    formula1, formulanode: TFormula;
    rad1, rad2, prompt: str255;

  begin

   DoDS := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin

      if (secondline.fFormula.fInfo = chOr) then
       if (firstline.fFormula.fInfo = chNeg) then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 1 contain the or}
        templine := nil;
        end;

      if (firstline.fFormula.fInfo = chOr) then
       if (secondline.fFormula.fInfo = chNeg) then
        if Equalformulas(firstline.fFormula.fLlink, secondline.fFormula.fRlink) then
        begin
        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new proofline}
        with newline do
        begin
        fFormula := firstline.fFormula.fRlink.CopyFormula;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' D.S.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoDS := aLineCommand;

        end;
     end;

  end;


*/


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

/*
  function TCopiProofWindow.DoHS: TCommand;

   var

    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    p, r, formulanode: TFormula;

  begin

   DoHS := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin
      if (firstline.fFormula.fInfo = chImplic) then
       if (secondline.fFormula.fInfo = chImplic) then
        if Equalformulas(secondline.fFormula.fRlink, firstline.fFormula.fLlink) then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 1 contain the arrow}
        templine := nil;
        end;

      if (firstline.fFormula.fInfo = chImplic) then
       if (secondline.fFormula.fInfo = chImplic) then
        if Equalformulas(secondline.fFormula.fLlink, firstline.fFormula.fRlink) then
        begin
        p := firstline.fFormula.fLlink;
        r := secondline.fFormula.fRlink;

        SupplyFormula(formulanode);
        formulanode.fKind := binary;  {formulanode is the new formula}
 {                                                                 node}
        formulanode.fInfo := chImplic;
        formulanode.fLlink := p.CopyFormula;
        formulanode.fRlink := r.CopyFormula;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new proofline}
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' H.S.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoHS := aLineCommand;

        end;
     end;

  end;

*/


/*
boolean mPPossible(TFormula selected, TFormula secondSelected){


  if ( (fParser.isImplic(selected)) &&
        (selected.equalFormulas(selected.fLLink,
                                          secondSelected))
        ||
        (fParser.isImplic(secondSelected)) &&
       (selected.equalFormulas(secondSelected.fLLink,
                                           selected)))
    return
        true;
  else
    return
        false;
}

 */


/* using inherited implicE

void doMP(){

  TProofline newline, firstline, secondline;
  TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

    if (selections != null){

      firstline = selections[0];
      secondline = selections[1];

      if ( (fParser.isImplic(secondline.fFormula)) &&
          (firstline.fFormula.equalFormulas(secondline.fFormula.fLLink,
                                            firstline.fFormula))) {
        newline = firstline;
        firstline = secondline;
        secondline = newline; //{to make line 1 contain the arrow}
        newline = null;
      }

      if ( (fParser.isImplic(firstline.fFormula)) &&
          (firstline.fFormula.equalFormulas(firstline.fFormula.fLLink,
                                            secondline.fFormula))) {

         newline = supplyProofline();
         int level=fModel.getHeadLastLine().fSubprooflevel;


         newline.fFormula = (firstline.fFormula.fRLink).copyFormula();
         newline.fFirstjustno = firstline.fLineno;
         newline.fSecondjustno = secondline.fLineno;
         newline.fJustification = mPJustification;
         newline.fSubprooflevel = level;

         TUndoableProofEdit newEdit = new TUndoableProofEdit();
         newEdit.fNewLines.add(newline);
         newEdit.doEdit();


      }
    }


 }


*/

/*

  function TCopiProofWindow.DoMP: TCommand;

  var

   newline: TCopiProofline;
   templine, firstline, secondline: TProofline;  {check}
   aLineCommand: TCopiLineCommand;
   formula1, formulanode: TFormula;
   rad1, rad2, prompt: str255;

 begin

  DoMP := gNoChanges;

  if (fTextList.TotalSelected = 2) then
   if fTextList.TwoSelected(firstline, secondline) then
    begin

     if (secondline.fFormula.fInfo = chImplic) then
      if Equalformulas(secondline.fFormula.fLlink, firstline.fFormula) then
       begin
       templine := firstline;
       firstline := secondline;
       secondline := templine; {to make line 2 contain the arrow}
       templine := nil;
       end;
     if (firstline.fFormula.fInfo = chImplic) then
      if Equalformulas(firstline.fFormula.fLlink, secondline.fFormula) then
       begin
       New(aLineCommand);
       FailNil(aLineCommand);
       aLineCommand.ICopiLineCommand(cAddLine, SELF);

       SupplyCopiProofline(newline); {newline points to new proofline}
       with newline do
       begin
       fFormula := firstline.fFormula.fRlink.CopyFormula;
       ffirstjustno := firstline.fLineno;
       fsecondjustno := secondline.fLineno;
       fjustification := ' M.P.';
       fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
       end;

       aLineCommand.fNewlines.InsertLast(newline);
       newline := nil;
       DoMP := aLineCommand;

       end;
    end;

 end;


 */

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

         TFormula notP=new TFormula(TFormula.unary,String.valueOf(chNeg),null,firstline.fFormula.fLLink.copyFormula());


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


/*

   function TCopiProofWindow.DoMT: TCommand;

   var

    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    formula1, formulanode, negnode: TFormula;

  begin

   DoMT := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin

      if (secondline.fFormula.fInfo = chImplic) then
       if (firstline.fFormula.fInfo = chNeg) then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 2 contain the arrow}
        templine := nil;
        end;

      if (firstline.fFormula.fInfo = chImplic) then
       if (secondline.fFormula.fInfo = chNeg) then
        if Equalformulas(firstline.fFormula.fRlink, secondline.fFormula.fRlink) then
        begin

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyFormula(negnode);
        negnode.fKind := unary;
        negnode.fInfo := chNeg;
        negnode.fRlink := firstline.fFormula.fLlink.CopyFormula;

        SupplyCopiProofline(newline); {newline points to new proofline}
        with newline do
        begin
        fFormula := negnode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' M.T.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoMT := aLineCommand;

        end;
     end;

  end;


 */

void doSimp(){
    TProofline newline, firstline;
    TProofline[] selections = fProofListView.exactlyNLinesSelected(1);

    if (selections != null) {

      firstline = selections[0];

      if (fParser.isAnd(firstline.fFormula)) { //p.q

        TFormula p = firstline.fFormula.fLLink.copyFormula();

        newline = supplyProofline();
        int level = fModel.getHeadLastLine().fSubprooflevel;

        newline.fFormula = p;
        newline.fFirstjustno = firstline.fLineno;
        newline.fJustification = simpJustification;
        newline.fSubprooflevel = level;

        TUndoableProofEdit newEdit = new TUndoableProofEdit();
        newEdit.fNewLines.add(newline);
        newEdit.doEdit();

      }
    }
}

/*
  function TCopiProofWindow.DoSimp: TCommand;

   var

    newline: TCopiProofline;
    firstline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;

  begin

   DoSimp := gNoChanges;

   if (fTextList.TotalSelected = 1) then
    if fTextList.OneSelected(firstline) then
     begin
      if (firstline.fFormula.fKind = binary) then
       if firstline.fFormula.fInfo = chAnd then
        begin

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := firstline.fFormula.fLlink.CopyFormula;
        ffirstjustno := firstline.fLineno;
        fjustification := ' Simp.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoSimp := aLineCommand;

        end;
     end;
  end;

*/






public class UGAction extends AbstractAction{
      JTextComponent fText;
      TProofline fFirstline=null;
      int fStage=1;

      TFormula muForm,nuForm,scope;


       public UGAction(JTextComponent text, String label, TProofline firstline){
         putValue(NAME, label);

         fText=text;
         fFirstline=firstline;
       }

        public void actionPerformed(ActionEvent ae){


          switch (fStage){

            case 1:
              findMu();
              break;

            case 2:
              findNu();
              break;

            default: ;
 }
 }




   void findMu(){
          String message;

   //       String aString = TUtilities.readTextToString(fText, TUtilities.defaultFilter);
String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);
       
       //TUtilities was the older one
         
         if ((aString==null)||
              (aString.length()!=1)||
              !fParser.isVariable(aString.charAt(0))){

            message = aString + " is not a variable.";

            fText.setText(message);
            fText.selectAll();
            fText.requestFocus();
          }
          else {

            muForm= new TFormula();

            muForm.fKind = TFormula.variable;
            muForm.fInfo = aString;

            // test for free in EI line

            TProofline freeLine= fModel.lineWithVariableFree(muForm, fFirstline,fEIJustification,-1);

            if (freeLine!=null){
              message = aString + " is free in " + freeLine.getLineNo() + " obtained by EI.";

              fText.setText(message);
              fText.selectAll();
              fText.requestFocus();
            }
            else{


            // test for free in CP assumption (in starting assumptions ok)

            freeLine= fModel.lineWithVariableFree(muForm, fFirstline,fAssJustification,fFirstline.getHeadlevel());


            if (freeLine!=null){
              message = aString + " is free in " + freeLine.getLineNo() + " CP assumption.";

               fText.setText(message);
               fText.selectAll();
               fText.requestFocus();
            }


            // by here we have got the mu ok


            else{
              ((TProofInputPanel)fInputPane).setLabel1("Doing UG-- Stage2, identifying variable to quantify with");
              message = "Variable to quantify with?";
              fText.setText(message);
              fText.selectAll();
              fText.requestFocus();

              fStage=2;
            }

            }
          }

      }


      void findNu(){
              String message;

            //  String aString = TUtilities.readTextToString(fText, TUtilities.defaultFilter);
String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);


              if ((aString==null)||
                  (aString.length()!=1)||
                  !fParser.isVariable(aString.charAt(0))){

                message = aString + " is not a variable.";

                fText.setText(message);
                fText.selectAll();
                fText.requestFocus();
              }
              else {

                nuForm= new TFormula();

                nuForm.fKind = TFormula.variable;
                nuForm.fInfo = aString;

                // test for subtitution instance
                // the condition here is that if we substitute nu for mu throughout, then mu for nu throughout
                // we get back what we started with so, Fxy to AllyFyy is no good but Fyy to AllyFyy is


                 TFormula trialScope= (fFirstline.fFormula).copyFormula();
                 trialScope.subTermVar(trialScope,nuForm,muForm);
                 trialScope.subTermVar(trialScope,muForm,nuForm);


                if(!fFirstline.fFormula.equalFormulas(fFirstline.fFormula,trialScope))  //check right way round change Dec 06 from free For
                   {
                    message = nuForm.getInfo() + " already occurs in "
                        + fParser.writeFormulaToString(fFirstline.fFormula) +".";

                  fText.setText(message);
                  fText.selectAll();
                  fText.requestFocus();
                }
                else{
                  scope = (fFirstline.fFormula).copyFormula();
                  scope.subTermVar(scope,nuForm,muForm);


                  TFormula formulanode = new TFormula();

                  formulanode.fKind = TFormula.quantifier;
                  formulanode.fInfo = String.valueOf(chUniquant);
                  formulanode.fLLink = nuForm;
                  formulanode.fRLink = scope;

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




    }


/*

     function TCopiProofWindow.DoUG: TCommand;
      OVERRIDE;

      var

       firstline: TProofline;
       newline: TCopiProofline;
       faultyline: TObject;
       aLineCommand: TCopiLineCommand;
       formulanode, scope, muForm, freeVarForm, nuForm: TFormula;
       outPutStr, prompt: str255;
       cancel, found: boolean;
       mu, freeVar, nu: char;


      function InCP (item: TObject): boolean;  (*occurs free in assumption of CP or other*)

       var
        found: boolean;

      begin
       found := FALSE;
       if not TCopiProofline(item).fBlankline then
        if TCopiProofline(item).fSelectable then
         if TCopiProofline(item).fJustification = strNull then
          found := TCopiProofline(item).fFormula.Freetest(muForm);

       InCP := found;
      end;

    {this needs a bit of abstraction}
      function EIFreeVar (firstline: TProofline; var freevar: char): boolean;

       procedure Test (item: TObject);
        var
         aProofline: TCopiProofline;
       begin
        if not found then
         begin
          aProofline := TCopiProofline(item);
          if aProofline.fLineno <= firstline.fLineno then
           if not aProofline.fBlankline then
           if length(aProofline.fjustification) > 4 then
           if (aProofline.fjustification[2] = 'E') and (aProofline.fjustification[3] = 'I') then
           begin
           freevar := aProofline.fjustification[5];

           freeVarForm.fInfo := freevar;

           found := firstline.fFormula.Freetest(freevarForm);
           end;
         end;
       end;

      begin
       found := FALSE;
       SupplyFormula(freeVarForm);
       freeVarForm.fKind := variable;
       fHead.Each(Test);
       freeVarForm.DismantleFormula;
       EIFreeVar := found;
      end;

     begin

      DoUG := gNoChanges;
      cancel := FALSE;
      found := FALSE;
      outPutStr := strNull;

      if fTemplate then {gTemplate}
       DoUG := DoHintUG
      else
       begin

        if (fTextList.TotalSelected = 1) then
         if fTextList.OneSelected(firstline) then
          if EIFreeVar(firstline, freeVar) then
           begin
           BugAlert(concat(freeVar, ' was introduced by EI.'));
           end
          else
           begin

                        {GetIndString(prompt, kStringRSRCID, 17);  Variable of quantification }

           repeat
                            {GetIndString(prompt, kStringRSRCID, 19); Term}
           prompt := 'Variable to generalize on?';

           gInputStr := concat(outPutStr, prompt);

           if not GetTheChoice(strNull, strNull, gInputStr) then
           cancel := TRUE
           else if length(gInputStr) > 0 then
           begin
           GetStringInput;
           skip(1, logicfilter);  (*primes gCurrch, and gLookaheadCh*)
                                 { ReadTerm; }
           if gCurrCh in gCopiVariables then
           begin
           found := TRUE;
           mu := gCurrCh;
                                       {beta := gCurrStrCh;}
           end
           else
           outPutStr := concat(gCurrCh, ' is not a variable.');
           end;

           until found or cancel;



           found := FALSE;
           outPutStr := strNull;

           if not cancel then
           begin
           supplyFormula(muForm);
           muForm.fKind := variable;
           muForm.fInfo := mu;
           if not firstline.fFormula.Freetest(muForm) then
           begin
           fParser.WriteFormulaToString(TCopiProofline(firstline).fFormula, outPutStr);
           BugAlert(concat(mu, ' must occur freely in ', outputStr, ' .'));
           muForm.DismantleFormula;
           cancel := TRUE;
           end;
           end;

           if not cancel then
           begin
           faultyline := fHead.FirstThat(InCP);
           if faultyline <> nil then
           begin
           fParser.WriteFormulaToString(TCopiProofline(faultyline).fFormula, outPutStr);
           BugAlert(concat(mu, ' occurs freely in assumption ', outPutStr, ' .'));
           muForm.DismantleFormula;
           cancel := TRUE;
           end
           end;


           if not cancel then
           repeat
                                  {GetIndString(prompt, kStringRSRCID, 19); Term}

           prompt := 'Variable to generalize with?';

           gInputStr := concat(outPutStr, prompt);

           if not GetTheChoice(strNull, strNull, gInputStr) then
           cancel := TRUE
           else if length(gInputStr) > 0 then
           begin
           GetStringInput;
           skip(1, logicfilter);  (*primes gCurrch, and gLookaheadCh*)
                                       {ReadTerm; }
           if gCurrCh in gCopiVariables then
           begin
           found := TRUE;
           nu := gCurrCh;
           end
           else
           outPutStr := concat(gCurrCh, ' is not a variable.');
           end;

           until found or cancel;

           if not cancel then
           begin
           SupplyFormula(nuForm);
           nuForm.fKind := variable;
           nuForm.fInfo := nu;
           if not firstline.fFormula.FreeForTest(nuForm, muForm) then {check this is the right way round}
           begin

           fParser.WriteFormulaToString(firstline.fFormula, outputStr);

           outputStr := concat(' in ', outputStr, '.');

           BugAlert(concat(StrofChar(nu), ' for ', StrofChar(mu), outputStr, ' leads to capture.'));


           muForm.DismantleFormula;
           nuForm.DismantleFormula;

           end
           else
           begin

           scope := firstline.fFormula.CopyFormula;
           NewSubTermVar(scope, nuForm, muForm);  {ok through here}

           SupplyFormula(formulanode);
           with formulanode do
           begin
           fKind := quantifier;
           fInfo := chUniquant;
           fLlink := nuForm;
           end;

           formulanode.fRlink := scope;  {!!}


           muForm.DismantleFormula;

           New(aLineCommand);
           FailNil(aLineCommand);
           aLineCommand.ICopiLineCommand(cAddLine, SELF);

           SupplyCopiProofline(newline);
           with newline do
           begin
           fFormula := formulanode;
           ffirstjustno := firstline.fLineno;
           fjustification := ' UG';
           fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
           end;

           formulanode := nil;

           aLineCommand.fNewlines.InsertLast(newline);
           newline := nil;
           DoUG := aLineCommand;

           end;
           end;
           end;
       end;
     end;



 */



 void doUG(){
   TProofline firstline;
   JButton defaultButton;
   TProofInputPanel inputPane;


   if (fTemplate)
     doHintUG();
   else{

     firstline = fProofListView.oneSelected();

     if (firstline != null) {


       JTextField text = new JTextField("Variable to generalize on");
       text.selectAll();

       defaultButton = new JButton(new UGAction(text,"Go", firstline));

       JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
       inputPane = new TProofInputPanel("Doing UG Stage 1", text, buttons);


       addInputPane(inputPane);

       inputPane.getRootPane().setDefaultButton(defaultButton);
       fInputPane.setVisible(true); // need this
       text.requestFocus();         // so selected text shows

     }



   }

 }



 void doEI(){

   TProofline newline, firstline;
   TProofline[] selections = fProofListView.exactlyNLinesSelected(1);

   if (selections != null) {

     firstline = selections[0];

     if (fParser.isExiquant(firstline.fFormula)) {

       TFormula scope=firstline.fFormula.scope();
       TFormula variable=firstline.fFormula.quantVarForm();

       TFormula instant= firstInstantiation(scope,variable);

       if (instant==null){
         bugAlert("DoingEI. Warning.","No more instantiating variables available.");

       }
       else{

      TFormula p=scope.copyFormula();

      p.subTermVar(p,instant,variable);

      newline = supplyProofline();
      int level = fModel.getHeadLastLine().fSubprooflevel;

      newline.fFormula = p;
      newline.fFirstjustno = firstline.fLineno;
      newline.fJustification = fEIJustification;
      newline.fSubprooflevel = level;

      TUndoableProofEdit newEdit = new TUndoableProofEdit();
      newEdit.fNewLines.add(newline);
      newEdit.doEdit();
     }

  }
}
 }

/*

   function TCopiProofWindow.DoEI: TCommand;
   OVERRIDE;

   var
    firstline: TProofline;
    newline: TCopiProofline;
    aLineCommand: TCopiLineCommand;
    term: char;
    formulanode, termForm: TFormula;

  begin
   DoEI := gNoChanges;

   if (fTextList.TotalSelected = 1) then
    if fTextList.OneSelected(firstline) then
     if firstline.fFormula.fInfo[1] = chExiquant then
      if firstline.fFormula.Scope.Freetest(firstline.fFormula.QuantVarForm) then

       begin
        if FirstInstantiation(term, firstline.fFormula.QuantVarForm, firstline.fFormula.Scope) then
        begin
        supplyformula(termForm);
        termForm.fInfo := term;
        termForm.fKind := variable;


        formulanode := firstline.fFormula.fRlink.CopyFormula;

        NewSubTermVar(formulanode, termForm, firstline.fFormula.QuantVarForm);

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fjustification := concat(' EI ', term);
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoEI := aLineCommand;

        end
        else
        begin
                               {GetIndString(prompt, kStringRSRCID, 1);}
        BugAlert('No more instantiating variables available.');
        end;
       end;

  end;

  function TCopiProofWindow.FirstInstantiation (var instantiation: char; variForm, scope: TFormula): boolean;

  var
   found: boolean;
   index: integer;
   instForm: TFormula;

  function NotFreeInProof: boolean;

   function There (item: TObject): boolean;

    var
     foundthere: boolean;

   begin
    foundthere := FALSE;
    if not TCopiProofline(item).fBlankline then
     foundthere := TCopiProofline(item).fFormula.FreeTest(instForm);

    There := foundthere;
   end;

  begin
   NotFreeInProof := (fHead.FirstThat(There) = nil);
  end;

 begin
  found := FALSE;
  instantiation := 'u';  (*Copi variables are u to z*)

  supplyFormula(instForm);
  instForm.fKind := variable;

  while not found and (instantiation <= 'z') do
   begin
    instForm.fInfo := instantiation;

    found := NotFreeInProof;

    if found then
     begin
      found := scope.FreeForTest(instForm, variForm);  (*must not get captured in scope*)
     end;

    if not found then
     instantiation := CHR(ORD(instantiation) + 1);
   end;

  FirstInstantiation := found;
 end;




 */


TFormula firstInstantiation(TFormula scope,TFormula variable){
  String variables=TCopiParser.gCopiVariables;
  TFormula instant = new TFormula(TFormula.variable,"",null,null);
  TProofline dummy;

  for (int i=0;i<variables.length();i++){

    instant.fInfo=variables.substring(i,i+1);

    dummy = fModel.variableFreeInProof(instant);

    if (dummy==null){  //not free in Proof
      if (scope.freeForTest(instant,variable))
        return
            instant;
    }
  }

 return
     null;
}



/******************* Utilities ************************************************/






TProofline endSubProof (int lastlevel){
   TProofline newline = supplyProofline();

  newline.fBlankline=true;
  newline.fJustification= "";
  newline.fSubprooflevel= lastlevel-1;
  newline.fSelectable=false;

return
      newline;
}







}

/******************* End of TCopi Proof Panel *********************************/

class TCopiProofPanel_absMenuItem_actionAdapter implements java.awt.event.ActionListener {
      TCopiProofPanel adaptee;

      TCopiProofPanel_absMenuItem_actionAdapter(TCopiProofPanel adaptee) {
        this.adaptee = adaptee;
      }
      public void actionPerformed(ActionEvent e) {
        adaptee.absMenuItem_actionPerformed(e);
      }
}





class TCopiProofPanel_cDMenuItem_actionAdapter implements java.awt.event.ActionListener {
          TCopiProofPanel adaptee;

          TCopiProofPanel_cDMenuItem_actionAdapter(TCopiProofPanel adaptee) {
            this.adaptee = adaptee;
          }
          public void actionPerformed(ActionEvent e) {
            adaptee.cDMenuItem_actionPerformed(e);
          }
    }



    class TCopiProofPanel_dSMenuItem_actionAdapter implements java.awt.event.ActionListener {
          TCopiProofPanel adaptee;

          TCopiProofPanel_dSMenuItem_actionAdapter(TCopiProofPanel adaptee) {
            this.adaptee = adaptee;
          }
          public void actionPerformed(ActionEvent e) {
            adaptee.dSMenuItem_actionPerformed(e);
          }
    }




    class TCopiProofPanel_hSMenuItem_actionAdapter implements java.awt.event.ActionListener {
          TCopiProofPanel adaptee;

          TCopiProofPanel_hSMenuItem_actionAdapter(TCopiProofPanel adaptee) {
            this.adaptee = adaptee;
          }
          public void actionPerformed(ActionEvent e) {
            adaptee.hSMenuItem_actionPerformed(e);
          }
    }

/*

class TCopiProofPanel_mPMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TCopiProofPanel adaptee;

  TCopiProofPanel_mPMenuItem_actionAdapter(TCopiProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.mPMenuItem_actionPerformed(e);
  }
}

 */

class TCopiProofPanel_mTMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TCopiProofPanel adaptee;

  TCopiProofPanel_mTMenuItem_actionAdapter(TCopiProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.mTMenuItem_actionPerformed(e);
  }
}

class TCopiProofPanel_simpMenuItem_actionAdapter implements java.awt.event.ActionListener {
          TCopiProofPanel adaptee;

          TCopiProofPanel_simpMenuItem_actionAdapter(TCopiProofPanel adaptee) {
            this.adaptee = adaptee;
          }
          public void actionPerformed(ActionEvent e) {
            adaptee.simpMenuItem_actionPerformed(e);
          }
    }



/*



 {$IFC THINK_PASCAL}

 unit TCopiProofWindow;

 interface

  uses
   SysEqu, Traps, ULoMem, UMacAppUtilities, UPatch, UObject, UViewCoords, UMemory, UFailure, UMenuSetup, UList, PrintTraps, UAssociation, UMacApp, UTEView,
 { � MacApp }

 { � Building Blocks }
   UPrinting, UDialog, UGridView,
 { � Implementation Use }
   Picker, DerImpNotes, UStream, ULogicGlobals90, UFormulaIntf, UProofViewIntf, USemanticTestIntf, UReAssemble90, UCopiIntf;
 (*PickerIntf,*)

 implementation

 {$ENDC}

 {$S  Copi2}
  function TCopiProofWindow.PrepareCutLine: TCommand;
   OVERRIDE;

   var
    aCopiCutLineCommand: TCopiCutLineCommand;
    firstline, linebefore: TProofline;

  begin
   if fTextList.OneSelected(firstline) then
    begin
     New(aCopiCutLineCommand);
     FailNil(aCopiCutLineCommand);
     aCopiCutLineCommand.ICopiCutLineCommand(cCutProofLine, SELF);

     aCopiCutLineCommand.fGarbagelines.InsertLast(firstline);

                                                 (*there always is a linebefore as firstline cannot be first*)

     aCopiCutLineCommand.fBadBottom := fHead.GetSameItemNo(firstline);

     linebefore := TProofline(fHead.At(aCopiCutLineCommand.fBadBottom - 1));

     if linebefore.fFormula.fInfo = '?' then
      begin
       aCopiCutLineCommand.fGarbagelines.InsertFirst(linebefore);
       aCopiCutLineCommand.fBadBottom := aCopiCutLineCommand.fBadBottom - 1;
      end;
                                                         (*remove subgoal*)

     if linebefore.fBlankline then
      begin
       aCopiCutLineCommand.fGarbagelines.InsertFirst(linebefore);
       aCopiCutLineCommand.fBadBottom := aCopiCutLineCommand.fBadBottom - 1;
      end;
                                                         (*Open subproof*)


     PrepareCutLine := aCopiCutLineCommand;
    end
   else
    PrepareCutLine := gNoChanges;

  end;


 {$S  Copi2}
  function TCopiProofWindow.PreparePrune: TCommand;
   OVERRIDE;

   var
    aCopiCutLineCommand: TCopiCutLineCommand;
    firstline, linebefore: TProofline;
    tailIndex, headIndex: integer;

  begin
   New(aCopiCutLineCommand);
   FailNil(aCopiCutLineCommand);
   aCopiCutLineCommand.ICopiCutLineCommand(cPrune, SELF);

   headIndex := fHead.fSize;
 {tailIndex:=fTail.fSize-1; never cut very last line}

 {while tailIndex > 0 do}
 {begin}
 {firstline := TProofline(fTail.At(tailIndex));}
 {if not firstline.fBlankline then}
 {if not (firstline.fFormula.fInfo = '?') then}
 {if LineCutable(firstline, aCopiCutLineCommand.fGarbagelines) then}
 {begin}
 {aCopiCutLineCommand.fGarbagelines.InsertFirst(firstline);}

 {there always is a linebefore as firstline cannot be first  check not converted properly}


 {aCopiCutLineCommand.fBadBottom := headIndex + fTail.GetSameItemNo(firstline);}
 {linebefore := TProofline(fTail.At((aCopiCutLineCommand.fBadBottom - HeadIndex) - 1));}

 {if linebefore.fFormula.fInfo = '?' then}
 {begin}
 {aCopiCutLineCommand.fGarbagelines.InsertFirst(linebefore);}
 {aCopiCutLineCommand.fBadBottom := aCopiCutLineCommand.fBadBottom - 1;}
 {end;}
 {remove subgoal}

 {if linebefore.fBlankline then}
 {begin}
 {aCopiCutLineCommand . fGarbagelines . InsertFirst ( linebefore );}
 {aCopiCutLineCommand . fBadBottom := aCopiCutLineCommand . fBadBottom - 1;}
 {end;}
 {Open subproof }
 {end;}

 {    tailIndex := tailIndex - 1;}

 {end;}
 {check, }
 {dont prune tail }

   if fTail.fSize = 0 then
    headIndex := headIndex - 1; (*don't cut last line*)

   while headIndex > 1 do    (*dont cut firstline*)
    begin
     firstline := TProofline(fHead.At(headIndex));
     if not firstline.fBlankline then
      if not (firstline.fFormula.fInfo = '?') then
       if LineCutable(firstline, aCopiCutLineCommand.fGarbagelines) then
        begin
        aCopiCutLineCommand.fGarbagelines.InsertFirst(firstline);

                                                 {there always is a linebefore as firstline cannot be first}

        aCopiCutLineCommand.fBadBottom := fHead.GetSameItemNo(firstline);

        linebefore := TProofline(fHead.At(aCopiCutLineCommand.fBadBottom - 1));

        if linebefore.fFormula.fInfo = '?' then
        begin
        aCopiCutLineCommand.fGarbagelines.InsertFirst(linebefore);
        aCopiCutLineCommand.fBadBottom := aCopiCutLineCommand.fBadBottom - 1;
        end;
                                         {remove subgoal}

        if linebefore.fBlankline then
        begin
        aCopiCutLineCommand.fGarbagelines.InsertFirst(linebefore);
        aCopiCutLineCommand.fBadBottom := aCopiCutLineCommand.fBadBottom - 1;
        end;
                                         {Open subproof}



        end;

     headIndex := headIndex - 1;
    end;

   if aCopiCutLineCommand.fGarbagelines.fSize > 0 then
    PreparePrune := aCopiCutLineCommand
   else
    begin
     aCopiCutLineCommand.fGarbageLines.Free;
     aCopiCutLineCommand.Free;
     PreparePrune := gNoChanges;
    end;

  end;

 {$S  Copi2}

  function TCopiProofWindow.DoMenuCommand (aCmdNumber: CmdNumber): TCommand;
   OVERRIDE;



  begin
   DoMenuCommand := gNoChanges;

   case aCmdNumber of

    cCutProofLine:


     DoMenuCommand := PrepareCutLine;

    cPrune:


     DoMenuCommand := PreparePrune;






    otherwise
     DoMenuCommand := inherited DoMenuCommand(aCmdNumber);
   end;

  end;


 {$S  Copi2}
  procedure TCopiProofWindow.DoSetupMenus;
   OVERRIDE;
   var
    firstline: TProofline;
    cutable: boolean;

  begin

   inherited DoSetupMenus;

   if fTextList.OneSelected(firstline) then
    cutable := LineCutable(firstline, nil)
   else
    cutable := FALSE;

   Enable(cCutProofLine, cutable);
   Enable(cPrune, TRUE);

  end;

 {$S  Copi2}

  function TCopiProofWindow.LineCutable (thisline: TProofline; alreadyCut: TList): BOOLEAN;
   OVERRIDE;
         (*its cutable if no line refers to it and its not the first line*)
   var
    search: TObject;
    lineno, index: integer;
    ok: boolean;
    nextline: TProofline;

   function Refers (item: TObject): BOOLEAN;
    var
     localBool, doIt: boolean;
   begin
    localBool := FALSE;
    doIt := false;
    if ok then
     begin
      if (alreadyCut <> nil) then
       begin
        if (alreadyCut.GetSameItemNo(item) = 0) then  (*This means that the one*)
                                         (*that does the referring is not in the already cut list*)
        doIt := TRUE;
       end
      else
       doIt := TRUE;

      if doIt then
       begin
        localBool := (TProofline(item).fLineno > lineno) and ((TProofline(item).fFirstJustNo = lineno) or (TProofline(item).fSecondJustNo = lineno) or (TProofline(item).fThirdJustNo = lineno));
        if localBool then
        ok := FALSE;
       end;
     end;
    Refers := localBool;
   end;

  begin
   ok := TRUE;

   lineno := thisline.fLineno;

   search := fTail.LastThat(Refers);

   if ok then
    search := fHead.LastThat(Refers);

   if ok then
    if thisline = TProofline(fHead.First) then
     ok := FALSE;

   if ok then {next line must be out of subproof}
    if thisLine.fLastassumption then
     begin
      nextline := nil;
      index := fTail.GetSameItemNo(thisLine);

      if index <> 0 then
       index := index + fHead.fSize
      else
       index := fHead.GetSameItemNo(thisLine);

      index := index + 1;   (*looking for nextline*)

      if index <= fHead.fSize then
       nextline := TProofLine(fHead.At(index))
      else if (index <= fHead.fSize + fTail.fSize) then
       nextline := TProofLine(fTail.At(index - fHead.fSize));


      if nextline <> nil then
       if (alreadyCut <> nil) then
        while (alreadyCut.GetSameItemNo(nextline) <> 0) and (index <= (fHead.fSize + fTail.fSize)) do (*means*)
                                                         (*nextline has been cut so should not be counted*)
        begin
        index := index + 1;   (*looking for nextline*)

        if index <= fHead.fSize then
        nextline := TProofLine(fHead.At(index))
        else if (index <= fHead.fSize + fTail.fSize) then
        nextline := TProofLine(fTail.At(index - fHead.fSize));
        end;



      if nextline <> nil then
       begin
        if (nextline.fSubprooflevel > thisLine.fSubprooflevel) then
        ok := false;


        if (nextline.fSubprooflevel = thisLine.fSubprooflevel) then
        if not nextline.fLastAssumption then  (*next subproof*)
        ok := false;

       end;

     end;
   LineCutable := ok;
  end;


 {$S AInit}

  procedure TCopiProofWindow.ICopiProofWindow;
   var
    aCopiParser: TCopiParser;
  begin
   SELF.IProofWindow;

   fParser.Free;
   New(aCopiParser);
   FailNIL(aCopiParser);
   fParser := aCopiParser;

        {   fReplacement := itsReplacement; }
  end;


 {$S Copi2}
  procedure TCopiProofWindow.WriteEntireProof (aRefNum: integer);
   OVERRIDE;

   procedure WriteProofLine (item: TObject);

   begin
    TCopiProofline(item).WriteTo(aRefNum);
   end;

  begin
   fHead.Each(WriteProofLine);

   fTail.Each(WriteProofLine);
  end;

 {************General*****************}

 {$S Copi2}

  procedure TCopiProofWindow.CheckCellHeights;
   OVERRIDE;

   var
    textlist: TTextListView;
    start, stop, top, tailtop: integer;

   procedure CheckEach (anItem: integer);

    var
     aProofline: TProofline;

   begin
    if anItem <= top then {first list}
     aProofline := TProofline(fHead.At(anItem))
    else
     aProofline := TProofline(fTail.At(anItem - top));

    if textlist.GetItemHeight(anItem) <> kCellHeight then
     begin
      textlist.SetItemHeight(anItem, 1, kCellHeight);
     end
   end;

  begin
   top := fHead.fSize;
   tailtop := fTail.fSize;
   start := 1;
   stop := top + tailtop;
   textlist := fTextList;
   textlist.EachItemDo(start, stop, CheckEach);
  end;

 {$S Copi2}

  procedure TCopiProofWindow.CreateBlankStart;
   OVERRIDE; {Creates a blankline start}

   var
    newline: TCopiProofline;

  begin
   SupplyCopiProofline(newline);
   with newline do
    begin
     fLineno := 0;
     fBlankline := TRUE;
     fSelectable := FALSE;
     fjustification := '';
     fSubprooflevel := 0; {-1 in other}
     fHeadlevel := 0;
    end;

   fHead.InsertFirst(newline);

   newline := nil;
  end;


 {$S  Copi2}
  procedure TCopiProofWindow.DoChoice (origView: TView; itsChoice: integer);
   OVERRIDE;

   var
    templateID: IDType;
    OSdummy: OSErr;

  begin
   templateID := origView.fIdentifier;

   case itsChoice of
    mButtonHit:
     case templateID[4] of
      'C':
       OSdummy := PostEvent(keydown, 133219); {overring all to get c}
      'K':
       OSdummy := PostEvent(keydown, 143150); {overridding and and}
      'R':
       OSdummy := PostEvent(keydown, 131169); {overridding superone to get a}
      'Y':
       OSdummy := PostEvent(keydown, 133986); {overridding superone to get b}

      'z':
       OSdummy := PostEvent(keydown, 137277); {=}

      otherwise
       inherited DoChoice(origView, itsChoice);
     end;

    otherwise
     inherited DoChoice(origView, itsChoice);

   end;
  end;

 {$S  Copi2}

  function TCopiProofWindow.FindLastAssumption (var subhead: TProofline): boolean;
   OVERRIDE;
 {of proof as a whole}

   var
    dummy: TObject;

   function Premise (item: TObject): boolean;

    var
     aProofline: TCopiProofline;

   begin
    Premise := FALSE;
    aProofline := TCopiProofline(item);
    if (aProofline.fLastAssumption) and (aProofline.fSubprooflevel = TCopiProofline(fHead.Last).fSubprooflevel) then
     begin
      Premise := TRUE;
      subhead := aProofline;
     end;
   end;

  begin
   dummy := nil;
   if (TCopiProofline(fHead.Last).fSubprooflevel > 0) then
    dummy := fHead.LastThat(Premise);

   FindLastAssumption := dummy <> nil;

  end;

 {$S  Copi2}

  function TCopiProofWindow.FirstInstantiation (var instantiation: char; variForm, scope: TFormula): boolean;

   var
    found: boolean;
    index: integer;
    instForm: TFormula;

   function NotFreeInProof: boolean;

    function There (item: TObject): boolean;

     var
      foundthere: boolean;

    begin
     foundthere := FALSE;
     if not TCopiProofline(item).fBlankline then
      foundthere := TCopiProofline(item).fFormula.FreeTest(instForm);

     There := foundthere;
    end;

   begin
    NotFreeInProof := (fHead.FirstThat(There) = nil);
   end;

  begin
   found := FALSE;
   instantiation := 'u';  (*Copi variables are u to z*)

   supplyFormula(instForm);
   instForm.fKind := variable;

   while not found and (instantiation <= 'z') do
    begin
     instForm.fInfo := instantiation;

     found := NotFreeInProof;

     if found then
      begin
       found := scope.FreeForTest(instForm, variForm);  (*must not get captured in scope*)
      end;

     if not found then
      instantiation := CHR(ORD(instantiation) + 1);
    end;

   FirstInstantiation := found;
  end;


 {$S  Copi2}

  procedure TCopiProofWindow.ListAssumptions (localHead: TList; theLine: TProofline; var listHead: TList);
   OVERRIDE;

 {forms a list of new, non standing, assumption formulas of theLine using linerecords as cells}
 {listHead list should be created and destroyed elsewhere}

   var
    level, standinglevel: integer;

   function Premise (item: TObject): boolean;

    var
     aProofline: TCopiProofline;

   begin
    aProofline := TCopiProofline(item);
    Premise := (aProofline.fLastAssumption) and (aProofline.fSubprooflevel = level) and (aProofline.fLineno <= theLine.fLineno);
   end;

  begin

   level := theLine.fSubprooflevel;

   standinglevel := 0;

   while (level > standinglevel) do
    begin
     listHead.InsertFirst(TCopiProofline(localHead.LastThat(Premise)).fFormula);

     level := level - 1;
    end;

  end;

 {$S  Copi2}

  procedure TCopiProofWindow.ResetToPseudoTail;
   OVERRIDE;

   var
    taillist, tempList: TList;
    top, count: integer;

   procedure CheckLine (item: TObject);

    var
     aProofline, theNextProofline: TCopiProofline;
     doselect: boolean;

   begin
    count := count + 1;
    aProofline := TCopiProofline(item);

    if aProofline.fBlankline then
     doselect := FALSE
    else
     begin
      if (aProofline.fFormula.fInfo = '?') then
       doselect := FALSE
      else
       begin
        tempList := NewList;
        ListAssumptions(fHead, aProofline, tempList);
        if Subset(tempList, taillist) then
        doselect := TRUE
        else
        doselect := FALSE;
        tempList.DeleteAll;
        tempList.Free;
       end;
     end;

    TCopiProofline(item).fSelectable := doselect;

    TCopiProofline(item).fSubProofSelectable := FALSE; {Copi doesnt do this}

   end;

  begin
   top := fHead.fSize;
   count := 0;

   taillist := NewList;
   ListAssumptions(fHead, TCopiProofline(fHead.Last), taillist);

   fHead.Each(CheckLine);

   taillist.DeleteAll;
   taillist.Free;

  end;


 {$S  Copi2}
  procedure TCopiProofWindow.SetUpControls (enable: boolean);
   OVERRIDE;

   var
    thisView: TView;

  begin


   thisView := SELF.FindSubView('BUT2');
   if thisView <> nil then
    TControl(thisView).DimState(not Enable, TRUE);

   thisView := SELF.FindSubView('BUT1');
   if thisView <> nil then
    TControl(thisView).DimState(not Enable, TRUE);

   inherited SetUpControls(enable);
  end;

 {*****Copi Proof Window Rules*}

 {$S  Copi2}

  function TCopiProofWindow.DoConj: TCommand;

   var

    newline: TCopiProofline;
    firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    formula1, formulanode: TFormula;
    rad1, rad2, prompt: str255;

  begin

   DoConj := gNoChanges;

   if fTemplate then {gTemplate}
    DoConj := DoHintConj
   else
    begin

     if (fTextList.TotalSelected = 2) then
      if fTextList.TwoSelected(firstline, secondline) then
       begin
        GetIndString(prompt, kStringRSRCID, 5); { Choose on buttons where you want the}
 {                                                             first selected line to go.';}
        GetIndString(rad1, kStringRSRCID, 6); { ''On left?';}
        GetIndString(rad2, kStringRSRCID, 7); { 'On right?'}

        if GetTheChoice(rad1, rad2, prompt) then {makes them choose left or right}
        begin

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyFormula(formulanode);
        with formulanode do
        begin
        fKind := binary; {formulanode is the new formula node}
        fInfo := chAnd;
        if fRadio then
        begin
        fLlink := firstline.fFormula.CopyFormula;
        fRlink := secondline.fFormula.CopyFormula;
        end
        else
        begin
        fLlink := secondline.fFormula.CopyFormula;
        fRlink := firstline.fFormula.CopyFormula;
        end
        end;

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        if fRadio then
        begin
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        end
        else
        begin
        ffirstjustno := secondline.fLineno;
        fsecondjustno := firstline.fLineno;
        end;
        fjustification := concat(chBlank, 'Conj.');
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoConj := aLineCommand;

        end;
       end;

     if fTextList.TotalSelected = 1 then
      if fTextList.OneSelected(firstline) then
       begin
        SupplyFormula(formulanode);
        with formulanode do
        begin
        fKind := binary; {formulanode is the new formula node}
        fInfo := chAnd;
        fLlink := firstline.fFormula.CopyFormula;
        fRlink := firstline.fFormula.CopyFormula;
        end;

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := firstline.fLineno;
        fjustification := ' Conj';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoConj := aLineCommand;

       end;
    end;
  end;

 {$S  Copi2}

  function TCopiProofWindow.DoSimp: TCommand;

   var

    newline: TCopiProofline;
    firstline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;

  begin

   DoSimp := gNoChanges;

   if (fTextList.TotalSelected = 1) then
    if fTextList.OneSelected(firstline) then
     begin
      if (firstline.fFormula.fKind = binary) then
       if firstline.fFormula.fInfo = chAnd then
        begin

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := firstline.fFormula.fLlink.CopyFormula;
        ffirstjustno := firstline.fLineno;
        fjustification := ' Simp.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoSimp := aLineCommand;

        end;
     end;
  end;

 {$S  Copi2}

  function TCopiProofWindow.DoAdd: TCommand;

   var

    newline: TCopiProofline;
    firstline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    root, formulanode: TFormula;
    cancel: boolean;
    rad1, rad2, prompt: str255;

  begin

   DoAdd := gNoChanges;

   if fTemplate then {gTemplate}
    DoAdd := DoHintAdd
   else
    begin

     if (fTextList.TotalSelected = 1) then
      if fTextList.OneSelected(firstline) then
       begin
                     {  GetIndString(prompt, kStringRSRCID, 11);  Choose on buttons }
                     {  GetIndString(rad1, kStringRSRCID, 12);  ''On left?';}
                     { GetIndString(rad2, kStringRSRCID, 13);  'On right?'}

        rad1 := strNull;
        rad2 := strNull;
        prompt := 'New Formula?';

        GetTheRoot(rad1, rad2, prompt, root, cancel);
        if not cancel then
        begin

        SupplyFormula(formulanode);
        formulanode.fKind := binary;  {formulanode is the new formula node}
        formulanode.fInfo := chOr;
        formulanode.fLlink := firstline.fFormula.CopyFormula;
        formulanode.fRlink := root;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fjustification := ' Add.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoAdd := aLineCommand;

        end;
       end;
    end;
  end;

 {$S  Copi2}

  function TCopiProofWindow.DoDS: TCommand;

   var

    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    formula1, formulanode: TFormula;
    rad1, rad2, prompt: str255;

  begin

   DoDS := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin

      if (secondline.fFormula.fInfo = chOr) then
       if (firstline.fFormula.fInfo = chNeg) then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 1 contain the or}
        templine := nil;
        end;

      if (firstline.fFormula.fInfo = chOr) then
       if (secondline.fFormula.fInfo = chNeg) then
        if Equalformulas(firstline.fFormula.fLlink, secondline.fFormula.fRlink) then
        begin
        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new proofline}
        with newline do
        begin
        fFormula := firstline.fFormula.fRlink.CopyFormula;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' D.S.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoDS := aLineCommand;

        end;
     end;

  end;


 {$S  Copi2}

  function TCopiProofWindow.DoMP: TCommand;

   var

    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    formula1, formulanode: TFormula;
    rad1, rad2, prompt: str255;

  begin

   DoMP := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin

      if (secondline.fFormula.fInfo = chImplic) then
       if Equalformulas(secondline.fFormula.fLlink, firstline.fFormula) then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 2 contain the arrow}
        templine := nil;
        end;
      if (firstline.fFormula.fInfo = chImplic) then
       if Equalformulas(firstline.fFormula.fLlink, secondline.fFormula) then
        begin
        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new proofline}
        with newline do
        begin
        fFormula := firstline.fFormula.fRlink.CopyFormula;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' M.P.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoMP := aLineCommand;

        end;
     end;

  end;

 {$S Copi}

  function TCopiProofWindow.DoMT: TCommand;

   var

    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    formula1, formulanode, negnode: TFormula;

  begin

   DoMT := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin

      if (secondline.fFormula.fInfo = chImplic) then
       if (firstline.fFormula.fInfo = chNeg) then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 2 contain the arrow}
        templine := nil;
        end;

      if (firstline.fFormula.fInfo = chImplic) then
       if (secondline.fFormula.fInfo = chNeg) then
        if Equalformulas(firstline.fFormula.fRlink, secondline.fFormula.fRlink) then
        begin

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyFormula(negnode);
        negnode.fKind := unary;
        negnode.fInfo := chNeg;
        negnode.fRlink := firstline.fFormula.fLlink.CopyFormula;

        SupplyCopiProofline(newline); {newline points to new proofline}
        with newline do
        begin
        fFormula := negnode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' M.T.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoMT := aLineCommand;

        end;
     end;

  end;

 {$S Copi}

  function TCopiProofWindow.DoHS: TCommand;

   var

    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    p, r, formulanode: TFormula;

  begin

   DoHS := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin
      if (firstline.fFormula.fInfo = chImplic) then
       if (secondline.fFormula.fInfo = chImplic) then
        if Equalformulas(secondline.fFormula.fRlink, firstline.fFormula.fLlink) then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 1 contain the arrow}
        templine := nil;
        end;

      if (firstline.fFormula.fInfo = chImplic) then
       if (secondline.fFormula.fInfo = chImplic) then
        if Equalformulas(secondline.fFormula.fLlink, firstline.fFormula.fRlink) then
        begin
        p := firstline.fFormula.fLlink;
        r := secondline.fFormula.fRlink;

        SupplyFormula(formulanode);
        formulanode.fKind := binary;  {formulanode is the new formula}
 {                                                                 node}
        formulanode.fInfo := chImplic;
        formulanode.fLlink := p.CopyFormula;
        formulanode.fRlink := r.CopyFormula;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new proofline}
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' H.S.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoHS := aLineCommand;

        end;
     end;

  end;

 {$S Copi}

  function TCopiProofWindow.DoCD: TCommand;

   var
    level: integer;
    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}

    aLineCommand: TCopiLineCommand;
    formulanode, q, s: TFormula;

  begin

   DoCD := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then



     begin
      if secondline.fFormula.fInfo = chAnd then
       begin
        templine := firstline;
        firstline := secondline;
        secondline := templine;
        templine := nil;
       end; {make line1 contain the and}


      if firstline.fFormula.fInfo = chAnd then
       if firstline.fFormula.fLlink.fInfo = chImplic then
        if firstline.fFormula.fRlink.fInfo = chImplic then
        if secondline.fFormula.fInfo = chOr then

        begin
        if Equalformulas(firstline.fFormula.fLlink.fLlink, secondline.fFormula.fLlink) then
        if Equalformulas(firstline.fFormula.fRlink.fLlink, secondline.fFormula.fRlink) then
        begin
        q := firstline.fFormula.fLlink.fRlink;
        s := firstline.fFormula.fRlink.fRlink;

        SupplyFormula(formulanode);
        formulanode.fKind := binary; {formulanode is the new formula}
 {                                                                      node}
        formulanode.fInfo := chOr;
        formulanode.fLlink := q.CopyFormula;
        formulanode.fRlink := s.CopyFormula;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new}
 {                                                                      proofline}
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' C.D.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;

        DoCD := aLineCommand;

        end;

        end;
     end;

  end;

         {$S Copi}

  function TCopiProofWindow.DoDD: TCommand;

   var
    level: integer;
    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;  {check}

    aLineCommand: TCopiLineCommand;
    formulanode, p, r: TFormula;

  begin

   DoDD := gNoChanges;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then



     begin
      if secondline.fFormula.fInfo = chAnd then
       begin
        templine := firstline;
        firstline := secondline;
        secondline := templine;
        templine := nil;
       end; {make line1 contain the and}


      if firstline.fFormula.fInfo = chAnd then
       if firstline.fFormula.fLlink.fInfo = chImplic then
        if firstline.fFormula.fRlink.fInfo = chImplic then
        if secondline.fFormula.fInfo = chOr then
        if secondline.fFormula.fLlink.fInfo = chNeg then
        if secondline.fFormula.fRlink.fInfo = chNeg then

        begin

        if Equalformulas(firstline.fFormula.fLlink.fRlink, secondline.fFormula.fLlink.fRlink) then
        if Equalformulas(firstline.fFormula.fRlink.fRlink, secondline.fFormula.fRlink.fRlink) then
        begin
        p := firstline.fFormula.fLlink.fLlink;
        r := firstline.fFormula.fRlink.fLlink;

        SupplyFormula(formulanode);
        formulanode.fKind := Unary;
        formulanode.fInfo := chNeg;
        formulanode.fRlink := p.CopyFormula;

        p := formulanode;  (*dont be confused p is now not p*)
        formulanode := nil;

        SupplyFormula(formulanode);
        formulanode.fKind := unary;
        formulanode.fInfo := chNeg;
        formulanode.fRlink := r.CopyFormula;

        r := formulanode;  (*dont be confused r is now not r*)
        formulanode := nil;

        SupplyFormula(formulanode);
        formulanode.fKind := binary;
        formulanode.fInfo := chOr;
        formulanode.fLlink := p;
        formulanode.fRlink := r;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new}
 {                                                                      proofline}
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' D.D.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;

        DoDD := aLineCommand;

        end;

        end;
     end;

  end;


 {$S Copi}

  function TCopiProofWindow.DoNewGoal: TCommand;
   OVERRIDE;

   var
    newline: TCopiProofline;
    aLineCommand: TCopiLineCommand;
    prompt, rad1, rad2: str255;
    root, formulanode: TFormula;
    cancel, choiceneeded: boolean;
    level: integer;

  begin
   DoNewGoal := gNoChanges;
   level := TCopiProofline(fHead.Last).fSubprooflevel;
   if fTail.fSize <> 0 then
    choiceneeded := level <> TCopiProofline(fTail.First).fSubprooflevel
   else
    choiceneeded := FALSE;

   begin
    GetIndString(prompt, kStringRSRCID, 2); { prompt := 'New Assumption?';}
    GetIndString(rad1, kStringRSRCID, 3); { 'Keep Last'}
    GetIndString(rad2, kStringRSRCID, 4); { Drop Last'}

    if choiceneeded then
     begin
      rad1 := 'After Last?';
      rad2 := 'Before Next?';
     end
    else
     begin
      rad1 := strNull;
      rad2 := strNull;
     end;


    prompt := 'New Goal? Then click on question mark if you wish to work on a different sub-problem.';

    GetTheRoot(rad1, rad2, prompt, root, cancel);
    if not cancel then
     begin
      if choiceneeded then
       if not fRadio then
        level := TCopiProofline(fTail.First).fSubprooflevel;


      New(aLineCommand);
      FailNil(aLineCommand);
      aLineCommand.ICopiLineCommand(cAddLine, SELF);

      SupplyFormula(formulanode);
      formulanode.fInfo := '?';
      formulanode.fKind := predicator;
      SupplyCopiProofline(newline); {newline points to new proofline}
      with newline do
       begin
        fFormula := formulanode;
        fJustification := '?';
        fselectable := false;
        fSubProofLevel := level;
       end;

      aLineCommand.fNewlines.InsertLast(newline);
      newline := nil;

      SupplyCopiProofline(newline); {newline points to new proofline}
      with newline do
       begin
        fSubprooflevel := level;
        fFormula := root;
        fjustification := '?';
       end;

      aLineCommand.fNewlines.InsertLast(newline);

      DoNewGoal := aLineCommand;
     end;
   end;
  end;



 {$S Copi}

  function TCopiProofWindow.DoAss: TCommand;

   var
    newline: TCopiProofline;
    aLineCommand: TCopiLineCommand;
    prompt, rad1, rad2: str255;
    root: TFormula;
    cancel: boolean;

  begin
   DoAss := gNoChanges;
   cancel := false;

   if TCopiProofline(fHead.Last).fSubprooflevel > kCopiMaxnesting then
    begin
     GetIndString(prompt, kStringRSRCID, 1);
     BugAlert(prompt); {'phew... no more please.')}
    end
   else
    begin
     if fTemplate then {gTemplate}
      begin
       prompt := 'Cancel! Ass is not usual with Tactics-- you will never be able to drop a new assumption.';

       cancel := not GetTheChoice(strNull, strNull, prompt);
      end;


     if not cancel then
      begin
       GetIndString(prompt, kStringRSRCID, 2); { prompt := 'New Antecedent?';}
       GetIndString(rad1, kStringRSRCID, 3); { 'Keep Last'}
       GetIndString(rad2, kStringRSRCID, 4); { Drop Last'}

       rad1 := strNull;
       rad2 := strNull;
       prompt := 'New Assumption?';

       GetTheRoot(rad1, rad2, prompt, root, cancel);
       if not cancel then
        begin
        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline); {newline points to new proofline}

        with newline do
        begin
        fSubprooflevel := TCopiProofline(fHead.Last).fSubprooflevel + 1;
        fFormula := root;
        fjustification := strNull;
        fLastassumption := TRUE;
        end;

        aLineCommand.fNewlines.InsertLast(newline);

        DoAss := aLineCommand;

        end;

      end;
    end;

  end;

 {$S Copi}



 {$S Copi}

  function TCopiProofWindow.DoEI: TCommand;
   OVERRIDE;

   var
    firstline: TProofline;
    newline: TCopiProofline;
    aLineCommand: TCopiLineCommand;
    term: char;
    formulanode, termForm: TFormula;

  begin
   DoEI := gNoChanges;

   if (fTextList.TotalSelected = 1) then
    if fTextList.OneSelected(firstline) then
     if firstline.fFormula.fInfo[1] = chExiquant then
      if firstline.fFormula.Scope.Freetest(firstline.fFormula.QuantVarForm) then

       begin
        if FirstInstantiation(term, firstline.fFormula.QuantVarForm, firstline.fFormula.Scope) then
        begin
        supplyformula(termForm);
        termForm.fInfo := term;
        termForm.fKind := variable;


        formulanode := firstline.fFormula.fRlink.CopyFormula;

        NewSubTermVar(formulanode, termForm, firstline.fFormula.QuantVarForm);

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fjustification := concat(' EI ', term);
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoEI := aLineCommand;

        end
        else
        begin
                               {GetIndString(prompt, kStringRSRCID, 1);}
        BugAlert('No more instantiating variables available.');
        end;
       end;

  end;

 {$S Copi}
  function TCopiProofWindow.DoEG: TCommand;
   OVERRIDE;

   var

    firstline: TProofline;
    newline: TCopiProofline;
    aLineCommand: TCopiLineCommand;
    firstformula, formulanode, root, copyf, currentNode, currentCopyNode, termForm, variForm: TFormula;
    outPutStr, prompt: str255;
    cancel, found, done: boolean;
    occurences, metSoFar, i: integer;

               {This is quite complicated because generalization is done on individual occurrences of a }
           {term.  I take a copy.  Then I insert and remove markers in the}
           {original and display it and alter the copy if the user indicates.}

                 (*dont need current index*)

   procedure RemoveMarker (yes: boolean);
      {removes marker and alters copy if needed}

   begin
    delete(currentNode.fInfo, 1, 1); {removes marker}
    if yes then
     begin
      currentCopyNode.fKind := variable;
      currentCopyNode.fInfo := variForm.fInfo;  (*surgery*)
      if (currentCopyNode.fRlink <> nil) then
       currentCopyNode.fRlink.DismantleFormula;
      currentCopyNode.fRlink := nil;
     end;

   end;

  begin

   DoEG := gNoChanges;
   cancel := FALSE;
   found := FALSE;
   prompt := strNull;
   outputStr := strNull;

   if fTemplate then {gTemplate}
    DoEG := DoHintEG
   else
    begin

     if (fTextList.TotalSelected = 1) then
      if fTextList.OneSelected(firstline) then
       begin

        repeat

        GetIndString(prompt, kStringRSRCID, 19); { Term }

        prompt := concat(outPutStr, prompt);

        GetTheTerm(strNull, strNull, prompt, termForm, cancel);

        occurences := firstline.fFormula.NumofFreeOccurences(termForm);

        if (occurences = 0) then
        outputStr := concat(prompt, ' must occur in the formula.')
        else
        found := TRUE;


        until found or CANCEL;

        outputStr := strNull;
        prompt := strNull;
        found := FALSE;

        if not cancel then
        repeat
        begin

        GetIndString(prompt, kStringRSRCID, 20); { Variable}

        prompt := concat(outputStr, prompt);

        if not GetTheChoice(strNull, strNull, prompt) then
        cancel := TRUE
        else if length(prompt) = 1 then
        begin
        if prompt[1] in gCopiVariables then
        begin
        found := TRUE;

        SupplyFormula(variForm);
        with variForm do
        begin
        fKind := variable;
        fInfo := prompt[1];
        end;

        end
        else
        outputStr := concat(prompt, ' is not a variable.');
        end;

        end;

        until found or cancel;

        outputStr := strNull;
        prompt := strNull;

        if not cancel then
        repeat
        begin
        found := FALSE;

        copyf := firstline.fFormula.CopyFormula;

        firstformula := firstline.fFormula;

        if occurences = 1 then {this is a silly way of finding the one occurence}
 {						                                      to alter the copy.}
        begin
        currentNode := nil;
        currentCopyNode := nil;
        metSoFar := 0;
        done := FALSE;

        NewInsertMarker(termForm, 1, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode);
        RemoveMarker(TRUE);

        prompt := 'Press Go to display the scope. Then Press Go again, (or Cancel or edit it).';

        if not GetTheChoice(strNull, strNull, prompt) then
        cancel := TRUE;

        end;


        if occurences > 1 then
        begin
        i := 1;

        while (i <= occurences) and not cancel do
        begin
        currentNode := nil;
        currentCopyNode := nil;

        metSoFar := 0;

        done := FALSE;

        NewInsertMarker(termForm, i, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode);
        firstline.fFormula := firstformula;
        fTextList.InvalidateItem(fTextList.FirstSelectedItem);

        prompt := 'Do you wish to generalize on the arrowed occurrence?';

        if i = occurences then
        prompt := 'After this choice, which is the last, the scope will be displayed. Press Go again (or edit it).';

        if not GetTheChoice('Yes?', 'No?', prompt) then
        cancel := TRUE;
        RemoveMarker(fRadio);
        i := i + 1;

        end;

        end;

        firstline.fFormula := firstformula;

        fTextList.InvalidateItem(fTextList.FirstSelectedItem);

        if cancel then
        copyf.DismantleFormula
        else
        begin

                                    {****}

                                    {  GetIndString(prompt, kStringRSRCID, 21);  }

        fParser.WriteFormulaToString(copyf, outputStr);

        copyf.DismantleFormula;

        prompt := outputStr;

        GetTheRoot(strNull, strNull, prompt, root, cancel);

        if not cancel then
        begin
        copyf := root.CopyFormula;
        NewSubTermVar(root, termForm, variForm);

        if not Equalformulas(root, firstline.fFormula) then
        begin
        cancel := true;
        BugAlert('This cannot be the scope of your formula-- please start again.')
        end
        else
        begin
        root.DismantleFormula;
        root := copyf.CopyFormula;
        if not root.FreeForTest(termForm, variForm) then {}
        begin

        fParser.WriteFormulaToString(root, outputStr);

        outputStr := concat(' in ', outputStr, '.');

        fParser.WriteTermToString(termForm, prompt);

        BugAlert(concat(prompt, ' for ', variForm.fInfo, outputStr, ' leads to capture.'));

        root.DismantleFormula; {check}
        copyf.DismantleFormula;
        variForm.DismantleFormula;
        end
        else
        found := TRUE;
        end;
        end;
        end;
        end;

        until found or cancel;

        if not cancel then
        begin

        SupplyFormula(formulanode);
        with formulanode do
        begin
        fKind := quantifier;
        fInfo := chExiquant;
        fLlink := variForm;
        fRlink := copyf;
        end;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fjustification := ' EG';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoEG := aLineCommand;

        end;
       end;
    end;
  end;

 {$S Copi}

  function TCopiProofWindow.DoUI: TCommand;
   OVERRIDE;

   var

    firstline: TProofline;
    newline: TCopiProofline;
    aLineCommand: TCopiLineCommand;
    formula1, termForm: TFormula;
    outPutStr, prompt: str255;
    cancel: boolean;

  begin

   DoUI := gNoChanges;
   cancel := false;

   if (fTextList.TotalSelected = 1) then
    if fTextList.OneSelected(firstline) then
     if firstline.fFormula.finfo[1] = chUniquant then
      if firstline.fFormula.fRLink.Freetest(firstline.fFormula.QuantVarForm) then
       begin

        GetIndString(prompt, kStringRSRCID, 18); { Term}

        GetTheTerm(strNull, strNull, prompt, termForm, cancel);


        if not cancel then
        begin
        formula1 := firstline.fFormula.fRlink.CopyFormula;

        if not formula1.FreeForTest(termForm, firstline.fFormula.QuantVarForm) then
        begin
        fParser.WriteFormulaToString(termForm, prompt);
        fParser.WriteFormulaToString(formula1, outputStr);

        outputStr := concat(' in ', outputStr, '.');

        BugAlert(concat(prompt, ' for ', StrofChar(firstline.fFormula.QuantVar), outputStr, ' leads to capture.'));

        formula1.DismantleFormula;
        end
        else
        begin
        NewSubTermVar(formula1, termForm, firstline.fFormula.QuantVarForm);

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formula1;
        ffirstjustno := firstline.fLineno;
        fjustification := ' UI';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoUI := aLineCommand;

        end;

        end;
       end;
  end;


 {$S Copi}

  function TCopiProofWindow.DoUG: TCommand;
   OVERRIDE;

   var

    firstline: TProofline;
    newline: TCopiProofline;
    faultyline: TObject;
    aLineCommand: TCopiLineCommand;
    formulanode, scope, muForm, freeVarForm, nuForm: TFormula;
    outPutStr, prompt: str255;
    cancel, found: boolean;
    mu, freeVar, nu: char;


   function InCP (item: TObject): boolean;  (*occurs free in assumption of CP or other*)

    var
     found: boolean;

   begin
    found := FALSE;
    if not TCopiProofline(item).fBlankline then
     if TCopiProofline(item).fSelectable then
      if TCopiProofline(item).fJustification = strNull then
       found := TCopiProofline(item).fFormula.Freetest(muForm);

    InCP := found;
   end;

 {this needs a bit of abstraction}
   function EIFreeVar (firstline: TProofline; var freevar: char): boolean;

    procedure Test (item: TObject);
     var
      aProofline: TCopiProofline;
    begin
     if not found then
      begin
       aProofline := TCopiProofline(item);
       if aProofline.fLineno <= firstline.fLineno then
        if not aProofline.fBlankline then
        if length(aProofline.fjustification) > 4 then
        if (aProofline.fjustification[2] = 'E') and (aProofline.fjustification[3] = 'I') then
        begin
        freevar := aProofline.fjustification[5];

        freeVarForm.fInfo := freevar;

        found := firstline.fFormula.Freetest(freevarForm);
        end;
      end;
    end;

   begin
    found := FALSE;
    SupplyFormula(freeVarForm);
    freeVarForm.fKind := variable;
    fHead.Each(Test);
    freeVarForm.DismantleFormula;
    EIFreeVar := found;
   end;

  begin

   DoUG := gNoChanges;
   cancel := FALSE;
   found := FALSE;
   outPutStr := strNull;

   if fTemplate then {gTemplate}
    DoUG := DoHintUG
   else
    begin

     if (fTextList.TotalSelected = 1) then
      if fTextList.OneSelected(firstline) then
       if EIFreeVar(firstline, freeVar) then
        begin
        BugAlert(concat(freeVar, ' was introduced by EI.'));
        end
       else
        begin

                     {GetIndString(prompt, kStringRSRCID, 17);  Variable of quantification }

        repeat
                         {GetIndString(prompt, kStringRSRCID, 19); Term}
        prompt := 'Variable to generalize on?';

        gInputStr := concat(outPutStr, prompt);

        if not GetTheChoice(strNull, strNull, gInputStr) then
        cancel := TRUE
        else if length(gInputStr) > 0 then
        begin
        GetStringInput;
        skip(1, logicfilter);  (*primes gCurrch, and gLookaheadCh*)
                              { ReadTerm; }
        if gCurrCh in gCopiVariables then
        begin
        found := TRUE;
        mu := gCurrCh;
                                    {beta := gCurrStrCh;}
        end
        else
        outPutStr := concat(gCurrCh, ' is not a variable.');
        end;

        until found or cancel;



        found := FALSE;
        outPutStr := strNull;

        if not cancel then
        begin
        supplyFormula(muForm);
        muForm.fKind := variable;
        muForm.fInfo := mu;
        if not firstline.fFormula.Freetest(muForm) then
        begin
        fParser.WriteFormulaToString(TCopiProofline(firstline).fFormula, outPutStr);
        BugAlert(concat(mu, ' must occur freely in ', outputStr, ' .'));
        muForm.DismantleFormula;
        cancel := TRUE;
        end;
        end;

        if not cancel then
        begin
        faultyline := fHead.FirstThat(InCP);
        if faultyline <> nil then
        begin
        fParser.WriteFormulaToString(TCopiProofline(faultyline).fFormula, outPutStr);
        BugAlert(concat(mu, ' occurs freely in assumption ', outPutStr, ' .'));
        muForm.DismantleFormula;
        cancel := TRUE;
        end
        end;


        if not cancel then
        repeat
                               {GetIndString(prompt, kStringRSRCID, 19); Term}

        prompt := 'Variable to generalize with?';

        gInputStr := concat(outPutStr, prompt);

        if not GetTheChoice(strNull, strNull, gInputStr) then
        cancel := TRUE
        else if length(gInputStr) > 0 then
        begin
        GetStringInput;
        skip(1, logicfilter);  (*primes gCurrch, and gLookaheadCh*)
                                    {ReadTerm; }
        if gCurrCh in gCopiVariables then
        begin
        found := TRUE;
        nu := gCurrCh;
        end
        else
        outPutStr := concat(gCurrCh, ' is not a variable.');
        end;

        until found or cancel;

        if not cancel then
        begin
        SupplyFormula(nuForm);
        nuForm.fKind := variable;
        nuForm.fInfo := nu;
        if not firstline.fFormula.FreeForTest(nuForm, muForm) then {check this is the right way round}
        begin

        fParser.WriteFormulaToString(firstline.fFormula, outputStr);

        outputStr := concat(' in ', outputStr, '.');

        BugAlert(concat(StrofChar(nu), ' for ', StrofChar(mu), outputStr, ' leads to capture.'));


        muForm.DismantleFormula;
        nuForm.DismantleFormula;

        end
        else
        begin

        scope := firstline.fFormula.CopyFormula;
        NewSubTermVar(scope, nuForm, muForm);  {ok through here}

        SupplyFormula(formulanode);
        with formulanode do
        begin
        fKind := quantifier;
        fInfo := chUniquant;
        fLlink := nuForm;
        end;

        formulanode.fRlink := scope;  {!!}


        muForm.DismantleFormula;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fjustification := ' UG';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        formulanode := nil;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoUG := aLineCommand;

        end;
        end;
        end;
    end;
  end;

  function TCopiProofWindow.DoII: TCommand;

   var
    newline: TCopiProofline;
    templine, firstline, secondline: TProofline;
    aLineCommand: TCopiLineCommand;
    formulanode, firstformula, secondformula, firstlink, secondlink, nuForm, muForm: TFormula;
    outPutStr, prompt: str255;
    cancel: boolean;
    negNum1, negNum2: integer;

   function NumofNegations (root: TFormula): integer;
    var
     i: integer;
   begin
    i := 0;
    while (root.fInfo = chNeg) do
     begin
      root := root.fRlink;
      i := i + 1;
     end;
    NumofNegations := i;
   end;

  begin

   DoII := gNoChanges;
   cancel := FALSE;
   outPutStr := strNull;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then
     begin
      negNum1 := NumofNegations(firstline.fFormula);
      negNum2 := NumofNegations(secondline.fFormula);

      if (negNum1 > 0) or (negNum2 > 0) then
       begin
        if negNum1 > negNum2 then
        begin
        templine := firstline;
        firstline := secondline;
        secondline := templine;
        templine := nil;
        end;

        begin

                     {GetIndString(prompt, kStringRSRCID, 17);  Variable of quantification }


        prompt := 'Nu-term?';

        GetTheTerm(strNull, strNull, prompt, nuForm, cancel);

        outPutStr := strNull;

        if not cancel then
        begin
        prompt := 'Mu-term?';

        GetTheTerm(strNull, strNull, prompt, muForm, cancel);

        if cancel then
        nuForm.dismantleformula;
        end;

        if not cancel then
        begin

        if not secondline.fFormula.Freetest(muForm) then
        begin
        fParser.WriteFormulaToString(TCopiProofline(secondline).fFormula, outPutStr);
        fParser.WriteFormulaToString(muForm, prompt);
        BugAlert(concat(prompt, ' must occur "freely" in ', outputStr, ' .'));
        muForm.DismantleFormula;
        nuForm.dismantleformula;
        cancel := TRUE;
        end;
        end;


        if not cancel then
        begin
        firstformula := firstline.fFormula.CopyFormula;
        secondformula := secondline.fFormula.CopyFormula;



                                         {NewSubTermVar(firstformula,nuForm,muForm); }

        NewSubTermVar(secondformula, nuForm, muForm);

        if not EqualFormulas(firstformula, secondformula.fRlink) then

        begin
        firstformula.DismantleFormula;
        secondformula.DismantleFormula;
        nuForm.DismantleFormula;
        muForm.DismantleFormula;


                                         {error message}
        end


        else
        begin
        firstformula.DismantleFormula;
        secondformula.DismantleFormula;

        SupplyFormula(secondlink);
        with secondlink do
        begin
        fKind := kons;
        fLlink := muForm;
        end;

        SupplyFormula(firstlink);
        with firstlink do
        begin
        fKind := kons;
        fLlink := nuForm;
        fRlink := secondlink;
        end;

        SupplyFormula(firstformula);
        with firstformula do
        begin
        fKind := equality;
        fInfo := chEquals;
        fRlink := firstlink;
        end;


        SupplyFormula(formulanode);
        with formulanode do
        begin
        fKind := unary;
        fInfo := chNeg;
        fRlink := firstformula;
        end;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' Id.';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoII := aLineCommand;

        end;
        end;
        end;
       end;
     end;

  end;

 {$S Copi}

  function TCopiProofWindow.DoIE: TCommand;

   var
    copyf, firstformula, currentNode, currentCopyNode, alphaForm, gammaForm, replacementForm: TFormula;
    newline: TCopiProofline;
    firstline, secondline, templine: TProofline;
    alphaStr, gammaStr, markerStr, variStr, termStr: string[1];
    subsinfirst, done, cancel, found: boolean;
    aLineCommand: TCopiLineCommand;
    occurences, inFirst, inSecond, metSoFar, i: integer;
    prompt, outputStr: str255;


   procedure RemoveMarker (yes: boolean);
      {removes marker and alters copy if needed}

   begin
    delete(currentNode.fInfo, 1, 1); {removes marker}
    if yes then
     begin
      currentCopyNode.fKind := replacementForm.fKind;
      currentCopyNode.fInfo := replacementForm.fInfo;  (*surgery*)
      if (currentCopyNode.fRlink <> nil) then
       currentCopyNode.fRlink.DismantleFormula;

      if replacementForm.fRLink <> nil then
       currentCopyNode.fRlink := replacementForm.fRlink.CopyFormula
      else
       currentCopyNode.fRlink := nil;
     end;

   end;

  begin {1}
   DoIE := gNoChanges;

   cancel := FALSE;
   found := FALSE;
   done := FALSE;
   subsinfirst := TRUE;  (*this means that the second is the identity*)

   occurences := 0;
   metSoFar := 0;
   inFirst := 0;
   insecond := 0;
   i := 0;
   prompt := strNull;
   outputStr := strNull;

   if (fTextList.TotalSelected = 2) then
    if fTextList.TwoSelected(firstline, secondline) then

     if ((firstline.fFormula.fKind = equality) or (firstline.fFormula.fKind = predicator)) then
      if ((secondline.fFormula.fKind = equality) or (secondline.fFormula.fKind = predicator)) then


       begin {2}

        if firstline.fFormula.fKind = equality then
        begin {3}
        if not (secondline.fFormula.fKind = equality) then
        begin {4}
        subsinfirst := FALSE;   (*the first is the identity*)
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 2 contain the equality}
        templine := nil;
        end{4}
        else
        begin
           {bothequals}

        inFirst := firstline.fFormula.NumofFreeOccurences(secondline.fFormula.FirstTerm) + firstline.fFormula.NumofFreeOccurences(secondline.fFormula.SecondTerm);
        inSecond := secondline.fFormula.NumofFreeOccurences(firstline.fFormula.FirstTerm) + secondline.fFormula.NumofFreeOccurences(firstline.fFormula.SecondTerm);

 {now, if neither of the second terms appear in the first, we want to subs in the second}
 {if neeither of the first terms appear in the second, we want to subs in the first}
 {otherwise we have to ask}

        if InFirst = 0 then
        begin {4}
        subsinfirst := FALSE;   (*the first is the identity*)
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 2 contain the equality}
        templine := nil;
        end {4}

        else if InSecond = 0 then {leave them as they are}

        else
        begin

        prompt := 'Do you wish to substitute in the first or in the second?';

        if not GetTheChoice('First?', 'Second?', prompt) then
        cancel := TRUE;

        if not fRadio then
        begin {4}
        subsinfirst := FALSE;   (*the first is the identity*)
        templine := firstline;
        firstline := secondline;
        secondline := templine; {to make line 2 contain the equality}
        templine := nil;
        end;{4}
        end;{3}
        end;
        end;






        if secondline.fFormula.fKind = equality then
        begin {5}
        alphaForm := secondline.fFormula.FirstTerm;
        gammaForm := secondline.fFormula.SecondTerm;

        if not cancel then
        repeat
        begin {6}
        found := FALSE;

        replacementForm := gammaForm; {replacing alpha by gamma}

        occurences := firstline.fFormula.NumofFreeOccurences(alphaForm);

                                         {***}

        copyf := firstline.fFormula.CopyFormula;

        firstformula := firstline.fFormula;

        if occurences > 0 then
        begin {7}
        i := 1;

        while (i <= occurences) and not cancel do
        begin {8}
        currentNode := nil;
        currentCopyNode := nil;

        metSoFar := 0;

        done := FALSE;

        NewInsertMarker(alphaForm, i, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode);

        firstline.fFormula := firstformula;
        if subsinfirst then
        fTextList.InvalidateItem(fTextList.FirstSelectedItem)
        else
        fTextList.InvalidateItem(fTextList.LastSelectedItem);

        prompt := 'Do you wish to replace the arrowed occurrence?';


        if not GetTheChoice('Yes?', 'No?', prompt) then
        cancel := TRUE;
        RemoveMarker(fRadio);
        i := i + 1;

        end; {8}

        end; {7}


        firstline.fFormula := firstformula;

        if subsinfirst then
        fTextList.InvalidateItem(fTextList.FirstSelectedItem)
        else
        fTextList.InvalidateItem(fTextList.LastSelectedItem);

        if cancel then
        copyf.DismantleFormula
        else
        begin {9}

        found := FALSE;

        replacementForm := alphaForm; {replacing gamma by alpha}

        occurences := firstline.fFormula.NumofFreeOccurences(gammaForm);

                                         {***}

        firstformula := firstline.fFormula;

        if occurences > 0 then
        begin {10}
        i := 1;

        while (i <= occurences) and not cancel do
        begin {11}
        currentNode := nil;
        currentCopyNode := nil;

        metSoFar := 0;

        done := FALSE;

        NewInsertMarker(gammaForm, i, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode);

        firstline.fFormula := firstformula;
        if subsinfirst then
        fTextList.InvalidateItem(fTextList.FirstSelectedItem)
        else
        fTextList.InvalidateItem(fTextList.LastSelectedItem);

        prompt := 'Do you wish to replace the arrowed occurrence?';


        if not GetTheChoice('Yes?', 'No?', prompt) then
        cancel := TRUE;
        RemoveMarker(fRadio);
        i := i + 1;

        end; {11}
        end; {10}

        found := TRUE;

        end; {9}
        end;{6}

        until found or cancel;

        firstline.fFormula := firstformula;

        if subsinfirst then
        fTextList.InvalidateItem(fTextList.FirstSelectedItem)
        else
        fTextList.InvalidateItem(fTextList.LastSelectedItem);


        if cancel then
        else if not EqualFormulas(firstline.fFormula, copyf) then
        begin


        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);

                                 {newline points to new proofline}
        with newline do
        begin {12}
        fFormula := copyf;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        fjustification := ' IE';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end; {12}

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoIE := aLineCommand;
        end;
        end;
       end;

  end;



 (**************Tactics***********************)


 {$S  Copi2}
  function CopiSupplyFormulaLine (formula: TFormula; prooflevel, firstjustno, secondjustno, thirdjustno: integer; justification: str255): TCopiProofLine;
 {copies formula}

   var
    newline: TCopiProofLine;

  begin
   SupplyCopiProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fSubprooflevel := prooflevel; {checkthis}
     fFormula := formula.CopyFormula;
     ffirstjustno := firstjustno;
     fsecondjustno := secondjustno;
     fthirdjustno := thirdjustno;
     fjustification := justification;
    end;

   CopiSupplyFormulaLine := newline;
   newline := nil;
  end;


 {$S  Copi2}
  procedure TCopiProofWindow.CopiAddIfNotThere (whichone: TFormula; prooflevel: integer; var itslineno: integer; var firstnewline, secondnewline: TCopiProofline);

 {returns lineno if the formula is in proof and selectable, else supplies a question mark then the formula}
 {and returns 0 as its line no }

   var
    formulanode: TFormula;

  begin
   itslineno := FindFormula(whichone);

   if itslineno = 0 then
    begin
     SupplyFormula(formulanode);
     formulanode.fInfo := '?';
     formulanode.fKind := predicator;

     SupplyCopiProofline(firstnewline);
     with firstnewline do
      begin
       fFormula := formulanode;
       fJustification := '?';
       fselectable := false;
       fSubProofLevel := prooflevel;
      end;

     SupplyCopiProofline(secondnewline);
     with secondnewline do
      begin
       fFormula := whichone.CopyFormula;
       fJustification := '?';
       fSubProofLevel := prooflevel;
      end;
    end;
  end;


 {$S  Copi2}
  function TCopiProofWindow.DoHintAdd: TCommand;
   var
    leftdisj, rightdisj, conclusion: TFormula;
    leftdisjlineno, rightdisjlineno, disjlineno, Consequent, level: integer;
    firstnewline, secondnewline: TCopiProofline;
    error, proofover: Boolean;
    aLineCommand: TCopiLineCommand;

  begin
   DoHintAdd := gNoChanges;

   error := false;
   proofover := false;
   leftdisjlineno := 0;
   rightdisjlineno := 0;

   conclusion := SELF.FindTailFormula;
   if (conclusion = nil) then
    error := true
   else if (conclusion.fInfo <> chOr) then
    error := true;



   if error then
    BugAlert('With the tactic for Add., the conclusion must be a disjunction.')
   else
    begin

     leftdisj := conclusion.fLlink;
     rightdisj := conclusion.fRlink;

     New(aLineCommand);
     FailNIL(aLineCommand);
     aLineCommand.ICopiLineCommand(cAddLine, SELF);

     level := TProofline(SELF.fHead.Last).fSubProofLevel;
     disjlineno := TProofline(fHead.Last).flineno + 1;


     CopiAddIfNotThere(leftdisj, level, leftdisjlineno, firstnewline, secondnewline);


     if leftdisjlineno = 0 then {not already there}
      begin
       aLineCommand.fNewlines.InsertLast(firstnewline);
       aLineCommand.fNewlines.InsertLast(secondnewline);
       leftdisjlineno := disjlineno + 1;
       disjlineno := disjlineno + 2;
      end
     else
      proofover := true;

     if not proofover then
      begin

       CopiAddIfNotThere(rightdisj, level, rightdisjlineno, firstnewline, secondnewline);


       if rightdisjlineno = 0 then {not already there}
        begin
        aLineCommand.fNewlines.InsertLast(firstnewline);
        aLineCommand.fNewlines.InsertLast(secondnewline);
        rightdisjlineno := disjlineno + 1;
        disjlineno := disjlineno + 2;
        end
       else
        begin
        proofover := true;
        aLineCommand.fNewlines.Delete(firstnewline);  (*these are ones from leftdisj*)
        aLineCommand.fNewlines.Delete(secondnewline);
        leftdisjlineno := 0;
        end;

      end;

     if proofover then
      begin
       if leftdisjlineno = 0 then
        leftdisjlineno := rightdisjlineno;

       aLineCommand.fNewlines.InsertLast(CopiSupplyFormulaLine(conclusion, level, leftdisjlineno, 0, 0, ' �I'));
      end;

     DoHintAdd := aLineCommand;

    end;
  end;


 {$S  Copi2}
  function TCopiProofWindow.DoHintEG: TCommand;
   var
    variForm, termForm, scope, conclusion: TFormula;
    scopelineno, genlineno, level: integer;
    firstnewline, secondnewline: TCopiProofline;
    error, cancel, proofover: Boolean;
    aLineCommand: TCopiLineCommand;
    prompt, outPutStr: str255;

  begin
   DoHintEG := gNoChanges;

   cancel := false;
   error := false;
   proofover := false;
   scopelineno := 0;


   conclusion := SELF.FindTailFormula;
   if (conclusion = nil) then
    error := true
   else if (conclusion.fInfo <> chExiquant) then
    error := true;



   if error then
    BugAlert('With the tactic for EG, the conclusion must be an existential.')
   else
    begin

     variForm := conclusion.QuantVarForm;
     scope := conclusion.Scope.CopyFormula;

     if not scope.Freetest(variForm) then
      begin
       fParser.WriteFormulaToString(scope, outPutStr);

       BugAlert(concat(StrOfChar(conclusion.QuantVar), ' has to occur free in ', outputStr));

       scope.DismantleFormula;
      end
     else
      begin

 (**)
       prompt := 'Term that was generalized on?';

       GetTheTerm(strNull, strNull, prompt, termForm, cancel);

       if not cancel then
        begin

        if not scope.FreeForTest(termForm, variForm) then{}
 {}
        begin
        fParser.WriteFormulaToString(scope, outPutStr);


        outputStr := concat(' in ', outputStr, '.');

        fParser.WriteTermToString(termForm, prompt);

        BugAlert(concat(prompt, ' for ', StrOfChar(conclusion.QuantVar), outputStr, ' leads to capture.'));


        scope.DismantleFormula;
        end
        else
        begin
        NewSubTermVar(scope, termForm, variForm);
        termForm.DismantleFormula;
 (**)



        New(aLineCommand);
        FailNIL(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        level := TProofline(SELF.fHead.Last).fSubProofLevel;
        genlineno := TProofline(fHead.Last).flineno + 1;


        CopiAddIfNotThere(scope, level, scopelineno, firstnewline, secondnewline);

        scope.DismantleFormula; (*AddIfNot there makes its own copy*)


        if scopelineno = 0 then {not already there}
        begin
        aLineCommand.fNewlines.InsertLast(firstnewline);
        aLineCommand.fNewlines.InsertLast(secondnewline);
        scopelineno := genlineno + 1;
        genlineno := genlineno + 2;
        end
        else
        proofover := true;


        if proofover then
        aLineCommand.fNewlines.InsertLast(CopiSupplyFormulaLine(conclusion, level, scopelineno, 0, 0, ' EG'));

        DoHintEG := aLineCommand;

        end;
        end;
      end;
    end;
  end;

 {$S  Copi2}
  function TCopiProofWindow.DoHintUG: TCommand;
   var
    variForm, scope, conclusion, freeFormula, muForm, freeVarForm: TFormula;
    scopelineno, genlineno, level: integer;
    firstnewline, secondnewline: TCopiProofline;
    error, found, cancel: Boolean;
    aLineCommand: TCopiLineCommand;
    outPutStr, prompt: str255;
    mu, freeVar: char;
    faultyline: TObject;

   function InCP (item: TObject): boolean;  (*occurs free in assumption of CP or other*)

    var
     found: boolean;

   begin
    found := FALSE;
    if not TCopiProofline(item).fBlankline then
     if TCopiProofline(item).fSelectable then
      if TCopiProofline(item).fJustification = strNull then
       found := TCopiProofline(item).fFormula.Freetest(muForm);

    InCP := found;
   end;

 {this needs a bit of abstraction}
   function EIFreeVar (firstline: TProofline; var freevar: char): boolean;

    procedure Test (item: TObject);
     var
      aProofline: TCopiProofline;
    begin
     if not found then
      begin
       aProofline := TCopiProofline(item);
       if aProofline.fLineno <= firstline.fLineno then
        if not aProofline.fBlankline then
        if length(aProofline.fjustification) > 4 then
        if (aProofline.fjustification[2] = 'E') and (aProofline.fjustification[3] = 'I') then
        begin
        freevar := aProofline.fjustification[5];

        freeVarForm.fInfo := freevar;

        found := firstline.fFormula.Freetest(freevarForm);
        end;
      end;
    end;

   begin
    found := FALSE;
    SupplyFormula(freeVarForm);
    freeVarForm.fKind := variable;
    fHead.Each(Test);
    freeVarForm.DismantleFormula;
    EIFreeVar := found;
   end;

  begin
   DoHintUG := gNoChanges;

   prompt := strNull;
   outPutStr := strNull;
   error := false;
   found := false;
   cancel := false;

   conclusion := SELF.FindTailFormula;

   if (conclusion = nil) then
    error := true
   else if (conclusion.fInfo <> chUniquant) then
    error := true;



   if error then
    BugAlert('With the tactic for UG, the conclusion must be a generalization.')
   else
    begin

     variForm := conclusion.QuantVarForm;
     scope := conclusion.Scope.CopyFormula;

     if not scope.Freetest(variForm) then
      begin
       fParser.WriteFormulaToString(scope, outPutStr);

       BugAlert(concat(StrOfChar(conclusion.QuantVar), ' has to occur free in ', outputStr));

       scope.DismantleFormula;
      end
     else
      begin

       repeat
                         {GetIndString(prompt, kStringRSRCID, 19); Term}
        prompt := 'Variable that was generalized on?';

        gInputStr := concat(outPutStr, prompt);

        if not GetTheChoice(strNull, strNull, gInputStr) then
        cancel := TRUE
        else if length(gInputStr) > 0 then
        begin
        GetStringInput;
        skip(1, logicfilter);  (*primes gCurrch, and gLookaheadCh*)
                              { ReadTerm; }
        if gCurrCh in gCopiVariables then
        begin
        found := TRUE;
        mu := gCurrCh;
                                    {beta := gCurrCh;}
        end
        else
        outPutStr := concat(gCurrCh, ' is not a variable.');
        end;

       until found or cancel;

       found := FALSE;
       outPutStr := strNull;

       if not cancel then
        begin
        supplyFormula(muForm);
        muForm.fKind := variable;
        muForm.fInfo := mu;

        NewSubTermVar(scope, muForm, variForm);  (*gives former scope*)


        if EIFreeVar(TProofline(fHead.Last), freeVar) then
        begin
        BugAlert(concat(freeVar, ' was introduced by EI.'));
        end
        else
        begin

        faultyline := fHead.FirstThat(InCP);
        if faultyline <> nil then
        begin
        fParser.WriteFormulaToString(TCopiProofline(faultyline).fFormula, outPutStr);
        BugAlert(concat(mu, ' occurs freely in assumption ', outPutStr, ' .'));
        muForm.DismantleFormula;
        cancel := TRUE;
        end;


        if not cancel then
        begin

        New(aLineCommand);
        FailNIL(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        level := TProofline(SELF.fHead.Last).fSubProofLevel;
        genlineno := TProofline(fHead.Last).flineno + 1;


        CopiAddIfNotThere(scope, level, scopelineno, firstnewline, secondnewline);


        if scopelineno = 0 then {not already there}
        begin
        aLineCommand.fNewlines.InsertLast(firstnewline);
        aLineCommand.fNewlines.InsertLast(secondnewline);
        scopelineno := genlineno + 1;
        genlineno := genlineno + 2;
        end;


        aLineCommand.fNewlines.InsertLast(CopiSupplyFormulaLine(conclusion, level, scopelineno, 0, 0, ' UG'));

        DoHintUG := aLineCommand;

        end;
        end;
        end;
      end;
    end;
  end;



 {$S  Copi2}
  function TCopiProofWindow.DoHintConj: TCommand;
   var
    leftconj, rightconj, conclusion: TFormula;
    leftconjlineno, rightconjlineno, conjlineno, Consequent, level: integer;
    firstnewline, secondnewline: TCopiProofline;
    error: Boolean;
    aLineCommand: TCopiLineCommand;

  begin
   DoHintConj := gNoChanges;

   error := false;
   conclusion := SELF.FindTailFormula;
   if (conclusion = nil) then
    error := true
   else if (conclusion.fInfo <> chAnd) then
    error := true;



   if error then
    BugAlert('With the tactic for Conj., the conclusion must be a conjunction.')
   else
    begin

     leftconj := conclusion.fLlink;
     rightconj := conclusion.fRlink;

     New(aLineCommand);
     FailNIL(aLineCommand);
     aLineCommand.ICopiLineCommand(cAddLine, SELF);

     level := TProofline(SELF.fHead.Last).fSubProofLevel;
     conjlineno := TProofline(fHead.Last).flineno + 1;


     CopiAddIfNotThere(leftconj, level, leftconjlineno, firstnewline, secondnewline);


     if leftconjlineno = 0 then {not already there}
      begin
       aLineCommand.fNewlines.InsertLast(firstnewline);
       aLineCommand.fNewlines.InsertLast(secondnewline);
       leftconjlineno := conjlineno + 1;
       conjlineno := conjlineno + 2;
      end;

     CopiAddIfNotThere(rightconj, level, rightconjlineno, firstnewline, secondnewline);


     if rightconjlineno = 0 then {not already there}
      begin
       aLineCommand.fNewlines.InsertLast(firstnewline);
       aLineCommand.fNewlines.InsertLast(secondnewline);
       rightconjlineno := conjlineno + 1;
       conjlineno := conjlineno + 2;
      end;

     aLineCommand.fNewlines.InsertLast(CopiSupplyFormulaLine(conclusion, level, leftconjlineno, rightconjlineno, 0, ' Conj.'));

     DoHintConj := aLineCommand;

    end;
  end;

         {*********CONDITIONAL Template*********************}

 {$S  Copi2}
  function TCopiProofWindow.DoHintCP: TCommand;

   var
    Anteroot, Conseroot, formulanode, conclusion: TFormula;
    level, lineno: integer;
    newline: TCopiProofline;
    cancel, error: Boolean;
    prompt: str255;
    aLineCommand: TCopiLineCommand;

  begin
   DoHintCP := gNoChanges;

   if TCopiProofline(fHead.Last).fSubprooflevel > kCopiMaxnesting then
    begin
     GetIndString(prompt, kStringRSRCID, 1);
     BugAlert(prompt); {'phew... no more please.')}
    end
   else
    begin
     error := false;
     conclusion := SELF.FindTailFormula;
     if (conclusion = nil) then
      error := true
     else if (conclusion.fInfo <> chImplic) then
      error := true;



     if error then
      BugAlert('With the tactic for CP, the conclusion must be an implication.')
     else
      begin

       anteroot := conclusion.fLlink.CopyFormula;
       conseroot := conclusion.fRlink.CopyFormula;


       prompt := strNull;


       New(aLineCommand);
       FailNIL(aLineCommand);
       aLineCommand.ICopiLineCommand(cAddLine, SELF);

       level := TProofline(SELF.fHead.Last).fSubProofLevel;
       lineno := TProofline(SELF.fHead.Last).fLineno;


       SupplyCopiProofline(newline); {newline points to new proofline}

       with newline do
        begin
        fSubprooflevel := level + 1;
        fFormula := anteroot;
        fjustification := strNull;
        fLastassumption := TRUE;
        end;

       aLineCommand.fNewlines.InsertLast(newline);

       SupplyFormula(formulanode); {creates implication }
       with formulanode do
        begin
        fInfo := '?';
        fKind := predicator;
        end;

       SupplyCopiProofline(newline); {newline points to new proofline}

       with newline do
        begin
        fSubprooflevel := level + 1;
        fFormula := formulanode;
        fjustification := '?';
        end;

       aLineCommand.fNewlines.InsertLast(newline);  (**)

       SupplyCopiProofline(newline); {newline points to new proofline}

       with newline do
        begin
        fSubprooflevel := level + 1;
        fFormula := conseroot;
        fjustification := '?';
        end;

       aLineCommand.fNewlines.InsertLast(newline);

       SupplyCopiProofline(newline); {newline points to new proofline}

       with newline do
        begin
        fSubprooflevel := level; {checkthis}
        fBlankline := TRUE;
        fjustification := '';
        fSelectable := FALSE;
        end;

       aLineCommand.fNewlines.InsertLast(newline);

       newline := nil;

       SupplyFormula(formulanode); {creates implication }
       with formulanode do
        begin
        fKind := binary;
        fInfo := chImplic;
        fLlink := anteroot.CopyFormula;
        fRlink := conseroot.CopyFormula;
        end;

       SupplyCopiProofline(newline); {newline points to new proofline}

       with newline do
        begin
        fSubprooflevel := level; {checkthis}
        fFormula := formulanode;
        ffirstjustno := lineno + 1;
        fsecondjustno := lineno + 3;
        fjustification := ' CP';
        end;  (*This is special, it has to read 3-4 CP, do this in draw method*)

       aLineCommand.fNewlines.InsertLast(newline);

       DoHintCP := aLineCommand;

      end;
    end;
  end;


 {$S Copi2}
  function TCopiProofWindow.DoTheorem: TCommand;

   var
    newline: TCopiProofline;
    aCopiLineCommand: TCopiLineCommand;
    prompt: str255;
    root: TFormula;
    cancel: boolean;

  begin
   DoTheorem := gNoChanges;

   prompt := 'Theorem?';
         {GetIndString(prompt, kStringRSRCID, 2);  prompt := 'New Antecedent?';}

   GetTheRoot(strNull, strNull, prompt, root, cancel);
   if not cancel then
    begin
     New(aCopiLineCommand);
     FailNil(aCopiLineCommand);
     aCopiLineCommand.ICopiLineCommand(cAddLine, SELF);

     SupplyCopiProofline(newline);
     with newline do
      begin
       fFormula := root;
       fJustification := 'Theorem';
       fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
      end;

     aCopiLineCommand.fNewlines.InsertLast(newline);
     newline := nil;

     DoTheorem := aCopiLineCommand;
    end;
  end;

 {$S  Copi2}
  function TCopiProofWindow.DoReplace: TCommand;

   var
    oldStr, newStr, aString: str255;
    entryView: TEditText;
    exitView: TStaticText;
    itsroot: TFormula;

    firstline: TProofline;
    newline: TCopiProofline;
    aLineCommand: TCopiLineCommand;
    newValuation: TList;

  begin
   DoReplace := gNoChanges;

   if (fTextList.TotalSelected = 1) then
    if fTextList.OneSelected(firstline) then

     begin

      entryView := TEditText(fRewrite.FindSubView('VW03'));
      entryView.GetText(oldStr);

      exitView := TStaticText(fRewrite.FindSubView('VW05'));
      exitView.GetText(newStr);

 (*this is a hack, the problem is that they might edit the initial formula*)

      fParser.WriteFormulaToString(firstline.fFormula, aString);

      if aString <> oldStr then
       BugAlert('You have altered the old formula, so the rewrite is unacceptable.')
      else
       begin


        if oldStr <> newStr then
        begin

        gIllformed := FALSE;
        gInputStr := newStr;
        GetStringInput;
        skip(1, logicfilter);  (*primes gCurrch, and gLookaheadCh*)
        fParser.wffcheck(itsroot, newValuation, gIllformed);
        if gIllformed then
        begin
        sysBeep(5); {check}
        end
        else
        begin

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := itsroot;
        ffirstjustno := firstline.fLineno;
        fjustification := fRewrite.fLastRewrite;
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoReplace := aLineCommand;

        end;
        end;
       end;
     end;
  end;

 {$IFC THINK_PASCAL}

 end.
{$ENDC}

 */


/**************************  OLD CODE NO LONGER USED **************************/

/*

void doConj(){    //doAndI
  if (fTemplate)
    doHintAndI();
  else {

    TProofline firstLine = fProofListView.oneSelected();

    if (firstLine != null) {
      oneSelectionAnd(firstLine);
      return;
    }

    //Trying two selection And

    TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

    if (selections != null)
       {


         JButton leftButton;
         JButton rightButton;
         TProofInputPanel inputPane;

         JTextField text = new JTextField(
             "Choose where you wish the first selected line to appear.");
         text.selectAll();

         /*
          if (TProofline(fHead.Last).fSubprooflevel > TProofline(fHead.Last).fHeadlevel) then {This can be 0 or -1}
          {                                                                           depending on whether}
          {                                                                           there are premises}



         TProofline lastLine = fModel.getHeadLastLine();

         /*  if (lastLine.fSubprooflevel>lastLine.fHeadlevel)
         {

           leftButton = new JButton(new AndIOnLeftAction(text, "On Left",
               selections));
           rightButton = new JButton(new AndIOnRightAction(text, "On Right",
               selections));

           JButton[] buttons = {
               new JButton(new CancelAction()), leftButton,
               rightButton}; // put cancel on left
           inputPane = new TProofInputPanel("Doing Conj",
                                            text, buttons);
         }

         addInputPane(inputPane);

         fInputPane.setVisible(true); // need this
         text.requestFocus(); // so selected text shows
       }
  }
 }

 void oneSelectionAnd(TProofline firstLine){

      TProofline newline = supplyProofline();

      TFormula formulanode = new TFormula();

      formulanode.fKind = TFormula.binary;
      formulanode.fInfo = String.valueOf(chAnd);
      formulanode.fLLink = firstLine.fFormula.copyFormula();
      formulanode.fRLink = firstLine.fFormula.copyFormula();


      newline.fFormula = formulanode;
      newline.fFirstjustno = firstLine.fLineno;
      newline.fSecondjustno = firstLine.fLineno;
      newline.fJustification = andIJustification;//" ^I";
      newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

      TUndoableProofEdit newEdit = new TUndoableProofEdit();
      newEdit.fNewLines.add(newline);
      newEdit.doEdit();

}



/*

  function TCopiProofWindow.DoConj: TCommand;

   var

    newline: TCopiProofline;
    firstline, secondline: TProofline;  {check}
    aLineCommand: TCopiLineCommand;
    formula1, formulanode: TFormula;
    rad1, rad2, prompt: str255;

  begin

   DoConj := gNoChanges;

   if fTemplate then {gTemplate}
    DoConj := DoHintConj
   else
    begin

     if (fTextList.TotalSelected = 2) then
      if fTextList.TwoSelected(firstline, secondline) then
       begin
        GetIndString(prompt, kStringRSRCID, 5); { Choose on buttons where you want the}
 {                                                             first selected line to go.';}
        GetIndString(rad1, kStringRSRCID, 6); { ''On left?';}
        GetIndString(rad2, kStringRSRCID, 7); { 'On right?'}

        if GetTheChoice(rad1, rad2, prompt) then {makes them choose left or right}
        begin

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        SupplyFormula(formulanode);
        with formulanode do
        begin
        fKind := binary; {formulanode is the new formula node}
        fInfo := chAnd;
        if fRadio then
        begin
        fLlink := firstline.fFormula.CopyFormula;
        fRlink := secondline.fFormula.CopyFormula;
        end
        else
        begin
        fLlink := secondline.fFormula.CopyFormula;
        fRlink := firstline.fFormula.CopyFormula;
        end
        end;

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        if fRadio then
        begin
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;
        end
        else
        begin
        ffirstjustno := secondline.fLineno;
        fsecondjustno := firstline.fLineno;
        end;
        fjustification := concat(chBlank, 'Conj.');
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoConj := aLineCommand;

        end;
       end;

     if fTextList.TotalSelected = 1 then
      if fTextList.OneSelected(firstline) then
       begin
        SupplyFormula(formulanode);
        with formulanode do
        begin
        fKind := binary; {formulanode is the new formula node}
        fInfo := chAnd;
        fLlink := firstline.fFormula.CopyFormula;
        fRlink := firstline.fFormula.CopyFormula;
        end;

        SupplyCopiProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := firstline.fLineno;
        fjustification := ' Conj';
        fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ICopiLineCommand(cAddLine, SELF);

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoConj := aLineCommand;

       end;
    end;
  end;

 void conjMenuItem_actionPerformed(ActionEvent e) {
             doConj();
    }

 class TCopiProofPanel_conjMenuItem_actionAdapter implements java.awt.event.ActionListener {
               TCopiProofPanel adaptee;

               TCopiProofPanel_conjMenuItem_actionAdapter(TCopiProofPanel adaptee) {
                 this.adaptee = adaptee;
               }
               public void actionPerformed(ActionEvent e) {
                 adaptee.conjMenuItem_actionPerformed(e);
               }
     }




 /****************   Rule of Conj (similar to AndI) ********************

 public class AndIOnLeftAction extends AbstractAction{
   JTextComponent fText;
   TProofline[] fSelections;

    public AndIOnLeftAction(JTextComponent text, String label,TProofline[] selections){
      putValue(NAME, label);

      fText=text;

      fSelections=selections;
    }

     public void actionPerformed(ActionEvent ae){

       TProofline newline = supplyProofline();

 TFormula formulanode = new TFormula();

 formulanode.fKind = TFormula.binary;
 formulanode.fInfo = String.valueOf(chAnd);
 formulanode.fLLink = fSelections[0].fFormula.copyFormula();
 formulanode.fRLink = fSelections[1].fFormula.copyFormula();


 newline.fFormula = formulanode;
 newline.fFirstjustno = fSelections[0].fLineno;
 newline.fSecondjustno = fSelections[1].fLineno;
 newline.fJustification = conjJustification;
 newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

 TUndoableProofEdit newEdit = new TUndoableProofEdit();
 newEdit.fNewLines.add(newline);
 newEdit.doEdit();

 removeInputPane();
   }

 }

 public class AndIOnRightAction extends AbstractAction{
       JTextComponent fText;
       TProofline[] fSelections;

        public AndIOnRightAction(JTextComponent text, String label,TProofline[] selections){
          putValue(NAME, label);

          fText=text;

          fSelections=selections;
        }

         public void actionPerformed(ActionEvent ae){

           TProofline newline = supplyProofline();

   TFormula formulanode = new TFormula();

   formulanode.fKind = TFormula.binary;
   formulanode.fInfo = String.valueOf(chAnd);
   formulanode.fLLink = fSelections[1].fFormula.copyFormula();
   formulanode.fRLink = fSelections[0].fFormula.copyFormula();


   newline.fFormula = formulanode;
   newline.fFirstjustno = fSelections[1].fLineno;
   newline.fSecondjustno = fSelections[0].fLineno;
   newline.fJustification = conjJustification;
   newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

   TUndoableProofEdit newEdit = new TUndoableProofEdit();
   newEdit.fNewLines.add(newline);
   newEdit.doEdit();

 removeInputPane();
       }
     }


 void addMenuItem_actionPerformed(ActionEvent e) {
                 doAdd();
    }

    class TCopiProofPanel_addMenuItem_actionAdapter implements java.awt.event.ActionListener {
                  TCopiProofPanel adaptee;

                  TCopiProofPanel_addMenuItem_actionAdapter(TCopiProofPanel adaptee) {
                    this.adaptee = adaptee;
                  }
                  public void actionPerformed(ActionEvent e) {
                    adaptee.addMenuItem_actionPerformed(e);
                  }
        }



 /************************  doAdd *************************************************

 public class AddAction extends AbstractAction{
    JTextComponent fText;
    TProofline fSelection;
    boolean fLeft;

     public AddAction(JTextComponent text, String label,TProofline selection, boolean left){
       putValue(NAME, label);

       fText=text;

       fSelection=selection;

       fLeft=left;
     }

      public void actionPerformed(ActionEvent ae){
     boolean useFilter =true;
     ArrayList dummy = new ArrayList();

     String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);
String aString= TUtilities.readTextToString(fText, TUtilities.defaultFilter);

     TFormula root = new TFormula();
     StringReader aReader = new StringReader(aString);
     boolean wellformed;

     wffCheck(root, /*dummy, aReader);

     if (!wellformed){
         String message = "The string is illformed."+
                           (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns



         fText.setText(message);
         fText.selectAll();
         fText.requestFocus();

          }
    else {
     TProofline newline = supplyProofline();


     TFormula formulanode = new TFormula();

     formulanode.fKind = TFormula.binary;
     formulanode.fInfo = String.valueOf(chOr);
     if (fLeft){
        formulanode.fLLink = root;
        formulanode.fRLink = fSelection.fFormula.copyFormula();
      }
      else{
         formulanode.fLLink = fSelection.fFormula.copyFormula();
         formulanode.fRLink = root;
       }


     newline.fFormula=formulanode;
     newline.fFirstjustno = fSelection.fLineno;
     newline.fJustification= addJustification;
     newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;

     TUndoableProofEdit  newEdit = new TUndoableProofEdit();
     newEdit.fNewLines.add(newline);
     newEdit.doEdit();

     removeInputPane();};


    }

   }





 void doAdd(){
    JButton onRightButton;
//     JButton onLeftButton;
      TProofInputPanel inputPane;



    if (fTemplate)
      doHintvI();
    else{
      TProofline firstLine=fProofListView.oneSelected();

      if (firstLine!=null){

        JTextField text = new JTextField("New formula?");

        text.selectAll();


         boolean left=true;


         onRightButton = new JButton(new AddAction(text,"Go",firstLine, !left));
//        onLeftButton = new JButton(new OrIAction(text,"On Left", firstLine,left));

           JButton[]buttons = {
               new JButton(new CancelAction()),onRightButton };  // put cancel on left
           inputPane = new TProofInputPanel("Doing Add.",
              text, buttons);



           addInputPane(inputPane);

          // inputPane.getRootPane().setDefaultButton(defaultButton);
           fInputPane.setVisible(true); // need this
           text.requestFocus();         // so selected text shows
         }

      }

    }


 /*

   function TCopiProofWindow.DoAdd: TCommand;

    var

     newline: TCopiProofline;
     firstline: TProofline;  {check}
     aLineCommand: TCopiLineCommand;
     root, formulanode: TFormula;
     cancel: boolean;
     rad1, rad2, prompt: str255;

   begin

    DoAdd := gNoChanges;

    if fTemplate then {gTemplate}
     DoAdd := DoHintAdd
    else
     begin

      if (fTextList.TotalSelected = 1) then
       if fTextList.OneSelected(firstline) then
        begin
                      {  GetIndString(prompt, kStringRSRCID, 11);  Choose on buttons }
                      {  GetIndString(rad1, kStringRSRCID, 12);  ''On left?';}
                      { GetIndString(rad2, kStringRSRCID, 13);  'On right?'}

         rad1 := strNull;
         rad2 := strNull;
         prompt := 'New Formula?';

         GetTheRoot(rad1, rad2, prompt, root, cancel);
         if not cancel then
         begin

         SupplyFormula(formulanode);
         formulanode.fKind := binary;  {formulanode is the new formula node}
         formulanode.fInfo := chOr;
         formulanode.fLlink := firstline.fFormula.CopyFormula;
         formulanode.fRlink := root;

         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ICopiLineCommand(cAddLine, SELF);

         SupplyCopiProofline(newline);
         with newline do
         begin
         fFormula := formulanode;
         ffirstjustno := firstline.fLineno;
         fjustification := ' Add.';
         fSubprooflevel := TCopiProofline(SELF.fHead.Last).fSubprooflevel;
         end;

         aLineCommand.fNewlines.InsertLast(newline);
         newline := nil;
         DoAdd := aLineCommand;

         end;
        end;
     end;
   end;

 */

/****************************************************************************************




*/



