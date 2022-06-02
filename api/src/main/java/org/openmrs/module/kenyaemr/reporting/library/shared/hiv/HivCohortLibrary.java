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
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.DateObsValueBetweenCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.CtxFromAListOfMedicationOrdersCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.FirstProgramEnrollment;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art.TransferredInAfterEnrollmentCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
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

	@Autowired
	private Moh731CohortLibrary moh731CohortLibrary;

	/**
	 * Patients referred from the given entry point onto the HIV program
	 * @param entryPoints the entry point concepts
	 * @return the cohort definition
	 */
	public CohortDefinition referredFrom(Concept... entryPoints) {
		EncounterType hivEnrollEncType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
		Concept methodOfEnrollment = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("referred from");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(TimeModifier.ANY);
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
		EncounterType hivEnrollEncType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
		Concept methodOfEnrollment = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("referred not from");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(TimeModifier.ANY);
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
		return commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV));
	}

	/**
	 * Patients who were enrolled in HIV care (excluding transfers) between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledExcludingTransfers() {
		return commonCohorts.enrolledExcludingTransfers(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV));
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
		cd.addSearch("completeProgram", ReportUtils.map(commonCohorts.compltedProgram(), "completedOnOrBefore=${onOrBefore}"));
		cd.setCompositionString("enrolledExcludingTransfers AND referredFrom AND NOT completeProgram");
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
		cd.addSearch("completeProgram", ReportUtils.map(commonCohorts.compltedProgram(), "completedOnOrBefore=${onOrBefore}"));
		cd.setCompositionString("enrolledExcludingTransfers AND referredNotFrom AND NOT completeProgram");
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
		EncounterType hivEnrollment = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
		EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
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
		onCtx.setTimeModifier(TimeModifier.LAST);
		onCtx.setQuestion(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED));
		onCtx.setValueList(Arrays.asList(Dictionary.getConcept(Dictionary.YES)));
		onCtx.setOperator(SetComparator.IN);

		CalculationCohortDefinition ctxFromAListOfMedicationOrders = new CalculationCohortDefinition(new CtxFromAListOfMedicationOrdersCalculation());
		ctxFromAListOfMedicationOrders.setName("ctxFromAListOfMedicationOrders");
		ctxFromAListOfMedicationOrders.addParameter(new Parameter("OnDate", "On Date", Date.class));

		//we need to include those patients who have either ctx in the med orders
		//that was not captured coded obs

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Having CTX either dispensed");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("onCtx", ReportUtils.map(onCtx, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onMedCtx", ReportUtils.map(commonCohorts.medicationDispensed(Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM), Dictionary.getConcept(Dictionary.DAPSONE)),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ctxFromAListOfMedicationOrders", ReportUtils.map(ctxFromAListOfMedicationOrders, "onDate=${onOrBefore}"));
		cd.addSearch("transferredOutDeadAndLtf", ReportUtils.map(transferredOutDeadAndLtf(), "onOrBefore=${onOrBefore}"));
		cd.setCompositionString("(onCtx OR onMedCtx OR ctxFromAListOfMedicationOrders) AND NOT transferredOutDeadAndLtf");

		return cd;
	}

	/**
	 * Patients who are transferred out, deads ltf
	 * @return the cohort definition
	 */
	public CohortDefinition transferredOutDeadAndLtf(){
		CalculationCohortDefinition calcLtf = new CalculationCohortDefinition(new LostToFollowUpCalculation());
		calcLtf.setName("lost to follow up");
		calcLtf.addParameter(new Parameter("onDate", "On Date", Date.class));

		CalculationCohortDefinition calcTout = new CalculationCohortDefinition(new IsTransferOutCalculation());
		calcTout.setName("to patients");
		calcTout.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.addSearch("ltf", ReportUtils.map(calcLtf, "onDate=${onOrBefore}"));
		cd.addSearch("to", ReportUtils.map(calcTout, "onDate=${onOrBefore}"));
		cd.addSearch("missedAppointment", ReportUtils.map(moh731CohortLibrary.missedAppointment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("deceased OR ltf OR to OR missedAppointment");

		return cd;

	}


	/**
	 * Patients who are in HIV care and are taking CTX prophylaxis between ${onOrAfter} and ${onOrBefore}
	 * @return
	 */
	public CohortDefinition inHivProgramAndOnCtxProphylaxis() {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in HIV program and on CTX prophylaxis");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inProgram", ReportUtils.map(commonCohorts.inProgram(hivProgram), "onDate=${onOrBefore}"));
		cd.addSearch("onCtxProphylaxis", ReportUtils.map(onCtxProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd .addSearch("transferredOutDeadAndLtf", ReportUtils.map(transferredOutDeadAndLtf(), "onOrBefore=${onOrBefore}"));
		cd.setCompositionString("(inProgram AND onCtxProphylaxis) AND NOT transferredOutDeadAndLtf");
		return cd;
	}

	/**
	 * Patients who are in HIV care and are taking Fluconazole prophylaxis between ${onOrAfter} and ${onOrBefore}
	 * @return
	 */
	public CohortDefinition inHivProgramAndOnFluconazoleProphylaxis() {
		Concept flucanozole = Dictionary.getConcept(Dictionary.FLUCONAZOLE);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
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
		cd.addSearch("testedForHivHivInfected", ReportUtils.map(commonCohorts.hasObs(hivInfected, indeterminate, positive, negative), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("resultOfHivTest OR testedForHivHivInfected");
		return cd;
	}

	/**
	 * Patients tested for HIV and turn to be positive ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public  CohortDefinition testedHivStatus(Concept status) {
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivInfected = Dictionary.getConcept(Dictionary.HIV_INFECTED);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("tested for positive for HIV");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("resultOfHivTestPositive", ReportUtils.map(commonCohorts.hasObs(hivStatus, status), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("testedForHivHivInfectedPositive", ReportUtils.map(commonCohorts.hasObs(hivInfected ,status), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("resultOfHivTestPositive OR testedForHivHivInfectedPositive");
		return cd;
	}

	/**
	 * Patients who started art from the transfer facility
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtFromTransferringFacilityOnDate() {
		Concept starteArtFromTransferringFacility = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_TREATMENT_START_DATE);
		DateObsValueBetweenCohortDefinition cd = new DateObsValueBetweenCohortDefinition();
		cd.setName("Patients Who Started ART From the Transferring Facility between date");
		cd.setQuestion(starteArtFromTransferringFacility);
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		return cd;

	}
	/**
	 * Patients enrolled in HIV program based on their first enrollment
	 * @return the CohortDefinition
	 */
	public CohortDefinition firstProgramEnrollment(Integer outcomePeriod) {
		DateCalculationCohortDefinition cd = new DateCalculationCohortDefinition(new FirstProgramEnrollment());
		cd.setName("First program enrollment date");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

		CompositionCohortDefinition compCd = new CompositionCohortDefinition();
		compCd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		compCd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

		CalculationCohortDefinition calcCd = new CalculationCohortDefinition(new TransferredInAfterEnrollmentCalculation());
		calcCd.setName("boolean transfer in after enrollment");
		calcCd.addParameter(new Parameter("onDate", "On Date", Date.class));
		calcCd.addCalculationParameter("outcomePeriod", outcomePeriod);


		compCd.addSearch("cd", ReportUtils.map(cd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		compCd.addSearch("calcCd", ReportUtils.map(calcCd));

		compCd.setCompositionString("cd AND NOT calcCd");

		return compCd;
	}
}