package a00970495.ass1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a00970495.ass1.cookies.CookieUtilities;
import a00970495.ass1.dao.QuoteDao;
import a00970495.ass1.dataBase.DataBase;
import a00970495.ass1.dataBase.DbConstants;
import a00970495.ass1.quote.Quote;
import a00970495.ass1.quote.QuoteReader;
import a00970495.ass1.util.Decipher;
import a00970495.ass1.util.InputValidator;
import a00970495.ass1.util.StringUtil;

@SuppressWarnings("serial")
@WebServlet("/Ass01_Servlet")
public class Ass01Servlet extends HttpServlet {
	protected static final String CHARSET_FOR_URL_ENCODING = "UTF-8";
	private static Logger LOG = LogManager.getLogger();
	private final static String PASSWORD_FOR_WEB = "you are hired";
	private final static String VOLUME_PATTERN = "^[0-9]{1,12}$";
	private final static String PRICE_PATTERN = "^[0-9]{1,4}(?:\\.[0-9]{1,5})?$";
	private final static String DATE_TIME_PATTERN = "^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) (?:0[0-9]|1[0-9]|2[0-3]):(?:[0-5][0-9]):(?:[0-5][0-9])$";

	private static String defDate, defOpen, defHigh, defLow, defClose, defVolume;
	private static String date, open, high, low, close, volume;

	private static StringBuffer errorMessage = new StringBuffer();

	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.debug("-----------------------");
		LOG.debug("Ass01Servlet: init:");
		try {
			super.init(config);
		} catch (ServletException e) {
			e.printStackTrace();
		}
		ServletContext context = config.getServletContext();

		defDate = LocalDateTime.now().toString();
		defOpen = context.getInitParameter("defOpen");
		defHigh = context.getInitParameter("defHigh");
		defLow = context.getInitParameter("defLow");
		defClose = context.getInitParameter("defClose");
		defVolume = context.getInitParameter("defVolume");

	}

	@SuppressWarnings("static-access")
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException,
			SQLException, IndexOutOfBoundsException, NullPointerException, ApplicationException, InvalidKeySpecException {
		LOG.debug("-----------------------");
		LOG.debug("Ass01Servlet: service():");
		HttpSession session = request.getSession();
		RequestDispatcher rs;

		ArrayList<String> ql = null;
		try {
			ql = QuoteDao.getTheinstance().getSqlQuerriesList();
		} catch (Exception e1) {
			LOG.debug("DAO object is not initalized yet");
		}

		if (session.isNew()) {
			ql.clear();
			session.setAttribute("lastQuerries", ql);
		}

		LOG.debug(request.getRequestURL().toString());

		String accept = request.getHeader("Accept");
		LOG.debug(accept);
		if ((accept.contains("text")) || (accept.contains("html"))) {
			LOG.debug("MIME-type checked");
			response.setContentType("text/html;charset=UTF-8");
		}

		String language = request.getParameter("language");
		String page = request.getParameter("page");

		String task = request.getParameter("task");

		String pass = (String) session.getAttribute("pass");

		LOG.debug(task);
		LOG.debug(pass);
		int index = 0;
		if (task != null) {
			switch (task.substring(0, 6)) {

			case "Delete":
				LOG.debug("Ass01_Servlet: delete button pressed, redirection proseeded:");
				try {
					index = Integer.parseInt(task.substring(6));
					readInputData(task, request, index);
					Quote q = QuoteReader.readQuoteFromLine(date, open, high, low, close, volume);
					QuoteDao qd = QuoteDao.getTheinstance();
					qd.delete(q, index);
					ql = qd.getSqlQuerriesList();
					session.setAttribute("lastQuerries", ql);
				} catch (SQLException | NumberFormatException | IndexOutOfBoundsException | NullPointerException | ApplicationException e) {
					LOG.debug(e.getMessage());
					e.printStackTrace();
				} finally {
					rs = request.getRequestDispatcher("quotes.jsp");
					rs.forward(request, response);
				}
				break;

			case "Update":
				LOG.debug("Ass01_Servlet: update button pressed, redirection proseeded:");

				try {
					index = Integer.parseInt(task.substring(6));
					readInputData(task, request, index);
					Quote q = QuoteReader.readQuoteFromLine(date, open, high, low, close, volume);
					QuoteDao.getTheinstance().update(q, index);
					ql = QuoteDao.getTheinstance().getSqlQuerriesList();
					session.setAttribute("lastQuerries", ql);
				} catch (SQLException | NumberFormatException | IndexOutOfBoundsException | NullPointerException | ApplicationException
						| DateTimeException e) {
					LOG.debug(e.getMessage());
				} finally {
					if (errorMessage.length() != 0) {
						session.setAttribute("ermsg", errorMessage);
						session.setAttribute("errorIndex", index);
					}
					rs = request.getRequestDispatcher("quotes.jsp");
					rs.forward(request, response);
				}
				break;

			case "Outofi":
				defDate = LocalDateTime.now().toString();
				LOG.debug("Ass01_Servlet: outofill button pressed, redirection proseeded:");
				try {
					Quote q = QuoteReader.readQuoteFromLine(defDate, defOpen, defHigh, defLow, defClose, defVolume);
					QuoteDao.getTheinstance().add(q);
					ql = QuoteDao.getTheinstance().getSqlQuerriesList();
					session.setAttribute("lastQuerries", ql);
				} catch (SQLException | NumberFormatException | IndexOutOfBoundsException | NullPointerException | ApplicationException e) {
					LOG.debug(e.getMessage());
					e.printStackTrace();
				} finally {
					rs = request.getRequestDispatcher("quotes.jsp");
					rs.forward(request, response);
				}
				break;

			case "Drop T":

				LOG.debug("Ass01_Servlet: Drop Table button pressed, redirection proseeded:");
				DataBase.requestDbTableDrop();
				try {
					QuoteDao.getTheinstance().init();
					ql = QuoteDao.getTheinstance().getSqlQuerriesList();
					session.setAttribute("lastQuerries", ql);

				} catch (ApplicationException e) {
					e.printStackTrace();
				} finally {
					rs = request.getRequestDispatcher("quotes.jsp");
					rs.forward(request, response);
				}
				break;

			case "Clear_":
				LOG.debug("Ass01_Servlet: Clear Session button pressed, redirection proseeded:");
				QuoteDao.getTheinstance().getSqlQuerriesList().clear();
				session.setAttribute("lastQuerries", null);
				rs = request.getRequestDispatcher("summary.jsp");
				rs.forward(request, response);
				break;

			default:
				break;
			}
		} else if (pass != null) {
			if (pass.equals("passed")) {
				LOG.debug("passed before through congrats: " + pass);

			} else if ((pass.equals("not")) && (request.getParameter("pass") != null) && StringUtil.isPureAscii(request.getParameter("pass"))) {
				if (DataBase.getTheInstance().getConnection() != null) {
					DataBase.getTheInstance().shutdown();
				}
				boolean authorized = true;
				try {
					String driver;
					String url;
					String user;
					String password;
					new Decipher();
					System.out.println("decipher instance created");

					if (request.getParameter("pass").equals(PASSWORD_FOR_WEB)) {
						LOG.debug("Loading database properties from hardcoded values to to falure to read the file");
						// hardcoding for aws debug purposes
						driver = "org.apache.derby.jdbc.EmbeddedDriver";
						url = "jdbc:derby:derby_jdbc_test;create=true";
						user = "admin";
						password = "admin";

					} else {

						InputStream input = Decipher.readFromFileAndDecrypt(request.getParameter("pass"));
						System.out.println("InputStream instance created");

						Properties properties = new Properties();
						properties.load(input);

						LOG.debug(properties);

						driver = properties.getProperty(DbConstants.DB_DRIVER_KEY);
						url = properties.getProperty(DbConstants.DB_URL_KEY);
						user = properties.getProperty(DbConstants.DB_USER_KEY);
						password = properties.getProperty(DbConstants.DB_PASSWORD_KEY);

						LOG.debug("Loading database properties from hardcoded values to to falure to read the file");

					}

					// InputStream input = new FileInputStream("..\\workspaceEE\\QAD\\WebContent\\WEB-INF\\files\\" +
					// DbConstants.PROPERTIES_FILENAME);

					LOG.debug("Filter loaded:");
					LOG.debug("driver: = " + driver);
					LOG.debug("url: = " + url);
					LOG.debug("user: = " + user);
					LOG.debug("pass: = " + password);

					// DataBase.loadDatabaseDriver(driver);
					// DataBase.init(url, user, password);
					DataBase.connectJDBCToAWSEC2();
					QuoteDao.getTheinstance().init();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ApplicationException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					authorized = true;
					LOG.debug("wrong pass");
					LOG.debug(e);
				}

				if (authorized) {
					LOG.debug("passed through PASSWORD congrats: " + pass);
					session.setAttribute("pass", "passed");
					rs = request.getRequestDispatcher("quotes.jsp");
					rs.forward(request, response);
				} else {
					LOG.debug("Again Wrong password!: " + pass);
					session.setAttribute("wp", "wp");
					rs = request.getRequestDispatcher("pass.jsp");
					rs.forward(request, response);
				}

			} else {
				LOG.debug("something went wrong with pass?");
				session.setAttribute("wp", "wp");
				rs = request.getRequestDispatcher("pass.jsp");
				rs.forward(request, response);
			}
		}
		if (("ru".equals(language)) || ("uk".equals(language)) || ("en".equals(language)) || ("fr".equals(language)))

		{
			LOG.debug("Ass01_Servlet: interface language changed, redirection proseeded:");
			try {
				String lang = request.getParameter("interfLang");
				Locale locale = new Locale(lang.substring(0, 2), lang.substring(3));
				request.setAttribute("language", locale.getLanguage());
				request.setAttribute("country", locale.getCountry());
				CookieUtilities.setCookie(response, "language", locale.getLanguage(), 60 * 60 * 24 * 7);
				CookieUtilities.setCookie(response, "country", locale.getCountry(), 60 * 60 * 24 * 7);
				rs = request.getRequestDispatcher(page);
				rs.forward(request, response);

			} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException | DateTimeException e) {
				response.sendError(500, "Something went wrong!");
				LOG.debug(e.getMessage());
				e.printStackTrace();
			}

		} else {
			LOG.debug("something went wrong...lang?");
			// rs = request.getRequestDispatcher("quotes.jsp");
			// rs.forward(request, response);
		}
		session.setAttribute("ermsg", null);
		session.setAttribute("errorIndex", null);
		errorMessage.setLength(0);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException | SQLException | ApplicationException
				| InvalidKeySpecException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException | SQLException | ApplicationException
				| InvalidKeySpecException e) {
			response.sendError(500, "Something went wrong with doGet!!!");
			e.printStackTrace();
		}
	}

	public static String getUrl(HttpServletRequest request) {
		LOG.debug(request.getRequestURL().toString());
		return request.getRequestURL().toString();
	}

	public static void readInputData(String task, HttpServletRequest request, int index) {
		HttpSession session = request.getSession();
		if (!InputValidator.isValidInput(request.getParameter("Date" + index), DATE_TIME_PATTERN)) {
			LOG.debug(request.getParameter("Date" + index));
			errorMessage.append("Please, provide \"Date\" field in correct format (yyyy-mm-dd hh:mm:ss).<br>");
			session.setAttribute("errDate", index);
			date = defDate;
		} else {
			date = request.getParameter("Date" + index);

		}
		LOG.debug(date);

		if (InputValidator.isValidInput(request.getParameter("Open" + index), PRICE_PATTERN)) {
			open = request.getParameter("Open" + index);
		} else {
			open = defOpen;
			errorMessage.append("Please, provide positive double value for \"Open\" field.<br>");
			session.setAttribute("errOpen", index);
		}
		LOG.debug(open);

		if (InputValidator.isValidInput(request.getParameter("High" + index), PRICE_PATTERN)) {
			high = request.getParameter("High" + index);
		} else {
			high = defHigh;
			errorMessage.append("Please, provide positive double value for \"High\" field.<br>");
			session.setAttribute("errHigh", index);
		}
		LOG.debug(high);

		if (InputValidator.isValidInput(request.getParameter("Low" + index), PRICE_PATTERN)) {
			low = request.getParameter("Low" + index);
		} else {
			low = defLow;
			errorMessage.append("Please, provide positive double value for \"Low\" field.<br>");
			session.setAttribute("errLow", index);
		}
		LOG.debug(low);

		if (InputValidator.isValidInput(request.getParameter("Close" + index), PRICE_PATTERN)) {
			close = request.getParameter("Close" + index);
		} else {
			close = defClose;
			errorMessage.append("Please, provide positive double value for \"Close\" field.<br>");
			session.setAttribute("errClose", index);
		}
		LOG.debug(close);

		if (InputValidator.isValidInput(request.getParameter("Volume" + index), VOLUME_PATTERN)) {
			volume = request.getParameter("Volume" + index);
		} else {
			volume = defVolume;
			errorMessage.append("Please, provide positive double value for \"Volume\" field.<br>");
			session.setAttribute("errVolume", index);
		}
		LOG.debug(volume);
	}
}
