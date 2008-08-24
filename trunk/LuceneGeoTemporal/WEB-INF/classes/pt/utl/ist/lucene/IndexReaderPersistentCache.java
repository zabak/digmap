package pt.utl.ist.lucene;

import org.apache.lucene.index.IndexReader;

import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 19/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class IndexReaderPersistentCache
{
    private static HashMap<IndexReader,HashMap<Object,Object>> cache = new HashMap<IndexReader,HashMap<Object,Object>>();
   

    public static void clean(IndexReader reader)
    {
        cache.remove(reader);
    }

    public static void put(Object key, Object value, IndexReader reader)
    {
        HashMap<Object,Object> readerCache = cache.get(reader);
        if(readerCache == null)
        {
            readerCache = new  HashMap<Object,Object>();
            cache.put(reader,readerCache);
        }
        readerCache.put(key,value);
    }

    public static Object get(IndexReader reader, Object key)
    {
        HashMap<Object,Object> readerCache = cache.get(reader);
        if(readerCache != null)
            return readerCache.get(key);
        return null;
    }

}
