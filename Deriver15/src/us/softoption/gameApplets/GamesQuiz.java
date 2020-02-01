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

import us.softoption.games.TConsistent;
import us.softoption.games.TInvalid;
import us.softoption.games.TMainConnective;
import us.softoption.games.TSatisfiable;
import us.softoption.games.TTruthTable;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.THowsonParser;
import us.softoption.parser.TJeffreyParser;
import us.softoption.parser.TParser;


/*
 * only puts up tabs if some tasks to do
 */
public class GamesQuiz extends JApplet{

  //static public int fNumOpen=0;

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


  TParser fParser =new TParser();;


  JTabbedPane fTabs= new JTabbedPane();

  TMainConnective fConnective;
  boolean fConnectiveRunning=false;

  TTruthTable fTT;
  boolean fTTRunning=false;

  TSatisfiable fSatis;
  boolean fSatisRunning=false;

  TConsistent fCons;
  boolean fConsRunning=false;

  TInvalid fInvalid;
  boolean fInvalidRunning=false;

  JLabel feedback=new JLabel("");
  JTextArea fCode=new JTextArea("");
  JPanel fFinishPanel=new JPanel(new GridBagLayout());
  
  String fConfCode="";

  NameEntry fNameEntry=new NameEntry();
  
  Dimension fPreferredSize= new Dimension (600,300);
  
  int fConnectiveAttempts=5; int fTTAttempts=5; int fSatisAttempts=5; int fConsAttempts=3; int fInvalAttempts=2;
  int fConnectiveTime=40; int fTTTime=150; int fSatisTime=150; int fConsTime=600; int fInvalTime=600;

	public void init(){
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
//		 long time=cal.getTimeInMillis();		
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
  
private void readParameters(){
	
	int positiveValue=0;   //NEED TO DO THIS THROUGHOUT

   {String connAttempts= getParameter("connectiveAttempts"); 
   if (connAttempts!=null){
	    try {
	    	positiveValue= Integer.parseInt(connAttempts);
	    	if (positiveValue>-1)
	    	   fConnectiveAttempts = positiveValue;
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   }

   String connTime= getParameter("connectiveTime"); 
   if (connTime!=null){
	    try {
	    	fConnectiveTime = Integer.parseInt(connTime);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }

   String tTAttempts= getParameter("tTAttempts"); 
   if (tTAttempts!=null){
	    try {
	    	fTTAttempts = Integer.parseInt(tTAttempts);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   
   String tTTime= getParameter("tTTime"); 
   if (tTTime!=null){
	    try {
	    	fTTTime = Integer.parseInt(tTTime);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   
   String satisAttempts= getParameter("satisAttempts"); 
   if (satisAttempts!=null){
	    try {
	    	fSatisAttempts = Integer.parseInt(satisAttempts);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   
   String satisTime= getParameter("satisTime"); 
   if (satisTime!=null){
	    try {
	    	fSatisTime = Integer.parseInt(satisTime);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   
   String consAttempts= getParameter("consAttempts"); 
   if (consAttempts!=null){
	    try {
	    	fConsAttempts = Integer.parseInt(consAttempts);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   
   String consTime= getParameter("consTime"); 
   if (consTime!=null){
	    try {
	    	fConsTime = Integer.parseInt(consTime);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   
   String invalAttempts= getParameter("invalAttempts"); 
   if (invalAttempts!=null){
	    try {
	    	fInvalAttempts = Integer.parseInt(invalAttempts);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }
   
   String invalTime= getParameter("invalTime"); 
   if (invalTime!=null){
	    try {
	    	fInvalTime = Integer.parseInt(invalTime);
	    } catch (NumberFormatException e) {
	        //Use default
	    }
   }

   String parser= getParameter("parser");
   if (parser!=null&&parser.equals("copi"))
	   fParser =new TCopiParser();
   if (parser!=null&&parser.equals("default"))
	   fParser =new TDefaultParser();
   if (parser!=null&&parser.equals("gentzen"))
	   fParser =new TParser();
   if (parser!=null&&parser.equals("hausman"))
	   fParser =new THausmanParser();
   if (parser!=null&&parser.equals("herrick"))
	   fParser =new THerrickParser();
   if (parser!=null&&parser.equals("bergmann"))
	   fParser =new TBergmannParser();
   if (parser!=null&&parser.equals("jeffrey"))
	   fParser =new TJeffreyParser();
   if (parser!=null&&parser.equals("howson"))
	   fParser =new THowsonParser();

}
	
private void createGUI(Container contentPane){
	
	readParameters();
	
	fConnective =new TMainConnective(this,fParser);
	  fConnective.setMaxAttempts(fConnectiveAttempts);
	  fConnective.setMaxTime(fConnectiveTime);

	   fTT =new TTruthTable(this,fParser);
	   fTT.setMaxAttempts(fTTAttempts);
	   fTT.setMaxTime(fTTTime);

	   fSatis =new TSatisfiable(this,fParser);
	   fSatis.setMaxAttempts(fSatisAttempts);
	   fSatis.setMaxTime(fSatisTime);

	   fCons =new TConsistent(this,fParser);
	   fCons.setMaxAttempts(fConsAttempts);
	   fCons.setMaxTime(fConsTime);

	   fInvalid =new TInvalid(this,fParser);
	   fInvalid.setMaxAttempts(fInvalAttempts);
	   fInvalid.setMaxTime(fInvalTime);
	
	setSize(fPreferredSize);
	
	//fFinishPanel.add(feedback);
	
	fFinishPanel.add(feedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

	   JPanel intro=new JPanel(new BorderLayout());
	  // fNameEntry.fName.setText(TDeriverApplication.getUser());

	   intro.add(fNameEntry,BorderLayout.NORTH);


	   String connectiveMessage="";
	   if (fConnectiveAttempts>0)
	     connectiveMessage="Main Connective: attempt "+fConnectiveAttempts+". Times out in "+fConnectiveTime+" seconds.";
	   String tTMessage="";
	   if (fTTAttempts>0)
	     tTMessage="Truth Table: attempt "+fTTAttempts+". Times out in "+fTTTime+" seconds.";
	   String satisMessage="";
	   if (fSatisAttempts>0)
	     satisMessage="Satisfiable: attempt "+fSatisAttempts+". Times out in "+fSatisTime+" seconds.";
	   String consMessage="";
	   if (fConsAttempts>0)
	     consMessage="Consistent: attempt "+fConsAttempts+". Times out in "+fConsTime+" seconds.";
	   String invalMessage="";
	   if (fInvalAttempts>0)
	     invalMessage="Invalid: attempt "+fInvalAttempts+". Times out in "+fInvalTime+" seconds.";


	   JTextArea text= new JTextArea(strCR
	        + "Work through the Tabs to Finish."
	        + " [When a Tab is opened, its timer starts.]"
	        + strCR
	        + strCR
	        + connectiveMessage
	        + strCR
	        + tTMessage
	        + strCR
	        + satisMessage
	        + strCR
	        + consMessage
	        + strCR
	        + invalMessage
	        + strCR
	        + strCR
	        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
	        + strCR
	);
	
	//Feb08 Label to replace text   
	   JLabel textLabel= new JLabel("<html><br>Work through the Tabs to Finish."
		        + " [When a Tab is opened, its timer starts.]"
		        + "<br>"
		        + "<br>"
		        + connectiveMessage
		        + "<br>"
		        + tTMessage
		        + "<br>"
		        + satisMessage
		        + "<br>"
		        + consMessage
		        + "<br>"
		        + invalMessage
		        + "<br>"
		        + "<br>"
		        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
		        + "<br> </html>"
		);

	  text.setLineWrap(true);
	   text.setWrapStyleWord(true);
	  text.setFont(new Font("Sans-Serif",Font.PLAIN,12));

	  text.setEditable(false);

	   //intro.add (text,BorderLayout.SOUTH);
	   intro.add (textLabel,BorderLayout.SOUTH);
	   
	   createFinishPanel();

	  fTabs.add("Intro",intro);
	  
	  if (fConnectiveAttempts>0)
		  fTabs.add("Main",fConnective);
	  if (fTTAttempts>0)
		  fTabs.add("TT",fTT);
	  if (fSatisAttempts>0)
	      fTabs.add("Satis",fSatis);
	  if (fConsAttempts>0)
		  fTabs.add("Cons",fCons);
	  if (fInvalAttempts>0)
		  fTabs.add("Inval",fInvalid);
	  fTabs.add("Finish", fFinishPanel);

	fTabs.addChangeListener(tabsChangeListener());
			
	/*		
			
			new ChangeListener(){
	public void stateChanged(ChangeEvent e){
//	  int index = fTabs.getSelectedIndex();

	  Component selected = fTabs.getSelectedComponent();

	  if (selected == fConnective) {
	    if (!fConnectiveRunning) {
	      fConnective.run();
	      fConnectiveRunning = true;
	    }
	  }
	  else {
	    if (selected == fTT) {
	      if (!fTTRunning) {
	        fTT.run();
	        fTTRunning = true;
	      }
	    }
	    else {
	      if (selected == fSatis) {
	        if (!fSatisRunning) {
	          fSatis.run();
	          fSatisRunning = true;
	        }

	      }
	      else {
	        if (selected == fCons) {
	          if (!fConsRunning) {
	            fCons.run();
	            fConsRunning = true;
	          }

	        }
	        else {
	          if (selected == fInvalid) {
	            if (!fInvalidRunning) {
	              fInvalid.run();
	              fInvalidRunning = true;
	            }

	          }
	          else {
	            if (selected == finishPanel) {
	              {
	                finishPanel.remove(feedback);
	                finishPanel.remove(code);

	                int correct = (fConnective.getCorrect()
	                               + fTT.getCorrect()
	                               + fSatis.getCorrect()
	                               + fCons.getCorrect()
	                               + fInvalid.getCorrect()
	                    );
	                int total = (fConnective.getTotal()
	                             + fTT.getTotal()
	                             + fSatis.getTotal()
	                             + fCons.getTotal()
	                             + fInvalid.getTotal()
	                    );

	                String name = fNameEntry.fName.getText();

	                if (name == null || name.equals("")) {
	                  feedback = new JLabel(
	                      "You need to enter a name on the Intro Page.");

	                }
	                else {

	                  feedback = new JLabel(fNameEntry.fName.getText() +
	                                        ", you have "
	                                        + correct
	                                        + " right out of "
	                                        + total
	                                        + ".");

	                  DateFormat shortTime=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
	                  String time=shortTime.format(new Date());

	                  JTextArea code = new JTextArea(strCR
	                                                 +
	                      "Here is the number you got correct: " + correct

	                                                 + strCR+ strCR
	                                                 +
	                      "Here is your Confirmation Code:"
	                      + strCR+ strCR 
	                      +"["
                            + TUtilities.urlEncode(TUtilities.xOrEncrypt(
	                          name+ ", "
	                          + time+ ", "
	                          + correct
	                          + " of "
	                          + total))
	                                                 + "]"
	                                                 + strCR +
	                                                 strCR
	                                                 + "To submit: (copy then) paste the number you got correct, and the Confirmation Code "
	                                                 + strCR
	                                                 + "(into the d2l Quiz Attempt).   "
	                                                 + strCR +
	                                                 strCR
	                                              //   + " You should copy and paste the Confirmation Code, as it may contain unusual characters."
	                      );

	                  code.setEditable(true);

	                  code.setLineWrap(true);
	                  code.setWrapStyleWord(true);
	                  code.setFont(new Font("Sans-Serif", Font.PLAIN, 12));

	                  finishPanel.add(code, BorderLayout.CENTER);

	                  finishPanel.add(new JLabel(
	                      "Submit the number correct and Confirmation Code."
	                      + "")
	                                  , BorderLayout.SOUTH);
	                }

	                finishPanel.add(feedback, BorderLayout.NORTH);

	              }
	            }
	          }
	        }
	      }
	    }
	  }
	}});  */

 contentPane.add(fTabs);
 
 this.setVisible(false);
 this.setVisible(true);
}


private ChangeListener tabsChangeListener(){
	return
	new ChangeListener(){
		public void stateChanged(ChangeEvent e){
//		  int index = fTabs.getSelectedIndex();

		  Component selected = fTabs.getSelectedComponent();

		  if (selected == fConnective) {
		    if (!fConnectiveRunning) {
		      fConnective.run();
		      fConnectiveRunning = true;
		    }
		  }
		  else {
		    if (selected == fTT) {
		      if (!fTTRunning) {
		        fTT.run();
		        fTTRunning = true;
		      }
		    }
		    else {
		      if (selected == fSatis) {
		        if (!fSatisRunning) {
		          fSatis.run();
		          fSatisRunning = true;
		        }

		      }
		      else {
		        if (selected == fCons) {
		          if (!fConsRunning) {
		            fCons.run();
		            fConsRunning = true;
		          }

		        }
		        else {
		          if (selected == fInvalid) {
		            if (!fInvalidRunning) {
		              fInvalid.run();
		              fInvalidRunning = true;
		            }

		          }
		          else {
		            if (selected == fFinishPanel) {
		            	refreshFinishPanel();
		              
		            }
		          }
		        }
		      }
		    }
		  }
		}};
}


private void createFinishPanel(){
	fFinishPanel.add(feedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

	
    fCode.setEditable(true);

    fCode.setLineWrap(true);
    fCode.setWrapStyleWord(true);
    fCode.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
    
   // code.setPreferredSize(new Dimension (540,140));

   // fFinishPanel.add(code, BorderLayout.CENTER);
    
		JScrollPane aScroller=new JScrollPane(fCode);
	aScroller.setPreferredSize(new Dimension (540,160));
	aScroller.setMinimumSize(new Dimension (540,160));
    
    
    
	  fFinishPanel.add(aScroller,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));


 /*   fFinishPanel.add(new JLabel(
        "Submit the number correct and Confirmation Code."
        + "")
                    , BorderLayout.SOUTH); */
    
	fFinishPanel.add(new JLabel(
            "Submit the number correct and Confirmation Code."),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
	
}

private void refreshFinishPanel(){
    fFinishPanel.remove(feedback);
    fCode.setText("");

    int correct = (fConnective.getCorrect()
                   + fTT.getCorrect()
                   + fSatis.getCorrect()
                   + fCons.getCorrect()
                   + fInvalid.getCorrect()
        );
    int total = (fConnective.getTotal()
                 + fTT.getTotal()
                 + fSatis.getTotal()
                 + fCons.getTotal()
                 + fInvalid.getTotal()
        );
    
    int maxAttempts = (fConnectiveAttempts
            + fTTAttempts
            + fSatisAttempts
            + fConsAttempts
            + fInvalAttempts
   );

    String name = fNameEntry.fName.getText();

    if (name == null || name.equals("")) {
      feedback = new JLabel(
          "You need to enter a name on the Intro Page.");

    }
    else {

      feedback = new JLabel(fNameEntry.fName.getText() +
                            ", you have "
                            + correct
                            + " right out of "
                            + total
                            + " attempted. [You should have attempted: "
          	              + maxAttempts +".]");

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
                                     + "To submit: (copy then) paste the number you got correct, and the Confirmation Code, "
                                     + strCR
                                     + "(into the Quiz Attempt).   "
                                     + strCR +
                                     strCR
                                  //   + " You should copy and paste the Confirmation Code, as it may contain unusual characters."
          );
    }

   // fFinishPanel.add(feedback, BorderLayout.NORTH);
    
	fFinishPanel.add(feedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
      
	fFinishPanel.setVisible(false);
	fFinishPanel.setVisible(true);

  }



private void removeConnectiveTab(){

fTabs.remove(fConnective);

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

