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

import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.html.HTMLEditorKit;

import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

/*Put this in a frame, setting the fContainer field, then use some code for the fram like

 setSize(500,200);
 setLocation((TDeriverApplication.fScreenSize.width-500)/2, (TDeriverApplication.fScreenSize.height-200)/2);  //frame does this
 setResizable(false);

*/

/*You need to use a scroll pane for the tables, for it is that which handles the header (which is
 kind of separate from the table)*/

/*
 * I have had trouble with threads. Be sure in Swing to use event dispatch thread to
 * update components
 */

public class TModalTruthTable extends JPanel{

  TParser fParser;//=new TParser();
  TFormula fRandom;
  int fCorrect=0;
  int fTotal=0;
  int fMaxAttempts=-1;
  
  JTextPane fTextPane= new JTextPane();


  JPanel fWorldsPanel= new JPanel(new GridBagLayout());
  JTable [] fWorlds;
  int fNumWorlds=3;
  String fWorldsIndices = "klmnopqrst";
  TFormula [] fOtherWorlds;
  
  TruthTableModel [] fTableModels=null;
  
  JPanel fAccessPanel= new JPanel(new GridBagLayout());
  boolean[][] fAccess= {{true,true,true},{true,true,true},{true,true,true}};   //needs to be in synch with NumWorlds
  
  
  ListSelectionModel fCSM=null;

  JButton submitButton = new JButton();

  JLabel feedback =new JLabel("You have " +fCorrect+" right out of " +fTotal+".");
  BorderLayout borderLayout2 = new BorderLayout();

  JScrollPane jsp = new JScrollPane(fWorldsPanel); 
 
  //JPanel jPanel2 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  long fElapsed=0;
  long fMaxTime=-1;
  TimeIncrementer fTimeIncrementer = new TimeIncrementer();

  Container fContainer;
  Dimension fPreferredSize=new Dimension(500,200);

  public TModalTruthTable(Container itsContainer, TParser itsParser){

  fContainer=itsContainer;
  fParser=itsParser;

    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  private void jbInit() throws Exception {

    setSize(fPreferredSize);
    setLayout(gridBagLayout1);

    fTextPane.setEditable(false);
    HTMLEditorKit anEditorKit = new HTMLEditorKit();
    fTextPane.setEditorKit(anEditorKit);
    fTextPane.setText("<html>You are here completing a truth table line. But a proposition" +
    		" can have different truth values in different worlds." +
    		"This is a <em>system</em> of worlds, showing the different truth values for the" +
    		" atomic propositions in the same" +
    		" formula. For a 'necessary' " + chModalNecessary
    		+ " (sub)formula to be true it has to be true in <em>every accessible</em> world in the system. "
    		+ "For a 'possible' "+ chModalPossible+ " (sub)formula to be true it has to be true in <em>at least one accessible world</em> in the system. "
    		+		"Click on the 2nd row <em>connectives</em>, to toggle them T F." +
    				" Fill out the entire truth table row for each world, then submit." +
    				"<p>(The clock stops 20 seconds while corrections are displayed.)</p> " +
    				"</html>");

/*		fJournalPane.setEditorKit(fEditorKit);
		fJournalPane.setDragEnabled(true);
		fJournalPane.setEditable(true);
		fJournalPane.setPreferredSize(fJournalPreferredSize); */
    fTextPane.setPreferredSize(new Dimension(460,120));

    submitButton.setText("Submit");
    submitButton.addActionListener(new TModalTruthTable_submitButton_actionAdapter(this));


add(  fTextPane,//jLabel1,
       new GridBagConstraints(0, 0, 1, 1, 1.0, 0.1
     , GridBagConstraints.WEST, GridBagConstraints.NONE,
     new Insets(0, 10, 0, 0), 30, 0));


initAccessPanel();

add(fAccessPanel,
        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.1
, GridBagConstraints.WEST, GridBagConstraints.NONE,
new Insets(0, 0, 0, 0), 100, 0)); 




add(submitButton,
                           new GridBagConstraints(0, 3, 1, 1, 0.2, 0.1
                                     , GridBagConstraints.CENTER,
                                     GridBagConstraints.NONE,
                                     new Insets(0, 0, 0, 0), 100, 0));
 add(feedback,
                          new GridBagConstraints(0, 4, 1, 1, 0.0, 0.1
     , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 100, 0));


 
  }
  
  /********************* Access *************************************/
  
  
  void initAccessPanel(){
	  fAccessPanel.setPreferredSize(new Dimension(460,50));
	  
	  JRadioButton button;
	  
	  
	  for (int i=0;i<fNumWorlds;i++){
		  for (int j=0;j<fNumWorlds;j++){
	  
	  button=new JRadioButton("<"+fWorldsIndices.substring(i,i+1)+
			                  ","+   fWorldsIndices.substring(j,j+1)
			                  +">",true);
	  
	  int [] indices = {i,j};
	  
	  button.addActionListener(new TModalTruthTable_accessButton_actionAdapter(this,indices));
	  
	  fAccessPanel.add(new JLabel("Access: "),
		         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.1
		        		 , GridBagConstraints.WEST, GridBagConstraints.NONE,
		        		 new Insets(0, 0, 0, 5), 0, 0));
	  
	  int x=(i*3+j)%5;
	  int y= ((i*3+j)<5?1:2);
	  
	  fAccessPanel.add(button,
		         new GridBagConstraints(x, y, 1, 1, 0.0, 0.1
		        		 , GridBagConstraints.WEST, GridBagConstraints.NONE,
		        		 new Insets(0, 0, 0, 0), 0, 0)); 
		  }
	  }
  }
  
  
 void updateModelsAccessibleWorlds(){
     TFormula [] accessibleWorlds;
     
     for (int i=0;i<fTableModels.length;i++){
    	 accessibleWorlds=accessibleWorlds(fAccess[i],fOtherWorlds);
    	 fTableModels[i].setAccessibleWorlds(accessibleWorlds); 	 
     }
 }
  
 void accessButton_actionPerformed(ActionEvent e,int []indices){
	 
JRadioButton button= (JRadioButton)e.getSource();

if (!fAccess[indices[0]][indices[1]]==button.isSelected()){

 fAccess[indices[0]][indices[1]]=button.isSelected();     //synch
 updateModelsAccessibleWorlds();
 
 
}


	 
 }
 
 
 /********************* End of Access *************************************/

  void ask(){

    if (( (fMaxAttempts != -1) &&
       (fTotal >= fMaxAttempts))
     ||
     ( (fMaxTime != -1) &&
       (fElapsed >= fMaxTime))
     )
        {      // we're stopping

          submitButton.setEnabled(false);
          
        for (int i=0;i<fTableModels.length;i++){
        	if (fTableModels[i]!=null)
                fTableModels[i].setTTTogglingEnabled(false);
            return;
        	
        }

       }

    fTimeIncrementer.start();
      submitButton.setEnabled(true);

   remove(jsp);

   //   jPanel2.remove(jsp);                 //not using at the moment

      int maxConnectives=5;

       fRandom=us.softoption.games.TRandomFormula.randomModalProp(maxConnectives,false);

       while (fRandom.numConnectives()<3)
         fRandom=us.softoption.games.TRandomFormula.randomModalProp(maxConnectives,false);    //these need to be of similar difficulty
          
       
      // say between 3 and 5 connectives
       
       //String random=fParser.writeFormulaToString(fRandom);
       
/*/debug      
    String testStr=//chModalNecessary+"F";
    	           //  chModalPossible+"F";
    	          //  chModalPossible+"~F"+chEquiv+"T";
                  //  chModalNecessary+"~F"+chEquiv+"T";
    	
   // 	chModalNecessary+"((F"+chEquiv+"T)"+chEquiv+"~F)"; //bad
    
  /*chModalPossible+"~((T"+chOr+"T)"+chImplic+"(T"+chEquiv+"T))"; 
    
  //  chModalNecessary+"(F"+chEquiv+"T)"; //good
                    
               //     ?((F?F)??F)
    
    fRandom= new TFormula();  
	
if	(fParser.wffCheck(fRandom,new ArrayList(),new StringReader(testStr)))
	
	;

*/
       
//debug  
       
 //   System.out.print(fParser.writeFormulaToString(fRandom) + "\n");
       
         
         fTableModels = new TruthTableModel[fNumWorlds];
   	     fOtherWorlds= new TFormula[fNumWorlds];
    	 TFormula variant=fRandom;
    	 
         for (int i=0;i<fTableModels.length;i++){
        	 fTableModels[i]= new  TruthTableModel(variant,fParser,TruthTableModel.MODAL) ;
        	 fOtherWorlds[i]= variant ;
        	 variant=variant(fRandom);
         }
/*         
         TFormula [] accessibleWorlds;
         
         for (int i=0;i<fTableModels.length;i++){
        	 accessibleWorlds=accessibleWorlds(fAccess[i],fOtherWorlds);
        	 fTableModels[i].setAccessibleWorlds(accessibleWorlds); 	 
         }
   */      
         updateModelsAccessibleWorlds();

         addWorlds();       

   Dimension tableSize=initializeTable(fTableModels[0],fWorlds[0]);

   //   jPanel2.remove(jsp);
     
      
      jsp=new JScrollPane(fWorldsPanel);
        
    /*************/     


    jsp.setSize(tableSize);  // we' like to center it, but cannot seem to get this working
    jsp.setMaximumSize(tableSize);

   jsp.setBorder(BorderFactory.createLineBorder(Color.black));

 add(jsp, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.5
              , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 35, 0));


   // setVisible(true);     //  need to have this here else does not update properly

 /*it draws in the wrong place-- java bug? */

  setVisible(false);  //new Aug 16 06
  setVisible(true);
  }
  
 TFormula [] accessibleWorlds(boolean[]flags,TFormula[]otherWorlds){
	int count=0;
	TFormula[] outWorlds = new TFormula[0];
	
	
if (flags!=null&&otherWorlds!=null&&flags.length==otherWorlds.length){	
	
for (int i=0;i<flags.length;i++){
	if (flags[i])
		count+=1;
}

 outWorlds = new TFormula[count];

int outIndex=0;

for (int i=0;i<flags.length;i++){
	if (flags[i]){
		outWorlds[outIndex]=otherWorlds[i];
		outIndex+=1;
	}
}
}
	 
return
   outWorlds;
 }
  
  
 void addWorlds(){
	   
	  fNumWorlds=3;
	  fWorlds= new JTable[fNumWorlds];

	  
      fWorldsPanel= new JPanel(new GridBagLayout());
      
      JScrollPane aScrollPane;
      JTable aWorld;
      
      int layoutIndex=0;
      
      
      for (int i=0;i<fWorlds.length;i++){
    	  
    	  aWorld= new JTable(fTableModels[i]);
    	  aWorld.setBackground(new Color(200, 200, 200));  //no point to this because we haven't put it in the panel
    	  aWorld.setMaximumSize(new Dimension(200, 48));
    	  aWorld.setPreferredSize(new Dimension(200, 48));
    	  
    	  fWorlds[i]=aWorld;
    	  
    	  
    	  
          fWorldsPanel.add(new JLabel("World "+ fWorldsIndices.substring(i, i+1)),    // a table has to be in a scroller to show its header
        		  
        		  
        		  new GridBagConstraints(0, layoutIndex, 1, 1, 0.2, 0.1
                  , GridBagConstraints.WEST,
                  GridBagConstraints.NONE,
                  new Insets(10, 10, 0, 0), 0, 0) );
          
          layoutIndex++;
    	  
    	  Dimension tableSize=initializeTable(fTableModels[i],fWorlds[i]);
    	  aScrollPane= new JScrollPane(fWorlds[i]);
    	  Dimension paddedSize = new Dimension(tableSize.width+40,tableSize.height+20);
    	  aScrollPane.setPreferredSize(paddedSize);
    	
    	  
    	  
      fWorldsPanel.add(aScrollPane,    // a table has to be in a scroller to show its header
    		  
    		  
    		  new GridBagConstraints(0, layoutIndex, 1, 1, 0.2, 0.1
              , GridBagConstraints.WEST,
              GridBagConstraints.NONE,
              new Insets(0, 10, 0, 0), 0, 0) );
      
      layoutIndex++;
      
      }
      
      fWorldsPanel.setPreferredSize(new Dimension(400,200));
      
	 
 }
  


Dimension initializeTable(TruthTableModel aModel, JTable aTable){
	aTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	aTable.setIntercellSpacing(new Dimension(0,0));
    //table.setRowHeight(16);

	aTable.setOpaque(false);

	aTable.clearSelection();


    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)(aTable.getCellRenderer(0,0));

    renderer.setHorizontalAlignment(0);  /*bit of a trick, which will probably break,
    we want the table text centered, and really it only uses one renderer ie 00*/

 Font theFont=renderer.getFont();

  renderer.setFont(new Font(theFont.getName(),theFont.getStyle(),12));  //Windows is doing something funny with size, so we'll make it 12

  FontMetrics fm=getFontMetrics(theFont);  //not used yet but planning to calculate column widths

 JTableHeader header=aTable.getTableHeader();

   header.setReorderingAllowed(false);  // we don't want them dragging the columns around
  //  table.getTableHeader().setResizingAllowed(false);

  //header.set

    //TableColumnModel columnModel=firstWorld.getColumnModel();
   
   TableColumnModel columnModel=aTable.getColumnModel();

    TableColumn column;

    int num=columnModel.getColumnCount();

    int cellWidth=25; //default is 75, careful, too narrow (eg12,14,16,20,22,24) won't work on Windows 26 workds

    for (int i=0;i<num;i++){

      column = columnModel.getColumn(i);

      // char symbol=randomStr.charAt(i);

     /*  switch (symbol){
    case '(': ;case ')' : width=8; break;
    case chNeg  : width=10; break;
  }*/

     //column.setMinWidth(width);
     column.setPreferredWidth(cellWidth);                      // do need this to stop spreading
   };

   fCSM=columnModel.getSelectionModel();  // can use a local fCSM here
   fCSM.clearSelection();

   fCSM.addListSelectionListener(
      new OurListener(aModel,aTable));
   
   aTable.setPreferredSize( new Dimension(num*cellWidth,48));// Feb09, this fixes clipping off the right bracket

   return
       new Dimension(num*cellWidth,48);  

}

TFormula variant(TFormula original){
	TFormula outFormula=original.copyFormula();
	double random;
	
	switch (outFormula.fKind){

	         case TFormula.predicator: 
	        	 random=Math.random();
	        	 
	        	if (random<0.5){
				   if (outFormula.fInfo.equals("T"))
					   outFormula.fInfo="F";
				   else
					   outFormula.fInfo="T";
	        	}
	           break;

	          case TFormula.unary:
	        	  outFormula.fRLink = variant(outFormula.fRLink);
	            break;

	          case TFormula.binary:
	        	  outFormula.fLLink = variant(outFormula.fLLink);
	        	  outFormula.fRLink = variant(outFormula.fRLink);

	            break;

	        default: ;
	      }
	
   return
   	outFormula;			
}


 public int getTotal(){
  return
      fTotal;
}

public int getCorrect(){
  return
      fCorrect;
 }

  void respond(boolean correct){

     fTimeIncrementer.stop();                 //to let them look at the right answer, if need be, ask() restarts it
     submitButton.setEnabled(false);          // ask resets it
    
     
     
   //  fTableModel.setTTTogglingEnabled(false);  // no need to reset this true, becuase the model changes
     
     for (int i=0;i<fTableModels.length;i++){
    	 fTableModels[i].setTTTogglingEnabled(false);   	
     }
     
     

    fTotal += 1;
    if (correct)
      fCorrect += 1;

 //   feedback.setText("You have " + fCorrect + " right out of " + fTotal +
  // " in "+ fElapsed  + " secs.");

    if (fMaxAttempts==-1)
        feedback.setText("You have " + fCorrect + " right out of " + fTotal +
                       " in "+ fElapsed  + " secs.");
     else
        // feedback.setText("You have " + fCorrect + " right out of " + fTotal +
        //         " in "+ fElapsed  + " secs. [Attempt " +fMaxAttempts+"]");

         feedback.setText("You have " + fCorrect + " right out of " + fTotal +
          " in "+ fElapsed  + " secs. [Attempt " +fMaxAttempts
                +", times out in: "+fMaxTime+" secs.]");

    if(!correct){
      Toolkit.getDefaultToolkit().beep();
      jsp.setBorder(BorderFactory.createLineBorder(Color.red));  // ""
      //jsp.repaint();
    //  fTableModel.showTTAnswer(); // fTableModel.showTTAnswer();
      
      for (int i=0;i<fTableModels.length;i++){
    	  fTableModels[i].showTTAnswer();   	
      }

      //setVisible(true);
      //Thread.yield();   //not sure about this Aug 16 06
      //this.repaint();

      }
}



  public void run(){

    fElapsed=0;
//    fTimeIncrementer.start();

    ask();

  //  setVisible(true);

  // MainLoop main =new MainLoop(); Mon
  // main.start();
  }


  public void setMaxAttempts(int max){
    if (max>0)
      fMaxAttempts=max;
  }

  public void setMaxTime(long max){
    if (max>0)
      fMaxTime=max;
}


  public void submitButton_actionPerformed(ActionEvent actionEvent) {

    boolean correct=true;
    
    for (int i=0;i<fTableModels.length&&correct;i++){  //have to get all right
    	correct=fTableModels[i].tTAnswerTrue();  
    }

    respond(correct);

    if (correct)                               //go straight on
      ask();
    else{

    	Thread worker = new Thread(){
    		public void run(){
    	          try {
    	              Thread.sleep(15000);               //give them time to see the answer
    	          }
    	          catch (Exception ex) {}
    		//report
    		SwingUtilities.invokeLater(new Runnable() {
    	        public void run() {
    	              ask();  // from event dispatch thread
    	              Toolkit.getDefaultToolkit().beep();
    	        }
    	      });
    		}
    	};
    	worker.start();
    }

//    	 we'll just use the applet thread Aug 06

   /*    SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          try {
            {
              Thread.yield();
              Thread.sleep(15000);               //give them time to see the answer
              ask();
              Toolkit.getDefaultToolkit().beep();
            }
          }
          catch (Exception ex) {}
        }
      }); */

    /*  try{     Thread.sleep(15000); } catch (InterruptedException ex){};
      ask();
      Toolkit.getDefaultToolkit().beep(); */

  }


  /////////// Inner Classes ///////////////////////////////



  class OurListener implements ListSelectionListener{
	  TruthTableModel fModel;
	  JTable fWorld;
	  
	  OurListener (TruthTableModel aTableModel,
	  JTable aWorld){
		  fModel= aTableModel;
		  fWorld= aWorld;	  
	  }
	  
	  
	  
    public void valueChanged(ListSelectionEvent e){

     //really here we are not interested in selections, we are interested in mouse clicks

      if (e.getValueIsAdjusting())
        return; // pass if it isn't settled

      int index = 0; // e.getLastIndex(); //we have a single selection model, and nothing is selected to start with


      ListSelectionModel lsm =
            (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty()) {

        } else {
            index = lsm.getMinSelectionIndex();
            fModel.tTToggle(0,index);        //only toggle selected
        }
        fWorld.clearSelection(); //unfortunately fires listchange event
    }
  }



  class TimeIncrementer implements ActionListener{
            javax.swing.Timer t =new javax.swing.Timer(1000,this);

  public void start(){
     t.start();
  }

  public void stop(){
     t.stop();
  }

  public void actionPerformed (ActionEvent ae){
     fElapsed+=1;  //every second
  }
  }


  
}

/////////////////////// JBuilder Classes ///////////////////

class TModalTruthTable_submitButton_actionAdapter
    implements ActionListener {
  private TModalTruthTable adaptee;
  TModalTruthTable_submitButton_actionAdapter(TModalTruthTable adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.submitButton_actionPerformed(actionEvent);
  }
}

class TModalTruthTable_accessButton_actionAdapter
implements ActionListener {
private TModalTruthTable adaptee;

int [] fIndices=null;

TModalTruthTable_accessButton_actionAdapter(TModalTruthTable adaptee,int [] indices) {
this.adaptee = adaptee;
fIndices=indices;
}

public void actionPerformed(ActionEvent actionEvent) {
adaptee.accessButton_actionPerformed(actionEvent,fIndices);
}
}




