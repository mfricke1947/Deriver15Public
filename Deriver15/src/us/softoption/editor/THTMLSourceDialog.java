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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


public class THTMLSourceDialog extends JDialog{
protected boolean m_succeeded = false;
protected JTextArea fSourceText;

JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuEdit = new JMenu("Edit");


  //undo helpers
   protected UndoAction fUndoAction = new UndoAction();
   protected RedoAction fRedoAction = new RedoAction();
   protected UndoManager fUndoManager = new UndoManager();

 // find and replace

    FindDialog fFindDialog=null;

    Action fFindAction = new AbstractAction("Find...",  new ImageIcon("Find16.gif")) {
      public void actionPerformed(ActionEvent e) {
        if (fFindDialog==null)
           fFindDialog = new FindDialog(THTMLSourceDialog.this, fSourceText, 0);
        else fFindDialog.setSelectedIndex(0);
        fFindDialog.setVisible(true); } };


    Action fReplaceAction = new AbstractAction("Replace...",
 new ImageIcon("Replace16.gif")) {
 public void actionPerformed(ActionEvent e) {
 if (fFindDialog==null)
     fFindDialog = new FindDialog(THTMLSourceDialog.this,fSourceText, 1);
 else
 fFindDialog.setSelectedIndex(1);
 fFindDialog.setVisible(true);
 }
 };



public THTMLSourceDialog(JFrame parent, String source) {
super(parent, "HTML Source", true);

  jMenuEdit=createEditMenu();

  jMenuBar1.add(jMenuEdit);

  this.setJMenuBar(jMenuBar1);

JPanel pp = new JPanel(new BorderLayout());
pp.setBorder(new EmptyBorder(10, 10, 5, 10));
fSourceText = new JTextArea(source, 20, 60);

fSourceText.setFont(new Font("Courier", Font.PLAIN, 12));




  UndoableEditListener aListener= new UndoableEditAdapter(this);

       fSourceText.getDocument().addUndoableEditListener(aListener);


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


bt = new JButton("Update Journal");
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
setLocationRelativeTo(parent);
}


  protected JMenu createEditMenu() {
   JMenu menu = new JMenu("Edit");
     //   Action retrievedAction;

        //Undo and redo are actions of our own creation.

        menu.add(fUndoAction);
        menu.add(fRedoAction);
        menu.addSeparator();

        {Action action = new DefaultEditorKit.CutAction();
         action.putValue(Action.NAME, "Cut");
         menu.add(action);}


        {Action action = new DefaultEditorKit.CopyAction();
        action.putValue(Action.NAME, "Copy");
        menu.add(action);}


         {Action action = new DefaultEditorKit.PasteAction();
         action.putValue(Action.NAME, "Paste");
         menu.add(action);}

       if (TBrowser.fSelectAllAction!=null)  //fSelectAllAction is static, one for entire app
          menu.add(TBrowser.fSelectAllAction);



        menu.addSeparator();



       menu.add(fFindAction);
       menu.add(fReplaceAction);


        return menu;
    }






public boolean succeeded() {
return m_succeeded;
}
public String getSource() {
return
      fSourceText.getText();
}

public JTextArea getSourceText() {
return
      fSourceText;
}



    /********************* UndoActions [Inner Classes] *******************************/

       class UndoAction extends AbstractAction {
             public UndoAction() {
                 super("Undo");
                 setEnabled(false);
             }

             public void actionPerformed(ActionEvent e) {
                 try {
                     fUndoManager.undo();
                 } catch (CannotUndoException ex) {
                     System.out.println("Unable to undo: " + ex);
                     ex.printStackTrace();
                 }
                 updateUndoState();
                 fRedoAction.updateRedoState();
             }

             protected void updateUndoState() {
                 if (fUndoManager.canUndo()) {
                     setEnabled(true);
                     putValue(Action.NAME, fUndoManager.getUndoPresentationName());
                 } else {
                     setEnabled(false);
                     putValue(Action.NAME, "Undo");
                 }
             }
         }

       class RedoAction extends AbstractAction {
             public RedoAction() {
                 super("Redo");
                 setEnabled(false);
             }

             public void actionPerformed(ActionEvent e) {
                 try {
                     fUndoManager.redo();
                 } catch (CannotRedoException ex) {
                     System.out.println("Unable to redo: " + ex);
                     ex.printStackTrace();
                 }
                 updateRedoState();
                 fUndoAction.updateUndoState();
             }

             protected void updateRedoState() {
                 if (fUndoManager.canRedo()) {
                     setEnabled(true);
                     putValue(Action.NAME, fUndoManager.getRedoPresentationName());
                 } else {
                     setEnabled(false);
                     putValue(Action.NAME, "Redo");
                 }
             }
         }


         void undoableEditHappened(UndoableEditEvent e) {
             //Remember the edit and update the menus.
               fUndoManager.addEdit(e.getEdit());
               fUndoAction.updateUndoState();
               fRedoAction.updateRedoState();

           }




}

class UndoableEditAdapter implements javax.swing.event.UndoableEditListener {
   THTMLSourceDialog adaptee;

   UndoableEditAdapter(THTMLSourceDialog adaptee) {
     this.adaptee = adaptee;
   }
   public void undoableEditHappened(UndoableEditEvent e) {
     adaptee.undoableEditHappened(e);
   }
 }

