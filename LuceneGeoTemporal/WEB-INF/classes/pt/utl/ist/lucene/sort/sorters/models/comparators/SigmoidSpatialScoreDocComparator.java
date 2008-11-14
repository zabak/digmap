package pt.utl.ist.lucene.sort.sorters.models.comparators;

import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.solr.util.NumberUtils;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.IndexReaderPersistentCache;
import pt.utl.ist.lucene.sort.sorters.models.SpatialDistancesScoreDocComparator;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.utils.GeoUtils;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 18/Ago/2008
 * @see pt.utl.ist.lucene.sort.sorters.models
 */
public class SigmoidSpatialScoreDocComparator implements SpatialDistancesScoreDocComparator
{
    private static final Logger logger = Logger.getLogger(SigmoidSpatialScoreDocComparator.class);


    public double alfa;
    public double alfa2;
    public double beta;
    ISpatialDistancesWrapper iSpatialDistancesWrapper = null;
    QueryParams queryParams = null;

    //this index is directly used from lucene CacheField
    String[] diagonalIndex;
    String[] internalCircleRadiumIndex;

    static String biggerDiagonalCacheKey = "biggerDiagonalCacheKey";
    static String twiceBiggerDiagonalCacheKey = "twiceBiggerDiagonalCacheKey";

    static String biggerRadiumCacheKey = "biggerDiagonalCacheKey";

    Double biggerDiagonal = null;
    Double twiceBiggerDiagonal = null;
    Double halfBiggerDiagonal = null;

    Double biggerInternalCircleRadium = null;

    ISpatialScoreStrategy strategy = null;
    

    private void initBiggerDiagonal(IndexReader reader) throws IOException
    {
        logger.info("Initializing Spatial Indexes for Queries Strategies");
        if(biggerDiagonal == null)
        {
            biggerDiagonal = (Double) IndexReaderPersistentCache.get(reader,biggerDiagonalCacheKey);
            twiceBiggerDiagonal = (Double) IndexReaderPersistentCache.get(reader,twiceBiggerDiagonalCacheKey);
            if(biggerDiagonal == null || twiceBiggerDiagonal == null)
            {
                biggerDiagonal = 0.0;
                Term last = null;
                TermEnum termEnum = reader.terms(new Term(Globals.LUCENE_DIAGONAL_INDEX,""));
                if(termEnum.term() != null && termEnum.term().field().equals(Globals.LUCENE_DIAGONAL_INDEX))
                    last = termEnum.term();
                if(termEnum.term() != null)
                    while(termEnum.next())
                        if(termEnum.term().field().equals(Globals.LUCENE_DIAGONAL_INDEX))
                            last = termEnum.term();
                if(last != null)
                {
                    biggerDiagonal = NumberUtils.SortableStr2double(last.text());
                    logger.info("Found bigger spatial width:" + biggerDiagonal);
                }
                twiceBiggerDiagonal = 2*biggerDiagonal;
                halfBiggerDiagonal = biggerDiagonal / ((double )2);
                logger.info("defining twice bigger spatial width:" + twiceBiggerDiagonal);
                termEnum.close();
                IndexReaderPersistentCache.put(biggerDiagonalCacheKey,biggerDiagonal,reader);
                IndexReaderPersistentCache.put(twiceBiggerDiagonalCacheKey,twiceBiggerDiagonal,reader);
            }
        }

        if(biggerInternalCircleRadium == null)
        {
            biggerInternalCircleRadium = (Double) IndexReaderPersistentCache.get(reader,biggerRadiumCacheKey);
            if(biggerInternalCircleRadium == null)
            {
                biggerInternalCircleRadium = 0.0;
                Term last = null;
                TermEnum termEnum = reader.terms(new Term(Globals.LUCENE_RADIUM_INDEX,""));
                if(termEnum.term() != null && termEnum.term().field().equals(Globals.LUCENE_RADIUM_INDEX))
                    last = termEnum.term();
                if(termEnum.term() != null)
                    while(termEnum.next())
                        if(termEnum.term().field().equals(Globals.LUCENE_RADIUM_INDEX))
                            last = termEnum.term();
                if(last != null)
                {
                    biggerInternalCircleRadium = NumberUtils.SortableStr2double(last.text());
                    logger.info("Found bigger spatial width:" + biggerInternalCircleRadium);
                }
                termEnum.close();
                IndexReaderPersistentCache.put(biggerRadiumCacheKey,biggerInternalCircleRadium,reader);
            }
        }
    }

    public void init(IndexReader reader) throws IOException
    {
        initBiggerDiagonal(reader);
        //This line must be after initBiggerDiagonal to lucene can put vales in cache
        diagonalIndex = FieldCache.DEFAULT.getStrings(reader, Globals.LUCENE_DIAGONAL_INDEX);
        internalCircleRadiumIndex = FieldCache.DEFAULT.getStrings(reader, Globals.LUCENE_RADIUM_INDEX); 
    }

    //Will not be used by us
    public int compare(ScoreDoc scoreDoc1, ScoreDoc scoreDoc2)
    {
        Float score1 = (Float) sortValue(scoreDoc1);
        Float score2 = (Float) sortValue(scoreDoc2);

        if (score1 > score2) return -1;
        if (score1 < score2) return 1;
        return 0;
    }

    private void initStrategy()
    {
        if(strategy == null)
        {
            String startegyClassName;
            if(queryParams != null)
            {
                startegyClassName = queryParams.getQueryConfiguration().getProperty("spatial.score.strategy");
            }
            else
            {
                startegyClassName = ConfigProperties.getProperty("spatial.score.strategy");
            }
            try
            {
                strategy = (ISpatialScoreStrategy) Class.forName(startegyClassName).newInstance();
            }
            catch (ClassNotFoundException e)
            {
                logger.error(e,e);
            }
            catch (IllegalAccessException e)
            {
                logger.error(e,e);
            }
            catch (InstantiationException e)
            {
                logger.error(e,e);
            }
            strategy.init(biggerDiagonal,queryParams,iSpatialDistancesWrapper,diagonalIndex,internalCircleRadiumIndex);
        }
    }

    public Comparable sortValue(ScoreDoc scoreDoc)
    {
        initStrategy();
        return strategy.sortValue(scoreDoc);
    }

    public int sortType()
    {
        return SortField.FLOAT;
    }

    public void addSpaceDistancesWrapper(ISpatialDistancesWrapper iSpatialDistances)
    {
        this.iSpatialDistancesWrapper = iSpatialDistances;
    }

    public ISpatialDistancesWrapper getSpaceDistancesWrapper()
    {
        return iSpatialDistancesWrapper;
    }

    public void cleanUp()
    {
        iSpatialDistancesWrapper = null;
        queryParams = null;
    }

    public void addQueryParams(QueryParams queryParams)
    {
        //restart strategy
        strategy = null;
        this.queryParams = queryParams;
        alfa = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.alfa");
        beta = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.beta");
        alfa2 = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.alfa.2");
    }
}
