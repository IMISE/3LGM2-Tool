<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Anwendungsbausteine und ihre Kommunikationsschnittstellen -->
<!--type: html -->
<!--description: Übersicht Anwendungsbausteine und ihre Kommunikationsschnittstellen -->
<!--author: Oliver Heller 18.01.2006 -->

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

<!-- alle Anwendungsbaustein-Kommunikationsschnittstellen-Kombinationen  -->
<xsl:key name="AWBKOM" match="/modell_3lgm_2/objects/element[@class='AwbKommssVerbindung']" use="child::field[@name='start']" />


<!-- Element mit Hashcode finden -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title>Übersicht über die Kommunikationsschnittstellen der Anwendungsbausteine</title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1>Übersicht über die Kommunikationsschnittstellen der Anwendungsbausteine</h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<table border="1" cellpadding="2" cellspacing="0">
			<tr><th>Anwendungsbaustein</th><th>Schnittstellen</th></tr>
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
	<xsl:variable name="AWBs" select="key('AWBKOM', @hash)" />
	<xsl:choose>
		<xsl:when test="count($AWBs) &gt; 0">
			<td><xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" /></td>
			<td><xsl:for-each select="key('hash',$AWBs/child::field[@name='end'])">
				<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
				<xsl:if test="position() != last()">
					<br/>
				</xsl:if>
			</xsl:for-each>	</td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<td><xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" /></td><td><br/></td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
	
	
</xsl:template>



</xsl:stylesheet>
