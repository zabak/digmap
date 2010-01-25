package pt.utl.ist.lucene.treceval.geotime.queries;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.XPath;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.index.Term;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.geotime.index.Config;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

import com.pjaol.lucene.search.SerialChainFilter;

/**
 * @author Jorge Machado
 * @date 25/Jan/2010
 * @time 12:43:27
 * @email machadofisher@gmail.com
 */
public class StrategyQueryBuilder
{
    Document topics;
    List<QueryProcessor> queryProcessors;



    public StrategyQueryBuilder(String topicsFile,boolean timesKey) throws MalformedURLException, DocumentException
    {
        queryProcessors = new ArrayList<QueryProcessor>();
        topics = Dom4jUtil.parse(new File(topicsFile));
        XPath xPath = topics.createXPath("//topic");
        List<Element> topicsElems = xPath.selectNodes(topics);
        for(Element topic: topicsElems)
        {
            QueryParser queryParser = new QueryParser(topic);
            QueryProcessor queryProcessor = new QueryProcessor(queryParser.getQuery(),timesKey);
            queryProcessors.add(queryProcessor);
        }
    }

    public String buildQuery(Element topic, boolean timeKeys)
    {
        return null;
    }



    public abstract class Iterator
    {
        public class QueryPackage
        {
            public Filter filter;
            public String query;

            public QueryPackage(Filter filter, String query) {
                this.filter = filter;
                this.query = query;
            }
        }
        protected java.util.Iterator<QueryProcessor> queryProcessorIter;
        private Iterator(List<QueryProcessor> queryProcessors) {this.queryProcessorIter = queryProcessors.iterator();}
        public abstract QueryPackage next();
        //todo testar
        protected Filter createBaseFilter(QueryProcessor q)
        {
            if(q.wantPlaces() && q.wantTimes())
            {   TermsFilter timeFilter = new TermsFilter();
                if(q.isTime_key())
                    timeFilter.addTerm(new Term(Config.S_HAS_YYYY_KEY,"true"));
                else
                    timeFilter.addTerm(new Term(Config.S_HAS_ANY_TIME_POINT,"true"));  //OPTAR POR DURACOES

                TermsFilter filterGeo = new TermsFilter();
                filterGeo.addTerm(new Term(Config.S_GEO_INDEXED,"true"));

                Filter[] filterChain = new Filter[]{filterGeo,timeFilter};
                int[] actionType = new int[]{SerialChainFilter.AND,SerialChainFilter.AND};
                return new SerialChainFilter(filterChain,actionType);
            }
            else if(q.wantPlaces())
            {
                TermsFilter filter = new TermsFilter();
                filter.addTerm(new Term(Config.S_GEO_INDEXED,"true"));
                return filter;
            }
            else if(q.wantTimes())
            {
                if(q.isTime_key())
                {
                    TermsFilter filter = new TermsFilter();
                    filter.addTerm(new Term(Config.S_HAS_YYYY_KEY,"true"));
                    return filter;
                }
                else
                {
                    TermsFilter filter = new TermsFilter();
                    filter.addTerm(new Term(Config.S_HAS_ANY_TIME_POINT,"true"));  //OPTAR POR DURACOES
                    return filter;
                }
            }
            else return null;
        }
    }

    public class BaseFilteredIterator extends Iterator
    {
        private BaseFilteredIterator(List<QueryProcessor> queryProcessors) {super(queryProcessors);}

        public QueryPackage next() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public class BaseIterator extends Iterator
    {
        private BaseIterator(List<QueryProcessor> queryProcessors) {super(queryProcessors);}

        public QueryPackage next()
        {
            if(!queryProcessorIter.hasNext())
                return null;
            QueryProcessor queryProcessor = queryProcessorIter.next();
            Query q = queryProcessor.getQ();
            return new QueryPackage(createBaseFilter(queryProcessor),queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.CONTENTS));
        }
    }

    public static void main(String [] args) throws MalformedURLException, DocumentException
    {
        StrategyQueryBuilder strategyQueryBuilder = new StrategyQueryBuilder("D:\\Jorge\\Documents\\ist\\doutoramento\\conferencias\\NTCIR\\topics.xml",false);

    }
}
