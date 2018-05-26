/**
 * Project: QuotesAnalizer_2017
 * File: Quote.java
 * Date: Aug 17, 2017
 * Time: 2:21:01 PM
 */

package a00970495.ass1.quote;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Ievgen Lytvynenko, A00970495
 *
 */

public class Quote {
	private static Logger LOG = LogManager.getLogger();
	public static final int ATTRIBUTE_COUNT = 7;
	public static String baseCurrencyName = "GBP";
	public static String counterCurrencyName = "USD";
	public static int period = 1;
	private LocalDateTime dateTime;
	private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
	private double open;
	private double high;
	private double low;
	private double close;
	private int volume;
	// private boolean isBearCandle;

	/**
	 * @return the baseCurrencyName
	 */
	public static String getBaseCurrencyName() {
		return baseCurrencyName;
	}

	/**
	 * @param baseCurrencyName
	 *            the baseCurrencyName to set
	 */
	public static void setBaseCurrencyName(String baseCurrencyName) {
		Quote.baseCurrencyName = baseCurrencyName;
	}

	/**
	 * @return the counterCurrencyName
	 */
	public static String getCounterCurrencyName() {
		return counterCurrencyName;
	}

	/**
	 * @param counterCurrencyName
	 *            the counterCurrencyName to set
	 */
	public static void setCounterCurrencyName(String counterCurrencyName) {
		Quote.counterCurrencyName = counterCurrencyName;
	}

	/**
	 * @param period
	 *            the period to set
	 */
	public void setPeriod(int period) {
		Quote.period = period;
	}

	public static class Builder {
		// required param
		private LocalDateTime dateTime;
		private DayOfWeek dayOfWeek;
		private double open;
		private double high;
		private double low;
		private double close;
		// optional
		private int volume;

		public Builder(LocalDateTime dateTime, DayOfWeek dayOfWeek, double open, double high, double low, double close) {
			this.dateTime = dateTime;
			this.dayOfWeek = dayOfWeek;
			this.open = open;
			this.high = high;
			this.low = low;
			this.close = close;
		}

		/**
		 * @param dayOfWeek
		 *            the dayOfWeek to set
		 */
		public Builder setDayOfWeek(DayOfWeek dayOfWeek) {
			this.dayOfWeek = dayOfWeek;
			return this;
		}

		/**
		 * @param dayOfWeek
		 *            the dayOfWeek to set
		 */
		public Builder setDayOfWeek(String dayOfWeek) {
			this.dayOfWeek = DayOfWeek.valueOf(dayOfWeek);
			return this;
		}

		/**
		 * @param dateTime
		 *            the dateTime to set
		 */
		public Builder setDateTime(LocalDateTime dateTime) {
			this.dateTime = dateTime;
			return this;
		}

		/**
		 * @param open
		 *            the open to set
		 */
		public Builder setOpen(double open) {
			this.open = open;
			return this;
		}

		/**
		 * @param high
		 *            the high to set
		 */
		public Builder setHigh(double high) {
			this.high = high;
			return this;
		}

		/**
		 * @param low
		 *            the low to set
		 */
		public Builder setLow(double low) {
			this.low = low;
			return this;
		}

		/**
		 * @param close
		 *            the close to set
		 */
		public Builder setClose(double close) {
			this.close = close;
			return this;
		}

		/**
		 * @param volume
		 *            the volume to set
		 */
		public Builder setVolume(int volume) {
			this.volume = volume;
			return this;
		}

		public Quote build() {
			return new Quote(this);
		}
	}

	public Quote(Builder builder) {
		this.dateTime = builder.dateTime;
		this.dayOfWeek = builder.dayOfWeek;
		this.open = builder.open;
		this.high = builder.high;
		this.low = builder.low;
		this.close = builder.close;
		this.volume = builder.volume;
	}

	/**
	 * @return the dateTime
	 */
	public LocalDateTime getDateTime() {

		return dateTime;
	}

	/**
	 * @return the dateTime String
	 */
	public String getDateTimeForSQL() {
		String dateTimeString = null;
		String date = null;
		String time = null;
		try {
			String dt = dateTime.toString();
			date = dt.substring(0, 10);
			time = dt.substring(11);
		} catch (Exception e) {
			LOG.debug(e.getMessage());
		}
		dateTimeString = date + " " + time + ":00";
		return dateTimeString;
	}

	/**
	 * @return the dateTime
	 */
	// public String getDateTimeForSQL() {
	//
	//
	// return dateTime;
	// }
	//
	//
	//
	// YYYY-MM-DD HH:MI:SS

	/**
	 * @param dateTime
	 *            the dateTime to set
	 */
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @return the baseCurrencyName
	 */
	public void incrementDay() {
		this.setDateTime(this.dateTime.plusDays(1));
	}

	/**
	 * @return the dayOfWeek
	 */
	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * @param dayOfWeek
	 *            the dayOfWeek to set
	 */
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * @return the open
	 */
	public double getOpen() {
		return open;
	}

	/**
	 * @param open
	 *            the open to set
	 */
	public void setOpen(double open) {
		this.open = open;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * @param high
	 *            the high to set
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}

	/**
	 * @param low
	 *            the low to set
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 * @return the close
	 */
	public double getClose() {
		return close;
	}

	/**
	 * @param close
	 *            the close to set
	 */
	public void setClose(double close) {
		this.close = close;
	}

	/**
	 * @return the period
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * @return the volume
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * @param volume
	 *            the volume to set
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

	/**
	 * @return candle size (high - low)
	 */
	public double getCandleSize() {
		return this.getHigh() - this.getLow();
	}

	/**
	 * @return candle's body size [(open - close)]
	 */
	public double getBodySize() {
		if (this.getClose() >= this.getOpen()) {
			return this.getClose() - this.getOpen();
		} else {
			return this.getOpen() - this.getClose();
		}

	}

	/**
	 * @return candle's UP move (high - open)
	 */
	public boolean getIsBearCandle() {
		if (this.getClose() < this.getOpen()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return candle's UP move (high - open)
	 */
	public double getUpMove() {
		return this.getHigh() - this.getOpen();
	}

	/**
	 * @return candle's DOWN move (open - low)
	 */
	public double getDownMove() {
		return this.getOpen() - this.getLow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Quote [dateTime=" + dateTime + ", dayOfWeek=" + dayOfWeek + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close
				+ ", volume=" + volume + "]";
	}

}
