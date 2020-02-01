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

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chComma;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chLSqBracket;
import static us.softoption.infrastructure.Symbols.chRSqBracket;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import us.softoption.editor.TBergmannDocument;
import us.softoption.editor.TCopiDocument;
import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.THausmanDocument;
import us.softoption.editor.THerrickDocument;
import us.softoption.editor.TJournal;
import us.softoption.editor.TPreferences;
import us.softoption.infrastructure.SymbolToolbar;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TFlag;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.interpretation.TSemantics;
import us.softoption.interpretation.TShapePanel;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TFormula;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.TParser;

/* TJournal is an interface for writing to
 * 
 */


public class Interpretations extends JApplet implements TJournal{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	 
	
	 TParser fParser; 
	 
	 TDeriverDocument fDeriverDocument;
	 TShapePanel fShapePanel;
	
	 JTextPane fJournalPane;
	 HTMLEditorKit fEditorKit;
	 JLabel  fLabel=new JLabel("Interpretations");
	 JPanel fComponentsPanel;  //usually buttons
	 boolean fNoCommands=true;
	 Dimension fPreferredSize=new Dimension(500,800);
	 Dimension fDrawingSize=new Dimension(538,300);  //was 500 10 for magin  //was 498
	 Dimension fJournalSize=new Dimension(530,200);  //was 490
	 
	 Thread fSatisfiableThread=null;
	 
	 JTextField fPalette;
	 JLabel fPaletteLabel=new JLabel("Palette: ");
	 JPanel fPalettePanel;
	 
	 SymbolToolbar fSymbolToolbar;
	 
	 String fInputText="";
	 String fTitle="";
	 //String fParserType="gentzen";
	 String fMakeDrawing="";
	 boolean fUseIdentity=false; // for extended palettes
	 
	 boolean fBadXML=false;

	
	public void init(){
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
		 long time=cal.getTimeInMillis();
		
		if (false/*time>10*/){
          JLabel label = new JLabel("The code for this applet is out of date.");
          contentPane.add(label);
		}
		else{
			//TPreferences.resetToDefaults();
			
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
	
	
private void readParameters(){
		   fInputText= getParameter("inputText");
		   
		   fTitle= getParameter("title"); 
					
		   String commands= getParameter("commands");
		   if (commands!=null&&commands.equals("noCommands"))
			   fNoCommands=true;
		   
		   String parser= getParameter("parser");
		   if (parser!=null&&parser.equals("bergmann"))   //canonical
			   TPreferences.fParser="bergmann [copi gentzen hausman herrick]";
		   if (parser!=null&&parser.equals("copi"))   //canonical
			   TPreferences.fParser="copi [bergmann gentzen hausman herrick]";
		   if (parser!=null&&parser.equals("gentzen"))
	           TPreferences.fParser="gentzen [bergmann copi hausman herrick]";
		   if (parser!=null&&parser.equals("hausman"))
			   TPreferences.fParser="hausman [bergmann copi gentzen herrick]";	   
		   if (parser!=null&&parser.equals("herrick"))
			   TPreferences.fParser="herrick [bergmann copi gentzen hausman]";	   

		   
		 //  fParserType= getParameter("parser");
	   
		   fMakeDrawing= getParameter("makeDrawing");

		   {String identity= getParameter("identity"); 
		   
		//this adds extra palette items for drawing functions   
		   
		   if ((identity!=null)&&identity.equals("true"))
	          fUseIdentity= true; //TEMP
		   else
			  fUseIdentity= false;

		   } 
		
	}
	
private void createGUI(Container contentPane){

		readParameters();
		
		if (TPreferences.fParser.charAt(0)=='b'){//.equals("bergmann [copi gentzen hausman]"))
			   fParser =new TBergmannParser();
			      				
	    }
	    else if (TPreferences.fParser.charAt(0)=='c'){//.equals("copi [gentzen hausman]"))
		   fParser =new TCopiParser();
		      				
    }
       else if (TPreferences.fParser.charAt(0)=='h'&&
			TPreferences.fParser.charAt(1)=='a'	){//.equals("hausman [copi gentzen]"))
		   fParser =new THausmanParser();		      				
    }
	else if (TPreferences.fParser.charAt(0)=='h'&&
				TPreferences.fParser.charAt(1)=='e'){//.equals("hausman [copi gentzen]"))
			   fParser =new THerrickParser();		      				
	}
    else
        fParser =new TParser();
    
    
	    
		
		/******** not sure why I am creating parsers twice *********/
    
		  if (TPreferences.fParser.charAt(0)=='b'){  //bergmann
		         fParser =new TBergmannParser();
		         fDeriverDocument=new TBergmannDocument(this,fUseIdentity);
		      }
			else if (TPreferences.fParser.charAt(0)=='c'){  //copi
         fParser =new TCopiParser();
         fDeriverDocument=new TCopiDocument(this,fUseIdentity);
      }
	else
	if (TPreferences.fParser.charAt(0)=='h'&&
		TPreferences.fParser.charAt(1)=='a'){  //hausman
    	         fParser =new THausmanParser();
    	         fDeriverDocument=new THausmanDocument(this,fUseIdentity);
    	      }
	else
		if (TPreferences.fParser.charAt(0)=='h'&&
			TPreferences.fParser.charAt(1)=='e'){  //herrick
	    	        fParser =new THerrickParser();
	    	        fDeriverDocument=new THerrickDocument(this,fUseIdentity);
	    	      }
	
	
	else{
         fParser =new TParser();
         fDeriverDocument= new TDeriverDocument(this,fUseIdentity); //TEMP
      	}
				
	
      
     fShapePanel=fDeriverDocument.fShapePanel;
     
    // fShapePanel.setPreferredSize(new Dimension(640,480));    //default is 640x480
	  
     if ((fMakeDrawing!=null)&&!fMakeDrawing.equals("")){
    /*	System.out.print("Before: "+fMakeDrawing );
    	System.out.println("" );
    	System.out.print("After: "+TUtilities.expandXML(fMakeDrawing) );
    	System.out.println("" );
    	System.out.println("" ); */
    	 
        createDrawingFromXML(TUtilities.expandXML(fMakeDrawing)); // it may be compressed, expansion does not hurt if not
     }
	       
	       
		   fJournalPane = new JTextPane();
		   fEditorKit = new HTMLEditorKit();
		   fComponentsPanel = new JPanel();  //usually buttons
		  

		
 
		
		if (fTitle!=null&&!fTitle.equals(""))
		      fLabel = new JLabel(fTitle);
			
		fEditorKit = new HTMLEditorKit();

		fJournalPane.setEditorKit(fEditorKit);
		fJournalPane.setDragEnabled(true);
		fJournalPane.setEditable(true);
		fJournalPane.setPreferredSize(fJournalSize);
		fJournalPane.setMinimumSize(new Dimension(300,200));

		if (fInputText!=null)
			fJournalPane.setText(fInputText);
			    
		contentPane.setPreferredSize(fPreferredSize);
		contentPane.setMinimumSize(new Dimension(500,300));  

		contentPane.setLayout(new GridBagLayout());
        /*title*/
		contentPane.add(fLabel,new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 0, 0));

		JScrollPane aScroller=new JScrollPane(fShapePanel);
		aScroller.setPreferredSize(fDrawingSize);
		aScroller.setMinimumSize(new Dimension(300,200));
		/*drawing*/
		contentPane.add(aScroller,new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 2, 10, 0), 0, 0));

		aScroller=new JScrollPane(fJournalPane);
		aScroller.setPreferredSize(fJournalSize);
		aScroller.setMinimumSize(new Dimension(300,200));
		/*palette*/
		initializePalettePanel();

		contentPane.add(fSymbolToolbar/*fPalettePanel*/,new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));		
	
		/*journal*/
		contentPane.add(aScroller,new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 2, 10, 0), 0, 0));

		
 /*
JComponent[] components= {trueButton(),satisfiableButton()};

if (fUseIdentity){
   JComponent[] identityComponents= {trueButton()}; // don't use satisfiable
   components=identityComponents;
} */
 
 /*JComponent[] paramComponents =readParamProofs();    //nov 06 not sure that I do proofs with this applet
 
 if ((paramComponents.length)>0)
    	   components= paramComponents; */
			
 initializeComponentsPanel(makeComponents());
		
		/*buttons*/

		contentPane.add(fComponentsPanel,new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
		}


protected JComponent [] makeComponents(){  //subclasses override
	JComponent[] components= {trueButton(),satisfiableButton()};
/*  I'm still thinking about this
	if (fUseIdentity){
	   JComponent[] identityComponents= {trueButton()}; // don't use satisfiable
	   components=identityComponents;
	} */
return
 components;
}



private void initializeComponentsPanel(JComponent [] components){
  fComponentsPanel.setPreferredSize(new Dimension(fPreferredSize.width,30));

  fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons


 for (int i=0;i<components.length;i++){
			      fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
			         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
			    }
			}

private void initializePalettePanel(){

	
fPalettePanel= new JPanel();
fPalettePanel.setPreferredSize(new Dimension(fPreferredSize.width,30));

fPalettePanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons

fPalette=new JTextField(fDeriverDocument.fDefaultPaletteText);
fPalette.setEditable(false);

fPalettePanel.add(fPaletteLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
fPalettePanel.add(fPalette,new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

//String symbols =  fParser.getInputPalette(); //+chTherefore;


/*2015 getInputPalette() now has parameters	(boolean lambda,boolean modal,
boolean setTheory) */


//String symbols =  fParser.getInputPalette(); //+chTherefore;

boolean lambda=false;boolean modal=false;boolean setTheory=false;

String symbols =  fParser.getInputPalette(lambda,modal,setTheory); 




if (symbols==null)
	symbols="";

fSymbolToolbar= new SymbolToolbar(symbols,fJournalPane);

}

/* don't think I use this in this applet. This is semantics
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
		
       value= getParameter(param);
		   while (value!=null&&i<10){
			//   components[i]=proofButton("Proof"+(i+1),value);
			   i++;
			   param= "proof"+i;
			   value= getParameter(param);
		   }
	}
	   
	   
	   return 
	   components;
}

*/
	
/***********************  Buttons ***************************/
	
protected	JButton trueButton(){
		JButton button = new JButton("True?");	    
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				doTrue();
			}});
		return
		   button;
	}
	
protected	JButton satisfiableButton(){
		JButton button = new JButton("Satisfiable?");	    
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				doSatisfiable();
			}});
		return
		   button;
	}
	
/***********************  End of Buttons ***************************/

/***********************  True *************************************/	
private void doTrue(){
	String inputStr=readSource(TUtilities.defaultFilter);
	
	 if (fParser.containsModal(inputStr)){ // no modal   //May 08
		 fDeriverDocument.writeToJournal("Sorry, no modal operators", 
				 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
		   return
		       ;
	 }	
	
	 if (fParser.containsSubscripts(inputStr)){ //    //Dec 09
		 fDeriverDocument.writeToJournal("(*Sorry, no subscripts*)", 
				 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
		   return
		       ;
	 }	 
	 
	 
	 
	 
	TFormula root = prepare(inputStr);   // this also picks up the valuation

    if (root!=null){
	     fDeriverDocument.selectionTrue(root);

	     fDeriverDocument.clearValuation(); // removes valuation
	   }
}  
	
/*
 * 
 * void trueMenuItem_actionPerformed(ActionEvent e) {

   String inputStr=readDualSource(TUtilities.defaultFilter);

   TFormula root = prepare(inputStr);   // this also picks up the valuation

   if (root!=null){
     fDeriverDocument.selectionTrue(root);

     fDeriverDocument.clearValuation(); // removes valuation
   }




     ;
 }
 * */

	
/************************ Satisfiable ************************/

private void doSatisfiable()  {
	String inputStr=readSource(TUtilities.defaultFilter);
	
	 if (fParser.containsModal(inputStr)){ // no modal   //May 08
		 fDeriverDocument.writeToJournal("(*Sorry, no modal operators*)", 
				 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
		   return
		       ;
	 }
	 
	 if (fParser.containsSubscripts(inputStr)){ //    //Dec 09
		 fDeriverDocument.writeToJournal("(*Sorry, no subscripts*)", 
				 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
		   return
		       ;
	 }
	
	   stringSatisfiable(inputStr);
	  }

private int nodeSatisfiable(TTestNode aTestRoot, TTreeModel aTreeModel){

	  if (aTestRoot.closeSequent()){
	    aTestRoot.fClosed=true;
	    aTestRoot.fDead=true;
	  }

	  // aTestRoot.fStepsToExpiry=TTestNode.kMaxTreeDepth;

	  // int maxSteps=50;

	   return
	       aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);


	   // need to recheck here for ExCV


	}

protected void produceSatisfiableOutput(int argtype, TTestNode aTestRoot /*, ActionListener l*/ ){

 // used to try to call listener from thread when finished


  String outputStr="";

/*if (l!=null){
  ActionEvent e;
  l.actionPerformed(e);
} */


  switch (argtype){

       case TTestNode.valid:
         outputStr=strCR+"(*Not satisfiable.*)";
         break;

       case TTestNode.notValid:
         outputStr=strCR+"(*Satisfiable.*)";

         TTestNode openNode=aTestRoot.aNodeOpen();   // trying to avoid gOpenNode
         if (openNode!=null){
           ArrayList <TFormula> interpretationList= openNode.createInterpretationList();

         TFlag drawable=new TFlag(false);

         outputStr+=interpretationListToString(interpretationList,drawable);

         if (drawable.getValue()) {
           if (fDeriverDocument.fShapePanel.drawingIsClear()){
             if (freeInterpretFreeVariables(interpretationList)){ //the formulas may have free variables, this assigns them
               //to arbitrary cnstants
               outputStr+=strCR + "(*Interpretation drawn.*)";

        //       jTabbedPane1.setSelectedIndex(1);

               fDeriverDocument.constructDrawing(interpretationList);
             }
               else
                 outputStr+=strCR+"(*Interpretation cannot be drawn,"
                 +" cannot interpret all the free variables.*)";


             /*constructDrawing()*/
           }
           else
             outputStr+=strCR+"(*Interpretation can be drawn,"
             +" if the existing drawing is cleared.*)";


         }
         else
            outputStr+=strCR+"(*Interpretation not drawable.*)";

         }
         break;



         /*

            begin
                fDeriverDocument.WriteToJournal(concat(gCr, '(*Satisfiable.*)'), TRUE, FALSE);
                CreateInterpretationList;

                if MEMBER(gApplication.fLastCommand, TShapeClearCommand) then
                 gApplication.CommitLastCommand; {the idea here is that the}
          {							user may try to clear the previous drawing, but it is not really}
          {							cleared until committed}

                drawable := TRUE;

                WriteInterpretationList(drawable);

                if drawable then
                 begin
                 if (fDeriverDocument.fShapeList.fSize < 2) then
                 begin
                 fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation drawn.*)'), TRUE, FALSE);
                 ConstructDrawing;
                 end
                 else
                 fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation can be drawn, if the existing drawing is cleared.*)'), TRUE, FALSE);

                 end
                else
                 fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation not drawable.*)'), TRUE, FALSE);
               end;


      }
     */
       case TTestNode.notKnown:
         outputStr=strCR+"(*Not known whether satisfiable.*)";
         break;


     }
  fDeriverDocument.writeToJournal(outputStr, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
}


private boolean readSatisfiableInput(String inputStr, TTestNode aTestRoot){
    boolean wellFormed = true;
       int badChar=-1;
     ArrayList dummy=new ArrayList();

     ArrayList newValuation;

    StringTokenizer st = new StringTokenizer(inputStr, ",");

    while ((st.hasMoreTokens())&& wellFormed){

      inputStr = st.nextToken();

      if (inputStr != strNull) { // can be nullStr if they put two commas togethe,should just skip
        TFormula root = new TFormula();
        StringReader aReader = new StringReader(inputStr);

        newValuation = fDeriverDocument.fValuation;

        wellFormed = fDeriverDocument.getParser().wffCheck(root, newValuation,
            aReader);

        if (!wellFormed) {
          fDeriverDocument.writeToJournal(
              "(*You need to supply a list of well formed formulas"
              + " separated by commas. Next is what the parser has to"
              + " say about your errors.*)"
              + strCR
              + fDeriverDocument.getParser().fCurrCh + TConstants.fErrors12
              + fDeriverDocument.getParser().fParserErrorMessage,
              TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
        }
        else {

          badChar = fDeriverDocument.getParser().badCharacters(root);

          if (badChar == TParser.kNone) {
            aTestRoot.addToAntecedents(root);
            fDeriverDocument.fValuation = newValuation;
                /*notice here that we use only the last valuation for
             the whole thing */
          }
          else {

            writeBadCharacterErrors(badChar);
            return
              false;       // it is well formed, but we cannot deal with, say, '='
          }
        }
      }
   //   System.out.print(inputStr + " ");
    }

 return
     wellFormed;
}

/*********************************** Methods involving threads ********************************************************/

/*not sure which class this belongs in*/

private void stringSatisfiable(final String inStr){   //final so the thread can read it, but not change it

  String oldText=fLabel.getText();
  fLabel.setText("Working on Satisfiable...");
  fLabel.repaint();


  if (fSatisfiableThread!=null){         // we'll only do one of these at a time
    fSatisfiableThread.interrupt();
    fSatisfiableThread=null;
  }

  fSatisfiableThread= new Thread(){
    public void run(){


  //   String inputStr;
   final TFlag wellFormed= new TFlag(false);


   final TTestNode aTestRoot = new TTestNode(fDeriverDocument.getParser(),null);  //does not initialize TreeModel

   TTreeModel aTreeModel= new TTreeModel(aTestRoot.fTreeNode);

   aTestRoot.fTreeModel=aTreeModel;                                  //Tree Model initialized now
  // aTes


  // aTestRoot.startSatisfactionTree();
  aTestRoot.initializeContext(aTreeModel);

   Runnable read = new Runnable(){
     public void run (){
       String selectionStr =inStr;// String selectionStr = readDualSource(TUtilities.logicFilter);  //leave March 06
       wellFormed.setValue(readSatisfiableInput(selectionStr, aTestRoot));

     }
   };

   try{
     SwingUtilities.invokeAndWait(read);        // don't want text changed while reading
   }
   catch (Exception ex) {
      wellFormed.setValue(false);
   }

 /* synchronized (fJournalPane){
    String selectionStr = readDualSource(TUtilities.logicFilter);
    wellFormed = readSatisfiableInput(selectionStr, aTestRoot);
  }  */


if (wellFormed.getValue()){
       TFormula.interpretFreeVariables(fDeriverDocument.fValuation, aTestRoot.fAntecedents);

       // check on what I think about surgery here

       final int argtype= nodeSatisfiable(aTestRoot,aTreeModel);

       Runnable write  = new Runnable(){
  public void run (){
    produceSatisfiableOutput(argtype, aTestRoot);

  }
};

try{
  SwingUtilities.invokeAndWait(write);        // don't want text changed while reading
}
catch (Exception ex) {
   ;
}
            }

    }};   // end of new Thread/ run

  fSatisfiableThread.start();

  fLabel.setText(oldText);
  fLabel.repaint();
}




	
/*	
	
void readAndStart(){
	String inputStr=readSource(TUtilities.logicFilter);
	
	//fProofPanel.startProof(inputStr);
} */
	
/*
  String inputStr=readDualSource(TUtilities.logicFilter);//TUtilities.readSelectionToString(fJournalPane,TUtilities.logicFilter);

    //May 04 DO WE WANT LISP FILTER HERE?



    ((TMyProofPanel)(fDeriverDocument.fProofPanel)).startProof(inputStr);
 * */

 
 /**************************** Utilities *************************************/

private void createDrawingFromXML(String xmlStr) {
	
//	Rectangle dummy = new Rectangle();
//	Color dummyColor ;
	
	   // String fileName=null;
	
/*	System.out.print(xmlStr);
	System.out.println(""); */

	    if (false /*!okToAbandon()*/)           // check is the current file is dirty
	    {
	      fDeriverDocument.writeToJournal(
	               "(*You need to decide whether to save the document"
	               + " because drawing may well draw over"
	               + " the existing drawing.*)"
	               + strCR,
	               TConstants.HIGHLIGHT, !TConstants.TO_MARKER);

	      return;
	    }

	   byte[] input = xmlStr.getBytes();

	  //  try {

	      ByteArrayInputStream stream = new ByteArrayInputStream(input);

	      fBadXML = false;

	      BadXMLHandler myBadXMLSetter = new BadXMLHandler();  // sets fBadXML on exception

	      XMLDecoder d = new XMLDecoder(new BufferedInputStream(
	          stream), null, myBadXMLSetter);

	      if (fBadXML) {
	        fDeriverDocument.writeToJournal(
	               "(*The XML reader, used to read your command,"
	               + " has reported that some of the XML"
	               + " in your command is bad.*)"
	               + strCR,
	               TConstants.HIGHLIGHT, !TConstants.TO_MARKER);


	        d.close();

	      }
	      else {

	        try {Object shapeList = d.readObject();
	        
	        d.close();  //June 09

	        if (!fBadXML){

	          fDeriverDocument.fShapePanel.setShapeList( (ArrayList) shapeList);
	        }
	      }
	     catch (ArrayIndexOutOfBoundsException ex){
	
	             fDeriverDocument.writeToJournal(
	               "(*The Object creator, used to produce the,"
	               + "drawing from your XML command, has failed to"
	               + " produce the drawing. Typically this means"
	               + " that you have asked for something that cannot be done.*)"
	               + strCR,
	               TConstants.HIGHLIGHT, !TConstants.TO_MARKER);


	      }

	      }
	  }





private boolean freeInterpretFreeVariables(ArrayList interpretation){
	  /* What we have here is a consistent list of positive and negative atomic formulas,
	   which are true, we need
	     to pull out the atomic terms and make them the universe, then interpret the  predicates and relations
	 suitably
	But they may have free variables. Any new constant will do here*/

	//  String universe=TFormula.atomicTermsInListOfFormulas(interpretation);
	  
	    Set <String> universeStrSet=TFormula.atomicTermsInListOfFormulas(interpretation);
	    
	    String universe="";
	    
	    for (Iterator i=universeStrSet.iterator();i.hasNext();)
	    	universe+=i.next();
	  
	  char searchCh;
	  char constant;
	  ArrayList valuation=new ArrayList();
	  int n=1;
	  TFormula valuForm;

	  for (int i=0;i<universe.length();i++){

	    searchCh=universe.charAt(i);

	    if (TParser.isVariable(searchCh)){
	      constant=TParser.nthNewConstant(n,universe);
	      n+=1;
	      if (constant==' ')
	        return
	            false;
	      else{
	           valuForm=new TFormula((short)0,constant +"/" + searchCh,null,null);
	           /*the info on a valuation looks like this "a/x"
	              and we want to substitute the constant a for the variable
	              x throughout the formula*/
	           valuation.add(valuForm);
	           fDeriverDocument.writeToJournal(strCR
	                                   +"In the drawing, the object "
	                                   +constant
	                                   + " is "
	                                   + searchCh+"."
	                                   +strCR
	                                   , !TConstants.HIGHLIGHT, !TConstants.TO_MARKER);

	      }
	    }

	  }

	  if (valuation.size()>0)
	    TFormula.interpretFreeVariables(valuation, interpretation);  //surgery

	 return
	     true;
	}

private String interpretationListToString(ArrayList interpretation, TFlag drawable){
    drawable.setValue(true);
    TSemantics newSemantics =new TSemantics();
    
    Set <String> outputStrSet=TFormula.atomicTermsInListOfFormulas(interpretation);
    
    String outputStr="";
    
    for (Iterator i=outputStrSet.iterator();i.hasNext();)
    	outputStr+=i.next();

   // String outputStr=TFormula.atomicTermsInListOfFormulas(interpretation);

    if (outputStr.length()>12)
       drawable.setValue(false);//{check, override for more with esl only a ..m available}


    outputStr=TUtilities.separateStringWithCommas(outputStr);

    /*fDeriverDocument.writeToJournal(strCR
                                    +"Universe= { "
                                    +outputStr
                                    + " }"
                                    +strCR
                                    , TConstants.HIGHLIGHT, !TConstants.TO_MARKER);*/

    String firstStr= strCR
                     +"Universe= { "
                     +outputStr
                     + " }"
                     +strCR;

    String secondStr="";

    outputStr=TFormula.trueAtomicFormulasInList(interpretation);

    if (outputStr.length()>0){
      drawable.setValue(false);                   //cannot draw a true proposition
      outputStr = TUtilities.separateStringWithCommas(outputStr);
      secondStr="True Propositions= { "
                                      + outputStr
                                      + " }"
                                      + strCR;
    }

    String thirdStr="";

    outputStr=TFormula.falseAtomicFormulasInList(interpretation);

    if (outputStr.length()>0){
      drawable.setValue(false);                 //cannot draw a false proposition
      outputStr = TUtilities.separateStringWithCommas(outputStr);
      thirdStr="False Propositions= { "
                                      + outputStr
                                      + " }"
                                      + strCR;
    }


 String fourthStr="";

 int length = TParser.gPredicates.length();
 String predicate;

 int count=0;

 for (int i=0;i<length;i++){
   predicate=TParser.gPredicates.substring(i,i+1);

   outputStr=TFormula.extensionOfUnaryPredicate(interpretation, predicate);

   if ((outputStr!=null)&&(outputStr.length()>0)){
      count+=1;
      outputStr = TUtilities.separateStringWithCommas(outputStr);
      fourthStr+=predicate
                                      + " = { "
                                      + outputStr
                                      + " }"
                                      + strCR;
   }
 }

 if (count>3)
   drawable.setValue(false); //we can draw no more than 3 unary

 /*
     tempStr := '';
      tempInfo := 'A'; {used to be superone}
      found := FALSE;

      count := 2;
      tempCh := 'A';
      while tempCh <= 'Z' do
       begin
        found := FALSE;
        tempStr := '';
        tempInfo[1] := tempCh;
        fInterpretationList.Each(CheckUnary);
        if found then
         begin
          if drawable then
           begin
           if count < 5 then
           begin
           fNewInterpretation[count] := concat(tempCh, tempStr);
                                     {the first char is a label to indicate which property}
           count := count + 1;
           end
           else
           drawable := FALSE;

           end;

          lengthofStr := length(tempStr);

          while lengthofStr > 1 do
           begin
           insert(',', tempStr, lengthofStr);
           lengthofStr := lengthofStr - 1;
           end;

          tempStr := concat(tempInfo, '= { ', tempStr, ' }', gCr);

          fDeriverDocument.WriteToJournal(tempStr, FALSE, FALSE);
         end;

        tempCh := chr(ord(tempCh) + 1);

       end;


 */

 String fifthStr="";

 count=0;

 for (int i=0;i<length;i++){
   predicate=TParser.gPredicates.substring(i,i+1);

   outputStr=TFormula.extensionOfBinaryPredicate(interpretation, predicate);

   if ((outputStr!=null)&&(outputStr.length()>0)){
     count+=1;
      outputStr = TUtilities.intoOrderedPairs(outputStr);
      fifthStr+=predicate
                                      + " = { "
                                      + outputStr
                                      + " }"
                                      + strCR;
   }

 }

 if (count>3)
   drawable.setValue(false); //we can draw no more than 3 binary



 /*
    incomplete := FALSE;
      gTestRoot.fAntecedents.Each(CheckTrue);

      if incomplete then (*partial interpretation --cycle*)
       begin
        fDeriverDocument.WriteToJournal(concat(gCr, 'Partial interpretation only-- program has used an infinite universe.'), FALSE, FALSE);
        fDeriverDocument.WriteToJournal(concat(gCr, 'Yet it knows there is a finite universe version.'), FALSE, FALSE);
        fDeriverDocument.WriteToJournal(concat(gCr, 'You should form a cycle of relations to complete.'), FALSE, FALSE);

       end;


      if drawable then
       InterpretFreeVariables; {cannot have x's etc in drawing}


 */


//NEED THE EXTRA BIT HERE ABOUT CYCLES



     return
         firstStr
                                    +secondStr
                                    +thirdStr
                                    +fourthStr
                                    +fifthStr;



         }



/* OLD CODE DEC 2013
private ArrayList myTokenizer(String inputStr){
	  ArrayList outputList=new ArrayList();

	  int comma,leftBracket,rightBracket;

	  if (inputStr.length()==0)
	    return
	       outputList;
	  else{

	     comma=inputStr.indexOf(chComma);
	     leftBracket=inputStr.indexOf(chLSqBracket);
	     rightBracket=inputStr.indexOf(chRSqBracket);

	     //we don't want to find any commas between the brackets

	     while ((leftBracket<comma)&&(comma<rightBracket)&&(comma!=-1)){
	        comma=inputStr.indexOf(chComma,comma+1);  //to next comma
	     }


	     if (comma==-1){                  // no commas, whole lot goes in
	        outputList.add(inputStr);
	        return
	           outputList;
	     }
	     else{                            // comma, not between brackets
	       outputList=myTokenizer(inputStr.substring(comma+1));  //tokenize tail

	       String insert=inputStr.substring(0,comma);          // miss the comma

	       outputList.add(0,insert);                            // prepend new one

	       return
	         outputList;

	     }

	  }
	}
*/
	   private ArrayList<String> myTokenizer(String inputStr){		   
		   
	   	  ArrayList<String> outputList=new ArrayList<String>();

	   	  int nested=0;
	   	  int nestedSq=0;

	   	  if (inputStr.length()==0)
	   	    return
	   	       outputList;
	   	  else{
	   		  char currCh;
	   		  for (int i=0;i<inputStr.length();i++){
	   			currCh=inputStr.charAt(i);
	   			
	   			if (currCh==chSmallLeftBracket)
	   				nested++;
	   			if (currCh==chSmallRightBracket)
	   				nested--;
	   			if (currCh==chLSqBracket)
	   				nestedSq++;
	   			if (currCh==chRSqBracket)
	   				nestedSq--;
	   		  
	   			if ((nested==0)&&          
	   				(nestedSq==0)&&
	   				(currCh==chComma)){    //commas separating the list of premises are not nested
	   				                       // found acomma, not between brackets
	   		   	       outputList=myTokenizer(inputStr.substring(i+1));  //tokenize tail

	   		   	       String insert=inputStr.substring(0,i);          // miss the comma

	   		   	       outputList.add(0,insert);                            // prepend new one

	   		   	       return
	   		   	         outputList;
	   			}
	   		  }
	   		  
	   		  //if we get to here, no commas left
	   		  
	   	        outputList.add(inputStr);  //all remainder goes in
	   	        return
	   	           outputList;
	   	  }
	   		  
/* OLD CODE

	   	     comma=inputStr.indexOf(chComma);
	   	     leftSqBracket=inputStr.indexOf(chLSqBracket);
	   	     rightSqBracket=inputStr.indexOf(chRSqBracket);

	   	     //we don't want to find any commas between the brackets

	   	     while ((comma!=-1)&&   //comma there
	   	    		 ((leftSqBracket<comma)&&(comma<rightSqBracket))||)
	   	     {
	   	        comma=inputStr.indexOf(chComma,comma+1);  //to next comma
	   	     }


	   	     if (comma==-1){                  // no commas, whole lot goes in
	   	        outputList.add(inputStr);
	   	        return
	   	           outputList;
	   	     }
	   	     else{                            // comma, not between brackets
	   	       outputList=myTokenizer(inputStr.substring(comma+1));  //tokenize tail

	   	       String insert=inputStr.substring(0,comma);          // miss the comma

	   	       outputList.add(0,insert);                            // prepend new one

	   	       return
	   	         outputList;

	   	     }

	   	  } */
	   	}


/* Prepare tries to parse a comma separated list of formulas and assembles them
into one large conjunct. It also checks that every constant that appears is also
in the drawing.

*/

protected TFormula prepare(String selection){
 TFormula tempRoot = null;
 TFormula leftRoot = null;
 boolean wellFormed=true;
 String inputStr=null;

 //do I need to clear the valuation?

 String badConstants=null;

 if ((selection==null)||selection==strNull) // no selection
   return
       null;

 if (fParser.containsModal(selection)){ // no modal   //May 08
	 fDeriverDocument.writeToJournal("Sorry, no modal operators", 
			 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
	   return
	       null;
 }
 
 ArrayList formulas=myTokenizer(selection);

 Iterator iter=formulas.iterator();

 while ((iter.hasNext())&&wellFormed){
   inputStr=(String)iter.next();

   if (inputStr!=strNull){   // can be nullStr if input starts with comma, or they put two commas togethe,should just skip
      /*TFormula */ leftRoot = new TFormula();
      StringReader aReader = new StringReader(inputStr);

      wellFormed=fDeriverDocument.getParser().wffCheck(leftRoot, fDeriverDocument.fValuation, aReader);
      /*the different types of document will have different parsers*/

      //this can use a different valuation for each formula

      if (!wellFormed)
         fDeriverDocument.writeToJournal(fDeriverDocument.getParser().fCurrCh +
                                             TConstants.fErrors12 +
                                             fDeriverDocument.getParser().fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
      else{
         if (tempRoot==null){  // first in list
            tempRoot=leftRoot;
            leftRoot = null;
            }
         else{

            TFormula andRoot = new TFormula();

            andRoot.fKind = TFormula.binary;
            andRoot.fInfo = String.valueOf(chAnd);
            andRoot.fLLink = leftRoot;
            andRoot.fRLink = tempRoot;

            tempRoot=andRoot;
            andRoot=null;
         }

         //fDeriverDocument.fValuation=newValuation;

         badConstants= fDeriverDocument.constantsNotReferring(tempRoot);

         if ((badConstants!=strNull)&&!tempRoot.isSpecialPredefined()){
           wellFormed=false;

           fDeriverDocument.writeToJournal(strCR +
                                             "(*You should have an object " +
                                             badConstants+
                                             " in the Universe*)"+
                                             strCR +
                                             "(*for the constant named " +
                                             badConstants+
                                             " to refer to*)"+
                                             strCR
                                             , TConstants.HIGHLIGHT, !TConstants.TO_MARKER);

             //MORE TO COME

         }

       }
    }
  //newValuation.clear();

  }     //END of while

 if (wellFormed)
   return
     tempRoot;
 else
   return
       null;
}











protected String readSource(int filter){

 	return
 	   TSwingUtilities.readSelectionToString(fJournalPane,filter);
 		}

private void writeBadCharacterErrors(int kind){
	   switch (kind) {

	     case TParser.kEquality:
	       fDeriverDocument.writeToJournal("(*Sorry, the semantics for = has not yet been implemented.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
	       ;
	   break;

	     case TParser.kUnique:
	       fDeriverDocument.writeToJournal("(*Sorry, the semantics for "
	                                   + chUnique

	                                   +" has not yet been implemented.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
	   ;
	     break;

	       case TParser.kHighArity:
	        fDeriverDocument.writeToJournal("(*Sorry, relations have to be of arity 2 or less.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
	    ;
	     break;

	       case TParser.kCompoundTerms:
	              fDeriverDocument.writeToJournal("(*Sorry, the semantics for compound terms has not yet been implemented.*)", TConstants.HIGHLIGHT, !TConstants.TO_MARKER)
	          ;
	      break;

	   }

	 } 



public void writeHTMLToJournal(String message,boolean append){
	// haven't written it yet
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

           if (toMarker){                   //looking after 1 before caret -- caret may be end and we want to remove marker on end
               int posMarker=findMarker();
               if(posMarker!=-1){
                 fJournalPane.setSelectionStart(posMarker);
                 fJournalPane.setSelectionEnd(posMarker+1);

               }
               }
        
           fJournalPane.replaceSelection(message);

           if (highlight) {
             fJournalPane.setSelectionStart(newCaretPosition);
             fJournalPane.setSelectionEnd(newCaretPosition+messageLength);
           }
           
           fJournalPane.requestFocus();  //focus usually lost to button that launches

         }
      }	
	
 int findMarker(){
	 /*want to find the first marker, usually >, after the selection*/

	 /*WARNING You have to go  careful here because the underlying text is html, so
	 if the journal contains 'Cat' and you String text=fJournalPane.getText();
	you are liable to get '<html>Cat</html>' back, which is more than the
	3 characters you started with
	Go via the document*/

	 String text;
	 int caretPos = fJournalPane.getSelectionEnd(); //if there isn't one it's dot which is the old one
	 int searchStart=0;

	 if (caretPos!=0)
	   searchStart=caretPos-1; //can be EOF, and we want to find that


	 Document theDoc=fJournalPane.getDocument();

	 try{text=theDoc.getText(0,theDoc.getLength());
	 }
	 catch (BadLocationException ex){
	  return
	      -1;
	 }
	return
	     text.indexOf(chInsertMarker,searchStart); //can be EOF, and we want to find that
	 }
 
 
 
 
 
 
 class BadXMLHandler implements ExceptionListener{

     public void exceptionThrown  (Exception e){
 fBadXML=true;
     }

      }
 
 
 
}

/*  Test stsring
 * 
 * 
 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?><java version=\"1.4.2_05\" class=\"java.beans.XMLDecoder\"><oca> <vma> <ocp> <vpx> <i>177</i> </v> <vpy> <i>108</i> </v> <vpbr> <ocr> <i>177</i> <i>108</i> <i>114</i> <i>81</i> </o> </v> <vpc> <occ> <i>255</i> <i>0</i> <i>0</i> <i>255</i> </o> </v> <vpn> <c>G</c> </v> <vps> <b>false</b> </v> </o> </v> <vma> <ocp> <vpx> <i>68</i> </v> <vpy> <i>91</i> </v> <vpbr> <ocr> <i>68</i> <i>91</i> <i>71</i> <i>80</i> </o> </v> <vpn> <c>F</c> </v> <vps> <b>false</b> </v> </o> </v> <vma> <oci> <vpx> <i>189</i> </v> <vpy> <i>132</i> </v> <vpn> <c>b</c> </v> <vps> <b>false</b> </v> </o> </v> <vma> <oci> <vpx> <i>109</i> </v> <vpy> <i>120</i> </v> <vpn> <c>a</c> </v> <vps> <b>false</b> </v> </o> </v> <vma> <ocb> <vpbr> <ocr> <i>5</i> <i>0</i> <i>150</i> <i>50</i> </o> </v> <vpss> <ocs/> </v> </o> </v> </o></java>
 * 
 * 
 * 
 * long version
 * 
 * <?xml version=\"1.0\" encoding=\"UTF-8\"?><java version=\"1.4.2_05\" class=\"java.beans.XMLDecoder\"><object class="java.util.ArrayList"> <void method="add"> <object class="interpretation.TProperty"> <void property="XCoord"> <int>177</int> </void> <void property="YCoord"> <int>108</int> </void> <void property="boundsRect"> <object class="java.awt.Rectangle"> <int>177</int> <int>108</int> <int>114</int> <int>81</int> </object> </void> <void property="color"> <object class="java.awt.Color"> <int>255</int> <int>0</int> <int>0</int> <int>255</int> </object> </void> <void property="name"> <char>G</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TProperty"> <void property="XCoord"> <int>68</int> </void> <void property="YCoord"> <int>91</int> </void> <void property="boundsRect"> <object class="java.awt.Rectangle"> <int>68</int> <int>91</int> <int>71</int> <int>80</int> </object> </void> <void property="name"> <char>F</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TIndividual"> <void property="XCoord"> <int>189</int> </void> <void property="YCoord"> <int>132</int> </void> <void property="name"> <char>b</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TIndividual"> <void property="XCoord"> <int>109</int> </void> <void property="YCoord"> <int>120</int> </void> <void property="name"> <char>a</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TInterpretationBoard"> <void property="boundsRect"> <object class="java.awt.Rectangle"> <int>5</int> <int>0</int> <int>150</int> <int>50</int> </object> </void> <void property="semantics"> <object class="interpretation.TSemantics"/> </void> </object> </void> </object></java>
 * 
 *  without escapes
 *  * <?xml version="1.0" encoding="UTF-8"?><java version="1.4.2_05" class="java.beans.XMLDecoder"><object class="java.util.ArrayList"> <void method="add"> <object class="interpretation.TProperty"> <void property="XCoord"> <int>177</int> </void> <void property="YCoord"> <int>108</int> </void> <void property="boundsRect"> <object class="java.awt.Rectangle"> <int>177</int> <int>108</int> <int>114</int> <int>81</int> </object> </void> <void property="color"> <object class="java.awt.Color"> <int>255</int> <int>0</int> <int>0</int> <int>255</int> </object> </void> <void property="name"> <char>G</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TProperty"> <void property="XCoord"> <int>68</int> </void> <void property="YCoord"> <int>91</int> </void> <void property="boundsRect"> <object class="java.awt.Rectangle"> <int>68</int> <int>91</int> <int>71</int> <int>80</int> </object> </void> <void property="name"> <char>F</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TIndividual"> <void property="XCoord"> <int>189</int> </void> <void property="YCoord"> <int>132</int> </void> <void property="name"> <char>b</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TIndividual"> <void property="XCoord"> <int>109</int> </void> <void property="YCoord"> <int>120</int> </void> <void property="name"> <char>a</char> </void> <void property="selected"> <boolean>false</boolean> </void> </object> </void> <void method="add"> <object class="interpretation.TInterpretationBoard"> <void property="boundsRect"> <object class="java.awt.Rectangle"> <int>5</int> <int>0</int> <int>150</int> <int>50</int> </object> </void> <void property="semantics"> <object class="interpretation.TSemantics"/> </void> </object> </void> </object></java>

 * 
 * Empty drawing
 * <java version="1.5.0_06" class="java.beans.XMLDecoder"> 
 <oca> 
  <vma> 
   <ocb> 
    <vpss> 
     <ocs/> 
    </v> 
   </o> 
  </v> 
 </o> 
</java> 
 * 
 * 
 * 
 */

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
