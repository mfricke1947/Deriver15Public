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

import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;



public class TCopiEtoLRules {


 /*Unfortunately the copi symbol for 'and' ie period plays havoc with the lisp so we have to use a different internal representa
  tion 'chCopiAnd' then replace it when we write back in EToL*/

/*Of course, the rules are different for the different logics
  i) the period for and here,
  ii) no Universal quantifier*/




public static String fPropRules="(define gPropRules '("

  +"( left-match (((? _) (It is not the case that)) ((? Sentence) (lambda (s) (proposition-p s))) ) ("+ chNeg+" ((? Sentence))))"

  +"( left-then-middle-match (((? _) (both)) ((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (and)) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+ "chCopiAnd"+" (? Sentence2))))"

  +"( middle-match (((? Sentence1) (lambda (s) (proposition-p s))) ((? _) (and) ) ((? Sentence2) (lambda (s) (proposition-p s))) ) (((? Sentence1) "+"chCopiAnd"+" (? Sentence2))))"

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
      +"(((? Sentence1) "+ "chCopiAnd"+" (? Sentence2))))"

+"( middle-match (((? Sentence1) (lambda (s) (sentence-p s))) ((? _) (and) ) ((? Sentence2) (lambda (s) (sentence-p s))) ) "
      +"(((? Sentence1) "+ "chCopiAnd"+" (? Sentence2))))"

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
      +"( ((? Term) is a (? NP) "+ "chCopiAnd"+" (? Subj) (? RV) (? Term))))"
//6.1
+"(general-match( ((? Term) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is a) s )))((? Adj) (lambda (s) (adj-p s)))((? Noun) (lambda (s) (adjnoun-p s))))"
      +"( ((? Term) is (? Adj) "+ "chCopiAnd"+" (? Term) is a (? Noun))))"
//7/1
+"(general-match ( ((? Term) (lambda (s) (term-p s)))((? _) (lambda (s) (equal? '(is a) s )))((? NP) (lambda (s) (np-p s)))((? _) (lambda (s) (equal? '(that) s )))((? VP) (lambda (s) (vp-p s))))"
      +"( ((? Term) is a (? NP) "+ "chCopiAnd"+" (? Term) (? VP))))"

+"(left-match (((? _) (Everything)) ((? VP) (lambda (s) (vp-p s))))"
      +"(("+ "x) (x  (? VP))))"

+"(left-match(((? _) (Something)) ((? VP) (lambda (s) (vp-p s))))"
      +"( ("+ chExiquant+"x) (x  (? VP))))"

+"(left-match (((? _) (Nothing)) ((? VP) (lambda (s) (vp-p s))))"
      +"("+ chNeg+"("+ chExiquant+"x) (x  (? VP))))"

+"(general-match ( ((? _) (lambda (s) (equal? '(Everything that) s )))((? VP1) (lambda (s) (vp-p s)))((? VP2) (lambda (s) (vp-p s))))"
      +"( ("+ "x) (x  (? VP1) "+ chImplic+" x  (? VP2))))"

+"(general-match ( ((? _) (lambda (s) (equal? '(Something that) s )))((? VP1) (lambda (s) (vp-p s)))((? VP2) (lambda (s) (vp-p s))))"
      +"( ("+ chExiquant+"x) (x  (? VP1) "+ "chCopiAnd"+" x  (? VP2))))"
//9.3
+"(general-match( ((? _) (lambda (s) (equal? '(Nothing that) s )))((? VP1) (lambda (s) (vp-p s)))((? VP2) (lambda (s) (vp-p s))))"
      +"( "+ chNeg+"("+ chExiquant+"x) (x  (? VP1) "+ "chCopiAnd"+" x  (? VP2))))"

+"(general-match( ((? _) (lambda (s) (equal? '(Everything that) s )))((? Subj) (lambda (s) (subject-p s)))((? RV) (lambda (s) (rv-p s)))((? VP) (lambda (s) (vp-p s))))"
      +"( ("+ "x) ((? Subj) (? RV) x "+ chImplic+" x  (? VP))))"

+"(general-match( ((? _) (lambda (s) (equal? '(Something that) s )))((? Subj) (lambda (s) (subject-p s)))((? RV) (lambda (s) (rv-p s)))((? VP) (lambda (s) (vp-p s))))"
      +"( ("+ chExiquant+"x) ((? Subj) (? RV) x "+ "chCopiAnd"+" x  (? VP))))"

+"(general-match( ((? _) (lambda (s) (equal? '(Nothing that) s )))((? Subj) (lambda (s) (subject-p s)))((? RV) (lambda (s) (rv-p s)))((? VP) (lambda (s) (vp-p s))))"
      +"( "+ chNeg+" ("+ chExiquant+"x) ((? Subj) (? RV) x "+ "chCopiAnd"+" x  (? VP))))"


/*Rule 11.1*/


+"(general-match ( ((? _) (lambda (s) (equal? '(Every ) s ))) ((? NP) (lambda (s) (np-p s))) ((? VP) (lambda (s) (vp-p s))) )"
      +" ( ("+ "x) (x is a (? NP) "+ chImplic+" x  (? VP))))"

+"(general-match ( ((? _) (lambda (s) (equal? '(Some ) s ))) ((? NP) (lambda (s) (np-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
      +" ( ("+ chExiquant+"x) (x is a (? NP) "+ "chCopiAnd"+" x  (? VP))))"

+"(general-match ( ((? _) (lambda (s) (equal? '(No ) s ))) ((? NP) (lambda (s) (np-p s))) ((? VP) (lambda (s) (vp-p s))) )"
      +"  ( "+ chNeg+" ("+ chExiquant+"x) (x is a (? NP) "+ "chCopiAnd"+" x  (? VP))))"

 /* 12.1 */
+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV1) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(everything that ) s ))) ((? Subj) (lambda (s) (subject-p s))) ((? RV2) (lambda (s) (rv-p s))) ) "
     +" ( ("+ "y) ((? Subj) (? RV2) y "+ chImplic+" (? Term) (? RV1) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV1) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(something that ) s ))) ((? Subj) (lambda (s) (subject-p s))) ((? RV2) (lambda (s) (rv-p s))) )"
     +"  ( ("+ chExiquant+"y) ((? Subj) (? RV2) y "+ "chCopiAnd"+" (? Term) (? RV1) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV1) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(nothing that ) s ))) ((? Subj) (lambda (s) (subject-p s))) ((? RV2) (lambda (s) (rv-p s))) ) "
     +" ( "+ chNeg+"("+ chExiquant+"y)  ((? Subj) (? RV2) y "+ "chCopiAnd"+" (? Term) (? RV1) y)))"
//13
+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(everything that ) s ))) ((? VP) (lambda (s) (vp-p s))) ) "
     +" ( ("+ "y) ( y (? VP)  "+ chImplic+" (? Term) (? RV) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(something that ) s ))) ((? VP) (lambda (s) (vp-p s))) ) "
     +" ( ("+ chExiquant+"y) ( y (? VP)  "+ "chCopiAnd"+" (? Term) (? RV) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(nothing that ) s ))) ((? VP) (lambda (s) (vp-p s))) )"
     +"  ( "+ chNeg+"("+ chExiquant+"y) ( y (? VP)  "+ "chCopiAnd"+" (? Term) (? RV) y)))"
//14
+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(every ) s ))) ((? NP) (lambda (s) (np-p s))) ) "
     +" ( ("+ "y) ( y is a  (? NP)  "+ chImplic+" (? Term) (? RV) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(some ) s ))) ((? NP) (lambda (s) (np-p s))) )"
     +"  ( ("+ chExiquant+"y) ( y is a  (? NP)  "+ "chCopiAnd"+" (? Term) (? RV) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(no) s ))) ((? NP) (lambda (s) (np-p s))) ) "
     +" ( "+ chNeg+"("+ chExiquant+"y) ( y is a  (? NP)  "+ "chCopiAnd"+" (? Term) (? RV) y)))"
/*15.1*/
+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(everything) s ))) ) "
     +" ( ("+ "y)((? Term) (? RV) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(something) s ))) ) "
     +" ( ("+ chExiquant+"y)((? Term) (? RV) y)))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(nothing) s ))) ) "
     +" ( "+ chNeg+"("+ chExiquant+"y)((? Term) (? RV) y)))"
//18.1
+"(general-match ( ((? _) (lambda (s) (equal? '(both) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) )"
     +"  ( ((? Subj1) (? VP) "+ "chCopiAnd"+"  (? Subj2) (? VP))))"

+"(general-match (((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
     +" ( ((? Subj1)(? VP) "+ "chCopiAnd"+"  (? Subj2)(? VP))))"

+"(general-match ( ((? _) (lambda (s) (equal? '(either) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
     +" ( ((? Subj1) (? VP)"+ chOr+" (? Subj2) (? VP))))"

+"(general-match ( ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) )"
     +"  ( ((? Subj1) (? VP)"+ chOr+" (? Subj2) (? VP))))"

+"(general-match ( ((? _) (lambda (s) (equal? '(neither) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(nor) s ))) ((? Subj2) (lambda (s) (subject-p s))) ((? VP) (lambda (s) (vp-p s))) ) "
     +" (  "+ chNeg+" ((? Subj1) (? VP)"+ chOr+" (? Subj2) (? VP))))"
//19.1
+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is both) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(and) s )))"
     +"  ((? BP2) (lambda (s) (bp-p s))) ) ( ((? Term) is (? BP1) "+ "chCopiAnd"+"  (? Term) is (? BP2))))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? BP2) (lambda (s) (bp-p s))) ) "
     +" ( ((? Term) is (? BP1) "+ "chCopiAnd"+"  (? Term) is (? BP2))))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is either) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? BP2) (lambda (s) (bp-p s))) )"
     +"  ( ((? Term) is (? BP1)"+ chOr+" (? Term) is (? BP2))))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? BP2) (lambda (s) (bp-p s))) )"
     +"  ( ((? Term) is (? BP1)"+ chOr+" (? Term) is (? BP2))))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? _) (lambda (s) (equal? '(is neither) s ))) ((? BP1) (lambda (s) (bp-p s))) ((? _) (lambda (s) (equal? '(nor) s ))) ((? BP2) (lambda (s) (bp-p s))) )"
     +"  ( "+ chNeg+"((? Term) is (? BP1)"+ chOr+" (? Term) is (? BP2))))"
//20.1
+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(both) s ))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
     +"  ( ((? Subj) (? VP1) "+ "chCopiAnd"+"  (? Subj) (? VP2))))"

+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? VP2) (lambda (s) (vp-p s))) ) "
     +" ( ((? Subj) (? VP1) "+ "chCopiAnd"+"  (? Subj) (? VP2))))"  /***/

+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(either) s ))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
     +"  ( ((? Subj) (? VP1)"+ chOr+" (? Subj) (? VP2))))"

+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(or) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
     +"  (((? Subj) (? VP1)"+ chOr+" (? Subj) (? VP2))))"

+"(general-match ( ((? Subj) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(neither) s ))) ((? VP1) (lambda (s) (vp-p s))) ((? _) (lambda (s) (equal? '(nor) s ))) ((? VP2) (lambda (s) (vp-p s))) )"
     +"  ( "+ chNeg+" ((? Subj) (? VP1)"+ chOr+" (? Subj) (? VP2))))"
//21.1
+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? _) (lambda (s) (equal? '(both) s ))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s )))"
     +"  ((? Subj2) (lambda (s) (subject-p s))) ) (((? Term) (? RV) (? Subj1) "+ "chCopiAnd"+"  (? Term) (? RV) (? Subj2))))"

+"(general-match ( ((? Term) (lambda (s) (term-p s))) ((? RV) (lambda (s) (rv-p s))) ((? Subj1) (lambda (s) (subject-p s))) ((? _) (lambda (s) (equal? '(and) s ))) ((? Subj2) (lambda (s) (subject-p s))) ) "
     +" (((? Term) (? RV) (? Subj1) "+ "chCopiAnd"+"  (? Term) (? RV) (? Subj2))))"

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

}
