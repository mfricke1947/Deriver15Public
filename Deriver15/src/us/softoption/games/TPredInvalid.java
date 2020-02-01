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


import static us.softoption.infrastructure.Symbols.chTherefore;

import java.awt.Container;

import javax.swing.JTextArea;

import us.softoption.parser.TFormula;
//import hostApplets.Interpretations;
import us.softoption.parser.TParser;

/*You need to use a scroll pane for the tables, for it is that which handles the header (which is
 kind of separate from the table)*/


public class TPredInvalid extends TPredConsistent{

	public TPredInvalid(Container itsContainer,TParser itsParser){
	    super(itsContainer,itsParser);
	  }

	  void initializeInstructions(){
	    fInstructions= new JTextArea("Produce a drawing or Interpretation which is a counter example (ie make all the premises true and the conclusion false)."
	          + " There are usually many correct answers."
	          + " (Aim: 100% right, maybe 5 minutes each. "
	          + "The clock stops while corrections are displayed.)"

	          );

	}

	void labelNotButton(){
	      notButton.setText("Not invalid");
	}

	String notSatisfiableAnswer(){
	      return
	      "Not invalid";

	}

	String produceLabel2(){
	        String randomStr = fParser.writeFormulaToString(fRandom);
	     String randomStr2 = fParser.writeFormulaToString(fRandom2);
	     String randomStr3 = fParser.writeFormulaToString(fRandom3);

	     return
	         randomStr+", "+randomStr2+" "+ chTherefore+ " " + randomStr3;

	  }


	TFormula satisfiableTestFormula(){
	  return
	      TFormula.conjoinFormulas(TFormula.conjoinFormulas(fRandom.copyFormula(),
	       fRandom2.copyFormula()),TFormula.negateFormula(fRandom3.copyFormula()));
	}



	}
