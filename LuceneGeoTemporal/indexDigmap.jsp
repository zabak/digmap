<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.*"%>
<%@page import="pt.utl.ist.lucene.analyzer.LgteAnalyzer"%>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="pt.utl.ist.lucene.forms.TimeBox" %>
<html>
<head><title>Example Web Application for Search with Lucene GeoTemporal Extensions</title></head>
<body>
<a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img src="lgte.png" alt="LGTE"/></a>

<h3>Please enter your search query:</h3>

<p>You can try a search centered in Portugal: lat:37 lng:-9 radium:1000</p>

<p>Or you can try a search centered in Belgique with text Imperii: Imperii lat:51 lng:-175 radiumMiles:1000</p>

<p>or you can try time: Imperii lat:51 lng:-175 radiumMiles:1000 time:1680</p>

<p>with or with not radiumYears for example radiumYears:200</p>

<p>try ths search and note that many results but the first set is closer in text and space: Imperii map lat:51 lng:-175 radiumMiles:1000 filter:no</p>

<form action="indexDigmap.jsp" method="post">
    <input type="text" size="50" name="q"/>
</form>
<%
    if (request.getParameter("q") != null && request.getParameter("q").length() > 1) {
%>
<h3>Results for '<%=request.getParameter("q")%>':</h3>
<hr>
<%
        try
        {
            LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.InExpB2DFRModel, Globals.DATA_DIR + "/lm/version1/digmap");
            Set<String> notTokenizableFields = new HashSet<String>();
            notTokenizableFields.add("collection");
            notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
            notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
            Analyzer analyzer = new LgteAnalyzer(notTokenizableFields);
            LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), analyzer);
            if (query != null)
            {
                System.out.println("Searching for: " + query.getQuery().toString());
                LgteHits hits = searcher.search(query);


                out.println("<h3>Number of matching documents = " + hits.length() + "</h3>");
                for (int i = 0; i < hits.length(); i++)
                {
                    LgteDocumentWrapper doc = hits.doc(i);
                    String filepath = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                    String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                    String collection = doc.get("collection");
                    double distance = hits.spaceDistanceKm(i);
                    double latitude = doc.getLatitude();
                    double longitude = doc.getLongitude();
                    int year = doc.getTimeYear();
                    TimeBox timeBox = doc.getTimeBox();
                    int distanceYears = hits.timeDistanceYears(i);
                    out.print("<p><b>COLLECTION</b>:" + collection + "</p>");
                    out.print("<p><b>TITLE</b>: <a href=\"downloadDigmap.jsp?id=" + docno + "&filepath=" + filepath + "\">" + doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE) + "</a></p>");
                    out.print("<p><b>DOCNO</b> " + docno + "</p>");
                    out.print("<p><b>score</b>: " + hits.score(i) + "</p>");
                    out.print("<p><b>summary</b>" + hits.summary(i, "contents") + "</p>");
                    out.print("<p><b>FILE_PATH</b>: " + filepath + "</p>");
                    out.print("<p><b>---GEO_DOC</b>: " + doc.get(Globals.LUCENE_GEO_DOC_INDEX) + "</p>");
                    out.print("<p><b>---GEO</b>: lat: " + latitude + "; lng: " + longitude + "</p>");
                    out.print("<p><b>---DISTANCE (KM)</b>: " + distance + "</p>");

                    out.print("<p><b>+++TIME_DOC</b>: " + doc.get(Globals.LUCENE_TIME_DOC_INDEX) + "</p>");
                    out.print("<p><b>+++DISTANCE (Years)</b>: " + distanceYears + "</p>");
                    out.print("<p><b>+++TIME</b>: " + year + "</p>");
                    if(timeBox!=null)
                        out.print("<p><b>+++TIMEBOX</b>: " + timeBox.getStartTimeYear() + " - " + timeBox.getEndTimeYear() + " </p>");



                    out.print("<hr>");
                }
                searcher.close();
            }
        }
        catch (Exception ee)
        {
            out.println("<b><p>Error: " + ee + "</p></b>");
            ee.printStackTrace();
        }
    }
%>
</body>
</html>