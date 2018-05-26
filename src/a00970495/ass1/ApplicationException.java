/**
 * Project: QuotesAnalizer_2017
 * File: ApplicationException.java
 * Date: Aug 17, 2017
 * Time: 3:22:46 PM
 */

package a00970495.ass1;

/**
 * @author Ievgen Lytvynenko, A00970495
 *
 */

@SuppressWarnings("serial")
public class ApplicationException extends Exception {

	/**
	 * Construct an ApplicationException
	 *
	 * @param message
	 *            the exception message.
	 */
	public ApplicationException(String message) {
		super(message);
	}

	/**
	 * Construct an ApplicationException
	 *
	 * @param throwable
	 *            a Throwable.
	 */
	public ApplicationException(Throwable throwable) {
		super(throwable);
	}

}
