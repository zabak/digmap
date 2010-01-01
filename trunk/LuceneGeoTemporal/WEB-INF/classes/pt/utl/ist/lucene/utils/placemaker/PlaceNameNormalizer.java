package pt.utl.ist.lucene.utils.placemaker;

/**
 * @author Jorge Machado
 * @date 1/Jan/2010
 * @time 23:11:22
 * @email machadofisher@gmail.com
 */
public class PlaceNameNormalizer {

    /**
     * Example:  normalizePlaceName("Marina del Rey","2445713") = MarinaDelRey@WOEID-2445713
     *
     * @param name
     * @param woeid
     * @return
     */
    public static String normalizePlaceName(String name, String woeid)
    {
        name = name.toLowerCase();
        StringBuilder nameBuilder = new StringBuilder();
        boolean eatChar = false;
        for(int i = 0;i < name.length();i++)
        {
            char c = name.charAt(i);
            if(c == ',')
                break;
            if(i == 0)
                nameBuilder.append(("" + c).toUpperCase());
            else if(c == ' ')
                eatChar = true;
            else if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
            {
                if(eatChar)
                    nameBuilder.append(("" + c).toUpperCase());
                else
                    nameBuilder.append(c);
                eatChar = false;
            }
            else
                eatChar = true;
        }
        nameBuilder.append("@WOEID-" + woeid);
        return nameBuilder.toString();
    }

    public static String normalizeWoeid(String woeid)
    {
        return "WOEID-" + woeid;
    }

    public static void main(String[] args)
    {
        System.out.println(normalizePlaceName("Marina del Rey","2445713"));
    }
}
