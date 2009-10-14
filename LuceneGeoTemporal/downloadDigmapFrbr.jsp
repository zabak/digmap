<%@page import="org.dom4j.Element"%>
<%@page import="pt.utl.ist.lucene.utils.Dom4jUtil"%>
<%@page import="pt.utl.ist.lucene.utils.XmlUtils"%>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="pt.utl.ist.lucene.treceval.Globals" %>
<%@ page import="java.io.File" %>
<%@ page import="org.dom4j.Document" %>
<%@ page import="java.util.Properties" %>
<%@ page import="pt.utl.ist.lucene.config.LocalProperties" %>
<%@ page import="org.dom4j.io.OutputFormat" %>
<%@ page import="org.dom4j.io.XMLWriter" %>
<%@ page import="org.xml.sax.InputSource" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.FileInputStream" %>
<%
    Properties props = new LocalProperties("digmapFrbr/conf.properties");
    String dataDir = props.getProperty("data.dir");
    String originalsDir = props.getProperty("originals.dir");

    String docno = request.getParameter("id");
    String filepath = request.getParameter("filepath");
    if (request.getParameter("type") != null && request.getParameter("type").equals("set")) {

        if (docno != null) {
            try {
                response.setContentType("text/xml");
                response.setCharacterEncoding("UTF-8");
                OutputStream outputStream = response.getOutputStream();
                outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes());
//                outputStream.write("<teste3>".getBytes());
                Map<String, String> namespaces = new HashMap<String, String>();
                namespaces.put("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
                namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
                Element element = (Element) XmlUtils.getFragment(dataDir.replace("\\", File.separator).replace("/", File.separator) + filepath.replace("\\", File.separator).replace("/", File.separator), "//oai_dc:dc[./dc:identifier='" + docno + "']", namespaces, "UTF-8");

                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("UTF-8");
                XMLWriter writer = new XMLWriter(outputStream, format);
                writer.write(element);
//                outputStream.write("</teste>".getBytes());
                writer.close();
//                Dom4jUtil.write(element, outputStream);
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    } else {
        if (docno != null) {
            try {
                response.setContentType("text/xml");
                OutputStream outputStream = response.getOutputStream();
                //            outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
                //            Map<String, String> namespaces = new HashMap<String, String>();
                //            namespaces.put("oai_dc", "http://www.digmap.eu/schemas/resource/");
                //            namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
                //            Element element = (Element) XmlUtils.getFragment(Globals.DATA_DIR + filepath.replace("\\", File.separator).replace("/", File.separator), "//digmap:record[@urn='" + docno + "']", namespaces);
                //            if (element == null)
                //                element = (Element) XmlUtils.getFragment(filepath, "//digmap:record[dc:identifier='" + docno + "']", namespaces);
                InputSource is = new InputSource(new InputStreamReader(new FileInputStream(new File(originalsDir.replace("\\", File.separator).replace("/", File.separator) + filepath.replace("\\", File.separator).replace("/", File.separator))), "UTF-8"));
                Document dom = Dom4jUtil.parse(is);
                Dom4jUtil.write(dom, outputStream);
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }
%>