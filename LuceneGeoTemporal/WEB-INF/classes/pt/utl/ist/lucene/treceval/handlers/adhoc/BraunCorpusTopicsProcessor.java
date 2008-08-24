package pt.utl.ist.lucene.treceval.handlers.adhoc;

import pt.utl.ist.lucene.treceval.ISearchCallBack;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.DefaultTopic;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormat;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 4:39:55
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class BraunCorpusTopicsProcessor implements ITopicsPreprocessor
{

    public void handle(String path, ISearchCallBack callBack, String confId, String run,String collection,String outputDir) throws MalformedURLException, DocumentException

    {
        File dir = new File(path);
        File[] files = dir.listFiles();
        int i = 0;
        File responsesDir = new File(outputDir);
        for(File f: files)
        {
            if(f.isFile())
            {
                i++;
                System.out.println("creating searchCallback responses for file " + i + "..." + f.getName());

                responsesDir.delete();
                responsesDir.mkdirs();
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(responsesDir.getAbsolutePath() + "/" + f.getName() + "-" + collection + "-" + run + "-" + confId + ".txt");
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                Document dom;
                try
                {
                    dom = Dom4jUtil.parse(f);
                }
                catch (DocumentException e)
                {
                    e.printStackTrace();
                    return;
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                    return;
                }
                TrecEvalOutputFormat outputFormat =  new TrecEvalOutputFormat();
                outputFormat.init("id", "");
                outputFormat.setOutputStream(outputStream);
                for(Object e: dom.getRootElement().elements())
                {

                    Element element = (Element) e;
                    String identifier = element.element("num").getText();
                    String title = element.element("title").getText();
                    String description = element.element("desc").getText();

                    System.out.println("processing topic: (" + identifier + ")" + title);

                    Topic t = new DefaultTopic(identifier,title,description,"ger",outputFormat);

                    callBack.searchCallback(t);

                }
                try
                {
                    outputFormat.close();
                    outputStream.flush();
                    outputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                System.gc();
            }
        }
        System.out.println("job done");
    }
}
