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


/* read April 14*/

/* starting to read June 10 2006*/

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chComma;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chLSqBracket;
import static us.softoption.infrastructure.Symbols.chRSqBracket;

import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;  //2015
import static us.softoption.infrastructure.Symbols.chSmallRightBracket; //2015

import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import us.softoption.games.TConsistent;
import us.softoption.games.TInvalid;
import us.softoption.games.TMainConnective;
import us.softoption.games.TPredConsistent;
import us.softoption.games.TPredInvalid;
import us.softoption.games.TPredSatisfiable;
import us.softoption.games.TPredTruthTable;
import us.softoption.games.TProofQuiz;
import us.softoption.games.TRandomProof;
import us.softoption.games.TRandomProofPanel;
import us.softoption.games.TSatisfiable;
import us.softoption.games.TTruthTable;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TFlag;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.interpretation.TSemantics;
import us.softoption.interpretation.TTestDisplayTree;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TFormula;
// 2015 import us.softoption.parser.TGlobals;
import us.softoption.parser.TParser;
import us.softoption.parser.TPriestParser;
import us.softoption.proofs.TProofInputPanel;
import us.softoption.proofs.TProofListModel;
import us.softoption.proofs.TProofPanel;
import us.softoption.proofs.TProofTableModel;
import us.softoption.tree.TTreeTableModel;




/*
 1. Notice here that we have a TDeriverDocument, and the HTML editor itself comes with a Document (from Swing). So there
 are two 'documents'

 2. We use one file chooser

 JFileChooser(File currentDirectory): Creates a JFileChooser using the given File as the path
 JFileChooser(String currentDirectoryPath): Creates a JFileChooser using the given path

 By using the last two constructors listed above, you can set the initial directory. The following examples demonstrate how to do this in a Windows environment:

 �//with the string representation of the path
    ����JFileChooser fc = new JFileChooser("C:\\temp\\");
    ����fc.showOpenDialog(parentComponent)

 //using a file object as the directory rather than a String
    �� File file = new File("C:\\temp\\");
    �� JFileChooser fc = new JFileChooser(file);
    �� fc.showOpenDialog(parentComponent)

 */

/*Note. A browser needs to know its application and its document. The document needs to know
its 'Journal'(which often is its Browser). So there is a type of circular
initialization. Also the constructors may need to have some of the stuff available. Here is an
approach.
i) create a document using the () constructor
ii) create a browser using (thatDocument, itsApplication) constructor
iii) finally call the thatDocument.setJournal(thatBrowser) method to provide the refernce to the browswer

Probably do all this from the application. */


public class TBrowser extends JFrame implements TJournal {


  static JFileChooser fFileChooser = new JFileChooser();  //USE PREFERENCES
  static MyFileFilter fFileFilter = new MyFileFilter("lgc", "Logic Documents");
  static {
     fFileChooser.setFileFilter(fFileFilter);

    // Preferences userPreferences = Preferences.userNodeForPackage(TBrowser.class/*this.getClass()*/);
     String home=TPreferences.fHome;

     if (home!=null){
        String oldHome=""; // we are saving the path of last directory on exit
        fFileChooser.setCurrentDirectory(new File(oldHome+home));
    }
   }

  static boolean useFilter=true;
  static int fFileFormat=3;   //Feb 10

  static String[] fFontNames;
  static String[] fFontSizes;
  static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

  static {
     fFontNames = ge.getAvailableFontFamilyNames();
     fFontSizes = new String[] {"8", "9", "10", "11", "12", "14",
     "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};
   }



  static StyleContext  fStyleContext=new StyleContext();

  static Action fSelectAllAction=null;  // this editor kit action has to be shared

  static final int kLISPCmd=-1;
  static final int kMakeDrawingCmd=1;
  static final int kWriteDrawingCmd=2;

  public static Dimension fDefaultSize= new Dimension(820, 500);
  public static int fDefaultMargin=200;       // a parameter for setting the width of a proof



 private TDeriverDocument fDeriverDocument;

 private TDeriverApplication fApplication;


//menus

  JMenuBar jMenuBar1 = new JMenuBar();

  //file menu
  JMenu fileMenu = new JMenu();
  JMenuItem newBrowserMenuItem = new JMenuItem();
  JMenuItem newFileMenuItem = new JMenuItem();
  JMenuItem openFileMenuItem = new JMenuItem();
  JMenuItem saveMenuItem = new JMenuItem();
  JMenuItem saveAsMenuItem = new JMenuItem();
  JMenuItem closeBrowserMenuItem = new JMenuItem();
  JMenuItem quitMenuItem = new JMenuItem();

  //edit menu
  protected Action fCutAction=new HTMLEditorKit.CutAction();
  protected Action fCopyAction=new HTMLEditorKit.CopyAction();

  JMenuItem fontMenuItem = new JMenuItem();


  //html menu
  JMenu HTMLMenu = new JMenu();
  JMenuItem editTextMenuItem = new JRadioButtonMenuItem("Edit text");
  JMenuItem liveTextMenuItem = new JRadioButtonMenuItem("Live text");
  JMenuItem insertImageMenuItem = new JMenuItem();
  JMenuItem insertLinkMenuItem = new JMenuItem();
  JMenuItem editHTMLMenuItem = new JMenuItem();
  JMenuItem eKitMenuItem = new JMenuItem();

  //semantics menu
  JMenu semanticsMenu = new JMenu();
  JMenuItem trueMenuItem = new JMenuItem();
  JMenuItem satisfiableMenuItem = new JMenuItem();
  JMenuItem consistentMenuItem = new JMenuItem();
  JMenuItem endorseMenuItem = new JMenuItem();
  JMenuItem denyMenuItem = new JMenuItem();
  JMenuItem propMenuItem = new JRadioButtonMenuItem("Propositional Level");
  JMenuItem predMenuItem = new JRadioButtonMenuItem("Predicate Level");
  JMenuItem valuationMenuItem = new JMenuItem();
  JMenuItem interpretationMenuItem = new JMenuItem();
  JMenuItem propositionsMenuItem = new JMenuItem();
  JMenuItem symbolizeMenuItem = new JMenuItem();
  JMenuItem toEnglishMenuItem = new JMenuItem();

  //actions menu
  JMenu actionsMenu = new JMenu();
  JMenuItem parseMenuItem = new JMenuItem();
  JMenuItem startProofMenuItem = new JMenuItem();
  JMenuItem startTreeMenuItem = new JMenuItem();
  JMenuItem startLambdaMenuItem = new JMenuItem();
  JMenuItem evaluateMenuItem = new JMenuItem();
  JMenuItem doCommandMenuItem = new JMenuItem();
  JMenuItem tryMenuItem = new JMenuItem();
  JMenuItem decodeMenuItem = new JMenuItem();
  JMenuItem xORMenuItem = new JMenuItem();
  JMenuItem saveAsHTMLItem = new JMenuItem();

  // games menu
  JMenu jMenuGames = new JMenu();
  JMenu propGamesSubMenu = new JMenu();
  JMenu predGamesSubMenu = new JMenu();
  JMenu examsSubMenu; // document to supply  = new JMenu();

  JMenu quizzesSubMenu;  //we'll let the document supply this  = new JMenu();

  JMenuItem connectiveMenuItem = new JMenuItem();
  JMenuItem truthTableMenuItem = new JMenuItem();
  JMenuItem satisfiableGameMenuItem = new JMenuItem();
  JMenuItem consistentGameMenuItem = new JMenuItem();
  JMenuItem invalidGameMenuItem = new JMenuItem();

  JMenuItem predConnectiveMenuItem = new JMenuItem();
  JMenuItem predSatisfiableGameMenuItem = new JMenuItem();
  JMenuItem predSatisfiable2GameMenuItem = new JMenuItem();
  JMenuItem predConsistentGameMenuItem = new JMenuItem();
  JMenuItem predInvalidGameMenuItem = new JMenuItem();
  JMenuItem predTruthTableMenuItem = new JMenuItem();

  //JMenuItem temp7MenuItem=new JMenuItem();   //Games menu
  //JMenuItem temp8MenuItem=new JMenuItem();

  //help menu
  JMenu helpMenu = new JMenu();
  JMenuItem helpMenuAbout = new JMenuItem();
  JMenuItem preferencesMenuItem = new JMenuItem();

  JMenu simpleMenu = new JMenu();
 JMenuItem insertBreakMenuItem = new JMenuItem();
 JMenuItem insertRuleMenuItem = new JMenuItem();
 JMenuItem pageLayoutMenuItem = new JMenuItem();
 JMenuItem printMenuItem = new JMenuItem();
 JMenuItem openWebPageMenuItem = new JMenuItem();


////////////////

  JPanel contentPane;


  JLabel statusBar = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextPane fJournalPane = new JTextPane();

  boolean fJournalEditable=true;  // hypertext dead or alive

  JTextPane fPalette = new JTextPane();

 boolean fDebugMode=TConstants.DEBUG; // condtional compilation for extra menus.

   FontDialog fFontDialog = new FontDialog(this, fFontNames, fFontSizes);

  String fCurrFileName = null;  // Full path and filename. null means new/untitled.
  File fCurrentFile;

  Document fDocument;     // this is the Swing document that goes with the text


 HTMLEditorKit fEditorKit = null;

  AttributeSet fStyleAttributes;

  Hashtable fActions;
  JMenu fEditMenu = new JMenu();


  String fFontName = "";
  protected int fFontSize = 0;
  boolean fSkipUpdate;


  //undo helpers
   protected UndoAction fUndoAction = new UndoAction();
   protected RedoAction fRedoAction = new RedoAction();
   protected UndoManager fUndoManager = new UndoManager();


   // find and replace

   FindDialog fFindDialog=null;

   Action fFindAction = new AbstractAction("Find...",  new ImageIcon("Find16.gif")) {
     public void actionPerformed(ActionEvent e) {
       if (fFindDialog==null)
         fFindDialog = new FindDialog(TBrowser.this,fJournalPane, 0);
       else
         fFindDialog.setSelectedIndex(0);
       fFindDialog.setVisible(true); } };


   Action fReplaceAction = new AbstractAction("Replace...",new ImageIcon("Replace16.gif")) {
     public void actionPerformed(ActionEvent e) {
       if (fFindDialog==null)
          fFindDialog = new FindDialog(TBrowser.this,fJournalPane, 1);
       else
          fFindDialog.setSelectedIndex(1);
       fFindDialog.setVisible(true);}};


  JTabbedPane fRightTabbedPane = new JTabbedPane();
  JSplitPane journalProofSplitPane = new JSplitPane();     // this is the LR one between the journal and the proof

  JSplitPane paletteTextSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT); // between the palette and the text in the journal

  JScrollPane fShapeScroller; // the tab for the interpretation drawing


  boolean fBadXML=false;


//boolean fDefaultReadFromClipboard=true;  // if there is no selected text  now use TPreferences.fReadFromClipboard

  Thread fSatisfiableThread=null;
  boolean fStopSatisTest;


/********************** CONSTRUCTORS ***************************/

public TBrowser(TDeriverDocument itsDocument,TDeriverApplication itsApplication) {

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    fDeriverDocument=itsDocument;
    fApplication=itsApplication;

    fEditorKit = new HTMLEditorKit();

    fJournalPane.setEditorKit(fEditorKit);  /*we need to ensure that each window has its own kit
                                             note, though, that SWING gives you only one for the application */

    ((HTMLDocument)fJournalPane.getDocument()).setDocumentFilter(new removeCommentFilter());

    fJournalPane.addHyperlinkListener(new TSimpleLinkListener(fJournalPane));

    createActionTable(fEditorKit);

    initializePalette(fDeriverDocument.fDefaultPaletteText);  //be sure to load preferences later as they may change this NO

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }


  // fApplication.addBrowserToList(this);



/*There are two kinds of configuration a) from the command line, and b) from the preferences.

   The command line takes 'preference' so we call it second*/

loadBrowserPreferences(); //an experiment Aug 2006


  if (TDeriverApplication.fConfiguration!=0)
     simplifyInterface(TDeriverApplication.fConfiguration);   //testing


  }

 public TBrowser(TDeriverDocument itsDocument,TDeriverApplication itsApplication,boolean debugMode) {
   this(itsDocument,itsApplication);

   fDebugMode=debugMode;
 }

/***************************  INITIALIZATION ********************/

private void initializePalette(String initStr){
  fPalette.setText(initStr);

      fPalette.setEditable(false);

      fPalette.setMinimumSize(new Dimension(200,30));  // we don't want the palette squeezed away


}


  private void jbInit() throws Exception  {


    contentPane = (JPanel) this.getContentPane();
    fDocument = fJournalPane.getDocument();        //SWING document not Deriver document
    fDocument.addDocumentListener(new TBrowser_fDocument_documentAdapter(this));     //change in journal

    fDeriverDocument.fProofPanel.addUndoableEditListener(new UndoableEditListener() {
        public void undoableEditHappened(UndoableEditEvent e){setDirty(true);}} );   //change in proof

    fDeriverDocument.fShapePanel.addUndoableEditListener(new UndoableEditListener() {
        public void undoableEditHappened(UndoableEditEvent e){setDirty(true);}} );   //change in drawing

    fDeriverDocument.fTreePanel.addUndoableEditListener(new UndoableEditListener() {
        public void undoableEditHappened(UndoableEditEvent e){setDirty(true);}} );   //change in drawing

    
    contentPane.setLayout(borderLayout1);
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.setSize(fDefaultSize);

    setWindowTitle();

    statusBar.setText(" ");

    createFileMenu();
    createEditMenu();
    createHTMLMenu();
    createSemanticsMenu();
    createActionsMenu();
    createGamesMenu();
    createHelpMenu();

    jMenuBar1.add(fileMenu);
    jMenuBar1.add(fEditMenu);
    jMenuBar1.add(HTMLMenu);
    jMenuBar1.add(semanticsMenu);
    jMenuBar1.add(actionsMenu);
    jMenuBar1.add(jMenuGames);
    jMenuBar1.add(helpMenu);

    this.setJMenuBar(jMenuBar1);


    {SimpleAttributeSet attrs = new SimpleAttributeSet();
  /*    StyleConstants.setFontFamily(attrs, "AppleGothic");
      StyleConstants.setFontSize(attrs, 12);  probably don't need this with html */

      fStyleAttributes = fStyleContext.addAttributes(SimpleAttributeSet.EMPTY,
          attrs);
    }

    fJournalPane.setCharacterAttributes(fStyleAttributes, false);

    fJournalPane.setDragEnabled(true);
    fPalette.setDragEnabled(true);

   /* presently use the same listener for both journal and proof */

   UndoableEditListener aListener= new TBrowser_fDocument_undoableEditAdapter(this);

    fDocument.addUndoableEditListener(aListener);

    journalProofSplitPane.setOneTouchExpandable(true);
    journalProofSplitPane.setLeftComponent(paletteTextSplitPane/*jScrollPane1*/);
    journalProofSplitPane.setRightComponent(fRightTabbedPane);


    paletteTextSplitPane.setTopComponent(fPalette);
    paletteTextSplitPane.setBottomComponent(jScrollPane1);

    contentPane.add(statusBar, BorderLayout.SOUTH);

   (fDeriverDocument.fShapePanel).setVisible(true);

   fRightTabbedPane.addTab("Proof", fDeriverDocument.fProofPanel);  // we use indices to access so keep this order, index 0

   fShapeScroller = new JScrollPane(fDeriverDocument.fShapePanel);

   fRightTabbedPane.addTab("Interpretation", fShapeScroller);  // we use indices to access so keep this order, index 1


   if (TPreferences.fTrees){
     fRightTabbedPane.addTab("Tree", fDeriverDocument.fTreePanel);
   }

   if (TPreferences.fLambda){
     fRightTabbedPane.addTab("Lambda", fDeriverDocument.fLambdaPanel);
   }


   contentPane.add(journalProofSplitPane, BorderLayout.CENTER);

   journalProofSplitPane.setDividerLocation(250 + journalProofSplitPane.getInsets().left);

   paletteTextSplitPane.setDividerLocation(36 + paletteTextSplitPane.getInsets().top);

    jScrollPane1.getViewport().add(fJournalPane,null);

  }






  private void createActionsMenu(){
    actionsMenu.setText("Actions");
    actionsMenu.addMenuListener(new TBrowser_actionsMenu_menuAdapter(this));

    parseMenuItem.setText("Parse");
    parseMenuItem.addActionListener(new TBrowser_parseMenuItem_actionAdapter(this));
    startProofMenuItem.addActionListener(new TBrowser_startProofMenuItem_actionAdapter(this));
    startProofMenuItem.setText("Start Proof");

    startTreeMenuItem.addActionListener(new TBrowser_startTreeMenuItem_actionAdapter(this));
    startTreeMenuItem.setText("Start Tree");

    startLambdaMenuItem.addActionListener(new TBrowser_startLambdaMenuItem_actionAdapter(this));
    startLambdaMenuItem.setText("Start Lambda Proof");

    doCommandMenuItem.setText("Do Command");
    doCommandMenuItem.addActionListener(new TBrowser_doCommandMenuItem_actionAdapter(this));

    evaluateMenuItem.addActionListener(new TBrowser_evaluateMenuItem_actionAdapter(this));
    evaluateMenuItem.setText("Evaluate in Scheme");

    tryMenuItem.setActionCommand("Try");
    tryMenuItem.setText("Try");
    tryMenuItem.addActionListener(new TBrowser_tryMenuItem_actionAdapter(this));

    decodeMenuItem.setActionCommand("Decode");
    decodeMenuItem.setText("Decode");
    decodeMenuItem.addActionListener(new TBrowser_decodeMenuItem_actionAdapter(this));

    xORMenuItem.setActionCommand("xOR");
    xORMenuItem.setText("xOR");
    xORMenuItem.addActionListener(new TBrowser_xORMenuItem_actionAdapter(this));

    actionsMenu.add(startProofMenuItem);
    if (TPreferences.fTrees)
       actionsMenu.add(startTreeMenuItem);
    if (TPreferences.fLambda)
       actionsMenu.add(startLambdaMenuItem);
    actionsMenu.add(doCommandMenuItem);

    if (fDebugMode){

      actionsMenu.add(evaluateMenuItem);

      actionsMenu.addSeparator();
      actionsMenu.add(tryMenuItem);
      actionsMenu.add(decodeMenuItem);
      actionsMenu.add(xORMenuItem);
      actionsMenu.add(parseMenuItem);
    }
  }

  //action provided by the editor kit by its name.
   private void createActionTable(HTMLEditorKit editorKit) {
       fActions = new Hashtable();
       Action[] actionsArray = editorKit.getActions();
       for (int i = 0; i < actionsArray.length; i++) {
           Action a = actionsArray[i];
           fActions.put(a.getValue(Action.NAME), a);
       }
   }

   //Create the edit menu.
      private void createEditMenu() {
        //  JMenu menu = new JMenu("Edit");
       //   Action retrievedAction;

          //Undo and redo are actions of our own creation.

          fEditMenu.setText("Edit");
         fEditMenu.addMenuListener(new TBrowser_fEditMenu_menuAdapter(this));

         fUndoAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
         fEditMenu.add(fUndoAction);
         fRedoAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

          fEditMenu.add(fRedoAction);
          fEditMenu.addSeparator();

          {//Action action = new HTMLEditorKit.CutAction();
           fCutAction.putValue(Action.NAME, "Cut");
           fCutAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
            fEditMenu.add(fCutAction);}


          {///Action action = new HTMLEditorKit.CopyAction();
          fCopyAction.putValue(Action.NAME, "Copy");
          fCopyAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
          fEditMenu.add(fCopyAction);}


           {Action action = new HTMLEditorKit.PasteAction();
           action.putValue(Action.NAME, "Paste");
           action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

           fEditMenu.add(action);}

         /*The next horrendous kludge is becuase there is a
      application wide sharing of the editor kit actions, select all, so we too
   have to share*/

           if (fSelectAllAction==null){  //fSelectAllAction is static

             if (fEditorKit != null) {
                Action retrievedAction=getActionByName(DefaultEditorKit.selectAllAction);

                retrievedAction.putValue(Action.NAME, "Select All");
                retrievedAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

                fEditMenu.add(retrievedAction);  // using the action hash table constructed here */
                fSelectAllAction=retrievedAction;

             /*  Action[] actionsArray = fEditorKit.getActions();
               Action retrievedAction = null;
               for (int i = 0; i < actionsArray.length; i++) {
                 Action a = actionsArray[i];
                 if ( (a.getValue(Action.NAME)).equals(DefaultEditorKit.
                                                       selectAllAction)) {
                   retrievedAction = a;
                   retrievedAction.putValue(Action.NAME, "Select All");
                   fSelectAll=retrievedAction;
                   menu.add(retrievedAction);
                   break;
                 }
                 ; */
             }
           }
           else{
             fSelectAllAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

             fEditMenu.add(fSelectAllAction);
           }




         /*   {Action retrievedAction=getActionByName(DefaultEditorKit.selectAllAction);

              retrievedAction.putValue(Action.NAME, "Select All");
              menu.add(retrievedAction);}  // using the action hash table constructed here */



    /*     {Action action = new StyledEditorKit.SelectAllAction();
           action.putValue(Action.NAME, "Select All");
           menu.add(action);} */

    /* I have had a lot of trouble with the select all action (March 04). This is the old code. Basically
    it works for the first window, but then the select all action is null retrieved from the action
   map is null for the second. This is the old code, the new code follows.


      {Action retrievedAction=getActionByName(DefaultEditorKit.selectAllAction);

              retrievedAction.putValue(Action.NAME, "Select All");
              menu.add(retrievedAction);}  // using the action hash table constructed here
            // select all action private in editorkits!   */

   /*

    {Action select = journalPane.getActionMap().get("select-all"/*DefaultEditorKit.selectAllAction);

      select.putValue(Action.NAME, "Select All");

      menu.add(select);
    }
    */


          //These actions come from the default editor kit.
          //Get the ones we want and stick them in the menu.

    /*     retrievedAction=getActionByName(DefaultEditorKit.cutAction);
          retrievedAction.putValue(Action.NAME, "Cut");
          menu.add(retrievedAction); */
   /*
          retrievedAction=getActionByName(DefaultEditorKit.copyAction);
          retrievedAction.putValue(Action.NAME, "Copy");
          menu.add(retrievedAction);

          retrievedAction=getActionByName(DefaultEditorKit.pasteAction);
          retrievedAction.putValue(Action.NAME, "Paste");
          menu.add(retrievedAction);



       //   menu.add(getActionByName(DefaultEditorKit.copyAction));
       //   menu.add(getActionByName(DefaultEditorKit.pasteAction)); */

          fEditMenu.addSeparator();

     /*     {Action action = new StyledEditorKit.SelectAllAction();
           action.putValue(Action.NAME, "Select All");
           menu.add(action);}  */


     /*     retrievedAction=getActionByName(DefaultEditorKit.selectAllAction);
          retrievedAction.putValue(Action.NAME, "Select All");
          menu.add(retrievedAction);


         // menu.add(getActionByName(DefaultEditorKit.selectAllAction));  */

     fFindAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

     fEditMenu.add(fFindAction);
     fReplaceAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

     fEditMenu.add(fReplaceAction);


   /* NO longer need the next code for students


     fEditMenu.addSeparator();

     fEditMenu.add(new AbstractAction("Convert old journal",
                   new ImageIcon("Replace16.gif")) {
                    public void actionPerformed(ActionEvent e) {
                   FindDialog aFindDialog = new FindDialog(TBrowser.this,fJournalPane, 1);
                   boolean more=true;

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("�",""+chUniquant);
                   more=true;
                   while (more) {
                     int result = aFindDialog.findNext(true, false);
                       if (result < 1)   //0 not found -1 error
                        more=false;
                         }

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("�",""+chExiquant);
                   more=true;
                   while (more) {
                      int result = aFindDialog.findNext(true, false);
                      if (result < 1)   //0 not found -1 error
                         more=false;
                   }

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("~",""+chNeg);
                   more=true;
                   while (more) {
                     int result = aFindDialog.findNext(true, false);
                     if (result < 1)   //0 not found -1 error
                     more=false;
                   }

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("�",""+chOr);
                   more=true;
                   while (more) {
                     int result = aFindDialog.findNext(true, false);
                     if (result < 1)   //0 not found -1 error
                     more=false;
                   }

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("^",""+chAnd);
                   more=true;
                   while (more) {
                     int result = aFindDialog.findNext(true, false);
                     if (result < 1)   //0 not found -1 error
                     more=false;
                   }

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("�",""+chImplic);
                   more=true;
                   while (more) {
                     int result = aFindDialog.findNext(true, false);
                     if (result < 1)   //0 not found -1 error
                     more=false;
                   }

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("�",""+chEquiv);
                   more=true;
                   while (more) {
                     int result = aFindDialog.findNext(true, false);
                     if (result < 1)   //0 not found -1 error
                     more=false;
                   }

                   fJournalPane.setCaretPosition(0);
                   aFindDialog.setFindAndReplace("\\",""+TMyProofPanel.chTherefore);
                   more=true;
                   while (more) {
                     int result = aFindDialog.findNext(true, false);
                     if (result < 1)   //0 not found -1 error
                     more=false;
                   }


//aFindDialog.show();
   }
   }
   );


   */


   }

  private void createFileMenu(){
     fileMenu.setText("File");
     fileMenu.addMenuListener(new TBrowser_fileMenu_menuAdapter(this));

     fileMenu.add(newBrowserMenuItem);
     fileMenu.add(newFileMenuItem);
     fileMenu.add(openFileMenuItem);
      fileMenu.add(closeBrowserMenuItem);
      fileMenu.add(saveMenuItem);
      fileMenu.add(saveAsMenuItem);
      fileMenu.addSeparator();
      fileMenu.add(openWebPageMenuItem);
      fileMenu.add(saveAsHTMLItem);
      fileMenu.addSeparator();
      fileMenu.add(pageLayoutMenuItem);
      fileMenu.add(printMenuItem);
      fileMenu.addSeparator();
      fileMenu.add(quitMenuItem);

      newBrowserMenuItem.setText("New Browser");
      newBrowserMenuItem.addActionListener(new TBrowser_newBrowserMenuItem_actionAdapter(this));

      newFileMenuItem.setText("New File");
      newFileMenuItem.addActionListener(new TBrowser_newFileMenuItem_actionAdapter(this));
      newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

      openFileMenuItem.setText("Open File");
      openFileMenuItem.addActionListener(new TBrowser_openFileMenuItem_actionAdapter(this));
      openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

      saveMenuItem.setText("Save");
      saveMenuItem.addActionListener(new TBrowser_saveMenuItem_actionAdapter(this));
      saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
      saveMenuItem.setEnabled(false);

      saveAsMenuItem.setText("Save As");
      saveAsMenuItem.addActionListener(new TBrowser_saveAsMenuItem_actionAdapter(this));


      saveAsHTMLItem.setText("Save Journal As HTML");
      saveAsHTMLItem.addActionListener(new TBrowser_saveAsHTMLItem_actionAdapter(this));

      openWebPageMenuItem.setText("Open Web Page in Journal");
      openWebPageMenuItem.addActionListener(new TBrowser_openWebPageMenuItem_actionAdapter(this));

      pageLayoutMenuItem.setText("Page Layout");
      pageLayoutMenuItem.setEnabled(false);

      printMenuItem.setText("Print Journal");
      printMenuItem.addActionListener(new TBrowser_printMenuItem_actionAdapter(this));

      closeBrowserMenuItem.setText("Close Browser");
      closeBrowserMenuItem.addActionListener(new TBrowser_closeBrowserMenuItem_actionAdapter(this));
      closeBrowserMenuItem.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

      quitMenuItem.setText("Quit");
      quitMenuItem.addActionListener(new TBrowser_quitMenuItem_ActionAdapter(this));
      quitMenuItem.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

 /*     fileMenu.setVisible(false);  // experiment Aug 5 06
      fileMenu.setEnabled(false);

      openFileMenuItem.setVisible(false);
      openFileMenuItem.setEnabled(false); */

}

private void createGamesMenu(){
  jMenuGames.setText("'Games'");
    jMenuGames.addMenuListener(new TBrowser_gamesMenu_menuAdapter(this));
    jMenuGames.add(propGamesSubMenu);
    propGamesSubMenu.setText("Propositional");
    connectiveMenuItem.setText("Main Connective");
    connectiveMenuItem.addActionListener(new TBrowser_connectiveMenuItem_ActionAdapter(this));
    propGamesSubMenu.add(connectiveMenuItem);
    truthTableMenuItem.setText("Truth Table");
    truthTableMenuItem.addActionListener(new TBrowser_truthTableMenuItem_ActionAdapter(this));
    propGamesSubMenu.add(truthTableMenuItem);
    satisfiableGameMenuItem.setText("Satisfiable");
    satisfiableGameMenuItem.addActionListener(new TBrowser_satisfiableGameMenuItem_ActionAdapter(this));
    propGamesSubMenu.add(satisfiableGameMenuItem);
    consistentGameMenuItem.setText("Consistent");
    consistentGameMenuItem.addActionListener(new TBrowser_consistentGameMenuItem_ActionAdapter(this));
    propGamesSubMenu.add(consistentGameMenuItem);
    invalidGameMenuItem.setText("Invalid");
    invalidGameMenuItem.addActionListener(new TBrowser_invalidGameMenuItem_ActionAdapter(this));
    propGamesSubMenu.add(invalidGameMenuItem);

  /*  propGamesSubMenu.setEnabled(false);   //for exam
    predGamesSubMenu.setEnabled(false);
    quizzesSubMenu.setEnabled(false);  */


  /*  JMenuItem tempMenuItem=new JMenuItem();
    tempMenuItem.setText("Quiz 2");
    tempMenuItem.addActionListener(new TBrowser_quiz2MenuItem_ActionAdapter(this));
    quizzesSubMenu.add(tempMenuItem);*/

    examsSubMenu=fDeriverDocument.supplyExamsSubMenu();
    quizzesSubMenu=fDeriverDocument.supplyQuizzesSubMenu();



   // quizzesSubMenu.add(new Quiz2());

   // quizzesSubMenu.add(new Quiz3());

 /*   JMenuItem temp2MenuItem=new JMenuItem();
    temp2MenuItem.setText("Quiz 3");
    temp2MenuItem.addActionListener(new TBrowser_quiz3MenuItem_ActionAdapter(this));
    quizzesSubMenu.add(temp2MenuItem); */

 /*   JMenuItem temp4aMenuItem=new JMenuItem();
    temp4aMenuItem.setText("Quiz 4");
    temp4aMenuItem.addActionListener(new TBrowser_quiz4MenuItem_ActionAdapter(this));
    quizzesSubMenu.add(temp4aMenuItem); */



//    quizzesSubMenu.add(new Quiz4());
   // quizzesSubMenu.add(new Quiz5());
//    quizzesSubMenu.add(new Quiz6());
 //   quizzesSubMenu.add(new Quiz7());
  //  quizzesSubMenu.add(new Quiz8());
   // quizzesSubMenu.add(new BonusQuiz());



/*
    JMenuItem temp3MenuItem=new JMenuItem();
    temp3MenuItem.setText("Mid-term Q6");
    temp3MenuItem.addActionListener(new TBrowser_midTerm2MenuItem_ActionAdapter(this));
    examsSubMenu.add(temp3MenuItem); */

   /* JMenuItem temp4MenuItem=new JMenuItem();
    temp4MenuItem.setText("Mid-term Q7,8");
    temp4MenuItem.addActionListener(new TBrowser_midTerm3MenuItem_ActionAdapter(this));
    examsSubMenu.add(temp4MenuItem);  */

    quizzesSubMenu.setText("Quizzes");

/*
    JMenuItem temp5MenuItem=new JMenuItem();
    temp5MenuItem.setText("Quiz 5");
    temp5MenuItem.addActionListener(new TBrowser_quiz5MenuItem_ActionAdapter(this));
    quizzesSubMenu.add(temp5MenuItem);  */

  /*  JMenuItem temp6MenuItem=new JMenuItem();
    temp6MenuItem.setText("Quiz 6");
    temp6MenuItem.addActionListener(new TBrowser_quiz5aMenuItem_ActionAdapter(this));
    quizzesSubMenu.add(temp6MenuItem);

*/

/*

    JMenuItem temp7MenuItem=new JMenuItem();
    temp7MenuItem.setText("Final Q6");
    temp7MenuItem.addActionListener(new TBrowser_finalQ6MenuItem_ActionAdapter(this));
*/

/*
    temp8MenuItem.setText("Final Q7,8");
    temp8MenuItem.addActionListener(new TBrowser_finalQ7MenuItem_ActionAdapter(this));

*/



    jMenuGames.add(predGamesSubMenu);
    predGamesSubMenu.setText("Predicate");

    predConnectiveMenuItem.setText("Main Connective");
    predConnectiveMenuItem.addActionListener(new TBrowser_predConnectiveMenuItem_ActionAdapter(this));
    predGamesSubMenu.add(predConnectiveMenuItem);

    predTruthTableMenuItem.setText("Truth Table");
    predTruthTableMenuItem.addActionListener(new TBrowser_predTruthTableMenuItem_ActionAdapter(this));
    predGamesSubMenu.add(predTruthTableMenuItem);



    predSatisfiableGameMenuItem.setText("Satisfiable 1");
    predSatisfiableGameMenuItem.addActionListener(new TBrowser_predSatisfiableGameMenuItem_ActionAdapter(this));
    predGamesSubMenu.add(predSatisfiableGameMenuItem);

    predSatisfiable2GameMenuItem.setText("Satisfiable 2");
    predSatisfiable2GameMenuItem.addActionListener(new TBrowser_predSatisfiable2GameMenuItem_ActionAdapter(this));
    predGamesSubMenu.add(predSatisfiable2GameMenuItem);

    predConsistentGameMenuItem.setText("Consistent");
    predConsistentGameMenuItem.addActionListener(new TBrowser_predConsistentGameMenuItem_ActionAdapter(this));
    predGamesSubMenu.add(predConsistentGameMenuItem);


    predInvalidGameMenuItem.setText("Invalid");
        predInvalidGameMenuItem.addActionListener(new TBrowser_predInvalidGameMenuItem_ActionAdapter(this));
    predGamesSubMenu.add(predInvalidGameMenuItem);


    if (quizzesSubMenu!=null)
      jMenuGames.add(quizzesSubMenu);



   // examsSubMenu.add(temp7MenuItem);
   // examsSubMenu.add(temp8MenuItem);
    examsSubMenu.setText("Exams");

    jMenuGames.add(examsSubMenu);

}

private void createHelpMenu(){


  helpMenu.setText("Help");
helpMenu.addActionListener(new TBrowser_helpMenu_actionAdapter(this));
helpMenuAbout.setText("About");
helpMenuAbout.addActionListener(new TBrowser_helpMenuAbout_ActionAdapter(this));

  preferencesMenuItem.setText("Preferences");

    preferencesMenuItem.setEnabled(true);


    preferencesMenuItem.addActionListener(new TBrowser_preferencesMenuItem_actionAdapter(this));

    helpMenu.add(helpMenuAbout);
    helpMenu.add(preferencesMenuItem);



}

private void createHTMLMenu(){

  HTMLMenu.setText("HTML");
    HTMLMenu.addMenuListener(new TBrowser_HTMLMenu_menuAdapter(this));


   HTMLMenu.add(editTextMenuItem);
    HTMLMenu.add(liveTextMenuItem);
    {ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(editTextMenuItem);
    buttonGroup.add(liveTextMenuItem);}
    ((JRadioButtonMenuItem)editTextMenuItem).setSelected(true);
    HTMLMenu.addSeparator();
    HTMLMenu.add(simpleMenu);


    insertImageMenuItem.setText("Insert Image");
    insertImageMenuItem.addActionListener(new TBrowser_insertImageMenuItem_actionAdapter(this));



    insertLinkMenuItem.setActionCommand("Insert Link");
    insertLinkMenuItem.setText("Insert Link");
    insertLinkMenuItem.addActionListener(new TBrowser_insertLinkMenuItem_actionAdapter(this));

    editHTMLMenuItem.setText("Edit Source");
    editHTMLMenuItem.addActionListener(new TBrowser_editHTMLMenuItem_actionAdapter(this));

    fontMenuItem.setText("Font");
    fontMenuItem.addActionListener(new TBrowser_fontMenuItem_actionAdapter(this));

    editTextMenuItem.addActionListener(new TBrowser_editTextMenuItem_actionAdapter(this));
    liveTextMenuItem.addActionListener(new TBrowser_liveTextMenuItem_actionAdapter(this));
//insertTagsMenuItem.setText("Insert HTML Markup");  //May
//   insertTagsMenuItem.addActionListener(new TBrowser_insertTagsMenuItem_actionAdapter(this));
    editTextMenuItem.setActionCommand("Edit text");
    editTextMenuItem.setText("Edit Text");
    liveTextMenuItem.setText("Live Text");
    eKitMenuItem.addActionListener(new TBrowser_eKitMenuItem_actionAdapter(this));
    eKitMenuItem.setText("Ekit HTML Editor");
    simpleMenu.setText("Simple Editing");
    insertBreakMenuItem.setText("Insert Break");
    insertBreakMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, java.awt.Event.SHIFT_MASK, false));
    insertBreakMenuItem.addActionListener(new TBrowser_insertBreakMenuItem_actionAdapter(this));
    insertRuleMenuItem.setText("Insert Line");
    insertRuleMenuItem.addActionListener(new TBrowser_insertRuleMenuItem_actionAdapter(this));

    simpleMenu.add(new TagAction(HTML.Tag.BLOCKQUOTE, "Blockquote",HTML.Attribute.DUMMY));
    simpleMenu.add(new TagAction(HTML.Tag.EM, "Emphasis",HTML.Attribute.DUMMY));
    simpleMenu.add(fontMenuItem);
    simpleMenu.add(new TagAction(HTML.Tag.H1, "Heading 1",HTML.Attribute.DUMMY));
    simpleMenu.add(new TagAction(HTML.Tag.H2, "Heading 2",HTML.Attribute.DUMMY));
    simpleMenu.add(new TagAction(HTML.Tag.H3, "Heading 3",HTML.Attribute.DUMMY));
    simpleMenu.add(new TagAction(HTML.Tag.H4, "Heading 4",HTML.Attribute.DUMMY));

    simpleMenu.add(insertBreakMenuItem);
    simpleMenu.add(insertImageMenuItem);
    simpleMenu.add(insertRuleMenuItem);
    simpleMenu.add(insertLinkMenuItem);


    simpleMenu.add(editHTMLMenuItem);

//HTMLMenu.add(insertTagsMenuItem);
    HTMLMenu.addSeparator();
    HTMLMenu.add(eKitMenuItem);

   /* can do it by tag action*/// HTMLMenu.add(new TagAction(HTML.Tag.A, "URL",HTML.Attribute.HREF));


}

private void createSemanticsMenu(){
  semanticsMenu.setText("Semantics");
  semanticsMenu.addMenuListener(new TBrowser_semanticsMenu_menuAdapter(this));



    trueMenuItem.setText("True?");
    trueMenuItem.addActionListener(new TBrowser_trueMenuItem_actionAdapter(this));
    satisfiableMenuItem.setText("Satisfiable?");
    satisfiableMenuItem.addActionListener(new TBrowser_satisfiableMenuItem_actionAdapter(this));
    endorseMenuItem.setText("Endorse");
    endorseMenuItem.addActionListener(new TBrowser_endorseMenuItem_actionAdapter(this));
    denyMenuItem.setText("Deny");
    denyMenuItem.addActionListener(new TBrowser_denyMenuItem_actionAdapter(this));



    valuationMenuItem.setText("Current Valuation");
    valuationMenuItem.addActionListener(new TBrowser_valuationMenuItem_actionAdapter(this));
    interpretationMenuItem.setText("Current Interpretation");
    interpretationMenuItem.addActionListener(new TBrowser_interpretationMenuItem_actionAdapter(this));
    propositionsMenuItem.setText("Current True Propositions");
    propositionsMenuItem.addActionListener(new TBrowser_propositionsMenuItem_actionAdapter(this));
    symbolizeMenuItem.setText("To Symbols");
    symbolizeMenuItem.addActionListener(new TBrowser_symbolizeMenuItem_actionAdapter(this));
    toEnglishMenuItem.setText("To English");
    toEnglishMenuItem.addActionListener(new TBrowser_toEnglishMenuItem_actionAdapter(this));

    /* semanticsMenu.add(valuationMenuItem);  not adding this Dec05, because we have no use for it at present */
    semanticsMenu.add(interpretationMenuItem);
    semanticsMenu.add(propositionsMenuItem);
    semanticsMenu.addSeparator();
    semanticsMenu.add(trueMenuItem);
    semanticsMenu.add(satisfiableMenuItem);
    semanticsMenu.addSeparator();

    if (TPreferences.fEndorseMenu){
  semanticsMenu.add(endorseMenuItem);
  semanticsMenu.add(denyMenuItem);
  semanticsMenu.addSeparator();
}


    semanticsMenu.add(symbolizeMenuItem);
    semanticsMenu.add(toEnglishMenuItem);
    semanticsMenu.addSeparator();




    semanticsMenu.add(propMenuItem);
    semanticsMenu.add(predMenuItem);
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(propMenuItem);
    buttonGroup.add(predMenuItem);

    ((JRadioButtonMenuItem)predMenuItem).setSelected(true);


}

/***************************   Preferences    **************************************/

  /*Note there is an order to this, preferences first (which are permanent)
   then command line (which are instance)*/

  private void loadBrowserPreferences(){

    TPreferences.loadUserPrefs();   // check we are up to date


if (TPreferences.fSimpleFileMenu)
   simplifyFileMenu();


if (!(TPreferences.fHTMLMenu))
 {
   HTMLMenu.setEnabled(false);
   HTMLMenu.setVisible(false);
 }


    if (!TPreferences.fGamesMenu)
     {
       jMenuGames.setEnabled(false);
       jMenuGames.setVisible(false);
     }


 if(!TPreferences.fInterpretation&&
    !TPreferences.fProofs&&
    !TPreferences.fTrees){
   fRightTabbedPane.setEnabled(false);
   fRightTabbedPane.setVisible(false);

   actionsMenu.remove(startProofMenuItem);
   actionsMenu.remove(startLambdaMenuItem);  // no proofs, no lambda proofs
 }
 else{
   if (!TPreferences.fProofs) {
     fRightTabbedPane.remove(fDeriverDocument.fProofPanel);    //.removeTab(fDeriverDocument.fProofPanel); //remove the proof
     actionsMenu.remove(startProofMenuItem);
     actionsMenu.remove(startLambdaMenuItem);  // no proofs, no lambda proofs
}


   if (!TPreferences.fInterpretation) {
     fRightTabbedPane.remove(fShapeScroller); //remove the interpretation
   }

   if (!TPreferences.fTrees) {
     fRightTabbedPane.remove(fDeriverDocument.fTreePanel); //remove the tree
}

 }

if (TPreferences.fPaletteText.equals("default"))
  initializePalette(fDeriverDocument.fDefaultPaletteText);  //document should do this.
else
  initializePalette(TPreferences.fPaletteText);



}

/*
public static void setCurrentUser(String user){
 Preferences userPreferences = Preferences.userNodeForPackage(TheApplication.getClass());

 fUser=user;
 userPreferences.put("User",user);
}

 */

/**********************************************************************************/


/************************ General Methods*****************************************/

private boolean closeBrowser() {
   if (okToAbandon())
     return
         fApplication.closeBrowser(this);
   else

    return false;  //no browser closed
 }

 protected void closeBrowserMenuItem_actionPerformed(ActionEvent e) {
    closeBrowser();
  }

  void createDrawingFromXML(String xmlStr) {
   // String fileName=null;

    if (!okToAbandon())           // check is the current file is dirty
    {
      fDeriverDocument.writeToJournal(
               "(*You need to decide whether to save the document"
               + " because drawing may well draw over"
               + " the existing drawing.*)"
               + strCR,
               TConstants.HIGHLIGHT, !TConstants.TO_MARKER);

      return;
    }

   byte[] input = xmlStr.getBytes();

  //  try {

      ByteArrayInputStream stream = new ByteArrayInputStream(input);

      fBadXML = false;

      BadXMLHandler myBadXMLSetter = new BadXMLHandler();

      XMLDecoder d = new XMLDecoder(new BufferedInputStream(
          stream), null, myBadXMLSetter);

      if (fBadXML) {
        fDeriverDocument.writeToJournal(
               "(*The XML reader, used to read your command,"
               + " has reported that some of the XML"
               + " in your command is bad.*)"
               + strCR,
               TConstants.HIGHLIGHT, !TConstants.TO_MARKER);


        d.close();

      }
      else {

        try {Object shapeList = d.readObject();

        if (!fBadXML){

          fDeriverDocument.fShapePanel.setShapeList( (ArrayList) shapeList);
          fRightTabbedPane.setSelectedIndex(1);
        }
      }
      catch (ArrayIndexOutOfBoundsException ex){
             fDeriverDocument.writeToJournal(
               "(*The Object creator, used to produce the,"
               + "drawing from your XML command, has failed to"
               + " produce the drawing. Typically this means"
               + " that you have asked for something that cannot be done.*)"
               + strCR,
               TConstants.HIGHLIGHT, !TConstants.TO_MARKER);


      }

      }
 /*   }
    catch (IOException e) {
      statusBar.setText("Error opening "+fileName);
    }

 //   catch (ClassNotFoundException cnfe) { /*need to handle this*/
  }


  String createXMLFromDrawing() {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();


      XMLEncoder os = new XMLEncoder(new BufferedOutputStream(
                              stream));

 //     os.writeObject(new Integer(1));     // file format version number


      os.writeObject(fDeriverDocument.fShapePanel.getShapeList());

      os.close();


return
          stream.toString();

  }

protected void fDocument_changedUpdate(DocumentEvent e) {
   setDirty(true);
 }

 protected void fDocument_insertUpdate(DocumentEvent e) {
   setDirty(true);
 }

 protected void fDocument_removeUpdate(DocumentEvent e) {
   setDirty(true);
 }

 protected void fDocument_undoableEditHappened(UndoableEditEvent e) {
   //Remember the edit and update the menus.
     fUndoManager.addEdit(e.getEdit());
     fUndoAction.updateUndoState();
     fRedoAction.updateRedoState();

 }


 protected void fontMenuItem_actionPerformed(ActionEvent e) {
  // this.repaint();  // don't know why

   fFontDialog.setAttributes(fJournalPane.getCharacterAttributes());

   {
     Dimension d1 = fFontDialog.getSize();
       Dimension d2 = this.getSize();
       int x = Math.max((d2.width-d1.width)/2, 0);
       int y = Math.max((d2.height-d1.height)/2, 0);
       fFontDialog.setBounds(x + this.getX(),
       y + this.getY(), d1.width, d1.height);
  }

       fFontDialog.setVisible(true);

       if (fFontDialog.getOption()==JOptionPane.OK_OPTION) {
         setAttributeSet(fFontDialog.getAttributes());       //this
         showAttributes(fJournalPane.getCaretPosition());
       }

 }



 /* being written boolean incomplete(TTestNode aTestRoot){

  if (aTestRoot.fAntecedents != null) {
    Iterator iter = aTestRoot.fAntecedents.iterator();

    while (iter.hasNext()) {

      TFormula nextFormula = ( (TFormula) iter.next());

      if ( (nextFormula.fKind == predicator)
          && (nextFormula.arity() == 0))
        outputStr = outputStr + nextFormula.fInfo;

    }

  }
}  */



boolean freeInterpretFreeVariables(ArrayList interpretation){
  /* What we have here is a consistent list of positive and negative atomic formulas,
   which are true, we need
     to pull out the atomic terms and make them the universe, then interpret the  predicates and relations
 suitably
But they may have free variables. Any new constant will do here*/

  //String universe=TFormula.atomicTermsInListOfFormulas(interpretation);
  
  Set <String> universeStrSet=TFormula.atomicTermsInListOfFormulas(interpretation);
  
  String universe="";
  
  for (Iterator i=universeStrSet.iterator();i.hasNext();)
  	universe+=i.next();
  
  
  
  char searchCh;
  char constant;
  ArrayList valuation=new ArrayList();
  int n=1;
  TFormula valuForm;

  for (int i=0;i<universe.length();i++){

    searchCh=universe.charAt(i);

    if (TParser.isVariable(searchCh)){
      constant=TParser.nthNewConstant(n,universe);
      n+=1;
      if (constant==' ')
        return
            false;
      else{
           valuForm=new TFormula((short)0,constant +"/" + searchCh,null,null);
           /*the info on a valuation looks like this "a/x"
              and we want to substitute the constant a for the variable
              x throughout the formula*/
           valuation.add(valuForm);
           fDeriverDocument.writeToJournal(strCR
                                   +"In the drawing, the object "
                                   +constant
                                   + " is "
                                   + searchCh+"."
                                   +strCR
                                   , !TConstants.HIGHLIGHT, !TConstants.TO_MARKER);

      }
    }

  }

  if (valuation.size()>0)
    TFormula.interpretFreeVariables(valuation, interpretation);  //surgery

 return
     true;
}

/*
 procedure InterpretFreeVariables;

    var
     searchStr, newStr: string[1];
     searchCh: CHAR;
     i, j: integer;
     tempStr: str255;


   begin
    searchStr := 'a';
    newStr := 'a';
    for i := 1 to length(fNewInterpretation[1]) do
     begin
      searchCh := fNewInterpretation[1][i];
      if searchCh in gVariables then
       begin
        tempStr := fNewInterpretation[1];

        newStr := FirstNewConstant(tempStr); (*avoid unsafe use of handle*)


        searchStr[1] := searchCh;
        j := pos(searchStr, fNewInterpretation[1]);
        while J <> 0 do
        begin
        Delete(fNewInterpretation[1], j, 1);
        insert(newStr, fNewInterpretation[1], j);
        j := pos(searchStr, fNewInterpretation[1]);
        end;

        if length(fNewInterpretation[2]) > 0 then
        begin
        j := pos(searchStr, fNewInterpretation[2]);
        while J <> 0 do
        begin
        Delete(fNewInterpretation[2], j, 1);
        insert(newStr, fNewInterpretation[2], j);
        j := pos(searchStr, fNewInterpretation[2]);
        end;
        end;
        if length(fNewInterpretation[3]) > 0 then
        begin
        j := pos(searchStr, fNewInterpretation[3]);
        while J <> 0 do
        begin
        Delete(fNewInterpretation[3], j, 1);
        insert(newStr, fNewInterpretation[3], j);
        j := pos(searchStr, fNewInterpretation[3]);
        end;
        end;
        if length(fNewInterpretation[4]) > 0 then
        begin
        j := pos(searchStr, fNewInterpretation[4]);
        while J <> 0 do
        begin
        Delete(fNewInterpretation[4], j, 1);
        insert(newStr, fNewInterpretation[4], j);
        j := pos(searchStr, fNewInterpretation[4]);
        end;
        end;
        if length(fNewInterpretation[5]) > 0 then
        begin
        j := pos(searchStr, fNewInterpretation[5]);
        while J <> 0 do
        begin
        Delete(fNewInterpretation[5], j, 1);
        insert(newStr, fNewInterpretation[5], j);
        j := pos(searchStr, fNewInterpretation[5]);
        end;
        end;
        if length(fNewInterpretation[6]) > 0 then
        begin
        j := pos(searchStr, fNewInterpretation[6]);
        while J <> 0 do
        begin
        Delete(fNewInterpretation[6], j, 1);
        insert(newStr, fNewInterpretation[6], j);
        j := pos(searchStr, fNewInterpretation[6]);
        end;
        end;
        if length(fNewInterpretation[7]) > 0 then
        begin
        j := pos(searchStr, fNewInterpretation[7]);
        while J <> 0 do
        begin
        Delete(fNewInterpretation[7], j, 1);
        insert(newStr, fNewInterpretation[7], j);
        j := pos(searchStr, fNewInterpretation[7]);
        end;
        end;

        if drawable then
        if (fDeriverDocument.fShapeList.fSize < 2) then (*cannot be drawn otherwise*)
        fDeriverDocument.WriteToJournal(concat(gCr, 'In the drawing, the object ', newStr, ' is ', searchStr, '.'), FALSE, FALSE);

       end;
     end;

   end;



*/

private int identifyCommand(String inputStr){

    inputStr=inputStr.trim();  //leading blanks

    StringTokenizer st = new StringTokenizer(inputStr, " ");
    String token=" ";


         token = st.nextToken();


       if (token.toLowerCase().equals("make")){
         token=" ";
         while ((st.hasMoreTokens())&& token.equals(" ")){
            token = st.nextToken(" (");        // blank or opening left bracket  delimiter
         }

         if (token.toLowerCase().equals("drawing")){
           return
               kMakeDrawingCmd;
         }
       }

       if (token.toLowerCase().equals("writexml")){
         token=" ";
         while ((st.hasMoreTokens())&& token.equals(" ")){
            token = st.nextToken(" (");        // blank or opening left bracket
         }

         if (token.toLowerCase().equals("drawing")){
           return
               kWriteDrawingCmd;
         }
       }


    return
        kLISPCmd;
  }

  /* What we have here is a consistent list of positive and negative atomic formulas, which are true, we need
       to pull out the atomic terms and make them the universe, then interpret the  predicates and relations
   suitably */

  /*This used to be called boolean writeInterpretationList(ArrayList interpretation)*/

  String interpretationListToString(ArrayList interpretation, TFlag drawable){
     drawable.setValue(true);
     TSemantics newSemantics =new TSemantics();


    // String outputStr=TFormula.atomicTermsInListOfFormulas(interpretation);
     
     Set <String> outputStrSet=TFormula.atomicTermsInListOfFormulas(interpretation);
     
     String outputStr="";
     
     for (Iterator i=outputStrSet.iterator();i.hasNext();)
    	 outputStr+=i.next();

     if (outputStr.length()>12)
        drawable.setValue(false);//{check, override for more with esl only a ..m available}


     outputStr=TUtilities.separateStringWithCommas(outputStr);

     /*fDeriverDocument.writeToJournal(strCR
                                     +"Universe= { "
                                     +outputStr
                                     + " }"
                                     +strCR
                                     , TConstants.HIGHLIGHT, !TConstants.TO_MARKER);*/

     String firstStr= strCR
                      +"Universe= { "
                      +outputStr
                      + " }"
                      +strCR;

     String secondStr="";

     outputStr=TFormula.trueAtomicFormulasInList(interpretation);

     if (outputStr.length()>0){
       drawable.setValue(false);                   //cannot draw a true proposition
       outputStr = TUtilities.separateStringWithCommas(outputStr);
       secondStr="True Propositions= { "
                                       + outputStr
                                       + " }"
                                       + strCR;
     }

     String thirdStr="";

     outputStr=TFormula.falseAtomicFormulasInList(interpretation);

     if (outputStr.length()>0){
       drawable.setValue(false);                 //cannot draw a false proposition
       outputStr = TUtilities.separateStringWithCommas(outputStr);
       thirdStr="False Propositions= { "
                                       + outputStr
                                       + " }"
                                       + strCR;
     }


  String fourthStr="";

  int length = TParser.gPredicates.length();
  String predicate;

  int count=0;

  for (int i=0;i<length;i++){
    predicate=TParser.gPredicates.substring(i,i+1);

    outputStr=TFormula.extensionOfUnaryPredicate(interpretation, predicate);

    if ((outputStr!=null)&&(outputStr.length()>0)){
       count+=1;
       outputStr = TUtilities.separateStringWithCommas(outputStr);
       fourthStr+=predicate
                                       + " = { "
                                       + outputStr
                                       + " }"
                                       + strCR;
    }
  }

  if (count>3)
    drawable.setValue(false); //we can draw no more than 3 unary

  /*
      tempStr := '';
       tempInfo := 'A'; {used to be superone}
       found := FALSE;

       count := 2;
       tempCh := 'A';
       while tempCh <= 'Z' do
        begin
         found := FALSE;
         tempStr := '';
         tempInfo[1] := tempCh;
         fInterpretationList.Each(CheckUnary);
         if found then
          begin
           if drawable then
            begin
            if count < 5 then
            begin
            fNewInterpretation[count] := concat(tempCh, tempStr);
                                      {the first char is a label to indicate which property}
            count := count + 1;
            end
            else
            drawable := FALSE;

            end;

           lengthofStr := length(tempStr);

           while lengthofStr > 1 do
            begin
            insert(',', tempStr, lengthofStr);
            lengthofStr := lengthofStr - 1;
            end;

           tempStr := concat(tempInfo, '= { ', tempStr, ' }', gCr);

           fDeriverDocument.WriteToJournal(tempStr, FALSE, FALSE);
          end;

         tempCh := chr(ord(tempCh) + 1);

        end;


  */

  String fifthStr="";

  count=0;

  for (int i=0;i<length;i++){
    predicate=TParser.gPredicates.substring(i,i+1);

    outputStr=TFormula.extensionOfBinaryPredicate(interpretation, predicate);

    if ((outputStr!=null)&&(outputStr.length()>0)){
      count+=1;
       outputStr = TUtilities.intoOrderedPairs(outputStr);
       fifthStr+=predicate
                                       + " = { "
                                       + outputStr
                                       + " }"
                                       + strCR;
    }

  }

  if (count>3)
    drawable.setValue(false); //we can draw no more than 3 binary



  /*
     incomplete := FALSE;
       gTestRoot.fAntecedents.Each(CheckTrue);

       if incomplete then (*partial interpretation --cycle*)
        begin
         fDeriverDocument.WriteToJournal(concat(gCr, 'Partial interpretation only-- program has used an infinite universe.'), FALSE, FALSE);
         fDeriverDocument.WriteToJournal(concat(gCr, 'Yet it knows there is a finite universe version.'), FALSE, FALSE);
         fDeriverDocument.WriteToJournal(concat(gCr, 'You should form a cycle of relations to complete.'), FALSE, FALSE);

        end;


       if drawable then
        InterpretFreeVariables; {cannot have x's etc in drawing}


  */


// NEED THE EXTRA BIT HERE ABOUT CYCLES



      return
          firstStr
                                     +secondStr
                                     +thirdStr
                                     +fourthStr
                                     +fifthStr;



          }

  /*

           procedure TJournalWindow.WriteInterpretationList (var drawable: boolean);
          {give this a good check}

            var
             i, lengthofStr, index, count: integer;
             tempStr: str255;
             tempInfo: string[7];
             tempCh: CHAR;
             found, incomplete: boolean;

            procedure CheckTerm (item: TObject);

             var
              tempformula: TFormula;
              arity: integer;

            begin {presented with predicates or negations of predicates}
             if not found then
              begin
               tempformula := TFormula(item);
               if (tempformula.fKind = unary) then
                tempformula := tempformula.fRlink;

               arity := tempformula.Arity;

               while (not found) and (arity > 0) do
                begin
                 if tempInfo = tempformula.NthTerm(arity).fInfo then
                 found := TRUE;
                 arity := arity - 1;
                end;

               if not found then
                if (pos(tempInfo, gConstantsInTestRoot) <> 0) then
                 found := TRUE;
              end;
            end;

            procedure CheckTrueProposition (item: TObject);

             var
              tempformula: TFormula;

            begin
             if not found then
              begin
               tempformula := TFormula(item);
               if (tempformula.fKind = predicator) then
                if (tempformula.Arity = 0) then
                 if tempInfo = tempformula.fInfo then
                 found := TRUE;
              end;
            end;

            procedure CheckFalseProposition (item: TObject);

             var
              tempformula: TFormula;

            begin
             if not found then
              begin
               tempformula := TFormula(item);
               if (tempformula.fKind = unary) then
                if (tempformula.fKind = predicator) then
                 if (tempformula.Arity = 0) then
                 if tempInfo = tempformula.fRLink.fInfo then
                 found := TRUE;
              end;
            end;

            procedure CheckUnary (item: TObject);

             var
              tempformula: TFormula;

            begin
             tempformula := TFormula(item);
             if not (tempformula.fKind = unary) then
              if (tempformula.fKind = predicator) then
               if (tempformula.Arity = 1) then
                if (tempInfo = tempFormula.fInfo) then
                 begin
                 tempStr := concat(tempStr, tempFormula.FirstTerm.fInfo);
                 found := TRUE; {wnat all of them}
                 end;
            end;

            procedure CheckBinary (item: TObject);
             var
              tempformula: TFormula;
            begin
             tempformula := TFormula(item);
             if not (tempformula.fKind = unary) then
              if (tempformula.fKind = predicator) then
               if (tempformula.Arity = 2) then
                if (tempInfo[1] = tempFormula.fInfo[1]) then
                 begin
                 tempStr := concat(tempStr, tempformula.FirstTerm.fInfo, tempformula.SecondTerm.fInfo);
                 found := TRUE; {wnat all of them}
                 end;
            end;

            procedure InterpretFreeVariables;

             var
              searchStr, newStr: string[1];
              searchCh: CHAR;
              i, j: integer;
              tempStr: str255;


            begin
             searchStr := 'a';
             newStr := 'a';
             for i := 1 to length(fNewInterpretation[1]) do
              begin
               searchCh := fNewInterpretation[1][i];
               if searchCh in gVariables then
                begin
                 tempStr := fNewInterpretation[1];

                 newStr := FirstNewConstant(tempStr); (*avoid unsafe use of handle*)


                 searchStr[1] := searchCh;
                 j := pos(searchStr, fNewInterpretation[1]);
                 while J <> 0 do
                 begin
                 Delete(fNewInterpretation[1], j, 1);
                 insert(newStr, fNewInterpretation[1], j);
                 j := pos(searchStr, fNewInterpretation[1]);
                 end;

                 if length(fNewInterpretation[2]) > 0 then
                 begin
                 j := pos(searchStr, fNewInterpretation[2]);
                 while J <> 0 do
                 begin
                 Delete(fNewInterpretation[2], j, 1);
                 insert(newStr, fNewInterpretation[2], j);
                 j := pos(searchStr, fNewInterpretation[2]);
                 end;
                 end;
                 if length(fNewInterpretation[3]) > 0 then
                 begin
                 j := pos(searchStr, fNewInterpretation[3]);
                 while J <> 0 do
                 begin
                 Delete(fNewInterpretation[3], j, 1);
                 insert(newStr, fNewInterpretation[3], j);
                 j := pos(searchStr, fNewInterpretation[3]);
                 end;
                 end;
                 if length(fNewInterpretation[4]) > 0 then
                 begin
                 j := pos(searchStr, fNewInterpretation[4]);
                 while J <> 0 do
                 begin
                 Delete(fNewInterpretation[4], j, 1);
                 insert(newStr, fNewInterpretation[4], j);
                 j := pos(searchStr, fNewInterpretation[4]);
                 end;
                 end;
                 if length(fNewInterpretation[5]) > 0 then
                 begin
                 j := pos(searchStr, fNewInterpretation[5]);
                 while J <> 0 do
                 begin
                 Delete(fNewInterpretation[5], j, 1);
                 insert(newStr, fNewInterpretation[5], j);
                 j := pos(searchStr, fNewInterpretation[5]);
                 end;
                 end;
                 if length(fNewInterpretation[6]) > 0 then
                 begin
                 j := pos(searchStr, fNewInterpretation[6]);
                 while J <> 0 do
                 begin
                 Delete(fNewInterpretation[6], j, 1);
                 insert(newStr, fNewInterpretation[6], j);
                 j := pos(searchStr, fNewInterpretation[6]);
                 end;
                 end;
                 if length(fNewInterpretation[7]) > 0 then
                 begin
                 j := pos(searchStr, fNewInterpretation[7]);
                 while J <> 0 do
                 begin
                 Delete(fNewInterpretation[7], j, 1);
                 insert(newStr, fNewInterpretation[7], j);
                 j := pos(searchStr, fNewInterpretation[7]);
                 end;
                 end;

                 if drawable then
                 if (fDeriverDocument.fShapeList.fSize < 2) then (*cannot be drawn otherwise*)
                 fDeriverDocument.WriteToJournal(concat(gCr, 'In the drawing, the object ', newStr, ' is ', searchStr, '.'), FALSE, FALSE);

                end;
              end;

            end;


   {$IFC myDebugging}

    procedure BugWriteFormula (root: TFormula); (*debugging*)
     var
      aParser: TParser;
      aStr: str255;

    begin

     New(aParser);

     aParser.WriteFormulaToString(root, aStr);
     write(aStr);

     aParser.Free;

    end;

  {$ENDC}


    procedure CheckTrue (item: TObject);
    begin
  {$IFC myDebugging}
     if FALSE then
      begin
       writeln('checking true');
       BugWriteFormula(TFormula(item));
      end;
  {$ENDC}

     if not incomplete then
      begin
       incomplete := not SELF.FormulaTrue(TFormula(item));
  {$IFC myDebugging}
       if FALSE then
        begin
         BugWriteFormula(TFormula(item));
         if incomplete then
         writeln('false');
        end;
  {$ENDC}
      end;
    end;

   begin
    InitNewInterpretationArray;    STARTHERE
    tempStr := '';
    tempInfo := 'a';
    found := FALSE;

    tempCh := 'a';
    while tempCh <= 'z' do
     begin
      found := FALSE;
      tempInfo[1] := tempCh;
      fInterpretationList.Each(CheckTerm);
      if found then
       begin

        tempStr := concat(tempStr, tempInfo);
       end;
      tempCh := chr(ord(tempCh) + 1);
     end;

    lengthofStr := length(tempStr);
    if lengthofStr > 12 then
     drawable := FALSE; {check, override for more with esl only a ..m available}

    if drawable then
     fNewInterpretation[1] := tempStr;

    while lengthofStr > 1 do
     begin
      insert(',', tempStr, lengthofStr);
      lengthofStr := lengthofStr - 1;
     end;

    tempStr := concat(gCr, 'Universe= { ', tempStr, ' }', gCr);

    fDeriverDocument.WriteToJournal(tempStr, FALSE, FALSE);

    tempStr := '';
    tempInfo := 'A';
    found := FALSE;

    tempCh := 'A';
    while tempCh <= 'Z' do
     begin
      found := FALSE;
      tempInfo[1] := tempCh;
      fInterpretationList.Each(CheckTrueProposition);
      if found then
       begin
        tempStr := concat(tempStr, tempInfo);
        drawable := FALSE;
       end;
      tempCh := chr(ord(tempCh) + 1);
     end;

    lengthofStr := length(tempStr);

    while lengthofStr > 1 do
     begin
      insert(',', tempStr, lengthofStr);
      lengthofStr := lengthofStr - 1;
     end;

    if lengthofStr > 0 then
     begin
      tempStr := concat('True Propositions= { ', tempStr, ' }', gCr);
      fDeriverDocument.WriteToJournal(tempStr, FALSE, FALSE);
     end;

    tempStr := '';
    tempInfo := 'A';
    found := FALSE;

    tempCh := 'A';
    while tempCh <= 'Z' do
     begin
      found := FALSE;
      tempInfo[1] := tempCh;
      fInterpretationList.Each(CheckFalseProposition);
      if found then
       begin
        tempStr := concat(tempStr, tempInfo);
        drawable := FALSE;
       end;
      tempCh := chr(ord(tempCh) + 1);
     end;

    lengthofStr := length(tempStr);

    while lengthofStr > 1 do
     begin
      insert(',', tempStr, lengthofStr);
      lengthofStr := lengthofStr - 1;
     end;

    if lengthofStr > 0 then
     begin
      tempStr := concat('False Propositions= { ', tempStr, ' }', gCr);
      fDeriverDocument.WriteToJournal(tempStr, FALSE, FALSE);
     end;

    tempStr := '';
    tempInfo := 'A'; {used to be superone}
    found := FALSE;

    count := 2;
    tempCh := 'A';
    while tempCh <= 'Z' do
     begin
      found := FALSE;
      tempStr := '';
      tempInfo[1] := tempCh;
      fInterpretationList.Each(CheckUnary);
      if found then
       begin
        if drawable then
         begin
         if count < 5 then
         begin
         fNewInterpretation[count] := concat(tempCh, tempStr);
                                   {the first char is a label to indicate which property}
         count := count + 1;
         end
         else
         drawable := FALSE;

         end;

        lengthofStr := length(tempStr);

        while lengthofStr > 1 do
         begin
         insert(',', tempStr, lengthofStr);
         lengthofStr := lengthofStr - 1;
         end;

        tempStr := concat(tempInfo, '= { ', tempStr, ' }', gCr);

        fDeriverDocument.WriteToJournal(tempStr, FALSE, FALSE);
       end;

      tempCh := chr(ord(tempCh) + 1);

     end;

    tempStr := '';
    tempInfo := 'A�';
    index := 2; (*altered for relations of higher arity*)

    case index of
     2:
      tempInfo[2] := chSupertwo;
     otherwise
    end;


    found := FALSE;
    count := 5;

    tempCh := 'A';

    while tempCh <= 'Z' do
     begin
      found := FALSE;
      tempStr := '';
      tempInfo[1] := tempCh;
      fInterpretationList.Each(CheckBinary);
      if found then
       begin
                       {    IF drawable THEN IF (tempInfo[2] <> '�') THEN drawable := FALSE; checks}
  {                              binary only}

        if drawable then
         begin
         if count < 8 then
         begin
         fNewInterpretation[count] := concat(tempCh, tempStr);
                                        {the first char is a label to indicate which property}
         count := count + 1;
         end
         else
         drawable := FALSE;
         end;

        lengthofStr := length(tempStr) - index + 1;
        if lengthofStr > 110 then
         lengthofStr := 110; {not out of range}

        while lengthofStr > 1 do
         begin
         insert('>,<', tempStr, lengthofStr);
         lengthofStr := lengthofStr - index;
         end;

        if length(tempStr) > 1 then
         tempStr := concat('<', tempStr, '>');

        tempStr := concat(tempInfo, '= { ', tempStr, ' }', gCr);

        fDeriverDocument.WriteToJournal(tempStr, FALSE, FALSE);

       end;

      tempCh := chr(ord(tempCh) + 1);

     end;

    incomplete := FALSE;
    gTestRoot.fAntecedents.Each(CheckTrue);

    if incomplete then (*partial interpretation --cycle*)
     begin
      fDeriverDocument.WriteToJournal(concat(gCr, 'Partial interpretation only-- program has used an infinite universe.'), FALSE, FALSE);
      fDeriverDocument.WriteToJournal(concat(gCr, 'Yet it knows there is a finite universe version.'), FALSE, FALSE);
      fDeriverDocument.WriteToJournal(concat(gCr, 'You should form a cycle of relations to complete.'), FALSE, FALSE);

     end;


    if drawable then
     InterpretFreeVariables; {cannot have x's etc in drawing}

   end;




     */


int nodeSatisfiable(TTestNode aTestRoot, TTreeModel aTreeModel){

  if (aTestRoot.closeSequent()){
    aTestRoot.fClosed=true;
    aTestRoot.fDead=true;
  }

  // aTestRoot.fStepsToExpiry=TTestNode.kMaxTreeDepth;

  // int maxSteps=50;

   return
       aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);


   // need to recheck here for ExCV


}


/*
 procedure NodeSatisfiable;

        var
         argtype: argumenttype;
         tempTest: TTestNode;

       begin
        if gTestroot.CloseSequent then
         begin
          gTestroot.fClosed := TRUE;
          gTestroot.fDead := TRUE;
         end;


        argtype := gTestroot.TreeValid(128);

        if gExCVFlag then {falgging for change of variable}
         if not (argtype = valid) then
          gExCV := TRUE;

        if gExCV then
         begin
          tempTest := gTestroot.CopyNodeinFull;

          DismantleTestTree(gTestroot);

          StartSatisfactionTree; {re-initializes, including gExCV}

          gExCV := TRUE;

          tempTest.fClosed := FALSE;
          tempTest.fDead := FALSE;
          tempTest.fSteptype := unknown;
          tempTest.fLLink := nil;
          tempTest.fRLink := nil;

          gTestroot := tempTest;
          tempTest := nil;

          argtype := gTestroot.TreeValid(128);

         end;

        case argtype of
         notvalid:
          begin
           fDeriverDocument.WriteToJournal(concat(gCr, '(*Satisfiable.*)'), TRUE, FALSE);
           CreateInterpretationList;

           if MEMBER(gApplication.fLastCommand, TShapeClearCommand) then
            gApplication.CommitLastCommand; {the idea here is that the}
     {							user may try to clear the previous drawing, but it is not really}
     {							cleared until committed}

           drawable := TRUE;

           WriteInterpretationList(drawable);

           if drawable then
            begin
            if (fDeriverDocument.fShapeList.fSize < 2) then
            begin
            fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation drawn.*)'), TRUE, FALSE);
            ConstructDrawing;
            end
            else
            fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation can be drawn, if the existing drawing is cleared.*)'), TRUE, FALSE);

            end
           else
            fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation not drawable.*)'), TRUE, FALSE);
          end;
         valid:
          fDeriverDocument.WriteToJournal(concat(gCr, '(*Not satisfiable.*)'), TRUE, FALSE);

         notknown:
          fDeriverDocument.WriteToJournal(concat(gCr, '(*Not known whether satisfiable.*)'), TRUE, FALSE);

         otherwise
        end;
       end;


*/



// Check if file is dirty.
// If so, prompt for save/don't save/cancel save decision.
public boolean okToAbandon() {

    if (!fDeriverDocument.isDirty()) {
     return true;
      }


  int value =  JOptionPane.showConfirmDialog(this, "Save changes?",
                                                                  "Deriver", JOptionPane.YES_NO_CANCEL_OPTION) ;

  switch (value) {
     case JOptionPane.YES_OPTION:
       // Yes, please save changes
       return saveFile();
     case JOptionPane.NO_OPTION:
       // No, abandon edits; that is, return true without saving
       return true;
     case JOptionPane.CANCEL_OPTION:
     default:
       // Cancel the dialog without saving or closing
       return false;
  }
}

protected void produceSatisfiableOutput(int argtype, TTestNode aTestRoot /*, ActionListener l*/ ){

 // used to try to call listener from thread when finished


  String outputStr="";

/*if (l!=null){
  ActionEvent e;
  l.actionPerformed(e);
} */


  switch (argtype){

       case TTestNode.valid:
         outputStr=strCR+"(*Not satisfiable.*)";
         break;

       case TTestNode.notValid:
         outputStr=strCR+"(*Satisfiable.*)";

         TTestNode openNode=aTestRoot.aNodeOpen();   // trying to avoid gOpenNode
         if (openNode!=null){
           ArrayList interpretationList= openNode.createInterpretationList();

         TFlag drawable=new TFlag(false);

         outputStr+=interpretationListToString(interpretationList,drawable);

         if (drawable.getValue()) {
           if (fDeriverDocument.fShapePanel.drawingIsClear()){
             if (freeInterpretFreeVariables(interpretationList)){ //the formulas may have free variables, this assigns them
               //to arbitrary cnstants
               outputStr+=strCR + "(*Interpretation drawn.*)";

               fRightTabbedPane.setSelectedIndex(1);

               fDeriverDocument.constructDrawing(interpretationList);
             }
               else
                 outputStr+=strCR+"(*Interpretation cannot be drawn,"
                 +" cannot interpret all the free variables.*)";


             /*constructDrawing()*/
           }
           else
             outputStr+=strCR+"(*Interpretation can be drawn,"
             +" if the existing drawing is cleared.*)";


         }
         else
            outputStr+=strCR+"(*Interpretation not drawable.*)";

         }
         break;



         /*

            begin
                fDeriverDocument.WriteToJournal(concat(gCr, '(*Satisfiable.*)'), TRUE, FALSE);
                CreateInterpretationList;

                if MEMBER(gApplication.fLastCommand, TShapeClearCommand) then
                 gApplication.CommitLastCommand; {the idea here is that the}
          {							user may try to clear the previous drawing, but it is not really}
          {							cleared until committed}

                drawable := TRUE;

                WriteInterpretationList(drawable);

                if drawable then
                 begin
                 if (fDeriverDocument.fShapeList.fSize < 2) then
                 begin
                 fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation drawn.*)'), TRUE, FALSE);
                 ConstructDrawing;
                 end
                 else
                 fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation can be drawn, if the existing drawing is cleared.*)'), TRUE, FALSE);

                 end
                else
                 fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation not drawable.*)'), TRUE, FALSE);
               end;


      }
     */
       case TTestNode.notKnown:
         outputStr=strCR+"(*Not known whether satisfiable.*)";
         break;


     }
  fDeriverDocument.writeToJournal(outputStr, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
}

public void recordPreferences(){
  Preferences userPreferences = Preferences.userNodeForPackage(this.getClass());
       String currentHome=(fFileChooser.getCurrentDirectory()).getPath();

     if (currentHome!=null){
       userPreferences.remove("Home");
       userPreferences.put("Home",currentHome);
    }

    int rightMargin=fDeriverDocument.fProofPanel.fModel.getRightMargin();

    if (rightMargin!=fDefaultMargin){
      userPreferences.remove("rightMargin");
      userPreferences.remove("RightMargin");              //old Label no longer used
      userPreferences.putInt("rightMargin", rightMargin);
    }
}


private boolean selectionMade(){

  return
    (fJournalPane.getSelectionStart()<fJournalPane.getSelectionEnd());
  }




  boolean readSatisfiableInput(String inputStr, TTestNode aTestRoot){
       boolean wellFormed = true;
          int badChar=-1;
        ArrayList dummy=new ArrayList();

        ArrayList newValuation;

       StringTokenizer st = new StringTokenizer(inputStr, ",");

       while ((st.hasMoreTokens())&& wellFormed){

         inputStr = st.nextToken();

         if (inputStr != strNull) { // can be nullStr if they put two commas togethe,should just skip
           TFormula root = new TFormula();
           StringReader aReader = new StringReader(inputStr);

           newValuation = fDeriverDocument.fValuation;

           wellFormed = fDeriverDocument.getParser().wffCheck(root, newValuation,
               aReader);

           if (!wellFormed) {
             fDeriverDocument.writeToJournal(
                 "(*You need to supply a list of well formed formulas"
                 + " separated by commas. Next is what the parser has to"
                 + " say about your errors.*)"
                 + strCR
                 + fDeriverDocument.getParser().fCurrCh + TConstants.fErrors12
                 + fDeriverDocument.getParser().fParserErrorMessage,
                 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
           }
           else {

             badChar = fDeriverDocument.getParser().badCharacters(root);

             if (badChar == TParser.kNone) {
               aTestRoot.addToAntecedents(root);
               fDeriverDocument.fValuation = newValuation;
                   /*notice here that we use only the last valuation for
                the whole thing */
             }
             else {

               writeBadCharacterErrors(badChar);
               return
                 false;       // it is well formed, but we cannot deal with, say, '='
             }
           }
         }
      //   System.out.print(inputStr + " ");
       }

    return
        wellFormed;
}


void setUpActionsMenu(){  //startProof always enables as can start from nothing

  if (selectionContainsModal())
    startProofMenuItem.setEnabled(false);
  else
    startProofMenuItem.setEnabled(true);


  if (selectionMade()){
      doCommandMenuItem.setEnabled(true);
      startTreeMenuItem.setEnabled(true);
      startLambdaMenuItem.setEnabled(true);
   }
   else{
     doCommandMenuItem.setEnabled(false);
     startTreeMenuItem.setEnabled(false);
     startLambdaMenuItem.setEnabled(false);
  }
}

void setUpEditMenu(){
  if (selectionMade()){
    fCutAction.setEnabled(true);
    fCopyAction.setEnabled(true);

  }
  else{
    fCutAction.setEnabled(false);
    fCopyAction.setEnabled(false);
  }

 if (textInJournal())
   fSelectAllAction.setEnabled(true);
 else
   fSelectAllAction.setEnabled(false);
}


void setUpGamesMenu(){              //CHECK THIS, PROBABLY WANT TO HAVE ONLY ONE INSTANCE OPEN AT A TIME

  /*

if  (TPredGamesQuiz.fNumOpen==0)
  temp7MenuItem.setEnabled(true);
else
  temp7MenuItem.setEnabled(false);

if  (TProofQuiz.fNumOpen==0)
  temp8MenuItem.setEnabled(true);
else
  temp8MenuItem.setEnabled(false);

*/


}


void setUpMenus(){  // we listen for a mouse press on the menu, then enable or disable items (key equivalents?)
                    //DON'T THINK ANYTHING CALLS THIS

  setUpFileMenu();
  setUpEditMenu();
  setUpHTMLMenu();
  setUpSemanticsMenu();
  setUpActionsMenu();
  setUpGamesMenu();
}


void setUpHTMLMenu(){// we don't mind them editing live text, so long as we know what mouse click means


      fontMenuItem.setEnabled(true/*fJournalEditable*/);
       insertImageMenuItem.setEnabled(true);
       insertLinkMenuItem.setEnabled(true);

       eKitMenuItem.setEnabled(true);
    }



// we listen for a mouse press on the menu, then enable or disable items

void setUpFileMenu(){

      saveMenuItem.setEnabled(fDeriverDocument.isDirty()); // only allow save if needed, 'save as' always possible

   }


void setUpSemanticsMenu(){

   valuationMenuItem.setEnabled((fDeriverDocument.fValuation!=null)&&((fDeriverDocument.fValuation.size()!=0)));

   interpretationMenuItem.setEnabled(!fDeriverDocument.fShapePanel.getSemantics().universeEmpty());

   boolean goodToGo=false;

   if (selectionMade())
     goodToGo=true;
   else{
     if (TPreferences.fReadFromClipboard){
       String testStr=readDualSource(TUtilities.logicFilter);
       if ((testStr!=null)&&testStr.length()!=0)
         goodToGo=true;
     }
   }

   if (goodToGo)
     goodToGo=!selectionContainsModal();      // this does not catch input from clipboard

   trueMenuItem.setEnabled(goodToGo);
   satisfiableMenuItem.setEnabled(goodToGo);  //temp May 05
   symbolizeMenuItem.setEnabled(goodToGo);
   toEnglishMenuItem.setEnabled(goodToGo);

    endorseMenuItem.setEnabled(goodToGo);
    denyMenuItem.setEnabled(goodToGo);
}


  //Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
      super.processWindowEvent(e);
      if (e.getID() == WindowEvent.WINDOW_CLOSING) {
        /*quitMenuItem_actionPerformed(null);*/
        closeBrowser(); //April O4
      }
    }

    String readDualSource(int filter){
   /*This reads the journal selection and, if there is none, the system clipboard.
    This is so the User can select and copy text in a real browswer and have us
    process it.*/

      String aString=TSwingUtilities.readSelectionToString(fJournalPane,filter);

      if (((aString==null)||(aString.length()==0))
          &&TPreferences.fReadFromClipboard){     //defaults to clipboard if no selection
        JTextPane aPane = new JTextPane();
        aPane.setEditorKit(fEditorKit);     // we need it to be html

        aString=TSwingUtilities.readSystemClipBoardToString(aPane,filter);

      }

      if (aString==null)
            return null;

        if (aString.length()==0)
            return null;


     return
         aString;

    }

    boolean selectionContainsModal(){

    String aString=TSwingUtilities.readSelectionToString(fJournalPane,TUtilities.defaultFilter);

          if ((aString!=null)&&(aString.length()!=0)){

            for (int i=0;i<aString.length();i++){
              if (TParser.isModalPossibleCh(aString.charAt(i))||
                  TParser.isModalNecessaryCh(aString.charAt(i)))



              return
                  true;
            }
          }



    return
        false;
    }


    StringReader readSelection(int filter){  //not dual ie not from clipboard


        String aString=TSwingUtilities.readSelectionToString(fJournalPane,filter);

        if (aString==null)
            return null;

        if (aString.length()==0)
            return null;

        return
              new StringReader(aString);
      }

  /*
      StringReader readSelection(boolean useFilter,String regEx, String replacement){


         String aString=TUtilities.readSelectionToString(fJournalPane,useFilter,regEx,replacement);

         if (aString==null)
             return null;

         if (aString.length()==0)
             return null;

         return
               new StringReader(aString);
       }

  */


    void saveAsMenuItem_actionPerformed(ActionEvent e) {
     //Handle the File|Save As menu item.
     boolean HTML=true;

     saveFileAs(!HTML);
   }


  TDocState docStateForSave(){
    Dimension browserSize=this.getSize();

    int horizSplit = paletteTextSplitPane.getDividerLocation();
    int vertSplit = journalProofSplitPane.getDividerLocation();

   // boolean template =;

    //(JRadioButtonMenuItem)propMenuItem).setSelected


    return
        new TDocState(browserSize,
                      horizSplit,
                      vertSplit,
                      fDeriverDocument.fProofPanel.getTemplate(),
                      fRightTabbedPane.getSelectedIndex(),
                      propMenuItem.isSelected(),
                      fJournalEditable);

  }

  void restoreDocState(TDocState theState){
    if (theState!=null){

      this.setSize(theState.getBrowserSize());

      int divider=theState.getHDivider();
      if (divider>-1)
         paletteTextSplitPane.setDividerLocation(divider);

       divider=theState.getVDivider();
       if (divider>-1)
          journalProofSplitPane.setDividerLocation(divider);

      ((JRadioButtonMenuItem)propMenuItem).setSelected(theState.getPropLevel());

       fJournalEditable=theState.getEditable();

       fRightTabbedPane.setSelectedIndex(theState.getTabIndex());
    }

}

/************************************* Action Methods ******************************************************/


private Action getActionByName(String name) {
       return (Action)(fActions.get(name));
   }


  //Help | About action performed
protected void helpMenuAbout_actionPerformed(ActionEvent e) {
    AboutBox dlg = new AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }


protected void newBrowserMenuItem_actionPerformed(ActionEvent e) {  //new command?

    fApplication.createDocument(TPreferences.fParser/*fDeriverDocument.fParserName*/);  // document->browser, of same type as us
  }


protected  void newFileMenuItem_actionPerformed(ActionEvent e) {
// Handle the File|New menu item.
  if (okToAbandon()) {
     // clears the text of the TextArea
     fJournalPane.setText("");


     fJournalEditable=true;   // when they open a new file they should be able to edit it
     ((JRadioButtonMenuItem)editTextMenuItem).setSelected(true); // (even if the menu did say not)

     fUndoManager.discardAllEdits();
     fUndoAction.updateUndoState();
     fRedoAction.updateRedoState();

     //need to clear the drawing

     fDeriverDocument.clearProofAndDrawing();

    // clear the current filename and set the file as clean:
    fCurrFileName = null;
    setDirty(false);

    setWindowTitle();

    statusBar.setText(" ");
  }
}

void openFileMenuItem_actionPerformed(ActionEvent e) {
openFile();
}


protected void quitMenuItem_actionPerformed(ActionEvent e) {

   while ((fApplication.getFirstBrowser()).closeBrowser()) ; //This closes or abandons
}



 void writeBadCharacterErrors(int kind){
   switch (kind) {

     case TParser.kEquality:
       fDeriverDocument.writeToJournal("(*Sorry, the semantics for = has not yet been implemented.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
       ;
   break;

     case TParser.kUnique:
       fDeriverDocument.writeToJournal("(*Sorry, the semantics for "
                                   + chUnique

                                   +" has not yet been implemented.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
   ;
     break;

       case TParser.kHighArity:
        fDeriverDocument.writeToJournal("(*Sorry, relations have to be of arity 2 or less.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
    ;
     break;

       case TParser.kCompoundTerms:
              fDeriverDocument.writeToJournal("(*Sorry, the semantics for compound terms has not yet been implemented.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
          ;
      break;

   }

 }






    protected void setAttributeSet(AttributeSet attr) {
       if (fSkipUpdate)
         return;

       int xStart = fJournalPane.getSelectionStart();
       int xFinish = fJournalPane.getSelectionEnd();

    /*   if (!journalPane.hasFocus()) {
         xStart = fSelectionStart;
         xFinish = fSelectionFinish;
       }  */
       if (xStart != xFinish) {
         ((HTMLDocument)fDocument).setCharacterAttributes(xStart, xFinish - xStart,
           attr, false);
       }
       else {
         MutableAttributeSet inputAttributes =
           fEditorKit.getInputAttributes();
         inputAttributes.addAttributes(attr);
       }
     }

     public void setDirty(boolean dirty){
           fDeriverDocument.setDirty(dirty);

       }


  void setWindowTitle()
  // Set the title
  {String shortFileName="Untitled";
    int pos = 0;

    if (fCurrFileName!=null)
      {pos = fCurrFileName.lastIndexOf("/");
        shortFileName = (pos > 0) ? fCurrFileName.substring(pos + 1) :
            fCurrFileName;
      }

  setTitle(fApplication.APP_NAME+ fDeriverDocument.fParserName+               " ["+shortFileName+"]");
  }


  protected void showAttributes(int p) {
       fSkipUpdate = true;

       AttributeSet a = ((HTMLDocument)fDocument).getCharacterElement(p).getAttributes();

       String name = StyleConstants.getFontFamily(a);
       if (!fFontName.equals(name)) {
         fFontName = name;
        // m_cbFonts.setSelectedItem(name);  toolbar
       }
       int size = StyleConstants.getFontSize(a);
       if (fFontSize != size) {
         fFontSize = size;
        // m_cbSizes.setSelectedItem(Integer.toString(m_fontSize));
       }
 /*      boolean bold = StyleConstants.isBold(a);
       if (bold != m_bBold.isSelected())
         m_bBold.setSelected(bold);
       boolean italic = StyleConstants.isItalic(a);
       if (italic != m_bItalic.isSelected())
         m_bItalic.setSelected(italic);  */    //toolbar stuff

       fSkipUpdate = false;
     }





  static public void showError(Exception ex, String message) {//Displays error messages in dialogs
       ex.printStackTrace();
       JOptionPane.showMessageDialog(/*this*/ null,
       message, /*fApplication.APP_NAME*/"Deriver",
       JOptionPane.WARNING_MESSAGE);
       }

 boolean textInJournal(){
   HTMLDocument htmlDocument = (HTMLDocument)fJournalPane.getDocument();
   return
       (htmlDocument.getEndPosition().getOffset()>1);

 }


 private void simplifyFileMenu(){

        fileMenu.remove(newBrowserMenuItem);
        fileMenu.remove(newFileMenuItem);
        fileMenu.remove(openFileMenuItem);
        fileMenu.remove(closeBrowserMenuItem);
        fileMenu.remove(saveMenuItem);
        fileMenu.remove(saveAsMenuItem);
       // fileMenu.addSeparator();
        fileMenu.remove(openWebPageMenuItem);
        fileMenu.remove(saveAsHTMLItem);
       // fileMenu.addSeparator();
        fileMenu.remove(pageLayoutMenuItem);
        fileMenu.remove(printMenuItem);
        //fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);

 }

 void case1(boolean noGames){
   simplifyFileMenu();

jMenuBar1.remove(HTMLMenu);
   if(noGames)
     jMenuBar1.remove(jMenuGames);

actionsMenu.remove(startProofMenuItem);
actionsMenu.remove(startTreeMenuItem);
actionsMenu.remove(startLambdaMenuItem);

semanticsMenu.removeAll();

{
  semanticsMenu.add(symbolizeMenuItem);
  semanticsMenu.add(toEnglishMenuItem);
  semanticsMenu.addSeparator();

  semanticsMenu.add(propMenuItem);
  semanticsMenu.add(predMenuItem);

  ((JRadioButtonMenuItem)propMenuItem).setSelected(true);
}


fPalette.setText(strCR+

                 "If you need some logical symbols, copy and paste any of these "+
                 fDeriverDocument.fBasicPalette+
                 " (or drag and drop them.) ");

fRightTabbedPane.setVisible(false);

// May07 helpMenu.remove(preferencesMenuItem);


 }

 void case3(boolean noStart){
   simplifyFileMenu();

   jMenuBar1.remove(HTMLMenu);

   if (noStart)
     actionsMenu.remove(startProofMenuItem);
     actionsMenu.remove(startTreeMenuItem);
     actionsMenu.remove(startLambdaMenuItem);

  // semanticsMenu.remove(valuationMenuItem);
   semanticsMenu.remove(interpretationMenuItem);
   //semanticsMenu.remove(propositionsMenuItem);
   // semanticsMenu.addSeparator();
   //semanticsMenu.remove(trueMenuItem);
   //semanticsMenu.remove(satisfiableMenuItem);
   // semanticsMenu.addSeparator();


   jMenuGames.removeAll();
      jMenuGames.add(propGamesSubMenu);


 ((JRadioButtonMenuItem)propMenuItem).setSelected(true);

 fPalette.setText(strCR+

                  "Use any of these "+
                  fDeriverDocument.fBasicPalette+
                  " if you need them. ");

 fRightTabbedPane.setVisible(false);

// Mayo7 helpMenu.remove(preferencesMenuItem);


 }

 public void simplifyInterface(int howSimple){
   boolean noGames=true;
   boolean noStart=true;

   switch (howSimple) {

     case 1:    // for the first tutorial, just symbolizing propositions

       case1(noGames);

       break;

     case 2:    // for the third tutorial,  symbolizing propositions and truth tables

      case1(!noGames);

      jMenuGames.removeAll();
      jMenuGames.add(propGamesSubMenu);


/*     simplifyFileMenu();

     jMenuBar1.remove(HTMLMenu);

     actionsMenu.remove(startProofMenuItem);

     //semanticsMenu.remove(valuationMenuItem);
     semanticsMenu.remove(interpretationMenuItem);
     //semanticsMenu.remove(propositionsMenuItem);
     // semanticsMenu.addSeparator();
     //semanticsMenu.remove(trueMenuItem);
     semanticsMenu.remove(satisfiableMenuItem);
     // semanticsMenu.addSeparator();

     jMenuGames.remove(examsSubMenu);
     jMenuGames.remove(predGamesSubMenu);


   ((JRadioButtonMenuItem)propMenuItem).setSelected(true);

   fPalette.setText(strCR+

                      "If you need some logical symbols, copy and paste any of these "+

                      " \u223C  \u2227  \u2228  \u2283  \u2261 or drag and drop them.");

     fRightTabbedPane.setVisible(false);  */
     break;

   case 3:    // for the fourth tutorial,  satisfiable, propgames, quiz2

   case3(noStart);

   break;

 case 4:    // for the fifth tutorial,  derivations
   case3(!noStart);
   fRightTabbedPane.removeTabAt(1); //remove the interpretation
   fRightTabbedPane.setVisible(true);
/*
 simplifyFileMenu();

 jMenuBar1.remove(HTMLMenu);

//actionsMenu.remove(startProofMenuItem);

//semanticsMenu.remove(valuationMenuItem);
 semanticsMenu.remove(interpretationMenuItem);
//semanticsMenu.remove(propositionsMenuItem);
// semanticsMenu.addSeparator();
//semanticsMenu.remove(trueMenuItem);
//semanticsMenu.remove(satisfiableMenuItem);
// semanticsMenu.addSeparator();

 jMenuGames.remove(examsSubMenu);
     jMenuGames.remove(predGamesSubMenu);



 ((JRadioButtonMenuItem)propMenuItem).setSelected(true);

 fPalette.setText(strCR+

                  "If you need some logical symbols, copy and paste any of these "+

                  " \u223C  \u2227  \u2228  \u2283  \u2261 \u2234 or drag and drop them.");

//fRightTabbedPane.setVisible(false);
 fRightTabbedPane.removeTabAt(1); //remove the interpretation */
 break;

     default:
         break;

   }
 }
 public void writeHTMLToJournal(String message, boolean append){
         HTMLDocument htmlDocument = (HTMLDocument)fJournalPane.getDocument();

           if (fEditorKit!=null){ //should always be non null anyway

             try {

               int position;

               if (append)
                 position=htmlDocument.getEndPosition().getOffset()-1;/*fJournalPane.getText().length();*/
               else
                 position=fJournalPane.getCaretPosition();



               fEditorKit.insertHTML(htmlDocument, position,
                                     message,0,0,HTML.Tag.BR);  //used to be HTML

             //  fEditorKit.

             }
             catch (Exception except) {

               except.printStackTrace();
             }
           }


 }


int findMarker(){
 /*want to find the first marker, usually >, after the selection*/

 /*WARNING You have to go  careful here because the underlying text is html, so
 if the journal contains 'Cat' and you String text=fJournalPane.getText();
you are liable to get '<html>Cat</html>' back, which is more than the
3 characters you started with
Go via the document*/

 String text;
 int caretPos = fJournalPane.getSelectionEnd(); //if there isn't one it's dot which is the old one
 int searchStart=0;

 if (caretPos!=0)
   searchStart=caretPos-1; //can be EOF, and we want to find that


 Document theDoc=fJournalPane.getDocument();

 try{text=theDoc.getText(0,theDoc.getLength());
 }
 catch (BadLocationException ex){
  return
      -1;
 }
return
     text.indexOf(chInsertMarker,searchStart); //can be EOF, and we want to find that
 }

/* procedure FIndMarker; {looks after selection-1 you don't want to find it before the selection,}
{                            but if it is eof and so too is the marker you want to remove the}
{                            marker}

  var
   theseChars: charshandle;
   found: BOOLEAN;

 begin
  found := FALSE;
  theseChars := TEGetText(fJournalTEView.fHTE);
  endIndex := fJournalTEView.fHTE^^.teLength;

  MoveHHi(Handle(theseChars)); {not needed}
  HLock(Handle(theseChars));

  i := endSelection;

  if (i <> 0) then
   i := i - 1; {covers eof case}

  while (i < endIndex) and not found do
   begin
    if theseChars^^[i] = chInsertMarker then
     begin
      found := TRUE;
      if found then
      beginSelection := i;
     end;
    i := i + 1;

   end;

  HUnLock(Handle(theseChars));
  if found then
   endSelection := i;
 end; */



       public void writeToJournal(String message, boolean highlight,boolean toMarker){

           int newCaretPosition = fJournalPane.getSelectionEnd(); //if there isn't one it's dot which is the old one

           int messageLength = message.length();

           if (messageLength>0) {

             fJournalPane.setSelectionStart(newCaretPosition);
             fJournalPane.setCaretPosition(newCaretPosition);    //leave existing selection and do everything after

             if (toMarker){                   //looking after 1 before caret -- caret may be end and we want to remove marker on end
                int posMarker=findMarker();
                if(posMarker!=-1){
                  fJournalPane.setSelectionStart(posMarker);
                  fJournalPane.setSelectionEnd(posMarker+1);

                }
                }


             fJournalPane.replaceSelection(message);

             if (highlight) {
               fJournalPane.setSelectionStart(newCaretPosition);
               fJournalPane.setSelectionEnd(newCaretPosition+messageLength);

             }

           }
        }




/*
             procedure TDeriverDocument.WriteToJournal (message: str255; highlight: BOOLEAN; toMarker: BOOLEAN);

          var
           newText: ptr;
           messagelength: LONGINT;
           itsText: Handle;
           endSelection, beginSelection, i, endIndex: LONGINT;

          procedure FIndMarker; {looks after selection-1 you don't want to find it before the selection,}
        {                            but if it is eof and so too is the marker you want to remove the}
        {                            marker}

           var
            theseChars: charshandle;
            found: BOOLEAN;

          begin
           found := FALSE;
           theseChars := TEGetText(fJournalTEView.fHTE);
           endIndex := fJournalTEView.fHTE^^.teLength;

           MoveHHi(Handle(theseChars)); {not needed}
           HLock(Handle(theseChars));

           i := endSelection;

           if (i <> 0) then
            i := i - 1; {covers eof case}

           while (i < endIndex) and not found do
            begin
             if theseChars^^[i] = chInsertMarker then
              begin
               found := TRUE;
               if found then
               beginSelection := i;
              end;
             i := i + 1;

            end;

           HUnLock(Handle(theseChars));
           if found then
            endSelection := i;
          end;

         begin
          messagelength := length(message);
          if messagelength > 0 then
           begin
            newText := Pointer(ord(@message) + 1);

            if (fJournalTEView.fHTE^^.teLength + messagelength) > maxInt then
             FailOSErr(memFullErr)
            else
             begin
              endSelection := fJournalTEView.fHTE^^.selEnd;
              beginSelection := endSelection;

              if toMarker then
               FIndMarker;

              if fJournalTEView.Focus then
               ;

              TESetSelect(beginSelection, endSelection, fJournalTEView.fHTE);

              if beginSelection <> endSelection then
               begin
               TEDelete(fJournalTEView.fHTE);
               endSelection := beginSelection;
               end;

              TEInsert(newText, messagelength, fJournalTEView.fHTE);

              if highlight then
               TESetSelect(endSelection, endSelection + messagelength, fJournalTEView.fHTE);

              if fJournalTEView.Focus then
               ;

              fJournalTEView.SynchView(TRUE); {true means redraw}

             end;
           end;
         end;


     */





void statusToggle(String label){
  String oldText=statusBar.getText();
  Thread worker = new Thread(){ public void run (){}};
  statusBar.setText(label);
}


public void writeOverJournalSelection(String message){

   if (message.length()>0)
     fJournalPane.replaceSelection(message);
}


/*

             procedure THandleStream.WriteOverSelection (outText: TEhandle);
          var
           size_of_text, selectionLength: longint;
           error: boolean;
           newText: ptr;
         begin
          selectionLength := outText^^.selEnd - outText^^.selStart;

          SELF.SetForReadingFrom;

          size_of_text := SELF.fSize;

          if (size_of_text > 0) then
           begin
            if (outText^^.teLength - selectionLength + size_of_text) > maxInt then
             FailOSErr(paramErr)   (*check this error code MF*)
            else
             begin
              if selectionLength <> 0 then
               TEDelete(outText);

              SELF.SetPosition(0);

              HLock(Handle(SELF.fHandle));

              newText := SELF.fHandle^;

              TEInsert(newText, size_of_text, outText);

              HUnLock(Handle(SELF.fHandle));
             end;
           end;
         end;



     */

/************************************ xml transformation *********************************************/

void xmlTransform(File input, ByteArrayOutputStream output) throws TransformerConfigurationException,
TransformerException{

    StreamSource stylesheet;//=new StreamSource("hell");
    
    try{
    	stylesheet = new StreamSource(TBrowser.class.getResourceAsStream("XLSTStylesheet.xsl"));
      }catch (Exception e) {
    	  stylesheet= null;
    	  return;
            }
      
      
	TransformerFactory transFact = TransformerFactory.newInstance();
    Transformer trans = transFact.newTransformer(stylesheet);
      
    
    trans.transform( new StreamSource(input),
            new StreamResult(/*System.out*/ output));
    /*   
    trans.transform( new StreamSource(input),
            new StreamResult(System.out));  debugging*/
    
}




/************************************ File Methods *********************************************/



// Open named file; read text from file into journalPane; read XML data to construct drawing
 //report to statusBar.


 /* The filing has proved to be very awkward thus far.  The journal needs to be html, and saved as
  html. Then Users can open it in browsers, edit elsewhere etc. It also means that this program
  can open text, html, etc. which has been created elsewhere. We also need to save the drawing
  which essentially is a list of shapes (and we could also save the document state etc). XML is the
  obvious way to do this either using the XML bean encoder and decoder, or a SAX parser etc. Then the
  html and XML need to be combined. In the old Macintosh world we might have had a data fork and
  a resource fork and put one in each. But here we are facing a flat file.

  What we are going to do for the moment is to save everything into one XML file. Also we will allow
  the user to save the Journal as HTML. Then, when opening, we'll look to see if the file is XML.
  If so we'll try to create our document. If not we'll try to read it into the journal as
  text (or html).

  */


  void openFile() {

/*There is a trap here. All the objects are created by name. So if you change the name of the
 objects or their saved fields in the source code, the old saved files won't work. You need to use a file version
 number and sort all this out. Be aware also that the obfuscator does not strip certain chosen
 names. Make sure the obfuscator does not strip your new names.*/

    String fileName=null;

    if (!okToAbandon())           // check is the current file is dirty
      return;

    if (!(fFileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION))
      return;                    // bale out if they cancel

    fileName=fFileChooser.getSelectedFile().getPath();

    try {

    	File file = new File(fileName);

    	fBadXML=false;

        BadXMLHandler tryTextInstead = new BadXMLHandler();

        XMLDecoder d = new XMLDecoder( new BufferedInputStream(
                              new FileInputStream(file)),null,tryTextInstead);


         /* The XML decoder is robust and tries to recover from errors. But anything
      written by our Save routines and the XML encoder will be in perfect form. So if
      there's an error reported then either the file is not XML (it could be HTML or
      text) or a user has altered it. Either way we are better off not trying to recover
      and create beans. Instead we should just take it as text. So we'll catch the errors
      and not let the decoder try to recover.

      And it starts reading immediately, so will report straight away.

      */


      if (fBadXML){
        d.close();
        BufferedReader inReader = new BufferedReader(new FileReader(file));
        fJournalPane.read(inReader,null);
        inReader.close();
      }
      else {   	  
 /*what we need to do now is to read the version and set the transform filter
  * flag for old versions (XLST transform of XML)   	  
  */
       Integer version=new Integer(0); 	  
       Object unknownObject;
       boolean transform=false;

       try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}

       if (unknownObject!=null){   //expecting version
          if (unknownObject instanceof Integer)
        	  version=(Integer)unknownObject;
       	}
       if (version<2)
    	   transform=true;
       
       d.close(); //stop reading, we'll read the version again	  
   
       openFileHelper(fileName,transform);
      }
      }
    catch (IOException e) {
    	statusBar.setText("Error opening "+fileName);
    	}
    }
 
  void openFileHelper(String fileName,boolean transform){  
  /*we have different file formats, so have to go careful with our casts */
	  	  
	  try {

		  File file = new File(fileName);
	  
		  BadXMLHandler tryTextInstead = new BadXMLHandler();

	      XMLDecoder d = new XMLDecoder( new BufferedInputStream(
	                              new FileInputStream(file)),null,tryTextInstead);
 
	  //we know the XML is good
	      
	     // File output = new File();
	      
	      if (transform){
	      
	    	  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	      
	      
	        //now we need this as a stream
	    	  try {xmlTransform(file,outputStream);} catch (TransformerConfigurationException e) {}catch
	                              (TransformerException ex) {return;}
	      
	    	  outputStream.close();
	      
	    /*	  String transOutput=outputStream.toString();
	      
	    	  byte [] s=outputStream.toByteArray(); */
	    	  
	    	  d = new XMLDecoder( new BufferedInputStream(
		    		  new ByteArrayInputStream(outputStream.toByteArray())),	    		  
		    		  null,tryTextInstead);	      
	      }
	      else
	          d = new XMLDecoder( new BufferedInputStream(
                      new FileInputStream(file)),null,tryTextInstead);
  
	  Integer version=new Integer(0);  /*each document has a (file format) version on it
and so does the Browser, later we will check here if there are changes and act appropriately. Update
or save in the old version*/

String textStr=null;
ArrayList shapeList=new ArrayList();
TProofListModel aModel= null;
TProofTableModel aTableModel= null;
TTreeTableModel aTreeModel= null;
// int [] colWidths=null;
TDocState docState= null;
/*Those are the objects we expect to read, in that order, but some early versions may omit some */


Object unknownObject;

try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}

if (unknownObject!=null)   //expecting version
   {if (unknownObject instanceof Integer)
      version=(Integer)unknownObject;
   else if (unknownObject instanceof String)
      textStr=(String)unknownObject;
   else if (unknownObject instanceof ArrayList)
      shapeList=(ArrayList)unknownObject;
   else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
             unknownObject instanceof us.softoption.proofwindow.TProofListModel))
      aModel=(TProofListModel)unknownObject;
   else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
       aTableModel=(TProofTableModel)unknownObject;
    else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
      aTreeModel=(TTreeTableModel)unknownObject;

   else if (unknownObject instanceof TDocState)
      docState=(TDocState)unknownObject;
}      // This is belt and braces just to deal with earlier incompatible file versions


try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}
if (unknownObject!=null)  // expecting String
        {if (unknownObject instanceof Integer)
           version=(Integer)unknownObject;
        else if (unknownObject instanceof String)
           textStr=(String)unknownObject;
        else if (unknownObject instanceof ArrayList)
           shapeList=(ArrayList)unknownObject;
        else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
                  unknownObject instanceof us.softoption.proofwindow.TProofListModel))
           aModel=(TProofListModel)unknownObject;
        else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
            aTableModel=(TProofTableModel)unknownObject;
         else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
      aTreeModel=(TTreeTableModel)unknownObject;
        else if (unknownObject instanceof TDocState)
           docState=(TDocState)unknownObject;
}

 try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}
  if (unknownObject!=null) // expecting ArrayList
          {if (unknownObject instanceof Integer)
             version=(Integer)unknownObject;
          else if (unknownObject instanceof String)
             textStr=(String)unknownObject;
          else if (unknownObject instanceof ArrayList)
             shapeList=(ArrayList)unknownObject;
          else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
                    unknownObject instanceof us.softoption.proofwindow.TProofListModel))
             aModel=(TProofListModel)unknownObject;
          else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
              aTableModel=(TProofTableModel)unknownObject;
           else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
      aTreeModel=(TTreeTableModel)unknownObject;
          else if (unknownObject instanceof TDocState)
             docState=(TDocState)unknownObject;
}

try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}
  if (unknownObject!=null) //expecting TProofList
          {if (unknownObject instanceof Integer)
             version=(Integer)unknownObject;
          else if (unknownObject instanceof String)
             textStr=(String)unknownObject;
          else if (unknownObject instanceof ArrayList)
             shapeList=(ArrayList)unknownObject;
          else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
                    unknownObject instanceof us.softoption.proofwindow.TProofListModel))
             aModel=(TProofListModel)unknownObject;
          else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
              aTableModel=(TProofTableModel)unknownObject;
           else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
      aTreeModel=(TTreeTableModel)unknownObject;
          else if (unknownObject instanceof TDocState)
             docState=(TDocState)unknownObject;
}
  
  try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}
  if (unknownObject!=null) //expecting TProofTable
          {if (unknownObject instanceof Integer)
             version=(Integer)unknownObject;
          else if (unknownObject instanceof String)
             textStr=(String)unknownObject;
          else if (unknownObject instanceof ArrayList)
             shapeList=(ArrayList)unknownObject;
          else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
                    unknownObject instanceof us.softoption.proofwindow.TProofListModel))
             aModel=(TProofListModel)unknownObject;
          else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
              aTableModel=(TProofTableModel)unknownObject;
           else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
      aTreeModel=(TTreeTableModel)unknownObject;
          else if (unknownObject instanceof TDocState)
             docState=(TDocState)unknownObject;
}  

/*    TProofListModel aModel = (TProofListModel)(d.readObject());  /* trouble is Aug 06, I have changed the
package "proofwindow" to "proofs" And this messes up the names of eg <object class="proofwindow.TProofListModel">
    <object class="proofwindow.TProofline"/>
*/
try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}
if (unknownObject!=null) //expecting TTreeModel
  {if (unknownObject instanceof Integer)
     version=(Integer)unknownObject;
  else if (unknownObject instanceof String)
     textStr=(String)unknownObject;
  else if (unknownObject instanceof ArrayList)
     shapeList=(ArrayList)unknownObject;
  else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
            unknownObject instanceof us.softoption.proofwindow.TProofListModel))
     aModel=(TProofListModel)unknownObject;
  else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
      aTableModel=(TProofTableModel)unknownObject;
   else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
aTreeModel=(TTreeTableModel)unknownObject;
  else if (unknownObject instanceof TDocState)
     docState=(TDocState)unknownObject;
}

 try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}
     if (unknownObject!=null) //expecting array of col widths
             {if (unknownObject instanceof Integer)
                version=(Integer)unknownObject;
             else if (unknownObject instanceof String)
                textStr=(String)unknownObject;
             else if (unknownObject instanceof ArrayList)
                shapeList=(ArrayList)unknownObject;
             else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
                       unknownObject instanceof us.softoption.proofwindow.TProofListModel))
                aModel=(TProofListModel)unknownObject;
             else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
                 aTableModel=(TProofTableModel)unknownObject;
              else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
             aTreeModel=(TTreeTableModel)unknownObject;
      //     else if (unknownObject instanceof int [])
      //       colWidths=(int [])unknownObject;
             else if (unknownObject instanceof TDocState)
                docState=(TDocState)unknownObject;
       }



try {unknownObject=(d.readObject());} catch (ArrayIndexOutOfBoundsException e) {unknownObject=null;}
if (unknownObject!=null) //expecting TDocState
      {if (unknownObject instanceof Integer)
         version=(Integer)unknownObject;
      else if (unknownObject instanceof String)
         textStr=(String)unknownObject;
      else if (unknownObject instanceof ArrayList)
         shapeList=(ArrayList)unknownObject;
      else if ((unknownObject instanceof us.softoption.proofs.TProofListModel||
                unknownObject instanceof us.softoption.proofwindow.TProofListModel))
         aModel=(TProofListModel)unknownObject;
      else if (unknownObject instanceof us.softoption.proofs.TProofTableModel)
          aTableModel=(TProofTableModel)unknownObject;
       else if (unknownObject instanceof us.softoption.tree.TTreeTableModel)
      aTreeModel=(TTreeTableModel)unknownObject;
      else if (unknownObject instanceof TDocState)
         docState=(TDocState)unknownObject;
}


//     TDocState docState = (TDocState)(d.readObject());  //experiment

d.close();

if (docState!=null)
   restoreDocState(docState);  // get the panels the right sizes before populating

fJournalPane.setText(textStr); //could use a reader and read it

fJournalPane.setSelectionStart(0);   //new sept04  want scroll to beginning
fJournalPane.setSelectionEnd(0);   //new sept04

fDeriverDocument.fShapePanel.setShapeList(shapeList);

// fDeriverDocument.fProofPanel.reconstructProof(template.booleanValue(),aModel);

/* in this next bit we convert from the old ProofList to the new ProofTable */

if (aModel!=null&&aTableModel==null){
	aTableModel= new TProofTableModel();
	aTableModel.setHead(aModel.getHead());
	aTableModel.setTail(aModel.getTail());
	
}



if (aTableModel!=null){   // we'll only restore proof if one has been saved
  if (docState != null)
     fDeriverDocument.fProofPanel.reconstructProof(docState.getTemplate(),
        aTableModel);
  else
    fDeriverDocument.fProofPanel.reconstructProof(false, aTableModel);
}

if (aTreeModel!=null)
  fDeriverDocument.fTreePanel.reconstructTree(aTreeModel /*, colWidths*/);




/*This is the old code pre march 05. I don't think that the logic is correct. The base always
needs to be set relative to the file or where the file comes from. With this code, it can get set
relative to the old file.


URL baseForRelativeURLs=((HTMLDocument)fJournalPane.getStyledDocument()).getBase();

if (baseForRelativeURLs==null)  // set it to this file
  ((HTMLDocument)fJournalPane.getStyledDocument()).setBase(file.toURL());

*/
try {
URL baseForRelativeURLs=file.toURL();
((HTMLDocument)fJournalPane.getStyledDocument()).setBase(baseForRelativeURLs);

}catch (IOException e) {
    statusBar.setText("Error relativeURL "+fileName);
}


// Cache the currently opened filename for use at save time...
this.fCurrFileName = fileName;
// ...and mark the edit session as being clean
setDirty(false);


// Display the name of the opened directory+file in the statusBar.
statusBar.setText("Opened "+fileName);

setWindowTitle();

  } catch (IOException e) {
	statusBar.setText("Error opening "+fileName);
	}


}


//   catch (ClassNotFoundException cnfe) { /*need to handle this*/}

// Save current file; handle not yet having a filename; report to statusBar.

 /*We use the beans persistence mechanism and XML for this. Objects are used
  with default constructors, and then with public get and set methods for fields
  we need to save or recreate. Then an XML encoder and decoder is used for the writing
  and reading*/

  boolean saveFile() {

    // Handle the case where we don't have a file name yet.
    if (fCurrFileName == null) {
      boolean asHTML=true;
      return
         saveFileAs(!asHTML);
    }

    try {
      // Open a file of the current name.
      File file = new File (fCurrFileName);

      XMLEncoder os = new XMLEncoder(new BufferedOutputStream(
                              new FileOutputStream(file)));

      os.writeObject(new Integer(fFileFormat));     // file format version number

      os.writeObject(fJournalPane.getText());

      os.writeObject(fDeriverDocument.fShapePanel.getShapeList());    /*get the text working first*/

 //     os.writeObject(new Boolean(fDeriverDocument.fProofPanel.getTemplate()));  //not used

      os.writeObject(fDeriverDocument.fProofPanel.getModel());

      os.writeObject(fDeriverDocument.fTreePanel.getModel());  //experiment Feb 08

   //  os.writeObject(fDeriverDocument.fTreePanel.fTreeTableView.getColWidths());   // not using now Feb08

      os.writeObject(docStateForSave()); //experiment

      os.close();

       setDirty(false);

       URL baseForRelativeURLs=((HTMLDocument)fJournalPane.getStyledDocument()).getBase();

        if (baseForRelativeURLs==null)  // set it to this file
          ((HTMLDocument)fJournalPane.getStyledDocument()).setBase(file.toURL());


             // Display the name of the saved directory+file in the statusBar.
       statusBar.setText("Saved to " + fCurrFileName);

       setWindowTitle();

      return true;
    }
    catch (IOException e) {
      statusBar.setText("Error saving "+ fCurrFileName);
    }
    return false;
  }

  boolean saveFileAs(boolean asHTML) {
     boolean reallySave=false;
     String chosenPath=null;

     while (!reallySave){

     if (!(fFileChooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION))
        return
            false;                           // if they cancel we'll leave this whole routine

     else
        {
        chosenPath = fFileChooser.getSelectedFile().getPath();

        File file = new File(chosenPath);

        if (file.exists()) { // check whether they are trying to overwrite an existing file


           if (JOptionPane.showOptionDialog(null, "Overwrite existing file?",
                                      "Warning, file already exists",
                                      JOptionPane.YES_NO_OPTION,
                                      JOptionPane.WARNING_MESSAGE,
                                      null, new Object[] {"Cancel", "OK"}
                                      , "Cancel")==1)
            reallySave=true;  // they want to overwrite (if they don't we go round the whole loop

         }
        else
            reallySave=true;     // no existing file to worry about
        }
     }




     if (asHTML)
        return
           saveFileAsHTML(chosenPath);
     else {
       fCurrFileName = chosenPath;
       return
           saveFile();
     }

   }

   void saveMenuItem_actionPerformed(ActionEvent e) {
//Handle the File|Save menu item.
 saveFile();
   }


private boolean saveFileAsHTML(String path) {
  // called by SaveAs which does the file dialogs etc., do not call directly

     try {
       // Open a file of the current name.
       File file = new File (path);


       // Create an output writer that will write to that file.
       // FileWriter handles international characters encoding conversions.
      FileWriter out = new FileWriter(file);
       String text = fJournalPane.getText();
       out.write(text);

       out.close();
       //setDirty(false);

       // Display the name of the saved directory+file in the statusBar.
       statusBar.setText("Journal Saved As HTML to " + path);

       return true;
     }
     catch (IOException e) {
       statusBar.setText("Error saving "+ path);
     }
     return false;
   }

/************************************ End of File Methods  *************************************************************/


/*********************************** Methods involving threads ********************************************************/

/*not sure which class this belongs in*/

void stringSatisfiable(final String inStr){   //final so the thread can read it, but not change it

  String oldText=statusBar.getText();
  statusBar.setText("Working on Satisfiable...");
  statusBar.repaint();



  if (fSatisfiableThread!=null){         // we'll only do one of these at a time
    fSatisfiableThread.interrupt();
    fSatisfiableThread=null;
  }

  fSatisfiableThread= new Thread(){
    public void run(){


  //   String inputStr;
   final TFlag wellFormed= new TFlag(false);;


   final TTestNode aTestRoot = new TTestNode(fDeriverDocument.getParser(),null);  //does not initialize TreeModel

   TTreeModel aTreeModel= new TTreeModel(aTestRoot.fTreeNode);

   aTestRoot.fTreeModel=aTreeModel;                                  //Tree Model initialized now
  // aTes


  // aTestRoot.startSatisfactionTree();
  aTestRoot.initializeContext(aTreeModel);

   Runnable read = new Runnable(){
     public void run (){
       String selectionStr =inStr;// String selectionStr = readDualSource(TUtilities.logicFilter);  //leave March 06
       wellFormed.setValue(readSatisfiableInput(selectionStr, aTestRoot));

     }
   };

   try{
     SwingUtilities.invokeAndWait(read);        // don't want text changed while reading
   }
   catch (Exception ex) {
      wellFormed.setValue(false);
   }

 /* synchronized (fJournalPane){
    String selectionStr = readDualSource(TUtilities.logicFilter);
    wellFormed = readSatisfiableInput(selectionStr, aTestRoot);
  }  */


if (wellFormed.getValue()){
       TFormula.interpretFreeVariables(fDeriverDocument.fValuation, aTestRoot.fAntecedents);

       // check on what I think about surgery here

       final int argtype= nodeSatisfiable(aTestRoot,aTreeModel);

       Runnable write  = new Runnable(){
  public void run (){
    produceSatisfiableOutput(argtype, aTestRoot);

  }
};

try{
  SwingUtilities.invokeAndWait(write);        // don't want text changed while reading
}
catch (Exception ex) {
   ;
}


       if (TConstants.DEBUG){

         TTestDisplayTree aTestDisplayTree = new TTestDisplayTree(aTreeModel);
         aTestDisplayTree.display(); //not doing at present AUg04
       }

            }


    }};   // end of new Thread/ run

  fSatisfiableThread.start();

 statusBar.setText(oldText);
}





/*

    procedure TJournalWindow.SelectionSatisfiable;

    {check trim this}

      var
       notRefTo: string[1];
       tempCh, shouldbeaComma: CHAR;
       drawable, equalspresent, equals, compoundTerms, higharity: boolean;
       testformula: TFormula;
       newValuation: TList;
       oldend: integer;




 procedure InterpretFreeVariables (valuation: TList);

   var
    templist: TList;
    termForm, variForm: TFormula;

   procedure TreatEachFormula (item: TObject);

    var
     tempformula: TFormula;

    procedure Interpret (item: TObject);

     var
      aFormula: TFormula; {this has valuation info}

    begin
     if item <> nil then
      begin
       aFormula := TFormula(item);
       termForm.fInfo := aFormula.fInfo[1];
       variForm.fInfo := aFormula.fInfo[3];
       NewSubTermVar(tempformula, termForm, variForm);
      end;
    end;

   begin
    tempformula := TFormula(item);
    if valuation.fSize <> 0 then
     valuation.Each(Interpret);
    gTestroot.fSucceedent.InsertFirst(tempformula); {being used as temp}
    tempformula := nil;
   end;

  begin
   SupplyFormula(termForm);
   termForm.fKind := functor;
   SupplyFormula(variForm);
   variForm.fKind := variable;

   gTestroot.fAntecedents.Each(TreatEachFormula); {puts them in succ}
   gTestroot.fAntecedents.DeleteAll;
   templist := gTestroot.fAntecedents;
   gTestroot.fAntecedents := gTestroot.fSucceedent;
   gTestroot.fSucceedent := templist;
   templist := nil;

   termForm.DismantleFormula;
   variForm.DismantleFormula;

  end;

 begin
  StartSatisfactionTree; {initializes gTestroot}

  gIllformed := FALSE;
  equalspresent := FALSE;
  equals := FALSE;
  compoundterms := FALSE;
  oldend := gInputEnd;
  shouldbeaComma := chComma;

  if (gInputStart <> gInputEnd) then
   begin

    while not gIllformed and MoreListInput(gInputHdl, gInputStart, oldend, gInputEnd, shouldbeaComma) and (not equalspresent) do
     begin
      if (shouldbeaComma <> chComma) then (*it might, mistakenly, be a therefore*)
       begin
       gIllformed := true;
       fDeriverDocument.WriteToJournal('You should use commas to separate items in a list.', TRUE, FALSE);
{if tempLeftRoot <> nil then tempLeftRoot.DismantleFormula; }
       end
      else
       begin


       GetInput;
       skip(1, Logicfilter);  (*primes gCurrch, and gLookaheadCh*)
       newValuation := fDeriverDocument.fvaluation;

       DoParsing(gRoot, newValuation, gIllformed);

       if not gIllformed then
       if BadCharacters(gRoot, equals, compoundterms, higharity) then
       begin
       if equals then
       fDeriverDocument.WriteToJournal('(*Sorry, the semantics for = has not yet been implemented.*)', TRUE, FALSE)

       else if compoundterms then
       fDeriverDocument.WriteToJournal('(*Sorry, the semantics for compoundterms has not yet been implemented.*)', TRUE, FALSE)
       else if higharity then
       fDeriverDocument.WriteToJournal('(*Sorry, relations must be of arity two or less.*)', TRUE, FALSE);



       equalspresent := TRUE;
       gRoot.DismantleFormula;
       end;

       if gIllformed then
       fDeriverDocument.WriteToJournal(concat(gCurrCh, gErrorsArray[12], gParserErrorMessage), TRUE, FALSE);

       if not gIllformed and not equalspresent then
       begin
       testformula := gRoot.CopyFormula;

       gConstantsInTestRoot := concat(gConstantsInTestRoot, gRoot.ConstantsInFormula);

                         {someconstants in original formula}
                         {may not appear in open node}

       RemoveDuplicates(gConstantsInTestRoot);

       gTestroot.fAntecedents.InsertFirst(testformula);

       gRoot.DismantleFormula;

       end;

       gInputStart := gInputEnd + 1; {miss comma}

       end;
     end;

    if (not gIllformed) and (not equalspresent) then
     begin
      fDeriverDocument.fvaluation := newValuation;
      newValuation := nil;

      if (fDeriverDocument.fvaluation.fSize <> 0) then
       InterpretFreeVariables(fDeriverDocument.fvaluation);

      NodeSatisfiable;

      DismantleTestTree(gTestroot);
     end;
   end;

 end;




*/











  void parseMenuItem_actionPerformed(ActionEvent e) {

 //   System.out.println("Hello world");

 //   writeToJournal("Hello world", false, false);
  //  writeToJournal("Hello world", true, false);

    // TTest test=new TTest();


    {
      StringReader aStringReader = readSelection(TUtilities.defaultFilter);

      if (aStringReader != null) {

        /*  if (aStringReader!=null)
          {
            test.Test(aStringReader, parserErrors);

            {String aString= new String();
            parserErrors.write(aString);
            writeToJournal(aString, true, false);

            System.out.println("Hello world" + aString);
          } } */


        {
        	boolean firstOrder=true;
        	
          TParser tempParser = new TPriestParser(aStringReader,firstOrder); //new THowsonParser(aStringReader);
          TFormula temp = new TFormula();
          //    try{

          //TGlobals.gCurrCh = (char)TGlobals.gInputStream.read();

          //   tempParser.fCurrCh = (char)tempParser.fInput.read();
          //   if(tempParser.fCurrCh) skip(1);

          //     tempParser.skip(1); // }

          //  tempParser.setLookAheads(tempParser.fDefaultFilter); }

          //    catch(IOException ex) {}

          //System.out.println(TGlobals.gCurrCh);

          System.out.println("Priest Starting, this is current char " +
                             tempParser.fCurrCh);

          ArrayList dummy = new ArrayList();

    //      writeToJournal(strCR+"Starting parse", true, false);

               if (tempParser.wffCheck(temp,/*dummy,*/aStringReader))
                        {
                       // System.out.println("wffcheck no errors, writing it out ");
                       // System.out.println(tempParser.writeTermToString(temp));

                      // System.out.println(tempParser.writeFormulaToString(temp));
  //                     writeToJournal(strCR+"Well formed, writing out "+tempParser.writeFormulaToString(temp) , true, false);



  tempParser.setVerbose(true);

  writeToJournal(tempParser.writeFormulaToString(temp), true, false);

                        }
                else
                        {
                        System.out.println("wff errors");
                        System.out.println("parser errors");
                        System.out.println(/*2015 TGlobals.g*/ tempParser.fParserErrorMessage);
                        }


          System.out.println("Now come the parser error stream***");

          System.out.println(tempParser.getErrorString());

          //          writeToJournal(tempParser.getErrorString(), true, false);
        }
      }

      /*     {String aString= new String();
          parserErrors.write(aString);


          System.out.println("Hello world" + aString);
           } */

  //////////////////////////////


    }
  }


  /*

public String readSelectionToString(boolean useFilter,String regEx, String replacement){
    String input = journalPane.getSelectedText();

    if (input==null)
        return null;

    if (useFilter)
      input=input.replaceAll(regEx,replacement);

    return
        input;
  }

*/

/*
public TMyProofPanel getCurrentProofPanel(){
 return
     (TMyProofPanel)(fDeriverDocument.fProofPanel);

} */

  public TProofPanel getCurrentProofPanel(){
   return
       (fDeriverDocument.fProofPanel);

}


  void startLambdaMenuItem_actionPerformed(ActionEvent e) {  //start proof

    fRightTabbedPane.setSelectedComponent(fDeriverDocument.fLambdaPanel);

    String inputStr=readDualSource(TUtilities.noFilter);   //lambda uses blanks

    fDeriverDocument.startLambdaProof(inputStr);  //need to do this because may be Copi document etc

  }

  void startProofMenuItem_actionPerformed(ActionEvent e) {  //start proof

    fRightTabbedPane.setSelectedComponent(fDeriverDocument.fProofPanel);
    String inputStr=readDualSource(TUtilities.logicFilter);//TUtilities.readSelectionToString(fJournalPane,TUtilities.logicFilter);
    fDeriverDocument.startProof(inputStr);  //need to do this because may be Copi document etc

  }

  void startTreeMenuItem_actionPerformed(ActionEvent e) {  //start proof

  fRightTabbedPane.setSelectedComponent(fDeriverDocument.fTreePanel);
  String inputStr=readDualSource(TUtilities.logicFilter);//TUtilities.readSelectionToString(fJournalPane,TUtilities.logicFilter);
  fDeriverDocument.startTree(inputStr);  //need to do this because may be Copi document etc
}


  void editHTMLMenuItem_actionPerformed(ActionEvent e) {



   try {

     StringWriter sw = new StringWriter();
     HTMLDocument htmlDocument = (HTMLDocument)fJournalPane.getDocument();

     fEditorKit.write(sw,htmlDocument,0,htmlDocument.getLength());

/*m_kit.write(sw, m_doc, 0, m_doc.getLength());
sw.close();

        */

      sw.close();

THTMLSourceDialog dlg = new THTMLSourceDialog(this,sw.toString());

 ((AbstractDocument)(dlg.getSourceText().getDocument())).setDocumentFilter(new removeCommentFilter());


/*HtmlProcessor.this, sw.toString());  */
dlg.setVisible(true);
if (!dlg.succeeded())
return;

StringReader sr = new StringReader(dlg.getSource());

fJournalPane.setText("");  //mf get rid of olds

HTMLDocument  newDocument = (HTMLDocument)fEditorKit.createDefaultDocument();
              newDocument.setDocumentFilter(new removeCommentFilter());

fEditorKit.read(sr,htmlDocument,0);

      sr.close();

//journalPane.setDocument(newDocument);  ??mf???

 /*
StringReader sr = new StringReader(dlg.getSource());
m_doc = (MutableHTMLDocument)m_kit.createDocument();
m_context = m_doc.getStyleSheet();
m_kit.read(sr, m_doc, 0);
sr.close();
m_editor.setDocument(m_doc);
documentChanged();
*/}
catch (Exception ex) {
showError(ex, "Error: "+ex);
}



  }


public TDeriverDocument getDeriverDocument(){
  return
      fDeriverDocument;
}


String getJournalText(){
   StringWriter sw = new StringWriter();
   try {


   HTMLDocument htmlDocument = (HTMLDocument) fJournalPane.getDocument();

   fEditorKit.write(sw, htmlDocument, 0, htmlDocument.getLength());

   /*m_kit.write(sw, m_doc, 0, m_doc.getLength());
      sw.close();

    */

   sw.close();
 }
 catch (Exception ex) {
showError(ex, "Error: "+ex);
}


  return
      sw.toString();

}

void setJournalText(String text){

try{
    HTMLDocument htmlDocument = (HTMLDocument) fJournalPane.getDocument();
    StringReader sr = new StringReader(text);

    fJournalPane.setText(""); //mf get rid of olds

 /*   HTMLDocument newDocument = (HTMLDocument) fEditorKit.
        createDefaultDocument(); */

    fEditorKit.read(sr, htmlDocument, 0);

    sr.close();
  }

      catch (Exception ex) {
     showError(ex, "Error: "+ex);
     }



}

//  to here July 1 06

public static final int proofTabIndex=0;
public static final int drawTabIndex=1;


public void setTabSelected(int i){

  if (i>0&&i<2)
    fRightTabbedPane.setSelectedIndex(i);

}


  void insertImageMenuItem_actionPerformed(ActionEvent e) { // insert image



    String imageURL =inputURL("Image file or url?", "");

    HTMLDocument htmlDocument = (HTMLDocument)fJournalPane.getDocument();

   // String imageURL =JOptionPane.showInputDialog(this, "Image file or url:");

    //need a dialog here

    if (imageURL==null||imageURL==strNull)
      return;

    if (fEditorKit!=null){ //should always be non null anyway

      try {
        fEditorKit.insertHTML(htmlDocument, fJournalPane.getCaretPosition(),
                              "<img src=\"" +
                              imageURL +
                              "\">", 0, 0, HTML.Tag.IMG);

      }
      catch (Exception except) {
        JOptionPane.showMessageDialog(this, "Image not loaded.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
        except.printStackTrace();
      }
    }

    /*

     652 CHAPTER 20 CONSTRUCTING AN HTML EDITOR APPLICATION
String url = inputURL("Please enter image URL:", null);}
if (url == null)
return;
try {
ImageIcon icon = new ImageIcon(new URL(url));
int w = icon.getIconWidth();
int h = icon.getIconHeight();
if (w<=0 || h<=0) {
JOptionPane.showMessageDialog(HtmlProcessor.this,
"Error reading image URL\n"+
url, APP_NAME,
JOptionPane.WARNING_MESSAGE);
return;
MutableAttributeSet attr = new SimpleAttributeSet();
attr.addAttribute(StyleConstants.NameAttribute,
HTML.Tag.IMG);
attr.addAttribute(HTML.Attribute.SRC, url);
attr.addAttribute(HTML.Attribute.HEIGHT,
Integer.toString(h));
attr.addAttribute(HTML.Attribute.WIDTH,
Integer.toString(w));
int p = m_editor.getCaretPosition();
m_doc.insertString(p, " ", attr);
}
catch (Exception ex) {
showError(ex, "Error: "+ex);
}
}
};
Figure 20.7
HtmlProcessor�s insert link dialog



     */




  }


  protected String inputURL(String prompt, String initialValue) {

    final JDialog dialog = new JDialog(this, true);
    String returnStr = null;
    final JTextField text = new JTextField(initialValue);
    final boolean [] okPressed={false};

  /*  class CancelAction
        extends AbstractAction {
      public CancelAction() {
        putValue(NAME, "Cancel");
      }
      public void actionPerformed(ActionEvent ae) {
        dialog.dispose();
      }
    }  */


 //   JButton cancel = new JButton(new CancelAction());

    JButton cancel2 = new JButton("Cancel");
    ActionListener lst2 = new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                         text.setText("");
                         dialog.dispose();
                       }
                       };
    cancel2.addActionListener(lst2);

    /*

      if (!(fFileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION))
           return;                    // bale out if they cancel

         fileName=fFileChooser.getSelectedFile().getPath();



 */

    JButton browse = new JButton("Browse");
    ActionListener lst3 = new ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                           if (!(fFileChooser.showOpenDialog(TBrowser.this)==JFileChooser.APPROVE_OPTION))
           return;                    // bale out if they cancel
                            /*  JFileChooser chooser = new JFileChooser();
                              if (chooser.showOpenDialog(TBrowser.this) !=
                                      JFileChooser.APPROVE_OPTION)
                                      return; */

                              File f = fFileChooser.getSelectedFile();
                              try {
                                      String str = f.toURL().toString();
                                      //op.setInitialSelectionValue(str);
                                      text.setText(str);
                              }
                              catch (Exception ex) {
                                      ex.printStackTrace();
                              }
                      }
              };
              browse.addActionListener(lst3);

    JButton ok = new JButton("OK");

    ActionListener lst4 = new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                        // returnStr= text.getText();

                       okPressed[0]=true;    // the dialog may be closed off close box

                        dialog.dispose();
                       }
                       };
    ok.addActionListener(lst4);

    JButton help = new JButton("Help");

    ActionListener lst5 = new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null,"You can have absolute addresses"
                                                      +" which look like this"
                                                      + strCR
                                                      +"'http://www.me.com/Logic/Images/myFace.gif'"
                                                      + strCR

                                                      + "or 'file:/Logic/Images/myFace.gif' "
                                                      + strCR
                                                      +"or relative addresses which have the form 'Images/myFace.gif'."

                                                      + strCR
                                                      +"Relative addresses are often more useful, but with them you will need"
                                                      + strCR
                                                      +"to save the journal file before the computer can find the target address"
                                                      + strCR
                                                      +"and thus display it. (For the program needs to know what any relative"
                                                      + strCR
                                                      +"addresses are relative to.)");


                       }
                       };
    help.addActionListener(lst5);



    JButton[]buttons ={help,browse,cancel2,ok};
    TProofInputPanel inputPane = new TProofInputPanel(prompt, text, buttons);


    dialog.setContentPane(inputPane);

    inputPane.getRootPane().setDefaultButton(ok);

    dialog.setSize(340,120);

    dialog.setLocationRelativeTo(this);



    //dialog.

    dialog.setVisible(true);

    if (okPressed[0]==true)
       returnStr= text.getText();

    if (returnStr != null && returnStr.length() == 0)
       returnStr = null;

   return
      returnStr;




 /*   Object[] options = { "OK", "CANCEL", "Brwose" };


   JOptionPane aPane= new JOptionPane();

   aPane.showInputDialog(this,"Hello", "There",
                         JOptionPane.QUESTION_MESSAGE,null,
                         options,options[0]);

 /*  JOptionPane bPane= new JOptionPane(this,"Hello", "There",
                         JOptionPane.QUESTION_MESSAGE,null,
                         options,options[0]
                  ); */

   ///////old

/*
    JPanel p = new JPanel();
               p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
               p.add(new JLabel(prompt));
               p.add(Box.createHorizontalGlue());
               JButton bt = new JButton("Browse");
               bt.setRequestFocusEnabled(false);
               p.add(bt);

               final JOptionPane op = new JOptionPane(p,
                       JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
               op.setWantsInput(true);
               if (initialValue != null)
                       op.setInitialSelectionValue(initialValue);

               ActionListener lst = new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                               JFileChooser chooser = new JFileChooser();
                               if (chooser.showOpenDialog(TBrowser.this) !=
                                       JFileChooser.APPROVE_OPTION)
                                       return;
                               File f = chooser.getSelectedFile();
                               try {
                                       String str = f.toURL().toString();
                                       op.setInitialSelectionValue(str);
                               }
                               catch (Exception ex) {
                                       ex.printStackTrace();
                               }
                       }
               };
               bt.addActionListener(lst);

               JDialog dlg = op.createDialog(this,  "Help");
               dlg.show();
               dlg.dispose();

               Object value = op.getInputValue();	// Changed - Pavel
               if(value == JOptionPane.UNINITIALIZED_VALUE)
                       return null;
               String str = (String)value;
               if (str != null && str.length() == 0)
                       str = null;
               return str; */
       }



  void insertLinkMenuItem_actionPerformed(ActionEvent e) {  // insert link

    String linkURL =inputURL("Target file or url?", "");

   if ((linkURL!=null)&&(linkURL.length()>0)){

        if (fEditorKit!=null){
          MutableAttributeSet attr = fEditorKit.getInputAttributes();
          boolean anchor = attr.isDefined(HTML.Tag.A);
          if (anchor) {
            attr.removeAttribute(HTML.Tag.A);
          }
          else {
            SimpleAttributeSet as = new SimpleAttributeSet();
            as.addAttribute(HTML.Attribute.HREF, linkURL);
            attr.addAttribute(HTML.Tag.A, as);
          }
          //setCharacterAttributes((JEditorPane)fJournalPane, attr, false);

          fJournalPane.setCharacterAttributes(attr, false);
        }
      }



String input = new String("abdedc");


  String[]premiseAndConclusion = input.split("z",2);

    input=input.replaceAll("[" + chBlank+"e]","");  // watch out for regex syntax and the need for escape characters

//    writeToJournal(input , true, false);


   fDeriverDocument.fProofPanel.bugAlert("Bug","Alert");



  }

  void evaluateMenuItem_actionPerformed(ActionEvent e) {


   String inputStr=TSwingUtilities.readSelectionToString(fJournalPane,TUtilities.noFilter);

   if ((inputStr!=null)&&(inputStr!=strNull))
      fDeriverDocument.doCommand(inputStr);
  }

  void makeDrawing(String inputStr){

    /* we know this starts 'make drawing' and then should have ''(  */


    int pos = inputStr.indexOf('(');

    if (pos != -1) {

      inputStr = inputStr.substring(pos);

      if (inputStr.length() > 2) {

        inputStr = inputStr.substring(1); //left brakcket
        inputStr = inputStr.substring(0, inputStr.length() - 1); //right bracket

        inputStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<java version=\"1.4.2_05\" class=\"java.beans.XMLDecoder\"> " +
            inputStr +
            "</java>"
            ;

        createDrawingFromXML(TUtilities.expandXML(inputStr)); // it may be compressed, expansion does not hurt if not
      }

    }
  }


  void decodeMenuItem_actionPerformed(ActionEvent e) {
    String inputStr=TSwingUtilities.readSelectionToString(fJournalPane,TUtilities.noFilter);

      if (inputStr!=null){


        writeToJournal(strCR+"Trying general decode: " +strCR+strCR+
                       TUtilities.generalDecode(TUtilities.noReturnsFilter(inputStr)) +          // we may have put returns in to make it wrap
                       strCR+
                     strCR, true, false);
/*

        writeToJournal("Trying noLogic; " +TUtilities.deCodeConfirmNoLogic(inputStr) +
                       strCR+
                     strCR, true, false);

        writeToJournal("Trying Logic; " +TUtilities.deCodeConfirmLogic(inputStr) +
                       strCR+
                     strCR, true, false);


*/


          // writeToJournal("[" + TUtilities.xOrEncrypt(TUtilities.urlDecode(inputStr)) + "]", true, false);

   //   writeToJournal("[" + TUtilities.urlDecode(inputStr) + "]", true, false);

      //  writeToJournal("[" + TUtilities.xOrEncrypt(inputStr) + "]", true, false);






    }


  }

  void xORMenuItem_actionPerformed(ActionEvent e) {
    String inputStr=TSwingUtilities.readSelectionToString(fJournalPane,TUtilities.noFilter);

      if (inputStr!=null){
       // writeToJournal("[" + TUtilities.xOrEncrypt(TUtilities.urlDecode(inputStr)) + "]", true, false);

       writeToJournal(TUtilities.urlDecode(inputStr)+ strCR+ strCR, true, false);

      writeToJournal(TUtilities.xOrEncrypt(TUtilities.urlDecode(inputStr)), true, false);


      //  writeToJournal("[" + TUtilities.xOrEncrypt(inputStr) + "]", true, false);
      }


  }



  void tryMenuItem_actionPerformed(ActionEvent e) { //try  rule
	  
	  System.out.println("Trying try");
	  
	  String inputStr=readDualSource(TUtilities.logicFilter);//TUtilities.readSelectionToString(fJournalPane,TUtilities.logicFilter);

	  fDeriverDocument.startResolution(inputStr);  //need to do this because may be Copi document etc

/*
    {StringReader aStringReader=readSelection(TUtilities.defaultFilter);

        if (aStringReader!=null) {

        	boolean firstOrder=true;

         {TParser tempParser= new TParser(aStringReader,firstOrder);
         TFormula temp= new TFormula();

         //TFormula out


            System.out.println("Starting, this is current char "+ tempParser.fCurrCh);

            ArrayList dummy=new ArrayList();

         if (tempParser.wffCheck(temp,dummy,aStringReader))
                    {

                      TFormula constants=temp.closedTermsInFormula();



                   // System.out.println("wffcheck no errors, writing it out ");
                   // System.out.println(tempParser.writeTermToString(temp));

                  // System.out.println(tempParser.writeFormulaToString(temp));
                   writeToJournal(strCR+"Well formed, writing out "+tempParser.writeFormulaToString(temp) , true, false);
                  writeToJournal(strCR+"Constants, writing out "+tempParser.writeListOfFormulas(constants), true, false);

                    }  /*
            else
                    {
                    System.out.println("wff errors");
                    System.out.println("parser errors");
                    System.out.println(TGlobals.gParserErrorMessage);
                    }  */
/*

            System.out.println("Now come the parser error stream***");

            System.out.println(tempParser.getErrorString());

     //          writeToJournal(tempParser.getErrorString(), true, false);
         }}
*/
    /*     {String aString= new String();
        parserErrors.write(aString);


        System.out.println("Hello world" + aString);
         } */

//////////////////////////////


//  }





/*    String inputStr=TUtilities.readSelectionToString(fJournalPane,TUtilities.noFilter);

    String encode ="";

    if (inputStr!=null){

      writeToJournal("To lines:" +
                     strCR + TUtilities.toLines(inputStr,8), true, false);




      encode=TUtilities.generalEncode(inputStr);
      writeToJournal(encode +
                     strCR, true, false);
      writeToJournal(TUtilities.generalDecode(encode) +
                     strCR, true, false);

      writeToJournal("Without extra:" +
                     strCR, true, false);

      encode=TUtilities.xOrEncrypt(TUtilities.urlEncode(inputStr));

      writeToJournal(encode +
               strCR, true, false);
      writeToJournal(TUtilities.urlDecode(TUtilities.xOrEncrypt(encode)) +
               strCR, true, false);

    }

    /*
    if (inputStr!=null){
      encode=TUtilities.enCodeConfirmLogic("Martin", inputStr);
      writeToJournal(encode +
                     strCR, true, false);
      writeToJournal(TUtilities.deCodeConfirmLogic(encode) +
                     strCR, true, false);
    } */


    String key="123456789123456789123456789909876";

  /*

      if (inputStr!=null){

        try{
           encode = TUtilities.dESEncrypt(inputStr, key);
          writeToJournal(encode +strCR, true, false);
          writeToJournal(TUtilities.dESDecrypt(encode, key), true, false);
        }
        catch (Exception ex){}
      }


*/
/*
    JFrame aFrame=new JFrame("Complete the Truth Table Line");
   TPredTruthTable game =new TPredTruthTable(aFrame);

   aFrame.getContentPane().add(game);
     aFrame.setSize(500,300);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-300)/2);
      aFrame.setResizable(false);

      game.run();

*/


/*    boolean noPropositions=true;
    boolean unaryPredsOnly=true;
    boolean noQuantifiers=false;

    writeToJournal(strCR
                   +fDeriverDocument.fParser.writeFormulaToString(us.softoption.games.TRandomFormula.randomPredFormula(5,false,noPropositions,unaryPredsOnly,noQuantifiers)), true, false);

*/

/*
     String inputStr=TUtilities.readSelectionToString(fJournalPane,TUtilities.noFilter);

      if (inputStr!=null){
       // writeToJournal(TUtilities.urlEncode(TUtilities.xOrEncrypt(inputStr)), true, false);

        writeToJournal(TUtilities.urlEncode(TUtilities.xOrEncrypt("Martin")+" : "+inputStr), true, false);
      //  writeToJournal(TUtilities.xOrEncrypt(inputStr), true, false);

     //   TUtilities.urlEncode(TUtilities.xOrEncrypt(TDeriverApplication.fCurrentUser+" : "+fProofStr))
      }

*/



/*    String inputStr=readDualSource(TUtilities.logicFilter);

(fDeriverDocument.fTreePanel).startTree(inputStr);

*/


 /*   if (TProofQuiz.fNumOpen==0){

        TProofQuiz quiz = new TProofQuiz();
        quiz.setVisible(true);
  } */

 /*   JEditorPane jep=new JEditorPane();

 String url="http://research.sbs.arizona.edu:8080/servlet/ShowParameters?user=Henry";  // GET example http://myhost.com/mypath/myscript.cgi?name1=value1&name2=value2


 //http://research.sbs.arizona.edu:8080/servlet/ShowParameters?user=Martin


 littleRoutine();




 try{
 jep.setPage(url);}
 catch (Exception ex){

 }

 JFrame aFrame=new JFrame();
 aFrame.getContentPane().add(jep);

 aFrame.setVisible(true);  */



/*   TParser tempParser=new TParser();
   String randomStr= tempParser.writeFormulaToString(us.softoption.games.TRandomFormula.randomPropFormula(5,false));

   int length =randomStr.length();

String [] randomArray = new String[length];

 for(int i=0;i<length;i++){
   randomArray[i]=randomStr.substring(i,i+1);
 }

 TMainConnective game =new TMainConnective();
 game.run();


 String [][] row=  new String[1][length];

 row[0]=randomArray;


JTable jt= new JTable(row,
                      new String [] {""});

  writeToJournal("Random "+randomStr , true, false); */




 // we want to make this random string into an array of strings

// char [] test=randomStr.toCharArray();




      /*   String [] args= {""};

    try {
      REPL.main(args);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
*/


 /*String inputStr=TUtilities.readSelectionToString(fJournalPane,TUtilities.noFilter);

     if ((inputStr!=null)&&(inputStr!=strNull)){

     /*    inputStr= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                   "<java version=\"1.4.2_05\" class=\"java.beans.XMLDecoder\"> "+
             inputStr+
             "</java>"
             ;

         createDrawingFromXML(inputStr);

     System.out.print(TUtilities.compressXML(inputStr)); */

  /*  System.out.print(TUtilities.compressXML(createXMLFromDrawing()));



      /*   String outputStr=TUtilities.compressString(TUtilities.noReturnsFilter(inputStr));
         System.out.print(inputStr.length() +outputStr.length() +outputStr);
         outputStr=TUtilities.expandString(outputStr);
         System.out.print(outputStr.length() +outputStr); */


   /*      try {

           String resultStr=(fApplication.fSISCScheme.eval(inputStr)).toString();

           System.out.print(resultStr) ;
           writeToJournal("SISC Scheme output: " + resultStr, true, false);
         }
         catch (Exception ex) {
           ex.printStackTrace();
         }*/
       }          //end of try









/*Interpreter interpreter;

try {

AppContext ctx = new AppContext();
Context.register("main", ctx);
interpreter = Context.enter("main");
REPL.loadHeap(interpreter, REPL.findHeap(""));
System.out.println(" done.");

 System.out.print((interpreter.eval("(+ 1 1)")).toString());
}
 catch(Exception ex) {
      ex.printStackTrace();
    }

*/

 //  fDeriverDocument.fApplication.fEToL.myDebugTest();


//  }

/*

   procedure TDeriverDocument.WriteToJournal (message: str255; highlight: BOOLEAN; toMarker: BOOLEAN);

     var
      newText: ptr;
      messagelength: LONGINT;
      itsText: Handle;
      endSelection, beginSelection, i, endIndex: LONGINT;

     procedure FIndMarker; {looks after selection-1 you don't want to find it before the selection,}
   {                            but if it is eof and so too is the marker you want to remove the}
   {                            marker}

      var
       theseChars: charshandle;
       found: BOOLEAN;

     begin
      found := FALSE;
      theseChars := TEGetText(fJournalTEView.fHTE);
      endIndex := fJournalTEView.fHTE^^.teLength;

      MoveHHi(Handle(theseChars)); {not needed}
      HLock(Handle(theseChars));

      i := endSelection;

      if (i <> 0) then
       i := i - 1; {covers eof case}

      while (i < endIndex) and not found do
       begin
        if theseChars^^[i] = chInsertMarker then
         begin
          found := TRUE;
          if found then
          beginSelection := i;
         end;
        i := i + 1;

       end;

      HUnLock(Handle(theseChars));
      if found then
       endSelection := i;
     end;

    begin
     messagelength := length(message);
     if messagelength > 0 then
      begin
       newText := Pointer(ord(@message) + 1);

       if (fJournalTEView.fHTE^^.teLength + messagelength) > maxInt then
        FailOSErr(memFullErr)
       else
        begin
         endSelection := fJournalTEView.fHTE^^.selEnd;
         beginSelection := endSelection;

         if toMarker then
          FIndMarker;

         if fJournalTEView.Focus then
          ;

         TESetSelect(beginSelection, endSelection, fJournalTEView.fHTE);

         if beginSelection <> endSelection then
          begin
          TEDelete(fJournalTEView.fHTE);
          endSelection := beginSelection;
          end;

         TEInsert(newText, messagelength, fJournalTEView.fHTE);

         if highlight then
          TESetSelect(endSelection, endSelection + messagelength, fJournalTEView.fHTE);

         if fJournalTEView.Focus then
          ;

         fJournalTEView.SynchView(TRUE); {true means redraw}

        end;
      end;
    end;



   */



  /************** OLD Open File Code ******************************************/


// Open named file; read text from file into jTextArea1; report to statusBar.
    void OldopenFile(String fileName) {
      try {
        // Open a file of the given name.
        File file = new File(fileName);

        // Get the size of the opened file.
        int size = (int)file.length();

        // Set to zero a counter for counting the number of
        // characters that have been read from the file.
        int chars_read = 0;

        // Create an input reader based on the file, so we can read its data.
        // FileReader handles international character encoding conversions.
        FileReader in = new FileReader(file);  //Martin here, don;t we need a stream for one byte html?

        // Create a character array of the size of the file,
        // to use as a data buffer, into which we will read
        // the text data.
        char[] data = new char[size];

        // Read all available characters into the buffer.
        while(in.ready()) {
          // Increment the count for each character read,
          // and accumulate them in the data buffer.
          chars_read += in.read(data, chars_read, size - chars_read);
        }

        in.close();

        // Create a temporary string containing the data,
        // and set the string into the JTextArea.
        fJournalPane.setText(new String(data, 0, chars_read));

        // Cache the currently opened filename for use at save time...
        this.fCurrFileName = fileName;
        // ...and mark the edit session as being clean
        setDirty(false);


        // Display the name of the opened directory+file in the statusBar.
        statusBar.setText("Opened "+fileName);

       setWindowTitle();
      }

      catch (IOException e) {
        statusBar.setText("Error opening "+fileName);
      }
    }



    void OLDopenFile(String fileName) {
      try {

        File file = new File(fileName);

   // sat 27th March

        BufferedReader inReader = new BufferedReader(new FileReader(file));

        String inString= new String();

        String appendStr= inReader.readLine();   // we are uninterested in the eol characters

        while (appendStr!=null){
          inString=inString+appendStr;
          appendStr= inReader.readLine();
        }

        inReader.close();

        StringReader inStrReader=new StringReader(inString);


        fJournalPane.read(inStrReader,null);  /*   Notice here
         that it is a Pane read method not a StringReaderMethod. The null here picks the filetype eg html Do I want streams?
        // the journal pane has an html editor kit and will read to </html> */


        inStrReader.close();


        /*If there is any xml we'll try to read it as a Bean serialization */

        int xmlStart =inString.lastIndexOf("<?xml");

        if (xmlStart!=-1){

          inString = inString.substring(xmlStart);


          //now we need this as a stream

          XMLDecoder d = new XMLDecoder( new BufferedInputStream(
                                new ByteArrayInputStream(inString.getBytes())));
          Object result = d.readObject();
          d.close();



        }




   // end of sat 27th March







        /********* old version
        FileReader in = new FileReader(file);

        journalPane.read(in,null);  // the null here picks the filetype eg html Do I want streams?

        long length = (journalPane.getText()).length();

        in.close();  // do we want a finally here?  */


        /*now we'll find the xml */

  /*      int size = (int)file.length();
        in = new FileReader(file);

        char[] key = new char[5];

        for (int i=0; i<(size-5);i++){
          in.read(key,0,5);
      //    if

        } */


//FileReader in = new FileReader(file);




      /*  The XML is appended, but maybe we can get it by reading the whole file

      we need to find the <?xml which starts it */


        FileInputStream inStream=new FileInputStream(file);

        int before=inStream.available();


        fJournalPane.read(inStream,null);
//      String test=journalPane.getText();
   //     System.out.print(test);

     //   int after =inStream.available();

        inStream.close();


   //     inStream.skip(length);

        ///////////

   /*    byte[] data = new byte[20];

        inStream.read(data,0,12);

        ByteArrayInputStream inn = new ByteArrayInputStream(data);

        InputStreamReader innn=new InputStreamReader(inn);

        char[] test = new char[10];

        innn.read(test); */



            ////////////

        XMLDecoder d = new XMLDecoder( inStream
                           /* new BufferedInputStream(
                                inStream)*/);
         Object result = d.readObject();
         d.close();






        // Cache the currently opened filename for use at save time...
        this.fCurrFileName = fileName;
        // ...and mark the edit session as being clean
        setDirty(false);


        // Display the name of the opened directory+file in the statusBar.
        statusBar.setText("Opened "+fileName);

       setWindowTitle();
      }

      catch (IOException e) {
        statusBar.setText("Error opening "+fileName);
      }
    }

  void saveAsHTMLItem_actionPerformed(ActionEvent e) {
    boolean HTML=true;
    saveFileAs(HTML);       // we always want to Save As to to check for overwrite

  }



public JTextPane getJournalPane(){
  return
      fJournalPane;
}

  /*******************************************************************************/


/*

   3:
       begin
        gInputStr := strNull;
        gOutputStr := strNull;
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



*/

void trueMenuItem_actionPerformed(ActionEvent e) {

   String inputStr=readDualSource(TUtilities.defaultFilter);

   TFormula root = prepare(inputStr);   // this also picks up the valuation

   if (root!=null){
     fDeriverDocument.selectionTrue(root);

     fDeriverDocument.clearValuation(); // removes valuation
   }




     ;
 }

/* replaced December 2013 
ArrayList myTokenizer(String inputStr){
  ArrayList outputList=new ArrayList();

  int comma,leftBracket,rightBracket;

  if (inputStr.length()==0)
    return
       outputList;
  else{

  /*   comma=inputStr.indexOf(chComma);
     leftBracket=inputStr.indexOf(chLSqBracket);
     rightBracket=inputStr.indexOf(chRSqBracket);  2015
	  
	  comma=inputStr.indexOf(chComma);
	     leftBracket=inputStr.indexOf(chLSqBracket);
	     rightBracket=inputStr.indexOf(chRSqBracket); 

     //we don't want to find any commas between the brackets

     while ((leftBracket<comma)&&(comma<rightBracket)&&(comma!=-1)){
       // comma=inputStr.indexOf(chComma,comma+1);  //to next comma
        comma=inputStr.indexOf(chComma,comma+1);  //to next comma
     }


     if (comma==-1){                  // no commas, whole lot goes in
        outputList.add(inputStr);
        return
           outputList;
     }
     else{                            // comma, not between brackets
       outputList=myTokenizer(inputStr.substring(comma+1));  //tokenize tail

       String insert=inputStr.substring(0,comma);          // miss the comma

       outputList.add(0,insert);                            // prepend new one

       return
         outputList;

     }

  }
}

*/

	   private ArrayList<String> myTokenizer(String inputStr){		   
		   
	   	  ArrayList<String> outputList=new ArrayList<String>();

	   	  int nested=0;
	   	  int nestedSq=0;

	   	  if (inputStr.length()==0)
	   	    return
	   	       outputList;
	   	  else{
	   		  char currCh;
	   		  for (int i=0;i<inputStr.length();i++){
	   			currCh=inputStr.charAt(i);
	   			
	   			if (currCh==chSmallLeftBracket)
	   				nested++;
	   			if (currCh==chSmallRightBracket)
	   				nested--;
	   			if (currCh==chLSqBracket)
	   				nestedSq++;
	   			if (currCh==chRSqBracket)
	   				nestedSq--;
	   		  
	   			if ((nested==0)&&          
	   				(nestedSq==0)&&
	   				(currCh==chComma)){    //commas separating the list of premises are not nested
	   				                       // found acomma, not between brackets
	   		   	       outputList=myTokenizer(inputStr.substring(i+1));  //tokenize tail

	   		   	       String insert=inputStr.substring(0,i);          // miss the comma

	   		   	       outputList.add(0,insert);                            // prepend new one

	   		   	       return
	   		   	         outputList;
	   			}
	   		  }
	   		  
	   		  //if we get to here, no commas left
	   		  
	   	        outputList.add(inputStr);  //all remainder goes in
	   	        return
	   	           outputList;
	   	  }

	   	}


/* Prepare tries to parse a comma separated list of formulas and assembles them
 into one large conjunct. It also checks that every constant that appears is also
 in the drawing.

 */

TFormula prepare(String selection){
  TFormula tempRoot = null;
  TFormula leftRoot = null;
  boolean wellFormed=true;
  String inputStr=null;

  //do I need to clear the valuation?

  String badConstants=null;

  if ((selection==null)||selection==strNull) // no selection
    return
        null;

 // StringTokenizer formulas = new StringTokenizer(selection,String.valueOf(TConstants.chComma));

  ArrayList formulas=myTokenizer(selection);

  Iterator iter=formulas.iterator();


  //CANNOT TOKENIZE ON COMMAS AS VALUATION HAS THEM


  while ((iter.hasNext())&&wellFormed){
    inputStr=(String)iter.next();

    if (inputStr!=strNull){   // can be nullStr if input starts with comma, or they put two commas togethe,should just skip
       /*TFormula */ leftRoot = new TFormula();
       StringReader aReader = new StringReader(inputStr);

       wellFormed=fDeriverDocument.getParser().wffCheck(leftRoot, fDeriverDocument.fValuation, aReader);
       /*the different types of document will have different parsers*/

       //this can use a different valuation for each formula

       if (!wellFormed)
          fDeriverDocument.writeToJournal(fDeriverDocument.getParser().fCurrCh +
                                              TConstants.fErrors12 +
                                              fDeriverDocument.getParser().fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
       else{
          if (tempRoot==null){  // first in list
             tempRoot=leftRoot;
             leftRoot = null;
             }
          else{

             TFormula andRoot = new TFormula();

             andRoot.fKind = TFormula.binary;
             andRoot.fInfo = String.valueOf(chAnd);
             andRoot.fLLink = leftRoot;
             andRoot.fRLink = tempRoot;

             tempRoot=andRoot;
             andRoot=null;


                 /*

                                SupplyFormula(temproot);
                         temproot.fKind := Binary;
                         temproot.fInfo := chAnd;
                         temproot.fLLink := tempLeftRoot;
                         temproot.fRLink := gRoot;
                         gRoot := temproot;
                         tempLeftRoot := temproot;
                         temproot := nil;
                         end;

                         fDeriverDocument.fvaluation := newValuation;
                         newValuation := nil;


              }
}*/

          }

          //fDeriverDocument.fValuation=newValuation;

          badConstants= fDeriverDocument.constantsNotReferring(tempRoot);

          if ((badConstants!=strNull)&&!tempRoot.isSpecialPredefined()){
            wellFormed=false;

            fDeriverDocument.writeToJournal(strCR +
                                              "(*You should have an object " +
                                              badConstants+
                                              " in the Universe*)"+
                                              strCR +
                                              "(*for the constant named " +
                                              badConstants+
                                              " to refer to*)"+
                                              strCR
                                              , TConstants.HIGHLIGHT, !TConstants.TO_MARKER);

              //MORE TO COME

          }

        }
     }
   //newValuation.clear();

   }     //END of while

  if (wellFormed)
    return
      tempRoot;
  else
    return
        null;
}







/*
 function TJournalWindow.Prepare: boolean;

{check this should do dismantling of illformed}

  var
   notRefTo: string[1];
   tempCh: CHAR;
   oldend, newend, i: integer;
   newValuation: TList;
   temproot, tempLeftRoot: TFormula;
   shouldbeaComma: CHAR;

{prepare checks whether reference is OK}



 begin
  gIllformed := FALSE;
  oldend := gInputEnd;
  temproot := nil;
  tempLeftRoot := nil;
  shouldbeaComma := chComma;

  if (gInputStart = gInputEnd) then
   Prepare := FALSE {nothing selected}
  else
   begin
    while not gIllformed and MoreListInput(gInputHdl, gInputStart, oldend, gInputEnd, shouldbeaComma) do
     begin

      if (shouldbeaComma <> chComma) then (*it might, mistakenly, be a therefore*)
       begin
       gIllformed := true;
       fDeriverDocument.WriteToJournal('You should use commas to separate items in a list.', TRUE, FALSE);
       if tempLeftRoot <> nil then
       tempLeftRoot.DismantleFormula;
       end
      else
       begin

       GetInput;
       skip(1, logicfilter);  (*primes gCurrch, and gLookaheadCh*)
       newValuation := fDeriverDocument.fvaluation;
       DoParsing(gRoot, newValuation, gIllformed);  (*overriden for different parsers*)

       if gIllformed then
       begin
       fDeriverDocument.WriteToJournal(concat(gErrorsArray[12], gParserErrorMessage), TRUE, FALSE);
                         {tempLeftRoot.DismantleFormula  }
       end
       else
       begin

       if tempLeftRoot = nil then
       tempLeftRoot := gRoot
       else
       begin
       SupplyFormula(temproot);
       temproot.fKind := Binary;
       temproot.fInfo := chAnd;
       temproot.fLLink := tempLeftRoot;
       temproot.fRLink := gRoot;
       gRoot := temproot;
       tempLeftRoot := temproot;
       temproot := nil;
       end;

       fDeriverDocument.fvaluation := newValuation;
       newValuation := nil;


       if not fDeriverDocument.ReferenceOK(gRoot, tempCh) then
       begin
       if not gRoot.SpecialPredefined then
       begin
       gIllformed := TRUE;
       notRefTo := ' ';
       notRefTo[1] := tempCh;
       fDeriverDocument.WriteToJournal(concat(gCr, '(*You should have an object ', notRefTo, ' in the Universe*)', gCr, '(*for the constant name ', notRefTo, ' to refer to.*)', gCr), TRUE, FALSE);
       end;
       end;

       end;

       gInputStart := gInputEnd + 1; {miss comma}
       end;
     end;

    Prepare := not gIllformed;
   end;

 end;

*/

/********************* UndoActions [Inner Classes] *******************************/

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



/*********************************************************************************/


/************************* Inner classes *******************************************/


class BadXMLHandler implements ExceptionListener{

           public void exceptionThrown  (Exception e){
       fBadXML=true;
           }

            }



  void valuationMenuItem_actionPerformed(ActionEvent e) {
   fDeriverDocument.writeValuation();
  }

  void interpretationMenuItem_actionPerformed(ActionEvent e) {
    fDeriverDocument.writeInterpretation();

  }

  void propositionsMenuItem_actionPerformed(ActionEvent e) {
    fDeriverDocument.writeTruePropositions();

  }

  void symbolizeMenuItem_actionPerformed(ActionEvent e) {

   String inputStr=readDualSource(TUtilities.peculiarFilter);

    if ((inputStr!=null)&&(inputStr!=strNull)){
      boolean propChosen = propMenuItem.isSelected();

          fDeriverDocument.fBridge.symbolize(inputStr,propChosen);
    }

  }

  void toEnglishMenuItem_actionPerformed(ActionEvent e) {

    String inputStr=readDualSource(TUtilities.defaultFilter);

      if ((inputStr!=null)&&(inputStr!=strNull)){

            fDeriverDocument.fBridge.translate(inputStr);   // translate back
      }

  }

  void satisfiableMenuItem_actionPerformed(ActionEvent e) {
   stringSatisfiable(readDualSource(TUtilities.logicFilter));
  }

  void denyMenuItem_actionPerformed(ActionEvent e) {
    {
      String inputStr=readDualSource(TUtilities.defaultFilter);

      TFormula root = prepare(inputStr);   // this also picks up the valuation, and passes it to the document

      if (root!=null){
        fDeriverDocument.youDeny(root,fDeriverDocument.fValuation);
        fDeriverDocument.clearValuation(); // removes valuation
      }
    }
}
  void endorseMenuItem_actionPerformed(ActionEvent e) {
    {
      String inputStr=readDualSource(TUtilities.defaultFilter);

      TFormula root = prepare(inputStr);   // this also picks up the valuation, and passes it to the document

      if (root!=null){
        fDeriverDocument.youEndorse(root,fDeriverDocument.fValuation);
        fDeriverDocument.clearValuation(); // removes valuation
      }
    }
}



  void doCommandMenuItem_actionPerformed(ActionEvent e) {

    String rawInputStr=TSwingUtilities.readSelectionToString(fJournalPane,TUtilities.noFilter);

      if ((rawInputStr!=null)&&(rawInputStr!=strNull)){
        String inputStr=TUtilities.noReturnsFilter(rawInputStr);

        int command=identifyCommand(inputStr); // same are not intended for LISP and may have symbols

        switch (command){
          case kLISPCmd:
            boolean lispSyntax=true;

            fDeriverDocument.fBridge.interpretCommand(TUtilities.peculiarFilter(rawInputStr),
                                                   !lispSyntax );   /* these are going
            to be
             commands some
             semantics, some to the LISP connected with translating back and forth*/

            break;
          case kMakeDrawingCmd:
            makeDrawing(inputStr);
            break;
          case kWriteDrawingCmd:
                fDeriverDocument.writeToJournal(strCR
                                       +TUtilities.compressXML(createXMLFromDrawing())
                                       +strCR
                                       , !TConstants.HIGHLIGHT, !TConstants.TO_MARKER);

            break;

        }
      }
  }

  void helpMenu_actionPerformed(ActionEvent e) {

  }

  void preferencesMenuItem_actionPerformed(ActionEvent e) {

    if (TPreferences.fNumOpen==0){

      JFrame window = new TPreferences();

      window.setTitle("Preferences");

      window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      window.setVisible(true);
    }
    else
      TPreferences.thePrefDialog.toFront();

  }

  void editTextMenuItem_actionPerformed(ActionEvent e) {
     fJournalPane.setEditable(true);
     fJournalEditable=true;


  }

  void liveTextMenuItem_actionPerformed(ActionEvent e) {
    fJournalPane.setEditable(false);
    fJournalEditable=false;

  }

/*don't think I am using tag actions at present*/

/*This is Loy p.898 */


  /*public*/ class TagAction extends StyledEditorKit.StyledTextAction {
   private HTML.Tag tag;
   private HTML.Attribute tagAttr;
   private String tagName;

   public TagAction(HTML.Tag t, String s, HTML.Attribute a) {
     /*many tags do not have attribute modifiers, this is called with
      HTML.Attribute.DUMMY*/
     super(s);
     tag = t;
     tagName = s;
     tagAttr = a;
   }

   public void actionPerformed(ActionEvent e) {
     /*many tags do not have attribute modifiers, this is called with
      HTML.Attribute.DUMMY*/
     String value="";

     JEditorPane editor = getEditor(e);
     if (editor != null) {
       if (tagAttr!=HTML.Attribute.DUMMY){
          value = JOptionPane.showInputDialog(TBrowser.this,
                                                  "Enter " + tagName +":");}
       StyledEditorKit kit = getStyledEditorKit(editor);
       MutableAttributeSet attr = kit.getInputAttributes();
       boolean anchor = attr.isDefined(tag);
       if (anchor) {
         attr.removeAttribute(tag);
       }
       else {
         SimpleAttributeSet as = new SimpleAttributeSet();
         if (value!=null&&!value.equals(""))
           attr.addAttribute(tagAttr, value);
         attr.addAttribute(tag, as);


       }


        setCharacterAttributes(editor, attr, false);

        if (tag==HTML.Tag.BLOCKQUOTE)  // bug (theirs) not updating

        {int selStart=fJournalPane.getSelectionStart();
         int selEnd=fJournalPane.getSelectionEnd();

         setJournalText(getJournalText());   //HACK

         fJournalPane.setSelectionStart(selStart);
         fJournalPane.setSelectionEnd(selEnd);




          //
        }
     }
   }
 }


/*
  void insertTagsMenuItem_actionPerformed(ActionEvent e) {

    Object[]   options = new Object[]
          {"Heading 1","Heading 2"
          };

    HTML.Tag [] tagOptions = new HTML.Tag[]
          {HTML.Tag.H1,HTML.Tag.H2
          };

    Object result= JOptionPane.showInputDialog(this,"Please choose the markup", "HTML markuup",
        JOptionPane.QUESTION_MESSAGE,null, options,"Heading 1");

    int index = Arrays.binarySearch(options,result);


/*
Fetches a tag constant for a well-known tag name (i.e. one of  the tags in the set
         {A, ADDRESS, APPLET, AREA, B,  BASE, BASEFONT, BIG,  BLOCKQUOTE, BODY, BR, CAPTION,
 CENTER, CITE, CODE,  DD, DFN, DIR, DIV, DL, DT, EM, FONT, FORM, FRAME,  FRAMESET, H1, H2,
 H3, H4, H5, H6, HEAD, HR, HTML,  I, IMG, INPUT, ISINDEX, KBD, LI, LINK, MAP, MENU,  META,
 NOBR, NOFRAMES, OBJECT, OL, OPTION, P, PARAM,  PRE, SAMP, SCRIPT, SELECT, SMALL, SPAN,
 STRIKE, S,  STRONG, STYLE, SUB, SUP, TABLE, TD, TEXTAREA,  TH, TITLE, TR, TT, U, UL, VAR}.
 If the given name does not represent one of the well-known tags, then  null will be returned.


   if (index>-1)
   {HTML.Tag tag=tagOptions[index];


   /* if ((result!=null)&&(result.length()>0))

      // HTML.Tag tag=HTML.getTag(result);
 if (fEditorKit!=null){
        /*   MutableAttributeSet attr = fEditorKit.getInputAttributes();
           boolean anchor = attr.isDefined(tag);
           if (anchor) {
             attr.removeAttribute(tag);
           }
           else {
             SimpleAttributeSet as = new SimpleAttributeSet();
             as.addAttribute(tag);
             attr.addAttribute(HTML.Tag.A, as);
           }


           fJournalPane.setCharacterAttributes(attr, false);
         }



   if (fEditorKit!=null){ //should always be non null anyway
     HTMLDocument htmlDocument = (HTMLDocument)fJournalPane.getDocument();

     int selStart=fJournalPane.getSelectionStart();
     int selEnd=fJournalPane.getSelectionEnd();

      try {
        fEditorKit.insertHTML(htmlDocument, selEnd,
                              "<h1>", 0, 0, null /*tag);

      }
      catch (Exception except) {
        JOptionPane.showMessageDialog(this, "Markup not inserted.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
        except.printStackTrace();
      }
    }


   } */


  void eKitMenuItem_actionPerformed(ActionEvent e) {

    fApplication.showEkit(this);

    URL baseForRelativeURLs=((HTMLDocument)fJournalPane.getStyledDocument()).getBase();

   ((HTMLDocument)fApplication.fEkit.getTextPane().getStyledDocument()).setBase(baseForRelativeURLs);


   fApplication.fEkit.getEkitCore().loadFrontJournal();


   /* Ekit ekit = new Ekit(); xcvxc

    JOptionPane option = new JOptionPane(ekit);

    JOptionPane.showConfirmDialog(null,ekit); */

  }



  private void insertBreak(){
    if (fEditorKit!=null){ //should always be non null anyway

      HTMLDocument htmlDocument = (HTMLDocument)fJournalPane.getDocument();

      try {
        int caretPos =fJournalPane.getCaretPosition();
        fEditorKit.insertHTML(htmlDocument, caretPos,
                              "<br>", 0, 0, HTML.Tag.BR);
        fJournalPane.setCaretPosition(caretPos+1);

      }
      catch (Exception except) {

      }
    }

  }

  private void insertRule(){
    if (fEditorKit!=null){ //should always be non null anyway

      HTMLDocument htmlDocument = (HTMLDocument)fJournalPane.getDocument();

      try {
        int caretPos =fJournalPane.getCaretPosition();
        fEditorKit.insertHTML(htmlDocument, caretPos,
                              "<hr>", 0, 0, HTML.Tag.HR);
        fJournalPane.setCaretPosition(caretPos+1);

      }
      catch (Exception except) {

      }
    }

  }

  void insertRuleMenuItem_actionPerformed(ActionEvent e) {

    insertRule();
  }


  void insertBreakMenuItem_actionPerformed(ActionEvent e) {

    insertBreak();
   /* private void insertBreak()
            throws IOException, BadLocationException, RuntimeException
            {
                    int caretPos = jtpMain.getCaretPosition();
                    htmlKit.insertHTML(htmlDoc, caretPos, "<BR>", 0, 0, HTML.Tag.BR);
                    jtpMain.setCaretPosition(caretPos + 1);
            } */

  }

  void printJournal(){
    // old using Hall PrintUtilities.printComponent(fJournalPane);

    DocumentRenderer renderer = new DocumentRenderer();
        renderer.print((HTMLDocument)(fJournalPane.getDocument()));
  }

  void printMenuItem_actionPerformed(ActionEvent e) {

    printJournal();

  }



  void fileMenu_menuSelected(MenuEvent e) {
setUpFileMenu();
  }

  void fEditMenu_menuSelected(MenuEvent e) {
setUpEditMenu();
  }

  void HTMLMenu_menuSelected(MenuEvent e) {
setUpHTMLMenu();
  }

  void semanticsMenu_menuSelected(MenuEvent e) {
    setUpSemanticsMenu();

  }

  void actionsMenu_menuSelected(MenuEvent e) {
  setUpActionsMenu();

}

void gamesMenu_menuSelected(MenuEvent e) {
  setUpGamesMenu();

  }


  void openWebPageMenuItem_actionPerformed(ActionEvent e) {
    String inputURL =inputURL("Url or file?", "");

    if (!inputURL.startsWith("http://")&&!inputURL.startsWith("file:")){
      if (inputURL.startsWith("/"))
        inputURL="file:" + inputURL;
      else{
        try{
          File f =new File(inputURL);
          inputURL=f.toURL().toString();
        }
        catch (Exception ex){
          inputURL="";          //really want good default
        }
      }

    }

    if (!inputURL.equals(""))
      try {
        fJournalPane.setPage(inputURL);
      }
    catch (Exception ex)
    {
      statusBar.setText("Could not open " + inputURL);
    }

  }


  /***********************  GAME METHODS ******************************/


    public void connectiveMenuItem_actionPerformed(ActionEvent e) {

// REVIEW THIS


      JFrame aFrame=new JFrame("Main Connective");

      TMainConnective game =new TMainConnective(aFrame,fDeriverDocument.getParser());

     // aFrame.setContentPane(game);

     aFrame.getContentPane().add(game);
     aFrame.setSize(400,200);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-400)/2, (TDeriverApplication.fScreenSize.height-200)/2);
      aFrame.setResizable(false);

    aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
    game.run();

//   aFrame.setVisible(true);
    }

    public void predConnectiveMenuItem_actionPerformed(ActionEvent e) {

// REVIEW THIS


    JFrame aFrame=new JFrame("Main Connective");

    TMainConnective game =new TMainConnective(aFrame,fDeriverDocument.getParser());

   // aFrame.setContentPane(game);

   aFrame.getContentPane().add(game);
   aFrame.setSize(400,200);
    aFrame.setLocation((TDeriverApplication.fScreenSize.width-400)/2, (TDeriverApplication.fScreenSize.height-200)/2);
    aFrame.setResizable(false);

    game.setPropositional(false);  // we'll use pred formulas

  aFrame.setVisible(true); // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
  game.run();

//   aFrame.setVisible(true);
  }


    public void truthTableMenuItem_actionPerformed(ActionEvent e) {
   //  TTruthTable game =new TTruthTable();
   //  game.run();

   JFrame aFrame=new JFrame("Complete the Truth Table Line");
   TTruthTable game =new TTruthTable(aFrame,fDeriverDocument.getParser());

   aFrame.getContentPane().add(game);
     aFrame.setSize(500,200);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-200)/2);
      aFrame.setResizable(false);

      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();
   }

   public void predTruthTableMenuItem_actionPerformed(ActionEvent e) {


   JFrame aFrame=new JFrame("Complete the Truth Table line for the entire formula");
  // TPredTruthTableOld game =new TPredTruthTableOld(aFrame);

  TPredTruthTable game =new TPredTruthTable(aFrame,fDeriverDocument.getParser());

   aFrame.getContentPane().add(game);
     aFrame.setSize(500,300);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-300)/2);
      aFrame.setResizable(false);

      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();
   }


   public void satisfiableGameMenuItem_actionPerformed(ActionEvent e) {
    //  TSatisfiable game =new TSatisfiable();
   //   game.run();

   JFrame aFrame=new JFrame("Show whether the formula is satisfiable ie make it true");
   TSatisfiable game =new TSatisfiable(aFrame,fDeriverDocument.getParser());

   aFrame.getContentPane().add(game);
     aFrame.setSize(500,230);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-230)/2);
      aFrame.setResizable(false);

      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();

   }

   public void predSatisfiableGameMenuItem_actionPerformed(ActionEvent e) {
    //  TSatisfiable game =new TSatisfiable();
   //   game.run();

   JFrame aFrame=new JFrame("Produce an Interpretation to make the formula true");
   TPredSatisfiable game =new TPredSatisfiable(aFrame,fDeriverDocument.getParser());

   aFrame.getContentPane().add(game);
     aFrame.setSize(600,560);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-560)/2);
      aFrame.setResizable(false);

      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();

   }

   public void predSatisfiable2GameMenuItem_actionPerformed(ActionEvent e) {
   //  TSatisfiable game =new TSatisfiable();
//   game.run();

  JFrame aFrame=new JFrame("Produce an Interpretation to make the formula true");
  //TPredSatisfiableOld game =new TPredSatisfiableOld(aFrame);

   TPredSatisfiable game =new TPredSatisfiable(aFrame,fDeriverDocument.getParser());

   game.setUseQuantifiers(true);

  aFrame.getContentPane().add(game);
    aFrame.setSize(600,560);   //500 x 230
     aFrame.setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-560)/2);
     aFrame.setResizable(false);

     aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
     game.run();

  }

   public void predConsistentGameMenuItem_actionPerformed(ActionEvent e) {
     //  TSatisfiable game =new TSatisfiable();
    //   game.run();

    JFrame aFrame=new JFrame("Produce an Interpretation to make all the formulae true");
   // TPredConsistentOld game =new TPredConsistentOld(aFrame);

   TPredConsistent game =new TPredConsistent(aFrame,fDeriverDocument.getParser());

    game.setUseQuantifiers(true);

    aFrame.getContentPane().add(game);
      aFrame.setSize(600,560);
       aFrame.setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-560)/2);
       aFrame.setResizable(false);

       aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
       game.run();

   }

   public void predInvalidGameMenuItem_actionPerformed(ActionEvent e) {
      //  TSatisfiable game =new TSatisfiable();
     //   game.run();

     JFrame aFrame=new JFrame("Produce an Interpretation to prove the argument invalid");
     //TPredInvalidOld game =new TPredInvalidOld(aFrame);

     TPredInvalid game =new TPredInvalid(aFrame,fDeriverDocument.getParser());

     game.setUseQuantifiers(true);

     aFrame.getContentPane().add(game);
       aFrame.setSize(600,560);
        aFrame.setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-560)/2);
        aFrame.setResizable(false);

        aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
        game.run();

   }


   public void consistentGameMenuItem_actionPerformed(ActionEvent e) {
    //  TConsistent game =new TConsistent();

    JFrame aFrame=new JFrame("Show whether the list of formulas is consistent.");
    TConsistent game =new TConsistent(aFrame,fDeriverDocument.getParser());

    aFrame.getContentPane().add(game);
      aFrame.setSize(600,230);
       aFrame.setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-230)/2);
       aFrame.setResizable(false);


      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();
   }

   public void invalidGameMenuItem_actionPerformed(ActionEvent e) {

      JFrame aFrame=new JFrame("Find a semantic counter-example.");
       TInvalid game =new TInvalid(aFrame,fDeriverDocument.getParser());

       aFrame.getContentPane().add(game);
         aFrame.setSize(600,230);
          aFrame.setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-230)/2);
          aFrame.setResizable(false);


      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();
   }

/*
   public void quiz2MenuItem_actionPerformed(ActionEvent e) {

    if (TGamesQuiz.fNumOpen==0){


    TDeriverApplication.determineUser();


      TGamesQuiz quiz = new TGamesQuiz("Quiz 2",
          5,40,
          5,150, /*10
          5,150, /*10
          3,600, /*5
          2,600); /*5
          quiz.setVisible(true);
    }
   } */

/*

   public void quiz3MenuItem_actionPerformed(ActionEvent e) {



   if (TProofQuiz.fNumOpen==0){

     int [] prooftypes= {TRandomProofPanel.SimpleAndIENegEOrI,
                         TRandomProofPanel.SimpleAndIENegEOrI,
                                        TRandomProofPanel.SimpleAndIENegEOrI,
                                        TRandomProofPanel.AndIENegEOrI,
                                        TRandomProofPanel.AndIENegEOrI};

     JTextArea text= new JTextArea(strCR
                                            + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
                                            + strCR
                                            + strCR
                                            + "Proof1: a simple derivation using \u223CE \u2227I \u2227E \u2228I ."
                                            + strCR
                                            + "Proof2: a simple derivation using \u223CE \u2227I \u2227E \u2228I ."
                                            + strCR
                                            + "Proof3: a simple derivation using \u223CE \u2227I \u2227E \u2228I ."
                                            + strCR
                                            + "Proof4: an intermediate derivation using \u223CE \u2227I \u2227E \u2228I ."
                                            + strCR
                                            + "Proof5: an intermediate derivation using \u223CE \u2227I \u2227E \u2228I  ."
                                            + strCR+ strCR
                                            + ""
                                            + strCR
                                            + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
                                            + strCR
                                    );




     /*old QUIZ 3, I think it was too difficult for them, so I have simplified it and the
     old one will probably become QUIZ4

     int [] prooftypes= {TRandomProofPanel.AndIENegEOrI,
                                        TRandomProofPanel.ImplicEEquivE,
                                        TRandomProofPanel.ImplicI,
                                        TRandomProofPanel.NegI,
                                        TRandomProofPanel.OrEEquivI};

     JTextArea text= new JTextArea(strCR
                                            + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
                                            + strCR
                                            + strCR
                                            + "Proof1: a derivation using \u223CE \u2227I \u2227E \u2228I ."
                                            + strCR
                                            + "Proof2: a derivation using \u2283 E \u2261 E ."
                                            + strCR
                                            + "Proof3: a derivation using \u2283 I ."
                                            + strCR
                                            + "Proof4: a derivation using ~I ."
                                            + strCR
                                            + "Proof5: a derivation using \u2228 E \u2261 I  ."
                                            + strCR+ strCR
                                            + ""
                                            + strCR
                                            + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
                                            + strCR
                                    );




     TProofQuiz quiz = new TProofQuiz(this,
                                      "Quiz 3",
                                      prooftypes,
                                      text
  );
     quiz.setVisible(true);
   }
  }   */

/*

  public void quiz4MenuItem_actionPerformed(ActionEvent e) {



     if (TProofQuiz.fNumOpen==0){

       int [] prooftypes= {TRandomProofPanel.SimpleImplicEEquivE,
                                        TRandomProofPanel.SimpleImplicEEquivE,
                                        TRandomProofPanel.SimpleImplicEEquivE,
                                        TRandomProofPanel.ImplicEEquivE,
                                        TRandomProofPanel.ImplicEEquivE};


       JTextArea text= new JTextArea(strCR
                                              + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
                                              + strCR
                                              + strCR
                                              + "Proof1: a simple derivation using \u2283E \u2261E  ."
                                              + strCR
                                              + "Proof2: a simple derivation using \u2283E \u2261E  ."
                                              + strCR
                                              + "Proof3: a simple derivation using \u2283E \u2261E  ."
                                              + strCR
                                              + "Proof4: an intermediate derivation using \u2283E \u2261E  ."
                                              + strCR
                                              + "Proof5: an intermediate derivation using \u2283E \u2261E   ."
                                              + strCR+ strCR
                                              + ""
                                              + strCR
                                              + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
                                              + strCR
                                      );






       TProofQuiz quiz = new TProofQuiz(fDeriverDocument.fProofPanel,//this,
                                        "Quiz 4",
                                        prooftypes,
                                        text
    );
       quiz.setVisible(true);
     }
  }  */

  /*
    public void quiz5MenuItem_actionPerformed(ActionEvent e) {

    if (TPredGamesQuiz.fNumOpen==0){

      TPredGamesQuiz quiz = new TPredGamesQuiz("Quiz 5",
          5,40,
          5,300,
          2,300,
          1,300,
          1,300,
          1,300);
      quiz.setVisible(true);
    }
   } */

   public void quiz5aMenuItem_actionPerformed(ActionEvent e) {

     if (TProofQuiz.fNumOpen==0){

       int [] prooftypes= {TRandomProofPanel.PredNoQuant,
                                        TRandomProofPanel.SimpleUI,
                                        TRandomProofPanel.SimpleUG,
                                        TRandomProofPanel.SimpleEG,
                                        TRandomProofPanel.SimpleEI};
       JTextArea text= new JTextArea(strCR
                                            + "Work through the Tabs to Finish. [Proof 1 is intermediate level, 2-5 are elementary.]"
                                            + strCR
                                            + strCR
                                            + "Proof 1: a derivation in predicate calculus, without quantifiers."
                                            + strCR
                                            + "Proof 2: a derivation using UI."
                                            + strCR
                                            + "Proof 3: a derivation using UG."
                                            + strCR
                                            + "Proof 4: a derivation using EG."
                                            + strCR
                                            + "Proof 5: a derivation using EI."
                                            + strCR+ strCR
                                            + ""
                                            + strCR
                                            + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
                                            + strCR
                                    );


      TProofQuiz quiz = new TProofQuiz(fDeriverDocument.fProofPanel,//this,
                                       new TRandomProof(),                       //should be produced by document not browser
                                       "Quiz 6",
                                      prooftypes,
                                      text
  );
      quiz.setVisible(true);
    }

   }

/*
   public void finalQ6MenuItem_actionPerformed(ActionEvent e) {

     if (TPredGamesQuiz.fNumOpen==0){

      TPredGamesQuiz quiz = new TPredGamesQuiz(fDeriverDocument.getParser(),
          "Final Q6",
          0,0,
          5,300,
          2,300,
          1,300,
          1,300,
          1,300);

    quiz.removeConnectiveTab();

      quiz.setVisible(true);
    }


   }

 */


/*

    public void midTerm2MenuItem_actionPerformed(ActionEvent e) {

     if (TGamesQuiz.fNumOpen==0){

       TGamesQuiz quiz = new TGamesQuiz(fDeriverDocument.getParser(),
                                        "Mid-term Question 6",
           0,0,
           5,75,
           5,75,
           3,360,
           3,360);

       quiz.removeConnectiveTab();


       quiz.setVisible(true);
     }
  }

 */

/*

   public void midTerm3MenuItem_actionPerformed(ActionEvent e) {

     if (TProofQuiz2.fNumOpen==0){

     TProofQuiz2 quiz = new TProofQuiz2(this,
                                      "Mid-term Question 6,7");
     quiz.setVisible(true);
   }
  }

 */

/*
    public void finalQ7MenuItem_actionPerformed(ActionEvent e) {

      if (TProofQuiz.fNumOpen==0){

       int [] prooftypes= {TRandomProofPanel.TwelveLineProp,                 //NEED TO BE DIFFERENT
                                          TRandomProofPanel.TenLinePred,
                                          TRandomProofPanel.TenLinePred};

       JTextArea text= new JTextArea(strCR
                                              + "Work through the Tabs to Finish. [These are 8-12 line derivations.]"
                                              + strCR
                                              + strCR
                                              + "For the Final, you need do just TWO of these THREE"

                                              + strCR
                                              + strCR
                                              + "Proof1: a propositional derivation which might use any of the propositional rules."
                                              + strCR
                                              + "Proof2: a predicate derivation which might use any of the rules."
                                              + strCR
                                              + "Proof3: a predicate derivation which might use any of the rules."

                                              + strCR+ strCR
                                              + ""
                                              + strCR
                                              + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
                                              + strCR
                                      );




       TProofQuiz quiz = new TProofQuiz(fDeriverDocument.fProofPanel,//this,
                                        "Final Q7,8",
                                        prooftypes,
                                        text
    );
       quiz.setVisible(true);
     }

  } */

/****************************************************************************************/



/****************** Temporary or Experimental Methods **********************/

/*  void debugBrowser(){

    TDeriverDocument document = new TDeriverDocument(fApplication);

      TBrowser newFrame=(TBrowser)(document.getJournal());  //NOV 06 CAST ONLY WORKS WITH APPLICATION
      //Validate frames that have preset sizes
      //Pack frames that have useful preferred size info, e.g. from their layout
      if (false) {
        newFrame.pack();
      }
      else {
        newFrame.validate();
      }
      //Center the window

      fApplication.placeWindow(newFrame);
      newFrame.setVisible(true);


} */

    void littleRoutine(){

       File file=new File("../data/data.xml");   //store the data as an xml string, then read and display


        String dataString="";

        fBadXML=false;

     try{


       XMLDecoder d = new XMLDecoder(new BufferedInputStream(
           new FileInputStream(file)), null,
                                     new ExceptionListener() {
         public void exceptionThrown(Exception e) {
           fBadXML=true;
         }
       });

       dataString = (String) d.readObject();
       d.close();
     }

     catch (Exception except) {

          }


     dataString+= "<br>Hello<br>";

     try{

       XMLEncoder os = new XMLEncoder(new BufferedOutputStream(
           new FileOutputStream(file)));

       os.writeObject(dataString);

       os.close();
     }

     catch (Exception except) {

         }

     }




     /****************** Helper Inner Classes QUIZZES **********************/























}    //THIS IS THE END OF THE EDITOR CLASS



 /****************** Helper Classes **********************/



class TBrowser_quitMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_quitMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.quitMenuItem_actionPerformed(e);
  }
}

class TBrowser_helpMenuAbout_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_helpMenuAbout_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.helpMenuAbout_actionPerformed(e);
  }
}

class TBrowser_connectiveMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_connectiveMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.connectiveMenuItem_actionPerformed(e);
  }
}

//connectiveMenuItem

class TBrowser_predConnectiveMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_predConnectiveMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.predConnectiveMenuItem_actionPerformed(e);
  }
}

class TBrowser_truthTableMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_truthTableMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.truthTableMenuItem_actionPerformed(e);
  }
}

class TBrowser_predTruthTableMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_predTruthTableMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.predTruthTableMenuItem_actionPerformed(e);
  }
}

class TBrowser_satisfiableGameMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_satisfiableGameMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.satisfiableGameMenuItem_actionPerformed(e);
  }
}

class TBrowser_predSatisfiableGameMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_predSatisfiableGameMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.predSatisfiableGameMenuItem_actionPerformed(e);
  }
}

class TBrowser_predSatisfiable2GameMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_predSatisfiable2GameMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.predSatisfiable2GameMenuItem_actionPerformed(e);
  }
}

class TBrowser_predConsistentGameMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_predConsistentGameMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.predConsistentGameMenuItem_actionPerformed(e);
  }
}

class TBrowser_predInvalidGameMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_predInvalidGameMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.predInvalidGameMenuItem_actionPerformed(e);
  }
}


class TBrowser_consistentGameMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_consistentGameMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.consistentGameMenuItem_actionPerformed(e);
  }
}

class TBrowser_invalidGameMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_invalidGameMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.invalidGameMenuItem_actionPerformed(e);
  }
}

/*

class TBrowser_quiz2MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_quiz2MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.quiz2MenuItem_actionPerformed(e);
  }
}

 */

/*

class TBrowser_quiz3MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_quiz3MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.quiz3MenuItem_actionPerformed(e);
  }
}

 */

/*

class TBrowser_quiz4MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_quiz4MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.quiz4MenuItem_actionPerformed(e);
  }
}

 */

/*

class TBrowser_quiz5MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_quiz5MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.quiz5MenuItem_actionPerformed(e);
  }
} */

class TBrowser_quiz5aMenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_quiz5aMenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.quiz5aMenuItem_actionPerformed(e);
  }
}

/*
class TBrowser_finalQ6MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_finalQ6MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.finalQ6MenuItem_actionPerformed(e);
  }
}  */

/*
class TBrowser_midTerm2MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_midTerm2MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.midTerm2MenuItem_actionPerformed(e);
  }
}

 */


/*
class TBrowser_midTerm3MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_midTerm3MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.midTerm3MenuItem_actionPerformed(e);
  }
}*/

/*

class TBrowser_finalQ7MenuItem_ActionAdapter implements ActionListener {
  TBrowser adaptee;

  TBrowser_finalQ7MenuItem_ActionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.finalQ7MenuItem_actionPerformed(e);
  }
}
 */

class TBrowser_newBrowserMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_newBrowserMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newBrowserMenuItem_actionPerformed(e);
  }
}

class TBrowser_openFileMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_openFileMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.openFileMenuItem_actionPerformed(e);
  }
}

class TBrowser_saveMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_saveMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.saveMenuItem_actionPerformed(e);
  }
}

class TBrowser_saveAsMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_saveAsMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.saveAsMenuItem_actionPerformed(e);
  }
}

class TBrowser_newFileMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_newFileMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newFileMenuItem_actionPerformed(e);
  }
}

class TBrowser_fDocument_documentAdapter implements javax.swing.event.DocumentListener {
  TBrowser adaptee;

  TBrowser_fDocument_documentAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void insertUpdate(DocumentEvent e) {
    adaptee.fDocument_insertUpdate(e);
  }
  public void removeUpdate(DocumentEvent e) {
    adaptee.fDocument_removeUpdate(e);
  }
  public void changedUpdate(DocumentEvent e) {
    adaptee.fDocument_changedUpdate(e);
  }
}

class TBrowser_closeBrowserMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_closeBrowserMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.closeBrowserMenuItem_actionPerformed(e);
  }
}

class TBrowser_fDocument_undoableEditAdapter implements javax.swing.event.UndoableEditListener {
  TBrowser adaptee;

  TBrowser_fDocument_undoableEditAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void undoableEditHappened(UndoableEditEvent e) {
    adaptee.fDocument_undoableEditHappened(e);
  }
}

class MyFileFilter extends javax.swing.filechooser.FileFilter
  {
    private String fDescription = null;
    private String fExtension = null;

    public MyFileFilter(String extension, String description) {
      fDescription = description;
      fExtension = "." + extension.toLowerCase();
    }

    public String getDescription() {
      return fDescription;
    }

    public boolean accept(File f) {
      if (f == null)
        return false;
      if (f.isDirectory())    //to navigate directories
        return true;
      return
          f.getName().toLowerCase().endsWith(fExtension);
    }
  }

class TBrowser_fontMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_fontMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.fontMenuItem_actionPerformed(e);
  }
}





/////////////////////////  THIS IS NO LONGER USED

/*


 Used to remember selection when lost focu, but interfered with Cut.

  int fSelectionStart = -1;
  int fSelectionFinish = -1;

   journalPane.addFocusListener(new Editor2_journalPane_focusAdapter(this));

   void journalPane_focusGained(FocusEvent e) {
    if (fSelectionStart>=0 && fSelectionFinish>=0)
          if (journalPane.getCaretPosition()==fSelectionStart) {
            journalPane.setCaretPosition(fSelectionFinish);
            journalPane.moveCaretPosition(fSelectionStart);
          }
          else
            journalPane.select(fSelectionStart, fSelectionFinish);


  }

  void journalPane_focusLost(FocusEvent e) {
    fSelectionStart = journalPane.getSelectionStart();
    f


 ionFinish = journalPane.getSelectionEnd();

  }


 class Editor2_journalPane_focusAdapter extends java.awt.event.FocusAdapter {
  Editor2 adaptee;

  Editor2_journalPane_focusAdapter(Editor2 adaptee) {
    this.adaptee = adaptee;
  }
  public void focusGained(FocusEvent e) {
    adaptee.journalPane_focusGained(e);
  }
  public void focusLost(FocusEvent e) {
    adaptee.journalPane_focusLost(e);
  }
}




*/

class TBrowser_parseMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_parseMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.parseMenuItem_actionPerformed(e);
  }
}

class TBrowser_startLambdaMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_startLambdaMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.startLambdaMenuItem_actionPerformed(e);
  }
}

class TBrowser_startProofMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_startProofMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.startProofMenuItem_actionPerformed(e);
  }
}

class TBrowser_startTreeMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_startTreeMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.startTreeMenuItem_actionPerformed(e);
  }
}

class TBrowser_editHTMLMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_editHTMLMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.editHTMLMenuItem_actionPerformed(e);
  }
}

class TBrowser_insertImageMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_insertImageMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.insertImageMenuItem_actionPerformed(e);
  }
}

class TBrowser_insertLinkMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_insertLinkMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.insertLinkMenuItem_actionPerformed(e);
  }
}

class TBrowser_evaluateMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_evaluateMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.evaluateMenuItem_actionPerformed(e);
  }
}

class TBrowser_decodeMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_decodeMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.decodeMenuItem_actionPerformed(e);
  }
}

class TBrowser_xORMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_xORMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.xORMenuItem_actionPerformed(e);
  }
}

class TBrowser_tryMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_tryMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.tryMenuItem_actionPerformed(e);
  }
}


/*  // Handle the File|Open menu or button, invoking okToAbandon and openFile
// as needed.
  void fileOpen() {
    if (!okToAbandon()) {
      return;
    }
    // Use the OPEN version of the dialog, test return for Approve/Cancel
    if (fFileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
      // Call openFile to attempt to load the text from file into TextArea
      openFile(fFileChooser.getSelectedFile().getPath());
    }
 //   this.repaint();
  }  */

class TBrowser_saveAsHTMLItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_saveAsHTMLItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.saveAsHTMLItem_actionPerformed(e);
  }
}



class TBrowser_trueMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_trueMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.trueMenuItem_actionPerformed(e);
  }
}



class TBrowser_valuationMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_valuationMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.valuationMenuItem_actionPerformed(e);
  }
}

class TBrowser_interpretationMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_interpretationMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.interpretationMenuItem_actionPerformed(e);
  }
}

class TBrowser_propositionsMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_propositionsMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.propositionsMenuItem_actionPerformed(e);
  }
}

class TBrowser_symbolizeMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_symbolizeMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.symbolizeMenuItem_actionPerformed(e);
  }
}

class TBrowser_toEnglishMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_toEnglishMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.toEnglishMenuItem_actionPerformed(e);
  }
}

class TBrowser_satisfiableMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_satisfiableMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.satisfiableMenuItem_actionPerformed(e);
  }
}

class TBrowser_denyMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_denyMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.denyMenuItem_actionPerformed(e);
  }
}

class TBrowser_endorseMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_endorseMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.endorseMenuItem_actionPerformed(e);
  }
}

class TBrowser_doCommandMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_doCommandMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.doCommandMenuItem_actionPerformed(e);
  }
}

class TBrowser_helpMenu_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_helpMenu_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.helpMenu_actionPerformed(e);
  }
}

class TBrowser_preferencesMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_preferencesMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.preferencesMenuItem_actionPerformed(e);
  }
}

class TBrowser_editTextMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_editTextMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.editTextMenuItem_actionPerformed(e);
  }
}

class TBrowser_liveTextMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_liveTextMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.liveTextMenuItem_actionPerformed(e);
  }
}

/*

class TBrowser_insertTagsMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_insertTagsMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.insertTagsMenuItem_actionPerformed(e);
  }
} */

class TBrowser_eKitMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_eKitMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.eKitMenuItem_actionPerformed(e);
  }
}

class TBrowser_insertBreakMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_insertBreakMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.insertBreakMenuItem_actionPerformed(e);
  }
}

class TBrowser_insertRuleMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_insertRuleMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.insertRuleMenuItem_actionPerformed(e);
  }
}


class TBrowser_printMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_printMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.printMenuItem_actionPerformed(e);
  }
}



class TBrowser_fileMenu_menuAdapter implements javax.swing.event.MenuListener {
  TBrowser adaptee;

  TBrowser_fileMenu_menuAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.fileMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}

class TBrowser_fEditMenu_menuAdapter implements javax.swing.event.MenuListener {
  TBrowser adaptee;

  TBrowser_fEditMenu_menuAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.fEditMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}

class TBrowser_HTMLMenu_menuAdapter implements javax.swing.event.MenuListener {
  TBrowser adaptee;

  TBrowser_HTMLMenu_menuAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.HTMLMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}

class TBrowser_semanticsMenu_menuAdapter implements javax.swing.event.MenuListener {
  TBrowser adaptee;

  TBrowser_semanticsMenu_menuAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.semanticsMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}

class TBrowser_actionsMenu_menuAdapter implements javax.swing.event.MenuListener {
  TBrowser adaptee;

  TBrowser_actionsMenu_menuAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.actionsMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}

class TBrowser_gamesMenu_menuAdapter implements javax.swing.event.MenuListener {
  TBrowser adaptee;

  TBrowser_gamesMenu_menuAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void menuSelected(MenuEvent e) {
    adaptee.gamesMenu_menuSelected(e);
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuCanceled(MenuEvent e) {
  }
}


class TBrowser_openWebPageMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TBrowser adaptee;

  TBrowser_openWebPageMenuItem_actionAdapter(TBrowser adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.openWebPageMenuItem_actionPerformed(e);
  }
}



class removeCommentFilter extends DocumentFilter {   //SWING displays html comments and we dont' want it to
                                                     //This removes ones from Interet Explorer. Doesn't work


    public void insertString(FilterBypass fb, int offs,
                             String str, AttributeSet a)
        throws BadLocationException {

      str=str.replaceAll("\\Q<!--StartFragment-->\\E","");  // watch out for regex syntax and the need for escape characters
      str=str.replaceAll("\\Q<!--EndFragment-->\\E","");

      str=str.replaceAll("StartFragment","");     // belt and braces
      str=str.replaceAll("EndFragment","");

            super.insertString(fb, offs, str, a);

    }

    public void replace(FilterBypass fb, int offs,
                        int length,
                        String str, AttributeSet a)
        throws BadLocationException {

      str=str.replaceAll("\\Q<!--StartFragment-->\\E","");
      str=str.replaceAll("\\Q<!--EndFragment-->\\E","");

      str=str.replaceAll("StartFragment","");     // belt and braces
      str=str.replaceAll("EndFragment","");

            super.replace(fb, offs, length, str, a);
    }

}


class CommentRemover extends HTMLEditorKit.ParserCallback {


public void handleStartTag(HTML.Tag tag,         //I am going to change the comments, but not through handleCOmment
 MutableAttributeSet attributes, int position) {

  if (tag == HTML.Tag.COMMENT){
    attributes.addAttribute(HTML.Attribute.CONTENT,"new string");
  }

}


}   // I probably need to use the HTML reader

/*


   <!-- 
    
    
    <xsl:template match="node() | @*">
        <xsl:choose>
            <xsl:when test="self::text()">
                <xsl:variable name="this-text" select="self::text()"/>
                <xsl:choose>
                    <xsl:when test="contains($this-text, $what)">
                        <xsl:call-template name="replace">
                            <xsl:with-param name="str" select="$this-text"/>
                            <xsl:with-param name="what" select="$what"/>
                            <xsl:with-param name="by-what" select="$by-what"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="replace">
        <xsl:param name="str"/>
        <xsl:param name="what"/>
        <xsl:param name="by-what"/>

        <xsl:choose>
            <xsl:when test="not($str)"/>
            <xsl:when test="starts-with($str, $what)">
                <xsl:value-of select="$by-what"/>
                <xsl:call-template name="replace">
                    <xsl:with-param name="str" select="substring($str, string-length($what)+1)"/>
                    <xsl:with-param name="what" select="$what"/>
                    <xsl:with-param name="by-what" select="$by-what"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="substring($str, 1, 1)"/>
                <xsl:call-template name="replace">
                    <xsl:with-param name="str" select="substring($str, 2)"/>
                    <xsl:with-param name="what" select="$what"/>
                    <xsl:with-param name="by-what" select="$by-what"/>                
                </xsl:call-template>    
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

-->



<!--

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     version="2.0">
  

 
  
  
  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:template match="/object">
    <object> 
          <xsl:value-of select="replace(.,'TProperty','HELLO')"/>
    </object> 
  </xsl:template>

</xsl:stylesheet>

-->
<!--

<xsl:stylesheet
                  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                  version="2.0"> 
            <xsl:output  method="xml"
                        indent="yes"
                        omit-xml-declaration="yes"/> 

            <xsl:template  match="/aaa"> 
                  <xxx> 
                        <xsl:value-of  select="replace(.,'o.','#')"/> 
                  </xxx> 
                  <yyy> 
                        <xsl:value-of  select="replace(.,'ho','QQQ','i')"/> 
                  </yyy> 
                  <zzz> 
                        <xsl:value-of  select="replace(.,'HHH','BFF')"/> 
                  </zzz> 
            </xsl:template> 

      </xsl:stylesheet>

-->




<!--
<xsl:template name="string-replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text"
          select="substring-after($text,$replace)" />
          <xsl:with-param name="replace" select="$replace" />
          <xsl:with-param name="by" select="$by" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

 
Here's how it is called: 

  <xsl:variable name="myVar">
    <xsl:call-template name="string-replace-all">
      <xsl:with-param name="text" select="'This is a sample text : {ReplaceMe} and {ReplaceMe}'" />
      <xsl:with-param name="replace" select="'{ReplaceMe}'" />
      <xsl:with-param name="by" select="'String.Replace() in XSLT'" />
    </xsl:call-template>
  </xsl:variable>
  
  -->




 */

