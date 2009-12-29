package pt.utl.ist.lucene.utils.temporal.tests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.temporal.TimextagDocumentSpliter;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 18:12:17
 * @email machadofisher@gmail.com
 */
public class DocmentSpliterTest extends TestCase
{
    public void testExample1()
    {
         String doc = "<DOC id=\"NYT_ENG_20051209.0416\" type=\"story\" >\n" +
                "<DATE_TIME>2005-12-09</DATE_TIME>\n" +
                "<HEADLINE>\n" +
                "WHAT'S OFFLINE\n" +
                "</HEADLINE>\n" +
                "<TEXT>\n" +
                "<P>\n" +
                "ROOT FOR $5-A-GALLON GAS\n" +
                "</P>\n" +
                "<P>\n" +
                "Gasoline prices stubbornly hover near $2 a gallon, but $5 would\n" +
                "be even better, writes Spencer Reiss in this month's Wired.\n" +
                "The higher the price, he reasons, the more incentive there\n" +
                "is to find alternatives.\n" +
                "</P>\n" +
                "<P>\n" +
                "\"For anyone with a fresh idea, expensive oil is as good\n" +
                "as a subsidy -- with no political strings attached,\" he\n" +
                "writes. \"Indeed, every extra penny you pay at the pump\n" +
                "is an incentive for some aspiring energy mogul to find\n" +
                "another fuel.\"\n" +
                "</P>\n" +
                "<P>\n" +
                "And low gasoline prices stand in the way of that, he\n" +
                "argues in an article that squarely takes on the conventional\n" +
                "wisdom that we are doomed to run out of gasoline\n" +
                "-- or at the very least destined to pay exorbitant\n" +
                "prices per gallon, because of a finite supply of petroleum\n" +
                "and rising demand in other countries, particularly Brazil, Russia, India\n" +
                "and China.\n" +
                "</P>\n" +
                "<P>\n" +
                "\"For the better part of a century, cheap oil has fatally\n" +
                "undercut all comers, not to mention smothering high-minded campaigns for\n" +
                "conservation, increased efficiency and energy independence,\" he writes.\n" +
                "</P>\n" +
                "<P>\n" +
                "Higher prices suddenly make all those ideas more attractive and give\n" +
                "energy companies and entrepreneurs a real reason to look at\n" +
                "things like converting natural gas into diesel fuels, or seeing\n" +
                "if biodiesel (fuels using vegetable oils) and ethanol are indeed\n" +
                "feasible.\n" +
                "</P>\n" +
                "<P>\n" +
                "And presumably once serious efforts devoted to finding alternatives are under\n" +
                "way, the price of those new fuels will drop thanks\n" +
                "to breakthroughs and economies of scale.\n" +
                "</P>\n" +
                "<P>\n" +
                "BUT WAIT, THERE'S MORE\n" +
                "</P>\n" +
                "<P>\n" +
                "Be they for spray-on hair, his rotisserie, the Pocket Fisherman or\n" +
                "some other product we can definitely live without, Ron Popeil's\n" +
                "infomercials are deliberately cheesy -- and extremely effective.\n" +
                "</P>\n" +
                "<P>\n" +
                "\"I've been on TV every day for the past seven years;\n" +
                "in those years, I've spent more than $300 million on\n" +
                "air time for my rotisserie alone,\" he told Andrew Vontz\n" +
                "in an enlightening question-and-answer interview in the December issue of\n" +
                "FHM.\n" +
                "</P>\n" +
                "<P>\n" +
                "Following are excerpts:\n" +
                "</P>\n" +
                "<P>\n" +
                "\"My best sales hour was probably $500,000 on QVC. The numbers\n" +
                "for selling retail on TV are much larger than wholesale.\n" +
                "Sell 7 million rotisseries with a retail price of $200\n" +
                "and you've got a nice hunk of change.\"\n" +
                "</P>\n" +
                "<P>\n" +
                "\"The infomercials have an entertainment value to them. Nielsen did a\n" +
                "study for CNBC. On a Sunday at 4 p.m., CNBC\n" +
                "replaced their usual programming with my hair-spray infomercial and the\n" +
                "audience doubled.\"\n" +
                "</P>\n" +
                "<P>\n" +
                "\"My bank account has more than $100 million in it, and\n" +
                "the taxes are paid.\"\n" +
                "</P>\n" +
                "<P>\n" +
                "ROCKET SCIENTISTS\n" +
                "</P>\n" +
                "<P>\n" +
                "Wall Street's \"rocket scientists\" are cool customers, according to Smart Money.\n" +
                "After all, computers do the hard part.\n" +
                "</P>\n" +
                "<P>\n" +
                "Rocket science is Wall Street's nickname for a quantitative investment approach\n" +
                "that creates mathematical formulas to determine when to buy and\n" +
                "sell stocks. The algorithms are fed into computers, which do\n" +
                "the actual trading. \"Emotion is removed from the equation,\" Nicole\n" +
                "Bullock writes. \"Fear and greed don't compute.\"\n" +
                "</P>\n" +
                "<P>\n" +
                "And unlike flesh-and-blood traders, computers neither fall in love with a\n" +
                "stock, which can cause an investor to hold onto it\n" +
                "too long, or carry grudges about disappointments, which could keep\n" +
                "them from buying once the company turns around.\n" +
                "</P>\n" +
                "<P>\n" +
                "Better yet, as one of the \"quants\" put it, \"we can\n" +
                "do the stock picking without using our brains for a\n" +
                "day or a year.\"\n" +
                "</P>\n" +
                "<P>\n" +
                "The reason quantitative trading works is that the algorithms are created\n" +
                "to spot and exploit inefficiencies in the market. Of course,\n" +
                "as buying and selling identify the imperfections, the market adjusts\n" +
                "and a new model is needed.\n" +
                "</P>\n" +
                "<P>\n" +
                "The magazine \"surveyed the best quant shops to find out where\n" +
                "the dials are pointing\" and presents five stocks \"the machines\n" +
                "say are poised to outperform.\" The list is AstraZeneca, Goodyear\n" +
                "Tire &amp; Rubber, Chevron, Microsoft and Taiwan Semiconductor.\n" +
                "</P>\n" +
                "<P>\n" +
                "FINAL TAKE\n" +
                "</P>\n" +
                "<P>\n" +
                "Being a millionaire isn't what it used to be thanks in\n" +
                "part to rising real estate prices and retirement accounts. \"The\n" +
                "number of households with a net worth of more than\n" +
                "$1 million rose by 8 percent in 2005 to 8.9\n" +
                "million,\" Kiplinger's reports. \"The secret: sticking to an investment plan.\"\n" +
                "</P>\n" +
                "</TEXT>\n" +
                "</DOC>";

         TimextagDocumentSpliter documentSpliter = new TimextagDocumentSpliter(doc,"<DOC generator=\"timexdoc.py\">\n" +
                "<reftime rstart=\"1\" rend=\"10\" val=\"2005-12-09\">\n" +
                "<TIMEX2 rstart=\"1\" rend=\"10\" val=\"2005-12-09\">2005-12-09</TIMEX2>\n" +
                "</reftime>\n" +
                "<TEXT rstart=\"29\" rend=\"4117\">\n" +
                "<TIMEX2 set=\"\" rend=\"171\" val=\"2005-12\" tmxclass=\"point\" rstart=\"162\" dirclass=\"same\" parsenode=\".1 p51\" prenorm=\"|amb|M|_\">this month</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"898\" val=\"\" tmxclass=\"point\" rstart=\"871\" dirclass=\"same\" parsenode=\".6 p5\" prenorm=\"\">the better part of a century</TIMEX2>\n" +
                "<TIMEX2 set=\"YES\" rend=\"1760\" val=\"XXXX-XX-XX\" tmxclass=\"recur\" rstart=\"1752\" parsenode=\".10 p13\" prenorm=\"XXXX-XX-XX\">every day</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"1785\" val=\"P7Y\" anchor_dir=\"ENDING\" tmxclass=\"duration\" rstart=\"1766\" anchor_val=\"2005\" parsenode=\".10 p18\" prenorm=\"P7Y\">the past seven years</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"1801\" val=\"\" tmxclass=\"duration\" rstart=\"1791\" parsenode=\".10 p26\" prenorm=\"\">those years</TIMEX2>\n" +
                "\n" +
                "<TIMEX2 set=\"\" rend=\"2022\" val=\"\" tmxclass=\"point\" rstart=\"2005\" dirclass=\"same\" parsenode=\".11 p11\" prenorm=\"\">My best sales hour</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"2314\" val=\"2005-12-04\" tmxclass=\"point\" rstart=\"2309\" dirclass=\"before\" parsenode=\".16 w2\" prenorm=\"|dex|W|XXXX-WXX-7\">Sunday</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"2324\" val=\"2005-12-08T16:00\" tmxclass=\"point\" rstart=\"2319\" dirclass=\"before\" parsenode=\".16 p9\" prenorm=\"|dex|D|T16:00\">4 p.m.</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"3313\" val=\"P1D\" anchor_dir=\"BEFORE\" tmxclass=\"duration\" rstart=\"3309\" anchor_val=\"2005-12-09\" parsenode=\".25 p46\" prenorm=\"P1D\">a\n" +
                "day</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"4031\" val=\"2005\" tmxclass=\"point\" rstart=\"4028\" dirclass=\"same\" parsenode=\".31 w19\" prenorm=\"|fq|_2005\">2005</TIMEX2>\n" +
                "</TEXT>\n" +
                "</DOC>");

        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(0).getNormalizedExpression(),"UNKNOWN");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(1).getNormalizedExpression(),"1998");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(2).getNormalizedExpression(),"1999");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(3).getNormalizedExpression(),"2000");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(4).getNormalizedExpression(),"2001");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(5).getNormalizedExpression(),"2002");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(6).getNormalizedExpression(),"2003");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(7).getNormalizedExpression(),"2004");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(8).getNormalizedExpression(),"2005");
        assertEquals(documentSpliter.getSentences().get(8).getAllTimeExpressions().get(9).getNormalizedExpression(),"UNKNOWN");


        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(0).getNormalizedExpression(),"20051209");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(1).getNormalizedExpression(),"200512");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(2).getNormalizedExpression(),"UNKNOWN");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(3).getNormalizedExpression(),"UNKNOWN");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(4).getNormalizedExpression(),"1998");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(5).getNormalizedExpression(),"1999");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(6).getNormalizedExpression(),"2000");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(7).getNormalizedExpression(),"2001");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(8).getNormalizedExpression(),"2002");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(9).getNormalizedExpression(),"2003");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(10).getNormalizedExpression(),"2004");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(11).getNormalizedExpression(),"2005");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(12).getNormalizedExpression(),"UNKNOWN");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(13).getNormalizedExpression(),"UNKNOWN");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(14).getNormalizedExpression(),"20051204");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(15).getNormalizedExpression(),"20051208");
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(16).getNormalizedExpression(),"20051209");  //P1D but anchor_dir = BEFORE is unkown
        assertEquals(documentSpliter.getAllNormalizedTimeExpressions().get(17).getNormalizedExpression(),"2005");


    }
}
