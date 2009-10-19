<%@ page import="java.util.Set" %>
<%@ page import="pt.utl.ist.lucene.treceval.Globals" %>
<%@ page import="org.apache.lucene.analysis.Analyzer" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="pt.utl.ist.lucene.analyzer.LgteAnalyzer" %>
<%@ page import="pt.utl.ist.lucene.*" %>
<%@ page import="pt.utl.ist.lucene.forms.TimeBox" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>





<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/navigation.css">
<link rel="stylesheet" type="text/css" href="css/page_layout.css">
<link rel="stylesheet" type="text/css" href="css/styles.css">
<link rel="stylesheet" type="text/css" href="css/mitra.css">
<link rel="stylesheet" type="text/css" href="jsp/templates/frbr/styles/mitra.css">
<link type="image/x-icon" href="http://portal.digmap.eu/images/digmap.ico" rel="shortcut icon">

<title>FRBP Search Engine - Results</title>
<script type="text/javascript" src="/searchFrbr/js/functions.js"></script><script type="text/javascript" src="/searchFrbr/js/ajax.js"></script>
</head>
<body class="twoColElsLtHdr">
<div style="left: 0px;" id="container">
<div id="supranav">

<table cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>
<td><img src="images/supranav_left.gif" align="absmiddle" width="30" height="30"></td><td id="supranav_select">

</td>
</tr>
</tbody>
</table>
</div>
<div id="headerTel">
<div id="brand">
<a href="http://search.theeuropeanlibrary.org/portal/"><img src="images/spacer.gif" align="absmiddle" width="220" height="50" hspace="20" border="0"></a>

<h1 id="header_title">The European Library</h1>
</div>
</div>
<div id="navcontainer">
<ul id="navlist">
<li class="first active">

<a href="">Search</a>
</li>
</ul>
</div>


        <%
            if (request.getParameter("q") != null && request.getParameter("q").length() > 1) {
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
                    String collUser = request.getParameter("collection");
                    if(collUser == null || collUser.length() == 0)
                        collUser = "all";
                    String q = request.getParameter("q");
                    if (request.getParameter("collection") != null && request.getParameter("collection").length() > 0)
                        q = "collection:" + request.getParameter("collection") + " AND (" + q + ")";
                    LgteQuery query = LgteQueryParser.parseQuery(q, analyzer, searcher);
                    if (query != null) {

                        System.out.println("Searching for: " + query.getQuery().toString());
                        LgteHits hits = searcher.search(query);

                        int startResult = 0;
                        int showNumber = 100;
                        int pageNumber = 0;
                        try
                        {
                            if(request.getParameter("page") != null)
                                pageNumber = Integer.parseInt(request.getParameter("page"));
                        }
                        catch(NumberFormatException e)
                        {

                        }
                        if (request.getParameter("page") != null && request.getParameter("show") != null) {
                            showNumber = Integer.parseInt(request.getParameter("show"));
                            startResult = pageNumber * showNumber - showNumber;
                        }

%>

<div id="subnavcontainer">
    <div class="separatorBar">
        <div class="navResults">
            Results:
            <span class="bold"><%=((pageNumber-1) * showNumber)%></span>

            -
            <span class="bold"><%=(((pageNumber-1) * showNumber) + showNumber)%></span>  of  <span class="bold"><%=hits.length()%></span>  for  <span class="bold"><%=request.getParameter("q")%></span>
        </div>
        <div class="navSearch">
            <form class="nospace" method="GET" action="index.jsp?show=20&page=1&xml=mitraStyle">
                    ... <%=collUser%>
                <input name="page" type="hidden" value="<%=request.getParameter("page")%>">
                <input name="show" type="hidden" value="<%=request.getParameter("show")%>">
                <input name="type" type="hidden" value="<%=request.getParameter("type")%>">
                <input name="xml" type="hidden" value="<%=request.getParameter("xml")%>">
                <input name="collection" type="hidden" value="<%=request.getParameter("collection")%>">
                <input type="text" name="q" style="width:200px" value="<%=request.getParameter("q")%>"> <input type="submit" value="Search">


            </form>
        </div>
    </div>
</div>
<table cellspacing="0" id="bigtable">

    <tr>
        <td id="leftNav"></td>
        <td id="bigtableCenter">
            <table width="100%" cellspacing="0">
                <tr>
                    <td id="topNav"></td>
                </tr>
                <tr>
                    <td id="bigtableResults">

                    <br>

                      <h3>Results for '<%=request.getParameter("q")%>':</h3>

<%

                        for (int i = startResult; i < hits.length() && i < startResult + showNumber; i++)
                        {
                            LgteDocumentWrapper doc = hits.doc(i);
                            String filepath = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH);
                            String originalRelativePath = doc.get("originalRelativePath");
                            String relativePath = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_RELATIVE_DATA_DIR_PATH);
                            String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
                            String collection = doc.get("collection");
//                            double distance = hits.spaceDistanceKm(i);
%>
                            <table cellspacing="0" cellpadding="0" class="tableItems">
                                <tr>

                                    <td align="center" valign="top"></td><td valign="top" width="100%">
                                        <table cellPadding="0" cellSpacing="0">
                                            <tr>
                                                <td class="tagTitle"><span class="bold">Title</span></td><td class="tagContent"><label class="big"><%=doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE)%></label></td>
                                            </tr>
                                            <%if(doc.getDocument().getValues("creator") != null)
                                            for (String creator : doc.getDocument().getValues("creator"))
                                            {
%>
                                                <tr>
                                                    <td class="tagTitle"><span class="bold">Author</span></td><td class="tagContent"><%=creator%></td>
                                                </tr>
<%
                                            }
                                            if(doc.getDocument().getValues("date") != null)
                                            for (String date : doc.getDocument().getValues("date"))
                                            {
%>
                                                <tr>
                                                    <td class="tagTitle"><span class="bold">Date</span></td><td class="tagContent"><%=date%></td>
                                                </tr>
<%
                                            }
%>
                                            <tr>
                                                <td class="tagTitle">Summary</td><td class="tagContent"><u></u> -  <%=hits.summary(i, "contentsStore")%></td>
                                            </tr>
                                        </table>
                                    </td>
                                    <td align="center" valign="top">
                                        <fieldset style="border: 2px solid #eeeeee; margin: 0px 5px 0px 0px; background-color: rgb(255, 255, 255);" class="radius boxGray mainBox">

                                        <div class="viewRecordBox">
                                        <img alt="nobel" src="images/nobel_prize.gif"><br>
                                        <a class="workSubLink" href="downloadDigmapFrbr.jsp?id=<%=docno%>&filepath=<%=originalRelativePath%>" target="_blank"><span class="bold">View Detailed Record</span></a>
                                        <%--<a class="workSubLink" href="downloadDigmapFrbr.jsp?type=set&id=<%=docno%>&filepath=<%=relativePath%>" target="_blank"><span class="bold">Dublin Core</span></a>--%>
                                        </div>
                                        </fieldset>
                                    </td>
                                    <td width="10px"> </td>
                                </tr>
                                <tr>
                                    <td colspan="5">
                                        <hr>
                                    </td>
                                </tr>
                            </table>
<%

//                            double latitude = doc.getLatitude();
//                            double longitude = doc.getLongitude();
//                            int year = doc.getTimeYear();
//                            TimeBox timeBox = doc.getTimeBox();
//                            UnknownForm unknownForm = doc.getUnknownForm();
//
//                            int distanceYears = hits.timeDistanceYears(i);
                           

//                            if (doc.getDocument().getValues("subject") != null)
//                                for (String creator : doc.getDocument().getValues("subject")) {
//                                    out.print("<tr><th>SUBJECT</th><td>" + creator + "</td></tr>");
//                                }
//                            out.print("<tr><th class=\"appField\">DOCNO</th><td>" + docno + "</td></tr>");
//                            out.print("<tr><th class=\"appField\">score</th><td> " + hits.score(i) + "</td></tr>");
//                            out.print("<tr><th class=\"appField\">only text score</th><td> " + hits.textScore(i) + "</td></tr>");
//
//                            out.print("<tr><th class=\"appField\">summary</th><td>" + hits.summary(i, "contentsStore") + "</td></tr>");
//                            out.print("<tr><th class=\"appField\">ORIGINAL FILE</th><td> " + originalRelativePath + "</td></tr>");
//                            out.print("<tr><th class=\"appField\">PROCESSED</th><td> " + relativePath + "</td></tr>");
//                            if (doc.isTimeDoc()) {
//                                int distanceYears = hits.timeDistanceYears(i);
//                                int year = doc.getTimeYear();
//                                TimeBox timeBox = doc.getTimeBox();
//                                out.print("<tr><th class=\"time\">TIME</th><td> " + year + "</td></tr>");
//                                out.print("<tr><th class=\"time\">DISTANCE (Years)</th><td> " + distanceYears + "</td></tr>");
//                                out.print("<tr><th class=\"time\">TIME SCORE</th><td> " + hits.timeScore(i) + "</td></tr>");
//                                if (timeBox != null)
//                                    out.print("<tr><th class=\"time\">TIMEBOX</th><td> " + timeBox.getStartTimeYear() + " - " + timeBox.getEndTimeYear() + " </td></tr>");
//                            }
//                            out.print("</table></div>");
                        }

                        if(showNumber != hits.length())
                        {
                    %>
                        <h4>The page navigation is NOT IN USE. Please use the <a href="index.jsp">open form</a> and define yourself the page to display</h4>
                    <%

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




                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<div id="footer">
<div id="footerContent">
<nobr><a href="http://www.theeuropeanlibrary.org/portal/organisation/footer/copyright_en.html">&copy; 2005-2009 The European Library</a></nobr>
</div>
<div id="subfooter"></div>
</div>
</div>

</body>
</html>







