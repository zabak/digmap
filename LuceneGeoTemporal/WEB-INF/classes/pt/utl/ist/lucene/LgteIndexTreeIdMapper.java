package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.utl.ist.lucene.treceval.geotime.index.*;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;

/**
 * @author Jorge Machado
 * @date 16/Jan/2010
 * @time 13:19:38
 * @email machadofisher@gmail.com
 */
public class LgteIndexTreeIdMapper
{
    private static final Logger logger = Logger.getLogger(LgteIndexTreeIdMapper.class);
    Map<IndexReader,Offsets> offsetsMap = new HashMap<IndexReader,Offsets>();
    Map<IndexReader,Offsets> offsetsInvertedMap = new HashMap<IndexReader,Offsets>();

    LgteIsolatedIndexReader reader;

    public LgteIndexTreeIdMapper(LgteIsolatedIndexReader reader)
    {
        this.reader = reader;
    }

    /**
     * @param parent parent Index
     * @param child child Index
     * @param foreignkeyFieldChild2Parent field in child with the foreignkey to id field in parent
     *
     * 
     * This method iterates all documents in childs index and
     * creates an array with the size of the number of document in parent index field filled this the offsets where the child
     * changes his parent
     *
     * Example
     *
     *    contents:  DOC_1 DOC_2 DOC_3
     *    sentences: DOC_1_1 DOC_1_2 DOC_1_3 DOC_2_1 DOC_2_3 DOC_3_1 DOC_3_2
     *    the result will be a mapping array of offsets:
     *
     *    contents->statments = [0 ; 3 ; 5 ]
     *
     *    This means that if any one ask for the docIds in statments with the id 2 in the contents the
     * translate(0) -> [0 , 1, 2]
     * translate(1) -> [3 , 4]
     * translate(2) -> [5 , 6]
     *

     */

    public void addMapping(IndexReader parent,IndexReader child, String foreignkeyFieldChild2Parent)
    {
        createOffsets(parent,child,foreignkeyFieldChild2Parent);
    }

     public void addMappingUseOtherMappingOffsets(IndexReader parent,IndexReader child, IndexReader existentParent, String foreignkeyFieldChild2Parent)
    {
        Offsets offsets = offsetsMap.get(existentParent);
        offsetsMap.put(parent,offsets);
        offsetsInvertedMap.put(child,offsets);
    }


    public boolean hasMapping(String field)
    {
        IndexReader iReader = reader.getIndexReader(field);
        return offsetsMap.get(iReader)!=null;
    }


    /**
     * Mapping is of the type
     * fieldParentA(idField)>fieldChildB(AIdField)>--->fieldChildZ(XIdField)
     * example:
     *    contents(id)>sentences(doc_id)
     * This method iterates all documents in childs index and
     * creates an array with the size of the number of document in parent index field filled this the offsets where the child
     * changes his parent
     *
     * Example
     *
     *    contents:  DOC_1 DOC_2 DOC_3
     *    sentences: DOC_1_1 DOC_1_2 DOC_1_3 DOC_2_1 DOC_2_3 DOC_3_1 DOC_3_2
     *    the result will be a mapping array of offsets:
     *
     *    contents->statments = [0 ; 3 ; 5 ]
     *
     *    This means that if any one ask for the docIds in statments with the id 2 in the contents the
     * translate(0) -> [0 , 1, 2]
     * translate(1) -> [3 , 4]
     * translate(2) -> [5 , 6]
     *
     *
     *
     * @param doc start point doc
     * @param field of type  fieldParentA(idField)>fieldChildB(AIdField)>--->fieldChildZ(XIdField) e.g. contents(id)>sentences(doc_id)
     * @return an array of docs
     */
    public int[] translateId(int doc,String field)
    {
        IndexReader iReader = reader.getIndexReader(field);
        Offsets offsets = offsetsMap.get(iReader);
        if(offsets == null)
        {
            return null;
        }
        return doMapping(doc,offsets);
    }

    public int translateIdInverted(int doc, String field)
    {
        IndexReader iReader = reader.getIndexReader(field);
        Offsets offsets = offsetsInvertedMap.get(iReader);
        if(offsets == null)
        {
            return doc;
        }
        return offsets.offsetsInverted[doc];
    }

    public int translateIdInverted(int doc)
    {
        Offsets offsets = offsetsInvertedMap.entrySet().iterator().next().getValue();
        if(offsets == null)
        {
            return doc;
        }
        return offsets.offsetsInverted[doc];
    }

    public boolean isChild(IndexReader iReader)
    {
        return offsetsInvertedMap.get(iReader)!=null;
    }

    private Offsets createOffsets(IndexReader parent,IndexReader child, String foreignkeyFieldChild2Parent)
    {
        int parentSize = parent.maxDoc();
        int childSize = child.maxDoc();
        int[] offsets = new int[parentSize];
        int[] offsetsInvert = new int[childSize];

        try {
            TermEnum childs = child.terms(new Term(foreignkeyFieldChild2Parent,""));

            int p = 0;
            int c = 0;
            do
            {
                offsets[p] = c;
                int docFreq = reader.docFreq(childs.term());
                int max = c + docFreq;
                for(; c < max && c < offsetsInvert.length ;c++)
                {
                    offsetsInvert[c] = p;
                }
                p++;
            }
            while(childs.next() && childs.term().field().equals(foreignkeyFieldChild2Parent));
            Offsets toMap =  new Offsets(offsets,c,offsetsInvert);
            offsetsMap.put(parent,toMap);
            offsetsInvertedMap.put(child,toMap);
        }
        catch (IOException e)
        {
            logger.error(e,e);
        }
        return null;
    }


    private int[] doMapping(int doc , Offsets offsets)
    {
        int start = offsets.offsets[doc];
        int max = (doc == offsets.offsets.length-1) ? offsets.maxdocsInTargetField: offsets.offsets[doc+1];

        int[] docs = new int[max-start];
        for(int i = 0; i < docs.length;i++)
        {
            docs[i] = start;
            start++;
        }
        return docs;
    }



    private class Offsets
    {
        int[] offsets = null;
        int[] offsetsInverted = null;
        int maxdocsInTargetField;

        public Offsets(int[] offsets, int maxdocsInTargetField, int[] inverted)
        {
            this.offsets = offsets;
            this.maxdocsInTargetField = maxdocsInTargetField;
            this.offsetsInverted = inverted;
        }
    }

    public static void main(String[] args) throws IOException, ParseException {

//        TextSimilarityScorer tfidf = new TextSimilarityScorer(new SimpleTokenizer(true,true));
//        BasicStringWrapper doc1 = new BasicStringWrapper("a b c d e f g h i j k l m");
//        BasicStringWrapper doc2 = new BasicStringWrapper("b c d e f g h i");
//        List<BasicStringWrapper> list = new ArrayList<BasicStringWrapper>();
//        list.add(doc1);
//        list.add(doc2);
//        MyStringWrapperIterator myStringWrapperIterator = new MyStringWrapperIterator(list.iterator());
//        tfidf.train(myStringWrapperIterator);
//        System.out.println("BM25:" + tfidf.bm25("a b","a b c d e f g h i j k l m"));
//        System.out.println("BM25:" + tfidf.bm25("a b","b c d e f g h i"));

        LgteIndexSearcherWrapper searcher = Config.openMultiSearcherForContentsAndSentences();
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.k1", "1.2d");
        queryConfiguration.setProperty("bm25.b", "0.75d");
        queryConfiguration.setProperty("bm25.k3", "0.75d");
        queryConfiguration.setProperty("index.tree", "true");
        LgteQuery query = LgteQueryParser.parseQuery("g_allWoeid:WOEID-23424778", searcher, new LgteWhiteSpacesAnalyzer(), queryConfiguration);
        LgteHits hits = searcher.search(query);

        System.out.println(hits.doc(0).get("id"));
        System.out.println(hits.doc(1).get("id"));
        System.out.println(hits.doc(2).get("id"));
        System.out.println(hits.doc(3).get("id"));
        System.out.println(hits.doc(4).get("id"));

        searcher.close();

        System.out.println("#################3");


        searcher = Config.openMultiSearcherSentences();
        queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.k1", "1.2d");
        queryConfiguration.setProperty("bm25.b", "0.75d");
        queryConfiguration.setProperty("bm25.k3", "0.75d");
        query = LgteQueryParser.parseQuery("g_allWoeid_sentences:WOEID-23424778", searcher, new LgteWhiteSpacesAnalyzer(), queryConfiguration);
        hits = searcher.search(query);

        System.out.println(hits.doc(0).get("id"));
        System.out.println(hits.doc(1).get("id"));
        System.out.println(hits.doc(2).get("id"));
        System.out.println(hits.doc(3).get("id"));
        System.out.println(hits.doc(4).get("id"));

        searcher.close();

        System.out.println("#################3");



        searcher = Config.openMultiSearcher();
        queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.k1", "1.2d");
        queryConfiguration.setProperty("bm25.b", "0.75d");
        queryConfiguration.setProperty("bm25.k3", "0.75d");
        query = LgteQueryParser.parseQuery("g_allWoeid:WOEID-23424778", searcher, new LgteWhiteSpacesAnalyzer(), queryConfiguration);
        hits = searcher.search(query);

        System.out.println(hits.doc(0).get("id"));
        System.out.println(hits.doc(1).get("id"));
        System.out.println(hits.doc(2).get("id"));
        System.out.println(hits.doc(3).get("id"));
        System.out.println(hits.doc(4).get("id"));

        searcher.close();

    }

}
