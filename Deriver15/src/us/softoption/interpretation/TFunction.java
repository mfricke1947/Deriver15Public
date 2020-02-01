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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

// This is going to be pretty well the same as Relation except it will display in lower case below

public class TFunction extends TRelation{



    public TFunction()               //need to sort out these constructors
    {
      this(new Point(20,60));

      fTypeID=IDFunction;
      fColor=Color.red;
    }

    public TFunction(Point topLeft)
    {
      super(topLeft);

      fTypeID=IDFunction;
      fColor=Color.red;
    }

    public TShape copy(){
      TFunction newShape=new TFunction();

      copyFieldsTo(newShape);
        return
           newShape;
     }

 /*    public void copyFieldsTo(TShape newShape){  // subclasses need to call super and just copy the fields
                                            // that your class defines
     super.copyFieldsTo(newShape);

             } */


public void drawInterior(Graphics2D graphic){
  //    graphic.fill(new Ellipse2D.Double(fXCoord, fYCoord,fWidth, fHeight));

  graphic.drawString(String.valueOf(fName),
                    ((fFrom.x+fTo.x)/2)-5,
                    ((fFrom.y+fTo.y)/2)+11); //IMPROVE THIS

  /*graphic.drawString(String.valueOf(fName),fXCoord+(fWidth/2)-2,fYCoord+fHeight-1);*/
      }




}



