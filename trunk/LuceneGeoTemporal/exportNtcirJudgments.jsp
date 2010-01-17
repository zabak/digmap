<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="java.util.Map"%>
<%

    System.out.println(new java.util.Date() + " : NTCIR EXPORT : session[" + request.getSession().getId() + "] ip[" + request.getRemoteAddr() + "]");
    Map<String, Map<String,String>> relevantTopicDoc = (Map<String, Map<String,String>>) request.getSession().getAttribute("relevantTopicDocs");
    if(relevantTopicDoc == null || relevantTopicDoc.size() == 0)
    {
        response.sendRedirect("indexNtcir.jsp");
    }
    else
    {
        response.setContentType("text/plain");
	
	java.util.List<String> logs = (java.util.List<String>)  request.getSession().getAttribute("logs");	
	if(logs != null)
	{
		for(String log: logs)
			out.print("##LOG:" + log + "\n");
	}

        for(Map.Entry<String, Map<String,String>> entry: relevantTopicDoc.entrySet())
        {
            out.print("#TOPIC$$"+entry.getKey().trim()+"$$TITLE:" + ((Map<String,String>)request.getSession().getAttribute("topics")).get(entry.getKey()).replace("\n", " ").trim() + "\n");
            out.print("#TOPIC$$"+entry.getKey().trim()+"$$DESC:" + ((Map<String,String>)request.getSession().getAttribute("topicsDesc")).get(entry.getKey()).replace("\n", " ").trim() + "\n");
            out.print("#TOPIC$$"+entry.getKey().trim()+"$$NARR:" + ((Map<String,String>)request.getSession().getAttribute("topicsNarr")).get(entry.getKey()).replace("\n", " ").trim() + "\n");
            for(Map.Entry<String,String> entryJudgment: entry.getValue().entrySet())
            {
                out.print(entry.getKey() + " 0 " + entryJudgment.getKey() + " " + entryJudgment.getValue() + "\n");
            }
        }
    }
%>
