package pt.utl.ist.lucene;

import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.VersionEnum;
import pt.utl.ist.lucene.priors.DocumentPriors;
import pt.utl.ist.lucene.priors.impl.LanguageModelPriors;
import pt.utl.ist.lucene.priors.impl.DoNothingPriors;
import pt.utl.ist.lucene.priors.impl.LanguageModelHiemstraPriors;
import org.apache.log4j.Logger;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public enum Model
{

    VectorSpaceModel("VectorSpace", false,"vs",new DoNothingPriors()),
    LanguageModelHiemstra("LanguageModelHiemstra", true,"hlm", new LanguageModelHiemstraPriors()),
    LanguageModel("LanguageModel", true,"lm", new LanguageModelPriors()),
    DLHHypergeometricDFRModel("DLHHypergeometricDFRModel", true,"DLHHypergeometricDFRModel", new DoNothingPriors()),
    InExpC2DFRModel("InExpC2DFRModel", true,"InExpC2DFRModel", new DoNothingPriors()),
    InExpB2DFRModel("InExpB2DFRModel", true,"InExpB2DFRModel", new DoNothingPriors()),
    IFB2DFRModel("IFB2DFRModel", true,"IFB2DFRModel", new DoNothingPriors()),
    InL2DFRModel("InL2DFRModel", true,"InL2DFRModel", new DoNothingPriors()),
    PL2DFRModel("PL2DFRModel", true,"PL2DFRModel", new DoNothingPriors()),
    BB2DFRModel("BB2DFRModel", true,"BB2DFRModel", new DoNothingPriors()),
    OkapiBM25Model("OkapiBM25Model", true,"bm25", new DoNothingPriors()),
    BM25b("BM25b", true,"bm25b", new DoNothingPriors());






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
    DocumentPriors documentPriors;

    private Model(String name, boolean isProbabilistic, String shortName, DocumentPriors documentPriors)
    {
        this.name = name;
        this.probabilistcModel = isProbabilistic;
        this.shortName = shortName;
        this.documentPriors = documentPriors;
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

    public DocumentPriors getDocumentFinalScorer()
    {
        return documentPriors;
    }
}
