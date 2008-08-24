/*
 * Created on Jun 23, 2004
 *
 * Class for accessing the XML structural index
 **/
package org.apache.lucene.ilps;

import java.sql.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

/**
 * @author borkur
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLStructureReader {
	private Properties dbProps; //Holds some properties about the database
	private Connection con;
	/*
	 * Constructor with some default database options. Not reccommended to use.
	 */
	public XMLStructureReader() {
		
		dbProps = new Properties();
		dbProps.setProperty("user", "flexir");
		dbProps.setProperty("host", "localhost");
		dbProps.setProperty("database", "inex_lucene_structure");

		try {
			con = getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	private Connection getConnection() throws SQLException {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found ... ");
		}
		String url =
			"jdbc:mysql://"
				+ dbProps.getProperty("host")
				+ "/"
				+ dbProps.getProperty("database");
		Connection con = DriverManager.getConnection(url, dbProps);
		return con;
	}

	public String getElementPath(int elemId) throws SQLException {
		String sqlElement =
			"SELECT  fileID, tagName, preOrder, postOrder, xpos"
				+ " FROM element, tag"
				+ " WHERE element.tagID = tag.tagID"
				+ " AND elementID = "
				+ elemId;
		Statement sEle = con.createStatement();
		sEle.execute(sqlElement);
		ResultSet resEle = sEle.getResultSet();
		resEle.next();
		int fileId = resEle.getInt(1);
		String tagName = resEle.getString(2);
		int preOrder = resEle.getInt(3);
		int postOrder = resEle.getInt(4);
		int xpos = resEle.getInt(5);
		Hashtable tagNames = new Hashtable();
		tagNames.put(new Integer(preOrder), tagName + "[" + xpos + "]");

		String sqlAncestors =
			"SELECT tagName, preOrder, xpos "
				+ "FROM element, tag "
				+ "WHERE fileID = "
				+ fileId
				+ " AND preOrder < "
				+ preOrder
				+ " AND postOrder > "
				+ postOrder
				+ " AND element.tagID = tag.tagID";
		Statement sAnc = con.createStatement();
		sAnc.execute(sqlAncestors);
		ResultSet rsAnc = sAnc.getResultSet();
		while (rsAnc.next()) {
			tagNames.put(
				new Integer(rsAnc.getInt(2)),
				rsAnc.getString(1) + "[" + rsAnc.getInt(3) + "]");
		}

		String path = "";
		TreeMap tm = new TreeMap(tagNames);
		Iterator iter = tm.keySet().iterator();
		while (iter.hasNext()) {
			path += "/" + tagNames.get(iter.next());
		}
		return path;
	}

	public String getElementFileName(int elemId) throws SQLException {
		String sqlName =
			"SELECT name"
				+ " FROM xmlfile, element"
				+ " WHERE xmlfile.fileID=element.fileID"
				+ " AND elementID = "
				+ elemId;
		Statement sName = con.createStatement();
		sName.execute(sqlName);
		ResultSet rsName = sName.getResultSet();
		rsName.next();
		return rsName.getString(1);
	}

	public String[] getElementAncestor(int elemId) throws SQLException {

		String sqlElem =
			"SELECT fileID, preOrder, postOrder"
				+ "FROM element"
				+ "WHERE elementID = "
				+ elemId;
		Statement sElem = con.createStatement();
		sElem.execute(sqlElem);
		ResultSet rsElem = sElem.getResultSet();
		rsElem.next();
		int fileId = rsElem.getInt(1);
		int preOrder = rsElem.getInt(2);
		int postOrder = rsElem.getInt(3);

		Vector ancestors = new Vector();

		String sqlAnc =
			"SELECT elementID"
				+ " FROM element"
				+ " WHERE fileID = "
				+ fileId
				+ " AND preOrder < "
				+ preOrder
				+ "AND postOrder > "
				+ postOrder;
		Statement sAnc = con.createStatement();
		sAnc.execute(sqlAnc);
		ResultSet rsAnc = sAnc.getResultSet();
		while (rsAnc.next()) {
			ancestors.add(String.valueOf(rsAnc.getInt(1)));
		}

		return (String[])(ancestors.toArray(new String[0]));
}

public static void main(String[] args) {

	XMLStructureReader xml = new XMLStructureReader();
	try {
		System.out.println(
			"El 15 has name "
				+ xml.getElementFileName(15)
				+ xml.getElementPath(15));
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
}
