<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="java.util.Map"%>
<%
    response.setContentType("text/plain");
    Map<String, Map<String,String>> relevantTopicDoc = (Map<String, Map<String,String>>) request.getSession().getAttribute("relevantTopicDocs");
    for(Map.Entry<String, Map<String,String>> entry: relevantTopicDoc.entrySet())
    {
        out.print("#TOPIC: " + entry.getKey().trim() + "\n");
        out.print("#TOPIC-TITLE: " + ((Map<String,String>)request.getSession().getAttribute("topics")).get(entry.getKey()).replace("\n", " ").trim() + "\n");
        out.print("#TOPIC-DESC: " + ((Map<String,String>)request.getSession().getAttribute("topicsDesc")).get(entry.getKey()).replace("\n", " ").trim() + "\n");
        out.print("#TOPIC-NARR: " + ((Map<String,String>)request.getSession().getAttribute("topicsNarr")).get(entry.getKey()).replace("\n", " ").trim() + "\n");
        for(Map.Entry<String,String> entryJudgment: entry.getValue().entrySet())
        {
            out.print(entry.getKey() + " 0 " + entryJudgment.getKey() + " " + entryJudgment.getValue() + "\n");
        }
    }
%>