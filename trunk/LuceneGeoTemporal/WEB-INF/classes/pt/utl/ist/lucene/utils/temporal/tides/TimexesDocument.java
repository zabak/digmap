package pt.utl.ist.lucene.utils.temporal.tides;

import org.apache.log4j.Logger;
import org.dom4j.*;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.util.ArrayList;
import java.util.List;

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


    public TimexesDocument(String timexesXml) {

        this.xml = timexesXml;
        if(timexesXml != null)
        {
            Document dom;

            try {
                dom = Dom4jUtil.parse(timexesXml);

                XPath xPathId = dom.createXPath("//doc/@id");
                Attribute id = (Attribute) xPathId.selectSingleNode(dom);
                this.id = id.getValue();
                XPath xPath = dom.createXPath("//TIMEX2");
                List<Element> timexes2 = xPath.selectNodes(dom.getRootElement());
                if(timexes2 != null && timexes2.size() > 0)
                {
                    boolean first = true;
                    for(Element timexElement: timexes2)
                    {
                        Timex2 timex2 = new Timex2(timexElement);

                        try {
                            Timex2TimeExpression timex2TimeExpression = new Timex2TimeExpression(timex2);
                            if(first && timex2TimeExpression.getTimeExpressions().size() > 0)
                            {
                                refTime = timex2TimeExpression.getTimeExpressions().get(0); //The first off all timeExpressions is the REFTIME of the document
                                first = false;
                            }
                            timex2TimeExpressionsList.add(timex2TimeExpression);
                        } catch (TimeExpression.BadTimeExpression badTimeExpression)
                        {
                            first = false;
                            logger.error(badTimeExpression + ": val(" + timex2.getVal() + ") anchor_val(" + timex2.getAnchorVal() + ") anchor_dir(" + timex2.getAnchorDir() + ")",badTimeExpression);
                        }
                    }
                }
            } catch (DocumentException e) {

                logger.error(e,e);
                logger.error("TIMEXES XML was:" + timexesXml);
            }
        }

    }

    public List<Timex2TimeExpression> getTimex2TimeExpressions() {
        return timex2TimeExpressionsList;
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