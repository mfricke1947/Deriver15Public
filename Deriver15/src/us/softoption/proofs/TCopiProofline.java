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

import static us.softoption.infrastructure.Symbols.strNull;
import us.softoption.editor.TPreferences;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TParser;

public class TCopiProofline extends TProofline{

 public TCopiProofline(){               // prefer the other constructor, because this creates a parser for each line
    fParser= new TCopiParser();         // need it for Beans and serialization
  }

  public TCopiProofline(TParser aParser){
    super(aParser);
  }

TProofline supplyProofline(){  //overidden by subclasses
    return
       new TCopiProofline(fParser);
}


/*

  TProofline shallowCopy(){             // needed for Undo
    TCopiProofline copy= new TCopiProofline(fParser);

    copy.fBlankline=fBlankline;
    copy.fFirstjustno=fFirstjustno;
    copy.fFormula = fFormula;       //not a copy!!
    copy.fHeadlevel = fHeadlevel;
    copy.fJustification = fJustification; //also not a copy
    copy.fLastassumption = fLastassumption;
    copy.fLineno = fLineno;
    copy.fSecondjustno=fSecondjustno;
    copy.fSelectable=fSelectable;
    copy.fSubprooflevel=fSubprooflevel;
    copy.fSubProofSelectable=fSubProofSelectable;
    copy.fThirdjustno=fThirdjustno;
    copy.fParser=fParser;
    copy.fRightMargin=fRightMargin;
    return
        copy;
    }

*/

private String drawBlankline(){
  String outStr;

  outStr= " <hr>";

  return
      outStr;
}


private String firstColumn(){
  String first="";           /* we make many html columns, one for each subprooflevel
                             but, for the Copi version we draw one less vertical line*/
/*
   int rgb=51;

   String grey="153,153,135";
   String blue="102,255,155";
   String red="251,51,255";
   String black="0,0,0";
   String realRed="251,0,0";
   String realBlue="0,0,255";
   String white="255,255,255";
*/
 //  String vertLines="";

   int end=fSubprooflevel-fHeadlevel;  //use to be just subprooflevel

   if ((fBlankline)&&fLineno!=0)
     end+=1;                   //try to draw black vert line in Copi

    for (int /*i=-1*/ i=0;i<end;i++){   //i=0 for Copi

      String colorWord="white";

      first+=
          "<td style= \"background-color: rgb("+white +") ;width: 1px;\">"
          + "<br>"/*"<font color="+colorWord+">|</font>"*/ + "</td>";    //May05, white spacer (and you have to have some content &nbsp;

  /* seem to require <br> to avoid widening. YET when I put the <br> in the
      Mac draws it too tall in the Journal ie it puts the break in ! */


      int index = ((i)%3);  //((i+1)%3);
      String color=black;//grey;
      colorWord="black";

      if (index==2){
        color = realBlue;
        colorWord="blue";
      }
      if (index==1){
        color = realRed;
        colorWord = "red";
      }


  first+=
          "<td style= \"background-color: rgb("+

          color +

      ") ;width: 2px;\">" + "<br>"+ "</td>";


      if (rgb<256)
        rgb+=51;
    }                    // end of for loop


if (fLastassumption)
      first+= "<td><strong> -> </strong></td>";

return
        first;
}

private String secondColumn(String rowspan){   //used to be 15 px for lineNo, but copy has period
 // String rowspan="";

    if (fBlankline){
      if (fLineno==0) // there can be a blankline start which is not drawn
        return
          strNull;
      else{
        return
            "<td rowspan=\"2\" style= \"width: 20px;\">" + "<hr>" +
            //"<hr align=\"right\" width=\"75%\">" /*drawBlankline() /*strNull*/ +
            "</td>"; // blanklines draw only the subprooflevels
      }
    }
  else
     return
         "<td "+rowspan+ " style= \"width: 20px;\">" + fLineno + ".</td>";  // the line number
}

private String thirdColumn(String colStr,int thirdColumnWidth, String rowspan){
  String thirdColumn="";

  if ((fBlankline)&&(fLineno<1)){ // there can be a blankline start which is not drawn
     thirdColumn = strNull;
  }
  else{
 //   int thirdColumnWidth = fRightMargin-secondColumnWidth;
    int maxChars=thirdColumnWidth/6;  // was 5 but gives too many

    String formulaStr="";
  //  String rowspan="";

    if (fBlankline){
      rowspan="rowspan=\"2\" ";
      formulaStr = drawBlankline();
    }
    else{
      if (fFormula != null){
        formulaStr = fParser.writeFormulaToString(fFormula,maxChars);
        if (formulaStr.length()>maxChars+2)   // if it is too long we'll shorten it
        	formulaStr="...";
      }
    }

    thirdColumn= "<td "+
        rowspan +
        "style= \"width: "
       + thirdColumnWidth+"px;\""+
       colStr + ">" +
       formulaStr+ "</td>";

    }

return
 thirdColumn;
}

String transformJustification(String inStr){
  String outStr=inStr;

  {
       if (fJustification.equals(TProofPanel.fAssJustification))
          outStr = "";     //copi don't draw these

        if (fJustification.equals(TProofPanel.fAssJustification)&&
            fLastassumption)
          outStr = "AP";     //copi do draw these
     }

  return
      outStr;
}

String fourthColumn(String rowspan){
 String fourthColumn;
 String temp="";

if (fBlankline)
   return
       "";
 else{
   if (fFirstjustno != 0)
     temp = temp + fFirstjustno;
   if (fSecondjustno != 0)
     temp = temp + "," + fSecondjustno;
   if (fThirdjustno != 0)
     temp = temp + "," + fThirdjustno;

   String justification=fJustification;

   if (fJustification!=null){
     if (fDerived && TPreferences.fBlind){
       justification = "";
       temp="";              //no prooflines either
     }
     else{
       justification=transformJustification(justification);
  /*     if (fJustification.equals(TProofPanel.fAssJustification))
          justification = "";     //copi don't draw these

        if (fJustification.equals(TProofPanel.fAssJustification)&&
            fLastassumption)
          justification = "AP";     //copi do draw these */
     }
   }

   fourthColumn = "<td "+ rowspan+ " >" + temp + justification +
       ( (fDerived && TPreferences.fPrintDerived) ? "&nbsp;Auto" : "") //to mark derived lines
       + "</td>";

 /*  if (fDerived && TPreferences.fBlind)
      fourthColumn = "<td>" +
                 ( (TPreferences.fPrintDerived) ? "&nbsp;Auto" : "") // hide justification
                + "</td>";  */
 }
return
   fourthColumn;

}


  /*
   Lines looks like

   1.  F^G
    ->2. H                     Ass
    | 3. F                     1 ^E
    |_____________________________

     4. H->F                     2-3 C.P.

   Blanklines are a special case. One at the beginning of a proof should
   not be drawn at all. The only others are those ending a subproof eg
   between 3 and 4. We'll use two rows for these so as to get the half
   height line.

   */

  private String specialFirstColumn(String rowspan, boolean upperRow){    //for blanklines and last assumptons ie subproof
   String first="";

    int end=fSubprooflevel-fHeadlevel;  //use to be just subprooflevel

    if (fBlankline)    // we treat a blankline as if it had an extra level so as to draw up to the previous line
      end+=1;

    if (!upperRow)
      rowspan="";      //lower columns don't span rows


     for (int i=0;i<end;i++){

       String colorWord = "white";
       String next;

       if (i==end-1)
         rowspan="";         // we go half height on last one, no matter what

       next="<td "+rowspan+"style= \"background-color: rgb("+white +") ;width: 1px;\">"
           + "<br>" + "</td>";  // this is the usual case

       if (fLastassumption){

         if ((i==end-1)&&upperRow)
             next= "<td> " + "&nbsp;" + "</td>"; // we go blank on last one for last Ass

        if ((i!=end-1)&&!upperRow)
             next= "";// "<td> " + "&nbsp;" + "</td>"; // we go blank on all lower rows except half height

         }

       first+=next;


       int index = ((i)%3);
       String color=black;
       colorWord="black";

       if (index==2){
         color = realBlue;
         colorWord="blue";
       }
       if (index==1){
         color = realRed;
         colorWord = "red";
       }

       next="<td "+rowspan+" style= \"background-color: rgb("+
           color +
           ") ;width: 2px;\">" + "<br>" + "</td>"; // this is the usual case

if (fLastassumption){

  if ((i==end-1)&&upperRow)
      next= "<td> "/* + "&nbsp;" */+ "</td>"; // we go blank on last one for last Ass

 if ((i!=end-1)&&!upperRow)
      next= "";//"<td> " + "&nbsp;" + "</td>"; // we go blank on all lower rows except half height

  }

first+=next;




       /********

       if ((i==end-1)&&
          fLastassumption&&
          upperRow)
             first+= "<td> " + "&nbsp;" + "</td>"; // we go blank on last one for last Ass
       else

          first+=
           "<td "+rowspan+" style= \"background-color: rgb("+
           color +
           ") ;width: 2px;\">" + "<br>" + "</td>";
*/

       if (rgb<256)
         rgb+=51;
     }                    // end of for loop
  return
         first;
 }



String drawBlankline2(String colStr,int thirdColumnWidth){

  if (fLineno==0)      // we don't draw the first line
    return
        strNull;
  else{
    String rowspan="rowspan=\"2\" ";
    boolean upperRow=true;

    return
        "<tr>" +
        specialFirstColumn(rowspan,upperRow) +
        "<td rowspan=\"2\" style= \"width: 20px;\">" + "<hr>" +"</td>" + // blanklines draw only the subprooflevels
        "<td rowspan=\"2\" style= \"width: " + thirdColumnWidth+"px;\""+colStr + ">" + "<hr>"+ "</td>"+
        "" + // fourthColumn +
        "</tr>" +

        "<tr>" +                                 // second row
        "<td> </td>"/*specialFirstColumn(rowspan,!upperRow)*/ + //vert lines
        "<td> </td>" +
        "<td> </td>" +
       "<td> </td>" +
        "</tr>"
        ;

  }
}

String drawLastAssumption(String colStr,int thirdColumnWidth){

  if (!fLastassumption)
    return
        "";
  else{

    String upper,lower;

    /*Two rows... the upper is all double height except the last vertical line which is blank, the lower
    is all blank except for the last vertical line which is filled in*/

    String rowspan="rowspan=\"2\" ";

    boolean upperRow=true;

    thirdColumnWidth-=15;  // to give space for the '->'

    upper=
        "<tr>" +
        specialFirstColumn(rowspan,upperRow) +
        "<td rowspan=\"2\"><strong> -> </strong></td>" +    // the arrow
        secondColumn(rowspan)+
        thirdColumn(colStr,thirdColumnWidth,rowspan)+
        fourthColumn(rowspan)+
        "</tr>";

    lower=
        "<tr>" + // second row
        specialFirstColumn(rowspan,!upperRow) + //vert lines
        "<td> </td>" +
        "<td> </td>" +
        "<td> </td>" +
        "</tr>";

    return
        upper +  lower;
  }
}



  public String toTableRow(int maxSubProofLevel){

    /*If you are interested in drawing only one line, rather than a whole proof in one table,
      call this with maxSubProofLevel of 0*/



    /*
     Lines looks like

     1.  F^G
      ->2. H                     Ass
      | 3. F                     1 ^E
      |_____________________________

     4. H->F                     2-3 C.P.


     now, a line on its own is fine, we can use one column for the line number, then one each for the
     vertical lines, the formula, and the justification.

     Conceptually, the line numbers are col 1, the vertical lines are subcolumns of col 2, the formula
     is col 3, and the justification col 4.

     But, if we wanted to combine several lines into a table we have the problem that the different rows
     might have a different number of columns and hence colspan= is needed.

     If this method is called with a (max) numberOfColumns, then we can insert a colspan if needed. The colspan
     needs to go with the formula (ie with F^G in line 1), we are looking for left justification.

     Now, some proofs start with a headlevel of -1, others with a headlevel of 0 (that information is
     on the line itself).

     Then the parameter tells us the maximum nesting. So the number of columns in the table as a whole is
     4 + maxSubProofLevel.

     So, any particular line has to look at the difference between its level and the headlevel. It that is
     maxSubProoflevel no colspan is needed. If it is one less than maxSubProoflevel a colspan of 2 is needed

     ie colspan = maxSubProoflevel - difference +1 (and colspan has to be greater than 1 to matter)


     */

    //int colspan=maxSubProofLevel- (fSubprooflevel-fHeadlevel) +1;

    /*May 05, I am now putting in two vertical lines for each subprooflevel-- a white spacer column
        then the real thing

       So the number of columns in the table as a whole is
     4 + 2*maxSubProofLevel.

     So, any particular line has to look at the difference between its level and the headlevel. It that is
     maxSubProoflevel no colspan is needed. If it is one less than maxSubProoflevel a colspan of 3 is needed

     ie colspan = 2(maxSubProoflevel - difference) +1 (and colspan has to be greater than 1 to matter)*/

    int colspan = 2 * (maxSubProofLevel - (fSubprooflevel - fHeadlevel)) + 1;

    String colStr = "";

  // rightMarginParam=355;  set by caller

    if (colspan > 1)
      colStr = " colspan=" + colspan + " ";

    int maxCols = 5;

    int secondColumnWidth = 0; // OLD (fSubprooflevel+1)* 5;

if (fSubprooflevel > -1)
  secondColumnWidth = fSubprooflevel * 6; //increment by width of two columns, should be 2 pixels
//but seems to be setting to 3


int thirdColumnWidth = fRightMargin - secondColumnWidth;


    if (fBlankline)
      return
          drawBlankline2(colStr,thirdColumnWidth);
    else if (fLastassumption)
      return
          drawLastAssumption(colStr,thirdColumnWidth);
    else

    {
      String firstColumn, secondColumn, thirdColumn, fourthColumn;

      String rowspan="";

      firstColumn = firstColumn();
      secondColumn = secondColumn(rowspan);

      /*
       for all the vertical lines we put in for subprooflevels, we must also take them off for
       the formula width

       */


      thirdColumn = thirdColumn(colStr, thirdColumnWidth,rowspan);

      fourthColumn = fourthColumn(rowspan);

        return
            "<tr>" +
            firstColumn +
            secondColumn +
            thirdColumn +
            fourthColumn +
            "</tr>";

    }
  }



  public int numVertLines(){

  int num=fSubprooflevel-fHeadlevel;  //use to be just subprooflevel
   if ((fBlankline)&&fLineno!=0)
      num+=1;


  return
      num;
 }







}





/*

 function TCopiProofline.Draw (fontSize, rightmargin: integer; var wrapno: integer): str255;
   OVERRIDE;

   var
    lineMessage, outPutStr: str255;
    rstart, linelength: integer;
    oldPort: Grafptr;
    oldFont, oldSize: integer;
    currentFontInfo: fontinfo;
    aCopiParser: TCopiParser;

   function EnterVertLines: str255;

    var
     j, k: integer;
     tempStr: str255;

   begin
    tempStr := strNull;
    j := 0;
    k := fSubprooflevel;
    while j < k do
     begin
      if (j = k - 1) and fLastassumption then {shortens vertical of new assumption}
       tempStr := concat(tempStr, chBlank, chCopiShortvertstroke, chCopiArrowHead, chBlank, chBlank)
      else
       tempStr := concat(tempStr, chBlank, chCopiVerticalstroke, chBlank, chBlank);
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
     begin
      if fJustification = ' CP' then
       lineMessage := concat(lineMessage, '-', StrOfNum(fsecondjustno)) (*special case, want 3-4 CP not 3,4 CP*)
      else
       lineMessage := concat(lineMessage, ',', StrOfNum(fsecondjustno));
     end;
    if fthirdjustno <> 0 then
     lineMessage := concat(lineMessage, ',', StrOfNum(fthirdjustno));

    if (ffirstjustno <> 0) or (fsecondjustno <> 0) or (fthirdjustno <> 0) then
     lineMessage := concat(lineMessage, ',', fjustification, gCr)
    else
     lineMessage := concat(lineMessage, fjustification, gCr);

   end;

   procedure DrawBlankLine; {this underlines the end of a CP}

   begin
    repeat
     lineMessage := concat(lineMessage, chCopiUnderline)
    until Stringwidth(lineMessage) > rstart - 20;

    lineMessage := concat(lineMessage, gCr);
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

   linelength := (3 * ((rstart div currentFontInfo.widMax) - 2 * fSubprooflevel)) div 2; {wrapping,}
 {               2/3 x max. width is ballpark figure}

   lineMessage := concat(lineMessage, EnterVertLines); {even blanks have these}

   if not fBlankline then
    begin

                {   IF fLastassumption THEN horiz spur under new assumption}
 {                    lineMessage := concat(lineMessage, chCopiArrowHead);  done in vert lines}

     lineMessage := concat(lineMessage, ShortStrOfNum(fLineno), '.', chBlank); {new blank}

     if (fLineno < 10) then
      lineMessage := concat(lineMessage, chBlank, chBlank);

               { IF (fSubprooflevel = - 1) THEN lineMessage := concat(lineMessage, chBlank); proofs}
 {                    without Prems}

     New(aCopiParser);
     FailNIL(aCopiParser);

     aCopiParser.WriteFormulaToString(fFormula, outPutStr);
     aCopiParser.Free;

     BreakIntoLines;

     lineMessage := concat(lineMessage, outPutStr);

     EnterJustification;

                {DrawString(lineMessage);}

    end
   else
    begin
     wrapno := 0; {need this for setting cell heights}
     if (fLineno > 0) then {blankstart has lineno 0}
      DrawBlankLine;
    end;

   Draw := lineMessage;

   lineMessage := '';
   outPutStr := '';
   TextFont(oldFont);
   TextSize(oldSize);

 end;


 */
