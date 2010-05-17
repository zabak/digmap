package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.Document;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.sort.sorters.TimeSpatialTextSorterSource;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.filter.ITimeSpatialDistancesWrapper;
import pt.utl.ist.lucene.versioning.LuceneVersion;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteIndexSearcherWrapper
{
    private IndexSearcher indexSearcher;

    Model defaultModel = Model.parse(ConfigProperties.getProperty("lucene.model"));

    Model model = defaultModel;

    LuceneVersion luceneVersion = LuceneVersionFactory.getLuceneVersion();

    private static final Logger logger = Logger.getLogger(LgteIndexSearcherWrapper.class);

    public LgteIndexSearcherWrapper(Model model, String s)
            throws IOException
    {
        this.model = model;
        ModelManager.getInstance().setModel(model);
        indexSearcher = LgteIndexManager.openSearcher(model, s);
    }

    public LgteIndexSearcherWrapper(Model model, String dir, Properties modelProperties)
            throws IOException
    {
        this.model = model;
        ModelManager.getInstance().setModel(model,modelProperties);
        indexSearcher = LgteIndexManager.openSearcher(model, dir);
    }

    public LgteIndexSearcherWrapper(Model model, Directory directory)
            throws IOException
    {
        this.model = model;
        ModelManager.getInstance().setModel(model);
        indexSearcher = LgteIndexManager.openSearcher(model, directory);
    }

    public LgteIndexSearcherWrapper(Model model, IndexReader indexReader) throws IOException
    {
        this.model = model;
        ModelManager.getInstance().setModel(model);
        indexSearcher = LgteIndexManager.openSearcher(model, indexReader);
    }

    public LgteIndexSearcherWrapper(String s)
            throws IOException
    {
        logger.info("using default space model");
        ModelManager.getInstance().setModel(defaultModel);
        indexSearcher = LgteIndexManager.openSearcher(defaultModel, s);
    }

    public LgteIndexSearcherWrapper(Directory directory)
            throws IOException
    {
        logger.info("using default space model");
        ModelManager.getInstance().setModel(defaultModel);
        indexSearcher = LgteIndexManager.openSearcher(defaultModel, directory);
    }

    public LgteIndexSearcherWrapper(IndexReader indexReader) throws IOException
    {
        logger.info("using default space model");
        ModelManager.getInstance().setModel(defaultModel);
        indexSearcher = LgteIndexManager.openSearcher(defaultModel, indexReader);
    }

    public IndexSearcher getIndexSearcher()
    {
        return indexSearcher;
    }


    public Explanation explain(String query, int doc, Analyzer a) throws java.io.IOException, ParseException
    {
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,this,a);
        return explainCascadeModels(lgteQuery.getQuery(), doc);
    }

    public Explanation explain(LgteQuery lgteQuery, int doc) throws java.io.IOException, ParseException
    {
        return explainCascadeModels(lgteQuery.getQuery(), doc);
    }

    public LgteHits search(String query, Analyzer a) throws java.io.IOException, ParseException
    {

        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,this,a);
        return searchAndFilter(lgteQuery);
    }

    public LgteHits search(String query) throws java.io.IOException, ParseException
    {

        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,this);
        return searchAndFilter(lgteQuery);
    }




    private LgteHits searchAndFilter(LgteQuery lgteQuery, LgteSort sort) throws IOException
    {
        FilterOrchestrator filterOrchestrator = new FilterOrchestrator();
        Filter finalFilter = filterOrchestrator.getFilter(lgteQuery);
        sort.addTimeSpaceDistancesWrapper(filterOrchestrator);
        Hits hits = cascadeModels(lgteQuery.getQuery(),finalFilter, sort);
        return new LgteHits(hits,filterOrchestrator.getTimeDistances(),filterOrchestrator.getSpaceDistances(), sort,lgteQuery);
    }

    private Explanation explainCascadeModels(Query query, int doc) throws IOException {
        Model model = ModelManager.getInstance().getModel();
        while(model.getCascadeModel() != null)
        {
            indexSearcher.search(query);
            ModelManager.getInstance().setOnlyModel(model.getCascadeModel());
            model = model.getCascadeModel();
        }
        return indexSearcher.explain(query,doc);
    }

    private Hits cascadeModels(Query query, Filter filter) throws IOException {
        Model model = ModelManager.getInstance().getModel();
        while(model.getCascadeModel() != null)
        {
            indexSearcher.search(query,filter);
            ModelManager.getInstance().setOnlyModel(model.getCascadeModel());
            model = model.getCascadeModel();
        }
        return indexSearcher.search(query,filter);
    }

    private Hits cascadeModels(Query query, Filter filter, Sort sort) throws IOException {
        Model model = ModelManager.getInstance().getModel();
        while(model.getCascadeModel() != null)
        {
            indexSearcher.search(query,filter,sort);
            ModelManager.getInstance().setOnlyModel(model.getCascadeModel());
            model = model.getCascadeModel();
        }
        return indexSearcher.search(query,filter,sort);
    }

    private TopDocs cascadeModels(Query query, Filter filter, int i) throws IOException {
        Model model = ModelManager.getInstance().getModel();
        while(model.getCascadeModel() != null)
        {
            indexSearcher.search(query,filter,i);
            ModelManager.getInstance().setOnlyModel(model.getCascadeModel());
            model = model.getCascadeModel();
        }
        return indexSearcher.search(query,filter,i);
    }

    private void cascadeModels(HitCollector hitCollector, Query query, Filter filter) throws IOException {
        Model model = ModelManager.getInstance().getModel();
        while(model.getCascadeModel() != null)
        {
            indexSearcher.search(query,filter);
            ModelManager.getInstance().setOnlyModel(model.getCascadeModel());
            model = model.getCascadeModel();
        }
        indexSearcher.search(query,filter,hitCollector);
    }




    private LgteHits searchAndFilter(LgteQuery lgteQuery, Filter userFilter, LgteSort sort) throws IOException
    {
        FilterOrchestrator filterOrchestrator = new FilterOrchestrator();
        sort.addTimeSpaceDistancesWrapper(filterOrchestrator);
        Filter finalFilter = filterOrchestrator.getFilter(lgteQuery,userFilter);
        Hits hits = cascadeModels(lgteQuery.getQuery(),finalFilter, sort);
        return new LgteHits(hits,filterOrchestrator.getTimeDistances(),filterOrchestrator.getSpaceDistances(),sort,lgteQuery);
    }

    /*****************
     * METHODS WITH NO SORT PARAMETER
     * will check in query
     */

    /**
     * @param lgteQuery  to searchCallback
     * @return hits
     * @throws IOException reading indexes
     */
    private LgteHits searchAndFilter(LgteQuery lgteQuery) throws IOException
    {
        if(!ModelManager.getInstance().hasModel())
            ModelManager.getInstance().setModel(lgteQuery.getQueryParams().getModel());
        if(!ModelManager.getInstance().hasQueryConfiguration())
            ModelManager.getInstance().setQueryConfiguration(lgteQuery.getQueryParams().getQueryConfiguration());

        FilterOrchestrator filterOrchestrator = new FilterOrchestrator();
        Filter finalFilter = filterOrchestrator.getFilter(lgteQuery);
        LgteSort sort = checkSort(lgteQuery,filterOrchestrator);
        Hits hits;
        if(sort != null)
        {
            hits = cascadeModels(lgteQuery.getQuery(),finalFilter, sort);
        }
        else
        {
            hits = cascadeModels(lgteQuery.getQuery(),finalFilter);
        }

        return new LgteHits(hits,filterOrchestrator.getTimeDistances(),filterOrchestrator.getSpaceDistances(),sort,lgteQuery);
    }
    /**
     *
     * @param query to searchCallback
     * @param qeEnum default value no
     * @throws ParseException parsing query
     * @param field searchCallback field
     * @param analyzer to analyze text
     * @return LgteHits
     * @throws java.io.IOException using indexes
     */
    public LgteHits search(String query, String field, Analyzer analyzer, QEEnum qeEnum) throws java.io.IOException, ParseException
    {
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setForceQE(qeEnum);
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query, field, analyzer, this, queryConfiguration);
        return searchAndFilter(lgteQuery);
    }

    /**
     *
     * @param query to searchCallback
     * @param qeEnum default value no
     * @return LgteHits
     * @throws java.io.IOException using indexes
     * @throws ParseException parsing query
     */
    public LgteHits search(String query, QEEnum qeEnum) throws java.io.IOException, ParseException
    {
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setForceQE(qeEnum);
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,this,queryConfiguration);
        return searchAndFilter(lgteQuery);
    }


    private LgteSort checkSort(LgteQuery lgteQuery, ITimeSpatialDistancesWrapper wrapper)
    {
        if((lgteQuery.getQueryParams().isSpatial() && lgteQuery.getQueryParams().getOrder().isSpatial()) || (lgteQuery.getQueryParams().isTime() && lgteQuery.getQueryParams().getOrder().isTime()))
        {
            TimeSpatialTextSorterSource dsort = new TimeSpatialTextSorterSource(lgteQuery.getQueryParams());
            dsort.addQueryParams(lgteQuery.getQueryParams());
            LgteSort sort = new LgteSort(new SortField("foo", dsort));
            sort.addTimeSpaceDistancesWrapper(wrapper);
            return sort;
        }
        return null;
    }
    private LgteHits searchAndFilter(LgteQuery lgteQuery, Filter userFilter) throws IOException
    {
        FilterOrchestrator filterOrchestrator = new FilterOrchestrator();
        Filter finalFilter = filterOrchestrator.getFilter(lgteQuery,userFilter);
        LgteSort sort = checkSort(lgteQuery, filterOrchestrator);
        Hits hits;
        if(sort != null)
        {
            hits = cascadeModels(lgteQuery.getQuery(),finalFilter, sort);
        }
        else
        {
            hits = cascadeModels(lgteQuery.getQuery(),finalFilter);
        }
        return new LgteHits(hits,filterOrchestrator.getTimeDistances(),filterOrchestrator.getSpaceDistances(),sort,lgteQuery);
    }


    public org.apache.lucene.search.TopDocs search(String query, String field, Analyzer analyzer, Filter filter, int i) throws java.io.IOException, ParseException
    {
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,field, analyzer,this);
        FilterOrchestrator filterOrchestrator = new FilterOrchestrator();
        Filter finalFilter = filterOrchestrator.getFilter(lgteQuery,filter);
        return cascadeModels(lgteQuery.getQuery(),finalFilter, i);
    }

    public void search(String query, String field, Analyzer analyzer, org.apache.lucene.search.Filter filter, HitCollector hitCollector) throws java.io.IOException, ParseException
    {
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,field, analyzer,this);
        if(lgteQuery.getQueryParams().getModel() != null) ModelManager.getInstance().setModel(lgteQuery.getQueryParams().getModel());
        FilterOrchestrator filterOrchestrator = new FilterOrchestrator();
        Filter finalFilter = filterOrchestrator.getFilter(lgteQuery,filter);
        cascadeModels(hitCollector,lgteQuery.getQuery(),finalFilter);
    }

    public LgteHits search(Query query) throws java.io.IOException, ParseException {
        LgteQuery lgteQuery = new LgteQuery(query);
        return searchAndFilter(lgteQuery);
    }


    public LgteHits search(LgteQuery lgteQuery) throws java.io.IOException
    {
        return searchAndFilter(lgteQuery);
    }

    public LgteHits search(LgteQuery lgteQuery, Filter filter) throws java.io.IOException
    {
        return searchAndFilter(lgteQuery,filter);
    }


    public LgteHits search(String query, LgteSort sort) throws java.io.IOException, ParseException
    {
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,this,sort);
        return searchAndFilter(lgteQuery, sort);
    }


    public LgteHits search(LgteQuery query, LgteSort sort) throws java.io.IOException
    {
        return searchAndFilter(query, sort);
    }

    public LgteHits search(String query, Filter filter, LgteSort sort) throws java.io.IOException, ParseException
    {
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,this,sort);
        return searchAndFilter(lgteQuery,filter,sort);
    }

    public LgteHits search(LgteQuery query, Filter filter, LgteSort sort) throws java.io.IOException
    {
        return searchAndFilter(query,filter,sort);
    }

    public void search(LgteQuery lgteQuery, HitCollector hitCollector) throws java.io.IOException
    {
        FilterOrchestrator filterOrchestrator = new FilterOrchestrator();
        Filter finalFilter = filterOrchestrator.getFilter(lgteQuery);
        cascadeModels(hitCollector,lgteQuery.getQuery(),finalFilter);
    }

    public LgteHits search(String query, Filter filter) throws java.io.IOException, ParseException
    {
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,this);
        return searchAndFilter(lgteQuery,filter);
    }

    public IndexReader getIndexReader()
    {
        return luceneVersion.getReader(indexSearcher);
    }



    public void close() throws java.io.IOException
    {
        IndexReaderPersistentCache.clean(luceneVersion.getReader(indexSearcher));
        indexSearcher.close();
        indexSearcher = null;
    }
}
