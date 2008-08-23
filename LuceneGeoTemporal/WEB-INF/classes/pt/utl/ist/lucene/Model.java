package pt.utl.ist.lucene;

import org.apache.log4j.Logger;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum Model
{
    LanguageModel("lm"),
    VectorSpaceModel("vs");

    public static Model defaultModel;
    private static final Logger logger = Logger.getLogger(Model.class);

    static
    {
        defaultModel = Model.parse(Globals.ConfigProperties.getProperty("lucene.model"));
    }

    String name;

    private Model(String name)
    {
        this.name = name;
    }

    public static Model parse(String name)
    {
        if(name == null)
            return null;
        for(Model spatialEnum: Model.values())
        {
            if(spatialEnum.name.equals(name))
                return spatialEnum;
        }
        return defaultModel;
    }


    public String getName()
    {
        return name;
    }
}
