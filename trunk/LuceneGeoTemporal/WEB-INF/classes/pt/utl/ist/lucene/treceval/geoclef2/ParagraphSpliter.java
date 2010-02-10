package pt.utl.ist.lucene.treceval.geoclef2;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.utils.nlp.Paragraph;

import java.util.List;

public class ParagraphSpliter
{
    private static final Logger logger = Logger.getLogger(ParagraphSpliter.class);

    public static List split(GeoClefDocument d,Class subType) {
        String delim = "\n";
        String text = d.getSgmlWithoutTags();
        if(d.getFileName().endsWith(".gz"))
        {
            delim = "\n\n";
            text = text.replaceAll("\""," ");
        }
        else
        {
            text = text.replaceAll("\nn ","\n  ");
        }
        try
        {
            return pt.utl.ist.lucene.utils.nlp.ParagraphSpliter.split(text, subType,delim);
        }
        catch (IllegalAccessException e)
        {
            logger.error(e,e);
        }
        catch (InstantiationException e)
        {
            logger.error(e,e);
        }
        return null;
    }
}
