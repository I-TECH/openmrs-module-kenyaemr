/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.tb;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.ScreenedForTbInLastVisitCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.PatientLastVisitCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.MissedLastTbAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbInitialTreatmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbTreatmentStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
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

	@Autowired
	private ArtCohortLibrary artCohortLibrary;

	@Autowired
	private Moh731CohortLibrary moh731CohortLibrary;

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
	 * Patients who are currently in care and screened for Tb during their last visit
	 * @return CohortDefinition
	 */
	public CohortDefinition currentlyOnCareAndScreenedInTheLastVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();

		CalculationCohortDefinition calculationCohortDefinition = new CalculationCohortDefinition(new ScreenedForTbInLastVisitCalculation());
		calculationCohortDefinition.setName("Screened for Tb during the last visit ");
		calculationCohortDefinition.addParameter(new Parameter("onDate", "On Date", Date.class));


		cd.setName("Currently in care and screen for Tb during last visit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("currentlyInCare", ReportUtils.map(moh731CohortLibrary.currentlyInCare(), "onDate=${onOrBefore}"));
		cd.addSearch("screenedForTbDuringLastVisit", ReportUtils.map(calculationCohortDefinition, "onDate=${onOrBefore}"));
		cd.setCompositionString("currentlyInCare AND screenedForTbDuringLastVisit");
		return cd;
	}

	/**
	 * Patients who were screened for tb and are not on any tb treatment
	 * @return the cohort definition
	 */
	public CohortDefinition screenedForTbAndNotOnTbTreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept onTreatment = Dictionary.getConcept(Dictionary.ON_TREATMENT_FOR_DISEASE);
		cd.setName("screened for tb and not on tb treatment");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("screened", ReportUtils.map(screenedForTb(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("currentlyOnCareAndScreenedInTheLastVisit", ReportUtils.map(currentlyOnCareAndScreenedInTheLastVisit(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("onTreatment", ReportUtils.map(commonCohorts.hasObs(tbDiseaseStatus, onTreatment), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("(screened OR currentlyOnCareAndScreenedInTheLastVisit) AND NOT onTreatment");

		return cd;
	}

	/**
	 * Patients who are screened for Tb and are Hiv positive
	 * @return CohortDefinition
	 */
	public CohortDefinition screenedForTbAndHivPositive(){
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Screened for tb and in hiv program");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("screenedForTb", ReportUtils.map(screenedForTbAndNotOnTbTreatment(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("inHivProgram", ReportUtils.map(hivCohortLibrary.enrolled(),"enrolledOnOrBefore=${onOrBefore}"));
		cd.setCompositionString("screenedForTb AND inHivProgram");
		return cd;

	}

	/**
	 * Patients screened for TB using ICF form
	 * @return CohortDefinition
	 */
	public CohortDefinition screenedForTbUsingICF() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new PatientLastVisitCalculation());
		cd.setName("Patients who had tb screens in last visit");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition comp = new CompositionCohortDefinition();
		comp.setName("Screened for tb in last visit using ICF form and some observations saved");
		comp.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		comp.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		comp.addSearch("usingICF", ReportUtils.map(cd, "onDate=${onOrBefore}"));
		comp.addSearch("obsSaved", ReportUtils.map(screenedForTb(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		comp.setCompositionString("usingICF AND obsSaved");

		return comp;
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
	 *
	 */
	public CohortDefinition startedTbTreatmentBetweenDates() {
		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		Concept tbStartDate = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE);
		cd.setName("Patients who started Tb treatment between dates");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.setQuestion(tbStartDate);
		cd.setTimeModifier(TimeModifier.LAST);
		return cd;
	}

	/**
	 * TB patients who started treatment in the month a year before ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition started12MonthsAgo() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started TB treatment 12 months ago");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("enrolledInTbProgram", ReportUtils.map(enrolled(), "enrolledOnOrBefore=${onDate}"));
		cd.addSearch("startedTreatmentInTheMonthAyYearBefore", ReportUtils.map(startedTbTreatmentBetweenDates(), "onOrAfter=${onDate-13},onOrBefore=${onDate-12}"));
		cd.setCompositionString("enrolledInTbProgram AND startedTreatmentInTheMonthAyYearBefore");
		return cd;
	}

	/**
	 * TB patients started treatment in the month a year before and died between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition diedAndStarted12MonthsAgo() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("started TB treatment 12 months ago and died");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("died", ReportUtils.map(died(), "onOrBefore=${onOrBefore}"));
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
	 * TB patients defaulted (i.e. who missed last appointment) on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition defaulted() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new MissedLastTbAppointmentCalculation());
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
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(hivProgram), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("inTbProgram", ReportUtils.map(commonCohorts.enrolled(tbProgram), "enrolledOnOrBefore=${onOrBefore}"));
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
		cd.addSearch("inTbProgram", ReportUtils.map(commonCohorts.enrolled(tbProgram), "enrolledOnOrBefore=${onOrBefore}"));
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
		cd.addSearch("inTbProgram", ReportUtils.map(commonCohorts.enrolled(tbProgram), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("hivTestedPositive", ReportUtils.map(hivCohortLibrary.testedHivStatus(Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
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

	/**
	 * Patients who have smear NOT done
	 */
	public CohortDefinition pulmonaryTbSmearNotDone() {
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		Concept smearNotDone = Dictionary.getConcept(Dictionary.NOT_DONE);
		return commonCohorts.hasObs(cultureResults,smearNotDone);
	}

	/**
	 * Patients in tb program and smear not done and have pulmonary tb results at 2 months
	 * @return the cohort definition
	 */
	public CohortDefinition ptbSmearNotDoneResultsAtMonths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pulmonaryTbPatients", ReportUtils.map(pulmonaryTbPatients(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("smearNotDone", ReportUtils.map(pulmonaryTbSmearNotDone(), "onOrBefore=${onOrBefore}"));
		cd.setCompositionString("pulmonaryTbPatients AND smearNotDone");
		return cd;
	}

	/**
	 * Patients who started tb results n months later
	 * @return cohort definition
	 */
	public CohortDefinition startedTbTreatmentResultsAtMonths(Integer months) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new TbTreatmentStartDateCalculation());
		cd.setName("patients Tb results at"+ months + "months");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("months", months);
		return cd;
	}

	/**
	 * Patients who have completed their initial tb treatment
	 * @return cohort definition
	 */
	public CohortDefinition completedInitialTreatment() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new TbInitialTreatmentCalculation());
		cd.setName("patients who completed tb initial treatment on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Total enrolled patients into tb program and have ptb smear not done results at 2 months
	 * @return cohort definition
	 */
	public CohortDefinition totalEnrolledPtbSmearNotDoneResultsAtMonths(int highMonths, int leastMonths  ) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Total enrolled in 2 months between "+leastMonths+" and "+highMonths);
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResultsAt2Months", ReportUtils.map(ptbSmearNotDoneResultsAtMonths(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("enrolledLMonthsAgo", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrBefore-"+ leastMonths +"m},enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("enrolledHMonthsAgo", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrBefore-"+ highMonths +"m},enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("resultsAt2Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(2), "onDate=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAt2Months AND enrolledLMonthsAgo AND enrolledHMonthsAgo AND resultsAt2Months");
		return cd;
	}

	/**
	 * Patient who finalized their initial treatment
	 * return the cohort definition
	 */
	public CohortDefinition ptbSmearNotDoneResults2MonthsFinalizedInitialtreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Finalized Initial treatment 8 to 12 months");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResultsAt2Months", ReportUtils.map(totalEnrolledPtbSmearNotDoneResultsAtMonths(12, 8), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAt2Months AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 * Patients who died
	 * @return cohort definition
	 */
	public CohortDefinition ptbSmearNotDoneResults2MonthsDied() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResultsAt2Months", ReportUtils.map(totalEnrolledPtbSmearNotDoneResultsAtMonths(12,8), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAt2Months AND died");
		return cd;
	}

	/**
	 * Patient who absconded the treatment
	 * @return cohort definition
	 */
	public CohortDefinition ptbSmearNotDoneResults2MonthsAbsconded() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResultsAt2Months", ReportUtils.map(totalEnrolledPtbSmearNotDoneResultsAtMonths(12, 8), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("defaulted", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAt2Months AND defaulted");
		return cd;
	}

	/**
	 * Patients who transferred out
	 * @return cohort definition
	 */
	public CohortDefinition ptbSmearNotDoneResults2MonthsTransferredOut() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResultsAt2Months", ReportUtils.map(totalEnrolledPtbSmearNotDoneResultsAtMonths(12, 8), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAt2Months AND transferredOut");
		return cd;
	}

	/**
	 * Total Patients evaluated
	 * @return cohort Definition
	 */
	public CohortDefinition ptbSmearNotDoneResults2MonthsTotalEvaluated() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResults2MonthsFinalizedInitialtreatment", ReportUtils.map(ptbSmearNotDoneResults2MonthsFinalizedInitialtreatment(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ptbSmearNotDoneResults2MonthsDied", ReportUtils.map(ptbSmearNotDoneResults2MonthsDied(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ptbSmearNotDoneResults2MonthsAbsconded", ReportUtils.map(ptbSmearNotDoneResults2MonthsAbsconded(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ptbSmearNotDoneResults2MonthsTransferredOut", ReportUtils.map(ptbSmearNotDoneResults2MonthsTransferredOut(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResults2MonthsFinalizedInitialtreatment OR ptbSmearNotDoneResults2MonthsDied OR ptbSmearNotDoneResults2MonthsAbsconded OR ptbSmearNotDoneResults2MonthsTransferredOut");
		return  cd;
	}

	/**
	 * Total enrolled 8-12 months earlier and have results a 8 months
	 * @return cohort definition
	 */
	public CohortDefinition totalEnrolled8Months(int highMonths, int leastMonths ) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResultsAtMonths", ReportUtils.map(ptbSmearNotDoneResultsAtMonths(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("enrolledHMonthsAgo", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrBefore-"+ highMonths +"m},enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("enrolledLMonthsAgo", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrBefore-"+ leastMonths +"m},enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("resultsAt8Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(8), "onDate=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAtMonths AND enrolledHMonthsAgo AND enrolledLMonthsAgo AND resultsAt8Months");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV positive
	 * @return cohort definition
	 */
	public CohortDefinition totalEnrolled8MonthsHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		Concept hivPositive = Dictionary.getConcept(Dictionary.POSITIVE);
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8Months", ReportUtils.map(totalEnrolled8Months(12, 8), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivPositive", ReportUtils.map(hivCohortLibrary.testedHivStatus(hivPositive), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8Months AND hivPositive");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV negative
	 * @return cohort definition
	 */
	public CohortDefinition totalEnrolled8MonthsHivNegative() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		Concept hivNegative = Dictionary.getConcept(Dictionary.NEGATIVE);
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8Months", ReportUtils.map(totalEnrolled8Months(12, 8), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivNegative", ReportUtils.map(hivCohortLibrary.testedHivStatus(hivNegative), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8Months AND hivNegative");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV test not done
	 * @return cohort definition
	 */
	public CohortDefinition totalEnrolled8MonthsHivTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		Concept hivNotDone = Dictionary.getConcept(Dictionary.NOT_DONE);
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8Months", ReportUtils.map(totalEnrolled8Months(12, 8), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivTestNotDone", ReportUtils.map(hivCohortLibrary.testedHivStatus(hivNotDone), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8Months AND hivTestNotDone");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * results at 8 months
	 */
	public CohortDefinition totalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("hivPositive", ReportUtils.map(totalEnrolled8MonthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivNegative", ReportUtils.map(totalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivNotDone", ReportUtils.map(totalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("hivPositive OR hivNegative OR hivNotDone");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on cpt
	 * @return cohort definition
	 */
	public CohortDefinition totalEnrolled8MonthsHivPositiveOnCpt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositive", ReportUtils.map(totalEnrolled8MonthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onCpt", ReportUtils.map(inTbAndHivProgramsAndOnCtxProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositive AND onCpt");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on art
	 * @return cohort definition
	 */
	public CohortDefinition totalEnrolled8MonthsHivPositiveOnArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositive", ReportUtils.map(totalEnrolled8MonthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositive AND onART");
		return cd;
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * completed the treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentResults8monthsHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositive", ReportUtils.map(totalEnrolled8MonthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositive AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * died
	 * @return cohort definition
	 */
	public CohortDefinition diedResults8monthsHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositive", ReportUtils.map(totalEnrolled8MonthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositive AND died");
		return cd;
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * absconded
	 * @return cohort definition
	 */
	public CohortDefinition abscondedResults8monthsHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositive", ReportUtils.map(totalEnrolled8MonthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositive AND absconded");
		return cd;
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * Transferred out
	 * @return cohort definition
	 */
	public CohortDefinition transferredOutResults8monthsHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositive", ReportUtils.map(totalEnrolled8MonthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositive AND transferredOut");
		return cd;
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * Transferred out, absconded, died, finished initial treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatmentResults8monthsHivPositive", ReportUtils.map(finalizedInitialTreatmentResults8monthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("diedResults8monthsHivPositive", ReportUtils.map(diedResults8monthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("abscondedResults8monthsHivPositive", ReportUtils.map(abscondedResults8monthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOutResults8monthsHivPositive", ReportUtils.map(transferredOutResults8monthsHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatmentResults8monthsHivPositive OR diedResults8monthsHivPositive OR abscondedResults8monthsHivPositive OR transferredOutResults8monthsHivPositive");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV negative
	 * finalized treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentTotalEnrolled8MonthsHivNegative() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivNegative", ReportUtils.map(totalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivNegative AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV negative
	 * died
	 * @return cohort definition
	 */
	public CohortDefinition diedTotalEnrolled8MonthsHivNegative() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivNegative", ReportUtils.map(totalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivNegative AND died");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV negative
	 * absconded
	 * @return cohort definition
	 */
	public CohortDefinition abscondedTotalEnrolled8MonthsHivNegative() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivNegative", ReportUtils.map(totalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivNegative AND absconded");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV negative
	 * transferred out
	 * @return cohort definition
	 */
	public CohortDefinition transferredOutTotalEnrolled8MonthsHivNegative() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivNegative", ReportUtils.map(totalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivNegative AND transferredOut");
		return cd;
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV negative
	 * Transferred out, absconded, died, finished initial treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivNegative() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatmentResults8monthsHivNegative", ReportUtils.map(finalizedInitialTreatmentTotalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("diedResults8monthsHivNegative", ReportUtils.map(diedTotalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("abscondedResults8monthsHivNegative", ReportUtils.map(abscondedTotalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOutResults8monthsHivNegative", ReportUtils.map(transferredOutTotalEnrolled8MonthsHivNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatmentResults8monthsHivNegative OR diedResults8monthsHivNegative OR abscondedResults8monthsHivNegative OR transferredOutResults8monthsHivNegative");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV test not done
	 * finalized  initial treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentTotalEnrolled8MonthsHivTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivTestNotDone AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV test not done
	 * died
	 * @return cohort definition
	 */
	public CohortDefinition diedTotalEnrolled8MonthsHivTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivTestNotDone AND died");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV test not done
	 * absconded
	 * @return cohort definition
	 */
	public CohortDefinition abscondedTotalEnrolled8MonthsHivTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivTestNotDone AND absconded");
		return cd;
	}

	/**
	 *Total enrolled 8-12 months earlier and have results a 8 months and are HIV test not done
	 * Transferred out
	 * @return cohort definition
	 */
	public CohortDefinition transferredOutTotalEnrolled8MonthsHivTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivTestNotDone AND transferredOut");
		return cd;
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV test not done
	 * Transferred out, absconded, died, finished initial treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatmentResults8monthsHivTestNotDone", ReportUtils.map(finalizedInitialTreatmentTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("diedResults8monthsHivTestNotDone", ReportUtils.map(diedTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("abscondedResults8monthsHivTestNotDone", ReportUtils.map(abscondedTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOutResults8monthsHivTestNotDone", ReportUtils.map(transferredOutTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatmentResults8monthsHivTestNotDone OR diedResults8monthsHivTestNotDone OR abscondedResults8monthsHivTestNotDone OR transferredOutResults8monthsHivTestNotDone");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * Finalized initial treatment
	 * results at 8 months
	 */
	public CohortDefinition finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveNegativeTestNotDone AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * died
	 * results at 8 months
	 */
	public CohortDefinition diedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveNegativeTestNotDone AND died");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * absconded
	 * results at 8 months
	 */
	public CohortDefinition abscondedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveNegativeTestNotDone AND absconded");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * absconded
	 * results at 8 months
	 */
	public CohortDefinition transferredOutTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(totalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveNegativeTestNotDone AND transferredOut");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * total evaluated
	 * results at 8 months
	 */
	public CohortDefinition finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveNegativeTestNotDone() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("diedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(diedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("abscondedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(abscondedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOutTotalEnrolled8MonthsHivPositiveNegativeTestNotDone", ReportUtils.map(transferredOutTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveNegativeTestNotDone OR diedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone OR abscondedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone OR transferredOutTotalEnrolled8MonthsHivPositiveNegativeTestNotDone");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on cpt
	 * finalized initial treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnCpt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnCpt AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on cpt
	 * died
	 * @return cohort definition
	 */
	public CohortDefinition diedTotalEnrolled8MonthsHivPositiveOnCpt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnCpt AND died");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on cpt
	 * absconded
	 * @return cohort definition
	 */
	public CohortDefinition abscondedTotalEnrolled8MonthsHivPositiveOnCpt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnCpt AND absconded");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on cpt
	 * transferred out
	 * @return cohort definition
	 */
	public CohortDefinition transferredOutTotalEnrolled8MonthsHivPositiveOnCpt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnCpt AND transferredOut");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * total evaluated
	 * results at 8 months
	 */
	public CohortDefinition finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnCpt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("diedTotalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(diedTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("abscondedTotalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(abscondedTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOutTotalEnrolled8MonthsHivPositiveOnCpt", ReportUtils.map(transferredOutTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnCpt OR diedTotalEnrolled8MonthsHivPositiveOnCpt OR abscondedTotalEnrolled8MonthsHivPositiveOnCpt OR transferredOutTotalEnrolled8MonthsHivPositiveOnCpt");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on art
	 * finalized initial treatment
	 * @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnArt AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on art
	 * died
	 * @return cohort definition
	 */
	public CohortDefinition diedTotalEnrolled8MonthsHivPositiveOnArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnArt AND died");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on art
	 * absconded
	 * @return cohort definition
	 */
	public CohortDefinition abscondedTotalEnrolled8MonthsHivPositiveOnArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnArt AND absconded");
		return cd;
	}

	/**
	 * Total patients enrolled 8-12 months earlier
	 * hiv +
	 * results at 8 months
	 * on art
	 * transfer out
	 * @return cohort definition
	 */
	public CohortDefinition transferOutTotalEnrolled8MonthsHivPositiveOnArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("totalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(totalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("totalEnrolled8MonthsHivPositiveOnArt AND transferOut");
		return cd;
	}

	/**
	 * Total number of patients registered 8-12 months earlier
	 * Patients should be hiv+, hiv-, hiv Test not done
	 * on art
	 * total evaluated
	 * results at 8 months
	 */
	public CohortDefinition finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("diedTotalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(diedTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("abscondedTotalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(abscondedTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferOutTotalEnrolled8MonthsHivPositiveOnArt", ReportUtils.map(transferOutTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnArt OR diedTotalEnrolled8MonthsHivPositiveOnArt OR abscondedTotalEnrolled8MonthsHivPositiveOnArt OR transferOutTotalEnrolled8MonthsHivPositiveOnArt");
		return cd;
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 2 months
	 * @return cohort definition
	 */
	public CohortDefinition newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(int highMonths, int leastMonths) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pulmoaryTbSmearNegative", ReportUtils.map(pulmonaryTbSmearNegative(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("enrolled15MonthsAgo", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrBefore-"+ highMonths +"m},enrolledOnOrBefore=${onOrBefore"));
		cd.addSearch("enrolled12MonthsAgo", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrBefore-"+ leastMonths +"m},enrolledOnOrBefore=${onOrBefore"));
		cd.addSearch("resultsAt2Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(2), "onDate=${onOrBefore}"));
		cd.setCompositionString("pulmoaryTbSmearNegative AND enrolled15MonthsAgo AND AND enrolled12MonthsAgo AND resultsAt2Months");
		return cd;
	}

	/**
	 * newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months
	 * finalized initial treatment
	 */
	public CohortDefinition finalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(15, 12),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(completedInitialTreatment(), "onDate=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 * newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months
	 * died
	 */
	public CohortDefinition diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(15, 12),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(died(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months AND died");
		return cd;
	}

	/**
	 * newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months
	 * absconded
	 */
	public CohortDefinition abscondedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(15, 12),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months AND absconded");
		return cd;
	}

	/**
	 * newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months
	 * transfer out
	 */
	public CohortDefinition transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(15, 12),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months AND transferOut");
		return cd;
	}

	/**
	 * newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months
	 * transfer out,absconded,died,finalized initial treatment
	 */
	public CohortDefinition transferOutAbscondedDiedFinalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(finalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("abscondedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(abscondedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months", ReportUtils.map(transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months OR diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months OR abscondedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months OR transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months");
		return cd;
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * @return cohort definition
	 */
	public CohortDefinition newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(int highMonths, int leastMonths) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pulmoaryTbSmearNegative", ReportUtils.map(pulmonaryTbSmearNegative(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("enrolled", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrAfter-"+ highMonths +"m},enrolledOnOrBefore=${onOrAfter-"+ leastMonths + "m}"));
		cd.addSearch("resultsAt2Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(8), "onDate=${onOrBefore}"));
		cd.setCompositionString("pulmoaryTbSmearNegative AND enrolled AND resultsAt2Months");
		return cd;
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * treatment completed
	 * @return cohort definition
	 */
	public CohortDefinition treatmentCompletedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("treatmentCompleted", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TREATMENT_COMPLETE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months AND treatmentCompleted");
		return cd;
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * died
	 * @return cohort definition
	 */
	public CohortDefinition diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months AND died");
		return cd;
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * Out of control
	 * @return cohort definition
	 */
	public CohortDefinition outOfControlNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("outOfControl", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TYPE_OF_TB_PATIENT), Dictionary.getConcept(Dictionary.RETREATMENT_AFTER_DEFAULT_TUBERCULOSIS)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months AND outOfControl");
		return cd;
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * transfer out
	 * @return cohort definition
	 */
	public CohortDefinition transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months AND transferOut");
		return cd;
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * became smear positive
	 * @return cohort definition
	 */
	public CohortDefinition becameSmearPositiveNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months", ReportUtils.map(newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("becameSmearPositive", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_FAILURE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months AND becameSmearPositive");
		return cd;
	}

	/**
	 * NewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months
	 * transfer out,out of control,became smear positive,died,completed treatment
	 */
	public CohortDefinition transferOutOutOfControlDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("treatmentCompleted", ReportUtils.map(treatmentCompletedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("outOfControl", ReportUtils.map(outOfControlNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferOut", ReportUtils.map(transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("becameSmearPositive", ReportUtils.map(becameSmearPositiveNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("treatmentCompleted OR died OR outOfControl OR transferOut OR becameSmearPositive");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 2 months
	 *  @return cohort definition
	 */
	public CohortDefinition extraPulmonaryTbResultsAt2Months(int highMonths, int leastMonths) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbPatients", ReportUtils.map(extraPulmonaryTbPatients(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("enrolled", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrAfter-"+ highMonths +"m},enrolledOnOrBefore=${onOrAfter-"+ leastMonths + "m}"));
		cd.addSearch("resultsAt2Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(2), "onDate=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbPatients AND enrolled AND resultsAt2Months");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 2 months
	 *  finalizedInitialTreatment
	 *  @return cohort definition
	 */
	public CohortDefinition finalizedInitialTreatmentExtraPulmonaryTbResultsAt2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt2Months", ReportUtils.map(extraPulmonaryTbResultsAt2Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TREATMENT_COMPLETE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt2Months AND finalizedInitialTreatment");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 2 months
	 *  died
	 *  @return cohort definition
	 */
	public CohortDefinition diedExtraPulmonaryTbResultsAt2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt2Months", ReportUtils.map(extraPulmonaryTbResultsAt2Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt2Months AND died");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 2 months
	 *  absconded
	 *  @return cohort definition
	 */
	public CohortDefinition abscondedExtraPulmonaryTbResultsAt2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt2Months", ReportUtils.map(extraPulmonaryTbResultsAt2Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DEFAULTED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt2Months AND absconded");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 2 months
	 *  transferredOut
	 *  @return cohort definition
	 */
	public CohortDefinition transferredOutExtraPulmonaryTbResultsAt2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt2Months", ReportUtils.map(extraPulmonaryTbResultsAt2Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt2Months AND transferredOut");
		return cd;
	}

	/**
	 * ExtraPulmonaryTbResultsAt2Months
	 * transfer out,absconded,died,completed treatment
	 */
	public CohortDefinition transferOutAbscondedDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("finalizedInitialTreatment", ReportUtils.map(finalizedInitialTreatmentExtraPulmonaryTbResultsAt2Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(diedExtraPulmonaryTbResultsAt2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("absconded", ReportUtils.map(abscondedExtraPulmonaryTbResultsAt2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(transferredOutExtraPulmonaryTbResultsAt2Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("finalizedInitialTreatment OR died OR absconded OR transferredOut");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 8 months
	 *  @return cohort definition
	 */
	public CohortDefinition extraPulmonaryTbResultsAt8Months(int highMonths, int leastMonths) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbPatients", ReportUtils.map(extraPulmonaryTbPatients(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("enrolled", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrAfter-"+ highMonths +"m},enrolledOnOrBefore=${onOrAfter-"+ leastMonths + "m}"));
		cd.addSearch("resultsAt2Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(8), "onDate=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbPatients AND enrolled AND resultsAt2Months");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 8 months
	 *  treatment completed
	 *  @return cohort definition
	 */
	public CohortDefinition treatmentCompleteExtraPulmonaryTbResultsAt8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt8Months", ReportUtils.map(extraPulmonaryTbResultsAt8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("treatmentCompleted", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TREATMENT_COMPLETE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt8Months AND treatmentCompleted");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 8 months
	 *  died
	 *  @return cohort definition
	 */
	public CohortDefinition diedExtraPulmonaryTbResultsAt8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt8Months", ReportUtils.map(extraPulmonaryTbResultsAt8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.DIED)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt8Months AND died");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 8 months
	 *  out of control
	 *  @return cohort definition
	 */
	public CohortDefinition outOfControlExtraPulmonaryTbResultsAt8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt8Months", ReportUtils.map(extraPulmonaryTbResultsAt8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("outOfControl", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TYPE_OF_TB_PATIENT), Dictionary.getConcept(Dictionary.RETREATMENT_AFTER_DEFAULT_TUBERCULOSIS)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt8Months AND outOfControl");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 8 months
	 *  transfer out
	 *  @return cohort definition
	 */
	public CohortDefinition transferredOutExtraPulmonaryTbResultsAt8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("extraPulmonaryTbResultsAt8Months", ReportUtils.map(extraPulmonaryTbResultsAt8Months(15, 12), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("extraPulmonaryTbResultsAt8Months AND transferredOut");
		return cd;
	}

	/**
	 *patients registered 12 to 15 months earlier
	 *  results at 8 months
	 *  transfer out, out of control, died, treatment completed
	 *  @return cohort definition
	 */
	public CohortDefinition transferOutOutOfControlDiedCompletedTreatmentExtraPulmonaryTbResultsAt8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("treatmentCompleted", ReportUtils.map(treatmentCompleteExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("died", ReportUtils.map(diedExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("outOfControl", ReportUtils.map(outOfControlExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("transferredOut", ReportUtils.map(transferredOutExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("treatmentCompleted OR died OR outOfControl OR transferredOut");
		return cd;
	}
}