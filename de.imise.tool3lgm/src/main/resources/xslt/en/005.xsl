<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Physical Data Processing Components: Overview -->
<!--type: html -->
<!--description: extracts all physical data processing components together 
	with their names and descriptions from a model -->
<!--author: Thomas Trommer -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:str="http://whatever"
	version="2.0">

	<!-- Funktion zum Ersetzen aller Vorkommen eines Teilstrings durch einen 
		anderen Teilstring -->
	<xsl:function name="str:replaceSubstring" as="xs:string">
		<xsl:param name="inputString" as="xs:string" />
		<xsl:param name="oldSubstring" as="xs:string" />
		<xsl:param name="newSubstring" as="xs:string" />
		<xsl:choose>
			<xsl:when test="contains($inputString, $oldSubstring)">
				<xsl:sequence
					select="concat(concat(substring-before($inputString, $oldSubstring), $newSubstring), str:replaceSubstring(substring-after($inputString, $oldSubstring), $oldSubstring, $newSubstring))" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$inputString" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:variable name="header_text" select="'Physical data processing components'" />

	<xsl:key name="element" match="modell_3lgm_2/objects/element"
		use="@class" />

	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="$header_text" />
				</title>
				<meta name="author" content="Tool3lgm" />
			</head>
			<body>
				<xsl:comment>
					This HTML file was generated automatically from a 3LGM&#178; model.
				</xsl:comment>
				<h1>
					<xsl:value-of select="$header_text" />
				</h1>
				<h2>
					<xsl:value-of select="modell_3lgm_2/header/title" />
				</h2>
				<table cellspacing="0" cellpadding="3" border="1">
					<tr>
						<th>Name</th>
						<th>Location</th>
						<th>Component Type</th>
					</tr>
					<tbody align="left" valign="top">
						<xsl:for-each select="key('element','PhysischerDVBaustein')">
							<xsl:sort data-type="text" select="child::field[@name='name']" />
							<tr>
								<td>
									<xsl:variable name="PDVB" select="child::field[@name='name']" />
									<xsl:if test="string($PDVB)">
										<xsl:value-of select="str:replaceSubstring($PDVB, '\-', '')" />
									</xsl:if>
									&#160;
								</td>

								<!--Hash des aktuellen Bausteins -->
								<xsl:variable name="hash" select="@hash" />

								<!--Voraussetzungen: Standorthash und Typhash immer im End-Feld einer 
									Relation definiert nur ein Standort und nur ein Typ -->


								<!--Versuch Standort- bzw. Typverbindungen zu finden -->
								<xsl:variable name="Standorthash"
									select="key('element','PdvbStoVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
								<xsl:variable name="Bausteintyphash"
									select="key('element','PdvbBtypVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
								<td>
									<xsl:variable name="Standort"
										select="key('element','Standort')[@hash=$Standorthash]/field[@name='name']/text()" />
									<xsl:if test="string($Standort)">
										<xsl:value-of select="str:replaceSubstring($Standort, '\-', '')" />
									</xsl:if>
									&#160;
								</td>
								<td>
									<xsl:variable name="Bausteintyp"
										select="key('element','Bausteintyp')[@hash=$Bausteintyphash]/field[@name='name']/text()" />
									<xsl:if test="string($Bausteintyp)">
										<xsl:value-of select="str:replaceSubstring($Bausteintyp, '\-', '')" />
									</xsl:if>
									&#160;
								</td>


							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
