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
import org.openmrs.Concept;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.BaseIndicatorReportBuilder;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.library.cohort.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.dimension.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.CommonIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;

/**
 * MOH 711 report
 */
@Component
@Builds("kenyaemr.common.report.moh711")
public class Moh711ReportBuilder extends BaseIndicatorReportBuilder {

	protected static final Log log = LogFactory.getLog(Moh711ReportBuilder.class);

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	private CommonIndicatorLibrary commonIndicators;

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	private ArtCohortLibrary artCohorts;

	@Autowired
	private ArtIndicatorLibrary artIndicators;

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.BaseIndicatorReportBuilder#buildDataSets()
	 */
	@Override
	public List<DataSetDefinition> buildDataSets() {
		return Arrays.asList(
				/*createTbDataSet(),*/
				createArtDataSet()
		);
	}

	/**
	 * Creates the ART data set
	 * @return the data set
	 */
	/*private DataSetDefinition createTbDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("G: TB");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", map(commonDimensions.age(), "onDate=${endDate}"));
		dsd.addDimension("gender", map(commonDimensions.gender()));

		List<ColumnParameters> columns = new ArrayList<ColumnParameters>();
		columns.add(new ColumnParameters("FP", "0-14 years, female", "gender=F|age=<15"));
		columns.add(new ColumnParameters("MP", "0-14 years, male", "gender=M|age=<15"));
		columns.add(new ColumnParameters("FA", ">14 years, female", "gender=F|age=15+"));
		columns.add(new ColumnParameters("MA", ">14 years, male", "gender=M|age=15+"));
		columns.add(new ColumnParameters("T", "grand total", ""));

		return dsd;
	}*/

	/**
	 * Creates the ART data set
	 * @return the data set
	 */
	private DataSetDefinition createArtDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("K: ART");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", ReportUtils.map(commonDimensions.age(), "onDate=${endDate}"));
		dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

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
		List<ColumnParameters> femaleColumns = Arrays.asList(colFPeds, colFAdults, colFTotal, colTotal);
		List<ColumnParameters> pedsColumns = Arrays.asList(colFPeds, colFAdults, colFTotal, colTotal);

		String indParams = "startDate=${startDate},endDate=${endDate}";

		EmrReportingUtils.addRow(dsd, "K1-1", "New enrollments - PMTCT", ReportUtils.map(artIndicators.enrolledExcludingTransfersAndReferredFrom(pmtct), indParams), femaleColumns);
		EmrReportingUtils.addRow(dsd, "K1-2", "New enrollments - VCT", ReportUtils.map(artIndicators.enrolledExcludingTransfersAndReferredFrom(vct), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-3", "New enrollments - TB", ReportUtils.map(artIndicators.enrolledExcludingTransfersAndReferredFrom(tb), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-4", "New enrollments - In Patient", ReportUtils.map(artIndicators.enrolledExcludingTransfersAndReferredFrom(inpatient), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-5", "New enrollments - CWC", ReportUtils.map(artIndicators.enrolledExcludingTransfersAndReferredFrom(cwc), indParams), pedsColumns);
		EmrReportingUtils.addRow(dsd, "K1-6", "New enrollments - All others", ReportUtils.map(artIndicators.enrolledExcludingTransfersAndNotReferredFrom(all), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-7", "New enrollments - Sub-total", ReportUtils.map(artIndicators.enrolledExcludingTransfers(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K2", "Cumulative enrolled", ReportUtils.map(artIndicators.enrolledCumulative(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-1", "Starting ARVs - WHO stage 1", ReportUtils.map(artIndicators.startedArtWithWhoStage(1), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-2", "Starting ARVs - WHO stage 2", ReportUtils.map(artIndicators.startedArtWithWhoStage(2), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-3", "Starting ARVs - WHO stage 3", ReportUtils.map(artIndicators.startedArtWithWhoStage(3), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-4", "Starting ARVs - WHO stage 4", ReportUtils.map(artIndicators.startedArtWithWhoStage(4), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-5", "Starting ARVs - Sub-total", ReportUtils.map(artIndicators.startedArt(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K4", "Cumulative started ARV", ReportUtils.map(artIndicators.startedArtCumulative(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K5-1", "Currently on ARVs - Pregnant women", ReportUtils.map(artIndicators.onArtAndPregnant(), indParams), femaleColumns);
		EmrReportingUtils.addRow(dsd, "K5-2", "Currently on ARVs - All others", ReportUtils.map(artIndicators.onArtAndNotPregnant(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K5-3", "Currently on ARVs - Sub-total", ReportUtils.map(artIndicators.onArt(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K6", "Eligible for ART", ReportUtils.map(artIndicators.eligibleForArt(), indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-1", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-2", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-3", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-4", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-1", "On prophylaxis - Cotrimoxazole", ReportUtils.map(artIndicators.onCotrimoxazoleProphylaxis(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-2", "On prophylaxis - Fluconazole", ReportUtils.map(artIndicators.onFluconazoleProphylaxis(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-3", "On prophylaxis - Sub-total", ReportUtils.map(artIndicators.onProphylaxis(), indParams), allColumns);

		return dsd;
	}
}