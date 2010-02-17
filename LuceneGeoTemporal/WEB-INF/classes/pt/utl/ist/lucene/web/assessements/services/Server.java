package pt.utl.ist.lucene.web.assessements.services;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import pt.utl.ist.lucene.config.ConfigProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 12:09:44
 * To change this template use File | Settings | File Templates.
 */
public class Server
{

    public static void importPool(HttpServletRequest request)
    {
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
    }
}
