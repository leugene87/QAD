/**
 * Project: QuotesAnalizer_2017
 * File: QuoteReader.java
 * Date: Aug 17, 2017
 * Time: 2:21:27 PM
 */

package a00970495.ass1.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a00970495.ass1.ApplicationException;
import a00970495.ass1.dataBase.DataBase;
import a00970495.ass1.dataBase.DbConstants;
import a00970495.ass1.quote.Quote;

/**
 * @author Ievgen Lytvynenko, A00970495
 *
 */
public class QuoteDao extends Dao {

	private static QuoteDao theInstance;
	public static final String TABLE_NAME = DbConstants.TABLE_NAME;
	private static Logger LOG = LogManager.getLogger();
	private ArrayList<String> sqlQuerries;
	public static String QUOTE_DATA_FILENAME = "GBP USD D11.csv";

	private QuoteDao() {
		super(TABLE_NAME);
		sqlQuerries = new ArrayList<>();
	}

	public ArrayList<String> getSqlQuerriesList() {
		return sqlQuerries;
	}

	/**
	 * @return the theinstance
	 */
	public static QuoteDao getTheinstance() {
		return theInstance;
	}

	public static synchronized void init() throws ApplicationException {
		if (theInstance == null) {
			theInstance = new QuoteDao();

		}
		LOG.debug("QuoteDao.init(): instanse found.");
		int quoteCount = theInstance.load();
		LOG.debug("Inserted " + quoteCount + " parts");

	}

	/**
	 * @throws ApplicationException
	 * @throws SQLException
	 */
	private int load() throws ApplicationException {
		int quoteCount = 0;
		// File file = new File(QUOTE_DATA_FILENAME);
		try {
			if (!(DataBase.tableExists(TABLE_NAME))) {
				System.out.println("creating table as it doesnt exist");
				create();
			} else {
				if (DataBase.dbTableDropRequested()) {
					System.out.println("dropping as requested");
					drop();
					theInstance.sqlQuerries.add("drop table " + TABLE_NAME);
					System.out.println("creating after drop");
					create();
					// quoteCount = QuoteReader.readQuoteFromFile(file, this);
					// LOG.debug("There are : " + quoteCount + " loaded from file raws");

				}
				// remove from
				/*
				 * else {
				 * drop();
				 * theInstance.sqlQuerries.add("drop table " + TABLE_NAME);
				 * create();
				 * }
				 */ // remove till
			}
			Connection connection = database.getConnection();
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			quoteCount = getRowCount(statement);
			LOG.debug("There are : " + quoteCount + "raws");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return quoteCount;
	}

	@Override
	public void create() throws SQLException {
		LOG.debug("Creating database table " + TABLE_NAME);

		String sqlString = String.format(
				"CREATE TABLE %s (%s BIGINT NOT NULL AUTO_INCREMENT, %s VARCHAR (30) NOT NULL, %s VARCHAR (21) NOT NULL, %s DECIMAL (10,5), %s DECIMAL (10,5), %s DECIMAL (10,5), %s DECIMAL (10,5), %s BIGINT, %s BIGINT NOT NULL, PRIMARY KEY (%s))",
				TABLE_NAME, //
				Column.INDEX.name, //
				Column.DATE_AND_TIME.name, //
				Column.DAY_OF_WEEK.name, //
				Column.OPEN.name, //
				Column.HIGH.name, //
				Column.LOW.name, //
				Column.CLOSE.name, //
				Column.PERIOD.name, //
				Column.VOLUME.name, //
				// Primary key
				Column.INDEX.name); //
		super.create(sqlString);

		theInstance.sqlQuerries.add(sqlString);
		LOG.debug("table " + TABLE_NAME + " created");
	}

	public void add(Quote quote) throws SQLException {
		String sqlString = String.format("INSERT INTO %s values(default, ?, ?, ?, ?, ?, ?, ?, ?)", TABLE_NAME);
		boolean result = execute(sqlString, //
				//
				quote.getDateTimeForSQL(), //
				quote.getDayOfWeek(), //
				quote.getOpen(), //
				quote.getHigh(), //
				quote.getLow(), //
				quote.getClose(), //
				quote.getPeriod(), //
				quote.getVolume());
		String quotedText = Pattern.quote("?");
		String last = sqlString.replaceFirst(quotedText, quote.getDateTimeForSQL()).replaceFirst(quotedText, quote.getDayOfWeek().toString())
				.replaceFirst(quotedText, String.valueOf(quote.getOpen())).replaceFirst(quotedText, String.valueOf(quote.getHigh()))
				.replaceFirst(quotedText, String.valueOf(quote.getLow())).replaceFirst(quotedText, String.valueOf(quote.getClose()))
				.replaceFirst(quotedText, String.valueOf(quote.getPeriod())).replaceFirst(quotedText, String.valueOf(quote.getVolume()));
		theInstance.sqlQuerries.add(last);
		LOG.debug(String.format("Adding %s was %s", quote, result ? "unsuccessful" : "successful"));
	}

	/**
	 * Update the inventory.
	 * 
	 * @param inventoryForSqlServer
	 * @throws SQLException
	 */
	public void update(Quote quote, int index) throws SQLException {
		LOG.debug("Updating: " + quote.toString());
		String sqlString = String.format("UPDATE %s SET %s=?,%s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?", TABLE_NAME, //
				Column.DATE_AND_TIME.name, //
				Column.DAY_OF_WEEK.name, //
				Column.OPEN.name, //
				Column.HIGH.name, //
				Column.LOW.name, //
				Column.CLOSE.name, //
				Column.PERIOD.name, //
				Column.VOLUME.name, //
				Column.INDEX.name);
		LOG.debug("Update statment: " + sqlString);
		// @SuppressWarnings("unused")
		boolean result = execute(sqlString, quote.getDateTimeForSQL(), quote.getDayOfWeek(), quote.getOpen(), quote.getHigh(), quote.getLow(),
				quote.getClose(), quote.getPeriod(), quote.getVolume(), index);
		String quotedText = Pattern.quote("?");
		String last = sqlString.replaceFirst(quotedText, quote.getDateTimeForSQL()).replaceFirst(quotedText, quote.getDayOfWeek().toString())
				.replaceFirst(quotedText, String.valueOf(quote.getOpen())).replaceFirst(quotedText, String.valueOf(quote.getHigh()))
				.replaceFirst(quotedText, String.valueOf(quote.getLow())).replaceFirst(quotedText, String.valueOf(quote.getClose()))
				.replaceFirst(quotedText, String.valueOf(quote.getPeriod())).replaceFirst(quotedText, String.valueOf(quote.getVolume()))
				.replaceFirst(quotedText, String.valueOf(index));
		theInstance.sqlQuerries.add(last);
		LOG.debug(String.format("Updated %s", quote));
		LOG.debug(result);
	}

	/**
	 * Delete the motorcycle from the database.
	 * 
	 * @param motorcycleForSqlServer
	 * @throws SQLException
	 */
	public void delete(Quote quote, int index) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();

			String sqlString = String.format("DELETE FROM %s WHERE %s= %s", TABLE_NAME, Column.INDEX.name, index);
			LOG.debug(sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			String quotedText = Pattern.quote("?");
			String last = sqlString.replaceFirst(quotedText, quote.getDateTimeForSQL()).replaceFirst(quotedText, quote.getDayOfWeek().toString())
					.replaceFirst(quotedText, String.valueOf(quote.getOpen())).replaceFirst(quotedText, String.valueOf(quote.getHigh()))
					.replaceFirst(quotedText, String.valueOf(quote.getLow())).replaceFirst(quotedText, String.valueOf(quote.getClose()))
					.replaceFirst(quotedText, String.valueOf(quote.getPeriod())).replaceFirst(quotedText, String.valueOf(quote.getVolume()))
					.replaceFirst(quotedText, String.valueOf(index));
			theInstance.sqlQuerries.add(last);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}

	/**
	 * Retrieve all the inventory parts' numbers from the database
	 * 
	 * @return the list of inventory parts' numbers
	 * @throws SQLException
	 */
	public List<Integer> getQuoteIndexes() throws SQLException {
		List<Integer> indexes = new ArrayList<>();

		String selectString = String.format("SELECT %s FROM %s", Column.INDEX.name, TABLE_NAME);
		LOG.debug(selectString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = database.getConnection();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSet = statement.executeQuery(selectString);

			while (resultSet.next()) {
				indexes.add(resultSet.getInt(Column.INDEX.name));
			}
		} catch (SQLException e) {
			LOG.debug(e.getMessage());
			e.printStackTrace();

		} finally {
			close(statement);
		}

		LOG.debug(String.format("Loaded %d parts from the database", indexes.size()));

		return indexes;
	}

	public int getLastIndex() throws SQLException {
		int lastIndex = 0;

		String selectString = String.format("SELECT %s FROM %s", Column.INDEX.name, TABLE_NAME);
		LOG.debug(selectString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = database.getConnection();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSet = statement.executeQuery(selectString);
			resultSet.last();
			lastIndex = resultSet.getInt(1);
		} catch (SQLException e) {
			LOG.debug(e.getMessage());
		} finally {
			close(statement);
		}

		LOG.debug(String.format("Last index red from table is: ", lastIndex));

		return lastIndex;
	}

	public Quote getQuoteByIndex(int index) throws SQLException {
		String selectString = String.format("SELECT * FROM %s WHERE %s =  %s ", TABLE_NAME, Column.INDEX.name, index);
		LOG.debug(selectString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);

			while (resultSet.next()) {

				String str = resultSet.getString(Column.DATE_AND_TIME.name).substring(0, 16);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime dateT = LocalDateTime.parse(str, formatter);

				DayOfWeek dayW = DayOfWeek.valueOf(resultSet.getString(Column.DAY_OF_WEEK.name));
				double open = resultSet.getDouble(Column.OPEN.name);
				double high = resultSet.getDouble(Column.HIGH.name);
				double low = resultSet.getDouble(Column.LOW.name);
				double close = resultSet.getDouble(Column.CLOSE.name);
				// int period = resultSet.getInt(Column.PERIOD.name);
				int volume = resultSet.getInt(Column.VOLUME.name);
				Quote quote = new Quote.Builder(dateT, dayW, open, high, low, close).setVolume(volume).build();

				return quote;
			}
		} finally {
			close(statement);
		}

		return null;
	}

	public enum Column {
		INDEX("id", 20), //
		DATE_AND_TIME("date_time", 30), //
		DAY_OF_WEEK("day_of_week", 21), //
		OPEN("c_open", 10), //
		HIGH("c_high", 10), //
		LOW("c_low", 10), //
		CLOSE("c_close", 10), //
		PERIOD("period", 10), //
		VOLUME("volume", 10); //

		String name;
		int length;

		private Column(String name, int length) {
			this.name = name;
			this.length = length;
		}

	}

}
