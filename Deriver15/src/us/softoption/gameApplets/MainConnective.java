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

package us.softoption.gameApplets;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Calendar;

import javax.swing.JApplet;
import javax.swing.JLabel;

import us.softoption.games.TMainConnective;
import us.softoption.infrastructure.TConstants;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.TParser;

/* 
 * 
 */

public class MainConnective extends JApplet{
	TParser fParser =new TParser();
	Dimension fPreferredSize=new Dimension(410,210);  // the game is 400x200
	TMainConnective fGame=null;
	
	public void init(){
		Container contentPane=this.getContentPane();
		 Calendar cal= Calendar.getInstance();
		 long time=cal.getTimeInMillis();
		 int year=cal.get(Calendar.YEAR);
		
		if (year>TConstants.APPLET_EXPIRY){
          JLabel label = new JLabel("The code for this applet expired in " +TConstants.APPLET_EXPIRY +" .");
          contentPane.add(label);
		}
		else{
			
			   String parser= getParameter("parser");
			   if (parser!=null&&parser.equals("bergmann"))
				   fParser =new TBergmannParser();
			   else if (parser!=null&&parser.equals("copi"))
				   fParser =new TCopiParser();
			   else if (parser!=null&&parser.equals("default"))
				   fParser =new TDefaultParser();
			   else if (parser!=null&&parser.equals("gentzen"))
				   fParser =new TParser();
			   else if (parser!=null&&parser.equals("hausman"))
				   fParser =new THausmanParser();
			   else if (parser!=null&&parser.equals("herrick"))
				   fParser =new THerrickParser();

			   
			   if (fGame!=null){
				   contentPane.remove(fGame); // when they re-initialize from javascript
			   }
			   
			   fGame =new TMainConnective(this,fParser);
			   
	/*		   contentPane.setLayout(new GridBagLayout());

			   contentPane.add(fGame,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				       ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
			*/   
			   fGame.setPreferredSize(new Dimension(400, 200));
			   fGame.setMaximumSize(new Dimension(400, 200));
			   fGame.setMinimumSize(new Dimension(400, 200));
			   
			   contentPane.setLayout(new BorderLayout());
			   //contentPane.setBackground(Color.lightGray);
			   
			   contentPane.add(fGame,java.awt.BorderLayout.EAST);
			   
			   fGame.run();
			   
			   this.setVisible(true);
			   this.setPreferredSize(fPreferredSize);
			
		}
	
		
	}
	
public void paint(Graphics g) {   // can see background properly in new Firefox

    	
    	super.paint(g);
 
    	g.drawRect(0, 0, 
     		   getSize().width - 1,
     		   getSize().height - 1);  	
    	

        }	
	
}
