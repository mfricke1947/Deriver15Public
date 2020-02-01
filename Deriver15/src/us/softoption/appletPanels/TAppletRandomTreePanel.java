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

package us.softoption.appletPanels;

import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chTherefore;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import us.softoption.editor.TDeriverDocument;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;
import us.softoption.tree.TTreePanel;

// The caller needs to call run()   


public class TAppletRandomTreePanel extends JPanel{


/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
public static final int CLOSETYPE=1;
public static final int SATISFYTYPE=2;
public static final int VALIDTYPE=3;
	
	
	
TTreePanel fTreePanel=null;	 //host? initialized in constructor
TDeriverDocument fDeriverDocument= null;
TParser fParser =null;

int fPanelType=1;

boolean fPredicateLogic=true;


TFormula fRandom;
TFormula fRandom2;
TFormula fRandom3;
String fRandomTreeStr;
int fMaxBranches=8;
	
Dimension fPreferredSize=new Dimension(600,400);	
	
int fCorrect=0;
int fTotal=0;
int fMaxAttempts=3;//-1;

JLabel feedback =new JLabel("You have " +
		                    fCorrect+
		                    " right out of " +
		                    fTotal+
		                    " attempted. [You should attempt "+
		                    fMaxAttempts + ".]");

long fElapsed=0;
long fMaxTime=600;//-1;
TimeIncrementer fTimeIncrementer = new TimeIncrementer();

boolean fKeepRunning=false;

Border fOldOpenBorder;
Border fOldClosedBorder;
Container fContainer;


JTextArea fSetProof;
  
  JButton closedButton = new JButton();
  JButton openButton = new JButton();
  
  boolean fLastWrong=false;
  boolean fAnswered=false;
  boolean fAnsweredClosed=true;  /* there are two buttons, and they can answer 'satisfiable' (and be
  right or wrong) or answer 'not satisfiable' (and be right or wrong)*/

  String fClosedButtonLabel="Closed";
  String fClosedErrorLabel="Closed claimed:";
  String fClosedErrorMessage="Tree has an open branch.";
  String fCloseableLabel="Closable branch.";
  String fCloseableMessage="Tree has a branch which can be closed. Do that first.";
  String fOpenButtonLabel="Open & Complete";
  String fOpenNotClosedErrorLabel="Open claimed:"; 
  String fOpenNotClosedErrorMessage="Tree is actually closed";
  String fOpenNotCompleteErrorLabel="Open complete claimed:"; 
  String fOpenNotCompleteErrorMessage="An open branch is not complete.";
  
  String fIntroLabel ="<html>Either close the tree or extend it until a branch is complete and open."
      +"<br>(Aim: 100% right, 60 seconds each. "
      + "The clock stops while corrections are displayed.)"; 
  
  

  JButton fConfirm=new JButton(new AbstractAction("Write Confirmation Code"){
   public void actionPerformed(ActionEvent e){
    // fCodeEntry.setCode(fBrowser.getCurrentProofPanel().produceConfirmationMessage());
   ;}});



 /* This almost always wants to have its Deriver document because the undoable proof
   edits set the fDirty field of the document */ 
   

 /* Initialization */  
   
   public TAppletRandomTreePanel(TDeriverDocument aDocument,
		   						 TParser aParser,boolean predLogic,
		   						 int type){

	 
	 fDeriverDocument=aDocument;
	 fParser=aParser;
	 fPredicateLogic=predLogic;
	 
	 fPanelType=type;
	 
	 initializeLabels(type);
	 
	 fTreePanel=supplyTreePanel(fDeriverDocument);

   setLayout(new GridBagLayout());
   
  // JLabel aLabel=new JLabel(label,SwingConstants.LEFT);

   add(supplyLabel()/*aLabel*/,
		   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
				   GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

   //dec07	JScrollPane aScroller=new JScrollPane(fTreePanel);
// Dec 07, the tree panel has a scroller aScroller.setPreferredSize(fPreferredSize);
	//aScroller.setMinimumSize(fMinimumSize);
   
	//JScrollPane aScroller=new JScrollPane(fTreePanel);
   fTreePanel.setPreferredSize(fPreferredSize);
   fTreePanel.setMinimumSize(new Dimension(500,300));
   
 //add(aScroller);
 
 add(fTreePanel /*aScroller*/,new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
	       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));


 {
	    closedButton.setText(fClosedButtonLabel);
	    closedButton.setDefaultCapable(false);
	    closedButton.addActionListener(new TAppletRandomTreePanel_closedButton_actionAdapter(this));
	    fOldClosedBorder=closedButton.getBorder(); // so we can set it back after change
	    
	    openButton.setText(fOpenButtonLabel);
	    openButton.setDefaultCapable(false);
	    openButton.addActionListener(new TAppletRandomTreePanel_openButton_actionAdapter(this));
	    fOldOpenBorder=openButton.getBorder(); // so we can set it back after change
 
	    add(closedButton,new GridBagConstraints(0, 2, 1, 1, 0.2, 0.10
				, GridBagConstraints.EAST,
				GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 10, 0));	    

	    
	    add(openButton,new GridBagConstraints(1, 2, 1, 1, 0.2, 0.10
                , GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 10, 0));


	    add(feedback,new GridBagConstraints(0, 3, 2, 1, 0.0, 0.10
	            , GridBagConstraints.CENTER, GridBagConstraints.NONE,
	            new Insets(0, 0, 0, 0), 100, 0));

 
 }

 }

 
 JLabel supplyLabel(){
	JLabel aLabel =new JLabel(fIntroLabel,SwingConstants.LEFT);   
	   aLabel.setPreferredSize(new Dimension(440,65));
	   aLabel.setMinimumSize(new Dimension(440,65));
return
 aLabel;
 }
 
 void initializeLabels(int type){
	 
	 switch (type){
	 
	 case CLOSETYPE: 
		 break;       // initialized by default
	 case SATISFYTYPE:
		 {fClosedButtonLabel="Not satisfiable";
	 	 fClosedErrorLabel="Not satisfiable claimed:";
	 	 fClosedErrorMessage="Unproven, tree has an open branch.";
	 	 fOpenButtonLabel="Satisfiable";
	 	 fOpenNotClosedErrorLabel="Satisfiable claimed:"; 
	 	 fOpenNotClosedErrorMessage="Tree is actually closed";
	 	 fOpenNotCompleteErrorLabel="Satisfiable claimed:"; 
	 	 fOpenNotCompleteErrorMessage="Unproven, no open branch is complete.";
	 	 
	 	fIntroLabel = "<html>Either close the tree to prove that the formulas are not satisfiable "
	 		  +"<br>or extend it until a branch is complete and open (to show that the"
	 	      +"<br>formulas are satisfiable)." ;
	 	break;}
		 
	 case VALIDTYPE:
	 {fClosedButtonLabel="Valid";
 	 fClosedErrorLabel="Valid claimed:";
 	 fClosedErrorMessage="Unproven, tree has an open branch.";
 	 fOpenButtonLabel="Invalid";
 	 fOpenNotClosedErrorLabel="Invalid claimed:"; 
 	 fOpenNotClosedErrorMessage="Tree is actually closed (argument valid).";
 	 fOpenNotCompleteErrorLabel="Invalid claimed:"; 
 	 fOpenNotCompleteErrorMessage="Unproven, no open branch is complete.";
 	 
 	fIntroLabel = "<html>The premises (if any) and the negation of the conclusion are the"
 		 + "<br>root formulas. Close the tree to prove that the argument is valid"
 		  +"<br>or extend it until a branch is complete and open (to show that the"
 	      +"<br>argument is invalid)." ;
 	break;}	 

	 }
 }
 
 
   /* End of Initialization */  
 
 public void setMaxTime(int time){
	 fMaxTime=time;
 }
 
 public void setMaxBranches(int width){
	 fMaxBranches=width;
 }
 
 public void setMaxAttempts(int attempts){
	 fMaxAttempts=attempts;
	 
	 feedback.setText("You have " +
             fCorrect+
             " right out of " +
             fTotal+
             " attempted. [You should attempt "+
             fMaxAttempts + ".]");
 }
 
 
 String supplyTreeStr(){

if (fPredicateLogic){ 
	 
if (fPanelType==VALIDTYPE){
    String [] possibles=us.softoption.games.TRandomProof.randomAnyAnyLevel(1);

    if (possibles!=null&&possibles.length>0)
       return
       possibles[0];
}

// if we are testing for valid, we'll use predefined valid
 
if (fRandomTreeStr!=null&&
    !fRandomTreeStr.equals(""))
	return
	fRandomTreeStr;
	
	else{
	    int maxConnectives=5;
	    boolean noPropositions=true;
	    boolean noAtomic=true;
	    boolean unaryPredsOnly=true;

	    boolean noQuantifiers=true;
	    boolean constantsOnly=true;
	    String preferredVariable="";

	     fRandom=us.softoption.games.TRandomFormula.randomPredFormula(
	    		    maxConnectives,
	    		    noAtomic,
                 	noPropositions,
                 	unaryPredsOnly,
                 	!noQuantifiers,
                 	!constantsOnly,
                 	preferredVariable); 
	     fRandom2=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	    		    noAtomic,
                 	noPropositions,
                 	unaryPredsOnly,
                 	!noQuantifiers,
                 	!constantsOnly,
                 	preferredVariable);
	     fRandom3=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	    		    noAtomic,
                 	noPropositions,
                 	unaryPredsOnly,
                 	!noQuantifiers,
                 	!constantsOnly,
                 	preferredVariable);
	     
	     System.out.println("Show");

	     while ( ((fParser.firstFreeVar(fRandom)!=chBlank)||(fParser.firstFreeVar(fRandom2)!=chBlank)||(fParser.firstFreeVar(fRandom3)!=chBlank))|| //no free variables
	    	   (((fRandom.numConnectives()+ fRandom2.numConnectives()+fRandom3.numConnectives())<5)||
	           ((fRandom.numConnectives()+ fRandom2.numConnectives()+fRandom3.numConnectives())>8))||
	          (us.softoption.games.TRandomFormula.treeBranching(fRandom)* 
	          us.softoption.games.TRandomFormula.treeBranching(fRandom2)* 
	          us.softoption.games.TRandomFormula.treeBranching(fRandom3)>fMaxBranches)){
	        fRandom=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	    		    noAtomic,
                 	noPropositions,
                 	unaryPredsOnly,
                 	!noQuantifiers,
                 	!constantsOnly,
                 	preferredVariable);
	       fRandom2=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	    		    noAtomic,
                 	noPropositions,
                 	unaryPredsOnly,
                 	!noQuantifiers,
                 	!constantsOnly,
                 	preferredVariable);
	       fRandom3=us.softoption.games.TRandomFormula.randomPredFormula(maxConnectives,
	    		    noAtomic,
                 	noPropositions,
                 	unaryPredsOnly,
                 	!noQuantifiers,
                 	!constantsOnly,
                 	preferredVariable);
	     }  //we'll allow atomic, but overall there must between 5-9 connectives	                                                                               // say between 3 and 5 connectives
return
fParser.writeFormulaToString(fRandom)+
","+
fParser.writeFormulaToString(fRandom2)+
chTherefore +
fParser.writeFormulaToString(fRandom3);
		

	}
}
else{ 
	 
	if (fPanelType==VALIDTYPE){
	    String [] possibles=us.softoption.games.TRandomProof.randomTwelveLineProp(1);

	    if (possibles!=null&&possibles.length>0)
	       return
	       possibles[0];
	}

//	 if we are testing for valid, we'll use predefined valid
	 
	if (fRandomTreeStr!=null&&
	    !fRandomTreeStr.equals(""))
		return
		fRandomTreeStr;
		
		else{
		    int maxConnectives=5;

		     fRandom=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);
		     fRandom2=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);
		     fRandom3=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);

		     while ((((fRandom.numConnectives()+ fRandom2.numConnectives()+fRandom3.numConnectives())<5)||
		          ((fRandom.numConnectives()+ fRandom2.numConnectives()+fRandom3.numConnectives())>8))||
		          (us.softoption.games.TRandomFormula.treeBranching(fRandom)* 
		    	   us.softoption.games.TRandomFormula.treeBranching(fRandom2)* 
		    	   us.softoption.games.TRandomFormula.treeBranching(fRandom3)>fMaxBranches)){
		        fRandom=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);
		       fRandom2=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);
		       fRandom3=us.softoption.games.TRandomFormula.randomPropFormula(maxConnectives,false);
		     }  //we'll allow atomic, but overall there must between 5-9 connectives	                                                                               // say between 3 and 5 connectives
	return
	fParser.writeFormulaToString(fRandom)+
	","+
	fParser.writeFormulaToString(fRandom2)+
	chTherefore +
	fParser.writeFormulaToString(fRandom3);
			

		}
	}
		
		
 }
 
 TTreePanel supplyTreePanel(TDeriverDocument aDocument){  //for subclass override
	 return
	 new TTreePanel(aDocument);	 
 }


 public String getSetProof(){
   return
       TUtilities.logicFilter(fSetProof.getText());
 }
 
 
 public void startTree(String treeStr){
	fTreePanel.startTree(TUtilities.logicFilter(treeStr));
}

 /*
 public String produceAProof(int type){
   String returnStr="";
   String [] possibles;

   switch (type) {
     case SimpleAndIENegEOrI:

       possibles=us.softoption.games.TRandomProof.randomSimpleNegEandEIorI(1);

       if (possibles!=null&&possibles.length>0)
          returnStr=possibles[0];
       break;
     case SimpleImplicEEquivE:

possibles=us.softoption.games.TRandomProof.randomSimpleImplicEEquivE (1);

if (possibles!=null&&possibles.length>0)
   returnStr=possibles[0];
break;

     case AndIENegEOrI:

        possibles=us.softoption.games.TRandomProof.randomNegEandEIorI(1);

        if (possibles!=null&&possibles.length>0)
           returnStr=possibles[0];
        break;

    case ImplicEEquivE:

        possibles=us.softoption.games.TRandomProof.randomImplicEEquivE(1);

        if (possibles!=null&&possibles.length>0)
           returnStr=possibles[0];
        break;
      case ImplicI:

          possibles=us.softoption.games.TRandomProof.randomImplicI(1);

          if (possibles!=null&&possibles.length>0)
             returnStr=possibles[0];
          break;
        case SimpleNegI:

              possibles=us.softoption.games.TRandomProof.randomSimpleNegI(1);

              if (possibles!=null&&possibles.length>0)
                 returnStr=possibles[0];
              break;
            case NegI:

                  possibles=us.softoption.games.TRandomProof.randomNegI(1);

                  if (possibles!=null&&possibles.length>0)
                     returnStr=possibles[0];
                  break;

        case OrEEquivI:

                    possibles=us.softoption.games.TRandomProof.randomOrEEquivI(1);

                    if (possibles!=null&&possibles.length>0)
                       returnStr=possibles[0];
        break;

      case TwelveLineProp:

                  possibles=us.softoption.games.TRandomProof.randomTwelveLineProp(1);

                  if (possibles!=null&&possibles.length>0)
                     returnStr=possibles[0];
      break;

    case PredNoQuant:

                possibles=us.softoption.games.TRandomProof.randomPredNoQuant(1);

                if (possibles!=null&&possibles.length>0)
                   returnStr=possibles[0];
    break;

  case SimpleUI:

              possibles=us.softoption.games.TRandomProof.randomSimpleUI(1);

              if (possibles!=null&&possibles.length>0)
                 returnStr=possibles[0];
    break;

  case SimpleUG:

                possibles=us.softoption.games.TRandomProof.randomSimpleUG(1);

                if (possibles!=null&&possibles.length>0)
                   returnStr=possibles[0];
    break;

  case SimpleEG:

                possibles=us.softoption.games.TRandomProof.randomSimpleEG(1);

                if (possibles!=null&&possibles.length>0)
                   returnStr=possibles[0];
    break;

  case SimpleEI:

                   possibles=us.softoption.games.TRandomProof.randomSimpleEI(1);

                   if (possibles!=null&&possibles.length>0)
                      returnStr=possibles[0];
     break;

   case TenLinePred:

                      possibles=us.softoption.games.TRandomProof.randomTenLinePred(1);

                      if (possibles!=null&&possibles.length>0)
                         returnStr=possibles[0];
     break;

   case AnyAnyLevel:

       possibles=us.softoption.games.TRandomProof.randomAnyAnyLevel(1);

       if (possibles!=null&&possibles.length>0)
          returnStr=possibles[0];
break;

   case Identity:

       possibles=us.softoption.games.TRandomProof.randomIdentity(1);

       if (possibles!=null&&possibles.length>0)
          returnStr=possibles[0];
break;

   }

   return
       returnStr;
 }
 
 */

 public String produceConfirmationMessage(){
   return
   "Hello";
  // fTreePanel.produceConfirmationMessage();// fCodeEntry.getCode();
 }

 public String produceProofStr(){ /*
	if (fTreePanel.finishedAndNoAutomation())
	   return
	   fTreePanel.getProofStr();
	else  */
		return
		"Cannot confirm yet.";
	 }
 
 
 /**************** LOOPS *******************/
 

 
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

	          closedButton.setEnabled(false);
	          openButton.setEnabled(false);

//	        if (fTableModel!=null)
//	           fTableModel.setTogglingEnabled(false);
	       return;
	       }

	    fTimeIncrementer.start();

	    fAnswered=false;
	    
	    startTree(supplyTreeStr());
	    
	    fTreePanel.setBorder(BorderFactory.createLineBorder(Color.black));

/*	   remove(fTreePanel);  //old one

	   fTreePanel=supplyTreePanel(fDeriverDocument);  //new one
	   fTreePanel.setPreferredSize(fPreferredSize);
	   fTreePanel.setMinimumSize(new Dimension(500,300));
	   
	   add(fTreePanel,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));


	// jsp.setBorder(BorderFactory.createLineBorder(Color.black));

	   setVisible(false);
	   setVisible(true);  */

	  }
 
 
 
 void respond(boolean correct){
	    fTotal += 1;
	    if (correct)
	      fCorrect += 1;

	    feedback.setText(/*"You have " + */fCorrect + " right out of " + fTotal +
	                      " attempted, in "+ fElapsed  + " secs. [Time limit: "+
	                      fMaxTime+ " secs."+
	                      " Max. attempts: "+
	                      fMaxAttempts+ ".]");

	    if(!correct){

	      if (fAnsweredClosed){ //two possibilities, closed and its not, or open and its not
	      
	    	  fTreePanel.bugAlert(fClosedErrorLabel, fClosedErrorMessage);  
	//    	  fOldClosedBorder=closedButton.getBorder();                                // we will set it back
	    	  closedButton.setBorder(BorderFactory.createLineBorder(Color.red));  //answered satisfiable when wasn't
  
	    	  fTreePanel.selectOpenBranch();
	    	  
	    	  /* if (fTreePanel.isTreeClosed())
	          {;}//fTableModel.showAnswer();
	        else{
	        	fOldOpenBorder=openButton.getBorder();                                // we will set it back
	        	openButton.setBorder(BorderFactory.createLineBorder(Color.red));  //answered satisfiable when wasn't
	        }
	      */} 
	      else{ //answered open complete branch when no branch is
	     
	    	  if (fTreePanel.isTreeClosed())
	    		  fTreePanel.bugAlert(fOpenNotClosedErrorLabel, fOpenNotClosedErrorMessage);  
	    	  else
	    		  fTreePanel.bugAlert(fOpenNotCompleteErrorLabel, fOpenNotCompleteErrorMessage);  
	    	  
	    	  // fTableModel.showAnswer();
	      }
	    }

	}
 
 public void run(){

	    fElapsed=0;
	    fKeepRunning=true;

	    ask();  // starts time incrementer
	  }
 
 void standardAction(boolean correct){

	  respond(correct);

	    if (correct)                               //go straight on
	      ask();
	    else{
	      Toolkit.getDefaultToolkit().beep();
	      fTimeIncrementer.stop(); //to let them look at the right answer
//	      fOldClosedBorder=closedButton.getBorder();
	      closedButton.setEnabled(false);
//	      fOldOpenBorder=openButton.getBorder();
	      openButton.setEnabled(false);
//	      fTableModel.setTogglingEnabled(false);  // no need to reset this true, becuase the model changes
//	      jsp.setBorder(BorderFactory.createLineBorder(Color.red));
	      fTreePanel.setBorder(BorderFactory.createLineBorder(Color.red));

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
	                	    closedButton.setBorder(fOldClosedBorder);
	                	    closedButton.setDefaultCapable(false);
	                        closedButton.setEnabled(true);
	                        openButton.setBorder(fOldOpenBorder); //
	                        openButton.setDefaultCapable(false);
	                        openButton.setEnabled(true);
	                        fTimeIncrementer.start();
	                      }
	                      Toolkit.getDefaultToolkit().beep();
	                }
	              });
	                }
	        };
	        worker.start();              // new thread
	      }
	    }  
	}
 
 /**************************************/
 
 
 
 public void closedButton_actionPerformed(ActionEvent actionEvent) {
 
	 
   fAnsweredClosed=true;
   boolean correct=false;
   if (fTreePanel!=null){
	   correct=fTreePanel.isTreeClosed();
   }
   
   if (correct){
	   Toolkit.getDefaultToolkit().beep();
	   Toolkit.getDefaultToolkit().beep();
	   
   }
   
   standardAction(correct);
	   
//	   fTableModel.satisfiableAnswerTrue();
//	    standardAction(correct);

	  }

	  public void openButton_actionPerformed(ActionEvent actionEvent) {

   //bale out if they have a closable branch which they haven't actually closed
		  
	if (fTreePanel.isABranchOpenAndClosable()){
		fTreePanel.bugAlert(fCloseableLabel,fCloseableMessage);
	
	    return;   //bale
	}
	
		  
   fAnsweredClosed=false;
   boolean correct=fTreePanel.isABranchOpenAndComplete();
   standardAction(correct);

	  }

	  public int getTotal(){
		   return
		       fTotal;
		 }

		 public int getCorrect(){
		   return
		       fCorrect;
		 }

	  /////////// Inner Classes ///////////////////////////////
 
	public void stopClock(){
		fTimeIncrementer.stop();
	}
	
	public void startClock(){
		fTimeIncrementer.start();
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
 
 
 
}  // end of main class


/////////////////////// JBuilder Classes ///////////////////

class TAppletRandomTreePanel_closedButton_actionAdapter
    implements ActionListener {
  private TAppletRandomTreePanel adaptee;
  TAppletRandomTreePanel_closedButton_actionAdapter(TAppletRandomTreePanel adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.closedButton_actionPerformed(actionEvent);
  }
}

class TAppletRandomTreePanel_openButton_actionAdapter
    implements ActionListener {
  private TAppletRandomTreePanel adaptee;
  TAppletRandomTreePanel_openButton_actionAdapter(TAppletRandomTreePanel adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.openButton_actionPerformed(actionEvent);
  }
}


