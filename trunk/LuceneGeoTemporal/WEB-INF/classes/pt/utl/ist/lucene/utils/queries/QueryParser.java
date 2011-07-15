package pt.utl.ist.lucene.utils.queries;

import org.apache.log4j.Logger;
import org.dom4j.*;
import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.util.List;

/**
 * @author Jorge Machado
 * @date 24/Jan/2010
 * @time 12:01:08
 * @email machadofisher@gmail.com
 */
public class QueryParser
{

    private static final Logger logger= Logger.getLogger(QueryParser.class);
    Element topic;
    Query q;


    public QueryParser(String topicXml) throws DocumentException
    {
        Document dom = Dom4jUtil.parse(topicXml);
        this.topic = dom.getRootElement();
        parse();
    }

    public QueryParser(Element topic)
    {
        this.topic = topic;
        parse();
    }

    public Query getQuery()
    {
        return q;
    }

    private void parse()
    {

        q = new Query();
        q.setId(topic.attribute("id").getValue());
        System.out.println("Processing topic " + q.getId());
        readOrignal();
        readTerms();
        readPlaces();
        readTimes();
        readWikipediaTerms();
        readGeoTemporalQueries();
        readFilters();
    }

    private void readOrignal()
    {
        XPath xPathTermsDesc = topic.createXPath("./original/desc");
        XPath xPathTermsNarr = topic.createXPath("./original/narr");
        q.setOriginalDesc(((Element)xPathTermsDesc.selectSingleNode(topic)).getTextTrim());
        q.setOriginalNarr(((Element)xPathTermsNarr.selectSingleNode(topic)).getTextTrim());

        XPath xPathTermsDescClean = topic.createXPath("./originalClean/desc");
        XPath xPathTermsNarrClean = topic.createXPath("./originalClean/narr");
        q.setOriginalDescClean(((Element)xPathTermsDescClean.selectSingleNode(topic)).getTextTrim());
        q.setOriginalNarrClean(((Element)xPathTermsNarrClean.selectSingleNode(topic)).getTextTrim());
    }

    private void readTerms()
    {
        XPath xPathTermsDesc = topic.createXPath("./terms/desc");
        XPath xPathTermsNarr = topic.createXPath("./terms/narr");
        q.getTerms().setDesc(((Element)xPathTermsDesc.selectSingleNode(topic)).getTextTrim());
        q.getTerms().setNarr(((Element)xPathTermsNarr.selectSingleNode(topic)).getTextTrim());
    }

    private void readPlaces()
    {
        XPath xPathPlaces = topic.createXPath("./places/term");
        List<Element> termPlacesElem = xPathPlaces.selectNodes(topic);
        for(Element termElem: termPlacesElem)
        {
            Query.Places.Term term = new Query.Places.Term();
            term.setPlace(termElem.getTextTrim());
            term.getWoeid().add(termElem.attribute("woeid").getValue());
            q.getPlaces().getTerms().add(term);
        }
    }

    private void readWikipediaTerms()
    {
        XPath xPathWikiPlaces = topic.createXPath("//wikpedia[@type='place' and ../@id='" + q.getId() + "']");
        List<Element> termPlacesElem = xPathWikiPlaces.selectNodes(topic.getDocument());
        for(Element termElem: termPlacesElem)
        {
            Query.WikipediaTerms.PlaceTerm term = new Query.WikipediaTerms.PlaceTerm();
            term.setTerm(termElem.attribute("term").getValue());
            term.setWoeid(termElem.attribute("woeid").getValue());
            term.setBoost(termElem.attribute("boost").getValue());

            q.getWikipediaTerms().getPlaceTerms().add(term);
        }

        XPath xPathWikiTimes = topic.createXPath("//wikpedia[@type='time' and ../@id='" + q.getId() + "']");
        List<Element> termsTimesElem = xPathWikiTimes.selectNodes(topic.getDocument());
        for(Element termElem: termsTimesElem)
        {
            Query.WikipediaTerms.TimeTerm term = new Query.WikipediaTerms.TimeTerm();
            term.setTerm(termElem.attribute("term").getValue());
            term.setTime(termElem.attribute("time").getValue());
            term.setBoost(termElem.attribute("boost").getValue());

            q.getWikipediaTerms().getTimeTerms().add(term);
        }
    }

    private void readGeoTemporalQueries()
    {
        XPath xPathTimeMetric = topic.createXPath("//time_metric_query[../@id='" + q.getId() + "']");
        List<Element> timeMetricElems = xPathTimeMetric.selectNodes(topic.getDocument());
        if(timeMetricElems.size() > 0)
        {
            Element timeMetricElem   = timeMetricElems.get(0);
            Query.TemporalQuery temporalQuery = new Query.TemporalQuery();
            temporalQuery.setQuery(timeMetricElem.getTextTrim());
            if(timeMetricElem.attribute("wikipedia") != null && timeMetricElem.attribute("wikipedia").getValue().equals("true"))
                temporalQuery.setWikipedia(true);
            else
                temporalQuery.setWikipedia(false);
            q.setTemporalQuery(temporalQuery);
        }
        else
            q.setTemporalQuery(null);

        XPath xPathGeoMetric = topic.createXPath("//geo_metric_query[../@id='" + q.getId() + "']");
        List<Element> geoMetricElems = xPathGeoMetric.selectNodes(topic.getDocument());
        if(geoMetricElems.size() > 0)
        {
            Element geoMetricElem   = geoMetricElems.get(0);
            Query.GeographicQuery geographicQuery = new Query.GeographicQuery();
            geographicQuery.setQuery(geoMetricElem.getTextTrim());
            if(geoMetricElem.attribute("wikipedia") != null && geoMetricElem.attribute("wikipedia").getValue().equals("true"))
                geographicQuery.setWikipedia(true);
            else
                geographicQuery.setWikipedia(false);
            q.setGeographicQuery(geographicQuery);
        }
        else
            q.setGeographicQuery(null);



//        XPath xPathWikiTimes = topic.createXPath("//wikpedia[@type='time' and ../@id='" + q.getId() + "']");
//        List<Element> termsTimesElem = xPathWikiTimes.selectNodes(topic.getDocument());
//        for(Element termElem: termsTimesElem)
//        {
//            Query.WikipediaTerms.TimeTerm term = new Query.WikipediaTerms.TimeTerm();
//            term.setTerm(termElem.attribute("term").getValue());
//            term.setTime(termElem.attribute("time").getValue());
//            term.setBoost(termElem.attribute("boost").getValue());
//
//            q.getWikipediaTerms().getTimeTerms().add(term);
//        }
    }

    private void readTimes()
    {
        XPath xPathPlaces = topic.createXPath("./times/term");
        List<Element> termTimesElem = xPathPlaces.selectNodes(topic);
        for(Element termElem: termTimesElem)
        {
            Query.Times.Term term = new Query.Times.Term();
            term.setTime(termElem.getTextTrim());
            q.getTimes().getTerms().add(term);
        }
    }

    private void readFilters()
    {
        XPath filterChainBoolXPath = topic.createXPath("./filterChain/boolean");
        List<Element> booleanElems = filterChainBoolXPath.selectNodes(topic);
        if(booleanElems != null && booleanElems.size() == 1)
        {
            Element boolElem = booleanElems.get(0);
            loadBooleanClause(boolElem,q.getFilterChain().getBooleanClause());
            return;
        }
        if(booleanElems != null && booleanElems.size() > 1)
        {
            logger.error("Parse error: just one boolean element allowed inside filter chain");
        }
    }

    private void loadBooleanClause(Element boolElem, Query.FilterChain.BooleanClause booleanClause)
    {
        String logicStr = boolElem.attribute("type").getValue();
        if(logicStr.equals("AND"))
            booleanClause.setLogicValue(Query.FilterChain.BooleanClause.LogicValue.AND);
        else
            booleanClause.setLogicValue(Query.FilterChain.BooleanClause.LogicValue.OR);

        List<Element> booleanTermElems = boolElem.elements();
        if(booleanTermElems == null || booleanTermElems.size() == 0)
        {
            logger.error("Parse Error: at least one boolean term needed inside a boolean clause");
            return;
        }

        for(Element booleanTermElem: booleanTermElems)
        {
            if(booleanTermElem.getName().equals("boolean"))
            {
                Query.FilterChain.BooleanClause innerBooleanClause = booleanClause.createBooleanClause();
                loadBooleanClause(booleanTermElem, innerBooleanClause);
            }
            else
            {
                Query.FilterChain.BooleanClause.Term term = booleanClause.createTerm();
                loadBooleanTermTerm(booleanTermElem, term);
            }

        }
    }

    private void loadBooleanTermTerm(Element booleanTermElem, Query.FilterChain.BooleanClause.Term term)
    {

        XPath fieldX = booleanTermElem.createXPath("./field");
        XPath valueX = booleanTermElem.createXPath("./value");
        Attribute attr = booleanTermElem.attribute("type");
        if(attr != null && attr.getValue().equals("duration"))
            term.setDuration(true);
        else
            term.setDuration(false);

        term.setField(((Element)fieldX.selectSingleNode(booleanTermElem)).getTextTrim());
        List<Element> valueElems = valueX.selectNodes(booleanTermElem);
        term.setValue(valueElems.get(0).getTextTrim());
        for(Element valueElem: valueElems)
        {
            Attribute attrWoeid = valueElem.attribute("woeid");
            if(attrWoeid != null)
            {
                term.getWoeid().add(attrWoeid.getValue());
            }
        }
    }
}
