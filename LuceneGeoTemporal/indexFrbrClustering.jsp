<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.*"%>
<%@page import="pt.utl.ist.lucene.analyzer.LgteAnalyzer"%>
<%@ page import="pt.utl.ist.lucene.config.LocalProperties" %>
<%@ page import="pt.utl.ist.lucene.forms.TimeBox" %>
<%@ page import="pt.utl.ist.lucene.utils.XmlUtils" %>
<%@ page import="java.io.DataInputStream" %>
<%@ page import="java.io.DataOutputStream" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLConnection" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="digmapFrbr.webLgteFrbr.SearchRequestHandler" %>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod" %>
<%@ page import="org.apache.commons.httpclient.methods.RequestEntity" %>
<%@ page import="org.apache.commons.httpclient.HttpClient" %>
<%
    Properties props = new LocalProperties("digmapFrbr/conf.properties");
    Globals.DATA_DIR = props.getProperty("data.dir");
    Globals.INDEX_DIR = props.getProperty("indexes.dir");
    String queryStr = request.getParameter("q");
    if(queryStr == null)
        queryStr = "";
    if(request.getParameter("creator") != null && request.getParameter("creator").length() > 0)
    {
        queryStr += " dc_creator:(" + request.getParameter("creator")+ ")^3";
    }
    if(request.getParameter("contributor") != null && request.getParameter("contributor").length() > 0)
    {
        queryStr += " dc_contributor:(" + request.getParameter("contributor")+ ")^2";
    }
    if(request.getParameter("title") != null && request.getParameter("title").length() > 0)
    {
        queryStr += " title:(" + request.getParameter("title")+ ")^3";
    }
    if(request.getParameter("date") != null && request.getParameter("date").length() > 0)
    {
        queryStr += " dc_date:(" + request.getParameter("date") + ")^2";
    }
    if(request.getParameter("description") != null && request.getParameter("description").length() > 0)
    {
        queryStr += " dc_description:(" + request.getParameter("description")+ ")^2";
    }
    if(request.getParameter("subject") != null && request.getParameter("subject").length() > 0)
    {
        queryStr += " dc_subject:(" + request.getParameter("subject")+ ")^2";
    }
    request.setAttribute("queryStr",queryStr);

    if (request.getParameter("xml") != null && (request.getParameter("xml").equals("frbr"))) {

        StringBuilder responseBuilder = new StringBuilder();
        if (queryStr != null && queryStr.length() > 1) {

            try {


                LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, Globals.INDEX_DIR + "/lm/version1/digmapFrbr");
                Set<String> notTokenizableFields = new HashSet<String>();
                notTokenizableFields.add("collection");
                notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                Analyzer analyzer = new LgteAnalyzer(notTokenizableFields);
                //we pass the searcher to parser because in case of query expansion it will be needed
                String q = queryStr;
                if (request.getParameter("collection") != null && request.getParameter("collection").length() > 0)
                    q = "collection:" + request.getParameter("collection") + " AND (" + q + ")";
                LgteQuery query = LgteQueryParser.parseQuery(q, analyzer, searcher);
                if (query != null) {
                    LgteHits hits = searcher.search(query);
                    int pageNumber = 1;
                    int startResult = 0;
                    int showNumber = 30;
                    if (request.getParameter("page") != null) {
                        pageNumber = Integer.parseInt(request.getParameter("page"));
                    }
                    if (request.getParameter("show") != null) {
                        showNumber = Integer.parseInt(request.getParameter("show"));
                        startResult = pageNumber * showNumber - showNumber;
                    }

                    PostMethod post = new PostMethod("http://localhost:8080/frbrCluster/Handler");

                    RequestEntity entity = new SearchRequestHandler(hits, startResult, showNumber, pageNumber, q);
                    post.setRequestEntity(entity);
                    HttpClient httpclient = new HttpClient();

                    try
                    {
                        int result = httpclient.executeMethod(post);
                        System.out.println("Ja terminou vai fazer release");
                        String responseStr = post.getResponseBodyAsString();
                        System.out.println("Ja terminou o release vai começar a escrever");
                        response.setContentType("text/xml");
                        response.setCharacterEncoding("UTF-8");
                        PrintWriter pw = response.getWriter();
                        pw.write(responseStr);
                        System.out.println("Ja terminou de escrever");
                    } finally {
                        try{
                        // Release current connection to the connection pool once you are done
                        post.releaseConnection();
                        }catch(Throwable e)
                        {
                            System.out.println("Cant release: already closed:" + e); 
                        }

                    }



                    searcher.close();
                }
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            responseBuilder = new StringBuilder();
            responseBuilder.append("<lgte>\n");
            responseBuilder.append("<response><error>no query</error></response>\n");
            responseBuilder.append("</lgte>\n");
        }


    } else {

%>
<html>
<head>
    <title>The European Library - FRBR Clustering</title>
    <!--FRBR Styles-->
    <link rel="stylesheet" type="text/css" href="css/styleIndexer.css">
    <link rel="stylesheet" type="text/css" href="frbr_style/navigation.css">
    <link rel="stylesheet" type="text/css" href="frbr_style/page_layout.css">
    <!--FRBR Styles-->
    <style type="text/css">
        #header{}

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
        .result th.time
        {
            background-color:#B1CFBC;
        }
        .result th.appField
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

        .searchForm td.column
        {
            border:1px solid black;
            background-color: gainsboro;
        }

    </style>
    <script type="text/javascript">
        /**
         * Return an HTML element given ID
         *
         * @author Jorge Machado
         * @date April 2008
         *
         * params:
         * @objectId required object
         */
        function getObjectById(objectId)
        {
            // cross-browser function to get an object's style object given its id
            try
            {
                if(document.getElementById && document.getElementById(objectId))
                {
                    // W3C DOM
                    return document.getElementById(objectId);
                }
                else if (document.all(objectId))
                {
                    // MSIE 4 DOM
                    return document.all(objectId);
                }
                else if (document.layers && document.layers[objectId])
                {
                    // NN 4 DOM.. note: this won't find nested layers
                    return document.layers[objectId];
                }
                else
                {
                    return false;
                }
            }
            catch(e)
            {
                return false;
            }
        }
        function hideOne(id)
        {
        //    getObjectById(id).style.visibility='hidden';
        //    getObjectById(id).style.position='absolute';
            getObjectById(id).style.display='none';
        }

        function showOne(id)
        {
        //    getObjectById(id).style.visibility='visible';
        //    getObjectById(id).style.position='relative';
            getObjectById(id).style.display='';
        }
        function showOrHideOne(id)
        {
            if(getObjectById(id).style.display == 'none')
                showOne(id);
            else
                hideOne(id);
        }

    </script>

</head>
<body class="twoColElsLtHdr">


<!--FRBR Style-->
<div style="left: 0px;" id="container">
<div id="supranav">
    <table border="0" cellpadding="0" cellspacing="0">
        <tbody>
            <tr>

                <td></td>
            </tr>
        </tbody>
    </table>
</div>
<div id="headerTel">

    <div id="brand">
        <a href="http://search.theeuropeanlibrary.org/portal/"><img src="frbr_style/spacer.gif" align="absmiddle" border="0" height="50" hspace="20" width="220"></a>




        <font size="5"> The European Library - FRBR Clustering</font>

    </div>
</div>
<div id="navcontainer">
    <ul id="navlist">
        <li class="second">
            <a href="indexFrbrLibrary.jsp">SEARCH Library</a>
        </li>
        <li class="first active">
            <a href="">FRBR Clustering</a>
        </li>
    </ul>
</div>
<div id="subnavcontainer">
    <div class="separatorBar">
        <div style="float: right; margin-top: -3px; padding-right: 8px;">
            <%--<form action="index.jsp" method="get">--%>
            <%--<input type="hidden" name="show" value="50"/>--%>
            <%--<input type="hidden" name="page" value="1"/>--%>
            <%--<input type="hidden" name="xml" value="false"/>--%>

            <%--<label for="mod_search_searchword" accesskey="?"></label><input value="search" name="verb" type="hidden"><input value="utf-8" name="encoding" type="hidden"><input value="true" name="html" type="hidden"><input value="1" name="collectionId" type="hidden"><input onblur="if(this.value=='') this.value='Search for...';" onfocus="if(this.value=='Search for...') this.value='';" value="Search for..." class="box" name="q" id="mod_search_searchword" type="text"> <input value="Search" class="button" type="submit">--%>
            <%--</form>--%>
        </div>
        <div style="float: left;"></div>
    </div>
</div>
<div class="IndexPage">
<div id="content">


<fieldset class="radius boxGray mainBox" style="vertical-align:top" >

<!--END FRBR STYLE-->

<!--
<div align="right">
    <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img border="0"  height="80" src="lgtesmall.png" alt="LGTE"/></a> <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal">go to project home page</a> | <a href="http://digmap.googlecode.com/files/lgte1_0_1b.war">download LGTE last version</a>
</div>
    -->

<table>
<tr>
<td valign="top" id="search">
<h3>Please enter your search query:</h3>

<form action="indexFrbrClustering.jsp" method="get">
    <input type="hidden" name="xml" value="frbr"/>
    <input type="hidden" name="page" value="1"/>
    <table class="searchForm">
        <tr>
            <td valign="top" class="column">
                <table>
                    <tr>
                        <td>
                            Search for
                        </td>
                        <td>
                            <input type="text" size="100" name="q" value="<%if(request.getParameter("q")!=null){out.print(request.getParameter("q"));}%>"/>
                        </td>
                        <td>
                            <a href="javascript:showOrHideOne('advancedSearch')">Advanced Search</a>
                        </td>
                    </tr>
                </table>
                <%
                    String display = "style=\"display:none\"";
                    if(request.getParameter("showAdvanced")!=null && request.getParameter("showAdvanced").equals("true"))
                        display = "";
                %>
                <table id="advancedSearch" <%=display%>>
                    <tr>
                        <td>
                            Title
                        </td>
                        <td>
                            <input type="text" size="100" name="title" value="<%if(request.getParameter("title")!=null){out.print(request.getParameter("title"));}%>"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Author
                        </td>
                        <td>
                            <input type="text" size="100" name="creator" value="<%if(request.getParameter("creator")!=null){out.print(request.getParameter("creator"));}%>"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Contributor
                        </td>
                        <td>
                            <input type="text" size="100" name="contributor" value="<%if(request.getParameter("contributor")!=null){out.print(request.getParameter("contributor"));}%>"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Date
                        </td>
                        <td>
                            <input type="text" size="100" name="date" value="<%if(request.getParameter("date")!=null){out.print(request.getParameter("date"));}%>"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Subject
                        </td>
                        <td>
                            <input type="text" size="100" name="subject" value="<%if(request.getParameter("subject")!=null){out.print(request.getParameter("subject"));}%>"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Description
                        </td>
                        <td>
                            <input type="text" size="100" name="description" value="<%if(request.getParameter("description")!=null){out.print(request.getParameter("description"));}%>"/>
                        </td>
                    </tr>
                </table>
                <table>
                    <tr>
                        <td>Show</td>
                        <td>
                            <select name="show">
                                <option value="20">20</option>
                                <option value="50">50</option>
                                <option value="100">100</option>
                                <option value="200">200</option>
                                <option value="500">500</option>
                            </select>
                            Clusters
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"><input value="Search" class="button"  type="submit"/></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>


</form>

<div id="results">
    <%
        if (queryStr != null && queryStr.length() > 1) {
    %>
    <h3>Results for '<%=queryStr%>':</h3>
    <%
            try {
                LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, Globals.INDEX_DIR + "/lm/version1/digmapFrbr");

                //LgteAnalizer let you define witch fields should not be tokenized
                //typicaly here we choose those fields used as identifiers, previously indexed with tokenize option setted to false in document Field.
                //Identifiers are constant fields, so should not be filtered with any kind of Tokenizers like stopwords, stemming, ...
                Set<String> notTokenizableFields = new HashSet<String>();
                notTokenizableFields.add("collection");
                notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                Analyzer analyzer = new LgteAnalyzer(notTokenizableFields);
                //we pass the searcher to parser because in case of query expansion it will be needed
                String q = queryStr;
                if (request.getParameter("collection") != null && request.getParameter("collection").length()>0)
                    q = "collection:" + request.getParameter("collection") + " AND (" + q + ")";
                LgteQuery query = LgteQueryParser.parseQuery(q, analyzer, searcher);
                if (query != null) {

                    System.out.println("Searching for: " + query.getQuery().toString());
                    LgteHits hits = searcher.search(query);


                    out.println("<h3>Number of matching documents = " + hits.length() + "</h3>");
                    out.println("<h3>Showing " + request.getParameter("show") + " results - Page " + request.getParameter("page") + "</h3>");

                    int startResult = 0;
                    int showNumber = 100;
                    if (request.getParameter("page") != null && request.getParameter("show") != null) {
                        int pageNumber = Integer.parseInt(request.getParameter("page"));
                        showNumber = Integer.parseInt(request.getParameter("show"));
                        startResult = pageNumber * showNumber - showNumber;
                    }


                    for (int i = startResult; i < hits.length() && i < startResult + showNumber; i++) {
                        LgteDocumentWrapper doc = hits.doc(i);
                        String filepath = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                        String originalRelativePath = doc.get("originalRelativePath");
                        String relativePath = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_RELATIVE_DATA_DIR_PATH);
                        String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                        String collection = doc.get("collection");
//                            double distance = hits.spaceDistanceKm(i);
//                            double latitude = doc.getLatitude();
//                            double longitude = doc.getLongitude();
//                            int year = doc.getTimeYear();
//                            TimeBox timeBox = doc.getTimeBox();
//                            UnknownForm unknownForm = doc.getUnknownForm();
//
//                            int distanceYears = hits.timeDistanceYears(i);
                        out.print("<div class=\"result\"><table>");
                        out.print("<tr><th>COLLECTION</th><td>" + collection + "</td></tr>");
                        out.print("<tr><th>TITLE</th><td>" + doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE) + "(<a href=\"downloadDigmapFrbr.jsp?type=set&id=" + docno + "&filepath=" + relativePath + "\">DublinCore Record </a> - " + "<a href=\"downloadDigmapFrbr.jsp?id=" + docno + "&filepath=" + originalRelativePath + "\">UNIMARC Record </a>)</td></tr>");
                        if (doc.getDocument().getValues("date") != null)
                            for (String date : doc.getDocument().getValues("date")) {
                                out.print("<tr><th>DATE</th><td>" + date + "</td></tr>");
                            }
                        if (doc.getDocument().getValues("creator") != null)
                            for (String creator : doc.getDocument().getValues("creator")) {
                                out.print("<tr><th>CREATOR</th><td>" + creator + "</td></tr>");
                            }
                        if (doc.getDocument().getValues("subject") != null)
                            for (String creator : doc.getDocument().getValues("subject")) {
                                out.print("<tr><th>SUBJECT</th><td>" + creator + "</td></tr>");
                            }
                        out.print("<tr><th class=\"appField\">DOCNO</th><td>" + docno + "</td></tr>");
                        out.print("<tr><th class=\"appField\">score</th><td> " + hits.score(i) + "</td></tr>");
                        out.print("<tr><th class=\"appField\">only text score</th><td> " + hits.textScore(i) + "</td></tr>");

                        out.print("<tr><th class=\"appField\">summary</th><td>" + hits.summary(i, "contentsStore") + "</td></tr>");
                        out.print("<tr><th class=\"appField\">ORIGINAL FILE</th><td> " + originalRelativePath + "</td></tr>");
                        out.print("<tr><th class=\"appField\">PROCESSED</th><td> " + relativePath + "</td></tr>");
                        if (doc.isTimeDoc()) {
                            int distanceYears = hits.timeDistanceYears(i);
                            int year = doc.getTimeYear();
                            TimeBox timeBox = doc.getTimeBox();
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
            catch (Exception ee) {
                out.println("<b><p>Error: " + ee + "</p></b>");
                ee.printStackTrace();
            }
        }
    %>
</div>
</td>

</tr>
</table>





<!--FOOTER FRBR Style-->
</fieldset>

<br><br>


<div class="clear"></div>
</div>
</div>
<div style="clear: both;"></div>
<div id="footer">
    <div id="footerContent">

        <nobr><a href="http://www.theeuropeanlibrary.org/portal/organisation/footer/copyright_en.html">© 2005-2009 The European Library</a></nobr>
    </div>
    <div id="subfooter"></div>
</div>
</div>
<!--End FRBR Style-->

</body>
</html>

<%
    }
%>