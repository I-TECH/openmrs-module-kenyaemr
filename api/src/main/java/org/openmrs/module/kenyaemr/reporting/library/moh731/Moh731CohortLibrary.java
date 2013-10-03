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

package org.openmrs.module.kenyaemr.reporting.library.moh731;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of cohort definitions used specifically in the MOH731 report
 */
@Component
public class Moh731CohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	private HivCohortLibrary hivCohorts;

	/**
	 * Patients who ART revisits
	 * @return the cohort definition
	 */
	public CohortDefinition revisitsArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		cd.addParameter(new Parameter("toDate", "To Date", Date.class));
		cd.addSearch("startedBefore", ReportUtils.map(hivCohorts.startedArt(), "onOrBefore=${startDate-1d}"));
		cd.addSearch("recentEncounter", ReportUtils.map(commonCohorts.hasEncounter(), "onOrAfter=${endDate-90d},onOrBefore=${endDate}"));
		cd.setCompositionString("recentEncounter AND startedBefore");
		return cd;
	}

	/**
	 * Currently on ART.. we could calculate this several ways...
	 * @return the cohort definition
 	 */
	public CohortDefinition currentlyOnArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		cd.addParameter(new Parameter("toDate", "To Date", Date.class));
		cd.addSearch("startedArt", ReportUtils.map(hivCohorts.startedArt(), "onOrAfter=${fromDate},onOrBefore=${toDate}"));
		cd.addSearch("revisitsArt", ReportUtils.map(revisitsArt(), "fromDate=${fromDate},toDate=${toDate}"));
		cd.setCompositionString("startedArt OR revisitsArt");
		return cd;
	}

	/**
	 * Taking original 1st line ART at 12 months
	 * @return the cohort definition
	 */
	public CohortDefinition onOriginalFirstLineAt12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		cd.addParameter(new Parameter("toDate", "To Date", Date.class));
		cd.addSearch("art12MonthNetCohort", ReportUtils.map(hivCohorts.netCohort12Months(), "onDate=${toDate}"));
		cd.addSearch("currentlyOnOriginalFirstLine", ReportUtils.map(hivCohorts.onOriginalFirstLine(), "onDate=${toDate}"));
		cd.setCompositionString("art12MonthNetCohort AND currentlyOnOriginalFirstLine");
		return cd;
	}

	/**
	 * Taking alternate 1st line ART at 12 months
	 * @return the cohort definition
	 */
	public CohortDefinition onAlternateFirstLineAt12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		cd.addParameter(new Parameter("toDate", "To Date", Date.class));
		cd.addSearch("art12MonthNetCohort", ReportUtils.map(hivCohorts.netCohort12Months(), "onDate=${toDate}"));
		cd.addSearch("currentlyOnAlternateFirstLine", ReportUtils.map(hivCohorts.onAlternateFirstLine(), "onDate=${toDate}"));
		cd.setCompositionString("art12MonthNetCohort AND currentlyOnAlternateFirstLine");
		return cd;
	}

	/**
	 * Taking 2nd line ART at 12 months
	 * @return the cohort definition
	 */
	public CohortDefinition onSecondLineAt12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		cd.addParameter(new Parameter("toDate", "To Date", Date.class));
		cd.addSearch("art12MonthNetCohort", ReportUtils.map(hivCohorts.netCohort12Months(), "onDate=${toDate}"));
		cd.addSearch("currentlyOnSecondLine", ReportUtils.map(hivCohorts.onSecondLine(), "onDate=${toDate}"));
		cd.setCompositionString("art12MonthNetCohort AND currentlyOnSecondLine");
		return cd;
	}

	/**
	 * Taking any ART at 12 months
	 * @return the cohort definition
	 */
	public CohortDefinition onTherapyAt12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		cd.addParameter(new Parameter("toDate", "To Date", Date.class));
		cd.addSearch("art12MonthNetCohort", ReportUtils.map(hivCohorts.netCohort12Months(), "onDate=${toDate}"));
		cd.addSearch("currentlyOnArt", ReportUtils.map(hivCohorts.onArt(), "onDate=${toDate}"));
		cd.setCompositionString("art12MonthNetCohort AND currentlyOnArt");
		return cd;
	}
}