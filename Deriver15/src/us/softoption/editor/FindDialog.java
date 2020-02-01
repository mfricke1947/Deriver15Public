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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;


/*NOT read June 10 2006*/


class FindDialog extends JDialog {
protected Component /*JFrame /*TBrowser /*HtmlProcessor*/ fOwner;   // it needs to have an owner frame higher which will
                                                //minimize it if necessary

protected JTextComponent/*JTextPane*/ fTextPane;


protected JTabbedPane m_tb;
protected JTextField m_txtFind1;
protected JTextField m_txtFind2;
protected Document m_docFind;
protected Document m_docReplace;
protected ButtonModel m_modelWord;
protected ButtonModel m_modelCase;

 /*
protected ButtonModel m_modelUp;
protected ButtonModel m_modelDown;

  */

protected int m_searchIndex = -1;
//protected boolean m_searchUp = false;
protected String m_searchData;

public FindDialog(JFrame owner, JTextComponent theTextComponent, int index){

  // index toggles between find and replace

   super(owner, "Find and Replace", false);
   fOwner = owner;

  FindDialogInitialize(theTextComponent,index);
}

 public FindDialog(JDialog owner, JTextComponent theTextComponent, int index){
    super(owner, "Find and Replace", false);
    fOwner = owner;

 FindDialogInitialize(theTextComponent,index);

 }

 public void setFindAndReplace(String findStr,
                         String replaceStr){
    try{

      m_docFind.remove(0,m_docFind.getLength());

      m_docFind.insertString(0, findStr, null);
    }
    catch (BadLocationException ex) {}


    try{
      m_docReplace.remove(0,m_docReplace.getLength());
      m_docReplace.insertString(0, replaceStr, null);
    }
    catch (BadLocationException ex) {}

 }

private void FindDialogInitialize(JTextComponent theTextComponent,

                                  int index) {


//  super(owner, "Find and Replace", false);


 //   fOwner = owner;

    fTextPane=theTextComponent;




    m_tb = new JTabbedPane();
// "Find" panel
    JPanel p1 = new JPanel(new BorderLayout());
    JPanel pc1 = new JPanel(new BorderLayout());
    JPanel pf = new JPanel();
    pf.setLayout(new DialogLayout2(20, 5));
    pf.setBorder(new EmptyBorder(8, 5, 8, 0));
    pf.add(new JLabel("Find what:"));
    m_txtFind1 = new JTextField();
    m_docFind = m_txtFind1.getDocument();
    pf.add(m_txtFind1);
    pc1.add(pf, BorderLayout.CENTER);
    JPanel po = new JPanel(new GridLayout(1,1/*2, 2*/, 8, 2));
    po.setBorder(new TitledBorder(new EtchedBorder(),
                                  "Options"));
    JCheckBox chkWord = new JCheckBox("Whole words only");
    chkWord.setMnemonic('w');
    m_modelWord = chkWord.getModel();
    po.add(chkWord);

 /*   ButtonGroup bg = new ButtonGroup();

    JRadioButton rdUp = new JRadioButton("Search up");
  rdUp.setMnemonic('u');
  m_modelUp = rdUp.getModel();
  bg.add(rdUp);
  po.add(rdUp); */

  JCheckBox chkCase = new JCheckBox("Match case");
  chkCase.setMnemonic('c');
  m_modelCase = chkCase.getModel();
  po.add(chkCase);

  /*
  JRadioButton rdDown = new JRadioButton("Search down", true);
  rdDown.setMnemonic('d');
  m_modelDown = rdDown.getModel();
  bg.add(rdDown);
  po.add(rdDown); */

  pc1.add(po, BorderLayout.SOUTH);
  p1.add(pc1, BorderLayout.CENTER);
  JPanel p01 = new JPanel(new FlowLayout());
  JPanel p = new JPanel(new GridLayout(2, 1, 2, 8));

  ActionListener findAction = new ActionListener() {
  public void actionPerformed(ActionEvent e) {
  findNext(false, true);
  }
  };
  JButton btFind = new JButton("Find Next");
  btFind.addActionListener(findAction);
  btFind.setMnemonic('f');
  p.add(btFind);
  ActionListener closeAction = new ActionListener() {
  public void actionPerformed(ActionEvent e) {
  setVisible(false);
  }
  };
  JButton btClose = new JButton("Close");
  btClose.addActionListener(closeAction);
  btClose.setDefaultCapable(true);
  p.add(btClose);
  p01.add(p);
  p1.add(p01, BorderLayout.EAST);
  m_tb.addTab("Find", p1);
// "Replace" panel
  JPanel p2 = new JPanel(new BorderLayout());
  JPanel pc2 = new JPanel(new BorderLayout());
  JPanel pc = new JPanel();
  pc.setLayout(new DialogLayout2(20, 5));
  pc.setBorder(new EmptyBorder(8, 5, 8, 0));
  pc.add(new JLabel("Find what:"));


  m_txtFind2 = new JTextField();
  m_txtFind2.setDocument(m_docFind);
  pc.add(m_txtFind2);
  pc.add(new JLabel("Replace:"));
  JTextField txtReplace = new JTextField();
  m_docReplace = txtReplace.getDocument();
  pc.add(txtReplace);
  pc2.add(pc, BorderLayout.CENTER);
  po = new JPanel(new GridLayout(1,1/*2, 2*/, 8, 2));
  po.setBorder(new TitledBorder(new EtchedBorder(),
  "Options"));
  chkWord = new JCheckBox("Whole words only");
  chkWord.setMnemonic('w');
  chkWord.setModel(m_modelWord);
  po.add(chkWord);

  /*
  bg = new ButtonGroup();
  rdUp = new JRadioButton("Search up");
  rdUp.setMnemonic('u');
  rdUp.setModel(m_modelUp);
  bg.add(rdUp);
  po.add(rdUp); */
  chkCase = new JCheckBox("Match case");
  chkCase.setMnemonic('c');
  chkCase.setModel(m_modelCase);
  po.add(chkCase);
  /*
  rdDown = new JRadioButton("Search down", true);
  rdDown.setMnemonic('d');
  rdDown.setModel(m_modelDown);
  bg.add(rdDown);
  po.add(rdDown); */

  pc2.add(po, BorderLayout.SOUTH);
  p2.add(pc2, BorderLayout.CENTER);
  JPanel p02 = new JPanel(new FlowLayout());
  p = new JPanel(new GridLayout(3, 1, 2, 8));
  ActionListener replaceAction = new ActionListener() {
  public void actionPerformed(ActionEvent e) {
  findNext(true, true);
  }
  };
  JButton btReplace = new JButton("Replace");
  btReplace.addActionListener(replaceAction);
  btReplace.setMnemonic('r');
  p.add(btReplace);
  ActionListener replaceAllAction = new ActionListener() {
  public void actionPerformed(ActionEvent e) {
  int counter = 0;
  while (true) {
    int result = findNext(true, false);
    if (result < 0)
    return;
    else if (result == 0)
    break;
    counter++;
    }
    JOptionPane.showMessageDialog(fOwner,
    counter+" replacement(s) have been done",
    "Help", //HtmlProcessor.APP_NAME,
    JOptionPane.INFORMATION_MESSAGE);
    }
    };
    JButton btReplaceAll = new JButton("Replace All");
    btReplaceAll.addActionListener(replaceAllAction);
    btReplaceAll.setMnemonic('a');
    p.add(btReplaceAll);
    btClose = new JButton("Close");
    btClose.addActionListener(closeAction);
    btClose.setDefaultCapable(true);
    p.add(btClose);
    p02.add(p);
    p2.add(p02, BorderLayout.EAST);
// Make button columns the same size
    p01.setPreferredSize(p02.getPreferredSize());
    m_tb.addTab("Replace", p2);
    m_tb.setSelectedIndex(index);
    JPanel pp = new JPanel(new BorderLayout());
    pp.setBorder(new EmptyBorder(5,5,5,5));
    pp.add(m_tb, BorderLayout.CENTER);
    getContentPane().add(pp, BorderLayout.CENTER);
    pack();
    setResizable(false);
    setLocationRelativeTo(fOwner/*owner*/);
    WindowListener flst = new WindowAdapter() {
    public void windowActivated(WindowEvent e) {
    m_searchIndex = -1;
    }
    public void windowDeactivated(WindowEvent e) {
    m_searchData = null;
    }
    };
    addWindowListener(flst);
    }












    public void setSelectedIndex(int index) {
    m_tb.setSelectedIndex(index);
    setVisible(true);
    m_searchIndex = -1;
}


  public int findNext(boolean doReplace, boolean showWarnings) {



   JTextComponent /*JTextPane*/ monitor = fTextPane; //remove this local //fOwner.getJournalPane(); /*getTextPane();*/

  int pos = monitor.getCaretPosition();

  /*
  if (m_modelUp.isSelected() != m_searchUp) {
  m_searchUp = m_modelUp.isSelected();
  m_searchIndex = -1;
  } */

  if (m_searchIndex == -1) {
  try {
  Document doc = monitor.getDocument(); /*fOwner.getDocument();*/
  /*if (m_searchUp)
  m_searchData = doc.getText(0, pos);
  else */
  m_searchData = doc.getText(pos, doc.getLength()-pos);
  m_searchIndex = pos;
  }
  catch (BadLocationException ex) {
  warning(ex.toString());
  return -1;
  }
  }
  String key = "";
  try {
  key = m_docFind.getText(0, m_docFind.getLength());
  }
  catch (BadLocationException ex) {}
  if (key.length()==0) {
  warning("Please enter the target to search");
  return -1;
  }
  if (!m_modelCase.isSelected()) {
  m_searchData = m_searchData.toLowerCase();
  key = key.toLowerCase();
  }
  if (m_modelWord.isSelected()) {
  for (int k=0; k<Utils.WORD_SEPARATORS.length; k++) {
  if (key.indexOf(Utils.WORD_SEPARATORS[k]) >= 0) {
  warning("The text target contains an illegal "+
  "character \'"+Utils.WORD_SEPARATORS[k]+"\'");
  return -1;
  }
  }
  }
  String replacement = "";
  if (doReplace) {
  try {
  replacement = m_docReplace.getText(0,
  m_docReplace.getLength());
      } catch (BadLocationException ex) {}
      }
      int xStart = -1;
      int xFinish = -1;
      while (true)
      {
    /*  if (m_searchUp)
      xStart = m_searchData.lastIndexOf(key, pos-1);
      else */
      xStart = m_searchData.indexOf(key, pos-m_searchIndex);
      if (xStart < 0) {
      if (showWarnings)
      warning("Text not found");
      return 0;
      }
      xFinish = xStart+key.length();
      if (m_modelWord.isSelected()) {
      boolean s1 = xStart>0;
      boolean b1 = s1 && !Utils.isSeparator(m_searchData.charAt(
      xStart-1));
      boolean s2 = xFinish<m_searchData.length();
      boolean b2 = s2 && !Utils.isSeparator(m_searchData.charAt(
      xFinish));
      if (b1 || b2)// Not a whole word
      {
     /* if (m_searchUp && s1)// Can continue up
      {
      pos = xStart;
      continue;
      } */
      if (/*!m_searchUp*/ true && s2)// Can continue down
      {
      pos = xFinish+1;
      continue;
      }
// Found, but not a whole word, and we cannot continue
      if (showWarnings)
      warning("Text not found");
      return 0;
      }
      }
      break;
      }
      if (/*!m_searchUp*/ true) {
      xStart += m_searchIndex;
      xFinish += m_searchIndex;
      }
      if (doReplace) {
    //    fOwner.setSelection(xStart, xFinish, m_searchUp);

        /* if (m_searchUp){*/
           monitor.setSelectionStart(xStart);
           monitor.setSelectionEnd(xFinish); //MORE HERE ON ORDER

      /*   }
         else{
           monitor.setSelectionStart(xStart); //MORE HERE ON ORDER
           monitor.setSelectionEnd(xFinish);
         } */


    /* Searches backward or forward
            (up or down)for search string
            Retrieves replacement text
            B1 and b2 determine
            whether the found string
            is in a word boundary
            Does actual replacement */

      monitor.replaceSelection(replacement);
//fOwner.setSelection(xStart, xStart+replacement.length(),m_searchUp);
m_searchIndex = -1;
}
else{
      /*if (m_searchUp){
               monitor.setSelectionEnd(xFinish);
               monitor.setSelectionStart(xStart); //MORE HERE ON ORDER

             }
             else*/{
               monitor.setSelectionStart(xStart); //MORE HERE ON ORDER
               monitor.setSelectionEnd(xFinish);
             }

}




//fOwner.setSelection(xStart, xFinish, m_searchUp);
return 1;
}
protected void warning(String message) {
JOptionPane.showMessageDialog(fOwner,
message, "Help",//HtmlProcessor.APP_NAME,
JOptionPane.INFORMATION_MESSAGE);
}



}

/******************* End of FindDialog Class ************/

/*public*/ class DialogLayout2
        implements LayoutManager
{
        protected static final int COMP_TWO_COL = 0;
        protected static final int COMP_BIG = 1;
        protected static final int COMP_BUTTON = 2;

        protected int m_divider = -1;
        protected int m_hGap = 10;
        protected int m_vGap = 5;
        protected Vector m_v = new Vector();

        public DialogLayout2() {}

        public DialogLayout2(int hGap, int vGap)
        {
                m_hGap = hGap;
                m_vGap = vGap;
        }

        public void addLayoutComponent(String name, Component comp) {}

        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent)
        {
                m_v.removeAllElements();
                int w = 0;
                int h = 0;
                int type = -1;

                for (int k=0 ; k<parent.getComponentCount(); k++)
                {
                        Component comp = parent.getComponent(k);
                        int newType = getLayoutType(comp);
                        if (k == 0)
                                type = newType;

                        if (type != newType)
                        {
                                Dimension d = preferredLayoutSize(m_v, type);
                                w = Math.max(w, d.width);
                                h += d.height + m_vGap;
                                m_v.removeAllElements();
                                type = newType;
                        }

                        m_v.addElement(comp);
                }

                Dimension d = preferredLayoutSize(m_v, type);
                w = Math.max(w, d.width);
                h += d.height + m_vGap;

                h -= m_vGap;

                Insets insets = parent.getInsets();
                return new Dimension(w+insets.left+insets.right,
                        h+insets.top+insets.bottom);
        }

        protected Dimension preferredLayoutSize(Vector v, int type)
        {
                int w = 0;
                int h = 0;
                switch (type)
                {
                case COMP_TWO_COL:
                        int divider = getDivider(v);
                        for (int k=1 ; k<v.size(); k+=2)
                        {
                                Component comp = (Component)v.elementAt(k);
                                Dimension d = comp.getPreferredSize();
                                w = Math.max(w, d.width);
                                h += d.height + m_vGap;
                        }
                        h -= m_vGap;
                        return new Dimension(divider+w, h);

                case COMP_BIG:
                        for (int k=0 ; k<v.size(); k++)
                        {
                                Component comp = (Component)v.elementAt(k);
                                Dimension d = comp.getPreferredSize();
                                w = Math.max(w, d.width);
                                h += d.height + m_vGap;
                        }
                        h -= m_vGap;
                        return new Dimension(w, h);

                case COMP_BUTTON:
                        Dimension d = getMaxDimension(v);
                        w = d.width + m_hGap;
                        h = d.height;
                        return new Dimension(w*v.size()-m_hGap, h);
                }
                throw new IllegalArgumentException("Illegal type "+type);
        }

        public Dimension minimumLayoutSize(Container parent)
        {
                return preferredLayoutSize(parent);
        }

        public void layoutContainer(Container parent)
        {
                m_v.removeAllElements();
                int type = -1;

                Insets insets = parent.getInsets();
                int w = parent.getWidth() - insets.left - insets.right;
                int x = insets.left;
                int y = insets.top;

                for (int k=0 ; k<parent.getComponentCount(); k++)
                {
                        Component comp = parent.getComponent(k);
                        int newType = getLayoutType(comp);
                        if (k == 0)
                                type = newType;

                        if (type != newType)
                        {
                                y = layoutComponents(m_v, type, x, y, w);
                                m_v.removeAllElements();
                                type = newType;
                        }

                        m_v.addElement(comp);
                }

                y = layoutComponents(m_v, type, x, y, w);
                m_v.removeAllElements();
        }

        protected int layoutComponents(Vector v, int type,
                int x, int y, int w)
        {
                switch (type)
                {
                case COMP_TWO_COL:
                        int divider = getDivider(v);
                        for (int k=1 ; k<v.size(); k+=2)
                        {
                                Component comp1 = (Component)v.elementAt(k-1);
                                Component comp2 = (Component)v.elementAt(k);
                                Dimension d = comp2.getPreferredSize();

                                comp1.setBounds(x, y, divider, d.height);
                                comp2.setBounds(x+divider, y, w-divider, d.height);
                                y += d.height + m_vGap;
                        }
                        return y;

                case COMP_BIG:
                        for (int k=0 ; k<v.size(); k++)
                        {
                                Component comp = (Component)v.elementAt(k);
                                Dimension d = comp.getPreferredSize();
                                comp.setBounds(x, y, w, d.height);
                                y += d.height + m_vGap;
                        }
                        return y;

                case COMP_BUTTON:
                        Dimension d = getMaxDimension(v);
                        int ww = d.width*v.size() + m_hGap*(v.size()-1);
                        int xx = x + Math.max(0, (w - ww)/2);
                        for (int k=0 ; k<v.size(); k++)
                        {
                                Component comp = (Component)v.elementAt(k);
                                comp.setBounds(xx, y, d.width, d.height);
                                xx += d.width + m_hGap;
                        }
                        return y + d.height;
                }
                throw new IllegalArgumentException("Illegal type "+type);
        }

        public int getHGap()
        {
                return m_hGap;
        }

        public int getVGap()
        {
                return m_vGap;
        }

        public void setDivider(int divider)
        {
                if (divider > 0)
                        m_divider = divider;
        }

        public int getDivider()
        {
                return m_divider;
        }

        protected int getDivider(Vector v)
        {
                if (m_divider > 0)
                        return m_divider;

                int divider = 0;
                for (int k=0 ; k<v.size(); k+=2)
                {
                        Component comp = (Component)v.elementAt(k);
                        Dimension d = comp.getPreferredSize();
                        divider = Math.max(divider, d.width);
                }
                divider += m_hGap;
                return divider;
        }

        protected Dimension getMaxDimension(Vector v)
        {
                int w = 0;
                int h = 0;
                for (int k=0 ; k<v.size(); k++)
                {
                        Component comp = (Component)v.elementAt(k);
                        Dimension d = comp.getPreferredSize();
                        w = Math.max(w, d.width);
                        h = Math.max(h, d.height);
                }
                return new Dimension(w, h);
        }

        protected int getLayoutType(Component comp)
        {
                if (comp instanceof AbstractButton)
                        return COMP_BUTTON;
                else if (comp instanceof JPanel ||
                        comp instanceof JScrollPane)
                        return COMP_BIG;
                else
                        return COMP_TWO_COL;
        }

        public String toString()
        {
                return getClass().getName() + "[hgap=" + m_hGap + ",vgap="
                        + m_vGap + ",divider=" + m_divider + "]";
        }
}

      class Utils
 {
// Unchanged code from example 20.6
 public static final char[] WORD_SEPARATORS = {' ', '\t', '\n',
     '\r', '\f', '.', ',', ':', '-', '(', ')', '[', ']', '{',
     '}', '<', '>', '/', '|', '\\', '\'', '\"'};


public static boolean isSeparator(char ch) {
for (int k=0; k<WORD_SEPARATORS.length; k++)
if (ch == WORD_SEPARATORS[k])
return true;
return false;
}
}

