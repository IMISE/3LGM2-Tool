<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Organisationseinheit verfügt über PC -->
<!--type: html -->
<!--description: Welche Organisationseinheiten verfügen über welche PCs? -->
<!--author: Thomas Rudert 02.02.2004 -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">

<xsl:variable name="header_text" select="'Welche Organisationseinheiten verf&#252;gen &#252;ber welche PCs?'" />
<xsl:variable name="org_text" select="'Organisationseinheit'" />
<xsl:variable name="pc_text" select="'PC'" />

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

<!-- Schluessel zur Auswahl von Elementen -->
<!-- Kante von Datenverarbeitungsbaustein-Konfiguration zu Anwendungsbaustein -->
<xsl:key name="kanteDbkAnb" match="/modell_3lgm_2/objects/element[@class='PdvbkAwbVerbindung']" use="child::field[@name='start']" />

<!-- Kante von physischen Datenverarbeitungsbaustein zu Datenverarbeitungsbaustein-Konfiguration -->
<xsl:key name="kantePhyDbk" match="/modell_3lgm_2/objects/element[@class='PdvbPdvbkVerbindung']" use="child::field[@name='start']" />

<!-- Knotenauswahl mit hashcode -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- alle Kanten von AufOrgKomb (KOH) zu einer Organisationseinheit (OEH) -->
<xsl:key name="kanteKohOeh" match="/modell_3lgm_2/objects/element[@class='OrgAufOrgVerbindung']" use="child::field[@name='end']" />

<!-- alle Kanten von AufOrgKomb zu einer Anwendungsbausteinkonfiguration (ABK) -->
<xsl:key name="kanteAbkKoh" match="/modell_3lgm_2/objects/element[@class='AwbkAufOrgVerbindung']" use="child::field[@name='start']" />

<!-- alle Kanten von AnwBauKonf zu Anwendungsbaustein(rechnerunt., konvent., gemischt)  finden -->
<xsl:key name="kanteAnbAbk" match="/modell_3lgm_2/objects/element[@class='AwbAwbkVerbindung']" use="child::field[@name='start']" />

<!-- Kante von Physischen DVBaustein zu Bausteintyp (Parameter PHY) -->
<xsl:key name="kantePhyBat" match="/modell_3lgm_2/objects/element[@class='PdvbBtypVerbindung']" use="child::field[@name='start']" />

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
		<table border="1" cellpadding="3" cellspacing="0">
			<thead><tr><th><xsl:value-of select="$org_text" /></th><th><xsl:value-of select="$pc_text" /></th></tr></thead>
			<tbody align="left" valign="top">
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='Organisationseinheit']">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tbody>
		</table>
	</body>
</html>
</xsl:template>

<xsl:template match="modell_3lgm_2/objects/element[@class='Organisationseinheit']">
	<tr>
		<td><xsl:value-of select="str:removeLineBreak(field[@name='name'])" /></td>
		<td>
			<xsl:call-template name="rekursiv1">
				<xsl:with-param name="oeh" select="@hash" />
			</xsl:call-template>
		</td>
	</tr>
</xsl:template>

<xsl:template name="rekursiv1">
	<xsl:param name="oeh" />
	<xsl:param name="maxIndex" select="count(key('kanteKohOeh', $oeh))" />
	<xsl:param name="index" select="1" />
	<xsl:param name="result" select="'§$§'" />
	<xsl:choose>
		<xsl:when test="$index &gt; $maxIndex">
			<xsl:choose>
				<xsl:when test="string-length(substring-after($result, '§$§')) &gt; 0">
					<xsl:call-template name="ausgabe">
						<xsl:with-param name="result" select="substring-after($result, '§$§')" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>&#160;</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursiv2">
				<xsl:with-param name="result" select="$result" />
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="koh" select="key('kanteKohOeh', $oeh)[$index]/field[@name='start']" />
				<xsl:with-param name="maxIndex1" select="$maxIndex" />
				<xsl:with-param name="index1" select="$index" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursiv2">
	<xsl:param name="koh" />
	<xsl:param name="oeh" />
	<xsl:param name="maxIndex1" />
	<xsl:param name="index1" />
	<xsl:param name="result" />
	<xsl:param name="maxIndex" select="count(key('kanteAbkKoh', $koh))" />
	<xsl:param name="index" select="1" />
	<xsl:choose>
		<xsl:when test="$index &gt; $maxIndex">
			<xsl:call-template name="rekursiv1">
				<xsl:with-param name="result" select="$result" />
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="maxIndex" select="$maxIndex1" />
				<xsl:with-param name="index" select="$index1 + 1" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursiv3">
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="abk" select="key('kanteAbkKoh', $koh)[$index]/field[@name='end']" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
				<xsl:with-param name="maxIndex2" select="$maxIndex" />
				<xsl:with-param name="index2" select="$index" />
				<xsl:with-param name="result" select="$result" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursiv3">
	<xsl:param name="abk" />
	<xsl:param name="koh" />
	<xsl:param name="oeh" />
	<xsl:param name="maxIndex1" />
	<xsl:param name="index1" />
	<xsl:param name="maxIndex2" />
	<xsl:param name="index2" />
	<xsl:param name="result" />
	<xsl:param name="maxIndex" select="count(key('kanteAnbAbk', $abk))" />
	<xsl:param name="index" select="1" />
	<xsl:choose>
		<xsl:when test="$index &gt; $maxIndex">
			<xsl:call-template name="rekursiv2">
				<xsl:with-param name="result" select="$result" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="maxIndex" select="$maxIndex2" />
				<xsl:with-param name="index" select="$index2 + 1" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursiv4">
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="abk" select="$abk" />
				<xsl:with-param name="anb" select="key('kanteAnbAbk', $abk)[$index]/field[@name='end']" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
				<xsl:with-param name="maxIndex2" select="$maxIndex2" />
				<xsl:with-param name="index2" select="$index2" />
				<xsl:with-param name="maxIndex3" select="$maxIndex" />
				<xsl:with-param name="index3" select="$index" />
				<xsl:with-param name="result" select="$result" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursiv4">
	<xsl:param name="anb" />
	<xsl:param name="abk" />
	<xsl:param name="koh" />
	<xsl:param name="oeh" />
	<xsl:param name="maxIndex1" />
	<xsl:param name="index1" />
	<xsl:param name="maxIndex2" />
	<xsl:param name="index2" />
	<xsl:param name="maxIndex3" />
	<xsl:param name="index3" />
	<xsl:param name="result" />
	<xsl:param name="maxIndex" select="count(key('kanteDbkAnb', $anb))" />
	<xsl:param name="index" select="1" />
	<xsl:choose>
		<xsl:when test="$index &gt; $maxIndex">
			<xsl:call-template name="rekursiv3">
				<xsl:with-param name="result" select="$result" />
				<xsl:with-param name="abk" select="$abk" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="maxIndex" select="$maxIndex3" />
				<xsl:with-param name="index" select="$index3 + 1" />
				<xsl:with-param name="maxIndex2" select="$maxIndex2" />
				<xsl:with-param name="index2" select="$index2" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursiv5">
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="abk" select="$abk" />
				<xsl:with-param name="anb" select="$anb" />
				<xsl:with-param name="dbk" select="key('kanteDbkAnb', $anb)[$index]/field[@name='end']" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
				<xsl:with-param name="maxIndex2" select="$maxIndex2" />
				<xsl:with-param name="index2" select="$index2" />
				<xsl:with-param name="maxIndex3" select="$maxIndex3" />
				<xsl:with-param name="index3" select="$index3" />
				<xsl:with-param name="maxIndex4" select="$maxIndex" />
				<xsl:with-param name="index4" select="$index" />
				<xsl:with-param name="result" select="$result" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursiv5">
	<xsl:param name="dbk" />
	<xsl:param name="anb" />
	<xsl:param name="abk" />
	<xsl:param name="koh" />
	<xsl:param name="oeh" />
	<xsl:param name="maxIndex1" />
	<xsl:param name="index1" />
	<xsl:param name="maxIndex2" />
	<xsl:param name="index2" />
	<xsl:param name="maxIndex3" />
	<xsl:param name="index3" />
	<xsl:param name="maxIndex4" />
	<xsl:param name="index4" />
	<xsl:param name="result" />
	<xsl:param name="maxIndex" select="count(key('kantePhyDbk', $dbk))" />
	<xsl:param name="index" select="1" />
	<xsl:choose>
		<xsl:when test="$index &gt; $maxIndex">
			<xsl:call-template name="rekursiv4">
				<xsl:with-param name="result" select="$result" />
				<xsl:with-param name="anb" select="$anb" />
				<xsl:with-param name="abk" select="$abk" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="maxIndex" select="$maxIndex4" />
				<xsl:with-param name="index" select="$index4 + 1" />
				<xsl:with-param name="maxIndex3" select="$maxIndex3" />
				<xsl:with-param name="index3" select="$index3" />
				<xsl:with-param name="maxIndex2" select="$maxIndex2" />
				<xsl:with-param name="index2" select="$index2" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursiv6">
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="abk" select="$abk" />
				<xsl:with-param name="anb" select="$anb" />
				<xsl:with-param name="dbk" select="$dbk" />
				<xsl:with-param name="phy" select="key('kantePhyDbk', $dbk)[$index]/field[@name='end']" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
				<xsl:with-param name="maxIndex2" select="$maxIndex2" />
				<xsl:with-param name="index2" select="$index2" />
				<xsl:with-param name="maxIndex3" select="$maxIndex3" />
				<xsl:with-param name="index3" select="$index3" />
				<xsl:with-param name="maxIndex4" select="$maxIndex4" />
				<xsl:with-param name="index4" select="$index4" />
				<xsl:with-param name="maxIndex5" select="$maxIndex" />
				<xsl:with-param name="index5" select="$index" />
				<xsl:with-param name="result" select="$result" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="rekursiv6">
	<xsl:param name="phy" />
	<xsl:param name="dbk" />
	<xsl:param name="anb" />
	<xsl:param name="abk" />
	<xsl:param name="koh" />
	<xsl:param name="oeh" />
	<xsl:param name="maxIndex1" />
	<xsl:param name="index1" />
	<xsl:param name="maxIndex2" />
	<xsl:param name="index2" />
	<xsl:param name="maxIndex3" />
	<xsl:param name="index3" />
	<xsl:param name="maxIndex4" />
	<xsl:param name="index4" />
	<xsl:param name="maxIndex5" />
	<xsl:param name="index5" />
	<xsl:param name="result" />
	<xsl:choose>
		<xsl:when test="count(key('kantePhyBat', $phy)) &gt; 0">
			<xsl:variable name="kante" select="key('kantePhyBat', $phy)[1]" />
			<xsl:choose>
				<xsl:when test="contains(key('hash', $kante/field[@name='end'])/field[@name='name'], 'PC')">
					<xsl:choose>
						<xsl:when test="not(contains($result, concat('§$§', key('hash', $kante/field[@name='start'])/field[@name='name'], '§$§')))" >
							<xsl:call-template name="rekursiv5">
								<xsl:with-param name="result" select="concat($result, key('hash', $kante/field[@name='start'])/field[@name='name'], '§$§')" />
								<xsl:with-param name="dbk" select="$dbk" />
								<xsl:with-param name="anb" select="$anb" />
								<xsl:with-param name="abk" select="$abk" />
								<xsl:with-param name="koh" select="$koh" />
								<xsl:with-param name="oeh" select="$oeh" />
								<xsl:with-param name="maxIndex" select="$maxIndex5" />
								<xsl:with-param name="index" select="$index5 + 1" />
								<xsl:with-param name="maxIndex4" select="$maxIndex4" />
								<xsl:with-param name="index4" select="$index4" />
								<xsl:with-param name="maxIndex3" select="$maxIndex3" />
								<xsl:with-param name="index3" select="$index3" />
								<xsl:with-param name="maxIndex2" select="$maxIndex2" />
								<xsl:with-param name="index2" select="$index2" />
								<xsl:with-param name="maxIndex1" select="$maxIndex1" />
								<xsl:with-param name="index1" select="$index1" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="rekursiv5">
								<xsl:with-param name="result" select="$result" />
								<xsl:with-param name="dbk" select="$dbk" />
								<xsl:with-param name="anb" select="$anb" />
								<xsl:with-param name="abk" select="$abk" />
								<xsl:with-param name="koh" select="$koh" />
								<xsl:with-param name="oeh" select="$oeh" />
								<xsl:with-param name="maxIndex" select="$maxIndex5" />
								<xsl:with-param name="index" select="$index5 + 1" />
								<xsl:with-param name="maxIndex4" select="$maxIndex4" />
								<xsl:with-param name="index4" select="$index4" />
								<xsl:with-param name="maxIndex3" select="$maxIndex3" />
								<xsl:with-param name="index3" select="$index3" />
								<xsl:with-param name="maxIndex2" select="$maxIndex2" />
								<xsl:with-param name="index2" select="$index2" />
								<xsl:with-param name="maxIndex1" select="$maxIndex1" />
								<xsl:with-param name="index1" select="$index1" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="rekursiv5">
						<xsl:with-param name="result" select="$result" />
						<xsl:with-param name="dbk" select="$dbk" />
						<xsl:with-param name="anb" select="$anb" />
						<xsl:with-param name="abk" select="$abk" />
						<xsl:with-param name="koh" select="$koh" />
						<xsl:with-param name="oeh" select="$oeh" />
						<xsl:with-param name="maxIndex" select="$maxIndex5" />
						<xsl:with-param name="index" select="$index5 + 1" />
						<xsl:with-param name="maxIndex4" select="$maxIndex4" />
						<xsl:with-param name="index4" select="$index4" />
						<xsl:with-param name="maxIndex3" select="$maxIndex3" />
						<xsl:with-param name="index3" select="$index3" />
						<xsl:with-param name="maxIndex2" select="$maxIndex2" />
						<xsl:with-param name="index2" select="$index2" />
						<xsl:with-param name="maxIndex1" select="$maxIndex1" />
						<xsl:with-param name="index1" select="$index1" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="rekursiv5">
				<xsl:with-param name="result" select="$result" />
				<xsl:with-param name="dbk" select="$dbk" />
				<xsl:with-param name="anb" select="$anb" />
				<xsl:with-param name="abk" select="$abk" />
				<xsl:with-param name="koh" select="$koh" />
				<xsl:with-param name="oeh" select="$oeh" />
				<xsl:with-param name="maxIndex" select="$maxIndex5" />
				<xsl:with-param name="index" select="$index5 + 1" />
				<xsl:with-param name="maxIndex4" select="$maxIndex4" />
				<xsl:with-param name="index4" select="$index4" />
				<xsl:with-param name="maxIndex3" select="$maxIndex3" />
				<xsl:with-param name="index3" select="$index3" />
				<xsl:with-param name="maxIndex2" select="$maxIndex2" />
				<xsl:with-param name="index2" select="$index2" />
				<xsl:with-param name="maxIndex1" select="$maxIndex1" />
				<xsl:with-param name="index1" select="$index1" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="ausgabe">
	<xsl:param name="result" />
	<xsl:if test="string-length($result) &gt; 0">
		<xsl:value-of select="str:removeLineBreak(substring-before($result, '§$§'))" /><br/>
		<xsl:call-template name="ausgabe">
			<xsl:with-param name="result" select="substring-after($result, '§$§')" />
		</xsl:call-template>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>