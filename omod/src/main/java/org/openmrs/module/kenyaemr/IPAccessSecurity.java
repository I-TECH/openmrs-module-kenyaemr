/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
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