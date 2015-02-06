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
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.QiEmtctIndicatorLibrary;
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
@Builds({"kenyaemr.hiv.report.qi.adult.emtct"})
public class QiEmtctReportBuilder extends AbstractReportBuilder {

	@Autowired
	private QiEmtctIndicatorLibrary qiEmtctIndicatorLibrary;

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
				ReportUtils.map(qiEmtctDataset(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/**
	 * Creates the dataset
	 * @return the dataset
	 */
	protected DataSetDefinition qiEmtctDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("3");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("3.1", "% of pregnant women attending at least four ANC visits", ReportUtils.map(qiEmtctIndicatorLibrary.patientsAttendingAtLeast4AncVisitsAndPregnant(), indParams), "");
		dsd.addColumn("3.2", "% of skilled deliveries within the facility catchment population", ReportUtils.map(qiEmtctIndicatorLibrary.skilledDeliveriesWithinFacility(), indParams), "");
		dsd.addColumn("3.3", "% of deliveries with accurately filled Partographs", ReportUtils.map(qiEmtctIndicatorLibrary.allDeliveriesShouldBeMonitoredUsingAnAccuratelyFilledPartograph(), indParams), "");
		dsd.addColumn("3.4", "% of Mother-newborn pairs reviewed  by health care provider 7-14 days of birth", ReportUtils.map(qiEmtctIndicatorLibrary.mothersNewBornPairReview(), indParams), "");
		dsd.addColumn("3.5", "% of pregnant women whose partners have been tested for HIV or who are known positive", ReportUtils.map(qiEmtctIndicatorLibrary.numberOfNewAnClients(), indParams), "");
		dsd.addColumn("3.6", "% of HIV-infected pregnant women receiving  HAART", ReportUtils.map(qiEmtctIndicatorLibrary.HIVInfectedPregnantWomenReceivingHAART(), indParams), "");
		dsd.addColumn("3.7", "% of HIV-infected pregnant or lactating women on ART for at least 6 months who had a VL assessment done", ReportUtils.map(qiEmtctIndicatorLibrary.hIVInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWhoHadAvlAssessmentDone(), indParams), "");
		dsd.addColumn("3.8", "% of HIV-infected pregnant or lactating women on ART for at least 6 months with VL suppression", ReportUtils.map(qiEmtctIndicatorLibrary.hivInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWithVlSuppression(), indParams), "");
		dsd.addColumn("3.9", "% HEI who received HIV DNA PCR testing by age 6 weeks and results are available", ReportUtils.map(qiEmtctIndicatorLibrary.heiWhoReceivedHivDnaPCRTestingByAge6WeeksAndResultsAreAvailable(), indParams), "");
		dsd.addColumn("3.10", "% HIV exposed infants on exclusive breast  feeding at age 6 months", ReportUtils.map(qiEmtctIndicatorLibrary.hivExposedInfantsOnExclusiveBreastFeedingAtAge6Months(), indParams), "");
		dsd.addColumn("3.11", "% HIV exposed mother baby pair (0-18 months) in active care among facility registered", ReportUtils.map(qiEmtctIndicatorLibrary.hivExposedMotherBabyPair0to18MonthsInActiveCareAmongFacilityRegistered(), indParams), "");
		dsd.addColumn("3.12", "% HIV exposed mother baby pair (0-18 months) in active care among population estimate", ReportUtils.map(qiEmtctIndicatorLibrary.hivExposedMotherBabyPair0To18MonthsInActiveCareAmongPopulationEstimate(), indParams), "");
		dsd.addColumn("3.13", "% HIV exposed infants diagnosed with HIV between 0 and 18 months", ReportUtils.map(qiEmtctIndicatorLibrary.hivExposedInfantsDiagnosedWithHivBetween0And18Months(), indParams), "");
		return dsd;
	}
}
