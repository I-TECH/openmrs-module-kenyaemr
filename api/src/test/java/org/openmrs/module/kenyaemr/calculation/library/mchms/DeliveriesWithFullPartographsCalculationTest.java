package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeliveriesWithFullPartographsCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @verifies deliveries with full partographs
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.DeliveriesWithFullPartographsCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void valuate_shouldCalculateDeliveriesWithFullPartographs() throws Exception {

		Form deliveryForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);
		EncounterType ancEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);

		// For the purposes of this test, everyone is a woman
		{
			/**
			 * save delivery form with partial partograph
			 */
			Patient patient = TestUtils.getPatient(7);
			Date encounterDate =  TestUtils.date(2014, 7, 20);
			Obs[] deliveryObs = {
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.PREGNANCY_DURATION_AMOUNT), 6, encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.METHOD_OF_DELIVERY), Dictionary.getConcept(Metadata.Concept.OTHER_NON_CODED) , encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.DATE_OF_CONFINEMENT), encounterDate , encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.ESTIMATED_MATERNAL_BLOOD_LOSS_QUALITATIVE), Dictionary.getConcept(Metadata.Concept.NONE), encounterDate),
					saveObs(patient, Dictionary.getConcept(Metadata.Concept.MATERNAL_CONDITION_DURING_PUERPERIUM), "OK", encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_1_MINUTE), 5, encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_5_MINUTES), 5, encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_10_MINUTES), 5, encounterDate)
			};

			TestUtils.saveEncounter(patient, ancEncounterType, deliveryForm, TestUtils.date(2014, 7, 20), deliveryObs);
		}

		{
			/**
			 * add full partograph
			 */
			Patient patient = TestUtils.getPatient(8);
			Date encounterDate =  TestUtils.date(2014, 7, 20);
			Obs[] deliveryObs = {
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.PREGNANCY_DURATION_AMOUNT), 6, encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.METHOD_OF_DELIVERY), Dictionary.getConcept(Metadata.Concept.OTHER_NON_CODED) , encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.DATE_OF_CONFINEMENT), encounterDate , encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.ESTIMATED_MATERNAL_BLOOD_LOSS_QUALITATIVE), Dictionary.getConcept(Metadata.Concept.NONE), encounterDate),
					saveObs(patient, Dictionary.getConcept(Metadata.Concept.MATERNAL_CONDITION_DURING_PUERPERIUM), "OK", encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_1_MINUTE), 5, encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_5_MINUTES), 5, encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_10_MINUTES), 5, encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.NEONTAL_RESUSCITATION), Dictionary.getConcept(Metadata.Concept.YES), encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.LOCATION_OF_BIRTH), Dictionary.getConcept(Metadata.Concept.UNKNOWN), encounterDate),
					TestUtils.saveObs(patient, Dictionary.getConcept(Metadata.Concept.DELIVERY_ASSISTANT), Dictionary.getConcept(Metadata.Concept.FAMILY_MEMBER), encounterDate)
			};

			TestUtils.saveEncounter(patient, ancEncounterType, deliveryForm, TestUtils.date(2014, 7, 20), deliveryObs);

		}

		List<Integer> ptIds = Arrays.asList(7, 8, 999);
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("onOrAfter", 4);

		CalculationResultMap resultMap = new DeliveriesWithFullPartographsCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		//Assert.assertTrue((Boolean) resultMap.get(8).getValue());
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // has no recorded status

	}

	private Obs saveObs(Patient patient, Concept concept, String val, Date date) {
		Obs obs = new Obs(patient, concept, date, null);
		obs.setValueText(val);
		return Context.getObsService().saveObs(obs, null);
	}
}