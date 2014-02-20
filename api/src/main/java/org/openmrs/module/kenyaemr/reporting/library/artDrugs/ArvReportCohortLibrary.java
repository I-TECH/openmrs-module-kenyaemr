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

package org.openmrs.module.kenyaemr.reporting.library.artDrugs;

import org.openmrs.Concept;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RegimenOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
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
public class ArvReportCohortLibrary {

	@Autowired
	RegimenManager regimenManager;

	/**
	 * Current ART regimen of each patient
	 * @return the cohort definition
	 */
	public CohortDefinition onRegimen(List<Concept> drugConcepts) {
		RegimenOrderCohortDefinition cd = new RegimenOrderCohortDefinition();
		Set<Concept> drugConceptSet = new HashSet<Concept>(drugConcepts);
		cd.setName("ART regimen");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setMasterConceptSet(regimenManager.getMasterSetConcept("ARV"));
		cd.setConceptSet(drugConceptSet);
		return cd;
	}
}
