<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Application Components: On which physical data processing components -->
<!--type: html -->
<!--description: Which application components run on which physical data processing components? -->
<!--author: Thomas Rudert -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">

<xsl:variable name="header_text" select="'Which application components run on which physical data processing components?'" />
<xsl:variable name="cont_text" select="'Contents'" />
<xsl:variable name="comp_text" select="'Computer-based Application Components'" />
<xsl:variable name="paper_text" select="'Paper-based application components'" />
<xsl:variable name="appl_text" select="'Application Component'" />
<xsl:variable name="conf_text" select="'Data processing component configuration'" />
<xsl:variable name="phys_text" select="'Physical data processing Component'" />

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
<xsl:key name="kantePhyDbk" match="/modell_3lgm_2/objects/element[@class='PdvbPdvbkVerbindung']" use="child::field[@name='end']" />


<!-- alle Kanten von Anwendungsbaustein zu Anwendungsprogramm finden -->
<xsl:key name="AWP" match="/modell_3lgm_2/objects/element[@class='RawbAwpVerbindung']" use="child::field[@name='start']" /> 

<!-- alle Kanten von Anwendungsprogramm zu Software finden -->
<xsl:key name="SW" match="/modell_3lgm_2/objects/element[@class='AwpSwpVerbindung']" use="child::field[@name='start']" /> 

<!-- Knotenauswahl mit hashcode -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>This HTML file was generated automatically from a 3LGM&#178; model.</xsl:comment>
		<h1><xsl:value-of select="$header_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<h3><xsl:value-of select="$cont_text" /></h3>
		<ul>
			<li><a href="#rech"><xsl:value-of select="$comp_text" /></a></li>
			<li><a href="#kon"><xsl:value-of select="$paper_text" /></a></li>
		</ul>
			
		<a name="rech"><h3><xsl:value-of select="$comp_text" /></h3></a>
		<table border="1" cellpadding="3" cellspacing="0" width="100%">
			<thead><tr><th width="40%"><xsl:value-of select="$appl_text" /></th><th width="20%"><xsl:value-of select="$conf_text" /></th><th width="40%"><xsl:value-of select="$phys_text" /></th></tr></thead>
			<tbody align="left" valign="top">
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='RechAnwendungsbaustein']">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tbody>
		</table>
		
		<a name="kon"><h3><xsl:value-of select="$paper_text" /></h3></a>
		<table border="1" cellpadding="3" cellspacing="0" width="100%">
			<thead><tr><th width="40%"><xsl:value-of select="$appl_text" /></th><th width="20%"><xsl:value-of select="$conf_text" /></th><th width="40%"><xsl:value-of select="$phys_text" /></th></tr></thead>
			<tbody align="left" valign="top">
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='KonAnwendungsbaustein']">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tbody>
		</table>
	</body>
</html>
</xsl:template>

<xsl:template match="modell_3lgm_2/objects/element[@class='RechAnwendungsbaustein']">
	<tr>
		<td>
			<xsl:call-template name="getRowspanRekursiv">
				<xsl:with-param name="kanten" select="key('kanteDbkAnb', @hash)" />
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="string-length(normalize-space(child::field[@name='name'])) &gt; 0">
					<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
					<xsl:text> (</xsl:text><xsl:value-of select="key('hash',key('SW',key('AWP',@hash)/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
					<xsl:text>) </xsl:text><br/>
				</xsl:when>
				<xsl:otherwise>
					&#160;
				</xsl:otherwise>
			</xsl:choose>
		</td>
		<xsl:choose>
			<xsl:when test="count(key('kanteDbkAnb', @hash)) &lt; 1">
				<td>&#160;</td><td>&#160;</td>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="datenverarbeitungsbaustein_konfiguration">
					<xsl:with-param name="anwendungsbaustein" select="@hash" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</tr>
</xsl:template>

<xsl:template match="modell_3lgm_2/objects/element[@class='KonAnwendungsbaustein']">
	<tr>
		<td>
			<xsl:call-template name="getRowspanRekursiv">
				<xsl:with-param name="kanten" select="key('kanteDbkAnb', @hash)" />
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="string-length(normalize-space(child::field[@name='name'])) &gt; 0">
					<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
				</xsl:when>
				<xsl:otherwise>
					&#160;
				</xsl:otherwise>
			</xsl:choose>
		</td>
		<xsl:choose>
			<xsl:when test="count(key('kanteDbkAnb', @hash)) &lt; 1">
				<td>&#160;</td><td>&#160;</td>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="datenverarbeitungsbaustein_konfiguration">
					<xsl:with-param name="anwendungsbaustein" select="@hash" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</tr>
</xsl:template>

<xsl:template name="datenverarbeitungsbaustein_konfiguration">
	<xsl:param name="anwendungsbaustein"/>
	<xsl:for-each select="key('kanteDbkAnb', $anwendungsbaustein)">
		<xsl:variable name="anzahlPhy" select="count(key('kantePhyDbk', child::field[@name='end']))" />
		<xsl:choose>
			<xsl:when test="$anzahlPhy &lt; 1">
				<td>
					<xsl:choose>		
						<xsl:when test="key('hash' ,child::field[@name='end'])/field[@name='name'] = '- &lt;Konfiguration&gt; -'">
							Konfiguration <xsl:value-of select="position()" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="str:removeLineBreak(key('hash' ,child::field[@name='end'])/field[@name='name'])" />
						</xsl:otherwise>
					</xsl:choose>
					</td><td>&#160;</td>
			</xsl:when>
			<xsl:when test="$anzahlPhy &gt; 1">
				<td>
					<xsl:attribute name="rowspan"><xsl:value-of select="$anzahlPhy" /></xsl:attribute>
					<xsl:choose>
						<xsl:when test="key('hash' ,child::field[@name='end'])/field[@name='name'] = '- &lt;Konfiguration&gt; -'">
							Konfiguration <xsl:value-of select="position()" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="str:removeLineBreak(key('hash' ,child::field[@name='end'])/field[@name='name'])" />
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:when>
			<xsl:otherwise>
				<td>
					<xsl:choose>
						<xsl:when test="key('hash' ,child::field[@name='end'])/field[@name='name'] = '- &lt;Konfiguration&gt; -'">
							Konfiguration <xsl:value-of select="position()" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="str:removeLineBreak(key('hash' ,child::field[@name='end'])/field[@name='name'])" />
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:call-template name="physischer_DVBaustein">
			<xsl:with-param name="konfiguration" select="child::field[@name='end']" />
		</xsl:call-template>

		<xsl:if test="position() != last()">
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="physischer_DVBaustein">
	<xsl:param name="konfiguration" />
	<xsl:for-each select="key('kantePhyDbk', $konfiguration)">
		<td><xsl:value-of select="str:removeLineBreak(key('hash', child::field[@name='start'])/field[@name='name'])" /></td>
		<xsl:if test="position() != last()" >
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="getRowspanRekursiv">
	<xsl:param name="counter" select="0" />
	<xsl:param name="index" select="1" />
	<xsl:param name="kanten" />
	<xsl:param name="maxIndex" select="count($kanten)" />

	<xsl:choose>
		<xsl:when test="$index &gt; $maxIndex">
			<xsl:if test="$counter &gt; 1">
				<xsl:attribute name="rowspan"><xsl:value-of select="$counter" /></xsl:attribute>
			</xsl:if>
		</xsl:when>
		<xsl:otherwise>
			<xsl:variable name="number" select="count(key('kantePhyDbk', $kanten[$index]/field[@name='end']))" />
			<xsl:choose>
				<xsl:when test="$number &gt; 0 ">
					<xsl:call-template name="getRowspanRekursiv">
						<xsl:with-param name="counter" select="$counter + $number" />
						<xsl:with-param name="index" select="$index + 1" />
						<xsl:with-param name="kanten" select="$kanten" />
						<xsl:with-param name="maxIndex" select="$maxIndex" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getRowspanRekursiv">
						<xsl:with-param name="counter" select="$counter + 1" />
						<xsl:with-param name="index" select="$index + 1" />
						<xsl:with-param name="kanten" select="$kanten" />
						<xsl:with-param name="maxIndex" select="$maxIndex" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
