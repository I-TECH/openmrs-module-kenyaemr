/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.web.WebConstants.GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP;

/**
 * Manages IP-level security
 */
public class IPAccessSecurity {

	protected static final Log log = LogFactory.getLog(IPAccessSecurity.class);

	/**
	 * Exception class for illegal access from a locked out IP
	 */
	public static class AccessFromLockedOutIPException extends RuntimeException {
		private AccessFromLockedOutIPException() {
			super("IP address is currently locked out");
		}
	}

	/**
	 * The mapping from user's IP address to the number of failed access attempts
	 */
	private static Map<String, Integer> failedAccessesByIP = new HashMap<String, Integer>();

	/**
	 * The mapping from user's IP address to the time that they were locked out
	 */
	private static Map<String, Date> lockoutTimeByIP = new HashMap<String, Date>();

	/**
	 * Registers a failed access attempt
	 * @param ipAddress the IP address
	 */
	public synchronized static void registerFailedAccess(String ipAddress) {
		int attempts = getFailedAccessesByIP(ipAddress) + 1;
		failedAccessesByIP.put(ipAddress, attempts);

		final int maxAllowed = getMaxAllowedFailedAccesses();

		if (attempts > maxAllowed) {

			// Has lockout time expired?
			Date lockedOutTime = lockoutTimeByIP.get(ipAddress);
			if (lockedOutTime != null && System.currentTimeMillis() - lockedOutTime.getTime() > EmrWebConstants.FAILED_LOGIN_LOCKOUT_TIME) {

				// End lock out, but register a failed attempt
				endLockOut(ipAddress);
				failedAccessesByIP.put(ipAddress, 1);
			}
			else {
				lockOut(ipAddress);
			}
		}
	}

	/**
	 * Registers a successful access attempt
	 * @param ipAddress the IP address
	 * @throws IPAccessSecurity.AccessFromLockedOutIPException if account is currently locked out
	 */
	public synchronized static void registerSuccessfulAccess(String ipAddress) throws AccessFromLockedOutIPException {
		if (isLockedOut(ipAddress)) {
			throw new AccessFromLockedOutIPException();
		}

		endLockOut(ipAddress);
	}

	/**
	 * Locks out an IP address from now
	 * @param ipAddress the IP address
	 */
	public synchronized static void lockOut(String ipAddress) {
		lockoutTimeByIP.put(ipAddress, new Date());
	}

	/**
	 * Ends the locked out period for an IP address
	 * @param ipAddress the IP address
	 */
	public synchronized static void endLockOut(String ipAddress) {
		lockoutTimeByIP.remove(ipAddress);
		failedAccessesByIP.remove(ipAddress);
	}

	/**
	 * Clears all lock outs for all IPs
	 */
	public synchronized static void reset() {
		failedAccessesByIP.clear();
		lockoutTimeByIP.clear();
	}

	/**
	 * Checks if an IP address is currently locked out
	 * @param ipAddress the IP address
	 * @return true if IP is locked out
	 */
	public static boolean isLockedOut(String ipAddress) {
		Date lockedOutTime = lockoutTimeByIP.get(ipAddress);
		return (lockedOutTime != null && (System.currentTimeMillis() - lockedOutTime.getTime()) < EmrWebConstants.FAILED_LOGIN_LOCKOUT_TIME);
	}

	/**
	 * Gets number of failed attempts by IP address
	 * @param ipAddress the IP address
	 * @return the number of failed attempts
	 */
	private static int getFailedAccessesByIP(String ipAddress) {
		Integer attempts = failedAccessesByIP.get(ipAddress);
		return (attempts == null) ? 0 : attempts;
	}

	/**
	 * Gets the maximum allowed number of failed accesses by an IP before lockout
	 * @return the number of accesses
	 */
	private static int getMaxAllowedFailedAccesses() {
		// look up the allowed # of attempts per IP
		Integer allowedLockoutAttempts = 100;

		String allowedLockoutAttemptsGP = Context.getAdministrationService().getGlobalProperty(GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP, "100");
		try {
			allowedLockoutAttempts = Integer.valueOf(allowedLockoutAttemptsGP.trim());
		}
		catch (NumberFormatException nfe) {
			log.error("Unable to format '" + allowedLockoutAttemptsGP + "' from global property " + GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP + " as an integer");
		}

		return allowedLockoutAttempts;
	}
}