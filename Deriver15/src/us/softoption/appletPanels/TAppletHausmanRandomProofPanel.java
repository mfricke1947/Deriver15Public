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

package us.softoption.appletPanels;

import us.softoption.editor.TDeriverDocument;
import us.softoption.proofs.TMyHausmanProofPanel;
import us.softoption.proofs.TProofPanel;

/*Need own proof panel and own random proofs */

public class TAppletHausmanRandomProofPanel extends TAppletRandomProofPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TAppletHausmanRandomProofPanel(TDeriverDocument aDocument,String label,int type){
		super(aDocument,label,type);	
	}
	
TProofPanel	 /*TMyProofPanel*/ supplyProofPanel(TDeriverDocument aDocument){
		 return
		 new TMyHausmanProofPanel(aDocument);	 
	 }
	 
	 
 public String produceAProof(int type){
		   String returnStr="";
		   String [] possibles;

		   switch (type) {
		     case SimpleAndIENegEOrI:

		       possibles=us.softoption.games.THausmanRandomProof.randomSimpleNegEandEIorI(1);

		       if (possibles!=null&&possibles.length>0)
		          returnStr=possibles[0];
		       break;
		     case SimpleImplicEEquivE:

		possibles=us.softoption.games.THausmanRandomProof.randomSimpleImplicEEquivE (1);

		if (possibles!=null&&possibles.length>0)
		   returnStr=possibles[0];
		break;

		     case AndIENegEOrI:

		        possibles=us.softoption.games.THausmanRandomProof.randomNegEandEIorI(1);

		        if (possibles!=null&&possibles.length>0)
		           returnStr=possibles[0];
		        break;

		    case ImplicEEquivE:

		        possibles=us.softoption.games.THausmanRandomProof.randomImplicEEquivE(1);

		        if (possibles!=null&&possibles.length>0)
		           returnStr=possibles[0];
		        break;
		      case ImplicI:

		          possibles=us.softoption.games.THausmanRandomProof.randomImplicI(1);

		          if (possibles!=null&&possibles.length>0)
		             returnStr=possibles[0];
		          break;
		        case SimpleNegI:

		              possibles=us.softoption.games.THausmanRandomProof.randomSimpleNegI(1);

		              if (possibles!=null&&possibles.length>0)
		                 returnStr=possibles[0];
		              break;
		            case NegI:

		                  possibles=us.softoption.games.THausmanRandomProof.randomNegI(1);

		                  if (possibles!=null&&possibles.length>0)
		                     returnStr=possibles[0];
		                  break;

		        case OrEEquivI:

		                    possibles=us.softoption.games.THausmanRandomProof.randomOrEEquivI(1);

		                    if (possibles!=null&&possibles.length>0)
		                       returnStr=possibles[0];
		        break;

		      case TwelveLineProp:

		                  possibles=us.softoption.games.THausmanRandomProof.randomTwelveLineProp(1);

		                  if (possibles!=null&&possibles.length>0)
		                     returnStr=possibles[0];
		      break;

		    case PredNoQuant:

		                possibles=us.softoption.games.THausmanRandomProof.randomPredNoQuant(1);

		                if (possibles!=null&&possibles.length>0)
		                   returnStr=possibles[0];
		    break;

		  case SimpleUI:

		              possibles=us.softoption.games.THausmanRandomProof.randomSimpleUI(1);

		              if (possibles!=null&&possibles.length>0)
		                 returnStr=possibles[0];
		    break;

		  case SimpleUG:

		                possibles=us.softoption.games.THausmanRandomProof.randomSimpleUG(1);

		                if (possibles!=null&&possibles.length>0)
		                   returnStr=possibles[0];
		    break;

		  case SimpleEG:

		                possibles=us.softoption.games.THausmanRandomProof.randomSimpleEG(1);

		                if (possibles!=null&&possibles.length>0)
		                   returnStr=possibles[0];
		    break;

		  case SimpleEI:

		                   possibles=us.softoption.games.THausmanRandomProof.randomSimpleEI(1);

		                   if (possibles!=null&&possibles.length>0)
		                      returnStr=possibles[0];
		     break;

		   case TenLinePred:

		                      possibles=us.softoption.games.THausmanRandomProof.randomTenLinePred(1);

		                      if (possibles!=null&&possibles.length>0)
		                         returnStr=possibles[0];
		     break;

		   case AnyAnyLevel:

		       possibles=us.softoption.games.THausmanRandomProof.randomAnyAnyLevel(1);

		       if (possibles!=null&&possibles.length>0)
		          returnStr=possibles[0];
		break;

		   case Identity:

		       possibles=us.softoption.games.THausmanRandomProof.randomIdentity(1);

		       if (possibles!=null&&possibles.length>0)
		          returnStr=possibles[0];
		break;

		   }

		   return
		       returnStr;
		 }	 
	 
	
}