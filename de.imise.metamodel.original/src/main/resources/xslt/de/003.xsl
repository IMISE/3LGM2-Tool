<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Organisationseinheiten: Übersicht -->
<!--type: html -->
<!--description: extrahiert alle Organisationseinheit mit Namen aus einem 
	Modell -->
<!--author: Thomas Rudert -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:str="http://whatever"
	version="2.0" exclude-result-prefixes="str">

	<xsl:variable name="zielobjekt" select="'Organisationseinheit'" />

	<xsl:variable name="header_text" select="'Organisationseinheiten'" />
	<xsl:variable name="name_text" select="'Bezeichnung'" />
	<xsl:variable name="desc_text" select="'Beschreibung'" />

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

	<!-- bei der Ausgabe muss 'disable-output-escaping' auf 'yes' gesetzt werden -->
	<xsl:variable name="zeilenumbruch" select="'&lt;br/&gt;'" />

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
					Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell
					erzeugt.
				</xsl:comment>
				<h1>
					<xsl:value-of select="$header_text" />
				</h1>
				<h2>
					<xsl:value-of select="modell_3lgm_2/header/title" />
				</h2>
				<table cellspacing="0" cellpadding="3" border="1">
					<tr>
						<th>
							<xsl:value-of select="$name_text" />
						</th>
						<th>
							<xsl:value-of select="$desc_text" />
						</th>
					</tr>
					<tbody align="left" valign="top">
						<xsl:for-each select="key('element', $zielobjekt)">
							<xsl:sort data-type="text" select="child::field[@name='name']" />
							<tr>
								<td>
									<xsl:value-of
										select="str:replaceSubstring(child::field[@name='name'], '\-', '')" />
									&#160;
								</td>
								<td>
									<xsl:value-of
										select="str:replaceSubstring(child::field[@name='description'], '&#x000a;', $zeilenumbruch)"
										disable-output-escaping="yes" />
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