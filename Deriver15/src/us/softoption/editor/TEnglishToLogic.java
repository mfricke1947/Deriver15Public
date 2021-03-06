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

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strCR;

import java.io.StringReader;

import jscheme.InputPort;
import jscheme.Scheme;
import jscheme.SchemeUtils;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

//import sisc.interpreter.*;




/* Most of the program is written in LISP (in Scheme, actually). Then there is a LISP
interpreter running within this program.

All the LISP programs and functions are introduced via Strings which are evaluated on
initialization. (We did not want to use external files. The User (or System) can
change some of these via preferences).

These Strings themselves are produced by using BBEdit to remove the line returns from the
LISP, then put in one definition per line.

Then LISP expressions are evaluated in the appropriate context.

Don't forget... We use special unicode symbols for the logic symbols, these need to be inserted
into the Strings that are lisp input.

We have written this only for propositions at present (notice for predicates the Pascal uses internal parsing
eg sentenceparse to get sentence-p

Notice also that the lisp originals tend to have more brackets in the symbolized outputs eg

(arthur studies) & (beryl thinks)

rather than

arthur studies & beryl thinks

we just use less brackets in the THEN part of the rules (check the file gRules, which is resources from the
old deriver).


*/

/* There are actually two Schemes jscheme and sisc, and I have been experimenting with both. At present, here, Aug 06,
 I am not using sisc */


/*m
 * 
 Note default is a subclass of gentzen
 */

public class TEnglishToLogic{


  Scheme fScheme  = new Scheme(null);
 // Interpreter fSISCScheme=null;



  /*The lisp context is a Scheme program written in a text file. The we use
  BBEdit to remove the returns and paste it in here as a string. Then the LISP
unit will evaluate it.*/


  String fLispContext=" ";

  String fUtilities="(begin"

+"(define (accumulate list-items) (if (null? list-items) empty (append (car list-items) (accumulate (cdr list-items)) ) ))"

+"(define (butlast alist n) (if (>= n (length alist)) empty (cons (car alist) (butlast (cdr alist) n)) ))"

+"(define (dotimes count limit body result) (if (= count limit) result (begin body (dotimes (+ count 1) limit body result)) ))"

+"(define (singleton-last aList) (if (null? aList) empty (if (null? (cdr aList)) aList (singleton-last (cdr aList)))))"

+"(define (myassoc key-list assoc-list) (if (null? assoc-list) #f (if (equal? key-list (caar assoc-list)) (cadar assoc-list) (myassoc key-list (cdr assoc-list)) ) ) )"

+"(define (rassoc value assoc-list)(if (null? assoc-list) #f (if  (equal? value (cadar assoc-list))(caar assoc-list)(rassoc value (cdr assoc-list)))))"

+"(define (nthcdr n thelist) (if (null? thelist) empty (if (= n 0) thelist (nthcdr (- n 1)(cdr thelist)) ) ) ))";

  String fRuleAccessors = "(begin (define (correct-matching-function key) (case key ((total-match) (eval total-match)) ((left-match) (eval left-match)) ((middle-match) (eval middle-match)) ((left-then-middle-match) (eval left-literal-then-middle-match)) (else (eval general-match)) ) ) (define (rule-matching-function-key rule) (car rule)) (define (rule-matching-function rule) (correct-matching-function (rule-matching-function-key rule))) (define (rule-if rule) (cadr rule)) (define (rule-then rule) (caddr rule)) )";


public static String fPropRules="(define gPropRules '("

	+"( left-match (((? _) (It is not the case that)) ((? Sentence) (lambda (s) (proposition-p s))) ) ("+ chNeg+" ((? Sentence))))"

	+"( left-then-middle-match (((? _) (both)) ((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (and)) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chAnd+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (and) ) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chAnd+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (unless) ) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chOr+" (? Sentence2))))"

	+"( left-then-middle-match (((? _) (either)) ((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (or)) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chOr+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (or) ) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chOr+" (? Sentence2))))"

	+"( left-then-middle-match (((? _) (neither)) ((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (nor)) ((? Sentence2) (lambda (s) (proposition-p s))) ) ("+ chNeg+"((? Sentence1) "+ chOr+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (if) ) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence2) "+ chImplic+" (? Sentence1))))"

	+"( middle-match (((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (only if) ) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chImplic+" (? Sentence2))))"

	+"( left-then-middle-match (((? _) (if)) ((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (then)) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chImplic+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (if and only if) ) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ chEquiv+" (? Sentence2))))"

	+"))";

  
public static String fRules="(define gRules '("

	+"( left-match (((? _) (It is not the case that)) ((? Sentence) (lambda (s) (sentence-p s))) ) "
	      +"("+chNeg+" ((? Sentence))))"

	+"( left-then-middle-match (((? _) (both)) ((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (and)) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence1) "+ chAnd+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (and) ) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence1) "+ chAnd+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (unless) ) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence1)"+ chOr+"(? Sentence2))))"

	+"( left-then-middle-match (((? _) (either)) ((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (or)) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence1)"+ chOr+"(? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (or) ) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence1)"+ chOr+"(? Sentence2))))"

	+"( left-then-middle-match (((? _) (neither)) ((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (nor)) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"("+ chNeg+"((? Sentence1)"+ chOr+"(? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (if) ) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence2) "+ chImplic+" (? Sentence1))))"

	+"( middle-match (((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (only if) ) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence1) "+ chImplic+" (? Sentence2))))"

	+"( left-then-middle-match (((? _) (if)) ((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (then)) ((? Sentence2) (lambda (s) (sentence-p s))) )"
	      +" (((? Sentence1) "+ chImplic+" (? Sentence2))))"

	+"( middle-match (((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (if and only if) ) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
	      +"(((? Sentence1) "+ chEquiv+" (? Sentence2))))"
	//3.1
	+"(middle-match( ((? Term) (lambda (s) (term-p s)))((? _) (is not) )((? BP) (lambda (s) (bp-p s))))"
	      +"("+ chNeg+" ( (? Term) is (? BP))))"

	+"(middle-match( ((? Term) (lambda (s) (term-p s)))((? _) (does not) )((? DP) (lambda (s) (dp-p s))))"
	      +"("+ chNeg+" ( (? Term) (? DP))))"
	//5.1
	+"(general-match( ((? Term) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is a) s )))((? NP) (lambda (s) (np-p s)))((? _) (lambda (s) (equal? '(that) s )))((? Subj) (lambda (s) (subject-p s)))((? RV) (lambda (s) (rv-p s))))"
	      +"( ((? Term) is a (? NP) "+ chAnd+" (? Subj) (? RV) (? Term))))"
	//6.1
	+"(general-match( ((? Term) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is a) s )))((? Adj) (lambda (s) (adj-p s)))((? Noun) (lambda (s) (adjnoun-p s))))"
	      +"( ((? Term) is (? Adj) "+ chAnd+" (? Term) is a (? Noun))))"
	//7/1
	+"(general-match ( ((? Term) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is a) s )))((? NP) (lambda (s) (np-p s)))((? _) (lambda (s) (equal? '(that) s )))((? VP) (lambda (s) (vp-p s))))"
	      +"( ((? Term) is a (? NP) "+ chAnd+" (? Term) (? VP))))"

	+"(left-match (((? _) (Everything)) ((? VP) (lambda (s) (vp-p s))))"
	      +"(("+ chUniquant+"x) (x  (? VP))))"

	+"(left-match(((? _) (Something)) ((? VP) (lambda (s) (vp-p s))))"
	      +"( ("+ chExiquant+"x) (x  (? VP))))"

	+"(left-match (((? _) (Nothing)) ((? VP) (lambda (s) (vp-p s))))"
	      +"("+ chNeg+"("+ chExiquant+"x) (x  (? VP))))"

	+"(general-match ( ((? _) (lambda (s) (equal? '(Everything that) s )))((? VP1) (lambda (s) (vp-p s)))((? VP2) (lambda (s) (vp-p s))))"
	      +"( ("+ chUniquant+"x) (x  (? VP1) "+ chImplic+" x  (? VP2))))"

	+"(general-match ( ((? _) (lambda (s) (equal? '(Something that) s )))((? VP1) (lambda (s) (vp-p s)))((? VP2) (lambda (s) (vp-p s))))"
	      +"( ("+ chExiquant+"x) (x  (? VP1) "+ chAnd+" x  (? VP2))))"
	//9.3
	+"(general-match( ((? _) (lambda (s) (equal? '(Nothing that) s )))((? VP1) (lambda (s) (vp-p s)))((? VP2) (lambda (s) (vp-p s))))"
	      +"( "+ chNeg+"("+ chExiquant+"x) (x  (? VP1) "+ chAnd+" x  (? VP2))))"

	+"(general-match( ((? _) (lambda (s) (equal? '(Everything that) s )))((? Subj) (lambda (s) (subject-p s)))((? RV) (lambda (s) (rv-p s)))((? VP) (lambda (s) (vp-p s))))"
	      +"( ("+ chUniquant+"x) ((? Subj) (? RV) x "+ chImplic+" x  (? VP))))"

	+"(general-match( ((? _) (lambda (s) (equal? '(Something that) s )))((? Subj) (lambda (s) (subject-p s)))((? RV) (lambda (s) (rv-p s)))((? VP) (lambda (s) (vp-p s))))"
	      +"( ("+ chExiquant+"x) ((? Subj) (? RV) x "+ chAnd+" x  (? VP))))"

	+"(general-match( ((? _) (lambda (s) (equal? '(Nothing that) s )))((? Subj) (lambda (s) (subject-p s)))((? RV) (lambda (s) (rv-p s)))((? VP) (lambda (s) (vp-p s))))"
	      +"( "+ chNeg+" ("+ chExiquant+"x) ((? Subj) (? RV) x "+ chAnd+" x  (? VP))))"


	/*Rule 11.1*/


	+"(general-match ( ((? _) (lambda (s) (equal? '(Every ) s ))) ((? NP) (lambda (s) (np-p s))) ((? VP) (lambda (s) (vp-p s))) )"
	      +" ( ("+ chUniquant+"x) (x is a (? NP) "+ chImplic+" x  (? VP))))"

	+"(general-match ( ((? _) (lambda (s) (equal? '(Some ) s ))) ((? NP) (lambda (s) (np-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
	      +" ( ("+ chExiquant+"x) (x is a (? NP) "+ chAnd+" x  (? VP))))"

	+"(general-match ( ((? _) (lambda (s) (equal? '(No ) s ))) ((? NP) (lambda (s) (np-p s))) ((? VP) (lambda (s) (vp-p s))) )"
	      +"  ( "+ chNeg+" ("+ chExiquant+"x) (x is a (? NP) "+ chAnd+" x  (? VP))))"

	 /* 12.1 */
	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV1) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(everything that ) s ))) ((? Subj) (lambda (s) (subject-p s))) ((? RV2) (lambda (s) (rv-p s))) ) "
	     +" ( ("+ chUniquant+"y) ((? Subj) (? RV2) y "+ chImplic+" (? Term) (? RV1) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV1) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(something that ) s ))) ((? Subj) (lambda (s) (subject-p s))) ((? RV2) (lambda (s) (rv-p s))) )"
	     +"  ( ("+ chExiquant+"y) ((? Subj) (? RV2) y "+ chAnd+" (? Term) (? RV1) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV1) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(nothing that ) s ))) ((? Subj) (lambda (s) (subject-p s))) ((? RV2) (lambda (s) (rv-p s))) ) "
	     +" ( "+ chNeg+"("+ chExiquant+"y)  ((? Subj) (? RV2) y "+ chAnd+" (? Term) (? RV1) y)))"
	//13
	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(everything that ) s ))) ((? VP) (lambda (s) (vp-p s))) ) "
	     +" ( ("+ chUniquant+"y) ( y (? VP)  "+ chImplic+" (? Term) (? RV) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(something that ) s ))) ((? VP) (lambda (s) (vp-p s))) ) "
	     +" ( ("+ chExiquant+"y) ( y (? VP)  "+ chAnd+" (? Term) (? RV) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(nothing that ) s ))) ((? VP) (lambda (s) (vp-p s))) )"
	     +"  ( "+ chNeg+"("+ chExiquant+"y) ( y (? VP)  "+ chAnd+" (? Term) (? RV) y)))"
	//14
	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(every ) s ))) ((? NP) (lambda (s) (np-p s))) ) "
	     +" ( ("+ chUniquant+"y) ( y is a  (? NP)  "+ chImplic+" (? Term) (? RV) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(some ) s ))) ((? NP) (lambda (s) (np-p s))) )"
	     +"  ( ("+ chExiquant+"y) ( y is a  (? NP)  "+ chAnd+" (? Term) (? RV) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(no) s ))) ((? NP) (lambda (s) (np-p s))) ) "
	     +" ( "+ chNeg+"("+ chExiquant+"y) ( y is a  (? NP)  "+ chAnd+" (? Term) (? RV) y)))"
	/*15.1*/
	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(everything) s ))) ) "
	     +" ( ("+ chUniquant+"y)((? Term) (? RV) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(something) s ))) ) "
	     +" ( ("+ chExiquant+"y)((? Term) (? RV) y)))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(nothing) s ))) ) "
	     +" ( "+ chNeg+"("+ chExiquant+"y)((? Term) (? RV) y)))"
	//18.1
	+"(general-match ( ((? _) (lambda (s) (equal? '(both) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) )"
	     +"  ( ((? Subj1) (? VP) "+ chAnd+"  (? Subj2) (? VP))))"

	+"(general-match (((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
	     +" ( ((? Subj1)(? VP) "+ chAnd+"  (? Subj2)(? VP))))"

	+"(general-match ( ((? _) (lambda (s) (equal? '(either) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
	     +" ( ((? Subj1) (? VP)"+ chOr+" (? Subj2) (? VP))))"

	+"(general-match ( ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) )"
	     +"  ( ((? Subj1) (? VP)"+ chOr+" (? Subj2) (? VP))))"

	+"(general-match ( ((? _) (lambda (s) (equal? '(neither) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(nor) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
	     +" (  "+ chNeg+" ((? Subj1) (? VP)"+ chOr+" (? Subj2) (? VP))))"
	//19.1
	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is both) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(and) s )))"
	     +"  ((? BP2) (lambda (s) (bp-p s))) ) ( ((? Term) is (? BP1) "+ chAnd+"  (? Term) is (? BP2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? BP2) (lambda (s) (bp-p s))) ) "
	     +" ( ((? Term) is (? BP1) "+ chAnd+"  (? Term) is (? BP2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is either) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? BP2) (lambda (s) (bp-p s))) )"
	     +"  ( ((? Term) is (? BP1)"+ chOr+" (? Term) is (? BP2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? BP2) (lambda (s) (bp-p s))) )"
	     +"  ( ((? Term) is (? BP1)"+ chOr+" (? Term) is (? BP2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is neither) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(nor) s ))) ((? BP2) (lambda (s) (bp-p s))) )"
	     +"  ( "+ chNeg+"((? Term) is (? BP1)"+ chOr+" (? Term) is (? BP2))))"
	//20.1
	+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(both) s ))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
	     +"  ( ((? Subj) (? VP1) "+ chAnd+"  (? Subj) (? VP2))))"

	+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? VP2) (lambda (s) (vp-p s))) ) "
	     +" ( ((? Subj) (? VP1) "+ chAnd+"  (? Subj) (? VP2))))"  /***/

	+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(either) s ))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
	     +"  ( ((? Subj) (? VP1)"+ chOr+" (? Subj) (? VP2))))"

	+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
	     +"  (((? Subj) (? VP1)"+ chOr+" (? Subj) (? VP2))))"

	+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(neither) s ))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(nor) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
	     +"  ( "+ chNeg+" ((? Subj) (? VP1)"+ chOr+" (? Subj) (? VP2))))"
	//21.1
	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(both) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s )))"
	     +"  ((? Subj2) (lambda (s) (subject-p s))) ) (((? Term) (? RV) (? Subj1) "+ chAnd+"  (? Term) (? RV) (? Subj2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? Subj2) (lambda (s) (subject-p s))) ) "
	     +" (((? Term) (? RV) (? Subj1) "+ chAnd+"  (? Term) (? RV) (? Subj2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(either) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(or) s )))"
	     +"  ((? Subj2) (lambda (s) (subject-p s))) ) (((? Term) (? RV) (? Subj1)"+ chOr+" (? Term) (? RV) (? Subj2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? Subj2) (lambda (s) (subject-p s))) )"
	     +"  (  ((? Term) (? RV) (? Subj1)"+ chOr+" (? Term) (? RV) (? Subj2))))"

	+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(neither) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(nor) s ))) ((? Subj2) (lambda (s) (subject-p s))) ) "
	     +" ( "+ chNeg+" ((? Term) (? RV) (? Subj1)"+ chOr+" (? Term) (? RV) (? Subj2))))"
	//22.1
	+"(middle-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (does)) ((? DP) (lambda (s) (dp-p s))) ) "
	      +"(  ( (? Term) (? DP) )))"


	/**/


	+") )";


String fProcessing="(begin "

+"(define (instantiate-variables pattern a-list) (cond ((null? pattern) empty) ((not(pair? (car pattern))) (cons (car pattern) (instantiate-variables (cdr pattern) a-list))) ((eq? '? (car (car pattern))) (append (extract-value (find-binding (car pattern) a-list)) (instantiate-variables (cdr pattern) a-list))) (#t (cons (instantiate-variables (car pattern) a-list) (instantiate-variables (cdr pattern) a-list))) ) )"

+"(define (symbolize-variables pattern a-list) (cond ((not (pair? pattern)) pattern) ((eq? '? (car pattern)) (extract-symbolization (find-binding pattern a-list))) (#t (cons (symbolize-variables (car pattern) a-list) (symbolize-variables (cdr pattern) a-list)))) )"

+"(define (try-rule rule assertion) (let ((binding-list ((rule-matching-function rule) assertion  (rule-if rule))) ) (if (eq? empty binding-list) empty (do ((binding-list binding-list (cdr binding-list)) (result-list empty)) ((null? binding-list) result-list) (let ((result (instantiate-variables (rule-then rule) (car binding-list)))) (if (not (null? result)) (set! result-list (cons result result-list))) )))) )"

+"(define (code list-of-coding-functions list-to-be-coded) (if (or ( null? list-of-coding-functions) ( null? list-to-be-coded)) empty (cons ((car list-of-coding-functions)(car list-to-be-coded)) (code (cdr list-of-coding-functions)  (cdr list-to-be-coded))) ) )"

+"(define (try-symbolization symbol-rule assertion) (let ((binding-list ((rule-matching-function symbol-rule) assertion  (rule-if symbol-rule))) ) (if (null? binding-list) empty (do ((binding-list binding-list (cdr binding-list)) (result-list empty)) ((null? binding-list) result-list) (let ((result  (symbolize-variables (rule-then symbol-rule) (car binding-list))) )(if (not (null? result)) (set! result-list (cons result result-list))) )) )) )"

+"(define (try-all-the-prop-shuffle-rules assertion) (if (null? assertion) empty (accumulate (map (lambda (rule) (try-rule rule assertion)) gPropRules))) )"

+"(define (try-all-the-prop-symbol-rules assertion) (if (null? assertion) empty (accumulate (map (lambda (symbol-rule) (try-symbolization symbol-rule assertion)) gPropSymbolizations))) )"

+"(define (try-all-the-symbol-rules assertion) (if (null? assertion) empty (accumulate (map (lambda (symbol-rule) (try-symbolization symbol-rule assertion)) gSymbolizations))) )"

+"(define (try-all-the-shuffle-rules assertion) (if (null? assertion)empty(accumulate (map (lambda (rule) (try-rule rule assertion)) gRules))) )"

+ /*maybe not used*/"(define (try-all-the-prop-rules assertion) (if (null? assertion) empty (let ( (result-list (accumulate (map (lambda (rule) (try-rule rule assertion)) gPropRules))) ) (if (null? result-list) (accumulate (map (lambda (symbol-rule) (try-symbolization symbol-rule assertion)) gPropSymbolizations)) result-list ) ) ) )"

+/*new endJuly for sentence-p*/"(define (try-all-the-rules assertion) (if (null? assertion) empty (let ( (result-list (try-all-the-symbol-rules assertion)) )(if (null? result-list)(try-all-the-shuffle-rules assertion)  result-list))))"

+")";

String fLispPredicates="(begin "

+"(define (adj-p word-list)(myassoc word-list gAdjectives))"

+"(define (adjnoun-p word-list)(if (null? word-list)#f (adjnoun-parse word-list)))"

+"(define (atomic-proposition-p word-list)(myassoc word-list gPropositions))"

+" (define (binary-adj-p word-list)(myassoc word-list gBinaryAdjectives))"

+" (define (BP-p word-list)(if (null? word-list) #f (b-phrase-parse word-list)))"

+" (define (DP-p word-list)(if (null? word-list) #f (d-phrase-parse word-list)))"

+"(define (noun-p word-list) (myassoc word-list gNouns) )"

//+ "(define (np-p word-list)(myassoc word-list gNouns)) "  more detail Sept 04

+ "(define (np-p word-list)(if (null? word-list) #f (np-parse word-list)))"

+"(define (passv-p word-list)(myassoc word-list gPassiveVerbs))"

+"(define (proposition-p word-list) (cond ((myassoc word-list gPropositions)) ((let ((shuffle (try-all-the-prop-shuffle-rules word-list))) (not (null? shuffle)))) (#t #f) ) )"

+"(define (rv-p word-list) (if (null? word-list) #f (relational-verb-parse word-list ) ) )"

// going to try parsing Sept 5+"(define (sentence-p word-list) (cond ((proposition-p word-list) ) ((let ((symbolization (try-all-the-symbol-rules word-list))) (not (null? symbolization)))) ((let ((shuffle (try-all-the-rules word-list))) (not (null? shuffle)))) (#t #f) ) )"

+"(define (sentence-p word-list)(if (null? word-list)#f (sentence-parse word-list)))"

+"(define (subject-p word-list) (if (null? word-list) #f (subject-parse word-list)))"

+"(define (term-p word-list) (cond ((myassoc word-list gNames)) ((myassoc word-list gEngVariables)) (#t #f) ) )"

+"(define (verbp-p word-list) (myassoc word-list gPassiveVerbs))"

+"(define (vi-p word-list)(myassoc word-list gIntransitiveVerbs))"

//+"(define (vp-p word-list)(myassoc word-list gIntransitiveVerbs))"

+"(define (vp-p word-list)(if (null? word-list) #f (verb-phrase-parse word-list ) ) )"

+"(define (vt-p word-list) (myassoc word-list gTransitiveVerbs) )"

+")";


String fAssocList="(begin (define *anonymous* '_)"

+" (define empty '())"   // Scheme should have this but ours does not

+"(define (extract-variable variable-expr) (cadr variable-expr ))"

+"(define (variable-p variable-expr) (if (list? variable-expr ) (if (= (length variable-expr) 2) (if (eqv? (car variable-expr) '?) #t #f) #f) #f))"

+"(define (make-binding variable datum symbolization ) (list variable datum symbolization))"

+"(define (extract-key binding) (car binding))"

+"(define (extract-value binding) (cadr binding))"

+"(define (extract-symbolization binding) (caddr binding))"

+"(define (find-binding variable-expr bindings) (assoc (extract-variable variable-expr) bindings))"

+"(define (add-binding variable-expr datum symbolization bindings) (if (eqv? *anonymous* (extract-variable variable-expr)) bindings (cons (make-binding (extract-variable variable-expr) datum symbolization) bindings)))"

+"(define (extract-var var-pair) (car var-pair) )"

+"(define (extract-test var-pair) (cadr var-pair) )"

+"(define (add-binding-to-each-member-of-list variable-expr datum symbolization list-of-assoc-lists) (if (null? list-of-assoc-lists) (list (add-binding variable-expr datum symbolization empty)) (map (lambda (assoc-list) (add-binding variable-expr datum symbolization assoc-list)) list-of-assoc-lists) ) ))";


String fGeneralMatch="(begin (define (general-match word-list variable-pair-list) (let ((result-list empty) (target-length (length word-list)) (no-of-variables (length variable-pair-list)) )(cond ( (and (null? word-list) (null? variable-pair-list)) 'success) ( (or (null? word-list) (null? variable-pair-list)) empty) (#t(let ((test (eval(extract-test (car variable-pair-list)))) ) (letrec ((mydotimes (lambda (count limit)(if (< count limit) (begin (let ((symbolization (test (butlast  word-list (- target-length (+ count 1))))) ) (if symbolization (let ((tail-assoc-list (general-match (nthcdr (+ count 1)  word-list) (cdr variable-pair-list))) ) (if (not (null? tail-assoc-list)) (begin (if (eqv? tail-assoc-list 'success) (set! tail-assoc-list empty)) (set! result-list (append result-list (add-binding-to-each-member-of-list (extract-var (car variable-pair-list)) (butlast  word-list (- target-length (+ count 1))) symbolization tail-assoc-list) )))) ) ) (mydotimes (+ 1 count) limit))) ))))(mydotimes 0 (- target-length (- no-of-variables 1)))) result-list) )) ) )"

 +"(define (total-match word-list variable-pair-list) (let* ((test (eval (extract-test (car variable-pair-list)))) (symbolization (test word-list)      ) ) (if symbolization (list (add-binding (extract-var (car variable-pair-list)) word-list symbolization empty)) empty ) ) )"

 +"(define (left-match word-list variable-pair-list) (let ((left-literal (extract-test (car variable-pair-list))) (variable (extract-var (cadr variable-pair-list))) (test (eval (extract-test (cadr variable-pair-list)))) ) (left-match-aux word-list left-literal variable test) ) )"

 +"(define (left-match-aux target left-literal variable test) (if (null? left-literal) (let ((symbolization (test target)) ) (if symbolization (list (add-binding variable target symbolization empty)) empty) )(if (equal? (car left-literal) (car target)) (left-match-aux (cdr target) (cdr left-literal) variable test) empty) ) )"

 +"(define (middle-match word-list variable-pair-list) (middle-match-aux word-list (car variable-pair-list) (cadr variable-pair-list) (caddr variable-pair-list) ) )"

 +"(define (middle-match-aux target left-variable-pair middle-variable-pair right-variable-pair) (let* ((left-variable (extract-var left-variable-pair)) (test1 (eval (extract-test left-variable-pair))) (middle-literal (extract-test middle-variable-pair)) (right-variable (extract-var right-variable-pair)) (test2 (eval (extract-test right-variable-pair))) (length-target (length target)) (length-middle (length middle-literal)) (result-list empty) ) (if (or (< length-target 3 ) (< length-target (+ 2 length-middle) )) empty (letrec ((mydotimes (lambda (count limit) (if (< count limit) (let ((temp-left (butlast target (- length-target (+ count 1) ))) (temp-middle (butlast (nthcdr (+ count 1)  target ) (- length-target (+ count 1 length-middle) ))) (temp-right  (nthcdr (+ count 1 length-middle)  target )) )(if (equal? middle-literal temp-middle) (let ((left-symbolization  (test1 temp-left )) ) (if left-symbolization (let ((right-symbolization  (test2 temp-right )) ) (if (and left-symbolization right-symbolization) (set! result-list (cons (add-binding right-variable temp-right right-symbolization (add-binding left-variable temp-left left-symbolization empty))result-list))) ))))(mydotimes (+ 1 count) limit))) )))(mydotimes 0 (- length-target (+ 1 length-middle) )) result-list))))"

 +"(define (left-literal-then-middle-match word-list variable-pair-list) (let ((left-literal (extract-test (car variable-pair-list))) ) (left-literal-then-middle-match-aux word-list left-literal (cadr variable-pair-list) (caddr variable-pair-list) (cadddr variable-pair-list) ) ) )"

 +"(define (left-literal-then-middle-match-aux target left-literal left-variable-pair middle-variable-pair  right-variable-pair) (if (null? left-literal) (middle-match-aux target left-variable-pair middle-variable-pair  right-variable-pair) (if (equal? (car target) (car left-literal)) (left-literal-then-middle-match-aux (cdr target) (cdr left-literal) left-variable-pair middle-variable-pair right-variable-pair) empty) ) ))";


String fPropositions="(define gPropositions '("
   +"((PHILOSOPHY IS HARD) H)"
   +"((PHILOSOPHY IS INTERESTING) I)"
   +"((LOGIC IS HARD) L)"
   +"((LOGIC IS INTERESTING) M)"
   +"((WE RUN A WAR) W)"
   +"((WE REDUCE UNEMPLOYMENT) U)"
   +"((WE INCREASE HEALTH COSTS) C)"
   +"))";

String fPropSymbolizations = "(define gPropSymbolizations '("
 +"(total-match (((? Prop)  (lambda (s) (proposition-p s))))((? Prop)))"
 +"))";

String fSymbolizations = "(define gSymbolizations '("

  +"(middle-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (is a)) ((? Noun) (lambda (s) (noun-p s))) ) (  ( (? Noun) (? Term) )))"

  +"(middle-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (is )) ((? Adj) (lambda (s) (adj-p s))) ) (  ( (? Adj) (? Term) )))"

  +"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? Vi) (lambda (s) (vi-p s))) ) (  ( (? Vi) (? Term) )))"

  +"(general-match ( ((? Term1) (lambda (s) (term-p s))) ((? Vt) (lambda (s) (vt-p s))) ((? Term2) (lambda (s) (term-p s))) ) (  ( (? Vt) (? Term1)(? Term2) )))"

  +"(general-match ( ((? Term1) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is) s))) ((? Verbp) (lambda (s) (verbp-p s))) ((? Term2) (lambda (s) (term-p s))) ) (  ( (? Verbp) (? Term1)(? Term2) )))"

  +"(general-match( ((? Term1) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is) s)))((? Adj2) (lambda (s) (binary-adj-p s)))((? Term2) (lambda (s) (term-p s))))(  ( (? Adj2) (? Term1)(? Term2) )))"

//7  +"(general-match( ((? Term) (lambda (s) (term-p s)))((? RV) (lambda (s) (rv-p s)))((? _) (lambda (s) (equal? '(itself) s))))(  ( (? RV) (? Term)(? Term) )))"

// there was a mistake here June05-- I had forgotten the himself herself itself

  +"(general-match( ((? Term) (lambda (s) (term-p s)))((? VT) (lambda (s) (vt-p s)))((? _) (lambda (s) (or (equal? '(himself) s)(equal? '(herself) s)(equal? '(itself) s)))))(  ( (? VT) (? Term)(? Term) )))"

  +"(general-match( ((? Term1) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is) s)))((? Verbp) (lambda (s) (verbp-p s)))((? _) (lambda (s) (or (equal? '(himself) s)(equal? '(herself) s)(equal? '(itself) s)))))(  ( (? Verbp) (? Term1)(? Term1) )))"

    +"(general-match( ((? Term1) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is) s)))((? Adj2) (lambda (s) (binary-adj-p s)))((? _) (lambda (s) (or (equal? '(himself) s)(equal? '(herself) s)(equal? '(itself) s)))))(  ( (? Adj2) (? Term1)(? Term1) )))"

  +"))";

String fGrammarData = "(begin"
  +"(define gAdjectives '( ((ANGRY) A)((BOLD) B)((CHEERFUL) C)))"
  +"(define gBinaryAdjectives '( ( (RUDER THAN) R)((SMARTER THAN) S)))"
  +"(define gEngVariables '( ((X) x) ((Y) y) ((Z) z) ((W) w)) )"
  +"(define gIntransitiveVerbs '(((STUDIES) S)((THINKS) T)))"
  +"(define gPassiveVerbs '( ( (DRIVEN BY) D)((ENCOURAGED BY) E)))"
  +"(define gNames '( ( (ARTHUR) a) ((BERYL) b) ((CHARLES) c) ) )"
  +"(define gNouns '( ((NINCOMPOOP) N) ((PHILOSOPHER) P)) )"
  +"(define gTransitiveVerbs '( ((ANNOYS) A) ((BRINGS) B) ) )"

 // +"(define gTerms (append gNames gEngVariables ) )"  /*need this for translating back*/  DON'T USE BECAUSE DYNAMIC

  + ")";


String fCommands = "(begin"
  +"(define (remember-adjective english-name-list logical-name )(set! gAdjectives (cons (list english-name-list logical-name) gAdjectives)))"
  +"(define (remember-binadj english-name-list logical-name )(set! gBinaryAdjectives (cons (list english-name-list logical-name) gBinaryAdjectives)))"
  +"(define (remember-iverb english-name-list logical-name )(set! gIntransitiveVerbs (cons (list english-name-list logical-name) gIntransitiveVerbs)))"
  +"(define (remember-pverb english-name-list logical-name )(set! gPassiveVerbs (cons (list english-name-list logical-name) gPassiveVerbs)))"
  +"(define (remember-tverb english-name-list logical-name )(set! gTransitiveVerbs (cons (list english-name-list logical-name) gTransitiveVerbs)))"
  +"(define (remember-name english-name-list logical-name )(set! gNames (cons (list english-name-list logical-name) gNames)))"
  +"(define (remember-noun english-name-list logical-name )(set! gNouns (cons (list english-name-list logical-name) gNouns)))"
  +"(define (remember-proposition english-name-list logical-name )(set! gPropositions (cons (list english-name-list logical-name) gPropositions)))"

  + ")";

private String [] fLtoEFilter={"",""}; /* sometimes internal lisp literals can be awkward, eg the period,
                          so we replace them then call this filter to put them back when we need to*/

                         // watch out for regex syntax and the need for escape characters


static final String lispFalseStr="#f";


public TEnglishToLogic(){

 Thread t= new Thread(){
   public void run() {
     initializeLISPContext();
   }
 };
 t.setDaemon(true);
 t.start();


}

public Scheme getScheme(){
  return
     fScheme;
}


public void setLtoEFilter(String[]filter){
  if(filter.length==2)
     fLtoEFilter = filter;
}
public void replacePropRules(String newRules){
  lispEvaluate(newRules);
}

public void replaceRules(String newRules){
  lispEvaluate(newRules);
}

public void resetToBergmannRules(){
   replacePropRules(TBergmannEtoLRules.fPropRules);  // needs different from superclass
   replaceRules(TBergmannEtoLRules.fRules);

}

public void resetToCopiRules(){
    replacePropRules(TCopiEtoLRules.fPropRules);  // copi needs different from superclass
    replaceRules(TCopiEtoLRules.fRules);
    String[] params={"chcopiand","."};                     //params={"chCopiAnd","."};  our lisp is lower case only
    setLtoEFilter(params);                           // so we write back the ands properly

}

public void resetToGentzenRules(){
    replacePropRules(TGentzenEtoLRules.fPropRules);  // needs different from superclass
    replaceRules(TGentzenEtoLRules.fRules);

}

public void resetToDefaultRules(){
    replacePropRules(TDefaultEtoLRules.fPropRules);  // needs different from superclass
    replaceRules(TDefaultEtoLRules.fRules);

}

public void resetToHerrickRules(){
     replacePropRules(THerrickEtoLRules.fPropRules);  // needs different from superclass
     replaceRules(THerrickEtoLRules.fRules);

}

void initializeLISPContext(){
lispEvaluate(fUtilities);  // sets the LISP evaluation context
lispEvaluate(fRuleAccessors);  // sets the LISP evaluation context
lispEvaluate(fProcessing);  // sets the LISP evaluation context
lispEvaluate(fLispPredicates);  // sets the LISP evaluation context
lispEvaluate(fAssocList);  // sets the LISP evaluation context
lispEvaluate(fGeneralMatch);  // sets the LISP evaluation context
lispEvaluate(fPropositions);  // sets the LISP evaluation context
lispEvaluate(fPropSymbolizations);  // sets the LISP evaluation context
lispEvaluate(fPropRules);  // sets the LISP evaluation context
lispEvaluate(fRules);  // sets the LISP evaluation context
lispEvaluate(fSymbolizations);  // sets the LISP evaluation context
lispEvaluate(fGrammarData);  // sets the LISP evaluation context
lispEvaluate(fCommands);  // sets the LISP evaluation context


lispEvaluate(TPartsOfSpeech.fBPhrase);
lispEvaluate(TPartsOfSpeech.fAdjNoun);
lispEvaluate(TPartsOfSpeech.fDPhrase);
lispEvaluate(TPartsOfSpeech.fNewGlobals);
lispEvaluate(TPartsOfSpeech.fNounPhrase);
lispEvaluate(TPartsOfSpeech.fRelClause);
lispEvaluate(TPartsOfSpeech.fRelVerb);
lispEvaluate(TPartsOfSpeech.fSentence);
lispEvaluate(TPartsOfSpeech.fSubject);
lispEvaluate(TPartsOfSpeech.fTokenizer);
lispEvaluate(TPartsOfSpeech.fVerbPhrase);

/* not using SISC
try {

AppContext ctx = new AppContext();
Context.register("eol", ctx);
fSISCScheme = Context.enter("eol");
REPL.loadHeap(fSISCScheme, REPL.findHeap(""));
System.out.println(" EOL done.");

 fSISCScheme.eval("(import s2j)");  // the java interface

fSISCScheme.eval(fUtilities);


fSISCScheme.eval(fRuleAccessors);  // sets the LISP evaluation context



fSISCScheme.eval(fProcessing);  // sets the LISP evaluation context



fSISCScheme.eval(fLispPredicates);  // sets the LISP evaluation context



fSISCScheme.eval(fAssocList);  // sets the LISP evaluation context




fSISCScheme.eval(fGeneralMatch);  // sets the LISP evaluation context



fSISCScheme.eval(fPropositions);  // sets the LISP evaluation context



fSISCScheme.eval(fPropSymbolizations);  // sets the LISP evaluation context



fSISCScheme.eval(fPropRules);  // sets the LISP evaluation context



fSISCScheme.eval(fRules);  // sets the LISP evaluation context


fSISCScheme.eval(fSymbolizations);  // sets the LISP evaluation context

fSISCScheme.eval(fGrammarData);  // sets the LISP evaluation context


fSISCScheme.eval(fCommands);  // sets the LISP evaluation context






fSISCScheme.eval(TPartsOfSpeech.fBPhrase);
fSISCScheme.eval(TPartsOfSpeech.fAdjNoun);
fSISCScheme.eval(TPartsOfSpeech.fDPhrase);
fSISCScheme.eval(TPartsOfSpeech.fNewGlobals);
fSISCScheme.eval(TPartsOfSpeech.fNounPhrase);
fSISCScheme.eval(TPartsOfSpeech.fRelClause);
fSISCScheme.eval(TPartsOfSpeech.fRelVerb);
fSISCScheme.eval(TPartsOfSpeech.fSentence);
fSISCScheme.eval(TPartsOfSpeech.fSubject);
fSISCScheme.eval(TPartsOfSpeech.fTokenizer);
fSISCScheme.eval(TPartsOfSpeech.fVerbPhrase);




}
catch(Exception ex) {
   ex.printStackTrace();
 }


*/

 fUtilities=null;
   fRuleAccessors=null;
   fProcessing=null;
   fLispPredicates=null;
   fAssocList=null;
   fGeneralMatch=null;
   fPropositions=null;
   fPropSymbolizations=null;
   fPropRules=null;
   fRules=null;
   fSymbolizations=null;
   fGrammarData=null;
   fCommands=null;


}


/*

 procedure implodeList (index: integer); (*to gOutputStream*)
   var
    localindex: integer;
    message: string;
  begin

   case itsnodetype(index) of
    symbol:
     begin
      message := svalue(index);
      gOutPutStream.WriteStringWithoutLengthByte(@message);
     end;
    number:
     begin
      message := StrofNum(ivalue(index));
      gOutPutStream.WriteStringWithoutLengthByte(@message);
     end;
    consNode:
     begin
      while iscons(index) do
       begin
        implodeList(car(index));
        index := cdr(index);
       end;
      if issymbol(index) and (svalue(index) = 'NIL') then

      else
       begin
        gOutPutStream.WriteCharacter('.');
        implodeList(index)
       end;
     end;

    otherwise

   end;
  end;


*/



      public Object lispEvaluate(String inputStr){
        boolean quoted = true;
        
        if (inputStr==null) //May 2010
        	return
        	   null;
       

        {
              try {

                InputPort input = new InputPort(new StringReader(inputStr));

                {
                  Object x;
                  Object result;
                  String resultStr;
                  if (input.isEOF(x = input.read()))
                    return
                        null;
                  //   write(eval(x), output, true); }

  //               System.out.println(x);                       //test in May

                                                     // let lisp process it

                    result = fScheme.eval(x);

                  //  resultStr = SchemeUtils.stringify(result, !quoted);

              //      System.out.println(resultStr);

               //     writeToJournal("Scheme output: " + resultStr, true, false);

               return
                   result;


                }
              }
            catch (Exception ex) {

          //    writeToJournal("Scheme Exception: " + ex, true, false);

              System.err.println("Scheme Exception: " + ex);
             }
             }
             return
                 null;

      }


String lookUpSymbol(String symbol, String assocList){

            Object result;

              result=lispEvaluate("(rassoc '"
                                  + symbol +chBlank
                                  + assocList+")");

              String resultStr = SchemeUtils.stringify(result, false);

              if (resultStr.equals(lispFalseStr))
                resultStr=null;
              else
                resultStr=TUtilities.innerListOutputToUpperCase(resultStr);

            //  System.out.println(resultStr+" LookUp " +symbol);

              return
                  resultStr;

      }


private String lookUpTerm(String symbol){
  String returnStr=null;

  returnStr=lookUpSymbol(symbol, "gNames");

  if (returnStr==null)
    returnStr=lookUpSymbol(symbol, "gEngVariables");

  return
      returnStr;
}


/*String lookUpProposition(String proposition){

      Object result;

        result=lispEvaluate("(rassoc '"
                            + proposition
                            +" gPropositions"+")");

        String resultStr = SchemeUtils.stringify(result, false);

        if (resultStr.equals(lispFalseStr))
          resultStr=null;
        else
          resultStr=TUtilities.innerListOutputToUpperCase(resultStr);

        System.out.println(resultStr+" LookUp");

        return
            resultStr;

}*/

/*


       function LookupProposition (proposition: string): integer;
        var
         pair, value: integer;

       begin
        gEOF := false;
        value := InternalStringtoSExpression(proposition);

        pair := rassoc(value, gPropositions);

        if (pair <> gNil) then
         LookupProposition := extract_key(pair)
        else
         LookupProposition := gNil;
       end;




   */



/*String lookUpTerm(String term){


  /*I used to use this for variables in quantifiers, but now I just upper case

  Object result;

  result=lispEvaluate("(rassoc '" +term + " gTerms"+")");

  String resultStr = SchemeUtils.stringify(result, false);


  if (resultStr.equals(lispFalseStr))
          resultStr=null;
  else
     resultStr=TUtilities.innerListOutputToUpperCase(resultStr);

        System.out.println(resultStr+" LookUp term");

        return
            resultStr;

}*/


/*

         function LookupTerm (term: string): integer;
        var
         pair, value: integer;

       begin
        gEOF := false;
        value := InternalStringtoSExpression(term);

        pair := rassoc(value, gNames);

        if (pair <> gNil) then
         LookupTerm := extract_key(pair)
        else
         begin
          pair := rassoc(value, gEngVariables);
          if (pair <> gNil) then
           LookupTerm := extract_key(pair)
          else
           LookupTerm := gNil;
         end;
       end;



   */

/*  String lookUpInTransVerb(String predicator){


    Object result;

    result=lispEvaluate("(rassoc '" +predicator + " gInTransitiveVerbs"+")");

    String resultStr = SchemeUtils.stringify(result, false);


    if (resultStr.equals(lispFalseStr))
            resultStr=null;
    else
       resultStr=TUtilities.innerListOutputToUpperCase(resultStr);

          System.out.println(resultStr+" LookUp trans verb");

          return
              resultStr;

  }*/


/*
    function try_all_the_propositional_rules (assertion: integer; var english: boolean): integer;

  (*here we first try to shuffle it around as english, then if that fails we try to symbolize it*)
    var
     temp: integer;

    function myfunction (rule: integer): integer;
    begin
     myfunction := try_rule(rule, assertion);
    end;

    function mysecondfunction (rule: integer): integer;
    begin
     mysecondfunction := try_symbolization(rule, assertion);
    end;

   begin
    if endp(assertion) then
     temp := gNil
    else
     begin

      temp := accumulate(mapcar(myfunction, gPropositionalRules));  (*english*)

      if (temp = gNil) then
       temp := accumulate(mapcar(mysecondfunction, gPropSymbolizationRules));  (*symbolize*)

     end;
    try_all_the_propositional_rules := temp;
   end;


  */

 String prettifyLispString(String input){

   input=input.substring(1,input.length()-1);  // omit outer brakcets
   if (fLtoEFilter[0].length()>0)
     input=input.replaceAll(fLtoEFilter[0],fLtoEFilter[1]);  // watch out for regex syntax and the need for escape characters
   input=input.toUpperCase();

  return
      input;
 }


 public String symbolizePropLevel(String englishWords){

   /*{If the result is symbolic we need to write it back without blanks in between} */


   Object result;
   String resultStr=null;
   int length;

   englishWords="'"+ englishWords;  //need quoted for lisp

   result=lispEvaluate("(try-all-the-prop-shuffle-rules "+ englishWords +")");
        // trying to shuffle the English

   length = SchemeUtils.length(result);

   if (length==1){    //unique shuffle
       resultStr = SchemeUtils.stringify(SchemeUtils.first(result), false);
       resultStr=prettifyLispString(resultStr);
      /* resultStr=resultStr.substring(1,resultStr.length()-1);  // omit outer brakcets
       resultStr=resultStr.toUpperCase();*/
     }

   else{
     if (length>1){ // ambiguous shuffle
       String temp;
       Object search=result;

       resultStr="{Ambiguous!"+ strCR;

       for (int i=0;i<length;i++){

         temp = SchemeUtils.stringify(SchemeUtils.first(search), false);
         temp=prettifyLispString(temp);
        /* temp=temp.substring(1,temp.length()-1);  // omit outer brakcets
         temp=temp.toUpperCase();*/

         resultStr = resultStr+
                     temp +
                     strCR;

         search=SchemeUtils.rest(search);
       }
       resultStr = resultStr+ "}";
     }
     else{      // no shuffle at all, so we try to symbolize
       result=lispEvaluate("(try-all-the-prop-symbol-rules "+ englishWords +")");
        // trying to symbolize

       length = SchemeUtils.length(result);

       if (length==1){    //unique shuffle
          resultStr = SchemeUtils.stringify(SchemeUtils.first(result), false);
          resultStr=prettifyLispString(resultStr);
         /* resultStr=resultStr.substring(1,resultStr.length()-1);  // omit outer brakcets
          resultStr=resultStr.toUpperCase();*/
        }
     }

   }
  // System.out.println(resultStr);

 return
     resultStr;
   }

   String prettifyQuantifiers(String input){ // here we lower case the variables

     //watch out for regex syntax and the need to escape characters \\

   String output=input.replaceAll(chUniquant+"X",chUniquant+"x");
   output=output.replaceAll(chUniquant+"Y",chUniquant+"y");
   output=output.replaceAll(chUniquant+"Z",chUniquant+"z");

   output=output.replaceAll("\\(X\\)","(x)");  //in copi there is no uniquant
   output=output.replaceAll("\\(Y\\)","(y)");
   output=output.replaceAll("\\(Z\\)","(z)");


   output=output.replaceAll(chExiquant+"X",chExiquant+"x");
   output=output.replaceAll(chExiquant+"Y",chExiquant+"y");
   output=output.replaceAll(chExiquant+"Z",chExiquant+"z");

     return
         output;
   }

   public String symbolizePredLevel(String englishWords){

   /*{If the result is symbolic we need to write it back without blanks in between} */


   Object result;
   String resultStr=null;
   int length;

   englishWords="'"+ englishWords;  //need quoted for lisp


   result=lispEvaluate("(try-all-the-symbol-rules "+ englishWords +")");
        // trying to symbolize

   length = SchemeUtils.length(result);

   if (length==1){    //successful symbolization
      resultStr = SchemeUtils.stringify(SchemeUtils.first(result), false);

      /* what this looks like now is ((s a)), ie lower case (with this lisp) and brakcets*/

      resultStr=resultStr.substring(2,resultStr.length()-2);  // omit outer brakcets

      resultStr = TUtilities.defaultFilter(resultStr); //removes blanks

      resultStr= (resultStr.substring(0,1)).toUpperCase() + resultStr.substring(1); //upcase the predicate Sa

   }
   else{


     result = lispEvaluate("(try-all-the-shuffle-rules " + englishWords +
                           ")");
     // trying to shuffle the English

     length = SchemeUtils.length(result);

     if (length == 1) { //unique shuffle
       resultStr = SchemeUtils.stringify(SchemeUtils.first(result), false);
      /* resultStr = resultStr.substring(1, resultStr.length() - 1); // omit outer brakcets
       resultStr=resultStr.toUpperCase();*/
       resultStr=prettifyLispString(resultStr);
       resultStr=prettifyQuantifiers(resultStr);
     }

     else {
       if (length > 1) { // ambiguous shuffle
         String tempStr;
         Object search = result;

         resultStr = "{Ambiguous!" + strCR;

         for (int i = 0; i < length; i++) {

           tempStr = SchemeUtils.stringify(SchemeUtils.first(search), false);
          /* tempStr = tempStr.substring(1, tempStr.length() - 1); // omit outer brakcets
           tempStr=tempStr.toUpperCase();*/
           tempStr=prettifyLispString(tempStr);
           tempStr=prettifyQuantifiers(tempStr);

           resultStr = resultStr +
               tempStr +
               strCR;

           search = SchemeUtils.rest(result);
         }
         resultStr = resultStr + "}";
       }

     }
   }
  // System.out.println(resultStr);

 return
     resultStr;
   }


/* NOT USING SISC FOR THIS

   public String symbolizePredLevelSISC(String englishWords){

     // uses SISc lisp only



    //  Object result;
      Value resultV=null;  //sisc
      String resultStr=null;
      int length=0;

      englishWords="'"+ englishWords;  //need quoted for lisp


     // result=lispEvaluate("(try-all-the-symbol-rules "+ englishWords +")");
           // trying to symbolize

      try{
          resultV=fSISCScheme.eval("(try-all-the-symbol-rules "+ englishWords +")");
          length = ((Quantity)fSISCScheme.eval("(length '"+resultV+")")).intValue();

         // (->jint number)
      }
      catch(Exception ex) {
                ex.printStackTrace();
         }

//int intlength = ((Quantity)length).intValue();

     // length = SchemeUtils.length(result);

     // next line breaks with case sensitive

      if (length==1){    //successful symbolization
         resultStr=resultV.toString();


         resultStr=resultStr.substring(2,resultStr.length()-2);  // omit outer brakcets

         resultStr = TUtilities.defaultFilter(resultStr); //removes blanks

         resultStr= (resultStr.substring(0,1)).toUpperCase() + resultStr.substring(1); //upcase the predicate Sa

      }
      else{

         resultV=null;  //sisc
         resultStr=null;


       // result = lispEvaluate("(try-all-the-shuffle-rules " + englishWords +
        //                      ")");
        // trying to shuffle the English


        try{
          resultV=fSISCScheme.eval("(try-all-the-shuffle-rules "+ englishWords +")");
          length = ((Quantity)fSISCScheme.eval("(length '"+resultV+")")).intValue();
      }
      catch(Exception ex) {
                ex.printStackTrace();
         }


        if (length==1) { //unique shuffle
          resultStr=resultV.toString();
          resultStr = resultStr.substring(1, resultStr.length() - 1); // omit outer brakcets
          resultStr=resultStr.toUpperCase();
          resultStr=prettifyQuantifiers(resultStr);
        }

        else {
           if (length > 1) { // ambiguous shuffle
            String temp;
            Object search = result;

            resultStr = "{Ambiguous!" + strCR;

            for (int i = 0; i < length; i++) {

              temp = SchemeUtils.stringify(SchemeUtils.first(search), false);
              temp = temp.substring(1, temp.length() - 1); // omit outer brakcets
              resultStr=resultStr.toUpperCase();
              resultStr=prettifyQuantifiers(resultStr);

              resultStr = resultStr +
                  temp +
                  strCR;

              search = SchemeUtils.rest(result);
            }
            resultStr = resultStr + "}";
          }

        }
      }
     // System.out.println(resultStr);

    return
        resultStr;
      }  */


/*

 (key = 'NAME') then
     begin
      if (listlength(third(english_word_list)) = 1) then
       if atomATerm(fourth(english_word_list)) then
       begin
       LispParseWords;
       answer := remember(cons(lispWordList, cdr(cdr(cdr(english_word_list)))), gNames);
       end;



   */



private String prettyOutput(String item, String list){
    list= list.substring(1,list.length()-1);  // omit outer brakcets

   return
       item + " = " + list.toUpperCase();

}

 public String rememberAdjective(Object lispInput){
 /* should have form (remember iverb (Drinks) d)
    and we know that it starts (remember iverb   )*/

lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

 if ((SchemeUtils.length(lispInput)==2)
   && (SchemeUtils.length(SchemeUtils.first(lispInput))==1)){

    String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

    String adj = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

    adj=adj.toUpperCase(); // all the internal lisp IS lower case, belt and braces here

    if ((adj.length()==1)
      && TParser.isPredicate(adj.charAt(0))){
       Object result = lispEvaluate("(remember-adjective '"
                                 + englishList
                                 + " '"
                                 + adj
                                 + ")");
    return
      prettyOutput(adj,englishList);
    }
  }
  return
      null;
}


 public String rememberIVerb(Object lispInput){
  /* should have form (remember iverb (Drinks) d)
     and we know that it starts (remember iverb   )*/

 lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

  if ((SchemeUtils.length(lispInput)==2)
    && (SchemeUtils.length(SchemeUtils.first(lispInput))==1)){

     String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

     String pred = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

     pred=pred.toUpperCase(); // all the internal lisp IS lower case, belt and braces here

     if ((pred.length()==1)
       && TParser.isPredicate(pred.charAt(0))){
        Object result = lispEvaluate("(remember-iverb '"
                                  + englishList
                                  + " '"
                                  + pred
                                  + ")");
     return
       prettyOutput(pred,englishList);
     }
   }
   return
       null;
 }

 public String rememberTVerb(Object lispInput){
   /* should have form (remember tverb (Drinks) d)
      and we know that it starts (remember tverb   )*/

  lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

   if ((SchemeUtils.length(lispInput)==2)
     && (SchemeUtils.length(SchemeUtils.first(lispInput))==1)){

      String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

      String pred = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

      pred=pred.toUpperCase(); // all the internal lisp IS lower case, belt and braces here

      if ((pred.length()==1)
        && TParser.isPredicate(pred.charAt(0))){
         Object result = lispEvaluate("(remember-tverb '"
                                   + englishList
                                   + " '"
                                   + pred
                                   + ")");
      return
        prettyOutput(pred,englishList);
      }
    }
    return
        null;
  }

  public String rememberPVerb(Object lispInput){
    /* should have form (remember iverb (Drinks) d)
       and we know that it starts (remember iverb   )*/

   lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

    if ((SchemeUtils.length(lispInput)==2)
      && (SchemeUtils.length(SchemeUtils.first(lispInput))==1)){

       String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

       String pred = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

       pred=pred.toUpperCase(); // all the internal lisp IS lower case, belt and braces here

       if ((pred.length()==1)
         && TParser.isPredicate(pred.charAt(0))){
          Object result = lispEvaluate("(remember-pverb '"
                                    + englishList
                                    + " '"
                                    + pred
                                    + ")");
       return
         prettyOutput(pred,englishList);
       }
     }
     return
         null;
   }

   public String rememberBinAdj(Object lispInput){
     /* should have form (remember iverb (Drinks) d)
        and we know that it starts (remember iverb   )*/

    lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

     if ((SchemeUtils.length(lispInput)==2)
       && (SchemeUtils.length(SchemeUtils.first(lispInput))==1)){

        String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

        String pred = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

        pred=pred.toUpperCase(); // all the internal lisp IS lower case, belt and braces here

        if ((pred.length()==1)
          && TParser.isPredicate(pred.charAt(0))){
           Object result = lispEvaluate("(remember-binadj '"
                                     + englishList
                                     + " '"
                                     + pred
                                     + ")");
        return
          prettyOutput(pred,englishList);
        }
      }
      return
          null;
    }



public String rememberName(Object lispInput){
 /* should have form (remember name (David) d)
    and we know that it starts (remember name   )*/

lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

 if ((SchemeUtils.length(lispInput)==2)
   && (SchemeUtils.length(SchemeUtils.first(lispInput))==1)){

    String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

    String name = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

    name=name.toLowerCase(); // all the internal lisp IS lower case, belt and braces here

    if ((name.length()==1)
      && TParser.isFunctor(name.charAt(0))){
       Object result = lispEvaluate("(remember-name '"
                                 + englishList
                                 + " '"
                                 + name
                                 + ")");
    return
      prettyOutput(name,englishList);
    }
  }
  return
      null;
}

public String rememberNoun(Object lispInput){
/* should have form (remember noun (Drinks) d)
   and we know that it starts (remember nou   )*/

lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

if ((SchemeUtils.length(lispInput)==2)
  && (SchemeUtils.length(SchemeUtils.first(lispInput))==1)){

   String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

   String noun = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

   noun=noun.toUpperCase(); // all the internal lisp IS lower case, belt and braces here

   if ((noun.length()==1)
     && TParser.isPredicate(noun.charAt(0))){
      Object result = lispEvaluate("(remember-noun '"
                                + englishList
                                + " '"
                                + noun
                                + ")");
   return
     prettyOutput(noun,englishList);
   }
 }
 return
     null;
}


public String rememberProposition(Object lispInput){
 /* should have form (remember proposition (Cats are great) C)
    and we know that it starts (remember proposition   )*/

//boolean successful=false;
//String resultStr=null;

 lispInput=SchemeUtils.rest(SchemeUtils.rest(lispInput));  // drop the first two which are known to be good

 if ((SchemeUtils.length(lispInput)==2)
   && (SchemeUtils.length(SchemeUtils.first(lispInput))>0)){

    String englishList= SchemeUtils.stringify(SchemeUtils.first(lispInput),false);

    String prop = SchemeUtils.stringify(SchemeUtils.second(lispInput),false);

    prop=prop.toUpperCase(); // all the internal lisp is lower case

    if ((prop.length()==1)
      && TParser.isPredicate(prop.charAt(0))){   //props and preds are the same
       Object result = lispEvaluate("(remember-proposition '"
                                 + englishList
                                 + " '"
                                 + prop
                                 + ")");


//   englishList= englishList.substring(1,englishList.length()-1);  // omit outer brakcets

 //  resultStr= prop + " = " + englishList.toUpperCase();


   return
      prettyOutput(prop,englishList);
  }
}



  return
      null;
}



public String symbolizeOneStep(String englishWords,boolean propChosen){

 if (propChosen)
   return
       symbolizePropLevel(englishWords);
 else
   return
       //symbolizePredLevelSISC(englishWords);
 symbolizePredLevel(englishWords);

  }

/*

  procedure SymbolizeOneStep (predicate: boolean; english_word_list: integer; var success: boolean);

 {If the result is symbolic we need to write it back without blanks in between}

   var
    symbolization: integer;
    english: boolean;

   procedure format (symbolization: integer);
    var
     search: integer;
     message: string;
   begin
    search := symbolization;

    message := '{Ambiguous!';
    gOutputStream.WriteStringWithoutLengthByte(@message);
    gOutPutStream.WriteCharacter(gReturn);

    while search <> gNil do
     begin
      putexp_omitting_outerbrackets(car(search));
      search := cdr(search);
      gOutPutStream.WriteCharacter(gReturn);
     end;
    gOutPutStream.WriteCharacter('}');
   end;


  begin
   gOutputStr := strNull;
   english := true;




   if predicate then
    symbolization := try_all_the_rules(english_word_list, english)  (*predicate analysis*)
   else
    symbolization := try_all_the_propositional_rules(english_word_list, english); (*prop analysis*)


   if symbolization = gNil then
    success := false
   else
    begin
     success := true;

     gOutputStream.SetForWritingTo;

     if listlength(symbolization) = 1 then
      begin
       if english then
        putexp_omitting_outerbrackets(first(symbolization))(*to gOutputStream*)
       else
        implodeList(first(symbolization))(*to gOutputStream*)
      end
     else
      format(symbolization);



     symbolization := 0;
     english_word_list := 0;
    end;

   if CollectionWise then
    if collectGarbage then
     ; (*mf remove*)

  end;

  */




String translateAtomic(TFormula root, TParser aParser){
  String resultStr=null,predicator=null,term1=null,term2=null;

  switch (root.arity()){

    case 0:                       //proposition
       resultStr= /*lookUpProposition(root.fInfo); */ lookUpSymbol(root.fInfo, "gPropositions");
      break;
    case 1:                       //predicate
       term1=/*lookUpTerm(root.firstTerm().fInfo);*/ lookUpTerm(root.firstTerm().fInfo/*, "gTerms"*/);

       if (term1==null)
         return
             null;                // we don't know it cannot translate
       else{
         predicator=/*lookUpInTransVerb(root.fInfo);*/ lookUpSymbol(root.fInfo, "gIntransitiveVerbs");

         if (predicator!=null)
           resultStr= term1 + chBlank + predicator;
         else{
            predicator= lookUpSymbol(root.fInfo, "gNouns");

            if (predicator!=null)
              resultStr= term1 + " IS A " + predicator;
            else{
               predicator= lookUpSymbol(root.fInfo, "gAdjectives");

               if (predicator!=null)
                 resultStr= term1 + " IS " + predicator;
               else
                 resultStr=null;  // we cannot find it
            }

         }

       }

       break;

     case 2:                       //predicate
        term1=lookUpTerm(root.firstTerm().fInfo);
        if (term1!=null)
            term2=lookUpTerm(root.secondTerm().fInfo);



        if ((term1==null)||(term2==null))
           return
              null;                // we don't know it cannot translate
        else{
           predicator= lookUpSymbol(root.fInfo, "gTransitiveVerbs");

           if (predicator!=null)
              resultStr= term1 + chBlank + predicator + chBlank +term2;
           else{
              predicator= lookUpSymbol(root.fInfo, "gPassiveVerbs");

              if (predicator!=null)
                 resultStr= term1 + chBlank + predicator + chBlank +term2;
             else{
                 predicator= lookUpSymbol(root.fInfo, "gBinaryAdjectives");

         if (predicator!=null)
           resultStr= term1 + " IS " + predicator + chBlank +term2 ;
         else
           resultStr=null;  // we cannot find it
      }

   }

 }

 break;



    default: ;
  }


  return
      resultStr;

}


/*

  procedure TranslateAtomic;
      var
       term1, term2: integer;
     begin


      case root.Arity of
       0:
        begin
         key := root.fInfo;
         temp := LookupProposition(key);
         if (temp <> gNil) then
         putexp_omitting_outerbrackets(temp)
         else
         TranslateBackFormula := false;   (*prop not known*)
        end;
       1:
        begin
         key := root.FirstTerm.fInfo;
         term1 := LookupTerm(key);

         if (term1 = gNil) then
         TranslateBackFormula := false  (*term not known*)
         else
         begin
         key := root.fInfo;
         temp := LookupInTransVerb(key);
         if (temp <> gNil) then
         putexp_omitting_outerbrackets(append(term1, temp))
         else
         begin
         temp := LookupNoun(key);
         if (temp <> gNil) then
         begin
         putexp_omitting_outerbrackets(term1);
         message := 'IS A  ';
         outPutStream.WriteStringWithoutLengthByte(@message);
         putexp_omitting_outerbrackets(temp);
         end
         else
         begin
         temp := LookupAdj(key);
         if (temp <> gNil) then
         begin
         putexp_omitting_outerbrackets(term1);
         message := 'IS ';
         outPutStream.WriteStringWithoutLengthByte(@message);
         putexp_omitting_outerbrackets(temp);
         end
         else
         TranslateBackFormula := false;   (*predicate not known*)
         end;
         end;
         end;
        end;

       2:
        begin
         key := root.FirstTerm.fInfo;
         term1 := LookupTerm(key);

         if (term1 = gNil) then
         TranslateBackFormula := false  (*term not known*)
         else
         begin
         key := root.SecondTerm.fInfo;
         term2 := LookupTerm(key);

         if (term2 = gNil) then
         TranslateBackFormula := false  (*term not known*)
         else
         begin
         key := root.fInfo;
         temp := LookupTransVerb(key);

         if (temp <> gNil) then
         putexp_omitting_outerbrackets(append(term1, append(temp, term2)))
         else
         begin
         temp := LookupPassiveVerb(key);
         if (temp <> gNil) then
         begin
         putexp_omitting_outerbrackets(term1);
         message := ' IS ';
         outPutStream.WriteStringWithoutLengthByte(@message);
         putexp_omitting_outerbrackets(temp);
         putexp_omitting_outerbrackets(term2);
         end
         else
         begin
         temp := LookupBinaryAdj(key);
         if (temp <> gNil) then
         begin
         putexp_omitting_outerbrackets(term1);
         message := ' IS ';
         outPutStream.WriteStringWithoutLengthByte(@message);
         putexp_omitting_outerbrackets(temp);
         putexp_omitting_outerbrackets(term2);
         end
         else
         TranslateBackFormula := false;   (*predicate not known*)
         end;
         end;
         end;
         end;
        end;
       otherwise
      end;


*/

public void myDebugTest(){

  Object result = lispEvaluate("(define orRule '(middle-match(((? Sentence1) (lambda (s) (proposition-p s)))((? _) (or) ) ((? Sentence2) (lambda (s) (proposition-p s))))((? Sentence1) v (? Sentence2))))");

  result = lispEvaluate("(try-rule orRule '(WE RUN A WAR OR WE RUN A WAR OR WE RUN A WAR))");

 System.out.println(SchemeUtils.stringify(result, false));

 result = lispEvaluate("(try-all-the-prop-rules '(EITHER WE RUN A WAR OR WE RUN A WAR OR WE RUN A WAR))"
);

System.out.println(SchemeUtils.stringify(result, false));

 result = lispEvaluate("(try-all-the-prop-shuffle-rules '(EITHER WE RUN A WAR OR WE RUN A WAR OR WE RUN A WAR))"
);

System.out.println(SchemeUtils.stringify(result, false));



/*
 result = lispEvaluate("(b-phrase-parse '(both driven by himself and driven by himself))"
                               );


 System.out.println(SchemeUtils.stringify(result, false));

 result = lispEvaluate("(d-phrase-parse '(annoys herself))");


 System.out.println(SchemeUtils.stringify(result, false));

 result = lispEvaluate("(rel-clause-top '(that engword (Arthur is driven by)))");


 System.out.println(SchemeUtils.stringify(result, false));

 result = lispEvaluate("(relational-verb-parse '(is ruder than))");

 System.out.println(SchemeUtils.stringify(result, false));

 result = lispEvaluate("(subject-parse '(everything that studies))");

 System.out.println(SchemeUtils.stringify(result, false));

 result = lispEvaluate("(verb-phrase-parse '(both studies and thinks and studies or thinks))");

 System.out.println(SchemeUtils.stringify(result, false));


*/




}


public String translateBack (TFormula root, TParser aParser){
  String result="Help";

   switch (root.fKind){
         case TFormula.functor:
         case TFormula.variable:


            break;

          case TFormula.predicator:
             result=translateAtomic(root,aParser);
             break;

          case TFormula.equality:

            //lookUpTerm("x");


             break;

           case TFormula.unary:
             result=
                 "IT IS NOT THE CASE THAT  "+
                 aParser.writeInner(root.fRLink);
             break;

/*
             unary:
          begin
          message := 'IT IS NOT THE CASE THAT';
          outPutStream.WriteStringWithoutLengthByte(@message);
          aParser.WriteInnerToStream(root.fRlink, outPutStream);
          end;  */


           case TFormula.binary:
             String left =aParser.writeInner(root.fLLink);
             String right =aParser.writeInner(root.fRLink);

             if (aParser.isImplic(root))
                result= "IF  "
                        + left
                        + " THEN "
                        + right;
             else
             if (aParser.isAnd(root))
              result= left
                      + " AND "
                      + right;
             else
             if (aParser.isOr(root))
               result= left
                      + " OR "
                      + right;
                else
                 if (aParser.isEquiv(root))
                   result= left
                          + " IF, AND ONLY IF,  "
                          + right;

             break;

            /*

                    binary:
                    if (root.fInfo = chImplic) then
                    begin
                    message := 'IF  ';
                    outPutStream.WriteStringWithoutLengthByte(@message);
                    aParser.WriteInnerToStream(root.fLlink, outPutStream);
                    message := ' THEN ';
                    outPutStream.WriteStringWithoutLengthByte(@message);
                    aParser.WriteInnerToStream(root.fRlink, outPutStream);
                    end
                    else
                    begin
                    aParser.WriteInnerToStream(root.fLlink, outPutStream);

                    case root.fInfo[1] of
                    chAnd:
                    message := ' AND ';
                    chOr:
                    message := ' OR ';
                    chEquiv:
                    message := ' IF, AND ONLY IF, ';

                    otherwise
                    end;

                    outPutStream.WriteStringWithoutLengthByte(@message);

                    aParser.WriteInnerToStream(root.fRlink, outPutStream);
                    end;


       */


           case TFormula.quantifier:
             String scope =aParser.writeInner(root.scope());


             if (aParser.isUniquant(root))
               result= "FOR ALL "
                      + root.quantVar()
                      + ", ";
                   //   +scope; //BUG APRIL 06
                else
               result= "THERE IS AN "
                         + root.quantVar()
                         + ", SUCH THAT ";
                    //     +scope; //BUG APRIL 06

           //  result=result.toUpperCase();  //BUG APRIL 06 don't upper case scope

           result=result.toUpperCase();
           result+=scope;

             break;

             /*
                      begin
                     if (root.fInfo = chUniquant) then
                     begin
                     message := 'FOR ALL ';
                     outPutStream.WriteStringWithoutLengthByte(@message);
                     putexp_omitting_outerbrackets(LookUpTerm(root.QuantVar));
                     message := ', ';
                     outPutStream.WriteStringWithoutLengthByte(@message);
                     end
                     else
                     begin
                     message := 'THERE IS AN ';
                     outPutStream.WriteStringWithoutLengthByte(@message);
                     putexp_omitting_outerbrackets(LookUpTerm(root.QuantVar));
                     message := ' SUCH THAT ';
                     outPutStream.WriteStringWithoutLengthByte(@message);
                     end;

                     aParser.WriteInnerToStream(root.Scope, outPutStream);
                     end;


        }*/

  case TFormula.typedQuantifier:

TFormula expansion=null;

if (aParser.isTypedUniquant(root))
  expansion=aParser.expandTypeUni(root);
else
if (aParser.isTypedExiquant(root))
  expansion=aParser.expandTypeExi(root);


if (expansion==null) //should not happen
 translateBack(root,aParser);


         default: ;
       }

   return
       result;
 }



/*

  if root <> nil then
     begin
      case root.fKind of
       predicator:
        TranslateAtomic;
       functor:
        aParser.WriteTermToStream(root, outPutStream);
       variable:
        aParser.WriteTermToStream(root, outPutStream);
       equality: {check}
        begin
        outPutStream.WriteCharacter('(');
        aParser.WriteTermToStream(root.fRlink.fLLink, outPutStream);

        outPutStream.WriteCharacter(root.fInfo[1]);

        aParser.WriteTermToStream(root.fRlink.fRlink.fLLink, outPutStream);
        outPutStream.WriteCharacter(')');
        end;




  */

 public String writeAssocList(Object list){
   String outPutStr="",tempStr=null,tempStr2=null;


   Object item=null;

   if (list!=null)
       item=SchemeUtils.first(list);

   while ((list!=null)&&(item!=null)){
     tempStr=SchemeUtils.stringify((SchemeUtils.first(SchemeUtils.rest(item))));
   //  tempStr=tempStr.substring(1,tempStr.length()-1);  // omit outer brakcets
     tempStr=tempStr.toUpperCase();

     tempStr2=SchemeUtils.stringify((SchemeUtils.first(item)));
     tempStr2=tempStr2.substring(1,tempStr2.length()-1);  // omit outer brakcets
     tempStr2=tempStr2.toUpperCase();

     outPutStr=outPutStr + tempStr +" = " + tempStr2 + strCR;

     list=SchemeUtils.rest(list);

     if (list!=null)
       item=SchemeUtils.first(list);

   }

   return
      outPutStr;

 }




 /*

  procedure WriteAssocList (list: integer);
    var
     dummy: integer;
    function WriteEach (item: integer): integer;   (*works by side-effect*)
    begin

     putexp_omitting_outerbrackets(car(cdr(item)));
     gOutputStream.WriteCharacter(chBlank);
     gOutputStream.WriteCharacter(chEquals);
     gOutputStream.WriteCharacter(chBlank);
     putexp_omitting_outerbrackets(car(item));

     gOutputStream.WriteCharacter(gReturn);
     WriteEach := 0;
    end;
   begin
  (*gOutputStream.SetForWritingTo;*)
    dummy := mapcar(writeEach, list);
   end;

  */

}

/*
kAdjectivesRSRCID = 14639;
  kBinaryAdjsRSRCID = 12924;
  kIntransitive_VerbsRSRCID = 10193;
  kNamesRSRCID = 20647;
  kNounsRSRCID = 16040;
  kPassiveVerbsRSRCID = 24361;
  kPredSymbolizationRulesRSRCID = 6761;
  kPropsRSRCID = 24459;
  kPropositionalRulesRSRCID = 17732;
  kPropSymbolizationRulesRSRCID = 13351;
  kRulesRSRCID = 335;
  kTransitive_VerbsRSRCID = 20575;
  kVariablesRSRCID = 12892;

  kPropAnalysis = false;
  kPredAnalysis = true;



*/


/*

 procedure InitializeResourceAssociationList (var target: integer; rsrcId: integer);  (*reads from Text in resource*)


 This seems to read text and create a LISP expression out of it.

 var
   TextLength: longint;
   aHdl: handle;
 begin

  TextLength := 0;

  TextLength := SizeResource(GetResource('TEXT', rsrcId));
  if TextLength <= 0 then
   sysBeep(5)
  else
   begin
    aHdl := NewHandle(TextLength);
    aHdl := GetResource('TEXT', rsrcId);

    PassHdlToInputHdl(aHdl, TextLength);  (*reads it to gInputHdl*)
    GetInput;
    skip(1, LispFilter);  (*primes gCurrch, and gLookaheadCh*)

    scan(LispFilter);
    getexp(target, LispFilter);
   end;
  ReleaseResource(aHdl);
 end;


procedure ReadGrammar;
  begin
   InitializeResourceAssociationList(gAdjectives, kAdjectivesRSRCID);  (*reads from Text in resource*)
   InitializeResourceAssociationList(gBinaryAdjs, kBinaryAdjsRSRCID);  (*reads from Text in resource*)
   InitializeResourceAssociationList(gNames, kNamesRSRCID);  (*reads from Text in resource*)
{InitializeResourceAssociationList(gRules, kRulesRSRCID);   kludge}
   InitializeResourceAssociationList(gPassiveVerbs, kPassiveVerbsRSRCID);  (*reads from Text in resource*)
   InitializeResourceAssociationList(gPropSymbolizationRules, kPropSymbolizationRulesRSRCID);  (*reads from Text in resource*)
{InitializeResourceAssociationList(gPredSymbolizationRules, kPredSymbolizationRulesRSRCID);  kludge}
   InitializeResourceAssociationList(gPropositions, kPropsRSRCID);  (*reads from Text in resource*)
   InitializeResourceAssociationList(gPropositionalRules, kPropositionalRulesRSRCID);
   InitializeResourceAssociationList(gNouns, kNounsRSRCID);  (*reads from Text in resource*)
   InitializeResourceAssociationList(gIntransitive_Verbs, kIntransitive_VerbsRSRCID);  (*reads from Text in resource*)
   InitializeResourceAssociationList(gTransitive_Verbs, kTransitive_VerbsRSRCID);  (*reads from Text in resource*)
   InitializeResourceAssociationList(gEngVariables, kVariablesRSRCID);  (*reads from Text in resource*)

  end;





 gPropositions from ResEdit (
((WE RUN A WAR) W)
((WE REDUCE UNEMPLOYMENT) U)
((WE INCREASE HEALTH COSTS) H)
) 

 so we need

 "(define gPropositions '(
((WE RUN A WAR) W)
((WE REDUCE UNEMPLOYMENT) U)
((WE INCREASE HEALTH COSTS) H)
) 
)"

 For prop symbolizations, in Scheme we have

 (remember-prop-symbolization
 '(111
      total-match
      (((? Prop)  (lambda (s) (proposition-p s)))
       )
      ((? Prop))))

so we need

"(define gPropSymbolizations '(
          (111
           total-match
           (((? Prop)  (lambda (s) (proposition-p s)))
            )
           ((? Prop))))
     ) 
     )"






*/
