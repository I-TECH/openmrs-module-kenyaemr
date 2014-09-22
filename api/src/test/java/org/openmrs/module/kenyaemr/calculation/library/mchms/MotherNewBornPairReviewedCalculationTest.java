package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link MotherNewBornPairReviewedCalculation}
 */
public class MotherNewBornPairReviewedCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		mchMetadata.install();
	}

	/**
	 *
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.MotherNewBornPairReviewedCalculation#evaluate (
	 * java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCheckMotherNewBornPairReviewed() {
		//get the consultation form and its encounter type
		EncounterType consultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
		Form postNatalForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_POSTNATAL_VISIT);
		Concept dateOfConfinement = Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT);

		//have a female patient and give the a delivery date
		//give patient #7 a delevry date of 01/07/2014
		Patient p7 = TestUtils.getPatient(7);
		TestUtils.saveObs(p7, dateOfConfinement, TestUtils.date(2014, 7, 10), TestUtils.date(2014, 7, 15));
		//give patient #7 an encounter 7 days later
		TestUtils.saveEncounter(p7, postNatalForm, TestUtils.date(2014, 7, 20));

		List<Integer> cohort = Arrays.asList(2, 5, 7);
		CalculationResultMap resultMap = new MotherNewBornPairReviewedCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // this patient passes the criteria
		Assert.assertFalse((Boolean) resultMap.get(5).getValue()); // NOT meeting any of the conditions set

	}

}
