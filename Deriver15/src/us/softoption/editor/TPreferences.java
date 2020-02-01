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


import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import us.softoption.infrastructure.TConstants;


/*

CLEAN THIS UP WE HAVE BOTH STATIC AND INSTANCE REFS TO THE USERS PREFS.

Call initPrefKeys when application launches


This is going to be key/value. But we decide what the keys are. Elsewhere the key 'Home' is
entered for the file home.

Select will enable Put value button.

About half the preferences are set automatically eg the file chooser remembers the last file save.

 But others eg User can be changed.



*/

public class TPreferences extends JFrame {
     private JLabel fNameField = new JLabel("Select!");
     private JTextField fValueField= new JTextField(24);
     private JList      fPrefList  = new JList(new String[] {});
     //private Preferences fUserPrefs;

     static public int fNumOpen=0;
     static public TPreferences thePrefDialog=null;  // only one

     // if you add any fields also alter the method reset to defaults

     static public boolean fPrintDerived=true;       // for printing the "auto' in proofs if a line is derived



     static public String  fHome="";                   //global values for the prefs.
     static public boolean fColorProof=true;                   //global values for the prefs.
     static public boolean fDerive=true;               //theorem prover
     static public boolean fEndorseMenu=true;
     static public boolean fGamesMenu=true;
     static public boolean fHTMLMenu=true;
     static public boolean fFirstOrder=false;          // first Order theories, induction
     static public boolean fIdentity=false;             //functional terms and identity
     static public boolean fInterpretation=true;
     static public boolean fLambda=false;
     static public boolean fModal=false;
     static public String  fPaletteText=TConstants.fDefaultPaletteText;
     static public String  fParser="default [barwise bergmann copi gentzen hausman herrick howson priest]"; /*the first word is the choice, all are possible values*/
     static public boolean fProofs=true;
     static public boolean fReadFromClipboard=false;  // if no text is selected, will default to clipboard
     static public boolean fRewriteRules=true;
     static public int     fRightMargin=300;//250;
     static public boolean fSetTheory=false;
     static public boolean fSimpleFileMenu=false;
     static public boolean fTrees=true;
     static public boolean fTypeLabels=false;
     static public boolean fUseAbsurd=true;
     static private String fUser="";

     static public boolean fBlind=false;   /* suppresses justification on auto derivation. Not something for
                                           the User to set, but when teaching, with applets, its useful to help
                                           them think through it */

     static String[] fOurKeys = {"Home","User","colorProof",
                                 "derive","endorseMenuItem","gamesMenu",
                                 "htmlMenu","firstOrder","identity",
                                 "interpretations","lambda","modal","paletteText",
                                 "parser","proofs","simpleFileMenu",
                                 "rightMargin","useAbsurd","rewriteRules",
                                 "setTheory",
                                 "trees","readFromClipboard","typeLabels"};
     static String[] fOurKeyDefaults = {
                                 "","","true",
                                 "true","true","true",
                                 "true","false","false",
                                 "true","false","false",TConstants.fDefaultPaletteText,
                                 "default [barwise bergmann copi gentzen hausman herrick howson priest]","true","false",
                                 "280","true","true",
                                 "false",
                                 "true","false","false"};


     //========================================================== constructor
     TPreferences() {

       thePrefDialog=this;                      //need to get this in early

       this.setMinimumSize(new Dimension(600,400));
       this.setSize(new Dimension(600,400));    // pack() will reset these
       this.setMaximumSize(new Dimension(600,600));
       this.setResizable(false);


    setLocation(((Toolkit.getDefaultToolkit().getScreenSize()).width-400)/2, 100);


       //fNameField.setEditable(false);

       fPrefList.addListSelectionListener(new ListSelectionListener(){
       public void valueChanged(ListSelectionEvent e){
         String selection = (String)fPrefList.getSelectedValue();
                 if (selection != null) {
                     String key = selection.substring(0, selection.indexOf("="));
                 //    _prefs.remove(key);  // Remove the selected key.
                 //    setListFromPrefs();  // Update display

                 fNameField.setText(key);
                 }
                 else
                   fNameField.setText("Select!");

 //TPreferences.thePrefDialog.pack();   // different sized keys cause the window to resize
       }
       });


        // fUserPrefs = Preferences.userNodeForPackage(this.getClass());
         setListFromPrefs();

         //-- Create and set attributes of widgets.
         JScrollPane scrollingList = new JScrollPane(fPrefList);
         JButton newKeyButton = new JButton("Put Key=Value");
      //   JButton clearButton = new JButton("Clear All");
      //   JButton delSelectedButton = new JButton("Remove Selected");

         //-- Set action listeners.
         newKeyButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {







                 if (fValueField.getText().length() > 0) {
                   Preferences userPreferences = Preferences.userNodeForPackage(TPreferences.class);
                     userPreferences.put(fNameField.getText(), fValueField.getText());

                     if(fNameField.getText().equals("parser"))
                       canonicalizeParserName(fValueField.getText());

                     try {userPreferences.flush();}
                     catch (BackingStoreException ex){

                     }

                     loadUserPrefs(); //for some reason it is taking me 2 new browsers to get an update

                     fNameField.setText("");   // Clear fields after saving.
                     fValueField.setText("");
                     setListFromPrefs();  // Update display
                 } else {
                     fNameField.setText("Key?");
                 }
             }});

        /* clearButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 try {
                     _prefs.clear();
                     setListFromPrefs();  // Update display
                 } catch (BackingStoreException ex) {
                     System.out.println(ex);
                 }
             }});  */

     /*    delSelectedButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 String selection = (String)fPrefList.getSelectedValue();
                 if (selection != null) {
                     String key = selection.substring(0, selection.indexOf("="));
                     _prefs.remove(key);  // Remove the selected key.
                     setListFromPrefs();  // Update display
                 }
             }}); */

  /*   try {fUserPrefs.clear();}catch (BackingStoreException ex) {
                     System.out.println(ex);
                 }


     fUserPrefs.remove("name");

  fUserPrefs.put("Home","");  */


         //-- Layout widgets.
         JPanel buttonPanel = new JPanel(new GridBagLayout());

         buttonPanel.add(new JLabel("Key:"), new GridBagConstraints(0, 0, 1, 1, 1.0, 0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0)
);
    buttonPanel.add(fNameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0
   , GridBagConstraints.WEST, GridBagConstraints.BOTH,
   new Insets(0, 0, 0, 0), 0, 0)
);
       buttonPanel.add(new JLabel("Value:"), new GridBagConstraints(2, 0, 1, 1, 1.0, 0
      , GridBagConstraints.EAST, GridBagConstraints.NONE,
      new Insets(0, 0, 0, 0), 0, 0)
);
  buttonPanel.add(fValueField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0
 , GridBagConstraints.WEST, GridBagConstraints.BOTH,
 new Insets(0, 0, 0, 0), 0, 0)
);
       buttonPanel.add(newKeyButton, new GridBagConstraints(4, 0, 1, 1, 1.0, 0
      , GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
      new Insets(0, 0, 0, 0), 0, 0))
;

       //  buttonPanel.add(delSelectedButton);
        // buttonPanel.add(clearButton);

        JLabel instructions=new JLabel("<html><em>Enter 'default' for default values. Use new Deriver Browser to effect your changes.</em></html.",SwingConstants.CENTER);


     GridBagLayout gridBagLayout = new GridBagLayout();

        //JPanel content = new JPanel(new BorderLayout());

         Container content = getContentPane();

         content.setLayout(gridBagLayout);


         content.add(buttonPanel  , new GridBagConstraints(0, 0, 1, 1, 1.0, 0.1
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0), 0, 0));

    content.add(instructions, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.05
   , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
   new Insets(0, 0, 0, 0), 0, 0));

       content.add(scrollingList, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.8
      , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));


         //this.setContentPane(content);
     //    this.pack();

    fNumOpen+=1;


    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }//end constructor


/*******************  USER **********************/


/*There is a difference here between a regular User and a user on a particular occasion-- think Lab
 machines.

 The regular User is stored. But when the application opens. TPreferences.fUser is initialized to
 "". When getUser is called the first time, it offers a dialog with the stored name. Then
 it reads and stores what comes back. In effect, it confirms what is happening on the first call of
 getUser (but does not ask again).
*/



public static String getUser(){
  String storedUser="";

  if (fUser==null||fUser.equals("")){
    Preferences userPreferences = Preferences.userNodeForPackage(TPreferences.class);
    storedUser = userPreferences.get("User", "");
  }

  while (fUser==null||fUser.equals("")){

    String reply = (String) JOptionPane.showInputDialog(null,
        "Please enter or confirm your name", "User Name",
        JOptionPane.QUESTION_MESSAGE, null, null, storedUser);

    if (reply != null && !reply.equals(""))
      setUser(reply);
  }
  return
      fUser;
}


public static void setUser(String user){
  Preferences userPreferences = Preferences.userNodeForPackage(TPreferences.class);

  fUser=user;
  userPreferences.put("User",user);
}


     //===================================================== setListFromPrefs
     private void setListFromPrefs() {

       Preferences userPreferences = Preferences.userNodeForPackage(TPreferences.class);
         try {
             String[] keys = userPreferences.keys();

             Arrays.sort(keys);

             for (int i=0; i<keys.length; i++) {
                 keys[i] += "=" + userPreferences.get(keys[i], "ERROR");
             }
             fPrefList.setListData(keys);

             fPrefList.setSelectedIndex(0);

         } catch (BackingStoreException ex) {
             System.out.println(ex);
         }
     }

  private void jbInit() throws Exception {
  }



  protected void processWindowEvent(WindowEvent e){
     if (e.getID()==WindowEvent.WINDOW_CLOSING)
       fNumOpen-=1;

     super.processWindowEvent(e);
  }

public static void initPrefKeys(){
  Preferences userPreferences = Preferences.userNodeForPackage(TPreferences.class);

  String[] storeKeys = {""};
  try {
    storeKeys = userPreferences.keys();
  }
  catch (BackingStoreException e) {

  }

  boolean found = false;
  String search="";

  for (int i=0; i < fOurKeys.length; i++) {
    found = false;
    search=fOurKeys[i];

    for (int j=0; (j < storeKeys.length)&&!found; j++) {
      if (search.equals(storeKeys[j]))
        found=true;
    }
  if(!found)
    userPreferences.put(search,fOurKeyDefaults[i]); //some of these should be boolean etc., but when read will default to proper default.
  }
}

static public void canonicalizeParserName(String key){
  if (key.length()>0){
    if ((key.charAt(0) == 'B' || key.charAt(0) == 'b')&&
       (key.charAt(1) == 'A' || key.charAt(1) == 'a'))
       canonicalBarwise(key);
    if ((key.charAt(0) == 'B' || key.charAt(0) == 'b')&&
    	(key.charAt(1) == 'E' || key.charAt(1) == 'e'))
    	       canonicalBergmann(key);
    if (key.charAt(0) == 'C' || key.charAt(0) == 'c')
       canonicalCopi(key);
    if (key.charAt(0) == 'D' || key.charAt(0) == 'd')
        canonicalDefault(key);
    if (key.charAt(0) == 'G' || key.charAt(0) == 'g')
       canonicalGentzen(key);

    if (key.length()>1){
      if ((key.charAt(0) == 'H' || key.charAt(0) == 'h')&&
          (key.charAt(1) == 'A' || key.charAt(1) == 'a'))
        canonicalHausman(key);
      if ((key.charAt(0) == 'H' || key.charAt(0) == 'h')&&
          (key.charAt(1) == 'E' || key.charAt(1) == 'e'))
        canonicalHerrick(key);
      if ((key.charAt(0) == 'H' || key.charAt(0) == 'h')&&
          (key.charAt(1) == 'O' || key.charAt(1) == 'o'))
        canonicalHowson(key);

      if ((key.charAt(0) == 'P' || key.charAt(0) == 'p')&&
          (key.charAt(1) == 'R' || key.charAt(1) == 'r'))
        canonicalPriest(key);

    }
  }
}

static public void canonicalBarwise(String key){
	  if (!key.equals("barwise [bergmann copi default gentzen hausman herrick howson priest]"))
	    Preferences.userNodeForPackage(TPreferences.class).put("parser","barwise [bergmann copi default gentzen hausman herrick howson priest]");

	}

static public void canonicalBergmann(String key){
  if (!key.equals("bergmann [barwise copi default gentzen hausman herrick howson priest]"))
    Preferences.userNodeForPackage(TPreferences.class).put("parser","bergmann [barwise copi default gentzen hausman herrick howson priest]");

}

static public void canonicalCopi(String key){
  if (!key.equals("copi [barwise bergmann default gentzen hausman herrick howson priest]"))
    Preferences.userNodeForPackage(TPreferences.class).put("parser","copi [barwise bergmann default gentzen hausman herrick howson priest]");

}

static public void canonicalDefault(String key){
	  if (!key.equals("default [barwise bergmann copi gentzen hausman herrick howson priest]"))
	    Preferences.userNodeForPackage(TPreferences.class).put("parser","default [barwise bergmann copi gentzen hausman herrick howson priest]");

	}


static public void canonicalHausman(String key){
  if (!key.equals("hausman [barwise bergmann default copi gentzen herrick howson priest]"))
    Preferences.userNodeForPackage(TPreferences.class).put("parser","hausman [barwise bergmann copi default gentzen herrick howson priest]");

}


static public void canonicalHerrick(String key){
  if (!key.equals("herrick [barwise bergmann copi default gentzen hausman howson priest]"))
    Preferences.userNodeForPackage(TPreferences.class).put("parser","herrick [barwise bergmann copi default gentzen hausman howson priest]");

}

static public void canonicalHowson(String key){
  if (!key.equals("howson [barwise bergmann copi default gentzen hausman herrick priest]"))
    Preferences.userNodeForPackage(TPreferences.class).put("parser","howson [barwise bergmann copi default gentzen hausman herrick priest]");

}

static public void canonicalPriest(String key){
  if (!key.equals("priest [barwise bergmann copi default gentzen hausman herrick howson]"))
    Preferences.userNodeForPackage(TPreferences.class).put("parser","priest [barwise bergmann copi default gentzen hausman herrick howson]");

}

static public void canonicalGentzen(String key){
  if (!key.equals("gentzen [barwise bergmann copi default hausman herrick howson priest]"))
    Preferences.userNodeForPackage(TPreferences.class).put("parser","gentzen [barwise bergmann default copi hausman herrick howson priest]");

}

public static void loadUserPrefs (){
Preferences userPreferences = Preferences.userNodeForPackage(TPreferences.class);


fHome=userPreferences.get("Home","");                   //global values for the prefs.
//fUser=userPreferences.get("User","");                 // don't load this, see get User
fColorProof=userPreferences.getBoolean("colorProof",true);                   //global values for the prefs.
fDerive=userPreferences.getBoolean("derive",true);
fEndorseMenu=userPreferences.getBoolean("endorseMenuItem",true);
fGamesMenu=userPreferences.getBoolean("gamesMenu",true);
fHTMLMenu=userPreferences.getBoolean("htmlMenu",true);
fFirstOrder=userPreferences.getBoolean("firstOrder",false);
fIdentity=userPreferences.getBoolean("identity",false);
fInterpretation=userPreferences.getBoolean("interpretations",true);
fLambda=userPreferences.getBoolean("lambda",false);
fModal=userPreferences.getBoolean("modal",false);
fPaletteText=userPreferences.get("paletteText",TConstants.fDefaultPaletteText);
fParser=userPreferences.get("parser","default [barwise bergmann copi gentzen hausman herrick howson priest]");
fProofs=userPreferences.getBoolean("proofs",true);
fSetTheory=userPreferences.getBoolean("setTheory",false);
fSimpleFileMenu=userPreferences.getBoolean("simpleFileMenu",false);
fRightMargin=userPreferences.getInt("rightMargin",280/*250*/);
fReadFromClipboard=userPreferences.getBoolean("readFromClipboard",false);
fRewriteRules=userPreferences.getBoolean("rewriteRules",true);
fTrees=userPreferences.getBoolean("trees",true);
fTypeLabels=userPreferences.getBoolean("typeLabels",false);
fUseAbsurd=userPreferences.getBoolean("useAbsurd",true);

}

public static void saveUserPrefs(){  //on exit

Preferences userPreferences = Preferences.userNodeForPackage(TPreferences.class);


}

public static void resetToDefaults(){

// The applets no longer uses this to start with a clean slate


   fPrintDerived=true;       // for printing the "auto' in proofs if a line is derived



       fHome="";                   //global values for the prefs.
      fColorProof=true;                   //global values for the prefs.
      fDerive=true;               //theorem prover
      fEndorseMenu=true;
      fGamesMenu=true;
      fHTMLMenu=true;
      fFirstOrder=false;          // first Order theories, induction
      fIdentity=false;             //functional terms and identity
      fInterpretation=true;
      fLambda=false;
      fModal=false;
       fPaletteText=TConstants.fDefaultPaletteText;
       fParser="default [barwise bergmann copi gentzen hausman herrick howson priest]"; /*the first word is the choice, all are possible values*/
      fProofs=true;
      fReadFromClipboard=false;  // if no text is selected, will default to clipboard
      fRewriteRules=true;
      fRightMargin=300;//250;
      fSetTheory=false;
      fSimpleFileMenu=false;
      fTrees=true;
      fTypeLabels=false;
      fUseAbsurd=true;
      String fUser="";

      fBlind=false;


}

}//end class PrefTest
