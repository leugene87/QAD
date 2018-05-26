/**
 * Project: QuotesAnalizer_2017
 * File: DbConstants.java
 * Date: Oct 17, 2017
 * Time: 2:25:51 PM
 */

package a00970495.ass1.dataBase;

/**
 * @author Ievgen Lytvynenko, A00970495
 *
 */

public interface DbConstants {
	String PROPERTIES_FILENAME = "dbh.properties"; // password for dbh "you are hired" :)
	String DB_DRIVER_KEY = "db.driver";
	String DB_URL_KEY = "db.url";
	String DB_USER_KEY = "db.user";
	String DB_PASSWORD_KEY = "db.password";

	// String TABLE_ROOT = Quote.baseCurrencyName + Quote.counterCurrencyName + Quote.period;
	String TABLE_NAME = "Quotes";

	// for MYSQL
	String PUBLIC_DNS = "ec2-18-217-182-151.us-east-2.compute.amazonaws.com";
	String PORT = "3306";
	String DATABASE = "EC2amazon";
	String REMOTE_DATABASE_USERNAME = "EC2amazon";
	String DATABASE_USER_PASSWORD = "EC2amazon";

}
