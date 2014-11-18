package org.openmrs.module.kenyaemr.calculation.library.mchcs;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.RelationshipType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfantMotherOrGuardianPairVisitsCalculationTest extends BaseModuleContextSensitiveTest {
	@Autowired
	CommonMetadata commonMetadata;

	private RelationshipType mother_relation;
	private RelationshipType guardian_relation;
	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		commonMetadata.install();

		mother_relation = Context.getPersonService().getRelationshipType(2);
		guardian_relation = MetadataUtils.existing(RelationshipType.class, CommonMetadata._RelationshipType.GUARDIAN_DEPENDANT);
	}

	/**
	 * @verifies all mother and/or guardian relations for a patient
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchcs.InfantMotherOrGuardianPairVisitsCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateVisitsWithinAPeriod() throws Exception {
		VisitType visitType = Context.getVisitService().getVisitType(1);

		{
			Patient infant = TestUtils.getPatient(2);
			Patient mother = TestUtils.getPatient(7);
			Patient guardian = TestUtils.getPatient(8);
			TestUtils.saveRelationship(mother, mother_relation, infant);
			TestUtils.saveRelationship(guardian, guardian_relation, infant);
			TestUtils.saveVisit(mother, visitType, TestUtils.date(2014, 5, 10, 10, 30, 00), TestUtils.date(2014, 5, 10, 12, 30, 00) );
			TestUtils.saveVisit(mother, visitType, TestUtils.date(2014, 7, 10, 10, 30, 00), TestUtils.date(2014, 7, 10, 12, 30, 00));
			TestUtils.saveVisit(guardian, visitType, TestUtils.date(2014, 8, 10, 10, 30, 00), TestUtils.date(2014, 8, 10, 12, 30, 00));
			TestUtils.saveVisit(infant, visitType, TestUtils.date(2014, 8, 10, 10, 30, 00), TestUtils.date(2014, 8, 10, 12, 30, 00));
		}

		{
			Patient infant = TestUtils.getPatient(6);
			Patient mother = TestUtils.getPatient(7);
			TestUtils.saveRelationship(mother, mother_relation, infant);
			TestUtils.saveVisit(mother, visitType, TestUtils.date(2014, 9, 10, 10, 30, 00), TestUtils.date(2014, 9, 10, 12, 30, 00));
			TestUtils.saveVisit(mother, visitType, TestUtils.date(2014, 1, 10, 10, 30, 00), TestUtils.date(2014, 1, 10, 12, 30, 00));
			TestUtils.saveVisit(mother, visitType, TestUtils.date(2014, 2, 10, 10, 30, 00), TestUtils.date(2014, 2, 10, 12, 30, 00));

		}

		List<Integer> ptIds = Arrays.asList(2, 6, 999);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reviewPeriod", 4);
		Date endDate = TestUtils.date(2014, 10, 13);
		PatientCalculationContext p = Context.getService(PatientCalculationService.class).createCalculationContext();
		p.setNow(endDate);
		CalculationResultMap resultMap = new InfantMotherOrGuardianPairVisitsCalculation().evaluate(ptIds, params, p);

		Assert.assertTrue((Boolean) resultMap.get(2).getValue());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());
		Assert.assertFalse((Boolean) resultMap.get(999).getValue());
	}

}