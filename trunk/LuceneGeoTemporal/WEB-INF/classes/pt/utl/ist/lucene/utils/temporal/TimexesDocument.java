package pt.utl.ist.lucene.utils.temporal;

import org.dom4j.*;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

import pt.utl.ist.lucene.utils.Dom4jUtil;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 19:52:33
 * @email machadofisher@gmail.com
 */
public class TimexesDocument
{
    private static final Logger logger = Logger.getLogger(TimexesDocument.class);

    List<Timex2TimeExpression.Timex2TimeExpressionsSet> timex2TimeExpressionsSets = new ArrayList<Timex2TimeExpression.Timex2TimeExpressionsSet>();
    String id;
    String xml;


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
                    for(Element timexElement: timexes2)
                    {
                        Timex2 timex2 = new Timex2(timexElement);
                        try {
                            Timex2TimeExpression timex2TimeExpression = new Timex2TimeExpression(timex2);
                            Timex2TimeExpression.Timex2TimeExpressionsSet timex2TimeExpressionsSet = new Timex2TimeExpression.Timex2TimeExpressionsSet(timex2.getRstart(),timex2.getRend(),timex2,timex2TimeExpression.getTimeExpressions());
                            timex2TimeExpressionsSets.add(timex2TimeExpressionsSet);
                        } catch (TimeExpression.BadTimeExpression badTimeExpression)
                        {
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

    public List<Timex2TimeExpression.Timex2TimeExpressionsSet> getTimex2TimeExpressionsSets() {
        return timex2TimeExpressionsSets;
    }

    public void setTimex2TimeExpressionsSets(List<Timex2TimeExpression.Timex2TimeExpressionsSet> timex2TimeExpressionsSets) {
        this.timex2TimeExpressionsSets = timex2TimeExpressionsSets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString()
    {
        return "ID: " + id + "  \n XML:\n" + xml;
    }
}
