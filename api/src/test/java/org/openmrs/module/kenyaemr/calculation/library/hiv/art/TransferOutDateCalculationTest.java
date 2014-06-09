package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TransferOutDateCalculationTest extends BaseModuleContextSensitiveTest {

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

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void evaluate_shouldCalculateTransferOutDateCalculation() throws Exception {

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        EncounterType hivDiscontinuationEncounterType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_DISCONTINUATION);
        Form hivDiscontinuationForm = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_DISCONTINUATION);

        Concept transferOutReasonConcept = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);

        Patient pat2 = TestUtils.getPatient(2);
        Patient pat3 = TestUtils.getPatient(2);

        TestUtils.enrollInProgram(pat2, hivProgram, new Date());
        TestUtils.enrollInProgram(pat3, hivProgram, new Date());

        Obs[] discontinuationObss = {TestUtils.saveObs(pat2, transferOutReasonConcept, Dictionary.getConcept(Dictionary.DIED), new Date()),
                TestUtils.saveObs(pat3, transferOutReasonConcept, Dictionary.getConcept(Dictionary.TRANSFERRED_OUT), new Date())};

        Date encounterDate = new Date();
        TestUtils.saveEncounter(pat2, hivDiscontinuationEncounterType, hivDiscontinuationForm, encounterDate, discontinuationObss);
        TestUtils.saveEncounter(pat3, hivDiscontinuationEncounterType, hivDiscontinuationForm, encounterDate, discontinuationObss);

        Context.flushSession();

        List<Integer> ptIds = Arrays.asList(2);

        CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new TransferOutDateCalculation());

//        Assert.assertTrue(encounterDate.equals((Date) resultMap.get(3).getValue()));
        Assert.assertTrue(resultMap.get(2) == null);

    }
}