package pt.utl.ist.lucene;

import org.apache.lucene.index.IndexReader;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 16/Jan/2010
 * @time 13:19:38
 * @email machadofisher@gmail.com
 */
public class IndexTreeManager
{
    Map<String,Offsets> offsetsMap = new HashMap<String,Offsets>();

    static IndexTreeManager instance = new IndexTreeManager();

    private IndexTreeManager(){}

    public static IndexTreeManager getInstance()
    {
        return instance;
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
     * @param doc
     * @param mapping
     * @param reader
     * @return
     */
    public int[] translateId(int doc,String mapping, IndexReader reader)
    {
        Offsets offsets = offsetsMap.get(mapping);
        if(offsets == null)
        {
            offsets = createOffsets(mapping,reader);
            offsetsMap.put(mapping,offsets);
        }
        return getMappingIds(doc,offsets);
    }

    /**
     *
     * @param mapping
     * @param reader
     * @return
     */
    private Offsets createOffsets(String mapping, IndexReader reader)
    {
        String[] fields = mapping.split(">");
        String[] fieldArgs = fields[0].split("\\(");
        String parentField = fieldArgs[0];
        String parentIdField = fieldArgs[1].substring(0,fieldArgs[1].length()-1);

        String[] childArgs = fields[1].split("\\(");
        String childField = childArgs[0];
        String childIdParentField = childArgs[1].substring(0,childArgs[1].length()-1);

        System.out.println("");
          return null;
    }

    private int[] getMappingIds(int doc , Offsets offsets)
    {
        return null;
    }

    private class Offsets
    {
        int[] offsets = null;
        int maxdocsInTargetField;

        public Offsets(int[] offsets, int maxdocsInTargetField)
        {
            this.offsets = offsets;
            this.maxdocsInTargetField = maxdocsInTargetField;
        }
    }

    public static void main(String[] args)
    {
        getInstance().createOffsets("contents(id)>sentences(doc_id)",null);
    }
}
