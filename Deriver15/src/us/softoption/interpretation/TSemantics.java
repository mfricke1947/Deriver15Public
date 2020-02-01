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

package us.softoption.interpretation;

import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.strCR;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TParser;

public class TSemantics{

private String fPossiblePropositions=TParser.gPredicates;
private String fPossibleProperties=TParser.gPredicates;
private String fPossibleRelations=TParser.gPredicates;
private String fPossibleFunctions=TParser.gFunctors;
private String fPossibleIdentities=TParser.gZeroFunctors;

static String strNull="";

private List fShapes;


private String fCurrentUniverse="";

public boolean [] fCurrentPropositions;  /*the index is the index of the propositionsr in the
                                    fPossibleProposition */


private String [] fCurrentProperties;  /*the index is the index of the properties in the
                                  fPossibleProperties eg  1 ie 'B' to strings eg "cfg" which
                                  are the individuals those properties apply to*/

private String [] fCurrentRelations;  /*the index is the index of the relations in the
                                 fPossibleRelations eg  1 ie 'B' to strings of ordered
                                 pais eg "cfgc" which
                                 are the pairs of individuals those relations apply to*/

private String [] fCurrentFunctions;  /*the index is the index of the functions in the
                                fPossibleFunctions eg  1 ie 'b' to strings of ordered
                                pais eg "cfgc" which
                                are the pairs of individuals those functions apply to*/
private char [] fCurrentIdentities;  /*the index is the index of the character in the
                               fPossibleIdentities eg  1 ie 'b' to a char eg 'c' which
                               the individual that b is identical to. The identities are
                               zero-ary functors, so the index is the name of the functor
                               and the entry here is the value.*/


/*
      fCurrentUniverse: STR255;
       fCurrentProperties: array['A'..'Z'] of STR255;
       fCurrentRelations: array['A'..'Z'] of STR255;
       fCurrentFunctions: array['a'..'z'] of STR255;
       fCurrentIdentities: array['a'..'z'] of CHAR;



    */


private String fIndividualNames;
//   HashMap fIndividuals;       // a map from keys, which are individual names, to the individual objects

private Rectangle [] fExtentRects;
private Point [] fHotSpots;

public TSemantics(){

}

public TSemantics(List theShapes,String theNames){
     fShapes=theShapes;
     fIndividualNames=theNames;

//     fIndividuals= new HashMap();      //name of shape(individual), object

     fCurrentPropositions= new boolean [fPossiblePropositions.length()];
     initializeBooleanArray(fCurrentPropositions);

     fCurrentProperties= new String [fPossibleProperties.length()];
     initializeStringArray(fCurrentProperties);

     fCurrentRelations= new String [fPossibleRelations.length()];
     initializeStringArray(fCurrentRelations);

     fCurrentFunctions= new String [fPossibleFunctions.length()];
     initializeStringArray(fCurrentFunctions);

     fCurrentIdentities= new char [fPossibleIdentities.length()];
     initializeCharArray(fCurrentIdentities);

     fExtentRects = new Rectangle [fIndividualNames.length()];  // need to call updateUniverse directly before using these
     fHotSpots = new Point [fIndividualNames.length()];  // need to call updateUniverse directly before using these

   }




protected void setShapeList(List theShapes){
   fShapes=theShapes;
   }

void initializePropertyHashMap(HashMap aMap){

   for(int i=0;i<fPossibleProperties.length();i++){
     aMap.put(new Character(fPossibleProperties.charAt(i)),"");
   }

   }

void initializeCharArray(char[] aCharArray){

   for(int i=0;i<aCharArray.length;i++){
      aCharArray[i]=chBlank;
         }

         }

 void initializeBooleanArray(boolean[] aBooleanArray){

               for(int i=0;i<aBooleanArray.length;i++){
                 aBooleanArray[i]=false;
               }

               }


void initializeStringArray(String[] aStringArray){

      for(int i=0;i<aStringArray.length;i++){
        aStringArray[i]="";
      }

      }


public String getCurrentUniverse(){
     return
         fCurrentUniverse;
   }

String []getCurrentProperties(){
     return
         fCurrentProperties;
   }

   boolean []getCurrentPropositions(){
     return
         fCurrentPropositions;
   }

  public String getPossiblePropositions(){
    return
        fPossiblePropositions;
  }



 String getPossibleProperties(){
        return
            fPossibleProperties;
      }

String []getCurrentRelations(){
           return
               fCurrentRelations;
         }

String []getCurrentFunctions(){
              return
                  fCurrentFunctions;
            }

char []getCurrentIdentities(){
                          return
                              fCurrentIdentities;
                        }


String getPossibleRelations(){
              return
                  fPossibleRelations;
            }

String getPossibleFunctions(){
                   return
                       fPossibleFunctions;
                 }

String getPossibleIdentities(){
                                    return
                                        fPossibleIdentities;
                                  }


public boolean universeEmpty(){

  return
      (fCurrentUniverse.length()==0);

                 }

                 /*

                     function TDeriverDocument.UniverseEmpty: BOOLEAN;
                    begin
                     UniverseEmpty := length(SELF.fCurrentUniverse) = 0;
                    end;


                   */




  boolean updateUniverse(){
      boolean changed=false;
      TShape theShape;
      String tempStr="";

      if (fShapes.size() > 0) {
         Iterator iter = fShapes.iterator();

         while (iter.hasNext()){
           theShape = (TShape) iter.next();

           if ((!theShape.getSelected())&&theShape.fTypeID==TShape.IDIndividual){
             tempStr = tempStr + theShape.fName;
//             Character theName = new Character(theShape.fName);
//             fIndividuals.remove(theName);
//             fIndividuals.put(theName, theShape);

             int index = fIndividualNames.indexOf(theShape.fName);
             fExtentRects[index] = theShape.getBoundsRect();
             fHotSpots[index] = ( (TIndividual) theShape).getHotSpot();
           }
         }
         char [] temp = tempStr.toCharArray();
         Arrays.sort(temp);
         tempStr= new String(temp);

         if (!tempStr.equals(fCurrentUniverse)){
           fCurrentUniverse = tempStr;
           changed = true;
         }

      }

      return
          changed;   //gIndividualNames
    }

boolean updateRelations(){   // presuppose here that the universe (and extentrects) have been updated first
      boolean changed = false;
      String tempStr = "";
      int relationIndex;
      String [] tempRelations;
      Rectangle fromRect;
      Rectangle toRect;
      char fromName, toName;
      int fromIndex,toIndex;


   tempRelations = new String[fPossibleRelations.length()];
   initializeStringArray(tempRelations);


   for (int i = 0; i < fCurrentUniverse.length(); i++) { // we are running through the names of the individuals in the universe of the diagram

     fromName = fCurrentUniverse.charAt(i);

     fromIndex = fIndividualNames.indexOf(fromName);

     fromRect = fExtentRects[fromIndex];

     tempStr = "";

     for (int j = 0; j < fCurrentUniverse.length(); j++) { // we are running through the names of the individuals in the universe of the diagram

       toName = fCurrentUniverse.charAt(j);

       toIndex = fIndividualNames.indexOf(toName);

       toRect = fExtentRects[toIndex];

       tempStr = "";

       if (fShapes.size() > 0) { // now we are going to run through the shapes to see which are relations applying to the pairs of individuals
         Iterator iter = fShapes.iterator();
         TShape theShape;

         while (iter.hasNext()) { //this checks if it contains an individual
           theShape = (TShape) iter.next();

           if ( (!theShape.getSelected()) &&
               (!TParser.isFunctor(theShape.fName)) &&       // small letters
               ( (theShape.fTypeID == TShape.IDRelationR) ||
                (theShape.fTypeID == TShape.IDFunction))) { //ie non selected relations

             if ( (fromRect.contains( ( (TRelation) theShape).getFrom())) &&
                 ( (toRect.contains( ( (TRelation) theShape).getTo())))) {

               relationIndex = fPossibleRelations.indexOf(theShape.fName);

               tempStr = tempRelations[relationIndex];

               int dupIndex = tempStr.indexOf(fromName + toName);

               if ((dupIndex < 0)||(dupIndex%2==1)){ // no duplicates
                   // not there or cross boundary e.g. abcd does NOT have bc
                   
                 tempStr = tempStr + fromName + toName;
                 tempRelations[relationIndex] = tempStr; //update
               }

             }
           }
         }
       }
     }
   }

         // at this point the tempRelations array should be good but we now need
         // to see if the real one is any different and update if so

    for (int i=0;i<fPossibleRelations.length();i++){ // running through the properties

   //    propertyKey = fPossibleProperties.charAt(i);

     //  tempStr=tempRelations[i];

       if (!(tempRelations[i].equals(fCurrentRelations[i]))) {
           changed = true;
           fCurrentRelations[i]=tempRelations[i];

       }

    }

   return
       changed;

    }

/*

        procedure UpdateRelations;

           var
            i, j: integer;
            fromRect, toRect: rect;
            charIndex: CHAR;

           procedure Test (shape: TShape);

           begin
            if (not shape.fIsSelected) then
             if not (shape.fName in gFunctors) then   (*small letters*)
              begin
               if (shape.fID = IDRelationR) or (shape.fID = IDFunction) then
               begin
               if PtInRect(shape.fFrom, fromRect) then
               if PtInRect(shape.fTo, toRect) then
               begin
               charIndex := shape.fName;
               tempInterpretationArray[charIndex] := concat(tempInterpretationArray[charIndex], charStr, charStr2);
               end;
               end;

              end;
           end;



          begin
           for i := 1 to sizeOfUni do
            begin
             charStr[1] := fCurrentUniverse[i];
             fromRect := extentRects[charStr[1]];
             for j := 1 to sizeOfUni do
              begin
               charStr2[1] := fCurrentUniverse[j];
               toRect := extentRects[charStr2[1]];
               EachVirtualShapeDo(Test);
              end;
            end;

           for charIndex := 'A' to 'Z' do
            if tempInterpretationArray[charIndex] <> fCurrentRelations[charIndex] then
             begin
              fCurrentRelations[charIndex] := tempInterpretationArray[charIndex]; {check}
              changed := TRUE;
             end;
          end;


*/



boolean updateFunctions(){   // presuppose here that the universe (and extentrects) have been updated first
      boolean changed = false;
      String tempStr = "";
      int functionIndex;
      String [] tempFunctions;
      Rectangle fromRect;
      Rectangle toRect;
      char fromName, toName;
      int fromIndex,toIndex;


   tempFunctions = new String[fPossibleFunctions.length()];
   initializeStringArray(tempFunctions);


   for (int i = 0; i < fCurrentUniverse.length(); i++) { // we are running through the names of the individuals in the universe of the diagram

     fromName = fCurrentUniverse.charAt(i);

     fromIndex = fIndividualNames.indexOf(fromName);

     fromRect = fExtentRects[fromIndex];

     tempStr = "";

     for (int j = 0; j < fCurrentUniverse.length(); j++) { // we are running through the names of the individuals in the universe of the diagram

       toName = fCurrentUniverse.charAt(j);

       toIndex = fIndividualNames.indexOf(toName);

       toRect = fExtentRects[toIndex];

       tempStr = "";

       if (fShapes.size() > 0) { // now we are going to run through the shapes to see which are relations applying to the pairs of individuals
         Iterator iter = fShapes.iterator();
         TShape theShape;

         while (iter.hasNext()) { //this checks if it contains an individual
           theShape = (TShape) iter.next();

           if ( (!theShape.getSelected()) &&                 //not selected
               (TParser.isFunctor(theShape.fName)) &&       // small letters
               ( (theShape.fTypeID == TShape.IDRelationR) ||
                (theShape.fTypeID == TShape.IDFunction))) { //ie non selected relations

             if ( (fromRect.contains( ( (TFunction) theShape).getFrom())) &&
                 ( (toRect.contains( ( (TFunction) theShape).getTo())))) {

               functionIndex = fPossibleFunctions.indexOf(theShape.fName);

               tempStr = tempFunctions[functionIndex];

               int debug = tempStr.indexOf(fromName + toName);

               if (tempStr.indexOf(fromName + toName) < 0) { // no duplicates
                 tempStr = tempStr + fromName + toName;
                 tempFunctions[functionIndex] = tempStr; //update
               }

             }
           }
         }
       }
     }
   }

         // at this point the tempFunctions array should be good but we now need
         // to see if the real one is any different and update if so

    for (int i=0;i<fPossibleFunctions.length();i++){ // running through the properties

   //    propertyKey = fPossibleProperties.charAt(i);

     //  tempStr=tempRelations[i];

       if (!(tempFunctions[i].equals(fCurrentFunctions[i]))) {
           changed = true;
           fCurrentFunctions[i]=tempFunctions[i];

       }

    }

   return
       changed;

    }



/*

 procedure UpdateFunctions;

    var
     i, j: integer;
     fromRect, toRect: rect;
     charIndex: CHAR;

    procedure Test (shape: TShape);

    begin
     if (not shape.fIsSelected) then
      if shape.fName in gFunctors then   (*small letters*)
       begin
        if (shape.fID = IDRelationR) or (shape.fID = IDFunction) then
        begin
        if PtInRect(shape.fFrom, fromRect) then
        if PtInRect(shape.fTo, toRect) then
        begin
        charIndex := shape.fName;
        tempFunctionArray[charIndex] := concat(tempFunctionArray[charIndex], charStr, charStr2);
        end;
        end;

       end;
    end;

   begin
    for i := 1 to sizeOfUni do
     begin
      charStr[1] := fCurrentUniverse[i];
      fromRect := extentRects[charStr[1]];
      for j := 1 to sizeOfUni do
       begin
        charStr2[1] := fCurrentUniverse[j];
        toRect := extentRects[charStr2[1]];
        EachVirtualShapeDo(Test);
       end;
     end;

    for charIndex := 'a' to 'z' do
     if tempFunctionArray[charIndex] <> fCurrentFunctions[charIndex] then
      begin
       fCurrentFunctions[charIndex] := tempFunctionArray[charIndex]; {check}
       changed := TRUE;
      end;
  end;

    */


   boolean updateIdentities(){   // presuppose here that the universe (and extentrects) have been updated first
         boolean changed = false;
         int identityIndex;
         char [] tempIdentities;
         Rectangle toRect;
         char toName;
         int toIndex;


         /*all we are interested in here is the ToRects. The line has a name, say 'c', then it is drawn from nowhere
         to, say, the individual 'd' */



      tempIdentities = new char[fPossibleIdentities.length()];
      initializeCharArray(tempIdentities);


        for (int j = 0; j < fCurrentUniverse.length(); j++) { // we are running through the names of the individuals in the universe of the diagram

          toName = fCurrentUniverse.charAt(j);

          toIndex = fIndividualNames.indexOf(toName);

          toRect = fExtentRects[toIndex];

          if (fShapes.size() > 0) {
              // now we are going to run through the shapes to see which are identities coming in to and individuals
            Iterator iter = fShapes.iterator();
            TShape theShape;

            while (iter.hasNext()) { //this checks if it contains an individual
              theShape = (TShape) iter.next();

              if ( (!theShape.getSelected()) &&                 //not selected
                  (TParser.isFunctor(theShape.fName)) &&       // small letters
                  (theShape.fTypeID == TShape.IDIdentity)) { //ie identity

                if ((toRect.contains( ( (TIdentity) theShape).getTo()))) {

                  identityIndex = fPossibleIdentities.indexOf(theShape.fName);

                  tempIdentities[identityIndex]=toName;

         //         int debug = tempStr.indexOf(fromName + toName);

                }
              }
            }
          }
        }


            // at this point the tempFunctions array should be good but we now need
            // to see if the real one is any different and update if so

       for (int i=0;i<fPossibleIdentities.length();i++){ // running through the properties

      //    propertyKey = fPossibleProperties.charAt(i);

        //  tempStr=tempRelations[i];

          if ((tempIdentities[i]!=(fCurrentIdentities[i]))) {
              changed = true;
              fCurrentIdentities[i]=tempIdentities[i];

          }

       }

      return
          changed;

    }



/*

    procedure UpdateIdentities;

       var
        j: integer;
        toRect: rect;
        charIndex: CHAR;

       procedure Test (shape: TShape);

       begin
        if (not shape.fIsSelected) then
         if shape.fName in gFunctors then   (*small letters*)
          begin
           if (shape.fID = IDIdentity) then
           begin
           if PtInRect(shape.fTo, toRect) then
           begin
           charIndex := shape.fName;
           tempIdentityArray[charIndex] := charStr2[1];
           end;
           end;
          end;
       end;

      begin
       for j := 1 to sizeOfUni do
        begin
         charStr2[1] := fCurrentUniverse[j];
         toRect := extentRects[charStr2[1]];
         EachVirtualShapeDo(Test);
        end;

       for charIndex := 'a' to 'z' do
        if tempIdentityArray[charIndex] <> fCurrentIdentities[charIndex] then
         begin
          fCurrentIdentities[charIndex] := tempIdentityArray[charIndex]; {check}
          changed := TRUE;
         end;
  end;

*/




boolean updateProperties(){   // presuppose here that the universe has been updated first
      boolean changed = false;
      String tempStr = "";
      char propertyKey;
      int individualIndex;
      int propertyIndex;
      char individualName;
      String [] tempProperties;


   tempProperties = new String[fPossibleProperties.length()];
   initializeStringArray(tempProperties);


   for (int j = 0; j < fCurrentUniverse.length(); j++) { // we are running through the names of the individuals in the universe of the diagram
       individualName = fCurrentUniverse.charAt(j);

       individualIndex = fIndividualNames.indexOf(individualName);

       Point hotSpot= fHotSpots[individualIndex];/* theIndividual.getHotSpot();*/

       tempStr = "";

       if (fShapes.size() > 0) {  // now we are going to run through the shapes to see which are properties applying to that individual
         Iterator iter = fShapes.iterator();
         TShape theShape;

         while (iter.hasNext()) { //this checks if it contains an individual
           theShape = (TShape) iter.next();


           if ((!theShape.getSelected()) &&
               theShape.fTypeID == TShape.IDProperty) {   //ie non selected properties

             if ((theShape.getBoundsRect()).contains(hotSpot)){ // applies to individual
               propertyIndex = fPossibleProperties.indexOf(theShape.fName);

               tempStr=tempProperties[propertyIndex];

               if (tempStr.indexOf(individualName) < 0){               // no duplicates
                 tempStr = tempStr + individualName;
                 tempProperties[propertyIndex]=tempStr;                 //update
               }
             }
           }
         }
       }
     }

         // at this point the tempProperties array should be good but we now need
         // to see if the real one is any different and update if so

    for (int i=0;i<fPossibleProperties.length();i++){ // running through the properties

   //    propertyKey = fPossibleProperties.charAt(i);

       tempStr=tempProperties[i];

       if (!tempStr.equals(fCurrentProperties[i])) {
           changed = true;
           fCurrentProperties[i]=tempStr;

       }

    }

   return
       changed;
    }

/*

              procedure UpdateProperties;

           var
            i: integer;
            hotspot: Point;
            charIndex: CHAR;

           procedure Test (shape: TShape);

            var
             itsExtentRect: rect;

           begin
            if (not shape.fIsSelected) then
             begin
              itsExtentRect := shape.fExtentRect;
              if (shape.fID = IDPropertyF) then
                                                {OR (shape.fID = IDPropertyG) OR (shape.fID =}
        {                            IDPropertyH) THEN all props are propertyF}
               begin
               if PtInRect(hotspot, itsExtentRect) then
               begin
               charIndex := shape.fName;
               if pos(charStr, tempInterpretationArray[charIndex]) = 0 then {no}
        {                                           duplicates}
               tempInterpretationArray[charIndex] := concat(tempInterpretationArray[charIndex], charStr);
               end;
               end;

             end;
           end;

          begin
           for i := 1 to sizeOfUni do
            begin
             charStr[1] := fCurrentUniverse[i];

             hotspot.h := (extentRects[charStr[1]].left + extentRects[charStr[1]].right) div 2;
             hotspot.v := (extentRects[charStr[1]].bottom + extentRects[charStr[1]].top) div 2;

             EachVirtualShapeDo(Test);
            end;

           for charIndex := 'A' to 'Z' do
            if tempInterpretationArray[charIndex] <> fCurrentProperties[charIndex] then
             begin
              fCurrentProperties[charIndex] := tempInterpretationArray[charIndex];
              changed := TRUE;
             end;
          end;


     */



public String interpretationToString(){                              //UNFINISHED MAY 04
 /*this is cribbed from the Interpretation Board drawing routine */


     String outputStr, tempStr;

     if (interpretationChanged());      // make sure we are up to date

     outputStr=getCurrentUniverse();

     outputStr= TUtilities.separateStringWithCommas(outputStr);

     outputStr= "Universe= {" + outputStr + "} " + strCR;


     String[] currentProperties=getCurrentProperties();
     String possibleProperties=getPossibleProperties();

  for (int i=0;i<currentProperties.length;i++){
    if (currentProperties[i].length()>0){
      tempStr=currentProperties[i];
      tempStr=TUtilities.separateStringWithCommas(tempStr);

      tempStr= possibleProperties.charAt(i)+"= {" + tempStr + "} ";

      outputStr=outputStr+ tempStr+strCR;
    }

  }


  String[] currentRelations=getCurrentRelations();
          String possibleRelations=getPossibleRelations();

          for (int i=0;i<currentRelations.length;i++){
            if (currentRelations[i].length()>0){
              tempStr=currentRelations[i];
              tempStr=TUtilities.intoOrderedPairs(tempStr);

              tempStr= possibleProperties.charAt(i)+"= {" + tempStr + "} ";

      outputStr=outputStr+ tempStr+strCR;

            }

          }



   return
         outputStr;
}



public String propositionsToString(){                              //UNFINISHED MAY 04
 /*this is cribbed from the Interpretation Board drawing routine */


     String outputStr=strNull, tempStr=strNull;



     boolean[] currentPropositions=getCurrentPropositions();
     String possiblePropositions=getPossiblePropositions();

  for (int i=0;i<currentPropositions.length;i++){
    if (currentPropositions[i]){
      tempStr=tempStr+possiblePropositions.charAt(i);
       }
  }

  if (tempStr!= strNull){  // there are some true ones

    tempStr=TUtilities.separateStringWithCommas(tempStr);

    outputStr= "True atomic propositions = {" + tempStr + "} " + strCR;

  }
  else
    outputStr= "All atomic propositions are assigned false.";


   return
         outputStr;
}



   /*

    procedure TDeriverDocument.WriteTruePropositions;
      var
       lengthofStr: integer;
       tempStr: str255;
       charIndex: CHAR;
     begin
      tempStr := strNull;

      for charIndex := 'A' to 'Z' do
       if fCurrentPropositions[charIndex] then
        tempStr := concat(tempStr, charIndex);

      lengthofStr := length(tempStr);
      if lengthofStr > 0 then
       begin
        while lengthofStr > 1 do
         begin
          insert(',', tempStr, lengthofStr);
          lengthofStr := lengthofStr - 1;
         end;

        tempStr := concat('True Atomic Propositions = { ', tempStr, ' }', gCR);

        WriteToJournal(tempStr, kHighlight, not kToMarker);
       end
      else
       begin
        tempStr := concat('All atomic propositions are assigned false.');

        WriteToJournal(tempStr, kHighlight, not kToMarker);
       end;
     end;


   */





  public boolean interpretationChanged(){
    boolean doFirst;
    boolean doSecond;
    boolean doThird;
    boolean doFourth;
    boolean doFifth;

    doFirst=updateUniverse();
    doSecond=updateProperties();
    doThird=updateRelations();
    doFourth=updateFunctions();
    doFifth=updateIdentities();
        return
        doFirst&&
        doSecond&&
        doThird&&
        doFourth&&
        doFifth;
    }

  /*

       function TDeriverDocument.InterpretationChanged: BOOLEAN;

      var
       changed: BOOLEAN;
       tempInterpretationArray: array['A'..'Z'] of str255;
       tempFunctionArray: array['a'..'z'] of str255;
       tempIdentityArray: array['a'..'z'] of CHAR;
       i: integer;
       charStr, charStr2: string[1];
       sizeOfUni: integer;
       extentRects: array['a'..'z'] of rect; {the constants are a subset of a..z}
       tempStr: str255;
       index: CHAR;

      procedure UpdateUniverse;

       var
        orderedStr: str255;
        charIndex: CHAR;

       procedure Test (shape: TShape);

       begin
        if (not shape.fIsSelected) then
         if (shape.fID = IDCircle) then
          begin
           charStr[1] := shape.fName;
           tempStr := concat(tempStr, charStr);
           extentRects[charStr[1]] := shape.fExtentRect;
          end;
       end;

      begin
       EachVirtualShapeDo(Test);

       orderedStr := strNull;
       for charIndex := 'a' to 'z' do
        begin
         if pos(StrofChar(charIndex), tempStr) <> 0 then {pos won't find a character}
          orderedStr := concat(orderedStr, StrofChar(charIndex));
        end;

       tempStr := orderedStr;

       if tempStr <> fCurrentUniverse then
        begin
         fCurrentUniverse := tempStr;
         changed := TRUE;
        end;
      end;

      procedure UpdateProperties;

       var
        i: integer;
        hotspot: Point;
        charIndex: CHAR;

       procedure Test (shape: TShape);

        var
         itsExtentRect: rect;

       begin
        if (not shape.fIsSelected) then
         begin
          itsExtentRect := shape.fExtentRect;
          if (shape.fID = IDPropertyF) then
                                            {OR (shape.fID = IDPropertyG) OR (shape.fID =}
    {                            IDPropertyH) THEN all props are propertyF}
           begin
           if PtInRect(hotspot, itsExtentRect) then
           begin
           charIndex := shape.fName;
           if pos(charStr, tempInterpretationArray[charIndex]) = 0 then {no}
    {                                           duplicates}
           tempInterpretationArray[charIndex] := concat(tempInterpretationArray[charIndex], charStr);
           end;
           end;

         end;
       end;

      begin
       for i := 1 to sizeOfUni do
        begin
         charStr[1] := fCurrentUniverse[i];

         hotspot.h := (extentRects[charStr[1]].left + extentRects[charStr[1]].right) div 2;
         hotspot.v := (extentRects[charStr[1]].bottom + extentRects[charStr[1]].top) div 2;

         EachVirtualShapeDo(Test);
        end;

       for charIndex := 'A' to 'Z' do
        if tempInterpretationArray[charIndex] <> fCurrentProperties[charIndex] then
         begin
          fCurrentProperties[charIndex] := tempInterpretationArray[charIndex];
          changed := TRUE;
         end;
      end;

      procedure UpdateRelations;

       var
        i, j: integer;
        fromRect, toRect: rect;
        charIndex: CHAR;

       procedure Test (shape: TShape);

       begin
        if (not shape.fIsSelected) then
         if not (shape.fName in gFunctors) then   (*small letters*)
          begin
           if (shape.fID = IDRelationR) or (shape.fID = IDFunction) then
           begin
           if PtInRect(shape.fFrom, fromRect) then
           if PtInRect(shape.fTo, toRect) then
           begin
           charIndex := shape.fName;
           tempInterpretationArray[charIndex] := concat(tempInterpretationArray[charIndex], charStr, charStr2);
           end;
           end;

          end;
       end;



      begin
       for i := 1 to sizeOfUni do
        begin
         charStr[1] := fCurrentUniverse[i];
         fromRect := extentRects[charStr[1]];
         for j := 1 to sizeOfUni do
          begin
           charStr2[1] := fCurrentUniverse[j];
           toRect := extentRects[charStr2[1]];
           EachVirtualShapeDo(Test);
          end;
        end;

       for charIndex := 'A' to 'Z' do
        if tempInterpretationArray[charIndex] <> fCurrentRelations[charIndex] then
         begin
          fCurrentRelations[charIndex] := tempInterpretationArray[charIndex]; {check}
          changed := TRUE;
         end;
      end;

      procedure UpdateFunctions;

       var
        i, j: integer;
        fromRect, toRect: rect;
        charIndex: CHAR;

       procedure Test (shape: TShape);

       begin
        if (not shape.fIsSelected) then
         if shape.fName in gFunctors then   (*small letters*)
          begin
           if (shape.fID = IDRelationR) or (shape.fID = IDFunction) then
           begin
           if PtInRect(shape.fFrom, fromRect) then
           if PtInRect(shape.fTo, toRect) then
           begin
           charIndex := shape.fName;
           tempFunctionArray[charIndex] := concat(tempFunctionArray[charIndex], charStr, charStr2);
           end;
           end;

          end;
       end;

      begin
       for i := 1 to sizeOfUni do
        begin
         charStr[1] := fCurrentUniverse[i];
         fromRect := extentRects[charStr[1]];
         for j := 1 to sizeOfUni do
          begin
           charStr2[1] := fCurrentUniverse[j];
           toRect := extentRects[charStr2[1]];
           EachVirtualShapeDo(Test);
          end;
        end;

       for charIndex := 'a' to 'z' do
        if tempFunctionArray[charIndex] <> fCurrentFunctions[charIndex] then
         begin
          fCurrentFunctions[charIndex] := tempFunctionArray[charIndex]; {check}
          changed := TRUE;
         end;
      end;

      procedure UpdateIdentities;

       var
        j: integer;
        toRect: rect;
        charIndex: CHAR;

       procedure Test (shape: TShape);

       begin
        if (not shape.fIsSelected) then
         if shape.fName in gFunctors then   (*small letters*)
          begin
           if (shape.fID = IDIdentity) then
           begin
           if PtInRect(shape.fTo, toRect) then
           begin
           charIndex := shape.fName;
           tempIdentityArray[charIndex] := charStr2[1];
           end;
           end;
          end;
       end;

      begin
       for j := 1 to sizeOfUni do
        begin
         charStr2[1] := fCurrentUniverse[j];
         toRect := extentRects[charStr2[1]];
         EachVirtualShapeDo(Test);
        end;

       for charIndex := 'a' to 'z' do
        if tempIdentityArray[charIndex] <> fCurrentIdentities[charIndex] then
         begin
          fCurrentIdentities[charIndex] := tempIdentityArray[charIndex]; {check}
          changed := TRUE;
         end;
      end;

     begin
      changed := FALSE;
      charStr := chBlank;
      charStr2 := chBlank;

      for index := 'A' to 'Z' do
       tempInterpretationArray[index] := strNull;

      tempStr := strNull;

      UpdateUniverse;

      sizeOfUni := length(tempStr);

      if sizeOfUni = 0 then
       begin
        for index := 'A' to 'Z' do
         begin
          fCurrentProperties[index] := strNull;
          fCurrentRelations[index] := strNull;
         end;
        for index := 'a' to 'z' do
         begin
          fCurrentFunctions[index] := strNull;
          fCurrentIdentities[index] := chBlank;
         end;
       end
      else
       begin
        UpdateProperties;

        for index := 'A' to 'Z' do
         tempInterpretationArray[index] := strNull;

        UpdateRelations;

        for index := 'a' to 'z' do
         tempFunctionArray[index] := strNull;

        UpdateFunctions;

        for index := 'a' to 'z' do
         tempIdentityArray[index] := chBlank;

        UpdateIdentities;
       end;

      InterpretationChanged := changed;

     end;



        */

boolean semanticsValid(){
   return
       false;
       }


boolean propertyValid (boolean withoutSelectees, TProperty theProperty){
   boolean valid=false;
   TShape theShape;
   Point hotSpot;
   char itsName=theProperty.getName();
   Rectangle itsExtentRect=theProperty.getBoundsRect();

   if (fShapes.size() > 0) {
      Iterator iter = fShapes.iterator();

      while (iter.hasNext()){            //this checks if it contains an individual
        theShape = (TShape) iter.next();

        if ((!valid)&&
            ((!withoutSelectees)||
            (!theShape.getSelected()))&&
            theShape.fTypeID==TShape.IDIndividual){

               hotSpot=((TIndividual)theShape).getHotSpot();

               valid=itsExtentRect.contains(hotSpot);
        }
       }

       if (valid){                     // this checks that it is not contained with another rectangle of the same kind

          iter = fShapes.iterator();

          while (iter.hasNext()){
             theShape = (TShape) iter.next();

             if ((valid)&&
                ((!withoutSelectees)||
                (!theShape.getSelected()))&&
                (theShape.fTypeID==TShape.IDProperty)&&
                (theShape!=theProperty)&&                // it is possible for the shape we are testing to be in the list
                 (theShape.fName==itsName) ){

                if ((theShape.getBoundsRect()).contains(itsExtentRect))
                   valid=false;
             }
          }
       }

    }

return
     valid;

       }
/*

                function TDeriverDocument.PropertyValid (withoutSelectees: BOOLEAN; itsName: CHAR; itsExtentRect: rect): BOOLEAN;

       {This tests whether a property extentRect contains an individual}

         var
          valid: BOOLEAN;
          hotspot: Point;

         procedure Look (shape: TShape); {checking contains individual}

         begin
          if not valid then
           if (not withoutSelectees) or (not shape.fIsSelected) then
            if (shape.fID = IDCircle) then
             begin
              hotspot.h := (shape.fExtentRect.left + shape.fExtentRect.right) div 2;
              hotspot.v := (shape.fExtentRect.bottom + shape.fExtentRect.top) div 2;
              if PtInRect(hotspot, itsExtentRect) then
              valid := TRUE;
             end;
         end;

         procedure LookAgain (shape: TShape); {checking does not contain its own property}

         begin
          if valid then
           if (not withoutSelectees) or (not shape.fIsSelected) then
            if (shape.fName = itsName) then
             if ((shape.fExtentRect.top >= itsExtentRect.top) and (shape.fExtentRect.bottom <= itsExtentRect.bottom) and (shape.fExtentRect.left >= itsExtentRect.left) and (shape.fExtentRect.right <= itsExtentRect.right)) or ((shape.fExtentRect.top <= itsExtentRect.top) and (shape.fExtentRect.bottom >= itsExtentRect.bottom) and (shape.fExtentRect.left <= itsExtentRect.left) and (shape.fExtentRect.right >= itsExtentRect.right)) then

              if not ((shape.fExtentRect.top = itsExtentRect.top) and (shape.fExtentRect.bottom = itsExtentRect.bottom) and (shape.fExtentRect.left = itsExtentRect.left) and (shape.fExtentRect.right = itsExtentRect.right)) then

              valid := FALSE;

         end;

        begin
         valid := FALSE;
         EachVirtualShapeDo(Look); {Not potential shapes?}

         if valid then
          EachVirtualShapeDo(LookAgain);

         PropertyValid := valid;
        end;


        */

boolean individualValid(boolean withoutSelectees, TShape thisShape){

/*         {This tests whether an individual's name is OK-- must be different from others and}
       {zero-ary functions.}*/

   boolean valid=false;
   TShape secondShape;

   char itsName=thisShape.getName();

   if (thisShape.fTypeID==TShape.IDIndividual){

     valid = (itsName != chBlank) && (TParser.isConstant(itsName));

     if (valid){

       if (fShapes.size() > 0) {
          Iterator iter = fShapes.iterator();

             while (iter.hasNext()){
                secondShape = (TShape) iter.next();

                if (((!withoutSelectees)||
                    (!secondShape.getSelected()))&&
                    (secondShape.fTypeID==TShape.IDIndividual||
                    secondShape.fTypeID==TShape.IDIdentity)&&
                    (thisShape!=secondShape)){

                if (secondShape.getName()==itsName)
                    valid=false;

                }
             }

       }

     }
     }
   return
       valid;
       }

/*
           function TDeriverDocument.IndividualValid (withoutSelectees: BOOLEAN; thisShape: TShape): BOOLEAN;

       {This tests whether an individual's name is OK-- must be different from others and}
       {zero-ary functions.}

         var
          valid: BOOLEAN;
          itsName: CHAR;

         procedure SecondDifferent (secondShape: TShape);

          var
           secondname: CHAR;

         begin
          if valid then
           if (not withoutSelectees) or (not secondShape.fIsSelected) then
            if (secondShape.fID = IDCircle) or (secondShape.fID = IDIdentity) then
             if secondShape <> thisShape then
              begin
              secondname := secondShape.fName;
              if secondname = itsName then
              valid := FALSE;
              end;
         end;

        begin
         itsName := thisShape.fName;
         valid := (itsName <> gBlank) and (itsName in gConstants); {none blank}
         if valid then
          EachVirtualShapeDo(SecondDifferent);
         IndividualValid := valid; {no two same}
        end;


           */


String findRelatees (boolean withoutSelectees, TRelation theRelation){

            /*This finds the individuals that a relation relates*/

            boolean valid = false;
            TShape theShape;
            Point fromSpot = theRelation.getFrom();
            Point toSpot = theRelation.getTo();
            char fromName = ' ';
            char toName = ' ';

            if (fShapes.size() > 0) {
              Iterator iter = fShapes.iterator();

              while (iter.hasNext()) { //checks from
                theShape = (TShape) iter.next();

                if ( (!valid) &&
                    ( (!withoutSelectees) ||
                     (!theShape.getSelected())) &&
                    theShape.fTypeID == TShape.IDIndividual) {

                  valid = (theShape.getBoundsRect()).contains(fromSpot);
                  if (valid)
                    fromName = theShape.fName;
                }
              }

              if (valid) { //  checks to

                valid = false;

                iter = fShapes.iterator();

                while (iter.hasNext()) {

                  theShape = (TShape) iter.next();

                  if ( (!valid) &&
                      ( (!withoutSelectees) ||
                       (!theShape.getSelected())) &&
                      theShape.fTypeID == TShape.IDIndividual) {

                    valid = (theShape.getBoundsRect()).contains(toSpot);
                    if (valid)
                      toName = theShape.fName;
                  }
                }

              }
              ;
            }


 if (valid)
   return
      String.valueOf(fromName) + toName;
else
  return
      strNull;
          }


char findIdenticalTo (boolean withoutSelectees, TIdentity theIdentity){

/*This finds the individuals that a relation relates*/

boolean valid = false;
TShape theShape;
Point toSpot = theIdentity.getTo();
char toName = ' ';

if (fShapes.size() > 0) {
   Iterator iter = fShapes.iterator();

   while (iter.hasNext()) { //checks from
                          theShape = (TShape) iter.next();

                          if ( (!valid) &&
                              ( (!withoutSelectees) ||
                               (!theShape.getSelected())) &&
                              theShape.fTypeID == TShape.IDIndividual) {

                            valid = (theShape.getBoundsRect()).contains(toSpot);
                            if (valid)
                              toName = theShape.fName;
                          }
                        }

                      }


           if (valid)
             return
                toName;
          else
            return
                ' ';
          }



boolean identityValid (boolean withoutSelectees, TIdentity theIdentity){

                      /*{This tests whether a relations ends latch onto individuals}
                                      and there are no duplicates*/

TShape theShape;
boolean valid;
char relatee = findIdenticalTo(withoutSelectees, theIdentity);

valid = (relatee != ' ');

if (valid) {

  valid = valid && TParser.isFunctor(theIdentity.fName);

  // duplicates ie no second shape with same name, no alias with same name?

  if (fShapes.size() > 0) {
    Iterator iter = fShapes.iterator();

    while (valid && iter.hasNext()) {
      theShape = (TShape) iter.next();

      if ( ( (!withoutSelectees) || (!theShape.getSelected())) &&
          (theShape != theIdentity) && // not us
          ( (theShape.fTypeID == TShape.IDIndividual) ||
           (theShape.fTypeID == TShape.IDIdentity))) {

        valid = valid && (theShape.fName != theIdentity.fName); //not the same name as us

      }
    }
  }
}
          return
             valid;
  }


/*

     function TDeriverDocument.IdentityValid (withoutSelectees: BOOLEAN; thisShape: TShape): BOOLEAN;
  {This tests whether an identity end latches onto individuals}
  { whether relation is already there is checked by the sketcher}

    var
     valid: BOOLEAN;
     toHotspot: Point;
     toExtentRect: rect;
     itsName: char;


    procedure LookTo (shape: TShape);
          {this checks that its head latches onto an individual}

    begin
     if not valid then
      if (shape.fID = IDCircle) then
       if (not withoutSelectees) or (not shape.fIsSelected) then
        begin
         toExtentRect := shape.fExtentRect;
         if PtInRect(toHotspot, toExtentRect) then
         valid := TRUE;
        end;
    end;

    procedure SecondDifferent (secondShape: TShape);
  {this checks that no two names (of indiviuals or 0-ary functions) are the same}
     var
      secondname: CHAR;

    begin
     if valid then
      if (not withoutSelectees) or (not secondShape.fIsSelected) then
       if (secondShape.fID = IDCircle) or (secondShape.fID = IDIdentity) then
        if secondShape <> thisShape then
         begin
         secondname := secondShape.fName;
         if secondname = itsName then
         valid := FALSE;
         end;
    end;

   begin
    valid := FALSE;
    toHotspot := thisShape.fTo;
    itsName := thisShape.fName;

    EachVirtualShapeDo(LookTo); {Not potential shapes?}

    valid := valid and (itsName in gConstants); {none blank}

    if valid then
     EachVirtualShapeDo(SecondDifferent);


    IdentityValid := valid;

   end;



*/


boolean relationValid (boolean withoutSelectees, TRelation theRelation){

            /*{This tests whether a relations ends latch onto individuals}
                            and there are no duplicates*/

   TShape theShape;
   boolean valid;
   String relatees = findRelatees(withoutSelectees, theRelation);

   valid = (relatees != strNull);

   if (valid) { // duplicates?
      if (fShapes.size() > 0) {
         Iterator iter = fShapes.iterator();

          while (valid && iter.hasNext()) {
             theShape = (TShape) iter.next();

             if ( ( (!withoutSelectees) || (!theShape.getSelected())) &&
                (theShape.fTypeID == TShape.IDRelationR) && //it's a relation
                (theShape.fName == theRelation.fName) && // same name
                (theShape != theRelation)) { // not me
                   String perhapsDuplicates = findRelatees(withoutSelectees,
                                                        (TRelation) theShape);  // see who it relates

                   if (perhapsDuplicates.equals(relatees))
                      valid = false;
                  }

                }
              }
            }
return
   valid;
  }

/*

   TShape theShape;
   Point fromSpot=theRelation.getFrom();
   Point toSpot=theRelation.getTo();
   char fromName=' ';
   char toName=' ';

 //            char itsName=theProperty.getName();
 //            Rectangle itsExtentRect=theProperty.getBoundsRect();

   if (fShapes.size() > 0) {
   Iterator iter = fShapes.iterator();

      while (iter.hasNext()){            //checks from
      theShape = (TShape) iter.next();

      if ((!valid)&&
         ((!withoutSelectees)||
         (!theShape.isSelected()))&&
          theShape.fTypeID==TShape.IDIndividual){

             valid=(theShape.getBoundsRect()).contains(fromSpot);
             if (valid)
               fromName=theShape.fName;
                  }
                 }

      if (valid){                     //  checks to

         valid=false;

         iter = fShapes.iterator();

         while (iter.hasNext()){

          theShape = (TShape) iter.next();

            if ((!valid)&&
           ((!withoutSelectees)||
             (!theShape.isSelected()))&&
             theShape.fTypeID==TShape.IDIndividual){

                  valid=(theShape.getBoundsRect()).contains(toSpot);
                  if (valid)
               toName=theShape.fName;
            }
           }

                       }










                       /*now to check for duplicates. we assume a) that this relation is not among the shapes
    (it shouldn't be because we are here checking before we insert it) and b) that the fCurrentRelatons
    are up to date (they should be because we are drawing the tracker


    Then there is a duplicate if the ordered pair of the relation is already there.*/

/*
   int relationIndex = fPossibleRelations.indexOf(theRelation.fName);
   String relationStr=fCurrentRelations[relationIndex];
   String testStr;
   String relationPair=(""+fromName+toName);
   int i=0;

   while (valid && (i<relationStr.length())){

     testStr=relationStr.substring(i,i+2);

     if (testStr.equals(relationPair))
       valid=false;

     i+=2;                 // we want to look at ordered pairs


   }


                    }




          return
               valid;

                 } */
/*
                 function TDeriverDocument.RelationValid (withoutSelectees: BOOLEAN; itsFrom, itsTo: Point): BOOLEAN;
                {This tests whether a relations ends latch onto individuals}
                { whether relation is already there is checked by the sketcher}

                  var
                   valid: BOOLEAN;
                   fromHotspot, toHotspot: Point;
                   fromExtentRect, toExtentRect: rect;

                  procedure LookFrom (shape: TShape);

                  begin
                   if not valid then
                    if (shape.fID = IDCircle) then
                     if (not withoutSelectees) or (not shape.fIsSelected) then
                      begin
                       fromExtentRect := shape.fExtentRect;
                       if PtInRect(fromHotspot, fromExtentRect) then
                       valid := TRUE;
                      end;
                  end;

                  procedure LookTo (shape: TShape);

                  begin
                   if not valid then
                    if (shape.fID = IDCircle) then
                     if (not withoutSelectees) or (not shape.fIsSelected) then
                      begin
                       toExtentRect := shape.fExtentRect;
                       if PtInRect(toHotspot, toExtentRect) then
                       valid := TRUE;
                      end;
                  end;

                 begin
                  valid := FALSE;
                  fromHotspot := itsFrom;
                  toHotspot := itsTo;

                  EachVirtualShapeDo(LookFrom); {Not potential shapes?}
                  if valid then
                   begin
                    valid := FALSE;
                    EachVirtualShapeDo(LookTo); {Not potential shapes?}
                   end;

                  RelationValid := valid;

                 end;
*/


/*

 function TShapeView.ClipBoardValid: BOOLEAN;

{The clipboard is valid if it is valid in itself, and if its individuals differ from those in the}
{document}

  var
   valid: BOOLEAN;
   clipShapeView: TShapeView;
   clipShapeDocument, actualDocument: TDeriverDocument;

  procedure NameOK (shape: TShape);

   procedure NamesDifferent (anotherShape: TShape);

   begin {got to deal with filtering etc.}
    if (anotherShape.fID = IDCircle) then {AND NOT anotherShape.fWasSelected }
     if (anotherShape.fName = shape.fName) then
      valid := FALSE;
   end;

  begin
   if valid then
    if shape.fID = IDCircle then
     actualDocument.EachVirtualShapeDo(NamesDifferent);

  end;

 begin
  valid := TRUE;
  actualDocument := fDeriverDocument;

  clipShapeView := TShapeView(gClipView);
  clipShapeDocument := clipShapeView.fDeriverDocument;

  valid := clipShapeDocument.DocumentValid(FALSE);

  if valid then
   clipShapeDocument.EachShapeDo(NameOK);

  ClipBoardValid := valid;
 end;



 */


public boolean isCharAnIdentity(char alias){
  return
      fCurrentIdentities[fPossibleIdentities.indexOf(alias)]!=chBlank;
}


public char valueOfIdentity(char alias){
  ;
    return
        fCurrentIdentities[fPossibleIdentities.indexOf(alias)];
  }


public char valueOfFunction(char functionName, char argument){
    /*can only be one entry and it is the first that we are after. A function
     is in the form of a list of ordered pairs, but as a continuous string with no separators*/
    char value=argument; //we want this to default to identity
    String function=fCurrentFunctions[fPossibleFunctions.indexOf(functionName)];

    String tempStr = TUtilities.intoOrderedPairs(function);    // just to make it less confusing for the programmer

    int index = tempStr.indexOf("<"+argument);  // eg <ab>

    if (index!=-1)
      value = tempStr.charAt(index+2);          // eg b

    return
       value;                                   // either the argument (defaults to identity) or value if there is one
    }

public boolean propositionTrue(char propName){

      return
          fCurrentPropositions[fPossiblePropositions.indexOf(propName)];
    }


public boolean propertyTrue(char propertyName, char individual){
  String property=fCurrentProperties[fPossibleProperties.indexOf(propertyName)];

  return
      (property.indexOf(individual)!=-1);
}

public boolean relationTrue(char relationName, char fromCh, char toCh){            //corrected Aug 06
  String relation=fCurrentRelations[fPossibleRelations.indexOf(relationName)];

  /*relations have the form abbabc ie ordered pairs stored as a string
   for one to be true an ordered pair, say ab has to be there and the index of
   a has to be even ie they must respect the boundaries*/

  boolean found=false;
  String search;
  String target = String.valueOf(fromCh)+toCh;

  for (int i=0; i<(relation.length()-1)&&!found;i+=2){
    search=relation.substring(i,i+2);
    found=search.equals(target);
  }

/*

  int test=fPossibleRelations.indexOf(relationName);

  int temp=relation.indexOf(fromCh+toCh);

  String searchStr=String.valueOf(fromCh);

  searchStr=searchStr+toCh;

  temp=relation.indexOf(searchStr); */

  return
      //(relation.indexOf(searchStr)!=-1); // we're looking for a pair eg "ac"

  found;
}



/*

   function TDeriverDocument.FindFunctionValue (funname, argument: CHAR): CHAR;
   (*can only be one entry and it is the first that we are after*)
     var
      tempCH: char;
      index, lengthToSearch: integer;
      found: boolean;
    begin
     found := false;
     tempCh := argument;  (*want it to default to identity*)
     index := 1;

     lengthToSearch := length(fCurrentFunctions[funname]);

     while (index < lengthToSearch) and (not found) do
      if fCurrentFunctions[funname][index] = argument then
       begin
        tempCh := fCurrentFunctions[funname][index + 1];
        found := TRUE;
       end
      else
       index := index + 2;
     FindFunctionValue := tempCh;
    end;



  */



public String firstBadConstant(String constants){

  //every constant either needs to be in the universe or a zero-ary functor whose referent is there.
char searchChar;

for (int i = 0; i < constants.length(); i++){
  searchChar = constants.charAt(i);

  if ( (getCurrentUniverse().indexOf(searchChar) == -1) && //it is not there
      !isCharAnIdentity(searchChar))                       // it is not an alias
    return
        String.valueOf(searchChar);
  }


return
    strNull;

}

/*
   i := 1;
       while (i <= length(constInFormula)) and found do
        begin
         charStr[1] := constInFormula[i];
         if pos(charStr, fCurrentUniverse) = 0 then
          if fCurrentIdentities[charStr[1]] = chBlank then  {not a 'dual name'}
           found := FALSE;
         i := i + 1;

     end;

*/




boolean documentValid (boolean withoutSelectees){
   boolean valid=true;

    TShape theShape;


    if (fShapes.size() > 0) {
      Iterator iter = fShapes.iterator();

      while ( (iter.hasNext()) && valid) {

        theShape = (TShape) iter.next();

        if (!theShape.getSelected()||!withoutSelectees)
          valid = theShape.isSemanticallySound(this,withoutSelectees);
      }
    }


   return
       valid;
}

/*

 function TDeriverDocument.DocumentValid (withoutSelectees: BOOLEAN): BOOLEAN;

  var
   valid: BOOLEAN;

  procedure ValidSoFar (shape: TShape);

   var
    targetRect: rect;
    itsFrom, itsTo: Point;
    itsName: CHAR;

  begin
   if valid then
    begin
     if (not withoutSelectees) or (not shape.fIsSelected) then
      begin
       case shape.fID of
       IDCircle:
       begin
       itsName := shape.fName;
       valid := SELF.IndividualValid(withoutSelectees, shape);
       end;
       IDPropertyF:{IDPropertyG,IDPropertyH}
       begin
       itsName := shape.fName;
       targetRect := shape.fExtentRect;
       valid := SELF.PropertyValid(withoutSelectees, itsName, targetRect);
       end;

       IDRelationR, IDFunction:
       begin
       itsFrom := shape.fFrom;
       itsTo := shape.fTo;
       valid := SELF.RelationValid(withoutSelectees, itsFrom, itsTo);
       end;

       IDIdentity:
       valid := SELF.IdentityValid(withoutSelectees, shape);

       otherwise
       end;
      end;

    end;
  end;

 begin
  valid := TRUE;
  SELF.EachVirtualShapeDo(ValidSoFar); {changed 23 June}
  DocumentValid := valid;
 end;




    */

}
