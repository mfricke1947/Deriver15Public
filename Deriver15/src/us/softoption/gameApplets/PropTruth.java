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

import static us.softoption.infrastructure.Symbols.strNull;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.TFormula;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.TParser;

/* I use a fair bit of Deriver code here. But it is not shared code (as that would
 * be too big. 
 * 
 * Instead there are copies of the source code in the directory here.
 * 
 * So, from time to time, Synch. The Derive code is the master, and is relatively 
 * stable in these areas.
 * 
 * 
 */

public class PropTruth extends JApplet{
 TParser fParser;
 JTextPane fJournalPane;
 HTMLEditorKit fEditorKit;
 JLabel label;
 JPanel fComponentsPanel;  //usually buttons
 Dimension fPreferredSize=new Dimension(500,300);

 
	
	public void init(){
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
		 long time=cal.getTimeInMillis();
		
		if (false/*time>10*/){
           JLabel label = new JLabel("The code for this applet is out of date.");
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
	
	private void createGUI(Container contentPane){

   fParser =new TParser();
   fJournalPane = new JTextPane();
   fEditorKit = new HTMLEditorKit();
   fComponentsPanel = new JPanel();  //usually buttons
   label=new JLabel("Propositional Truth: One step of evaluation only");

	
   String inputText= getParameter("inputText");
   String title= getParameter("title"); 
	
	if (title!=null)
      label = new JLabel(title);
   
   String parser= getParameter("parser");
   if (parser!=null&&parser.equals("bergmann"))
	   fParser =new TBergmannParser();
   else if (parser!=null&&parser.equals("copi"))
	   fParser =new TCopiParser();
   else if (parser!=null&&parser.equals("default"))
		   fParser =new TDefaultParser();
   else if (parser!=null&&parser.equals("gentzen"))
		   fParser =new TParser();
	   
   else if (parser!=null&&parser.equals("hausman"))
	   fParser =new THausmanParser();
   else if (parser!=null&&parser.equals("herrick"))
	   fParser =new THerrickParser();

   fEditorKit = new HTMLEditorKit();
   
   

fJournalPane.setEditorKit(fEditorKit);
fJournalPane.setDragEnabled(true);
fJournalPane.setEditable(true);
fJournalPane.setPreferredSize(fPreferredSize);
fJournalPane.setMinimumSize(new Dimension(300,200));

if (inputText!=null)
	fJournalPane.setText(inputText);
	    
contentPane.setBackground(Color.lightGray);

contentPane.setPreferredSize(fPreferredSize);
contentPane.setMinimumSize(new Dimension(500,300));  

contentPane.setLayout(new GridBagLayout());

contentPane.add(label,new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

JScrollPane aScroller=new JScrollPane(fJournalPane);
aScroller.setPreferredSize(fPreferredSize);
aScroller.setMinimumSize(new Dimension(300,200));

contentPane.add(aScroller,new GridBagConstraints(0, 1, 2, 2, 0.0, 0.0
       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

	JComponent[] buttons= {trueButton()};
	initializeComponentsPanel(buttons);


contentPane.add(fComponentsPanel,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
	       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
}


	private void initializeComponentsPanel(JComponent [] components){

	   fComponentsPanel.setPreferredSize(new Dimension(fPreferredSize.width,30));

	    fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons

	    fComponentsPanel.setBackground(Color.lightGray);

	    for (int i=0;i<components.length;i++){
	      fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
	         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
	    }
	}

/******************** Buttons ***************************/

JButton trueButton(){
	JButton button = new JButton("True?");	    
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			doIsTrue();
		}});
	return
	   button;
}

private void doIsTrue(){
	String inputStr=readSource(TUtilities.defaultFilter);

    if ((inputStr!=null)&&(inputStr!=strNull)){

    	isSelectionTrue(inputStr);   // translate back
    }
    else
        Toolkit.getDefaultToolkit().beep();
}


private void isSelectionTrue(String inputStr){
	StringReader aStringReader= new StringReader(inputStr);

if (aStringReader!=null) {

  TFormula root = new TFormula();
  boolean wellformed;
  ArrayList dummy = new ArrayList();

  wellformed=fParser.wffCheck(root, /*dummy,*/ aStringReader);

  if (wellformed &&                  // we're doing only one step ie atomic etc
		  ((root.fKind==TFormula.predicator)||
		  ((root.fKind==TFormula.unary)
				  &&(root.fRLink.fKind==TFormula.predicator))||
		  ((root.fKind==TFormula.binary)
				  &&(root.fLLink.fKind==TFormula.predicator)
				  &&(root.fRLink.fKind==TFormula.predicator)))){

    if (formulaTrue(root))
       writeOverJournalSelection("T");
    else
    	writeOverJournalSelection("F");
  }
  else
  {
      Toolkit.getDefaultToolkit().beep();
  }
      
 }}


/******************** End of Buttons ***************************/



/******************** Truth Definition ************************/

private boolean atomicFormulaTrue(TFormula root){
	
    if (root.fInfo.equals("T"))
    	return
    	   true;

	if ((root.isSpecialPredefined())&& root.equalFormulas(root,TFormula.fTruth))
	  return
        true;   
	   
 return
   false;  // covers false and absurd and all else
}

private boolean formulaTrue(TFormula root){

	 if (root==null)                 //should never happen
	   return
	       false;


	  switch (root.fKind){

	    case TFormula.predicator:
	      return
	          atomicFormulaTrue(root);

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


	   case TFormula.quantifier:

	    default:
	      ;
	  }

	  return
	      false;

	}




/**************************** Utilities *************************************/

private String readSource(int filter){

	return
	   TSwingUtilities.readSelectionToString(fJournalPane,filter);
		}

private void writeOverJournalSelection(String message){

	   if (message.length()>0)
	     fJournalPane.replaceSelection(message);
	}
	
private void writeToJournal(String message, boolean highlight,boolean toMarker){

        int newCaretPosition = fJournalPane.getSelectionEnd(); //if there isn't one it's dot which is the old one

        int messageLength = message.length();

        if (messageLength>0) {

          fJournalPane.setSelectionStart(newCaretPosition);
          fJournalPane.setCaretPosition(newCaretPosition);    //leave existing selection and do everything after

          fJournalPane.replaceSelection(message);

          if (highlight) {
            fJournalPane.setSelectionStart(newCaretPosition);
            fJournalPane.setSelectionEnd(newCaretPosition+messageLength);

          }

        }
     }	
	
	
	
	
	
	
}