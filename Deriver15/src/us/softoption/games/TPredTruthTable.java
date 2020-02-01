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

package us.softoption.games;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import us.softoption.interpretation.TIndividual;
import us.softoption.interpretation.TInterpretationBoard;
import us.softoption.interpretation.TProperty;
import us.softoption.interpretation.TSemantics;
import us.softoption.interpretation.TShape;
import us.softoption.interpretation.TShapePanel;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;





/*Put this in a frame, setting the fContainer field, then use some code for the fram like

 setSize(500,200);
 setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-200)/2);  //frame does this
 setResizable(false);

*/

/*You need to use a scroll pane for the tables, for it is that which handles the header (which is
 kind of separate from the table)*/


public class TPredTruthTable extends JPanel{

  TParser fParser;//=new TParser();
  TFormula fRandom;
  int fCorrect=0;
  int fTotal=0;
  int fMaxAttempts=-1;

  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();

  JTable jt = new JTable();
  TTModel fTableModel=null;
  ListSelectionModel fCSM=null;

  JButton submitButton = new JButton();

  JLabel feedback =new JLabel("You have " +fCorrect+" right out of " +fTotal+".");
  BorderLayout borderLayout2 = new BorderLayout();

  JScrollPane jsp =new JScrollPane(jt);

  JPanel jPanel2 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();


 // boolean fNextThread=true;

  long fElapsed=0;
  long fMaxTime=-1;
  TimeIncrementer fTimeIncrementer = new TimeIncrementer();

  TShapePanel fShapePanel= new TShapePanel();  // probably won't use

  ShapeDisplay fShapeDisplay;

  TSemantics fSemantics;

  Container fContainer;
  Dimension fPreferredSize=new Dimension(500,200);

  public TPredTruthTable(Container itsContainer,TParser itsParser){

  //   super("Complete the Truth Table Line");

  fContainer=itsContainer;
  fParser=itsParser;

    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }






  private void jbInit() throws Exception {

	  setSize(fPreferredSize);

//  setSize(500,300);
 //   setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-300)/2);  //frame does this

    setLayout(gridBagLayout1);



    jLabel1.setText("Click on the 2nd row connectives, and atomic formulas, to toggle them T F.");
    jLabel2.setText("(Aim to get 100% of these right, in about a minute each.)");
    jLabel3.setText("(The clock stops 15 seconds while corrections are displayed.)");

    submitButton.setText("Submit");
    submitButton.addActionListener(new TPredTruthTable_submitButton_actionAdapter(this));


fShapeDisplay = new ShapeDisplay();

    fShapeDisplay.setPreferredSize(new Dimension(500, 100));
    fShapeDisplay.setMinimumSize(new Dimension(500, 100));

    add(fShapeDisplay,
           new GridBagConstraints(0, 0, 1, 1, 1.0, 0.1
         , GridBagConstraints.CENTER, GridBagConstraints.NONE,
     new Insets(0, 0, 0, 0), 30, 0));

add(jLabel1,
       new GridBagConstraints(0, 1, 1, 1, 1.0, 0.1
     , GridBagConstraints.CENTER, GridBagConstraints.NONE,
     new Insets(0, 0, 0, 0), 30, 0));


 add(jLabel2,
       new GridBagConstraints(0, 2, 1, 1, 1.0, 0.1
         , GridBagConstraints.CENTER, GridBagConstraints.NONE,
     new Insets(0, 0, 0, 0), 30, 0));

 add(jLabel3,
 new GridBagConstraints(0, 3, 1, 1, 1.0, 0.1
            , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 30, 0));


add(submitButton,
                           new GridBagConstraints(0, 5, 1, 1, 0.2, 0.1
                                     , GridBagConstraints.CENTER,
                                     GridBagConstraints.NONE,
                                     new Insets(0, 0, 0, 0), 100, 0));
 add(feedback,
                          new GridBagConstraints(0, 6, 1, 1, 0.0, 0.1
     , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 100, 0));

    jt.setBackground(new Color(200, 200, 200));  //no point to this because we haven't put it in the panel
    jt.setMaximumSize(new Dimension(200, 48));
    jt.setPreferredSize(new Dimension(200, 48));

    jsp=new JScrollPane(jt);
   // jsp.setSize(400,100);

    jPanel2.setLayout(flowLayout1);
    jPanel2.setSize(400,100); //this.getContentPane().add(jsp, java.awt.BorderLayout.CENTER);
  }

  public void setLabel2Text(String text){
    jLabel2.setText(text);
  }


  class ShapeDisplay extends JPanel{
    private List fShapes= new ArrayList();

     ShapeDisplay(){

       fSemantics=new TSemantics(fShapes,TShapePanel.gIndividualNames);

       buildInterpretation();

     }


int delta(){
  double random=Math.random();

    int delta=0;

    if (random<0.33)
      delta=30;
    else
      if (random<0.67)
        delta=60;

return
          delta;
}

    int gamma(){
  double random=Math.random();

    int delta=0;

    if (random<0.33)
      delta=15;
    else
      if (random<0.67)
        delta=30;

return
          delta;
}



     void buildInterpretation(){

fShapes.clear();


       TShape aShape;

  {


    aShape = new TInterpretationBoard(fSemantics);
      fShapes.add(aShape);


    aShape=new TProperty();
     aShape.setBoundsRect(new Rectangle(180,10,80,40));
     aShape.setColor(Color.red);
     aShape.setName('F');
     aShape.setSelected(false);
     fShapes.add(aShape);

     aShape=new TProperty();
     aShape.setBoundsRect(new Rectangle(240,20,60,60));
     aShape.setColor(Color.blue);
     aShape.setName('G');
     aShape.setSelected(false);
     fShapes.add(aShape);

     aShape=new TProperty();
     aShape.setBoundsRect(new Rectangle(250,10,80,60));
     aShape.setColor(Color.yellow);
     aShape.setName('H');
     aShape.setSelected(false);
     fShapes.add(aShape);


// we don't want shapes one on top of another

      aShape=new TIndividual(new Point(190+delta(),13+gamma()));
      aShape.setName('a');
      aShape.setSelected(false);
      fShapes.add(aShape);

      aShape=new TIndividual(new Point(257+delta(),20+gamma()));
            aShape.setName('b');
            aShape.setSelected(false);
            fShapes.add(aShape);

            aShape=new TIndividual(new Point(310+delta(),23+gamma()));
                  aShape.setName('c');
                  aShape.setSelected(false);
                  fShapes.add(aShape);


  }

  if (fSemantics.interpretationChanged()) ; // to update the Semantics


}



    protected void paintComponent(Graphics g) {

     super.paintComponent(g);  // background


       for(Iterator i=fShapes.iterator(); i.hasNext(); ) {
         ((TShape)i.next()).draw((Graphics2D)g);}



     }


  }

  void ask(){

    if (( (fMaxAttempts != -1) &&
       (fTotal >= fMaxAttempts))
     ||
     ( (fMaxTime != -1) &&
       (fElapsed >= fMaxTime))
     )
        {      // we're stopping

          submitButton.setEnabled(false);

        if (fTableModel!=null)
           fTableModel.setTogglingEnabled(false);
       return;
       }




    fTimeIncrementer.start();
      submitButton.setEnabled(true);

   //   this.getContentPane().remove(jsp);  //old one


   remove(jsp);

      jPanel2.remove(jsp);                 //not using at the moment

      int maxConnectives=5;
      boolean atomicAllowed=false;
      boolean noPropositions=true;
      boolean unaryPredsOnly=true;
      boolean noQuantifiers=true;
      boolean constantsOnly=true;
      String preferredVariable="";

       fRandom=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,atomicAllowed, noPropositions,unaryPredsOnly,noQuantifiers,constantsOnly,preferredVariable);


       while (fRandom.numConnectives()<3)
         fRandom=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,atomicAllowed, noPropositions,unaryPredsOnly,noQuantifiers,constantsOnly,preferredVariable);    //these need to be of similar difficulty
                                                                                  // say between 3 and 5 connectives


  fShapeDisplay.buildInterpretation();  // we'll get a new one, need to do this before the table model

 fTableModel=new TTModel(fRandom);

  jt= new JTable(fTableModel);  //we'll use the formula as its header

  Dimension tableSize=initializeTable(jt);

      jPanel2.remove(jsp);

   jsp=new JScrollPane(jt); //temp

   Dimension size=jt.getSize();

   jsp.setSize(tableSize);  // we' like to center it, but cannot seem to get this working
   jsp.setMaximumSize(tableSize);

   jsp.setBorder(BorderFactory.createLineBorder(Color.black));


 add(jsp, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.5
              , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 35, 0));


   // setVisible(true);     //  need to have this here else does not update properly


 // fContainer.setVisible(true);  commented out Oct 06

  setVisible(false);  //new Aug 16 06
  setVisible(true);
  }

Dimension initializeTable(JTable table){
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setIntercellSpacing(new Dimension(0,0));
    //table.setRowHeight(16);

    table.setOpaque(false);

    table.clearSelection();


    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)(table.getCellRenderer(0,0));

    renderer.setHorizontalAlignment(0);  /*bit of a trick, which will probably break,
    we want the table text centered, and really it only uses one renderer ie 00*/

 Font theFont=renderer.getFont();

  renderer.setFont(new Font(theFont.getName(),theFont.getStyle(),12));  //Windows is doing something funny with size, so we'll make it 12

  FontMetrics fm=getFontMetrics(theFont);  //not used yet but planning to calculate column widths

 JTableHeader header=table.getTableHeader();

   header.setReorderingAllowed(false);  // we don't want them dragging the columns around
  //  table.getTableHeader().setResizingAllowed(false);

  //header.set

    TableColumnModel columnModel=jt.getColumnModel();

    TableColumn column;

    int num=columnModel.getColumnCount();

    int cellWidth=25; //default is 75, careful, too narrow (eg12,14,16,20,22,24) won't work on Windows 26 workds

    String entry="";

    for (int i=0;i<num;i++){

      column = columnModel.getColumn(i);

      entry=(String)(fTableModel.getValueAt(0,i));

      if (entry.length()==2)
        cellWidth=30;
      else
        cellWidth=25;                   // yet all seem to have same width.


      // char symbol=randomStr.charAt(i);

     /*  switch (symbol){
    case '(': ;case ')' : width=8; break;
    case chNeg  : width=10; break;
  }*/

     //column.setMinWidth(width);
     column.setPreferredWidth(cellWidth);                      // do need this to stop spreading
   };

   fCSM=columnModel.getSelectionModel();
   fCSM.clearSelection();

   fCSM.addListSelectionListener(
      new OurListener());

   return
       new Dimension(num*cellWidth,48);

}

 public int getTotal(){
  return
      fTotal;
}

public int getCorrect(){
  return
      fCorrect;
 }

  void respond(boolean correct){

     fTimeIncrementer.stop();                 //to let them look at the right answer, if need be, ask() restarts it
     submitButton.setEnabled(false);          // ask resets it
     fTableModel.setTogglingEnabled(false);  // no need to reset this true, becuase the model changes

    fTotal += 1;
    if (correct)
      fCorrect += 1;


    String feedbackStr="";

   if (fMaxAttempts==-1)
     feedbackStr="You have " + fCorrect + " right out of " + fTotal +
                     " in "+ fElapsed  + " secs.";
   else
     feedbackStr="You have " + fCorrect + " right out of " + fTotal +
                     " in "+ fElapsed  + " secs. "
               +"[Attempt " +fMaxAttempts
                +", times out in: "+fMaxTime+" secs.]";




    feedback.setText(feedbackStr);

    if(!correct){
      Toolkit.getDefaultToolkit().beep();
      jsp.setBorder(BorderFactory.createLineBorder(Color.red));  // ""
      //jsp.repaint();
      fTableModel.showAnswer();

      //setVisible(true);
      Thread.yield();

      }
}








  public void run(){

    fElapsed=0;
//    fTimeIncrementer.start();

    ask();

  //  setVisible(true);

  // MainLoop main =new MainLoop(); Mon
  // main.start();
  }


  public void setMaxAttempts(int max){
    if (max>0)
      fMaxAttempts=max;
  }

  public void setMaxTime(long max){
    if (max>0)
      fMaxTime=max;
}


  public void submitButton_actionPerformed(ActionEvent actionEvent) {

    boolean correct=fTableModel.answerTrue();

    respond(correct);

    if (correct)                               //go straight on
      ask();
    else{
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          try {
            {
              Thread.yield();
              Thread.sleep(15000);               //give them time to see the answer
              ask();
              Toolkit.getDefaultToolkit().beep();
            }
          }
          catch (Exception ex) {}
        }
      });
    }

  }


  /////////// Inner Classes ///////////////////////////////



  class OurListener implements ListSelectionListener{
    public void valueChanged(ListSelectionEvent e){

     //really here we are not interested in selections, we are interested in mouse clicks

      if (e.getValueIsAdjusting())
        return; // pass if it isn't settled

      int index = 0; // e.getLastIndex(); //we have a single selection model, and nothing is selected to start with


      ListSelectionModel lsm =
            (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty()) {

        } else {
            index = lsm.getMinSelectionIndex();
            fTableModel.toggle(0,index);        //only toggle selected
        }
        jt.clearSelection(); //unfortunately fires listchange event
    }
  }



  class TimeIncrementer implements ActionListener{
            javax.swing.Timer t =new javax.swing.Timer(1000,this);

  public void start(){
     t.start();
  }

  public void stop(){
     t.stop();
  }

  public void actionPerformed (ActionEvent ae){
     fElapsed+=1;  //every second
  }
  }


  class TTModel extends AbstractTableModel{

    int fLength=0;

    ArrayList fStrings;

    Object [][] fData;
    String [] fHeader;
    String [] fAnswer;
    int fRowCount=2;
    TFormula fFormula;

    boolean fTogglingEnabled=true;

    TTModel(TFormula theFormula){
      fFormula=theFormula;
   String randomStr= fParser.writeFormulaToString(theFormula);


   /*we have the formula as a string, say "FavGb", we want the header
   to be an array of strings {"Fa","v","Gb"}, and the table data to
   be a two element array of array of string {{"T","T","F"},{"T","T","F"}}  the second row
   is the answer*/

   breakIntoStrings();

   fLength =fStrings.size();

   fHeader= new String[fLength]; // used also to determine which characters are connectives
   String [] randomArray = new String[fLength];
   String [] randomArray2 = new String[fLength];  //answer


   String currStr;
   for(int i=0;i<fLength;i++){
     currStr=(String)fStrings.get(i);
     fHeader[i]=currStr;
     randomArray[i]=currStr;
     randomArray2[i]="";          // set the 'answer' blank to start with
   }

 fData=  new Object[2][fLength];  //two rows

 //String answer=answerStr();
 fAnswer=toStringArray(answerStr());

 fData[0]=randomArray;
 fData[1]=randomArray2;
    }


void breakIntoStrings(){  /* the formulas consist of Fa Gb etc + brackets and connectives,
     we want atomic formulas in same cell */
  String randomStr= fParser.writeFormulaToString(fFormula);

  fStrings= new ArrayList();

  for (int i=0;i<randomStr.length();i++){
    if (randomStr.charAt(i)=='F'||
    randomStr.charAt(i)=='G'||
    randomStr.charAt(i)=='H'){
  fStrings.add(randomStr.substring(i, i+2));
  i+=1;
}
else
  fStrings.add(randomStr.substring(i, i+1));
  }

}


String answerStr(){
      TFormula answerFormula=fFormula.copyFormula();
      surgeryForAnswer(answerFormula);
      return
          fParser.writeFormulaToString(answerFormula);
}

public boolean answerTrue(){
      return
          equalStringArrays(fAnswer,(String[])fData[0]);
    }

boolean equalStringArrays(String [] a,String[] b){
      if (a.length==b.length){
        for(int i=0;i<a.length;i++){
           if (!a[i].equals(b[i]))
               return
                 false;
        }
        return
            true;
     }
    return
          false;
}

    /*what we have is a string like "Fav(Gb^Hc)" and we want "FF(TFF).
     We can determine the truth value of the connectives, then use surgery to replace those connectives with T or F
     then use our ordinary write routines*/

private boolean formulaTrue(TFormula root){
      if (root==null)                 //should never happen
       return
           false;


      switch (root.fKind){

        case TFormula.predicator:

        return
            fSemantics.propertyTrue(root.propertyName(), root.firstTerm().getInfo().charAt(0));


        case TFormula.unary:
          return
              !formulaTrue(root.fRLink);

        case TFormula.binary:
          if (fParser.isAnd(root))
            return
                (formulaTrue(root.fLLink)&&formulaTrue(root.fRLink));

          if (fParser.isOr(root))
             return
              (formulaTrue(root.fLLink)||formulaTrue(root.fRLink));

          if (fParser.isImplic(root))
             return
              ((!formulaTrue(root.fLLink))||formulaTrue(root.fRLink));

           if (fParser.isEquiv(root))
             return
              (((!formulaTrue(root.fLLink))||formulaTrue(root.fRLink))&&
               (formulaTrue(root.fLLink)||(!formulaTrue(root.fRLink))));
         default:
          ;
      }

      return
          false;
}

///////////////  getters and setters ///////////////////////


   public int getColumnCount(){
        return
            fLength;}
   public String getColumnName(int col) {
                return fHeader[col];}
public String[] getHeader(){
                 return
                     fHeader;
}
   public int getRowCount(){
                   return fRowCount;}

   public Object getValueAt(int row, int column){
        return
            fData[row][column];}

    /*
    public boolean isCellEditable(int row, int col) {

      if (row < 1)
        return true;
      else
        return false;
    }  */


    public void setValueAt(Object value, int row, int col) {


                fData[row][col] =  value;
                fireTableCellUpdated(row, col);
        }


 ///////////////   end of getters and setters ////////





public void showAnswer(){
   fData[1]=fAnswer;        //we probably don't need the fAnswer field

   fireTableChanged(new TableModelEvent(this,1));

   //fireTableStructureChanged();
}

void surgeryForAnswer(TFormula answerFormula){
   short kind=answerFormula.getKind();



     if (formulaTrue(answerFormula))
      answerFormula.setInfo("T");
    else
      answerFormula.setInfo("F");


 /* if ((kind==TFormula.unary)||(kind==TFormula.binary))  we need to worry about atomic   */{  //don't bother with atomic

  if (kind==TFormula.predicator){
    answerFormula.setRLink(null);   // drop the term, eg Fa now becomes F
  }

  if (answerFormula.getLLink()!=null)
    surgeryForAnswer(answerFormula.getLLink());

  if (answerFormula.getRLink()!=null)
    surgeryForAnswer(answerFormula.getRLink());
  }
}



void setTogglingEnabled(boolean value){
  fTogglingEnabled = value;
}


public void toggle (int row, int col){
  /*we only toggle row 0, from "T" to "F" and back, but only for connectives */

  if (fTogglingEnabled){

    String template = (String) fHeader[col]; //1 remains unchanged

    if (template.equals("T") ||
        template.equals("F") ||
        template.equals("(") ||
        template.equals(")"))
      return; //do nothing, because it is not a connective

    if ( ( (String) fData[0][col]).equals("T"))
      setValueAt("F", 0, col);
    else //when it first comes in it will be a connective, but then goes t/f
      setValueAt("T", 0, col);
  }
}

String [] toStringArray(String aString){
  int length =aString.length();

   String [] anArray = new String[length];

 for(int i=0;i<length;i++){
   anArray[i]=aString.substring(i,i+1);

 }

 return
     anArray;
}


  }
}

/////////////////////// JBuilder Classes ///////////////////

class TPredTruthTable_submitButton_actionAdapter
    implements ActionListener {
  private TPredTruthTable adaptee;
  TPredTruthTable_submitButton_actionAdapter(TPredTruthTable adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.submitButton_actionPerformed(actionEvent);
  }
}





