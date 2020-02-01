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

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import us.softoption.interpretation.TTestNode;

/*

The data structure is a tree, which we are going to display in the standard 'inverted tree' form.

We need to map the tree to the table. The levels in the tree are just going to be the rows in the table.
But then, for any particular level or row, it is going to go <blank,data,blank, data etc blank> or
 <blank,blank,blank,data,blank, data etc blank,blank,blank> ie the data fills from the middle


About the tree structure.... The Model is TTreeModel which is a subclass of DefaultTreeModel

Then the Nodes in the tree are DefaultMutableTreeNode but the data this carries is TTestNode and the
TTestNode itself  has a reference back to the DefaultMutableTreeNode which is hosting it, thus

public DefaultMutableTreeNode fTreeNode= new DefaultMutableTreeNode(this); // the node that holds me

So the Nodes have two faces.

[Even I get confused...]

*/

public class TTreeTableModel extends AbstractTableModel{

int fColumnCount=0;
int fRowCount=0;
int fLeaves=0;

Object [][] fData=new Object[1][1] ;

DefaultMutableTreeNode fHostRoot=new DefaultMutableTreeNode(new TTreeDataNode(null,null));  //dummy
TTestNode fRoot=null;  // this is the data going with the fHostRoot

//TTreeModel fTreeModel=null; FEB08        // like DefaultTreeModel, but with context for variables




  public TTreeTableModel(){

/* OLD TEST DATA

       DefaultMutableTreeNode leaf1=new DefaultMutableTreeNode("Leaf1");
 DefaultMutableTreeNode leaf2=new DefaultMutableTreeNode("LeftDiag");
 DefaultMutableTreeNode leaf3=new DefaultMutableTreeNode("RightDiag");
 DefaultMutableTreeNode leaf4=new DefaultMutableTreeNode("Leaf4");
  DefaultMutableTreeNode leaf5=new DefaultMutableTreeNode("Leaf5");
  DefaultMutableTreeNode leaf6=new DefaultMutableTreeNode("Leaf6");
  DefaultMutableTreeNode leaf7=new DefaultMutableTreeNode("Leaf7");

 leaf1.add(leaf2);
 leaf1.add(leaf3);
  leaf3.add(leaf4);
  leaf4.add(leaf5);

  leaf2.add(leaf6);
  leaf6.add(leaf7); */

  //  fHostRoot.add(leaf1);


 updateCache();  // need this FEB08

  }

  public TTreeTableModel(/*TTreeModel aModel,*/ TTestNode aRoot){
 //   fTreeModel=aModel;
    fRoot=aRoot;

    fHostRoot=fRoot.fTreeNode;

    updateCache();

  }

/*  public void setHost(JTable host){
     fHostTable=host;

  } */


 /************* getters and setters for Beans and File IO ******************/

 public DefaultMutableTreeNode getHostRoot(){
   return
       fHostRoot;
 }

public void setHostRoot(DefaultMutableTreeNode aRoot){
      fHostRoot=aRoot;
}

public TTestNode getRoot(){
      return
          fRoot;
    }

public void setRoot(TTestNode aRoot){
         fRoot=aRoot;
}

 /************* needed override/implementation methods ******************/



public int getColumnCount(){             /*branches*2 +1 */
   return
       fColumnCount;

/*  int branches = fHostRoot.getLeafCount();   //should cache
  return

      (branches*2)+1;*/
}

public int getRowCount(){
  return
      fRowCount;
/*  return
      fHostRoot.getDepth()+1; */
}


/*

We need to map the tree to the table. The levels in the tree are just going to be the rows in the table.
But then, for any particular level or row, it is going to go <blank,data,blank, data etc blank> or
 <blank,blank,blank,data,blank, data etc blank,blank,blank> ie the data fills from the middle


*/
public Object getValueAt(int row, int column){

  if ((row<0||row>fRowCount-1)||
      (column<0||column>fColumnCount-1))
    return
        null;
  else
  return
      fData[row][column];
}

public boolean isSelectable(int row, int column){

  Object value =getValueAt(row,column);

  if (value instanceof DefaultMutableTreeNode)
    return
        true;
  else
    return
        false;


}

/************* End of needed override/implementation methods ******************/



/***************   Data cache ************************************/

/*I'm struggling with the placement algorithm.  Here are some requirements

 a) every leaf in its own colum.
 b) small number of colums
 c) roughly central and symmetrical


 Try by merging subtrees from leaves and recursing. What I need to know is an array of the
 subtree and column the root is in.

 */

class PlacementData {
  Object [][] fData=new Object[1][1];
  int fRootIndex=0;
}

PlacementData join(DefaultMutableTreeNode joinNode,PlacementData subtree1,PlacementData subtree2){
  if (subtree1==null)
    return
        subtree2;
  if (subtree2==null)
   return
       subtree1;

  PlacementData output = new PlacementData();

  /*side by side subtrees */

  int subtree1Cols=subtree1.fData[0].length;
  int subtree2Cols=subtree2.fData[0].length;

  int cols = subtree1Cols +
             subtree2Cols +
             1                            //extra column for join
             -2;                          // but need only one lineNo col and justCol
  int rows=subtree1.fData.length>subtree2.fData.length?
           subtree1.fData.length:
           subtree2.fData.length;         // need max rows

  rows+=1;                               // for join


  output.fData= new Object[rows][cols];  // initialize

  for (int i=0;i<rows;i++){
    for (int j=0;j<cols;j++){
    output.fData[i][j]="";
  }
}


  for(int row=0;row<subtree1.fData.length;row++){
    for (int col = 1; col < subtree1Cols-1; col++) {   //omit lineNo and Just
      output.fData[row + 1][col] = subtree1.fData[row][col];
    }
    // now for lineNo and justification
    Object maybeLineNo = subtree1.fData[row][0];
    Object maybeJustification = subtree1.fData[row][subtree1Cols-1];

    if (maybeLineNo instanceof Integer){
      output.fData[row + 1][0]=maybeLineNo;
      output.fData[row + 1][cols-1]=maybeJustification;

    }

  }

  int colOffset=subtree1Cols-1;

 for(int row=0;row<subtree2.fData.length;row++){
   for(int col=1;col<subtree2Cols-1;col++){            //omit lineNo and Just
     output.fData[row+1][col+colOffset]=subtree2.fData[row][col];
   }
   // now for lineNo and justification
   Object maybeLineNo = subtree2.fData[row][0];
   Object maybeJustification = subtree2.fData[row][subtree2Cols-1];

   if (maybeLineNo instanceof Integer){
     output.fData[row + 1][0]=maybeLineNo;
     output.fData[row + 1][cols-1]=maybeJustification;

}

  }

  output.fData[0][subtree1Cols-1] = joinNode;   // goes where the just used to be
  output.fRootIndex=subtree1Cols-1;    //put it between two

  if(joinNode.getUserObject() instanceof TTreeDataNode){      //can be String
    TTreeDataNode data=(TTreeDataNode) (joinNode.getUserObject());

    int lineNo=data.fLineno;
    int justNo=data.fFirstjustno;
    int secondJustNo=data.fSecondjustno;
    String justification = data.fJustification;

    if (lineNo>0)
      output.fData[0][0]= new Integer(lineNo);
// a lineNo of 0 is an error, also the closing Xs of closed branches
// it can be a node, the closing X of a closed branch -> no lineNo
// it can be the string "Vertical" for vertical line, no lineNumber from the line

    output.fData[0][cols-1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
                              ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
                              justification; //if a row has formula nodes all of them have the same justification

}




 return
     output;
}

PlacementData extend(DefaultMutableTreeNode extendNode,PlacementData subtree1){


    PlacementData output = new PlacementData();

    /*put node on top */

    int subtree1Cols=subtree1.fData[0].length;

    int cols = subtree1Cols;
    int rows=subtree1.fData.length;

    output.fData= new Object[rows+1][cols];  // initialize
    for (int i=0;i<rows+1;i++){
      for (int j=0;j<cols;j++){
        output.fData[i][j]="";
      }
    }


    for (int i=0;i<rows;i++){
      for (int j=0;j<cols;j++){

      output.fData[i + 1][j] = subtree1.fData[i][j];
    }
  }

    output.fData[0][subtree1.fRootIndex] = extendNode;
    output.fRootIndex=subtree1.fRootIndex;

    Object userObject=extendNode.getUserObject();

    if(userObject instanceof TTreeDataNode){      //can be String
      TTreeDataNode data=(TTreeDataNode) (extendNode.getUserObject());

      int lineNo=data.fLineno;
      int justNo=data.fFirstjustno;
      int secondJustNo=data.fSecondjustno;
      String justification = data.fJustification;

      if (lineNo>0)
        output.fData[0][0]= new Integer(lineNo);
     // a lineNo of 0 is an error, also the closing Xs of closed branches
     // it can be a node, the closing X of a closed branch -> no lineNo
     // it can be the string "Vertical" for vertical line, no lineNumber from the line


      output.fData[0][cols-1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
          ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
          justification; //if a row has formula nodes all of them have the same justification

}

    if(userObject instanceof String){               //can be String
      if ( ((String) userObject).equals("LeftDiag"))
          {   // put spaces in if it is spreading horizontally

        for (int i = output.fRootIndex + 1; i < cols; i++)
          output.fData[0][i] = new String("Horizontal");
      }
      if ( ((String) userObject).equals("RightDiag"))
      {

        for (int i = 0; i < output.fRootIndex; i++)
          output.fData[0][i] = new String("Horizontal");
      }

    }
   return
       output;
}

PlacementData leaf(DefaultMutableTreeNode leaf){
  PlacementData output = new PlacementData();

  output.fData= new Object[1][3];
  output.fData[0][0]="";
  output.fData[0][1]=leaf;
  output.fData[0][2]="";
  output.fRootIndex= 1;

  if(leaf.getUserObject() instanceof TTreeDataNode){      //can be String
    TTreeDataNode data=(TTreeDataNode) (leaf.getUserObject());

    int lineNo=data.fLineno;
    int justNo=data.fFirstjustno;
    int secondJustNo=data.fSecondjustno;
    String justification = data.fJustification;

    if (lineNo>0)
      output.fData[0][0]= new Integer(lineNo);
// a lineNo of 0 is an error, also the closing Xs of closed branches
// it can be a node, the closing X of a closed branch -> no lineNo
// it can be the string "Vertical" for vertical line, no lineNumber from the line

    output.fData[0][2] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
        ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
      justification; //if a row has formula nodes all of them have the same justification

}


  return
     output;
}

PlacementData newPlaceDescendants(DefaultMutableTreeNode start){
  int children = start.getChildCount();

   switch (children){
     case 0:
       return
           leaf(start);

     case 1:
       DefaultMutableTreeNode onlyChild=(DefaultMutableTreeNode)(start.getFirstChild());

       return
           extend(start,newPlaceDescendants(onlyChild));


     case 2:
       DefaultMutableTreeNode leftChild=(DefaultMutableTreeNode)(start.getFirstChild());
       DefaultMutableTreeNode rightChild=(DefaultMutableTreeNode)(start.getChildAt(1));
       return
           join(start,newPlaceDescendants(leftChild),newPlaceDescendants(rightChild));
   }
   return
       null;

}

/******************************/

void placeDescendants(DefaultMutableTreeNode start,int row,int column){
  int children = start.getChildCount();

  switch (children){
    case 0:
      return;

    case 1:
      DefaultMutableTreeNode onlyChild=(DefaultMutableTreeNode)(start.getFirstChild());


      fData[row+1][column]=onlyChild;

      if(onlyChild.getUserObject() instanceof TTreeDataNode){      //can be String
        TTreeDataNode data=(TTreeDataNode) (onlyChild.getUserObject());

        int lineNo=data.fLineno;
        int justNo=data.fFirstjustno;
        int secondJustNo=data.fSecondjustno;
        String justification = data.fJustification;

        fData[row+1][0]= new Integer(lineNo); // it can be the string "Vertical" for vertical line, no lineNumber from the line
        fData[row+1][fColumnCount - 1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
            ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
            justification; //if a row has formula nodes all of them have the same justification

      }

      placeDescendants(onlyChild,row+1, column);

      return;

    case 2:
      DefaultMutableTreeNode leftChild=(DefaultMutableTreeNode)(start.getFirstChild());
      DefaultMutableTreeNode rightChild=(DefaultMutableTreeNode)(start.getChildAt(1));

      fData[row+1][column-1]=leftChild;                //the immediate children are left and right diagonals
      fData[row+1][column+1]=rightChild;               //ie they are a blank line and have no line number, and no justification

      placeDescendants(leftChild,row+1, column-1);
      placeDescendants(rightChild,row+1, column+1);
      return;

  }

}

public int getTreeDepth(){
  return
      fHostRoot.getDepth();
}


int stepsLeft(DefaultMutableTreeNode node){

// counting leaves to the left of the root

if (node.getChildCount()==0)
    return
        0;                            // none

DefaultMutableTreeNode firstChild=(DefaultMutableTreeNode)(node.getFirstChild());

if (node.getChildCount()==1)
    return
       stepsLeft(firstChild);    // keep going down

DefaultMutableTreeNode secondChild=(DefaultMutableTreeNode)(node.getChildAt(1));


return
     ((stepsLeft(firstChild)+1)>(stepsLeft(secondChild)-2)?
      (stepsLeft(firstChild)+1):
     (stepsLeft(secondChild)-2));    // possible to step one right, then a whole bunch left

}

int indexOfRoot(){
  int leftLeaves=stepsLeft(fHostRoot);

  /*then index 0 is lineNo,so root wants to get stepsLeft +1 in*/

return
     leftLeaves+1;
}


public void updateCache(){         //inefficient algorithm, but rarely executed

 if (fHostRoot==null)
   return;



PlacementData data = newPlaceDescendants(fHostRoot);

 fData=data.fData;
 fRowCount=fData.length;
 fColumnCount=fData[0].length;


/* //OLD
 fRowCount= fHostRoot.getDepth()+1;
            // + numBlankRows();

 fLeaves= fHostRoot.getLeafCount();
// fLeftLeaves=leftLeafCount(fHostRoot);

 fColumnCount= (fLeaves==1?3:fLeaves+1+2);  //leaves+1 for leaves, 2 colums for No and just, and root is a special case

  fData= new Object[fRowCount][fColumnCount];

  for (int i=0;i<fRowCount;i++){
    for (int j=0;j<fColumnCount;j++){

      fData[i][j]="";
    }
  }

  int lineNo=((TTreeDataNode) fHostRoot.getUserObject()).fLineno;

  int hostIndex=indexOfRoot();

  fData[0][hostIndex]=fHostRoot;   //put the root hard to the left
  fData[0][0]= new Integer(lineNo);

  int justNo=((TTreeDataNode) fHostRoot.getUserObject()).fFirstjustno;
  String justification = ((TTreeDataNode) fHostRoot.getUserObject()).fJustification;
  fData[0][fColumnCount - 1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
    justification; //if a row has formula nodes all of them have the same justification


  placeDescendants(fHostRoot,0,hostIndex);

End of OLD */
}

/*OLD

  //now to fill data

Enumeration  breadthFirst=fHostRoot.breadthFirstEnumeration();

 ArrayList l;
 int level=0;
 int numOnLevel=0;
 int paddingOnEnds=0;
 int lineNo=1;

 DefaultMutableTreeNode next=null;

 if (breadthFirst.hasMoreElements())
   next=(DefaultMutableTreeNode)(breadthFirst.nextElement());


 boolean spacerRow=false;

 for (int row=0;row<fRowCount;row++){

   if (!spacerRow){
     fData[row][0]= new Integer(lineNo);           // row/line number
     lineNo+=1;
   }
   else
     spacerRow=false;                             // no lineno and reset

/*The row after a row with multiple children is a spacer. So we detect spacer
lower down, and act and reset on entry

   l=new ArrayList();

   while (level==row){
     l.add(next);

    if (next.getChildCount()>1)
         spacerRow=true;

     if (breadthFirst.hasMoreElements()){
       next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
       level=next.getLevel();
     }
     else
       break;
   }

   /*we now have all of one level in our array list
    and we start transferring them, with spacing blanks into our data structure

   int tempIndex=0;
   Object colData;

   for (int column=0;column<fColumnCount;column++){

      numOnLevel=l.size();

      paddingOnEnds= (fColumnCount - numOnLevel - (numOnLevel-1))/2;

      if (column<paddingOnEnds||(fColumnCount-1)-column<paddingOnEnds){
      //  fData[row][column] = new DefaultMutableTreeNode("padding"); //new String("padding");  //
        continue;
      }

      // blanks on left and right ends

 //at this stage we've done all the evens ie the interbranch padding, and left and right padding

 //now to map one index to the other

     tempIndex=column;

     tempIndex-=paddingOnEnds; // this will give us zero based indices eg 0,1,2,3 4

     if (tempIndex%2==1){            // all odd columns are blank, the spaces between entries
      // fData[row][column] = "more padding";  // initialized balnk
       continue;
     }

     tempIndex/=2;

     colData=l.get(tempIndex);

     if (colData instanceof DefaultMutableTreeNode){
       DefaultMutableTreeNode temp=(DefaultMutableTreeNode)colData;
       if(temp.getUserObject() instanceof TTreeDataNode){
         int justNo=((TTreeDataNode) temp.getUserObject()).fFirstjustno;
         String justification = ((TTreeDataNode) temp.getUserObject()).fJustification;


         fData[row][fColumnCount -1] = ((justNo==0)?"":String.valueOf(justNo)) + justification; //if a row has formula nodes all of them
         // have the same justification
       }
     }


     fData[row][column] = colData;

     int dummy=0;  //for debug stop
   }


 } */

 /****** OLD *******/

/*  for (int row=0;row<fRowCount;row++){
    for (int col=0;col<fRowCount;col++){

      fData[row][col]= getDataAt(row,col);

    }

  }


} */

/*  OLD

private Object getDataAt(int row, int column) {
    int level=0;

     Enumeration  breadthFirst=fHostRoot.breadthFirstEnumeration();   //unfortunately an enumeration can be used only once, so
                                                                 // I am repeating lots here. Needs rewrite

 ArrayList l=new ArrayList();

 while ((breadthFirst.hasMoreElements()&&level<=row)){
   DefaultMutableTreeNode next=(DefaultMutableTreeNode)(breadthFirst.nextElement());
   level=next.getLevel();
   if (level==row)
     l.add(next);
 }

 int numOnLevel=l.size();
 int totalColumns=getColumnCount();   // now need to fill odd columns centered on middle

 int paddingOnEnds= (totalColumns - numOnLevel - (numOnLevel-1))/2;

 if (column<paddingOnEnds||(totalColumns-1)-column<paddingOnEnds)
    return
       "";   // blanks on left and right ends

 //at this stage we've done all the evens ie the interbranch padding, and left and right padding

 //now to map one index to the other

 column-=paddingOnEnds; // this will give us zero based indices eg 0,1,2,3 4

 if (column%2==1)            // all odd columns are blank, the spaces between entries
   return
       "";
                         // this leaves us with 0,2,4

 column/=2;             // this will remove allowance for the blanks between branches eg 0,1,2

  return
     l.get(column);
}

*/



public String getColumnName(int c){

  return
      " ";                               // blank (not null) headers,

}

public int getItsColumn(Object thisOne){
  int value=-1;

 for (int i=0;i<fRowCount;i++){
   for (int j=0;j<fColumnCount;j++){

     if (fData[i][j] == thisOne)
       return
           j;
   }
 }

 return
     value;
}

public static int ROWCHANGE=1;
public static int COLCHANGE=2;

public void treeChanged(int type, Object entry ){
  updateCache();

if (type==ROWCHANGE)
  fireTableRowsInserted(0, 1);

//fireTableDataChanged();

if (type==COLCHANGE){
  fireTableRowsInserted(0, 1);

  fireTableStructureChanged();

 // fireTableRowsInserted(0, 1);

 // fireTableDataChanged();

//  TableModelEvent e= new TableModelEvent(this, 0, 0, getItsColumn(entry));

//  fireTableChanged(e);

}

  //fireTableStructureChanged();
}








}





