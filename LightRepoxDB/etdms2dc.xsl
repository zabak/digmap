<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
                              xmlns:etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/"
                              xmlns:dc="http://purl.org/dc/elements/1.1/"
                              exclude-result-prefixes="etdms">
  <xsl:output method="xml"
        omit-xml-declaration="yes"
        encoding="utf-8"/>

  <xsl:template match="/etdms:thesis">
    <oai_dc:dc xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
      <xsl:apply-templates/>
    </oai_dc:dc>
  </xsl:template>

  <xsl:template match="etdms:title">
    <dc:title><xsl:apply-templates/></dc:title>
  </xsl:template>

  <xsl:template match="etdms:creator">
    <dc:creator><xsl:apply-templates/></dc:creator>
  </xsl:template>

  <xsl:template match="etdms:subject">
    <dc:subject><xsl:apply-templates/></dc:subject>
  </xsl:template>

  <xsl:template match="etdms:description">
    <dc:description><xsl:apply-templates/></dc:description>
  </xsl:template>

  <xsl:template match="etdms:publisher">
    <dc:publisher><xsl:apply-templates/></dc:publisher>
  </xsl:template>

  <xsl:template match="etdms:contributor">
    <dc:contributor><xsl:apply-templates/></dc:contributor>
  </xsl:template>

  <xsl:template match="etdms:date">
    <dc:date><xsl:apply-templates/></dc:date>
  </xsl:template>

  <xsl:template match="etdms:type">
    <dc:type><xsl:apply-templates/></dc:type>
  </xsl:template>

  <xsl:template match="etdms:format">
    <dc:format><xsl:apply-templates/></dc:format>
  </xsl:template>

  <xsl:template match="etdms:identifier">
    <dc:identifier><xsl:apply-templates/></dc:identifier>
  </xsl:template>

  <xsl:template match="etdms:language">
    <dc:language><xsl:apply-templates/></dc:language>
  </xsl:template>

  <xsl:template match="etdms:rights">
    <dc:rights><xsl:apply-templates/></dc:rights>
  </xsl:template>

  <xsl:template match="etdms:source">
    <dc:source><xsl:apply-templates/></dc:source>
  </xsl:template>

  <xsl:template match="etdms:relation">
    <dc:relation><xsl:apply-templates/></dc:relation>
  </xsl:template>

  <xsl:template match="etdms:coverage">
    <dc:coverage><xsl:apply-templates/></dc:coverage>
  </xsl:template>

  <xsl:template match="etdms:degree">
  </xsl:template>

</xsl:stylesheet>