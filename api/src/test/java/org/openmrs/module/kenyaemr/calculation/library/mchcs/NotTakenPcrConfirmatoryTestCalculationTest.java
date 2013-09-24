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
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchcs.NotTakenPcrConfirmatoryTestCalculation }
 */
public class NotTakenPcrConfirmatoryTestCalculationTest extends BaseModuleContextSensitiveTest{

}
	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-metadata.xml");
	}
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.NotTakenPcrConfirmatoryTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate infants exited from HEI care and have no pcr confirmatory test
	 */
	@Test
	public void evaluate_shouldCalculateNotTakenPcrConfirmatoryTest() throws Exception {
	}


}
