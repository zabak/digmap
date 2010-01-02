package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 12:37:34
 * @email machadofisher@gmail.com
 */
public class NyTimesDocument {


    private static final Logger logger = Logger.getLogger(NyTimesDocument.class);
    
    int headLineStartOffset = 0;
    int headLineEndOffset = 0;
    int dateLineStartOffSet = 0;
    int dateLineEndOffSet = 0;

    int placeMakerHeadLineStartOffset = 0;
    int placeMakerHeadLineEndOffset = 0;
    int placeMakerDateLineStartOffSet = 0;
    int placeMakerDateLineEndOffSet = 0;

    StringBuilder sgml = new StringBuilder();
    int articleYear;
    int articleMonth;
    int articleDay;

    String fSourceFile;
    String fDateYearMonthSort;

    String dId;
    String dType;
    String dHeadline;
    String dDateline;
    List<TextFragment> dParagraphs = null;         //for parapragphs
    TextFragment dText = null;

    String pArticleNumber;
    String pDateYearMonthDaySort;
    Date pDate;


    public NyTimesDocument() {
    }


    public String getSgml()
    {
        return sgml.toString();
    }

    public void appendSgmlLine(String line)
    {
        sgml.append(line).append("\n");
    }

    public int getArticleYear() {
        return articleYear;
    }

    public void setArticleYear(int articleYear) {
        this.articleYear = articleYear;
    }

    public int getArticleMonth() {
        return articleMonth;
    }

    public void setArticleMonth(int articleMonth) {
        this.articleMonth = articleMonth;
    }

    public int getArticleDay() {
        return articleDay;
    }

    public void setArticleDay(int articleDay) {
        this.articleDay = articleDay;
    }

    public String getFSourceFile() {
        return fSourceFile;
    }

    public void setFSourceFile(String fSourceFile) {
        this.fSourceFile = fSourceFile;
    }

    public String getFDateYearMonthSort() {
        return fDateYearMonthSort;
    }

    public void setFDateYearMonthSort(String fDateYearMonthSort) {
        this.fDateYearMonthSort = fDateYearMonthSort;
    }

    public String getDId() {
        return dId;
    }

    public void setDId(String dId) {
        this.dId = dId;
    }

    public int getHeadLineStartOffset() {
        return headLineStartOffset;
    }

    public void setHeadLineStartOffset(int headLineStartOffset) {
        this.headLineStartOffset = headLineStartOffset;
    }

    public int getHeadLineEndOffset() {
        return headLineEndOffset;
    }

    public void setHeadLineEndOffset(int headLineEndOffset) {
        this.headLineEndOffset = headLineEndOffset;
    }

    public int getDateLineStartOffSet() {
        return dateLineStartOffSet;
    }

    public void setDateLineStartOffSet(int dateLineStartOffSet) {
        this.dateLineStartOffSet = dateLineStartOffSet;
    }

    public int getDateLineEndOffSet() {
        return dateLineEndOffSet;
    }

    public void setDateLineEndOffSet(int dateLineEndOffSet) {
        this.dateLineEndOffSet = dateLineEndOffSet;
    }

    public int getPlaceMakerHeadLineStartOffset() {
        return placeMakerHeadLineStartOffset;
    }

    public void setPlaceMakerHeadLineStartOffset(int placeMakerHeadLineStartOffset) {
        this.placeMakerHeadLineStartOffset = placeMakerHeadLineStartOffset;
    }

    public int getPlaceMakerHeadLineEndOffset() {
        return placeMakerHeadLineEndOffset;
    }

    public void setPlaceMakerHeadLineEndOffset(int placeMakerHeadLineEndOffset) {
        this.placeMakerHeadLineEndOffset = placeMakerHeadLineEndOffset;
    }

    public int getPlaceMakerDateLineStartOffSet() {
        return placeMakerDateLineStartOffSet;
    }

    public void setPlaceMakerDateLineStartOffSet(int placeMakerDateLineStartOffSet) {
        this.placeMakerDateLineStartOffSet = placeMakerDateLineStartOffSet;
    }

    public int getPlaceMakerDateLineEndOffSet() {
        return placeMakerDateLineEndOffSet;
    }

    public void setPlaceMakerDateLineEndOffSet(int placeMakerDateLineEndOffSet) {
        this.placeMakerDateLineEndOffSet = placeMakerDateLineEndOffSet;
    }

    public String getDType() {
        return dType;
    }

    public void setDType(String dType) {
        this.dType = dType;
    }

    public String getDHeadline() {
        return dHeadline;
    }

    public void setDHeadline(String dHeadline) {
        this.dHeadline = dHeadline;
    }

    public String getDDateline() {
        return dDateline;
    }

    public void setDDateline(String dDateline) {
        this.dDateline = dDateline;
    }

    public List<TextFragment> getParagraphs()
    {
        return dParagraphs;
    }

    public List<String> getDParagraphs() {
        List<String> ps = new ArrayList<String>();
        if(dParagraphs != null)
        {
            for(TextFragment p: dParagraphs)
                ps.add(p.getP());
            return ps;
        }
        return null;
    }

    public void setDParagraphs(List<TextFragment> dParagraphs) {
        this.dParagraphs = dParagraphs;
    }

    public String getPArticleNumber() {
        return pArticleNumber;
    }

    public void setPArticleNumber(String pArticleNumber) {
        this.pArticleNumber = pArticleNumber;
    }

    public String getPDateYearMonthDaySort() {
        return pDateYearMonthDaySort;
    }

    public void setPDateYearMonthDaySort(String pDateYearMonthDaySort) {
        this.pDateYearMonthDaySort = pDateYearMonthDaySort;
    }

    public Date getPDate() {
        return pDate;
    }

    public void setPDate(Date pDate) {
        this.pDate = pDate;
    }


    public TextFragment getDText()
    {
        return dText;
    }

    public void setDText(TextFragment dText) {
        this.dText = dText;
    }

    public static class TextFragment {
        String p;
        int startOffset;
        int endOffset;

        int placeMakerParserStartOffset;
        int placeMakerParserEndOffset;


        public TextFragment(int startOffset) {
            this.startOffset = startOffset;
        }

        public TextFragment(String p, int startOffset, int endOffset) {
            this.p = p;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public void setStartOffset(int startOffset) {
            this.startOffset = startOffset;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public void setEndOffset(int endOffset) {
            this.endOffset = endOffset;
        }


        public int getPlaceMakerParserStartOffset() {
            return placeMakerParserStartOffset;
        }

        public void setPlaceMakerParserStartOffset(int placeMakerParserStartOffset) {
            this.placeMakerParserStartOffset = placeMakerParserStartOffset;
        }

        public int getPlaceMakerParserEndOffset() {
            return placeMakerParserEndOffset;
        }

        public void setPlaceMakerParserEndOffset(int placeMakerParserEndOffset) {
            this.placeMakerParserEndOffset = placeMakerParserEndOffset;
        }
    }

    public void printOut()
    {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Document " + pArticleNumber + " (" + articleYear + "/" + articleMonth + "/" + articleDay + "):");
        System.out.println("  fSourceFile: " + fSourceFile);
        System.out.println("  fDateYearMonthSort: " + fDateYearMonthSort);
        System.out.println("  .........................................");
        System.out.println("  pArticleNumber: " + pArticleNumber);
        System.out.println("  pDateYearMonthDaySort: " + pDateYearMonthDaySort);
        System.out.println("  pDate: " + pDate);
        System.out.println("  .........................................");
        System.out.println("  dId:" + dId);
        System.out.println("  dType:" + dType);
        System.out.println("  dHeadLine:" + dHeadline);
        System.out.println("  dDateLine:" + dDateline);
        System.out.println("  .........................................");
        if(dParagraphs != null)
            for(TextFragment p: dParagraphs)
            {
                System.out.println("   P:" + p.getP());
            }
        System.out.println("  TEXT.....................................");
        System.out.println(dText.getP());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    public String toString()
    {

        StringBuilder sb = new StringBuilder();
        placeMakerHeadLineStartOffset = 0;
        sb.append(dHeadline);
        placeMakerHeadLineEndOffset = sb.length();

        sb.append(" ");

        if(dParagraphs != null && dParagraphs.size() > 0)
            for(TextFragment p : dParagraphs){
                p.setPlaceMakerParserStartOffset(sb.length());
                sb.append(p.getP()).append("\n");
                p.setPlaceMakerParserEndOffset(sb.length());
            }
        if(dText != null)
        {
            sb.append(" ");
            dText.setPlaceMakerParserStartOffset(sb.length());
            sb.append(dText.getP());
            dText.setPlaceMakerParserEndOffset(sb.length());
        }
        return sb.toString();
    }

    public int placeMakerOffset2OriginalOffset(int offset)
    {
        if(offset >= placeMakerDateLineStartOffSet && offset <= placeMakerHeadLineEndOffset)
        {
            int diff = offset - placeMakerHeadLineStartOffset;
            return headLineStartOffset + diff;
        }
        else if(dParagraphs != null && dParagraphs.size()>0)
        {
            for(TextFragment pFragment : dParagraphs)
            {
                if(offset >= pFragment.getPlaceMakerParserStartOffset() && offset <= pFragment.getPlaceMakerParserEndOffset())
                {
                    int diff = offset - pFragment.getPlaceMakerParserStartOffset();
                    return pFragment.getStartOffset() + diff;
                }
            }
        }
        if(dText != null)
        {
            int diff = offset - dText.getPlaceMakerParserStartOffset();
            return dText.getStartOffset() + diff;
        }

        logger.error("Mapping not found for offset " + offset + " on document " + dId);
        return -1;
    }
}
