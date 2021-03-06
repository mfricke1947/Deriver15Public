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

package us.softoption.games;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import us.softoption.infrastructure.TUtilities;
import us.softoption.proofs.TProofPanel;

public class TRandomProofPanel extends JPanel{

  TConfCodeEntry fCodeEntry=new TConfCodeEntry("Proof 1 Code: "); // not used
  JTextArea fSetProof;
  //TBrowser fBrowser;            //so we can start proofs automatically

  TProofPanel fProofPanel;

  JButton fStart=new JButton(new AbstractAction("Start Proof"){
   public void actionPerformed(ActionEvent e){
 //  fBrowser.getCurrentProofPanel().startProof(TUtilities.logicFilter(fSetProof.getText()));
  fProofPanel.startProof(TUtilities.logicFilter(fSetProof.getText()));

 }});

  JButton fConfirm=new JButton(new AbstractAction("Write Confirmation Code"){
   public void actionPerformed(ActionEvent e){
     //fCodeEntry.setCode(fBrowser.getCurrentProofPanel().produceConfirmationMessage());
     fCodeEntry.setCode(fProofPanel.produceConfirmationMessage());
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

   TRandomProof fRandomProof; //the proof strings to use


 public TRandomProofPanel(TProofPanel itsProofPanel,String label,int type, TRandomProof aRandomProof){

   //fBrowser=itsBrowser;

   fProofPanel =itsProofPanel;

   fCodeEntry=new TConfCodeEntry(label);

   fRandomProof=aRandomProof;

   setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

   add(Box.createVerticalStrut(16));

   JPanel topPanel=new JPanel();

   topPanel.setLayout(new BorderLayout());

   JLabel aLabel=new JLabel("Give a proof of this, then Write Confirmation Code:",SwingConstants.LEFT);

   topPanel.add(aLabel,BorderLayout.WEST);

   add(topPanel);

   add(Box.createVerticalStrut(16));

   topPanel=new JPanel();

   topPanel.setLayout(new BorderLayout());

   String proofStr=produceAProof(type);

   fSetProof=new JTextArea(proofStr);
   fSetProof.setLineWrap(true);
 fSetProof.setWrapStyleWord(true);
 fSetProof.setFont(new Font("Sans-Serif",Font.PLAIN,12));


   fSetProof.setEditable(false);

   topPanel.add(fSetProof);

   add(topPanel);

   add(Box.createVerticalStrut(16));

/*   JTextArea someText=new JTextArea("Copy it, 'Start Proof' from 'Actions Menu', do the Proof,"
                       + " write the Confirmation Code"
                       + strCR
                       +"using the 'Edit+ Menu', copy and paste the Code below");
  someText.setEditable(false);
  someText.setLineWrap(true);
someText.setWrapStyleWord(true);
 someText.setFont(new Font("Sans-Serif",Font.PLAIN,12));


   add(someText);  */

   JPanel buttonPanel=new JPanel(new BorderLayout());



 buttonPanel.add(fStart,BorderLayout.EAST);
 buttonPanel.add(fConfirm,BorderLayout.WEST);

 add(buttonPanel);

 //set default button?

   add(Box.createVerticalStrut(16));

   add(fCodeEntry);

   add(Box.createVerticalStrut(32));


 }

 JButton getStartButton(){
   return
       fStart;
 }

 String getSetProof(){
   return
       TUtilities.logicFilter(fSetProof.getText());
 }

/* static*/ String produceAProof(int type){
   String returnStr="";
   String [] possibles;

   switch (type) {
     case SimpleAndIENegEOrI:

       possibles=fRandomProof.randomSimpleNegEandEIorI(1);

       if (possibles!=null&&possibles.length>0)
          returnStr=possibles[0];
       break;
     case SimpleImplicEEquivE:

possibles=us.softoption.games.TRandomProof.randomSimpleImplicEEquivE (1);

if (possibles!=null&&possibles.length>0)
   returnStr=possibles[0];
break;

     case AndIENegEOrI:

        possibles=fRandomProof.randomNegEandEIorI(1);

        if (possibles!=null&&possibles.length>0)
           returnStr=possibles[0];
        break;

    case ImplicEEquivE:

        possibles=fRandomProof.randomImplicEEquivE(1);

        if (possibles!=null&&possibles.length>0)
           returnStr=possibles[0];
        break;
      case ImplicI:

          possibles=fRandomProof.randomImplicI(1);

          if (possibles!=null&&possibles.length>0)
             returnStr=possibles[0];
          break;
        case SimpleNegI:

              possibles=fRandomProof.randomSimpleNegI(1);

              if (possibles!=null&&possibles.length>0)
                 returnStr=possibles[0];
              break;
            case NegI:

                  possibles=fRandomProof.randomNegI(1);

                  if (possibles!=null&&possibles.length>0)
                     returnStr=possibles[0];
                  break;

        case OrEEquivI:

                    possibles=fRandomProof.randomOrEEquivI(1);

                    if (possibles!=null&&possibles.length>0)
                       returnStr=possibles[0];
        break;

      case TwelveLineProp:

                  possibles=fRandomProof.randomTwelveLineProp(1);

                  if (possibles!=null&&possibles.length>0)
                     returnStr=possibles[0];
      break;

    case PredNoQuant:

                possibles=fRandomProof.randomPredNoQuant(1);

                if (possibles!=null&&possibles.length>0)
                   returnStr=possibles[0];
    break;

  case SimpleUI:

              possibles=fRandomProof.randomSimpleUI(1);

              if (possibles!=null&&possibles.length>0)
                 returnStr=possibles[0];
    break;

  case SimpleUG:

                possibles=fRandomProof.randomSimpleUG(1);

                if (possibles!=null&&possibles.length>0)
                   returnStr=possibles[0];
    break;

  case SimpleEG:

                possibles=fRandomProof.randomSimpleEG(1);

                if (possibles!=null&&possibles.length>0)
                   returnStr=possibles[0];
    break;

  case SimpleEI:

                   possibles=fRandomProof.randomSimpleEI(1);

                   if (possibles!=null&&possibles.length>0)
                      returnStr=possibles[0];
     break;

   case TenLinePred:

                      possibles=fRandomProof.randomTenLinePred(1);

                      if (possibles!=null&&possibles.length>0)
                         returnStr=possibles[0];
     break;



   case AnyAnyLevel:

                   possibles=fRandomProof.randomAnyAnyLevel(1);

                   if (possibles!=null&&possibles.length>0)
                      returnStr=possibles[0];
  break;

case Identity:

              possibles=fRandomProof.randomIdentity(1);

              if (possibles!=null&&possibles.length>0)
                 returnStr=possibles[0];
break;




}


   return
       returnStr;
 }

 public String getConfirmationCode(){
   return
       fCodeEntry.getCode();
 }


}
