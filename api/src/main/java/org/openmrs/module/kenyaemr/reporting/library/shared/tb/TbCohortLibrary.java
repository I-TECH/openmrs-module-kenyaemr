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

package org.openmrs.module.kenyaemr.reporting.library.shared.tb;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of TB related cohort definitions
 */
@Component
public class TbCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;
	@Autowired
	private HivCohortLibrary hivCohortLibrary;

	/**
	 * Patients who were enrolled in TB program (including transfers) between ${enrolledOnOrAfter} and ${enrolledOnOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolled() {
		return commonCohorts.enrolled(MetadataUtils.existing(Program.class, TbMetadata._Program.TB));
	}

	/**
	 * Patients who were screened for TB between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition screenedForTb() {
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept diseaseDiagnosed = Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED);
		Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);
		return commonCohorts.hasObs(tbDiseaseStatus, diseaseSuspected, diseaseDiagnosed, noSignsOrSymptoms);
	}

	/**
	 * TB patients who died TB between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition died() {
		Concept tbTreatmentOutcome = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME);
		Concept died = Dictionary.getConcept(Dictionary.DIED);
		return commonCohorts.hasObs(tbTreatmentOutcome, died);
	}

	/**
	 * TB patients who started treatment in the month a year before ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition started12MonthsAgo() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started TB treatment 12 months ago");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("enrolled12MonthsAgo", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onDate-13m},enrolledOnOrBefore=${onDate-12m}"));
		cd.setCompositionString("enrolled12MonthsAgo");
		return cd;
	}

	/**
	 * TB patients started treatment in the month a year before and died between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition diedAndStarted12MonthsAgo() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started TB treatment 12 months ago and died");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("died", ReportUtils.map(died(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("started12MonthsAgo", ReportUtils.map(started12MonthsAgo(), "onDate=${onOrBefore}"));
		cd.setCompositionString("died AND started12MonthsAgo");
		return cd;
	}

	/**
	 * TB patients who completed treatment between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition completedTreatment() {
		Concept tbTreatmentOutcome = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME);
		Concept complete = Dictionary.getConcept(Dictionary.TREATMENT_COMPLETE);
		return commonCohorts.hasObs(tbTreatmentOutcome, complete);
	}

	/**
	 * TB patients defaulted (i.e. who missed last appointment) between ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition defaulted() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new MissedLastAppointmentCalculation());
		cd.setName("defaulted");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * patients in TB and HIV and on CPT between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition inTbAndHivProgramsAndOnCtxProphylaxis() {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		Concept[] drugs = { Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM) };
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.inProgram(hivProgram), "onDate=${onOrBefore}"));
		cd.addSearch("inTbProgram", ReportUtils.map(commonCohorts.inProgram(tbProgram), "onDate=${onOrBefore}"));
		cd.addSearch("onCtx", ReportUtils.map(commonCohorts.medicationDispensed(drugs), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND inTbProgram AND onCtx");
		return cd;
	}

	/**
	 * patients tested for HIV(concept id 1169 or 1594217) and are tb patients ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition testedForHivAndInTbProgram() {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inTbProgram", ReportUtils.map(commonCohorts.inProgram(tbProgram), "onDate=${onOrBefore}"));
		cd.addSearch("hivTested", ReportUtils.map(hivCohortLibrary.testedForHiv(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inTbProgram AND hivTested");
		return cd;
	}

	/**
	 * patients tested for HIV(concept id 1169 or 1594217) and are tb patients and the results turned out to be positive ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition testedHivPositiveAndInTbProgram() {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inTbProgram", ReportUtils.map(commonCohorts.inProgram(tbProgram), "onDate=${onOrBefore}"));
		cd.addSearch("hivTestedPositive", ReportUtils.map(hivCohortLibrary.testedHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inTbProgram AND hivTestedPositive");
		return cd;
	}

	/**
	 * Patients who have Tb re treatment   between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition tbRetreatments() {
		Concept patientClassification = Dictionary.getConcept(Dictionary.TYPE_OF_TB_PATIENT);
		Concept retreatment = Dictionary.getConcept(Dictionary.RETREATMENT_AFTER_DEFAULT_TUBERCULOSIS);
		return commonCohorts.hasObs(patientClassification,retreatment);
	}

	/**
	 * Patients who have expulmonary Tb    between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition extraPulmonaryTbPatients() {
		Concept diseaseClassification = Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE);
		Concept extraPulmonary = Dictionary.getConcept(Dictionary.MYCROBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
		return commonCohorts.hasObs(diseaseClassification,extraPulmonary);
	}

	/**
	 * Patients who have pulmonary Tb    between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition pulmonaryTbPatients() {
		Concept diseaseClassification = Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE);
		Concept pulmonary = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		return commonCohorts.hasObs(diseaseClassification,pulmonary);
	}

	/**
	 * Patients who have smear negative   between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition smearNegativePatients() {
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		Concept smearNegative = Dictionary.getConcept(Dictionary.NEGATIVE);
		return commonCohorts.hasObs(cultureResults,smearNegative);
	}

	/**
	 * Patients who have smear positive   between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition smearPositivePatients() {
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);
		return commonCohorts.hasObs(cultureResults,smearPositive);
	}

	/**
	 * Patients who have pulmonary Tb and  smear negative   between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition pulmonaryTbSmearNegative() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pulmonaryTbPatients", ReportUtils.map(pulmonaryTbPatients(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("smearNegative", ReportUtils.map(smearNegativePatients(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("pulmonaryTbPatients AND smearNegative");
		return cd;
	}

	/**
	 * Patients who have pulmonary Tb and  smear positive   between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition pulmonaryTbSmearPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pulmonaryTbPatients", ReportUtils.map(pulmonaryTbPatients(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("smearPositive", ReportUtils.map(smearPositivePatients(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("pulmonaryTbPatients AND smearPositive");
		return cd;
	}

	/**
	 * Patients have new Tb detected cases   between ${onOrAfter} and ${onOrBefore}
	 */
	public CohortDefinition tbNewDetectedCases() {
		Concept patientClassification = Dictionary.getConcept(Dictionary.TYPE_OF_TB_PATIENT);
		Concept newDetects = Dictionary.getConcept(Dictionary.SMEAR_POSITIVE_NEW_TUBERCULOSIS_PATIENT);
		return commonCohorts.hasObs(patientClassification,newDetects);
	}

}