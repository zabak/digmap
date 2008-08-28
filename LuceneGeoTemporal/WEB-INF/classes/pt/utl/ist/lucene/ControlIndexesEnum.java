package pt.utl.ist.lucene;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum ControlIndexesEnum
{

    DOCUMENT_FILE_PATH(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_FILE_PATH),
    DOCUMENT_ID_FIELD(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD),

    LUCENE_CENTROIDE_LATITUDE_INDEX(Globals.LUCENE_CENTROIDE_LATITUDE_INDEX),
    LUCENE_CENTROIDE_LONGITUDE_INDEX(Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX),
    LUCENE_NORTHLIMIT_INDEX(Globals.LUCENE_NORTHLIMIT_INDEX),
    LUCENE_SOUTHLIMIT_INDEX(Globals.LUCENE_SOUTHLIMIT_INDEX),
    LUCENE_EASTLIMIT_INDEX(Globals.LUCENE_EASTLIMIT_INDEX),
    LUCENE_WESTLIMIT_INDEX(Globals.LUCENE_WESTLIMIT_INDEX),
    LUCENE_DIAGONAL_INDEX(Globals.LUCENE_DIAGONAL_INDEX),
    LUCENE_RADIUM_INDEX(Globals.LUCENE_RADIUM_INDEX),
    LUCENE_RADIUM_ORIGINAL_INDEX(Globals.LUCENE_RADIUM_ORIGINAL_INDEX),
    LUCENE_GEO_DOC_INDEX(Globals.LUCENE_GEO_DOC_INDEX),
    LUCENE_TIME_DOC_INDEX(Globals.LUCENE_TIME_DOC_INDEX),
    LUCENE_CENTROIDE_LATITUDE_ORIGINAL_INDEX(Globals.LUCENE_CENTROIDE_LATITUDE_ORIGINAL_INDEX),
    LUCENE_CENTROIDE_LONGITUDE_ORIGINAL_INDEX(Globals.LUCENE_CENTROIDE_LONGITUDE_ORIGINAL_INDEX),
    LUCENE_NORTHLIMIT_ORGINAL_INDEX(Globals.LUCENE_NORTHLIMIT_ORGINAL_INDEX),
    LUCENE_SOUTHLIMIT_ORGINAL_INDEX(Globals.LUCENE_SOUTHLIMIT_ORGINAL_INDEX),
    LUCENE_EASTLIMIT_ORGINAL_INDEX(Globals.LUCENE_EASTLIMIT_ORGINAL_INDEX),
    LUCENE_WESTLIMIT_ORGINAL_INDEX(Globals.LUCENE_WESTLIMIT_ORGINAL_INDEX),
    LUCENE_DIAGONAL_ORIGINAL_INDEX(Globals.LUCENE_DIAGONAL_ORIGINAL_INDEX),

    LUCENE_TIMEWIDTH_INDEX(Globals.LUCENE_TIMEWIDTH_INDEX),
    LUCENE_TIME_INDEX(Globals.LUCENE_TIME_INDEX),
    LUCENE_START_TIME_LIMIT_INDEX(Globals.LUCENE_START_TIME_LIMIT_INDEX),
    LUCENE_END_TIME_LIMIT_INDEX(Globals.LUCENE_END_TIME_LIMIT_INDEX),

    LUCENE_TIME_ORIGINAL_INDEX(Globals.LUCENE_TIME_ORIGINAL_INDEX),
    LUCENE_TIMEWIDTH_ORIGINAL_INDEX(Globals.LUCENE_TIMEWIDTH_ORIGINAL_INDEX),
    LUCENE_START_TIME_LIMIT_INDEX_ORIGINAL(Globals.LUCENE_START_TIME_LIMIT_INDEX_ORIGINAL),
    LUCENE_END_TIME_LIMIT_INDEX_ORIGINAL(Globals.LUCENE_END_TIME_LIMIT_INDEX_ORIGINAL);


    private final String type;

    ControlIndexesEnum(String namespace)
    {
        this.type = namespace;
    }

    public static ControlIndexesEnum parse(String str)
    {
        for (ControlIndexesEnum orderEnum : values())
        {
            if(orderEnum.type.equals(str))
            {
                return orderEnum;
            }
        }

        return null;
    }

    public String getValue()
    {
        return type;
    }
}
