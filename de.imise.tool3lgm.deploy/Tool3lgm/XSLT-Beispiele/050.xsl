<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Eigenschaften von PCs und Servern -->
<!--type: html -->
<!--description: Welche PCs sind so veraltet, dass sie ausgetauscht gehören? -->
<!--author: Thomas Rudert 09.09.2003 -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">

<xsl:variable name="header_text" select="'Welche PCs sind so veraltet, dass sie ausgetauscht geh&#246;ren?'" />
<xsl:variable name="note_text" select="'Hinweis:'" />
<xsl:variable name="notes_text" select="'Da das Attribut &#34;veraltet&#34; nicht eindeutig definiert werden kann und die Prozessorbezeichnung nicht eindeutig ist, werden alle physischen Datenverarbeitungsbaustein ausgebenen die in ihrer Bausteintyp-Bezeichnung die Zeichenkette &#34;PC&#34;, &#34;Server&#34; oder &#34;SERVER&#34; enthalten. Um eine gewisse Gruppierung dieser DVBausteine zu erreichen, werden sie alphabetisch nach ihrer Prozessorbezeichnung sortiert.'" />
<xsl:variable name="name_text" select="'Bezeichnung'" />
<xsl:variable name="cpu_text" select="'Prozessor'" />
<xsl:variable name="ram_text" select="'RAM'" />
<xsl:variable name="hdd_text" select="'HDD'" />
<xsl:variable name="oper_text" select="'Betriebssystem'" />
<xsl:variable name="desc_text" select="'Beschreibung'" />
<xsl:variable name="inve_text" select="'InventarNr'" />
<xsl:variable name="loca_text" select="'Standort'" />

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

<xsl:function name="str:removeLineBreak" as="xs:string">
	<xsl:param name="string" as="xs:string" />
	<xsl:sequence select="str:replaceSubstring($string, '\-', '')" />
</xsl:function>

<!-- bei der Ausgabe muss 'disable-output-escaping' auf 'yes' gesetzt werden -->
<xsl:variable name="zeilenumbruch" select="'&lt;br/&gt;'" />

<!-- Schluessel zur Auswahl von Elementen -->

<!-- Kante von Physischen DVBaustein zu Bausteintyp (Parameter BAT) -->
<xsl:key name="kantePhyBau" match="/modell_3lgm_2/objects/element[@class='PdvbBtypVerbindung']" use="child::field[@name='end']" />

<!-- Kante von Physischen DVBaustein zu Standort-->
<xsl:key name="kantePhySta" match="/modell_3lgm_2/objects/element[@class='PdvbStoVerbindung']" use="child::field[@name='start']" />

<!-- Elementasuwahl mittels Hashcode -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1><xsl:value-of select="$header_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<h3><xsl:value-of select="$note_text" /></h3>
		<p><xsl:value-of select="$notes_text" /></p>
		<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='Bausteintyp']">
			<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
		</xsl:apply-templates>
	</body>
</html>
</xsl:template>

<xsl:template match="/modell_3lgm_2/objects/element[@class='Bausteintyp']">
	<xsl:if test="contains(child::field[@name='name'], 'PC') or contains(child::field[@name='name'], 'Server') or contains(child::field[@name='name'], 'SERVER')">
		<h3><xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" /></h3>
		<table border="1" cellspacing="0" cellpadding="3">
			<thead><tr><th><xsl:value-of select="$name_text" /></th><th><xsl:value-of select="$cpu_text" /></th><th><xsl:value-of select="$ram_text" /></th><th><xsl:value-of select="$hdd_text" /></th><th><xsl:value-of select="$oper_text" /></th><th><xsl:value-of select="$desc_text" /></th><th><xsl:value-of select="$inve_text" /></th><th><xsl:value-of select="$loca_text" /></th></tr></thead>
			<tbody valign="top" align="left">
				<xsl:for-each select="key('kantePhyBau', @hash)">
					<xsl:sort select="key('hash', child::field[@name='start'])/field[@name='processor']" order="ascending" data-type="text" />
					<xsl:call-template name="dvbaustein">
						<xsl:with-param name="baustein" select="key('hash', child::field[@name='start'])" />
					</xsl:call-template>
				</xsl:for-each>
			</tbody>
		</table>
	</xsl:if>
</xsl:template>

<xsl:template name="dvbaustein">
	<xsl:param name="baustein" />
	<tr>
		<td><xsl:value-of select="str:removeLineBreak($baustein/field[@name='name'])" />&#160;</td>
		<td><xsl:value-of select="$baustein/field[@name='processor']" />&#160;</td>
		<td><xsl:value-of select="$baustein/field[@name='ramsize']" />&#160;</td>
		<td><xsl:value-of select="$baustein/field[@name='disksize']" />&#160;</td>
		<td><xsl:value-of select="$baustein/field[@name='os_type']" />&#160;</td>
		<td><xsl:value-of select="str:replaceSubstring($baustein/field[@name='description'], '&#x000a;', $zeilenumbruch)" disable-output-escaping="yes"/>&#160;</td>
		<td><xsl:value-of select="$baustein/field[@name='inventar']" />&#160;</td>
		<td><xsl:value-of select="
			if (empty(key('hash', key('kantePhySta', $baustein/@hash)/field[@name='end'])/field[@name='name']))
				then
					''
				else
					str:removeLineBreak(key('hash', key('kantePhySta', $baustein/@hash)/field[@name='end'])/field[@name='name'])" />&#160;</td>
	</tr>
</xsl:template>

</xsl:stylesheet>