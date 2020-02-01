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

import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import us.softoption.editor.TPreferences;


public class TProofTableView extends JTable /*JList*/{

TProofTableModel fModel;
TProofPanel fHostPanel;


public TProofTableView(TProofPanel hostPanel,/*ListModel*/ TableModel dataModel){
   super(dataModel);

   fModel=(TProofTableModel)dataModel;

   fHostPanel=hostPanel;

   getSelectionModel().addListSelectionListener(new SynchronizeSelections());
 /*  getColumnModel().addListSelectionListener(new SynchronizeSelections()); */

   /*addListSelectionListener(new SynchronizeSelections()); */ // we need for the JList selections to be allowed only in Proofline selections are

//   this.setBorder(new EmptyBorder(5,5,5,5));    //trying to get rid of gap between cells, DOES NOT WORK

  this.setRowHeight(16);   // 16 this looks better,April 04, 20px is too much, 18 too much


  int vColIndex = 0;
  TableColumn col = getColumnModel().getColumn(vColIndex);
  int width = 10;
  col.setPreferredWidth(TPreferences.fRightMargin);

  
  
  // this.setCellRenderer(new TProofListCellRenderer());  //new Dec 2007, This is default but subclasses, eg Copi will change it

 //getColumn(0).setCellRenderer(new TProofTableColumnRenderer());
  
  col.setCellRenderer(new TProofTableColumnRenderer(TProofTableModel.fProofColIndex));
  col = getColumnModel().getColumn(TProofTableModel.fJustColIndex);
  col.setCellRenderer(new TProofTableColumnRenderer(TProofTableModel.fJustColIndex));
  
  //setDefaultRenderer(TProofline.class, new TProofTableColumnRenderer());
  
  //NO RENDERER !!
  
}

void setColumnCellRenderers(TProofTableColumnRenderer firstRend,
		TProofTableColumnRenderer secondRend){
	//assumption here of two
	TableColumn col = getColumnModel().getColumn(TProofTableModel.fProofColIndex);
	col.setCellRenderer(firstRend);
	col = getColumnModel().getColumn(TProofTableModel.fJustColIndex);
	col.setCellRenderer(secondRend);
	
}

/* from Internet
 * 
 * SelectionListener listener = new SelectionListener(table);
table.getSelectionModel().addListSelectionListener(listener);
table.getColumnModel().getSelectionModel()
    .addListSelectionListener(listener);

public class SelectionListener implements ListSelectionListener {
    JTable table;

    // It is necessary to keep the table since it is not possible
    // to determine the table from the event's source
    SelectionListener(JTable table) {
        this.table = table;
    }
    public void valueChanged(ListSelectionEvent e) {
        // If cell selection is enabled, both row and column change events are fired
        if (e.getSource() == table.getSelectionModel()
              && table.getRowSelectionAllowed()) {
            // Column selection changed
            int first = e.getFirstIndex();
            int last = e.getLastIndex();
        } else if (e.getSource() == table.getColumnModel().getSelectionModel()
               && table.getColumnSelectionAllowed() ){
            // Row selection changed
            int first = e.getFirstIndex();
            int last = e.getLastIndex();
        }

        if (e.getValueIsAdjusting()) {
            // The mouse button has not yet been released
        }
    }
}

*/

public TProofline oneSelected(){

  TProofline []selection = exactlyNLinesSelected(1);

  if ((selection!=null)&&(selection.length==1))
    return
        selection[0];
  else
    return
        null;



 }


public int totalSelected()
{
  int []selections = getSelectedRows();
// Object []selections = getSelectedValues();

  return
     selections.length;

}


public TProofline[][] nSubProofsSelected(int n){

// exactly n proofs selected

 /*{The condition is that it is selected and the nextline is a blankline indicating eo subproof}
    However, we don't worry about the blankline because another routine resetSelectables? checks
  it and sets the field fSubProofSelectable*/

 /* Each subproof has to have a head and a tail, so we will return an array of two element arrays*/

 TProofline head = null, tail = null, searchline = null;
 int numFound = 0;

 //Object[] selections = getSelectedValues();
 
 int [] selections = getSelectedRows();

 int numSelections=selections.length;

 if (numSelections < n) //must have at least n, can have more if there are ordinary selections
   return
       null;
 else {
   TProofline[][] returnArray = new TProofline[n][2];

   TProofline selectedLine;

   for (int i = 0; (i < numSelections); i++) {

     selectedLine = (TProofline)getValueAt(selections[i],TProofTableModel.fProofColIndex); //3 columns
    // selectedLine = (TProofline) selections[i];

     if (selectedLine.fSubProofSelectable) { // may be one

       tail = selectedLine;
       head = null;

       Iterator iter = fModel.getHead().iterator();

       while (iter.hasNext()) {
         searchline = (TProofline) iter.next();

         if ( (searchline.fJustification.equals(TProofPanel.fAssJustification)) &&
             (searchline.fSubprooflevel == tail.fSubprooflevel) &&
             (searchline.fLineno <= tail.fLineno))

           head = searchline; // looking for last one
       }

       if (head != null) { // finished while loop and we have found one
         if (numFound >= n)
           return
               null; // there are too many, bale
         else {
           returnArray[numFound][0] = head;
           returnArray[numFound][1] = tail;

           numFound+=1;
         }

       }
     }

   }

   if (numFound == n)
     return
         returnArray;
   else
     return
         null;
 }
}


 public TProofline[] exactlyNLinesSelected(int n){

// exactly n, selected and selectable, some selected lines are not, for those indicating subproofs

     //maybe not work for 0?

int numFound = 0;

int []selections = getSelectedRows();

int numSelections=selections.length;

if (selections.length<n)   //at least n needed
   return
       null;
else
 {TProofline[]returnArray = new TProofline[n];
 TProofline selectedLine;

 for (int i = 0; i < numSelections; i++) {

    
	 selectedLine = (TProofline)getValueAt(selections[i],TProofTableModel.fProofColIndex); //2 columns
	// selectedLine = (TProofline) selections[i];
    if (selectedLine.fSelectable){
       if (numFound<n){
         returnArray[numFound] = selectedLine;
         numFound+=1;
       }
       else
         return
             null;

     }
   }

  if (numFound==n)
    return
       returnArray;
  else
    return
        null;
   }
 }


  public TProofTableView() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
   // this.setSelectionForeground(Color.black);
  }


 /*
  function TProofListView.OneSelected (var whichone: TProofline): Boolean;

    var
     index: integer;
     twofound: boolean;

    procedure CheckIt (anItem: integer);

    begin {it has to be selected and selectable, some selected lines are not, for subproofs}
     if TProofline(fProofWindow.fHead.At(anItem)).fSelectable then
      begin
       if (index = 0) then
        index := anItem
       else
        twofound := true;
      end;
    end;

   begin
    OneSelected := false;
    whichone := nil;
    index := 0;
    twofound := false;

    self.EachSelectedItemDo(CheckIt);

    if (index <> 0) & not twofound then
     begin
      whichone := TProofline(fProofWindow.fHead.At(index));
      OneSelected := true;
     end;
   end;


     */


    class SynchronizeSelections implements ListSelectionListener{
        public void valueChanged (ListSelectionEvent e){

          //this needs to unselect unselectable lines, and synchronize lines which are selected between
          //the prooflines in the data model and the java selection model. Actually we don't do second task

          TProofline theProofline;
      


          if ((!e.getValueIsAdjusting())||(e.getFirstIndex()==-1))
            return;   // nothing happening

          int first=e.getFirstIndex();
          int last=e.getLastIndex();
          
          int lastSelected=first; //index of last selected line, usually single selection

          for (int i=first;i<=last;i++){
            
        	  theProofline= (TProofline)/*fModel.*/getValueAt(i,1);
        	  theProofline= (TProofline)fModel.getElementAt(i);

            if (/*isSelectedIndex(i)*/isRowSelected(i)&&
                (!theProofline.fSelectable &&
                 !theProofline.fSubProofSelectable ))
            {
              removeRowSelectionInterval(i, i); //unselect it
             // removeSelectionInterval(i, i); //unselect it
            }
            
            if (/*isSelectedIndex(i)*/isRowSelected(i))
            	lastSelected=i;  // for changing goals or loading formulas we don't
                                 // worry if the actual proofline can be selected

          }
          
          //from to can be down or up, made error in logic about this. Fixed Jan09
          
          /*and there are two other things we'd like to do a) if they mouse on a ? ie a new goal, we
        want to change goals and b) if the input panel is up we want to load any formula they clik on into
        }it*/

         //theProofline = (TProofline) fModel.getElementAt(first); // we'll only worry about the first one

          theProofline = (TProofline) fModel.getElementAt(lastSelected); // new Jan 09
          
         if ((theProofline!=null)&& (!theProofline.fBlankline)){  // ignore clicks on blanklines

           if (!fHostPanel.usingInputPane()) { // don't change goals in the middle of something else

             if ( (first != (fModel.getHead().size()))) {
               /* we only need to change if the ? is different, the insertion ? is first item
                                                         in the tail*/

               if ("?".equals(fHostPanel.fParser.writeFormulaToString(theProofline.fFormula))) {
                 fModel.changeGoals(first);

                 fHostPanel.bugAlert("Advisory: Changing goals.",
                     "Notice that you have started to work on a different goal.");

               }
             }
           }
           else { // ie, it does have an input pane

             if (theProofline.fFormula!=null){

               String aString = fHostPanel.fParser.writeFormulaToString(theProofline.fFormula);

               if (!"?".equals(aString))
                 fHostPanel.display(aString);
             }

           }
         }

        }
      }





/*
        function TProofListView.DoMouseCommand (var theMouse: POINT; var info: EventInfo; var hysteresis: POINT): TCommand;
        OVERRIDE;

        var
         aString: Str255;
         entryView: TEditText;

         whichPart: GridViewPart;
         aRow, aCol: integer;
         aCell: GridCell;
         top, tailtop: integer;
         theProofLine: TProofline;
         anItem: integer;

       begin
                {intercepts mousedowns to loadup selection}

        if self.focus then
         ;

        whichPart := IdentifyPoint(theMouse, aRow, aCol);
        aCell.h := aCol;
        anItem := aRow;
        aString := strNull;
        if whichPart = inCell then
         if aCell.h = 1 then
          begin
           top := fProofWindow.fHead.fSize;
           tailtop := fProofWindow.fTail.fSize;

           if anItem <= top then {first list}
            begin
             theProofLine := TProofline(fProofWindow.fHead.At(anItem));
             ProperWriteFormula(theProofLine.fFormula, aString);

            end
           else if anItem <= (top + tailtop) then {second list}
            begin
             theProofLine := TProofline(fProofWindow.fTail.At(anItem - top));
             ProperWriteFormula(theProofLine.fFormula, aString);
            end;

                          { GetText(aCell,aString);  contains line nos etc}
           entryView := TEditText(fProofWindow.FindSubView('Txtp'));
           entryView.SetText(aString, TRUE);
                          {entryView.SetSelection(0,length(aString),TRUE); better without this}


           if (TProofWindow(fProofWindow).fRewrite <> nil) then {check, a patch fix this}
            if CanSelectItem(anItem) then
             begin
             if IsItemSelected(anItem) then
             aString := strNull; {the mousedown will deselect}

                                          {LOADs into rewrite window}
             entryView := TEditText(TProofWindow(fProofWindow).fRewrite.FindSubView('VW03')); {edit text}
             entryView.SetText(aString, TRUE);

                               {static text}
             TStaticText(TProofWindow(fProofWindow).fRewrite.FindSubView('VW05')).SetText(aString, TRUE);
             end;


           if (aString = '?') and (anItem <> top + 1) then
            fProofWindow.ChangeGoals(anItem);

          end;

        if self.focus then
         ; {check mf}

        DoMouseCommand := inherited DoMouseCommand(theMouse, info, hysteresis);

       end;


     */


}


