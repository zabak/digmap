package pt.utl.ist.lucene.filter;

import org.apache.lucene.search.Filter;

import java.util.List;
import java.util.ArrayList;

import com.pjaol.lucene.search.SerialChainFilter;

/**
 * Helps building a Serial Chain Filter Array
 *
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene.level1query.parser
 */
public class SerialChainFilterBuilder
{
    List<Filter> filters = new ArrayList<Filter>();
    List<Integer> serialOperations = new ArrayList<Integer>();


    public SerialChainFilterBuilder()
    {
    }

    public void andFilter(Filter filter)
    {
        addFilter(filter, SerialChainFilter.AND);
    }

    public void orFilter(Filter filter)
    {
        addFilter(filter, SerialChainFilter.OR);
    }

    public void serialAndFilter(Filter filter)
    {
        addFilter(filter, SerialChainFilter.SERIALAND);
    }

    private void addFilter(Filter filter, int op)
    {
        filters.add(filter);
        serialOperations.add(op);
    }

    public Filter getFilter()
    {

        Filter[] filtersArray = new Filter[filters.size()];
        int[] serialOps = new int[serialOperations.size()];
        int i = 0;
        for (Filter f : filters)
        {
            filtersArray[i] = f;
            serialOps[i] = serialOperations.get(i);
            i++;
        }
        return new SerialChainFilter(filtersArray, serialOps);
    }
}
