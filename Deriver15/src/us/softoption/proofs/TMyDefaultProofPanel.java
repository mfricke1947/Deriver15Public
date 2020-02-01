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

package us.softoption.proofs;

import static us.softoption.infrastructure.Symbols.chAnd2;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chUniquant;
import us.softoption.editor.TDeriverDocument;
import us.softoption.parser.TDefaultParser;


public class TMyDefaultProofPanel extends TMyProofPanel {
	
public TMyDefaultProofPanel(TDeriverDocument itsDeriverDocument){
	    super(itsDeriverDocument);
	    
	    fAndEJustification=" "+ chAnd2 + "E";
	    fAndIJustification=" "+ chAnd2 + "I";

	    fEIJustification=" "+ chExiquant + "E";

	    UGJustification=" "+ chUniquant + "I";

	    EGJustification=" "+ chExiquant + "I";
	   // EIJustification=" "+ chExiquant + "E";


	    fTIInput = "Doing Assumption";

	    andIMenuItem.setText("&I");   // super uses ^I
	    andEMenuItem.setText("&E");
	  }

public TMyDefaultProofPanel(TDeriverDocument itsDeriverDocument,boolean wantsIdentity){
	      super(itsDeriverDocument,wantsIdentity);
	  }
	
	void initializeParser(){
		  fParser=new TDefaultParser();
		};
}
