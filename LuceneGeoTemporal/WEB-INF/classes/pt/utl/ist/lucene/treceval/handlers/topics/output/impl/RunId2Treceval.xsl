<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output omit-xml-declaration="yes"></xsl:output>
	<xsl:template match="/">
        <xsl:variable name="runid" select="//RUNID"/>
        <xsl:for-each select="//DOCUMENT">
			<xsl:value-of select="../../@ID"/>
			<xsl:text> Q0 </xsl:text>
			<xsl:value-of select="./@DOCID"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="number(./@RANK) - 1"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="./@SCORE"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="$runid"/><xsl:text>
</xsl:text>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
