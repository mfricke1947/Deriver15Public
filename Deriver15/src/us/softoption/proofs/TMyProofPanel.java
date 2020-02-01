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


public class TMyProofPanel extends TProofPanel {

  static final char chBlank = ' ';
  static final char chComma = ',';
  static final char chLSqBracket = '[';
  static final char chRSqBracket = ']';
  public static final char chTherefore='\u2234';

//  static final String strNull = "";  now in constants


  /* This almost always wants to have its Deriver document because the undoable proof
edits set the fDirty field of the document */

/*  public TMyProofPanel(){

  } */


public TMyProofPanel(TDeriverDocument itsDeriverDocument){
    super(itsDeriverDocument);
  }

public TMyProofPanel(TDeriverDocument itsDeriverDocument,boolean wantsIdentity){
      super(itsDeriverDocument,wantsIdentity);
  }
/*******************  Factory *************************/

TReAssemble supplyTReAssemble (TTestNode root){         // so we can subclass
  return
      new TReAssemble(fParser, root, null, 0);
}


/******************************************************/





  public void startProof(String inputStr){


    dismantleProof(); //{previous one}

    initProof();

    if (load(inputStr))
      startUp();

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
   //   System.out.print("found it");
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


/*

      procedure TProofWindow.CollapseTrivialCase; {This collapses the proof if the conclusion is one of}
  {                                             the premises}

    var
     lastline: TProofLine;
     found: boolean;
     conclusion: TFormula;
     currentLineno: integer;

    procedure Find (item: TObject);

    begin
     if not found then
      begin
       found := Equalformulas(conclusion, TProofLine(item).fFormula);
       currentLineno := TProofLine(item).fLineno;
      end;
    end;

   begin
    if (fProoftype = premConc) or (fProoftype = NopremConc) then
     begin
      found := false;
      conclusion := TProofLine(fTail.Last).fFormula;

      fHead.Each(Find);

      if found then
       begin
        TProofLine(fTail.Last).fLineno := TProofLine(fTail.Last).fLineno - 1;
        TProofLine(fTail.Last).ffirstjustno := currentLineno;
        TProofLine(fTail.Last).fjustification := ' R';
        TProofLine(fTail.Last).fSelectable := TRUE;
        TProofLine(fTail.Last).fSubProofSelectable := false;

        fHead.InsertLast(fTail.Last);

        TProofLine(fTail.First).DismantleProofLine;
        fTail.DeleteAll; {check garbage}

        fProoftype := pfFinished;
       end;
     end;
   end;


   */

  void startUp(){

  fModel.setLastAssumption();

  collapseTrivialCase();

  fModel.placeInsertionMarker();

  this.setVisible(true);

  }

  /*

   procedure TMyProofWindow.StartProof;

     procedure StartUp;

     begin
      SetLastAssumption;

      CollapseTrivialCase;

                  {SetButtons;}

      SetListSize;
      SELF.Open;
      SELF.BugAlert(strNull);
      SELF.fTextList.SetEmptySelection(FALSE); {to remove earlier selection}
      SELF.CheckCellHeights;  {mf new 6/24/91to force wrap in the assumptions}
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



      */


/*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
      compound terms eg Pf(a,b),Hc.

   Also in some systems a relation Lxy is written L(x,y) ie also with commas

  Also, in set theory we write {1,2,3} for a 'comprehension' of a set 

   We want to separate the premises but not the terms. So we will change the
   premise comma separators to another character. For the moment '!'*/


private static char chSeparator='�';  // need to pick a character here that is not used in logic

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


public boolean load(String inputStr){

        TParser parser=null;;

        if (fDeriverDocument!=null)
          parser=fDeriverDocument.getParser();

        if (parser==null){
          if (fParser==null)
            initializeParser();
          parser = fParser;
        }
        //we need to sort the above out, the document has a parser, and so too does the proof panel

        parser.initializeErrorString();


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

   if (premisesAndConclusion[0]!=null&&!strNull.equals(premisesAndConclusion[0])) /*!=strNull)*/{  // there are premises

 /*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
     compound terms eg Pf(a,b),Hc.

  Also in some systems a relation Lxy is written L(x,y) ie also with commas


  We want to separate the premises but not the terms. So we will change the
  premise comma separators to another character

 int nested=0;
 char currCh;

 StringBuffer input= new StringBuffer(premisesAndConclusion[0]);
 for (int i=0;i<input.length();i++){
   currCh=input.charAt(i);

   if (currCh==chSmallLeftBracket)
     nested++;
   if (currCh==chSmallRightBracket)
     nested--;

   if ((nested<1)&&(currCh==chComma))    //commas separating the list of premises are not nested
     input.setCharAt(i,chSeparator);
 }

 premisesAndConclusion[0]=input.toString(); */

       premisesAndConclusion[0]=changeListSeparator(premisesAndConclusion[0]);

     StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chSeparator)/*String.valueOf(chComma)*/);

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

  

//String


/* november 8th moving to split

       data=moreListInput(inputStr);

       while ((wellformed)&&(data.fMoreInput)) {
         inputStr= data.fFrontString;
         if (inputStr!=strNull){   // can be nullStr if input starts with therefore, should just skip
            TFormula root = new TFormula();
            StringReader aReader = new StringReader(inputStr);
            Vector dummy=new Vector();
            TParser parser =fDeriverDocument.fParser;

            //we want to parse the front string,and then make the backstring the new inputStr

            wffCheck(root, /*dummy, aReader), aReader)my, aReader);

            if (!wellformed)
              fDeriverDocument.writeToJournal(parser.fCurrCh + TConstants.fErrors12 + parser.fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
            else {              //wellformed, either premise or conclusion
              if (previousDelimiter==chTherefore)
                addConclusion(root);
              else
                addPremise(root);
            }
         }

         if (wellformed){
           previousDelimiter=data.fDelimiter;
           inputStr = data.fRearString;       // now finished with first string
           data=moreListInput(inputStr);      //read next
          }

       /*

            if (data.fFrontString != null) {
              inputStr = data.fRearString;
            }
            else
              wellformed = false;
          }
        inputStr = data.fRearString;
        data=moreListInput(inputStr);
       }  */


       return
           wellformed;

     }

/*

          procedure TMyProofWindow.Load; {selection already read}

       var
        delimiter, previousdelimiter: char;
        newValuation: TList;
        oldend: INTEGER;
                     {dummy:TParser;}

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

     (*in the case where a therefore comes first, MoreListInput returns a null string wchich gets skipped*)

            begin

            GetInput;
            skip(1, LogicFilter);  (*primes gCurrch, and gLookaheadCh*)

            fDeriverDocument.fParser.wffcheck(gRoot, newValuation, gIllformed);  {gentzen}

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


      */




void addTailLines(TFormula root){
   TFormula newnode= new TFormula(TFormula.predicator,String.valueOf(chQuestionMark),null,null);
   TProofline newline= supplyProofline();

   newline.fFormula=newnode;
   newline.fJustification = "?";
   newline.fSelectable=false;

   fModel.insertAtTailFirst(newline);

   {TProofline secondline = supplyProofline();

     secondline.fFormula = root.copyFormula();
     secondline.fJustification = "?";
     secondline.fSelectable = false;

   fModel.insertAtTailLast(secondline);
   }

/*
                procedure AddTailLines;

               begin
                SupplyFormula(formulanode);
                formulanode.fKind := predicator;
                formulanode.finfo := '?'; {check}

                SupplyProofline(newline);
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

                SupplyProofline(newline);
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

        */
     }

void addConclusion(TFormula root){
       if ((fProofType == premNoConc) || (fProofType == noPremNoConc))  // don't add a second one}
       {

          if (fProofType == noPremNoConc)
           createBlankStart();

//            {gHint := true;                      this is a global to show that the proof is in gHint mode}
//      {  gTail := gPseudoTail;            gPseudoTail marks where the insertions will be made. See Append}

          addTailLines(root);

          if (fProofType == premNoConc)
           fProofType = premConc;
          else
           fProofType = noPremConc;
       }



       /*

        void addPremise(TFormula root){
   TProofline newline= new TProofline();
   newline.fFormula=root.copyFormula();
   newline.fJustification = "Ass";

   switch (fProoftype){

     case noPremNoConc: {
       newline.fLineno=1;
       newline.fHeadlevel=0;

       fModel.insertFirst(newline);

       fProoftype=premNoConc;
       break;
     }

     case premNoConc:
       fModel.insertAtPseudoTail(newline);
        break;

     default:  break;

   }

 }


        */
}

     /*

       procedure TMyProofWindow.AddConclusion;

        var
         formulanode: TFormula;
         newline: TProofline;

        procedure AddTailLines;

        begin
         SupplyFormula(formulanode);
         formulanode.fKind := predicator;
         formulanode.finfo := '?'; {check}

         SupplyProofline(newline);
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

         SupplyProofline(newline);
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



      */

void addPremise(TFormula root){
   TProofline newline= supplyProofline();
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


/*


   procedure TMyProofWindow.AddPremise;

   var
    newline: TProofline;

  begin

   SupplyProofline(newline);
   with newline do
    begin
     fFormula := gRoot.CopyFormula;
     fJustification := 'Ass';
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


  */





/*

class StringSplitData {
       String fFrontString=null;
       String fRearString=null;
       char fDelimiter= chBlank;
       boolean fMoreInput=false;
     }

*/



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

  TReAssemble aReAssembly= supplyTReAssemble(root); //new TReAssemble(fParser,root,null,0);

    aReAssembly.reAssembleProof();

    ArrayList tempHead=aReAssembly.fHead;
    int lastAssumption = aReAssembly.fLastAssIndex;



    improve(tempHead, lastAssumption);  // seems of  temp out Dec)6

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

  /*

   procedure CreatProofSegment;

       var
        lastAssumption, dummy: INTEGER;


       procedure SetHeadLevels;

        var
         headLevel: INTEGER;

        procedure SetLevel (item: Tobject);

        begin
         TProofline(item).fHeadLevel := headLevel;
        end;

       begin
        if TProofline(fHead.First).fblankline then
         headLevel := -1 {no antecedents}
        else
         headLevel := 0;
        tempHead.Each(SetLevel);
       end;

      begin
       lastAssumption := 0;

    {$IFC bestpath}
       ReAssembleProof(gTestroot, tempHead, lastAssumption); { }
    {$ENDC}

       Improve(tempHead); {12 March 1991 moved on a few lines mf put back in}

       SetHeadLevels; {sadly, each proofline has to carry info on its head level}

       PrepareSegmentForSplice(fHead, tempHead, lastAssumption);

    {Improve(tempHead); new position}

    (*major changes here because you may prove something that is an assumption*)

    (*RemoveDuplicatesinNew; *)
      end;


  */





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

//   aTestRoot.startSatisfactionTree();

  // aTestRoot.fStepsToExpiry=TTestNode.kMaxTreeDepth;

   TTreeModel aTreeModel= new TTreeModel(aTestRoot.fTreeNode);  //debug remove later

  // aTestRoot.fTreeModel=aTreeModel;

   aTestRoot.initializeContext(aTreeModel);  //debug Tree Model initialized now


  // int maxSteps=50;

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

 /*

   if gexCVflag then
       if not (outcome = valid) then
        gexCV := TRUE;
      if gUniCVflag then
       if not (outcome = valid) then
        gUniCV := TRUE;

      if gexCV or gUniCV then
       begin
        temptest := gTestroot.CopyNodeinFull;
        temptest.fclosed := FALSE;
        temptest.fdead := FALSE;
        temptest.flLink := nil;
        temptest.fRlink := nil;

        DismantleTestTree(gTestroot);

        gTestroot := temptest;
        temptest := nil;

        outcome := gTestroot.TreeValid(kMaxtreesize); {check re-init stringstore}

       end;

      case outcome of
       valid:
        begin
        sysBeep(5); { ReDisplayProof;}
        derivationFound := true;

        BugAlert('A derivation has been found... please wait much longer.');

 {DismantleProof; previous one}

 {WriteDerivation;}

        BugAlert('!');

        end;

       notvalid:
        begin
        sysBeep(5);
        BugAlert('Not derivable from these standing assumptions.');
        end;
       notknown:
        begin
        sysBeep(5);
        BugAlert('Unsure whether the sequent can be derived.');
        end;
       otherwise
      end;

     end;


   end;


   */


/*
    This is a routine from TBrowser

    void selectionSatisfiable(){

         String inputStr;
         boolean wellFormed = true;
         int badChar=-1;
       ArrayList dummy=new ArrayList();

       ArrayList newValuation;

       TTestNode aTestRoot = new TTestNode(fDeriverDocument.fParser,null);  //does not initialize TreeModel

       DefaultTreeModel aTreeModel= new DefaultTreeModel(aTestRoot.fTreeNode);

       aTestRoot.fTreeModel=aTreeModel;                                  //Tree Model initialized now
      // aTes


       aTestRoot.startSatisfactionTree();


      String selectionStr=TUtilities.readSelectionToString(fJournalPane,TUtilities.logicFilter);

      StringTokenizer st = new StringTokenizer(selectionStr, ",");

      while ((st.hasMoreTokens())&& wellFormed){

        inputStr = st.nextToken();

        if (inputStr != strNull) { // can be nullStr if they put two commas togethe,should just skip
          TFormula root = new TFormula();
          StringReader aReader = new StringReader(inputStr);

          newValuation = fDeriverDocument.fValuation;

          wellFormed = fDeriverDocument.fParser.wffCheck(root, newValuation,
              aReader);

          if (!wellFormed) {
            fDeriverDocument.writeToJournal(
                "(*You need to supply a list of well formed formulas"
                + " separated by commas. Next is what the parser has to"
                + " say about your errors.*)"
                + strCR
                + fDeriverDocument.fParser.fCurrCh + TConstants.fErrors12
                + fDeriverDocument.fParser.fParserErrorMessage,
                TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
          }
          else {

            badChar = badCharacters(root);

            if (badChar == kNone) {
              aTestRoot.addToAntecedents(root);
              fDeriverDocument.fValuation = newValuation;
                  /*notice here that we use only the last valuation for
               the whole thing
            }
            else {

              writeBadCharacterErrors(badChar);
              return;
            }
          }
        }
        System.out.print(inputStr + " ");
      }


    if (wellFormed){
           TFormula.interpretFreeVariables(fDeriverDocument.fValuation, aTestRoot.fAntecedents);


           // check on what I think about surgery here

           nodeSatisfiable(aTestRoot);

           TTestDisplayTree aTestDisplayTree= new TTestDisplayTree(aTreeModel);


     //not doing at present AUg04      aTestDisplayTree.display();

                }





    }
*/








}



/*

 function TMyProofWindow.DoDerive (long: boolean): TCommand;

   var
    firstline: TProofline;
    newline: TProofline;
    aLineCommand: TLineCommand;
    derivationFound: boolean;
    tempHead: TList;

   procedure DoProof;

    var
     outcome: argumenttype;
     temptest: TTestnode;
     abandon, equals, compoundterms, higharity: boolean;

    procedure AddFormulas;

     var
      aProofline: TProofline;
      i: INTEGER;

    begin
     i := 1;
     if fHead.fSize <> 0 then
      repeat
       aProofline := TProofline(fHead.At(i));
       if not aProofline.fblankline then
        if aProofline.fSelectable then
        begin
        gTestroot.fAntecedents.InsertFirst(aProofline.fFormula.CopyFormula);
 {best to have these in reverse order, because the User may have done some work}

        if (not abandon) then
        abandon := fDeriverDocument.fJournalWindow.BadCharacters(aProofline.fFormula, equals, compoundterms, higharity);

        end;
       i := i + 1;
      until (i > fHead.fSize);

                {adds standing antecedents}

     gTestroot.fSucceedent.InsertLast(TProofline(fTail.At(2)).fFormula.CopyFormula);

     if (not abandon) then
      abandon := fDeriverDocument.fJournalWindow.BadCharacters(TProofline(fTail.Last).fFormula, equals, compoundterms, higharity);

    end;

   begin
    BugAlert('This may take some time. To abandon, press the command key and type a stop.');

    equals := FALSE;
    compoundterms := FALSE;
    higharity := FALSE;
    abandon := FALSE;

    StartSatisfactionTree; {initializes gTestroot}
    AddFormulas;

    if abandon then
     begin
      if equals then
       BugAlert('(*Sorry, "Derive It" for = has not yet been implemented.*)')

      else if compoundterms then
       BugAlert('(*Sorry, "Derive It" for compoundterms has not yet been implemented.*)')
      else if higharity then
       BugAlert('(*Sorry, "Derive It" for high-arity predicates has not yet been implemented.*)');

     end

    else

     begin
      outcome := gTestroot.TreeValid(kMaxtreesize);

      if gexCVflag then
       if not (outcome = valid) then
        gexCV := TRUE;
      if gUniCVflag then
       if not (outcome = valid) then
        gUniCV := TRUE;

      if gexCV or gUniCV then
       begin
        temptest := gTestroot.CopyNodeinFull;
        temptest.fclosed := FALSE;
        temptest.fdead := FALSE;
        temptest.flLink := nil;
        temptest.fRlink := nil;

        DismantleTestTree(gTestroot);

        gTestroot := temptest;
        temptest := nil;

        outcome := gTestroot.TreeValid(kMaxtreesize); {check re-init stringstore}

       end;

      case outcome of
       valid:
        begin
        sysBeep(5); { ReDisplayProof;}
        derivationFound := true;

        BugAlert('A derivation has been found... please wait much longer.');

 {DismantleProof; previous one}

 {WriteDerivation;}

        BugAlert('!');

        end;

       notvalid:
        begin
        sysBeep(5);
        BugAlert('Not derivable from these standing assumptions.');
        end;
       notknown:
        begin
        sysBeep(5);
        BugAlert('Unsure whether the sequent can be derived.');
        end;
       otherwise
      end;

     end;


   end;

   procedure CreatProofSegment;

    var
     lastAssumption, dummy: INTEGER;


    procedure SetHeadLevels;

     var
      headLevel: INTEGER;

     procedure SetLevel (item: Tobject);

     begin
      TProofline(item).fHeadLevel := headLevel;
     end;

    begin
     if TProofline(fHead.First).fblankline then
      headLevel := -1 {no antecedents}
     else
      headLevel := 0;
     tempHead.Each(SetLevel);
    end;

   begin
    lastAssumption := 0;

 {$IFC bestpath}
    ReAssembleProof(gTestroot, tempHead, lastAssumption); { }
 {$ENDC}

    Improve(tempHead); {12 March 1991 moved on a few lines mf put back in}

    SetHeadLevels; {sadly, each proofline has to carry info on its head level}

    PrepareSegmentForSplice(fHead, tempHead, lastAssumption);

 {Improve(tempHead); new position}

 (*major changes here because you may prove something that is an assumption*)

 (*RemoveDuplicatesinNew; *)
   end;

   procedure InsertAll;
    var
     theSubprooflevel: integer;
    procedure PutIn (item: TObject);
    begin
     TProofline(item).fSubProofLevel := TProofline(item).fSubProofLevel + theSubprooflevel;
     aLineCommand.fNewlines.InsertLast(TProofline(item));
    end;
   begin
    theSubprooflevel := TProofline(fHead.Last).fSubProofLevel - TProofline(fHead.Last).fHeadLevel;
    tempHead.Each(PutIn);
   end;

   procedure InsertFirstLine;
    var
     theProofline: TProofline;
     i: integer;

   begin
    theProofline := TProofline(tempHead.First);

    theProofline.fSubprooflevel := theProofline.fSubprooflevel + TProofline(fHead.Last).fSubProofLevel - TProofline(fHead.Last).fHeadLevel;

    aLineCommand.fNewlines.InsertLast(theProofline);

    for i := 2 to tempHead.fSize do
     begin
      TProofline(tempHead.At(i)).DismantleProofline;
     end;
 (*tempHead.DeleteAll;*)
   end;

   procedure RecognizePlan;  (*for future development*)
    var
     target: TFormula;
   begin
    target := TProofline(fTail.At(2)).fFormula;
   end;



  begin
   DoDerive := gNoChanges;
   if (TProofline(fTail.First).fSubProofLevel) < TProofline(fHead.Last).fSubProofLevel then
    begin
     sysBeep(5);
     BugAlert('First please drop the extra assumptions.');
    end

   else
    begin
     derivationFound := false;

     DoProof;

     if derivationFound then
      begin
       CreatProofSegment;

       New(aLineCommand);
       FailNil(aLineCommand);

       if long then
        begin
        aLineCommand.ILineCommand(cDeriveIt, SELF);
        InsertAll;
        end
       else
        begin
        aLineCommand.ILineCommand(cNextLine, SELF);
        InsertFirstLine;
        end;

       DoDerive := aLineCommand;
      end;

     DismantleTestTree(gTestroot);
    end;
  end;



*/















    /************ Experiments with Rewrite ******************
     *
     *
     */




   public class RewriteAction extends AbstractAction{

     TRewriteRules fRules;
     int fLineno;
     boolean fMustChange;    //whether the rewrites have to change the formula

    RewriteAction(String label,
                  TRewriteRules rules,
                  int lineNo,
                  boolean mustChange ){
      putValue(NAME, label);

      fRules=rules;
      fLineno=lineNo;
      fMustChange=mustChange;

    }

    public void actionPerformed(ActionEvent ae){
      if ((fRules.getNewRoot()!=null)&&
          (fRules.getSelectionRoot()!=null)&&
          (!fRules.getNewRoot().equalFormulas(fRules.getNewRoot(),fRules.getSelectionRoot())||
           !fMustChange)
      )
          {

       // we need to find the entire after formula

       TFormula afterRoot = fRules.getAfterRoot();

            if (afterRoot!=null){ // should alwyas be

              TProofline newline = supplyProofline();

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

        boolean mustChange=true;

        defaultButton = new JButton(new RewriteAction("Go",
                                                      rules,
                                                      selectedLine.fLineno,
                                                      mustChange));


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

 /*********************************** OLD CODE ****************************************/

 /* I don't think I use this any more

StringSplitData moreListInput(String inputStr){

       /*{this is to break up a list of input which is separated by commas or a therefore}
         You have to take care when calling this because}
                  {with commas you are interested in the stuff before them, but with therefores you are interested}
                  {in the stuff after.}

        Then, to complicate it more, if there is a valuation, with commas within square brackets,
        you ignore those commas



       StringSplitData data = new StringSplitData();

       /*This can get called with either a null string or no string

       if ((inputStr==null)||(inputStr==strNull))
         return
             data;
       else{

       int firstComma=inputStr.indexOf(chComma);
       int firstTherefore=inputStr.indexOf(chTherefore);
       int firstLeftSquareBracket=inputStr.indexOf(chLSqBracket);
       int firstRightSquareBracket=inputStr.indexOf(chRSqBracket);



       if ((firstLeftSquareBracket>0)&&                      //miss the valuation
           (firstRightSquareBracket>0)&&
           (firstLeftSquareBracket<firstRightSquareBracket)){
         while ((firstComma>firstLeftSquareBracket)&&(firstComma<firstRightSquareBracket))
         {
          firstComma= inputStr.indexOf(chComma,firstComma);// next one or -1
         }
       }

       // right we now have two good indices which might be -1 (not found)

       if (((firstTherefore==-1)||(firstComma<firstTherefore))&&(firstComma!=-1)){     //comma first or good comma and no therefore
         data.fFrontString=inputStr.substring(0,firstComma);   //off by one?
         data.fRearString=inputStr.substring(firstComma+1);
         data.fDelimiter=chComma;
         data.fMoreInput=true;
       }
       else
          if (firstTherefore!=-1) {                               //therefore first
            if (firstTherefore==0)
              data.fFrontString=strNull;
            else
               data.fFrontString=inputStr.substring(0,firstTherefore);   //off by one?

            data.fRearString=inputStr.substring(firstTherefore+1);
            data.fDelimiter=chTherefore;
            data.fMoreInput=true;
          }
      else{
        data.fFrontString = inputStr;        //no comma, no therefore, but real input
        data.fMoreInput=true;
      }

    return
       data;

        }
}*/
        /*

                     function MoreListInput (var theseChars: CharsHandle; start: integer; oldEnd: integer; var newEnd: integer; var delimiter: char): boolean;
           {this is to break up a list of input which is separated by commas or a therefore}
           {-- the var parameter delimiter returns which one. You have to take care when calling this because}
           {with commas you are interested in the stuff before them, but with therefores you are interested}
           {in the stuff after.}

             var
              found: boolean;
              i, brackets: integer;
              searchCh: char;

            begin
             MoreListInput := (start < oldEnd);

             if (start >= oldEnd) then
              theseChars := nil; {check, to help Getinput to read strings}

             found := FALSE;
             brackets := 0;
             newEnd := oldEnd; {in case no commas or therefores}

             if theseChars <> nil then
              begin

               if (oldEnd - start) > 0 then
                begin
                 i := start;

                 while (i < oldEnd) and not found do
                  begin
                  searchCh := theseChars^^[i];
                  if searchCh = chLSqBracket then {do not wish to find commas within sq}
           {                                                          brackets}
                  brackets := brackets + 1;
                  if (searchCh = chRSqBracket) then
                  if (brackets <> 0) then
                  brackets := brackets - 1;

                  if (searchCh = chComma) or (searchCh = chTherefore) then
                  if (brackets = 0) then
                  begin
                  found := TRUE;
                  delimiter := searchCh;
                  newEnd := i; {routines which call this advance the index to miss}
           {                                                 out delimiter completely}
                  end;
                  i := i + 1;
                  end;
                end;
              end;
            end;


           */


/* we let mergedata do this now

void prepareSegmentForSplice(ArrayList firstHead, ArrayList secondHead){
/*This alters a second proof so that it can be fitted into the context of the first

int firstProofType, secondProofType;

  if (((TProofline)firstHead.get(0)).fBlankline)
    firstProofType=noPremConc;
  else
    firstProofType=premConc;

  if (((TProofline)secondHead.get(0)).fBlankline)
    secondProofType=noPremConc;
  else
    secondProofType=premConc;

    TProofListModel.renumberLines(secondHead,1000);

  /*
     garbageList := nil;
                garbageList := newlist;

                firstTail := firstlocalHead.fSize; {index}
                secondTail := secondlocalHead.fSize; { second index}

                if TProofline(firstlocalHead.First).fBlankline then
                 firstprooftype := NoPremConc
                else
                 begin
                  firstprooftype := PremConc;
                 end;

                if TProofline(secondlocalHead.First).fBlankline then
                 secondprooftype := NoPremConc
                else
                 begin
                  if (secondlastAssumption = secondlocalHead.fSize) then
                   secondprooftype := PremNoConc
                  else
                   secondprooftype := PremConc;
                 end;

                RenumberLines(secondlocalHead, 1000);






          }  */


/*

           procedure PrepareSegmentForSplice (firstlocalHead: TList; var secondlocalHead: TList; var secondlastAssumption: integer);

           {This alters a second proof so that it can be fitted into the context of the first}


             var
              garbageList: TList;
              garbageLIne, newline: TProofline;
              firstFormula, secondFormula: TFormula;

              firstprooftype, secondprooftype: prooftype;
              firstTail, secondTail, i: integer;

             procedure Sift;

              var
               searchline: TProofline;
               itslineno, index, limit: integer;

           {This discards all the Ass steps of the second proof, renumbering to}
           {the lines in the first proof.}

             begin
              index := 1;
              limit := secondlastAssumption;
              while (index <= limit) do {going to discard premises}
               begin
                searchline := TProofline(secondlocalHead.At(index));
                if InProof(firstlocalHead, searchline.fFormula, itslineno) then
                 begin
                  ReNumSingleLine(secondlocalHead, index, itslineno);
                  garbageList.InsertFirst(searchLine);
                 end
                else
                 begin
                  sysBeep(5);
           {$IFC myDebugging}
                  writeln('Merge error at index', index, ' and limit', limit);
           {$ENDC}
                 end;

                index := index + 1;
               end;

              index := 1;
              while (index <= limit) do {going to tidy up}
               begin
                secondlocalHead.Delete(secondlocalHead.First);
                index := index + 1;
               end;
              secondlastAssumption := 0;
             end;

            begin
             garbageList := nil;
             garbageList := newlist;

             firstTail := firstlocalHead.fSize; {index}
             secondTail := secondlocalHead.fSize; { second index}

             if TProofline(firstlocalHead.First).fBlankline then
              firstprooftype := NoPremConc
             else
              begin
               firstprooftype := PremConc;
              end;

             if TProofline(secondlocalHead.First).fBlankline then
              secondprooftype := NoPremConc
             else
              begin
               if (secondlastAssumption = secondlocalHead.fSize) then
                secondprooftype := PremNoConc
               else
                secondprooftype := PremConc;
              end;

             RenumberLines(secondlocalHead, 1000);

             case firstprooftype of
              NoPremConc:
               case secondprooftype of
                NoPremConc:
                 begin
                  garbageLIne := TProofline(secondlocalHead.First);
                  secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
                  garbageLIne.DismantleProofline;
                 end;

                PremConc:
                 begin
             {June 22 1991 to correct error when second proof has assumptions but}
           {the first has no standing assumptions eg P^P hookP}
                  Sift;
                  IncreaseSubProofLevels(secondlocalHead, -1);
                 end;

                otherwise
               end;


              PremConc:
               case secondprooftype of
                NoPremConc:
                 begin
                  IncreaseSubProofLevels(secondlocalHead, +1);
                  garbageLIne := TProofline(secondlocalHead.First);
                  secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
                  garbageLIne.DismantleProofline;
                 end;

                PremNoConc:
                 begin
                  secondFormula := TProofline(secondlocalHead.First).fFormula;

                  SupplyProofline(newline);
                  with newline do
                  begin
                  fFormula := secondFormula.CopyFormula;
                  ffirstjustno := 1;
                  fJustification := ' R';
                  fSubprooflevel := 0;
                  end;
                  secondlocalHead.InsertLast(newline);

                  Sift;
                 end;

                PremConc:
                 Sift;

                otherwise

               end;
              otherwise

             end;

             RemoveDuplicatesinNew(firstlocalHead, secondlocalHead);
             RenumberLines(secondlocalHead, (TProofline(firstlocalHead.Last).fLineno + 1));



             for i := 1 to garbageList.fSize do
              TProofline(garbageList.At(i)).DismantleProofline;
             garbageList.DeleteAll;
             garbageList.Free;

            end;

     */





