package pt.utl.ist.lucene.utils;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

/**
 * @author Jorge Machado
 * @date 25/Jul/2008
 * @see jomm.util
 */
public class HighlightFormatter implements Formatter
{
    public String highlightTerm(String originalText, TokenGroup group) {
        if(group.getTotalScore()<=0)
		{
			return originalText;
		}
		return HighlightFormatter.highlightTerm(originalText);
    }

    public static String highlightTerm(String originalText) {

		return "<b><i>" + originalText + "</i></b>";
    }
}
