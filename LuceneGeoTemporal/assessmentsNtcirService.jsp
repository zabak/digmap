<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="pt.utl.ist.lucene.web.assessements.services.*"%>
<%
    if(Server.operation(request,response))
    {
%>
<%=request.getAttribute("docno")%>;
<%
    }
    else
    {//E IGUAL DOS DOIS LADOS  O SERVICO E QUE METE ISTO
%>
<%=request.getAttribute("docno")%>;
<%
    }
%>
