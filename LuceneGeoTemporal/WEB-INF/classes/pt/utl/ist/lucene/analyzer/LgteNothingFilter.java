package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

import java.io.IOException;

/**
 * Normalizes token text to lower case.
 *
 * @version $Id: LgteDiacriticFilter.java,v 1.2 2007/12/27 01:45:58 jmachado Exp $
 */
public final class LgteNothingFilter extends TokenFilter
{
    public LgteNothingFilter(TokenStream in)
    {
        super(in);
    }

    public final Token next() throws IOException
    {
        return input.next();
    }

   

    public static void main(String args[])
    {
           for(int i = 0; i < 255;i++)
               System.out.println(i + ":" + (char)i);
    }
}
