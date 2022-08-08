/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.QiPaedsIndicatorLibrary;
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
 * Quality Improvement report
 */
@Component
@Builds({"kenyaemr.hiv.report.qi.peds.c.tx"})
public class QiPaedCTxReportBuilder extends AbstractReportBuilder {

	@Autowired
	private QiPaedsIndicatorLibrary qiIndicators;

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
				ReportUtils.map(qiPaedsDataset(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Creates the data set
	 * @return the data set
	 */
	protected DataSetDefinition qiPaedsDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("2");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("2.1", "% of patients in care with 2 or more visits, 3 months apart", ReportUtils.map(qiIndicators.clinicalVisit(), indParams), "");
		dsd.addColumn("2.1a", "Numerator", ReportUtils.map(qiIndicators.clinicalVisitNum(), indParams), "");
		dsd.addColumn("2.1b", "Denominator", ReportUtils.map(qiIndicators.clinicalVisitDen(), indParams), "");
		dsd.addColumn("2.1c", "QI gap", ReportUtils.map(qiIndicators.complementClinicalVisitNum(), indParams), "");

		dsd.addColumn("2.2", "% of HIV infected patients in care with at least one cd4 count", ReportUtils.map(qiIndicators.hivMonitoringCd4(), indParams), "");
		dsd.addColumn("2.2a", "Numerator", ReportUtils.map(qiIndicators.hivMonitoringCd4Num(), indParams), "");
		dsd.addColumn("2.2b", "Denominator", ReportUtils.map(qiIndicators.hivMonitoringCd4Den(), indParams), "");
		dsd.addColumn("2.2c", "QI gap", ReportUtils.map(qiIndicators.complementHivMonitoringCd4Num(), indParams), "");

		dsd.addColumn("2.3", "% eligible patients initiated on ART", ReportUtils.map(qiIndicators.artInitiation(), indParams), "");
		dsd.addColumn("2.3a", "Numerator", ReportUtils.map(qiIndicators.artInitiationNum(), indParams), "");
		dsd.addColumn("2.3b", "Denominator", ReportUtils.map(qiIndicators.artInitiationDen(), indParams), "");
		dsd.addColumn("2.3c", "QI gap", ReportUtils.map(qiIndicators.complementArtInitiationNum(), indParams), "");

		dsd.addColumn("2.4", "% of patients on ART with at least one VL result during the last 12 months", ReportUtils.map(qiIndicators.hivMonitoringViralLoad(), indParams), "");
		dsd.addColumn("2.4a", "Numerator", ReportUtils.map(qiIndicators.hivMonitoringViralLoadNum(), indParams), "");
		dsd.addColumn("2.4b", "Denominator", ReportUtils.map(qiIndicators.hivMonitoringViralLoadDen(), indParams), "");
		dsd.addColumn("2.4c", "QI gap", ReportUtils.map(qiIndicators.complementHivMonitoringViralLoadNum(), indParams), "");

		dsd.addColumn("2.5", "% of patients on ART for at least 6 months with VL suppression", ReportUtils.map(qiIndicators.hivMonitoringViralLoadSupression(), indParams), "");
		dsd.addColumn("2.5a", "Numerator", ReportUtils.map(qiIndicators.hivMonitoringViralLoadSupressionNum(), indParams), "");
		dsd.addColumn("2.5b", "Denominator", ReportUtils.map(qiIndicators.hivMonitoringViralLoadSupressionDen(), indParams), "");
		dsd.addColumn("2.5c", "QI gap", ReportUtils.map(qiIndicators.complementHivMonitoringViralLoadSupressionNum(), indParams), "");

		dsd.addColumn("2.6", "% of patients screened for TB at last clinic visit", ReportUtils.map(qiIndicators.tbScreeningServiceCoverage(), indParams), "");
		dsd.addColumn("2.6a", "Numerator", ReportUtils.map(qiIndicators.tbScreeningServiceCoverageNum(), indParams), "");
		dsd.addColumn("2.6b", "Denominator", ReportUtils.map(qiIndicators.tbScreeningServiceCoverageDen(), indParams), "");
		dsd.addColumn("2.6c", "QI gap", ReportUtils.map(qiIndicators.complementTbScreeningServiceCoverageNum(), indParams), "");

		dsd.addColumn("2.8", "% of patients with Nutritional assessment", ReportUtils.map(qiIndicators.nutritionalAssessment(), indParams), "");
		dsd.addColumn("2.8a", "Numerator", ReportUtils.map(qiIndicators.nutritionalAssessmentNum(), indParams), "");
		dsd.addColumn("2.8b", "Denominator", ReportUtils.map(qiIndicators.nutritionalAssessmentDen(), indParams), "");
		dsd.addColumn("2.8c", "QI gap", ReportUtils.map(qiIndicators.complementNutritionalAssessmentNum(), indParams), "");

		return dsd;
	}
}
