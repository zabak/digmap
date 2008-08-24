import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

public class SearchNGramsServlet extends HttpServlet {
	
  private SearchNGrams ngrams;
  
  public void init ( ) {
	  String path = SearchNGrams.getProperties().getProperty("index.path");
	  if ( path == null || path.length()==0 ) path = getServletContext().getRealPath("/test-index");
	  ngrams = new SearchNGrams(path);
  }
  
  public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doPost(request,response);
  }

  public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String output = request.getParameter("o");
	  String query = request.getParameter("q");
	  if("json".equals(output)) response.setContentType("text/javascript");
	  if("xml".equals(output)) response.setContentType("text/xml");
	  else response.setContentType("text/plain");
	  if(query==null || query.length()==0) {
		  if("json".equals(output)) response.getWriter().write("{\"error\":"+ true + ",\"message\":\"Please specify the query ngram using the \"q\" parameter\"}");
		  if("xml".equals(output)) response.getWriter().write("<ngram-frequency><error>true</error><message>Please specify the query ngram using the \"q\" parameter</message></ngram-frequency>");
		  else response.getWriter().write("Please specify the query ngram using the \"q\" parameter");
	  } else {
		  response.getWriter().write(ngrams.getNGramFrequency(query,output));
	  }
  }
  
}