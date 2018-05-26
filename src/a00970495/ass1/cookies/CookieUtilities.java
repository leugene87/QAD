package a00970495.ass1.cookies;


import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Two static methods for use in cookie handling.
 */

public class CookieUtilities {

	/**
	 * Given the request object, a name, and a default value,
	 * this method tries to find the value of the cookie with
	 * the given name. If no cookie matches the name,
	 * the default value is returned.
	 */

	public static String getCookieValue(HttpServletRequest request, String cookieName, String defaultValue) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookieName.equals(cookie.getName())) {
					return (cookie.getValue());
				}
			}
		}
		return (defaultValue);
	}

	/**
	 * Given the request object and a name, this method tries
	 * to find and return the cookie that has the given name.
	 * If no cookie matches the name, null is returned.
	 */

	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookieName.equals(cookie.getName())) {
					return (cookie);
				}
			}
		}
		return (null);
	}

	public static void setCookie(HttpServletResponse response, String name, String valeur, int maxAge) throws IOException {
		Cookie cookie = new Cookie(name, URLEncoder.encode(valeur, "UTF-8"));
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static String getDeCodedValue(String cookieName, String value) throws IOException {
		Cookie cookie = new Cookie(cookieName, URLDecoder.decode(value, "UTF-8"));
		return cookie.getValue();
	}

}
