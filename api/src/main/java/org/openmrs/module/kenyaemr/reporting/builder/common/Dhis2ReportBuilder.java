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
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.Dhis2IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtIndicatorLibrary;
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
 * Dhis2 Report
 */
@Component
@Builds({"kenyaemr.common.report.dhis2"})
public class Dhis2ReportBuilder extends AbstractReportBuilder {

	protected static final Log log = LogFactory.getLog(Dhis2ReportBuilder.class);

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	private ArtIndicatorLibrary artIndicators;


	@Autowired
	private Dhis2IndicatorLibrary dhis2IndicatorLibrary;


	@Override
	protected List<Parameter> getParameters(ReportDescriptor descriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class)
		);
	}

	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		return Arrays.asList(
				ReportUtils.map(dhis2Dataset(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Create a data set for the dhis2 report
	 * @return dataset
	 */
	protected DataSetDefinition dhis2Dataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Dhis2 Indicators");
		dsd.setDescription("Generates Indicators that would be uploaded to dhis2");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", ReportUtils.map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));

		ColumnParameters children =new ColumnParameters("CP", "Children", "age=<15");
		ColumnParameters adults =new ColumnParameters("AP", "Adults", "age=15+");
		ColumnParameters colTotal = new ColumnParameters("TP", "grand total", "");

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//2.6 Infant Testing (initial tests only)
		dsd.addColumn("PCR-2", "PCR (within 2 months) Infant Testing (initial tests only)", ReportUtils.map(dhis2IndicatorLibrary.pcrInitialWithin2Months(), indParams), "");
		dsd.addColumn("PCR-3-8", "PCR (from 3 to 8 months)Infant Testing (initial tests only)", ReportUtils.map(dhis2IndicatorLibrary.pcrInitial3To8Months(), indParams), "");
		dsd.addColumn("SER-9-12", "Serology(from 9 to 12 months) Infant Testing (initial tests only)", ReportUtils.map(dhis2IndicatorLibrary.serologyAntBodyTestBetween9And12Months(), indParams), "");
		dsd.addColumn("PCR-9-12", "PCR(from 9 to 12 months)Infant Testing (initial test only)", ReportUtils.map(dhis2IndicatorLibrary.pcrTestBetween9And12Months(), indParams), "");
		dsd.addColumn("HEI-12-T", "Total HEI tested by 12 months", ReportUtils.map(dhis2IndicatorLibrary.totalHeiTestedBy12Months(), indParams), "");

		//2.7 Confirmed Infant Test Results
		dsd.addColumn("PCR-2-C", "PCR (by 2 months) Confirmed Infant Test Results Positive", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("PCR-3-8-C", "PCR (3 to 8 months) Confirmed Infant Test Results Positive", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositiveBetween3To8Months(), indParams), "");
		dsd.addColumn("PCR-9-12-C", "PCR (9 to 12 months) Confirmed Infant Test Results Positive", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositiveBetween9To12Months(), indParams), "");
		dsd.addColumn("PCR-C-T", "Total Confirmed Positive Infant test result by PCR", ReportUtils.map(dhis2IndicatorLibrary.pcrTotalConfirmedPositive(), indParams), "");

		//2.8 Infant Feeding
		dsd.addColumn("EBF-6", "EBF (6 months) Infant Feeding", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("ERF-6", "ERF (6 months) Infant Feeding", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("MF-6", "MF (6 months) Infant Feeding", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("TE-6", "Total Exposed aged 6 months", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("BF-12", "BF (at 12 months) Infant Feeding", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("NBF-12", "Not BF (12 months) Infant Feeding", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("NKIF-12", "Not Known Infant Feeding (12 months)", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("TE-12", "Total Exposed 12 months", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");

		//2.9 Infant ARV Prophylaxis(at first contact only)
		dsd.addColumn("IANC", "Issued in ANC (Infant ARV prophylaxis)", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("LAD", "Labour and Delivery (Infant ARV prophylaxis)", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("PNC", "PNC (<72hrs) (Infant ARV prophylaxis)", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("TIIP", "Total Infants Issued Prophylaxis", ReportUtils.map(dhis2IndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");



		return dsd;

	}
}
