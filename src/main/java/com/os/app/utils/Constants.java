package com.os.app.utils;

import java.util.Locale;

public class Constants {

	public static final String STATUS_STARTED = "STARTED";

	public static final String STATUS_NOT_STARTED = "NOT STARTED";

	public static final int PAGE_SIZE = 10;

	public final static boolean DEFAULT_FALSE = false;
	public final static String DEFAULT_TIMEZONE = "Asia/Singapore";
	public final static String SERVER_TIMEZONE = "Asia/Singapore";
	public final static int DEFAULT_ZERO = 0;

	public static final String AUTH_HEADER_NAME = "x-auth-token";
	public static final long TOKEN_TIMEOUT = 1000 * 60 * 60 * 24;
	public static final int TOKEN_EXPIRE_DURATION = -2;
	public final static String SYSTEMADMIN_DOMAIN = "sysadmin";
	public final static String LOCATIONUPDATE_IMAGE_PATH = "%s/api/locationUpdate/%d/image";
	public final static String SYSTEMUSER_IMAGE_PATH = "%s/api/employee/%d/profilePhoto";

	public static final Locale LOCALE = Locale.US;

}
