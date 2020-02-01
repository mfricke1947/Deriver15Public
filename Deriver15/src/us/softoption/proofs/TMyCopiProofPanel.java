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

import static us.softoption.infrastructure.Symbols.chLeftCurlyBracket;
import static us.softoption.infrastructure.Symbols.chQuestionMark;
import static us.softoption.infrastructure.Symbols.chRightCurlyBracket;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.strNull;

import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;

import us.softoption.editor.TDeriverDocument;
import us.softoption.infrastructure.FunctionalParameter;
import us.softoption.infrastructure.TConstants;
import us.softoption.interpretation.TTestDisplayTree;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

public class TMyCopiProofPanel extends TCopiProofPanel{

  static final char chBlank = ' ';
static final char chComma = ',';
static final char chLSqBracket = '[';
static final char chRSqBracket = ']';
public static final char chTherefore='\u2234';

  /* This almost always wants to have its Deriver document because the undoable proof
edits set the fDirty field of the document */

/*public TMyCopiProofPanel(){

} */


public TMyCopiProofPanel(TDeriverDocument itsDeriverDocument){
  super(itsDeriverDocument);
}


/*******************  Factory *************************/

TReAssemble supplyTReAssemble (TTestNode root){
  return
      new TCopiReAssemble(fParser, root, null, 0);
}


/******************************************************/


public void startProof(String inputStr){


  dismantleProof(); //{previous one}

  initProof();

  if (load(inputStr))
    startUp();

  }


  /*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
        compound terms eg Pf(a,b),Hc.

     Also in some systems a relation Lxy is written L(x,y) ie also with commas


     We want to separate the premises but not the terms. So we will change the
     premise comma separators to another character. For the moment '!'*/


  private static char chSeparator='�';

  private String changeListSeparator(String input){

    int nested=0;
    char currCh;

    StringBuffer output= new StringBuffer(input);
    for (int i=0;i<input.length();i++){
      currCh=output.charAt(i);

      if ((currCh==chSmallLeftBracket)||
    	    	(currCh==chLeftCurlyBracket))
    	      nested++;
    	    if ((currCh==chSmallRightBracket)||
    	    	(currCh==chRightCurlyBracket))
    	      nested--;


      if ((nested<1)&&(currCh==chComma))    //commas separating the list of premises are not nested
        output.setCharAt(i,chSeparator);
    }

    return
        output.toString();
}





  public boolean load(String inputStr){                     // same as TMyProofPanel

          TParser parser =fDeriverDocument.getParser();
                      ArrayList dummy=new ArrayList();
         boolean wellformed = true;

         fProofStr="";  //re-initialize; the old proof may still be there and if this turns out to be illformed will stay there

      //   StringSplitData data;

         if ((inputStr==null)||inputStr==strNull){

           createBlankStart();
           return
               wellformed;
         }

  String[]premisesAndConclusion = inputStr.split(String.valueOf(chTherefore),2);  /* they may input two
          therefore symbols, in which case we'll split at the first and let the parser report the second*/

     if (premisesAndConclusion[0]!=null&&!strNull.equals(premisesAndConclusion[0])){  // there are premises

       premisesAndConclusion[0]=changeListSeparator(premisesAndConclusion[0]);

       StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chSeparator)/*String.valueOf(chComma)*/);



   //    StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chComma));

    while ((premises.hasMoreTokens())&&wellformed){
       inputStr=premises.nextToken();

       if (inputStr!=null&&!inputStr.equals(strNull)){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
              TFormula root = new TFormula();
              StringReader aReader = new StringReader(inputStr);


              wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

              if (!wellformed)
                fDeriverDocument.writeToJournal(parser.fCurrCh + TConstants.fErrors12 + parser.fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
              else
                  {addPremise(root);
                  if (fProofStr.length()==0)
                    fProofStr=inputStr;
                  else
                    fProofStr+=chComma+inputStr;
                  }
              }
       }          // done with premises
     }
       if (premisesAndConclusion.length>1){  // if there is no therefore the original 'split' won't split the input
         inputStr = premisesAndConclusion[1];

         if (inputStr!=null&&!inputStr.equals(strNull)){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
              TFormula root = new TFormula();
              StringReader aReader = new StringReader(inputStr);

              wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

              if (!wellformed)
                fDeriverDocument.writeToJournal(parser.fCurrCh + TConstants.fErrors12 + parser.fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
              else
                  {addConclusion(root);
                  fProofStr+=chTherefore+inputStr;
                  }
              }

       }


        return
             wellformed;

     }



     void addConclusion(TFormula root){         //similar to TMyProofPanel
          if ((fProofType == premNoConc) || (fProofType == noPremNoConc))  // don't add a second one}
          {

             if (fProofType == noPremNoConc)
              createBlankStart();


             addTailLines(root);

             if (fProofType == premNoConc)
              fProofType = premConc;
             else
              fProofType = noPremConc;
          }

   }

   void addPremise(TFormula root){
   TProofline newline= supplyProofline();//new TCopiProofline();   //copi line
   newline.fFormula=root.copyFormula();
   newline.fJustification = fAssJustification;

   switch (fProofType){

     case noPremNoConc: {
       newline.fLineno=1;
       newline.fHeadlevel=0;

       fModel.insertFirst(newline);

       fProofType=premNoConc;
       break;
     }

     case premNoConc:
       fModel.insertAtPseudoTail(newline);
        break;

     default:  break;

   }

 }



   void addTailLines(TFormula root){
      TFormula newnode= new TFormula(TFormula.predicator,String.valueOf(chQuestionMark),null,null);
      TProofline newline= supplyProofline();

      newline.fFormula=newnode;
      newline.fJustification = "?";
      newline.fSelectable=false;

      fModel.insertAtTailFirst(newline);

      {TProofline secondline = supplyProofline();//new TCopiProofline();   // needs to be copi lines

        secondline.fFormula = root.copyFormula();
        secondline.fJustification = "?";
        secondline.fSelectable = false;

      fModel.insertAtTailLast(secondline);
      }

     }



void startUp(){

  //  fModel.setLastAssumption();

    collapseTrivialCase();

    fModel.placeInsertionMarker();

    this.setVisible(true);

    }


    class findConclusion implements FunctionalParameter {   //this should be more general, doesn't it just find a formula?
    boolean found=false;
    TFormula conclusion;
    int lineno=0;

    findConclusion(TFormula theConclusion){
    conclusion=theConclusion;
    }

  public void  execute(Object parameters){
    TProofline workingLine =(TProofline)parameters;

    if (!found){
      found=conclusion.equalFormulas(conclusion, workingLine.fFormula);
      lineno=workingLine.fLineno;
    }
  }

  public boolean testIt(Object parameter){
    return
        false;
  }
}







    void collapseTrivialCase(){


  if ((fProofType == premConc) || (fProofType == noPremConc)){
    TProofline conclusionLine = fModel.getTailLastLine();
    findConclusion finder = new findConclusion(conclusionLine.fFormula);

    fModel.doToEachInHead(finder);

    if (finder.found){
  //    System.out.print("found it");
      conclusionLine.fLineno=conclusionLine.fLineno-1;
      conclusionLine.fFirstjustno=finder.lineno;
      conclusionLine.fJustification= " R";
      conclusionLine.fSelectable=true;
      conclusionLine.fSubProofSelectable=false;

      fModel.addToHead(fModel.getHeadSize(),conclusionLine);  // last in head without resetting levels etc.

      fModel.clearTail();

      fProofType = pfFinished;
    }

  }

  }


  /*******************  deriving **************************/

void deriveItMenuItem_actionPerformed(ActionEvent e) {
boolean allLines=true;
doDerive(allLines);
  }

void nextLineMenuItem_actionPerformed(ActionEvent e) {
  boolean allLines=true;
doDerive(!allLines);

    }


ArrayList createProofSegment(TTestNode root){

    TReAssemble aReAssembly= supplyTReAssemble(root);

      aReAssembly.reAssembleProof();

      ArrayList tempHead=aReAssembly.fHead;
      int lastAssumption = aReAssembly.fLastAssIndex;



    improve(tempHead, lastAssumption);

     setHeadLevels(tempHead);

      // improve
      // set head levels
      //prepare segment for splice


    //  prepareSegmentForSplice(fModel.getHead(),tempHead);

      int dummy=0;

      TMergeData mergeData=new TMergeData(fModel.getHead(),dummy,
                                          tempHead,lastAssumption,dummy,dummy,
                                          aReAssembly.supplyProofline());

      mergeData.prepareSegmentForSplice();


      return
          tempHead;

  }



    void doDerive(boolean allLines){
     int outcome;

       if (fModel.getTailSize()==0){
         bugAlert("Exiting from Derive It. Warning.",
                  "We need a target conclusion to derive to.");
         return;
       }



      if (fModel.getTailLine(0).fSubprooflevel<fModel.getHeadLastLine().fSubprooflevel){
        bugAlert("Exiting from Derive It. Warning.",
                 "First please drop the extra assumptions.");
        return;
      }


      TTestNode aTestRoot=assembleTestNode();

      if (aTestRoot==null)
        return;


      TTreeModel aTreeModel= new TTreeModel(aTestRoot.fTreeNode);  //debug remove later


      aTestRoot.initializeContext(aTreeModel);  //debug Tree Model initialized now



      outcome=aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);

      if (TConstants.DEBUG){

        TTestDisplayTree aTestDisplayTree = new TTestDisplayTree(aTreeModel); //debug
        aTestDisplayTree.display(); //debug
      }


      // need to do the CV stuff

      switch (outcome){

         case TTestNode.valid:
           bugAlert("Derive It. Derivation found.",
                 "Please wait while it is re-assembled.");

        boolean debug=false;

        if (debug)
        {
          ArrayList debugHead= createProofSegment(aTestRoot.getLeftChild());
          insertAll(debugHead);
        }

          ArrayList tempHead= createProofSegment(aTestRoot);
          if (allLines)
             insertAll(tempHead);
          else
             insertFirstLine(tempHead);

           removeBugAlert();
           break;
         case TTestNode.notValid:
           bugAlert("Derive It. Warning.",
                 "Not derivable from these standing assumptions.");
           break;

         case TTestNode.notKnown:
           bugAlert("Derive It. Warning.",
                 "Unsure whether sequent can be derived.");
           break;


      }



}



  /************ Experiments with Rewrite ******************
    *
    *
    */




  public class RewriteAction extends AbstractAction{

    TRewriteRules fRules;
    int fLineno;

   RewriteAction(String label, TRewriteRules rules, int lineNo){
     putValue(NAME, label);

     fRules=rules;
     fLineno=lineNo;

   }

   public void actionPerformed(ActionEvent ae){
     if ((fRules.getNewRoot()!=null)&&
         (fRules.getSelectionRoot()!=null)&&
         !fRules.getNewRoot().equalFormulas(fRules.getNewRoot(),fRules.getSelectionRoot())
     )
         {

      // we need to find the entire after formula

      TFormula afterRoot = fRules.getAfterRoot();

           if (afterRoot!=null){ // should alwyas be

             TProofline newline = supplyProofline();//new TCopiProofline(fParser);

             newline.fFormula = afterRoot.copyFormula();
             newline.fFirstjustno = fLineno;
             newline.fJustification = fRules.getLastRewrite();
             newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

             TUndoableProofEdit newEdit = new TUndoableProofEdit();

             newEdit.fNewLines.add(newline);

             newEdit.doEdit();
             removeInputPane();
           }

    }


   }

 }


    public void doNewRewrite(){
    JButton defaultButton;

    TProofInputPanel inputPane;

   // fSelectionRewrite="";

    TProofline selectedLine=fProofListView.oneSelected();

    if (selectedLine!=null){

      String originalFormulaStr=fParser.writeFormulaToString(selectedLine.fFormula);

      TRewriteRules rules= new TRewriteRules(selectedLine.fFormula,fParser);


 /*     fBeforeText.setText(originalFormulaStr);
      fAfterText.setText("");
      fNewRoot=new TFormula();
      fSelectionRoot=new TFormula(); */

       defaultButton = new JButton(new RewriteAction("Go",rules,selectedLine.fLineno));


       JComponent[]components = {rules.getComboBox(),  new JButton(new CancelAction()), defaultButton };  // put cancel on left

       inputPane = new TProofInputPanel("Choose rule, select (sub)formula to rewrite, click Go...",
                                        rules.getBeforeText(),
                                        "After rewrite, the formula will look like this:",
                                        rules.getAfterText(),
                                        components);


            addInputPane(inputPane);

            inputPane.getRootPane().setDefaultButton(defaultButton);
            fInputPane.setVisible(true); // need this
            rules.getBeforeText().requestFocus();         // so selected text shows

     }

 }




 void rewriteMenuItem_actionPerformed(ActionEvent e) {

 doNewRewrite();

    }

}


/*


 {****MyCopiProof Window *******}

 procedure TMyCopiProofWindow.IMyCopiProofWindow (itsDeriverDocument: TCopiDocument);

 begin
  SELF.IProofWindow;

  fDeriverDocument := itsDeriverDocument;
  fFont := fDeriverDocument.fProofFont; {fields of subobject}
  fMargin := fDeriverDocument.fRightMargin;

 end;

{Menu commands}

 function TMyCopiProofWindow.DoMenuCommand (aCmdNumber: CmdNumber): TCommand;
  OVERRIDE;
  var
   doOpen: boolean;
   aWindow: TWindow;

 begin
  DoMenuCommand := gNoChanges;

  case aCmdNumber of

   cWriteToJournal:
    WriteProofToJournal;

   cTemplates:
    if (fTemplate = TRUE) then
     begin
      fTemplate := FALSE;
                       {  RemoveTemplates; check not any more}
     end
    else
     fTemplate := TRUE;

   cOpenProof:
    begin
     aWindow := fDeriverDocument.fProofWindow;
     doOpen := not aWindow.IsShown;
     aWindow.Show(doOpen, kRedraw);
     if doOpen then
      aWindow.Select
     else if TProofWindow(aWindow).fRewrite.IsShown then
      TProofWindow(aWindow).fRewrite.Show(doOpen, kRedraw);
    end;

   cOpenDraw:
    begin
     aWindow := fDeriverDocument.fDrawShapeView.GetWindow;
     doOpen := not aWindow.IsShown;
     aWindow.Show(doOpen, kRedraw);
     if doOpen then
      aWindow.Select;
    end;

   otherwise
    begin

     DoMenuCommand := inherited DoMenuCommand(aCmdNumber);
    end;

  end; {Case}
 end;

 procedure TMyCopiProofWindow.DoSetupMenus;
  OVERRIDE;

 begin

  inherited DoSetupMenus;

  if not SELF.fIsModal then
   begin

    Enable(cWriteToJournal, (fHead.fSize + fTail.fSize <> 0));
    EnableCheck(cTemplates, TRUE, fTemplate);

    Enable(cOpenProof, true);
    Enable(cOpenDraw, true);

    if SELF.IsShown then
     SetCmdName(cOpenProof, 'Hide Proof')
    else
     SetCmdName(cOpenProof, 'Show Proof'); {put in resources, also must check with close box}

    if fDeriverDocument.fDrawShapeView.GetWindow.IsShown then
     SetCmdName(cOpenDraw, 'Hide Drawing')
    else
     SetCmdName(cOpenDraw, 'Show Drawing'); {put in resources, also must check with close box}


   end;

 end;

 procedure TMyCopiProofWindow.AddPremise;

  var
   newline: TCopiProofline;

 begin

  SupplyCopiProofline(newline);
  with newline do
   begin
    fFormula := gRoot.CopyFormula;

    fJustification := strNull;  (*Copi has nothing check for traceback routines*)

   end;

  case fProoftype of
   NOpremNOconc:
    begin

     newline.fLineno := 1;
     newline.fHeadLevel := 0;

     SELF.InsertFirst(newline);

     fProoftype := premNOconc;

    end;

   premNOconc:
    begin
     SELF.InsertAtPseudoTail(newline)
    end;
   otherwise
  end;
  newline := nil;

 end;

 procedure TMyCopiProofWindow.AddConclusion;

  var
   formulanode: TFormula;
   newline: TCopiProofline;

  procedure AddTailLines;

  begin
   SupplyFormula(formulanode);
   formulanode.fkind := predicator;
   formulanode.finfo := '?'; {check}

   SupplyCopiProofline(newline);
   with newline do
    begin
                    {lineno := gTail.flineno + 1;}
     fFormula := formulanode;
     fJustification := '?';
     fSelectable := FALSE;
                    {subprooflevel := gTail.fsubprooflevel;}
    end;

   SELF.InsertAtTailFirst(newline);

   newline := nil;
   formulanode := nil;

   SupplyCopiProofline(newline);
   with newline do
    begin
     fFormula := gRoot.CopyFormula;
     fJustification := '?';
     fSelectable := FALSE;
                    {subprooflevel := gTail.fsubprooflevel;}
    end;
   SELF.InsertAtTailLast(newline);

   newline := nil;
   formulanode := nil;
  end;

 begin {AddProofConclusion}
  if (fProoftype = premNOconc) or (fProoftype = NOpremNOconc) then {don't add a second one}
   begin

    if (fProoftype = NOpremNOconc) then
     CreateBlankStart;

      {gHint := true;                      this is a global to show that the proof is in gHint mode}
{  gTail := gPseudoTail;            gPseudoTail marks where the insertions will be made. See Append}

    AddTailLines;

    if fProoftype = premNOconc then
     fProoftype := premConc
    else
     fProoftype := NopremConc;
   end;
 end;

 procedure TMyCopiProofWindow.Load; {selection already read}

  var
   delimiter, previousdelimiter: CHAR;


   newValuation: TList;
   oldend: integer;
                {dummy:TCopiParser;}

 begin

  gIllformed := FALSE;
  oldend := gInputEnd;
  delimiter := chComma;
  previousdelimiter := chBlank;

  if (gInputStart = gInputEnd) then {no selection}
   CreateBlankStart

  else
   begin

    while not gIllformed and MoreListInput(gInputHdl, gInputStart, oldend, gInputEnd, delimiter) and ((delimiter = chComma) or (delimiter = chTherefore)) do
     begin
      if (gInputStart <> gInputEnd) then {skip nullstrings}

       begin

       GetInput;
       skip(1, standardfilter);  (*primes gCurrch, and gLookaheadCh*)

       fDeriverDocument.fParser.wffcheck(gRoot, newValuation, gIllformed);

       if gIllformed then
       fDeriverDocument.WriteToJournal(concat(gCurrCh, gErrorsArray[12], gParserErrorMessage), TRUE, FALSE)

       else
       begin
                              {testformula := gRoot.CopyFormula;}

       if (previousdelimiter = chTherefore) then                               {or ((previousdelimiter = chBlank) and (delimiter = chTherefore)) }
 {no premises}
       AddConclusion
       else
       AddPremise;

       gRoot.DismantleFormula;

       end;
       end;

      previousdelimiter := delimiter;

      gInputStart := gInputEnd + 1; {miss comma}

     end;

   end;
 end;

 procedure TMyCopiProofWindow.ReInitProof;

 begin
 end;

 procedure TMyCopiProofWindow.ReStartProof;

 begin
 end;

 procedure TMyCopiProofWindow.StartProof;

  procedure StartUp;

  begin
               { SetLastAssumption; dont do this for Copi}

   CollapseTrivialCase;

               {SetButtons;}

   SetListSize;
   SELF.Open;
   SELF.BugAlert(strNull);
   SELF.fTextList.SetEmptySelection(FALSE); {to remove earlier selection}
   SELF.ForceRedraw; {to remove earlier proof}
   SELF.Select;

   TDialogView(SELF.FindSubView('WND2')).SelectEditText('Txtp', FALSE);

  end;

 begin

  DismantleProof; {previous one}

  InitProof;

  Load;

  if gIllformed then
   begin
    {ShowWindow(gModelessDP);}
{    SelectWindow(gModelessDP);}
{    FixDialogMenu;}
{    SetLoadButtons;}
   end
  else
   StartUp;

 end;

 procedure TMyCopiProofWindow.WriteDerivation;

 begin
 end;

 procedure TMyCopiProofWindow.WriteProofToJournal;

  var
   aString: str255;
   fontSize, rightMargin, dummy, i: integer;

  procedure WriteOut (item: Tobject);

  begin
   aString := TCopiProofline(item).Draw(fontSize, rightMargin, dummy);
   fDeriverDocument.WriteToJournal(aString, FALSE, FALSE);
  end;

  procedure SpecialWriteOut (item: Tobject);  (*to indicate insertion point*)

  begin
   aString := TCopiProofline(item).Draw(fontSize, rightmargin, dummy);
   insert(' <<', aString, length(aString));
   fDeriverDocument.WriteToJournal(aString, FALSE, FALSE);
  end;

 begin
  if fDeriverDocument.fJournalFont then
   fontSize := 12
  else
   fontSize := 9;
  rightMargin := fDeriverDocument.fRightMargin;

  TESetSelect(maxint, maxint, fDeriverDocument.fJournalTEView.fHTE);
  fDeriverDocument.WriteToJournal(gCR, FALSE, FALSE);
  fHead.Each(WriteOut);


  if fTail.fSize > 0 then
   begin

    SpecialWriteOut(fTail.At(1));
    for i := 2 to fTail.fSize do
     WriteOut(fTail.At(i));

   end;
 end;
{Debugging}
{$IFC qDebug}

 procedure TMyCopiProofWindow.Fields (procedure DoToField (fieldName: str255; fieldAddr: Ptr; fieldType: integer));
  OVERRIDE;

 begin

  inherited Fields(DoToField);

 end;

{$ENDC}

{$IFC THINK_PASCAL}


end.

procedure TCopiDocument.OldDoMakeViews (forPrinting: BOOLEAN);
 OVERRIDE; {check finder printing}

 var
  shapeView: TShapeView;
  palette: TPalette;
  aSubView, aProofView, aCopiRewriteView: TView;
  anotherHandler: TStdPrintHandler;

          {new one}

 procedure DoMakeTextViews (forPrinting: BOOLEAN);

  var
   aView, aParentView: TView;
   aTEView: TTEView;
   aHandler: TStdPrintHandler;

 begin
  if forPrinting then
               { We're only finder printing--don't need a window, just the view being printed  }

               {problem of finder printing without journal.finterpretation list?}

   aView := DoCreateViews(SELF, nil, kJournalTextViewRsrcID, gZeroVPt)
  else
   begin
    aView := NewTemplateWindow(kCopiJournalWindowRsrcID, SELF); {to here}
    fJournalWindow := TCopiJournalWindow(aView);
    TCopiJournalWindow(fJournalWindow).ICopiJournalWindow(SELF); {init other fields}
   end;

  FailNIL(aView); { ??? Will we have already failed?}

  aParentView := aView;
  aView := aParentView.FindSubView('TEVW');

  aTEView := TTEView(aView);

               {SetTextStyle(aStyle, kLogic, [], 12, gRGBBlack);}

  fJournalTEView := aTEView; { Must cast because FindSubView returns TView }
  aTEView.StuffText(fJournalText); { Stuff the initial text in }

  new(aHandler);
  FailNIL(aHandler);
  aHandler.IStdPrintHandler(SELF, fJournalTEView, FALSE, TRUE, FALSE); { its document }
 { its view }
 { does not have square dots }
 { horzontal page size is fixed }
 { vertical page size is variable (could be set }
               { �to true on non-style TE systems)    }
  aHandler.fMinimalMargins := FALSE;
               {ShowReverted for stylse etc;}
 end;

begin
 if forPrinting then
  palette := nil
 else
  begin

   new(palette);
   FailNIL(palette);
   palette.IPalette(SELF);
   fDrawPaletteView := palette;

  end;

 new(shapeView);
 FailNIL(shapeView);
 shapeView.IShapeView(SELF, palette, FALSE);

 aProofView := NewTemplateWindow(kCopiProofWindowRsrcID, SELF);
 FailNIL(aProofView);

 fProofWindow := TMyCopiProofWindow(aProofView);
 TMyCopiProofWindow(fProofWindow).IMyCopiProofWindow(SELF);
 aSubView := aProofView.FindSubView('LSTG'); {text list grid view}
 TCopiProofListView(aSubView).ICopiProofListView(fProofWindow); {ref to parent}

 aCopiRewriteView := NewTemplateWindow(kCopiRewriteWindowRsrcID, SELF);
 FailNIL(aCopiRewriteView);
 TCopiRewriteWindow(aCopiRewriteView).IRewriteWindow(fProofWindow); {ref to parent}

 TCopiProofWindow(fProofWindow).ICopiProofWindow(TCopiProofListView(aSubView), TCopiRewriteWindow(aCopiRewriteView)); {gives}
{               list, Rewrite, and window refs to eachother}

          {fJournalWindow.IJournalWindow(SELF);  will have to init other fields}

 new(anotherHandler);
 FailNIL(anotherHandler);
 anotherHandler.IStdPrintHandler(SELF, fProofWindow.fTextList, FALSE, TRUE, FALSE); { its document }
 { its view }
 { does not have square dots }
 { horzontal page size is fixed }
 { vertical page size is variable (could be set }
          { �to true on non-style TE systems)    }
 anotherHandler.fMinimalMargins := FALSE;
          {ShowReverted for stylse etc;}

 DoMakeTextViews(forPrinting);

end;

procedure OLDDoMakeDrawingView;
(*It creates its own print handler in IRes*)
 var
  aDrawWindow: TWindow;
begin
 aDrawWindow := NewTemplateWindow(kDrawViewRsrcID, SELF);
 FailNIL(aDrawWindow);

 fDrawPaletteView := TPalette(aDrawWindow.FindSubView('VW03'));
 FailNil(fDrawPaletteView);

 fDrawShapeView := TShapeView(aDrawWindow.FindSubView('VW02'));  (*already done in init*)
 FailNil(fDrawShapeView);

 fDrawShapeView.fPalette := fDrawPaletteView;
 fDrawShapeView.fScroller := fDrawShapeView.GetScroller(TRUE);
end;



{$ENDC }




 */
