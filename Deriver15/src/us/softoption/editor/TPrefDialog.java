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

package us.softoption.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*import java.io.*;
import java.net.*;
import java.util.*; */

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
//import javax.swing.text.*;
//import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
//import javax.swing.text.html.*;

//import javax.swing.undo.*;


public class TPrefDialog extends JDialog{
protected boolean m_succeeded = false;
protected JTextArea fSourceText;



  //undo helpers
 /*  protected UndoAction fUndoAction = new UndoAction();
   protected RedoAction fRedoAction = new RedoAction();
   protected UndoManager fUndoManager = new UndoManager();

 // find and replace

    FindDialog fFindDialog=null;  */





public TPrefDialog(/*JFrame parent, String source*/) {
//super(parent, "HTML Source", true);



JPanel pp = new JPanel(new BorderLayout());
pp.setBorder(new EmptyBorder(10, 10, 5, 10));
fSourceText = new JTextArea("Help", 20, 60);

fSourceText.setFont(new Font("Courier", Font.PLAIN, 12));
fSourceText.setLineWrap(true);
fSourceText.setWrapStyleWord(true);


//  UndoableEditListener aListener= new UndoableEditAdapter(this);

//       fSourceText.getDocument().addUndoableEditListener(aListener);




JScrollPane sp = new JScrollPane(fSourceText);
pp.add(sp, BorderLayout.CENTER);
JPanel p = new JPanel(new FlowLayout());
JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 0));

JButton bt = new JButton("Cancel");
ActionListener lst = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
    dispose();
    }
    };
    bt.addActionListener(lst);
    p1.add(bt);


bt = new JButton("Set");
lst = new ActionListener() {
public void actionPerformed(ActionEvent e) {
m_succeeded = true;
dispose();
}
};
bt.addActionListener(lst);
p1.add(bt);


p.add(p1);
pp.add(p, BorderLayout.SOUTH);
getContentPane().add(pp, BorderLayout.CENTER);
pack();
setResizable(true);
//setLocationRelativeTo(parent);
}








public boolean succeeded() {
return m_succeeded;
}
/*public String getSource() {
return
      fSourceText.getText();
}*/








}



