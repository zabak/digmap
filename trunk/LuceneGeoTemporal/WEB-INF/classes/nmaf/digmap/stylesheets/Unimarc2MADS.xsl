<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:mads="http://www.loc.gov/mads/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:marc="info:lc/xmlns/marcxchange-v1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="marc">

	<xsl:include href="MARC21slimUtils.xsl"/>
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<!--
	  removed duplication of 550 $a text               tmee 11/1/2006
	  fixed namespace references between mads and mods ntra 10/6/2006

	  
      revised rsg/jer 11/29/05
      adapted from MARC21Slim2MODS3.xsl ntra 7/6/05
	    -->
	<!-- authority attribute defaults to 'naf' if not set using this authority parameter, for <authority> descriptors: name, titleInfo, geographic -->
	<xsl:param name="authority"/>
	<xsl:variable name="auth">
		<xsl:choose>
			<xsl:when test="$authority">
				<xsl:value-of select="$authority"/>
			</xsl:when>
			<xsl:otherwise>naf</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<!-- xsl:variable name="controlField008-11" select="substring(descendant-or-self::marc:controlfield[@tag=008],12,1)"/>
	<xsl:variable name="controlField008-14" select="substring(descendant-or-self::marc:controlfield[@tag=008],15,1)"/ -->
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="descendant-or-self::marc:collection">
				<mads:madsCollection xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.loc.gov/mads/ http://www.loc.gov/standards/mads/mads.xsd http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-2.xsd"
				                     xmlns:mods="http://www.loc.gov/mods/v3">
					<xsl:for-each select="descendant-or-self::marc:collection/marc:record">
						<mads:mads version="1.0">
							<xsl:call-template name="marcRecord"/>
						</mads:mads>
					</xsl:for-each>
				</mads:madsCollection>
			</xsl:when>
			<xsl:otherwise>
				<mads:mads version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				           xsi:schemaLocation="http://www.loc.gov/mads/ http://www.loc.gov/standards/mads/mads.xsd http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-2.xsd" xmlns:mods="http://www.loc.gov/mods/v3">
					<xsl:for-each select="descendant-or-self::marc:record">
						<xsl:call-template name="marcRecord"/>
					</xsl:for-each>
				</mads:mads>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="marcRecord">	
		<mads:authority>
			<xsl:apply-templates select="marc:datafield[200 &lt;= @tag  and @tag &lt; 300]"/>
		</mads:authority>
		<!-- related --> 
		<xsl:apply-templates select="marc:datafield[500 &lt;= @tag and @tag &lt;= 550]|marc:datafield[600 &lt;= @tag and @tag &lt;= 650]"/>
		<!-- variant -->
		<xsl:apply-templates select="marc:datafield[400 &lt;= @tag and @tag &lt;= 450]"/>
		<!-- notes -->
		<xsl:apply-templates select="marc:datafield[300 &lt;= @tag and @tag &lt;= 330]"/>
		<!-- url -->
		<xsl:apply-templates select="marc:datafield[@tag=856]"/>
		<!--xsl:apply-templates select="marc:datafield[@tag=010]"/-->
		<!--xsl:apply-templates select="marc:datafield[@tag=024]"/-->
		<mads:recordInfo>

			<!-- <xsl:apply-templates select="marc:datafield[@tag=024]"/> -->
			<!--xsl:apply-templates select="marc:datafield[@tag=040]/marc:subfield[@code='a']"/-->
			<!--xsl:apply-templates select="marc:controlfield[@tag=008]"/>
			<xsl:apply-templates select="marc:controlfield[@tag=005]"/-->
			<xsl:apply-templates select="marc:controlfield[@tag=001]"/>
			<!--xsl:apply-templates select="marc:datafield[@tag=040]/marc:subfield[@code='b']"/-->
		</mads:recordInfo>
	</xsl:template>

	<!-- start of secondary templates -->

	<!-- ======== xlink ======== -->

	<!-- <xsl:template name="uri"> 
    <xsl:for-each select="marc:subfield[@code='0']">
      <xsl:attribute name="xlink:href">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:for-each>
     </xsl:template> 
   -->
	<xsl:template match="marc:subfield[@code='i']">
		<xsl:attribute name="type">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="role">
		<xsl:for-each select="marc:subfield[@code='e']">
			<mads:role>
				<mads:roleTerm type="text">
					<xsl:value-of select="."/>
				</mads:roleTerm>
			</mads:role>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="part">
		<xsl:variable name="partNumber">
			<xsl:call-template name="specialSubfieldSelect">
				<xsl:with-param name="axis">n</xsl:with-param>
				<xsl:with-param name="anyCodes">n</xsl:with-param>
				<xsl:with-param name="afterCodes">fghkdlmor</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="partName">
			<xsl:call-template name="specialSubfieldSelect">
				<xsl:with-param name="axis">p</xsl:with-param>
				<xsl:with-param name="anyCodes">p</xsl:with-param>
				<xsl:with-param name="afterCodes">fghkdlmor</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="string-length(normalize-space($partNumber))">
			<mads:partNumber>
				<xsl:call-template name="chopPunctuation">
					<xsl:with-param name="chopString" select="$partNumber"/>
				</xsl:call-template>
			</mads:partNumber>
		</xsl:if>
		<xsl:if test="string-length(normalize-space($partName))">
			<mads:partName>
				<xsl:call-template name="chopPunctuation">
					<xsl:with-param name="chopString" select="$partName"/>
				</xsl:call-template>
			</mads:partName>
		</xsl:if>
	</xsl:template>

	<xsl:template name="nameABCDN">
		<xsl:for-each select="marc:subfield[@code='a']">
			<mads:namePart>
				<xsl:call-template name="chopPunctuation">
					<xsl:with-param name="chopString" select="."/>
				</xsl:call-template>
			</mads:namePart>
		</xsl:for-each>
		<xsl:for-each select="marc:subfield[@code='b']">
			<mads:namePart>
				<xsl:value-of select="."/>
			</mads:namePart>
		</xsl:for-each>
		<xsl:if test="marc:subfield[@code='c'] or marc:subfield[@code='d'] or marc:subfield[@code='n']">
			<mads:namePart>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">cdef</xsl:with-param>
				</xsl:call-template>
			</mads:namePart>
		</xsl:if>
	</xsl:template>

	<xsl:template name="nameABCDQ">
		<mads:namePart>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:call-template name="subfieldSelect">
						<xsl:with-param name="codes">ab</xsl:with-param>
						<xsl:with-param name="delimeter">, </xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</mads:namePart>
		<!-- xsl:for-each select="marc:subfield[@code='a']">
			<mads:namePart>
				<xsl:call-template name="chopPunctuation">
					<xsl:with-param name="chopString" select="."/>
				</xsl:call-template>
			</mads:namePart>
		</xsl:for-each>
		<xsl:for-each select="marc:subfield[@code='b']">
			<mads:namePart>
				<xsl:value-of select="."/>
			</mads:namePart>
		</xsl:for-each -->
		<xsl:call-template name="termsOfAddress"/>
		<xsl:call-template name="nameDate"/>
	</xsl:template>

	<xsl:template name="nameACDENQ">
		<mads:namePart>
			<xsl:call-template name="subfieldSelect">
				<xsl:with-param name="codes">acdenq</xsl:with-param>
			</xsl:call-template>
		</mads:namePart>
	</xsl:template>

	<xsl:template name="nameDate">
		<xsl:for-each select="marc:subfield[@code='d']">
			<mads:namePart type="date">
				<xsl:call-template name="chopPunctuation">
					<xsl:with-param name="chopString" select="."/>
				</xsl:call-template>
			</mads:namePart>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="specialSubfieldSelect">
		<xsl:param name="anyCodes"/>
		<xsl:param name="axis"/>
		<xsl:param name="beforeCodes"/>
		<xsl:param name="afterCodes"/>
		<xsl:variable name="str">
			<xsl:for-each select="marc:subfield">
				<xsl:if test="contains($anyCodes, @code)      or (contains($beforeCodes,@code) and following-sibling::marc:subfield[@code=$axis])      or (contains($afterCodes,@code) and preceding-sibling::marc:subfield[@code=$axis])">
					<xsl:value-of select="text()"/>
					<xsl:text> </xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="substring($str,1,string-length($str)-1)"/>
	</xsl:template>

	<xsl:template name="termsOfAddress">
		<xsl:if test="marc:subfield[@code='d' or @code='c']">
			<mads:namePart type="termsOfAddress">
				<xsl:call-template name="chopPunctuation">
					<xsl:with-param name="chopString">
						<xsl:call-template name="subfieldSelect">
							<xsl:with-param name="codes">cd</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</mads:namePart>
		</xsl:if>
	</xsl:template>

	<xsl:template name="displayLabel">
		<xsl:if test="marc:subfield[@code='z']">
			<xsl:attribute name="displayLabel">
				<xsl:value-of select="marc:subfield[@code='z']"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="marc:subfield[@code='3']">
			<xsl:attribute name="displayLabel">
				<xsl:value-of select="marc:subfield[@code='3']"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="isInvalid">
		<xsl:if test="@code='z'">
			<xsl:attribute name="invalid">yes</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="sub2Attribute">
		<!-- 024 -->
		<xsl:if test="../marc:subfield[@code='2']">
			<xsl:attribute name="type">
				<xsl:value-of select="../marc:subfield[@code='2']"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template match="marc:controlfield[@tag=001]">
		<mods:recordIdentifier>
			<xsl:if test="../marc:controlfield[@tag=003]">
				<xsl:attribute name="source">
					<xsl:value-of select="../marc:controlfield[@tag=003]"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</mods:recordIdentifier>
	</xsl:template>

	<xsl:template match="marc:controlfield[@tag=005]">
		<mods:recordChangeDate encoding="iso8601">
			<xsl:value-of select="."/>
		</mods:recordChangeDate>
	</xsl:template>

	<xsl:template match="marc:controlfield[@tag=100]/marc:subfield[@code='a']">
		<mods:recordCreationDate encoding="marc">
			<xsl:value-of select="substring(.,1,6)"/>
		</mods:recordCreationDate>
	</xsl:template>

	<!--xsl:template match="marc:datafield[@tag=010]">
		<xsl:for-each select="marc:subfield">
			<mads:identifier type="lccn">
				<xsl:call-template name="isInvalid"/>
				<xsl:value-of select="."/>
			</mads:identifier>
		</xsl:for-each>
	</xsl:template-->

	<!--xsl:template match="marc:datafield[@tag=024]">
		<xsl:for-each select="marc:subfield[@code='a')]">
			<mads:identifier>
				<xsl:call-template name="isInvalid"/>
				<xsl:call-template name="sub2Attribute"/>
				<xsl:value-of select="."/>
			</mads:identifier>
		</xsl:for-each>
	</xsl:template-->

	<!--xsl:template match="marc:datafield[@tag=040]/marc:subfield[@code='a']">
		<mods:recordContentSource authority="marcorg">
			<xsl:value-of select="."/>
		</mods:recordContentSource>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=040]/marc:subfield[@code='b']">
		<mods:languageOfCataloging>
			<mods:languageTerm authority="iso639-2b" type="code">
				<xsl:value-of select="."/>
			</mods:languageTerm>
		</mods:languageOfCataloging>
	</xsl:template-->

	<!-- ========== names  ========== -->
	<xsl:template match="marc:datafield[@tag=200]">
		<mads:name type="personal">
			<xsl:call-template name="setAuthority"/>
			<xsl:call-template name="nameABCDQ"/>
			<!--xsl:call-template name="role"/-->
		</mads:name>
		<!--xsl:apply-templates select="*[marc:subfield[not(contains('abcdeq',@code))]]"/-->
		<!--xsl:call-template name="title"/-->
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=210 and @ind1='0']">
		<mads:name type="corporate">
			<xsl:call-template name="setAuthority"/>
			<xsl:call-template name="nameABCDN"/>
			<!--xsl:call-template name="role"/-->
		</mads:name>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=210 and @ind1='1']">
		<mads:name type="conference">
			<xsl:call-template name="setAuthority"/>
			<xsl:call-template name="nameABCDN"/>
			<!--xsl:call-template name="nameACDENQ"/-->
		</mads:name>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=400]">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<mads:name type="personal">
				<xsl:call-template name="nameABCDQ"/>
				<!--xsl:call-template name="role"/-->
			</mads:name>
			<xsl:apply-templates/>
			<!--xsl:call-template name="title"/-->
		</mads:variant>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=410 and @ind1='0']">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<mads:name type="corporate">
				<xsl:call-template name="nameABCDN"/>
				<!--xsl:call-template name="role"/-->
			</mads:name>
			<xsl:apply-templates/>
		</mads:variant>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=410 and @ind1='1' ]">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<mads:name type="conference">
				<xsl:call-template name="nameABCDN"/>
				<!--xsl:call-template name="nameACDENQ"/-->
			</mads:name>
			<xsl:apply-templates/>
		</mads:variant>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=500]|marc:datafield[@tag=600]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<!-- <xsl:call-template name="uri"/> -->
			<mads:name type="personal">
				<xsl:call-template name="setAuthority"/>
				<xsl:call-template name="nameABCDQ"/>
				<!--xsl:call-template name="role"/-->
			</mads:name>
			<!--xsl:call-template name="title"/-->
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=510 and @ind1='0']|marc:datafield[@tag=610 and @ind1='0']">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<!-- <xsl:call-template name="uri"/> -->
			<mads:name type="corporate">
				<xsl:call-template name="setAuthority"/>
				<xsl:call-template name="nameABCDN"/>
				<!--xsl:call-template name="role"/-->
			</mads:name>
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=510 and @ind1='1']|marc:datafield[@tag=610 and @ind1='1']">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<!-- <xsl:call-template name="uri"/> -->
			<mads:name type="conference">
				<xsl:call-template name="setAuthority"/>
				<xsl:call-template name="nameABCDN"/>
				<!--xsl:call-template name="nameACDENQ"/-->
			</mads:name>
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template>

	<!-- ========= geographic  ========== -->
	<xsl:template match="marc:subfield[@code='y']">
		<mads:geographic>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:value-of select="."/>
				</xsl:with-param>
			</xsl:call-template>
		</mads:geographic>
	</xsl:template>
	<xsl:template name="geographic">
		<mads:geographic>
			<xsl:if test="@tag=515">
				<xsl:value-of select="marc:subfield[@code='a']"/>
			</xsl:if>
			<xsl:call-template name="setAuthority"/>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<!--xsl:choose>
						<xsl:when test="@tag=181 or @tag=481 or @tag=581">
							<xsl:apply-templates select="marc:subfield[@code='z']"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="marc:subfield[@code='a']"/>
						</xsl:otherwise>
					</xsl:choose-->
					<xsl:value-of select="marc:subfield[@code='a']"/>
				</xsl:with-param>
			</xsl:call-template>
		</mads:geographic>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=115][marc:subfield[@code='a']]">
		<xsl:call-template name="geographic"/>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=415][marc:subfield[@code='a']]">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<xsl:call-template name="geographic"/>
		</mads:variant>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=515]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<!-- <xsl:call-template name="uri"/> -->
			<xsl:call-template name="geographic"/>
		</mads:related>
	</xsl:template>
	<!--xsl:template match="marc:datafield[@tag=580]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template-->
	<xsl:template match="marc:datafield[@tag=615][marc:subfield[@code='y']]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<xsl:call-template name="geographic"/>
		</mads:related>
	</xsl:template>
	<!--xsl:template match="marc:datafield[@tag=755]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<xsl:call-template name="genre"/>
			<xsl:call-template name="setAuthority"/>
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=780]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=785]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template-->
	<!-- ========== notes  ========== -->
	<xsl:template match="marc:datafield[667 &lt;= @tag and @tag &lt;= 688]">
		<mads:note>
			<xsl:choose>
				<xsl:when test="@tag=667">
					<xsl:attribute name="type">nonpublic</xsl:attribute>
				</xsl:when>
				<xsl:when test="@tag=670">
					<xsl:attribute name="type">source</xsl:attribute>
				</xsl:when>
				<xsl:when test="@tag=675">
					<xsl:attribute name="type">notFound</xsl:attribute>
				</xsl:when>
				<xsl:when test="@tag=678">
					<xsl:attribute name="type">history</xsl:attribute>
				</xsl:when>
				<xsl:when test="@tag=681">
					<xsl:attribute name="type">subject example</xsl:attribute>
				</xsl:when>
				<xsl:when test="@tag=682">
					<xsl:attribute name="type">deleted heading information</xsl:attribute>
				</xsl:when>
				<xsl:when test="@tag=688">
					<xsl:attribute name="type">application history</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:choose>
						<xsl:when test="@tag=667 or @tag=675">
							<xsl:value-of select="marc:subfield[@code='a']"/>
						</xsl:when>
						<xsl:when test="@tag=670 or @tag=678">
							<xsl:call-template name="subfieldSelect">
								<xsl:with-param name="codes">ab</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="680 &lt;= @tag and @tag &lt;=688">
							<xsl:call-template name="subfieldSelect">
								<xsl:with-param name="codes">ai</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</mads:note>
	</xsl:template>
	<!-- ========== url  ========== -->
	<xsl:template match="marc:datafield[@tag=856][marc:subfield[@code='u']]">
		<mads:url>
			<xsl:if test="marc:subfield[@code='z' or @code='3']">
				<xsl:attribute name="displayLabel">
					<xsl:call-template name="subfieldSelect">
						<xsl:with-param name="codes">z3</xsl:with-param>
					</xsl:call-template>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="marc:subfield[@code='u']"/>
		</mads:url>
	</xsl:template>
	<xsl:template name="relatedTypeAttribute">
		<xsl:choose>
			<xsl:when test="@tag=500 or @tag=510 or @tag=515 or @tag=520 or @tag=540 or @tag=545 or @tag=550">
				<xsl:if test="substring(marc:subfield[@code='5'],1,1)='a'">
					<xsl:attribute name="type">earlier</xsl:attribute>
				</xsl:if>
				<xsl:if test="substring(marc:subfield[@code='5'],1,1)='b'">
					<xsl:attribute name="type">later</xsl:attribute>
				</xsl:if>
				<!--xsl:if test="substring(marc:subfield[@code='5'],1,1)='t'">
					<xsl:attribute name="type">parentOrg</xsl:attribute>
				</xsl:if-->
				<xsl:if test="substring(marc:subfield[@code='5'],1,1)='g'">
					<xsl:attribute name="type">broader</xsl:attribute>
				</xsl:if>
				<xsl:if test="substring(marc:subfield[@code='5'],1,1)='h'">
					<xsl:attribute name="type">narrower</xsl:attribute>
				</xsl:if>
				<xsl:if test="contains('fin|', substring(marc:subfield[@code='5'],1,1))">
					<xsl:attribute name="type">other</xsl:attribute>
				</xsl:if>
			</xsl:when>
			<xsl:when test="@tag=530 or @tag=630">
				<xsl:attribute name="type">other</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<!-- 6xx -->
				<xsl:attribute name="type">equivalent</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		<!--xsl:apply-templates select="marc:subfield[@code='i']"/-->
	</xsl:template>
	<xsl:template name="variantTypeAttribute">
		<xsl:choose>
			<xsl:when test="@tag=400 or @tag=410 or @tag=415 or @tag=420 or @tag=440 or @tag=445 or @tag=450">
				<xsl:if test="substring(marc:subfield[@code='5'],1,1)='d'">
					<xsl:attribute name="type">acronym</xsl:attribute>
				</xsl:if>
				<xsl:if test="contains('fit', substring(marc:subfield[@code='5'],1,1))">
					<xsl:attribute name="type">other</xsl:attribute>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<!-- 430  -->
				<xsl:attribute name="type">other</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		<!--xsl:apply-templates select="marc:subfield[@code='i']"/-->
	</xsl:template>
	<xsl:template name="setAuthority">
		
	</xsl:template>
	<xsl:template match="*"/>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="yes" url="..\..\..\..\..\..\..\..\..\..\temp\loischana3.xml" htmlbaseurl="" outputurl="" processortype="internal" useresolver="no" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
-->