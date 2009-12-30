package pt.utl.ist.lucene.utils.temporal;

import pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression;
import pt.utl.ist.lucene.utils.nlp.Sentence;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 16:57:08
 * @email machadofisher@gmail.com
 */
public class TemporalSentence extends Sentence
{
    
    List<Timex2TimeExpression> timexes;


    public TemporalSentence() {
         this.timexes = new ArrayList<Timex2TimeExpression>();
    }

    public TemporalSentence(int index, String phrase, int startOffset, int endOffset)
    {
        super(index,phrase,startOffset,endOffset);
        this.timexes = new ArrayList<Timex2TimeExpression>();

    }

    public TemporalSentence(int index, List<Timex2TimeExpression> timeExpressions, String phrase, int startOffset, int endOffset)
    {
        super(index,phrase,startOffset,endOffset);
        if(timeExpressions == null)
            this.timexes = new ArrayList<Timex2TimeExpression>();
        else
            this.timexes = timeExpressions;
    }

    public List<Timex2TimeExpression> getTimexes() {
        return timexes;
    }

    public List<TimeExpression> getAllTimeExpressions()
    {
        List<TimeExpression> allTimeExpressions = new ArrayList<TimeExpression>();
        for(Timex2TimeExpression timex: timexes)
        {
            allTimeExpressions.addAll(timex.getTimeExpressions());
        }
        return allTimeExpressions;
    }

    public List<String> getAllNLExpressions()
    {
        List<String> allTimeExpressions = new ArrayList<String>();
        for(Timex2TimeExpression timex: timexes)
        {
            allTimeExpressions.add(timex.getTimex2().getText());
        }
        return allTimeExpressions;
    }

    public void setTimeExpressions(List<Timex2TimeExpression> timexes) {
        this.timexes = timexes;
    }



    public String toString()
    {
        String txt = super.toString();
        txt+="\nTimeExpressions [\n";
        if(timexes!=null)
        {
            String catToken = "";
            for(Timex2TimeExpression timex : timexes)
            {

                txt+= catToken + timex.getTimex2().getText() + " : {";
                catToken = ",\n";
                String catToken2 = "";
                for(TimeExpression timeExpression: timex.getTimeExpressions())
                {
                    txt+=catToken2 + timeExpression;
                    catToken2 = ",";
                }
                txt +="}";
            }
        }
        txt+="\n]";
        return txt;
    }
}
