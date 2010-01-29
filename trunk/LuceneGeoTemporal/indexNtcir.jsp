<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.*"%>
<%@page import="pt.utl.ist.lucene.treceval.IndexCollections"%>
<%@ page import="pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument" %>
<%@ page import="pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression" %>
<%@ page import="pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="java.io.File" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="pt.utl.ist.lucene.config.ConfigProperties" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="pt.utl.ist.lucene.treceval.geotime.NyTimesDocument" %>
<%@ page import="java.io.StringReader" %>
<%@ page import="org.apache.lucene.index.IndexReader" %>
<%@ page import="pt.utl.ist.lucene.treceval.geotime.index.*" %>
<%@ page import="org.apache.lucene.index.LgteIsolatedIndexReader" %>
<%@ page import="pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer" %>
<%@ page import="pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer" %>
<html>
<head>
<title>NTCIR/GeoTime - Tool fot Relevance Judgements creation with Lucene GeoTemporal Extensions (LGTE) AT GeoTime NTCIR</title>
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

    label.PLACE
    {
        background-color:lightcoral;
    }

    label.TIMEX
    {
        background-color:lightskyblue;
    }
    label.word
    {
        background-color:yellow;
    }
    #results
    {
    }
    .result {margin-top:10px}
	form
	{
		margin:0;
		padding:0;
	}
    .form
    {
        border: 1px solid black;
        padding: 3px;
        background-color: #eeeeee;
	}
	
	
	.searchForm
    {
        border-top: 1px solid black;
        border-left: 1px solid black;
        border-right: 1px solid black;
		padding-top:10px;
		padding-bottom:10px;
        padding-left: 3px;
		padding-right: 3px;
        background-color: #eeeeee;
		border-bottom: 1px solid black;
    
	}
	#adminbox
	{
		background-color: #dddddd;
		border:1px solid gray;
	}
	#adminboxContent
	{
        border-top:1px solid gray;
		padding-left:3px;
		padding-right:3px;
	}
    #adminbox .menu td
    {
		padding:3px;
		border-left:1px solid gray;
    }
	#adminbox .menu
    {

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
    <table width="100%">
        <tr>
            <td>
                <a href="http://metadata.berkeley.edu/NTCIR-GeoTime/"><img alt="NTCIR GeoTime" border="0" src="ntcir.gif"><label style="font-size:24px">GeoTime</label></a>  <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal"><img border="0"  height="80" src="lgtesmall.png" alt="LGTE"/></a>
            </td>
            <td align="right">
                <a href="http://www.inesc-id.pt"><img width="100px" border="0" src="inesc-id.jpg" alt="INESC-ID Lisbon"></a> <a href="http://www.ist.utl.pt"><img width="50px"  border="0" src="ist.jpg" alt="Instituto Superior Técnico"></a> <a href="http://www.estgp.pt"><img width="50px" src="estg-ipp.jpg" border="0" alt="Escola Superior de Tecnologia e Gestão de Portalegre"></a>                
            </td>
        </tr>
    </table>
    <a href="http://code.google.com/p/digmap/wiki/LuceneGeoTemporal">go to project home page</a> | <a href="http://digmap2.ist.utl.pt:8080/mitra">Digmap Collection</a> | <a href="http://digmap2.ist.utl.pt:8080/lgte/">DEMO Online</a>
</div>

<%
    if(request.getParameter("agree")!=null)
    {
        System.out.println(new java.util.Date() + " : NTCIR AGREE : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "]");
        request.getSession().setAttribute("agree","true");
    }
    if(request.getSession().getAttribute("agree")==null)
    {
%>


<div class="form">
    <form action="indexNtcir.jsp" method="post">
    <h2>All operations performed in this tool (queries, relevance judgements, topic management) will be saved in the system log for future investigation purposes. 	<br>Please click in the box below if you agree with these conditions in order to proceed.</h2>

    <p><input type="checkbox" name="agree"> I declare that I agree with these conditions and I want start use the system</p>
    <input type="button" value="I declare that I agree with these conditions" onclick="if(!this.form.agree.checked){alert('You need to agree with the conditions in order to proceed.');}else{this.form.submit();}">
    </form>
</div>
<%
    }
    else
    {
%>


<p>The LGTE tool was developed at Technical University of Lisbon under the DIGMAP project (Project co-funded by the Community programme <a class="footerlink" href="http://europa.eu.int/information_society/activities/econtentplus/" title="eContentplus"> eContent<em>plus</em></a>) and provides a set of services over the GeoTime collection: manage topics, search the documents (marking with colors the time expressions and the places names found in text), create relevance judgements and finally import and export the judgements in the treceval format. </p>
<p>Please choose the desired topic before submit a search in order to join the judgements of the results with the selected topic. You can use the text you want in the search. You don't need to create judgements to all the results returned but you need to click in <b>"confirm judgements"</b> in order to save them in the session. You could also click in "copy to query" to copy the description and the narrative from the list, that search will use the description and the narrative fields of the topic.</p>
<p>Please use the export/import service to save your work. This application is using only the server session to keep the judgements which expire after 30 minutes of inactivity.</p>
<p>The export format should be used in the import proccess and is based in the assessements format used in the treceval tool plus the topic id, description and narrative.</p>
<p><i>contact: Jorge Machado [machadofisher AT gmail DOT com]</i></p>

<%
    Map<String, String> topics = (Map<String, String>) request.getSession().getAttribute("topics");
    Map<String, String> topicsDesc = (Map<String, String>) request.getSession().getAttribute("topicsDesc");
    Map<String, String> topicsNarr = (Map<String, String>) request.getSession().getAttribute("topicsNarr");

	if(topics == null || topics.size()==0)
	{
		
        topics = new HashMap<String, String>();
        topicsDesc = new HashMap<String, String>();
        topicsNarr = new HashMap<String, String>();
        
        request.getSession().setAttribute("topics", topics);
        request.getSession().setAttribute("topicsDesc", topicsDesc);
        request.getSession().setAttribute("topicsNarr", topicsNarr);

		//meter os topicos do NTCIR
		topics.put("GeoTime-0001","When and where did Astrid Lindgren die");
		topicsDesc.put("GeoTime-0001","The user wants to know when and in what city the children's author Astrid Lindgren died.");
		topicsNarr.put("GeoTime-0001","");

		topics.put("GeoTime-0002","When and where did Hurricane Katrina make landfall in the United States?");
		topicsDesc.put("GeoTime-0002","The user would like to know where and when the hurricane named Katrina, which caused extensive damage in the south-western United States, made landfall. The names of states where landfall came is acceptable, but the year alone is insufficient.");
		topicsNarr.put("GeoTime-0002","");

		topics.put("GeoTime-0003","When and where did Paul Nitze die?");
		topicsDesc.put("GeoTime-0003","The user wants to know when Paul Nitze, former USA Defense Secretary died and where he died.");
		topicsNarr.put("GeoTime-0003","");

		topics.put("GeoTime-0004","When and where did the SARS epidemic begin?");
		topicsDesc.put("GeoTime-0004","The user is investigating the SARS (Severe acute respiratory syndrome) epidemic caused by a new strain of virus. The user wants to know when the virus was first detected and in what province of China.");
		topicsNarr.put("GeoTime-0004","");

		topics.put("GeoTime-0005","When and where did Katharine Hepburn die?");
		topicsDesc.put("GeoTime-0005","The user is investigating the actress Katharine Hepburn and wants to know when and where she died.");
		topicsNarr.put("GeoTime-0005","");

		topics.put("GeoTime-0006","When and where did anti-government demonstrations occur in Uzbekistan?");
		topicsDesc.put("GeoTime-0006","The user wants to know what month and year an anti-government riot took place in Uzbekistan that was put down by military force. The user also wants to know where in Uzbekistan this took place.");
		topicsNarr.put("GeoTime-0006","");

		topics.put("GeoTime-0007","How old was Max Schmeling when he died, and where did he die?");
		topicsDesc.put("GeoTime-0007","The user wants to know where the German boxer Max Schmeling died and how old he was when he died.");
		topicsNarr.put("GeoTime-0007","");

		topics.put("GeoTime-0008","When and where did Chechen rebels take Russians hostage in a theatre?");
		topicsDesc.put("GeoTime-0008","The user would like to know when Chechen rebels terrorized and held hostage Russian theatre-goers. Also where did the hostage incident take place?");
		topicsNarr.put("GeoTime-0008","");

		topics.put("GeoTime-0009","When and where did Rosa Parks die?");
		topicsDesc.put("GeoTime-0009","The user would like to know where and when the American civil rights activist Rosa Parks died.");
		topicsNarr.put("GeoTime-0009","");

		topics.put("GeoTime-0010","When was the decision made on siting the ITER and where is it to be built?");
		topicsDesc.put("GeoTime-0010","The ITER (International Thermonuclear Experimental Reactor) is an experimental facility for conducting international joint research on the feasibility of fusion energy. When was the decision made on where to build the facility and where is it to be sited?");
		topicsNarr.put("GeoTime-0010","");

		topics.put("GeoTime-0011","Describe when and where train accidents occurred which had fatalities in the period 2002 to 2005.");
		topicsDesc.put("GeoTime-0011","The user wants to know about train accidents in which people died as a result. Where did these train disasters take place and when, between 2002 and 2005?");
		topicsNarr.put("GeoTime-0011","");

		topics.put("GeoTime-0012","When and where did Yasser Arafat die?");
		topicsDesc.put("GeoTime-0012","The user wants to know about Palestinian leader Yasser Arafat, in particular when he died and where.");
		topicsNarr.put("GeoTime-0012","");

		topics.put("GeoTime-0013","What Portuguese colony was transferred to China and when?");
		topicsDesc.put("GeoTime-0013","Sometime in the past few years a former Portuguese colony had its sovereignty transferred to China. The user wants to know the name of the colony and when the transfer took place.");
		topicsNarr.put("GeoTime-0013","");

		topics.put("GeoTime-0014","When and where did a volcano erupt in Africa during 2002?");
		topicsDesc.put("GeoTime-0014","The user would like to know the date in 2002 in which a volcano erupted in Africa. What was the name of the volcano and in which country is it located?");
		topicsNarr.put("GeoTime-0014","");

		topics.put("GeoTime-0015","What American football team won the Superbowl in 2002, and where was the game played?");
		topicsDesc.put("GeoTime-0015","The user is interested in the American football Superbowl game in 2002.  In what city was it played?");
		topicsNarr.put("GeoTime-0015","");

		topics.put("GeoTime-0016","When and where were the last three Winter Olympics held?");
		topicsDesc.put("GeoTime-0016","The Winter Olympics are held every four years.  In which year and in what city were the last three olympics held?");
		topicsNarr.put("GeoTime-0016","");

		topics.put("GeoTime-0017","When and where was a candidate for president of a democratic South American country kidnapped by a rebel group?");
		topicsDesc.put("GeoTime-0017","The user wants to know  in which country and what date a presidential candidate in South America was kidnapped?");
		topicsNarr.put("GeoTime-0017","");

		topics.put("GeoTime-0018","What date was a country was invaded by the United States in 2002?");
		topicsDesc.put("GeoTime-0018","The United States invaded another country in 2002.  The user wants to know the exact date of the invasion and what country was invaded");
		topicsNarr.put("GeoTime-0018","");

		topics.put("GeoTime-0019","When and where did the funeral of Queen Elizabeth (the Queen Mother) take place?");
		topicsDesc.put("GeoTime-0019","The Queen mother, Elizabeth, of the United Kingdom died in 2002.  The user wants to know the exact date and location of her funeral");
		topicsNarr.put("GeoTime-0019","");

		topics.put("GeoTime-0020","What country is the most recent to join the UN and when did it join?");
		topicsDesc.put("GeoTime-0020","A European country finally joined the United Nations.  The user wants to know which country it is and when it joined.");
		topicsNarr.put("GeoTime-0020","");

		topics.put("GeoTime-0021","When and where were the 2010 Winter Olympics host city location announced?");
		topicsDesc.put("GeoTime-0021","The International Olympic Committee decides when and where the next Winter Olympics is held.  When was this announcement made for the next Winter Olympics,  and from what city was it made?");
		topicsNarr.put("GeoTime-0021","");

		topics.put("GeoTime-0022","When and where did a massive earthquake occur in December 2003?");
		topicsDesc.put("GeoTime-0022","A massive earthquake occurred in December 2003.  The user would like to know when this took place and what city and country suffered this quake");
		topicsNarr.put("GeoTime-0022","");


		topics.put("GeoTime-0023","When did the largest expansion of the European Union take place, and which countries became members?");
		topicsDesc.put("GeoTime-0023","The European Union added ten new member states.  Which countries were added and when did this addition take place?]");
		topicsNarr.put("GeoTime-0023","");

		topics.put("GeoTime-0024","When and what country has banned cell phones?");
		topicsDesc.put("GeoTime-0024","Only one country in the world forbids mobile phones.  Which one is it and when did the ban take place?");
		topicsNarr.put("GeoTime-0024","");


		topics.put("GeoTime-0025","How long after the Sumatra earthquake did the tsunami hit Sri Lanka?");
		topicsDesc.put("GeoTime-0025","The largest earthquake in recent times occurred off the coast of Sumatra in 2005.  The earthquake caused a massive tsunami which spread across the Indian Ocean.  The user would like to know how long it took the tsunami to reach Sri Lanka.  An somewhat indefinite answer like 'a few days' is acceptable.");
		topicsNarr.put("GeoTime-0025","");
	}

    Map<String, Map<String, String>> relevantTopicDoc = (Map<String, Map<String, String>>) request.getSession().getAttribute("relevantTopicDocs");
    if (relevantTopicDoc == null) {
        relevantTopicDoc = new HashMap<String, Map<String, String>>();
        request.getSession().setAttribute("relevantTopicDocs", relevantTopicDoc);
      /*  topics = new HashMap<String, String>();
        topicsDesc = new HashMap<String, String>();
        topicsNarr = new HashMap<String, String>();

        request.getSession().setAttribute("topics", topics);
        request.getSession().setAttribute("topicsDesc", topicsDesc);
        request.getSession().setAttribute("topicsNarr", topicsNarr); */
    }

    if (ServletFileUpload.isMultipartContent(request)) {
        System.out.println(new java.util.Date() + " : NTCIR IMPORT : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "]");
        try {
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            List items = upload.parseRequest(request); /* FileItem */

            File repositoryPath = new File(ConfigProperties.getProperty("output.tmp.dir"));
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            diskFileItemFactory.setRepository(repositoryPath);

            Iterator iter = items.iterator();
            for (Object item1 : items) {
                FileItem item = (FileItem) item1;
                BufferedReader reader = new BufferedReader(new InputStreamReader(item.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.trim().length() == 0)
                    {
                    }
		    else if(line.startsWith("##"))
		    {
		    }
                    else if(line.startsWith("#TOPIC$$")) 
		    {
                        int startId = "#TOPIC$$".length();
                        int endId = line.indexOf("$$", startId);
                        int startType = endId + 2;
                        int endType = line.indexOf(":", startType);
                        String docId = line.substring(startId, line.indexOf("$$", startId));
                        String type = line.substring(startType, endType);
                        String text = line.substring(line.indexOf(":",endType)+1);
//                        if(type.equals("TITLE"))
//                            topics.put(docId,text);
//                         else
                        if(type.equals("DESC"))
                            topics.put(docId,text);
                        else if(type.equals("NARR"))
                            topicsDesc.put(docId,text);
                    }
                    else
                    {
                        int firstSpace = line.indexOf(" ");
                        int secondSpace = line.indexOf(" ",firstSpace+1);
                        int thirdSpace = line.indexOf(" ",secondSpace+1);
                        String topicId = line.substring(0, firstSpace).trim();
                        String docid = line.substring(secondSpace,thirdSpace).trim();
                        String relevance = line.substring(thirdSpace).trim();

                        Map<String,String> topicJudgementsAux = relevantTopicDoc.get(topicId);
                        if(topicJudgementsAux == null)
                        {
                            topicJudgementsAux = new HashMap<String,String>();
                            relevantTopicDoc.put(topicId,topicJudgementsAux);
                        }
                        topicJudgementsAux.put(docid,relevance);
                    }
                }
            }
        }
        catch (FileUploadException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
        catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }


    Map<String, String> topicJudgements = null;

    if (request.getParameter("topicID") != null && request.getParameter("topicID").length() > 0) {
        topicJudgements = relevantTopicDoc.get(request.getParameter("topicID"));
        if (topicJudgements == null) {
            topicJudgements = new HashMap<String, String>();
            relevantTopicDoc.put(request.getParameter("topicID"), topicJudgements);
        }
    }





    if (request.getParameter("topicOp") != null && request.getParameter("topicOp").equals("clean")) {
        System.out.println(new java.util.Date() + " : NTCIR CLEAN : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "]");
        relevantTopicDoc = new HashMap<String, Map<String, String>>();
        topics = new HashMap<String, String>();
        topicsDesc = new HashMap<String, String>();
        topicsNarr = new HashMap<String, String>();
	
        request.getSession().setAttribute("relevantTopicDocs", relevantTopicDoc);
        request.getSession().setAttribute("topics", topics);
        request.getSession().setAttribute("topicsDesc", topicsDesc);
        request.getSession().setAttribute("topicsNarr", topicsNarr);
		request.getSession().removeAttribute("logs");
    }
    else if (request.getParameter("topicOp") != null && request.getParameter("topicOp").equals("confirm")) 
	{
        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = (String) params.nextElement();
            if (param.startsWith("NYT_")) {

                String relevance = request.getParameter(param);
                if (relevance != null && relevance.length() > 0) {
                    if (relevance.equals("relevant")) {
                        topicJudgements.put(param, "1");
                        System.out.println(new java.util.Date() + " : NTCIR CONFIRM : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "] judgement[" + request.getParameter("topicID") + " 0 " + param + " 1]");
                    } else if (relevance.equals("irrelevant")) {
                        System.out.println(new java.util.Date() + " : NTCIR CONFIRM : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "] judgement[" + request.getParameter("topicID") + " 0 " + param + " 0]");
                        topicJudgements.put(param, "0");
                    }
                }
            }
        }
    } 
	else if (request.getParameter("topic") != null && request.getParameter("topicID") != null) 
	{
        if (topics == null) 
		{
            topics = new HashMap<String, String>();
            topicsDesc = new HashMap<String, String>();
            topicsNarr = new HashMap<String, String>();
            request.getSession().setAttribute("topics", topics);
            request.getSession().setAttribute("topicsDesc", topicsDesc);
            request.getSession().setAttribute("topicsNarr", topicsNarr);
        }
        System.out.println(new java.util.Date() + " : NTCIR TOPIC : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "] topicID[" + request.getParameter("topicID") + "] topic[" + request.getParameter("topic").replace("\n"," ") + "] topicDesc[" + request.getParameter("topicDesc").replace("\n"," ") + "] topicNarr[" + request.getParameter("topicNarr").replace("\n"," ") + "]" );

        topics.put(request.getParameter("topicID"), request.getParameter("topic"));
        topicsDesc.put(request.getParameter("topicID"), request.getParameter("topicDesc"));
        topicsNarr.put(request.getParameter("topicID"), request.getParameter("topicNarr"));
    }













%>

<div id="adminbox">
   <div class="menu">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<%--<td>--%>
				<%--<%--%>
					<%--if(topics != null && topics.size() > 0)--%>
					<%--{	%>--%>
					  <%--<input type="button" onclick="showOrHideOne('adminboxContent')" value="Add Or Remove Topics">--%>
				<%--<%	}--%>
				<%--%>--%>
				<%--</td>--%>
				<td>
					  <form action="indexNtcir.jsp" method="post" enctype="multipart/form-data" >
							Import Judgements: <input type="file" name="judgments"> <input type="button" value="Import" onclick="this.form.submit()">
					  </form>
				</td>
				<td>
					<% if (relevantTopicDoc != null && relevantTopicDoc.size() > 0)
					{%>
					<form action="exportNtcirJudgments.jsp" method="post" >
							<input type="button" value="Export my judgements to treceval format" onclick="this.form.submit()">
					</form>
					<%}%>
				</td>
			</tr>
		</table>
	</div>
	<%
		String display = "style=\"display:none\"";
		if(topics == null || topics.size() == 0)
			display = "";
	%>
	<div id="adminboxContent"  <%=display%>>
		 <form class="box" action="indexNtcir.jsp" method="post">
		   <div class="form">
				<table id="topicsTable" style="width:100%">
					<tr><td width="10%">TopicID</td><td><input type="text" style="width:100%" name="topicID" value="NTCIR3-98-043" /></td></tr>
					<tr><td width="10%">Topic</td><td><input type="text" style="width:100%" name="topic" value="Worldwide Natural Disasters"/> </td></tr>
					<tr><td width="10%">Topic Desc</td><td><textarea rows="5" style="width:100%" name="topicDesc">  What are the natural disasters caused by abnormal phenomena, such as floods, earthquakes, and famines, that appear worldwide?</textarea></td></tr>
					<tr><td width="10%">Topic Narr</td><td><textarea rows="5" style="width:100%" name="topicNarr">  Relevant documents include the name of the area where the abnormal phenomenon occurs, its details, and figures for the loss of lives and property damages. Without figures, the document is partially relevant. Documents without the name of the area or details about the abnormal phenomenon are not relevant</textarea></td></tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="Add Topic"/> &nbsp;&nbsp; 		
							<input type="hidden" name="topicOp" value=""/>
							<input type="button" onclick="if(confirm('Are you sure about cleaning all judgments?')){this.form.topicOp.value='clean';this.form.submit()}" value="Clean Topics"/>
					   </td>
					</tr>
				</table>
			</div>
		</form>
	 </div> 
</div>
<%
    if (topics != null && topics.size() > 0) {
        List<String> topicsStrs = new ArrayList<String>(topics.keySet());
        Collections.sort(topicsStrs, new Comparator<String>() {

            public int compare(String o1, String o2) {
                int comp = o1.compareTo(o2);
                if(comp>0)return 1;
                else if(comp < 0) return -1;
                else return 0;
            }
        });


%>

  <form name="SearchForm" action="indexNtcir.jsp" method="post">
    <div class="searchForm">
    <input type="hidden" name="topicOp" value="search">
		<table width="100%">
			<tr>
				<td nowrap="nowrap" colspan="2">
					Choose a topic to evaluate before submit: ( <a href="javascript:alert('Choose a Topic, submit a search and mark the documents as relevant or irrelevant, use the Annotations Link to see the places and the time expressions marked with colors in the document body')">help</a> ) 								
				
					<select name="topicID">
							<option value=""></option>
						<%
							for(String topicId : topicsStrs)
							{
								String checked = "";
								if(request.getParameter("topicID") != null && topicId.equals(request.getParameter("topicID")))
								{
									checked = "selected";
								}
						%>
								<option <%=checked%>  onclick="document.canconfirm='true';document.SearchForm.topicID.value=this.value" value="<%=topicId%>"> <%=topicId%> - <%=topics.get(topicId)%></option>
						<%
							}
						%>
					</select>	
				</td>	
				<td>
					<script language="JavaScript" type="text/javascript">
						<!--
							function showTopicNarr(topicId)
							{
								<%
									for(String topicId : topicsStrs)
									{
									
								%>
										if(topicId == '<%=topicId%>')
										{
											alert("TOPIC:<%=topicId%> \nDESC:<%=topics.get(topicId)%> \nNARR:<%=topicsDesc.get(topicId)%> \n");
										}
								<%
									}
								%>
							}
							
							function copy2SearchQuery(form,topicId)
							{
								<%
									for(String topicId : topicsStrs)
									{
								%>
										if(topicId == '<%=topicId%>')
										{
											form.q.value="<%=topics.get(topicId).replace("\"","\\\"")%> <%=topicsDesc.get(topicId).replace("\"","\\\"")%>";
							            }
								<%
									}
								%>
							}
						-->
					</script>
				
					<input type="button" value="see topic narrative" onclick="showTopicNarr(this.form.topicID.value);"><br/>
					<input type="button" value="copy to query" onclick="copy2SearchQuery(this.form,this.form.topicID.value);">
				</td>
			</tr>
			<tr>
				<td align="right" width="100px">
					Search query:
				</td>
				<td>
					<%
						if(request.getParameter("topicDesc")!=null)
						{
					%>
					<input type="text" style="width:100%" name="q" value="<%=request.getParameter("topic") + " " + request.getParameter("topicDesc")%>"> 
					<%
						}
						else
						{
					%>
					<input type="text" style="width:100%" name="q" value="<%=request.getParameter("q")%>">

					<%
						}
					%>
				</td>
			</tr>
			<tr>
				<td align="right">
					<input type="hidden" name="startAt" value="0">
					Show
				</td>
				<td>
					<%
						String sel10 = "";
						String sel20 = "";
						String sel50 = "";
						String sel100 = "";
						if(request.getParameter("docs")!=null)
						{
							Integer docs = Integer.parseInt(request.getParameter("docs"));
							if(docs == 10)
								sel10 = " selected=\"selected\"";
							if(docs == 20)
								sel20 = " selected=\"selected\"";
							if(docs == 50)
								sel50 = " selected=\"selected\"";
							if(docs == 100)
								sel100 = " selected=\"selected\"";
						}
					%> 
					<select name="docs">
						<option value="10" <%=sel10%>>10</option>
						<option value="20" <%=sel20%>>20</option>
						<option value="50" <%=sel50%>>50</option>
						<option value="100" <%=sel100%>>100</option>
					</select>
					Results
				</td>
			</tr>
			<tr>
				<td></td>
				<td colspan="2">
					<input type="button" onclick="if(this.form.topicID.value!=''){this.form.submit();}else{alert('Please choose a Topic before search. If you don\'t have any topics defined please add one topic with the form below')}" value="Search Documents"/>
					&nbsp;&nbsp;
					<input style="background-color:yellow;padding:3px;" type="button" onclick="if(this.form.topicID.value==''){alert('Please choose a Topic');}else if(confirm('Are you sure about confirm these judgements all judgments?')){this.form.topicOp.value='confirm';this.form.submit()}" value="Click here to confirm your Judgements after you choose the relevance of the documents"/>
				</td>
			</tr>

		</table>


	</div>


<%
    
	}




    if (request.getParameter("topicOp") != null && request.getParameter("topicOp").equals("search") && request.getParameter("q") != null && request.getParameter("q").length() > 1) {

	String log = "[" + (new java.util.Date()).toString() + "] : NTCIR QUERY: session[" + request.getSession().getId() + "] topic[" + request.getParameter("topicID") + "] query[" + request.getParameter("q").replace("\n", " ") + "] ip[" + request.getRemoteAddr() + "]";
	System.out.println(log);
	java.util.List<String> logs = (java.util.List<String>)request.getSession().getAttribute("logs");
	if(logs == null)
	{
		logs = new java.util.ArrayList<String>();
		request.getSession().setAttribute("logs",logs);
	}
	logs.add(log);



%>
<h3>Results for '<%=request.getParameter("q")%>':</h3>
<hr>
<%
    try {
        IndexReader readerContents = LgteIndexManager.openReader(IndexContents.indexPath, Model.OkapiBM25Model);
        IndexReader readerGeoTime = LgteIndexManager.openReader(IndexGeoTime.indexPath, Model.OkapiBM25Model);
        IndexReader readerTimexes = LgteIndexManager.openReader(IndexTimexes.indexPath, Model.OkapiBM25Model);
        IndexReader readerWoeid = LgteIndexManager.openReader(IndexWoeid.indexPath, Model.OkapiBM25Model);
        IndexReader readerSentences = LgteIndexManager.openReader(IndexSentences.indexPath, Model.OkapiBM25Model);
        IndexReader readerGeoTimeSentences = LgteIndexManager.openReader(IndexGeoTimeSentences.indexPath, Model.OkapiBM25Model);
        IndexReader readerTimexesSentences = LgteIndexManager.openReader(IndexTimexesSentences.indexPath, Model.OkapiBM25Model);
        IndexReader readerWoeidSentences = LgteIndexManager.openReader(IndexWoeidSentences.indexPath, Model.OkapiBM25Model);

        Map<String, IndexReader> readers = new HashMap<String, IndexReader>();


        readers.put(Config.ID, readerSentences);
//        readers.put(Config.ID, readerContents);
        readers.put(Config.DOC_ID, readerSentences);
        readers.put(Config.CONTENTS, readerContents);
        readers.put(Config.SENTENCES, readerSentences);
        IndexReader readerDB = LgteIndexManager.openReader("G:\\INDEXES\\ntcir\\TEXT_TEMP_GEO_DB", Model.OkapiBM25Model);

        readers.put("regexpr(^S_.*)", readerGeoTime);
        readers.put("regexpr(^t_.*)", readerTimexes);
        readers.put("regexpr(^g_.*)", readerWoeid);
        readers.put("regexpr(^S_.*_sentences)", readerGeoTimeSentences);
        readers.put("regexpr(^t_.*_sentences)", readerTimexesSentences);
        readers.put("regexpr(^g_.*_sentences)", readerWoeidSentences);
        readers.put("regexpr(.*_DB$)", readerDB);
        LgteIsolatedIndexReader lgteIsolatedIndexReader = new LgteIsolatedIndexReader(readers);
        lgteIsolatedIndexReader.addTreeMapping(readerContents, readerSentences, Config.DOC_ID);
        lgteIsolatedIndexReader.addTreeMapping(readerTimexes, readerTimexesSentences, Config.DOC_ID);
        lgteIsolatedIndexReader.addTreeMapping(readerWoeid, readerWoeidSentences, Config.DOC_ID);
        lgteIsolatedIndexReader.addTreeMapping(readerGeoTime, readerGeoTimeSentences, Config.DOC_ID);
        lgteIsolatedIndexReader.addTreeMapping(readerGeoTime, readerGeoTimeSentences, Config.DOC_ID);
        lgteIsolatedIndexReader.addTreeMapping(readerGeoTime, readerDB, Config.DOC_ID);


        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, lgteIsolatedIndexReader);
        Map<String, Analyzer> analyzersMap = new HashMap<String, Analyzer>();
        analyzersMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());
//        analyzersMap.put(Config.SENTENCES, IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer brokerStemAnalyzer = new LgteBrokerStemAnalyzer(analyzersMap, new LgteWhiteSpacesAnalyzer());

//                LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), analyzer);
//        System.out.println("Searching for: " + request.getParameter("q"));
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.getQueryProperties().put("bm25.k1", "1.2d");
        queryConfiguration.getQueryProperties().put("bm25.b", "0.75d");
        queryConfiguration.getQueryProperties().put("bm25.k3", "0.75d");
        queryConfiguration.getQueryProperties().put("index.tree", "true");
        LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), searcher, brokerStemAnalyzer, queryConfiguration);
        LgteHits hits = searcher.search(query);

//                LgteHits hits = searcher.search(request.getParameter("q"),analyzer);

        int startAt = Integer.parseInt(request.getParameter("startAt"));
        int docs = Integer.parseInt(request.getParameter("docs"));

        out.println("<h3>Number of matching documents = " + hits.length() + " - showing from " + startAt + " to " + (startAt + docs) + "</h3><hr/>");
        for (int i = startAt; i < startAt + docs && i < hits.length(); i++) {
            LgteDocumentWrapper doc = hits.doc(i);
            String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);

            String sgml = doc.get(Config.TEXT_DB);
            NyTimesDocument nyt = new NyTimesDocument(new BufferedReader(new StringReader(sgml)), "dummy.txt");
            String sgmlWithOutTags = sgml.replaceAll("<[^>]+>", "");

//            System.out.println(sgml);
            String placeMaker = doc.get(Config.GEO_DB);
            String timexes = doc.get(Config.TEMPORAL_DB);
            StringBuilder annotatedText = new StringBuilder();
            int pos = 0;

            PlaceMakerDocument placeMakerDocument = null;
            if (placeMaker != null) {
                placeMakerDocument = new PlaceMakerDocument(placeMaker);
            }
            TimexesDocument timexesDocument = null;
            if (timexes != null) {

                timexesDocument = new TimexesDocument(timexes);
//                System.out.println(docno);
                List<Timex2TimeExpression> timexesList = timexesDocument.getTimex2TimeExpressions();
                Collections.sort(timexesList, new Comparator<Timex2TimeExpression>() {

                    public int compare(Timex2TimeExpression o1, Timex2TimeExpression o2) {
                        if (o1.getStartOffset() > o2.getStartOffset())
                            return 1;
                        else if (o1.getStartOffset() < o2.getStartOffset())
                            return -1;
                        else return 0;

                    }
                }
                );
            }
            Iterator<Timex2TimeExpression> timexesIter;
            if (timexesDocument != null)
                timexesIter = timexesDocument.getTimex2TimeExpressions().iterator();
            else
                timexesIter = new ArrayList<Timex2TimeExpression>().iterator();

            Iterator<PlaceMakerDocument.PlaceRef> placesIter;
            if (placeMakerDocument != null) {
                List<PlaceMakerDocument.PlaceRef> refs = placeMakerDocument.getAllRefs();
                Collections.sort(refs, new Comparator<PlaceMakerDocument.PlaceRef>() {

                    public int compare(PlaceMakerDocument.PlaceRef o1, PlaceMakerDocument.PlaceRef o2) {
                        if (o1.getStartOffset() > o2.getStartOffset())
                            return 1;
                        else if (o1.getStartOffset() < o2.getStartOffset())
                            return -1;
                        else return 0;

                    }
                }
                );
                placesIter = refs.iterator();
            } else
                placesIter = new ArrayList<PlaceMakerDocument.PlaceRef>().iterator();


            int lastOffset = 0;
            Timex2TimeExpression nowTimex = null;
            PlaceMakerDocument.PlaceRef nowPlaceRef = null;
            while (timexesIter.hasNext() || placesIter.hasNext()) {
                if (nowPlaceRef == null && placesIter.hasNext())
                    nowPlaceRef = placesIter.next();
                if (nowTimex == null && timexesIter.hasNext())
                    nowTimex = timexesIter.next();

                int startOffsetPlaceRef = 0;
                int endOffsetPlaceRef = 0;
                if (nowPlaceRef != null) {
                    startOffsetPlaceRef = nyt.toStringOffset2txtwithoutTagsOffset(nowPlaceRef.getStartOffset());
                    endOffsetPlaceRef = nyt.toStringOffset2txtwithoutTagsOffset(nowPlaceRef.getEndOffset());
                }
                if ((nowTimex != null && nowPlaceRef != null && nowTimex.getStartOffset() < startOffsetPlaceRef) || nowTimex != null && nowPlaceRef == null) {
                    if (lastOffset <= nowTimex.getStartOffset() && nowTimex.getStartOffset() < nowTimex.getEndOffset() && nowTimex.getStartOffset() < sgmlWithOutTags.length() && nowTimex.getEndOffset() < sgmlWithOutTags.length() && nowTimex.getStartOffset() > 0 && nowTimex.getEndOffset() > 0 && nowTimex.getStartOffset() > pos) {
//                        System.out.println(nowTimex.getStartOffset() + ":" + nowTimex.getEndOffset());
                        annotatedText.append(sgmlWithOutTags.substring(pos, nowTimex.getStartOffset()));
                        annotatedText.append("<label class=\"TIMEX\" start=\"" + nowTimex.getStartOffset() + "\">");
                        annotatedText.append(sgmlWithOutTags.substring(nowTimex.getStartOffset(), nowTimex.getEndOffset() + 1));
                        annotatedText.append("</label>");
                        lastOffset = nowTimex.getEndOffset();
                        pos = nowTimex.getEndOffset() + 1;
                    }
                    nowTimex = null;
                } else if (nowPlaceRef != null) {

                    if (lastOffset <= startOffsetPlaceRef && startOffsetPlaceRef < endOffsetPlaceRef && startOffsetPlaceRef < sgmlWithOutTags.length() && endOffsetPlaceRef < sgmlWithOutTags.length() && startOffsetPlaceRef > 0 && endOffsetPlaceRef > 0 && startOffsetPlaceRef > pos) {
//                        System.out.println(nowPlaceRef.getStartOffset() + ":" + nowPlaceRef.getEndOffset());
                        annotatedText.append(sgmlWithOutTags.substring(pos, startOffsetPlaceRef));
                        annotatedText.append("<label class=\"PLACE\">");
                        annotatedText.append(sgmlWithOutTags.substring(startOffsetPlaceRef, endOffsetPlaceRef + 1));
                        annotatedText.append("</label>");
                        lastOffset = endOffsetPlaceRef;
                        pos = endOffsetPlaceRef + 1;
                    }
                    nowPlaceRef = null;
                } else {

                }
            }
            if (sgmlWithOutTags.length() > pos + 1)
                annotatedText.append(sgmlWithOutTags.substring(pos + 1));


            String style = "";
            String relevance = "";
            String relevantSelected = "";
            String irrelevantSelected = "";
//            System.out.println("TOPICID " + request.getParameter("topicID"));
            if (request.getParameter("topicID") != null && request.getParameter("topicID").length() > 0) {

                topicJudgements = relevantTopicDoc.get(request.getParameter("topicID"));
//                System.out.println("JUDGMENTS: " + topicJudgements.size());
                if (topicJudgements.get(docno) != null) {

                    relevance = topicJudgements.get(docno);
//                    System.out.println("Found DOCNO: " + relevance);
                    if (relevance != null) {
                        if (relevance.equals("0")) {
                            style = "style=\"background-color:red\"";
                            irrelevantSelected = " selected";
                        } else {
                            style = "style=\"background-color:green\"";
                            relevantSelected = " selected";
                        }
                    }
                }
            }
            out.print("<a name=\"" + docno + "\"></a>\n");
            out.print("<table><tr><td " + style + " id=\"table" + docno + "\">");
            String title = hits.doc(i).get(Config.TITLE);
            if (title != null)
                out.print("<h3>" + i + " - " + title + " - (DOCNO: " + docno + ")</h3>\n");
            else
                out.print("<h3>" + i + " - (DOCNO: " + docno + ")</h3>\n");

            out.print("<p><b>score</b>: " + hits.textScore(i) + "</p>\n");
            out.print("<p><b>summary</b>" + hits.summary(i, pt.utl.ist.lucene.Globals.LUCENE_DEFAULT_FIELD, 100, 4) + "</p>\n");
//            out.print("<p><a onclick=\"popup('doc" + i +"');\" href=\"#\">Show Anotations:</a></p>\n");
            out.print("<p><a onclick=\"getObjectById('table" + docno + "').style.backgroundColor='lightgray';return !showPopup('doc" + i + "', event);\" href=\"#\">Show Anotations</a></p>\n");
            out.print("</td><td>\n");
            if (topics.size() > 0) {

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
    [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]
	<%
        if(topics.size() > 0)
        {
    %>
    <input type="button" value="Mark relevant" onclick="this.form['<%=docno%>'].value='relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='green';window.location='#<%=docno%>';"/>
    <input type="button" value="Mark irrelevant" onclick="this.form['<%=docno%>'].value='irrelevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='red';window.location='#<%=docno%>';"/>
    <%
        }
    %>
    
	<br>
    <%  String annotatedTextStr = annotatedText.toString().replace("\n","<br>");
	
	String qq= request.getParameter("q");
	/*org.apache.lucene.analysis.TokenStream stream = pt.utl.ist.lucene.treceval.IndexCollections.en.getAnalyzerWithStemming().tokenStream("",new java.io.StringReader(qq));
	org.apache.lucene.analysis.Token t;	
	while((t=stream.next())!=null)
	{
		String s = t.termText(); 
		if(s.trim().length()>1)
		    annotatedTextStr = annotatedTextStr.replaceAll(s,"<label class=\"word\">" + s + "</label>");
	}*/
	org.apache.lucene.analysis.TokenStream stream = pt.utl.ist.lucene.treceval.IndexCollections.en.getAnalyzerWithStemming().tokenStream("",new java.io.StringReader(qq));
	org.apache.lucene.analysis.Token t;
	while((t=stream.next())!=null)
	{
		String str = t.termText();
        String Str = str.substring(0,1).toUpperCase()+str.substring(1);
        String STR = str.toUpperCase();
		if(str.trim().length()>1)
		{
			annotatedTextStr = annotatedTextStr.replaceAll(str,"<label class=\"word\">"  + str + "</label>");
			annotatedTextStr = annotatedTextStr.replaceAll(Str,"<label class=\"word\">" + Str + "</label>");
			annotatedTextStr = annotatedTextStr.replaceAll(STR,"<label class=\"word\">" + STR + "</label>");
		}
	}
    %>
	<%=annotatedTextStr%>
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
%>
<p>
<%
            if(startAt > 0)
            {
%>
<a href="<%=request.getContextPath()%>/indexNtcir.jsp?topicID=<%=request.getParameter("topicID")%>&startAt=<%=startAt-docs%>&docs=<%=docs%>&q=<%=request.getParameter("q")%>"> Previous Results</a> &nbsp; &nbsp; <%=startAt%> to <%=startAt+docs%>
<%
            }
            if(startAt + docs < hits.length())
            {
%>
<a href="<%=request.getContextPath()%>/indexNtcir.jsp?topicID=<%=request.getParameter("topicID")%>&startAt=<%=startAt+docs%>&docs=<%=docs%>&q=<%=request.getParameter("q")%>"> Next Results </a>
<%
            }
%>
</p>
<%
        }
        catch (Exception ee) {
            out.println("<b><p>Error: " + ee + "</p></b>");

            ee.printStackTrace();
        }
    }
%>
</form>
<%}%>
<jsp:include page="footer.jsp"/>
</body>
</html>
