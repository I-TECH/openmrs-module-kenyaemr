/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.library.moh711.Moh711IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;

/**
 * MOH 711 report
 */
@Component
@Builds({"kenyaemr.common.report.moh711"})
public class Moh711ReportBuilder extends AbstractReportBuilder {

	protected static final Log log = LogFactory.getLog(Moh711ReportBuilder.class);

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	private HivIndicatorLibrary hivIndicators;

	@Autowired
	private ArtIndicatorLibrary artIndicators;

	@Autowired
	private TbIndicatorLibrary tbIndicators;

	@Autowired
	private Moh711IndicatorLibrary moh711Indicators;

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
	String indParams = "startDate=${startDate},endDate=${endDate}";

	/**
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		return Arrays.asList(
				ReportUtils.map(createANCPMTCTDataSet(), "startDate=${startDate},endDate=${endDate}")/*,
				ReportUtils.map(createArtDataSet(), "startDate=${startDate},endDate=${endDate}")*/
		);
	}
/**
 * A. ANC / PMCT
 * Creates ANC/PMTCT dataset
 */
private DataSetDefinition createANCPMTCTDataSet() {
	CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
	dsd.setName("ANC_PMTCT");
	dsd.setDescription("ANC PMTCT");
	dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
	dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
	dsd.addDimension("age", map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
	dsd.addDimension("gender", map(commonDimensions.gender()));

	List<ColumnParameters> columns = new ArrayList<ColumnParameters>();
	dsd.addColumn("New ANC Clients", "", ReportUtils.map(moh711Indicators.noOfNewANCClients(), indParams), "");
	dsd.addColumn("Revisiting ANC Clients", "", ReportUtils.map(moh711Indicators.noOfANCClientsRevisits(), indParams), "");
	/*dsd.addColumn("Clients given IPT (1st dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT1stDose(), indParams), "");
	dsd.addColumn("Clients given IPT (2nd dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT2ndDose(), indParams), "");
	dsd.addColumn("Clients given IPT (3rd dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT3rdDose(), indParams), "");*/
	dsd.addColumn("Clients with Hb less than 11 g per dl", "", ReportUtils.map(moh711Indicators.noOfANCClientsLowHB(), indParams), "");
	dsd.addColumn("Clients completed 4 Antenatal Visits", "", ReportUtils.map(moh711Indicators.ancClientsCompleted4Visits(), indParams), "");
	/*dsd.addColumn("LLINs distributed to under 1 year", "", ReportUtils.map(moh711Indicators.distributedLLINsUnder1Year(), indParams), "");
	dsd.addColumn("LLINs distributed to ANC clients", "", ReportUtils.map(moh711Indicators.distributedLLINsToANCClients(), indParams), "");
	*/
	dsd.addColumn("clients tested for Syphilis", "", ReportUtils.map(moh711Indicators.ancClientsTestedForSyphillis(), indParams), "");
	dsd.addColumn("clients tested Positive for Syphilis", "", ReportUtils.map(moh711Indicators.ancClientsTestedSyphillisPositive(), indParams), "");
	dsd.addColumn("Total women done breast examination", "", ReportUtils.map(moh711Indicators.breastExaminationDone(), indParams), "");
	/*dsd.addColumn("Adolescents (10-14 years) presenting with pregnancy at 1st ANC Visit", "", ReportUtils.map(moh711Indicators.adolescents10To14FirstANC(), indParams), "");
	dsd.addColumn("Adolescents (15-19 years) presenting with pregnancy at 1st ANC Visit", "", ReportUtils.map(moh711Indicators.adolescents15To19FirstANC(), indParams), "");
	dsd.addColumn("Youth (20-24 years) presenting with pregnancy at 1st ANC Visit", "", ReportUtils.map(moh711Indicators.youth20To24FirstANC(), indParams), "");
	*/
	dsd.addColumn("Women presenting with pregnancy  at 1ST ANC in the First Trimeseter(<= 12 Weeks)", "", ReportUtils.map(moh711Indicators.presentingPregnancy1stANC1stTrimester(), indParams), "");
	/*dsd.addColumn("Clients issued with Iron", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithIron(), indParams), "");
	dsd.addColumn("Clients issued with Folic", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithFolic(), indParams), "");
	dsd.addColumn("Clients issued with Combined Ferrous Folate", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithFerrousFolic(), indParams), "");
	dsd.addColumn("Pregnant women presenting in ANC with complication associated with FGM", "", ReportUtils.map(moh711Indicators.ancClientsWithFGMRelatedComplications(), indParams), "");*/

	/*EmrReportingUtils.addRow(dsd, "G1", "No. of detected cases (who have new Tb detected cases)", ReportUtils.map(tbIndicators.tbNewDetectedCases(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G2", "No. of Pulmonary smear positive (who have pulmonary TB and smear positive)", ReportUtils.map(tbIndicators.pulmonaryTbSmearPositive(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G3", "No. of Pulmonary smear negative (who have pulmonary TB and smear negative)", ReportUtils.map(tbIndicators.pulmonaryTbSmearNegative(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G4", "No. of Extra pulmonary TB (who have extra pulmonary TB)", ReportUtils.map(tbIndicators.extraPulmonaryTbPatients(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G5", "No. of TB Re-treatments (who are in Tb re-treatments)", ReportUtils.map(tbIndicators.tbRetreatmentsPatients(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G6", "No. of TB and Tested for HIV (who are in Tb program and tested for HIV)", ReportUtils.map(tbIndicators.inTbAndTestedForHiv(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G7", "No. of TB and Tested for HIV (whose HIV result is positive)", ReportUtils.map(tbIndicators.inTbAndTestedForHivPositive(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G8", "No. of TB and HIV (who are both in TB and HIV and are on CPT)", ReportUtils.map(tbIndicators.inTbAndHivProgramsAndOnCtxProphylaxis(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G9", "No. of TB defaulters (who defaulted or missed appointments)", ReportUtils.map(tbIndicators.defaulted(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G10", "No. of TB completes (who Completed Tb Treatment)", ReportUtils.map(tbIndicators.completedTbTreatment(), indParams), columns);
	EmrReportingUtils.addRow(dsd, "G11", "No. of TB deaths (who started tx this month last year)", ReportUtils.map(tbIndicators.diedAndStarted12MonthsAgo(), indParams), columns);
*/
	return dsd;
}
	/**
	 * Creates the ART data set
	 * @return the data set
	 */
/*	private DataSetDefinition createTbDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("G");
		dsd.setDescription("TB");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		dsd.addDimension("gender", map(commonDimensions.gender()));

		List<ColumnParameters> columns = new ArrayList<ColumnParameters>();
		columns.add(new ColumnParameters("FP", "0-14 years, female", "gender=F|age=<15"));
		columns.add(new ColumnParameters("MP", "0-14 years, male", "gender=M|age=<15"));
		columns.add(new ColumnParameters("FA", ">14 years, female", "gender=F|age=15+"));
		columns.add(new ColumnParameters("MA", ">14 years, male", "gender=M|age=15+"));
		columns.add(new ColumnParameters("T", "total", ""));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		EmrReportingUtils.addRow(dsd, "G1", "No. of detected cases (who have new Tb detected cases)", ReportUtils.map(tbIndicators.tbNewDetectedCases(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G2", "No. of Pulmonary smear positive (who have pulmonary TB and smear positive)", ReportUtils.map(tbIndicators.pulmonaryTbSmearPositive(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G3", "No. of Pulmonary smear negative (who have pulmonary TB and smear negative)", ReportUtils.map(tbIndicators.pulmonaryTbSmearNegative(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G4", "No. of Extra pulmonary TB (who have extra pulmonary TB)", ReportUtils.map(tbIndicators.extraPulmonaryTbPatients(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G5", "No. of TB Re-treatments (who are in Tb re-treatments)", ReportUtils.map(tbIndicators.tbRetreatmentsPatients(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G6", "No. of TB and Tested for HIV (who are in Tb program and tested for HIV)", ReportUtils.map(tbIndicators.inTbAndTestedForHiv(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G7", "No. of TB and Tested for HIV (whose HIV result is positive)", ReportUtils.map(tbIndicators.inTbAndTestedForHivPositive(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G8", "No. of TB and HIV (who are both in TB and HIV and are on CPT)", ReportUtils.map(tbIndicators.inTbAndHivProgramsAndOnCtxProphylaxis(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G9", "No. of TB defaulters (who defaulted or missed appointments)", ReportUtils.map(tbIndicators.defaulted(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G10", "No. of TB completes (who Completed Tb Treatment)", ReportUtils.map(tbIndicators.completedTbTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G11", "No. of TB deaths (who started tx this month last year)", ReportUtils.map(tbIndicators.diedAndStarted12MonthsAgo(), indParams), columns);

		return dsd;
	}*/

	/**
	 * Creates the ART data set
	 * @return the data set
	 */
	private DataSetDefinition createArtDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("K");
		dsd.setDescription("ART");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", ReportUtils.map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
/*

		ColumnParameters colFPeds = new ColumnParameters("FP", "0-14 years, female", "gender=F|age=<15");
		ColumnParameters colMPeds = new ColumnParameters("MP", "0-14 years, male", "gender=M|age=<15");
		ColumnParameters colFAdults = new ColumnParameters("FA", ">14 years, female", "gender=F|age=15+");
		ColumnParameters colMAdults = new ColumnParameters("MA", ">14 years, male", "gender=M|age=15+");
		ColumnParameters colFTotal = new ColumnParameters("F", "totals, female", "gender=F");
		ColumnParameters colMTotal = new ColumnParameters("M", "totals, male", "gender=M");
		ColumnParameters colTotal = new ColumnParameters("T", "grand total", "");

		Concept pmtct = Dictionary.getConcept(Dictionary.PMTCT_PROGRAM);
		Concept vct = Dictionary.getConcept(Dictionary.VCT_PROGRAM);
		Concept tb = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM);
		Concept[] inpatient = { Dictionary.getConcept(Dictionary.PEDIATRIC_INPATIENT_SERVICE), Dictionary.getConcept(Dictionary.ADULT_INPATIENT_SERVICE) };
		Concept cwc = Dictionary.getConcept(Dictionary.UNDER_FIVE_CLINIC);
		Concept[] all = { pmtct, vct, tb, inpatient[0], inpatient[1], cwc };

		List<ColumnParameters> allColumns = Arrays.asList(colFPeds, colMPeds, colFAdults, colMAdults, colFTotal, colMTotal, colTotal);
		List<ColumnParameters> femaleColumns = Arrays.asList(colFPeds, colFAdults, colFTotal);
		List<ColumnParameters> pedsColumns = Arrays.asList(colFPeds, colMPeds, colFTotal, colMTotal, colTotal);

		String indParams = "startDate=${startDate},endDate=${endDate}";

		EmrReportingUtils.addRow(dsd, "K1-1", "New enrollments - PMTCT", ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(pmtct), indParams), femaleColumns);
		EmrReportingUtils.addRow(dsd, "K1-2", "New enrollments - VCT", ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(vct), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-3", "New enrollments - TB", ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(tb), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-4", "New enrollments - In Patient", ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(inpatient), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-5", "New enrollments - CWC", ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(cwc), indParams), pedsColumns);
		EmrReportingUtils.addRow(dsd, "K1-6", "New enrollments - All others", ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndNotReferredFrom(all), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-7", "New enrollments - Sub-total", ReportUtils.map(hivIndicators.enrolledExcludingTransfers(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K2", "Cumulative enrolled", ReportUtils.map(hivIndicators.enrolledCumulative(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-1", "Starting ARVs - WHO stage 1", ReportUtils.map(artIndicators.startedArtWithWhoStage(1), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-2", "Starting ARVs - WHO stage 2", ReportUtils.map(artIndicators.startedArtWithWhoStage(2), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-3", "Starting ARVs - WHO stage 3", ReportUtils.map(artIndicators.startedArtWithWhoStage(3), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-4", "Starting ARVs - WHO stage 4", ReportUtils.map(artIndicators.startedArtWithWhoStage(4), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-5", "Starting ARVs - Sub-total", ReportUtils.map(artIndicators.startedArt(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K4", "Cumulative started ARV", ReportUtils.map(artIndicators.startedArtCumulative(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K5-1", "Currently on ARVs - Pregnant women", ReportUtils.map(artIndicators.onArtAndPregnant(), indParams), femaleColumns);
		EmrReportingUtils.addRow(dsd, "K5-2", "Currently on ARVs - All others", ReportUtils.map(artIndicators.onArtAndNotPregnant(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K5-3", "Currently on ARVs - Sub-total", ReportUtils.map(moh731Indicators.currentlyOnArt(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K6", "Eligible for ART", ReportUtils.map(artIndicators.eligibleForArt(), indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-1", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-2", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-3", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-4", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-1", "On prophylaxis - Cotrimoxazole", ReportUtils.map(hivIndicators.onCotrimoxazoleProphylaxis(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-2", "On prophylaxis - Fluconazole", ReportUtils.map(hivIndicators.onFluconazoleProphylaxis(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-3", "On prophylaxis - Sub-total", ReportUtils.map(hivIndicators.onProphylaxis(), indParams), allColumns);
*/
		return dsd;
	}
}