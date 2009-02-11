package pt.utl.ist.lucene;

import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.VersionEnum;
import pt.utl.ist.lucene.models.DocumentFinalScorer;
import pt.utl.ist.lucene.models.impl.LanguageModelFinalScorer;
import pt.utl.ist.lucene.models.impl.DoNothingFinalScorer;
import pt.utl.ist.lucene.models.impl.LanguageModelHiemstraFinalScorer;
import org.apache.log4j.Logger;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum Model
{

    VectorSpaceModel("VectorSpace", false,"vs",null),
    LanguageModelHiemstra("LanguageModelHiemstra", true,"hlm", new LanguageModelHiemstraFinalScorer()),
    LanguageModel("LanguageModel", true,"lm", new LanguageModelFinalScorer()),
    DLHHypergeometricDFRModel("DLHHypergeometricDFRModel", true,"DLHHypergeometricDFRModel", new DoNothingFinalScorer()),
    InExpC2DFRModel("InExpC2DFRModel", true,"InExpC2DFRModel", new DoNothingFinalScorer()),
    InExpB2DFRModel("InExpB2DFRModel", true,"InExpB2DFRModel", new DoNothingFinalScorer()),
    IFB2DFRModel("IFB2DFRModel", true,"IFB2DFRModel", new DoNothingFinalScorer()),
    InL2DFRModel("InL2DFRModel", true,"InL2DFRModel", new DoNothingFinalScorer()),
    PL2DFRModel("PL2DFRModel", true,"PL2DFRModel", new DoNothingFinalScorer()),
    BB2DFRModel("BB2DFRModel", true,"BB2DFRModel", new DoNothingFinalScorer()),
    OkapiBM25Model("OkapiBM25Model", true,"bm25", new DoNothingFinalScorer());






    public static Model defaultModel;
    private static final Logger logger = Logger.getLogger(Model.class);

    static
    {
        defaultModel = Model.parse(ConfigProperties.getProperty("lucene.model"));
        if(defaultModel.isProbabilistcModel() && LuceneVersionFactory.getLuceneVersion().getVersion() != VersionEnum.v143)
        {
            logger.warn("Language Model should be used only with Lucene Version 1.4.3, please check pt/utl/ist/lucene/app.properties, or check in your code if you are using LM");
//            System.exit(-1);
        }
    }

    String name;
    boolean probabilistcModel;
    String shortName;
    DocumentFinalScorer documentFinalScorer;

    private Model(String name, boolean isProbabilistic, String shortName, DocumentFinalScorer documentFinalScorer)
    {
        this.name = name;
        this.probabilistcModel = isProbabilistic;
        this.shortName = shortName;
        this.documentFinalScorer = documentFinalScorer;
    }

    public static Model parse(String name)
    {
        if(name == null)
            return null;
        for(Model modelEnum: Model.values())
        {
            if(modelEnum.shortName.equals(name) || modelEnum.name.equals(name))
                return modelEnum;
        }
        return defaultModel;
    }


    public String getName()
    {
        return name;
    }


    public String getShortName()
    {
        return shortName;
    }

    public boolean isProbabilistcModel()
    {
        return probabilistcModel;
    }

    public DocumentFinalScorer getDocumentFinalScorer()
    {
        return documentFinalScorer;
    }
}
