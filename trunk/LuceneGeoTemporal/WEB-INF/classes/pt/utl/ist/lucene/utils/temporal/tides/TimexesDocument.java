package pt.utl.ist.lucene.utils.temporal.tides;

import org.apache.log4j.Logger;
import org.dom4j.*;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 19:52:33
 * @email machadofisher@gmail.com
 */
public class TimexesDocument
{
    private static final Logger logger = Logger.getLogger(TimexesDocument.class);

    List<Timex2TimeExpression> timex2TimeExpressionsList = new ArrayList<Timex2TimeExpression>();
    String id;
    String xml;
    TimeExpression refTime;

    TimeExpression min = null;
    TimeExpression max = null;

    Map<TimeExpression.TEClass, Integer> stats = new HashMap<TimeExpression.TEClass, Integer>();

    public TimexesDocument(String timexesXml) {

        this.xml = timexesXml;
        if(timexesXml != null)
        {
            Document dom;

            try {
                dom = Dom4jUtil.parse(timexesXml);

                XPath xPathRefTime = dom.createXPath("//reftime/TIMEX2");
                Element refTimeTimex2 = (Element) xPathRefTime.selectSingleNode(dom.getRootElement());
                if(refTimeTimex2 != null)
                {
                    Timex2 timex2 = new Timex2(refTimeTimex2);
                    try {
                        refTime =  new Timex2TimeExpression(timex2).getTimeExpressions().get(0);
                    } catch (TimeExpression.BadTimeExpression badTimeExpression) {
                        logger.error(badTimeExpression + ": val(" + timex2.getVal() + ") anchor_val(" + timex2.getAnchorVal() + ") anchor_dir(" + timex2.getAnchorDir() + ")",badTimeExpression);
                    }
                }
                else
                {
                    refTime = null;
                }

                XPath xPathId = dom.createXPath("//doc/@id");
                Attribute id = (Attribute) xPathId.selectSingleNode(dom);
                
                this.id = id.getValue();
//                if(id.getValue().equals("NYT_ENG_20020107.0019"))
//                    System.out.println("");
                XPath xPath = dom.createXPath("//TIMEX2");
                List<Element> timexes2 = xPath.selectNodes(dom.getRootElement());
                if(timexes2 != null && timexes2.size() > 0)
                {
                    for(Element timexElement: timexes2)
                    {
                        if(timexElement != refTimeTimex2)
                        {

                            Timex2 timex2 = new Timex2(timexElement);

                            try {
                                Timex2TimeExpression timex2TimeExpression = new Timex2TimeExpression(timex2);
                                timex2TimeExpressionsList.add(timex2TimeExpression);
                                for(TimeExpression t: timex2TimeExpression.getTimeExpressions())
                                {
                                    if(t.isMetric() && (max == null || t.getC().getTimeInMillis() > max.getC().getTimeInMillis()))
                                        max = t;
                                    if(t.isMetric() && (min == null || t.getC().getTimeInMillis() < min.getC().getTimeInMillis()))
                                        min = t;

                                    Integer stat = stats.get(t.getTeClass());
                                    if(stat == null)
                                        stats.put(t.getTeClass(),1);
                                    else
                                        stats.put(t.getTeClass(),stat+1);
                                }
                            }
                            catch (TimeExpression.BadTimeExpression badTimeExpression)
                            {
                                logger.error(badTimeExpression + ": val(" + timex2.getVal() + ") anchor_val(" + timex2.getAnchorVal() + ") anchor_dir(" + timex2.getAnchorDir() + ")",badTimeExpression);
                            }
                        }
                    }
                }
            } catch (DocumentException e) {

                logger.error(e,e);
                logger.error("TIMEXES XML was:" + timexesXml);
            }
        }

    }

    public String getXml() {
        return xml;
    }

    public TimeExpression getMin() {
        return min;
    }

    public TimeExpression getMax() {
        return max;
    }

    public List<Timex2TimeExpression> getTimex2TimeExpressions() {
        return timex2TimeExpressionsList;
    }

    public Map<TimeExpression.TEClass, Integer> getStats() {
        return stats;
    }

    public List<TimeExpression> getAllTimeExpressions()
    {
        List<TimeExpression> timeExpressions = new ArrayList<TimeExpression>();
        if(timex2TimeExpressionsList != null)
        {
            for(Timex2TimeExpression t: timex2TimeExpressionsList)
            {
                timeExpressions.addAll(t.getTimeExpressions());
            }
        }
        return timeExpressions;
    }

    public List<TimeExpression> getAllInvalidTimeExpressions()
    {
        List<TimeExpression> invalidTimeExpressions = new ArrayList<TimeExpression>();
        if(timex2TimeExpressionsList != null)
        {
            for(TimeExpression t: getAllTimeExpressions())
            {
                if(!t.isValid())
                    invalidTimeExpressions.add(t);
            }
        }
        return invalidTimeExpressions;
    }

    public List<TimeExpression> getAllValidTimeExpressions()
    {
        List<TimeExpression> validTimeExpressions = new ArrayList<TimeExpression>();
        if(timex2TimeExpressionsList != null)
        {
            for(TimeExpression t: getAllTimeExpressions())
            {
                if(t.isValid())
                    validTimeExpressions.add(t);
            }
        }
        return validTimeExpressions;
    }

    public List<TimeExpression> getAllMetricTimeExpressions()
    {
        List<TimeExpression> validTimeExpressions = new ArrayList<TimeExpression>();
        if(timex2TimeExpressionsList != null)
        {
            for(TimeExpression t: getAllTimeExpressions())
            {
                if(t.isValid() && t.getType() != TimeExpression.Type.UNKNOWN)
                    validTimeExpressions.add(t);
            }
        }
        return validTimeExpressions;
    }

    public void setTimex2TimeExpressions(List<Timex2TimeExpression> timex2TimeExpressionsList) {
        this.timex2TimeExpressionsList = timex2TimeExpressionsList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Assuming that all documents bring the REFTIME field
     * e.g.   <DOC generator="timexdoc.py">
     *           <reftime rstart="1" rend="10" val="2005-12-09">
     *             <TIMEX2 rstart="1" rend="10" val="2005-12-09">2005-12-09</TIMEX2>
     *           </reftime>
     *           ...
     *         </DOC>
     * @return a TimeExpression with the reference time os the document, usualy the creation date
     */
    public TimeExpression getRefTime() {
        return refTime;
    }

    public String toString()
    {
        return "ID: " + id + "  \n XML:\n" + xml;
    }
}
