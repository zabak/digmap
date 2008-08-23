package pt.utl.ist.lucene.treceval.preprocessors.adhoc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import pt.utl.ist.lucene.treceval.ISearchCallBack;
import pt.utl.ist.lucene.treceval.ITopicsProcessor;
import pt.utl.ist.lucene.treceval.output.Topic;
import pt.utl.ist.lucene.treceval.output.impl.DefaultTopic;
import pt.utl.ist.lucene.treceval.output.impl.TrecEvalOutputFormat;
import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 4:40:19
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class ClefTopicsProcessor implements ITopicsProcessor
{

    public void processPath(String path, String confId, ISearchCallBack iSearchCallBack, String run, String collection, String output)
    {
        File dir = new File(path);
        File[] files = dir.listFiles();
        int i = 0;
        for (File f : files)
        {
            if (f.isFile())
            {
                i++;
                System.out.println("creating searchCallback responses for file " + i + "..." + f.getName());
                File responsesDir = new File(output);
                responsesDir.delete();
                responsesDir.mkdirs();
                FileOutputStream outputStream;
                try
                {
                    outputStream = new FileOutputStream(responsesDir.getAbsolutePath() + "/" + f.getName() + "-" + collection + "-" + run + "-" + confId + ".txt");
                }
                catch (FileNotFoundException e)
                {
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
                TrecEvalOutputFormat outputFormat = new TrecEvalOutputFormat(outputStream, "id", null);
                for (Object e : dom.getRootElement().elements())
                {
                    Element element = (Element) e;
                    String identifier = element.element("identifier").getText();
                    String title = element.element("title").getText();
                    String description = element.element("description").getText();
                    String lang = element.attribute("lang").getText();
                    System.out.println("processing topic: (" + identifier + ")" + title);
                    Topic t = new DefaultTopic(identifier, title, description, lang, outputFormat);
                    iSearchCallBack.searchCallback(t, outputStream, 2000, 1000);
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
