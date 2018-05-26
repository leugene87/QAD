
package a00970495.ass1.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {

	/**
	 * 
	 */
	public InputValidator() {

	}

	public static boolean isValidInput(String input, String pattern) {
		Pattern patt = Pattern.compile(pattern);
		Matcher match = patt.matcher(input);
		return match.matches();
	}
}
