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


// SplashScreen.java
// A simple application to show a title screen in the center of the screen
// for the amount of time given in the constructor.  This class includes
// a sample main() method to test the splash screen, but it's meant for use
// with other applications.
//

import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class TSplashScreen extends JWindow {
  private int duration;

  JLabel expire;
  JLabel title = new JLabel
      (strCR +"Deriver.", JLabel.CENTER);
  JLabel copyrt = new JLabel
      ("<html>Copyright (c) 1986-2020 Martin Frické and SoftOption®.</html>", JLabel.CENTER);
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public TSplashScreen() {

    // Set the window's bounds, centering the window
    int width = 450;
    int height =115;
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screen.width-width)/2;
    int y = (screen.height-height)/2;
    setBounds(x,y,width,height);

    JPanel content = (JPanel)getContentPane();
    content.setBackground(Color.white);
    //  Color oraRed = new Color(Color.    //new Color(156, 20, 20,  255);
    content.setBorder(BorderFactory.createLineBorder(Color.CYAN/*oraRed*/, 4));



    title.setFont(new Font("Sans-Serif", Font.BOLD, 14));
    copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));

    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public TSplashScreen(int d) {
    this();
    duration = d;
    }

    private void jbInit() throws Exception {


    this.getContentPane().setLayout(gridBagLayout1);

    this.getContentPane().add(title,
                              new GridBagConstraints(0, 0, 1, 1, 1.0, 0.6
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 326, 0));
    this.getContentPane().add(copyrt,
                              new GridBagConstraints(0, 2, 1, 1, 1.0, 0.2
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets( 0, 0, 0, 0), 84, 0));
  }

  // A simple little method to show a title screen in the center
  // of the screen for the amount of time given in the constructor
  public void showSplashExit(long expirySecs) {
	  
	/*This  was changed in 2015 so it never goes out of date and exits*/
	  

    Calendar cal= Calendar.getInstance();  // 1 day in secs = 86,400,000
    long time=cal.getTimeInMillis();

    long days= ((expirySecs*1000)-time)/86400000;

    if (days<0){
      expire = new JLabel("[Expired, please download from Internet.]", JLabel.CENTER);
    }
    else{
      if (days>1)
      expire = new JLabel("[Expires in "+ days + " days.]", JLabel.CENTER);
    else
      expire = new JLabel("[Expiring within a day.]", JLabel.CENTER);

    }

    // Build the splash screen

    expire.setFont(new Font("Sans-Serif", Font.PLAIN, 9));
    
    /*2015 no expire notice 

    this.getContentPane().add(expire,
                              new GridBagConstraints(0, 1, 1, 1, 1.0, 0.2
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
*/

    // Display it
    setVisible(true);

    // Wait a little while, maybe while loading resources
    try { Thread.sleep(duration); } catch (Exception e) {}

    setVisible(false);

    /* 2015  no exit on expiry
    if (days<0)
      System.exit(0);
      */
  }



/*  public void showSplashAndExit() {
    showSplash();
    System.exit(0);
  }  */

  public static void main(String[] args) {
    // Throw a nice little title page up on the screen first
    TSplashScreen splash = new TSplashScreen(10000);
    // Normally, we'd call splash.showSplash() and get on with the program.
    // But, since this is only a test...
  //  splash.showSplashAndExit();
  }


}
