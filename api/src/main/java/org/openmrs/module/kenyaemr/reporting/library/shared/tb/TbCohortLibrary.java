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
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.InProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.MissedAppointmentsOrDefaultedCalculation;
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
		return commonCohorts.enrolled(MetadataUtils.getProgram(TbMetadata._Program.TB));
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
	 * TB patients who missed appointments between ${onDate}
	 * @return the cohort definition
	 */
	public CohortDefinition missedAppointmentOrDefaulted() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new MissedAppointmentsOrDefaultedCalculation());
		cd.setName("Patients who missed and defaulted appointments");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * patients in TB and HIV and on CPT between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition inTbAndHivProgramsAndOnCPT() {
		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);
		Program tbProgram = MetadataUtils.getProgram(TbMetadata._Program.TB);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		Concept[] drugs = { Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM) };
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.inProgram(hivProgram), "onDate=${onDate}"));
		cd.addSearch("inTbProgram", ReportUtils.map(commonCohorts.inProgram(tbProgram), "onDate=${onDate}"));
		cd.addSearch("onCPTMedication", ReportUtils.map(commonCohorts.onMedication(drugs), "onDate=${onDate}"));
		cd.setCompositionString("inHivProgram AND inTbProgram AND onCPTMedication");
		return cd;
	}
}