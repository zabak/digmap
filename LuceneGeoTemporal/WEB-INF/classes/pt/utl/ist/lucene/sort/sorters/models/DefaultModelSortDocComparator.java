package pt.utl.ist.lucene.sort.sorters.models;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.index.IndexReader;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.sort.ModelSortDocComparator;

import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 17/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public class DefaultModelSortDocComparator implements ModelSortDocComparator
{
    HashMap<Integer,Float> scoresCache;

    private float timeFactor;
    private float spatialFactor;
    private float textFactor;
    private QueryParams queryParams;

    private SpatialDistancesScoreDocComparator spatialScoreDocComparator = null;
    private TimeDistancesScoreDocComparator timeScoreDocComparator = null;
    private LgteScoreDocComparator textScoreDocComparator = null;

//    private QueryParams queryParams = null;

    public void initModel(TimeDistancesScoreDocComparator time,
                          SpatialDistancesScoreDocComparator spatial,
                          LgteScoreDocComparator text,
                          QueryParams queryParams,
                          IndexReader reader)
    {
        this.spatialScoreDocComparator = spatial;
        this.timeScoreDocComparator = time;
        this.textScoreDocComparator = text;


        this.queryParams = queryParams;
        this.timeFactor = queryParams.getQueryConfiguration().getFloatProperty("default.model.time.factor");
        spatialFactor = queryParams.getQueryConfiguration().getFloatProperty("default.model.spatial.factor");
        textFactor = queryParams.getQueryConfiguration().getFloatProperty("default.model.text.factor");

        scoresCache = new HashMap<Integer,Float>();
    }

    public void cleanUp()
    {
        if(spatialScoreDocComparator != null)
            spatialScoreDocComparator.cleanUp();
        if(timeScoreDocComparator != null)
            timeScoreDocComparator.cleanUp();
        if(textScoreDocComparator != null)
            textScoreDocComparator.cleanUp();
        queryParams = null;
        scoresCache.clear();
    }

    public float getScore(int doc, float score)
    {
        return (Float) sortValue(new ScoreDoc(doc,score));
    }

    public int compare(ScoreDoc scoreDoc1, ScoreDoc scoreDoc2)
    {
        Float score1 = (Float) sortValue(scoreDoc1);
        Float score2 = (Float) sortValue(scoreDoc2);

        if (score1 > score2) return -1;
        if (score1 < score2) return 1;
        return 0;
    }



    public Comparable sortValue(ScoreDoc scoreDoc)
    {
        Float score = scoresCache.get(scoreDoc.doc);
        if(score != null)
            return score;
        float spaceScore1 = 0f;
        float timeScore1 = 0f;
        float textScore1 = 0f;
        if(queryParams.getOrder().isSpatial() &&  spatialScoreDocComparator != null)
        {
            spaceScore1 = (Float) spatialScoreDocComparator.sortValue(scoreDoc);
        }
        if(queryParams.getOrder().isTime() && timeScoreDocComparator != null)
        {
            timeScore1 = (Float) timeScoreDocComparator.sortValue(scoreDoc);
        }
        if(queryParams.getOrder().isScore() && textScoreDocComparator != null)
        {
            textScore1 =  (Float) textScoreDocComparator.sortValue(scoreDoc);
        }
        score = timeScore1*timeFactor + spaceScore1*spatialFactor + textScore1*textFactor;
        scoresCache.put(scoreDoc.doc,score);
        return score;
    }

    public int sortType()
    {
        return SortField.FLOAT;
    }
}
