<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: alle physischen DV-Bausteine -->
<!--type: html -->
<!--description: extrahiert alle physischen DV-Bausteine mit Namen aus einem Modell -->
<!--author: Thomas Trommer 02.05.2005 -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	version="2.0">

<xsl:variable name="header_text" select="'physische DV-Bausteine'" />

<xsl:key name="element" match="modell_3lgm_2/objects/element" use="@class" />

<xsl:template match="/">
<html>
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm"/>
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1><xsl:value-of select="$header_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<table cellspacing="0" cellpadding="3" border="1">
			<tr>
				<th><xsl:value-of select="'Bezeichnung'" /></th>
				<th><xsl:value-of select="'Standort'" /></th>
				<th><xsl:value-of select="'Bausteintyp'" /></th>
			</tr>
			<tbody align="left" valign="top">
				<xsl:for-each select="key('element','PhysischerDVBaustein')">
					<xsl:sort data-type="text" select="child::field[@name='name']" />
					<tr>
						<td>
							<xsl:value-of select="./field[@name='name']/text()" />&#160;
						</td>
					
						<!--Hash des aktuellen Bausteins-->
						<xsl:variable name="hash" select="@hash" />
					
						<!--Voraussetzungen:
						Standorthash und Typhash immer im End-Feld  einer Relation definiert
						nur ein Standort und nur ein Typ-->
					
				
						<!--Versuch Standort- bzw. Typverbindungen zu finden -->
						<xsl:variable name="Standorthash" select="key('element','PdvbStoVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
						<xsl:variable name="Bausteintyphash" select="key('element','PdvbBtypVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
					
						<td>
							<xsl:variable name="Standort" select="key('element','Standort')[@hash=$Standorthash]/field[@name='name']/text()" />
							<xsl:value-of select="$Standort" /> &#160;
						</td>
					
				
						<td>
							<xsl:variable name="Bausteintyp" select="key('element','Bausteintyp')[@hash=$Bausteintyphash]/field[@name='name']/text()" />
							<xsl:value-of select="$Bausteintyp"/> &#160;
						</td>
					
					
					</tr>
				</xsl:for-each>
			</tbody>
		</table>
	</body>
</html>
</xsl:template>

</xsl:stylesheet>