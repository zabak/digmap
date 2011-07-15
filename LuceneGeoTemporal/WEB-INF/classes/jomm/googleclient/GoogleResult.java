package jomm.googleclient;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 7/Jul/2011
 * Time: 12:09:05
 * To change this template use File | Settings | File Templates.
 */
public class GoogleResult {

    String title;
    String url;
    String snippet;

    public GoogleResult(String title, String url, String snippet) {
        this.title = title;
        this.url = url;
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String toString() {
        return "GoogleResult{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", snippet='" + snippet + '\'' +
                '}';
    }
}
