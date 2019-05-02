<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Hierarchie der Aufgaben -->
<!--type: html -->
<!--description: Welche Aufgaben sind Teilaufgaben einer anderen Aufgabe? -->
<!--author: Thomas Rudert 25.06.2004 -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">

<xsl:variable name="header_text" select="'Welche Teilaufgaben geh&#246;ren zu einer Aufgabe?'" />
<xsl:variable name="header1_text" select="'Hierarchie der Aufgaben'" />
<xsl:variable name="error_text" select="'In diesem Modell sind keine Aufgaben enthalten!'" />

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
<xsl:key name="objektKlasse" match="/modell_3lgm_2/objects/element" use="@class" />

<xsl:key name="istTeilaufgabeVon" match="/modell_3lgm_2/objects/element[@class='AufAufVerbindung']" use="child::field[@name='start']" />
<xsl:key name="istOberaufgabeVon" match="/modell_3lgm_2/objects/element[@class='AufAufVerbindung']" use="child::field[@name='end']" />

<!-- Element mit Hashcode finden -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm" />
		<style>
			ul { list-style-type:square; }
		</style>
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1><xsl:value-of select="$header1_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>	
		<h3><xsl:value-of select="$header_text" /></h3>
		<xsl:choose>
			<xsl:when test="count(key('objektKlasse', 'Aufgabe')) &lt; 1">
				<p><xsl:value-of select="$error_text" /></p>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='Aufgabe']">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</body>
</html>
</xsl:template>

<xsl:template match="element[@class='Aufgabe']">
	<ul>
		<xsl:if test="count(key('istTeilaufgabeVon', @hash)) = 0">
			<li>
				<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
				<xsl:call-template name="resolveHierarchy">
					<xsl:with-param name="masterTask" select="@hash" />
				</xsl:call-template>
			</li>
		</xsl:if>
	</ul>
</xsl:template>

<xsl:template name="resolveHierarchy">
	<xsl:param name="masterTask" />
	<xsl:param name="usedTasks" select="$masterTask"/>
	<ul>
		<xsl:for-each select="key('istOberaufgabeVon', $masterTask)">
			<xsl:sort select="str:removeLineBreak(key('hash', child::field[@name='start'])/child::field[@name='name'])" order="ascending" data-type="text" />
			<xsl:variable name="hash" select="child::field[@name='start']" />
			<li>
				<xsl:choose>
					<xsl:when test="contains($usedTasks, $hash)">
						<xsl:text>ACHTUNG KREIS MODELLIERT(</xsl:text>
						<xsl:value-of select="$hash" />
						<xsl:text>; </xsl:text>
						<xsl:value-of select="str:removeLineBreak(key('hash', $hash)/child::field[@name='name'])" />
						<xsl:text>)</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="str:removeLineBreak(key('hash', $hash)/child::field[@name='name'])" />
						<xsl:call-template name="resolveHierarchy">
							<xsl:with-param name="masterTask" select="$hash" />
							<xsl:with-param name="usedTasks" select="concat($hash, $usedTasks)" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</li>
		</xsl:for-each>
	</ul>
</xsl:template>

</xsl:stylesheet>