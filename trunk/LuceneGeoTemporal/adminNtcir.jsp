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
    <input type="hidden" name="task" value="NtcirGeoTime2010">
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
    <table class="form">
        <tr><th colspan="2">Document Assessment Pools</th></tr>
        <tr>
            <th>LocalID </th>
            <th>RunID</th>
            <th>Desc</th>
            <th>Date</th>
            <th>Closed</th>
        </tr>
<%
        List<Pool> pools = DBServer.getPools("NtcirGeoTime2010");
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

    <br>
