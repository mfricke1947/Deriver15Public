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

package us.softoption.editor;


import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import us.softoption.games.THausmanRandomProof;
import us.softoption.games.TProofQuiz;
import us.softoption.games.TRandomProofPanel;
import us.softoption.parser.THerrickParser;
import us.softoption.proofs.TMyHerrickProofPanel;


public class THerrickDocument extends THausmanDocument{


public THerrickDocument(){

  fRandomProof= new THausmanRandomProof(); // for quizzes, for subclasses to override

}

public THerrickDocument(TJournal itsJournal){
  super(itsJournal);

  fRandomProof= new THausmanRandomProof(); // for quizzes, for subclasses to override
}

public THerrickDocument(TJournal itsJournal,boolean wantsIdentity){
  super(itsJournal,wantsIdentity);

  fRandomProof= new THausmanRandomProof(); // for quizzes, for subclasses to override
}





/*******************  Important overrides ***********************/

public void initializeParser(){     //TIDY UP CODE

   fParser= new THerrickParser();
   fParserName="Herrick";
      }


void initializePalettes(){
        fDefaultPaletteText=strCR+

                              "F \u2234 F & G"+

                              " \u223C  &  \u2228  \u2283  \u2261  \u2203  \u2234 " +  // \u2200 no uniquant
                              strCR+
                              strCR+

                         " Rxy[a/x,b/y] (\u2200x)(Fx \u2283 Gx)";


       fBasicPalette=" \u223C  &  \u2228  \u2283  \u2261  \u2203  \u2234 ";

  }






public void initializeProofPanel(){
   fProofPanel= new TMyHerrickProofPanel(this);
      }


      public JMenu supplyQuizzesSubMenu(){        // the different document types eg Copi want different ones of these
        JMenu quizzes= new JMenu();


        quizzes.add(new Quiz2());
        quizzes.add(new HausmanQuiz3());
        quizzes.add(new Quiz7());
        return
            quizzes;

    }


/******************* End of Important overrides ***********************/

    class HausmanQuiz3 extends JMenuItem{

             public HausmanQuiz3() {
               setText("Quiz 3 [Prop]");
               addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {

                   if (TProofQuiz.fNumOpen == 0) {

                     int[] prooftypes = {
                         TRandomProofPanel.SimpleAndIENegEOrI,
                         TRandomProofPanel.SimpleAndIENegEOrI,
                         TRandomProofPanel.SimpleAndIENegEOrI,
                         TRandomProofPanel.AndIENegEOrI,
                         TRandomProofPanel.AndIENegEOrI};

                     JTextArea text = new JTextArea(strCR
                                                    + "Work through the Tabs to Finish. [These are elementary/intermediate level derivations.]"
                                                    + strCR
                                                    + strCR
                                                    +
                                                    "Proof1: a simple derivation using Conj., Simp., and  Add.."
                                                    + strCR
                                                    +
                                                    "Proof2: a simple derivation using Conj., Simp., and  Add.."
                                                    + strCR
                                                    +
                                                    "Proof3: a simple derivation using Conj., Simp., and  Add.."
                                                    + strCR
                                                    +
                                                    "Proof4: an intermediate derivation using Conj., Simp., and  Add.."
                                                    + strCR
                                                    +
                                                    "Proof5: an intermediate derivation using Conj., Simp., and  Add.."
                                                    + strCR + strCR
                                                    + ""
                                                    + strCR
                                                    + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
                                                    + strCR
                         );

                     TProofQuiz quiz = new TProofQuiz(/*fDeriverDocument.*/fProofPanel,//TBrowser.this,
                         fRandomProof,
                                                      "Quiz 3",
                                                      prooftypes,
                                                      text
                         );
                     quiz.setVisible(true);
                   }
                 }

               });
             }
         }





      }
