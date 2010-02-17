package pt.utl.ist.lucene.web.assessements.dao;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 10:18:09
 * To change this template use File | Settings | File Templates.
 */
import jomm.db.ConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoServer
{
    private static ConnectionManager manager = new ConnectionManager("ntcir","root","");

    public static Connection getConnection() throws SQLException
    {
        return manager.getConnection();
    }
    public static void main(String[] args) {

        try {

            Connection conn = manager.getConnection();
            PreparedStatement ps = conn.prepareStatement("select * from user");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                System.out.println("name: " + rs.getString("login"));
                System.out.println("rs.getString(\"password\") = " + rs.getString("password"));
                System.out.println("rs.getBoolean(\"admin\") = " + rs.getBoolean("admin"));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
