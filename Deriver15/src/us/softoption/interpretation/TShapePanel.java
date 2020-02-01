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

package us.softoption.interpretation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import us.softoption.editor.TDeriverDocument;
import us.softoption.infrastructure.FunctionalParameter;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

/**
 This both controls shapes and provides view
 */

/*

NB. In the Pascal we implemented 'filtering' in which various operations, eg color change,
did not really take place first off, an illusion was produced instead to permit undo.
This lead to

{Enumeration of shapes}
  {NB:  EachShapeDo iterates through the list of shapes.}
{     EachPotentialShape iterates through all the shapes in document}
{       plus any 'pastee' shapes which may have been added by a}
{     not-yet-committed PASTE.}
{     EachVirtualShape iterates through only those shapes that appear}
{       to be present at the moment to the USER , given the}
{     UNDO/REDO status of the last command.  Thus it iterates}
{     through some but possibly not all of the the shapes in the}
{     document, and possibly also through not-yet-in-the-document}
{     pastees}

We are trying not to do this here.


*/

/* Aug 06 We used to have Delete and Cut on the edit Menu. I am not clear why we would
want both so I have removed Delete (just not adding the item to the menu bar*/

/*Having a lot of trouble with accelerators, basically because they are global, so am going to do without
The shapes edit menu interacts with the text edit menu  Aug 06 Undo has it, but there is only one Undo manager

 Actually, doing better now with requesting focus in mouse. Then registering keyboard actions.
 Finally faking keyboard accelerator to put up clover leaf on menu*/

public class TShapePanel extends JPanel implements ChangeListener   {

  //  private static TShapePanel fGlobalShapePanel;

   public static  RenderingHints fQualityRendering =
  new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);{
                fQualityRendering.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);}


static final char chBlank = ' ';

public static final String gIndividualNames="abcdefghijklmnopqrstuv";
                                          /*zero-order functors a..v are constants, w..z are variables
                                          the binary predefineds like + are treated as special cases
                                          and there are the numerals 0,1,2; conceptually gFunctors is a
                                          set of char*/

static final boolean kWithoutSelectees=true;


    private Color fBackgroundColour;
    private List fShapes= new ArrayList();
  TPalette fPalette = new TPalette(this);

  InputListener fListener = new InputListener();

  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenu1 = new JMenu();
  JMenuItem pasteMenuItem = new JMenuItem();
  JMenuItem copyMenuItem = new JMenuItem();
  JMenuItem cutMenuItem = new JMenuItem();
  JMenuItem selectAllMenuItem = new JMenuItem();
//  JMenuItem jMenuItem4 = new JMenuItem();
//  JMenuItem jMenuItem5 = new JMenuItem();



  //undo helpers
  protected UndoAction fUndoAction = new UndoAction();
  protected RedoAction fRedoAction = new RedoAction();
  protected UndoManager fUndoManager = new UndoManager();
  private UndoableEditListener fUndoListener;     // only allows one, more typical to have list

  private ArrayList fUndoListeners = new ArrayList();  // july 06 implementatation of list of listeners

  static AbstractUndoableEdit fLastEdit=null; // this is so we can restrict the undos to 1


  TSemantics fSemantics;
  JMenu jMenu2 = new JMenu();
  JMenu jMenu3 = new JMenu();
  JMenuItem yellowMenuItem = new JMenuItem();
  JMenuItem blueMenuItem = new JMenuItem();
  JMenuItem redMenuItem = new JMenuItem();
  JMenuItem greenMenuItem = new JMenuItem();
  JMenuItem magentaMenuItem = new JMenuItem();
  JMenuItem cyanMenuItem = new JMenuItem();

  JMenuItem jMenuItem7 = new JMenuItem();
  JMenuItem jMenuItem8 = new JMenuItem();
  JMenuItem hatchMenuItem = new JMenuItem();

  //TBrowser fJournalWindow=null;

  TDeriverDocument fDeriverDocument=null;
  JMenuItem deleteMenuItem = new JMenuItem();

  boolean fUseIdentity=false;    // for getting more menu items independently of Preferences

  public TShapePanel() {
commonInitialization();
  }

  public TShapePanel(TDeriverDocument itsDocument,String title, int width, int height, Color bgColour)
    {

   commonInitialization();  // let jbinit create toolbar

   fDeriverDocument=itsDocument;

setPreferredSize(new Dimension(width, height));
        fBackgroundColour = bgColour;

        this.setBackground(fBackgroundColour);

        addMouseListener(fListener);
        addMouseMotionListener(fListener);

    }


public TShapePanel(TDeriverDocument itsDocument,String title,
                   int width, int height,
                   Color bgColour,
                   boolean useIdentity)
    {

      fUseIdentity=useIdentity;

      fPalette = new TPalette(this,fUseIdentity);

   commonInitialization();  // we need to set identity use before jbInit adds it

   fDeriverDocument=itsDocument;

 setPreferredSize(new Dimension(width, height));
  fBackgroundColour = bgColour;

        this.setBackground(fBackgroundColour);



        addMouseListener(fListener);
        addMouseMotionListener(fListener);


    }


 void commonInitialization(){
   try {
     jbInit();

     fSemantics = new TSemantics(fShapes, gIndividualNames);
     createInterpretationBoard(fSemantics);

   }
   catch (Exception e) {
     e.printStackTrace();
   }
 }






  private void jbInit() throws Exception {



    jMenu1.setText("Edit");
    jMenu1.addMouseListener(new TShapePanel_jMenu1_mouseAdapter(this));
    jMenu1.addActionListener(new TShapePanel_jMenu1_actionAdapter(this));
  //  jMenuItem5.setText("Undo");
 //   jMenuItem4.setText("Redo");
    cutMenuItem.setText("Cut");
    cutMenuItem.addActionListener(new TShapePanel_cutMenuItem_actionAdapter(this));
    cutMenuItem.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));


    copyMenuItem.setText("Copy");
    copyMenuItem.addActionListener(new TShapePanel_copyMenuItem_actionAdapter(this));
    copyMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));


    pasteMenuItem.setText("Paste");
    pasteMenuItem.addActionListener(new TShapePanel_pasteMenuItem_actionAdapter(this));
   pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));


    jMenu2.setText("Pattern");
    jMenu3.setText("Color");
    redMenuItem.setText("Red");
    redMenuItem.addActionListener(new TShapePanel_redMenuItem_actionAdapter(this));
    blueMenuItem.setText("Blue");
    blueMenuItem.addActionListener(new TShapePanel_blueMenuItem_actionAdapter(this));
    yellowMenuItem.setText("Yellow");
    yellowMenuItem.addActionListener(new TShapePanel_yellowMenuItem_actionAdapter(this));


    greenMenuItem.setText("Green");
    greenMenuItem.addActionListener(new TShapePanel_greenMenuItem_actionAdapter(this));
    magentaMenuItem.setText("Magenta");
    magentaMenuItem.addActionListener(new TShapePanel_magentaMenuItem_actionAdapter(this));
    cyanMenuItem.setText("Cyan");
    cyanMenuItem.addActionListener(new TShapePanel_cyanMenuItem_actionAdapter(this));




    jMenuItem8.setText("Plain");
    jMenuItem8.addActionListener(new TShapePanel_jMenuItem8_actionAdapter(this));
    jMenuItem7.setText("Spotty");
    jMenuItem7.addActionListener(new TShapePanel_jMenuItem7_actionAdapter(this));
    hatchMenuItem.setText("Hatch");
    hatchMenuItem.addActionListener(new TShapePanel_hatchMenuItem_actionAdapter(this));


    selectAllMenuItem.setText("Select All");
    selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectAllMenuItem_actionPerformed(e);
      }
    });
    selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    /*this really is only to display the clover leaf A, because the keyboardactions do the work (this is masked by other editors)*/


    initializeKeyBoardActions();







    deleteMenuItem.setText("Delete");
    deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke('B'));
    deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteMenuItem_actionPerformed(e);
      }
    });






    fUndoAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    jMenu1.add(fUndoAction);

    fRedoAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    jMenu1.add(fRedoAction);

  UndoableEditListener aListener= new UndoableEditListener(){
                           public void undoableEditHappened(UndoableEditEvent e)
                               {
                                 //Remember the edit and update the menus.
                               fUndoManager.addEdit(e.getEdit());
                               fUndoAction.updateUndoState();
                               fRedoAction.updateRedoState();
                                }
                            };

    addUndoableEditListener(aListener);




    jMenuBar1.add(jMenu1);
    jMenuBar1.add(jMenu2);
    jMenuBar1.add(jMenu3);
//    jMenu1.add(jMenuItem5);
//    jMenu1.add(jMenuItem4);
    jMenu1.addSeparator();
    jMenu1.add(cutMenuItem);
    jMenu1.add(copyMenuItem);
    jMenu1.add(pasteMenuItem);
   // jMenu1.add(deleteMenuItem);  Aug 06
    jMenu1.addSeparator();
    jMenu1.add(selectAllMenuItem);

 /*   this.add(jMenuBar1,null);
    this.add(fPalette, null);    March 09*/
    
    this.setLayout(new GridBagLayout());

	this.add(jMenuBar1,new GridBagConstraints(0, 0, 1, 1, 0.5, 1.0
	       ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 2), 0, 0));

	this.add(fPalette,new GridBagConstraints(1, 0, 1, 1, 0.5, 1.0
	       ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
/*March 09 */

    jMenu3.add(redMenuItem);
    jMenu3.add(blueMenuItem);
    jMenu3.add(yellowMenuItem);
    jMenu3.add(greenMenuItem);
    jMenu3.add(magentaMenuItem);
    jMenu3.add(cyanMenuItem);

    jMenu2.add(jMenuItem8);
    jMenu2.add(jMenuItem7);
    jMenu2.add(hatchMenuItem);

 this.setFocusable(true); ///
 //  addKeyListener(fPalette); ////

addKeyListener(new KeyAdapter(){      // this is not working, don't have focus?

   public void keyTyped(KeyEvent e) {

     char ch = e.getKeyChar();

     if (ch=='\b')
       performDelete();
   }


 });

  }


private void initializeKeyBoardActions(){

  // mouse down requests focus, then ...


  registerKeyboardAction(new java.awt.event.ActionListener() {
                          public void actionPerformed(ActionEvent e) {
                          cutMenuItem_actionPerformed(e);}},
                       "Cut",
                       KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false),
                       JComponent.WHEN_FOCUSED);
                   registerKeyboardAction(new java.awt.event.ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                           copyMenuItem_actionPerformed(e);}},
                                        "Copy",
                                        KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false),
                                        JComponent.WHEN_FOCUSED);
                                    registerKeyboardAction(new java.awt.event.ActionListener() {
                                                          public void actionPerformed(ActionEvent e) {
                                                          pasteMenuItem_actionPerformed(e);}},
                                                       "Paste",
                                                       KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false),
                                                       JComponent.WHEN_FOCUSED);


  registerKeyboardAction(new java.awt.event.ActionListener() {
                          public void actionPerformed(ActionEvent e) {
                          selectAllMenuItem_actionPerformed(e);}},
                       "Select All",
                       KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false),
                       JComponent.WHEN_FOCUSED);

  registerKeyboardAction(new java.awt.event.ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                             deleteMenuItem_actionPerformed(e);}},
                                          "Delete",
                                          KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0),
                           JComponent.WHEN_FOCUSED);


}





public List getShapeList(){
  return
      fShapes;
}

public void setShapeList(List theShapes){   // we assume here that we are not already listening
                                            // to the incoming shapes
   fShapes=theShapes;

   if (fSemantics!=null)
     fSemantics.setShapeList(theShapes);

   setSemanticsForInterpretationBoards();

   listenToAll(fShapes);

   if (fSemantics.interpretationChanged());  //NEW ENSURE UPDATE AFTER FILE OPEN

   }


   public boolean getUseIdentity(){
       return
           fUseIdentity;
     }

   public void setUseIdentity(boolean use){
     fUseIdentity = use;
   }

public TSemantics getSemantics(){
  return
     fSemantics;
}

public void constructDrawing(ArrayList interpretation){
  TDrawingConstructor drawingConstructor= new TDrawingConstructor(interpretation);
  drawingConstructor.constructDrawing();
}


/*

For the drawing, we form a Venn diagram out of three rectangles for the three properties. The
 vertical lines are 1,3,8,5,12,10,14 (in order) and the horizontal ones 2,4,7,6,9,11,13. (I would
 put this on a more rational basis, but we have the Pascal legacy code to stick with). On this grid,
 F is 1,2,5,6 G is 8,4,10,9 H is 3,7,12,11. And these overlap so as to give areas for FandG etc.

    */

public class TDrawingConstructor{
  final int spacing=50;
  int line1,line2,line3,line4,line5,line6,line7,line8,line9,line10,line11,line12,line13,line14;
  String universe; 

  String predicate1="";
  String predicate2="";
  String predicate3="";

  String FandGandH="";
  String FandGandnotH="";
  String FandnotGandH="";
  String FandnotGandnotH="";
  String notFandGandH="";
  String notFandGandnotH="";
  String notFandnotGandH="";
  String notFandnotGandnotH="";

  String relation1="";
  String relation2="";
  String relation3="";

  String rExtension="";
  String sExtension="";
  String tExtension="";



  ArrayList fInterpretation;

 /* I made a slip here, and you should watch for it if looking at the Pascal code. In Pascal,
  rectangles go (left,top,right,bottom), but Jave new Rectangle (left,top,width,height) ! That is
  we need new Rectangle(left,top,right-left,bottom-top). */




TDrawingConstructor (ArrayList interpretation){
   fInterpretation=interpretation;
}

void findPropertyExtensions(){

   int length = TParser.gPredicates.length();
   String searchPredicate="";

   String extension1="";
   String extension2="";
   String extension3="";
   String outputStr ="";

   int count=0;

   for (int i=0;i<length;i++){
     searchPredicate=TParser.gPredicates.substring(i,i+1);

     outputStr=TFormula.extensionOfUnaryPredicate(fInterpretation, searchPredicate);

     if ((outputStr!=null)&&(outputStr.length()>0)){
       count+=1;
       switch (count) {
         case 1: predicate1=searchPredicate;
                 extension1=outputStr;
                 break;
         case 2: predicate2=searchPredicate;
                 extension2=outputStr;
                 break;
         case 3: predicate3=searchPredicate;
                 extension3=outputStr;
                 break;
       }
      }
    }

    /*we now know the extensions but we need to know the 'types' */

    int type=0;
    char search;

    for (int i=0;i<universe.length();i++){
      search=universe.charAt(i);
      type=0;

      if (extension1.indexOf(search)>-1)
        type+=4;
      if (extension2.indexOf(search)>-1)
        type+=2;
      if (extension3.indexOf(search)>-1)
        type+=1;

      switch (type){
        case 7: FandGandH+=search;break;
        case 6: FandGandnotH+=search;break;
        case 5: FandnotGandH+=search;break;
        case 4: FandnotGandnotH+=search;break;
        case 3: notFandGandH+=search;break;
        case 2: notFandGandnotH+=search;break;
        case 1: notFandnotGandH+=search;break;
        case 0: notFandnotGandnotH+=search;break;
      }



   }


}

 void findRelationsExtensions(){

   int length = TParser.gPredicates.length();
   String searchRelation="";


   String outputStr ="";

   int count=0;

   for (int i=0;i<length;i++){
     searchRelation=TParser.gPredicates.substring(i,i+1);

     outputStr=TFormula.extensionOfBinaryPredicate(fInterpretation, searchRelation);

     if ((outputStr!=null)&&(outputStr.length()>0)){
       count+=1;
       switch (count) {
         case 1: relation1=searchRelation;
                 rExtension=outputStr;
                 break;
         case 2: relation2=searchRelation;
                 sExtension=outputStr;
                 break;
         case 3: relation3=searchRelation;
                 tExtension=outputStr;
                 break;
       }
      }
    }
}



public void constructDrawing(){


/*we will construct the drawing then call update to get the semantic interpretation right*/

   resetToEmpty();    // should be empty anyway

//   universe=TFormula.atomicTermsInListOfFormulas(fInterpretation);
   
   Set <String> universeStrSet=TFormula.atomicTermsInListOfFormulas(fInterpretation);
   
    universe="";
   
   for (Iterator i=universeStrSet.iterator();i.hasNext();)
   	universe+=i.next();
    

   findPropertyExtensions();

   findRelationsExtensions();

   determineLines();

   createProperties();

   createIndividuals();

   createRelations();

   deselect();            //all shapes are created selected


  fPalette.check();       // some of the names may need updating

  repaint();

}

void createIndividuals(){

  if(FandnotGandnotH.length()>0)
    placeIndividuals(FandnotGandnotH,line1,line2,line3,line6);
  if(notFandGandnotH.length()>0)
    placeIndividuals(notFandGandnotH,line12,line7,line10,line9);
  if(notFandnotGandH.length()>0)
    placeIndividuals(notFandnotGandH,line3,line9,line12,line11);

  if(FandGandnotH.length()>0)
    placeIndividuals(FandGandnotH,line8,line4,line5,line7);
  if(FandnotGandH.length()>0)
    placeIndividuals(FandnotGandH,line3,line7,line8,line6);
  if(notFandGandH.length()>0)
    placeIndividuals(notFandGandH,line5,line7,line12,line11);

  if(FandGandH.length()>0)
    placeIndividuals(FandGandH,line8,line7,line5,line6);
  if(notFandnotGandnotH.length()>0)
    placeIndividuals(notFandnotGandnotH,line4,line11,line14,line13);



}

void createProperties(){
  TShape aShape;

  if ((FandGandH+FandGandnotH+FandnotGandH+FandnotGandnotH).length()>0){
     aShape=new TProperty();
     aShape.setBoundsRect(new Rectangle(line1,line2,(line5-line1),(line6-line2)));
     aShape.setColor(Color.blue);
     aShape.setName(predicate1.charAt(0));
     addShape(aShape);//addShapeToList(aShape, fShapes);
  }

  if ((FandGandH+FandGandnotH+notFandGandH+notFandGandnotH).length()>0){
     aShape=new TProperty();
     aShape.setBoundsRect(new Rectangle(line8,line4,line10-line8,line9-line4));
     aShape.setColor(Color.red);
     aShape.setName(predicate2.charAt(0));
     addShape(aShape);//addShapeToList(aShape, fShapes);
  }

  if ((FandGandH+FandnotGandH+notFandGandH+notFandnotGandH).length()>0){
       aShape=new TProperty();
       aShape.setBoundsRect(new Rectangle(line3,line7,line12-line3,line11-line7));
       aShape.setColor(Color.yellow);
       aShape.setName(predicate3.charAt(0));
       addShape(aShape);//addShapeToList(aShape, fShapes);
    }


}


  void createRelationsHelper(char name, String extension){
    TRelation aRelation;
    TIndividual anIndividual;

    int length =extension.length();

    if ((length>0)&&(length%2==0)){    //ordered pairs

      for (int i=0;i<length;i+=2){
        aRelation = new TRelation();

        anIndividual = individualFromName(extension.charAt(i));
        if (anIndividual != null)
          aRelation.setFrom(anIndividual.getHotSpot());
        anIndividual = individualFromName(extension.charAt(i+1));
        if (anIndividual != null)
          aRelation.setTo(anIndividual.getHotSpot());

        aRelation.setName(name);
        addShape(aRelation);
      }
    }
  }

  void createRelations(){

    if (rExtension.length()>0)
      createRelationsHelper(relation1.charAt(0),rExtension);
    if (sExtension.length()>0)
      createRelationsHelper(relation2.charAt(0),sExtension);
    if (tExtension.length()>0)
      createRelationsHelper(relation3.charAt(0),tExtension);


    }



void determineLines(){
   int increment;

   line1 = 100;
   increment = (FandnotGandnotH.length() * spacing);
  if (increment == 0){
    increment = spacing / 4;} // {h space}
    line3 = line1 + increment;

  increment = (FandnotGandH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line8 = line3 + increment;

  increment = (FandGandH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line5 = line8 + increment;

  increment = (notFandGandH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line12 = line5 + increment;

  increment = (notFandGandnotH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line10 = line12 + increment;

  increment = (notFandnotGandnotH.length() * spacing);
  line14 = line12 + increment;

  line2 = 50; //10;
  increment = (FandnotGandnotH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line4 = line2 + increment;

  increment = (FandGandnotH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line7 = line4 + increment;

  increment = (FandGandH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line6 = line7 + increment;

  increment = (notFandGandH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}

  line9 = line6 + spacing;
  increment = (notFandnotGandH.length() * spacing);
  if (increment == 0){
   increment = spacing / 4;}
  line11 = line9 + increment;

  increment = (notFandnotGandnotH.length() * spacing);
  line13 = line11 + increment;


  }

  void placeIndividuals(String individuals,int boxleft,int boxtop,int boxright,int boxbottom){

    final int kCircleRadius=8;
    float horizmagnification, vertmagnification;
    double angleincrement,currentangle;
    int count,radius;
    Point centre, currentPoint;

    /*The individuals go inside a box. A circle is inscribed and they are place on it
 starting at the top. However, the box need not be square. If it is a rectangle, the
circle is distorted into an ellipse by being 'magnified' either vertically or horizontally*/


    horizmagnification = (float)(boxright - boxleft) / (boxbottom - boxtop);  //need to cast for float division
        if (horizmagnification < 1){
          vertmagnification = 1 / horizmagnification;
          horizmagnification = 1;
        }
        else
         vertmagnification = 1;

        if (horizmagnification == 1)
            radius = ((boxright - boxleft) - kCircleRadius) / 2;
           else
            radius = ((boxbottom - boxtop) - kCircleRadius) / 2;

        centre= new Point(((boxright + boxleft) / 2) - kCircleRadius,  // in the middle but offset left
                          ((boxbottom + boxtop) / 2) + kCircleRadius); // and down by size of indidual


    count=individuals.length();

    angleincrement=(6.25/count);     //radians
    currentangle=0;

    int x,y;
    TShape aShape;
    Point topLeft;
    char name;

    for (int i=0;i<count;i++){

      x=(int)(Math.round(centre.x + (horizmagnification * radius * Math.sin(currentangle))));
      y=(int)(Math.round(centre.y - (vertmagnification * radius * Math.cos(currentangle))));

      topLeft=new Point(x,(y-2*kCircleRadius));
      aShape=new TIndividual(topLeft);
      name=individuals.charAt(i);
      aShape.setName(name);
      addShape(aShape);


      currentangle = currentangle + angleincrement;


    }

          /*

             for i := 1 to count do
               begin
                currentpoint.h := Round(centre.h + (horizmagnification * radius * sin(currentangle)));
                currentpoint.v := Round(centre.v - (vertmagnification * radius * cos(currentangle)));

                setRect(tempRect, currentpoint.h, currentpoint.v - 2 * kCircleRadius, currentpoint.h + 2 * kCircleRadius, currentpoint.v);
                individualRecords[individuals[i]].theRect := tempRect;

                currentangle := currentangle + angleincrement;
               end;
             end;


    */

    /*
       var
         horizmagnification, vertmagnification, angleincrement, currentangle: real;
         i, count, radius: integer;
         centre, startpoint, currentpoint: point;
         tempRect: rect;

       begin
        horizmagnification := (boxright - boxleft) / (boxbottom - boxtop);
        if horizmagnification < 1 then
         begin
          vertmagnification := 1 / horizmagnification;
          horizmagnification := 1;
         end
        else
         vertmagnification := 1;

        if horizmagnification = 1 then
         radius := ((boxright - boxleft) - kCircleRadius) div 2
        else
         radius := ((boxbottom - boxtop) - kCircleRadius) div 2;

        centre.v := ((boxbottom + boxtop) div 2) + kCircleRadius;
        centre.h := ((boxright + boxleft) div 2) - kCircleRadius;


    }*/
  }


/*
procedure TJournalWindow.ConstructDrawing;

  const
   spacing = 50;

  var
   FandGandHinds, FandGandNotHinds, FandNotGandHinds, FandNotGandNotHinds, NotFandGandHinds, NotFandGandNotHinds, NotFandNotGandHinds, NotFandNotGandNotHinds, TotalInds, searchStr: str255;
   individualRecords: array['a'..'z'] of shapedata;
   increment, i, line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12, line13, line14: integer;
   newShape: TShape;
   tempStr1: string[1];
   tempRect: rect;
   prop1, prop2, prop3, rel1, rel2, rel3: CHAR;

  procedure DetermineExtentRects (individuals: str255; boxleft, boxtop, boxright, boxbottom: integer);

   var
    horizmagnification, vertmagnification, angleincrement, currentangle: real;
    i, count, radius: integer;
    centre, startpoint, currentpoint: point;
    tempRect: rect;

  begin
   horizmagnification := (boxright - boxleft) / (boxbottom - boxtop);
   if horizmagnification < 1 then
    begin
     vertmagnification := 1 / horizmagnification;
     horizmagnification := 1;
    end
   else
    vertmagnification := 1;

   if horizmagnification = 1 then
    radius := ((boxright - boxleft) - kCircleRadius) div 2
   else
    radius := ((boxbottom - boxtop) - kCircleRadius) div 2;

   centre.v := ((boxbottom + boxtop) div 2) + kCircleRadius;
   centre.h := ((boxright + boxleft) div 2) - kCircleRadius;

   count := length(individuals);

   angleincrement := 6.25 / count; {radians}
   currentangle := 0;

   for i := 1 to count do
    begin
     currentpoint.h := Round(centre.h + (horizmagnification * radius * sin(currentangle)));
     currentpoint.v := Round(centre.v - (vertmagnification * radius * cos(currentangle)));

     setRect(tempRect, currentpoint.h, currentpoint.v - 2 * kCircleRadius, currentpoint.h + 2 * kCircleRadius, currentpoint.v);
     individualRecords[individuals[i]].theRect := tempRect;

     currentangle := currentangle + angleincrement;
    end;
  end;

 begin
          {fWindow.Activate(FALSE); deactivate journal}

  if fDeriverDocument.fShapeList.fSize > 0 then
   begin
    tempRect := TShape(fDeriverDocument.fShapeList.First).fExtentRect;
    insetRect(tempRect, -5, -5);
    fDeriverDocument.fShapeList.DeleteAll;

    fDeriverDocument.fDrawShapeView.InvalidRect(tempRect);


                        {SELF.ForceRedraw;}
   end;

  fDeriverDocument.CreateInterpretationBoard;

                {fDeriverDocument.fDrawShapeView.Deselect;}

  fDeriverDocument.InitInterpretationArray;

  fDeriverDocument.fCurrentUniverse := fNewInterpretation[1];

  if length(fNewInterpretation[2]) > 0 then
   begin
    prop1 := fNewInterpretation[2][1];
    Delete(fNewInterpretation[2], 1, 1); {the first char is the name of the property}
    fDeriverDocument.fCurrentProperties[prop1] := fNewInterpretation[2];
   end;

  if length(fNewInterpretation[3]) > 0 then
   begin
    prop2 := fNewInterpretation[3][1];
    Delete(fNewInterpretation[3], 1, 1); {the first char is the name of the property}
    fDeriverDocument.fCurrentProperties[prop2] := fNewInterpretation[3];
   end;

  if length(fNewInterpretation[4]) > 0 then
   begin
    prop3 := fNewInterpretation[4][1];
    Delete(fNewInterpretation[4], 1, 1); {the first char is the name of the property}
    fDeriverDocument.fCurrentProperties[prop3] := fNewInterpretation[4];
   end;

  if length(fNewInterpretation[5]) > 0 then
   begin
    rel1 := fNewInterpretation[5][1];
    Delete(fNewInterpretation[5], 1, 1);
    fDeriverDocument.fCurrentRelations[rel1] := fNewInterpretation[5];
   end;

  if length(fNewInterpretation[6]) > 0 then
   begin
    rel2 := fNewInterpretation[6][1];
    Delete(fNewInterpretation[6], 1, 1);
    fDeriverDocument.fCurrentRelations[rel2] := fNewInterpretation[6];
   end;

  if length(fNewInterpretation[7]) > 0 then
   begin
    rel3 := fNewInterpretation[7][1];
    Delete(fNewInterpretation[7], 1, 1);
    fDeriverDocument.fCurrentRelations[rel3] := fNewInterpretation[7];
   end;

  TotalInds := fNewInterpretation[1];
  FandGandHinds := TotalInds;
  FandGandNotHinds := TotalInds;
  FandNotGandHinds := TotalInds;
  FandNotGandNotHinds := TotalInds;
  NotFandGandHinds := TotalInds;
  NotFandGandNotHinds := TotalInds;
  NotFandNotGandHinds := TotalInds;
  NotFandNotGandNotHinds := TotalInds;

  tempStr1 := 'a'; {to fix length attribute}

  for i := 1 to length(TotalInds) do
   begin
    tempStr1[1] := TotalInds[i];

    searchStr := fNewInterpretation[2]; {F's}
    if pos(tempStr1, searchStr) <> 0 then
     begin
      Delete(NotFandGandHinds, pos(tempStr1, NotFandGandHinds), 1);
      Delete(NotFandNotGandHinds, pos(tempStr1, NotFandNotGandHinds), 1);
      Delete(NotFandNotGandNotHinds, pos(tempStr1, NotFandNotGandNotHinds), 1);
      Delete(NotFandGandNotHinds, pos(tempStr1, NotFandGandNotHinds), 1);
     end
    else
     begin
      Delete(FandGandHinds, pos(tempStr1, FandGandHinds), 1);
      Delete(FandNotGandHinds, pos(tempStr1, FandNotGandHinds), 1);
      Delete(FandNotGandNotHinds, pos(tempStr1, FandNotGandNotHinds), 1);
      Delete(FandGandNotHinds, pos(tempStr1, FandGandNotHinds), 1);
     end;

    searchStr := fNewInterpretation[3]; {G's}
    if pos(tempStr1, searchStr) <> 0 then
     begin
      Delete(NotFandNotGandHinds, pos(tempStr1, NotFandNotGandHinds), 1);
      Delete(NotFandNotGandNotHinds, pos(tempStr1, NotFandNotGandNotHinds), 1);
      Delete(FandNotGandHinds, pos(tempStr1, FandNotGandHinds), 1);
      Delete(FandNotGandNotHinds, pos(tempStr1, FandNotGandNotHinds), 1);
     end
    else
     begin
      Delete(NotFandGandHinds, pos(tempStr1, NotFandGandHinds), 1);
      Delete(NotFandGandNotHinds, pos(tempStr1, NotFandGandNotHinds), 1);
      Delete(FandGandHinds, pos(tempStr1, FandGandHinds), 1);
      Delete(FandGandNotHinds, pos(tempStr1, FandGandNotHinds), 1);
     end;

    searchStr := fNewInterpretation[4]; {H's}
    if pos(tempStr1, searchStr) <> 0 then
     begin
      Delete(NotFandGandNotHinds, pos(tempStr1, NotFandGandNotHinds), 1);
      Delete(FandNotGandNotHinds, pos(tempStr1, FandNotGandNotHinds), 1);
      Delete(NotFandNotGandNotHinds, pos(tempStr1, NotFandNotGandNotHinds), 1);
      Delete(FandGandNotHinds, pos(tempStr1, FandGandNotHinds), 1);
     end
    else
     begin
      Delete(NotFandGandHinds, pos(tempStr1, NotFandGandHinds), 1);
      Delete(FandNotGandHinds, pos(tempStr1, FandNotGandHinds), 1);
      Delete(NotFandNotGandHinds, pos(tempStr1, NotFandNotGandHinds), 1);
      Delete(FandGandHinds, pos(tempStr1, FandGandHinds), 1);
     end;
   end;

  line1 := 100;
  increment := (length(FandNotGandNotHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line3 := line1 + increment;
  increment := (length(FandNotGandHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line8 := line3 + increment;
  increment := (length(FandGandHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line5 := line8 + increment;
  increment := (length(NotFandGandHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line12 := line5 + increment;
  increment := (length(NotFandGandNotHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line10 := line12 + increment;
  increment := (length(NotFandNotGandNotHinds) * spacing);
  line14 := line12 + increment;

  line2 := 10;
  increment := (length(FandNotGandNotHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line4 := line2 + increment;
  increment := (length(FandGandNotHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line7 := line4 + increment;
  increment := (length(FandGandHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line6 := line7 + increment;
  increment := (length(NotFandGandHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}

  line9 := line6 + spacing;
  increment := (length(NotFandNotGandHinds) * spacing);
  if increment = 0 then
   increment := spacing div 4; {h space}
  line11 := line9 + increment;

  increment := (length(NotFandNotGandNotHinds) * spacing);
  line13 := line11 + increment;

          {Debugging}
          {$IFC myDebugging}
  if FALSE then
   begin
    writeln('lines', line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12);

   end;

          {$ENDC}

  if (length(FandGandHinds) + length(FandGandNotHinds) + length(NotFandGandHinds) + length(NotFandGandNotHinds)) <> 0 then
   begin
    newShape := TShape(gShapesArray[IDPropertyF].Clone);
    FailNIL(newShape);
    newShape.fShade := cVStripes;
    newShape.fExtentrect.left := line8;
    newShape.fExtentrect.top := line4;
    newShape.fExtentrect.right := line10;
    newShape.fExtentrect.bottom := line9;

    newShape.fName := prop2;

    fDeriverDocument.AddShape(newShape);
    fDeriverDocument.fDrawShapeView.InvalShape(newShape);
   end;

  if (length(FandGandHinds) + length(FandNotGandHinds) + length(NotFandGandHinds) + length(NotFandNotGandHinds)) <> 0 then
   begin
    newShape := TShape(gShapesArray[IDPropertyF].Clone);
    FailNIL(newShape);
    newShape.fShade := cHStripes;
    newShape.fExtentrect.left := line3;
    newShape.fExtentrect.top := line7;
    newShape.fExtentrect.right := line12;
    newShape.fExtentrect.bottom := line11;

    newShape.fName := prop3;

    fDeriverDocument.AddShape(newShape);
    fDeriverDocument.fDrawShapeView.InvalShape(newShape);
   end;

  if (length(FandGandHinds) + length(FandGandNotHinds) + length(FandNotGandHinds) + length(FandNotGandNotHinds)) <> 0 then
   begin
    newShape := TShape(gShapesArray[IDPropertyF].Clone); {F underneath}
    FailNIL(newShape);
    newShape.fExtentrect.left := line1;
    newShape.fExtentrect.top := line2;
    newShape.fExtentrect.right := line5;
    newShape.fExtentrect.bottom := line6;

    newShape.fName := prop1;

    fDeriverDocument.AddShape(newShape);
    fDeriverDocument.fDrawShapeView.InvalShape(newShape);
   end;

  if length(FandNotGandNotHinds) <> 0 then
   DetermineExtentRects(FandNotGandNotHinds, line1, line2, line3, line6);
  if length(NotFandGandNotHinds) <> 0 then
   DetermineExtentRects(NotFandGandNotHinds, line12, line7, line10, line9);
  if length(NotFandNotGandHinds) <> 0 then
   DetermineExtentRects(NotFandNotGandHinds, line3, line9, line12, line11);

  if length(FandGandNotHinds) <> 0 then
   DetermineExtentRects(FandGandNotHinds, line8, line4, line5, line7);
  if length(FandNotGandHinds) <> 0 then
   DetermineExtentRects(FandNotGandHinds, line3, line7, line8, line6);
  if length(NotFandGandHinds) <> 0 then
   DetermineExtentRects(NotFandGandHinds, line5, line7, line12, line11);

  if length(FandGandHinds) <> 0 then
   DetermineExtentRects(FandGandHinds, line8, line7, line5, line6);
  if length(NotFandNotGandNotHinds) <> 0 then
   DetermineExtentRects(NotFandNotGandNotHinds, line4, line11, line14, line13);

  for i := 1 to length(TotalInds) do
   begin
    newShape := TShape(gShapesArray[IDCircle].Clone);
    FailNIL(newShape);
    newShape.fName := TotalInds[i];
    newShape.fExtentrect := individualRecords[TotalInds[i]].theRect;

               {Debugging}
               {$IFC myDebugging}
    if FALSE then
     begin
      writeln('new circle name ', newShape.fName, newShape.fExtentrect.left, newShape.fExtentrect.right, newShape.fExtentrect.top, newShape.fExtentrect.bottom);

     end;

               {$ENDC}

    fDeriverDocument.AddShape(newShape);
    fDeriverDocument.fDrawShapeView.InvalShape(newShape);
   end;

  searchStr := fNewInterpretation[5]; {R's}
  if length(searchStr) > 0 then
   begin
                    {check, needs good overhaul}
    i := 1;

    while i < length(searchStr) do
     begin
      newShape := TShape(gShapesArray[IDRelationR].Clone);
      FailNIL(newShape);
      tempRect := individualRecords[searchStr[i]].theRect;
      newShape.fFrom.h := (tempRect.left + tempRect.right) div 2;
      newShape.fFrom.v := (tempRect.top + tempRect.bottom) div 2;

      tempRect := individualRecords[searchStr[i + 1]].theRect;
      newShape.fTo.h := (tempRect.left + tempRect.right) div 2;
      newShape.fTo.v := (tempRect.top + tempRect.bottom) div 2;

      if searchStr[i] = searchStr[i + 1] then  (*relating to itself*)
       begin
       newShape.fFrom.h := newShape.fFrom.h - kCircleRadius + 1;
       newShape.fTo.h := newShape.fTo.h + 1;
       end;

      newShape.fName := rel1;

      TRelation(newShape).SetSelectionRect;

      fDeriverDocument.AddShape(newShape);
      fDeriverDocument.fDrawShapeView.InvalShape(newShape);
      i := i + 2;
     end;
   end;

  searchStr := fNewInterpretation[6]; {S's}
  if length(searchStr) <> 0 then
   begin
    i := 1;

    while i < length(searchStr) do
     begin
      newShape := TShape(gShapesArray[IDFunction].Clone);
      FailNIL(newShape);
      tempRect := individualRecords[searchStr[i]].theRect;
      newShape.fFrom.h := (tempRect.left + tempRect.right) div 2;
      newShape.fFrom.v := (tempRect.top + tempRect.bottom) div 2;

      tempRect := individualRecords[searchStr[i + 1]].theRect;
      newShape.fTo.h := (tempRect.left + tempRect.right) div 2;
      newShape.fTo.v := (tempRect.top + tempRect.bottom) div 2;

      if searchStr[i] = searchStr[i + 1] then  (*relating to itself*)
       begin
       newShape.fFrom.h := newShape.fFrom.h - kCircleRadius + 1;
       newShape.fTo.h := newShape.fTo.h + 1;
       end;

      newShape.fName := rel2;

      TRelation(newShape).SetSelectionRect;

      fDeriverDocument.AddShape(newShape);
      fDeriverDocument.fDrawShapeView.InvalShape(newShape);
      i := i + 2;
     end;
   end;

  searchStr := fNewInterpretation[7]; {T's}
  if length(searchStr) <> 0 then
   begin
    i := 1;

    while i < length(searchStr) do
     begin
      newShape := TShape(gShapesArray[IDIdentity].Clone);
      FailNIL(newShape);
      tempRect := individualRecords[searchStr[i]].theRect;
      newShape.fFrom.h := (tempRect.left + tempRect.right) div 2;
      newShape.fFrom.v := (tempRect.top + tempRect.bottom) div 2;

      tempRect := individualRecords[searchStr[i + 1]].theRect;
      newShape.fTo.h := (tempRect.left + tempRect.right) div 2;
      newShape.fTo.v := (tempRect.top + tempRect.bottom) div 2;

      newShape.fName := rel3;

      if searchStr[i] = searchStr[i + 1] then  (*relating to itself*)
       begin
       newShape.fFrom.h := newShape.fFrom.h - kCircleRadius + 1;
       newShape.fTo.h := newShape.fTo.h + 1;
       end;

      TRelation(newShape).SetSelectionRect;

      fDeriverDocument.AddShape(newShape);
      fDeriverDocument.fDrawShapeView.InvalShape(newShape);
      i := i + 2;
     end;
   end;

          {tempRect:=fFrame.fContentRect;}
          {InsetRect(tempRect,-20,-20);}

          {fFrame.InvalidRect(tempRect);}
          {fFrame.UpdateEvent;}

  if fDeriverDocument.InterpretationChanged then
   fDeriverDocument.fDrawShapeView.UpdateInterpretationBoards; {check,new}

  fDeriverDocument.fDrawShapeView.DrawContents; {new}

 end;



*/

}  //end of TDrawingConstructor


void createInterpretationBoard(TSemantics theSemantics){
   TInterpretationBoard aBoard = new TInterpretationBoard(theSemantics);

   addShape(aBoard);

    }

    protected void paintComponent(Graphics g) {

      super.paintComponent(g);  // background

if (fSemantics.interpretationChanged()); // this may be the best place for this,
                                         //when we draw we need to know that the semantics is correct

        for(Iterator i=fShapes.iterator(); i.hasNext(); ) {
          ((TShape)i.next()).draw((Graphics2D)g);}

  if (fListener.fSelectionFeedback!=null)
    ((Graphics2D)g).draw(fListener.fSelectionFeedback);
  if (fListener.fProtoShapeFeedback!=null)
     (fListener.fProtoShapeFeedback).draw((Graphics2D)g);

      }



public boolean drawingIsClear(){  //it can have only interpretation boards
  boolean clear=true;
  if (fShapes.size() > 0) {
     Iterator iter = fShapes.iterator();

     while (iter.hasNext()&&clear) {
       clear= ((TShape) iter.next())instanceof TInterpretationBoard;

     }
   }
   return
       clear;

}


public void resetToEmpty(){

  List shapes= new ArrayList();

  setShapeList(shapes);

  createInterpretationBoard(fSemantics);

  killLastEdit();

  repaint();
}

      public void stateChanged(ChangeEvent e){

       // what this hears is changes in the shapes such as resizes, drags, selections etc.

            repaint(); // at the moment we're being pretty crude with this, redrawing all the shapes



        if (fDeriverDocument!=null)         // if this drawing has a journal, set it for saving
          fDeriverDocument.setDirty(true);



          }





          /**
           * Factory method to get the canvas singleton object.
           */
  /*        public static TShapePanel getGlobalShapePanel()
          {
              if(fGlobalShapePanel == null) {
                  fGlobalShapePanel = new TShapePanel("Shape Frame", 640, 480,
                                               Color.white);
              }
              fGlobalShapePanel.setVisible(true);
              return
                  fGlobalShapePanel;
          } */


/**************************************************/


TIndividual individualFromName(char name){  // this does not worry about identities
    if (fShapes.size() > 0) {
        Iterator iter = fShapes.iterator();

        while (iter.hasNext()) {
          TShape theShape = (TShape) iter.next();

          if ( (theShape.fTypeID == TShape.IDIndividual) &&
              (name == theShape.fName))
           return
               (TIndividual)theShape;
        }
        }
 return
     null;

}

char firstTermAvail(){
  // we  need to iterate through the characters and through the shapes

  boolean charGood =false;
  int i=0;
  char searchChar=gIndividualNames.charAt(i);

  if (fShapes.size() > 0) {
    while ( (!charGood) && (i < gIndividualNames.length())) {

      searchChar=gIndividualNames.charAt(i);

      charGood=true;  // this character is good unless we can prove otherwise

      Iterator iter = fShapes.iterator();

      while (charGood&&iter.hasNext()) {
        TShape theShape = (TShape) iter.next();

        if (((theShape.fTypeID == TShape.IDIndividual)||
            (theShape.fTypeID == TShape.IDIdentity))&&
            (searchChar == theShape.fName||    // should switch from char to string
             (fSemantics.getCurrentIdentities())[i]!=chBlank))  // it's a unary function (fCurrentIdentities[firstchar] <> chBlank)

            charGood=false;
     }
     i++;
    }
  }
  else {
    charGood=true;   //if there are no shapes the first character will do
  }

  if (charGood)
    return
        searchChar;
  else
    return
        chBlank;

}

/*

  March 05.  I am going to rewrite this without filtering and functional parameters

   class Look implements FunctionalParameter {
   boolean fAvailable=true;
   char fFirstChar=chBlank;

   public void  execute(Object parameter){
     TShape theShape = (TShape) parameter;

     if (fAvailable) {
       if (theShape.fTypeID == TShape.IDIndividual) {
         if ( (fFirstChar == theShape.fName) /* || more here on identities)
           fAvailable = false;
       }
     }
   }
}


 char firstTermAvail(){
   int i=0;
   Look looker= new Look();

   looker.fAvailable=false;
   looker.fFirstChar=chBlank;

   while ((!looker.fAvailable)&& (i<gIndividualNames.length())){

     looker.fAvailable=true;
     looker.fFirstChar=gIndividualNames.charAt(i);

     eachVirtualShapeDo(looker);

     if (!looker.fAvailable)
       i++;
   }

   if (looker.fAvailable)
      return
        looker.fFirstChar;
   else
      return
         chBlank;           //UNFINISHED
          }

          */

/*

           function TDeriverDocument.FirstTermAvail: CHAR;

          {returns first term not already used in universe or 0-ary functors }

            var
             found: BOOLEAN;
             firstchar: CHAR;

            procedure Look (shape: TShape);

            begin
             if (shape.fID = IDCircle) then
              begin
               if (firstchar = shape.fName) or (fCurrentIdentities[firstchar] <> chBlank) then
                found := FALSE;
              end;
            end;

           begin

            found := FALSE;

            firstchar := 'a';

            while (not found) and (firstchar <= 'l') do  (*check used to be m*)
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

*/


/********************** Shape List Operations ************************/

void addShapeToList(TShape aShape, List aShapeList){

/*unfortunately we need to have a front to back screen order, properties at the back
 (drawn first), then individuals, finally relations between individuals*/

   if ((aShape instanceof TRelation)||
       (aShape instanceof TFunction)||
       (aShape instanceof TIdentity))
      aShapeList.add(aShape);             // add at the end
   else if (aShape instanceof TProperty)
      aShapeList.add(0,aShape);           // add at the beginning
   else {
     int insertIndex=0;
     if (aShapeList.size() > 0) {
       Iterator iter = aShapeList.iterator();

       while (iter.hasNext()) {
         if (! ((TShape) iter.next()instanceof TProperty))
           break; //put it after rectangles
         else
           insertIndex++;
       }
     }
    aShapeList.add(insertIndex,aShape);
   }
 }

class AddSelectedShape implements FunctionalParameter {
   List fList;

   AddSelectedShape(List aList){
     fList=aList;
   }


    public void  execute(Object parameter){
      TShape theShape =(TShape)parameter;

   if (theShape.getSelected())
       addShapeToList(theShape.copy(),fList);
    }

    public boolean testIt(Object parameter){
      return
          false;
    }
  }


ArrayList copySelectedShapes(){
  ArrayList newList = new ArrayList();
  AddSelectedShape copyIfWanted = new AddSelectedShape(newList);

  eachShapeDo(copyIfWanted);

  return
      newList;
}

ArrayList deepCopyShapeList(List aShapeList){  //note here, this does NOT start us listening to the copied shapes
  ArrayList newList = new ArrayList();

  if (aShapeList.size() > 0) {
     Iterator iter = aShapeList.iterator();

     while (iter.hasNext())
       newList.add(((TShape)iter.next()).copy());
   }
  return
      newList;
}

void listenToAll(List aShapeList){

  if (aShapeList.size() > 0) {
     Iterator iter = aShapeList.iterator();

     while (iter.hasNext())
       ((TShape)iter.next()).addChangeListener(this);
   }

}

 void stopListening(List aShapeList){

   if (aShapeList.size() > 0) {
      Iterator iter = aShapeList.iterator();

      while (iter.hasNext())
        ((TShape)iter.next()).removeChangeListener(this);
    }

 }



void setSemanticsForInterpretationBoards(){  //don't think we need this, update it when we draw it
          if (fShapes.size() > 0) {
            Iterator iter = fShapes.iterator();

            while (iter.hasNext()){
              TShape theShape=(TShape) iter.next();
              if (theShape.fTypeID==TShape.IDInterpretationBoard){
                ((TInterpretationBoard)theShape).setSemantics(fSemantics);
              }
            }
          }


        }



void updateInterpretationBoards(){  //don't think we need this, update it when we draw it
   if (fShapes.size() > 0) {
     Iterator iter = fShapes.iterator();

     while (iter.hasNext()){
       TShape theShape=(TShape) iter.next();
       if (theShape.fTypeID==TShape.IDInterpretationBoard){
         theShape.setSelected(false);
      //   ((TInterpretationBoard)theShape).updateInterpretationBoard();
       }
     }
   }


 }


/*
class DeleteSelectedShape implements FunctionalParameter {
   List fList;

   DeleteSelectedShape(List aList){
     fList=aList;
   }


    public void  execute(Object parameter){
      TShape theShape =(TShape)parameter;

   if (theShape.isSelected())
       fList.remove(theShape);
    }
  } */

boolean deleteSelectedShapes(List aList){ // this has to be coded differently because we
                                       // might change the list we are iterating through
   boolean change=false;

   if (aList.size() > 0) {
     Iterator iter = aList.iterator();

     while (iter.hasNext()){
       if (((TShape)(iter.next())).getSelected()){
         iter.remove();
         change=true;
       }
     }
   }
   return
       change;
  }


void eachShapeDo (FunctionalParameter doThis){

                if (fShapes.size() > 0) {
                  Iterator iter = fShapes.iterator();

                  while (iter.hasNext())
                    doThis.execute(iter.next());

                }


              }


/*
           procedure TDeriverDocument.EachShapeDo (procedure DoThis (shape: TShape));

            begin
             fShapeList.Each(DoThis);
            end;


           */

void eachPotentialShapeDo (FunctionalParameter doThis){

      if (fShapes.size() > 0) {
        Iterator iter = fShapes.iterator();

        while (iter.hasNext())
          doThis.execute(iter.next());

          // more here on replacement shapes
      }


    }

/*

       procedure TDeriverDocument.EachPotentialShapeDo (procedure DoThis (shape: TShape));

     begin
      EachShapeDo(DoThis);
      if fReplaceCommand <> nil then
       fReplaceCommand.EachNewShapeDo(DoThis);
     end;


     */


    void eachVirtualShapeDo (FunctionalParameter doThis){

          if (fShapes.size() > 0) {
            Iterator iter = fShapes.iterator();

            while (iter.hasNext())
              doThis.execute(iter.next());

              // more here on
          }


        }


 /*

   procedure TDeriverDocument.EachVirtualShapeDo (procedure DoThis (shape: TShape));

  procedure MaybeDoThis (shape: TShape);

  begin
   if (not fFiltering) | (not shape.fWasSelected) then
    DoThis(shape);
  end;

 begin

  EachShapeDo(MaybeDoThis);
  if fReplaceCommand <> nil then
   fReplaceCommand.EachNewShapeDo(DoThis);
 end;


  */


    /*

      public void doToEach(FunctionalParameter aFunction, ArrayList aList){
   if (aList.size() > 0) {
     Iterator iter = aList.iterator();

     while (iter.hasNext())
       aFunction.execute(iter.next());
   }

 }
*/

  /*
   class FindAssumption implements FunctionalParameter {
       boolean fFound=false;
       TProofline fLastAssumptionLine;
       int fLevel=0;

       public FindAssumption (int level){
         fLevel=level;

       }

     public void  execute(Object parameter){
       TProofline workingLine =(TProofline)parameter;

       if ((workingLine.fJustification == "Ass")&&(workingLine.fSubprooflevel ==fLevel)){
         fFound=true;
         fLastAssumptionLine=workingLine;
       }
     }
   }


   */

  class deselectShape implements FunctionalParameter {


    public void execute(Object parameter) {
      TShape theShape = (TShape) parameter;

      theShape.setSelected(false);
    }

  public boolean testIt(Object parameter){
    return
        false;
  }
   }




void deselect(){



eachPotentialShapeDo(new deselectShape());

    }

/*
      procedure TShapeView.Deselect;

      procedure DeselShape (shape: TShape);

      begin
       shape.fIsSelected := FALSE;
      end;

     begin
      DoHighlightSelection(hlOn, hlOff);
      fDeriverDocument.EachPotentialShapeDo(DeselShape);
     end;


     */




    public void addShape(TShape aShape){
     // fShapes.remove(aShape);   // just in case it was already there

      /*unfortunately we need to have a front to back screen order, properties at the back
       (drawn first), then individuals, finally relations between individuals*/

   /*   if (aShape instanceof TLine)
         fShapes.add(aShape);             // add at the end
      else if (aShape instanceof TProperty)
         fShapes.add(0,aShape);           // add at the beginning
      else {
        int insertIndex=0;
        if (fShapes.size() > 0) {
          Iterator iter = fShapes.iterator();

          while (iter.hasNext()) {
            if (! ((TShape) iter.next()instanceof TProperty))
              break; //put it after rectangles
            else
              insertIndex++;
          }
        }
       fShapes.add(insertIndex,aShape);
      }*/


 //  aShape.setSemantics(fSemantics);

   aShape.addChangeListener(this);     // and we'll listen for any of its changes, such as being selected

   addShapeToList(aShape,fShapes);

   if (aShape instanceof TInterpretationBoard)
         ((TInterpretationBoard)aShape).setSemantics(fSemantics);


      fPalette.check();       // some of the names may need updating

      repaint();
    }



    /**
     * Set the foreground colour of the Canvas.
     * @param  newColour   the new colour for the foreground of the Canvas
     */
    public void setForegroundColor(String colorString)
    {
/*        if(colorString.equals("red"))
            graphic.setColor(Color.red);
        else if(colorString.equals("black"))
            graphic.setColor(Color.black);
        else if(colorString.equals("blue"))
            graphic.setColor(Color.blue);
        else if(colorString.equals("yellow"))
            graphic.setColor(Color.yellow);
        else if(colorString.equals("green"))
            graphic.setColor(Color.green);
        else if(colorString.equals("magenta"))
            graphic.setColor(Color.magenta);
        else if(colorString.equals("white"))
            graphic.setColor(Color.white);
        else if(colorString.equals("cyan"))
            graphic.setColor(Color.cyan);
        else
            graphic.setColor(Color.black); */
    }

    /**
     * Wait for a specified number of milliseconds before finishing.
     * This provides an easy way to specify a small delay which can be
     * used when producing animations.
     * @param  milliseconds  the number
     */
    public void wait(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (Exception e)
        {
            // ignoring exception at the moment
        }
    }




  /********************Mouse Handling **********************/

  protected class InputListener extends MouseAdapter implements MouseMotionListener
          {


  private TShape fShapeUnderMouse=null;
  private TShape fUniqueSelection=null;
  private int fNumSelected=0;
  private Point fAnchorForResize= new Point(0,0);
  private int fDragStatus;
  private Point fAnchorPt= new Point(0,0); //usually where the click or press is.

  static final int cNoStatus = 0, cAreaSelect = 1, cDragCreate=2, cDragResize=3,cDragMove=4;

  Rectangle fSelectionFeedback=null; // used for mouse tracking
  TShape fProtoShapeFeedback=null;   // used for mouse tracking on creating shape

  AbstractUndoableEdit fNewEdit=null;

  class moveSelectedShapes implements FunctionalParameter {
    int fDeltax, fDeltay;

    moveSelectedShapes(int deltax, int deltay) {
      fDeltax = deltax;
      fDeltay = deltay;

    }

    public void execute(Object parameter) {
      TShape theShape = (TShape) parameter;

      if (theShape.getSelected())
        theShape.moveBy(fDeltax, fDeltay); // toggle
    }
     public boolean testIt(Object parameter){
    return
        false;
  }
  }




  public void mouseDragged(MouseEvent e){
    Point currPt = e.getPoint();

    switch (fDragStatus) {

      case cAreaSelect:
           fSelectionFeedback= new Rectangle(fAnchorPt);
           fSelectionFeedback.add(currPt);
           repaint();
           break;

      case cDragCreate:
           if (fProtoShapeFeedback!=null){
             fProtoShapeFeedback.resize(fAnchorPt,currPt);  // resize only does something if there is change
           }
           break;

      case cDragResize:
           if (fUniqueSelection!=null){
             int deltax=currPt.x - fAnchorForResize.x;
             int deltay=currPt.y - fAnchorForResize.y;
             if ((deltax!=0)||(deltay!=0)){
               fUniqueSelection.resize(fAnchorForResize, currPt);
               ( (TUndoableResize) fNewEdit).stateChanged(new ChangeEvent(this));
             }
           }
           break;

      case cDragMove:
           int deltax=currPt.x - fAnchorPt.x;
           int deltay=currPt.y - fAnchorPt.y;
           if ((deltax!=0)||(deltay!=0)){
              eachShapeDo(new moveSelectedShapes(deltax,deltay));
              fAnchorPt=currPt;

              ((TUndoableMove)fNewEdit).stateChanged(new ChangeEvent(this));




              }
           break;

      default:;


      /*      case DRAG_MOVE:
                                        selectedShape.translate(curPt.x - dragAnchor.x, curPt.y - dragAnchor.y);
                                        dragAnchor = curPt; // update for next dragged event
                                        break;
                                case DRAG_CREATE: case DRAG_RESIZE:
                                        selectedShape.resize(dragAnchor, curPt);
                                        break;  */
                        }


   }

   public void mouseMoved(MouseEvent e){

    }

    /*mousePressedWithSelectionTool has its complications. Basically it needs to open the way
     to select, drag, or resize. Selection a) should take place on a click but in front to back order,
     b) and normally selecting one thing should deselect all others, c) but there can be multiple
     selection (for editing, say)and this can take place either by holding the shift key down or d) by
     drawing a rectangle and selecting all within it. Then drag takes place with the mouse on the shape
     but not its selection handles and it should drag all selected shape. Resize takes place with the
     mouse on a handle on a unique shape and resizes on it. Shapes could themselves resize and drag, but
     given the front to back, and multiple selections we are probably best to control it from here.


     */

class checkShape implements FunctionalParameter {
      Point fMousePosition;

      checkShape(MouseEvent e) {
        fMousePosition = e.getPoint();
        fUniqueSelection = null;
        fNumSelected = 0;
      }

      public void execute(Object parameter) {
        TShape theShape = (TShape) parameter;

        if (theShape.getSelected()) {
          if (fNumSelected == 0)
            fUniqueSelection = theShape;
          else
            fUniqueSelection = null;
          fAnchorForResize = theShape.getAnchorForResize(fMousePosition);
          fNumSelected++;
        }

        if (theShape.contains(fMousePosition))
          fShapeUnderMouse = theShape; //this gives us the last one and thus
        // a front to back search visually

    //   theShape.fIsSelected=false; we used to do this and I don't know why
      }

      /*
                                  procedure CheckShape (aShape: TShape);
       {we want it to select individuals, then relations,then props then interps}

                          var
                           itsExtentRect: Rect;

                         begin
                          itsExtentRect := aShape.fExtentRect;
                          if shapeUnderMouse = nil then
                           begin
                            if PtInRect(theMouse, itsExtentRect) then
                             shapeUnderMouse := aShape;
                           end
                          else if PtInRect(theMouse, itsExtentRect) then
                           begin
                            if aShape.fID = IDCircle then {individuals}
                             shapeUnderMouse := aShape
       else if (aShape.fID >= IDRelationR) and (aShape.fID <= IDIdentity) then
                             shapeUnderMouse := aShape
       else if shapeUnderMouse.fID = IDInterpretationBoard then
                             shapeUnderMouse := aShape;

                           end;
                         end;


       */


      public boolean testIt(Object parameter) {
        return
            false;

      }
    }



  private void mousePressedWithSelectTool(MouseEvent e){

     fShapeUnderMouse=null;
     eachVirtualShapeDo(new checkShape(e));

     /* it is a resize iff there already is a unique selection, and the press hits a handle.
      We won't worry about the shift key*/

     if (fShapeUnderMouse==null){                          // resize or area select
       if ((fNumSelected==1)&& (fAnchorForResize!=null)){  //resize

         {fNewEdit = new TUndoableResize(fUniqueSelection);}
        // newEdit.stateChanged(new ChangeEvent(this));}  // let shape call this
        //  fUniqueSelection.addChangeListener(fNewEdit);}    // only arm undo if there is change

          fDragStatus=cDragResize;
          return;                                          // mouse drag takes over
       }
       else
         fDragStatus=cAreaSelect;

     }
     else{
       if (!(fShapeUnderMouse.getSelected() || e.isShiftDown()))
         deselect();

       if (e.isShiftDown()){ //shift key
          fShapeUnderMouse.setSelected(!fShapeUnderMouse.getSelected()); //toggle
       }
       else{
          if (!fShapeUnderMouse.getSelected())
         fShapeUnderMouse.setSelected(true);
      }

      if (fShapeUnderMouse.getSelected()){                    //its a Move
        {fNewEdit = new TUndoableMove();}
         //  fShapeUnderMouse.addChangeListener(newEdit);}    // only arm undo if there is change

        fDragStatus = cDragMove;
      }

    }


  /*
          begin {shape select/move/...}

                if not (shapeUnderMouse.fIsSelected or info.theShiftKey) then
                Deselect;

                if info.theShiftKey then
                   begin
                   shapeUnderMouse.fIsSelected := not shapeUnderMouse.fIsSelected;
                   if shapeUnderMouse.fIsSelected then
                      shapeUnderMouse.Highlight(hlOff, hlOn)
                   else
                      shapeUnderMouse.Highlight(hlOn, hlOff);
                   end
                else if not shapeUnderMouse.fIsSelected then
                   begin
                   shapeUnderMouse.fIsSelected := TRUE;
                   DoHighlightSelection(hlOff, hlOn);
                   end;

                if shapeUnderMouse.fIsSelected then
                begin
                New(shapeDragger);
                FailNIL(shapeDragger);
                shapeDragger.IShapeDragger(SELF);
                DoMouseCommand := shapeDragger;
                end;
                                  {ELSE, fall-through, we return gNoChanges}
                end; {shape select/move/...}

               */
            }

  public void mouseClicked(MouseEvent e){
   //requestFocusInWindow();               // need this for the key events to palette  Aug06

   TShapePanel.this.requestFocus();     //Aug 06

            }


class checkAreaSelect implements FunctionalParameter {


  public void execute(Object parameter) {
    TShape theShape = (TShape) parameter;
    Rectangle boundsRect = theShape.getBoundsRect();

    if (fSelectionFeedback.contains(boundsRect))
      theShape.setSelected(!theShape.getSelected()); // toggle
  }

  public boolean testIt(Object parameter) {
    return
        false;

  }
}


void doAreaSelect(MouseEvent e){

   if (!e.isShiftDown()){ //shift key
     deselect();
   }



 /*  if shapeUnderMouse = nil then {area select}
             begin
             if not info.theShiftKey then
             Deselect;
             New(shapeSelector);
             FailNIL(shapeSelector);
             shapeSelector.IShapeSelector(cMouseCommand, SELF);
             DoMouseCommand := shapeSelector;
             end {area select} */

/*

  procedure TShapeSelector.DoIt;
    OVERRIDE;

    var
     shapeView: TShapeView;

    procedure TestShape (shape: TShape);

     var
      itsExtentRect: rect;
      itsBounds: rect;

    begin
     itsExtentRect := shape.fExtentRect;
     itsBounds := fBounds;
     if RectsNest(itsBounds, itsExtentRect) then
      begin
       if shape.fIsSelected then
        shape.Highlight(hlOn, hlOff)
       else
        shape.Highlight(hlOff, hlOn);
       shape.fIsSelected := not shape.fIsSelected;
      end;
    end;

   begin
    shapeView := fDrawShapeView;
    shapeView.fDeriverDocument.EachVirtualShapeDo(TestShape);
   end;


  */


   if(fSelectionFeedback!=null&&!fSelectionFeedback.isEmpty())
      eachVirtualShapeDo(new checkAreaSelect());

   fSelectionFeedback=null;

   repaint();     // to erase the selection rectangle
            }


  public void mouseReleased(MouseEvent e){
    switch (fDragStatus) {

      case cAreaSelect:
        doAreaSelect(e);

        killLastEdit(); // we treat selection as an (undoable) change of state
         break;
      case cDragCreate:
         Point currPt=e.getPoint();
         int deltax=currPt.x - fAnchorPt.x;
         int deltay=currPt.y - fAnchorPt.y;

         fProtoShapeFeedback.removeChangeListener(TShapePanel.this);

         if (((deltax!=0)||(deltay!=0))&&(fProtoShapeFeedback.isSemanticallySound(fSemantics,!kWithoutSelectees))){
           fNewEdit = new TUndoableAddShape();
           ((TUndoableAddShape)fNewEdit).stateChanged(new ChangeEvent(this));

           addShape(fProtoShapeFeedback);
         }
         fProtoShapeFeedback=null;
         repaint();
          break;


       case cDragResize:
       case cDragMove:


          if (!fSemantics.documentValid(!kWithoutSelectees)) {
             if((fNewEdit!=null)&&(fNewEdit.canUndo())){

               fNewEdit.undo(); // if the resize or move ruins the document, we'll  undo it and kill the edit
               killLastEdit();
             }
          };


        break;
       default: ;
    }

    fDragStatus = cNoStatus;
    fShapeUnderMouse=null;
    if ((fProtoShapeFeedback!=null)||(fSelectionFeedback!=null)){  //erase the feedback
      fProtoShapeFeedback = null;
      fSelectionFeedback = null;
      repaint();
    }

   //   if (fSemantics.interpretationChanged());     //this may need to be elsewhere** put it in paintComponent
  }

  public void mouseEntered(MouseEvent e){

                                          }
  public void mouseExited(MouseEvent e){

                                                              }


  public void mousePressed(MouseEvent e){

    TShapePanel.this.requestFocus();     //Aug 06

    fNewEdit=null;
    fAnchorPt=e.getPoint();

                                       // check palette

     if (fPalette.getSelection()==TPalette.cSelectTool)  // selection tool
        mousePressedWithSelectTool(e);
     else{                             // drawing
        deselect();

        /*There are three phases with the mouse: press, drag, release. On press we are going
         to create a prototype shape and listen to it-- no shape is added to the shape
         list at this point. Drag will resize this prototype. Then on release we will create and
         genuinely add a shape provided some constraints are satisfied. There will typically
         be two contraints a) there has been some drag (so no zero size shapes are added), b)
         the logical interpretation says that it is ok (more on that elsewhere.*/

        TShape newShape=fPalette.getPrototype().copy();

        newShape.setCoords(fAnchorPt);

  //      newShape.setIsVisible(true);

 //       newShape.setSemantics(fSemantics);

         /*

         TUndoableAddShape  newEdit = new TUndoableAddShape();
         newEdit.stateChanged(new ChangeEvent(this));

         addShape(newShape);  // we'll add it and let drag resize it, but if it is too small drag will remove it

*/

         fShapeUnderMouse=newShape;       // we don't do anything with this.

         fProtoShapeFeedback=newShape;

         fProtoShapeFeedback.addChangeListener(TShapePanel.this);

         fProtoShapeFeedback.setSelected(true);    // forces repaint

         fDragStatus=cDragCreate;

/*
        if (fPalette.getSelection()==TPalette.cEllipse){  // circle
          TEllipse circle = new TEllipse(fAnchorPt);

          circle.setSelected(true);

          circle.setIsVisible(true);

          addShape(circle);  // we'll add it and let drag resize it, but if it is too small drag will remove it
          fShapeUnderMouse=circle;

          fDragStatus=cDragCreate;
        }*/
  /*      if (fPalette.getSelection()==TPalette.cRectangle){  // circle
  TRectangle rectangle = new TRectangle(fAnchorPt);

  rectangle.setSelected(true);

rectangle.setIsVisible(true);

  addShape(rectangle);  // we'll add it and let drag resize it, but if it is too small drag will remove it
  fShapeUnderMouse=rectangle;

  fDragStatus=cDragCreate;
}*/

        /*

if (fPalette.getSelection()==TPalette.cLine){  // NEED TO WRITE THE CODE FOR THIS
  TRelation line = new TRelation(fAnchorPt);

  line.setSelected(true);

line.setIsVisible(true);

  addShape(line);  // we'll add it and let drag resize it, but if it is too small drag will remove it
  fShapeUnderMouse=line;

  fDragStatus=cDragCreate;
}*/


     }

//if (fSemantics.interpretationChanged())
   //   updateInterpretationBoards();     //this may need to be elsewhere, and don't needd to check if interparetion changes

}



  /*

         function TShapeView.DoMouseCommand (var theMouse: Point; var info: EventInfo; var hysteresis: Point): TCommand;

        var
         palette: TPalette;
         protoShape: TShape;
         squareSketcher: TSquareSketcher;
         circleSketcher: TCircleSketcher;
         relationSketcher: TRelationSketcher;
         identitySketcher: TIdentitySketcher;
         shapeUnderMouse: TShape;
         shapeSelector: TShapeSelector;
         shapeDragger: TShapeDragger;
         fi: FailInfo;

        procedure HdlInitCmdFailed (error: OSErr; message: LONGINT);

        begin
         protoShape.Free;
        end;

        procedure CheckShape (aShape: TShape);
           {we want it to select individuals, then relations,then props then interps}

         var
          itsExtentRect: Rect;

        begin
         itsExtentRect := aShape.fExtentRect;
         if shapeUnderMouse = nil then
          begin
           if PtInRect(theMouse, itsExtentRect) then
            shapeUnderMouse := aShape;
          end
         else if PtInRect(theMouse, itsExtentRect) then
          begin
           if aShape.fID = IDCircle then {individuals}
            shapeUnderMouse := aShape
           else if (aShape.fID >= IDRelationR) and (aShape.fID <= IDIdentity) then
            shapeUnderMouse := aShape
           else if shapeUnderMouse.fID = IDInterpretationBoard then
            shapeUnderMouse := aShape;

          end;
        end;

       begin
        DoMouseCommand := gNoChanges;
        if (SELF <> gClipView) then {!!! Was fCanSelect}
         begin
          palette := fPalette;
          fClickPt := theMouse;
          if palette.fCurrShape > 0 then {draw mode}
           begin
            FailSpaceIsLow; { Make sure we aren't low on memory }

            Deselect;

                          {Clone appropriate shape}

            protoShape := TShape(gShapesArray[palette.fCurrShape].Clone);
            FailNIL(protoShape);

            CatchFailures(fi, HdlInitCmdFailed);
         { Make sure cloning the shape left us with enough memory}
      {    to continue.}
            FailSpaceIsLow;

            case palette.fCurrShape of

             IDCircle:
             begin

             New(circleSketcher);
             FailNIL(circleSketcher);
             circleSketcher.ICircleSketcher(SELF, protoShape, info.theOptionKey);
             Success(fi);
             DoMouseCommand := circleSketcher;
             end;

             IDRelationR, IDFunction:
             begin

             New(relationSketcher);
             FailNIL(relationSketcher);
             relationSketcher.IRelationSketcher(SELF, protoShape, info.theOptionKey);
             Success(fi);
             DoMouseCommand := relationSketcher;
             end;

             IDIdentity:
             begin

             New(identitySketcher);
             FailNIL(identitySketcher);
             identitySketcher.IIdentitySketcher(SELF, protoShape, info.theOptionKey);
             Success(fi);
             DoMouseCommand := identitySketcher;
             end;

             otherwise
             begin

             New(squareSketcher);
             FailNIL(squareSketcher);
             squareSketcher.ISquareSketcher(SELF, protoShape, info.theOptionKey);
             Success(fi);
             DoMouseCommand := squareSketcher;
             end;
            end;

           end {draw mode}
          else
           begin {select mode}
            shapeUnderMouse := nil;
            fDeriverDocument.EachVirtualShapeDo(CheckShape);

            if shapeUnderMouse = nil then {area select}
             begin
             if not info.theShiftKey then
             Deselect;
             New(shapeSelector);
             FailNIL(shapeSelector);
             shapeSelector.IShapeSelector(cMouseCommand, SELF);
             DoMouseCommand := shapeSelector;
             end {area select}

            else
             begin {shape select/move/...}

             if not (shapeUnderMouse.fIsSelected or info.theShiftKey) then
             Deselect;

             if info.theShiftKey then
             begin
             shapeUnderMouse.fIsSelected := not shapeUnderMouse.fIsSelected;
             if shapeUnderMouse.fIsSelected then
             shapeUnderMouse.Highlight(hlOff, hlOn)
             else
             shapeUnderMouse.Highlight(hlOn, hlOff);
             end
             else if not shapeUnderMouse.fIsSelected then
             begin
             shapeUnderMouse.fIsSelected := TRUE;
             DoHighlightSelection(hlOff, hlOn);
             end;

             if shapeUnderMouse.fIsSelected then
             begin
             New(shapeDragger);
             FailNIL(shapeDragger);
             shapeDragger.IShapeDragger(SELF);
             DoMouseCommand := shapeDragger;
             end;
                               {ELSE, fall-through, we return gNoChanges}
             end; {shape select/move/...}
           end; {Select mode}

          if fDeriverDocument.InterpretationChanged then
           fDeriverDocument.fDrawShapeView.UpdateInterpretationBoards; {check MF}

         end; {can select}


       end;

       */


          }

  void cutMenuItem_actionPerformed(ActionEvent e) {
    performCut();
  }


  void performDelete(){

   TUndoableDelete  newEdit = new TUndoableDelete();  // should check whether valid before creating this possible garbage

   if (deleteSelectedShapes(fShapes)){
     newEdit.stateChanged(new ChangeEvent(this));
     repaint();
   }
 }


 void  performCut(){


    TUndoableCut  newEdit = new TUndoableCut();

    newEdit.stateChanged(new ChangeEvent(this));

  /*   newEdit.doEdit(); */

    (TShapeClipboard.fGlobalClipboard).setShapeList(copySelectedShapes());
    if (deleteSelectedShapes(fShapes))
      repaint();
  }


Rectangle unionOfSelectees(){
   Rectangle returnRect=null;

   if (fShapes.size() > 0) {
     Iterator iter = fShapes.iterator();
     TShape theShape;
     boolean initialized=false;

     while (iter.hasNext()){
       theShape= (TShape) iter.next();

       if (theShape.getSelected()){
         if (!initialized) {
           returnRect = theShape.getBoundsRect();
           initialized = true;
         }
         else
           returnRect.add(theShape.getBoundsRect());
       }
     }
   }

   return
       returnRect;
}

void performSelectAll(){

   if (fShapes.size() > 0) {
      Iterator iter = fShapes.iterator();

      while (iter.hasNext()) {
        TShape nextShape= (TShape) iter.next();
        if (! (nextShape instanceof TInterpretationBoard))
          nextShape.setSelected(true);

      }
    }

}

 void  performPaste(){

// paste does not change the clipboard

   List pasteList = (TShapeClipboard.fGlobalClipboard).getShapeList();




   if ( (TShapeClipboard.fGlobalClipboard).clipboardValidForPaste(fShapes)) {
     // we have to check this because the User can bypass the menu by using the keyboard

       TUndoablePaste newEdit = new TUndoablePaste();

       newEdit.stateChanged(new ChangeEvent(this));

       Point whereToPaste;
       Rectangle unionRect=unionOfSelectees();

       if (unionRect!=null)
         whereToPaste=unionRect.getLocation();
       else
         whereToPaste=fListener.fAnchorPt;   //last click

       if (deleteSelectedShapes(fShapes))
          repaint();                         // it's going to repaint anyway

      if (pasteList.size() > 0) {
         Iterator iter = pasteList.iterator();
         TShape newShape;

         (TShapeClipboard.fGlobalClipboard).prepareForPaste();  // sets the top left to 00

         while (iter.hasNext()){
           newShape= ((TShape) iter.next()).copy();

           Rectangle boundsRect=newShape.getBoundsRect();

           boundsRect.translate(whereToPaste.x,whereToPaste.y);

           newShape.setBoundsRect(boundsRect);

           addShape(newShape); //more to it THAN THIS eg resolve interpr board reference
         }
       }




       /*   newEdit.doEdit(); */

   /*    (TShapeClipboard.fGlobalClipboard).setShapeList(copySelectedShapes());
       if (deleteSelectedShapes(fShapes))
     repaint(); */
   }

 }


/*

   procedure TShapePasteCommand.DoIt;
   OVERRIDE;

   var
    whereToPaste: Point;
    noOfShapes: INTEGER;
    translation: VPoint;
    t: INTEGER;
    vhs: VHSelect;
    extent: rect;
    scrollerExtent: VRect;

   procedure PasteShape (clipShape: TShape);

    var
     aShape: TShape;

   begin
    aShape := TShape(clipShape.Clone);
    FailNil(aShape);

    if fDeriverDocument.TermAlreadyThere(aShape.fName) or TermInPasteList(aShape.fName) then
     aShape.ReviseName(fDeriverDocument.FirstTermAvail);

    if (aShape.fID = IDInterpretationBoard) then
     TInterpretationBoard(aShape).fDeriverDocument := fDeriverDocument;

   {if an Iboard is cut from one document and pasted into another, this*()}
 { ( *reference needs to be resolved to the new on* )  }

    with aShape do
     begin
      fIsSelected := TRUE;
      fWasSelected := TRUE;

 {$H-}
      OffsetRect(fExtentRect, whereToPaste.h, whereToPaste.v);
     end;
    fPasteList.InsertLast(aShape);
    fDrawShapeView.InvalShape(aShape);
    fDeriverDocument.fDrawPaletteView.Check; {MF}
   end;

  begin
           {$IFC qDebug}
   if not MEMBER(gClipView, TShapeView) then
    ProgramBreak('Attempt to paste a non-TShapeView clipboard');
           {$ENDC}

  {The next section figures out where the pasted shapes should be placed}
 {  in the view.  Lovely, isn't it?}
   if gPasteReplacesSelection then
    begin
   {If we're replacing shapes, then paste the new shapes starting at}
 {   the top-left corner of the replaced shapes.  Otherwise, start}
 {   at the last clicked point in the view}
     fDeriverDocument.SurveyShapes(TRUE, noOfShapes, extent);
     if noOfShapes > 0 then
      whereToPaste := extent.topLeft
     else
      whereToPaste := fDrawShapeView.fClickPt;
    end
   else
    begin
     fDrawShapeView.fSuperView.GetExtent(scrollerExtent);
     for vhs := v to h do
      with scrollerExtent.topLeft do
       begin
     {temp var "t" needed because Code Generator finds the}
 {     following expression too complex}
        t := (scrollerExtent.botRight.vh[vhs] + vh[vhs] - gClipView.fSize.vh[vhs]) div 2; {scrollerExtent.}
        whereToPaste.vh[vhs] := Max(vh[vhs], t); {translation.}
       end;
    end;
   SubPt(gClipMargin, whereToPaste);

   fDrawShapeView.SaveSelection(gPasteReplacesSelection);
   fDrawShapeView.Deselect;

   TShapeView(gClipView).fDeriverDocument.EachShapeDo(PasteShape);

   fDeriverDocument.fFiltering := gPasteReplacesSelection;
   fDeriverDocument.fReplaceCommand := SELF;
   fDrawShapeView.AdjustSize; {Make sure all the Pasted shapes can be seen}
   fDeriverDocument.fDrawPaletteView.Check;
  end;


  */


/********************* UNDO SUPPORT*******************************/



 void doSetUpMenus(){
   boolean somethingSelected=false;
   boolean cutOK=false;

   // undo redo etc is looked after by the undo manager

   if (fShapes.size() > 0) {
     Iterator iter = fShapes.iterator();

     while ( (iter.hasNext()) && (!somethingSelected)) {
       if ( ( (TShape) iter.next()).getSelected())
         somethingSelected = true;
     }
   }

   if (somethingSelected) {
     boolean withoutSelectees =true;

     cutOK=fSemantics.documentValid(withoutSelectees);   // needs to be valid after the selection has gone.


   }


  cutMenuItem.setEnabled(cutOK);
  copyMenuItem.setEnabled(somethingSelected);
  deleteMenuItem.setEnabled(cutOK);

  pasteMenuItem.setEnabled((TShapeClipboard.fGlobalClipboard).clipboardValidForPaste(fShapes));

//LATER ON NEED TO ENABLE COLOR PATTERN CHANGE IF SOMETHING IS SELECTED OR PALETTE CHOICE IS A SHAPE



}

/*

   procedure TShapeView.DoSetupMenus;

   var
    i: integer;
    anySelection: BOOLEAN;
    anyShapes: BOOLEAN;
    haveMemory: BOOLEAN;
    cutOK: BOOLEAN;
    pasteOK: BOOLEAN;
    aMenuHandle: MenuHandle;
    item: integer;
    itemName: Str255;

   procedure TestShapes (theShape: TShape);

   begin
    anySelection := anySelection or theShape.fIsSelected;
    anyShapes := anyShapes or (not fDeriverDocument.fFiltering) or (not theShape.fWasSelected);
   end;

  begin

   inherited DoSetupMenus;

   anySelection := FALSE;
   anyShapes := FALSE;
   cutOK := TRUE;

  { Find out if we are low on memory.  If we are then we'll disable all}
 {  memory-intensive commands.}
   haveMemory := not MemSpaceIsLow;

           { This checks every virtual shape--could be made faster. }
   fDeriverDocument.EachVirtualShapeDo(TestShapes);

   if anySelection then
    cutOK := fDeriverDocument.DocumentValid(TRUE); {checks if a cut will}
 {               leave a valid document}

   for i := cWhite to cGray do
    Enable(i, anySelection);

   if anySelection and gConfiguration.hasColorQD then
    begin
                {Enable each of the Color menu items, if the Color menu is present}
     aMenuHandle := GetMHandle(mColor);
     if aMenuHandle <> nil then
      for item := 1 to CountMItems(aMenuHandle) do
       begin
     {There can be more than 31 menu entries with scrolling menus,}
 {     but trying to enable an item with number > 31 is bad news.}
 {     If the menu itself is enabled (which it will be in MacApp}
 {     if any of the first 31 items is enabled), then the extras}
 {     will always be enabled.}
                          {Don't enable line separators.}
        GetItem(aMenuHandle, item, itemName);
        if (item <= 31) and (itemName <> '-') then
        EnableItem(aMenuHandle, item);
       end;
    end;

   Enable(cCut, anySelection and haveMemory and cutOK);
   Enable(cCopy, anySelection and haveMemory);

   if Member(gClipView, TShapeView) then
    pasteOK := ClipBoardValid { ClipBoardValid valid data}
   else
    pasteOK := TRUE;

   if haveMemory and pasteOK then
    CanPaste(kShapeClipType);

   Enable(cClear, anySelection and cutOK);

   Enable(cSelectAll, anyShapes);

   Enable(cOpenProof, true);
   Enable(cOpenDraw, true);

   if fDeriverDocument.fProofWindow.IsShown then
    SetCmdName(cOpenProof, 'Hide Proof')
   else
    SetCmdName(cOpenProof, 'Show Proof'); {put in resources, also must check with close box}

   if SELF.GetWindow.IsShown then
    SetCmdName(cOpenDraw, 'Hide Drawing')
   else
    SetCmdName(cOpenDraw, 'Show Drawing'); {put in resources, also must check with close box}


  end;


  */

 /*




  // doTI

  TUndoableProofEdit  newEdit = new TUndoableProofEdit();
     newEdit.fNewLines.add(newline);
     newEdit.doEdit();

  /********************* undo support, the undoable edits are an inner class below*****************/

  public void addUndoableEditListener(UndoableEditListener listener){
      //fUndoListener =listener;  June06

       fUndoListeners.add(listener);
    }

   public void tellListeners(UndoableEditEvent e){

     Iterator iter = fUndoListeners.iterator();

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



public class TShapeListChange extends AbstractUndoableEdit implements ChangeListener{

//change document

  List fOldShapes;

  public TShapeListChange() {

    fOldShapes=deepCopyShapeList(fShapes);

  }


public void stateChanged(ChangeEvent e){  //not all apparent edits end up actually changing anything
                                          // we only want undos if there are changes

  if (fLastEdit!=this){ // just a precaution against stateChange getting called many times

     if (fLastEdit != null)  // kill the previous one so there's only one undo
        fLastEdit.die(); // don't kill ourselves

     fLastEdit = this;
  //   fUndoListener.undoableEditHappened(new UndoableEditEvent(TShapePanel.this, this)); // tell the listener.

     tellListeners(new UndoableEditEvent(TShapePanel.this,this));  // tell the listeners.

   }

  }

  public String getPresentationName() {
    return

        "needs overriding";
  }



  public void redo() throws CannotRedoException {
    super.redo();

    stopListening(fShapes);   //stop listening to the current shapes

    List temp;
    temp=fShapes;
    setShapeList(fOldShapes); //start listening to the new current shapes
    fOldShapes=temp;

  //  listenToAll(fShapes);

     repaint();


  }

  public void undo() throws CannotUndoException {
    super.undo();

    stopListening(fShapes);   //stop listening to the current shapes

    List temp;
    temp=fShapes;
    setShapeList(fOldShapes); //start listening to the new current shapes
    fOldShapes=temp;

  //  listenToAll(fShapes);    //start listening to the new current shapes

    repaint();

  }
}


public class TUndoableAddShape  extends TShapeListChange{

  public String getPresentationName() {
    return

        "add shape";
  }
}




public class TUndoableCut extends TShapeListChange{

//change both clipboard and (via its superclass) document


  List fOldClipboardShapes = null;



  public TUndoableCut() {

    fOldClipboardShapes=deepCopyShapeList((TShapeClipboard.fGlobalClipboard).getShapeList());

  }


  public String getPresentationName() {
    return

        "cut";
  }


  public void redo() throws CannotRedoException {
    super.redo();

    List temp;
    temp=(TShapeClipboard.fGlobalClipboard).getShapeList();
    (TShapeClipboard.fGlobalClipboard).setShapeList(fOldClipboardShapes);
    fOldClipboardShapes=temp;
 }

  public void undo() throws CannotUndoException {
    super.undo();

    List temp;
    temp=(TShapeClipboard.fGlobalClipboard).getShapeList();
    (TShapeClipboard.fGlobalClipboard).setShapeList(fOldClipboardShapes);
    fOldClipboardShapes=temp;
  }
}

public class TUndoableCopy extends AbstractUndoableEdit implements ChangeListener{

//change only the clipboard


  List fOldClipboardShapes = null;



  public TUndoableCopy() {

    fOldClipboardShapes=deepCopyShapeList((TShapeClipboard.fGlobalClipboard).getShapeList());

  }

  public void stateChanged(ChangeEvent e){  //not all apparent edits end up actually changing anything
                                            // we only want undos if there are changes

    if (fLastEdit!=this){ // just a precaution against stateChange getting called many times

       if (fLastEdit != null)  // kill the previous one so there's only one undo
          fLastEdit.die(); // don't kill ourselves

       fLastEdit = this;
      // fUndoListener.undoableEditHappened(new UndoableEditEvent(TShapePanel.this, this)); // tell the listener.

      tellListeners(new UndoableEditEvent(TShapePanel.this,this));  // tell the listeners.

     }

    }


  public String getPresentationName() {
    return

        "copy";
  }


  public void redo() throws CannotRedoException {
    super.redo();

    List temp;
    temp=(TShapeClipboard.fGlobalClipboard).getShapeList();
    (TShapeClipboard.fGlobalClipboard).setShapeList(fOldClipboardShapes);
    fOldClipboardShapes=temp;
 }

  public void undo() throws CannotUndoException {
    super.undo();

    List temp;
    temp=(TShapeClipboard.fGlobalClipboard).getShapeList();
    (TShapeClipboard.fGlobalClipboard).setShapeList(fOldClipboardShapes);
    fOldClipboardShapes=temp;
  }
}


public class TUndoableResize extends AbstractUndoableEdit implements ChangeListener{

//change one shape only

  Rectangle fOldSize = null;
  TShape fShape;

  public TUndoableResize(TShape aShape) {

    super();

    fShape=aShape;
    fOldSize=fShape.getBoundsRect();

  }


public void stateChanged(ChangeEvent e){  //not all apparent edits end up actually changing anything
                                          // we only want undos if there are changes

   fShape.removeChangeListener(this); // ordinarily stateChange can get called many
                                      //times during resize, so we'll stop listening once we know
                                      // there is change


   if (fLastEdit!=this){
      if (fLastEdit != null)  // kill the previous one so there's only one undo
        fLastEdit.die(); // don't kill ourselves

      //fUndoListener.undoableEditHappened(new UndoableEditEvent(TShapePanel.this, this)); // tell the listener.

      tellListeners(new UndoableEditEvent(TShapePanel.this,this));  // tell the listeners.

      fLastEdit = this;
   }
  }

  public String getPresentationName() {
    return

        "resize";
  }



  public void redo() throws CannotRedoException {
    super.redo();

    Rectangle temp;
    temp=fShape.getBoundsRect();
    fShape.setBoundsRect(fOldSize);
    fOldSize=temp;

  }

  public void undo() throws CannotUndoException {
    super.undo();

    Rectangle temp;
    temp=fShape.getBoundsRect();
    fShape.setBoundsRect(fOldSize);
    fOldSize=temp;
  }
}



public class TUndoableMove  extends TShapeListChange{

  public String getPresentationName() {
    return

        "move";
  }
}

public class TUndoableDelete  extends TShapeListChange{

  public String getPresentationName() {
    return

        "delete";
  }
}



public class TUndoablePaste  extends TShapeListChange{

  public String getPresentationName() {
    return

        "paste";
  }
}



public class TUndoableColorChange  extends TShapeListChange{

  public String getPresentationName() {
    return

        "color change";
  }
}

public class TUndoablePatternChange  extends TShapeListChange{

  public String getPresentationName() {
    return

        "pattern change";
  }
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



void killLastEdit(){
    if (fLastEdit != null){ // kill the previous one so there's only one undo
      fLastEdit.die();
      fUndoAction.updateUndoState();  // make sure the menus are correct
      fRedoAction.updateRedoState();
    }
  }



 boolean changeColor(Color newColor){  //selections and any palette icon
   boolean change=false;

   if (fShapes.size() > 0) {
     Iterator iter = fShapes.iterator();

     while (iter.hasNext()) {
       TShape theShape = (TShape) iter.next();

       if (theShape.getSelected()) {
         theShape.setColor(newColor);
         change = true;
       }
     }
   }

   if (!change){
     TShape prototype=fPalette.getPrototype();

     if (prototype!=null)
       prototype.setColor(newColor);
       fPalette.repaint();           // no listeners for change in the palette
   }

  return
      change; // to drawing itself
 }

 boolean changePattern(int newPattern){  //selections and any palette icon
   boolean change=false;

   if (fShapes.size() > 0) {
     Iterator iter = fShapes.iterator();

     while (iter.hasNext()) {
       TShape theShape = (TShape) iter.next();

       if (theShape.getSelected()) {
         theShape.setPattern(newPattern);
         change = true;
       }
     }
   }

   if (!change){
     TShape prototype=fPalette.getPrototype();

     if (prototype!=null)
       prototype.setPattern(newPattern);
       fPalette.repaint();           // no listeners for change in the palette
   }

  return
      change; // to drawing itself
 }



  void redMenuItem_actionPerformed(ActionEvent e) {   //red

   TUndoableColorChange anUndoableChange = new TUndoableColorChange();

   if (changeColor(Color.RED))
     anUndoableChange.stateChanged(new ChangeEvent(this));

 //   ((TUndoableMove)fNewEdit).stateChanged(new ChangeEvent(this));

  }

  void blueMenuItem_actionPerformed(ActionEvent e) {//blue
    TUndoableColorChange anUndoableChange = new TUndoableColorChange();

if (changeColor(Color.BLUE))
  anUndoableChange.stateChanged(new ChangeEvent(this));

  }

  void yellowMenuItem_actionPerformed(ActionEvent e) {  //yellow
    TUndoableColorChange anUndoableChange = new TUndoableColorChange();

    if (changeColor(Color.YELLOW))
      anUndoableChange.stateChanged(new ChangeEvent(this));

  }
  void greenMenuItem_actionPerformed(ActionEvent e) {  //yellow
    TUndoableColorChange anUndoableChange = new TUndoableColorChange();

    if (changeColor(Color.GREEN))
      anUndoableChange.stateChanged(new ChangeEvent(this));

  }  void magentaMenuItem_actionPerformed(ActionEvent e) {  //yellow
    TUndoableColorChange anUndoableChange = new TUndoableColorChange();

    if (changeColor(Color.MAGENTA))
      anUndoableChange.stateChanged(new ChangeEvent(this));

  }  void cyanMenuItem_actionPerformed(ActionEvent e) {  //yellow
    TUndoableColorChange anUndoableChange = new TUndoableColorChange();

    if (changeColor(Color.CYAN))
      anUndoableChange.stateChanged(new ChangeEvent(this));

  }


  void jMenuItem8_actionPerformed(ActionEvent e) {  //plain pattern
    TUndoablePatternChange anUndoableChange = new TUndoablePatternChange();

if (changePattern(TShape.NO_PATTERN))
  anUndoableChange.stateChanged(new ChangeEvent(this));

  }

  void jMenuItem7_actionPerformed(ActionEvent e) {  //spotty pattern
    TUndoablePatternChange anUndoableChange = new TUndoablePatternChange();

if (changePattern(TShape.SPOTTY_PATTERN))
  anUndoableChange.stateChanged(new ChangeEvent(this));


  }

  void hatchMenuItem_actionPerformed(ActionEvent e) {  //spotty pattern
  TUndoablePatternChange anUndoableChange = new TUndoablePatternChange();

if (changePattern(TShape.HATCH_PATTERN))
anUndoableChange.stateChanged(new ChangeEvent(this));


}


  void copyMenuItem_actionPerformed(ActionEvent e) {
   performCopy();
  }

void performCopy(){
    TUndoableCopy  newEdit = new TUndoableCopy();

    newEdit.stateChanged(new ChangeEvent(this));

/*   newEdit.doEdit(); */

(TShapeClipboard.fGlobalClipboard).setShapeList(copySelectedShapes());

  }

  void jMenu1_actionPerformed(ActionEvent e) {
// aha click on the menu itself (place to update enable disable

    cutMenuItem.setEnabled(false);


  }

  void jMenu1_mousePressed(MouseEvent e) {
    doSetUpMenus();

  }

  void pasteMenuItem_actionPerformed(ActionEvent e) {
   performPaste();
  }

  void selectAllMenuItem_actionPerformed(ActionEvent e) {
    performSelectAll();

  }

  void deleteMenuItem_actionPerformed(ActionEvent e) {
    performDelete();
  }



/********************* END OF UNDO SUPPORT*******************************/



}

class TShapePanel_cutMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_cutMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cutMenuItem_actionPerformed(e);
  }
}

class TShapePanel_redMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_redMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.redMenuItem_actionPerformed(e);
  }
}

class TShapePanel_blueMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_blueMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.blueMenuItem_actionPerformed(e);
  }
}

class TShapePanel_yellowMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_yellowMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.yellowMenuItem_actionPerformed(e);
  }
}

class TShapePanel_greenMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_greenMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.greenMenuItem_actionPerformed(e);
  }
}

class TShapePanel_magentaMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_magentaMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.magentaMenuItem_actionPerformed(e);
  }
}

class TShapePanel_cyanMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_cyanMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cyanMenuItem_actionPerformed(e);
  }
}

class TShapePanel_jMenuItem8_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_jMenuItem8_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem8_actionPerformed(e);
  }
}

class TShapePanel_jMenuItem7_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_jMenuItem7_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem7_actionPerformed(e);
  }
}

class TShapePanel_hatchMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_hatchMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.hatchMenuItem_actionPerformed(e);
  }
}

class TShapePanel_copyMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_copyMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.copyMenuItem_actionPerformed(e);
  }
}

class TShapePanel_jMenu1_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_jMenu1_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenu1_actionPerformed(e);
  }
}

class TShapePanel_jMenu1_mouseAdapter extends java.awt.event.MouseAdapter {
  TShapePanel adaptee;

  TShapePanel_jMenu1_mouseAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.jMenu1_mousePressed(e);
  }
}

class TShapePanel_pasteMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TShapePanel adaptee;

  TShapePanel_pasteMenuItem_actionAdapter(TShapePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.pasteMenuItem_actionPerformed(e);
  }
}


