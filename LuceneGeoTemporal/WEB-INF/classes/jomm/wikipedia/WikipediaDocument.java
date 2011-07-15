package jomm.wikipedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 7/Jul/2011
 * Time: 12:30:11
 * To change this template use File | Settings | File Templates.
 */
public class WikipediaDocument
{
    String title;
    List<String> paragraphs = new ArrayList<String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs;
    }
}
