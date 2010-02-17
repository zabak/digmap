<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="pt.utl.ist.lucene.web.assessements.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.dao.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.services.*"%>


<%
    if(Server.operation(request,response))
    {
%>
<html>
<head>
<title>NTCIR/GeoTime - Tool fot Relevance Judgements creation with Lucene GeoTemporal Extensions (LGTE) AT GeoTime NTCIR</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/popup.txt"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/utility.txt"></script>
<link type="text/css" rel="stylesheet" href="css/styles.css"/>
</head>
<body>
<jsp:include page="headerNtcir.jsp"/>
<%
    User user = null;
	user = Server.login(request);
    if(request.getParameter("agree")!=null)
    {
        System.out.println(new java.util.Date() + " : NTCIR AGREE : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "]");
        request.getSession().setAttribute("agree","true");
    }
    if(user != null && request.getSession().getAttribute("agree")==null)
    {
%>
<div class="form">
    <form action="assessmentsNtcir.jsp" method="post">
    <h2>All operations performed in this tool (relevance judgements, topic management) will be saved in the system log for future research purposes. 	<br>Please click in the box below if you agree with these conditions in order to proceed.</h2>

    <p><input type="checkbox" name="agree"> I declare that I agree with these conditions and I want start use the system</p>
    <input type="button" value="I declare that I agree with these conditions" onclick="if(!this.form.agree.checked){alert('You need to agree with the conditions in order to proceed.');}else{this.form.submit();}">
    </form>
</div>
<%
    }
    else
    {
		boolean tryLogin = false;

		if(request.getParameter("username") != null)
		{
		     tryLogin = true;
		}

		if(user != null)
		{
		    if(user.isAdmin())
		    {
		    %>
				<jsp:include page="adminNtcir.jsp"/>
            <%
		    }
		    else
		    {
		    %>
				<jsp:include page="assessorNtcir.jsp"/>
			<%
		    }
		}
		else
		{
			if(tryLogin)
			{
%>
				<p class="warning">Authentication failed</p>
<%			
			}
%>
            <br>
			<form id="loginbox" action="assessmentsNtcir.jsp" method="post">
				<table>
				    <tr>
						<th colspan="2"> Authentication Form for GeoTime 2010 Assessors</th>
					</tr>
					<tr>
						<td>Username</td><td><input type="text" name="username"></td>
					</tr>
					<tr>
						<td>Password</td><td><input type="password" name="password"></td>
					</tr>
					<tr><td colspan="2"><input type="submit"></td></tr>
				</table>
			</form>
<%
	   }
	 }
%>
<jsp:include page="footer.jsp"/>
</body>
</html>
<%
    }
%>
