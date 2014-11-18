package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Program;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link PregnantWithANCVisitsCalculation}
 */

public class PregnantWithANCVisitsCalculationTest extends BaseModuleContextSensitiveTest {

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
     * @verifies calculate recorded pregnancy status at ART start for all patients
     * @see org.openmrs.module.kenyaemr.calculation.library.hiv.art.PregnantAtArtStartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     */
    @Test
    public void evaluate_shouldCalculateWomenWithANCVisits() throws Exception {
        Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);
        Concept yes = Dictionary.getConcept(Dictionary.YES);
        Concept no = Dictionary.getConcept(Dictionary.NO);
        Form ancForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ANTENATAL_VISIT);
        EncounterType ancEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
        Program program = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

        // For the purposes of this test, everyone is a woman
        {
            Patient patient = TestUtils.getPatient(7);
            TestUtils.enrollInProgram(patient, program, TestUtils.date(2014, 1, 1));
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 1, 20));
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 1, 20));
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 2, 20));
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 3, 20));
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 4, 20));
        }

        {
            Patient patient = TestUtils.getPatient(8);
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 1, 20));
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 1, 20));
            TestUtils.saveEncounter(patient, ancEncounterType, ancForm, TestUtils.date(2014, 2, 20));
            TestUtils.saveObs(patient, pregnancyStatus, yes, TestUtils.date(2014, 9, 10));
            TestUtils.saveObs(patient, pregnancyStatus, yes, TestUtils.date(2014, 8, 10));
            TestUtils.saveObs(patient, pregnancyStatus, yes, TestUtils.date(2014, 7, 10));
        }

        List<Integer> ptIds = Arrays.asList(7, 8, 999);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("visits", 4);

        CalculationResultMap resultMap = new PregnantWithANCVisitsCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());

        Assert.assertTrue((Boolean) resultMap.get(7).getValue());
        Assert.assertFalse((Boolean) resultMap.get(8).getValue());
        Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // has no recorded status
    }

}