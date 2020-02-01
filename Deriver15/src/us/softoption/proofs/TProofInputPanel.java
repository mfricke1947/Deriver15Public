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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import us.softoption.infrastructure.Palette;
import us.softoption.infrastructure.SymbolToolbar;



/*This is the little input panel that gets stuck at the top of proofs. We are actually going to do
two styles, because the layout of the rewrite one is slightly different. We'll use the number of parameters
 in the constructor to differentiate them*/

/*I am going to try to attach a symbol palette. It's done via a new constructor
 * which has String symbols as a parameter.*/


public class TProofInputPanel extends JPanel{

  private JLabel fLabel1 = new JLabel();
  private JLabel fLabel2 = new JLabel();
  private JTextField fText1 = new JTextField();
  private JTextField fText2 = new JTextField();


  JPanel fSymbolPalette = new JPanel();  //usually buttons
  SymbolToolbar fSymbolToolbar= null;
  JPanel fComponentsPanel = new JPanel();  //usually buttons

/*One label no palette */
  
  
  public TProofInputPanel(String label, JTextField textField, JComponent [] components) {   // mf code not JBuilder

    this.setMaximumSize(new Dimension(32767, 32767));
    this.setMinimumSize(new Dimension(100, 81));
    this.setPreferredSize(new Dimension(100, 81));
    this.setLayout(new GridBagLayout());                   // the outer grid is a column of 3

   // fLabel1.setMinimumSize(new Dimension(45, 16));
   // fLabel1.setText(label);
    
    fLabel1= new JLabel(label);   //new Jan 09

    fText1 = textField;

    fText1.setDragEnabled(true);
    
    fComponentsPanel.setMaximumSize(new Dimension(2147483647, 30));
    fComponentsPanel.setMinimumSize(new Dimension(405, 30));
    fComponentsPanel.setPreferredSize(new Dimension(405, 30));

    fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons


    for (int i=0;i<components.length;i++){
      fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 12, 0), 0, 0));
    }

    this.add(fLabel1,          new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
             ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 0), 355, 10));
     this.add(fText1,    new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
             ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 324, 0));
     this.add(fComponentsPanel,           new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
             ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 8, 8));

    }
  
  /*One label with palette */
  
  public TProofInputPanel(String label, 
		                  JTextField textField, 
		                  JComponent [] components,
		                  String symbols) {   // mf code not JBuilder

	    this.setMaximumSize(new Dimension(32767, 32767));
	    this.setMinimumSize(new Dimension(100, 81));
	    this.setPreferredSize(new Dimension(100, 81));
	    this.setLayout(new GridBagLayout());                   // the outer grid is a column of 3

	   fLabel1= new JLabel(label);
	    
	/*    fLabel1.setMinimumSize(new Dimension(45, 16));
	    fLabel1.setPreferredSize(new Dimension(200, 16));   // using symbol palette-- keep to left 200
	    fLabel1.setMaximumSize(new Dimension(200, 16));
	    fLabel1.setSize(new Dimension(200, 16));
	    fLabel1.setText(label); */
	    
	   // fLabel1.setBorder(BorderFactory.createEtchedBorder());

	    fText1 = textField;

	    fText1.setDragEnabled(true);
	    
	    
	    initializeSymbolPalette(symbols);
	    initializeSymbolToolbar(symbols);
	    
	  //  fSymbolPalette.setBorder(BorderFactory.createEtchedBorder());

	    fComponentsPanel.setMaximumSize(new Dimension(2147483647, 30));
	    fComponentsPanel.setMinimumSize(new Dimension(405, 30));
	    fComponentsPanel.setPreferredSize(new Dimension(405, 30));

	    fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons


	    for (int i=0;i<components.length;i++){
	      fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
	         ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 12, 0), 0, 0));
	    }

	 
	  /*  this.add(fLabel1,          new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 0), 10, 10)); */
	    this.add(fLabel1,          new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 0), 0, 10));
	    this.add(/*fSymbolPalette*/ fSymbolToolbar,          new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	    this.add(fText1,    new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
	            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 324, 0));
	    this.add(fComponentsPanel,           new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0
	            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 8, 8));


	    }

 /*We have here a vertical grid of label, static text, label, static text, panel of horizontal grid of buttons*/
/*  Two Labels */

    public TProofInputPanel(String label1,
                            JTextField textField1,
                            String label2,
                            JTextField textField2,
                            JComponent [] components) {   // mf code not JBuilder

      this.setMaximumSize(new Dimension(32767, 32767));
      this.setMinimumSize(new Dimension(100, 132));  //132
      this.setPreferredSize(new Dimension(100, 132));
      this.setLayout(new GridBagLayout());                   // vertical grid of 5

    //  fLabel1.setMinimumSize(new Dimension(45, 16));
    //  fLabel1.setText(label1);
      
      fLabel1= new JLabel(label1);  // new Jan 09

      fText1 = textField1;
      fText1.setDragEnabled(true);
      fText1.setEditable(false);

   //   fLabel2.setMinimumSize(new Dimension(45, 16));
   //   fLabel2.setText(label2);
      
      fLabel2= new JLabel(label2);  // new Jan 09

      fText2 = textField2;
      fText2.setDragEnabled(true);
      fText2.setEditable(false);

      fComponentsPanel.setMaximumSize(new Dimension(2147483647, 30));
      fComponentsPanel.setMinimumSize(new Dimension(405, 30));
      fComponentsPanel.setPreferredSize(new Dimension(405, 30));

      fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n components


      for (int i=0;i<components.length;i++){
        fComponentsPanel.add(components[i],   new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
           ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));


      }





this.add(fLabel1,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 0), 355, 10));
this.add(fText1, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 324, 0));
this.add(fLabel2,new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 0), 355, 10));
this.add(fText2, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 324, 0));

this.add(fComponentsPanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0
               ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 8, 2));

    } 

    public void setLabel1(String theString){
        fLabel1.setText(theString);


    }
    public void setText1(String theString){
      fText1.setText(theString);
      fText1.selectAll();
      fText1.requestFocus();

    }

    
/**********************Symbol Input Palette ***************************/
    
    // We take a String of symbols and create a pallette of buttons for each of them
    
void initializeSymbolPalette(String symbols){
	//Palette aPalette = new Palette(fSymbolPalette,symbols,fText1);
	
	if(symbols!=null){
		fSymbolPalette= new Palette(symbols,fText1);
		fSymbolPalette.setSize(new Dimension(300, 21));         //was 300
		fSymbolPalette.setMaximumSize(new Dimension(300, 21));
		fSymbolPalette.setMinimumSize(new Dimension(300, 21));
		fSymbolPalette.setPreferredSize(new Dimension(300, 21));
	}
	

	
	
	
//	fSymbolPalette.setPreferredSize(new Dimension(300, 21));
	
	/*
	if(symbols!=null){
	fSymbolPalette.setSize(new Dimension(300, 21));
	fSymbolPalette.setMaximumSize(new Dimension(300, 21));
	fSymbolPalette.setMinimumSize(new Dimension(300, 21));
	fSymbolPalette.setPreferredSize(new Dimension(300, 21));

	fSymbolPalette.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons
	
	//fSymbolPalette.setBackground(Color.BLACK);
	
	//fLabel1.setBackground(Color.BLACK);
	
	String subStr;
	JButton newone;
	
	for (int i=0;i<symbols.length();i++){
		  subStr=symbols.substring(i, i+1);
		  newone= new JButton(subStr); 		  
		  initializeSymbolButton(newone,subStr);	  
		  fSymbolPalette.add(newone,
				  new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
				           ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 1));
	      }
	
	} */
}

void initializeSymbolButton(JButton button, final String symbol){
	button.setSize(20, 20);
	button.setPreferredSize(new Dimension(20, 20));  //was 20x20
	button.putClientProperty( "JButton.buttonType", "toolbar"  );
	
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		//	int start=fText1.getSelectionStart();
		//	int end=fText1.getSelectionEnd();
			fText1.replaceSelection(symbol);
			fText1.requestFocus();
		}});
	
}

/**********************Symbol Input Toolbar ***************************/

// We take a String of symbols and create a pallette of buttons for each of them

void initializeSymbolToolbar(String symbols){

if (symbols==null)
	symbols="";

if(symbols!=null){
	fSymbolToolbar= new SymbolToolbar(symbols,fText1);
/*	fSymbolPalette.setSize(new Dimension(300, 21));         //was 300
	fSymbolPalette.setMaximumSize(new Dimension(300, 21));
	fSymbolPalette.setMinimumSize(new Dimension(300, 21));
	fSymbolPalette.setPreferredSize(new Dimension(300, 21)); */
}





//fSymbolPalette.setPreferredSize(new Dimension(300, 21));

/*
if(symbols!=null){
fSymbolPalette.setSize(new Dimension(300, 21));
fSymbolPalette.setMaximumSize(new Dimension(300, 21));
fSymbolPalette.setMinimumSize(new Dimension(300, 21));
fSymbolPalette.setPreferredSize(new Dimension(300, 21));

fSymbolPalette.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons

//fSymbolPalette.setBackground(Color.BLACK);

//fLabel1.setBackground(Color.BLACK);

String subStr;
JButton newone;

for (int i=0;i<symbols.length();i++){
	  subStr=symbols.substring(i, i+1);
	  newone= new JButton(subStr); 		  
	  initializeSymbolButton(newone,subStr);	  
	  fSymbolPalette.add(newone,
			  new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
			           ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 1));
      }

} */
}

void initializeToolbarButton(JButton button, final String symbol){
button.setSize(20, 20);
button.setPreferredSize(new Dimension(20, 20));  //was 20x20
button.putClientProperty( "JButton.buttonType", "toolbar"  );

button.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e){
	//	int start=fText1.getSelectionStart();
	//	int end=fText1.getSelectionEnd();
		fText1.replaceSelection(symbol);
		fText1.requestFocus();
	}});

}

}


