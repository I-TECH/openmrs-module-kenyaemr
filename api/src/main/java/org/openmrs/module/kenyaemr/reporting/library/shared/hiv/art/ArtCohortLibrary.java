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

package org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art;

import org.openmrs.Concept;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RegimenOrderCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Library of ART Drugs related cohort definitions
 */
@Component
public class ArtCohortLibrary {

	@Autowired
	private RegimenManager regimenManager;

	@Autowired
	private CommonCohortLibrary commonCohorts;

	/**
	 * Patients on the given regimen. In the future this should look at dispensing records during the reporting period
	 * which implicitly check whether a patient is active. As a workaround until we get to dispensing records, we
	 * explicitly check whether a patient is active here by looking for recent encounters.
	 *
	 * @return the cohort definition
	 */
	public CohortDefinition onRegimen(List<Concept> drugConcepts) {
		RegimenOrderCohortDefinition regCd = new RegimenOrderCohortDefinition();
		Set<Concept> drugConceptSet = new HashSet<Concept>(drugConcepts);
		regCd.setName("ART regimen");
		regCd.addParameter(new Parameter("onDate", "On Date", Date.class));
		regCd.setMasterConceptSet(regimenManager.getMasterSetConcept("ARV"));
		regCd.setConceptSet(drugConceptSet);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Has an encounter in last 3 months and on regimen");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("onRegimen", ReportUtils.map(regCd, "onDate=${onDate}"));
		cd.addSearch("hasEncounterInLast3Months", ReportUtils.map(commonCohorts.hasEncounter(), "onOrAfter=${onDate-90d}"));
		cd.setCompositionString("onRegimen AND hasEncounterInLast3Months");
		return cd;
	}
}