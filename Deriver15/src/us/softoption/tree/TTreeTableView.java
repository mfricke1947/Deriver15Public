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

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;

public class TTreeTableView extends JTable{
  int fPreferredWidth = 100;
  int fPreferredMinimum = 50;
  int fIncrement = 9;        //width increment for each character

  int fPreferredNumWidth = 20;
  int fPreferredJustificationWidth = 80;
  int fPreferredMinimumTableWidth = 500;

  public TTreeTableView() {

  }

  public TTreeTableView(AbstractTableModel model) {

    super(model);

    setColumnSelectionAllowed(false);
    setRowSelectionAllowed(false);
    setCellSelectionEnabled(true);

    setShowHorizontalLines(false);
    setShowVerticalLines(false);
    {
      // Disable auto resizing
      setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

// Set the first visible column, the linenum to 10 pixels wide
      int vColIndex = 0;
      TableColumn col = getColumnModel().getColumn(vColIndex);
      int width = 10;
      col.setPreferredWidth(width);

// fTreeTableView.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    }

    TTreeTableCellRenderer aRenderer = new TTreeTableCellRenderer( (
        TTreeTableModel) model);

    this.setDefaultRenderer(Object.class, aRenderer); //the first parameter tells which things it applies to

    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    //This will start with 3 columns

    // Set the first visible column, the linenum to 10 pixels wide
    TableColumn col = getColumnModel().getColumn(0);
    if (col != null)
      col.setPreferredWidth(20);

    // Set the first visible column, the linenum to 10 pixels wide
    col = getColumnModel().getColumn(1);
    if (col != null)
      col.setPreferredWidth(400);

    // Set the first visible column, the linenum to 10 pixels wide
    col = getColumnModel().getColumn(2);
    if (col != null)
      col.setPreferredWidth(80);

   // this.setPreferredSize(new Dimension(500,this.getRowHeight())); don't do this

    SelectionListener listener = new SelectionListener(this);
    getSelectionModel().addListSelectionListener(listener); //same listener for both
    getColumnModel().getSelectionModel().addListSelectionListener(listener);

    getModel().addTableModelListener(new TTreeTableModelListener(this));

  }

  /*************************** Selection listener **********************/



  public class SelectionListener
      implements ListSelectionListener {
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
        // Toolkit.getDefaultToolkit().beep();
        int first = e.getFirstIndex();
        int last = e.getLastIndex();
      }
      else if (e.getSource() == table.getColumnModel().getSelectionModel()
               && table.getColumnSelectionAllowed()) {
        // Row selection changed
        // Toolkit.getDefaultToolkit().beep();
        int first = e.getFirstIndex();
        int last = e.getLastIndex();
      }

      if (e.getValueIsAdjusting()) {
        // The mouse button has not yet been released
      }
    }
  }

  /*************************** End of Selection listener **********************/


  public boolean isCellSelected(int row, // we override this because you can select only non dead non closed TreeData
                                int column) {

    if (super.isCellSelected(row, column)) {
      Object cellValue = getValueAt(row, column);
      if (cellValue instanceof DefaultMutableTreeNode) {

        Object dataValue = ( ( (DefaultMutableTreeNode) cellValue).
                            getUserObject());

        //  TTreeDataNode dataValue=(TTreeDataNode)(((DefaultMutableTreeNode)cellValue).getUserObject());

        if (dataValue instanceof TTreeDataNode && // some of the data is Strings
            ! ( (TTreeDataNode) dataValue).fClosed) // can select dead for closing branch

          return
              true;
      }
    }
    return
        false;
  }

  int[][] selectedIndices() { //returned as row index in [0] , col index in [1]

    // int maxRows = getRowCount();
    // int maxCols = getColumnCount();

    int selRows[] = getSelectedRows();
    int selRowCount = selRows.length;
    int selCols[] = getSelectedColumns();
    int selColCount = selCols.length;

    if (selRowCount == 0 || selColCount == 0)
      return
          null;

    int num = (selRowCount > selColCount ? selRowCount : selColCount); // two selected cells might be in same row (or col)

    int realCount = 0;

    for (int r = 0; (r < selRowCount) && realCount < num; r++) { // this is important because selRows and selCols return underlying selection, but we
      for (int c = 0; (c < selColCount) && realCount < num; c++) { // override isCellSelected to unselect some of these
        if (isCellSelected(selRows[r], selCols[c])) {
          realCount++;
        }
      }
    }

    num = realCount;

    if (num == 0)
      return
          null;

    int selected[][] = new int[2][num];

    int index = 0;

    for (int r = 0; (r < selRowCount) && index < num; r++) {
      for (int c = 0; (c < selColCount) && index < num; c++) {
        if (isCellSelected(selRows[r], selCols[c])) {
          selected[0][index] = selRows[r];
          selected[1][index] = selCols[c];

          index++;
        }
      }
    }

    return
        selected;
  }

  TTreeDataNode[] selectedDataNodes() {
    int[][] indices = selectedIndices();

    if (indices == null)
      return
          null;

    int num = indices[0].length;

    if (num == 0)
      return
          null;

    TTreeDataNode[] nodes = new TTreeDataNode[num];

    DefaultMutableTreeNode cellValue;

    for (int i = 0; i < num; i++) {
      cellValue = (DefaultMutableTreeNode) getValueAt(indices[0][i],
          indices[1][i]);
      nodes[i] = (TTreeDataNode) (cellValue.getUserObject());
    }
    return
        nodes;
  }

  public void selectCellContaining(DefaultMutableTreeNode node){  //for highlighting branches etc

    Object cellValue;

    int rows = getRowCount();
    int cols = getColumnCount();

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {

        cellValue = getValueAt(r,c);

        if (cellValue instanceof DefaultMutableTreeNode )

        if (cellValue==node){
          if (!(super.isCellSelected(r,c)))

          changeSelection(r,c,true,false);

        return;
        }
}

   }





  }

  /********************************* Column widths *****************************/

  public int[] getColWidths() {
    int num = getColumnCount();
    int[] output = new int[num];
    TableColumn col;

    for (int i = 0; i < num; i++) {

      col = getColumnModel().getColumn(i);
      output[i] = col.getWidth();
    }

    return
        output;
  }

  public void setColPreferredWidths(int[] widths) {
  int num = getColumnCount();

  if (widths!=null&&
      num==widths.length){

  TableColumn col;

  for (int i = 0; i < num; i++) {

    col = getColumnModel().getColumn(i);
    col.setPreferredWidth(widths[i]);
  }

  }
}


  public int[] calculateWidths(int insertIndex) {

    /*we are going to insert a new column left and right of the insertIndex. We are going
      to leave most of the old column widths alone. But, as far as we can, we will take
     the space for the new columns from the central column.
     */

    int[] oldWidths = getColWidths();
    int[] newWidths = new int[oldWidths.length + 2]; //adding two columns

    if (insertIndex != -1) {

      for (int i = 0; i < newWidths.length; i++) {
        if (i < insertIndex - 1)
          newWidths[i] = oldWidths[i];

        if (i > insertIndex + 1)
          newWidths[i] = oldWidths[i - 2];

        if (i == insertIndex - 1 || i == insertIndex + 1)
          newWidths[i] = fPreferredWidth;

        if (i == insertIndex) {
          newWidths[i] = oldWidths[i] - 2 * fPreferredWidth;
          if (newWidths[i] < fPreferredWidth)
            newWidths[i] = fPreferredMinimum;
        }
      }
    }

    return
        newWidths;
  }

  public void resetWidths() {

    TableColumn col;
    int count = getColumnCount();

    for (int i = 1; i < count - 1; i++) {

      col = getColumnModel().getColumn(i);
      col.setPreferredWidth(fPreferredWidth);
    }

    if (count > 0) {
      col = getColumnModel().getColumn(0);
      col.setPreferredWidth(fPreferredNumWidth);

      col = getColumnModel().getColumn(count - 1);
      col.setPreferredWidth(fPreferredJustificationWidth);

    }

  }

  public void resetWidths2(TTreeDataNode root) {
    /*we run down each column and set its width the width of the max entry*/

    int cols = getColumnCount();
    int rows = getRowCount();
    int colWidth = fPreferredMinimum;
    int runningWidth=0;
    TableColumn col;

    for (int j=1;j<cols-1;j++){
      colWidth = fPreferredMinimum;

      for (int i=0;i<rows;i++){

        Object cellValue = getValueAt(i, j);
        if (cellValue instanceof DefaultMutableTreeNode) {
          Object dataValue = ( ( (DefaultMutableTreeNode) cellValue).
                          getUserObject());

        if (dataValue instanceof TTreeDataNode) {
          TTreeDataNode data = (TTreeDataNode) dataValue;
          if (data==root)
            colWidth=100;         // make sure the root column is reasonably wide
          int dataWidth = data.toString().length() * fIncrement;
          colWidth = colWidth > dataWidth ?
            colWidth :
            dataWidth;
        }
      }
    }
    col = getColumnModel().getColumn(j);
    col.setPreferredWidth(colWidth);
    runningWidth+=colWidth;
  }

  if (cols>0){
    col = getColumnModel().getColumn(0);          // the lineNo
    col.setPreferredWidth(fPreferredNumWidth);
    runningWidth+=fPreferredNumWidth;

    col = getColumnModel().getColumn(cols-1);    // the justification
    col.setPreferredWidth(fPreferredJustificationWidth);
    runningWidth+=fPreferredJustificationWidth;

    if (cols>4&&runningWidth<fPreferredMinimumTableWidth){

      col = getColumnModel().getColumn(1);          // padd
      col.setPreferredWidth(col.getPreferredWidth()+
                            (fPreferredMinimumTableWidth-runningWidth)/2);

      col = getColumnModel().getColumn(cols-2);          // padd
      col.setPreferredWidth(col.getPreferredWidth()+
                      (fPreferredMinimumTableWidth-runningWidth)/2);


    }

  }
}

public void setActualColWidthsToPreferred() {

   int cols = getColumnCount();

   TableColumn col;

   for (int j=0;j<cols;j++){

   col = getColumnModel().getColumn(j);
   col.setWidth(col.getPreferredWidth());

 }
}

public void resetWidths( int [] newWidths){
 // int [] newWidths= calculateWidths(insertIndex);
  TableColumn col;

  for (int i=0;i<newWidths.length;i++){

    col = getColumnModel().getColumn(i);
    col.setPreferredWidth(newWidths[i]);
  }

}

}

/*
 Ok, this won't look as pretty, but it will be easier to implement:
Traverse the tree., counting the number of Leaf nodes (L)
Hard code the minimun distance between each leaf node (d)
Start at the top level, with nodes organized into rows.
All rows have length of L*d.
Add nodes left to right in each row.
Each node gets (number of children for this node)/(number of children for all nodes at this level) of the remaining space, and is placed at the midpoint of it's allocated space.

I think this will work, but I'm worried about it being slow, because I have to traverse before I start, and then at each row I have to know the count of children, which means an extra partial traversal.
Joel Coehoorn
Thursday, July 21, 2005

*/
