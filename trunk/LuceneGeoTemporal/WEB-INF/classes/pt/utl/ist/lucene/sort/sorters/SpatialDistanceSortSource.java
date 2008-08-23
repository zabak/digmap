package pt.utl.ist.lucene.sort.sorters;

import org.apache.lucene.search.*;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;

import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import pt.utl.ist.lucene.sort.SpatialDistancesSorterSource;

public class SpatialDistanceSortSource implements SpatialDistancesSorterSource
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ISpatialDistancesWrapper iSpatialDistancesWrapper;
    private SpatialDistanceSortSource.DistanceScoreDocLookupComparator dsdlc;


    public void cleanUp()
    {
        iSpatialDistancesWrapper = null;

        if (dsdlc != null)
            dsdlc.cleanUp();
    }

    public ScoreDocComparator newComparator(IndexReader reader, String field) throws IOException
    {
        dsdlc = new SpatialDistanceSortSource.DistanceScoreDocLookupComparator(reader, iSpatialDistancesWrapper);
        return dsdlc;
    }

    public void addSpaceDistancesWrapper(ISpatialDistancesWrapper iSpatialDistances)
    {
        iSpatialDistancesWrapper = iSpatialDistances;
        if (dsdlc != null)
        {
            dsdlc.setISpaceDistancesWrapper(iSpatialDistances);
        }
    }

    public ISpatialDistancesWrapper getSpaceDistancesWrapper()
    {
        return iSpatialDistancesWrapper;
    }


    private class DistanceScoreDocLookupComparator implements ScoreDocComparator
    {

        ISpatialDistancesWrapper iSpatialDistancesWrapper;

        public DistanceScoreDocLookupComparator(IndexReader reader, ISpatialDistancesWrapper iSpatialDistancesWrapper)
        {
            this.iSpatialDistancesWrapper = iSpatialDistancesWrapper;
            return;
        }


        public void setISpaceDistancesWrapper(ISpatialDistancesWrapper iSpatialDistancesWrapper)
        {
            this.iSpatialDistancesWrapper = iSpatialDistancesWrapper;
        }

        public int compare(ScoreDoc aDoc, ScoreDoc bDoc)
        {
            double a = iSpatialDistancesWrapper.getSpaceDistance(aDoc.doc);
            double b = iSpatialDistancesWrapper.getSpaceDistance(bDoc.doc);
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
            return iSpatialDistancesWrapper.getSpaceDistance(iDoc.doc);
        }

        public void cleanUp()
        {
            iSpatialDistancesWrapper = null;
        }
    }


}
