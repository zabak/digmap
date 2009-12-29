package pt.utl.ist.lucene.utils.temporal;

import org.dom4j.Element;
import org.dom4j.Attribute;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 17:45:36
 * @email machadofisher@gmail.com
 */

//Example <TIMEX2 set="" rend="2314" val="2005-12-04" tmxclass="point" rstart="2309" dirclass="before" parsenode=".16 w2" prenorm="|dex|W|XXXX-WXX-7">Sunday</TIMEX2>

    /*
    *   from: http://fofoca.mitre.org/annotation_guidelines/2005_timex2_standard_v1.1.pdf
    *   Grammar for VAL
    *   YYYY-MM-DDThh:mm:ss  example 1998-10-08T21:36:42.85
    *   YYYY-MM-DD  Day
    *   YYYY-MM     Month
    *   YYYY        Year
    *   YYY         Decade
    *   YY          Century
    *   Y           Millenium
    *
    *   Thh:mm:ss
    *   Thh:mm
    *   Thh
    *   YYYY-MM-DDThh:mm:ssZ   GMT Time or UTC Time
    *   YYYY-MM-DDThh:mm:ss-XX   GMT Time or UTC Time minus XX hours
    *
    *   YYYY-WXX      Week XX of the year
    *
    *   BCXXXX  Before Current Era XXXX years  for example BC0231 is 231 before Christ
    *   KAXXXX, some number of thousand years (XXXX) ago
    *   GAXXXX, some number of billion years (XXXX) ago
    *   MAXXXX  XXXX Mileniums Ago
    *   0XXX   year XXX
    *
    *
    * When Anchor_VAL has a value and AnchorDir has a value
    *  AnchorVal is the reference point to what val is refering to
    *    like VAL Value
    *
    * Anchor Dir could be:
    *
    * WITHIN
    * STARTING
    * ENDING
    * AS_OF
    * BEFORE
    * AFTER
    *
    *   VAL
    *
    *   PTXH  Pass X hours from anchor val relative to Anchor_Dir
    *   PXY   Pass X years from anchor val
    *   PXM   Pass X months from anchor val
    *   PXD  Pass X days from anchor val
    *   PXW  Pass X weeks from anchor val
    *
    * 
    * */
public class Timex2
{

    private String text;
    private String val;
    private String anchorVal;
    private String anchorDir;
    private int rstart;
    private int rend;
    private String tmxclass;
    private String dirclass;
    private String parsenode;
    private String prenorm;


    public Timex2(String val, String anchorVal) {
        this.val = val;
        this.anchorVal = anchorVal;
    }


    public Timex2(String val, String anchorVal, String anchorDir) {
        this.val = val;
        this.anchorVal = anchorVal;
        this.anchorDir = anchorDir;
    }

    public Timex2(String val) {
        this.val = val;
    }

    public Timex2(Element elem) {
        parse(elem);
    }


    public void parse(Element elem)
    {
        text = elem.getTextTrim();
        Attribute val = elem.attribute("val");
        Attribute anchorVal = elem.attribute("anchor_val");
        Attribute anchorDir = elem.attribute("anchor_dir");
        Attribute rstart = elem.attribute("rstart");
        Attribute rend = elem.attribute("rend");
        Attribute tmxclass = elem.attribute("tmxclass");
        Attribute dirclass = elem.attribute("dirclass");
        Attribute parsenode = elem.attribute("parsenode");
        Attribute prenorm = elem.attribute("prenorm");

        if(val != null)
            this.val = val.getValue();
        if(anchorVal != null)
            this.anchorVal = anchorVal.getValue();
        if(anchorDir != null)
            this.anchorDir = anchorDir.getValue();
        if(rstart != null)
            this.rstart = Integer.parseInt(rstart.getValue());
        if(rend != null)
            this.rend = Integer.parseInt(rend.getValue());
        if(tmxclass != null)
            this.tmxclass = tmxclass.getValue();
        if(dirclass != null)
            this.dirclass = dirclass.getValue();
        if(parsenode != null)
            this.parsenode = parsenode.getValue();
        if(prenorm != null)
            this.prenorm = prenorm.getValue();
    }


    public String getText() {
        return text;
    }

    public String getVal() {
        return val;
    }

    public String getAnchorVal() {
        return anchorVal;
    }

    public String getAnchorDir() {
        return anchorDir;
    }

    public int getRstart() {
        return rstart;
    }

    public int getRend() {
        return rend;
    }

    public String getTmxclass() {
        return tmxclass;
    }

    public String getDirclass() {
        return dirclass;
    }

    public String getParsenode() {
        return parsenode;
    }

    public String getPrenorm() {
        return prenorm;
    }
}
