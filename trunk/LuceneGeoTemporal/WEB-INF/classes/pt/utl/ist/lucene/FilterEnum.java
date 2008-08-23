package pt.utl.ist.lucene;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum FilterEnum
{
    no("no"),
    space("sp"),
    time("t"),
    timeSpace("t_sp");

    private final String type;

    FilterEnum(String namespace)
    {
        this.type = namespace;
    }

    public static FilterEnum parse(String str)
    {
        for (FilterEnum orderEnum : values())
        {
            if(orderEnum.type.equals(str))
            {
                return orderEnum;
            }
        }

        return timeSpace;
    }

    public String getValue()
    {
        return type;
    }

    public boolean isSpace()
    {
        return this == space || this == timeSpace;
    }

    public boolean isTime()
    {
        return this == time || this == timeSpace;
    }
}
