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

package us.softoption.editor;



import static us.softoption.infrastructure.Symbols.strCR;

import javax.swing.JMenu;

import us.softoption.parser.TCopiParser;
import us.softoption.proofs.TMyCopiProofPanel;





public class TCopiDocument extends TDeriverDocument{


public TCopiDocument(){

  fEToL.replacePropRules(TCopiEtoLRules.fPropRules);                    // copi needs different from superclass
  fEToL.replaceRules(TCopiEtoLRules.fRules);
  String[] params={"chcopiand","."};                     //params={"chCopiAnd","."};  our lisp is lower case only
  fEToL.setLtoEFilter(params);                           // so we write back the ands properly

               }



public TCopiDocument(TJournal itsJournal){   // when it is running without a Browser, eg in an applet
  super(itsJournal);

 fEToL.replacePropRules(TCopiEtoLRules.fPropRules);                    // copi needs different from superclass
 fEToL.replaceRules(TCopiEtoLRules.fRules);
 String[] params={"chcopiand","."};                     //params={"chCopiAnd","."};  our lisp is lower case only
 fEToL.setLtoEFilter(params);                           // so we write back the ands properly




}

  public TCopiDocument(TJournal itsJournal,boolean wantsIdentity){   // when it is running without a Browser, eg in an applet
    super(itsJournal,wantsIdentity);

   fEToL.replacePropRules(TCopiEtoLRules.fPropRules);                    // copi needs different from superclass
   fEToL.replaceRules(TCopiEtoLRules.fRules);
   String[] params={"chcopiand","."};                     //params={"chCopiAnd","."};  our lisp is lower case only
   fEToL.setLtoEFilter(params);                           // so we write back the ands properly



}







void initializePalettes(){
  fDefaultPaletteText=strCR+

                        "F \u2234 F . G"+

                        " \u223C  .  \u2228  \u2283  \u2261  \u2203  \u2234 " +  // \u2200 no uniquant
                        strCR+
                        strCR+

                   " Rxy[a/x,b/y] (\u2200x)(Fx \u2283 Gx)";


 fBasicPalette=" \u223C  .  \u2228  \u2283  \u2261  \u2203  \u2234 ";

  }


public void initializeParser(){     //TIDY UP CODE

   fParser= new TCopiParser();
   fParserName="Copi";
      }

public void initializeProofPanel(){
  /*      if (TPreferences.fRewriteRules)
        fProofPanel= new TRewrite(this);   //add Copi
    else */
        fProofPanel= new TMyCopiProofPanel(this);

  }


  public JMenu supplyExamsSubMenu(){        // the different document types eg Copi want different ones of these
  JMenu exams= new JMenu();

  exams.add(new MidTermQ6());
  exams.add(new FinalQ6());
  exams.add(new FinalQ78());

  return
      exams;

}



  public JMenu supplyQuizzesSubMenu(){        // the different document types eg Copi want different ones of these
    JMenu quizzes= new JMenu();


    quizzes.add(new Quiz2());
    quizzes.add(new Quiz7());
    return
        quizzes;

}



}

/*



     {$IFC THINK_PASCAL}

     unit TCopiDocument;



     interface

      uses
       DerImpNotes, SysEqu, Traps, ULoMem, UMacAppUtilities, UPatch, UObject, UViewCoords, UMemory, UFailure, UMenuSetup, UList, PrintTraps, UAssociation, UMacApp, UTEView,
     { � MacApp }

     { � Building Blocks }
       UPrinting, UDialog, UGridView,
     { � Implementation Use }
       Picker, UStream, ULogicGlobals90, LispUnit, UFormulaIntf, EnglishToLogic, UProofViewIntf, UCopiIntf, USemanticTestIntf, UReAssemble90, UDeriverIntf;                                                                                                                                 												      			      (*only compile this if running theorem prover any gentzen systems*)

     (*you'll also have to put the semi-colon earlier if not compiled*)
                             {reassemble is also commented out in the MakeFile and so are its segments in the}
     {			resources}


     implementation

     {$ENDC}



     {************* TCopiDocument *******************}

      procedure TCopiDocument.ICopiDocument (fileType: OSType);
       var
        aCopiParser: TCopiParser;
      begin
       SELF.IShapeDocument(fileType);
       SELF.fParser.Free;
       New(aCopiParser);
       FailNIL(aCopiParser);
       fParser := aCopiParser;
      end;

     {$S Copi}
      procedure TCopiDocument.DoMakeViews (forPrinting: BOOLEAN); {check finder printing}

       var
        aSubView: TView;
        aDocState: DocState;


               {new one}

       procedure DoMakeTextViews (forPrinting: BOOLEAN);

        var
         aView, aParentView: TView;
         aTEView: TTEView;
         aHandler: TStdPrintHandler;
         newStyle: TextStyle;

        procedure RestoreJournalWindow;
                     { RestoreWindow restores the window & scroller using the settings in the documents fDocState}
     {		field }
        begin
         with aDocState.theJournalWindowRect do
          begin
           fJournalWindow.Resize(right - left, bottom - top, FALSE);
           fJournalWindow.Locate(left, top, FALSE);
          end;
         fJournalWindow.ForceOnScreen;
         with aDocState.theJournalScrollPosition do {check}
          TScroller(fJournalWindow.FindSubView('SCLR')).ScrollTo(h, v, FALSE);
        end;


       begin
        if forPrinting then
                    { We're only finder printing--don't need a window, just the view being printed  }

                    {problem of finder printing without journal.finterpretation list?}

         aView := DoCreateViews(SELF, nil, kJournalTextViewRsrcID, gZeroVPt)
        else
         begin
          aView := NewTemplateWindow(kCopiJournalWindowRsrcID, SELF); {to here}
          FailNIL(aView);{12/7/90}
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
        aHandler.fMinimalMargins := FALSE;
                    {ShowReverted for stylse etc;}

        if not fJournalFont then (*defaults to large Font this resets to small*)
         begin
          SetTextStyle(newStyle, kLogicFont, [], 9, gRGBBlack);
          fJournalTEView.SetOneStyle(0, maxInt, doSize, newStyle, kdontRedraw);
         end;

        if fReopening then
         RestoreJournalWindow
        else
         begin
          fJournalWindow.AdaptToScreen;
          fJournalWindow.SimpleStagger(kStaggerAmount, kStaggerAmount, gStaggerCount);
                    {17 aug}
         end;

        fJournalWindow.Open;

       end;

       procedure DoMakeDrawingView (forPrinting: boolean);
     (*It creates its own print handler in IRes*)
        var
         aDrawWindow: TWindow;
         aShapeView: TShapeView;
         minSize: Point;
         maxSize: Point;
         i: integer;


        procedure RestoreWindow;
                     { RestoreWindow restores the window & scroller using the settings in the documents fDocState}
     {		field }
        begin
         with aDocState.theWindowRect do
          begin
           aDrawWindow.Resize(right - left, bottom - top, FALSE);
           aDrawWindow.Locate(left, top, FALSE);
          end;
         aDrawWindow.ForceOnScreen;
         with aDocState.theScrollPosition do
          fDrawShapeView.fScroller.ScrollTo(h, v, FALSE);
        end;

       begin
        if forPrinting then
         begin
          aShapeView := TShapeView(DoCreateViews(SELF, nil, kDrawViewRsrcID, gZeroVPt));
          fDrawShapeView := aShapeView;
         end
        else

         begin
          aDrawWindow := NewTemplateWindow(kDrawWindowRsrcID, SELF);
          FailNIL(aDrawWindow);

          fDrawPaletteView := TPalette(aDrawWindow.FindSubView('VW03'));
          FailNil(fDrawPaletteView);

          fDrawShapeView := TShapeView(aDrawWindow.FindSubView('VW02'));  (*already done in init*)
          FailNil(fDrawShapeView);

          fDrawShapeView.fPalette := fDrawPaletteView;
          fDrawShapeView.fScroller := fDrawShapeView.GetScroller(TRUE);

          if fReopening then
           begin
            RestoreWindow;
            for i := 1 to kShapesinPalette do {MF}
            begin
            gShapesArray[i].fShade := aDocState.theShades[i];
            gShapesArray[i].fColor := aDocState.theColors[i];
            end;
            if InterpretationChanged then
            fDrawShapeView.UpdateInterpretationBoards;{MF}
           end

          else
           begin
            aDrawWindow.AdaptToScreen;
            aDrawWindow.SimpleStagger(kStaggerAmount, kStaggerAmount, gStaggerCount);
            CreateInterpretationBoard;
           end;

                     { set window's resize limits so it can't become wider than the shapeview's edge }
          with aDrawWindow.fResizeLimits do
           begin
            minSize := topLeft;
            maxSize := botRight;
           end;
          with maxSize do
           h := Min(fDrawShapeView.fSize.h + fDrawPaletteView.fSize.h + kSBarSizeMinus1, h);
          aDrawWindow.SetResizeLimits(minSize, maxSize);
         end;
       end;

       procedure DoMakeProofView;
        var
         aProofView, aCopiRewriteView: TView;
         anotherHandler: TStdPrintHandler;
        procedure RestoreProofData;
        begin
         fProofWindow.InitProof;

         fProofWindow.fHead.Free; {the problem is that data is read from the disk to document}
         fProofWindow.fHead := fHeadFromFile; {and then on to the proof window}
         fHeadFromFile := nil;

         fProofWindow.fTail.Free;
         fProofWindow.fTail := fTailFromFile;
         fTailFromFile := nil;

         fProofWindow.SetListSize;

         fProofWindow.CheckCellHeights;

         fProofWindow.ResetSelectables;

         fProofWindow.fProofType := fProofTypeFromFile;
        end;

        procedure RestoreProofWindow;
        begin
         with aDocState.theProofWindowRect do {MF from here}
          begin
           fProofWindow.Resize(right - left, bottom - top, FALSE);

           fProofWindow.Locate(left, top, FALSE);
          end;
         fProofWindow.ForceOnScreen;
         with aDocState.theProofScrollPosition do
          TScroller(fProofWindow.FindSubView('SCLR')).ScrollTo(h, v, FALSE);
        end;

       begin
        aProofView := NewTemplateWindow(kCopiProofWindowRsrcID, SELF);
        FailNIL(aProofView);

        fProofWindow := TMyCopiProofWindow(aProofView);
        TMyCopiProofWindow(fProofWindow).IMyCopiProofWindow(SELF);
        aSubView := aProofView.FindSubView('LSTG'); {text list grid view}
        TCopiProofListView(aSubView).ICopiProofListView(fProofWindow); {ref to parent}

        aCopiRewriteView := NewTemplateWindow(kCopiRewriteWindowRsrcID, SELF);
        FailNIL(aCopiRewriteView);
        TCopiRewriteWindow(aCopiRewriteView).IRewriteWindow(fProofWindow); {ref to parent}

        TCopiProofWindow(fProofWindow).EstablishReferences(TCopiProofListView(aSubView), TCopiRewriteWindow(aCopiRewriteView)); {gives}
     {               list, Rewrite, and window refs to eachother}

               {fJournalWindow.IJournalWindow(SELF);  will have to init other fields}

        new(anotherHandler);
        FailNIL(anotherHandler);
        anotherHandler.IStdPrintHandler(SELF, fProofWindow.fTextList, FALSE, TRUE, FALSE); { its document }

        RestoreProofData;


        if fReopening then
         begin
          RestoreProofWindow;
          fProofWindow.fTemplate := aDocState.theTemplate;

          if (aDocState.theNumofHeadLines + aDocState.theNumofTailLInes) > 1 then {proof}
     {                       window open}
           begin


            fProofWindow.Open;

            TDialogView(fProofWindow.FindSubView('WND2')).SelectEditText('Txtp', FALSE); {puts}
     {                         insertion in edit text}
           end;
         end;


       end;

      begin
             {	if forPrinting then}
     {			palette := nil}
     {		else}
     {			begin}
     {}
     {				new(palette);}
     {				FailNIL(palette);}
     {				palette.IPalette(SELF);}
     {				fDrawPaletteView := palette;}
     {}
     {}
     {}
     {			end; }

       aDocState := fDocState;

       DoMakeTextViews(forPrinting); (*used to be at end*)

       DoMakeProofView;

       DoMakeDrawingView(forPrinting);

      end;


      function TCopiDocument.FirstTermAvail: CHAR;
       OVERRIDE;

     {returns first term not already used in shapes}

       var
        found: BOOLEAN;
        firstchar: CHAR;

       procedure Look (shape: TShape);

       begin
        if (shape.fID = IDCircle) then
         begin
          if (firstchar = shape.fName) then
           found := FALSE;
         end;
       end;

      begin

       found := FALSE;

       firstchar := 'a';

       while (not found) and (firstchar <= 't') do {check used to be m}
        begin
         found := TRUE;
         EachVirtualShapeDo(Look); {changed}
         if not found then
          firstchar := Succ(firstchar);

        end;

       if found then
        FirstTermAvail := firstchar
       else
        FirstTermAvail := ' ';
      end;

      function TCopiDocument.FirstVarFree (root: TFormula; var ch: CHAR): BOOLEAN;
       OVERRIDE;

      begin
       FirstVarFree := CopiFirstFreeVar(root, ch); {to allow overiding for different variables}
      end;

      function TCopiDocument.FormulasConstants (root: TFormula): str255;
       OVERRIDE;

      begin
       FormulasConstants := ConstantsInCopiFormula(root);
      end;

      function TCopiDocument.IndividualValid (withoutSelectees: BOOLEAN; thisShape: TShape): BOOLEAN;
       OVERRIDE;

     {This tests whether an individual's name is OK}

       var
        valid: BOOLEAN;
        itsName: CHAR;

       procedure SecondDifferent (secondShape: TShape);

        var
         secondname: CHAR;

       begin
        if valid then
         if (not withoutSelectees) or (not secondShape.fIsSelected) then
          if (secondShape.fID = IDCircle) then
           if secondShape <> thisShape then
            begin
            secondname := secondShape.fName;
            if secondname = itsName then
            valid := FALSE;
            end;
       end;

      begin
       itsName := thisShape.fName;
       valid := (itsName <> gBlank) and (itsName in gCopiConstants); {none blank}
       if valid then
        EachVirtualShapeDo(SecondDifferent);
       IndividualValid := valid; {no two same}
      end;

      procedure TCopiDocument.WriteFormula (root: TFormula);
       OVERRIDE;
     {to Global gOutputStr. }
     {keep outer brackets as often followed by interp}
       var
        aCopiParser: TCopiParser;

      begin
       New(aCopiParser);
       FailNIL(aCopiParser);
       aCopiParser.WriteInner(root, gOutPutStr);
       aCopiParser.Free;
      end;

     {*************TCopiJournalWindow***************}

      procedure TCopiJournalWindow.IRes (itsDocument: TDocument; itsSuperview: TView; var itsParams: Ptr);
       OVERRIDE;

      begin
       inherited IRes(itsDocument, itsSuperview, itsParams);

       fDeriverDocument := TDeriverDocument(itsDocument);
       fInterpretationList := nil;
      end;

      procedure TCopiJournalWindow.ICopiJournalWindow (itsDeriverDocument: TDeriverDocument);

      begin
       SELF.IJournalWindow(itsDeriverDocument);
      end;

     {$S Copi}

      procedure TCopiJournalWindow.DoChoice (origView: TView; itsChoice: integer);
       OVERRIDE;

       var
        templateID: IDType;
        OSdummy: OSErr;

      begin
       templateID := origView.fIdentifier;

       case itsChoice of
        mButtonHit:
         case templateID[4] of
          'A':
           OSdummy := PostEvent(keydown, 137512);
          'B':
           OSdummy := PostEvent(keydown, 139611);

          'D':
           OSdummy := PostEvent(keydown, 133061);
          'E':
           OSdummy := PostEvent(keydown, 142383); {/}
          'F':
           OSdummy := PostEvent(keydown, 132984); {x}
          'G':
           OSdummy := PostEvent(keydown, 135289); {y}
          'H':
           OSdummy := PostEvent(keydown, 132730); {z}
          'I':
           OSdummy := PostEvent(keydown, 143998); {neg}
          'J':
           OSdummy := PostEvent(keydown, 133571); {or}

          'L':
           OSdummy := PostEvent(keydown, 138192); {implic}
          'M':
           OSdummy := PostEvent(keydown, 137389); {equiv}
          'N':
           OSdummy := PostEvent(keydown, 142124); {,}
          'O':
           OSdummy := PostEvent(keydown, 131910); {F}
          'P':
           OSdummy := PostEvent(keydown, 132423); {G}
          'Q':
           OSdummy := PostEvent(keydown, 132168); {H}

          'S':
           OSdummy := PostEvent(keydown, 131169); {A}
          'T':
           OSdummy := PostEvent(keydown, 133986); {B}
          'U':
           OSdummy := PostEvent(keydown, 133219); {C}
          'V':
           OSdummy := PostEvent(keydown, 134994); {R}
          'W':
           OSdummy := PostEvent(keydown, 131411); {S}
          'X':
           OSdummy := PostEvent(keydown, 135508); {T}

          'Z':
           OSdummy := PostEvent(keydown, 144136); {Bs}
          'a':
           OSdummy := PostEvent(keydown, 138845); {]}
          'b':
           OSdummy := PostEvent(keydown, 138537); {)}
          'h':
           OSdummy := PostEvent(keydown, 141916); {\}
          'c':
           SELF.DoThis(1);
          'd':
           SELF.DoThis(2);
          'e':
           SELF.DoThis(3);
          'f':
           SELF.DoThis(4);
          'g':
           SELF.DoThis(5);
          'j':
           SELF.DoThis(6);

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

         end;
        otherwise
         inherited DoChoice(origView, itsChoice);
       end;
      end;

      procedure TCopiJournalWindow.DoParsing (var root: TFormula; var newValuation: TList; var Illformed: BOOLEAN);
       OVERRIDE;
     {VAR dummy:TCopiParser;	}

      begin
       fDeriverDocument.fParser.wffcheck(root, newValuation, Illformed); {Copi}
      end;

     {$S  Copi}

      procedure TCopiJournalWindow.DoThis (selector: integer);
       OVERRIDE;

       var
        dummy: integer;
        OSdummy: OSErr;
        oldend, newend, english_word_list: integer;
        equals, compoundterms, success: BOOLEAN;

       function GentzenToCopi (inChar: char): integer;
       begin
        if inChar = chUniquant then
         GentzenToCopi := -1         (*filter out uniquant*)
        else if (inChar = chAnd) then
         GentzenToCopi := ord(chCopiAnd)    (*change ands*)
        else
         GentzenToCopi := ord(inChar);
       end;

      begin

       case selector of
        1:

     {This should read a selection from some text, and attempt to make one step of a}
     {symbolization of it.   If it can do so and the symbolization is unique it should replace}
     {the selection with the symbolization. If it can do so and the symbolization is not}
     { unique it should replace the selection with a list of symbolizations.   If it cannot}
     {symbolize the selection it should beep and leave the selection as it was.}

     {$IFC IntroToDeriver}
        (*The beginners version translates english instead of endorse-deny*)
         begin
          if CollectionWise then
           if collectGarbage then
            ;

          ReadSelection(fDeriverDocument.fJournalTEView.fHTE);
          GetInput;
          skip(1, peculiarfilter);  (*primes gCurrch, and gLookaheadCh*)
          scan(peculiarfilter);

          getexplist(english_word_list, peculiarfilter);

          if fDeriverDocument.fJournalTEView.Focus then
           ;

          if TRadio(SELF.FindSubView('jou1')).IsOn then
           SymbolizeOneStep(kPropAnalysis, english_word_list, success)
          else
           SymbolizeOneStep(kPredAnalysis, english_word_list, success);

          if success then
           begin
            if fDeriverDocument.fJournalTEView.Focus then
            ;

            gOutputStream := gOutputStream.FilterCharacters(GentzenToCopi);

            gOutputStream.WriteOverSelection(fDeriverDocument.fJournalTEView.fHTE);

            fDeriverDocument.fJournalTEView.SynchView(TRUE);  {true means redraw}

           end
          else
           sysBeep(5);
         end;
     {$ELSEC}

        begin
         gInputStr := strNull;
         gOutPutStr := strNull;
         gIllformed := FALSE;
         ReadSelection(fDeriverDocument.fJournalTEView.fHTE);
                         {This primes inputHdl and indices}

         if Prepare then
          begin
           fDeriverDocument.YouEndorse(gRoot, fDeriverDocument.fvaluation);
           gRoot.DismantleFormula;
           fDeriverDocument.fvaluation.DeleteAll; {this does not free objects, should}
     {                                                               do}
          end;
        end;

     {$ENDC}
        2:

     {$IFC IntroToDeriver}
         begin
          inherited DoThis(selector);
         end;
     {$ELSEC}

        begin
         gInputStr := strNull;
         gOutPutStr := strNull;
         gIllformed := FALSE;
         ReadSelection(fDeriverDocument.fJournalTEView.fHTE);
                         {This primes inputHdl and indices}

         if Prepare then
          begin

           fDeriverDocument.YouDeny(gRoot, fDeriverDocument.fvaluation);
           gRoot.DismantleFormula;
           fDeriverDocument.fvaluation.DeleteAll; {this does not free objects, should}
     {                                                               do}

          end;
        end;

     {$ENDC}
        3:
         begin
          gInputStr := strNull;
          gOutPutStr := strNull;
          gIllformed := FALSE;
          ReadSelection(fDeriverDocument.fJournalTEView.fHTE);
                         {This primes inputHdl and indices}

          if Prepare then
           begin
            SelectionTrue;
            gRoot.DismantleFormula;
            fDeriverDocument.fvaluation.DeleteAll; {this does not free objects, should}
     {                                                               do}

           end;
         end;
        4:
         begin
          gInputStr := strNull;
          ReadSelection(fDeriverDocument.fJournalTEView.fHTE);
          gOutPutStr := strNull;

          SelectionSatisfiable; {check dismantle }

         end;

        5:
         begin
          gInputStr := strNull;
          ReadSelection(fDeriverDocument.fJournalTEView.fHTE);
          gOutPutStr := strNull;

          TMyCopiProofWindow(fDeriverDocument.fProofWindow).StartProof; {check dismantle }

         end;


     {$IFC IntroToDeriver}
        6:
         begin
          inherited DoThis(selector);
         end;
     {$ENDC}

        otherwise
       end;

      end;

      function TCopiJournalWindow.FirstNewConstant (inHere: str255): str255;
       OVERRIDE;

       var
        found: BOOLEAN;
        searchStr: string[1];

      begin
       found := FALSE;
       searchStr := 'a';

       while (searchStr[1] <= 't') and not found do
        begin
         if pos(searchStr, inHere) = 0 then
          found := TRUE
         else
          searchStr[1] := CHAR(ord(searchStr[1]) + 1);

        end;
       FirstNewConstant := searchStr;
      end;

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




