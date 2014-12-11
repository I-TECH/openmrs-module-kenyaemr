package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.RelationshipType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;

public class MotherGuardianVisitsCalculationTest extends BaseModuleContextSensitiveTest {
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
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchcs.InfantMotherGuardianRelationsCalculation#evaluate (
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
			TestUtils.saveVisit(mother, visitType, computeWithinRangeEncounterDate(6), computeWithinRangeEncounterDate(6) );
			TestUtils.saveVisit(mother, visitType, computeWithinRangeEncounterDate(5), computeWithinRangeEncounterDate(5));
			TestUtils.saveVisit(guardian, visitType, computeWithinRangeEncounterDate(3), computeWithinRangeEncounterDate(3));
		}

		{
			Patient infant = TestUtils.getPatient(6);
			Patient mother = TestUtils.getPatient(7);
			TestUtils.saveRelationship(mother, mother_relation, infant);
			TestUtils.saveVisit(mother, visitType, computeWithinRangeEncounterDate(2), computeWithinRangeEncounterDate(2));
			TestUtils.saveVisit(mother, visitType, computeWithinRangeEncounterDate(10), computeWithinRangeEncounterDate(10));
			TestUtils.saveVisit(mother, visitType, computeWithinRangeEncounterDate(12), computeWithinRangeEncounterDate(12));

		}

		List<Integer> ptIds = Arrays.asList(2, 6, 999);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reviewPeriod", 4);
		CalculationResultMap resultMap = new MotherGuardianVisitsCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertThat(((ListResult) resultMap.get(7)).getValues().size(), is(1));
		Assert.assertThat(((ListResult) resultMap.get(8)).getValues().size(), is(1));
		Assert.assertNull(resultMap.get(999));
	}

	private Date computeWithinRangeEncounterDate(int months){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -months);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.MILLISECOND);
		return cal.getTime();
	}

}