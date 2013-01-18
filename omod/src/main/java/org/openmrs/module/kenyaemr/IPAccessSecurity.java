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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages IP-level security
 */
public class IPAccessSecurity {

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

		if (attempts > KenyaEmrWebConstants.MAX_ALLOWED_LOGIN_ATTEMPTS) {

			// Has lockout time expired?
			Date lockedOutTime = lockoutTimeByIP.get(ipAddress);
			if (lockedOutTime != null && System.currentTimeMillis() - lockedOutTime.getTime() > KenyaEmrWebConstants.FAILED_LOGIN_LOCKOUT_TIME) {

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
		return (lockedOutTime != null && (System.currentTimeMillis() - lockedOutTime.getTime()) < KenyaEmrWebConstants.FAILED_LOGIN_LOCKOUT_TIME);
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
}