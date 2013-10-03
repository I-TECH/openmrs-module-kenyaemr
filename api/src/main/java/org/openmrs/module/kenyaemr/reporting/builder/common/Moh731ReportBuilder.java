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
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.BaseIndicatorReportBuilder;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.dataset.definition.MergingDataSetDefinition;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.PwpIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.mchms.MchmsIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * MOH 731 report
 */
@Component
@Builds("kenyaemr.common.report.moh731")
public class Moh731ReportBuilder extends BaseIndicatorReportBuilder {

	protected static final Log log = LogFactory.getLog(Moh731ReportBuilder.class);

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	private HivIndicatorLibrary artIndicators;

	@Autowired
	private TbIndicatorLibrary tbIndicators;

	@Autowired
	private PwpIndicatorLibrary pwpIndicators;

	@Autowired
	private Moh731IndicatorLibrary moh731Indicators;

	@Autowired
	private MchmsIndicatorLibrary mchmsIndicators;

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.BaseIndicatorReportBuilder#buildDataSets()
	 */
	@Override
	public List<DataSetDefinition> buildDataSets() {
		log.debug("Setting up report definition");

		return Arrays.asList(pmtctDataSet(), careAndTreatmentDataSet());
	}

	/**
	 * Creates the dataset for section #2: Prevention of Mother-to-Child Transmission
	 *
	 * @return the dataset
	 */
	protected DataSetDefinition pmtctDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("2: Prevention of Mother-to-Child Transmission");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("HV02-01", "Testing for HIV (Antenatal)", ReportUtils.map(mchmsIndicators.testedForHivInMchmsAntenatal(), indParams), "");
		dsd.addColumn("HV02-02", "Testing for HIV (Labor and Delivery)", ReportUtils.map(mchmsIndicators.testedForHivInMchmsDelivery(), indParams), "");
		dsd.addColumn("HV02-03", "Testing for HIV (Postnatal (within 72hrs))", ReportUtils.map(mchmsIndicators.testedForHivInMchmsPostnatal(), indParams), "");
		dsd.addColumn("HV02-04", "Testing for HIV (Total (Sum HV02-01 to HV02-03))", ReportUtils.map(mchmsIndicators.testedForHivInMchms(), indParams), "");

		dsd.addColumn("HV02-05", "HIV positive results (At entry into ANC)", ReportUtils.map(mchmsIndicators.testedHivPositiveBeforeMchms(), indParams), "");
		dsd.addColumn("HV02-06", "HIV positive results (Antenatal)", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchmsAntenatal(), indParams), "");
		dsd.addColumn("HV02-07", "HIV positive results (Labor and Delivery)", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchmsDelivery(), indParams), "");
		dsd.addColumn("HV02-08", "HIV positive results (Postnatal (within 72hrs))", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchmsPostnatal(), indParams), "");
		dsd.addColumn("HV02-09", "HIV positive results (Total (Sum HV02-05 to HV02-08))", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchms(), indParams), "");

		dsd.addColumn("HV02-10", "Total with known status (HV02-04 plus HV02-05)", ReportUtils.map(mchmsIndicators.testedForHivBeforeOrDuringMchms(), indParams), "");

		return dsd;
	}

	/**
	 * Creates the dataset for section #3: Care and Treatment
	 *
	 * @return the dataset
	 */
	protected DataSetDefinition careAndTreatmentDataSet() {
		CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
		cohortDsd.setName("3 (Cohort DSD)");
		cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.age(), "onDate=${endDate}"));
		cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

		SimpleIndicatorDataSetDefinition nonCohortDsd = new SimpleIndicatorDataSetDefinition();
		nonCohortDsd.setName("3 (Non-cohort DSD)");
		nonCohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		nonCohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		MergingDataSetDefinition mergedDsd = new MergingDataSetDefinition();
		mergedDsd.setName("3: Care and Treatment");
		mergedDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		mergedDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		mergedDsd.addDataSetDefinition(cohortDsd);
		mergedDsd.addDataSetDefinition(nonCohortDsd);
		mergedDsd.setMergeOrder(MergingDataSetDefinition.MergeOrder.NAME);

		ColumnParameters colInfants = new ColumnParameters(null, "<1", "age=<1");
		ColumnParameters colMPeds = new ColumnParameters(null, "<15, Male", "gender=M|age=<15");
		ColumnParameters colFPeds = new ColumnParameters(null, "<15, Female", "gender=F|age=<15");
		ColumnParameters colMAdults = new ColumnParameters(null, "15+, Male", "gender=M|age=15+");
		ColumnParameters colFAdults = new ColumnParameters(null, "15+, Female", "gender=F|age=15+");
		ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

		List<ColumnParameters> allColumns = Arrays.asList(colInfants, colMPeds, colFPeds, colMAdults, colFAdults, colTotal);
		List<ColumnParameters> nonInfantColumns = Arrays.asList(colMPeds, colFPeds, colMAdults, colFAdults, colTotal);

		String indParams = "startDate=${startDate},endDate=${endDate}";

		// TODO HV03-01 and HV03-2 (HIV Exposed Infants)

		// 3.1 (On CTX Prophylaxis)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "On CTX Prophylaxis", ReportUtils.map(artIndicators.onCotrimoxazoleProphylaxis(), indParams), nonInfantColumns, Arrays.asList("03", "04", "05", "06", "07"));

		// 3.2 (Enrolled in Care)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Enrolled in care", ReportUtils.map(artIndicators.enrolledExcludingTransfers(), indParams), allColumns, Arrays.asList("08", "09", "10", "11", "12", "13"));

		// 3.3 (Currently in Care)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Currently in care", ReportUtils.map(moh731Indicators.currentlyInCare(), indParams), allColumns, Arrays.asList("14", "15", "16", "17", "18", "19"));

		// 3.4 (Starting ART)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Starting ART", ReportUtils.map(artIndicators.startedArt(), indParams), allColumns, Arrays.asList("20", "21", "22", "23", "24", "25"));

		cohortDsd.addColumn("HV03-26", "Starting ART (Pregnant)", ReportUtils.map(artIndicators.startedArtWhilePregnant(), indParams), "");
		cohortDsd.addColumn("HV03-27", "Starting ART (TB Patient)", ReportUtils.map(artIndicators.startedArtWhileTbPatient(), indParams), "");

		// 3.5 (Revisits ART)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Revisits ART", ReportUtils.map(moh731Indicators.revisitsArt(), indParams), allColumns, Arrays.asList("28", "29", "30", "31", "32", "33"));

		// 3.6 (Currently on ART [All])
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Currently on ART [All]", ReportUtils.map(moh731Indicators.currentlyOnArt(), indParams), allColumns, Arrays.asList("34", "35", "36", "37", "38", "39"));

		// 3.7 (Cumulative Ever on ART)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Cumulative ever on ART", ReportUtils.map(moh731Indicators.cumulativeOnArt(), indParams), nonInfantColumns, Arrays.asList("40", "41", "42", "43", "44"));

		// 3.8 (Survival and Retention on ART at 12 months)
		cohortDsd.addColumn("HV03-45", "ART Net Cohort at 12 months", ReportUtils.map(moh731Indicators.art12MonthNetCohort(), indParams), "");
		cohortDsd.addColumn("HV03-46", "On original 1st Line at 12 months", ReportUtils.map(moh731Indicators.onOriginalFirstLineAt12Months(), indParams), "");
		cohortDsd.addColumn("HV03-47", "On alternative 1st Line at 12 months", ReportUtils.map(moh731Indicators.onAlternateFirstLineAt12Months(), indParams), "");
		cohortDsd.addColumn("HV03-48", "On 2nd Line (or higher) at 12 months ", ReportUtils.map(moh731Indicators.onSecondLineAt12Months(), indParams), "");
		cohortDsd.addColumn("HV03-49", "On therapy at 12 months (Total) ", ReportUtils.map(moh731Indicators.onTherapyAt12Months(), indParams), "");

		// 3.9 (Screening)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Screened for TB", ReportUtils.map(tbIndicators.screenedForTb(), indParams), nonInfantColumns, Arrays.asList("50", "51", "52", "53", "54"));

		// TODO HV03-55 (Screened for cervical cancer (F 18+))

		// 3.10 (Prevention with Positives)
		cohortDsd.addColumn("HV09-04", "Modern contraceptive methods", ReportUtils.map(pwpIndicators.modernContraceptivesProvided(), indParams), "");
		cohortDsd.addColumn("HV09-05", "Provided with condoms", ReportUtils.map(pwpIndicators.condomsProvided(), indParams), "");

		// 3.11 (HIV Care Visits)
		nonCohortDsd.addColumn("HV03-70", "HIV care visits (Females 18+)", ReportUtils.map(moh731Indicators.hivCareVisitsFemale18(), indParams));
		nonCohortDsd.addColumn("HV03-71", "HIV care visits (Scheduled)", ReportUtils.map(moh731Indicators.hivCareVisitsScheduled(), indParams));
		nonCohortDsd.addColumn("HV03-72", "HIV care visits (Unscheduled)", ReportUtils.map(moh731Indicators.hivCareVisitsUnscheduled(), indParams));
		nonCohortDsd.addColumn("HV03-73", "HIV care visits (Total)", ReportUtils.map(moh731Indicators.hivCareVisitsTotal(), indParams));

		return mergedDsd;
	}
}