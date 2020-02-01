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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import us.softoption.editor.TPreferences;
import us.softoption.games.TInvalid;
import us.softoption.infrastructure.TUtilities;

/* Need to get the formulas from invalid game and feed into tree
 * 
 */

public class Branches extends Trees{
	TInvalid fGame;
	
	
void createGUI(Container contentPane){

   createParserAndDoc();
		
   createPanels();
   
   fTreePanel.setMinimumSize(new Dimension(600,400));
   
   TPreferences.fRightMargin=500;  // not reading, using default
   
   fPreferredSize=new Dimension(600,800);
				    
   contentPane.setPreferredSize(fPreferredSize);
   contentPane.setMinimumSize(fMinimumSize);  

   contentPane.setLayout(new GridBagLayout());

   contentPane.add(fLabel,new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

   contentPane.add(fTreePanel,new GridBagConstraints(0, 1, 2, 2, 0.0, 0.0
			       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

  // TInvalid game =new TInvalid(this,fParser);
   
   fGame =new TInvalid(this,fParser);
   
   fGame.setPreferredSize(new Dimension(600,230));
   fGame.setMinimumSize(new Dimension(600,230));
   
   contentPane.add(fGame,new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
	       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
 
   
   /*
   
   contentPane.add(fComponentsPanel,new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
				       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
*/
   fGame.setListener(new UpdateTree());
   fGame.setInstructions(
		   
		   
		   "Extend the tree to try to produce a complete open branch. If there is one, read from it a truth value "+
		   
		   "assignment for the truth table line below. "+
		   
   
		   "Click below on the truth value assignment to the propositions, toggling them T F, to make the all "
	        + "the premises simultaneously true and the conclusion false (thus establishing that the argument is invalid). The default assigns them values randomly."
	        + " There need not be a unique answer."
	        + " (The clock stops while corrections are displayed.)"
   
   
   
   );
   
   fGame.run();

}

//	 end of createGUI	


public class UpdateTree extends AbstractAction{

    
    public void actionPerformed(ActionEvent ae){
  //  	System.out.print(ae.getActionCommand());
    	
    	String inputStr=TUtilities.logicFilter(ae.getActionCommand());
    	
    	fTreePanel.startTree(inputStr);

    }
}
	 
	
	
}