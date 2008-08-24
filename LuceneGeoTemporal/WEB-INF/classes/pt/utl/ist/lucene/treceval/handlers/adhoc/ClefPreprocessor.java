package pt.utl.ist.lucene.treceval.handlers.adhoc;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.net.MalformedURLException;

import pt.utl.ist.lucene.treceval.handlers.adhoc.IndexField;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.handlers.collections.ICollectionPreprocessor;
import pt.utl.ist.lucene.treceval.dublincore.DcFieldEnum;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.apache.log4j.Logger;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:31:53
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class ClefPreprocessor implements ICollectionPreprocessor
{

    private static final Logger logger = Logger.getLogger(ClefPreprocessor.class);

    
    public void handle(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException
    {
        File dir = new File(collectionPath);
        File[] files = dir.listFiles();

        int i = 0;
        int totalElements = 0;
        for (File f : files)
        {
            i++;
            System.out.println("indexing file " + i + "...");
            int elementsCounter = 0;
            Document dom = Dom4jUtil.parse(f);

            for (Object e : dom.getRootElement().elements())
            {
                elementsCounter++;

                Element element = (Element) e;
                Element id = null;
                if (element.element("header") != null)
                    id = element.element("header").element("id");
                else if (element.element("id") != null)
                    id = element.element("id");
                else
                {
                    System.out.println("Header ID Fail: \n" + element.getText());
                }

//                StringBuilder all1Str = new StringBuilder();
//                StringBuilder all2Str = new StringBuilder();
//                StringBuilder all3Str = new StringBuilder();
//                StringBuilder all4Str = new StringBuilder();
//                StringBuilder all5Str = new StringBuilder();
                StringBuilder all1Str = null;
                StringBuilder all2Str = null;
                StringBuilder all3Str = null;
                StringBuilder all4Str = null;
                StringBuilder all5Str = new StringBuilder();

                Map<String,String> indexFields = getRecordIndexFields("", element,all1Str,all2Str,all3Str,all4Str,all5Str);

//                indexFields.add(new IndexField("contents",all1Str.toString()));
//                indexFields.add(new IndexField("contentsTitle2Subject2",all2Str.toString()));
//                indexFields.add(new IndexField("contentsTitle3Subject2",all3Str.toString()));
//                indexFields.add(new IndexField("contentsFilteredTitle2Subject2",all4Str.toString()));
                indexFields.put("contentsFilteredTitle3Subject2",all5Str.toString());
                

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
        System.out.println("processed: " + totalElements);
    }

    private Map<String,String> getRecordIndexFields(String parent, Element e, StringBuilder all1Str, StringBuilder all2Str, StringBuilder all3Str,StringBuilder all4Str,StringBuilder all5Str)
    {

        Map<String,String> indexFields = new HashMap<String,String>();
        String name = parent + "/" + e.getQualifiedName();
        Iterator eIter = e.elementIterator();
        if (!eIter.hasNext())
        {
            IndexField small = new IndexField(e.getName(), e.getText());
//            indexFields.add(small);

            DcFieldEnum dcFieldEnum = DcFieldEnum.parse(small.getName());

            //All 1x
//            all1Str.append(" ").append(small.getValue());
//
//
//
//            //All 1x title 2 and 3  subject 2 and 2
//            if(dcFieldEnum == DcFieldEnum.title)
//            {
//                all2Str.append(" ").append(small.getValue()).append(" ").append(small.getValue());
//                all3Str.append(" ").append(small.getValue()).append(" ").append(small.getValue()).append(" ").append(small.getValue());
//            }
//            else if(dcFieldEnum == DcFieldEnum.subject)
//            {
//                all2Str.append(" ").append(small.getValue()).append(" ").append(small.getValue());
//                all3Str.append(" ").append(small.getValue()).append(" ").append(small.getValue());
//            }
//            else
//            {
//                all2Str.append(" ").append(small.getValue());
//                all3Str.append(" ").append(small.getValue());
//            }


            //title title (subject subject)*n creator contributor description location publisher issued
            //title title title (subject subject)*n creator contributor description location publisher issued
            if(dcFieldEnum == DcFieldEnum.title)
            {
//                all4Str.append(" ").append(small.getValue()).append(" ").append(small.getValue());
                all5Str.append(" ").append(small.getValue()).append(" ").append(small.getValue()).append(" ").append(small.getValue());
            }
            else if(dcFieldEnum == DcFieldEnum.subject)
            {
//                all4Str.append(" ").append(small.getValue()).append(" ").append(small.getValue());
                all5Str.append(" ").append(small.getValue()).append(" ").append(small.getValue());
            }
            else if(dcFieldEnum == DcFieldEnum.creator ||
                    dcFieldEnum == DcFieldEnum.contributor ||
                    dcFieldEnum == DcFieldEnum.description ||
                    dcFieldEnum == DcFieldEnum.location ||
                    dcFieldEnum == DcFieldEnum.issued ||
                    dcFieldEnum == DcFieldEnum.relation)
            {
//                all4Str.append(" ").append(small.getValue());
                all5Str.append(" ").append(small.getValue());
            }
        }
        else
        {
            while (eIter.hasNext())
            {
                Element element = (Element) eIter.next();
                Map<String,String> innerIndexFields = getRecordIndexFields(name, element, all1Str,all2Str,all3Str,all4Str,all5Str);
                if (innerIndexFields.size() > 0)
                    indexFields.putAll(innerIndexFields);
            }
        }
        return indexFields;
    }

}
