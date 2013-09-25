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

package org.openmrs.module.kenyaemr.calculation.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.VisitType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.*;

/**
 * Tests for {@link OnMedicationCalculation}
 */
public class OnMedicationCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-metadata.xml");
	}

	/**
	 * @see OnMedicationCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {
		PatientService ps = Context.getPatientService();
		VisitType outpatientType = MetadataUtils.getVisitType(CommonMetadata.VisitType.OUTPATIENT);
		EncounterType consultationType = MetadataUtils.getEncounterType(CommonMetadata.EncounterType.CONSULTATION);
		Concept medOrders = Dictionary.getConcept(Dictionary.MEDICATION_ORDERS);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept flucanozole = Dictionary.getConcept(Dictionary.FLUCONAZOLE);

		// Give #2 Dapsone on May 1st
		TestUtils.saveVisit(ps.getPatient(2), outpatientType, TestUtils.date(2012, 5, 1), TestUtils.date(2012, 5, 1, 12, 0, 0),
				TestUtils.saveEncounter(ps.getPatient(2), consultationType, TestUtils.date(2012, 5, 1),
						TestUtils.saveObs(ps.getPatient(2), medOrders, dapsone, TestUtils.date(2012, 5, 1))
				)
		);

		// Give #6 Dapsone on May 1st but subsequent visit on June 1st with no such order
		TestUtils.saveVisit(ps.getPatient(6), outpatientType, TestUtils.date(2012, 5, 1), TestUtils.date(2012, 5, 1, 12, 0, 0),
				TestUtils.saveEncounter(ps.getPatient(6), consultationType, TestUtils.date(2012, 5, 1),
						TestUtils.saveObs(ps.getPatient(6), medOrders, dapsone, TestUtils.date(2012, 5, 1))
				)
		);
		TestUtils.saveVisit(ps.getPatient(6), outpatientType, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 1, 12, 0, 0),
				TestUtils.saveEncounter(ps.getPatient(6), consultationType, TestUtils.date(2012, 6, 1))
		);

		// Give #7 Flucanozole on May 1st
		TestUtils.saveVisit(ps.getPatient(7), outpatientType, TestUtils.date(2012, 5, 1), TestUtils.date(2012, 5, 1, 12, 0, 0),
				TestUtils.saveEncounter(ps.getPatient(7), consultationType, TestUtils.date(2012, 5, 1),
						TestUtils.saveObs(ps.getPatient(7), medOrders, flucanozole, TestUtils.date(2012, 5, 1))
				)
		);

		// Give #8 Dapsone on Jan 1st
		TestUtils.saveVisit(ps.getPatient(8), outpatientType, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 1, 12, 0, 0),
				TestUtils.saveEncounter(ps.getPatient(8), consultationType, TestUtils.date(2012, 1, 1),
						TestUtils.saveObs(ps.getPatient(8), medOrders, dapsone, TestUtils.date(2012, 1, 1))
				)
		);
		
		List<Integer> cohort = Arrays.asList(2, 6, 7, 8);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("drugs", Collections.singleton(dapsone));
		PatientCalculationContext context = EmrTestUtils.calculationContext(TestUtils.date(2012, 6, 30));

		CalculationResultMap resultMap = new OnMedicationCalculation().evaluate(cohort, params, context);
		Assert.assertTrue((Boolean) resultMap.get(2).getValue());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // subsequent visit
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // wrong drug
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // visit date to old
	}
}