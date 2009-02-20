package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.context.Context;
import pt.utl.ist.lucene.context.ContextNode;
import pt.utl.ist.lucene.utils.Files;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge
 * @date 17/Fev/2009
 * @time 15:15:20
 */
public class LgteContextIndexWriter extends LgteIndexWriter
{
    private static final Logger logger = Logger.getLogger(LgteContextIndexWriter.class);
    String path;


    public LgteContextIndexWriter(String s, Analyzer analyzer, boolean b)
            throws IOException
    {
        super(s, analyzer, b);
        initCid();
        path = s;
    }

    public LgteContextIndexWriter(String s, boolean b)
            throws IOException
    {
        super(s, b);
        initCid();
        path = s;
    }

    public LgteContextIndexWriter(String s, boolean b, Model model)
            throws IOException
    {
        super(s, b, model);
        initCid();
        path = s;
    }

    public LgteContextIndexWriter(String s, Analyzer analyzer, boolean b, Model model)
            throws IOException
    {
        super(s, analyzer, b, model);
        initCid();
        path = s;
    }

    public LgteContextIndexWriter(File file, Analyzer analyzer, boolean b)
            throws IOException
    {
        super(file, analyzer, b);
        initCid();
        path = file.getAbsolutePath();
    }

    public LgteContextIndexWriter(File file, Analyzer analyzer, boolean b, Model model)
            throws IOException
    {
        super(file, analyzer, b, model);
        initCid();
        path = file.getAbsolutePath();
    }

    public LgteContextIndexWriter(Directory directory, Analyzer analyzer, boolean b)
            throws IOException
    {
        super(directory, analyzer, b);
        if (directory instanceof FSDirectory)
        {
            path = ((FSDirectory) directory).getFile().getAbsolutePath();
        }
        else if (directory instanceof RAMDirectory)
        {
            logger.warn("Context writer not implemented to RAMDirectory");
        }
    }

    public LgteContextIndexWriter(Directory directory, Analyzer analyzer, boolean b, Model model)
            throws IOException
    {
        super(directory, analyzer, b, model);
        if (directory instanceof FSDirectory)
        {
            path = ((FSDirectory) directory).getFile().getAbsolutePath();
        }
        else if (directory instanceof RAMDirectory)
        {
            logger.warn("Context writer not implemented to RAMDirectory");
        }
    }

    /**
     * ***************************************
     * Context Writer Methods
     */

    IndexWriter contextWriterProxy = null;
    //    IndexWriter documentContextWriterProxy = null;
    public static final String CONTEXT_RAW_RELATIVE_PATH = "ContextsRaw";
    public static final String CONTEXT_INDEXES_RELATIVE_PATH = "ContextsIndexes";
    public static final String DOCUMENTS_CONTEXT_TEMPORATY_PATH = "DocsWithContexts";
    public static final String CID_CONTEXT_FILE = "Contexts/maxcid.txt";


    public String CONTEXT_RAW_FIELD = "r_field";
    public String CONTEXT_RAW_CID = "r_cid";
    public String CONTEXT_RAW_FROM_INDEX = "r_from";
    public String CONTEXT_RAW_TO_INDEX = "r_to";
    public String CONTEXT_RAW_FROM_TO_INDEX = "r_from_to";
    public String CONTEXT_RAW_DIRECT_FROM_INDEX = "r_direct_link_from";
    public String CONTEXT_RAW_DIRECT_TO_INDEX = "r_direct_link_to";

    public String CONTEXT_CID = "cid_seq";
    public String CONTEXT_FROM_INDEX = "from";
    public String CONTEXT_TO_INDEX = "to";
    public String CONTEXT_DIST_INDEX = "dist";
    public String CONTEXT_FROM_TO_INDEX = "from_to";
    public String CONTEXT_DIRECT_FROM_INDEX = "direct_link_from";
    public String CONTEXT_DIRECT_TO_INDEX = "direct_link_to";

    private int maxCID = -1;

    private boolean initContextWriterProxy()
    {
        if (path == null)
        {
            logger.warn("Lgte Context Writer not implemented to RAMDirectory");
            return false;
        }
        try
        {
            if (contextWriterProxy == null)
            {
                File contextFolder = new File(path + CONTEXT_RAW_RELATIVE_PATH);
                if (!contextFolder.exists())
                {
                    logger.info("Context folder don't exist yest, creating: " + contextFolder.getAbsolutePath());
                    contextFolder.mkdirs();
                }
                try
                {
                    contextWriterProxy = new IndexWriter(contextFolder, new LgteWhiteSpacesAnalyzer(), false);
                }
                catch (FileNotFoundException e)
                {
                    logger.info("Context Index don't exist yest, creating: " + contextFolder.getAbsolutePath());
                    contextWriterProxy = new IndexWriter(contextFolder, new LgteWhiteSpacesAnalyzer(), true);
                }
            }

//            if (documentContextWriterProxy == null)
//            {
//                File documentContextFolder = new File(path + DOCUMENTS_CONTEXT_RELATIVE_PATH);
//                if(!documentContextFolder.exists())
//                {
//                    logger.info("DocumentContextFolder folder don't exist yest, creating: " + documentContextFolder.getAbsolutePath());
//                }
//                documentContextWriterProxy = new IndexWriter(documentContextFolder, new LgteNothingAnalyzer(), false);
//            }
        }
        catch (IOException e)
        {
            logger.error(e, e);
            return false;
        }
        return true;
    }

    /**
     * Create a new context index
     *
     * @throws java.io.IOException on error
     */
    public void deleteContexts() throws IOException
    {
        File contextFolder = new File(path + CONTEXT_RAW_RELATIVE_PATH);
        if (contextWriterProxy != null)
            contextWriterProxy.close();
        if (!contextFolder.exists())
        {
            logger.info("Context folder don't exist yest, creating: " + contextFolder.getAbsolutePath());
            contextFolder.mkdirs();
        }

//        File documentContextFolder = new File(path + DOCUMENTS_CONTEXT_RELATIVE_PATH);
//        if(!documentContextFolder.exists())
//        {
//            logger.info("DocumentContextFolder folder don't exist yest, creating: " + documentContextFolder.getAbsolutePath());
//        }
        contextWriterProxy = new IndexWriter(contextFolder, new LgteWhiteSpacesAnalyzer(), true);
//        documentContextWriterProxy = new IndexWriter(documentContextFolder, new LgteNothingAnalyzer(), true);
    }

    /**
     * Adds a context to context Index and will index contexts on close
     *
     * @param context to index
     * @return context ID
     * @throws java.io.IOException on write error
     */
    public synchronized long addContext(Context context) throws IOException
    {
        if (!initContextWriterProxy())
            return -1;

        logger.info("Generating distances betwween nodes");
        context.generateDistances();
        indexContext(context, maxCID);
        maxCID++;
        return maxCID;
    }

    private void initCid() throws IOException
    {
        if (maxCID < 0)
        {
            File cidFile = new File(path + CID_CONTEXT_FILE);
            if (cidFile.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(cidFile));
                String cid = reader.readLine();
                reader.close();
                maxCID = Integer.parseInt(cid);
            }
            else
            {
                logger.info("CID File dows not exist yet, using 0");
                maxCID = 0;
            }
        }
    }

    private void flushCID() throws IOException
    {
        if (maxCID > 0)
        {
            File cidFile = new File(path + CID_CONTEXT_FILE);
            if (cidFile.getParentFile().exists())
            {
                logger.info("Flushing CID with:" + maxCID);
                FileWriter cidFileWriter = new FileWriter(cidFile);
                cidFileWriter.write("" + maxCID);
                cidFileWriter.close();
            }
            else
                logger.warn("Context Folder does not exist, can't flush CID");
        }
        else
            logger.info("No CID to flush");
    }

    public void deleteContext(int id)
    {
        deleteDocument(new Term(CONTEXT_RAW_CID, "" + id));
    }

    public void close() throws IOException
    {
        super.close();
        flushCID();
        if (contextWriterProxy != null)
        {
            contextWriterProxy.optimize();
            contextWriterProxy.close();
        }
//        if (documentContextWriterProxy != null)
//            documentContextWriterProxy.close();
        generateIdIndexes(CONTEXT_RAW_RELATIVE_PATH,CONTEXT_INDEXES_RELATIVE_PATH + "from", CONTEXT_RAW_FROM_INDEX);
        generateIdIndexes(CONTEXT_INDEXES_RELATIVE_PATH + "from",CONTEXT_INDEXES_RELATIVE_PATH,CONTEXT_RAW_TO_INDEX);
        indexContextsInDocuments();
    }

    /**
     * index a context in context index
     *
     * @param c context
     * @param cid to index
     *
     * @throws java.io.IOException on write error
     */
    private void indexContext(Context c, int cid) throws IOException
    {
        for (ContextNode fromNode : c.getNodes())
        {
            if (fromNode.getDistancesVector() != null && fromNode.getDistancesVector().size() > 0)
            {
                for (Map.Entry<ContextNode, Integer> entry : fromNode.getDistancesVector().entrySet())
                {
                    ContextNode toNode = entry.getKey();
                    Integer distance = entry.getValue();
                    if (distance >= 0 && distance < Integer.MAX_VALUE)
                    {
                        Document raw = new Document();
                        raw.add(new Field(CONTEXT_RAW_FIELD, "" + c.getField(), true, true, false, true));
                        raw.add(new Field(CONTEXT_RAW_CID, "" + cid, true, true, false, true));
                        raw.add(new Field(CONTEXT_RAW_FROM_INDEX, "" + fromNode.getDocId(), true, true, false, true));
                        raw.add(new Field(CONTEXT_RAW_TO_INDEX, "" + toNode.getDocId(), true, true, false, true));
                        raw.add(new Field(CONTEXT_DIST_INDEX, "" + distance, true, true, false, true));
                        raw.add(new Field(CONTEXT_RAW_FROM_TO_INDEX, "" + fromNode.getDocId() + " " + toNode.getDocId(), true, true, true, true));
                        if (distance == 1)
                        {
                            raw.add(new Field(CONTEXT_RAW_DIRECT_FROM_INDEX, "" + fromNode.getDocId(), true, true, false, true));
                            raw.add(new Field(CONTEXT_RAW_DIRECT_TO_INDEX, "" + toNode.getDocId(), true, true, false, true));
                        }
                        contextWriterProxy.addDocument(raw);
                    }
                }
            }
        }
    }

    /**
     *
     * This method augment context lines creating keys to real document id's using docNo
     * will create an index field for each raw field using real ids in original lucene index
     *
     * @param from index folder to iterate
     * @param to index folder of augmented index
     * @param field to iterate
     * @throws IOException on index errors
     *
     */
    public void generateIdIndexes(String from, String to, String field) throws IOException
    {

        DocIdDocNoIterator idDocNoIterator = getDocIdDocNoIterator();
        File contextIdIndexesFolder = new File(path + to);
        if (!contextIdIndexesFolder.exists())
        {
            logger.info("Context Indexes folder don't exist yest, creating: " + contextIdIndexesFolder.getAbsolutePath());
            contextIdIndexesFolder.mkdirs();
        }

        IndexReader readerContextRaw = IndexReader.open(path + from);
        IndexWriter contextWriterIndexesProxy = new IndexWriter(contextIdIndexesFolder, new LgteWhiteSpacesAnalyzer(), true);
        TermDocs termDocs = readerContextRaw.termDocs();
        while(idDocNoIterator.next())
        {

            int doc = idDocNoIterator.getDocId();
            String docNo = idDocNoIterator.getDocNo();
            termDocs.seek(new Term(field,docNo));
            int contextCounter = -1;
            HashMap<String, Integer> cidDelta = new HashMap<String,Integer>();
            while(termDocs.next())
            {
                Document raw = readerContextRaw.document(termDocs.doc());
                if(raw.get(CONTEXT_CID)==null)
                {
                    String cid = raw.get(CONTEXT_RAW_CID);
                    Integer cidInteger = cidDelta.get(cid);
                    if(cidInteger == null)
                    {
                        cidInteger = ++contextCounter;
                        cidDelta.put(cid,cidInteger);
                    }
                    raw.add(new Field(CONTEXT_CID, "" + cidInteger, true, true, false, true));
                }



                if(raw.get(CONTEXT_RAW_FROM_INDEX).equals(docNo))
                {
                    raw.add(new Field(CONTEXT_FROM_INDEX, "" + doc, true, true, false, true));
                    String fromToOld = raw.get(CONTEXT_FROM_TO_INDEX);
                    if(fromToOld != null)
                    {
                        raw.removeField(CONTEXT_FROM_TO_INDEX);
                        raw.add(new Field(CONTEXT_FROM_TO_INDEX,doc + " " +  fromToOld, true, true, false, true));
                    }
                    else
                        raw.add(new Field(CONTEXT_FROM_TO_INDEX, "" + doc, true, true, false, true));
                }
                else
                {
                    raw.add(new Field(CONTEXT_TO_INDEX, "" + doc, true, true, false, true));
                    String fromToOld = raw.get(CONTEXT_FROM_TO_INDEX);
                    if(fromToOld != null)
                    {
                        raw.removeField(CONTEXT_FROM_TO_INDEX);
                        raw.add(new Field(CONTEXT_FROM_TO_INDEX, fromToOld + " " + doc, true, true, true, true));
                    }
                    else
                        raw.add(new Field(CONTEXT_FROM_TO_INDEX, "" + doc, true, true, true, true));
                }

                if(raw.get(CONTEXT_RAW_DIRECT_FROM_INDEX) != null)
                {
                    if(raw.get(CONTEXT_RAW_DIRECT_FROM_INDEX).equals(docNo))
                        raw.add(new Field(CONTEXT_DIRECT_FROM_INDEX, "" + doc, true, true, false, true));
                    else
                        raw.add(new Field(CONTEXT_DIRECT_TO_INDEX, "" + doc, true, true, false, true));
                }
                contextWriterIndexesProxy.addDocument(raw);
            }
        }

        contextWriterIndexesProxy.optimize();
        contextWriterIndexesProxy.close();
        readerContextRaw.close();
        idDocNoIterator.close();
    }


    /**
     * The objective of this method is to augment documents with context fields organized by distance from the source node
     * At the end each context found should produce a new field with all text in context documents found
     * At the end each context found should produce in affected documents a contextfield$0 with the original text
     * e.g.
     *   before
     *   doc1  (contents: A E I) (authors: A B C)
     *   doc2  (contents: O) (authors: D E F)
     *   doc3  (contents: U) (authors: G)
     *
     *   doc1 <-(contents (CONTEXT_1))-> doc2
     *   doc1 <-(contents (CONTEXT_2))-> doc3
     *
     *   after method
     *   in document 1 context 1 will get relative ID = 0 and context 2 will get relative id 1
     *   in document 2 context 1 will get relative ID = 0
     *   in document 3 context 2 will get relative ID = 0
     *   in document 1 context 2 will get relative ID = 1
     *
     *   Will be created indexes relative to context (cid_seq in contextLines) and distance (dist in context lines) from source:
     *
     *   index [field] will keep all text from all documents in all contexts in some field
     *   index [field]$0 will keep original text of document
     *   indexes format  [field]$$[relativeContext]$[distance]
     *
     *   doc1  (contents: A E I O U) (contents$0: A E I) (contents$$0$1: O) (contents$$1$1: U) (authors: A B C )
     *   doc2  (contents: O A E I) (contents$0: O) (contents$$0$1: A E I) (authors: D E F)
     *   doc3  (contents: U A E I) (contents$0: U) (contents$$0$1: A E I) (authors: G)
     *
     * This method iterate all the documents in collection
     *    0 - for each document t
     *          1 - the method load all context entries where document is a from node
     *            1.1 - For each from arrow
     *              1.1.1 - If not exist is created a bucket to merge all fields from target documents to create index [field]
     *                      the fields started by [field]$$ (context distance fields) are removed from the document
     *                      Field $0 or field is loaded and field$0 is initialized and added to bucket
     *              1.1.2 - Target context document found in line is loaded
     *              1.1.3 - lookup for contextField$0 or contextField in target document
     *              1.1.4 - Add found field to bucket contextField separated with a '\n' in the end
     *              1.1.5 - Add a field [field]$$[cid_seq]$[dist] with text present in target doc contextField
     *          2 - For each context in bucket
     *              create in document a field [field] for each line in field bucket
     *          3 - add new document to new index
     *          4 - Replace index with new index 
     * @throws IOException onm index IO errors
     */
    public void indexContextsInDocuments() throws IOException
    {
        DocIdDocNoIterator idDocNoIterator = getDocIdDocNoIterator();
        IndexReader docsReader = IndexReader.open(path);
        IndexReader readerContextRaw = IndexReader.open(path + CONTEXT_INDEXES_RELATIVE_PATH);
        new File(path + DOCUMENTS_CONTEXT_TEMPORATY_PATH).mkdirs();
        LgteIndexWriter writer = new LgteIndexWriter(path + DOCUMENTS_CONTEXT_TEMPORATY_PATH,getAnalyzer(),true);

        // 0 - for each document t
        TermDocs termDocs = readerContextRaw.termDocs();
        while(idDocNoIterator.next())
        {
            int doc = idDocNoIterator.getDocId();
            String docNo = idDocNoIterator.getDocNo();
            Document document = docsReader.document(doc);
            Map<String,StringBuilder> docContextBucket = new HashMap<String,StringBuilder>();

            //lets obtain all lines for term with doc id in from field
            termDocs.seek(new Term(CONTEXT_FROM_INDEX,"" + doc));

            // 1 - the method load all context entries where document is a from node
            while(termDocs.next())
            {
                // 1.1 - For each from arrow
                Document raw = readerContextRaw.document(termDocs.doc());
                //lets obtain the context field e.g. contents (a context in contents field) 
                String contextField = raw.get(CONTEXT_RAW_FIELD);
                //lets check if other lines refered this context field
                StringBuilder contextBucket = docContextBucket.get(contextField);
                if(contextBucket == null)
                {
                    // 1.1.1 - If not exist is created a bucket to merge all fields from target documents to create index [field] in this document with all contents
                    contextBucket = new StringBuilder();
                    docContextBucket.put(contextField,contextBucket);
                    //         The fields started by [field]$$ (context distance fields) are removed from the document
                    //         because all these fields will be indexed again
                    String distanceFieldsPrefix = contextField + "$$";
                    document.removeFields(distanceFieldsPrefix);
                    //         Field $0 or field is loaded and field$0 is initialized and added to bucket
                    String originalTargetTextField = contextField + "$0";
                    String field0text = document.get(originalTargetTextField);
                    if(field0text == null)
                    {
                        logger.info("Adding context first time to document " + docNo + " in field " + contextField + " ..setting " + contextField + "$0");
                        field0text = document.get(contextField);
                        document.add(new Field(originalTargetTextField,field0text,true,true,true,true));

                    }
                    logger.debug("removing " + contextField + " in document " + docNo + " ...will keep text in Bucket");
                    document.removeField(contextField);

                    logger.debug("appending " + contextField + " in bucket of " + docNo + " with field " + contextField);
                    contextBucket.append(field0text).append('\n');
                }

                // 1.1.2 - Target context document found in line is loaded
                int targetDocId = Integer.parseInt(raw.get(CONTEXT_TO_INDEX));
                Document targetDoc = docsReader.document(targetDocId);

                // 1.1.3 - lookup for contextField$0 or contextField in target document
                String targetContextField0 = targetDoc.get(contextField + "$0");
                if(targetContextField0 == null)
                    targetContextField0 = targetDoc.get(contextField);

                //1.1.4 - Add found field to bucket contextField separated with a '\n' in the end
                contextBucket.append(targetContextField0).append('\n');

                //1.1.5 - Add a field [field]$$[cid_seq]$[dist] with text present in target doc contextField
                String cid_seq = raw.get(CONTEXT_CID);
                String dist = raw.get(CONTEXT_DIST_INDEX);
                String contextDistanceField = contextField + "$$" + cid_seq + "$" + dist;
                document.add(new Field(contextDistanceField,targetContextField0,true,true,true,true));
            }
            // 2 - For each target context original text in bucket
            //     create a new field in document
            for(Map.Entry<String,StringBuilder> field:  docContextBucket.entrySet())
            {
                BufferedReader reader = new BufferedReader(new StringReader(field.getValue().toString()));
                String line0;
                while((line0=reader.readLine()) != null)
                {
                    document.add(new Field(field.getKey(),line0,true,true,true,true));
                }
                reader.close();
            }

            // 3 - add new document to new index
            writer.addDocument(document);
        }

        idDocNoIterator.close();
        docsReader.close();
        readerContextRaw.close();
        writer.optimize();
        writer.close();
        
        // 4 - Replace index with new index
        Files.delDirsE(path);
        Files.copyDirectory(new File(path + DOCUMENTS_CONTEXT_TEMPORATY_PATH), new File(path));
    }




}
