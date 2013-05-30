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

package org.openmrs.module.kenyaemr.reporting.library.cohort;

import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Library of TB related cohort definitions
 */
@Component
public class TbCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	/**
	 * Patients who were enrolled in TB program (including transfers) between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolled() {
		return commonCohorts.enrolled(Metadata.getProgram(Metadata.TB_PROGRAM));
	}

	/**
	 * Patients who were screened for TB between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition screenedForTb() {
		return commonCohorts.hasEncounter(Metadata.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE));
	}
}