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

package org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.PatientSetService;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnAlternateFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnOriginalFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnSecondLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PregnantAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TbPatientAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RegimenOrderCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Library of ART related cohort definitions
 */
@Component
public class ArtCohortLibrary {

	@Autowired
	private RegimenManager regimenManager;

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	private HivCohortLibrary hivCohortLibrary;

	/**
	 * Patients who are eligible for ART on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition eligibleForArt() {

		NumericObsCohortDefinition cd4Less500 = new NumericObsCohortDefinition();
		cd4Less500.setName("CDLessThan500");
		cd4Less500.setQuestion(Dictionary.getConcept(Dictionary.CD4_COUNT));
		cd4Less500.setOperator1(RangeComparator.LESS_THAN);
		cd4Less500.setValue1(500.0);
		cd4Less500.setTimeModifier(PatientSetService.TimeModifier.LAST);

		NumericObsCohortDefinition cd4Less350 = new NumericObsCohortDefinition();
		cd4Less350.setName("CDLessThan350");
		cd4Less350.setQuestion(Dictionary.getConcept(Dictionary.CD4_COUNT));
		cd4Less350.setOperator1(RangeComparator.LESS_THAN);
		cd4Less350.setValue1(350.0);
		cd4Less350.setTimeModifier(PatientSetService.TimeModifier.LAST);

		NumericObsCohortDefinition cdLess1000 = new NumericObsCohortDefinition();
		cdLess1000.setName("CDLessThan1000");
		cdLess1000.setQuestion(Dictionary.getConcept(Dictionary.CD4_COUNT));
		cdLess1000.setOperator1(RangeComparator.LESS_THAN);
		cdLess1000.setValue1(1000.0);
		cdLess1000.setTimeModifier(PatientSetService.TimeModifier.LAST);

		NumericObsCohortDefinition cd4PercentLess25 = new NumericObsCohortDefinition();
		cd4PercentLess25.setName("CDPercentLessThan25");
		cd4PercentLess25.setQuestion(Dictionary.getConcept(Dictionary.CD4_PERCENT));
		cd4PercentLess25.setOperator1(RangeComparator.LESS_THAN);
		cd4PercentLess25.setValue1(25.0);
		cd4PercentLess25.setTimeModifier(PatientSetService.TimeModifier.LAST);

		NumericObsCohortDefinition cd4PercentLess20 = new NumericObsCohortDefinition();
		cd4PercentLess20.setName("CDPercentLessThan20");
		cd4PercentLess20.setQuestion(Dictionary.getConcept(Dictionary.CD4_PERCENT));
		cd4PercentLess20.setOperator1(RangeComparator.LESS_THAN);
		cd4PercentLess20.setValue1(20.0);
		cd4PercentLess20.setTimeModifier(PatientSetService.TimeModifier.LAST);

		CodedObsCohortDefinition whoStage = new CodedObsCohortDefinition();
		whoStage.setQuestion(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE));
		whoStage.setOperator(SetComparator.IN);
		whoStage.setTimeModifier(PatientSetService.TimeModifier.LAST);

		Concept CURRENT_WHO_STAGE = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);
		Concept WHO_STAGE_3_ADULT = Dictionary.getConcept(Dictionary.WHO_STAGE_3_ADULT);
		Concept WHO_STAGE_4_ADULT = Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT);
		Concept WHO_STAGE_3_PEDS = Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS);
		Concept WHO_STAGE_4_PEDS = Dictionary.getConcept(Dictionary.WHO_STAGE_4_PEDS);

		CompositionCohortDefinition pediUnder24InProgram = new CompositionCohortDefinition();
		pediUnder24InProgram.setName("pediUnder24InProgram");
		pediUnder24InProgram.addParameter(new Parameter("onDate", "On Date", Date.class));
		pediUnder24InProgram.addSearch("over24Months", ReportUtils.map(commonCohorts.agedAtLeast(2), "effectiveDate=${onDate}"));
		pediUnder24InProgram.addSearch("inHIVProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onDate}"));
		pediUnder24InProgram.setCompositionString("inHIVProgram and NOT over24Months");

		CompositionCohortDefinition pedi2To5years = new CompositionCohortDefinition();
		pedi2To5years.setName("pedi2To5years");
		pedi2To5years.addParameter(new Parameter("onDate", "On Date", Date.class));
		pedi2To5years.addSearch("over24Months", ReportUtils.map(commonCohorts.agedAtLeast(2), "effectiveDate=${onDate}"));
		pedi2To5years.addSearch("over5Years", ReportUtils.map(commonCohorts.agedAtLeast(5), "effectiveDate=${onDate}"));
		pedi2To5years.addSearch("inHIVProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onDate}"));
		pedi2To5years.addSearch("whoStage", ReportUtils.map(commonCohorts.hasObs(CURRENT_WHO_STAGE,  WHO_STAGE_3_PEDS, WHO_STAGE_4_PEDS )));
		pedi2To5years.addSearch("cd4PercentLess25", ReportUtils.map(cd4PercentLess25));
		pedi2To5years.addSearch("cdLess1000", ReportUtils.map(cdLess1000));
		pedi2To5years.setCompositionString("((inHIVProgram AND(over24Months AND NOT over5Years)) AND (whoStage OR cd4PercentLess25 OR cdLess1000))");

		CompositionCohortDefinition pedi5To12years = new CompositionCohortDefinition();
		pedi5To12years.setName("pedi5To12years");
		pedi5To12years.addParameter(new Parameter("onDate", "On Date", Date.class));
		pedi5To12years.addSearch("under13Years", ReportUtils.map(commonCohorts.agedAtMost(12), "effectiveDate=${onDate}"));
		pedi5To12years.addSearch("under5Years", ReportUtils.map(commonCohorts.agedAtLeast(5), "effectiveDate=${onDate}"));
		pedi5To12years.addSearch("inHIVProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onDate}"));
		pedi5To12years.addSearch("whoStage", ReportUtils.map((commonCohorts.hasObs(CURRENT_WHO_STAGE, WHO_STAGE_3_PEDS, WHO_STAGE_4_PEDS )), "onOrBefore=${onDate}"));
		pedi5To12years.addSearch("cd4PercentLess20", ReportUtils.map(cd4PercentLess20));
		pedi5To12years.addSearch("cdLess500", ReportUtils.map(cd4Less500));
		pedi5To12years.setCompositionString("inHIVProgram AND (under13Years AND NOT under5Years) AND (whoStage OR cd4PercentLess20 OR cdLess500)");

		CompositionCohortDefinition over12years = new CompositionCohortDefinition();
		over12years.setName("over12years");
		over12years.addParameter(new Parameter("onDate", "On Date", Date.class));
		over12years.addSearch("under13Years", ReportUtils.map(commonCohorts.agedAtMost(12), "effectiveDate=${onDate}"));
		over12years.addSearch("inHIVProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onDate}"));
		over12years.addSearch("whoStage", ReportUtils.map((commonCohorts.hasObs(CURRENT_WHO_STAGE, WHO_STAGE_3_ADULT, WHO_STAGE_4_ADULT )), "onOrBefore=${onDate}"));
		over12years.addSearch("cd4Less350", ReportUtils.map(cd4Less350));
		over12years.setCompositionString("(inHIVProgram AND NOT under13Years) AND (whoStage OR cd4Less350)");

		CompositionCohortDefinition eligibleForART = new CompositionCohortDefinition();

		eligibleForART.setName("eligibleForART");
		eligibleForART.addParameter(new Parameter("onDate", "onDate", Date.class));
		eligibleForART.addSearch("pediUnder24InProgram", ReportUtils.map(pediUnder24InProgram, "onDate=${onDate}"));
		eligibleForART.addSearch("pedi2To5years", ReportUtils.map(pedi2To5years, "onDate=${onDate}"));
		eligibleForART.addSearch("pedi5To12years", ReportUtils.map(pedi5To12years, "onDate=${onDate}"));
		eligibleForART.addSearch("over12years", ReportUtils.map(over12years, "onDate=${onDate}"));
		eligibleForART.addSearch("onART", ReportUtils.map(onArt(), "onDate=${onDate}"));
		eligibleForART.addSearch("notLostToFollowUp",ReportUtils.map(commonCohorts.hasEncounter(), "onOrAfter=${onDate-90d}"));
		eligibleForART.setCompositionString("((pediUnder24InProgram OR pedi2To5years OR pedi5To12years OR over12years) AND notLostToFollowUp) AND NOT onART");

		return eligibleForART;
	}

	/**
	 * Patients who are on ART on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onArt() {
		SqlCohortDefinition cd = new SqlCohortDefinition("select distinct(patient_id) from orders where concept_id in (select concept_id from concept_set where concept_set =" + Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS).getConceptId() + ") and (discontinued_date is null or discontinued_date > :onDate) and start_date < :onDate and (auto_expire_date is null or auto_expire_date > :onDate) and voided = 0");
		cd.setName("on ART on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who missed appointments on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition missedAppointments() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new MissedLastAppointmentCalculation());
		cd.setName("missed appointment on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Patients who are on art and  missed appointments on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition onArtAndMissedAppointments() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and Missed last appointment");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("onArt", ReportUtils.map(onArt(), "onDate=${onDate}"));
		cd.addSearch("missedAppointments", ReportUtils.map(missedAppointments(), "onDate=${onDate}"));
		cd.setCompositionString("onArt AND NOT missedAppointments");
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
		cd.addSearch("onArt", ReportUtils.map(onArtAndMissedAppointments(), "onDate=${onDate}"));
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
		cd.addSearch("onArt", ReportUtils.map(onArtAndMissedAppointments(), "onDate=${onDate}"));
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
	 * Patients on the given regimen. In the future this should look at dispensing records during the reporting period
	 * which implicitly check whether a patient is active. As a workaround until we get to dispensing records, we
	 * explicitly check whether a patient is active here by looking for recent encounters.
	 *
	 * @return the cohort definition
	 */
	public CohortDefinition onRegimen(List<Concept> drugConcepts) {
		RegimenOrderCohortDefinition regCd = new RegimenOrderCohortDefinition();
		Set<Concept> drugConceptSet = new HashSet<Concept>(drugConcepts);
		regCd.setName("ART regimen");
		regCd.addParameter(new Parameter("onDate", "On Date", Date.class));
		regCd.setMasterConceptSet(regimenManager.getMasterSetConcept("ARV"));
		regCd.setConceptSet(drugConceptSet);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Has an encounter in last 3 months and on regimen");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("onRegimen", ReportUtils.map(regCd, "onDate=${onDate}"));
		cd.addSearch("hasEncounterInLast3Months", ReportUtils.map(commonCohorts.hasEncounter(), "onOrBefore=${onDate},onOrAfter=${onDate-90d}"));
		cd.setCompositionString("onRegimen AND hasEncounterInLast3Months");
		return cd;
	}

	/**
	 * Patients who are in HIV Program and on a given regimen
	 * @return the cohort definition
	 */
	public CohortDefinition inHivProgramAndOnRegimen(List<Concept> drugConcepts) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("In Hiv program and on regimen");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.inProgram(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "onDate=${onDate}"));
		cd.addSearch("onRegimen", ReportUtils.map(onRegimen(drugConcepts), "onDate=${onDate}"));
		cd.setCompositionString("inHivProgram AND onRegimen");
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
	 * Patients who started ART between ${onOrAfter} and ${onOrBefore} excluding transfer ins
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtExcludingTransferins() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Started ART excluding transfer ins");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", ReportUtils.map(startedArt(), "onOrBefore=${onOrAfter}"));
		cd.addSearch("transferIns", ReportUtils.map(commonCohorts.transferredIn(), "onOrBefore=${onOrAfter}"));
		cd.setCompositionString("startedArt AND NOT transferIns");
		return cd;
	}

	/**
	 * Patients who started ART on ${onOrBefore} excluding transfer ins
	 * @return the cohort definition
	 */
	public CohortDefinition startedArtExcludingTransferinsOnDate() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Started ART excluding transfer ins on date in this facility");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("startedArt", ReportUtils.map(startedArt(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("transferIns", ReportUtils.map(hivCohortLibrary.startedArtFromTransferringFacilityOnDate(), "onOrBefore=${onOrBefore}"));
		cd.setCompositionString("startedArt AND NOT transferIns");
		return  cd;
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
}