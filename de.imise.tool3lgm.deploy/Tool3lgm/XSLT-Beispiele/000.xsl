<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Modellstatistik -->
<!--type: html -->
<!--description: Erstellt eine Statistik über die Anzahl der einzelnen Objekte im Modell -->
<!--author: Thomas Rudert 18.09.2003 -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:variable name="header_text" select="'Modellstatistik'" />
<xsl:variable name="content_text" select="'Inhalt'" />
<xsl:variable name="fach_text" select="'Objekte der fachlichen Ebene'" />
<xsl:variable name="log_text" select="'Objekte der logischen Werkzeugebene'" />
<xsl:variable name="phy_text" select="'Objekte der physichen Werkzeugebene'" />
<xsl:variable name="inter_text" select="'Interebenenobjekte'" />
<xsl:variable name="summary_text" select="'Zusammenfassung'" />
<xsl:variable name="model_text" select="'Modellobjekt'" />
<xsl:variable name="number_text" select="'Anzahl'" />
<xsl:variable name="aufobjver_text" select="'AufObjVerbindung'" />
<xsl:variable name="auf_text" select="'Aufgabe'" />
<xsl:variable name="auforgkomb_text" select="'AufOrgKombination'" />
<xsl:variable name="obj_text" select="'Objekttyp'" />
<xsl:variable name="org_text" select="'Organisationseinheit'" />
<xsl:variable name="pro_text" select="'Prozess'" />
<xsl:variable name="awb_text" select="'Anwendungsbaustein'" />
<xsl:variable name="awp_text" select="'Anwendungsprogramm'" />
<xsl:variable name="bau_text" select="'Bausteinschnittstelle'" />
<xsl:variable name="ben_text" select="'Benutzungsschnittstelle'" />
<xsl:variable name="dbs_text" select="'Datenbanksystem'" />
<xsl:variable name="dat_text" select="'Datensatztyp'" />
<xsl:variable name="dbv_text" select="'DBVerwaltungssystem'" />
<xsl:variable name="doks_text" select="'Dokumentensammlung'" />
<xsl:variable name="dokt_text" select="'Dokumententyp'" />
<xsl:variable name="ert_text" select="'Ereignistyp'" />
<xsl:variable name="etnt_text" select="'ETNTKombination'" />
<xsl:variable name="etdt_text" select="'ETDTKombination'" />
<xsl:variable name="kommp_text" select="'Kommunikationsprozess'" />
<xsl:variable name="komms_text" select="'Kommunikationsstandard'" />
<xsl:variable name="kon_text" select="'KonAnwendungsbaustein'" />
<xsl:variable name="nach_text" select="'Nachrichtentyp'" />
<xsl:variable name="orgp_text" select="'Organisationsplan'" />
<xsl:variable name="rech_text" select="'RechAnwendungsbaustein'" />
<xsl:variable name="sof_text" select="'Softwareprodukt'" />
<xsl:variable name="bst_text" select="'Bausteintyp'" />
<xsl:variable name="duv_text" select="'DatenuebertragungsVerbindung'" />
<xsl:variable name="netp_text" select="'Netzprotokoll'" />
<xsl:variable name="nett_text" select="'Netztyp'" />
<xsl:variable name="dvb_text" select="'PhysischerDVBaustein'" />
<xsl:variable name="sta_text" select="'Standort'" />
<xsl:variable name="sub_text" select="'Subnetz'" />
<xsl:variable name="abk_text" select="'ABKonfiguration'" />
<xsl:variable name="dbk_text" select="'DBKonfiguration'" />
<xsl:variable name="sumKnot_text" select="'Summe der Knoten'" />
<xsl:variable name="sumKan_text" select="'Summe der Kanten'" />
<xsl:variable name="sumText_text" select="'Summe der Textfelder'" />
<xsl:variable name="all_text" select="'Gesamtsumme'" />
<xsl:variable name="sum_text" select="'Summe'" />

<xsl:key name="anzahlObjekte" match="element" use="@class" />

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
		<h3><xsl:value-of select="$content_text" /></h3>
		<ol>
			<li><a href="#fach"><xsl:value-of select="$fach_text" /></a></li>
			<li><a href="#log"><xsl:value-of select="$log_text" /></a></li>
			<li><a href="#phy"><xsl:value-of select="$phy_text" /></a></li>
			<li><a href="#inter"><xsl:value-of select="$inter_text" /></a></li>
			<li><a href="#zusammen"><xsl:value-of select="$summary_text" /></a></li>
		</ol>
		<xsl:apply-templates select="modell_3lgm_2/objects" />
	</body>
</html>
</xsl:template>

<xsl:template match="modell_3lgm_2/objects">
<!-- Knoten der fachlichen Ebene -->
	<xsl:variable name="AufObjVerbindung" select="count(key('anzahlObjekte', 'AufObjVerbindung'))" />
	<xsl:variable name="Aufgabe" select="count(key('anzahlObjekte', 'Aufgabe'))" />
	<xsl:variable name="AufOrgKombination" select="count(key('anzahlObjekte', 'AufOrgKombination'))" />
	<xsl:variable name="Objekttyp" select="count(key('anzahlObjekte', 'Objekttyp'))" />
	<xsl:variable name="Organisationseinheit" select="count(key('anzahlObjekte', 'Organisationseinheit'))" />
	<xsl:variable name="Prozess" select="count(key('anzahlObjekte', 'Prozess'))" />
	<xsl:variable name="sumFach" select="$AufObjVerbindung + $Aufgabe + $AufOrgKombination + $Objekttyp + $Organisationseinheit + $Prozess" />

	<a name="fach"><h3><xsl:value-of select="$fach_text" /></h3></a>
	<table cellspacing="0" cellpadding="3" border="1">
		<thead><tr><th width="300"><xsl:value-of select="$model_text" /></th><th><xsl:value-of select="$number_text" /></th></tr></thead>
		<tfoot align="right" valign="top"><tr><td><xsl:value-of select="$sum_text" /></td><td><xsl:value-of select="$sumFach" /></td></tr></tfoot>
		<tbody align="left" valign="top">
			<tr><td><xsl:value-of select="$aufobjver_text" /></td><td><xsl:value-of select="$AufObjVerbindung" /></td></tr>
			<tr><td><xsl:value-of select="$auf_text" /></td><td><xsl:value-of select="$Aufgabe" /></td></tr>
			<tr><td><xsl:value-of select="$auforgkomb_text" /></td><td><xsl:value-of select="$AufOrgKombination" /></td></tr>
			<tr><td><xsl:value-of select="$obj_text" /></td><td><xsl:value-of select="$Objekttyp" /></td></tr>
			<tr><td><xsl:value-of select="$org_text" /></td><td><xsl:value-of select="$Organisationseinheit" /></td></tr>
			<tr><td><xsl:value-of select="$pro_text" /></td><td><xsl:value-of select="$Prozess" /></td></tr>
		</tbody>
	</table>
	
<!-- Knoten der logischen Werkzeugebene -->
	<xsl:variable name="Anwendungsbaustein" select="count(key('anzahlObjekte', 'Anwendungsbaustein'))" />
	<xsl:variable name="Anwendungsprogramm" select="count(key('anzahlObjekte', 'Anwendungsprogramm'))" />
	<xsl:variable name="Bausteinschnittstelle" select="count(key('anzahlObjekte', 'Bausteinschnittstelle'))" />
	<xsl:variable name="Benutzungsschnittstelle" select="count(key('anzahlObjekte', 'Benutzungsschnittstelle'))" />
	<xsl:variable name="Datenbanksystem" select="count(key('anzahlObjekte', 'Datenbanksystem'))" />
	<xsl:variable name="Datensatztyp" select="count(key('anzahlObjekte', 'Datensatztyp'))" />
	<xsl:variable name="DBVerwaltungssystem" select="count(key('anzahlObjekte', 'DBVerwaltungssystem'))" />
	<xsl:variable name="Dokumentensammlung" select="count(key('anzahlObjekte', 'Dokumentensammlung'))" />
	<xsl:variable name="Dokumententyp" select="count(key('anzahlObjekte', 'Dokumententyp'))" />
	<xsl:variable name="Ereignistyp" select="count(key('anzahlObjekte', 'Ereignistyp'))" />
	<xsl:variable name="ETNTKombination" select="count(key('anzahlObjekte', 'ETNTKombination'))" />
	<xsl:variable name="ETDTKombination" select="count(key('anzahlObjekte', 'ETDTKombination'))" />
	<xsl:variable name="Kommunikationsprozess" select="count(key('anzahlObjekte', 'Kommunikationsprozess'))" />
	<xsl:variable name="Kommunikationsstandard" select="count(key('anzahlObjekte', 'Kommunikationsstandard'))" />
	<xsl:variable name="KonAnwendungsbaustein" select="count(key('anzahlObjekte', 'KonAnwendungsbaustein'))" />
	<xsl:variable name="Nachrichtentyp" select="count(key('anzahlObjekte', 'Nachrichtentyp'))" />
	<xsl:variable name="Organisationsplan" select="count(key('anzahlObjekte', 'Organisationsplan'))" />
	<xsl:variable name="RechAnwendungsbaustein" select="count(key('anzahlObjekte', 'RechAnwendungsbaustein'))" />
	<xsl:variable name="Softwareprodukt" select="count(key('anzahlObjekte', 'Softwareprodukt'))" />
	<xsl:variable name="sumLog" select="$Anwendungsbaustein + $Anwendungsprogramm + $Bausteinschnittstelle + $Benutzungsschnittstelle + $Datenbanksystem + $Datensatztyp + $DBVerwaltungssystem + $Dokumentensammlung + $Dokumententyp +	$Ereignistyp + $ETNTKombination + $ETDTKombination + $Kommunikationsprozess + $Kommunikationsstandard + $KonAnwendungsbaustein + $Nachrichtentyp + $Organisationsplan + $RechAnwendungsbaustein + $Softwareprodukt" />
	
	<a name="log"><h3><xsl:value-of select="$log_text" /></h3></a>
	<table cellspacing="0" cellpadding="3" border="1">
		<thead><tr><th width="300"><xsl:value-of select="$model_text" /></th><th><xsl:value-of select="$number_text" /></th></tr></thead>
		<tfoot align="right" valign="top"><tr><td><xsl:value-of select="$sum_text" /></td><td><xsl:value-of select="$sumLog" /></td></tr></tfoot>
		<tbody align="left" valign="top">			
			<tr><td><xsl:value-of select="$awb_text" /></td><td><xsl:value-of select="$Anwendungsbaustein" /></td></tr>
			<tr><td><xsl:value-of select="$awp_text" /></td><td><xsl:value-of select="$Anwendungsprogramm" /></td></tr>
			<tr><td><xsl:value-of select="$bau_text" /></td><td><xsl:value-of select="$Bausteinschnittstelle" /></td></tr>
			<tr><td><xsl:value-of select="$ben_text" /></td><td><xsl:value-of select="$Benutzungsschnittstelle" /></td></tr>
			<tr><td><xsl:value-of select="$dbs_text" /></td><td><xsl:value-of select="$Datenbanksystem" /></td></tr>
			<tr><td><xsl:value-of select="$dat_text" /></td><td><xsl:value-of select="$Datensatztyp" /></td></tr>
			<tr><td><xsl:value-of select="$dbv_text" /></td><td><xsl:value-of select="$DBVerwaltungssystem" /></td></tr>
			<tr><td><xsl:value-of select="$doks_text" /></td><td><xsl:value-of select="$Dokumentensammlung" /></td></tr>
			<tr><td><xsl:value-of select="$dokt_text" /></td><td><xsl:value-of select="$Dokumententyp" /></td></tr>
			<tr><td><xsl:value-of select="$ert_text" /></td><td><xsl:value-of select="$Ereignistyp" /></td></tr>
			<tr><td><xsl:value-of select="$etnt_text" /></td><td><xsl:value-of select="$ETNTKombination" /></td></tr>
			<tr><td><xsl:value-of select="$etdt_text" /></td><td><xsl:value-of select="$ETDTKombination" /></td></tr>
			<tr><td><xsl:value-of select="$kommp_text" /></td><td><xsl:value-of select="$Kommunikationsprozess" /></td></tr>
			<tr><td><xsl:value-of select="$komms_text" /></td><td><xsl:value-of select="$Kommunikationsstandard" /></td></tr>
			<tr><td><xsl:value-of select="$kon_text" /></td><td><xsl:value-of select="$KonAnwendungsbaustein" /></td></tr>
			<tr><td><xsl:value-of select="$nach_text" /></td><td><xsl:value-of select="$Nachrichtentyp" /></td></tr>
			<tr><td><xsl:value-of select="$orgp_text" /></td><td><xsl:value-of select="$Organisationsplan" /></td></tr>
			<tr><td><xsl:value-of select="$rech_text" /></td><td><xsl:value-of select="$RechAnwendungsbaustein" /></td></tr>
			<tr><td><xsl:value-of select="$sof_text" /></td><td><xsl:value-of select="$Softwareprodukt" /></td></tr>
		</tbody>
	</table>

<!-- Knoten der physischen Werkzeugebene -->
	<xsl:variable name="Bausteintyp" select="count(key('anzahlObjekte', 'Bausteintyp'))" />
	<xsl:variable name="DatenuebertragungsVerbindung" select="count(key('anzahlObjekte', 'DatenuebertragungsVerbindung'))" />
	<xsl:variable name="Netzprotokoll" select="count(key('anzahlObjekte', 'Netzprotokoll'))" />
	<xsl:variable name="Netztyp" select="count(key('anzahlObjekte', 'Netztyp'))" />
	<xsl:variable name="PhysischerDVBaustein" select="count(key('anzahlObjekte', 'PhysischerDVBaustein'))" />
	<xsl:variable name="Standort" select="count(key('anzahlObjekte', 'Standort'))" />
	<xsl:variable name="Subnetz" select="count(key('anzahlObjekte', 'Subnetz'))" />
	<xsl:variable name="sumPhy" select="$Bausteintyp + $DatenuebertragungsVerbindung + $Netzprotokoll + $Netztyp + $PhysischerDVBaustein + $Standort + $Subnetz" />

	<a name="phy"><h3><xsl:value-of select="$phy_text" /></h3></a>
	<table cellspacing="0" cellpadding="3" border="1">
		<thead><tr><th width="300"><xsl:value-of select="$model_text" /></th><th><xsl:value-of select="$number_text" /></th></tr></thead>
		<tfoot align="right" valign="top"><tr><td><xsl:value-of select="$sum_text" /></td><td><xsl:value-of select="$sumPhy" /></td></tr></tfoot>
		<tbody align="left" valign="top">
			<tr><td><xsl:value-of select="$bst_text" /></td><td><xsl:value-of select="$Bausteintyp" /></td></tr>
			<tr><td><xsl:value-of select="$duv_text" /></td><td><xsl:value-of select="$DatenuebertragungsVerbindung" /></td></tr>
			<tr><td><xsl:value-of select="$netp_text" /></td><td><xsl:value-of select="$Netzprotokoll" /></td></tr>
			<tr><td><xsl:value-of select="$nett_text" /></td><td><xsl:value-of select="$Netztyp" /></td></tr>
			<tr><td><xsl:value-of select="$dvb_text" /></td><td><xsl:value-of select="$PhysischerDVBaustein" /></td></tr>
			<tr><td><xsl:value-of select="$sta_text" /></td><td><xsl:value-of select="$Standort" /></td></tr>
			<tr><td><xsl:value-of select="$sub_text" /></td><td><xsl:value-of select="$Subnetz" /></td></tr>
		</tbody>
	</table>

<!-- intere Ebenenobjekte -->
	<xsl:variable name="ABKonfiguration" select="count(key('anzahlObjekte', 'ABKonfiguration'))" />
	<xsl:variable name="DBKonfiguration" select="count(key('anzahlObjekte', 'DBKonfiguration'))" />
	<xsl:variable name="sumInter" select="$ABKonfiguration + $DBKonfiguration" />

	<a name="inter"><h3><xsl:value-of select="$inter_text" /></h3></a>
	<table cellspacing="0" cellpadding="3" border="1">
	<thead><tr><th width="300"><xsl:value-of select="$model_text" /></th><th><xsl:value-of select="$number_text" /></th></tr></thead>
		<tfoot align="right" valign="top"><tr><td><xsl:value-of select="$sum_text" /></td><td><xsl:value-of select="$sumInter" /></td></tr></tfoot>
		<tbody align="left" valign="top">
			<tr><td><xsl:value-of select="$abk_text" /></td><td><xsl:value-of select="$ABKonfiguration" /></td></tr>
			<tr><td><xsl:value-of select="$dbk_text" /></td><td><xsl:value-of select="$DBKonfiguration" /></td></tr>
		</tbody>
	</table>

<!-- sonstige Modellobjekte -->
	<xsl:variable name="Textfelder" select="count(key('anzahlObjekte', 'TextfeldFach')) + count(key('anzahlObjekte', 'TextfeldLog')) + count(key('anzahlObjekte', 'TextfeldPhy'))" />
		
<!-- Zusammenfassung -->
	<xsl:variable name="sumKnoten" select="$sumFach - $AufObjVerbindung + $sumLog + $sumPhy - $DatenuebertragungsVerbindung + $DBKonfiguration" />
	<xsl:variable name="sumKante" select="	
	count(key('anzahlObjekte', 'AufAufOrgVerbindung')) + 
	count(key('anzahlObjekte', 'AufAufVerbindung')) +
	count(key('anzahlObjekte', 'AufObjVerbindung')) +
	count(key('anzahlObjekte', 'AwbAwbkVerbindung')) +
	count(key('anzahlObjekte', 'AwbAwbVerbindung')) +
	count(key('anzahlObjekte', 'AwbAwpVerbindung')) +
	count(key('anzahlObjekte', 'AwbDbsVerbindung')) +
	count(key('anzahlObjekte', 'AwbDoksVerbindung')) +
	count(key('anzahlObjekte', 'AwbkAufOrgVerbindung')) +
	count(key('anzahlObjekte', 'AwbKommssVerbindung')) +
	count(key('anzahlObjekte', 'AwbOrgpVerbindung')) +
	count(key('anzahlObjekte', 'AwpSwpVerbindung')) +
	count(key('anzahlObjekte', 'BssEtntVerbindung')) +
	count(key('anzahlObjekte', 'BssKommstVerbindung')) +
	count(key('anzahlObjekte', 'DatenuebertragungsVerbindung')) +
	count(key('anzahlObjekte', 'DbsDatVerbindung')) +
	count(key('anzahlObjekte', 'DbsDbvsVerbindung')) +
	count(key('anzahlObjekte', 'DoksDokVerbindung')) +
	count(key('anzahlObjekte', 'EtAufVerbindung')) +
	count(key('anzahlObjekte', 'EtntDotVerbindung')) +
	count(key('anzahlObjekte', 'EtntEtVerbindung')) +
	count(key('anzahlObjekte', 'EtntKommstVerbindung')) +
	count(key('anzahlObjekte', 'EtntNatVerbindung')) +
	count(key('anzahlObjekte', 'KawbAwbVerbindung')) +
	count(key('anzahlObjekte', 'KawbDoksVerbindung')) +
	count(key('anzahlObjekte', 'KawbOrgpVerbindung')) +
	count(key('anzahlObjekte', 'KommbezEtntVerbindung')) +
	count(key('anzahlObjekte', 'KommBeziehung')) +
	count(key('anzahlObjekte', 'ObjLogspVerbindung')) +
	count(key('anzahlObjekte', 'ObjObjVerbindung')) +
	count(key('anzahlObjekte', 'ObjReprVerbindung')) +
	count(key('anzahlObjekte', 'OrgAufOrgVerbindung')) +
	count(key('anzahlObjekte', 'PdvbBtypVerbindung')) +
	count(key('anzahlObjekte', 'PdvbkAwbVerbindung')) +
	count(key('anzahlObjekte', 'PdvbPdvbkVerbindung')) +
	count(key('anzahlObjekte', 'PdvbPdvbVerbindung')) +
	count(key('anzahlObjekte', 'PdvbStoVerbindung')) +
	count(key('anzahlObjekte', 'PdvbSubnVerbindung')) +
	count(key('anzahlObjekte', 'PrzAufVerbindung')) +
	count(key('anzahlObjekte', 'RawbAwbVerbindung')) +
	count(key('anzahlObjekte', 'RawbAwpVerbindung')) +
	count(key('anzahlObjekte', 'RawbDbsVerbindung')) +
	count(key('anzahlObjekte', 'SubnNetzpVerbindung')) +
	count(key('anzahlObjekte', 'SubnNetztVerbindung')) +
	count(key('anzahlObjekte', 'SwpAufVerbindung'))" />

	<xsl:variable name="Summe" select="$sumKnoten + $sumKante + $Textfelder" />

	<a name="zusammen"><h3><xsl:value-of select="$summary_text" /></h3></a>
	<table cellspacing="0" cellpadding="3" border="1">
		<thead><tr><th width="300"><xsl:value-of select="$model_text" /></th><th><xsl:value-of select="$number_text" /></th></tr></thead>
		<tfoot align="right" valign="top"><tr><td><xsl:value-of select="$all_text" /></td><td><xsl:value-of select="$Summe" /></td></tr></tfoot>	
		<tbody align="left" valign="top">
			<tr><td><xsl:value-of select="$sumKnot_text" /></td><td><xsl:value-of select="$sumKnoten" /></td></tr>
			<tr><td><xsl:value-of select="$sumKan_text" /></td><td><xsl:value-of select="$sumKante" /></td></tr>
			<tr><td><xsl:value-of select="$sumText_text" /></td><td><xsl:value-of select="$Textfelder" /></td></tr>
		</tbody>
	</table>

</xsl:template>

</xsl:stylesheet>