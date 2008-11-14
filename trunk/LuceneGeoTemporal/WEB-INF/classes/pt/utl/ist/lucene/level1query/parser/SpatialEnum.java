package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.Globals;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 3:04:45
 * @see clef
 */
public enum SpatialEnum
{
    LATITUDE(Globals.LUCENE_LATITUDE_FIELD_QUERY),
    LONGITUDE(Globals.LUCENE_LONGITUDE_FIELD_QUERY),
    NORTHLIMIT(Globals.LUCENE_NORTHLIMIT_FIELD_QUERY),
    SOUTHLIMIT(Globals.LUCENE_SOUTHLIMIT_FIELD_QUERY),
    EASTLIMIT(Globals.LUCENE_EASTLIMIT_FIELD_QUERY),
    WESTLIMIT(Globals.LUCENE_WESTLIMIT_FIELD_QUERY),
    RADIUM(Globals.LUCENE_RADIUM_FIELD_QUERY),
    RADIUM_MILES(Globals.LUCENE_RADIUM_MILES_FIELD_QUERY),
    RADIUM_KM(Globals.LUCENE_RADIUM_KM_FIELD_QUERY),

    EXTRA_LATITUDE("***"),
    EXTRA_LONGITUDE("***");

    private String name;

    private SpatialEnum(String name)
    {
        this.name = name;
    }

    public static SpatialEnum parse(String name)
    {
        if(name == null)
            return null;
        for(SpatialEnum spatialEnum: SpatialEnum.values())
        {
            if(spatialEnum.name.equals(name))
                return spatialEnum;
        }
        if(name.startsWith(LATITUDE.getName()))
            return EXTRA_LATITUDE;
        else if(name.startsWith(LONGITUDE.getName()))
            return EXTRA_LONGITUDE;
        return null;
    }


    public String getName()
    {
        return name;
    }
}
