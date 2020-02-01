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
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.softoption.editor.TPreferences;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TParser;


public class TPredGamesQuiz extends JFrame{

  static public int fNumOpen = 0;

  TParser fParser;

  JTabbedPane fTabs = new JTabbedPane();

  TMainConnective fConnective;
  boolean fConnectiveRunning = false;

  TPredTruthTable/*Old*/ fTT;
  boolean fTTRunning = false;

  TPredSatisfiable/*Old*/ fSatis;
  boolean fSatisRunning = false;

  TPredSatisfiable/*Old*/ fSatis2;
  boolean fSatis2Running = false;

  TPredConsistent/*Old*/ fCons;
  boolean fConsRunning = false;

  TPredInvalid/*Old*/ fInvalid;
  boolean fInvalidRunning = false;

  JLabel feedback = new JLabel("");
  JTextArea code = new JTextArea();
  JPanel finishPanel = new JPanel(new BorderLayout());

  NameEntry fNameEntry = new NameEntry();

  public TPredGamesQuiz(TParser itsParser,
                        String title,
                        int connectiveAttempts, int connectiveTime,
                        int tTAttempts, int tTTime,
                        int satisAttempts, int satisTime,
                        int satis2Attempts, int satis2Time,
                        int consAttempts, int consTime,
                        int invalAttempts, int invalTime){



    super(title);

    fNumOpen += 1;

    setSize(600, 560/*350*/);
  //  setLocation( (TDeriverApplication.fScreenSize.width - 600) / 2,
 //               (TDeriverApplication.fScreenSize.height - 560) / 2);
    this.setResizable(false);

    fParser=itsParser;

    fConnective = new TMainConnective(this,fParser);
    fConnective.setPropositional(false); // we'll use pred formulas
    fConnective.setMaxAttempts(connectiveAttempts);
    fConnective.setMaxTime(connectiveTime);

    fTT = new TPredTruthTable/*Old*/(this,fParser);
    fTT.setMaxAttempts(tTAttempts);
    fTT.setMaxTime(tTTime);
    fTT.setLabel2Text("Complete the truth values for the entire formula");

    //fSatis = new TPredSatisfiableOld(this);
    fSatis = new TPredSatisfiable(this,fParser);
    fSatis.setMaxAttempts(satisAttempts);
    fSatis.setMaxTime(satisTime);

    //fSatis2 = new TPredSatisfiableOld(this);
    fSatis2 = new TPredSatisfiable(this,fParser);
    fSatis2.setUseQuantifiers(true);
    fSatis2.setMaxAttempts(satis2Attempts);
    fSatis2.setMaxTime(satis2Time);

    //fCons = new TPredConsistentOld(this);
    fCons = new TPredConsistent(this,fParser);
    fCons.setUseQuantifiers(true);
    fCons.setMaxAttempts(consAttempts);
    fCons.setMaxTime(consTime);

    //fInvalid = new TPredInvalidOld(this);
    fInvalid = new TPredInvalid(this,fParser);
    fInvalid.setUseQuantifiers(true);
    fInvalid.setMaxAttempts(invalAttempts);
    fInvalid.setMaxTime(invalTime);

    finishPanel.add(feedback);

    JPanel intro = new JPanel(new BorderLayout());

    String entry=TPreferences.getUser();

    fNameEntry.fName.setText(entry);
    intro.add(fNameEntry, BorderLayout.NORTH);

    String connectiveMessage = "";
    if (connectiveAttempts != 0)
      connectiveMessage = "Main Connective: attempt " + connectiveAttempts +
          ". Times out in " + connectiveTime + " seconds.";
    String tTMessage = "";
    if (tTAttempts != 0)
      tTMessage = "Truth Table: attempt " + tTAttempts + ". Times out in " +
          tTTime + " seconds.";
    String satisMessage = "";
    if (satisAttempts != 0)
      satisMessage = "Satisfiable: attempt " + satisAttempts + ". Times out in " +
          satisTime + " seconds.";

    String satis2Message = "";
    if (satis2Attempts != 0)
      satis2Message = "Satisfiable 2: attempt " + satis2Attempts +
          ". Times out in " + satis2Time + " seconds.";

    String consMessage = "";
    if (consAttempts != 0)
      consMessage = "Consistent: attempt " + consAttempts + ". Times out in " +
          consTime + " seconds.";
    String invalMessage = "";
    if (invalAttempts != 0)
      invalMessage = "Invalid: attempt " + invalAttempts + ". Times out in " +
          invalTime + " seconds.";

    JTextArea text = new JTextArea(strCR
                                   + "Work through the Tabs to Finish."
                                   + " [When a Tab is opened, its timer starts.]"
                                   + strCR
                                   + strCR
                                   + connectiveMessage
                                   + strCR
                                   + tTMessage
                                   + strCR
                                   + satisMessage
  + strCR
        + satis2Message
        + strCR
        + consMessage
        + strCR
        + invalMessage
        + strCR
        + strCR
        + "When you reach Finish, submit if you are satisfied. Otherwise close and open to start over."
        + strCR
);

  text.setLineWrap(true);
   text.setWrapStyleWord(true);
  text.setFont(new Font("Sans-Serif",Font.PLAIN,12));

  text.setEditable(false);

   intro.add (text,BorderLayout.SOUTH);


  fTabs.add("Intro",intro);
  fTabs.add("Main",fConnective);
  fTabs.add("TT",fTT);
  fTabs.add("Satis",fSatis);
  fTabs.add("Satis2",fSatis2);
  fTabs.add("Cons",fCons);
  fTabs.add("Inval",fInvalid);
  fTabs.add("Finish", finishPanel);




fTabs.addChangeListener(new ChangeListener(){
public void stateChanged(ChangeEvent e){
  int index = fTabs.getSelectedIndex();

  Component selected = fTabs.getSelectedComponent();

  if (selected == fConnective) {
    if (!fConnectiveRunning) {
      fConnective.run();
      fConnectiveRunning = true;
    }
  }
  else {
    if (selected == fTT) {
      if (!fTTRunning) {
        fTT.run();
        fTTRunning = true;
      }
    }
    else {
      if (selected == fSatis) {
        if (!fSatisRunning) {
          fSatis.run();
          fSatisRunning = true;
        }

      }
      else {
            if (selected == fSatis2) {
              if (!fSatis2Running) {
                fSatis2.run();
                fSatis2Running = true;
              }

      }
      else {
        if (selected == fCons) {
          if (!fConsRunning) {
            fCons.run();
            fConsRunning = true;
          }

        }
        else {
          if (selected == fInvalid) {
            if (!fInvalidRunning) {
              fInvalid.run();
              fInvalidRunning = true;
            }

          }
          else {
            if (selected == finishPanel) {
              {
                finishPanel.remove(feedback);
                finishPanel.remove(code);

                int correct = (fConnective.getCorrect()
                               + fTT.getCorrect()
                               + fSatis.getCorrect()
                               + fSatis2.getCorrect()
                               + fCons.getCorrect()
                               + fInvalid.getCorrect()
                    );
                int total = (fConnective.getTotal()
                             + fTT.getTotal()
                             + fSatis.getTotal()
                             + fSatis2.getTotal()
                             + fCons.getTotal()
                             + fInvalid.getTotal()
                    );

                String name = fNameEntry.fName.getText();

                if (name == null || name.equals("")) {
                  feedback = new JLabel(
                      "You need to enter a name on the Intro Page.");

                }
                else {

                  feedback = new JLabel(fNameEntry.fName.getText() +
                                        ", you have "
                                        + correct
                                        + " right out of "
                                        + total
                                        + ".");

                  DateFormat shortTime=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
                  String time=shortTime.format(new Date());


                  JTextArea code = new JTextArea(strCR
                                                 +
                                                 "Here is the number you got correct: " +
                                                 correct

                                                 + strCR
                                                 +
                                                 "Here is your confirmation code:  ["
                                                 +
                                                 TUtilities.urlEncode(
                      TUtilities.xOrEncrypt(
                          name+ ", "
                          + time+ ", "
                          + correct
                          + " of "
                          + total))

                                                 + "]"
                                                 + strCR +
                                                 strCR
                                                 + "To submit: paste the number you got correct, and the Confirmation Code (into the d2l "
                                                 + strCR
                                                 + "Quiz Attempt).   "
                                                 + strCR +
                                                 strCR
                                                 + " You should copy and paste the Confirmation Code, as it may contain unusual characters."
                      );

                  code.setEditable(false);

                  code.setLineWrap(true);
                  code.setWrapStyleWord(true);
                  code.setFont(new Font("Sans-Serif", Font.PLAIN, 12));

                  finishPanel.add(code, BorderLayout.CENTER);

                  finishPanel.add(new JLabel(
                      "Submit the number correct and Confirmation Code."
                      + "")
                                  , BorderLayout.SOUTH);
                }

                finishPanel.add(feedback, BorderLayout.NORTH);
              }
            }
          }
        }
      }
      }
    }
  }
}});
   /*
   switch (index){
     case 1: if (!fConnectiveRunning){
         fConnective.run();
         fConnectiveRunning=true;
         }
         break;
     case 2: if (!fTTRunning){
           fTT.run();
           fTTRunning=true;
           }
         break;
     case 3: if (!fSatisRunning){
        fSatis.run();
        fSatisRunning=true;
             }
        break;
      case 4: if (!fConsRunning){
          fCons.run();
          fConsRunning=true;
               }
          break;

        case 5: if (!fInvalidRunning){
            fInvalid.run();
            fInvalidRunning=true;
                 }
            break;

        case 6:
          finishPanel.remove(feedback);
          finishPanel.remove(code);

          int correct = (fConnective.getCorrect()
                         +fTT.getCorrect()
                         +fSatis.getCorrect()
                         +fCons.getCorrect()
                         +fInvalid.getCorrect()
              );
          int total = (fConnective.getTotal()
                       +fTT.getTotal()
                       +fSatis.getTotal()
                       +fCons.getTotal()
                       +fInvalid.getTotal()
              );


          String name=fNameEntry.fName.getText();

          if (name==null||name.equals("")){
            feedback=new JLabel("You need to enter a name on the Intro Page.");

          }
          else{

            feedback = new JLabel(fNameEntry.fName.getText() + ", you have "
                                  + correct
                                  + " right out of "
                                  + total
                                  + ".");



            JTextArea code = new JTextArea(strCR
                                           +"Here is the number you got correct: " +correct

                                           +strCR
                                           +"Here is your confirmation code:  ["
                                          +TUtilities.urlEncode( TUtilities.xOrEncrypt(
                                               name
                                               + " "
                                               + correct
                                               + " of "
                                               + total))
                                           + "]"
                                           +strCR+strCR
                                           +"To submit: paste the number you got correct, and the Confirmation Code (into the d2l "
                                           +strCR
                                           +"Quiz Attempt).   "
                                           +strCR+strCR
                                       +" You should copy and paste the Confirmation Code, as it may contain unusual characters."
                );

            code.setEditable(false);

            code.setLineWrap(true);
 code.setWrapStyleWord(true);
 code.setFont(new Font("Sans-Serif",Font.PLAIN,12));




            finishPanel.add(code, BorderLayout.CENTER);

            finishPanel.add(new JLabel("Submit the number correct and Confirmation Code."
                                       +"")
                            , BorderLayout.SOUTH);
          }

          finishPanel.add(feedback, BorderLayout.NORTH);

            break;


   }
   }});

*/



  getContentPane().add(fTabs);





/*


 //TEMP   Container panel= fConnectiveGame.getContentPane();

    fConnectiveGame.setVisible(false);

    setContentPane(fConnectiveGame);

    setSize(fConnectiveGame.getSize());

    //setSize(400,200);
    setLocation((TDeriverApplication.fScreenSize.width-400)/2, (TDeriverApplication.fScreenSize.height-200)/2);
    this.setResizable(false);

    fConnectiveGame.run();


    setVisible(true);  */



}

  public void removeConnectiveTab(){

fTabs.remove(fConnective);

}





class NameEntry extends JPanel{
  public JTextField fName= new JTextField(20);
  public NameEntry(){
    super (new BorderLayout());

  //  setPreferredSize(new Dimension(32,100));

    JLabel label = new JLabel("Name: ");

    add(label,BorderLayout.WEST);
    add(fName,BorderLayout.CENTER);

  }
}

  protected void processWindowEvent(WindowEvent e){
    if (e.getID()==WindowEvent.WINDOW_CLOSING)
      fNumOpen-=1;

    super.processWindowEvent(e);
  }
}

