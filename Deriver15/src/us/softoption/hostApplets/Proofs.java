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

import java.awt.Color;
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
import us.softoption.editor.TDefaultDocument;
import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.THausmanDocument;
import us.softoption.editor.THerrickDocument;
import us.softoption.editor.TJournal;
import us.softoption.editor.TPreferences;
import us.softoption.infrastructure.SymbolToolbar;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.TParser;
import us.softoption.proofs.TMyBergmannProofPanel;
import us.softoption.proofs.TMyCopiProofPanel;
import us.softoption.proofs.TMyDefaultProofPanel;
import us.softoption.proofs.TMyHausmanProofPanel;
import us.softoption.proofs.TMyHerrickProofPanel;
import us.softoption.proofs.TMyProofPanel;
import us.softoption.proofs.TProofPanel;

/* TJournal is an interface for writing to
 * 
 */

public class Proofs extends JApplet implements TJournal{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	TDeriverDocument fDeriverDocument= new TDeriverDocument(this);
	
	
	public boolean fRewriteRules=true; // extra menu item
	
	//THIS IS A LITTLE INELEGANT-- USING A GLOBAL (should read and pass it)
	
	
	public boolean fRemoveAdvanced=false; // remove that menu
	
	 TParser fParser;
	 TProofPanel fProofPanel;     // TMy or TMyCopi etc.
	 JTextPane fJournalPane;      // often not visible, if using buttons to start
	//dec09  Palette fPalette;
	 SymbolToolbar fSymbolToolbar;

	 HTMLEditorKit fEditorKit;
	 JLabel  fLabel=new JLabel("Derivations");
	 JPanel fComponentsPanel;    //usually buttons

	 String fInputText=null;
	 
	 //boolean fNoCommands=true;
	 Dimension fPreferredSize=new Dimension(600,400);
	 Dimension fMinimumSize=new Dimension(/*540*/ 500,300);  //tried 540, Jan 09 did not look as good
	 Dimension fJournalPreferredSize=new Dimension(600,300);
	 //Dimension fJournalPreferredSize=new Dimension(400,300);

	 /************ TO DO *************/
	 public void writeHTMLToJournal(String message,boolean append){
			// haven't written it yet
		}

	 
	 
	 /*************************/
	 
	 
	 
	
	public void init(){
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
	//	 long time=cal.getTimeInMillis();
	//	 long expiry=TConstants.expiry;
		
		 int year=cal.get(Calendar.YEAR);
			
		 if (year>TConstants.APPLET_EXPIRY){
	          JLabel label = new JLabel("The code for this applet expired in " +TConstants.APPLET_EXPIRY +" .");
           contentPane.add(label);
		}
		else{
			TPreferences.resetToDefaults();
			   
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
   if (parser!=null&&parser.equals("bergmann"))
	   TPreferences.fParser="bergmann [copi gentzen hausman herric]";
   else if (parser!=null&&parser.equals("copi"))
	   TPreferences.fParser="copi [bergmann gentzen hausman herrick]";
   else if (parser!=null&&parser.equals("gentzen"))
	   TPreferences.fParser="gentzen [bergmann copi hausman herrick]";
   else if (parser!=null&&parser.equals("hausman"))
	   TPreferences.fParser="hausman [bergmann copi gentzen herrick]";
   else if (parser!=null&&parser.equals("herrick"))
	   TPreferences.fParser="herrick [bergmann copi gentzen hausman]";
   
 /*  String commands= getParameter("commands");
   if (commands!=null&&commands.equals("noCommands"))
	   fNoCommands=true;  */
   String rewrites= getParameter("rewrites"); //by default true but needs advancedMenu
   if (rewrites!=null&&rewrites.equals("false"))
	   fRewriteRules=false;     //TProofPanel will not put up the menu
	
   String noAdvanced= getParameter("advancedMenu");  //true by default
   if (noAdvanced!=null&&noAdvanced.equals("false"))
	   fRemoveAdvanced=true;     //TProofPanel will put up the menu   
   
   String firstOrder= getParameter("firstOrder");
   if (firstOrder!=null&&firstOrder.equals("true"))
	   TPreferences.fFirstOrder=true;     //parse first Order + induction
   
   String identity= getParameter("identity");
   if (identity!=null&&identity.equals("true"))
	   TPreferences.fIdentity=true; 
   
   String setTheory= getParameter("setTheory");
   if (setTheory!=null&&setTheory.equals("true"))
	   TPreferences.fSetTheory=true;     //set Theory axioms  
   
   String blind= getParameter("blind");
   if (blind!=null&&blind.equals("true"))
	   TPreferences.fBlind=true;        //hide justification on auto proof 

   {String derive= getParameter("derive"); 
   if ((derive!=null)&&derive.equals("false"))
	   TPreferences.fDerive= false;
   }
   
   {String derive= getParameter("useAbsurd"); // default true except Bergmann
   if ((derive!=null)&&derive.equals("true"))
	   TPreferences.fUseAbsurd= true;
   
   }
   
   TPreferences.fRightMargin=420;  // not reading, using default

}
	
void adjustProofMenus(){
    {boolean forTest=false;    //DO THIS BY READING FROM PARAMETERS
    if (fRemoveAdvanced)
    	fProofPanel.removeAdvancedMenu();
    
    if (!fRewriteRules)
    	fProofPanel.removeRewriteMenuItem();

    fProofPanel.removeConfCodeWriter();
    fProofPanel.removeMarginMenuItem();
    fProofPanel.removeWriteProofMenuItem();

    if (forTest)
       fProofPanel.removeDeriveSupport();  
    	  
      }
	
}
	
void readParamsAndCreateDocuments(){
	   fJournalPane = new JTextPane();
	   fEditorKit = new HTMLEditorKit();
	   fComponentsPanel = new JPanel();  //usually buttons
	 			
readParameters();
		
	   if (TPreferences.fParser.charAt(0)=='b'){//.equals("bergmann [copi gentzen hausman]"))
		   TPreferences.fUseAbsurd= false;  //default true for most others
		   fParser =new TBergmannParser();
		   fDeriverDocument=new TBergmannDocument(this);
		   fProofPanel =new TMyBergmannProofPanel(fDeriverDocument);
		   
	   }
	   else
		   if (TPreferences.fParser.charAt(0)=='c'){//.equals("copi [gentzen hausman]"))
			   fParser =new TCopiParser();
			   fDeriverDocument=new TCopiDocument(this);       // need the this so the parser errors come back
			   fProofPanel =new TMyCopiProofPanel(fDeriverDocument);
		   }
	   else
			   if (TPreferences.fParser.charAt(0)=='g'){//.gentzen hausman]"))
				   fParser =new TParser();
				   fDeriverDocument=new TDeriverDocument(this);       // need the this so the parser errors come back
				   fProofPanel =new TMyProofPanel(fDeriverDocument);
			   }
	   else 
		   if ((TPreferences.fParser.charAt(0)=='h')&&
				(TPreferences.fParser.charAt(1)=='a'))   {//.equals("copi [gentzen hausman]"))
		   fParser =new THausmanParser();
		   fDeriverDocument=new THausmanDocument(this);
		   fProofPanel =new TMyHausmanProofPanel(fDeriverDocument);
	   }
	   else
		   if ((TPreferences.fParser.charAt(0)=='h')&&
					(TPreferences.fParser.charAt(1)=='e'))   {//.equals("copi [gentzen hausman]"))
			   fParser =new THerrickParser();
			   fDeriverDocument=new THerrickDocument(this);
			   fProofPanel =new TMyHerrickProofPanel(fDeriverDocument);
		   }
      else{
		   fParser =new TDefaultParser();
		   fDeriverDocument=new TDefaultDocument(this);
		   fProofPanel =new TMyDefaultProofPanel(fDeriverDocument);
	   }

fEditorKit = new HTMLEditorKit();

	fJournalPane.setEditorKit(fEditorKit);
	fJournalPane.setDragEnabled(true);
	fJournalPane.setEditable(true);
	fJournalPane.setPreferredSize(fJournalPreferredSize);
	fJournalPane.setMinimumSize(new Dimension(300,200));

	if (fInputText!=null)
		fJournalPane.setText(fInputText);

   adjustProofMenus();	
}
	
void createGUI(Container contentPane){
	
	/*
	 * This has a 
	 *            label title
	 *            proof panel within a scroller
	 *            several launch buttons within a component panel
	 *   OR
	 *   	      label title
	 *            proof panel within a scroller
	 *            palette
	 *            a component panel with journal input text panel within a scroller and a start button
	 *         
	 *    We can decide which is which by reading the param ie input proofs
	 *    If there are some it is the former, if not it is the latter.
	 */
	
	    readParamsAndCreateDocuments();
			    
		contentPane.setPreferredSize(fPreferredSize);
		contentPane.setMinimumSize(fMinimumSize);  

		contentPane.setLayout(new GridBagLayout());
		
		contentPane.setBackground(Color.lightGray);

		contentPane.add(fLabel,new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 0, 0));

		JScrollPane aScroller=new JScrollPane(fProofPanel);
	
		
		aScroller.setPreferredSize(fPreferredSize);
		aScroller.setMinimumSize(fMinimumSize);

		contentPane.add(aScroller,new GridBagConstraints(0, 1, 2, 2, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
	
		
	/*	
		fJournalScroller.setPreferredSize(fJournalPreferredSize);
		//anotherScroller.setMinimumSize(new Dimension(300,200));
		//anotherScroller.setMinimumSize(new Dimension(380,200));
		fJournalScroller.setMinimumSize(new Dimension(360,200)); */

//now we decide whether to use a palette
		
   JComponent[] paramComponents =readParamProofs();
   
   if ((paramComponents.length)>0)
	   finishNoPalette(contentPane,paramComponents);
   else
	   finishWithPalette(contentPane);
/*		
 JComponent[] components= {anotherScroller,startButton()};
 JComponent[] paramComponents =readParamProofs();
 
 int depth =fJournalPane.getPreferredSize().height;
 
 if ((paramComponents.length)>0){     // don't use default journal, load buttons from javascript
    	   components= paramComponents;
    	   depth=30;
 }
			
 initializeComponentsPanel(components,depth);
		
 contentPane.add(fComponentsPanel,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
*/		}



void finishNoPalette(Container contentPane,JComponent [] components){
	int depth=30;    // this is the height of the buttons
	
	 initializeComponentsPanel(components,depth);
		
	 contentPane.add(fComponentsPanel,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
				       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));	
	
}


void finishWithPalette(Container contentPane){
	
/*2015 getInputPalette() now has parameters	(boolean lambda,boolean modal,
		boolean setTheory) */
	
	
//	String symbols =  fParser.getInputPalette(); //+chTherefore;
	
	boolean lambda=false;boolean modal=false;boolean setTheory=false;
	
	String symbols =  fParser.getInputPalette(lambda,modal,setTheory); 
	
	if (symbols==null)
		symbols="";
/* Dec 09	
	fPalette= new Palette(symbols,fJournalPane);
	fPalette.setSize(new Dimension(300, 21));
	fPalette.setMaximumSize(new Dimension(300, 21));
	fPalette.setMinimumSize(new Dimension(300, 21));
	fPalette.setPreferredSize(new Dimension(300, 21));
*/	
	fSymbolToolbar= new SymbolToolbar(symbols,fJournalPane);
	
	
	 contentPane.add(/*fPalette*/fSymbolToolbar,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
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

  fComponentsPanel.setBackground(Color.lightGray);

 for (int i=0;i<components.length;i++){
			      fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
			         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
			    }
			}



JComponent [] readParamProofs(){
	JComponent[] components={};
   int i=0;

   String param= "proof"+i;	
	
   String value= getParameter(param);
	   while (value!=null&&i<10){
		   i++;
		   param= "proof"+i;
		   value= getParameter(param);
	   }
	   
	   
	if (i>0){   
	int count =i;
	   components= new JComponent[count];
	
	   i=0;
	
	   param= "proof"+i;
	   
	   String label="Proof";
	   
	   if (count>6)
		   label="Pf";     // we only fit 6, but we will squeeze a few more
		
       value= getParameter(param);
		   while (value!=null&&i<10){
			   components[i]=proofButton(label+(i+1),value);
			   i++;
			   param= "proof"+i;
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
				fProofPanel.startProof(filteredStr);
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
	
	//System.out.print("Hello " + inputStr);
	
	fProofPanel.startProof(inputStr);
}
	
/*
  String inputStr=readDualSource(TUtilities.logicFilter);//TUtilities.readSelectionToString(fJournalPane,TUtilities.logicFilter);

    //May 04 DO WE WANT LISP FILTER HERE?



    ((TMyProofPanel)(fDeriverDocument.fProofPanel)).startProof(inputStr);
 * */

 
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
         
     // focus!    System.out.println("writeToJournal() called in applet");

         if (messageLength>0) {

           fJournalPane.setSelectionStart(newCaretPosition);
           fJournalPane.setCaretPosition(newCaretPosition);    //leave existing selection and do everything after

           fJournalPane.replaceSelection(message);

           if (highlight) {
             fJournalPane.setSelectionStart(newCaretPosition);
             fJournalPane.setSelectionEnd(newCaretPosition+messageLength);
             
             fJournalPane.requestFocusInWindow();

           }

         }
      }	
	

}



/*
   JFrame aFrame=new JFrame("Show whether the formula is satisfiable ie make it true");
   TSatisfiable game =new TSatisfiable(aFrame,fDeriverDocument.getParser());

   aFrame.getContentPane().add(game);
     aFrame.setSize(500,230);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-230)/2);
      aFrame.setResizable(false);

      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();
   }
 * */
