package pt.utl.ist.lucene.model;

import pt.utl.ist.lucene.Model;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;

/**
 * @author Jorge Machado
 * @date 12/Mai/2010
 * @time 12:18:44
 * @email machadofisher@gmail.com
 */
public class ModelInitBm25Normalized  implements Model.ModelInit {

    private static Logger logger = Logger.getLogger(ModelInitBm25Normalized.class);
    static ModelInitBm25Normalized instance = new ModelInitBm25Normalized();

    public static ModelInitBm25Normalized getInstance()
    {
        return instance;
    }

    private ModelInitBm25Normalized(){}

    private Map<String,FieldContainer> maxFieldScore = new HashMap<String,FieldContainer>();

    public void init()
    {
        logger.info("INIT ModelInitBm25Normalized clearing maxFieldScores");
        maxFieldScore.clear();
    }

    public void updateMaxs(String field, Double value, int doc, int numDocs)
    {
        FieldContainer fieldContainer = maxFieldScore.get(field);
        if(fieldContainer == null)
        {
            fieldContainer = new FieldContainer();
            fieldContainer.docs = new double[numDocs];
            maxFieldScore.put(field,fieldContainer);
        }
        Double maxScore = fieldContainer.max;
        double docTotal = fieldContainer.docs[doc];
        docTotal+=value;
        fieldContainer.docs[doc] = docTotal;

        if(maxScore < docTotal)
            fieldContainer.max = docTotal;
    }

    public Double getMax(String field)
    {
        return maxFieldScore.get(field).max;
    }


    public static class FieldContainer
    {

        double max = 0.0;
        double[] docs;

    }

}
