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
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.tb.MissedLastTbAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.OnCPTCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbInitialTreatmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbTreatmentStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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

	@Autowired
	private ArtCohortLibrary artCohortLibrary;

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
		cd.addSearch("pulmonaryTbPatients", ReportUtils.map(pulmonaryTbPatients(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("smearNotDone", ReportUtils.map(pulmonaryTbSmearNotDone(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
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
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("ptbSmearNotDoneResultsAt2Months", ReportUtils.map(ptbSmearNotDoneResultsAtMonths(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("enrolled", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrAfter-"+ highMonths +"m},enrolledOnOrBefore=${onOrAfter-"+ leastMonths + "m}"));
		cd.addSearch("resultsAt2Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(2), "onDate=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAt2Months AND enrolled AND resultsAt2Months");
		return cd;
	}

	/**
	 * Patient who finalized their initial treatment
	 * return the cohort definition
	 */
	public CohortDefinition ptbSmearNotDoneResults2MonthsFinalizedInitialtreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
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
		cd.addSearch("enrolled", ReportUtils.map(enrolled(), "enrolledOnOrAfter=${onOrBefore-"+ highMonths +"m},enrolledOnOrBefore=${onOrBefore-"+ leastMonths + "m}"));
		cd.addSearch("resultsAt8Months", ReportUtils.map(startedTbTreatmentResultsAtMonths(8), "onDate=${onOrBefore}"));
		cd.setCompositionString("ptbSmearNotDoneResultsAtMonths AND enrolled AND resultsAt8Months");
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
	 * Patients on CPT
	 * @return cohort definition
	 */
	public CohortDefinition onCPT() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new OnCPTCalculation());
		cd.setName("patients cpt on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
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
		cd.addSearch("onCpt", ReportUtils.map(onCPT(), "onDate=${onOrBefore}"));
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
}