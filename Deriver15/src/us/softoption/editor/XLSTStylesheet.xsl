<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="what" select="'F'"/>
    <xsl:param name="by-what" select="'eFFe'"/>
   
    <xsl:template match="/"> 
     	<xsl:apply-templates/> 
	</xsl:template>


 <xsl:template match="@*|node()">  <!-- pass through -->
   <xsl:copy>
     <xsl:apply-templates select="@*|node()"/>
   </xsl:copy>
 </xsl:template>

   <xsl:template match="object[@class]"> 
   
   <xsl:variable name="className" select="@class" />
   <xsl:variable name="idValue" select="@id" />
   
   
   <xsl:choose>
     <xsl:when test= "starts-with($className,'interpretation.TProperty')"> 
       <object id= "{$idValue}" class= "us.softoption.interpretation.TProperty"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when> 
     
     <xsl:when test= "starts-with($className,'interpretation.TIndividual')"> 
       <object id= "{$idValue}" class= "us.softoption.interpretation.TIndividual"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when> 
 
      <xsl:when test= "starts-with($className,'interpretation.TRelation')"> 
       <object id= "{$idValue}" class= "us.softoption.interpretation.TRelation"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when> 
     
      <xsl:when test= "starts-with($className,'interpretation.TIdentity')"> 
       <object id= "{$idValue}" class= "us.softoption.interpretation.TIdentity"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when> 
     
     <xsl:when test= "starts-with($className,'interpretation.TInterpretationBoard')"> 
       <object id= "{$idValue}" class= "us.softoption.interpretation.TInterpretationBoard"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when> 

     <xsl:when test= "starts-with($className,'interpretation.TSemantics')"> 
       <object id= "{$idValue}" class= "us.softoption.interpretation.TSemantics"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when>     
 
     <xsl:when test= "starts-with($className,'proofwindow.TProofListModel')"> 
       <object id= "{$idValue}" class= "us.softoption.proofwindow.TProofListModel"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when>
     
     <xsl:when test= "starts-with($className,'proofs.TProofListModel')"> 
       <object id= "{$idValue}" class= "us.softoption.proofs.TProofListModel"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when>
     
     <xsl:when test= "starts-with($className,'proofs.TProofline')"> 
       <object id= "{$idValue}" class= "us.softoption.proofs.TProofline"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when>     
 
     <xsl:when test= "starts-with($className,'parser.TFormula')"> 
       <object id= "{$idValue}" class= "us.softoption.parser.TFormula"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when>  

     <xsl:when test= "starts-with($className,'editor.TDocState')"> 
       <object id= "{$idValue}" class= "us.softoption.editor.TDocState"  > 
        <xsl:apply-templates/>
       </object>      
     </xsl:when>  
 
     <xsl:otherwise>
     
     <object id= "{$idValue}" class= "{$className}"> 
         <xsl:apply-templates/>
     </object> 
     
     
     
     </xsl:otherwise>
     </xsl:choose>
     
   </xsl:template>

</xsl:stylesheet>

    
