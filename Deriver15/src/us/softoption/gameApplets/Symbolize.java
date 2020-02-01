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

import jscheme.InputPort;
import jscheme.SchemeUtils;
import us.softoption.editor.TEnglishToLogic;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.TFormula;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.TParser;

/* 
 * 
 */

public class Symbolize extends JApplet{
 TEnglishToLogic fEtoL;
 TParser fParser;
 JTextPane fJournalPane;
 HTMLEditorKit fEditorKit;
 JLabel label;
 JPanel fComponentsPanel;  //usually buttons
 boolean fNoCommands=false;
 Dimension fPreferredSize=new Dimension(500,300);
 boolean fPropChosen=false; // default to predicate level analysis for symbolization

 
	
	public void init(){
		setBackground( Color.lightGray ); // change here the applet window color
		
		Container contentPane=this.getContentPane();
		
		contentPane.setBackground(Color.lightGray);
		
		 Calendar cal= Calendar.getInstance();
		 long time=cal.getTimeInMillis();
		
		 int year=cal.get(Calendar.YEAR);
			
         if (year>TConstants.APPLET_EXPIRY){
	          JLabel label = new JLabel("The code for this applet expired in " +TConstants.APPLET_EXPIRY +" .");          contentPane.add(label);
		}
		else{			
			createGUI(contentPane);		
		}	
	}
	
	private void createGUI(Container contentPane){
   fEtoL=new TEnglishToLogic();
   //fEtoL.resetToDefaultRules();
   fParser =new TDefaultParser();
   fEtoL.resetToDefaultRules();
   
   
   fJournalPane = new JTextPane();
   fEditorKit = new HTMLEditorKit();
   fComponentsPanel = new JPanel();  //usually buttons
   
   fComponentsPanel.setBackground(Color.lightGray);
   
   label=new JLabel("Symbolization 1.0");

	
   String inputText= getParameter("inputText");
   String title= getParameter("title"); 
	
	if (title!=null)
      label = new JLabel(title);
	
   String commands= getParameter("commands");
   if (commands!=null&&commands.equals("noCommands"))
	   fNoCommands=true;
   
   String parser= getParameter("parser");
   if (parser!=null&&parser.equals("bergmann")){
	   fParser =new TBergmannParser();
	   fEtoL.resetToBergmannRules();
   }
   if (parser!=null&&parser.equals("copi")){
	   fParser =new TCopiParser();
	   fEtoL.resetToCopiRules();
   }
   if (parser!=null&&parser.equals("genzen")){
	   fParser =new TParser();
	   fEtoL.resetToGentzenRules();
   }
   if (parser!=null&&parser.equals("hausman")){
	   fParser =new THausmanParser();
	   fEtoL.resetToCopiRules();
   }
   if (parser!=null&&parser.equals("herrick")){
	   fParser =new THerrickParser();
	   fEtoL.resetToHerrickRules();
   }
   
   String level= getParameter("propLevel");
   if (level!=null&&level.equals("true"))
	   fPropChosen=true;
	
fEditorKit = new HTMLEditorKit();

fJournalPane.setEditorKit(fEditorKit);
fJournalPane.setDragEnabled(true);
fJournalPane.setEditable(true);
fJournalPane.setPreferredSize(fPreferredSize);
fJournalPane.setMinimumSize(new Dimension(300,200));

if (inputText!=null)
	fJournalPane.setText(inputText);
	    
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

if(fNoCommands){
	JComponent[] buttons= {toSymbolsButton(),toEnglishButton()};
	initializeComponentsPanel(buttons);
}
else{
	JComponent[] buttons= {toSymbolsButton(),toEnglishButton(),doCommandButton()};
	initializeComponentsPanel(buttons);
}

contentPane.add(fComponentsPanel,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
	       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
}

public void paint(Graphics g) {   // can see background properly in new Firefox

    	
    	super.paint(g);
 
    	g.drawRect(0, 0, 
     		   getSize().width - 1,
     		   getSize().height - 1);  	
    	

        }
	
	
	
	private void initializeComponentsPanel(JComponent [] components){
   fComponentsPanel.setPreferredSize(new Dimension(fPreferredSize.width,30));

	    fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons


	    for (int i=0;i<components.length;i++){
	      fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
	         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
	    }
	}

/******************** Buttons ***************************/

	private JButton doCommandButton(){
	JButton button = new JButton("Do Command");	    
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			doCommand();
		}});
	return
	   button;
}


	private JButton toEnglishButton(){
	JButton button = new JButton("To English");	    
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			toEnglish();
		}});
	return
	   button;
	
}


	private JButton toSymbolsButton(){
	JButton button = new JButton("To Symbols");	    
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			toSymbols();
		}});
	return
	   button;
}

	private void toEnglish() {

    String inputStr=readSource(TUtilities.defaultFilter);

      if ((inputStr!=null)&&(inputStr!=strNull)){

            translate(inputStr);   // translate back
      }
      else
          Toolkit.getDefaultToolkit().beep();

  }

	private void translate(String inputStr){


	 {StringReader aStringReader= new StringReader(inputStr);

	     if (aStringReader!=null) {

	       TFormula root = new TFormula();
	       boolean wellformed;
	       ArrayList dummy = new ArrayList();

	       wellformed=fParser.wffCheck(root, /*dummy,*/ aStringReader);

	       if (wellformed){
	         String translation=fEtoL.translateBack(root, fParser);

	         if (translation!=null)
	            writeOverJournalSelection(translation);
	         else
	            Toolkit.getDefaultToolkit().beep();
	       }
	       else
	           Toolkit.getDefaultToolkit().beep();

	      }}

	   }


/*we need to get the selection, translate it, write it back
 * 
 * */

	private void toSymbols(){
	String inputStr=readSource(TUtilities.peculiarFilter);

    if ((inputStr!=null)&&(inputStr!=strNull)){
    	
   
        inputStr="(" + inputStr + ")";  // we'll make it into a list so that the LISP parser can parse it

  /*      String test=fEtoL.symbolizeOneStep("(PHILOSOPHY IS HARD)", true);
        
        test=fEtoL.symbolizeOneStep("(philosophy is hard)", true); */
        
        String resultStr=fEtoL.symbolizeOneStep(inputStr,fPropChosen);

        if (resultStr!=null)
           writeOverJournalSelection(resultStr);
        else
           Toolkit.getDefaultToolkit().beep();
    }
    else
        Toolkit.getDefaultToolkit().beep();
}

/******************** End of Buttons ***************************/









/******************** Commands *************************/

/* Much of this comes from the Lisp bridge in Deriver  
 * We're reading the selection and not expecting opening
 * and closing brackets*/

	private void doCommand() {

    String rawInputStr=readSource(TUtilities.noFilter);

      if ((rawInputStr!=null)&&(rawInputStr!=strNull)){
    	  String inputStr="("+TUtilities.noReturnsFilter(rawInputStr)
                           + ")";  // we'll make it into a list so that the 
    	                           //LISP parser can parse it

            doCommandHelper(inputStr);
      }
      else
          Toolkit.getDefaultToolkit().beep();

  }


	private void doCommandHelper(String inputStr){
	

   boolean quoted = true;
         try {

		           InputPort input = new InputPort(new StringReader(inputStr));

		           {
		             Object x;
		             Object result;
		             String resultStr;
		             if (input.isEOF(x = input.read()))
		               return;

		               resultStr = SchemeUtils.stringify(x, !quoted);

		               int oneOfOurs= identify(resultStr);

		               if (oneOfOurs>-1){
		                  process(oneOfOurs, resultStr,x);

		            //     System.out.println(resultStr);
		               }
		             else{                                   // let lisp process it

		               result = fEtoL.getScheme().eval(x);

		               resultStr = SchemeUtils.stringify(result, !quoted);

		          //     System.out.println(resultStr);

		               writeToJournal("Scheme output: " + resultStr, true, false);
		             }

		           }
		         }
		       catch (Exception ex) {

		         writeToJournal("Scheme Exception: " + ex, true, false);

		     //    System.err.println("Scheme Exception: " + ex);
		        }

		 }

private int identify(String inputStr) {

    if (inputStr.indexOf("(assign true") == 0)
      return
          0;

    if (inputStr.indexOf("(assign false") == 0)
      return
          1;

    if (inputStr.indexOf("(remember adjective") == 0)
      return
          2;

    if (inputStr.indexOf("(remember iverb") == 0)
      return
          3;

    if (inputStr.indexOf("(remember proposition") == 0)
      return
          4;

    if (inputStr.indexOf("(remember name") == 0)
      return
          5;

    if (inputStr.indexOf("(remember noun") == 0)
      return
          6;

    if (inputStr.indexOf("(remember tverb") == 0)
      return
          7;

    if (inputStr.indexOf("(remember pverb") == 0)
      return
          8;

    if (inputStr.indexOf("(remember binadj") == 0)
      return
          9;

    if (inputStr.indexOf("(write propositions") == 0)
      return
          10;

    if (inputStr.indexOf("(all") == 0)
      return
          11;

    return
        -1;
  }


private void process(int type, String inputStr, Object lispInput){
    /*Some of these involve semantics (and these are passed to the
     semantics) and others involve symbolization (and these
 are passed to the LISP in EnglishToLogic*/

  /*it has the input as lisp and as string*/

  String message=null;


    switch (type){
  /*    case 0: if (assignTrue(inputStr))
            writeToJournal("OK", kHighlight, !kToMarker);
        break;

      case 1: if (assignFalse(inputStr))
            writeToJournal("OK", kHighlight, !kToMarker);
        break; */

      case 2:
         message= fEtoL.rememberAdjective(lispInput);
        break;

      case 3:
        message= fEtoL.rememberIVerb(lispInput);
       break;

      case 4:
         message= fEtoL.rememberProposition(lispInput);
        break;

      case 5:
       message= fEtoL.rememberName(lispInput);
      break;

      case 6:
        message= fEtoL.rememberNoun(lispInput);
       break;

      case 7:
      message= fEtoL.rememberTVerb(lispInput);
      break;

      case 8:
           message= fEtoL.rememberPVerb(lispInput);
          break;

      case 9:
                message= fEtoL.rememberBinAdj(lispInput);
               break;
 
      case 10:
         message= fEtoL.writeAssocList(fEtoL.lispEvaluate("gPropositions"));

        break;

      case 11:
        processAll(inputStr);
        break;


      default:

    }

    if (message!=null)
       writeOverJournalSelection(message);
    else
       Toolkit.getDefaultToolkit().beep();

  }


private void processAll(String inputStr) {

    /*This starts "(all and then there should be a bunch
      of other commands (cmd1 ) (cmd2) etc).

     We can pick them off by lISP, get their strings and let the
     command interpeter deal with them*/



    Object cdr=fEtoL.lispEvaluate("(cdr '"+inputStr+")"); //drop all
    Object car;
    String cdrStr;


    while (cdr!=null){
      cdrStr=cdr.toString();
      car=fEtoL.lispEvaluate("(car '"+cdrStr+")"); //get command, and interpet adds brackets
      doCommandHelper(car.toString());
      writeToJournal(strCR, !TConstants.HIGHLIGHT, !TConstants.TO_MARKER);  // different command outputs on new lines
      cdr=fEtoL.lispEvaluate("(cdr '"+cdrStr+")"); //drop command we've done

    }

}

/************************** End of Commands *********************************/

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

/*TEST

It is not the case that PHILOSOPHY IS HARD
PHILOSOPHY IS HARD and PHILOSOPHY IS INTERESTING
PHILOSOPHY IS HARD or PHILOSOPHY IS INTERESTING
if PHILOSOPHY IS HARD then PHILOSOPHY IS INTERESTING
PHILOSOPHY IS HARD if and only if PHILOSOPHY IS INTERESTING



"((PHILOSOPHY IS HARD) H)"
   +"((PHILOSOPHY IS INTERESTING) I)"
   +"((LOGIC IS HARD) L)"
   +"((LOGIC IS INTERESTING) M)"
   +"((WE RUN A WAR) W)"
   +"((WE REDUCE UNEMPLOYMENT) U)"
   +"((WE INCREASE HEALTH COSTS) C)"







*/