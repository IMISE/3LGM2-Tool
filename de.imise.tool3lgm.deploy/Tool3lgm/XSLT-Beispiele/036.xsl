<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Physischen Datenverarbeitungsbausteine bei Durchführung einer Aufgabe? -->
<!--type: html -->
<!--description: Welche physischen Datenverarbeitungsbausteine werden von einem Mitarbeiter in einer bestimmten Rolle bei der Durchführung einer Aufgabe benutzt? -->
<!--author: Oliver Heller 02.02.2006 -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">

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

<!-- Schluessel zur Auswahl von Elementen -->

<!-- alle Kanten von Rolle zu Aufgabe -->
<xsl:key name="ObjReprVerbindung" match="/modell_3lgm_2/objects/element[@class='ObjReprVerbindung']" use="child::field[@name='start']" />

<!-- alle Kanten von Aufgabe zu einer AufgabeOrgansisationseinheitKombination (AufOrgKomb - KOH) -->
<xsl:key name="AufAufOrgVerbindung" match="/modell_3lgm_2/objects/element[@class='AufAufOrgVerbindung']" use="child::field[@name='start']" />

<!-- alle Kanten von AufOrgKomb zu einer Anwendungsbausteinkonfiguration (ABK) -->
<xsl:key name="AwbkAufOrgVerbindung" match="/modell_3lgm_2/objects/element[@class='AwbkAufOrgVerbindung']" use="child::field[@name='start']" />

<!-- alle Kanten von AnwBauKonf zu Anwendungsbaustein(rechnerunt., konvent., gemischt)  finden -->
<xsl:key name="AwbAwbkVerbindung" match="/modell_3lgm_2/objects/element[@class='AwbAwbkVerbindung']" use="child::field[@name='start']" />


<!-- alle Kanten von AnwBauKonf zu Anwendungsbaustein(rechnerunt., konvent., gemischt)  finden -->
<xsl:key name="PdvbkAwbVerbindung" match="/modell_3lgm_2/objects/element[@class='PdvbkAwbVerbindung']" use="child::field[@name='start']" />

<!-- alle Kanten von AnwBauKonf zu Anwendungsbaustein(rechnerunt., konvent., gemischt)  finden -->
<xsl:key name="PdvbPdvbkVerbindung" match="/modell_3lgm_2/objects/element[@class='PdvbPdvbkVerbindung']" use="child::field[@name='start']" />


<!-- Element mit Hashcode finden -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title>Welche physischen Datenverarbeitungsbausteine werden von einem Mitarbeiter in einer bestimmten Rolle bei der Durchf&#252;hrung einer Aufgabe benutzt?</title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1>Welche physischen Datenverarbeitungsbausteine werden von einem Mitarbeiter in einer bestimmten Rolle bei der Durchf&#252;hrung einer Aufgabe benutzt?</h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<table border="1" cellpadding="3" cellspacing="0">
			<tr><th>Rolle</th><th>Aufgabe</th><th>Physischer DVB</th></tr>
			<tbody valign="top" align="left"><tr>
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='Rolle']">
					
					<!-- nach den Namen der Rollen sortieren -->
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tr></tbody>
		</table>
	</body>
</html>
</xsl:template>

<xsl:template match="element[@class='Rolle']">
	<!-- Anzahl der Kanten von der Aufgabe zu einer AufgabeOrgansisationseinheitKombination (AufOrgKomb - KOH) -->
	<tr><td><xsl:attribute name="rowspan">
		<xsl:value-of select="count(key('ObjReprVerbindung', @hash))" />
		</xsl:attribute>
		<xsl:attribute name="colspan">
		<xsl:value-of select="1" />
		</xsl:attribute>
	<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])"/></td>
	<xsl:for-each select="key('ObjReprVerbindung', @hash)">
		<xsl:sort select="str:removeLineBreak(key('hash',child::field[@name='end'])/child::field[@name='name'])" order="ascending" data-type="text" />
		<xsl:if test="position() &gt; 1">
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:if>
		<td><xsl:value-of select="str:removeLineBreak(key('hash',child::field[@name='end'])/child::field[@name='name'])" /></td><td>
		<xsl:for-each select="key('hash', key('PdvbPdvbkVerbindung', key('PdvbkAwbVerbindung',key('AwbAwbkVerbindung',key('AwbkAufOrgVerbindung',key('AufAufOrgVerbindung',child::field[@name='end'])/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='end'])">
			<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
			<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
			<xsl:if test="position() != last()">
				<br/>
			</xsl:if>
		</xsl:for-each><br/></td>
	</xsl:for-each></tr>
</xsl:template>

</xsl:stylesheet>
