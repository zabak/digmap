<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0" 
 xmlns="http://www.digmap.eu/schemas/resource/"
xmlns:bn="info:lc/xmlns/marcxchange-v1" xmlns:dc="http://purl.org/dc/elements/1.1/">
	<xsl:import href="lib_unimarc2dc.xsl" />	


	<xsl:output method='xml' indent='yes'/>

	<xsl:template match="/">
				<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="bn:record">

		<record xmlns:dc="http://purl.org/dc/elements/1.1/" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:tel="http://krait.kb.nl/coop/tel/handbook/telterms.html"
		xmlns:dcterms="http://purl.org/dc/terms/"
		xmlns:mods="http://www.loc.gov/mods"
		xmlns:lib="http://dublincore.org/usage/meetings/2003/06/dclib-encodingschemes.html"
		xmlns="http://www.digmap.eu/schemas/resource/"
		>



			<!-- dc:Identifier -->
			<xsl:for-each select="//bn:controlfield[@tag='001']">
				<tel:recordId xsi:type='dcterms:URI'><xsl:text>http://catalogo.bn.pt/ipac20/ipac.jsp?profile=bn&amp;source=~!bnp&amp;view=subscriptionsummary&amp;uri=full=3100024~!</xsl:text><xsl:value-of select="."/><xsl:text>~!0&amp;ri=1&amp;aspect=subtab13&amp;menu=search&amp;ipp=20&amp;spp=20&amp;staffonly=&amp;term=teste&amp;index=.GW&amp;uindex=&amp;aspect=subtab13&amp;menu=search&amp;ri=1</xsl:text></tel:recordId>
			</xsl:for-each>						
			<xsl:for-each select="//bn:datafield[@tag='010']/bn:subfield[@code='a']">
				<dc:identifier xsi:type='lib:ISBN'><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>						
			<xsl:for-each select="//bn:datafield[@tag='011' ]/bn:subfield[@code='a']">
				<dc:identifier xsi:type='lib:ISSN'><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="//bn:datafield[@tag='013' ]/bn:subfield[@code='a']">
				<dc:identifier><xsl:text>URN:ISMN:</xsl:text><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>						
			<xsl:for-each select="//bn:datafield[@tag='020']/bn:subfield[@code='a']">
				<dc:identifier><xsl:text>URN:NBN:</xsl:text><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>						
			<xsl:for-each select="//bn:datafield[@tag='071']/bn:subfield[@code='a']">
				<dc:identifier><xsl:text>Nº do Editor - Música (Publishers' Numbers for Music): </xsl:text><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>						
			<xsl:for-each select="//bn:datafield[@tag='255']/bn:subfield[@code='x']">
				<dc:identifier><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="//bn:datafield[@tag='966']">
				<dc:identifier>
				<xsl:text>Cota (Call-Number): </xsl:text>
					<xsl:if test=".//bn:subfield[@code='d']">
						<xsl:value-of select=".//bn:subfield[@code='d']"/>
						<xsl:text> </xsl:text>
					</xsl:if>
					<xsl:value-of select=".//bn:subfield[@code='l']"/><xsl:text>-</xsl:text><xsl:value-of select=".//bn:subfield[@code='s']"/>
				</dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="//bn:datafield[@tag='021']">
				<dc:identifier><xsl:text>URN:LDN:</xsl:text><xsl:value-of select=".//bn:subfield[@code='a']"/><xsl:text>:</xsl:text><xsl:value-of select=".//bn:subfield[@code='b']"/></dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="//bn:datafield[@tag='856']/bn:subfield[@code='u' or @code='g']">
				<dc:identifier xsi:type='dcterms:URI'><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>	


			<xsl:call-template name="unimarc2dc_qualified" />

			<xsl:for-each select="//bn:datafield[@tag='205']">
				<dc:description>
					<xsl:text>Edição: </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='a']"/>
					<xsl:for-each select=".//bn:subfield[@code='b']">
						<xsl:text>; </xsl:text>
						<xsl:value-of select="."/>
					</xsl:for-each>
				</dc:description>
			</xsl:for-each>	




		</record>
				
	</xsl:template>    
</xsl:stylesheet>


