/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.report.indicator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class Moh731ReportTest extends BaseModuleContextSensitiveTest {

	@Autowired
	KenyaEmr emr;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		emr.contextRefreshed();
	}

	@Test
	public void test() throws Exception {

		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
		Form hivAddendum = Context.getFormService().getFormByUuid(MetadataConstants.CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM_UUID);

		// Enroll patient #6 in the HIV program
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(6), hivProgram, TestUtils.date(2012, 1, 15), null);

		// Submit an HIV addendum form for patient #6
		TestUtils.saveEncounter(Context.getPatientService().getPatient(6), hivAddendum, TestUtils.date(2012, 1, 15));

		Moh731Report report = new Moh731Report();
		ReportDefinition rd = report.getReportDefinition();
		EvaluationContext ec = new EvaluationContext();
		ec.addParameterValue("startDate", TestUtils.date(2012, 1, 1)); // Run report for Jan 2012
		ec.addParameterValue("endDate", TestUtils.date(2012, 1, 31));

		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);

		Assert.assertEquals(1, data.getDataSets().size());
		MapDataSet dataSet = (MapDataSet) data.getDataSets().get("MOH 731 DSD");
		Assert.assertNotNull(dataSet);

		Assert.assertEquals(1, ((IndicatorResult) dataSet.getColumnValue(1, "HV03-09")).getValue().intValue());
		Assert.assertEquals(1, ((IndicatorResult) dataSet.getColumnValue(1, "HV03-13")).getValue().intValue());

		//TestUtils.printReport(data);
	}
}