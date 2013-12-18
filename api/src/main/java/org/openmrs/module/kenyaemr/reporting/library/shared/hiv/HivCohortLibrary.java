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
import org.openmrs.Program;
import org.openmrs.api.PatientSetService;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.EligibleForArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnAlternateFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnOriginalFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnSecondLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PregnantAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TbPatientAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Library of ART related cohort definitions
 */
@Component
public class HivCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	/**
	 * Patients referred from the given entry point onto the HIV program
	 * @param entryPoints the entry point concepts
	 * @return the cohort definition
	 */
	public CohortDefinition referredFrom(Concept... entryPoints) {
		EncounterType hivEnrollEncType = MetadataUtils.getEncounterType(HivMetadata._EncounterType.HIV_ENROLLMENT);
		Concept methodOfEnrollment = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("referred from");
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
		EncounterType hivEnrollEncType = MetadataUtils.getEncounterType(HivMetadata._EncounterType.HIV_ENROLLMENT);
		Concept methodOfEnrollment = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("referred not from");
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
		return commonCohorts.enrolled(MetadataUtils.getProgram(HivMetadata._Program.HIV));
	}

	/**
	 * Patients who were enrolled in HIV care (excluding transfers) between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledExcludingTransfers() {
		return commonCohorts.enrolledExcludingTransfers(MetadataUtils.getProgram(HivMetadata._Program.HIV));
	}

	/**
	 * Patients who were enrolled in HIV care (excluding transfers) from the given entry points between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledExcludingTransfersAndReferredFrom(Concept... entryPoints) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("enrolled excluding transfers in HIV care from entry points");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("enrolledExcludingTransfers", ReportUtils.map(enrolledExcludingTransfers(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("referredFrom", ReportUtils.map(referredFrom(entryPoints), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("enrolledExcludingTransfers AND referredFrom");
		return cd;
	}

	/**
	 * Patients who were enrolled in HIV care (excluding transfers) not from the given entry points between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledExcludingTransfersAndNotReferredFrom(Concept... entryPoints) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("enrolled excluding transfers in HIV care not from entry points");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("enrolledExcludingTransfers", ReportUtils.map(enrolledExcludingTransfers(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("referredNotFrom", ReportUtils.map(referredNotFrom(entryPoints), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("enrolledExcludingTransfers AND referredNotFrom");
		return cd;
	}

	/**
	 * Patients who were pregnant when they started ART
	 * @return the cohort definition
	 */
	public CohortDefinition pregnantAtArtStart() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new PregnantAtArtStartCalculation());
		cd.setName("pregnant at start of ART");
		return cd;
	}

	/**
	 * Patients who were TB patients when they started ART
	 * @return the cohort definition
	 */
	public CohortDefinition tbPatientAtArtStart() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new TbPatientAtArtStartCalculation());
		cd.setName("TB patient at start of ART");
		return cd;
	}

	/**
	 * Patients with given WHO stage when started ART
	 * @return the cohort definition
	 */
	public CohortDefinition whoStageAtArtStart(int stage) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new WhoStageAtArtStartCalculation());
		cd.setName("who stage " + stage + " at start of ART");
		cd.setWithResult(stage);
		return cd;
	}

	/**
	 * Patients who started ART between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition startedArt() {
		DateCalculationCohortDefinition cd = new DateCalculationCohortDefinition(new InitialArtStartDateCalculation());
		cd.setName("started ART");
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
		cd.setName("started ART while pregnant");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", ReportUtils.map(startedArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pregnantAtArtStart", ReportUtils.map(pregnantAtArtStart()));
		cd.setCompositionString("startedArt AND pregnantAtArtStart");
		return cd;
	}

	/**
	 * Patients who started ART while being a TB patient between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtWhileTbPatient() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started ART while being TB patient");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", ReportUtils.map(startedArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("tbPatientAtArtStart", ReportUtils.map(tbPatientAtArtStart()));
		cd.setCompositionString("startedArt AND tbPatientAtArtStart");
		return cd;
	}

	/**
	 * Patients who started ART with the given WHO stage between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtWithWhoStage(int stage) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started ART with WHO stage " + stage);
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", ReportUtils.map(startedArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("withWhoStage", ReportUtils.map(whoStageAtArtStart(stage)));
		cd.setCompositionString("startedArt AND withWhoStage");
		return cd;
	}

	/**
	 * Patients who are eligible for ART on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition eligibleForArt() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new EligibleForArtCalculation());
		cd.setName("eligible for ART on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are on ART on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onArt() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new OnArtCalculation());
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
		cd.setName("on ART and pregnant");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("onArt", ReportUtils.map(onArt(), "onDate=${onDate}"));
		cd.addSearch("pregnant", ReportUtils.map(commonCohorts.pregnant(), "onDate=${onDate}"));
		cd.setCompositionString("onArt AND pregnant");
		return cd;
	}

	/**
	 * Patients who are on ART and not pregnant on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onArtAndNotPregnant() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and not pregnant");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("onArt", ReportUtils.map(onArt(), "onDate=${onDate}"));
		cd.addSearch("pregnant", ReportUtils.map(commonCohorts.pregnant(), "onDate=${onDate}"));
		cd.setCompositionString("onArt AND NOT pregnant");
		return cd;
	}

	/**
	 * Patients who are taking their original first line regimen on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onOriginalFirstLine() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new OnOriginalFirstLineArtCalculation());
		cd.setName("on original first line regimen");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are taking an alternate first line regimen on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onAlternateFirstLine() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new OnAlternateFirstLineArtCalculation());
		cd.setName("on alternate first line regimen");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are taking a second line regimen on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onSecondLine() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new OnSecondLineArtCalculation());
		cd.setName("on second line regimen");
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
		cd.addSearch("startedArt12MonthsAgo", ReportUtils.map(startedArt(), "onOrAfter=${onDate-13m},onOrBefore=${onDate-12m}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.transferredOut(), "onOrAfter=${onDate-13m}"));
		cd.setCompositionString("startedArt12MonthsAgo AND NOT transferredOut");
		return cd;
	}

	/**
	 * Patients who took CTX prophylaxis between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition onCtxProphylaxis() {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("on CTX prophylaxis");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(PatientSetService.TimeModifier.LAST);
		cd.setQuestion(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED));
		cd.setValueList(Arrays.asList(Dictionary.getConcept(Dictionary.YES)));
		cd.setOperator(SetComparator.IN);
		return cd;
	}

	/**
	 * Patients who are in HIV care and are taking CTX prophylaxis between ${onOrAfter} and ${onOrBefore}
	 * @return
	 */
	public CohortDefinition inHivProgramAndOnCtxProphylaxis() {
		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in HIV program and on CTX prophylaxis");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inProgram", ReportUtils.map(commonCohorts.inProgram(hivProgram), "onDate=${onOrBefore}"));
		cd.addSearch("onCtxProphylaxis", ReportUtils.map(onCtxProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inProgram AND onCtxProphylaxis");
		return cd;
	}

	/**
	 * Patients who are in HIV care and are taking Fluconazole prophylaxis between ${onOrAfter} and ${onOrBefore}
	 * @return
	 */
	public CohortDefinition inHivProgramAndOnFluconazoleProphylaxis() {
		Concept flucanozole = Dictionary.getConcept(Dictionary.FLUCONAZOLE);
		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in HIV program and on Fluconazole prophylaxis");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inProgram", ReportUtils.map(commonCohorts.inProgram(hivProgram), "onDate=${onOrBefore}"));
		cd.addSearch("onMedication", ReportUtils.map(commonCohorts.medicationDispensed(flucanozole), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inProgram AND onMedication");
		return cd;
	}

	/**
	 * Patients who are in HIV care and are on Flucanzole or CTX prophylaxis
	 * @return
	 */
	public CohortDefinition inHivProgramAndOnAnyProphylaxis() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in HIV program and on any prophylaxis");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("onCtx", ReportUtils.map(inHivProgramAndOnCtxProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onFlucanozole", ReportUtils.map(inHivProgramAndOnFluconazoleProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("onCtx OR onFlucanozole");
		return cd;
	}

	/**
	 * Patients tested for HIV between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public  CohortDefinition testedForHiv() {
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept indeterminate = Dictionary.getConcept(Dictionary.INDETERMINATE);
		Concept hivInfected = Dictionary.getConcept(Dictionary.HIV_INFECTED);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("tested for HIV");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("resultOfHivTest", ReportUtils.map(commonCohorts.hasObs(hivStatus, unknown, positive, negative), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("testedForHivHivInfected", ReportUtils.map(commonCohorts.hasObs(hivInfected, indeterminate,positive,negative), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("resultOfHivTest OR testedForHivHivInfected");
		return cd;
	}

	/**
	 * Patients tested for HIV and turn to be positive ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public  CohortDefinition testedHivPositive() {
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivInfected = Dictionary.getConcept(Dictionary.HIV_INFECTED);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("tested for positive for HIV");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("resultOfHivTestPositive", ReportUtils.map(commonCohorts.hasObs(hivStatus, positive), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("testedForHivHivInfectedPositive", ReportUtils.map(commonCohorts.hasObs(hivInfected ,positive), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("resultOfHivTestPositive OR testedForHivHivInfectedPositive");
		return cd;
	}
}