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

package org.openmrs.module.kenyaemr.reporting.library.cohortAnalysis;

import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.calculation.library.cohort.OneMonthCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.PatientCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.RetentionAliveCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.RetentionDeadCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.RetentionLTFUCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.RetentionStoppedCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.RetentionTOCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.SixMonthCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.ThreeMonthCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.TwoMonthsCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.TwoWeeksCohortCalculation;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of cohorts of interest to Global funds research
 */
@Component
public class SixMonthsAdherenceCohortLibrary {

	/**
	 * All patients
	 */
	public CohortDefinition allPatientsCohortDefinition() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new PatientCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.setName("All patients cohort");
		return cd;
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortDefinition twoWeeksCohortDefinition() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new TwoWeeksCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.setName("Two weeks art refill cohort");
		return cd;
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortDefinition oneMonthCohortDefinition() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new OneMonthCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.setName("One Month art refill cohort");
		return cd;
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortDefinition twoMonthCohortDefinition() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new TwoMonthsCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.setName("Two Months art refill cohort");
		return cd;
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortDefinition threeMonthCohortDefinition() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new ThreeMonthCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.setName("Three Months art refill cohort");
		return cd;
	}
	/**
	 * Two weeks cohort definition
	 */
	public CohortDefinition sixMonthCohortDefinition() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new SixMonthCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.setName("Six Months art refill cohort");
		return cd;
	}

	/**
	 * alive cohort definition
	 */
	public CohortDefinition aliveCohortDefinition(String key) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new RetentionAliveCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("cohort", key);
		cd.setName("alive at 12 months cohort");
		return cd;
	}

	/**
	 * alive cohort definition
	 */
	public CohortDefinition deadCohortDefinition(String key) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new RetentionDeadCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("cohort", key);
		cd.setName("dead at 12 months cohort");
		return cd;
	}

	/**
	 * alive cohort definition
	 */
	public CohortDefinition ltfuCohortDefinition(String key) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new RetentionLTFUCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("cohort", key);
		cd.setName("ltfu at 12 months cohort");
		return cd;
	}

	/**
	 * alive cohort definition
	 */
	public CohortDefinition stoppedCohortDefinition(String key) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new RetentionStoppedCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("cohort", key);
		cd.setName("stopped art at 12 months cohort");
		return cd;
	}

	/**
	 * alive cohort definition
	 */
	public CohortDefinition toCohortDefinition(String key) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new RetentionTOCohortCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("cohort", key);
		cd.setName("TO at 12 months cohort");
		return cd;
	}
}