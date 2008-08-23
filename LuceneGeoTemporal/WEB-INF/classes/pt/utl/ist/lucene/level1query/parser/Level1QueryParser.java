package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.level1query.Level1Query;
import pt.utl.ist.lucene.Globals;

import java.util.List;
import java.util.ArrayList;


/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class Level1QueryParser
{

    private long termId = 0;
    List<IQuery> allObjects = new ArrayList<IQuery>();

    QueryParams queryParams = new QueryParams();


    public Level1Query buildQuery(String query)
    {

        List objects = getObjects(query, true, null);

        if (objects.size() > 0 && objects.get(0) instanceof Logical)
        {
            objects.remove(0);
            if (objects.size() > 0 && objects.get(objects.size() - 1) instanceof Logical)
            {
                objects.remove(objects.size() - 1);
            }
        }

        Level1Query q = new Level1Query();
        q.setObjects(objects);
        q.setAllObjects(allObjects);
        q.setQueryParams(queryParams);
        return q;
    }

    public Term getTerm(String term, String index)
    {
        Term t = new Term(term.toLowerCase(), index, termId++);
        allObjects.add(t);
        return t;
    }

    private List<IQuery> getObjects(String query, boolean useIndexField, String index)
    {
        List<IQuery> objects = new ArrayList<IQuery>();
        String merge = "";
        IndexField pendingIndexField = null;
        boolean escape = false;
        for (int i = 0; i < query.length(); i++)
        {
            //******   ""   *********//
            if (query.charAt(i) == '"' && !escape)
            {
                int last = query.indexOf('"', i + 1);
                String insideComa = query.substring(i + 1, last);
                i = last;
                String suffix = getSuffix(query, last);
                i += suffix.length();
                Coma c;
                if (pendingIndexField != null)
                    c = buildComa(insideComa, pendingIndexField.getIndexName(),suffix);
                else
                    c = buildComa(insideComa, index,suffix);
                if (useIndexField && pendingIndexField != null)
                {
                    pendingIndexField.setQuery(c);
                    objects.add(pendingIndexField);
                    pendingIndexField = null;
                }
                else
                    objects.add(c);
            }
            //******   ()   *********//
            else if (query.charAt(i) == '('  && !escape)
            {
                int last = indexOfCloseParentisis(i, query);
                String insideParentisis = query.substring(i + 1, last);
                i = last;
                String suffix = getSuffix(query, last);
                i += suffix.length();
                Parentisis p;
                if (pendingIndexField != null)
                    p = buildParentisis(insideParentisis, pendingIndexField.getIndexName(),suffix);
                else
                    p = buildParentisis(insideParentisis, index,suffix);
                if (useIndexField && pendingIndexField != null)
                {
                    pendingIndexField.setQuery(p);
                    objects.add(pendingIndexField);
                    pendingIndexField = null;
                }
                else
                    objects.add(p);
            }
            else if (query.charAt(i) == '[' && !escape)
            {
                int last = indexOfCloseRectParentisis(i, query);
                String insideParentisis = query.substring(i + 1, last);
                i = last;
                RectParentisis p;
                if (pendingIndexField != null)
                    p = buildRectParentisis(insideParentisis, pendingIndexField.getIndexName());
                else
                    p = buildRectParentisis(insideParentisis, index);
                if (useIndexField && pendingIndexField != null)
                {
                    pendingIndexField.setQuery(p);
                    objects.add(pendingIndexField);
                    pendingIndexField = null;
                }
                else
                    objects.add(p);
            }
            //******   space   *********//
            else if (query.charAt(i) == ' ')
            {
                if (merge.length() > 0)
                {
                    if (merge.equals("AND") || merge.equals("OR") || merge.equals("NOT") || merge.equals("TO"))
                    {
                        Logical l = new Logical();
                        l.setLogical(merge);
                        objects.add(l);
                        merge = "";
                    }
                    else
                    {
                        addTerm(pendingIndexField, merge, index, useIndexField, objects);
                        pendingIndexField = null;
                        merge = "";
                    }
                }
            }
            else if (query.charAt(i) == '\\' && !escape)
            {
                escape = true;
                merge += '\\';
            }
            //******   INDEX   *********//
            else if (useIndexField && query.charAt(i) == ':' && !escape)
            {
                pendingIndexField = new IndexField();
                pendingIndexField.setIndexName(merge);
                merge = "";
            }
            else
            {
                escape = false;
                merge += query.charAt(i);
            }
            merge = merge.trim();
        }
        //******   LAST WORD   *********//
        if (merge.length() > 0)
        {
            addTerm(pendingIndexField, merge, index, useIndexField, objects);
        }
        return objects;
    }

    public String getSuffix(String query, int last)
    {
        if(query.length() > last+1)
        {
            if(query.charAt(last+1) == '^' || query.charAt(last+1) == '~')
            {
                int i;
                for(i = last + 1; i < query.length();i++)
                {
                    if(query.charAt(i) == ' ' || query.charAt(i) == ')')
                    {
                        break;
                    }
                }
                return query.substring(last+1,i);
            }
        }
        return "";
    }

    public int indexOfCloseParentisis(int start, String query)
    {
        return indexOfCloseParentisis(start, query, '(', ')');
    }

    public int indexOfCloseRectParentisis(int start, String query)
    {
        return indexOfCloseParentisis(start, query, '[', ']');
    }

    public int indexOfCloseParentisis(int start, String query, char opener, char closer)
    {

        int opens = 0;
        for (int i = start; i < query.length(); i++)
        {
            if (query.charAt(i) == opener)
                opens++;
            else if (query.charAt(i) == closer)
            {
                opens--;
                if (opens == 0)
                    return i;
            }
        }
        return -1;
    }


    /**
     * Only add Textual Terms to List, Spatial and Time terms will be added only to allObjects List
     *
     * @param pendingIndexField waiting
     * @param merge             text
     * @param index             index field
     * @param useIndexField     to use index level1query terms
     * @param objects           to add terms
     */
    public void addTerm(IndexField pendingIndexField, String merge, String index, boolean useIndexField, List<IQuery> objects)
    {
        Term t;
        if (pendingIndexField != null)
            t = getTerm(merge, pendingIndexField.getIndexName());
        else
            t = getTerm(merge, index);
        if (useIndexField && pendingIndexField != null)
        {
            if(pendingIndexField.getIndexName().equals(Globals.LUCENE_QE_FIELD_QUERY))
            {
                queryParams.setQEEnum(t.getValue());
            }
            else if(pendingIndexField.getIndexName().equals(Globals.LUCENE_ORDER_FIELD_QUERY))
            {
                queryParams.setOrder(t.getValue());
            }
            else if(pendingIndexField.getIndexName().equals(Globals.LUCENE_FILTER_FIELD_QUERY))
            {
                queryParams.setFilter(t.getValue());
            }
            else{
                SpatialEnum spatialEnum = SpatialEnum.parse(pendingIndexField.getIndexName());
                if (spatialEnum != null)
                {
                    if (spatialEnum == SpatialEnum.RADIUM)
                    {
                        queryParams.setRadium(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.RADIUM_MILES)
                    {
                        queryParams.setRadiumMiles(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.RADIUM_KM)
                    {
                        queryParams.setRadiumKm(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.LATITUDE)
                    {
                        queryParams.setLatitude(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.LONGITUDE)
                    {
                        queryParams.setLongitude(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.NORTHLIMIT)
                    {
                        queryParams.setNorthlimit(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.SOUTHLIMIT)
                    {
                        queryParams.setSouthlimit(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.EASTLIMIT)
                    {
                        queryParams.setEastlimit(t.getValue());
                    }
                    else if (spatialEnum == SpatialEnum.WESTLIMIT)
                    {
                        queryParams.setWestlimit(t.getValue());
                    }
                }
                else
                {
                    TimeEnum timeEnum = TimeEnum.parse(pendingIndexField.getIndexName());
                    if (timeEnum != null)
                    {
                        if (timeEnum == TimeEnum.TIME)
                        {
                            queryParams.setTime(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.START_TIME)
                        {
                            queryParams.setStartTime(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.END_TIME)
                        {
                            queryParams.setEndTime(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.TIME_MILISECONDS)
                        {
                            queryParams.setTimeMiliseconds(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.START_TIME_MILISECONDS)
                        {
                            queryParams.setStartTimeMiliseconds(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.END_TIME_MILISECONDS)
                        {
                            queryParams.setEndTimeMiliseconds(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.RADIUM_YEARS)
                        {
                            queryParams.setRadiumYears(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.RADIUM_MONTHS)
                        {
                            queryParams.setRadiumMonths(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.RADIUM_DAYS)
                        {
                            queryParams.setRadiumDays(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.RADIUM_HOURS)
                        {
                            queryParams.setRadiumHours(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.RADIUM_MINUTES)
                        {
                            queryParams.setRadiumMinutes(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.RADIUM_SECONDS)
                        {
                            queryParams.setRadiumSeconds(t.getValue());
                        }
                        else if (timeEnum == TimeEnum.RADIUM_MILISECONDS)
                        {
                            queryParams.setRadiumMiliseconds(t.getValue());
                        }
                    }
                    else
                    {
                        pendingIndexField.setQuery(t);
                        objects.add(pendingIndexField);
                    }
                }
            }
        }
        else
            objects.add(t);
    }


    private List<IQuery> getTerms(String query, String index)
    {
        List<IQuery> objects = new ArrayList<IQuery>();
        String merge = "";
        for (int i = 0; i < query.length(); i++)
        {
            if (query.charAt(i) == ' ')
            {
                if (merge.length() > 0)
                {
                    Term t = getTerm(merge, index);
                    objects.add(t);
                    merge = "";
                }
            }
            else
            {
                merge += query.charAt(i);
            }
            merge = merge.trim();
        }
        if (merge.length() > 0)
        {
            Term t = getTerm(merge, index);
            objects.add(t);
        }
        return objects;
    }

    private Parentisis buildParentisis(String query, String index, String sufix)
    {
        Parentisis p = new Parentisis(sufix);
        List<IQuery> objects = getObjects(query, true, index);
        p.setObjects(objects);
        return p;
    }

    private RectParentisis buildRectParentisis(String query, String index)
    {
        RectParentisis p = new RectParentisis();
        List<IQuery> objects = getObjects(query, true, index);
        p.setObjects(objects);
        return p;
    }

    public Coma buildComa(String query, String index,String suffix)
    {
        Coma c = new Coma(suffix);
        List objects = getTerms(query, index);
        c.setObjects(objects);
        return c;
    }

    public static void main(String[] args)
    {

        Level1Query q = new Level1QueryParser().buildQuery("lat:-38.2323232 lng:2.34432342 dlib.year:\\(year99\\) dlib.year:(year99) AND title:jonhy^0.223 AND creator:joao~3 AND (contents:(ticer))");
        System.out.println(q.toString());

        q = new Level1QueryParser().buildQuery("lat:-38.2323232 lng:2.34432342 (dlib.year:(year99)^0.555 AND dlib.year:\"year99\"^0.555 (contents:[00000 TO 44444]) OR (contents:(ticer)))");
        System.out.println(q.toString());

        q = new Level1QueryParser().buildQuery("dlib.year:(year99) AND (contents:(ticer))");
        System.out.println(q.toString());

        q = new Level1QueryParser().buildQuery("(tese) AND dc.creator:\"lusiada\"");
        System.out.println(q.toString());
        q = new Level1QueryParser().buildQuery("((tese) AND \"lusiada\")");
        System.out.println(q.toString());
        q = new Level1QueryParser().buildQuery("contents:jorge");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("site:jorge AND contents:joao");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("site:(jorge) AND joao");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("site:\"jorge\" NOT (joao)");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("site:(jorge) ORG \"joao\"");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("site:(jorge) joao");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("site:(jorge)joao");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("   site:( jorge  )    joao");
        System.out.println(q);
        q = new Level1QueryParser().buildQuery("   site:( jorge ana)    joao");
        String[] notAdStrings = {"site"};

        System.out.println(">>>>" + q.toString(notAdStrings));


        q = new Level1QueryParser().buildQuery("site:(\"http://purl.pt/1\") AND contents:(lusiadas) AND dc.creator:jorge AND jonhy");
        System.out.println(q);

    }


}
