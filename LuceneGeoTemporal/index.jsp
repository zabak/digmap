<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="pt.utl.ist.lucene.LgteQueryParser"%>
<%@page import="pt.utl.ist.lucene.LgteQuery"%>
<%@page import="pt.utl.ist.lucene.LgteHits"%>
<%@page import="pt.utl.ist.lucene.LgteDocumentWrapper"%>
<%@page import="pt.utl.ist.lucene.analyzer.LgteAnalyzer"%>
<%@page import="pt.utl.ist.lucene.LgteIndexSearcherWrapper"%>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.Globals"%>
<%@ page import="org.apache.lucene.store.Directory" %>
<%@ page import="org.apache.lucene.store.FSDirectory" %>
<html>
  <head><title>Example Web Application for Search with Lucene GeoTemporal Extensions</title></head>
  <body>
	<img src="lgte.png" />
    <h3>Please enter your search query:</h3>
    <form action="index.jsp" method="post">
        <input type="text" size="50" name="q" />
    </form>
    <%
        if (request.getParameter("q") != null && request.getParameter("q").length() > 1) {
    %>
             <h3>Results for '<%=request.getParameter("q")%>':</h3>
             <hr>
    <%
            try
            {
                LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Globals.DATA_DIR + "/lm/version1/bc");
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
                    out.print("<p><b>TITLE</b>: <a href=\"download.jsp?docno=" + docno + "&filepath=" + filepath + "\">" + doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE) + "</a></p>");
                    out.print("<p><b>DOCNO</b> " + docno + "</p>");
                    out.print("<p><b>score</b>: " + hits.score(i) + "</p>");
                    out.print("<p><b>summary</b>" + hits.summary(i,"contents")+ "</p>");
                    out.print("<p><b>FILE_PATH</b>: " + filepath + "</p>");
                    out.print("<hr>");
//                    out.println("<p>File: <a href=\"download.jsp?download=" +
//                            doc.get("filepath") + "\">" + doc.get("contents") +
//                            "</a>, score: " + hits.score(i) + "</p>");
                }
                searcher.close();
            }
            catch (Exception ee)
            {
                out.println("<b><p>Error: " + ee + "</p></b>");
            }
        }
    %>
  </body>
</html>