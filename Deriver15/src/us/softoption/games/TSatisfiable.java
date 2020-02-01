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

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import us.softoption.interpretation.TTestNode;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

/*You need to use a scroll pane for the tables, for it is that which handles the header (which is
 kind of separate from the table)*/


public class TSatisfiable extends JPanel{

  TParser fParser;//=new TParser();
  TFormula fRandom;
  int fCorrect=0;
  int fTotal=0;
  int fMaxAttempts=-1;

  JTextArea jText1;//JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();

  JTable jt = new JTable();
  TTModel fTableModel;
  ListSelectionModel csm;

  JButton submitButton = new JButton();
  JButton notButton = new JButton();

  JLabel feedback =new JLabel("You have " +fCorrect+" right out of " +fTotal+".");
  BorderLayout borderLayout2 = new BorderLayout();

  JScrollPane jsp =new JScrollPane(jt);

  JPanel jPanel2 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  boolean fLastWrong=false;
  boolean fAnswered=false;
  boolean fAnsweredSatisfiable=true;  /* there are two buttons, and they can answer 'satisfiable' (and be
                               right or wrong) or answer 'not satisfiable' (and be right or wrong)*/

  long fElapsed=0;
  long fMaxTime=-1;
  TimeIncrementer fTimeIncrementer = new TimeIncrementer();

  boolean fKeepRunning=false;

  Border fOldBorder;
  Container fContainer;

  public TSatisfiable(Container itsContainer, TParser itsParser){

 //    super("Show whether the formula is satisfiable ie make it true");

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

    setSize(500,230);
  //  setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-230)/2);

  setLayout(gridBagLayout1);


    //jLabel1.setText("Click on the 2nd row propositions, to toggle them T F.");


    jText1= new JTextArea("Click on the truth value assignment to the propositions, toggling them T F, to make the whole formula True."
        + " The default assigns values randomly."
        + " There need not be a unique answer."
        + " (Aim: 100% right, 10 seconds each. "
        + "The clock stops while corrections are displayed.)"

        );
    jText1.setOpaque(false);
    jText1.setEditable(false);
    jText1.setPreferredSize(new Dimension(400, 64));
    jText1.setWrapStyleWord(true);
    jText1.setLineWrap(true);

    jText1.setFont(new Font("Sans-Serif", Font.ITALIC, 12));

    JScrollPane aScrollPane=new JScrollPane(jText1);

    aScrollPane.setPreferredSize(new Dimension(400, 64));
    aScrollPane.setSize(new Dimension(400, 64));
    aScrollPane.getViewport().setBackground(SystemColor.control);
    aScrollPane.setOpaque(false);

    jLabel2.setText("(Aim to get 100% of these right, in about 10 seconds each.)");
    jLabel3.setText("(The clock stops 15 seconds while corrections are displayed.)");

    submitButton.setText("Submit");
    submitButton.addActionListener(new TSatisfiable_submitButton_actionAdapter(this));

    notButton.setText("Not satisfiable");
    notButton.addActionListener(new TSatisfiable_notButton_actionAdapter(this));
    fOldBorder=notButton.getBorder(); // so we can set it back after change

/*
    this.getContentPane().add(aScrollPane,
          new GridBagConstraints(0, 0, 2, 1, 1.0, 0.5
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));


    this.getContentPane().add(notButton,
                              new GridBagConstraints(0, 2, 1, 1, 0.2, 0.10
                                        , GridBagConstraints.CENTER,
                                        GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 0), 100, 0));


   this.getContentPane().add(submitButton,
                              new GridBagConstraints(1, 2, 1, 1, 0.2, 0.10
                                                                       , GridBagConstraints.CENTER,
                                                                       GridBagConstraints.NONE,
                                                                       new Insets(0, 0, 0, 0), 100, 0));

   this.getContentPane().add(feedback,
                             new GridBagConstraints(0, 3, 2, 1, 0.0, 0.10
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 100, 0)); */

add(aScrollPane,
          new GridBagConstraints(0, 0, 2, 1, 1.0, 0.5
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));


add(notButton,new GridBagConstraints(0, 2, 1, 1, 0.2, 0.10
                                        , GridBagConstraints.CENTER,
                                        GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 0), 100, 0));


add(submitButton,new GridBagConstraints(1, 2, 1, 1, 0.2, 0.10
                                                                       , GridBagConstraints.CENTER,
                                                                       GridBagConstraints.NONE,
                                                                       new Insets(0, 0, 0, 0), 100, 0));

add(feedback,new GridBagConstraints(0, 3, 2, 1, 0.0, 0.10
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

Dimension initializeTable(JTable table){
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setIntercellSpacing(new Dimension());
    table.setRowHeight(16);

    table.setOpaque(false);

    table.clearSelection();


    ((DefaultTableCellRenderer)(table.getCellRenderer(0,0))).setHorizontalAlignment(0);  /*bit of a trick, which will probably break,
    we want the table text centered, and really it only uses one renderer ie 00*/

    table.getTableHeader().setReorderingAllowed(false);  // we don't want them dragging the columns around
    table.getTableHeader().setResizingAllowed(true);

    TableColumnModel columnModel=jt.getColumnModel();

    TableColumn column;

    int num=columnModel.getColumnCount();

    int cellWidth=25; //default is 75, careful, too narrow (eg12,14,16,20,22,24) won't work on Windows 25, 26 workds

    for (int i=0;i<num;i++){

      column = columnModel.getColumn(i);

      // char symbol=randomStr.charAt(i);

     // int width=12; //default is 75

     /*  switch (symbol){
    case '(': ;case ')' : width=8; break;
    case chNeg  : width=10; break;
  }*/

  //    column.setMinWidth(width);
      column.setPreferredWidth(cellWidth);                      // do need this to stop spreading
   };


   csm=columnModel.getSelectionModel();
   csm.clearSelection();

   csm.addListSelectionListener(
      new OurListener());

   return
       new Dimension(num*cellWidth,48);

}

/*

  void respond(){
    fTotal += 1;
    if (!fLastWrong)
      fCorrect += 1;

    feedback.setText("You have " + fCorrect + " right out of " + fTotal +
                      " in "+ fElapsed  + " secs.");

    if(fLastWrong){
      Toolkit.getDefaultToolkit().beep();
      fTimeIncrementer.stop(); //to let them look at the right answer
      submitButton.setEnabled(false);
      notButton.setEnabled(false);
      fTableModel.setTogglingEnabled(false);  // no need to reset this true, becuase the model changes
      Border oldBorder=notButton.getBorder();

      jsp.setBorder(BorderFactory.createLineBorder(Color.red));


      if (fAnsweredSatisfiable){ //two possibilities, it is satisfiable but they have failed to find it, or not satisfiable
        if (fTableModel.isSatisfiable())
          fTableModel.showAnswer();
        else{
          notButton.setBorder(BorderFactory.createLineBorder(Color.red));  // answered satisfiable when not
        }
      }
      else{                  //answered not satisfiable when it is
        fTableModel.showAnswer();

      }

      try {
        Thread.sleep(15000);
      }
      catch (Exception ex) {}

      submitButton.setEnabled(true);
      notButton.setBorder(oldBorder);
      notButton.setEnabled(true);
      fTimeIncrementer.start();
    }

} */

  void respond(boolean correct){
    fTotal += 1;
    if (correct)
      fCorrect += 1;

 //   feedback.setText("You have " + fCorrect + " right out of " + fTotal +
  //                    " in "+ fElapsed  + " secs.");

     if (fMaxAttempts==-1)
       feedback.setText("You have " + fCorrect + " right out of " + fTotal +
                      " in "+ fElapsed  + " secs.");
    else
         feedback.setText("You have " + fCorrect + " right out of " + fTotal +
         " in "+ fElapsed  + " secs. [Attempt " +fMaxAttempts
               +", times out in: "+fMaxTime+" secs.]");





     if(!correct){

      if (fAnsweredSatisfiable){ //two possibilities, it is satisfiable but they have failed to find it, or not satisfiable
        if (fTableModel.isSatisfiable())
          fTableModel.showAnswer();
        else{
          fOldBorder=notButton.getBorder();                                // we will set it back
          notButton.setBorder(BorderFactory.createLineBorder(Color.red));  //answered satisfiable when wasn't
        }
      }
      else{                  //answered not satisfiable when it is
        fTableModel.showAnswer();
      }
    }

}

 void initializeButtons(){

submitButton = new JButton();
submitButton.setText("Submit");
submitButton.addActionListener(new TSatisfiable_submitButton_actionAdapter(this));

notButton = new JButton();
    notButton.setText("Not satisfiable");
    notButton.addActionListener(new TSatisfiable_notButton_actionAdapter(this));
 }

  public void run(){

    fElapsed=0;
    fKeepRunning=true;


    ask();

 //  MainLoop main =new MainLoop();
 //  main.start();
  }

public void setMaxAttempts(int max){
  if (max>0)
    fMaxAttempts=max;
}

public void setMaxTime(long max){
  if (max>0)
    fMaxTime=max;
}



  void ask(){

    if (( (fMaxAttempts != -1) &&
       (fTotal >= fMaxAttempts))
     ||
     ( (fMaxTime != -1) &&
       (fElapsed >= fMaxTime))
     )
   fKeepRunning=false;


 if (!fKeepRunning)

        {      // we're stopping

          submitButton.setEnabled(false);
          notButton.setEnabled(false);

        if (fTableModel!=null)
           fTableModel.setTogglingEnabled(false);
       return;
       }





    fTimeIncrementer.start();

    fAnswered=false;

   // this.getContentPane().remove(jsp);  //old one
   remove(jsp);  //old one
   jPanel2.remove(jsp);                 //not using at the moment

    int maxConnectives=5;

   fRandom=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);

   while (fRandom.numConnectives()<3)
     fRandom=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);    //these need to be of similar difficulty
                                                                              // say between 3 and 5 connectives



     fTableModel=new TTModel(fRandom);

jt= new JTable(fTableModel);  //we'll use the formula as its header

Dimension tableSize=initializeTable(jt);

    jPanel2.remove(jsp);

 jsp=new JScrollPane(jt); //temp

 Dimension size=jt.getSize();

 jsp.setSize(tableSize);  // we' like to center it, but cannot seem to get this working
 jsp.setMaximumSize(tableSize);


 jsp.setBorder(BorderFactory.createLineBorder(Color.black));


/* this.getContentPane().add(jsp,
                                  new GridBagConstraints(0, 1, 2, 1, 1.0, 0.5
            , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 35, 0)); */

add(jsp, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.5
            , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 35, 0));

 /*jsp.*/setVisible(false);
 /*jsp.*/setVisible(true);
 //fContainer.setVisible(true);
  }


  public int getTotal(){
   return
       fTotal;
 }

 public int getCorrect(){
   return
       fCorrect;
 }





void standardAction(boolean correct){

  respond(correct);

    if (correct)                               //go straight on
      ask();
    else{
      Toolkit.getDefaultToolkit().beep();
      fTimeIncrementer.stop(); //to let them look at the right answer
      submitButton.setEnabled(false);
      notButton.setEnabled(false);
      fTableModel.setTogglingEnabled(false);  // no need to reset this true, becuase the model changes
      jsp.setBorder(BorderFactory.createLineBorder(Color.red));

      {Thread worker = new Thread(){            //move to a new thread
      		public void run(){
      	          try {
      	              Thread.sleep(15000);  //give them time to see the answer
      	          }
      	          catch (Exception ex) {}
      		//come back later from event dispatch thread
      		SwingUtilities.invokeLater(new Runnable() {
      	        public void run() {
      	           ask();            // from event dispatch thread
      	           if (fKeepRunning){
      	                submitButton.setEnabled(true);
      	                notButton.setBorder(fOldBorder); //
      	                notButton.setEnabled(true);
      	                fTimeIncrementer.start();
      	              }
      	              Toolkit.getDefaultToolkit().beep();
      	        }
      	      });
      		}
      	};
      	worker.start();              // new thread
      }





      /* SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          try {
            {
              Thread.yield();
              Thread.sleep(15000);               //give them time to see the answer
              ask();

              if (fKeepRunning){
                submitButton.setEnabled(true);
                notButton.setBorder(fOldBorder); //
                notButton.setEnabled(true);
                fTimeIncrementer.start();
              }
              Toolkit.getDefaultToolkit().beep();
            }
          }
          catch (Exception ex) {}
        }
      });*/



    }


}

  public void submitButton_actionPerformed(ActionEvent actionEvent) {

    fAnsweredSatisfiable=true;
    boolean correct=fTableModel.satisfiableAnswerTrue();
    standardAction(correct);

  }

  public void notButton_actionPerformed(ActionEvent actionEvent) {

    fAnsweredSatisfiable=false;
    boolean correct=fTableModel.notSatisfiableAnswerTrue();
     standardAction(correct);

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
    Object [][] fData;
    String [] fHeader;
    String [] fAnswer;
    int fRowCount=2;
    TFormula fFormula;
    ArrayList fInterpretation=null; //an Interpretation that satisfies it supplied by the theorem prover
    boolean fTogglingEnabled=true;

    TTModel(TFormula theFormula){
      fFormula=theFormula;



      String randomStr= fParser.writeFormulaToString(theFormula);

   initializeTableData(randomStr);

   fInterpretation = TTestNode.decidableFormulaSatisfiable(fParser,fFormula);
    }



void initializeTableData(String randomStr){
    /*we have the formula as a string, say "AvB", we want the header
     to be an array of strings {"A","v","B"}, and the table data to
     be a two element array of array of string {{"T","v","T"},{"","",""}}  notice that
     the propositions have been initialized to "F", eventually the answer will go in the second row*/

     fLength =randomStr.length();

     fHeader= new String[fLength]; // used also to determine which characters are connectives
     String [] randomArray = new String[fLength];
     String [] randomArray2 = new String[fLength];

     String currStr;
     char currChar;

     String alreadyAssigned="";
     String values="";

     for(int i=0;i<fLength;i++){
       currStr=randomStr.substring(i,i+1);
       currChar=randomStr.charAt(i);

       fHeader[i]=currStr;

       if (fParser.isConnective(currChar)||
        /*  // TCopiParser.isCopiConnective(currChar)||
           TCopiParser.isConnective(currChar)||
      //   TBergmannParser.isBergmannConnective(currChar)||
           TBergmannParser.isConnective(currChar)|| */


     /*      currChar==chNeg||
           currChar==chAnd||
           currChar==chOr||
           currChar==chImplic||
           currChar==chEquiv||  */
           currChar=='('||
           currChar==')')

          randomArray[i]=currStr;  //pass through unchanged
        else{

          /*now we try to assign randomly */

          int index = alreadyAssigned.indexOf(currChar);

          if (index != -1) {
            randomArray[i] = values.substring(index, index+1); // assign the prop the same value as before
          }
          else {

            double random = Math.random();

            if (random < 0.5)
              randomArray[i] = "T";
            else
              randomArray[i] = "F"; // initialize props randomly

            alreadyAssigned+=currChar;  // note what we have done
            values+=randomArray[i];
          }
        }
        randomArray2[i]="";          // set the 'answer' blank to start with
     }

   fData=  new Object[2][fLength];  //two rows

   fAnswer=toStringArray(answerStr());  //do we need this?

   fData[0]=randomArray;
   fData[1]=randomArray2;
}


private boolean isSatisfiable(){
  return
      fInterpretation!=null;
}

String answerStr(){
      TFormula answerFormula=fFormula.copyFormula();
      surgeryForAnswer(answerFormula);
      return
          fParser.writeFormulaToString(answerFormula);
}

public boolean satisfiableAnswerTrue(){   //if satisfiable and first row is true
      return
          rowATrueFormula(0);
    }

public boolean notSatisfiableAnswerTrue(){
  return
      !isSatisfiable();
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

boolean rowATrueFormula(int row){

  if (row<0||!(row<getRowCount()))
     return
        false;

  String formulaStr =fromStringArray((String[])fData[row]);

  TFormula root = new TFormula();
  StringReader aReader = new StringReader(formulaStr);
  ArrayList dummy = new ArrayList();
  boolean wellFormed = fParser.wffCheck(root, /*dummy,*/
             aReader);

  if (!wellFormed)
    return
        false;
  else
    return
        formulaTrue(root);

}

    /*what we have is a string like "Fv(T^F)" and we want "FF(TFF).
     We can determine the truth value of the connectives, then use surgery to replace those connectives with T or F
     then use our ordinary write routines*/

private boolean formulaTrue(TFormula root){
      if (root==null)                 //should never happen
       return
           false;


      switch (root.fKind){

        case TFormula.predicator:
          if (root.getInfo().equals("T"))
             return
               true;
          else{
            if (root.getInfo().equals("F"))
              return
                  false;
            else
              return
                  false;
          }
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



  if (fInterpretation!=null){ //should not be null if this is called, and it will be a list like A,~B, etc
    String trueProps= TFormula.trueAtomicFormulasInList(fInterpretation);
    String falseProps= TFormula.falseAtomicFormulasInList(fInterpretation);

    if (trueProps!=null)
      for(int i=0;i<trueProps.length();i++){
        setAllOccurences(1,trueProps.charAt(i),'T');
      }
    if (falseProps!=null)
        for(int i=0;i<falseProps.length();i++){
          setAllOccurences(1,falseProps.charAt(i),'F');
        }


  }

//   fData[1]=fAnswer;        //we probably don't need the fAnswer field

   fireTableChanged(new TableModelEvent(this,1));

   //fireTableStructureChanged();
}

void surgeryForAnswer(TFormula answerFormula){
  short kind=answerFormula.getKind();

  if ((kind==TFormula.unary)||(kind==TFormula.binary)){  //don't bother with atomic

    if (formulaTrue(answerFormula))
      answerFormula.setInfo("T");
    else
      answerFormula.setInfo("F");

  if (answerFormula.getLLink()!=null)
    surgeryForAnswer(answerFormula.getLLink());

  if (answerFormula.getRLink()!=null)
    surgeryForAnswer(answerFormula.getRLink());
  }
}


void toggleAll(char proposition){  //we only change row 0

   char label;

  for (int i=0;i<getColumnCount();i++){
    String template=(String)fHeader[i];

    if (template==null||template.length()<1)
    break;                                 //should never happen

    label=template.charAt(0);

    if (label==proposition){
      if (((String)fData[0][i]).equals("T"))
         setValueAt("F",0,i);
      else                      //when it first comes in it will be a connective, but then goes t/f
         setValueAt("T",0,i);
    }

  }
}

void setAllOccurences(int row, char proposition,char value){  //we only change row 0

   char label;

  for (int i=0;i<getColumnCount();i++){
    String template=(String)fHeader[i];

    if (template==null||template.length()<1)
    break;                                 //should never happen

    label=template.charAt(0);

    if (label==proposition){
       setValueAt(String.valueOf(value),row,i);
    }

  }
}

void setTogglingEnabled(boolean value){
  fTogglingEnabled=value;
}

public void toggle (int row, int col){
  /*we only toggle row 0, from "T" to "F" and back, but only for propositions
   we also have to remain in synch ie if A is set to T in one instance, it has to
   be set for all instances*/

 if (fTogglingEnabled){

   String template = (String) fHeader[col]; // remains unchanged

   if (template == null || template.length() < 1)
     return; //should never happen

   char dataChar = template.charAt(0);

   if (dataChar == chNeg ||
       dataChar == chAnd ||
       dataChar == chOr ||
       dataChar == chImplic ||
       dataChar == chEquiv ||
       dataChar == '(' ||
       dataChar == ')')
     return; //do nothing, because it is a connective

   toggleAll(dataChar); //change all of them
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

String fromStringArray(String[] aStringArray){
    int length =aStringArray.length;

    String answer="";

   for(int i=0;i<length;i++){
     answer+=aStringArray[i];
   }
   return
       answer;
}


  }
}

/////////////////////// JBuilder Classes ///////////////////

class TSatisfiable_submitButton_actionAdapter
    implements ActionListener {
  private TSatisfiable adaptee;
  TSatisfiable_submitButton_actionAdapter(TSatisfiable adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.submitButton_actionPerformed(actionEvent);
  }
}

class TSatisfiable_notButton_actionAdapter
    implements ActionListener {
  private TSatisfiable adaptee;
  TSatisfiable_notButton_actionAdapter(TSatisfiable adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.notButton_actionPerformed(actionEvent);
  }
}



