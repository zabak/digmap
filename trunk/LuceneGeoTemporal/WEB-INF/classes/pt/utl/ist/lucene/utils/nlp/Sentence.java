package pt.utl.ist.lucene.utils.nlp;

import pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

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
    String phrase;
    int startOffset;
    int endOffset;


    public Sentence() {
    }

    public Sentence(int index, String phrase, int startOffset, int endOffset)
    {
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



    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public String toString()
    {
        return "SENTENCE " + index + " (" + startOffset + "," + endOffset + "): [" + phrase + "]";
    }
}
