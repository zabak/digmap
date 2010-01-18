package pt.utl.ist.lucene;

import org.apache.lucene.index.*;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 16/Jan/2010
 * @time 13:19:38
 * @email machadofisher@gmail.com
 */
public class LgteIndexTreeIdMapper
{
    private static final Logger logger = Logger.getLogger(LgteIndexTreeIdMapper.class);
    Map<String,Offsets> offsetsMap = new HashMap<String,Offsets>();
    Map<String,Offsets> offsetsInvertedMap = new HashMap<String,Offsets>();

    LgteIsolatedIndexReader reader;

    public LgteIndexTreeIdMapper(LgteIsolatedIndexReader reader)
    {
        this.reader = reader;
    }

//    public List<String> getMapping

    /**
     * @param mapping Mapping is of the type
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
     *    statements: DOC_1_1 DOC_1_2 DOC_1_3 DOC_2_1 DOC_2_3 DOC_3_1 DOC_3_2
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
    public void addMapping(String mapping)
    {
        createOffsets(mapping);
    }

    public boolean hasMapping(String field)
    {
        return offsetsMap.get(field) != null;
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
     *    statements: DOC_1_1 DOC_1_2 DOC_1_3 DOC_2_1 DOC_2_3 DOC_3_1 DOC_3_2
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
        Offsets offsets = offsetsMap.get(field);
        if(offsets == null)
        {
            return null;
        }
        return doMapping(doc,offsets);
    }

    public int translateIdInverted(int doc, String field)
    {
        Offsets offsets = offsetsInvertedMap.get(field);
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

    public boolean isInvertable(IndexReader reader)
    {
        return reader.numDocs() < offsetsMap.values().iterator().next().maxdocsInTargetField;
    }

    /**
     *
     * @param mapping of type  fieldParentA(idField)>fieldChildB(AIdField)>--->fieldChildZ(XIdField) e.g. contents(id)>sentences(doc_id)
     * @return
     */
    private Offsets createOffsets(String mapping)
    {
        String[] fields = mapping.split(">");
        String[] fieldArgs = fields[0].split("\\(");
        String parentField = fieldArgs[0];
        String parentIdField = fieldArgs[1].substring(0,fieldArgs[1].length()-1);

        String[] childArgs = fields[1].split("\\(");
        String childField = childArgs[0];
        String childIdParentField = childArgs[1].substring(0,childArgs[1].length()-1);

        IndexReader child = reader.getIndexReader(childField);
        IndexReader parent = reader.getIndexReader(parentField);
        int parentSize = parent.maxDoc();
        int childSize = child.maxDoc();
        int[] offsets = new int[parentSize];
        int[] offsetsInvert = new int[childSize];

        try {

            TermEnum childs = child.terms(new Term(childIdParentField,""));
            int p = 0;
            int c = 0;
            do
            {
                offsets[p] = c;
                int docFreq = reader.docFreq(childs.term());
                int max = c + docFreq;
                for(; c < max ;c++)
                {
                    offsetsInvert[c] = p;
                }
                p++; 
            }
            while(childs.next() && childs.term().field().equals(childIdParentField));
            Offsets toMap =  new Offsets(offsets,c,offsetsInvert);
            offsetsMap.put(parentField,toMap);
            offsetsInvertedMap.put(childField,toMap);
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

    public static void main(String[] args)
    {

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
        System.out.println(73938 % 7);
    }
    
}
