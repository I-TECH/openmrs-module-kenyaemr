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
package org.openmrs.module.kenyaemr.reporting.builder.tb;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * New Smear-Negative PTB for 2 months
 */
@Component
@Builds({"kenyaemr.tb.report.new.smear.negative.ptb.2"})
public class NewSmearNegativePTB2ReportBuilder extends AbstractReportBuilder {

	@Autowired
	TbIndicatorLibrary tbIndicatorLibrary;

	/**
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
	 */
	@Override
	protected List<Parameter> getParameters(ReportDescriptor descriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class)
		);
	}

	/**
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		return Arrays.asList(
				ReportUtils.map(ptbSmearNotDoneResults2Months(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/**
	 * Create the data set
	 * @return data set
	 */
	protected DataSetDefinition ptbSmearNotDoneResults2Months() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Smear Not Done 2 months");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("1", "Total Enrolled", ReportUtils.map(tbIndicatorLibrary.totalEnrolled(), indParams), "");
		dsd.addColumn("2", "Finalized Initial Treatment", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatment(), indParams), "");
		dsd.addColumn("3", "Died", ReportUtils.map(tbIndicatorLibrary.died(), indParams), "");
		dsd.addColumn("4", "Absconded", ReportUtils.map(tbIndicatorLibrary.absconded(), indParams), "");
		dsd.addColumn("5", "Transferred Out", ReportUtils.map(tbIndicatorLibrary.transferredOut(), indParams), "");
		dsd.addColumn("6", "Total Evaluated", ReportUtils.map(tbIndicatorLibrary.totalEvaluated(), indParams), "");
	   return  dsd;
	}
}
