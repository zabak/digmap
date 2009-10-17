<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:frbr="http://purl.org/vocab/frbr/core#" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" exclude-result-prefixes="rdf xsl fn">

	<xsl:output method="html" encoding="UTF-8" indent="yes"/>

	<xsl:template match="response">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>FRBR Result</title>
		<link rel="stylesheet" type="text/css" href="css/navigation.css"/>
		<link rel="stylesheet" type="text/css" href="css/page_layout.css"/>
		<link rel="stylesheet" type="text/css" href="css/styles.css"/>
		<link rel="stylesheet" type="text/css" href="css/mitra.css"/>
		<link rel="stylesheet" type="text/css" href="css/stylesGoogle.css"/>
		
		<script type="text/javascript">
                    function toggle(idElement, idText, img, showMessage, hideMessage, anchor){
			var elementToToggle = document.getElementById(idElement);
                        var text2Show = document.getElementById(idText);
			var image2Show = document.getElementById(img);

                        if(elementToToggle != null) {
                            if(elementToToggle.style.display == 'block') {
                                elementToToggle.style.display = 'none';
				image2Show.style.backgroundPosition = '-91px -74px';
                                text2Show.innerHTML = showMessage;
                            }
                            else {
                                elementToToggle.style.display = 'block';
                                text2Show.innerHTML = hideMessage;
				image2Show.style.backgroundPosition = '-105px -74px';
                                window.location.href=anchor;
                            }
                        }
                    }

                </script>

	</head>
	<body class="twoColElsLtHdr">
                <div id="container" style="left: 0px;">
		<!--Header-->
                <xsl:call-template name="header"/>

		<!--Number of results collection and type-->
                <xsl:call-template name="resultsBarTop"/>
		
		<div id="res">
			<xsl:apply-templates select="node"/>
		</div>

		<!--footer-->
                <xsl:call-template name="footer"/>
		</div>
	</body>
</html>
	</xsl:template> 

	<xsl:template match="node">
		<ol id="rso">
			<li class="g w0">
				<h3 class="r">
					<em><xsl:call-template name="getTitle"/></em>
				</h3>
				<div class="s">
					<xsl:call-template name="getAuthors"/>
				</div>
				<xsl:call-template name="manifestation"/>
			</li>
		</ol>
	</xsl:template>

	
	<xsl:template name="manifestation">
		<ol id="rso">
			<xsl:variable name="total"><xsl:value-of select="count(record)"/></xsl:variable>
			<xsl:choose>
				<xsl:when test="count(record) &lt; 3">
					<xsl:for-each select="record">
						<li class="g w0" style="margin-left: 3em;">
							<xsl:element name="a">
								<xsl:attribute name="class">l</xsl:attribute>
								<xsl:attribute name="href">http://digmap2.ist.utl.pt:8080/records_frbr/<xsl:value-of select="@catalog"/>/nobel/<xsl:value-of select="@id"/>.xml</xsl:attribute>
								<xsl:attribute name="title">http://digmap2.ist.utl.pt:8080/records_frbr/<xsl:value-of select="@catalog"/>/nobel/<xsl:value-of select="@id"/>.xml</xsl:attribute>
								<em><xsl:call-template name="getAllTitles"/></em>
							</xsl:element>
							<div class="s">
								<xsl:call-template name="getAuthors"/>
								<xsl:if test="count(info/author) &gt; 0"><br/></xsl:if>
								<!--<cite>http://digmap2.ist.utl.pt:8080/records_frbr/<xsl:value-of select="@catalog"/>/nobel/<xsl:value-of select="@id"/>.xml</cite>
								<cite>See detailed record</cite>-->
								<cite>Catalog: "<xsl:value-of select="@catalog"/>", ID: "<xsl:value-of select="@id"/>"</cite>
							</div>
						</li>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<div class="mbl" style="margin-left: 3em;">
						<div class="bl">
							<span id="mbl2" class="ch">
								<xsl:attribute name="onclick">javascript:toggle('compactDiv_<xsl:value-of select="record[position()=1]/@id"/>', 'idText_<xsl:value-of select="record[position()=1]/@id"/>', 'img_<xsl:value-of select="record[position()=1]/@id"/>', 'Show <xsl:value-of select="$total"/><xsl:choose><xsl:when test="$total = 1"> result</xsl:when><xsl:otherwise> results</xsl:otherwise></xsl:choose>', 'Hide <xsl:value-of select="$total"/><xsl:choose><xsl:when test="$total = 1"> result</xsl:when><xsl:otherwise> results</xsl:otherwise></xsl:choose>', '#img_<xsl:value-of select="../record[position()=1]/@id"/>');</xsl:attribute>
								<table class="ts ti">
									<tbody>
										<tr>
											<td>
												<div class="csb mbi" title="" style="background-position: -91px -74px;">
													<xsl:attribute name="id">img_<xsl:value-of select="record[position()=1]/@id"/></xsl:attribute>
												</div>
											</td>
										</tr>
									</tbody>
								</table>
								<a class="mblink" href="#">
									<xsl:attribute name="onclick">return false;</xsl:attribute>
									<span>
										<xsl:attribute name="id">idText_<xsl:value-of select="record[position()=1]/@id"/></xsl:attribute>Show <xsl:value-of select="$total"/><xsl:choose><xsl:when test="$total = 1"> result</xsl:when><xsl:otherwise> results</xsl:otherwise></xsl:choose>
									</span>
								</a>
							</span>
						</div>
					</div>
					

					<xsl:if test="$total &gt; 0">
						<div class="std" style="display: none;">
							<xsl:attribute name="id">compactDiv_<xsl:value-of select="record[position()=1]/@id"/></xsl:attribute>
							<ol class="mr">
								<xsl:for-each select="record">
									<li class="g w0" style="margin-left: 3em;">
										<xsl:element name="a">
											<xsl:attribute name="class">l</xsl:attribute>
											<xsl:attribute name="href">http://digmap2.ist.utl.pt:8080/records_frbr/<xsl:value-of select="@catalog"/>/nobel/<xsl:value-of select="@id"/>.xml</xsl:attribute>
											<xsl:attribute name="title">http://digmap2.ist.utl.pt:8080/records_frbr/<xsl:value-of select="@catalog"/>/nobel/<xsl:value-of select="@id"/>.xml</xsl:attribute>
											<em><xsl:call-template name="getAllTitles"/></em>
										</xsl:element>
										<div class="s">
											<xsl:call-template name="getAuthors"/>
											<xsl:if test="count(info/author) &gt; 0"><br/></xsl:if>
											<!--<cite>http://digmap2.ist.utl.pt:8080/records_frbr/<xsl:value-of select="@catalog"/>/nobel/<xsl:value-of select="@id"/>.xml</cite>
											<cite>See detailed record</cite>-->
											<cite>Catalog: "<xsl:value-of select="@catalog"/>", ID: "<xsl:value-of select="@id"/>"</cite>
										</div>
									</li>
								</xsl:for-each>
							</ol>
						</div>
					</xsl:if>
				</xsl:otherwise>
				
				
			</xsl:choose>
		</ol>
	</xsl:template>

	<xsl:template name="getTitle">
		<xsl:value-of select="info/title/."/> (<xsl:value-of select="info/title/@xml:lang"/>)<br/>
	</xsl:template>

	<xsl:template name="getAllTitles">
		<xsl:for-each select="info/title">
			<xsl:value-of select="."/> (<xsl:if test="../date"><xsl:value-of select="../date/."/> - </xsl:if><xsl:value-of select="@xml:lang"/>)<br/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="getAuthors">
		<xsl:for-each select="info/author">
			<xsl:value-of select="@name"/><xsl:if test="@date"> (<xsl:value-of select="@date"/>)</xsl:if><xsl:if test="position() != last()">;<xsl:text> </xsl:text></xsl:if>
		</xsl:for-each>
	</xsl:template>

	

	<!-- TEL HEADER -->
    <xsl:template name="header">
        <div id="supranav">
            <table border="0" cellpadding="0" cellspacing="0">
                <tbody>
                    <tr>
                        <td>
                            <xsl:element name="img">
                                <xsl:attribute name="src">images/supranav_left.gif</xsl:attribute>
                                <xsl:attribute name="align">absmiddle</xsl:attribute>
                                <xsl:attribute name="width">30</xsl:attribute>
                                <xsl:attribute name="height">30</xsl:attribute>
                            </xsl:element>
                        </td>
                        <td id="supranav_select">
                            
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div id="headerTel">

            <div id="brand">
                <a href="http://search.theeuropeanlibrary.org/portal/">
                    <xsl:element name="img">
                        <xsl:attribute name="src">images/spacer.gif</xsl:attribute>
                        <xsl:attribute name="align">absmiddle</xsl:attribute>
                        <xsl:attribute name="width">220</xsl:attribute>
                        <xsl:attribute name="height">50</xsl:attribute>
                        <xsl:attribute name="hspace">20</xsl:attribute>
                        <xsl:attribute name="border">0</xsl:attribute>
                    </xsl:element>
                </a>
                <h1 id="header_title">The European Library</h1>
            </div>
            <!-- // header -->
        </div>
    </xsl:template>

    <!-- TEL FOOTER -->
    <xsl:template name="footer">
        <div id="footer">
            <div id="footerContent">
                <!--
                <a href="http://www.theeuropeanlibrary.org/portal/organisation/services/faq_en.html">FAQ's</a> |
                <a href="http://www.theeuropeanlibrary.org/portal/organisation/footer/jobscareers_en.html">Jobs &amp; Careers</a> |
                <a href="http://www.theeuropeanlibrary.org/portal/organisation/policy/policy_en.html">Policy</a> |
                <a href="http://www.theeuropeanlibrary.org/portal/organisation/footer/termsofservice_en.html">Terms of service</a> |
                <a href="http://www.theeuropeanlibrary.org/portal/organisation/footer/sitemap_en.html">Sitemap</a> |
                <a href="http://www.theeuropeanlibrary.org/portal/organisation/about_us/aboutusbios_en.html">Contact&nbsp;&amp;&nbsp;Feedback</a> |
                -->
                <nobr><a href="http://www.theeuropeanlibrary.org/portal/organisation/footer/copyright_en.html">© 2005-2009 The European Library</a></nobr>
            </div>

            <div id="subfooter">
            </div>
        </div>
    </xsl:template>



	<xsl:template name="resultsBarTop">
        <div id="navcontainer">
            <ul id="navlist">
                <li class="first active"><a href="">Search</a></li>
            </ul>
        </div>
        <div id="subnavcontainer">
		<div class="separatorBar">
			<div class="navResults">
			Results:
			<span class="bold">x
			</span>
			-
			<span class="bold">xx
			</span>
			of
			<span class="bold"><xsl:value-of select="/response/totalResults"/></span>
			for
			<span class="bold">"Keyword"</span>
			</div>
			
		</div>
	</div>
    </xsl:template>

</xsl:stylesheet>