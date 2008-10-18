package pt.utl.ist.lucene;

import pt.utl.ist.lucene.config.ConfigProperties;

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


    public static final OrderEnum defaultOrder = parse(ConfigProperties.getProperty("lgte.default.order"));


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
        return defaultOrder;
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

    public String toString()
    {
        return type;
    }
}
