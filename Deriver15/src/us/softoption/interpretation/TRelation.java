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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

// read Jan 2nd

public class TRelation extends TShape{

  Point fFrom=new Point(0,0);
  Point fTo=new Point(0,0);

  static final int kCircleRadius = 8;


    public TRelation()               //need to sort out these constructors
    {
      this(new Point(20,60));

      fTypeID=IDRelationR;
    }

    public TRelation(Point topLeft)
    {
      fWidth = 10;
      fHeight=10;

      fXCoord=topLeft.x;
      fYCoord=topLeft.y;

      fFrom=topLeft;
      fTo.x=topLeft.x+fWidth;
      fTo.y=topLeft.y+fHeight;

      //fIsVisible = false;

      fTypeID=IDRelationR;
    }

    public TShape copy(){
      TRelation newShape=new TRelation();

      copyFieldsTo(newShape);
        return
           newShape;
     }

     public void copyFieldsTo(TShape newShape){  // subclasses need to call super and just copy the fields
                                            // that your class defines
     super.copyFieldsTo(newShape);

     ((TRelation)newShape).fFrom=new Point(fFrom);
     ((TRelation)newShape).fTo=new Point(fTo);



             }


public Point getFrom(){
   return
       fFrom;
             }

public Point getTo(){
                return
                    fTo;
                          }


public void setFrom(Point p){
      if ((fFrom.x!=p.x)||(fFrom.y!=p.y)){
        fFrom = p;

        setBoundsRect(formSelectionRect());

        fireStateChanged();
      }
    }

public void setTo(Point p){
          if ((fTo.x!=p.x)||(fTo.y!=p.y)){
            fTo = p;

            setBoundsRect(formSelectionRect());

            fireStateChanged();
          }
        }

public void drawFrame(Graphics2D graphic){

      if (fFrom.distance(fTo)<kCircleRadius)
        graphic.draw(new Ellipse2D.Double(fFrom.x-4*kCircleRadius,fFrom.y-8*kCircleRadius,8*kCircleRadius,8*kCircleRadius));   // relating to itself
      else

      graphic.draw(new Line2D.Double(fFrom,fTo));

      drawArrowHead(graphic);
      }

public void drawInterior(Graphics2D graphic){
  //    graphic.fill(new Ellipse2D.Double(fXCoord, fYCoord,fWidth, fHeight));

  graphic.drawString(String.valueOf(fName),
                     (fFrom.x+fTo.x)/2,
                     ((fFrom.y+fTo.y)/2)-5);

  /*graphic.drawString(String.valueOf(fName),fXCoord+(fWidth/2)-2,fYCoord+fHeight-1);*/
      }


Rectangle formSelectionRect(){ // the issue here is that the line can go
                                                   // in any direction but the bounds rect cannot
                                                   // have negative height or width or other algorithms
                                                   // won't work

     Rectangle newRect = new Rectangle(fFrom);
     newRect.add(fTo);		// creates smallest rectangle which includes both ends
     return
         newRect;
              }





/*

                             procedure TRelationSketcher.TrackFeedback (anchorPoint, nextPoint: VPoint; turnItOn, mouseDidMove: BOOLEAN);

                var
                 itsExtentRect: rect;

               begin

                if mouseDidMove then
                 begin
                  PenMode(patXOR);

                  if (ABS((fShape.fFrom.h - fShape.fTo.h)) = kCircleRadius) and (fShape.fFrom.v = fShape.fTo.v) then {degenerate case of}
              {                       relating to itslef}
                   begin
                    itsExtentRect := fShape.fExtentRect;
                    FrameOval(itsExtentRect);
                   end

                  else
                   begin

                    MoveTo(anchorPoint.h, anchorPoint.v);
                    LineTo(nextPoint.h, nextPoint.v)
                   end;
                 end;
               end;




*/





              public void setCoords(Point p){
                int deltax=p.x-fFrom.x;
                int deltay=p.y-fFrom.y;


              if ((fFrom.x!=p.x)||(fFrom.y!=p.y)){
                fFrom.translate(deltax,deltay);
                fTo.translate(deltax,deltay);

                setBoundsRect(formSelectionRect());


                 fireStateChanged();
              }
               }

              public void moveBy(int deltax, int deltay){
                if (deltax!=0||deltay!=0){
                  fXCoord += deltax;
                  fYCoord += deltay;

                  fFrom.x+=deltax;
                  fFrom.y+=deltay;
                  fTo.x+=deltax;
                  fTo.y+=deltay;

                  fireStateChanged();
                }
              }

public void resize(Point anchor, Point end){   // a line has direction, can resize either way

if (anchor.equals(fFrom))
   setTo(end);

if (anchor.equals(fTo))
   setFrom(end);
 }

/*boolean contains(Point p){
   Line2D.Double thisLine =  new Line2D.Double(fXCoord, fYCoord,fXCoord+fWidth, fYCoord+fHeight);
   return
       thisLine.contains(p);
                }*/



  public void drawArrowHead(Graphics2D graphic){

    double deltax = fTo.x-fFrom.x;
    double deltay = fTo.y-fFrom.y;

    double theta = Math.atan2(deltay,deltax);



    graphic.translate(fTo.x,fTo.y);   // Move to tip
    graphic.rotate(theta);


    graphic.draw(new Line2D.Double(new Point(0,0),new Point(-3,-3)));
    graphic.draw(new Line2D.Double(new Point(0,0),new Point(-3,+3)));

    graphic.rotate(-theta);
    graphic.translate(-fTo.x,-fTo.y);

}

protected static final int FROM = 0, TO = 1;

  protected Rectangle[] getHandleRects()
  {
     Rectangle[] handles = new Rectangle[2];
     handles[FROM] = new Rectangle(fFrom.x - HANDLE_SIZE/2, fFrom.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
     handles[TO] = new Rectangle(fTo.x - HANDLE_SIZE/2, fTo.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
    return
      handles;
  }

  public Point getAnchorForResize(Point mouseLocation)
  {
          int whichHandle = getHandleHit(mouseLocation);

          if (whichHandle == NONE)
                  return null;
          switch (whichHandle) {
                  case FROM: return new Point(fTo);
                  case TO: return new Point(fFrom);
          }
          return null;
  }



     boolean isSemanticallySound(TSemantics context,boolean withoutSelectees){     // this is used
                                      // typically to determine whether the addition of
                                      // the shape makes sense or is permitted


    if (context!=null)
      return
         context.relationValid(withoutSelectees,this);
    else
     return
       false;


              }



}



