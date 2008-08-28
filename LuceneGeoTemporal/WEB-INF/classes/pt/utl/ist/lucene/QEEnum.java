package pt.utl.ist.lucene;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum QEEnum
{
    no("no"),
    text("text"),
    lgte("lgte");

    private final String type;

    public static QEEnum defaultQE = QEEnum.parse(Globals.LGTE_USE_QE_BY_DEFAULT);

    QEEnum(String namespace)
    {
        this.type = namespace;
    }

    public static QEEnum parse(String str)
    {
        for (QEEnum qeEnum : values())
        {
            if(qeEnum.type.equals(str))
            {
                return qeEnum;
            }
        }
        return defaultQE;
    }

    public String getValue()
    {
        return type;
    }

    public boolean isQE()
    {
        return this != no;
    }


}
