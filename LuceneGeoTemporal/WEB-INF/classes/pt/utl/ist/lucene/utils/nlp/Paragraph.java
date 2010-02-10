package pt.utl.ist.lucene.utils.nlp;

import pt.utl.ist.lucene.utils.Strings;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 16:57:08
 * @email machadofisher@gmail.com
 */
public class Paragraph {


    int index;
    List<Sentence> sentences = new ArrayList<Sentence>();
    String phrase = null;

    public Paragraph() {
    }

    public Paragraph(int index)
    {
        this.index = index;
    }


    public int getStartOffset() {
        return this.sentences.get(0).getStartOffset();
    }

    public int getEndOffset() {
        return this.sentences.get(sentences.size()-1).getEndOffset();
    }


    public List<Sentence> getSentences() {
        return sentences;
    }

    public String getPhrase() {
        if(phrase != null)
            return phrase;
        StringBuilder phraseBuilder = new StringBuilder();
        for(Sentence s: sentences)
            phraseBuilder.append(s.getPhrase());
        phrase = phraseBuilder.toString();
        return phrase;
    }

    public String getCleanedPhrase() {
        getPhrase();
        return Strings.cleanSpacesTabsLineBreak(phrase);
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String toString()
    {
        return "PARAGRAPH " + index + " (" + getStartOffset() + "," + getEndOffset() + "): [" + phrase + "]";
    }

    public static void main(String [] args)
    {
        System.out.println(">" + "    ola\n a    todos   \n  ".replaceAll("[ \t\r\n]+"," ").trim() + "<");
        System.out.println("ola\n a    todos".replace("[ \t]*"," "));
    }
}
