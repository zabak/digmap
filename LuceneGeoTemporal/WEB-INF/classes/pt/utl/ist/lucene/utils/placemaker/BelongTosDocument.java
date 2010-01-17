package pt.utl.ist.lucene.utils.placemaker;

import org.dom4j.Document;
import org.dom4j.XPath;
import org.dom4j.Element;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 15/Jan/2010
 * @time 19:11:20
 * @email machadofisher@gmail.com
 */
public class BelongTosDocument
{
//    static Map<String,Integer> typeMap = new HashMap<String,Integer>();
//    static Map<String,Integer> belongToMap = new HashMap<String,Integer>();
//    static Map<String,Integer> nameMap = new HashMap<String,Integer>();
//
//    String woeid;
//    long number = 559490689;
//    int[] belongToWoeidIndex;
//    int[] nameIndex;
//    int[] typeIndex;

    private String woeid;
    List<Place> belongTos = null;
    Document dom;


    public BelongTosDocument(String woeid, Document dom)
    {
        Map<String,String> namespaces = new HashMap<String,String>();
        namespaces.put("y","http://where.yahooapis.com/v1/schema.rng");
        this.woeid = woeid;
        XPath xPath = dom.createXPath("//y:place");
        xPath.setNamespaceURIs(namespaces);
        List<Element> places = xPath.selectNodes(dom.getRootElement());
        if(places != null)
        {
            belongTos = new ArrayList<Place>();
            for(Element pElem: places)
            {
                Place p = new Place();
                XPath xPathWoeid = dom.createXPath("./y:woeid");
                xPathWoeid.setNamespaceURIs(namespaces);
                XPath xPathPlaceTypeName  = dom.createXPath("./y:placeTypeName");
                xPathPlaceTypeName.setNamespaceURIs(namespaces);
                XPath xPathName = dom.createXPath("./y:name");
                xPathName.setNamespaceURIs(namespaces);

                p.setWoeid(((Element)xPathWoeid.selectSingleNode(pElem)).getTextTrim());
                p.setName(((Element)xPathName.selectSingleNode(pElem)).getTextTrim());
                Element type = ((Element)xPathPlaceTypeName.selectSingleNode(pElem));
                p.setType(type.getTextTrim());
                p.setTypeCode(Integer.parseInt(type.attribute("code").getValue()));
                belongTos.add(p);
            }
        }
    }

    public Document getDom() {
        return dom;
    }

    public void setDom(Document dom) {
        this.dom = dom;
    }

    public String getWoeid() {
        return woeid;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

    public List<Place> getBelongTos() {
        return belongTos;
    }

    public void setBelongTos(List<Place> belongTos) {
        this.belongTos = belongTos;
    }

    public static class Place
    {
        private int typeCode;
        private String type;
        private String woeid;
        private String name;


        public Place() {
        }

        public Place(int typeCode, String type, String woeid, String name) {
            this.typeCode = typeCode;
            this.type = type;
            this.woeid = woeid;
            this.name = name;
        }

        public int getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(int typeCode) {
            this.typeCode = typeCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWoeid() {
            return woeid;
        }

        public void setWoeid(String woeid) {
            this.woeid = woeid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args)
    {
//        int[] teste = new int[]
    }

}

