<%@page import="org.dom4j.Element"%>
<%@page import="pt.utl.ist.lucene.utils.Dom4jUtil"%>
<%@page import="pt.utl.ist.lucene.utils.XmlUtils"%>
<%@ page import="java.io.OutputStream" %>
<%
    String docno = request.getParameter("docno");
    String filepath = request.getParameter("filepath");
    if (docno != null)
    {
        try
        {
            response.setContentType("text/xml");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write("<?xml version=\"1.0\"?>".getBytes());
            Element element = (Element) XmlUtils.getFragment(filepath, "//DOC[contains(DOCNO,'" + docno + "')]",null);
            Dom4jUtil.write(element, outputStream);
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
%>