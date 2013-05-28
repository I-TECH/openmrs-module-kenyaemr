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
import org.openmrs.module.kenyaemr.reporting.dataset.definition.MergingDataSetDefinition;
import org.openmrs.module.kenyaemr.reporting.library.cohort.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.dimension.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.indicator.CommonIndicatorLibrary;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
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
	 * @see org.openmrs.module.kenyaemr.reporting.builder.indicator.BaseIndicatorReportBuilder#buildDataSet()
	 */
	@Override
	public DataSetDefinition buildDataSet() {
		log.debug("Setting up cohort definitions");

		setupCohortDefinitions();

		log.debug("Setting up cohort indicators");

		setupCohortIndicators();

		log.debug("Setting up report definition");

		return createDataSet();
	}

	private void setupCohortDefinitions() {
		cohortDefinitions = new HashMap<String, CohortDefinition>();
	}

	private void setupCohortIndicators() {
		cohortIndicators = new HashMap<String, CohortIndicator>();
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

		MergingDataSetDefinition mergedDsd = new MergingDataSetDefinition();
		mergedDsd.setName(getName() + " DSD");
		mergedDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		mergedDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		mergedDsd.addDataSetDefinition(cohortDsd);
		mergedDsd.setMergeOrder(MergingDataSetDefinition.MergeOrder.NAME);

		/////////////// K1 (Number of new patients enrolled excluding transfers) ///////////////

		cohortDsd.addColumn("K1-7-1", "New enrollments - sub total (0-14 years, Female)", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), "gender=F|age=<15");
		cohortDsd.addColumn("K1-7-2", "New enrollments - sub total (0-14 years, Male)", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), "gender=M|age=<15");
		cohortDsd.addColumn("K1-7-3", "New enrollments - sub total (>14 years, Female)", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), "gender=F|age=15+");
		cohortDsd.addColumn("K1-7-4", "New enrollments - sub total (>14 years, Male)", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), "gender=M|age=15+");
		cohortDsd.addColumn("K1-7-5", "New enrollments - sub total (Totals, Female)", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), "gender=F");
		cohortDsd.addColumn("K1-7-6", "New enrollments - sub total (Totals, Male)", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), "gender=M");
		cohortDsd.addColumn("K1-7-7", "New enrollments - sub total (Grand Total)", map(artIndicators.enrolledExcludingTransfers(), "startDate=${startDate},endDate=${endDate}"), "");

		cohortDsd.addColumn("K3-5-1", "Starting ARVs - sub total (0-14 years, Female)", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), "gender=F|age=<15");
		cohortDsd.addColumn("K3-5-2", "Starting ARVs - sub total (0-14 years, Male)", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), "gender=M|age=<15");
		cohortDsd.addColumn("K3-5-3", "Starting ARVs - sub total (>14 years, Female)", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), "gender=F|age=15+");
		cohortDsd.addColumn("K3-5-4", "Starting ARVs - sub total (>14 years, Male)", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), "gender=M|age=15+");
		cohortDsd.addColumn("K3-5-5", "Starting ARVs - sub total (Totals, Female)", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), "gender=F");
		cohortDsd.addColumn("K3-5-6", "Starting ARVs - sub total (Totals, Male)", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), "gender=M");
		cohortDsd.addColumn("K3-5-7", "Starting ARVs - sub total (Grand Total)", map(artIndicators.startedArt(), "startDate=${startDate},endDate=${endDate}"), "");

		return mergedDsd;
	}
}