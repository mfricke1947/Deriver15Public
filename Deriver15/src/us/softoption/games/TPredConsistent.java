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


import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.TJournal;
import us.softoption.editor.TJournalStub;
import us.softoption.interpretation.TShapePanel;
import us.softoption.interpretation.TTestNode;
import us.softoption.parser.TFormula;
//import hostApplets.Interpretations;
import us.softoption.parser.TParser;

/*You need to use a scroll pane for the tables, for it is that which handles the header (which is
 kind of separate from the table)*/


public class TPredConsistent extends JPanel{

  TParser fParser;//=new TParser();
  TFormula fRandom;
  TFormula fRandom2;
  TFormula fRandom3;
  boolean fUseQuantifiers=false;

  int fCorrect=0;
  int fTotal=0;
  int fMaxAttempts=-1;

  String fInterpretation="";

  ArrayList fInterpretationList=null; //an Interpretation that satisfies it supplied by the theorem prover

  JTextArea fInstructions;

  JTextArea fInterpretationText;
  JScrollPane aScrollPane2;

  JLabel jLabel2 = new JLabel();

  JButton submitButton = new JButton();
  JButton notButton = new JButton();

  JLabel feedback =new JLabel("You have " +fCorrect+" right out of " +fTotal+".");

  GridBagLayout gridBagLayout1 = new GridBagLayout();

  boolean fLastWrong=false;
  boolean fAnswered=false;
  boolean fAnsweredSatisfiable=true;  /* there are two buttons, and they can answer 'satisfiable' (and be
                               right or wrong) or answer 'not satisfiable' (and be right or wrong)*/
  boolean fNextThread=true;

  long fElapsed=0;
  long fMaxTime=-1;
  TimeIncrementer fTimeIncrementer = new TimeIncrementer();

  boolean fKeepRunning=false;

  Border fOldBorder;
  Container fContainer;

  TDeriverDocument fDeriverDocument;
  TShapePanel fShapePanel;

  Dimension fDrawingSize=new Dimension(500,240);


  public TPredConsistent(Container itsContainer,TParser itsParser){

	  fParser=itsParser;
	  createGUI(itsContainer);
  }

  /*********** overridden by TPredInvalidOld ***********/
  void initializeInstructions(){
		fInstructions= new JTextArea("Produce an Interpretation to satisfy all the displayed formulas at once ie make them all true."
		          + " There are usually many correct answers."
		          + " (Aim: 100% right, maybe 5 minutes each. "
		          + "The clock stops while corrections are displayed.)"

		          );

	}


  void labelNotButton(){
	  notButton.setText("Not satisfiable");
	}



  String notSatisfiableAnswer(){
      return
      "Not satisfiable";

}

  String produceLabel2(){
		 String randomStr = fParser.writeFormulaToString(fRandom);
		 String randomStr2 = fParser.writeFormulaToString(fRandom2);
		 String randomStr3 = fParser.writeFormulaToString(fRandom3);

		 return
		 "Formulas: "+randomStr+", "+randomStr2+", " + randomStr3;

		  }

  TFormula satisfiableTestFormula(){
	  return
	      TFormula.conjoinFormulas(TFormula.conjoinFormulas(fRandom.copyFormula(),
	       fRandom2.copyFormula()),fRandom3.copyFormula());
	}


  /******************************************/





  void createGUI(Container contentPane){
	  fContainer=contentPane;
	  TJournal host=new TJournalStub();
	  fDeriverDocument= new TDeriverDocument(host);
	  fShapePanel=fDeriverDocument.fShapePanel;

	   setSize(500,230);
	      setLayout(gridBagLayout1);

	      /*drawing*/

		  	JScrollPane aScroller=new JScrollPane(fShapePanel);
			aScroller.setPreferredSize(fDrawingSize);
			aScroller.setMinimumSize(new Dimension(300,200));

			add(aScroller,new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
			       ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 0), 0, 0));

		 /*instructions*/
			initializeInstructions();


			fInstructions.setOpaque(false);
			fInstructions.setEditable(false);
			fInstructions.setPreferredSize(new Dimension(400, 48));
			fInstructions.setWrapStyleWord(true);
			fInstructions.setLineWrap(true);

			fInstructions.setFont(new Font("Sans-Serif", Font.ITALIC, 12));

	      JScrollPane aScrollPane=new JScrollPane(fInstructions);

	      aScrollPane.setPreferredSize(new Dimension(400, 48));
	      aScrollPane.setSize(new Dimension(400, 48));
	      aScrollPane.getViewport().setBackground(SystemColor.control);
	      aScrollPane.setOpaque(false);

		  add(aScrollPane,
		            new GridBagConstraints(0, 1, 2, 1, 0, 0.3
		          , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
		          new Insets(0, 0, 0, 0), 0, 0));

	      /*we leave room for formula at (0,2)*/

		  /*interpretation*/
	   fInterpretationText= new JTextArea("");


	   fInterpretationText.setOpaque(false);
	   fInterpretationText.setEditable(false);
	   fInterpretationText.setPreferredSize(new Dimension(400, 64));
	   fInterpretationText.setWrapStyleWord(true);
	   fInterpretationText.setLineWrap(true);

	   aScrollPane2=new JScrollPane(fInterpretationText);

	   aScrollPane2.setPreferredSize(new Dimension(400, 64));
	   aScrollPane2.setSize(new Dimension(400, 64));
	   aScrollPane2.getViewport().setBackground(SystemColor.control);
	   aScrollPane2.setOpaque(false);

	  add(aScrollPane2,new GridBagConstraints(0, 3, 2, 1, 0, 0.35
	                                            , GridBagConstraints.CENTER,
	                                            GridBagConstraints.BOTH,
	                                          new Insets(0, 0, 0, 0), 0, 0));
	  /*buttons*/

      submitButton.setText("Submit");
      submitButton.addActionListener(new TPredConsistent_submitButton_actionAdapter(this));

      labelNotButton();
      notButton.addActionListener(new TPredConsistent_notButton_actionAdapter(this));
      fOldBorder=notButton.getBorder(); // so we can set it back after change

	  add(notButton,new GridBagConstraints(0, 4, 1, 1, 0.2, 0.10
	                                          , GridBagConstraints.CENTER,
	                                          GridBagConstraints.NONE,
	                                          new Insets(0, 0, 0, 0), 100, 0));

	  add(submitButton,new GridBagConstraints(1, 4, 1, 1, 0.2, 0.10
	                                                                         , GridBagConstraints.CENTER,
	                                                                         GridBagConstraints.NONE,
	                                                                         new Insets(0, 0, 0, 0), 100, 0));

	  add(feedback,new GridBagConstraints(0, 5, 2, 1, 0.0, 0.10
	          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
	          new Insets(0, 0, 0, 0), 100, 0));
  }


void updateInterpretation(){      /* we're using the clock incrementer to check this every sec, we cannot
                                  easily listen for it as they might be changing browsers  */


  String outputStr = fShapePanel.getSemantics().interpretationToString();

  if (!fInterpretation.equals(outputStr)){
    fInterpretation=outputStr;
    fInterpretationText.setText("Current Interpretation: " +
                                strCR+
                                fInterpretation);
    aScrollPane2.validate();

  }

}


  void respond(boolean correct){
    fTotal += 1;
    if (correct)
      fCorrect += 1;

    String feedbackStr="";

    if (fMaxAttempts==-1)
      feedbackStr="You have " + fCorrect + " right out of " + fTotal +
                      " in "+ fElapsed  + " secs.";
    else
      feedbackStr="You have " + fCorrect + " right out of " + fTotal +
                      " in "+ fElapsed  + " secs."
                      +"[Attempt " +fMaxAttempts
                +", times out in: "+fMaxTime+" secs.]";



    feedback.setText(feedbackStr);

    if(!correct){
      showAnswer();

    }

}

  public void run(){

    fElapsed=0;
    fKeepRunning=true;
    ask();
  }



public void setMaxAttempts(int max){
  if (max>0)
    fMaxAttempts=max;
}

public void setMaxTime(long max){
  if (max>0)
    fMaxTime=max;
}

public void setUseQuantifiers(boolean useThem){
  fUseQuantifiers=useThem;

}

void produceRandomFormulae(){
	  int maxConnectives=5;

	    boolean noPropositions=true;
	    boolean unaryPredsOnly=true;

	    boolean noQuantifiers=!fUseQuantifiers;
	    boolean constantsOnly=false;

	    String preferredVariable="";

	   fRandom=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	                                            false,
	                                            noPropositions,
	                                            unaryPredsOnly,
	                                            noQuantifiers,
	                                            constantsOnly,
	                                            preferredVariable);

	   //and we don't want any free variables just yet

	   while ((fRandom.numConnectives()<3)||fParser.firstFreeVar(fRandom)!=chBlank)
	     fRandom=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	                                              false,
	                                              noPropositions,
	                                              unaryPredsOnly,
	                                              noQuantifiers,
	                                              constantsOnly,
	                                              preferredVariable);    //these need to be of similar difficulty
	                                                                              // say between 3 and 5 connectives
	  fRandom2=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	false,
	                                                                                                                          noPropositions,
	                                                                                                                          unaryPredsOnly,
	                                                                                                                          noQuantifiers,
	                                                                                                                          constantsOnly,
	                                                                                                                          preferredVariable);

	                                                                                 //and we don't want any free variables just yet

while ((fRandom2.numConnectives()<3)||fParser.firstFreeVar(fRandom2)!=chBlank)
	                                                                                   fRandom2=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	                                                                                                                            false,
	                                                                                                                            noPropositions,
	                                                                                                                            unaryPredsOnly,
	                                                                                                                            noQuantifiers,
	                                                                                                                            constantsOnly,
	                                                                                                                            preferredVariable);    //these need to be of similar difficulty
	                                                                              // say between 3 and 5 connectives
	fRandom3=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	                                            false,
	                                            noPropositions,
	                                            unaryPredsOnly,
	                                            noQuantifiers,
	                                            constantsOnly,
	                                            preferredVariable);

	   //and we don't want any free variables just yet

	   while ((fRandom3.numConnectives()<3)||fParser.firstFreeVar(fRandom3)!=chBlank)
	     fRandom3=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	                                              false,
	                                              noPropositions,
	                                              unaryPredsOnly,
	                                              noQuantifiers,
	                                              constantsOnly,
	                                              preferredVariable);    //these need to be of similar difficulty
	                                                                              // say between 3 and 5 connectives


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
       return;
       }

    fTimeIncrementer.start();

    fAnswered=false;
               //not using at the moment

    produceRandomFormulae();

 /*   int maxConnectives=5;

    boolean noPropositions=true;
    boolean unaryPredsOnly=true;

    boolean noQuantifiers=!fUseQuantifiers;
    boolean constantsOnly=false;

    String preferredVariable="";

   fRandom=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
                                            false,
                                            noPropositions,
                                            unaryPredsOnly,
                                            noQuantifiers,
                                            constantsOnly,
                                            preferredVariable);

   //and we don't want any free variables just yet

   while ((fRandom.numConnectives()<3)||fRandom.firstFreeVar()!=chBlank)
     fRandom=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
                                              false,
                                              noPropositions,
                                              unaryPredsOnly,
                                              noQuantifiers,
                                              constantsOnly,
                                              preferredVariable);    //these need to be of similar difficulty
                                                                              // say between 3 and 5 connectives
*/
   fInterpretationText.setForeground(Color.black);

   fInterpretation="";   // we may be displaying a correction from previous round. This resets.

   fInterpretationList = TTestNode.decidableFormulaSatisfiable(fParser,satisfiableTestFormula());

   if (fInterpretationList !=null)
     if (TParser.freeInterpretFreeVariables(fInterpretationList))  // just converts free variables eg x, y to constants
       ;
 //String randomStr = fParser.writeFormulaToString(fRandom);

 remove(jLabel2);

 jLabel2=new JLabel(produceLabel2(),JLabel.CENTER);

/* we put the question formula in as a label at 0,2 *******/

add(jLabel2, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.10
            , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(3, 10, 0, 2), 35, 0));

 fContainer.setVisible(true);

 setVisible(false);
 setVisible(true);
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
      fInterpretationText.setForeground(Color.red);


      {Thread worker = new Thread(){
          public void run() {
            try {
                Thread.sleep(15000);               //give them time to see the answer
              } catch (Exception ex) {}
              // come back later from event dispatch thread
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                ask();

                if (fKeepRunning){
                  submitButton.setEnabled(true);
                  notButton.setBorder(fOldBorder); //
                  notButton.setEnabled(true);
                  fInterpretationText.setForeground(Color.black);
                  fTimeIncrementer.start();
                }
                Toolkit.getDefaultToolkit().beep();
              }
            });
            }
          };
           worker.start();  // new thread
          }

 /* out Oct 06     SwingUtilities.invokeLater(new Runnable() {
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
                fInterpretationText.setForeground(Color.black);
                fTimeIncrementer.start();
              }
              Toolkit.getDefaultToolkit().beep();
            }
          }
          catch (Exception ex) {}
        }
      }); */
    }

}

  public void submitButton_actionPerformed(ActionEvent actionEvent) {

    fAnsweredSatisfiable=true;
    boolean correct= false;

    TFormula valuedFormula=fDeriverDocument.truthPresuppositionsHold(satisfiableTestFormula());

    if (valuedFormula!=null)
      correct=fDeriverDocument.valuedFormulaTrue(valuedFormula);

    // we'll deem them to be wrong if they aren't actually right

    standardAction(correct);

  }

  public void notButton_actionPerformed(ActionEvent actionEvent) {

    fAnsweredSatisfiable=false;
    boolean correct= notSatisfiableAnswerTrue();
    standardAction(correct);

  }



  /////////// Inner Classes ///////////////////////////////


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

     updateInterpretation();  //an experiment

  }
  }


  public boolean notSatisfiableAnswerTrue(){
  return
      !isSatisfiable();
    }

    private boolean isSatisfiable(){
    return
        fInterpretationList!=null;
  }


  public void showAnswer(){
  String outputStr="";


    if (fInterpretationList!=null){ //should not be null if this is called, and it will be a list like A,~B, etc

      outputStr = TTestNode.interpretationListToString(fInterpretationList);


    }
    else
      outputStr=notSatisfiableAnswer();

    fInterpretationText.setForeground(Color.red);
    fInterpretationText.setText(outputStr);   // the clock stops while feedback is given, so their drawing does not destroy this
    aScrollPane2.validate();

}



}

/////////////////////// JBuilder Classes ///////////////////

class TPredConsistent_submitButton_actionAdapter
    implements ActionListener {
  private TPredConsistent adaptee;
  TPredConsistent_submitButton_actionAdapter(TPredConsistent adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.submitButton_actionPerformed(actionEvent);
  }
}

class TPredConsistent_notButton_actionAdapter
    implements ActionListener {
  private TPredConsistent adaptee;
  TPredConsistent_notButton_actionAdapter(TPredConsistent adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.notButton_actionPerformed(actionEvent);
  }
}







