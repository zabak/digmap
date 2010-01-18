package pt.utl.ist.lucene;

import org.apache.lucene.search.*;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteIndexManager {


    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param reader for indexes
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, IndexReader reader) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(reader, model);
        initSimilarity(indexSearcher, model);
        return indexSearcher;
    }

    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param dir indexes
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, Directory dir) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(dir, model);
        initSimilarity(indexSearcher, model);
        return indexSearcher;
    }

    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param dir indexes
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, String dir) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(dir, model);
        initSimilarity(indexSearcher, model);
        return indexSearcher;
    }

    /**
     * Create a new Searcher for choosed Model
     * @param dir indexes
     * @param model choosed
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(File dir, Model model) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(dir, model);
        initSimilarity(indexSearcher, model);
        return indexSearcher;
    }

    public static IndexReader openReader(File f, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
            LanguageModelIndexReader reader = new LanguageModelIndexReader(IndexReader.open(f));
            reader.readExtendedData(f.getAbsolutePath());
            return reader;
        }
        else if(model == Model.VectorSpaceModel)
        {
            return IndexReader.open(f);
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }



    public static IndexReader openReader(String f, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
            LanguageModelIndexReader reader = new LanguageModelIndexReader(IndexReader.open(f));
            reader.readExtendedData(f);
            return reader;
        }
        else if(model == Model.VectorSpaceModel)
        {
            return IndexReader.open(f);
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

    private static final Logger logger = Logger.getLogger(LgteIndexManager.class);

    public static IndexReader openReader(Directory f, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
            logger.warn("Using Directory constructor caches are inactive");        
            return new LanguageModelIndexReader(IndexReader.open(f));
        }
        else if(model == Model.VectorSpaceModel)
        {
            return IndexReader.open(f);
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

    private static IndexSearcher initSearcher(IndexReader reader, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
            if(model == Model.LanguageModel)
                return new IndexSearcherLanguageModel(reader);
            else
                return new ProbabilisticCleanIndexSearcher(reader);
        }
        else if(model == Model.VectorSpaceModel)
        {
            return new IndexSearcher(reader);
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

    private static IndexSearcher initSearcher(Directory dir, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
            if(model == Model.LanguageModel)
                return new IndexSearcherLanguageModel(openReader(dir,model));
            else
                return new ProbabilisticCleanIndexSearcher(openReader(dir,model));
        }
        else if(model == Model.VectorSpaceModel)
        {
            return new IndexSearcher(dir);
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

    private static IndexSearcher initSearcher(String dir, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
            if(model == Model.LanguageModel)
                return new IndexSearcherLanguageModel(openReader(dir,model));
            else
                return new ProbabilisticCleanIndexSearcher(openReader(dir,model));
        }
        else if(model == Model.VectorSpaceModel)
        {
            return new IndexSearcher(dir);
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

    private static IndexSearcher initSearcher(File dir, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
            if(model == Model.LanguageModel)
                return new IndexSearcherLanguageModel(openReader(dir,model));
            else
                return new ProbabilisticCleanIndexSearcher(openReader(dir,model));
        }
        else if(model == Model.VectorSpaceModel)
        {
            return new IndexSearcher(openReader(dir,model));
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }



    private static void initSimilarity(IndexSearcher indexSearcher, Model model)
    {
        
        if(model.isProbabilistcModel())
        {
            indexSearcher.setSimilarity(new LangModelSimilarity());
            System.setProperty("RetrievalModel", model.getName());
        }
        else if(model == Model.VectorSpaceModel)
        {
            indexSearcher.setSimilarity(new DefaultSimilarity());
            System.setProperty("RetrievalModel", "VectorSpace");
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

//    private static void initDataCacher(Properties modelProperties)
//    {
//        //todo This is necessary in order to not affect actual results
//        //todo with previous results.
//        //todo It is necessary to understand wha is DataCacher of IPLS and be sure
//        //todo that this clear will not afect performance
////        DataCacher.Instance().clear();
//        if (DataCacher.Instance().get("LM-beta") == null)
//            if(modelProperties == null || modelProperties.get("LM-beta") == null)
//                DataCacher.Instance().put("LM-beta", ConfigProperties.getProperty("LM-beta"));
//            else
//                DataCacher.Instance().put("LM-beta", modelProperties.get("LM-beta"));
//
//        if (DataCacher.Instance().get("LM-lambda") == null)
//            if(modelProperties == null ||  modelProperties.get("LM-lambda") == null)
//                DataCacher.Instance().put("LM-lambda", ConfigProperties.getProperty("LM-lambda"));
//            else
//                DataCacher.Instance().put("LM-lambda", modelProperties.get("LM-lambda"));
//
//        if (DataCacher.Instance().get("LM-cmodel") == null)
//            if(modelProperties == null ||  modelProperties.get("LM-cmodel") == null)
//                DataCacher.Instance().put("LM-cmodel",  ConfigProperties.getProperty("LM-cmodel"));
//            else
//                DataCacher.Instance().put("LM-cmodel", modelProperties.get("LM-cmodel"));
//
//        if (DataCacher.Instance().get("LM-lengths") == null)
//            if(modelProperties == null ||  modelProperties.get("LM-lengths") == null)
//                DataCacher.Instance().put("LM-lengths", ConfigProperties.getProperty("LM-lengths"));
//            else
//                DataCacher.Instance().put("LM-lengths", modelProperties.get("LM-lengths"));
//    }



}
