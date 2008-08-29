package pt.utl.ist.lucene.treceval.handlers.adhoc;

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
import java.util.zip.ZipEntry;
import java.util.zip.GZIPInputStream;

import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.handlers.collections.ICollectionPreprocessor;
import pt.utl.ist.lucene.utils.Dom4jUtil;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:31:53
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class LatEnGh95Preprocessor implements ICollectionPreprocessor
{



    private static final Logger logger = Logger.getLogger(ClefPreprocessor.class);


    public void handle(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException
    {
        runLatEn(null,collectionPath + "/lat-en",callBack,true,null);
        runGh95(null,collectionPath + "/gh-95",callBack,true,null);
    }

    public void runGh95(IndexWriter writer, String collectionPath, IndexFilesCallBack callBack, boolean storeTermVectors, String collection) throws MalformedURLException, DocumentException
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
                    ZipEntry ze;

                    byte[] buffer = new byte[1024];
                    // decompress the file
                    new File(collectionPath + "/out").mkdirs();
                    FileOutputStream fo = new FileOutputStream(new File(collectionPath + "/out/out.xml"));
                    fo.write("<docs>".getBytes());
                    int length;
                    while ((length = gz.read(buffer, 0, 1024)) != -1)
                    {
                        String newStr = EscapeChars.forXMLOnlySpecialInternal(new String(buffer, 0, length));
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

                        Map<String,String> indexFields = getRecordIndexFieldsGh95("", element);

                        try
                        {
                            if(id == null)
                                logger.error("ERROR: id come null");
                            else
                                callBack.indexDoc(id.getText(),indexFields,null,null);
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
     * Lat EN Fields
     * @param writer
     * @param collectionPath
     * @param callBack
     * @param storeTermVectors
     * @param collection
     * @throws MalformedURLException
     * @throws DocumentException
     */

    public void runLatEn(IndexWriter writer, String collectionPath, IndexFilesCallBack callBack, boolean storeTermVectors, String collection) throws MalformedURLException, DocumentException
    {
        File dir = new File(collectionPath);
        File[] files = dir.listFiles();

        new File(collectionPath + "/out").mkdirs();
        int i = 0;
        int totalElements = 0;
        for (File f : files)
        {
            if(f.isFile() && f.getName().endsWith(".gz"))
            {
                i++;
                System.out.println("indexing file " + i + "...");
                int elementsCounter = 0;

                try
                {
                    GZIPInputStream gz = new GZIPInputStream(new FileInputStream(f));
                    ZipEntry ze;

                    byte[] buffer = new byte[1024];
                    // decompress the file
                    new File(collectionPath + "/out").mkdirs();
                    FileOutputStream fo = new FileOutputStream(new File(collectionPath + "/out/out.xml"));
                    fo.write("<docs>".getBytes());
                    int length;
                    while ((length = gz.read(buffer, 0, 1024)) != -1)
                    {
                        String newStr = EscapeChars.forXMLOnlySpecialInternal(new String(buffer, 0, length));
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

                        Map<String,String> indexFields = getRecordIndexFieldsLatEn("", element);

                        try
                        {
                            if(id == null)
                                logger.error("ERROR: id come null");
                            else
                                callBack.indexDoc(id.getText(),indexFields,null,null);
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

        System.out.println("processed: " + totalElements);
    }

    private Map<String,String> getRecordIndexFieldsLatEn(String parent, Element e)
    {

        Map<String,String> indexFields = new HashMap<String,String>();

        String headLine = TagsUtils.getMultipleElementsText(e,"HEADLINE");
        String source = TagsUtils.getMultipleElementsText(e,"SOURCE");
        String author = TagsUtils.getMultipleElementsText(e,"BYLINE");
        String type = TagsUtils.getMultipleElementsText(e,"TYPE");
        String date = TagsUtils.getMultipleElementsText(e,"DATE");

        TagsUtils.FirstSecondLines firstSecondLines = TagsUtils.readFirstSecondLinesSubelementsFirstDot(e,"TEXT","P");
        String firstLine = firstSecondLines.getFirstLine();
        String secondLine = firstSecondLines.getSecondLine();
        String text = firstSecondLines.getAllText();


        StringBuilder str = new StringBuilder();
        str
                .append(headLine)
                .append(' ')
                .append(headLine)
                .append(' ')
                .append(headLine)
                .append(' ')
                .append(firstLine)
                .append(' ')
                .append(secondLine)
                .append(' ')
                .append(text)
                .append(' ')
                .append(date)
                .append(' ')
                .append(type)
                .append(' ')
                .append(type)
                .append(' ')
                .append(author)
                .append(' ')
                .append(source);


        indexFields.put("contents", str.toString());
        return indexFields;
    }

    private Map<String,String> getRecordIndexFieldsGh95(String parent, Element e)
    {

        Map<String,String> indexFields = new HashMap<String,String>();

        TagsUtils.FirstSecondLines firstSecondLines = TagsUtils.readFirstSecondLinesFirstDot(e,"TEXT");

        String headLine = TagsUtils.getMultipleElementsText(e,"HEADLINE");
        String author = TagsUtils.getMultipleElementsText(e,"BYLINE");
//        String source = TagsUtils.getMultipleElementsText(e,"SOURCE");
        String type = TagsUtils.getMultipleElementsText(e,"ARTICLETYPE");
        String flag = TagsUtils.getMultipleElementsText(e,"FLAG");
        String text = firstSecondLines.getAllText();
        String date = TagsUtils.parseDate(e,"DATE");
        String firstLine = firstSecondLines.getFirstLine();
        String secondLine = firstSecondLines.getSecondLine();

        if(e.element("CORRECTION") != null)
        {
            System.out.println("CORRECTION FOUND:" + e.element("CORRECTION").getStringValue());
        }
       
        StringBuilder str = new StringBuilder();
        str
                .append(headLine)
                .append(' ')
                .append(headLine)
                .append(' ')
                .append(headLine)
                .append(' ')
//                .append(source)
//                .append(' ')
                .append(firstLine)
                .append(' ')
                .append(secondLine)
                .append(' ')
                .append(text)
                .append(' ')
                .append(type)
                .append(' ')
                .append(type)
                .append(' ')
                .append(flag)
                .append(' ')
                .append(flag)
                .append(' ')
                .append(date)
                .append(' ')
                .append(author);

        indexFields.put("contents", str.toString());
        return indexFields;
    }






    public static void main(String[] args)
    {
        System.out.println("950222".substring(2,4));
    }


}

  