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

package us.softoption.tree;

// I was just experimenting here with our own cell renderer. I don't think we use this

import java.awt.BasicStroke;
import java.awt.Color;
/*import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints; */
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

public class TTreeTableCellRenderer //extends DefaultTableCellRenderer{
                                  extends JLabel implements TableCellRenderer{  //allows us to use JLabel for formatting



  int rgb=51;

String grey="153,153,135";
String blue="102,255,155";
String red="251,51,255";
String black="0,0,0";
String realRed="251,0,0";
String realBlue="0,0,255";
String white="255,255,255";

char squareRoot= '\u221A';
char largeX= '\u2716';

static final Color lightSkyBlue2 = new Color(164,211,238);  //Mac selection background


  Object fCellContent;
  TTreeTableModel fModel;

  int fVertPen=16;   //the cell depth is 16

  int fFirstColStart=5;
  int fSecondColStart=18;

  Font fOldFont=null;


TTreeTableCellRenderer(){

}


TTreeTableCellRenderer(TTreeTableModel aModel){   //needs to know its model to know what is selectable
  fModel=aModel;
}


/****** Pattern to use
 *
 *        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at
            // (rowIndex, vColIndex)

            if (isSelected) {
                // cell (and perhaps other cells) are selected
            }

            if (hasFocus) {
                // this cell is the anchor and the table has the focus
            }

            // Configure the component with the specified value
            setText(value.toString());

            // Set tool tip if desired
            setToolTipText((String)value);

            // Since the renderer is a component, return itself
            return this;
        }

  // The following methods override the defaults for performance reasons
        public void validate() {}
        public void revalidate() {}
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

*/




  public Component getTableCellRendererComponent(JTable table,Object value,
                                                 boolean isSelected,
                                                 boolean cellHasFocus,
                                                 int row,int col){

    if (fOldFont==null)   // one time initialization
      fOldFont=getFont();
    else
      setFont(fOldFont);  // we bold it for selected, so need to reset it


    fCellContent=value;

    if (value==null){
      setText(null);
      return
          this;
    }



    Rectangle r = table.getCellRect(row, col, false);

    if (fModel.isSelectable(row,col)){

      setForeground(Color.black);
      if (isSelected){
         setForeground(Color.red /*lightSkyBlue2/* Color.black*/);
         setBackground(Color.black);
         setFont(fOldFont.deriveFont(Font.BOLD+Font.ITALIC));
      }
     else{
        setBackground(Color.white);
     }
    }
   else{
     setForeground(Color.black);
     setBackground(Color.white); //not selectable don't invert background
   }




   String outStr=value.toString();
  // setText(outStr);              // temp for debugging

   if (outStr.equals("LeftDiag")){

     setText(null);           // we are using only one label here, need to keep it in Synch

     Icon diag = new LeftDiagonal(r.width, r.height);

     setIcon(diag);
   }
   else if (outStr.equals("RightDiag")){

     setText(null);           // we are using only one label here, need to keep it in Synch

     Icon diag = new RightDiagonal(r.width, r.height);

     setIcon(diag);
   }
   else if (outStr.equals("Vertical")){

     setText(null);           // we are using only one label here, need to keep it in Synch

     Icon diag = new Vertical(r.width, r.height);

     setIcon(diag);
   }
   else if (outStr.equals("Horizontal")){

     setText(null);           // we are using only one label here, need to keep it in Synch

     Icon diag = new Horizontal(r.width, r.height);

     setIcon(diag);
   }
   else

   {

    setIcon(null);           // we are using only one label here, need to keep it in Synch



   if ((value instanceof Integer)||       // the line number
       (value instanceof String))
     setHorizontalAlignment(JLabel.LEFT); // the justification
   else
     setHorizontalAlignment(JLabel.CENTER);


   if (value instanceof DefaultMutableTreeNode){
     TTreeDataNode dataValue=(TTreeDataNode)(((DefaultMutableTreeNode)value).getUserObject());

     if (dataValue.fDead){

     outStr+=" " + squareRoot;

  /*     Icon tick = new DeadTick((r.width), (r.height));

       setIcon(tick);
       setHorizontalTextPosition(LEFT);
       setIconTextGap(10);

*/

     }

     if (dataValue.fClosed){

        outStr+=largeX;

}

   }

       setText(outStr);

  }


    return
      this ;
    // super.getTableCellRendererComponent(table,value,isSelected,cellHasFocus,row,col);
  }

/*

 public void paintComponent(Graphics g) {

   super.paintComponent(g); // JPanel draws background

   Graphics2D  g2d = (Graphics2D)g;

   g2d.setStroke(new BasicStroke(2)); // 2 pixel pen

   //g.drawString(fCellContent.toString(),0,10);


//   JLabel label= new JLabel("Help");



 }  */


/********* see Note above ******/

 public void validate() {}
 public void revalidate() {}
 protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
 public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

/******************************/

 class DeadTick implements Icon{

 private int width,height;

 public DeadTick(int w,int h){

   width=50;
   height=10;
 }


 public void paintIcon (Component c, Graphics g, int x, int y){

   Graphics2D  g2d = (Graphics2D)g;

   g2d.setStroke(new BasicStroke(2)); // 2 pixel pen

   g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON );


   g.drawLine((x+width/2),y+height,width,y);   //line in middle 45 degrees up

 }

 public int getIconWidth(){return width;}
 public int getIconHeight(){return height;}


}

class LeftDiagonal implements Icon{

private int width,height;

public LeftDiagonal(int w,int h){

  width=w;
  height=h;
}

public void paintIcon (Component c, Graphics g, int x, int y){

  Graphics2D  g2d = (Graphics2D)g;

  g2d.setStroke(new BasicStroke(2)); // 2 pixel pen

  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON );


  g.drawLine((x+width/2),y+height,width,y);

}

public int getIconWidth(){return width;}
public int getIconHeight(){return height;}


}

class RightDiagonal implements Icon{

  private int width,height;

  public RightDiagonal(int w,int h){

    width=w;
    height=h;
  }

  public void paintIcon (Component c, Graphics g, int x, int y){

    Graphics2D  g2d = (Graphics2D)g;

    g2d.setStroke(new BasicStroke(2)); // 2 pixel pen

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON );


    g.drawLine(x,y,x+width/2,y+height);

  }

  public int getIconWidth(){return width;}
  public int getIconHeight(){return height;}


}

class Vertical implements Icon{

  private int width,height;

  public Vertical(int w,int h){

    width=w;
    height=h;
  }

  public void paintIcon (Component c, Graphics g, int x, int y){

    Graphics2D  g2d = (Graphics2D)g;

    g2d.setStroke(new BasicStroke(2)); // 2 pixel pen

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON );


    g.drawLine(x+width/2,y-1,x+width/2,y+1+height);

  }

  public int getIconWidth(){return width;}
  public int getIconHeight(){return height;}


}

class Horizontal implements Icon{

  private int width,height;

  public Horizontal(int w,int h){

    width=w;
    height=h;
  }

  public void paintIcon (Component c, Graphics g, int x, int y){

    Graphics2D  g2d = (Graphics2D)g;

    g2d.setStroke(new BasicStroke(2)); // 2 pixel pen

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON );


    g.drawLine(x,y,x+width,y);

  }

  public int getIconWidth(){return width;}
  public int getIconHeight(){return height;}


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
