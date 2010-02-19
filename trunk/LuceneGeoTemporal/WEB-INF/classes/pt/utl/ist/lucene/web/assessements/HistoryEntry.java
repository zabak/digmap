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
    String obs;

    public String getColor()
    {
        if(relevance != null)
        {
            if(relevance.equals("relevant"))
                return "green";
            else if(relevance.equals("partially-relevant-where"))
                return "rgb(249,208,172)";
            else if(relevance.equals("partially-relevant-when"))
                return "lightskyblue";
            else if(relevance.equals("partially-relevant-other"))
                return "yellow";
            else if(relevance.equals("irrelevant"))
                return "red";
        }
        return "";
    }
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

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override
    public String toString() {
        return "HistoryEntry{" +
                "assessor='" + assessor + '\'' +
                ", relevance='" + relevance + '\'' +
                ", date=" + date +
                ", obs='" + obs + '\'' +
                '}';
    }
}
