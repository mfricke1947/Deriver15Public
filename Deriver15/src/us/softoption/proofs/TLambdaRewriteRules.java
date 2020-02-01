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

import static us.softoption.infrastructure.Symbols.chBeta;
import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chLambda;

import java.awt.Toolkit;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.text.BadLocationException;

import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;


public class TLambdaRewriteRules extends TRewriteRules {

public TLambdaRewriteRules(TFormula selectedFormula,
                        TParser aParser){
 super(selectedFormula, aParser);
   }


/********** overrides to use lambda parser ****************/

   public TFormula getAfterRoot(){
  // we need to find the entire after formula

  TFormula afterRoot = new TFormula();
  StringReader aReader = new StringReader(fAfterText.getText());
  ArrayList dummy = new ArrayList();

  boolean wellFormed = fParser.lambdaWffCheck(afterRoot, dummy, aReader);

  if (wellFormed)
    return afterRoot;
  else
    return
        null;
}


boolean getOldFormula(){
  boolean wellFormed=false;

  String entry=fBeforeText.getText();
  int selStart=fBeforeText.getSelectionStart();
  int selEnd=fBeforeText.getSelectionEnd();
  //int entryEnd=entry.length();

  try
     {fPreSelection=fBeforeText.getText(0,selStart);}  //NB getText uses offset, len
  catch (BadLocationException e)
     {fPreSelection="";
     System.out.print("Rewrite catch Pre");}
  fSelection=fBeforeText.getSelectedText();
  try
     {fPostSelection=fBeforeText.getText(selEnd,entry.length()-selEnd);}
  catch (BadLocationException ex)
     {fPostSelection="";
     System.out.print("Rewrite catch Post");
     }

//They must not have a trailing blank because parser will parse but before will be an
     // application and after cannot be because that blank goes missing

if (fSelection==null||
     fSelection.length()==0||
     fSelection.charAt(0)==chBlank||
     fSelection.charAt(fSelection.length()-1)==chBlank){
   Toolkit.getDefaultToolkit().beep(); //give error message?
   return
    !wellFormed;
}


   fSelectionRoot = new TFormula();
   StringReader aReader = new StringReader(fSelection);
   ArrayList dummy = new ArrayList();

   wellFormed = fParser.lambdaWffCheck(fSelectionRoot, dummy, aReader);


   /*The subformula check is for this with AvBcd they could select AvBc which is WFF but not a subformula*/

    if (!wellFormed||!(fSelectedFormula).subFormulaOccursInFormula(fSelectionRoot, fSelectedFormula)){
     Toolkit.getDefaultToolkit().beep(); //give error message?
     fBeforeText.select(0,0);            // we'll clear the selection if it is not well formed
   }

  return
     wellFormed;
}



void putNewFormula(){


  // change of syntax means don't need following

    // with lambda's we want to put the reduction in brackets to prevent incorrect association
  // eg (lambda x. a)<expr> where expr reduces to b lambda x. ab


   if (!(fNewRoot==null||fParser.writeFormulaToString(fNewRoot).equals("")))   // we'll only do this if there is change
   {if ((fPreSelection.length()==0)&&
       (fSelection.length()>0)&&
       (fPostSelection.length()==0)

         ){

       if ((fSelection.charAt(0)!= '('))
        fSelectionRewrite = fParser.writeFormulaToString(fNewRoot);   //we'll omit brackets (doesn't with lambda)
      else
        fSelectionRewrite = fParser.writeInner(fNewRoot);


       }
       else
         fSelectionRewrite = fParser.writeInner(fNewRoot);


       //{June 1990 but then you get the problem of p:-pVp and then the pVp associating incorrectly!}

     fAfterText.setText(fPreSelection +
                        fSelectionRewrite +
                        fPostSelection);
     }
   }







  /**********************/



   JComboBox initializeRules(){
    JComboBox rules;

   // String [] ruleStrings={"p^p :: q^p","two"};

    rules=new JComboBox();
    rules.setMaximumRowCount(6);

    rules.addItem(new DoReduce());
    rules.addItem(new DoTrue());
    rules.addItem(new DoFalse());
    rules.addItem(new DoCond());
    rules.addItem(new DoZero());
    rules.addItem(new DoOne());


  return
     rules;
  }


  class DoReduce extends AbstractRule{


    boolean doRule() {
      TFormula p,m,term,var,scope;


      if (fParser.isApplication(fSelectionRoot)&&
          fParser.isLambda(fSelectionRoot.fLLink)) {  // Lambda x.A b::(Ex m)~p)

        //This is like Universal Instantiation

        term = fSelectionRoot.fRLink;
        scope = fSelectionRoot.fLLink.scope();
        var = fSelectionRoot.fLLink.lambdaVarForm();

        if ( (term != null) &&
            (scope != null) &&
            (var != null)) {

         if (!scope.freeForTest(term, var)) {

           Toolkit.getDefaultToolkit().beep(); // need to change variable here

          if (fParser.lambdaChangeVariable(fSelectionRoot)){
            scope = fSelectionRoot.fLLink.scope();
            var = fSelectionRoot.fLLink.lambdaVarForm();
          }
          else
            return
                false;   // run out of terms
         }


          term=term.copyFormula();
          scope=scope.copyFormula();
          var=var.copyFormula();




          scope.subTermVar(scope, term, var);

          fNewRoot =scope;

          /*
                    String message = aString + " for " +
                                       fFirstline.fFormula.quantVar()+
                                       " in " +
                                       fParser.writeFormulaToString(scope) +
                                       " leads to capture. " +
                                       "Use another term or Cancel";


           */



  /*        p = fSelectionRoot.fRLink.fRLink.copyFormula();
          m = fSelectionRoot.fRLink.quantVarForm().copyFormula();

          fNewRoot = new TFormula(TFormula.quantifier,
                                  String.valueOf(chExiquant),
                                  m,
                                  new TFormula(TFormula.unary,
                                               String.valueOf(chNeg),
                                               null,
                                               p)
              ); */

          fLastRewrite = " " + chBeta + "Reduce";
          return
              true;
        }
      }
      return
              false;
    }

    public String toString() {
      return
          "<html>"+
          "<em>Reduce &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
          "<strong>"+
          "("+chLambda+"x.A b) :- "+"A[b/x]"+
          "</strong>"+
          "</html>";
    }
}

  class DoCond extends AbstractRule{

     boolean doRule() {

       fLastRewrite = " Cond";

       if (fSelectionRoot.equalFormulas(fSelectionRoot,TLambda.C)||
           fSelectionRoot.equalFormulas(fSelectionRoot,TLambda.implic)){

           fNewRoot =TLambda.CForm;

           return
               true;
         }

         if (fParser.alphaEqualFormulas(fSelectionRoot,TLambda.CForm)){

             fNewRoot =TLambda.implic;

             return
                 true;
         }
       return
               false;
     }

     public String toString() {
       return
           "<html>"+
           "<em>Cond &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
           "<strong>"+
            chImplic+" :: "+chLambda+"x."+
                      chLambda+"y."+
                      chLambda+"z."+"((x y) z)"+
           "</strong>"+
           "</html>";
     }
}



  class DoFalse extends AbstractRule{

     boolean doRule() {

       fLastRewrite = " False";

       if (fSelectionRoot.equalFormulas(fSelectionRoot,TLambda.F)){

           fNewRoot =TLambda.FForm;

           return
               true;
         }

         if (fParser.alphaEqualFormulas(fSelectionRoot,TLambda.FForm)){

             fNewRoot =TLambda.F;

             return
                 true;
         }
       return
               false;
     }

     public String toString() {
       return
           "<html>"+
           "<em>False &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
           "<strong>"+
            "F"+" :: "+chLambda+"x."+chLambda+"y."+"y"+
           "</strong>"+
           "</html>";
     }
}
   class DoTrue extends AbstractRule{

      boolean doRule() {

        fLastRewrite = " True";

        if (fSelectionRoot.equalFormulas(fSelectionRoot,TLambda.T)){

            fNewRoot =TLambda.TForm;

            return
                true;
          }

          if (fParser.alphaEqualFormulas(fSelectionRoot,TLambda.TForm)){

              fNewRoot =TLambda.T;

              return
                  true;
          }
        return
                false;
      }

      public String toString() {
        return
            "<html>"+
            "<em>True &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
            "<strong>"+
             "T"+" :: "+chLambda+"x."+chLambda+"y."+"x"+
            "</strong>"+
            "</html>";
      }
}

  class DoZero extends AbstractRule{


     boolean doRule() {

       fLastRewrite = " Zero";

       if (fSelectionRoot.equalFormulas(fSelectionRoot,TLambda.zero)){

           fNewRoot =TLambda.zeroLong;

      //     fLastRewrite = " " + chBeta + "Zero";
           return
               true;
         }

         if (fParser.alphaEqualFormulas(fSelectionRoot,TLambda.zeroLong)){

             fNewRoot =TLambda.zero;

  //           fLastRewrite = " Zero";
             return
                 true;
         }
       return
               false;
     }

     public String toString() {
       return
           "<html>"+
           "<em>Zero &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
           "<strong>"+
            "0"+" :: "+chLambda+"s."+"("+chLambda+"z."+"z)"+
           "</strong>"+
           "</html>";
     }
}

   class DoOne extends AbstractRule{


       boolean doRule() {

         fLastRewrite = " One";

         if (fSelectionRoot.equalFormulas(fSelectionRoot,TLambda.one)){

             fNewRoot =TLambda.oneLong;

             return
                 true;
           }

           if (fParser.alphaEqualFormulas(fSelectionRoot,TLambda.oneLong)){

               fNewRoot =TLambda.one;

               return
                   true;
           }
         return
                 false;
       }

       public String toString() {
         return
             "<html>"+
             "<em>One &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
             "<strong>"+
              "1"+" :: "+chLambda+"s."+"("+chLambda+"z."+"s(z))"+
             "</strong>"+
             "</html>";
       }
}



}
