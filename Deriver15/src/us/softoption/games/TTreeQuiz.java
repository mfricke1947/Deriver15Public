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


import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.softoption.editor.TDeriverDocument;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TParser;



public class TTreeQuiz extends JFrame{

        private static final long serialVersionUID = 1L;
  static public int fNumOpen=0;


  TParser fParser=null;

  TDeriverDocument fDeriverDocument= null;

  boolean fPredicateLogic=true;

  /*********** Panels ******************/

  TRandomTreePanel fCloseTree;
  boolean fCloseRunning=false;
  int fCloseAttempts=5;
  int fCloseTime=600;

  TRandomTreePanel fSatisTree;
  boolean fSatisRunning=false;
  int fSatisAttempts=5;
  int fSatisTime=600;

  TRandomTreePanel fValidTree;
  boolean fValidRunning=false;
  int fValidAttempts=5;
  int fValidTime=600;

/****************** End of Panels ****************/

  JTabbedPane fTabs= new JTabbedPane();

  JLabel fWelcome = new JLabel("<html>Work through the Tabs to Finish.<br>"
                  + "Tree1: <br>"
                  + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over.");

  JLabel fFeedback=new JLabel("");
  JTextArea fCode=new JTextArea("");
  JScrollPane codeScroller;
  JPanel fFinishPanel=new JPanel(new GridBagLayout());

  String fConfCode="";

  NameEntry fNameEntry=new NameEntry();

  Dimension fPreferredSize= new Dimension (540,540);




 public TTreeQuiz(TParser aParser, TDeriverDocument aDocument){

    fParser=aParser;
    fDeriverDocument=aDocument;

    createGUI(/*contentPane*/);

  }















        private void createGUI(/*Container contentPane*/){



           /*********** panels **************/

          fCloseTree =new TRandomTreePanel(fDeriverDocument,fParser,
                          fPredicateLogic,TRandomTreePanel.CLOSETYPE);
          fCloseTree.setMaxAttempts(fCloseAttempts);
          fCloseTree.setMaxTime(fCloseTime);
          fCloseTree.startTree("");

           String closeMessage="";
           if (fCloseAttempts>0)
             closeMessage="Close: attempt "+fCloseAttempts
             +". Times out in "+fCloseTime+" seconds.";


                  fSatisTree =new TRandomTreePanel(fDeriverDocument,fParser,
                                  fPredicateLogic,TRandomTreePanel.SATISFYTYPE);
                  fSatisTree.setMaxAttempts(fSatisAttempts);
                  fSatisTree.setMaxTime(fSatisTime);
                  fSatisTree.startTree("");
                   String satisMessage="";
                   if (fSatisAttempts>0)
                     satisMessage="Satisfiable: attempt "+fSatisAttempts
                     +". Times out in "+fSatisTime+" seconds.";

                          fValidTree =new TRandomTreePanel(fDeriverDocument,fParser,
                                          fPredicateLogic,TRandomTreePanel.VALIDTYPE);
                          fValidTree.setMaxAttempts(fValidAttempts);
                          fValidTree.setMaxTime(fValidTime);
                          fValidTree.startTree("");
                           String validMessage="";
                           if (fSatisAttempts>0)
                             validMessage="Validity: attempt "+fValidAttempts
                             +". Times out in "+fValidTime+" seconds.";




        setSize(fPreferredSize);



        fFinishPanel.add(fFeedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

           JPanel intro=new JPanel(new BorderLayout());
          // fNameEntry.fName.setText(TDeriverApplication.getUser());

           intro.add(fNameEntry,BorderLayout.NORTH);


          fWelcome = new JLabel("<html>Work through the Tabs to Finish.<br>"
                          + "<br>"
                          + closeMessage +"<br>"
                          + satisMessage +"<br>"
                          + validMessage +"<br>"
                          +"<br>"
                          + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over.");




           intro.add (fWelcome,BorderLayout.CENTER);

           //createFinishPanel();

           initializeFinishPanel();

        //  fTabs.add("Intro",intro);

          fTabs.add("Intro",intro);

           fTabs.add("Close",fCloseTree);
           fTabs.add("Satis",fSatisTree);
           fTabs.add("Valid",fValidTree);

          fTabs.add("Finish",fFinishPanel);  //scroller in case it gets too big


        fTabs.addChangeListener(tabsChangeListener());

 /*contentPane.*/add(fTabs);

 this.setVisible(false);
 this.setVisible(true);
}

        /*
TRandomTreePanel	supplyRandomPanel(TDeriverDocument aDocument,TParser aParser,int type){

                return

                        new TRandomTreePanel(aDocument,     //try another one
                aParser,
                type);

        }  */


        private ChangeListener tabsChangeListener(){
        return
        new ChangeListener(){
                public void stateChanged(ChangeEvent e){

                        fCloseTree.stopClock();
                        fSatisTree.stopClock();
                        fValidTree.stopClock();

                  Component selected = fTabs.getSelectedComponent();

              if (selected == fCloseTree) {
                  if (!fCloseRunning) {
                                  fCloseTree.run();
                                  fCloseRunning = true;
                  }
                  else
                          fCloseTree.startClock();

              }

              if (selected == fSatisTree) {
                  if (!fSatisRunning) {
                                  fSatisTree.run();
                                  fSatisRunning = true;
                  }
                  else
                          fSatisTree.startClock();
              }

              if (selected == fValidTree) {
                  if (!fValidRunning) {
                                  fValidTree.run();
                                  fValidRunning = true;
                  }
                  else
                          fValidTree.startClock();
              }

                            if (selected == fFinishPanel) {
                                updateFinishPanel();

                            }
                          }
                        };
                      }


        private void initializeFinishPanel(){
    fFinishPanel=new JPanel(new GridBagLayout()/*new BorderLayout()*/);
    fCode=new JTextArea("");

   // codeScroller =new JScrollPane(fCode);

   // fFinishPanel.add(codeScroller, BorderLayout.CENTER);

    fCode.setEditable(true);
    fCode.setLineWrap(true);
    fCode.setWrapStyleWord(true);
    fCode.setFont(new Font("Sans-Serif", Font.PLAIN, 12));

   // code.setPreferredSize(new Dimension (540,140));

   // fFinishPanel.add(code, BorderLayout.CENTER);

                JScrollPane aScroller=new JScrollPane(fCode);
        aScroller.setPreferredSize(new Dimension (480,160));
        aScroller.setMinimumSize(new Dimension (480,160));



          fFinishPanel.add(aScroller,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));


 //   fFinishPanel.add(new JLabel("Be sure to submit the Confirmation Code."   //adding every update?
 //                                      +""), BorderLayout.SOUTH);

        fFinishPanel.add(new JLabel(
    "Be sure to submit the Confirmation Code."),
    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

  }



public String treeQuizConfirmation(String name,String compositeStr){
        DateFormat shortTime=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
    String time=shortTime.format(new Date());

    return
    "["
        + TUtilities.generalEncode(
                name+ ", "
                + time+ ", "
                + compositeStr)
                                       + "]";
}


private void updateFinishPanel(){

        //fFinishPanel.remove(codeScroller);
        fFinishPanel.remove(fFeedback);

    String name = fNameEntry.fName.getText();

    if (name == null || name.equals("")) {
      fFeedback = new JLabel(
          "You need to enter a name on the Intro Page.");
   //   fFinishPanel.add(feedback, BorderLayout.NORTH);
    }
   else {

            int correct = (fCloseTree.getCorrect()
                  + fSatisTree.getCorrect()
                  + fValidTree.getCorrect()
                /*  + fInvalid.getCorrect() */
       );
   int total = (fCloseTree.getTotal()
                + fSatisTree.getTotal()
                + fValidTree.getCorrect()
              /*  + fCons.getTotal()
                + fInvalid.getTotal() */
       );

              fFeedback = new JLabel(fNameEntry.fName.getText() +
                  ", you have "
                  + correct
                  + " right out of "
                  + total
                  + " attempted.");


//	fFinishPanel.add(feedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
//		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));


           DateFormat shortTime=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
              String time=shortTime.format(new Date());


              fConfCode= "["
                  + TUtilities.urlEncode(TUtilities.xOrEncrypt(
                          name+ ", "
                          + time+ ", "
                          + correct
                          + " of "
                          + total))
                                                 + "]";

              fCode.setText(//strCR
                                            // +
                  "Here is the number you got correct: " + correct

                                             + strCR+ strCR
                                             +
                  "Here is your Confirmation Code:"
                  + strCR+ strCR
                  + fConfCode
                                             + strCR +
                                             strCR
                                             + "To submit: (copy then) paste the number you got correct, and the Confirmation"
                                             + strCR
                                             + "Code (into the Quiz Attempt).   "
                                             + strCR +
                                             strCR
                                          //   + " You should copy and paste the Confirmation Code, as it may contain unusual characters."
                  );


   }

                fFinishPanel.add(fFeedback,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                               ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

                fFinishPanel.setVisible(false);
                fFinishPanel.setVisible(true);



}




public String getConfCode(){
        return
        fConfCode;
}



class NameEntry extends JPanel{
  /**
         *
         */
        private static final long serialVersionUID = 1L;
public JTextField fName= new JTextField(20);
  public NameEntry(){
    super (new BorderLayout());

    JLabel label = new JLabel("Name: ");

    add(label,BorderLayout.WEST);
    add(fName,BorderLayout.CENTER);

  }
}

}


