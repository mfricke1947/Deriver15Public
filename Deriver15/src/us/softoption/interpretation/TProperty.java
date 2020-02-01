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

package us.softoption.interpretation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;


// read Jan 2nd

public class TProperty extends TBox{   // this is like a rectangle

public TProperty(){
     fTypeID=TShape.IDProperty;

     /* Experiment on fill patterns *******************

*/   /*    fBufferedImage = new BufferedImage(5, 5,
                                   BufferedImage.TYPE_INT_RGB);
            Graphics2D big = fBufferedImage.createGraphics();
            big.setColor(fColor);
            big.fillRect(0, 0, 5, 5);
            big.setColor(Color.lightGray);
            big.fillOval(0, 0, 5, 5);

            fRepetition = new Rectangle(0,0,5,5); */






  /*

End of experiment       */

  }

   public TShape copy(){
      TProperty newShape=new TProperty();

      copyFieldsTo(newShape);
       return
           newShape;
        }





    public void drawInterior(Graphics2D graphic){
      Composite originalComposite = graphic.getComposite();



/* Experiment on fill patterns *******************

*/       if ((fBufferedImage!=null)&&(fRepetition!=null))

            graphic.setPaint(new TexturePaint(fBufferedImage, fRepetition));




  /*

End of experiment       */



  graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
    0.5F));                    //half transparent



   graphic.fill(new Rectangle2D.Double(fXCoord, fYCoord,fWidth, fHeight));

      graphic.setComposite(originalComposite);

      graphic.setPaint(Color.black);

      String nameToDraw=String.valueOf(fName);

      TextLayout layout=getStringLayout(graphic,nameToDraw);

      Rectangle2D stringBounds=layout.getBounds();

      float middleX=fXCoord+(fWidth/2);
      float bottomY=fYCoord+fHeight;

      float offSet=(float)(stringBounds.getWidth()/2);


      graphic.drawString(String.valueOf(fName),middleX-offSet,bottomY-1);


      }

  boolean isSemanticallySound(TSemantics context,boolean withoutSelectees){     // this is used
                                      // typically to determine whether the addition of
                                      // the shape makes sense or is permitted


    if (context!=null)
      return
         context.propertyValid(withoutSelectees,this);
    else
     return
       false;
              }




}
