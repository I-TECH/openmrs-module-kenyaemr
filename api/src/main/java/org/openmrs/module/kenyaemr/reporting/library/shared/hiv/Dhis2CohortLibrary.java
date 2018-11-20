/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.EncounterType;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of Dhis2 related cohort definitions
 */
@Component
public class Dhis2CohortLibrary {
	@Autowired
	CommonCohortLibrary commonCohortLibrary;
	/**
	 * Providing a dummy definition for the missing pieces in dhis
	 * @return cohort definition
	 */
	public CohortDefinition dummyCohortDefinitionMethod() {

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Need to be changed with correct logic - just a place holder");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		return commonCohortLibrary.hasEncounter(MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.CONSULTATION));
	}
}
