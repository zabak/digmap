package pt.utl.ist.lucene.utils.nlp;

import org.apache.log4j.Logger;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.sentences.MedlineSentenceModel;

import java.util.List;
import java.util.ArrayList;

import pt.utl.ist.lucene.utils.Strings;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 15:56:51
 * @email machadofisher@gmail.com
 */
public class ParagraphSpliter
{
    public static List<Paragraph> split(String text, Class subType) throws IllegalAccessException, InstantiationException {
        return split(text,subType,"\n");
    }
    public static List<Paragraph> split(String text, Class subType, String paragraphDelim) throws IllegalAccessException, InstantiationException
    {
        List<Sentence> sentences = SentenceSpliter.split(text,Sentence.class);
        List<Paragraph> paragraphs = new ArrayList<Paragraph>();
        Paragraph p = (Paragraph) subType.newInstance();
        p.setIndex(paragraphs.size());
        for(Sentence sentence: sentences)
        {
            p.getSentences().add(sentence);
            if(sentence.getPhrase().replaceAll(" ","").endsWith(paragraphDelim))
            {
                if(p.getSentences().size() > 0)
                    paragraphs.add(p);
                p = (Paragraph) subType.newInstance();
                p.setIndex(paragraphs.size());
            }
        }
        if(p.getSentences().size() > 0)
            paragraphs.add(p);
        return paragraphs;
    }
}
