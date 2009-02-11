package pt.utl.ist.lucene.models.impl;

import pt.utl.ist.lucene.models.DocumentFinalScorer;
import org.apache.lucene.index.IndexReader;

/**
 * @author Jorge
 * @date 9/Fev/2009
 * @time 19:37:41
 */
public class DoNothingFinalScorer implements DocumentFinalScorer
{
    public float computeFinalScore(float sumOfTermsScores, IndexReader reader, int docLen)
    {
        return sumOfTermsScores;
    }
}
