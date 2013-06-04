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

package org.openmrs.module.kenyaemr.reporting.builder.indicator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.indicator.HivCareVisitsIndicator;
import org.openmrs.module.kenyaemr.reporting.dataset.definition.MergingDataSetDefinition;
import org.openmrs.module.kenyaemr.reporting.library.cohort.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.dimension.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.CommonIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.PwpIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.TbIndicatorLibrary;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.map;

/**
 * MOH 731 report
 */
@Component
public class Moh731Report extends BaseIndicatorReportBuilder {

	protected static final Log log = LogFactory.getLog(Moh731Report.class);

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	private CommonIndicatorLibrary commonIndicators;

	@Autowired
	private ArtCohortLibrary artCohorts;

	@Autowired
	private ArtIndicatorLibrary artIndicators;

	@Autowired
	private TbIndicatorLibrary tbIndicators;

	@Autowired
	private PwpIndicatorLibrary pwpIndicators;

	/**
	 * Report specific cohorts and indicators
	 */
	private Map<String, CohortDefinition> cohortDefinitions;
	private Map<String, CohortIndicator> cohortIndicators;
	private Map<String, Indicator> nonCohortIndicators;

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder#getTags()
	 */
	@Override
	public String[] getTags() {
		return new String[] { "moh", "hiv" };
	}

	/**
	 * @see BaseIndicatorReportBuilder#getName()
	 */
	@Override
	public String getName() {
		return "MOH 731";
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Comprehensive HIV/AIDS Facility Reporting Form - NASCOP";
	}

	/**
	 * @see BaseIndicatorReportBuilder#getExcelTemplateResourcePath()
	 */
	@Override
	public String getExcelTemplateResourcePath() {
		return "report_templates/Moh731Report.xls";
	}

	/**
	 * @see BaseIndicatorReportBuilder#buildDataSets()
	 */
	@Override
	public List<DataSetDefinition> buildDataSets() {
		log.debug("Setting up cohort definitions");

		setupCohortDefinitions();

		log.debug("Setting up cohort indicators");

		setupCohortIndicators();

		log.debug("Setting up non-cohort indicators");

		setupNonCohortIndicators();

		log.debug("Setting up report definition");

		return Arrays.asList(createDataSet());
	}

	private void setupCohortDefinitions() {
		cohortDefinitions = new HashMap<String, CohortDefinition>();
		{ // Revisits on ART
			CompositionCohortDefinition cd = new CompositionCohortDefinition();
			cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
			cd.addParameter(new Parameter("toDate", "To Date", Date.class));
			cd.addSearch("startedBefore", map(artCohorts.startedArt(), "onOrBefore=${startDate-1d}"));
			cd.addSearch("recentEncounter", map(commonCohorts.hasEncounter(), "onOrAfter=${endDate-90d},onOrBefore=${endDate}"));
			cd.setCompositionString("recentEncounter AND startedBefore");
			cohortDefinitions.put("revisitsArt", cd);
		}
		{ // Currently on ART.. we could calculate this several ways...
			CompositionCohortDefinition cd = new CompositionCohortDefinition();
			cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
			cd.addParameter(new Parameter("toDate", "To Date", Date.class));
			cd.addSearch("startedArt", map(artCohorts.startedArt(), "onOrAfter=${fromDate},onOrBefore=${toDate}"));
			cd.addSearch("revisitsArt", map(cohortDefinitions.get("revisitsArt"), "fromDate=${fromDate},toDate=${toDate}"));
			cd.setCompositionString("startedArt OR revisitsArt");
			cohortDefinitions.put("currentlyOnArt", cd);
		}
		{ // Taking original 1st line ART at 12 months
			CompositionCohortDefinition cd = new CompositionCohortDefinition();
			cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
			cd.addParameter(new Parameter("toDate", "To Date", Date.class));
			cd.addSearch("art12MonthNetCohort", map(artCohorts.netCohort12Months(), "onDate=${toDate}"));
			cd.addSearch("currentlyOnOriginalFirstLine", map(artCohorts.onOriginalFirstLine(), "onDate=${toDate}"));
			cd.setCompositionString("art12MonthNetCohort AND currentlyOnOriginalFirstLine");
			cohortDefinitions.put("onOriginalFirstLineAt12Months", cd);
		}
		{ // Taking alternate 1st line ART at 12 months
			CompositionCohortDefinition cd = new CompositionCohortDefinition();
			cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
			cd.addParameter(new Parameter("toDate", "To Date", Date.class));
			cd.addSearch("art12MonthNetCohort", map(artCohorts.netCohort12Months(), "onDate=${toDate}"));
			cd.addSearch("currentlyOnAlternateFirstLine", map(artCohorts.onAlternateFirstLine(), "onDate=${toDate}"));
			cd.setCompositionString("art12MonthNetCohort AND currentlyOnAlternateFirstLine");
			cohortDefinitions.put("onAlternateFirstLineAt12Months", cd);
		}
		{ // Taking 2nd line ART at 12 months
			CompositionCohortDefinition cd = new CompositionCohortDefinition();
			cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
			cd.addParameter(new Parameter("toDate", "To Date", Date.class));
			cd.addSearch("art12MonthNetCohort", map(artCohorts.netCohort12Months(), "onDate=${toDate}"));
			cd.addSearch("currentlyOnSecondLine", map(artCohorts.onSecondLine(), "onDate=${toDate}"));
			cd.setCompositionString("art12MonthNetCohort AND currentlyOnSecondLine");
			cohortDefinitions.put("onSecondLineAt12Months", cd);
		}
		{ // Taking any ART at 12 months
			CompositionCohortDefinition cd = new CompositionCohortDefinition();
			cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
			cd.addParameter(new Parameter("toDate", "To Date", Date.class));
			cd.addSearch("art12MonthNetCohort", map(artCohorts.netCohort12Months(), "onDate=${toDate}"));
			cd.addSearch("currentlyOnArt", map(artCohorts.onArt(), "onDate=${toDate}"));
			cd.setCompositionString("art12MonthNetCohort AND currentlyOnArt");
			cohortDefinitions.put("onTherapyAt12Months", cd);
		}
	}

	private void setupCohortIndicators() {
		cohortIndicators = new HashMap<String, CohortIndicator>();
		{
			CohortIndicator ind = createCohortIndicator("currentlyInCare", "Currently in care (includes transfers)");
			ind.setCohortDefinition(map(commonCohorts.hasEncounter(), "onOrAfter=${endDate-90d},onOrBefore=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("revisitsArt", "Revisits ART");
			ind.setCohortDefinition(map(cohortDefinitions.get("revisitsArt"), "fromDate=${startDate},toDate=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("currentlyOnArt", "Currently on ART");
			ind.setCohortDefinition(map(cohortDefinitions.get("currentlyOnArt"), "fromDate=${startDate},toDate=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("cumulativeOnArt", "Cumulative ever on ART");
			ind.setCohortDefinition(map(artCohorts.startedArt(), "onOrBefore=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("art12MonthNetCohort", "ART 12 Month Net Cohort");
			ind.setCohortDefinition(map(artCohorts.netCohort12Months(), "onDate=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("onOriginalFirstLineAt12Months", "On original 1st line at 12 months");
			ind.setCohortDefinition(map(cohortDefinitions.get("onOriginalFirstLineAt12Months"), "fromDate=${startDate},toDate=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("onAlternateFirstLineAt12Months", "On alternate 1st line at 12 months");
			ind.setCohortDefinition(map(cohortDefinitions.get("onAlternateFirstLineAt12Months"), "fromDate=${startDate},toDate=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("onSecondLineAt12Months", "On 2nd line at 12 months");
			ind.setCohortDefinition(map(cohortDefinitions.get("onSecondLineAt12Months"), "fromDate=${startDate},toDate=${endDate}"));
		}
		{
			CohortIndicator ind = createCohortIndicator("onTherapyAt12Months", "On therapy at 12 months");
			ind.setCohortDefinition(map(cohortDefinitions.get("onTherapyAt12Months"), "fromDate=${startDate},toDate=${endDate}"));
		}
	}

	/**
	 * Setup non-cohort SQL indicators
	 */
	private void setupNonCohortIndicators() {
		nonCohortIndicators = new HashMap<String, Indicator>();
		{
			HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
			ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
			ind.addParameter(new Parameter("endDate", "End Date", Date.class));
			ind.setFilter(HivCareVisitsIndicator.Filter.FEMALES_18_AND_OVER);
			nonCohortIndicators.put("hivCareVisitsFemale18", ind);
		}
		{
			HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
			ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
			ind.addParameter(new Parameter("endDate", "End Date", Date.class));
			ind.setFilter(HivCareVisitsIndicator.Filter.SCHEDULED);
			nonCohortIndicators.put("hivCareVisitsScheduled", ind);
		}
		{
			HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
			ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
			ind.addParameter(new Parameter("endDate", "End Date", Date.class));
			ind.setFilter(HivCareVisitsIndicator.Filter.UNSCHEDULED);
			nonCohortIndicators.put("hivCareVisitsUnscheduled", ind);
		}
		{
			HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
			ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
			ind.addParameter(new Parameter("endDate", "End Date", Date.class));
			nonCohortIndicators.put("hivCareVisitsTotal", ind);
		}
	}

	/**
	 * Creates the report data set
	 * @return the data set
	 */
	private DataSetDefinition createDataSet() {
		CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
		cohortDsd.setName(getName() + " Cohort DSD");
		cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cohortDsd.addDimension("age", map(commonDimensions.age(), "onDate=${endDate}"));
		cohortDsd.addDimension("gender", map(commonDimensions.gender()));

		SimpleIndicatorDataSetDefinition nonCohortDsd = new SimpleIndicatorDataSetDefinition();
		nonCohortDsd.setName(getName() + " Non-cohort DSD");
		nonCohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		nonCohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		MergingDataSetDefinition mergedDsd = new MergingDataSetDefinition();
		mergedDsd.setName(getName() + " DSD");
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

		// TODO 3.1 (On Cotrimoxazole Prophylaxis)

		// 3.2 (Enrolled in Care)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Enrolled in care", map(artIndicators.enrolledExcludingTransfers(), indParams), allColumns, Arrays.asList("08", "09", "10", "11", "12", "13"));

		// 3.3 (Currently in Care)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Currently in care", map(cohortIndicators.get("currentlyInCare"), indParams), allColumns, Arrays.asList("14", "15", "16", "17", "18", "19"));

		// 3.4 (Starting ART)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Starting ART", map(artIndicators.startedArt(), indParams), allColumns, Arrays.asList("20", "21", "22", "23", "24", "25"));

		cohortDsd.addColumn("HV03-26", "Starting ART (Pregnant)", map(artIndicators.startedArtWhilePregnant(), indParams), "");
		cohortDsd.addColumn("HV03-27", "Starting ART (TB Patient)", map(artIndicators.startedArtWhileTbPatient(), indParams), "");

		// 3.5 (Revisits ART)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Revisits ART", map(cohortIndicators.get("revisitsArt"), indParams), allColumns, Arrays.asList("28", "29", "30", "31", "32", "33"));

		// 3.6 (Currently on ART [All])
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Currently on ART [All]", map(cohortIndicators.get("currentlyOnArt"), indParams), allColumns, Arrays.asList("34", "35", "36", "37", "38", "39"));

		// 3.7 (Cumulative Ever on ART)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Cumulative ever on ART", map(cohortIndicators.get("cumulativeOnArt"), indParams), nonInfantColumns, Arrays.asList("40", "41", "42", "43", "44"));

		// 3.8 (Survival and Retention on ART at 12 months)
		cohortDsd.addColumn("HV03-46", "ART Net Cohort at 12 months", map(cohortIndicators.get("art12MonthNetCohort"), indParams), "");
		cohortDsd.addColumn("HV03-46", "On original 1st Line at 12 months", map(cohortIndicators.get("onOriginalFirstLineAt12Months"), indParams), "");
		cohortDsd.addColumn("HV03-47", "On alternative 1st Line at 12 months", map(cohortIndicators.get("onAlternateFirstLineAt12Months"), indParams), "");
		cohortDsd.addColumn("HV03-48", "On 2nd Line (or higher) at 12 months ", map(cohortIndicators.get("onSecondLineAt12Months"), indParams), "");
		cohortDsd.addColumn("HV03-49", "On therapy at 12 months (Total) ", map(cohortIndicators.get("onTherapyAt12Months"), indParams), "");

		// 3.9 (Screening)
		EmrReportingUtils.addRow(cohortDsd, "HV03", "Screened for TB", map(tbIndicators.screenedForTb(), indParams), nonInfantColumns, Arrays.asList("50", "51", "52", "53", "54"));

		// TODO HV03-55 (Screened for cervical cancer (F 18+))

		// 3.10 (Prevention with Positives)
		cohortDsd.addColumn("HV09-04", "Modern contraceptive methods", map(pwpIndicators.modernContraceptivesProvided(), indParams), "");
		cohortDsd.addColumn("HV09-05", "Provided with condoms", map(pwpIndicators.condomsProvided(), indParams), "");

		// 3.11 (HIV Care Visits)
		nonCohortDsd.addColumn("HV03-70", "HIV care visits (Females 18+)", map(nonCohortIndicators.get("hivCareVisitsFemale18"), indParams));
		nonCohortDsd.addColumn("HV03-71", "HIV care visits (Scheduled)", map(nonCohortIndicators.get("hivCareVisitsScheduled"), indParams));
		nonCohortDsd.addColumn("HV03-72", "HIV care visits (Unscheduled)", map(nonCohortIndicators.get("hivCareVisitsUnscheduled"), indParams));
		nonCohortDsd.addColumn("HV03-73", "HIV care visits (Total)", map(nonCohortIndicators.get("hivCareVisitsTotal"), indParams));

		return mergedDsd;
	}

	protected CohortIndicator createCohortIndicator(String id, String description) {
		CohortIndicator ind = new CohortIndicator(description);
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		cohortIndicators.put(id, ind);
		return ind;
	}
}