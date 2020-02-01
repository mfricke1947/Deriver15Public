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

package us.softoption.hostApplets;

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

import us.softoption.editor.TBergmannDocument;
import us.softoption.editor.TCopiDocument;
import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.TGirleDocument;
import us.softoption.editor.THausmanDocument;
import us.softoption.editor.THowsonDocument;
import us.softoption.editor.TJeffreyDocument;
import us.softoption.editor.TJournal;
import us.softoption.editor.TPreferences;
import us.softoption.editor.TPriestDocument;
import us.softoption.infrastructure.Palette;
import us.softoption.infrastructure.SymbolToolbar;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TGirleParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THowsonParser;
import us.softoption.parser.TJeffreyParser;
import us.softoption.parser.TParser;
import us.softoption.parser.TPriestParser;
import us.softoption.tree.TBarwiseTreePanel;
import us.softoption.tree.TTreePanel;

/* TJournal is an interface for writing to
 * 
 */

public class Trees extends JApplet implements TJournal{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	TDeriverDocument fDeriverDocument= new TDeriverDocument(this);

	
	 TParser fParser;
	 TTreePanel fTreePanel;     // TMy or TMyCopi etc.
	 JTextPane fJournalPane;      // often not visible, if using buttons to start
	 Palette fPalette;
	 SymbolToolbar fSymbolToolbar;
	 
	 HTMLEditorKit fEditorKit;
	 JLabel  fLabel=new JLabel("Trees");
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
		
	/*	try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());   //March 09
		} catch (UnsupportedLookAndFeelException e) {System.err.println("Look and Feel");} */
		
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
		
		 int year=cal.get(Calendar.YEAR);
			
		 if (year>TConstants.APPLET_EXPIRY){
	          JLabel label = new JLabel("The code for this applet expired in " +TConstants.APPLET_EXPIRY +" .");
           contentPane.add(label);
		}
		else{
			   TPreferences.resetToDefaults();
			   
			   readParameters();
			   
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
	
	
void readParameters(){
   fInputText= getParameter("inputText");

   String title= getParameter("title"); 		
   if (title!=null)
      fLabel = new JLabel(title);
   
   String parser= getParameter("parser");
   if (parser!=null&&parser.equals("barwise"))
	   TPreferences.fParser="barwise [bergmann copi gentzen hausman priest]";
   else if (parser!=null&&parser.equals("bergmann"))
	   TPreferences.fParser="bergmann [barwise copi gentzen hausman priest]";
   else if (parser!=null&&parser.equals("copi"))
	   TPreferences.fParser="copi [barwise bergmann gentzen hausman priest]";
   else if (parser!=null&&parser.equals("gentzen"))
	   TPreferences.fParser="gentzen [barwise bergmann copi hausman priest]";
   else if (parser!=null&&parser.equals("girle"))
	   TPreferences.fParser="girle [barwise bergmann copi gentzen hausman priest]";
   else if (parser!=null&&parser.equals("hausman"))
	   TPreferences.fParser="hausman [barwise bergmann copi gentzen priest]"; 
   else if (parser!=null&&parser.equals("howson"))
	   TPreferences.fParser="howson [barwise bergmann copi gentzen hausman priest]";
   else if (parser!=null&&parser.equals("jeffrey"))
	   TPreferences.fParser="jeffrey [barwise bergmann copi gentzen hausman priest]";
   else if (parser!=null&&parser.equals("priest"))
	   TPreferences.fParser="priest [barwise bergmann copi gentzen hausman howson]"; 
  
   String firstOrder= getParameter("firstOrder");
   if (firstOrder!=null&&firstOrder.equals("true"))
	   TPreferences.fFirstOrder=true;     //parse first Order + induction  
   
   String blind= getParameter("blind");
   if (blind!=null&&blind.equals("true"))
	   TPreferences.fBlind=true;        //hide justification on auto proof 

   {String derive= getParameter("derive"); 
   if ((derive!=null)&&derive.equals("false"))
	   TPreferences.fDerive= false;
   }
   
   {String modal= getParameter("modal");   //does not always work with my server
   if ((modal!=null)&&modal.equals("true"))
	   TPreferences.fModal= true;
   }
   

   TPreferences.fRightMargin=360;  // not reading, using default
   
   TPreferences.fIdentity=false;  // true by default, but want it false for Trees
   
   { String identities= getParameter("identity");
	   if (identities!=null&&identities.equals("true"))
		   TPreferences.fIdentity=true; 
	}

}

void createParserAndDoc(){
	
	   if (TPreferences.fParser.charAt(0)=='b'&&
		   TPreferences.fParser.charAt(1)=='e'){//.equals("bergmann [copi gentzen hausman]"))
		   TPreferences.fUseAbsurd= false;  //default true for most others
		   fParser =new TBergmannParser();
		   fDeriverDocument=new TBergmannDocument(this);

		   
	   }
	   else
		   if (TPreferences.fParser.charAt(0)=='c'){//.equals("copi [gentzen hausman]"))
			   fParser =new TCopiParser();
			   fDeriverDocument=new TCopiDocument(this);

		   }
	   else 
		   if (TPreferences.fParser.charAt(0)=='g'){//.equals("copi [gentzen hausman]"))
			   fParser =new TGirleParser();
			   fDeriverDocument=new TGirleDocument(this);

		   }
	   else 
		   if (TPreferences.fParser.charAt(0)=='h'&&
			   TPreferences.fParser.charAt(1)=='a'){//.equals("copi [gentzen hausman]"))
		   fParser =new THausmanParser();
		   fDeriverDocument=new THausmanDocument(this);

	   }
		   else 
			   if ((TPreferences.fParser.charAt(0)=='h'&&
				  TPreferences.fParser.charAt(1)=='o')||
				  (TPreferences.fParser.charAt(0)=='b'&& //for barwise, we'll use Howson parser
				  TPreferences.fParser.charAt(1)=='a')
			   
			   ){//.equals("copi [gentzen hausman]"))
			   fParser =new THowsonParser();
			   fDeriverDocument=new THowsonDocument(this);

		   }
			   else 
				   if (TPreferences.fParser.charAt(0)=='j'&&
					  TPreferences.fParser.charAt(1)=='e'){//jeffrey"))
				   fParser =new TJeffreyParser();
				   fDeriverDocument=new TJeffreyDocument(this);  //Jeffrey similar
			   }
	   
			   else 
				   if (TPreferences.fParser.charAt(0)=='p'&&
					  TPreferences.fParser.charAt(1)=='r'){//.equals("copi [gentzen hausman]"))
				   fParser =new TPriestParser();
				   fDeriverDocument=new TPriestDocument(this);

			   }
	   
	   
	   
	   else{
		   fParser =new TParser();

	   }
	   
		
		  if (TPreferences.fParser.charAt(0)=='b'&& //for barwise, 
			TPreferences.fParser.charAt(1)=='a')
			  fTreePanel =new TBarwiseTreePanel(fDeriverDocument);
			  else
				  fTreePanel =new TTreePanel(fDeriverDocument);
		
		
		
		
		
		fTreePanel.setPreferredSize(fPreferredSize);
		fTreePanel.setMinimumSize(fMinimumSize);
		
		
		fJournalPane = new JTextPane();
		 fEditorKit = new HTMLEditorKit();

			fJournalPane.setEditorKit(fEditorKit);
			fJournalPane.setDragEnabled(true);
			fJournalPane.setEditable(true);
			fJournalPane.setPreferredSize(fJournalPreferredSize);
			fJournalPane.setMinimumSize(new Dimension(300,200));

			if (fInputText!=null)
				fJournalPane.setText(fInputText);
			
		JScrollPane journalScroller=new JScrollPane(fJournalPane);
			
		journalScroller.setPreferredSize(fJournalPreferredSize);
		journalScroller.setMinimumSize(new Dimension(300,200));	   
	   
		fComponentsPanel = new JPanel();  //usually buttons
}

void createPanels(){
	
	  if (TPreferences.fParser.charAt(0)=='b'&& //for barwise, 
				TPreferences.fParser.charAt(1)=='a')
				  fTreePanel =new TBarwiseTreePanel(fDeriverDocument);
				  else
					  fTreePanel =new TTreePanel(fDeriverDocument);


	
	
	
	fTreePanel.setPreferredSize(fPreferredSize);
	fTreePanel.setMinimumSize(fMinimumSize);
	
	
	fJournalPane = new JTextPane();
	 fEditorKit = new HTMLEditorKit();

		fJournalPane.setEditorKit(fEditorKit);
		fJournalPane.setDragEnabled(true);
		fJournalPane.setEditable(true);
		fJournalPane.setPreferredSize(fJournalPreferredSize);
		fJournalPane.setMinimumSize(new Dimension(300,200));

		if (fInputText!=null)
			fJournalPane.setText(fInputText);
		
	JScrollPane journalScroller=new JScrollPane(fJournalPane);
		
	journalScroller.setPreferredSize(fJournalPreferredSize);
	journalScroller.setMinimumSize(new Dimension(300,200));
		
		fComponentsPanel = new JPanel();  //usually buttons
		


 JComponent[] components= {journalScroller,startButton()};
 JComponent[] paramComponents =readParamProofs();
 
 /*Here we will either have the journal pane and a start button, or a bunch of buttons
  * for the preset proofs
  */
 
 int depth =fJournalPane.getPreferredSize().height;
 
 if ((paramComponents.length)>0){     // don't use default journal, load buttons from javascript
    	   components= paramComponents;
    	   depth=30;
 }
			
 initializeComponentsPanel(components,depth);
	
}
	
void createGUI(Container contentPane){
	/*
	 * This has a 
	 *            label title
	 *            tree panel within a scroller
	 *            several launch buttons within a component panel
	 *   OR
	 *   	      label title
	 *            tree panel within a scroller
	 *            palette
	 *            a component panel with journal input text panel within a scroller and a start button
	 *         
	 *    We can decide which is which by reading the param ie input proofs
	 *    If there are some it is the former, if not it is the latter.
	 */

	createParserAndDoc();
	
	//createPanels();   pre Jan09
			    
		contentPane.setPreferredSize(fPreferredSize);
		contentPane.setMinimumSize(fMinimumSize);  

		contentPane.setLayout(new GridBagLayout());

		contentPane.add(fLabel,new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 0, 0));

		contentPane.add(fTreePanel,new GridBagConstraints(0, 1, 2, 2, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
	
		
		
	/*	
		
    contentPane.add(fComponentsPanel,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0)); */
		
		
		//now we decide whether to use a palette
		
		   JComponent[] paramComponents =readParamProofs();
		   
		   if ((paramComponents.length)>0)
			   finishNoPalette(contentPane,paramComponents);
		   else
			   finishWithPalette(contentPane);		
	
		}

void finishNoPalette(Container contentPane,JComponent [] components){
	int depth=30;    // this is the height of the buttons
	
	 initializeComponentsPanel(components,depth);
		
	 contentPane.add(fComponentsPanel,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
				       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));	
	
}

//end of createGUI

void finishWithPalette(Container contentPane){
	
	/*2015 getInputPalette() now has parameters	(boolean lambda,boolean modal,
	boolean setTheory) */


//String symbols =  fParser.getInputPalette(); //+chTherefore;

boolean lambda=false;boolean modal=false;boolean setTheory=false;

String symbols =  fParser.getInputPalette(lambda,modal,setTheory); 

	
//	String symbols =  fParser.getInputPalette(); /*+chTherefore;*/
	
	if (symbols==null)
		symbols="";
	
	fPalette= new Palette(symbols,fJournalPane);
	fPalette.setSize(new Dimension(330, 32));  //was 21
	fPalette.setMaximumSize(new Dimension(330, 32));
	fPalette.setMinimumSize(new Dimension(330, 32));
	fPalette.setPreferredSize(new Dimension(330, 32));
	
	fSymbolToolbar= new SymbolToolbar(symbols,fJournalPane);
	
	
	 contentPane.add(/*fPalette*/ fSymbolToolbar,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));	
	
	
	int depth =fJournalPane.getPreferredSize().height;	
	// put the journal in a scroller	
	JScrollPane aScroller= new JScrollPane(fJournalPane);
	aScroller.setPreferredSize(fJournalPreferredSize);
	aScroller.setMinimumSize(new Dimension(300,200));
	aScroller.setMinimumSize(new Dimension(380,200));
	aScroller.setMinimumSize(new Dimension(360,200));
	// put scroller and button in components
	JComponent[] components= {aScroller,startButton()};
	
	initializeComponentsPanel(components,depth);
	// put compenents in content
	 contentPane.add(fComponentsPanel,new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));	
	
}





void initializeComponentsPanel(JComponent [] components,int depth){
  fComponentsPanel.setPreferredSize(new Dimension(fPreferredSize.width,depth));

  fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons


 for (int i=0;i<components.length;i++){
			      fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
			         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
			    }
			}



JComponent [] readParamProofs(){
	JComponent[] components={};
   int i=0;

   String param= "tree"+i;	
	
   String value= getParameter(param);
	   while (value!=null&&i<10){
		   i++;
		   param= "tree"+i;
		   value= getParameter(param);
	   }
	   
	   
	if (i>0){   
	int count =i;
	   components= new JComponent[count];
	
	   i=0;
	
	   param= "tree"+i;
	   
	   String label="Tree";
	   
	   if (count>6)
		   label="Tr";     // we only fit 6, but we will squeeze a few more
		
       value= getParameter(param);
		   while (value!=null&&i<10){
			   components[i]=proofButton(label+(i+1),value);
			   i++;
			   param= "tree"+i;
			   value= getParameter(param);
		   }
	}
	   	   
	   return 
	   components;
}
	
/***********************  Buttons ***************************/
	
	JButton proofButton(String label, final String inputStr){
		JButton button = new JButton(label);	    
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String filteredStr=TUtilities.logicFilter(inputStr);
        //     filteredStr=TUtilities.htmlEscToUnicodeFilter(filteredStr);
			fTreePanel.startTree(filteredStr);
			}});
		return
		   button;
	}
	
	JButton startButton(){
		JButton button = new JButton("Start from selection");	    
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				readAndStart();
			}});
		return
		   button;
	}
	
/***********************  End of Buttons ***************************/
	
void readAndStart(){
	String inputStr=readSource(TUtilities.logicFilter);
	
	fTreePanel.startTree(inputStr);
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

           fJournalPane.requestFocusInWindow(); //new Oct09
         }
      }	
	

}


