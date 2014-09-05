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
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.ObsInLastVisitCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.InCareHasAtLeast2VisitsCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of Quality Improvement cohorts for HIV care
 */
@Component
public class QiCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	ArtCohortLibrary artCohortLibrary;

	@Autowired
	Moh731CohortLibrary moh731CohortLibrary;

	@Autowired
	HivCohortLibrary hivCohortLibrary;

	public CohortDefinition hadNutritionalAssessmentAtLastVisit() {
		Concept weight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
		Concept height = Dictionary.getConcept(Dictionary.HEIGHT_CM);
		Concept muac = Dictionary.getConcept(Dictionary.MUAC);

		ObsInLastVisitCohortDefinition hadWeight = new ObsInLastVisitCohortDefinition();
		hadWeight.setName("patients with weight obs in last visit");
		hadWeight.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadWeight.setQuestion(weight);

		ObsInLastVisitCohortDefinition hadHeight = new ObsInLastVisitCohortDefinition();
		hadHeight.setName("patients with height obs in last visit");
		hadHeight.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadHeight.setQuestion(height);

		ObsInLastVisitCohortDefinition hadMuac = new ObsInLastVisitCohortDefinition();
		hadMuac.setName("patients with MUAC obs in last visit");
		hadMuac.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadMuac.setQuestion(muac);

		CompositionCohortDefinition cd =new CompositionCohortDefinition();
		hadMuac.setName("patients with nutritional assessment in last visit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("hadWeight", ReportUtils.map(hadWeight, "onOrBefore=${onOrBefore}"));
		cd.addSearch("hadHeight", ReportUtils.map(hadHeight, "onOrBefore=${onOrBefore}"));
		cd.addSearch("hadMuac", ReportUtils.map(hadMuac, "onOrBefore=${onOrBefore}"));
		cd.setCompositionString("(hadWeight AND hadHeight) OR hadMuac");
		return cd;
	}

	public CohortDefinition hivInfectedAndNotOnARTAndHasHivClinicalVisit() {
		EncounterType hivEnroll = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
		EncounterType hivConsult = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
		CompositionCohortDefinition cd =new CompositionCohortDefinition();
		cd.setName("Not on ART with at least one HIV clinical Visit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("atLeastOneHIVClinicalVisit", ReportUtils.map(commonCohorts.hasEncounter(hivEnroll, hivConsult), "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("eligibleForART", ReportUtils.map(artCohortLibrary.eligibleForArt(), "onDate=${onOrAfter-6m}"));
		cd.setCompositionString("atLeastOneHIVClinicalVisit AND eligibleForART");
		return cd;
	}

	/**
	 *
	 */
	public CohortDefinition inCareHasAtLeast2Visits() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new InCareHasAtLeast2VisitsCalculation());
		cd.setName("patients in care and have at least 2 visits 3 months a part");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return  cd;
	}

	/**
	 *
	 */
	public CohortDefinition clinicalVisit() {
		EncounterType hivEnroll = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
		EncounterType hivConsult = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in care and has a visit during 6 months review period");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inCare", ReportUtils.map(moh731CohortLibrary.currentlyInCare(), "onDate=${onOrBefore}"));
		cd.addSearch("atLeastOneHIVClinicalVisit", ReportUtils.map(commonCohorts.hasEncounter(hivEnroll, hivConsult), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inCare AND atLeastOneHIVClinicalVisit");
		return cd;
	}

	/**
	 * Patients with at least on viral load results during last 12 months duration
	 * @return CohortDefinition
	 */
	public CohortDefinition viralLoadResultsDuringLast12Months() {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setName("Viral Load results");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		cd.setTimeModifier(TimeModifier.ANY);
		return  cd;
	}

	/**
	 * Patients on ART for at least 12 months by the end of the review period
	 * Patients have at least one Viral Load (VL) results during the last 1 months
	 * @return cohort definition
	 */
	public CohortDefinition onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and have VL during the last 12 months");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		cd.addSearch("viralLoadResults", ReportUtils.map(viralLoadResultsDuringLast12Months(), "onOrAfter=${onOrBefore-13m},onOrBefore=${onOrBefore-12}"));
		cd.setCompositionString("onART12Months AND viralLoadResults");
		return cd;
	}

	/**
	 * Number of HIV infected patients on ART 12 months ago
	 * Have atleast one clinical visit during the six months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and have at least one clinical visit during the last 12 months");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		cd.addSearch("atLeastOneHIVClinicalVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("onART12Months AND atLeastOneHIVClinicalVisit");
		return cd;
	}

	/**
	 * Number of patients on ART for at least 12 months
	 * VL < 1000 copies
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast12MonthsAndVlLess1000() {
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

		//find the <1000 copies of recent obs
		NumericObsCohortDefinition cdVlLess1000 = new NumericObsCohortDefinition();
		cdVlLess1000.setName("less1000Copies");
		cdVlLess1000.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cdVlLess1000.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		cdVlLess1000.setOperator1(RangeComparator.LESS_THAN);
		cdVlLess1000.setValue1(1000.0);
		cdVlLess1000.setTimeModifier(TimeModifier.LAST);


		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.setName("onARTatLeast12MonthsAndVlLess1000");
		compositionCohortDefinition.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("vlLess1000", ReportUtils.map(cdVlLess1000, "onOrBefore=${onOrBefore}"));

		compositionCohortDefinition.setCompositionString("onART12Months AND vlLess1000");

		return compositionCohortDefinition;
	}

	/**
	 * Number of patients on ART for at least 12
	 * Have at least one VL results
	 */
	public CohortDefinition onARTatLeast12MonthsAndAtLeastVlResults() {
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

		//find the <1000 copies of recent obs
		NumericObsCohortDefinition atLeastVlResults = new NumericObsCohortDefinition();
		atLeastVlResults.setName("atLeastOneVlResults");
		atLeastVlResults.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		atLeastVlResults.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		atLeastVlResults.setTimeModifier(TimeModifier.ANY);

		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.setName("onARTatLeast12MonthsAndAtLeastVlResults");
		compositionCohortDefinition.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("atLeastOneVlResults", ReportUtils.map(atLeastVlResults, "onOrBefore=${onOrBefore}"));

		compositionCohortDefinition.setCompositionString("onART12Months AND atLeastOneVlResults");

		return compositionCohortDefinition;
	}

	/**
	 * Number of HIV infected patients currently NOT on ant TB treatment
	 * Patients have at least one HIV clinical visit during the 6 months review period
	 */
	public CohortDefinition hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months() {
		//those patients on tb treatment
		DateObsCohortDefinition onTbTreatment = new DateObsCohortDefinition();
		onTbTreatment.setName("On Tb Treatment");
		onTbTreatment.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		onTbTreatment.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		onTbTreatment.setQuestion(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE));
		onTbTreatment.setTimeModifier(TimeModifier.LAST);

		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		compositionCohortDefinition.addSearch("inHivProgram", ReportUtils.map(hivCohortLibrary.enrolled()));
		compositionCohortDefinition.addSearch("onTbTreatment", ReportUtils.map(onTbTreatment, "onOrAfter=${onOrBefore-6},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("hasHivClinicalVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrBefore-6},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.setCompositionString("inHivProgram AND hasHivClinicalVisit AND NOT onTbTreatment");
		return compositionCohortDefinition;
	}


}