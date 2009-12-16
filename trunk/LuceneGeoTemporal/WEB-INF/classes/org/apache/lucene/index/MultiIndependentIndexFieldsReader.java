package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.*;
import java.io.IOException;

import pt.utl.ist.lucene.utils.StringComparator;

/** An IndexReader which reads multiple indexes, appending their content.
 *
 * Jorge Machado
 *
 * todo At this time it is mandatory to add all documentos to all indexes even if the fields are empty
 *
 * @version 1.0
 */
public class MultiIndependentIndexFieldsReader extends IndexReader {
    private IndexReader[] subReaders;
    //  private int[] starts;                           // 1st docno for each segment
    private Hashtable normsCache = new Hashtable();
    private int maxDoc = 0;
    private int numDocs = -1;
    private boolean hasDeletions = false;

    /**
     * <p>Construct a MultiReader aggregating the named set of (sub)readers.
     * Directory locking for delete, undeleteAll, and setNorm operations is
     * left to the subreaders. </p>
     * <p>Note that all subreaders are closed if this Multireader is closed.</p>
     * @param subReaders set of (sub)readers
     * @throws java.io.IOException
     */
    public MultiIndependentIndexFieldsReader(IndexReader[] subReaders) throws IOException {
        super(subReaders.length == 0 ? null : subReaders[0].directory());
        initialize(subReaders);
    }

    /** Construct reading the named set of readers. */
    MultiIndependentIndexFieldsReader(Directory directory, SegmentInfos sis, boolean closeDirectory, IndexReader[] subReaders)
            throws IOException {
        super(directory, sis, closeDirectory);
        initialize(subReaders);
    }

    private void initialize(IndexReader[] subReaders) throws IOException{
        this.subReaders = subReaders;
        maxDoc = subReaders[0].maxDoc();
//    starts = new int[subReaders.length + 1];    // build starts array
        for (int i = 0; i < subReaders.length; i++) {
//      starts[i] = maxDoc;
//            maxDoc += subReaders[i].maxDoc();      // compute maxDocs

            if (subReaders[i].hasDeletions())
                hasDeletions = true;
        }
//    starts[subReaders.length] = maxDoc;
    }


    /** Return an array of term frequency vectors for the specified document.
     *  The array contains a vector for each vectorized field in the document.
     *  Each vector vector contains term numbers and frequencies for all terms
     *  in a given vectorized field.
     *  If no such fields existed, the method returns null.
     *
     * All indexes have all documents
     *
     * @author Jorge Machado
     * todo Need optimization
     * todo need debug
     */
    public TermFreqVector[] getTermFreqVectors(int n) throws IOException
    {
        //Creates A map of fields terms and frequencies because independent subreaders could have the same documents and the same fields
        //<field, <term, freq>>
        HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>(0);

        for(IndexReader subReader: subReaders)
        {
            TermFreqVector[] vectors = subReader.getTermFreqVectors(n);
            for(TermFreqVector vector: vectors)
            {
                HashMap<String, Integer> field = map.get(vector.getField());
                if(field == null)
                {
                    field = new HashMap<String, Integer>();
                    map.put(vector.getField(),field);
                }
                for(int i = 0; i < vector.getTerms().length;i++)
                {
                    String term = vector.getTerms()[i];
                    Integer freq = field.get(term);
                    if(freq == null)
                        field.put(term,vector.getTermFrequencies()[i]);
                    else
                        field.put(term,freq + vector.getTermFrequencies()[i]);
                }
            }
        }
        TermFreqVector[] vectors = new TermFreqVector[map.size()];
        int vectorPos = 0;
        for(Map.Entry<String, HashMap<String, Integer>> field: map.entrySet())
        {

            int totalTerms = field.getValue().size();
            String[] terms = new String[totalTerms];
            int cont = 0;
            for(String term: field.getValue().keySet())
            {
                terms[cont] = term;
                cont++;
            }
            int[] freqs = new int[totalTerms];
            Arrays.sort(terms, StringComparator.ASC);
            for(int i = 0; i < terms.length;i++)
            {
                freqs[i] = field.getValue().get(terms[i]);
            }
            TermFreqVector vector = new SegmentTermVector(field.getKey(),terms,freqs);
            vectors[vectorPos] = vector;
            vectorPos++;
        }
        return vectors;
    }

    /** Return term frequency vector for the specified document.
     *
     *  The vector contains term numbers and frequencies for all terms
     *  in a given vectorized field.
     *
     * All indexes have all documents
     *
     * @author Jorge Machado
     * todo Need optimization
     * todo need debug
     */
    public TermFreqVector getTermFreqVector(int n, String field)
            throws IOException {
        //Creates A map of fields terms and frequencies because independent subreaders could have the same documents and the same fields
        //<field, <term, freq>>
        HashMap<String, Integer> map = new  HashMap<String, Integer>(0);

        for(IndexReader subReader: subReaders)
        {
            TermFreqVector vector = subReader.getTermFreqVector(n,field);

            for(int i = 0; i < vector.getTerms().length;i++)
            {
                String term = vector.getTerms()[i];
                Integer freq = map.get(term);
                if(freq == null)
                    map.put(term,vector.getTermFrequencies()[i]);
                else
                    map.put(term,freq + vector.getTermFrequencies()[i]);
            }

        }
        if(map.size() == 0)
            return null;
        int totalTerms = map.size();
        String[] terms = new String[totalTerms];
        int cont = 0;
        for(String term: map.keySet())
        {
            terms[cont] = term;
            cont++;
        }
        int[] freqs = new int[totalTerms];
        Arrays.sort(terms, StringComparator.ASC);
        for(int i = 0; i < terms.length;i++)
        {
            freqs[i] = map.get(terms[i]);
        }
        return new SegmentTermVector(field,terms,freqs);
    }

    /**
     * Returns the max docNum in all subReaders
     * todo need modification if the docs IDs are not equal in all indexes
     * @return
     */
    public synchronized int numDocs()
    {
        if (numDocs == -1) {        // check cache
            int max = 0;            // cache miss--recompute
            for (int i = 0; i < subReaders.length; i++)
            {
                int subNum = subReaders[i].numDocs();      // sum from readers
                max = subNum > max ? subNum : max;
            }
            numDocs = max;
        }
        return numDocs;
    }

    public int maxDoc()
    {
        return maxDoc;
    }

    /**
     * todo Need Check
     * todo need modification if the docs IDs are not equal in all indexes
     * @param n
     * @return
     * @throws IOException
     */
    public Document document(int n) throws IOException
    {
        Document d = null;
        for (int i = 0; i < subReaders.length; i++)
        {
            Document dS = subReaders[i].document(n);
            if(d == null) d = dS;
            else
            {
                Enumeration fields = dS.fields();
                while(fields.hasMoreElements())
                {
                    d.add((Field) fields.nextElement());
                }
            }
        }
        return d;
    }

    /**
     * Not implemented
     * @param n
     * @return
     */
    public boolean isDeleted(int n)
    {
        throw new ReadOnlyIndex("MultiIndependentIndexFieldsReader is a ReadOnly Index");
    }

    /**
     * todo Need Check
     * todo need modification if the docs IDs are not equal in all indexes
     */

    public int getFieldLength(int docNumber, String field)
            throws IOException {
        int len = 0;
        for (IndexReader subReader : subReaders) len += subReader.getFieldLength(docNumber, field);
        return len;
    }

    /**
     * todo Need Check
     * todo need modification if the docs IDs are not equal in all indexes
     */

    public int getDocLength(int docNumber) throws IOException {
        int len = 0;
        for (IndexReader subReader : subReaders) len += subReader.getDocLength(docNumber);
        return len;
    }

    public boolean hasDeletions() { return hasDeletions; }

    protected void doDelete(int n) throws IOException {
        throw new ReadOnlyIndex("MultiIndependentIndexFieldsReader is a ReadOnly Index");
    }

    protected void doUndeleteAll() throws IOException {
        throw new ReadOnlyIndex("MultiIndependentIndexFieldsReader is a ReadOnly Index");
    }



    public synchronized byte[] norms(String field) throws IOException {
        return null;
//        throw new NotImplemented("MultiIndependentIndexFieldsReader does not implements this method yet");
    }

    public synchronized void norms(String field, byte[] result, int offset)
            throws IOException {
        throw new NotImplemented("MultiIndependentIndexFieldsReader does not implements this method yet");
    }

    protected void doSetNorm(int n, String field, byte value)
            throws IOException {
        throw new NotImplemented("MultiIndependentIndexFieldsReader does not implements this method yet");
    }

    public TermEnum terms() throws IOException {
        return new MultiTermEnum(subReaders, new int[subReaders.length], null);
    }

    public TermEnum terms(Term term) throws IOException {
        return new MultiTermEnum(subReaders, new int[subReaders.length], term);
    }

    public int docFreq(Term t) throws IOException {
        int total = 0;          // sum freqs in segments
        for (int i = 0; i < subReaders.length; i++)
            total += subReaders[i].docFreq(t);
        return total;
    }

    public TermDocs termDocs() throws IOException {
        return new MultiTermDocs(subReaders, new int[subReaders.length]);
    }

    public TermPositions termPositions() throws IOException {
        return new MultiTermPositions(subReaders, new int[subReaders.length]);
    }

    protected void doCommit() throws IOException {
        for (int i = 0; i < subReaders.length; i++)
            subReaders[i].commit();
    }

    protected synchronized void doClose() throws IOException {
        for (int i = 0; i < subReaders.length; i++)
            subReaders[i].close();
    }

    /**
     * @see org.apache.lucene.index.IndexReader#getFieldNames()
     */
    public Collection getFieldNames() throws IOException {
        // maintain a unique set of field names
        Set fieldSet = new HashSet();
        for (int i = 0; i < subReaders.length; i++) {
            IndexReader reader = subReaders[i];
            Collection names = reader.getFieldNames();
            // iterate through the field names and add them to the set
            for (Iterator iterator = names.iterator(); iterator.hasNext();) {
                String s = (String) iterator.next();
                if(!fieldSet.contains(s))
                    fieldSet.add(s);
            }
        }
        return fieldSet;
    }

    /**
     * @see org.apache.lucene.index.IndexReader#getFieldNames(boolean)
     */
    public Collection getFieldNames(boolean indexed) throws IOException {
        // maintain a unique set of field names
        Set fieldSet = new HashSet();
        for (int i = 0; i < subReaders.length; i++) {
            IndexReader reader = subReaders[i];
            Collection<String> names = reader.getFieldNames(indexed);
            for(String field: names)
                if(!fieldSet.contains(field))
                    fieldSet.add(field);
        }
        return fieldSet;
    }

    public Collection getIndexedFieldNames(boolean storedTermVector) {
        // maintain a unique set of field names
        Set fieldSet = new HashSet();
        for (int i = 0; i < subReaders.length; i++) {
            IndexReader reader = subReaders[i];
            Collection<String> names = reader.getIndexedFieldNames(storedTermVector);
            for(String field: names)
                if(!fieldSet.contains(field))
                    fieldSet.add(field);
        }
        return fieldSet;
    }

}


