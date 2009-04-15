package pt.utl.ist.lucene.priors.impl;

import pt.utl.ist.lucene.priors.DocumentPriors;
import org.apache.lucene.index.IndexReader;

/**
 * @author Jorge
 * @date 9/Fev/2009
 * @time 19:37:41
 */
public class DoNothingPriors implements DocumentPriors
{
    public float computeFinalScore(float sumOfTermsScores, IndexReader reader, int docLen)
    {
        return sumOfTermsScores;
    }
}
