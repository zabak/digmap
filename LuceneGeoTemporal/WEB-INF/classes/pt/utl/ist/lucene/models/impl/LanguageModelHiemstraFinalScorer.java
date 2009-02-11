package pt.utl.ist.lucene.models.impl;

import pt.utl.ist.lucene.models.DocumentFinalScorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Jorge
 * @date 9/Fev/2009
 * @time 19:37:41
 */
public class LanguageModelHiemstraFinalScorer implements DocumentFinalScorer
{
    private static Logger logger = Logger.getLogger(LanguageModelHiemstraFinalScorer.class);

    private float collSize = -1;

    private float getColSize(LanguageModelIndexReader reader) throws IOException
    {
        if(collSize < 0)
            collSize = (float) reader.getTotalDocFreqs();
        return collSize;
    }
    public float computeFinalScore(float sumOfTermsScores, IndexReader reader, int docLen)
    {
        try
        {
            float collSize = getColSize((LanguageModelIndexReader) reader);
            float probabilityDocument = docLen / collSize;
            return sumOfTermsScores + (float) Math.log10(1.0f + probabilityDocument); //- (float)Math.exp(Math.log10(1.0f/collSize));
        }
        catch (IOException e)
        {
            logger.error(e,e);
        }
        return sumOfTermsScores;
    }

    public static void main(String[] args)
    {
        System.out.println(Math.exp(Math.log(2) + Math.log(2)));
    }
}
