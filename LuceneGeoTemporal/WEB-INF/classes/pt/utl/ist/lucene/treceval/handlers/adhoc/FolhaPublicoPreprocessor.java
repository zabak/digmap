package pt.utl.ist.lucene.treceval.handlers.adhoc;

import pt.utl.ist.lucene.treceval.handlers.collections.ICollectionPreprocessor;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.util.EscapeChars;
import pt.utl.ist.lucene.treceval.util.TagsUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.dom4j.DocumentException;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.MalformedURLException;
import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:31:53
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class FolhaPublicoPreprocessor implements ICollectionPreprocessor
{



    private static final Logger logger = Logger.getLogger(ClefPreprocessor.class);


    public void handle(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException
    {
        runFolha(null,collectionPath + "/folha-pt",callBack,true,null);
        runPublico(null,collectionPath + "/publico-pt",callBack,true,null);
    }

    public void runFolha(IndexWriter writer, String collectionPath, IndexFilesCallBack callBack, boolean storeTermVectors, String collection) throws MalformedURLException, DocumentException
    {
        File dir = new File(collectionPath);
        File[] files = dir.listFiles();
        int totalElements = 0;
        new File(collectionPath + "/out").mkdirs();
        int i = 0;
        for (File f : files)
        {
            if(f.isFile() && f.getName().endsWith(".sgml"))
            {
                i++;
                System.out.println("indexing file " + i + "...");
                int elementsCounter = 0;

                try
                {


                    FileInputStream gz = new FileInputStream(f);
                    byte[] buffer = new byte[1024];
                    // decompress the file
                    new File(collectionPath + "/out").mkdirs();
                    FileOutputStream fo = new FileOutputStream(new File(collectionPath + "/out/out.xml"));
                    fo.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<docs>".getBytes());
                    int length;
                    while ((length = gz.read(buffer, 0, 1024)) != -1)
                    {
                        String newStr = EscapeChars.forXMLOnlySpecialInternal(new String(buffer, 0, length,"ISO-8859-1"));
                        fo.write(newStr.getBytes());
                    }
                    fo.write("</docs>".getBytes());
                    fo.close();
                    gz.close();

                    Document dom = Dom4jUtil.parse(new File(collectionPath + "/out/out.xml"));

                    for (Object e : dom.getRootElement().elements())
                    {
                        elementsCounter++;

                        Element element = (Element) e;
                        Element id = null;
                        if (element.element("DOCNO") != null)
                            id = element.element("DOCNO");
                        else
                        {
                            System.out.println("Header ID Fail: \n" + element.getText());
                        }

                        Map<String,String> indexFields = getRecordIndexFieldsFolha("", element);

                        try
                        {
                            if(id == null)
                                logger.error("ERROR: id come null");
                            else
                                callBack.indexDoc(id.getText(),indexFields);
                        }
                        catch (IOException e1)
                        {
                            logger.error(e1,e1);
                        }

                    }
                    System.out.print(" found:" + elementsCounter);
                    totalElements += elementsCounter;
                    dom.clearContent();
                    System.gc();
                }
                catch (IOException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    return;
                }

            }

        }
    }


    public void runPublico(IndexWriter writer, String collectionPath, IndexFilesCallBack callBack, boolean storeTermVectors, String collection) throws MalformedURLException, DocumentException
    {
         File dir = new File(collectionPath);
        File[] files = dir.listFiles();
        int totalElements = 0;
        new File(collectionPath + "/out").mkdirs();
        int i = 0;
        for (File f : files)
        {
            if(f.isFile() && f.getName().endsWith(".sgml"))
            {
                i++;
                System.out.println("indexing file " + i + "...");
                int elementsCounter = 0;

                try
                {


                    FileInputStream gz = new FileInputStream(f);
                    byte[] buffer = new byte[1024];
                    // decompress the file
                    new File(collectionPath + "/out").mkdirs();
                    FileOutputStream fo = new FileOutputStream(new File(collectionPath + "/out/out.xml"));
                    fo.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<docs>".getBytes());
                    int length;
                    while ((length = gz.read(buffer, 0, 1024)) != -1)
                    {
                        String newStr = EscapeChars.forXMLOnlySpecialInternal(new String(buffer, 0, length,"ISO-8859-1"));
                        fo.write(newStr.getBytes());
                    }
                    fo.write("</docs>".getBytes());
                    fo.close();
                    gz.close();

                    Document dom = Dom4jUtil.parse(new File(collectionPath + "/out/out.xml"));

                    for (Object e : dom.getRootElement().elements())
                    {
                        elementsCounter++;

                        Element element = (Element) e;
                        Element id = null;
                        if (element.element("DOCNO") != null)
                            id = element.element("DOCNO");
                        else
                        {
                            System.out.println("Header ID Fail: \n" + element.getText());
                        }

                        Map<String,String> indexFields = getRecordIndexFieldsPublico("", element);

                        try
                        {
                            if(id == null)
                                logger.error("ERROR: id come null");
                            else
                                callBack.indexDoc(id.getText(),indexFields);
                        }
                        catch (IOException e1)
                        {
                            logger.error(e1,e1);
                        }

                    }
                    System.out.print(" found:" + elementsCounter);
                    totalElements += elementsCounter;
                    dom.clearContent();
                    System.gc();
                }
                catch (IOException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    return;
                }

            }

        }
    }

    /**
     * RecordFields Folha
     * @param parent
     * @param e
     * @return
     */
    private Map<String,String> getRecordIndexFieldsFolha(String parent, Element e)
    {

        Map<String,String> indexFields = new HashMap<String,String>();


        String date = TagsUtils.parseDatePt(e,"DATE");
        String type = TagsUtils.getMultipleElementsText(e,"CATEGORY");
        String author = TagsUtils.getMultipleElementsText(e,"AUTHOR");

        TagsUtils.FirstSecondLines firstSecondLines = TagsUtils.readFirstSecondLinesFirstDot(e,"TEXT");
        String text = firstSecondLines.getAllText();
        String firstLine = firstSecondLines.getFirstLine();
        String secondLine = firstSecondLines.getSecondLine();


        StringBuilder str = new StringBuilder();
        str
                .append(firstLine)
                .append(' ')
                .append(firstLine)
                .append(' ')
                .append(firstLine)
                .append(' ')
                .append(secondLine)
                .append(' ')
                .append(text)
                .append(' ')
                .append(date)
                .append(' ')
                .append(author)
                .append(' ')
                .append(type)
                .append(' ')
                .append(type);


        indexFields.put("contents", str.toString());

        return indexFields;
    }

    /**
     * RecordFields Folha
     * 
     *
     * @param parent
     * @param e
     * @return
     */
    private Map<String,String> getRecordIndexFieldsPublico(String parent, Element e)
    {

        Map<String,String> indexFields = new HashMap<String,String>();


        String date = TagsUtils.parseDatePt(e,"DATE");
        String type = TagsUtils.getMultipleElementsText(e,"CATEGORY");
        String author = TagsUtils.getMultipleElementsText(e,"AUTHOR");

        TagsUtils.FirstSecondLines firstSecondLines = TagsUtils.readFirstSecondLinesFirstDot(e,"TEXT");
        String text = firstSecondLines.getAllText();
        String firstLine = firstSecondLines.getFirstLine();
        String secondLine = firstSecondLines.getSecondLine();


        StringBuilder str = new StringBuilder();
        str
                .append(firstLine)
                .append(' ')
                .append(firstLine)
                .append(' ')
                .append(firstLine)
                .append(' ')
                .append(secondLine)
                .append(' ')
                .append(text)
                .append(' ')
                .append(date)
                .append(' ')
                .append(author)
                .append(' ')
                .append(type)
                .append(' ')
                .append(type);


        indexFields.put("contents", str.toString());

        return indexFields;
    }

    public static void main(String[] args)
    {
        System.out.println("950222".substring(2,4));
    }


}
