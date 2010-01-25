package pt.utl.ist.lucene.treceval.geotime.queries;

import org.dom4j.*;

import java.util.List;

import pt.utl.ist.lucene.utils.Dom4jUtil;

/**
 * @author Jorge Machado
 * @date 24/Jan/2010
 * @time 12:01:08
 * @email machadofisher@gmail.com
 */
public class QueryParser
{
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
        readTerms();
        readPlaces();
        readTimes();
        readFilters();
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
        if(booleanElems == null || booleanElems.size() == 0)
            return;
        Element boolElem = booleanElems.get(0);
        String logicStr = boolElem.attribute("type").getValue();
        if(logicStr.equals("AND"))
            q.getFilterChain().getBooleanClause().setLogicValue(Query.FilterChain.BooleanClause.LogicValue.AND);
        else
            q.getFilterChain().getBooleanClause().setLogicValue(Query.FilterChain.BooleanClause.LogicValue.OR);

        XPath filterChainTermXPath = topic.createXPath("./filterChain/boolean/term");
        List<Element> termElems = filterChainTermXPath.selectNodes(topic);
        for(Element termElem: termElems)
        {
            XPath fieldX = termElem.createXPath("./field");
            XPath valueX = termElem.createXPath("./value");
            Query.FilterChain.BooleanClause.Term term = new Query.FilterChain.BooleanClause.Term();
            term.setField(((Element)fieldX.selectSingleNode(termElem)).getTextTrim());
            List<Element> valueElems = valueX.selectNodes(termElem);
            term.setValue(valueElems.get(0).getTextTrim());
            for(Element valueElem: valueElems)
            {
                Attribute attrWoeid = valueElem.attribute("woeid");
                if(attrWoeid != null)
                {
                    term.getWoeid().add(attrWoeid.getValue());
                }
            }
            q.getFilterChain().getBooleanClause().getTerms().add(term);
        }
    }
}
