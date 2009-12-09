package experiments;

import java.util.*;
import java.text.*;
import org.apache.log4j.*;

public class OpenGisDate implements Cloneable {
    
	private Date startTime = null; //Time value on ISO format [ eg: 1995-12-01/2000-09-31/P1D ]
    private Date endTime = null;
    private Date currentTime = null;
    
    private int periodTimes = 0;
    private char periodType;
    private static final String TIME_SEPARATOR = "/";
    
    public static final int MONTHLY_PERIOD = 0;
    public static final int DAILY_PERIOD = 1;
    public static final int HOURLY_PERIOD = 2;
    
    public OpenGisDate clone() {
    	OpenGisDate aux = new OpenGisDate(startTime,endTime,periodTimes,periodType);
    	return aux;
    }
    
    private SimpleDateFormat[] formats = {
        new SimpleDateFormat("yyyy"),
    	new SimpleDateFormat("yyyy-MM"),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"),
        new SimpleDateFormat("yyyy-MM-dd"),
        new SimpleDateFormat("yy-MM-dd'T'HH:mm:ss"),
        new SimpleDateFormat("yy-MM-dd'T'HH:mm"),
        new SimpleDateFormat("yy-MM-dd"),
        new SimpleDateFormat("yyyy G"),
        new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z"),
        new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa"),
        new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")
    };
    
    private SimpleDateFormat printFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    public OpenGisDate(Date sTime, Date eTime) {
        startTime = sTime;
        currentTime = sTime;
        endTime = eTime;
        periodTimes = 1;
        periodType = 'D';
    }
    
    public OpenGisDate(Date sTime, Date eTime, int pTimes, char pType) {
        startTime = sTime;
        currentTime = sTime;
        endTime = eTime;
        periodTimes = pTimes;
        periodType = pType;
    }
    
    public OpenGisDate(String s1, String s2) throws Exception {
    	startTime = parseTime(s1,null);
        currentTime = startTime;
        if(s2==null) endTime = parseTime(s1,startTime);
        else endTime = parseTime(s2,startTime);
        periodTimes = 1;
        periodType = 'D';
    }
    
    public OpenGisDate(String t) throws Exception {
        if ( t == null || t == "" ) {
            throw  (new Exception("No valid OWS request [\"TIME\" missing]"));
        }
        try {
            StringTokenizer times = new StringTokenizer(t, TIME_SEPARATOR);
            startTime = parseTime(times.nextToken().trim(), null );   
            if ( times.hasMoreTokens() ) {
                endTime = parseTime(times.nextToken().trim(), startTime );
            } else { endTime = startTime; }
            if ( times.hasMoreTokens() ) {
                //PT1H
                String tempPeriod = times.nextToken().trim();
                if ( tempPeriod.charAt(0) != 'P' ) {
                    throw (new  Exception("Request bad formed! [invalid \"TIME\": " + t + "]"));
                } else if ( tempPeriod.charAt(1) == 'T' ) {
                    periodType = tempPeriod.charAt(tempPeriod.length() - 1);
                    periodTimes =  Integer.parseInt(tempPeriod.substring(2, tempPeriod.length() - 1) );
                } else {
                    periodType =  tempPeriod.charAt(tempPeriod.length() - 1);
                    periodTimes =  Integer.parseInt(tempPeriod.substring(1, tempPeriod.length() - 1) );
                }
                if ( periodType != 'D' && periodType != 'M' &&  periodType != 'Y' &&  periodType != 'H' ) {
                    throw (new  Exception("Request bad formed! [invalid \"TIME\": " + t + "]"));
                }
            } else {
                periodTimes = 1;
                periodType = 'D';
                
            }
            currentTime = startTime;
        } catch (NumberFormatException nfE) {
            throw (new  Exception("No valid integer for period of \"TIME\" attribute [" + t + "]"));
        }
    }
    
    public Date getStartTime() { return startTime; }
    
    public Date getEndTime() { return endTime; }
	
    public Date getStartDate() { return startTime; }
	
    public Date getEndDate() { return endTime; }
    
    public void setStartDate( Date startTime ) { this.startTime = startTime; }
	
    public void setEndDate( Date endTime ) { this.endTime = endTime; }
    
    public int getPeriod(int pType) {
        if ( (periodType == 'H' && pType == HOURLY_PERIOD) || (periodType == 'D' && pType == DAILY_PERIOD) || (periodType == 'M' && pType == MONTHLY_PERIOD) ) {
            return periodTimes;
        } else if ( periodType == 'H' && pType == DAILY_PERIOD )
            return (periodTimes/24 == 0)? 1: (int)(periodTimes/24);
            if ( periodType == 'H' && pType == MONTHLY_PERIOD )
                return (periodTimes/(24 * 30) == 0)? 1: (int)(periodTimes/(24 * 30));
            else if ( periodType == 'D' && pType == MONTHLY_PERIOD )
                return (periodTimes/30 == 0)? 1: (int)(periodTimes/30);
            else if ( periodType == 'D' && pType == HOURLY_PERIOD )
                return periodTimes * 24;
            else if ( periodType == 'M' && pType == DAILY_PERIOD )
                return periodTimes * 30;
            else if ( periodType == 'M' && pType == HOURLY_PERIOD )
                return periodTimes * 30 * 24;
            return periodTimes;
    }
    
    public void reset() { currentTime = startTime; }
    
    public Date getNextTime() { return getNextTime(periodTimes); }
    
    public Date getNextTime(int times) {
        Date d =  ( currentTime.after(endTime) )? null: currentTime;
        Calendar calendar = printFormat.getCalendar();
        calendar.setTime(currentTime);
        if ( periodType == 'D' )
            calendar.add(Calendar.DAY_OF_MONTH, times);
        else if ( periodType == 'M' )
            calendar.add(Calendar.MONTH, times);
        else if ( periodType == 'Y' )
            calendar.add(Calendar.YEAR, times);
        else if ( periodType == 'H' )
            calendar.add(Calendar.HOUR_OF_DAY, times);
        else {
            return null;
        }
        currentTime = calendar.getTime();        
        return d;
    }

    public OpenGisDate getNextRange() throws Exception { return getNextRange(periodTimes); }
    
    public OpenGisDate getNextRange(int times) throws Exception {
        Date sDate = ( currentTime.after(endTime) )? null: currentTime;
        Calendar calendar = printFormat.getCalendar();
        calendar.setTime(currentTime);
        if ( periodType == 'D' )
            calendar.add(Calendar.DAY_OF_MONTH, times);
        else if ( periodType == 'M' )
            calendar.add(Calendar.MONTH, times);
        else if ( periodType == 'Y' )
            calendar.add(Calendar.YEAR, times);
        else if ( periodType == 'H' )
            calendar.add(Calendar.HOUR_OF_DAY, times);
        else {
            return null;
        }
        currentTime = calendar.getTime();
        Date eDate = ( currentTime.after(endTime) )? null: currentTime;
        if ( sDate == null || eDate == null ) return null;
        else return new OpenGisDate(sDate, eDate, periodTimes, periodType);
    }
    
    public int nSteps(int s) {
        long t = endTime.getTime() - startTime.getTime();
        if ( periodType == 'D' )
            t = t/(24 * 60 * 60 * 1000);
        else if ( periodType == 'M' )
            t = t/(24 * 60 * 60 * 31 * 1000);
        else if ( periodType == 'Y' )
            t = t/(24 * 60 * 60 * 31 * 356 * 1000);
        else if ( periodType == 'H' )
            t = t/(60 * 60 * 1000);
        return Math.max(1, (int)(t/s));
    }
    
    public long overlap ( OpenGisDate date ) {
    	if (date.endTime.before(this.startTime) || date.startTime.after(this.endTime)) return 0;
    	return diference (date.getCenterDate());
    }
    
    public long diference ( OpenGisDate date ) {
    	return diference (date.getCenterDate());
    }
    
    public long diference ( Date date ) {
    	Date aux = getCenterDate();
    	return aux.getTime() - date.getTime();
    }
    
    public Date getCenterDate () {
    	long aux1 = startTime.getTime();
    	long aux2 = endTime.getTime();
    	return new Date((long)((aux1+aux2)/2));
    }
    
    public long getDuration () {
    	long aux1 = startTime.getTime();
    	long aux2 = endTime.getTime();
    	return Math.abs(aux2-aux1);
    }
    
    public String toString() { return toString(printFormat); }
    
    public String toString(SimpleDateFormat dateFormat) {
        StringBuffer buff = new StringBuffer();
        buff.append(dateFormat.format(startTime));
        buff.append("/");
        buff.append(dateFormat.format(endTime));
        buff.append("/");
        buff.append("PT");
        buff.append(periodTimes);
        buff.append(periodType);
        return new String(buff);
    }
    
    
    private Date parseTime(String t, Date otherDate) throws Exception {
        Date time = null;
        boolean found = false;
        int i = 0;
        t = t.replace("a.c.","AD").replace("b.c.","BC");
        while ( i < formats.length && !found ) {
            try {
                time = formats[i].parse(t);
                found = true;
            } catch (ParseException pE) { i++; }
        }
        if ( time == null ) {
            throw (new  Exception("No valid time format for \"TIME\" [" + t + "]"));
        } else if(otherDate!=null){
        	if(otherDate.getSeconds()==time.getSeconds()) {
        		time.setSeconds(59);
            	if(otherDate.getMinutes()==time.getMinutes()) {
            		time.setMinutes(59);
                	if(otherDate.getHours()==time.getHours()) {
                    	time.setHours(23);
                    	if(otherDate.getDate()==time.getDate()) {
                        	time.setDate(31);
                        	if(otherDate.getMonth()==time.getMonth()) time.setMonth(11);
                    	}
                	}
            	}
        	}
        }
        return time;
    }
    
    public static void main ( String args[] ) throws Exception {
    	System.out.println(new OpenGisDate("1971 a.c.",null).toString());
    }

}
