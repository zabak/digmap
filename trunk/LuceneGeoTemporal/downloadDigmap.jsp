<%@page import="org.dom4j.Element"%>
<%@page import="pt.utl.ist.lucene.utils.Dom4jUtil"%>
<%@page import="pt.utl.ist.lucene.utils.XmlUtils"%>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%
    String docno = request.getParameter("id");
    String filepath = request.getParameter("filepath");
    if (docno != null)
    {
        try
        {
            response.setContentType("text/xml");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write("<?xml version=\"1.0\"?>".getBytes());
            Map<String, String> namespaces = new HashMap<String,String>();
            namespaces.put("digmap","http://www.digmap.eu/schemas/resource/");
            namespaces.put("dc","http://purl.org/dc/elements/1.1/");
            Element element = (Element) XmlUtils.getFragment(filepath, "//digmap:record[@urn='" + docno + "']",namespaces);
            if(element == null)
                element = (Element) XmlUtils.getFragment(filepath, "//digmap:record[dc:identifier='" + docno + "']",namespaces);
            Dom4jUtil.write(element, outputStream);
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
%>