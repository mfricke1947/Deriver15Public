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

import javax.swing.JMenu;

import us.softoption.games.TBergmannRandomProof;
import us.softoption.parser.TBergmannParser;
import us.softoption.proofs.TMyBergmannProofPanel;


/*Need to change the Copis to Bergmann */


public class TBergmannDocument extends TDeriverDocument{


public TBergmannDocument(){

 // fEToL.replacePropRules(TBergmannEtoLRules.fPropRules);                    // bergmann needs different from superclass
 // fEToL.replaceRules(TBergmannEtoLRules.fRules);
  
  fEToL.resetToBergmannRules();

  fRandomProof=new TBergmannRandomProof();                                  // needs its own proof strings

               }



public TBergmannDocument(TJournal itsJournal){   // when it is running without a Browser, eg in an applet
  super(itsJournal);

// fEToL.replacePropRules(TBergmannEtoLRules.fPropRules);   // bergmann needs different from superclass
// fEToL.replaceRules(TBergmannEtoLRules.fRules);

 fEToL.resetToBergmannRules();
 
 
 fRandomProof=new TBergmannRandomProof();       // different syntax etc to super
}

public TBergmannDocument(TJournal itsJournal, boolean wantsIdentity){   // when it is running without a Browser, eg in an applet
    super(itsJournal,wantsIdentity);

  // fEToL.replacePropRules(TBergmannEtoLRules.fPropRules);   // bergmann needs different from superclass
  // fEToL.replaceRules(TBergmannEtoLRules.fRules);

   fEToL.resetToBergmannRules();
   
   fRandomProof=new TBergmannRandomProof();       // different syntax etc to super
}





void initializePalettes(){
  fDefaultPaletteText=strCR+

                        "F \u2234 F & G"+

                        " \u223C  &  \u2228  \u2283  \u2261 \u2200  \u2203  \u2234 " +
                        strCR+
                        strCR+

                   " Rxy[a/x,b/y] (\u2200x)(Fx \u2283 Gx)";


 fBasicPalette=" \u223C  &  \u2228  \u2283  \u2261  \u2203  \u2234 ";

  }


public void initializeParser(){     //TIDY UP CODE

   fParser= new TBergmannParser();
   fParserName="Bergmann";

      }

public void initializeProofPanel(){
  /*      if (TPreferences.fRewriteRules)
        fProofPanel= new TRewrite(this);   //add Copi
    else */
        fProofPanel= new TMyBergmannProofPanel(this);

  }


  public JMenu supplyExamsSubMenu(){        // the different document types eg Copi want different ones of these
  JMenu exams= new JMenu();

  exams.add(new MidTermQ6());
  exams.add(new FinalQ6());
  exams.add(new FinalQ78());

  return
      exams;

}

/* Here is super

 public JMenu supplyQuizzesSubMenu(){        // the different document types eg Copi want different ones of these
   JMenu quizzes= new JMenu();


   quizzes.add(new Quiz2());
   quizzes.add(new Quiz3());
   quizzes.add(new Quiz4());
   quizzes.add(new Quiz5());
   quizzes.add(new Quiz6());
   quizzes.add(new Quiz7());
   quizzes.add(new Quiz8());
   quizzes.add(new BonusQuiz());
   return
       quizzes;

}  */



  public JMenu supplyQuizzesSubMenu(){        // the different document types eg Copi want different ones of these
    JMenu quizzes= new JMenu();

    quizzes.add(new Quiz2());
quizzes.add(new Quiz3());
quizzes.add(new Quiz4());
quizzes.add(new Quiz5());
quizzes.add(new Quiz6());
quizzes.add(new Quiz7());
quizzes.add(new Quiz8());
quizzes.add(new BonusQuiz());
return
    quizzes;



 /*   quizzes.add(new Quiz2());
    quizzes.add(new Quiz7());
    return
        quizzes;  */

}



}






