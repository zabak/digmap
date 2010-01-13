package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.metrics.TemporalMetrics;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;

import java.io.IOException;
import java.io.File;
import java.util.List;

import org.apache.solr.util.NumberUtils;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexMetrics {
    public static String indexPath =  Config.indexBase + "\\METRICS_INDEXED";

    public static void main(String[] args) throws IOException {
        new File(indexPath).mkdir();
        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator(Config.documentPath,Config.timexesPath,Config.placemakerPath);
        LgteIndexWriter writer = new LgteIndexWriter(indexPath,new LgteNothingAnalyzer(),true, Model.BM25b);

        int count = 1;
        IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d;
        while((d = iterator.next())!=null)
        {
            indexDocument(writer,d);
            count++;
            if(count % 1000 == 0)
            {
                System.out.println(count + ":" + d.getD().getDId());
            }
        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Config.ID,d.getD().getDId());
        if(d.hasPlaces())
        {
            doc.addGeoBoxField(
                    d.getPm().getBoundingBoxPoint2().getLat(),
                    d.getPm().getBoundingBoxPoint1().getLat(),
                    d.getPm().getBoundingBoxPoint1().getLng(),
                    d.getPm().getBoundingBoxPoint2().getLng(),
                    d.getPm().getGeographicCentroide().getLat(),
                    d.getPm().getGeographicCentroide().getLng());
        }
        if(d.hasTimexes())
        {
            List<TimeExpression> metricTimeExpressions = d.getTd().getAllIndexableTimeExpressions();
            if(metricTimeExpressions.size() > 0)
            {
                try {
                    TemporalMetrics temporalMetrics = new TemporalMetrics(metricTimeExpressions);
                    doc.addTimeBoxFields(d.getTd().getMin().getC().getTime(),d.getTd().getMax().getC().getTime(),temporalMetrics.getTemporalIntervalPointsCentroide2());
                    doc.addField(Config.METRIC_T_CENTROIDE_1, NumberUtils.long2sortableStr(temporalMetrics.getTemporalCentroide().getTime()),false,true,false,true);
                    doc.addField(Config.METRIC_T_CENTROIDE_2, NumberUtils.long2sortableStr(temporalMetrics.getTemporalIntervalPointsCentroide().getTime()),false,true,false,true);
                    doc.addField(Config.METRIC_T_CENTROIDE_3, NumberUtils.long2sortableStr(temporalMetrics.getLeftLimitsCentroideTimeExpression().getC().getTimeInMillis()),false,true,false,true);
                    doc.storeUtokenized(Config.METRIC_T_CENTROIDE_REFS,"" + temporalMetrics.getNumberRefsCentroide());
                } catch (TimeExpression.BadTimeExpression badTimeExpression) {
                    badTimeExpression.printStackTrace();
                }
            }
        }
        writer.addDocument(doc);
    }
}
