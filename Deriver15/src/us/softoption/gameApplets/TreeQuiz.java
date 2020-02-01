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

package us.softoption.gameApplets;


import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.softoption.appletPanels.TAppletRandomTreePanel;
import us.softoption.editor.TBergmannDocument;
import us.softoption.editor.TCopiDocument;
import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.THausmanDocument;
import us.softoption.editor.THowsonDocument;
import us.softoption.editor.TPreferences;
import us.softoption.editor.TPriestDocument;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THowsonParser;
import us.softoption.parser.TParser;
import us.softoption.parser.TPriestParser;



public class TreeQuiz extends JApplet{

	private static final long serialVersionUID = 1L;


  TParser fParser =new TParser();
  
  TDeriverDocument fDeriverDocument= new TDeriverDocument();
  
  boolean fPredicateLogic=true;
  
  /*********** Panels ******************/
  
  TAppletRandomTreePanel fCloseTree;
  boolean fCloseRunning=false;
  int fCloseAttempts=5;
  int fCloseTime=600;
  
  TAppletRandomTreePanel fSatisTree;
  boolean fSatisRunning=false;
  int fSatisAttempts=5;
  int fSatisTime=600;
  
  TAppletRandomTreePanel fValidTree;
  boolean fValidRunning=false;
  int fValidAttempts=5;
  int fValidTime=600;
  
  int fMaxBranches=8;

/****************** End of Panels ****************/

  JTabbedPane fTabs= new JTabbedPane();
  
  JLabel fWelcome = new JLabel("<html>Work through the Tabs to Finish.<br>"
		  + "Tree1: <br>"
		  + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over.");

  JLabel fFeedback=new JLabel("");
  JTextArea fCode=new JTextArea("");
  JScrollPane codeScroller;
  JPanel fFinishPanel=new JPanel(new GridBagLayout());
  
  String fConfCode="";

  NameEntry fNameEntry=new NameEntry();
  
  Dimension fPreferredSize= new Dimension(600,600); /*(540,540);*/

	public void init(){
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
		
		 int year=cal.get(Calendar.YEAR);
			
		 if (year>TConstants.APPLET_EXPIRY){
	          JLabel label = new JLabel("The code for this applet expired in " +TConstants.APPLET_EXPIRY +" .");
           contentPane.add(label);
		}
		else{			
			createGUI(contentPane);		
		}	
	}

public void paint(Graphics g) {   // can see background properly in new Firefox

    	
    	super.paint(g);
 
    	g.drawRect(0, 0, 
     		   getSize().width - 1,
     		   getSize().height - 1);  	
    	

        }

	private int [] readParamProofTypes(){
   int i=7;
   int valueInt=0;
   int [] types=null;
   String param= "treeType"+i;		
   String value= null;
	   
	while ((value==null)&&i>-1){
		param= "treeType"+i;
		value= getParameter(param);
		if (value==null)
			i--;           //if not we'll stay with the index we have found
	}
	   
	if (i<0)
       return
		null;
	   
    types = new int[i+1];
 
   while (value!=null&&i>-1){
	   try {
		    valueInt = Integer.parseInt(value);
		    } catch (NumberFormatException e) {
		    valueInt=0;    //Use default
		    }
	       types[i]=valueInt;
	   
		   i--;
		   param= "treeType"+i;
		   value= getParameter(param);
	   }
	   	   
	   return 
	   types;
}

	private void readParameters(){
		
		   {String parser= getParameter("parser");
		   if (parser!=null&&parser.equals("bergmann")){
			   TPreferences.fParser="bergmann [copi gentzen hausman]";
		   }
		 
		   if (parser!=null&&parser.equals("copi")){
			   TPreferences.fParser="copi [bergmann gentzen hausman howson priest]";
		   }
		   
		   if (parser!=null&&parser.equals("hausman")){
			   TPreferences.fParser="hausman [bergmann copi gentzen howson priest]";
		   }
		   
		   if (parser!=null&&parser.equals("howson")){
			   TPreferences.fParser="howson [bergmann copi gentzen hausman priest]";
		   }
		   
		   if (parser!=null&&parser.equals("priest")){
			   TPreferences.fParser="priest [bergmann copi gentzen hausman howson]";
		   }
		   
		   }		
		
		int positiveValue=0;   //NEED TO DO THIS THROUGHOUT

		   {String connAttempts= getParameter("closeAttempts"); 
		   if (connAttempts!=null){
			    try {
			    	positiveValue= Integer.parseInt(connAttempts);
			    	if (positiveValue>-1)
			    	   fCloseAttempts = positiveValue;
			    } catch (NumberFormatException e) {
			        //Use default
			    }
		   }
		   }
		   
		   positiveValue=0;

		   String connTime= getParameter("closeTime"); 
		   if (connTime!=null){
			    try {
			    	positiveValue = Integer.parseInt(connTime);
			    	if (positiveValue>-1)
			    		fCloseTime = positiveValue;
			    } catch (NumberFormatException e) {
			        //Use default
			    }
		   }
		   
		   positiveValue=0;
		
		   {String satisAttempts= getParameter("satisAttempts"); 
		   if (satisAttempts!=null){
			    try {
			    	positiveValue= Integer.parseInt(satisAttempts);
			    	if (positiveValue>-1)
			    	   fSatisAttempts = positiveValue;
			    } catch (NumberFormatException e) {
			        //Use default
			    }
		   }
		   }
		   
		   positiveValue=0;

		   String satisTime= getParameter("satisTime"); 
		   if (satisTime!=null){
			    try {
			    	positiveValue = Integer.parseInt(satisTime);
			    	if (positiveValue>-1)
			    		fSatisTime = positiveValue;
			    } catch (NumberFormatException e) {
			        //Use default
			    }
		   }
		   
		   positiveValue=0;
		
		   {String validAttempts= getParameter("validAttempts"); 
		   if (validAttempts!=null){
			    try {
			    	positiveValue= Integer.parseInt(validAttempts);
			    	if (positiveValue>-1)
			    	   fValidAttempts = positiveValue;
			    } catch (NumberFormatException e) {
			        //Use default
			    }
		   }
		   }
		   
		   positiveValue=0;

		   String validTime= getParameter("validTime"); 
		   if (validTime!=null){
			    try {
			    	positiveValue = Integer.parseInt(validTime);
			    	if (positiveValue>-1)
			    		fValidTime = positiveValue;
			    } catch (NumberFormatException e) {
			        //Use default
			    }
		   }
		   
		   positiveValue=0;

		   String maxBranches= getParameter("maxBranches"); 
		   if (maxBranches!=null){
			    try {
			    	positiveValue = Integer.parseInt(maxBranches);
			    	if (positiveValue>-1)
			    		fMaxBranches = positiveValue;
			    } catch (NumberFormatException e) {
			        //Use default
			    }
		   }
		
		   {String level= getParameter("propLevel");
		   if (level!=null&&level.equals("true")){
			   fPredicateLogic=false;
		   }
		   }
		   
		   {String identity= getParameter("identity"); 
		   if ((identity!=null)&&identity.equals("true"))
	          TPreferences.fIdentity= true;
		   else
			  TPreferences.fIdentity= false;

		   }
		   
		   
	   {String welcome= getParameter("welcome"); 
	   if ((welcome!=null)){	  
           fWelcome= new JLabel(welcome);  // general passed in
	   }  
   	   
   	  
	   }  

}

	
	private void createGUI(Container contentPane){
	
	readParameters();
	
	/******** parser *******/ 
	
	   if (TPreferences.fParser.charAt(0)=='b'){//.equals("bergmann [copi gentzen hausman]"))
		   fParser =new TBergmannParser();
		   fDeriverDocument=new TBergmannDocument();	//this gives Bergmann random proofs	   
	   }
	   
	   if (TPreferences.fParser.charAt(0)=='c'){//.equals("bergmann [copi gentzen hausman]"))
		   fParser =new TCopiParser();
		   fDeriverDocument=new TCopiDocument();	//this gives Bergmann random proofs	   
	   }
	   
	   if (TPreferences.fParser.charAt(0)=='h'&&
			   TPreferences.fParser.charAt(1)=='a'){//.equals("copi [gentzen hausman]"))
		   fParser =new THausmanParser();
		   fDeriverDocument=new THausmanDocument();	//this gives Bergmann random proofs	   
	   }
	   
	   if (TPreferences.fParser.charAt(0)=='h'&&
			   TPreferences.fParser.charAt(1)=='o'){//.equals("copi [gentzen hausman]"))
		   fParser =new THowsonParser();
		   fDeriverDocument=new THowsonDocument();	//this gives Bergmann random proofs	   
	   }
	   
	   if (TPreferences.fParser.charAt(0)=='p'&&
			   TPreferences.fParser.charAt(1)=='r'){//.equals("copi [gentzen hausman]"))
		   fParser =new TPriestParser();
		   fDeriverDocument=new TPriestDocument();	//this gives Bergmann random proofs	   
	   }
	   
	
	   /*********** panels **************/
	   
	  fCloseTree =new TAppletRandomTreePanel(fDeriverDocument,fParser, 
			  fPredicateLogic,TAppletRandomTreePanel.CLOSETYPE);
	  fCloseTree.setMaxAttempts(fCloseAttempts);
	  fCloseTree.setMaxTime(fCloseTime);
	  fCloseTree.setMaxBranches(fMaxBranches);
	  fCloseTree.startTree("");
	  
	   String closeMessage="";
	   if (fCloseAttempts>0)
	     closeMessage="Close: attempt "+fCloseAttempts
	     +". Times out in "+fCloseTime+" seconds.";
	
	   
		  fSatisTree =new TAppletRandomTreePanel(fDeriverDocument,fParser, 
				  fPredicateLogic,TAppletRandomTreePanel.SATISFYTYPE);
		  fSatisTree.setMaxAttempts(fSatisAttempts);
		  fSatisTree.setMaxTime(fSatisTime);
		  fSatisTree.setMaxBranches(fMaxBranches);
		  fSatisTree.startTree("");	  
		   String satisMessage="";
		   if (fSatisAttempts>0)
		     satisMessage="Satisfiable: attempt "+fSatisAttempts
		     +". Times out in "+fSatisTime+" seconds.";
	  
			  fValidTree =new TAppletRandomTreePanel(fDeriverDocument,fParser, 
					  fPredicateLogic,TAppletRandomTreePanel.VALIDTYPE);
			  fValidTree.setMaxAttempts(fValidAttempts);
			  fValidTree.setMaxTime(fValidTime);
			  fValidTree.setMaxBranches(fMaxBranches);
			  fValidTree.startTree("");	  
			   String validMessage="";
			   if (fValidAttempts>0)
			     validMessage="Validity: attempt "+fValidAttempts
			     +". Times out in "+fValidTime+" seconds.";

	


	setSize(fPreferredSize);
	

	
	fFinishPanel.add(fFeedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

	   JPanel intro=new JPanel(new BorderLayout());
	  // fNameEntry.fName.setText(TDeriverApplication.getUser());

	   intro.add(fNameEntry,BorderLayout.NORTH);
	  
	  
	  fWelcome = new JLabel("<html>Work through the Tabs to Finish.<br>"
			  + "<br>"
			  + closeMessage +"<br>"
			  + satisMessage +"<br>"
			  + validMessage +"<br>"
			  +"<br>"
			  + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over.");
	  
	  
	  
	  
	   intro.add (fWelcome,BorderLayout.CENTER);
	   
	   //createFinishPanel();
	   
	   initializeFinishPanel();

	//  fTabs.add("Intro",intro);
	
	  fTabs.add("Intro",intro);
	  
	  if (fCloseAttempts>0)
		  fTabs.add("Close",fCloseTree);
	   if (fSatisAttempts>0)
		   fTabs.add("Satis",fSatisTree);
	   if (fValidAttempts>0)
		   fTabs.add("Valid",fValidTree);
	  
	  fTabs.add("Finish",fFinishPanel);  //scroller in case it gets too big


	fTabs.addChangeListener(tabsChangeListener());
			
 contentPane.add(fTabs);
 
 this.setVisible(false);
 this.setVisible(true);
}

	/*
TAppletRandomTreePanel	supplyRandomPanel(TDeriverDocument aDocument,TParser aParser,int type){

		return
		
			new TAppletRandomTreePanel(aDocument,     //try another one
                aParser,
                type);
		
	}  */


	private ChangeListener tabsChangeListener(){
	return
	new ChangeListener(){
		public void stateChanged(ChangeEvent e){
			
			fCloseTree.stopClock();
			fSatisTree.stopClock();
			fValidTree.stopClock();

		  Component selected = fTabs.getSelectedComponent();

	      if (selected == fCloseTree) {
	          if (!fCloseRunning) {
	        		  fCloseTree.run();
	        		  fCloseRunning = true;
	          }
	          else
	        	  fCloseTree.startClock();
	        	  
	      }
		  
	      if (selected == fSatisTree) {
	          if (!fSatisRunning) {
	        		  fSatisTree.run();
	        		  fSatisRunning = true;
	          }
	          else
	        	  fSatisTree.startClock();
	      }
		  
	      if (selected == fValidTree) {
	          if (!fValidRunning) {
	        		  fValidTree.run();
	        		  fValidRunning = true;
	          }
	          else
	        	  fValidTree.startClock();
	      }
		  
		            if (selected == fFinishPanel) {
		            	updateFinishPanel();
		              
		            }
		          }
		        };
		      }


	private void initializeFinishPanel(){
    fFinishPanel=new JPanel(new GridBagLayout()/*new BorderLayout()*/);
    fCode=new JTextArea("");

   // codeScroller =new JScrollPane(fCode);

   // fFinishPanel.add(codeScroller, BorderLayout.CENTER);
    
    fCode.setEditable(true);
    fCode.setLineWrap(true);
    fCode.setWrapStyleWord(true);
    fCode.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
    
   // code.setPreferredSize(new Dimension (540,140));

   // fFinishPanel.add(code, BorderLayout.CENTER);
    
		JScrollPane aScroller=new JScrollPane(fCode);
	aScroller.setPreferredSize(new Dimension (480,160));
	aScroller.setMinimumSize(new Dimension (480,160));
    
    
    
	  fFinishPanel.add(aScroller,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));


 //   fFinishPanel.add(new JLabel("Be sure to submit the Confirmation Code."   //adding every update?
 //                                      +""), BorderLayout.SOUTH);

	fFinishPanel.add(new JLabel(
    "Be sure to submit the Confirmation Code."),
    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

  }



public String treeQuizConfirmation(String name,String compositeStr){
	DateFormat shortTime=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
    String time=shortTime.format(new Date());   
    
    return
    "["
        + TUtilities.generalEncode(
                name+ ", "
                + time+ ", "
                + compositeStr)
                                       + "]";
}


private void updateFinishPanel(){
	
	//fFinishPanel.remove(codeScroller);
	fFinishPanel.remove(fFeedback);

    String name = fNameEntry.fName.getText();

    if (name == null || name.equals("")) {
      fFeedback = new JLabel(
          "You need to enter a name on the Intro Page.");
   //   fFinishPanel.add(feedback, BorderLayout.NORTH);
    }
   else {
	   
	    int correct = (fCloseTree.getCorrect()
                  + fSatisTree.getCorrect()
                  + fValidTree.getCorrect()
                /*  + fInvalid.getCorrect() */
       );
	    int total = (fCloseTree.getTotal()
                + fSatisTree.getTotal()
                + fValidTree.getCorrect()
              /*  + fCons.getTotal()
                + fInvalid.getTotal() */
       );
	    int maxAttempts = (fCloseAttempts
                + fSatisAttempts
                + fValidAttempts
       );
	   
	      fFeedback = new JLabel(fNameEntry.fName.getText() +
                  ", you have "
                  + correct
                  + " right out of "
                  + total
                  + " attempted. [You should have attempted: "
	              + maxAttempts +".]");
 
    
//	fFinishPanel.add(feedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
//		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

	   
	   DateFormat shortTime=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
	      String time=shortTime.format(new Date());
	      
	      
	      fConfCode= "["
	          + TUtilities.urlEncode(TUtilities.xOrEncrypt(
	                  name+ ", "
	                  + time+ ", "
	                  + correct
	                  + " of "
	                  + total))
	                                         + "]";

	      fCode.setText(//strCR
	                                    // +
	          "Here is the number you got correct: " + correct

	                                     + strCR+ strCR
	                                     +
	          "Here is your Confirmation Code:"
	          + strCR+ strCR 
	          + fConfCode
	                                     + strCR +
	                                     strCR
	                                     + "To submit: (copy then) paste the number you got correct, and the Confirmation"
	                                     + strCR
	                                     + "Code (into the Quiz Attempt).   "
	                                     + strCR +
	                                     strCR
	                                  //   + " You should copy and paste the Confirmation Code, as it may contain unusual characters."
	          );


   }
	   
		fFinishPanel.add(fFeedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
	      
		fFinishPanel.setVisible(false);
		fFinishPanel.setVisible(true);



}  




public String getConfCode(){
	return
	fConfCode;
}



class NameEntry extends JPanel{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public JTextField fName= new JTextField(20);
  public NameEntry(){
    super (new BorderLayout());

    JLabel label = new JLabel("Name: ");

    add(label,BorderLayout.WEST);
    add(fName,BorderLayout.CENTER);

  }
}

}


