package pt.utl.ist.lucene.web.assessements.dao;

import pt.utl.ist.lucene.web.assessements.HistoryEntry;
import pt.utl.ist.lucene.web.assessements.TopicDoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 10:45:44
 * To change this template use File | Settings | File Templates.
 */
public class DBServer
{
    public static boolean login(String username, String password)
    {
        boolean auth = false;
        try
        {
            Connection conn = DaoServer.getConnection();
            PreparedStatement ps = conn.prepareStatement("select * from user where username=? and password=?");
            ps.setString(1,username);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                auth = true;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return auth;
    }

    public static boolean loginAdmin(String username, String password)
    {
        boolean auth = false;
        try
        {
            Connection conn = DaoServer.getConnection();
            PreparedStatement ps = conn.prepareStatement("select * from user where username=? and password=? and admin=?");
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setBoolean(3,true);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                auth = true;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return auth;
    }

    public static String addTopicDoc(String topic, String docno, String relevance, String admin) throws SQLException {
        boolean auth = false;
        try
        {
            Connection conn = DaoServer.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "select * from topic_doc where topic = ? and docno = ?");
            ps.setString(1,topic);
            ps.setString(2,docno);
            ResultSet rs = ps.executeQuery();
            boolean next = rs.next();
            rs.close();
            ps.close();
            if(next)
                return "already inserted";

            ps = conn.prepareStatement(
                    "insert into topic_doc(topic,docno,relevance,date,admin) values(?,?,?,?,?)");
            ps.setString(1,topic);
            ps.setString(2,docno);
            ps.setString(3,relevance);
            ps.setTimestamp(4,new Timestamp(new java.util.Date().getTime()));
            ps.setString(5,admin);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public static void addRelevanceJudgement(String topic, String docno, String relevance, String user) throws SQLException
    {
        try
        {
            Connection conn = DaoServer.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "update topic_doc set relevance = ? where topic = ? and docno = ?");
            ps.setString(1,relevance);
            ps.setString(2,topic);
            ps.setString(3,docno);
            ps.execute();
            ps.close();
            ps = conn.prepareStatement(
                    "insert into history(assessor, date, relevance, topic, docno) values(?,?,?,?,?)");
            ps.setString(1,user);
            ps.setTimestamp(2,new Timestamp(new java.util.Date().getTime()));
            ps.setString(3,relevance);
            ps.setString(4,topic);
            ps.setString(5,docno);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }

    public static List<TopicDoc> getTopicDocs(String topic) throws SQLException
    {
        try
        {
            Connection conn = DaoServer.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "select * from topic_doc where closed = false and topic = ? order by score");
            ps.setString(1,topic);
            ResultSet rs = ps.executeQuery();
            List<TopicDoc> topicDocs = new ArrayList<TopicDoc>();
            while (rs.next())
            {
                TopicDoc t = new TopicDoc();
                topicDocs.add(t);
                t.setAdmin(rs.getString("admin"));
                t.setDate(rs.getTimestamp("date"));
                t.setDocno(rs.getString("docno"));
                t.setHtml(rs.getString("html"));
                t.setRelevance(rs.getString("relevance"));
                t.setTopic(topic);

                PreparedStatement psHistory = conn.prepareStatement(
                        "select * from history where topic = ? and docno = ? order by date");
                psHistory.setString(1,topic);
                psHistory.setString(2,t.getDocno());
                ResultSet rsHistory = psHistory.executeQuery();
                while (rsHistory.next())
                {
                    HistoryEntry historyEntry = new HistoryEntry();
                    t.getHistoryEntries().add(historyEntry);
                    historyEntry.setAssessor(rsHistory.getString("assessor"));
                    historyEntry.setDate(rsHistory.getTimestamp("date"));
                    historyEntry.setRelevance(rsHistory.getString("relevance"));
                }
                rsHistory.close();
                psHistory.close();
            }
            rs.close();
            ps.close();
            return topicDocs;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }


    public static void main(String[] args) throws SQLException {

        addTopicDoc("teste3","123","partially-relevante","admin");
        addRelevanceJudgement("teste3","doc","relevant","jorge");
        List<TopicDoc> topicDocs = getTopicDocs("teste3");
        for (TopicDoc topicDoc : topicDocs) {
            System.out.println(topicDoc);
        }

    }
}
