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


/*look up the file us.softoption.games.TRandomProof.htm and use Dreamweaver to read some of this

 AND test them with testStringArray

 Also I have started randomFormulas files in MockUp->Tests->*/


/*Unfortunately, or part out of necessity, I am supplying these as strings (for load proof to load and away). But, first,
the different logical systems have different symbols eg ^ and & for 'and' so the different parsers won't be able to parse
 these strings. Then second the different systems have different rules, so what is a ~E in Gentzen is not a ~E in Bergmann. So
 I will subclass this and let each system have its own generator.  June 16 07*/

public class TBergmannRandomProof extends TRandomProof{

 public TBergmannRandomProof(){

   String [] newAndE={


  "R&S \u2234 R",

  "R&S \u2234 S",

  "((R&S)&(R&T)) \u2234 R&S",

  "R,(R&S)&(R&T),T \u2234 S",

  "R,S \u2234 R&S",

  "R,S \u2234 S&R",

  "R&S,R,T \u2234 (R&S)&(R&T)",

  "R,S,T \u2234 (R&S)&(R&T)",

  "R \u2234 R&R",

  "R&(S&T) \u2234 (R&S)&T",

  "(R&S)&(R&T) \u2234 R&(S&T)",

  " R,S&T,(A&B)&C \u2234 (A&S)&R",

  "R \u2234 R\u2228S",

  "R \u2234 S\u2228R",

  "R&S \u2234 S\u2228T",

  "R&S \u2234 R&(S\u2228T)"


    };


fSimpleNegEandEIorI  = newAndE;   //changing super


    String [] newNegE ={ "N&\u223CR,K&(F\u2283H),(\u223C U\u2228G)&\u223CJ \u2234 ((F\u2283H) &\u223CR)&(\u223C U\u2228G)",

        "(F&G)&(F&H) \u2234G&H",

        "F,(F&G)&(F&H)\u2234G\u2228H",

        "F&G,F,H \u2234 (G&F)&(F&H)",

        "F&(G&H) \u2234 (F&G)&H",

        "(F&G)&(F&H) \u2234 F&(G&H)",

        "F,G&H,(A&B)&C \u2234 (A&G)&F",

        "F&G \u2234 F&(G\u2228H)",
        " (F&H)&(R&G) \u2234G&F"
   };


fNegEandEIorI = newNegE;  //no subproofs








String SimpleImplicEEquivE  [] ={
         "M, M\u2283N \u2234 N",

       "M\u2283N, M \u2234 N&M",

       "M\u2283N, N\u2283O,M \u2234O",

       " M\u2283(N\u2283O), M\u2283N,M \u2234O&M",

       "M\u2283((~N)\u2283O), M\u2283~N,M \u2234O&M",

       "M\u2261N,N \u2234 M",          //altered Sept 21 07

       "M\u2261N,M \u2234 N&M",       //altered Sept 21 07


       "M\u2261N, N \u2234 O\u2228M",

       "M\u2261N, N\u2261O, M \u2234 O"

  };

  fSimpleImplicEEquivE =SimpleImplicEEquivE ;


  String ImplicEEquivE [] ={
      "F\u2283(G\u2283H), F\u2283G,F \u2234H&F ",
  "F\u2283((~G)\u2283H), F\u2283~G,F \u2234H&F",
  " F\u2261G, G \u2234 H\u2228F",
  "F\u2261G, G\u2261H, F \u2234 H ",
  "F\u2261G, H&G \u2234G&F"


};

  fImplicEEquivE=ImplicEEquivE;


  String ImplicI [] ={
    "(F\u2283G), (G\u2283H) \u2234 (F\u2283H) ","(F\u2283(G\u2283H)), (F\u2283G) \u2234 (F\u2283H)" ,
"(F\u2283G) \u2234 (F\u2283(F&G)) ",
"\u2234 (F&G)\u2283(G&F) ",
" \u2234 \u223C\u223CF\u2283(\u223CF\u2228F)",
" \u2234 (F\u2283(G\u2283H))\u2283((F&G)\u2283H)",
"\u2234 ((F&G)\u2283H)\u2283(F\u2283(G\u2283H)) ",

"\u2234 ((M&N)&O)\u2283(M&(N&O))",

"\u2234 (M\u2283(N&O))\u2283((M\u2283N)&(M\u2283O))"



};

fImplicI=ImplicI;

String SimpleNegI [] ={
    "F \u2234 \u223C(\u223CF)" ,
    "F \u2234 \u223C(\u223C(\u223C(\u223CF))) ",
    "\u223CF \u2234 \u223C(F&G)",
    " \u2234 F\u2283\u223C\u223CF",
    "F&\u223CG \u2234 \u223C(F\u2283G)",
    "F&\u223CG \u2234 \u223C(F\u2261G)",
    "\u2234\u223C(F&(\u223CF)) ",
"C&(\u223CF), A&((\u223CG)&B) \u2234 \u223C(F&G)"
};

fSimpleNegI=SimpleNegI;



String NegI [] ={
    "R &(\u223CR) \u2234 S" ,
"\u223C(R&S),R,S\u2234H",
"\u2234 R\u2228\u223CR",
" \u2234 R\u2283\u223C\u223CR",
"\u2234 (R\u2283S)\u2283(\u223CS\u2283\u223CR)",
"\u2234(\u223CS\u2283\u223CR)\u2283(R\u2283S)",
"\u223C(R&S)\u2234\u223CR\u2228\u223CS",
"\u2234 \u223C(R\u2228S)\u2283(\u223CR&\u223CS)",

"\u2234 (R\u2283S)\u2283(\u223CR\u2228S)"

};

fNegI=NegI;

String OrEEquivI [] ={

    "(F&F)\u2228(G&G),F\u2283H,G\u2283H\u2234H" ,
"F\u2228G \u2234G\u2228F",
"(F\u2283G)&(H\u2283D), F\u2228H \u2234 G\u2228D",
" F\u2228(G&\u223CG)\u2234 F",
"F\u2228G, G\u2283H \u2234 \u223CF\u2283H",
"\u223CF&\u223CG\u2234 \u223C(F\u2228G) ",
"\u2234 (\u223CF&\u223CG)\u2283\u223C(F\u2228G) ",
"\u2234 (F&(G\u2228H))\u2283((F&G)\u2228(F&H))",

"\u2234 ((F\u2228G)&(F\u2228H))\u2283(F\u2228(G&H))" ,
"\u2234 (F&(G\u2228H))\u2283((F&G)\u2228(F&H))",   ///
"\u2234 ((F\u2228G)&(F\u2228H))\u2283(F\u2228(G&H))",
"\u2234 (\u223CF\u2228G)\u2283(F\u2283G)",
"\u2234 (F&F)\u2261F ",
"\u2234 (F\u2228F)\u2261F ",
"\u2234 F\u2228(G\u2228H)\u2261(F\u2228G)\u2228H",
"\u2234 F\u2228G\u2261G\u2228F"


};

fOrEEquivI=OrEEquivI;

String TwelveLineProp []={

    "N&\u223CR,K&(F\u2283H),(~U\u2228G)&\u223CJ \u2234 ((F\u2283H) &~~~R)&(~U\u2228G)",

    "(F&~~G)&(F&H) \u2234G&H" ,

    "F,(F&G)&(F&H)\u2234~~G\u2228~~H",

    "F&G,F,~~H \u2234 (G&F)&(F&H)",

    "F&(G&H) \u2234 (F&~~G)&H",

    "(F&G)&(F&H) \u2234 F&(~~G&H)",

    "F,G&H,(~~A&B)&C \u2234 (A&G)&F",

    "F&G \u2234 ~~F&(G\u2228H)",

    " (F&H)&(R&~~G) \u2234G&F",

    "\u2234 (F\u2228G)\u2283(G\u2228F)",

    "\u2234 G\u2283~~G",

    "\u2234 G\u2228~G",

    "\u2234 (\u223CF\u2228G)\u2283(F\u2283G)",

    "\u2234 (\u223CF&\u223CG)\u2283\u223C(F\u2228G)" ,

    "\u223CF&\u223CG\u2234 \u223C(F\u2228G) ",

    "F\u2228G, G\u2283H \u2234 \u223CF\u2283H",

    " F\u2228(G&\u223CG)\u2234 F",

    "(F\u2283G)&(H\u2283D), F\u2228H \u2234 G\u2228D",

    "(F&F)\u2228(G&G),F\u2283H,G\u2283H\u2234H",

    "\u2234 ((F&G)\u2283H)\u2283(F\u2283(G\u2283H))",

    " (Z&W)\u2283(L\u2228K),(W&Z)\u2234K\u2228 L",

"D\u2283E, E\u2283(Z&W),~Z,~W\u2234~D"


/*    Taken out Nov 29 07

    "\u2234 F\u2228G\u2261G\u2228F",
    "\u2234 (\u223CF\u2228G)\u2283(F\u2283G)",
    "\u2234 (\u223CF&\u223CG)\u2283\u223C(F\u2228G) " ,

"\u223CF&\u223CG\u2234 \u223C(F\u2228G) ",


    "F\u2228G, G\u2283H \u2234 \u223CF\u2283H",   //wed

" F\u2228(G&\u223CG)\u2234 F",

"(F\u2283G)&(H\u2283D), F\u2228H \u2234 G\u2228D",  //ill formed

    "(F&F)\u2228(G&G),F\u2283H,G\u2283H\u2234H",

    "\u2234 ((F&G)\u2283H)\u2283(F\u2283(G\u2283H))",  //thurs

    "(Z&W)\u2283(L\u2228K),(W&Z)\u2234K\u2228L"  //sat ill formed   */


};

fTwelveLineProp=TwelveLineProp;  //changing super



String TwelveLinePredNoQuant []={
    "\u2234 Fa\u2228Gb\u2261Gb\u2228Fa",
    "\u2234 (\u223CFa\u2228Gb)\u2283(Fa\u2283Gb)",
    "\u2234 (\u223CFa&\u223CGb)\u2283\u223C(Fa\u2228Gb) " ,

"\u223CFa&\u223CGb\u2234 \u223C(Fa\u2228Gb) ",


    "Fa\u2228Gb, Gb\u2283Hc \u2234 \u223CFa\u2283Hc",

" Fa\u2228(Gb&\u223CGb)\u2234 Fa",

"(Fa\u2283Gb)&(Hc\u2283D), Fa\u2228Hc \u2234 Gb\u2228D",

    "(Fa&Fa)\u2228(Gb&Gb),Fa\u2283Hc,Gb\u2283Hc\u2234Hc",

    "\u2234 ((Fa&Gb)\u2283Hc)\u2283(Fa\u2283(Gb\u2283Hc))",

    "(Za&W)\u2283(La\u2228Kb),(W&Za)\u2234Kb\u2228La"


};

fTwelveLinePredNoQuant=TwelveLinePredNoQuant;


String SimpleUI []={"(\u2200x) (Fx) \u2234 Fa&Fb ",

      "Fc,(\u2200x) (Fx\u2283Gx) \u2234 Gc ",

      "Fa,(\u2200x) (Fx\u2283Gx), (\u2200x) (Gx\u2283Hx) \u2234Ha ",

      "(\u2200x) (Fx\u2283Gx), (\u2200x) (Gx\u2283Hx) \u2234(Fa\u2283Ha) "
};

 fSimpleUI=SimpleUI;

String SimpleUG []={"(\u2200x) (Fx&Gx) \u2234 (\u2200x)Fx" ,

"(\u2200x) (Fx&Gx) \u2234 (\u2200y)Fy" ,

"\u2234 (\u2200x)Fx\u2261(\u2200y)Fy" ,

"(\u2200x) (Fx&Gx) \u2234 (\u2200y)Fy&(\u2200z)Gz" ,

"\u2234 (\u2200x) (Fx&Gx) \u2283 (\u2200y)Fy&(\u2200z)Gz" ,
      "\u2234 ((\u2200y)Fy&(\u2200z)Gz)\u2283 (\u2200x) (Fx&Gx)",

"\u2234 (\u2200x) (Fx&Gx) \u2261 (\u2200x)Fx&(\u2200x)Gx"

};


 fSimpleUG=SimpleUG;


String SimpleEG []={"Fa,Ga\u2234(\u2203x)(Fx&Gx) ",

"Fa,Ga\u2234(\u2203x)(Fx&Ga) ",

"Fa,Ga\u2234(\u2203x)(Fa&Gx) ",

"Fa,Ga\u2234(\u2203x)(Fx)&Ga "

  };
  fSimpleEG=SimpleEG;


String SimpleEI []={"(\u2203x) (Fx&Gx) \u2234 (\u2203x)(Fx ) ",

"(\u2203x) (Fx&Gx) \u2234 (\u2203y)(Gy) ",

"(\u2203x)(Fx)\u2234 (\u2203y)(Fy) ",

"(\u2203x)(Fx&Gx) \u2234 (\u2203y)(Fy)&(\u2203z)(Gz)"


  };
  fSimpleEI=SimpleEI;


String TenLinePred []={
     "\u2234((\u2200x)(\u2200y)Fxy)\u2261((\u2200y)(\u2200x)Fxy)",

"\u2234((\u2203x)Fx)\u2283(\u223C(\u2200x)\u223CFx)",

"\u2234(\u223C(\u2203x)\u223CFx)\u2283((\u2200x)Fx)",

"\u2234(\u2200x)(Fx\u2283Gx)\u2283((\u2200x)Fx\u2283(\u2200x)Gx)",

"\u2234(\u2200x)(Fx\u2283Gx)\u2283((\u2203x)Fx\u2283(\u2203x)Gx)",

"(\u2200x)(Fx\u2283\u223CGx), (\u2203x)(Hx&Fx)\u2234 (\u2203x)(Hx&\u223CGx)",

"(\u2200x)(Fx\u2283\u223CGx), (\u2200x)(Hx\u2283Gx)\u2234 (\u2200x)(Hx\u2283\u223CFx)",

"(\u2200x)(Fx\u2283\u223CGx),(\u2200x)(Hx\u2283Gx)\u2234(\u2200x)(Fx\u2283\u223CHx)",

"(\u2203x)(Jx&Kx),(\u2200x)(Jx\u2283Lx)\u2234(\u2203x)(Lx&Kx)",

"(\u2200x)(Mx\u2283Nx),(\u2203x)(Mx&Ox)\u2234(\u2203x)(Ox&Nx)",

"(\u2203x)(Px&\u223CQx),(\u2200x)(Px\u2283Rx)\u2234(\u2203x)(Rx&\u223CQx)",

"(\u2200x)(Sx\u2283\u223CTx),(\u2203x)(Sx&Ux)\u2234(\u2203x)(Ux&\u223CTx)",

"(\u2200x)(Vx\u2283Wx),(\u2200x)(Wx\u2283\u223CXx)\u2234(\u2200x)(Xx\u2283\u223CVx)",

"(\u2203x)(Yx&Zx),(\u2200x)(Zx\u2283Ax)\u2234(\u2203x)(Ax&Yx)",

"(\u2200x)(Bx\u2283\u223CCx),(\u2203x)(Cx&Dx)\u2234(\u2203x)(Dx& \u223CBx)",

"(\u2200x)(Fx\u2283Gx),(\u2203x)(Fx&\u223CGx)\u2234(\u2203x)(Gx& \u223CFx)"


 };

 fTenLinePred =TenLinePred;


String AnyAnyLevel []=
     {

     "\u2234 (F\u2228(F&G))\u2261F",
     "\u2234 (F&(F\u2228G))\u2261F",
     "\u2234 (F\u2283(F\u2283G))\u2261(F\u2283G)",
     "\u2234 F\u2228(G\u2228H)\u2261(F\u2228G)\u2228H",
     "\u2234 F&(G&H)\u2261(F&G)&H ",
     "\u2234 F\u2228G\u2261G\u2228F",
     "\u2234 F&G\u2261G&F",
     "\u2234 (F\u2261G)\u2261(G\u2261F)",
     "\u2234 F\u2283(G\u2283H)\u2261G\u2283(F\u2283H) ",
     "\u2234(F\u2283G)\u2261(\u223CG\u2283\u223CF) ",
     "\u2234 \u223C(F&G)\u2261\u223CF\u2228\u223CG",
     "\u2234 \u223C(F\u2228G)\u2261\u223CF&\u223CG ",
     "\u2234 F&(G\u2228H)\u2261(F&G)\u2228(F&H)",
     "\u2234 F\u2228(G&H)\u2261(F\u2228G)&(F\u2228H) ",
     "\u2234 F\u2261\u223C\u223CF ",
     "\u2234 (F\u2261G)\u2261(F\u2283G)&(G\u2283F)",
     "\u2234 (F\u2261G)\u2261(F&G)\u2228(\u223CF&\u223CG) ",
     "\u2234 F\u2228\u223CF ",
     "\u2234 (F\u2283(G\u2283H))\u2261((F&G)\u2283H) ",
     "\u2234 (F&F)\u2261F",
     "\u2234 (F\u2228F)\u2261F ",
     "\u2234 (F\u2283G)\u2261(\u223CF\u2228G)",
     "\u2234 (F\u2283G)\u2261\u223C(F&\u223CG)",
     "\u2234 (F\u2228G)\u2261(\u223CF\u2283G)",
     "\u2234 (F&G)\u2261\u223C(F\u2283\u223CG)",
     "\u2234 (F\u2261G)\u2261 ((F\u2283G) & (G\u2283F)) ",
     " F\u2283G,G\u2283H\u2234F\u2283H ",
     "\u2234 F\u2283G\u2261\u223CG\u2283\u223CF",
     "\u2234 ((\u2200x)F)\u2261F ",
     "\u2234 ((\u2200x)Fx)\u2261((\u2200y)Fy) ",
     "\u2234 ((\u2200x)(\u2200y)Fxy)\u2261((\u2200y)(\u2200x)Fxy) ",
     "\u2234 ((\u2203x)(\u2203y)Fxy)\u2261((\u2203y)(\u2203x)Fxy) ",
     "\u2234 ((\u2203x)(\u2200y)Fxy)\u2283((\u2200y)(\u2203x)Fxy) ",
     "\u2234 ((\u2203x)Fx)\u2261(\u223C(\u2200x)\u223CFx) ",
     "\u2234 (\u223C(\u2203x)\u223CFx)\u2261((\u2200x)Fx) ",
     "\u2234 (\u2200x)(Fx\u2261Gx)\u2283((\u2200x)Fx\u2261(\u2200x)Gx) ",
     "\u2234 (\u2200x)(Fx\u2261Gx)\u2283((\u2203x)Fx\u2261(\u2203x)Gx) ",
     "\u2234 (\u2200x)(Fx\u2283Gx)\u2283((\u2200x)Fx\u2283(\u2200x)Gx) ",
     "\u2234 (\u2200x)(Fx\u2283Gx)\u2283((\u2203x)Fx\u2283(\u2203x)Gx) ",
     "\u2234 ((\u2200x)Fx\u2283(\u2200x)Gx)\u2283(\u2203x)(Fx\u2283Gx) ",
     "\u2234 (\u2200x)(Fx&Gx)\u2261((\u2200x)Fx&(\u2200x)Gx) ",
     "\u2234 (\u2200x)(Fx\u2228Gx)\u2283((\u2200x)Fx\u2228(\u2203x)Gx) ",
     "\u2234 ((\u2200x)Fx\u2228(\u2200x)Gx)\u2283(\u2200x)(Fx\u2228Gx) ",
     "\u2234 ((\u2203x)Fx\u2228(\u2203x)Gx)\u2261(\u2203x)(Fx\u2228Gx)"};
fAnyAnyLevel=AnyAnyLevel;

String Identity []=
     {"a=b \u2234 b=a" ,
"Gab, a=b \u2234 Gaa&Gbb",
"a=b,b=c \u2234 a=c&c=a",
"(\u2200x)Kx \u2234 Kf(b)",
"(\u2200x)(Fx\u2283 Ff(x)),Fa \u2234 Ff(a)",
"\u2234 (\u2200x)(x=x)",
"\u2234 (\u2200x)(\u2200y)((x=y)\u2283(y=x))",
"\u2234 (\u2200x)(\u2200y)(\u2200z)(((x=y)&(y=z))\u2283(x=z))"

 };
 fIdentity=Identity;


  }

public static void testStringArray (String[] stringArray)

// use this to test whether any statics you produce are well formed
{

// comment back in when needed-- out for applets

/*
  int available=stringArray.length;


TBergmannDocument document = new TBergmannDocument();           //i)   the document
TBrowser browser = new TBrowser(document,new TDeriverApplication());           //ii)  the browser
document.setJournal(browser);                    //iii) the back reference


   TMyBergmannProofPanel aPanel=new TMyBergmannProofPanel(document);

  for (int i=0;i<available;i++){
    String inStr=stringArray[i];

    if (!aPanel.load(TUtilities.logicFilter(inStr)))
    {
     System.out.print(inStr + " " +"No Good"+ i);
    }
    else
      System.out.print("Strings OK");
      ;

  }
*/
}



}
