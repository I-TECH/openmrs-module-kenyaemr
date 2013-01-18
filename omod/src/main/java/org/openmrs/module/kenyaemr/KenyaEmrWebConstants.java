package org.openmrs.module.kenyaemr;

/**
 * Web related module constants
 */
public class KenyaEmrWebConstants {

	/**
	 * Time in milliseconds to lockout an IP or user after repeated
	 * failed login attempts
	 */
	public static final int FAILED_LOGIN_LOCKOUT_TIME = 300000; // 5 minutes

	/**
	 * Name of session attribute for temporary reset passwords
	 */
	public static final String SESSION_ATTR_RESET_PASSWORD = "resetPassword";
}
