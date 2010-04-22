<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@page import="org.apache.lucene.analysis.Analyzer"%>
<%@page import="java.util.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.dao.*"%>
<%@page import="pt.utl.ist.lucene.web.assessements.services.*"%>

<%

    String idTopicOriginal = request.getParameter("id_topic");
    String id_topic = request.getParameter("id_topic");
    String startAt = "0";
    String max = "1000";
    if(id_topic!=null && id_topic.indexOf("LIMIT")>0)
    {
        String limit= id_topic.substring(id_topic.indexOf("LIMIT") + "LIMIT".length());
        id_topic = id_topic.substring(0,id_topic.indexOf("LIMIT"));
        startAt = limit.substring(0,limit.indexOf(","));
        max = limit.substring(limit.indexOf(",")+1);
    }

    List<Topic> topics = DBServer.getTopics("NtcirGeoTime2010");

%>
  <br/>
  <a href="assessmentsNtcir.jsp?op=logout">Logout <%=Server.getUsername(request)%></a> 
  <form style="margin-top:4px" name="AssessmentsForm" action="assessmentsNtcir.jsp" method="post">
    <div class="searchForm">
		<table width="100%">
			<tr>
				<td nowrap="nowrap" colspan="2">
					Choose a topic to evaluate: ( <a href="javascript:alert('Choose a Topic, and mark the documents as relevant, partially relevant (when, where or other) or irrelevant, use the \"Display Document\" to see the places and the time expressions marked with colors in the document body')">help</a> )
					<select name="id_topic">
							<option value=""></option>
						<%
							for(Topic topic : topics)
							{
							    int mDocs = topic.getDocs();
								String checked = "";
								/*if(id_topic != null && topic.getIdTopic().equals(id_topic))
								{
									checked = "selected";
								}*/

						        if(startAt.equals("0") && id_topic != null && topic.getIdTopic().equals(id_topic))
						            checked = "selected";
						        String mm = mDocs <= 100 ? (""+ mDocs): "100";
						        %>
								    <option onclick="this.form.submit()" <%=checked%>  value="<%=(topic.getIdTopic() + "LIMIT0,100")%>"> <%=topic.getIdTopic()%> (001-<%=mm%>) - <%=topic.getDescription()%></option>
								<%
								    checked="";


								if(topic.getDocs() > 100) {
								    mm = mDocs <= 200 ? (""+ mDocs): "200";
								    if(startAt.equals("100") && id_topic != null && topic.getIdTopic().equals(id_topic))
								        checked="selected";
								%>
								    <option onclick="this.form.submit()" <%=checked%>  value="<%=(topic.getIdTopic() + "LIMIT100,100")%>"> <%=topic.getIdTopic()%> (101-<%=mm%>) - <%=topic.getDescription()%></option>
								<%
								  checked="";
								} if(topic.getDocs() > 200) {
								    mm = mDocs <= 300 ? (""+ mDocs): "300";
								    if(startAt.equals("200") && id_topic != null && topic.getIdTopic().equals(id_topic))
								      checked="selected";
								%>
								    <option onclick="this.form.submit()" <%=checked%>  value="<%=(topic.getIdTopic() + "LIMIT200,100")%>"> <%=topic.getIdTopic()%> (201-<%=mm%>) - <%=topic.getDescription()%></option>
								<%
								  checked="";
                                } if(topic.getDocs() > 300) {
                                    mm = mDocs <= 400 ? (""+ mDocs): "400";
                                    if(startAt.equals("300") && id_topic != null && topic.getIdTopic().equals(id_topic))
                                      checked="selected";
								%>
								    <option onclick="this.form.submit()" <%=checked%>  value="<%=(topic.getIdTopic() + "LIMIT300,100")%>"> <%=topic.getIdTopic()%> (301-<%=mm%>) - <%=topic.getDescription()%></option>
								<%
								  checked="";
								} if(topic.getDocs() > 400) {
								    mm = mDocs <= 500 ? (""+ mDocs): "500";
								    if(startAt.equals("400") && id_topic != null && topic.getIdTopic().equals(id_topic))
								        checked="selected";
								%>
								    <option onclick="this.form.submit()" <%=checked%>  value="<%=(topic.getIdTopic() + "LIMIT400,100")%>"> <%=topic.getIdTopic()%> (401-<%=mm%>) - <%=topic.getDescription()%></option>
								<%
								  checked="";
                                } if(topic.getDocs() > 500) {
                                    mm = mDocs <= 600 ? (""+mDocs) : "600";
                                    if(startAt.equals("500") && id_topic != null && topic.getIdTopic().equals(id_topic))
                                        checked="selected";
								%>
								    <option onclick="this.form.submit()" <%=checked%>  value="<%=(topic.getIdTopic() + "LIMIT500,100")%>"> <%=topic.getIdTopic()%> (501-<%=mm%>) - <%=topic.getDescription()%></option>
								<%
								  checked="";
                                }
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
					<input type="submit" value="Change topic without save"><br/>
				</td>
			</tr>
			<tr>
			    <td colspan="2">
			        Highlight these keywords: <input type="text" name="keywords" value="<%=(request.getParameter("keywords") != null ? request.getParameter("keywords") : "")%>">
			    </td>
			</tr>
		</table>
	</div>
    <!--</form>-->

<%
    if(id_topic!=null && id_topic.trim().length() >= 0)
    {
        int i = 0;
        List<TopicDoc> topicDocs = DBServer.getTopicDocs(id_topic,startAt,max);
        if(topicDocs.size() > 0)
        {

         %>
    <!--<form action="assessmentsNtcir.jsp?op=addJudgments" method="post">-->
        <input type="hidden" name="op" value="">
        <input type="hidden" name="id_topic" value="<%=id_topic%>">
        <input style="background-color:yellow;padding:3px;" type="button" onclick="this.form.op.value='addJudgments';this.form.submit()" value="Click here to confirm your Judgements after you choose the relevance of the documents"/>

        <h2>Assessments Stats for current Pool and current Topic</h2>
        <%
        AssessmentsBoard aB = DBServer.loadAssessmentsBoardOpenPools("NtcirGeoTime2010",id_topic);
        %>
         <table class="dataLine">
            <tr>
                <th>Topic/relevance</th>
                <th>relevant</th>
                <th>partially relevant<br> where</th>
                <th>partially relevant<br> when</th>
                <th>partially relevant<br> other</th>
                <th>irrelevant</th>
                <th>Assessed/Total</th>
            </tr>
             <tr>
                <td><%=id_topic%></td>
                <td><%=aB.getCount(id_topic,"relevant")%></td>
                <td><%=aB.getCount(id_topic,"partially-relevant-where")%></td>
                <td><%=aB.getCount(id_topic,"partially-relevant-when")%></td>
                <td><%=aB.getCount(id_topic,"partially-relevant-other")%></td>
                <td><%=aB.getCount(id_topic,"irrelevant")%></td>
                <td><%=aB.getTotalsAssessed(id_topic)%>/<%=aB.getTotals(id_topic)%></td>
            </tr>
        </table>

        <h2><a href="javascript:showOrHideOne('stats')">Show Assessments Stats for all Topics</a></h2>
        <div id="stats" style="display:none">

        <%
        aB = DBServer.loadAssessmentsBoardOpenPools("NtcirGeoTime2010");
        %>
                 <table class="data">
                    <tr>
                        <th>Topic/relevance</th>
                        <th>relevant</th>
                        <th>partially relevant<br> where</th>
                        <th>partially relevant<br> when</th>
                        <th>partially relevant<br> other</th>
                        <th>irrelevant</th>
                        <th>Assessed/Total</th>
                    </tr>
                 <%
                    for(Topic topic: topics)
                    {
                 %>
                         <tr>
                            <td><%=topic.getIdTopic()%></td>
                            <td><%=aB.getCount(topic.getIdTopic(),"relevant")%></td>
                            <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-where")%></td>
                            <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-when")%></td>
                            <td><%=aB.getCount(topic.getIdTopic(),"partially-relevant-other")%></td>
                            <td><%=aB.getCount(topic.getIdTopic(),"irrelevant")%></td>
                            <td><%=aB.getTotalsAssessed(topic.getIdTopic())%>/<%=aB.getTotals(topic.getIdTopic())%></td>
                        </tr>
                 <%
                    }
                 %>
            </table>


        </div>
        <br>


        <hr>
        <%
            for (TopicDoc topicDoc: topicDocs)
            {
                    i = topicDoc.getRank();
                    String style = "";
                    String relevance = "";
                    String relevantSelected = "";
                    String partiallyRelevantWhenSelected = "";
                    String partiallyRelevantWhereSelected = "";
                    String partiallyRelevantOtherSelected = "";
                    String irrelevantSelected = "";

                    if(topicDoc.getRelevance().equals("irrelevant"))
                    {
                            style = "style=\"padding:5px\" class=\"irrelevant\"";
                            irrelevantSelected = " selected";
                    }
                    else if(topicDoc.getRelevance().equals("partially-relevant-where"))
                    {
                            style = "style=\"padding:5px\" class=\"partiallyRelevantWhere\"";
                            partiallyRelevantWhereSelected = " selected";
                    }
                    else if(topicDoc.getRelevance().equals("partially-relevant-when"))
                    {
                            style = "style=\"padding:5px\" class=\"partiallyRelevantWhen\"";
                            partiallyRelevantWhenSelected = " selected";
                    }
                    else if(topicDoc.getRelevance().equals("partially-relevant-other"))
                    {
                            style = "style=\"padding:5px\" class=\"partiallyRelevantOther\"";
                            partiallyRelevantOtherSelected = " selected";
                    }
                    else if(topicDoc.getRelevance().equals("relevant"))
                    {
                            style = "style=\"padding:5px\" class=\"relevant\"";
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
                    out.print("<input type=\"button\" value=\"irrelevant\" onclick=\"this.form['" + docno + "'].value='irrelevant';hideCurrentPopup();getObjectById('table" + docno + "').style.backgroundColor='red';window.location='#"+ docno + "';\"/>");

                    out.print("<p><b>score</b>: " + topicDoc.getScore() + "</p>\n");
                    out.print("<p style=\"background-color:white\"><a onclick=\"getObjectById('table" + docno + "').style.backgroundColor='lightgray';return !showPopup('doc" + i + "', event);\" href=\"#\">Display Document</a></p>\n");
                    out.print("<p style=\"background-color:white\"><a onclick=\"return !showPopup('docInfo" + i + "', event);\" href=\"#\">Show Assessments Info</a></p>\n");
                    out.print("</td>\n");
                    out.print("<td align=\"right\" valign=\"top\">\n");
                     %>
                    <table>
                        <tr>
                            <td></td>
                            <td>
                                <select name="<%=docno%>" onchange="if(this.value == 'relevant'){getObjectById('table<%=docno%>').style.backgroundColor='green';}else if(this.value == 'irrelevant'){getObjectById('table<%=docno%>').style.backgroundColor='red';} else if(this.value == 'partially-relevant-where'){getObjectById('table<%=docno%>').style.backgroundColor='rgb(249,208,172)';} else if(this.value == 'partially-relevant-when'){getObjectById('table<%=docno%>').style.backgroundColor='lightskyblue';} else if(this.value == 'partially-relevant-other'){getObjectById('table<%=docno%>').style.backgroundColor='yellow';}" name="<%=docno%>">
                                    <option value="">Choose relevance</option>
                                    <option value="relevant" <%=relevantSelected%>>relevant</option>
                                    <option value="partially-relevant-where" <%=partiallyRelevantWhereSelected%>>partially relevant where</option>
                                    <option value="partially-relevant-when" <%=partiallyRelevantWhenSelected%>>partially relevant when</option>
                                    <option value="partially-relevant-other" <%=partiallyRelevantOtherSelected%>>partially relevant other</option>
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
                        <input type="button" value="relevant" onclick="this.form['<%=docno%>'].value='relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='green';window.location='#<%=docno%>';"/>
                        <input type="button" value="part. relevant where" onclick="this.form['<%=docno%>'].value='partially-relevant-where';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='rgb(249,208,172)';window.location='#<%=docno%>';"/>
                        <input type="button" value="part. relevant when" onclick="this.form['<%=docno%>'].value='partially-relevant-when';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='lightskyblue';window.location='#<%=docno%>';"/>
                        <input type="button" value="part. relevant other" onclick="this.form['<%=docno%>'].value='partially-relevant-other';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='yellow';window.location='#<%=docno%>';"/>
                        <input type="button" value="irrelevant" onclick="this.form['<%=docno%>'].value='irrelevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='red';window.location='#<%=docno%>';"/>
                        <br>
                        <%=topicDoc.getHtml(request.getParameter("keywords"))%>
                        [<A onclick="hideCurrentPopup(); window.location='#<%=docno%>'; return false;" href="#"><font size="4">Close</font></A>]
                        <input type="button" value="relevant" onclick="this.form['<%=docno%>'].value='relevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='green';window.location='#<%=docno%>';"/>
                        <input type="button" value="part. relevant where" onclick="this.form['<%=docno%>'].value='partially-relevant-where';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='rgb(249,208,172)';window.location='#<%=docno%>';"/>
                        <input type="button" value="part. relevant when" onclick="this.form['<%=docno%>'].value='partially-relevant-when';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='lightskyblue';window.location='#<%=docno%>';"/>
                        <input type="button" value="part. relevant other" onclick="this.form['<%=docno%>'].value='partially-relevant-other';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='yellow';window.location='#<%=docno%>';"/>
                        <input type="button" value="irrelevant" onclick="this.form['<%=docno%>'].value='irrelevant';hideCurrentPopup();getObjectById('table<%=docno%>').style.backgroundColor='red';window.location='#<%=docno%>';"/>
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

       }
    }
   %>
        </form>
        <%
%>

