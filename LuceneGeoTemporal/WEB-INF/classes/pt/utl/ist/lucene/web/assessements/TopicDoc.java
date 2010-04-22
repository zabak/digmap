package pt.utl.ist.lucene.web.assessements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 11:52:18
 * To change this template use File | Settings | File Templates.
 */
public class TopicDoc
{
    List<HistoryEntry> historyEntries = new ArrayList<HistoryEntry>();

    String topic;
    String docno;
    String relevance;
    Date date;
    String admin;
    String html;
    String docTitle;
    double score;
    int rank;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDocno() {
        return docno;
    }

    public void setDocno(String docno) {
        this.docno = docno;
    }

    public String getRelevance() {
        return relevance;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getHtml() {
        return html;
    }

    public String getHtml(String keywords) throws IOException
    {
        String html = getHtml();
        if(keywords == null || keywords.trim().length() == 0)
            return html;
        org.apache.lucene.analysis.TokenStream stream = pt.utl.ist.lucene.treceval.IndexCollections.en.getAnalyzerWithStemming().tokenStream("",new java.io.StringReader(keywords));
        org.apache.lucene.analysis.Token t;
        while((t=stream.next())!=null)
        {
            String str = t.termText();
            String Str = str.substring(0,1).toUpperCase()+str.substring(1);
            String STR = str.toUpperCase();
            if(str.trim().length()>1)
            {
                html = html.replaceAll(str,"<label class=\"keyword\">"  + str + "</label>");
                html = html.replaceAll(Str,"<label class=\"keyword\">" + Str + "</label>");
                html = html.replaceAll(STR,"<label class=\"keyword\">" + STR + "</label>");
            }
        }
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<HistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(List<HistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "TopicDoc{" +
                "historyEntries=" + historyEntries +
                ", topic='" + topic + '\'' +
                ", docno='" + docno + '\'' +
                ", relevance='" + relevance + '\'' +
                ", date=" + date +
                ", admin='" + admin + '\'' +
                ", html='" + html + '\'' +
                ", docTitle='" + docTitle + '\'' +
                ", score=" + score +
                ", rank=" + rank +
                '}';
    }
}
