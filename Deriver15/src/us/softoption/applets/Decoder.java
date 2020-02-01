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

package us.softoption.applets;



import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import us.softoption.editor.TJournal;
import us.softoption.infrastructure.Symbols;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;


/* TJournal is an interface for writing to
 * 
 */

public class Decoder extends JApplet implements TJournal{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	 JTextPane fJournalPane;      // often not visible, if using buttons to start
	 HTMLEditorKit fEditorKit;
	 JLabel  fLabel=new JLabel("Decoder");
	 JPanel fComponentsPanel;    //usually buttons

	 String fInputText=null;
	 
	 Dimension fPreferredSize=new Dimension(600,400);
	 Dimension fMinimumSize=new Dimension(/*500*/ 540,300);
	 Dimension fJournalPreferredSize=new Dimension(600,300);

	 /************ TO DO *************/
	 public void writeHTMLToJournal(String message,boolean append){
			// haven't written it yet
		}

	 
	 
	 /*************************/
	 
	 
	 
	
	public void init(){
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
		
		 int year=cal.get(Calendar.YEAR);
			
		 if (year>TConstants.APPLET_EXPIRY){
	          JLabel label = new JLabel("The code for this applet expired in " +TConstants.APPLET_EXPIRY +" .");
           contentPane.add(label);
		}
		else{
		//	   TPreferences.resetToDefaults();
			 
			   
			   createGUI(contentPane);
			   
			   this.setVisible(true);
			   this.setPreferredSize(fPreferredSize);
			
		}		
	}

public void paint(Graphics g) {   // can see background properly in new Firefox

    	
    	super.paint(g);
 
    	g.drawRect(0, 0, 
     		   getSize().width - 1,
     		   getSize().height - 1);  	
    	

        }
	

void createPanels(){

	
	fJournalPane = new JTextPane();
	 fEditorKit = new HTMLEditorKit();

		fJournalPane.setEditorKit(fEditorKit);
		fJournalPane.setDragEnabled(true);
		fJournalPane.setEditable(true);
		fJournalPane.setPreferredSize(fJournalPreferredSize);
		fJournalPane.setMinimumSize(new Dimension(500,400));

		if (fInputText!=null)
			fJournalPane.setText(fInputText);
		
	JScrollPane journalScroller=new JScrollPane(fJournalPane);
		
	journalScroller.setPreferredSize(fJournalPreferredSize);
	journalScroller.setMinimumSize(new Dimension(500,400));
		
		fComponentsPanel = new JPanel();  //usually buttons
		


 JComponent[] components= {journalScroller,startButton()};
 
 /*Here we will either have the journal pane and a start button, or a bunch of buttons
  * for the preset proofs
  */
 
 int depth =fJournalPane.getPreferredSize().height;

			
 initializeComponentsPanel(components,depth);
	
}
	
void createGUI(Container contentPane){

	
	createPanels();
			    
		contentPane.setPreferredSize(fPreferredSize);
		contentPane.setMinimumSize(fMinimumSize);  

		contentPane.setLayout(new GridBagLayout());

		contentPane.add(fLabel,new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

	//	contentPane.add(fTreePanel,new GridBagConstraints(0, 1, 2, 2, 0.0, 0.0
//		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
	
    contentPane.add(fComponentsPanel,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
		}

// end of createGUI

void initializeComponentsPanel(JComponent [] components,int depth){
  fComponentsPanel.setPreferredSize(new Dimension(fPreferredSize.width,depth));

  fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons


 for (int i=0;i<components.length;i++){
			      fComponentsPanel.add(components[i],   new GridBagConstraints(0, i, 1, 1, 0.0, 0.0
			         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
			    }
			}




	
/***********************  Buttons ***************************/
	
	JButton startButton(){
		JButton button = new JButton("Decode");	    
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				readAndStart();
			}});
		return
		   button;
	}
	
/***********************  End of Buttons ***************************/
	
void readAndStart(){
	String inputStr=readSource(TUtilities.noFilter);
	
	
    if (inputStr!=null){


        writeToJournal(Symbols.strCR+
        		       Symbols.strCR+
        		       "Trying general decode: " +Symbols.strCR+Symbols.strCR+
                       TUtilities.generalDecode(TUtilities.noReturnsFilter(inputStr)) +          // we may have put returns in to make it wrap
                       Symbols.strCR+
                     Symbols.strCR, true, false);
    }
	
//	fTreePanel.startTree(inputStr);
}


 
 /**************************** Utilities *************************************/

 String readSource(int filter){

 	return
 	   TSwingUtilities.readSelectionToString(fJournalPane,filter);
 		}

 public void writeOverJournalSelection(String message){

 	   if (message.length()>0)
 	     fJournalPane.replaceSelection(message);
 	}


 	
 public void writeToJournal(String message, boolean highlight,boolean toMarker){

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


