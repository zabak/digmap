package pt.utl.ist.lucene.treceval.geotime;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 6/Jul/2011
 * Time: 11:13:01
 * To change this template use File | Settings | File Templates.
 */
public class MainichiDailyDocument extends KoreaTimesDocument{

    public MainichiDailyDocument(BufferedReader reader, String fileName) throws IOException, EOFException {
        super(reader, fileName);
    }

    public int compare(String id)
    {
        return (getDId()).compareTo(id);
    }

    public boolean isBiggerThan(NyTimesDocument anotherDoc)
    {
        return getFSourceFile().compareTo(anotherDoc.getFSourceFile()) > 0
                ||
                compare(anotherDoc.getDId())>0;
    }
}
