<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output omit-xml-declaration="yes" encoding="UTF-8"/>
	<xsl:template match="/">

			<html>
				<head>
					<title>Results</title>
				</head>
				<body>
	
						<table border="1">
							<tbody>
								<tr>
									<th> </th>
									<xsl:for-each select="//run">
										<th><xsl:value-of select="./@id"/></th>
									</xsl:for-each>
								</tr>
								<xsl:for-each select="//run[1]/results/result">
									<tr>
										<th><xsl:value-of select="@name"/></th>
										<xsl:variable name="resultName" select="@name"/>
										<xsl:for-each select="//run/results/result[@name=$resultName]">
											<th><xsl:value-of select="."/></th>
										</xsl:for-each>
									</tr>
								</xsl:for-each>								
							</tbody>
						</table>
				</body>
			</html>
	</xsl:template>
</xsl:stylesheet>
