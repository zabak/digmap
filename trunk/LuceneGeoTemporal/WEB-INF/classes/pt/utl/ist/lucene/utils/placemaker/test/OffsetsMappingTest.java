package pt.utl.ist.lucene.utils.placemaker.test;

import junit.framework.TestCase;

import java.io.IOException;

import org.dom4j.DocumentException;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;
import pt.utl.ist.lucene.treceval.geotime.DocumentIterator;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 2:28:04
 * @email machadofisher@gmail.com
 */
public class OffsetsMappingTest extends TestCase {


    public void testMappings()
    {
        try {
            PlaceMakerIterator placeMakerIterator = new PlaceMakerIterator("D:\\Servidores\\DATA\\ntcir\\PlaceMaker");
            DocumentIterator documentIterator = new DocumentIterator("D:\\Servidores\\DATA\\ntcir\\data");

            NyTimesDocument document;
            PlaceMakerDocument placeMakerDocument;
            while((document = documentIterator.next())!=null)
            {
                System.out.println(document.getDId());
                placeMakerDocument = placeMakerIterator.next();
                if(placeMakerDocument.getPlaceDetails() != null)
                {
                    for(PlaceMakerDocument.PlaceDetails placeDetails : placeMakerDocument.getPlaceDetails())
                    {
                        for(PlaceMakerDocument.PlaceRef placeRef: placeDetails.getRefs())
                        {

                            int start = document.toStringOffset2txtwithoutTagsOffset(placeRef.getStartOffset());
                            int end = document.toStringOffset2txtwithoutTagsOffset(placeRef.getEndOffset());

                            if(placeRef.getStartOffset() < 0 || placeRef.getEndOffset() < 0)
                            {
                                System.out.println("Ref wrong: " + document.getDId() + " " + placeDetails.getName() + " - " + placeRef.getStartOffset()+ " : " + + placeRef.getEndOffset());
                            }
                            else
                            {
                             
                               String original = document.getSgml().replaceAll("<[^>]+>","").substring(start,end).replaceAll("\n"," ").replace(" ","");
                               String other = document.toString().substring(placeRef.getStartOffset(),placeRef.getEndOffset()).replaceAll("\n","").replace(" ","");
                                if(!original.equals(other))
                                    System.out.println(">" + document.getSgml().replaceAll("<[^>]+>","").substring(start,end).replaceAll("\n"," ").replace(" ","") + " - >" + document.toString().substring(placeRef.getStartOffset(),placeRef.getEndOffset()).replaceAll("\n","").replace(" ","")+"<");
                                if(placeRef.getStartOffset() < document.getToStringHeadLineEndOffset() && placeRef.getEndOffset() > document.getToStringHeadLineEndOffset())
                                    assertEquals(document.getSgml().replaceAll("<[^>]+>","").substring(start,document.getHeadLineEndOffset()).replaceAll("\n"," ").replace(" ",""),document.toString().substring(placeRef.getStartOffset(),document.getToStringHeadLineEndOffset()).replaceAll("\n","").replace(" ",""));
                                else
                                    assertEquals(original,other);
                            }
                        }

                    }
                }
            }
        }
        catch (IOException e)
        {
            fail(e.toString());
            e.printStackTrace();
        }
        catch (DocumentException e)
        {
            fail(e.toString());
            e.printStackTrace();
        }
    }

}
