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

/* read Feb 4 04*/

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class TShape{


static final int IDIndividual = 1,
      IDProperty = 2,
      IDRelationR = 3,
      IDFunction = 4,
      IDIdentity = 5,
      IDInterpretationBoard = 6;       /*need these for filing*/
/*

    IDCircle = 1; {MF ID of TCircle items}
    IDPropertyF = 2;
    IDRelationR = 3;
    IDFunction = 4;
    IDIdentity = 5;
    IDInterpretationBoard = 6;
   */

static final int NO_PATTERN=0,
                 SPOTTY_PATTERN=1,
                 HATCH_PATTERN=2;


protected int fTypeID=0;

protected int fXCoord,fYCoord,fWidth,fHeight;  // the coords are usually top left
                                             //fExtentRect: Rect; {size of object}

protected Color fColor=Color.blue;


protected BufferedImage fBufferedImage=null;             // this is for putting a pattern on the shape (subclasses use it)
protected Rectangle fRepetition=new Rectangle(0,0,5,5);  // this is for putting a pattern on the shape (subclasses use it)
protected int fPatternType=0;

public boolean fSelected=true;

//protected boolean fIsVisible;


public char fName=' ';  //April 23  would like to change this to char, but affects a lot

public static  RenderingHints fQualityRendering =
new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                   RenderingHints.VALUE_ANTIALIAS_ON);{
              fQualityRendering.put(RenderingHints.KEY_RENDERING,
              RenderingHints.VALUE_RENDER_QUALITY);}

public TShape(){
 }




public TShape copy(){                  // subclasses need to override to return an object of the correct type
 TShape newShape=new TShape();

 copyFieldsTo(newShape);

 return
           newShape;
        }

public void copyFieldsTo(TShape newShape){  // subclasses need to call super and just copy the fields
                                       // that your class defines
newShape.fTypeID=fTypeID;

newShape.fXCoord=fXCoord;
newShape.fYCoord=fYCoord;
newShape.fWidth=fWidth;
newShape.fHeight=fHeight;

newShape.fColor=fColor;

newShape.fBufferedImage=fBufferedImage;
newShape.fRepetition=fRepetition;

newShape.fSelected=fSelected;

newShape.fName=fName;

        }


/***************************** field accessors, mutators etc ********************/

public Rectangle getBoundsRect(){
   return
      new Rectangle(fXCoord,fYCoord,fWidth,fHeight);
                 }

public Color getColor(){
  return
      fColor;

}

public char getName(){
   return
      fName;
  }

public int getPattern(){
  return
      fPatternType;
}


static public TextLayout getStringLayout(Graphics2D graphic, String aString){

                 // this is a utility to help us draw strings where we want to (eg in the middle)

                   Font font = graphic.getFont();

                   FontRenderContext frc = graphic.getFontRenderContext();

                   TextLayout layout = new TextLayout(aString, font, frc);

                   return
                      layout;
                   }


public int getTypeID(){
                  return
                      fTypeID;
                }

public int getXCoord(){
           return
               fXCoord;
}

public int getYCoord(){
            return
                fYCoord;
          }



public boolean getSelected(){
                return
                    fSelected;
              }

public void setBoundsRect(Rectangle r){
   if(fXCoord != r.x||fYCoord != r.y||fWidth != r.width||fHeight != r.height){
     fXCoord = r.x;
     fYCoord = r.y;
     fWidth = r.width;
     fHeight = r.height;

     fireStateChanged();
   }

 }

public void setColor(Color newColor){
   if (!newColor.equals(fColor)){
     fColor = newColor;

     // BUFFERED IMAGE

     setPattern(fPatternType); //leaves the same pattern but changes its color


     fireStateChanged();
   }
 }

public void setCoords(Point p){
   if ((fXCoord!=p.x)||(fYCoord!=p.y)){
     fXCoord = p.x;
     fYCoord = p.y;
     fireStateChanged();
   }
    }


public void setHeight(int height){
    if (fHeight!=height){
         fHeight = height;
         fireStateChanged();
       }
     }

public void setName(char name){
   fName=name;
   fireStateChanged();
}

public void setPattern(int pattern){

//remember here the pattern might have the same type (ie same pattern) but different color

            switch (pattern) {
              case NO_PATTERN:
                installNoPattern();
                break;
              case SPOTTY_PATTERN:
                installSpottyPattern();
                break;
              case HATCH_PATTERN:
                 installHatchPattern();
                 break;


          }
             }

public void setSelected(boolean isSelected){
         if (fSelected!=isSelected){
           fSelected = isSelected;
           fireStateChanged();
           }
         }

 public void setWidth(int width){
  if (fWidth!=width){
       fWidth = width;
       fireStateChanged();
     }
   }


public void setXCoord(int x){
if (fXCoord!=x){
     fXCoord = x;
     fireStateChanged();
   }
 }


 public void setYCoord(int y){
 if (fYCoord!=y){
   fYCoord = y;
   fireStateChanged();
 }
  }

/**************************Drawing *********************************************/

/* Draw sets the context and calls
   i) draw frame      // subclass should override
   ii) draw interior  // subclass should override
    iii) draw selection handles

 The subclasses can override any of this.

   */

public void draw(Graphics2D graphic){

     /*
      g2d.setPaint(fillColorOrPattern);
      g2d.setStroke(penThicknessOrPattern);
      g2d.setComposite(someAlphaComposite);
      g2d.setFont(anyFont);
      g2d.translate(...);
      g2d.rotate(...);
      g2d.scale(...);
      g2d.shear(...);
      g2d.setTransform(someAffineTransform);

      */


       graphic.setRenderingHints(/*TShapePanel.*/fQualityRendering);

       graphic.setColor(Color.black);
       drawFrame(graphic);

       graphic.setColor(fColor);       //foreground
       drawInterior(graphic);

       if (fSelected){
         Composite originalComposite = graphic.getComposite();
         graphic.setColor(Color.gray); //foreground
         graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
           0.5F));                    //half transparent

         drawHandles(graphic);

         graphic.setComposite(originalComposite);
       }

 }

public void drawFrame(Graphics2D graphic){

            }


public void drawHandles(Graphics2D graphic){
          if (fSelected) {

            Rectangle[] handles = getHandleRects();
               for (int i = 0; i < handles.length; i++)
                  graphic.fillRect(handles[i].x, handles[i].y, handles[i].width, handles[i].height);

          }
        }

    public void drawInterior(Graphics2D graphic){

               }


/**************************Methods (plain) *********************************************/

  boolean contains(Point p){  //must be within extent rect

          return

         (p.x>= fXCoord)&&(p.x<=(fXCoord+fWidth))&&
         (p.y>= fYCoord)&&(p.y<=(fYCoord+fHeight));
        }

boolean isSemanticallySound(TSemantics context,boolean withoutSelectees){  // this is overidden by the subclasses and is used
                                // typically to determine whether the addition of
                                // the shape makes sense or is permitted
     return
        true;                   // we'll default to true, because interpretation boards etc are always sound
        }

public void moveBy(int deltax, int deltay){
          if (deltax!=0||deltay!=0){
            fXCoord += deltax;
            fYCoord += deltay;
            fireStateChanged();
          }
        }


  void moveTo(Point p){

        setCoords(p);

        }

        public void resize(Point anchor, Point end){   //usually called by mouse dragging

             Rectangle newRect = new Rectangle(anchor);
             newRect.add(end);		// creates smallest rectangle which includes both anchor & end
             setBoundsRect(newRect);  	// reset bounds & redraw affected areas
                      }

/******************* Events  ***********************************************************/

/* A typical listener might be the panel that the shape is drawn on. Then when the shape
 changes it fires a StateChanged event and the panel then redraws it.*/

private EventListenerList fListenerList = new EventListenerList();
private ChangeEvent fChangeEvent = new ChangeEvent(this);

public void addChangeListener(ChangeListener aListener){
                          fListenerList.add(ChangeListener.class,aListener);
                        }

public void removeChangeListener(ChangeListener aListener){
                            fListenerList.remove(ChangeListener.class,aListener);
                          }

protected void fireStateChanged(){
                         Object [] list=fListenerList.getListenerList();
                         for (int index=list.length-2;index>=0;index-=2){  // these are stored as ordered pairs with class info first
                           if (list[index]==ChangeListener.class)
                              ((ChangeListener)list[index+1]).stateChanged(fChangeEvent);
                         }
                      }



/**************************Patterns *********************************************/




public void installNoPattern(){
   if (fBufferedImage != null){
       fBufferedImage = null;
       fPatternType=NO_PATTERN;
       fireStateChanged();}
      }

public void installSpottyPattern(){

// color change? pattern change?
          fBufferedImage = new BufferedImage(5, 5,
                                 BufferedImage.TYPE_INT_RGB);
          Graphics2D big = fBufferedImage.createGraphics();
          big.setColor(Color.black);// big.setColor(fColor);
          big.fillRect(0, 0, 5, 5);
          big.setColor(fColor);//  //big.setColor(Color.lightGray);
          big.fillOval(0, 0, 5, 5);
          fPatternType=SPOTTY_PATTERN;
          fireStateChanged();
           }

public void installHatchPattern(){

// color change? pattern change?
                     fBufferedImage = new BufferedImage(5, 5,
                                            BufferedImage.TYPE_INT_RGB);
                     Graphics2D big = fBufferedImage.createGraphics();
                    big.setColor(Color.black);// big.setColor(fColor);
                     big.fillRect(0, 0, 5, 5);

                   big.setColor(fColor);//  //big.setColor(Color.lightGray);
                   big.fillRect(1, 1, 4, 5);
                     fPatternType=HATCH_PATTERN;
                     fireStateChanged();
                      }


/**************************Selection handles *********************************************/


protected static final int HANDLE_SIZE = 6;
protected static final int NONE = -1, NW = 0, SW = 1, SE = 2 , NE = 3;


protected Rectangle[] getHandleRects()
      {
         Rectangle[] handles = new Rectangle[4];
         handles[NW] = new Rectangle(fXCoord - HANDLE_SIZE/2, fYCoord - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
         handles[SW] = new Rectangle(fXCoord - HANDLE_SIZE/2, fYCoord + fHeight - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
         handles[SE] = new Rectangle(fXCoord + fWidth - HANDLE_SIZE/2, fYCoord + fHeight - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
         handles[NE] = new Rectangle(fXCoord + fWidth - HANDLE_SIZE/2, fYCoord - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
       return
          handles;
      }


      /** Helper method to determine if a point is within one of the resize
        * corner knobs.  If not selected, we have no resize knobs, so it can't
        * have been a click on one.  Otherwise, we calculate the knob rects and
        * then check whether the point falls in one of them.  The return value
        * is one of NW, NE, SW, SE constants depending on which knob is found,
        * or NONE if the click doesn't fall within any knob.
        */
      protected int getHandleHit(Point pt)
      {
              if (!fSelected)	// if we aren't selected, the knobs aren't showing and thus there are no knobs to check
                      return NONE;

              Rectangle[] knobs = getHandleRects();
              for (int i = 0; i < knobs.length; i++)
                  if (knobs[i].contains(pt))
                              return i;
                 return NONE;
      }



      /** Method used to determine if a mouse click is starting
       * a resize event. In order for it to be a resize, the click must have
       * been within one of the knob rects (checked by the helper method
       * getKnobContainingPoint) and if so, we return the "anchor" ie the knob
       * opposite this corner that will remain fixed as the user drags the
       * resizing knob of the other corner around. During the drag actions of a
       * resize, that fixed anchor point and the current mouse point will be
       * passed to the resize method, which will reset the bounds in response
       * to the movement. If the mouseLocation wasn't a click in a knob and
       * thus not the beginning of a resize event, null is returned.
       */
      public Point getAnchorForResize(Point mouseLocation)
      {
              int whichHandle = getHandleHit(mouseLocation);

              if (whichHandle == NONE) // no resize knob is at this location
                      return null;
              switch (whichHandle) {
                      case NW: return new Point(fXCoord + fWidth, fYCoord + fHeight);
                      case NE: return new Point(fXCoord, fYCoord + fHeight);
                      case SW: return new Point(fXCoord + fWidth, fYCoord);
                      case SE: return new Point(fXCoord, fYCoord);
              }
              return null;
      }

}



/*     Pascal declarations from older version

 TShape = object(TObject)

     fID: INTEGER;

     fExtentRect: Rect; {size of object}

     fShade: INTEGER; {shade of object}
     fOldShade: INTEGER; {with the reshade command the Undo/Redo need to}
 {                                                  know the old shade}

     fColor: RGBColor; {color of object}
     fOldColor: RGBColor; {with the recolor command the Undo/Redo need to}
 {                                                   know the old color}

     fIsSelected: boolean; {is this shape selected ?}
     fWasSelected: boolean; {old selection status, set when the last command}
 {                                                     was performed}

     fName: CHAR;
     fFrom: POINT; {for relations}
     fTo: POINT;

                              {Initialization}
     procedure TShape.IShape (itsExtent: Rect; itsID: INTEGER);

                              {Screen display}
     procedure TShape.Draw;
     procedure TShape.DrawOutline;
     procedure TShape.EachHandleDo (procedure DoThis (Handle: Rect; handVHS: VHSelect; handTopOrLeft: boolean));
     procedure TShape.highlight (fromHL, toHL: HLState);

                              {Filing}
     function TShape.ID: INTEGER;
    {Used so that a filed shape can identify what kind of shape object}
 {    is to be launched to represent it in memory}

     procedure TShape.ReadFrom (aRefNum: INTEGER);
                              {Read the shape from input file}

     procedure TShape.WriteTo (aRefNum: INTEGER);
                              {Write data characterizing the shape onto the Save-file}

     procedure TShape.ReviseName (newname: CHAR);
     procedure TShape.SetExtentRect (tothis: Rect);

                              {Debugging}
                              {$IFC qDebug}
     procedure TShape.Fields (procedure DoToField (fieldName: STR255; fieldAddr: Ptr; fieldType: INTEGER));
     OVERRIDE;
                              {$ENDC}

    end;





*/

