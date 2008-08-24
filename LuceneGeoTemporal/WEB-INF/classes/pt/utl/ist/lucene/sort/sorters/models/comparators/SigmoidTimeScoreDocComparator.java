package pt.utl.ist.lucene.sort.sorters.models.comparators;

import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.solr.util.NumberUtils;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.IndexReaderPersistentCache;
import pt.utl.ist.lucene.sort.sorters.models.TimeDistancesScoreDocComparator;
import pt.utl.ist.lucene.utils.MyCalendar;
import pt.utl.ist.lucene.utils.GeoUtils;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;
import pt.utl.ist.lucene.level1query.QueryParams;

import java.io.IOException;
import java.util.Date;

/**
 * @author Jorge Machado
 * @date 18/Ago/2008
 * @see pt.utl.ist.lucene.sort.sorters.models
 */
public class SigmoidTimeScoreDocComparator implements TimeDistancesScoreDocComparator
{

    private static final Logger logger = Logger.getLogger(SigmoidTimeScoreDocComparator.class);

    public int radiumStrategy = ConfigProperties.getIntProperty("sigmoide.radium.strategy");
    QueryParams queryParams = null;
    public double alfa;
    public double alfa2;
    public double beta;
    Long radium = null;
    ITimeDistancesWrapper iTimeDistancesWrapper = null;

    static final String timewidthCacheCacheKey = "SigmoidTimeScoreDocComparator.timewidthCacheCacheKey";
    Long[] timewidthCache = null;

    static final String lowerCacheKey = "SigmoidTimeScoreDocComparator.lowerCacheKey";
    static final String higherCacheKey = "SigmoidTimeScoreDocComparator.higherCacheKey";
    static final String defaultRadiumCacheKey = "SigmoidTimeScoreDocComparator.defaultRadiumCacheKey";
    Long lower = (long) -1;
    Long higher = (long) -1;
    Long defaultRadium = (long) -1;

    public void init(IndexReader reader) throws IOException
    {
        //Find lower and higher terms
        if (lower < 0)
        {
            lower = (Long) IndexReaderPersistentCache.get(reader,lowerCacheKey);
            higher = (Long) IndexReaderPersistentCache.get(reader,higherCacheKey);
            defaultRadium = (Long) IndexReaderPersistentCache.get(reader,defaultRadiumCacheKey);
            if(lower == null || higher == null || defaultRadium == null)
            {
                logger.info("Initializing Time Indexes for Query Strategies");
                TermEnum termEnum = reader.terms(new Term(Globals.LUCENE_TIME_INDEX, ""));
                if (termEnum.term() == null)
                {
                    lower = (long)0;
                    higher = (long)0;
                    defaultRadium = (long)0;
                    IndexReaderPersistentCache.put(lowerCacheKey, (long) 0,reader);
                    IndexReaderPersistentCache.put(higherCacheKey, (long) 0,reader);
                    IndexReaderPersistentCache.put(defaultRadiumCacheKey, (long) 0,reader);
                }
                else
                {
                    Term first = null;
                    Term last = null;
                    boolean in = false;
                    if(termEnum.term().field().equals(Globals.LUCENE_TIME_INDEX))
                    {
                        in = true;
                        first = termEnum.term();
                        last = first;
                    }
                    else
                        while (termEnum.next()) //we have a first term
                        {
                            if (termEnum.term().field().equals(Globals.LUCENE_TIME_INDEX))
                            {
                                logger.info("Found lower Time");
                                in = true;
                                first = termEnum.term();
                                last = first;
                                break;
                            }
                        }
                    if (!in)
                    {
                        lower = (long)0;
                        higher = (long)0;
                        defaultRadium = (long)0;
                        IndexReaderPersistentCache.put(lowerCacheKey, (long) 0,reader);
                        IndexReaderPersistentCache.put(higherCacheKey, (long) 0,reader);
                        IndexReaderPersistentCache.put(defaultRadiumCacheKey, (long) 0,reader);
                    }
                    else
                    {
                        while (termEnum.next())
                            if (termEnum.term().field().equals(Globals.LUCENE_TIME_INDEX))
                            {
                                last = termEnum.term();
                            }

                        if (first != null && last != null)
                        {
                            logger.info("Found lower Time");
                            lower = Long.parseLong(NumberUtils.SortableStr2long(first.text()));
                            higher = Long.parseLong(NumberUtils.SortableStr2long(last.text()));
                            defaultRadium = (higher - lower) / 2;
                            IndexReaderPersistentCache.put(lowerCacheKey, (long) 0,reader);
                            IndexReaderPersistentCache.put(higherCacheKey, (long) 0,reader);
                            IndexReaderPersistentCache.put(defaultRadiumCacheKey, (long) 0,reader);
                            logger.info("Setting default parameter: <lower:" + lower + ":" + new Date(lower) + "; higher:" + higher + ":" + new Date(higher) + ">");
                            logger.info("default radium for strategy 1:" + defaultRadium);
                        }
                        termEnum.close();
                    }
                }
            }


            timewidthCache = (Long[]) IndexReaderPersistentCache.get(reader,timewidthCacheCacheKey);
            if(timewidthCache == null)
            {
                timewidthCache = new Long[reader.maxDoc()];
                IndexReaderPersistentCache.put(timewidthCacheCacheKey, timewidthCache,reader);

                logger.info("Initializing Documents Time width cache field");
                //Fill Time width Cache
                

                for (int i = 0; i < timewidthCache.length; i++)
                {
                    String width = reader.document(i).get(Globals.LUCENE_TIMEWIDTH_ORIGINAL_INDEX);
                    if (width == null)
                        timewidthCache[i] = null;
                    else
                        timewidthCache[i] = Long.parseLong(width);
                }
                logger.info("Time width cache size:" + timewidthCache.length);
            }
        }
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


    public Comparable sortValue(ScoreDoc scoreDoc)
    {
        if (iTimeDistancesWrapper == null || iTimeDistancesWrapper.getTimeDistances() == null)
            return 1f;
        Long distance = iTimeDistancesWrapper.getTimeDistance(scoreDoc.doc);

        Long timewidth = timewidthCache[scoreDoc.doc];
        if (timewidth != null)
        {
            return ((Double) GeoUtils.distancePointAreaMapExpDDmbr(distance, timewidth)).floatValue();
        }
        else
        {
            return ((Double) GeoUtils.sigmoideDistanceRadium(distance, radium, alfa, beta)).floatValue();
        }
    }

    private void fillRadium()
    {
        if (queryParams.getRadiumMiliseconds() >= 0)
        {
            radium = queryParams.getRadiumMiliseconds();
        }
        else
        {
            int strategy = queryParams.getQueryConfiguration().getIntProperty("sigmoide.radium.strategy");
            if (strategy == 1)
            {
                radium = defaultRadium;
            }
            else if (strategy == 2)
            {
                long distanceHigher = Math.abs(queryParams.getTimeMiliseconds() - higher);
                long distanceLower = Math.abs(queryParams.getTimeMiliseconds() - lower);
                if (distanceLower > 0 && distanceLower < distanceHigher)
                    radium = distanceLower;
                else if (distanceHigher > 0)
                    radium = distanceHigher;
                else
                    radium = defaultRadium;
                alfa = alfa2;
            }
            else if (strategy == 3)
            {
                MyCalendar m = new MyCalendar();
                radium = Math.abs(queryParams.getTimeMiliseconds() - m.getTimeInMillis());
                alfa = alfa2;
            }
        }
        if (radium == null || radium < 0)
            logger.error("Error filling radium, something is wrong please contact developers");
    }


    public int sortType()
    {
        return SortField.FLOAT;
    }

    public void addTimeDistancesWrapper(ITimeDistancesWrapper iTimeDistances)
    {
        this.iTimeDistancesWrapper = iTimeDistances;
    }

    public ITimeDistancesWrapper getTimeDistancesWrapper()
    {
        return iTimeDistancesWrapper;
    }

    public void cleanUp()
    {
        iTimeDistancesWrapper = null;
        queryParams = null;
        radium = null;
    }

    public void addQueryParams(QueryParams queryParams)
    {
        this.queryParams = queryParams;
        fillRadium();
        alfa = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.alfa");
        beta = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.beta");
        alfa2 = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.alfa.2");
    }

}
