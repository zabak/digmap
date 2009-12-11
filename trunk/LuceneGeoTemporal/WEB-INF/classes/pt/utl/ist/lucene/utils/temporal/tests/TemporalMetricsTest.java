package pt.utl.ist.lucene.utils.temporal.tests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.temporal.TemporalMetrics;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.util.List;

/**
 * @author Jorge Machado
 * @date 11/Dez/2009
 * @time 19:00:30
 * @email machadofisher@gmail.com
 */
public class TemporalMetricsTest extends TestCase
{
    public void testCentroideY()
    {
        try
        {
            TemporalMetrics temporalMetrics = new TemporalMetrics("1990 199001 199001 199002 19900101");
            assertTrue(temporalMetrics.getNumberRefsCentroide()-1.249315< 0.001);

            temporalMetrics = new TemporalMetrics("1990 199001 199001 199002 19900101 1980");
            assertTrue(temporalMetrics.getNumberRefsCentroide()-1.1246575< 0.001);
        }
        catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            fail("Expressions were correct and parse fail");
        }
    }

    public void testTemporalCentroide()
    {
        try
        {
            TemporalMetrics temporalMetrics = new TemporalMetrics("1990 199001 199001 199002 19900101");
            assertEquals(temporalMetrics.getTemporalCentroideTimeExpression().getExpression(),"19900531");
            assertEquals(temporalMetrics.getTemporalIntervalPointsCentroideTimeExpression().getExpression(),"19900221");
            
            temporalMetrics = new TemporalMetrics("1990 199001 199001 199002 19900101 1988");
            assertEquals(temporalMetrics.getTemporalCentroideTimeExpression().getExpression(),"19890724");
            assertEquals(temporalMetrics.getTemporalIntervalPointsCentroideTimeExpression().getExpression(),"19891113");
        }
        catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            fail("Expressions were correct and parse fail");
        }
    }
}
