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


import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.softoption.proofs.TProofPanel;


public class TProofQuiz2 extends JFrame{

  static public int fNumOpen=0;

  //TBrowser fBrowser;
  TProofPanel fProofPanel;
  TRandomProof fRandomProof;


  JTabbedPane fTabs= new JTabbedPane();

  TRandomProofPanel fProof1;
  TRandomProofPanel fProof2;


  JTextArea code;
  JScrollPane codeScroller;
  JPanel fFinishPanel;


  public TProofQuiz2(TProofPanel itsProofPanel, TRandomProof itsRandomProof,
                    String title){



  super (title);

  //fBrowser=itsBrowser;
  fProofPanel=itsProofPanel;
  fRandomProof=itsRandomProof;

  fNumOpen+=1;

  setSize(600,300);
 // setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-300)/2);
  this.setResizable(false);


  fProof1 =new TRandomProofPanel(fProofPanel,/*fBrowser*///fBrowser.getCurrentProofPanel(),
                                 "Proof 1 Code: ",TRandomProofPanel.TwelveLineProp,fRandomProof);
  String firstSet=fProof1.getSetProof();
  fProof2 =new TRandomProofPanel(/*fBrowser*///fBrowser.getCurrentProofPanel(),
          fProofPanel,
                                 "Proof 2 Code: ",TRandomProofPanel.TwelveLineProp,fRandomProof);

  while (firstSet.equals(fProof2.getSetProof()))         // need tobe different
     fProof2 =new TRandomProofPanel(fProofPanel,/*fBrowser*///fBrowser.getCurrentProofPanel(),
                                    "Proof 2 Code: ",TRandomProofPanel.TwelveLineProp,fRandomProof);


   JPanel intro=new JPanel(new BorderLayout());

   JTextArea text= new JTextArea(strCR
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

  text.setLineWrap(true);
  text.setWrapStyleWord(true);
  text.setFont(new Font("Sans-Serif",Font.PLAIN,12));


   intro.add (text,BorderLayout.SOUTH);

   initializeFinishPanel();


  fTabs.add("Intro",intro);
  fTabs.add("Proof 1",fProof1);
  fTabs.add("Proof 2",fProof2);
  fTabs.add("Finish",fFinishPanel);  //scroller in case it gets too big




fTabs.addChangeListener(new ChangeListener(){
public void stateChanged(ChangeEvent e){
   int index=fTabs.getSelectedIndex();

   switch (index){
     case 1:
     case 2:
         break;

        case 3:

          updateFinishPanel();

            break;


   }
   }});


  getContentPane().add(fTabs);


  }

  void initializeFinishPanel(){
    fFinishPanel=new JPanel(new BorderLayout());
    code=new JTextArea("");

    codeScroller =new JScrollPane(code);

    fFinishPanel.add(codeScroller, BorderLayout.CENTER);

    fFinishPanel.add(new JLabel("Submit all the Confirmation Codes."   //adding every update?
                                       +"")
                            , BorderLayout.SOUTH);

  }

  void updateFinishPanel(){

   fFinishPanel.remove(codeScroller);

    code = new JTextArea(strCR
                                           +"Here are your Confirmation Codes: "

                                           +strCR+strCR
                                           +"Proof 1:  ["
                                          +fProof1.getConfirmationCode()
                                           + "]"
                                           +strCR
                                           +"Proof 2:  ["
                                          +fProof2.getConfirmationCode()
                                           + "]"
                                           +strCR

                                           +strCR+strCR
                                           +"To submit: Copy and Paste all the Confirmation Codes (into the d2l Quiz Attempt). "
                                           +strCR
                                           +"   "
                                           +strCR+strCR
                                       +""
                );

            code.setEditable(false);

            code.setLineWrap(true);
 code.setWrapStyleWord(true);
 code.setFont(new Font("Sans-Serif",Font.PLAIN,12));


  codeScroller=new JScrollPane(code);

  fFinishPanel.add(codeScroller, BorderLayout.CENTER);


  }


  protected void processWindowEvent(WindowEvent e){
    if (e.getID()==WindowEvent.WINDOW_CLOSING)
      fNumOpen-=1;

    super.processWindowEvent(e);
  }


}

