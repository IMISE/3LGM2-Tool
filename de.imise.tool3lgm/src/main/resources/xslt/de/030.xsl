<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Anwendungsbausteine: unterstützende Aufgaben -->
<!--type: html -->
<!--description: Welche Anwendungsbausteine erledigen welche Aufgaben? -->
<!--author: Oliver Heller -->

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



<!-- alle Kanten von AufOrgKomb zu einer Anwendungsbausteinkonfiguration (ABK) -->
<xsl:key name="ABK" match="/modell_3lgm_2/objects/element[@class='AwbkAufOrgVerbindung']" use="child::field[@name='start']" />



<!-- alle Organisationseinheiten -->
<xsl:key name="OEH" match="/modell_3lgm_2/objects/element[@class='OrgAufOrgVerbindung']" use="child::field[@name='start']" />

<!-- alle Aufgaben -->
<xsl:key name="AUF" match="/modell_3lgm_2/objects/element[@class='AufAufOrgVerbindung']" use="child::field[@name='end']" />

<!--  alle Kanten von AwbKonf zur Organisationseinheit -->
<xsl:key name="KOH" match="/modell_3lgm_2/objects/element[@class='AwbkAufOrgVerbindung']" use="child::field[@name='end']" />


<!-- alle Kanten von Anwendungsbaustein zu AnwBauKonf finden -->
<xsl:key name="AWBK" match="/modell_3lgm_2/objects/element[@class='AwbAwbkVerbindung']" use="child::field[@name='end']" />

<!-- alle Kanten von Anwendungsbaustein zu Anwendungsprogramm finden -->
<xsl:key name="AWP" match="/modell_3lgm_2/objects/element[@class='RawbAwpVerbindung']" use="child::field[@name='start']" /> 

<!-- alle Kanten von Anwendungsprogramm zu Software finden -->
<xsl:key name="SW" match="/modell_3lgm_2/objects/element[@class='AwpSwpVerbindung']" use="child::field[@name='start']" /> 


<!-- Element mit Hashcode finden -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title>Welche Anwendungsbausteine erledigen welche Aufgaben?</title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1>Welche Anwendungsbausteine erledigen welche Aufgaben?</h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<table border="1" cellpadding="2" cellspacing="0">
			<tr><th>Anwendungsbaustein</th><th>Aufgabe</th></tr>
			<tbody valign="top" align="left"><tr>
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='RechAnwendungsbaustein']">
					
					<!-- nach den Namen der Anwendungsbausteine sortieren -->
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tr></tbody>
		</table>
	</body>
</html>
</xsl:template>

<xsl:template match="element[@class='RechAnwendungsbaustein']">
	<xsl:variable name="AWBKs" select="key('AWBK', @hash)" />
	<xsl:choose>
		<xsl:when test="count($AWBKs) &gt; 0">
			<td><xsl:value-of select="str:removeLineBreak(child::field[@name='name'])"/><br/>
			<xsl:text>( </xsl:text><xsl:value-of select="key('hash',key('SW',key('AWP',@hash)/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
			<xsl:text> )</xsl:text>
			

			</td>
			<td><xsl:for-each select="key('hash',key('AUF',key('KOH',$AWBKs/child::field[@name='start'])/child::field[@name='start'])/child::field[@name='start'])">
				<xsl:sort select="child::field[@name='name']" order="ascending" data-type="text" />
					<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])"/><br/>
				</xsl:for-each><br/></td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<td><xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" /><br/>
			<xsl:text>( </xsl:text><xsl:value-of select="key('hash',key('SW',key('AWP',@hash)/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
			<xsl:text> )</xsl:text>
			</td><td><br/></td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
