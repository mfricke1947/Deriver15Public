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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.UIManager;

import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TDebugDocument;
import us.softoption.interpretation.TShapePanel;
/*import sisc.*;
import sisc.interpreter.*;
import sisc.ser.*;  */
import com.hexidec.ekit.Ekit;

/**
 * <p>Title:TJournalWindow </p>
 * <p>Description: with undo and save</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


/* read April 11 04 */


/* There is just one application and it is the overall manager.

Then there are zero or more deriver documents. These are just data (files, really). To look
at a document, a browser is used.

So, the deriver document has all the data. And there is a browser that looks at the data. One
browser can change data from one document to another document, or you can have several
browsers each with their own data.

This is just like a Web browser and the web pages that it can display.

A deriver browser consists of three views:- the journal panel that looks at
HTML text, a proof panel which looks at proofs, and a drawing panel which looks
at drawings or interpretations.

A browser manages many menu commands (like Open, New Browser, New File, Save, etc.).

The application looks after the browser list.

There are two Scheme(LISP) interpreters. One is public and global and the application looks
after it.  The second is hidden within the English to Logic translation unit*/



/*ok I'm doing a horrendous and naughty hack to get the JBuilder to archive my generic resources. The sisc
lisp uses a file based heap normally called "sisc.shp". Now I want to put this in with the source for
sisc  to automatically copy it to the code.class output, when compiling, where is can be found
by new MemoryRandomAccessInputStream(sisc.REPL.class.getResourceAsStream("sisc.shp"));
(and bundled by JAR). But my version
of JBuilder has disabled the ability to define generic resource types. So, temporarily I have given the
file the wrong suffix "sisc.png" which means that it is an 'image' and Jbuilder automatically copies and bundles
these
*/

/*As of Jan07 I am not using the lisp and 'sisc.shp'; in particular it is not in the applets-- too big.

 I am removing sisc.png and putting it with 'old code' */


public class TDeriverApplication {

  public static TDeriverApplication TheApplication;

  boolean packFrame = false;

  public static final String APP_NAME = "Deriver";

  static ArrayList fBrowsers = new ArrayList(); // the browsers
  static final int fXOffset = 30, fYOffset = 30;
  public static Dimension fScreenSize;

  static {fScreenSize = Toolkit.getDefaultToolkit().getScreenSize();}



  boolean fHelpShows=false;        //not implemented yet
  JFrame  fHelpWindow;             //not implemented yet


  //Scheme fScheme  = new Scheme(null);
  //Interpreter fSISCScheme = null;  not using at prsent Aug 06

//  TEnglishToLogic fEToL = new TEnglishToLogic(); Aug 10 06, moved to document so all documenets have their own context

  Ekit fEkit=null;

  static public TDebugDocument fDebug=null;
  static public int fConfiguration=0;  /*we use this to allow Deriver to simplify itself.
  Arguments get passed in through main(), usually by the jnlp file which launches Deriver. Then if
  we want a simple version, we hide some of the menus etc*/



// static private String fUser="";  // we occasionally need this for encypting submissions. Now in Preferences

 static private boolean fCommandLineCopi=false;
 static private boolean fCommandLineBergmann=false;
 static private boolean fCommandLineGentzen=false;
 static private boolean fCommandLineHausman=false;
 static private boolean fCommandLineHerrick=false;
 static private boolean fCommandLineHowson=false;
 static private boolean fCommandLinePriest=false;


  public TDeriverApplication() {


    TheApplication=this;          // there is only one

    TPreferences.initPrefKeys();
    TPreferences.loadUserPrefs(); //load Preferences on launch and elsewhere whenever used (because User may have saved new ones).

    if (fCommandLineBergmann)
      createDocument("bergmann");     // command line takes precedence over preferences
    else
    if (fCommandLineCopi)
      createDocument("copi");     // command line takes precedence over preferences
    else

    if (fCommandLineGentzen)
      createDocument("gentzen");     // command line takes precedence over preferences
    else
    if (fCommandLineHausman)
      createDocument("hausman");     // command line takes precedence over preferences
    else
    if (fCommandLineHerrick)
      createDocument("herrick");     // command line takes precedence over preferences
    else
    if (fCommandLineHowson)
      createDocument("howson");     // command line takes precedence over preferences
    else
    if (fCommandLinePriest)
      createDocument("priest");     // command line takes precedence over preferences


    else
       {
      TPreferences.canonicalizeParserName(TPreferences.fParser);  //tidying up previous poor User input
      createDocument(TPreferences.fParser); //main business
    }

    if (TConstants.DEBUG)
      createDebugDocument();



// Sept 7, what is this doing    fScheme.readEvalWriteLoop();

    /*Dec 06, I don't think I am using sisc at the moment*/

/* Jan 13 07 not using sisc at present

SeekableInputStream heap=null;

  try{
    heap = new MemoryRandomAccessInputStream(TDeriverApplication.class.
                                             getResourceAsStream("sisc.png"));
  }catch (Exception e) {
            heap= null;
        }


   if (heap==null)
     heap=REPL.findHeap("");   // this is for the SISC lisp




if (heap==null)
   {
     TBrowser frontBrowser=getFrontBrowser();
     if (frontBrowser!=null)
        JOptionPane.showMessageDialog(frontBrowser, "SISC LISP did not load, could not find Heap","LISP Problem",
                                 JOptionPane.INFORMATION_MESSAGE);
 //  Toolkit.getDefaultToolkit().beep();
                return;
    }

 */


/*try {     not using SISC at present

AppContext ctx = new AppContext();
Context.register("main", ctx);
fSISCScheme = Context.enter("main");
REPL.loadHeap(fSISCScheme, heap);
//System.out.println(" done.");

// System.out.print((fSISCScheme.eval("(+ 1 1)")).toString());
}
 catch(Exception ex) {
      ex.printStackTrace();
    } */

//System.err.println("LIsp loaded properly");

  }

/*
 private void loadPreferences(){
   Preferences userPreferences = Preferences.userNodeForPackage(this.getClass());

int rightMargin=userPreferences.getInt("RightMargin",200);

if (rightMargin!=200)
  TBrowser.fDefaultMargin=rightMargin;

String user=userPreferences.get("User",null);

     if (user!=null){
        fUser=user;
    }

 }  */



  public boolean closeBrowser(TBrowser aBrowser){
    if (numOfBrowers()==1)
     {
       aBrowser.recordPreferences();

       System.exit(0); // It is the last window
     }
    else
     {
     removeBrowserFromList(aBrowser); // remove it from the list of windows
     aBrowser.setVisible(false);

   //  dispose();  This may be a bit strong it disposes of stylecontext which is shared.
     return true;
     }

    return
        true;
  }

  /* now in Preferences

  public static void determineUser(){
    while (fUser==null||fUser.equals("")){

      String reply = (String) JOptionPane.showInputDialog(null,
          "Please enter your name", "User Name",
          JOptionPane.QUESTION_MESSAGE, null, null, "");

      if (reply != null && !reply.equals(""))
        setCurrentUser(reply);
    }
  }

public static String getUser(){
  if (fUser==null)
    fUser="";       //should never happen, prbably dpm't need this
  return
      fUser;
}

   public static void setCurrentUser(String user){
  Preferences userPreferences = Preferences.userNodeForPackage(TheApplication.getClass());

  fUser=user;
  userPreferences.put("User",user);
}


   */


/*Note. A browser needs to know its application and its document. The document needs to know
its 'Journal'(which often is its Browser). So there is a type of circular
initialization. Also the constructors may need to have some of the stuff available. Here is an
approach.
i) create a document using the () constructor
ii) create a browser using (thatDocument, itsApplication) constructor
iii) finally call the thatDocument.setJournal(thatBrowser) method to provide the refernce to the browswer

Probably do all this from the application. */



public void createDocument(String type){     // a document is the data and it goes with a Browser which is its viewer
    TDeriverDocument document;
    TBrowser browser=null;

    if (type.length()>0&&(type.charAt(0)=='B'||type.charAt(0)=='b')){
      document = new TBergmannDocument();                  //i)   the document
      browser = new TBrowser(document,this);           //ii)  the browser
      document.setJournal(browser);                    //iii) the back reference
    }
    else if (type.length()>0&&(type.charAt(0)=='C'||type.charAt(0)=='c')){
      document = new TCopiDocument();                  //i)   the document
      browser = new TBrowser(document,this);           //ii)  the browser
      document.setJournal(browser);                    //iii) the back reference
    }
    
    else if (type.length()>0&&(type.charAt(0)=='D'||type.charAt(0)=='d')){
        document = new TDefaultDocument();                  //i)   the document
        browser = new TBrowser(document,this);           //ii)  the browser
        document.setJournal(browser);                    //iii) the back reference
      }

//document= new TCopiDocument(this);
    else if (type.length()>0&&(type.charAt(0)=='G'||type.charAt(0)=='g')){
      document = new TDeriverDocument();               //i)   the document

 //  document = new THowsonDocument();  //TEMP WHILE TESTING

      browser = new TBrowser(document,this);           //ii)  the browser
      document.setJournal(browser);                    //iii) the back reference
    }
    else if (type.length()>1&&
             (type.charAt(0)=='H'||type.charAt(0)=='h')&&
             (type.charAt(1)=='A'||type.charAt(1)=='a')){
      document = new THausmanDocument();               //i)   the document
      browser = new TBrowser(document,this);           //ii)  the browser
      document.setJournal(browser);                    //iii) the back reference
    }
    else if (type.length()>1&&
             (type.charAt(0)=='H'||type.charAt(0)=='h')&&
             (type.charAt(1)=='E'||type.charAt(1)=='e')){
      document = new THerrickDocument();               //i)   the document
      browser = new TBrowser(document,this);           //ii)  the browser
      document.setJournal(browser);                    //iii) the back reference
    }
    else if (type.length()>1&&
         (type.charAt(0)=='H'||type.charAt(0)=='h')&&
         (type.charAt(1)=='O'||type.charAt(1)=='o')){
  document = new THowsonDocument();               //i)   the document
  browser = new TBrowser(document,this);           //ii)  the browser
  document.setJournal(browser);                    //iii) the back reference
}
else if (type.length()>1&&
         (type.charAt(0)=='P'||type.charAt(0)=='p')&&
         (type.charAt(1)=='R'||type.charAt(1)=='r')){
  document = new TPriestDocument();               //i)   the document
  browser = new TBrowser(document,this);           //ii)  the browser
  document.setJournal(browser);                    //iii) the back reference
    }

    else
{
      document = new TDeriverDocument();               //i)   the document
      browser = new TBrowser(document,this);           //ii)  the browser
      document.setJournal(browser);                    //iii) the back reference

    }
    // the document creates its own browser

   // TBrowser browser = (TBrowser)document.getJournal();

   if (browser!=null){

     addBrowserToList(browser);

     //Validate frames that have preset sizes
     //Pack frames that have useful preferred size info, e.g. from their layout
     if (packFrame) {
       browser.pack();
     }
     else {
       browser.validate();
     }

     placeWindow(browser);
     browser.setVisible(true);
   }
  }

  public void createDebugDocument(){
    TDeriverDocument document;
    TBrowser browser=null;

    if (TConstants.DEBUG){
     document = new TDeriverDocument();               //i)   the document

  //  document = new THowsonDocument();                  // temp while testing

     browser = new TBrowser(document,this);           //ii)  the browser
     document.setJournal(browser);                    //iii) the back reference

     placeWindow(browser);
     browser.setVisible(true);
   }




      /*
    if (TConstants.DEBUG){
   //   fDebug = new TDebugDocument(this);
    // the document creates a browser

     TBrowser frame=(TBrowser)fDebug.getJournal();

     placeWindow(frame);
    frame.setVisible(true);


    } */

  }

/***************** Browser List *******************************/

public void addBrowserToList(TBrowser newBrowser){
   fBrowsers.add(newBrowser);
  }

public void removeBrowserFromList(TBrowser removedBrowser){
   fBrowsers.remove(removedBrowser);
    }

TBrowser getFirstBrowser(){
   return
   (TBrowser)(fBrowsers.get(0));
    }
int numOfBrowers(){
    return
        fBrowsers.size();
    }


/***************** End of Browser List *******************************/

    void placeWindow(TBrowser browser)
    {
      Dimension frameSize = browser.getSize();
       if (frameSize.height > fScreenSize.height) {
         frameSize.height = fScreenSize.height;
       }
       if (frameSize.width > fScreenSize.width) {
         frameSize.width = fScreenSize.width;
       }

       {int num =numOfBrowers();
       browser.setLocation(fXOffset*num, fYOffset*num); //Set the window's location
       }
    }



public void showEkit(TBrowser aBrowser){
  if (fEkit==null){
    fEkit = new Ekit(this);
    fEkit.setLocationRelativeTo(aBrowser);
  }


   fEkit.setVisible(true);

}

public String getFrontJournalText(){

  TBrowser front=getFirstBrowser();

  if (front!=null)
  return
      front.getJournalText();
else
  return
      null;

}






  public void setFrontJournalText(String text){

  TBrowser front=getFirstBrowser();

  if (front!=null)
     front.setJournalText(text);
}



public TShapePanel getFrontShapePanel(){

   TBrowser front=getFirstBrowser();

   if (front!=null){
     TDeriverDocument frontDoc=front.getDeriverDocument();
     if (frontDoc!=null){
       return
           frontDoc.getShapePanel();
     }
   }


   return
       null;

}


 public TBrowser getFrontBrowser(){
   return
       getFirstBrowser();
 }

 public TDeriverDocument getFrontDeriverDocument(){

   TBrowser front=getFirstBrowser();

   if (front!=null){
     TDeriverDocument frontDoc=front.getDeriverDocument();
     if (frontDoc!=null){
       return
           frontDoc;
     }
   }


   return
       null;

}










private static void configureSwitches(String[] argv){
  int input=0;                     // we will configure the application, showing and hiding menus
                                    // according to command line input

/*all that is done at present is that we read a number and put it in fConfiguration,
then any browser will read that prior to setting itself up

At the moment there are 4 simple versions
                                     */



    for(int i = 0; i < argv.length; i++)
       {
       if (argv[i].equals("-h") ||
          argv[i].equals("-H")  ||
          argv[i].equals("-?"))     {/* usage();*/ }
      else if(argv[i].equals("-t"))     {}

      else if(argv[i].startsWith("-p")){  //parser

        char selector= ((argv[i].substring(2, argv[i].length())).trim()).charAt(0);
        if (selector=='b'||selector=='B')
          fCommandLineBergmann=true;
        else if (selector=='c'||selector=='C')
          fCommandLineCopi=true;
        else if (selector=='g'||selector=='G')
            fCommandLineGentzen=true;
        else if (selector=='h'||selector=='H')
          fCommandLineHausman=true;                //gentzen is the default
        else if (selector=='e'||selector=='E')
          fCommandLineHerrick=true;
        else if (selector=='o'||selector=='O')
          fCommandLineHowson=true;
        else if (selector=='p'||selector=='P')
          fCommandLinePriest=true;         //gentzen is the default
      }
      else if(argv[i].startsWith("-l"))
                            {
                                    if(argv[i].indexOf('_') == 4 && argv[i].length() >= 7)
                                    {
                                     String       sLang = argv[i].substring(2, argv[i].indexOf('_'));
                                    }
                            }
      else if (argv[i].equals("1") ||    //TBrowser uses this when configuring
               argv[i].equals("2") ||
               argv[i].equals("3") ||
               argv[i].equals("4"))
                 {try{
                     input = Integer.parseInt(argv[i]);}
                  catch (NumberFormatException e){
                     input=0;}
                  if (input>0)
                     {fConfiguration=input;}
                 }
   }


}



//Main method
  public static void main(String[] argv) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }


/*TBergmannRandomProof dummy=    new TBergmannRandomProof(); // to get initialization

us.softoption.games.TRandomProof.testStringArray (us.softoption.games.TBergmannRandomProof.fTwelveLineProp);  // debugging */

 configureSwitches(argv);



 Calendar cal= Calendar.getInstance();
    long time=cal.getTimeInMillis();

 /*
  * 
  * 
  * 

Nov 29 2011 1322596551943  1322596551

Jan 27 2011 1296138983423  1296138983
  
  Nov 18 "expression" time: long = 1163849597106 1163849597

  March 11 "expression" time: long = 1173657563540 1173657563

  August 20 "expression" time: long = 1187626801017 1187626801

  Dec 2 08 "expression" time: long = 1228262963921  1228262963
  
  Jan 28 10                          1264721218842  1264721218

  30 day increment is long = 2592000000  millisec  2592000 sec
  10 day increment is                               864000 sec
  */

// String date =cal.toString();  "expression" time: long = 1141477800483
    
    //April 2012 1336926456102 ie 1336926456
    
    //November 2013 1384898351780 ie 1384898351

  TSplashScreen splash = new TSplashScreen(5000);

  splash.showSplashExit(TConstants.EXPIRY /*1154360383 +3*864000*/);   // This can cause the application to exit if it is out of date. At moment expires in 30days

 new TDeriverApplication();
  }

} // end of class


/*

               {*******************   TDeriverApplication   ******************************}

  TDeriverApplication = object(TApplication)

{$IFC WithHelpWindow}
    fHelpShows: boolean;
    fHelpWindow: TWindow;
{$ENDC }

               {Initialization}
    procedure TDeriverApplication.IDeriverApplication;

                                        {Document creation}
    function TDeriverApplication.DoMakeDocument (itsCmdNumber: CmdNumber): TDocument;
    OVERRIDE;


{$IFC WithHelpWindow}
    procedure TDeriverApplication.MakeAndOpenHelpView;
    function TDeriverApplication.DoMenuCommand (aCmdNumber: CmdNumber): TCommand;
    OVERRIDE;
{$ENDC}
    procedure TDeriverApplication.DoSetupMenus;
    OVERRIDE;


    function TDeriverApplication.KindOfDocument (itsCmdNumber: CmdNumber; itsAppFilePtr: AppFilePtr): CmdNumber;
    OVERRIDE;

                         {Handle data found left in the Clipboard by forces outside the application}
    function TDeriverApplication.MakeViewForAlienClipboard: TView;
    OVERRIDE;

    procedure TDeriverApplication.SFGetParms (itsCmdNumber: CmdNumber; var dlgID: INTEGER; var where: POINT; var fileFilter, dlgHook, filterProc: ProcPtr; typeList: HTypeList);
    OVERRIDE;
               {Sets up the data structures required by StdFile (the ToolBox}
{                    routine that puts up the Open... dialog) so that the several file}
{                    types supported by this application are handled.}

                                        {Debugging}
                                        {$IFC qDebug}
    procedure TDeriverApplication.IdentifySoftware;
    OVERRIDE;
                                        {$ENDC}

   end;

end of class definition


 {$IFC THINK_PASCAL}
 unit TDeriverApp;



 interface





           (*on application launch, Gentzen is default*)

  uses
   DerImpNotes, SysEqu, Traps, ULoMem, UMacAppUtilities, UPatch, UObject, UViewCoords, UMemory, UFailure, UMenuSetup, UList, PrintTraps, UAssociation, UMacApp, UTEView,
 { � MacApp }

 { � Building Blocks }
   UPrinting, UDialog, UGridView,
 { � Implementation Use }
   Picker, UStream, ULogicGlobals90, LispUnit, UFormulaIntf, UProofViewIntf, UCopiIntf, USemanticTestIntf,
 (*   $SETC bestpath:=TRUE	  this compiler variable also in Main*)
   UReAssemble90, UDeriverIntf;                                                                                                                                 												      			      (*only compile this if running theorem prover any gentzen systems*)

 (*you'll also have to put the semi-colon earlier if not compiled*)
                         {reassemble is also commented out in the MakeFile and so are its segments in the}
 {			resources}


 implementation

 {$ENDC}



 {********************************  TDeriverApplication ***********************}

 {$S AInit}

  procedure TDeriverApplication.IDeriverApplication; {Initialize the application}

   var
    fromPt, toPt: Point;
    r: Rect;

    top: INTEGER;
    i: INTEGER;
    { aJournalWindow: TJournalWindow;}
                 { aProofWindow: TMyProofWindow;}




   procedure SetRGBColor (var RGB: RGBColor; red, green, blue: INTEGER);

   begin
    RGB.red := red;
    RGB.green := green;
    RGB.blue := blue;
   end;

   function SetHStripes: Pattern;

    var
     tempPat: Pattern;

   begin
    tempPat := black;

    tempPat[0] := 0;
    tempPat[1] := 0;
    tempPat[2] := 0;
    tempPat[4] := 0;
    tempPat[5] := 0;
    tempPat[6] := 0;
    SetHStripes := tempPat;
   end;

   function SetVStripes: Pattern;

    var
     tempPat: Pattern;

   begin
    tempPat := LtGray;

    tempPat[0] := 0;
    tempPat[2] := 0;
    tempPat[4] := 0;
    tempPat[6] := 0;
    SetVStripes := tempPat;
   end;

   procedure InitgErrorsArray; {check, make these string resources}

    var
     i: INTEGER;

   begin

    for i := 1 to 15 do
     gErrorsArray[i] := '';

    gErrorsArray[1] := concat(gCr, '(*Universe must be non-empty.*)');
    gErrorsArray[2] := concat(gCr, '(*The arity of a relation should be no greater than 2.*)', gCr, '(*Any relation of arity n, n>2, can be represented*)', gCr, '(*as a conjunction of n+1 2-arity relations.*)');
    gErrorsArray[3] := concat(gCr, 'You win. You are right-- it is true!', gCr);
    gErrorsArray[4] := concat(gCr, 'I win. You are mistaken-- it is false!', gCr);
    gErrorsArray[5] := concat(gCr, 'You win. You are right-- it is false!', gCr);
    gErrorsArray[6] := concat(gCr, 'I win. You are mistaken-- it is true!', gCr);
    gErrorsArray[7] := concat(gCr, 'I win. I am right-- it is true!', gCr);
    gErrorsArray[8] := concat(gCr, 'You win. I am mistaken-- it is false!', gCr);
    gErrorsArray[9] := concat(gCr, 'I win. I am right-- it is false!', gCr);
    gErrorsArray[10] := concat(gCr, 'You win. I am mistaken-- it is true!', gCr);
    gErrorsArray[11] := concat(gCr, '(* is not a term.*)', gCr);
    gErrorsArray[12] := concat(gCr, '(*Selection is illformed.*)', gCr);
    gErrorsArray[13] := concat(gCr, '(* is not a constant.*)', gCr);
    gErrorsArray[14] := concat(gCr, '(* is not a variable.*)', gCr);

   end;

   procedure InitgShapesArray;
    var
     circle: TCircle;
     propertyF, propertyG, propertyH: TPropertyF;

     relationR: TRelationR;
     relationS: TRelationS;
     identity: TIdentity;
     interpretationBoard: TInterpretationBoard;
   begin
    SetRect(r, 0, 0, 0, 0); {Not displayed in palette but needed in}
 {                             gShapesArray}

    New(interpretationBoard);
    FailNil(interpretationBoard);
    interpretationBoard.IInterpretationBoard(r, IDInterpretationBoard, nil);

                 {thisresets rect}


    if (IDInterpretationBoard <= kShapesInPalettePlus1) then
     gShapesArray[IDInterpretationBoard] := interpretationBoard;

    SetRect(r, (kPaletteWidth div 2 - kCircleRadius), (((3 * kPaletteWidth) div 2) - kCircleRadius), (kPaletteWidth div 2 + kCircleRadius), (((3 * kPaletteWidth) div 2) + kCircleRadius)); {Define the prototype}
 {                                                      shapes}
    New(circle);
    FailNil(circle);
    circle.ICircle(r, IDCircle);
    circle.fName := 'a';
    if (IDCircle <= kShapesInPalettePlus1) then
     gShapesArray[IDCircle] := circle;

    SetRect(r, 2, kPaletteWidth + 2, 18, kPaletteWidth + 18); {MF}
    OffSetRect(r, 0, kPaletteWidth);

    New(propertyF);
    FailNil(propertyF);
    propertyF.IPropertyF(r, IDPropertyF);
    propertyF.fShade := cWhite;
    propertyF.fName := 'A';
    if (IDPropertyF <= kShapesInPalettePlus1) then
     gShapesArray[IDPropertyF] := propertyF;

    OffSetRect(r, 0, kPaletteWidth);
    New(relationR);
    FailNil(relationR);

    SetPt(fromPt, r.left, r.bottom);
                 {SetPt(fromPt, r.left-5, r.bottom+5); }
 {    SetPt(toPt, r.right + 2, r.top - 2);}
    SetPt(toPt, r.right, r.top);

    relationR.IRelationR(fromPt, toPt, r, IDRelationR);
    relationR.fname := 'A';
    if (IDRelationR <= kShapesInPalettePlus1) then
     gShapesArray[IDRelationR] := relationR;

    OffSetRect(r, 0, kPaletteWidth);
    New(relationS);
    FailNil(relationS);

    SetPt(fromPt, r.left, r.bottom);
         {	SetPt(fromPt, r.left-8, r.bottom+8); }
    { SetPt(toPt, r.right + 2, r.top - 2);}
    SetPt(toPt, r.right, r.top);

    relationS.IRelationS(fromPt, toPt, r, IDFunction);
    relationS.fname := 'a';

    if (IDFunction <= kShapesInPalettePlus1) then
     gShapesArray[IDFunction] := relationS;

    OffSetRect(r, 0, kPaletteWidth);
    New(Identity);
    FailNil(Identity);

    SetPt(fromPt, r.left, r.bottom);
         {	SetPt(fromPt, r.left-2, r.bottom+2); }
     {SetPt(toPt, r.right + 2, r.top - 2);}
    SetPt(toPt, r.right, r.top);

    Identity.IIdentity(fromPt, toPt, r, IDIdentity);
    Identity.fname := 'a';
    if (IDIdentity <= kShapesInPalettePlus1) then
     gShapesArray[IDIdentity] := Identity;

   end;

   procedure InitgChoiceArray;
    var
     i: integer;
   begin
    top := 0;
    for i := 0 to kShapesInPalette do {Define the palette choices}
     begin
      SetRect(r, 0, top, kPaletteWidth, top + kPaletteWidth);
      gChoiceArray[i] := r;
      top := top + kPaletteWidth;
     end;

 {$IFC identity}

 {$ELSEC }
    SetRect(r, 0, 0, 0, 0);     {this makes the palette smaller leaving out functions}
    gChoiceArray[IDFunction] := r;
    gChoiceArray[IDIdentity] := r;
 {$ENDC}


   end;


 {$IFC WithHelpWindow}
   procedure InitHelp;

   begin
    fHelpShows := false;
    fHelpWindow := nil;
   end;
 {$ENDC}

  begin
   gToMarker := TRUE;
   gHighLight := TRUE;
   gBlank := ' ';
   gCurrCh := ' ';

   gCr := ' '; {length attribute}
   gCr[1] := CHR(chRturn);

   InitgErrorsArray;

   if gConfiguration.hasColorQD then
    gMBarDisplayed := kColorMenuBar
   else
    gMBarDisplayed := kNonColorMenuBar;

   IApplication(kDocType); {Generic initialization-- change this for the one you want}

   gPat[cWhite] := White; {Fill the global array of patterns}
   gPat[cHStripes] := SetHStripes;
   gPat[cVStripes] := SetVStripes;
   gPat[cLtGray] := LtGray;
   gPat[cGray] := Gray;

   SetRGBColor(gRGBWhite, $FFFF, $FFFF, $FFFF);
   SetRGBColor(gRGBBlack, 0, 0, 0);

     {Set the standard margins to use in the clipboard}
   SetPt(gClipMargin, 16, 16);

   InitgShapesArray;

   InitgChoiceArray;

   with gArwBitMap do {Define the arrow bitmap to be drawn in the palette}
    begin
     rowBytes := 2;
     SetRect(bounds, 0, 0, 16, 16);
     baseAddr := @arrow.data;
    end;


   SetRect(gPaletteExtent, 0, 0, kPaletteWidth, top);

   gPasteReplacesSelection := FALSE;
   gConstrainDrags := true;
   gStaggerCount := 0;

   if gConfiguration.hasColorQD then
    begin
   {Unlike GetCursor, GetCCursor makes a copy of the color cursor}
 {   resource.  Therefore, you should make one call to GetCCursor}
 {   and multiple calls to SetCCursor}
     gRainbowArrow := GetCCursor(kRainbowArrow);
     FailNil(gRainbowArrow);
    end;

 {$IFC WithHelpWindow}
   InitHelp;
 {$ENDC WithHelpWindow}

   gOpenNode := nil;


  end;

 {$S AOpen}

  function TDeriverApplication.DoMakeDocument (itsCmdNumber: CmdNumber): TDocument;
 {NB: Not used to create the document for a shape view in the Clipboard}

   var
    shapeDocument: TDeriverDocument;   {check MF changes}
    anCopiDocument: TCopiDocument;

  begin
   case itsCmdNumber of

    cNewCopi:
     begin
      New(anCopiDocument);
      FailNil(anCopiDocument);
      anCopiDocument.ICopiDocument(kCopiDocType);
      DoMakeDocument := anCopiDocument;
     end;

    otherwise
     begin      {plain cNew    is newGentzen}
      New(shapeDocument);
      FailNil(shapeDocument);
      shapeDocument.IShapeDocument(kDocType);
      DoMakeDocument := shapeDocument;
     end;
   end;
  end;


 {$IFC WithHelpWindow}
  procedure TDeriverApplication.MakeAndOpenHelpView;

   var
    aView, aParentView: TView;
    aTEView: TTEView;
    aHandler: TStdPrintHandler;
    HelpTextLength: longint;
    HelpHdl: handle;

  begin
   aParentView := NewTemplateWindow(kHelpViewRsrcID, nil);
   FailNil(aParentView);

   aView := aParentView.FindSubView('VW02');


   aTEView := TTEView(aView);

   HelpTextLength := SizeResource(GetResource('HELP', kHelpRSRCID));

   if HelpTextLength < 0 then
    sysBeep(5)
   else
    begin
     HelpHdl := NewHandle(HelpTextLength); {put this with opening help}
     HelpHdl := GetResource('HELP', kHelpRSRCID);

     aTEView.StuffText(HelpHdl); { Stuff the initial text in }

     new(aHandler);
     FailNIL(aHandler);
     aHandler.IStdPrintHandler(nil, aTEView, FALSE, TRUE, FALSE); { its document }

    end;

   SELF.fHelpWindow := TWindow(aParentView);

   SELF.fHelpWindow.Open;
 {SelectWindow(aWindow.fWmgrWindow);}
  end;

 {$ENDC}


 {$S ASelCommand}

 {$IFC WithHelpWindow}

  function TDeriverApplication.DoMenuCommand (aCmdNumber: CmdNumber): TCommand;
   OVERRIDE;



   procedure ToggleHelpWindow;
   {Hides or Shows & Selects the indicated window, depending on aFlag}

   begin
    if SELF.fHelpShows then
     begin
      fHelpShows := FALSE;
      fHelpWindow.Close;
     end
    else
     begin
      fHelpShows := TRUE;
      SELF.MakeandOpenHelpView;   {new}

     end;
   end;


  begin
   DoMenuCommand := gNoChanges;
   case aCmdNumber of


    cHelp:
     ToggleHelpWindow;


    otherwise
     DoMenuCommand := inherited DoMenuCommand(aCmdNumber);
   end; {Case}
  end;

 {$ENDC}

 {$S MyRes}

  procedure TDeriverApplication.DoSetupMenus;
   OVERRIDE;

  begin
   inherited DoSetupMenus;

   Enable(cNewCopi, TRUE);


 {$IFC WithHelpWindow}
   Enable(cHelp, true);

   if SELF.fHelpShows then
    SetCmdName(cHelp, 'Close Help')
   else
    SetCmdName(cHelp, 'Open Help'); {put in resources}

 {$ENDC}
  end;

 {$IFC qDebug}

  procedure TDeriverApplication.IdentifySoftware;
   OVERRIDE;

  begin
   WriteLn('Deriver Source date: 12 May  90; Compiled on: ', COMPDATE, ' @ ', COMPTIME);
   inherited IdentifySoftware;
  end;
 {$ENDC}

  function TDeriverApplication.KindOfDocument (itsCmdNumber: CmdNumber; itsAppFilePtr: AppFilePtr): CmdNumber;
   OVERRIDE;
   var
    itsFileType: OSType;
  begin
   if itsCmdNumber = cFinderNew then

 {$IFC openswithCopi}
    KindOfDocument := cNewCopi   {app's icon opened from the Finder;}
 {												launch doc ESL}
 {$ELSEC}
    KindOfDocument := cNew   {app's icon opened from the Finder;}
 {												launch Gentzen doc}
 {$ENDC}

   else if itsAppFilePtr = nil then
                         {must be cNewBoxDocument or cNewTextDocument already}
    KindOfDocument := itsCmdNumber
   else
                         {otherwise, must be an existing disk file; look at file-type}
    begin
     itsFileType := itsAppFilePtr^.fType;
     if itsFileType = kDocType then
      KindOfDocument := cNew
     else if itsFileType = kCopiDocType then
      KindOfDocument := cNewCopi
 {$IFC qDebug}
     else
      ProgramBreak('No Kind of Doc matched');
 {$ENDC}
 {	PSC Warning: Floating Semicolon	}
    end;
  end;

 {$S AClipboard}

  function TDeriverApplication.MakeViewForAlienClipboard: TView;
   OVERRIDE;
  {Launch a view to represent the data found in the Clipboard at}
 {  application start-up time, or when returning from an excursion}
 {  to Switcher, or when returning from a Desk Accessory}

   var
    offset: LONGINT;
    clipShapeView: TShapeView;
    clipShapeDoc: TDeriverDocument;
    clipShapes: ShapesOnClipboard;
    aNewShape: TShape;
    i: INTEGER;
    err: LONGINT;
    fi: FailInfo;

   procedure HdlFailure (error: OSErr; message: LONGINT);

   begin
    if clipShapes <> nil then
     DisposHandle(Handle(clipShapes));
    clipShapeDoc.Free;
   end;

  begin
   clipShapes := nil;

     {Before doing anything else, make sure the scrap contains shapes}
   if GetScrap(nil, kShapeClipType, offset) > 0 then {found my kind of data }
    begin
     New(clipShapeDoc);
     FailNil(clipShapeDoc);
     clipShapeDoc.IShapeDocument(kDocType);

     CatchFailures(fi, HdlFailure);
     New(clipShapeView);
     FailNil(clipShapeView);
     clipShapeView.IShapeView(clipShapeDoc, nil, true);
       {$IFC FALSE}
  {What do we do with these?}
     with clipShapeView do
      begin
       fCanSelect := FALSE;
       fWrittenToDeskScrap := true;
      end;
       {$ENDC}

     clipShapes := ShapesOnClipboard(NewPermHandle(0));
     FailNil(clipShapes);
     FailSpaceIsLow;
     err := GetScrap(Handle(clipShapes), kShapeClipType, offset);
   {Only a negative result indicates an error--FailOSErr considers}
 {   any non-zero result an error.}
     if err < 0 then
      FailOSErr(err);

     for i := 0 to clipShapes^^.theNumberOfShapes - 1 do
      begin
       aNewShape := TShape(gShapesArray[clipShapes^^.theShapes[i].theId].Clone);
       FailNil(aNewShape);
       with aNewShape, clipShapes^^.theShapes[i] do
        begin
        fShade := theShade;
        fColor := theColor;
        fExtentRect := theRect;
        end;
       clipShapeDoc.AddShape(aNewShape);
      end;

     Success(fi);
     MakeViewForAlienClipboard := clipShapeView;
    end
   else
    MakeViewForAlienClipboard := inherited MakeViewForAlienClipboard;
  end;

 {$S ANonRes}
  procedure TDeriverApplication.SFGetParms (itsCmdNumber: CmdNumber; var dlgID: INTEGER; var where: Point; var fileFilter, dlgHook, filterProc: ProcPtr; typeList: HTypeList);
   OVERRIDE;
    {The idea is that this HTypeList thing is a handle to a structure which}
 {   		holds the list of document types you can deal with.  Four bytes are}
 {		required for each document type.  Hence, you need to multiply the total}
 {		number of types of documents you deal with times four, to come up with}
 {		the size to set the Handle to (See SetHandleSize call below).}
 {}
 {	The first element of the the type list is automatically filled for you by}
 {		MacApp; hence, if you handle more than one type, you only need to}
 {		stuff the typelist structure with the types of further documents.}

  begin
   inherited SFGetParms(itsCmdNumber, dlgID, where, fileFilter, dlgHook, filterProc, typeList);

   SetHandleSize(Handle(typeList), 8); {i.e., allow for 2 file types}
                                 {typeList^^[1] is already the main file type (kDocType) which is the gentzen one}
   typeList^^[2] := kCopiDocType;




  end;




 {$IFC THINK_PASCAL}

 end.

 {$ENDC}



*/
