package pt.utl.ist.lucene.filter.distancebuilders;

import com.pjaol.lucene.search.ISerialChainFilter;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.solr.util.NumberUtils;
import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.filter
 */
public class TimeDistanceBuilderFilter extends ISerialChainFilter implements ITimeDistancesWrapper
{

    private static final Logger logger = Logger.getLogger(TimeDistanceBuilderFilter.class);

    String timeField;
    long pointTime;
    long distance;
    Map<Integer, Long> timeDistances;


    public TimeDistanceBuilderFilter(String timeField, long pointTime, long distance)
    {
        this.timeField = timeField;
        this.pointTime = pointTime;
        this.distance = distance;
    }
    public TimeDistanceBuilderFilter(String timeField, long pointTime)
    {
        this.timeField = timeField;
        this.pointTime = pointTime;
        this.distance = -1;
    }

    /**
     * This method is for now a simple distance calculator since
     * that indexText documents in bitset have been already checked and
     * they are in time requested scope
     * <p/>
     * Calc time timeDistances in marked Bits
     *
     * @param reader to read Indexes
     * @param bits already marked
     * @return marked bitSet
     * @throws Exception
     */
    public BitSet bits(IndexReader reader, BitSet bits) throws Exception
    {

        int size = bits.cardinality();
        BitSet result = new BitSet(size);

        /* store calculated timeDistances for reuse by other components */
        timeDistances = new HashMap<Integer, Long>(size);

        long start = System.currentTimeMillis();

        String[] timeIndex = FieldCache.DEFAULT.getStrings(reader, timeField);

        /* loop over indexText set bits (hits from the boundary box filter) */
        int i = bits.nextSetBit(0);
        while (i >= 0)
        {
            long x;
            String time = timeIndex[i];
            if (time != null)  //No time no distance -> no bit set
            {
                time = NumberUtils.SortableStr2long(time);

                if (time != null)
                {
                    x = Long.parseLong(time);
                    long d = Math.abs(pointTime - x);
                    if (distance < 0 || d < distance) //distance < 0 for queries where distance is not defined
                    {
                        result.set(i);
                        timeDistances.put(i, d);
                    }

//                timeDistances.put(i, d);
                }
            }
            i = bits.nextSetBit(i + 1);
        }

        long end = System.currentTimeMillis();
        logger.info("Time taken : " + (end - start) +
                ", results : " + timeDistances.size() +
                ", incoming size: " + size);


        return result;
    }


    public BitSet bits(IndexReader reader) throws IOException
    {
        /* Create a BitSet to store the result */
        int maxdocs = reader.numDocs();
        BitSet bits = new BitSet(maxdocs);

//        int size = bits.cardinality();

        /* store calculated timeDistances for reuse by other components */
        timeDistances = new HashMap<Integer, Long>(maxdocs);

        long start = System.currentTimeMillis();

        String[] timeIndex = FieldCache.DEFAULT.getStrings(reader, timeField);

        /* loop over indexText set bits (hits from the boundary box filter) */

        for (int i = 0; i < maxdocs; i++)
        {
            long x;
            String time = timeIndex[i];

            if (time != null)  //No time no distance -> no bit set
            {
                time = NumberUtils.SortableStr2long(time);
                x = Long.parseLong(time);
                long d = Math.abs(pointTime - x);
                if (distance < 0 ||  d < distance)
                {
                    bits.set(i);
                }
                timeDistances.put(i, d);
            }
        }

        long end = System.currentTimeMillis();
        logger.info("BoundaryBox Time Taken: "+ (end - start));




        return bits;
    }


    public long getPointTime()
    {
        return pointTime;
    }

    public long getDistance()
    {
        return distance;
    }

    public Map<Integer, Long> getTimeDistances()
    {
        return timeDistances;
    }

    public Long getTimeDistance(int doc)
    {
        if(timeDistances == null)
            return null;
        return timeDistances.get(doc);
    }

    
}
