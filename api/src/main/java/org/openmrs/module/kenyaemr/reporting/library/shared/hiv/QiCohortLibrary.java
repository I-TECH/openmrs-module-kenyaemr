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
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.ObsInLastVisitCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.InCareHasAtLeast2VisitsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.PatientsWhoMeetCriteriaForNutritionalSupport;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.DiedInMonthOneOfReviewCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.HavingAtLeastOneVisitInEachQuoterCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.InCareInMonths4To6During6MonthsReviewPeriodCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.PatientsWithVLResultsAtLeastMonthAgoCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.PatientsWithVLResultsLessThanXValueCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
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

	@Autowired
	private TbCohortLibrary tbCohortLibrary;

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
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("((hadWeight AND hadHeight AND adult) OR hadMuac) AND NOT deceased");
		return cd;
	}

	/**
	 * intersection of hadNutritionalAssessmentAtLastVisit and hasHivVisitAdult
	 * @return CohortDefinition
	 */
	public CohortDefinition hadNutritionalAssessmentAtLastVisitAndhasHivVisitAdult() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hadNutritionalAssessmentAtLastVisit", ReportUtils.map(hadNutritionalAssessmentAtLastVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasHivVisitAdult", ReportUtils.map(hasHivVisitAdult(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hadNutritionalAssessmentAtLastVisit AND hasHivVisitAdult AND NOT deceased");
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
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("atLeastOneEligibilityCriteria", ReportUtils.map(artCohortLibrary.EligibleForArtExclusive(), "onDate=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore-6m}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(hasVisits AND atLeastOneEligibilityCriteria AND adult) AND NOT (onART OR deceased)");
		return cd;
	}

	/**
	 * Patients in care and has at least 2 visits
	 */
	public CohortDefinition inCareHasAtLeast2Visits() {
		CalculationCohortDefinition hasAtLeast2VisitsWithin3Months = new CalculationCohortDefinition(new InCareHasAtLeast2VisitsCalculation());
		hasAtLeast2VisitsWithin3Months.setName("patients in care and have at least 2 visits 3 months a part");
		hasAtLeast2VisitsWithin3Months.addParameter(new Parameter("onDate", "On Date", Date.class));

		CalculationCohortDefinition cdHasAtLeast1VisitInEveryQuoter = new CalculationCohortDefinition(new HavingAtLeastOneVisitInEachQuoterCalculation());
		cdHasAtLeast1VisitInEveryQuoter.setName("patients who have at least one visit in every quoter");
		cdHasAtLeast1VisitInEveryQuoter.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Adult and in care");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("has2VisitsIn3Months", ReportUtils.map(hasAtLeast2VisitsWithin3Months, "onDate=${onOrBefore}"));
		cd.addSearch("atLeast1VisitInEachQuoter", ReportUtils.map(cdHasAtLeast1VisitInEveryQuoter, "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("adult AND (has2VisitsIn3Months OR atLeast1VisitInEachQuoter) AND NOT deceased");
		return  cd;
	}

	/**
	 *Patients with a clinical visits
	 * @return CohortDefinition
	 */
	public CohortDefinition clinicalVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("in care and has a visit during 6 months review period");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inCare", ReportUtils.map(moh731CohortLibrary.currentlyInCare(), "onDate=${onOrBefore}"));
		cd.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("enrolledIn4To6MonthOfReviewPeriod", ReportUtils.map(enrolledIn4To6MonthOfReviewPeriod(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("inCare AND hasVisit AND adult AND NOT (enrolledIn4To6MonthOfReviewPeriod OR deceased)");
		return cd;
	}

	/**
	 * patients who dont meet the criteria of clinical visit
	 * @return CohortDefinition
	 */
	public CohortDefinition complimentClinicalVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("compliment - clinical visits");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("patientsInCareAndHasAtLeast2Visits", ReportUtils.map(patientsInCareAndHasAtLeast2Visits(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("clinicalVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("clinicalVisit AND NOT (patientsInCareAndHasAtLeast2Visits OR  deceased)");
		return cd;
	}

	/**
	 * Intersection of inCareHasAtLeast2Visits and clinicalVisit
	 * @return cohort definition
	 */
	public CohortDefinition patientsInCareAndHasAtLeast2Visits() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Intersection of inCareHasAtLeast2Visits and clinicalVisit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inCareHasAtLeast2Visits", ReportUtils.map(inCareHasAtLeast2Visits(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("clinicalVisit", ReportUtils.map(clinicalVisit(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(inCareHasAtLeast2Visits AND clinicalVisit) AND NOT deceased");
		return cd;

	}

	/**
	 * Patients who are newly enrolled in 4th to 6th month
	 * @return CohortDefinition
	 */
	 public CohortDefinition enrolledIn4To6MonthOfReviewPeriod() {

		 CalculationCohortDefinition enrolled4To6Months = new CalculationCohortDefinition(new InCareInMonths4To6During6MonthsReviewPeriodCalculation());
		 enrolled4To6Months.setName("Patients in care at the 4th and 6th month during review");
		 enrolled4To6Months.addParameter(new Parameter("onDate", "On Date", Date.class));

		 CalculationCohortDefinition died = new CalculationCohortDefinition(new DiedInMonthOneOfReviewCalculation());
		 died.setName("Patients who died in the first month");
		 died.addParameter(new Parameter("onDate", "On Date", Date.class));

		 Concept REASON_FOR_PROGRAM_DISCONTINUATION = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		 Concept TRANSFERRED_OUT = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);

		 CompositionCohortDefinition comp = new CompositionCohortDefinition();
		 comp.setName("Enrolled on 4th to 6th months, transferred and died in month 1");
		 comp.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		 comp.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		 comp.addSearch("enrolled4To6Months", ReportUtils.map(enrolled4To6Months, "onDate=${onOrBefore}"));
		 comp.addSearch("transfer", ReportUtils.map(commonCohorts.hasObs(REASON_FOR_PROGRAM_DISCONTINUATION, TRANSFERRED_OUT), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		 comp.addSearch("diedInMonth1", ReportUtils.map(died, "onDate=${onOrBefore}"));
		 comp.setCompositionString("enrolled4To6Months OR transfer OR diedInMonth1");
		 return comp;
	 }

	/**
	 * Patients with at least on viral load results during last 12 months duration
	 * @return CohortDefinition
	 */
	public CohortDefinition viralLoadResultsDuringLast12Months(int months) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new PatientsWithVLResultsAtLeastMonthAgoCalculation());
		cd.setName("Patients With VL results within 12 months");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("months", months);
		return  cd;
	}

	/**
	 * Patients on ART for at least 12 months by the end of the review period
	 * Patients have at least one Viral Load (VL) results during the last 12 months
	 * @return cohort definition
	 */
	public CohortDefinition onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART at least 12 months and have VL during the last 12 months");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("onARTForAtLeast12Months", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore-12m}"));
		cd.addSearch("viralLoadResults", ReportUtils.map(viralLoadResultsDuringLast12Months(12), "onDate=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTForAtLeast12Months AND viralLoadResults AND adult AND NOT deceased");
		return cd;
	}

	/**
	 * Intersection of onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months and onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast12MonthAndHaveAtLeastOneVisitDuringTheLast6MonthsReview() {

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTatLeast12Month", ReportUtils.map(onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("onARTatLeast12MonthsAndHaveAtLeastOneVisit", ReportUtils.map(onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTatLeast12Month AND onARTatLeast12MonthsAndHaveAtLeastOneVisit AND NOT deceased");

		return cd;

	}

	/**
	 * This aggregate other indicators to be used in others as intersections
	 */
	public CohortDefinition hivMonitoringViralLoadNumAndDen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients with the viral load");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months", ReportUtils.map(onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview", ReportUtils.map(onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months AND onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview AND NOT deceased");
		return cd;
	}

	/**
	 * Number of HIV infected patients on ART 12 months ago
	 * Have atleast one clinical visit during the six months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART for atleast 12 months and have at least one clinical visit during the last 6 months");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTForAtLeast12Months", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore-12m}"));
		cd.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTForAtLeast12Months AND hasVisit AND adult AND NOT deceased");
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
		CalculationCohortDefinition cdVlLess1000 = new CalculationCohortDefinition( new PatientsWithVLResultsLessThanXValueCalculation());
		cdVlLess1000.setName("VL Less than 1000 Copies");
		cdVlLess1000.addParameter(new Parameter("onDate", "On Date", Date.class));
		cdVlLess1000.addCalculationParameter("months", 12);
		cdVlLess1000.addCalculationParameter("threshold", 1000.0);


		compositionCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		compositionCohortDefinition.setName("Number of patients on ART for at least 12 months and VL < 1000 copies");
		compositionCohortDefinition.addSearch("onARTForAtLeast12MonthsAdultAndHasVl", ReportUtils.map(onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months(), "onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("vlLess1000", ReportUtils.map(cdVlLess1000, "onDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));

		compositionCohortDefinition.setCompositionString("onARTForAtLeast12MonthsAdultAndHasVl AND vlLess1000 AND NOT deceased");

		return compositionCohortDefinition;
	}

	/**
	 * intersection of onARTatLeast12MonthsAndVlLess1000 and hivMonitoringViralLoadNumAndDen
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast12MonthsAndVlLess1000AndHivMonitoringViralLoadNumAndDen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTatLeast12MonthsAndVlLess1000", ReportUtils.map(onARTatLeast12MonthsAndVlLess1000(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("hivMonitoringViralLoadNumAndDen", ReportUtils.map(hivMonitoringViralLoadNumAndDen(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTatLeast12MonthsAndVlLess1000 AND hivMonitoringViralLoadNumAndDen AND NOT deceased");
		return cd;
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
		compositionCohortDefinition.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		compositionCohortDefinition.setCompositionString("(inHivProgram AND hasVisit AND adult) AND NOT (onTbTreatment OR deceased)");
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
		cd.addSearch("negativeTB", ReportUtils.map(commonCohorts.hasObs(tbDiseaseStatus, noSignsOrSymptoms), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("onIPT", ReportUtils.map(onINHProphylaxis(), "onOrAfter=${onOrBefore-24m},onOrBefore=${onOrBefore}"));
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
		inhDispensed.setTimeModifier(TimeModifier.LAST);
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
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("spousePartner", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.FAMILY_MEMBER), Dictionary.getConcept(Dictionary.PARTNER_OR_SPOUSE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivPositivePartner", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.SIGN_SYMPTOM_PRESENT), Dictionary.getConcept(Dictionary.YES)), "onOrAfter=${onOrBefore-12m},onOrBefore=${onOrBefore}"));
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
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
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
		cd.addSearch("hivPositiveChild", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.SIGN_SYMPTOM_PRESENT), Dictionary.getConcept(Dictionary.YES)), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
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
		cd.addSearch("notPregnant", ReportUtils.map(commonCohorts.hasObs(pregnancyStatus, notPregnant), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("above15Years", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("below49Years", ReportUtils.map(commonCohorts.agedAtMost(49), "effectiveDate=${onOrBefore}"));
		cd.addSearch("onModernContraceptives", ReportUtils.map(pwpCohortLibrary.modernContraceptivesProvided(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND notPregnant AND above15Years AND below49Years AND onModernContraceptives AND NOT deceased");
		return cd;
	}

	/**
	 * intersection of nonPregnantWomen15To49YearsOnModernContraceptives and nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit
	 * @return CohortDefinition
	 */
	public CohortDefinition nonPregnantWomen15To49YearsOnModernContraceptivesAndHasVisits() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("nonPregnantWomen15To49YearsOnModernContraceptives", ReportUtils.map(nonPregnantWomen15To49YearsOnModernContraceptives(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit", ReportUtils.map(nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("nonPregnantWomen15To49YearsOnModernContraceptives AND nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit AND NOT deceased");
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
		cd.addSearch("notPregnant", ReportUtils.map(commonCohorts.hasObs(pregnancyStatus, notPregnant), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("above15Years", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("below49Years", ReportUtils.map(commonCohorts.agedAtMost(49), "effectiveDate=${onOrBefore}"));
		cd.addSearch("hasVisits", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND notPregnant AND above15Years AND below49Years AND hasVisits AND NOT deceased");
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
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasCd4 AND adult AND NOT (onART OR deceased)");
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
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasVisits AND adult AND NOT (onART OR deceased)");
		return cd;
	}

	/**
	 * Intersection of hasCD4ResultsAdult and hasHivVisitAdult
	 * @return CohortDefinition
	 */
	public CohortDefinition hasCD4ResultsAndHasHivVisitAdult() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasCD4ResultsAdult", ReportUtils.map(hasCD4ResultsAdult(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasHivVisitAdult", ReportUtils.map(hasHivVisitAdult(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasCD4ResultsAdult AND hasHivVisitAdult AND NOT (onART OR deceased)");
		return cd;
	}

	/**
	 * Adult patients screened for tb using ICF form
	 * @return CohortDefinition
	 */
	public CohortDefinition screenedForTBUsingICF() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("adult", ReportUtils.map(commonCohorts.agedAtLeast(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("screenedForTb", ReportUtils.map(tbCohortLibrary.screenedForTbUsingICF(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("adult AND screenedForTb AND NOT deceased");
		return cd;
	}

	/**
	 * intersection of screenedForTBUsingICF and hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months
	 * @return CohortDefinition
	 */
	public CohortDefinition screenedForTBUsingICFNotOnTbTreatmentAndHsaClinicalVisits(){
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("screenedForTBUsingICF", ReportUtils.map(screenedForTBUsingICF(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months", ReportUtils.map(hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("screenedForTBUsingICF AND hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months AND NOT deceased");
		return cd;
	}

	public CohortDefinition complimentHasCd4Results() {

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasCD4ResultsAndHasHivVisitAdult", ReportUtils.map(hasCD4ResultsAndHasHivVisitAdult(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasHivVisitAdult", ReportUtils.map(hasHivVisitAdult(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasHivVisitAdult AND NOT hasCD4ResultsAndHasHivVisitAdult AND NOT onART AND NOT deceased");
		return cd;
	}

	public CohortDefinition complimentPatientsWhoAreEligibleAndStartedArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("eligibleAndStartedARTAndHivInfectedAndNotOnARTAndHasHivClinicalVisit", ReportUtils.map(artCohortLibrary.eligibleAndStartedARTAndHivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfectedAndNotOnARTAndHasHivClinicalVisit", ReportUtils.map(hivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hivInfectedAndNotOnARTAndHasHivClinicalVisit AND NOT eligibleAndStartedARTAndHivInfectedAndNotOnARTAndHasHivClinicalVisit AND NOT deceased");
		return cd;
	}

	public CohortDefinition complimentPatientsOnArtHavingAtLeastOneViralLoad() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("numerator", ReportUtils.map(onARTatLeast12MonthAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("denominator", ReportUtils.map(onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("denominator AND NOT numerator AND NOT deceased");
		return cd;
	}

	public CohortDefinition complimentOnARTatLeast12MonthsAndVlLess1000() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTatLeast12MonthsAndVlLess1000AndHivMonitoringViralLoadNumAndDen", ReportUtils.map(onARTatLeast12MonthsAndVlLess1000AndHivMonitoringViralLoadNumAndDen(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivMonitoringViralLoadNumAndDen", ReportUtils.map(hivMonitoringViralLoadNumAndDen(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hivMonitoringViralLoadNumAndDen AND NOT onARTatLeast12MonthsAndVlLess1000AndHivMonitoringViralLoadNumAndDen AND NOT deceased");
		return cd;
	}

	public CohortDefinition complimentTbScreeningUsingIcfAdult() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("screenedForTBUsingICFNotOnTbTreatmentAndHsaClinicalVisits", ReportUtils.map(screenedForTBUsingICFNotOnTbTreatmentAndHsaClinicalVisits(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months", ReportUtils.map(hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months AND NOT screenedForTBUsingICFNotOnTbTreatmentAndHsaClinicalVisits AND NOT deceased");
		return cd;
	}

	public CohortDefinition complimentNutritionalAssessmentNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hadNutritionalAssessmentAtLastVisitAndhasHivVisitAdult", ReportUtils.map(hadNutritionalAssessmentAtLastVisitAndhasHivVisitAdult(), "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasHivVisitAdult", ReportUtils.map(hasHivVisitAdult(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasHivVisitAdult AND NOT hadNutritionalAssessmentAtLastVisitAndhasHivVisitAdult AND NOT deceased");
		return cd;
	}

	public CohortDefinition complimentReproductiveHealthFamilyPlanningNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("nonPregnantWomen15To49YearsOnModernContraceptivesAndHasVisits", ReportUtils.map(nonPregnantWomen15To49YearsOnModernContraceptivesAndHasVisits(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit", ReportUtils.map(nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit AND NOT nonPregnantWomen15To49YearsOnModernContraceptivesAndHasVisits AND NOT deceased");
		return cd;
	}
}