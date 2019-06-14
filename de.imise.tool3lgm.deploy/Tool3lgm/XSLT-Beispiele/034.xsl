<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Wo wird welcher Kommunikationsstandard verwendet -->
<!--type: html -->
<!--description: Übersicht wo welcher Kommunikationsstandard verwendet wird -->
<!--author: Oliver Heller 19.01.2006 -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:str="http://whatever"
	version="2.0"
	exclude-result-prefixes="str">

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

<!-- alle Anwendungsbaustein-Kommunikationsschnittstellen-Kombinationen  -->
<xsl:key name="BSSKOM" match="/modell_3lgm_2/objects/element[@class='BssKommstVerbindung']" use="child::field[@name='end']" />

<xsl:key name="KOMBSS" match="/modell_3lgm_2/objects/element[@class='BssKommstVerbindung']" use="child::field[@name='start']" />

<xsl:key name="AWBKOM" match="/modell_3lgm_2/objects/element[@class='AwbKommssVerbindung']" use="child::field[@name='end']" />

<xsl:key name="KOMBEZETNT" match="/modell_3lgm_2/objects/element[@class='KommbezEtntVerbindung']" use="child::field[@name='start']"/>

<xsl:key name="ETNTKOM" match="/modell_3lgm_2/objects/element[@class='EtntKommstVerbindung']" use="child::field[@name='start']"/>

<xsl:key name="BSSETNT" match="/modell_3lgm2_2/objects/element[@class='BssEtntVerbindung']" use="child::field[@name='start']"/>

<!-- Element mit Hashcode finden -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title>Übersicht wo welcher Kommunikationsstandard verwendet wird</title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1>Übersicht wo welcher Kommunikationsstandard verwendet wird</h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<table border="1" cellpadding="4" cellspacing="0">
			<tr><th>Kommunikationsstandard</th><th>Schnittstellen</th><th>bestehende Verbindungen</th><th>mögliche Verbindungen</th></tr>
			<tbody valign="top" align="left"><tr>
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='Kommunikationsstandard']">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tr></tbody>
		</table>
	</body>
</html>
</xsl:template>

<xsl:template match="element[@class='Kommunikationsstandard']">
	<!-- Anzahl der Kanten von der Aufgabe zu einer AufgabeOrgansisationseinheitKombination (AufOrgKomb - KOH) -->
	<xsl:variable name="BSS" select="key('BSSKOM', @hash)" />
	<xsl:choose>
		<xsl:when test="count($BSS) &gt; 0">
			<td><xsl:value-of select="str:removeLineBreak(key('hash',$BSS/child::field[@name='end'])/child::field[@name='name'])" /></td>
			<td><xsl:for-each select="key('hash',$BSS/child::field[@name='start'])">
				<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
				<xsl:if test="position() != last()">
					<br/>
				</xsl:if>
			</xsl:for-each>	</td><td>
			<xsl:call-template name="KommBeziehung">
				<xsl:with-param name="KOM" select="@hash" />
			</xsl:call-template><br/></td>
			<td>
			<xsl:call-template name="KommBeziehung2">
				<xsl:with-param name="KOM" select="@hash" />
			</xsl:call-template><br/></td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<td><xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" /></td><td><br/></td><td><br/></td><td><br/></td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
	
	
</xsl:template>

<xsl:template name="KommBeziehung">
	<xsl:param name="KOM" />
	<xsl:variable name="KOMBEZ" select="/modell_3lgm_2/objects/element[@class='KommBeziehung']" />
	<xsl:for-each select="$KOMBEZ">
	<!-- Test ob Kommunikationsbeziehung eine ETNT-beziehung mit dem Kommunikationsstandard verwendet
		beide Schnittstellen den Kommunikationsstandard verwenden -->
		<xsl:if test="key('ETNTKOM',key('KOMBEZETNT',@hash)/child::field[@name='end'])/child::field[@name='end'] = $KOM
		 and key('KOMBSS',child::field[@name='start'])/child::field[@name='end'] = $KOM and key('KOMBSS',child::field[@name='end'])/child::field[@name='end'] = $KOM">
		  <!--and key('KOMBEZETNT',@hash)/child::field[@name='end'] = key('BSSETNT',child::field[@name='start'])/child::field[@name='end'] and key('KOMBEZETNT',@hash)/child::field[@name='start'] = key('BSSETNT',child::field[@name='start'])/child::field[@name='end'] ">-->
			<xsl:value-of select="key('hash',child::field[@name='start'])/child::field[@name='name']"/>
			<xsl:text> (</xsl:text><xsl:value-of select="key('hash',key('AWBKOM',child::field[@name='start'])/child::field[@name='start'])/child::field[@name='name']"/><xsl:text>) </xsl:text>
			<xsl:if test="child::field[@name='state']='FORWARD'">
				<xsl:text> --&#62; </xsl:text>
			</xsl:if>
			<xsl:if test="child::field[@name='state']='BACKWARD'">
				<xsl:text> &#60;-- </xsl:text>
			</xsl:if>
			<xsl:if test="child::field[@name='state']='DOUBLE'">
				<xsl:text> --- </xsl:text>
			</xsl:if>			
			<xsl:value-of select="key('hash',child::field[@name='end'])/child::field[@name='name']"/>
			<xsl:text> (</xsl:text><xsl:value-of select="key('hash',key('AWBKOM',child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name']"/><xsl:text>) </xsl:text><br/>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="KommBeziehung2">
	<xsl:param name="KOM" />
	<xsl:variable name="KOMBEZ" select="/modell_3lgm_2/objects/element[@class='KommBeziehung']" />
	<xsl:for-each select="$KOMBEZ">
		<xsl:if test="key('KOMBSS',child::field[@name='start'])/child::field[@name='end'] = $KOM and key('KOMBSS',child::field[@name='end'])/child::field[@name='end'] = $KOM">
			<xsl:value-of select="key('hash',child::field[@name='start'])/child::field[@name='name']"/>
			<xsl:text> (</xsl:text><xsl:value-of select="key('hash',key('AWBKOM',child::field[@name='start'])/child::field[@name='start'])/child::field[@name='name']"/><xsl:text>) </xsl:text>
			<xsl:if test="child::field[@name='state']='FORWARD'">
				<xsl:text> --&#62; </xsl:text>
			</xsl:if>
			<xsl:if test="child::field[@name='state']='BACKWARD'">
				<xsl:text> &#60;-- </xsl:text>
			</xsl:if>
			<xsl:if test="child::field[@name='state']='DOUBLE'">
				<xsl:text> --- </xsl:text>
			</xsl:if>
			<xsl:value-of select="key('hash',child::field[@name='end'])/child::field[@name='name']"/>
			<xsl:text> (</xsl:text><xsl:value-of select="key('hash',key('AWBKOM',child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name']"/><xsl:text>) </xsl:text><br/>
		</xsl:if>
	</xsl:for-each>
</xsl:template>
</xsl:stylesheet>
