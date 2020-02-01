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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TShapeClipboard{
  static TShapeClipboard fGlobalClipboard = new TShapeClipboard();
  private List fShapes= new ArrayList();

  TSemantics fSemantics=null;

  public TShapeClipboard(){

  fSemantics=new TSemantics(fShapes, TShapePanel.gIndividualNames);

 }

 public TShapeClipboard(List aShapeList){
    fShapes=aShapeList;

    fSemantics=new TSemantics(fShapes, TShapePanel.gIndividualNames);
  }

public List getShapeList(){
   return
       fShapes;
      }


public void setShapeList(List aShapeList){
      fShapes=aShapeList;

      fSemantics.setShapeList(fShapes);
    }


public Rectangle unionOfShapes(){
       Rectangle returnRect=null;

       if (fShapes.size() > 0) {
         Iterator iter = fShapes.iterator();
         TShape theShape;
         boolean initialized=false;

         while (iter.hasNext()){
           theShape= (TShape) iter.next();

           if (!initialized) {
               returnRect = theShape.getBoundsRect();
               initialized = true;
             }
             else
               returnRect.add(theShape.getBoundsRect());
           }

       }

       return
           returnRect;
    }

void prepareForPaste(){     // we want the shapes relative to an origin of 0,0
 Rectangle shapesRect=unionOfShapes();

 if (fShapes.size() > 0) {
   Iterator iter = fShapes.iterator();
   TShape theShape;
   Point offSet = shapesRect.getLocation();

   while (iter.hasNext()) {
     theShape = (TShape) iter.next();

     Rectangle boundsRect = theShape.getBoundsRect();

     boundsRect.translate(-offSet.x, -offSet.y);

     theShape.setBoundsRect(boundsRect);
   }
 }
    }


boolean clipboardValidForPaste(List documentShapeList){
      boolean valid = false;
      String clipboardNames = "";
      String documentNames="";

/*  The clipboard is valid if it is valid in itself, and if its individuals differ from those in the}
  {document}*/

if (fShapes.size()>0){             // needs to be non-empty for paste

  (this.fSemantics).updateUniverse();

  boolean withoutSelectees = true;

  if ( (this.fSemantics).documentValid(!withoutSelectees)) {
    valid = true;

    TShape theShape;

    if (fShapes.size() > 0) {
      Iterator iter = fShapes.iterator();

      while (iter.hasNext()) {
        theShape = (TShape) iter.next();

        if (theShape.fTypeID == TShape.IDIndividual) {
          clipboardNames = clipboardNames + theShape.fName;
        }
      }
    }

    if (documentShapeList.size() > 0) {
      Iterator iter = documentShapeList.iterator();

      while (iter.hasNext()) {
        theShape = (TShape) iter.next();

        if (theShape.fTypeID == TShape.IDIndividual) {
          documentNames = documentNames + theShape.fName;
        }
      }
    }

    for (int i = 0; i < clipboardNames.length(); i++) { // looking to see if there is one name in both lists
      if (documentNames.indexOf(clipboardNames.charAt(i)) > -1) {
        valid = false;
        break;
      }
    }

  }
}

   return
       valid;
    }

  /*

   function TShapeView.ClipBoardValid: BOOLEAN;

  {The clipboard is valid if it is valid in itself, and if its individuals differ from those in the}
  {document}

    var
     valid: BOOLEAN;
     clipShapeView: TShapeView;
     clipShapeDocument, actualDocument: TDeriverDocument;

    procedure NameOK (shape: TShape);

     procedure NamesDifferent (anotherShape: TShape);

     begin {got to deal with filtering etc.}
      if (anotherShape.fID = IDCircle) then {AND NOT anotherShape.fWasSelected }
       if (anotherShape.fName = shape.fName) then
        valid := FALSE;
     end;

    begin
     if valid then
      if shape.fID = IDCircle then
       actualDocument.EachVirtualShapeDo(NamesDifferent);

    end;

   begin
    valid := TRUE;
    actualDocument := fDeriverDocument;

    clipShapeView := TShapeView(gClipView);
    clipShapeDocument := clipShapeView.fDeriverDocument;

    valid := clipShapeDocument.DocumentValid(FALSE);

    if valid then
     clipShapeDocument.EachShapeDo(NameOK);

    ClipBoardValid := valid;
   end;



   */


}
