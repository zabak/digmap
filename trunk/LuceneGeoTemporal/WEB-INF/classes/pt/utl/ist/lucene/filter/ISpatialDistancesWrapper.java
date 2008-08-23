package pt.utl.ist.lucene.filter;

import java.util.Map;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.filter
 */
public interface ISpatialDistancesWrapper
{
    public Map<Integer,Double> getSpaceDistances();
    public Double getSpaceDistance(int doc);
}
