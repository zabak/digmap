package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Token;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Jorge Machado
 * @date 29/Ago/2008
 * @see pt.utl.ist.lucene.analyzer
 */
public class LgteNothingTokenizer extends Tokenizer
{

    private static final Logger logger = Logger.getLogger(LgteNothingTokenizer.class);

    String token = null;

    public LgteNothingTokenizer(Reader reader)
    {
        super(reader);
        StringBuilder string = new StringBuilder();
        char[] buffer = new char[1024];
        int readed;
        try
        {
            while((readed = reader.read(buffer)) > 0)
            {
                string.append(buffer,0,readed);
            }
        }
        catch (IOException e)
        {
            logger.error(e,e);
        }
        token = string.toString();
    }
    public Token next() throws IOException
    {
        if(token != null)
        {
            String aux = token;
            token = null;
            return new Token(aux,0,aux.length());
        }
        return null;
    }
}
