
package jomm.web.filter;

/**
 *
 * @author  jmachado
 */

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SetCharacterEncodingFilter implements Filter{

    private static Logger logger = Logger.getLogger(SetCharacterEncodingFilter.class);

    private FilterConfig filterConfig = null;
    private String encoding = null;

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
                         throws
                                IOException, ServletException {


        //try{
           HttpServletRequest hrequest = (HttpServletRequest) request;
           
           String encoding = selectEncoding(request);

           if (encoding != null)
           {
               hrequest.setCharacterEncoding(encoding);
           }
           chain.doFilter(request, response);
        /*}
        catch (IllegalStateException e)
        {
            logger.warn(e);
        }*/

    }
    public void init(FilterConfig filterConfig) throws
           ServletException {
           this.filterConfig = filterConfig;
           this.encoding = filterConfig.getInitParameter("encoding");
    }
    protected String selectEncoding(ServletRequest request) {
           return (this.encoding);
    }

     public void destroy() {
     }

}
