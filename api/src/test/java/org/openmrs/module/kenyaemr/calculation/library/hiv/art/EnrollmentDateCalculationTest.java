package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by agnes on 6/11/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//@Transactional
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
//@ContextConfiguration()locations = {"classpath:applicationContext.xml"})
public class EnrollmentDateCalculationTest extends BaseModuleContextSensitiveTest {
//    @Autowired
//    private ContextDAO contextDAO;

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


        Concept enrolled = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);

        Patient pt = TestUtils.getPatient(6);

//        Date encounterDate = TestUtils.date(2014, 6, 10);
        Obs[] enrollmentObs = {TestUtils.saveObs(pt, enrolled, Dictionary.getConcept(Dictionary.HIV_PROGRAM), new Date())};


        Date encounterDate = TestUtils.date(2014, 6, 10);
        TestUtils.saveEncounter(TestUtils.getPatient(6), hivProgram, encounterDate,enrollmentObs);

        List<Integer> ptIds = Arrays.asList(6);
        CalculationResultMap resultMap = new EnrollmentDateCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
        Assert.assertEquals(encounterDate, resultMap.get(6).getValue());

    }
}
