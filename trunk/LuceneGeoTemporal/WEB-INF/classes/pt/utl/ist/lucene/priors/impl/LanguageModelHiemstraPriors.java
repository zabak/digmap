package pt.utl.ist.lucene.priors.impl;

import pt.utl.ist.lucene.priors.DocumentPriors;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Jorge
 * @date 9/Fev/2009
 * @time 19:37:41
 */
public class LanguageModelHiemstraPriors implements DocumentPriors
{
    private static Logger logger = Logger.getLogger(LanguageModelHiemstraPriors.class);

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
//            float collSize = getColSize((LanguageModelIndexReader) reader);
//            float probabilityDocument = docLen / collSize;
//            return sumOfTermsScores + (float) Math.log10(1.0f + probabilityDocument); //- (float)Math.exp(Math.log10(1.0f/collSize));


            float collSize = getColSize((LanguageModelIndexReader) reader);
//            float probabilityDocument = docLen / collSize;
//            return sumOfTermsScores + (float) Math.log10(1.0f + probabilityDocument); //- (float)Math.exp(Math.log10(1.0f/collSize));
            return (float) (Math.exp(sumOfTermsScores + Math.log(docLen / collSize)) * Math.log(docLen)) ;
        }
        catch (IOException e)
        {
            logger.error(e,e);
            return 0;
        }
//        return sumOfTermsScores;
    }

    public static void main(String[] args)
    {
        System.out.println(Math.exp(Math.log(2) + Math.log(2)));
    }
}
