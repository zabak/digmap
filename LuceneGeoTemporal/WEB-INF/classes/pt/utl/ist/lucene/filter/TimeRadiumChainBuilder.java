package pt.utl.ist.lucene.filter;

import com.pjaol.search.geo.utils.BoundaryBoxFilter;
import org.apache.solr.util.NumberUtils;
import pt.utl.ist.lucene.filter.distancebuilders.TimeDistanceBuilderFilter;
import pt.utl.ist.lucene.filter.distancebuilders.GetAllDocumentsFilter;
import pt.utl.ist.lucene.utils.Dates;

import java.util.GregorianCalendar;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class TimeRadiumChainBuilder implements ITimeDistancesWrapper
{
    public BoundaryBoxFilter timeFilter;
    public TimeDistanceBuilderFilter timeDistanceBuilderFilter;

    public TimeRadiumChainBuilder(String time, long radiumMiliseconds, String fld)
    {
        GregorianCalendar c = Dates.getGregorianCalendar(time);
        init(c.getTimeInMillis(),radiumMiliseconds,fld);
    }

    public TimeRadiumChainBuilder(long time, long radiumMiliseconds, String fld)
    {
        init(time,radiumMiliseconds,fld);
    }

    private void init(long time, long radiumMiliseconds, String fld)
    {
        if(radiumMiliseconds >= 0)
        {
            long lowerTime = time - radiumMiliseconds;
            long topTime = time + radiumMiliseconds;

            String startTime = NumberUtils.long2sortableStr(lowerTime);
            String endTime = NumberUtils.long2sortableStr(topTime);

            timeFilter = new BoundaryBoxFilter(fld, startTime, endTime, true, true);
        }
        else
            timeFilter = null;
        timeDistanceBuilderFilter = new TimeDistanceBuilderFilter(fld,time,radiumMiliseconds);//this filter is only to fill distances
    }

    public void build(SerialChainFilterBuilder builder)
    {
        if(timeFilter != null)
            builder.andFilter(timeFilter);
        else
            builder.andFilter(new GetAllDocumentsFilter());
        builder.serialAndFilter(timeDistanceBuilderFilter);
    }

    public BoundaryBoxFilter getTimeFilter()
    {
        return timeFilter;
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
