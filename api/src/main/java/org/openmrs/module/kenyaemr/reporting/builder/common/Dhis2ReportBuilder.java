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
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.Dhis2IndicatorLibrary;
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
	private Dhis2IndicatorLibrary dhis2Indicators;
	@Autowired
	private MchcsIndicatorLibrary mchcsIndicatorLibrary;
	@Autowired
	private HivIndicatorLibrary hivIndicators;
	@Autowired
	private Moh731IndicatorLibrary moh731Indicators;
	@Autowired
	private TbIndicatorLibrary tbIndicators;
	@Autowired
	private PwpIndicatorLibrary pwpIndicators;
	@Autowired
	private MchmsIndicatorLibrary mchmsIndicators;


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
				//ReportUtils.map(counsellingAndTesting(), "startDate=${startDate},endDate=${endDate}"),
				ReportUtils.map(pmtctDataSet(), "startDate=${startDate},endDate=${endDate}"),
				ReportUtils.map(careAndTreatment(), "startDate=${startDate},endDate=${endDate}")
				//ReportUtils.map(vmmc(), "startDate=${startDate},endDate=${endDate}"),
				//ReportUtils.map(pep(), "startDate=${startDate},endDate=${endDate}"),
				//ReportUtils.map(bloodSafety(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Create a data set for hiv counselling and Testing
	 * @return dataset
	 */
	protected DataSetDefinition counsellingAndTesting() {
		CohortIndicatorDataSetDefinition counsellingAndTestingDsd = new CohortIndicatorDataSetDefinition();
		counsellingAndTestingDsd.setName("1");
		counsellingAndTestingDsd.setDescription("HIV Counselling And Testing");
		counsellingAndTestingDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		counsellingAndTestingDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		counsellingAndTestingDsd.addDimension("age", ReportUtils.map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		counsellingAndTestingDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

		//defining dimensions to be used in this dsd
		ColumnParameters colMaleUnder15 = new ColumnParameters(null, "<15, Male", "gender=M|age=<15");
		ColumnParameters colFemaleUnder = new ColumnParameters(null, "<15, Female", "gender=F|age=<15");
		ColumnParameters colMale15to24 = new ColumnParameters(null, "15 to 24, Male", "gender=M|age=<24");
		ColumnParameters colFemale15to24 = new ColumnParameters(null, "15 to 24, Female", "gender=F|age=<24");
		ColumnParameters colMaleAbove24 = new ColumnParameters(null, "25+, Male", "gender=M|age=25+");
		ColumnParameters colFemaleAbove24 = new ColumnParameters(null, "25+, Female", "gender=F|age=25+");
		ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

		//supply the columns as a list
		List<ColumnParameters> allColumns = Arrays.asList(colMaleUnder15, colFemaleUnder, colMale15to24, colFemale15to24, colMaleAbove24, colFemaleAbove24, colTotal);

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//1.1 testing for hiv
		counsellingAndTestingDsd.addColumn("HV01-01-01", "Testing for HIV - First Testing HIV", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		counsellingAndTestingDsd.addColumn("HV01-01-02", "Testing for HIV - Repeat Testing HIV", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		counsellingAndTestingDsd.addColumn("HV01-01-03", "Testing for HIV - Total Tested HIV", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		counsellingAndTestingDsd.addColumn("HV01-01-04", "Testing for HIV - Couple Testing", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		counsellingAndTestingDsd.addColumn("HV01-01-05", "Testing for HIV - Static Testing HIV (Health Facility)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		counsellingAndTestingDsd.addColumn("HV01-01-06", "Testing for HIV - Outreach Testing HIV", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");

		//1.2 Receiving Results (Couples only)
		counsellingAndTestingDsd.addColumn("HV01-02-01", "Concordant Couples Receiving Results (Couples Only)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		counsellingAndTestingDsd.addColumn("HV01-02-02", "Discordant Couples Receiving Results (Couples Only)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");

		//1.3 Receiving Positive results
		EmrReportingUtils.addRow(counsellingAndTestingDsd, "HV01-03", "Receiving HIV + Results", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), allColumns, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));

		return counsellingAndTestingDsd;
	}

	/**
	 * Create a data set for the care and treatment section
	 * @return dataset
	 */
	protected DataSetDefinition careAndTreatment() {

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
	/**
	 * Creates the dataset for section #2: Prevention of Mother-to-Child Transmission
	 *
	 * @return the dataset
	 */
	protected DataSetDefinition pmtctDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("2");
		dsd.setDescription("Prevention of Mother-to-Child Transmission - PMTCT");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//2.1 Testing for HIV
		dsd.addColumn("HV02-01-01", "Testing for HIV - Antenatal Testing for HIV", ReportUtils.map(mchmsIndicators.testedForHivInMchmsAntenatal(), indParams), "");
		dsd.addColumn("HV02-01-02", "Testing for HIV - Labor and Delivery Testing for HIV", ReportUtils.map(mchmsIndicators.testedForHivInMchmsDelivery(), indParams), "");
		dsd.addColumn("HV02-01-03", "Testing for HIV - Postnatal (within 72hrs) Testing for HIV", ReportUtils.map(mchmsIndicators.testedForHivInMchmsPostnatal(), indParams), "");
		dsd.addColumn("HV02-01-04", "Testing for HIV - Total Women Tested(PMTCT)", ReportUtils.map(mchmsIndicators.testedForHivInMchms(), indParams), "");

		//2.2 HIV Positive Results
		dsd.addColumn("HV02-02-01", "HIV positive results - Known positive status (At entry into ANC)", ReportUtils.map(mchmsIndicators.testedHivPositiveBeforeMchms(), indParams), "");
		dsd.addColumn("HV02-02-02", "HIV positive results - Antenatal Positive to HIV Test", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchmsAntenatal(), indParams), "");
		dsd.addColumn("HV02-02-03", "HIV positive results - Labor and Delivery Positive to HIV Test", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchmsDelivery(), indParams), "");
		dsd.addColumn("HV02-02-04", "HIV positive results - Postnatal (within 72hrs) Positive to HIV Test", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchmsPostnatal(), indParams), "");
		dsd.addColumn("HV02-02-05", "HIV Positive results - Total Positive (PMTCT) ", ReportUtils.map(mchmsIndicators.testedHivPositiveInMchms(), indParams), "");

		//2.3partners Involvement
		dsd.addColumn("HV02-03-01", "Partners Involvement - Male partners tested - (ANC/L&D)", ReportUtils.map(mchmsIndicators.partnerTestedDuringAncOrDelivery(), indParams), "");
		dsd.addColumn("HV02-03-02", "Partners Involvement - Discordant Couples", ReportUtils.map(mchmsIndicators.discordantCouples(), indParams), "");

		//2.4 Maternal Prophylaxis( at first contact only)
		/*dsd.addColumn("HV02-04-01", "Maternal Prophylaxis( at first contact only) - Prophylaxis-NVP Only", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV02-04-02", "Maternal Prophylaxis( at first contact only) - Prophylaxis-(AZT+SdNVP)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV02-04-03", "Maternal Prophylaxis( at first contact only) - Prophylaxis-interrupted HAART", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV02-04-04", "Maternal Prophylaxis( at first contact only) - Prophylaxis-HAART", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV02-04-05", "Maternal Prophylaxis( at first contact only) - Total PMTCT Prophylaxis", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
*/
		//2.5 Assesment for ART Eligibility in MCH (at Diagnosis)
		dsd.addColumn("HV02-05-01", "Assessment for ART in MCH (at Diagnosis) - Assessed for eligibility in 1st ANC - WHO Staging done", ReportUtils.map(mchmsIndicators.assessedForArtEligibility(ArtAssessmentMethod.WHO_STAGING), indParams), "");
		dsd.addColumn("HV02-05-02", "Assessment for ART in MCH (at Diagnosis) - Assessed for eligibility in 1st ANC - CD4", ReportUtils.map(mchmsIndicators.assessedForArtEligibility(ArtAssessmentMethod.CD4_COUNT), indParams), "");
		dsd.addColumn("HV02-05-03", "Assessment for ART in MCH (at Diagnosis) - Assessed for Eligibility in ANC", ReportUtils.map(mchmsIndicators.assessedForArtEligibility(null), indParams), "");
		//dsd.addColumn("HV02-05-04", "Assessment for ART in MCH (at Diagnosis) - Started on ART during ANC", ReportUtils.map(mchmsIndicators.assessedForArtEligibility(null), indParams), "");


		//2.6 Infant Testing (initial tests only)
		dsd.addColumn("HV02-06-01", "PCR (within 2 months) Infant Testing (Initial test only)", ReportUtils.map(mchcsIndicatorLibrary.pcrWithInitialIn2Months(), indParams), "");
		dsd.addColumn("HV02-06-02", "PCR (from 3 to 8 months) Infant Testing (Initial test only)", ReportUtils.map(mchcsIndicatorLibrary.pcrWithInitialBetween3And8MonthsOfAge(), indParams), "");
		dsd.addColumn("HV02-06-03", "Serology (from 9 to 12 months) Infant Testing (Initial test only)", ReportUtils.map(mchcsIndicatorLibrary.serologyAntBodyTestBetween9And12Months(), indParams), "");
		dsd.addColumn("HV02-06-04", "PCR (from 9 to 12 months) Infant Testing (Initial test only)", ReportUtils.map(mchcsIndicatorLibrary.pcrTestBetween9And12Months(), indParams), "");
		dsd.addColumn("HV02-06-05", "Total HEI Tested by 12 months", ReportUtils.map(mchcsIndicatorLibrary.totalHeiTestedBy12Months(), indParams), "");


		//2.7 Confirmed Infant Test Results
		dsd.addColumn("HV02-07-01", "PCR (by 2 months) Confirmed Infant Test Results Positive", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("HV02-07-02", "PCR (3 to 8 months) Confirmed Infant Test Results Positive", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositiveBetween3To8Months(), indParams), "");
		dsd.addColumn("HV02-07-03", "PCR (9 to 12 months) Confirmed Infant Test Results Positive", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositiveBetween9To12Months(), indParams), "");
		dsd.addColumn("HV02-07-04", "Total Confirmed Positive Infant test result by PCR", ReportUtils.map(mchcsIndicatorLibrary.pcrTotalConfirmedPositive(), indParams), "");

		//2.8 Infant Feeding
		dsd.addColumn("HV02-08-01", "EBF(6 months) Infant Feeding", ReportUtils.map(mchcsIndicatorLibrary.exclusiveBreastFeedingAtSixMonths(), indParams), "");
		dsd.addColumn("HV02-08-02", "ERF(6 months) Infant Feeding", ReportUtils.map(mchcsIndicatorLibrary.exclusiveReplacementFeedingAtSixMonths(), indParams), "");
		dsd.addColumn("HV02-08-03", "MF(6 months) Infant Feeding", ReportUtils.map(mchcsIndicatorLibrary.mixedFeedingAtSixMonths(), indParams), "");
		dsd.addColumn("HV02-08-04", "Total Exposed aged six Months", ReportUtils.map(mchcsIndicatorLibrary.totalExposedAgedSixMoths(), indParams), "");
		dsd.addColumn("HV02-08-05", "BF(at 12 months) Infant Feeding", ReportUtils.map(mchcsIndicatorLibrary.motherOnTreatmentAndBreastFeeding(), indParams), "");
		dsd.addColumn("HV02-08-06", "Not BF(at 12 months) Infant Feeding", ReportUtils.map(mchcsIndicatorLibrary.motherOnTreatmentAndNotBreastFeeding(), indParams), "");
		dsd.addColumn("HV02-08-07", "Not known Infant Feeding (12 months)", ReportUtils.map(mchcsIndicatorLibrary.motherOnTreatmentAndNotBreastFeedingUnknown(), indParams), "");
		dsd.addColumn("HV02-08-08", "Total Exposed 12", ReportUtils.map(mchcsIndicatorLibrary.totalBreastFeedingMotherOnTreatment(), indParams), "");

		//2.9 Infant ARV Prophylaxis(at first contact only) to do this later
		/*dsd.addColumn("HV02-09-01", "Issued in ANC (Infant ARV prophylaxis)", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("HV02-09-02", "Labour and Delivery (Infant ARV prophylaxis)", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("HV02-09-03", "PNC (<72hrs) (Infant ARV prophylaxis)", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
		dsd.addColumn("HV02-09-04", "Total Infants Issued Prophylaxis", ReportUtils.map(mchcsIndicatorLibrary.pcrConfirmedPositive2Months(), indParams), "");
*/
		return  dsd;
	}

	/**
	 * MOH 731-4 Voluntary Male Circumcision
	 *
	 */
	protected DataSetDefinition vmmc() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("4");
		dsd.setDescription("Voluntary Medical Male Circumcision");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", ReportUtils.map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

		//defining dimensions to be used in this dsd
		ColumnParameters col0To14 = new ColumnParameters("", "Circumcised 0-14 years", "gender=M|age=<15");
		ColumnParameters col15to24 = new ColumnParameters("", "Circumcised 15-24 years", "gender=M|age=<24");
		ColumnParameters colAbove25 = new ColumnParameters("", "Circumcised 25 years and Above", "gender=M|age=25+");
		ColumnParameters colTotal = new ColumnParameters("", "Total Circumcised", "");

		//supply the columns as a list
		List<ColumnParameters> allColumns = Arrays.asList(col0To14, col15to24, colAbove25, colTotal);

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//4.1 Voluntary Medical Male Circumcision
		EmrReportingUtils.addRow(dsd, "HV04-01", "Voluntary Medical Male Circumcision", ReportUtils.map(moh731Indicators.cumulativeOnArt(), indParams), allColumns, Arrays.asList("01", "02", "03", "04"));

		//4.2 HIV Status(at circumcision)
		dsd.addColumn("HV04-02-01", "Positive - HIV Status (at circumcision)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV04-02-02", "Negative - HIV Status (at circumcision)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV04-02-03", "Unknown - HIV Status (at circumcision)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");

		//4.3 Adverse Events (Circumcision)
		dsd.addColumn("HV04-03-01", "During - AE(s) Moderate Adverse Events (Circumcision)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV04-03-02", "During - AE(s) Severe Adverse Events (Circumcision)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV04-03-03", "Post - AE(s) Moderate Adverse Events (Circumcision)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV04-03-04", "Post - AE(s) Severe Adverse Events (Circumcision)", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV04-03-05", "Total AE During", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV04-03-06", "Total AE Post", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
	return dsd;
	}

	/**
	 * MOH 731-5 Post-Exposure Prophylaxis
	 */
	protected DataSetDefinition pep() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("5");
		dsd.setDescription("Post-Exposure Prophylaxis");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//adding dimensions to be used
		ColumnParameters occupationalMale = new ColumnParameters("", "Occupational Male", "gender=M");
		ColumnParameters occupationalFemale = new ColumnParameters("", "Occupational Female", "gender=F");
		ColumnParameters sexualAssaultMale = new ColumnParameters("", "Sexual Assault Male ", "gender=M");
		ColumnParameters sexualAssaultFemale = new ColumnParameters("", "Sexual Assault Female ", "gender=F");
		ColumnParameters otherReasonsMale = new ColumnParameters("", "Other reasons Male ", "gender=M");
		ColumnParameters otherReasonsFemale = new ColumnParameters("", "Other reasons Female ", "gender=F");
		ColumnParameters totalType = new ColumnParameters("", "Total Exposure ", "");

		//provide a list of columns that could be used
		List<ColumnParameters> occupational = Arrays.asList(occupationalMale,occupationalFemale);
		List<ColumnParameters> sexassault = Arrays.asList(sexualAssaultMale,sexualAssaultFemale );
		List<ColumnParameters> other = Arrays.asList(otherReasonsMale,otherReasonsFemale);
		List<ColumnParameters> totals = Arrays.asList(totalType);

		 //5.1 Type of exposure
		EmrReportingUtils.addRow(dsd, "HV05-01", "Type of Exposure", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), occupational, Arrays.asList("01", "02"));
		EmrReportingUtils.addRow(dsd, "HV05-01", "Type of Exposure", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), sexassault, Arrays.asList("03", "04"));
		EmrReportingUtils.addRow(dsd, "HV05-01", "Type of Exposure", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), other, Arrays.asList("05", "06"));
		EmrReportingUtils.addRow(dsd, "HV05-01", "Type of Exposure", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), totals, Arrays.asList("07"));

		//5.2 Provided with Prophylaxis
		EmrReportingUtils.addRow(dsd, "HV05-02", "Provided with Prophylaxis", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), occupational, Arrays.asList("01", "02"));
		EmrReportingUtils.addRow(dsd, "HV05-02", "TProvided with Prophylaxis", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), sexassault, Arrays.asList("03", "04"));
		EmrReportingUtils.addRow(dsd, "HV05-02", "Provided with Prophylaxis", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), other, Arrays.asList("05", "06"));
		EmrReportingUtils.addRow(dsd, "HV05-02", "Provided with Prophylaxis", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), totals, Arrays.asList("07"));

		return dsd;
	}

	/**
	 * MOH 731-6 Blood Safety
	 */
	protected DataSetDefinition bloodSafety() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("6");
		dsd.setDescription("Blood Safety");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("HV06-01-01", "Blood Safety - Donated blood units", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV06-01-02", "Blood Safety - Blood units screened for TTIs", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");
		dsd.addColumn("HV06-01-03", "Blood Safety - Blood units reactive to HIV", ReportUtils.map(dhis2Indicators.dummyCohortIndicatorMethod(), indParams), "");

		return  dsd;
	}
}
