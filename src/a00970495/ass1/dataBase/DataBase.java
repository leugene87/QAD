/**
 * Project: QuotesAnalizer_2017
 * File: DataBase.java
 * Date: Oct 17, 2017
 * Time: 2:25:32 PM
 */

package a00970495.ass1.dataBase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a00970495.ass1.ApplicationException;

/**
 * @author Ievgen Lytvynenko, A00970495
 *
 */

public class DataBase {
	private static final Logger LOG = LogManager.getLogger();

	private static DataBase theInstance = new DataBase();
	private static Connection connection;
	private static boolean dbTableDropRequested;

	private static String url;
	private static String userName;
	private static String passWord;

	Statement stmt = null;
	ResultSet queryResults = null;

	private DataBase() {
	}

	/**
	 * @return the theInstance
	 */
	public static DataBase getTheInstance() {
		return theInstance;
	}

	public static void init(String url, String userName, String passWord) throws ApplicationException, ClassNotFoundException, SQLException {
		DataBase.url = url;
		DataBase.userName = userName;
		DataBase.passWord = passWord;
		LOG.debug("Loading database properties from input param");

	}

	public Connection getConnection() throws SQLException {
		if (connection != null) {
			return connection;
		}
		try {
			connect();
		} catch (SQLException e1) {
			LOG.debug(e1.getMessage());
		}

		return connection;
	}

	public void connect() throws SQLException {

		if (userName == null || passWord == null) {

			connection = DriverManager.getConnection(url);

		} else {
			connection = DriverManager.getConnection(url, userName, passWord);

		}

	}

	/**
	 * Returns the Class object associated with the class or interface with the
	 * given string name.
	 * 
	 * @param className
	 *            The full class name of the database driver object.
	 * @throws ClassNotFoundException
	 */
	public static void loadDatabaseDriver(String className) throws ClassNotFoundException {

		Class.forName(className);
	}

	/**
	 * Close the connections to the database
	 */
	public void shutdown() {
		if (connection != null) {
			try {
				LOG.debug("Closing the DB connection");
				connection.close();
				connection = null;
			} catch (SQLException e) {
				LOG.debug(e.getMessage());
			}
		}
	}

	/**
	 * Determine if the database table exists.
	 * 
	 * @param tableName
	 * @return true is the table exists, false otherwise
	 * @throws SQLException
	 */
	public static boolean tableExists(String targetTableName) throws SQLException {
		DatabaseMetaData databaseMetaData = DataBase.getTheInstance().getConnection().getMetaData();
		ResultSet resultSet = null;
		String tableName = null;

		try {
			resultSet = databaseMetaData.getTables(null, null, null, new String[] { "TABLE" });
			while (resultSet.next()) {
				tableName = resultSet.getString("TABLE_NAME");
				if (tableName.equalsIgnoreCase(targetTableName)) {
					LOG.debug("Found the target table named: " + targetTableName);
					return true;
				}
			}
		} finally {
			resultSet.close();
		}

		return false;
	}

	public static void requestDbTableDrop() {
		dbTableDropRequested = true;
	}

	public static boolean dbTableDropRequested() {
		return dbTableDropRequested;
	}

	public static void connectJDBCToAWSEC2() {

		System.out.println("----MySQL JDBC Connection Testing -------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}

		System.out.println("MySQL JDBC Driver Registered!");
		connection = null;

		try {
			System.out.println("jdbc:mysql://" + DbConstants.PUBLIC_DNS + ":" + DbConstants.PORT + "/" + DbConstants.DATABASE + " ,user: "
					+ DbConstants.REMOTE_DATABASE_USERNAME + ", pw: " + DbConstants.DATABASE_USER_PASSWORD);
			connection = DriverManager.getConnection("jdbc:mysql://" + DbConstants.PUBLIC_DNS + ":" + DbConstants.PORT + "/" + DbConstants.DATABASE,
					DbConstants.REMOTE_DATABASE_USERNAME, DbConstants.DATABASE_USER_PASSWORD);
		} catch (SQLException e) {
			System.out.println("Connection Failed!:\n" + e.getMessage());
		}

		if (connection != null) {
			System.out.println("SUCCESS!!!! You made it, take control     your database now!");
		} else {
			System.out.println("FAILURE! Failed to make connection!");
		}

	}
}
