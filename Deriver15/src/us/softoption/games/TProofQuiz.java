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
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.softoption.editor.TPreferences;
import us.softoption.proofs.TProofPanel;


public class TProofQuiz extends JFrame{

  static public int fNumOpen=0;

  //TBrowser fBrowser;
  TProofPanel fProofPanel;
  TRandomProof fRandomProof;


  JTabbedPane fTabs= new JTabbedPane();

  TRandomProofPanel[] fProofs;

  int numProofs=0;


  JTextArea code;
  JScrollPane codeScroller;
  JPanel fFinishPanel;

  NameEntry fNameEntry=new NameEntry();


  public TProofQuiz(TProofPanel itsProofPanel,//TBrowser itsBrowser,
                    TRandomProof itsRandomProof,
                    String title,
                    int [] prooftypes,
                    JTextArea introText ){



  super (title);

 // fBrowser=itsBrowser;

  fProofPanel=itsProofPanel;
  fRandomProof=itsRandomProof;

  fNumOpen+=1;

  setSize(600,300);
 // setLocation((TDeriverApplication.fScreenSize.width-600)/2, (TDeriverApplication.fScreenSize.height-300)/2);
  this.setResizable(false);

  int lower=TRandomProofPanel.typeRange[0];
  int upper=TRandomProofPanel.typeRange[1];

  numProofs=prooftypes.length;

  fProofs = new TRandomProofPanel[numProofs];

  String [] proofStrings= new String[numProofs]; //THIS IS A HACK THAT I NEED TO FIX, BUT WE DON'T WANT DUPLICATES

  for (int i=0;i<numProofs;i++){

    fProofs[i] =null;
    proofStrings[i]="";


    if (prooftypes[i]>lower&&prooftypes[i]<upper){
      fProofs[i] = supplyRandomProofPanel(fProofPanel,/*fBrowser*///fBrowser.getCurrentProofPanel(),
                                         "Proof " + (i + 1) + " Code: ",
                                         prooftypes[i]);

      boolean duplicate=true; int tries=10; String testStr="";

      while (duplicate&&tries>0){

      duplicate=false;

      testStr=fProofs[i].getSetProof();

      for (int j=0;(j<i);j++){
        if (testStr.equals(proofStrings[j])) {
          duplicate=true;
          tries-=1;
          fProofs[i] = supplyRandomProofPanel(fProofPanel,/*fBrowser*///fBrowser.getCurrentProofPanel(),         //try another one
                                         "Proof " + (i + 1) + " Code: ",
                                         prooftypes[i]);
          break;   // leave j loop and go back to while

        }

      }
      }

      proofStrings[i]=testStr;  // success, fill in next part of array
    }



  }


   JPanel intro=new JPanel(new BorderLayout());

   String entry=TPreferences.getUser();

   fNameEntry.fName.setText(entry);

   intro.add(fNameEntry,BorderLayout.NORTH);



  introText.setLineWrap(true);
 introText.setWrapStyleWord(true);
  introText.setFont(new Font("Sans-Serif",Font.PLAIN,12));


   intro.add (introText,BorderLayout.SOUTH);

   initializeFinishPanel();


  fTabs.add("Intro",intro);
  for (int i=0;i<numProofs;i++){
    if (fProofs[i]!=null)
      fTabs.add("Proof "+(i+1), fProofs[i]);
  }
  fTabs.add("Finish",fFinishPanel);  //scroller in case it gets too big



fTabs.addChangeListener(new ChangeListener(){
public void stateChanged(ChangeEvent e){

if (fTabs.getSelectedComponent()==fFinishPanel)

          updateFinishPanel();

   }});


  getContentPane().add(fTabs);


  }

/*****************************Factory *****************************/
 TRandomProofPanel supplyRandomProofPanel(TProofPanel itsProofPanel,String label,int type){

   return
       new TRandomProofPanel(itsProofPanel,label,type,fRandomProof);
 }

 /*****************************End of Factory *****************************/


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


   String outputStr="";

   for (int i=0;i<numProofs;i++){
    if (fProofs[i]!=null)
      outputStr+= strCR
                +"Proof "+(i+1)+":  ["
                +fProofs[i].getConfirmationCode()
                + "]";

  }



    code = new JTextArea(strCR
                                           +"Here are your Confirmation Codes: "

                                           +strCR
                                           +outputStr
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

