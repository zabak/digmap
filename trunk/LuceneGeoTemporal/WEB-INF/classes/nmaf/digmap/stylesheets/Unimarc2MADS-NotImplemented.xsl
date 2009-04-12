	<!-- ========== titles  ========== -->
	<xsl:template match="marc:datafield[@tag=130]">
		<xsl:call-template name="uniform-title"/>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=430]">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<xsl:call-template name="uniform-title"/>
			<xsl:apply-templates/>
		</mads:variant>
	</xsl:template>

	<xsl:template match="marc:datafield[@tag=530]|marc:datafield[@tag=730]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<xsl:call-template name="uniform-title"/>
			<xsl:apply-templates/>
		</mads:related>
	</xsl:template>

	<xsl:template name="title">
		<xsl:variable name="hasTitle">
			<xsl:for-each select="marc:subfield">
				<xsl:if test="(contains('tfghklmors',@code) )">
					<xsl:value-of select="@code"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($hasTitle) &gt; 0 ">

			<mads:titleInfo>
				<xsl:call-template name="setAuthority"/>
				<mods:title>
					<xsl:variable name="str">
						<xsl:for-each select="marc:subfield">
							<xsl:if test="(contains('tfghklmors',@code) )">
								<xsl:value-of select="text()"/>
								<xsl:text> </xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>

					<xsl:call-template name="chopPunctuation">
						<xsl:with-param name="chopString">
							<xsl:value-of select="substring($str,1,string-length($str)-1)"/>
						</xsl:with-param>
					</xsl:call-template>
				</mods:title>
				<xsl:call-template name="part"/>
				<!-- <xsl:call-template name="uri"/> -->
			</mads:titleInfo>
		</xsl:if>
	</xsl:template>

	<xsl:template name="uniform-title">
		<xsl:variable name="hasTitle">
			<xsl:for-each select="marc:subfield">
				<xsl:if test="(contains('tfghklmors',@code) )">
					<xsl:value-of select="@code"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($hasTitle) &gt; 0 ">

			<mads:titleInfo>
				<xsl:call-template name="setAuthority"/>
				<mods:title>
					<xsl:variable name="str">
						<xsl:for-each select="marc:subfield">
							<xsl:if test="(contains('adfghklmors',@code) )">
								<xsl:value-of select="text()"/>
								<xsl:text> </xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>

					<xsl:call-template name="chopPunctuation">
						<xsl:with-param name="chopString">
							<xsl:value-of select="substring($str,1,string-length($str)-1)"/>
						</xsl:with-param>
					</xsl:call-template>
				</mods:title>
				<xsl:call-template name="part"/>
				<!-- <xsl:call-template name="uri"/> -->
			</mads:titleInfo>
		</xsl:if>
	</xsl:template>


	<!-- ========== topics  ========== -->
	<xsl:template match="marc:subfield[@code='x']">
		<mads:topic>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:value-of select="."/>
				</xsl:with-param>
			</xsl:call-template>
		</mads:topic>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=150][marc:subfield[@code='a' or @code='b']]|marc:datafield[@tag=180][marc:subfield[@code='x']]">
		<xsl:call-template name="topic"/>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=450][marc:subfield[@code='a' or @code='b']]|marc:datafield[@tag=480][marc:subfield[@code='x']]">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<xsl:call-template name="topic"/>
		</mads:variant>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=550 or @tag=750][marc:subfield[@code='a' or @code='b']]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<!-- <xsl:call-template name="uri"/> -->
			<xsl:call-template name="topic"/>
		</mads:related>
	</xsl:template>
	<xsl:template name="topic">
		<mads:topic>
			<xsl:call-template name="setAuthority"/>
			<!-- tmee2006 dedupe 550a
			<xsl:if test="@tag=550 or @tag=750">
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">ab</xsl:with-param>
				</xsl:call-template>
			</xsl:if>	
			-->
			<xsl:choose>
				<xsl:when test="@tag=180 or @tag=480 or @tag=580 or @tag=780">
					<xsl:call-template name="chopPunctuation">
						<xsl:with-param name="chopString">
							<xsl:apply-templates select="marc:subfield[@code='x']"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:choose>
						<xsl:when test="@tag=180 or @tag=480 or @tag=580 or @tag=780">
							<xsl:apply-templates select="marc:subfield[@code='x']"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="subfieldSelect">
								<xsl:with-param name="codes">ab</xsl:with-param>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				</xsl:call-template> 	
		</mads:topic>
	</xsl:template>
	<!-- ========= temporals  ========== -->
	<xsl:template match="marc:subfield[@code='y']">
		<mads:temporal>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:value-of select="."/>
				</xsl:with-param>
			</xsl:call-template>
		</mads:temporal>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=148][marc:subfield[@code='a']]|marc:datafield[@tag=182 ][marc:subfield[@code='y']]">
		<xsl:call-template name="temporal"/>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=448][marc:subfield[@code='a']]|marc:datafield[@tag=482][marc:subfield[@code='y']]">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<xsl:call-template name="temporal"/>
		</mads:variant>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=548 or @tag=748][marc:subfield[@code='a']]|marc:datafield[@tag=582 or @tag=782][marc:subfield[@code='y']]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<!-- <xsl:call-template name="uri"/> -->
			<xsl:call-template name="temporal"/>
		</mads:related>
	</xsl:template>
	<xsl:template name="temporal">
		<mads:temporal>
		<xsl:call-template name="setAuthority"/>
			<xsl:if test="@tag=548 or @tag=748">
				<xsl:value-of select="marc:subfield[@code='a']"/>
			</xsl:if>			
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:choose>
						<xsl:when test="@tag=182 or @tag=482 or @tag=582 or @tag=782">
							<xsl:apply-templates select="marc:subfield[@code='y']"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="marc:subfield[@code='a']"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</mads:temporal>
		<xsl:apply-templates/>
	</xsl:template>
	<!-- ========== genre  ========== -->
	<xsl:template match="marc:subfield[@code='v']">
		<mads:genre>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:value-of select="."/>
				</xsl:with-param>
			</xsl:call-template>
		</mads:genre>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=155][marc:subfield[@code='a']]|marc:datafield[@tag=185][marc:subfield[@code='v']]">
		<xsl:call-template name="genre"/>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=455][marc:subfield[@code='a']]|marc:datafield[@tag=485 ][marc:subfield[@code='v']]">
		<mads:variant>
			<xsl:call-template name="variantTypeAttribute"/>
			<xsl:call-template name="genre"/>
		</mads:variant>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=555]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<!-- <xsl:call-template name="uri"/> -->
			<xsl:call-template name="genre"/>
		</mads:related>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag=555 or @tag=755][marc:subfield[@code='a']]|marc:datafield[@tag=585][marc:subfield[@code='v']]">
		<mads:related>
			<xsl:call-template name="relatedTypeAttribute"/>
			<xsl:call-template name="genre"/>
		</mads:related>
	</xsl:template>
	<xsl:template name="genre">
		<mads:genre>
			<xsl:if test="@tag=555">
				<xsl:value-of select="marc:subfield[@code='a']"/>
			</xsl:if>
			<xsl:call-template name="setAuthority"/>
			<xsl:call-template name="chopPunctuation">
				<xsl:with-param name="chopString">
					<xsl:choose>
						<xsl:when test="@tag=185 or @tag=485 or @tag=585">
							<xsl:apply-templates select="marc:subfield[@code='v']"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="marc:subfield[@code='a']"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</mads:genre>
		<xsl:apply-templates/>
	</xsl:template>



















		<xsl:template name="setAuthority">
		<!-- xsl:choose>
			< !-- can be called from the datafield or subfield level, so "..//@tag" means
	   the tag can be at the subfield's parent level or at the datafields own level -- >
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=100 and (@ind1=0 or @ind1=1) and $controlField008-11='a' and $controlField008-14='a'">
				<xsl:attribute name="authority">
					<xsl:text>naf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=100 and (@ind1=0 or @ind1=1) and $controlField008-11='a' and $controlField008-14='b'">
				<xsl:attribute name="authority">
					<xsl:text>lcsh</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=100 and (@ind1=0 or @ind1=1) and $controlField008-11='k'">
				<xsl:attribute name="authority">
					<xsl:text>lacnaf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=100 and @ind1=3 and $controlField008-11='a' and $controlField008-14='b'">
				<xsl:attribute name="authority">
					<xsl:text>lcsh</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=100 and @ind1=3 and $controlField008-11='k' and $controlField008-14='b'">
				<xsl:attribute name="authority">cash</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=110 and $controlField008-11='a' and $controlField008-14='a'">
				<xsl:attribute name="authority">naf</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=110 and $controlField008-11='a' and $controlField008-14='b'">
				<xsl:attribute name="authority">lcsh</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=110 and $controlField008-11='k' and $controlField008-14='a'">
				<xsl:attribute name="authority">
					<xsl:text>lacnaf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=110 and $controlField008-11='k' and $controlField008-14='b'">
				<xsl:attribute name="authority">
					<xsl:text>cash</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="100 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 155 and $controlField008-11='b'">
				<xsl:attribute name="authority">
					<xsl:text>lcshcl</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=100 or ancestor-or-self::marc:datafield/@tag=110 or ancestor-or-self::marc:datafield/@tag=111 or ancestor-or-self::marc:datafield/@tag=130 or ancestor-or-self::marc:datafield/@tag=151) and $controlField008-11='c'">
				<xsl:attribute name="authority">
					<xsl:text>nlmnaf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=100 or ancestor-or-self::marc:datafield/@tag=110 or ancestor-or-self::marc:datafield/@tag=111 or ancestor-or-self::marc:datafield/@tag=130 or ancestor-or-self::marc:datafield/@tag=151) and $controlField008-11='d'">
				<xsl:attribute name="authority">
					<xsl:text>nalnaf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="100 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 155 and $controlField008-11='r'">
				<xsl:attribute name="authority">
					<xsl:text>aat</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="100 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 155 and $controlField008-11='s'">
				<xsl:attribute name="authority">sears</xsl:attribute>
			</xsl:when>
			<xsl:when test="100 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 155 and $controlField008-11='v'">
				<xsl:attribute name="authority">rvm</xsl:attribute>
			</xsl:when>
			<xsl:when test="100 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 155 and $controlField008-11='z'">
				<xsl:attribute name="authority">
					<xsl:value-of select="../marc:datafield[ancestor-or-self::marc:datafield/@tag=040]/marc:subfield[@code='f']"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=111 or ancestor-or-self::marc:datafield/@tag=130) and $controlField008-11='a' and $controlField008-14='a'">
				<xsl:attribute name="authority">
					<xsl:text>naf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=111 or ancestor-or-self::marc:datafield/@tag=130) and $controlField008-11='a' and $controlField008-14='b'">
				<xsl:attribute name="authority">
					<xsl:text>lcsh</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=111 or ancestor-or-self::marc:datafield/@tag=130) and $controlField008-11='k' ">
				<xsl:attribute name="authority">
					<xsl:text>lacnaf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=148 or ancestor-or-self::marc:datafield/@tag=150  or ancestor-or-self::marc:datafield/@tag=155) and $controlField008-11='a' ">
				<xsl:attribute name="authority">
					<xsl:text>lcsh</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=148 or ancestor-or-self::marc:datafield/@tag=150  or ancestor-or-self::marc:datafield/@tag=155) and $controlField008-11='a' ">
				<xsl:attribute name="authority">
					<xsl:text>lcsh</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=148 or ancestor-or-self::marc:datafield/@tag=150  or ancestor-or-self::marc:datafield/@tag=155) and $controlField008-11='c' ">
				<xsl:attribute name="authority">
					<xsl:text>mesh</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=148 or ancestor-or-self::marc:datafield/@tag=150  or ancestor-or-self::marc:datafield/@tag=155) and $controlField008-11='d' ">
				<xsl:attribute name="authority">
					<xsl:text>nal</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=148 or ancestor-or-self::marc:datafield/@tag=150  or ancestor-or-self::marc:datafield/@tag=155) and $controlField008-11='k' ">
				<xsl:attribute name="authority">
					<xsl:text>cash</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=151 and $controlField008-11='a' and $controlField008-14='a'">
				<xsl:attribute name="authority">
					<xsl:text>naf</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=151 and $controlField008-11='a' and $controlField008-14='b'">
				<xsl:attribute name="authority">lcsh</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=151 and $controlField008-11='k' and $controlField008-14='a'">
				<xsl:attribute name="authority">lacnaf</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=151 and $controlField008-11='k' and $controlField008-14='b'">
				<xsl:attribute name="authority">cash</xsl:attribute>
			</xsl:when>

			<xsl:when test="(..//ancestor-or-self::marc:datafield/@tag=180 or ..//ancestor-or-self::marc:datafield/@tag=181 or ..//ancestor-or-self::marc:datafield/@tag=182 or ..//ancestor-or-self::marc:datafield/@tag=185) and $controlField008-11='a'">
				<xsl:attribute name="authority">lcsh</xsl:attribute>
			</xsl:when>

			<xsl:when test="ancestor-or-self::marc:datafield/@tag=700 and (@ind1='0' or @ind1='1') and @ind2='0'">
				<xsl:attribute name="authority">naf</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=700 and (@ind1='0' or @ind1='1') and @ind2='5'">
				<xsl:attribute name="authority">lacnaf</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=700 and @ind1='3' and @ind2='0'">
				<xsl:attribute name="authority">lcsh</xsl:attribute>
			</xsl:when>
			<xsl:when test="ancestor-or-self::marc:datafield/@tag=700 and @ind1='3' and @ind2='5'">
				<xsl:attribute name="authority">cash</xsl:attribute>
			</xsl:when>
			<xsl:when test="(700 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 755 ) and @ind2='1'">
				<xsl:attribute name="authority">lcshcl</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=700 or ancestor-or-self::marc:datafield/@tag=710 or ancestor-or-self::marc:datafield/@tag=711 or ancestor-or-self::marc:datafield/@tag=730 or ancestor-or-self::marc:datafield/@tag=751)  and @ind2='2'">
				<xsl:attribute name="authority">nlmnaf</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=700 or ancestor-or-self::marc:datafield/@tag=710 or ancestor-or-self::marc:datafield/@tag=711 or ancestor-or-self::marc:datafield/@tag=730 or ancestor-or-self::marc:datafield/@tag=751)  and @ind2='3'">
				<xsl:attribute name="authority">nalnaf</xsl:attribute>
			</xsl:when>
			<xsl:when test="(700 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 755 ) and @ind2='6'">
				<xsl:attribute name="authority">rvm</xsl:attribute>
			</xsl:when>
			<xsl:when test="(700 &lt;= ancestor-or-self::marc:datafield/@tag and ancestor-or-self::marc:datafield/@tag &lt;= 755 ) and @ind2='7'">
				<xsl:attribute name="authority">
					<xsl:value-of select="marc:subfield[@code='2']"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=710 or ancestor-or-self::marc:datafield/@tag=711 or ancestor-or-self::marc:datafield/@tag=730 or ancestor-or-self::marc:datafield/@tag=751)  and @ind2='5'">
				<xsl:attribute name="authority">lacnaf</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=710 or ancestor-or-self::marc:datafield/@tag=711 or ancestor-or-self::marc:datafield/@tag=730 or ancestor-or-self::marc:datafield/@tag=751)  and @ind2='0'">
				<xsl:attribute name="authority">naf</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=748 or ancestor-or-self::marc:datafield/@tag=750 or ancestor-or-self::marc:datafield/@tag=755)  and @ind2='0'">
				<xsl:attribute name="authority">lcsh</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=748 or ancestor-or-self::marc:datafield/@tag=750 or ancestor-or-self::marc:datafield/@tag=755)  and @ind2='2'">
				<xsl:attribute name="authority">mesh</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=748 or ancestor-or-self::marc:datafield/@tag=750 or ancestor-or-self::marc:datafield/@tag=755)  and @ind2='3'">
				<xsl:attribute name="authority">nal</xsl:attribute>
			</xsl:when>
			<xsl:when test="(ancestor-or-self::marc:datafield/@tag=748 or ancestor-or-self::marc:datafield/@tag=750 or ancestor-or-self::marc:datafield/@tag=755)  and @ind2='5'">
				<xsl:attribute name="authority">cash</xsl:attribute>
			</xsl:when>
		</xsl:choose -->
	</xsl:template>