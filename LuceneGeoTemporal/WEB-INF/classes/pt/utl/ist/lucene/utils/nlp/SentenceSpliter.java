package pt.utl.ist.lucene.utils.nlp;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.sentences.MedlineSentenceModel;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 15:56:51
 * @email machadofisher@gmail.com
 */
public class SentenceSpliter
{

    private static final Logger logger = Logger.getLogger(SentenceSpliter.class);

    static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    static final SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();

    String text;

    public static List split(String text, Class subType)
    {
        List<Sentence> sentences = new ArrayList<Sentence>();

        List<String> tokenList = new ArrayList<String>();
        List<String> whiteList = new ArrayList<String>();
        Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0,text.length());
        tokenizer.tokenize(tokenList,whiteList);

        String[] tokens = new String[tokenList.size()];
        String[] whites = new String[whiteList.size()];
        tokenList.toArray(tokens);
        whiteList.toArray(whites);
        int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);

        int sentStartTok = 0;
        int sentEndTok;
        int startOffset = 0;
        int endOffset = 0;
        for (int i = 0; i < sentenceBoundaries.length; ++i)
        {
            StringBuilder sentenceText = new StringBuilder();
            sentEndTok = sentenceBoundaries[i];
            for (int j=sentStartTok; j <= sentEndTok; j++) {
                String fragment = tokens[j]+whites[j+1];
                sentenceText.append(fragment);
                endOffset += fragment.length();
            }
            if(!sentenceText.toString().trim().equals(text.substring(startOffset, endOffset).trim()))
            {
                logger.error("Validation check fail Sentence is not equal to the substring extracted from original text between startOffset and endOffset");
                logger.error("->Sentence->:" + sentenceText.toString());
                logger.error("->text.substring(startOffset,endOffset)->:" + text.substring(startOffset, endOffset));
            }
            logger.debug(startOffset + "-" + endOffset + ":" + text.substring(startOffset, endOffset));

            Sentence sentence;
            try {
                sentence = (Sentence) subType.newInstance();
                sentence.setIndex(i);
                sentence.setPhrase(sentenceText.toString());
                sentence.setStartOffset(startOffset);
                sentence.setEndOffset(endOffset);
                logger.debug(sentence);
                sentences.add(sentence);
                startOffset = endOffset;
                sentStartTok = sentEndTok+1;
            } catch (InstantiationException e) {
                logger.error(e,e);
            } catch (IllegalAccessException e) {
                logger.error(e,e);
            }

        }
        return sentences;
    }
}
