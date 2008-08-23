
package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

import java.io.*;


/**
 * Normalizes token text to lower case.
 *
 * @version $Id: LgteDiacriticFilter.java,v 1.2 2007/12/27 01:45:58 jmachado Exp $
/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public final class LgteDiacriticFilter extends TokenFilter
{
    public LgteDiacriticFilter(TokenStream in)
    {
        super(in);
    }

    public final Token next() throws IOException
    {
        Token t = input.next();

        if (t == null)
            return null;
        String value = t.termText();
        return new Token(clean(value),t.startOffset(),t.endOffset());
    }

    public static String clean(String value)
    {
        StringBuilder finalTerm = new StringBuilder();
        for (int j=0 ; j < value.length() ; j++)
        {
            char c = value.charAt(j);
            switch(c)
            {
                case 131: c = 'f'; break;
                case 138: c = 'S'; break;
                case 140: c = 'E'; break;
                case 154: c = 'S'; break;
                case 156: c = 'e'; break;
                case 159: c = 'Y'; break;
                case 167: c = 'S'; break;
                case 169: c = 'c'; break;
                case 192: c = 'A'; break;
                case 193: c = 'A'; break;
                case 194: c = 'A'; break;
                case 195: c = 'A'; break;
                case 196: c = 'A'; break;
                case 197: c = 'A'; break;
                case 198: c = 'A'; break;
                case 199: c = 'C'; break;
                case 200: c = 'E'; break;
                case 201: c = 'E'; break;
                case 202: c = 'E'; break;
                case 203: c = 'E'; break;
                case 204: c = 'I'; break;
                case 205: c = 'I'; break;
                case 206: c = 'I'; break;
                case 207: c = 'I'; break;
                case 208: c = 'D'; break;
                case 209: c = 'N'; break;
                case 210: c = 'O'; break;
                case 211: c = 'O'; break;
                case 212: c = 'O'; break;
                case 213: c = 'O'; break;
                case 214: c = 'O'; break;
                case 215: c = 'X'; break;
                case 216: c = 'O'; break;
                case 217: c = 'U'; break;
                case 218: c = 'U'; break;
                case 219: c = 'U'; break;
                case 220: c = 'U'; break;
                case 221: c = 'Y'; break;
                case 222: c = 'P'; break;
                case 223: c = 'B'; break;
                case 224: c = 'a'; break;
                case 225: c = 'a'; break;
                case 226: c = 'a'; break;
                case 227: c = 'a'; break;
                case 228: c = 'a'; break;
                case 229: c = 'a'; break;
                case 230: c = 'a'; break;
                case 231: c = 'c'; break;
                case 232: c = 'e'; break;
                case 233: c = 'e'; break;
                case 234: c = 'e'; break;
                case 235: c = 'e'; break;
                case 236: c = 'i'; break;
                case 237: c = 'i'; break;
                case 238: c = 'i'; break;
                case 239: c = 'i'; break;
                case 240: c = 'o'; break;
                case 241: c = 'n'; break;
                case 242: c = 'o'; break;
                case 243: c = 'o'; break;
                case 244: c = 'o'; break;
                case 245: c = 'o'; break;
                case 246: c = 'o'; break;
                case 247: break;
                case 248: c = 'o'; break;
                case 249: c = 'u'; break;
                case 250: c = 'u'; break;
                case 251: c = 'u'; break;
                case 252: c = 'u'; break;
                case 253: c = 'y'; break;
                case 254: c = 'p'; break;
            }
            finalTerm.append(c);
        }
        return finalTerm.toString();
    }

    public static void main(String args[])
    {
           for(int i = 0; i < 255;i++)
               System.out.println(i + ":" + (char)i);
    }
}
