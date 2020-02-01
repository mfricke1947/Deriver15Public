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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import us.softoption.editor.TPreferences;

public class TProofTableColumnRenderer extends JPanel implements TableCellRenderer{



  int rgb=51;

String grey="153,153,135";
String blue="102,255,155";
String red="251,51,255";
String black="0,0,0";
String realRed="251,0,0";
String realBlue="0,0,255";
String white="255,255,255";

static final Color lightSkyBlue2 = new Color(164,211,238);  //Mac selection background


  TProofline fProofline;

  int fVertPen=16;   //the cell depth is 16

  int fColumn=0;
  int fFirstColStart=5;
  int fSecondColStart=18;

  TProofTableColumnRenderer(){

	    }
  
  TProofTableColumnRenderer(int column){
fColumn=column;
  }

  public Component getTableCellRendererComponent(JTable table,Object value, 
		  boolean isSelected, boolean cellHasFocus,int rowIndex, int colIndex){

    fProofline=(TProofline)value;


setForeground(Color.black);

  if (isSelected){
     setBackground(lightSkyBlue2/* Color.black*/);
   //  setForeground(Color.red);
   }
   else{
     setBackground(Color.white);
     //setForeground(Color.black);
   }

    return
      this ;
  }



 public void paintComponent(Graphics g) {

   super.paintComponent(g); // JPanel draws background

   Graphics2D  g2d = (Graphics2D)g;

   g2d.setStroke(new BasicStroke(1.2f)); // float-- 2 seems a bit clumpy
   
   //2015 was 1.2f
   
   // new 2015 mf
   RenderingHints rh = new RenderingHints(
		    RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON);

		rh.put(RenderingHints.KEY_RENDERING, 
		    RenderingHints.VALUE_RENDER_QUALITY);

		g2d.setRenderingHints(rh);
   
   
   // end of new 2015

   paintLine(g);
 }

 public void paintLine(Graphics g){
	 
//we'll draw the formula in fColumn and the justification in any other column
// so this component needs to be initialized correction

if (fColumn==TProofTableModel.fProofColIndex){	 

   if (!fProofline.fBlankline)
  g.drawString(String.valueOf(fProofline.fLineno),fFirstColStart,fVertPen-2);    //line nummber

drawVertLines(g,fProofline.numVertLines());

drawFormula(g,fProofline.numVertLines());
}

if (fColumn==TProofTableModel.fJustColIndex)
   drawJustification(g); 


 }


 private void drawVertLines(Graphics g, int numVert){
   Color color=Color.black;
   int index = 1;

   for (int i=1;i<=numVert;i++){

     index = (i%3);
     color=Color.black;

     if (index==0){
        color = Color.blue;
        }
     if (index==2){
        color = Color.red;
      }

      g.setColor(color);

      if (fProofline.fLastassumption&&
          i==numVert&&
          (fProofline.fSubprooflevel>fProofline.fHeadlevel))                //not standing assumptoins
        g.drawLine(fSecondColStart+(6*i),3,fSecondColStart+(6*i),fVertPen); //last new assumption has short vertical
      else
        g.drawLine(fSecondColStart+(6*i),0,fSecondColStart+(6*i),fVertPen);

      if (fProofline.fLastassumption&&i==numVert)
        g.drawLine(fSecondColStart+(6*i),fVertPen-2,fSecondColStart+(6*i) +10,fVertPen-2); //last assumption has underline on outer

   }

 }



 private void drawFormula(Graphics g, int numVert){

   if (!TPreferences.fColorProof)                //if they don't want the formulae colored we'll go back to black
     g.setColor(Color.black);

   if (!fProofline.fBlankline)
     g.drawString(fProofline.formulaToString(),
                  fSecondColStart+(6*numVert)+4,
                  fVertPen-3);    //formula


 }



 private void drawJustification(Graphics g){
     String fourthContent="";

   if (!fProofline.fBlankline){

     String temp="";

        if (fProofline.fFirstjustno != 0)
          temp = temp + fProofline.fFirstjustno;
        if (fProofline.fSecondjustno != 0)
          temp = temp + "," + fProofline.fSecondjustno;
        if (fProofline.fThirdjustno != 0)
          temp = temp + "," + fProofline.fThirdjustno;


       fourthContent= temp + fProofline.fJustification +
            ( (fProofline.fDerived && TPreferences.fPrintDerived) ? " Auto" : "");

       if (fProofline.fDerived && TPreferences.fBlind)
         fourthContent= ( (TPreferences.fPrintDerived) ? " Auto" : ""); // hide justification

       }


       g.drawString(fourthContent,
                  /*  fProofline.fRightMargin,*/ 2,
                    fVertPen-2);

 }







}


