package pt.utl.ist.lucene.treceval.geotime;

/**
 * @author Jorge Machado
 * @date 23/Dez/2009
 * @time 9:51:32
 * @email machadofisher@gmail.com
 */
public class Experiment
{

//    public static void main(String[] args) throws Exception
//
//    {
//
////        String path = "F:\\coleccoesIR\\ntcir\\data\\nyt_eng_200201.gz";
//        String path = "D:\\Servidores\\DATA\\ntcir\\nyt_eng_200201.gz";
//        DocumentIterator di = new DocumentIterator(path);
//        Document d;
//        while(!(d=di.next()).getDId().equals("NYT_ENG_20020110.0112"))
//        {
//            System.out.println(d.getDId());
////            System.out.println(d.getSgml());
//        }
//        System.out.println(d.getSgml());
//        String xml = d.getSgml();
//        xml = xml.replace("\\R"," ").replace("\\S"," ").replace("\\T"," ").replace("\\N"," ");
//        di.close();
//        PostMethod post = new PostMethod("http://deptal.estgp.pt:9090/jmachado/TIMEXTAG/index.php");
//
//        HttpClient client = new HttpClient();
//
//            post.addParameter("debug","false");
//            post.addParameter("service", "sgml2Timexes");
//            post.addParameter("t", "1");
//            post.addParameter("input", xml);
//            post.setDoAuthentication( false );
//            client.executeMethod( post );
//            String response = post.getResponseBodyAsString();
//        System.out.println(response);
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setValidating(false);
//                factory.setNamespaceAware(true);
//            org.w3c.dom.Document document;
//            DocumentBuilder loader = factory.newDocumentBuilder();
//            try{
//                document = loader.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
//            }catch(Exception e)
//            {
//                System.out.println("Parsing this XML:");
//                System.out.println(xml);
//                System.out.println("Response from tagger:");
//                System.out.println(response);
//                throw e;
//            }
//            post.releaseConnection();
//
//
//    }

  



}
