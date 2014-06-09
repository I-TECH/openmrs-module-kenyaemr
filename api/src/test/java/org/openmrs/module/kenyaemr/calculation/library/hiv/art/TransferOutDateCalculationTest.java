package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * Test for {@link TransferOutDateCalculation}
 */

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

    @Test
    public void evaluate_shouldCalculateTransferOutDateCalculation() throws Exception {

//        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        EncounterType hivDiscontinuationEncounterType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_DISCONTINUATION);
        Form hivDiscontinuationForm = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_DISCONTINUATION);

        Concept transferOutReasonConcept = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);

        Patient pat2 = TestUtils.getPatient(2);
        Patient pat6 = TestUtils.getPatient(6);

//        TestUtils.enrollInProgram(pat2, hivProgram, TestUtils.date(2014, 3, 1));
//        TestUtils.enrollInProgram(pat6, hivProgram, TestUtils.date(2014, 3, 10));

        Obs[] discontinuationObss = {TestUtils.saveObs(pat2, transferOutReasonConcept, Dictionary.getConcept(Dictionary.DIED), new Date()),
                TestUtils.saveObs(pat6, transferOutReasonConcept, Dictionary.getConcept(Dictionary.TRANSFERRED_OUT), new Date())};

        Date encounterDate = TestUtils.date(2014, 3, 10);
        TestUtils.saveEncounter(pat2, hivDiscontinuationEncounterType, hivDiscontinuationForm, encounterDate, discontinuationObss);
        TestUtils.saveEncounter(pat6, hivDiscontinuationEncounterType, hivDiscontinuationForm, encounterDate, discontinuationObss);

        Context.flushSession();

        List<Integer> ptIds = Arrays.asList(2,6);

        CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new TransferOutDateCalculation());

        Assert.assertTrue(encounterDate.equals(resultMap.get(6).getValue()));
        Assert.assertNull(resultMap.get(2));

    }
}