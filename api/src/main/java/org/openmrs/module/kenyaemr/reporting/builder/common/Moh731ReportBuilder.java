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
import org.openmrs.module.kenyaemr.ArtAssessmentMethod;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.dataset.definition.MergingDataSetDefinition;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.PwpIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.mchcs.MchcsIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.mchms.MchmsIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * MOH 731 report
 */
@Component
@Builds({"kenyaemr.common.report.moh731"})
public class Moh731ReportBuilder extends AbstractReportBuilder {

	protected static final Log log = LogFactory.getLog(Moh731ReportBuilder.class);

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	private HivIndicatorLibrary hivIndicators;

	@Autowired
	private ArtIndicatorLibrary artIndicators;

	@Autowired
	private TbIndicatorLibrary tbIndicators;

	@Autowired
	private PwpIndicatorLibrary pwpIndicators;

	@Autowired
	private Moh731IndicatorLibrary moh731Indicators;

	@Autowired
	private MchmsIndicatorLibrary mchmsIndicators;

	@Autowired
	private MchcsIndicatorLibrary mchcsIndicatorLibrary;

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
				ReportUtils.map(pmtctDataSet(), "startDate=${startDate},endDate=${endDate}"),
				ReportUtils.map(careAndTreatmentDataSet(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Creates the dataset for section #2: Prevention of Mother-to-Child Transmission
	 *
	 * @return the dataset
	 */
	protected DataSetDefinition pmtctDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("2");
		dsd.setDescription("Prevention of Mother-to-Child Transmission");
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

		dsd.addColumn("HV02-10", "Total with known status (Total (HV02-04 to HV02-05))", ReportUtils.map(mchmsIndicators.testedForHivBeforeOrDuringMchms(), indParams), "");

		dsd.addColumn("HV02-11", "Male partners tested - (ANC/L&D)", ReportUtils.map(mchmsIndicators.partnerTestedDuringAncOrDelivery(), indParams), "");

		dsd.addColumn("HV02-12", "Discordant Couples", ReportUtils.map(mchmsIndicators.discordantCouples(), indParams), "");

		dsd.addColumn("HV02-18", "Assessed for eligibility at 1st ANC - WHO Staging done", ReportUtils.map(mchmsIndicators.assessedForArtEligibility(ArtAssessmentMethod.WHO_STAGING), indParams), "");
		dsd.addColumn("HV02-19", "Assessed for eligibility at 1st ANC - CD4", ReportUtils.map(mchmsIndicators.assessedForArtEligibility(ArtAssessmentMethod.CD4_COUNT), indParams), "");
		dsd.addColumn("HV02-20", "Assesed for Eligibility in ANC (Sum HV02-18 to HV02-19)", ReportUtils.map(mchmsIndicators.assessedForArtEligibility(null), indParams), "");


		dsd.addColumn("HV02-24", "PCR within 2 months", ReportUtils.map(mchcsIndicatorLibrary.pcrWithInitialIn2Months(), indParams), "");
		dsd.addColumn("HV02-25", "PCR from 3 to 8 months", ReportUtils.map(mchcsIndicatorLibrary.pcrWithInitialBetween3And8MonthsOfAge(), indParams), "");
		dsd.addColumn("HV02-26", "Serology antibody test(from 9 to 12 months)", ReportUtils.map(mchcsIndicatorLibrary.serologyAntBodyTestBetween9And12Months(), indParams), "");
		dsd.addColumn("HV02-27", "PCR from 9 to 12 months", ReportUtils.map(mchcsIndicatorLibrary.pcrTestBetween9And12Months(), indParams), "");

		dsd.addColumn("HV02-28", "Total HEI Tested by 12 months (Total (Sum HV02-24 to HV02-26))", ReportUtils.map(mchcsIndicatorLibrary.totalHeiTestedBy12Months(), indParams), "");

		dsd.addColumn("HV02-29", "Confirmed PCR Positive(Within 2 months)", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("HV02-30", "Confirmed PCR Positive(3-8 months)", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositiveBetween3To8Months(), indParams), "");
		dsd.addColumn("HV02-31", "Confirmed PCR Positive(9-12 months)", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositiveBetween9To12Months(), indParams), "");

		dsd.addColumn("HV02-32", "Total Confirmed Positive(Total (Sum HV2-29 to HV02-31))", ReportUtils.map(mchcsIndicatorLibrary.pcrTotalConfirmedPositive(), indParams), "");

		dsd.addColumn("HV02-33", "Exclusive Breastfeeding(at 6 months)", ReportUtils.map(mchcsIndicatorLibrary.exclusiveBreastFeedingAtSixMonths(), indParams), "");
		dsd.addColumn("HV02-34", "Exclusive Replacement Feeding(at 6 months)", ReportUtils.map(mchcsIndicatorLibrary.exclusiveReplacementFeedingAtSixMonths(), indParams), "");
		dsd.addColumn("HV02-35", "Mixed Feeding(at 6 months)", ReportUtils.map(mchcsIndicatorLibrary.mixedFeedingAtSixMonths(), indParams), "");

		dsd.addColumn("HV02-36", "Total Exposed aged six Months( Total sum(HIV02-33 to HIV02-35))", ReportUtils.map(mchcsIndicatorLibrary.totalExposedAgedSixMoths(), indParams), "");

		dsd.addColumn("HV02-37", "Mother on ARV treatment and breastfeeding", ReportUtils.map(mchcsIndicatorLibrary.motherOnTreatmentAndBreastFeeding(), indParams), "");
		dsd.addColumn("HV02-38", "Mother on ARV treatment and Not breastfeeding", ReportUtils.map(mchcsIndicatorLibrary.motherOnTreatmentAndNotBreastFeeding(), indParams), "");
		dsd.addColumn("HV02-39", "Mother on ARV treatment if breastfeeding unknown", ReportUtils.map(mchcsIndicatorLibrary.motherOnTreatmentAndNotBreastFeedingUnknown(), indParams), "");

		dsd.addColumn("HV02-40", "Mother on ARV treatment (Total Sum(HIV02-37 to HIV02-39))", ReportUtils.map(mchcsIndicatorLibrary.totalBreastFeedingMotherOnTreatment(), indParams), "");

		// TO DO 2.9 Infant ARV Prophylaxis(at first contact only)

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
		cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

		SimpleIndicatorDataSetDefinition nonCohortDsd = new SimpleIndicatorDataSetDefinition();
		nonCohortDsd.setName("3 (Non-cohort DSD)");
		nonCohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		nonCohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		MergingDataSetDefinition mergedDsd = new MergingDataSetDefinition();
		mergedDsd.setName("3");
		mergedDsd.setDescription("Care and Treatment");
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

		cohortDsd.addColumn("HV03-01", "HIV Exposed Infants (within 2 months)", ReportUtils.map(mchcsIndicatorLibrary.hivExposedInfantsWithin2Months(), indParams), "");
		cohortDsd.addColumn("HV03-02", "HIV Exposed Infants (Eligible for CTX at 2 months)", ReportUtils.map(mchcsIndicatorLibrary.hivExposedInfantsWithin2MonthsAndEligibleForCTX(), indParams), "");

		// 3.1 (On CTX Prophylaxis)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "On CTX Prophylaxis", ReportUtils.map(hivIndicators.onCotrimoxazoleProphylaxis(), indParams), nonInfantColumns, Arrays.asList("03", "04", "05", "06", "07"));

		// 3.2 (Enrolled in Care)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Enrolled in care", ReportUtils.map(hivIndicators.enrolledExcludingTransfers(), indParams), allColumns, Arrays.asList("08", "09", "10", "11", "12", "13"));

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