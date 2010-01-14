<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.*"%>
<%@page import="pt.utl.ist.lucene.treceval.IndexCollections"%>
<%@ page import="pt.utl.ist.lucene.treceval.geotime.index.Config" %>
<%@ page import="pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument" %>
<%@ page import="pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression" %>
<%@ page import="pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument" %>
<%@ page import="java.util.*" %>
<html>
<head><title>Example Web Application for Search with Lucene GeoTemporal Extensions</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/popup.txt"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/utility.txt"></script>
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

    label.TIMEX
    {
        background-color:red;
    }
    #results
    {
    }
    .result {margin-top:10px}
    .form
    {
        margin:10px;
        border: 1px solid black;
        padding: 5px;
        background-color: lightgray;
    }
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

    .popupLink {
        outline: none
    }
    .closeLink
    {
        font-size: 22px;
        font-family: Verdana,Tahoma,Arial;color: black;
    }
    .popup {
        COLOR:black;
        BORDER-RIGHT: black 3px solid;
        PADDING-RIGHT: 3px;
        BORDER-TOP: black 1px solid;
        PADDING-LEFT: 3px; Z-INDEX: 10;
        VISIBILITY: hidden;
        PADDING-BOTTOM: 3px;
        BORDER-LEFT: black 1px solid;
        PADDING-TOP: 3px;
        BORDER-BOTTOM: black 3px solid;
        POSITION: absolute;
        BACKGROUND-COLOR: white;

    }

    #blanket {
        background-color:#111;
        opacity: 0.65;
        filter:alpha(opacity=65);
        position:absolute;
        z-index: 9001;
        top:0px;
        left:0px;
        width:100%;
    }
    .popUpDiv {
        position:absolute;
        background-color:#eeeeee;
        width:300px;
        height:300px;
        z-index: 9002;


</style>
<script type="text/javascript">
    <!--
    <%
        if(request.getParameter("topicID") != null)
        {
        %>
        canconfirm='true';
        <%
        } else {
        %>
        canconfirm='false';
        <%
        }
        %>
        -->
</script>
</head>
<body>
<%--<div id="blanket" style="display:none;"></div>--%>
<div id="header">
    <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img border="0"  height="80" src="lgtesmall.png" alt="LGTE"/></a> <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal">go to project home page</a> | <a href="indexDigmap.jsp">go to Georeferenced Collection</a> | <a href="http://digmap.googlecode.com/files/lgte1_0_1b.war">download LGTE last version</a>
</div>
<a href="index.jsp">go back</a>
<h3>Please enter your search query:</h3>

<p>try for example: <a href="indexNtcir.jsp?q=final flight of concorde">final flight of concorde</a></p>

<form name="SearchForm" action="indexNtcir.jsp" method="post">
    <div class="form">
        <input type="hidden" name="topicID" value=""/>
        <%
        String testQuery = "Worldwide Natural Disasters What are the natural disasters caused by abnormal phenomena, such as floods, earthquakes, and famines, that appear worldwide?";
        %>
        <textarea rows="10" cols="200" name="q" value="<%=request.getParameter("q") == null ? testQuery:request.getParameter("q")%>"></textarea> <br/>
        <input type="submit" value="Search Documents"/>
    </div>
</form>

<form class="box" action="indexNtcir.jsp" method="post">
    <div class="form">
        <table>
            <tr><td>TopicID</td><td><input type="text" size="200" name="topicID" value="NTCIR3-98-043" /></td></tr>
            <tr><td>Topic</td><td><input type="text" size="200" name="topic" value="Worldwide Natural Disasters"/> </td></tr>
            <tr><td>Topic Desc</td><td><textarea rows="5" cols="200" name="topicDesc">  What are the natural disasters caused by abnormal phenomena, such as floods, earthquakes, and famines, that appear worldwide?</textarea></td></tr>
            <tr><td>Topic Narr</td><td><textarea rows="5" cols="200" name="topicNarr">  Relevant documents include the name of the area where the abnormal phenomenon occurs, its details, and figures for the loss of lives and property damages. Without figures, the document is partially relevant. Documents without the name of the area or details about the abnormal phenomenon are not relevant</textarea></td></tr>
            <tr><td colspan="2"><input type="submit" value="Add Topic"/></td></tr>
        </table>
    </div>
</form>


<%

    Map<String, String> topics = (Map<String, String>) request.getSession().getAttribute("topics");
    Map<String, String> topicsDesc = (Map<String, String>) request.getSession().getAttribute("topicsDesc");
    Map<String, String> topicsNarr = (Map<String, String>) request.getSession().getAttribute("topicsNarr");

    Map<String, Map<String,String>> relevantTopicDoc = (Map<String, Map<String,String>>) request.getSession().getAttribute("relevantTopicDocs");
    if(relevantTopicDoc == null)
    {
        relevantTopicDoc = new HashMap<String, Map<String,String>>();
        request.getSession().setAttribute("relevantTopicDocs",relevantTopicDoc);
    }
    Map<String,String> topicJudgements = null;
    if(request.getParameter("topicID") != null && request.getParameter("topicID").length() > 0)
    {
        topicJudgements = relevantTopicDoc.get(request.getParameter("topicID"));
        if(topicJudgements == null)
        {
            topicJudgements = new HashMap<String, String>();
            relevantTopicDoc.put(request.getParameter("topicID"),topicJudgements);
        }
    }

    if (request.getParameter("topicOp") != null && request.getParameter("topicOp").equals("clean")) {
        relevantTopicDoc = new HashMap<String, Map<String,String>>();
        topics = new HashMap<String, String>();
        topicsDesc = new HashMap<String, String>();
        topicsNarr = new HashMap<String, String>();
        request.getSession().setAttribute("relevantTopicDocs", relevantTopicDoc);
        request.getSession().setAttribute("topics", topics);
        request.getSession().setAttribute("topicsDesc", topicsDesc);
        request.getSession().setAttribute("topicsNarr", topicsNarr);
    }
    System.out.println(request.getParameter("topicOp"));

    if (request.getParameter("topicOp") != null && request.getParameter("topicOp").equals("confirm")) {

        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = (String) params.nextElement();
            if(param.startsWith("NYT_"))
            {
                String relevance = request.getParameter(param);
                if(relevance != null && relevance.length() > 0)
                {
                    if(relevance.equals("relevant"))
                    {
                        topicJudgements.put(param,"1");
                    }
                    else if(relevance.equals("irrelevant"))
                    {
                        topicJudgements.put(param,"0");
                    }
                }
            }
        }

    } else if (request.getParameter("topic") != null && request.getParameter("topicID") != null) {
        if (topics == null) {
            topics = new HashMap<String, String>();
            topicsDesc = new HashMap<String, String>();
            topicsNarr = new HashMap<String, String>();
            request.getSession().setAttribute("topics", topics);
            request.getSession().setAttribute("topicsDesc", topicsDesc);
            request.getSession().setAttribute("topicsNarr", topicsNarr);
        }
        topics.put(request.getParameter("topicID"), request.getParameter("topic"));
        topicsDesc.put(request.getParameter("topicID"), request.getParameter("topicDesc"));
        topicsNarr.put(request.getParameter("topicID"), request.getParameter("topicNarr"));
    }


    if (topics != null && topics.size() > 0) {
%>
<form action="indexNtcir.jsp" method="post">
<div class="form">
    <p>After a search choose a Topic to judge and mark te relevant documents:</p>
    <%
        for(Map.Entry<String,String> entry: topics.entrySet())
        {
            String checked = "";
            if(request.getParameter("topicID") != null && entry.getKey().equals(request.getParameter("topicID")))
            {
                checked = "checked=\"true\"";
            }
    %>

    <p><input <%=checked%> onclick="document.canconfirm=true;document.SearchForm.topicID.value=this.value" type="radio" name="topicID" value="<%=entry.getKey()%>"/> <%=entry.getKey()%> - <a href="indexNtcir.jsp?topicID=<%=entry.getKey()%>&q=<%=entry.getValue() + " " + topicsDesc.get(entry.getKey())%>"><%=entry.getValue()%></a></p>
    <%
        }
    %>
    <input type="hidden" name="topicOp" value="clean"/>
    <input type="button" onclick="if(confirm('Are you sure about cleaning all judgments?')){this.form.topicOp.value='clean';this.form.submit()}" value="Clean Topics"/>
    <input type="button" onclick="if(canconfirm == 'false'){alert('Please choose a Topic');}else if(confirm('Are you sure about confirm these judgements all judgments?')){this.form.topicOp.value='confirm';this.form.submit()}" value="Confirm Judgements"/>


</div>
<div class="form">
    <p><a href="<%=request.getContextPath()%>/exportNtcirJudgments.jsp" target="_blank">Export my judgments</a></p>
</div>

<%
    }





    if (request.getParameter("q") != null && request.getParameter("q").length() > 1) {






%>
<h3>Results for '<%=request.getParameter("q")%>':</h3>
<hr>
<%
    try {
        LgteIndexSearcherWrapper searcher = Config.openMultiSearcher();
        Analyzer analyzer = IndexCollections.en.getAnalyzerWithStemming();
//                LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), analyzer);
        System.out.println("Searching for: " + request.getParameter("q"));
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.getQueryProperties().put("bm25.k1", "1.2d");
        queryConfiguration.getQueryProperties().put("bm25.b", "0.75d");
        queryConfiguration.getQueryProperties().put("bm25.k3", "0.75d");
        LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), searcher, analyzer, queryConfiguration);
        LgteHits hits = searcher.search(query);

//                LgteHits hits = searcher.search(request.getParameter("q"),analyzer);


        out.println("<h3>Number of matching documents = " + hits.length() + "</h3>");
        for (int i = 0; i < 50; i++) {
            LgteDocumentWrapper doc = hits.doc(i);
            String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);

            String sgmlWithOutTags = doc.get(Config.TEXT_DB);
            String placeMaker = doc.get(Config.GEO_DB);
            String timexes = doc.get(Config.TEMPORAL_DB);
            StringBuilder annotatedText = new StringBuilder();
            int pos = 0;
            if (placeMaker != null) {
                PlaceMakerDocument placeMakerDocument = new PlaceMakerDocument(placeMaker);

            }
            if (timexes != null) {

                TimexesDocument timexesDocument = new TimexesDocument(timexes);
                System.out.println(docno);
                List<Timex2TimeExpression> timexesList = timexesDocument.getTimex2TimeExpressions();
                Collections.sort(timexesList, new Comparator<Timex2TimeExpression>() {

                    public int compare(Timex2TimeExpression o1, Timex2TimeExpression o2) {
                        if(o1.getStartOffset() > o2.getStartOffset())
                            return 1;
                        else if(o1.getStartOffset() < o2.getStartOffset())
                            return -1;
                        else return 0;

                    }
                }
                );
                int lastOffset = 0;
                for (Timex2TimeExpression timex2 : timexesDocument.getTimex2TimeExpressions()) {
                    if (lastOffset <= timex2.getStartOffset() && timex2.getStartOffset() < timex2.getEndOffset() && timex2.getStartOffset() < sgmlWithOutTags.length() && timex2.getEndOffset() < sgmlWithOutTags.length() && timex2.getStartOffset() > 0 && timex2.getEndOffset() > 0) {
                        System.out.println(timex2.getStartOffset() + ":" + timex2.getEndOffset());
                        annotatedText.append(sgmlWithOutTags.substring(pos, timex2.getStartOffset()));
                        annotatedText.append("<label class=\"TIMEX\">");
                        annotatedText.append(sgmlWithOutTags.substring(timex2.getStartOffset(), timex2.getEndOffset() + 1));
                        annotatedText.append("</label>");
                        lastOffset = timex2.getEndOffset();
                        pos = timex2.getEndOffset() + 1;
                    }
                }
                if (sgmlWithOutTags.length() > pos + 1)
                    annotatedText.append(sgmlWithOutTags.substring(pos + 1));
            }


            String fileAnnotatedPath; //doc.get(SentenceCleanTagger.output + "\\" + docno.substring(0, 14) + ".sentences.zip");
//                    out.print("<p><b>TITLE</b>: <a href=\"downloadCran.jsp?docno=" + docno + "&filepath=" + filepath + "\">" + doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE) + "</a></p>");
            String style="";
            String relevance = "";
            String relevantSelected = "";
            String irrelevantSelected = "";
            System.out.println("TOPICID " + request.getParameter("topicID"));
            if(request.getParameter("topicID") != null && request.getParameter("topicID").length() > 0)
            {

                topicJudgements = relevantTopicDoc.get(request.getParameter("topicID"));
                System.out.println("JUDGMENTS: " + topicJudgements.size());
                if(topicJudgements.get(docno) != null)
                {

                    relevance =  topicJudgements.get(docno);
                    System.out.println("Found DOCNO: " + relevance);
                    if(relevance!= null)
                    {
                        if(relevance.equals("0"))
                        {
                            style="style=\"background-color:red\"";
                            irrelevantSelected = " selected";
                        }
                        else
                        {
                            style="style=\"background-color:green\"";
                            relevantSelected = " selected";
                        }
                    }
                }
            }
            out.print("<table><tr><td " + style + " id=\"table" + docno + "\"><a name=\"" + docno + "\">\n");
            out.print("<p>" + i + " - <b>DOCNO</b> " + docno + "</p>\n");

            out.print("<p><b>score</b>: " + hits.textScore(i) + "</p>\n");
            out.print("<p><b>summary</b>" + hits.summary(i, pt.utl.ist.lucene.Globals.LUCENE_DEFAULT_FIELD, 100, 4) + "</p>\n");
//            out.print("<p><a onclick=\"popup('doc" + i +"');\" href=\"#\">Show Anotations:</a></p>\n");
            out.print("<p><a onclick=\"getObjectById('table" + docno+ "').style.backgroundColor='lightgray';return !showPopup('doc" + i + "', event);\" href=\"#\">Show Anotations:</a></p>\n");
            out.print("</td><td>\n");
            if(topics.size() > 0)
            {

%>
<select onchange="if(this.value == 'relevant'){getObjectById('table<%=docno%>').style.backgroundColor='green';}else if(this.value == 'irrelevant'){getObjectById('table<%=docno%>').style.backgroundColor='red';}" name="<%=docno%>">
    <option value="">Choose relevance</option>
    <option value="relevant" <%=relevantSelected%>>relevant</option>
    <option value="irrelevant" <%=irrelevantSelected%>>irrelevant</option>
</select>

<%           }
    out.print("</td</tr></table>\n");
%>

<%--<div class="popUpDiv" id="<%="doc" + i%>" style="display:none;">--%>
<div class="popup" id="doc<%=i%>" onclick="event.cancelBubble = true;">
    [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]<br>
    <%=annotatedText.toString().replace("\n","<br>")%>
    [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]
    <%--[<A onclick="popup('<%="doc" + i%>');" href="#"><font size="4">Close</font></A>]--%>
    <%
        if(topics.size() > 0)
        {
    %>
    <input type="button" value="Mark relevant" onclick="this.form['<%=docno%>'].value='relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='green';window.location='#<%=docno%>';"/>
    <input type="button" value="Mark irrelevant" onclick="this.form['<%=docno%>'].value='irrelevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='red';window.location='#<%=docno%>';"/>
    <%
        }
    %>
</div>
<hr>
<%
                //                out.print("<div id=\"doc" + i + "\" style=\"display:none;\">" + annotatedText.toString().replace("\n","<br>"));
//                out.print("<p><a href=\"javascript:showOrHideOne('doc"+ i + "')\">Close Notes: " + docno  + "</a></p>\n");
//                out.print("</div>\n");
//                out.print("<hr>\n");
            }
            searcher.close();
        }
        catch (Exception ee) {
            out.println("<b><p>Error: " + ee + "</p></b>");

            ee.printStackTrace();
        }
    }
%>
</form>
<jsp:include page="footer.jsp"/>
</body>
</html>