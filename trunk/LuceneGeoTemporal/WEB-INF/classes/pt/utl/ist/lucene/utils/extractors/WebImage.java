package pt.utl.ist.lucene.utils.extractors;


import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge Machado jmachado@ext.bn.pt
 * DateMonthYearArticles: 4/Jan/2007
 * Time: 18:18:24
 * To change this template use File | Settings | File Templates.
 */
public class WebImage
{

    private static final Logger logger = Logger.getLogger(WebImage.class);
    public static final int SURROUND_IMAGE_WORDS = 10;

    public static final int SHORT_IMG_KB = 10;
    public static final int MEDIUM_IMG_KB = 500;
    public static final int LARGE_IMG_KB = 800;

    public static final String SHORT_IMG = "short";
    public static final String MEDIUM_IMG = "medium";
    public static final String LARGE_IMG = "large";
    public static final String VERY_BIG_IMG = "xlarge";


    private String alt ="";
    private String surroundText ="";
    private String src="";
    private String extension="";
    private String completeUrl ="";
    private int textOffSet;
    private String mimeType="";
    private Date lastModiDate;
    private int size;
    private String filename="";
    private String parentUrl="";


    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getSurroundText() {
        return surroundText;
    }

    public int getTextOffSet() {
        return textOffSet;
    }

    public void setTextOffSet(int textOffSet) {
        this.textOffSet = textOffSet;
    }

    public void setSurroundText(String surroundText) {
        this.surroundText = surroundText;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCompleteUrl() {
        return completeUrl;
    }

    public void setCompleteUrl(String completeUrl) {
        this.completeUrl = completeUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Date getLastModiDate() {
        return lastModiDate;
    }

    public void setLastModiDate(Date lastModiDate) {
        this.lastModiDate = lastModiDate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {

        int lastSlash;
        if(filename!=null && (lastSlash=filename.lastIndexOf("/"))>=0 && filename.length()>=lastSlash+1)
            this.filename = filename.substring(lastSlash+1);
        else
            this.filename = filename;
    }

    public void initRemoteProperties(){
        logger.info("initRemoteProperties of image:" + completeUrl);
        URL u = null;
        try {
            u = new URL(completeUrl);
            URLConnection c = u.openConnection();
            setLastModiDate(new Date(c.getLastModified()));
            setMimeType(c.getContentType());
            setSize(c.getContentLength());
            setFilename(u.getFile());
        } catch (MalformedURLException e) {
            logger.error(e.toString());
        } catch (IOException e) {
            logger.error(e.toString(),e);
        }
    }

    public String getImageSizeDescription(){
        if(size/1024 <= SHORT_IMG_KB)
            return SHORT_IMG;
        else if(size/1024 <= MEDIUM_IMG_KB)
            return MEDIUM_IMG;
        else if(size/1024 <= MEDIUM_IMG_KB)
            return MEDIUM_IMG;
        else if(size/1024 <= LARGE_IMG_KB)
            return LARGE_IMG;
        else
            return VERY_BIG_IMG;
    }

    public String toString(){
//        initRemoteProperties();
        String str = "\n-------------------\nImage:";
        str += "\nText:" + surroundText;
//        str += "\nsrc:" + src;
//        str += "\nalt:" + alt;
//        str += "\nURL:" + completeUrl;
//        str += "\nPARENT:" + parentUrl;
//        str += "\nsize:" + size;
//        str += "\nsizeDesc:" + getImageSizeDescription();
//        str += "\nmimeType:" + mimeType;
//        str += "\nextension:" + extension;
//        str += "\nlastModification:" + lastModiDate;
//        str += "\nfilename:" + filename;
//        str += "\noffSet:" + textOffSet;

        return str;
    }

}
