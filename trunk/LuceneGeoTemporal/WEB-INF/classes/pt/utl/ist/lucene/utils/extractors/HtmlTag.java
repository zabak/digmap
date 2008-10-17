package pt.utl.ist.lucene.utils.extractors;

/**
 * @author Jorge Machado
 * @date 29/Nov/2007
 * @time 9:21:30
 */
public enum HtmlTag
{
    H1("h1"),
    H2("h2"),
    H3("h3"),
    H4("h4"),
    H5("h5"),
    H6("h6"),
    H7("h7"),
    A("a"),
    P("p"),
    SCRIPT("script"),
    STYLE("style"),
    TITLE("title"),
    HEAD("head"),
    BODY("body");



    private String value;

    private HtmlTag(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public static HtmlTag parse(String value)
    {
        for (HtmlTag htmlTag : values())
        {
            if(htmlTag.value.equalsIgnoreCase(value))
            {
                return htmlTag;
            }
        }
        return null;
    }

    public boolean isHeader()
    {
        return  this == HtmlTag.H1 ||
                this == HtmlTag.H2 ||
                this == HtmlTag.H3 ||
                this == HtmlTag.H4 ||
                this == HtmlTag.H5 ||
                this == HtmlTag.H6 ||
                this == HtmlTag.H7;
    }
}
