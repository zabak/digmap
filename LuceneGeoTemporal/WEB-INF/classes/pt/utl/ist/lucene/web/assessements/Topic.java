package pt.utl.ist.lucene.web.assessements;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 13:33:44
 * To change this template use File | Settings | File Templates.
 */
public class Topic
{
    String idTopic;
    String description;
    String narrative;
    String task;
    int docs;


    public String getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(String idTopic) {
        this.idTopic = idTopic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public int getDocs() {
        return docs;
    }

    public void setDocs(int docs) {
        this.docs = docs;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "idTopic='" + idTopic + '\'' +
                ", description='" + description + '\'' +
                ", narrative='" + narrative + '\'' +
                ", task='" + task + '\'' +
                '}';
    }

    public String getDescriptionForCombo() {
        if(description.length() > 70)
        {
            return description.substring(0,70);
        }
        return description;
    }
}
