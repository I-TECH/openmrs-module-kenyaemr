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

package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.cohortAnalysis.SixMonthsAdherenceIndicatorLibrary;
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
 * Six months art adherence report builder
 */
@Component
@Builds("kenyaemr.common.report.artSixMonthsAnalysis")
public class SixMonthArtAdherenceReportBuilder extends AbstractReportBuilder {

	protected static final Log log = LogFactory.getLog(SixMonthArtAdherenceReportBuilder.class);

	@Autowired
	private SixMonthsAdherenceIndicatorLibrary indicatorLibrary;


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
				ReportUtils.map(cohortReportDataset(), "startDate=${startDate}")
		);
	}

	/**
	 *
	 * @return the dataset
	 */
	protected DataSetDefinition cohortReportDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("sixMonthArtAdherenceReport");
		dsd.setDescription("Six Months art adherence report");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		//dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		String indParams = "startDate=${startDate}";

		dsd.addColumn("allPatients", "All Patients Cohort", ReportUtils.map(indicatorLibrary.allPatients(), indParams), "");
		dsd.addColumn("2weeks", "Two Weeks Cohort", ReportUtils.map(indicatorLibrary.twoWeeksPatients(), indParams), "");
		dsd.addColumn("1Month", "One Month Cohort", ReportUtils.map(indicatorLibrary.oneMonthPatients(), indParams), "");
		dsd.addColumn("2Months", "Two Months Cohort", ReportUtils.map(indicatorLibrary.twoMonthPatients(), indParams), "");
		dsd.addColumn("3months", "Three Months Cohort", ReportUtils.map(indicatorLibrary.threeMonthPatients(), indParams), "");
		dsd.addColumn("6months", "Six Months Cohort", ReportUtils.map(indicatorLibrary.sixMonthPatients(), indParams), "");

		dsd.addColumn("allAlive", "alive patients", ReportUtils.map(indicatorLibrary.alivePatients("all"), indParams), "");
		dsd.addColumn("2wksAlive", "two-week alive patients", ReportUtils.map(indicatorLibrary.alivePatients("2weeks"), indParams), "");
		dsd.addColumn("1MonthAlive", "one-month alive patients", ReportUtils.map(indicatorLibrary.alivePatients("1month"), indParams), "");
		dsd.addColumn("2MonthsAlive", "two-month alive patients", ReportUtils.map(indicatorLibrary.alivePatients("2month"), indParams), "");
		dsd.addColumn("3MonthsAlive", "three-month alive patients", ReportUtils.map(indicatorLibrary.alivePatients("3month"), indParams), "");
		dsd.addColumn("6MonthsAlive", "six-month alive patients", ReportUtils.map(indicatorLibrary.alivePatients("6month"), indParams), "");

		dsd.addColumn("allDead", "dead patients", ReportUtils.map(indicatorLibrary.deadPatients("all"), indParams), "");
		dsd.addColumn("2wksDead", "two-week dead patients", ReportUtils.map(indicatorLibrary.deadPatients("2weeks"), indParams), "");
		dsd.addColumn("1MonthDead", "one-month dead patients", ReportUtils.map(indicatorLibrary.deadPatients("1month"), indParams), "");
		dsd.addColumn("2MonthsDead", "two-month dead patients", ReportUtils.map(indicatorLibrary.deadPatients("2month"), indParams), "");
		dsd.addColumn("3MonthsDead", "three-month dead patients", ReportUtils.map(indicatorLibrary.deadPatients("3month"), indParams), "");
		dsd.addColumn("6MonthsDead", "six-month dead patients", ReportUtils.map(indicatorLibrary.deadPatients("6month"), indParams), "");

		dsd.addColumn("alllftu", "LFTU patients", ReportUtils.map(indicatorLibrary.ltfuPatients("all"), indParams), "");
		dsd.addColumn("2wkslftu", "two-week LFTU patients", ReportUtils.map(indicatorLibrary.ltfuPatients("2weeks"), indParams), "");
		dsd.addColumn("1Monthlftu", "one-month LFTU patients", ReportUtils.map(indicatorLibrary.ltfuPatients("1month"), indParams), "");
		dsd.addColumn("2Monthslftu", "two-month LFTU patients", ReportUtils.map(indicatorLibrary.ltfuPatients("2month"), indParams), "");
		dsd.addColumn("3Monthslftu", "three-month LFTU patients", ReportUtils.map(indicatorLibrary.ltfuPatients("3month"), indParams), "");
		dsd.addColumn("6Monthslftu", "six-month LFTU patients", ReportUtils.map(indicatorLibrary.ltfuPatients("6month"), indParams), "");

		dsd.addColumn("allStopped", "Stopped patients", ReportUtils.map(indicatorLibrary.stoppedPatients("all"), indParams), "");
		dsd.addColumn("2wksStopped", "two-week Stopped patients", ReportUtils.map(indicatorLibrary.stoppedPatients("2weeks"), indParams), "");
		dsd.addColumn("1MonthStopped", "one-month Stopped patients", ReportUtils.map(indicatorLibrary.stoppedPatients("1month"), indParams), "");
		dsd.addColumn("2MonthsStopped", "two-month Stopped patients", ReportUtils.map(indicatorLibrary.stoppedPatients("2month"), indParams), "");
		dsd.addColumn("3MonthsStopped", "three-month Stopped patients", ReportUtils.map(indicatorLibrary.stoppedPatients("3month"), indParams), "");
		dsd.addColumn("6MonthsStopped", "six-month Stopped patients", ReportUtils.map(indicatorLibrary.stoppedPatients("6month"), indParams), "");

		dsd.addColumn("allTo", "TO patients", ReportUtils.map(indicatorLibrary.toPatients("all"), indParams), "");
		dsd.addColumn("2wksTo", "two-week TO patients", ReportUtils.map(indicatorLibrary.toPatients("2weeks"), indParams), "");
		dsd.addColumn("1MonthTo", "one-month TO patients", ReportUtils.map(indicatorLibrary.toPatients("1month"), indParams), "");
		dsd.addColumn("2MonthsTo", "two-month TO patients", ReportUtils.map(indicatorLibrary.toPatients("2month"), indParams), "");
		dsd.addColumn("3MonthsTo", "three-month TO patients", ReportUtils.map(indicatorLibrary.toPatients("3month"), indParams), "");
		dsd.addColumn("6MonthsTo", "six-month TO patients", ReportUtils.map(indicatorLibrary.toPatients("6month"), indParams), "");


		return dsd;
	}


}