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
import org.openmrs.api.PatientSetService;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Library of PwP related definitions
 */
@Component
public class PwpCohortLibrary {

	/**
	 * Patients who were provided condoms between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition condomsProvided() {
		Concept condomsProvided = Dictionary.getConcept(Dictionary.CONDOMS_PROVIDED_DURING_VISIT);
		Concept yes = Dictionary.getConcept(Dictionary.YES);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("condoms provided");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(condomsProvided);
		cd.setValueList(Collections.singletonList(yes));
		cd.setOperator(SetComparator.IN);
		return cd;
	}

	/**
	 * Patients who were provided modern contraceptives between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition modernContraceptivesProvided() {
		Concept methodOfFamilyPlanning = Dictionary.getConcept(Dictionary.METHOD_OF_FAMILY_PLANNING);
		Concept naturalFamilyPlanning = Dictionary.getConcept(Dictionary.NATURAL_FAMILY_PLANNING);
		Concept sexualAbstinence = Dictionary.getConcept(Dictionary.SEXUAL_ABSTINENCE);
		Concept none = Dictionary.getConcept(Dictionary.NONE);
		Concept notApplicable = Dictionary.getConcept(Dictionary.NOT_APPLICABLE);
		Concept otherNonCoded = Dictionary.getConcept(Dictionary.OTHER_NON_CODED);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("modern contraceptives provided");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(methodOfFamilyPlanning);
		cd.setValueList(Arrays.asList(naturalFamilyPlanning, sexualAbstinence, notApplicable, otherNonCoded, none));
		cd.setOperator(SetComparator.NOT_IN);
		return cd;
	}
}