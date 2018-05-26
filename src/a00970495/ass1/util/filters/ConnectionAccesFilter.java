package a00970495.ass1.util.filters;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a00970495.ass1.dataBase.DataBase;

public class ConnectionAccesFilter implements Filter {

	private static Logger LOG = LogManager.getLogger();

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		LOG.debug("-----------------------");
		File directory = new File("./");
		System.out.println(directory.getAbsolutePath());
		LOG.debug("ConnectionAccesFilter: doFilter()");
		HttpServletRequest rec = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = rec.getSession();
		RequestDispatcher rs;
		LOG.debug(request.getParameter("pass"));
		try {
			LOG.debug("TRY TO PASS PASS");
			if (session.getAttribute("pass") != null) {
				if (session.getAttribute("pass").equals("passed")) {
					LOG.debug("PASSED AS authorized: " + session.getAttribute("pass"));
					chain.doFilter(rec, res);
				} else if (session.getAttribute("pass").equals("not")) {
					LOG.debug("NOT PASSED : " + session.getAttribute("pass"));
					try {
						if (DataBase.getTheInstance().getConnection() != null) {
							DataBase.getTheInstance().shutdown();
						}
					} catch (SQLException e) {

						e.printStackTrace();
					}
					rs = rec.getRequestDispatcher("pass.jsp");
					rs.forward(rec, res);
				}

			}

			if ((request.getParameter("pass") == null) && (session.getAttribute("pass") == null)) {
				LOG.debug("YOU SHELL NOTT PASS");
				try {
					if (DataBase.getTheInstance().getConnection() != null) {
						DataBase.getTheInstance().shutdown();
					}
				} catch (SQLException e) {

					e.printStackTrace();
				}
				session.setAttribute("pass", "not");
				rs = rec.getRequestDispatcher("pass.jsp");
				rs.forward(rec, res);
			}
		} catch (

		NullPointerException e) {
			LOG.debug(e);
			e.printStackTrace();
		}
		LOG.debug("ConnectionAccesFilter: End Of Filter");

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
