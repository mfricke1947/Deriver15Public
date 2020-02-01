/*
Copyright (C) 2014 Martin Frické (mfricke@u.arizona.edu http://softoption.us mfricke@softoption.us)

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

package us.softoption.infrastructure;

import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

// 5/6/18 altered/introduced Combinatory Logic filter
// 11/1/08

// 1/18/20 encryption put back in from 2009 just to get
// other files to compile

public class TUtilities{

  public final static int noFilter=0;
  public final static int defaultFilter=1;
  public final static int logicFilter=2;
  public final static int lispFilter=3;
  public final static int peculiarFilter=4;

/************** List processing ********************************************/

static public Object nextInList(ArrayList list,FunctionalParameter test, int index){

 if ((list==null)||list.size()<=index)
   return
       null;

 int length=list.size();
 boolean found=false;
 int i=index;
 Object search;

 while(!found&&(i<length)){

    search=list.get(i);

    if (test.testIt(search))
       return
           search;
  else
     i+=1;

 }
 return
    null;
}


/***************Compression *******************************************************/


 static public String compressXML(String inputStr){  // watch out for regex syntax and the need for escape characters
   String outputStr=inputStr;

   /*This is simple 1-1 replacement by text that will not appear in the 'real' text*/

   outputStr=outputStr.replaceAll("<boolean>","<b>");
   outputStr=outputStr.replaceAll("</boolean>","</b>");

   outputStr=outputStr.replaceAll("<char>","<c>");
   outputStr=outputStr.replaceAll("</char>","</c>");

   outputStr=outputStr.replaceAll("<int>","<i>");
   outputStr=outputStr.replaceAll("</int>","</i>");

   outputStr=outputStr.replaceAll("</object>","</o>");
   outputStr=outputStr.replaceAll("<object class=\"java.util.ArrayList\">","<oca>");
   outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TInterpretationBoard\">","<ocb>");
   outputStr=outputStr.replaceAll("<object class=\"java.awt.Color\">","<occ>");
   outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TIndividual\">","<oci>");
   outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TProperty\">","<ocp>");
   outputStr=outputStr.replaceAll("<object class=\"java.awt.Point\">","<ocpo>");
   outputStr=outputStr.replaceAll("<object class=\"java.awt.Rectangle\">","<ocr>");
   outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TRelation\">","<ocre>");
   outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TFunction\">","<ocrf>");
   outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TIdentity\">","<ocri>");
   //outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TSemantics\"/>","<ocs/>");
   //Jan 09 don't like escaped character. The reader does not like it but the creator needs it.
   outputStr=outputStr.replaceAll("<object class=\"us.softoption.interpretation.TSemantics\"/>","<ocs>");


   outputStr=outputStr.replaceAll("</void>","</v>");
   outputStr=outputStr.replaceAll("<void method=\"add\">","<vma>");
   outputStr=outputStr.replaceAll("<void property=\"boundsRect\">","<vpbr>");
   outputStr=outputStr.replaceAll("<void property=\"color\">","<vpc>");
   outputStr=outputStr.replaceAll("<void property=\"from\">","<vpf>");
   outputStr=outputStr.replaceAll("<void property=\"name\">","<vpn>");
   outputStr=outputStr.replaceAll("<void property=\"selected\">","<vps>");
   outputStr=outputStr.replaceAll("<void property=\"semantics\">","<vpss>");
   outputStr=outputStr.replaceAll("<void property=\"to\">","<vpt>");
   outputStr=outputStr.replaceAll("<void property=\"XCoord\">","<vpx>");
   outputStr=outputStr.replaceAll("<void property=\"YCoord\">","<vpy>");


  return
      outputStr;
}

static public String expandXML(String inputStr){   // watch out for regex syntax and the need for escape characters
  String outputStr=inputStr;

   /*This is simple 1-1 replacement by text that will not appear in the 'real' text*/

   outputStr=outputStr.replaceAll("<b>","<boolean>");
   outputStr=outputStr.replaceAll("</b>","</boolean>");

   outputStr=outputStr.replaceAll("<c>","<char>");
   outputStr=outputStr.replaceAll("</c>","</char>");

   outputStr=outputStr.replaceAll("<i>","<int>");
   outputStr=outputStr.replaceAll("</i>","</int>");


   outputStr=outputStr.replaceAll("</o>","</object>");
   outputStr=outputStr.replaceAll("<oca>","<object class=\"java.util.ArrayList\">");
   outputStr=outputStr.replaceAll("<ocb>","<object class=\"us.softoption.interpretation.TInterpretationBoard\">");
   outputStr=outputStr.replaceAll("<occ>","<object class=\"java.awt.Color\">");
   outputStr=outputStr.replaceAll("<oci>","<object class=\"us.softoption.interpretation.TIndividual\">");
   outputStr=outputStr.replaceAll("<ocp>","<object class=\"us.softoption.interpretation.TProperty\">");
   outputStr=outputStr.replaceAll("<ocpo>","<object class=\"java.awt.Point\">");
   outputStr=outputStr.replaceAll("<ocr>","<object class=\"java.awt.Rectangle\">");
   outputStr=outputStr.replaceAll("<ocre>","<object class=\"us.softoption.interpretation.TRelation\">");
   outputStr=outputStr.replaceAll("<ocrf>","<object class=\"us.softoption.interpretation.TFunction\">");
   outputStr=outputStr.replaceAll("<ocri>","<object class=\"us.softoption.interpretation.TIdentity\">");  
   //outputStr=outputStr.replaceAll("<ocs/>","<object class=\"us.softoption.interpretation.TSemantics\"/>");
   //Jan 09 don't like escaped character BUT the XML object creator need it on the long version
   outputStr=outputStr.replaceAll("<ocs>","<object class=\"us.softoption.interpretation.TSemantics\"/>");

   outputStr=outputStr.replaceAll("</v>","</void>");
   outputStr=outputStr.replaceAll("<vma>","<void method=\"add\">");
   outputStr=outputStr.replaceAll("<vpbr>","<void property=\"boundsRect\">");
   outputStr=outputStr.replaceAll("<vpc>","<void property=\"color\">");
   outputStr=outputStr.replaceAll("<vpf>","<void property=\"from\">");
   outputStr=outputStr.replaceAll("<vpn>","<void property=\"name\">");
   outputStr=outputStr.replaceAll("<vps>","<void property=\"selected\">");
   outputStr=outputStr.replaceAll("<vpss>","<void property=\"semantics\">");
   outputStr=outputStr.replaceAll("<vpt>","<void property=\"to\">");
   outputStr=outputStr.replaceAll("<vpx>","<void property=\"XCoord\">");
   outputStr=outputStr.replaceAll("<vpy>","<void property=\"YCoord\">");

  return
      outputStr;
}

/***************String Processing *******************************************************/

//5/6/18 altered/introduced Combinatory Logic filter

static public String combinatoryLogicFilter(String inputStr){
	
	/*
	 * 	<combex> ::= <atom> | <application> | (<combex>)
		<atom> ::= <variable>|<constant>|<combinator>
		<application> ::= <combex> <combex>
	 * 
	 * 
	 */
	
	/*
	 *  The problem here is that a space both is a general separator and means application.
	 *  So a b c d is a series of applications, so is a b (c d). But a b (c d ) is a problem
	 *  because the User has just typed a space for a nice layout, but the parser will
	 *  read the space after the d as the start of an application and the ) that it
	 *  encounters will make the expression ill formed.
	 *  
	 *  We want to remove repeat blanks, then blank) and (blank .
	
	*/
	
	
String outputStr;
    
    outputStr=inputStr.trim(); // removes leading and trailing white
    outputStr=outputStr.replaceAll("\u00A0",""); // removes html/unicode non breaking space?
    outputStr=outputStr.replaceAll("\\s+"," ");   // replace repeats with single blank
    outputStr=outputStr.replaceAll(" \\)",")");   // replace blank) with )
    outputStr=outputStr.replaceAll("\\( ","(");   // replace blank( with (

    return
        outputStr;
}

static public String defaultFilter(String inputStr){  //ie standard filter
    String outputStr;

    outputStr=inputStr.replaceAll("\\s",strNull); // removes ascii whitespace?
    outputStr=outputStr.replaceAll("\u00A0",strNull); // removes html/unicode non breaking space?

    return
        outputStr;
  }

static public String noFilter(String inputStr){  //ie standard filter
        String outputStr;

        outputStr=inputStr;

        return
            outputStr;
  }


static public String lambdaFilter(String inputStr){  //ie standard filter
        String outputStr;
        
        outputStr=inputStr.trim(); // removes leading and trailling white

  //      outputStr=outputStr.replaceAll("\\s",strAt); // removes ascii whitespace?
 // don't put @ in parser prefers space
        
        
        outputStr=outputStr.replaceAll("\u00A0",strNull); // removes html/unicode non breaking space?

        return
            outputStr;
  }


static public String logicFilter(String inputStr){
    String outputStr;

 // Replace line breaks with space
    outputStr=inputStr.replaceAll("\r",strNull);
    // Replace newline
    outputStr=inputStr.replaceAll("\n",strNull);
    
    
    outputStr=inputStr.replaceAll("\\s",strNull); // removes ascii whitespace?
    outputStr=outputStr.replaceAll("\u00A0",strNull); // removes html/unicode non breaking space?

    return
        outputStr;
  }

/*The next is used sometimes when reading a parameter from a webpage and feeding to applet */
static public String htmlEscToUnicodeFilter(String inputStr){
        String outputStr;

        outputStr=inputStr.replaceAll("&#9672;","\u25C8"); // modal possible
        outputStr=outputStr.replaceAll("&#9108;","\u2394"); // modal necessary

        outputStr=outputStr.replaceAll("&not;",""+'\u223C'); // not
        outputStr=outputStr.replaceAll("&sim;",""+'\u223C'); // not
        
        outputStr=outputStr.replaceAll("&#8743;",""+'\u2227'); // and
        outputStr=outputStr.replaceAll("&and;",""+'\u2227'); // and
        outputStr=outputStr.replaceAll("&amp;",""+'\u2227'); // and
        
        outputStr=outputStr.replaceAll("&or;",""+'\u2228'); // or
        
        outputStr=outputStr.replaceAll("&sup;",""+'\u2283'); //implic
        outputStr=outputStr.replaceAll("&rarr;",""+'\u2283'); //implic
        
        outputStr=outputStr.replaceAll("&harr;",""+'\u2261'); // equiv
        
        outputStr=outputStr.replaceAll("&exist;",""+'\u2203'); // exist
        outputStr=outputStr.replaceAll("&sum;",""+'\u2203'); // exist
        
        outputStr=outputStr.replaceAll("&forall;",""+'\u2200'); // all
        outputStr=outputStr.replaceAll("&prod;",""+'\u2200'); // all
        
        outputStr=outputStr.replaceAll("&there4;",""+'\u2234'); // therefore
        
       
        return
            outputStr;
  }


  static public String noReturnsFilter(String inputStr){
    String outputStr;

    outputStr=inputStr.replaceAll("[" + strCR+"]",""); // removes returns

    return
        outputStr;
}

static public String lispFilter(String inputStr){
String outputStr;

outputStr=inputStr.replaceAll("[" + strCR+"]"," "); // changes a return to a blank which is a separator

return
    outputStr;
}
static public String innerListOutputToUpperCase(String inputStr){  // removes enclosing brackets and upper cases
inputStr=inputStr.substring(1,(inputStr.length()-1));

return
    inputStr.toUpperCase();
}


static public String peculiarFilter(String inputStr){
 String outputStr;

 outputStr=inputStr.toLowerCase();
 outputStr=outputStr.replaceAll("[^()a-z]"," ");   // we want just lower case, brackets, and blanks

return
     outputStr;
}


   static  public String readStringToString(String input, int filter){

       String outputStr=input;

       if (input==null)
           return null;

       switch (filter){
         case noFilter:
           break;
         case defaultFilter:
           outputStr=defaultFilter(input);
           break;
         case logicFilter:
           outputStr=logicFilter(input);
           break;
         case lispFilter:
           outputStr=lispFilter(input);
           break;


         case peculiarFilter:
           outputStr=peculiarFilter(input);
           break;

         default:;
       }

       return
           outputStr;
     }

static public String removeDuplicateChars(String inputStr){
      if (inputStr == null)
        return
            null;
      else {
        StringBuffer output = new StringBuffer(inputStr);
        int length = inputStr.length();

        if (length>1){
          for (int i = length-1; i >= 0; i--) {
            char ch = inputStr.charAt(i);
            if (inputStr.indexOf(ch) != i)
              output.deleteCharAt(i);
          }
        }
        return
            output.toString();
      }
    }


static public String removeDuplicatePairsOfChars(String inputStr){
/* this is expecting an even no of chars, eg abacab
    it views these as pairs and removes duplicates eg leaving abac */


 if (inputStr == null)
    return
      null;
 else {
    StringBuffer output = new StringBuffer(inputStr);
    int length = inputStr.length();

    if ((length % 2) ==0)  //even

       if (length>2){
          for (int i = length-2; i > 1; i-=2) {
            String pair = inputStr.substring(i,i+2);

            if (inputStr.indexOf(pair)!=i)  { // earlier occurrence
              output.deleteCharAt(i+1);
              output.deleteCharAt(i);
            }

          }
        }
        return
       output.toString();
            }
          }


static public String sortPairs (String inputStr){
/*if you have a string of say acabbd and this is conceived of as ordered pairs
eg ac ab bd then this sorts them to abacbd  (bizarre). It also removes
duplicates*/

String indexStr= "abcdefghijklmnopqrstuvwxyz";
String outputStr="";

/*this is not a brilliant algorithm, but we are going to look through all
   pairs aa ab ac etc. in order. If they are in the inputStr and coincide with
   the pair boundaries we'll take them */

for (int i=0;i<26;i++){
 for (int j=0;j<26;j++){
   String pair = indexStr.substring(i,i+1)+indexStr.charAt(j);

   int pos=inputStr.indexOf(pair);

   boolean found=false;

   while (pos>-1 && ! found){
     if (pos%2==0){           // coincide with ordered pair boundary
       found=true;
       outputStr=outputStr+pair;
     }
     else
      pos=inputStr.indexOf(pair,pos+1);  // look further along
   }

 }
}

return
  outputStr;

}


static public String separateStringWithCommas(String inputStr){
     StringBuffer tempBuffer = new StringBuffer("");

        for (int i=0;i<inputStr.length();i++){
          tempBuffer.append(inputStr.charAt(i));
          if (i<(inputStr.length()-1))
             tempBuffer.append(",");
        }
        return
            tempBuffer.toString();

  }

static public String addSpaceToInnerParantheses(String inputStr){
    StringBuffer tempBuffer = new StringBuffer("");
    
    int firstIndex=inputStr.indexOf('(');
    int lastIndex=inputStr.lastIndexOf(')');
    

       for (int i=0;i<inputStr.length();i++){
 
    	   if ((inputStr.charAt(i)=='(')&&(i!=firstIndex))
    		   tempBuffer.append(" (");
    	   else
    	   if ((inputStr.charAt(i)==')')&&(i!=lastIndex))
    		   tempBuffer.append(") ");
    	   else   	   
    	   tempBuffer.append(inputStr.charAt(i));
       }
          
       return
           tempBuffer.toString();

 }

static public String intoOrderedPairs(String inputStr){
       StringBuffer tempBuffer = new StringBuffer("");

       int i=0;

          while (i<inputStr.length()){    //expecting pairs

            if (i>1)
               tempBuffer.append(",");

            tempBuffer.append("<");
            tempBuffer.append(inputStr.charAt(i));
            i+=1;
            tempBuffer.append(inputStr.charAt(i));
            tempBuffer.append(">");
            i+=1;
          }
          return
              tempBuffer.toString();
    }


static public String stringDifference(String inputStr, String notThese){
    String outputStr = inputStr;
    String replaceStr;

  for (int i=0;i<notThese.length();i++){
    replaceStr = notThese.substring(i, i + 1);

    outputStr = outputStr.replaceAll(replaceStr, strNull);
  }

         return
             outputStr;
       }


public static String toLines(String inputStr, int lineLength){
       StringBuffer b = new StringBuffer(inputStr);

      // int numInserts=0;

       int bufferLength=b.length();

       for (int i=lineLength-1;i<bufferLength;i+=lineLength){
         b.insert(i,strCR);
      //   numInserts+=1;
       }

       return
             b.toString();
}

/* think this is not used
private String StripHTML(String source)
{
	return "";}

*/







/***************Encryption  *******************************************************/

static final String defaultKey = "EncryptDeriver"; // The key for 'encrypting' and 'decrypting'.


/***************Encryption This has been omitted since 2009
 * but just put back now to get the rest to compile  *******************************************************/

 // The key for 'encrypting' and 'decrypting'.

//xOR Encrypt not working with logical symbols

public static String xOrEncrypt(String str){
  return
     xOrEncrypt(str,defaultKey);
}


public static String xOrEncrypt(String str,String key)
          {

            byte [] strBytes;
            try{strBytes=str.getBytes("UTF-8");}
            catch(UnsupportedEncodingException e){strBytes=null;
               return
                 "";};

            byte [] inKey;
            try{inKey=key.getBytes("UTF-8");}
            catch(UnsupportedEncodingException e){inKey=null;
              return
                "";};

            int lenStr = strBytes.length;
            int lenKey = inKey.length;

            byte [] outBytes= new byte[lenStr];

            for ( int i = 0, j = 0; i < lenStr; i++, j++ )
             {
                if ( j >= lenKey ) j = 0;  // Wrap 'round to beginning of key string.

                //
                // XOR the chars together. Must cast back to char to avoid compile error.
                //
        //        sb.setCharAt(i, (char)(str.charAt(i) ^ key.charAt(j)));

               /* outBytes[i]=*/strBytes[i]^=inKey[j];
             }

             String outStr="";

             try{outStr=new String(strBytes,"UTF-8");}
             catch(UnsupportedEncodingException e){};


            return
                outStr;
          }




static public String urlEncode(String inputStr){
  String outputStr="";
  try{outputStr=URLEncoder.encode(inputStr,"UTF-8");}
             catch(UnsupportedEncodingException e){};
  return
      outputStr;
}

static public String urlDecode(String inputStr){
  String outputStr="";
  try{outputStr=URLDecoder.decode(inputStr,"UTF-8");}
             catch(UnsupportedEncodingException e){};
  return
      outputStr;
}


/*The problem here is that we need to code text and logic symbols in such a way that the
 result is fully visible and cut and pastable. If we url encode only, it deals with the logic
 but leaves plain text unchanged. But then if we XOR it, it encodes the lot, but some might
 not be visible, so we url encode again*/
static public String generalEncode(String inputStr){

  return
      urlEncode(xOrEncrypt(urlEncode(inputStr)));
}

static public String generalDecode(String inputStr){

  if (inputStr==null||inputStr.length()==0)
   return
       inputStr;
  else{
     if (inputStr.charAt(0)=='[')             //string may or may not have the enclosing brackets
        inputStr=inputStr.substring(1);

     if (inputStr.length()>0&&inputStr.charAt(inputStr.length()-1)==']')
        inputStr=inputStr.substring(0,inputStr.length()-1);
     return
       urlDecode(xOrEncrypt(urlDecode(inputStr)));
   }
}








}









