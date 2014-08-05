<?xml version="1.0" encoding="iso-8859-1"?>

<!--name: Physical data processing components: arranged according to component 
	types -->
<!--type: html -->
<!--description: Which physical data processing components belong to which 
	component type? What properties do the physical data processing components 
	have? -->
<!--author: Thomas Rudert -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:str="http://whatever"
	version="2.0" exclude-result-prefixes="str">

	<xsl:variable name="header_text"
		select="'Which physical data processing components belong to which component type? What properties do the physical data processing components have?'" />
	<xsl:variable name="cont_text" select="'Contents'" />
	<xsl:variable name="with_text"
		select="'Properties of physical data processing components with a defined component type'" />
	<xsl:variable name="without_text"
		select="'Properties of physical data processing components without a defined component type'" />
	<xsl:variable name="phys_text" select="'Physical data processing component'" />
	<xsl:variable name="prop_text" select="'Property'" />
	<xsl:variable name="value_text" select="'Value'" />

	<!-- Schluessel zur Auswahl von Elementen -->

	<!-- Kante von Physischen DVBaustein zu Bausteintyp (Parameter BAT) -->
	<xsl:key name="kantePhyBau"
		match="/modell_3lgm_2/objects/element[@class='PdvbBtypVerbindung']"
		use="child::field[@name='end']" />

	<!-- Kante von Physischen DVBaustein zu Bausteintyp (Parameter PHY) -->
	<xsl:key name="kante1PhyBau"
		match="/modell_3lgm_2/objects/element[@class='PdvbBtypVerbindung']"
		use="child::field[@name='start']" />

	<!-- Kante von Physischen DVBaustein zu Standort -->
	<xsl:key name="kantePhySta"
		match="/modell_3lgm_2/objects/element[@class='PdvbStoVerbindung']"
		use="child::field[@name='start']" />

	<!-- Kante von Physischen DVBaustein zu Subnetz -->
	<xsl:key name="kantePhySub"
		match="/modell_3lgm_2/objects/element[@class='PdvbSubnVerbindung']"
		use="child::field[@name='start']" />

	<!-- Elementasuwahl mittels Hashcode -->
	<xsl:key name="hash" match="/modell_3lgm_2/objects/element"
		use="@hash" />

	<!-- Wurzelknoten -->
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="$header_text" />
					>
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
				<h3>
					<xsl:value-of select="$cont_text" />
				</h3>
				<ul>
					<li>
						<a href="#eig">
							<xsl:value-of select="$with_text" />
						</a>
					</li>
					<li>
						<a href="#ohne">
							<xsl:value-of select="$without_text" />
						</a>
					</li>
				</ul>

				<a name="eig">
					<h3>
						<xsl:value-of select="$with_text" />
					</h3>
				</a>
				<xsl:apply-templates
					select="modell_3lgm_2/objects/element[@class='Bausteintyp']">
					<xsl:sort select="child::field[@name='name']" order="ascending"
						data-type="text" />
				</xsl:apply-templates>

				<a name="ohne">
					<h3>
						<xsl:value-of select="$without_text" />
					</h3>
				</a>
				<table border="1" cellspacing="0" cellpadding="3" width="100%">
					<thead>
						<tr>
							<th width="30%">
								<xsl:value-of select="$phys_text" />
							</th>
							<th width="20%">
								<xsl:value-of select="$prop_text" />
							</th>
							<th width="50%">
								<xsl:value-of select="$value_text" />
							</th>
						</tr>
					</thead>
					<tbody valign="top" align="left">
						<xsl:apply-templates
							select="/modell_3lgm_2/objects/element[@class='PhysischerDVBaustein']">
							<xsl:sort select="child::field[@name='name']" order="ascending"
								data-type="text" />
						</xsl:apply-templates>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="/modell_3lgm_2/objects/element[@class='Bausteintyp']">
		<h3>
			<xsl:value-of select="child::field[@name='name']" />
		</h3>
		<xsl:if
			test="(count(key('kantePhyBau', @hash)) + count(key('kantePhyBau', @hash))) &gt; 0">
			<table border="1" cellspacing="0" cellpadding="3" width="100%">
				<thead>
					<tr>
						<th width="30%">
							<xsl:value-of select="$phys_text" />
						</th>
						<th width="20%">
							<xsl:value-of select="$prop_text" />
						</th>
						<th width="50%">
							<xsl:value-of select="$value_text" />
						</th>
					</tr>
				</thead>
				<tbody valign="top" align="left">
					<xsl:call-template name="dvbaustein">
						<xsl:with-param name="bausteintyp" select="@hash" />
					</xsl:call-template>
				</tbody>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="dvbaustein">
		<xsl:param name="bausteintyp" />

		<xsl:for-each select="key('kantePhyBau', $bausteintyp)">
			<xsl:sort
				select="key('hash', child::field[@name='start']/field[@name='name'])"
				order="ascending" data-type="text" />
			<xsl:call-template name="dvbaustein_ausgabe">
				<xsl:with-param name="dvbaustein"
					select="key('hash', child::field[@name='start'])" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="dvbaustein_ausgabe">
		<xsl:param name="dvbaustein" />
		<xsl:variable name="standort"
			select="key('hash', key('kantePhySta', $dvbaustein/@hash)/field[@name='end'])" />
		<xsl:variable name="subnetz"
			select="key('hash', key('kantePhySub', $dvbaustein/@hash)/field[@name='end'])" />
		<tr>
			<td>
				<xsl:call-template name="getRowspan">
					<xsl:with-param name="dvbaustein" select="$dvbaustein" />
				</xsl:call-template>
				<xsl:value-of select="$dvbaustein/field[@name='name']" />
			</td>
			<xsl:if
				test="count($dvbaustein/field[@name!='name' and .!=''and @name!='layer' and @name!='downtime']) = 0 and count($subnetz) = 0 and count($standort) = 0">
				<td>
					<br />
				</td>
				<td>
					<br />
				</td>
			</xsl:if>
			<xsl:apply-templates select="$dvbaustein/field" />
			<xsl:call-template name="eigenschaften">
				<xsl:with-param name="dvbaustein" select="$dvbaustein/@hash" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="field">
		<!-- Downtime und Layer werden nicht angegeben da nicht sinnvoll bzw. nie 
			angegeben -->
		<xsl:if
			test="@name!='name' and .!='' and @name!='layer' and @name!='downtime'">
			<td>
				<xsl:value-of select="@name" />
				:
			</td>
			<td>
				<xsl:value-of select="." />
				<br />
			</td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="eigenschaften">
		<xsl:param name="dvbaustein" />
		<xsl:variable name="standort"
			select="key('hash', key('kantePhySta', $dvbaustein)/field[@name='end'])" />
		<xsl:variable name="subnetz"
			select="key('hash', key('kantePhySub', $dvbaustein)/field[@name='end'])" />
		<xsl:if test="count($standort) &gt; 0">
			<td>Standort:</td>
			<td>
				<xsl:value-of select="$standort/field[@name='name']" />
				<xsl:if test="$standort/field[@name='description'] != ''">
					(
					<xsl:value-of select="$standort/field[@name='description']" />
					)
				</xsl:if>
			</td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:if>
		<xsl:if test="count($subnetz) &gt; 0">
			<td>Subnetz:</td>
			<td>
				<xsl:value-of select="$subnetz/field[@name='name']" />
				<xsl:if test="$subnetz/field[@name='description'] != ''">
					(
					<xsl:value-of select="$subnetz/field[@name='description']" />
					)
				</xsl:if>
			</td>
			<xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getRowspan">
		<xsl:param name="dvbaustein" />
		<xsl:variable name="felder"
			select="count($dvbaustein/field[@name!='name' and .!=''and @name!='layer' and @name!='downtime'])" />
		<xsl:choose>
			<xsl:when test="count(key('kantePhySta', $dvbaustein/@hash)) &gt; 0">
				<xsl:choose>
					<xsl:when test="count(key('kantePhySub', $dvbaustein/@hash)) &gt; 0">
						<xsl:attribute name="rowspan"><xsl:value-of
							select="$felder + 2" /></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="rowspan"><xsl:value-of
							select="$felder + 1" /></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="count(key('kantePhySub', $dvbaustein/@hash)) &gt; 0">
						<xsl:attribute name="rowspan"><xsl:value-of
							select="$felder + 1" /></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$felder &gt; 1">
								<xsl:attribute name="rowspan"><xsl:value-of
									select="$felder" /></xsl:attribute>
							</xsl:when>
							<xsl:otherwise />
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template
		match="/modell_3lgm_2/objects/element[@class='PhysischerDVBaustein']">
		<xsl:if test="count(key('kante1PhyBau', @hash)) = 0">
			<xsl:call-template name="dvbaustein_ausgabe">
				<xsl:with-param name="dvbaustein" select="self::node()" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
