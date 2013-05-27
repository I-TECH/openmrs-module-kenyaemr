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

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.api.PatientSetService;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DateObsValueBetweenCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.map;

/**
 * Library of common cohort definitions
 */
@Component
public class CommonCohortLibrary {

	/**
	 * Patients who are female
	 * @return the cohort definition
	 */
	public CohortDefinition females() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setName("females");
		cd.setFemaleIncluded(true);
		return cd;
	}

	/**
	 * Patients who are male
	 * @return the cohort definition
	 */
	public CohortDefinition males() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setName("males");
		cd.setMaleIncluded(true);
		return cd;
	}

	/**
	 * Patients who at most maxAge years old on ${effectiveDate}
	 * @return the cohort definition
	 */
	public CohortDefinition agedAtMost(int maxAge) {
		AgeCohortDefinition cd = new AgeCohortDefinition();
		cd.setName("aged at most");
		cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		cd.setMaxAge(maxAge);
		return cd;
	}

	/**
	 * Patients who are at least minAge years old on ${effectiveDate}
	 * @return the cohort definition
	 */
	public CohortDefinition agedAtLeast(int minAge) {
		AgeCohortDefinition cd = new AgeCohortDefinition();
		cd.setName("aged at least");
		cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		cd.setMinAge(minAge);
		return cd;
	}

	/**
	 * Patients who are female and at least 18 years old on ${effectiveDate}
	 * @return the cohort definition
	 */
	public CohortDefinition femalesAgedAtLeast18() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("females aged at least 18");
		cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		cd.addSearch("females", map(females()));
		cd.addSearch("agedAtLeast18", map(agedAtLeast(18), "effectiveDate=${effectiveDate}"));
		cd.setCompositionString("females AND agedAtLeast18");
		return cd;
	}

	/**
	 * Patients who have an encounter between ${onOrAfter} and ${onOrBefore}
	 * @param types the encounter types
	 * @return the cohort definition
	 */
	public CohortDefinition hasEncounter(EncounterType... types) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setTimeQualifier(TimeQualifier.ANY);
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		if (types.length > 0) {
			cd.setEncounterTypeList(Arrays.asList(types));
		}
		return cd;
	}

	/**
	 * Patients who are enrolled on the given program between ${enrolledOnOrAfter} and ${enrolledOnOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledInProgram(Program program) {
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setName("enrolled in program between dates");
		cd.addParameter(new Parameter("enrolledOnOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("enrolledOnOrBefore", "Before Date", Date.class));
		cd.setPrograms(Collections.singletonList(program));
		return cd;
	}

	/**
	 * Patients who transferred in between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition transferredIn() {
		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);

		DateObsValueBetweenCohortDefinition cd = new DateObsValueBetweenCohortDefinition();
		cd.setName("transferred in between dates");
		cd.setQuestion(transferInDate);
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		return cd;
	}

	/**
	 * Patients who transferred in between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition transferredOut() {
		Concept reasonForDiscontinue = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept transferredOut = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.setName("transferred out between dates");
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(reasonForDiscontinue);
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Collections.singletonList(transferredOut));
		return cd;
	}
}