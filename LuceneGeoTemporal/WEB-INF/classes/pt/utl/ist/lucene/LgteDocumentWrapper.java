package pt.utl.ist.lucene;

import org.apache.lucene.document.Document;
import org.apache.solr.util.NumberUtils;
import com.pjaol.search.geo.utils.DistanceUtils;
import pt.utl.ist.lucene.utils.GeoUtils;
import pt.utl.ist.lucene.utils.MyCalendar;
import pt.utl.ist.lucene.forms.*;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.LuceneVersion;

import java.util.Date;

/**
 * Lucene Extenstion Document
 * This document provides a wrapper to manipulate Geo and Temporal
 * Indexes with total transparency
 *
 * @author Jorge Machado
 * @date 14/Ago/2008
 *
 * @see pt.utl.ist.lucene
 *
 *
 */
public class LgteDocumentWrapper
{

    private static LuceneVersion luceneVersion = LuceneVersionFactory.getLuceneVersion();

    Document document;


    public LgteDocumentWrapper(Document doc)
    {
        document = doc;
    }

    public LgteDocumentWrapper()
    {
        document = new Document();
    }

    public Document getDocument()
    {
        return document;
    }

    /**
     * Add a field independently of the lucene version
     * @param field name
     * @param value to index
     */
    public void indexText( String field, String value)
    {
        addField(field, value,true,true,true,true);
    }

    /**
     * Add a field independently of the lucene version
     * @param field name
     * @param value to index
     */
    public void storeUtokenized( String field, String value)
    {
        addField(field, value,true,false,false,false);
    }

    /**
     * Add a field independently of the lucene version
     * @param field name
     * @param value to index
     * @param store in index
     * @param tokenized tokenize terms
     * @param termVector vectorize terms
     * @param index index text
     */
    public void addField( String field, String value,  boolean store,boolean index, boolean tokenized, boolean termVector)
    {
        luceneVersion.addField(document,field,value,store,index,tokenized,termVector);
    }

    /**
     * Add a field independently of the lucene version
     * @param field name
     * @param value to index
     * @param store in index
     * @param tokenized tokenize terms
     * @param index index or not index text
     */
    public void addField( String field, String value,  boolean store, boolean index, boolean tokenized)
    {
        luceneVersion.addField(document,field,value,store,index,tokenized);
    }

    /**
     * Set this document as a Geographic Circumference
     * @param form unkonwn of resource
     */
    public void addUnknownFormField(UnknownForm form)
    {
        //Calc distance between lower left and top right corners
        double diagonal = form.getWidth();
        addGeoPointField(form.getCentroide());
        addField(Globals.LUCENE_DIAGONAL_ORIGINAL_INDEX,""+diagonal,true,false,false);
        addField(Globals.LUCENE_DIAGONAL_INDEX, NumberUtils.double2sortableStr(diagonal),false,true,false,true);
    }

    public GeoPoint getGeoPoint()
    {
        Double latitude = getLatitude();
        Double longitude = getLongitude();
        if(latitude != null && longitude != null)
        {
            return new GeoPoint(latitude,longitude);
        }
        return null;
    }

    public Double getDiagonal()
    {
        return getDoubleField(Globals.LUCENE_DIAGONAL_ORIGINAL_INDEX);
    }
    /**
     * Set this document as a Geographic Circumference
     *
     * @param form unkonwn of resource
     * @return return a DefaultUknownForm instance
     */
    public UnknownForm getUnknownFormField(UnknownForm form)
    {
        Double diagonal = getDoubleField(Globals.LUCENE_DIAGONAL_ORIGINAL_INDEX);
        GeoPoint geoPoint = getGeoPoint();
        if(geoPoint != null && diagonal != null)
            return new DefaultUknownForm(geoPoint,diagonal);
        return null;            
    }

    /**
     * Set this document as a Geographic Circumference
     *
     * @param latitude of resource
     * @param longitude of resource
     * @param radium of resource
     */
    public void addCircleBoxField(double latitude, double longitude, double radium)
    {
        //Calc distance between lower left and top right corners
        double diagonal = radium * 2;
        addGeoPointField(latitude,longitude);
        addField(Globals.LUCENE_DIAGONAL_ORIGINAL_INDEX,""+diagonal,true,false,false);
        addField(Globals.LUCENE_DIAGONAL_INDEX, NumberUtils.double2sortableStr(diagonal),false,true,false,true);
        addField(Globals.LUCENE_RADIUM_ORIGINAL_INDEX,""+radium,true,false,false);
        addField(Globals.LUCENE_RADIUM_INDEX, NumberUtils.double2sortableStr(radium),false,true,false,true);
    }

    /**
     * Set this document as a Geographic Box
     *
     * @param form associated with document
     */
    public void addRectangleField(RectangleForm form)
    {
        addGeoBoxField(form.getNorth(),form.getSouth(),form.getWest(),form.getEast());
    }

    /**
     * Set this document as a Geographic Box
     *
     * @param north limit of resource
     * @param south limit of resource
     * @param west limit of resource
     * @param east limit of resource
     */
    public void addGeoBoxField(double north, double south, double west, double east)
    {
        //Calc distance between lower left and top right corners
        double diagonal = DistanceUtils.getDistanceMi(south, west, north, east);
        double middleLatitude = GeoUtils.calcMiddleLatitude(north, south);
        double middleLongitude = GeoUtils.calcMiddleLongitude(west, east);

        addGeoPointField(middleLatitude,middleLongitude);

        addField(Globals.LUCENE_DIAGONAL_ORIGINAL_INDEX,""+diagonal,true,false,false);
        addField(Globals.LUCENE_NORTHLIMIT_ORGINAL_INDEX,""+north,true,false,false);
        addField(Globals.LUCENE_SOUTHLIMIT_ORGINAL_INDEX,""+south,true,false,false);
        addField(Globals.LUCENE_EASTLIMIT_ORGINAL_INDEX,""+east,true,false,false);
        addField(Globals.LUCENE_WESTLIMIT_ORGINAL_INDEX,""+west,true,false,false);

        addField(Globals.LUCENE_DIAGONAL_INDEX, NumberUtils.double2sortableStr(diagonal),false,true,false,true);
        addField(Globals.LUCENE_NORTHLIMIT_INDEX,NumberUtils.double2sortableStr(north),false,true,false,true);
        addField(Globals.LUCENE_SOUTHLIMIT_INDEX,NumberUtils.double2sortableStr(south),false,true,false,true);
        addField(Globals.LUCENE_EASTLIMIT_INDEX,NumberUtils.double2sortableStr(east),false,true,false,true);
        addField(Globals.LUCENE_WESTLIMIT_INDEX,NumberUtils.double2sortableStr(west),false,true,false,true);
    }

    /**
     * Set this document as a Geographic GeoPoint
     *
     * @param geoPoint spatial geoPoint of resource
     */
    public void addGeoPointField(GeoPoint geoPoint)
    {
        addGeoPointField(geoPoint.getLat(), geoPoint.getLng());
    }

    /**
     * Set this document as a Geographic GeoPoint
     *
     * @param latitude of resource
     * @param longitude of resource
     */
    public void addGeoPointField(double latitude, double longitude)
    {
        addField(Globals.LUCENE_CENTROIDE_LATITUDE_ORIGINAL_INDEX,""+latitude,true,false,false);
        addField(Globals.LUCENE_CENTROIDE_LONGITUDE_ORIGINAL_INDEX,""+longitude,true,false,false);

        addField(Globals.LUCENE_CENTROIDE_LATITUDE_INDEX,NumberUtils.double2sortableStr(latitude),false,true,false,true);
        addField(Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX,NumberUtils.double2sortableStr(longitude),false,true,false,true);
    }

    /**
     * Associate this resource with a timestamp
     *
     * @param year of resource
     */
    public void addTimeField(int year)
    {
        addTimeField(year,1,1,0,0,0);
    }

    /**
     * Associate this resource with a timestamp
     *
     * @param year of resource
     * @param month of year
     */
    public void addTimeField(int year, int month)
    {
        addTimeField(year,month,1,0,0,0);
    }

    /**
     * Associate this resource with a timestamp
     *
     * @param year of resource
     * @param month of year
     * @param day of month
     */
    public void addTimeField(int year, int month, int day)
    {
        addTimeField(year,month,day,0,0,0);
    }

    /**
     * Associate this resource with a timestamp
     *
     * @param year of resource
     * @param month of year
     * @param day of month
     * @param hour of day
     * @param minute of hour
     * @param second of minute
     */
    public void addTimeField(int year, int month, int day, int hour, int minute, int second)
    {
        MyCalendar myCalendar = new MyCalendar(year,month,day,hour,minute,second);
        addTimeField(myCalendar.getTimeInMillis());
    }

    /**
     * Associate this resource with a timestamp
     *
     * @param miliseconds of resource
     */
    public void addTimeField(long miliseconds)
    {
        addField(Globals.LUCENE_TIME_ORIGINAL_INDEX,"" + miliseconds,true,false,false);
        addField(Globals.LUCENE_TIME_INDEX,NumberUtils.long2sortableStr(miliseconds),false,true,false,true);
    }

    /**
     * Set this document as time interval
     *
     * @param fromYear first year
     * @param toYear last year
     */
    public void addTimeBoxField(int fromYear,int toYear)
    {
        addTimeBoxField(fromYear,1,1,toYear,1,1);
    }

    /**
     * Set this document as time interval
     *
     * @param fromYear first year
     * @param fromMonth fist month
     * @param toYear last year
     * @param toMonth last month
     */
    public void addTimeBoxField(int fromYear,int fromMonth, int toYear, int toMonth)
    {
        addTimeBoxField(fromYear,fromMonth,1,0,0,0,toYear,toMonth,1,0,0,0);
    }

    /**
     * Set this document as time interval
     *
     * @param fromYear first year
     * @param fromMonth fist month
     * @param toYear last year
     * @param toMonth last month
     * @param fromDay in month
     * @param toDay in month
     */
    public void addTimeBoxField(int fromYear,int fromMonth, int fromDay, int toYear, int toMonth, int toDay)
    {
        addTimeBoxField(fromYear,fromMonth,fromDay,0,0,0,toYear,toMonth,toDay,0,0,0);
    }


    /**
     * Set this document as time interval
     *
     * @param fromYear first year
     * @param fromMonth fist month
     * @param fromDay first day
     * @param toYear last year
     * @param toMonth last month
     * @param toDay to last day
     * @param fromHour in day
     * @param fromMinute in hour
     * @param fromSecond in second
     * @param toHour in day
     * @param toMinute in hour
     * @param toSecond in minute
     */
    public void addTimeBoxField(int fromYear,int fromMonth, int fromDay, int fromHour, int fromMinute, int fromSecond,  int toYear, int toMonth, int toDay, int toHour, int toMinute, int toSecond)
    {
        MyCalendar fromCalendar = new MyCalendar(fromYear,fromMonth,fromDay,fromHour,fromMinute,fromSecond);
        MyCalendar toCalendar = new MyCalendar(toYear,toMonth,toDay,toHour,toMinute,toSecond);

        //Calc distance between dates
        long middleMili = (fromCalendar.getTimeInMillis() + toCalendar.getTimeInMillis())/2;
        long width = Math.abs(toCalendar.getTimeInMillis() - fromCalendar.getTimeInMillis());

        addTimeBoxField(middleMili,width/2);
    }

    /**
     * Add new time field and width
     *
     * @param centerMiddleTimeMiliseconds in time
     * @param radium in miliseconds
     */
    public void addTimeBoxField(long centerMiddleTimeMiliseconds, long radium)
    {
        addTimeField(centerMiddleTimeMiliseconds);

        long start = centerMiddleTimeMiliseconds - radium;
        long end = centerMiddleTimeMiliseconds + radium;
        long width = 2*radium;
        addField(Globals.LUCENE_TIMEWIDTH_ORIGINAL_INDEX,""+width,true,false,false);
        addField(Globals.LUCENE_START_TIME_LIMIT_INDEX_ORIGINAL,""+start,true,false,false);
        addField(Globals.LUCENE_END_TIME_LIMIT_INDEX_ORIGINAL,""+end,true,false,false);

        addField(Globals.LUCENE_TIMEWIDTH_INDEX, NumberUtils.long2sortableStr(width),false,true,false,true);
        addField(Globals.LUCENE_START_TIME_LIMIT_INDEX,NumberUtils.long2sortableStr(start),false,true,false,true);
        addField(Globals.LUCENE_END_TIME_LIMIT_INDEX,NumberUtils.long2sortableStr(end),false,true,false,true);
    }



    public String get(String field)
    {
        return document.get(field);
    }

    public Double getLatitude()
    {
        return getDoubleField(Globals.LUCENE_CENTROIDE_LATITUDE_ORIGINAL_INDEX);
    }

    public Double getLongitude()
    {
        return getDoubleField(Globals.LUCENE_CENTROIDE_LONGITUDE_ORIGINAL_INDEX);
    }

    public Double getNorthLimit()
    {
        return getDoubleField(Globals.LUCENE_NORTHLIMIT_ORGINAL_INDEX);
    }

    public Double getSouthLimit()
    {
        return getDoubleField(Globals.LUCENE_SOUTHLIMIT_ORGINAL_INDEX);
    }

    public Double getEastLimit()
    {
        return getDoubleField(Globals.LUCENE_EASTLIMIT_ORGINAL_INDEX);
    }

    public Double getRadium()
    {
        return getDoubleField(Globals.LUCENE_RADIUM_ORIGINAL_INDEX);
    }

    public RectangleForm getRectangleForm()
    {
        Double north = getNorthLimit();
        Double south = getSouthLimit();
        Double west = getWestLimit();
        Double east = getEastLimit();
        if(north != null && south != null && west != null && east != null)
        {
            return new RectangleForm(north,south,west,east);
        }
        return null;
    }

    public CircleForm getCircleForm()
    {
        Double latitude = getLatitude();
        Double longitude = getLongitude();
        Double radium = getRadium();

        if(latitude != null && longitude != null && radium != null)
        {
            return new CircleForm(latitude,longitude,radium);
        }
        return null;
    }

    public Double getWestLimit()
    {
        return getDoubleField(Globals.LUCENE_WESTLIMIT_ORGINAL_INDEX);
    }

    private Double getDoubleField(String fieldName)
    {
        String field = document.get(fieldName);
        if(field == null)
            return null;
        else
            return Double.parseDouble(field);
    }

    public Date getTime()
    {
        String time = document.get(Globals.LUCENE_TIME_ORIGINAL_INDEX);
        if(time != null)
        {
            long mili = Long.parseLong(time);
            return new Date(mili);
        }
        return null;
    }
}