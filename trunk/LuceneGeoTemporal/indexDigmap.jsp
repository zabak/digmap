<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.*"%>
<%@page import="pt.utl.ist.lucene.analyzer.LgteAnalyzer"%>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="pt.utl.ist.lucene.forms.TimeBox" %>
<%@ page import="pt.utl.ist.lucene.forms.UnknownForm" %>
<%@ page import="pt.utl.ist.lucene.forms.RectangleForm" %>
<%@ page import="pt.utl.ist.lucene.forms.CircleForm" %>
<html>
<head>
    <title>Example Web Application for Search with Lucene GeoTemporal Extensions</title>
    <style type="text/css">
       #header{}
       body * {font-size:11px;font-family: Verdana, Helvetica, sans-serif;}
       #search{}
       #searches{padding:3px;}
       #searches a{text-decoration:none}
       #searches td{font-size:11px;font-family: Verdana, Helvetica, sans-serif; border-bottom: 1px solid black;}
       #fields ul{margin:0; padding:5px;border:1px solid black; background-color:#4C6C8F;color:white;}
       #fields ul li{margin:0}
       #fields li{font-size:11px;font-family: Verdana, Helvetica, sans-serif;}

       #results
       {
       }
       .result {margin-top:10px}
       .result th
       {
           background-color:#A5B6C6;
           font-size:11px;font-family: Verdana, Helvetica, sans-serif;
           text-align:left;
           font-weight:normal;


       }
       .result th.spatial
       {
           background-color:#B1CFBC;
       }
       .result th.time
       {
           background-color:#D3CBB7;
       }
        .result td
       {border-bottom:1px solid black;
            font-size:11px;font-family: Verdana, Helvetica, sans-serif;
          text-align:left;
          font-weight:normal;
           margin-top:5px;
       }
       
    </style>

</head>
<body>
<div id="header">
    <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img border="0"  height="80" src="lgtesmall.png" alt="LGTE"/></a> <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal">go to project home page</a> | <a href="http://digmap.googlecode.com/files/lgte.war">download LGTE last version</a>
</div>
<a href="index.jsp">go back</a>

<table>
<tr>
    <td valign="top" id="search">
        <h3>Please enter your search query:</h3>
        <form action="indexDigmap.jsp" method="post">
            <input type="text" size="100" name="q" value="<%if(request.getParameter("q")!=null){out.print(request.getParameter("q"));}%>"/>
            <input type="submit"/>
        </form>
        <h3>or you can try one of our test queries</h3>
        <table cellpadding="0" cellspacing="0" id="searches">
            <tr><td>You can try a search centered in Portugal</td><td><a href="indexDigmap.jsp?q=lat:37 lng:-9 radium:1000">lat:37 lng:-9 radium:1000</a></td></tr>
            <tr><td>A search centered in Belgique with text "Imperii"</td><td><a href="indexDigmap.jsp?q=Imperii lat:51 lng:4 radiumMiles:1000">Imperii lat:51 lng:4 radiumMiles:1000</a></td></tr>
            <tr><td>Using a diferent Text Model</td><td><a href="indexDigmap.jsp?q=Imperii lat:51 lng:4 radiumMiles:1000 time:1680 model:bm25">model:bm25 Imperii lat:51 lng:4 radiumMiles:1000 time:1680</a></td></tr>
            <tr><td>Using Textual Query Expansion</td><td><a href="indexDigmap.jsp?q=qe:text Imperii lat:51 lng:4 radiumMiles:1000 time:1680">qe:text Imperii lat:51 lng:4 radiumMiles:1000 time:1680</a></td></tr>
            <tr><td>Using LGTE Query Expansion</td><td><a href="indexDigmap.jsp?q=qe:lgte Imperii lat:51 lng:4 radiumMiles:1000 time:1680">qe:lgte Imperii lat:51 lng:4 radiumMiles:1000 time:1680</a></td></tr>
            <tr><td>Try time search</td><td><a href="indexDigmap.jsp?q=Imperii lat:51 lng:4 radiumMiles:1000 time:1680">Imperii lat:51 lng:4 radiumMiles:1000 time:1680</a></td></tr>
            <tr><td>Note that the first set is closer in text and space because no radium was defined</td><td><a href="indexDigmap.jsp?q=Imperii map lat:51 lng:4 radiumMiles:1000 time:1666 filter:no">Imperii map lat:51 lng:4 radiumMiles:1000 time:1666 filter:no</a></td></tr>
            <tr><td>Note that the first set is closer in time, text because time radium is short</td><td><a href="indexDigmap.jsp?q=Imperii map lat:51 lng:4 radiumMiles:1000 time:1666 radiumYears:10 filter:no">Imperii map lat:51 lng:4 radiumMiles:1000 time:1666 radiumYears:10 filter:no</a></td></tr>
            <tr><td>Try an interval time search and a spatial box search in Belgique</td><td><a href="indexDigmap.jsp?q=starttime:1650 endtime:1660 lat:51 west:3 east:5 south:50 north:52">starttime:1650 endtime:1660 lat:51 west:3 east:5 south:50 north:52</a></td></tr>
            <tr><td>Find all time resources</td><td><a href="indexDigmap.jsp?q=time:1900 filter:no">time:1900 filter:no</a></td></tr>
            <tr><td>Find all geo resources</td><td><a href="indexDigmap.jsp?q=lat:0 lng:0 filter:no">lat:0 lng:0 filter:no</a></td></tr>
        </table>
        <div id="results">
        <%
            if (request.getParameter("q") != null && request.getParameter("q").length() > 1) {
        %>
        <h3>Results for '<%=request.getParameter("q")%>':</h3>
        <%
                try
                {
                    LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, Globals.DATA_DIR + "/lm/version1/digmap");

                    //LgteAnalizer let you define witch fields should not be tokenized
                    //typicaly here we choose those fields used as identifiers, previously indexed with tokenize option setted to false in document Field.
                    //Identifiers are constant fields, so should not be filtered with any kind of Tokenizers like stopwords, stemming, ...
                    Set<String> notTokenizableFields = new HashSet<String>();
                    notTokenizableFields.add("collection");
                    notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                    notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                    Analyzer analyzer = new LgteAnalyzer(notTokenizableFields);
                    //we pass the searcher to parser because in case of query expansion it will be needed
                    LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"),analyzer,searcher);
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
                            UnknownForm unknownForm = doc.getUnknownForm();

                            int distanceYears = hits.timeDistanceYears(i);
                            out.print("<div class=\"result\"><table>");
                            out.print("<tr><th>COLLECTION</th><td>" + collection + "</td></tr>");
                            out.print("<tr><th>TITLE</th><td> <a href=\"downloadDigmap.jsp?id=" + docno + "&filepath=" + filepath + "\">" + doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE) + "</a></td></tr>");
                            out.print("<tr><th>DOCNO</th><td>" + docno + "</td></tr>");
                            out.print("<tr><th>score</th><td> " + hits.score(i) + "</td></tr>");
                            out.print("<tr><th>only text score</th><td> " + hits.textScore(i) + "</td></tr>");
                            out.print("<tr><th>summary</th><td>" + hits.summary(i, "contentsStore") + "</td></tr>");
                            out.print("<tr><th>FILE_PATH</th><td> " + filepath + "</td></tr>");
                            if (doc.isGeoDoc())
                            {
                                out.print("<tr><th class=\"spatial\">GEO</th><td> lat: " + latitude + "; lng: " + longitude + "</td></tr>");
                                out.print("<tr><th class=\"spatial\">DISTANCE (KM)</th><td> " + distance + "</td></tr>");
                                out.print("<tr><th class=\"spatial\">GEO SCORE</th><td> " + hits.spatialScore(i) + "</td></tr>");
                                if (unknownForm != null && unknownForm.getWidth() > 0)
                                {
                                    if (unknownForm instanceof RectangleForm)
                                        out.print("<tr><th class=\"spatial\">Rectangle Form(west,east,south,north,width(miles))</th><td> (" + ((RectangleForm) unknownForm).getWest() + ";" + ((RectangleForm) unknownForm).getEast() + ";" + ((RectangleForm) unknownForm).getSouth() + ";" + ((RectangleForm) unknownForm).getNorth() + ";" + unknownForm.getWidth() + ") </td></tr>");
                                    else if (unknownForm instanceof CircleForm)
                                        out.print("<tr><th class=\"spatial\">Circle Form(lat,lng,radium,width(miles))</th><td> (" + ((CircleForm) unknownForm).getLat() + ";" + ((CircleForm) unknownForm).getLng() + ";" + unknownForm.getWidth() + ") </td></tr>");
                                    else
                                        out.print("<tr><th class=\"spatial\">Unknown Form(lat,lng,width(miles))</th><td> (" + unknownForm.getCentroide().getLat() + ";" + unknownForm.getCentroide().getLng() + ";" + unknownForm.getWidth() + ") </td></tr>");
                                }
                            }

                            if (doc.isTimeDoc())
                            {
                                out.print("<tr><th class=\"time\">TIME</th><td> " + year + "</td></tr>");
                                out.print("<tr><th class=\"time\">DISTANCE (Years)</th><td> " + distanceYears + "</td></tr>");
                                out.print("<tr><th class=\"time\">TIME SCORE</th><td> " + hits.timeScore(i) + "</td></tr>");
                                if (timeBox != null)
                                    out.print("<tr><th class=\"time\">TIMEBOX</th><td> " + timeBox.getStartTimeYear() + " - " + timeBox.getEndTimeYear() + " </td></tr>");
                            }
                            out.print("</table></div>");
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
        </div>
    </td>
    <td  valign="top"  id="fields">
        <h3>LGTE Fields:</h3>
        <ul>
            <li>
                lat (query reference latitude)
            </li>
            <li>
                lng (query reference longitude)
            </li>
            <li>
                north (limited box north limit)
            </li>
            <li>
                south (limited box south limit)
            </li>
            <li>
                east (limited box east limit)
            </li>
            <li>
                west (limited box west limit)
            </li>
            <li>
                radium (limit radium in miles by default)
            </li>
            <li>
                radiumMiles
            </li>
            <li>
                radiumKm
            </li>
            <li>
                starttime (Time interval query bottom limit yyyy[-mm[-dd]] or reverse direction)
            </li>
            <li>
                endtime (Top limit in time)
            </li>
            <li>
                starttimeMiliseconds (in miliseconds)
            </li>
            <li>
                endtimeMiliseconds (in miliseconds)
            </li>
            <li>
                time (Reference Query Time yyyy[-mm[-dd]] or reverse direction)
            </li>
            <li>
                timeMiliseconds (in miliseconds)
            </li>
            <li>
                radiumYears (limit radium in years)
            </li>
            <li>
                radiumMonths
            </li>
            <li>
                radiumDays
            </li>
            <li>
                radiumHours
            </li>
            <li>
                radiumMinutes
            </li>
            <li>
                radiumSeconds
            </li>
            <li>
                radiumMiliSeconds
            </li>
            <li>
                order (order type spatial, temporal, textual)
            </li>
            <li>
                filter (filter results out of the box)
            </li>
            <li>
                qe (use query expansion)
            </li>
            <li>
                model (Searching model)
            </li>
        </ul>
        <h3>LGTE Supported Retrieval Models:</h3>
        <ul>
            <li>`VectorSpace` or just `vs`</li>
            <li>`LanguageMoldel` or just `lm`</li>
            <li>`DLHHypergeometricDFRModel`</li>
            <li>`InExpC2DFRModel`</li>
            <li>`InExpB2DFRModel`</li>
            <li>`IFB2DFRModel` </li>
            <li>`InL2DFRModel`</li>
            <li>`PL2DFRModel` </li>
            <li>`BB2DFRModel` </li>
            <li>`OkapiBM25Model` or just `bm25`</li>
        </ul>
        <p>To learn more about LGTE query language please go to wiki page: <a href="http://code.google.com/p/digmap/wiki/QueryLanguage">http://code.google.com/p/digmap/wiki/QueryLanguage</a></p>
    </td>
</tr>
</table>

<jsp:include page="footer.jsp"/>

</body>
</html>