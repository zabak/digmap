<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%--

  User: Jorge
  Date: 13/Nov/2009
  Time: 8:35:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<String[]> collections = new ArrayList<String[]>();
    collections.add(new String[]{"","All"});
    collections.add(new String[]{"kbr","Royal Library of Belgium"});
    collections.add(new String[]{"bnp","National Library of Portugal"});
    collections.add(new String[]{"lit","Martynas Mazvydas National Library of Lithuania"});
    collections.add(new String[]{"fra","French National Library"});
    collections.add(new String[]{"bl","The British Library"});
    collections.add(new String[]{"cze","National Library of the Czech Republic"});
    collections.add(new String[]{"ger","German National Library"});
    collections.add(new String[]{"lat","National Library of Latvia"});
    collections.add(new String[]{"rus","The National Library of Russia"});
    collections.add(new String[]{"ser","National Library of Servia"});
    collections.add(new String[]{"spa","National Library of Spain"});
    collections.add(new String[]{"nsz","National Szechenyi Library of Hungary"});

%>

  <table>
      <tr>
          <td colspan="2">Collection:</td>
      </tr>



<%


    for(String[] collection: collections)
    {
        String checked = "";
        if(request.getParameter("collection") == null && collection[1].equals("All"))
            checked = "checked=\"checked\"";
        else if(request.getParameter("collection")!=null && request.getParameter("collection").equals(collection[0]))
            checked = "checked=\"checked\"";
        
%>
        <tr><td><input type="radio" <%=checked%> name="collection" value="<%=collection[0]%>"></td><td><%=collection[1]%></td></tr>
<%
    }
%>

      
  </table>