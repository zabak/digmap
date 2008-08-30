<%@page import="org.dom4j.Element"%>
<%@page import="pt.utl.ist.lucene.utils.Dom4jUtil"%>
<%@page import="pt.utl.ist.lucene.utils.XmlUtils"%>
<%@ page import="java.io.OutputStream" %>
<%@ page import="pt.utl.ist.lucene.treceval.Globals" %>
<%@ page import="java.io.File" %>
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
            Element element = (Element) XmlUtils.getFragment(Globals.DATA_DIR + filepath.replace("\\", File.separator).replace("/", File.separator), "//DOC[contains(DOCNO,'" + docno + "')]", null);
            Dom4jUtil.write(element, outputStream);
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
%>