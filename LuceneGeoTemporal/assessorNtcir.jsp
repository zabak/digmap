<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="java.util.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.dao.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.services.*"%>

<%


    List<Topic> topics = DBServer.getTopics("NtcirGeoTime2010");

%>
  <br/>
  <a href="assessmentsNtcir.jsp?op=logout">Logout <%=Server.getUsername(request)%></a> 
  <form style="margin-top:4px" name="AssessmentsForm" action="assessmentsNtcir.jsp" method="post">
    <div class="searchForm">
		<table width="100%">
			<tr>
				<td nowrap="nowrap" colspan="2">
					Choose a topic to evaluate: ( <a href="javascript:alert('Choose a Topic, and mark the documents as relevant, partially relevant or irrelevant, use the Annotations Link to see the places and the time expressions marked with colors in the document body')">help</a> )

					<select name="id_topic">
							<option value=""></option>
						<%
							for(Topic topic : topics)
							{
								String checked = "";
								if(request.getParameter("id_topic") != null && topic.getIdTopic().equals(request.getParameter("id_topic")))
								{
									checked = "selected";
								}
						%>
								<option onclick="this.form.submit()" <%=checked%>  value="<%=topic.getIdTopic()%>"> <%=topic.getIdTopic()%> - <%=topic.getDescription()%></option>
						<%
							}
						%>
					</select>
				</td>
				<td>
					<script language="JavaScript" type="text/javascript">
						<!--
							function showTopicNarr(topicId)
							{
								<%
									for(Topic topic : topics)
							        {
								%>
										if(topicId == '<%=topic.getIdTopic()%>')
										{
											alert("TOPIC: <%=topic.getIdTopic()%> \n\nDESC: <%=topic.getDescription()%> \n\nNARR: <%=topic.getNarrative()%> \n");
										}
								<%
									}
								%>
							}
						-->
					</script>
					<input type="button" value="see topic info" onclick="showTopicNarr(this.form.id_topic.value);"><br/>
					<input type="submit"><br/>
				</td>
			</tr>
		</table>
	</div>
   </form>

<%
    if(request.getParameter("id_topic")!=null && request.getParameter("id_topic").trim().length() >= 0)
    {
        int i = 0;
        List<TopicDoc> topicDocs = DBServer.getTopicDocs(request.getParameter("id_topic"));
        if(topicDocs.size() > 0)
        {

         %>
    <form action="assessmentsNtcir.jsp?op=addJudgments" method="post">
        <input type="hidden" name="id_topic" value="<%=request.getParameter("id_topic")%>">
        <input style="background-color:yellow;padding:3px;" type="button" onclick="this.form.submit()" value="Click here to confirm your Judgements after you choose the relevance of the documents"/>
        <%
            for (TopicDoc topicDoc: topicDocs)
            {
                    i++;
                    String style = "";
                    String relevance = "";
                    String relevantSelected = "";
                    String partiallyRelevantSelected = "";
                    String irrelevantSelected = "";

                    if(topicDoc.getRelevance().equals("irrelevant"))
                    {
                            style = "style=\"padding:5px;background-color:red\"";
                            irrelevantSelected = " selected";
                    }
                    else if(topicDoc.getRelevance().equals("partially-relevant"))
                    {
                            style = "style=\"padding:5px;background-color:yellow\"";
                            partiallyRelevantSelected = " selected";
                    }
                    else if(topicDoc.getRelevance().equals("relevant"))
                    {
                            style = "style=\"padding:5px;background-color:green\"";
                            relevantSelected = " selected";
                    }

                    String docno = topicDoc.getDocno();
                    out.print("<a name=\"" + docno + "\"></a>\n");
                    out.print("<table width=\"100%\"><tr>");



                    out.print("<td " + style + " id=\"table" + docno + "\">");
                    String title = topicDoc.getDocTitle();
                    if (title != null)
                        out.print("<h3>" + i + " - " + title + " - (DOCNO: " + docno + ")</h3>\n");
                    else
                        out.print("<h3>" + i + " - (DOCNO: " + docno + ")</h3>\n");

                    out.print("<p><b>score</b>: " + topicDoc.getScore() + "</p>\n");
                    out.print("<p style=\"background-color:white\"><a onclick=\"getObjectById('table" + docno + "').style.backgroundColor='lightgray';return !showPopup('doc" + i + "', event);\" href=\"#\">Show Anotations</a></p>\n");
                    out.print("<p style=\"background-color:white\"><a onclick=\"return !showPopup('docInfo" + i + "', event);\" href=\"#\">Show Assessments Info</a></p>\n");
                    out.print("</td>\n");
                    out.print("<td align=\"right\" valign=\"top\">\n");
                     %>
                    <table>
                        <tr>
                            <td></td>
                            <td>
                                <select name="<%=docno%>" onchange="if(this.value == 'relevant'){getObjectById('table<%=docno%>').style.backgroundColor='green';}else if(this.value == 'irrelevant'){getObjectById('table<%=docno%>').style.backgroundColor='red';} else if(this.value == 'partially-relevant'){getObjectById('table<%=docno%>').style.backgroundColor='yellow';}" name="<%=docno%>">
                                    <option value="">Choose relevance</option>
                                    <option value="relevant" <%=relevantSelected%>>relevant</option>
                                    <option value="partially-relevant" <%=partiallyRelevantSelected%>>partially relevant</option>
                                    <option value="irrelevant" <%=irrelevantSelected%>>irrelevant</option>
                                </select>
                            </td>
                         </tr>
                         <tr>
                            <td>Obs.</td>
                            <td>
                                <textarea cols="50" rows="5" name="obs<%=docno%>"></textarea>
                                <br>
                                Use this box to add comments about your assessment
                            </td>
                        </tr>
                    </table>


          <%
                    out.print("</td></tr></table>\n");
            %>
                    <div class="popup" id="doc<%=i%>" onclick="event.cancelBubble = true;">
                        [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]
                        <input type="button" value="Mark relevant" onclick="this.form['<%=docno%>'].value='relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='green';window.location='#<%=docno%>';"/>
                        <input type="button" value="Mark partially relevant" onclick="this.form['<%=docno%>'].value='partially-relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='yellow';window.location='#<%=docno%>';"/>
                        <input type="button" value="Mark irrelevant" onclick="this.form['<%=docno%>'].value='irrelevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='red';window.location='#<%=docno%>';"/>
                        <br>
                        <%=topicDoc.getHtml()%>
                        [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]
                        <input type="button" value="Mark relevant" onclick="this.form['<%=docno%>'].value='relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='green';window.location='#<%=docno%>';"/>
                        <input type="button" value="Mark partially relevant" onclick="this.form['<%=docno%>'].value='partially-relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='yellow';window.location='#<%=docno%>';"/>
                        <input type="button" value="Mark irrelevant" onclick="this.form['<%=docno%>'].value='irrelevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='red';window.location='#<%=docno%>';"/>
                    </div>
                    <div class="popup" id="docInfo<%=i%>" onclick="event.cancelBubble = true;">
                        [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]
                        <br>
                            <table class="history">
                                <tr>
                                    <th>Assessor</th>
                                    <th>Date</th>
                                    <th>Relevance</th>
                                    <th>Obs</th>
                                </tr>
                                <%
                                    for(HistoryEntry entry:topicDoc.getHistoryEntries())
                                    {
                                        %>
                                            <tr>
                                                <td><%=entry.getAssessor()%></td>
                                                <td><%=entry.getDate()%></td>
                                                <td bgcolor="<%=entry.getColor()%>"><%=entry.getRelevance()%></td>
                                                <td><%=entry.getObs()%></td>
                                            </tr>
                                        <%
                                    }
                                %>
                            </table>
                        <br>
                        [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]
                    </div>
                    <hr>
                <%

            }
            %>
        </form>
        <%
       }
    }

%>

