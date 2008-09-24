<%@page contentType="text/html; charset=UTF-8" language="java" %>
<html>
<head><title>Example Web Application for Search with Lucene GeoTemporal Extensions</title>
    <style type="text/css">
       #header{}
       body * {font-size:11px;font-family: Verdana, Helvetica, sans-serif;}
    </style>
</head>
<body>
<div id="header">
    <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img border="0"  height="80" src="lgtesmall.png" alt="LGTE"/></a> <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal">go to project home page</a> | <a href="http://digmap.googlecode.com/files/lgte1_0_1b.war">download LGTE last version</a>
</div>

<h3>Please choose a collection:</h3>
<ul>
    <li><a href="indexCran.jsp">Cranfield Corpus</a> (Text Index)</li>
    <li><a href="indexBc.jsp">Braun Corpus</a> (Text Index)</li>
    <li><a href="indexDigmap.jsp">Digmap Georeferenced Resources</a> (Georeferenced, Temporal and Text Index)</li>
</ul>
<jsp:include page="footer.jsp"/>
</body>
</html>