package pt.utl.ist.lucene.utils.queries;

import org.dom4j.*;
import org.apache.log4j.Logger;

import java.util.List;

import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.queries.Query;

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
        readOrignal();
        readTerms();
        readPlaces();
        readTimes();
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
