package pt.utl.ist.lucene.web.assessements;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 12:01:40
 * To change this template use File | Settings | File Templates.
 */
public class HistoryEntry
{
    String assessor;
    String relevance;
    Date date;

    public String getAssessor() {
        return assessor;
    }

    public void setAssessor(String assessor) {
        this.assessor = assessor;
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

    @Override
    public String toString() {
        return "HistoryEntry{" +
                "assessor='" + assessor + '\'' +
                ", relevance='" + relevance + '\'' +
                ", date=" + date +
                '}';
    }
}
