package pt.utl.ist.lucene.treceval.geotime;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 6/Jul/2011
 * Time: 12:06:46
 * To change this template use File | Settings | File Templates.
 */
public interface GeoTimeDocument {
    public String getTimeExpressionDocumentNormalized();
    public String getSgmlWithoutTags();
    public String getSgml();
    public int getArticleYear();
    public int getArticleMonth();
    public int getArticleDay();
    public String getFSourceFile();
    public String getDId();
    public String getDType();
    public String getDHeadline();
    public List<NyTimesDocument.TextFragment> getParagraphs();
    public List<String> getDParagraphs();
    public String getPDateYearMonthDaySort();
    public NyTimesDocument.TextFragment getDText();
}
