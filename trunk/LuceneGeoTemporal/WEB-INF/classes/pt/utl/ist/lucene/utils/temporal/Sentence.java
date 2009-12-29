package pt.utl.ist.lucene.utils.temporal;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 16:57:08
 * @email machadofisher@gmail.com
 */
public class Sentence {

    
    int index;
    List<Timex2TimeExpression.Timex2TimeExpressionsSet> timexes;
    String phrase;
    int startOffset;
    int endOffset;

    public Sentence(int index, String phrase, int startOffset, int endOffset)
    {
        this.timexes = new ArrayList<Timex2TimeExpression.Timex2TimeExpressionsSet>();
        this.phrase = phrase;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.index = index;
    }
    public Sentence(int index, List<Timex2TimeExpression.Timex2TimeExpressionsSet> timeExpressions, String phrase, int startOffset, int endOffset)
    {
        if(timeExpressions == null)
            this.timexes = new ArrayList<Timex2TimeExpression.Timex2TimeExpressionsSet>();
        else
            this.timexes = timeExpressions;
        this.phrase = phrase;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.index = index;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public List<Timex2TimeExpression.Timex2TimeExpressionsSet> getTimexes() {
        return timexes;
    }

    public List<TimeExpression> getAllTimeExpressions()
    {
        List<TimeExpression> allTimeExpressions = new ArrayList<TimeExpression>();
        for(Timex2TimeExpression.Timex2TimeExpressionsSet timex: timexes)
        {
            allTimeExpressions.addAll(timex.getExpressions());
        }
        return allTimeExpressions;
    }

    public List<String> getAllNLExpressions()
    {
        List<String> allTimeExpressions = new ArrayList<String>();
        for(Timex2TimeExpression.Timex2TimeExpressionsSet timex: timexes)
        {
            allTimeExpressions.add(timex.getTimex2().getText());
        }
        return allTimeExpressions;
    }

    public void setTimeExpressions(List<Timex2TimeExpression.Timex2TimeExpressionsSet> timexes) {
        this.timexes = timexes;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String toString()
    {
        String txt = "SENTENCE " + index + " (" + startOffset + "," + endOffset + "): [" + phrase + "]";
        txt+="\nTimeExpressions [";
        if(timexes!=null)
        {
            String catToken = "";
            for(Timex2TimeExpression.Timex2TimeExpressionsSet timex : timexes)
            {

                txt+= catToken + timex.getTimex2().getText() + " : {";
                catToken = ",";
                String catToken2 = "";
                for(TimeExpression timeExpression: timex.getExpressions())
                {
                    txt+=catToken2 + timeExpression.getNormalizedExpression();
                    catToken2 = ",";
                }
                txt +="}";
            }
        }
        txt+="\n]";
        return txt;
    }
}
