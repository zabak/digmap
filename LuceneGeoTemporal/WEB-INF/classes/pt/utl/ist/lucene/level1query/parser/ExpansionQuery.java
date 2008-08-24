package pt.utl.ist.lucene.level1query.parser;


import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.level1query.Level1Query;

import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;

/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class ExpansionQuery
{

    Level1Query query;

    public ExpansionQuery(String query)
    {
        this.query = new Level1QueryParser().buildQuery(query);
    }

    public ExpansionQuery(Level1Query query)
    {
        this.query = query;
    }

    public String expand()
    {

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        if (query.getObjects().size() == 0)
            return "";

        for (Object obj : query.getObjects())
        {
            if (!(obj instanceof Term))
                return query.toString();
            if (!first)
                sb.append(' ');
            sb.append(obj.toString());
            first = false;
        }

        List<String> query = new ArrayList<String>();
        query.add(sb.toString());
        List<String> values = ConfigProperties.getListValues("expand.rule");
        for (String value : values)
        {
            sb.append(" ").append(MessageFormat.format(value, query.toArray()));
        }
        return sb.toString();

    }


    public Level1Query getQuery()
    {
        return query;
    }

    public void setQuery(Level1Query query)
    {
        this.query = query;
    }
}
