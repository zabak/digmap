package pt.utl.ist.lucene.utils.temporal;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Jorge Machado
 * @date 11/Dez/2009
 * @time 11:01:03
 * @email machadofisher@gmail.com
 */
public class TimeExpressionsTree
{

    private static final Logger logger = Logger.getLogger(TimeExpressionsTree.class);
    private Map<String,TimeExpressionNode> yearNodes = new HashMap<String,TimeExpressionNode>();

    public void addExpression(TimeExpression timeExpression)
    {
        if(timeExpression.getType() == TimeExpression.Type.YYYY)
        {
            TimeExpressionNode year = loadYear(timeExpression);
            year.incRefs();
        }
        else if(timeExpression.getType() == TimeExpression.Type.YYYYMM)
        {
            TimeExpressionNode month = loadMonth(timeExpression);
            month.incRefs();
        }
        else if(timeExpression.getType() == TimeExpression.Type.YYYYMMDD)
        {
            TimeExpressionNode day = loadDay(timeExpression);
            day.incRefs();
        }
    }


    List<TimeExpressionNode> getRootNodes()
    {
        List<TimeExpressionNode> nodes = new ArrayList<TimeExpressionNode>();
        for(TimeExpressionNode node: yearNodes.values())
        {
            nodes.add(node);
        }
        Collections.sort(nodes,TimeExpressionNodeComparator.getInstance());
        return nodes;
    }

    private static class TimeExpressionNodeComparator implements Comparator<TimeExpressionNode>
    {
        static TimeExpressionNodeComparator instance = new TimeExpressionNodeComparator();

        public static TimeExpressionNodeComparator getInstance()
        {
            return instance;
        }

        private TimeExpressionNodeComparator(){}

        public int compare(TimeExpressionNode o1, TimeExpressionNode o2) {
            return o1.getTimeExpression().getExpression().compareTo(o2.getTimeExpression().getExpression());
        }
    }


    /**
     * Load a Month creating it if not exist
     * @param timeExpression TimeExpression
     * @return TimeExpressionNode
     */
    private TimeExpressionNode loadYear(TimeExpression timeExpression)
    {
        String subExprYYYY = timeExpression.getSubExpressionYYYY();
        TimeExpressionNode year = yearNodes.get(subExprYYYY);
        if(year == null)
        {
            try {
                year = new TimeExpressionNode(new TimeExpression(subExprYYYY));
            } catch (TimeExpression.BadTimeExpression e) {
                logger.error(e,e);
                return year;
            }
            yearNodes.put(timeExpression.getExpression(),year);
        }
        return year;
    }

    /**
     * Load a Month creating it if not exist
     * @param timeExpression TimeExpression
     * @return TimeExpressionNode
     */
    private TimeExpressionNode loadMonth(TimeExpression timeExpression)
    {
        TimeExpressionNode year = loadYear(timeExpression);
        String subExprYYYYMM = timeExpression.getSubExpressionYYYYMM();
        TimeExpressionNode month = year.getChildrenNodes().get(subExprYYYYMM);
        if(month == null)
        {
            year = new TimeExpressionNode(timeExpression);
            year.getChildrenNodes().put(timeExpression.getExpression(),month);
        }
        return month;
    }

    /**
     * Load a Day creating it if not exist
     * @param timeExpression TimeExpression
     * @return TimeExpressionNode
     */
    private TimeExpressionNode loadDay(TimeExpression timeExpression)
    {
        TimeExpressionNode month = loadMonth(timeExpression);
        String subExprYYYYMMDD = timeExpression.getSubExpressionYYYYMMDD();
        TimeExpressionNode day = month.getChildrenNodes().get(subExprYYYYMMDD);
        if(day == null)
        {
            day = new TimeExpressionNode(timeExpression);
            month.getChildrenNodes().put(timeExpression.getExpression(),day);
        }
        return day;
    }



    public class TimeExpressionNode
    {
        private Map<String,TimeExpressionNode> childrenNodes = new HashMap<String,TimeExpressionNode>();
        TimeExpression timeExpression;
        private int refs = 0;


        public TimeExpressionNode(TimeExpression timeExpression) {
            this.timeExpression = timeExpression;
        }

        public TimeExpression getTimeExpression() {
            return timeExpression;
        }

        public int getRefs() {
            return refs;
        }


        private void incRefs()
        {
            refs++;
        }

        private Map<String, TimeExpressionNode> getChildrenNodes() {
            return childrenNodes;
        }

        public List<TimeExpressionNode> getChilds()
        {
            List<TimeExpressionNode> nodes = new ArrayList<TimeExpressionNode>();
            for(TimeExpressionNode node: childrenNodes.values())
            {
                nodes.add(node);
            }
            Collections.sort(nodes,TimeExpressionNodeComparator.getInstance());
            return nodes;
        }
    }
}
