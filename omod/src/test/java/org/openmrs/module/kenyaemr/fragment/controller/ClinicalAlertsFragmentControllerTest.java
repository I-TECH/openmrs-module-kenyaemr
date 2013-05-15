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

package org.openmrs.module.kenyaemr.fragment.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Tests for {@link ClinicalAlertsFragmentController}
 */
public class ClinicalAlertsFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	private ClinicalAlertsFragmentController controller;

	@Autowired
	private KenyaEmr emr;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		controller = new ClinicalAlertsFragmentController();

		emr.getCalculationManager().refresh();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.fragment.controller.ClinicalAlertsFragmentController#controller()
	 */
	@Test
	public void controller() {
	}

	/**
	 * @see ClinicalAlertsFragmentController#getAlerts(Integer, org.openmrs.module.kenyaemr.KenyaEmr)
	 */
	@Test
	public void getAlerts() {
		List<SimpleObject> alerts = controller.getAlerts(7, emr);

		Assert.assertTrue(alerts.size() >= 1);
		Assert.assertTrue(alerts.get(0).containsKey("message"));

		// TODO once we get all calculations working with the test data, check each one is not an error
	 	//for (SimpleObject obj : alerts) {
			//System.out.println(obj.toJson());
		//}
	}
}