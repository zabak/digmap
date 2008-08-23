package pt.utl.ist.lucene.filter.distancebuilders;

import com.pjaol.lucene.search.ISerialChainFilter;
import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.apache.solr.util.NumberUtils;

import java.util.Map;
import java.util.BitSet;
import java.util.HashMap;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.filter
 */
public class GetAllDocumentsFilter extends Filter
{
    public GetAllDocumentsFilter()
    {

    }
    
    public BitSet bits(IndexReader reader) throws IOException
    {
        int size = reader.maxDoc();
        BitSet result = new BitSet(size);
        result.set(0,size,true);
        return result;
    }
}
