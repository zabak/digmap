package pt.utl.ist.lucene;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum OrderEnum
{
    score("sc"),
    space("sp"),
    time("t"),
    scoreSpace("sc_sp"),
    scoreTime("sc_t"),
    timeSpace("t_sp"),
    scoreTimeSpace("sc_t_sp");



    private final String type;

    OrderEnum(String namespace)
    {
        this.type = namespace;
    }

    public static OrderEnum parse(String str)
    {
        for (OrderEnum orderEnum : OrderEnum.values())
        {
            if(orderEnum.type.equals(str))
            {
                return orderEnum;
            }
        }

        return scoreTimeSpace;
    }

    public String getValue()
    {
        return type;
    }

    public boolean isSpatial()
    {
        return this == space || this == timeSpace || this == scoreSpace || this == scoreTimeSpace;
    }

    public boolean isTime()
    {
        return this == time || this == timeSpace || this == scoreTime || this == scoreTimeSpace;
    }

    public boolean isScore()
    {
        return this == space || this == score || this == timeSpace || this == scoreTimeSpace;
    }



}
