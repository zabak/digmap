package pt.utl.ist.lucene.test.reasoningqueryprocessor;

import pt.utl.ist.lucene.treceval.geotime.queries.QueryParser;
import pt.utl.ist.lucene.treceval.geotime.queries.Query;
import org.dom4j.DocumentException;
import junit.framework.TestCase;

/**
 * @author Jorge Machado
 * @date 24/Jan/2010
 * @time 12:34:34
 * @email machadofisher@gmail.com
 */
public class QueryParserTest extends TestCase
{
    public void testParser()
    {

        String xml = "<topic id=\"GeoTime-0025\">\n" +
                "     <filterChain>\n" +
                "\t     <boolean type=\"OR\">\n" +
                "\t\t<term>\n" +
                "\t\t\t<field>place</field>\n" +
                "\t\t\t<value woeid=\"23424778\">Sri Lanka</value>\n" +
                "\t\t\t\n" +
                "\t\t</term>\n" +
                "\t\t<term>\n" +
                "\t\t\t<field>timeType</field>\n" +
                "\t\t\t<value>any</value>\n" +
                "\t\t</term>\n" +
                "\t     </boolean>\n" +
                "     </filterChain>\n" +
                "     <terms>\n" +
                "\t<desc>Sumatra earthquake tsunami hit Sri Lanka</desc>\n" +
                "\t<narr>largest earthquake recent times occurred coast Sumatra 2005 earthquake caused massive tsunami spread across Indian Ocean took tsunami reach Sri Lanka</narr>\n" +
                "     </terms>\n" +
                "     <times>\n" +
                "\t<term>2005</term>\n" +
                "     </times>\n" +
                "     <places>\n" +
                "        <term woeid=\"55959675\">Indian Ocean</term>\n" +
                "\t<term woeid=\"23424778\">Sri Lanka</term>\n" +
                "     </places>\n" +
                "</topic>";

        try
        {
            Query q = new QueryParser(xml).getQuery();
            assertEquals(q.getFilterChain().getBooleanClause().getLogicValue(), Query.FilterChain.BooleanClause.LogicValue.OR);
            assertEquals(q.getId(),"GeoTime-0025");
            assertTrue(q.getFilterChain().getBooleanClause().getTerms().size() == 2);
            assertEquals(((Query.FilterChain.BooleanClause.Term)q.getFilterChain().getBooleanClause().getTerms().get(0)).getField(),"place");
            assertEquals(((Query.FilterChain.BooleanClause.Term)q.getFilterChain().getBooleanClause().getTerms().get(1)).getField(),"timeType");
            assertEquals(((Query.FilterChain.BooleanClause.Term)q.getFilterChain().getBooleanClause().getTerms().get(0)).getWoeid().get(0),"23424778");
            assertEquals(((Query.FilterChain.BooleanClause.Term)q.getFilterChain().getBooleanClause().getTerms().get(1)).getWoeid().size(),0);
            assertEquals(((Query.FilterChain.BooleanClause.Term)q.getFilterChain().getBooleanClause().getTerms().get(0)).getValue(),"Sri Lanka");
            assertEquals(((Query.FilterChain.BooleanClause.Term)q.getFilterChain().getBooleanClause().getTerms().get(1)).getValue(),"any");

            assertEquals(q.getTerms().getDesc(),"Sumatra earthquake tsunami hit Sri Lanka");
            assertEquals(q.getTerms().getNarr(),"largest earthquake recent times occurred coast Sumatra 2005 earthquake caused massive tsunami spread across Indian Ocean took tsunami reach Sri Lanka");
            assertEquals(q.getPlaces().getTerms().size(),2);
            assertEquals(q.getPlaces().getTerms().get(0).getPlace(),"Indian Ocean");
            assertEquals(q.getPlaces().getTerms().get(1).getPlace(),"Sri Lanka");
            assertEquals(q.getPlaces().getTerms().get(0).getWoeid().get(0),"55959675");
            assertEquals(q.getPlaces().getTerms().get(1).getWoeid().get(0),"23424778");
            assertEquals(q.getTimes().getTerms().size(),1);
            assertEquals(q.getTimes().getTerms().get(0).getTime(),"2005");

        }
        catch (DocumentException e)
        {
            fail(e.toString());
            e.printStackTrace();
        }

    }
}
