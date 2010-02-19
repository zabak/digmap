package pt.utl.ist.lucene.web.assessements.dao;

import jomm.db.ConnectionManager;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.web.assessements.*;

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
    private static ConnectionManager manager = new ConnectionManager(
            ConfigProperties.getProperty("assessments.catalog"),
            ConfigProperties.getProperty("assessments.username"),
            ConfigProperties.getProperty("assessments.password"));

    public static Connection getConnection() throws SQLException
    {
        return manager.getConnection();
    }

    public static AssessmentsBoard loadAssessmentsBoard(String task)
    {
        try
        {
            AssessmentsBoard assessmentsBoard = new AssessmentsBoard();
            List<Topic> topics = getTopics(task);
            for (Topic topic : topics)
            {
                assessmentsBoard.addTopic(topic.getIdTopic());
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("select relevance, count(relevance) as c from topic_doc where topic = ? group by relevance");
                ps.setString(1,topic.getIdTopic());
                ResultSet rs = ps.executeQuery();
                while(rs.next())
                {
                    assessmentsBoard.addRelevance(topic.getIdTopic(),rs.getString("relevance"),rs.getInt("c"));
                }
                ps.close();
                rs.close();
            }
            for (Topic topic : topics)
            {
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("select count(*) as c from topic_doc where topic = ?");
                ps.setString(1,topic.getIdTopic());
                ResultSet rs = ps.executeQuery();
                rs.next();
                assessmentsBoard.addTotals(topic.getIdTopic(),rs.getInt("c"));
                ps.close();
                rs.close();
            }
            return assessmentsBoard;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public static AssessmentsBoard loadAssessmentsBoard(String task, int pool)
    {
        try
        {
            AssessmentsBoard assessmentsBoard = new AssessmentsBoard();
            List<Topic> topics = getTopics(task);
            for (Topic topic : topics)
            {
                assessmentsBoard.addTopic(topic.getIdTopic());
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("select relevance, count(relevance) as c from topic_doc where topic = ? and pool = ? group by relevance");
                ps.setString(1,topic.getIdTopic());
                ps.setInt(2,pool);
                ResultSet rs = ps.executeQuery();
                while(rs.next())
                {
                    assessmentsBoard.addRelevance(topic.getIdTopic(),rs.getString("relevance"),rs.getInt("c"));
                }

                ps.close();
                rs.close();
            }
            for (Topic topic : topics)
            {
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("select count(*) as c from topic_doc where topic = ? and pool = ?");
                ps.setString(1,topic.getIdTopic());
                ps.setInt(2,pool);
                ResultSet rs = ps.executeQuery();
                rs.next();
                assessmentsBoard.addTotals(topic.getIdTopic(),rs.getInt("c"));
                ps.close();
                rs.close();
            }
            return assessmentsBoard;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public static AssessmentsBoard loadAssessmentsBoardOpenPools(String task)
    {
        try
        {
            AssessmentsBoard assessmentsBoard = new AssessmentsBoard();
            List<Topic> topics = getTopics(task);
            for (Topic topic : topics)
            {
                assessmentsBoard.addTopic(topic.getIdTopic());
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("select relevance, count(relevance) as c from topic_doc where topic = ? and closed = false group by relevance");
                ps.setString(1,topic.getIdTopic());
                ResultSet rs = ps.executeQuery();
                while(rs.next())
                {
                    assessmentsBoard.addRelevance(topic.getIdTopic(),rs.getString("relevance"),rs.getInt("c"));
                }

                ps.close();
                rs.close();
            }
            for (Topic topic : topics)
            {
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("select count(*) as c from topic_doc where topic = ? and closed = false");
                ps.setString(1,topic.getIdTopic());
                ResultSet rs = ps.executeQuery();
                rs.next();
                assessmentsBoard.addTotals(topic.getIdTopic(),rs.getInt("c"));
                ps.close();
                rs.close();
            }
            return assessmentsBoard;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public static AssessmentsBoard loadAssessmentsBoardOpenPools(String task, String topic)
    {
        try
        {
            AssessmentsBoard assessmentsBoard = new AssessmentsBoard();
            assessmentsBoard.addTopic(topic);
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("select relevance, count(relevance) as c from topic_doc where topic = ? and closed = false group by relevance");
            ps.setString(1,topic);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                assessmentsBoard.addRelevance(topic,rs.getString("relevance"),rs.getInt("c"));
            }

            ps.close();
            rs.close();

            ps = conn.prepareStatement("select count(*) as c from topic_doc where topic = ? and closed = false");
            ps.setString(1,topic);
            rs = ps.executeQuery();
            rs.next();
            assessmentsBoard.addTotals(topic,rs.getInt("c"));
            ps.close();
            rs.close();

            return assessmentsBoard;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }



    public static User login(String username, String password)
    {
        User auth = null;
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("select * from user where username=? and password=?");
            ps.setString(1,username);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                auth = new User();
                auth.setUsername(username);
                auth.setAdmin(rs.getBoolean("admin"));
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
            Connection conn = getConnection();
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

    public static String addTopicDoc(String topic, String docno, String title, String relevance, String admin, int pool,double score, int rank,String html) throws SQLException {
        boolean auth = false;
        try
        {
            Connection conn = getConnection();
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
                    "insert into topic_doc(topic,docno,relevance,date,admin,pool,score,rank,html,title) values(?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1,topic);
            ps.setString(2,docno);
            ps.setString(3,relevance);
            ps.setTimestamp(4,new Timestamp(new java.util.Date().getTime()));
            ps.setString(5,admin);
            ps.setInt(6,pool);
            ps.setDouble(7,score);
            ps.setInt(8,rank);
            ps.setString(9,html);
            ps.setString(10,title);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
        return null;
    }


    public static int addPool(String runid, String desc, String task) throws SQLException {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "insert into pool(runid,description,task,date) values(?,?,?,?)");
            ps.setString(1,runid);
            ps.setString(2,desc);
            ps.setString(3,task);
            ps.setTimestamp(4,new Timestamp(new java.util.Date().getTime()));
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("select LAST_INSERT_ID()");

            ResultSet rs = ps.executeQuery();
            rs.next();
            int id = rs.getInt(1);
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }

    public static void openClosePool(int id) throws SQLException {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("select closed from pool where id = " + id );
            ResultSet rs = ps.executeQuery();
            rs.next();
            if(rs.getBoolean("closed"))
                openPool(id);
            else
                closePool(id);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }


    public static void closePool(int id) throws SQLException {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("update pool set closed = true where id = " + id );
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("update topic_doc set closed = true where pool = " + id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }

    public static void openPool(int id) throws SQLException {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("update pool set closed = false where id = " + id );
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("update topic_doc set closed = false where pool = " + id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }


    public static void addRelevanceJudgement(String topic, String docno, String relevance, String user,String obs) throws SQLException
    {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "update topic_doc set relevance = ? where topic = ? and docno = ?");
            ps.setString(1,relevance);
            ps.setString(2,topic);
            ps.setString(3,docno);
            ps.execute();
            ps.close();
            ps = conn.prepareStatement(
                    "insert into history(assessor, date, relevance, topic, docno,obs) values(?,?,?,?,?,?)");
            ps.setString(1,user);
            ps.setTimestamp(2,new Timestamp(new java.util.Date().getTime()));
            ps.setString(3,relevance);
            ps.setString(4,topic);
            ps.setString(5,docno);
            ps.setString(6,obs);
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
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "select * from topic_doc where closed = false and topic = ? order by score desc");
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
                t.setScore(rs.getDouble("score"));
                t.setDocTitle(rs.getString("title"));
                t.setRank(rs.getInt("rank"));
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
                    historyEntry.setObs(rsHistory.getString("obs"));
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




    public static List<Topic> getTopics(String task) throws SQLException
    {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "select * from topic where task = ? order by id_topic");
            ps.setString(1,task);
            ResultSet rs = ps.executeQuery();
            List<Topic> topics = new ArrayList<Topic>();
            while (rs.next())
            {
                Topic t = new Topic();
                topics.add(t);
                t.setIdTopic(rs.getString("id_topic"));
                t.setDescription(rs.getString("description"));
                t.setNarrative(rs.getString("narrative"));
                t. setTask(task);
            }
            rs.close();
            ps.close();
            return topics;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }

    public static Topic getTopic(String topicId) throws SQLException
    {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "select * from topic where id_topic = ?");
            ps.setString(1,topicId);
            ResultSet rs = ps.executeQuery();
            List<Topic> topics = new ArrayList<Topic>();
            rs.next();
            Topic t = new Topic();
            topics.add(t);
            t.setIdTopic(rs.getString("id_topic"));
            t.setDescription(rs.getString("description"));
            t.setNarrative(rs.getString("narrative"));
            rs.close();
            ps.close();
            return t;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }

    public static List<Pool> getPools(String task) throws SQLException
    {
        try
        {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "select * from pool where task = ? order by id");
            ps.setString(1,task);
            ResultSet rs = ps.executeQuery();
            List<Pool> pools = new ArrayList<Pool>();
            while (rs.next())
            {
                Pool p = new Pool();
                pools.add(p);
                p.setId(rs.getInt("id"));
                p.setRunId(rs.getString("runid"));
                p.setClosed(rs.getBoolean("closed"));
                p.setDescription(rs.getString("description"));
                p.setDate(rs.getTimestamp("date"));
                p.setTask(rs.getString("task"));
            }
            rs.close();
            ps.close();
            return pools;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }


    public static void main(String[] args) throws SQLException {

//        System.out.println(login("admin","admin!ntcir2010"));
//        addTopicDoc("teste3","123","partially-relevante","admin",1,2.3);
//        addRelevanceJudgement("teste3","doc","relevant","jorge");
//        List<TopicDoc> topicDocs = getTopicDocs("teste3");
//        for (TopicDoc topicDoc : topicDocs) {
//            System.out.println(topicDoc);
//        }
//        System.out.println(getTopics("NtcirGeoTime2010"));

        System.out.println(addPool("teste","teste","teste"));
    }
}
