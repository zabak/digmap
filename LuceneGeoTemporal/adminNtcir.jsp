<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.web.assessements.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.dao.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.services.*"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>

<%
    if (ServletFileUpload.isMultipartContent(request))
    {
        Server.importPool(request);
    }
%>

<br>
<a href="assessmentsNtcir.jsp?op=logout">Logout <%=Server.getUsername(request)%></a> 
<form class="form" action="assessmentsNtcir.jsp" method="post" enctype="multipart/form-data">
    <input type="hidden" name="task" value="NtcirGeoTime2011">
    <input type="hidden" name="indexLocation" value="C:/WORKSPACE_JM/DATA/INDEXES/NTCIR/TEXT_TEMP_GEO_DB">
    <input type="hidden" name="indexTitleLocation" value="C:/WORKSPACE_JM/DATA/INDEXES/NTCIR/contents">
    <table>
        <tr>
            <td>Import File</td><td><input type="file" name="file"></td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit"></td>
        </tr>
    </table>
</form>

 <br>
    <h2>Document Assessment Pools</h2>
    <table class="form">
        <tr>
            <th>LocalID </th>
            <th>RunID</th>
            <th>Desc</th>
            <th>Date</th>
            <th>Closed</th>
        </tr>
<%
        List<Pool> pools = DBServer.getPools("NtcirGeoTime2011");
        for(Pool pool: pools)
        {
%>
            <tr>
                <td><%=pool.getId()%></td>
                <td><%=pool.getRunId()%></td>
                <td><%=pool.getDescription()%></td>
                <td><%=pool.getDate()%></td>
                <td><%=pool.isClosed()%></td>
                <td><a href="assessmentsNtcir.jsp?op=openClose&pool=<%=pool.getId()%>">open/close</a></td>
                <td><a href="assessmentsNtcir.jsp?op=download&pool=<%=pool.getId()%>">Download Assessments</a></td>
            </tr>
<%
        }
%>
    </table>

    <h2>General Assessments Stats</h2>
    <%
    AssessmentsBoard aB = DBServer.loadAssessmentsBoard("NtcirGeoTime2011");
    %>
     <table class="data">
        <tr>
            <th>Topic/relevance</th>
            <th>relevant</th>
            <th>partially relevant<br> where</th>
            <th>partially relevant<br> when</th>
            <th>partially relevant<br> other</th>
            <th>irrelevant</th>
            <th>Assessed/Total</th>
        </tr>
     <%
        List<Topic> topics =   DBServer.getTopics("NtcirGeoTime2011");
        for(Topic topic: topics)
        {
     %>
             <tr>
                <td><%=topic.getIdTopic()%></td>
                <td><%=aB.getCount(topic.getIdTopic(),"relevant")%></td>
                <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-where")%></td>
                <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-when")%></td>
                <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-other")%></td>
                <td><%=aB.getCount(topic.getIdTopic(),"irrelevant")%></td>
                <td><%=aB.getTotalsAssessed(topic.getIdTopic())%>/<%=aB.getTotals(topic.getIdTopic())%></td>
            </tr>
     <%
        }
     %>
    </table>
    <br>
    
    <%
        
    for(Pool pool: pools)
    {

%>
        <h2>Assessments Stats For Pool: <%=pool.getRunId()%> <%=pool.getDescription()%></h2>

        <%
            aB = DBServer.loadAssessmentsBoard("NtcirGeoTime2011",pool.getId());
        %>
             <table class="data">
                <tr>
                    <th>Topic/relevance</th>
                    <th>relevant</th>
                    <th>partially relevant<br> where</th>
                    <th>partially relevant<br> when</th>
                    <th>partially relevant<br> other</th>
                    <th>irrelevant</th>
                    <th>Assessed/Total</th>
                </tr>
             <%
                for(Topic topic: topics)
                {
             %>
                     <tr>
                        <td><%=topic.getIdTopic()%></td>
                        <td><%=aB.getCount(topic.getIdTopic(),"relevant")%></td>
                        <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-where")%></td>
                        <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-when")%></td>
                        <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-other")%></td>
                        <td><%=aB.getCount(topic.getIdTopic(),"irrelevant")%></td>
                        <td><%=aB.getTotalsAssessed(topic.getIdTopic())%>/<%=aB.getTotals(topic.getIdTopic())%></td>
                    </tr>
             <%
                }
             %>
                </table>

    <%
        }
    %>