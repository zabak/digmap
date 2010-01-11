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
<%
    Properties props = new LocalProperties("digmapFrbr/conf.properties");
    Globals.DATA_DIR = props.getProperty("data.dir");
    Globals.INDEX_DIR = props.getProperty("indexes.dir");

    if (request.getParameter("xml") != null && (request.getParameter("xml").equals("frbr") || request.getParameter("xml").equals("true"))) {

        StringBuilder responseBuilder = new StringBuilder();


        responseBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        responseBuilder.append("<lgte>\n");
        responseBuilder.append("<request query=\"" + XmlUtils.escape(request.getParameter("q")) + "\"/>\n");
        if (request.getParameter("q") != null && request.getParameter("q").length() > 1)
        {
            responseBuilder.append("<response>\n");
            try {


                LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, Globals.INDEX_DIR + "/lm/version1/digmapFrbr");
                Set<String> notTokenizableFields = new HashSet<String>();
                notTokenizableFields.add("collection");
                notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                notTokenizableFields.add(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                Analyzer analyzer = new LgteAnalyzer(notTokenizableFields);
                //we pass the searcher to parser because in case of query expansion it will be needed
                String q = request.getParameter("q");
                if (request.getParameter("collection") != null && request.getParameter("collection").length() > 0)
                    q = "collection:" + request.getParameter("collection") + " AND (" + q + ")";
                LgteQuery query = LgteQueryParser.parseQuery(q, analyzer, searcher);
                if (query != null) {
                    LgteHits hits = searcher.search(query);
                    responseBuilder.append("<results totalResults=\"" + hits.length() + "\" showing=\""+request.getParameter("show")+"\" page=\"" + request.getParameter("page") + "\">\n");


                    int pageNumber = 1;
                    int startResult = 0;
                    int showNumber = 1000;
                    if (request.getParameter("page") != null) {
                        pageNumber = Integer.parseInt(request.getParameter("page"));
                    }
                    if (request.getParameter("show") != null) {
                        showNumber = Integer.parseInt(request.getParameter("show"));
                        showNumber = showNumber*50;
                        startResult = pageNumber * showNumber - showNumber;
                    }

                    for (int i = startResult; i < hits.length() && i < startResult + showNumber; i++) {
                        LgteDocumentWrapper doc = hits.doc(i);
                        String filepath = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                        String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                        responseBuilder.append("<record catalog=\"" + doc.get("collection") + "\" score=\"" + hits.score(i) + "\" id=\"" + docno + "\"/>");
                    }
                    searcher.close();
                    responseBuilder.append("</results>");
                }
            }
            catch (Exception ee)
            {
                responseBuilder = new StringBuilder();
                responseBuilder.append("<error>" + pt.utl.ist.lucene.utils.TextToHTMLEnconder.escapeHTML(ee.toString()) + "</error>");
                ee.printStackTrace();
            }
            responseBuilder.append("</response>\n");
        }
        else
        {
            responseBuilder = new StringBuilder();
            responseBuilder.append("<lgte>\n");
            responseBuilder.append("<response><error>no query</error></response>\n");
        }
        responseBuilder.append("</lgte>\n");

        if(request.getParameter("xml") != null && (request.getParameter("xml").equals("frbr") ))
        {
            URL url;
            URLConnection urlConnection;
            DataOutputStream outStream;
            DataInputStream inStream;
            String body = "request=" + URLEncoder.encode(responseBuilder.toString(), "UTF-8");
            url = new URL("http://localhost:8080/frbrCluster/Handler");
            urlConnection = url.openConnection();
            ((HttpURLConnection) urlConnection).setRequestMethod("POST");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
//            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            urlConnection.setRequestProperty("Content-Length","" + body.length());


            // Create I/O streams
            outStream = new DataOutputStream(urlConnection.getOutputStream());


            // Send request
            outStream.writeBytes(body);
            outStream.flush();


            inStream = new DataInputStream(urlConnection.getInputStream());

            byte[] bytes = new byte[1024];
            int read;
            StringBuilder builder = new StringBuilder();
            while((read = inStream.read(bytes)) >= 0)
            {
                String readed = new String(bytes,0,read,"UTF-8");
                builder.append(readed);
            }
            inStream.close();
            outStream.close();
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");
            PrintWriter pw = response.getWriter();
            pw.write(builder.toString());

        }
        else
        {
                response.setContentType("text/xml");
                PrintWriter pw = response.getWriter();
                pw.write(responseBuilder.toString());
        }
        
    }
    else if(request.getParameter("xml") != null && (request.getParameter("xml").equals("mitraStyle")))
    {

%>

<jsp:include page="resultsMitraStyle.jsp"/>


<%
    }
    else
    {

%>
<html>
<head>
    <title>Example Web Application for Search with Lucene GeoTemporal Extensions</title>
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

<h1 id="header_title">The European Library</h1>


</div>
</div>
<div id="navcontainer">
<ul id="navlist">
<li class="first active">
<a href="">SEARCH FRBR</a>

</li>
</ul>
</div>
<div id="subnavcontainer">
<div class="separatorBar">
<div style="float: right; margin-top: -3px; padding-right: 8px;">
<form action="index.jsp" method="get">
    <input type="hidden" name="show" value="50"/>
    <input type="hidden" name="page" value="1"/>
    <input type="hidden" name="xml" value="false"/>

<label for="mod_search_searchword" accesskey="?"></label><input value="search" name="verb" type="hidden"><input value="utf-8" name="encoding" type="hidden"><input value="true" name="html" type="hidden"><input value="1" name="collectionId" type="hidden"><input onblur="if(this.value=='') this.value='Search for...';" onfocus="if(this.value=='Search for...') this.value='';" value="Search for..." class="box" name="q" id="mod_search_searchword" type="text"> <input value="Search" class="button" type="submit">
</form>
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
        <form action="index.jsp" method="get">
            <table class="searchForm">
                <tr>
                    <td valign="top" class="column">
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
                                    Results
                                </td>
                            </tr>
                            <tr>
                                <td>Start in Page</td>
                                <td>
                                    <select name="page">
                                    <%
                                        for(int i = 1; i < 50; i++)
                                        {
                                    %>
                                           <option value="<%=i%>"><%=i%></option>
                                    <%
                                        }
                                    %>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>Response Type</td>
                                <td>
                                    <select name="xml">
                                        <option value="frbr">Cluster FRBR</option>
                                        <option value="mitraStyle">Search Engine Style</option>
                                        <option value="false">Result  in HTML (Engine RAW DATA)</option>
                                        <option value="true">Result in XML</option>


                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td height="40px"></td>
                            </tr>
                            <tr>
                                <td>
                                    Search for
                                </td>
                                <td>
                                    <input type="text" size="100" name="q" value="<%if(request.getParameter("q")!=null){out.print(request.getParameter("q"));}%>"/>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2"><input value="Search" class="button"  type="submit"/></td>

                            </tr>
                        </table>


                    </td>
                    <td valign="top" class="column">
                        <jsp:include page="collections.jsp"/>
                    </td>
                </tr>
            </table>


        </form>
        <h3>or you can try one of our test queries</h3>
        <table cellpadding="0" cellspacing="0" id="searches">
            <tr><td>You to search for literature</td><td><a href="index.jsp?q=literature">literature</a></td></tr>
            <tr><td>You can now try literature but in year 1965</td><td><a href="index.jsp?q=literature time:1965">literature time:1965</a></td></tr>
            <tr><td>You can filter results in time</td><td><a href="index.jsp?q=literature starttime:1960 endtime:1970 filter:t">literature starttime:1960 endtime:1970 filter:t</a></td></tr>
        </table>
        <div id="results">
        <%
            if (request.getParameter("q") != null && request.getParameter("q").length() > 1) {
        %>
        <h3>Results for '<%=request.getParameter("q")%>':</h3>
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
                    String q = request.getParameter("q");
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


<fieldset class="radius boxGray mainBox">

<b>Sources (ISO2709 / XML)</b>
<br>

<ul>

<li>The British Library - <a title="The British Library" href="http://digmap2.ist.utl.pt:8080/records_frbr/bl/source/biblebibfinal.lex">Bible</a> / <a title="The British Library" href="http://digmap2.ist.utl.pt:8080/records_frbr/bl/source/nobelbibfinal.lex">Nobel</a> / <a title="The British Library" href="http://digmap2.ist.utl.pt:8080/records_frbr/bl/source/authorities/biblenacofinal.lex">Authorities Bible</a> / <a title="The British Library" href="http://digmap2.ist.utl.pt:8080/records_frbr/bl/source/authorities/nobelNT.lex">Authorities Nobel</a></li>

<li>National Library of the Czech Republic - <a title="National Library of the Czech Republic" href="http://digmap2.ist.utl.pt:8080/records_frbr/cze/source/bible_bib.marc">Bible</a> / <a title="National Library of the Czech Republic" href="http://digmap2.ist.utl.pt:8080/records_frbr/cze/source/nobel_bib.marc">Nobel</a> / <a title="National Library of the Czech Republic" href="http://digmap2.ist.utl.pt:8080/records_frbr/cze/source/bible_aut.marc">Authorities Bible</a> / <a title="National Library of the Czech Republic" href="http://digmap2.ist.utl.pt:8080/records_frbr/cze/source/nobel_aut.marc">Authorities Nobel</a></li>

<li>German National Library - <a title="German National Library" href="http://digmap2.ist.utl.pt:8080/records_frbr/ger/source/bibel.mrc">Bible</a> / <a title="German National Library" href="http://digmap2.ist.utl.pt:8080/records_frbr/ger/source/nobel.mrc">Nobel</a></li>


<li>National Library of Latvia - <a title="National Library of Latvia" href="http://digmap2.ist.utl.pt:8080/records_frbr/lat/source/bib_bible.xml">Bible XML</a> / <a title="National Library of Latvia" href="http://digmap2.ist.utl.pt:8080/records_frbr/lat/source/bib_nobel.xml">Nobel XML</a> / <a title="National Library of Latvia" href="http://digmap2.ist.utl.pt:8080/records_frbr/lat/source/aut_bible.xml">Authorities Bible XML</a> / <a title="National Library of Latvia" href="http://digmap2.ist.utl.pt:8080/records_frbr/lat/source/aut_nobel.xml">Authorities Nobel XML</a> / <a title="National Library of Latvia" href="http://digmap2.ist.utl.pt:8080/records_frbr/lat/source/AuthorityRecordsLNPW.sav">Authority SAV</a></li>


<li>Martynas Mazvydas National Library of Lithuania - <a title="Martynas Mazvydas National Library of Lithuania" href="http://digmap2.ist.utl.pt:8080/records_frbr/lit/source/biblia_bibliographic_records_83.iso">Bible</a> / <a title="Martynas Mazvydas National Library of Lithuania" href="http://digmap2.ist.utl.pt:8080/records_frbr/lit/source/litNobel_prize_bibliographic_records_340.iso">Nobel</a> / <a title="Martynas Mazvydas National Library of Lithuania" href="http://digmap2.ist.utl.pt:8080/records_frbr/lit/source/Biblia_authority_records_26.iso">Authorities Bible</a> / <a title="Martynas Mazvydas National Library of Lithuania" href="http://digmap2.ist.utl.pt:8080/records_frbr/lit/source/litNobel_prize_authority_records_60.iso">Authorities Nobel</a></li>

<li>National Library of Portugal  - <a title="National Library of Portugal" href="http://digmap2.ist.utl.pt:8080/records_frbr/bnp/source/biblias.bibs.iso">Bible</a> / <a title="National Library of Portugal" href="http://digmap2.ist.utl.pt:8080/records_frbr/bnp/source/NOBAILITERATURA.iso">Nobel</a></li>


<li>The National Library of Russia - <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/bible.mrc">Bible</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Nobel%20Prize%20Bunin.mrc">Nobel Prize Bunin</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Nobel%20Prize%20Psternak.mrc">Nobel Prize Psternak</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Nobel%20Prize%20Sholohov.mrc">Nobel Prize Sholohov</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Nobel%20Prize%20Soldgenicyn.mrc">Nobel Prize Soldgenicyn</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Authority%20Bible.mrc">Authorities Bible</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Authority%20Bunin.mrc">Authority Bunin</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Authority%20Pasternak.mrc">Authority Pasternak</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Authority%20Soldgenicyn.mrc">Authority Soldgenicyn</a> / <a title="The National Library of Russia" href="http://digmap2.ist.utl.pt:8080/records_frbr/rus/source/Authority%20Solohov.mrc">Authority Solohov</a></li>


<li>National Library of Servia - <a title="SERBIA sources" href="http://digmap2.ist.utl.pt:8080/records_frbr/ser/source/bible24recordsmarc21.xml">Bible XML</a> / <a title="SERBIA sources" href="http://digmap2.ist.utl.pt:8080/records_frbr/ser/source/nobelprize.xml">Nobel XML</a></li>


<li>National Library of Spain -  <a title="National Library of Spain" href="http://digmap2.ist.utl.pt:8080/records_frbr/spa/source/BIBLIA2709.mrc">Bible</a> / <a title="National Library of Spain" href="http://digmap2.ist.utl.pt:8080/records_frbr/spa/source/Nobeles-utf8.mrc">Nobel</a> / <a title="National Library of Spain" href="http://digmap2.ist.utl.pt:8080/records_frbr/spa/source/bible_auth.mrk">Bible Authorities</a></li>


<li>Royal Library of Belgium  - <a title="Royal Library of Belgium" href="http://digmap2.ist.utl.pt:8080/records_frbr/kbr/source/bible.iso">Bible</a> / <a title="Royal Library of Belgium" href="http://digmap2.ist.utl.pt:8080/records_frbr/kbr/source/KBR-FRBR-nobel">Nobel</a></li>

<li>National Sz�ch�nyi Library of Hungary  - <a title="National Sz�ch�nyi Library of Hungary" href="http://digmap2.ist.utl.pt:8080/records_frbr/nsz/source/Biblia.mrc">Bible</a> / <a title="National Sz�ch�nyi Library of Hungary" href="http://digmap2.ist.utl.pt:8080/records_frbr/nsz/source/NobelBibNszl.mrc">Nobel</a> / <a title="National Sz�ch�nyi Library of Hungary" href="http://digmap2.ist.utl.pt:8080/records_frbr/nsz/source/NobelAuthNszl.mrc">Nobel Authorities</a></li>

</ul>
</fieldset>

<div class="clear"></div>
</div>
</div>
<div style="clear: both;"></div>
<div id="footer">
<div id="footerContent">

<nobr><a href="http://www.theeuropeanlibrary.org/portal/organisation/footer/copyright_en.html">� 2005-2009 The European Library</a></nobr>
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