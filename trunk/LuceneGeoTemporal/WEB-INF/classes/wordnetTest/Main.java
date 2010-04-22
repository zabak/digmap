package wordnetTest;


import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 6/Out/2009
 * Time: 11:44:52
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    // D:\Jorge\Documents\software\wordnet\WNdb-3.0

    public static void main(String[] args) throws MalformedURLException {

       
        // construct the URL to the Wordnet dictionary directory
        String wnhome = System.getenv("WNHOME");
        String path = "D:\\Jorge\\Documents\\software\\wordnet\\WNdb-3.0" + File.separator + "dict";
        URL url = new URL("file", null, path);

        // construct the dictionary object and open it
        IDictionary dict = new Dictionary(url);
        dict.open();
//
//        // look up first sense of the word "dog"
//        IIndexWord idxWord = dict.getIndexWord("dog", POS.NOUN);
//        IWordID wordID = idxWord.getWordIDs().get(0);
//        IWord word = dict.getWord(wordID);
//        System.out.println("Id = " + wordID);
//        System.out.println("Lemma = " + word.getLemma());
//        System.out.println("Gloss = " + word.getSynset().getGloss());
        // get the synset


        IIndexWord idxWord = dict.getIndexWord("country", POS.NOUN);
        IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
        IWord word = dict.getWord(wordID);
        ISynset synset = word.getSynset();

        // get the hypernyms
        List<ISynsetID> hypernyms =
                synset.getRelatedSynsets(Pointer.HYPONYM);

        // print out each hypernym’s id and synonyms
        List<IWord> words;
        for(ISynsetID sid : hypernyms){
            words = dict.getSynset(sid).getWords();
            System.out.print(sid + " {");
            for(Iterator<IWord> i = words.iterator(); i.hasNext();){
                System.out.print(i.next().getLemma());
                if(i.hasNext()) System.out.print(", ");
            }
            System.out.println("}");
            printHipernyms(dict,sid,2, "   ");
        }
    }

    public static void printHipernyms(IDictionary dict,ISynsetID sid , int level, String spaces)
    {
        if(level == 0)
            return;
        ISynset synset2 = dict.getSynset(sid);
        List<ISynsetID> hypernyms2 =
                synset2.getRelatedSynsets(Pointer.HYPONYM);
        for(ISynsetID sid2 : hypernyms2){
            List<IWord> words = dict.getSynset(sid2).getWords();
            System.out.print(sid2 + spaces + "=>{");

            for(Iterator<IWord> i = words.iterator(); i.hasNext();){

                System.out.print(i.next().getLemma());
                if(i.hasNext()) System.out.print(", ");
            }
            System.out.println("}");
            printHipernyms(dict, sid2, level-1,"   " + spaces);
        }
    }
}