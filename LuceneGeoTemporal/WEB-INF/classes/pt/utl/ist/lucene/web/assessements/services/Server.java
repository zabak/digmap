package pt.utl.ist.lucene.web.assessements.services;

import org.apache.log4j.Logger;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import pt.utl.ist.lucene.LgteIndexManager;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.treceval.geotime.EOFException;
import pt.utl.ist.lucene.treceval.geotime.KoreaTimesDocument;
import pt.utl.ist.lucene.treceval.geotime.MainichiDailyDocument;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument;
import pt.utl.ist.lucene.web.assessements.Topic;
import pt.utl.ist.lucene.web.assessements.User;
import pt.utl.ist.lucene.web.assessements.dao.DBServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 12:09:44
 * To change this template use File | Settings | File Templates.
 */
public class Server
{

    private static final Logger logger = Logger.getLogger(Server.class);
    public static String getUsername(HttpServletRequest request) throws SQLException
    {
        return ((User) request.getSession().getAttribute("ntcir.user")).getUsername();
    }
    public static boolean operation(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        User user = (User) request.getSession().getAttribute("ntcir.user");
        if(request.getParameter("op") != null)
        {
            if(request.getParameter("op").equals("download"))
            {
                downloadAssessments(request,response);
                return false;
            }
            else if(request.getParameter("op").equals("logout"))
            {
                request.getSession().invalidate();
            }
            else if(request.getParameter("op").equals("addJudgments"))
            {
                request.setAttribute("docno","FAIL;none");
                String id_topic = request.getParameter("id_topic");
                if(id_topic!=null && id_topic.indexOf("LIMIT")>0)
                {
                    id_topic = id_topic.substring(0,id_topic.indexOf("LIMIT"));
                }
                Enumeration params = request.getParameterNames();
                try{
                    Throwable error = null;
                    while (params.hasMoreElements())
                    {
                        String param = (String) params.nextElement();
                        if (param.startsWith("NYT_") || param.startsWith("KT") || param.startsWith("XIE") || param.startsWith("EN"))
                        {
                            String relevance = request.getParameter(param);
                            if (relevance != null && relevance.length() > 0)
                            {
                                System.out.println(new java.util.Date() + " : NTCIR CONFIRM : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "] user[" + user.getUsername() + "] judgement[" + id_topic + " " + param + " " + relevance + "]");
                                try{
                                    DBServer.addRelevanceJudgement(id_topic,param,relevance,user.getUsername(),request.getParameter("obs" + param));
                                    request.setAttribute("docno","OK;"+param);
                                }catch(Throwable e)
                                {
                                    error = e;
                                    logger.error(e,e);
                                    request.setAttribute("docno","FAIL;"+param);
                                }
                            }
                        }
                    }
                    if(error != null && error instanceof SQLException)
                        throw (SQLException)error;
                    else if(error instanceof RuntimeException)
                        throw (RuntimeException) error;
                }
                catch(SQLException e)
                {
                    throw e;
                }
                catch(RuntimeException e)
                {
                    throw e;
                }
            }
            else if(request.getParameter("op").equals("openClose"))
            {
                if(user.isAdmin())
                {
                    DBServer.openClosePool(Integer.parseInt(request.getParameter("pool")));
                }
            }

        }
        return true;
    }

    private static void downloadAssessments(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException
    {
        Integer pool = Integer.parseInt(request.getParameter("pool"));
        Connection conn = DBServer.getConnection();
        response.setContentType("text/plain");
        PrintWriter pwWriter = response.getWriter();

        PreparedStatement ps = conn.prepareStatement(
                "select * from topic_doc where pool = ? order by topic");
        ps.setInt(1,pool);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            pwWriter.write(rs.getString("topic") + " 0 " + rs.getString("docno") + " " + rs.getString("relevance") + "\n");
        }
        rs.close();
        ps.close();
    }

    public static User login(HttpServletRequest request)
    {
        User auth = (User) request.getSession().getAttribute("ntcir.user");
        if(auth != null)
            return auth;

        if(request.getParameter("password") != null && request.getParameter("username")!= null)
        {
            auth = DBServer.login(request.getParameter("username"),request.getParameter("password"));
            if(auth != null)
                request.getSession().setAttribute("ntcir.user",auth);
        }

        return auth;
    }


    public static void importPool(HttpServletRequest request)
    {

//        if (ServletFileUpload.isMultipartContent(request)) {
//            System.out.println(new java.util.Date() + " : NTCIR POOL IMPORT : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "]");
//            try {
//                FileItemFactory factory = new DiskFileItemFactory();
//                ServletFileUpload upload = new ServletFileUpload(factory);
//                List items = upload.parseRequest(request);
//                File repositoryPath = new File(ConfigProperties.getProperty("output.tmp.dir"));
//                DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
//                diskFileItemFactory.setRepository(repositoryPath);
//                String file = null;
//                Map<String,String> parameters = new HashMap<String,String>();
//                for (Object item1 : items) {
//                    FileItem item = (FileItem) item1;
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(item.getInputStream()));
//                    String line;
//                    StringBuilder builder = new StringBuilder();
//                    while ((line = reader.readLine()) != null) {
//
//                        builder.append(line);
//                    }
//                    if(item.getFieldName().equals("file"))
//                        file = builder.toString();
//                    else
//                        parameters.put(item.getFieldName(),builder.toString());
//                }
//                User admin = (User) request.getSession().getAttribute("ntcir.user");
//                String index = parameters.get("indexLocation");
//                String indexTitle = parameters.get("indexTitleLocation");
//                importACLIAformat(admin,Dom4jUtil.parse(file),parameters.get("task"),index,indexTitle);
//            }
//            catch (FileUploadException ex) {
//                System.out.println(ex);
//                ex.printStackTrace();
//            }
//            catch (Exception ex) {
//                System.out.println(ex);
//                ex.printStackTrace();
//            }
//        }
    }

    public static void importPool(String filePath, String task,String index,String indexTitle) throws IOException, DocumentException, SQLException
    {
        User user = new User();
        user.setUsername("admin");
        importACLIAformat(user,Dom4jUtil.parse(new File(filePath)),task,index,indexTitle);
    }




    private static void importACLIAformat(User admin,  Document dom, String task, String index, String indexTitle) throws SQLException, DocumentException, IOException {



        XPath xPath = dom.createXPath("//METADATA/RUNID");
        String runId = xPath.selectSingleNode(dom).getText().trim();
        xPath = dom.createXPath("//METADATA/DESCRIPTION");
        String runDescription = xPath.selectSingleNode(dom).getText().trim();
        int poolId = DBServer.addPool(runId,runDescription,task);
        LanguageModelIndexReader reader = (LanguageModelIndexReader) LgteIndexManager.openReader(index, Model.OkapiBM25Model);
        LanguageModelIndexReader readerTitle = (LanguageModelIndexReader) LgteIndexManager.openReader(indexTitle, Model.OkapiBM25Model,false);



        try{
            DBServer.getConnection().setAutoCommit(false);
            xPath = dom.createXPath("//TOPIC");
            List<Element> topics = xPath.selectNodes(dom);
            for(Element topic: topics)
            {
                xPath = dom.createXPath("@ID");
                String idTopic = xPath.selectSingleNode(topic).getText().trim();
                Topic topicInfo = DBServer.getTopic(idTopic);
                System.out.println("topicInfo = " + topicInfo.getDescription());
                System.out.println("topicInfo = " + topicInfo.getNarrative());
                System.out.println("idTopic:" + idTopic);
                xPath = dom.createXPath("GEOTIME_RESULT/DOCUMENT");
                List<Element> documents = xPath.selectNodes(topic);
// <DOCUMENT RANK="1" DOCID="NYT_ENG_20020128.0182" SCORE="24.0141" />
                for(Element result: documents)
                {
                    xPath = dom.createXPath("@RANK");
                    int rank = Integer.parseInt(xPath.selectSingleNode(result).getText().trim());
                    xPath = dom.createXPath("./@DOCID");
                    String docno = xPath.selectSingleNode(result).getText();

                    Integer docId = (Integer) reader.get(docno);
                    org.apache.lucene.document.Document doc = reader.document(docId);
                    String geo = doc.get(Config.GEO_DB);
                    String time = doc.get(Config.TEMPORAL_DB);
                    String text = doc.get(Config.TEXT_DB);
                    String title = readerTitle.document(docId).get(Config.TITLE);
                    String html = getHtml(docno,topicInfo.getDescription(),topicInfo.getNarrative(),text,geo,time);
                    xPath = dom.createXPath("./@SCORE");
                    double score = Double.parseDouble(xPath.selectSingleNode(result).getText().trim());
                    System.out.println("     TOPIC DOC: " + rank + " " + docno + " " + score);
                    DBServer.addTopicDoc(idTopic,docno,title,"not-defined",admin.getUsername(),poolId,score,rank,html);

                }
                DBServer.getConnection().commit();
            }
        }
        catch(SQLException e)
        {
            DBServer.getConnection().setAutoCommit(true);
            reader.close();
            throw e;
        }
        catch(RuntimeException e)
        {
            DBServer.getConnection().setAutoCommit(true);
            reader.close();
            throw e;
        } catch (EOFException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    public static String getHtml(String docno,String desc, String narr, String sgml, String placeMaker, String timexes) throws DocumentException, EOFException, IOException
    {
        System.out.println("Trying : " + docno);
//        System.out.println("SGML:" + sgml);
        StringBuilder annotatedText = new StringBuilder();



        String sgmlWithOutTags = sgml.replaceAll("<!\\-[^\\->]+\\->", "TT");
        sgmlWithOutTags = sgmlWithOutTags.replaceAll("<[^>]+>", "");
        if(docno.startsWith("KT99"))
            sgmlWithOutTags = sgmlWithOutTags.replaceFirst("                                   ","");
//        System.out.println("SGML NO TAGS:" + sgmlWithOutTags);
        int pos = 0;
        NyTimesDocument nyt;
        if(docno.startsWith("KT"))
            nyt = new KoreaTimesDocument(new BufferedReader(new StringReader(sgml)),"dummy.txt");
        else if(docno.startsWith("EN"))
            nyt = new MainichiDailyDocument(new BufferedReader(new StringReader(sgml)),"dummy.txt");
        else
            nyt = new NyTimesDocument(new BufferedReader(new StringReader(sgml)),"dummy.txt");
//        NyTimesDocument nyt = new NyTimesDocument(new BufferedReader(new StringReader(sgml)), "dummy.txt");
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

        String annotatedTextStr = annotatedText.toString().replace("\n","<br>");

        org.apache.lucene.analysis.TokenStream stream = pt.utl.ist.lucene.treceval.IndexCollections.en.getAnalyzerWithStemming().tokenStream("",new java.io.StringReader(desc + " " + narr));
        org.apache.lucene.analysis.Token t;
        while((t=stream.next())!=null)
        {
            String str = t.termText();
            String Str = str.substring(0,1).toUpperCase()+str.substring(1);
            String STR = str.toUpperCase();
            if(str.trim().length()>1)
            {
                if(!str.equalsIgnoreCase("place") && str.length() > 3)
                {
                annotatedTextStr = annotatedTextStr.replaceAll(str,"<label class=\"word\">"  + str + "</label>");
                annotatedTextStr = annotatedTextStr.replaceAll(Str,"<label class=\"word\">" + Str + "</label>");
                annotatedTextStr = annotatedTextStr.replaceAll(STR,"<label class=\"word\">" + STR + "</label>");
                }
            }
        }

        return annotatedTextStr;
    }



    public static String getHtml2(String docno,String desc, String narr, String sgml, String placeMaker, String timexes) throws DocumentException, EOFException, IOException
    {
        StringBuilder annotatedText = new StringBuilder();
        String sgmlWithOutTags = sgml.replaceAll("<[^>]+>", "");
        int pos = 0;
        NyTimesDocument nyt;
        if(docno.startsWith("KT_"))
            nyt = new KoreaTimesDocument(new BufferedReader(new StringReader(sgml)),"dummy.txt");
        else if(docno.startsWith("EN"))
            nyt = new MainichiDailyDocument(new BufferedReader(new StringReader(sgml)),"dummy.txt");
        else
            nyt = new NyTimesDocument(new BufferedReader(new StringReader(sgml)),"dummy.txt");
//        NyTimesDocument nyt = new NyTimesDocument(new BufferedReader(new StringReader(sgml)), "dummy.txt");
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
                    annotatedText.append("<label class=\"PLACE\"><a href=\"javascript:abreMapa(" + nowPlaceRef.getPlaceDetails().getCentroide().getLat() + "," +nowPlaceRef.getPlaceDetails().getCentroide().getLng() + ")\">");
                    annotatedText.append(sgmlWithOutTags.substring(startOffsetPlaceRef, endOffsetPlaceRef + 1));
                    annotatedText.append("</a></label>");
                    lastOffset = endOffsetPlaceRef;
                    pos = endOffsetPlaceRef + 1;
                }
                nowPlaceRef = null;
            } else {

            }
        }
        if (sgmlWithOutTags.length() > pos + 1)
            annotatedText.append(sgmlWithOutTags.substring(pos + 1));

        String annotatedTextStr = annotatedText.toString().replace("\n","<br>");

        org.apache.lucene.analysis.TokenStream stream = pt.utl.ist.lucene.treceval.IndexCollections.en.getAnalyzerWithStemming().tokenStream("",new java.io.StringReader(desc + " " + narr));
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

        return annotatedTextStr;
    }


    public static void main(String[] args) throws IOException, DocumentException, SQLException
    {
//        args = new String[]{"C:\\Documents and Settings\\jmachado\\Os meus documentos\\Downloads\\ENruns-pd50.xml","NtcirGeoTime2010","C:\\WORKSPACE_JM\\DATA\\INDEXES\\NTCIR\\TEXT_TEMP_GEO_DB","C:\\WORKSPACE_JM\\DATA\\INDEXES\\NTCIR\\contents"};

        args = new String[]{"F:\\COLECCOES\\GeoTime\\ENruns-pd100-50.xml","NtcirGeoTime2011","F:\\COLECCOES\\ntcir\\INDEXES\\TEXT_TEMP_GEO_DB","F:\\COLECCOES\\ntcir\\INDEXES\\contents"};

        String file = args[0];
        String task = args[1];
        String index = args[2];
        String indexTitle = args[3];
        importPool(file,task,index,indexTitle);
    }
}
