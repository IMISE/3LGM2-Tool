<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Functions supported by application components -->
<!--type: html -->
<!--description: What functions are still supported by paper-based application 
	components? For what functions are there different possibilites? -->
<!--author: Thomas Rudert, Oliver Heller -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:str="http://whatever"
	version="2.0" exclude-result-prefixes="str">

	<xsl:variable name="header_text"
		select="'What functions are still supported by paper-based application components? For what functions are there different possibilites?'" />
	<xsl:variable name="task_text" select="'Function'" />
	<xsl:variable name="comp_text"
		select="'Computer-based application components'" />
	<xsl:variable name="paper_text" select="'Paper-based application components'" />
	<xsl:variable name="hybr_text" select="'Mixed application components'" />

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

	<!-- alle Kanten von AnwBauKonf zu Anwendungsbausteinen finden -->
	<xsl:key name="ANB"
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
				<table border="1" cellpadding="3" cellspacing="0" width="100%">
					<tr>
						<th width="20%">
							<xsl:value-of select="$task_text" />
						</th>
						<th width="40%">
							<xsl:value-of select="$comp_text" />
						</th>
						<th width="40%">
							<xsl:value-of select="$paper_text" />
						</th>
					</tr>
					<tbody valign="top" align="left">
						<xsl:apply-templates
							select="modell_3lgm_2/objects/element[@class='Aufgabe']">
							<!-- nach den Namen der Aufgaben sortieren -->
							<xsl:sort select="str:removeLineBreak(child::field[@name='name'])"
								order="ascending" data-type="text" />
						</xsl:apply-templates>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="element[@class='Aufgabe']">
		<tr>
			<td width="16%">
				<xsl:value-of select="str:removeLineBreak(field[@name='name'])" />
			</td>
			<xsl:call-template name="anwendungsbaustein">
				<xsl:with-param name="aufgabe" select="@hash" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template name="anwendungsbaustein">
		<xsl:param name="aufgabe" />

		<td width="40%">
			<!--<xsl:for-each select="key('AufOrgKomb', $aufgabe)" > <xsl:for-each 
				select="key('ABK', field[@name='end'])"> <xsl:for-each select="key('ANB', 
				child::field[@name='end'])"> <xsl:variable name="awb" select="key('hash', 
				child::field[@name='end'])" /> <xsl:if test="$awb/@class = 'RechAnwendungsbaustein'"> 
				<xsl:value-of select="str:removeLineBreak($awb/child::field[@name='name'])" 
				/> <br/> </xsl:if> </xsl:for-each> </xsl:for-each> </xsl:for-each> -->
			<xsl:for-each
				select="key('hash',key('ANB',key('ABK', key('AufOrgKomb', $aufgabe)/child::field[@name='end'])/child::field[@name='end'])/ child::field[@name='end'])">
				<xsl:sort select="str:removeLineBreak(child::field[@name='name'])"
					order="ascending" data-type="text" />
				<xsl:if test="@class = 'RechAnwendungsbaustein'">
					<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
					<xsl:text> (</xsl:text>
					<xsl:value-of
						select="key('hash',key('SW',key('AWP',@hash)/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
					<xsl:text>) </xsl:text>
					<br />
				</xsl:if>
			</xsl:for-each>
			&#160;
		</td>

		<td width="40%">
			<!--<xsl:for-each select="key('AufOrgKomb', $aufgabe)" > <xsl:for-each 
				select="key('ABK', field[@name='end'])"> <xsl:for-each select="key('ANB', 
				child::field[@name='end'])"> <xsl:variable name="awb" select="key('hash', 
				child::field[@name='end'])" /> <xsl:if test="$awb/@class = 'KonAnwendungsbaustein'"> 
				<xsl:value-of select="str:removeLineBreak($awb/child::field[@name='name'])" 
				/> <br/> </xsl:if> </xsl:for-each> </xsl:for-each> </xsl:for-each> -->
			<xsl:for-each
				select="key('hash',key('ANB',key('ABK', key('AufOrgKomb', $aufgabe)/child::field[@name='end'])/child::field[@name='end'])/ child::field[@name='end'])">
				<xsl:sort select="str:removeLineBreak(child::field[@name='name'])"
					order="ascending" data-type="text" />
				<xsl:if test="@class = 'KonAnwendungsbaustein'">
					<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
					<br />
				</xsl:if>
			</xsl:for-each>
			&#160;
		</td>

	</xsl:template>

</xsl:stylesheet>
