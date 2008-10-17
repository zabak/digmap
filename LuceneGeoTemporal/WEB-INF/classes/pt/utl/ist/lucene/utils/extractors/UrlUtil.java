package pt.utl.ist.lucene.utils.extractors;


import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge Machado jmachado@ext.bn.pt
 * DateMonthYearArticles: 26/Set/2005
 * Time: 15:37:58
 * To change this template use File | Settings | File Templates.
 */
public class UrlUtil {

  /*****************************
   *
   * @param url to normalize
   * @return Thi method recive a complete url and normalize it,
   *
   *    take out last slash if directory
   *    put http:// if dont have it
   *    take out :80 port if exist
   *
   */
  public static String normalizeUrlEnd(String url){
      try{
          if(url == null || url.length() == 0)
              return null;

//          if(url.startsWith("Handler"))
//            return url;
          
          if(!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("ftp://"))
              url = "http://" + url;

          if(url.indexOf("?") < 0 && url.endsWith("/"))
              url = url.substring(0,url.length() -1);





          String originalUrl = url;

          int startdomain = url.indexOf("://") + 3;
          int firstSlash = url.indexOf("/",startdomain);
          String domain;
          if(firstSlash >= 0)
              domain =  url.substring(startdomain,firstSlash);
          else
              domain = url.substring(startdomain);

          if(domain.length() == 0)
              return null;

          int portStart = domain.lastIndexOf(":");
          String portInfo;
          if(portStart > 0){

              if(domain.length() - 1 > portStart){
                  portInfo = domain.substring(portStart+1);
                  domain = domain.substring(0,portStart);
                  try{
                      int port = Integer.parseInt(portInfo);
                      if(port != 80)
                          domain += ":" + port;
                  }catch(NumberFormatException e){
                      return null;
                  }
              }
              else
                  domain = domain.substring(0,portStart);
          }
          if(firstSlash>0)
              return originalUrl.substring(0,startdomain) + domain + originalUrl.substring(firstSlash);
          else
              return originalUrl.substring(0,startdomain) + domain;

      }catch(Exception e){
          System.err.println(e.toString());
          e.printStackTrace();
          return null;
      }
  }

    public static String encodeUrlEnd(String urlStr) throws MalformedURLException
    {
        try {
            return URIUtil.encodePath(urlStr,"UTF-8");
        }
        catch (URIException e)
        {
            e.printStackTrace();
            throw new MalformedURLException(e.toString());
        }
    }

    public static String encodeUrlEnd(String urlStr, String enc) throws MalformedURLException
    {

        try {
          return URIUtil.encodePath(urlStr,enc);
        }
        catch (URIException e)
        {
            e.printStackTrace();
            throw new MalformedURLException(e.toString());
        }

//        try{
//        URL url = new URL(urlStr);
//        String path = url.getPath();
//
//        if(path.length()<2){
//            return urlStr;
//        }
//
//
//        String pathEncoded="";
//        List dirs = Strings.getListStrings(path,"/");
//        Iterator iter = dirs.iterator();
//        while (iter.hasNext()) {
//            String s = (String) iter.next();
//            pathEncoded += "/" + URLEncoder.encode(s,"UTF-8");
//        }
//        if(path.endsWith("/"))
//            pathEncoded +="/";
//
//
//        String query = url.getQuery();
//        if(query!=null)
//            query = "?" + query;
//        else
//            query = "";
//
//        int port = url.getPort();
//        String portStr="";
//        if(port>0)
//            portStr = ":" + port;
//        String urlToReturn = url.getProtocol() + "://" + url.getHost() + portStr + pathEncoded + query;
//        return urlToReturn;
//        }
//        catch(UnsupportedEncodingException e)
//        {
//            e.printStackTrace();
//            return urlStr;
//        }
    }

    public static String getPresentableUrl(String url)
    {
        if(url == null)
            return null;
        if(url.length() < 120)
            return url;

        String urlAux = url;
        int start = url.indexOf("://");
        if(start >= 0)
        {
            urlAux = url.substring(start+3);
        }

        int firstSlash = urlAux.indexOf('/');
        int lastSlash = urlAux.lastIndexOf('/');
        if(firstSlash == lastSlash)
            return urlAux;
        else
            return url.substring(0,start + 3) + urlAux.substring(0,firstSlash) + "/..." + urlAux.substring(lastSlash);
    }
  public static void main(String [] args) throws MalformedURLException, UnsupportedEncodingException {
//      System.out.println(getPResentableUrl("http://www.ipportalegre.pt/html1/4Acesso%20ao%20Ensino%20Superior/4Concursos%20Especiais%20de%20Acesso/maiores%20de%2023%20anos/5Entrevistas/ESAE_maiores23_%20entrevistas%20_final.pdf"));
  }
}
