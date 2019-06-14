<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: alle Anwendungsbausteine -->
<!--type: html -->
<!--description: extrahiert alle Anwendungsbausteine mit Namen aus einem Modell -->
<!--author: Thomas Trommer 05.05.2005 -->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	version="2.0">

<!--Überschrift-->
<xsl:variable name="header_text" select="'Alle Anwendungsbausteine des Modells'" />

<!--Key - für die Performance-->
<xsl:key name="element" match="modell_3lgm_2/objects/element" use="@class" />


<xsl:template match="/">
<html>
	<head>
		<title><xsl:value-of select="$header_text" /></title>
		<meta name="author" content="Tool3lgm"/>
	</head>
	<body>
		<xsl:comment>Diese HTML-Datei wurde maschinell aus einem 3LGM&#178;-Modell erzeugt.</xsl:comment>
		<h1><xsl:value-of select="$header_text" /></h1>
		<h2><xsl:value-of select="modell_3lgm_2/header/title" /></h2>
		<h3>rechnerbasierte Anwendungsbausteine</h3>
		<table cellspacing="0" cellpadding="3" border="1">
			<tr>
				<th><xsl:value-of select="'Bezeichnung'" /></th>
				<th><xsl:value-of select="'Softwareprodukt'" /></th>
				<!-- <th><xsl:value-of select="'Datenbanksystem'" /></th> -->
				<th><xsl:value-of select="'DBVerwaltungssystem'" /></th>
			</tr>
			<tbody align="left" valign="top">
				<xsl:for-each select="key('element','RechAnwendungsbaustein')">
					<xsl:sort data-type="text" select="child::field[@name='name']" />
					<tr>
						<td>
							<xsl:value-of select="./field[@name='name']/text()" />&#160;
						</td>
					
						<!--Hash des aktuell bearbeiteten Anwendungsbausteins--> 
						<xsl:variable name="hash" select="@hash" />
					
						<!--Voraussetzungen:
						Softwareprodukthash und Datenbankhash immer im End-Feld  einer Relation definiert
						nur ein Softwareprodukt und nur eine Datenbank-->
					
						<xsl:variable name="Datenbankhash" select="key('element','RawbDbsVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
						<xsl:variable name="DBVShash" select="key('element','DbsDbvsVerbindung')/field[@name='start'][text()=$Datenbankhash]/../field[@name='end']/text()" />
						<xsl:variable name="Anwendungsprodukthash" select="key('element','RawbAwpVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
						<xsl:variable name="Softwareprodukthash" select="key('element','AwpSwpVerbindung')/field[@name='start'][text()=$Anwendungsprodukthash]/../field[@name='end']/text()" />
											
						<td>
							<xsl:variable name="Softwareprodukt" select="key('element','Softwareprodukt')[@hash=$Softwareprodukthash]/field[@name='name']/text()" />
							<xsl:value-of select="$Softwareprodukt"/> &#160;
						</td>
					
						<!--					
						<td>
							<xsl:variable name="Datenbank" select="key('element','Datenbanksystem')[@hash=$Datenbankhash]/field[@name='name']/text()" />
							<xsl:value-of select="$Datenbank" /> &#160;
						</td>
						-->

						<td>
							<xsl:variable name="DBVerwaltungssystem" select="key('element','DBVerwaltungssystem')[@hash=$DBVShash]/field[@name='name']/text()" />
							<xsl:value-of select="$DBVerwaltungssystem" /> &#160;
						</td>
					</tr>
				</xsl:for-each>
			</tbody>
		</table>
		
		


		<h3>allgemeine Anwendungsbausteine</h3>
		<table cellspacing="0" cellpadding="3" border="1">
			<tr>
				<th><xsl:value-of select="'Bezeichnung'" /></th>
				<th><xsl:value-of select="'Softwareprodukt'" /></th>
				<th><xsl:value-of select="'Datenbanksystem'" /></th>
			</tr>
			<tbody align="left" valign="top">
				<xsl:for-each select="key('element','Anwendungsbaustein')">
					<xsl:sort data-type="text" select="child::field[@name='name']" />
					<tr>
						<td>
							<xsl:value-of select="./field[@name='name']/text()" />&#160;
						</td>
					
						<!--Hash des zu bearbeitenden AWB-->
						<xsl:variable name="hash" select="@hash" />
					
						<!--Voraussetzungen:
						Softwareprodukthash und Datenbankhash immer im End-Feld  einer Relation definiert
						nur ein Softwareprodukt und nur eine Datenbank-->
					
				
		
						<xsl:variable name="Datenbankhash" select="key('element','AwbDbsVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
						<xsl:variable name="Anwendungsprodukthash" select="key('element','AwbAwpVerbindung')/field[@name='start'][text()=$hash]/../field[@name='end']/text()" />
						<xsl:variable name="Softwareprodukthash" select="key('element','AwpSwpVerbindung')/field[@name='start'][text()=$Anwendungsprodukthash]/../field[@name='end']/text()" />
											
						<td>
							<xsl:variable name="Softwareprodukt" select="key('element','Softwareprodukt')[@hash=$Softwareprodukthash]/field[@name='name']/text()" />
							<xsl:value-of select="$Softwareprodukt"/> &#160;
						</td>
					
											
						<td>
							<xsl:variable name="Datenbank" select="key('element','Datenbanksystem')[@hash=$Datenbankhash]/field[@name='name']/text()" />
							<xsl:value-of select="$Datenbank" /> &#160;
						</td>
					
				
						
					
					</tr>
				</xsl:for-each>
			</tbody>
		</table>
		



		<h3>papierbasierte Anwendungsbausteine</h3>
		<table cellspacing="0" cellpadding="3" border="1">
			<tr>
				<th><xsl:value-of select="'Bezeichnung'" /></th>
			</tr>
			<tbody align="left" valign="top">
				<xsl:for-each select="key('element','KonAnwendungsbaustein')">
					<xsl:sort data-type="text" select="child::field[@name='name']" />
					<tr>
						<td>
							<xsl:value-of select="./field[@name='name']/text()" />&#160;
						</td>
					</tr>
				</xsl:for-each>
			</tbody>
		</table>

	</body>
</html>
</xsl:template>

</xsl:stylesheet>