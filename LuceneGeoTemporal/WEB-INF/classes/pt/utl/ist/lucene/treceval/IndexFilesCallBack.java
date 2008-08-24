package pt.utl.ist.lucene.treceval;

import java.io.IOException;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:37:57
 * @see pt.utl.ist.lucene.treceval
 */
public interface IndexFilesCallBack
{
    public void indexDoc(String id, Map<String,String> indexFields) throws IOException;
}
