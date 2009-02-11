package pt.utl.ist.lucene.models;

import org.apache.lucene.index.IndexReader;

/**
 * @author Jorge
 * @date 9/Fev/2009
 * @time 17:54:55
 */
public interface DocumentFinalScorer
{
    public float computeFinalScore(float sumOfTermsScores, IndexReader reader, int docLen);
}
