<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Übersicht über Daten der Masterdatenbank -->
<!--type: html -->
<!--description: Welche Daten werden von der Masterdatenbank an Subsysteme geschickt? -->
<!--author: Oliver Heller 02.02.2006 -->

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

<xsl:function name="str:removeLineBreak" as="xs:string">
	<xsl:param name="string" as="xs:string" />
	<xsl:sequence select="str:replaceSubstring($string, '\-', '')" />
</xsl:function>

<!-- Schluessel zur Auswahl von Elementen -->

<!-- Kanten von Objekttyp zu Repräsentationsform (Nachrichtentyp/Dokumententyp) -->
<xsl:key name="kanteObjRep" match="/modell_3lgm_2/objects/element[@class='ObjReprVerbindung']" use="child::field[@name='start']" />

<!-- Kanten von Repräsentationsform (Nachrichtentyp/Dokumententyp) zu ETNT (Kanten beginnen bei ETNT)-->
<xsl:key name="kanteRepEtnt" match="/modell_3lgm_2/objects/element[@class='EtntDotVerbindung' or @class='EtntNatVerbindung']" use="child::field[@name='end']" /> 

<!-- Kante von ETNT zu Bausteintyp mit Bausteintyp kann senden (Kanten beginnen bei BAU) -->
<xsl:key name="kanteEtntBauSenden" match="/modell_3lgm_2/objects/element[@class='BssEtntVerbindung'][child::field[@name='state']='DOUBLE' or child::field[@name='state']='BACKWARD']" use="child::field[@name='end']" />

<!-- Kante von ETNT zu Bausteintyp mit Bausteintyp kann empfangen (Kanten beginnen bei BAU) -->
<xsl:key name="kanteEtntBauEmpfangen" match="/modell_3lgm_2/objects/element[@class='BssEtntVerbindung'][child::field[@name='state']='DOUBLE' or child::field[@name='state']='FORWARD']" use="child::field[@name='start']" />

<!-- Kante von Bausteinschnittstelle zu Basuteinschnittstelle (Kommunikationsbeziehung) -->
<xsl:key name="kanteSenderEmpfaenger" match="/modell_3lgm_2/objects/element[@class='KommBeziehung'][child::field[@name='state']='FORWARD' or child::field[@name='state']='DOUBLE']" use="child::field[@name='start']" />
<xsl:key name="kanteEmpfaengerSender" match="/modell_3lgm_2/objects/element[@class='KommBeziehung'][child::field[@name='state']='BACKWARD' or child::field[@name='state']='DOUBLE']" use="child::field[@name='end']" />

<!-- Kante von Kommunikationsbeziehung zu ETNT -->
<xsl:key name="kanteKommEtnt" match="/modell_3lgm_2/objects/element[@class='KommbezEtntVerbindung']" use="child::field[@name='start']" />

<!-- Kante von ETNT nach Ereignistyp -->
<xsl:key name="kanteEtntEt" match="/modell_3lgm_2/objects/element[@class='EtntEtVerbindung']" use="child::field[@name='start']" />

<!-- Kante von Anwendungsbaustein (rechnerunterstütz/konventionell/gemischt) zu Bausteinschnittstelle -->
<xsl:key name="kanteAnwBau" match="/modell_3lgm_2/objects/element[@class='AwbKommssVerbindung']" use="child::field[@name='end']" />

<!-- Element mit dem Hashcode auswaehler -->
<xsl:key name="hash" match="/modell_3lgm_2/objects/element" use="@hash" />

<!-- Kante von DBS zu AWB -->
<xsl:key name="AWB" match="/modell_3lgm_2/objects/element[@class='RawbDbsVerbindung']" use="child::field[@name='end']" />

<!-- Wurzelknoten -->
<xsl:template match="/">
<html>
	<head>
		<title>Welche Daten werden von der Masterdatenbank an Subsysteme geschickt?</title>
		<meta name="author" content="Tool3lgm" />
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1>Welche Daten werden von der Masterdatenbank an Subsysteme geschickt?</h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<h3>Hinweis:</h3>
		<p>
			Der Anwendungsbaustein, welcher das Master-DBS besitzt, ist als Sender gesondert hervor gehoben (Fett gedruckt). Dies dient dem einfachen Überblick,
			welche Daten an direkt verbundene Anwendungsbausteine gesendet werden.
		</p>
		<table with="100%" cellspacing="0" cellpadding="3" border="1">
			<thead><tr><td width="50"></td><th>Anwendungsbaustein (kann senden)</th><th>Anwendungsbaustein (kann empfangen)</th><th>ausl&#246;sender Ereignistyp</th></tr>
			</thead>
			<tbody align="left" valign="top">
			
				<xsl:apply-templates select="modell_3lgm_2/objects/element[@class='ObjLogspVerbindung']">
					<xsl:sort select="str:removeLineBreak(child::field[@name='name'])" order="ascending" data-type="text" />
				</xsl:apply-templates>
			</tbody>
		</table>
	</body>
</html>
</xsl:template>



<xsl:template match="modell_3lgm_2/objects/element[@class='ObjLogspVerbindung']">
	<tr><td><xsl:attribute name="colspan">4</xsl:attribute><h4><xsl:attribute name="style">margin-top:10pt; margin-bottom:0pt;</xsl:attribute>
	<xsl:text>Master-DBS: </xsl:text><xsl:value-of select="str:removeLineBreak(key('hash',child::field[@name='end'])/child::field[@name='name'])" /><br/>
	<xsl:text>Objekttyp: </xsl:text><xsl:value-of select="str:removeLineBreak(key('hash',child::field[@name='start'])/child::field[@name='name'])" /><br/>
	<xsl:text>Anwendungsbaustein: </xsl:text><xsl:value-of select="str:removeLineBreak(key('hash',key('AWB',child::field[@name='end'])/child::field[@name='start'])/child::field[@name='name'])" />	
	</h4></td></tr>
	
	<xsl:call-template name="repraesentationsform">
		<xsl:with-param name="objekttyp" select="child::field[@name='start']"/>
		<xsl:with-param name="awb" select="key('hash',key('AWB',child::field[@name='end'])/child::field[@name='start'])"/>
	</xsl:call-template>
</xsl:template>

<xsl:template name="repraesentationsform">
	<xsl:param name="objekttyp" />
	<xsl:param name="awb" />
	<xsl:for-each select="key('kanteObjRep' ,$objekttyp)">
		<xsl:variable name="temp" select="key('hash', child::field[@name='end'])" />
		<xsl:if test="$temp/@class='Nachrichtentyp' or $temp/@class='Dokumententyp'">
			<xsl:call-template name="etnt">
				<xsl:with-param name="repForm" select="child::field[@name='end']" />
				<xsl:with-param name="awb" select="$awb"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="etnt">
	<xsl:param name="repForm" />
	<xsl:param name="awb" />
	<xsl:for-each select="key('kanteRepEtnt', $repForm)">
		<xsl:call-template name="bausteinschnittstelle_sender">
			<xsl:with-param name="etnt" select="child::field[@name='start']" />
			<xsl:with-param name="awb" select="$awb"/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<xsl:template name="bausteinschnittstelle_sender">
	<xsl:param name="etnt" />
	<xsl:param name="awb" />
	<xsl:for-each select="key('kanteEtntBauSenden' , $etnt)">
		<xsl:call-template name="kommunikationsbeziehung">
			<xsl:with-param name="sender" select="child::field[@name='start']" />
			<xsl:with-param name="etnt" select="$etnt" />
			<xsl:with-param name="awb" select="$awb"/>
		</xsl:call-template>		
	</xsl:for-each>
</xsl:template>

<xsl:template name="kommunikationsbeziehung">
	<xsl:param name="etnt" />
	<xsl:param name="sender" />
	<xsl:param name="awb" />
	<xsl:for-each select="key('kanteSenderEmpfaenger', $sender)">
		<xsl:variable name="empfaenger" select="child::field[@name='end']" />
		<xsl:for-each select="key('kanteKommEtnt', @hash)">
			<xsl:if test="child::field[@name='end'] = $etnt">
				<xsl:call-template name="bausteinschnittstelle_empfaenger">
					<xsl:with-param name="sender" select="$sender" />
					<xsl:with-param name="etnt" select="$etnt" />
					<xsl:with-param name="empfaenger" select="$empfaenger" />
					<xsl:with-param name="awb" select="$awb"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
			
	<xsl:for-each select="kanteEmpfaengerSender">
		<xsl:variable name="empfaenger" select="child::field[@name='start']" />
		<xsl:for-each select="key('kanteKommEtnt', @hash)">
			<xsl:if test="child::field[@name='end'] = $etnt">
				<xsl:call-template name="bausteinschnittstelle_empfaenger">
					<xsl:with-param name="sender" select="$sender" />
					<xsl:with-param name="etnt" select="$etnt" />
					<xsl:with-param name="empfaenger" select="$empfaenger" />
					<xsl:with-param name="awb" select="$awb"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
</xsl:template>

<xsl:template name="bausteinschnittstelle_empfaenger">
	<xsl:param name="sender" />
	<xsl:param name="etnt" />
	<xsl:param name="empfaenger" />
	<xsl:param name="awb" />
	<xsl:for-each select="key('kanteEtntBauEmpfangen', $empfaenger)">
	<xsl:sort select="$sender" order="ascending" data-type="text" />
		<xsl:if test="child::field[@name='end']=$etnt">
			<xsl:call-template name="ausgabe">
				<xsl:with-param name="sender" select="$sender" />
				<xsl:with-param name="empfaenger" select="$empfaenger" />
				<xsl:with-param name="etnt" select="$etnt" />
				<xsl:with-param name="awb" select="$awb"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="ausgabe">
	<xsl:param name="sender" />
	<xsl:param name="empfaenger" />
	<xsl:param name="etnt" />
	<xsl:param name="awb" /> 
	<tr><td></td>
		<td>
		<xsl:if test="key('hash', key('kanteAnwBau' ,$sender)/child::field[@name='start'])=$awb">
		<h4><xsl:value-of select="str:removeLineBreak(key('hash', key('kanteAnwBau' ,$sender)/child::field[@name='start'])/child::field[@name='name'])" /></h4>
		</xsl:if>
		<xsl:if test="key('hash', key('kanteAnwBau' ,$sender)/child::field[@name='start'])!=$awb">
		<xsl:value-of select="str:removeLineBreak(key('hash', key('kanteAnwBau' ,$sender)/child::field[@name='start'])/child::field[@name='name'])" />
		</xsl:if>
		</td>
		<td><xsl:value-of select="str:removeLineBreak(key('hash', key('kanteAnwBau' ,$empfaenger)/child::field[@name='start'])/child::field[@name='name'])" /></td>
		<td><xsl:value-of select="str:removeLineBreak(key('hash', key('kanteEtntEt', $etnt)/child::field[@name='end'])/child::field[@name='name'])" /></td>
	</tr>
</xsl:template>

</xsl:stylesheet>
