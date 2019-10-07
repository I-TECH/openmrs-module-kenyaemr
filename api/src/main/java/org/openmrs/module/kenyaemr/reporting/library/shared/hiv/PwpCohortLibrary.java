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

import org.openmrs.Concept;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
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
		cd.setTimeModifier(TimeModifier.ANY);
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
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(methodOfFamilyPlanning);
		cd.setValueList(Arrays.asList(naturalFamilyPlanning, sexualAbstinence, notApplicable, otherNonCoded, none));
		cd.setOperator(SetComparator.NOT_IN);
		return cd;
	}
}