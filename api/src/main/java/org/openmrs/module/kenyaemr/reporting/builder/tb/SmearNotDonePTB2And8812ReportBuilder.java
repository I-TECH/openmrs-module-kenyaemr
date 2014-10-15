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
 * New Smear-Negative PTB for 2 and 8  months registered 8-12 months earlier
 */
@Component
@Builds({"kenyaemr.tb.report.smear.not.done.ptb.2.8.8.12"})
public class SmearNotDonePTB2And8812ReportBuilder extends AbstractReportBuilder {

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
				ReportUtils.map(ptbSmearNotDoneResults2Months(), "startDate=${startDate},endDate=${endDate}"),
				ReportUtils.map(ptbSmearNotDoneResults8Months(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/**
	 * Create the data set
	 * @return data set
	 */
	protected DataSetDefinition ptbSmearNotDoneResults2Months() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("2");
		dsd.setDescription("PTB Smear Not Done TB Cases - Results at 2 Months");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("PTB2-01", "Total Enrolled", ReportUtils.map(tbIndicatorLibrary.totalEnrolled(), indParams), "");
		dsd.addColumn("PTB2-02", "Finalized Initial Treatment", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatment(), indParams), "");
		dsd.addColumn("PTB2-03", "Died", ReportUtils.map(tbIndicatorLibrary.died(), indParams), "");
		dsd.addColumn("PTB2-04", "Absconded", ReportUtils.map(tbIndicatorLibrary.absconded(), indParams), "");
		dsd.addColumn("PTB2-05", "Transferred Out", ReportUtils.map(tbIndicatorLibrary.transferredOut(), indParams), "");
		dsd.addColumn("PTB2-06", "Total Evaluated", ReportUtils.map(tbIndicatorLibrary.totalEvaluated(), indParams), "");
	   return  dsd;
	}

	/**
	 * Create the data set
	 * @return data set
	 */
	protected DataSetDefinition ptbSmearNotDoneResults8Months() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("8");
		dsd.setDescription("PTB Smear Not Done TB Cases - Results at 8 Months");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//HIV+
		dsd.addColumn("PTB8-01", "Total Enrolled - HIV+", ReportUtils.map(tbIndicatorLibrary.totalEnrolled8MonthsHivPositive(), indParams), "");
		dsd.addColumn("PTB8-02", "Finalized Initial Treatment - HIV+", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentResults8monthsHivPositive(), indParams), "");
		dsd.addColumn("PTB8-03", "Died - HIV+", ReportUtils.map(tbIndicatorLibrary.diedResults8monthsHivPositive(), indParams), "");
		dsd.addColumn("PTB8-04", "Absconded - HIV+", ReportUtils.map(tbIndicatorLibrary.abscondedResults8monthsHivPositive(), indParams), "");
		dsd.addColumn("PTB8-05", "Transferred Ou - HIV+", ReportUtils.map(tbIndicatorLibrary.transferredOutResults8monthsHivPositive(), indParams), "");
		dsd.addColumn("PTB8-06", "Total Evaluated - HIV+", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositive(), indParams), "");

		//HIV-
		dsd.addColumn("PTB8-07", "Total Enrolled - HIV-", ReportUtils.map(tbIndicatorLibrary.totalEnrolled8MonthsHivNegative(), indParams), "");
		dsd.addColumn("PTB8-08", "Finalized Initial Treatment - HIV-", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentTotalEnrolled8MonthsHivNegative(), indParams), "");
		dsd.addColumn("PTB8-09", "Died - HIV-", ReportUtils.map(tbIndicatorLibrary.diedTotalEnrolled8MonthsHivNegative(), indParams), "");
		dsd.addColumn("PTB8-10", "Absconded - HIV-", ReportUtils.map(tbIndicatorLibrary.abscondedTotalEnrolled8MonthsHivNegative(), indParams), "");
		dsd.addColumn("PTB8-11", "Transferred Ou - HIV-", ReportUtils.map(tbIndicatorLibrary.transferredOutTotalEnrolled8MonthsHivNegative(), indParams), "");
		dsd.addColumn("PTB8-12", "Total Evaluated - HIV-", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivNegative(), indParams), "");

		//HIV Test not done
		dsd.addColumn("PTB8-13", "Total Enrolled - HIV Test not done", ReportUtils.map(tbIndicatorLibrary.totalEnrolled8MonthsHivTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-14", "Finalized Initial Treatment - HIV Test not done", ReportUtils.map(tbIndicatorLibrary.finishedInitialTreatmentTotalEnrolled8MonthsHivTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-15", "Died - HIV Test not done", ReportUtils.map(tbIndicatorLibrary.diedTotalEnrolled8MonthsHivTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-16", "Absconded - HIV Test not done", ReportUtils.map(tbIndicatorLibrary.abscondedTotalEnrolled8MonthsHivTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-17", "Transferred Ou - HIV Test not done", ReportUtils.map(tbIndicatorLibrary.transferredOutTotalEnrolled8MonthsHivTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-18", "Total Evaluated - HIV Test not done", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivTestNotDone(), indParams), "");

		//Totals
		dsd.addColumn("PTB8-19", "Total Enrolled - Total", ReportUtils.map(tbIndicatorLibrary.totalEnrolled8MonthsHivPositiveNegativeTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-20", "Finalized Initial Treatment - Total", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-21", "Died - Total", ReportUtils.map(tbIndicatorLibrary.diedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-22", "Absconded - Total", ReportUtils.map(tbIndicatorLibrary.abscondedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-23", "Transferred Ou - Total", ReportUtils.map(tbIndicatorLibrary.transferredOutTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), indParams), "");
		dsd.addColumn("PTB8-24", "Total Evaluated - Total", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveNegativeTestNotDone(), indParams), "");

		//HIV+ on CPT
		dsd.addColumn("PTB8-25", "Total Enrolled - HIV+ on CPT", ReportUtils.map(tbIndicatorLibrary.totalEnrolled8MonthsHivPositiveOnCpt(), indParams), "");
		dsd.addColumn("PTB8-26", "Finalized Initial Treatment - HIV+ on CPT", ReportUtils.map(tbIndicatorLibrary.finalizeInitialTreatmentTotalEnrolled8MonthsHivPositiveOnCpt(), indParams), "");
		dsd.addColumn("PTB8-27", "Died - HIV+ on CPT", ReportUtils.map(tbIndicatorLibrary.diedTotalEnrolled8MonthsHivPositiveOnCpt(), indParams), "");
		dsd.addColumn("PTB8-28", "Absconded - HIV+ on CPT", ReportUtils.map(tbIndicatorLibrary.abscondedTotalEnrolled8MonthsHivPositiveOnCpt(), indParams), "");
		dsd.addColumn("PTB8-29", "Transferred Ou - HIV+ on CPT", ReportUtils.map(tbIndicatorLibrary.transferredOutTotalEnrolled8MonthsHivPositiveOnCpt(), indParams), "");
		dsd.addColumn("PTB8-30", "Total Evaluated - HIV+ on CPT", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnCpt(), indParams), "");

		//HIV+ on ART
		dsd.addColumn("PTB8-31", "Total Enrolled - HIV+ on ART", ReportUtils.map(tbIndicatorLibrary.totalEnrolled8MonthsHivPositiveOnArt(), indParams), "");
		dsd.addColumn("PTB8-32", "Finalized Initial Treatment - HIV+ on ART", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnArt(), indParams), "");
		dsd.addColumn("PTB8-33", "Died - HIV+ on ART", ReportUtils.map(tbIndicatorLibrary.diedTotalEnrolled8MonthsHivPositiveOnArt(), indParams), "");
		dsd.addColumn("PTB8-34", "Absconded - HIV+ on ART", ReportUtils.map(tbIndicatorLibrary.abscondedTotalEnrolled8MonthsHivPositiveOnArt(), indParams), "");
		dsd.addColumn("PTB8-35", "Transferred Ou - HIV+ on ART", ReportUtils.map(tbIndicatorLibrary.transferredOutTotalEnrolled8MonthsHivPositiveOnArt(), indParams), "");
		dsd.addColumn("PTB8-36", "Total Evaluated - HIV+ on ART", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnArt(), indParams), "");

		return  dsd;
	}
}
