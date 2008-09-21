package pt.utl.ist.lucene;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.ilps.DataCacher;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import pt.utl.ist.lucene.config.ConfigProperties;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteIndexSearcherManager
{

    public static boolean extentData = false;


    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param reader for indexes
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, IndexReader reader) throws IOException
    {
        return openSearcher(model,reader,null);
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
        return openSearcher(model,dir,null);
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
        return openSearcher(model,dir,null);
    }
    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param dir indexes
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, File dir) throws IOException
    {
        return openSearcher(dir, model, null);
    }

    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param reader for indexes
     * @param modelProperties init properties of model, can be null
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, IndexReader reader, Properties modelProperties) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(reader, model);
        initSimilarity(indexSearcher, model, modelProperties);
        return indexSearcher;
    }

    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param dir indexes
     * @param modelProperties init properties of model, can be null
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, Directory dir, Properties modelProperties) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(dir, model);
        initSimilarity(indexSearcher, model, modelProperties);
        return indexSearcher;
    }

    /**
     * Create a new Searcher for choosed Model
     * @param model choosed
     * @param dir indexes
     * @param modelProperties init properties of model, can be null
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(Model model, String dir, Properties modelProperties) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(dir, model);
        initSimilarity(indexSearcher, model, modelProperties);
        return indexSearcher;
    }

    /**
     * Create a new Searcher for choosed Model
     * @param dir indexes
     * @param model choosed
     * @param modelProperties init properties of model, can be null
     * @return IndexSearcher
     * @throws IOException opening index
     */
    public static IndexSearcher openSearcher(File dir, Model model, Properties modelProperties) throws IOException
    {
        IndexSearcher indexSearcher = initSearcher(dir, model);
        initSimilarity(indexSearcher, model, modelProperties);
        return indexSearcher;
    }

    public static IndexReader openReader(File f, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
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

    public static IndexReader openReader(String f, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
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

    public static IndexReader openReader(Directory f, Model model) throws IOException
    {
        if(model.isProbabilistcModel())
        {
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
            return new IndexSearcherLanguageModel(reader);
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
            return new IndexSearcherLanguageModel(dir);
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
            return new IndexSearcherLanguageModel(dir);
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
            return new IndexSearcherLanguageModel(openReader(dir,model));
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



    private static void initSimilarity(IndexSearcher indexSearcher, Model model, Properties modelProperties)
    {
        
        if(model.isProbabilistcModel())
        {
            indexSearcher.setSimilarity(new LangModelSimilarity());
            System.setProperty("RetrievalModel", model.getName());
            if(!extentData)
            {
                ((IndexSearcherLanguageModel) indexSearcher).readExtendedDate(Globals.TMP_DIR);
                extentData = true;
            }
            initDataCacher(modelProperties);

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

    private static void initDataCacher(Properties modelProperties)
    {
        //todo This is necessary in order to not affect actual results
        //todo with previous results.
        //todo It is necessary to understand wha is DataCacher of IPLS and be sure
        //todo that this clear will not afect performance
        DataCacher.Instance().clear();
        if (DataCacher.Instance().get("LM-beta") == null)
            if(modelProperties == null || modelProperties.get("LM-beta") == null)
                DataCacher.Instance().put("LM-beta", ConfigProperties.getProperty("LM-beta"));
            else
                DataCacher.Instance().put("LM-beta", modelProperties.get("LM-beta"));

        if (DataCacher.Instance().get("LM-lambda") == null)
            if(modelProperties == null ||  modelProperties.get("LM-lambda") == null)
                DataCacher.Instance().put("LM-lambda", ConfigProperties.getProperty("LM-lambda"));
            else
                DataCacher.Instance().put("LM-lambda", modelProperties.get("LM-lambda"));

        if (DataCacher.Instance().get("LM-cmodel") == null)
            if(modelProperties == null ||  modelProperties.get("LM-cmodel") == null)
                DataCacher.Instance().put("LM-cmodel",  ConfigProperties.getProperty("LM-cmodel"));
            else
                DataCacher.Instance().put("LM-cmodel", modelProperties.get("LM-cmodel"));

        if (DataCacher.Instance().get("LM-lengths") == null)
            if(modelProperties == null ||  modelProperties.get("LM-lengths") == null)
                DataCacher.Instance().put("LM-lengths", ConfigProperties.getProperty("LM-lengths"));
            else
                DataCacher.Instance().put("LM-lengths", modelProperties.get("LM-lengths"));
    }



}
