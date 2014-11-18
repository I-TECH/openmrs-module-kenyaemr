package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.databasechange.BooleanConceptChangeSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfantsDNAPCRCalculationTest extends BaseModuleContextSensitiveTest {
	@Autowired
	private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setUp() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		mchMetadata.install();
	}

	/**
	 * @verifies infants who received dna pcr test on their 6th week after birth
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchcs.InfantsDNAPCRCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void valuate_shouldCalculateInfantsWhoReceivedDNAPCRTestAtSixWeeks() throws Exception {

		Form followUpForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHCS_FOLLOW_UP);
		EncounterType mchcsConsultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_CONSULTATION);

		// For the purposes of this test, everyone is a woman
		{
			/**
			 * save patient with dna pcr test carried 41 days after birth
			 */
			Patient patient = TestUtils.getPatient(7);
			patient.setBirthdate(TestUtils.date(2014, 8, 10));
			Obs[] dnaPcrObs = {
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.HIV_DNA_POLYMERASE_CHAIN_REACTION), Dictionary.getConcept(Metadata.Concept.DETECTED), TestUtils.date(2014, 9, 20)),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.HIV_DNA_POLYMERASE_CHAIN_REACTION), Dictionary.getConcept(Metadata.Concept.NOT_DETECTED) , TestUtils.date(2014, 9, 30)),
			};

			TestUtils.saveEncounter(patient, mchcsConsultationEncounterType, followUpForm, TestUtils.date(2014, 9, 30), dnaPcrObs);
		}

		{
			/**
			 * save patient with dna pcr test carried outside 6 weeks margin
			 */
			Patient patient = TestUtils.getPatient(8);
			patient.setBirthdate(TestUtils.date(2014, 8, 10));
			Obs[] dnaPcrObs = {
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.HIV_DNA_POLYMERASE_CHAIN_REACTION), Dictionary.getConcept(Metadata.Concept.DETECTED), TestUtils.date(2014, 8, 30)),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.HIV_DNA_POLYMERASE_CHAIN_REACTION), Dictionary.getConcept(Metadata.Concept.NOT_DETECTED) , TestUtils.date(2014, 9, 10)),
			};

			TestUtils.saveEncounter(patient, mchcsConsultationEncounterType, followUpForm, TestUtils.date(2014, 9, 30), dnaPcrObs);
		}

		{
			/**
			 * save patient with birth date set but with no dna-pcr obs
			 */
			Patient patient = TestUtils.getPatient(6);
			Obs[] dnaPcrObs = {
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.HIV_DNA_POLYMERASE_CHAIN_REACTION), Dictionary.getConcept(Metadata.Concept.DETECTED), TestUtils.date(2014, 8, 30)),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.HIV_DNA_POLYMERASE_CHAIN_REACTION), Dictionary.getConcept(Metadata.Concept.NOT_DETECTED) , TestUtils.date(2014, 9, 10)),
			};

			TestUtils.saveEncounter(patient, mchcsConsultationEncounterType, followUpForm, TestUtils.date(2014, 9, 30));
		}

		List<Integer> ptIds = Arrays.asList(7, 8, 999, 6);
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("durationAfterBirth", 6);

		CalculationResultMap resultMap = new InfantsDNAPCRCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());
		Assert.assertNull(resultMap.get(999)); // voided, no dob and pcr obs
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // the patient has no dna-pcr obs

	}

}