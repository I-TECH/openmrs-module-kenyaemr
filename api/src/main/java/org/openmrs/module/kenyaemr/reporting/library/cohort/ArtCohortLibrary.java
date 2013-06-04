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
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.art.*;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EmrCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EmrDateCalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.map;

/**
 * Library of ART related cohort definitions
 */
@Component
public class ArtCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	/**
	 * Patients referred from the given entry point onto the HIV program
	 * @param entryPoints the entry point concepts
	 * @return the cohort definition
	 */
	public CohortDefinition referredFrom(Concept... entryPoints) {
		EncounterType hivEnrollEncType = Metadata.getEncounterType(Metadata.HIV_ENROLLMENT_ENCOUNTER_TYPE);
		Concept methodOfEnrollment = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("entered care between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(methodOfEnrollment);
		cd.setValueList(Arrays.asList(entryPoints));
		cd.setOperator(SetComparator.IN);
		cd.setEncounterTypeList(Collections.singletonList(hivEnrollEncType));
		return cd;
	}

	/**
	 * Patients referred from the given entry point onto the HIV program
	 * @param entryPoints the entry point concepts
	 * @return the cohort definition
	 */
	public CohortDefinition referredNotFrom(Concept... entryPoints) {
		EncounterType hivEnrollEncType = Metadata.getEncounterType(Metadata.HIV_ENROLLMENT_ENCOUNTER_TYPE);
		Concept methodOfEnrollment = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("entered care between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(methodOfEnrollment);
		cd.setValueList(Arrays.asList(entryPoints));
		cd.setOperator(SetComparator.NOT_IN);
		cd.setEncounterTypeList(Collections.singletonList(hivEnrollEncType));
		return cd;
	}

	/**
	 * Patients who were enrolled in HIV care (including transfers) between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolled() {
		return commonCohorts.enrolled(Metadata.getProgram(Metadata.HIV_PROGRAM));
	}

	/**
	 * Patients who were enrolled in HIV care (excluding transfers) between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledExcludingTransfers() {
		return commonCohorts.enrolledExcludingTransfers(Metadata.getProgram(Metadata.HIV_PROGRAM));
	}

	/**
	 * Patients who were enrolled in HIV care (excluding transfers) from the given entry points between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledExcludingTransfersAndReferredFrom(Concept... entryPoints) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("enrolled excluding transfers in HIV care from entry points between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("enrolledExcludingTransfers", map(enrolledExcludingTransfers(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("referredFrom", map(referredFrom(entryPoints), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("enrolledExcludingTransfers AND referredFrom");
		return cd;
	}

	/**
	 * Patients who were enrolled in HIV care (excluding transfers) not from the given entry points between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledExcludingTransfersAndNotReferredFrom(Concept... entryPoints) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("enrolled excluding transfers in HIV care not from entry points between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("enrolledExcludingTransfers", map(enrolledExcludingTransfers(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("referredNotFrom", map(referredNotFrom(entryPoints), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("enrolledExcludingTransfers AND referredNotFrom");
		return cd;
	}

	/**
	 * Patients who were pregnant when they started ART
	 * @return the cohort definition
	 */
	public CohortDefinition pregnantAtArtStart() {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new PregnantAtArtStartCalculation());
		cd.setName("pregnant at start of ART");
		return cd;
	}

	/**
	 * Patients who were TB patients when they started ART
	 * @return the cohort definition
	 */
	public CohortDefinition tbPatientAtArtStart() {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new TbPatientAtArtStartCalculation());
		cd.setName("TB patient at start of ART");
		return cd;
	}

	/**
	 * Patients with given WHO stage when started ART
	 * @return the cohort definition
	 */
	public CohortDefinition whoStageAtArtStart(int stage) {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new WhoStageAtArtStartCalculation());
		cd.setName("who stage " + stage + " at start of ART");
		cd.setWithResult(stage);
		return cd;
	}

	/**
	 * Patients who started ART between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition startedArt() {
		EmrDateCalculationCohortDefinition cd = new EmrDateCalculationCohortDefinition(new InitialArtStartDateCalculation());
		cd.setName("started ART between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		return cd;
	}

	/**
	 * Patients who started ART while pregnant between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtWhilePregnant() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started ART while pregnant between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", map(startedArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pregnantAtArtStart", map(pregnantAtArtStart()));
		cd.setCompositionString("startedArt AND pregnantAtArtStart");
		return cd;
	}

	/**
	 * Patients who started ART while being a TB patient between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtWhileTbPatient() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started ART while being TB patient between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", map(startedArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("tbPatientAtArtStart", map(tbPatientAtArtStart()));
		cd.setCompositionString("startedArt AND tbPatientAtArtStart");
		return cd;
	}

	/**
	 * Patients who started ART with the given WHO stage between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtWithWhoStage(int stage) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started ART with WHO stage between dates");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", map(startedArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("withWhoStage", map(whoStageAtArtStart(stage)));
		cd.setCompositionString("startedArt AND withWhoStage");
		return cd;
	}

	/**
	 * Patients who are eligible for ART on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition eligibleForArt() {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new EligibleForArtCalculation());
		cd.setName("eligible for ART on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are on ART on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onArt() {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new OnArtCalculation());
		cd.setName("on ART on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are on ART and pregnant on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onArtAndPregnant() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and pregnant on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("onArt", map(onArt(), "onDate=${onDate}"));
		cd.addSearch("pregnant", map(commonCohorts.pregnant(), "onDate=${onDate}"));
		cd.setCompositionString("onArt AND pregnant");
		return cd;
	}

	/**
	 * Patients who are on ART and not pregnant on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onArtAndNotPregnant() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and not pregnant on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("onArt", map(onArt(), "onDate=${onDate}"));
		cd.addSearch("pregnant", map(commonCohorts.pregnant(), "onDate=${onDate}"));
		cd.setCompositionString("onArt AND NOT pregnant");
		return cd;
	}

	/**
	 * Patients who are taking their original first line regimen on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onOriginalFirstLine() {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new OnOriginalFirstLineArtCalculation());
		cd.setName("on original first line regimen on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are taking an alternate first line regimen on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onAlternateFirstLine() {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new OnAlternateFirstLineArtCalculation());
		cd.setName("on alternate first line regimen on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are taking a second line regimen on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onSecondLine() {
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(new OnSecondLineArtCalculation());
		cd.setName("on second line regimen on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are in the "12 month net cohort" on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition netCohort12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in 12 net cohort on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("startedArt12MonthsAgo", map(startedArt(), "onOrAfter=${onDate-13m},onOrBefore=${onDate-12m}"));
		cd.addSearch("transferredOut", map(commonCohorts.transferredOut(), "onOrAfter=${onDate-13m}"));
		cd.setCompositionString("startedArt12MonthsAgo AND NOT transferredOut");
		return cd;
	}

	/**
	 * Patients who are in HIV care and are on the specified medication on ${onDate}
	 * @return
	 */
	public CohortDefinition inHivProgramAndOnMedication(Concept... concepts) {
		Program hivProgram = Metadata.getProgram(Metadata.HIV_PROGRAM);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in HIV program and on medication between dates");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("inProgram", map(commonCohorts.inProgram(hivProgram), "onDate=${onDate}"));
		cd.addSearch("onMedication", map(commonCohorts.onMedication(concepts), "onDate=${onDate}"));
		cd.setCompositionString("inProgram AND onMedication");
		return cd;
	}
}