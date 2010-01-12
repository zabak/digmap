package pt.utl.ist.lucene.sort;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortComparatorSource;
import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;
import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import pt.utl.ist.lucene.filter.ITimeSpatialDistancesWrapper;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public class LgteSort extends Sort implements LgteScorer
{

    SortField[] sortFields = null;

    public LgteSort(org.apache.lucene.search.SortField sortField)
    {
        super(sortField);
        sortFields = new SortField[1];
        sortFields[0] = sortField;
    }

    public LgteSort(org.apache.lucene.search.SortField[] sortFields)
    {
        super(sortFields);
        this.sortFields = sortFields;
    }

    public void addTimeSpaceDistancesWrapper(ITimeSpatialDistancesWrapper timeSpaceDistancesWrapper)
    {
        cleanUp();
        addTimeDistancesWrapper(timeSpaceDistancesWrapper);
        addSpaceDistancesWrapper(timeSpaceDistancesWrapper);
    }

    public void addTimeDistancesWrapper(ITimeDistancesWrapper timeDistancesWrapper)
    {
        if (sortFields != null)
        {
            for (SortField sortField : sortFields)
            {
                SortComparatorSource sortComparatorSource = sortField.getFactory();
                if (sortComparatorSource instanceof TimeDistancesSorterSource)
                {
                    ((TimeDistancesSorterSource) sortComparatorSource).addTimeDistancesWrapper(timeDistancesWrapper);
                }
            }
        }
    }

    public void addSpaceDistancesWrapper(ISpatialDistancesWrapper spatialDistancesWrapper)
    {
        if (sortFields != null)
        {
            for (SortField sortField : sortFields)
            {
                SortComparatorSource sortComparatorSource = sortField.getFactory();
                if (sortComparatorSource instanceof SpatialDistancesSorterSource)
                {
                    ((SpatialDistancesSorterSource) sortComparatorSource).addSpaceDistancesWrapper(spatialDistancesWrapper);
                }
            }
        }
    }

    public void cleanUp()
    {
        if (sortFields != null)
        {
            for (SortField sortField : sortFields)
            {
                SortComparatorSource sortComparatorSource = sortField.getFactory();
                if (sortComparatorSource instanceof LgteSortComparatorSource)
                {
                    ((LgteSortComparatorSource) sortComparatorSource).cleanUp();
                }
            }
        }
    }

    /**
     * @param doc to score
     * @param score given by sourceComparatorSource
     * @return the score of the first LgteScorer found in sortFields
     */
    public float getScore(int doc, float score)
    {
        if (sortFields != null)
        {
            for (SortField sortField : sortFields)
            {
                SortComparatorSource sortComparatorSource = sortField.getFactory();
                if (sortComparatorSource instanceof LgteScorer)
                {
                    return ((LgteScorer) sortComparatorSource).getScore(doc,score);
                }
            }
        }
        return score;
    }

    /**
     * @param doc to score
     * @param score given by sourceComparatorSource
     * @return the score of the first LgteScorer found in sortFields
     */
    public float getTextScore(int doc, float score)
    {
        if (sortFields != null)
        {
            for (SortField sortField : sortFields)
            {
                SortComparatorSource sortComparatorSource = sortField.getFactory();
                if (sortComparatorSource instanceof LgteScorer)
                {
                    return ((LgteScorer) sortComparatorSource).getTextScore(doc,score);
                }
            }
        }
        return score;
    }

    /**
     * @param doc to score
     * @param score given by sourceComparatorSource
     * @return the score of the first LgteScorer found in sortFields
     */
    public float getSpatialScore(int doc, float score)
    {
        if (sortFields != null)
        {
            for (SortField sortField : sortFields)
            {
                SortComparatorSource sortComparatorSource = sortField.getFactory();
                if (sortComparatorSource instanceof LgteScorer)
                {
                    return ((LgteScorer) sortComparatorSource).getSpatialScore(doc,score);
                }
            }
        }
        return score;
    }

    /**
     * @param doc to score
     * @param score given by sourceComparatorSource
     * @return the score of the first LgteScorer found in sortFields
     */
    public float getTimeScore(int doc, float score)
    {
        if (sortFields != null)
        {
            for (SortField sortField : sortFields)
            {
                SortComparatorSource sortComparatorSource = sortField.getFactory();
                if (sortComparatorSource instanceof LgteScorer)
                {
                    return ((LgteScorer) sortComparatorSource).getTimeScore(doc,score);
                }
            }
        }
        return score;
    }
}
