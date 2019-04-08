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
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test for {@link NeedsViralLoadTestCalculation}
 * Created by ningosi on 08/06/15.
 */
@Ignore
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
        Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);

        // Enroll patients #6, #7 and #8  in the HIV Program
        TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, TestUtils.date(2011, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2011, 1, 1));
        TestUtils.enrollInProgram(TestUtils.getPatient(8), hivProgram, TestUtils.date(2011, 1, 1));

        // Put patient #7 on Stavudine

        TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2015, 1, 1), null);

        //put patient #6 on art and give viral load that is one year ago
        TestUtils.saveDrugOrder(TestUtils.getPatient(6), stavudine, TestUtils.date(2014, 1, 1), null);
        //give them vl more than 1 year a go
        TestUtils.saveObs(TestUtils.getPatient(6),Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), 850, TestUtils.date(2014, 5, 1));



        List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);
        PatientCalculationContext context = patientCalculationService.createCalculationContext();
        context.setNow(TestUtils.date(2015, 7, 1));

        CalculationResultMap resultMap = new NeedsViralLoadTestCalculation().evaluate(ptIds, null, context);

        Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV Program
        Assert.assertFalse((Boolean) resultMap.get(7).getValue());   //Not on art
        Assert.assertThat((Boolean) resultMap.get(8).getValue(), is(true));   //No viral load recorded
        Assert.assertThat((Boolean) resultMap.get(6).getValue(), is(true));   //Needs vl every year

    }

}