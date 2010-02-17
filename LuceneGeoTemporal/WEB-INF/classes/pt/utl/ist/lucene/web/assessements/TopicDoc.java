package pt.utl.ist.lucene.web.assessements;

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

    public void setHtml(String html) {
        this.html = html;
    }

    public List<HistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(List<HistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
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
                '}';
    }
}
