<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Objekttypen: Speicherung -->
<!--type: html -->
<!--description: In welchen Anwendungsbausteinen werden die Objekttypen gespeichert? -->
<!--author: Thomas Rudert -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">


<xsl:variable name="header_text" select="'In welchen Anwendungsbausteinen werden die Objekttypen gespeichert?'" />
<xsl:variable name="obje_text" select="'Objekttyp'" />
<xsl:variable name="mast_text" select="'Master (f&#252;hrender) Anwendungsbaustein'" />
<xsl:variable name="appl_text" select="'Anwendungsbausteine'" />

<!-- Funktion zum Ersetzen aller Vorkommen eines Teilstrings durch einen anderen Teilstring -->
<xsl:function name="str:replaceSubstring" as="xs:string">
	<xsl:param name="inputString" as="xs:string"/>
	<xsl:param name="oldSubstring" as="xs:string"/>
	<xsl:param name="newSubstring" as="xs:string"/>
	<xsl:choose>
		<xsl:when test="contains($inputString, $oldSubstring)">
			<xsl:sequence select="concat(concat(substring-before($inputString, $oldSubstring), $newSubstring), str:replaceSubstring(substring-after($inputString, $oldSubstring), $oldSubstring, $newSubstring))" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:sequence select="$inputString" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:function>

<xsl:function name="str:removeLineBreak" as="xs:string">
	<xsl:param name="string" as="xs:string" />
	<xsl:sequence select="str:replaceSubstring($string, '\-', '')" />
</xsl:function>

<xsl:key name="ObjLogspVerbindung" match="/modell_3lgm_2/objects/element[@class='ObjLogspVerbindung']" use="child::field[@name='start']" />
<xsl:key name="DbsBauVerbindung" match="/modell_3lgm_2/objects/element[@class='AwbDoksVerbindung' or @class='AwbDbsVerbindung' or @class='KawbDoksVerbindung' or @class='RawbDbsVerbindung']" use="child::field[@name='end']" />

<xsl:key name="ObjReprVerbindung" match="/modell_3lgm_2/objects/element[@class='ObjReprVerbindung']" use="child::field[@name='start']" />
<xsl:key name="ReprDbsVerbindung" match="/modell_3lgm_2/objects/element[@class='DoksDokVerbindung' or @class='DbsDatVerbindung']" use="child::field[@name='end']" />

<xsl:variable name="baustein" select="/modell_3lgm_2/objects/element[@class='Anwendungsbaustein' or @class='RechAnwendungsbaustein' or @class='KonAnwendungsbaustein']" />
<xsl:variable name="maxBaustein" select="count($baustein)" />

<!-- alle Kanten von Anwendungsbaustein zu Anwendungsprogramm finden -->
<xsl:key name="AWP" match="/modell_3lgm_2/objects/element[@class='RawbAwpVerbindung']" use="child::field[@name='start']" /> 

<!-- alle Kanten von Anwendungsprogramm zu Software finden -->
<xsl:key name="SW" match="/modell_3lgm_2/objects/element[@class='AwpSwpVerbindung']" use="child::field[@name='start']" /> 


<!-- Element mit dem Hashcode auswaehlen -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1><xsl:value-of select="$header_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<table cellspacing="0" cellpadding="3" border="1">
			<tr><th><xsl:value-of select="$obje_text" /></th><th><xsl:value-of select="$mast_text" /></th><th><xsl:value-of select="$appl_text" /></th></tr>
			<tbody align="left" valign="top">
				<xsl:apply-templates select="/modell_3lgm_2/objects/element[@class='Objekttyp']">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tbody>
		</table>
	</body>
</html>
</xsl:template>

<xsl:template match="/modell_3lgm_2/objects/element[@class='Objekttyp']">
	<xsl:call-template name="rekursiv1">
		<xsl:with-param name="objekttyp" select="@hash" />
	</xsl:call-template>
</xsl:template>

<xsl:template name="rekursiv1">
	<xsl:param name="objekttyp" />
	<xsl:param name="kanteObjRep" select="key('ObjReprVerbindung', $objekttyp)" />
	<xsl:param name="maxKanteObjRep" select="count($kanteObjRep)" />
	<xsl:param name="indexKanteObjRep" select="1" />
	<xsl:param name="ergebnis" select="''" />
	<xsl:param name="counter" select="0" />
	
	<xsl:choose>
		<xsl:when test="$indexKanteObjRep &gt; $maxKanteObjRep">
			<xsl:call-template name="ausgabe">
				<xsl:with-param name="objekttyp" select="$objekttyp" />
				<xsl:with-param name="ergebnis" select="$ergebnis" />
				<xsl:with-param name="anzahl" select="$counter" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:variable name="repForm" select="$kanteObjRep[$indexKanteObjRep]/child::field[@name='end']" />
			<xsl:variable name="klasse" select="key('hash', $repForm)/@class" />
			<xsl:choose>
				<xsl:when test="$klasse = 'Datensatztyp' or $klasse = 'Dokumententyp'">
					<xsl:call-template name="rekursiv2">
						<xsl:with-param name="objekttyp" select="$objekttyp" />
						<xsl:with-param name="repForm" select="$repForm" />
						<xsl:with-param name="kanteObjRep" select="$kanteObjRep" />
						<xsl:with-param name="maxKanteObjRep" select="$maxKanteObjRep" />
						<xsl:with-param name="indexKanteObjRep" select="$indexKanteObjRep" />
						<xsl:with-param name="ergebnis" select="$ergebnis" />
						<xsl:with-param name="counter" select="$counter" />						
					</xsl:call-template>				
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="rekursiv1">
						<xsl:with-param name="objekttyp" select="$objekttyp" />
						<xsl:with-param name="kanteObjRep" select="$kanteObjRep" />
						<xsl:with-param name="maxKanteObjRep" select="$maxKanteObjRep" />
						<xsl:with-param name="indexKanteObjRep" select="$indexKanteObjRep + 1" />
						<xsl:with-param name="ergebnis" select="$ergebnis" />
						<xsl:with-param name="counter" select="$counter" />						
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>	
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursiv2">
	<xsl:param name="objekttyp" />
	<xsl:param name="repForm" />
	<xsl:param name="kanteObjRep" />
	<xsl:param name="maxKanteObjRep" />
	<xsl:param name="indexKanteObjRep" />
	<xsl:param name="kanteRepDbs" select="key('ReprDbsVerbindung', $repForm)" />
	<xsl:param name="maxKanteRepDbs" select="count($kanteRepDbs)" />
	<xsl:param name="indexKanteRepDbs" select="1" />	
	<xsl:param name="ergebnis" />
	<xsl:param name="counter" />
	
	<xsl:choose>
		<xsl:when test="$indexKanteRepDbs &gt; $maxKanteRepDbs">
			<xsl:call-template name="rekursiv1">
				<xsl:with-param name="objekttyp" select="$objekttyp" />
				<xsl:with-param name="kanteObjRep" select="$kanteObjRep" />
				<xsl:with-param name="maxKanteObjRep" select="$maxKanteObjRep" />
				<xsl:with-param name="indexKanteObjRep" select="$indexKanteObjRep + 1" />
				<xsl:with-param name="ergebnis" select="$ergebnis" />
				<xsl:with-param name="counter" select="$counter" />						
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursiv3">
				<xsl:with-param name="objekttyp" select="$objekttyp" />
				<xsl:with-param name="repForm" select="$repForm" />
				<xsl:with-param name="dbs" select="$kanteRepDbs[$indexKanteRepDbs]/child::field[@name='start']" />
				<xsl:with-param name="kanteObjRep" select="$kanteObjRep" />
				<xsl:with-param name="maxKanteObjRep" select="$maxKanteObjRep" />
				<xsl:with-param name="indexKanteObjRep" select="$indexKanteObjRep" />
				<xsl:with-param name="kanteRepDbs" select="$kanteRepDbs" />
				<xsl:with-param name="maxKanteRepDbs" select="$maxKanteRepDbs" />
				<xsl:with-param name="indexKanteRepDbs" select="$indexKanteRepDbs" />
				<xsl:with-param name="ergebnis" select="$ergebnis" />
				<xsl:with-param name="counter" select="$counter" />						
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursiv3">
	<xsl:param name="objekttyp" />
	<xsl:param name="repForm" />
	<xsl:param name="dbs" />
	<xsl:param name="kanteObjRep" />
	<xsl:param name="maxKanteObjRep" />
	<xsl:param name="indexKanteObjRep" />
	<xsl:param name="kanteRepDbs" />
	<xsl:param name="maxKanteRepDbs" />
	<xsl:param name="indexKanteRepDbs" />	
	<xsl:param name="kanteDbsAwb" select="key('DbsBauVerbindung', $dbs)" />
	<xsl:param name="maxKanteDbsAwb" select="count($kanteDbsAwb)" />
	<xsl:param name="indexKanteDbsAwb" select="1" />	
	<xsl:param name="ergebnis" />
	<xsl:param name="counter" />
	
	<xsl:choose>
		<xsl:when test="$indexKanteDbsAwb &gt; $maxKanteDbsAwb">
			<xsl:call-template name="rekursiv2">
				<xsl:with-param name="objekttyp" select="$objekttyp" />
				<xsl:with-param name="repForm" select="$repForm" />
				<xsl:with-param name="kanteObjRep" select="$kanteObjRep" />
				<xsl:with-param name="maxKanteObjRep" select="$maxKanteObjRep" />
				<xsl:with-param name="indexKanteObjRep" select="$indexKanteObjRep" />
				<xsl:with-param name="kanteRepDbs" select="$kanteRepDbs" />
				<xsl:with-param name="maxKanteRepDbs" select="$maxKanteRepDbs" />
				<xsl:with-param name="indexKanteRepDbs" select="$indexKanteRepDbs + 1" />
				<xsl:with-param name="ergebnis" select="$ergebnis" />
				<xsl:with-param name="counter" select="$counter" />						
			</xsl:call-template>		
		</xsl:when>
		<xsl:otherwise>
			<xsl:variable name="hash" select="$kanteDbsAwb[$indexKanteDbsAwb]/child::field[@name='start']" />
			<xsl:choose>
				<xsl:when test="contains($ergebnis, $hash)">
					<xsl:call-template name="rekursiv3">
						<xsl:with-param name="objekttyp" select="$objekttyp" />
						<xsl:with-param name="repForm" select="$repForm" />
						<xsl:with-param name="dbs" select="$dbs" />
						<xsl:with-param name="kanteObjRep" select="$kanteObjRep" />
						<xsl:with-param name="maxKanteObjRep" select="$maxKanteObjRep" />
						<xsl:with-param name="indexKanteObjRep" select="$indexKanteObjRep" />
						<xsl:with-param name="kanteRepDbs" select="$kanteRepDbs" />
						<xsl:with-param name="maxKanteRepDbs" select="$maxKanteRepDbs" />
						<xsl:with-param name="indexKanteRepDbs" select="$indexKanteRepDbs" />
						<xsl:with-param name="kanteDbsAwb" select="$kanteDbsAwb" />
						<xsl:with-param name="maxKanteDbsAwb" select="$maxKanteDbsAwb" />
						<xsl:with-param name="indexKanteDbsAwb" select="$indexKanteDbsAwb + 1" />
						<xsl:with-param name="ergebnis" select="$ergebnis" />
						<xsl:with-param name="counter" select="$counter" />						
					</xsl:call-template>		
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="rekursiv3">
						<xsl:with-param name="objekttyp" select="$objekttyp" />
						<xsl:with-param name="repForm" select="$repForm" />
						<xsl:with-param name="dbs" select="$dbs" />
						<xsl:with-param name="kanteObjRep" select="$kanteObjRep" />
						<xsl:with-param name="maxKanteObjRep" select="$maxKanteObjRep" />
						<xsl:with-param name="indexKanteObjRep" select="$indexKanteObjRep" />
						<xsl:with-param name="kanteRepDbs" select="$kanteRepDbs" />
						<xsl:with-param name="maxKanteRepDbs" select="$maxKanteRepDbs" />
						<xsl:with-param name="indexKanteRepDbs" select="$indexKanteRepDbs" />
						<xsl:with-param name="kanteDbsAwb" select="$kanteDbsAwb" />
						<xsl:with-param name="maxKanteDbsAwb" select="$maxKanteDbsAwb" />
						<xsl:with-param name="indexKanteDbsAwb" select="$indexKanteDbsAwb + 1" />
						<xsl:with-param name="ergebnis" select="concat($ergebnis, '§$§' , $hash)" />
						<xsl:with-param name="counter" select="$counter + 1" />						
					</xsl:call-template>		
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="ausgabe">
	<xsl:param name="objekttyp" />
	<xsl:param name="ergebnis" />
	<xsl:param name="anzahl" />
	<tr>
		<xsl:choose>
			<xsl:when test="$anzahl &gt; 0">
				<td>
					<xsl:attribute name="rowspan"><xsl:value-of select="$anzahl" /></xsl:attribute>
					<xsl:value-of select="str:removeLineBreak(key('hash', $objekttyp)/child::field[@name='name'])" />
				</td>
				<td>
					<xsl:attribute name="rowspan"><xsl:value-of select="$anzahl" /></xsl:attribute>
					<xsl:value-of select="
						if (empty(key('hash', key('DbsBauVerbindung', key('ObjLogspVerbindung', $objekttyp)/child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name']))
						then ''
						else str:removeLineBreak(key('hash', key('DbsBauVerbindung', key('ObjLogspVerbindung', $objekttyp)/child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name'])" />
					<xsl:if test="count(key('hash', key('DbsBauVerbindung', key('ObjLogspVerbindung', $objekttyp)/child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name']) &gt; 0">					
					<xsl:text> (</xsl:text>
						<xsl:value-of select="key('hash',key('SW',key('AWP',key('DbsBauVerbindung', key('ObjLogspVerbindung', $objekttyp)/child::field[@name='end'])/child::field[@name='start'])/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
					<xsl:text>) </xsl:text>									
					</xsl:if>
					<br/>				
				</td>
				<xsl:call-template name="stringTrennen">
					<xsl:with-param name="zeichenkette" select="substring-after($ergebnis, '§$§')" />
					<xsl:with-param name="eintraege" select="$anzahl" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<td><xsl:value-of select="str:removeLineBreak(key('hash', $objekttyp)/child::field[@name='name'])" /></td><td>
				<xsl:value-of select="
					if (empty(key('hash', key('DbsBauVerbindung', key('ObjLogspVerbindung', $objekttyp)/child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name']))
					then ''
					else str:removeLineBreak(key('hash', key('DbsBauVerbindung', key('ObjLogspVerbindung', $objekttyp)/child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name'])" />&#160;</td><td>&#160;</td>
			</xsl:otherwise>
		</xsl:choose>
	</tr>
</xsl:template>

<xsl:template name="stringTrennen">
	<xsl:param name="zeichenkette" />
	<xsl:param name="eintraege" />
	<xsl:choose>
		<xsl:when test="$eintraege &gt; 1">
			<td><xsl:value-of select="str:removeLineBreak(key('hash', substring-before($zeichenkette, '§$§'))/child::field[@name='name'])" />
			<xsl:text> (</xsl:text>
				<xsl:value-of select="key('hash',key('SW',key('AWP',substring-before($zeichenkette, '§$§'))/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
			<xsl:text>) </xsl:text>	
			 </td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
			<xsl:call-template name="stringTrennen">
				<xsl:with-param name="zeichenkette" select="substring-after($zeichenkette, '§$§')" />
				<xsl:with-param name="eintraege" select="$eintraege - 1" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<td><xsl:value-of select="str:removeLineBreak(key('hash', $zeichenkette)/child::field[@name='name'])" />
			<xsl:text> (</xsl:text>
				<xsl:value-of select="key('hash',key('SW',key('AWP',$zeichenkette)/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
			<xsl:text>) </xsl:text>	
			</td>
		</xsl:otherwise>
	</xsl:choose>			
</xsl:template>

</xsl:stylesheet>
