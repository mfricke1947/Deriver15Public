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

import java.io.StringReader;

import jscheme.InputPort;
import jscheme.Scheme;

/*a global Scheme for general use */


public class TScheme {
  static public Scheme fScheme  = new Scheme(null);


  static public Object globalLispEvaluate(String inputStr){
     try {
                   InputPort input = new InputPort(new StringReader(inputStr));

                   {
                     Object x;
                     Object result;
                     String resultStr;
                     if (input.isEOF(x = input.read()))
                       return
                           null;

                       result = fScheme.eval(x);
                  return
                      result;
                   }
                 }
               catch (Exception ex) {

             //    writeToJournal("Scheme Exception: " + ex, true, false);

                 System.err.println("Scheme Exception: " + ex);
                }

                return
                    null;

       }
}
