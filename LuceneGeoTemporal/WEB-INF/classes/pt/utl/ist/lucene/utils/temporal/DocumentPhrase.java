package pt.utl.ist.lucene.utils.temporal;

import java.util.List;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 16:57:08
 * @email machadofisher@gmail.com
 */
public class DocumentPhrase
{
    int index;
    List<TimeExpression> timeExpressions;
    String phrase;
    int startIndex;
    int endIndex;

    public DocumentPhrase(int index, List<TimeExpression> timeExpressions, String phrase, int startIndex, int endIndex)
    {
        this.timeExpressions = timeExpressions;
        this.phrase = phrase;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.index = index;
    }

    public List<TimeExpression> getTimeExpressions() {
        return timeExpressions;
    }

    public void setTimeExpressions(List<TimeExpression> timeExpressions) {
        this.timeExpressions = timeExpressions;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
}
