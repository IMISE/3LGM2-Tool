<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Elements: Statistics -->
<!--type: html -->
<!--description: Counts the number of entities for each object class -->
<!--author: Thomas Rudert -->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:variable name="header_text" select="'Model statistics'" />
	<xsl:variable name="content_text" select="'Contents'" />
	<xsl:variable name="fach_text" select="'Objects of the Domain Layer'" />
	<xsl:variable name="log_text" select="'Objects of the Logical Tool Layer'" />
	<xsl:variable name="phy_text" select="'Objects of the Physical Tool Layer'" />
	<xsl:variable name="inter_text" select="'Inter-Layer Objects'" />
	<xsl:variable name="summary_text" select="'Summary'" />
	<xsl:variable name="model_text" select="'Model object'" />
	<xsl:variable name="number_text" select="'Number'" />
	<xsl:variable name="aufobjver_text" select="'Function-Entity Type Connection'" />
	<xsl:variable name="auf_text" select="'Function'" />
	<xsl:variable name="auforgkomb_text"
		select="'Function-Organizational Unit Combination'" />
	<xsl:variable name="obj_text" select="'Entity Type'" />
	<xsl:variable name="org_text" select="'Organizational Unit'" />
	<xsl:variable name="pro_text" select="'Process'" />
	<xsl:variable name="awb_text" select="'Application Component'" />
	<xsl:variable name="awp_text" select="'Application Program'" />
	<xsl:variable name="bau_text" select="'Component Interface'" />
	<xsl:variable name="ben_text" select="'User Interface'" />
	<xsl:variable name="dbs_text" select="'Database System'" />
	<xsl:variable name="dat_text" select="'Dataset Type'" />
	<xsl:variable name="dbv_text" select="'Database Management System'" />
	<xsl:variable name="doks_text" select="'Document Collection'" />
	<xsl:variable name="dokt_text" select="'Document Type'" />
	<xsl:variable name="ert_text" select="'Event type'" />
	<xsl:variable name="etnt_text" select="'ETMT-Combination'" />
	<xsl:variable name="etdt_text" select="'ETDT-Combination'" />
	<xsl:variable name="kommp_text" select="'Communication Process'" />
	<xsl:variable name="komms_text" select="'Communication Standard'" />
	<xsl:variable name="kon_text" select="'KonAnwendungsbaustein'" />
	<xsl:variable name="nach_text" select="'Message Type'" />
	<xsl:variable name="orgp_text" select="'Organizational Plan'" />
	<xsl:variable name="rech_text"
		select="'Computer-based application component'" />
	<xsl:variable name="sof_text" select="'Software Product'" />
	<xsl:variable name="bst_text" select="'Component Type'" />
	<xsl:variable name="duv_text" select="'Data transmission link'" />
	<xsl:variable name="netp_text" select="'Net Protocol'" />
	<xsl:variable name="nett_text" select="'Net Type'" />
	<xsl:variable name="dvb_text" select="'Physical Data Processing Component'" />
	<xsl:variable name="sta_text" select="'Location'" />
	<xsl:variable name="sub_text" select="'Subnet'" />
	<xsl:variable name="abk_text" select="'Application Component Configuration'" />
	<xsl:variable name="dbk_text" select="'Database Configuration'" />
	<xsl:variable name="sumKnot_text" select="'Sum of Vertices'" />
	<xsl:variable name="sumKan_text" select="'Sum of Edges'" />
	<xsl:variable name="sumText_text" select="'Sum of Description Fields'" />
	<xsl:variable name="all_text" select="'Total sum'" />
	<xsl:variable name="sum_text" select="'Sum'" />

	<xsl:key name="anzahlObjekte" match="element" use="@class" />

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
					This HTML file was generated automatically from a 3LGM file..
				</xsl:comment>
				<h1>
					<xsl:value-of select="$header_text" />
				</h1>
				<h2>
					<xsl:value-of select="modell_3lgm_2/header/title" />
				</h2>
				<h3>
					<xsl:value-of select="$content_text" />
				</h3>
				<ol>
					<li>
						<a href="#fach">
							<xsl:value-of select="$fach_text" />
						</a>
					</li>
					<li>
						<a href="#log">
							<xsl:value-of select="$log_text" />
						</a>
					</li>
					<li>
						<a href="#phy">
							<xsl:value-of select="$phy_text" />
						</a>
					</li>
					<li>
						<a href="#inter">
							<xsl:value-of select="$inter_text" />
						</a>
					</li>
					<li>
						<a href="#zusammen">
							<xsl:value-of select="$summary_text" />
						</a>
					</li>
				</ol>
				<xsl:apply-templates select="modell_3lgm_2/objects" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="modell_3lgm_2/objects">
		<!-- Knoten der fachlichen Ebene -->
		<xsl:variable name="AufObjVerbindung"
			select="count(key('anzahlObjekte', 'AufObjVerbindung'))" />
		<xsl:variable name="Aufgabe"
			select="count(key('anzahlObjekte', 'Aufgabe'))" />
		<xsl:variable name="AufOrgKombination"
			select="count(key('anzahlObjekte', 'AufOrgKombination'))" />
		<xsl:variable name="Objekttyp"
			select="count(key('anzahlObjekte', 'Objekttyp'))" />
		<xsl:variable name="Organisationseinheit"
			select="count(key('anzahlObjekte', 'Organisationseinheit'))" />
		<xsl:variable name="Prozess"
			select="count(key('anzahlObjekte', 'Prozess'))" />
		<xsl:variable name="sumFach"
			select="$AufObjVerbindung + $Aufgabe + $AufOrgKombination + $Objekttyp + $Organisationseinheit + $Prozess" />

		<a name="fach">
			<h3>
				<xsl:value-of select="$fach_text" />
			</h3>
		</a>
		<table cellspacing="0" cellpadding="3" border="1">
			<thead>
				<tr>
					<th width="300">
						<xsl:value-of select="$model_text" />
					</th>
					<th>
						<xsl:value-of select="$number_text" />
					</th>
				</tr>
			</thead>
			<tfoot align="right" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$sum_text" />
					</td>
					<td>
						<xsl:value-of select="$sumFach" />
					</td>
				</tr>
			</tfoot>
			<tbody align="left" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$aufobjver_text" />
					</td>
					<td>
						<xsl:value-of select="$AufObjVerbindung" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$auf_text" />
					</td>
					<td>
						<xsl:value-of select="$Aufgabe" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$auforgkomb_text" />
					</td>
					<td>
						<xsl:value-of select="$AufOrgKombination" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$obj_text" />
					</td>
					<td>
						<xsl:value-of select="$Objekttyp" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$org_text" />
					</td>
					<td>
						<xsl:value-of select="$Organisationseinheit" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$pro_text" />
					</td>
					<td>
						<xsl:value-of select="$Prozess" />
					</td>
				</tr>
			</tbody>
		</table>

		<!-- Knoten der logischen Werkzeugebene -->
		<xsl:variable name="Anwendungsbaustein"
			select="count(key('anzahlObjekte', 'Anwendungsbaustein'))" />
		<xsl:variable name="Anwendungsprogramm"
			select="count(key('anzahlObjekte', 'Anwendungsprogramm'))" />
		<xsl:variable name="Bausteinschnittstelle"
			select="count(key('anzahlObjekte', 'Bausteinschnittstelle'))" />
		<xsl:variable name="Benutzungsschnittstelle"
			select="count(key('anzahlObjekte', 'Benutzungsschnittstelle'))" />
		<xsl:variable name="Datenbanksystem"
			select="count(key('anzahlObjekte', 'Datenbanksystem'))" />
		<xsl:variable name="Datensatztyp"
			select="count(key('anzahlObjekte', 'Datensatztyp'))" />
		<xsl:variable name="DBVerwaltungssystem"
			select="count(key('anzahlObjekte', 'DBVerwaltungssystem'))" />
		<xsl:variable name="Dokumentensammlung"
			select="count(key('anzahlObjekte', 'Dokumentensammlung'))" />
		<xsl:variable name="Dokumententyp"
			select="count(key('anzahlObjekte', 'Dokumententyp'))" />
		<xsl:variable name="Ereignistyp"
			select="count(key('anzahlObjekte', 'Ereignistyp'))" />
		<xsl:variable name="ETNTKombination"
			select="count(key('anzahlObjekte', 'EreignisNachrichtenTyp'))" />
		<xsl:variable name="ETDTKombination"
			select="count(key('anzahlObjekte', 'EreignisDokumentenTyp'))" />
		<xsl:variable name="Kommunikationsprozess"
			select="count(key('anzahlObjekte', 'Kommunikationsprozess'))" />
		<xsl:variable name="Kommunikationsstandard"
			select="count(key('anzahlObjekte', 'Kommunikationsstandard'))" />
		<xsl:variable name="KonAnwendungsbaustein"
			select="count(key('anzahlObjekte', 'KonAnwendungsbaustein'))" />
		<xsl:variable name="Nachrichtentyp"
			select="count(key('anzahlObjekte', 'Nachrichtentyp'))" />
		<xsl:variable name="Organisationsplan"
			select="count(key('anzahlObjekte', 'Organisationsplan'))" />
		<xsl:variable name="RechAnwendungsbaustein"
			select="count(key('anzahlObjekte', 'RechAnwendungsbaustein'))" />
		<xsl:variable name="Softwareprodukt"
			select="count(key('anzahlObjekte', 'Softwareprodukt'))" />
		<xsl:variable name="sumLog"
			select="$Anwendungsbaustein + $Anwendungsprogramm + $Bausteinschnittstelle + $Benutzungsschnittstelle + $Datenbanksystem + $Datensatztyp + $DBVerwaltungssystem + $Dokumentensammlung + $Dokumententyp +	$Ereignistyp + $ETNTKombination + $ETDTKombination + $Kommunikationsprozess + $Kommunikationsstandard + $KonAnwendungsbaustein + $Nachrichtentyp + $Organisationsplan + $RechAnwendungsbaustein + $Softwareprodukt" />

		<a name="log">
			<h3>
				<xsl:value-of select="$log_text" />
			</h3>
		</a>
		<table cellspacing="0" cellpadding="3" border="1">
			<thead>
				<tr>
					<th width="300">
						<xsl:value-of select="$model_text" />
					</th>
					<th>
						<xsl:value-of select="$number_text" />
					</th>
				</tr>
			</thead>
			<tfoot align="right" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$sum_text" />
					</td>
					<td>
						<xsl:value-of select="$sumLog" />
					</td>
				</tr>
			</tfoot>
			<tbody align="left" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$awb_text" />
					</td>
					<td>
						<xsl:value-of select="$Anwendungsbaustein" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$awp_text" />
					</td>
					<td>
						<xsl:value-of select="$Anwendungsprogramm" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$bau_text" />
					</td>
					<td>
						<xsl:value-of select="$Bausteinschnittstelle" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$ben_text" />
					</td>
					<td>
						<xsl:value-of select="$Benutzungsschnittstelle" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$dbs_text" />
					</td>
					<td>
						<xsl:value-of select="$Datenbanksystem" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$dat_text" />
					</td>
					<td>
						<xsl:value-of select="$Datensatztyp" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$dbv_text" />
					</td>
					<td>
						<xsl:value-of select="$DBVerwaltungssystem" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$doks_text" />
					</td>
					<td>
						<xsl:value-of select="$Dokumentensammlung" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$dokt_text" />
					</td>
					<td>
						<xsl:value-of select="$Dokumententyp" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$ert_text" />
					</td>
					<td>
						<xsl:value-of select="$Ereignistyp" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$etnt_text" />
					</td>
					<td>
						<xsl:value-of select="$ETNTKombination" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$etdt_text" />
					</td>
					<td>
						<xsl:value-of select="$ETDTKombination" />
					</td>
				</tr>
				<!--<tr><td><xsl:value-of select="$kommp_text" /></td><td><xsl:value-of 
					select="$Kommunikationsprozess" /></td></tr> -->
				<tr>
					<td>
						<xsl:value-of select="$komms_text" />
					</td>
					<td>
						<xsl:value-of select="$Kommunikationsstandard" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$kon_text" />
					</td>
					<td>
						<xsl:value-of select="$KonAnwendungsbaustein" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$nach_text" />
					</td>
					<td>
						<xsl:value-of select="$Nachrichtentyp" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$orgp_text" />
					</td>
					<td>
						<xsl:value-of select="$Organisationsplan" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$rech_text" />
					</td>
					<td>
						<xsl:value-of select="$RechAnwendungsbaustein" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$sof_text" />
					</td>
					<td>
						<xsl:value-of select="$Softwareprodukt" />
					</td>
				</tr>
			</tbody>
		</table>

		<!-- Knoten der physischen Werkzeugebene -->
		<xsl:variable name="Bausteintyp"
			select="count(key('anzahlObjekte', 'Bausteintyp'))" />
		<xsl:variable name="DatenuebertragungsVerbindung"
			select="count(key('anzahlObjekte', 'DatenuebertragungsVerbindung'))" />
		<xsl:variable name="Netzprotokoll"
			select="count(key('anzahlObjekte', 'Netzprotokoll'))" />
		<xsl:variable name="Netztyp"
			select="count(key('anzahlObjekte', 'Netztyp'))" />
		<xsl:variable name="PhysischerDVBaustein"
			select="count(key('anzahlObjekte', 'PhysischerDVBaustein'))" />
		<xsl:variable name="Standort"
			select="count(key('anzahlObjekte', 'Standort'))" />
		<xsl:variable name="Subnetz"
			select="count(key('anzahlObjekte', 'Subnetz'))" />
		<xsl:variable name="sumPhy"
			select="$Bausteintyp + $DatenuebertragungsVerbindung + $Netzprotokoll + $Netztyp + $PhysischerDVBaustein + $Standort + $Subnetz" />

		<a name="phy">
			<h3>
				<xsl:value-of select="$phy_text" />
			</h3>
		</a>
		<table cellspacing="0" cellpadding="3" border="1">
			<thead>
				<tr>
					<th width="300">
						<xsl:value-of select="$model_text" />
					</th>
					<th>
						<xsl:value-of select="$number_text" />
					</th>
				</tr>
			</thead>
			<tfoot align="right" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$sum_text" />
					</td>
					<td>
						<xsl:value-of select="$sumPhy" />
					</td>
				</tr>
			</tfoot>
			<tbody align="left" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$bst_text" />
					</td>
					<td>
						<xsl:value-of select="$Bausteintyp" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$duv_text" />
					</td>
					<td>
						<xsl:value-of select="$DatenuebertragungsVerbindung" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$netp_text" />
					</td>
					<td>
						<xsl:value-of select="$Netzprotokoll" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$nett_text" />
					</td>
					<td>
						<xsl:value-of select="$Netztyp" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$dvb_text" />
					</td>
					<td>
						<xsl:value-of select="$PhysischerDVBaustein" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$sta_text" />
					</td>
					<td>
						<xsl:value-of select="$Standort" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$sub_text" />
					</td>
					<td>
						<xsl:value-of select="$Subnetz" />
					</td>
				</tr>
			</tbody>
		</table>

		<!-- intere Ebenenobjekte -->
		<xsl:variable name="ABKonfiguration"
			select="count(key('anzahlObjekte', 'ABKonfiguration'))" />
		<xsl:variable name="DBKonfiguration"
			select="count(key('anzahlObjekte', 'DBKonfiguration'))" />
		<xsl:variable name="sumInter" select="$ABKonfiguration + $DBKonfiguration" />

		<a name="inter">
			<h3>
				<xsl:value-of select="$inter_text" />
			</h3>
		</a>
		<table cellspacing="0" cellpadding="3" border="1">
			<thead>
				<tr>
					<th width="300">
						<xsl:value-of select="$model_text" />
					</th>
					<th>
						<xsl:value-of select="$number_text" />
					</th>
				</tr>
			</thead>
			<tfoot align="right" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$sum_text" />
					</td>
					<td>
						<xsl:value-of select="$sumInter" />
					</td>
				</tr>
			</tfoot>
			<tbody align="left" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$abk_text" />
					</td>
					<td>
						<xsl:value-of select="$ABKonfiguration" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$dbk_text" />
					</td>
					<td>
						<xsl:value-of select="$DBKonfiguration" />
					</td>
				</tr>
			</tbody>
		</table>

		<!-- sonstige Modellobjekte -->
		<xsl:variable name="Textfelder"
			select="count(key('anzahlObjekte', 'Textfield'))" />

		<!-- Zusammenfassung -->
		<xsl:variable name="sumKnoten"
			select="$sumFach - $AufObjVerbindung + $sumLog + $sumPhy - $DatenuebertragungsVerbindung + $DBKonfiguration" />
		<xsl:variable name="sumKante"
			select="	
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
	count(key('anzahlObjekte', 'AwbKawbVerbindung')) +
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
	count(key('anzahlObjekte', 'RawbRawbVerbindung')) +
	count(key('anzahlObjekte', 'RawbAwpVerbindung')) +
	count(key('anzahlObjekte', 'RawbDbsVerbindung')) +
	count(key('anzahlObjekte', 'SubnNetzpVerbindung')) +
	count(key('anzahlObjekte', 'SubnNetztVerbindung')) +
	count(key('anzahlObjekte', 'SwpAufVerbindung'))" />

		<xsl:variable name="Summe" select="$sumKnoten + $sumKante + $Textfelder" />

		<a name="zusammen">
			<h3>
				<xsl:value-of select="$summary_text" />
			</h3>
		</a>
		<table cellspacing="0" cellpadding="3" border="1">
			<thead>
				<tr>
					<th width="300">
						<xsl:value-of select="$model_text" />
					</th>
					<th>
						<xsl:value-of select="$number_text" />
					</th>
				</tr>
			</thead>
			<tfoot align="right" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$all_text" />
					</td>
					<td>
						<xsl:value-of select="$Summe" />
					</td>
				</tr>
			</tfoot>
			<tbody align="left" valign="top">
				<tr>
					<td>
						<xsl:value-of select="$sumKnot_text" />
					</td>
					<td>
						<xsl:value-of select="$sumKnoten" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$sumKan_text" />
					</td>
					<td>
						<xsl:value-of select="$sumKante" />
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="$sumText_text" />
					</td>
					<td>
						<xsl:value-of select="$Textfelder" />
					</td>
				</tr>
			</tbody>
		</table>

	</xsl:template>

</xsl:stylesheet>