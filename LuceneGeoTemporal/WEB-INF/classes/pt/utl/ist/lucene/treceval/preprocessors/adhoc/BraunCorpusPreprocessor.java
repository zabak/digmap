package pt.utl.ist.lucene.treceval.preprocessors.adhoc;

import pt.utl.ist.lucene.treceval.Preprocessor;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.IndexField;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:31:53
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class BraunCorpusPreprocessor implements Preprocessor
{

    private static final Logger logger = Logger.getLogger(BraunCorpusPreprocessor.class);


    public void run(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException
    {
        File dir = new File(collectionPath);
        File[] files = dir.listFiles();
        int i = 0;
        int totalElements = 0;
        for(File f: files)
        {
            i++;
            System.out.println("indexing file " + i + "...");
            int elementsCounter = 0;
            Document dom = Dom4jUtil.parse(f);
            for(Object e: dom.getRootElement().elements())
            {
                elementsCounter++;

                Element element = (Element) e;
                Element id = null;
                if(element.element("DOCNO") != null)
                    id = element.element("DOCNO");
                else
                {
                    System.out.println("DOCNO Fail: \n" + element.getText());
                }

                Map<String,String> indexFields = new HashMap<String,String>();
                String text = element.element("TEXT").getText();
                BufferedReader reader = new BufferedReader(new StringReader(text));
                StringBuilder firstLines = new StringBuilder();
                try
                {
                    String line = reader.readLine();
                    for(int li = 0; li < 10 && line != null;li++)
                    {
                        if(line.indexOf("TITEL") < 0)
                            firstLines.append(" ").append(line);
                        line = reader.readLine();
                    }
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                indexFields.put("contents",firstLines.toString() + " " + firstLines.toString() + " "  + text);
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
        System.out.println("Total elements found in " + collectionPath + ":" + totalElements);
    }

    public Set<IndexField> getRecordIndexFields(String parent, Element e)
    {
        Set<IndexField> indexFields = new HashSet<IndexField>();
        String name = parent + "/" + e.getQualifiedName();
        Iterator eIter = e.elementIterator();
        if (!eIter.hasNext())
        {
            IndexField small = new IndexField(e.getName(), e.getText());
            indexFields.add(small);
        }
        else
        {
            while (eIter.hasNext())
            {
                Element element = (Element) eIter.next();
                Set<IndexField> innerIndexFields = getRecordIndexFields(name, element);
                if (innerIndexFields.size() > 0)
                    indexFields.addAll(innerIndexFields);
            }
        }
        return indexFields;
    }

}
