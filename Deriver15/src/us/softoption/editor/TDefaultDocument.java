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

import us.softoption.parser.TDefaultParser;



public class TDefaultDocument extends TDeriverDocument{
	

	public TDefaultDocument(){
		  
		  fEToL.resetToDefaultRules();

		//  fRandomProof=new TBergmannRandomProof();                                  // needs its own proof strings

		               }
	
	
	
public TDefaultDocument(TJournal itsJournal){   // when it is running without a Browser, eg in an applet

		 super(itsJournal);
		 
		 
		
		 fEToL.resetToDefaultRules();
		 // fEToL.replacePropRules(TDefaultEtoLRules.fPropRules);                    // copi needs different from superclass
		//  fEToL.replaceRules(TDefaultEtoLRules.fRules);

		}

public TDefaultDocument(TJournal itsJournal,boolean wantsIdentity){
	 super(itsJournal,wantsIdentity);
	 
	 fEToL.resetToDefaultRules();
	 
	// fEToL.replacePropRules(TDefaultEtoLRules.fPropRules);                    // copi needs different from superclass
	//  fEToL.replaceRules(TDefaultEtoLRules.fRules);

	
}	

	public void initializeParser(){

		     fParser= new TDefaultParser(); // default
		     fParserName="Default";

		  }

}