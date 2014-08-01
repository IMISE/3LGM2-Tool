<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Application Components: Overview 2 -->
<!--type: html -->
<!--description: extracts all application components with their names, descriptions, software products and database management sytsems -->
<!--author: Thomas Trommer -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0">

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

<!--Überschrift-->
<xsl:variable name="header_text" select="'All application components of a model'" />

<!--Key - für die Performance-->
<xsl:key name="element" match="modell_3lgm_2/objects/element" use="@class" />

<!-- bei der Ausgabe muss 'disable-output-escaping' auf 'yes' gesetzt werden -->
<xsl:variable name="zeilenumbruch" select="'&lt;br/&gt;'" />

<xsl:template match="/">
<html>
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm"/>
	</head>
	<body>
		<xsl:comment>This HTML file was generated automatically from a 3LGM&#178; model.</xsl:comment>
		<h1><xsl:value-of select="$header_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<h3>Computer-based application components</h3>

		<xsl:variable name="anzahl" select="count(key('element','RechAnwendungsbaustein'))" />
		<xsl:choose>
			<xsl:when test="$anzahl=0">
				<xsl:text>nonexistent</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<table cellspacing="0" cellpadding="3" border="1">
					<tr>
						<th><xsl:value-of select="'Name'" /></th>
						<th><xsl:value-of select="'Description'" /></th>
						<th><xsl:value-of select="'Software Product'" /></th>
						<!-- <th><xsl:value-of select="'Database System'" /></th> -->
						<th><xsl:value-of select="'Database Management System'" /></th>
					</tr>
					<tbody align="left" valign="top">
						<xsl:for-each select="key('element','RechAnwendungsbaustein')">
							<xsl:sort data-type="text" select="child::field[@name='name']" />
							<tr>
								<td>
									<xsl:value-of select="str:replaceSubstring(child::field[@name='name'], '\-', '')" />&#160;
								</td>
								<td>
									<xsl:value-of select="str:replaceSubstring(child::field[@name='description'], '&#x000a;', $zeilenumbruch)" disable-output-escaping="yes" />&#160;
								</td>
								
								<!--Hash des aktuell bearbeiteten Anwendungsbausteins--> 
								<xsl:variable name="hash" select="@hash" />
							
								<!--Voraussetzungen:
								Softwareprodukthash und Datenbankhash immer im End-Feld  einer Relation definiert
								nur ein Softwareprodukt und nur eine Datenbank-->
							
								<xsl:variable name="Datenbankhash" select="key('element','RawbDbsVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
								<xsl:variable name="DBVShash" select="key('element','DbsDbvsVerbindung')/field[@name='start'][text()=$Datenbankhash]/../field[@name='end']/text()" />
								<xsl:variable name="Anwendungsprodukthash" select="key('element','RawbAwpVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
								<xsl:variable name="Softwareprodukthash" select="key('element','AwpSwpVerbindung')/field[@name='start'][text()=$Anwendungsprodukthash]/../field[@name='end']/text()" />
													
								<td>
									<xsl:variable name="Softwareprodukt" select="key('element','Softwareprodukt')[@hash=$Softwareprodukthash]/field[@name='name']/text()" />
									<xsl:value-of select="$Softwareprodukt"/> &#160;
								</td>
							
								<!--					
								<td>
									<xsl:variable name="Datenbank" select="key('element','Datenbanksystem')[@hash=$Datenbankhash]/field[@name='name']/text()" />
									<xsl:value-of select="$Datenbank" /> &#160;
								</td>
								-->
		
								<td>
									<xsl:variable name="DBVerwaltungssystem" select="key('element','DBVerwaltungssystem')[@hash=$DBVShash]/field[@name='name']/text()" />
									<xsl:value-of select="$DBVerwaltungssystem" /> &#160;
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:otherwise>
		</xsl:choose>


		<h3>Paper-based application components</h3>

		<xsl:variable name="anzahl" select="count(key('element','KonAnwendungsbaustein'))" />
		<xsl:choose>
			<xsl:when test="$anzahl=0">
				<xsl:text>nonexistent</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<table cellspacing="0" cellpadding="3" border="1">
					<tr>
						<th><xsl:value-of select="'Name'" /></th>
						<th><xsl:value-of select="'Description'" /></th>
					</tr>
					<tbody align="left" valign="top">
						<xsl:for-each select="key('element','KonAnwendungsbaustein')">
							<xsl:sort data-type="text" select="child::field[@name='name']" />
							<tr>
								<td>
									<xsl:value-of select="str:replaceSubstring(child::field[@name='name'], '\-', '')" />&#160;
								</td>
								<td>
									<xsl:value-of select="str:replaceSubstring(child::field[@name='description'], '&#x000a;', $zeilenumbruch)" disable-output-escaping="yes" />&#160;
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:otherwise>
		</xsl:choose>

	</body>
</html>
</xsl:template>

</xsl:stylesheet>