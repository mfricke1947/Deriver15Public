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

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import us.softoption.editor.TPreferences;

public class TProofListCellRenderer extends JPanel implements ListCellRenderer{



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

  int fFirstColStart=5;
  int fSecondColStart=18;

  TProofListCellRenderer(){
 //   setSize(300,400);
  }

  public Component getListCellRendererComponent(JList list,Object value, int index, boolean isSelected, boolean cellHasFocus){

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
   
   //2015 basic stroke was 1.2f

   paintLine(g);
 }

 public void paintLine(Graphics g){

   if (!fProofline.fBlankline)
  g.drawString(String.valueOf(fProofline.fLineno),fFirstColStart,fVertPen-2);    //line nummber

drawVertLines(g,fProofline.numVertLines());

drawFormula(g,fProofline.numVertLines());

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
                    fProofline.fRightMargin,
                    fVertPen-2);

 }







}

/*

 function TProofLine.Draw (fontSize, rightmargin: integer; var wrapno: integer): str255;

   var
    lineMessage, outPutStr: str255;
    rstart, linelength: integer;
    oldPort: Grafptr;
    oldFont, oldSize: integer;
    currentFontInfo: fontinfo;
    aParser: TParser;

   function EnterVertLines: str255;

    var
     j, k, l: integer;
     tempStr: str255;

   begin
    tempStr := strNull;
    j := -1;
    k := fSubprooflevel;
    l := fHeadlevel - 1;
    while j < k do
     begin
      if (j = k - 1) and (j > l) and fLastassumption then {shortens vertical of new}
 {                                                                         assumption}
       tempStr := concat(tempStr, chBlank, chShortvertstroke)
      else
       tempStr := concat(tempStr, chBlank, chVerticalstroke);
      j := j + 1;
     end;
    EnterVertLines := tempStr;
   end;

   procedure BreakIntoLines;
      {ensures outPutStr is shorter than 250-lineMessage-20}
      {and wrapped into lines}

    var
     tempStr: str255;
     index, i: integer;

   begin
    tempStr := strNull;
    index := (250 - length(lineMessage)) - 20;

    delete(outPutStr, index, (length(outPutStr) - index) + 1);

    index := 250 - (length(outPutStr) div linelength) * (((fSubprooflevel + 1) * 2) + 6);
    delete(outPutStr, index, (length(outPutStr) - index) + 1); {to make room for}
 {                                                                           vertlines and CR}

    index := length(outPutStr) div linelength;

    tempStr := concat(gCr, chBlank, chBlank, chBlank, chBlank, EnterVertLines);

    for i := index downto 1 do
     begin
      insert(tempStr, outPutStr, i * linelength);
      wrapno := wrapno + 1;
     end;

   end;

   procedure EnterJustification;

    var
     tempStr: str255;
     index, widthofotherlines: integer;

   begin
    tempStr := strNull;

    index := length(lineMessage);
    widthofotherlines := 0;

    while (index <> 0) and (widthofotherlines = 0) do
     begin
      if lineMessage[index] = gReturn then
       begin
        tempStr := copy(lineMessage, 1, index - 1);
        widthofotherlines := Stringwidth(tempStr);
       end
      else
       index := index - 1;
     end;

    repeat
     lineMessage := concat(lineMessage, chBlank)
    until ((Stringwidth(lineMessage) - widthofotherlines) > (rstart - 5)) or (length(lineMessage) > 240);

    repeat
     lineMessage := concat(lineMessage, chNarrowSpace)
    until ((Stringwidth(lineMessage) - widthofotherlines) >= rstart) or (length(lineMessage) > 240);

    if ffirstjustno <> 0 then
     lineMessage := concat(lineMessage, StrOfNum(ffirstjustno));
    if fsecondjustno <> 0 then
     lineMessage := concat(lineMessage, ',', StrOfNum(fsecondjustno));
    if fthirdjustno <> 0 then
     lineMessage := concat(lineMessage, ',', StrOfNum(fthirdjustno));

    lineMessage := concat(lineMessage, fjustification, gCr);

   end;

  begin {of DrawProofLine}
   wrapno := 1;
   GetPort(oldPort);
   oldFont := oldPort^.txFont;
   oldSize := oldPort^.txSize;

   TextFont(kLogicFont); {solely to get char widths correct}
   TextSize(fontSize);

   GetFontInfo(currentFontInfo);

   rstart := rightmargin * 5; {check}

   lineMessage := strNull;
   outPutStr := strNull;

   if not fBlankline then
    begin
     linelength := (3 * ((rstart div currentFontInfo.widMax) - 2 * fSubprooflevel)) div 2;{wrapping,}
 {                    2/3 x max. width is ballpark figure}

     lineMessage := ShortStrOfNum(fLineno);

     if (fLineno < 10) then
      lineMessage := concat(lineMessage, chBlank, chBlank);

     lineMessage := concat(lineMessage, EnterVertLines);

     if (fSubprooflevel = -1) then
      lineMessage := concat(lineMessage, chBlank); {proofs}
 {                    without Prems}

     if fLastassumption then {horiz spur under new assumption}
      lineMessage := concat(lineMessage, chUnderScore);

     New(aParser);
     FailNIL(aParser);
     aParser.WriteFormulaToString(fFormula, outPutStr);
     aParser.Free;

     BreakIntoLines;

     lineMessage := concat(lineMessage, outPutStr);

     EnterJustification;

                {DrawString(lineMessage);}

    end
   else
    wrapno := 0; {need this for setting cell heights}

   Draw := lineMessage;

   lineMessage := '';
   outPutStr := '';
   TextFont(oldFont);
   TextSize(oldSize);

  end;


*/
