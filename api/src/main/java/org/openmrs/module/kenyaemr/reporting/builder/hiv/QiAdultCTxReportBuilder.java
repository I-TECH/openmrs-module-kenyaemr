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

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.QiIndicatorLibrary;
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
@Builds({"kenyaemr.hiv.report.qi.adult.c.tx"})
public class QiAdultCTxReportBuilder extends AbstractReportBuilder {

	@Autowired
	private QiIndicatorLibrary qiIndicators;

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
				ReportUtils.map(qiDataset(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Creates the dataset
	 * @return the dataset
	 */
	protected DataSetDefinition qiDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("1");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("1.1", "% of patient in care with 2 or more visits, 3 months a part during the 6 months Review period", ReportUtils.map(qiIndicators.clinicalVisit(), indParams), "");
		dsd.addColumn("1.2", "% of HIV infected patients in care with at least one CD4 count during the 6 months Review period", ReportUtils.map(qiIndicators.hivMonitoringCd4(), indParams), "");
		dsd.addColumn("1.3", "% eligible patients initiated on ART", ReportUtils.map(qiIndicators.artInitiation(), indParams), "");
		dsd.addColumn("1.4", "% of patients on ART with at least one VL results during the last 12 months", ReportUtils.map(qiIndicators.hivMonitoringViralLoad(), indParams), "");
		dsd.addColumn("1.5", "% of patients on ART for at least 6 months with VL suppression", ReportUtils.map(qiIndicators.hivMonitoringViralLoadSuppression(), indParams), "");
		dsd.addColumn("1.6", "% of patients screened for TB using ICF card at last clinic visit", ReportUtils.map(qiIndicators.tbScreeningServiceCoverage(), indParams), "");
		//dsd.addColumn("1.7", "% of patients eligible for IPT who were initiated on IPT", ReportUtils.map(qiIndicators.patientsEligibleForIPTWhoWereInitiatedOnIPT(), indParams), "");
		dsd.addColumn("1.8", "% of patients with Nutritional assessment at the last clinic visit", ReportUtils.map(qiIndicators.nutritionalAssessment(), indParams), "");
		//dsd.addColumn("1.9", "% of patients eligible for nutritional support and who received nutritional support", ReportUtils.map(qiIndicators.patientsEligibleForNutritionalSupportAndWhoReceived(), indParams), "");
		//dsd.addColumn("1.10", "% of patients whose partner(s) have been tested for HIV or have known positive Status", ReportUtils.map(qiIndicators.partnerTesting(), indParams), "");
		//dsd.addColumn("1.11", "% of patients whose children have been tested for HIV or have known positive Status", ReportUtils.map(qiIndicators.childrenTesting(), indParams), "");
		dsd.addColumn("1.12", "% non-pregnant women patients who are on modern contraceptive methods During the review period", ReportUtils.map(qiIndicators.reproductiveHealthFamilyPlanning(), indParams), "");
		//dsd.addColumn("13", "% HIV infected non-pregnant women 18 to 65 years who have been screened for Cervical Cancer in within the last 12 months", ReportUtils.map(qiIndicators.nutritionalAssessment(), indParams), "");
		return dsd;
	}
}