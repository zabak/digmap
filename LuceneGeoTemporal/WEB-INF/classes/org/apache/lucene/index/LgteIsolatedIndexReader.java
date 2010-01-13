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


    Readers readers = new Readers();


    private class Readers
    {
        private Map<String, IndexReader> readers = new HashMap<String,IndexReader>();
        IndexReader[] readersArray;

        private Readers()
        {

        }

        public IndexReader[] getReadersArray() {
            return readersArray;
        }

        public Collection<IndexReader> getReaders() {
            return readers.values();
        }

        public Map<String, IndexReader> getReadersMap() {
            return readers;
        }

        public void setReaders(Map<String, IndexReader> readers)
        {
            this.readers = readers;
            int cont = 0;
            readersArray = new IndexReader[readers.size()];
            for(IndexReader reader:readers.values())
            {
                readersArray[cont] = reader;
                cont++;
            }
        }

        public IndexReader getReader(String field)
        {
            IndexReader reader = readers.get(field);

            if(reader == null)
            {
                for(Map.Entry<String,IndexReader> entry: readers.entrySet())
                {
                    if(entry.getKey().matches("regexpr(.*)"))
                    {
                        String regExpr = entry.getKey().substring("regexpr(".length(),entry.getKey().length() -1);
                        if(field.matches(regExpr))
                        {
                            reader = entry.getValue();
                            readers.put(field,reader);
                            break;
                        }
                    }
                }
                if(reader == null)
                    reader = readers.get("*");
            }
            return reader;
        }

        public Collection<String> getFields()
        {
            return readers.keySet();
        }
    }

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
     * @param paths Pair<FieldName, Path> - The field name can be a regular expression
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
        this.readers.setReaders(readers);

    }

    private void init(String path, List<String> fields, Model model) throws IOException
    {
        Map<String , IndexReader> readers = new HashMap<String,IndexReader>(fields.size());
        for(String field: fields)
        {
            if(model.isProbabilistcModel())
            {
                IndexReader reader = new LanguageModelIndexReader(IndexReader.open(path + File.separator + field + "_field"));
                readers.put(field,reader);
            }
            else if(model == Model.VectorSpaceModel)
            {
                IndexReader reader = IndexReader.open(path + File.separator + field + "_field");
                readers.put(field,reader);
            }
            else
            {
                System.err.println("Unknown retrieval model: " + model);
                throw new IllegalArgumentException();
            }
        }
        this.readers.setReaders(readers);
    }
    private void init(Map<String,String> paths, Model model) throws IOException
    {
        Map<String , IndexReader> readers = new HashMap<String,IndexReader>(paths.size());
        for(Map.Entry<String,String> path: paths.entrySet())
        {
            if(model.isProbabilistcModel())
            {
                IndexReader reader = new LanguageModelIndexReader(IndexReader.open(path.getValue()));
                readers.put(path.getKey(),reader);
            }
            else if(model == Model.VectorSpaceModel)
            {
                IndexReader reader = IndexReader.open(path.getValue());
                readers.put(path.getKey(),reader);
            }
            else
            {
                System.err.println("Unknown retrieval model: " + model);
                throw new IllegalArgumentException();
            }
        }
        this.readers.setReaders(readers);
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

        for(IndexReader subReader: readers.getReaders())
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

        for(IndexReader subReader: readers.getReaders())
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
        return readers.getReader(field).getFieldLength(docNumber,field);
    }

    public int getDocLength(int docNumber) throws IOException {
        int len = 0;
        for (IndexReader subReader : readers.getReaders()) len += subReader.getDocLength(docNumber);
        return len;
    }


    int numDocs = -1;
    public int numDocs() {
        if (numDocs == -1) {        // check cache
            int max = 0;            // cache miss--recompute
            for (IndexReader reader: readers.getReaders())
            {
                int subNum = reader.numDocs();      // sum from readers
                max = subNum > max ? subNum : max;
            }
            numDocs = max;
        }
        return numDocs;
    }

    public int maxDoc() {
        return readers.getReaders().iterator().next().maxDoc();
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
        for (IndexReader reader : readers.getReaders())
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
        return readers.getReader(field).norms(field);
    }

    public void norms(String field, byte[] bytes, int offset) throws IOException {
        readers.getReader(field).norms(field,bytes,offset);
    }

    protected void doSetNorm(int doc, String field, byte value) throws IOException {
        readers.getReader(field).doSetNorm(doc,field,value);
    }

    public TermEnum terms() throws IOException
    {
        return new MultiTermEnum(readers.getReadersArray(), new int[readers.getReadersArray().length], null);
    }

    public TermEnum terms(Term t) throws IOException {
        return readers.getReader(t.field()).terms(t);
    }

    public int docFreq(Term t) throws IOException {
        return readers.getReader(t.field()).docFreq(t);
    }

    public TermDocs termDocs() throws IOException {
        return new MultiTermDocs(readers);
    }

    public TermPositions termPositions() throws IOException {
        return new MultiTermPositions(readers);
    }





    protected void doCommit() throws IOException {
        for (IndexReader reader: readers.getReaders())
        {
            reader.commit();
        }
    }

    protected synchronized void doClose() throws IOException {
        for (IndexReader reader: readers.getReaders())
        {
            reader.close();
        }
    }

    public Collection getFieldNames() throws IOException {
        return readers.getFields();
    }

    /**
     * @see org.apache.lucene.index.IndexReader#getFieldNames(boolean)
     */
    public Collection getFieldNames(boolean indexed) throws IOException {
        // maintain a unique set of field names
        Set fieldSet = new HashSet();
        for (IndexReader reader: readers.getReaders()) {
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
        for (IndexReader reader: readers.getReaders()) {
            Collection<String> names = reader.getIndexedFieldNames(storedTermVector);
            for(String field: names)
                if(!fieldSet.contains(field))
                    fieldSet.add(field);
        }
        return fieldSet;
    }


    public TermDocs termDocs(Term term) throws IOException {
        return readers.getReader(term.field()).termDocs(term);
    }

    public TermPositions termPositions(Term term) throws IOException {
        return readers.getReader(term.field()).termPositions(term);
    }

    public void setNorm(int doc, String field, float value) throws IOException {
        throw new NotImplemented("MultiIndependentIndexFieldsReader does not implements this method yet");
    }

    class MultiTermDocs implements TermDocs {
        Readers readers;
        protected Term term;

        protected TermDocs current;              // == readerTermDocs[pointer]

        public MultiTermDocs(Readers r) {
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
            current = readers.getReader(term.field()).termDocs();
            current.seek(term);
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
                if (end == 0) {
                    // none left in segment
                    if(current != null)
                        current.close();
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
            if(current != null)
                current.close();
        }
    }

    class MultiTermPositions extends MultiTermDocs implements TermPositions {
        public MultiTermPositions(Readers r) {
            super(r);
        }

        protected TermDocs termDocs(IndexReader reader) throws IOException {
            return reader.termPositions();
        }

        public int nextPosition() throws IOException {
            return ((TermPositions)current).nextPosition();
        }

    }
}