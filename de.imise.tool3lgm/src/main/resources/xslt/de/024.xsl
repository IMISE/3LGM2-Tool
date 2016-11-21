<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Aufgaben: unterstützende Anwendungsbausteine gruppiert nach Organisationseinheiten -->
<!--type: html -->
<!--description: Welche Aufgaben werden in welchen Organisationseinheiten 
	durch welche Anwendungsbausteine erledigt (Variante 1)? (Teilaufgaben mit 
	in uebergeordnete Aufgaben einbeziehen) -->
<!--author: Thomas Rudert -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:str="http://whatever"
	xmlns:lgm="http://whatever" xmlns:int="http://whatever" version="2.0"
	exclude-result-prefixes="str lgm int">

	<xsl:variable name="header_text"
		select="'Welche Aufgaben werden in welchen Organisationseinheiten durch welche Anwendungsbausteine erledigt?'" />
	<xsl:variable name="note_text" select="'Bemerkung:'" />
	<xsl:variable name="note1_text"
		select="'das Dokument ist nach der hierarchischen Struktur der Aufgaben aufgebaut, Teilaufgaben k&#246;nnen daher mehrfach auftreten'" />
	<xsl:variable name="note2_text"
		select="'die Organisationseinheiten und Anwendungsbausteine der Teilaufgaben werden mit in die &#252;bergeordnete Aufgaben einbezogen'" />
	<xsl:variable name="note3_text"
		select="'die Organisationseinheiten zu einer Aufgabe sind nach ihrer Aufgaben-Organisationseinheiten-Konfiguration gruppiert'" />
	<xsl:variable name="note4_text"
		select="'die Anwendungsbausteine zu einer Organisationseinheit sind nach ihrer Anwendungsbaustein-Konfiguration gruppiert'" />
	<xsl:variable name="note5_text"
		select="'wenn ein Anwendungsbaustein rechnerbasiert oder gemischt ist, dann steht hinter diesem Anwendungsbaustein in Klammern das dazugeh&#246;rige Softwareprodukt'" />
	<xsl:variable name="task_text" select="'Aufgabe'" />
	<xsl:variable name="org_text" select="'Organisationseinheit'" />
	<xsl:variable name="appl_text" select="'Anwendungsbaustein'" />
	<xsl:variable name="error_text"
		select="'In diesem Modell sind keine Aufgaben enthalten!'" />

	<xsl:function name="int:maximum" as="xs:integer">
		<xsl:param name="x" as="xs:integer" />
		<xsl:param name="y" as="xs:integer" />
		<xsl:choose>
			<xsl:when test="$x &gt; $y">
				<xsl:sequence select="$x" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$y" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

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

	<!-- Funktion zaehlt die Vorkommen eines Teilstrings in einem String -->
	<xsl:function name="str:countOccurrences" as="xs:integer">
		<xsl:param name="inputString" as="xs:string" />
		<xsl:param name="subString" as="xs:string" />
		<xsl:choose>
			<xsl:when test="contains($inputString, $subString)">
				<xsl:sequence
					select="1 + str:countOccurrences(substring-after($inputString, $subString), $subString)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="0" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- Funktion aktualisiert den Ergebnisstring -->
	<!-- $($AufgabeHash$+$AufOrgKombHash$+$AufOrgKombHash$+$...$+$AufOrgKombHash$)$$($AufgabeHash$+$AufOrgKombHash$+$AufOrgKombHash... 
		$)$ -->
	<!-- jeder AufgabeHash kommt maximal einmal vor und jeder AufOrgKombHash 
		kommt bei jeder AufgabeHash maximal einmal vor -->
	<xsl:function name="lgm:updateResult" as="xs:string">
		<xsl:param name="result" as="xs:string" />
		<xsl:param name="position" as="xs:string" />
		<xsl:param name="aufOrgKomb" as="xs:string" />
		<xsl:choose>
			<xsl:when test="string-length($position) = 0">
				<xsl:sequence select="$result" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="aufgabe"
					select="substring-before(substring-after($position, '$]$'), '$)$')" />
				<xsl:sequence
					select="lgm:updateResult(lgm:addAufOrgKomb($result, concat('$($', $aufgabe), concat('$+$', $aufOrgKomb)), substring-after($position, '$)$'), $aufOrgKomb)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- geht in Hierarchie ein Level runter (2.3 zu 1.2.3 / '' zu 1) -->
	<!-- Leesweise von rechts nach links -->
	<xsl:function name="lgm:hierarchyDown" as="xs:string">
		<xsl:param name="current" as="xs:string" />
		<xsl:choose>
			<xsl:when test="$current=''">
				<xsl:sequence select="1" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="concat('1.', $current)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- zählt in Hierarchie eins weiter (1.3.4 zu 2.3.4 / 2 zu 3) -->
	<!-- Leesweise von rechts nach links -->
	<xsl:function name="lgm:hierarchyNext" as="xs:string">
		<xsl:param name="current" as="xs:string" />
		<xsl:variable name="temp" select="substring-before($current, '.')" />
		<xsl:choose>
			<xsl:when test="$temp=''">
				<xsl:sequence select="string(number($current)+1)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence
					select="concat(concat(string(number($temp)+1), '.'), substring-after($current, '.'))" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- setzt den letzten Zähler der Hierarchie (1.3.4 mit 5 zu 5.3.4) -->
	<!-- Leesweise von rechts nach links -->
	<xsl:function name="lgm:hierarchySet" as="xs:string">
		<xsl:param name="current" as="xs:string" />
		<xsl:param name="new" as="xs:integer" />
		<xsl:variable name="temp" select="substring-before($current, '.')" />
		<xsl:choose>
			<xsl:when test="string-length($temp)=0">
				<xsl:sequence select="string($new)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence
					select="concat(concat(string($new), '.'), substring-after($current, '.'))" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- formt Hierarchie in lesbaren String um (1.3.4 zu 4.3.1) -->
	<!-- Leesweise von rechts nach links wird umgewandelt in links nach rechts -->
	<xsl:function name="lgm:hierarchyOutput" as="xs:string">
		<xsl:param name="current" as="xs:string" />
		<xsl:variable name="temp" select="substring-before($current, '.')" />
		<xsl:choose>
			<xsl:when test="string-length($temp) = 0">
				<xsl:sequence select="$current" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence
					select="concat(concat(lgm:hierarchyOutput(substring-after($current, '.')), '.'), $temp)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- Funktion fuegt AufOrgKomb zur Aufgabe hinzu, falls sie da noch nicht 
		aufgefuehrt wird -->
	<xsl:function name="lgm:addAufOrgKomb" as="xs:string">
		<xsl:param name="result" as="xs:string" />
		<!-- AufgabeHash mit vorangestellten Trennzeichen $($ -->
		<xsl:param name="aufgabe" as="xs:string" />
		<!-- AufOrgHash mit vorangestellten Trennzeichen $+$ -->
		<xsl:param name="aufOrgKomb" as="xs:string" />
		<xsl:choose>
			<xsl:when test="contains($result, $aufgabe)">
				<xsl:variable name="subString"
					select="concat($aufgabe, substring-before(substring-after($result, $aufgabe), '$)$'))" />
				<xsl:choose>
					<xsl:when test="contains($subString, $aufOrgKomb)">
						<xsl:sequence select="$result" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence
							select="str:replaceSubstring($result, $subString, concat($subString, $aufOrgKomb))" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence
					select="concat(concat(concat($aufgabe, $aufOrgKomb), '$)$'), $result)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- Funktion entfernt das Zeilenumbruch Zeichen '\-' aus dem String -->
	<xsl:function name="str:removeLineBreak" as="xs:string">
		<xsl:param name="string" as="xs:string" />
		<xsl:sequence select="str:replaceSubstring($string, '\-', '')" />
	</xsl:function>

	<!-- Schluessel zur Auswahl von Elementen -->
	<xsl:key name="objektKlasse" match="/modell_3lgm_2/objects/element"
		use="@class" />

	<xsl:key name="OberaufgabenVon"
		match="/modell_3lgm_2/objects/element[@class='AufAufVerbindung']" use="child::field[@name='start']" />
	<xsl:key name="TeilaufgabenVon"
		match="/modell_3lgm_2/objects/element[@class='AufAufVerbindung']" use="child::field[@name='end']" />

	<!-- alle Kanten von Aufgabe zu einer AufgabeOrgansisationseinheitKombination 
		(AufOrgKomb - KOH) -->
	<xsl:key name="AufOrgKomb"
		match="/modell_3lgm_2/objects/element[@class='AufAufOrgVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von AufOrgKomb zu einer Anwendungsbausteinkonfiguration 
		(AufOrgKomb - ABK) -->
	<xsl:key name="ABK"
		match="/modell_3lgm_2/objects/element[@class='AwbkAufOrgVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von AufOrgKomb zu einer Organisationseinheit (AufOrgKomb 
		- OEH) -->
	<xsl:key name="OEH"
		match="/modell_3lgm_2/objects/element[@class='OrgAufOrgVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von AnwBauKonf zu Anwendungsbaustein(rechnerunt., konvent., 
		gemischt) (ABK - AWB) finden -->
	<xsl:key name="AWB"
		match="/modell_3lgm_2/objects/element[@class='AwbAwbkVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von RechAwb bzw. Awb zu einem Anwendungsprogramm (AWB - 
		AWP) -->
	<xsl:key name="AWP"
		match="/modell_3lgm_2/objects/element[@class='AwbAwpVerbindung' or @class='RawbAwpVerbindung']"
		use="child::field[@name='start']" />

	<!-- alle Kanten von Anwendungsprogramm zu einem Softwareprodukt (AWP - 
		SWP) -->
	<xsl:key name="SWP"
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
					Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell
					erzeugt.
				</xsl:comment>
				<h1>
					<xsl:value-of select="$header_text" />
				</h1>

				<h2>
					<xsl:value-of select="modell_3lgm_2/header/title" />
				</h2>
				<h4>
					<xsl:value-of select="$note_text" />
				</h4>
				<ul>
					<li>
						<xsl:value-of select="$note1_text" />
					</li>
					<li>
						<xsl:value-of select="$note2_text" />
					</li>
					<li>
						<xsl:value-of select="$note3_text" />
					</li>
					<li>
						<xsl:value-of select="$note4_text" />
					</li>
					<li>
						<xsl:value-of select="$note5_text" />
					</li>
				</ul>
				<xsl:choose>
					<xsl:when test="count(key('objektKlasse', 'Aufgabe')) &lt; 1">
						<p>
							<xsl:value-of select="$error_text" />
						</p>
					</xsl:when>
					<xsl:otherwise>
						<table border="1">
							<tr>
								<th></th>
								<th>
									<xsl:value-of select="$task_text" />
								</th>
								<th>
									<xsl:value-of select="$org_text" />
								</th>
								<th>
									<xsl:value-of select="$appl_text" />
								</th>
							</tr>
							<tbody valign="top" align="left">
								<xsl:call-template name="rekursiveStart" />
							</tbody>
						</table>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="aufgaben">
		<xsl:param name="result" />
		<xsl:param name="hierarchy" select="1" />
		<xsl:param name="index" select="1" />
		<xsl:param name="maxIndex"
			select="count(/modell_3lgm_2/objects/element[@class='Aufgabe'])" />
		<xsl:variable name="aufgabe"
			select="/modell_3lgm_2/objects/element[@class='Aufgabe'][$index]/@hash" />
		<xsl:choose>
			<xsl:when test="count(key('OberaufgabenVon', $aufgabe)) = 0">
				<xsl:call-template name="ausgabe">
					<xsl:with-param name="aufgabe" select="$aufgabe" />
					<xsl:with-param name="result" select="$result" />
					<xsl:with-param name="hierarchy" select="$hierarchy" />
				</xsl:call-template>
				<xsl:call-template name="resolveHierarchy">
					<xsl:with-param name="masterTask" select="$aufgabe" />
					<xsl:with-param name="result" select="$result" />
					<xsl:with-param name="hierarchy"
						select="lgm:hierarchyDown(string($hierarchy))" />
				</xsl:call-template>
				<xsl:if test="$index &lt; $maxIndex">
					<xsl:call-template name="aufgaben">
						<xsl:with-param name="result" select="$result" />
						<xsl:with-param name="hierarchy"
							select="lgm:hierarchyNext(string($hierarchy))" />
						<xsl:with-param name="index" select="$index + 1" />
						<xsl:with-param name="maxIndex" select="$maxIndex" />
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$index &lt; $maxIndex">
					<xsl:call-template name="aufgaben">
						<xsl:with-param name="result" select="$result" />
						<xsl:with-param name="hierarchy" select="$hierarchy" />
						<xsl:with-param name="index" select="$index + 1" />
						<xsl:with-param name="maxIndex" select="$maxIndex" />
					</xsl:call-template>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="ausgabe">
		<xsl:param name="aufgabe" />
		<xsl:param name="result" />
		<xsl:param name="hierarchy" select="1" />
		<xsl:variable name="aufOrgKomb"
			select="concat(substring-before(substring-after($result, concat($aufgabe, '$+$')), '$)$'), '$+$')" />

		<xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
		<td>
			<xsl:call-template name="getRowSpan">
				<xsl:with-param name="aufgabe" select="$aufgabe" />
				<xsl:with-param name="result" select="$aufOrgKomb" />
			</xsl:call-template>
			<xsl:value-of select="lgm:hierarchyOutput(string($hierarchy))" />
		</td>
		<td>
			<xsl:call-template name="getRowSpan">
				<xsl:with-param name="aufgabe" select="$aufgabe" />
				<xsl:with-param name="result" select="$aufOrgKomb" />
			</xsl:call-template>
			<xsl:value-of
				select="str:removeLineBreak(key('hash', $aufgabe)/child::field[@name='name'])" />
		</td>
		<xsl:choose>
			<xsl:when test="string-length(substring-before($aufOrgKomb, '$+$')) = 0">
				<td>&#160;</td>
				<td>&#160;</td>
				<xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="ausgabeOrg">
					<xsl:with-param name="aufOrgKomb" select="$aufOrgKomb" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="ausgabeOrg">
		<!-- AufOrgKomb$+$AufOrgKomb$+$...$+$ -->
		<xsl:param name="aufOrgKomb" />

		<xsl:variable name="hash" select="substring-before($aufOrgKomb, '$+$')" />
		<xsl:variable name="aufOrgKombNew" select="substring-after($aufOrgKomb, '$+$')" />

		<xsl:if test="string-length($hash) &gt; 0">
			<xsl:variable name="rowspan" select="count(key('ABK', $hash))" />
			<td>
				<xsl:if test="$rowspan &gt; 1">
					<xsl:attribute name="rowspan">
					<xsl:value-of select="$rowspan" />
				</xsl:attribute>
				</xsl:if>
				<xsl:for-each select="key('OEH', $hash)">
					<xsl:sort
						select="str:removeLineBreak(key('hash', child::field[@name='end'])/child::field[@name='name'])"
						order="ascending" data-type="text" />
					<xsl:value-of
						select="str:removeLineBreak(key('hash', child::field[@name='end'])/child::field[@name='name'])" />
					<xsl:if test="position() != last()">
						<xsl:text>, </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</td>
			<xsl:choose>
				<xsl:when test="$rowspan = 0">
					<td>&#160;</td>
					<xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
					<xsl:if test="string-length($aufOrgKombNew) != 0">
						<xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="ausgabeABK">
						<xsl:with-param name="aufOrgKomb" select="$hash" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="ausgabeOrg">
				<xsl:with-param name="aufOrgKomb" select="$aufOrgKombNew" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ausgabeABK">
		<xsl:param name="aufOrgKomb" />
		<xsl:for-each select="key('ABK', $aufOrgKomb)">
			<xsl:if test="position() != 1">
				<xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
			</xsl:if>
			<td>
				<xsl:call-template name="ausgabeAWB">
					<xsl:with-param name="ABK" select="child::field[@name='end']" />
				</xsl:call-template>
			</td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="ausgabeAWB">
		<xsl:param name="ABK" />
		<xsl:for-each select="key('AWB', $ABK)">
			<xsl:sort
				select="str:removeLineBreak(key('hash', child::field[@name='end'])/child::field[@name='name'])"
				order="ascending" data-type="text" />
			<xsl:variable name="awb"
				select="key('hash', child::field[@name='end'])" />
			<xsl:value-of select="str:removeLineBreak($awb/child::field[@name='name'])" />
			<xsl:if test="$awb/@class != 'KonAnwendungsbaustein'">
				(
				<xsl:for-each select="key('AWP', $awb/@hash)">
					<xsl:for-each select="key('SWP', child::field[@name='end'])">
						<xsl:value-of
							select="str:removeLineBreak(key('hash', child::field[@name='end'])/child::field[@name='name'])" />
						<xsl:if test="position() != last()">
							<xsl:text>, </xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:for-each>
				)
			</xsl:if>
			<xsl:if test="position() != last()">
				<br />
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="resolveHierarchy">
		<xsl:param name="masterTask" />
		<xsl:param name="usedTasks" select="concat($masterTask, '$,$')" />
		<xsl:param name="hierarchy" />
		<xsl:param name="result" />

		<xsl:for-each select="key('TeilaufgabenVon', $masterTask)">
			<xsl:sort
				select="str:removeLineBreak(key('hash', child::field[@name='start'])/child::field[@name='name'])"
				order="ascending" data-type="text" />
			<xsl:variable name="hash" select="child::field[@name='start']" />
			<xsl:variable name="current"
				select="lgm:hierarchySet($hierarchy, position())" />
			<xsl:call-template name="ausgabe">
				<xsl:with-param name="aufgabe" select="$hash" />
				<xsl:with-param name="result" select="$result" />
				<xsl:with-param name="hierarchy" select="$current" />
			</xsl:call-template>

			<xsl:if test="not(contains($usedTasks, $hash))">
				<xsl:call-template name="resolveHierarchy">
					<xsl:with-param name="masterTask" select="$hash" />
					<xsl:with-param name="usedTasks"
						select="concat(concat($hash, '$,$'), $usedTasks)" />
					<xsl:with-param name="hierarchy" select="lgm:hierarchyDown($current)" />
					<xsl:with-param name="result" select="$result" />
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="getRowSpan">
		<xsl:param name="aufgabe" />
		<xsl:param name="result" />
		<xsl:param name="counter" select="0" />
		<xsl:choose>
			<xsl:when test="string-length(substring-before($result, '$+$')) = 0">
				<xsl:if test="$counter &gt; 1">
					<xsl:attribute name="rowspan">
					<xsl:value-of select="$counter" />
				</xsl:attribute>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="aufOrgKomb" select="substring-before($result, '$+$')" />
				<xsl:call-template name="getRowSpan">
					<xsl:with-param name="aufgabe" select="$aufgabe" />
					<xsl:with-param name="result"
						select="substring-after($result, '$+$')" />
					<xsl:with-param name="counter"
						select="$counter + int:maximum(1, count(key('ABK', $aufOrgKomb)))" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="rekursiveStart">
		<xsl:param name="result" select="''" />
		<xsl:param name="index" select="1" />
		<xsl:variable name="aufgaben"
			select="/modell_3lgm_2/objects/element[@class='Aufgabe']" />
		<xsl:choose>
			<xsl:when test="$index &gt; count($aufgaben)">
				<xsl:call-template name="aufgaben">
					<xsl:with-param name="result" select="$result" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="hash" select="$aufgaben[$index]/@hash" />
				<xsl:choose>
					<xsl:when test="count(key('OberaufgabenVon', $hash)) = 0">
						<xsl:call-template name="rekursive">
							<xsl:with-param name="result" select="$result" />
							<xsl:with-param name="topIndex" select="$index+1" />
							<xsl:with-param name="position"
								select="concat(concat('$[$1$,$1$]$', $hash), '$)$')" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="rekursiveStart">
							<xsl:with-param name="result" select="$result" />
							<xsl:with-param name="index" select="$index+1" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="rekursive">
		<xsl:param name="result" />
		<xsl:param name="position" />
		<xsl:param name="topIndex" />

		<!-- String mir der Position im Graphen von rechts nach links aufgebaut -->
		<!-- ...$)$$[$indexAufOrgKomb$,$indexTeilAufgabe$]$AufgabeHash$)$ -->
		<xsl:variable name="hash"
			select="substring-before(substring-after($position, '$]$'), '$)$')" />
		<xsl:variable name="indexTeilaufgabe"
			select="number(substring-before(substring-after($position, '$,$'), '$]$'))" />
		<xsl:variable name="indexAufOrgKomb"
			select="number(substring-before(substring-after($position, '$[$'), '$,$'))" />
		<xsl:variable name="aufOrgKomb"
			select="key('AufOrgKomb', $hash)/child::field[@name='end']" />
		<xsl:choose>
			<xsl:when test="$indexAufOrgKomb &gt; count($aufOrgKomb)">
				<xsl:variable name="partOf" select="key('TeilaufgabenVon', $hash)" />
				<xsl:choose>
					<xsl:when test="$indexTeilaufgabe &gt; count($partOf)">
						<xsl:variable name="temp"
							select="substring-after($position, '$)$')" />
						<xsl:choose>
							<xsl:when test="string-length($temp) = 0">
								<xsl:call-template name="rekursiveStart">
									<xsl:with-param name="result" select="$result" />
									<xsl:with-param name="index" select="$topIndex+1" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="rekursive">
									<xsl:with-param name="result" select="$result" />
									<xsl:with-param name="position" select="$temp" />
									<xsl:with-param name="topIndex" select="$topIndex" />
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="subTask"
							select="key('TeilaufgabenVon' ,$hash)[$indexTeilaufgabe]/child::field[@name='start']" />
						<xsl:choose>
							<xsl:when test="contains($position, $subTask)">
								<xsl:message>
									<xsl:text>KREIS modelliert! HASH: </xsl:text>
									<xsl:value-of select="$subTask" />
									<xsl:text>POSITION: </xsl:text>
									<xsl:value-of select="$position" />
								</xsl:message>
								<xsl:variable name="temp"
									select="concat(concat(concat(substring-before($position, '$,$'), '$,$'), string($indexTeilaufgabe+1)), concat('$]$', substring-after($position, '$]$')))" />
								<xsl:call-template name="rekursive">
									<xsl:with-param name="result" select="$result" />
									<xsl:with-param name="position" select="$temp" />
									<xsl:with-param name="topIndex" select="$topIndex" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:variable name="temp"
									select="concat(concat(concat(substring-before($position, '$,$'), '$,$'), string($indexTeilaufgabe+1)), concat('$]$', substring-after($position, '$]$')))" />
								<xsl:call-template name="rekursive">
									<xsl:with-param name="result" select="$result" />
									<xsl:with-param name="position"
										select="concat(concat(concat('$[$1$,$1$]$', $subTask), '$)$'), $temp)" />
									<xsl:with-param name="topIndex" select="$topIndex" />
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="positionNew"
					select="concat(concat(concat(concat(substring-before($position, '$[$'), '$[$'), string($indexAufOrgKomb+1)), '$,$'), substring-after($position, '$,$'))" />
				<xsl:call-template name="rekursive">
					<xsl:with-param name="position" select="$positionNew" />
					<xsl:with-param name="result"
						select="lgm:updateResult($result, $position, $aufOrgKomb[$indexAufOrgKomb])" />
					<xsl:with-param name="topIndex" select="$topIndex" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>