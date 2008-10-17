package pt.utl.ist.lucene.utils.extractors;


import java.io.File;
import java.io.Reader;
import java.io.IOException;
import java.util.Set;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Utilizador
 * DateMonthYearArticles: 7/Nov/2004
 * Time: 20:16:56
 * To change this template use File | Settings | File Templates.
 */
public interface Extractor {
    
    public void init(File file) throws IOException;
    public String getComments();
    public String getText();
    public String getTitle();
    public String getH();
    public String getH1();
    public String getH2();
    public String getH3();
    public String getH4();
    public String getH5();
    public String getH6();
    public String getH7();
    public String getAnchor();
    public String getAuthor();
    public String getAbstract();
    public String getKeywords();
    public String getDescription();
    public String getScript();
    public Set getOutLinks(String urlStr);
    public List getImages(String urlStr);
    public String getBannedTextEnd();
    public String getBannedTextStart();

}
