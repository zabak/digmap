package pt.utl.ist.lucene.priors.impl;

import pt.utl.ist.lucene.priors.DocumentPriors;
import pt.utl.ist.lucene.ModelManager;
import pt.utl.ist.lucene.QueryConfiguration;
import org.apache.lucene.index.IndexReader;

import java.util.Properties;

/**
 * @author Jorge
 * @date 9/Fev/2009
 * @time 19:34:31
 */
public class LanguageModelPriors implements DocumentPriors
{
    float beta = -1.0f;
    float log10 = (float) Math.log(10);

    private float getLmBeta()
    {
        if (beta == -1.0f)
        {
            Properties modelProperties = ModelManager.getInstance().getModelProperties();
            QueryConfiguration queryConfiguration = ModelManager.getInstance().getQueryConfiguration();
            beta = queryConfiguration.getFloatProperty("LM-beta",modelProperties);
        }
        return beta;
    }

    public float computeFinalScore(float sumOfTermsScores, IndexReader reader, int docLen)
    {
        return sumOfTermsScores + getLmBeta() * (float) Math.log(docLen) / log10;
    }
}
