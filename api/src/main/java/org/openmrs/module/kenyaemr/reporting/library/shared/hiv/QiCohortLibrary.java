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

package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.Dictionary;
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
 * Library of Quality Improvement cohorts for HIV care
 */
@Component
public class QiCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	/**
	 * Patients with a CD4 result between {onOrAfter} and {onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition patientsWithCd4() {
		Concept cd4Count = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Concept cd4Percent = Dictionary.getConcept(Dictionary.CD4_PERCENT);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("patients with CD4 results");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasCdCount", ReportUtils.map(commonCohorts.hasObs(cd4Count), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasCd4Percent", ReportUtils.map(commonCohorts.hasObs(cd4Percent), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("hasCdCount OR hasCd4Percent");
		return cd;
	}

	/**
	 * Patients with a HIV care visit between {onOrAfter} and {onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition patientsWithHivVisit() {
		EncounterType hivEnrollment = MetadataUtils.getEncounterType(HivMetadata._EncounterType.HIV_ENROLLMENT);
		EncounterType hivConsultation = MetadataUtils.getEncounterType(HivMetadata._EncounterType.HIV_CONSULTATION);
		return commonCohorts.hasEncounter(hivEnrollment, hivConsultation);
	}
}