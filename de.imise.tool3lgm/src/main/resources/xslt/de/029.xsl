<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Organisationseinheiten: unterstützende Anwendungsbausteine -->
<!--type: html -->
<!--description: Welche Organisationseinheiten nutzen welche Anwendungsbausteine? -->
<!--author: Oliver Heller -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:str="http://whatever"
	version="2.0" exclude-result-prefixes="str">

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

	<!-- Funktion entfernt das Zeilenumbruch Zeichen '\-' aus dem String -->
	<xsl:function name="str:removeLineBreak" as="xs:string">
		<xsl:param name="string" as="xs:string" />
		<xsl:sequence select="str:replaceSubstring($string, '\-', '')" />
	</xsl:function>

	<!-- Schluessel zur Auswahl von Elementen -->

	<!-- alle Kanten von Aufgabe zu einer AufgabeOrgansisationseinheitKombination 
		(AufOrgKomb - KOH) -->
	<xsl:key name="AufOrgKomb"
		match="/modell_3lgm_2/objects/element[@class='AufAufOrgVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von AufOrgKomb zu einer Anwendungsbausteinkonfiguration 
		(ABK) -->
	<xsl:key name="ABK"
		match="/modell_3lgm_2/objects/element[@class='AwbkAufOrgVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von AufOrgKomb zu einer Organisationseinheit (OEH) -->
	<xsl:key name="OEH"
		match="/modell_3lgm_2/objects/element[@class='OrgAufOrgVerbindung']"
		use="child::field[@name='end']" />

	<!-- alle Kanten von AnwBauKonf zu Anwendungsbaustein(rechnerunt., konvent., 
		gemischt) finden -->
	<xsl:key name="AWB"
		match="/modell_3lgm_2/objects/element[@class='AwbAwbkVerbindung']"
		use="child::field[@name='start']" />


	<!-- alle Kanten von Anwendungsbaustein zu Anwendungsprogramm finden -->
	<xsl:key name="AWP"
		match="/modell_3lgm_2/objects/element[@class='RawbAwpVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von Anwendungsprogramm zu Software finden -->
	<xsl:key name="SW"
		match="/modell_3lgm_2/objects/element[@class='AwpSwpVerbindung']" use="child::field[@name='start']" />



	<!-- Element mit Hashcode finden -->
	<xsl:key name="hash" match="/modell_3lgm_2/objects/element"
		use="@hash" />

	<!-- Wurzelknoten -->
	<xsl:template match="/">
		<html>
			<head>
				<title>Welche Organisationseinheiten nutzen welche
					Anwendungsbausteine?</title>
				<meta name="author" content="Tool3lgm" />
			</head>
			<body>
				<xsl:comment>
					Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell
					erzeugt.
				</xsl:comment>
				<h1>Welche Organisationseinheiten nutzen welche Anwendungsbausteine?</h1>
				<h2>
					<xsl:value-of select="modell_3lgm_2/header/title" />
				</h2>
				<table border="1" cellpadding="2" cellspacing="0">
					<tr>
						<th>Organisationseinheit</th>
						<th>Anwendungsbaustein</th>
					</tr>
					<tbody valign="top" align="left">
						<tr>
							<xsl:apply-templates
								select="modell_3lgm_2/objects/element[@class='Organisationseinheit']">

								<!-- nach den Namen der Aufgaben sortieren -->
								<xsl:sort select="str:removeLineBreak(child::field[@name='name'])"
									order="ascending" data-type="text" />
							</xsl:apply-templates>
						</tr>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="element[@class='Organisationseinheit']">
		<xsl:variable name="AWBs"
			select="key('hash',key('AWB', key('ABK',key('OEH',@hash)/child::field[@name='start'])/child::field[@name='end'])/child::field[@name='end'])" />
		<tr>
			<td>
				<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
			</td>
			<td>
				<xsl:for-each select="$AWBs">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])"
						order="ascending" data-type="text" />
					<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
					<xsl:text> (</xsl:text>
					<xsl:value-of
						select="key('hash',key('SW',key('AWP',@hash)/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
					<xsl:text>) </xsl:text>
					<br />
					<xsl:if test="position() != last()">
						<br />
					</xsl:if>
				</xsl:for-each>
				<br />
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
