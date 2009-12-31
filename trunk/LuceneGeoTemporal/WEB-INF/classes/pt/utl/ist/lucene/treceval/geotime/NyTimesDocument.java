package pt.utl.ist.lucene.treceval.geotime;

import java.util.List;
import java.util.Date;

/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 12:37:34
 * @email machadofisher@gmail.com
 */
public class NyTimesDocument {
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
    List<String> dParagraphs = null;         //for parapragphs
    String dText = null;

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


    public List<String> getDParagraphs() {
        return dParagraphs;
    }

    public void setDParagraphs(List<String> dParagraphs) {
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


    public String getDText()
    {
        return dText;
    }

    public void setDText(String dText) {
        this.dText = dText;
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
           for(String p: dParagraphs)
           {
               System.out.println("   P:" + p);
           }
       System.out.println("  TEXT.....................................");
       System.out.println(dText);
       System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
   }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(dHeadline);
        sb.append(" ");
        if(dParagraphs != null && dParagraphs.size() > 0)
            for(String p : dParagraphs){sb.append(p).append("\n");}
        if(dText != null)
            sb.append(" ").append(dText);
        return sb.toString();
    }
}
