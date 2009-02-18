package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.context.Context;
import pt.utl.ist.lucene.context.ContextNode;

import java.io.*;
import java.util.Map;

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

    /******************************************
     * Context Writer Methods
     */

    IndexWriter contextWriterProxy = null;
    //    IndexWriter documentContextWriterProxy = null;
    public static final String CONTEXT_RELATIVE_PATH = "Contexts";
    //    public static final String DOCUMENTS_CONTEXT_RELATIVE_PATH = "DocumentsContext";
    public static final String CID_CONTEXT_FILE = "Contexts/maxcid.txt";



    public String CONTEXT_RAW_CID = "r_cid";
    public String CONTEXT_RAW_FROM_INDEX = "r_from";
    public String CONTEXT_RAW_DEST_INDEX = "r_dest";
    public String CONTEXT_RAW_DIST_INDEX = "r_dist";

    public String CONTEXT_CID = "cid";
    public String CONTEXT_FROM_INDEX = "from";
    public String CONTEXT_DEST_INDEX = "dest";
    public String CONTEXT_DIST_INDEX = "dist";

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
                File contextFolder = new File(path + CONTEXT_RELATIVE_PATH);
                if(!contextFolder.exists())
                {
                    logger.info("Context folder don't exist yest, creating: " + contextFolder.getAbsolutePath());
                    contextFolder.mkdirs();
                }
                try{
                    contextWriterProxy = new IndexWriter(contextFolder, new LgteNothingAnalyzer(), false);
                }
                catch(FileNotFoundException e)
                {
                    logger.info("Context Index don't exist yest, creating: " + contextFolder.getAbsolutePath());
                    contextWriterProxy = new IndexWriter(contextFolder, new LgteNothingAnalyzer(), true);
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
     * @throws java.io.IOException on error
     */
    public void deleteContexts() throws IOException
    {
        File contextFolder = new File(path + CONTEXT_RELATIVE_PATH);
        if(!contextFolder.exists())
        {
            logger.info("Context folder don't exist yest, creating: " + contextFolder.getAbsolutePath());
        }
//        File documentContextFolder = new File(path + DOCUMENTS_CONTEXT_RELATIVE_PATH);
//        if(!documentContextFolder.exists())
//        {
//            logger.info("DocumentContextFolder folder don't exist yest, creating: " + documentContextFolder.getAbsolutePath());
//        }
        contextWriterProxy = new IndexWriter(contextFolder, new LgteNothingAnalyzer(), true);
//        documentContextWriterProxy = new IndexWriter(documentContextFolder, new LgteNothingAnalyzer(), true);
    }

    /**
     * Adds a context to context Index and will index contexts on close
     *
     * @param context to index
     * @return context ID
     */
    public synchronized long addContext(Context context) throws IOException
    {
        if(!initContextWriterProxy())
            return -1;

        logger.info("Generating distances betwween nodes");
        context.generateDistances();
        indexContext(context,maxCID);
        maxCID++;
        return maxCID;
    }

    private void initCid() throws IOException
    {
        if(maxCID < 0)
        {
            File cidFile = new File(path + CID_CONTEXT_FILE);
            if(cidFile.exists())
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
        if(maxCID > 0)
        {
            File cidFile = new File(path + CID_CONTEXT_FILE);
            if(cidFile.getParentFile().exists())
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

    public void deleteContext(long id)
    {

    }

    public void close() throws IOException
    {
        super.close();
        flushCID();
        if (contextWriterProxy != null)
            contextWriterProxy.close();
//        if (documentContextWriterProxy != null)
//            documentContextWriterProxy.close();
    }

    /**
     * index a context in context index
     * @param c
     * @param cid
     */
    private void indexContext(Context c, int cid) throws IOException
    {
        for(ContextNode fromNode: c.getNodes())
        {
            if(fromNode.getDistancesVector() != null && fromNode.getDistancesVector().size() > 0)
            {
                for(Map.Entry<ContextNode,Integer> entry: fromNode.getDistancesVector().entrySet())
                {
                    ContextNode destNode = entry.getKey();
                    Integer distance = entry.getValue();
                    if(distance >= 0 && distance < Integer.MAX_VALUE)
                    {
                        Document raw = new Document();
                        raw.add(new Field(CONTEXT_RAW_CID,"" + cid,true,true,false,true));
                        raw.add(new Field(CONTEXT_RAW_FROM_INDEX,"" + fromNode.getDocId(),true,true,false,true));
                        raw.add(new Field(CONTEXT_RAW_DEST_INDEX,"" + destNode,true,true,false,true));
                        raw.add(new Field(CONTEXT_RAW_DIST_INDEX,"" + distance,true,true,false,true));
                        contextWriterProxy.addDocument(raw);
                    }
                }
            }
        }
    }
}
