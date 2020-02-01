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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;


/*ask() starts the whole thing, and sets up a listener. Then ourListener either calls ask
directly, or, if the previous answer is wrong, shows the right answer while waiting then
calling ask

 if fMaxAttempts is set, then ask() will not ask more than these

*/



public class TMainConnective extends JPanel{
  int fIndex;                    //of main connective
  TParser fParser;
  TFormula fRandom;
  boolean fPropositional=true;
  int fCorrect=0;
  int fTotal=0;

  int fMaxAttempts=-1;

  JLabel jLabel1 = new JLabel();
  JTable jt = new JTable();
  JLabel jLabel2 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();

  JLabel feedback =new JLabel("You have " +fCorrect+" right out of " +fTotal+".");
  BorderLayout borderLayout2 = new BorderLayout();

  ListSelectionModel fCSM;
  OurListener fOurListener= new OurListener();

  long fElapsed=0;
  long fMaxTime=-1;
  TimeIncrementer fTimeIncrementer = new TimeIncrementer();

  Container fContainer;

  public TMainConnective(Container itsContainer,TParser itsParser){

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

    setSize(400,200);
  //  setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-400)/2, (Toolkit.getDefaultToolkit().getScreenSize().height-200)/2);
 //   this.setResizable(false);
    
    setBackground(Color.lightGray);


    jLabel1.setText("Click once on the main connective.");
    jLabel2.setEnabled(false);
    jLabel2.setText("(Aim to get 100% of these right, in about 3 seconds each.)");
  //  this.getContentPane().setLayout(borderLayout1);

 /*   setLayout(borderLayout1);

    jt.setMaximumSize(new Dimension(200, 16));

    jt.setPreferredSize(new Dimension(200, 16));
    jt.setSelectionBackground(Color.lightGray);
    jPanel1.setLayout(borderLayout2);
    jPanel1.setMaximumSize(new Dimension(400, 64));
    jPanel1.setMinimumSize(new Dimension(400, 64));
    jPanel1.setOpaque(false);
    jPanel1.setPreferredSize(new Dimension(400, 64));
    feedback.setMaximumSize(new Dimension(161, 32));
    feedback.setMinimumSize(new Dimension(161, 32));

   add(jLabel1, java.awt.BorderLayout.NORTH);
   add(jLabel2, java.awt.BorderLayout.CENTER);
   add(jPanel1, java.awt.BorderLayout.SOUTH);

  jPanel1.add(feedback, java.awt.BorderLayout.SOUTH);
  jPanel1.add(jt, java.awt.BorderLayout.WEST);  */



   setLayout(new GridBagLayout());

 //lower panel
   jPanel1.setLayout(new GridBagLayout());
   jPanel1.setMaximumSize(new Dimension(380, 32));  //400  64
   jPanel1.setMinimumSize(new Dimension(380, 32));
   jPanel1.setOpaque(false);
   jPanel1.setPreferredSize(new Dimension(380, 32));


   jt.setMaximumSize(new Dimension(180, 16));  //200
   jt.setPreferredSize(new Dimension(180, 16));  //200
   jt.setSelectionBackground(Color.lightGray);
   jPanel1.add(jt, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                 ,GridBagConstraints.WEST, GridBagConstraints.NONE,
                  new Insets(0, 0, 0, 0), 0, 0));

   feedback.setMaximumSize(new Dimension(161, 32));
   feedback.setMinimumSize(new Dimension(161, 32));
/*   jPanel1.add(feedback, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                   ,GridBagConstraints.WEST, GridBagConstraints.NONE,
                     new Insets(10, 0, 0, 0), 0, 0)); */

//main panel

   add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE,
                      new Insets(10, 10, 10, 10), 0, 0));
   add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                      ,GridBagConstraints.WEST, GridBagConstraints.NONE,
                       new Insets(10, 10, 10, 10), 0, 0));
   add(jPanel1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                     ,GridBagConstraints.WEST, GridBagConstraints.NONE,
                  new Insets(10, 10, 10, 10), 0, 0));

   add(feedback, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                  ,GridBagConstraints.WEST, GridBagConstraints.NONE,
                     new Insets(10, 10, 0, 0), 0, 0));
}


public void setMaxAttempts(int max){
  if (max>0)
    fMaxAttempts=max;

  feedback.setText("You have " +
      fCorrect+
      " right out of " +
      fTotal+
      " attempted. [You should attempt "+
      fMaxAttempts + ".]");

}

public void setMaxTime(long max){
  if (max>0)
    fMaxTime=max;
}

public void setPropositional(boolean useProp){
  fPropositional=useProp;
}


/*
public  void ask(){
  if (( (fMaxAttempts != -1) &&
      (fTotal >= fMaxAttempts))
    ||
    ( (fMaxTime != -1) &&
      (fElapsed >= fMaxTime))
    )
       {      // we're stopping

       if (fCSM!=null)
          fCSM.removeListSelectionListener(
               fOurListener);
      return;
      }
   fTimeIncrementer.start();

    jPanel1.remove(jt);
    jPanel1.repaint();

    if (fPropositional)
       fRandom = us.softoption.games.TRandomFormula.randomPropFormula(5, false);
    else{
      boolean noPropositions=false;
      boolean unaryPredsOnly=false;
      boolean noQuantifiers=false;
      boolean constantsOnly=false;
      String preferredVariable="";
      fRandom = us.softoption.games.TRandomFormula.randomPredFormula(5, false,noPropositions,unaryPredsOnly,noQuantifiers,constantsOnly,preferredVariable);
    }
    String randomStr = fParser.writeFormulaToString(fRandom);
    fIndex = fParser.indexOfMainConnective(fRandom);

    int length = randomStr.length();

    String[] randomArray = new String[length];

    for (int i = 0; i < length; i++) {
      randomArray[i] = randomStr.substring(i, i + 1);
    }

    String[][] row = new String[1][length];

    row[0] = randomArray;
    String[] header = new String[length];
    for (int i = 0; i < length; i++) {
      header[i] = "";
    }

    jt = new JTable(row, header);

    Dimension tableSize = initializeTable(jt);

    jPanel1.add(jt, BorderLayout.WEST);

    fContainer.setVisible(true); // we need it to update

    //jPanel1.repaint();  // new Jan06

// requestFocus();  //new

// add(new JTable(new String[2][2],new String[]{"Hello","l"}));

    //add(jt);

    jt.requestFocus(); // need this or does not show last selected

}

*/

public  void ask(){
  if (( (fMaxAttempts != -1) &&
      (fTotal >= fMaxAttempts))
    ||
    ( (fMaxTime != -1) &&
      (fElapsed >= fMaxTime))
    )
       {      // we're stopping

       if (fCSM!=null)
          fCSM.removeListSelectionListener(
               fOurListener);
      return;
      }
   fTimeIncrementer.start();

    jPanel1.remove(jt);
    jPanel1.repaint();
    fContainer.repaint(); // new Aug06

    if (fPropositional)
       fRandom = us.softoption.games.TRandomFormula.randomPropFormula(5, false);
    else{
      boolean noPropositions=false;
      boolean unaryPredsOnly=false;
      boolean noQuantifiers=false;
      boolean constantsOnly=false;
      String preferredVariable="";
      fRandom = us.softoption.games.TRandomFormula.randomPredFormula(5, false,noPropositions,unaryPredsOnly,noQuantifiers,constantsOnly,preferredVariable);
    }
    String randomStr = fParser.writeFormulaToString(fRandom);
    fIndex = fParser.indexOfMainConnective(fRandom);

    int length = randomStr.length();

    String[] randomArray = new String[length];

    for (int i = 0; i < length; i++) {
      randomArray[i] = randomStr.substring(i, i + 1);
    }

    String[][] row = new String[1][length];

    row[0] = randomArray;
    String[] header = new String[length];
    for (int i = 0; i < length; i++) {
      header[i] = "";
    }

    jt = new JTable(row, header);

    Dimension tableSize = initializeTable(jt);

 //   jPanel1.add(jt, BorderLayout.WEST);

    jPanel1.add(jt, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.NONE,
               new Insets(0, 0, 0, 0), 0, 0));


    fContainer.setVisible(true); // we need it to update

    fContainer.repaint(); // new Aug06

    //jPanel1.repaint();  // new Jan06

// requestFocus();  //new

// add(new JTable(new String[2][2],new String[]{"Hello","l"}));

    //add(jt);

    /*jsp.*/setVisible(false);
    /*jsp.*/setVisible(true);
    fContainer.setVisible(true);

    jt.requestFocus(); // need this or does not show last selected

}


  public int getTotal(){
   return
       fTotal;
 }

 public int getCorrect(){
   return
       fCorrect;
 }



  Dimension initializeTable(JTable table){
    jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
jt.setIntercellSpacing(new Dimension());
jt.setRowHeight(16);

 jt.setOpaque(false);

 jt.setSelectionBackground(Color.lightGray);


TableColumnModel columnModel=jt.getColumnModel();

 int num=columnModel.getColumnCount();

TableColumn column;

 int cellWidth=fParser.fMinCellWidth; //12 or 14 //default is 75, careful, too narrow (eg12,14,16,20,22,24) won't work on Windows with the header 25, 26 workds

for (int i=0;i<num;i++){

  column = columnModel.getColumn(i);

 // char symbol=randomStr.charAt(i);

  //int width=12; //default is 75

 // switch (symbol){
  //  case '(': ;case ')' : width=8; break;
 //   case chNeg  : width=10; break;
 // }

  column.setMinWidth(1);
  column.setPreferredWidth(cellWidth);
};


fCSM=columnModel.getSelectionModel();

fCSM.clearSelection();
fCSM.setSelectionInterval(num-1,num-1);                //the last one cannot be main connective, and something has to be selected

fCSM.addListSelectionListener(
      fOurListener);


return
       new Dimension(num*cellWidth,48);
  }



public void run(){

      fElapsed=0;
      fTimeIncrementer.start();

      ask();
  }


void showCorrect(){   //if answer is wrong

      Toolkit.getDefaultToolkit().beep();

      fCSM.clearSelection();
      DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)(jt.getCellRenderer(0,fIndex));  //?

    //  renderer.setBackground(Color.BLACK);
    //  renderer.setBackground(Color.WHITE);

   // renderer.setOpaque(false);  //experimenting
  //  renderer.setForeground(null); // defaults to table
  //  renderer.setBackground(null);

      fCSM.setSelectionInterval(fIndex, fIndex); //show right answer (which generates selection events)

      jt.requestFocus();

  //    Thread.yield();

}


/*
class OurListener implements ListSelectionListener{
   public void valueChanged(ListSelectionEvent e){

      if (e.getValueIsAdjusting())
        return; // pass if it isn't settled

      int index=0;

      ListSelectionModel lsm =                        //should just use fCSM here
                     (ListSelectionModel)e.getSource();

      if (lsm.isSelectionEmpty()) {
         return;
      }
      else {
        index = lsm.getMinSelectionIndex();
      }

     boolean correct=(index == fIndex);


     fTotal += 1;
    if (correct)
      fCorrect += 1;

    feedback.setText("You have " + fCorrect + " right out of " + fTotal +
                      " in "+ fElapsed  + " secs.");

       if(correct){
   //      respond(correct);
         ask();
       }
       else{
          fTimeIncrementer.stop();                 //to let them look at the right answer, if need be, ask() restarts it

          Toolkit.getDefaultToolkit().beep();

          fCSM.removeListSelectionListener(
            fOurListener);                   //stop listening until we have finished with the feedback

          fCSM.clearSelection();
          fCSM.setSelectionInterval(fIndex, fIndex); //show right answer

          jt.setValueAt(jt.getValueAt(0,fIndex),0,fIndex);  // trying to get the windows machines to update
          jt.requestFocus();

          Thread worker= new Thread(){
            public void run(){

              yield();   //new

              try {sleep(3000);
                  }catch (Exception ex) {};

            SwingUtilities.invokeLater(new Runnable(){
                public void run(){

                  ask();                      //starts time incrementer, adds a new listener

                  Toolkit.getDefaultToolkit().beep();

            }
          });
            }};

          worker.start();

    }

   }
  }

*/

class OurListener implements ListSelectionListener{
   public void valueChanged(ListSelectionEvent e){

      if (e.getValueIsAdjusting())
        return; // pass if it isn't settled

      int index=0;

      ListSelectionModel lsm =                        //should just use fCSM here
                     (ListSelectionModel)e.getSource();

      if (lsm.isSelectionEmpty()) {
         return;
      }
      else {
        index = lsm.getMinSelectionIndex();
      }

     boolean correct=(index == fIndex);


     fTotal += 1;
    if (correct)
      fCorrect += 1;

    if (fMaxAttempts==-1)
       feedback.setText("You have " + fCorrect + " right out of " + fTotal +
                      " in "+ fElapsed  + " secs.");
    else
        feedback.setText("You have " + fCorrect + " right out of " + fTotal +
                " in "+ fElapsed  + " secs. [Attempt " +fMaxAttempts
                +", times out in: "+fMaxTime+" secs.]");


       if(correct){
   //      respond(correct);
         ask();
       }
       else{
          fTimeIncrementer.stop();                 //to let them look at the right answer, if need be, ask() restarts it

          Toolkit.getDefaultToolkit().beep();

          fCSM.removeListSelectionListener(
            fOurListener);                   //stop listening until we have finished with the feedback

          fCSM.clearSelection();
          fCSM.setSelectionInterval(fIndex, fIndex); //show right answer

          jt.setValueAt(jt.getValueAt(0,fIndex),0,fIndex);  // trying to get the windows machines to update
          jt.requestFocus();

          // we'll just use the applet thread Aug 06

         Thread worker= new Thread(){
            public void run(){
           try {sleep(3000);
                  }catch (Exception ex) {};

            SwingUtilities.invokeLater(new Runnable(){
                public void run(){

                  ask();                      //starts time incrementer, adds a new listener

                  Toolkit.getDefaultToolkit().beep();

            }
          });
            }};

          worker.start();
       /*   try{     Thread.sleep(3000); } catch (InterruptedException ex){};
          ask();
          Toolkit.getDefaultToolkit().beep(); */
    }


   }
  }

        class TimeIncrementer implements ActionListener{
            javax.swing.Timer t =new javax.swing.Timer(1000,this);

       /*     TimeIncrementer(){
              t.start();
            } */

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

