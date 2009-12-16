package org.apache.lucene.index;

import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.utils.StringComparator;

import java.util.*;
import java.io.IOException;
import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 14:19:07
 * @email machadofisher@gmail.com
 */
public class LgteIsolatedIndexReader extends IndexReader
{
    Map<String, IndexReader> readers = new HashMap<String,IndexReader>();
    IndexReader[] readersArray;

    /**
     * Constructor used if IndexReader is not owner of its directory.
     * This is used for IndexReaders that are used within other IndexReaders that take care or locking directories.
     *
     * @param path of index
     * @param model model
     * @throws java.io.IOException on open
     */
    public LgteIsolatedIndexReader(String path, Model model) throws IOException {
        super(null);
        List<String> fields = new ArrayList<String>();
        File f = new File(path);
        for(File d: f.listFiles())
        {
            if(d.isDirectory() && d.getName().endsWith("_field"))
            {
                fields.add(d.getName().substring(0,d.getName().indexOf("_field")));
            }
        }
        init(path,fields,model);
    }

    /**
     * Constructor used if IndexReader is not owner of its directory.
     * This is used for IndexReaders that are used within other IndexReaders that take care or locking directories.
     *
     * @param paths Pairs FieldName, Path
     * @param model model
     * @throws java.io.IOException on open
     */
    public LgteIsolatedIndexReader(Map<String,String> paths, Model model) throws IOException {
        super(null);
        init(paths,model);
    }

    public LgteIsolatedIndexReader(Map<String, IndexReader> readers)
    {
        super(null);
        this.readers = readers;
        int cont = 0;
        readersArray = new IndexReader[readers.size()];
        for(IndexReader reader:readers.values())
        {
            readersArray[cont] = reader;
        }
    }

    private void init(String path, List<String> fields, Model model) throws IOException
    {
        readersArray = new IndexReader[fields.size()];
        int cont = 0;
        for(String field: fields)
        {
            if(model.isProbabilistcModel())
            {
                IndexReader reader = new LanguageModelIndexReader(IndexReader.open(path + File.separator + field + "_field"));
                readers.put(field,reader);
                readersArray[cont] = reader;
            }
            else if(model == Model.VectorSpaceModel)
            {
                IndexReader reader = IndexReader.open(path + File.separator + field + "_field");
                readers.put(field,reader);
                readersArray[cont] = reader;
            }
            else
            {
                System.err.println("Unknown retrieval model: " + model);
                throw new IllegalArgumentException();
            }
            cont++;
        }
    }
    private void init(Map<String,String> paths, Model model) throws IOException
    {
        readersArray = new IndexReader[paths.size()];
        int cont = 0;
        for(Map.Entry<String,String> path: paths.entrySet())
        {
            if(model.isProbabilistcModel())
            {
                IndexReader reader = new LanguageModelIndexReader(IndexReader.open(path.getValue()));
                readers.put(path.getKey(),reader);
                readersArray[cont] = reader;
            }
            else if(model == Model.VectorSpaceModel)
            {
                IndexReader reader = IndexReader.open(path.getValue());
                readers.put(path.getKey(),reader);
                readersArray[cont] = reader;
            }
            else
            {
                System.err.println("Unknown retrieval model: " + model);
                throw new IllegalArgumentException();
            }
            cont++;
        }
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

        for(IndexReader subReader: readers.values())
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

        for(IndexReader subReader: readers.values())
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

    public int getFieldLength(int docNumber, String field) throws IOException
    {
        return readers.get(field).getFieldLength(docNumber,field);
    }

    public int getDocLength(int docNumber) throws IOException {
        int len = 0;
        for (IndexReader subReader : readers.values()) len += subReader.getDocLength(docNumber);
        return len;
    }


    int numDocs = -1;
    public int numDocs() {
        if (numDocs == -1) {        // check cache
            int max = 0;            // cache miss--recompute
            for (IndexReader reader: readers.values())
            {
                int subNum = reader.numDocs();      // sum from readers
                max = subNum > max ? subNum : max;
            }
            numDocs = max;
        }
        return numDocs;
    }

    public int maxDoc() {
        return readers.values().iterator().next().maxDoc();
    }

    /**
     * todo Need Check
     * todo need modification if the docs IDs are not equal in all indexes
     * @param n
     * @return
     * @throws java.io.IOException
     */
    public Document document(int n) throws IOException
    {
        Document d = null;
        for (IndexReader reader : readers.values())
        {
            Document dS = reader.document(n);
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

    public boolean hasDeletions() { return false; }

    protected void doDelete(int n) throws IOException {
        throw new ReadOnlyIndex("MultiIndependentIndexFieldsReader is a ReadOnly Index");
    }

    protected void doUndeleteAll() throws IOException {
        throw new ReadOnlyIndex("MultiIndependentIndexFieldsReader is a ReadOnly Index");
    }

    public boolean isDeleted(int n) {
        return false;
    }



    public byte[] norms(String field) throws IOException {
        return readers.get(field).norms(field);
    }

    public void norms(String field, byte[] bytes, int offset) throws IOException {
        readers.get(field).norms(field,bytes,offset);
    }

    protected void doSetNorm(int doc, String field, byte value) throws IOException {
        readers.get(field).doSetNorm(doc,field,value);
    }

    public TermEnum terms() throws IOException
    {
        return new MultiTermEnum(readersArray, new int[readers.size()], null);
    }

    public TermEnum terms(Term t) throws IOException {
        return readers.get(t.field()).terms(t);
    }

    public int docFreq(Term t) throws IOException {
        return readers.get(t.field()).docFreq(t);
    }

    public TermDocs termDocs() throws IOException {
        return new MultiTermDocs(readers);
    }

    public TermPositions termPositions() throws IOException {
        return new MultiTermPositions(readers);
    }





    protected void doCommit() throws IOException {
        for (IndexReader reader: readers.values())
        {
            reader.commit();
        }
    }

    protected synchronized void doClose() throws IOException {
        for (IndexReader reader: readers.values())
        {
            reader.close();
        }
    }

    public Collection getFieldNames() throws IOException {
        return readers.keySet();
    }

    /**
     * @see org.apache.lucene.index.IndexReader#getFieldNames(boolean)
     */
    public Collection getFieldNames(boolean indexed) throws IOException {
        // maintain a unique set of field names
        Set fieldSet = new HashSet();
        for (IndexReader reader: readers.values()) {
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
        for (IndexReader reader: readers.values()) {
            Collection<String> names = reader.getIndexedFieldNames(storedTermVector);
            for(String field: names)
                if(!fieldSet.contains(field))
                    fieldSet.add(field);
        }
        return fieldSet;
    }


    public TermDocs termDocs(Term term) throws IOException {
        return readers.get(term.field()).termDocs(term);
    }

    public TermPositions termPositions(Term term) throws IOException {
        return readers.get(term.field()).termPositions(term);
    }

    public void setNorm(int doc, String field, float value) throws IOException {
        throw new NotImplemented("MultiIndependentIndexFieldsReader does not implements this method yet");
    }

    class MultiTermDocs implements TermDocs {
        Map<String, IndexReader> readers;
        protected Term term;

        protected TermDocs current;              // == readerTermDocs[pointer]

        public MultiTermDocs(Map<String, IndexReader> r) {
            readers = r;

        }

        public int doc() {
            return current.doc();
        }
        public int freq() {
            return current.freq();
        }

        public void seek(Term term) throws IOException
        {
            current = readers.get(term.field()).termDocs();
            this.term = term;
        }

        public void seek(TermEnum termEnum) throws IOException {
            seek(termEnum.term());
        }

        public boolean next() throws IOException {
            if (current != null && current.next()) {
                return true;
            } else
                return false;
        }

        /** Optimized implementation. */
        public int read(final int[] docs, final int[] freqs) throws IOException {
            while (true) {
                while (current == null) {
                        return 0;
                    }
                int end = current.read(docs, freqs);
                if (end == 0) {          // none left in segment
                    current = null;
                } else {            // got some
//                    final int b = base;        // adjust doc numbers
//                    for (int i = 0; i < end; i++)
//                        docs[i] += b;
                    return end;
                }
            }
        }

        /** As yet unoptimized implementation. */
        public boolean skipTo(int target) throws IOException {
            do {
                if (!next())
                    return false;
            } while (target > doc());
            return true;
        }



        public void close() throws IOException {
           current.close();
        }
    }

    class MultiTermPositions extends MultiTermDocs implements TermPositions {
        public MultiTermPositions(Map<String, IndexReader> r) {
            super(r);
        }

        protected TermDocs termDocs(IndexReader reader) throws IOException {
            return (TermDocs)reader.termPositions();
        }

        public int nextPosition() throws IOException {
            return ((TermPositions)current).nextPosition();
        }

    }
}