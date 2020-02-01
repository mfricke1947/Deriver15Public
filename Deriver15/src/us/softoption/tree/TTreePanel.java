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

package us.softoption.tree;


import static us.softoption.infrastructure.Symbols.chKappa;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chRho;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;
import static us.softoption.parser.TFormula.binary;
import static us.softoption.parser.TFormula.modalKappa;
import static us.softoption.parser.TFormula.modalRho;
import static us.softoption.parser.TFormula.quantifier;
import static us.softoption.parser.TFormula.unary;
import static us.softoption.parser.TFormula.variable;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.MenuEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.TPreferences;
import us.softoption.infrastructure.Symbols;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;
import us.softoption.proofs.TProofInputPanel;

/*

The data structure is a tree, in TTreeTableModle, which we are going to display in the standard 'inverted tree' form.

We need to map the tree to the table. The levels in the tree are just going to be the rows in the table.
But then, for any particular level or row, it is going to go <blank,data,blank, data etc blank> or
 <blank,blank,blank,data,blank, data etc blank,blank,blank> ie the data fills from the middle


*/

public class TTreePanel extends JPanel{

 // final static String fWorlds = "klmnopqrstuvwxyzabcdefghij";

  final static String setMember = "SM";

  // these following do not want to be static because there can be several panels open at once

  String andDJustification = " " + Symbols.chAnd + "D";
  String negDJustification = " ~~D";
  String implicDJustification = " " + Symbols.chImplic + "D";
  String equivDJustification = " " + Symbols.chEquiv + "D";
  String exiDJustification = " " + Symbols.chExiquant + "D";
  String uniqueDJustification = " " + Symbols.chUnique + "D";
  String negUniqueDJustification = " ~" + Symbols.chUnique + "D";
  String negAndDJustification = " ~" + Symbols.chAnd + "D";
  String negArrowDJustification = " ~" + Symbols.chImplic + "D";
  String negEquivDJustification = " ~" + Symbols.chEquiv + "D";
  String negExiDJustification = " ~" + Symbols.chExiquant + "D";
  String negUniDJustification = " ~" + Symbols.chUniquant + "D";
  String noreDJustification = " ~" + Symbols.chOr + "D";
  String orDJustification = " " + Symbols.chOr + "D";
  String UDJustification = " "+Symbols.chUniquant+ "D";
  String identityDJustification = " =D";
  String identityIJustification = " =I";

  String notPossibleJustification =" MN";
  String s5PossJustification =" "+chModalPossible+"S5";
  String s5NecessJustification =" "+chModalNecessary+"S5";
  String tNecessJustification =" "+chModalNecessary+"T";
  String rPossJustification =" "+chModalPossible+"R";
  String rNecessJustification =" "+chModalNecessary+"R";
  String rNecessNecessJustification =" "+chModalNecessary+chModalNecessary+"R";
  String symNecessNecessJustification =" "+chModalNecessary+chModalNecessary+"SymR";
  String accessRefJustification= " Refl";
  String accessSymJustification= " Sym";
  String accessTransJustification= " Trans";
  
  String kPNJustification =" KPN";
  String pRJustification =" PR";
  String kRJustification =" KR";
  String kKRJustification =" KKR";
  String kTRJustification =" KTR";
  String trKRJustification =" TrKR";
  
  String typeEJustification =" =Type";
  
 

  String chNeg=""+Symbols.chNeg;
  String chAnd=""+Symbols.chAnd;
  String chOr=""+Symbols.chOr;
  String chImplic=""+Symbols.chImplic;
  String chEquiv=""+Symbols.chEquiv;
  String chUniquant=""+Symbols.chUniquant;
  String chExiquant=""+Symbols.chExiquant;
  String chIdentity="=";

  JScrollPane jScrollPane1 = new JScrollPane(); // to contain the prooflist

  public TTreeTableModel fTreeTableModel = new TTreeTableModel(); // this is where the data is

  public TTreeTableView fTreeTableView = new TTreeTableView(fTreeTableModel);

  TParser fParser = new TParser(); // default, initialization links to,the document has one too, maybe only need one

  TDeriverDocument fDeriverDocument= null;  //this needs to be null

  public String fTreeStr = "";
    /* this is to hold the string version of the premises and conclusion, so that we
  can confirm which tree was proved. Filled in load()*/


TTreeDataNode fTreeDataRoot = null;  // the root of the tree, but as a TestNode, not as the Swing Node that holds it
TTreeModel fTreeModel= null;
String fStartStr="";         //for restart


  JPanel fInputPane = null; // this takes input in a modeless dialog fashion, visually above the proof

  JMenuBar jMenuBar1 = new JMenuBar();

  JMenu fActionsMenu = new JMenu();
  JMenuItem extendMenuItem = new JMenuItem();

  JMenuItem closeMenuItem = new JMenuItem();

  JMenuItem isClosedMenuItem = new JMenuItem();
  JMenuItem isCompleteMenuItem = new JMenuItem();

  JMenuItem identityMenuItem = new JMenuItem();
  JMenuItem startOverMenuItem = new JMenuItem();
  
  JMenu fAccessMenu = new JMenu();
  JMenuItem refMenuItem = new JMenuItem("Ref.");
  JMenuItem symMenuItem = new JMenuItem("Sym.");
  JMenuItem transMenuItem = new JMenuItem("Trans.");

  JMenu fRulesMenu = new JMenu();
  JMenuItem s4MenuItem = new JRadioButtonMenuItem("S4");
  JMenuItem s5MenuItem = new JRadioButtonMenuItem("S5");
  JMenuItem kMenuItem = new JRadioButtonMenuItem("K");
  JMenuItem tMenuItem = new JRadioButtonMenuItem("T");
  JMenuItem s5AltMenuItem = new JRadioButtonMenuItem("S5Alt");

  ButtonGroup ruleSetbuttonGroup = new ButtonGroup();

  boolean s4Switch=false;
  boolean s5Switch=true;
  boolean kSwitch=false;
  boolean tSwitch=false;
  boolean s5AltSwitch=false;
  
  boolean s4Rules=false;
  boolean kRules=false;
  boolean tRules=false;
  boolean s5AltRules=false;
  boolean s5Rules=true;

  boolean fUseIdentity=false;    // for getting more menu items independently of Preferences


  GridBagLayout gridBagLayout1 = new GridBagLayout();

  private ArrayList <UndoableEditListener> fListeners = new ArrayList();
  //private UndoableEditListener fListener;  //in this case the edit is not undoable
                                           //but we tell the document the tree is
                                          // dirty and can be saved


  /************* Constructors ******************/


 public TTreePanel() {
   enableEvents(AWTEvent.WINDOW_EVENT_MASK);

   try {
     jbInit();
   }
   catch (Exception e) {
     e.printStackTrace();
   }

 }

  public TTreePanel(TDeriverDocument itsDeriverDocument) {
    this();

    fDeriverDocument=itsDeriverDocument;

    fParser=fDeriverDocument.getParser();

    localizeJustStrings(); //new Dec09
    
    //Dec 09 This is a mistake. Translates values on formula Nodes
 /*   
   chNeg=fParser.translateConnective(chNeg);
   chAnd=fParser.translateConnective(chAnd);
   chOr=fParser.translateConnective(chOr);
   chImplic=fParser.translateConnective(chImplic);
   chEquiv=fParser.translateConnective(chEquiv);
   chUniquant= !fParser.translateConnective(chUniquant).equals("")?  //some systems do not use uniquant
               fParser.translateConnective(chUniquant):
               "U";
   chExiquant=fParser.translateConnective(chExiquant);
   chIdentity=fParser.translateConnective(chIdentity);


   andDJustification = " " + chAnd + "D";
   negDJustification = " "+chNeg+chNeg+"D";
   implicDJustification = " " + chImplic + "D";
   equivDJustification = " " + chEquiv + "D";
   exiDJustification = " " + chExiquant + "D";
   negAndDJustification = " "+chNeg + chAnd + "D";
   negArrowDJustification = " "+chNeg + chImplic + "D";
   negEquivDJustification = " "+chNeg + chEquiv + "D";
   negExiDJustification = " "+chNeg + chExiquant + "D";
   negUniDJustification = " "+chNeg + chUniquant + "D";
   noreDJustification = " "+chNeg + chOr + "D";
   orDJustification = " " + chOr + "D";
   UDJustification = " "+chUniquant+ "D";
   identityDJustification = " "+chIdentity+"D"; */

    initializeTreeModel();



  }

  //Component initialization
  private void jbInit() throws Exception {
    this.setSize(new Dimension(300, 400));
    this.setLayout(gridBagLayout1);
    
    int numMenus=0;

    //modal rules menu

   if (TPreferences.fModal)
   {

	   fAccessMenu.setText("Access");
	   fAccessMenu.add(refMenuItem);
	   fAccessMenu.add(symMenuItem);
	   fAccessMenu.add(transMenuItem);
	   
	     refMenuItem.addActionListener(new TTreePanel_refMenuItem_actionAdapter(this));
	     symMenuItem.addActionListener(new TTreePanel_symMenuItem_actionAdapter(this));
	     transMenuItem.addActionListener(new TTreePanel_transMenuItem_actionAdapter(this));
	   
	   
	   

     fRulesMenu.setDoubleBuffered(true);  //why do I want double buffered?
     fRulesMenu.setText("Rule Set");
  //   fRulesMenu.setMinimumSize(new Dimension(400, 20));
     
     
  //   fRulesMenu.addMenuListener(new TTreePanel_fRulesMenu_menuAdapter(this));

     
     fRulesMenu.add(s5MenuItem);
     fRulesMenu.add(kMenuItem);
     fRulesMenu.add(tMenuItem);
     fRulesMenu.add(s4MenuItem);
     fRulesMenu.add(s5AltMenuItem);


     s4MenuItem.addActionListener(new TTreePanel_ruleSetMenuItem_actionAdapter(this));
     s5MenuItem.addActionListener(new TTreePanel_ruleSetMenuItem_actionAdapter(this));
     kMenuItem.addActionListener(new TTreePanel_ruleSetMenuItem_actionAdapter(this));
     tMenuItem.addActionListener(new TTreePanel_ruleSetMenuItem_actionAdapter(this));
     s5AltMenuItem.addActionListener(new TTreePanel_ruleSetMenuItem_actionAdapter(this));

     {
         ruleSetbuttonGroup.add(s4MenuItem);
         ruleSetbuttonGroup.add(s5MenuItem);
      ruleSetbuttonGroup.add(kMenuItem);
      ruleSetbuttonGroup.add(tMenuItem);
      ruleSetbuttonGroup.add(s5AltMenuItem);


      ((JRadioButtonMenuItem)s5MenuItem).setSelected(true);
     }

     jMenuBar1.add(fAccessMenu);
     jMenuBar1.add(fRulesMenu);
     numMenus+=2;

   }


    fActionsMenu.setDoubleBuffered(true);
    fActionsMenu.setText("Actions");
    fActionsMenu.addMenuListener(new TTreePanel_fActionsMenu_menuAdapter(this));

    jMenuBar1.add(fActionsMenu);
    numMenus+=1;


    jMenuBar1.setMinimumSize(new Dimension(numMenus*70, 20)); // we don't want the menubar squeezed away
    
    //jMenuBar1.validate();

    this.add(jMenuBar1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                                               , GridBagConstraints.NORTHEAST,
                                               GridBagConstraints.NONE,
                                               /*HORIZONTAL,*/
                                               new Insets(0, 0, 0, 0), 0,
                                               0 /*301, 20*/));
/*input dialog comes here, index 1 */

    this.add(jScrollPane1, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(0, 0, 0, 0), 0,
                                                  0 /*0, 170*/));
    
    /*The above is set down to index 2 to allow room for the input panel */



    jScrollPane1.getViewport().add(fTreeTableView, null);





   // fEditMenu.addSeparator();

    /******** new *****************/

 createActionsMenu();
      }
  
protected void createActionsMenu(){
	   fActionsMenu.add(extendMenuItem);

	    extendMenuItem.setText("Extend");
	    extendMenuItem.addActionListener(new TTreePanel_extendMenuItem_actionAdapter(this));


	    if (true||
	            fUseIdentity){
	     // fActionsMenu.addSeparator();

	    }
	    
	    
	    
	    
	    fActionsMenu.add(closeMenuItem);

	    closeMenuItem.setText("Close");
	    closeMenuItem.addActionListener(new TTreePanel_closeMenuItem_actionAdapter(this));

	    fActionsMenu.addSeparator();

	    fActionsMenu.add(isClosedMenuItem);
	    isClosedMenuItem.setText("Closed?");
	    isClosedMenuItem.addActionListener(new
	                                     TTreePanel_isClosedMenuItem_actionAdapter(this));
	  fActionsMenu.add(isCompleteMenuItem);
	isCompleteMenuItem.setText("Complete Open Branch?");
	isCompleteMenuItem.addActionListener(new
	                                 TTreePanel_isCompleteMenuItem_actionAdapter(this));


	    fActionsMenu.addSeparator();


	    if (TPreferences.fIdentity||
	        fUseIdentity){
	 // fActionsMenu.addSeparator();

	  fActionsMenu.add(identityMenuItem);

	  identityMenuItem.setText("Identity Introduce");
	  identityMenuItem.addActionListener(new
	                                     TTreePanel_identityMenuItem_actionAdapter(this));
	}

	fActionsMenu.add(startOverMenuItem);

	startOverMenuItem.setText("Start Over");
	startOverMenuItem.addActionListener(new
	                                   TTreePanel_startOverMenuItem_actionAdapter(this));

	
}


void initializeTreeModel(){
  fTreeDataRoot = new TTreeDataNode(fParser,null);                          //does not initialize TreeModel
  fTreeModel= new TTreeModel(fTreeDataRoot.fTreeNode);
  fTreeDataRoot.fTreeModel=fTreeModel;                                      //Tree Model initialized now
}

void localizeJustStrings(){
	
	String localNeg=fParser.translateConnective(chNeg);
	String localAnd=fParser.translateConnective(chAnd);
	String localOr=fParser.translateConnective(chOr);
	String localImplic=fParser.translateConnective(chImplic);
	String localEquiv=fParser.translateConnective(chEquiv);
	String localUniquant= !fParser.translateConnective(chUniquant).equals("")?  //some systems do not use uniquant
	               fParser.translateConnective(chUniquant):
	               "U";
	String localExiquant=fParser.translateConnective(chExiquant);
	               String localIdentity=fParser.translateConnective(chIdentity);


	   andDJustification = " " + localAnd + "D";	   
	   negDJustification = " "+localNeg+localNeg+"D";	   
	   implicDJustification = " " + localImplic + "D";	   
	   equivDJustification = " " + localEquiv + "D";	   
	   exiDJustification = " " + localExiquant + "D";	   
	   negAndDJustification = " "+localNeg + localAnd + "D";	   
	   negArrowDJustification = " "+localNeg + localImplic + "D";	   
	   negEquivDJustification = " "+localNeg + localEquiv + "D";	   
	   negExiDJustification = " "+localNeg + localExiquant + "D";	   
	   negUniDJustification = " "+localNeg + localUniquant + "D";   
	   noreDJustification = " "+localNeg + localOr + "D";	   
	   orDJustification = " " + localOr + "D";
	   UDJustification = " "+localUniquant+ "D";
	   identityDJustification = " "+localIdentity+"D";	
	
	
	
	
}

/********************* undo support, the undoable edits are an inner class below*****************/

public void addUndoableEditListener(UndoableEditListener listener){
    fListeners.add(listener);
  }


public void tellListeners(UndoableEditEvent e){
  Iterator iter = fListeners.iterator();

      while (iter.hasNext()){
        ((UndoableEditListener)iter.next()).undoableEditHappened(e);

      }


}

/************ havnen't implement remove nor list of listeners ******/


/*************************** Saving and Opening Files, Beans *************************/

public TTreeTableModel getModel(){
  return
      fTreeTableModel;
}

public void  setModel(TTreeTableModel aModel){

      fTreeTableModel=aModel;
}



/*************************** Opening Files *************************/

public void reconstructTree(TTreeTableModel aModel/*, int [] colWidths*/){  //from File

  // just cannot seem to get this to open to the right widths, just opens to default 75


  fTreeTableModel=aModel;

  jScrollPane1.getViewport().remove(fTreeTableView);

 // JTableHeader oldHeader=fTreeTableView.getTableHeader();

  fTreeTableView= new TTreeTableView(fTreeTableModel);   // should have this in an init routine

  jScrollPane1.getViewport().add(fTreeTableView, null);   // remove the old one?

//  fTreeTableView.setTableHeader(oldHeader);

//  fTreeTableView.setColPreferredWidths(colWidths);

//  fTreeTableView.setSize(new Dimension(500, 400));

  DefaultMutableTreeNode swingRoot = fTreeTableModel.getHostRoot();

  Object data = swingRoot.getUserObject();

  if (data instanceof TTreeDataNode){  // should be

    fTreeDataRoot= (TTreeDataNode)data;

 //   fTreeTableView.resetWidths2(fTreeDataRoot);

  //  fTreeTableView.setActualColWidthsToPreferred();

  }


/*{  int [] colWidths = fTreeTableView.getColWidths();

  int total=0;

  for (int i=0;i<colWidths.length;i++){
    total+=colWidths[i];
  }

  fTreeTableView.setSize(total,fTreeTableView.getHeight());
}  */


//  fTreeTableView.doLayout();

//

  //Feb10

//  fTreeTableModel.updateCache();    treeChange updates cache                // this updates the table data based on the tree

//fTreeTableModel.treeChanged(TTreeTableModel.COLCHANGE,null);              //need a listener for this

//fTreeTableView.resetWidths2(fTreeDataRoot);

// fTreeTableView.doLayout();

fTreeTableModel.treeChanged(TTreeTableModel.COLCHANGE,null);  // not sure what I need to get an update


//setVisible(false);
//setVisible(true);
  // end Feb 10


//deSelectAll();


  //fTreeTableView.repaint();

}

/*************************** Input pane methods *******************************/

/* This simulates a modeless dialog appearing in the Panel at the top *********/

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


  void removeBugAlert(){
  removeInputPane();
}

void enableMenus(){
  fActionsMenu.setEnabled(true);



}

      void disableMenus(){
        fActionsMenu.setEnabled(false);



}

void removeInputPane(){
    if (fInputPane!=null){
      fInputPane.setVisible(false);
       this.remove(fInputPane);

        fInputPane=null;


  enableMenus();
    }
  }





public boolean usingInputPane(){
  return
      fInputPane!=null;
}

public class CancelAction extends AbstractAction{

     public CancelAction(){
       putValue(NAME, "Cancel");
     }

      public void actionPerformed(ActionEvent ae){


        removeInputPane();
      }

      }


  /*************************** End of Input pane methods *******************************/



public boolean getUseIdentity(){
    return
        fUseIdentity;
  }

public void setUseIdentity(boolean use){
          fUseIdentity=use;
  }


public void resetToEmpty(){
	removeInputPane();
    initTree();
    fStartStr="";
	
}

public void startTree(String inputStr){


   // dismantleProof(); //{previous one}

    removeInputPane();

    initTree();

   if (load(inputStr))
   //   startUp()
   ;

  fStartStr=inputStr;  //for restart

  }


 void initTree(){
   jScrollPane1.getViewport().remove(fTreeTableView);

   initializeTreeModel();

   fTreeTableModel = new TTreeTableModel(/*fTreeModel,*/ fTreeDataRoot); // this is where the data is

   fTreeTableView = new TTreeTableView(fTreeTableModel);

   jScrollPane1.getViewport().add(fTreeTableView, null);
 }



 /*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
       compound terms eg Pf(a,b),Hc.

    Also in some systems a relation Lxy is written L(x,y) ie also with commas


    We want to separate the premises but not the terms. So we will change the
    premise comma separators to another character. For the moment '!'*/


 private static char chSpecialSeparator='#';  //was ! ie unique!

 private String changeListSeparator(String input){

   int nested=0;
   char currCh;

   StringBuffer output= new StringBuffer(input);
   for (int i=0;i<input.length();i++){
     currCh=output.charAt(i);

     if (currCh==chSmallLeftBracket)
       nested++;
     if (currCh==chSmallRightBracket)
       nested--;

     if ((nested<1)&&(currCh==Symbols.chComma))    //commas separating the list of premises are not nested
       output.setCharAt(i,chSpecialSeparator);
   }

   return
       output.toString();
}






 boolean load(String inputStr){                       //similar routine in TMyProofPanel

   TTreeDataNode currentNode=null;
   int lineNo=1;

  fParser.initializeErrorString();



   ArrayList dummy=new ArrayList();
   boolean wellformed = true;

   fTreeStr="";  //re-initialize; the old proof may still be there and if this turns out to be illformed will stay there


        if ((inputStr==null)||inputStr==strNull){
          return
              false;                // cannot be empty for a tree
        }

 String[]premisesAndConclusion = inputStr.split(String.valueOf(Symbols.chTherefore),2);  /* they may input two
         therefore symbols, in which case we'll split at the first and let the parser report the second */

    if (premisesAndConclusion[0]!=strNull){  // there are premises


      premisesAndConclusion[0]=changeListSeparator(premisesAndConclusion[0]);  //kludge


      StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chSpecialSeparator));

   while ((premises.hasMoreTokens())&&wellformed){
      inputStr=premises.nextToken();

      if (inputStr!=strNull){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
             TFormula root = new TFormula();
             StringReader aReader = new StringReader(inputStr);


             wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

             if (!wellformed){

               fDeriverDocument.writeToJournal(fParser.fCurrCh + TConstants.fErrors12 + 
            		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""), TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
             }
             else
                 {//addPremise(root);

                  if (currentNode==null)          //first one is a special case
                  {fTreeDataRoot.addToAntecedents(root);
                   fTreeDataRoot.fJustification=setMember;
                   if (fParser.containsModalOperator(root))
                     fTreeDataRoot.fWorld=fParser.startWorld();
                   fTreeDataRoot.fLineno=lineNo;

                   lineNo++;

                   fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE,fTreeDataRoot);              //need a listener for this, need it no Insert
                    currentNode=fTreeDataRoot;
                  }
                  else{
                    TTreeDataNode newNode =(TTreeDataNode)(fTreeDataRoot.supplyTTestNode(fParser,fTreeModel));   //using one node for each formula
                    newNode.addToAntecedents(root);
                    newNode.fJustification=setMember;
                    if (fParser.containsModalOperator(root))
                     newNode.fWorld=fParser.startWorld();
                    newNode.fLineno=lineNo;
                    straightInsert(currentNode,newNode,null);  // change
                 //   fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE,newNode);              //need a listener for this
                    currentNode=newNode;

                    lineNo++;
                  }



                 if (fTreeStr.length()==0)
                   fTreeStr=inputStr;
                 else
                   fTreeStr+=Symbols.chComma+inputStr;
                 }
             }
      }          // done with premises

      if (premisesAndConclusion.length>1){  // if there is no therefore the original 'split' won't split the input
        inputStr = premisesAndConclusion[1];

        if (inputStr!=strNull){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
             TFormula root = new TFormula();
             StringReader aReader = new StringReader(inputStr);

             wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

             if (!wellformed){
             //  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");
               fDeriverDocument.writeToJournal(fParser.fCurrCh + TConstants.fErrors12 + 
            		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""), TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
             }else
                 {//addConclusion(root);

                   TFormula newFormula = new TFormula();

                   newFormula.fKind = TFormula.unary;
                   newFormula.fInfo = String.valueOf(Symbols.chNeg);
                   newFormula.fRLink = root;

                   root=newFormula;
                   // add its negation
                   if (currentNode==null)          //first one is a special case
                     {fTreeDataRoot.addToAntecedents(root);
                       fTreeDataRoot.fJustification=setMember;
                       if (fParser.containsModalOperator(root))
                          fTreeDataRoot.fWorld=fParser.startWorld();
                       fTreeDataRoot.fLineno=lineNo;

                       lineNo++;   // really no need, there are no more

                       fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE,fTreeDataRoot);              //need a listener for this, need it no Insert
                       currentNode=fTreeDataRoot;
                     }
                     else{

                       TTreeDataNode newNode = (TTreeDataNode) (fTreeDataRoot.supplyTTestNode(fParser,
                           fTreeModel)); //using one node for each formula
                       newNode.addToAntecedents(root);
                       newNode.fJustification = setMember;
                       if (fParser.containsModalOperator(root))
                          newNode.fWorld=fParser.startWorld();
                       newNode.fLineno = lineNo;
                       straightInsert(currentNode,newNode, null);
                       fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE, newNode); //need a listener for this
                       currentNode = newNode;
                     }

                 fTreeStr+=Symbols.chTherefore+inputStr;
                 }
             }

      }

    };

        return
            wellformed;

     }


 public boolean isTreeClosed(){
   return
       (fTreeDataRoot!=null?fTreeDataRoot.isTreeClosed():true);
 }


public boolean isABranchOpenAndComplete(){
  return
    (fTreeDataRoot!=null?fTreeDataRoot.isABranchOpenAndComplete():true);
}

  public boolean isABranchOpenAndClosable(){
    return
      (fTreeDataRoot!=null?fTreeDataRoot.isABranchOpenAndClosable():false);
  }


public void selectOpenBranch(){

  deSelectAll();

  TTreeDataNode openLeaf= fTreeDataRoot.returnOpenLeaf();

  fTreeTableView.selectCellContaining(openLeaf.fTreeNode);

}



void closeFromNegationOfIdentity(){
  TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

     deSelectAll();

  if ((selectedNodes!=null)&&(selectedNodes.length==1)){
    TTreeDataNode selected= selectedNodes[0];
    TFormula firstFormula=null;

    if (selected.fAntecedents!=null&&
       selected.fAntecedents.size()==1){
       firstFormula=(TFormula)(selected.fAntecedents.get(0));

      if (!firstFormula.isNegIdentity(firstFormula))
        bugAlert("Trying to Close Branch. Warning.", "Select two formulas that contradict, or a single negation of identity.");
      else{

        TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);

        newDataNode.fClosed = true;
// newDataNode.fDead=true;  // don't make it dead else renderer will tick it

        selected.fTreeNode.removeAllChildren(); //immediate close
        selected.straightInsert(newDataNode, null);

        fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE, newDataNode); //need a listener for this


      }


}
else
 return
   ;

  }
    ;

}



void closeMenuItem_actionPerformed(ActionEvent e) {
  TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();


if ((selectedNodes!=null)&&(selectedNodes.length==1))
     closeFromNegationOfIdentity();
else{
     deSelectAll();

if ((selectedNodes==null)||(selectedNodes.length!=2))
   bugAlert("Trying to Close Branch. Warning.", "You need to select two formulas.");
 else{

  TTreeDataNode selected= selectedNodes[0];
  TTreeDataNode secondSelected= selectedNodes[1];


  if (!selected.fWorld.equals(TTreeDataNode.nullWorld)&&      //if the world is null we understand the formula as being universal ie without modal operators
	  !secondSelected.fWorld.equals(TTreeDataNode.nullWorld)&&
	  !selected.fWorld.equals(secondSelected.fWorld)){  //they need to be in the same world, for modal
    bugAlert("Trying to Close Branch. Warning.", "The selected two formulas need to be in the same world.");
    return
        ;
  }
  TFormula firstFormula=null;

 if (selected.fAntecedents!=null&&
    selected.fAntecedents.size()==1){
      firstFormula=(TFormula)(selected.fAntecedents.get(0));
 }
 else
  return
    ;

  TFormula secondFormula=null;

 if (secondSelected.fAntecedents!=null&&
    secondSelected.fAntecedents.size()==1){
      secondFormula=(TFormula)(secondSelected.fAntecedents.get(0));
 }
 else
  return
    ;

 if (!TFormula.formulasContradict(firstFormula,secondFormula))
   return
       ;


 //They need to be in the same branch

 TreeNode[] firstbranch = selected.fTreeNode.getPath();
 TreeNode[] secondbranch = secondSelected.fTreeNode.getPath();

 boolean firstHigher= false;
 for (int i=0;(i<secondbranch.length)&&!firstHigher;i++)
   firstHigher=(secondbranch[i]==selected.fTreeNode);

 boolean secondHigher= false;
 for (int i=0;(i<firstbranch.length)&&!secondHigher;i++)
   secondHigher=(firstbranch[i]==secondSelected.fTreeNode);

 if (!firstHigher&&
     !secondHigher){
   bugAlert("Trying to Close Branch. Warning.", "The two formulas need to be in the same branch.");
   return
       ;
 }

 else{   //we are in business

   TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);

   newDataNode.fClosed = true;
   // newDataNode.fDead=true;  // don't make it dead else renderer will tick it

   // we need to add it after the lower selection
   if (firstHigher) {
     secondSelected.fTreeNode.removeAllChildren(); //immediate close
     secondSelected.straightInsert(newDataNode, null);
   }
   else {
     selected.fTreeNode.removeAllChildren(); //immediate close
     selected.straightInsert(newDataNode, null);
   }
   fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE, newDataNode); //need a listener for this

   //  deSelectAll();
 }
}
}
}

 /*
  public void selectOpenBranch(){
    TTreeDataNode openLeaf=fRoot.returnOpenLeaf();

    TreeNode[] openbranch = openLeaf.fTreeNode.getPath();
    Object userObject;

    for (int i=0;(i<openbranch.length);i++){
  userObject=((DefaultMutableTreeNode)openbranch[i]).getUserObject();
  if (userObject instanceof TTreeDataNode){  // we act only on these
    TTreeDataNode data = (TTreeDataNode) userObject;

    data.fS
  }
}


 }  */

 /*
   public boolean branchComplete(TreeNode[] branch){
     boolean complete=true;
     Object userObject;

     for (int i=0;(i<branch.length)&&complete;i++){
       userObject=((DefaultMutableTreeNode)branch[i]).getUserObject();
       if (userObject instanceof TTreeDataNode){  // we act only on these
         TTreeDataNode data = (TTreeDataNode) userObject;

         int type=typeOfFormula((TFormula)(data.fAntecedents.get(0)));

         if (!data.fDead&&
             type!=atomic&&
             type!=negatomic)
           complete=false;
       }
     }

  return
      complete;
   }
 */


void extendMenuItem_actionPerformed(ActionEvent e) {

    final TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();
    TFormula root;


    if ((selectedNodes!=null)&&(selectedNodes.length==1))
    {
      TTreeDataNode selected= selectedNodes[0];

      TFormula theFormula=null;

      if (!selected.fDead&&               //can select dead for closing formula
          selected.fAntecedents!=null&&
          selected.fAntecedents.size()==1){
        theFormula=(TFormula)(selected.fAntecedents.get(0));
      }
      else
        return
          ;

    switch (selected.typeOfFormula(theFormula)){

      case TTestNode.aand:
        doAnd(selected,theFormula);
        break;

      case TTestNode.implic:
        doImplic(selected,theFormula);
        break;

      case TTestNode.doubleneg:
        doDoubleNeg(selected,theFormula);
        break;

/*** Modal ***/
      case TTestNode.notPossible:
  doNegPossible(selected,theFormula);
  break;

case TTestNode.notNecessary:
doNegNecessary(selected,theFormula);
break;

case TTestNode.modalPossible:
  if (s5Rules)
     doPossible(selected,theFormula);
  else
  if (kRules)
     doPossibleR(selected,theFormula);
break;

case TTestNode.modalNecessary:
   if (s5Rules)
      doNecessary(selected,theFormula);
   else
   if (tRules)
	  doNecessaryT(selected,theFormula);  // order is important here, two right ways (S% and T) and one wrong way
   else
   if (kRules)
	   bugAlert("Trying "+chModalNecessary+ "R","With R necess, you need also to select a second line with an 'Access' relation.");


break;

/***** End of Modal ********/

/*** Epistemic ***/

case TTestNode.notModalKappa:
	doNotKnow(selected,theFormula);
	break;

case TTestNode.notModalRho:
	doNotKnowNot(selected,theFormula);
	break;
	
case TTestNode.modalRho:
	doPossibleKnow(selected,theFormula);
	break;
	
case TTestNode.modalKappa:
	doKnow(selected,theFormula);
	break;	

case TTestNode.modalDoubleKappa:
	doKnow(selected,theFormula);
	doTRKR(selected,theFormula);
	break;	
	
	
/*** End Epistemic ***/


      case TTestNode.equivv:
        doEquivv(selected,theFormula);
        break;

      case TTestNode.exi:
       doExi(selected,theFormula);
       break;
       
      case TTestNode.unique:
          doUnique(selected,theFormula);
          break;

     case TTestNode.negand:
       doNegAnd(selected,theFormula);
       break;

     case TTestNode.negarrow:
       doNegArrow(selected,theFormula);
       break;

     case TTestNode.negexi:
       doNegExi(selected,theFormula);
       break;

     case TTestNode.neguni:
       doNegUni(selected,theFormula);
       break;

     case TTestNode.nequiv:
       doNegEquiv(selected,theFormula);
       break;


     case TTestNode.nore:
       doNore(selected,theFormula);
       break;
       
     case TTestNode.negunique:
         doNegUnique(selected,theFormula);
         break;

      case TTestNode.ore:
        doOr(selected,theFormula);
        break;

      case TTestNode.uni:
        doUni(selected,theFormula);
        break;

      case TTestNode.typedUni:
        root=fParser.expandTypeUni(theFormula);
        if (root!=null)
           doUni(selected,root);
        break;
      case TTestNode.negTypedUni:    //FIX

        root=fParser.expandTypeUni(theFormula.fRLink);

        root = new TFormula(TFormula.unary,
                             String.valueOf(chNeg),
                             null,
                             root);

        doNegUni(selected,root);
        break;
      case TTestNode.typedExi:
        root=fParser.expandTypeExi(theFormula);
        if (root!=null)
          doExi(selected,root);
        break;

      case TTestNode.negTypedExi:    //FIX
        root=fParser.expandTypeExi(theFormula.fRLink);

        root = new TFormula(TFormula.unary,
                     String.valueOf(chNeg),
                     null,
                     root);

        doNegExi(selected,root);

        break;

    }

  }

  if ((selectedNodes!=null)&&(selectedNodes.length==2)){  //might be IE or necessaryR or necessarynecessaryR or necnecSymR

     if (isKRPossible(selectedNodes)){
    	 doKR(selectedNodes);
    	 return;
     }
	  
	  if (isNecessaryRPossible(selectedNodes)&& kRules){
	  
	if (s4Rules/*&&isNecessaryNecessaryRPossible(selectedNodes)*/){  //might be double necessary, could do either

	  	  
	  getTheChoice(new AbstractAction(chModalNecessary+ "R")
      {public void actionPerformed(ActionEvent ae){
        removeInputPane();
        doNecessaryR(selectedNodes);
      }},           //the calling actions must remove the input pane

    new AbstractAction(""+chModalNecessary+chModalNecessary+ "R")
      {public void actionPerformed(ActionEvent ae){
        removeInputPane();
        doNecessaryNecessaryR(selectedNodes);
      }},
    "You have a choice between the rules Necessary R and Necessary Necessary R.",
    "Please choose");}
	else
		doNecessaryR(selectedNodes);
    
	return ;

  }

  //else

    TTreeDataNode firstSelected = selectedNodes[0];
    TTreeDataNode secondSelected = selectedNodes[1];

    TFormula firstFormula = null;
    TFormula secondFormula = null;

    if ((firstSelected.fAntecedents != null &&
        firstSelected.fAntecedents.size() == 1)&&
       (secondSelected.fAntecedents != null &&
        secondSelected.fAntecedents.size() == 1)&&
       firstSelected.fWorld.equals(secondSelected.fWorld)

       )
   {
      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));
      if (typeEPossible(firstSelected,secondSelected,firstFormula,secondFormula))
    	  doTypeE(firstSelected,secondSelected,firstFormula,secondFormula);
      else   
    	  doIE(firstSelected,secondSelected,firstFormula,secondFormula);
    }

  }

}



void identityMenuItem_actionPerformed(ActionEvent e) {
  TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

if ((selectedNodes!=null)&&(selectedNodes.length==1))
{
  TTreeDataNode selected = selectedNodes[0];
  doII(selected);
}

}

void isClosedMenuItem_actionPerformed(ActionEvent e) {

  if (isTreeClosed())
    bugAlert("Tree closed?","Yes, it is. All branches are closed");
else
    bugAlert("Tree closed?","No, there is an open branch.");

}

void isCompleteMenuItem_actionPerformed(ActionEvent e) {

  if (isABranchOpenAndComplete())
    bugAlert("Is there a complete open branch?","Yes, there is one.");
else
    bugAlert("Is there a complete open branch?","No, every open branch is incomplete.") ;

}

void refMenuItem_actionPerformed(ActionEvent e) {

TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

	  if ((selectedNodes!=null)&&(selectedNodes.length==1))
	  {
	    TTreeDataNode selected = selectedNodes[0];
	    
	      TFormula theFormula=null;

	      if (//!selected.fDead&&               //can select dead for closing formula
	          !selected.fWorld.equals(TTreeDataNode.nullWorld)&&
	    	  selected.fAntecedents!=null&&
	          selected.fAntecedents.size()==1){
	        theFormula=(TFormula)(selected.fAntecedents.get(0));
	      }
	      else
	        return
	          ;
	      
	    doRefI(selected, theFormula);
	  }

	}

void symMenuItem_actionPerformed(ActionEvent e) {

	TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

		  if ((selectedNodes!=null)&&(selectedNodes.length==1))
		  {
		    TTreeDataNode selected = selectedNodes[0];
		    
		      TFormula theFormula=null;

		      if (//!selected.fDead&&               //can select dead for closing formula
		         // !selected.fWorld.equals(TTreeDataNode.nullWorld)&&
		    	  selected.fAntecedents!=null&&
		          selected.fAntecedents.size()==1){
		        theFormula=(TFormula)(selected.fAntecedents.get(0));
		      }
		      else
		        return
		          ;
		      
		    doSymI(selected, theFormula);
		  }

		}

void transMenuItem_actionPerformed(ActionEvent e) {

	TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

		  if ((selectedNodes!=null)&&(selectedNodes.length==2))
		  {
		    TTreeDataNode selected1 = selectedNodes[0];
		    TTreeDataNode selected2 = selectedNodes[1];
		    
		      TFormula theFormula1,theFormula2=null;

		      if (//!selected.fDead&&               //can select dead for closing formula
		         // !selected.fWorld.equals(TTreeDataNode.nullWorld)&&
		    	  selected1.fAntecedents!=null&&
		          selected1.fAntecedents.size()==1&&
		          selected2.fAntecedents!=null&&
		          selected2.fAntecedents.size()==1){
		        theFormula1=(TFormula)(selected1.fAntecedents.get(0));
		        theFormula2=(TFormula)(selected2.fAntecedents.get(0));
		      }
		      else
		        return
		          ;
		      
		    doTransI(selected1,selected2, theFormula1,theFormula2);
		  }

		}

void ruleSetMenuItem_actionPerformed(ActionEvent e) {

if (s4MenuItem.isSelected())
    s4Switch=true;
else
    s4Switch=false;

if (s5MenuItem.isSelected())
    s5Switch=true;
else
    s5Switch=false;

if (kMenuItem.isSelected())
      kSwitch=true;
  else
    kSwitch=false;

if (tMenuItem.isSelected())
    tSwitch=true;
else
    tSwitch=false;

if (s5AltMenuItem.isSelected())
	s5AltSwitch=true;
else
	s5AltSwitch=false;

// now, the rules include each other

if (s5Switch){
   s5Rules=true;
   kRules=false;
   tRules=false;
   s4Rules=false;
   s5AltRules=false;
}

if (kSwitch){
	   s5Rules=false;
	   kRules=true;
	   tRules=false;
	   s4Rules=false;
	   s5AltRules=false;
	}

if (tSwitch){
	   s5Rules=false;
	   kRules=true;
	   tRules=true;
	   s4Rules=false;
	   s5AltRules=false;
	}

if (s4Switch){
	   s5Rules=false;
	   kRules=true;
	   tRules=true;
	   s4Rules=true;
	   s5AltRules=false;
	}

if (s5AltSwitch){
	   s5Rules=false;
	   kRules=true;
	   tRules=true;
	   s4Rules=true;
	   s5AltRules=true;
	}
}


  void startOverMenuItem_actionPerformed(ActionEvent e) {

startTree(fStartStr);
}

  void symmMenuItem_actionPerformed(ActionEvent e) {

	  TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

	  	  if ((selectedNodes!=null)&&(selectedNodes.length==1))
	  	  {
	  	    TTreeDataNode selected = selectedNodes[0];
	  	    
	  	      TFormula theFormula=null;

	  	      if (
	  	    	  selected.fAntecedents!=null&&
	  	          selected.fAntecedents.size()==1){
	  	        theFormula=(TFormula)(selected.fAntecedents.get(0));
	  	      }
	  	      else
	  	        return
	  	          ;
	  	      
	  	    doSymI(selected, theFormula);
	  	  }

	  	}


  void deSelectAll(){
    fTreeTableView.removeRowSelectionInterval(0,fTreeTableModel.fRowCount-1);

  }

  void doSetUpActionsMenu(){
    TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

    if (((selectedNodes!=null)&&(selectedNodes.length==1))||
        isIEPossible(selectedNodes)||          //length 2
        isNecessaryRPossible(selectedNodes)||          //length 2
        isKRPossible(selectedNodes)){
      extendMenuItem.setEnabled(true);
    }
     else{
       extendMenuItem.setEnabled(false);
     }
 /*   
    if ((selectedNodes!=null)&&(selectedNodes.length==1)){
        anaConMenuItem.setEnabled(true);
      }
       else{
         anaConMenuItem.setEnabled(false);
      } */

     if ((selectedNodes!=null)&&(selectedNodes.length==1)){
       identityMenuItem.setEnabled(true);
     }
      else{
        identityMenuItem.setEnabled(false);
     }


     if ((selectedNodes!=null)&&((selectedNodes.length==1)||(selectedNodes.length==2)))
       closeMenuItem.setEnabled(true);
    else
      closeMenuItem.setEnabled(false);




  }

   void fActionsMenu_menuSelected(MenuEvent e) {
     doSetUpActionsMenu();
}

class TTreePanel_fActionsMenu_menuAdapter implements javax.swing.event.MenuListener {
     TTreePanel adaptee;

     TTreePanel_fActionsMenu_menuAdapter(TTreePanel adaptee) {
       this.adaptee = adaptee;
     }
     public void menuSelected(MenuEvent e) {
       adaptee.fActionsMenu_menuSelected(e);
     }
     public void menuDeselected(MenuEvent e) {
     }
     public void menuCanceled(MenuEvent e) {
     }
}



  /**************************** Extension rules *****************************/



  void doAnd(TTreeDataNode selected,TFormula theFormula){
    TFormula leftFormula=theFormula.fLLink.copyFormula();

    TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
    leftDataNode.fAntecedents.add(0,leftFormula);
    leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
    leftDataNode.fJustification=andDJustification;

    TFormula rightFormula=theFormula.fRLink.copyFormula();

    TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
    rightDataNode.fAntecedents.add(0,rightFormula);
    rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
    rightDataNode.fJustification=andDJustification;


    selected.fDead=true;

    straightInsert(selected,leftDataNode,rightDataNode);
  };

  void doDoubleNeg(TTreeDataNode selected,TFormula theFormula){
    TFormula newFormula=theFormula.fRLink.fRLink.copyFormula();

    TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
    newDataNode.fAntecedents.add(0,newFormula);
    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
    newDataNode.fJustification=negDJustification;

    selected.fDead=true;

    straightInsert(selected,newDataNode,null);
  };


/**************** Modal Rules *************************/

  void doNegPossible(TTreeDataNode selected,TFormula theFormula){
    TFormula newFormula=theFormula.copyFormula();

    newFormula.fInfo= String.valueOf(chModalNecessary);  //permuting operators
    newFormula.fRLink.fInfo= String.valueOf(Symbols.chNeg);

    TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
    newDataNode.fAntecedents.add(0,newFormula);
    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
    newDataNode.fJustification=notPossibleJustification;
    newDataNode.fWorld=selected.fWorld;

    selected.fDead=true;

    straightInsert(selected,newDataNode,null);
  };

  void doNegNecessary(TTreeDataNode selected,TFormula theFormula){
    TFormula newFormula=theFormula.copyFormula();

    newFormula.fInfo= String.valueOf(chModalPossible);  //permuting operators
    newFormula.fRLink.fInfo= String.valueOf(Symbols.chNeg);

    TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
    newDataNode.fAntecedents.add(0,newFormula);
    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
    newDataNode.fJustification=notPossibleJustification;
    newDataNode.fWorld=selected.fWorld;

    selected.fDead=true;

    straightInsert(selected,newDataNode,null);
  };


  void doPossible(TTreeDataNode selected,TFormula theFormula){

  String newWorld = newWorldForBranches(selected.fTreeNode);

  if (newWorld.equals(""))
    return;

 TFormula scope=theFormula.fRLink.copyFormula();

 TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
 newDataNode.fAntecedents.add(0,scope);
 newDataNode.fFirstjustno=selected.fLineno;
 newDataNode.fWorld=newWorld;
 newDataNode.fJustification=s5PossJustification;


 selected.fDead=true;

 straightInsert(selected,newDataNode,null);
};





void doPossibleR(TTreeDataNode selected,TFormula theFormula){  //restricted on accessibility

  String newWorld = newWorldForBranches(selected.fTreeNode);

  if (newWorld.equals(""))
    return;

 String oldWorld=selected.fWorld;

TFormula access=fParser.makeAnAccessRelation(oldWorld,newWorld);

 TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
 newDataNode.fAntecedents.add(0,access);
 newDataNode.fFirstjustno=selected.fLineno;
 newDataNode.fJustification=rPossJustification;

 selected.fDead=true;

 straightInsert(selected,newDataNode,null);

TFormula scope=theFormula.fRLink.copyFormula();

newDataNode = new TTreeDataNode(fParser,fTreeModel);
newDataNode.fAntecedents.add(0,scope);
newDataNode.fFirstjustno=selected.fLineno;
newDataNode.fJustification=rPossJustification;
newDataNode.fWorld=newWorld;

selected.fDead=true;

straightInsert(selected,newDataNode,null);

};


/************************ Necessary Action****************/

  public class NecessaryAction extends AbstractAction{
     JTextComponent fText;
     TTreeDataNode fSelected=null;
     TFormula fFormula=null;

     public NecessaryAction(JTextComponent text, String label, TTreeDataNode selected, TFormula formula){
       putValue(NAME, label);

       fText=text;
       fSelected=selected;
       fFormula=formula;
     }

     public void actionPerformed(ActionEvent ae){


       String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

       boolean wellformed=false;

       if (fParser.isPossibleWorld(aString))//(aString.length()==1&& (fWorlds.indexOf(aString))>-1)
         wellformed=true;

       if (!wellformed) {
         String message = "You need to enter a single lower case letter or single numeral."; //filter out returns

                          //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

                          fText.setText(message);
                          fText.selectAll();
                          fText.requestFocus();
                        }

                        else {   // we're good

                          TFormula scope = fFormula.fRLink.copyFormula();

                            TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
                            newDataNode.fAntecedents.add(0,scope);
                            newDataNode.fFirstjustno=fSelected.fLineno;
                            newDataNode.fJustification= s5NecessJustification;
                            newDataNode.fWorld= aString;


                           // selected.fDead=true;    don't make it dead


                            straightInsert(fSelected,newDataNode,null);

                            removeInputPane();

                        }

                    }

                  }


/************************ End of UI Action *********/




  void doNecessary(TTreeDataNode selected,TFormula theFormula){
    JButton defaultButton;
    TProofInputPanel inputPane;

    JTextField text = new JTextField("Enter the index for the possible world (a single lower case letter or numeral)?");
    text.selectAll();

    defaultButton = new JButton(new NecessaryAction(text,"Go", selected, theFormula));

    JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
    inputPane = new TProofInputPanel("Doing "+chModalNecessary+ "S5", text, buttons);


    addInputPane(inputPane);
    inputPane.getRootPane().setDefaultButton(defaultButton);
    fInputPane.setVisible(true); // need this
    text.requestFocus();         // so selected text shows
};


void doNecessaryT(TTreeDataNode selected,TFormula theFormula){
	

   TFormula scope = theFormula.fRLink.copyFormula();

	                            TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
	                            newDataNode.fAntecedents.add(0,scope);
	                            newDataNode.fFirstjustno=selected.fLineno;
	                            newDataNode.fJustification= tNecessJustification;
	                            newDataNode.fWorld= selected.fWorld;


	                            selected.fDead=true;   


	                            straightInsert(selected,newDataNode,null);
	                        }

	



boolean isNecessaryRPossible(TTreeDataNode [] selectedNodes){   //check for two selected access relation and necess
// preliminary check
	
	if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
	    TTreeDataNode firstSelected = selectedNodes[0];
	    TTreeDataNode secondSelected = selectedNodes[1];

	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	    if ( (firstSelected.fAntecedents != null &&
	          firstSelected.fAntecedents.size() == 1) &&
	        (secondSelected.fAntecedents != null &&
	         secondSelected.fAntecedents.size() == 1)) {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	      if (!TParser.isModalNecessary(firstFormula)&&
	    	  !TParser.isModalNecessary(secondFormula))
	    	  return
	    	  	false;
	    }}
	else
		return
			false;
	
	
	
	
	
	return
	   necessaryRExtension(selectedNodes)!=null;

  /*  if ((selectedNodes!=null)&&(selectedNodes.length==2)){
      TTreeDataNode firstSelected = selectedNodes[0];
      TTreeDataNode secondSelected = selectedNodes[1];

      TFormula firstFormula = null;
      TFormula secondFormula = null;

    if ((firstSelected.fAntecedents != null &&
        firstSelected.fAntecedents.size() == 1)&&
       (secondSelected.fAntecedents != null &&
        secondSelected.fAntecedents.size() == 1)) {
      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

      if (fParser.getAccessRelation(firstFormula).equals("")
          &&
          fParser.getAccessRelation(secondFormula).equals(""))

        return
            false;

      if (firstSelected.typeOfFormula(firstFormula)==TTestNode.modalNecessary
          ||
          secondSelected.typeOfFormula(secondFormula)==TTestNode.modalNecessary)

        return
            true;


    }

  }
  return
      false; */

}

/* This is a nonsense
boolean isNecessaryNecessaryRPossible(TTreeDataNode [] selectedNodes){   //check for two selected access relation and necess
	
	//need one to have double necessary
	
	   TTreeDataNode firstSelected = selectedNodes[0];
	    TTreeDataNode secondSelected = selectedNodes[1];

	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	    if ((firstSelected.fAntecedents != null &&
	        firstSelected.fAntecedents.size() == 1)&&
	       (secondSelected.fAntecedents != null &&
	        secondSelected.fAntecedents.size() == 1))
	   {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));
	    }
	
	if (firstFormula==null||secondFormula==null)
		return
		   false;
	
	if (fParser.isModalNecessary(firstFormula)&&
		!fParser.isModalNecessary(firstFormula.getRLink())	)
		return
		   false;
	
	if (fParser.isModalNecessary(secondFormula)&&
			!fParser.isModalNecessary(secondFormula.getRLink())	)
			return
			   false;
	
	return
	   necessaryRExtension(selectedNodes)!=null;

}
*/

boolean isNecessaryNecessarySymRPossible(TTreeDataNode [] selectedNodes){   //check for two selected access relation and necess

    if ((selectedNodes!=null)&&(selectedNodes.length==2)){
      TTreeDataNode firstSelected = selectedNodes[0];
      TTreeDataNode secondSelected = selectedNodes[1];

      TFormula firstFormula = null;
      TFormula secondFormula = null;

    if ((firstSelected.fAntecedents != null &&
        firstSelected.fAntecedents.size() == 1)&&
       (secondSelected.fAntecedents != null &&
        secondSelected.fAntecedents.size() == 1)) {
      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

      if (fParser.getAccessRelation(firstFormula).equals("")
          &&
          fParser.getAccessRelation(secondFormula).equals(""))

        return
            false;

      if (firstSelected.typeOfFormula(firstFormula)==TTestNode.modalNecessary
          ||
          secondSelected.typeOfFormula(secondFormula)==TTestNode.modalNecessary)

        return
            true;


    }

  }
  return
      false;

}


TTreeDataNode necessaryRExtension(TTreeDataNode [] selectedNodes){

	  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
	    TTreeDataNode firstSelected = selectedNodes[0];
	    TTreeDataNode secondSelected = selectedNodes[1];

	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	    if ( (firstSelected.fAntecedents != null &&
	          firstSelected.fAntecedents.size() == 1) &&
	        (secondSelected.fAntecedents != null &&
	         secondSelected.fAntecedents.size() == 1)) {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	      //They need to be in the same branch

	      TreeNode[] firstbranch = firstSelected.fTreeNode.getPath();
	      TreeNode[] secondbranch = secondSelected.fTreeNode.getPath();

	      boolean firstHigher = false;
	      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
	        firstHigher = (secondbranch[i] == firstSelected.fTreeNode);

	      boolean secondHigher = false;
	      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
	        secondHigher = (firstbranch[i] == secondSelected.fTreeNode);

	      if (!firstHigher &&
	          !secondHigher) {
	        bugAlert("Trying to do Necessary R. Warning.",
	                 "The two formulas need to be in the same branch.");
	        return
	           null;
	            
	      }

	      else { //we are in business

	        boolean firstIsAccess = true;
	        String worlds = fParser.getAccessRelation(firstFormula);

	        TTreeDataNode accessSelected = firstSelected;
	        TTreeDataNode necessarySelected = secondSelected;
	        TFormula access = firstFormula;
	        TFormula necessary = secondFormula;

	        if (worlds.equals("")) {
	          accessSelected = secondSelected;
	          necessarySelected = firstSelected;
	          access = secondFormula;
	          necessary = firstFormula;
	          worlds = fParser.getAccessRelation(access);
	        }
	        
	        if (worlds.equals("")) {    // no worlds at all, this could happen with non-modal formulas
	        	bugAlert("Trying to do Necessary R. Warning.",
                "There is no Access relation.");
	        	
	        	return
		          null;
		        }
	       
	        
	        if (!fParser.isModalNecessary(necessary)) {    // no worlds at all, this could happen with non-modal formulas
	        	bugAlert("Trying to do Necessary R. Warning.",
                "There is no Necessary formula.");
	        	
	        	return
		          null;
		        }
	        
	        

	        //we need to check that the access from and the world of the necess are the same

	        if (!necessarySelected.fWorld.equals(worlds.substring(0, 1))) {
	          bugAlert("Trying to do Necessary R. Warning.",
	                   "The necessary formula does not have Access.");
	          return
	             null;

	        }

	        TFormula scope = necessary.fRLink.copyFormula();

	        TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);
	        newDataNode.fAntecedents.add(0, scope);
	        newDataNode.fFirstjustno = necessarySelected.fLineno;
	        newDataNode.fSecondjustno = accessSelected.fLineno;
	        newDataNode.fJustification = rNecessJustification;
	        newDataNode.fWorld = worlds.substring(1, 2);

	        return
	           newDataNode;
	      }

	    }
	  }
	  return
	     null;
	}


void doNecessaryR(TTreeDataNode [] selectedNodes){
/*
  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
    TTreeDataNode firstSelected = selectedNodes[0];
    TTreeDataNode secondSelected = selectedNodes[1];

    TFormula firstFormula = null;
    TFormula secondFormula = null;

    if ( (firstSelected.fAntecedents != null &&
          firstSelected.fAntecedents.size() == 1) &&
        (secondSelected.fAntecedents != null &&
         secondSelected.fAntecedents.size() == 1)) {
      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

      //They need to be in the same branch

      TreeNode[] firstbranch = firstSelected.fTreeNode.getPath();
      TreeNode[] secondbranch = secondSelected.fTreeNode.getPath();

      boolean firstHigher = false;
      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
        firstHigher = (secondbranch[i] == firstSelected.fTreeNode);

      boolean secondHigher = false;
      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
        secondHigher = (firstbranch[i] == secondSelected.fTreeNode);

      if (!firstHigher &&
          !secondHigher) {
        bugAlert("Trying to do Necessary R. Warning.",
                 "The two formulas need to be in the same branch.");
        return
            ;
      }

      else { //we are in business

        boolean firstIsAccess = true;
        String worlds = fParser.getAccessRelation(firstFormula);

        TTreeDataNode accessSelected = firstSelected;
        TTreeDataNode necessarySelected = secondSelected;
        TFormula access = firstFormula;
        TFormula necessary = secondFormula;

        if (worlds.equals("")) {
          accessSelected = secondSelected;
          necessarySelected = firstSelected;
          access = secondFormula;
          necessary = firstFormula;
          worlds = fParser.getAccessRelation(access);
        }

        //we need to check that the access from and the world of the necess are the same

        if (!necessarySelected.fWorld.equals(worlds.substring(0, 1))) {
          bugAlert("Trying to do Necessary R. Warning.",
                   "The necessary formula does not have Access.");
          return;

        }

        TFormula scope = necessary.fRLink.copyFormula();

        TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);
        newDataNode.fAntecedents.add(0, scope);
        newDataNode.fFirstjustno = necessarySelected.fLineno;
        newDataNode.fSecondjustno = accessSelected.fLineno;
        newDataNode.fJustification = rNecessJustification;
        newDataNode.fWorld = worlds.substring(1, 2);

        straightInsert(necessarySelected, newDataNode, null);
      }

    }
  } */
	
	TTreeDataNode newDataNode=necessaryRExtension(selectedNodes);
	
	if (newDataNode!=null){
		TTreeDataNode necessarySelected = fParser.isModalNecessary((TFormula) (selectedNodes[0].fAntecedents.get(0)))?
				                          selectedNodes[0]:
					                      selectedNodes[1];
		straightInsert(necessarySelected, newDataNode, null);		
	}
}

void doNecessaryNecessaryR(TTreeDataNode [] selectedNodes){

	  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
	    TTreeDataNode firstSelected = selectedNodes[0];
	    TTreeDataNode secondSelected = selectedNodes[1];

	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	    if ( (firstSelected.fAntecedents != null &&
	          firstSelected.fAntecedents.size() == 1) &&
	        (secondSelected.fAntecedents != null &&
	         secondSelected.fAntecedents.size() == 1)) {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	      //They need to be in the same branch

	      TreeNode[] firstbranch = firstSelected.fTreeNode.getPath();
	      TreeNode[] secondbranch = secondSelected.fTreeNode.getPath();

	      boolean firstHigher = false;
	      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
	        firstHigher = (secondbranch[i] == firstSelected.fTreeNode);

	      boolean secondHigher = false;
	      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
	        secondHigher = (firstbranch[i] == secondSelected.fTreeNode);

	      if (!firstHigher &&
	          !secondHigher) {
	        bugAlert("Trying to do Necessary Necessary R. Warning.",
	                 "The two formulas need to be in the same branch.");
	        return
	            ;
	      }

	      else { //we are in business

	        boolean firstIsAccess = true;
	        String worlds = fParser.getAccessRelation(firstFormula);

	        TTreeDataNode accessSelected = firstSelected;
	        TTreeDataNode necessarySelected = secondSelected;
	        TFormula access = firstFormula;
	        TFormula necessary = secondFormula;

	        if (worlds.equals("")) {
	          accessSelected = secondSelected;
	          necessarySelected = firstSelected;
	          access = secondFormula;
	          necessary = firstFormula;
	          worlds = fParser.getAccessRelation(access);
	        }

	        //we need to check that the access from and the world of the necess are the same

	        if (!necessarySelected.fWorld.equals(worlds.substring(0, 1))) {
	          bugAlert("Trying to do Necessary Necessary R. Warning.",
	                   "The necessary formula does not have Access.");
	          return;

	        }

	        TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);
	        newDataNode.fAntecedents.add(0, necessary.copyFormula());
	        newDataNode.fFirstjustno = necessarySelected.fLineno;
	        newDataNode.fSecondjustno = accessSelected.fLineno;
	        newDataNode.fJustification = rNecessNecessJustification;
	        newDataNode.fWorld = worlds.substring(1, 2);

	        straightInsert(necessarySelected, newDataNode, null);
	      }

	    }
	  }
	}

void doNecessaryNecessarySymR(TTreeDataNode [] selectedNodes){

	  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
	    TTreeDataNode firstSelected = selectedNodes[0];
	    TTreeDataNode secondSelected = selectedNodes[1];

	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	    if ( (firstSelected.fAntecedents != null &&
	          firstSelected.fAntecedents.size() == 1) &&
	        (secondSelected.fAntecedents != null &&
	         secondSelected.fAntecedents.size() == 1)) {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	      //They need to be in the same branch

	      TreeNode[] firstbranch = firstSelected.fTreeNode.getPath();
	      TreeNode[] secondbranch = secondSelected.fTreeNode.getPath();

	      boolean firstHigher = false;
	      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
	        firstHigher = (secondbranch[i] == firstSelected.fTreeNode);

	      boolean secondHigher = false;
	      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
	        secondHigher = (firstbranch[i] == secondSelected.fTreeNode);

	      if (!firstHigher &&
	          !secondHigher) {
	        bugAlert("Trying to do Necessary Necessary SymR. Warning.",
	                 "The two formulas need to be in the same branch.");
	        return
	            ;
	      }

	      else { //we are in business

	        boolean firstIsAccess = true;
	        String worlds = fParser.getAccessRelation(firstFormula);

	        TTreeDataNode accessSelected = firstSelected;
	        TTreeDataNode necessarySelected = secondSelected;
	        TFormula access = firstFormula;
	        TFormula necessary = secondFormula;

	        if (worlds.equals("")) {
	          accessSelected = secondSelected;
	          necessarySelected = firstSelected;
	          access = secondFormula;
	          necessary = firstFormula;
	          worlds = fParser.getAccessRelation(access);
	        }

	        //we need to check that the access to and the world of the necess are the same
	        
	        String fromWorld=worlds.substring(1, 2);
	        String toWorld=worlds.substring(0, 1);

	        if (!necessarySelected.fWorld.equals(fromWorld)) {
	          bugAlert("Trying to do Necessary Necessary SymR. Warning.",
	                   "The necessary formula does not have 'inverse' Access.");
	          return;

	        }

	        TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);
	        newDataNode.fAntecedents.add(0, necessary.copyFormula());
	        newDataNode.fFirstjustno = necessarySelected.fLineno;
	        newDataNode.fSecondjustno = accessSelected.fLineno;
	        newDataNode.fJustification = rNecessNecessJustification;
	        newDataNode.fWorld = toWorld;

	        straightInsert(necessarySelected, newDataNode, null);
	      }

	    }
	  }
	}


/***************************  Type Elimination ********/

//want a type identity and a typed formula

public boolean typeEPossible (TTreeDataNode firstSelected,TTreeDataNode secondSelected,
        TFormula firstFormula,TFormula secondFormula){
	TTreeDataNode identitySelected = null;
    TTreeDataNode typedSelected = null;
    TFormula identity = null;
    TFormula typed = null;
    
	int firstType=firstSelected.typeOfFormula(firstFormula);
	int secondType=secondSelected.typeOfFormula(secondFormula);
		
	if ((firstType==TTestNode.atomic && TFormula.isEquality(firstFormula))&&
		((secondType==TTestNode.typedExi)||
		 (secondType==TTestNode.typedUni)||	
		(secondType==TTestNode.negTypedExi)||
		(secondType==TTestNode.negTypedUni))
		){
		identitySelected = firstSelected;
	    typedSelected = secondSelected;
	    identity = firstFormula;
	    typed = secondFormula;		
	}
	else
		if ((secondType==TTestNode.atomic && TFormula.isEquality(secondFormula))&&
				((firstType==TTestNode.typedExi)||
				 (firstType==TTestNode.typedUni)||	
				(firstType==TTestNode.negTypedExi)||
				(firstType==TTestNode.negTypedUni))
				){
				identitySelected = secondSelected;
			    typedSelected = firstSelected;
			    identity = secondFormula;
			    typed = firstFormula;
			    secondType=firstType;
			}
	
	// we know the first type is equality, the second type is what it is
		
	if (identitySelected==null||typedSelected==null)
		return
		   false;
	
	//we're good to go further.
	
	TFormula firstTerm=identity.firstTerm();
	TFormula secondTerm=identity.secondTerm();

   if (!((firstTerm.isClosedTerm() &&
		secondTerm.isClosedTerm())))
	   return
	   false;
 
   //we're good to go further.
   
   TFormula typeFormula=null;
   
   if (secondType==TTestNode.typedExi||
       secondType==TTestNode.typedUni)
	   typeFormula=typed.quantTypeForm();
   else
	   if (secondType==TTestNode.negTypedExi||
		       secondType==TTestNode.negTypedUni)
			   typeFormula=typed.fRLink.quantTypeForm();
   
   TFormula replacement=null;
   
   if (firstTerm.equalFormulas(firstTerm, typeFormula))
	   replacement=secondTerm.copyFormula();

   if (secondTerm.equalFormulas(secondTerm, typeFormula))
	   replacement=firstTerm.copyFormula();;
   
   if (replacement==null)
	   return
	   false;
   
   return
   true;


}


// want a type identity and a typed formula

public void doTypeE (TTreeDataNode firstSelected,TTreeDataNode secondSelected,
        TFormula firstFormula,TFormula secondFormula){
	TTreeDataNode identitySelected = null;
    TTreeDataNode typedSelected = null;
    TFormula identity = null;
    TFormula typed = null;
    
	int firstType=firstSelected.typeOfFormula(firstFormula);
	int secondType=secondSelected.typeOfFormula(secondFormula);
		
	if ((firstType==TTestNode.atomic && TFormula.isEquality(firstFormula))&&
		((secondType==TTestNode.typedExi)||
		 (secondType==TTestNode.typedUni)||	
		(secondType==TTestNode.negTypedExi)||
		(secondType==TTestNode.negTypedUni))
		){
		identitySelected = firstSelected;
	    typedSelected = secondSelected;
	    identity = firstFormula;
	    typed = secondFormula;		
	}
	else
		if ((secondType==TTestNode.atomic && TFormula.isEquality(secondFormula))&&
				((firstType==TTestNode.typedExi)||
				 (firstType==TTestNode.typedUni)||	
				(firstType==TTestNode.negTypedExi)||
				(firstType==TTestNode.negTypedUni))
				){
				identitySelected = secondSelected;
			    typedSelected = firstSelected;
			    identity = secondFormula;
			    typed = firstFormula;
			    secondType=firstType;
			}
	
	// we know the first type is equality, the second type is what it is
		
	if (identitySelected==null||typedSelected==null)
		return;
	
	//we're good to go further.
	
	TFormula firstTerm=identity.firstTerm();
	TFormula secondTerm=identity.secondTerm();

   if (!((firstTerm.isClosedTerm() &&
		secondTerm.isClosedTerm())))
      return;
 
   //we're good to go further.
   
   TFormula typeFormula=null;
   
   if (secondType==TTestNode.typedExi||
       secondType==TTestNode.typedUni)
	   typeFormula=typed.quantTypeForm();
   else
	   if (secondType==TTestNode.negTypedExi||
		       secondType==TTestNode.negTypedUni)
			   typeFormula=typed.fRLink.quantTypeForm();
   
   TFormula replacement=null;
   
   if (firstTerm.equalFormulas(firstTerm, typeFormula))
	   replacement=secondTerm.copyFormula();

   if (secondTerm.equalFormulas(secondTerm, typeFormula))
	   replacement=firstTerm.copyFormula();;
   
   if (replacement==null)
	   return;
   
   TFormula newFormula=typed.copyFormula();
   
   if (secondType==TTestNode.typedExi||
	       secondType==TTestNode.typedUni)
	   newFormula.setQuantType(replacement);
	   else
		   if (secondType==TTestNode.negTypedExi||
			       secondType==TTestNode.negTypedUni)
			   newFormula.fRLink.setQuantType(replacement);
 
 
   TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
   newDataNode.fAntecedents.add(0,newFormula);
   newDataNode.fFirstjustno=firstSelected.fLineno;
   newDataNode.fSecondjustno=secondSelected.fLineno;
   newDataNode.fJustification= typeEJustification;
  // newDataNode.fWorld= selected.fWorld;  
   
   straightInsert(firstSelected,newDataNode,null);


}





/**************** end of Type Elinination ******/





/************* Access **************/


void doRefI(TTreeDataNode selected, TFormula theFormula){

  //we require selected here to pick out the desired world branch

       TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
       newDataNode.fAntecedents.add(0,fParser.makeAnAccessRelation(selected.fWorld,selected.fWorld));
       newDataNode.fFirstjustno=selected.fLineno;
       newDataNode.fJustification= accessRefJustification;
      // newDataNode.fWorld= selected.fWorld;  
       
       straightInsert(selected,newDataNode,null);
  }

void doSymI(TTreeDataNode selected, TFormula theFormula){

	  //selected needs to be an access relation
	
    String worlds = fParser.getAccessRelation(theFormula);

    if (worlds.equals("")) {
    	bugAlert("Trying to do Access Symmetry. Warning.", "Select an Access relation.");
    	return;
    		
    }
	
	TFormula access=fParser.makeAnAccessRelation(worlds.substring(1, 2),worlds.substring(0, 1));

	       TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
	       newDataNode.fAntecedents.add(0,access);
	       newDataNode.fFirstjustno=selected.fLineno;
	       newDataNode.fJustification= accessSymJustification;
	      // newDataNode.fWorld= selected.fWorld;  
	       
	       straightInsert(selected,newDataNode,null);
	  }

void doTransI(TTreeDataNode selected1,TTreeDataNode selected2, TFormula theFormula1,TFormula theFormula2){

	  //selected needs to be an access relation
	
  String worlds1 = fParser.getAccessRelation(theFormula1);
  String worlds2 = fParser.getAccessRelation(theFormula2);

  if (worlds1.equals("")||
	  worlds2.equals("")) {
  	bugAlert("Trying to do Access Trans. Warning.", "Select two Access relations.");
  	return;
  		
  }
	
 boolean reverse=false;
 
 if (worlds1.charAt(1)!=worlds2.charAt(0)){
	 reverse=true;
	 if (worlds2.charAt(1)!=worlds1.charAt(0)){
		 bugAlert("Trying to do Access Trans. Warning.", "The second world of one Access must be the first of the other.");
		  	return;
	 }
 }
  
 String w1 = reverse?worlds2.substring(0, 1):worlds1.substring(0, 1); 
 String w2 = reverse?worlds1.substring(1, 2):worlds2.substring(1, 2); 
  
  TFormula access=fParser.makeAnAccessRelation(w1,w2);

	       TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
	       newDataNode.fAntecedents.add(0,access);
	       newDataNode.fFirstjustno=reverse?selected2.fLineno:selected1.fLineno;
	       newDataNode.fSecondjustno=reverse?selected1.fLineno:selected2.fLineno;
	       newDataNode.fJustification= accessTransJustification;
	      // newDataNode.fWorld= selected.fWorld;  
	       
	       straightInsert(reverse?selected2:selected1,newDataNode,null);
	  }

/*************** End of Modal Rules ***********************/


/**************** Epistemic Rules *************************/

TTreeDataNode kRExtension(TTreeDataNode [] selectedNodes){
	
	// one is kappa(agent, prop), the other Access(agent,from,to)

	  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
	    TTreeDataNode firstSelected = selectedNodes[0];
	    TTreeDataNode secondSelected = selectedNodes[1];

	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	    if ( (firstSelected.fAntecedents != null &&
	          firstSelected.fAntecedents.size() == 1) &&
	        (secondSelected.fAntecedents != null &&
	         secondSelected.fAntecedents.size() == 1)) {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	      //They need to be in the same branch

	      TreeNode[] firstbranch = firstSelected.fTreeNode.getPath();
	      TreeNode[] secondbranch = secondSelected.fTreeNode.getPath();

	      boolean firstHigher = false;
	      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
	        firstHigher = (secondbranch[i] == firstSelected.fTreeNode);

	      boolean secondHigher = false;
	      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
	        secondHigher = (firstbranch[i] == secondSelected.fTreeNode);

	      if (!firstHigher &&
	          !secondHigher) {
	        bugAlert("Trying to do K R. Warning.",
	                 "The two formulas need to be in the same branch.");
	        return
	           null;
	            
	      }

	      else { //we are in business

	        boolean firstIsAccess = true;
	        String worlds = fParser.getEAccessRelation(firstFormula);

	        TTreeDataNode accessSelected = firstSelected;
	        TTreeDataNode necessarySelected = secondSelected;
	        TFormula access = firstFormula;
	        TFormula necessary = secondFormula;

	        if (worlds.equals("")) {
	          accessSelected = secondSelected;
	          necessarySelected = firstSelected;
	          access = secondFormula;
	          necessary = firstFormula;
	          worlds = fParser.getEAccessRelation(access);
	        }
	        
	        if (worlds.equals("")) {    // no worlds at all, this could happen with non-modal formulas
	        	bugAlert("Trying to do KR. Warning.",
                "There is no EAccess relation.");
	        	return
		          null;
		        }
	        
	        if (!fParser.isModalKappa(necessary)) {    // no worlds at all, this could happen with non-modal formulas
	        	bugAlert("Trying to do KR. Warning.",
                "There is no Knows formula.");
	        	
	        	return
		          null;
		        }

	        //we need to check that the access from and the world of the necess are the same

	        String agent=worlds.substring(0, 1);
	        String from=worlds.substring(1, 2);
	        String to=worlds.substring(2, 3);
	        
	        if (!necessary.fLLink.fInfo.equals(agent)) {
		          bugAlert("Trying to do KR. Warning.",
		                   "The agent has to be the same for the formula and EAccess.");
		          return
		             null;

		        }
	        
	        if (!necessarySelected.fWorld.equals(from)) {
	          bugAlert("Trying to do KR. Warning.",
	                   "The necessary formula does not have Access.");
	          return
	             null;

	        }

	        TFormula scope = necessary.fRLink.copyFormula();

	        TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);
	        newDataNode.fAntecedents.add(0, scope);
	        newDataNode.fFirstjustno = necessarySelected.fLineno;
	        newDataNode.fSecondjustno = accessSelected.fLineno;
	        newDataNode.fJustification = kRJustification;
	        newDataNode.fWorld = to;

	        return
	           newDataNode;
	      }

	    }
	  }
	  return
	     null;
	}

boolean isKRPossible(TTreeDataNode [] selectedNodes){   //check for two selected access relation and necess
// preliminary check
// we'll define it to be possible of one K formula
	// then let the routine give the error messages
	
	if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
	    TTreeDataNode firstSelected = selectedNodes[0];
	    TTreeDataNode secondSelected = selectedNodes[1];

	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	    if ( (firstSelected.fAntecedents != null &&
	          firstSelected.fAntecedents.size() == 1) &&
	        (secondSelected.fAntecedents != null &&
	         secondSelected.fAntecedents.size() == 1)) {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	      if (TParser.isModalKappa(firstFormula)||
	    	  TParser.isModalKappa(secondFormula))
	    	  return
	    	  	true;
	    }}

		return
			false;
}

void doKR(TTreeDataNode [] selectedNodes){
		
		TTreeDataNode newDataNode=kRExtension(selectedNodes);
		
		if (newDataNode!=null){
			TTreeDataNode necessarySelected = fParser.isModalKappa((TFormula) (selectedNodes[0].fAntecedents.get(0)))?
					                          selectedNodes[0]:
						                      selectedNodes[1];
			straightInsert(necessarySelected, newDataNode, null);
			
			//we actually want to put a second node in here, the KKR node
			
			TTreeDataNode kKRNode = new TTreeDataNode(fParser, fTreeModel);
			kKRNode.fAntecedents.add(0, ((TFormula)(necessarySelected.fAntecedents.get(0))).copyFormula());
			kKRNode.fFirstjustno = newDataNode.fFirstjustno;
			kKRNode.fSecondjustno = newDataNode.fSecondjustno;
			kKRNode.fJustification = kKRJustification;
			kKRNode.fWorld = newDataNode.fWorld;
			
			straightInsert(necessarySelected, kKRNode, null);
			
			
		}
	}



void doNotKnowNot(TTreeDataNode selected,TFormula theFormula){
	// this has neg rho(agent,prop) and we want kappa (agent, neg prop) 	
	
	  TFormula agent=theFormula.fRLink.fLLink.copyFormula();
	  TFormula prop=theFormula.fRLink.fRLink.copyFormula();
		
	  TFormula newFormula= new TFormula(modalKappa,
			       String.valueOf(chKappa),
			       agent,  
			       new TFormula(TFormula.unary,
			    		            String.valueOf(chNeg),
					       null,prop));
	  
	  TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
	  newDataNode.fAntecedents.add(0,newFormula);
	  newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	  newDataNode.fJustification=kPNJustification;
	  newDataNode.fWorld=selected.fWorld;

	  selected.fDead=true;

	  straightInsert(selected,newDataNode,null);
	};

void doNotKnow(TTreeDataNode selected,TFormula theFormula){
// this has neg kappa(agent,prop) and we want rho (agent, neg prop) 	
	
  TFormula agent=theFormula.fRLink.fLLink.copyFormula();
  TFormula prop=theFormula.fRLink.fRLink.copyFormula();
	
  TFormula newFormula= new TFormula(modalRho,
		       String.valueOf(chRho),
		       agent,  
		       new TFormula(TFormula.unary,
		    		            String.valueOf(chNeg),
				       null,prop));
  
  TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
  newDataNode.fAntecedents.add(0,newFormula);
  newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
  newDataNode.fJustification=kPNJustification;
  newDataNode.fWorld=selected.fWorld;

  selected.fDead=true;

  straightInsert(selected,newDataNode,null);
};

void doPossibleKnow(TTreeDataNode selected,TFormula theFormula){  //restricted on accessibility

	// rho(agent, prop)
	
	TFormula agent=theFormula.fLLink.copyFormula();
	
	  String newWorld = newWorldForBranches(selected.fTreeNode);

	  if (newWorld.equals(""))
	    return;

	 String oldWorld=selected.fWorld;

	TFormula access=fParser.makeAnEAccessRelation(agent.fInfo,oldWorld,newWorld);

	 TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
	 newDataNode.fAntecedents.add(0,access);
	 newDataNode.fFirstjustno=selected.fLineno;
	 newDataNode.fJustification=pRJustification;

	 selected.fDead=true;

	 straightInsert(selected,newDataNode,null);

	TFormula scope=theFormula.fRLink.copyFormula();

	newDataNode = new TTreeDataNode(fParser,fTreeModel);
	newDataNode.fAntecedents.add(0,scope);
	newDataNode.fFirstjustno=selected.fLineno;
	newDataNode.fJustification=pRJustification;
	newDataNode.fWorld=newWorld;

	selected.fDead=true;

	straightInsert(selected,newDataNode,null);

	};
	
	void doKnow(TTreeDataNode selected,TFormula theFormula){  //restricted on accessibility

		// kappa(agent, prop)
		
		TFormula agent=theFormula.fLLink.copyFormula();
		

		 String oldWorld=selected.fWorld;

		TFormula scope=theFormula.fRLink.copyFormula();

		TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
		newDataNode.fAntecedents.add(0,scope);
		newDataNode.fFirstjustno=selected.fLineno;
		newDataNode.fJustification=kTRJustification;
		newDataNode.fWorld=oldWorld;

		selected.fDead=true;

		straightInsert(selected,newDataNode,null);

		};
		
		void doTRKR(TTreeDataNode selected,TFormula theFormula){  //restricted on accessibility

			// kappa(x, kappa(y, prop))  gives us kappa(x, prop)
			
			if (theFormula!=null&&
				TParser.isModalKappa(theFormula)&&
				TParser.isModalKappa(theFormula.fRLink)){
			
			TFormula agent=theFormula.fLLink.copyFormula();
			TFormula scope=theFormula.fRLink.fRLink.copyFormula();
	
			 String oldWorld=selected.fWorld;

			TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
			newDataNode.fAntecedents.add(0,new TFormula(
											modalKappa,
											String.valueOf(chKappa),
					                     agent,
					                     scope));
			newDataNode.fFirstjustno=selected.fLineno;
			newDataNode.fJustification=trKRJustification;
			newDataNode.fWorld=oldWorld;

			selected.fDead=true;

			straightInsert(selected,newDataNode,null);

			}	
		}
		

/**************** Epistemic Rules *************************/

void doEquivv(TTreeDataNode selected,TFormula theFormula){

   DefaultMutableTreeNode aNode= selected.fTreeNode;

TFormula leftFormula=theFormula.fLLink.copyFormula();

 TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
 leftDataNode.fAntecedents.add(0,leftFormula);
 leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
 leftDataNode.fJustification=equivDJustification;

 TFormula left2Formula=theFormula.fRLink.copyFormula();

 TTreeDataNode left2DataNode = new TTreeDataNode(fParser,fTreeModel);
 left2DataNode.fAntecedents.add(0,left2Formula);
 left2DataNode.fFirstjustno=selected.fLineno;left2DataNode.fWorld=selected.fWorld;
 left2DataNode.fJustification=equivDJustification;

TFormula rightFormula=theFormula.fLLink.copyFormula();

 TFormula newFormula = new TFormula();

newFormula.fKind = TFormula.unary;
newFormula.fInfo = String.valueOf(Symbols.chNeg);
newFormula.fRLink = rightFormula;

rightFormula= newFormula;                       //not A


 TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
 rightDataNode.fAntecedents.add(0,rightFormula);
 rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
 rightDataNode.fJustification=equivDJustification;

 TFormula right2Formula=theFormula.fRLink.copyFormula();

newFormula = new TFormula();

newFormula.fKind = TFormula.unary;
newFormula.fInfo = String.valueOf(Symbols.chNeg);
newFormula.fRLink = right2Formula;

right2Formula= newFormula;                       //not B


 TTreeDataNode right2DataNode = new TTreeDataNode(fParser,fTreeModel);
 right2DataNode.fAntecedents.add(0,right2Formula);
 right2DataNode.fFirstjustno=selected.fLineno;right2DataNode.fWorld=selected.fWorld;
 right2DataNode.fJustification=equivDJustification;


 selected.fDead=true;

  splitInsertTwo(selected,leftDataNode,left2DataNode,
                 rightDataNode,right2DataNode);
 }



TFormula newConstantForBranches(DefaultMutableTreeNode target){  //InBranchesContaining target

   String constants="";
   ArrayList formulas=new ArrayList();

   Enumeration breadthFirst = fTreeDataRoot.fTreeNode.breadthFirstEnumeration();

   DefaultMutableTreeNode next = (DefaultMutableTreeNode) (breadthFirst.nextElement());

   TTreeDataNode searchNode=null;

while (next!=null){

  if ((next.getUserObject() instanceof TTreeDataNode)&&
      (next==target||
      next.isNodeAncestor(target)||
      next.isNodeDescendant(target))){
   searchNode=(TTreeDataNode)(next.getUserObject());
   TFormula formula=null;
   if (searchNode.fAntecedents.size()>0){
     formula = (TFormula) (searchNode.fAntecedents.get(0));
     formulas.add(formula);
   }
}
 if (breadthFirst.hasMoreElements()) {
  next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
}
else
  next=null;
}
return
     TParser.newConstant(formulas, null);
}


String newWorldForBranches(DefaultMutableTreeNode target){  //InBranchesContaining target

     String worlds="";

     Enumeration breadthFirst = fTreeDataRoot.fTreeNode.breadthFirstEnumeration();

     DefaultMutableTreeNode next = (DefaultMutableTreeNode) (breadthFirst.nextElement());

     TTreeDataNode searchNode=null;

  while (next!=null){

    if ((next.getUserObject() instanceof TTreeDataNode)&&
        (next==target||
        next.isNodeAncestor(target)||
        next.isNodeDescendant(target))){
     searchNode=(TTreeDataNode)(next.getUserObject());
     worlds+=searchNode.fWorld;    //will give us duplicates
  }
   if (breadthFirst.hasMoreElements()) {
    next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
  }
  else
    next=null;
  }


/*  //we will return the first world not in this list

  for(int i=0;i<fWorlds.length();i++){
    if (worlds.indexOf(fWorlds.substring(i,i+1))<0)  //not there
      return
        fWorlds.substring(i,i+1);
  }
*/

return
    fParser.firstNewWorld(worlds);

//  return "";
}



 void doExi(TTreeDataNode selected,TFormula theFormula){

   TFormula newConstant = newConstantForBranches(selected.fTreeNode);

   if (newConstant==null)
     return;

  TFormula variForm=theFormula.quantVarForm();
  TFormula scope=theFormula.fRLink.copyFormula();
  scope.subTermVar(scope,newConstant,variForm);

  TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
  newDataNode.fAntecedents.add(0,scope);
  newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
  newDataNode.fJustification=exiDJustification;

  selected.fDead=true;

  straightInsert(selected,newDataNode,null);
};

void doUnique(TTreeDataNode selected,TFormula theFormula){

	   TFormula newConstant = newConstantForBranches(selected.fTreeNode);

	   if (newConstant==null)
	     return; //dfasd
	   	  
/*
 String firstVariables=variablesInFormula(first);
         String secondVariables=variablesInFormula(second);

         char newVar =nthNewVariable(1, firstVariables+secondVariables);	  
 */
	   // Fa & allxally(Fx&Fy->x=y)

		  TFormula firstVar=theFormula.quantVarForm();
		  TFormula Fx=theFormula.fRLink.copyFormula();
		  TFormula Fa=Fx.copyFormula();
		  Fa.subTermVar(Fa,newConstant,firstVar);
		  
		  Set <String> oldVariables =fParser.variablesInFormula(theFormula);
		  String secondVarStr= fParser.nthNewVariable(1,oldVariables);
		  
		  if(secondVarStr.equals(""))
			  return;
		  TFormula secondVar =new TFormula(
			  			variable,
			  			secondVarStr,
			  			null,
			  			null);
  
		  TFormula Fy=Fx.copyFormula();
		  Fy.subTermVar(Fy,secondVar,firstVar);
	  
	  TFormula uniForm = new TFormula(quantifier,
			  String.valueOf(chUniquant),
			  firstVar,
			  new TFormula(
			  			quantifier,
			  			String.valueOf(chUniquant),
			  			secondVar,
			  			new TFormula(
					  			binary,
					  			String.valueOf(chImplic),
					  			new TFormula(
							  			binary,
							  			String.valueOf(chAnd),
							  			Fx,
							  			Fy),
							  			TFormula.equateTerms(firstVar.copyFormula(),secondVar.copyFormula())
							  			)
			  			));

	  //uniForm.fRLink = TFormula.equateTerms(variablenode,variablenode.copyFormula());
/*
	  TFormula newFormula = new TFormula();

	  newFormula.fKind = TFormula.binary;
	  newFormula.fInfo = String.valueOf(Symbols.chAnd);
	  newFormula.fLLink = Fa;
	  newFormula.fRLink = uniForm;

	  TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
	  newDataNode.fAntecedents.add(0,newFormula);
	  newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	  newDataNode.fJustification=uniqueDJustification;
	  
	*/  
	  /*******
	   * 
	   * 
	   * 
	   */
	  
	   TTreeDataNode firstDataNode = new TTreeDataNode(fParser,fTreeModel);
	   firstDataNode.fAntecedents.add(0,Fa);
	   firstDataNode.fFirstjustno=selected.fLineno;firstDataNode.fWorld=selected.fWorld;
	   firstDataNode.fJustification=uniqueDJustification;

	    TTreeDataNode secondDataNode = new TTreeDataNode(fParser,fTreeModel);
	    secondDataNode.fAntecedents.add(0,uniForm);
	    secondDataNode.fFirstjustno=selected.fLineno;secondDataNode.fWorld=selected.fWorld;
	    secondDataNode.fJustification=uniqueDJustification;


	    selected.fDead=true;

	    straightInsert(selected,firstDataNode,secondDataNode);

	  
	  
/*
	  selected.fDead=true;

	  straightInsert(selected,newDataNode,null); */
	}

void doNegUnique(TTreeDataNode selected,TFormula theFormula){
	
	// notEx!Fx

	   TFormula newConstant = newConstantForBranches(selected.fTreeNode);

	   if (newConstant==null)
	     return; 
	   
	   // Fa & allxally(Fx&Fy->x=y)

		  TFormula firstVar=theFormula.fRLink.quantVarForm();
		  TFormula Fx=theFormula.fRLink.fRLink.copyFormula();
		  TFormula notFx=new TFormula(
		  			unary,
		  			String.valueOf(chNeg),
		  			null,
		  			Fx);
		  TFormula allxnotFx=new TFormula(
		  			quantifier,
		  			String.valueOf(chUniquant),
		  			firstVar.copyFormula(),
		  			notFx);
		  
		  
		  TFormula Fa=Fx.copyFormula();
		  Fa.subTermVar(Fa,newConstant,firstVar);
		  
		  Set <String> oldVariables =fParser.variablesInFormula(theFormula);
		  String secondVarStr= fParser.nthNewVariable(1,oldVariables);
		  
		  if(secondVarStr.equals(""))
			  return;
		  TFormula secondVar =new TFormula(
			  			variable,
			  			secondVarStr,
			  			null,
			  			null);

		  TFormula Fy=Fx.copyFormula();
		  Fy.subTermVar(Fy,secondVar,firstVar);
	  
	  TFormula exiForm = new TFormula(quantifier,
			  String.valueOf(chExiquant),
			  firstVar,
			  new TFormula(
			  			quantifier,
			  			String.valueOf(chExiquant),
			  			secondVar,
			  			new TFormula(
					  			binary,
					  			String.valueOf(chAnd),
					  			new TFormula(
							  			binary,
							  			String.valueOf(chAnd),
							  			Fx,
							  			Fy),
							  			
							  			
							  			new TFormula(
									  			unary,
									  			String.valueOf(chNeg),
									  			null,
									  			TFormula.equateTerms(firstVar.copyFormula(),secondVar.copyFormula()))						  			
							  			)
			  			));

  
	   TTreeDataNode firstDataNode = new TTreeDataNode(fParser,fTreeModel);
	   firstDataNode.fAntecedents.add(0,allxnotFx);
	   firstDataNode.fFirstjustno=selected.fLineno;firstDataNode.fWorld=selected.fWorld;
	   firstDataNode.fJustification=negUniqueDJustification;

	    TTreeDataNode secondDataNode = new TTreeDataNode(fParser,fTreeModel);
	    secondDataNode.fAntecedents.add(0,exiForm);
	    secondDataNode.fFirstjustno=selected.fLineno;secondDataNode.fWorld=selected.fWorld;
	    secondDataNode.fJustification=negUniqueDJustification;


	    selected.fDead=true;

	    splitInsert(selected,firstDataNode,secondDataNode);

	};


/************* Identity **************/


void doII(TTreeDataNode selected){

  //we require selected here only to pick out the desired branch

  TFormula variablenode= new TFormula();

  variablenode.fKind = variable;
  variablenode.fInfo = "x";

  TFormula uniForm = new TFormula();

  uniForm.fKind = quantifier;
  uniForm.fInfo = String.valueOf(chUniquant);
  uniForm.fLLink = variablenode;
  uniForm.fRLink = TFormula.equateTerms(variablenode,variablenode.copyFormula());

// (Allx)(x=x)

  TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
  newDataNode.fAntecedents.add(0,uniForm);
  newDataNode.fJustification=identityIJustification;
  newDataNode.fWorld = selected.fWorld;

  straightInsert(selected,newDataNode,null);
  }

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

       fParent.fTermsToTreat[fParent.fNumTreated].fInfo= Symbols.chInsertMarker+
                                                    fParent.fTermsToTreat[fParent.fNumTreated].fInfo;


         String message= fParser.writeFormulaToString(fParent.fCopy);


         fParent.fText.setText(message);

         fParent.fText.requestFocus();

     }
     else{                                        //  last one, return to parent

      JButton defaultButton = new JButton(fParent);

      JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
      TProofInputPanel inputPane = new TProofInputPanel("Doing =D-- Stage3,"+
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


TTreeDataNode fFirstline=null;
TTreeDataNode fSecondline=null;
TFormula fFirstFormula;
TFormula fSecondFormula;
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


public IEAction(JTextField text, String label,TTreeDataNode firstline,TTreeDataNode secondline,
                    TFormula firstFormula, TFormula secondFormula){
   putValue(NAME, label);

   fText = text;
   fFirstline = firstline;
   fSecondline = secondline;
   fFirstFormula=firstFormula;
   fSecondFormula=secondFormula;

   fAlpha = fSecondFormula.firstTerm(); // alpha=gamma
   fGamma = fSecondFormula.secondTerm();

   fCopy = fFirstFormula.copyFormula(); //??

   fNumAlpha = fFirstFormula.numOfFreeOccurrences(fAlpha);

   if (fNumAlpha > 0) {
     fAlphas = new TFormula[fNumAlpha];    // create an array of the actual terms in the copy that we will do surgery on

     for (int i = 0; i < fNumAlpha; i++) { // initialize
       fAlphas[i] = fCopy.depthFirstNthOccurence(fAlpha, i + 1); // one uses zero based index, other 1 based
     }
   }

 fNumGamma = fFirstFormula.numOfFreeOccurrences(fGamma);

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

void subFormCheck(){

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

  int occurences =fFirstFormula.numOfFreeOccurrences(fAlpha);

}

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

wellformed= fParser.wffCheck(root, /*dummy,*/ aReader);  // it can never be illformed since we put a well formed one there

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

    if (fCopy.formulaInList(fFirstline.getInstantiations())){
    	bugAlert("Identity Elimination, Observation.","You have created the result before.");
    }
    else{
	
	
	if (!fCopy.equalFormulas(fCopy, fFirstFormula)) {
    TTreeDataNode newDataNode = new TTreeDataNode(fParser, fTreeModel);
    newDataNode.fAntecedents.add(0, fCopy);
    newDataNode.fFirstjustno = fFirstline.fLineno;
    newDataNode.fSecondjustno = fSecondline.fLineno;
    newDataNode.fJustification = identityDJustification;
    newDataNode.fWorld=fFirstline.fWorld; //the worlds are the same for both lines

    // selected.fDead=true;    don't make it dead
    
    fFirstline.addToInstantiations(fCopy.copyFormula());  // don't let them do the same twice

    straightInsert(fFirstline, newDataNode, null);

    removeInputPane();
  }
  else{
    removeInputPane();
    bugAlert("=D", "You need to substitute for at least one occurrence.");
    deSelectAll();
  }
    }
}

private void askAboutAlpha(){
String aString;
String message;

if (fGammaOnly||fNumAlpha == 0) { // we just go on to gamma
  fStage = 3;
  askAboutGamma();
}

else {
  if (fNumAlpha > 0) {

    fAlphas[0].fInfo = Symbols.chInsertMarker + fAlphas[0].fInfo;
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
        "Doing =D-- Stage1, substitute for this occurrence of left term?",
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


     fGammas[0].fInfo= Symbols.chInsertMarker+ fGammas[0].fInfo;
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
  TProofInputPanel inputPane = new TProofInputPanel("Doing =D-- Stage2, substitute for this occurrence of right term?", fText, buttons);


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

void launchIEAction(TTreeDataNode firstline,TTreeDataNode secondline,
                    TFormula firstFormula, TFormula secondFormula){

TFormula alpha=secondFormula.firstTerm();
TFormula gamma=secondFormula.secondTerm();

String captured=capturePossible(alpha,   // alpha=gamma
                                gamma,
                                firstFormula);


if (!captured.equals("")){

  bugAlert("Problems with free and bound variables (remedy: rewrite bound variable)",
      "The variable "+ captured + " occurs in the identity and is bound in "
      + fParser.writeFormulaToString(firstFormula));

}
else{


     // now we want to move into the substiuting bit



     JTextField text = new JTextField("Starting =D"); ////// HERE
     text.selectAll();

     IEAction launchAction =new IEAction(text, "Go", firstline,
         secondline,firstFormula,secondFormula);

     JButton defaultButton = new JButton(launchAction);

//     JButton defaultButton = new JButton(new IEAction(text, "Go", firstline,
//         secondline));

     JButton[] buttons = {
         new JButton(new CancelAction()), defaultButton}; // put cancel on left
     TProofInputPanel inputPane = new TProofInputPanel(
         "Doing Identity Substitution", text, buttons);

     addInputPane(inputPane);

     inputPane.getRootPane().setDefaultButton(defaultButton);
     fInputPane.setVisible(true); // need this
     text.requestFocus(); // so selected text shows  */

     launchAction.start();
   }
}

class FirstSecondAction extends AbstractAction{
  boolean fFirst=true;
  TTreeDataNode fFirstline;
  TTreeDataNode fSecondline;
  TFormula fFirstFormula;
  TFormula fSecondFormula;

  FirstSecondAction(boolean isFirst,TTreeDataNode firstline, TTreeDataNode secondline,
                    TFormula firstFormula,TFormula secondFormula){
    if (isFirst)
       putValue(NAME, "First");
     else
       putValue(NAME, "Second");



     fFirst=isFirst;
     fFirstline=firstline;
     fSecondline=secondline;
     fFirstFormula=firstFormula;
     fSecondFormula=secondFormula;
  }

  public void actionPerformed(ActionEvent ae){

    if (!fFirst){       // if they want to subs in first, fine; otherwise we have to swap
      TTreeDataNode temp = fFirstline;
      fFirstline = fSecondline;
      fSecondline = temp; // now the secondline is the identity

      {TFormula tempFormula=fFirstFormula;
        fFirstFormula=fSecondFormula;
        fSecondFormula=tempFormula;
      }

    }


    removeInputPane();

    launchIEAction(fFirstline,fSecondline,fFirstFormula,fSecondFormula);

  }

}


private void orderForSwap(TTreeDataNode firstSelection, TTreeDataNode secondSelection,
                          TFormula firstFormula, TFormula secondFormula){
/*{this determines which we are going to subs in-- they could both be identities}
// this launches or puts up a prelim dialog which itself launches
we want the identity as the second line and the formula it is substituted in as the first line */

   int dispatcher=0;
   int inFirst=0;
   int inSecond=0;

   if (fParser.isEquality(firstFormula))
     inSecond = (secondFormula).numOfFreeOccurrences(firstFormula.firstTerm()) +
           (secondFormula).numOfFreeOccurrences(firstFormula.secondTerm());

   if (fParser.isEquality(secondFormula))
     inFirst = (firstFormula).numOfFreeOccurrences(secondFormula.firstTerm()) +
           (firstFormula).numOfFreeOccurrences(secondFormula.secondTerm());

   if ((inFirst+inSecond)==0)
     return;                  //if neither appears in the other no substitution is possible


   if (fParser.isEquality(firstFormula)){
     if (!fParser.isEquality(secondFormula))
       dispatcher=2;
     else
       dispatcher=3;    // both
   }
   else
     dispatcher=1;     //first not, second is

   switch (dispatcher){
     case 0: break;   // neither an identity cannot happen because orderForSwap called only if at least one is
     case 1:          // what we want first not identity second is
       launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
       break;
     case 2: {        // wrong way round so we swap
       {TTreeDataNode temp=firstSelection;
         firstSelection = secondSelection;
         secondSelection = temp; // now the secondSelection is the identity
       }
       {TFormula temp=firstFormula;
       firstFormula=secondFormula;
       secondFormula=temp;
       }

       launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
       break;
     }
     case 3: {               // both identities

       /*{now, if neither of the second terms appear in the first, we want to subs in the second}
          {if neeither of the first terms appear in the second, we want to subs in the first}
        {otherwise we have to ask} Don't fully understand the logic of this Jan06
        oh, I suppose it is this a=b and f(a)=c, can only subs in second etc.*/

       if (inFirst == 0) {
         {TTreeDataNode temp = firstSelection;
           firstSelection = secondSelection;
           secondSelection = temp; // now the secondSelection is the identity
         }
         {TFormula temp=firstFormula;
           firstFormula = secondFormula;
           secondFormula = temp;
         }
         launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
       }
       else {
         if (inSecond == 0) { // leave them as they are, both identities some in first none in second
           launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
         }
         else { // we ask

           TProofInputPanel inputPane;
           JTextField text = new JTextField(
               "Do you wish to substitute in the first or in the second?");

           text.setDragEnabled(true);
           text.selectAll();

           boolean isFirst = true;

           JButton firstButton = new JButton(new FirstSecondAction(isFirst,
               firstSelection, secondSelection,firstFormula,secondFormula));
           JButton secondButton = new JButton(new FirstSecondAction(!isFirst,
               firstSelection, secondSelection,firstFormula,secondFormula));

           JButton[] buttons = {
               new JButton(new CancelAction()), firstButton, secondButton}; // put cancel on left
           inputPane = new TProofInputPanel("Doing Identity Substitution", text,
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


boolean isIEPossible(TTreeDataNode [] selectedNodes){
  if ((selectedNodes!=null)&&(selectedNodes.length==2)){
    TTreeDataNode firstSelected = selectedNodes[0];
    TTreeDataNode secondSelected = selectedNodes[1];

    if(!firstSelected.fWorld.equals(secondSelected.fWorld))
      return
          false;


    TFormula firstFormula = null;
    TFormula secondFormula = null;

  if ((firstSelected.fAntecedents != null &&
      firstSelected.fAntecedents.size() == 1)&&
     (secondSelected.fAntecedents != null &&
      secondSelected.fAntecedents.size() == 1)) {
    firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
    secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

    if ((fParser.isEquality(firstFormula) &&
        firstFormula.firstTerm().isClosedTerm() &&
        firstFormula.secondTerm().isClosedTerm())

        ||
        (fParser.isEquality(secondFormula)) &&
        secondFormula.firstTerm().isClosedTerm() &&
        secondFormula.secondTerm().isClosedTerm())

      return
          true;
  }

}
return
    false;

}


public void doIE (TTreeDataNode firstSelected,TTreeDataNode secondSelected,
             TFormula firstFormula,TFormula secondFormula){


  if ((fParser.isEquality(firstFormula) &&
      firstFormula.firstTerm().isClosedTerm() &&
      firstFormula.secondTerm().isClosedTerm())

      ||
      (fParser.isEquality(secondFormula)) &&
      secondFormula.firstTerm().isClosedTerm() &&
      secondFormula.secondTerm().isClosedTerm())





    {
           orderForSwap(firstSelected, secondSelected,firstFormula,secondFormula); // this launches or puts up a prelim dialog which launches
        //{we allow substitution is any formula provided no variable in the equality is bound in the formula}
    }
  }



void doImplic(TTreeDataNode selected,TFormula theFormula){

 TFormula leftFormula=theFormula.fLLink.copyFormula();

 TFormula newFormula = new TFormula();

newFormula.fKind = TFormula.unary;
newFormula.fInfo = String.valueOf(Symbols.chNeg);
newFormula.fRLink = leftFormula;

 leftFormula= newFormula;                       //not A


TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
leftDataNode.fAntecedents.add(0,leftFormula);
leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
leftDataNode.fJustification=implicDJustification;

 TFormula rightFormula=theFormula.fRLink.copyFormula();

TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
rightDataNode.fAntecedents.add(0,rightFormula);
rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
rightDataNode.fJustification=implicDJustification;

selected.fDead=true;

splitInsert(selected,leftDataNode,rightDataNode);

}


void doNegAnd(TTreeDataNode selected,TFormula theFormula){

   TFormula leftFormula=theFormula.fRLink.fLLink.copyFormula();

   TFormula newFormula = new TFormula();

  newFormula.fKind = TFormula.unary;
  newFormula.fInfo = String.valueOf(Symbols.chNeg);
  newFormula.fRLink = leftFormula;

   leftFormula= newFormula;                       //not A


  TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
  leftDataNode.fAntecedents.add(0,leftFormula);
  leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
  leftDataNode.fJustification=negAndDJustification;

   newFormula = new TFormula();
   TFormula rightFormula=theFormula.fRLink.fRLink.copyFormula();

   newFormula.fKind = TFormula.unary;
newFormula.fInfo = String.valueOf(Symbols.chNeg);
newFormula.fRLink = rightFormula;

 rightFormula= newFormula;                       // not B


  TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
  rightDataNode.fAntecedents.add(0,rightFormula);
  rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
  rightDataNode.fJustification=negAndDJustification;

  selected.fDead=true;

  splitInsert(selected,leftDataNode,rightDataNode);

}

void doNegArrow(TTreeDataNode selected,TFormula theFormula){
  TFormula leftFormula=theFormula.fRLink.fLLink.copyFormula();

  TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
  leftDataNode.fAntecedents.add(0,leftFormula);
  leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
  leftDataNode.fJustification=negArrowDJustification;

  TFormula rightFormula=theFormula.fRLink.fRLink.copyFormula();

  TFormula newFormula = new TFormula();

newFormula.fKind = TFormula.unary;
newFormula.fInfo = String.valueOf(Symbols.chNeg);
newFormula.fRLink = rightFormula;

rightFormula= newFormula;                       //not B


  TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
  rightDataNode.fAntecedents.add(0,rightFormula);
  rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
  rightDataNode.fJustification=negArrowDJustification;


  selected.fDead=true;

  straightInsert(selected,leftDataNode,rightDataNode);
  };


void doNegEquiv(TTreeDataNode selected,TFormula theFormula){

  TFormula A=theFormula.fRLink.fLLink.copyFormula(); //A
  TFormula B=theFormula.fRLink.fRLink.copyFormula(); //B

  TFormula notA = new TFormula(TFormula.unary,
                         String.valueOf(chNeg), null, A.copyFormula());
  TFormula notB = new TFormula(TFormula.unary,
                         String.valueOf(chNeg), null, B.copyFormula());

   TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
   leftDataNode.fAntecedents.add(0,A);
   leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
   leftDataNode.fJustification=negEquivDJustification;


   TTreeDataNode left2DataNode = new TTreeDataNode(fParser,fTreeModel);
   left2DataNode.fAntecedents.add(0,notB);
   left2DataNode.fFirstjustno=selected.fLineno;left2DataNode.fWorld=selected.fWorld;
   left2DataNode.fJustification=negEquivDJustification;


   TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
   rightDataNode.fAntecedents.add(0,notA);
   rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
   rightDataNode.fJustification=negEquivDJustification;


   TTreeDataNode right2DataNode = new TTreeDataNode(fParser,fTreeModel);
   right2DataNode.fAntecedents.add(0,B);
   right2DataNode.fFirstjustno=selected.fLineno;right2DataNode.fWorld=selected.fWorld;
   right2DataNode.fJustification=negEquivDJustification;


   selected.fDead=true;

    splitInsertTwo(selected,leftDataNode,left2DataNode,
                   rightDataNode,right2DataNode);
 }

  void doNegExi(TTreeDataNode selected,TFormula theFormula){  //not is to all not
    TFormula uniFormula =theFormula.fRLink.copyFormula(); // the un-negated Exi
    uniFormula.fInfo=String.valueOf(chUniquant); //changed exiquant to uniquant
    uniFormula.fRLink=new TFormula(TFormula.unary,
                               String.valueOf(Symbols.chNeg),
                               null,
                               uniFormula.fRLink);   //negated scope

    TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
    newDataNode.fAntecedents.add(0,uniFormula);
    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
    newDataNode.fJustification=negExiDJustification;

    selected.fDead=true;

    straightInsert(selected,newDataNode,null);
  };

  void doNegUni(TTreeDataNode selected,TFormula theFormula){  //not all to is not
    TFormula exiFormula =theFormula.fRLink.copyFormula(); // the un-negated Uni
    exiFormula.fInfo=String.valueOf(chExiquant); //changed Uniquant to exiquant
    exiFormula.fRLink=new TFormula(TFormula.unary,
                               String.valueOf(Symbols.chNeg),
                               null,
                               exiFormula.fRLink);   //negated scope

    TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
    newDataNode.fAntecedents.add(0,exiFormula);
    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
    newDataNode.fJustification=negUniDJustification;

    selected.fDead=true;

    straightInsert(selected,newDataNode,null);
  };

  void doNore(TTreeDataNode selected,TFormula theFormula){
    TFormula leftFormula=theFormula.fRLink.fLLink.copyFormula();

    TFormula newFormula = new TFormula();

    newFormula.fKind = TFormula.unary;
    newFormula.fInfo = String.valueOf(Symbols.chNeg);
    newFormula.fRLink = leftFormula;

    leftFormula=newFormula;

    TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
    leftDataNode.fAntecedents.add(0,leftFormula);
    leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
    leftDataNode.fJustification=noreDJustification;

    TFormula rightFormula=theFormula.fRLink.fRLink.copyFormula();

    newFormula = new TFormula();

    newFormula.fKind = TFormula.unary;
    newFormula.fInfo = String.valueOf(Symbols.chNeg);
    newFormula.fRLink = rightFormula;

    rightFormula=newFormula;

    TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
    rightDataNode.fAntecedents.add(0,rightFormula);
    rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
    rightDataNode.fJustification=noreDJustification;


    selected.fDead=true;

    straightInsert(selected,leftDataNode,rightDataNode);
  };

 void doOr(TTreeDataNode selected,TFormula theFormula){

 // int [][] selectedIndices= fTreeTableView.selectedIndices();   //we know there is only one selected

  DefaultMutableTreeNode aNode= selected.fTreeNode;



  TFormula leftFormula=theFormula.fLLink.copyFormula();

TTreeDataNode leftDataNode = new TTreeDataNode(fParser,fTreeModel);
leftDataNode.fAntecedents.add(0,leftFormula);
leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
leftDataNode.fJustification=orDJustification;

  TFormula rightFormula=theFormula.fRLink.copyFormula();

TTreeDataNode rightDataNode = new TTreeDataNode(fParser,fTreeModel);
rightDataNode.fAntecedents.add(0,rightFormula);
rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
rightDataNode.fJustification=orDJustification;

selected.fDead=true;

 splitInsert(selected,leftDataNode,rightDataNode);
 // selected.straightInsert(leftDataNode,rightDataNode);

//fTreeTableView.addColumn(new TableColumn());fTreeTableView.addColumn(new TableColumn());
//fTreeTableModel.treeChanged(TTreeTableModel.COLCHANGE,leftDataNode);              //need a listener for this

//deSelectAll();

 }


/************************ UI Action****************/
 
 /*Don't want to instantiate to the same formula twice*/

public class UIAction extends AbstractAction{
   JTextComponent fText;
   TTreeDataNode fSelected=null;
   TFormula fFormula=null;

   public UIAction(JTextComponent text, String label, TTreeDataNode selected, TFormula formula){
     putValue(NAME, label);

     fText=text;
     fSelected=selected;
     fFormula=formula;
   }

   public void actionPerformed(ActionEvent ae){

     //boolean useFilter = true;
     //ArrayList dummy = new ArrayList();

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
                    	  
                    	 
                    	  

                        TFormula scope = fFormula.fRLink.copyFormula();
                        scope.subTermVar(scope,term,fFormula.quantVarForm());
                        
                        if (term.formulaInList(fSelected.getInstantiations())){
                        	bugAlert("Universal Decomposition, Observation.","You have already made this instantiation.");
                        }
                        else{

                          TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
                          newDataNode.fAntecedents.add(0,scope);
                          newDataNode.fFirstjustno=fSelected.fLineno;
                          newDataNode.fJustification= UDJustification;
                          newDataNode.fWorld= fSelected.fWorld;

                         // selected.fDead=true;    don't make it dead

                          fSelected.addToInstantiations(term.copyFormula());

                          straightInsert(fSelected,newDataNode,null);

                          removeInputPane();
                        }

                      }

                  }

                }


              /************************ End of UI Action *********/

 void doUni(TTreeDataNode selected,TFormula theFormula){
   JButton defaultButton;
   TProofInputPanel inputPane;

   JTextField text = new JTextField("Constant, or closed term, to instantiate with?");
   text.selectAll();

   defaultButton = new JButton(new UIAction(text,"Go", selected, theFormula));

   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
   inputPane = new TProofInputPanel("Doing "+chUniquant+ "D", text, buttons);


   addInputPane(inputPane);
   inputPane.getRootPane().setDefaultButton(defaultButton);
   fInputPane.setVisible(true); // need this
   text.requestFocus();         // so selected text shows
};

/*
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
       inputPane = new TProofInputPanel("Doing UE", text, buttons);


       addInputPane(inputPane);

       inputPane.getRootPane().setDefaultButton(defaultButton);
       fInputPane.setVisible(true); // need this
       text.requestFocus();         // so selected text shows


     }


}


 */


	
	
	
	
/*************************************************************************/	


 void straightInsert(TTreeDataNode at, TTreeDataNode left, TTreeDataNode right){

   /*had a lot of trouble getting the update to display properly. Found, and fixed, the bug, Dec 23*/

   int depth = fTreeTableModel.getTreeDepth();

   at.straightInsert(left, right,fTreeDataRoot.fTreeNode,depth); //this updates the tree

   fTreeTableModel.updateCache();
   fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE,null);  //need/have a listener for this

  // fTreeTableView.resetWidths();

 //  fTreeTableView.doLayout();

 deSelectAll();
 tellListeners(new UndoableEditEvent(this,null));

 }

 void splitInsert(TTreeDataNode at, TTreeDataNode left, TTreeDataNode right){

 /*had a lot of trouble getting the update to display properly. Found, and fixed, the bug, Dec 23*/

   int depth = fTreeTableModel.getTreeDepth();

   at.splitInsert(left,right,fTreeDataRoot.fTreeNode,depth);                       //this updates the tree

  // int [] newWidths = fTreeTableView.calculateWidths(fTreeTableModel.getItsColumn(at.fTreeNode));  // column widths, uses oldCache

   fTreeTableModel.updateCache();                    // this updates the table data based on the tree

   fTreeTableModel.treeChanged(TTreeTableModel.COLCHANGE,null);              //need a listener for this

   fTreeTableView.resetWidths2(fTreeDataRoot);

   fTreeTableView.doLayout();

   deSelectAll();
   tellListeners(new UndoableEditEvent(this,null));
 }

 void splitInsertTwo(TTreeDataNode at, TTreeDataNode left,TTreeDataNode left2,
                     TTreeDataNode right,TTreeDataNode right2){

  int depth = fTreeTableModel.getTreeDepth();

  at.splitInsertTwo(left,left2,right,right2,fTreeDataRoot.fTreeNode,depth);                       //this updates the tree

  fTreeTableModel.updateCache();                    // this updates the table data based on the tree

  fTreeTableModel.treeChanged(TTreeTableModel.COLCHANGE,null);              //need a listener for this

  fTreeTableView.resetWidths2(fTreeDataRoot);

  fTreeTableView.doLayout();

  deSelectAll();
  tellListeners(new UndoableEditEvent(this,null));
}


 /**************************** End of Extension rules *****************************/


     class TTreePanel_extendMenuItem_actionAdapter implements java.awt.event.ActionListener {
       TTreePanel adaptee;

       TTreePanel_extendMenuItem_actionAdapter(TTreePanel adaptee) {
         this.adaptee = adaptee;
       }
       public void actionPerformed(ActionEvent e) {
         adaptee.extendMenuItem_actionPerformed(e);
       }
}

   
     
     class TTreePanel_identityMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TTreePanel adaptee;

  TTreePanel_identityMenuItem_actionAdapter(TTreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.identityMenuItem_actionPerformed(e);
  }
}


class TTreePanel_isClosedMenuItem_actionAdapter implements java.awt.event.ActionListener {
TTreePanel adaptee;

TTreePanel_isClosedMenuItem_actionAdapter(TTreePanel adaptee) {
this.adaptee = adaptee;
}
public void actionPerformed(ActionEvent e) {
adaptee.isClosedMenuItem_actionPerformed(e);
}
}

class TTreePanel_isCompleteMenuItem_actionAdapter implements java.awt.event.ActionListener {
TTreePanel adaptee;

TTreePanel_isCompleteMenuItem_actionAdapter(TTreePanel adaptee) {
this.adaptee = adaptee;
}
public void actionPerformed(ActionEvent e) {
adaptee.isCompleteMenuItem_actionPerformed(e);
}
}

class TTreePanel_refMenuItem_actionAdapter implements java.awt.event.ActionListener {
	TTreePanel adaptee;

	TTreePanel_refMenuItem_actionAdapter(TTreePanel adaptee) {
	this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
	adaptee.refMenuItem_actionPerformed(e);
	}
	}

class TTreePanel_ruleSetMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TTreePanel adaptee;

  TTreePanel_ruleSetMenuItem_actionAdapter(TTreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ruleSetMenuItem_actionPerformed(e);
  }
}

class TTreePanel_symMenuItem_actionAdapter implements java.awt.event.ActionListener {
	TTreePanel adaptee;

	TTreePanel_symMenuItem_actionAdapter(TTreePanel adaptee) {
	this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
	adaptee.symMenuItem_actionPerformed(e);
	}
	}

class TTreePanel_transMenuItem_actionAdapter implements java.awt.event.ActionListener {
	TTreePanel adaptee;

	TTreePanel_transMenuItem_actionAdapter(TTreePanel adaptee) {
	this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
	adaptee.transMenuItem_actionPerformed(e);
	}
	}

class TTreePanel_startOverMenuItem_actionAdapter implements java.awt.event.ActionListener {
TTreePanel adaptee;

TTreePanel_startOverMenuItem_actionAdapter(TTreePanel adaptee) {
this.adaptee = adaptee;
}
public void actionPerformed(ActionEvent e) {
adaptee.startOverMenuItem_actionPerformed(e);
}
}


     class TTreePanel_closeMenuItem_actionAdapter implements java.awt.event.ActionListener {
       TTreePanel adaptee;

       TTreePanel_closeMenuItem_actionAdapter(TTreePanel adaptee) {
         this.adaptee = adaptee;
       }
       public void actionPerformed(ActionEvent e) {
         adaptee.closeMenuItem_actionPerformed(e);
       }
}


}


/*
try{ 
 new BaleOut(5);}
 catch(TimeOutException e){System.out.format("In Valid");
	}
 
 final Timer timer = new Timer();

 class RemindTask extends TimerTask {
 	
 	public int answer(){
 		return
 		notKnown;
 	}
	   
     public void run(){
     	
     	//try {
         System.out.format("Time's up!%n");
        
         timer.cancel(); //Terminate the timer thread
         
if (answer==-1)
	 answer();
     
     }
 }



 timer.schedule(new RemindTask(), 5*1000);


*/ 

/*

class TimeOutException extends Exception {}




public class BaleOut {
	    Timer timer;

	    public BaleOut(int seconds) throws TimeOutException {
	        timer = new Timer();
	        timer.schedule(new RemindTask(), seconds*1000);
		}
	    
	    class RemindTask extends TimerTask {
	    	
	    	void bale()  throws TimeOutException{
	    		
	    		 throw new TimeOutException();
	    		
	    	}
	    	
	        public void run(){
	        	
	        	//try {
	            System.out.format("Time's up!%n");
	           
	            timer.cancel(); //Terminate the timer thread
	            
	            try{
	            bale();}
	        	
	        	catch(TimeOutException e){System.out.format("Exception");
	      /*  	throw e;} 
	        }
	    }


/*
	    public static void main(String args[]) {
	        new Reminder(5);
	        System.out.format("Task scheduled.%n");
	    }
	    
	   
	}	 

*/




