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


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chArrow;
import static us.softoption.infrastructure.Symbols.chDoubleArrow;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chHArr;
import static us.softoption.infrastructure.Symbols.chLambda;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chNotSign;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chTherefore;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strCR;
import us.softoption.parser.TPriestParser;


/*Need to change the Copis to Bergmann */


public class TPriestDocument extends TDeriverDocument{


public TPriestDocument(){

//  fEToL.replacePropRules(TBergmannEtoLRules.fPropRules);                    // bergmann needs different from superclass
//  fEToL.replaceRules(TBergmannEtoLRules.fRules);

//  fRandomProof=new TBergmannRandomProof();                                  // needs its own proof strings

               }



public TPriestDocument(TJournal itsJournal){   // when it is running without a Browser, eg in an applet
  super(itsJournal);

// fEToL.replacePropRules(TBergmannEtoLRules.fPropRules);   // bergmann needs different from superclass
// fEToL.replaceRules(TBergmannEtoLRules.fRules);

// fRandomProof=new TBergmannRandomProof();       // different syntax etc to super
}

public TPriestDocument(TJournal itsJournal, boolean wantsIdentity){   // when it is running without a Browser, eg in an applet
    super(itsJournal,wantsIdentity);

 //  fEToL.replacePropRules(TBergmannEtoLRules.fPropRules);   // bergmann needs different from superclass
//   fEToL.replaceRules(TBergmannEtoLRules.fRules);

//   fRandomProof=new TBergmannRandomProof();       // different syntax etc to super
}





void initializePalettes(){
  fDefaultPaletteText=strCR+

                        "F \u2234 F "+ chAnd +" G "+
                        " " +chNotSign +
                        " " +chAnd +
                        " " +chOr +
                        " " +chArrow +
                        " " +chHArr +
                        " " +chUniquant +
                        " " +chExiquant +
                        " " +chTherefore +

                        " " +(TPreferences.fModal?(chModalNecessary+" "+chModalPossible+" "):"")+
                        " " +(TPreferences.fLambda?(chLambda+" " +chDoubleArrow):"")+


                        strCR+
                        strCR+


                   "Rxy[a/x,b/y] \u2200x (Fx \u2283 Gx)";


  fBasicPalette=                      " " +chNotSign +
                                      " " +chAnd +
                                      " " +chOr +
                                      " " +chArrow +
                                      " " +chHArr +
                                      " " +chUniquant +
                                      " " +chExiquant +
                                      " " +chTherefore +
                                      " " +(TPreferences.fModal?(chModalNecessary+" "+chModalPossible+" "):"")+
                                      " " +(TPreferences.fLambda?(chLambda+" " +chDoubleArrow):"")+
                                      ""
;

  }


public void initializeParser(){     //TIDY UP CODE

   fParser= new TPriestParser();
   fParserName="Priest";
      }

}






