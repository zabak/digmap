<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.*"%>
<%@page import="pt.utl.ist.lucene.analyzer.LgteAnalyzer"%>
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
    <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img border="0"  height="80" src="lgtesmall.png" alt="LGTE"/></a> <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal">go to project home page</a> | <a href="indexDigmap.jsp">go to Georeferenced Collection</a> | <a href="http://digmap.googlecode.com/files/lgte.war">download LGTE last version</a>
</div>
<a href="index.jsp">go back</a>
<h3>Please enter your search query:</h3>

<p>try for example: <a href="indexBc.jsp?q=Officie amptluyden">Officie amptluyden</a></p>

<form action="indexBc.jsp" method="post">
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
                LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, Globals.DATA_DIR + "/lm/version1/bc");
                Analyzer analyzer = new LgteAnalyzer();
                LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), analyzer);
                System.out.println("Searching for: " + query.getQuery().toString());
                LgteHits hits = searcher.search(query);


                out.println("<h3>Number of matching documents = " + hits.length() + "</h3>");
                for (int i = 0; i < hits.length(); i++)
                {
                    LgteDocumentWrapper doc = hits.doc(i);
                    String filepath = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                    String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                    out.print("<p><b>TITLE</b>: <a href=\"downloadBc.jsp?docno=" + docno + "&filepath=" + filepath + "\">" + doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE) + "</a></p>");
                    out.print("<p><b>DOCNO</b> " + docno + "</p>");
                    out.print("<p><b>score</b>: " + hits.score(i) + "</p>");
                    out.print("<p><b>summary</b>" + hits.summary(i,"contents")+ "</p>");
                    out.print("<p><b>FILE_PATH</b>: " + filepath + "</p>");
                    out.print("<hr>");
                }
                searcher.close();
            }
            catch (Exception ee)
            {
                out.println("<b><p>Error: " + ee + "</p></b>");
            }
        }
    %>
    <jsp:include page="footer.jsp"/>
  </body>
</html>