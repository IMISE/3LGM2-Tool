<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Aufgaben mit Objekttypen -->
<!--type: html -->
<!--description: Welche Aufgabe interpretiert oder bearbeitet welche Objekttypen? -->
<!--author: Thomas Rudert 02.01.2004 -->

<!-- XSLT-Dokument -->
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">

<xsl:variable name="header_text" select="'Welche Aufgabe interpretiert oder bearbeitet welche Objekttypen?'" />
<xsl:variable name="task_text" select="'Aufgabe'" />
<xsl:variable name="read_text" select="'interpretiert Objekttyp'" />
<xsl:variable name="write_text" select="'bearbeitet Objekttyp'" />

<!-- Schluessel zur Auwahl von Knoten (erhebliche Performance Steigerung) -->
<!-- AufObjVerbindung mit Aufgabe interpretiert Objekttyp -->
<xsl:key name="AufgabeInterpretiert" match="/modell_3lgm_2/objects/element[@class='AufObjVerbindung'][child::field[@name='state']='BACKWARD']" use="child::field[@name='start']" />

<!-- AufObjVerbindung mit Aufgabe bearbeitet Objekttyp -->
<xsl:key name="AufgabeBearbeitet" match="/modell_3lgm_2/objects/element[@class='AufObjVerbindung'][child::field[@name='state']='FORWARD']" use="child::field[@name='start']" />

<!-- AufObjVerbindung mit Aufgabe interpretiert und bearbeitetObjekttyp -->
<xsl:key name="AufgabeInterBearb" match="/modell_3lgm_2/objects/element[@class='AufObjVerbindung'][child::field[@name='state']='DOUBLE']" use="child::field[@name='start']" />

<!-- Knoten auswaehlen der den angegebenen hashcode besitzt -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

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

<!-- Funktion entfernt das Zeilenumbruch Zeichen '\-' aus dem String -->
<xsl:function name="str:removeLineBreak" as="xs:string">
	<xsl:param name="string" as="xs:string" />
	<xsl:sequence select="str:replaceSubstring($string, '\-', '')" />
</xsl:function>

<!-- Routine fuer Wurzelknoten im XML-Dokument -->
<xsl:template match="/">

<!-- Konstukt fuer HTML-Dokument erstellen -->
<html>

<!-- HTML-Kopf erstellen -->
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm" />
	</head>

<!-- Inhalt der Seit erzeugen -->
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1><xsl:value-of select="$header_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<table border="1" cellpadding="3" cellspacing="0">
			<thead><tr><th><xsl:value-of select="$task_text" /></th><th><xsl:value-of select="$read_text" /></th><th><xsl:value-of select="$write_text" /></th></tr></thead>
			<tbody align="left" valign="top">

				<!-- Prozedur fuer alle Aufgaben aus dem Objekt aufrufen -->
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='Aufgabe']">

					<!-- gefundene Aufgaben alphabetisch nach ihrer Bezeichnung sortieren -->
					<xsl:sort data-type="text" select="str:removeLineBreak(field[@name='name'])" />
				</xsl:apply-templates>
			</tbody>
		</table>
	</body>
</html>
</xsl:template>

<!-- Prozedur fuer Aufgaben -->
<xsl:template match="modell_3lgm_2/objects/element[@class='Aufgabe']">
	<xsl:variable name="anzahlInterpretiert" select="count(key('AufgabeInterpretiert', @hash))" />
	<xsl:variable name="anzahlBearbeitet" select="count(key('AufgabeBearbeitet', @hash))" />

	<xsl:choose>
		<xsl:when test="($anzahlInterpretiert &gt; ($anzahlBearbeitet))">
			<xsl:call-template name="rekursivAnfang">
				<xsl:with-param name="hash" select="@hash" />
				<xsl:with-param name="anzahlInterpretiert" select="$anzahlInterpretiert" />
				<xsl:with-param name="anzahlBearbeitet" select="$anzahlBearbeitet" />
				<xsl:with-param name="anzahl" select="$anzahlInterpretiert" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursivAnfang">
				<xsl:with-param name="hash" select="@hash" />
				<xsl:with-param name="anzahlInterpretiert" select="$anzahlInterpretiert" />
				<xsl:with-param name="anzahlBearbeitet" select="$anzahlBearbeitet" />
				<xsl:with-param name="anzahl" select="$anzahlBearbeitet" />
			</xsl:call-template>		
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursivAnfang">
	<xsl:param name="hash" />
	<xsl:param name="anzahlInterpretiert" />
	<xsl:param name="anzahlBearbeitet" />
	<xsl:param name="anzahl" />

	<!-- Anzahl der AO-Verbindungen die Status bearbeiten und interpretieren haben -->
	<xsl:variable name="anzahlBoth" select="count(key('AufgabeInterBearb', @hash))" />

	<tr>
		<td>
			<xsl:if test="($anzahl + $anzahlBoth) &gt; 1">
				<xsl:attribute name="rowspan"><xsl:value-of select="$anzahlBoth + $anzahl" /></xsl:attribute>
			</xsl:if>
			<xsl:value-of select="str:removeLineBreak(key('hash', @hash)/child::field[@name='name'])" />
			<xsl:if test="($anzahl + $anzahlBoth) &lt; 1">
				<td>&#160;</td><td>&#160;</td>
			</xsl:if>
		</td>
		<xsl:call-template name="both">
			<xsl:with-param name="aufgabe" select="@hash" />
		</xsl:call-template>

		<xsl:call-template name="rekursiv">
			<xsl:with-param name="interpretiert" select="key('AufgabeInterpretiert', $hash)" />
			<xsl:with-param name="bearbeitet" select="key('AufgabeBearbeitet', $hash)" />
			<xsl:with-param name="anzahlInterpretiert" select="$anzahlInterpretiert" />
			<xsl:with-param name="anzahlBearbeitet" select="$anzahlBearbeitet" />
			<xsl:with-param name="maxIndex" select="$anzahl" />
		</xsl:call-template>
	</tr>	
</xsl:template>

<xsl:template name="rekursiv">
	<xsl:param name="index" select="1" />
	<xsl:param name="interpretiert" />
	<xsl:param name="bearbeitet" />
	<xsl:param name="anzahlInterpretiert" />
	<xsl:param name="anzahlBearbeitet" />
	<xsl:param name="maxIndex" />
	
	<xsl:choose>
		<xsl:when test="$index &gt; $maxIndex" /> <!-- fertig -->
		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="$index &gt; $anzahlInterpretiert"> <!-- interpretiert ist fertig -->
					<td>&#160;</td>
				</xsl:when>
				<xsl:otherwise>
					<td><xsl:value-of select="str:removeLineBreak(key('hash', $interpretiert[$index]/child::field[@name='end'])/child::field[@name='name'])" /></td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$index &gt; $anzahlBearbeitet"> <!-- bearbeitet ist fertig -->
					<td>&#160;</td>
				</xsl:when>
				<xsl:otherwise>
					<td><xsl:value-of select="str:removeLineBreak(key('hash', $bearbeitet[$index]/child::field[@name='end'])/child::field[@name='name'])" /></td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
			<xsl:call-template name="rekursiv">
				<xsl:with-param name="index" select="$index + 1" />
				<xsl:with-param name="interpretiert" select="$interpretiert" />
				<xsl:with-param name="bearbeitet" select="$bearbeitet" />
				<xsl:with-param name="anzahlInterpretiert" select="$anzahlInterpretiert" />
				<xsl:with-param name="anzahlBearbeitet" select="$anzahlBearbeitet" />
				<xsl:with-param name="maxIndex" select="$maxIndex" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>	
</xsl:template>

<xsl:template name="both">
	<xsl:param name="aufgabe" />
	<xsl:for-each select="key('AufgabeInterBearb', $aufgabe)">
		<xsl:sort select="key('hash', child::field[@name='end'])/child::field[@name='name']" order="ascending" data-type="text" />
		<xsl:variable name="temp" select="str:removeLineBreak(key('hash', child::field[@name='end'])/child::field[@name='name'])" />
		<td><xsl:value-of select="$temp" /></td><td><xsl:value-of select="$temp" /></td>
		<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>