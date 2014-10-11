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