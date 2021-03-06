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

import us.softoption.appletPanels.TAppletBergmannRandomProofPanel;
import us.softoption.appletPanels.TAppletHausmanRandomProofPanel;
import us.softoption.appletPanels.TAppletRandomProofPanel;
import us.softoption.editor.TBergmannDocument;
import us.softoption.editor.TCopiDocument;
import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.THausmanDocument;
import us.softoption.editor.TPreferences;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.TParser;




public class ProofQuiz extends JApplet{

	private static final long serialVersionUID = 1L;


  TParser fParser =new TParser();
  
  TDeriverDocument fDeriverDocument= new TDeriverDocument();
  
  TAppletRandomProofPanel[] fProofs;

  int numProofs=0;
  
  int [] prooftypes={1};


  JTabbedPane fTabs= new JTabbedPane();
  
  // reprogram to read next from Javascript
  
  JLabel fWelcomeLabel=null; // prefer label we will read if passed in as a parameter, else default quiz3
  
  
  JTextArea fWelcome; // we will read if passed in as a parameter, else default quiz3
  
  JTextArea fWelcomeQuiz3= new JTextArea(strCR
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
  
  JTextArea fWelcomeQuiz3Bergmann= new JTextArea(strCR
          + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
          + strCR
          + strCR
          + "Proof1: a simple derivation using &I &E \u2228I ."
          + strCR
          + "Proof2: a simple derivation using &I &E \u2228I ."
          + strCR
          + "Proof3: a simple derivation using &I &E \u2228I ."
          + strCR
          + "Proof4: an intermediate derivation using &I &E \u2228I ."
          + strCR
          + "Proof5: an intermediate derivation using &I &E \u2228I  ."
          + strCR+ strCR
          + ""
          + strCR
          + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
          + strCR
  );
  
 
  
  JTextArea fWelcomeQuiz4= new JTextArea(strCR
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
  
  
  JTextArea fWelcomeQuiz5= new JTextArea(strCR
          + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
          + strCR
          + strCR
          + "Proof1: a simple derivation using \u2283 I ."
          + strCR
          + "Proof2: a simple derivation using \u2283 I  ."
          + strCR
          + "Proof3: a simple derivation using \u2283 I  ."
          + strCR
          + "Proof4: a simple derivation using \u2283 I  ."
          + strCR
          + "Proof5: a simple derivation using \u2283 I   ."
          + strCR+ strCR
          + ""
          + strCR
          + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
          + strCR
  );
  
 JTextArea fWelcomeQuiz6=new JTextArea(strCR
          + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
          + strCR
          + strCR
          + "Proof1: a simple derivation using \u223C  I ."
          + strCR
          + "Proof2: a simple derivation using \u223C  I  ."
          + strCR
          + "Proof3: a simple derivation using \u223C  I  ."
          + strCR
          + "Proof4: a harder derivation using \u223C  I  ."
          + strCR
          + "Proof5: a harder derivation using \u223C  I   ."
          + strCR+ strCR
          + ""
          + strCR
          + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
          + strCR
  );
 
 JTextArea fWelcomeQuiz6Bergmann=new JTextArea(strCR
         + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
         + strCR
         + strCR
         + "Proof1: a simple derivation using \u223C  I or \u223C  E ."
         + strCR
         + "Proof2: a simple derivation using \u223C  I  or \u223C  E ."
         + strCR
         + "Proof3: a simple derivation using \u223C  I  or \u223C  E ."
         + strCR
         + "Proof4: a harder derivation using \u223C  I or \u223C  E ."
         + strCR
         + "Proof5: a harder derivation using \u223C  I or \u223C  E ."
         + strCR+ strCR
         + ""
         + strCR
         + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
         + strCR
 );
 
 
 //for midTerm we have 2 TAppletPanel.TwelveLineProp ie javascript code 6
 
 
JTextArea fWelcomeMidTerm= new JTextArea(strCR
	        + "Work through the Tabs to Finish. [These are intermediate level derivations about 10-12 lines long.]"
	        + strCR
	        + strCR
	        + "Proof1: a derivation which might use any of the propositional rules."
	        + strCR
	        + "Proof2: a derivation which might use any of the propositional rules."
	        + strCR+ strCR
	        + ""
	        + strCR
	        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
	        + strCR
	);

JTextArea fWelcomeQuiz8=new JTextArea(strCR
        + "Work through the Tabs to Finish. [Proof 1 is intermediate level, 2-5 are elementary.]"
        + strCR
        + strCR
        + "Proof1: a derivation in predicate calculus, without quantifiers."
        + strCR
        + "Proof2: a derivation using UI."
        + strCR
        + "Proof3: a derivation using UG."
        + strCR
        + "Proof4: a derivation using EG."
        + strCR
        + "Proof5: a derivation using EI."
        + strCR+ strCR
        + ""
        + strCR
        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
        + strCR
);

JTextArea fWelcomeQuiz8Bergmann=new JTextArea(strCR
        + "Work through the Tabs to Finish. [Proof 1 is intermediate level, 2-5 are elementary.]"
        + strCR
        + strCR
        + "Proof1: a derivation in predicate calculus, without quantifiers."
        + strCR
        + "Proof2: a derivation using UE."
        + strCR
        + "Proof3: a derivation using UI."
        + strCR
        + "Proof4: a derivation using EI."
        + strCR
        + "Proof5: a derivation using EE."
        + strCR+ strCR
        + ""
        + strCR
        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
        + strCR
);


JTextArea fWelcomeFinalQ7=new JTextArea(strCR
        + "Work through the Tabs to Finish. [These are 8-12 line derivations.]"
        + strCR
        + strCR
        + "For the Final you need to do TWO of these THREE."
        + strCR
        + strCR
        + "Proof1: a propositional derivation which might use any of the propositional rules."
        + strCR
        + "Proof2: a predicate derivation which might use any of the rules."
        + strCR
        + "Proof3: a predicate derivation which might use any of the rules."
        + strCR
        + strCR
        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
        + strCR
);

JTextArea fWelcomeBonusQuiz= new JTextArea(strCR
        + "Work through the Tabs to Finish. [You may use rewrite rules.]"
        + strCR
        + strCR
        + "Proof 1: a derivation of any type and difficulty."
        + strCR
        + "Proof 2: a derivation of any type and difficulty."
        + strCR
        + "Proof 3: a derivation of any type and difficulty."
        + strCR
        + "Proof 4: a derivation of any type and difficulty."
        + strCR
        + "Proof 5: a derivation of any type and difficulty."
        + strCR+ strCR
        + ""
        + strCR
        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
        + strCR
);

JTextArea fWelcomeIdentityBonusQuiz= new JTextArea(strCR
        + "Work through the Tabs to Finish."
        + strCR
        + strCR
        + "Proof 1: a derivation which may use Identity."
        + strCR
        + "Proof 2: a derivation which may use Identity."
        + strCR
        + "Proof 3: a derivation which may use Identity."
        + strCR
        + "Proof 4: a derivation which may use Identity."
        + strCR
        + "Proof 5: a derivation which may use Identity."
        + strCR+ strCR
        + ""
        + strCR
        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
        + strCR
);

  JLabel feedback=new JLabel("");
  JTextArea fCode=new JTextArea("");
  JScrollPane codeScroller;
  JPanel fFinishPanel=new JPanel(new GridBagLayout());
  
  String fConfCode="";

  NameEntry fNameEntry=new NameEntry();
  
  Dimension fPreferredSize= new Dimension (600,400);
  
  boolean fForTest=false;  //removes theorem proving
  boolean fRemoveAdvanced=true;  //removes advanced menu

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
   String param= "proofType"+i;		
   String value= null;
	   
	while ((value==null)&&i>-1){
		param= "proofType"+i;
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
		   param= "proofType"+i;
		   value= getParameter(param);
	   }
	   	   
	   return 
	   types;
}

	private void readParameters(){
		
		fWelcome=fWelcomeQuiz3;  //default
		
		   {String parser= getParameter("parser");
		   if (parser!=null&&parser.equals("bergmann")){
			   TPreferences.fParser="bergmann [copi gentzen hausman]";
			   fWelcome=fWelcomeQuiz3Bergmann;  //default
		   }
		 
		   if (parser!=null&&parser.equals("copi")){
			   TPreferences.fParser="copi [bergmann gentzen hausman]";
			   //fWelcome=fWelcomeQuiz3Bergmann;  //default
		   }
		   
		   if (parser!=null&&parser.equals("hausman")){
			   TPreferences.fParser="hausman [bergmann copi gentzen]";
			//   fWelcome=fWelcomeQuiz3Hausman; now read from params
		   }
		   
		   }
		
		
		

	   {String welcome= getParameter("welcome"); 
	   if ((welcome!=null)&&welcome.equals("quiz4")){	   
          fWelcome= fWelcomeQuiz4;        //default

	   }
          
	   else if ((welcome!=null)&&welcome.equals("quiz5"))
           fWelcome= fWelcomeQuiz5;

	   else if ((welcome!=null)&&welcome.equals("quiz6"))
           fWelcome= fWelcomeQuiz6;
	   
	   else if ((welcome!=null)&&welcome.equals("quiz6Bergmann"))
           fWelcome= fWelcomeQuiz6Bergmann;
   	   
	   else if ((welcome!=null)&&welcome.equals("midTerm"))
           fWelcome= fWelcomeMidTerm;

	   else if ((welcome!=null)&&welcome.equals("quiz8"))
           fWelcome= fWelcomeQuiz8;
	   
	   else if ((welcome!=null)&&welcome.equals("quiz8Bergmann"))
           fWelcome= fWelcomeQuiz8Bergmann;
           
	   else if ((welcome!=null)&&welcome.equals("finalQ7"))
               fWelcome= fWelcomeFinalQ7;
	   
	   else if ((welcome!=null)&&welcome.equals("bonusQuiz"))
           fWelcome= fWelcomeBonusQuiz;
   	   
	   else if ((welcome!=null)&&welcome.equals("bonusIdentityQuiz"))
           fWelcome= fWelcomeIdentityBonusQuiz;
   	   
	   else if (welcome!=null){
           fWelcome= new JTextArea(welcome);  // general passed in
           
           //fWelcome.setText("<html> Hello </html>");
	   }
	     	  
	   }
	   
	   {String welcomeLabel= getParameter("welcomeLabel"); 
	   if (welcomeLabel!=null)  
           fWelcomeLabel= new JLabel(welcomeLabel);  // general passed in
	   }
	   
	   {String forTest= getParameter("forTest"); 
	   if ((forTest!=null)&&forTest.equals("true"))
          fForTest= true;

	   } 
	   {String keepAdvanced= getParameter("advanced"); 
	   if ((keepAdvanced!=null)&&keepAdvanced.equals("true"))
		   fRemoveAdvanced= false;

	   }
	   
	   {String derive= getParameter("derive"); 
	   if ((derive!=null)&&derive.equals("false"))
		   TPreferences.fDerive= false;
	   }
	   
	   TPreferences.fRightMargin=360;    //not reading, using default
}

	
	private void createGUI(Container contentPane){
	
	readParameters();
	
	   if (TPreferences.fParser.charAt(0)=='b'){//.equals("bergmann [copi gentzen hausman]"))
		   TPreferences.fUseAbsurd= false;  //default true for most others
		   fParser =new TBergmannParser();
		   fDeriverDocument=new TBergmannDocument();	//this gives Bergmann random proofs	   
	   }
	   
	   if (TPreferences.fParser.charAt(0)=='c'){//.equals("bergmann [copi gentzen hausman]"))
		   TPreferences.fUseAbsurd= false;  //default true for most others
		   fParser =new TCopiParser();
		   fDeriverDocument=new TCopiDocument();	//this gives Bergmann random proofs	   
	   }
	   
	   if (TPreferences.fParser.charAt(0)=='h'){//.equals("bergmann [copi gentzen hausman]"))
		   TPreferences.fUseAbsurd= false;  //default true for most others
		   fParser =new THausmanParser();
		   fDeriverDocument=new THausmanDocument();	//this gives Bergmann random proofs	   
	   }
	   
	   
	   
	
	int [] temp=readParamProofTypes();
	
	if (temp!=null)
		prooftypes=temp;  // a default has been initialized

	setSize(fPreferredSize);
	

/*********/	
	
int lower=TAppletRandomProofPanel.typeRange[0];
int upper=TAppletRandomProofPanel.typeRange[1];

numProofs=prooftypes.length;

   fProofs = new TAppletRandomProofPanel[numProofs];

	  String [] proofStrings= new String[numProofs]; //THIS IS A HACK THAT I NEED TO FIX, BUT WE DON'T WANT DUPLICATES

	  for (int i=0;i<numProofs;i++){

	    fProofs[i] =null;
	    proofStrings[i]="";


	    if (prooftypes[i]>lower&&prooftypes[i]<upper){
	      fProofs[i] = supplyRandomPanel(fDeriverDocument,
	                                         "Proof " + (i + 1) + " Code: ",
	                                         prooftypes[i]);

	      boolean duplicate=true; int tries=10; String testStr="";

	      while (duplicate&&tries>0){

	      duplicate=false;

	      testStr=fProofs[i].getSetProof();

	      for (int j=0;(j<i);j++){
	        if (testStr.equals(proofStrings[j])) {
	          duplicate=true;
	          tries-=1;
	          fProofs[i] = supplyRandomPanel(fDeriverDocument,                   //try another one
	                                         "Proof " + (i + 1) + " Code: ",
	                                         prooftypes[i]);
	          break;   // leave j loop and go back to while

	        }

	      }
	      }

	      proofStrings[i]=testStr;  // success, fill in next part of array
	      
	     
	      fProofs[i].pruneMenus(fForTest,fRemoveAdvanced);
	      fProofs[i].startProof(testStr);  // load it
	    }



	  }	
	
	
	
	
	
	
	
	
	
/***************/	
	
	fFinishPanel.add(feedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

	   JPanel intro=new JPanel(new BorderLayout());
	  // fNameEntry.fName.setText(TDeriverApplication.getUser());

	   intro.add(fNameEntry,BorderLayout.NORTH);


	//   String connectiveMessage="";



	  fWelcome.setLineWrap(true);
	  fWelcome.setWrapStyleWord(true);
	  fWelcome.setFont(new Font("Sans-Serif",Font.PLAIN,12));

	  fWelcome.setEditable(false);
	  
	  //if we have read in a Label, we will use it
	  
	  if (fWelcomeLabel!=null)
		  intro.add (fWelcomeLabel,BorderLayout.CENTER);
	  else
	   intro.add (fWelcome,BorderLayout.SOUTH);
	   
	   //createFinishPanel();
	   
	   initializeFinishPanel();

	//  fTabs.add("Intro",intro);
	
	  fTabs.add("Intro",intro);
	  for (int i=0;i<numProofs;i++){
	    if (fProofs[i]!=null)
	      fTabs.add("Proof "+(i+1), fProofs[i]);
	  }
	  fTabs.add("Finish",fFinishPanel);  //scroller in case it gets too big

	  
	  
	/*  
	  
	  fTabs.add("Main",fConnective);
	  fTabs.add("TT",fTT);
	  fTabs.add("Satis",fSatis);
	  fTabs.add("Cons",fCons);
	  fTabs.add("Inval",fInvalid);
	  fTabs.add("Finish", fFinishPanel);   */

	fTabs.addChangeListener(tabsChangeListener());
			
 contentPane.add(fTabs);
 
 this.setVisible(false);
 this.setVisible(true);
}
	
TAppletRandomProofPanel	supplyRandomPanel(TDeriverDocument aDocument,String label,int type){
   
if (TPreferences.fParser.charAt(0)=='b')
		return
			new TAppletBergmannRandomProofPanel(aDocument,   //need right rules
            label,
            type);
	
	else if (TPreferences.fParser.charAt(0)=='h')
		return
		new TAppletHausmanRandomProofPanel(aDocument,   //need right rules
        label,
        type);

else  
		return
		
			new TAppletRandomProofPanel(aDocument,                   //try another one
                label,
                type);
		
	}


	private ChangeListener tabsChangeListener(){
	return
	new ChangeListener(){
		public void stateChanged(ChangeEvent e){

		  Component selected = fTabs.getSelectedComponent();


		            if (selected == fFinishPanel) {
		            	updateFinishPanel();
		              
		            }
		          }
		        };
		      }


	private void initializeFinishPanel(){
    fFinishPanel=new JPanel(new BorderLayout());
    fCode=new JTextArea("");

    codeScroller =new JScrollPane(fCode);

    fFinishPanel.add(codeScroller, BorderLayout.CENTER);

    fFinishPanel.add(new JLabel("Be sure to submit the Confirmation Code."   //adding every update?
                                       +"")
                            , BorderLayout.SOUTH);

  }



public String proofQuizConfirmation(String name,String compositeStr){
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
	
	fFinishPanel.remove(codeScroller);
	fFinishPanel.remove(feedback);

    String name = fNameEntry.fName.getText();

    if (name == null || name.equals("")) {
      feedback = new JLabel(
          "You need to enter a name on the Intro Page.");
      fFinishPanel.add(feedback, BorderLayout.NORTH);
    }
    else {	
	

	   String outputStr="";
	   String compositeStr="";

	   for (int i=0;i<numProofs;i++){
	    if (fProofs[i]!=null)
	      outputStr+= strCR
	                +"Proof "+(i+1)+":  ["
	                +fProofs[i].produceProofStr()
	                + "]";
          compositeStr+= strCR+ fProofs[i].produceProofStr(); 
	   }

	   fConfCode=TUtilities.toLines(proofQuizConfirmation(name,compositeStr),
			                        70);

	    fCode = new JTextArea(strCR
	                                           +"Here is what you have proved: "

	                                           +strCR
	                                           +outputStr
	                                           +strCR+strCR
	                                           +"To submit: Copy and Paste the following Confirmation Codes (into the d2l Quiz Attempt): "
	                                           +strCR
	                                           +strCR
	                                           +fConfCode
	                                           +strCR+strCR
	                                       +""
	                );

	            fCode.setEditable(false);

	            fCode.setLineWrap(true);
	 fCode.setWrapStyleWord(true);
	 fCode.setFont(new Font("Sans-Serif",Font.PLAIN,12));


	  codeScroller=new JScrollPane(fCode);

	  fFinishPanel.add(codeScroller, BorderLayout.CENTER);


	  }

}


public String getConfCode(){
	return
	fConfCode;
}



class NameEntry extends JPanel{
  public JTextField fName= new JTextField(20);
  public NameEntry(){
    super (new BorderLayout());

    JLabel label = new JLabel("Name: ");

    add(label,BorderLayout.WEST);
    add(fName,BorderLayout.CENTER);

  }
}

}


