/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.EligibleForArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.EligibleForArtExclusiveCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnAlternateFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnOriginalFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnSecondLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PregnantAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TbPatientAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferredInAfterArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RegimenOrderCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.QiCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.QiPaedsCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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

	@Autowired
	private QiCohortLibrary qiCohortLibrary;

	@Autowired
	private QiPaedsCohortLibrary qiPaedsCohortLibrary;

	/**
	 * Patients who are eligible for ART on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition eligibleForArt() {

		CalculationCohortDefinition eligibleForART = new CalculationCohortDefinition(new EligibleForArtCalculation());
		eligibleForART.setName("eligible for ART on date");
		eligibleForART.addParameter(new Parameter("onDate", "On Date", Date.class));
		return eligibleForART;
	}

	public CohortDefinition EligibleForArtExclusive() {
		CalculationCohortDefinition eligibleForARTExclusive = new CalculationCohortDefinition(new EligibleForArtExclusiveCalculation());
		eligibleForARTExclusive.setName("eligible for ART on date exclusively");
		eligibleForARTExclusive.addParameter(new Parameter("onDate", "On Date", Date.class));
		return eligibleForARTExclusive;
	}

	/**
	 * Patients who are LTFU
	 * @return the cohort definition
	 */
	public CohortDefinition lostToFollowUpPatients() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new LostToFollowUpCalculation());
		cd.setName("lost to follow on date");
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
	 * Patients who are in the "month net cohort" on ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition netCohortMonths(int months) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in " + months + " month net cohort on date");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("startedArtMonthsAgo", ReportUtils.map(startedArt(), "onOrAfter=${onDate-"+ (months + 1) + "m},onOrBefore=${onDate-" + months + "m}"));
		cd.addSearch("transferredOut", ReportUtils.map(commonCohorts.transferredOut(), "onOrAfter=${onDate-" + (months + 1) + "m}"));
		cd.setCompositionString("startedArtMonthsAgo AND NOT transferredOut");
		return cd;
	}

	/**
	 * Patients who are in the "month net cohort" on ${onDate}
	 * Patients who started art between dates given months
	 * Used for art cohort analysis
	 * @return the cohort definition
	 */
	public CohortDefinition netCohortMonthsBetweenDatesGivenMonths(Integer period) {
		CalculationCohortDefinition calc = new CalculationCohortDefinition(new TransferredInAfterArtStartCalculation());
		calc.setName("Patients who transferred in while started art");
		calc.addCalculationParameter("outcomePeriod", period);
		calc.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("month net cohort on date given months");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addSearch("startedArtMonthsAgo", ReportUtils.map(startedArt(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
		cd.addSearch("transferInWhileOnArt", ReportUtils.map(calc));
		cd.setCompositionString("startedArtMonthsAgo AND NOT transferInWhileOnArt");
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
	 * Patients who are eligible and started art during 6 months review period adults
	 * @return CohortDefinition
	 */
	public CohortDefinition eligibleAndStartedARTAdult() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Eligible and started ART");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("eligible", ReportUtils.map(EligibleForArtExclusive(), "onDate=${onOrBefore}"));
		cd.addSearch("startART", ReportUtils.map(startedArt(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("eligible AND startART AND adult AND NOT deceased");
		return  cd;
	}

	/**
	 * Intersection of eligibleAndStartedARTAdult and hivInfectedAndNotOnARTAndHasHivClinicalVisit
	 * @return CohortDefinition
	 */
	public CohortDefinition eligibleAndStartedARTAndHivInfectedAndNotOnARTAndHasHivClinicalVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("eligibleAndStartedART", ReportUtils.map(eligibleAndStartedARTAdult(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfectedAndNotOnART", ReportUtils.map(qiCohortLibrary.hivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("eligibleAndStartedART AND hivInfectedAndNotOnART AND NOT deceased");
		return cd;
	}

	/**
	 * Patients who are eligible and started art during 6 months review period children
	 * @return CohortDefinition
	 */
	public CohortDefinition eligibleAndStartedARTPeds() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Eligible and started ART");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("eligible", ReportUtils.map(EligibleForArtExclusive(), "onDate=${onOrBefore}"));
		cd.addSearch("startART", ReportUtils.map(startedArt(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("eligible and startART and child");
		return  cd;
	}

	/**
	 * Intersection of eligibleAndStartedARTPeds and hivInfectedAndNotOnARTAndHasHivClinicalVisit
	 * @return CohortDefinition
	 */
	public CohortDefinition eligibleAndStartedARTPedsAndhivInfectedAndNotOnARTAndHasHivClinicalVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("eligibleAndStartedARTPeds", ReportUtils.map(eligibleAndStartedARTPeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfectedAndNotOnARTAndHasHivClinicalVisit", ReportUtils.map(qiPaedsCohortLibrary.hivInfectedAndNotOnARTAndHasHivClinicalVisit(),  "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("eligibleAndStartedARTPeds AND hivInfectedAndNotOnARTAndHasHivClinicalVisit");
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

	public  CohortDefinition patientsOnRegimen(String regimenName) {
		String sqlQuery="SELECT d.patient_id\n" +
		"from kenyaemr_etl.etl_drug_event d\n" +
				"  inner join (\n" +
				"select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
				"    max(fup.visit_date) as latest_vis_date,\n" +
				"    mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
				"    max(d.visit_date) as date_discontinued,\n" +
				"    d.patient_id as disc_patient,\n" +
				"  de.patient_id as started_on_drugs,\n" +
				"  de.program as hiv_program\n" +
				"from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
				"join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
				"join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
				"left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and date(date_started) <= date(:endDate)\n" +
				"left outer JOIN\n" +
				"(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
				"where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
				"group by patient_id\n" +
				") d on d.patient_id = fup.patient_id\n" +
				"where de.program = 'HIV' and fup.visit_date <= date(:endDate)\n" +
				"group by patient_id\n" +
				"having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
				"(date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
				"(((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
				"    ) onArt on d.patient_id=onArt.patient_id\n" +
				" where d.program=\"HIV\" and (d.voided is null or d.voided=0) and d.regimen_name=':regimenName'\n" +
				"      and d.date_started <= date(:endDate) and (d.date_discontinued is null or d.date_discontinued > date(:endDate));  ";

		sqlQuery = sqlQuery.replaceAll(":regimenName", regimenName);
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsOnRegimen");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients on a particular Regimen");
		return cd;
	}
}