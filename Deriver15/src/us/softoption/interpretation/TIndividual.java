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
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/* read Feb 4th  */

public class TIndividual extends TShape{      // This is like an ellipse (of fixed size)


public TIndividual()
    {
  fWidth = 14;
  fHeight=14;

  fXCoord=0;
  fYCoord=0;

  fColor=Color.white;

  fTypeID=TShape.IDIndividual;

    }

public TIndividual(Point topLeft)
    {
     this();

      fXCoord=topLeft.x;
      fYCoord=topLeft.y;
   }

/*    public Point getAnchorForResize(Point mouseLocation){
      return
          null;
    } */

public TShape copy(){
  TIndividual newShape=new TIndividual(new Point(0,0));

  copyFieldsTo(newShape);
   return
       newShape;
    }



/**********************Drawing********************************************/


public void drawFrame(Graphics2D graphic){
          graphic.draw(new Ellipse2D.Double(fXCoord, fYCoord,fWidth, fHeight));
          }

public void drawInterior(Graphics2D graphic){

graphic.fill(new Ellipse2D.Double(fXCoord, fYCoord,fWidth, fHeight));

          graphic.setPaint(Color.black);

          graphic.setFont((graphic.getFont()).deriveFont(10));

          String nameToDraw=String.valueOf(fName);


    Point2D.Float penStart= getPenStart(graphic, nameToDraw);

    graphic.drawString(nameToDraw,penStart.x,penStart.y);



     /*     graphic.drawString(String.valueOf(fName),fXCoord+4,fYCoord+12); */
}

Point2D.Float getPenStart(Graphics2D graphic,String nameToDraw){


   TextLayout layout=getStringLayout(graphic,nameToDraw);

   Rectangle2D stringBounds=layout.getBounds();

   float middleX=fXCoord+(fWidth/2);
   float middleY=fYCoord+fHeight/2;

   float offSetX=(float)(stringBounds.getWidth()/2);
   float offSetY=(float)(stringBounds.getHeight()/2);

   float descent= layout.getDescent();

   Point2D.Float penStart= new Point2D.Float(middleX-offSetX,middleY+offSetY-(descent-2));

     return
         penStart;
   }


/**********************Methods (plain) ***********************************/




Point getHotSpot(){              //returns the center
   Point hotSpot= new Point(0,0);
   hotSpot.x=fXCoord+(fWidth/2);
   hotSpot.y=fYCoord+(fHeight/2);

   return
       hotSpot;
    }

boolean isSemanticallySound(TSemantics context,boolean withoutSelectees){  // this is overidden by the subclasses and is used
                                  // typically to determine whether the addition of
                                  // the shape makes sense or is permitted

if (context!=null)
  return
     context.individualValid(withoutSelectees,this);
else
 return
   false;
}



public void resize(Point anchor, Point end){ //cannot resize individuals
                                             // we'll let them just follow the mouse
    moveBy(end.x-(fXCoord+fWidth/2),end.y-(fYCoord+fHeight/2));

    }


}



