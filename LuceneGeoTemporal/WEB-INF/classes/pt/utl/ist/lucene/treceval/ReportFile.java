package pt.utl.ist.lucene.treceval;

import java.io.*;

/**
 * @author Jorge
 * @date 24/Abr/2009
 * @time 13:26:14
 */
public class ReportFile
{
    File file;


    public ReportFile(File file)
    {
        this.file = file;
    }

    public ReportResult getResult(String queryId) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        ReportResult reportResult = new ReportResult();
        while((line = reader.readLine())!=null)
        {
            String[] lineElements = line.split("[ \\n\\t]+");
            if(lineElements.length == 3 && lineElements[1].equals(queryId))
            {
                reportResult.ingest(lineElements);
            }
        }
        reader.close();
        return reportResult;
    }
}
