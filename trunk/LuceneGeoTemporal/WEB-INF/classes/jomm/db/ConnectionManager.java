package jomm.db;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 10:29:13
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionManager
{
    private String URL;
    private String DRIVER;
    private String UserName;
    private String Pass;

    private Connection conn;

    public ConnectionManager(String catalog, String username, String password)
    {
        URL = "jdbc:mysql://localhost/" + catalog;
        DRIVER = "com.mysql.jdbc.Driver";
        UserName = username;
        Pass = password;
    }

    public synchronized Connection getConnection() throws SQLException
    {
        if(conn != null)
        {
            if(!conn.isClosed() && conn.isValid(5000))
                return conn;
            if(!conn.isClosed())
                conn.close();
            conn = null;
        }

        try
        {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, UserName, Pass);
            conn.setAutoCommit(true);
            return conn;
        }catch (ClassNotFoundException e)
        {
            throw new SQLException(e.getMessage());
        }
    }
}
