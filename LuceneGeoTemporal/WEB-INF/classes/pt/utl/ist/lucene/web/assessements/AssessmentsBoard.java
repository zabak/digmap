package pt.utl.ist.lucene.web.assessements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge
 * @date 18/Fev/2010
 * @time 10:01:46
 * @mail machadofisher@gmail.com
 * @copyright ModiPlace 2010
 * @site www.modiplace.pt
 */
public class AssessmentsBoard
{
    //Topic --> Relevance --> Assessed Docs
    Map<String,Map<String,Integer>> board = new HashMap<String,Map<String,Integer>>();
    Map<String,Integer> totals = new HashMap<String,Integer>();


    public void addTopic(String topic)
    {
        if(board.get(topic) == null)
            board.put(topic,new HashMap<String,Integer>());
    }



    public void addRelevance(String topic,String relevance, Integer count)
    {
        addTopic(topic);
        board.get(topic).put(relevance,count);
    }

    public void addTotals(String topic, Integer count)
    {
        totals.put(topic,count);
    }

    public int getCount(String topic, String relevance)
    {
        Map<String,Integer> map = board.get(topic);
        if(map == null)
            return 0;
        Integer count = map.get(relevance);
        if(count == null) return 0;
        return count;
    }

    public int getTotals(String topic)
    {
        return totals.get(topic);
    }

    public int getTotalsAssessed(String topic)
    {
        Map<String,Integer> topicMap = board.get(topic);
        if(topicMap == null)
            return 0;
        int count = 0;
        for(Map.Entry<String,Integer>  entry: topicMap.entrySet())
        {
            if(!entry.getKey().equals("not-defined"))
            {
                Integer countTopic = entry.getValue();
                if(countTopic != null)
                    count += countTopic;
            }
        }
        return count;
    }





}
