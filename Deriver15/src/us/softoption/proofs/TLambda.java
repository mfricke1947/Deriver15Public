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

package us.softoption.proofs;

import static us.softoption.infrastructure.Symbols.chAlpha;
import static us.softoption.infrastructure.Symbols.chDoubleArrow;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chLambda;
import static us.softoption.infrastructure.Symbols.chLeftCurlyBracket;
import static us.softoption.infrastructure.Symbols.chRightCurlyBracket;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;

import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import us.softoption.editor.TDeriverDocument;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TSwingUtilities;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;


public class TLambda extends TMyProofPanel {


static TFormula x = new TFormula(
                               TFormula.variable,
                                     "x",
                                     null,
                                     null);
static TFormula y = new TFormula(
                                     TFormula.variable,
                                     "y",
                                     null,
                                   null);




static TFormula z = new TFormula(
                             TFormula.variable,
                                   "z",
                                   null,
                                   null);
static TFormula s = new TFormula(
                                   TFormula.variable,
                                   "s",
                                   null,
                                   null);

static TFormula sz = new TFormula(
                              TFormula.application,
                               "",
                               s,
                               z);

static TFormula zero = new TFormula(
                                   TFormula.functor,
                                 "0",
                                 null,
                                 null);

static TFormula zeroLong = new TFormula(
                                 TFormula.lambda,
                                 String.valueOf(chLambda),
                                 s,
                                 new TFormula(
                                          TFormula.lambda,
                                          String.valueOf(chLambda),
                                          z,
                                          z
                                               )
                                  );

static TFormula one = new TFormula(
                                                                 TFormula.functor,
                                                               "1",
                                                               null,
                                                               null);

static TFormula oneLong = new TFormula(
                                   TFormula.lambda,
                                   String.valueOf(chLambda),
                                   s,
                                   new TFormula(
                                            TFormula.lambda,
                                            String.valueOf(chLambda),
                                            z,
                                            sz
                                                    )
                                  );



static TFormula T = new TFormula(TFormula.functor,
                                "T",
                                null,
                                null);
static TFormula TForm = new TFormula(
                                 TFormula.lambda,
                                 String.valueOf(chLambda),
                                 x,
                                 new TFormula(
                                 TFormula.lambda,
                                      String.valueOf(chLambda),
                                      y,
                                      x
                                                                                  )
                                  );

static TFormula F = new TFormula(TFormula.functor,
                                                                    "F",
                                                                                                                           null,
                                                                                                                           null);
static TFormula FForm = new TFormula(
                                                               TFormula.lambda,
                                                               String.valueOf(chLambda),
                                                               x,
                                                               new TFormula(
                                                               TFormula.lambda,
                                                                    String.valueOf(chLambda),
                                                                    y,
                                                                    y
                                                                                                                )
                                                                );


static TFormula C = new TFormula(TFormula.functor,
                                 "C",
                                 null,
                                 null);
static TFormula implic = new TFormula(TFormula.functor,
                                 ""+chImplic,
                                 null,
                                 null);

static TFormula CForm = new TFormula(
                                 TFormula.lambda,
                                 String.valueOf(chLambda),
                                 x,
                                 new TFormula(
                                          TFormula.lambda,
                                          String.valueOf(chLambda),
                                          y,
                             new TFormula(
                            TFormula.lambda,
                            String.valueOf(chLambda),
                            z,
                             new TFormula(
                            TFormula.application,
                            "",
                            new TFormula(
                            TFormula.application,
                            "",
                            x,
                             y
                             )
,
                             z
                             )

                             )

                                           ));








static String alphaJustification = " " + chAlpha;


JMenuItem fAlphaMenuItem= new JMenuItem(chAlpha+" Alpha");



public TLambda(TDeriverDocument itsDeriverDocument){
    super(itsDeriverDocument);

    rearrangeMenus();
  }

public TLambda(TDeriverDocument itsDeriverDocument,boolean wantsIdentity){
      super(itsDeriverDocument,wantsIdentity);

          rearrangeMenus();
  }


public boolean loadLambda(String inputStr){

 /*need to go careful here, the inputStr can contain spaces, as can wff lambdas */

               TParser parser=null;;

               if (fDeriverDocument!=null)
                 parser=fDeriverDocument.getParser();

               if (parser==null){
                 if (fParser==null)
                   initializeParser();
                 parser = fParser;
               }
               //we need to sort the above out, the document has a parser, and so too does the proof panel

               parser.initializeErrorString();


              ArrayList dummy=new ArrayList();
              boolean wellformed = true;

              fProofStr="";  //re-initialize; the old proof may still be there and if this turns out to be illformed will stay there

           //   StringSplitData data;

              if ((inputStr==null)||inputStr==strNull){

                createBlankStart();
                return
                    wellformed;
              }

       String[]premisesAndConclusion = inputStr.split(String.valueOf(chDoubleArrow),2);  /* they may input two
               therefore symbols, in which case we'll split at the first and let the parser report the second*/

          if (premisesAndConclusion[0]!=strNull){  // there are premises

        /*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
            compound terms eg Pf(a,b),Hc. We want to separate the premises but not the terms. So we will change the
         premise separators to another character*/

        int nested=0;
        char currCh;
        char chSeparator='�';
        StringBuffer input= new StringBuffer(premisesAndConclusion[0]);
        for (int i=0;i<input.length();i++){
          currCh=input.charAt(i);

          if ((currCh==chSmallLeftBracket)||
        	    	(currCh==chLeftCurlyBracket))
        	      nested++;
        	    if ((currCh==chSmallRightBracket)||
        	    	(currCh==chRightCurlyBracket))
        	      nested--;


          if ((nested<1)&&(currCh==chComma))
            input.setCharAt(i,chSeparator);
        }

        premisesAndConclusion[0]=input.toString();

            StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chSeparator)/*String.valueOf(chComma)*/);

            while ((premises.hasMoreTokens())&&wellformed){
               inputStr=premises.nextToken();

            if (inputStr!=strNull){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
                   TFormula root = new TFormula();
                   StringReader aReader = new StringReader(inputStr);


                   wellformed=parser.lambdaWffCheck(root, dummy, aReader);

                   if (!wellformed)
                     fDeriverDocument.writeToJournal(parser.fCurrCh + TConstants.fErrors12 + parser.fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
                   else
                       {addPremise(root);
                       if (fProofStr.length()==0)
                         fProofStr=inputStr;
                       else
                         fProofStr+=chComma+inputStr;
                       }
                   }
            }          // done with premises

            if (premisesAndConclusion.length>1){  // if there is no therefore the original 'split' won't split the input
              inputStr = premisesAndConclusion[1];

              if (inputStr!=strNull){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
                   TFormula root = new TFormula();
                   StringReader aReader = new StringReader(inputStr);

                   wellformed=fParser.lambdaWffCheck(root, dummy, aReader);

                   if (!wellformed)
                     fDeriverDocument.writeToJournal(parser.fCurrCh + TConstants.fErrors12 + parser.fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
                   else
                       {addConclusion(root);
                       fProofStr+=chDoubleArrow+inputStr;
                       }
                   }

            }

          };


              return
                  wellformed;

     }



void rearrangeMenus(){  // we use the advanced menu

fMenuBar.remove(fRulesMenu);
fMenuBar.remove(fWizardMenu);

//fMenuBar.add(rewriteMenuItem);

fAdvancedRulesMenu.setText("Rules");
fAdvancedRulesMenu.remove(theoremMenuItem);

//fAlphaMenuItem.setText(chAlpha+" Alpha");
fAlphaMenuItem.addActionListener(new TLambda_fAlphaMenuItem_actionAdapter(this));
fAdvancedRulesMenu.add(fAlphaMenuItem);



};

void fAlphaMenuItem_actionPerformed(ActionEvent e) {
      doAlpha();
    }



void doSetUpRulesMenu(){     /* does advanced rules as well*/
       TFormula selectedFormula=null, secondSelectedFormula=null, thirdSelectedFormula=null;

       TProofline selection = fProofListView.oneSelected();

       boolean oneSelected;

       int totalSelected=fProofListView.totalSelected();

       oneSelected=(selection!=null);

       fAlphaMenuItem.setEnabled(false);

       if (oneSelected){
         selectedFormula = selection.fFormula;

         if (fParser.isLambda(selectedFormula))
           fAlphaMenuItem.setEnabled(true);



       }




}




void  doStartAgain(){              //menuItem override
    TUndoableProofEdit newEdit = new TUndoableProofEdit();   // this copies old lines

    if (fProofStr!=null)          // record of this proof as a string
      startLambdaProof(fProofStr);

    newEdit.doEdit();            // does not do any editing but kills last edit and allows undo
 }



 public class AlphaAction extends AbstractAction{
       JTextComponent fText;
       TProofline fFirstline=null;




public AlphaAction(JTextComponent text, String label, TProofline firstline){
          putValue(NAME, label);

          fText=text;
          fFirstline=firstline;
        }

         public void actionPerformed(ActionEvent ae){


           /*********************/


           boolean useFilter = true;
           ArrayList dummy = new ArrayList();

           String aString = TSwingUtilities.readTextToString(fText, TUtilities.noFilter);

           TFormula newVar = new TFormula();
           StringReader aReader = new StringReader(aString);
           boolean wellformed=false;

           wellformed=fParser.lambdaWffCheck(newVar, dummy, aReader)&&
                      fParser.isVariable(newVar);//fParser.term(term,aReader);

           if (!wellformed) {
             String message = "The string is not a term." +
                 (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

             //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

             fText.setText(message);
             fText.selectAll();
             fText.requestFocus();
           }


           else {

             TFormula scope = fFirstline.fFormula.fRLink.copyFormula();

             if(!scope.freeForTest(newVar, fFirstline.fFormula.lambdaVarForm())){

               String message = aString + " for " +
                                fFirstline.fFormula.lambdaVar()+
                                " in " +
                                fParser.writeFormulaToString(scope) +
                                " leads to capture. " +
                                "Use another variable or Cancel";


                fText.setText(message);
                fText.selectAll();
                fText.requestFocus();
             }
             else{

            	 scope.subTermVar(scope,newVar,fFirstline.fFormula.lambdaVarForm());

               TProofline newline = supplyProofline();

               TFormula newLambda= new TFormula(
                                 TFormula.lambda,
                                 String.valueOf(chLambda),
                                 newVar,
                                 scope
                                  );


               int level = fModel.getHeadLastLine().fSubprooflevel;

               newline.fFormula = newLambda;
               newline.fJustification = alphaJustification;
               newline.fFirstjustno = fFirstline.fLineno;
               newline.fSubprooflevel = level;

               TUndoableProofEdit newEdit = new TUndoableProofEdit();
               newEdit.fNewLines.add(newline);
               newEdit.doEdit();

               removeInputPane();
             }
           }

       }

    }



 void doAlpha(){
   TProofline firstline;
   JButton defaultButton;
    JButton dropLastButton;
    TProofInputPanel inputPane;


   firstline=fProofListView.oneSelected();

   if ((firstline != null)&&fParser.isLambda(firstline.fFormula)) {

     JTextField text = new JTextField("New variable to use?");
        text.selectAll();

        defaultButton = new JButton(new AlphaAction(text,"Go", firstline));

        JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
        inputPane = new TProofInputPanel("Doing Alpha", text, buttons);


        addInputPane(inputPane);

        inputPane.getRootPane().setDefaultButton(defaultButton);
        fInputPane.setVisible(true); // need this
        text.requestFocus();         // so selected text shows


      }


}




public void doNewRewrite(){
       JButton defaultButton;

       TProofInputPanel inputPane;


       TProofline selectedLine=fProofListView.oneSelected();

       if (selectedLine!=null){

         String originalFormulaStr=fParser.writeFormulaToString(selectedLine.fFormula);

         TRewriteRules rules= new TLambdaRewriteRules(selectedLine.fFormula,fParser);

          boolean mustChange=true;
          defaultButton = new JButton(new RewriteAction("Go",
                                                        rules,
                                                        selectedLine.fLineno,
                                                        !mustChange));


          JComponent[]components = {rules.getComboBox(),  new JButton(new CancelAction()), defaultButton };  // put cancel on left

          inputPane = new TProofInputPanel("Select Rewrite Rule, select (sub)formula to reduce, click Go...",
                                           rules.getBeforeText(),
                                           "After rewrite, the whole formula will look like this:",
                                           rules.getAfterText(),
                                           components);


               addInputPane(inputPane);

               inputPane.getRootPane().setDefaultButton(defaultButton);
               fInputPane.setVisible(true); // need this
               rules.getBeforeText().requestFocus();         // so selected text shows

        }

  }


  public void startLambdaProof(String inputStr){


    dismantleProof(); //{previous one}

    initProof();

    if (loadLambda(inputStr))
      startUp();

  }

}


class TLambda_fAlphaMenuItem_actionAdapter implements java.awt.event.ActionListener {
  TLambda adaptee;

  TLambda_fAlphaMenuItem_actionAdapter(TLambda adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.fAlphaMenuItem_actionPerformed(e);
  }
}
