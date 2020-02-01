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

import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chSuperscript1;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import us.softoption.infrastructure.TUtilities;

// read Jan 2nd

public class TInterpretationBoard extends TBox{   // this is like a rectangle

static int fMinWidth=150;

private TSemantics fSemantics=null;

public TInterpretationBoard(){
    fTypeID = IDInterpretationBoard;
    fColor=Color.white;
    fName = chBlank;
    fXCoord=5;
    fYCoord=0;
    fWidth=200;
    fHeight=30;

    fSelected=false;
    fSemantics=null;


  }

  /*
   procedure TInterpretationBoard.IInterpretationBoard (itsExtent: Rect; itsID: INTEGER; itsDeriverDocument: TDeriverDocument);

 begin
  fDeriverDocument := itsDeriverDocument;
  IBox(itsExtent, itsID);
  fID := IDInterpretationBoard;
  fShade := cWhite;
  fColor := gRGBBlack;
  fName := chBlank;
  fExtentRect.left := 5;
  fExtentRect.top := 0;
  fExtentRect.right := 205;
  fExtentRect.bottom := 30;
 end;



      */

public TInterpretationBoard(TSemantics theSemantics){
   this();

   fSemantics=theSemantics;
     }

   public TShape copy(){
      TInterpretationBoard newShape=new TInterpretationBoard();

      copyFieldsTo(newShape);
       return
           newShape;
        }

public void copyFieldsTo(TShape newShape){  // subclasses need to call super and just copy the fields
                                               // that your class defines
        super.copyFieldsTo(newShape);

        ((TInterpretationBoard)newShape).fSemantics=fSemantics;
                }


public void setSemantics(TSemantics theSemantics){
  fSemantics=theSemantics;

}

public TSemantics getSemantics(){
  return
      fSemantics;
}


    public void drawFrame(Graphics2D graphic){

// we'll make sure it's big enough first

      updateSize();

      graphic.draw(new Rectangle2D.Double(fXCoord-1, fYCoord-1,fWidth+1, fHeight+1));
      }



  public void drawInterior(Graphics2D graphic){
    int leftMargin = fXCoord + 5;
    int currentHeightMargin = fYCoord + 12;
    String tempStr;

    Composite originalComposite = graphic.getComposite();

    graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                    0.5F)); //half transparent

    graphic.fill(new Rectangle2D.Double(fXCoord, fYCoord, fWidth, fHeight));

    graphic.setComposite(originalComposite);

    graphic.setPaint(Color.black);

    tempStr = fSemantics.getCurrentUniverse();

    tempStr = TUtilities.separateStringWithCommas(tempStr);

    graphic.drawString("Universe= {" + tempStr + "} ", leftMargin,
                       currentHeightMargin);

    currentHeightMargin += 11;

    /*
     leftmargin := fExtentRect.left + 10;
         currentmargin := fExtentRect.top + 10;

         MoveTo(leftmargin, currentmargin);
         drawstring('Universe= {');

     tempStr := fDeriverDocument.fCurrentUniverse;

         for i := 1 to length(tempStr) do
          begin
           if i > 1 then
     DrawChar(',');
           DrawChar(tempStr[i]);
          end;

         drawstring('}');

         currentmargin := currentmargin + 11;



     */

    String[] currentProperties = fSemantics.getCurrentProperties();
    String possibleProperties = fSemantics.getPossibleProperties();

    for (int i = 0; i < currentProperties.length; i++) {
      if (currentProperties[i].length() > 0) {
        tempStr = currentProperties[i];
        tempStr = TUtilities.separateStringWithCommas(tempStr);

        graphic.drawString(possibleProperties.charAt(i) + "= {" + tempStr + "} ",
                           leftMargin, currentHeightMargin);

        currentHeightMargin += 11;
      }

    }

    /*
                       for charIndex := 'A' to 'Z' do
                  begin
     if length(fDeriverDocument.fCurrentProperties[charIndex]) > 0 then
                    begin

                    MoveTo(leftmargin, currentmargin);
                    currentmargin := currentmargin + 11;

                    drawstring(charIndex);
                    drawstring('= {');

                    tempStr := fDeriverDocument.fCurrentProperties[charIndex];

                    for i := 1 to length(tempStr) do
                    begin
                    if i > 1 then
                    DrawChar(',');
                    DrawChar(tempStr[i]);
                    end;
                    drawstring('}');
                    end;

                  end;



     */

    String[] currentRelations = fSemantics.getCurrentRelations();
    String possibleRelations = fSemantics.getPossibleRelations();

    for (int i = 0; i < currentRelations.length; i++) {
      if (currentRelations[i].length() > 0) {
        tempStr = currentRelations[i];
        tempStr = TUtilities.intoOrderedPairs(tempStr);

        graphic.drawString(possibleRelations.charAt(i) + "= {" + tempStr + "} ",
                           leftMargin, currentHeightMargin);

        currentHeightMargin += 11;
      }

    }

    /*
                           for charIndex := 'A' to 'Z' do
                  begin
     if length(fDeriverDocument.fCurrentRelations[charIndex]) > 0 then
                    begin

                    MoveTo(leftmargin, currentmargin);
                    currentmargin := currentmargin + 11;

                    drawstring(charIndex);
                                      { drawstring('x');}
             {                         drawstring(charIndex); didnot like this}
                    drawstring('�= {');

                    tempStr := fDeriverDocument.fCurrentRelations[charIndex];

                    i := 1;
                    while i < length(tempStr) do
                    begin
                    if i > 1 then
                    DrawChar(',');
                    DrawChar('<');
                    DrawChar(tempStr[i]);
                    i := i + 1;
                    DrawChar(tempStr[i]);
                    DrawChar('>');
                    i := i + 1;
                    end;
                    drawstring('}');
                    end;
                  end;

     */


    if (true) /*TPreferences.fIdentity||
       fUseIdentity)  This only draws if there are any*/ {
      String[] currentFunctions = fSemantics.getCurrentFunctions();
      String possibleFunctions = fSemantics.getPossibleFunctions();
      boolean functionsExist=false;

      String fancySuper= chSuperscript1+ "= {";


      for (int i = 0; i < currentFunctions.length; i++) {
        if (currentFunctions[i].length() > 0) {
          functionsExist=true;

          tempStr = currentFunctions[i];
          tempStr = TUtilities.intoOrderedPairs(tempStr);

          graphic.drawString(possibleFunctions.charAt(i) + fancySuper + tempStr + "} ",
                             leftMargin, currentHeightMargin);

          currentHeightMargin += 11;
        }

      }

      char[] currentIdentities = fSemantics.getCurrentIdentities();
      String possibleIdentities = fSemantics.getPossibleIdentities();

      for (int i = 0; i < currentIdentities.length; i++) {
         if (currentIdentities[i]!=' ') {

    graphic.drawString(possibleIdentities.charAt(i) + "= {" + currentIdentities[i] + "} ",
                       leftMargin, currentHeightMargin);

    currentHeightMargin += 11;
  }

}


      if (functionsExist){
        currentHeightMargin += 5;

        graphic.drawString("Instances of a function", leftMargin,currentHeightMargin);
        currentHeightMargin += 11;

        graphic.drawString("default to identity", leftMargin,currentHeightMargin);
        currentHeightMargin += 11;

      }

    }
  }

 /*

  procedure TInterpretationBoard.Draw;
  const
   minwidth = 200;

  var
   x, leftmargin, currentmargin, width, height, i: INTEGER;
   s, charIndex: char;
   r, badrect, itsExtentRect: Rect;
   tempStr: str255;
   itsColor: RGBColor;

 begin

  if fDeriverDocument <> nil then {if this is in clipboard it can loose reference}
          {to its document. Hence doc set to nil in commit}
   begin

    PenNormal;

              { PenMode(patOr); MF TO MAKE TRANSPARENT}

    itsExtentRect := fExtentRect;

    if gConfiguration.hasColorQD then
     begin
                    {Get the color of the menu item representing the shape's color}
      itsColor := fColor;
      RGBForeColor(itsColor);
      PaintRect(itsExtentRect);
      FillRect(itsExtentRect, gPat[fShade]);
      ForeColor(blackColor);
     end
    else
     begin
      PenPat(gPat[fShade]); {MF}
      PaintRect(itsExtentRect); {gPat[fShade]);}
     end;

    PenNormal; {MF}

    badrect := fExtentRect;

    width := length(fDeriverDocument.fCurrentUniverse);

    height := 2;

    for charIndex := 'A' to 'Z' do
     begin
      if (2 * length(fDeriverDocument.fCurrentRelations[charIndex]) - 7) > width then
       width := 2 * length(fDeriverDocument.fCurrentRelations[charIndex]) - 7;

      if length(fDeriverDocument.fCurrentProperties[charIndex]) > 0 then
       height := height + 1;
      if length(fDeriverDocument.fCurrentRelations[charIndex]) > 0 then
       height := height + 1;
     end;

    for charIndex := 'a' to 'z' do
     begin
      if (2 * length(fDeriverDocument.fCurrentFunctions[charIndex]) - 7) > width then
       width := 2 * length(fDeriverDocument.fCurrentFunctions[charIndex]) - 7;
      if length(fDeriverDocument.fCurrentFunctions[charIndex]) > 0 then
       height := height + 1;
     end;

    for charIndex := 'a' to 'z' do
     begin
      if (fDeriverDocument.fCurrentIdentities[charIndex]) <> chBlank then
       height := height + 1;
     end;

{$IFC identity }
    height := (height + 1) * 10; (*new*)
{$ELSEC}
    height := (height) * 10; (*new*)
{$ENDC}
    width := 80 + width * 8;  (*check, used to be 7*)

    if (width < minwidth) then
     width := minwidth;

    if (width <> (fExtentRect.right - fExtentRect.left)) or (height <> (fExtentRect.bottom - fExtentRect.top)) then
     begin

      itsExtentRect := fExtentRect;

                    {fDeriverDocument.fDrawShapeView.InvalidRect(r);}

      fExtentRect.right := fExtentRect.left + width;
      fExtentRect.bottom := fExtentRect.top + height;

      UnionRect(badrect, itsExtentRect, badrect);   (*check, aren't badrect and extenrect the same*)

      fDeriverDocument.fDrawShapeView.InvalidRect(badrect);
     end;

    PenNormal; {MF}

    TextFont(kLogicFont);
    TextFace([]);
    TextSize(9);
    TextMode(srcOr);

    leftmargin := fExtentRect.left + 10;
    currentmargin := fExtentRect.top + 10;

    MoveTo(leftmargin, currentmargin);
    drawstring('Universe= {');

    tempStr := fDeriverDocument.fCurrentUniverse;

    for i := 1 to length(tempStr) do
     begin
      if i > 1 then
       DrawChar(',');
      DrawChar(tempStr[i]);
     end;

    drawstring('}');

    currentmargin := currentmargin + 11;

    for charIndex := 'A' to 'Z' do
     begin
      if length(fDeriverDocument.fCurrentProperties[charIndex]) > 0 then
       begin

       MoveTo(leftmargin, currentmargin);
       currentmargin := currentmargin + 11;

       drawstring(charIndex);
       drawstring('= {');

       tempStr := fDeriverDocument.fCurrentProperties[charIndex];

       for i := 1 to length(tempStr) do
       begin
       if i > 1 then
       DrawChar(',');
       DrawChar(tempStr[i]);
       end;
       drawstring('}');
       end;

     end;

    for charIndex := 'A' to 'Z' do
     begin
      if length(fDeriverDocument.fCurrentRelations[charIndex]) > 0 then
       begin

       MoveTo(leftmargin, currentmargin);
       currentmargin := currentmargin + 11;

       drawstring(charIndex);
                         { drawstring('x');}
{                         drawstring(charIndex); didnot like this}
       drawstring('�= {');

       tempStr := fDeriverDocument.fCurrentRelations[charIndex];

       i := 1;
       while i < length(tempStr) do
       begin
       if i > 1 then
       DrawChar(',');
       DrawChar('<');
       DrawChar(tempStr[i]);
       i := i + 1;
       DrawChar(tempStr[i]);
       DrawChar('>');
       i := i + 1;
       end;
       drawstring('}');
       end;
     end;

    for charIndex := 'a' to 'z' do
     begin
      if length(fDeriverDocument.fCurrentFunctions[charIndex]) > 0 then
       begin

       MoveTo(leftmargin, currentmargin);
       currentmargin := currentmargin + 11;

       drawstring(charIndex);
                         { drawstring('x');}
{                         drawstring(charIndex); didnot like this}
       drawstring('�= {');

       tempStr := fDeriverDocument.fCurrentFunctions[charIndex];

       i := 1;
       while i < length(tempStr) do
       begin
       if i > 1 then
       DrawChar(',');
       DrawChar('<');
       DrawChar(tempStr[i]);
       i := i + 1;
       DrawChar(tempStr[i]);
       DrawChar('>');
       i := i + 1;
       end;
       drawstring('}');
       end;
     end;

    for charIndex := 'a' to 'z' do
     begin
      if (fDeriverDocument.fCurrentIdentities[charIndex]) <> chBlank then
       begin
       MoveTo(leftmargin, currentmargin);
       currentmargin := currentmargin + 11;

       drawstring(charIndex);
       drawstring('= {');

       DrawChar(fDeriverDocument.fCurrentIdentities[charIndex]);
       drawstring('}');
       end;
     end;


{$IFC identity}

    MoveTo(leftmargin, currentmargin);
    currentmargin := currentmargin + 11;
    drawstring('Instances of a function default to identity.');
{$ENDC }

   end;

  itsExtentRect := fExtentRect;
  FrameRect(itsExtentRect); { DrawOutline;}

 end;



  */





      public void resize(Point anchor, Point end){ //cannot resize board
                                               // we'll let them just follow the mouse
     // moveBy(end.x-(fXCoord+fWidth/2),end.y-(fYCoord+fHeight/2));

      }


void updateSize(){          // this makes it the right size for display, called by drawFrame
   int width;
   int height=2;

   width= fSemantics.getCurrentUniverse().length();

   String[] currentProperties=fSemantics.getCurrentProperties();

   for (int i=0;i<currentProperties.length;i++){    // look through all the possible properties here,
     if (currentProperties[i].length() > 0) { // not just the instantiated ones
       height += 1;
     }
   }

   String[] currentRelations=fSemantics.getCurrentRelations();

     for (int i=0;i<currentRelations.length;i++){    // look through all the possible properties here,
       int relationWidth=currentRelations[i].length();

       if (relationWidth>0){
         if (((2 * relationWidth) - 6 /*used to be 7*/) > width)
            width = ((2 * relationWidth) - 6);
         height+=1;
       }
   /*
    if (2 * length(fDeriverDocument.fCurrentRelations[charIndex]) - 7) > width then
         width := 2 * length(fDeriverDocument.fCurrentRelations[charIndex]) - 7;

    */
   }

   String[] currentFunctions=fSemantics.getCurrentFunctions();
   boolean functionsExist=false;

     for (int i=0;i<currentFunctions.length;i++){    // look through all the possible properties here,
       int functionWidth=currentFunctions[i].length();

       if (functionWidth>0){
         functionsExist=true;
         if (((2 * functionWidth) - 6 /*used to be 7*/) > width)
            width = ((2 * functionWidth) - 6);
         height+=1;
       }
   }
   if (functionsExist)
     height+=2;       // this is to allow enough room for the default warning label

   char[] currentIdentities=fSemantics.getCurrentIdentities();

   for (int i=0;i<currentIdentities.length;i++){    // look through all the possible properties here,
     if (currentIdentities[i]!=chBlank) { // not just the instantiated ones
       height += 1;
     }
   }


   fHeight = (height + 1) * 10;

   width= 80 + width * 11 /*use to be 8*/;

   if (width < fMinWidth)
      width = fMinWidth;

   if (functionsExist)
     width+=5;       // this is to allow enough room for the default warning label

   fWidth=width;


      }


}

/*

          procedure TShapeView.UpdateInterpretationBoards;

      const
       minwidth = 200;
      var
       oldR, newR, badRect: Rect;
       width, height: integer;
       charIndex: char;

      procedure UpdateBoard (shape: TShape);

       var
        anInterpretationBoard: TInterpretationBoard;

      begin
       if (shape.fID = IDInterpretationBoard) then
        begin
         anInterpretationBoard := TInterpretationBoard(shape);
         if anInterpretationBoard.fIsSelected then
          begin
           anInterpretationBoard.fIsSelected := FALSE;
           anInterpretationBoard.Highlight(hlOn, hlOff);
          end;

         badRect := anInterpretationBoard.fExtentRect;

         if width <> (anInterpretationBoard.fExtentRect.Right - anInterpretationBoard.fExtentRect.Left) then
          begin
           oldR := anInterpretationBoard.fExtentRect;
           InsetRect(oldR, -2, -2);
           anInterpretationBoard.fExtentRect.Right := anInterpretationBoard.fExtentRect.Left + width;
           newR := anInterpretationBoard.fExtentRect;
           InsetRect(newR, -2, -2);

           unionRect(oldR, newR, badRect);
          end;

         if height <> (anInterpretationBoard.fExtentRect.Bottom - anInterpretationBoard.fExtentRect.Top) then
          begin
           oldR := anInterpretationBoard.fExtentRect;

           InsetRect(oldR, -2, -2);
           anInterpretationBoard.fExtentRect.Bottom := anInterpretationBoard.fExtentRect.Top + height;

           newR := anInterpretationBoard.fExtentRect;
           InsetRect(newR, -2, -2);

           unionRect(oldR, newR, newR);

           unionRect(newR, badRect, badRect);

          end;

         oldR := anInterpretationBoard.fExtentRect;
         InsetRect(oldR, -2, -2);
         UnionRect(oldR, badRect, badRect);
         InvalidRect(badRect);
                        {InvalShape(shape); Wed10th}
        end;
      end;

     begin

      width := length(fDeriverDocument.fCurrentUniverse);

      height := 2;

      for charIndex := 'A' to 'Z' do
       begin
        if (2 * length(fDeriverDocument.fCurrentRelations[charIndex]) - 7) > width then
         width := 2 * length(fDeriverDocument.fCurrentRelations[charIndex]) - 7;

        if length(fDeriverDocument.fCurrentProperties[charIndex]) > 0 then
         height := height + 1;
        if length(fDeriverDocument.fCurrentRelations[charIndex]) > 0 then
         height := height + 1;
       end;

      for charIndex := 'a' to 'z' do
       begin
        if (2 * length(fDeriverDocument.fCurrentFunctions[charIndex]) - 7) > width then
         width := 2 * length(fDeriverDocument.fCurrentFunctions[charIndex]) - 7;

        if length(fDeriverDocument.fCurrentFunctions[charIndex]) > 0 then
         height := height + 1;
       end;

      for charIndex := 'a' to 'z' do
       begin
        if (fDeriverDocument.fCurrentIdentities[charIndex]) <> chBlank then
         height := height + 1;
       end;


      height := (height + 1) * 10; (*new*)
      width := 80 + width * 8;  (*used to be 7*)

      if (width < minwidth) then
       width := minwidth;

      fDeriverDocument.EachVirtualShapeDo(UpdateBoard);

     end;



   */
