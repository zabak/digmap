package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;


/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class RectParentisis extends InsideQuery implements IQuery
{

    public String toString()
    {
        return "[" + super.toString() + "]";
    }

    public String toStringHighLight()
    {
        return toStringToPresent();
    }


    public String toString(String[] notAdmitedIndexes)
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toString();
        }
        return toReturn.trim();
    }

    public String toStringToPresent()
    {
        return "[" + super.toStringToPresent() + "]";
//        try{
//            String range = super.toString();
//            String[] rangeFields = range.split(" ");
//            if(rangeFields.length != 3)
//                return toString();
//            else if(rangeFields[1].equals("TO"))
//            {
//                long from = Long.parseLong(rangeFields[0]);
//                long to = Long.parseLong(rangeFields[2]);
//                Date dfrom = new Date(from);
//                Date dto = new Date(to);
//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                return "[" + df.format(dfrom) + " TO " + df.format(dto) + "]";
//            }
//        }
//        catch(Exception e)
//        {
//            logger.info(e,e);
//        }
//        return toString();
    }

//    public void setObjects(List objects)
//    {
//        if(objects.size() == 3
//                && objects.get(1) instanceof Logical
//                && objects.get(0) instanceof Term
//                && objects.get(2) instanceof Term)
//        {
//            try
//            {
//                GregorianCalendar gcStart = Dates.getGregorianCalendar(((Term)objects.get(0)).getValue(),true);
//                GregorianCalendar gcEnd = Dates.getGregorianCalendar(((Term)objects.get(2)).getValue(),false);
//                ((Term)objects.get(0)).setValue("" + gcStart.getTimeInMillis());
//                ((Term)objects.get(2)).setValue("" + gcEnd.getTimeInMillis());
//            }
//            catch(Exception e)
//            {
//                logger.debug(e);
//            }
//        }
//        this.objects = objects;
//    }


}
