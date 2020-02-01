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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Calendar;

import javax.swing.JApplet;
import javax.swing.JLabel;

import us.softoption.games.TSatisfiable;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.TParser;



public class Satisfiable extends JApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	TParser fParser =new TParser();
	Dimension fPreferredSize=new Dimension(500,230);
	
	public void init(){
		Container contentPane=this.getContentPane();
		
		contentPane.setBackground(Color.lightGray);
		
		 Calendar cal= Calendar.getInstance();
		 long time=cal.getTimeInMillis();
		
		if (false/*time>10*/){
          JLabel label = new JLabel("The code for this applet is out of date.");
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
				
			
			   TSatisfiable game =new TSatisfiable(this,fParser);
			   
			   //contentPane.add(game);
			   
			   setContentPane(game);
			   
			   game.run();
			   
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


/*
   JFrame aFrame=new JFrame("Show whether the formula is satisfiable ie make it true");
   TSatisfiable game =new TSatisfiable(aFrame,fDeriverDocument.getParser());

   aFrame.getContentPane().add(game);
     aFrame.setSize(500,230);
      aFrame.setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-230)/2);
      aFrame.setResizable(false);

      aFrame.setVisible(true);   // used to be commented out but the Proguard obfuscator won't let the sub-panel set this
      game.run();
   }
 * */
