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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import us.softoption.editor.TDeriverDocument;
//import us.softoption.proofs.TRewrite;
import us.softoption.infrastructure.TUtilities;
import us.softoption.proofs.TMyProofPanel;
import us.softoption.proofs.TProofPanel;

public class TAppletRandomProofPanel extends JPanel{


/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
TProofPanel/*TMyProofPanel*/ fProofPanel=null;	 //host? initialized in constructor
TDeriverDocument fDeriverDocument= null;
	
Dimension fPreferredSize=new Dimension(600,400);	
	
	
	// TConfCodeEntry fCodeEntry=new TConfCodeEntry("Proof 1 Code: "); // not used
  JTextArea fSetProof;
  //TBrowser fBrowser;            //so we can start proofs automatically

  /*
  JButton fStart=new JButton(new AbstractAction("Start Proof"){
   public void actionPerformed(ActionEvent e){
   //fBrowser.getCurrentProofPanel().startProof(TUtilities.logicFilter(fSetProof.getText()));
    fProofPanel.startProof(TUtilities.logicFilter(fSetProof.getText()));  
   }}); */

  JButton fConfirm=new JButton(new AbstractAction("Write Confirmation Code"){
   public void actionPerformed(ActionEvent e){
    // fCodeEntry.setCode(fBrowser.getCurrentProofPanel().produceConfirmationMessage());
   ;}});


  public static final int [] typeRange={0,18}; // needs to match the range as some calling routines range check
  public static final int AndIENegEOrI=1;  // the range of these numbers is important as some calling routines range check
  public static final int ImplicEEquivE=2;
  public static final int ImplicI=3;
  public static final int NegI=4;
  public static final int OrEEquivI=5;

  public static final int TwelveLineProp=6;
  public static final int PredNoQuant=7;
   public static final int SimpleUI=8;
   public static final int SimpleUG=9;
   public static final int SimpleEG=10;
   public static final int SimpleEI=11;
   public static final int TenLinePred=12;
   public static final int SimpleAndIENegEOrI=13;
   public static final int SimpleImplicEEquivE=14;
   public static final int SimpleNegI=15;
   public static final int AnyAnyLevel=16;
   public static final int Identity=17;


 /* This almost always wants to have its Deriver document because the undoable proof
   edits set the fDirty field of the document */ 
   
 public TAppletRandomProofPanel(TDeriverDocument aDocument,String label,int type){  /*TBrowser itsBrowser,*/

	 fDeriverDocument=aDocument;
	 
	 fProofPanel=supplyProofPanel(fDeriverDocument);
	 
	 // fBrowser=itsBrowser;

  // fCodeEntry=new TConfCodeEntry(label);

   setLayout(new GridBagLayout());
   
   JLabel aLabel=new JLabel("Derive this:",SwingConstants.LEFT);

   add(aLabel,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

   
	JScrollPane aScroller=new JScrollPane(fProofPanel);
	aScroller.setPreferredSize(fPreferredSize);
	aScroller.setMinimumSize(new Dimension(500,300));
   
 add(aScroller);
 
 add(aScroller,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
	       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));


   String proofStr=produceAProof(type);

   fSetProof=new JTextArea(proofStr);
   fSetProof.setLineWrap(true);
 fSetProof.setWrapStyleWord(true);
 fSetProof.setFont(new Font("Sans-Serif",Font.PLAIN,12));


   fSetProof.setEditable(false);
 }
 
 TProofPanel/*TMyProofPanel*/ supplyProofPanel(TDeriverDocument aDocument){  //for subclass override
	 return
	 new TMyProofPanel(aDocument);	 
 }


 public String getSetProof(){
   return
       TUtilities.logicFilter(fSetProof.getText());
 }
 
 
 public void startProof(String proof){
	fProofPanel.startProof(TUtilities.logicFilter(proof));
}

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
 
public void pruneMenus(boolean forTest,boolean removeAdvanced){
	 if (removeAdvanced)
		 fProofPanel.removeAdvancedMenu();
	 if (forTest)
		 fProofPanel.removeDeriveSupport();
	 
     fProofPanel.removeConfCodeWriter();  //do these three anyway, not relevant
     fProofPanel.removeMarginMenuItem();
     fProofPanel.removeWriteProofMenuItem();
 }
 

 public String produceConfirmationMessage(){
   return
   fProofPanel.produceConfirmationMessage();// fCodeEntry.getCode();
 }

 public String produceProofStr(){
	if (fProofPanel.finishedAndNoAutomation())
	   return
	   fProofPanel.getProofStr();
	else
		return
		"Cannot confirm yet.";
	 }
 
}
