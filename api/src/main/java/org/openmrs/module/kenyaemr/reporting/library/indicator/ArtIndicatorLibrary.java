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

package org.openmrs.module.kenyaemr.reporting.library.indicator;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.reporting.library.cohort.ArtCohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.library.indicator.CommonIndicatorLibrary.createCohortIndicator;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.map;

/**
 * Library of ART related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ArtIndicatorLibrary {

	@Autowired
	private CommonIndicatorLibrary commonIndicators;

	@Autowired
	private ArtCohortLibrary artCohorts;

	/**
	 * Number of patients enrolled in HIV care (excluding transfers)
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfers() {
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
		return commonIndicators.enrolledExcludingTransfers(hivProgram);
	}

	/**
	 * Number of patients who started ART
	 * @return the indicator
	 */
	public CohortIndicator startedArt() {
		return createCohortIndicator("Number of patients who started ART", map(artCohorts.startedArt(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
}