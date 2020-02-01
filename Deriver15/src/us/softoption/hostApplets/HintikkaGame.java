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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
/*
import us.softoption.editor.TDeriverDocument;
import us.softoption.editor.THausmanDocument;
import us.softoption.editor.TJournal;  */

public class HintikkaGame extends Interpretations{



/***********************  Buttons ***************************/
	
protected	JButton trueButton(){
		JButton button = new JButton("Endorse");	  
		  
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				doEndorse();
			}});
		return
		   button;
	}
	
protected	JButton satisfiableButton(){
		JButton button = new JButton("Deny");	  
		  
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				doDeny();
			}});
		return
		   button;
	}
	
/***********************  End of Buttons ***************************/


/***********************  Endorse ***************************/

private void doEndorse(){

      String inputStr=readSource(TUtilities.defaultFilter);
      
      if (fParser.containsModal(inputStr)){ // no modal   //May 08
		 fDeriverDocument.writeToJournal("Sorry, no modal operators", 
				 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
		   return
		       ;
	 }	

      TFormula root = prepare(inputStr);   // this also picks up the valuation, and passes it to the document

      if (root!=null){
        fDeriverDocument.youEndorse(root,fDeriverDocument.fValuation);
        fDeriverDocument.clearValuation(); // removes valuation

}
}

/***********************  Deny ***************************/

private void doDeny(){

      String inputStr=readSource(TUtilities.defaultFilter);
      
      if (fParser.containsModal(inputStr)){ // no modal   //May 08
		 fDeriverDocument.writeToJournal("Sorry, no modal operators", 
				 TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
		   return
		       ;
	 }	

      TFormula root = prepare(inputStr);   // this also picks up the valuation, and passes it to the document

      if (root!=null){
        fDeriverDocument.youDeny(root,fDeriverDocument.fValuation);
        fDeriverDocument.clearValuation(); // removes valuation

}
}



}