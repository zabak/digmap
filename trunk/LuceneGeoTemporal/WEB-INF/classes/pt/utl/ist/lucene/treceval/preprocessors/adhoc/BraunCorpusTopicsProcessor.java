package pt.utl.ist.lucene.treceval.preprocessors.adhoc;

import pt.utl.ist.lucene.treceval.ISearchCallBack;
import pt.utl.ist.lucene.treceval.ITopicsProcessor;
import pt.utl.ist.lucene.treceval.output.Topic;
import pt.utl.ist.lucene.treceval.output.impl.DefaultTopic;
import pt.utl.ist.lucene.treceval.output.impl.TrecEvalOutputFormat;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import pt.utl.ist.lucene.QEEnum;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 4:39:55
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class BraunCorpusTopicsProcessor implements ITopicsProcessor
{

    public void processPath(String path, String confId, ISearchCallBack iSearchCallBack, String run, String collection, String outputDir)
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
                TrecEvalOutputFormat outputFormat =  new TrecEvalOutputFormat(outputStream, "id", "");
                for(Object e: dom.getRootElement().elements())
                {

                    Element element = (Element) e;
                    String identifier = element.element("num").getText();
                    String title = element.element("title").getText();
                    String description = element.element("desc").getText();

                    System.out.println("processing topic: (" + identifier + ")" + title);

                    Topic t = new DefaultTopic(identifier,title,description,"ger",outputFormat);

                    iSearchCallBack.searchCallback(t,outputStream,1000,400);

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
