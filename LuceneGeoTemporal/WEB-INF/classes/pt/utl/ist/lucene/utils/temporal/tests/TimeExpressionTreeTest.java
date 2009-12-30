package pt.utl.ist.lucene.utils.temporal.tests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.temporal.metrics.TimeExpressionsTree;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.util.List;

/**
 * @author Jorge Machado
 * @date 11/Dez/2009
 * @time 15:49:12
 * @email machadofisher@gmail.com
 */
public class TimeExpressionTreeTest extends TestCase
{
    public void testTree()
    {

        try
        {
            TimeExpressionsTree tree = new TimeExpressionsTree();
            tree.addExpression(new TimeExpression("1990"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("199002"));
            tree.addExpression(new TimeExpression("19900101"));
            validateTree(tree);

            tree = new TimeExpressionsTree();
            tree.addExpression(new TimeExpression("19900101"));
            tree.addExpression(new TimeExpression("1990"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("199002"));
            validateTree(tree);

            tree = new TimeExpressionsTree();
            tree.addExpression(new TimeExpression("199002"));
            tree.addExpression(new TimeExpression("19900101"));
            tree.addExpression(new TimeExpression("1990"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("199001"));
            validateTree(tree);

            tree = new TimeExpressionsTree();
            tree.addExpression(new TimeExpression("199002"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("1990"));
            tree.addExpression(new TimeExpression("19900101"));
            tree.addExpression(new TimeExpression("199001"));
            validateTree(tree);

            tree = new TimeExpressionsTree();
            tree.addExpression(new TimeExpression("199002"));
            tree.addExpression(new TimeExpression("1990"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("19900101"));
            tree.addExpression(new TimeExpression("199001"));
            validateTree(tree);


            tree = new TimeExpressionsTree();
            tree.addExpression(new TimeExpression("199002"));
            tree.addExpression(new TimeExpression("1990"));
            tree.addExpression(new TimeExpression("20090401"));
            tree.addExpression(new TimeExpression("199001"));
            tree.addExpression(new TimeExpression("19900101"));
            tree.addExpression(new TimeExpression("199001"));
            validateTree2(tree);

        } catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            badTimeExpression.printStackTrace(); 
        }
    }

    private void validateTree(TimeExpressionsTree tree)
    {
        List<TimeExpressionsTree.TimeExpressionNode> years = tree.getRootNodes();
        assertTrue(years.size() == 1);
        assertTrue(years.get(0).getTimeExpression().getNormalizedExpression().equals("1990"));
        assertTrue(years.get(0).getRefs() ==1 );
        assertTrue(years.get(0).getChilds().size() == 2);
        assertTrue(years.get(0).getChilds().get(0).getTimeExpression().getNormalizedExpression().equals("199001"));
        assertTrue(years.get(0).getChilds().get(1).getTimeExpression().getNormalizedExpression().equals("199002"));
        assertTrue(years.get(0).getChilds().get(0).getRefs() == 2);
        assertTrue(years.get(0).getChilds().get(1).getRefs() == 1);
        assertTrue(years.get(0).getChilds().get(0).getChilds().size() == 1);
        assertTrue(years.get(0).getChilds().get(0).getChilds().get(0).getTimeExpression().getNormalizedExpression().equals("19900101"));
        assertTrue(years.get(0).getChilds().get(0).getChilds().get(0).getRefs() == 1);

    }
    private void validateTree2(TimeExpressionsTree tree)
    {
        List<TimeExpressionsTree.TimeExpressionNode> years = tree.getRootNodes();
        assertTrue(years.size() == 2);
        assertTrue(years.get(0).getTimeExpression().getNormalizedExpression().equals("1990"));
        assertTrue(years.get(1).getTimeExpression().getNormalizedExpression().equals("2009"));
        assertTrue(years.get(1).getRefs() == 0);
        assertTrue(years.get(1).getChilds().size() == 1);
        assertTrue(years.get(1).getChilds().get(0).getTimeExpression().getNormalizedExpression().equals("200904"));
        assertTrue(years.get(1).getChilds().get(0).getRefs() == 0);
        assertTrue(years.get(1).getChilds().get(0).getChilds().size() == 1);
        assertTrue(years.get(1).getChilds().get(0).getChilds().get(0).getTimeExpression().getNormalizedExpression().equals("20090401"));
        assertTrue(years.get(1).getChilds().get(0).getChilds().get(0).getRefs() == 1);

        assertTrue(years.get(0).getRefs() ==1 );
        assertTrue(years.get(0).getChilds().size() == 2);
        assertTrue(years.get(0).getChilds().get(0).getTimeExpression().getNormalizedExpression().equals("199001"));
        assertTrue(years.get(0).getChilds().get(1).getTimeExpression().getNormalizedExpression().equals("199002"));
        assertTrue(years.get(0).getChilds().get(0).getRefs() == 2);
        assertTrue(years.get(0).getChilds().get(1).getRefs() == 1);
        assertTrue(years.get(0).getChilds().get(0).getChilds().size() == 1);
        assertTrue(years.get(0).getChilds().get(0).getChilds().get(0).getTimeExpression().getNormalizedExpression().equals("19900101"));
        assertTrue(years.get(0).getChilds().get(0).getChilds().get(0).getRefs() == 1);
    }
}
