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
		dsd.setName("Adults");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("1.1", "% of patient in care with 2 or more visits, 3 months a part during the 6 months Review period", ReportUtils.map(qiIndicators.clinicalVisit(), indParams), "");
		dsd.addColumn("1.1a", "Numerator", ReportUtils.map(qiIndicators.patientsInCareAndHasAtLeast2Visits(), indParams), "");
		dsd.addColumn("1.1b", "Denominator", ReportUtils.map(qiIndicators.patientsWithClinicalVisits(), indParams), "");
		dsd.addColumn("1.1c", "QI gap", ReportUtils.map(qiIndicators.complimentPatientsWithClinicalVisits(), indParams), "");

		dsd.addColumn("1.2", "% of HIV infected patients in care with at least one CD4 count during the 6 months Review period", ReportUtils.map(qiIndicators.hivMonitoringCd4(), indParams), "");
		dsd.addColumn("1.2a", "Numerator", ReportUtils.map(qiIndicators.hasCd4Results(), indParams), "");
		dsd.addColumn("1.2b", "Denominator", ReportUtils.map(qiIndicators.hasVisits(), indParams), "");
		dsd.addColumn("1.2c", "QI gap", ReportUtils.map(qiIndicators.complimentHasCd4Results(), indParams), "");

		dsd.addColumn("1.3", "% eligible patients initiated on ART", ReportUtils.map(qiIndicators.artInitiation(), indParams), "");
		dsd.addColumn("1.3a", "Numerator", ReportUtils.map(qiIndicators.patientsWhoAreEligibleAndStartedArt(), indParams), "");
		dsd.addColumn("1.3b", "Denominator", ReportUtils.map(qiIndicators.hivInfectedPatientsNotOnArtAndHasHivClinicalVisit(), indParams), "");
		dsd.addColumn("1.3c", "QI gap", ReportUtils.map(qiIndicators.complimentPatientsWhoAreEligibleAndStartedArt(), indParams), "");

		dsd.addColumn("1.4", "% of patients on ART with at least one VL results during the last 12 months", ReportUtils.map(qiIndicators.hivMonitoringViralLoad(), indParams), "");
		dsd.addColumn("1.4a", "Numerator", ReportUtils.map(qiIndicators.patientsOnArtHavingAtLeastOneViralLoad(), indParams), "");
		dsd.addColumn("1.4b", "Denominator", ReportUtils.map(qiIndicators.onArtWithAtLeastOneClinicalVisit(), indParams), "");
		dsd.addColumn("1.4c", "QI gap", ReportUtils.map(qiIndicators.complimentPatientsOnArtHavingAtLeastOneViralLoad(), indParams), "");

		dsd.addColumn("1.5", "% of patients on ART for at least 6 months with VL suppression", ReportUtils.map(qiIndicators.hivMonitoringViralLoadSuppression(), indParams), "");
		dsd.addColumn("1.5a", "Numerator", ReportUtils.map(qiIndicators.onARTatLeast12MonthsAndVlLess1000(), indParams), "");
		dsd.addColumn("1.5b", "Denominator", ReportUtils.map(qiIndicators.hivMonitoringViralLoadNumAndDen(), indParams), "");
		dsd.addColumn("1.5c", "QI gap", ReportUtils.map(qiIndicators.complimentOnARTatLeast12MonthsAndVlLess1000(), indParams), "");

		dsd.addColumn("1.6", "% of patients screened for TB using ICF card at last clinic visit", ReportUtils.map(qiIndicators.tbScreeningServiceCoverage(), indParams), "");
		dsd.addColumn("1.6a", "Numerator", ReportUtils.map(qiIndicators.tbScreeningUsingIcfAdult(), indParams), "");
		dsd.addColumn("1.6b", "Denominator", ReportUtils.map(qiIndicators.patientsCurrentlyNotOnTbTreatmentAndHaveClinicalVisit(), indParams), "");
		dsd.addColumn("1.6c", "QI gap", ReportUtils.map(qiIndicators.complimentTbScreeningUsingIcfAdult(), indParams), "");

		//dsd.addColumn("1.7", "% of patients eligible for IPT who were initiated on IPT", ReportUtils.map(qiIndicators.patientsEligibleForIPTWhoWereInitiatedOnIPT(), indParams), "");

		dsd.addColumn("1.8", "% of patients with Nutritional assessment at the last clinic visit", ReportUtils.map(qiIndicators.nutritionalAssessment(), indParams), "");
		dsd.addColumn("1.8a", "Numerator", ReportUtils.map(qiIndicators.nutritionalAssessmentNum(), indParams), "");
		dsd.addColumn("1.8b", "Denominator", ReportUtils.map(qiIndicators.nutritionalAssessmentDen(), indParams), "");
		dsd.addColumn("1.8c", "QI gap", ReportUtils.map(qiIndicators.complimentNutritionalAssessmentNum(), indParams), "");

		//dsd.addColumn("1.9", "% of patients eligible for nutritional support and who received nutritional support", ReportUtils.map(qiIndicators.patientsEligibleForNutritionalSupportAndWhoReceived(), indParams), "");
		//dsd.addColumn("1.10", "% of patients whose partner(s) have been tested for HIV or have known positive Status", ReportUtils.map(qiIndicators.partnerTesting(), indParams), "");
		//dsd.addColumn("1.11", "% of patients whose children have been tested for HIV or have known positive Status", ReportUtils.map(qiIndicators.childrenTesting(), indParams), "");

		dsd.addColumn("1.12", "% non-pregnant women patients who are on modern contraceptive methods During the review period", ReportUtils.map(qiIndicators.reproductiveHealthFamilyPlanning(), indParams), "");
		dsd.addColumn("1.12a", "Numerator", ReportUtils.map(qiIndicators.reproductiveHealthFamilyPlanningNum(), indParams), "");
		dsd.addColumn("1.12b", "Denominator", ReportUtils.map(qiIndicators.reproductiveHealthFamilyPlanningDen(), indParams), "");
		dsd.addColumn("1.12c", "QI gap", ReportUtils.map(qiIndicators.complimentReproductiveHealthFamilyPlanningNum(), indParams), "");
		//dsd.addColumn("13", "% HIV infected non-pregnant women 18 to 65 years who have been screened for Cervical Cancer in within the last 12 months", ReportUtils.map(qiIndicators.nutritionalAssessment(), indParams), "");
		return dsd;
	}
}