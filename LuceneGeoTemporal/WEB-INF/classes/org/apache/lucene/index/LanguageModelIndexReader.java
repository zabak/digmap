package org.apache.lucene.index;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.ilps.DataCacher;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.ControlIndexesEnum;

/**  A <code>LanguageModelIndexReader</code> contains another IndexReader, which it
 * uses as its basic source of data, possibly transforming the data along the
 * way or providing additional functionality. The class
 * <code>LanguageModelIndexReader</code> itself simply implements all abstract methods
 * of <code>IndexReader</code> with versions that pass all requests to the
 * contained index reader. 
 * <p>Added functionality: get documents length (cached)
 */
public class LanguageModelIndexReader extends IndexReader {

    /** Base class for LanguageModeling {@link TermDocs} implementations. */
    public static class LanguageModelTermDocs implements TermDocs {
        protected TermDocs in;

        public LanguageModelTermDocs(TermDocs in) {
            this.in = in;
        }

        public void seek(Term term) throws IOException {
            in.seek(term);
        }
        public void seek(TermEnum e) throws IOException {
            in.seek(e);
        }
        public int doc() {
            return in.doc();
        }
        public int freq() {
            return in.freq();
        }
        public boolean next() throws IOException {
            return in.next();
        }
        public int read(int[] docs, int[] freqs) throws IOException {
            return in.read(docs, freqs);
        }
        public boolean skipTo(int i) throws IOException {
            return in.skipTo(i);
        }
        public void close() throws IOException {
            in.close();
        }
    }

    /** Base class for LanguageModeling {@link TermPositions} implementations. */
    public static class LanguageModelTermPositions
            extends LanguageModelTermDocs
            implements TermPositions {

        public LanguageModelTermPositions(TermPositions in) {
            super(in);
        }

        public int nextPosition() throws IOException {
            return ((TermPositions) this.in).nextPosition();
        }

//        //TODO LGTE TERM POSITIONS TEM COISAS NOVAS VER ISTO PARA A 2.0
//        public int getPayloadLength()
//        {
//            return 0;  //To change body of implemented methods use File | Settings | File Templates.
//        }

//        public byte[] getPayload(byte[] bytes, int i) throws IOException
//        {
//            return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        public boolean isPayloadAvailable()
//        {
//            return false;  //To change body of implemented methods use File | Settings | File Templates.
//        }
    }

    /** Base class for LanguageModeling {@link TermEnum} implementations. */
    public static class LanguageModelTermEnum extends TermEnum {
        protected TermEnum in;

        public LanguageModelTermEnum(TermEnum in) {
            this.in = in;
        }

        public boolean next() throws IOException {
            return in.next();
        }
        public Term term() {
            return in.term();
        }
        public int docFreq() {
            return in.docFreq();
        }
        public void close() throws IOException {
            in.close();
        }
    }

    protected IndexReader in;
    protected int collSizeTotal;
    protected int totalDocFreqs;

    static final String totalDocFreqsString = "totalDocFreqs";
    static final String collSizeString = "collSizeTotal";
    static final String DOC_LENGTH = "doc_length";
    static final String lengthString = "Length";

    /**
     * <p>Construct a LanguageModelIndexReader based on the specified base reader.
     * Directory locking for delete, undeleteAll, and setNorm operations is
     * left to the base reader.</p>
     * <p>Note that base reader is closed if this LanguageModelIndexReader is closed.</p>
     * @param in specified base reader.
     */
    public LanguageModelIndexReader(IndexReader in) {
        super(in.directory());
        this.in = in;
        this.collSizeTotal = -1;
        this.totalDocFreqs = -1;
    }

    public TermFreqVector[] getTermFreqVectors(int docNumber)
            throws IOException {
        return in.getTermFreqVectors(docNumber);
    }

    public TermFreqVector getTermFreqVector(int docNumber, String field)
            throws IOException {
        return in.getTermFreqVector(docNumber, field);
    }

    public int numDocs() {
        return in.numDocs();
    }
    public int maxDoc() {
        return in.maxDoc();
    }

    public Document document(int n) throws IOException {
        return in.document(n);
    }

//    //LGTE extension todo check this
//    public Document document(int i, FieldSelector fieldSelector) throws CorruptIndexException, IOException
//    {
//        return in.document(i,fieldSelector);
//    }

    public boolean isDeleted(int n) {
        return in.isDeleted(n);
    }
    public boolean hasDeletions() {
        return in.hasDeletions();
    }
    protected void doUndeleteAll() throws IOException {
        in.undeleteAll();
    }

    public byte[] norms(String f) throws IOException {
        return in.norms(f);
    }
    public void norms(String f, byte[] bytes, int offset) throws IOException {
        in.norms(f, bytes, offset);
    }
    protected void doSetNorm(int d, String f, byte b) throws IOException {
        in.setNorm(d, f, b);
    }

    public TermEnum terms() throws IOException {
        return in.terms();
    }
    public TermEnum terms(Term t) throws IOException {
        return in.terms(t);
    }

    public int docFreq(Term t) throws IOException {
        return in.docFreq(t);
    }

    public TermDocs termDocs() throws IOException {
        return in.termDocs();
    }

    public TermPositions termPositions() throws IOException {
        return in.termPositions();
    }

    protected void doDelete(int n) throws IOException {
        LuceneVersionFactory.getLuceneVersion().deleteDocument(n,in);
    }
    protected void doCommit() throws IOException {
        in.commit();
    }
    protected void doClose() throws IOException {
        in.close();
    }

//    public Collection getFieldNames(FieldOption fieldOption)
//    {
//        //todo
//
//        return null;
//    }

    public Collection getFieldNames() throws IOException
    {
        //LGTE
        return LuceneVersionFactory.getLuceneVersion().getFieldsNames(in);
    }

    public Collection getFieldNames(boolean indexed) throws IOException
    {
        //LGTE
        return LuceneVersionFactory.getLuceneVersion().getFieldsNames(in,indexed);
    }

    /**
     *
     * @param storedTermVector if true, returns only Indexed fields that have term vector info,
     *                        else only indexed fields without term vector info
     * @return Collection of Strings indicating the names of the fields
     */
    public Collection getIndexedFieldNames(boolean storedTermVector) {
        return LuceneVersionFactory.getLuceneVersion().getIndexedFieldsNames(in,storedTermVector);
    }

    /**
     * @param field counts only tokens in the specified field
     *
     * @return Number of tokens in this field in the entire collection
     * (tokens = occurances of terms)
     * always >= number of terms.
     */
    public int getCollectionTokenNumber(String field) throws IOException {

        int collSize = 0;
        for (int doc = 0; doc < in.maxDoc(); ++doc) {
            collSize += in.getFieldLength(doc, field);
        }
        return collSize;
    }

    /**
     * @return Number of tokens in collection (tokens = occurances of terms)
     * always >= number of terms.
     */
    public int getCollectionTokenNumber() throws IOException {
        if (collSizeTotal == -1) {
            Object collSizeTotalObj = DataCacher.Instance().get(collSizeString);
            if (collSizeTotalObj != null) {
                collSizeTotal =
                        ((Integer) DataCacher.Instance().get(collSizeString))
                                .intValue();
            } else {
                collSizeTotal = 0;
                Collection indexedFields = in.getFieldNames(true);
                for (Iterator iterator = indexedFields.iterator();
                     iterator.hasNext();
                        ) {
                    String fieldName = (String) iterator.next();
                    //ALTERADO LGTE
                    if(ControlIndexesEnum.parse(fieldName) == null) //only add fields not in ControlIndexes Enum
                        collSizeTotal += getCollectionTokenNumber(fieldName);
                    //ALTERADO LGTE

                }
                DataCacher.Instance().put(
                        collSizeString,
                        new Integer(collSizeTotal));
            }
        }
        return collSizeTotal;
    }

    /**
     * Returns how often the token appears in collection
     * @param t is a term
     * @return the collection frequency of the term
     * @throws IOException
     */
    public int collFreq(Term t) throws IOException {
        TermDocs td = termDocs(t);
        int cf = td.freq();
        while (td.next()) {
            cf += td.freq();
        }
        return cf;
    }

    /**
     * @return Number of tokens in collection (tokens = occurances of terms)
     * always >= number of terms.
     */
    public int getTotalDocFreqs() throws IOException {
        if (totalDocFreqs == -1) {
            Object totalDF = DataCacher.Instance().get(totalDocFreqsString);
            if (totalDF != null) {
                totalDocFreqs = ((Integer) totalDF).intValue();
            } else {
                totalDocFreqs = 0;
                TermEnum allTerms = terms();
                do {
                    Term t = allTerms.term();
                    //ALTERADO LGTE
                    if(t != null && ControlIndexesEnum.parse(t.field()) == null) //only add fields not in ControlIndexes Enum
                    {
                        totalDocFreqs += docFreq(t);
                    }
                    //ALTERADO LGTE
                } while (allTerms.next());
                DataCacher.Instance().put(
                        totalDocFreqsString,
                        new Integer(totalDocFreqs));
            }
        }
        return totalDocFreqs;
    }


    /**
     * @param directory path to index directory
     */
    public void storeExtendedData(String directory) {
        // request information to make the DataCacher store it, then dump
        try {
            int totalDocFreqs = getTotalDocFreqs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            DataCacher.Instance().writeToFiles(directory);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * @param directory path to index directory
     */
    public void readExtendedData(String directory) {
        DataCacher.Instance().loadFromFiles(directory);
    }

    /*
      * @see org.apache.lucene.index.IndexReader#getFieldLength(int)
      */
    public int getFieldLength(int docNumber, String field) throws IOException {
        return in.getFieldLength(docNumber, field);
    }

    /*
      * @see org.apache.lucene.index.IndexReader#getDocLength(int)
      */
    public int getDocLength(int docNumber) throws IOException {
        return in.getDocLength(docNumber);
    }
}
