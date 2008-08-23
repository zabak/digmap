package pt.utl.ist.lucene.treceval;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 4:30:51
 * @see pt.utl.ist.lucene.treceval
 */
public interface ITopicsProcessor
{
    public void processPath(String path, String confId, ISearchCallBack iSearchCallBack, String run,String collection, String outputDir);
}
