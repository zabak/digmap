package nmaf.util.structure;

/**
 * @author Jorge
 * @date 17/Mar/2009
 * @time 16:55:42
 */
public class Tuple<CLAZZ1,CLAZZ2>
{
    CLAZZ1 v1;
    CLAZZ2 v2;


    public Tuple(CLAZZ1 v1,CLAZZ2 v2)
    {
        this.v1 = v1;
        this.v2 = v2;
    }


    public CLAZZ1 getV1()
    {
        return v1;
    }

    public void setV1(CLAZZ1 v1)
    {
        this.v1 = v1;
    }

    public CLAZZ2 getV2()
    {
        return v2;
    }

    public void setV2(CLAZZ2 v2)
    {
        this.v2 = v2;
    }
}
