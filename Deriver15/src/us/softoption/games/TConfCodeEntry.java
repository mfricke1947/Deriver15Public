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

package us.softoption.games;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TConfCodeEntry extends JPanel{
  public JTextArea fCode= new JTextArea();
  public JLabel fLabel = new JLabel("Confirmation Code: ");

  public TConfCodeEntry(){
    super (new BorderLayout());

    setPreferredSize(new Dimension(200,32));

    add(fLabel,BorderLayout.WEST);
    add(fCode,BorderLayout.CENTER);

    fCode.setText("");


    fCode.setLineWrap(true);
 fCode.setWrapStyleWord(true);
 fCode.setFont(new Font("Sans-Serif",Font.PLAIN,12));

  }

  public TConfCodeEntry(String label){
    this();

    remove(fLabel);
    fLabel=new JLabel(label);
    add(fLabel,BorderLayout.WEST);
  }

public String getCode(){
  return
      fCode.getText();
}

public void setCode(String aString){

      fCode.setText(aString);
}

}
