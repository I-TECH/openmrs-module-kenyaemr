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
import org.openmrs.Program;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.ObsInLastVisitCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.InCareHasAtLeast2VisitsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.PatientsWhoMeetCriteriaForNutritionalSupport;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * Library of Quality Improvement cohorts for HIV care adult
 */
@Component
public class QiCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	private ArtCohortLibrary artCohortLibrary;

	@Autowired
	private Moh731CohortLibrary moh731CohortLibrary;

	@Autowired
	private HivCohortLibrary hivCohortLibrary;

	@Autowired
	private PwpCohortLibrary pwpCohortLibrary;

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
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("(hadWeight AND hadHeight AND adult) OR hadMuac");
		return cd;
	}

	/**
	 *HIV infected patients NOT on ART and has hiv clinical visit
	 * @return CohortDefinition
	 */
	public CohortDefinition hivInfectedAndNotOnARTAndHasHivClinicalVisit() {
		CompositionCohortDefinition cd =new CompositionCohortDefinition();
		cd.setName("Not on ART with at least one HIV clinical Visit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("eligibleForART", ReportUtils.map(artCohortLibrary.eligibleForArt(), "onDate=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("hasVisits AND eligibleForART AND adult");
		return cd;
	}

	/**
	 * Patients in care and has at least 2 visits
	 */
	public CohortDefinition inCareHasAtLeast2Visits() {
		CalculationCohortDefinition hasAtLeast2VisitsWithin3Months = new CalculationCohortDefinition(new InCareHasAtLeast2VisitsCalculation());
		hasAtLeast2VisitsWithin3Months.setName("patients in care and have at least 2 visits 3 months a part");
		hasAtLeast2VisitsWithin3Months.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Adult and in care");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inCare", ReportUtils.map(moh731CohortLibrary.currentlyInCare(), "onDate=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("has2VisitsIn3Months", ReportUtils.map(hasAtLeast2VisitsWithin3Months, "onDate=${onOrBefore}"));
		cd.setCompositionString("inCare AND adult AND has2VisitsIn3Months");
		return  cd;
	}

	/**
	 *Patients with a=clinical visits
	 * @return CohortDefinition
	 */
	public CohortDefinition clinicalVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in care and has a visit during 6 months review period");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inCare", ReportUtils.map(moh731CohortLibrary.currentlyInCare(), "onDate=${onOrBefore}"));
		cd.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("inCare AND hasVisit AND adult");
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
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("onART12Months AND viralLoadResults AND adult");
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
		cd.addSearch("atLeastOneHIVClinicalVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("onART12Months AND atLeastOneHIVClinicalVisit AND adult");
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
		cdVlLess1000.setName("Less than 1000 Copies");
		cdVlLess1000.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cdVlLess1000.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		cdVlLess1000.setOperator1(RangeComparator.LESS_THAN);
		cdVlLess1000.setValue1(1000.0);
		cdVlLess1000.setTimeModifier(TimeModifier.LAST);


		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.setName("Number of patients on ART for at least 12 months and VL < 1000 copies");
		compositionCohortDefinition.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("vlLess1000", ReportUtils.map(cdVlLess1000, "onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));

		compositionCohortDefinition.setCompositionString("onART12Months AND vlLess1000 AND adult");

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
		atLeastVlResults.setName("At least one VL results");
		atLeastVlResults.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		atLeastVlResults.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		atLeastVlResults.setTimeModifier(TimeModifier.ANY);

		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.setName("Number of patients on ART for at least 12 and have one VL results");
		compositionCohortDefinition.setName("onARTatLeast12MonthsAndAtLeastVlResults");
		compositionCohortDefinition.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("atLeastOneVlResults", ReportUtils.map(atLeastVlResults, "onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));

		compositionCohortDefinition.setCompositionString("onART12Months AND atLeastOneVlResults AND adult");

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
		compositionCohortDefinition.setName("in HIV has clinic and NOT in TB");
		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		compositionCohortDefinition.addSearch("inHivProgram", ReportUtils.map(hivCohortLibrary.enrolled()));
		compositionCohortDefinition.addSearch("onTbTreatment", ReportUtils.map(onTbTreatment, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("hasHivVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		compositionCohortDefinition.setCompositionString("(inHivProgram AND hasHivVisit AND adult) AND NOT onTbTreatment");
		return compositionCohortDefinition;
	}

	/**
	 * Number of patients with negative TB screens
	 * Patients who  have NOt had IPT
	 */
	public CohortDefinition patientWithNegativeTbScreenWhoHaveNotHadIPT() {
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Screened for TB negative and NOT on IPT");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("negativeTB", ReportUtils.map(commonCohorts.hasObs(tbDiseaseStatus, noSignsOrSymptoms), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onIPT", ReportUtils.map(onINHProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("(negativeTB AND adult) AND NOT onIPT");
		return cd;
	}

	/**
	 * Patients who are on INH
	 * Isoniazid dispensed
	 * Isoniazid medication
	 * INH prophylaxis
	 * @return CohortDefinition
	 */
	public CohortDefinition onINHProphylaxis() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

		CodedObsCohortDefinition inhDispensed = new CodedObsCohortDefinition();
		inhDispensed.setName("isoniazid dispensed");
		inhDispensed.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		inhDispensed.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		inhDispensed.setTimeModifier(PatientSetService.TimeModifier.LAST);
		inhDispensed.setQuestion(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED));
		inhDispensed.setValueList(Arrays.asList(Dictionary.getConcept(Dictionary.YES)));
		inhDispensed.setOperator(SetComparator.IN);

		cd.setName("Formulation of INH");
		cd.addSearch("inhDispensed", ReportUtils.map(inhDispensed, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("inhMedication", ReportUtils.map(commonCohorts.medicationDispensed(Dictionary.getConcept(Dictionary.ISONIAZID)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("inhProphylaxis", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.PATIENT_REPORTED_CURRENT_TUBERCULOSIS_PROPHYLAXIS), Dictionary.getConcept(Dictionary.ISONIAZID_PROPHYLAXIS)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inhDispensed OR inhMedication OR inhProphylaxis");
		return cd;
	}

	/**
	 * Number of patients who have NOT had IPT within the last 2 years
	 * Patients who have negative tb screen at last clinic visit during the 6 months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition patientsWhoHaveHadNoIptWithinLast2YearsTbNegativeDuring6MonthsReviewPeriod() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);
		cd.setName("Patients with No IPT within 2 years and Tb Negative");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("negativeTB", ReportUtils.map(commonCohorts.hasObs(tbDiseaseStatus, noSignsOrSymptoms), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onIPT", ReportUtils.map(onINHProphylaxis(), "onOrAfter=${onOrBefore-24m},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("(negativeTB AND adult) AND NOT onIPT");
		return  cd;
	}

	/**
	 * Patients who meet criteria for nutritional support
	 * Patients who received nutritional support
	 * @return CohortDefinition
	 */
	public CohortDefinition patientsWhoMeetNutritionalSupportAtLastClinicVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients who meet nutritional assessment and received nutritional support");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("meetNutritionCriteria", ReportUtils.map(patientsWhoMeetCriteriaForNutritionalSupport(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("lastClinicVisit", ReportUtils.map(hadNutritionalAssessmentAtLastVisit(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("meetNutritionCriteria AND lastClinicVisit AND adult");
		return  cd;
	}

	/**
	 * Patients who meet criteria for nutritional support
	 * BMI < 18.5 in adult or
	 * MUAC < 23 cm
	 */
	public CohortDefinition patientsWhoMeetCriteriaForNutritionalSupport() {
		CalculationCohortDefinition cdMeetCriteria = new CalculationCohortDefinition(new PatientsWhoMeetCriteriaForNutritionalSupport());
		cdMeetCriteria.setName("Patients who meet criteria for nutritional support");
		cdMeetCriteria.addParameter(new Parameter("onDate", "onDate", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Meet nutritional criteria and adult");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("meetCriteria", ReportUtils.map(cdMeetCriteria, "onDate=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("meetCriteria AND adult");
		return cd;
	}

	/**
	 * patients who are HIV positive
	 * Partners having at least hiv known positive known status
	 * @return CohortDefinition
	 */
	public CohortDefinition hivPositivePatientsWhosePartnersAreHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv positive with a partner who is positive");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrAfter=${onOrAfter},enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("spousePartner", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.FAMILY_MEMBER), Dictionary.getConcept(Dictionary.PARTNER_OR_SPOUSE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivPositivePartner", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.SIGN_SYMPTOM_PRESENT), Dictionary.getConcept(Dictionary.YES)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND hivPositivePartner AND spousePartner AND adult");
		return  cd;

	}

	/**
	 *Hiv infected patients with at least one clinic visit during the six months review period
	 * Have at least one partner
	 * @return CohortDefinition
	 */
	public CohortDefinition hivPositivePatientsWithAtLeastOnePartner() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv positive with at least one partner");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("anyPartner", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.FAMILY_MEMBER), Dictionary.getConcept(Dictionary.PARTNER_OR_SPOUSE)), "onOrBefore=${onOrBefore}"));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND anyPartner AND hasVisits AND adult");
		return  cd;
	}

	/**
	 * patients who are HIV positive
	 * children having at least hiv known positive known status
	 * @return CohortDefinition
	 */
	public CohortDefinition hivPositivePatientsWhoseChildrenAreHivPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv infected and children Hiv Positive");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("hasChild", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.FAMILY_MEMBER), Dictionary.getConcept(Dictionary.CHILD)), "onOrBefore=${onOrBefore}"));
		cd.addSearch("hivPositiveChild", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.SIGN_SYMPTOM_PRESENT), Dictionary.getConcept(Dictionary.YES)), "onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND hivPositiveChild AND hasChild AND adult");
		return  cd;

	}

	/**
	 *Hiv infected patients with at least one clinic visit during the six months review period
	 * Have at least one child
	 * @return CohortDefinition
	 */
	public CohortDefinition hivPositivePatientsWithAtLeastOneChildOrMinor() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv positive with at least one child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("anyChild", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.FAMILY_MEMBER), Dictionary.getConcept(Dictionary.CHILD)), "onOrBefore=${onOrBefore}"));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND anyChild AND hasVisits AND adult");
		return  cd;
	}

	/**
	 * Patients of HIV-infected non pregnant women aged 15-49 years
	 * Patients on modern contraceptives during the review period
	 * @return CohortDefinition
	 */
	public CohortDefinition nonPregnantWomen15To49YearsOnModernContraceptives() {
		Concept notPregnant = Dictionary.getConcept(Dictionary.NO);
		Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Non pregnant 15 to 49 years on modern contraceptives");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("notPregnant", ReportUtils.map(commonCohorts.hasObs(pregnancyStatus, notPregnant), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("above15Years", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("below49Years", ReportUtils.map(commonCohorts.agedAtMost(49), "effectiveDate=${onOrBefore}"));
		cd.addSearch("onModernContraceptives", ReportUtils.map(pwpCohortLibrary.modernContraceptivesProvided(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND notPregnant AND above15Years AND below49Years AND onModernContraceptives");
		return cd;
	}

	/**
	 * Patients who are hiv positive 15 to 49 years
	 * have at least one hiv clinical visit during the 6 months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit() {
		Concept notPregnant = Dictionary.getConcept(Dictionary.NO);
		Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Non pregnant 15 to 49 years with at least one hiv clinical visit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("notPregnant", ReportUtils.map(commonCohorts.hasObs(pregnancyStatus, notPregnant), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("above15Years", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("below49Years", ReportUtils.map(commonCohorts.agedAtMost(49), "effectiveDate=${onOrBefore}"));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND notPregnant AND above15Years AND below49Years AND hasVisits");
		return cd;
	}

	/**
	 * Has cd4 results only adult
	 * @return CohortDefinition
	 */
	public CohortDefinition hasCD4ResultsAdult() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasCd4", ReportUtils.map(hivCohortLibrary.hasCd4Result(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("hasCd4 AND adult");
		return cd;
	}

	/**
	 * Has visits only adult
	 * @return CohortDefinition
	 */
	public CohortDefinition hasHivVisitAdult() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("hasVisits AND adult");
		return cd;
	}

}