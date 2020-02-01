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



/* For the parsing of English to be a little more descriminating we use certain subroutines to sub-parse
some parts of speech.

These are written elsewhere, outside this project, in Scheme (LISP) then introduced here as functions,
The Scheme Interpreter in the English to Logic class then calls these as necessary.

The English To Logic class lispevaluates these strings, to set the lisp functions.


*/



public class TPartsOfSpeech{

  static String fNewGlobals = "(begin"

+"(define gEngKeyWords '(IT IS NOT THE CASE THAT BOTH AND EITHER OR NEITHER NOR IF THEN ONLY UNLESS ITSELF HIMSELF HERSELF A DOES))"

+"(define gUQuantWords '(EVERYTHING SOMETHING NOTHING))"

+"(define gRQuantWords '(EVERY SOME NO))"

+")";

static String fTokenizer = "(define (get-token-triple word-list)"

+"(if (null? word-list) (list '() 'endlist '()) (let ((firstWord (car word-list)) (key (list (car word-list))) (restWords (cdr word-list))"

+") (if (member firstWord gEngKeyWords) (list firstWord 'engword restWords) (if (member firstWord gUQuantWords) (list firstWord 'qu restWords) (if (member firstWord gRQuantWords) (list firstWord 'qr restWords)"

+"(if (assoc key gIntransitiveVerbs) (list firstWord 'iVerb restWords) (if (assoc key gNouns) (list firstWord 'Noun restWords) (if (or (assoc key gNames)(assoc key gEngVariables)) (list firstWord 'name restWords) (if (assoc key gAdjectives) (list firstWord 'adj restWords) (if (assoc key gTransitiveVerbs) (list firstWord 'tVerb restWords) (if (assoc key gBinaryAdjectives) (list firstWord 'binadj restWords)"

+"(if (not (null? restWords)) (let* ((secondWord (car restWords)) (firstTwoWords (list firstWord secondWord))) (if (assoc firstTwoWords gBinaryAdjectives) (list firstTwoWords 'binadj (cdr restWords)) (if (assoc firstTwoWords gPassiveVerbs) (list firstTwoWords 'pverb (cdr restWords)) (list firstWord 'unknownToken restWords) ) ) )"

+"(list firstWord 'unknownToken restWords)"

+") )) ) ))))) ) ) ) )";

  static String fAdjNoun= "(begin "

+"(define (adjnoun-parse word-list)(let ((top (adjnoun-top (get-token-triple word-list))))(if top(if (equal? (cadr top) 'endlist) #t #f)#f)))"

+"(define (adjnoun-top token-triple)(let ((next (consume-adjectives token-triple))     )(if (equal? (cadr next) 'noun)(get-token-triple (caddr next)) #f  ))))";

static String fBPhrase = "(begin "

+"(define (b-phrase-parse word-list) (let ((top (bp-top (get-token-triple word-list))) ) (if top (if (equal? (cadr top) 'endlist) #t #f) #f ) ) )"

+"(define (bp-top token-triple) (let ((tertiary (bp-tertiary token-triple)) ) (if tertiary (let ((next-token (car tertiary)) (next-type (cadr tertiary)) (next-wordlist (caddr tertiary)) (larger-bp #f) )(if (and (equal? next-type 'engword) (or (equal? next-token 'AND)(equal? next-token 'OR))) (set! larger-bp (bp-top (get-token-triple next-wordlist ))) ) (if larger-bp larger-bp tertiary ) ) #f ) ) )"

+"(define (bp-tertiary token-triple) (case (cadr token-triple) ((engword) (bp-tertiary-engword token-triple)) ((adj) (get-token-triple (caddr token-triple) )) ((pverb) (pverb-binadj-subroutine token-triple)) ((binadj) (pverb-binadj-subroutine token-triple))(else #f) ))"

+"(define (pverb-binadj-subroutine token-triple) (let ((next (get-token-triple (caddr token-triple ))) ) (if (and (equal?(cadr next) 'engword) (or (equal? (car next) 'ITSELF) (equal? (car next) 'HIMSELF) (equal? (car next) 'HERSELF) ) ) (get-token-triple (caddr next )) (subj-top next) ) ))"

+"(define (bp-tertiary-engword token-triple) (case (car token-triple) ((BOTH) (bp-bothand-type token-triple 'AND)) ((EITHER) (bp-bothand-type token-triple 'OR)) ((NEITHER) (bp-bothand-type token-triple 'NOR))((A) (np-top (get-token-triple (caddr token-triple )))) (else #f) ))"

+"(define (bp-bothand-type token-triple key2) (let ((next (if (equal? key2 'NOR) (bp-top (get-token-triple (caddr token-triple ))) (bp-tertiary (get-token-triple (caddr token-triple ))))) ) (if next (let ((next-token (car next)) (next-type (cadr next)) (next-wordlist (caddr next)) ) (if (equal? next-type 'endlist) #f (if (and (equal? next-type 'engword) (equal? next-token key2)) (bp-top (get-token-triple next-wordlist )) #f ) ) ) #f ) ) )"

+")";

static String fDPhrase="(begin "

+"(define (d-phrase-parse word-list) (let ((top (dp-top (get-token-triple word-list))) ) (if top (if (equal? (cadr top) 'endlist) #t #f) #f ) ) )"

+"(define (dp-top token-triple) (let ((tertiary (dp-tertiary token-triple)) ) (if tertiary (let ((next-token (car tertiary)) (next-type (cadr tertiary)) (next-wordlist (caddr tertiary)) (larger-dp #f) )(if (and (equal? next-type 'engword) (or (equal? next-token 'AND)(equal? next-token 'OR))) (set! larger-dp (dp-top (get-token-triple next-wordlist ))) ) (if larger-dp larger-dp tertiary ) ) #f ) ) )"

+"(define (dp-tertiary token-triple) (case (cadr token-triple) ((engword) (dp-tertiary-engword token-triple)) ((iverb) (get-token-triple (caddr token-triple) )) ((tverb) (let ((next (get-token-triple (caddr token-triple ))) ) (if (and (equal?(cadr next) 'engword) (or (equal? (car next) 'ITSELF) (equal? (car next) 'HIMSELF) (equal? (car next) 'HERSELF) ) ) (get-token-triple (caddr next )) (subj-top next) ) )) (else #f) ))"

+"(define (dp-tertiary-engword token-triple) (case (car token-triple) ((BOTH) (dp-bothand-type token-triple 'AND)) ((EITHER) (dp-bothand-type token-triple 'OR)) ((NEITHER) (dp-bothand-type token-triple 'NOR)) (else #f) ))"

+"(define (dp-bothand-type token-triple key2) (let ((next (if (equal? key2 'NOR) (dp-top (get-token-triple (caddr token-triple ))) (dp-tertiary (get-token-triple (caddr token-triple ))))) ) (if next (let ((next-token (car next)) (next-type (cadr next)) (next-wordlist (caddr next)) ) (if (equal? next-type 'endlist) #f (if (and (equal? next-type 'engword) (equal? next-token key2)) (dp-top (get-token-triple next-wordlist )) #f ) ) ) #f ) ) )"

+")";

static String fNounPhrase="(begin"

+"(define (np-parse word-list) (let ((top (np-top (get-token-triple word-list))) ) (if top (if (equal? (cadr top) 'endlist) #t #f) #f ) ) )"

+"(define (consume-adjectives token-triple) (if (equal? (cadr token-triple) 'adj) (consume-adjectives (get-token-triple (caddr token-triple))) token-triple ) )"

+"(define (np-top token-triple) (let ((next (consume-adjectives token-triple)) (next-but-one '(() 'endlist ())) ) (if (equal? (cadr next) 'noun) (begin (set! next-but-one (get-token-triple (caddr next))) (if (and (equal? (cadr next-but-one) 'engword) (equal? (car next-but-one) 'THAT)) (rel-clause-top next-but-one) next-but-one ) ) #f ) ) )"


+ ")";

static String fRelClause="(define (rel-clause-top token-triple) (if (and (equal? (cadr token-triple) 'engword) (equal? (car token-triple) 'THAT))(let* ((next (get-token-triple (caddr token-triple))) (alternative next) (verb-phrase (vp-top next)) ) (if verb-phrase verb-phrase (let ((subject (subj-top alternative)) ) (if subject (rv-top subject) #f ) ) ) ) #f ) )";

static String fRelVerb="(begin"

+"(define (relational-verb-parse word-list) (let ((top (rv-top (get-token-triple word-list))) ) (if top (if (equal? (cadr top) 'endlist) #t #f) #f ) ) )"

+"(define (rv-top token-triple) (case (cadr token-triple) ((tverb) (get-token-triple (caddr token-triple) )) ((engword) (if (equal? (car token-triple) 'IS) (let* ((next (get-token-triple (caddr token-triple) )) (next-type (cadr next))) (if (or (equal? next-type 'pverb) (equal? next-type 'binadj)) (get-token-triple (caddr next) ) #f ) ) #f ) ) (else #f) ) )"

+")";

  static String fSentence="(begin"

+"(define (sentence-parse word-list) (let ((top (sentence-top (get-token-triple word-list))) ) (if top (if (equal? (cadr top) 'endlist) #t #f) #f ) ) )"

+"(define (sentence-top token-triple)(let ((tertiary (sentence-tertiary token-triple)) ) (if tertiary (let ((next-token (car tertiary)) (next-type (cadr tertiary)) (next-wordlist (caddr tertiary)) ) (if (or (equal? next-token 'AND) (equal? next-token 'OR) (equal? next-token 'IF) (equal? next-token 'UNLESS) (equal? next-token 'ONLY) ) (if (or (equal? next-token 'AND) (equal? next-token 'OR) (equal? next-token 'UNLESS)) (sentence-top (get-token-triple next-wordlist )) (let* ((second-triple (get-token-triple next-wordlist )) (second-token (car second-triple)) (second-type (cadr second-triple)) (second-wordlist (caddr second-triple))(third-triple (get-token-triple second-wordlist )) (third-token (car third-triple)) (third-type (cadr third-triple)) (third-wordlist (caddr third-triple))(fourth-triple (get-token-triple third-wordlist )) (fourth-token (car fourth-triple)) (fourth-type (cadr fourth-triple)) (fourth-wordlist (caddr fourth-triple)) ) (if (and (equal? next-token 'IF) (not (equal? second-token 'AND))) (sentence-top second-triple) (if (and (equal? next-token 'ONLY) (equal? second-token 'IF)) (sentence-top third-triple) (if (and (equal? next-token 'IF) (equal? second-token 'AND) (equal? third-token 'ONLY) (equal? fourth-token 'IF)) (sentence-top (get-token-triple fourth-wordlist )) #f ) ) ) ) ) tertiary ) ) #f ) ) )"

+"(define (sentence-tertiary token-triple) (let ((sentence-try (sentence-tertiary-try token-triple)) ) (if sentence-try sentence-try (sentence-secondary token-triple)) ) )"

+"(define (sentence-tertiary-try token-triple) (case (car token-triple) ((BOTH) (sentence-bothand-type token-triple 'AND)) ((EITHER) (sentence-bothand-type token-triple 'OR)) ((NEITHER) (sentence-bothand-type token-triple 'NOR)) ((IF) (sentence-bothand-type token-triple 'THEN)) ((IT) (it-etc token-triple))(else (sentence-secondary token-triple)) ) )"

+"(define (it-etc token-triple) (let* ((second-triple (get-token-triple (caddr token-triple) )) (second-token (car second-triple)) (second-type (cadr second-triple)) (second-wordlist (caddr second-triple))(third-triple (get-token-triple second-wordlist )) (third-token (car third-triple)) (third-type (cadr third-triple)) (third-wordlist (caddr third-triple))(fourth-triple (get-token-triple third-wordlist )) (fourth-token (car fourth-triple)) (fourth-type (cadr fourth-triple)) (fourth-wordlist (caddr fourth-triple))(fifth-triple (get-token-triple fourth-wordlist )) (fifth-token (car fifth-triple)) (fifth-type (cadr fifth-triple)) (fifth-wordlist (caddr fifth-triple))(sixth-triple (get-token-triple fifth-wordlist )) (sixth-token (car sixth-triple)) (sixth-type (cadr sixth-triple)) (sixth-wordlist (caddr sixth-triple)) ) (if (and (equal? (car token-triple) 'IT) (equal? second-token 'IS) (equal? third-token 'NOT) (equal? fourth-token 'THE) (equal? fifth-token 'CASE) (equal? sixth-token 'THAT)) (sentence-top (get-token-triple sixth-wordlist )) #f ) ) )"

+"(define (sentence-bothand-type token-triple key2) (let ((next (if (or (equal? key2 'NOR) (equal? key2 'THEN)) (sentence-top (get-token-triple (caddr token-triple ))) (sentence-tertiary (get-token-triple (caddr token-triple ))))) ) (if next (let ((next-token (car next)) (next-type (cadr next)) (next-wordlist (caddr next)) ) (if (equal? next-type 'endlist) #f (if (and (equal? next-type 'engword) (equal? next-token key2)) (sentence-top (get-token-triple next-wordlist )) #f ) ) ) #f ) ) )"

+"(define (sentence-secondary token-triple) (let ((subject (subj-top token-triple)) ) (if subject (vp-top subject) #f ) ) )"

+")";

static String fSubject="(begin"

+"(define (subject-parse word-list) (let ((top (subj-top (get-token-triple word-list))) ) (if top (if (equal? (cadr top) 'endlist) #t #f) #f ) ) )"

+"(define (subj-top token-triple) (let ((tertiary (subj-tertiary token-triple)) ) (if tertiary (let ((next-token (car tertiary)) (next-type (cadr tertiary)) (next-wordlist (caddr tertiary)) ) (if (and (equal? next-type 'engword) (or (equal? next-token 'AND)(equal? next-token 'OR))) (subj-top (get-token-triple next-wordlist )) tertiary ) ) #f ) ) )"

+"(define (subj-tertiary token-triple) (case (cadr token-triple) ((engword) (subj-tertiary-engword token-triple)) ((name) (get-token-triple (caddr token-triple) )) ((qU) (qU token-triple)) ((qR) (np-top (get-token-triple (caddr token-triple) ))) (else #f) ))"

+"(define (subj-tertiary-engword token-triple)(case (car token-triple) ((BOTH) (subj-bothand-type token-triple 'AND)) ((EITHER) (subj-bothand-type token-triple 'OR)) ((NEITHER) (subj-bothand-type token-triple 'NOR)) (else #f) ))"

+"(define (subj-bothand-type token-triple key2)(let ((next (if (equal? key2 'NOR) (subj-top (get-token-triple (caddr token-triple ))) (subj-tertiary (get-token-triple (caddr token-triple ))))) ) (if next (let ((next-token (car next)) (next-type (cadr next)) (next-wordlist (caddr next)) ) (if (equal? next-type 'endlist) #f (if (and (equal? next-type 'engword) (equal? next-token key2)) (subj-top (get-token-triple next-wordlist )) #f ) ) ) #f ) ) )"

+"(define (qU token-triple) (let* ((next (get-token-triple (caddr token-triple))) (next-token (car next)) (next-type (cadr next)) ) (if (and (equal? next-type 'engword) (equal? next-token 'THAT)) (rel-clause-top next) next ) ) )"

+")";

static String fVerbPhrase="(begin"

+"(define (verb-phrase-parse word-list) (let ((top (vp-top (get-token-triple word-list))) ) (if top (if (equal? (cadr top) 'endlist) #t #f) #f ) ) )"

+"(define (vp-top token-triple) (let ((tertiary (vp-tertiary token-triple)) ) (if tertiary (let ((next-token (car tertiary)) (next-type (cadr tertiary)) (next-wordlist (caddr tertiary)) (larger-vp #f) )(if (and (equal? next-type 'engword) (or (equal? next-token 'AND)(equal? next-token 'OR))) (set! larger-vp (vp-top (get-token-triple next-wordlist ))) )(if larger-vp larger-vp tertiary ) ) #f ) ) )"

+"(define (vp-tertiary token-triple) (case (cadr token-triple) ((engword) (vp-tertiary-engword token-triple)) ((iverb) (get-token-triple (caddr token-triple) )) ((tverb) (let ((next (get-token-triple (caddr token-triple ))) ) (if (and (equal?(cadr next) 'engword) (equal? (car next) 'ITSELF)) (get-token-triple (caddr next )) (subj-top next) ) )) (else #f) ))"

+"(define (vp-tertiary-engword token-triple) (case (car token-triple) ((BOTH) (vp-bothand-type token-triple 'AND)) ((EITHER) (vp-bothand-type token-triple 'OR)) ((NEITHER) (vp-bothand-type token-triple 'NOR))((IS) (let ((next (get-token-triple (caddr token-triple ))) ) (if (and (equal?(cadr next) 'engword) (equal? (car next) 'NOT)) (set! next (get-token-triple (caddr next ))) ) (bp-top next) )) ((DOES) (let ((next (get-token-triple (caddr token-triple ))) ) (if (and (equal?(cadr next) 'engword) (equal? (car next) 'NOT)) (set! next (get-token-triple (caddr next ))) ) (dp-top next) ))(else #f) ))"

+"(define (vp-bothand-type token-triple key2) (let ((next (if (equal? key2 'NOR) (vp-top (get-token-triple (caddr token-triple ))) (vp-tertiary (get-token-triple (caddr token-triple ))))) ) (if next (let ((next-token (car next)) (next-type (cadr next)) (next-wordlist (caddr next)) ) (if (equal? next-type 'endlist) #f (if (and (equal? next-type 'engword) (equal? next-token key2)) (vp-top (get-token-triple next-wordlist )) #f ) ) ) #f ) ) )"

+")";

}
