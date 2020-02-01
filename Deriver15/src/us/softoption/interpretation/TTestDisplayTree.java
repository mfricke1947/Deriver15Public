// TestTree.java
// A Simple test to see how we can build a tree and populate it.
//

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

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;


public class TTestDisplayTree extends JFrame {

  JTree tree;
  DefaultTreeModel fTreeModel;

//  TParser fParser;

//  public TTestNode fTestRoot= new TTestNode(fParser); // was gTestRoot

  public TTestDisplayTree(DefaultTreeModel aTreeModel) {



    super("Tree Test Example");
    setSize(400, 300);

    fTreeModel=aTreeModel;


 //   addWindowListener(new BasicWindowMonitor());

// fParser=aParser;
  }



  public void display() {
    // Build up a bunch of TreeNodes. We use DefaultMutableTreeNode because the
    // DefaultTreeModel can use that to build a complete tree.


  //  DefaultMutableTreeNode root = new DefaultMutableTreeNode(/*fTestRoot*/ "abc");



  //  DefaultMutableTreeNode subroot = new DefaultMutableTreeNode("SubRoot");
  //  DefaultMutableTreeNode leaf1 = new DefaultMutableTreeNode("Leaf 1");
  //  DefaultMutableTreeNode leaf2 = new DefaultMutableTreeNode("Leaf 2");

    // Build our tree model starting at the root node, and then make a JTree out
    // of that.
  //  treeModel = new DefaultTreeModel(root);
    tree = new JTree(fTreeModel);

    // Build the tree up from the nodes we created
 //   fTreeModel.insertNodeInto(subroot, root, 0);
 //   fTreeModel.insertNodeInto(leaf1, subroot, 0);
 //   fTreeModel.insertNodeInto(leaf2, root, 1);


 DefaultTreeCellRenderer renderer =  (DefaultTreeCellRenderer)tree.getCellRenderer();

 renderer.setLeafIcon(
      new ImageIcon("world.gif"));


    // And display it
    getContentPane().add(tree, BorderLayout.CENTER);

    setVisible(true);
  }

 /* void display() {
    TTestTree tt = new TTestTree();
    tt.init();
    tt.setVisible(true);
  } */




}
