package pt.utl.ist.lucene.sort.sorters;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.ScoreDocComparator;
import org.apache.lucene.search.SortField;
import org.apache.lucene.index.IndexReader;
import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;
import pt.utl.ist.lucene.sort.TimeDistancesSorterSource;
import pt.utl.ist.lucene.level1query.QueryParams;

import java.io.IOException;

public class TimeDistanceSortSource implements TimeDistancesSorterSource
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ITimeDistancesWrapper iTimeDistancesWrapper;
    private TimeDistanceSortSource.DistanceScoreDocLookupComparator dsdlc;


    public void cleanUp()
    {
        iTimeDistancesWrapper = null;

        if (dsdlc != null)
            dsdlc.cleanUp();
    }

    public void addQueryParams(QueryParams queryParams)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ScoreDocComparator newComparator(IndexReader reader, String field) throws IOException
    {
        dsdlc = new TimeDistanceSortSource.DistanceScoreDocLookupComparator(reader, iTimeDistancesWrapper);
        return dsdlc;
    }

    public void addTimeDistancesWrapper(ITimeDistancesWrapper iTimeDistances)
    {
        iTimeDistancesWrapper = iTimeDistances;
        if (dsdlc != null)
        {
            dsdlc.setITimeDistancesWrapper(iTimeDistancesWrapper);
        }
    }

    public ITimeDistancesWrapper getTimeDistancesWrapper()
    {
        return iTimeDistancesWrapper;
    }

    private class DistanceScoreDocLookupComparator implements ScoreDocComparator
    {

        ITimeDistancesWrapper iTimeDistancesWrapper;

        public DistanceScoreDocLookupComparator(IndexReader reader, ITimeDistancesWrapper iTimeDistancesWrapper)
        {
            this.iTimeDistancesWrapper = iTimeDistancesWrapper;
        }


        public void setITimeDistancesWrapper(ITimeDistancesWrapper iTimeDistancesWrapper)
        {
            this.iTimeDistancesWrapper = iTimeDistancesWrapper;
        }

        public int compare(ScoreDoc aDoc, ScoreDoc bDoc)
        {

//			if (this.distances == null) {
//					distances = spaceDistanceFilter.getDistances();
//			}
            long a = iTimeDistancesWrapper.getTimeDistance(aDoc.doc);
            long b = iTimeDistancesWrapper.getTimeDistance(bDoc.doc);
            if (a > b) return 1;
            if (a < b) return -1;

            return 0;
        }

        public int sortType()
        {
            return SortField.FLOAT;
        }

        public Comparable sortValue(ScoreDoc iDoc)
        {
            return iTimeDistancesWrapper.getTimeDistance(iDoc.doc);
        }

        public void cleanUp()
        {
            iTimeDistancesWrapper = null;
        }
    }


}
