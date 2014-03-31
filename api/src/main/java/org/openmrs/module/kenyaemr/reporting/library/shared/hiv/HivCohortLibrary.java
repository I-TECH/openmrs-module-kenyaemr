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
import org.openmrs.module.kenyaemr.calculation.library.hiv.NeverTakenCtxOrDapsoneCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
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
	 * Patients with a CD4 result between {onOrAfter} and {onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition hasCd4Result() {
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
	public CohortDefinition hasHivVisit() {
		EncounterType hivEnrollment = MetadataUtils.getEncounterType(HivMetadata._EncounterType.HIV_ENROLLMENT);
		EncounterType hivConsultation = MetadataUtils.getEncounterType(HivMetadata._EncounterType.HIV_CONSULTATION);
		return commonCohorts.hasEncounter(hivEnrollment, hivConsultation);
	}

	/**
	 * Patients who took CTX prophylaxis between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition onCtxProphylaxis() {
		CodedObsCohortDefinition onCtx = new CodedObsCohortDefinition();
		onCtx.setName("on CTX prophylaxis");
		onCtx.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		onCtx.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		onCtx.setTimeModifier(PatientSetService.TimeModifier.LAST);
		onCtx.setQuestion(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED));
		onCtx.setValueList(Arrays.asList(Dictionary.getConcept(Dictionary.YES)));
		onCtx.setOperator(SetComparator.IN);

		CalculationCohortDefinition onMedCtx = new CalculationCohortDefinition(new NeverTakenCtxOrDapsoneCalculation());
		onMedCtx.setName("on ctx from medication orders");
		onMedCtx.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		onMedCtx.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Having CTX either from either points");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("onCtx", ReportUtils.map(onCtx, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onMedCtx", ReportUtils.map(onMedCtx,"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("onCtx OR onMedCtx");

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