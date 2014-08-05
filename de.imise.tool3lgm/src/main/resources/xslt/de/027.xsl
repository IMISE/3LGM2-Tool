<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Objekttypen: Kommunikation -->
<!--type: html -->
<!--description: Welche Objekttypen können zwischen welchen Anwendungsbausteinen 
	kommuniziert werden? -->
<!--author: Thomas Rudert -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:str="http://whatever"
	version="2.0" exclude-result-prefixes="str">

	<xsl:variable name="header_text"
		select="'Welche Objekttypen k&#246;nnen zwischen welchen Anwendungsbausteinen kommuniziert werden?'" />
	<xsl:variable name="note_text" select="'Hinweis:'" />
	<xsl:variable name="notes1_text"
		select="'Mit diesem Dokument kann analysiert werden, welche Anwendungsbausteine welche Objekttypen miteinander kommunizieren k&#246;nnen. Ein Objekttyp kann durch Dokumententypen und Nachrichtentypen repr&#228;sentiert werden. Diese Repr&#228;sentationsform kann zusammen mit einem Ereignistyp in einem Ereignis-Nachrichtentyp/Ereignis-Dokumententyp (ETNT) enthalten sein. Anwendungsbausteine k&#246;nnen eine oder mehrere Bausteinschnittstellen besitzen, welche wiederum mit anderen Bausteinschnittstellen eine Kommunikationsbeziehung besitzen k&#246;nnen. F&#252;r Bausteinschnittstellen ist im Modell festgelegt, welche ETNT sie empfangen, senden oder empfangen und senden k&#246;nnen.'" />
	<xsl:variable name="notes2_text"
		select="'Das Dokument wurde folgenderma&#223;en aus dem Modell erstellt und dementsprechend aufgebaut:'" />
	<xsl:variable name="notes3_text"
		select="'F&#252;r jeden Objekttyp wird zuerst der Name ausgegeben. Danach werden die Repr&#228;sentationsformen zu dem Objekttyp gesucht und f&#252;r die gefundenen Repr&#228;sentationsformen werden die ETNT ausgew&#228;hlt. Bei den ETNT wird &#252;berpr&#252;ft, welche Bausteinschnittstellen die ETNT senden oder senden und empfangen k&#246;nnen. Bei diesen (sendenden) Bausteinschnittstellen wird geschaut, ob diese Kommunikationsbeziehungen zu anderen (empfangenden) Bausteinschnittstellen besitzen. Wenn diese (empfangende) Bausteinschnittstelle auch die ETNT empfangen kann werden die, zu den beiden Bausteinschnittstellen geh&#246;renden Anwendungsbausteine und der, zu der ETNT geh&#246;rige Ereignistyp ausgegeben.'" />
	<xsl:variable name="notes4_text"
		select="'Da die Kommunikationsbeziehung zwischen zwei Bausteinschnittstellen zwar im Meta-Modell, aber nicht im 3LGM&#178;-Tool an eine bestimmt ETNT gekoppelt ist, kann es vorkommen, dass kommunizierende Anwendungsbausteinpaare zu einen Objekttyp ausgegeben werden, wo eine Kommunikation gar nicht stattfindet bzw. sinnvoll ist.'" />
	<xsl:variable name="send_text" select="'Anwendungsbaustein (kann senden)'" />
	<xsl:variable name="rece_text" select="'Anwendungsbaustein (kann empfangen)'" />
	<xsl:variable name="event_text" select="'ausl&#246;sender Ereignistyp'" />

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

	<!-- Kanten von Objekttyp zu Repräsentationsform (Nachrichtentyp/Dokumententyp) -->
	<xsl:key name="kanteObjRep"
		match="/modell_3lgm_2/objects/element[@class='ObjReprVerbindung']"
		use="child::field[@name='start']" />

	<!-- Kanten von Repräsentationsform (Nachrichtentyp/Dokumententyp) zu ETNT 
		(Kanten beginnen bei ETNT) -->
	<xsl:key name="kanteRepEtnt"
		match="/modell_3lgm_2/objects/element[@class='EtntDotVerbindung' or @class='EtntNatVerbindung']"
		use="child::field[@name='end']" />

	<!-- Kante von ETNT zu Bausteintyp mit Bausteintyp kann senden (Kanten beginnen 
		bei BAU) -->
	<xsl:key name="kanteEtntBauSenden"
		match="/modell_3lgm_2/objects/element[@class='BssEtntVerbindung'][child::field[@name='state']='DOUBLE' or child::field[@name='state']='BACKWARD']"
		use="child::field[@name='end']" />

	<!-- Kante von ETNT zu Bausteintyp mit Bausteintyp kann empfangen (Kanten 
		beginnen bei BAU) -->
	<xsl:key name="kanteEtntBauEmpfangen"
		match="/modell_3lgm_2/objects/element[@class='BssEtntVerbindung'][child::field[@name='state']='DOUBLE' or child::field[@name='state']='FORWARD']"
		use="child::field[@name='start']" />

	<!-- Kante von Bausteinschnittstelle zu Basuteinschnittstelle (Kommunikationsbeziehung) -->
	<xsl:key name="kanteSenderEmpfaenger"
		match="/modell_3lgm_2/objects/element[@class='KommBeziehung'][child::field[@name='state']='FORWARD' or child::field[@name='state']='DOUBLE']"
		use="child::field[@name='start']" />
	<xsl:key name="kanteEmpfaengerSender"
		match="/modell_3lgm_2/objects/element[@class='KommBeziehung'][child::field[@name='state']='BACKWARD' or child::field[@name='state']='DOUBLE']"
		use="child::field[@name='end']" />

	<!-- Kante von Kommunikationsbeziehung zu ETNT -->
	<xsl:key name="kanteKommEtnt"
		match="/modell_3lgm_2/objects/element[@class='KommbezEtntVerbindung']"
		use="child::field[@name='start']" />

	<!-- Kante von ETNT nach Ereignistyp -->
	<xsl:key name="kanteEtntEt"
		match="/modell_3lgm_2/objects/element[@class='EtntEtVerbindung']" use="child::field[@name='start']" />

	<!-- Kante von Anwendungsbaustein (rechnerunterstütz/konventionell/gemischt) 
		zu Bausteinschnittstelle -->
	<xsl:key name="kanteAnwBau"
		match="/modell_3lgm_2/objects/element[@class='AwbKommssVerbindung']"
		use="child::field[@name='end']" />


	<!-- alle Kanten von Anwendungsbaustein zu Anwendungsprogramm finden -->
	<xsl:key name="AWP"
		match="/modell_3lgm_2/objects/element[@class='RawbAwpVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von Anwendungsprogramm zu Software finden -->
	<xsl:key name="SW"
		match="/modell_3lgm_2/objects/element[@class='AwpSwpVerbindung']" use="child::field[@name='start']" />



	<!-- Element mit dem Hashcode auswaehler -->
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
					Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell
					erzeugt.
				</xsl:comment>
				<h1>
					<xsl:value-of select="$header_text" />
				</h1>
				<h2>
					<xsl:value-of select="modell_3lgm_2/header/title" />
				</h2>
				<h3>
					<xsl:value-of select="$note_text" />
				</h3>
				<p>
					<xsl:value-of select="$notes1_text" />
					<br />
					<xsl:value-of select="$notes2_text" />
					<br />
					<xsl:value-of select="$notes3_text" />
					<br />
					<xsl:value-of select="$notes4_text" />
				</p>
				<table with="100%" cellspacing="0" cellpadding="3" border="1">
					<thead>
						<tr>
							<td width="50"></td>
							<th>
								<xsl:value-of select="$send_text" />
							</th>
							<th>
								<xsl:value-of select="$rece_text" />
							</th>
							<th>
								<xsl:value-of select="$event_text" />
							</th>
						</tr>
					</thead>
					<tbody align="left" valign="top">

						<xsl:apply-templates
							select="modell_3lgm_2/objects/element[@class='Objekttyp']">
							<xsl:sort select="str:removeLineBreak(child::field[@name='name'])"
								order="ascending" data-type="text" />
						</xsl:apply-templates>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="modell_3lgm_2/objects/element[@class='Objekttyp']">
		<tr>
			<td>
				<xsl:attribute name="colspan">4</xsl:attribute>
				<h3>
					<xsl:attribute name="style">margin-top:12pt; margin-bottom:0pt;</xsl:attribute>
					<xsl:value-of select="str:removeLineBreak(child::field[@name='name'])" />
				</h3>
			</td>
		</tr>

		<xsl:call-template name="repraesentationsform">
			<xsl:with-param name="objekttyp" select="@hash" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="repraesentationsform">
		<xsl:param name="objekttyp" />
		<xsl:for-each select="key('kanteObjRep' ,$objekttyp)">
			<xsl:variable name="temp"
				select="key('hash', child::field[@name='end'])" />
			<xsl:if
				test="$temp/@class='Nachrichtentyp' or $temp/@class='Dokumententyp'">
				<xsl:call-template name="etnt">
					<xsl:with-param name="repForm" select="child::field[@name='end']" />
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="etnt">
		<xsl:param name="repForm" />
		<xsl:for-each select="key('kanteRepEtnt', $repForm)">
			<xsl:call-template name="bausteinschnittstelle_sender">
				<xsl:with-param name="etnt" select="child::field[@name='start']" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="bausteinschnittstelle_sender">
		<xsl:param name="etnt" />
		<xsl:for-each select="key('kanteEtntBauSenden' , $etnt)">
			<xsl:call-template name="kommunikationsbeziehung">
				<xsl:with-param name="sender" select="child::field[@name='start']" />
				<xsl:with-param name="etnt" select="$etnt" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="kommunikationsbeziehung">
		<xsl:param name="etnt" />
		<xsl:param name="sender" />
		<xsl:for-each select="key('kanteSenderEmpfaenger', $sender)">
			<xsl:variable name="empfaenger" select="child::field[@name='end']" />
			<xsl:for-each select="key('kanteKommEtnt', @hash)">
				<xsl:if test="child::field[@name='end'] = $etnt">
					<xsl:call-template name="bausteinschnittstelle_empfaenger">
						<xsl:with-param name="sender" select="$sender" />
						<xsl:with-param name="etnt" select="$etnt" />
						<xsl:with-param name="empfaenger" select="$empfaenger" />
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
					</xsl:call-template>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="bausteinschnittstelle_empfaenger">
		<xsl:param name="sender" />
		<xsl:param name="etnt" />
		<xsl:param name="empfaenger" />

		<xsl:for-each select="key('kanteEtntBauEmpfangen', $empfaenger)">
			<xsl:if test="child::field[@name='end']=$etnt">
				<xsl:call-template name="ausgabe">
					<xsl:with-param name="sender" select="$sender" />
					<xsl:with-param name="empfaenger" select="$empfaenger" />
					<xsl:with-param name="etnt" select="$etnt" />
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="ausgabe">
		<xsl:param name="sender" />
		<xsl:param name="empfaenger" />
		<xsl:param name="etnt" />

		<tr>
			<td></td>
			<td>
				<xsl:value-of
					select="str:removeLineBreak(key('hash', key('kanteAnwBau' ,$sender)/child::field[@name='start'])/child::field[@name='name'])" />
				<xsl:text> (</xsl:text>
				<xsl:value-of
					select="key('hash',key('SW',key('AWP',key('kanteAnwBau' ,$sender)/child::field[@name='start'])/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
				<xsl:text>) </xsl:text>
				<br />

			</td>
			<td>
				<xsl:variable name="empfaengerStart"
					select="key('hash', key('kanteAnwBau' ,$empfaenger)/child::field[@name='start'])/child::field[@name='name']" />
				<xsl:variable name="anzahl" select="count($empfaengerStart)" />
				<xsl:choose>
					<xsl:when test="$anzahl=0">
						<xsl:text>&#160;</xsl:text>
					</xsl:when>
					<xsl:otherwise>

						<xsl:value-of select="str:removeLineBreak($empfaengerStart)" />
						<xsl:text> (</xsl:text>
						<xsl:value-of
							select="key('hash',key('SW',key('AWP',key('kanteAnwBau' ,$empfaenger)/child::field[@name='start'])/child::field[@name='end'])/child::field[@name='end'])/child::field[@name='name']" />
						<xsl:text>) </xsl:text>
						<br />

					</xsl:otherwise>
				</xsl:choose>

			</td>
			<td>
				<xsl:value-of
					select="str:removeLineBreak(key('hash', key('kanteEtntEt', $etnt)/child::field[@name='end'])/child::field[@name='name'])" />
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
