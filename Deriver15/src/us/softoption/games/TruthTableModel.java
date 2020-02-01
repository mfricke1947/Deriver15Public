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


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;

import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import us.softoption.interpretation.TTestNode;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

/*This is for displaying truth tables in a table.
 * It can do three different things
 * a) display connectives unchanged but toggle values for propositions so,
 * for example, F&T can be toggled to T&T  (we call this satisfiable)
 * b) leave the values for the propositions unchanged but toggle the values for the connectives
 * so, for example T&F can be toggled to TFF or TTF (we call this truth table)
 * 
 */



class TruthTableModel extends AbstractTableModel{
	static final int SATISFIABLE =0;
	static final int TRUTHTABLE =1;
	static final int MODAL =2;

    int fLength=0;
    Object [][] fData;
    String [] fHeader;
    String [] fAnswer;
    int fRowCount=2;
    TFormula fFormula;
    TFormula [] fAccessibleWorlds=new TFormula[0];  //values for the same formula in different worlds
    TParser fParser;
    ArrayList fInterpretation=null; //an Interpretation that satisfies it supplied by the theorem prover
    boolean fTogglingEnabled=true;

    TruthTableModel(){                         //temp
    	
    }
    
    TruthTableModel(TFormula theFormula,TParser aParser, int type){
    if (type==SATISFIABLE)
    	initForSatisfiable(theFormula,aParser);
    else 
    if (type==MODAL)
    	initForModal(theFormula, aParser);
    else
    	initForTT(theFormula, aParser);
    	
    }
    
void initForSatisfiable(TFormula theFormula,TParser aParser){
      fFormula=theFormula;
      fParser=aParser;



      String randomStr= fParser.writeFormulaToString(theFormula);

   initializeTableData(randomStr);

   fInterpretation = TTestNode.decidableFormulaSatisfiable(fParser,fFormula);
    }



void initializeTableData(String randomStr){
    /*we have the formula as a string, say "AvB", we want the header
     to be an array of strings {"A","v","B"}, and the table data to
     be a two element array of array of string {{"T","v","T"},{"","",""}}  notice that
     the propositions have been initialized to "F", eventually the answer will go in the second row*/

     fLength =randomStr.length();

     fHeader= new String[fLength]; // used also to determine which characters are connectives
     String [] randomArray = new String[fLength];
     String [] randomArray2 = new String[fLength];

     String currStr;
     char currChar;

     String alreadyAssigned="";
     String values="";

     for(int i=0;i<fLength;i++){
       currStr=randomStr.substring(i,i+1);
       currChar=randomStr.charAt(i);

       fHeader[i]=currStr;

       if (fParser.isConnective(currChar)||
        /*  // TCopiParser.isCopiConnective(currChar)||
           TCopiParser.isConnective(currChar)||
      //   TBergmannParser.isBergmannConnective(currChar)||
           TBergmannParser.isConnective(currChar)|| */


     /*      currChar==chNeg||
           currChar==chAnd||
           currChar==chOr||
           currChar==chImplic||
           currChar==chEquiv||  */
           currChar=='('||
           currChar==')')

          randomArray[i]=currStr;  //pass through unchanged
        else{

          /*now we try to assign randomly */

          int index = alreadyAssigned.indexOf(currChar);

          if (index != -1) {
            randomArray[i] = values.substring(index, index+1); // assign the prop the same value as before
          }
          else {

            double random = Math.random();

            if (random < 0.5)
              randomArray[i] = "T";
            else
              randomArray[i] = "F"; // initialize props randomly

            alreadyAssigned+=currChar;  // note what we have done
            values+=randomArray[i];
          }
        }
        randomArray2[i]="";          // set the 'answer' blank to start with
     }

   fData=  new Object[2][fLength];  //two rows

   fAnswer=toStringArray(answerStr());  //do we need this?

   fData[0]=randomArray;
   fData[1]=randomArray2;
}

void initForTT(TFormula theFormula, TParser aParser){
    fFormula=theFormula;
    fParser= aParser;
    
    
 String randomStr= fParser.writeFormulaToString(theFormula);


 /*we have the formula as a string, say "TvF", we want the header
 to be an array of strings {"T","v","F"}, and the table data to
 be a two element array of array of string {{"T","v","F"},{"T","T","F"}}  notice that
  the connective has become "T", the answer, in the second row*/

 fLength =randomStr.length();

 fHeader= new String[fLength]; // used also to determine which characters are connectives
 String [] randomArray = new String[fLength];
 String [] randomArray2 = new String[fLength];  //answer


 String currStr;
 for(int i=0;i<fLength;i++){
   currStr=randomStr.substring(i,i+1);
   fHeader[i]=currStr;
   randomArray[i]=currStr;
   randomArray2[i]="";          // set the 'answer' blank to start with
 }

fData=  new Object[2][fLength];  //two rows

//String answer=answerStr();
fAnswer=toStringArray(answerStr());

fData[0]=randomArray;
fData[1]=randomArray2;
  }

void initForModal(TFormula theFormula,TParser aParser){
    fFormula=theFormula;
    fParser= aParser;
    String randomStr;
    
 //   if (theFormula==null)
 //   	randomStr=random;
 //   else
    	randomStr= fParser.writeFormulaToString(theFormula);


 /*we have the formula as a string, say "TvF", we want the header
 to be an array of strings {"T","v","F"}, and the table data to
 be a two element array of array of string {{"T","v","F"},{"T","T","F"}}  notice that
  the connective has become "T", the answer, in the second row*/

 fLength =randomStr.length();
 
 //fLength+=1;  //to allow for world index

 fHeader= new String[fLength]; // used also to determine which characters are connectives
 String [] randomArray = new String[fLength];
 String [] randomArray2 = new String[fLength];  //answer


 String currStr;
 for(int i=0;i<fLength;i++){
   currStr=randomStr.substring(i,i+1);
   fHeader[i]=currStr;
   randomArray[i]=currStr;
   randomArray2[i]="";          // set the 'answer' blank to start with
 }
 
 //fHeader[0]="n";
 //randomArray[0]="n";

//fData=  new Object[2][fLength];  //two rows
 
 //fRowCount=4;
 fData=  new Object[2][fLength];  //four rows

fAnswer=toStringArray(answerStr());

fData[0]=randomArray;
fData[1]=randomArray2;

//fData[2]=randomArray;
//fData[3]=randomArray2;
  }

/**************************************************************************************/

public void setAccessibleWorlds(TFormula [] accessibleWorlds){
	 fAccessibleWorlds=accessibleWorlds;
	 
	 fAnswer=toStringArray(answerStr());  //update, change worlds changes answer
}

public boolean isSatisfiable(){
  return
      fInterpretation!=null;
}

String answerStr(){
      TFormula answerFormula=fFormula.copyFormula();
      surgeryForAnswer(answerFormula,fAccessibleWorlds);
      return
          fParser.writeFormulaToString(answerFormula);
}

public boolean satisfiableAnswerTrue(){   //if satisfiable and first row is true
      return
          rowATrueFormula(0);
    }

public boolean notSatisfiableAnswerTrue(){
  return
      !isSatisfiable();
    }

boolean equalStringArrays(String [] a,String[] b){
      if (a.length==b.length){
        for(int i=0;i<a.length;i++){
           if (!a[i].equals(b[i]))
               return
                 false;
        }
        return
            true;
     }
    return
          false;
}

boolean rowATrueFormula(int row){

  if (row<0||!(row<getRowCount()))
     return
        false;

  String formulaStr =fromStringArray((String[])fData[row]);

  TFormula root = new TFormula();
  StringReader aReader = new StringReader(formulaStr);
  ArrayList dummy = new ArrayList();
  boolean wellFormed = fParser.wffCheck(root, /*dummy,*/
             aReader);

  if (!wellFormed)
    return
        false;
  else
    return
        formulaTrue(root, fAccessibleWorlds);

}

    /*what we have is a string like "Fv(T^F)" and we want "FF(TFF).
     We can determine the truth value of the connectives, then use surgery to replace those connectives with T or F
     then use our ordinary write routines*/

private boolean formulaTrue(TFormula root, TFormula [] otherWorlds){
      if (root==null)                 //should never happen
       return
           false;

      if (otherWorlds==null)
      	otherWorlds= new TFormula[0];

      switch (root.fKind){

        case TFormula.predicator:
          if (root.getInfo().equals("T"))
             return
               true;
          else{
            if (root.getInfo().equals("F"))
              return
                  false;
            else
              return
                  false;
          }
        case TFormula.unary:
        	TFormula [] reducedOthers = new TFormula[otherWorlds.length];
        	for (int i=0;i<otherWorlds.length;i++){
        		reducedOthers[i]= otherWorlds[i].fRLink;
        	}
        	
        	if (fParser.isNegation(root))
        	   return
        		!formulaTrue(root.fRLink, reducedOthers);
        	
        	if (fParser.isModalPossible(root)){
        		boolean isTrue=false;   // notice here, if there are no other (accessible) worlds, possible is false
            	for (int i=0;i<otherWorlds.length&&!isTrue;i++){
            		isTrue=formulaTrue(otherWorlds[i].fRLink, reducedOthers);
            	}
         	   return
         		isTrue;
        	}
        	if (fParser.isModalNecessary(root)){
        		boolean isFalse=false; // notice here, if there are no other (accessible) worlds, necessary is true
            	for (int i=0;i<otherWorlds.length&&!isFalse;i++){
            		isFalse=!formulaTrue(otherWorlds[i].fRLink, reducedOthers);
            	}
         	   return
         		!isFalse;
        	}

        case TFormula.binary:
        	TFormula [] reducedLeft = new TFormula[otherWorlds.length];
        	for (int i=0;i<otherWorlds.length;i++){
        		reducedLeft[i]= otherWorlds[i].fLLink;
        	}
        	TFormula [] reducedRight = new TFormula[otherWorlds.length];
        	for (int i=0;i<otherWorlds.length;i++){
        		reducedRight[i]= otherWorlds[i].fRLink;
        	}
         	
        	
          if (fParser.isAnd(root))
            return
                (formulaTrue(root.fLLink,reducedLeft)&&formulaTrue(root.fRLink,reducedRight));

          if (fParser.isOr(root))
             return
              (formulaTrue(root.fLLink,reducedLeft)||formulaTrue(root.fRLink,reducedRight));

          if (fParser.isImplic(root))
             return
              ((!formulaTrue(root.fLLink,reducedLeft))||formulaTrue(root.fRLink,reducedRight));

           if (fParser.isEquiv(root))
             return
              (((!formulaTrue(root.fLLink,reducedLeft))||formulaTrue(root.fRLink,reducedRight))&&
               (formulaTrue(root.fLLink,reducedLeft)||(!formulaTrue(root.fRLink,reducedRight))));
         default:
          ;
      }

      return
          false;
}

///////////////  getters and setters ///////////////////////


   public int getColumnCount(){
        return
            fLength;}
   public String getColumnName(int col) {
                return fHeader[col];}
public String[] getHeader(){
                 return
                     fHeader;
}
   public int getRowCount(){
                   return fRowCount;}

   public Object getValueAt(int row, int column){
        return
            fData[row][column];}

    /*
    public boolean isCellEditable(int row, int col) {

      if (row < 1)
        return true;
      else
        return false;
    }  */


    public void setValueAt(Object value, int row, int col) {


                fData[row][col] =  value;
                fireTableCellUpdated(row, col);
        }


 ///////////////   end of getters and setters ////////





public void showAnswer(){



  if (fInterpretation!=null){ //should not be null if this is called, and it will be a list like A,~B, etc
    String trueProps= TFormula.trueAtomicFormulasInList(fInterpretation);
    String falseProps= TFormula.falseAtomicFormulasInList(fInterpretation);

    if (trueProps!=null)
      for(int i=0;i<trueProps.length();i++){
        setAllOccurences(1,trueProps.charAt(i),'T');
      }
    if (falseProps!=null)
        for(int i=0;i<falseProps.length();i++){
          setAllOccurences(1,falseProps.charAt(i),'F');
        }


  }

//   fData[1]=fAnswer;        //we probably don't need the fAnswer field

   fireTableChanged(new TableModelEvent(this,1));

   //fireTableStructureChanged();
}

void surgeryForAnswer(TFormula answerFormula,TFormula [] otherWorlds){
  short kind=answerFormula.getKind();

  if ((kind==TFormula.unary)||(kind==TFormula.binary)){  //don't bother with atomic
	  
	if (otherWorlds==null)
		otherWorlds=new TFormula[0];

    if (formulaTrue(answerFormula,otherWorlds))
      answerFormula.setInfo("T");
    else
      answerFormula.setInfo("F");

  if (answerFormula.getLLink()!=null){
     	TFormula [] reducedLeft = new TFormula[otherWorlds.length];
    	for (int i=0;i<otherWorlds.length;i++){
    		reducedLeft[i]= otherWorlds[i].fLLink;
    	}	  
    surgeryForAnswer(answerFormula.getLLink(), reducedLeft);
    }
  
  

  if (answerFormula.getRLink()!=null){
		TFormula [] reducedRight = new TFormula[otherWorlds.length];
		for (int i=0;i<otherWorlds.length;i++){
			reducedRight[i]= otherWorlds[i].fRLink;
		}
    surgeryForAnswer(answerFormula.getRLink(), reducedRight);
  }

  }
}


void toggleAll(char proposition){  //we only change row 0

   char label;

  for (int i=0;i<getColumnCount();i++){
    String template=(String)fHeader[i];

    if (template==null||template.length()<1)
    break;                                 //should never happen

    label=template.charAt(0);

    if (label==proposition){
      if (((String)fData[0][i]).equals("T"))
         setValueAt("F",0,i);
      else                      //when it first comes in it will be a connective, but then goes t/f
         setValueAt("T",0,i);
    }

  }
}

void setAllOccurences(int row, char proposition,char value){  //we only change row 0

   char label;

  for (int i=0;i<getColumnCount();i++){
    String template=(String)fHeader[i];

    if (template==null||template.length()<1)
    break;                                 //should never happen

    label=template.charAt(0);

    if (label==proposition){
       setValueAt(String.valueOf(value),row,i);
    }

  }
}

void setTogglingEnabled(boolean value){
  fTogglingEnabled=value;
}

public void toggle (int row, int col){
  /*we only toggle row 0, from "T" to "F" and back, but only for propositions
   we also have to remain in synch ie if A is set to T in one instance, it has to
   be set for all instances*/

 if (fTogglingEnabled){

   String template = (String) fHeader[col]; // remains unchanged

   if (template == null || template.length() < 1)
     return; //should never happen

   char dataChar = template.charAt(0);

   if (dataChar == chNeg ||
       dataChar == chAnd ||
       dataChar == chOr ||
       dataChar == chImplic ||
       dataChar == chEquiv ||
       dataChar == chModalPossible ||
       dataChar == chModalNecessary||
       dataChar == '(' ||
       dataChar == ')')
     return; //do nothing, because it is a connective

   toggleAll(dataChar); //change all of them
 }
}

String [] toStringArray(String aString){
  int length =aString.length();

   String [] anArray = new String[length];

 for(int i=0;i<length;i++){
   anArray[i]=aString.substring(i,i+1);

 }

 return
     anArray;
}

String fromStringArray(String[] aStringArray){
    int length =aStringArray.length;

    String answer="";

   for(int i=0;i<length;i++){
     answer+=aStringArray[i];
   }
   return
       answer;
}

/****************  The next is for the plain truth table with toggling connectives *********************/

public boolean tTAnswerTrue(){
    return
        equalStringArrays(fAnswer,(String[])fData[0]);
  }

public void showTTAnswer(){
	   fData[1]=fAnswer;        //we probably don't need the fAnswer field

	   fireTableChanged(new TableModelEvent(TruthTableModel.this,1));

	   //fireTableStructureChanged();
	}


void setTTTogglingEnabled(boolean value){
	  fTogglingEnabled = value;
	}


	public void tTToggle (int row, int col){
	  /*we only toggle row 0, from "T" to "F" and back, but only for connectives */

	  if (fTogglingEnabled){

	    String template = (String) fHeader[col]; //1 remains unchanged

	    if (template.equals("T") ||
	        template.equals("F") ||
	        template.equals("(") ||
	        template.equals(")"))
	      return; //do nothing, because it is not a connective

	    if ( ( (String) fData[0][col]).equals("T"))
	      setValueAt("F", 0, col);
	    else //when it first comes in it will be a connective, but then goes t/f
	      setValueAt("T", 0, col);
	  }
	}

/*	String [] toStringArray(String aString){
	  int length =aString.length();

	   String [] anArray = new String[length];

	 for(int i=0;i<length;i++){
	   anArray[i]=aString.substring(i,i+1);

	 }

	 return
	     anArray;
	} */

/*
boolean equalStringArrays(String [] a,String[] b){
    if (a.length==b.length){
      for(int i=0;i<a.length;i++){
         if (!a[i].equals(b[i]))
             return
               false;
      }
      return
          true;
   }
  return
        false;
}
*/
/*what we have is a string like "Fv(T^F)" and we want "FF(TFF).
We can determine the truth value of the connectives, then use surgery to replace those connectives with T or F
then use our ordinary write routines*/

/*
private boolean formulaTrue(TFormula root){
 if (root==null)                 //should never happen
  return
      false;


 switch (root.fKind){

   case TFormula.predicator:
     if (root.getInfo().equals("T"))
        return
          true;
     else
       return
           false;

   case TFormula.unary:
     return
         !formulaTrue(root.fRLink);

   case TFormula.binary:
     if (fParser.isAnd(root))
       return
           (formulaTrue(root.fLLink)&&formulaTrue(root.fRLink));

     if (fParser.isOr(root))
        return
         (formulaTrue(root.fLLink)||formulaTrue(root.fRLink));

     if (fParser.isImplic(root))
        return
         ((!formulaTrue(root.fLLink))||formulaTrue(root.fRLink));

      if (fParser.isEquiv(root))
        return
         (((!formulaTrue(root.fLLink))||formulaTrue(root.fRLink))&&
          (formulaTrue(root.fLLink)||(!formulaTrue(root.fRLink))));
    default:
     ;
 }

 return
     false;
}

*/

/****************  End of plain truth table with toggling connectives *********************/





  }