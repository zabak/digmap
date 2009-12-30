package pt.utl.ist.lucene.utils.temporal.metrics;

import org.apache.log4j.Logger;

import java.util.*;

import pt.utl.ist.lucene.utils.temporal.TimeExpression;

/**
 * @author Jorge Machado
 * @date 11/Dez/2009
 * @time 11:01:03
 * @email machadofisher@gmail.com
 */

/**
 *     for expressions:  1990 199001 19900114 20090104
 *
 *     the tree will be  <Expression, TotalRefs>
 *                       Tree
 *                        |
 *                ----------------
 *            <1990, 1>  ,  <2009,0>
 *                |            |
 *      ----------            -------------
 *     <199001, 1>             <200901,0>
 *          |                       |
 * -----------                     ----------
 * <19900114, 1 >                   <20090104,1>
 * 
 *
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


    public List<TimeExpressionNode> getRootNodes()
    {
        List<TimeExpressionNode> nodes = new ArrayList<TimeExpressionNode>();
        for(TimeExpressionNode node: yearNodes.values())
        {
            nodes.add(node);
        }
        Collections.sort(nodes,TimeExpressionNodeComparator.getInstance());
        return nodes;
    }

    public boolean hasRootNodes()
    {
        return yearNodes.size() > 0;
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
            return o1.getTimeExpression().getNormalizedExpression().compareTo(o2.getTimeExpression().getNormalizedExpression());
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
                if(timeExpression.getType() == TimeExpression.Type.YYYY)
                    year = new TimeExpressionNode(timeExpression);
                else
                    year = new TimeExpressionNode(new TimeExpression(subExprYYYY));
            } catch (TimeExpression.BadTimeExpression e) {
                logger.error(e,e);
                return null;
            }
            yearNodes.put(subExprYYYY,year);
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
            try {
                if(timeExpression.getType() == TimeExpression.Type.YYYYMM)
                    month = new TimeExpressionNode(timeExpression);
                else
                    month = new TimeExpressionNode(new TimeExpression(subExprYYYYMM));
            } catch (TimeExpression.BadTimeExpression e) {
                logger.error(e,e);
                return null;
            }
            year.getChildrenNodes().put(subExprYYYYMM,month);
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
            try {
                if(timeExpression.getType() == TimeExpression.Type.YYYYMMDD)
                    day = new TimeExpressionNode(timeExpression);
                else
                    day = new TimeExpressionNode(new TimeExpression(subExprYYYYMMDD));
            } catch (TimeExpression.BadTimeExpression e) {
                logger.error(e,e);
                return null;
            }
            month.getChildrenNodes().put(subExprYYYYMMDD,day);
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

        public boolean hasChilds()
        {
            return childrenNodes.size() > 0;
        }

        public String toString()
        {
            return timeExpression.getNormalizedExpression() + " : childs(" + childrenNodes.size() + ") refs[" + refs + "]";
        }
    }
}
