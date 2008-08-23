package pt.utl.ist.lucene.treceval.dublincore;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 3:04:45
 * @see pt.utl.ist.lucene.treceval
 */
public enum DcFieldEnum
{
    title("title"),
    creator("creator"),
    subject("subject"),
    contributor("contributor"),
    description("description"),
    issued("issued"),
    publisher("publisher"),
    location("location"),
    relation("relation");



    private String name;

    private DcFieldEnum(String name)
    {
        this.name = name;
    }

    public static DcFieldEnum parse(String name)
    {
        if(name == null)
            return null;
        for(DcFieldEnum dcFieldEnum:values())
        {
            if(dcFieldEnum.name.equals(name))
                return dcFieldEnum;
        }
        return null;
    }

}
