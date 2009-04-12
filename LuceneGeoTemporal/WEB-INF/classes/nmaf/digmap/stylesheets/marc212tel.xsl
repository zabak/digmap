<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  xmlns="http://www.digmap.eu/schemas/resource/"
xmlns:bn="info:lc/xmlns/marcxchange-v1" xmlns:dc="http://purl.org/dc/elements/1.1/">



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
			<!--xsl:for-each select="//bn:controlfield[@tag='001']">
				<tel:recordId xsi:type='dcterms:URI'><xsl:text>http://opac.porbase.org/ipac20/ipac.jsp?profile=porbase&amp;uri=full=3100024@!</xsl:text><xsl:value-of select="."/><xsl:text>@!0&amp;ri=1&amp;aspect=basic_search&amp;menu=search&amp;source=192.168.0.17@!porbase&amp;ipp=20&amp;staffonly=&amp;term=&amp;index=&amp;uindex=&amp;aspect=basic_search&amp;menu=search&amp;ri=1</xsl:text></tel:recordId>
			</xsl:for-each-->					
			<xsl:for-each select="//bn:datafield[@tag='020']/bn:subfield[@code='a']">
				<dc:identifier xsi:type='lib:ISBN'><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>						
			<xsl:for-each select="//bn:datafield[@tag='022' ]/bn:subfield[@code='a']">
				<dc:identifier xsi:type='lib:ISSN'><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="//bn:datafield[@tag='015']/bn:subfield[@code='a']">
				<dc:identifier><xsl:text>URN:NBN:</xsl:text><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>	
			
			<xsl:for-each select="//bn:datafield[@tag='856']/bn:subfield[@code='u' or @code='g']">
				<dc:identifier xsi:type='dcterms:URI'><xsl:value-of select="."/></dc:identifier>
			</xsl:for-each>	
			
			<xsl:call-template name="ibermarc2dc_qualified" />

			<xsl:for-each select="//bn:datafield[@tag='250']">
				<dc:description>
					<xsl:text>Edition: </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='a']"/>
					<xsl:for-each select=".//bn:subfield[@code='b']">
						<xsl:text>; </xsl:text>
						<xsl:value-of select="."/>
					</xsl:for-each>
				</dc:description>
			</xsl:for-each>	


		</record>
				
	</xsl:template>  



<xsl:template name="ibermarc2dc_qualified" xmlns:dcterms="http://purl.org/dc/terms/">

	<!-- dc:Title -->
	<xsl:for-each select="//bn:datafield[@tag='245']">
		<dc:title>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:for-each select=".//bn:subfield[@code='b']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='n']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='p']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
		</dc:title>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='130' or @tag='210' or @tag='222' or @tag='240' or @tag='242' or @tag='246' or @tag='730' or @tag='740']/bn:subfield[@code='a']">
		<dcterms:alternative><xsl:value-of select="."/></dcterms:alternative>
	</xsl:for-each>




	<!-- dc:Creator -->
	<xsl:for-each select="//bn:datafield[@tag='710' or @tag='711' or @tag='720' or @tag='110' or @tag='111']"><!-- @tag='700' or   or @tag='100'  -->
		<dc:creator>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='f']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='f']"/>  
			</xsl:if>
		</dc:creator>
	</xsl:for-each>



	<!-- dc:Subject -->
	<xsl:for-each select="//bn:datafield[@tag='650' or @tag='651']">
		<dc:subject>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>  
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='c']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='x']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='y']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='y']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='z']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='z']"/>  
			</xsl:if>
		</dc:subject>
	</xsl:for-each>

	


	<!-- dc:Description -->
	<xsl:for-each select="//bn:datafield[@tag='245']/bn:subfield[@code='h']">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='245']/bn:subfield[@code='c']">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='500']/bn:subfield[@code='a']">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:for-each>
	<!--xsl:for-each select="//bn:datafield[@tag='500' or @tag='303' or @tag='304' or @tag='305' or @tag='308' or @tag='320' or @tag='326' or @tag='330' or @tag='327' or @tag='328']/bn:subfield[@code='a']">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:for-each-->


	<xsl:for-each select="//bn:datafield[@tag='255']/bn:subfield[@code='a' or @code='b' or @code='c']">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:for-each>


	<!-- dc:Publisher -->
	<xsl:for-each select="//bn:datafield[@tag='260']/bn:subfield[@code='b']">
		<dc:publisher><xsl:value-of select="."/></dc:publisher>
	</xsl:for-each>			
	<xsl:for-each select="//bn:datafield[@tag='260']/bn:subfield[@code='a']">
		<dc:publisher><xsl:value-of select="."/></dc:publisher>
	</xsl:for-each>

	<!-- dc:Contributor -->
	<xsl:for-each select="//bn:datafield[@tag='801' or @tag='810' or @tag='811' or @tag='820' or @tag='821']"> <!--  @tag='800'  -->
		<dc:creator>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='f']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='f']"/>  
			</xsl:if>
		</dc:creator>
	</xsl:for-each>

	<!-- dc:Date -->
	<xsl:for-each select="//bn:datafield[@tag='260']/bn:subfield[@code='c']">
		<dc:date><xsl:value-of select="."/></dc:date>
	</xsl:for-each>						

	<!-- dc:Type -->


	<!-- dc:Format -->	
	
	<!-- dc:source -->
	<xsl:for-each select="//bn:datafield[@tag='561']/bn:subfield[@code='a']">
		<dc:source><xsl:value-of select="."/></dc:source>
	</xsl:for-each>						

	<!-- dc:Relation -->
	<xsl:for-each select="//bn:datafield[@tag='243']">
		<dc:relation>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
		</dc:relation>
	</xsl:for-each>


	<!-- dc:Language -->
	<xsl:for-each select="//bn:datafield[@tag='041']/bn:subfield[@code='a']">
		<dc:language xsi:type="dcterms:ISO639-2"><xsl:value-of select="."/></dc:language>
	</xsl:for-each>						

	<!-- dc:Rights -->
	<!--xsl:for-each select="//bn:datafield[@tag='310']/bn:subfield[@code='a']">
		<dc:rights><xsl:value-of select="."/></dc:rights>
	</xsl:for-each-->


	<!-- dc:coverage -->

</xsl:template>   

</xsl:stylesheet>


