package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import pt.utl.ist.lucene.utils.DataCacher;
import pt.utl.ist.lucene.utils.IDataCacher;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 17/Jan/2010
 * @time 19:33:40
 * @email machadofisher@gmail.com
 */
public abstract class ProbabilisticIndexReader extends IndexReader implements IDataCacher
{


    protected ProbabilisticIndexReader(Directory directory) {
        super(directory);
    }

    ProbabilisticIndexReader(Directory directory, SegmentInfos segmentInfos, boolean closeDirectory) {
        super(directory, segmentInfos, closeDirectory);
    }

    public abstract int getCollectionTokenNumber(String field) throws IOException;
    public abstract double getAvgLenTokenNumber(String field) throws IOException;
    public abstract int getCollectionSize() throws IOException;
    public abstract int collFreq(Term t) throws IOException;
    public abstract int numDocs(String field);
    public abstract int maxDoc(String field);


}
