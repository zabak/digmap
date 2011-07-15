package pt.utl.ist.lucene.filter.distancebuilders;

import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;
import pt.utl.ist.lucene.filter.SerialChainFilterBuilder;

import java.util.Map;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class TimeDistanceBuilder implements ITimeDistancesWrapper
{
   public TimeDistanceBuilderFilter timeDistanceBuilderFilter;

    public TimeDistanceBuilder(long time, String fld)
    {
        timeDistanceBuilderFilter = new TimeDistanceBuilderFilter(fld,time);
    }

    public void build(SerialChainFilterBuilder builder)
    {
        builder.andFilter(timeDistanceBuilderFilter);
    }

    public Map<Integer, Long> getTimeDistances()
    {
        return timeDistanceBuilderFilter.getTimeDistances();
    }

    public Long getTimeDistance(int doc)
    {
        return timeDistanceBuilderFilter.getTimeDistance(doc);
    }
}
