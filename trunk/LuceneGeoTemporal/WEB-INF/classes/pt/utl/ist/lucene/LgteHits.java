package pt.utl.ist.lucene;

import com.pjaol.search.geo.utils.DistanceUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.sort.LgteScorer;
import pt.utl.ist.lucene.versioning.LuceneVersion;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.VersionEncoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class wrap an instance of Hits and gives two distance vectors in space and time
 *
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteHits
{
    LuceneVersion luceneVersion = LuceneVersionFactory.getLuceneVersion();
    Map<Integer,Integer> timeDistancesYearsCache;
    Map<Integer,Long> timeDistances;
    Map<Integer,Double> spaceDistances;
    Hits hits;
    Sort sort;
    LgteQuery lgteQuery;


    /**
     * Create a new Wrapper
     *
     * @param hits wrapped
     * @param timeDistances in miliseconds for each document
     * @param spaceDistances in miliseconds for each document
     * @param sort sorter to get score
     * @param lgteQuery query
     */
    protected LgteHits(Hits hits, Map<Integer, Long> timeDistances, Map<Integer, Double> spaceDistances, Sort sort, LgteQuery lgteQuery)
    {
        this.timeDistances = timeDistances;
        this.spaceDistances = spaceDistances;
        this.hits = hits;
        this.sort = sort;
        this.lgteQuery = lgteQuery;
    }

    /**
     * Create a new Wrapper
     * @param hits wrapped
     * @param timeDistances in miliseconds for each document
     * @param spaceDistances in miliseconds for each document
     * @param lgteQuery query
     */
    protected LgteHits(Hits hits, Map<Integer, Long> timeDistances, Map<Integer, Double> spaceDistances, LgteQuery lgteQuery)
    {
        this.lgteQuery = lgteQuery;
        this.timeDistances = timeDistances;
        this.spaceDistances = spaceDistances;
        this.hits = hits;
    }

    /**
     * call default Class
     * @return hits lenght
     * @see org.apache.lucene.search.Hits
     */
    public int length()
    {
        return hits.length();
    }

    /**
     * create a DocumentWrapper
     * @param i position of document
     * @return a document wrapper
     * @throws java.io.IOException on error when accessing indexes
     */
    public LgteDocumentWrapper doc(int i) throws java.io.IOException
    {
        Document doc = hits.doc(i);
        return new LgteDocumentWrapper(doc);
    }

    /**
     * call default Class
     * @see org.apache.lucene.search.Hits
     * @return score
     * @param i position of document
     * @throws java.io.IOException on error when accessing indexes
     */
    public float score(int i) throws java.io.IOException
    {
        if(sort != null && sort instanceof LgteScorer)
            return ((LgteScorer)sort).getScore(hits.id(i),hits.score(i));
        return hits.score(i);
    }

    public int id(int i) throws java.io.IOException
    {
        return hits.id(i);
    }

    /**
     * Return Space Distance in Km
     * @param i doc position
     * @return space distance in Km
     * @throws IOException when try open to get id
     */
    public double spaceDistanceKm(int i) throws IOException
    {
        if(spaceDistances != null)
        {
            Double distance = spaceDistances.get(id(i));
            if(distance != null)
                return distance * 1.609344;
        }

        if(lgteQuery.getQueryParams().isSpatialPoint())
        {
            Double latitude = doc(i).getLatitude();
            Double longitude = doc(i).getLongitude();
            if(latitude != null && longitude != null)
                return DistanceUtils.getDistanceMi(lgteQuery.getQueryParams().getLatitude(),lgteQuery.getQueryParams().getLongitude(),latitude,longitude);
        }
        return -1;

    }

    /**
     * Return Space Distance in Km
     * @param i doc position
     * @return space distance in miles
     * @throws IOException when try open to get id
     */
    public double spaceDistanceMiles(int i) throws IOException
    {
        if(spaceDistances != null)
        {
            Double distance = spaceDistances.get(id(i));
            if(distance != null)
                return distance;
        }
        if(lgteQuery.getQueryParams().isSpatialPoint())
        {
            Double latitude = doc(i).getLatitude();
            Double longitude = doc(i).getLongitude();
            if(latitude != null && longitude != null)
                return DistanceUtils.getDistanceMi(lgteQuery.getQueryParams().getLatitude(),lgteQuery.getQueryParams().getLongitude(),latitude,longitude);
        }
        return -1;
    }

    /**
     * Return Time Distance in Miliseconds
     *
     * @param i doc position
     * @return time distance in Miliseconds
     * @throws IOException when try open to get id
     */
    public long timeDistanceMiliseconds(int i) throws IOException
    {
        return timeDistances.get(id(i));
    }

    static SimpleDateFormat simpleDateFormatYear = new SimpleDateFormat("yyyy");

    /**
     * Return Time Distance in Years
     *
     * @param i doc position
     * @return time distance in years
     * @throws IOException when try open to get id
     */
    public int timeDistanceYears(int i) throws IOException
    {
        if(timeDistancesYearsCache == null)
            timeDistancesYearsCache = new HashMap<Integer,Integer>();
        Integer difYears = timeDistancesYearsCache.get(i);
        if(difYears != null)
            return difYears;

        long distance = timeDistances.get(id(i));
        Date now = new Date();
        Date beforeAux = new Date(now.getTime() - distance);
        int yearNow = Integer.parseInt(simpleDateFormatYear.format(now));
        int yearBeforeAux = Integer.parseInt(simpleDateFormatYear.format(beforeAux));
        difYears = yearNow - yearBeforeAux;
        timeDistancesYearsCache.put(i,difYears);
        return difYears;
    }

    public Hits getHits()
    {
        return hits;
    }

    public String summary(int doc, String field) throws IOException, ParseException
    {
        return luceneVersion.highlight(
                Globals.HIGHLIGHT_FORMATTER,
                Globals.HIGHLIGHT_ENCODER,
                doc(doc).get(field),
                field,
                lgteQuery.getAnalyzer(),
                lgteQuery.getQuery(),
                Globals.HIGHLIGHT_FRAGMENT_SIZE,
                Globals.HIGHLIGHT_FRAGMENTS,
                Globals.HIGHLIGHT_FRAGMENT_SEPARATOR);
    }
    public String summary(int doc, String field, Formatter formater) throws IOException, ParseException
    {
        return luceneVersion.highlight(
                formater,
                Globals.HIGHLIGHT_ENCODER,
                doc(doc).get(field),
                field,
                lgteQuery.getAnalyzer(),
                lgteQuery.getQuery(),
                Globals.HIGHLIGHT_FRAGMENT_SIZE,
                Globals.HIGHLIGHT_FRAGMENTS,
                Globals.HIGHLIGHT_FRAGMENT_SEPARATOR);
    }
    public String summary(int doc, String field, VersionEncoder encoder) throws IOException, ParseException
    {
        return luceneVersion.highlight(
                Globals.HIGHLIGHT_FORMATTER,
                encoder,
                doc(doc).get(field),
                field,
                lgteQuery.getAnalyzer(),
                lgteQuery.getQuery(),
                Globals.HIGHLIGHT_FRAGMENT_SIZE,
                Globals.HIGHLIGHT_FRAGMENTS,
                Globals.HIGHLIGHT_FRAGMENT_SEPARATOR);
    }
    public String summary(int doc, String field, int fragmentSize,int fragments) throws IOException, ParseException
    {
        return luceneVersion.highlight(
                Globals.HIGHLIGHT_FORMATTER,
                Globals.HIGHLIGHT_ENCODER,
                doc(doc).get(field),
                field,
                lgteQuery.getAnalyzer(),
                lgteQuery.getQuery(),
                fragmentSize,
                fragments,
                Globals.HIGHLIGHT_FRAGMENT_SEPARATOR);
    }

    public String summary(int doc, String field, int fragmentSize,int fragments, String fragmentSeparator) throws IOException, ParseException
    {
        return luceneVersion.highlight(
                Globals.HIGHLIGHT_FORMATTER,
                Globals.HIGHLIGHT_ENCODER,
                doc(doc).get(field),
                field,
                lgteQuery.getAnalyzer(),
                lgteQuery.getQuery(),
                fragmentSize,
                fragments,
                fragmentSeparator);
    }

    public String summary(int doc, String field,VersionEncoder encoder, Formatter formater, int fragmentSize,int fragments, String fragmentSeparator) throws IOException, ParseException
    {
        return luceneVersion.highlight(
                formater,
                encoder,
                doc(doc).get(field),
                field,
                lgteQuery.getAnalyzer(),
                lgteQuery.getQuery(),
                fragmentSize,
                fragments,
                fragmentSeparator);
    }
}
