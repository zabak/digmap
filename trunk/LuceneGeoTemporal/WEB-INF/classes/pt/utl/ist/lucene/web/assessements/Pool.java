package pt.utl.ist.lucene.web.assessements;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 15:16:17
 * To change this template use File | Settings | File Templates.
 */
public class Pool
{
    long id;
    String runId;
    String description;
    String task;
    Date date;
    boolean closed;

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Pool{" +
                "id=" + id +
                ", runId='" + runId + '\'' +
                ", description='" + description + '\'' +
                ", task='" + task + '\'' +
                ", date=" + date +
                ", closed=" + closed +
                '}';
    }
}
