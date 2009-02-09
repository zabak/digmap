package pt.utl.ist.lucene.treceval.geoclef.reader;

import org.dom4j.*;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.StringWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;

import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.geoclef.Globals;
import pt.utl.ist.lucene.forms.UnknownForm;
import pt.utl.ist.lucene.forms.DefaultUknownForm;
import pt.utl.ist.lucene.forms.GeoPoint;
import pt.utl.ist.lucene.forms.RectangleForm;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.gml2.GMLReader;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Jorge Machado
 * @date 10/Nov/2008
 * @see pt.utl.ist.lucene.treceval.geoclef.reader
 */
public class GeoParserResultsIterator
{
    private static final Logger logger = Logger.getLogger(GeoParserResultsIterator.class);

    static GeometryFactory fact = new GeometryFactory();



    int fileIndex = 0;
    GeoParserResult nextRecord = null;
    XPath docs = null;
    List<Element> docElems = null;
    File[] files = null;
    File nowFile = null;
    int elemsIndex = 0;
    boolean eofs = false;

    GeoParserResultsIterator missingDocsIterator = null;

    public GeoParserResultsIterator(String geoParsedResultsFolder)
    {
        init(geoParsedResultsFolder,true);
    }
    public GeoParserResultsIterator(String geoParsedResultsFolder,boolean checkMissingFolder)
    {
        init(geoParsedResultsFolder,checkMissingFolder);
    }
    private void init(String geoParsedResultsFolder,boolean checkMissingFolder)
    {
        if(checkMissingFolder && new File(geoParsedResultsFolder + "-missing").exists())
        {
            missingDocsIterator = new GeoParserResultsIterator(geoParsedResultsFolder + "-missing");
        }
        files = new File(geoParsedResultsFolder).listFiles();
        generateNextRecord();
    }

    public GeoParserResult next(String docNo)
    {
        docNo = docNo.trim();
        if(!eofs && nextRecord != null && nextRecord.getDocNo().equals(docNo))
        {
            //FOUND DOCNO
            GeoParserResult nextRecordOut = nextRecord;
            generateNextRecord();
            return nextRecordOut;
        }
        //ACTUAL DOCNO IS LOWER WILL INCREASE THE POINTER TO NEXT RECORD
        else if(!eofs && nextRecord.getDocNo().compareTo(docNo) < 0)
        {
            logger.warn("Jumped record: " + nextRecord.getDocNo());
            generateNextRecord();
            while(!eofs && nextRecord != null && nextRecord.getDocNo().compareTo(docNo) < 0)
            {
                logger.warn("Jumped record: " + nextRecord.getDocNo());
                generateNextRecord();
            }
            if(!eofs && nextRecord != null)
            {
                GeoParserResult nextRecordOut = nextRecord;
                generateNextRecord();
                if(nextRecordOut.getDocNo().equals(docNo))
                    return nextRecordOut;
                //REACHED A BIGGER RECORD AND WAS NOT FOUND
                if(missingDocsIterator == null)
                    logger.warn("MISSING_ITERATOR: Missing Record in " + nowFile.getName() + " requested: " + docNo + " reached: " + nextRecordOut.getDocNo() + " that is bigger");
                else
                    logger.warn("Missing Record in " + nowFile.getName() + " requested: " + docNo + " reached: " + nextRecordOut.getDocNo() + " that is bigger, will try Missing iterator");
            }
            else
            {
                //REACHED THE END OF FILE
                if(missingDocsIterator == null)
                    logger.warn("END OF MISSING_ITERATOR: Missing Record in " + nowFile.getName() + " requested: " + docNo);
                else
                    logger.warn("END OF FILE: Missing Record in " + nowFile.getName() + " requested: " + docNo + " positioned in the END will try Missing Iterator");
            }
            if(missingDocsIterator != null)
            {
                return missingDocsIterator.next(docNo);
            }
            return null;
        }
        //DOCNO NOT FOUND IN THIS FILE, ACTUAL DOCNO IS BIGGER, WILL TRY MISSING ITERATOR
        else
        {
            if(missingDocsIterator == null)
            {
                logger.warn("BIGGER MISSING_ITERATOR: Missing Record in " + nowFile.getName() + " requested: " + docNo + " iterator cache record is " + nextRecord.getDocNo() + " that is bigger");
                return null;
            }
            else
            {
                logger.warn("BIGGER Missing Record in " + nowFile.getName() + " requested: " + docNo + " iterator cache record is " + nextRecord.getDocNo() + " that is bigger - will try in Missing Iterator");
                return missingDocsIterator.next(docNo);
            }
        }
    }


    public GeoParserResult next()
    {
        if(!eofs && nextRecord != null)
        {
            GeoParserResult nextRecordOut = nextRecord;
            generateNextRecord();
            return nextRecordOut;
        }
        else
        {
            return null;
        }
    }

    private void generateNextRecord()
    {
        if(docElems == null || docElems.size() == elemsIndex)
        {
            if(fileIndex >= files.length)
            {
                eofs=true;
                return;
            }
            nowFile = files[fileIndex];
            fileIndex++;
            logger.info("CHANGING ITERATOR FILE to: " + nowFile.getName());

            try
            {
                Document dom = Dom4jUtil.parse(nowFile);
                XPath xPathDocs = dom.createXPath("//local:DOC");
                xPathDocs.setNamespaceURIs(Globals.namespacesOutput);
                docElems = xPathDocs.selectNodes(dom);
                elemsIndex = 0;
                parseRecord();
                elemsIndex++;
            }
            catch (DocumentException e)
            {
                logger.error(e,e);
            }
            catch (MalformedURLException e)
            {
                logger.error(e,e);
            }
        }
        else
        {
            parseRecord();
            elemsIndex++;
        }

    }

    private void parseRecord()
    {
        Element nowElement = docElems.get(elemsIndex);
        nextRecord = new GeoParserResult();
        XPath xPathDocno = nowElement.createXPath("./@DOCNO");
        xPathDocno.setNamespaceURIs(Globals.namespacesOutput);
        Node docnoElem = xPathDocno.selectSingleNode(nowElement);
        nextRecord.setDocNo(docnoElem.getText().trim());

        XPath xPathTerms = nowElement.createXPath(".//gp:PlaceName//gp:TermName");
        xPathTerms.setNamespaceURIs(Globals.namespacesOutput);
        List<Node> terms = xPathTerms.selectNodes(nowElement);
        List<String> termsStrs = new ArrayList<String>();
        for(Node termNode:terms)
        {
            if(termNode.getText() != null && termNode.getText().length() > 0)
                termsStrs.add(termNode.getText());
        }
        nextRecord.setPlaces(termsStrs);


//        XPath xPathGeneralPoint = nowElement.createXPath("./gp:GeoparseResult/gp:General//gml:Point/gml:coordinates");
//        xPathGeneralPoint.setNamespaceURIs(Globals.namespacesOutput);
//        Element generalPointCoordinates = (Element) xPathGeneralPoint.selectSingleNode(nowElement);
//
//        XPath xPathGeneralScopeRing = nowElement.createXPath("./gp:GeoparseResult/gp:General/gp:GeographicScope//gml:LinearRing//gml:coordinates");
//        xPathGeneralScopeRing.setNamespaceURIs(Globals.namespacesOutput);
//        Element generalScopeCoordinates = (Element) xPathGeneralScopeRing.selectSingleNode(nowElement);


        XPath xPathGeneral = nowElement.createXPath("./gp:GeoparseResult/gp:General//gp:GeographicScope//gml:Polygon | ./gp:GeoparseResult/gp:General//gp:GeographicScope//gml:Point");
        xPathGeneral.setNamespaceURIs(Globals.namespacesOutput);
        Element scope = (Element) xPathGeneral.selectSingleNode(nowElement);
        if(scope!=null)
        {
            StringWriter sw = new StringWriter();
            try
            {
                Dom4jUtil.write(scope,sw);
                GMLReader reader = new GMLReader();
                Geometry geo = reader.read(sw.toString().replace("xmlns:gml=\"http://www.opengis.net/gml\"",""), fact);
                Envelope envelope = geo.getEnvelopeInternal();
                UnknownForm unknownForm;
                if(envelope.getMinX() != envelope.getMaxX() || envelope.getMinY() != envelope.getMaxY())
                {
                    unknownForm = new RectangleForm(envelope.getMaxX(),envelope.getMinY(),envelope.getMinX(),envelope.getMaxY(),new GeoPoint(geo.getCentroid().getX(), geo.getCentroid().getY()));
                }
                else
                {
                    unknownForm = new DefaultUknownForm(new GeoPoint(geo.getCentroid().getX(), geo.getCentroid().getY()),0);
                }
                nextRecord.setGenericUnknownForm(unknownForm);
            }
            catch (IOException e)
            {
                logger.error(e,e);
            }
            catch (ParserConfigurationException e)
            {
                logger.error(e,e);
            }
            catch (SAXException e)
            {
                logger.error(e,e);
            }
        }


//        if(generalScopeCoordinates != null)
//        {
//
//            String text = generalScopeCoordinates.getText();
//            StringWriter sw = new StringWriter();
//            e1 = serialize((NodeInfo) e1).replace("xmlns:gml=\"http://www.opengis.net/gml\"","");
//            GMLReader reader = new GMLReader();
//            Dom4jUtil.write(generalScopeCoordinates);
//            Geometry geo = reader.read(e1.toString(), fact);
//
//            if(geo.getSRID()<=0) geo.setSRID(4326);
//            return geo;
//
//            rdr.read("LINEAR")
//
//            if(text!=null && text.trim().length() > 0)
//            {
//                String[] points = text.split(" ");
//                List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
//                for(String point: points)
//                {
//                    String[] coords = point.split(",");
//                    if(coords.length != 2)
//                        logger.error("Error in record General Point with donno:" + nextRecord.getDocNo());
//                    else
//                    {
//                        String latitude = coords[0];
//                        String longitude = coords[1];
//                        double lat = Double.parseDouble(latitude);
//                        double lng = Double.parseDouble(longitude);
//                        GeoPoint geoPoint = new GeoPoint(lat, lng);
//                        geoPoints.add(geoPoint);
//                    }
//                }
//
//                double distance = 0;
//                for(GeoPoint geoPoint:geoPoints)
//                {
//                    for(GeoPoint geoPoint2:geoPoints)
//                    {
//                        double distanceCandidate = DistanceUtils.orthodromicDistance(geoPoint.getLng(),geoPoint.getLat(),geoPoint2.getLng(),geoPoint2.getLat());
//                        if(distanceCandidate > distance)
//                            distance = distanceCandidate;
//                    }
//                }
//                //Falta calcular o centroide
////                if(nextRecord.getGenericUnknownForm() == null)
////                    nextRecord.setGenericUnknownForm(new DefaultUknownForm());
//
//            }
//
//        }
//        else if(generalPointCoordinates != null)
//        {
//            String text = generalPointCoordinates.getText();
//            if(text!=null && text.trim().length() > 0)
//            {
//                String[] coords = text.split(",");
//                if(coords.length != 2)
//                    logger.error("Error in record General Point with donno:" + nextRecord.getDocNo());
//                else
//                {
//                    String latitude = coords[0];
//                    String longitude = coords[1];
//                    double lat = Double.parseDouble(latitude);
//                    double lng = Double.parseDouble(longitude);
//                    nextRecord.setGenericUnknownForm(new DefaultUknownForm(new GeoPoint(lat, lng),0));
//                }
//            }
//        }
//        else
//        {
//            XPath xPathGeneralScope = nowElement.createXPath("./gp:GeoparseResult/gp:General/gp:GeographicScope//gml:coordinates");
//            xPathGeneralScope.setNamespaceURIs(Globals.namespacesOutput);
//            generalScopeCoordinates = (Element) xPathGeneralScope.selectSingleNode(nowElement);
//            if(generalScopeCoordinates != null)
//            {
//                String text = generalScopeCoordinates.getText();
//                if(text!=null && text.trim().length() > 0)
//                {
//                    String[] points = text.split(" ");
//                    List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
//                    for(String point: points)
//                    {
//                        String[] coords = point.split(",");
//                        if(coords.length != 2)
//                            logger.error("Error in record General Point with donno:" + nextRecord.getDocNo());
//                        else
//                        {
//                            String latitude = coords[0];
//                            String longitude = coords[1];
//                            double lat = Double.parseDouble(latitude);
//                            double lng = Double.parseDouble(longitude);
//                            GeoPoint geoPoint = new GeoPoint(lat, lng);
//                            geoPoints.add(geoPoint);
//                        }
//                    }
//
//                    double distance = 0;
//                    for(GeoPoint geoPoint:geoPoints)
//                    {
//                        for(GeoPoint geoPoint2:geoPoints)
//                        {
//                            double distanceCandidate = DistanceUtils.orthodromicDistance(geoPoint.getLng(),geoPoint.getLat(),geoPoint2.getLng(),geoPoint2.getLat());
//                            if(distanceCandidate > distance)
//                                distance = distanceCandidate;
//                        }
//                    }
//                    //Falta calcular o centroide
////                if(nextRecord.getGenericUnknownForm() == null)
////                    nextRecord.setGenericUnknownForm(new DefaultUknownForm());
//
//                }
//
//            }
//        }
    }

    public static void main(String [] args)
    {
        GeoParserResultsIterator geoParserResultsIterator = new GeoParserResultsIterator(Globals.outputGeoParseDir + "\\gh95");
        int i = 0;
        GeoParserResult geoParserResult = geoParserResultsIterator.next();
        while((geoParserResult = geoParserResultsIterator.next()) !=null)
        {
            System.out.println((i++) + ":" + geoParserResult.getDocNo());
        }
    }
}
