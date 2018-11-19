/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link PatientDueForTbProgramEnrollmentCalculation}
 */
public class PatientDueForTbProgramEnrollmentCalculationTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private TbMetadata tbMetadata;

    /**
     * Setup each test
     */
    @Before
    public void setup() throws Exception {
        executeDataSet("dataset/test-concepts.xml");

        tbMetadata.install();
    }
    /**
     * @see PatientDueForTbProgramEnrollmentCalculation#getFlagMessage()
     */
    @Test
    public void getFlagMessage() {
        Assert.assertThat(new PatientDueForTbProgramEnrollmentCalculation().getFlagMessage(), notNullValue());
    }

    /**
     * @see PatientDueForTbProgramEnrollmentCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     */
    @Test
    public void evaluate_shouldReturnClientsStartedOnTbDrugsNotEnrolledinTbProgram() {

        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        Concept startAntiTbQuestion = Context.getConceptService().getConcept(162309);
        Concept OnAntiTbQuestion = Context.getConceptService().getConcept(164948);
        Concept yes = Dictionary.getConcept(Dictionary.YES);
        Concept no = Dictionary.getConcept(Dictionary.NO);

        // Enroll patient #6
        PatientService ps = Context.getPatientService();

        TestUtils.enrollInProgram(ps.getPatient(6), tbProgram, TestUtils.date(2011, 1, 1));

        TestUtils.saveObs(TestUtils.getPatient(7), startAntiTbQuestion, yes, TestUtils.date(2012, 12, 1));
        TestUtils.saveObs(TestUtils.getPatient(7), OnAntiTbQuestion, no, TestUtils.date(2012, 12, 1));

        List<Integer> cohort = Arrays.asList(6, 7);
        CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(cohort, new PatientDueForTbProgramEnrollmentCalculation());
        Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // is still in program
        Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // has started anti tb drugs

    }
}