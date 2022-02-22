/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test for {@link NeedsViralLoadTestCalculation}
 *
 */
public class NeedsViralLoadTestCalculationTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private CommonMetadata commonMetadata;

    @Autowired
    private HivMetadata hivMetadata;

    /**
     * Setup each test
     */
    @Before
    public void setup() throws Exception {
        executeDataSet("dataset/test-concepts.xml");

        commonMetadata.install();
        hivMetadata.install();
    }
    /**
     * @see NeedsViralLoadTestCalculation#getFlagMessage()
     */
    @Test
    public void getFlagMessage() {
        Assert.assertThat(new NeedsViralLoadTestCalculation().getFlagMessage(), notNullValue());
    }
    /**
     * @see NeedsViralLoadTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     * @verifies determine whether patients need a Viral load test
     */
    @Test
    public void evaluate_shouldDetermineWhetherPatientsNeedsViralLoadTest() throws Exception {

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program mchProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
        Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);

        // Enroll patients #6, #7 and #8  #9 in the HIV Program
        TestUtils.enrollInProgram(TestUtils.getPatient(4), hivProgram, TestUtils.date(2018, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(5), hivProgram, TestUtils.date(2018, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2018, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2018, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(8), hivProgram, TestUtils.date(2018, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(9), hivProgram, TestUtils.date(2018, 1, 1));
        // Enroll patients #8 and  #9 in the MCH Program
        TestUtils.enrollInProgram(TestUtils.getPatient(8), mchProgram, TestUtils.date(2022, 2, 1));  //newly on art
        TestUtils.enrollInProgram(TestUtils.getPatient(9), mchProgram, TestUtils.date(2022, 2, 1));  //Already on art

        //set the birthdate of #7 to be this year less than 24 years
        TestUtils.getPatient(7).setBirthdate(TestUtils.date(2013, 6, 1));
        //put patient #5, #6,#7,#8 and #9 on art
        TestUtils.saveDrugOrder(TestUtils.getPatient(6), stavudine, TestUtils.date(2019, 1, 1), null);
        TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2019, 1, 1), null);
        TestUtils.saveDrugOrder(TestUtils.getPatient(8), stavudine, TestUtils.date(2021, 6, 1), null);
        TestUtils.saveDrugOrder(TestUtils.getPatient(9), stavudine, TestUtils.date(2019, 1, 1), null);
        //give patient #6,#7 and #8  viral load
        TestUtils.saveObs(TestUtils.getPatient(7),Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), 850, TestUtils.date(2021, 5, 1)); //vl 9 months ago
        TestUtils.saveObs(TestUtils.getPatient(9),Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), 1050, TestUtils.date(2022, 1, 1));

        List<Integer> ptIds = Arrays.asList(4, 5, 6, 7, 8, 9);
        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);
        PatientCalculationContext context = patientCalculationService.createCalculationContext();
        context.setNow(TestUtils.date(2022, 2, 20));

        CalculationResultMap resultMap = new NeedsViralLoadTestCalculation().evaluate(ptIds, null, context);

        Assert.assertFalse((Boolean) resultMap.get(4).getValue()); // not in HIV Program
        Assert.assertFalse((Boolean) resultMap.get(5).getValue());   //Not on art
        Assert.assertThat((Boolean) resultMap.get(9).getValue(), is(true));   //Needs vl since pregnant, already on art and >1 months without vl
        Assert.assertThat((Boolean) resultMap.get(8).getValue(), is(true));   //Needs vl since pregnant, newly on art and >6 months without vl
        Assert.assertThat((Boolean) resultMap.get(7).getValue(), is(true));   //Needs vl since vl >6 months months ago and less than 24 yrs
        Assert.assertThat((Boolean) resultMap.get(6).getValue(), is(true));   //Needs vl no vl and has been on art >6 months
    }

}