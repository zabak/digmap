package pt.utl.ist.lucene.versioning;

import pt.utl.ist.lucene.FilterEnum;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum VersionEnum
{
    v143("143"),
    v2("2");

    private final String type;

    VersionEnum(String namespace)
    {
        this.type = namespace;
    }

    public static VersionEnum parse(String str)
    {
        for (VersionEnum versionEnum : values())
        {
            if(versionEnum.type.equals(str))
            {
                return versionEnum;
            }
        }

        return null;
    }

    public String getValue()
    {
        return type;
    }
}
