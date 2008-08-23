package pt.utl.ist.lucene.treceval;

/**
 * @author Jorge Machado
 * @date 22/Abr/2008
 * @time 11:56:06
 * @see mitra.app.domain
 */
public class IndexField 
{
    long id;
    String name;
    String value;
    long collectionId;
    long resourceId;


    public IndexField(String index, String value) {
        this.name = index;
        this.value = value;
    }

    public IndexField() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public long getCollectionId()
    {
        return collectionId;
    }

    public void setCollectionId(long collectionId)
    {
        this.collectionId = collectionId;
    }

    public long getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(long resourceId)
    {
        this.resourceId = resourceId;
    }
}
