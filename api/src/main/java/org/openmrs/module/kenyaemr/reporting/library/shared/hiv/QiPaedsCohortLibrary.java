package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.Concept;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.ObsInLastVisitCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.InCareHasAtLeast2VisitsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.PatientsWhoMeetCriteriaForNutritionalSupport;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
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
public class QiPaedsCohortLibrary {

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

	@Autowired
	private QiCohortLibrary qiCohortLibrary;

	/**
	 * Patients in care and has at least 2 visits
	 */
	public CohortDefinition inCareHasAtLeast2Visits() {
		CalculationCohortDefinition cdInCareHasAtLeast2Visits = new CalculationCohortDefinition(new InCareHasAtLeast2VisitsCalculation());
		cdInCareHasAtLeast2Visits.setName("patients in care and have at least 2 visits 3 months a part");
		cdInCareHasAtLeast2Visits.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("child and in care");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inCare", ReportUtils.map(cdInCareHasAtLeast2Visits, "onDate=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("inCare AND child");
		return  cd;
	}

	/**
	 *Patients with a=clinical visits
	 * @return CohortDefinition
	 */
	public CohortDefinition clinicalVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in care and has a visit during 6 months review period - child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inCare", ReportUtils.map(moh731CohortLibrary.currentlyInCare(), "onDate=${onOrBefore}"));
		cd.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("inCare AND hasVisit AND child");
		return cd;
	}

	/**
	 *HIV infected patients NOT on ART and has hiv clinical visit
	 * @return CohortDefinition
	 */
	public CohortDefinition hivInfectedAndNotOnARTAndHasHivClinicalVisit() {
		CompositionCohortDefinition cd =new CompositionCohortDefinition();
		cd.setName("Not on ART with at least one HIV clinical Visit - child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("eligibleForART", ReportUtils.map(artCohortLibrary.eligibleForArt(), "onDate=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(hasVisits AND eligibleForART AND child) AND NOT onART");
		return cd;
	}

	/**
	 * Patients on ART for at least 12 months by the end of the review period
	 * Patients have at least one Viral Load (VL) results during the last 1 months
	 * @return cohort definition
	 */
	public CohortDefinition onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and have VL during the last 12 months - Child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		cd.addSearch("viralLoadResults", ReportUtils.map(qiCohortLibrary.viralLoadResultsDuringLast12Months(), "onOrAfter=${onOrBefore-13m},onOrBefore=${onOrBefore-12}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("onART12Months AND viralLoadResults AND child");
		return cd;
	}

	/**
	 * Number of HIV infected patients on ART 12 months ago
	 * Have atleast one clinical visit during the six months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART and have at least one clinical visit during the last 12 months - Child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onART12Months", ReportUtils.map(artCohortLibrary.netCohortMonths(12), "onDate=${onOrBefore}"));
		cd.addSearch("atLeastOneHIVClinicalVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("onART12Months AND atLeastOneHIVClinicalVisit AND child");
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
		cdVlLess1000.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cdVlLess1000.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		cdVlLess1000.setOperator1(RangeComparator.LESS_THAN);
		cdVlLess1000.setValue1(1000.0);
		cdVlLess1000.setTimeModifier(PatientSetService.TimeModifier.LAST);


		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.setName("Number of patients on ART for at least 12 months and VL < 1000 copies Child");
		compositionCohortDefinition.setName("onARTatLeast12MonthsAndVlLess1000");
		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		compositionCohortDefinition.addSearch("onARTatLeast6Months", ReportUtils.map(artCohortLibrary.netCohortMonths(6), "onDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("vlLess1000", ReportUtils.map(cdVlLess1000, "onOrAfter=${onOrBefore-12},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));

		compositionCohortDefinition.setCompositionString("onARTatLeast6Months AND vlLess1000 AND child");

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
		atLeastVlResults.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		atLeastVlResults.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		atLeastVlResults.setTimeModifier(PatientSetService.TimeModifier.ANY);

		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		compositionCohortDefinition.setName("Number of patients on ART for at least 12 and have one VL results - Child");
		compositionCohortDefinition.setName("onARTatLeast12MonthsAndAtLeastVlResults");
		compositionCohortDefinition.addSearch("onART6Months", ReportUtils.map(artCohortLibrary.netCohortMonths(6), "onDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("atLeastOneVlResults", ReportUtils.map(atLeastVlResults, "onOrAfter=${onOrBefore-12m},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));

		compositionCohortDefinition.setCompositionString("onART6Months AND atLeastOneVlResults AND child");

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
		onTbTreatment.setTimeModifier(PatientSetService.TimeModifier.LAST);

		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setName("in HIV has clinic and NOT in TB");
		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		compositionCohortDefinition.addSearch("inHivProgram", ReportUtils.map(hivCohortLibrary.enrolled()));
		compositionCohortDefinition.addSearch("onTbTreatment", ReportUtils.map(onTbTreatment, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("hasHivVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		compositionCohortDefinition.setCompositionString("(inHivProgram AND hasHivVisit AND child) AND NOT onTbTreatment");
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
		cd.addSearch("onIPT", ReportUtils.map(qiCohortLibrary.onINHProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("(negativeTB AND child) AND NOT onIPT");
		return cd;
	}

	/**
	 * Number of patients who have NOT had IPT within the last 2 years
	 * Patients who have negative tb screen at last clinic visit during the 6 months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition patientsWhoHaveHadNoIptWithinLast2YearsTbNegativeDuring6MonthsReviewPeriod() {

		CodedObsCohortDefinition coded = new CodedObsCohortDefinition();
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);
		coded.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		coded.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		coded.setName("Patients with no signs of TB");
		coded.setQuestion(tbDiseaseStatus);
		coded.setOperator(SetComparator.IN);
		coded.setTimeModifier(PatientSetService.TimeModifier.LAST);
		coded.setValueList(Arrays.asList(noSignsOrSymptoms));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();

		cd.setName("Patients with No IPT within 2 years and Tb Negative");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("negativeTB", ReportUtils.map(coded, "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("onIPT", ReportUtils.map(qiCohortLibrary.onINHProphylaxis(), "onOrAfter=${onOrBefore-24m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("(negativeTB AND child) AND NOT onIPT");
		return  cd;
	}

	public CohortDefinition hadNutritionalAssessmentAtLastVisit() {
		Concept weight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
		Concept height = Dictionary.getConcept(Dictionary.HEIGHT_CM);
		Concept muac = Dictionary.getConcept(Dictionary.MUAC);

		ObsInLastVisitCohortDefinition hadWeight = new ObsInLastVisitCohortDefinition();
		hadWeight.setName("patients with weight obs in last visit Child");
		hadWeight.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadWeight.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		hadWeight.setQuestion(weight);

		ObsInLastVisitCohortDefinition hadHeight = new ObsInLastVisitCohortDefinition();
		hadHeight.setName("patients with height obs in last visit Child");
		hadHeight.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadHeight.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		hadHeight.setQuestion(height);

		ObsInLastVisitCohortDefinition hadMuac = new ObsInLastVisitCohortDefinition();
		hadMuac.setName("patients with MUAC obs in last visit Child");
		hadMuac.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		hadMuac.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		hadMuac.setQuestion(muac);

		CompositionCohortDefinition cd =new CompositionCohortDefinition();
		hadMuac.setName("patients with nutritional assessment in last visit Child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

		cd.addSearch("hadWeight", ReportUtils.map(hadWeight, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hadHeight", ReportUtils.map(hadHeight, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hadMuac", ReportUtils.map(hadMuac, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("(hadWeight AND hadHeight AND child) OR hadMuac");
		return cd;
	}

	/**
	 * Patients who meet criteria for nutritional support
	 * BMI < 18.5 in adult or
	 * MUAC < 23 cm
	 */
	public CohortDefinition patientsWhoMeetCriteriaForNutritionalSupport() {
		CalculationCohortDefinition cdMeetCriteria = new CalculationCohortDefinition(new PatientsWhoMeetCriteriaForNutritionalSupport());
		cdMeetCriteria.setName("Patients who meet criteria for nutritional support child");
		cdMeetCriteria.addParameter(new Parameter("onDate", "onDate", Date.class));
		//there should be an additional criteria for filtering those who were given nutrition
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Meet nutritional criteria and child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("meetCriteria", ReportUtils.map(cdMeetCriteria, "onDate=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("meetCriteria AND child");
		return cd;
	}

	/**
	 * Patients who meet criteria for nutritional support
	 * Patients who received nutritional support
	 * @return CohortDefinition
	 */
	public CohortDefinition patientsWhoMeetNutritionalSupportAtLastClinicVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients who meet nutritional assessment and received nutritional support Child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("meetNutritionCriteria", ReportUtils.map(patientsWhoMeetCriteriaForNutritionalSupport(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("lastClinicVisit", ReportUtils.map(hadNutritionalAssessmentAtLastVisit(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("meetNutritionCriteria AND lastClinicVisit AND child");
		return  cd;
	}

	/**
	 * Number of HIV infected children aged 8 to 14 years
	 * Whose status is disclosed to them
	 * @return CohortDefinition
	 */
	public CohortDefinition childrenInfected8to14YearsWhoseStatusDisclosedToThem() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hivInfected", ReportUtils.map(infectedWithHiv(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("disclosed", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.HIV_STATUS_DISCLOSED_TO_OTHER_FAMILY_MEMBERS), Dictionary.getConcept(Dictionary.YES)), "onOrBefore=${onOrBefore}"));
		cd.addSearch("childMore8Years", ReportUtils.map(commonCohorts.agedAtLeast(8), "effectiveDate=${onOrBefore}"));
		cd.addSearch("childLess15", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("hivInfected AND disclosed AND childMore8Years AND childLess15");
		return cd;
	}

	/**
	 * Number of children infected with hiv
	 * @return CohortDefinition
	 */
	public CohortDefinition infectedWithHiv() {
		Concept dnaPcr1 = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept dnaPcr2 = Context.getConceptService().getConceptByUuid("1030AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		Concept antiBody1 = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
		Concept antiBody2 = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_2_QUALITATIVE);
		
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept detected = Dictionary.getConcept(Dictionary.DETECTED);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Children Infected with HIV");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("dnaPcr1", ReportUtils.map(commonCohorts.hasObs(dnaPcr1, detected), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("dnaPcr2", ReportUtils.map(commonCohorts.hasObs(dnaPcr2, positive), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("antiBody1", ReportUtils.map(commonCohorts.hasObs(antiBody1, positive), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("antiBody2", ReportUtils.map(commonCohorts.hasObs(antiBody2, positive), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("dnaPcr1 OR dnaPcr2 OR antiBody1 OR antiBody2");
		return cd;
	}

	/**
	 * Number of HIV infected children aged 8 to 14 years
	 * Who are enrolled into care with at least one HIV Clinical visit during 6 months review
	 * @return CohortDefinition
	 */
	public CohortDefinition childrenInfected8to14YearsEnrolledInCareWithAtLeastOneHivVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inHivProgram", ReportUtils.map(hivCohortLibrary.enrolled(), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("childMore4", ReportUtils.map(commonCohorts.agedAtLeast(8), "effectiveDate=${onOrBefore}"));
		cd.addSearch("childLess15", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfected", ReportUtils.map(infectedWithHiv(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND hasVisit AND childMore4 AND childLess15");
		return cd;
	}

	/**
	 * Has cd4 results only paeds
	 * @return CohortDefinition
	 */
	public CohortDefinition hasCD4ResultsPaeds() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasCd4", ReportUtils.map(hivCohortLibrary.hasCd4Result(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("hasCd4 AND child AND hasVisits");
		return cd;
	}

	/**
	 * Has visits only paeds
	 * @return CohortDefinition
	 */
	public CohortDefinition hasHivVisitPaeds() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("hasVisits AND child");
		return cd;
	}
}
