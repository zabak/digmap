package jomm.googleclient;

import jomm.utils.HttpUtils;
import org.apache.commons.httpclient.util.URIUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 7/Jul/2011
 * Time: 12:08:57
 * To change this template use File | Settings | File Templates.
 */
public class GoogleClient {


    public static final String START_TITLE = "<h3 class=\"r\">";
    public static final String END_TITLE = "</h3>";

    public static final String START_SNIPPET = "<div class=\"s\">";
    public static final String END_SNIPPET = "</div>";

    public static long lastCall = System.currentTimeMillis();
    public static final long MAX_INTERVAL_CALLS = 5000;


    public static List<GoogleResult> getResults(String q) throws Throwable, MalformedURLException
    {

        List<GoogleResult> results = new ArrayList<GoogleResult>();
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("User-Agent","lgte");
        String response = HttpUtils.doGetWithBasicAuthentication(new URL("http://www.google.pt/search?ie=utf-8&oe=utf-8&aq=t&client=lgte&q=" + URIUtil.encodeQuery(q)),null,null,2000, headers);
        long passed = System.currentTimeMillis() - lastCall;
        if(System.currentTimeMillis() - lastCall < MAX_INTERVAL_CALLS)
        {
            long willWait = MAX_INTERVAL_CALLS-passed;
            System.out.println("Waiting to call google: " + willWait + " ms" );
            Thread.sleep(willWait);
        }
        long willWait = (long) (Math.random()*MAX_INTERVAL_CALLS);
        System.out.println("Waiting random to call google: " + willWait + " ms" );
        Thread.sleep(willWait);

        System.out.println("Continuing");

        int startResult = 0;
        while((startResult = response.indexOf(START_TITLE,startResult))>=0)
        {
            try{
                int startHref = response.indexOf("<a ",startResult);
                startHref = response.indexOf("href=\"",startHref);
                int endHref = response.indexOf("\"",startHref + "href=\"".length());
                String hrefValue = response.substring(startHref + "href=\"".length(),endHref);

                int endTitle;
                String title = null;
                String snippet = null;
                try {
                    endTitle = response.indexOf(END_TITLE,startResult);
                    title = response.substring(startResult,endTitle).replaceAll("<[^>]+>","");
                    try{
                        int startSnippet = response.indexOf(START_SNIPPET,endTitle);
                        int endSnippet = response.indexOf(END_SNIPPET,startSnippet);
                        snippet = response.substring(startSnippet,endSnippet).replaceAll("<[^>]+>","");
                    }
                    catch(Throwable e){System.out.println("Cannot read snippet in google client"+ e.toString());}
                }
                catch(Throwable e){System.out.println("Cannot read title in google client"+ e.toString());}

                GoogleResult gr = new GoogleResult(title,hrefValue,snippet);
                results.add(gr);
            }catch(Throwable e)
            {
                System.out.println("Cannot getResult from google:" + e.toString());
            }
            startResult+=START_TITLE.length() + 1;
        }
        lastCall =  System.currentTimeMillis();
        return results;
    }

    public static void main(String[] args) throws Throwable {
        List<GoogleResult> results = getResults("last figlht concorde where did it land");
        for (GoogleResult result : results) {
            System.out.println(result);
        }
    }
}
