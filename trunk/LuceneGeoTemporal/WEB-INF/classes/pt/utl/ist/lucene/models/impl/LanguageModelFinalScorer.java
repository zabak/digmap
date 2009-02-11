package pt.utl.ist.lucene.models.impl;

import pt.utl.ist.lucene.models.DocumentFinalScorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.ilps.DataCacher;

/**
 * @author Jorge
 * @date 9/Fev/2009
 * @time 19:34:31
 */
public class LanguageModelFinalScorer implements DocumentFinalScorer
{
    float beta = -1.0f;
    float log10 = (float) Math.log(10);

    private float getLmBeta()
    {
        if (beta == -1.0f)
            beta = (DataCacher.Instance().get("LM-beta") != null)
                    ? (Float.valueOf((String) DataCacher.Instance().get("LM-beta")))
                    .floatValue() : 1.0f;
        return beta;
    }

    public float computeFinalScore(float sumOfTermsScores, IndexReader reader, int docLen)
    {
        return sumOfTermsScores + getLmBeta() * (float) Math.log(docLen) / log10;
    }
}
