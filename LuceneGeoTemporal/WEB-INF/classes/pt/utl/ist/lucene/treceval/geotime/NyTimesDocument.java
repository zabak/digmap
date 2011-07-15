package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;


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

    int toStringHeadLineStartOffset = 0;
    int toStringHeadLineEndOffset = 0;
    int toStringDateLineStartOffSet = 0;
    int toStringDateLineEndOffSet = 0;

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


    boolean sgmlValid = true;

    public boolean isSgmlValid() {
        return sgmlValid;
    }

    public static boolean test(byte[] b, String csnam) {
        CharsetDecoder cd =
                Charset.availableCharsets().get(csnam).newDecoder( );
        try {
            cd.decode(ByteBuffer.wrap(b));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }
    public NyTimesDocument(BufferedReader reader, String fileName) throws IOException, EOFException
    {

        int headLineStartOffset = 0;
        int headLineEndOffset = 0;
        int dateLineStartOffSet = 0;
        int dateLineEndOffSet = 0;


        int pos = 0;

        setDParagraphs(new ArrayList<NyTimesDocument.TextFragment>());
        String line;
        boolean inHeadLine = false;
        boolean inDateLine = false;
        boolean inP = false;
        boolean inText = false;
        String auxP = "";
        String auxText = "";
        NyTimesDocument.TextFragment inParagrahp = null;
        NyTimesDocument.TextFragment inTextFrag = null;

        setFSourceFile(fileName);
        int i_ = fileName.lastIndexOf("_")+1;

        //Nao e usado falha no xie   mas nao rebenta
        setFDateYearMonthSort(fileName.substring(i_,fileName.lastIndexOf(".")));
        boolean first = true;
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        while((line = reader.readLine()) != null && !line.toUpperCase().equals("</DOC>"))
        {

            if(line.startsWith("<DATE_TIME>"))
                continue;
//            if(!test(line.getBytes(),"UTF-8"))
//            {
//                StringBuilder lineBuilder = new StringBuilder();
//                for(int i = 0; i < line.length();i++)
//                {
//                    char c = line.charAt(i);
//                    if(!test(new String("" + c).getBytes(),"UTF-8"))
//                    {
//                        lineBuilder.append("?");
//                    }
//                    else
//                    {
//                        lineBuilder.append(c);
//                    }
//                }
//                logger.error("BAD character encoding at line: " + line + " using line: " + lineBuilder.toString());
//                line = lineBuilder.toString();
//            }



            if(first)
            {
                first = false;
            }
            else
            {
                pos++;
            }
            appendSgmlLine(line);
            if(line.startsWith(("<DOC")))
            {
                pos += line.substring(line.indexOf(">") + 1).length();
                int iid  = line.indexOf("id=\"") + 4;
                setDId(line.substring(iid,line.indexOf("\"",iid)));

                //Nao e usado falha no Korea e no Mainichi nao rebenta
                setPArticleNumber(getDId().substring(getDId().lastIndexOf(".")+1));

                //int liid_ = getDId().lastIndexOf("_")+1;
                int liid_ = getDId().lastIndexOf(".")-8;
                setPDateYearMonthDaySort(getDId().substring(liid_,getDId().lastIndexOf(".")));

                setArticleYear(Integer.parseInt(getPDateYearMonthDaySort().substring(0,4)));
                setArticleMonth(Integer.parseInt(getPDateYearMonthDaySort().substring(4,6)));
                setArticleDay(Integer.parseInt(getPDateYearMonthDaySort().substring(6)));
                pos++;
                String datetime = getArticleYear() + "-" +getArticleMonth() + "-" + getArticleDay();
                appendSgmlLine("<DATE_TIME>" + datetime + "</DATE_TIME>");
                pos+=datetime.length();

                GregorianCalendar c = new GregorianCalendar(getArticleYear(),getArticleMonth()-1,getArticleDay());
                setPDate(c.getTime());

                int itype  = line.indexOf("type=\"") + 6;
                setDType(line.substring(itype,line.indexOf("\"",itype)));
            }
            else if(line.startsWith(("<HEADLINE>")))
            {
                pos += line.substring("<HEADLINE>".length()).length();
                headLineStartOffset = pos;
                headLineEndOffset = pos;
                dateLineStartOffSet = pos;
                dateLineEndOffSet = pos;
                inHeadLine = true;
            }
            else if(line.startsWith(("</HEADLINE>")))
            {
                pos += line.substring("</HEADLINE>".length()).length();
                inHeadLine = false;
                setDHeadline(auxText);
                headLineEndOffset = pos;
                dateLineStartOffSet = pos;
                dateLineEndOffSet = pos;
                auxText = "";
            }
            else if(inHeadLine)
            {
                auxText += " " + line;
                pos += line.length();
            }
            else if(line.startsWith(("<DATELINE>")))
            {
                pos += line.substring("<DATELINE>".length()).length();
                inDateLine = true;
                dateLineStartOffSet = pos;
                dateLineEndOffSet = pos;
            }
            else if(line.startsWith(("</DATELINE>")))
            {
                dateLineEndOffSet = pos;
                pos += line.substring("</DATELINE>".length()).length();
                inDateLine = false;
                setDDateline(auxText);
                auxText = "";
            }
            else if(inDateLine)
            {
                pos += line.length(); //dont use the " " because was introduced by this methos is not original
                auxText += " " + line;
            }
            else if(line.startsWith(("<P>")))
            {
                pos += line.substring("<P>".length()).length();
                inParagrahp = new NyTimesDocument.TextFragment(pos);
                inP = true;
            }
            else if(line.startsWith(("</P>")))
            {
                pos += line.substring("</P>".length()).length();
                inParagrahp.setEndOffset(pos);
                inParagrahp.setP(auxP);

                inP = false;
                getParagraphs().add(inParagrahp);
                auxP = "";
                inParagrahp = null;
            }
            else if(inP)
            {
                pos += line.length();
                auxP += " " + line;
            }
            else if(line.startsWith(("<TEXT>")))
            {
                pos += line.substring("<TEXT>".length()).length();
                inTextFrag = new NyTimesDocument.TextFragment(pos);
                inText = true;
            }
            else if(line.startsWith(("</TEXT>")))
            {
                inTextFrag.setEndOffset(pos);
                inTextFrag.setP(auxText);

                inText = false;
                setDText(inTextFrag);
                auxText = "";
                inTextFrag = null;
            }
            else if(inText)
            {
                pos += line.length();
                auxText += " " + line;
            }
            else
            {
                pos += line.length();
            }
        }
        if(line == null && pos == 0)
        {
            throw new EOFException();
        }
        else if(line == null)
            sgmlValid = false;
        else
            appendSgmlLine(line);



        setHeadLineStartOffset(headLineStartOffset);
        setHeadLineEndOffset(headLineEndOffset);
        setDateLineStartOffSet(dateLineStartOffSet);
        setDateLineEndOffSet(dateLineEndOffSet);

        toString(); //to fill offsets
    }

    

    public String getTimeExpressionDocumentNormalized()
    {
        return String.format("%04d%02d%02d",getArticleYear(),getArticleMonth(), getArticleDay());
    }
    public String getSgmlWithoutTags()
    {
        return getSgml().replaceAll("<[^>]+>","");
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

    public int getToStringHeadLineStartOffset() {
        return toStringHeadLineStartOffset;
    }

    public void setToStringHeadLineStartOffset(int toStringHeadLineStartOffset) {
        this.toStringHeadLineStartOffset = toStringHeadLineStartOffset;
    }

    public int getToStringHeadLineEndOffset() {
        return toStringHeadLineEndOffset;
    }

    public void setToStringHeadLineEndOffset(int toStringHeadLineEndOffset) {
        this.toStringHeadLineEndOffset = toStringHeadLineEndOffset;
    }

    public int getToStringDateLineStartOffSet() {
        return toStringDateLineStartOffSet;
    }

    public void setToStringDateLineStartOffSet(int toStringDateLineStartOffSet) {
        this.toStringDateLineStartOffSet = toStringDateLineStartOffSet;
    }

    public int getToStringDateLineEndOffSet() {
        return toStringDateLineEndOffSet;
    }

    public void setToStringDateLineEndOffSet(int toStringDateLineEndOffSet) {
        this.toStringDateLineEndOffSet = toStringDateLineEndOffSet;
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

        int toStringStartOffset;
        int toStringEndOffset;


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


        public int getToStringStartOffset() {
            return toStringStartOffset;
        }

        public void setToStringStartOffset(int toStringStartOffset) {
            this.toStringStartOffset = toStringStartOffset;
        }

        public int getToStringEndOffset() {
            return toStringEndOffset;
        }

        public void setToStringEndOffset(int toStringEndOffset) {
            this.toStringEndOffset = toStringEndOffset;
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
        toStringHeadLineStartOffset = 0;
        sb.append(dHeadline);
        toStringHeadLineEndOffset = sb.length();

        sb.append(" ");

        if(dParagraphs != null && dParagraphs.size() > 0)
            for(TextFragment p : dParagraphs){
                p.setToStringStartOffset(sb.length());
                sb.append(p.getP()).append("\n");
                p.setToStringEndOffset(sb.length());
            }
        if(dText != null)
        {
            sb.append(" ");
            dText.setToStringStartOffset(sb.length());
            sb.append(dText.getP());
            dText.setToStringEndOffset(sb.length());
        }
        return sb.toString();
    }

    public int toStringOffset2txtwithoutTagsOffset(int offset)
    {
        //SO os anotados com o Nytimes é que tem este problema
        if(!getDId().startsWith("nyt"))
            return offset;

        if(offset >= toStringDateLineStartOffSet && offset <= toStringHeadLineEndOffset)
        {
            int diff = offset - toStringHeadLineStartOffset;
            return headLineStartOffset + diff;
        }
        else if(offset >= toStringDateLineStartOffSet && offset <= toStringDateLineEndOffSet)
        {
            int diff = offset - toStringDateLineStartOffSet;
            return toStringDateLineStartOffSet + diff;
        }
        else if(dParagraphs != null && dParagraphs.size()>0)
        {
            for(TextFragment pFragment : dParagraphs)
            {
                if(offset >= pFragment.getToStringStartOffset() && offset <= pFragment.getToStringEndOffset())
                {
                    int diff = offset - pFragment.getToStringStartOffset();
                    return pFragment.getStartOffset() + diff;
                }
            }
        }
        if(dText != null)
        {
            int diff = offset - dText.getToStringStartOffset();
            return dText.getStartOffset() + diff;
        }

        logger.error("Mapping not found for offset " + offset + " on document " + dId);
        return -1;


    }


    public int compare(String id)
    {
        return getDId().compareTo(id);
    }


    public boolean isBiggerThan(NyTimesDocument anotherDoc)
    {
        return
                compare(anotherDoc.getDId())>0;
    }
}
