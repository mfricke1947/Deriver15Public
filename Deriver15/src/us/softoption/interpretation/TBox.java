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
import java.awt.geom.Rectangle2D;


// read Feb 4th

public class TBox extends TShape{   // this is like a rectangle but notice NO TYPE ID
                                    // so expect subclass

public TBox(){

}


public TShape copy(){
      TBox newShape=new TBox();

      copyFieldsTo(newShape);
       return
           newShape;
        }


public void drawFrame(Graphics2D graphic){
      graphic.draw(new Rectangle2D.Double(fXCoord-1, fYCoord-1,fWidth+1, fHeight+1));
      }


public void drawInterior(Graphics2D graphic){
      Composite originalComposite = graphic.getComposite();

      graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
       0.5F));                    //half transparent

      graphic.fill(new Rectangle2D.Double(fXCoord, fYCoord,fWidth, fHeight));

      graphic.setComposite(originalComposite);

      graphic.setPaint(Color.black);
      graphic.drawString(String.valueOf(fName),fXCoord+(fWidth/2)-2,fYCoord+fHeight-1);


      }
}



