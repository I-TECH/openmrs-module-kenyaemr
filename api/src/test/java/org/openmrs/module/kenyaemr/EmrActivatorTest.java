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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.ModuleActivator;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link EmrActivator}
 */
public class EmrActivatorTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
	}

	@Test
	public void integration() {
		ModuleActivator activator = new EmrActivator();
		activator.willStart();
		activator.started();
		activator.willRefreshContext();

		// Can't currently refresh all EMR content as takes too long (e.g. ~9000 locations)
		//activator.contextRefreshed();

		activator.willStop();
		activator.stopped();
	}
}