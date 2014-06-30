package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EnrollmentDateCalculationTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private CommonMetadata commonMetadata;

    @Autowired
    private HivMetadata hivMetadata;

    @Before
    public void setUp() throws Exception {
        executeDataSet("dataset/test-concepts.xml");
        commonMetadata.install();
        hivMetadata.install();
    }
    @Test
    public void evaluate_shouldCalculateEnrollmentDateCalculation() throws Exception {
        EncounterType hivProgram = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);

        Date encounterDate = TestUtils.date(2014, 3, 1);
        TestUtils.saveEncounter(TestUtils.getPatient(6), hivProgram, encounterDate);

        List<Integer> ptIds = Arrays.asList(6);
        CalculationResultMap resultMap = new EnrollmentDateCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
        Assert.assertEquals(encounterDate, resultMap.get(6).getValue());

    }
}