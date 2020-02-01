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

/* read Feb 4 04*/

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class PanelFrame{

  public  PanelFrame() {

   JFrame frame = new JFrame();

   TShapePanel shapePanel = null;


  /*     new TShapePanel("Shape Frame", 640, 480,
                                               Color.white);  commented out because
   constructor now has reference to document */

              shapePanel.setVisible(true);




           frame.setContentPane(shapePanel);
           frame.setTitle("First attempt");
           frame.addWindowListener(
             new WindowAdapter() {

                 public void windowClosing(WindowEvent e) {

               /*
                 FileDialog fd = new FileDialog(frame,"Save Picture As",FileDialog.SAVE);
                 fd.setDirectory(System.getProperty("user.dir")); // Set the default directory to the current directory
                 fd.setVisible(true);
                 String fdir = fd.getDirectory();
                 String fname = fd.getFile();
                 if(fname!=null){
                   File f = new File(fdir+File.separator+fname);
                   try {
                     ImageIO.write(canvasImage, "png", f);
                   }
                   catch (IOException ie){
                   }
                 }
                */
                 System.exit(0);
               }
             }
           );


           frame.pack();
     frame.setVisible(true);
  }


  static public void main (String args[]){
    new PanelFrame();
  }
}
