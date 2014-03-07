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
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ObsInLastVisitCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
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

	public CohortDefinition hadNutritionalAssessmentAtLastVisit() {
		Concept weight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
		Concept height = Dictionary.getConcept(Dictionary.HEIGHT_CM);
		Concept muac = Dictionary.getConcept(Dictionary.MUAC);

		ObsInLastVisitCohortDefinition hadWeight = new ObsInLastVisitCohortDefinition();
		hadWeight.setName("patients with weight obs in last visit");
		hadWeight.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadWeight.setQuestion(weight);

		ObsInLastVisitCohortDefinition hadHeight = new ObsInLastVisitCohortDefinition();
		hadHeight.setName("patients with height obs in last visit");
		hadHeight.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadHeight.setQuestion(height);

		ObsInLastVisitCohortDefinition hadMuac = new ObsInLastVisitCohortDefinition();
		hadMuac.setName("patients with MUAC obs in last visit");
		hadMuac.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadMuac.setQuestion(muac);

		CompositionCohortDefinition cd =new CompositionCohortDefinition();
		hadMuac.setName("patients with nutritional assessment in last visit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("hadWeight", ReportUtils.map(hadWeight, "onOrBefore=${onOrBefore}"));
		cd.addSearch("hadHeight", ReportUtils.map(hadHeight, "onOrBefore=${onOrBefore}"));
		cd.addSearch("hadMuac", ReportUtils.map(hadMuac, "onOrBefore=${onOrBefore}"));
		cd.setCompositionString("(hadWeight AND hadHeight) OR hadMuac");
		return cd;
	}
}