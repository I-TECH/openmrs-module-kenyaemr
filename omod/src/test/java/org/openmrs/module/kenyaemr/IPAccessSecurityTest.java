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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.IPAccessSecurity;
import org.openmrs.web.WebConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class IPAccessSecurityTest extends BaseModuleWebContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(WebConstants.GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(WebConstants.GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP);
		}
		gp.setPropertyValue("10");
		Context.getAdministrationService().saveGlobalProperty(gp);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.IPAccessSecurity
	 */
	@Test
	public void test() throws Exception {

		String ipAddress1 = "127.0.0.1";
		String ipAddress2 = "127.0.1.1";
		IPAccessSecurity.reset();

		// Check IP isn't locked out after a single failed attempt
		Assert.assertFalse(IPAccessSecurity.isLockedOut(ipAddress1));
		IPAccessSecurity.registerFailedAccess(ipAddress1);
		Assert.assertFalse(IPAccessSecurity.isLockedOut(ipAddress1));

		// Check IP is locked out after too many failed attempts
		for (int i = 0; i < 10; ++i) {
			IPAccessSecurity.registerFailedAccess(ipAddress1);
		}
		Assert.assertTrue(IPAccessSecurity.isLockedOut(ipAddress1));

		// Check other IP isn't locked out
		Assert.assertFalse(IPAccessSecurity.isLockedOut(ipAddress2));

		// Check successful access throws exception
		try {
			IPAccessSecurity.registerSuccessfulAccess(ipAddress1);
			Assert.fail();
		}
		catch (IPAccessSecurity.AccessFromLockedOutIPException ex) {
		}

		// Check explicit unlock
		IPAccessSecurity.endLockOut(ipAddress1);
		Assert.assertFalse(IPAccessSecurity.isLockedOut(ipAddress1));
	}
}