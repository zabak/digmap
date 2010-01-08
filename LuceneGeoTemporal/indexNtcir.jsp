<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.*"%>
<%@page import="pt.utl.ist.lucene.analyzer.LgteAnalyzer"%>
<%@ page import="pt.utl.ist.lucene.treceval.geotime.index.IndexGeoTime" %>
<%@ page import="pt.utl.ist.lucene.treceval.geotime.utiltasks.SentenceCleanTagger" %>
<%@ page import="pt.utl.ist.lucene.utils.LgteAnalyzerManager" %>
<%@ page import="pt.utl.ist.lucene.treceval.IndexCollections" %>
<html>
<head><title>Example Web Application for Search with Lucene GeoTemporal Extensions</title>
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
    <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img border="0"  height="80" src="lgtesmall.png" alt="LGTE"/></a> <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal">go to project home page</a> | <a href="indexDigmap.jsp">go to Georeferenced Collection</a> | <a href="http://digmap.googlecode.com/files/lgte1_0_1b.war">download LGTE last version</a>
</div>
<a href="index.jsp">go back</a>
<h3>Please enter your search query:</h3>

<p>try for example: <a href="indexNtcir.jsp?q=">final flight of concorde</a></p>

<form action="indexNtcir.jsp" method="post">
    <input type="text" size="50" name="q"/>
    <input type="submit"/>
</form>
<%
        if (request.getParameter("q") != null && request.getParameter("q").length() > 1) {
    %>
             <h3>Results for '<%=request.getParameter("q")%>':</h3>
             <hr>
    <%
            try {
                LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, IndexGeoTime.indexPath);
                Analyzer analyzer = IndexCollections.en.getAnalyzerWithStemming();
//                LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), analyzer);
                System.out.println("Searching for: " + request.getParameter("q"));
                LgteHits hits = searcher.search(request.getParameter("q"), analyzer);


                out.println("<h3>Number of matching documents = " + hits.length() + "</h3>");
                for (int i = 0; i < 50; i++) {
                    LgteDocumentWrapper doc = hits.doc(i);
                    String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                    String fileAnnotatedPath = doc.get(SentenceCleanTagger.output + "\\" + docno.substring(0, 14) + ".sentences.zip");
//                    out.print("<p><b>TITLE</b>: <a href=\"downloadCran.jsp?docno=" + docno + "&filepath=" + filepath + "\">" + doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE) + "</a></p>");
                    out.print("<p><b>DOCNO</b> " + docno + "</p>");

                    out.print("<p><b>score</b>: " + hits.textScore(i) + "</p>");
                    out.print("<p><b>summary</b>" + hits.summary(i, pt.utl.ist.lucene.Globals.LUCENE_DEFAULT_FIELD) + "</p>");
                    out.print("<p><b>File</b> " + fileAnnotatedPath + "</p>");
                    out.print("<hr>");
                }
                searcher.close();
            }
            catch (Exception ee) {
                out.println("<b><p>Error: " + ee + "</p></b>");

                ee.printStackTrace();
            }
        }
    %>
    <jsp:include page="footer.jsp"/>
  </body>
</html>