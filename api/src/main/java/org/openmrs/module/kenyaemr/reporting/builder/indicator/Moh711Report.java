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
import org.openmrs.module.kenyaemr.reporting.dataset.definition.MergingDataSetDefinition;
import org.openmrs.module.kenyaemr.reporting.library.cohort.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.dimension.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.CommonIndicatorLibrary;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.map;

/**
 * MOH 711 report
 */
@Component
public class Moh711Report extends BaseIndicatorReportBuilder {

	protected static final Log log = LogFactory.getLog(Moh711Report.class);

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
	 * Report specific cohorts and indicators
	 */
	private Map<String, CohortDefinition> cohortDefinitions;
	private Map<String, CohortIndicator> cohortIndicators;

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder#getTags()
	 */
	@Override
	public String[] getTags() {
		return new String[] { "moh", "hiv", "tb" };
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.indicator.BaseIndicatorReportBuilder#getName()
	 */
	@Override
	public String getName() {
		return "MOH 711";
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder#getDescription()
	 */
	@Override
	public String getDescription() {
		return "National Integrated Form for Reproductive Health, HIV/AIDS, Malaria, TB and Child Nutrition";
	}

	/**
	 * @see BaseIndicatorReportBuilder#buildDataSets()
	 */
	@Override
	public List<DataSetDefinition> buildDataSets() {
		log.debug("Setting up cohort definitions");

		//setupCohortDefinitions();

		log.debug("Setting up cohort indicators");

		//setupCohortIndicators();

		log.debug("Setting up data set definitions");

		return Arrays.asList(
				createTbDataSet(),
				createArtDataSet()
		);
	}

	/*private void setupCohortDefinitions() {
		cohortDefinitions = new HashMap<String, CohortDefinition>();
	}

	private void setupCohortIndicators() {
		cohortIndicators = new HashMap<String, CohortIndicator>();
	}*/

	/**
	 * Creates the ART data set
	 * @return the data set
	 */
	private DataSetDefinition createTbDataSet() {
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
	}

	/**
	 * Creates the ART data set
	 * @return the data set
	 */
	private DataSetDefinition createArtDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("K: ART");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", map(commonDimensions.age(), "onDate=${endDate}"));
		dsd.addDimension("gender", map(commonDimensions.gender()));

		List<ColumnParameters> columns = new ArrayList<ColumnParameters>();
		columns.add(new ColumnParameters("FP", "0-14 years, female", "gender=F|age=<15"));
		columns.add(new ColumnParameters("MP", "0-14 years, male", "gender=M|age=<15"));
		columns.add(new ColumnParameters("FA", ">14 years, female", "gender=F|age=15+"));
		columns.add(new ColumnParameters("MA", ">14 years, male", "gender=M|age=15+"));
		columns.add(new ColumnParameters("F", "totals, female", "gender=F"));
		columns.add(new ColumnParameters("M", "totals, male", "gender=M"));
		columns.add(new ColumnParameters("T", "grand total", ""));

		EmrReportingUtils.addRow(dsd, "K1-7", "New enrollments - sub total", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), columns);
		EmrReportingUtils.addRow(dsd, "K2-1", "Cumulative enrolled", map(artIndicators.enrolledCumulative(), "startDate=${startDate},endDate=${endDate}"), columns);
		EmrReportingUtils.addRow(dsd, "K3-5", "Starting ARVs - sub total", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), columns);

		return dsd;
	}
}