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


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.MenuEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.TPreferences;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

// 1/4/09

/* This almost always wants to have its Deriver document because the undoable proof
edits set the fDirty field of the document */


/*Note that TraceBack uses the justifications to identify the lines. So if your
subclasses use a different justification check you have subclassed the justification*/



public class TProofPanel extends JPanel{   // used to be TProofWindow


  final static int noPremNoConc=1;
  final static int noPremConc=2;
  final static int premNoConc=3;
  final static int premConc=4;
  final static int pfFinished=5;



  final static int kMaxNesting = 10; //no of subprooflevels}


  final static String negEJustification=" "+ chNeg + "E";


  //final static String orIJustification=" "+ chOr + "I";
  //final static String arrowIJustification=" "+ chImplic + "I";

 // final static String andEJustification=" "+ chAnd + "E";
  final static String equivIJustification=" "+ chEquiv + "I";
  final static String equivEJustification=" "+ chEquiv + "E";
  final static String absIJustification=" AbsI";

  static String EGJustification=" EG";
  final static String UIJustification=" UI";
  static String UGJustification=" UG";

  final static String typedRewriteJustification=" Type";
 // final static String typedUniJustification=" Type";

  final static String IEJustification=" =E";
  final static String IIJustification=" =I";

  final static String uniqueIJustification=" !I";
  final static String uniqueEJustification=" !E";

  final static String inductionJustification="Induction";  // no space needed, no line numbers


  final static String questionJustification="?";
  final static String repeatJustification=" R";



  static String fAndEJustification=" "+ chAnd + "E";   // subclass can alter this
  static String fAndIJustification=" "+ chAnd + "I";   // subclass can alter this
  final static String fAssJustification="Ass";  // subclass CANNOT alter this. we use it to identify assumptions

  //if it needs to display differently, use the TProofline drawing routine
  // in particular String transformJustification(String inStr)

  static String fOrIJustification=" "+ chOr + "I";   // subclass can alter this
  static String fCommJustification=" Com";   // subclass can alter this

  static String fImplicEJustification=" "+ chImplic + "E";

  static String fImplicIJustification=" "+ chImplic + "I";     //used in traceBack()
  static String fNegIJustification=" "+ chNeg + "I";
  static String fNegEJustification=" "+ chNeg + "E";
  static String fOrEJustification=" "+ chOr + "E";
  static String fEquivIJustification=" "+ chEquiv + "I";
  static String fEIJustification=" EI";

  static String fTIInput = "Doing TI";


  boolean fTemplate;
  int fProofType;
 // int fRightMargin=200;  //for prooflines Leave this with Model then Beans will save it

  JScrollPane jScrollPane1 = new JScrollPane();  // to contain the prooflist

  public TProofTableModel fModel= new TProofTableModel();  // this is where the data is

  TProofTableView fProofListView = new TProofTableView(this,fModel);//to display the data

  TParser fParser = null;   // note the document has one too, maybe only need one (although each proofline needs a reference to one


  TDeriverDocument fDeriverDocument;
  //TJournal fDeriverDocument;

  //undo helpers
  protected UndoAction fUndoAction = new UndoAction();
  protected RedoAction fRedoAction = new RedoAction();
  protected UndoManager fUndoManager = new UndoManager();
  private UndoableEditListener fListener;     // only allows one, more typical to have list

  private ArrayList fListeners = new ArrayList();  // july 06 implementatation of list of listeners

  static TUndoableProofEdit fLastEdit=null; // this is so we can restrict the undos to 1

  JPanel fInputPane = null; // this takes input in a modeless dialog fashion, visually above the proof

  /*String fInputPalette=strNeg+strAnd+strOr+chImplic+chEquiv+chUniquant+chExiquant+chLambda+chMemberOf
  +chNotMemberOf+chUnion+chIntersection+chPowerSet+chSubset+chEmptySet; */
  
  String fInputPalette="";   // call parser to initialize
  
  JMenuBar fMenuBar = new JMenuBar();
  JMenu fRulesMenu = new JMenu();

  JMenu fAdvancedRulesMenu = new JMenu();

  JMenu fEditMenu = new JMenu();
  JMenuItem rAMenuItem = new JMenuItem();
  JMenuItem tIMenuItem = new JMenuItem();
  JMenuItem negEMenuItem = new JMenuItem();
  JMenuItem negIMenuItem = new JMenuItem();
  JMenuItem andEMenuItem = new JMenuItem();
  JMenuItem andIMenuItem = new JMenuItem();
  JMenuItem theoremMenuItem = new JMenuItem();
  JMenuItem orEMenuItem = new JMenuItem();
  JMenuItem orIMenuItem = new JMenuItem();
  JMenuItem implicEMenuItem = new JMenuItem();
  JMenuItem implicIMenuItem = new JMenuItem();
  JMenuItem equivEMenuItem = new JMenuItem();
  JMenuItem equivIMenuItem = new JMenuItem();
  JMenuItem uIMenuItem = new JMenuItem();
  JMenuItem uGMenuItem = new JMenuItem();
  JMenuItem eIMenuItem = new JMenuItem();
  JMenuItem eGMenuItem = new JMenuItem();

  JMenuItem iIMenuItem = new JMenuItem();
  JMenuItem iEMenuItem = new JMenuItem();

  JMenuItem inductionMenuItem = new JMenuItem();

  JMenuItem uniqueIMenuItem = new JMenuItem();
  JMenuItem uniqueEMenuItem = new JMenuItem();


  JMenuItem rewriteMenuItem = new JMenuItem();

  GridBagLayout gridBagLayout1 = new GridBagLayout();

  JMenuItem newGoalMenuItem = new JMenuItem();

  JMenuItem cutLineMenuItem = new JMenuItem();
  JMenuItem pruneMenuItem = new JMenuItem();
  JMenuItem startAgainMenuItem = new JMenuItem();

  JMenu fWizardMenu = new JMenu();
  JCheckBoxMenuItem tacticsMenuItem = new JCheckBoxMenuItem();
  JMenuItem absurdMenuItem = new JMenuItem();
  JMenuItem writeProofMenuItem = new JMenuItem();
  JMenuItem writeConfirmationMenuItem = new JMenuItem();
  JMenuItem marginMenuItem = new JMenuItem();
  JMenuItem nextLineMenuItem = new JMenuItem();
  JMenuItem deriveItMenuItem = new JMenuItem();


  public String fProofStr=""; /* this is to hold the string version of the premises and conclusion, so that we
  can confirm which proof was proved. Filled in load()*/


 boolean fUseIdentity=false;    // for getting more menu items independently of Preferences

 private boolean fLambda=false;   // Lambda proofs have different menus
                                       // the same proof panel is used for both
                                       // so, on Load, the set Lambda (t/f)
                                       // should be called to get the menus right


 /* This almost always wants to have its Deriver document because the undoable proof
edits set the fDirty field of the document */

 private TProofPanel() {
   enableEvents(AWTEvent.WINDOW_EVENT_MASK);
   initializeParser();
   initializeInputPalette ();


   try {
     jbInit();
   }
   catch(Exception e) {
     e.printStackTrace();
   }

//   fProofListView.addListSelectionListener(new SynchronizeSelections());

   createBlankStart();

 }



 public TProofPanel(TDeriverDocument itsDeriverDocument){
   this();
   fDeriverDocument=itsDeriverDocument;

 }

 public TProofPanel(TDeriverDocument itsDeriverDocument,boolean wantsIdentity){
     fUseIdentity=true;
     enableEvents(AWTEvent.WINDOW_EVENT_MASK);
     initializeParser();
     initializeInputPalette ();


     try {
       jbInit();
     }
     catch(Exception e) {
       e.printStackTrace();
     }

   createBlankStart();

  fDeriverDocument=itsDeriverDocument;

}


 //Component initialization
  private void jbInit() throws Exception  {
    this.setSize(new Dimension(300, 400));
    this.setLayout(gridBagLayout1);

    fRulesMenu.setDoubleBuffered(true);
    fRulesMenu.setText("Rules");
    fRulesMenu.addMenuListener(new TProofPanel_fRulesMenu_menuAdapter(this));
    fRulesMenu.addMouseListener(new TProofPanel_fRulesMenu_mouseAdapter(this));

    //if (TPreferences.fIdentity/*TConstants.identity*/) {

    fAdvancedRulesMenu  = new JMenu();
    fAdvancedRulesMenu.setText("Advanced");
    fAdvancedRulesMenu.addMenuListener(new TProofPanel_fRulesMenu_menuAdapter(this));
    /*the adapter just enables or disables all the rules, so we can use the same one as for the plain rules*/
    //}


    fEditMenu.setText("Edit+");
    fEditMenu.addMouseListener(new TProofPanel_fEditMenu_mouseAdapter(this));
    rAMenuItem.setText("Repeat");
    rAMenuItem.addActionListener(new TProofPanel_rAMenuItem_actionAdapter(this));

    tIMenuItem.addActionListener(new TProofPanel_tIMenuItem_actionAdapter(this));
    tIMenuItem.setText("Assumption");
    negIMenuItem.setText("~I");
    negIMenuItem.addActionListener(new TProofPanel_negIMenuItem_actionAdapter(this));
    negEMenuItem.setText("~E");
    negEMenuItem.addActionListener(new TProofPanel_negEMenuItem_actionAdapter(this));
    andIMenuItem.setText("^I");
    andIMenuItem.addActionListener(new TProofPanel_andIMenuItem_actionAdapter(this));
    andEMenuItem.setText("^E");
    andEMenuItem.addActionListener(new TProofPanel_andEMenuItem_actionAdapter(this));
    theoremMenuItem.setText("Theorem");
    theoremMenuItem.addActionListener(new TProofPanel_theoremMenuItem_actionAdapter(this));

    iIMenuItem.setText("=I");
    iIMenuItem.addActionListener(new TProofPanel_iIMenuItem_actionAdapter(this));
    iEMenuItem.setText("=E");
    iEMenuItem.addActionListener(new TProofPanel_iEMenuItem_actionAdapter(this));

    uniqueIMenuItem.setText("!I");
    uniqueIMenuItem.addActionListener(new TProofPanel_uniqueIMenuItem_actionAdapter(this));
    uniqueEMenuItem.setText("!E");
    uniqueEMenuItem.addActionListener(new TProofPanel_uniqueEMenuItem_actionAdapter(this));

    inductionMenuItem.setText("Induction");
    inductionMenuItem.addActionListener(new TProofPanel_inductionMenuItem_actionAdapter(this));

    rewriteMenuItem.setText("Rewrite Rules");
    rewriteMenuItem.addActionListener(new TProofPanel_rewriteMenuItem_actionAdapter(this));


    orIMenuItem.setText("vI");
    orIMenuItem.addActionListener(new TProofPanel_orIMenuItem_actionAdapter(this));
    orEMenuItem.setText("vE");
    orEMenuItem.addActionListener(new TProofPanel_orEMenuItem_actionAdapter(this));
    implicIMenuItem.setText(chImplic + "I");
    implicIMenuItem.addActionListener(new TProofPanel_implicIMenuItem_actionAdapter(this));
    implicEMenuItem.setText(chImplic +"E");
    implicEMenuItem.addActionListener(new TProofPanel_implicEMenuItem_actionAdapter(this));
    equivIMenuItem.setText(chEquiv +"I");
    equivIMenuItem.addActionListener(new TProofPanel_equivIMenuItem_actionAdapter(this));
    equivEMenuItem.setText(chEquiv +"E");
    equivEMenuItem.addActionListener(new TProofPanel_equivEMenuItem_actionAdapter(this));
    uGMenuItem.setText("UG");
    uGMenuItem.addActionListener(new TProofPanel_uGMenuItem_actionAdapter(this));
    uIMenuItem.setText("UI");
    uIMenuItem.addActionListener(new TProofPanel_uIMenuItem_actionAdapter(this));
    eGMenuItem.setText("EG");
    eGMenuItem.addActionListener(new TProofPanel_eGMenuItem_actionAdapter(this));
    eIMenuItem.setText("EI");
    eIMenuItem.addActionListener(new TProofPanel_eIMenuItem_actionAdapter(this));
    cutLineMenuItem.setText("Cut Proofline");
    cutLineMenuItem.addActionListener(new TProofPanel_cutLineMenuItem_actionAdapter(this));
    newGoalMenuItem.addActionListener(new TProofPanel_newGoalMenuItem_actionAdapter(this));
    newGoalMenuItem.setText("New Subgoal");
    fWizardMenu.setText("Wizard");
    fWizardMenu.addMenuListener(new TProofPanel_fWizardMenu_menuAdapter(this));
    //fWizardMenu.addMouseListener(new TProofPanel_fWizardMenu_mouseAdapter(this));
    tacticsMenuItem.setText("Tactics");
    tacticsMenuItem.addActionListener(new TProofPanel_tacticsMenuItem_actionAdapter(this));
    absurdMenuItem.setText("Absurd I");
    absurdMenuItem.addActionListener(new TProofPanel_absurdMenuItem_actionAdapter(this));
    writeProofMenuItem.setText("Write To Journal");
    writeProofMenuItem.addActionListener(new TProofPanel_writeProofMenuItem_actionAdapter(this));
    writeConfirmationMenuItem.setText("Write Confirmation Code");
    writeConfirmationMenuItem.addActionListener(new
        TProofPanel_writeConfirmationMenuItem_actionAdapter(this));
    marginMenuItem.setText("Set Margin");
    marginMenuItem.addActionListener(new
                                     TProofPanel_marginMenuItem_actionAdapter(this));
    nextLineMenuItem.setText("Next Line");
    nextLineMenuItem.addActionListener(new TProofPanel_nextLineMenuItem_actionAdapter(this));
    deriveItMenuItem.setText("Derive It");
    deriveItMenuItem.addActionListener(new TProofPanel_deriveItMenuItem_actionAdapter(this));
    pruneMenuItem.setText("Prune");
    pruneMenuItem.addActionListener(new TProofPanel_pruneMenuItem_actionAdapter(this));

    startAgainMenuItem.setText("Start Again");
    startAgainMenuItem.addActionListener(new TProofPanel_startAgainMenuItem_actionAdapter(this));

    fMenuBar.add(fRulesMenu);
    if ((TPreferences.fIdentity/*TConstants.identity*/)||
       fUseIdentity||
       TPreferences.fRewriteRules||
       TPreferences.fFirstOrder||
       TPreferences.fSetTheory) {

    fMenuBar.add(fAdvancedRulesMenu);
    }

    fMenuBar.add(fEditMenu);
    fMenuBar.add(fWizardMenu);

    this.add(jScrollPane1,     new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),0,0 /*0, 170*/));

    fMenuBar.setMinimumSize(new Dimension(240,20));  // we don't want the menubar squeezed away

    //2015 was 120,20
    
    
    this.add(fMenuBar,         new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, /*HORIZONTAL,*/ new Insets(0, 0, 0, 0), 0,0/*301, 20*/));

    jScrollPane1.getViewport().add(fProofListView, null);
    fRulesMenu.add(tIMenuItem);
    fRulesMenu.add(negIMenuItem);
    fRulesMenu.add(negEMenuItem);
    fRulesMenu.add(andIMenuItem);
    fRulesMenu.add(andEMenuItem);
    fRulesMenu.add(orIMenuItem);
    fRulesMenu.add(orEMenuItem);
    fRulesMenu.add(implicIMenuItem);
    fRulesMenu.add(implicEMenuItem);
    fRulesMenu.add(equivIMenuItem);
    fRulesMenu.add(equivEMenuItem);
    fRulesMenu.add(uGMenuItem);
    fRulesMenu.add(uIMenuItem);
    fRulesMenu.add(eGMenuItem);
    fRulesMenu.add(eIMenuItem);
    fRulesMenu.add(rAMenuItem);
    if (TPreferences.fUseAbsurd)
      fRulesMenu.add(absurdMenuItem);


    assembleAdvancedMenu();

    fEditMenu.add(fUndoAction);

    fEditMenu.add(fRedoAction);

    fEditMenu.addSeparator();

    fEditMenu.add(cutLineMenuItem);
    fEditMenu.add(pruneMenuItem);
    fEditMenu.add(startAgainMenuItem);
    fEditMenu.addSeparator();
    fEditMenu.add(newGoalMenuItem);
    fEditMenu.addSeparator();
    fEditMenu.add(writeProofMenuItem);
    fEditMenu.add(writeConfirmationMenuItem);
    fEditMenu.addSeparator();
    fEditMenu.add(marginMenuItem);

    fWizardMenu.add(tacticsMenuItem);
    if ((TPreferences.fDerive)&&
    	!TPreferences.fSetTheory&&
    	!TPreferences.fIdentity&&
    	!TPreferences.fFirstOrder){
      fWizardMenu.add(nextLineMenuItem);
      fWizardMenu.add(deriveItMenuItem);
    }

    UndoableEditListener aListener= new UndoableEditListener(){
                           public void undoableEditHappened(UndoableEditEvent e)
                               {
                                 //Remember the edit and update the menus.
                               fUndoManager.addEdit(e.getEdit());
                               fUndoAction.updateUndoState();
                               fRedoAction.updateRedoState();

                               fDeriverDocument.setDirty(true);
                                }
                            };

    addUndoableEditListener(aListener);

  }

public void initializeInputPalette (){   // to allow subclasses to override and use their own
	                                     // symbols
	
	/*2015 getInputPalette() now has parameters	(boolean lambda,boolean modal,
	boolean setTheory) */

	fInputPalette=fParser.getInputPalette(TPreferences.fLambda, TPreferences.fModal, TPreferences.fSetTheory);
	
	
/*2015	fInputPalette=fParser.getInputPalette(); */
//	fInputPalette= your Symbol string
	
}

  /************* Proofline factory ************/

/* we want this to subclass for other types of proof eg Copi */

public TProofline supplyProofline(){
   return
       new TProofline(fParser);
}

/************* End of Proofline factory ************/

 /*******************  Factory *************************/

 TTestNode supplyTTestNode (TParser aParser,TTreeModel aTreeModel){         // so we can subclass
   return
       new TTestNode (aParser,aTreeModel);
 }


/******************************************************/




void assembleAdvancedMenu(){

if (TPreferences.fIdentity||
      fUseIdentity||
      TPreferences.fSetTheory||
      TPreferences.fFirstOrder){

  fAdvancedRulesMenu.add(iIMenuItem);
  fAdvancedRulesMenu.add(iEMenuItem);
  fAdvancedRulesMenu.addSeparator();
  fAdvancedRulesMenu.add(uniqueIMenuItem);
  fAdvancedRulesMenu.add(uniqueEMenuItem);
  fAdvancedRulesMenu.addSeparator();
}

if (TPreferences.fFirstOrder){
  fAdvancedRulesMenu.add(inductionMenuItem);
  fAdvancedRulesMenu.addSeparator();
}
if (TPreferences.fRewriteRules)
  fAdvancedRulesMenu.add(rewriteMenuItem);

fAdvancedRulesMenu.add(theoremMenuItem);

if (TPreferences.fSetTheory){
	 SetTheory setSupport= new SetTheory(this,fParser);
	 setSupport.augmentAdvancedMenu(fAdvancedRulesMenu);
}

}

void initializeParser(){
  fParser=new TParser();
};

 /************** Methods *************/


 public boolean getLambda(){
    return
        fLambda;
  }

public void setLambda(boolean lambda){
          fLambda=lambda;

          checkMenus(fLambda);
}

public void checkMenus(boolean lambda){

}

public boolean getUseIdentity(){
   return
       fUseIdentity;
 }

public void setUseIdentity(boolean use){
         fUseIdentity=use;
}



 void addInputPane(JPanel inputPane){
     if (fInputPane!=null)
        removeInputPane();

      fInputPane=inputPane;
      fInputPane.setAlignmentX((float) 0.0);
      fInputPane.setAlignmentY((float) 0.0);

      this.add(fInputPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
              ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

      fInputPane.setVisible(false);  // experiment, but need it

   /*   fRulesMenu.setEnabled(false);   //we're modal and don't want anything else done
      fAdvancedRulesMenu.setEnabled(false);
      fEditMenu.setEnabled(false);
      fWizardMenu.setEnabled(false);*/

   disableMenus();

  }


public void bugAlert(String label,String message){
    CancelAction ok = new CancelAction();

    ok.putValue(AbstractAction.NAME, "OK");

    JButton okButton = new JButton(ok);

    JButton [] buttons = {okButton};

    JTextField text = new JTextField(message);

    text.selectAll();

    TProofInputPanel inputPane = new TProofInputPanel(label, text
      ,buttons);

    addInputPane(inputPane);

    inputPane.getRootPane().setDefaultButton(okButton);

    fInputPane.setVisible(true);

    text.requestFocus();

   // fInputPane.setVisible(true);   // do I need two of these?


  }

  public void createBlankStart(){
      TProofline newline = supplyProofline();

      newline.fBlankline=true;
      newline.fFormula=null;
      newline.fSelectable=false;
      newline.fHeadlevel=-1;
      newline.fSubprooflevel=-1;

      fModel.addToHead(0,newline);

      fProofType=noPremNoConc;  //new Nov 03

  /*    {Iterator e = fHead.iterator();  //CheckLevels
       TProofline aProofline = null;

        while (e.hasNext()){
          aProofline=(TProofline)(e.next());
          aProofline.fSubprooflevel=-1;
          aProofline.fHeadlevel=-1;
        }

        e=fTail.iterator();

        while (e.hasNext()){
          aProofline=(TProofline)(e.next());
          aProofline.fSubprooflevel=-1;
          aProofline.fHeadlevel=-1;
        }                             // end of CheckLevels

      }                                do I need this? */

    }

  /*

     procedure TProofWindow.CreateBlankStart; {Creates a blankline start}

      procedure CheckLevels (item: TObject);

      begin
       TProofLine(item).fSubprooflevel := -1;
       TProofLine(item).fHeadlevel := -1;

      end;

      var
       newline: TProofLine;

     begin
      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 0;
        fBlankline := TRUE;
        fSelectable := false;
        fjustification := '';
        fSubprooflevel := -1;
        fHeadlevel := -1;
       end;

      fHead.InsertFirst(newline);

      fHead.Each(CheckLevels);
      fTail.Each(CheckLevels);

      newline := nil;
     end;


     */

  public void dismantleProof(){

      //System.out.print("dismantle needs to commit undo command");

      if (fLastEdit!=null){ // kill the previous one, we don't want to undo a proof that is gone
        fLastEdit.die();
        fUndoAction.updateUndoState();
        fRedoAction.updateRedoState();
      }


      removeInputPane();

    }


   void doSetUpEditMenu(){


     boolean cutOK = false;

     TProofline firstline = fProofListView.oneSelected();

     if ( (firstline != null) && fModel.lineCutable(firstline, null)) {
       cutOK = true;
     }

   cutLineMenuItem.setEnabled(cutOK);


   if (newGoalPossible())
     newGoalMenuItem.setEnabled(true);
   else
     newGoalMenuItem.setEnabled(false);


   int size= fModel.getProofSize();

   if ((size>1)||((size==1)&&(!fModel.getHeadLastLine().fBlankline))){

     writeProofMenuItem.setEnabled(true);

     pruneMenuItem.setEnabled(true);
    startAgainMenuItem.setEnabled(true);


   }
   else{
     writeProofMenuItem.setEnabled(false); // don't write singleton blank line

     pruneMenuItem.setEnabled(false);
     startAgainMenuItem.setEnabled(false);
   }

   if (fModel.getHeadSize()!=0&&fModel.getTailSize()!=0)
        newGoalMenuItem.setEnabled(true);
   else
     newGoalMenuItem.setEnabled(false);

if (fModel.finishedAndNoAutomation())
     writeConfirmationMenuItem.setEnabled(true);
else
     writeConfirmationMenuItem.setEnabled(false);  // don't write singleton blank line




}

 void doSetUpWizardMenu(){

 tacticsMenuItem.setSelected(fTemplate);

/*if (!TConstants.DEBUG){  */
//  nextLineMenuItem.setEnabled(false);
//deriveItMenuItem.setEnabled(false);/*}
/*else{*/

/*if (TProofQuiz.fNumOpen!=0){  */
//  nextLineMenuItem.setEnabled(false);  // disable if examining
//   deriveItMenuItem.setEnabled(false);
/*}
else { */

  nextLineMenuItem.setEnabled(fModel.getTailSize() != 0); // attempt derive iff there is a conclusion
  deriveItMenuItem.setEnabled(fModel.getTailSize() != 0);
//}
/*}*/



}



void doSetUpRulesMenu(){     /* does advanced rules as well*/
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


/*some rules are available independently of whether tactics (ie fTemplate) is set
  we'll deal with those first

 rA, NegE, AndE, EquivE*/

if (oneSelected
    &&totalSelected==1)
  rAMenuItem.setEnabled(true);
else
  rAMenuItem.setEnabled(false);

if ((oneSelected)
    &&totalSelected==1
    && (fParser.isNegation(selectedFormula)))
  negEMenuItem.setEnabled(true);
else
  negEMenuItem.setEnabled(false);

if ((oneSelected)
    &&totalSelected==1
    && (fParser.isAnd(selectedFormula)))
  andEMenuItem.setEnabled(true);
else
  andEMenuItem.setEnabled(false);

if ((oneSelected)
    &&totalSelected==1)
  rewriteMenuItem.setEnabled(true);
else
  rewriteMenuItem.setEnabled(false);

if (twoSelected
    &&totalSelected==2
    &&(( (fParser.isImplic(selectedFormula)) &&
            (selectedFormula.equalFormulas(selectedFormula.fLLink,
                                              secondSelectedFormula)))
      || (fParser.isImplic(secondSelectedFormula)) &&
                  (selectedFormula.equalFormulas(secondSelectedFormula.fLLink,
                                                    selectedFormula)))

         )
          implicEMenuItem.setEnabled(true);
       else
          implicEMenuItem.setEnabled(false);



if ((oneSelected)
            &&totalSelected==1
            && (TParser.isEquiv(selectedFormula)))
  equivEMenuItem.setEnabled(true);
else
  equivEMenuItem.setEnabled(false);

if ((oneSelected)
    &&totalSelected==1
    && (TParser.isUniquant(selectedFormula)))
  uIMenuItem.setEnabled(true);
else
  uIMenuItem.setEnabled(false);


if ((oneSelected)
    &&totalSelected==1
    && (TParser.isUnique(selectedFormula)))
  uniqueEMenuItem.setEnabled(true);
else
  uniqueEMenuItem.setEnabled(false);


if (twoSelected
    &&totalSelected==2
    && (TParser.isEquality(selectedFormula)
        ||  TParser.isEquality(secondSelectedFormula)))
  iEMenuItem.setEnabled(true);
else
  iEMenuItem.setEnabled(false);


  /*

       JMenuItem rAMenuItem = new JMenuItem();     DONE
       JMenuItem tIMenuItem = new JMenuItem();
       JMenuItem negEMenuItem = new JMenuItem();   DONE
       JMenuItem negIMenuItem = new JMenuItem();
       JMenuItem andEItem5 = new JMenuItem();      DONE
       JMenuItem andIMenuItem = new JMenuItem();
       JMenuItem theoremMenuItem = new JMenuItem();DONE (always available)
       JMenuItem orEMenuItem = new JMenuItem();
       JMenuItem orIMenuItem = new JMenuItem();
       JMenuItem implicEMenuItem = new JMenuItem(); DONE
       JMenuItem implicIMenuItem = new JMenuItem();
       JMenuItem equivEMenuItem = new JMenuItem();  DONE
       JMenuItem equivIMenuItem = new JMenuItem();
       JMenuItem uIMenuItem = new JMenuItem();      DONE
       JMenuItem uGMenuItem = new JMenuItem();
       JMenuItem eIMenuItem = new JMenuItem();
       JMenuItem eGMenuItem = new JMenuItem();


       iEMenuItem          DONE

    */








/*end of 'indpendent' rules */



  if (fTemplate){

     TFormula conclusion =findNextConclusion();

     tIMenuItem.setEnabled(false);

     if ((conclusion!=null)&&(fParser.isNegation(conclusion)))
          negIMenuItem.setEnabled(true);
        else
          negIMenuItem.setEnabled(false);

     if ((conclusion!=null)&&(noneSelected))   // we are going to allow ~E as a tactic
        negEMenuItem.setEnabled(true);
      //we don't need an else here because it will have been
      //enabled or disabled above generically;



     if ((conclusion!=null)&&(fParser.isAnd(conclusion)))
       andIMenuItem.setEnabled(true);
     else
       andIMenuItem.setEnabled(false);

     if ((selection != null)
        &&fParser.isOr(selectedFormula)
        &&(conclusion!=null))
      orEMenuItem.setEnabled(true);
    else
      orEMenuItem.setEnabled(false);

     if ((conclusion!=null)&&(fParser.isOr(conclusion)))
        orIMenuItem.setEnabled(true);
     else
        orIMenuItem.setEnabled(false);


     if ((conclusion!=null)&&(fParser.isImplic(conclusion)))
       implicIMenuItem.setEnabled(true);
     else
       implicIMenuItem.setEnabled(false);



    if ((conclusion!=null)&&(TParser.isEquiv(conclusion)))
       equivIMenuItem.setEnabled(true);
     else
       equivIMenuItem.setEnabled(false);

     if ((conclusion!=null)&&(TParser.isUniquant(conclusion)))
            uGMenuItem.setEnabled(true);
          else
            uGMenuItem.setEnabled(false);

     if ((conclusion!=null)&&(TParser.isExiquant(conclusion)))
        eGMenuItem.setEnabled(true);
     else
        eGMenuItem.setEnabled(false);




      if ((conclusion!=null)&&(TParser.isUnique(conclusion)))
  uniqueIMenuItem.setEnabled(true);
else
  uniqueIMenuItem.setEnabled(false);





     if (oneSelected
         &&totalSelected==1
         &&(conclusion!=null)
         &&(TParser.isExiquant(selectedFormula)))
        eIMenuItem.setEnabled(true);
     else
        eIMenuItem.setEnabled(false);

      if ((conclusion!=null)&&(TFormula.equalFormulas(conclusion,TFormula.fAbsurd)))
       absurdMenuItem.setEnabled(true);
     else
       absurdMenuItem.setEnabled(false);


        /*


               JMenuItem tIMenuItem = new JMenuItem();  DONE

               JMenuItem negIMenuItem = new JMenuItem(); DONE

               JMenuItem andIMenuItem = new JMenuItem(); DONE

               JMenuItem orEMenuItem = new JMenuItem(); DONE
               JMenuItem orIMenuItem = new JMenuItem(); DONE

               JMenuItem implicIMenuItem = new JMenuItem(); DONE

               JMenuItem equivIMenuItem = new JMenuItem();  DONE

               JMenuItem uGMenuItem = new JMenuItem();  DONE
               JMenuItem eIMenuItem = new JMenuItem();  DONE
               JMenuItem eGMenuItem = new JMenuItem();  DONE

               absurd DONE


            */



  }
  else{  // no template

    TProofline headLastLine=fModel.getHeadLastLine();
    TProofline lastAssumption=fModel.findLastAssumption();


    if ((headLastLine!=null)&&(headLastLine.fSubprooflevel > kMaxNesting))
      tIMenuItem.setEnabled(false);
    else
      tIMenuItem.setEnabled(true);

    negIMenuItem.setEnabled(negIPossible(lastAssumption,
                     oneSelected,
                        twoSelected,
                        selectedFormula,
                        secondSelectedFormula,
                        totalSelected));
  //FEB 08

/*
    if ((lastAssumption!=null)&&
       (lastAssumption.fFormula!=null)   // not needed, blankstart
       &&((oneSelected
           &&totalSelected==1
           &&(TFormula.equalFormulas(selectedFormula,TFormula.fAbsurd)))
         ||(twoSelected
            &&totalSelected==2
            &&
            (TFormula.formulasContradict(selectedFormula,secondSelectedFormula)) ) ))
      negIMenuItem.setEnabled(true);
    else
      negIMenuItem.setEnabled(false);*/

    if (canDoVE(new FiveLines()))
       orEMenuItem.setEnabled(true);
    else
       orEMenuItem.setEnabled(false);

    if ((oneSelected)&&totalSelected==1) {

      orIMenuItem.setEnabled(true);
      uGMenuItem.setEnabled(true);
      eGMenuItem.setEnabled(true);
    }
    else {

      orIMenuItem.setEnabled(false);
      uGMenuItem.setEnabled(false);
      eGMenuItem.setEnabled(false);
    }

    if (oneSelected
    &&(totalSelected==1)
    &&(lastAssumption!=null)
             )
              implicIMenuItem.setEnabled(true);
           else
              implicIMenuItem.setEnabled(false);


    if (canDoEquivI(new FiveLines()))
           equivIMenuItem.setEnabled(true);
    else
      equivIMenuItem.setEnabled(false);


    if ((oneSelected&&totalSelected==1)       // we don't want subproofs selected
        || (twoSelected&&totalSelected==2)) {

      andIMenuItem.setEnabled(true);
    }
    else {

      andIMenuItem.setEnabled(false);
    }


    if ((twoSelected)
        &&(totalSelected==2)
        &&(TFormula.formulasContradict(selectedFormula,secondSelectedFormula)))
       absurdMenuItem.setEnabled(true);
    else
       absurdMenuItem.setEnabled(false);



     if (twoSelected
         &&(lastAssumption!=null)
         &&(totalSelected==2)
         &&(TParser.isExiquant(selectedFormula)
         && selectedFormula.equalFormulas(selectedFormula.scope(),lastAssumption.fFormula)))
          eIMenuItem.setEnabled(true);
       else
          eIMenuItem.setEnabled(false);


        if ((oneSelected)
                 &&totalSelected==1
                 && (selectedFormula.canAbbrevUnique()))
       uniqueIMenuItem.setEnabled(true);
     else
       uniqueIMenuItem.setEnabled(false);




   }  // end

/************ different set up for template ***********/

   /*


                JMenuItem tIMenuItem = new JMenuItem();  DONE*

               JMenuItem negIMenuItem = new JMenuItem(); DONE*

               JMenuItem andIMenuItem = new JMenuItem(); DONE*

               JMenuItem orEMenuItem = new JMenuItem(); DONE*
               JMenuItem orIMenuItem = new JMenuItem(); DONE*

               JMenuItem implicIMenuItem = new JMenuItem(); DONE*

               JMenuItem equivIMenuItem = new JMenuItem();  DONE*

               JMenuItem uGMenuItem = new JMenuItem();  DONE*
               JMenuItem eIMenuItem = new JMenuItem();  DONE*
               JMenuItem eGMenuItem = new JMenuItem();  DONE*

               absurd DONE*



             */


}

void display(String aString){

  if (fInputPane!=null)
    ((TProofInputPanel)fInputPane).setText1(aString);
  


}

void prependIfNotThere(TProofline line, ArrayList list){
  if (!list.contains(line))
    list.add(0,line);

}

void assembleLinesToCut(TProofline startLine, ArrayList garbageLines){
  if ((startLine != null)&&fModel.lineCutable(startLine, null)){

    prependIfNotThere(startLine, garbageLines);

    TProofline predecessor = fModel.predecessor(startLine);

    if (predecessor != null) {

      /*if line is a subgoal, and the linebefore is therefore a ?, need to cut that as well */

      if (predecessor.fJustification.equals(questionJustification))
        prependIfNotThere(predecessor, garbageLines);

        /*if line is the conclusion of a subproof, cut all subproof */

      if (predecessor.fBlankline) {
        TProofline twoBefore = fModel.predecessor(predecessor);

        if (twoBefore != null) { // we don't cut the first line of proof

          prependIfNotThere(predecessor, garbageLines); //the blankline

          if (startLine.fJustification.equals(fNegIJustification) ||
              startLine.fJustification.equals(fImplicIJustification) ||
              startLine.fJustification.equals(fEIJustification) ||
              startLine.fJustification.equals(fOrEJustification) ||
              startLine.fJustification.equals(equivIJustification)) {

            //we cut every line back to, and inclusive, to previous assumption

            TProofline lastAssumption = fModel.
                findLastAssumptionOfPriorSubProof(startLine,
                startLine.fSubprooflevel + 1);

            TProofline toCut = fModel.predecessor(predecessor);

            while (toCut != null && toCut != lastAssumption) {
              prependIfNotThere(toCut, garbageLines);
              toCut = fModel.predecessor(toCut);
            }

            prependIfNotThere(lastAssumption, garbageLines);

            // for those rules with two subproofs we have to cut the other one

            if (startLine.fJustification.equals(fOrEJustification) ||
                startLine.fJustification.equals(equivIJustification)) {

              int endIndex = fModel.indexOfLineno(startLine.fSecondjustno) +
                  1; //we want the blankline

              //there is a mistake in the original Pascal here using fFirstjustno

              TProofline endOfSecond = (TProofline) (fModel.getElementAt(
                  endIndex));

              lastAssumption = fModel.findLastAssumptionOfPriorSubProof(
                  endOfSecond, endOfSecond.fSubprooflevel + 1);

              toCut = endOfSecond;

              while (toCut != null && toCut != lastAssumption) {
                prependIfNotThere(toCut, garbageLines);
                toCut = fModel.predecessor(toCut);
              }

              prependIfNotThere(lastAssumption, garbageLines);

            }

          }
        }

      }
    }
  }


}

void doCutProofline(){   // this assumes that this command is enabled only if the line is actually cutable

   TProofline firstline=fProofListView.oneSelected();

   if ((firstline != null)&&fModel.lineCutable(firstline, null)){

     TUndoableProofEdit newEdit = new TUndoableProofEdit();

     assembleLinesToCut(firstline,newEdit.fGarbageLines);

     newEdit.doCutLinesEdit();
   }
}

 /*

  function TProofWindow.PrepareCutLine: TCommand;

    var
     aCutLineCommand: TCutLineCommand;
     firstline, linebefore: TProofline;
     lineno, lastTIIndex, endOfSubIndex: integer;

   begin
    if fTextList.OneSelected(firstline) then
     begin
      New(aCutLineCommand);
      FailNil(aCutLineCommand);
      aCutLineCommand.ICutLineCommand(cCutProofLine, SELF);

      aCutLineCommand.fGarbagelines.InsertLast(firstline);

                 {there always is a linebefore as firstline cannot be first}

      aCutLineCommand.fBadBottom := fHead.GetSameItemNo(firstline);

      linebefore := TProofline(fHead.At(aCutLineCommand.fBadBottom - 1));

      if linebefore.fFormula.fInfo = '?' then
       begin
        aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
        aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;
       end;
                 {remove subgoal}

      if linebefore.fBlankline and (aCutLineCommand.fBadBottom > 2) then  (*dont bother with first*)
       begin
        aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
        aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;

        if (firstline.fjustification = ' ~I') or (firstline.fjustification = ' �I') or (firstline.fjustification = ' EI') or (firstline.fjustification = ' �I') or (firstline.fjustification = ' �E') then
         begin
         lineno := LastAssLineno(aCutLineCommand.fBadBottom, lastTIindex);

         while linebefore.fLineno > lineno do  (*takes out entire subproof*)
         begin
         linebefore := TProofline(fHead.At(aCutLineCommand.fBadBottom - 1));
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
         aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;
         end;
         end;

        if (firstline.fjustification = ' �I') or (firstline.fjustification = ' �E') then
         begin
         if (firstline.fjustification = ' �I') then
         endOfSubIndex := (IndexOfLineno(firstline.fFirstJustno) + 1)  (*includes blankline*)
         else
         endOfSubIndex := (IndexOfLineno(firstline.fFirstJustno) + 1);  (*includes blankline*)


         lineno := LastAssLineno(endOfSubIndex, lastTIindex);
         aCutLineCommand.fBadBottom := endOfSubIndex;

         linebefore := TProofline(fHead.At(endOfSubIndex));  (*blankline*)
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
                                     {aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;}

         while linebefore.fLineno > lineno do  (*takes out entire subproof*)
         begin
         linebefore := TProofline(fHead.At(aCutLineCommand.fBadBottom - 1));
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
         aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;
         end;
         end;

       end;
                 {Cut subproof}

      PrepareCutLine := aCutLineCommand;
     end
    else
     PrepareCutLine := gNoChanges;

   end;


*/


 void  doStartAgain(){
   TUndoableProofEdit newEdit = new TUndoableProofEdit();   // this copies old lines

   if (fProofStr!=null)          // record of this proof as a string
     startProof(fProofStr);

   newEdit.doEdit();            // does not do any editing but kills last edit and allows undo
 }

 void doPrune(){   // this assumes that this command is enabled only if the line is actually cutable

   int searchIndex = fModel.getHeadSize()-1;
   TProofline lineToCut;

   TUndoableProofEdit newEdit = new TUndoableProofEdit();

   if (fModel.getTailSize()==0)
     searchIndex-=1;              // don't cut the last line

/*we'll go backward through the proof cutting the lines we can. Notice that
when a line is cut the size of the proof changes (that does not matter in this algortihm)*/

   while (searchIndex>0){         // don't cut the first line

     lineToCut=fModel.getHeadLine(searchIndex);

     if (fModel.lineCutable(lineToCut,newEdit.fGarbageLines))
       assembleLinesToCut(lineToCut,newEdit.fGarbageLines);

    searchIndex-=1;

   }

     if (newEdit.fGarbageLines.size()>0)
      newEdit.doCutLinesEdit();


}


 /*

  {$S ADoCommand}
   function TProofWindow.PreparePrune: TCommand;

    var
     aCutLineCommand: TCutLineCommand;
     firstline, linebefore: TProofline;
     headIndex, lineno, lastTIIndex, endOfSubIndex: integer;

   begin
    New(aCutLineCommand);
    FailNil(aCutLineCommand);
    aCutLineCommand.ICutLineCommand(cPrune, SELF);

    headIndex := fHead.fSize;

    if fTail.fSize = 0 then
     headIndex := headIndex - 1; (*don't cut last line*)

    while headIndex > 1 do    (*dont cut firstline*)
     begin
      firstline := TProofline(fHead.At(headIndex));
      if not firstline.fBlankline then
       if not (firstline.fFormula.fInfo = '?') then
        if LineCutable(firstline, aCutLineCommand.fGarbagelines) then
         if aCutLineCommand.fGarbageLines.GetSameItemNo(firstline) = 0 then  {dont}
  {							cut same line twice-- cutting subproofs adds lots of lines}
  {							and then the iteration scans them again}
         begin
         aCutLineCommand.fGarbagelines.InsertFirst(firstline);

                                                        {there always is a linebefore as firstline cannot be first}

         aCutLineCommand.fBadBottom := fHead.GetSameItemNo(firstline);

         linebefore := TProofline(fHead.At(aCutLineCommand.fBadBottom - 1));

         if linebefore.fFormula.fInfo = '?' then
         begin
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
         aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;
         end;
                                                 {remove subgoal}


         if linebefore.fBlankline and (aCutLineCommand.fBadBottom > 2) then  (*dont bother with first*)
         begin
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
         aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;

         if (firstline.fjustification = ' ~I') or (firstline.fjustification = ' �I') or (firstline.fjustification = ' EI') or (firstline.fjustification = ' �I') or (firstline.fjustification = ' �E') then
         begin
         lineno := LastAssLineno(aCutLineCommand.fBadBottom, lastTIindex);

         while linebefore.fLineno > lineno do  (*takes out entire subproof*)
         begin
         linebefore := TProofline(fHead.At(aCutLineCommand.fBadBottom - 1));
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
         aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;
         end;
         end;

         if (firstline.fjustification = ' �I') or (firstline.fjustification = ' �E') then
         begin
         if (firstline.fjustification = ' �I') then
         endOfSubIndex := (IndexOfLineno(firstline.fFirstJustno) + 1)  (*includes blankline*)
         else
         endOfSubIndex := (IndexOfLineno(firstline.fFirstJustno) + 1);  (*includes blankline*)


         lineno := LastAssLineno(endOfSubIndex, lastTIindex);
         aCutLineCommand.fBadBottom := endOfSubIndex;

         linebefore := TProofline(fHead.At(endOfSubIndex));  (*blankline*)
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
                                     {aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;}

         while linebefore.fLineno > lineno do  (*takes out entire subproof*)
         begin
         linebefore := TProofline(fHead.At(aCutLineCommand.fBadBottom - 1));
         aCutLineCommand.fGarbagelines.InsertFirst(linebefore);
         aCutLineCommand.fBadBottom := aCutLineCommand.fBadBottom - 1;
         end;
         end;

         end;
                 {Cut subproof}

         end;

      headIndex := headIndex - 1;
     end;

    if aCutLineCommand.fGarbagelines.fSize > 0 then
     PreparePrune := aCutLineCommand
    else
     begin
      aCutLineCommand.fGarbageLines.Free;
      aCutLineCommand.Free;
      PreparePrune := gNoChanges;
     end;

   end;


*/

 String posForksAsString(){

   ArrayList allNegations=fModel.listNegationSubFormulasInProof();

   String outStr="";

   Iterator iter = allNegations.iterator();


   while (iter.hasNext()) {
     TFormula aFormula = (TFormula) iter.next();

     outStr= outStr+fParser.writeFormulaToString(aFormula.fRLink)+ chBlank;  //removing the negation
   }
return
       outStr;
 }

TFormula findNextConclusion(){

  TProofline conclusion= fModel.getNextConclusion();

  if (conclusion!=null)
    return
        conclusion.fFormula;
  else
    return
        null;


}


/*

  function TProofWindow.FindTailFormula: TFormula;
  begin
   FindTailFormula := nil;
   if SELF.fTail.fSize <> 0 then
    FindTailFormula := TProofline(SELF.fTail.At(2)).fFormula; {first is "?"}
  end;


*/



public TProofTableModel getModel(){
  return
      fModel;
}


public boolean getTemplate(){
  return
      fTemplate;
}


public String getProofStr(){
  return
      fProofStr;
}



public void reconstructProof(boolean template,TProofTableModel aModel){  //from File

  fTemplate=template;
  fModel=aModel;

  fProofListView= new TProofTableView(this,fModel);   // should have this in an init routine

  fModel.resetSelectables();  //need to do this because we don't save the selection fields and the defaults are not enough.

  // fProofListView.addListSelectionListener(new SynchronizeSelections());
  jScrollPane1.getViewport().add(fProofListView, null);



  fProofListView.repaint();



  /*

     if tailNum <> 0 then
         begin
          if not TProofLine(fHeadFromFile.First).fBlankline then
          fProofTypeFromFile := PREMCONC
          else
          fProofTypeFromFile := NOPREMCONC;
         end
        else if not TProofLine(fHeadFromFile.First).fBlankline then
         fProofTypeFromFile := PREMNOCONC;
       end;


  }*/
}


  /*********************** Input Pane **********************************/


void removeBugAlert(){
  removeInputPane();
}

void enableMenus(){
  fRulesMenu.setEnabled(true);
        fAdvancedRulesMenu.setEnabled(true);
        fEditMenu.setEnabled(true);
        fWizardMenu.setEnabled(true);

}

      void disableMenus(){
        fRulesMenu.setEnabled(false);
              fAdvancedRulesMenu.setEnabled(false);
              fEditMenu.setEnabled(false);
              fWizardMenu.setEnabled(false);

}

void removeInputPane(){
    if (fInputPane!=null){
      fInputPane.setVisible(false);
       this.remove(fInputPane);

        fInputPane=null;

  /*      fRulesMenu.setEnabled(true);
        fAdvancedRulesMenu.setEnabled(true);
        fEditMenu.setEnabled(true);
        fWizardMenu.setEnabled(true);*/

  enableMenus();
    }
  }



 public void removeAdvancedMenu(){
    fMenuBar.remove(fAdvancedRulesMenu);
}

public void removeDeriveSupport(){
	   fWizardMenu.remove(nextLineMenuItem);
           fWizardMenu.remove(deriveItMenuItem);

           fAdvancedRulesMenu.remove(theoremMenuItem);
}

public void removeRewriteMenuItem(){
	fAdvancedRulesMenu.remove(rewriteMenuItem);

}

public void removeConfCodeWriter(){
  fEditMenu.remove(writeConfirmationMenuItem);
}


public void removeNewGoalMenuItem(){
	fEditMenu.remove(newGoalMenuItem);
}


public void removeMarginMenuItem(){
  fEditMenu.remove(marginMenuItem);

}

public void removePruneMenuItem(){
        fEditMenu.remove(pruneMenuItem);
}

public void removeWriteProofMenuItem(){
  fEditMenu.remove(writeProofMenuItem);

}







  public void setTemplate(boolean template){

        fTemplate=template;
  }

public boolean usingInputPane(){
  return
      fInputPane!=null;
}


/***************************Setting up proof *********************************************/



  public void initProof(){
      fProofType = noPremNoConc;
   //   fTextlines = 0;

      fModel.clear();  //check
    //  fTail.clear();

      fTemplate=false;

     // gIllformed := false; don't forget this
    }


  /*
       procedure TProofWindow.InitProof;

     begin

      fProoftype := NOpremNOconc;
      fTextlines := 0;

      fHead.DeleteAll; {check garbage}
      fTail.DeleteAll; {check garbage}

      fTemplate := false;
      gIllformed := false;
     end;


     */



void setListSize(){

  }

  public class CancelAction extends AbstractAction{

       public CancelAction(){
         putValue(NAME, "Cancel");
       }

        public void actionPerformed(ActionEvent ae){


          removeInputPane();
        }

      }


public void startLambdaProof(String inputStr){    // a stub that needs to be overridden by subclass





    }

public void startProof(String inputStr){    // a stub that needs to be overridden by subclass

    }



/*

class SynchronizeSelections implements ListSelectionListener{
    public void valueChanged (ListSelectionEvent e){

      //this needs to unselect unselectable lines, and synchronize lines which are selected between
      //the prooflines in the data model and the java selection model. Actually we don't do second task

      if ((!e.getValueIsAdjusting())||(e.getFirstIndex()==-1))
        return;   // nothing happening

      for (int i=e.getFirstIndex();i<=e.getLastIndex();i++){
        TProofline theProofline= (TProofline)fModel.getElementAt(i);

        if (fProofListView.isSelectedIndex(i)&&
            (!theProofline.fSelectable &&
             !theProofline.fSubProofSelectable ))
        {
     //     System.out.println("calling remove");
          fProofListView.removeSelectionInterval(i, i); //unselect it
        }

      }

  //    System.out.println("Synchroniz selection");

    }
  }

*/


  /************************ Rule of DoTheorem **********************************/



    public class TheoremAction extends AbstractAction{
      JTextComponent fText;
      TFormula fRoot=null;

     /*We have here to get two things out of the User:- the root of new formula, and its
      justification. So when initialized fRoot is set to null. Then, when we get the
      root it is set to a value, and we look for the justification.  */


       public TheoremAction(JTextComponent text, String label){
         putValue(NAME, label);

         fText=text;
       }

        public void actionPerformed(ActionEvent ae){


        if (fRoot==null){
          boolean useFilter = true;
          ArrayList dummy = new ArrayList();

          String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

          TFormula root = new TFormula();
          StringReader aReader = new StringReader(aString);
          boolean wellformed;

          wellformed = fParser.wffCheck(root, /*dummy,*/ aReader);

          if (!wellformed) {
            String message = "The string is illformed." +
                (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

            //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

            fText.setText(message);
            fText.selectAll();
            fText.requestFocus();
          }
          else {
            fRoot=root;      // found root move to second stage

            String message = "Brief annotation? eg. Theorem 1";
            fText.setText(message);
            fText.selectAll();
            fText.requestFocus();
          }
        }

        else {               //we have a root, getting justification

          String justification=fText.getText();

          if (justification.equals("Brief annotation? eg. Theorem 1"))
            justification="Theorem";   // correcting thoughtless input

          TProofline newline = supplyProofline();

           newline.fFormula = fRoot;
           newline.fJustification = justification;
            newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

            TUndoableProofEdit newEdit = new TUndoableProofEdit();
            newEdit.fNewLines.add(newline);
            newEdit.doEdit();

            removeInputPane();
          }

      }

    }


    /************************ Rule of doInduction **********************************/



      public class InductionAction extends AbstractAction{
        JTextComponent fText;
        TFormula fRoot=null;

        TFormula nFormula = new TFormula(TFormula.variable,"x",null,null);

       /*We have here to get  the root of new formula, whicch must contain free x  */


         public InductionAction(JTextComponent text, String label){
           putValue(NAME, label);

           fText=text;
         }

          public void actionPerformed(ActionEvent ae){


          if (fRoot==null){
            boolean useFilter = true;
            ArrayList dummy = new ArrayList();

            String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

            TFormula root = new TFormula();
            StringReader aReader = new StringReader(aString);
            boolean wellformed;

            wellformed = fParser.wffCheck(root, /*dummy,*/ aReader);

            if (!wellformed) {
              String message = "The string is illformed." +
                  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

              //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

              fText.setText(message);
              fText.selectAll();
              fText.requestFocus();
            }
            else {
                   // found root move to, checking that it contains n

              if (root.numOfFreeOccurrences(nFormula)==0){
                String message = "The inductive formula must contain x free.";
                fText.setText(message);
                fText.selectAll();
                fText.requestFocus();
              }
              else{            // good to go
                fRoot=root;


                TProofline newline = supplyProofline();

                 newline.fFormula = makeInductionFormula(fRoot,nFormula);
                 newline.fJustification = inductionJustification;
                  newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

                  TUndoableProofEdit newEdit = new TUndoableProofEdit();
                  newEdit.fNewLines.add(newline);
                  newEdit.doEdit();

              removeInputPane();

              }
            }
          }

          else {               //we have a root, getting justification

            String justification=fText.getText();

            if (justification.equals("Brief annotation? eg. Theorem 1"))
              justification="Theorem";   // correcting thoughtless input


            }

        }

        /// NEED TO USE CONSTANTS HERE

        TFormula makeInductionFormula(TFormula uniForm, TFormula inductVar){
          TFormula baseForm=uniForm.copyFormula();                   // uniform is P(n)
          TFormula zeroForm=new TFormula(TFormula.functor,"0",null,null);

          baseForm.subTermVar(baseForm,zeroForm,inductVar);       // P(0)


          TFormula succTerm=new TFormula(TFormula.functor,"'",null,null);

          succTerm.appendToFormulaList(inductVar.copyFormula());     //n'

          TFormula succForm=uniForm.copyFormula();

          succForm.subTermVar(succForm,succTerm,inductVar);       // P(n')

          TFormula hookForm = new TFormula(TFormula.binary,          // P(n)->P(n')
                                           String.valueOf(chImplic),
                                           uniForm.copyFormula(),
                                           succForm);

          TFormula quantForm = new TFormula(TFormula.quantifier,     // Alln(P(n)->P(n'))
                                           String.valueOf(chUniquant),
                                           inductVar.copyFormula(),
                                           hookForm);

          TFormula anotherQuantForm = new TFormula(TFormula.quantifier,     // Alln(P(n))
                                          String.valueOf(chUniquant),
                                          inductVar.copyFormula(),
                                          uniForm);

          TFormula andForm = new TFormula(TFormula.binary,          // P(0)^Alln(P(n)->P(n'))
                                 String.valueOf(chAnd),
                                 baseForm.copyFormula(),
                                 quantForm);



          return
              new TFormula(TFormula.binary,          // {P(0)^Alln(P(n)->P(n'))] -> Alln(P(n))
                                         String.valueOf(chImplic),
                                         andForm,
                                         anotherQuantForm);
        }


       /*
        baseForm := uniForm.CopyFormula;

              NewSubTermVar(baseForm, zeroForm, inductvariable);     (*P(0)*)

              itstermlist := nil;   {constructs the successor of the variable}
              AddItemOnEnd(itstermlist, inductvariable.CopyFormula);

              SupplyFormula(succTerm);
              with succTerm do
              begin
              fKind := functor;
              fInfo := '''';
              fRlink := itstermlist;
              end;

              itstermlist := nil;
              succForm := uniForm.CopyFormula;
              NewSubTermVar(succForm, succTerm, inductVariable);  (*P(v')*)

              SupplyFormula(formulanode);
              formulanode.fKind := binary; {formulanode is the new formula node}
              formulanode.fInfo := chImplic;
              formulanode.fLlink := uniform.CopyFormula;
              formulanode.fRlink := succForm;

              stepForm := formulanode;                             (*P(v) hook P(v')*)
              formulanode := nil;

              SupplyFormula(formulanode);
              with formulanode do
              begin
              fKind := quantifier;
              fInfo := chUniquant;
              fLlink := inductvariable.CopyFormula;
              fRlink := stepForm;
              end;

              stepForm := formulanode;                                   (*Allv(P(v) hook P(v'))*)
              formulanode := nil;

              SupplyFormula(formulanode);
              with formulanode do
              begin
              fKind := quantifier;
              fInfo := chUniquant;
              fLlink := inductvariable;
              fRlink := uniForm;
              end;

              uniForm := formulanode;                                   (*(Allv)P(v)*)
              formulanode := nil;

              SupplyFormula(formulanode);
              formulanode.fKind := binary; {formulanode is the new formula node}
              formulanode.fInfo := chAnd;
              formulanode.fLlink := baseForm;
              formulanode.fRlink := stepForm;
              baseForm := formulanode;
              formulanode := nil;


              SupplyFormula(formulanode);
              formulanode.fKind := binary; {formulanode is the new formula node}
              formulanode.fInfo := chImplic;
              formulanode.fLlink := baseForm;
              formulanode.fRlink := uniForm;


        */






      }

      /*          GetTheRoot(strNull, strNull, prompt, uniForm, cancel);

             if not cancel then
              begin
               if uniForm.NumofFreeOccurences(inductvariable) = 0 then
                BugAlert(concat('The inductive formula must contain n.'))
               else
                begin  */





    /************************ Rule of DoII **********************************/



    public class IIAction extends AbstractAction{
      JTextComponent fText;
      TFormula fRoot=null;

     /*We just need to get the term  */


       public IIAction(JTextComponent text, String label){
         putValue(NAME, label);

         fText=text;
       }

        public void actionPerformed(ActionEvent ae){

          boolean wellformed;

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

      }

    }

    /*
         SupplyFormula(secondlink);
         with secondlink do
          begin
           fKind := kons;
           fLlink := termForm;
          end;

         SupplyFormula(firstlink);
         with firstlink do
          begin
           fKind := kons;
           fLlink := termForm.CopyFormula;
           fRlink := secondlink;
          end;

         SupplyFormula(formulanode);
         with formulanode do
          begin
           fKind := equality;
           fInfo := chEquals;
           fRlink := firstlink;
          end;


    */

   /************************ Rule of DoIE **********************************/


/*This is based on EG, but (hopefully) simplified*/

/*
    begin {1}
            DoIE := gNoChanges;

            cancel := FALSE;
            found := FALSE;
            done := FALSE;
            subsinfirst := TRUE;  (*this means that the second is the identity*)
            chStr := ' ';

            occurences := 0;
            metSoFar := 0;
            inFirst := 0;
            insecond := 0;
            i := 0;
            prompt := strNull;
            outputStr := strNull;

            if fTextList.TwoSelected(firstline, secondline) then
             if (firstline.fFormula.fKind = equality) | (secondline.fFormula.fKind = equality) then
          {we allow substitution is any formula provided no variable in the equality is bound in the formula}

              begin
               OrderForSwap;  {secondline now contains identity}
               if not cancel then
                begin

                 alphaForm := secondline.fFormula.FirstTerm;
                 gammaForm := secondline.fFormula.SecondTerm;

                 if CapturePossible then
                 begin
                 outPutStr := strNull;
                 fParser.WriteFormulaToString(firstline.fFormula, outPutStr);
                 BugAlert(concat('The variable ', chStr, ' occurs in the identity and is bound in ', outPutStr, ' . '));
                 end
                 else
                 begin
                 subFormulaRewrite := SubFormCheck;
                 if not cancel then
                 begin
                 repeat
                 begin {6}
                 found := FALSE;

                 replacementForm := gammaForm; {replacing alpha by gamma}

                 occurences := firstline.fFormula.NumofFreeOccurences(alphaForm);

                                                  {***}

                 copyf := firstline.fFormula.CopyFormula;

                 firstformula := firstline.fFormula;

                 if (occurences > 0) & (not subformulaRewrite | useFirstTerm) then
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

                 if (occurences > 0) & (not subformulaRewrite | not useFirstTerm) then
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


                 if not cancel then
                 if not EqualFormulas(firstline.fFormula, copyf) then
                 begin
                 New(aLineCommand);
                 FailNil(aLineCommand);
                 aLineCommand.ILineCommand(cAddLine, SELF);

                 SupplyProofline(newline);

                                          {newline points to new proofline}
                 with newline do
                 begin {12}
                 fFormula := copyf;
                 ffirstjustno := firstline.fLineno;
                 fsecondjustno := secondline.fLineno;
                 fjustification := ' IE';
                 fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
                 end; {12}

                 aLineCommand.fNewlines.InsertLast(newline);
                 newline := nil;
                 DoIE := aLineCommand;
                 end;
                 end;
                 end;
                end;
              end;
       end;

*/


public class IEYesNoAction extends AbstractAction{


   IEAction fParent;
   boolean fYes;
   TFormula fSubstitution;

   public IEYesNoAction(IEAction parent,boolean yes,TFormula substitution){

     if (yes)
       putValue(NAME, "Yes");
     else
       putValue(NAME, "No");

     fParent=parent;
     fYes=yes;
     fSubstitution=substitution;

   }


  /*
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


   */




   public void actionPerformed(ActionEvent ae){

      TFormula surgeryTerm;

     if (fParent.fNumTreated<fParent.fNumToTreat){

        surgeryTerm= fParent.fTermsToTreat[fParent.fNumTreated];

        surgeryTerm.fInfo=surgeryTerm.fInfo.substring(1);  // surgically omits the marker which is leading


        if (fYes){

          /* The surgery term might be a, f(a), f(g(a,b)) etc, and so too might be the term that is to
          be substituted, fSubstitution. We just copy everything across*/


           surgeryTerm.fKind = fSubstitution.getKind();
           surgeryTerm.fInfo = fSubstitution.getInfo(); // (*surgery*)
           if (fSubstitution.getLLink() == null)
             surgeryTerm.fLLink=null;
           else
             surgeryTerm.fLLink=fSubstitution.getLLink().copyFormula();;  // should be no left link
           if (fSubstitution.getRLink() == null)
             surgeryTerm.fRLink=null;
           else
             surgeryTerm.fRLink=fSubstitution.getRLink().copyFormula();;  // important becuase there might be the rest of a term there
        }

       // if they have pressed the No button, fYes is false and we do nothing

       fParent.fNumTreated+=1;

   }

     if (fParent.fNumTreated<fParent.fNumToTreat){
                   // put the marker in the next one

       fParent.fTermsToTreat[fParent.fNumTreated].fInfo= chInsertMarker+
                                                    fParent.fTermsToTreat[fParent.fNumTreated].fInfo;


         String message= fParser.writeFormulaToString(fParent.fCopy);


         fParent.fText.setText(message);

         fParent.fText.requestFocus();

     }
     else{                                        //  last one, return to parent

      JButton defaultButton = new JButton(fParent);

      JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
      TProofInputPanel inputPane = new TProofInputPanel("Doing IE-- Stage3,"+
            " displaying result. " +
            "If suitable, press Go.", fParent.fText, buttons);


      addInputPane(inputPane);

      String message= fParser.writeFormulaToString(fParent.fCopy);

       fParent.fText.setEditable(true);
       fParent.fText.setText(message);
       fParent.fText.selectAll();

      inputPane.getRootPane().setDefaultButton(defaultButton);
      fInputPane.setVisible(true); // need this
      fParent.fText.requestFocus();         // so selected text shows

   fParent.fStage+=1;  // 3 I think

   if (fParent.fStage==3)
     fParent.askAboutGamma();  //I think
   else
     fParent.displayResult();


     }

   }

 }

   public class IEAction extends AbstractAction{


JTextField fText;


  TProofline fFirstline=null;
  TProofline fSecondline=null;
  TFormula fAlpha=null, fGamma=null, fScope=null, fCopy=null,
      fCurrentNode=null,fCurrentCopyNode=null;
  int fNumAlpha=0; //of term alpha
  int fNumGamma=0; //of term gamma
 // int fNumAlphaTreated=0;
  int fStage=1;
  TFormula.MarkerData markerData;

  private TFormula [] fAlphas; // the occurrences of alpha in the (copy of) original formula
  TFormula [] fGammas; // the occurrences of gamma in the (copy of) original formula

  TFormula [] fTermsToTreat;
  int fNumTreated=0;
  int fNumToTreat=0;

  boolean useFilter=true;

  boolean fAlphaOnly=false;
  boolean fGammaOnly=false;

 /*We only have to run through the occurrences seeing which ones they want to subs in */


   public IEAction(JTextField text, String label, TProofline firstline,TProofline secondline){
     putValue(NAME, label);

     fText = text;
     fFirstline = firstline;
     fSecondline = secondline;

     fAlpha = fSecondline.getFormula().firstTerm(); // alpha=gamma
     fGamma = fSecondline.getFormula().secondTerm();

     fCopy = fFirstline.fFormula.copyFormula(); //??

 /*    fNumAlpha = (fFirstline.fFormula).numInPredOrTerm(fAlpha);

 Aug 06 There is a change here that needs explaining. In the original Deriver one could substitute into
compound formulas eg a=b Fa^Ga to get Fb^Gb. But I must have been nervous when first coding this. And restricted
the substitution to atomic formulas only (you could do compounds by taking them apart and putting them
back together).

Colin has asked me to revert to compound substitution. Hence the change


 */
     fNumAlpha = (fFirstline.fFormula).numOfFreeOccurrences(fAlpha);

     if (fNumAlpha > 0) {
       fAlphas = new TFormula[fNumAlpha];    // create an array of the actual terms in the copy that we will do surgery on

       for (int i = 0; i < fNumAlpha; i++) { // initialize
         fAlphas[i] = fCopy.depthFirstNthOccurence(fAlpha, i + 1); // one uses zero based index, other 1 based
       }
     }

   //  fNumGamma = (fFirstline.fFormula).numInPredOrTerm(fGamma);

   fNumGamma = (fFirstline.fFormula).numOfFreeOccurrences(fGamma);

     if (fNumGamma > 0) {
       fGammas = new TFormula[fNumGamma];    // create an array of the actual terms in the copy that we will do surgery on

       for (int i = 0; i < fNumGamma; i++) { // initialize
         fGammas[i] = fCopy.depthFirstNthOccurence(fGamma, i + 1); // one uses zero based index, other 1 based
       }
     }
   }


public void start(){
  fStage=1;
  actionPerformed(null);
}


    public void actionPerformed(ActionEvent ae){


      switch (fStage){


        case 1:
          subFormCheck();
          break;

        case 2:
          askAboutAlpha();
          break;

        case 3:
          askAboutGamma();
          break;

        case 4:
          displayResult();
          break;

        case 5:
          readResult();
          break;

        default: ;
      }
      }



      /*
             function SubFormCheck: boolean;
               var
                temp: boolean;
              begin
            {There is a potential problem here that we must look out for. If one term in the identity}
            {is a subterm of the other, eg y=f(y) and both occur in the original formula eg Gf(y)}
            {the we should ask whether substitution for y or f(y) is required and not permit both}

               temp := ((gammaForm.SubFormulaOccursInFormula(gammaForm, alphaForm) | gammaForm.SubFormulaOccursInFormula(alphaForm, gammaForm)) & (firstline.fFormula.NumofFreeOccurences(alphaForm) <> 0) & (firstline.fFormula.NumofFreeOccurences(gammaForm) <> 0));

               if temp then
                begin
                 outPutStr := strNull;
                 fParser.WriteFormulaToString(alphaForm, outPutStr);
                 prompt := concat('Do you wish to substitute for ', outputStr, ' ?');

                 if not GetTheChoice('Yes?', 'Other term?', prompt) then
                  cancel := TRUE;
            {This sets fRadio}
                 useFirstTerm := fRadio;
                end;{3}

               SubFormCheck := temp;
              end;

      */

void subFormCheck(){    // YOU NEED TO WRITE THIS JAN 06

      /* In the Pascal I worried

            {There is a potential problem here that we must look out for. If one term in the identity}
                {is a subterm of the other, eg y=f(y) and both occur in the original formula eg Gf(y)}
                {the we should ask whether substitution for y or f(y) is required and not permit both}

      The terms 'overlap' so we cannot ask about one then the other. The User must choose

        */

boolean flag =(fAlpha.numInPredOrTerm(fGamma)!=0)
           ||(fGamma.numInPredOrTerm(fAlpha)!=0);

if (flag){
  String outputStr="Do you wish to substitute for "
      +fParser.writeFormulaToString(fAlpha) +"?";


  getTheChoice(new AbstractAction("No")
                 {public void actionPerformed(ActionEvent ae){
                   fGammaOnly=true;
                   removeInputPane();
                   fStage = 3;  // go straight to askGamma and miss alpha
                   askAboutGamma();
                 }},           //the calling actions must remove the input pane

               new AbstractAction("Yes")
                 {public void actionPerformed(ActionEvent ae){
                   fAlphaOnly=true;
                   removeInputPane();
                   fStage = 2;
                   askAboutAlpha();
                 }},
               "One term is a subterm of the other, just treat one at a time",
               outputStr);

}
else{
  fStage = 2;
  actionPerformed(null);
}
}










  void alphaByGamma(){

    int occurences =fFirstline.getFormula().numOfFreeOccurrences(fAlpha);

  }


 /*

      replacementForm := gammaForm; {replacing alpha by gamma}

             occurences := firstline.fFormula.NumofFreeOccurences(alphaForm);

                                              {***}

             copyf := firstline.fFormula.CopyFormula;

             firstformula := firstline.fFormula;

             if (occurences > 0) & (not subformulaRewrite | useFirstTerm) then
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



  */



private void displayResult(){

String message= fParser.writeFormulaToString(fCopy);

fText.setEditable(false);  // we don't want them changing it
fText.setText(message);
fText.selectAll();
fText.requestFocus();

fStage=5;

}


private void readResult(){


if (fScope==null){
  boolean useFilter = true;
  ArrayList dummy = new ArrayList();

  String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

  TFormula root = new TFormula();
  StringReader aReader = new StringReader(aString);
  boolean wellformed;

  wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);  // it can never be illformed since we put a well formed one there

  if (!wellformed) {
    String message = "The string is illformed." +
        (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");

    fText.setText(message);
    fText.selectAll();
    fText.requestFocus();
  }
  else {
    fScope = root;

    goodFinish();
  }
}
}


private void goodFinish(){

/*  with newline do
             begin {12}
             fFormula := copyf;
             ffirstjustno := firstline.fLineno;
             fsecondjustno := secondline.fLineno;
             fjustification := ' IE';
             fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
             end; {12}  */

                    TProofline newline = supplyProofline();

                    int level = fModel.getHeadLastLine().fSubprooflevel;

                    newline.fFormula = fCopy;
                    newline.fJustification = IEJustification;
                    newline.fFirstjustno = fFirstline.fLineno;
                    newline.fSecondjustno = fSecondline.fLineno;
                    newline.fSubprooflevel = level;

                    TUndoableProofEdit newEdit = new TUndoableProofEdit();
                    newEdit.fNewLines.add(newline);
                    newEdit.doEdit();

                    removeInputPane();
      }


/*private void alterCopy (TFormula termPart, TFormula variable){

termPart.fKind=TFormula.variable;     /*surgery
termPart.fInfo=variable.fInfo;
termPart.fRLink=null;       // need this to get rid of any subterms

} */

/*private void removeMarker(boolean alterCopy){
/* {removes marker and alters copy if needed}

fCurrentNode.fInfo=fCurrentNode.fInfo.substring(1);  // omits the marker which is leading

if (alterCopy){
 fCurrentCopyNode.fKind = TFormula.variable;
 fCurrentCopyNode.fInfo = fGamma.fInfo; // (*surgery*)

 fCurrentCopyNode.fRLink = null;  // important becuase there might be the rest of a term there


}



} */

/*

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



   */


/*private void askAboutGamma(){
 fStage=3;
 displayResult();             //TEMP
}*/

private void askAboutAlpha(){
  String aString;
  String message;

  if (fGammaOnly||fNumAlpha == 0) { // we just go on to gamma
    fStage = 3;
    askAboutGamma();
  }

  else {
    if (fNumAlpha > 0) {

 /*     ( (TProofInputPanel) fInputPane).fLabel.setText("Doing IE-- Stage1," + //they never see this
          " Occurrences of first term. " +
          "Substitute for this one?");  */

      fAlphas[0].fInfo = chInsertMarker + fAlphas[0].fInfo;
      fTermsToTreat = fAlphas;
      fNumTreated = 0;
      fNumToTreat = fNumAlpha;

      /********* going to yes/no subroutine *****/

      boolean yes = true;

      JButton yesButton = new JButton(new IEYesNoAction(this, yes, fGamma));
      JButton noButton = new JButton(new IEYesNoAction(this, !yes, fGamma));

      message = fParser.writeFormulaToString(fCopy);

      //JTextField text = new JTextField(message);

      fText.setText(message);

      JButton[] buttons = {
          noButton, yesButton}; // put cancel on left
      TProofInputPanel inputPane = new TProofInputPanel(
          "Doing =E-- Stage1, substitute for this occurrence of left term?",
          fText, buttons);

      addInputPane(inputPane);

      fInputPane.setVisible(true); // need this
      fText.setEditable(false);
      fText.requestFocus(); // so selected text shows

      message = fParser.writeFormulaToString(fCopy);

      fText.setText(message);
      fText.selectAll();
      fText.requestFocus();

    //  fStage = 3; // 3 probably, or 2  // the yes/no sets this

    }
  }
}

private void askAboutGamma(){
  String aString;
  String message;


   if (fAlphaOnly||fNumGamma ==0){       // we just go on to display
     fStage=4;
     displayResult();
   }

   else{
     if (fNumGamma >0) {


  /*     ((TProofInputPanel)fInputPane).fLabel.setText("Doing IE-- Stage2,"+    //they never see this
          " Occurrences of second term. " +
          "Substitute for this one?");  */

       fGammas[0].fInfo= chInsertMarker+ fGammas[0].fInfo;
       fTermsToTreat=fGammas;
       fNumTreated=0;
       fNumToTreat=fNumGamma;


        /********* going to yes/no subroutine *****/

        boolean yes=true;

     JButton yesButton = new JButton(new IEYesNoAction(this,yes,fAlpha));
     JButton noButton = new JButton(new IEYesNoAction(this,!yes,fAlpha));


     message= fParser.writeFormulaToString(fCopy);

    //JTextField text = new JTextField(message);

    fText.setText(message);

    JButton[]buttons = {noButton, yesButton };  // put cancel on left
    TProofInputPanel inputPane = new TProofInputPanel("Doing =E-- Stage2, substitute for this occurrence of right term?", fText, buttons);


    addInputPane(inputPane);

          fInputPane.setVisible(true); // need this
          fText.setEditable(false);
         fText.requestFocus();         // so selected text shows

   message= fParser.writeFormulaToString(fCopy);

  fText.setText(message);
  fText.selectAll();
  fText.requestFocus();

  fStage=4;  // 3 probably, or 2

     }
   }

}


}




       /*


       */



      /************************ Rule of DoHintNegINoAbs **********************************/



             public class HintReductioNoAbs extends AbstractAction{
               JTextComponent fText;
               TFormula fAssumption;
               TFormula fTarget;
               String fJustification;




                public HintReductioNoAbs(JTextComponent text,
                                     String label,
                                     TFormula assumption,
                                     TFormula target,
                                     String justification){
                  putValue(NAME, label);

                  fText=text;
                  fAssumption=assumption;
                  fTarget=target;
                  fJustification=justification;
                }

                 public void actionPerformed(ActionEvent ae){



                   boolean useFilter = true;
                   ArrayList dummy = new ArrayList();

                   String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

                   TFormula root = new TFormula();
                   StringReader aReader = new StringReader(aString);
                   boolean wellformed;

                   wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

                   if (!wellformed) {
                     String message = "The string is illformed." +
                         (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

                     //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

                     fText.setText(message);
                     fText.selectAll();
                     fText.requestFocus();
                   }
                   else {  // we have now got a Positive Horn

                     /* Two cases:- if the target is ~C we add C; if the target is C we add ~C.
                      Gentzen does not permit the second case. But we will disable the menu
                      in with Genzten rules. Then the subclasses can use it */


                     TProofline newline = supplyProofline();
            /*         TFormula conclusion = findNextConclusion();

                     TFormula negroot;
                     String justification;

                     if (fParser.isNegation(conclusion)) {
                       negroot = conclusion.fRLink; // C to ~C
                       justification = fNegIJustification;
                     }
                     else {
                       negroot = new TFormula(TFormula.unary, //~C to C
                                              String.valueOf(chNeg),
                                              null,
                                              conclusion);
                       justification = fNegEJustification; // need to fix this
                     }  */

                     TUndoableProofEdit newEdit = new TUndoableProofEdit();

                     TProofline headLastLine = fModel.getHeadLastLine();
                     int currentHeadLineno = headLastLine.fLineno;
                     int level = headLastLine.fSubprooflevel;

                     newline.fLineno = currentHeadLineno;
                     newline.fFormula = fAssumption.copyFormula(); //start subproof
                     newline.fJustification = fAssJustification;
                     newline.fSubprooflevel = level + 1;
                     newline.fLastassumption = true;

                     newEdit.fNewLines.add(newline);
                     currentHeadLineno += 1;

                     int posHornLineno = addIfNotThere(root, level+1,
                         newEdit.fNewLines);

                     if (posHornLineno == -1) {
                       posHornLineno = currentHeadLineno + 2;
                       currentHeadLineno += 2;
                     }

                     TFormula negHorn = new TFormula(TFormula.unary,
                         String.valueOf(chNeg), null, root);

                     int negHornLineno = addIfNotThere(negHorn, level+1,
                         newEdit.fNewLines);

                     if (negHornLineno == -1) {
                       negHornLineno = currentHeadLineno + 2;
                       currentHeadLineno += 2;
                     }

                     newEdit.fNewLines.add(endSubProof(level + 1));

                     newline = supplyProofline();

                     newline.fFormula = fTarget.copyFormula();
                     newline.fFirstjustno = posHornLineno;
                     newline.fSecondjustno = negHornLineno;
                     newline.fJustification = fJustification;
                     newline.fSubprooflevel = level;

                     newEdit.fNewLines.add(newline);

                     newEdit.doEdit();

                     removeInputPane();
                   }



               }

        }










    /************************ Rule of DoHintAbsI **********************************/



        public class DoHintAbsI extends AbstractAction{
          JTextComponent fText;




           public DoHintAbsI(JTextComponent text, String label){
             putValue(NAME, label);

             fText=text;
           }

            public void actionPerformed(ActionEvent ae){



              boolean useFilter = true;
              ArrayList dummy = new ArrayList();

              String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

              TFormula root = new TFormula();
              StringReader aReader = new StringReader(aString);
              boolean wellformed;

              wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

              if (!wellformed) {
                String message = "The string is illformed." +
                    (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

                //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

                fText.setText(message);
                fText.selectAll();
                fText.requestFocus();
              }
              else {

                 TUndoableProofEdit newEdit = new TUndoableProofEdit();

                 TProofline headLastLine=fModel.getHeadLastLine();
                 int currentHeadLineno = headLastLine.fLineno;
                 int level = headLastLine.fSubprooflevel;

                // TProofline newline = supplyProofline();

                 int posHornLineno=addIfNotThere(root, level, newEdit.fNewLines);

                 if (posHornLineno==-1){
                   posHornLineno = currentHeadLineno + 2;
                   currentHeadLineno += 2;
                 }

                 TFormula negroot= new TFormula();

                       negroot.fKind = TFormula.unary;
                       negroot.fInfo = String.valueOf(chNeg);
                       negroot.fRLink = root;

                       int negHornLineno=addIfNotThere(negroot, level, newEdit.fNewLines);

                       if (negHornLineno==-1){
                         negHornLineno = currentHeadLineno + 2;
                         currentHeadLineno += 2;
                       }

                       TProofline newline = supplyProofline();

                     newline.fFormula = TFormula.fAbsurd.copyFormula();
                     newline.fFirstjustno = posHornLineno;
                     newline.fSecondjustno = negHornLineno;
                     newline.fJustification = absIJustification;
                     newline.fSubprooflevel = level;


                newEdit.fNewLines.add(newline);
                newEdit.doEdit();

                removeInputPane();
              }

              /*

                           New(aLineCommand);
                      FailNIL(aLineCommand);
                      aLineCommand.ILineCommand(cAddLine, SELF);

                      level := TProofline(SELF.fHead.Last).fSubProofLevel;

                      AddIfNotThere(Contraroot, level, PosHorn, firstnewline, secondnewline);

                      if PosHorn = 0 then {not already there}
                       begin
                       aLineCommand.fNewlines.InsertLast(firstnewline);
                       aLineCommand.fNewlines.InsertLast(secondnewline);
                       PosHorn := TProofline(fHead.Last).flineno + 2; {the lines are only in the}
                {                                                                    command at this stage not the}
                {                                                                    document}
                       end;

                      SupplyFormula(neghornroot);
                      with neghornroot do
                       begin
                       fKind := unary;
                       fInfo := chNeg;
                       fRlink := Contraroot;
                       end;

                      AddIfNotThere(neghornroot, level, NegHorn, firstnewline, secondnewline);

                      neghornroot.fRlink := nil;
                      neghornroot.DismantleFormula;

                      if NegHorn = 0 then {not already there}
                       begin
                       aLineCommand.fNewlines.InsertLast(firstnewline);
                       aLineCommand.fNewlines.InsertLast(secondnewline);
                       if (PosHorn = TProofline(fHead.Last).flineno + 2) then
                       NegHorn := TProofline(fHead.Last).flineno + 4
                       else
                       NegHorn := TProofline(fHead.Last).flineno + 2;

                (*the first is the case where there is no existing pos horn,*)
                (*the second is where a pos horn has been found.*)

                       end;


                      aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, PosHorn, NegHorn, 0, ' AbsI'));




           }*/

          }

        }



/************************ Rule of TI **********************************/



  public class TIKeepLastAction extends AbstractAction{
    JTextComponent fText;

     public TIKeepLastAction(JTextComponent text, String label){
       putValue(NAME, label);

       fText=text;
     }

      public void actionPerformed(ActionEvent ae){
     boolean useFilter =true;
     ArrayList dummy = new ArrayList();

     String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

     TFormula root = new TFormula();
     StringReader aReader = new StringReader(aString);
     boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

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

  public class TIDropLastAction extends AbstractAction{
    JTextComponent fText;
    int fLastLevel;

     public TIDropLastAction(JTextComponent text, int level){
       putValue(NAME, "Drop Last");

       fText=text;
       fLastLevel=level;
     }

      public void actionPerformed(ActionEvent ae){
     boolean useFilter =true;
     ArrayList dummy = new ArrayList();

     String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

     TFormula root = new TFormula();
     StringReader aReader = new StringReader(aString);
     boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

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
     newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;
     newline.fLastassumption=true;

     TUndoableProofEdit  newEdit = new TUndoableProofEdit();

     newEdit.fNewLines.add(endSubProof(fLastLevel));
     newEdit.fNewLines.add(newline);
     newEdit.doEdit();

     removeInputPane();};
    }

  }

  public void doTI(){
     JButton defaultButton;
     JButton dropLastButton;
     TProofInputPanel inputPane;

      if (fModel.getHeadLastLine().fSubprooflevel > kMaxNesting) {
        bugAlert("Doing"+fAssJustification+". Warning.", "Phew... no more assumptions, please.");
      }
      else {

        if (fTemplate) {
          bugAlert("Doing"+fAssJustification+". Warning.", "Cancel! Ass. is not usual with Tactics on-- you will never be able to drop a new assumption.");
        }
        else {

          JTextField text = new JTextField("New antecedent?");
          text.setDragEnabled(true);
          text.selectAll();

        /*
         if (TProofline(fHead.Last).fSubprooflevel > TProofline(fHead.Last).fHeadlevel) then {This can be 0 or -1}
    {                                                                           depending on whether}
    {                                                                           there are premises}

         */

        TProofline lastLine = fModel.getHeadLastLine();

        if (lastLine.fSubprooflevel>lastLine.fHeadlevel){

          defaultButton = new JButton(new TIKeepLastAction(text,"Keep Last"));
          dropLastButton = new JButton(new TIDropLastAction(text,lastLine.fSubprooflevel));

          JButton[]buttons = {
              new JButton(new CancelAction()), dropLastButton,
              defaultButton };  // put cancel on left
          inputPane = new TProofInputPanel(fTIInput,
             text, buttons,fInputPalette);
        }
       else{
         defaultButton = new JButton(new TIKeepLastAction(text,"Go"));

        JButton[]buttons = {
         new JButton(new CancelAction()),

          defaultButton };  // put cancel on left
        inputPane = new TProofInputPanel(fTIInput,
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
   function TProofWindow.DoTheorem: TCommand;

     var
      newline: TProofline;
      aLineCommand: TLineCommand;
      prompt: str255;
      root: TFormula;
      cancel: boolean;
      copyindex: integer;

    begin
     DoTheorem := gNoChanges;



     prompt := 'Theorem?';
              {GetIndString(prompt, kStringRSRCID, 2);  prompt := 'New Antecedent?';}

     GetTheRoot(strNull, strNull, prompt, root, cancel);

     if not cancel then
      begin
       prompt := 'Brief annotation? eg. Theorem 1';

       if GetTheChoice(strNull, strNull, prompt) then
        begin

         copyIndex := length(prompt);

         if copyIndex > 10 then
          copyIndex := 10;

         prompt := copy(prompt, 1, copyIndex);

         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         SupplyProofline(newline);
         with newline do
          begin
          fFormula := root;
          fJustification := prompt;
          fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
          end;

         aLineCommand.fNewlines.InsertLast(newline);
         newline := nil;
         prompt := strNull;

         DoTheorem := aLineCommand;
        end;
      end;
    end;



  */


 public void doInduction(){
 JButton defaultButton;
 TProofInputPanel inputPane;


 JTextField text = new JTextField("Enter inductive formula A(x) containing the term x.");

    text.setDragEnabled(true);
    text.selectAll();

    defaultButton = new JButton(new InductionAction(text,"Go"));

    JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
    inputPane = new TProofInputPanel("Induction", text, buttons,fInputPalette);


         addInputPane(inputPane);

         inputPane.getRootPane().setDefaultButton(defaultButton);
         fInputPane.setVisible(true); // need this
         text.requestFocus();         // so selected text shows
}



/*

           function TProofWindow.DoInduction: TCommand;
         var
          newline: TProofline;
          aLineCommand: TLineCommand;
          zeroForm, baseForm, stepForm, uniForm, formulanode, inductvariable, succTerm, succForm, itstermlist: TFormula;
          prompt: str255;
          cancel, found: boolean;

        begin
         DoInduction := gNoChanges;

         cancel := FALSE;
         prompt := strNull;

         SupplyFormula(zeroForm);
         with zeroForm do
          begin
           fKind := functor;
           fInfo := '0';
          end;

         SupplyFormula(inductvariable);
         with inductvariable do
          begin
           fKind := variable;
           fInfo := 'n';
          end;

         begin
          prompt := 'Enter inductive formula A(n) containing the term n.';

          GetTheRoot(strNull, strNull, prompt, uniForm, cancel);

          if not cancel then
           begin
            if uniForm.NumofFreeOccurences(inductvariable) = 0 then
             BugAlert(concat('The inductive formula must contain n.'))
            else
             begin
              baseForm := uniForm.CopyFormula;

              NewSubTermVar(baseForm, zeroForm, inductvariable);     (*P(0)*)

              itstermlist := nil;   {constructs the successor of the variable}
              AddItemOnEnd(itstermlist, inductvariable.CopyFormula);

              SupplyFormula(succTerm);
              with succTerm do
              begin
              fKind := functor;
              fInfo := '''';
              fRlink := itstermlist;
              end;

              itstermlist := nil;
              succForm := uniForm.CopyFormula;
              NewSubTermVar(succForm, succTerm, inductVariable);  (*P(v')*)

              SupplyFormula(formulanode);
              formulanode.fKind := binary; {formulanode is the new formula node}
              formulanode.fInfo := chImplic;
              formulanode.fLlink := uniform.CopyFormula;
              formulanode.fRlink := succForm;

              stepForm := formulanode;                             (*P(v) hook P(v')*)
              formulanode := nil;

              SupplyFormula(formulanode);
              with formulanode do
              begin
              fKind := quantifier;
              fInfo := chUniquant;
              fLlink := inductvariable.CopyFormula;
              fRlink := stepForm;
              end;

              stepForm := formulanode;                                   (*Allv(P(v) hook P(v'))*)
              formulanode := nil;

              SupplyFormula(formulanode);
              with formulanode do
              begin
              fKind := quantifier;
              fInfo := chUniquant;
              fLlink := inductvariable;
              fRlink := uniForm;
              end;

              uniForm := formulanode;                                   (*(Allv)P(v)*)
              formulanode := nil;

              SupplyFormula(formulanode);
              formulanode.fKind := binary; {formulanode is the new formula node}
              formulanode.fInfo := chAnd;
              formulanode.fLlink := baseForm;
              formulanode.fRlink := stepForm;
              baseForm := formulanode;
              formulanode := nil;


              SupplyFormula(formulanode);
              formulanode.fKind := binary; {formulanode is the new formula node}
              formulanode.fInfo := chImplic;
              formulanode.fLlink := baseForm;
              formulanode.fRlink := uniForm;


              New(aLineCommand);
              FailNil(aLineCommand);
              aLineCommand.ILineCommand(cAddLine, SELF);

              SupplyProofline(newline);
              with newline do
              begin
              fFormula := formulanode;
              fJustification := 'Induction';
              fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
              end;
              formulanode := nil;

              aLineCommand.fNewlines.InsertLast(newline);
              newline := nil;
              DoInduction := aLineCommand;

             end;
           end;
         end;
        end;



    */






public void doTheorem(){
JButton defaultButton;
TProofInputPanel inputPane;


JTextField text = new JTextField("Theorem?");

   text.setDragEnabled(true);
   text.selectAll();

   defaultButton = new JButton(new TheoremAction(text,"Go"));

   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
   inputPane = new TProofInputPanel("Doing Theorem", text, buttons);


        addInputPane(inputPane);

        inputPane.getRootPane().setDefaultButton(defaultButton);
        fInputPane.setVisible(true); // need this
        text.requestFocus();         // so selected text shows
}


/*

      function TProofWindow.DoTI: TCommand; {check, this takes two copies}

    var
     newline: TProofline;
     aLineCommand: TLineCommand;
     prompt, rad1, rad2: str255;
     root: TFormula;
     cancel: boolean;

   begin
    DoTI := gNoChanges;
    cancel := false;

    SELF.fRadio := TRUE;

    TRadio(SELF.findsubview('rady')).ToggleIf(FALSE, TRUE); {makes sure keeplast is default}
    TRadio(SELF.findsubview('radz')).ToggleIf(TRUE, TRUE); {makes sure keeplast is default}

    if TProofline(fHead.Last).fSubprooflevel > kMaxnesting then
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

        GetTheRoot(rad1, rad2, prompt, root, cancel);
        if not cancel then
         begin

         if fRadio then {keep last}
         begin
         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         aLineCommand.fNewlines.InsertLast(Addassumption(root, not fRadio));

         DoTI := aLineCommand;
         end
         else
                           {droplast}
         begin
         if (TProofline(fHead.Last).fSubprooflevel > TProofline(fHead.Last).fHeadlevel) then {This can be 0 or -1}
  {                                                                           depending on whether}
  {                                                                           there are premises}
         begin
         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         aLineCommand.fNewlines.InsertLast(EndSubProof(TProofline(SELF.fHead.Last).fSubprooflevel));

         aLineCommand.fNewlines.InsertLast(Addassumption(root, not fRadio));

         DoTI := aLineCommand;

         end;

         end;

         end;
       end;
     end;

   end;



  */


 /*************************** Rule of ImplicE **********************************/

 /*

  function TProofWindow.DoImplicE: TCommand;

  var

   firstline, newline, secondline: TProofline;
   aLineCommand: TLineCommand;
   formula1, formulanode: TFormula;
   rad1, rad2, prompt: str255;

 begin

  DoImplicE := gNoChanges;

  if fTextList.TwoSelected(firstline, secondline) then
   begin

    if (secondline.fFormula.fInfo = chImplic) then
     if Equalformulas(secondline.fFormula.fllink, firstline.fFormula) then
      begin
       newline := firstline;
       firstline := secondline;
       secondline := newline; {to make line 2 contain the arrow}
       newline := nil;
      end;
    if (firstline.fFormula.fInfo = chImplic) then
     if Equalformulas(firstline.fFormula.fllink, secondline.fFormula) then
      begin
       New(aLineCommand);
       FailNil(aLineCommand);
       aLineCommand.ILineCommand(cAddLine, SELF);

       SupplyProofline(newline); {newline points to new proofline}
       with newline do
       begin
       fFormula := firstline.fFormula.fRlink.CopyFormula;
       ffirstjustno := firstline.fLineno;
       fsecondjustno := secondline.fLineno;
       fJustification := ' �E';
       fSubprooflevel := TProofline(SELF.
  .Last).fSubprooflevel;
       end;

       aLineCommand.fNewlines.InsertLast(newline);
       newline := nil;
       DoImplicE := aLineCommand;

      end;
   end;

 end;



  */





 void doImplicE(){

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
          newline.fJustification = fImplicEJustification;
          newline.fSubprooflevel = level;

          TUndoableProofEdit newEdit = new TUndoableProofEdit();
          newEdit.fNewLines.add(newline);
          newEdit.doEdit();


       }
     }


 }


 /*************************** Rule of ImplicI **********************************/

/*

  function TProofWindow.DoImplicI: TCommand;

   var

    subhead, subtail: TProofline;
    aLineCommand: TLineCommand;
    level: integer;

  begin
   DoImplicI := gNoChanges;

   if fTemplate then {gTemplate}
    DoImplicI := DoHintImplicI
   else
    begin

     if fTextList.OneSelected(subtail) then
      if FindLastAssumption(subhead) then
       begin
        level := TProofline(fHead.Last).fSubprooflevel;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ILineCommand(cAddLine, SELF);

        aLineCommand.fNewlines.InsertLast(EndSubProof(level));

        aLineCommand.fNewlines.InsertLast(AddImplication(subhead.fFormula, subtail.fFormula, level - 1, subtail.fLineno));

        DoImplicI := aLineCommand;
       end;
    end;

  end;


 */

void doHintImplicI(){
 TFormula newFormula=null;
 TProofline newline= supplyProofline();
 TFormula conclusion =findNextConclusion();


   TFormula anteroot = conclusion.fLLink;
   TFormula conseroot = conclusion.fRLink;

   TUndoableProofEdit newEdit = new TUndoableProofEdit();

   TProofline headLastLine=fModel.getHeadLastLine();

  // int anteLineno = headLastLine.fLineno + 1;
   int oldeHeadLineno = headLastLine.fLineno;
   int level = headLastLine.fSubprooflevel;

   newline.fFormula=anteroot.copyFormula();
   newline.fJustification= fAssJustification;
   newline.fSubprooflevel= level+1;
   newline.fLastassumption=true;

   newEdit.fNewLines.add(newline);

   newline = supplyProofline();

   int conseLineno=addIfNotThere(conseroot, level+1, newEdit.fNewLines);

   if (conseLineno==-1)
     conseLineno=oldeHeadLineno +3;   // the assumption, the ?, and then it


  /* int conseLineno=fModel.lineNoOfLastSelectableEqualFormula(conseroot);

   if (conseLineno==-1){   // not there

     newFormula= new TFormula();

     newFormula.fInfo = "?";
     newFormula.fKind = TFormula.predicator;

     newline = supplyProofline();

     newline.fFormula = newFormula;
     newline.fSelectable = false;
     newline.fJustification = "?";
     newline.fSubprooflevel = level+1;

     newEdit.fNewLines.add(newline);

     newline = new TProofline(fParser);

     newline.fFormula = conseroot.copyFormula();
     newline.fJustification = "?";
     newline.fSubprooflevel = level+1;

     newEdit.fNewLines.add(newline);

     conseLineno=oldeHeadLineno +3;   // the assumption, the ?, and then it

   } */

   newEdit.fNewLines.add(endSubProof(level+1));
   newEdit.fNewLines.add(addImplication(anteroot, conseroot,level, conseLineno));



   /*********************?
    *
    */



         newEdit.doEdit();



}


/*

function TProofWindow.DoHintImplicI: TCommand;

  var
   Anteroot, Conseroot, conclusion: TFormula;
   Consequent, level: integer;
   firstnewline, secondnewline: TProofline;
   error: Boolean;
   aLineCommand: TLineCommand;

 begin

  DoHintImplicI := gNoChanges;
  error := false;
  conclusion := SELF.FindTailFormula;
  if (conclusion = nil) then
   error := true
  else if (conclusion.fInfo <> chImplic) then
   error := true;



  if error then
   BugAlert('With the tactic for �I, the conclusion must be an implication.')
  else
   begin

    anteroot := conclusion.fLlink;
    conseroot := conclusion.fRlink;

    New(aLineCommand);
    FailNIL(aLineCommand);
    aLineCommand.ILineCommand(cAddLine, SELF);

    level := TProofline(SELF.fHead.Last).fSubProofLevel;

    aLineCommand.fNewlines.InsertLast(Addassumption(Anteroot, false));

    AddIfNotThere(Conseroot, level + 1, Consequent, firstnewline, secondnewline);

    if Consequent = 0 then {not already there}
     begin
      aLineCommand.fNewlines.InsertLast(firstnewline);
      aLineCommand.fNewlines.InsertLast(secondnewline);
      Consequent := TProofline(fHead.Last).flineno + 3; {the lines are only in the}
{                                                                       command at this stage not}
{                                                                       the document}
     end;

    aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

    aLineCommand.fNewlines.InsertLast(AddImplication(Anteroot, Conseroot, level, Consequent));

    DoHintImplicI := aLineCommand;

   end;
 end;




*/


 /*

   function TProofWindow.AddImplication (Anteroot, Conseroot: TFormula; prooflevel, Consequent: integer): TProofLine;

    var
     formulanode: TFormula;
     newline: TProofLine;

   begin
    SupplyFormula(formulanode); {creates implication }
    with formulanode do
     begin
      fKind := binary;
      fInfo := chImplic;
      fllink := Anteroot.CopyFormula;
      fRlink := Conseroot.CopyFormula;
     end;

    SupplyProofline(newline); {newline points to new proofline}

    with newline do
     begin
      fSubprooflevel := prooflevel; {checkthis}
      fFormula := formulanode;
      ffirstjustno := Consequent;
      fjustification := ' �I';
     end;

    formulanode := nil;

    AddImplication := newline;
    newline := nil;
   end;



 */

TProofline addEquiv (TFormula anteroot, TFormula conseroot, int level, int antecedent, int consequent){
     TProofline newline=supplyProofline();
     TFormula formulanode = new TFormula();

     formulanode.fKind = TFormula.binary;
     formulanode.fInfo = String.valueOf(chEquiv);
     formulanode.fLLink = anteroot.copyFormula();
     formulanode.fRLink = conseroot.copyFormula();


     newline.fSubprooflevel = level;
     newline.fFormula = formulanode;
     newline.fFirstjustno = consequent;
     newline.fSecondjustno = antecedent;
     newline.fJustification = equivIJustification;

     return
         newline;



}




/*

 function TProofWindow.AddEquiv (Anteroot, Conseroot: TFormula; prooflevel, Antecedent, Consequent: integer): TProofLine;

  var
   newline: TProofLine;
   formulanode: TFormula;

 begin

  SupplyFormula(formulanode); {creates equiv }
  with formulanode do
   begin
    fKind := binary;
    fInfo := chEquiv;
    fllink := Anteroot.CopyFormula;
    fRlink := Conseroot.CopyFormula;
   end;

  SupplyProofline(newline); {newline points to new proofline}

  with newline do
   begin
    fSubprooflevel := prooflevel; {checkthis}
    fFormula := formulanode;
    ffirstjustno := Consequent;
    fsecondjustno := Antecedent;
    fjustification := ' �I';
   end;

  formulanode := nil;
  AddEquiv := newline;
  newline := nil;
 end;



 */


/*

 function TProofWindow.AddExTarget (targetroot: TFormula; prooflevel, one, two: integer): TProofLine;

  var
   newline: TProofLine;

 begin
  SupplyProofline(newline); {newline points to new proofline}

  with newline do
   begin
    fSubprooflevel := prooflevel; {checkthis}
    fFormula := targetroot.CopyFormula;
    ffirstjustno := one;
    fsecondjustno := two;
    fjustification := ' EI';
   end;

  AddExTarget := newline;
  newline := nil;
 end;


*/

TProofline addExTarget (TFormula targetroot, int level, int one,int two){
  TProofline newline=supplyProofline();



     newline.fSubprooflevel = level;
     newline.fFormula = targetroot.copyFormula();
     newline.fFirstjustno = one;
     newline.fSecondjustno = two;
     newline.fJustification = fEIJustification;

     return
         newline;



}

/*

    procedure TProofWindow.AddIfNotThere (whichone: TFormula; prooflevel: integer; var itslineno: integer; var firstnewline, secondnewline: TProofline);

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

       SupplyProofline(firstnewline);
       with firstnewline do
        begin
         fFormula := formulanode;
         fJustification := '?';
         fselectable := false;
         fSubProofLevel := prooflevel;
        end;

       SupplyProofline(secondnewline);
       with secondnewline do
        begin
         fFormula := whichone.CopyFormula;
         fJustification := '?';
         fSubProofLevel := prooflevel;
        end;
      end;
    end;


*/

int addIfNotThere(TFormula theFormula, int level, ArrayList lineList){
 /* {returns lineno if the formula is in proof and selectable, else supplies a question mark
  line and a formula ine and adds them to the line list}
 {and returns -1 as its line no } */

    int returnLineno=fModel.lineNoOfLastSelectableEqualFormula(theFormula);

     if (returnLineno==-1){   // not there

       TFormula newFormula= new TFormula();

       newFormula.fInfo = "?";
       newFormula.fKind = TFormula.predicator;

       TProofline newline = supplyProofline();

       newline.fFormula = newFormula;
       newline.fSelectable = false;
       newline.fJustification = "?";
       newline.fSubprooflevel = level;

       lineList.add(newline);

       newline = supplyProofline();

       newline.fFormula = theFormula.copyFormula();
       newline.fJustification = "?";
       newline.fSubprooflevel = level;

       lineList.add(newline);

     }

return
         returnLineno;

}


/*

 procedure TProofWindow.AddIfNotThere (whichone: TFormula; prooflevel: integer; var itslineno: integer; var firstnewline, secondnewline: TProofline);

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

     SupplyProofline(firstnewline);
     with firstnewline do
      begin
       fFormula := formulanode;
       fJustification := '?';
       fselectable := false;
       fSubProofLevel := prooflevel;
      end;

     SupplyProofline(secondnewline);
     with secondnewline do
      begin
       fFormula := whichone.CopyFormula;
       fJustification := '?';
       fSubProofLevel := prooflevel;
      end;
    end;
  end;


*/



TProofline addImplication (TFormula anteroot, TFormula conseroot, int level, int consequent){
  TProofline newline=supplyProofline();
     TFormula formulanode = new TFormula();

     formulanode.fKind = TFormula.binary;
     formulanode.fInfo = String.valueOf(chImplic);
     formulanode.fLLink = anteroot.copyFormula();
     formulanode.fRLink = conseroot.copyFormula();


     newline.fSubprooflevel = level;
     newline.fFormula = formulanode;
     newline.fFirstjustno = consequent;
     newline.fJustification = fImplicIJustification;

     return
         newline;
}



 void doImplicI(){
   TProofline subhead,subtail;
   int level;

   if (fTemplate)
     doHintImplicI();
   else{
     subtail=fProofListView.oneSelected();

     if (subtail!=null){
       subhead=fModel.findLastAssumption();

       if (subhead!=null){

         level=fModel.getHeadLastLine().fSubprooflevel;

         TUndoableProofEdit  newEdit = new TUndoableProofEdit();

         newEdit.fNewLines.add(endSubProof(level));
         newEdit.fNewLines.add(addImplication(subhead.fFormula, subtail.fFormula,level-1, subtail.fLineno));
         newEdit.doEdit();

       }

     }

   }

 }



/*************************** Rule of NegE **********************************/

 void doHintNegE(){
  TFormula newFormula=null;
  TProofline newline=null;
  TFormula conclusion =findNextConclusion();

  if (conclusion!=null){        //although MenuSetUp checks this

    TFormula doubleneg= new TFormula(TFormula.unary,
                                     String.valueOf(chNeg),
                                     null,
                                     new TFormula(TFormula.unary,
                                        String.valueOf(chNeg),
                                        null,
                                        conclusion));

    TUndoableProofEdit newEdit = new TUndoableProofEdit();

    TProofline headLastLine=fModel.getHeadLastLine();

    int level = headLastLine.fSubprooflevel;
    int lastlineno = headLastLine.fLineno;

    int doubleneglineno=addIfNotThere(doubleneg, level, newEdit.fNewLines);

    if (doubleneglineno==-1){   // not there
      doubleneglineno = lastlineno+2;
      lastlineno += 2;
    }

            newline = supplyProofline();

            newline.fFormula = conclusion.copyFormula();
            newline.fFirstjustno=doubleneglineno;

            newline.fJustification = negEJustification;
            newline.fSubprooflevel = level;

            newEdit.fNewLines.add(newline);

          newEdit.doEdit();

  }

}


void doNegE(){
  TProofline firstLine = fProofListView.oneSelected();

  if ( (firstLine != null) && fParser.isDoubleNegation(firstLine.fFormula)) {
    TProofline newline = supplyProofline();

    newline.fFormula = (firstLine.fFormula.fRLink.fRLink).copyFormula();
    newline.fFirstjustno = firstLine.fLineno;
    newline.fJustification = negEJustification;
    newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

    TUndoableProofEdit newEdit = new TUndoableProofEdit();
    newEdit.fNewLines.add(newline);
    newEdit.doEdit();
    return;
  }
  if (fTemplate) {

    TFormula conclusion = findNextConclusion();

    boolean noneSelected = (fProofListView.exactlyNLinesSelected(0)) != null;
    if ( (conclusion != null) && (noneSelected)) // we are going to allow ~E as a tactic
      doHintNegE();
  }
}
 /*
  function TProofWindow.DoNegE: TCommand;

  var

   firstline, newline: TProofline;
   aLineCommand: TLineCommand;

 begin

  DoNegE := gNoChanges;
  if fTextList.OneSelected(firstline) then
   begin
    if firstline.fFormula.NegationP then {one negation}
     begin
      if firstline.fFormula.fRlink.NegationP then {second negation}
       begin

       New(aLineCommand);
       FailNil(aLineCommand);
       aLineCommand.ILineCommand(cAddLine, SELF);

       SupplyProofline(newline);
       with newline do
       begin
       fFormula := firstline.fFormula.fRlink.fRlink.CopyFormula;
       ffirstjustno := firstline.fLineno;
       fJustification := ' ~E';
       fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
       end;

       aLineCommand.fNewlines.InsertLast(newline);
       newline := nil;
       DoNegE := aLineCommand;

       end;
     end;
   end;
 end;


  */


/********************** Rule of NegI ****************************/

 void doHintNegI(){

   if (!TPreferences.fUseAbsurd)
     doHintNegINoAbs();
   else{






     /* Two cases:- if the target is ~C we add C; if the target is C we add ~C.
          Gentzen does not permit the second case. But we will disable the menu
      in with Genzten rules. Then the subclasses can use it */




// TFormula newFormula=null;
     TProofline newline = supplyProofline();
     TFormula conclusion = findNextConclusion();

     TFormula negroot;

     if (fParser.isNegation(conclusion))
       negroot = conclusion.fRLink; //~C to C
     else
       negroot = new TFormula(TFormula.unary, //C to ~C
                              String.valueOf(chNeg),
                              null,
                              conclusion);

     //negroot = conclusion.fRLink;

     TUndoableProofEdit newEdit = new TUndoableProofEdit();

     TProofline headLastLine = fModel.getHeadLastLine();

     int oldeHeadLineno = headLastLine.fLineno;
     int level = headLastLine.fSubprooflevel;

     newline.fFormula = negroot.copyFormula();
     newline.fJustification = fAssJustification;
     newline.fSubprooflevel = level + 1;
     newline.fLastassumption = true;

     newEdit.fNewLines.add(newline);

     newline = supplyProofline();

     int absurdLineno = addIfNotThere(TFormula.fAbsurd, level + 1,
                                      newEdit.fNewLines);

     if (absurdLineno == -1) // not there

       absurdLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it

     /* int absurdLineno=fModel.lineNoOfLastSelectableEqualFormula(TFormula.fAbsurd);
      if (absurdLineno==-1){   // not there

        newFormula= new TFormula();

        newFormula.fInfo = "?";
        newFormula.fKind = TFormula.predicator;

        newline = new TProofline(fParser);

        newline.fFormula = newFormula;
        newline.fSelectable = false;
        newline.fJustification = "?";
        newline.fSubprooflevel = level+1;

        newEdit.fNewLines.add(newline);

        newline = new TProofline(fParser);

        newline.fFormula = TFormula.fAbsurd.copyFormula();
        newline.fJustification = "?";
        newline.fSubprooflevel = level+1;

        newEdit.fNewLines.add(newline);

        absurdLineno=oldeHeadLineno +3;   // the assumption, the ?, and then it

      }*/

     newEdit.fNewLines.add(endSubProof(level + 1));
     newEdit.fNewLines.add(addNegAssumption(negroot, level, absurdLineno, 0));

     newEdit.doEdit();
   }


}



 /*

   function TProofWindow.DoHintNegI: TCommand;

  var
   Negroot, Contraroot, neghornroot, conclusion: TFormula;
   absurdity: integer;
   firstnewline, secondnewline: TProofline;
   aLineCommand: TLineCommand;
   cancel, error: Boolean;
   level: integer;

 begin
  DoHintNegI := gNoChanges;

  error := false;
  conclusion := SELF.FindTailFormula;
  if (conclusion = nil) then
   error := true
  else if (conclusion.fInfo <> chNeg) then
   error := true;



  if error then
   BugAlert('With the tactic for ~I, the conclusion must be a negation.')
  else
   begin

    Negroot := conclusion.fRlink;


    New(aLineCommand);
    FailNIL(aLineCommand);
    aLineCommand.ILineCommand(cAddLine, SELF);

    level := TProofline(SELF.fHead.Last).fSubProofLevel;

    aLineCommand.fNewlines.InsertLast(Addassumption(Negroot, false));

    AddIfNotThere(gAbsurdFormula, level + 1, absurdity, firstnewline, secondnewline);

    if absurdity = 0 then {not already there}
     begin
      aLineCommand.fNewlines.InsertLast(firstnewline);
      aLineCommand.fNewlines.InsertLast(secondnewline);
      absurdity := TProofline(fHead.Last).flineno + 3; {the lines are only in the}
{                                                                       command at this stage not}
{                                                                       the document}
     end; (*****)

    aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

    aLineCommand.fNewlines.InsertLast(AddNegAssumption(Negroot, level, absurdity, 0));

    DoHintNegI := aLineCommand;

   end;
 end;


  */


void doHintNegINoAbs(){

JButton defaultButton;
JButton dropLastButton;
TProofInputPanel inputPane;

   TFormula conclusion = findNextConclusion();

 TFormula negroot;
 String justification;

 if (fParser.isNegation(conclusion)) {
   negroot = conclusion.fRLink; // C to ~C
   justification = fNegIJustification;
 }
 else {
   negroot = new TFormula(TFormula.unary, //~C to C
                          String.valueOf(chNeg),
                          null,
                          conclusion);
   justification = fNegEJustification; // need to fix this
 }





JTextField text = new JTextField("Positive Horn? Hint, one of: "+posForksAsString());

   text.setDragEnabled(true);
   text.selectAll();

   defaultButton = new JButton(new HintReductioNoAbs(text,"Go",negroot,conclusion,justification));

   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
   inputPane = new TProofInputPanel("Doing Reductio", text, buttons);


        addInputPane(inputPane);

        inputPane.getRootPane().setDefaultButton(defaultButton);
        fInputPane.setVisible(true); // need this
        text.requestFocus();         // so selected text shows


 }


 public boolean negIPossible(TProofline lastAssumption,
                      boolean oneSelected,
                      boolean twoSelected,
                      TFormula selectedFormula,
                      TFormula secondSelectedFormula,
                      int totalSelected){
   if ((lastAssumption!=null)&&
   (lastAssumption.fFormula!=null)   // not needed, blankstart
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

 void doNegI(){
   if (fTemplate){
     if (TPreferences.fUseAbsurd)
       doHintNegI();
     else{
       doHintNegINoAbs();
     }


   }
    else{
      TProofline lastAssumption=fModel.findLastAssumption();

      if (lastAssumption!=null){                           // if we haven't got a last assumption we cannot drop it
         TProofline firstLine=fProofListView.oneSelected();

         if (firstLine!=null)
           introduceFromContradiction(lastAssumption,firstLine);
         else{
            TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

            if (selections != null)
               introduceFromContradictoryLines(lastAssumption,selections);
      }

      }
    }
 }

void introduceFromContradictoryLines(TProofline lastAssumption,TProofline[] selections){
   if (TFormula.formulasContradict(selections[0].fFormula,selections[1].fFormula)){
      int level=fModel.getHeadLastLine().fSubprooflevel;
      TUndoableProofEdit  newEdit = new TUndoableProofEdit();

      newEdit.fNewLines.add(endSubProof(level));
      newEdit.fNewLines.add(addNegAssumption(lastAssumption.fFormula, level-1, selections[0].fLineno, selections[1].fLineno));
      newEdit.doEdit();
      }
   }


 void introduceFromContradiction(TProofline lastAssumption, TProofline firstLine){

   if ((lastAssumption!=null)&&
       fParser.isContradiction(firstLine.fFormula)) {
    int level=fModel.getHeadLastLine().fSubprooflevel;
    TUndoableProofEdit  newEdit = new TUndoableProofEdit();

    newEdit.fNewLines.add(endSubProof(level));
    newEdit.fNewLines.add(addNegAssumption(lastAssumption.fFormula, level-1, firstLine.fLineno, 0));
    newEdit.doEdit();
    }
 }


 /*

  function TProofWindow.DoNegI: TCommand;
    var
     newline, firstline, secondline, subhead: TProofline;
     aLineCommand: TLineCommand;
     ok: boolean;
     level: integer;

    procedure IntroduceFromAbsurdity;
    begin
     if FindLastAssumption(subhead) then
      begin
       if EqualFormulas(firstline.fFormula, gAbsurdFormula) then
        begin
         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);
         level := TProofline(SELF.fHead.Last).fSubprooflevel;
         aLineCommand.fNewlines.InsertLast(EndSubProof(level));
         aLineCommand.fNewlines.InsertLast(AddNegAssumption(subhead.fFormula, level - 1, firstline.fLineno, 0));
         DoNegI := aLineCommand;
        end;
      end;
    end;

    procedure IntroduceFromContradictoryLines;
    begin
     if FindLastAssumption(subhead) then
      begin
       ok := FALSE;
       if (firstline.fFormula.fKind = unary) then
        if Equalformulas(firstline.fFormula.fRlink, secondline.fFormula) then
         ok := TRUE;
       if not ok then
        if (secondline.fFormula.fKind = unary) then
         if Equalformulas(secondline.fFormula.fRlink, firstline.fFormula) then
         ok := TRUE;
       if ok then

        begin
         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);
         level := TProofline(SELF.fHead.Last).fSubprooflevel;

         aLineCommand.fNewlines.InsertLast(EndSubProof(level));

         aLineCommand.fNewlines.InsertLast(AddNegAssumption(subhead.fFormula, level - 1, firstline.fLineno, secondline.fLineno));

         DoNegI := aLineCommand;

        end;
      end;
    end;


   begin
    DoNegI := gNoChanges;

    if fTemplate then {gTemplate}
     DoNegI := DoHintNegI
    else
     begin
      if fTextList.OneSelected(firstline) then
       IntroduceFromAbsurdity
      else if fTextList.TwoSelected(firstline, secondline) then
       IntroduceFromContradictoryLines;
     end;
   end;


  */


 TProofline addNegAssumption(TFormula whichone, int level, int posHorn, int negHorn){
   TProofline newline=supplyProofline();
   TFormula formulanode = new TFormula();

   formulanode.fKind = TFormula.unary;
   formulanode.fInfo = String.valueOf(chNeg);
   formulanode.fRLink = whichone.copyFormula();

   newline.fSubprooflevel = level;
   newline.fFormula = formulanode;
   newline.fFirstjustno = posHorn;
   newline.fSecondjustno = negHorn;
   newline.fJustification = fNegIJustification;

   return
       newline;
 }

 /*

   function TProofWindow.AddNegAssumption (whichone: TFormula; prooflevel, PosHorn, NegHorn: integer): TProofLine;
  {creates and returns negation of assumption}

    var
     formulanode: TFormula;
     newline: TProofLine;

   begin

    SupplyFormula(formulanode);
    with formulanode do
     begin
      fKind := unary;
      fInfo := chNeg;
      fRlink := whichone.CopyFormula;
     end;

    SupplyProofline(newline); {newline points to new proofline}

    with newline do
     begin
      fSubprooflevel := prooflevel; {checkthis}
      fFormula := formulanode;
      ffirstjustno := PosHorn;
      fsecondjustno := NegHorn;
      fjustification := ' ~I';
     end;

    formulanode := nil;
    AddNegAssumption := newline;
    newline := nil;
   end;


  */


 /************************ Strategy of New Goal **********************************/



   public class NewGoalAction extends AbstractAction{
     JTextComponent fText;

     boolean fAfterLast;

      public NewGoalAction(JTextComponent text, String label, boolean afterLast){


        fText=text;
        fAfterLast=afterLast;


          putValue(NAME, label);


      }

       public void actionPerformed(ActionEvent ae){
      boolean useFilter =true;
      ArrayList dummy = new ArrayList();

      String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

      TFormula root = new TFormula();
      StringReader aReader = new StringReader(aString);

      boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);
      if (!wellformed){
          String message = "The string is illformed."+
                            (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

        //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

          fText.setText(message);
          fText.selectAll();
          fText.requestFocus();

           }
     else {

      int level= fModel.getHeadLastLine().fSubprooflevel;

      if (!fAfterLast)
        level= fModel.getTailLine(0).fSubprooflevel;   // before the tail line (after subproof)

      TFormula formulanode= new TFormula();

      formulanode.fInfo = "?";
      formulanode.fKind = TFormula.predicator;


      TProofline newline = supplyProofline();

      newline.fFormula=formulanode;
      newline.fJustification= questionJustification;
      newline.fSubprooflevel= level;
      newline.fSelectable= false;

      TUndoableProofEdit  newEdit = new TUndoableProofEdit();
      newEdit.fNewLines.add(newline);

      newline = supplyProofline();

      newline.fFormula=root;
      newline.fJustification= questionJustification;
      newline.fSubprooflevel= level;

      newEdit.fNewLines.add(newline);

      newEdit.doEdit();

      removeInputPane();};

    /*

      SupplyFormula(formulanode);
            formulanode.fInfo := '?';
            formulanode.fKind := predicator;

            SupplyProofline(newline); {newline points to new proofline}
            with newline do
             begin
              fFormula := formulanode;
              fJustification := '?';
              fselectable := FALSE;
              fSubprooflevel := level;
             end;

            aLineCommand.fNewlines.InsertLast(newline);
            newline := nil;

            SupplyProofline(newline); {newline points to new proofline}
            with newline do
             begin
              fSubprooflevel := level;
              fFormula := root;
              fJustification := '?';
             end;

            aLineCommand.fNewlines.InsertLast(newline);


 }*/
}

   }


boolean newGoalPossible(){
  if (fModel.getHead()!=null&&
      fModel.getHead().size()>0&&
      fModel.getTail()!=null&&
      fModel.getTail().size()>0)
     return
         true;
  else
    return
        false;
}


 void doNewGoal(){

/*This is similar to TI */

if (newGoalPossible()){


  JButton defaultButton;
  JButton dropLastButton;
  TProofInputPanel inputPane;
  boolean afterLast = true;

  JTextField text = new JTextField(
      "New Goal? Then click on question mark to work on a different sub-problem.");
  text.setDragEnabled(true);
  text.selectAll();

  TProofline lastLine = fModel.getHeadLastLine();
  TProofline tailFirstLine = fModel.getTailLine(0);

  if ( (tailFirstLine != null) &&
      lastLine.fSubprooflevel > tailFirstLine.fSubprooflevel) {

    /*If the insertion point is at the end of a subproof, the new goal can either go in
            continuing the subproof, or continuing outside it.*/

    defaultButton = new JButton(new NewGoalAction(text, "After Last", afterLast));
    dropLastButton = new JButton(new NewGoalAction(text, "Before Next",
        !afterLast));

    JButton[] buttons = {
        new JButton(new CancelAction()), dropLastButton,
        defaultButton}; // put cancel on left
    inputPane = new TProofInputPanel("New Goal",
                                     text, buttons,fInputPalette);
  }
  else {
    defaultButton = new JButton(new NewGoalAction(text, "Go", afterLast));

    JButton[] buttons = {
        new JButton(new CancelAction()),

        defaultButton}; // put cancel on left
    inputPane = new TProofInputPanel("New Goal",
                                     text, buttons,fInputPalette);

  }

  addInputPane(inputPane);

  fInputPane.setVisible(true); // need this
  text.requestFocus(); // so selected text shows
}

 }


 /*

   {$S ADoCommand}
   function TProofWindow.DoNewGoal: TCommand;

    var
     newline: TProofline;
     aLineCommand: TLineCommand;
     prompt, rad1, rad2: str255;
     root, formulanode: TFormula;
     cancel, choiceneeded: boolean;
     level: integer;

   begin
    DoNewGoal := gNoChanges;
    level := TProofline(fHead.Last).fSubprooflevel;
    if fTail.fSize <> 0 then
     choiceneeded := level <> TProofline(fTail.First).fSubprooflevel
    else
     choiceneeded := FALSE;

    begin
     GetIndString(prompt, kStringRSRCID, 2); { prompt := 'New Antecedent?';}
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
         level := TProofline(fTail.First).fSubprooflevel;

       New(aLineCommand);
       FailNil(aLineCommand);
       aLineCommand.ILineCommand(cAddLine, SELF);

       SupplyFormula(formulanode);
       formulanode.fInfo := '?';
       formulanode.fKind := predicator;
       SupplyProofline(newline); {newline points to new proofline}
       with newline do
        begin
         fFormula := formulanode;
         fJustification := '?';
         fselectable := FALSE;
         fSubprooflevel := level;
        end;

       aLineCommand.fNewlines.InsertLast(newline);
       newline := nil;

       SupplyProofline(newline); {newline points to new proofline}
       with newline do
        begin
         fSubprooflevel := level;
         fFormula := root;
         fJustification := '?';
        end;

       aLineCommand.fNewlines.InsertLast(newline);

       DoNewGoal := aLineCommand;

      end;

    end;

   end;



 */


/********************** Rule of UG ****************************/

 void doHintUG(){   // enabled only if appropriate



   TFormula conclusion =findNextConclusion();

   TFormula variForm = conclusion.quantVarForm();
   TFormula scope = conclusion.scope();

   TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variForm);

   if (freeFormula!=null){
     String message = "Not permitted "
         + fParser.writeFormulaToString(variForm)
         + " is free in "
         + fParser.writeFormulaToString(freeFormula);
     bugAlert("DoingUG. Warning.",message);
   }
   else{
      TUndoableProofEdit newEdit = new TUndoableProofEdit();

      TProofline headLastLine=fModel.getHeadLastLine();

      int level = headLastLine.fSubprooflevel;
      int lastlineno = headLastLine.fLineno;

      int scopelineno=addIfNotThere(scope, level, newEdit.fNewLines);

      if (scopelineno==-1){   // not there
         scopelineno = lastlineno+2;
         lastlineno += 2;
      }

           TProofline newline = supplyProofline();

           newline.fFormula = conclusion.copyFormula();
           newline.fFirstjustno=scopelineno;

           newline.fJustification = UGJustification;
           newline.fSubprooflevel = level;

           newEdit.fNewLines.add(newline);

         newEdit.doEdit();


   }


 }




/*

  function TProofWindow.DoHintUG: TCommand;
   var
    variForm, scope, conclusion, freeFormula: TFormula;
    scopelineno, genlineno, level: integer;
    firstnewline, secondnewline: TProofline;
    error, freevar: Boolean;
    aLineCommand: TLineCommand;
    outPutStr: str255;

   procedure TestFree (item: TObject);

    var
     aProofline: TProofline;

   begin
    if not freevar then
     begin
      aProofline := TProofline(item);
      if aProofline.fselectable then
       if aProofline.fJustification = 'Ass' then
        freevar := aProofline.fFormula.Freetest(variForm);
      if freevar then
       freeformula := aProofline.fFormula;
     end;
   end;

  begin
   DoHintUG := gNoChanges;

   error := false;
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
     scope := conclusion.Scope;

     freevar := false;

     fHead.Each(TestFree);

     if freevar then
      begin
       fParser.WriteFormulaToString(freeformula, outPutStr);

       BugAlert(concat('Not permitted ', conclusion.QuantVar, ' is free in ', outputStr));

      end
     else
      begin

       New(aLineCommand);
       FailNIL(aLineCommand);
       aLineCommand.ILineCommand(cAddLine, SELF);

       level := TProofline(SELF.fHead.Last).fSubProofLevel;
       genlineno := TProofline(fHead.Last).flineno + 1;


       AddIfNotThere(scope, level, scopelineno, firstnewline, secondnewline);


       if scopelineno = 0 then {not already there}
        begin
        aLineCommand.fNewlines.InsertLast(firstnewline);
        aLineCommand.fNewlines.InsertLast(secondnewline);
        scopelineno := genlineno + 1;
        genlineno := genlineno + 2;
        end;


       aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, scopelineno, 0, 0, ' UG'));

       DoHintUG := aLineCommand;

      end;
    end;
  end;



 */




 public class UGAction extends AbstractAction{
      JTextComponent fText;
      TProofline fFirstline=null;




       public UGAction(JTextComponent text, String label, TProofline firstline){
         putValue(NAME, label);

         fText=text;
         fFirstline=firstline;
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
          else {

            TFormula variablenode= new TFormula();

            variablenode.fKind = TFormula.variable;
            variablenode.fInfo = aString;

            // test for free

            TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variablenode);

            if (freeFormula!=null){
              String message = aString + " is free in " + fParser.writeFormulaToString(freeFormula);

               fText.setText(message);
               fText.selectAll();
               fText.requestFocus();
            }
            else{

              TFormula formulanode = new TFormula();

              formulanode.fKind = TFormula.quantifier;
              formulanode.fInfo = String.valueOf(chUniquant);
              formulanode.fLLink = variablenode;
              formulanode.fRLink = fFirstline.fFormula.copyFormula();

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
       inputPane = new TProofInputPanel("Doing UG", text, buttons);


       addInputPane(inputPane);

       inputPane.getRootPane().setDefaultButton(defaultButton);
       fInputPane.setVisible(true); // need this
       text.requestFocus();         // so selected text shows

     }



   }

 }

/*
  function TProofWindow.DoUG: TCommand;

    var

     firstline, newline: TProofline;
     aLineCommand: TLineCommand;
     freeformula, formulanode, variablenode: TFormula;
     outputStr, prompt: str255;
     cancel, found, freevar: boolean;
     variCh: char;

    procedure TestFree (item: TObject);

     var
      aProofline: TProofline;

    begin
     if not freevar then
      begin
       aProofline := TProofline(item);
       if aProofline.fselectable then
        if aProofline.fJustification = 'Ass' then
         freevar := aProofline.fFormula.Freetest(variablenode);
       if freevar then
        freeformula := aProofline.fFormula;
      end;
    end;

   begin

    DoUG := gNoChanges;
    cancel := FALSE;
    found := FALSE;
    freevar := FALSE;

    if fTemplate then {gTemplate}
     DoUG := DoHintUG
    else
     begin

      if fTextList.OneSelected(firstline) then
       begin

        GetIndString(prompt, kStringRSRCID, 17); { Variable of quantification }

        repeat
         begin
         if not GetTheChoice(strNull, strNull, prompt) then
         cancel := TRUE
         else if length(prompt) > 0 then
         begin
         if prompt[1] in gVariables then
         begin
         found := TRUE;
         variCh := prompt[1];
         end
         else
         begin
         prompt := concat(prompt, ' is not a variable.');
         BugAlert(prompt);
         end;
         end;
         end;

        until found or cancel;

        if not cancel then
         begin

         SupplyFormula(variablenode);
         with variablenode do
         begin
         fKind := variable;
         fInfo := StrofChar(variCh);
         end;


         fHead.Each(TestFree);

         if freevar then
         begin
         fParser.WriteFormulaToString(freeformula, outPutStr);

         BugAlert(concat(prompt, ' is free in ', outputStr));

         variablenode.DismantleFormula;
         end
         else
         begin



         SupplyFormula(formulanode);
         with formulanode do
         begin
         fKind := quantifier;
         fInfo := chUniquant;
         fLlink := variablenode;
         fRlink := firstline.fFormula.CopyFormula;
         end;

         variablenode := nil;

         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         SupplyProofline(newline);
         with newline do
         begin
         fFormula := formulanode;
         ffirstjustno := firstline.fLineno;
         fJustification := ' UG';
         fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
         end;

         aLineCommand.fNewlines.InsertLast(newline);
         newline := nil;
         DoUG := aLineCommand;

         end;
         end;

       end;
     end;
   end;



 */


/*Use getTheTerm in UI and EG and HIntEG when you have time to debug them*/

/*term needs to be created by caller*/

private boolean getTheTerm(JTextComponent inOutText,TFormula term){
   boolean useFilter = true;
   ArrayList dummy = new ArrayList();
   String aString = TSwingUtilities.readTextToString(inOutText, TUtilities.defaultFilter);
   StringReader aReader = new StringReader(aString);
   boolean wellformed=false;

   wellformed=fParser.term(term,aReader);

   if (!wellformed) {
            String message = "The string is not a term." +
                (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

            //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

            inOutText.setText(message);
            inOutText.selectAll();
            inOutText.requestFocus();
          }
return
   wellformed;
}


/*************************** Rule of UI *************************/


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

          if (!wellformed) {
            String message = "The string is not a term." +
                (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

            //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

            fText.setText(message);
            fText.selectAll();
            fText.requestFocus();
          }


          /*********************/



        //  boolean useFilter=true;


       //   String aString = TSwingUtilities.readTextToString(fText, useFilter,
       //       TParser.defaultFilter, strNull);
//

          else {

            TFormula scope = fFirstline.fFormula.fRLink.copyFormula();

            if(!scope.freeForTest(term, fFirstline.fFormula.quantVarForm())){



              /*

                          begin
                       fParser.WriteFormulaToString(formula1, outPutStr);


                       outputStr := concat(' in ', outputStr, '.');

                       fParser.WriteTermToString(termForm, prompt);

                       BugAlert(concat(prompt, ' for ', StrOfChar(firstline.fFormula.QuantVar), outputStr, ' leads to capture.'));



*/


              String message = aString + " for " +
                               fFirstline.fFormula.quantVar()+
                               " in " +
                               fParser.writeFormulaToString(scope) +
                               " leads to capture. " +
                               "Use another term or Cancel";


               fText.setText(message);
               fText.selectAll();
               fText.requestFocus();
            }
            else{

            	scope.subTermVar(scope,term,fFirstline.fFormula.quantVarForm());
              /*

                          NewSubTermVar(formula1, termForm, firstline.fFormula.QuantVarForm);
                       termForm.DismantleFormula;

                       New(aLineCommand);
                       FailNil(aLineCommand);
                       aLineCommand.ILineCommand(cAddLine, SELF);

                       SupplyProofline(newline);
                       with newline do
                       begin
                       fFormula := formula1;
                       ffirstjustno := firstline.fLineno;
                       fJustification := ' UI';
                       fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
                       end;

                       aLineCommand.fNewlines.InsertLast(newline);
                       newline := nil;
                       DoUI := aLineCommand;


           }
}*/



              TProofline newline = supplyProofline();

              int level = fModel.getHeadLastLine().fSubprooflevel;

              newline.fFormula = scope;
              newline.fJustification = UIJustification;
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

    public void doUniqueE(){

      TProofline firstLine = fProofListView.oneSelected();

      if (firstLine != null&&fParser.isUnique(firstLine.fFormula)) {


        TFormula scope = firstLine.fFormula.expandUnique();

        if (scope == null) {
          bugAlert("DoingUniqueE. Warning.",
                   "There are no variables left to use in the expansion.");
        }
        else {

          TUndoableProofEdit newEdit = new TUndoableProofEdit();

          TProofline headLastLine = fModel.getHeadLastLine();

          int level = headLastLine.fSubprooflevel;
          int lastlineno = headLastLine.fLineno;


          TProofline newline = supplyProofline();

          newline.fFormula = scope.copyFormula();
          newline.fFirstjustno = lastlineno+1;

          newline.fJustification = uniqueEJustification;
          newline.fSubprooflevel = level;

          newEdit.fNewLines.add(newline);

          newEdit.doEdit();

        }
      }
      }

 /*
   function TProofWindow.DoUniqueE: TCommand;
    var
     firstline, newline: TProofline;
     aLineCommand: TLineCommand;
     formulanode: TFormula;
   begin
    DoUniqueE := gNoChanges;

    if fTextList.OneSelected(firstline) then
     begin
      if (firstline.fFormula.fKind = quantifier) then
       if firstline.fFormula.fInfo = chUnique then
        begin
         formulanode := firstline.fFormula.ExpandUnique;
         if formulanode = nil then
         BugAlert('There are no variables left to use in the expansion.')
         else
         begin

         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         SupplyProofline(newline);
         with newline do
         begin
         fFormula := formulanode;
         ffirstjustno := firstline.fLineno;
         fJustification := ' !E';
         fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
         end;

         aLineCommand.fNewlines.InsertLast(newline);
         newline := nil;
         formulanode := nil;
         DoUniqueE := aLineCommand;
         end;
        end;
     end;
 end;

*/

    public void doHintUniqueI(){ TFormula newFormula=null;
      TProofline newline=null;
      TFormula conclusion =findNextConclusion();

      if ((conclusion==null)||!fParser.isUnique(conclusion))
        bugAlert("DoingTacticsUniqueI. Warning.",
                 "With the tactic for "+chUnique+ "the conclusion must be a unique quantification.");
      // do not need this as menu is disabled if conditions not satisfied
      else{


    TFormula scope=conclusion.expandUnique();

    if (scope==null){
      bugAlert("DoingTacticsUniqueI. Warning.",
                 "There are no variables left to use in the expansion.");

    }
    else{

    }


        TUndoableProofEdit newEdit = new TUndoableProofEdit();

        TProofline headLastLine=fModel.getHeadLastLine();

        int level = headLastLine.fSubprooflevel;
        int lastlineno = headLastLine.fLineno;

        int scopelineno=addIfNotThere(scope, level, newEdit.fNewLines);

        if (scopelineno==-1){   // not there
          scopelineno = lastlineno+2;
          lastlineno += 2;
        }

                newline = supplyProofline();

                newline.fFormula = conclusion.copyFormula();
                newline.fFirstjustno=scopelineno;

                newline.fJustification = uniqueIJustification;
                newline.fSubprooflevel = level;

                newEdit.fNewLines.add(newline);

              newEdit.doEdit();

      }

    }



/*
      function TProofWindow.DoHintUniqueI: TCommand;
      var
       scope, conclusion: TFormula;
       scopelineno, genlineno, level: integer;
       firstnewline, secondnewline: TProofline;
       error: Boolean;
       aLineCommand: TLineCommand;

     begin

      DoHintUniqueI := gNoChanges;


      error := false;
      conclusion := SELF.FindTailFormula;
      if (conclusion = nil) then
       error := true
      else if (conclusion.fInfo <> chUnique) then
       error := true;

      if error then
       BugAlert('With the tactic for !I, the conclusion must be have �! as its main connective.')
      else
       begin
        scope := conclusion.ExpandUnique;
        if scope = nil then
         BugAlert('There are no variables left to use in the expansion.')
        else
         begin
          New(aLineCommand);
          FailNIL(aLineCommand);
          aLineCommand.ILineCommand(cAddLine, SELF);


          level := TProofline(SELF.fHead.Last).fSubProofLevel;
          genlineno := TProofline(fHead.Last).flineno + 1;

          AddIfNotThere(scope, level, scopelineno, firstnewline, secondnewline);


          if scopelineno = 0 then {not already there}
           begin
           aLineCommand.fNewlines.InsertLast(firstnewline);
           aLineCommand.fNewlines.InsertLast(secondnewline);
           scopelineno := genlineno + 1;
           genlineno := genlineno + 2;
           end;


          aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, scopelineno, 0, 0, ' !I'));

          DoHintUniqueI := aLineCommand;
         end;
       end;
     end;


 */



public void doUniqueI(){
  TFormula formulanode;

  if (fTemplate)
   doHintUniqueI();
 else {

TProofline firstLine = fProofListView.oneSelected();

   if (firstLine != null) {

     formulanode=firstLine.fFormula.abbrevUnique();

     if (formulanode==null){
       bugAlert("DoingUnique. Warning.", "Your formula does not have the right form.");

     }
     else{


       TProofline newline = supplyProofline();

       newline.fFormula = formulanode;
       newline.fFirstjustno = firstLine.fLineno;
       newline.fJustification = uniqueIJustification;
       newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

       TUndoableProofEdit newEdit = new TUndoableProofEdit();
       newEdit.fNewLines.add(newline);
       newEdit.doEdit();
     }
   }
 }
  }

/*
     function TProofWindow.DoUniqueI: TCommand;
      var
       firstline, newline: TProofline;
       aLineCommand: TLineCommand;
       formulanode: TFormula;

     begin
      if fTemplate then {gTemplate}
       DoUniqueI := DoHintUniqueI
      else
       begin
        DoUniqueI := gNoChanges;
        if fTextList.OneSelected(firstline) then
         begin
          begin
           formulanode := firstline.fFormula.AbbrevUnique;
           if formulanode = nil then
           BugAlert('Your formula does not have the right form.')
           else
           begin

           New(aLineCommand);
           FailNil(aLineCommand);
           aLineCommand.ILineCommand(cAddLine, SELF);

           SupplyProofline(newline);
           with newline do
           begin
           fFormula := formulanode;
           ffirstjustno := firstline.fLineno;
           fJustification := ' !I';
           fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
           end;

           aLineCommand.fNewlines.InsertLast(newline);
           newline := nil;
           formulanode := nil;
           DoUniqueI := aLineCommand;
           end;
          end;
         end;
       end;
     end;



 */


void doUI(){
  TProofline firstline;
  JButton defaultButton;
   JButton dropLastButton;
   TProofInputPanel inputPane;


  firstline=fProofListView.oneSelected();

  if ((firstline != null)&&fParser.isUniquant(firstline.fFormula)) {

    JTextField text = new JTextField("Term to instantiate with?");
       text.selectAll();

       defaultButton = new JButton(new UIAction(text,"Go", firstline));

       JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
       inputPane = new TProofInputPanel("Doing UI", text, buttons,fInputPalette);


       addInputPane(inputPane);

       inputPane.getRootPane().setDefaultButton(defaultButton);
       fInputPane.setVisible(true); // need this
       text.requestFocus();         // so selected text shows


     }


}


/*

 function TProofWindow.DoUI: TCommand;

   var

    firstline, newline: TProofline;
    aLineCommand: TLineCommand;
    formula1, termForm: TFormula;
    outputStr, prompt: str255;
    cancel: boolean;

  begin

   DoUI := gNoChanges;
   cancel := FALSE;

   if fTextList.OneSelected(firstline) then
    if firstline.fFormula.fInfo[1] = chUniquant then

     begin

      GetIndString(prompt, kStringRSRCID, 18); { Term }

      GetTheTerm(strNull, strNull, prompt, termForm, cancel);

      if not cancel then
       begin
        formula1 := firstline.fFormula.fRlink.CopyFormula;

        if not formula1.FreeForTest(termForm, firstline.fFormula.QuantVarForm) then{}
 {}
        begin
        fParser.WriteFormulaToString(formula1, outPutStr);


        outputStr := concat(' in ', outputStr, '.');

        fParser.WriteTermToString(termForm, prompt);

        BugAlert(concat(prompt, ' for ', StrOfChar(firstline.fFormula.QuantVar), outputStr, ' leads to capture.'));

        formula1.DismantleFormula;
        end
        else
        begin
        NewSubTermVar(formula1, termForm, firstline.fFormula.QuantVarForm);
        termForm.DismantleFormula;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ILineCommand(cAddLine, SELF);

        SupplyProofline(newline);
        with newline do
        begin
        fFormula := formula1;
        ffirstjustno := firstline.fLineno;
        fJustification := ' UI';
        fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DoUI := aLineCommand;

        end;

       end;
     end;
  end;



*/

/***************************************/

void doHintAbsI(){
JButton defaultButton;
JButton dropLastButton;
TProofInputPanel inputPane;


JTextField text = new JTextField("Positive Conjunct? Hint, one of: "+posForksAsString());

   text.setDragEnabled(true);
   text.selectAll();

   defaultButton = new JButton(new DoHintAbsI(text,"Go"));

   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
   inputPane = new TProofInputPanel("Doing Absurd I", text, buttons);


        addInputPane(inputPane);

        inputPane.getRootPane().setDefaultButton(defaultButton);
        fInputPane.setVisible(true); // need this
        text.requestFocus();         // so selected text shows
}


/*

 function TProofWindow.DoHintAbsI: TCommand;

   var
    Negroot, Contraroot, neghornroot, conclusion: TFormula;
    PosHorn, NegHorn: integer;
    prompt: str255;
    firstnewline, secondnewline: TProofline;
    aLineCommand: TLineCommand;
    cancel, error: Boolean;
    level: integer;

  begin
   DoHintAbsI := gNoChanges;

   error := false;
   conclusion := SELF.FindTailFormula;
   if (conclusion = nil) then
    error := true
   else if not EqualFormulas(conclusion, gAbsurdFormula) then
    error := true;



   if error then
    BugAlert('With the tactic for Abs I, the conclusion must be Absurd.')
   else
    begin
     PosHorn := 0;
     Neg
 := 0;

     cancel := false;
     prompt := strNull;

     prompt := strNull;

     if not cancel then

      begin
       GetIndString(prompt, kStringRSRCID, 23); { 'Positive conjunct?';}

       prompt := concat(prompt, chBlank, ListPosForks);

       GetTheRoot(strNull, strNull, prompt, Contraroot, cancel);

       prompt := strNull;

      end;

     if not cancel then
      begin

       New(aLineCommand);
       FailNIL(aLineCommand);
       aLineCommand.ILineCommand(cAddLine, SELF);

       level := TProofline(SELF.fHead.Last).fSubProofLevel;

       AddIfNotThere(Contraroot, level, PosHorn, firstnewline, secondnewline);

       if PosHorn = 0 then {not already there}
        begin
        aLineCommand.fNewlines.InsertLast(firstnewline);
        aLineCommand.fNewlines.InsertLast(secondnewline);
        PosHorn := TProofline(fHead.Last).flineno + 2; {the lines are only in the}
 {                                                                    command at this stage not the}
 {                                                                    document}
        end;

       SupplyFormula(neghornroot);
       with neghornroot do
        begin
        fKind := unary;
        fInfo := chNeg;
        fRlink := Contraroot;
        end;

       AddIfNotThere(neghornroot, level, NegHorn, firstnewline, secondnewline);

       neghornroot.fRlink := nil;
       neghornroot.DismantleFormula;

       if NegHorn = 0 then {not already there}
        begin
        aLineCommand.fNewlines.InsertLast(firstnewline);
        aLineCommand.fNewlines.InsertLast(secondnewline);
        if (PosHorn = TProofline(fHead.Last).flineno + 2) then
        NegHorn := TProofline(fHead.Last).flineno + 4
        else
        NegHorn := TProofline(fHead.Last).flineno + 2;

 (*the first is the case where there is no existing pos horn,*)
 (*the second is where a pos horn has been found.*)

        end;


       aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, PosHorn, NegHorn, 0, ' AbsI'));

       DoHintAbsI := aLineCommand;
      end;
    end;
  end;


*/

void doAbsI(){

    if (fTemplate)
      doHintAbsI();
    else {

      TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

      if (selections != null) {

        TProofline firstline = selections[0];
        TProofline secondline = selections[1];

        if (TFormula.formulasContradict(firstline.fFormula,secondline.fFormula)) {

            TProofline newline = supplyProofline();

            newline.fFormula = TFormula.fAbsurd.copyFormula();
            newline.fFirstjustno = firstline.fLineno;
            newline.fSecondjustno = secondline.fLineno;
            newline.fJustification = absIJustification;
            newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

            TUndoableProofEdit newEdit = new TUndoableProofEdit();

            newEdit.fNewLines.add(newline);

            newEdit.doEdit();

          }

        }

    }
  }

/*

 function TProofWindow.DoAbsI: TCommand;

   var
    newline, firstline, secondline: TProofline;
    aLineCommand: TLineCommand;
    ok: boolean;
    level: integer;

  begin
   DoAbsI := gNoChanges;
   if fTemplate then {gTemplate}
    DoAbsI := DoHintAbsI
   else
    begin
     if fTextList.TwoSelected(firstline, secondline) then
      begin
       ok := FALSE;
       if (firstline.fFormula.fKind = unary) then
        if Equalformulas(firstline.fFormula.fRlink, secondline.fFormula) then
        ok := TRUE;
       if not ok then
        if (secondline.fFormula.fKind = unary) then
        if Equalformulas(secondline.fFormula.fRlink, firstline.fFormula) then
        ok := TRUE;
       if ok then
        begin
        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ILineCommand(cAddLine, SELF);
        level := TProofline(SELF.fHead.Last).fSubprooflevel;

        SupplyProofline(newline);
        with newline do
        begin
        fFormula := gAbsurdFormula.CopyFormula;
        ffirstjustno := firstline.fLineno;
        fsecondjustno := secondline.fLineno;

        fJustification := concat(chBlank, 'AbsI');
        fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;

        DoAbsI := aLineCommand;

        end;
      end;
    end;
  end;



*/


/***************************************/


/*************************** Rule of AndI *************************/



void doHintAndI(){
  TFormula newFormula=null;
  TProofline newline=null;
  TFormula conclusion =findNextConclusion();

  if ((conclusion==null)||!fParser.isAnd(conclusion))
    bugAlert("Doing"+fAndIJustification+". Warning.",
             "With the tactic for "+chAnd+ "the conclusion must be a conjunction.");
  // do not need this as menu is disabled if conditions not satisfied
  else{

    TFormula leftconj = conclusion.fLLink;
    TFormula rightconj = conclusion.fRLink;

    TUndoableProofEdit newEdit = new TUndoableProofEdit();

    TProofline headLastLine=fModel.getHeadLastLine();

    int level = headLastLine.fSubprooflevel;
    int lastlineno = headLastLine.fLineno;

    int leftconjlineno=addIfNotThere(leftconj, level, newEdit.fNewLines);

    if (leftconjlineno==-1){   // not there
      leftconjlineno = lastlineno+2;
      lastlineno += 2;
    }

    int rightconjlineno=addIfNotThere(rightconj, level, newEdit.fNewLines);

    if (rightconjlineno==-1){   // not there
      rightconjlineno = lastlineno+2;
      lastlineno += 2;
    }



        /*fModel.lineNoOfLastSelectableEqualFormula(leftconj);

    if (leftconjlineno==-1){   // not there

      newFormula= new TFormula();

      newFormula.fInfo = "?";
      newFormula.fKind = TFormula.predicator;

      newline = supplyProofline();

      newline.fFormula = newFormula;
      newline.fSelectable = false;
      newline.fJustification = "?";
      newline.fSubprooflevel = level;

      newEdit.fNewLines.add(newline);

      newline = supplyProofline();

      newline.fFormula = leftconj.copyFormula();
      newline.fJustification = "?";
      newline.fSubprooflevel = level;
    //  newline.fLineno += 7;           the do edit sets the linennos

      newEdit.fNewLines.add(newline);

      leftconjlineno = conjlineno+1;
      conjlineno += 2;

    } */

  /*  int rightconjlineno=fModel.lineNoOfLastSelectableEqualFormula(rightconj);

     if (rightconjlineno==-1){   // not there

       newFormula= new TFormula();

       newFormula.fInfo = "?";
       newFormula.fKind = TFormula.predicator;

       newline = supplyProofline();

       newline.fFormula = newFormula;
       newline.fSelectable = false;
       newline.fJustification = "?";
       newline.fSubprooflevel = level;

       newEdit.fNewLines.add(newline);

       newline = supplyProofline();

       newline.fFormula = rightconj.copyFormula();
       newline.fJustification = "?";
       newline.fSubprooflevel = level;

       newEdit.fNewLines.add(newline);

       rightconjlineno = conjlineno+1;
       conjlineno += 2;

     }  */


            newline = supplyProofline();

            newline.fFormula = conclusion.copyFormula();
            newline.fFirstjustno=leftconjlineno;
            newline.fSecondjustno=rightconjlineno;

            newline.fJustification = fAndIJustification;
            newline.fSubprooflevel = level;

            newEdit.fNewLines.add(newline);

          newEdit.doEdit();





  }

}

/*

 function TProofWindow.DoHintAndI: TCommand;
   var
    leftconj, rightconj, conclusion: TFormula;
    leftconjlineno, rightconjlineno, conjlineno, Consequent, level: integer;
    firstnewline, secondnewline: TProofline;
    error: Boolean;
    aLineCommand: TLineCommand;

  begin
   DoHintAndI := gNoChanges;

   error := false;
   conclusion := SELF.FindTailFormula;
   if (conclusion = nil) then
    error := true
   else if (conclusion.fInfo <> chAnd) then
    error := true;



   if error then
    BugAlert('With the tactic for ^I, the conclusion must be a conjunction.')
   else
    begin

     leftconj := conclusion.fLlink;
     rightconj := conclusion.fRlink;

     New(aLineCommand);
     FailNIL(aLineCommand);
     aLineCommand.ILineCommand(cAddLine, SELF);

     level := TProofline(SELF.fHead.Last).fSubProofLevel;
     conjlineno := TProofline(fHead.Last).flineno + 1;


     AddIfNotThere(leftconj, level, leftconjlineno, firstnewline, secondnewline);


     if leftconjlineno = 0 then {not already there}
      begin
       aLineCommand.fNewlines.InsertLast(firstnewline);
       aLineCommand.fNewlines.InsertLast(secondnewline);
       leftconjlineno := conjlineno + 1;
       conjlineno := conjlineno + 2;
      end;

     AddIfNotThere(rightconj, level, rightconjlineno, firstnewline, secondnewline);


     if rightconjlineno = 0 then {not already there}
      begin
       aLineCommand.fNewlines.InsertLast(firstnewline);
       aLineCommand.fNewlines.InsertLast(secondnewline);
       rightconjlineno := conjlineno + 1;
       conjlineno := conjlineno + 2;
      end;

     aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, leftconjlineno, rightconjlineno, 0, ' ^I'));

     DoHintAndI := aLineCommand;

    end;
  end;


*/


 void doAndI(){
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

           */

          TProofline lastLine = fModel.getHeadLastLine();

          /*  if (lastLine.fSubprooflevel>lastLine.fHeadlevel)*/
          {

            leftButton = new JButton(new AndIOnLeftAction(text, "On Left",
                selections));
            rightButton = new JButton(new AndIOnRightAction(text, "On Right",
                selections));

            JButton[] buttons = {
                new JButton(new CancelAction()), leftButton,
                rightButton}; // put cancel on left
            inputPane = new TProofInputPanel("Doing"+ fAndIJustification,
                                             text, buttons);
          }

          addInputPane(inputPane);

        //  inputPane.getRootPane().setDefaultButton(defaultButton);
          fInputPane.setVisible(true); // need this
          text.requestFocus(); // so selected text shows
        }
   }
 }



 /*

  function TProofWindow.DoAndI: TCommand;

  var

   firstline, newline, secondline: TProofline;
   aLineCommand: TLineCommand;
   formula1, formulanode: TFormula;
   rad1, rad2, prompt: str255;

 begin

  DoAndI := gNoChanges;
  if fTemplate then {gTemplate}
   DoAndI := DoHintAndI
  else
   begin
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
       aLineCommand.ILineCommand(cAddLine, SELF);

       SupplyFormula(formulanode);
       with formulanode do
       begin
       fKind := binary;
       fInfo := chAnd;
       if fRadio then
       begin
       fllink := firstline.fFormula.CopyFormula;
       fRlink := secondline.fFormula.CopyFormula;
       end
       else
       begin
       fllink := secondline.fFormula.CopyFormula;
       fRlink := firstline.fFormula.CopyFormula;
       end
       end;

       SupplyProofline(newline);
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
       fJustification := concat(chBlank, chAnd, 'I');
       fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
       end;

       aLineCommand.fNewlines.InsertLast(newline);
       newline := nil;
       Do
   := aLineCommand;

       end;
     end;

    if fTextList.OneSelected(firstline) then
     begin
      SupplyFormula(formulanode);
      with formulanode do
       begin
       fKind := binary;
       fInfo := chAnd;
       fllink := firstline.fFormula.CopyFormula;
       fRlink := firstline.fFormula.CopyFormula;
       end;

      SupplyProofline(newline);
      with newline do
       begin
       fFormula := formulanode;
       ffirstjustno := firstline.fLineno;
       fsecondjustno := firstline.fLineno;
       fJustification := ' ^I';
       fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
       end;

      New(aLineCommand);
      FailNil(aLineCommand);
      aLineCommand.ILineCommand(cAddLine, SELF);

      aLineCommand.fNewlines.InsertLast(newline);
      newline := nil;
      DoAndI := aLineCommand;

     end;
   end;
 end;




   */









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
     newline.fJustification = fAndIJustification;//" ^I";
     newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

     TUndoableProofEdit newEdit = new TUndoableProofEdit();
     newEdit.fNewLines.add(newline);
     newEdit.doEdit();

}

/*

       if fTextList.OneSelected(firstline) then
        begin
         SupplyFormula(formulanode);
         with formulanode do
          begin
          fKind := binary;
          fInfo := chAnd;
          fllink := firstline.fFormula.CopyFormula;
          fRlink := firstline.fFormula.CopyFormula;
          end;

         SupplyProofline(newline);
         with newline do
          begin
          fFormula := formulanode;
          ffirstjustno := firstline.fLineno;
          fsecondjustno := firstline.fLineno;
          fJustification := ' ^I';
          fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
          end;

         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         aLineCommand.fNewlines.InsertLast(newline);
         newline := nil;
         DoAndI := aLineCommand;

        end;
      end;


      */

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
   newline.fJustification = fAndIJustification;
   newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

   TUndoableProofEdit newEdit = new TUndoableProofEdit();
   newEdit.fNewLines.add(newline);
   newEdit.doEdit();

removeInputPane();




         /*
           boolean useFilter =true;
        Vector dummy = new Vector();

        String aString= TSwingUtilities.readTextToString(fText,useFilter,TParser.defaultFilter,strNull);

        TFormula root = new TFormula();
        StringReader aReader = new StringReader(aSwffCheck(root, /*dummy, aReader)rmwffCheck(root, /*dummy, aReader)r.wffCheck(root, /*dummy, aReader);

        if (!wellformed){
            String message = "The string is illformed."+
                              (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

          //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

            fText.setText(message);
            fText.selectAll();
            fText.requestFocus();

             }
       else {
        TProofline newline = new TProofline();

        newline.fFormula=root;
        newline.fJustification= assJustification;
        newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel+1;
        newline.fLastassumption=true;

        TUndoableProofEdit  newEdit = new TUndoableProofEdit();
        newEdit.fNewLines.add(newline);
        newEdit.doEdit();

        removeInputPane();};  */
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
       newline.fJustification = fAndIJustification;
       newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

       TUndoableProofEdit newEdit = new TUndoableProofEdit();
       newEdit.fNewLines.add(newline);
       newEdit.doEdit();

    removeInputPane();




             /*
               boolean useFilter =true;
            Vector dummy = new Vector();

            String aString= TSwingUtilities.readTextToString(fText,useFilter,TParser.defaultFilter,strNull);

            TFormula root = new TFormula();
            StringReader aReader = new StringReader(aSwffCheck(root, /*dummy, aReader)llformwffCheck(root, /*dummy, aReader)arser.wffCheck(root, /*dummy, aReader);

            if (!wellformed){
                String message = "The string is illformed."+
                                  (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

              //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

                fText.setText(message);
                fText.selectAll();
                fText.requestFocus();

                 }
           else {
            TProofline newline = new TProofline();

            newline.fFormula=root;
            newline.fJustification= "Ass";
            newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel+1;
            newline.fLastassumption=true;

            TUndoableProofEdit  newEdit = new TUndoableProofEdit();
            newEdit.fNewLines.add(newline);
            newEdit.doEdit();

            removeInputPane();};  */
           }

         }





/*
          function TProofWindow.DoAndE: TCommand;

            var

             firstline, newline: TProofline;
             aLineCommand: TLineCommand;
             formula1: TFormula;
             rad1, rad2, prompt: str255;

           begin

            DoAndE := gNoChanges;

            if fTextList.OneSelected(firstline) then
             begin
              if (firstline.fFormula.fKind = binary) then
               if firstline.fFormula.fInfo = chAnd then
                begin
                 GetIndString(prompt, kStringRSRCID, 8); { Choose on buttons }
                 GetIndString(rad1, kStringRSRCID, 9); { ''On left?';}
                 GetIndString(rad2, kStringRSRCID, 10); { 'On right?'}

                 if GetTheChoice(rad1, rad2, prompt) then
                 begin
          {makes them choose left or}
          {                                                                         right}

                 New(aLineCommand);
                 FailNil(aLineCommand);
                 aLineCommand.ILineCommand(cAddLine, SELF);

                 SupplyProofline(newline);
                 with newline do
                 begin
                 if fRadio then
                 fFormula := firstline.fFormula.fllink.CopyFormula
                 else
                 fFormula := firstline.fFormula.fRlink.CopyFormula;
                 ffirstjustno := firstline.fLineno;
                 fJustification := ' ^E';
                 fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
                 end;

                 aLineCommand.fNewlines.InsertLast(newline);
                 newline := nil;
                 DoAndE := aLineCommand;
                 end;

                end;
             end;
           end;



*/

public class AndEAction extends AbstractAction{
   JTextComponent fText;
   boolean fLeft;
   TProofline fSelection;

    public AndEAction(JTextComponent text, String label,TProofline proofline, boolean onLeft){
      putValue(NAME, label);

      fText=text;

      fLeft=onLeft;
      fSelection=proofline;

    }

public void actionPerformed(ActionEvent ae){

   TProofline newline = supplyProofline();


   if (fLeft)
     newline.fFormula = fSelection.fFormula.fLLink.copyFormula();
    else
    newline.fFormula = fSelection.fFormula.fRLink.copyFormula();

   newline.fFirstjustno = fSelection.fLineno;

newline.fJustification = fAndEJustification;
newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

TUndoableProofEdit newEdit = new TUndoableProofEdit();
newEdit.fNewLines.add(newline);
newEdit.doEdit();

removeInputPane();


   }

 }


/*
   function TProofWindow.GetTheChoice (radioText1, radioText2: str255; var prompt: str255): boolean;

 {wish them to endorse their choice on radio buttons, or to return something through prompt}

   var
    cancel, allowApplicationToSleep: boolean;
    dismisser: IDType;
    itsDialogView: TDialogView;

  begin
   cancel := false;

   SetRadioText(radioText1, radioText2, false);

   BugAlert(prompt); {sets prompt}

   SetUpControls(false);

   itsDialogView := TDialogView(SELF.FindSubView('WND2'));
 {itsDialogView.fDismissed := false;}

   fIsModal := TRUE;

   allowApplicationToSleep := FALSE;

   InvalidateMenus;

   dismisser := MyPoseModally(itsDialogView);

   if (dismisser <> 'BUTe') then
    cancel := TRUE;

   fIsModal := false;
   SetRadioText(strNull, strNull, TRUE);
   if not cancel then
    prompt := EntryReply;

   BugAlert(strNull);
   SetUpControls(TRUE);
   GetTheChoice := not cancel;
  end;


 */

private void getTheChoice(Action leftAction,    //the calling actions must remove the input pane
                         Action rightAction,
                         String heading,String prompt){

  JButton leftButton=new JButton(leftAction);
  JButton rightButton=new JButton(rightAction);

  TProofInputPanel inputPane;

  JTextField text = new JTextField(
              prompt);
  text.selectAll();

  JButton[] buttons = {
     new JButton(new CancelAction()), leftButton,rightButton};

  inputPane = new TProofInputPanel(heading,text, buttons);

  addInputPane(inputPane);

  fInputPane.setVisible(true); // need this
  text.requestFocus(); // so selected text shows

}


 void doAndE(){
   TProofline firstLine=fProofListView.oneSelected();

   if ((firstLine!=null)&&fParser.isAnd(firstLine.fFormula)){

     {
          JButton leftButton;
          JButton rightButton;
          TProofInputPanel inputPane;

          JTextField text = new JTextField(
              "Choose which conjunct you would like.");
          text.selectAll();


          {

            boolean left=true;

            leftButton = new JButton(new AndEAction(text, "Left", firstLine,
                left));
            rightButton = new JButton(new AndEAction(text, "Right", firstLine,
                !left));

            JButton[] buttons = {
                new JButton(new CancelAction()), leftButton,
                rightButton}; // put cancel on left
            inputPane = new TProofInputPanel("Doing " + fAndEJustification,
                                             text, buttons);
          }

          addInputPane(inputPane);

       //   inputPane.getRootPane().setDefaultButton(defaultButton);
          fInputPane.setVisible(true); // need this
          text.requestFocus(); // so selected text shows
        }



   }


 }



 /************************ Rule of EG ****************************/


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

     /*We have here to get three things out of the User:- the term to generalize on,
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

         // boolean useFilter = true;
         // String message=null;
         // String aString=null;

         /* if (fTerm==null)   // First stage, trying to find the term
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
              findTerm();
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
     
      boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);
      
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





  /*

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

            fParser.WriteFormulaToString(root, outPutStr);

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


*/
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

/*

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



       */




          private void findTerm(){
    String message;


    String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

            TFormula term = new TFormula();
            StringReader aReader = new StringReader(aString);
            boolean wellformed = false;

            wellformed = fParser.term(term, aReader);

            if (!wellformed) {
              message = "The string is not a term." +
                  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

              //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

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
        (fNumOccurrences ==1) ){  */

      if (fNumOccurrences ==0){

    /*    if (fNumOccurrences ==1) {
          TFormula surgeryTerm = fCopy.nthFreeOccurence(fTerm, 1);

          if (surgeryTerm != null)
            alterCopy(surgeryTerm, fVariable);
           }  */


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


/*

         if not cancel then
               repeat
               begin
               found := FALSE;

               occurences := firstline.fFormula.NumofFreeOccurences(termForm);

               copyf := firstline.fFormula.CopyFormula;

               firstformula := firstline.fFormula;

               if occurences = 0 then

               begin

               prompt := 'Press Go to display the scope. Then Press Go again, (or Cancel or edit it).';

               if not GetTheChoice(strNull, strNull, prompt) then
               cancel := TRUE;

               end;

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



   */

  /*************************** Rule of UI *************************/


  public class HintEGAction extends AbstractAction{
        JTextComponent fText;
        TFormula fVariForm,fScope,fConclusion;


         public HintEGAction(JTextComponent text, String label, TFormula variForm, TFormula scope, TFormula conclusion){
           putValue(NAME, label);

           fText=text;
           fVariForm=variForm;
           fScope=scope;
           fConclusion=conclusion;
         }

          public void actionPerformed(ActionEvent ae){
            String message="";

            String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

            TFormula term = new TFormula();
            StringReader aReader = new StringReader(aString);
            boolean wellformed=false;

            wellformed=fParser.term(term,aReader);

            if (!wellformed) {
              message = "The string is not a term." +
                  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

              fText.setText(message);
              fText.selectAll();
              fText.requestFocus();
            }


            else {

             if(!fScope.freeForTest(term, fVariForm)){

                message = aString + " for " +
                                 fParser.writeFormulaToString(fVariForm)+
                                 " in " +
                                 fParser.writeFormulaToString(fScope) +
                                 " leads to capture. " +
                                 "Use another term or Cancel";


                 fText.setText(message);
                 fText.selectAll();
                 fText.requestFocus();
              }
              else{


/*

                                  begin
                          NewSubTermVar(scope, termForm, variForm);
                          termForm.DismantleFormula;
                   (**)



                          New(aLineCommand);
                          FailNIL(aLineCommand);
                          aLineCommand.ILineCommand(cAddLine, SELF);

                          level := TProofline(SELF.fHead.Last).fSubProofLevel;
                          genlineno := TProofline(fHead.Last).flineno + 1;


                          AddIfNotThere(scope, level, scopelineno, firstnewline, secondnewline);

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
                          aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, scopelineno, 0, 0, ' EG'));


*/



                boolean proofover = false;

                fScope.subTermVar(fScope, term, fVariForm);
                
                /*****this is UG
                 *
                 * TUndoableProofEdit newEdit = new TUndoableProofEdit();

      TProofline headLastLine=fModel.getHeadLastLine();

      int level = headLastLine.fSubprooflevel;
      int lastlineno = headLastLine.fLineno;

      int scopelineno=addIfNotThere(scope, level, newEdit.fNewLines);

      if (scopelineno==-1){   // not there
         scopelineno = lastlineno+2;
         lastlineno += 2;
      }

           TProofline newline = supplyProofline();

           newline.fFormula = conclusion.copyFormula();
           newline.fFirstjustno=scopelineno;

           newline.fJustification = UGJustification;
           newline.fSubprooflevel = level;

           newEdit.fNewLines.add(newline);

         newEdit.doEdit();


   }

                 *
                 *
                 *
                 *
                 *
                 *
                 *
                 * ******/

                TUndoableProofEdit newEdit = new TUndoableProofEdit();

                TProofline headLastLine = fModel.getHeadLastLine();

                int level = headLastLine.fSubprooflevel;
                int lastlineno = headLastLine.fLineno;

                int scopelineno = addIfNotThere(fScope, level, newEdit.fNewLines);

                if (scopelineno == -1) { // not there
                  scopelineno = lastlineno + 2;
                  lastlineno += 2;
                }
              //  else
               //   proofover = true;    CHANGED APRIL 06 TO BE LIKE UG

              //  if (proofover) {

                  TProofline newline = supplyProofline();

                  newline.fFormula = fConclusion.copyFormula();
                  newline.fJustification = EGJustification;
                  newline.fFirstjustno = scopelineno;
                  newline.fSubprooflevel = level;

                  newEdit.fNewLines.add(newline);

              //  }

              newEdit.doEdit();

               removeInputPane();


              }
            }

        }

      }



 public void doHintEG(){  // only enabled if viable
   JButton defaultButton;
   TProofInputPanel inputPane;

   TFormula conclusion = findNextConclusion();
   TFormula variForm = conclusion.quantVarForm();
   TFormula scope = conclusion.scope().copyFormula();


   JTextField text = new JTextField("Term that was generalized on?");
       text.selectAll();

       defaultButton = new JButton(new HintEGAction(text,"Go", variForm,scope,conclusion));

       JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
       inputPane = new TProofInputPanel("Doing"+EGJustification+" with Tactics.", text, buttons);


       addInputPane(inputPane);

       inputPane.getRootPane().setDefaultButton(defaultButton);
       fInputPane.setVisible(true); // need this
       text.requestFocus();         // so selected text shows


 }


/*

  function TProofWindow.DoHintEG: TCommand;
   var
    variForm, termForm, scope, conclusion: TFormula;
    scopelineno, genlineno, level: integer;
    firstnewline, secondnewline: TProofline;
    error, cancel, proofover: Boolean;
    aLineCommand: TLineCommand;
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
        aLineCommand.ILineCommand(cAddLine, SELF);

        level := TProofline(SELF.fHead.Last).fSubProofLevel;
        genlineno := TProofline(fHead.Last).flineno + 1;


        AddIfNotThere(scope, level, scopelineno, firstnewline, secondnewline);

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
        aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, scopelineno, 0, 0, ' EG'));

        DoHintEG := aLineCommand;

        end;
      end;
    end;
  end;



 */

/**************************  doIE **********************************/


/*
 function CapturePossible: boolean;
         var
          temp: boolean;
          i: integer;
        begin
         temp := FALSE;

         variablesInTerms := concat(alphaForm.FreeVariablesinFormula, gammaForm.FreeVariablesinFormula);
      {here we are not interested in the fact that they are free, we just want to know the variables}
      {and all variables in this context have to be free}

         if length(variablesInTerms) <> 0 then
          begin
           RemoveDuplicates(variablesInTerms);
           boundVars := firstline.fFormula.BoundVariablesInFormula;
           for i := 1 to length(boundVars) do
            begin
             chStr := 'a';  (*length byte*)
             chStr[1] := boundVars[i];
             if pos(chStr, variablesInTerms) <> 0 then
             temp := true;
            end;
          end;
         CapturePossible := temp;
        end;


 */
 
//Dec09
 String capturePossible(TFormula alpha, TFormula gamma, TFormula firstLineFormula){
 	  String capturable="";
 	  
 	  
 	  Set <String> atomicTermsInIdentity =alpha.atomicTermsInFormula();
 	      
 	  if (atomicTermsInIdentity.addAll(gamma.atomicTermsInFormula()))
 		  ;
 	
 	  String boundVars=firstLineFormula.boundVariablesInFormula();
 	  String search;

 	  for (int i=0;i<boundVars.length();i++){
 	    search=boundVars.substring(i,i+1);

 	    if (atomicTermsInIdentity.contains(search)){
 	      capturable=search;
 	      break;
 	    }

 	  }
 	  return
 	      capturable;
 	}
/*
String capturePossible(TFormula alpha, TFormula gamma, TFormula firstLineFormula){
  String capturable="";
  String atomicTermsInIdentity =alpha.atomicTermsInFormula()+gamma.atomicTermsInFormula();
  atomicTermsInIdentity=TUtilities.removeDuplicateChars(atomicTermsInIdentity);

  String boundVars=firstLineFormula.boundVariablesInFormula();
  String search;

  for (int i=0;i<boundVars.length();i++){
    search=boundVars.substring(i,i+1);

    if (atomicTermsInIdentity.indexOf(search)>-1){
      capturable=search;
      break;
    }

  }
  return
      capturable;
}
*/




void launchIEAction(TProofline firstline,TProofline secondline){


/*
      if CapturePossible then
                 begin
                 outPutStr := strNull;
                 fParser.WriteFormulaToString(firstline.fFormula, outPutStr);
                 BugAlert(concat('The variable ', chStr, ' occurs in the identity and is bound in ', outPutStr, ' . '));
                 end
                 else
                 begin


  */

TFormula alpha=secondline.getFormula().firstTerm();
TFormula gamma=secondline.getFormula().secondTerm();

String captured=capturePossible(alpha,   // alpha=gamma
                                gamma,
                                firstline.getFormula());


if (!captured.equals("")){

  bugAlert("Problems with free and bound variables (remedy: rewrite bound variable)",
      "The variable "+ captured + " occurs in the identity and is bound in "
      + fParser.writeFormulaToString(firstline.getFormula()));

}
else{


     // now we want to move into the substiuting bit



     JTextField text = new JTextField("Starting =E"); ////// HERE
     text.selectAll();

     IEAction launchAction =new IEAction(text, "Go", firstline,
         secondline);

     JButton defaultButton = new JButton(launchAction);

//     JButton defaultButton = new JButton(new IEAction(text, "Go", firstline,
//         secondline));

     JButton[] buttons = {
         new JButton(new CancelAction()), defaultButton}; // put cancel on left
     TProofInputPanel inputPane = new TProofInputPanel(
         "Doing Identity Elimination", text, buttons);

     addInputPane(inputPane);

     inputPane.getRootPane().setDefaultButton(defaultButton);
     fInputPane.setVisible(true); // need this
     text.requestFocus(); // so selected text shows  */

     launchAction.start();
   }
}

class FirstSecondAction extends AbstractAction{
  boolean fFirst=true;
  TProofline fFirstline;
  TProofline fSecondline;

  FirstSecondAction(boolean isFirst,TProofline firstline, TProofline secondline){
    if (isFirst)
       putValue(NAME, "First");
     else
       putValue(NAME, "Second");



     fFirst=isFirst;
     fFirstline=firstline;
     fSecondline=secondline;
  }

  public void actionPerformed(ActionEvent ae){

    if (!fFirst){       // if they want to subs in first, fine; otherwise we have to swap
      TProofline temp = fFirstline;
      fFirstline = fSecondline;
      fSecondline = temp; // now the secondline is the identity
    }


    removeInputPane();

    launchIEAction(fFirstline,fSecondline);

  }

}


private void orderForSwap(TProofline firstline, TProofline secondline){
/*{this determines which we are going to subs in-- they could both be identities}
// this launches or puts up a prelim dialog which itself launches
we want the identity as the second line and the formula it is substituted in as the first line */

   int dispatcher=0;
   int inFirst=0;
   int inSecond=0;

   if (fParser.isEquality(firstline.getFormula()))
     inSecond = (secondline.getFormula()).numOfFreeOccurrences(firstline.getFormula().firstTerm()) +
           (secondline.getFormula()).numOfFreeOccurrences(firstline.getFormula().secondTerm());

   if (fParser.isEquality(secondline.getFormula()))
     inFirst = (firstline.getFormula()).numOfFreeOccurrences(secondline.getFormula().firstTerm()) +
           (firstline.getFormula()).numOfFreeOccurrences(secondline.getFormula().secondTerm());

   if ((inFirst+inSecond)==0)
     return;                  //if neither appears in the other no substitution is possible


   if (fParser.isEquality(firstline.getFormula())){
     if (!fParser.isEquality(secondline.getFormula()))
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

 /*
  procedure OrderForSwap;

        {this determines which we are going to subs in-- they could both be identities}
          begin
           if firstline.fFormula.fKind = equality then
            begin
             if not (secondline.fFormula.fKind = equality) then
              begin {4}
               subsinfirst := FALSE;   (*the first is the identity*)
               newline := firstline;
               firstline := secondline;
               secondline := newline; {to make line 2 contain the equality}
               newline := nil;
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
               newline := firstline;
               firstline := secondline;
               secondline := newline; {to make line 2 contain the equality}
               newline := nil;
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
               newline := firstline;
               firstline := secondline;
               secondline := newline; {to make line 2 contain the equality}
               newline := nil;
               end;{4}
               end;{3}
              end;
            end;
         end;

*/

public void doIE(){
 TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

   if (selections != null) {
    TProofline firstline =  selections[0];
    TProofline secondline =  selections[1];

    if (fParser.isEquality(firstline.getFormula())||
        fParser.isEquality(secondline.getFormula())){
           orderForSwap(firstline, secondline); // this launches or puts up a prelim dialog which launches
        //{we allow substitution is any formula provided no variable in the equality is bound in the formula}
    }
  }
}

/*
        function TProofWindow.DoIE: TCommand;

        var
         copyf, firstformula, currentNode, currentCopyNode, alphaForm, gammaForm, replacementForm: TFormula;
         firstline, secondline, newline: TProofline;
         subsinfirst, done, cancel, found, subFormulaRewrite, useFirstTerm: boolean;
         aLineCommand: TLineCommand;
         occurences, inFirst, inSecond, metSoFar, i: integer;
         prompt, outputStr, variablesInTerms, boundVars: str255;
         chStr: string[1];


        procedure OrderForSwap;

      {this determines which we are going to subs in-- they could both be identities}
        begin
         if firstline.fFormula.fKind = equality then
          begin
           if not (secondline.fFormula.fKind = equality) then
            begin {4}
             subsinfirst := FALSE;   (*the first is the identity*)
             newline := firstline;
             firstline := secondline;
             secondline := newline; {to make line 2 contain the equality}
             newline := nil;
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
             newline := firstline;
             firstline := secondline;
             secondline := newline; {to make line 2 contain the equality}
             newline := nil;
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
             newline := firstline;
             firstline := secondline;
             secondline := newline; {to make line 2 contain the equality}
             newline := nil;
             end;{4}
             end;{3}
            end;
          end;
        end;

        function CapturePossible: boolean;
         var
          temp: boolean;
          i: integer;
        begin
         temp := FALSE;

         variablesInTerms := concat(alphaForm.FreeVariablesinFormula, gammaForm.FreeVariablesinFormula);
      {here we are not interested in the fact that they are free, we just want to know the variables}
      {and all variables in this context have to be free}

         if length(variablesInTerms) <> 0 then
          begin
           RemoveDuplicates(variablesInTerms);
           boundVars := firstline.fFormula.BoundVariablesInFormula;
           for i := 1 to length(boundVars) do
            begin
             chStr := 'a';  (*length byte*)
             chStr[1] := boundVars[i];
             if pos(chStr, variablesInTerms) <> 0 then
             temp := true;
            end;
          end;
         CapturePossible := temp;
        end;

        function SubFormCheck: boolean;
         var
          temp: boolean;
        begin
      {There is a potential problem here that we must look out for. If one term in the identity}
      {is a subterm of the other, eg y=f(y) and both occur in the original formula eg Gf(y)}
      {the we should ask whether substitution for y or f(y) is required and not permit both}

         temp := ((gammaForm.SubFormulaOccursInFormula(gammaForm, alphaForm) | gammaForm.SubFormulaOccursInFormula(alphaForm, gammaForm)) & (firstline.fFormula.NumofFreeOccurences(alphaForm) <> 0) & (firstline.fFormula.NumofFreeOccurences(gammaForm) <> 0));

         if temp then
          begin
           outPutStr := strNull;
           fParser.WriteFormulaToString(alphaForm, outPutStr);
           prompt := concat('Do you wish to substitute for ', outputStr, ' ?');

           if not GetTheChoice('Yes?', 'Other term?', prompt) then
            cancel := TRUE;
      {This sets fRadio}
           useFirstTerm := fRadio;
          end;{3}

         SubFormCheck := temp;
        end;




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
        chStr := ' ';

        occurences := 0;
        metSoFar := 0;
        inFirst := 0;
        insecond := 0;
        i := 0;
        prompt := strNull;
        outputStr := strNull;

        if fTextList.TwoSelected(firstline, secondline) then
         if (firstline.fFormula.fKind = equality) | (secondline.fFormula.fKind = equality) then
      {we allow substitution is any formula provided no variable in the equality is bound in the formula}

          begin
           OrderForSwap;  {secondline now contains identity}
           if not cancel then
            begin

             alphaForm := secondline.fFormula.FirstTerm;
             gammaForm := secondline.fFormula.SecondTerm;

             if CapturePossible then
             begin
             outPutStr := strNull;
             fParser.WriteFormulaToString(firstline.fFormula, outPutStr);
             BugAlert(concat('The variable ', chStr, ' occurs in the identity and is bound in ', outPutStr, ' . '));
             end
             else
             begin
             subFormulaRewrite := SubFormCheck;
             if not cancel then
             begin
             repeat
             begin {6}
             found := FALSE;

             replacementForm := gammaForm; {replacing alpha by gamma}

             occurences := firstline.fFormula.NumofFreeOccurences(alphaForm);

                                              {***}

             copyf := firstline.fFormula.CopyFormula;

             firstformula := firstline.fFormula;

             if (occurences > 0) & (not subformulaRewrite | useFirstTerm) then
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

             if (occurences > 0) & (not subformulaRewrite | not useFirstTerm) then
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


             if not cancel then
             if not EqualFormulas(firstline.fFormula, copyf) then
             begin
             New(aLineCommand);
             FailNil(aLineCommand);
             aLineCommand.ILineCommand(cAddLine, SELF);

             SupplyProofline(newline);

                                      {newline points to new proofline}
             with newline do
             begin {12}
             fFormula := copyf;
             ffirstjustno := firstline.fLineno;
             fsecondjustno := secondline.fLineno;
             fjustification := ' IE';
             fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
             end; {12}

             aLineCommand.fNewlines.InsertLast(newline);
             newline := nil;
             DoIE := aLineCommand;
             end;
             end;
             end;
            end;
          end;
       end;



   */


/**************************  doII **********************************/

public void doII(){
JButton defaultButton;
JButton dropLastButton;
TProofInputPanel inputPane;


JTextField text = new JTextField("Term?");

   text.setDragEnabled(true);
   text.selectAll();

   defaultButton = new JButton(new IIAction(text,"Go"));

   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
   inputPane = new TProofInputPanel("Doing Identity Introduction", text, buttons,fInputPalette);


        addInputPane(inputPane);

        inputPane.getRootPane().setDefaultButton(defaultButton);
        fInputPane.setVisible(true); // need this
        text.requestFocus();         // so selected text shows
}


/*
  function TProofWindow.DoII: TCommand;

  var
   newline: TProofline;
   aLineCommand: TLineCommand;
   formulanode, firstlink, secondlink, termForm: TFormula;
   prompt: str255;
   cancel: boolean;

 begin
  DoII := gNoChanges;
  cancel := FALSE;

          {GetIndString(prompt, kStringRSRCID, 19); }

  prompt := 'Term?';

  GetTheTerm(strNull, strNull, prompt, termForm, cancel);

  if not cancel then
   begin
    SupplyFormula(secondlink);
    with secondlink do
     begin
      fKind := kons;
      fLlink := termForm;
     end;

    SupplyFormula(firstlink);
    with firstlink do
     begin
      fKind := kons;
      fLlink := termForm.CopyFormula;
      fRlink := secondlink;
     end;

    SupplyFormula(formulanode);
    with formulanode do
     begin
      fKind := equality;
      fInfo := chEquals;
      fRlink := firstlink;
     end;

    New(aLineCommand);
    FailNil(aLineCommand);
    aLineCommand.ILineCommand(cAddLine, SELF);

    SupplyProofline(newline);
    with newline do
     begin
      fFormula := formulanode;
      fjustification := 'II';
      fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
     end;

    aLineCommand.fNewlines.InsertLast(newline);
    newline := nil;
    DoII := aLineCommand;
   end;
 end;


 */


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

         JTextField text = new JTextField("Term to generalize on?");
         text.selectAll();

      defaultButton = new JButton(new EGAction(text,"Go", firstline));

      JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
      inputPane = new TProofInputPanel("Doing EG-- Stage1, identifying term", text, buttons);


      addInputPane(inputPane);




      inputPane.getRootPane().setDefaultButton(defaultButton);
      fInputPane.setVisible(true); // need this
      text.requestFocus();         // so selected text shows


    }

   }



 }




 /*

   function TProofWindow.DoEG: TCommand;

    var

     firstline, newline: TProofline;
     aLineCommand: TLineCommand;
     firstformula, formulanode, root, copyf, currentNode, currentCopyNode, termForm, variForm: TFormula;
     outputStr, prompt: str255;
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

      if fTextList.OneSelected(firstline) then
       begin

        GetIndString(prompt, kStringRSRCID, 19); { Term }

        GetTheTerm(strNull, strNull, prompt, termForm, cancel);

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
         if prompt[1] in gVariables then
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

         occurences := firstline.fFormula.NumofFreeOccurences(termForm);

         copyf := firstline.fFormula.CopyFormula;

         firstformula := firstline.fFormula;

         if occurences = 0 then

         begin

         prompt := 'Press Go to display the scope. Then Press Go again, (or Cancel or edit it).';

         if not GetTheChoice(strNull, strNull, prompt) then
         cancel := TRUE;

         end;

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

         fParser.WriteFormulaToString(copyf, outPutStr);


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

         fParser.WriteFormulaToString(root, outPutStr);

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
         aLineCommand.ILineCommand(cAddLine, SELF);

         SupplyProofline(newline);
         with newline do
         begin
         fFormula := formulanode;
         ffirstjustno := firstline.fLineno;
         fJustification := ' EG';
         fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
         end;

         aLineCommand.fNewlines.InsertLast(newline);
         newline := nil;
         DoEG := aLineCommand;

         end;
       end;
     end;
   end;


 */


/************************ Rule of EI ****************************/


void doHintEI(){   //menu enabled only if existential selected and conclusion exists



  TProofline selection = fProofListView.oneSelected();
  TFormula selectedFormula=selection.fFormula;

  TFormula conclusion =findNextConclusion();

   TFormula variForm = selectedFormula.quantVarForm();
   TFormula scope = selectedFormula.scope();

   TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variForm);

   if (freeFormula!=null){                              //free in premises
     String message = "Not permitted "
         + fParser.writeFormulaToString(variForm)
         + " is free in "
         + fParser.writeFormulaToString(freeFormula);
     bugAlert("Doing"+fEIJustification+" with Tactics." +   " Warning.",message);
   }
   else{

     if (conclusion.freeTest(variForm)) { //free in B}

       String outPutStr = fParser.writeFormulaToString(conclusion);

       //  BugAlert(concat(firstline.fFormula.QuantVarForm.fInfo, ' is free in ', outputStr, '.'));

       bugAlert("Doing"+fEIJustification+" with Tactics." +   " Warning.",
                fParser.writeFormulaToString(variForm) +
                " is free in " +
                outPutStr +
                ".");

     }
     else {                     // everything ok

       TUndoableProofEdit newEdit = new TUndoableProofEdit();

       TProofline headLastLine = fModel.getHeadLastLine();



       int level = headLastLine.fSubprooflevel;
       int lastlineno = headLastLine.fLineno;

       TProofline newline = supplyProofline();


       newline.fFormula=scope.copyFormula();
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

/*

 function TProofWindow.DoHintEI: TCommand;

   var
    Targetroot, scope, freeformula, variForm: TFormula;
    variCh: char;
    Consequent, level: integer;
    found, cancel: Boolean;
    prompt, outPutStr: str255;
    firstline, firstnewline, secondnewline: TProofline;
    aLineCommand: TLineCommand;


   procedure TestFree (item: TObject);

    var
     aProofline: TProofline;

   begin
    if not found then
     begin
      aProofline := TProofline(item);
      if aProofline.fselectable then
       if aProofline.fJustification = 'Ass' then
        found := aProofline.fFormula.Freetest(variForm);
      if found then
       freeformula := aProofline.fFormula;
     end;
   end;

  begin
   DoHintEI := gNoChanges;

   cancel := false;

   if not (fTextList.TotalSelected = 1) then
    begin
     GetIndString(prompt, kStringRSRCID, 30); { 'exit?';}

     if not GetTheChoice(strNull, strNull, prompt) then
      cancel := true; {makes them choose left or right}

    end;

   if not cancel then
    if (fTextList.TotalSelected = 1) then
     begin
      if fTextList.OneSelected(firstline) then
       begin
        if not (firstline.fFormula.fInfo[1] = chExiquant) then
        begin
        BugAlert('Selected formula is not an existential.');
        end
        else
        begin
        targetroot := SELF.FindTailFormula;

        if (targetroot = nil) then
        BugAlert('With the tactic for EI, you must have a conclusion.')
        else
        begin

        prompt := strNull;

        variForm := firstline.fFormula.QuantVarForm;
        variCh := variForm.fInfo[1];

        if Targetroot.Freetest(variForm) then {not free in B}
        begin
        fParser.WriteFormulaToString(Targetroot, outPutStr);

        BugAlert(concat(StrofChar(variCh), ' is free in ', outPutStr, '.'));

        end
        else
        begin
        found := false; {looking for free in premises}

        found := false; {looking for free in premises}

        fHead.Each(TestFree);

        if found then
        begin
        fParser.WriteFormulaToString(freeformula, outPutStr);
        BugAlert(concat(StrofChar(variCh), ' is free in ', outPutStr, '.'));
        end;

        if not found then
        begin

        scope := firstline.fFormula.fRlink;

        New(aLineCommand);
        FailNIL(aLineCommand);
        aLineCommand.ILineCommand(cAddLine, SELF);

        level := TProofline(SELF.fHead.Last).fSubProofLevel;

        aLineCommand.fNewlines.InsertLast(Addassumption(scope, false));

        AddIfNotThere(Targetroot, level + 1, Consequent, firstnewline, secondnewline);

        if Consequent = 0 then {not already there}
        begin
        aLineCommand.fNewlines.InsertLast(firstnewline);
        aLineCommand.fNewlines.InsertLast(secondnewline);
        Consequent := TProofline(fHead.Last).flineno + 3; {the}
 {                                                  lines are only in the command at this stage not}
 {                                                  the document}
        end;

        aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

        aLineCommand.fNewlines.InsertLast(AddExTarget(Targetroot, level, firstline.flineno, Consequent));

        DoHintEI := aLineCommand;

        end;
        end;
        end;
        end;
       end;
     end;
  end;



*/

void doEI(){
   TFormula variForm, scope;

   TProofline subhead,subtail;

  if (fTemplate)
    doHintEI();
  else{

    TProofline firstline;
    TProofline[] selections = fProofListView.exactlyNLinesSelected(2);

     if (selections != null){

       firstline = selections[0];
       subtail = selections[1];

       if (fParser.isExiquant(firstline.fFormula)) {

         variForm = firstline.fFormula.quantVarForm();
         scope = firstline.fFormula.scope();

         subhead = fModel.findLastAssumption();

         if (subhead != null) {

           if (scope.equalFormulas(scope, subhead.fFormula)) {
             if (subtail.fFormula.freeTest(variForm)) { //free in B}

               String outPutStr = fParser.writeFormulaToString(subtail.fFormula);

               //  BugAlert(concat(firstline.fFormula.QuantVarForm.fInfo, ' is free in ', outputStr, '.'));

               bugAlert("DoingEI. Warning.", firstline.fFormula.quantVar() +
                        " is free in " +
                        outPutStr +
                        ".");

             }
             else {                                         // looking for free in premises

               // test for free

            TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variForm);  //check this, pasted from UG

            /* now, it is allowed to be free in the assumption of the instantion, so .... */

            if ((freeFormula!=null)&&freeFormula.equalFormulas(freeFormula,subhead.fFormula))
              freeFormula=null;   // this one does not count

            if (freeFormula!=null){
              bugAlert("DoingEI. Warning.", firstline.fFormula.quantVar() +
                       " is free in " +
                       fParser.writeFormulaToString(freeFormula) +
                       ".");
            }
            else {

               int level=fModel.getHeadLastLine().fSubprooflevel;

               TUndoableProofEdit  newEdit = new TUndoableProofEdit();

               newEdit.fNewLines.add(endSubProof(level));
               newEdit.fNewLines.add(addExTarget(subtail.fFormula, level-1, firstline.fLineno,subtail.fLineno));
               newEdit.doEdit();

            }




             }
           }

         }
       }
     }






  }

}

/*
 function TProofWindow.DoEI: TCommand;

   var

    firstline, subhead, subtail, newline: TProofline;
    aLineCommand: TLineCommand;
    variCh: char;
    found: boolean;
    outputStr: str255;
    level: integer;

    exTarget, freeformula, formulanode, variForm: TFormula;

   procedure TestFree (item: TObject);

    var
     aProofline: TProofline;

   begin
    if not found then
     begin
      aProofline := TProofline(item);
      if aProofline.fselectable then
       if aProofline.fJustification = 'Ass' then
        if not Equalformulas(aProofline.fFormula, subhead.fFormula) then
        found := aProofline.fFormula.Freetest(variForm);
      if found then
       freeformula := aProofline.fFormula;
     end;
   end;

  begin
   DoEI := gNoChanges;

   if fTemplate then {gTemplate}
    DoEI := DoHintEI
   else
    begin

     if fTextList.TwoSelected(firstline, subtail) then

      if firstline.fFormula.fInfo[1] = chExiquant then
       begin
        variForm := firstline.fFormula.QuantVarForm;
        if FindLastAssumption(subhead) then
        begin
        if Equalformulas(firstline.fFormula.fRlink, subhead.fFormula) then
        if subtail.fFormula.Freetest(variForm) then {not free in B}
        begin
        fParser.WriteFormulaToString(subtail.fFormula, outPutStr);

        BugAlert(concat(firstline.fFormula.QuantVarForm.fInfo, ' is free in ', outputStr, '.'));

        end
        else
        begin
        found := FALSE; {looking for free in premises}

        fHead.Each(TestFree);

        if found then
        begin
        fParser.WriteFormulaToString(freeformula, outPutStr);

        BugAlert(concat(firstline.fFormula.QuantVarForm.fInfo, ' is free in ', outputStr, '.'));
        end;

        if not found then
        begin

        level := TProofline(SELF.fHead.Last).fSubprooflevel;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ILineCommand(cAddLine, SELF);

        aLineCommand.fNewlines.InsertLast(EndSubProof(level));

        aLineCommand.fNewlines.InsertLast(AddExTarget(subtail.fFormula, level - 1, firstline.fLineno, subtail.fLineno));

        DoEI := aLineCommand;

        end;
        end;
        end;
       end;
    end;
  end;


*/


 /************************ Rule of EquivE ****************************/

 public class EquivEAction extends AbstractAction{
   JTextComponent fText;
   boolean fLeft;
   TProofline fSelection;

  public EquivEAction(JTextComponent text, String label,TProofline proofline, boolean onLeft){
      putValue(NAME, label);

      fText=text;

      fLeft=onLeft;
      fSelection=proofline;
    }

public void actionPerformed(ActionEvent ae){

   TProofline newline = supplyProofline();

   int level=fModel.getHeadLastLine().fSubprooflevel;

   TFormula formulanode = new TFormula();

   formulanode.fKind = TFormula.binary;
   formulanode.fInfo = String.valueOf(chImplic);



   if (fLeft){
     formulanode.fLLink = fSelection.fFormula.fLLink.copyFormula();
     formulanode.fRLink = fSelection.fFormula.fRLink.copyFormula();
   }
   else {
   formulanode.fLLink = fSelection.fFormula.fRLink.copyFormula();
   formulanode.fRLink = fSelection.fFormula.fLLink.copyFormula();
   }

   newline.fFormula = formulanode;

   newline.fFirstjustno = fSelection.fLineno;

   newline.fJustification = equivEJustification;
   newline.fSubprooflevel = level;

   TUndoableProofEdit newEdit = new TUndoableProofEdit();
   newEdit.fNewLines.add(newline);
   newEdit.doEdit();

   removeInputPane();
   }

 }









void doEquivE(){
   TProofline firstLine=fProofListView.oneSelected();

    if ((firstLine!=null)&&fParser.isEquiv(firstLine.fFormula)){

      {
           JButton leftButton;
           JButton rightButton;
           TProofInputPanel inputPane;

           JTextField text = new JTextField(
               "Choose on buttons.");
           text.selectAll();


           {

             boolean left=true;

             leftButton = new JButton(new EquivEAction(text, "L" + chImplic+ "R", firstLine,
                 left));
             rightButton = new JButton(new EquivEAction(text, "R" + chImplic+ "L", firstLine,
                 !left));

             JButton[] buttons = {
                 new JButton(new CancelAction()), leftButton,
                 rightButton}; // put cancel on left
             inputPane = new TProofInputPanel("Doing " + chEquiv + "E",
                                              text, buttons);
           }

           addInputPane(inputPane);


           fInputPane.setVisible(true); // need this
           text.requestFocus(); // so selected text shows
         }

    }

}


/*

  function TProofWindow.DoEquivE: TCommand;

    var

     firstline, newline: TProofline;
     aLineCommand: TLineCommand;
     formulanode: TFormula;
     rad1, rad2, prompt: str255;

   begin

    DoEquivE := gNoChanges;

    if fTextList.OneSelected(firstline) then
     begin
      if (firstline.fFormula.fKind = binary) then
       if firstline.fFormula.fInfo = chEquiv then
        begin
         GetIndString(prompt, kStringRSRCID, 14); { Choose on buttons }
         GetIndString(rad1, kStringRSRCID, 15); { Left �right;}
         GetIndString(rad2, kStringRSRCID, 16); { 'Right � left?'}

         if GetTheChoice(rad1, rad2, prompt) then
         begin {makes them choose left or}
  {                                                                         right}

         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         SupplyFormula(formulanode);
         formulanode.fKind := binary;
         formulanode.fInfo := chImplic;
         if fRadio then
         begin
         formulanode.fllink := firstline.fFormula.fllink.CopyFormula;
         formulanode.fRlink := firstline.fFormula.fRlink.CopyFormula;
         end
         else
         begin
         formulanode.fllink := firstline.fFormula.fRlink.CopyFormula;
         formulanode.fRlink := firstline.fFormula.fllink.CopyFormula;
         end;

         SupplyProofline(newline);
         with newline do
         begin
         fFormula := formulanode;
         ffirstjustno := firstline.fLineno;
         fJustification := ' �E';
         fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
         end;

         aLineCommand.fNewlines.InsertLast(newline);
         newline := nil;
         DoEquivE := aLineCommand;
         end;

        end;
     end;
   end;



*/

/************************ Rule of EquivI ****************************/

void doHintEquivI(){    //menu not enabled if not possible


  TFormula conclusion =findNextConclusion();

  if (conclusion!=null) { //menu enabled only it this is true


     TFormula anteroot = conclusion.fLLink;
     TFormula conseroot = conclusion.fRLink;


          TUndoableProofEdit newEdit = new TUndoableProofEdit();

          TProofline headLastLine=fModel.getHeadLastLine();


       int oldeHeadLineno = headLastLine.fLineno;
       int level = headLastLine.fSubprooflevel;

       TProofline newline = supplyProofline();

       newline.fFormula=anteroot.copyFormula();
       newline.fJustification= fAssJustification;
       newline.fSubprooflevel= level+1;
       newline.fLastassumption=true;

       newEdit.fNewLines.add(newline);

       int firstLineno=addIfNotThere(conseroot, level+1, newEdit.fNewLines);

       if (firstLineno==-1){
         firstLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
         oldeHeadLineno += 3;
       }

       newEdit.fNewLines.add(endSubProof(level+1));

       newline = supplyProofline();

       newline.fFormula=conseroot.copyFormula();
       newline.fJustification= fAssJustification;
       newline.fSubprooflevel= level+1;
       newline.fLastassumption=true;

       newEdit.fNewLines.add(newline);

       int secondLineno=addIfNotThere(anteroot, level+1, newEdit.fNewLines);

       if (secondLineno==-1){
         secondLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
         oldeHeadLineno += 3;
       }

       newEdit.fNewLines.add(endSubProof(level+1));


  newline = supplyProofline();

     newline.fFormula = conclusion.copyFormula();
     newline.fFirstjustno = firstLineno;
     newline.fSecondjustno = secondLineno;
     newline.fJustification = equivIJustification;
     newline.fSubprooflevel = level;

     newEdit.fNewLines.add(newline);

     newEdit.doEdit();



        }
}




/*

 function TProofWindow.DoHintEquivI: TCommand;

   var
    Anteroot, Conseroot, conclusion: TFormula;
    Antecedent, Consequent, level: integer;
    cancel, consequentThere, error: Boolean;
    aLineCommand: TLineCommand;
    firstnewline, secondnewline: TProofline;

  begin
   DoHintEquivI := gNoChanges;

   error := false;
   conclusion := SELF.FindTailFormula;
   if (conclusion = nil) then
    error := true
   else if (conclusion.fInfo <> chEquiv) then
    error := true;



   if error then
    BugAlert('With the tactic for �I, the conclusion must be an equivalence.')
   else
    begin

     anteroot := conclusion.fLlink;
     conseroot := conclusion.fRlink;


     Antecedent := 0;
     Consequent := 0;

     cancel := false;
     consequentThere := true;

     New(aLineCommand);
     FailNIL(aLineCommand);
     aLineCommand.ILineCommand(cAddLine, SELF);

     level := TProofline(SELF.fHead.Last).fSubProofLevel;

     aLineCommand.fNewlines.InsertLast(Addassumption(Anteroot, false));

     AddIfNotThere(Conseroot, level + 1, Consequent, firstnewline, secondnewline);

     if Consequent = 0 then {not already there}
      begin
       consequentThere := false;
       aLineCommand.fNewlines.InsertLast(firstnewline);
       aLineCommand.fNewlines.InsertLast(secondnewline);
       Consequent := TProofline(fHead.Last).flineno + 3; {the lines are only in the}
 {                                                                       command at this stage not}
 {                                                                       the document}
      end;

     aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

     aLineCommand.fNewlines.InsertLast(Addassumption(Conseroot, false));

     AddIfNotThere(Anteroot, level + 1, Antecedent, firstnewline, secondnewline);

     if Antecedent = 0 then {not already there}
      begin
       aLineCommand.fNewlines.InsertLast(firstnewline);
       aLineCommand.fNewlines.InsertLast(secondnewline);
       if consequentThere then
        Antecedent := TProofline(fHead.Last).flineno + 3 {the lines are only in the}
 {                                                                           command at this stage}
 {                                                                           not the document}
       else
        Antecedent := TProofline(fHead.Last).flineno + 6 {the lines are only in the}
 {                                                                           command at this stage}
 {                                                                           not the document}

      end;

     aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

     aLineCommand.fNewlines.InsertLast(AddEquiv(Anteroot, Conseroot, level, Antecedent, Consequent));

     DoHintEquivI := aLineCommand;

    end;
  end;


*/

class FiveLines {
  TProofline fFirstline=null;
  TProofline fSubhead1=null;
  TProofline fSubhead2=null;
  TProofline fSubtail1=null;
  TProofline fSubtail2=null;
}

boolean canDoEquivI(FiveLines lines){
  TProofline[] selected;
  if (fProofListView.totalSelected() == 2) { //must have this

    TProofline[][] subProofs = fProofListView.nSubProofsSelected(1);

    if (subProofs != null) {
      lines.fSubhead1 = subProofs[0][0];
      lines.fSubtail1 = subProofs[0][1];

      lines.fSubtail2 = fProofListView.oneSelected();

      if (lines.fSubtail2 != null) {
        // lines.fSubtail2 = (TProofline) selected[0];
        lines.fSubhead2 = fProofListView.fModel.findLastAssumption();
      }

    }

    /*This is the normal case and all heads and tails should be set. However..... */

    if ( (lines.fSubhead1 == null) || (lines.fSubhead2 == null) || (lines.fSubtail1 == null) ||
        (lines.fSubtail2 == null)) {

      /*  {In the normal way they choose two lines, but one is a selectable line and the other}
              {the last line of a subproof, so total selected = 2 but TwoSelected is false. However if they proof }
              {is of F=F Two selected will be true} */

      lines.fSubhead1 = null;
      lines.fSubhead2 = null;
      lines.fSubtail1 = null;
      lines.fSubtail2 = null;

      selected = fProofListView.exactlyNLinesSelected(2);

      if (selected != null) {
        lines.fSubtail1 = (TProofline) selected[0];
        lines.fSubtail2 = (TProofline) selected[1];

        if (lines.fSubtail1.fFormula.equalFormulas(lines.fSubtail1.fFormula,
                                            lines.fSubtail2.fFormula))
          lines.fSubhead1 = lines.fSubtail1;
        lines.fSubhead2 = fProofListView.fModel.findLastAssumption();
      }

    }

    /*We continue if everything is set*/

    if ( (lines.fSubhead1 != null) && (lines.fSubhead2 != null) && (lines.fSubtail1 != null) &&
        (lines.fSubtail2 != null)) {
      if (lines.fSubhead1.fFormula.equalFormulas(lines.fSubhead1.fFormula,
                                          lines.fSubtail2.fFormula) &&
          lines.fSubhead2.fFormula.equalFormulas(lines.fSubhead2.fFormula,
                                          lines.fSubtail1.fFormula)) {
        return true;
      }
    }
  }
  return
      false;
}

 void doEquivI(){
   TProofline subtail1 = null, subtail2 = null;
  // TProofline[] selected;

   if (fTemplate)
     doHintEquivI();
   else {

   FiveLines lines= new FiveLines();

   if (canDoEquivI(lines)){

       /******** OLD VERSION
     if (fProofListView.totalSelected() == 2) { //must have this

       TProofline[][] subProofs = fProofListView.nSubProofsSelected(1);

       if (subProofs != null) {
         subhead1 = subProofs[0][0];
         subtail1 = subProofs[0][1];

         subtail2 = fProofListView.oneSelected();

         if (subtail2 != null) {
           // subtail2 = (TProofline) selected[0];
           subhead2 = fProofListView.fModel.findLastAssumption();
         }

       }

       /*This is the normal case and all heads and tails should be set. However.....

       if ( (subhead1 == null) || (subhead2 == null) || (subtail1 == null) ||
           (subtail2 == null)) {

         /*  {In the normal way they choose two lines, but one is a selectable line and the other}
                 {the last line of a subproof, so total selected = 2 but TwoSelected is false. However if they proof }
                 {is of F=F Two selected will be true}

         subhead1 = null;
         subhead2 = null;
         subtail1 = null;
         subtail2 = null;

         selected = fProofListView.exactlyNLinesSelected(2);

         if (selected != null) {
           subtail1 = (TProofline) selected[0];
           subtail2 = (TProofline) selected[1];

           if (subtail1.fFormula.equalFormulas(subtail1.fFormula,
                                               subtail2.fFormula))
             subhead1 = subtail1;
           subhead2 = fProofListView.fModel.findLastAssumption();
         }

       }

       /*We continue if everything is set

       if ( (subhead1 != null) && (subhead2 != null) && (subtail1 != null) &&
           (subtail2 != null)) {
         if (subhead1.fFormula.equalFormulas(subhead1.fFormula,
                                             subtail2.fFormula) &&
             subhead2.fFormula.equalFormulas(subhead2.fFormula,
                                             subtail1.fFormula))*/

           subtail1=lines.fSubtail1;
           subtail2=lines.fSubtail2;


           int level = fModel.getHeadLastLine().fSubprooflevel;

           TUndoableProofEdit newEdit = new TUndoableProofEdit();

           newEdit.fNewLines.add(endSubProof(level));
           newEdit.fNewLines.add(addEquiv(subtail2.fFormula, subtail1.fFormula,
                                          level - 1,subtail2.fLineno,subtail1.fLineno));
           newEdit.doEdit();


   }
       }
     }



/*

  function TProofWindow.DoEquivI: TCommand;

   label
    99, 98;

   var
    strangecase: boolean;
    firstline, subhead1, subhead2, subtail1, subtail2: TProofline;

    aLineCommand: TLineCommand;
    formula1: TFormula;
    level: integer;

  begin

   DoEquivI := gNoChanges;

   if fTemplate then {gTemplate}
    DoEquivI := DoHintEquivI
   else
    begin

 {In the normal way they choose two lines, but one is a selectable line and the other}
 {the last line of a subproof, so total selected = 2 but TwoSelected is false. However if they proof }
 {is of F=F Two selected will be true}

     strangecase := fTextList.TwoSelected(subtail1, subtail2); {trying for F equiv F }

     if strangecase then
      begin
       if Equalformulas(subtail1.fFormula, subtail2.fFormula) then
        begin
        subhead1 := subtail1;
        goto 99;
        end
       else
        goto 98;
      end;

     if (fTextList.TotalSelected = 2) then
      if fTextList.OneSubProofSelected(subhead1, subtail1) then
       if fTextList.OneSelected(subtail2) then
 99:
        if FindLastAssumption(subhead2) then
        begin
        if (Equalformulas(subhead1.fFormula, subtail2.fFormula) and Equalformulas(subhead2.fFormula, subtail1.fFormula)) then
        begin

        level := TProofline(fHead.Last).fSubprooflevel;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ILineCommand(cAddLine, SELF);

        aLineCommand.fNewlines.InsertLast(EndSubProof(level));

        aLineCommand.fNewlines.InsertLast(AddEquiv(subtail2.fFormula, subtail1.fFormula, level - 1, subtail2.fLineno, subtail1.fLineno));

        DoEquivI := aLineCommand;

        end;
        end;
    end;
 98:
  end;



*/




  /************************ Rule of OrE ****************************/



void doHintvE(){

    TProofline orline=fProofListView.oneSelected();

    if ((orline != null)&&fParser.isOr(orline.fFormula)) {  //menu enabled only it this is true


      TFormula conclusion =findNextConclusion();

      if (conclusion!=null) { //menu enabled only it this is true

        TFormula leftDisjunct = orline.fFormula.fLLink;
        TFormula rightDisjunct = orline.fFormula.fRLink;

        TUndoableProofEdit newEdit = new TUndoableProofEdit();

        TProofline headLastLine=fModel.getHeadLastLine();


     int oldeHeadLineno = headLastLine.fLineno;
     int level = headLastLine.fSubprooflevel;

     TProofline newline = supplyProofline();

     newline.fFormula=leftDisjunct.copyFormula();
     newline.fJustification= fAssJustification;
     newline.fSubprooflevel= level+1;
     newline.fLastassumption=true;

     newEdit.fNewLines.add(newline);

     int firstLineno=addIfNotThere(conclusion, level+1, newEdit.fNewLines);

     if (firstLineno==-1){
       firstLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
       oldeHeadLineno += 3;
     }

     newEdit.fNewLines.add(endSubProof(level+1));

     newline = supplyProofline();

     newline.fFormula=rightDisjunct.copyFormula();
     newline.fJustification= fAssJustification;
     newline.fSubprooflevel= level+1;
     newline.fLastassumption=true;

     newEdit.fNewLines.add(newline);

     int secondLineno=addIfNotThere(conclusion, level+1, newEdit.fNewLines);

     if (secondLineno==-1){
       secondLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
       oldeHeadLineno += 3;
     }

     newEdit.fNewLines.add(endSubProof(level+1));
   //  newEdit.fNewLines.add(addImplication(anteroot, conseroot,level, conseLineno));


newline = supplyProofline();

   newline.fFormula = conclusion.copyFormula();
   newline.fFirstjustno = orline.fLineno;
   newline.fSecondjustno = firstLineno;
   newline.fThirdjustno = secondLineno;
   newline.fJustification = fOrEJustification;
   newline.fSubprooflevel = level;

   newEdit.fNewLines.add(newline);

   newEdit.doEdit();



      }
       }


}


  /*

   function TProofWindow.DoHintvE: TCommand;

     var
      Targetroot, left, right: TFormula;
      FirstSub, SecondSub, level: integer;
      firstThere, cancel: Boolean;
      prompt: str255;
      aLineCommand: TLineCommand;
      firstline, firstnewline, secondnewline: TProofline;

    begin
     DoHintvE := gNoChanges;

     cancel := false;

     if not (fTextList.TotalSelected = 1) then
      begin
       GetIndString(prompt, kStringRSRCID, 29);

                  {prompt:='Select a disjunct formula, and press go.';}

       if not GetTheChoice(strNull, strNull, prompt) then
        cancel := true; {makes them choose left or right}

      end;

     if not cancel then
      if (fTextList.TotalSelected = 1) then
       begin
        if fTextList.OneSelected(firstline) then
         begin
          if not (firstline.fFormula.fInfo = chOr) then
          begin
          BugAlert('Selected formula is not a disjunct.');

          end
          else
          begin

          targetroot := SELF.FindTailFormula;

          if (targetroot = nil) then
          BugAlert('With the tactic for �E, you must have a conclusion.')
          else
          begin

          FirstSub := 0;
          SecondSub := 0;
          firstThere := true;

          New(aLineCommand);
          FailNIL(aLineCommand);
          aLineCommand.ILineCommand(cAddLine, SELF);

          level := TProofline(SELF.fHead.Last).fSubProofLevel;

          left := firstline.fFormula.fllink;

          aLineCommand.fNewlines.InsertLast(Addassumption(left, false));

          AddIfNotThere(Targetroot, level + 1, FirstSub, firstnewline, secondnewline);

          if FirstSub = 0 then {not already there}
          begin
          firstThere := false;
          aLineCommand.fNewlines.InsertLast(firstnewline);
          aLineCommand.fNewlines.InsertLast(secondnewline);
          FirstSub := TProofline(fHead.Last).flineno + 3 {the lines are}
   {                                                    only in the command at this stage not the}
   {                                                    document}
          end;

          aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

          right := firstline.fFormula.fRlink;

          aLineCommand.fNewlines.InsertLast(Addassumption(right, false));

          AddIfNotThere(Targetroot, level + 1, SecondSub, firstnewline, secondnewline);

          if SecondSub = 0 then {not already there}
          begin
          aLineCommand.fNewlines.InsertLast(firstnewline);
          aLineCommand.fNewlines.InsertLast(secondnewline);
          if firstThere then
          SecondSub := TProofline(fHead.Last).flineno + 3 {the lines}
   {                                                          are only in the command at this stage not}
   {                                                          the document}
          else
          SecondSub := TProofline(fHead.Last).flineno + 6 {the lines}
   {                                                          are only in the command at this stage not}
   {                                                          the document}

          end;

          aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

          aLineCommand.fNewlines.InsertLast(AddTarget(Targetroot, level, firstline.flineno, FirstSub, SecondSub));

          DoHintvE := aLineCommand;

          end;
          end;
         end;
       end;
    end;



*/


boolean canDoVE(FiveLines lines){
      /*
      {In the normal way they choose three lines, but one is a selectable line and the other}
      {the last line of a subproof, so total selected = 3 but Three Selected is false. However
      if the proof }
      {is from the same assumption Three selected will be true}


     */
      TProofline [] selected=  fProofListView.exactlyNLinesSelected(3);
      boolean strangecase;

      strangecase=(selected!=null);

      if (strangecase){
        lines.fFirstline=(TProofline)selected[0];
        lines.fSubtail1=(TProofline)selected[1];
        lines.fSubtail2=(TProofline)selected[2];
      }


      if (fProofListView.totalSelected()==3){       // we have to have this

        if (!strangecase){
           selected=  fProofListView.exactlyNLinesSelected(2);

           if (selected==null)
             return
                 false;
           else{

             lines.fFirstline = (TProofline) selected[0];
             lines.fSubtail2 = (TProofline) selected[1];

             TProofline [][] subProofs = fProofListView.nSubProofsSelected(1);

             if (subProofs==null)
                return
                    false;
             else{
                lines.fSubhead1=subProofs[0][0];
                lines.fSubtail1=subProofs[0][1];

             }

           }





           /*{This peculiar condition}
     {                                 covers the case of the same antecedent twice}
     {and the jump is to avoid a side effect in the selection routines}
  */


        }

        // at this point we have the v Line and both tails set, we lines.fSubhead1 set
        // in the normal case, but not in the strange one

        // The firstline has to be an 'Or'

        if (!fParser.isOr(lines.fFirstline.fFormula))
          return
              false;      // bale out


        lines.fSubhead2=fProofListView.fModel.findLastAssumption();

        if (lines.fSubhead2==null)
                  return
                      false;
        else{
           if (strangecase)
             lines.fSubhead1=lines.fSubhead2;
        }

       // now all is set


       if (((TFormula.equalFormulas(lines.fSubhead1.fFormula, lines.fFirstline.fFormula.fLLink) && TFormula.equalFormulas(lines.fSubhead2.fFormula, lines.fFirstline.fFormula.fRLink)) ||
           (TFormula.equalFormulas(lines.fSubhead2.fFormula, lines.fFirstline.fFormula.fLLink) && TFormula.equalFormulas(lines.fSubhead1.fFormula, lines.fFirstline.fFormula.fRLink)) &&
           TFormula.equalFormulas(lines.fSubtail1.fFormula, lines.fSubtail2.fFormula)))
          return
              true;
      }

return
   false;

  }



  void dovE(){
    TProofline firstline=null,subtail1=null, subtail2=null;


    if (fTemplate)
      doHintvE();
    else{
        FiveLines lines=new FiveLines();

      if (canDoVE(lines)){

        firstline=lines.fFirstline;
        subtail1=lines.fSubtail1;
        subtail2=lines.fSubtail2;

        TProofline newline = supplyProofline();
        int level = fModel.getHeadLastLine().fSubprooflevel;

        newline.fFormula=subtail1.fFormula.copyFormula();
        newline.fFirstjustno=firstline.fLineno;
        newline.fSecondjustno=subtail1.fLineno;
        newline.fThirdjustno=subtail2.fLineno;
        newline.fJustification= fOrEJustification;
        newline.fSubprooflevel= level-1;


        TUndoableProofEdit  newEdit = new TUndoableProofEdit();

        newEdit.fNewLines.add(endSubProof(level));

        newEdit.fNewLines.add(newline);

        newEdit.doEdit();

      }


    }

    }



/*

    function TProofWindow.DovE: TCommand;

    label
     99, 98;

    var
     orTarget: TFormula;
     level: integer;
     strangecase: boolean;
     firstline, subhead1, subhead2, subtail1, subtail2: TProofline;

     aLineCommand: TLineCommand;
     formula1: TFormula;

   begin

    DovE := gNoChanges;

    if fTemplate then {gTemplate}
     DovE := DoHintvE
    else
     begin
  {In the normal way they choose three lines, but one is a selectable line and the other}
  {the last line of a subproof, so total selected = 3 but Three Selected is false. However if they proof }
  {is of from the same assumption Three selected will be true}

      strangecase := fTextList.ThreeSelected(firstline, subtail1, subtail2);

      if (fTextList.TotalSelected = 3) then
       begin
        if strangecase then
         goto 99
        else if fTextList.TwoSelected(firstline, subtail2) then {This peculiar condition}
  {                                 covers the case of the same antecedent twice}
  {and the jump is to avoid a side effect in the selection routines}

  99:
         begin
         if strangecase then
         goto 98
         else if fTextList.OneSubProofSelected(subhead1, subtail1) then
  98:
         if FindLastAssumption(subhead2) then
         begin
         if strangecase then
         subhead1 := subhead2;

         if (Equalformulas(subhead1.fFormula, firstline.fFormula.fllink) and Equalformulas(subhead2.fFormula, firstline.fFormula.fRlink)) or (Equalformulas(subhead2.fFormula, firstline.fFormula.fllink) and Equalformulas(subhead1.fFormula, firstline.fFormula.fRlink)) then
         if Equalformulas(subtail1.fFormula, subtail2.fFormula) then
         begin
         New(aLineCommand);
         FailNil(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         level := TProofline(fHead.Last).fSubprooflevel;

         aLineCommand.fNewlines.InsertLast(EndSubProof(level));

         orTarget := subtail1.fFormula;

         aLineCommand.fNewlines.InsertLast(AddTarget(orTarget, level - 1, firstline.fLineno, subtail1.fLineno, subtail2.fLineno));

         DovE := aLineCommand;

         end;

         end;
         end;
       end;
     end;
   end;



*/

 /************************ Rule of OrI ****************************/


 public class OrIAction extends AbstractAction{
    JTextComponent fText;
    TProofline fSelection;
    boolean fLeft;

     public OrIAction(JTextComponent text, String label,TProofline selection, boolean left){
       putValue(NAME, label);

       fText=text;

       fSelection=selection;

       fLeft=left;
     }

      public void actionPerformed(ActionEvent ae){
     boolean useFilter =true;
     ArrayList dummy = new ArrayList();

     String aString= TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

     TFormula root = new TFormula();
     StringReader aReader= new StringReader(aString);
    
     boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);
     
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
     newline.fJustification= fOrIJustification;
     newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;

     TUndoableProofEdit  newEdit = new TUndoableProofEdit();
     newEdit.fNewLines.add(newline);
     newEdit.doEdit();

     removeInputPane();};


    }

  }





  public class HintVIAction extends AbstractAction{
     JTextComponent fText;
     TProofline fSelection;
     TUndoableProofEdit fEdit;

      public HintVIAction(JTextComponent text, String label,TProofline proofline,
                          TUndoableProofEdit edit){
        putValue(NAME, label);

        fText=text;
        fSelection=proofline;
        fEdit=edit;

      }

  public void actionPerformed(ActionEvent ae){

     fEdit.doEdit();

  removeInputPane();


     }

 }


 void doHintvI(boolean rightOnly){

   // menu enabled only if good

     TFormula newFormula=null;
     boolean proofover=false;
     TProofline newline=null;
     int leftdisjlineno=0,rightdisjlineno=0;
     TFormula conclusion =findNextConclusion();

     if ((conclusion!=null)||!fParser.isOr(conclusion)){







       /*we have four cases here a) trivial b) left, c) right, d) both*/

       TFormula leftdisj = conclusion.fLLink;
       TFormula rightdisj = conclusion.fRLink;

       TUndoableProofEdit leftEdit = new TUndoableProofEdit();

       TProofline headLastLine=fModel.getHeadLastLine();

       int level = headLastLine.fSubprooflevel;
       int lastlineno = headLastLine.fLineno;

       leftdisjlineno=addIfNotThere(leftdisj, level, leftEdit.fNewLines);

       newline = supplyProofline();

         newline.fFormula = conclusion.copyFormula();
         if (leftdisjlineno!=-1)
           newline.fFirstjustno = leftdisjlineno;
         else
           newline.fFirstjustno = lastlineno+2;

         newline.fJustification = fOrIJustification;
         newline.fSubprooflevel = level;

        leftEdit.fNewLines.add(newline);


       if (leftdisjlineno!=-1){   // trivial proof finished
        leftEdit.doEdit();

        return;                   // finished, so leave
      }

      TUndoableProofEdit rightEdit = new TUndoableProofEdit();

      rightdisjlineno=addIfNotThere(rightdisj, level, rightEdit.fNewLines);

      newline = supplyProofline();

         newline.fFormula = conclusion.copyFormula();
         if (rightdisjlineno!=-1)
           newline.fFirstjustno = rightdisjlineno;
         else
           newline.fFirstjustno = lastlineno+2;

         newline.fJustification = fOrIJustification;
         newline.fSubprooflevel = level;

        rightEdit.fNewLines.add(newline);


        /* if our rule is right only, we have to do this

        Right
        Right v ?   vI
        ? v Right comm   */

       if (rightOnly){
         TFormula temp, commuted;

        temp=newline.fFormula.getLLink();
        newline.fFormula.setLLink(newline.fFormula.getRLink());
        newline.fFormula.setRLink(temp);             // commuted previous line

        newline = supplyProofline();

        newline.fFormula = conclusion.copyFormula();
        if (rightdisjlineno!=-1){
          newline.fLineno = lastlineno + 2; // the previous newline is either newline 1 or newline 3
          newline.fFirstjustno = lastlineno + 1;
        }
        else{
          newline.fLineno = lastlineno + 4;
          newline.fFirstjustno = lastlineno + 3;
        }
        newline.fJustification = fCommJustification;
        newline.fSubprooflevel = level;

         rightEdit.fNewLines.add(newline);
       }



       if (rightdisjlineno!=-1){   // trivial proof finished
        rightEdit.doEdit();

        return;                   // finished, so leave
      }


   //not trivial

   TUndoableProofEdit bothEdit = new TUndoableProofEdit();
   leftdisjlineno=addIfNotThere(leftdisj, level, bothEdit.fNewLines);
   rightdisjlineno=addIfNotThere(rightdisj, level, bothEdit.fNewLines);


   // now we have 3 edit actions, we'll let the User decide


   TProofline firstLine=fProofListView.oneSelected();  //starting And


      {
           JButton leftButton;
           JButton rightButton;
           JButton bothButton;
           TProofInputPanel inputPane;

           JTextField text = new JTextField(
               "If you are unsure, choose 'Both' then later edit out the unused one (tricky!).");
           text.selectAll();


           {

             leftButton = new JButton(new HintVIAction(text, "Left", firstLine,leftEdit));
             bothButton = new JButton(new HintVIAction(text, "Both", firstLine,bothEdit));
             rightButton = new JButton(new HintVIAction(text, "Right", firstLine,rightEdit));

             JButton[] buttons = {
                 new JButton(new CancelAction()), leftButton,bothButton,
                 rightButton}; // put cancel on left
             inputPane = new TProofInputPanel("Doing"+fOrIJustification+" with Tactics." + "Choose disjunct to aim for.",
                                              text, buttons);
           }

           addInputPane(inputPane);

        //   inputPane.getRootPane().setDefaultButton(defaultButton);
           fInputPane.setVisible(true); // need this
           text.requestFocus(); // so selected text shows
         }





    /* old version

       TFormula leftdisj = conclusion.fLLink;
       TFormula rightdisj = conclusion.fRLink;

       TUndoableProofEdit newEdit = new TUndoableProofEdit();

       TProofline headLastLine=fModel.getHeadLastLine();

       int level = headLastLine.fSubprooflevel;
       int lastlineno = headLastLine.fLineno;

       leftdisjlineno=addIfNotThere(leftdisj, level, newEdit.fNewLines);

       if (leftdisjlineno==-1){   // not there
         leftdisjlineno = lastlineno+2;
         lastlineno += 2;
       }
       else
         proofover=true;

      if (!proofover){

        rightdisjlineno = addIfNotThere(rightdisj, level, newEdit.fNewLines);

        if (rightdisjlineno == -1) { // not there
          rightdisjlineno = lastlineno + 2;
          lastlineno += 2;
        }
        else{
          proofover=true;
          int lastIndex=newEdit.fNewLines.size()-1;
          newEdit.fNewLines.remove(lastIndex);      // these are the last two lines from left disjunction
          newEdit.fNewLines.remove(lastIndex-1);
        }

      }

      if (proofover){                             // only makes step if can be done
        if (leftdisjlineno == -1)
          leftdisjlineno = rightdisjlineno;

        newline = supplyProofline();

        newline.fFormula = conclusion.copyFormula();
        newline.fFirstjustno = leftdisjlineno;

        newline.fJustification = orIJustification;
        newline.fSubprooflevel = level;

        newEdit.fNewLines.add(newline);
      }

      newEdit.doEdit(); */


     }

   }



/*

  function TProofWindow.DoHintVI: TCommand;
   var
    leftdisj, rightdisj, conclusion: TFormula;
    leftdisjlineno, rightdisjlineno, disjlineno, Consequent, level: integer;
    firstnewline, secondnewline: TProofline;
    error, proofover: Boolean;
    aLineCommand: TLineCommand;

  begin
   DoHintVI := gNoChanges;

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
    BugAlert('With the tactic for �I, the conclusion must be a disjunction.')
   else
    begin

     leftdisj := conclusion.fLlink;
     rightdisj := conclusion.fRlink;

     New(aLineCommand);
     FailNIL(aLineCommand);
     aLineCommand.ILineCommand(cAddLine, SELF);

     level := TProofline(SELF.fHead.Last).fSubProofLevel;
     disjlineno := TProofline(fHead.Last).flineno + 1;


     AddIfNotThere(leftdisj, level, leftdisjlineno, firstnewline, secondnewline);


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

       AddIfNotThere(rightdisj, level, rightdisjlineno, firstnewline, secondnewline);


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

       aLineCommand.fNewlines.InsertLast(SupplyFormulaLine(conclusion, level, leftdisjlineno, 0, 0, ' �I'));
      end;

     DoHintVI := aLineCommand;

    end;
  end;



 */


 void dovI(boolean rightOnly){ // some versions allow introduction only on right to A v ?
   JButton onRightButton;
    JButton onLeftButton;
     TProofInputPanel inputPane;
     JButton[] buttons;



   if (fTemplate)
     doHintvI(rightOnly);  //new on right only
   else{
     TProofline firstLine=fProofListView.oneSelected();

     if (firstLine!=null){

       JTextField text = new JTextField("New formula?");

       text.selectAll();


        boolean left=true;



        if (rightOnly){
          onRightButton = new JButton(new OrIAction(text,"Go",firstLine, !left));

          JButton[] rightButtons = {
              new JButton(new CancelAction()),
              onRightButton}; // put cancel on left
          buttons=rightButtons;

        }
        else{
          onRightButton = new JButton(new OrIAction(text,"On Right",firstLine, !left));
          onLeftButton = new JButton(new OrIAction(text, "On Left", firstLine, left));

          JButton[] bothButtons = {
              new JButton(new CancelAction()), onLeftButton,
              onRightButton}; // put cancel on left
          buttons=bothButtons;

        }


          inputPane = new TProofInputPanel("Doing"+fOrIJustification,
             text, buttons,fInputPalette);



          addInputPane(inputPane);

         // inputPane.getRootPane().setDefaultButton(defaultButton);
          fInputPane.setVisible(true); // need this
          text.requestFocus();         // so selected text shows
        }

     }

   }




/*

  function TProofWindow.DovI: TCommand;

   var

    firstline, newline: TProofline;
    aLineCommand: TLineCommand;
    root, formulanode: TFormula;
    cancel: boolean;
    rad1, rad2, prompt: str255;

  begin

   DovI := gNoChanges;

   if fTemplate then {gTemplate}
    DovI := DoHintvI
   else
    begin

     if fTextList.OneSelected(firstline) then
      begin
       GetIndString(prompt, kStringRSRCID, 11); { Choose on buttons }
       GetIndString(rad1, kStringRSRCID, 12); { ''On left?';}
       GetIndString(rad2, kStringRSRCID, 13); { 'On right?'}

       GetTheRoot(rad1, rad2, prompt, root, cancel);
       if not cancel then
        begin

        SupplyFormula(formulanode);
        formulanode.fKind := binary; {formulanode is the new formula node}
        formulanode.fInfo := chOr;
        if fRadio then
        begin
        formulanode.fRlink := firstline.fFormula.CopyFormula;
        formulanode.fllink := root;
        end
        else
        begin
        formulanode.fllink := firstline.fFormula.CopyFormula;
        formulanode.fRlink := root;
        end;

        New(aLineCommand);
        FailNil(aLineCommand);
        aLineCommand.ILineCommand(cAddLine, SELF);

        SupplyProofline(newline);
        with newline do
        begin
        fFormula := formulanode;
        ffirstjustno := firstline.fLineno;
        fJustification := ' �I';
        fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
        end;

        aLineCommand.fNewlines.InsertLast(newline);
        newline := nil;
        DovI := aLineCommand;

        end;
      end;
    end;
  end;


 */





TProofline endSubProof (int lastlevel){
   TProofline newline = supplyProofline();

  newline.fBlankline=true;
  newline.fJustification= "";
  newline.fSubprooflevel= lastlevel-1;
  newline.fSelectable=false;

return
      newline;
}


/*

  function TProofWindow.EndSubProof (lastlevel: integer): TProofLine;
 {Adds a blank line and insets}

   var
    newline: TProofLine;

  begin
   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fSubprooflevel := lastlevel - 1; {checkthis}
     fBlankline := TRUE;
     fjustification := '';
     fSelectable := false;
    end;

   EndSubProof := newline;
   newline := nil;
  end;


   */

public void doRA(){
TProofline firstLine=fProofListView.oneSelected();

if (firstLine!=null){
      TProofline newline = supplyProofline();

      newline.fFormula=firstLine.fFormula.copyFormula();
      newline.fFirstjustno=firstLine.fLineno;
      newline.fJustification= repeatJustification;
      newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;


      TUndoableProofEdit  newEdit = new TUndoableProofEdit();

      newEdit.fNewLines.add(newline);

       newEdit.doEdit();


  //    fModel.insertAtPseudoTail(newline);  //no undo yet

    }

  }



/*

      function TProofWindow.DoRA: TCommand;

    var
     firstline: TProofline;
     newline: TProofline;
     aLineCommand: TLineCommand;

   begin
    DoRA := gNoChanges;

    if fTextList.OneSelected(firstline) then
     begin
      New(aLineCommand);
      FailNil(aLineCommand);
      aLineCommand.ILineCommand(cAddLine, SELF);

      SupplyProofline(newline);
      with newline do
       begin
        fFormula := firstline.fFormula.CopyFormula;
        ffirstjustno := firstline.fLineno;
        fJustification := ' R';
        fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
       end;

      aLineCommand.fNewlines.InsertLast(newline);
      newline := nil;
      DoRA := aLineCommand;

     end;
   end;


    }



   */


/*

      procedure TProofWindow.SetListSize;
  {this sets the gridlist the same size as the proof}

    var
     n: integer;

   begin
    fTextList.DelItemLast(fTextList.fNumOfRows); {safety}

    n := fHead.fSize + fTail.fSize;
    fTextList.InsItemLast(n); {set to 0 in resource}
   end;


   */

 void toNewPseudoTail(){   //TUESDAY. WORKING IN THIS  Hey the model should do this?
   int indexOfNextQuestionMark = fModel.nextQuestionMark();

   if (indexOfNextQuestionMark>-1){
     fModel.resetSplitBetweenLists(indexOfNextQuestionMark
                                   /*-1 alterned July 04*/);
   }
   else{   // there isn't another question mark and the proof is finished
     fTemplate=false;
     fProofType=pfFinished;

     fModel.resetSplitBetweenLists(fModel.getSize());  // put all lines in Head list

   }
 }

  /*

     procedure TProofWindow.ToNewPseudoTail;

     var
      tailFirstline: TObject;  (*check, used to be TProofline*)
      inHead: boolean;

    begin
     if FindNextQuestionMark(tailFirstline, inHead) then {might be in first list or second}
      begin
       if inHead then
        while (fTail.First <> tailFirstline) do
         begin
               {we have to transfer all from after the  question mark from end to front of second list}
          fTail.InsertFirst(fHead.At(fHead.fSize));
          fHead.delete(fHead.At(fHead.fSize));
         end

       else
        while (fTail.First <> tailFirstline) do
         begin
     {we have to transfer all from before the  question mark from front of second list}
   {  to the end of first list}
          fHead.InsertLast(fTail.First);
          fTail.delete(fTail.First);
         end;

                  {ResetSelectables; }

      end
     else
      begin
       fTemplate := false; {This must be set if there is no next and proof is finished}

       if fTail.fSize <> 0 then
        repeat {we have to transfer all tail to first list}

         fHead.InsertLast(fTail.At(1));
         fTail.delete(fTail.At(1));
        until fTail.fSize = 0;

       fProoftype := pfFinished;
      end;
   { DrawProof;}
   {}
   { SetButtons;}

    end;


   */

  /********************* undo support, the undoable edits are an inner class below*****************/

  public void addUndoableEditListener(UndoableEditListener listener){
    //  fListener =listener; June 06

      fListeners.add(listener);
    }


  public void tellListeners(UndoableEditEvent e){

 //   for (int i=0;i<fListeners.size();i++)
//      ((UndoableEditListener)fListeners.get(i)).undoableEditHappened(e);

    Iterator iter = fListeners.iterator();

        while (iter.hasNext()){
          ((UndoableEditListener)iter.next()).undoableEditHappened(e);

        }


  }

  /************ havnen't implement remove nor list of listeners ******/


 /******************  we'll make the edits an inner class ********************/
/*
may want to restrict to a single undo, otherwise gets very complicated if several proofs
  are done one after another. Also, we're pretty well copying the entire proof to undo--
 cannot keep doing thatUse die

*/

public class TUndoableProofEdit extends AbstractUndoableEdit{


  ArrayList fNewLines= new ArrayList();
  ArrayList fGarbageLines= new ArrayList();
  ArrayList fOldHead = null;
  ArrayList fOldTail =null;
  boolean fCutQuestionMark=false;
  int fOldProofType= pfFinished;

/*

     fNewLines: TList;
      fOldHead: TList;
      fOldTail: TList;
      fGarbageLInes: TList;
      fCutQuestionMark: boolean;
      fOldProofType: proofType;
      fProofWindow: TProofWindow;


   */


public  TUndoableProofEdit(){


    fOldHead = /*fModel*/TProofListModel.shallowCopy(fModel.getHead());  // note: this is only a shallow copy, I think that's enough as we don't change formulas
    fOldTail = /*fModel*/TProofListModel.shallowCopy(fModel.getTail());
    fCutQuestionMark=false;
    fOldProofType= fProofType;
}

  /*

    procedure TLineCommand.ILineCommand (itsCmdNumber: CmdNumber; itsProofWindow: TProofWindow);

  procedure CopyHead (item: TObject);

   var
    aProofline: TProofline;

  begin
   aProofline := TProofline(item).CloneIt;
   fOldHead.InsertLast(aProofline);
  end;

  procedure CopyTail (item: TObject);

   var
    aProofline, newline: TProofline;

  begin
   aProofline := TProofline(item).CloneIt;
   fOldTail.InsertLast(aProofline);
  end;

 begin
  ICommand(itsCmdNumber, itsProofWindow.fDocument, itsProofWindow, itsProofWindow.GetScroller(TRUE));
  fProofWindow := itsProofWindow;

  fProofWindow.fTextList.SetEmptySelection(TRUE); {check de-selects}

  fNewlines := nil;
  fNewlines := NewList;
  fCausesChange := TRUE;
  fCanUndo := TRUE;

  fCutQuestionMark := FALSE;

  fOldProofType := fProofWindow.fProofType;

  fOldHead := nil;
  fOldHead := NewList;
  fOldTail := nil;
  fOldTail := NewList;
  fGarbageLInes := nil;
  fGarbageLInes := NewList;

  fProofWindow.fHead.Each(CopyHead);
  fProofWindow.fTail.Each(CopyTail);

 end;


   */


public String getPresentationName(){
 return

   "proof change";
 }

 void cutQuestionMark(){
   //{This removes the question line, and the 'conclusion' line after the gPseudoTail}
  int headSize=fModel.getHeadSize();
  int tailSize=fModel.getTailSize();

   if (tailSize==0)
     toNewPseudoTail();// {this is to check that} a new subgoals has not been added to a finished proof}

   // what we are looking for is that the formula on the head last line, and the one on
   // the tail second line (the first is a question mark) are the same. And that they have
   // the same proof level. If so, we'll cut the first two from the tail, slightly collapsing the proof

   if ((headSize>0)&&(tailSize>1)){
     TProofline lastHeadLine = fModel.getHeadLastLine();
     TProofline secondTailLine = fModel.getTailLine(1);

     if (lastHeadLine.fSubprooflevel==secondTailLine.fSubprooflevel){
       TFormula firstFormula = lastHeadLine.fFormula;
       TFormula secondFormula = secondTailLine.fFormula;

       if (TFormula.equalFormulas(firstFormula,secondFormula)){
         fCutQuestionMark=true;  //now remove first two tail lines.

         fModel.remove(headSize+1);
         fModel.remove(headSize);

         fModel.incrementTailLineNos(-2,lastHeadLine.fLineno);

         toNewPseudoTail();

         cutQuestionMark();

       }
         //Bizarre case to go in here
     }
   }
}

 /*

   procedure TLineCommand.CutQuestionMark;

{This removes the question line, and the 'conclusion' line after the gPseudoTail}

  var
   templine: TProofline;
   i, headlastlineno: integer;
   firstformula, secondformula: TFormula;

  procedure DecrementLineNos (item: TObject);

   var
    aProofline: TProofline;

  begin
   aProofline := TProofline(item);
   aProofline.fLineno := aProofline.fLineno - 2;
   if aProofline.fFirstJustno > headlastlineno then
    aProofline.fFirstJustno := aProofline.fFirstJustno - 2;
   if aProofline.fSecondJustno > headlastlineno then
    aProofline.fSecondJustno := aProofline.fSecondJustno - 2;
   if aProofline.fThirdJustno > headlastlineno then
    aProofline.fThirdJustno := aProofline.fThirdJustno - 2;
  end;

  procedure IncrementLineNos (item: TObject);

   var
    aProofline: TProofline;

  begin
   aProofline := TProofline(item);
   aProofline.fLineno := aProofline.fLineno + 1;
   if aProofline.fFirstJustno > headlastlineno then
    aProofline.fFirstJustno := aProofline.fFirstJustno + 1;
   if aProofline.fSecondJustno > headlastlineno then
    aProofline.fSecondJustno := aProofline.fSecondJustno + 1;
   if aProofline.fThirdJustno > headlastlineno then
    aProofline.fThirdJustno := aProofline.fThirdJustno + 1;
  end;

  procedure BizarreCase; {Alterations to CutQuestionMark 6/28/91}
   var
    lastlevel: integer;
    newline: TProofline;
    assFormula: TFormula;

   function FindLastAssumption: boolean; {of proof as a whole}

    var
     dummy: TObject;

    function Premise (item: TObject): boolean;
     var
      aProofLIne: TProofLine;
    begin
     Premise := false;
     aProofLIne := TProofLine(item);
     if (aProofLIne.fjustification = 'Ass') and (aProofLIne.fSubprooflevel = lastlevel + 1) then
      begin
       Premise := TRUE;
       assFormula := aProofLIne.fFormula;
      end;
    end;

   begin
    dummy := nil;
    lastlevel := TProofLine(fProofWindow.fHead.Last).fSubprooflevel;
    dummy := fProofWindow.fHead.LastThat(Premise);
    FindLastAssumption := dummy <> nil;
   end;


{This is when a proof is started with TacticsOn, then they are switched off and the last}
{assmption is dropped-- thus damaging the proof. The last assumption has to be }
{added to the tail.}
  begin
   if FindLastAssumption then
    begin
     assFormula := assFormula.CopyFormula;
     SupplyProofline(newline); {newline points to new proofline}
     with newline do
      begin
       fSubprooflevel := TProofLine(fProofWindow.fHead.Last).fSubprooflevel + 1;
       fLineNo := TProofLine(fProofWindow.fHead.Last).fLineNo + 1;
       fFormula := assFormula;
       fjustification := 'Ass';
       fLastassumption := TRUE;
       fHeadlevel := TProofLine(fProofWindow.fHead.Last).fHeadLevel;
      end;
     fProofWindow.fHead.InsertLast(newline);
     fProofWindow.fTail.Each(IncrementLineNos);
     newline := nil;
    end;
  end;

 begin
          {IF fProofWindow.fTemplate THEN fProofWindow.ToNewPseudoTail; must work forward}

  if fProofWindow.fTail.fSize = 0 then
   fProofWindow.ToNewPseudoTail; {this is to check that}
{   a new subgoals has not been added to a finished proof}

  if (fProofWindow.fHead.fSize > 0) and (fProofWindow.fTail.fSize > 1) then
   begin
    headlastlineno := TProofline(fProofWindow.fHead.Last).fLineno;

    if (TProofline(fProofWindow.fHead.Last).fSubprooflevel = TProofline(fProofWindow.fTail.At(2)).fSubprooflevel) then
     begin
      firstformula := TProofline(fProofWindow.fHead.Last).fFormula;
      secondformula := TProofline(fProofWindow.fTail.At(2)).fFormula;

      if Equalformulas(firstformula, secondformula) then
       begin
       fCutQuestionMark := TRUE;
       templine := TProofline(fProofWindow.fTail.first);
       fGarbageLInes.InsertFirst(templine);
       fProofWindow.fTail.delete(fProofWindow.fTail.first);
       templine := TProofline(fProofWindow.fTail.first);
       fGarbageLInes.InsertFirst(templine);
       fProofWindow.fTail.delete(fProofWindow.fTail.first);

       fProofWindow.fTail.Each(DecrementLineNos);

                         {IF NOT fProofWindow.fTemplate THEN   check}

       fProofWindow.ToNewPseudoTail; {goes to}
{                              first for next}

       CutQuestionMark; {new}

                         {fProofWindow.fProofType := pfFinished; check this is done in ToNewPsed}
       end;
     end
    else if (TProofline(fProofWindow.fHead.Last).fSubprooflevel < TProofline(fProofWindow.fTail.At(2)).fSubprooflevel) then
     BizarreCase;

   end;
 end;


  */


/*
  procedure TCutLineCommand.DoIt;
   OVERRIDE;

   var
    newline: TProofline;
    textlist: TTextListView;
    badtop, i: integer;
    badCell: GridCell;

   procedure Remove (item: TObject);

   begin
    fProofWindow.fHead.delete(item);
    if not TProofline(item).fBlankline then
     begin
      fProofWindow.DecrementLineNos(fProofWindow.fHead, TProofline(item).fLineno, 1);
      fProofWindow.DecrementLineNos(fProofWindow.fTail, TProofline(item).fLineno, 1);
     end;
   end;

  begin
           {enters with old and new lists same}

           {first alter newlist}

   if fGarbageLInes.fSize <> 0 then
    fGarbageLInes.Each(Remove);

          { IF (fProofWindow.fProofType = premconc) OR (fProofWindow.fProofType = NOpremconc) THEN}
 {               CutQuestionMark;  check maybe this should be in}

           {the erase bad grid cells}

   badCell.h := 1;

   textlist := fProofWindow.fTextList;

   badtop := textlist.fNumofRows;

   EraseBadCells(fBadBottom, badtop);

           { ensure same no of cells as prooflines}

   badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

   if (textlist.fNumofRows - badtop) <> 0 then
    begin
     if (textlist.fNumofRows - badtop) < 0 then
      fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
     else
      fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);
    end;

           {redraw}

   for i := (fBadBottom) to badtop do {patch over erase chunks problem with zero heights}
    begin
     badCell.v := i;
     textlist.InvalidateCell(badCell);
    end;

   fProofWindow.CheckCellHeights;
   fProofWindow.ResetSelectables;
  end;


*/


public void doCutLinesEdit(){                            // note this is not an override, we want to callit directly


   int endIndex=fGarbageLines.size()-1;
   TProofline cutLine;

   fProofListView.clearSelection();

   while (endIndex>=0){
        cutLine = (TProofline)(fGarbageLines.get(endIndex));

        fModel.removeProofline(cutLine);

        endIndex-=1;                                // clearer to work from the end back
      }

   cutQuestionMark();                               // don't know whether we need this

   if (fLastEdit!=null)                             // kill the previous one so there's only one undo
        fLastEdit.die();

 //  fListener.undoableEditHappened(new UndoableEditEvent(TProofPanel.this,this));  // tell the listener.

 tellListeners(new UndoableEditEvent(TProofPanel.this,this));  // tell the listeners.

   fLastEdit=this;

   fModel.resetSelectables();   //NEW APRIL

}





public void doEdit(){                            // note this is not an override, we want to callit directly

int i=0;
int endIndex=fNewLines.size()-1;
TProofline aNewLine;

fProofListView.clearSelection();

while (i<=endIndex){
     aNewLine = (TProofline)(fNewLines.get(i));

     fModel.insertAtPseudoTail(aNewLine);

     i=i+1;
   }

cutQuestionMark();

if (fLastEdit!=null)                             // kill the previous one so there's only one undo
     fLastEdit.die();

//fListener.undoableEditHappened(new UndoableEditEvent(TProofPanel.this,this));  // tell the listener.

   tellListeners(new UndoableEditEvent(TProofPanel.this,this));  // tell the listener.

fLastEdit=this;

fModel.resetSelectables();   //NEW APRIL

}

 /*
  procedure TLineCommand.DoIt;
  OVERRIDE;

  var
   newline: TProofline;
   textlist: TTextListView;
   badbottom, badtop, i: integer;
   badCell: GridCell;

  procedure AddToTail (item: TObject);

   var
    newline: TProofline;

  begin
   newline := TProofline(item);
   fProofWindow.InsertAtPseudoTail(newline);
  end;

 begin
          {enters with old and new lists same}

          {first alter newlist}

  if fNewlines.fSize <> 0 then
   fNewlines.Each(AddToTail);

{ IF (fProofWindow.fProofType = premconc) OR (fProofWindow.fProofType = NOpremconc) THEN   check not needed}
  CutQuestionMark;

          {the erase bad grid cells}

  badCell.h := 1;
  badbottom := fOldHead.fSize + 1;
  if ((fProofWindow.fHead.fSize + 1) < badbottom) then
   badbottom := fProofWindow.fHead.fSize + 1;

  textlist := fProofWindow.fTextList;

  badtop := textlist.fNumofRows;

  EraseBadCells(badbottom, badtop);

          { ensure same no of cells as prooflines}

  badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

  if (textlist.fNumofRows - badtop) <> 0 then
   begin
    if (textlist.fNumofRows - badtop) < 0 then
     fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
    else
     fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);
   end;

          {redraw}

  for i := (badbottom) to badtop do {patch over erase chunks problem with zero heights}
   begin
    badCell.v := i;
    textlist.InvalidateCell(badCell);
   end;

  fProofWindow.CheckCellHeights;
  fProofWindow.ResetSelectables;
 end;



  */

public void redo() throws CannotRedoException{
    super.redo();
  //  System.out.println("UndoableProofEditRedo");

    fProofListView.clearSelection();   // they may have made one in the meantime

    ArrayList tempHead=fModel.getHead();
    ArrayList tempTail=fModel.getTail();



    fModel.replaceHeadAndTail(fOldHead,fOldTail);
    fOldHead=tempHead;
    fOldTail=tempTail;

    int tempProofType = fProofType;
    fProofType = fOldProofType;
    fOldProofType = tempProofType;

    fModel.resetSelectables();

   }

/*

       procedure TLineCommand.RedoIt;
     OVERRIDE;

     var
      textlist: TTextListView;
      badbottom, badtop, i: integer;
      badCell: GridCell;
      tempList: TLIst;
      tempProofType: proofType;

    begin

     fProofWindow.fTextList.SetEmptySelection(TRUE); {remove selections they've made in}
   {                                                           meantime}

             {change lists}

     tempList := fProofWindow.fHead;
     fProofWindow.fHead := fOldHead;
     fOldHead := tempList;
     tempList := nil;

     tempList := fProofWindow.fTail;
     fProofWindow.fTail := fOldTail;
     fOldTail := tempList;
     tempList := nil;

     tempProofType := fProofWindow.fProofType;
     fProofWindow.fProofType := fOldProofType;
     fOldProofType := tempProofType;

             {remove garbage}

     fGarbageLInes.DeleteAll; {check}

             {Erase bad}

     badCell.h := 1;
     badbottom := fOldHead.fSize + 1;
     if ((fProofWindow.fHead.fSize + 1) < badbottom) then
      badbottom := fProofWindow.fHead.fSize + 1;

     textlist := fProofWindow.fTextList;

     badtop := textlist.fNumofRows;

     EraseBadCells(badbottom, badtop);

             {to ensure same no of cells as prooflines}

     badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

     if (textlist.fNumofRows - badtop) <> 0 then
      begin
       if (textlist.fNumofRows - badtop) < 0 then
        fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
       else
        fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);
      end;

     for i := (badbottom) to badtop do
      begin
       badCell.v := i;
       textlist.InvalidateCell(badCell);
      end;

     fProofWindow.CheckCellHeights;
     fProofWindow.ResetSelectables;

    end;


    */


public void undo() throws CannotUndoException{
  super.undo();
  //System.out.println("UndoableProofEditUndo");

  fProofListView.clearSelection();   // they may have made one in the meantime

  ArrayList tempHead=fModel.getHead();
  ArrayList tempTail=fModel.getTail();
  fModel.replaceHeadAndTail(fOldHead,fOldTail);
  fOldHead=tempHead;
  fOldTail=tempTail;

  int tempProofType = fProofType;
  fProofType = fOldProofType;
  fOldProofType = tempProofType;

  fModel.resetSelectables();
 }

/*

  procedure TLineCommand.UndoIt;
    OVERRIDE;

    var
     textlist: TTextListView;
     badbottom, badtop, i: integer;
     badCell: GridCell;
     tempList: TLIst;
     tempProofType: proofType;

    procedure AddToGarbage (item: TObject);

    begin
     fGarbageLInes.InsertLast(item);
    end;

   begin
    fProofWindow.fTextList.SetEmptySelection(TRUE); {remove selections they've made in}
  {                                                           meantime}

            {change lists}

    tempList := fProofWindow.fHead;
    fProofWindow.fHead := fOldHead;
    fOldHead := tempList;
    tempList := nil;

    tempList := fProofWindow.fTail;
    fProofWindow.fTail := fOldTail;
    fOldTail := tempList;
    tempList := nil;

    tempProofType := fProofWindow.fProofType;
    fProofWindow.fProofType := fOldProofType;
    fOldProofType := tempProofType;

            {insert garbage}

    if fNewlines.fSize <> 0 then
     fNewlines.Each(AddToGarbage);

            {Erase bad}

    badCell.h := 1;
    badbottom := fOldHead.fSize + 1;
    if ((fProofWindow.fHead.fSize + 1) < badbottom) then
     badbottom := fProofWindow.fHead.fSize + 1;

    textlist := fProofWindow.fTextList;

    badtop := textlist.fNumofRows;

    EraseBadCells(badbottom, badtop);

    badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

    if (textlist.fNumofRows - badtop) <> 0 then {to ensure same no of cells as prooflines}
     begin
      if (textlist.fNumofRows - badtop) < 0 then
       fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
      else
       fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);

     end;

    for i := (badbottom) to badtop do
     begin
      badCell.v := i;
      textlist.InvalidateCell(badCell);
     end;

    fProofWindow.CheckCellHeights;
    fProofWindow.ResetSelectables;

   end;



*/



}


class TUndoAbleMarginChange extends TUndoableProofEdit{
  int fOldMargin;
  int fNewMargin;

  TUndoAbleMarginChange(int margin){
    fOldMargin=fModel.getRightMargin();
    fNewMargin=margin;

  }


  public void doEdit(){  // override

fModel.setRightMargin(fNewMargin);

if (fLastEdit!=null)                             // kill the previous one so there's only one undo
     fLastEdit.die();

//fListener.undoableEditHappened(new UndoableEditEvent(TProofPanel.this,this));  // tell the listener.

   tellListeners(new UndoableEditEvent(TProofPanel.this,this));  // tell the listener.

fLastEdit=this;

}

 public String getPresentationName(){
 return

   "margin change";
 }


 public void undo() throws CannotUndoException{
   super.undo();
   fModel.setRightMargin(fOldMargin);
 }
 public void redo() throws CannotRedoException{
   super.redo();
   fModel.setRightMargin(fNewMargin);
 }
}

  void rAMenuItem_actionPerformed(ActionEvent e) {

doRA();

  }

  void tIMenuItem_actionPerformed(ActionEvent e) {  //assumpption
doTI();
  }

////////////////////  Undo

   class UndoAction extends AbstractAction {
         public UndoAction() {
             super("Undo");
             setEnabled(false);
         }

         public void actionPerformed(ActionEvent e) {
             try {
                 fUndoManager.undo();
             } catch (CannotUndoException ex) {
                 System.out.println("Unable to undo: " + ex);
                 ex.printStackTrace();
             }
             updateUndoState();
             fRedoAction.updateRedoState();
         }

         protected void updateUndoState() {
             if (fUndoManager.canUndo()) {
                 setEnabled(true);
                 putValue(Action.NAME, fUndoManager.getUndoPresentationName());
             } else {
                 setEnabled(false);
                 putValue(Action.NAME, "Undo");
             }
         }
     }

     class RedoAction extends AbstractAction {
         public RedoAction() {
             super("Redo");
             setEnabled(false);
         }

         public void actionPerformed(ActionEvent e) {
             try {
                 fUndoManager.redo();
             } catch (CannotRedoException ex) {
                 System.out.println("Unable to redo: " + ex);
                 ex.printStackTrace();
             }
             updateRedoState();
             fUndoAction.updateUndoState();
         }

         protected void updateRedoState() {
             if (fUndoManager.canRedo()) {
                 setEnabled(true);
                 putValue(Action.NAME, fUndoManager.getRedoPresentationName());
             } else {
                 setEnabled(false);
                 putValue(Action.NAME, "Redo");
             }
         }
     }

void negEMenuItem_actionPerformed(ActionEvent e) {
   doNegE();
  }

  void negIMenuItem_actionPerformed(ActionEvent e) {
    doNegI();

  }

  void absurdMenuItem_actionPerformed(ActionEvent e) {
    doAbsI();
 }


  void andIMenuItem_actionPerformed(ActionEvent e) {
     doAndI();
  }

  void andEMenuItem_actionPerformed(ActionEvent e) {
    doAndE();
  }

  void theoremMenuItem_actionPerformed(ActionEvent e) {
    doTheorem();
  }

  void iIMenuItem_actionPerformed(ActionEvent e) {
    doII();
  }

  void iEMenuItem_actionPerformed(ActionEvent e) {
    doIE();
  }

  void inductionMenuItem_actionPerformed(ActionEvent e) {
  doInduction();
}


  void uniqueIMenuItem_actionPerformed(ActionEvent e) {
  doUniqueI();
}

void uniqueEMenuItem_actionPerformed(ActionEvent e) {
  doUniqueE();
}


  void rewriteMenuItem_actionPerformed(ActionEvent e) {

  }

  void orIMenuItem_actionPerformed(ActionEvent e) {
    boolean rightOnly=true;
    dovI(!rightOnly);

  }

  void orEMenuItem_actionPerformed(ActionEvent e) {
     dovE();
  }

  void implicIMenuItem_actionPerformed(ActionEvent e) {
    doImplicI();

  }

  void implicEMenuItem_actionPerformed(ActionEvent e) {
    doImplicE();
  }

  void equivIMenuItem_actionPerformed(ActionEvent e) {
    doEquivI();
  }

  void equivEMenuItem_actionPerformed(ActionEvent e) {
    doEquivE();
  }

  void uGMenuItem_actionPerformed(ActionEvent e) {
    doUG();

  }

  void uIMenuItem_actionPerformed(ActionEvent e) {
    doUI();
  }

  void eGMenuItem_actionPerformed(ActionEvent e) {
    doEG();
  }

  void eIMenuItem_actionPerformed(ActionEvent e) {
    doEI();

  }

  void cutLineMenuItem_actionPerformed(ActionEvent e) {
    doCutProofline();
  }

  void fEditMenu_mousePressed(MouseEvent e) {
    doSetUpEditMenu();
  }

  void fRulesMenu_mousePressed(MouseEvent e) {
    doSetUpRulesMenu();
  }

/*

public int getRightMargin(){
  return
      fRightMargin;
}


void setRightMargin(int margin){
  if ((margin!=fRightMargin)&&margin>100){
    fRightMargin = margin;
    fModel.setRightMargin(margin);
  }
}

*/






  void newGoalMenuItem_actionPerformed(ActionEvent e) {
   doNewGoal();
  }

  void tacticsMenuItem_actionPerformed(ActionEvent e) {
   if (tacticsMenuItem.isSelected())
     fTemplate=true;
    else
     fTemplate=false;
  }

  void writeProofMenuItem_actionPerformed(ActionEvent e) {
    writeProof();

  }


void writeProof(){
  String outputStr=fModel.proofToString();

  boolean append=true;

  (fDeriverDocument.getJournal()).writeHTMLToJournal(outputStr,append);

}

public void writeConfirmationMenuItem_actionPerformed(ActionEvent actionEvent) {
  writeConfirmation();

  }

void writeConfirmation(){
  String message= "Cannot confirm that there is a completed proof which does not use automatic derivation";

  String user=TPreferences.getUser();

  if( fModel.finishedAndNoAutomation()){

    //not yet using name of User



   // TDeriverApplication.determineUser();

  /*  if (TDeriverApplication.fCurrentUser.equals("")){

      String reply = (String) JOptionPane.showInputDialog(null,
          "Please enter your name", "User Name",
          JOptionPane.QUESTION_MESSAGE, null, null, "");

      if(reply!=null||!reply.equals(""))
         TDeriverApplication.fCurrentUser =reply;
    } */
 //   String encrypt = TUtilities.urlEncode( TUtilities.xOrEncrypt(TDeriverApplication.fCurrentUser+" : "+fProofStr));//TUtilities.xOrEncrypt(fProofStr);

 //COMBINING URLENCODE AND XOR NOT WORKING WELL

 String encrypt = TUtilities.urlEncode(fProofStr);

 fDeriverDocument.writeToJournal(strCR+"Confirmed for ["
                                    +user
                                    +"] : [" + encrypt + "]",
                                    TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
  }
  else
    fDeriverDocument.writeToJournal(message, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
}

/*another routine needs to check that there is a User name*/

public String produceConfirmationMessage(){    //COMBINE THIS AND PREVIOUS
  String message= "Cannot confirm yet.";

  if( fModel.finishedAndNoAutomation()){


  //  if (!TPreferences.getUser().equals(""))  // something else needs to check this
       message = TUtilities.urlEncode(fProofStr);


  }
  return
      message;

}

public boolean finishedAndNoAutomation(){  // means a proof has been done without theorem proving
  return
   fModel.finishedAndNoAutomation();
}


  /***************** Deriving stuff **********************
   *
   *
   */

  TTestNode assembleTestNode(){
   TTestNode aTestRoot = supplyTTestNode(fDeriverDocument.getParser(),null);  //does not initialize TreeModel

 /*
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



   */

  int size=fModel.getHeadSize();
  TProofline aProofline;
  int badChar;

  for (int i=0;((i<size)&&(aTestRoot!=null));i++){
    aProofline=fModel.getHeadLine(i);

    if ((!aProofline.fBlankline)&&aProofline.fSelectable){
      badChar=fParser.badCharacters(aProofline.fFormula);

      if (badChar==TParser.kNone)
         aTestRoot.fAntecedents.add(0,aProofline.fFormula.copyFormula()); // {best to have these in reverse order, because the User may have done some work}
      else{
         aTestRoot=null;
         writeBadCharError(badChar);
      }
     }
    }

  if (aTestRoot!=null){
      aProofline=fModel.getTailLine(1);
      badChar=fParser.badCharacters(aProofline.fFormula);

      if (badChar==TParser.kNone)
        aTestRoot.fSuccedent.add(aProofline.fFormula.copyFormula());
      else{
         aTestRoot=null;
         writeBadCharError(badChar);
      }

    }

 /*

     gTestroot.fSucceedent.InsertLast(TProofline(fTail.At(2)).fFormula.CopyFormula);

          if (not abandon) then
           abandon := fDeriverDocument.fJournalWindow.BadCharacters(TProofline(fTail.Last).fFormula, equals, compoundterms, higharity);


 */

   return
       aTestRoot;

 }

 void insertAll(ArrayList tempHead){

  if ((tempHead!=null)&&(tempHead.size() > 0)) {
    Iterator iter = tempHead.iterator();
    TProofline aProofLine;
    TProofline headLastLine = fModel.getHeadLastLine();
    int theSubProofLevel = headLastLine.fSubprooflevel -
        headLastLine.fHeadlevel;

    TUndoableProofEdit newEdit = new TUndoableProofEdit();

    while (iter.hasNext()) {
      aProofLine = (TProofline) iter.next();
      aProofLine.fSubprooflevel += theSubProofLevel;

      aProofLine.fDerived=true; // to stop the students cheating

      newEdit.fNewLines.add(aProofLine);

    }

    newEdit.doEdit();
  }

}


/*
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


 */


void insertFirstLine(ArrayList tempHead){
  if ((tempHead!=null)&&(tempHead.size() > 0)) {

  TProofline theProofLine=(TProofline)tempHead.get(0);
  TProofline headLastLine = fModel.getHeadLastLine();
  int theSubProofLevel = headLastLine.fSubprooflevel -
      headLastLine.fHeadlevel;

  TUndoableProofEdit newEdit = new TUndoableProofEdit();


    theProofLine.fSubprooflevel += theSubProofLevel;
    theProofLine.fDerived=true; // to stop the students cheating

    newEdit.fNewLines.add(theProofLine);



  newEdit.doEdit();
}

}



/*
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


*/


void removeDuplicates(ArrayList localHead){

  //not assumptions

  int i,j,limit,deletions;
  TProofline firstline,searchline;

  limit=localHead.size()-1;
  i=0;
  deletions=0;

  while ((i+deletions)<limit){
    firstline=(TProofline)localHead.get(i);

    if (!firstline.fBlankline){
      j=i+1;

      while ((j + deletions) < limit){//not last line
        searchline = (TProofline) (localHead.get(j));
        if (!searchline.fBlankline &&
            (!searchline.fJustification.equals(fAssJustification)) &&
            TFormula.equalFormulas(firstline.fFormula, searchline.fFormula)
            ) {  // it may be possible to delete searchline

              TProofListModel.resetSelectablesToHere(localHead,j);

              if (firstline.fSelectable){ //{searchline.fformula} redundant}

                TProofListModel.reNumSingleLine(localHead, j, firstline.fLineno);
                localHead.remove(j);

                j-=1;
                deletions+=1;
              }
           }
           j+=1;
        }
    }
    i+=1;
  }

}

/*
 procedure RemoveDuplicates;

    var
     i, j, limit, deletions: INTEGER;
     firstline, searchline: TProofline;

   begin
    limit := localHead.fSize;
    i := 1;
    deletions := 0;
    while (i + deletions) < limit do
     begin
      firstline := TProofline(localHead.At(i));
      if not firstline.fblankline then
       begin
        j := i + 1;
        while (j + deletions) < limit do {not last line}
        begin
        searchline := TProofline(localHead.At(j));
        if not searchline.fblankline then
        if not (searchline.fJustification = 'Ass') then
        if EqualFormulas(firstline.fFormula, searchline.fFormula) then
        begin
        ResetSelectablestoHere(localHead, j);

        if firstline.fSelectable then {searchline.fformula}
 {                                                     redundant}
        begin
                                                   {$IFC myDebugging}

        if FALSE then
        begin
        writeln('in improve');
        writeln('redundant i is', i, 'j is ', j);
        end;

                                                   {$ENDC}

        ReNumSingleLine(localHead, j, firstline.fLineno);

        localHead.Delete(localHead.At(j));
        searchline.DismantleProofline;
        j := j - 1;
        deletions := deletions + 1;
        end;
        end;
        j := j + 1;
        end;
       end;
      i := i + 1;
     end;
   end;


*/

void numberLines(ArrayList localHead){
  TProofListModel.renumberLines(localHead,1000);  //renumber to numbers that do not occur
  TProofListModel.renumberLines(localHead,1);
}

void prune(ArrayList localHead){
  int index=0;
  int deletions=0;
  int limit =localHead.size()-1;
  TProofline aProofline,nextline;

  while ((index+deletions)<limit){

    aProofline=(TProofline)localHead.get(index);
    nextline=(TProofline)localHead.get(index+1);

    if (!nextline.fSelectable) {  //redundant

      if (aProofline.fBlankline &&
          (index + 2 < localHead.size()))
        aProofline.fSubprooflevel=((TProofline)localHead.get(index+2)).fSubprooflevel;
        //to cope with cutting entire subproofs

      localHead.remove(index+1);
      deletions+=1;
    }
    else
      index+=1;

  }

}

/* There's a problem here, this can remove a premise without decrementing the fLastAss index ie the point where the premises end*/

void removeRedundant(ArrayList localHead, int lastAssIndex){
  if (localHead!=null&&localHead.size()>0){
    TProofline lastLine=((TProofline) localHead.get(localHead.size()-1));
    Iterator iter = localHead.iterator();

    while (iter.hasNext()) {
      ( (TProofline) iter.next()).fSelectable = false; //using selectable as a flag
    }

    ((TProofline) localHead.get(0)).fSelectable = true; //in case of blankstart
    lastLine.fSelectable = true;                        //setting first and last line true

    int i=0;

    while (i<=lastAssIndex)
       {((TProofline) localHead.get(i)).fSelectable = true;
       i++;}  //Oct 08 to fix decrement lastAss-- we won't prune premises


    traceBack(localHead,lastLine.fLineno);

    prune(localHead);

    iter = localHead.iterator();

    while (iter.hasNext()) {
      ( (TProofline) iter.next()).fSelectable = false; //using selectable as a flag
    }


  }


}


/*

 procedure RemoveRedundant;
      {any line not referred to as indirect justifications of the last line  is redundant}

    procedure TraceBack (itslineno: INTEGER);

     var
      tracesearch, nextline: TProofline;
      found: boolean;
      index: INTEGER;

    begin
     index := itslineno; {index cannot be less}
     tracesearch := TProofline(localHead.At(index));
     found := FALSE;

     while (index <= localHead.fSize) and not found do
      begin
       if tracesearch.fLineno = itslineno then
        found := TRUE
       else
        begin
        index := index + 1;
        tracesearch := TProofline(localHead.At(index));
        end;
      end;

     if found then
      begin
       tracesearch.fSelectable := TRUE; {using selectedflag as boolean}
       if (index < localHead.fSize) then
        begin
        nextline := TProofline(localHead.At(index + 1));
        if nextline.fblankline then
        nextline.fSelectable := TRUE; {blankline}
 {                                   has same lineno as last line of subprood}
        end;

       if tracesearch.fJustification = ' ~I' then
        SelectLastAssumption(index);

       if tracesearch.fJustification = ' �I' then
        SelectLastAssumption(index);

       if tracesearch.fJustification = ' EI' then
        SelectLastAssumption(index);

       if tracesearch.fJustification = ' �E' then
        begin
        SelectLastAssumption(index);
        SelectLastAssumption(lastTIindex - 1);
        end;

       if tracesearch.fJustification = ' �I' then
        begin
        SelectLastAssumption(index);
        SelectLastAssumption(lastTIindex - 1);
        end;

       if tracesearch.ffirstjustno <> 0 then {its ancestors}
        TraceBack(tracesearch.ffirstjustno);
       if tracesearch.fsecondjustno <> 0 then
        TraceBack(tracesearch.fsecondjustno);
       if tracesearch.fthirdjustno <> 0 then
        TraceBack(tracesearch.fthirdjustno);
      end;
    end;

 procedure Prune;

     var
      index, deletions, limit: INTEGER;
      aProofline, nextline: TProofline;

    begin
     index := 1;
     deletions := 0;
     limit := localHead.fSize;
     while (index + deletions < limit) do
      begin
       aProofline := TProofline(localHead.At(index));
       nextline := TProofline(localHead.At(index + 1));

       if not nextline.fSelectable then {nextline.fformula redundant}
        begin

        if aProofline.fblankline then
        if (index + 2) <= localHead.fSize then
        aProofline.fSubprooflevel := TProofline(localHead.At(index + 2)).fSubprooflevel;
                               {this is to cope with cutting out entire subproofs}

        localHead.Delete(localHead.At(index + 1));
        nextline.DismantleProofline;
        deletions := deletions + 1;

        end
       else
        index := index + 1;
      end;
    end;

    procedure SetSelectables (item: Tobject);

    begin
     TProofline(item).fSelectable := FALSE;
    end;

   begin {use selectable as a flag}

    localHead.Each(SetSelectables);
    TProofline(localHead.First).fSelectable := TRUE; {in case of blankstart}
    TProofline(localHead.Last).fSelectable := TRUE;

    TraceBack(TProofline(localHead.Last).fLineno);

    Prune;

    localHead.Each(SetSelectables);
   end;


*/

void improve(ArrayList localHead, int lastAssIndex){   //change Oct 08 to add lastAssIndex

  removeDuplicates(localHead);

 // TDeriverApplication.fDebug.displayProof(localHead);

  numberLines(localHead);

  removeRedundant(localHead,lastAssIndex);

  numberLines(localHead);

}

/*
 begin
 {$IFC bestpath}

   RemoveDuplicates;

   NumberLines(localHead);

   RemoveRedundant;

   NumberLines(localHead);
 {$ENDC}

  end;


*/


/*

 procedure TMyProofWindow.Improve (var localHead: TList); {check}

{ removes and renumbers duplicate lines, and removes lines not referred to}

  var
   lastTIindex: INTEGER;

{$IFC bestpath}

  procedure SelectLastAssumption (beforeHere: INTEGER);

   var
    dummy: Tobject;
    tempLast: TProofline;

   function Premise (item: Tobject): boolean;

    var
     aProofline: TProofline;

   begin
    Premise := FALSE;
    aProofline := TProofline(item);
    if (aProofline.fJustification = 'Ass') then
     if (aProofline.fSubprooflevel = tempLast.fSubprooflevel + 1) then
      if aProofline.fLineno <= tempLast.fLineno then
       Premise := TRUE;

   end;

  begin
   lastTIindex := 0;
   tempLast := TProofline(localHead.At(beforeHere));

   dummy := localHead.LastThat(Premise);

   if dummy <> nil then
    begin
     lastTIindex := localHead.GetSameItemNo(dummy);
     TProofline(dummy).fSelectable := TRUE;
    end;
  end;

  procedure RemoveRedundant;
     {any line not referred to as indirect justifications of the last line  is redundant}

   procedure TraceBack (itslineno: INTEGER);

    var
     tracesearch, nextline: TProofline;
     found: boolean;
     index: INTEGER;

   begin
    index := itslineno; {index cannot be less}
    tracesearch := TProofline(localHead.At(index));
    found := FALSE;

    while (index <= localHead.fSize) and not found do
     begin
      if tracesearch.fLineno = itslineno then
       found := TRUE
      else
       begin
       index := index + 1;
       tracesearch := TProofline(localHead.At(index));
       end;
     end;

    if found then
     begin
      tracesearch.fSelectable := TRUE; {using selectedflag as boolean}
      if (index < localHead.fSize) then
       begin
       nextline := TProofline(localHead.At(index + 1));
       if nextline.fblankline then
       nextline.fSelectable := TRUE; {blankline}
{                                   has same lineno as last line of subprood}
       end;

      if tracesearch.fJustification = ' ~I' then
       SelectLastAssumption(index);

      if tracesearch.fJustification = ' �I' then
       SelectLastAssumption(index);

      if tracesearch.fJustification = ' EI' then
       SelectLastAssumption(index);

      if tracesearch.fJustification = ' �E' then
       begin
       SelectLastAssumption(index);
       SelectLastAssumption(lastTIindex - 1);
       end;

      if tracesearch.fJustification = ' �I' then
       begin
       SelectLastAssumption(index);
       SelectLastAssumption(lastTIindex - 1);
       end;

      if tracesearch.ffirstjustno <> 0 then {its ancestors}
       TraceBack(tracesearch.ffirstjustno);
      if tracesearch.fsecondjustno <> 0 then
       TraceBack(tracesearch.fsecondjustno);
      if tracesearch.fthirdjustno <> 0 then
       TraceBack(tracesearch.fthirdjustno);
     end;
   end;


    }
     */




  TProofline nextBlankLineAfterSubproof(ArrayList localHead,TProofline theLine){
    if (localHead==null||localHead.size()<2)
      return
          null;

    int index=localHead.indexOf(theLine) +1;

    TProofline search;

    while (index<localHead.size()){

      search=(TProofline)localHead.get(index);

      if ((search.fSubprooflevel==(theLine.fSubprooflevel-1))&&  // a closing blank line has level one less than subproof
          search.fBlankline)
        return
           search;

     index+=1;
     }
  return
          null;
}


String traceAssOverride(){     //for subclasses
  return
      "";
}

    void traceBack(ArrayList localHead,int lineNo){
      int index=lineNo-1;    //cannot be less than lineNo-1
      TProofline traceSearch=(TProofline)localHead.get(index);
      boolean found=false;
      int lastTIIndex;

      // first we find the line with this line number

      while ((index<localHead.size())&&!found){

        if (traceSearch.fLineno==lineNo)
          found=true;
        else{
          index+=1;
          traceSearch=(TProofline)localHead.get(index);
        }
      }

      // if we find it, we mark it and things it depends on

        if (found){
          traceSearch.fSelectable=true; // using selectable as a boolena flag

          if (index<(localHead.size()-1)){   // not last line
            TProofline nextBlank =nextBlankLineAfterSubproof(localHead,traceSearch);

            if(nextBlank!=null)
              nextBlank.fSelectable=true;

          /*the line we are looking at might be in a subproof, in which case we must not cut the blankline
            that closes the subproof */

          }
                /*(TProofline)localHead.get(index+1);
            if (nextLine.fBlankline)
              nextLine.fSelectable=true;  //following blankline has same lineno as last line of subproof
          }    */                           // mark blankline also

          if (traceSearch.fJustification.equals(fNegIJustification)||
              traceSearch.fJustification.equals(fImplicIJustification)||
              traceSearch.fJustification.equals(fEIJustification)||
              traceSearch.fJustification.equals(traceAssOverride()))
                 lastTIIndex=selectLastAssumption(localHead,index);

          if (traceSearch.fJustification.equals(fOrEJustification)||
              traceSearch.fJustification.equals(fEquivIJustification)){
                 lastTIIndex = selectLastAssumption(localHead, index);
                 lastTIIndex = selectLastAssumption(localHead, lastTIIndex-1);
                 //must have a -1 here because need to get out of subproof to previous line
          }

          if (traceSearch.fFirstjustno!=0)  //its ancestors
            traceBack(localHead,traceSearch.fFirstjustno);
          if (traceSearch.fSecondjustno!=0)  //its ancestors
            traceBack(localHead,traceSearch.fSecondjustno);
          if (traceSearch.fThirdjustno!=0)  //its ancestors
            traceBack(localHead,traceSearch.fThirdjustno);

      }
    }

    /*
     procedure TraceBack (itslineno: INTEGER);

          var
           tracesearch, nextline: TProofline;
           found: boolean;
           index: INTEGER;

         begin
          index := itslineno; {index cannot be less}
          tracesearch := TProofline(localHead.At(index));
          found := FALSE;

          while (index <= localHead.fSize) and not found do
           begin
            if tracesearch.fLineno = itslineno then
             found := TRUE
            else
             begin
             index := index + 1;
             tracesearch := TProofline(localHead.At(index));
             end;
           end;

          if found then
           begin
            tracesearch.fSelectable := TRUE; {using selectedflag as boolean}
            if (index < localHead.fSize) then
             begin
             nextline := TProofline(localHead.At(index + 1));
             if nextline.fblankline then
             nextline.fSelectable := TRUE; {blankline}
      {                                   has same lineno as last line of subprood}
             end;

            if tracesearch.fJustification = ' ~I' then
             SelectLastAssumption(index);

            if tracesearch.fJustification = ' �I' then
             SelectLastAssumption(index);

            if tracesearch.fJustification = ' EI' then
             SelectLastAssumption(index);

            if tracesearch.fJustification = ' �E' then
             begin
             SelectLastAssumption(index);
             SelectLastAssumption(lastTIindex - 1);
             end;

            if tracesearch.fJustification = ' �I' then
             begin
             SelectLastAssumption(index);
             SelectLastAssumption(lastTIindex - 1);
             end;

            if tracesearch.ffirstjustno <> 0 then {its ancestors}
             TraceBack(tracesearch.ffirstjustno);
            if tracesearch.fsecondjustno <> 0 then
             TraceBack(tracesearch.fsecondjustno);
            if tracesearch.fthirdjustno <> 0 then
             TraceBack(tracesearch.fthirdjustno);
           end;
         end;


*/

    int selectLastAssumption(ArrayList localHead, int beforeHere){
      // needs to be an assumption at same subprooflevel

      int lastTIIndex=beforeHere-1;
      boolean found=false;
      TProofline searchLine;
      TProofline tempLast=(TProofline)localHead.get(beforeHere);
      int rightLevel=tempLast.fSubprooflevel+1;

      while ((lastTIIndex>-1)&&!found){
        searchLine=(TProofline)localHead.get(lastTIIndex);

        if ((searchLine.fSubprooflevel==rightLevel)&&
            (searchLine.fJustification.equals(TProofPanel.fAssJustification))&&
            searchLine.fLineno<=tempLast.fLineno){  //don't know why we need last condition
          found=true;
          searchLine.fSelectable=true;
        }
        else
          lastTIIndex-=1;
      }

      return
          lastTIIndex;  //-1 is not found
    }

    /*

     procedure SelectLastAssumption (beforeHere: INTEGER);

        var
         dummy: Tobject;
         tempLast: TProofline;

        function Premise (item: Tobject): boolean;

         var
          aProofline: TProofline;

        begin
         Premise := FALSE;
         aProofline := TProofline(item);
         if (aProofline.fJustification = 'Ass') then
          if (aProofline.fSubprooflevel = tempLast.fSubprooflevel + 1) then
           if aProofline.fLineno <= tempLast.fLineno then
            Premise := TRUE;

        end;

       begin
        lastTIindex := 0;
        tempLast := TProofline(localHead.At(beforeHere));

        dummy := localHead.LastThat(Premise);

        if dummy <> nil then
         begin
          lastTIindex := localHead.GetSameItemNo(dummy);
          TProofline(dummy).fSelectable := TRUE;
         end;
       end;


*/

    void setHeadLevels(ArrayList localHead){
  int headlevel;

  if (((TProofline)localHead.get(0)).fBlankline)  // no premises
    headlevel=-1;
  else
    headlevel=0;

   Iterator iter = localHead.iterator();

    while (iter.hasNext()) {
       ( (TProofline) iter.next()).fHeadlevel=headlevel;
        }


}

      /*

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


   */



//////////////  DERIVING ////////

void deriveItMenuItem_actionPerformed(ActionEvent e) {
//doDerive();  stub at present, pushed down to subclass TMyProofPanel
  }

void writeBadCharError(int badChar){
    switch (badChar) {

      case TParser.kEquality:
        bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, the semantics for = has not yet been implemented.");
        break;

      case TParser.kUnique:
        bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, the semantics for "
                                    + chUnique
                                    +" has not yet been implemented.");
        break;
     case TParser.kHighArity:
       bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, relations have to be of arity 2 or less.");
        break;

        case TParser.kCompoundTerms:
          bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, the semantics for compound terms has not yet been implemented.");
        break;

    }


}

  void nextLineMenuItem_actionPerformed(ActionEvent e) {

  }

  void fRulesMenu_menuSelected(MenuEvent e) {
//Toolkit.getDefaultToolkit().beep();

    doSetUpRulesMenu();
  }

  void fWizardMenu_menuSelected(MenuEvent e) {
    doSetUpWizardMenu();

  }

  void pruneMenuItem_actionPerformed(ActionEvent e) {
    doPrune();

  }

  void startAgainMenuItem_actionPerformed(ActionEvent e) {
  doStartAgain();

}


  public void marginMenuItem_actionPerformed(ActionEvent actionEvent) {
 //   MarginDialog dlg= new MarginDialog((TBrowser)fDeriverDocument.getJournal(),"Set Margin",true);

//    dlg.setVisible(true);

  }



  class MarginDialog
    extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel2 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel jLabel1 = new JLabel();
  JFormattedTextField jFormattedTextField1 = new JFormattedTextField(new Integer(fModel.getRightMargin()));

  public MarginDialog(Frame frame, String string, boolean _boolean) {
    super(frame, string, _boolean);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();

      setLocationRelativeTo(frame);
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public MarginDialog() {
    this(new Frame(), "Set Margin", true);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
   // jPanel1.setBorder(BorderFactory.createLineBorder(Color.black));
    jPanel1.setLayout(flowLayout1);
    jButton1.setText("Cancel");
    jButton2.setText("Set");
    jPanel2.setLayout(gridLayout1);
    jLabel1.setText("Proof Margin (100<)");
   // jFormattedTextField1.setText("jFormattedTextField1");
    getContentPane().add(panel1);
    panel1.add(jPanel1, java.awt.BorderLayout.SOUTH);
    jPanel1.add(jButton1);


    ActionListener listen = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
    dispose();
       }
    };
    jButton1.addActionListener(listen);

    ActionListener listen2 = new ActionListener() {
    public void actionPerformed(ActionEvent e) {

      int value= ((Number)jFormattedTextField1.getValue()).intValue();


      if (value!=fModel.getRightMargin()){
        //fModel.setRightMargin(value);
        TUndoAbleMarginChange change=new TUndoAbleMarginChange(value);
        change.doEdit();
      }

    dispose();
       }
    };
    jButton2.addActionListener(listen2);


    jPanel1.add(jButton2);
    panel1.add(jPanel2, java.awt.BorderLayout.CENTER);
    jPanel2.add(jLabel1);
    jPanel2.add(jFormattedTextField1);
  }
}











}  //end of TProofPanel


/*
TProofWindow = object(TWindow)
  fTextList: TProofListView;
  fTextlines: INTEGER;
  fHead, fTail: TList; {two lists, one for up to Pseudo Tail, one}
{                                                         for rest}

  fTemplate: boolean; {to indicate whether templates are operating}
  fProofType: proofType;
  fRadio: boolean;
  fFont: boolean; {true for large font, false for small}
  fMargin: INTEGER;
  fRewrite: TRewriteWindow;  (*new*)
  fParser: TParser;

*/

/*

 {	This file has been processed by The THINK Pascal Source Converter, v1.03.	}

 unit UProofViewIntf;

      {contains selecting and manipulating prrf}

 interface

  uses
   DerImpNotes,

   SysEqu, Traps, ULoMem, UMacAppUtilities, UPatch, UObject, UViewCoords, UMemory, UFailure, UMenuSetup, UList, PrintTraps, UAssociation, UMacApp, UTEView,
 { � MacApp }

 { � Building Blocks }
   UPrinting, UDialog, UGridView,
 { � Implementation Use }
   Picker, Packages, Events, Resources, ToolUtils, OSEvents, UStream, ULogicGlobals90, UFormulaIntf; (*PickerIntf,*)

  const
   cAddLine = 1500;
   cCutProofLine = 1501;  (*edit*)
   cPrune = 1502;  (*edit*)

   kCellHeight = 16; {take care here as TextGridView calculates 17/16 as a cell height}
 {                                  from font info in SetUpFont, if you change fonts, alter this}

   kProofWindowRSRCID = 1010;
   kProofTextListViewRSRCID = 1011;
   kStringRSRCID = 11449;

   kMaxNesting = 10; {no of subprooflevels}

  type
   proofType = (NOpremNOconc, NOpremconc, premNOconc, premconc, pfFinished);

   ProofLineData = record
     theLineno, theFirstjustno, theSecondjustno, theThirdjustno, theSubprooflevel, theHeadlevel: INTEGER;
     theFormula: str255;
 {$IFC NOT Defeasible}
     theJustification: string[10]; {check must match proofline}
 {$ELSEC}
     theJustification: string[12]; {check must match proofline}
 {$ENDC}
     theBlankline, theLastassumption: boolean;
    end;

                {***************TPROOFLINE***********************}

   TProofline = object(TOBJECT)
     fLineno, fFirstjustno, fSecondjustno, fThirdjustno, fSubprooflevel: INTEGER;
     fHeadlevel: INTEGER; {either0 or -1}
     fFormula: TFormula;
 {$IFC NOT Defeasible}
     fJustification: string[10]; {check must match prooflinedata}
 {$ELSEC}
     fJustification: string[12]; {check must match prooflinedata}
 {$ENDC}
     fBlankline, fLastassumption, fSelectable, fSubProofSelectable: boolean;

                     {all new lines usually come from the procedure SupplyProofline}

     procedure TProofLine.IProofline;

     function TProofline.CloneIt: TProofline;
     procedure TProofline.DismantleProofLine;

     procedure TProofline.ReadFrom (aRefNum: INTEGER);
     procedure TProofline.WriteTo (aRefNum: INTEGER);

     function TProofline.Draw (fontSize, rightmargin: INTEGER; var wrapno: INTEGER): str255;

                                  {Debugging}
                                  {$IFC qDebug}

     procedure TProofline.Fields (procedure DoToField (fieldName: str255; fieldAddr: Ptr; fieldType: INTEGER));
     OVERRIDE;

     procedure TProofline.GetInspectorName (var inspectorName: str255);
     OVERRIDE;
                                  {$ENDC}

    end;

                {***************TPROOFWINDOW***********************}

   TProofWindow = object(TWindow)
     fTextList: TProofListView;
     fTextlines: INTEGER;
     fHead, fTail: TList; {two lists, one for up to Pseudo Tail, one}
 {                                                         for rest}

     fTemplate: boolean; {to indicate whether templates are operating}
     fProofType: proofType;
     fRadio: boolean;
     fFont: boolean; {true for large font, false for small}
     fMargin: INTEGER;
     fRewrite: TRewriteWindow;  (*new*)
     fParser: TParser;

     procedure TProofWindow.IProofWindow;

     procedure TProofWindow.CloseByUser;
     OVERRIDE; {fixes one of their}
 {                                        bugs}
     procedure TProofWindow.DismantleProof;
     function TProofWindow.DoMenuCommand (aCmdNumber: CmdNumber): TCommand;
     OVERRIDE;
     procedure TProofWindow.DoSetupMenus;
     OVERRIDE;
     procedure TProofWindow.EstablishReferences (itsTextList: TProofListView; itsRewrite: TRewriteWindow);
     procedure TProofWindow.Free;
     OVERRIDE;
     procedure TProofWindow.InitProof;
     procedure TProofWindow.SetListSize;
     procedure TProofWindow.WriteEntireProof (aRefNum: INTEGER);

                                    {********message handling***************}

     procedure TProofWindow.BugAlert (message: str255);
     procedure TProofWindow.DoChoice (origView: TView; itsChoice: INTEGER);
     OVERRIDE;
     function TProofWindow.EntryReply: str255;
     function TProofWindow.GetTheChoice (radioText1, radioText2: str255; var prompt: str255): boolean;
     procedure TProofWindow.GetTheRoot (radioText1, radioText2: str255; var prompt: str255; var root: TFormula; var cancel: boolean);
     procedure TProofWindow.GetTheTerm (radioText1, radioText2: str255; var prompt: str255; var root: TFormula; var cancel: boolean);
     procedure TProofWindow.SetRadioText (message1, message2: str255; redraw: boolean);
     procedure TProofWindow.SetUpControls (enable: boolean);

                                    {****** proof manipulation************}

     function TProofWindow.Addassumption (formula: TFormula; dropLast: boolean): TProofline;
     function TProofWindow.AddEquiv (Anteroot, Conseroot: TFormula; prooflevel, Antecedent, Consequent: INTEGER): TProofline;
     function TProofWindow.AddExTarget (targetroot: TFormula; prooflevel, one, two: INTEGER): TProofline;

     procedure TProofWindow.AddIfNotThere (whichone: TFormula; prooflevel: INTEGER; var itslineno: INTEGER; var firstnewline, secondnewline: TProofline);

     function AddImplication (Anteroot, Conseroot: TFormula; prooflevel, Consequent: INTEGER): TProofline;

     function TProofWindow.AddNegAssumption (whichone: TFormula; prooflevel, PosHorn, NegHorn: INTEGER): TProofline;
     function TProofWindow.AddTarget (whichone: TFormula; prooflevel, orLineNo, second, third: INTEGER): TProofline;
     procedure TProofWindow.ChangeGoals (anItem: integer);
     procedure TProofWindow.CollapseTrivialCase;
     procedure TProofWindow.CreateBlankStart;
     procedure TProofWindow.DecrementLineNos (thisList: TList; fromthisline, amount: integer);
     function TProofWindow.EndSubProof (lastlevel: INTEGER): TProofline;
     function TProofWindow.FindFormula (given: TFormula): INTEGER;
     function TProofWindow.FindLastAssumption (var subhead: TProofline): boolean; {of proof as a whole}
     function TProofWindow.FindNextQuestionMark (var whichone: TOBJECT; var inHead: boolean): boolean;
     procedure TProofWindow.IncrementLineNos (thisList: TList; amount: INTEGER);
     function TProofWindow.IndexOfLineno (lineno: integer): INTEGER;

     procedure TProofWindow.InsertFirst (thisLine: TProofline);
 {check May 90 used to be a var param}

     procedure TProofWindow.InsertAtPseudoTail (thisLine: TProofline);
 {check May 90 used to be a var param}
     procedure TProofWindow.InsertAtTailFirst (thisLine: TProofline);
 {check May 90 used to be a var param}

     procedure TProofWindow.InsertAtTailLast (thisLine: TProofline);
 {check May 90 used to be a var param}


     function TProofWindow.LastAssLineno (beforeHere: INTEGER; var lastTIindex: integer): integer;


     function TProofWindow.LineCutable (thisline: TProofline; alreadyCut: TList): BOOLEAN;
     function TProofWindow.LinenoOfIndex (index: integer): INTEGER;
     procedure TProofWindow.ListAssumptions (localHead: TList; theLine: TProofline; var listHead: TList);

     function TProofWindow.SupplyFormulaLine (formula: TFormula; prooflevel, firstjustno, secondjustno, thirdjustno: integer; justification: str255): TProofLine;
 {copies formula}

     procedure TProofWindow.RemoveAtPseudoTail;
     procedure TProofWindow.ResetSelectables;
     procedure TProofWindow.ResetSelectablesToHere (var localHead: TList; index: INTEGER);
     procedure TProofWindow.ResetToPseudoTail;
     procedure TProofWindow.SetLastAssumption;
     procedure TProofWindow.ToNewPseudoTail;

                                    {****************cells*************}

     procedure TProofWindow.CheckCellHeights;

                                    {**************ProofCommands**********************}

     function TProofWindow.DoAbsI: TCommand;
     function TProofWindow.DoTI: TCommand;
     function TProofWindow.DoRA: TCommand;
     function TProofWindow.DoTheorem: TCommand;
     function TProofWindow.DoNegI: TCommand;
     function TProofWindow.DoNegE: TCommand;
     function TProofWindow.DoAndI: TCommand;
     function TProofWindow.DoAndE: TCommand;
     function TProofWindow.DovI: TCommand;
     function TProofWindow.DovE: TCommand;
     function TProofWindow.DoImplicI: TCommand;
     function TProofWindow.DoImplicE: TCommand;
     function TProofWindow.DoEquivI: TCommand;
     function TProofWindow.DoEquivE: TCommand;
     function TProofWindow.DoUG: TCommand;
     function TProofWindow.DoUI: TCommand;
     function TProofWindow.DoEG: TCommand;
     function TProofWindow.DoEI: TCommand;
     function TProofWindow.DoII: TCommand;
     function TProofWindow.DoIE: TCommand;

 {$IFC FirstOrderTheories}
     function TProofWindow.DoInduction: TCommand;
     function TProofWindow.DoUniClose: TCommand;
     function TProofWindow.DoUniOpen: TCommand;
     function TProofWindow.DoUniqueI: TCommand;
     function TProofWindow.DoHintUniqueI: TCommand;
     function TProofWindow.DoUniqueE: TCommand;

 {$ENDC}

     function TProofWindow.DoReplace: TCommand;

                                    {***********Tactics***********}

     function TProofWindow.ListPosForks: str255;
     function TProofWindow.FindTailFormula: TFormula;

     function TProofWindow.DoHintAbsI: TCommand;

     function TProofWindow.DoHintNegI: TCommand;
     function TProofWindow.DoHintImplicI: TCommand;
     function TProofWindow.DoHintVE: TCommand;
     function TProofWindow.DoHintEquivI: TCommand;
     function TProofWindow.DoHintEI: TCommand;

     function TProofWindow.DoHintVI: TCommand;
     function TProofWindow.DoHintAndI: TCommand;
     function TProofWindow.DoHintUG: TCommand;
     function TProofWindow.DoHintEG: TCommand;

 {$IFC FirstOrderTheories}
     function TProofWindow.DoHintUniClose: TCommand;

 {$ENDC}

                                   {*************FancyStuff***********}

     function TProofWindow.DoNewGoal: TCommand;
     function TProofWindow.PrepareCutLine: TCommand;
     function TProofWindow.PreparePrune: TCommand;

                                                         {*************Debugging************}
                                    {Debugging}
                                    {$IFC qDebug}

     procedure TProofWindow.Fields (procedure DoToField (fieldName: str255; fieldAddr: Ptr; fieldType: INTEGER));
     OVERRIDE;

     procedure TProofWindow.GetInspectorName (var inspectorName: str255);
     OVERRIDE;
                                    {$ENDC}

    end;


             {***************TRewriteWindow*****************}

   TRewriteWindow = object(TWindow)
     fProofWindow: TProofWindow;
     fLastRewrite: string[7]; (*changed from 6*)

     procedure TRewriteWindow.IRewriteWindow (itsProofWindow: TProofWindow);
     procedure TRewriteWindow.DoChoice (origView: TView; itsChoice: INTEGER);
     OVERRIDE;

     function TRewriteWindow.GetOldFormula (var preselection, selection, postselection: str255; var itsroot: TFormula): boolean;
     procedure TRewriteWindow.PutNewFormula (preselection, selection, postselection: str255; itsroot: TFormula);

     function TRewriteWindow.DoAndAssocLR (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoAndAssocRL (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoOrAssocLR (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoOrAssocRL (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoComm (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoOrComm (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoDistribAnd (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoDistribOr (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoDNLR (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoDNRL (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoDMAnd (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoDMOr (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoTransLR (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoTransRL (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoImplic (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoExp (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoEquiv1 (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoEquiv2 (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoQNExi (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoQNUni (var oldformula, newformula: TFormula): boolean;




     function TRewriteWindow.DoQNUni1 (var oldformula, newformula: TFormula): boolean;

     function TRewriteWindow.DoQNUni2 (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoQNUni3 (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoQNUni4 (var oldformula, newformula: TFormula): boolean;


     function TRewriteWindow.DoTautOr1 (var oldformula, newformula: TFormula): boolean;

     function TRewriteWindow.DoTautOr2 (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoTautAnd1 (var oldformula, newformula: TFormula): boolean;
     function TRewriteWindow.DoTautAnd2 (var oldformula, newformula: TFormula): boolean;



    end;


                {***************MyButton********************}

   TMyButton = object(TButton)

     function TMyButton.DoMouseCommand (var theMouse: POINT; var info: EventInfo; var hysteresis: POINT): TCommand;
     OVERRIDE;

                                 {override this to use command objects for proof manipulation}

    end;

                {***************ProofGridList********************}

   TProofListView = object(TTextListView)
     fProofWindow: TProofWindow;

     procedure TProofListView.IProofListView (itsWindow: TProofWindow);

                                      {****************TProofListOverride****************}

     function TProofListView.CanSelectItem (anItem: INTEGER): boolean;
     OVERRIDE;
     function TProofListView.DoMouseCommand (var theMouse: POINT; var info: EventInfo; var hysteresis: POINT): TCommand;
     OVERRIDE; {to load selection into entry text}
     procedure TProofListView.DrawCell (aCell: GridCell; aQDRect: Rect);
     OVERRIDE;

     function TProofListView.DrawCellToString (anItem: integer; var wrapno: integer): str255;

                                      {to cope with returns}
     procedure TProofListView.GetItemText (anItem: INTEGER; var aString: str255);
     OVERRIDE;


                                      {*******selection********}

     function TProofListView.TotalSelected: INTEGER;
     function TProofListView.OneSelected (var whichone: TProofline): boolean;
     function TProofListView.TwoSelected (var first, second: TProofline): boolean;
     function TProofListView.ThreeSelected (var first, second, third: TProofline): boolean;
     function TProofListView.OneSubProofSelected (var itsHead, itsTail: TProofline): boolean;

                                      {*************General***********}

     procedure TProofListView.SetFontSize;
     procedure TProofListView.ProperWriteFormula (root: TFormula; var outPutStr: str255);   {to be overidden by children}


                                      {Debugging}
                                      {$IFC qDebug}
     procedure TProofListView.Fields (procedure DoToField (fieldName: str255; fieldAddr: Ptr; fieldType: INTEGER));
     OVERRIDE;

     procedure TProofListView.GetInspectorName (var inspectorName: str255);
     OVERRIDE;
                                      {$ENDC}

    end;

                {*******************   TLINECOMMAND   ******************************}

   TLineCommand = object(TCommand)
     fNewLines: TList;
     fOldHead: TList;
     fOldTail: TList;
     fGarbageLInes: TList;
     fCutQuestionMark: boolean;
     fOldProofType: proofType;
     fProofWindow: TProofWindow;

     procedure TLineCommand.ILineCommand (itsCmdNumber: CmdNumber; itsProofWindow: TProofWindow);

     procedure TLineCommand.CutQuestionMark;
     procedure TLineCommand.EraseBadCells (badbottom, badtop: INTEGER);

                                    {*******command performing ************}

     procedure TLineCommand.DoIt;
     OVERRIDE;
     procedure TLineCommand.RedoIt;
     OVERRIDE;
     procedure TLineCommand.UndoIt;
     OVERRIDE;
     procedure TLineCommand.Commit;
     OVERRIDE;

                                    {Debugging}
                                    {$IFC qDebug}
     procedure TLineCommand.Fields (procedure DoToField (fieldName: str255; fieldAddr: Ptr; fieldType: INTEGER));
     OVERRIDE;

     procedure TLineCommand.GetInspectorName (var inspectorName: str255);
     OVERRIDE;
                                    {$ENDC}

    end;


   {*******************   TCUTLINECOMMAND   ******************************}

   TCutLineCommand = object(TLineCommand)
     fBadBottom: integer;

     procedure TCutLineCommand.ICutLineCommand (itsCmdNumber: CmdNumber; itsProofWindow: TProofWindow);

                                    {*******command performing ************}

     procedure TCutLineCommand.DoIt;
     OVERRIDE;
     procedure TCutLineCommand.RedoIt;
     OVERRIDE;
     procedure TCutLineCommand.UndoIt;
     OVERRIDE;
     procedure TCutLineCommand.Commit;
     OVERRIDE;


    end;

                {****************General*************}

  procedure InitUProofView;

  procedure SupplyProofline (var newline: TProofline);

 implementation

 {$I TProofline.p}
 {$I TProofWindow.p}
 {$I TProofRules.p}
 {$I TRewriteWindow.p}
 {$I TMyButton.p}
 {$I TProofListView.p}
 {$I TLineCommand.p}
 {$I TCutlineCommand.p}

 {********Global ***************}

 {$S AInit}

  procedure InitUProofView;
   var
    aProofLine: TProofLine;
    aProofWindow: TProofWindow;
    aMyButton: TMyButton;
    aProofListView: TProofListView;
    aLineCommand: TLineCommand;
    aCutLineCommand: TCutLineCommand;
    aRewrite: TRewriteWindow;

  begin
   if gDeadStripSuppression then
    if MEMBER(TObject(nil), TProofLine) then
     ;

   if gDeadStripSuppression then
    if MEMBER(TObject(nil), TProofWindow) then
     ;

   if gDeadStripSuppression then
    if MEMBER(TObject(nil), TMyButton) then
     ;

   if gDeadStripSuppression then
    if MEMBER(TObject(nil), TProofListView) then
     ;

   if gDeadStripSuppression then
    if MEMBER(TObject(nil), TLineCommand) then
     ;
   if gDeadStripSuppression then
    if MEMBER(TObject(nil), TCutLineCommand) then
     ;

   new(aProofLine);   {to stop the linker stripping}
   dispose(aProofLine);
   new(aProofWindow);   {to stop the linker stripping}
   dispose(aProofWindow);
   new(aMyButton);   {to stop the linker stripping}
   dispose(aMyButton);
   new(aProofListView);   {to stop the linker stripping}
   dispose(aProofListView);
   new(aLineCommand);   {to stop the linker stripping}
   dispose(aLineCommand);
   new(aCutLineCommand);   {to stop the linker stripping}
   dispose(aCutLineCommand);

   new(aRewrite);   {to stop the linker stripping}
   dispose(aRewrite);
  end;

 {************TProofline ***************}

 {$S ProofView}

  procedure SupplyProofline (var newline: TProofLine);

  begin
   New(newline);
   FailNil(newline);
   with newline do
    begin
     fLineno := 0;
     ffirstjustno := 0;
     fsecondjustno := 0;
     fthirdjustno := 0;
     fSubprooflevel := 0;
     fHeadlevel := 0;
     fFormula := nil;
     fjustification := strNull;
     fBlankline := false;
     fLastassumption := false;
     fSelectable := TRUE;
     fSubProofSelectable := false;

    end;

  end;


 end.




*/

class TProofPanel_rAMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_rAMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.rAMenuItem_actionPerformed(e);
  }
}

class TProofPanel_tIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_tIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.tIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_negEMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_negEMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.negEMenuItem_actionPerformed(e);
  }
}

class TProofPanel_negIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_negIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.negIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_andIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_andIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.andIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_andEMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_andEMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.andEMenuItem_actionPerformed(e);
  }
}

class TProofPanel_theoremMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_theoremMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.theoremMenuItem_actionPerformed(e);
  }
}

class TProofPanel_iEMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_iEMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.iEMenuItem_actionPerformed(e);
  }
}

class TProofPanel_inductionMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_inductionMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.inductionMenuItem_actionPerformed(e);
  }
}


class TProofPanel_rewriteMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_rewriteMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.rewriteMenuItem_actionPerformed(e);
  }
}

class TProofPanel_uniqueIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_uniqueIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.uniqueIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_uniqueEMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_uniqueEMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.uniqueEMenuItem_actionPerformed(e);
  }
}
class TProofPanel_iIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_iIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.iIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_orIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_orIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.orIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_orEMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_orEMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.orEMenuItem_actionPerformed(e);
  }
}

class TProofPanel_implicIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_implicIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.implicIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_implicEMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_implicEMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.implicEMenuItem_actionPerformed(e);
  }
}

class TProofPanel_equivIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_equivIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.equivIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_equivEMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_equivEMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.equivEMenuItem_actionPerformed(e);
  }
}

class TProofPanel_uGMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_uGMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.uGMenuItem_actionPerformed(e);
  }
}

class TProofPanel_uIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_uIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.uIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_eGMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_eGMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.eGMenuItem_actionPerformed(e);
  }
}

class TProofPanel_eIMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_eIMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.eIMenuItem_actionPerformed(e);
  }
}

class TProofPanel_cutLineMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_cutLineMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cutLineMenuItem_actionPerformed(e);
  }
}

class TProofPanel_fEditMenu_mouseAdapter extends java.awt.event.MouseAdapter {
  TProofPanel adaptee;

  TProofPanel_fEditMenu_mouseAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.fEditMenu_mousePressed(e);
  }
}

class TProofPanel_fRulesMenu_mouseAdapter extends java.awt.event.MouseAdapter {
  TProofPanel adaptee;

  TProofPanel_fRulesMenu_mouseAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.fRulesMenu_mousePressed(e);
  }
}

class TProofPanel_newGoalMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_newGoalMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newGoalMenuItem_actionPerformed(e);
  }
}

class TProofPanel_tacticsMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_tacticsMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.tacticsMenuItem_actionPerformed(e);
  }
}



class TProofPanel_absurdMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_absurdMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.absurdMenuItem_actionPerformed(e);
  }
}

class TProofPanel_writeProofMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_writeProofMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.writeProofMenuItem_actionPerformed(e);
  }
}

class TProofPanel_writeConfirmationMenuItem_actionAdapter
    implements ActionListener {
  private TProofPanel adaptee;
  TProofPanel_writeConfirmationMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.writeConfirmationMenuItem_actionPerformed(actionEvent);
  }
}

class TProofPanel_marginMenuItem_actionAdapter
    implements ActionListener {
  private TProofPanel adaptee;
  TProofPanel_marginMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.marginMenuItem_actionPerformed(actionEvent);
  }
}

class TProofPanel_deriveItMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_deriveItMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.deriveItMenuItem_actionPerformed(e);
  }
}

class TProofPanel_nextLineMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_nextLineMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.nextLineMenuItem_actionPerformed(e);
  }
}

class TProofPanel_fRulesMenu_menuAdapter implements javax.swing.event.MenuListener {
  TProofPanel adaptee;

  TProofPanel_fRulesMenu_menuAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.fRulesMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}

class TProofPanel_fWizardMenu_menuAdapter implements javax.swing.event.MenuListener {
  TProofPanel adaptee;

  TProofPanel_fWizardMenu_menuAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.fWizardMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}

class TProofPanel_pruneMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_pruneMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.pruneMenuItem_actionPerformed(e);
  }
}

class TProofPanel_startAgainMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TProofPanel adaptee;

  TProofPanel_startAgainMenuItem_actionAdapter(TProofPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.startAgainMenuItem_actionPerformed(e);
  }
}


