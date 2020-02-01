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

import static us.softoption.infrastructure.Symbols.chSuperscript1;
import static us.softoption.infrastructure.Symbols.chSuperscript2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import us.softoption.editor.TPreferences;


class TPalette  extends JToolBar {

   static final int cNoSelection=-1, cSelectTool = 0, cEllipse = 1, cRectangle=2, cLine=3,
                    cFunctionLine=4, cIdentityLine=5;

   public static  RenderingHints fQualityRendering =
  new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);{
                fQualityRendering.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);}


   TShapePanel fShapePanel;

  ButtonGroup fButtonGroup = new ButtonGroup();
//   JToggleButton jToggleButton1 = new JToggleButton(new ImageIcon("Images/Select.GIF"));
   JToggleButton jToggleButton1 = new JToggleButton(new TArrowIcon());
   JToggleButton fIndividualButton;/* = new JToggleButton(new TIndividualIcon(new Point(0,0))); */
   JToggleButton fPropertyButton; /* = new JToggleButton(); */
   JToggleButton fRelationButton; /*= new JToggleButton();*/

   JToggleButton fFunctionButton;
   JToggleButton fIdentityButton;

   TShape [] fShapesArray; //{prototype shapes for the palette}

   /*

        kShapesInPalette = 5; {MF Number of shapes in the palette, in addition to the arrow}
    kShapesInPalettePlus1 = 6;


    */

   TIndividualIcon fProtoIndividual;
   TPropertyIcon fProtoProperty;
   TRelationIcon fProtoRelation;
   TFunctionIcon fProtoFunction;
   TIdentityIcon fProtoIdentity;

   boolean fUseIdentity=false;    // for getting more menu items independently of Preferences


public TPalette(TShapePanel aShapePanel){
   fShapePanel=aShapePanel;
   commonInitialization();
    }

public TPalette(TShapePanel aShapePanel, boolean wantsIdentity){

      setUseIdentity(wantsIdentity);
      fShapePanel=aShapePanel;
   commonInitialization();
}

private void commonInitialization(){
  initializeIndividualButton();
     initializePropertyButton();
     initializeRelationButton();

                                //we're best to create them but not show them
     initializeFunctionButton();
     initializeIdentityButton();

       jToggleButton1.setSelected(true);

        add(jToggleButton1, null);
        add(fIndividualButton, null);
        add(fPropertyButton, null);
        add(fRelationButton, null);

        if (TPreferences.fIdentity||
            fUseIdentity){
          add(fFunctionButton, null);
          add(fIdentityButton, null);
        }

        fButtonGroup.add(jToggleButton1);
        fButtonGroup.add(fIndividualButton);
        fButtonGroup.add(fPropertyButton);
        fButtonGroup.add(fRelationButton);

        if (TPreferences.fIdentity||
            fUseIdentity){
          fButtonGroup.add(fFunctionButton);
          fButtonGroup.add(fIdentityButton);
        }

      check();}




void initializeIndividualButton(){

   fProtoIndividual= new TIndividualIcon(new Point(0,0));
   fIndividualButton = new JToggleButton(fProtoIndividual);

   fIndividualButton.addKeyListener(new KeyAdapter(){
        public void keyTyped(KeyEvent e) {
           TShape thePrototype=getPrototype();


           if (thePrototype!=null){

              String names="abcdefghijklmnopqrstuvwxyz";

              char ch = e.getKeyChar();

              if ((names.indexOf(ch)>-1)&&
                  (thePrototype.getName()!=ch)){
                      thePrototype.setName(ch);
                      repaint();
              }
           }
        }});


    }

void initializePropertyButton(){

   fProtoProperty= new TPropertyIcon();
   fPropertyButton = new JToggleButton(fProtoProperty);

   fPropertyButton.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e) {
               TShape thePrototype=getPrototype();


               if (thePrototype!=null){

                  String names="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                  char ch = e.getKeyChar();

                  if ((names.indexOf(ch)>-1)&&
                      (thePrototype.getName()!=ch)){
                          thePrototype.setName(ch);
                          repaint();
                  }
               }
            }});

/*
                       if (ch >= 'A') and (ch <= 'Z') then
             if ((shapeIndex = IDPropertyF) or (shapeIndex = IDRelationR) or (shapeIndex = IDFunction)) then
              if gShapesArray[shapeIndex].fName <> ch then
               begin
                gShapesArray[shapeIndex].fName := ch;
                fPalette.UpdateInvalid(shapeIndex);
               end;


           */
        }

void initializeRelationButton(){


           fProtoRelation= new TRelationIcon();
           fRelationButton = new JToggleButton(fProtoRelation);


           fRelationButton.addKeyListener(new KeyAdapter(){
                    public void keyTyped(KeyEvent e) {
                       TShape thePrototype=getPrototype();


                       if (thePrototype!=null){

                          String names="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                          char ch = e.getKeyChar();

                          if ((names.indexOf(ch)>-1)&&
                              (thePrototype.getName()!=ch)){
                                  thePrototype.setName(ch);
                                  repaint();
                          }
                       }
                    }});

        /*
                               if (ch >= 'A') and (ch <= 'Z') then
                     if ((shapeIndex = IDPropertyF) or (shapeIndex = IDRelationR) or (shapeIndex = IDFunction)) then
                      if gShapesArray[shapeIndex].fName <> ch then
                       begin
                        gShapesArray[shapeIndex].fName := ch;
                        fPalette.UpdateInvalid(shapeIndex);
                       end;


                   */
                }

                void initializeFunctionButton(){


                          fProtoFunction= new TFunctionIcon();
                          fFunctionButton = new JToggleButton(fProtoFunction);


                          fFunctionButton.addKeyListener(new KeyAdapter(){
                                   public void keyTyped(KeyEvent e) {
                                      TShape thePrototype=getPrototype();


                                      if (thePrototype!=null){

                                         String names="abcdefghijklmnopqrstuvwxyz";

                                         char ch = e.getKeyChar();

                                         if ((names.indexOf(ch)>-1)&&
                                             (thePrototype.getName()!=ch)){
                                                 thePrototype.setName(ch);
                                                 repaint();
                                         }
                                      }
                                   }});

                       /*
                                              if (ch >= 'A') and (ch <= 'Z') then
                                    if ((shapeIndex = IDPropertyF) or (shapeIndex = IDRelationR) or (shapeIndex = IDFunction)) then
                                     if gShapesArray[shapeIndex].fName <> ch then
                                      begin
                                       gShapesArray[shapeIndex].fName := ch;
                                       fPalette.UpdateInvalid(shapeIndex);
                                      end;


                                  */
                               }


void initializeIdentityButton(){


fProtoIdentity= new TIdentityIcon();
fIdentityButton = new JToggleButton(fProtoIdentity);


fIdentityButton.addKeyListener(new KeyAdapter(){
                                                  public void keyTyped(KeyEvent e) {
                                                     TShape thePrototype=getPrototype();


                                                     if (thePrototype!=null){

                                                        String names="abcdefghijklmnopqrstuvwxyz";

                                                        char ch = e.getKeyChar();

                                                        if ((names.indexOf(ch)>-1)&&
                                                            (thePrototype.getName()!=ch)){
                                                                thePrototype.setName(ch);
                                                                repaint();
                                                        }
                                                     }
                                                  }});

                                              }



void check(){
   char dummy = fShapePanel.firstTermAvail();

   if ((fProtoIndividual.fName!=dummy)||
       (fProtoIdentity.fName!=dummy)){           //MORE TO COME ON IDENTITY, I think it is in now, Nov 06
       fProtoIndividual.fName=dummy;
       fProtoIdentity.fName=dummy;

       repaint();
   }

   }


   /*procedure TPalette.Check;

     var
      dummy: char;

    begin
     dummy := TDeriverDocument(fDocument).FirstTermAvail; {MF}

     if (gShapesArray[IDCircle].fName <> dummy) or (gShapesArray[IDIdentity].fName <> dummy) then
      begin
       gShapesArray[IDCircle].fName := dummy;
       gShapesArray[IDIdentity].fName := dummy;

       InvalidRect(gChoiceArray[IDCircle]); {The Clipboard does not have a palette}
       InvalidRect(gChoiceArray[IDIdentity]);{The Clipboard does not have a palette}

      end;

    end;
*/



class TArrowIcon implements Icon{

int [] xPoints = {0,0,2,6,8,4,9};
int [] yPoints = {0,9,4,10,9,4,3};

   public int getIconHeight(){
          return
              12;
        }

   public int getIconWidth(){
               return
                   12;
             }

   public void paintIcon (Component c, Graphics g,
                             int x, int y){

    ((Graphics2D)g).setRenderingHints(TShapePanel.fQualityRendering);

        if (c.isEnabled()) {
                 g.setColor(c.getForeground());
             } else {
                 g.setColor(Color.gray);
             }

             g.translate(x, y);
             g.fillPolygon(xPoints, yPoints, xPoints.length);
             g.translate(-x, -y);  //Restore Graphics object

      }
   }




public void InitShapesArray(){

   }

/*

     procedure InitgShapesArray;
      var
       circle: TCircle;
       propertyF, propertyG, propertyH: TPropertyF;

       relationR: TRelationR;
       relationS: TRelationS;
       identity: TIdentity;
       interpretationBoard: TInterpretationBoard;
     begin
      SetRect(r, 0, 0, 0, 0); {Not displayed in palette but needed in}
   {                             gShapesArray}

      New(interpretationBoard);
      FailNil(interpretationBoard);
      interpretationBoard.IInterpretationBoard(r, IDInterpretationBoard, nil);

                   {thisresets rect}


      if (IDInterpretationBoard <= kShapesInPalettePlus1) then
       gShapesArray[IDInterpretationBoard] := interpretationBoard;

      SetRect(r, (kPaletteWidth div 2 - kCircleRadius), (((3 * kPaletteWidth) div 2) - kCircleRadius), (kPaletteWidth div 2 + kCircleRadius), (((3 * kPaletteWidth) div 2) + kCircleRadius)); {Define the prototype}
   {                                                      shapes}
      New(circle);
      FailNil(circle);
      circle.ICircle(r, IDCircle);
      circle.fName := 'a';
      if (IDCircle <= kShapesInPalettePlus1) then
       gShapesArray[IDCircle] := circle;

      SetRect(r, 2, kPaletteWidth + 2, 18, kPaletteWidth + 18); {MF}
      OffSetRect(r, 0, kPaletteWidth);

      New(propertyF);
      FailNil(propertyF);
      propertyF.IPropertyF(r, IDPropertyF);
      propertyF.fShade := cWhite;
      propertyF.fName := 'A';
      if (IDPropertyF <= kShapesInPalettePlus1) then
       gShapesArray[IDPropertyF] := propertyF;

      OffSetRect(r, 0, kPaletteWidth);
      New(relationR);
      FailNil(relationR);

      SetPt(fromPt, r.left, r.bottom);
                   {SetPt(fromPt, r.left-5, r.bottom+5); }
   {    SetPt(toPt, r.right + 2, r.top - 2);}
      SetPt(toPt, r.right, r.top);

      relationR.IRelationR(fromPt, toPt, r, IDRelationR);
      relationR.fname := 'A';
      if (IDRelationR <= kShapesInPalettePlus1) then
       gShapesArray[IDRelationR] := relationR;

      OffSetRect(r, 0, kPaletteWidth);
      New(relationS);
      FailNil(relationS);

      SetPt(fromPt, r.left, r.bottom);
           {	SetPt(fromPt, r.left-8, r.bottom+8); }
      { SetPt(toPt, r.right + 2, r.top - 2);}
      SetPt(toPt, r.right, r.top);

      relationS.IRelationS(fromPt, toPt, r, IDFunction);
      relationS.fname := 'a';

      if (IDFunction <= kShapesInPalettePlus1) then
       gShapesArray[IDFunction] := relationS;

      OffSetRect(r, 0, kPaletteWidth);
      New(Identity);
      FailNil(Identity);

      SetPt(fromPt, r.left, r.bottom);
           {	SetPt(fromPt, r.left-2, r.bottom+2); }
       {SetPt(toPt, r.right + 2, r.top - 2);}
      SetPt(toPt, r.right, r.top);

      Identity.IIdentity(fromPt, toPt, r, IDIdentity);
      Identity.fname := 'a';
      if (IDIdentity <= kShapesInPalettePlus1) then
       gShapesArray[IDIdentity] := Identity;

     end;




       */


public int getSelection(){
     if (jToggleButton1.isSelected())
       return
           cSelectTool;
     if (fIndividualButton.isSelected())
       return
           cEllipse;
     if (fPropertyButton.isSelected())
       return
           cRectangle;
     if (fRelationButton.isSelected())
       return
           cLine;
     if (fFunctionButton.isSelected())
       return
           cFunctionLine;
     if (fIdentityButton.isSelected())
       return
          cIdentityLine;



     return
         cNoSelection;
   }


 public TShape getPrototype(){
      if (jToggleButton1.isSelected())
        return
            null;
      if (fIndividualButton.isSelected())
        return
            fProtoIndividual;
      if (fPropertyButton.isSelected())
        return
            fProtoProperty;
      if (fRelationButton.isSelected())
        return
            fProtoRelation;
      if (fFunctionButton.isSelected())
  return
      fProtoFunction;
      if (fIdentityButton.isSelected())
         return
            fProtoIdentity;


      return
          null;
    }

 class TIndividualIcon extends TIndividual implements Icon{

    TIndividualIcon(Point p){
     super(p);

     fSelected=false;
         }

    public int getIconHeight(){
           return
               12;
         }

    public int getIconWidth(){
                return
                    12;
              }

    public void paintIcon (Component c, Graphics g,
                              int x, int y){
         if (c.isEnabled()) {
                  g.setColor(c.getForeground());
              } else {
                  g.setColor(Color.gray);
              }

              g.translate(x, y);
              draw((Graphics2D)g);
              g.translate(-x, -y);  //Restore Graphics object

       }
    }

class TPropertyIcon extends TProperty implements Icon{

TPropertyIcon(){
   fWidth = 10;
   fHeight=10;
   fSelected=false;
   fName='F';
         }

    public int getIconHeight(){
           return
               12;
         }

    public int getIconWidth(){
                return
                    12;
              }

    public void paintIcon (Component c, Graphics g,
                              int x, int y){
         if (c.isEnabled()) {
                  g.setColor(c.getForeground());
              } else {
                  g.setColor(Color.gray);
              }

              g.translate(x, y);
              draw((Graphics2D)g);
              g.translate(-x, -y);  //Restore Graphics object

       }
    }

    class TRelationIcon extends TRelation implements Icon{

    TRelationIcon(){
       fWidth = 10;
       fHeight=10;
       fSelected=false;
       fName='A';
             }

        public int getIconHeight(){
               return
                   12;
             }

        public int getIconWidth(){
                    return
                        12;
                  }

public void draw(Graphics2D graphic){      // we're jumping up the hierarchy here (not drwaing fram and interior

   String nameToDraw=String.valueOf(fName)+chSuperscript2;

   TextLayout layout=getStringLayout(graphic,nameToDraw);

   Rectangle2D stringBounds=layout.getBounds();

   float middleX=6;
   float bottomY=12;

   float offSet=(float)(stringBounds.getWidth()/2);


   graphic.drawString(nameToDraw,middleX-offSet,bottomY-1);





                //    graphic.drawString(String.valueOf(fName)+chSuperscript2,2,10);
                  }

 /*



   if tempCh in gFunctors then
         DrawString(concat(tempCh, '�')) (*unary function*)
         else
         DrawString(concat(tempCh, '�')); (*binary relation*)
         end;



  */


   public void paintIcon (Component c, Graphics g,
                                  int x, int y){
             if (c.isEnabled()) {
                      g.setColor(c.getForeground());
                  } else {
                      g.setColor(Color.gray);
                  }

                  g.translate(x, y);
                  draw((Graphics2D)g);
                  g.translate(-x, -y);  //Restore Graphics object

           }
        }


        class TFunctionIcon extends TFunction implements Icon{

         TFunctionIcon(){
            fWidth = 10;
            fHeight=10;
            fSelected=false;
            fName='a';
                  }

             public int getIconHeight(){
                    return
                        12;
                  }

             public int getIconWidth(){
                         return
                             12;
                       }

     public void draw(Graphics2D graphic){      // we're jumping up the hierarchy here (not drwaing fram and interior

        String nameToDraw=String.valueOf(fName)+chSuperscript1;

        TextLayout layout=getStringLayout(graphic,nameToDraw);

        Rectangle2D stringBounds=layout.getBounds();

        float middleX=6;
        float bottomY=12;

        float offSet=(float)(stringBounds.getWidth()/2);


        graphic.drawString(nameToDraw,middleX-offSet,bottomY-1);





                     //    graphic.drawString(String.valueOf(fName)+chSuperscript2,2,10);
                       }

      /*



        if tempCh in gFunctors then
              DrawString(concat(tempCh, '�')) (*unary function*)
              else
              DrawString(concat(tempCh, '�')); (*binary relation*)
              end;



       */


        public void paintIcon (Component c, Graphics g,
                                       int x, int y){
                  if (c.isEnabled()) {
                           g.setColor(c.getForeground());
                       } else {
                           g.setColor(Color.gray);
                       }

                       g.translate(x, y);
                       draw((Graphics2D)g);
                       g.translate(-x, -y);  //Restore Graphics object

                }
        }


class TIdentityIcon extends TIdentity implements Icon{

         TIdentityIcon(){
            fWidth = 10;
            fHeight=10;
            fSelected=false;
            fName='a';
                  }

             public int getIconHeight(){
                    return
                        12;
                  }

             public int getIconWidth(){
                         return
                             12;
                       }

     public void draw(Graphics2D graphic){      // we're jumping up the hierarchy here (not drwaing fram and interior

        String nameToDraw=String.valueOf(fName)/*+chSuperscript1*/;

        TextLayout layout=getStringLayout(graphic,nameToDraw);

        Rectangle2D stringBounds=layout.getBounds();

        float middleX=6;
        float bottomY=12;

        float offSet=(float)(stringBounds.getWidth()/2);


        graphic.drawString(nameToDraw,middleX-offSet,bottomY-1);

                       }


        public void paintIcon (Component c, Graphics g,
                                       int x, int y){
                  if (c.isEnabled()) {
                           g.setColor(c.getForeground());
                       } else {
                           g.setColor(Color.gray);
                       }

                       g.translate(x, y);
                       draw((Graphics2D)g);
                       g.translate(-x, -y);  //Restore Graphics object

                }
        }


/*********************************keys  OLD STUFF****************************


 public void keyTyped(KeyEvent e) {
   TShape thePrototype=getPrototype();


   if (thePrototype!=null){
     int id = thePrototype.getTypeID();
     String names="abcdefghijklmnopqrstuvwxyz";

     char ch = e.getKeyChar();

     if ((names.indexOf(ch)>-1)&&
       (thePrototype.getName()!=ch)&&
       ((id==TShape.IDIndividual)||
        (id==TShape.IDRelationR)||
        (id==TShape.IDFunction)||
        (id==TShape.IDIdentity))){
           thePrototype.setName(ch);
           repaint();
     }

   }

            }*/


 /*

  function TShapeView.DoKeyCommand (ch: char; aKeyCode: integer; var info: EventInfo): TCommand;
  OVERRIDE;

  var
   shapeIndex: integer;

 begin

  shapeIndex := fPalette.fCurrShape;
  if (ch >= 'a') and (ch <= 'z') then
   if ((shapeIndex = IDCircle) or (shapeIndex = IDRelationR) or (shapeIndex = IDFunction) or (shapeIndex = IDIdentity)) then
    if gShapesArray[shapeIndex].fName <> ch then
     begin
      gShapesArray[shapeIndex].fName := ch;
      fPalette.UpdateInvalid(shapeIndex);
     end;

  if (ch >= 'A') and (ch <= 'Z') then
   if ((shapeIndex = IDPropertyF) or (shapeIndex = IDRelationR) or (shapeIndex = IDFunction)) then
    if gShapesArray[shapeIndex].fName <> ch then
     begin
      gShapesArray[shapeIndex].fName := ch;
      fPalette.UpdateInvalid(shapeIndex);
     end;

  DoKeyCommand := inherited DoKeyCommand(ch, aKeyCode, info);
 end;



  */

 public boolean getUseIdentity(){
     return
         fUseIdentity;
   }

 public void setUseIdentity(boolean use){
           fUseIdentity=use;
  }


 }


