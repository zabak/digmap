package pt.utl.ist.lucene.utils.temporal;

import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.XPath;
import org.dom4j.Element;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.sentences.MedlineSentenceModel;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 16:54:03
 * @email machadofisher@gmail.com
 */
public class TimextagDocumentSpliter {

    String sgml;
    String text;
    String timexesXml;
    List<DocumentPhrase> phrases = new ArrayList<DocumentPhrase>();
    Iterator<Element> timexesIterator = null;
    private Timex2 nowTimex = null;

    static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    static final SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();

    public TimextagDocumentSpliter(String sgml,  String timexesXml) throws DocumentException
    {
        this.sgml = sgml;
        this.timexesXml = timexesXml;
        this.text = sgml.replaceAll("<[^>]+>","");
        if(timexesXml != null)
        {
            Document dom = Dom4jUtil.parse(timexesXml);
            XPath xPath = dom.createXPath("//TIMEX2");
            List<Element> timexes2 = xPath.selectNodes(dom.getRootElement());
            if(timexes2 != null && timexes2.size() > 0)
            {
                timexesIterator = timexes2.iterator();
                nowTimex = new Timex2(timexesIterator.next());
            }
        }

        splitPhrases();
    }



    private void splitPhrases()
    {
        List<String> tokenList = new ArrayList<String>();
        List<String> whiteList = new ArrayList<String>();
        Tokenizer tokenizer
                = TOKENIZER_FACTORY.tokenizer(text.toCharArray(),
                0,text.length());
        tokenizer.tokenize(tokenList,whiteList);

        String[] tokens = new String[tokenList.size()];
        String[] whites = new String[whiteList.size()];
        tokenList.toArray(tokens);
        whiteList.toArray(whites);
        int[] sentenceBoundaries
                = SENTENCE_MODEL.boundaryIndices(tokens,whites);


        int sentStartTok = 0;
        int sentEndTok = 0;
        int pointerStart = 0;
        int pointerEnd = 0;
        for (int i = 0; i < sentenceBoundaries.length; ++i) {
            sentEndTok = sentenceBoundaries[i];

            System.out.println("SENTENCE "+(i+1)+": ");
            for (int j=sentStartTok; j <= sentEndTok; j++) {
                String fragment = tokens[j]+whites[j+1];
                System.out.print(fragment);
                pointerEnd += fragment.length();
            }
            System.out.println();

            System.out.println(pointerStart + "-" + pointerEnd + ":" + text.substring(pointerStart,pointerEnd));

            if(nowTimex.getRstart() >= pointerStart && nowTimex.getRend() <= pointerEnd)
            {
                
            }
            

            pointerStart = pointerEnd;

            sentStartTok = sentEndTok+1;
        }
    }

    public List<DocumentPhrase> getPhrases()
    {
        return null;
    }


    public String getSgml() {
        return sgml;
    }

    public String getText() {
        return text;
    }

    public String getTimexesXml() {
        return timexesXml;
    }
}
