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
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.ObsInLastVisitCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.InCareHasAtLeast2VisitsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.PatientsWhoMeetCriteriaForNutritionalSupport;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.HavingAtLeastOneVisitInEachQuoterCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.PatientsWithVLResultsAtLeastMonthAgoCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.cqi.PatientsWithVLResultsLessThanXValueCalculation;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary;
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
	private TbCohortLibrary tbCohortLibrary;

	@Autowired
	private QiCohortLibrary qiCohortLibrary;


	/**
	 * Patients in care and has at least 2 visits
	 */
	public CohortDefinition inCareHasAtLeast2Visits() {
		CalculationCohortDefinition cdInCareHasAtLeast2Visits = new CalculationCohortDefinition(new InCareHasAtLeast2VisitsCalculation());
		cdInCareHasAtLeast2Visits.setName("patients in care and have at least 2 visits 3 months a part");
		cdInCareHasAtLeast2Visits.addParameter(new Parameter("onDate", "On Date", Date.class));

		CalculationCohortDefinition cdHasAtLeast1VisitInEveryQuoter = new CalculationCohortDefinition(new HavingAtLeastOneVisitInEachQuoterCalculation());
		cdHasAtLeast1VisitInEveryQuoter.setName("patients who have at least one visit in every quoter");
		cdHasAtLeast1VisitInEveryQuoter.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("child and in care");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("inCare", ReportUtils.map(cdInCareHasAtLeast2Visits, "onDate=${onOrBefore}"));
		cd.addSearch("atLeastAVisitInEachQuoter", ReportUtils.map(cdHasAtLeast1VisitInEveryQuoter, "onDate=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(inCare OR atLeastAVisitInEachQuoter) AND child AND NOT deceased");
		return  cd;
	}

	/**
	 * intersection of inCareHasAtLeast2Visits and  clinicalVisit
	 * @return CohortDefinition
	 */
	public CohortDefinition inCareHasAtLeast2VisitsAndClinicalVisit() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inCareHasAtLeast2Visits", ReportUtils.map(inCareHasAtLeast2Visits(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("clinicalVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("inCareHasAtLeast2Visits AND clinicalVisit AND NOT onART AND NOT deceased");
		return cd;
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
		cd.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("enrolledIn4To6MonthOfReviewPeriod", ReportUtils.map(qiCohortLibrary.enrolledIn4To6MonthOfReviewPeriod(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("inCare AND hasVisit AND child AND NOT enrolledIn4To6MonthOfReviewPeriod AND NOT onART AND NOT deceased");
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
		cd.addSearch("atLeastOneEligibilityCriteria", ReportUtils.map(artCohortLibrary.EligibleForArtExclusive(), "onDate=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore-6m}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(hasVisits AND atLeastOneEligibilityCriteria AND child) AND NOT onART AND NOT deceased");
		return cd;
	}

	/**
	 * Children who were screened for TB
	 * @return CohortDefinition
	 */
	public CohortDefinition screenForTBUsingICFAndChild() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Child screened for TB using ICF form");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("screened", ReportUtils.map(tbCohortLibrary.screenedForTbUsingICF(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("child AND screened AND NOT deceased");
		return cd;
	}

	/**
	 * Intersection of screenForTBUsingICFAndChild and hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months
	 * @return CohortDefinition
	 */
	public CohortDefinition screenForTBUsingICFAndChildAndIsHivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(){
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("screenForTBUsingICFAndChild", ReportUtils.map(screenForTBUsingICFAndChild(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months", ReportUtils.map(hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("screenForTBUsingICFAndChild AND hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months AND NOT deceased");
		return cd;
	}

	/**
	 * Patients who had vl X months ago
	 * @return CodedObsCohortDefinition
	 */
	public CohortDefinition havingVlXMonthsAgo(int months) {
		CalculationCohortDefinition cdVl = new CalculationCohortDefinition(new PatientsWithVLResultsAtLeastMonthAgoCalculation());
		cdVl.setName("Patients with vl results at least 12 months ago");
		cdVl.addParameter(new Parameter("onDate", "On Date", Date.class));
		cdVl.addCalculationParameter("months", months);
		return cdVl;
	}

	/**
	 * Patients on ART for at least 12 months by the end of the review period
	 * Patients have at least one Viral Load (VL) results during the last 1 months
	 * @return cohort definition
	 */
	public CohortDefinition onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART for at least 6 months and have VL during the last 12 months - Child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("onARTForAtLeast6Months", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore-6m}"));
		cd.addSearch("viralLoadResultsIn12Months", ReportUtils.map(havingVlXMonthsAgo(12), "onDate=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTForAtLeast6Months AND child AND viralLoadResultsIn12Months AND NOT deceased");
		return cd;
	}

	/**
	 * Intersection of onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months and onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12MonthsAndOnARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months", ReportUtils.map(onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview", ReportUtils.map(onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months AND onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview AND NOT deceased");
		return cd;
	}

	/**
	 *
	 */
	public CohortDefinition hivMonitoringViralLoadNumDenForChild() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Viral Load for children Num And Den");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months", ReportUtils.map(onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview", ReportUtils.map(onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12Months AND onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview AND NOT deceased");
		return cd;
	}

	/**
	 * Number of HIV infected patients on ART 12 months ago
	 * Have atleast one clinical visit during the six months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("on ART at least 6 months and have at least one clinical visit during the last 12 months - Child");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTForAtLeast6Months", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore-6m}"));
		cd.addSearch("hasVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTForAtLeast6Months AND hasVisit AND child AND NOT deceased");
		return cd;
	}

	/**
	 * Number of patients on ART for at least 12 months
	 * VL < 1000 copies
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast6MonthsAndVlLess1000() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();

		//find the <1000 copies of recent obs
		CalculationCohortDefinition cdVlLess1000 = new CalculationCohortDefinition(new PatientsWithVLResultsLessThanXValueCalculation());
		cdVlLess1000.setName("Less than 1000 Copies");
		cdVlLess1000.addParameter(new Parameter("onDate", "On Date", Date.class));
		cdVlLess1000.addCalculationParameter("months", 12);
		cdVlLess1000.addCalculationParameter("threshold", 1000.0);


		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setName("Number of patients on ART for at least 6 months and VL < 1000 copies in last 12 months -  Child");
		cd.setName("onARTatLeast6MonthsAndVlLess1000");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("onARTatLeast6Months", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore-6m}"));
		cd.addSearch("vlLess1000", ReportUtils.map(cdVlLess1000, "onDate=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));

		cd.setCompositionString("onARTatLeast6Months AND vlLess1000 AND child AND NOT deceased");

		return cd;
	}
	/**
	 * intersect onARTatLeast6MonthsAndVlLess1000 and hivMonitoringViralLoadNumDenForChild
	 * @return CohortDefinition
	 */
	public CohortDefinition onARTatLeast6MonthsAndVlLess1000AndHivMonitoringViralLoadNumDenForChild() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onARTatLeast6MonthsAndVlLess1000", ReportUtils.map(onARTatLeast6MonthsAndVlLess1000(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("hivMonitoringViralLoadNumDenForChild", ReportUtils.map(hivMonitoringViralLoadNumDenForChild(),  "onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onARTatLeast6MonthsAndVlLess1000 AND hivMonitoringViralLoadNumDenForChild AND NOT deceased");
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
		compositionCohortDefinition.addSearch("hasHivVisit", ReportUtils.map(hivCohortLibrary.hasHivVisit(), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		compositionCohortDefinition.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		compositionCohortDefinition.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		compositionCohortDefinition.setCompositionString("(inHivProgram AND hasHivVisit AND child) AND NOT (onTbTreatment OR deceased)");
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
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(negativeTB AND child) AND NOT (onIPT OR deceased)");
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
		coded.setTimeModifier(TimeModifier.LAST);
		coded.setValueList(Arrays.asList(noSignsOrSymptoms));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();

		cd.setName("Patients with No IPT within 2 years and Tb Negative");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("negativeTB", ReportUtils.map(coded, "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("onIPT", ReportUtils.map(qiCohortLibrary.onINHProphylaxis(), "onOrAfter=${onOrBefore-24m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(negativeTB AND child) AND NOT (onIPT OR deceased)");
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

		cd.addSearch("hadWeight", ReportUtils.map(hadWeight, "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("hadHeight", ReportUtils.map(hadHeight, "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("hadMuac", ReportUtils.map(hadMuac, "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.addSearch("child", ReportUtils.map(commonCohorts.agedAtMost(15), "effectiveDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("(hadWeight AND hadHeight AND child) OR hadMuac AND NOT deceased");
		return cd;
	}
	/**
	 * intersection of hadNutritionalAssessmentAtLastVisit and hasHivVisitPaeds
	 * @return CohortDefinition
	 */
	public CohortDefinition hadNutritionalAssessmentAtLastVisitAndHasHivVisitPaeds() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hadNutritionalAssessmentAtLastVisit", ReportUtils.map(hadNutritionalAssessmentAtLastVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasHivVisitPaeds", ReportUtils.map(hasHivVisitPaeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hadNutritionalAssessmentAtLastVisit AND hasHivVisitPaeds AND NOT deceased");
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
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("meetCriteria AND child AND NOT deceased");
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
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("meetNutritionCriteria AND lastClinicVisit AND child AND NOT deceased");
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
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hivInfected AND disclosed AND childMore8Years AND childLess15 AND NOT deceased");
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
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("dnaPcr1 OR dnaPcr2 OR antiBody1 OR antiBody2 AND NOT deceased");
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
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("inHivProgram AND hasVisit AND childMore4 AND childLess15 AND NOT deceased");
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
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasCd4 AND child AND hasVisits AND NOT (onART OR deceased)");
		return cd;
	}

	/**
	 * Intersection of hasCD4ResultsPaeds and hasHivVisitPaeds
	 * @return CohortDefinition
	 */
	public CohortDefinition hasCD4ResultsPaedsAndhasHivVisitPaeds() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasCD4ResultsPaeds", ReportUtils.map(hasCD4ResultsPaeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasHivVisitPaeds", ReportUtils.map(hasHivVisitPaeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasCD4ResultsPaeds AND hasHivVisitPaeds AND NOT deceased");
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
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasVisits AND child AND NOT (onART OR deceased)");
		return cd;
	}

	public CohortDefinition complementClinicalVisitNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("inCareHasAtLeast2VisitsAndClinicalVisit", ReportUtils.map(inCareHasAtLeast2VisitsAndClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("clinicalVisit", ReportUtils.map(clinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART", ReportUtils.map(artCohortLibrary.onArt(), "onDate=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("clinicalVisit AND NOT inCareHasAtLeast2VisitsAndClinicalVisit AND NOT (onART OR deceased)");
		return cd;
	}

	public CohortDefinition complementHivMonitoringCd4Num() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hasCD4ResultsPaedsAndhasHivVisitPaeds", ReportUtils.map(hasCD4ResultsPaedsAndhasHivVisitPaeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hasHivVisitPaeds", ReportUtils.map(hasHivVisitPaeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hasHivVisitPaeds AND NOT (hasCD4ResultsPaedsAndhasHivVisitPaeds OR deceased)");
		return cd;
	}

	public CohortDefinition complementArtInitiationNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("eligibleAndStartedARTPedsAndhivInfectedAndNotOnARTAndHasHivClinicalVisit", ReportUtils.map(artCohortLibrary.eligibleAndStartedARTPedsAndhivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivInfectedAndNotOnARTAndHasHivClinicalVisit", ReportUtils.map(hivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("hivInfectedAndNotOnARTAndHasHivClinicalVisit AND NOT eligibleAndStartedARTPedsAndhivInfectedAndNotOnARTAndHasHivClinicalVisit AND NOT deceased");
		return cd;
	}

	public CohortDefinition complementHivMonitoringViralLoadNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("numerator", ReportUtils.map(onARTatLeast6MonthsAndHaveAtLeastVLResultsDuringTheLast12MonthsAndOnARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("denominator", ReportUtils.map(onARTatLeast6MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("denominator AND NOT numerator AND NOT deceased");
		return cd;
	}

	public CohortDefinition complementHivMonitoringViralLoadSupressionNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("numerator", ReportUtils.map(onARTatLeast6MonthsAndVlLess1000AndHivMonitoringViralLoadNumDenForChild(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("denominator", ReportUtils.map(hivMonitoringViralLoadNumDenForChild(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("denominator AND NOT numerator AND NOT deceased");
		return cd;
	}

	public CohortDefinition complementTbScreeningServiceCoverageNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("numerator", ReportUtils.map(screenForTBUsingICFAndChildAndIsHivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("denominator", ReportUtils.map(hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("denominator AND NOT numerator AND NOT deceased");
		return cd;
	}

	public CohortDefinition complementNutritionalAssessmentNum() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("numerator", ReportUtils.map(hadNutritionalAssessmentAtLastVisitAndHasHivVisitPaeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("denominator", ReportUtils.map(hasHivVisitPaeds(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("deceased", ReportUtils.map(commonCohorts.deceasedPatients(), "onDate=${onOrBefore}"));
		cd.setCompositionString("denominator AND NOT numerator AND NOT deceased");
		return cd;
	}
}
