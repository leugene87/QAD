/**
 * Project: QuotesAnalizer_2017
 * File: QuoteReader.java
 * Date: Aug 17, 2017
 * Time: 2:21:27 PM
 */

package a00970495.ass1.quote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a00970495.ass1.ApplicationException;
import a00970495.ass1.dao.QuoteDao;
import a00970495.ass1.dataBase.DbConstants;

/**
 * @author Ievgen Lytvynenko, A00970495
 *
 */

public class QuoteReader {
	private static QuoteDao quote = QuoteDao.getTheinstance();
	private static final Logger LOG = LogManager.getLogger();
	private static File readedFile;
	public static final String FIELD_DELIMITER = "\\,";
	public static final int MAX_QUOTES_TO_READ = 165;

	/**
	 * 
	 */
	@SuppressWarnings("static-access")
	public QuoteReader() {
		try {
			quote.init();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	public static Quote readQuoteFromLine(String date, String open, String high, String low, String close, String volume)
			throws ApplicationException, NumberFormatException, IndexOutOfBoundsException, NullPointerException, DateTimeException {
		LOG.debug("QuoteReader: reading updated line from screen input and setting the dao object");
		int volumeVal = 0;
		double openPrice = 0, highPrice = 0, lowPrice = 0, closePrice = 0;

		LocalDateTime datetime = buildDateFromValidString(date);
		DayOfWeek dayOfWeek = datetime.getDayOfWeek();

		openPrice = Double.parseDouble(open);
		highPrice = Double.parseDouble(high);
		lowPrice = Double.parseDouble(low);
		closePrice = Double.parseDouble(close);
		volumeVal = Integer.parseInt(volume);

		Quote q = new Quote.Builder(datetime, dayOfWeek, openPrice, highPrice, lowPrice, closePrice).setVolume(volumeVal).build();
		LOG.debug(q.toString());
		LOG.debug(DbConstants.TABLE_NAME);

		return q;
	}

	private static LocalDateTime buildDateFromValidString(String date) {
		LOG.debug("QuoteReader: reading Date String");
		date = date.replace("T", " ");
		int year = 0, month = 0, dayOfMonth = 0, hour = 0, minute = 0;
		LocalDateTime datetime = null;

		int yearSize = date.indexOf("-", 0);
		int monthSize = date.indexOf("-", yearSize + 1);
		int daySize = date.indexOf(" ", 0);

		year = Integer.parseInt(date.substring(0, 4));
		switch (monthSize) {
		case 7:
			System.out.println("7 format date");
			month = Integer.parseInt(date.substring(5, 7));
			if (daySize == 10) {
				dayOfMonth = Integer.parseInt(date.substring(8, 10));
				hour = Integer.parseInt(date.substring(11, 13));
				minute = Integer.parseInt(date.substring(14, 16));
			} else if (daySize == 9) {
				dayOfMonth = Integer.parseInt(date.substring(8, 9));
				hour = Integer.parseInt(date.substring(10, 12));
				minute = Integer.parseInt(date.substring(13, 15));
			} else {
				dayOfMonth = 1;
				LOG.debug("QuoteReader.readQuoteFromLine: ERROR SETTING DAY. DEFAULT VALUE IS SET.");
			}
			break;
		case 6:
			System.out.println("6 format date");
			month = Integer.parseInt(date.substring(5, 6));
			System.out.println(month);
			if (daySize == 9) {
				dayOfMonth = Integer.parseInt(date.substring(7, 9));
				System.out.println(dayOfMonth);
				hour = Integer.parseInt(date.substring(10, 12));
				System.out.println(hour);
				minute = Integer.parseInt(date.substring(13, 15));
				System.out.println(minute);
			} else if (daySize == 8) {
				dayOfMonth = Integer.parseInt(date.substring(7, 8));
				hour = Integer.parseInt(date.substring(9, 11));
				minute = Integer.parseInt(date.substring(12, 14));
			} else {
				dayOfMonth = 1;
				LOG.debug("QuoteReader.readQuoteFromLine: ERROR SETTING DAY. DEFAULT VALUE IS SET.");
			}
			break;
		default:
			month = 1;
			LOG.debug("QuoteReader.readQuoteFromLine: ERROR SETTING MONTH. DEFAULT VALUE IS SET.");
			break;
		}
		datetime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
		return datetime;
	}

	public static int readQuoteFromFile(File quoteDataFile, QuoteDao dao) throws ApplicationException {
		setReadedFile(quoteDataFile);
		LOG.debug("reading data from " + quoteDataFile + " file, and setting the dao object");
		BufferedReader quoteReader = null;

		int quoteCount = 0;

		try {
			quoteReader = new BufferedReader(new FileReader(
					// for a local path use
					"..\\workspaceEE\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\QAD\\upload\\" + quoteDataFile));

			// for a ec2
			// "..\\var\\lib\\tomcat8\\webapps\\QAD-debug\\upload\\" + quoteDataFile));
			String line = null;
			// line = quoteReader.readLine(); // skip the header line
			int lineNumber = 0; // was 1
			while (((line = quoteReader.readLine()) != null) && (lineNumber <= MAX_QUOTES_TO_READ)) {
				lineNumber++;
				Quote quote = buildQuoteFromString(line, lineNumber);
				try {
					dao.add(quote);
					quoteCount++;
				} catch (SQLException e) {
					LOG.debug(e);
				}
			}
		} catch (IOException e) {
			LOG.debug(e);
		} finally {
			try {
				if (quoteReader != null) {
					quoteReader.close();
				}
			} catch (IOException e) {
				LOG.debug(e);
			}
		}
		LOG.debug("Red " + quoteCount + " inventory items from " + quoteDataFile + "file");
		return quoteCount;
	}

	/**
	 * @return the readedFile
	 */
	public static File getReadedFile() {
		return readedFile;
	}

	/**
	 * @param readedFile
	 *            the readedFile to set
	 */
	public static void setReadedFile(File rededFile) {
		readedFile = rededFile;
	}

	/**
	 * Parse a motorcycle data string into a Motorcycle object;
	 * 
	 * @param row
	 * @throws ApplicationException
	 */
	private static Quote buildQuoteFromString(String line, int lineNumber) throws ApplicationException {
		String missingField = "---";
		ArrayList<String> elementsArray = new ArrayList<>();
		String[] elements = line.split(FIELD_DELIMITER);
		if (elements.length != Quote.ATTRIBUTE_COUNT) {
			String message = String.format(
					"Expected %d elements from line number: " + lineNumber + "\nin a file: " + readedFile.getAbsolutePath() + "\nbut got %d: %s",
					Quote.ATTRIBUTE_COUNT, elements.length, Arrays.toString(elements));

			LOG.debug(message);
		}
		for (int i = 0; i < elements.length; i++) {
			elementsArray.add(elements[i]);
		}
		if (elementsArray.size() < Quote.ATTRIBUTE_COUNT) {
			int missing = Quote.ATTRIBUTE_COUNT - elementsArray.size();
			for (int f = 0; f < missing; f++) {
				elementsArray.add(missingField);
			}
		}
		int index = 0;

		String date = elementsArray.get(index++);
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int dayOfMonth = Integer.parseInt(date.substring(8, 10));

		String time = elementsArray.get(index++);
		////// adjust on time!!!!!!
		int hour = 0;
		int minute = 0;
		if (time.length() == 5) {
			hour = Integer.parseInt(time.substring(0, 2));
			minute = Integer.parseInt(time.substring(3, 5));
		} else if (time.length() == 4) {
			hour = Integer.parseInt(time.substring(0, 1));
			minute = Integer.parseInt(time.substring(2, 4));
		}

		double openPrice = Double.parseDouble(elementsArray.get(index++));
		double highPrice = Double.parseDouble(elementsArray.get(index++));
		double lowPrice = Double.parseDouble(elementsArray.get(index++));
		double closePrice = Double.parseDouble(elementsArray.get(index++));
		int volumeVal = Integer.parseInt(elementsArray.get(index++));

		LocalDateTime datetime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
		DayOfWeek dayOfWeek = datetime.getDayOfWeek();
		if (elementsArray.size() != Quote.ATTRIBUTE_COUNT) {
			String message1 = String.format("Expected %d elements from:\n" + readedFile.getAbsolutePath() + ", \n but got %d: %s",
					Quote.ATTRIBUTE_COUNT, elements.length, Arrays.toString(elements));
			LOG.debug(message1);
		}

		Quote q = new Quote.Builder(datetime, dayOfWeek, openPrice, highPrice, lowPrice, closePrice).setVolume(volumeVal).build();
		LOG.debug(q.toString());

		return q;
	}

}
