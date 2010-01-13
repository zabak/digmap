package pt.utl.ist.lucene.utils.temporal.tests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.temporal.metrics.TemporalMetrics;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

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
            assertEquals(temporalMetrics.getTemporalCentroideTimeExpression().getNormalizedExpression(),"19900531");
            assertEquals(temporalMetrics.getIntervalCentroideTimeExpression().getNormalizedExpression(),"19900221");
            
            temporalMetrics = new TemporalMetrics("1990 199001 199001 199002 19900101 1988");
            assertEquals(temporalMetrics.getTemporalCentroideTimeExpression().getNormalizedExpression(),"19890724");
            assertEquals(temporalMetrics.getIntervalCentroideTimeExpression().getNormalizedExpression(),"19891113");

            temporalMetrics = new TemporalMetrics("1990 199001 199001 199002 19900101 1988");
            assertEquals(temporalMetrics.getTemporalCentroideTimeExpression().getNormalizedExpression(),"19890724");
            assertEquals(temporalMetrics.getLeftLimitsCentroideTimeExpression().getNormalizedExpression(),"19890906");
        }
        catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            fail("Expressions were correct and parse fail");
        }
    }
}
