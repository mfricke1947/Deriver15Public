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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

/*You need to use a scroll pane for the tables, for it is that which handles the header (which is
 kind of separate from the table)*/


public class TModalSatisfiable extends JPanel{

  TParser fParser;//=new TParser();
  TFormula fRandom;
  int fCorrect=0;
  int fTotal=0;
  int fMaxAttempts=-1;

  JTextArea jText1;//JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();

  JTable jt = new JTable();
  TruthTableModel fTableModel;
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

  public TModalSatisfiable(Container itsContainer, TParser itsParser){

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
    submitButton.addActionListener(new TModalSatisfiable_submitButton_actionAdapter(this));

    notButton.setText("Not satisfiable");
    notButton.addActionListener(new TModalSatisfiable_notButton_actionAdapter(this));
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
submitButton.addActionListener(new TModalSatisfiable_submitButton_actionAdapter(this));

notButton = new JButton();
    notButton.setText("Not satisfiable");
    notButton.addActionListener(new TModalSatisfiable_notButton_actionAdapter(this));
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



     fTableModel=new TruthTableModel(fRandom,fParser,TruthTableModel.SATISFIABLE);

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


  
}

/////////////////////// JBuilder Classes ///////////////////

class TModalSatisfiable_submitButton_actionAdapter
    implements ActionListener {
  private TModalSatisfiable adaptee;
  TModalSatisfiable_submitButton_actionAdapter(TModalSatisfiable adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.submitButton_actionPerformed(actionEvent);
  }
}

class TModalSatisfiable_notButton_actionAdapter
    implements ActionListener {
  private TModalSatisfiable adaptee;
  TModalSatisfiable_notButton_actionAdapter(TModalSatisfiable adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.notButton_actionPerformed(actionEvent);
  }
}



