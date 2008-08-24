package pt.utl.ist.lucene;

import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.VersionEnum;
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
        defaultModel = Model.parse(ConfigProperties.getProperty("lucene.model"));
        if(defaultModel == LanguageModel && LuceneVersionFactory.getLuceneVersion().getVersion() != VersionEnum.v143)
        {
            logger.warn("Language Model should be used only with Lucene Version 1.4.3, please check pt/utl/ist/lucene/app.properties, or check in your code if you are using LM");
//            System.exit(-1);
        }
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
