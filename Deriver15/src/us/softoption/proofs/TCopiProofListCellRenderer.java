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

import java.awt.Color;
import java.awt.Graphics;

import us.softoption.editor.TPreferences;

public class TCopiProofListCellRenderer extends TProofListCellRenderer{




 public void paintLine(Graphics g){

   if ((fProofline.fLineno==0)&&
        fProofline.fBlankline)    // we don't draw a first blankline line
  return
     ;


   int numVert= fProofline.numVertLines();


   drawVertLines(g,numVert);  //draw the vertical lines first, but one less of them


   if (!fProofline.fBlankline)
     g.drawString(String.valueOf(fProofline.fLineno)+".",
                  fFirstColStart+(numVert*6),          //number comes inside lines
                  fVertPen-2);                         //line nummber


   drawFormula(g,numVert);

   drawJustification(g);


}




 private void drawVertLines(Graphics g,int numVert){

   Color color=Color.black;
   int index = 2;                                            //try to draw black vert line in Copi

   for (int i=0;i<numVert;i++){


     index = (i%3);
     color=Color.black;

     if (index==1){
        color = Color.blue;
        }
     if (index==0){
        color = Color.red;
      }

      g.setColor(color);


      if (fProofline.fLastassumption&&(i==numVert-1)&&
         (fProofline.fSubprooflevel>fProofline.fHeadlevel)) {               //not standing assumptoins
            g.drawLine(fFirstColStart + (6 * i), 10, fFirstColStart + (6 * i), fVertPen); //last new assumption has short vertical
            g.drawLine(fFirstColStart+(6*i),fVertPen/2,fFirstColStart+(6*i) +5,fVertPen/2); //last assumption has arrow in
      }
      else{
        if (fProofline.fBlankline&&(i==numVert-1)){
          g.drawLine(fFirstColStart + (6 * i), 0, fFirstColStart + (6 * i), //on closing blank line draw top half only
                     fVertPen / 2);
          g.drawLine(fFirstColStart+(6*i),fVertPen/2,fFirstColStart+(6*i) +(fProofline.fRightMargin-(6*i)),fVertPen/2); //line accross middle
        }
        else
        g.drawLine(fFirstColStart + (6 * i), 0, fFirstColStart + (6 * i),
                   fVertPen);
      }

   }

 }



 private void drawFormula(Graphics g, int numVert){

   if (!TPreferences.fColorProof)                //if they don't want the formulae colored we'll go back to black
     g.setColor(Color.black);

   if (!fProofline.fBlankline)
     g.drawString(fProofline.formulaToString(),
                  fSecondColStart+(6*numVert)+4 +4,  /*extra 4 added Feb 08, numbers above 9 overwrite formula*/
                  fVertPen-3);    //formula


 }

 /*

 String transformJustification(String inStr){
   String outStr=inStr;

 /*  {
        if (inStr.equals(TProofPanel.fAssJustification))
           outStr = "";     //copi don't draw these

         if (inStr.equals(TProofPanel.fAssJustification)&&
             fProofline.fLastassumption)
           outStr = "AP";     //copi do draw these
      }

   return
       outStr;

return
     fProofline.transformJustification(inStr);
} */

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


       fourthContent= temp + fProofline.transformJustification(fProofline.fJustification) +
            ( (fProofline.fDerived && TPreferences.fPrintDerived) ? " Auto" : "");

       if (fProofline.fDerived && TPreferences.fBlind)
         fourthContent= ( (TPreferences.fPrintDerived) ? " Auto" : ""); // hide justification

       }


       g.drawString(fourthContent,
                    fProofline.fRightMargin,
                    fVertPen-2);

 }




}


