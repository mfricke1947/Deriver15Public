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

import java.awt.Dimension;

/*This is to remember everything about a document-- where its windows and scrollers are, which menus are active
etc.*/


public class TDocState{
  Dimension fBrowserSize=TBrowser.fDefaultSize;
  int fVertDivider=-1;  //journal-proof
  int fHorizDivider=-1; // palette-text

  boolean fTemplate=false;  //whether the proof is using templates and hints

  int fTabIndex=0;  // whether the proof (0) or the drawing is active
  boolean fPropLevel=false; // whether the semantics is set to propositional or predicate

  boolean fJournalEditable=true; // whether the hypertext is live

  public TDocState(){

  }

  public TDocState(Dimension browserSize,int horizDivider,int vertDivider,
                   boolean template, int tabIndex,boolean proplevel, boolean editable){
    fBrowserSize=browserSize;
    fVertDivider=vertDivider;
    fHorizDivider=horizDivider;

    fTemplate=template;

    fTabIndex=tabIndex;

    fPropLevel=proplevel;

    fJournalEditable=editable;

  }


  public int getTabIndex(){
    return
        fTabIndex;
  }

  public Dimension getBrowserSize(){
    return
        fBrowserSize;
  }

  public int getHDivider(){
    return
        fHorizDivider;
  }

  public boolean getTemplate(){
    return
        fTemplate;
  }

  public boolean getEditable(){
    return
        fJournalEditable;
  }


  public int getVDivider(){
    return
        fVertDivider;
  }


  public boolean getPropLevel(){
     return
         fPropLevel;
   }

  public void setTabIndex(int index){
    fTabIndex=index;
  }

  public void setBrowserSize(Dimension browserSize){
        fBrowserSize=browserSize;
  }


  public void setEditable(boolean editable){
      fJournalEditable=editable;

    }

  public void setPropLevel(boolean proplevel){
    fPropLevel=proplevel;

  }

  public void setHDivider(int horizDivider){
          fHorizDivider=horizDivider;
    }

  public void setTemplate(boolean template){
   fTemplate=template;
    }


  public void setVDivider(int vertDivider){
            fVertDivider=vertDivider;
      }



}
