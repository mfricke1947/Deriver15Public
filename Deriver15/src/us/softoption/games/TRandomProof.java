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




//import us.softoption.games.*;


/*look up the file us.softoption.games.TRandomProof.htm and use Dreamweaver to read some of this

 AND test them with testStringArray

 Also I have started randomFormulas files in MockUp->Tests->*/


/*Unfortunately, or part out of necessity, I am supplying these as strings (for load proof to load and away). But, first,
the different logical systems have different symbols eg ^ and & for 'and' so the different parsers won't be able to parse
 these strings. Then second the different systems have different rules, so what is a ~E in Gentzen is not a ~E in Bergmann. So
 I will subclass this and let each system have its own generator.  June 16 07*/



public class TRandomProof{

 static String Therefore=""+'\u2234';

  static String fSimpleImplicEEquivE  [] ={       /* F \u2234 F & G                */
      "M, M\u2283N "+Therefore+" N",

    "M\u2283N, M \u2234 N\u2227M",

    "M\u2283N, N\u2283O,M \u2234O",

    " M\u2283(N\u2283O), M\u2283N,M \u2234O\u2227M",

    "M\u2283((~N)\u2283O), M\u2283~N,M \u2234O\u2227M",

    "M\u2261N \u2234 N\u2283M",

    " M\u2261N \u2234 M\u2283N",

    "M\u2261N, N \u2234 O\u2228M",

    "M\u2261N, N\u2261O, M \u2234 O"

  };

  static String fSimpleNegEandEIorI [] ={
      "\u223C\u223CR\u2234R",

"(\u223C(\u223CR))\u2234R",

"R, \u223C\u223C\u223C\u223CS,T\u2234S",

"R\u2227S \u2234 R",

"R\u2227S \u2234 S",

" \u223C\u223C((R\u2227S)\u2227(R\u2227T)) \u2234 R\u2227S",

"R,(R\u2227\u223C\u223CS)\u2227(R\u2227T),T \u2234 S",

"R,S \u2234 R\u2227S",

"R,S \u2234 S\u2227R",

"R\u2227S,R,\u223C\u223CT \u2234 (R\u2227S)\u2227(R\u2227T)",

"R,S,T \u2234 (R\u2227S)\u2227(R\u2227T)",

"R \u2234 R\u2227R",

"R\u2227(S\u2227T) \u2234 (R\u2227S)\u2227T",

"(R\u2227S)\u2227(R\u2227T) \u2234 R\u2227(S\u2227T)",

" R,S\u2227T,(A\u2227B)\u2227C \u2234 (A\u2227S)\u2227R",

"R \u2234 R\u2228S",

"R \u2234 S\u2228R",

"R\u2227S \u2234 S\u2228T",

"R\u2227\u223C\u223CS \u2234 R\u2227(S\u2228T)"


  };

  static String fNegEandEIorI [] ={ //use Dreamweaver, JBuilder converts to unicode on close and open
      "N\u2227\u223CR,K\u2227(F\u2283H),(\u223C U\u2228G)\u2227\u223CJ \u2234 ((F\u2283H) \u2227\u223CR)\u2227(\u223C U\u2228G)",

   "\u223C\u223C((F\u2227G)\u2227(F\u2227H)) \u2234G\u2227H" ,

   "F,(F\u2227\u223C\u223CG)\u2227(F\u2227H)\u2234G\u2228H",

   "F\u2227G,F,\u223C\u223CH \u2234 (G\u2227F)\u2227(F\u2227H)",

   "F\u2227(G\u2227H) \u2234 (F\u2227G)\u2227H",

   "(F\u2227G)\u2227(F\u2227H) \u2234 F\u2227(G\u2227H)",

   "F,G\u2227H,(A\u2227B)\u2227C \u2234 (A\u2227G)\u2227F",

   "F\u2227\u223C\u223CG \u2234 F\u2227(G\u2228H)",
   " (F\u2227H)\u2227(R\u2227G) \u2234G\u2227F"
}

;

static String fImplicEEquivE [] ={
    "F\u2283(G\u2283H), F\u2283G,F \u2234H\u2227F ",
"F\u2283((~G)\u2283H), F\u2283~G,F \u2234H\u2227F",
" F\u2261G, G \u2234 H\u2228F",
"F\u2261G, G\u2261H, F \u2234 H ",
"F\u2261G, H\u2227G \u2234G\u2227F"


};

static String fImplicI [] ={
    "(F\u2283G), (G\u2283H) \u2234 (F\u2283H) ","(F\u2283(G\u2283H)), (F\u2283G) \u2234 (F\u2283H)" ,
"(F\u2283G) \u2234 (F\u2283(F\u2227G)) ",
"\u2234 (F\u2227G)\u2283(G\u2227F) ",
" \u2234 \u223C\u223CF\u2283(\u223CF\u2228F)",
" \u2234 (F\u2283(G\u2283H))\u2283((F\u2227G)\u2283H)",
"\u2234 ((F\u2227G)\u2283H)\u2283(F\u2283(G\u2283H)) ",

"\u2234 ((M\u2227N)\u2227O)\u2283(M\u2227(N\u2227O))",

"\u2234 (M\u2283(N\u2227O))\u2283((M\u2283N)\u2227(M\u2283O))"



};

static String fSimpleNegI [] ={
    "F \u2234 \u223C(\u223CF)" ,
    "F \u2234 \u223C(\u223C(\u223C(\u223CF))) ",
    "\u223CF \u2234 \u223C(F\u2227G)",
    " \u2234 F\u2283\u223C\u223CF",
    "F\u2227\u223CG \u2234 \u223C(F\u2283G)",
    "F\u2227\u223CG \u2234 \u223C(F\u2261G)",
    "\u2234\u223C(F\u2227(\u223CF)) ",
"C\u2227(\u223CF), A\u2227((\u223CG)\u2227B) \u2234 \u223C(F\u2227G)"
};

static String fNegI [] ={
    "R \u2227(\u223CR) \u2234 S" ,
"\u223C(R\u2227S),R,S\u2234H",
"\u2234 R\u2228\u223CR",
" \u2234 R\u2283\u223C\u223CR",
"\u2234 (R\u2283S)\u2283(\u223CS\u2283\u223CR)",
"\u2234(\u223CS\u2283\u223CR)\u2283(R\u2283S)",
"\u223C(R\u2227S)\u2234\u223CR\u2228\u223CS",
"\u2234 \u223C(R\u2228S)\u2283(\u223CR\u2227\u223CS)",

"\u2234 (R\u2283S)\u2283(\u223CR\u2228S)"

};

static String fOrEEquivI [] ={

    "(F\u2227F)\u2228(G\u2227G),F\u2283H,G\u2283H\u2234H" ,
"F\u2228G \u2234G\u2228F",
"(F\u2283G)\u2227(H\u2283D), F\u2228H \u2234 G\u2228D",
" F\u2228(G\u2227\u223CG)\u2234 F",
"F\u2228G, G\u2283H \u2234 \u223CF\u2283H",
"\u223CF\u2227\u223CG\u2234 \u223C(F\u2228G) ",
"\u2234 (\u223CF\u2227\u223CG)\u2283\u223C(F\u2228G) ",
"\u2234 (F\u2227(G\u2228H))\u2283((F\u2227G)\u2228(F\u2227H))",

"\u2234 ((F\u2228G)\u2227(F\u2228H))\u2283(F\u2228(G\u2227H))" ,
"\u2234 (F\u2227(G\u2228H))\u2283((F\u2227G)\u2228(F\u2227H))",   ///
"\u2234 ((F\u2228G)\u2227(F\u2228H))\u2283(F\u2228(G\u2227H))",
"\u2234 (\u223CF\u2228G)\u2283(F\u2283G)",
"\u2234 (F\u2227F)\u2261F ",
"\u2234 (F\u2228F)\u2261F ",
"\u2234 F\u2228(G\u2228H)\u2261(F\u2228G)\u2228H",
"\u2234 F\u2228G\u2261G\u2228F"


};

public static String fTwelveLineProp []={
    "\u2234 F\u2228G\u2261G\u2228F",
    "\u2234 (\u223CF\u2228G)\u2283(F\u2283G)",
    "\u2234 (\u223CF\u2227\u223CG)\u2283\u223C(F\u2228G) " ,

"\u223CF\u2227\u223CG\u2234 \u223C(F\u2228G) ",


    "F\u2228G, G\u2283H \u2234 \u223CF\u2283H",   //wed

" F\u2228(G\u2227\u223CG)\u2234 F",

"(F\u2283G)\u2227(H\u2283D), F\u2228H \u2234 G\u2228D",  //ill formed

    "(F\u2227F)\u2228(G\u2227G),F\u2283H,G\u2283H\u2234H",

    "\u2234 ((F\u2227G)\u2283H)\u2283(F\u2283(G\u2283H))",  //thurs

    "(Z\u2227W)\u2283(L\u2228K),(W\u2227Z)\u2234K\u2228L"  //sat ill formed


};

static String fTwelveLinePredNoQuant []={
    "\u2234 Fa\u2228Gb\u2261Gb\u2228Fa",
    "\u2234 (\u223CFa\u2228Gb)\u2283(Fa\u2283Gb)",
    "\u2234 (\u223CFa\u2227\u223CGb)\u2283\u223C(Fa\u2228Gb) " ,

"\u223CFa\u2227\u223CGb\u2234 \u223C(Fa\u2228Gb) ",


    "Fa\u2228Gb, Gb\u2283Hc \u2234 \u223CFa\u2283Hc",

" Fa\u2228(Gb\u2227\u223CGb)\u2234 Fa",

"(Fa\u2283Gb)\u2227(Hc\u2283D), Fa\u2228Hc \u2234 Gb\u2228D",

    "(Fa\u2227Fa)\u2228(Gb\u2227Gb),Fa\u2283Hc,Gb\u2283Hc\u2234Hc",

    "\u2234 ((Fa\u2227Gb)\u2283Hc)\u2283(Fa\u2283(Gb\u2283Hc))",

    "(Za\u2227W)\u2283(La\u2228Kb),(W\u2227Za)\u2234Kb\u2228La"


};

static String fSimpleUI []={"(\u2200x) (Fx) \u2234 Fa\u2227Fb ",

      "Fc,(\u2200x) (Fx\u2283Gx) \u2234 Gc ",

      "Fa,(\u2200x) (Fx\u2283Gx), (\u2200x) (Gx\u2283Hx) \u2234Ha ",

      "(\u2200x) (Fx\u2283Gx), (\u2200x) (Gx\u2283Hx) \u2234(Fa\u2283Ha) "
};

  static String fSimpleUG []={"(\u2200x) (Fx\u2227Gx) \u2234 (\u2200x)Fx" ,

"(\u2200x) (Fx\u2227Gx) \u2234 (\u2200y)Fy" ,

"\u2234 (\u2200x)Fx\u2261(\u2200y)Fy" ,

"(\u2200x) (Fx\u2227Gx) \u2234 (\u2200y)Fy\u2227(\u2200z)Gz" ,

"\u2234 (\u2200x) (Fx\u2227Gx) \u2283 (\u2200y)Fy\u2227(\u2200z)Gz" ,
      "\u2234 ((\u2200y)Fy\u2227(\u2200z)Gz)\u2283 (\u2200x) (Fx\u2227Gx)",

"\u2234 (\u2200x) (Fx\u2227Gx) \u2261 (\u2200x)Fx\u2227(\u2200x)Gx"

};

  static String fSimpleEG []={"Fa,Ga\u2234(\u2203x)(Fx\u2227Gx) ",

"Fa,Ga\u2234(\u2203x)(Fx\u2227Ga) ",

"Fa,Ga\u2234(\u2203x)(Fa\u2227Gx) ",

"Fa,Ga\u2234(\u2203x)(Fx)\u2227Ga "

  };

  static String fSimpleEI []={"(\u2203x) (Fx\u2227Gx) \u2234 (\u2203x)(Fx ) ",

"(\u2203x) (Fx\u2227Gx) \u2234 (\u2203y)(Gy) ",

"(\u2203x)(Fx)\u2234 (\u2203y)(Fy) ",

"(\u2203x)(Fx\u2227Gx) \u2234 (\u2203y)(Fy)\u2227(\u2203z)(Gz)"


  };


 static String fTenLinePred []={
     "\u2234((\u2200x)(\u2200y)Fxy)\u2261((\u2200y)(\u2200x)Fxy)",

"\u2234((\u2203x)Fx)\u2283(\u223C(\u2200x)\u223CFx)",

"\u2234(\u223C(\u2203x)\u223CFx)\u2283((\u2200x)Fx)",

"\u2234(\u2200x)(Fx\u2283Gx)\u2283((\u2200x)Fx\u2283(\u2200x)Gx)",

"\u2234(\u2200x)(Fx\u2283Gx)\u2283((\u2203x)Fx\u2283(\u2203x)Gx)",

"(\u2200x)(Fx\u2283\u223CGx), (\u2203x)(Hx\u2227Fx)\u2234 (\u2203x)(Hx\u2227\u223CGx)",

"(\u2200x)(Fx\u2283\u223CGx), (\u2200x)(Hx\u2283Gx)\u2234 (\u2200x)(Hx\u2283\u223CFx)",

"(\u2200x)(Fx\u2283\u223CGx),(\u2200x)(Hx\u2283Gx)\u2234(\u2200x)(Fx\u2283\u223CHx)",

"(\u2203x)(Jx\u2227Kx),(\u2200x)(Jx\u2283Lx)\u2234(\u2203x)(Lx\u2227Kx)",

"(\u2200x)(Mx\u2283Nx),(\u2203x)(Mx\u2227Ox)\u2234(\u2203x)(Ox\u2227Nx)",

"(\u2203x)(Px\u2227\u223CQx),(\u2200x)(Px\u2283Rx)\u2234(\u2203x)(Rx\u2227\u223CQx)",

"(\u2200x)(Sx\u2283\u223CTx),(\u2203x)(Sx\u2227Ux)\u2234(\u2203x)(Ux\u2227\u223CTx)",

"(\u2200x)(Vx\u2283Wx),(\u2200x)(Wx\u2283\u223CXx)\u2234(\u2200x)(Xx\u2283\u223CVx)",

"(\u2203x)(Yx\u2227Zx),(\u2200x)(Zx\u2283Ax)\u2234(\u2203x)(Ax\u2227Yx)",

"(\u2200x)(Bx\u2283\u223CCx),(\u2203x)(Cx\u2227Dx)\u2234(\u2203x)(Dx\u2227 \u223CBx)",

"(\u2200x)(Fx\u2283Gx),(\u2203x)(Fx\u2227\u223CGx)\u2234(\u2203x)(Gx\u2227 \u223CFx)"


 };


static String fAnyAnyLevel []=
     {

     "\u2234 (F\u2228(F\u2227G))\u2261F",
     "\u2234 (F\u2227(F\u2228G))\u2261F",
     "\u2234 (F\u2283(F\u2283G))\u2261(F\u2283G)",
     "\u2234 F\u2228(G\u2228H)\u2261(F\u2228G)\u2228H",
     "\u2234 F\u2227(G\u2227H)\u2261(F\u2227G)\u2227H ",
     "\u2234 F\u2228G\u2261G\u2228F",
     "\u2234 F\u2227G\u2261G\u2227F",
     "\u2234 (F\u2261G)\u2261(G\u2261F)",
     "\u2234 F\u2283(G\u2283H)\u2261G\u2283(F\u2283H) ",
     "\u2234(F\u2283G)\u2261(\u223CG\u2283\u223CF) ",
     "\u2234 \u223C(F\u2227G)\u2261\u223CF\u2228\u223CG",
     "\u2234 \u223C(F\u2228G)\u2261\u223CF\u2227\u223CG ",
     "\u2234 F\u2227(G\u2228H)\u2261(F\u2227G)\u2228(F\u2227H)",
     "\u2234 F\u2228(G\u2227H)\u2261(F\u2228G)\u2227(F\u2228H) ",
     "\u2234 F\u2261\u223C\u223CF ",
     "\u2234 (F\u2261G)\u2261(F\u2283G)\u2227(G\u2283F)",
     "\u2234 (F\u2261G)\u2261(F\u2227G)\u2228(\u223CF\u2227\u223CG) ",
     "\u2234 F\u2228\u223CF ",
     "\u2234 (F\u2283(G\u2283H))\u2261((F\u2227G)\u2283H) ",
     "\u2234 (F\u2227F)\u2261F",
     "\u2234 (F\u2228F)\u2261F ",
     "\u2234 F\u2283G\u2261\u223CF\u2228G ",
     "\u2234 (F\u2283G)\u2261(\u223CF\u2228G)",
     "\u2234 (F\u2283G)\u2261\u223C(F\u2227\u223CG)",
     "\u2234 (F\u2228G)\u2261(\u223CF\u2283G)",
     "\u2234 (F\u2227G)\u2261\u223C(F\u2283\u223CG)",
     "\u2234 (F\u2261G)\u2261 ((F\u2283G) \u2227 (G\u2283F)) ",
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
     "\u2234 (\u2200x)(Fx\u2227Gx)\u2261((\u2200x)Fx\u2227(\u2200x)Gx) ",
     "\u2234 (\u2200x)(Fx\u2228Gx)\u2283((\u2200x)Fx\u2228(\u2203x)Gx) ",
     "\u2234 ((\u2200x)Fx\u2228(\u2200x)Gx)\u2283(\u2200x)(Fx\u2228Gx) ",
     "\u2234 ((\u2203x)Fx\u2228(\u2203x)Gx)\u2261(\u2203x)(Fx\u2228Gx)"};


 public static String fIdentity []=
     {"a=b \u2234 b=a" ,
"Gab, a=b \u2234 Gaa\u2227Gbb",
"a=b,b=c \u2234 a=c\u2227c=a",
"(\u2200x)Kx \u2234 Kf(b)",
"(\u2200x)(Fx\u2283 Ff(x)),Fa \u2234 Ff(a)",
"\u2234 (\u2200x)(x=x)",
"\u2234 (\u2200x)(\u2200y)((x=y)\u2283(y=x))",
"\u2234 (\u2200x)(\u2200y)(\u2200z)(((x=y)\u2227(y=z))\u2283(x=z))"

 };


 public TRandomProof(){

 }



public static void testStringArray (String[] stringArray)

// use this to test whether any statics you produce are well formed
{

//us.softoption.games.TBergmannRandomProof.testStringArray(stringArray);

// comment back in when needed-- out for applets

/*
  int available=stringArray.length;


TDeriverDocument document = new TDeriverDocument();           //i)   the document
TBrowser browser = new TBrowser(document,new TDeriverApplication());           //ii)  the browser
document.setJournal(browser);                    //iii) the back reference


   TMyProofPanel aPanel=new TMyProofPanel(document);

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


static public String aRandomSelection(String[] stringArray){

      String output="";

      int available=stringArray.length;

      if (available>0){

        output=stringArray[(int)(Math.floor(Math.random() * (available)))];  //floor is largest int not larger than
      }

      return
          output;
  }

static public String [] randomSelection(int n,String[] stringArray){

    String[] output={""};

    int available=stringArray.length;

    if (n<=available){
      output=new String[n];

      int [] indices = new int[n];

      for  (int i=0;i<n;i++){
        boolean done=false;                // bum algorithm
        int test=0;
        while (!done){
          done=true;
          test = (int)(Math.floor(Math.random() * (available)));  //floor is largest int not larger than
          for (int j = 0; j < i; j++) {
            if (indices[i]==indices[j])
              done=false;
          }
        }
        indices[i]=(int)test;
        output[i]=stringArray[test];
      }
    }

  return
      output;
  }

static public String [] randomSimpleNegEandEIorI(int n){
   //testStringArray (fSimpleNegEandEIorI);
    return
        randomSelection(n,fSimpleNegEandEIorI);
}
  static public String [] randomSimpleImplicEEquivE (int n){
  // testStringArray (fSimpleImplicEEquivE );
    return
        randomSelection(n,fSimpleImplicEEquivE );
}




static public String [] randomNegEandEIorI(int n){
 //testStringArray (fNegEandEIorI);
  return
      randomSelection(n,fNegEandEIorI);
}

static public String [] randomImplicEEquivE(int n){
  return
      randomSelection(n,fImplicEEquivE);
}

static public String [] randomImplicI(int n){
 //  testStringArray (fImplicI);
  return
      randomSelection(n,fImplicI);
}

static public String [] randomSimpleNegI(int n){
  return
      randomSelection(n,fSimpleNegI);
}

static public String [] randomNegI(int n){
  //testStringArray (fNegI);
  return
      randomSelection(n,fNegI);
}

static public String [] randomOrEEquivI(int n){
  return
      randomSelection(n,fOrEEquivI);
}

static public String [] randomTwelveLineProp(int n){

// testStringArray (fTwelveLineProp);

  return
      randomSelection(n,fTwelveLineProp);
}

static public String [] randomPredNoQuant(int n){

 // testStringArray (fTwelveLinePredNoQuant);

  return
      randomSelection(n,fTwelveLinePredNoQuant);
}

static public String [] randomSimpleUI(int n){

 // testStringArray (fSimpleUI);

  return
      randomSelection(n,fSimpleUI);
}

static public String [] randomSimpleUG(int n){

 //testStringArray (fSimpleUG);

  return
      randomSelection(n,fSimpleUG);
}

static public String [] randomSimpleEG(int n){

// testStringArray (fSimpleEG);

  return
      randomSelection(n,fSimpleEG);
}

static public String [] randomSimpleEI(int n){

 //testStringArray (fSimpleEI);

  return
      randomSelection(n,fSimpleEI);
}

static public String [] randomTenLinePred(int n){

 //testStringArray (fTenLinePred);

  return
      randomSelection(n,fTenLinePred);
}

static public String [] randomAnyAnyLevel(int n){

  //testStringArray (fAnyAnyLevel);

  return
      randomSelection(n,fAnyAnyLevel);
}

static public String [] randomIdentity(int n){

  //testStringArray (fAnyAnyLevel);

  return
      randomSelection(n,fIdentity);
}

}
