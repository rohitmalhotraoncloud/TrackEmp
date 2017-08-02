package com.os.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eugene G. Ustimenko
 * @date Jan 27, 2015
 */
public class Utils {

	private Utils() {
	}

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	public static Date changeTimeZone(Date date, TimeZone fromTimeZone, TimeZone toTimeZone) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(fromTimeZone);
		calendar.setTime(date);
		calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
		if (fromTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
		}
		calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());

		if (toTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
		}

		return calendar.getTime();
	}

	public static Date getDate(String mmddyyyy) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {
			return dateFormat.parse(mmddyyyy);
		} catch (ParseException e) {
			logger.error("Could not parse date " + mmddyyyy, e);
		}
		return null;
	}

	public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
}
