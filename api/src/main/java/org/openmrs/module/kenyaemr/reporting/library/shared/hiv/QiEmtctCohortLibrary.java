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
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.api.PatientSetService;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.DateObsValueBetweenCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.HEICohortsXMonthsDuringReviewCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.InfantMotherOrGuardianPairVisitsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.InfantsDNAPCRCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.AllDeliveriesOnOrAfterMonthsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.DeliveriesWithFullPartographsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.EddEstimateFromMchmsProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.MotherNewBornPairReviewedCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.OnHaartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.PregnantWithANCVisitsCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.mchcs.MchcsCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.reporting.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * Library of Quality Improvement cohorts for HIV care patients in MCHMS and MCHCS
 */
@Component
public class QiEmtctCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	private QiCohortLibrary qiCohortLibrary;

	@Autowired
	private HivCohortLibrary hivCohortLibrary;

	@Autowired
	private ArtCohortLibrary artCohortLibrary;

	@Autowired
	private MchcsCohortLibrary mchcsCohortLibrary;

	/**
	 * Number of pregnant women attending at least N ANC visits
	 * @return org.openmrs.module.reporting.cohort.definition.CohortDefinition
	 */
	public CohortDefinition patientsAttendingAtLeastAncVisitsAndPregnant(Integer ancVisits) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new PregnantWithANCVisitsCalculation());
		cd.setName("Pregnant women who had at least "+ ancVisits +" during the review period");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("visits", ancVisits);
		return cd;
	}

	/**
	 * Number of women delivered in the facility during the review period
	 * @return CohortDefinition
	 */
	public CohortDefinition womenDeliveredInFacility() {

		Concept placeOfBirth = Dictionary.getConcept(Dictionary.LOCATION_OF_BIRTH);
		Concept home = Dictionary.getConcept(Dictionary.HOME);
		Concept other = Dictionary.getConcept(Dictionary.OTHER_NON_CODED);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);
		Concept enrouteToHealthFacility = Dictionary.getConcept(Dictionary.EN_ROUTE_TO_HEALTH_FACILITY);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("Women delivered in a facility");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(placeOfBirth);
		cd.setValueList(Arrays.asList(home, other, unknown, enrouteToHealthFacility));
		cd.setOperator(SetComparator.NOT_IN);
		return cd;
	}

	/**
	 * Number of expected deliveries in the facility catchment population during the review period
	 * @return CohortDefinition
	 */
	public  CohortDefinition numberOfExpectedDeliveriesInTheFacilityCatchmentPopulationDuringTheReviewPeriod() {

		//find patients who are in MCHMS program without edd but lmp
		DateCalculationCohortDefinition cdLmpEdd = new DateCalculationCohortDefinition(new EddEstimateFromMchmsProgramCalculation());
		cdLmpEdd.setName("edd from lmp");
		cdLmpEdd.addParameter(new Parameter("onDate", "On Date", Date.class));

		//checking it from the edd concept obs
		DateObsValueBetweenCohortDefinition eddObsConcept = new DateObsValueBetweenCohortDefinition();
		eddObsConcept.setName("patients Who edd between date");
		eddObsConcept.setQuestion(Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT));
		eddObsConcept.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		eddObsConcept.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

		//combine the two cohort definition
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("patients with edd");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("lmp", ReportUtils.map(cdLmpEdd, "onDate=${onOrBefore}"));
		cd.addSearch("edd", ReportUtils.map(eddObsConcept, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("lmp AND edd");

		return cd;
	}

	/**
	 *
	 */
	public CohortDefinition numberOfNewAnClients() {
		EncounterCohortDefinition encCd = new EncounterCohortDefinition();
		encCd.setName("has encounter between dates");
		encCd.setTimeQualifier(TimeQualifier.FIRST);
		encCd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		encCd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		encCd.setEncounterTypeList(Arrays.asList(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION)));
		encCd.setFormList(Arrays.asList(MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ANTENATAL_VISIT)));
		return encCd;
	}

	/**
	 *
	 */
	public CohortDefinition mothersNewBornPairReview() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new MotherNewBornPairReviewedCalculation());
		cd.setName("Mother-new born pair reviewed");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/*
	* pregnant wommen
	*@return CohortDefinition
	 */
	public CohortDefinition pregnant() {
		CalculationCohortDefinition calculationCd = new CalculationCohortDefinition(new IsPregnantCalculation());
		calculationCd.setName("Pregnant");
		calculationCd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return calculationCd;
	}

	/**
	 * Partners of pregnant women should be tested for HIV or have a known positive status
	 * @return CohortDefinition
	 */
	public CohortDefinition pregnantWomenWhosePartnersHaveBeenTestedForHivOrWhoAreKnownPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Pregnant women and whose partners are tested");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("testedOrKnownHiv", ReportUtils.map(qiCohortLibrary.hivPositivePatientsWhosePartnersAreHivPositive(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pregnant", ReportUtils.map(pregnant(), "onDate=${onOrBefore}"));
		cd.setCompositionString("testedOrKnownHiv AND pregnant");
		return cd;
	}

	/**
	 * Number of HIV infected pregnant women who were receiving HAART
	 * @return CohortDefinition
	 */
	public CohortDefinition hivInfectedPregnantWomenReceivingHaart() {
		CalculationCohortDefinition calculationCd = new CalculationCohortDefinition(new OnHaartCalculation());
		calculationCd.setName("Mother on HAART");
		calculationCd.addParameter(new Parameter("onDate", "On Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv infected women receiving Haart");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("pregnant", ReportUtils.map(pregnant(), "onDate=${onOrBefore}"));
		//cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("onHaart", ReportUtils.map(calculationCd, "onDate=${onOrBefore}"));
		cd.setCompositionString("pregnant AND onHaart");
		return cd;
	}

	/**
	 * Number of HIV-infected pregnant women who had at least one ANC visit during the 6 months review period.
	 * @return CohortDefinition
	 */
	public CohortDefinition hIVInfectedPregnantWomenWhoHadAtLeastOneAncVisitDuring6MonthsReviewPeriod() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv infected, pregnant women with at least one anc visit");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hivTestedPositive", ReportUtils.map(hivCohortLibrary.testedHivStatus(Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("pregnant", ReportUtils.map(pregnant(), "onDate=${onOrBefore}"));
		cd.addSearch("atLeastOneANCVisit", ReportUtils.map(patientsAttendingAtLeastAncVisitsAndPregnant(1), "onDate=${onOrBefore}"));
		cd.setCompositionString("(hivTestedPositive OR inHivProgram) AND pregnant AND atLeastOneANCVisit");
		return cd;
	}

	/**
	 * Number of HIV-infected pregnant or lactating women on ART for at least 6 months with a VL result not older than 6 months at their last visit.
	 * @return CohortDefinition
	 */
	public CohortDefinition hivInfectedOrLactatingWomenOnARTForAtLeast6MonthsWithVLResultsNotOlderThan6monthsAtTheirLastVisit() {

		//find the <1000 copies of recent obs
		NumericObsCohortDefinition cdVl = new NumericObsCohortDefinition();
		cdVl.setName("VL result");
		cdVl.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cdVl.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cdVl.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		cdVl.setTimeModifier(PatientSetService.TimeModifier.LAST);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv infected, pregnant women or lactating on ART and VL in the last 6 months");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("onART6Months", ReportUtils.map(artCohortLibrary.netCohortMonths(6), "onDate=${onOrBefore}"));
		cd.addSearch("hivTestedPositive", ReportUtils.map(hivCohortLibrary.testedHivStatus(Dictionary.getConcept(Dictionary.POSITIVE)), "onOrBefore=${onOrBefore}"));
		cd.addSearch("inHivProgram", ReportUtils.map(commonCohorts.enrolled(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)), "enrolledOnOrBefore=${onOrBefore}"));
		cd.addSearch("pregnant", ReportUtils.map(pregnant(), "onDate=${onOrBefore}"));
		cd.addSearch("vlResults", ReportUtils.map(cdVl, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("(hivTestedPositive OR inHivProgram) AND onART6Months AND pregnant AND vlResults");
		return cd;

	}

	/**
	 * Number of HIV-infected pregnant or lactating women who have been on ART for at least 6 months with at least one ANC visit during the 6 months review period.
	 * @return CohortDefinition
	 */
	public CohortDefinition hivInfectedOrLactatingWomenOnARTForAtLeast6MonthsWithAtLeastOneAncVisitDuringThe6MonthsReviewPeriod() {

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Hiv infected, pregnant women or lactating on ART and has ANC in the last 6 months");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("hadAncVisitPregnantOrLactatingHivInfected", ReportUtils.map(hIVInfectedPregnantWomenWhoHadAtLeastOneAncVisitDuring6MonthsReviewPeriod(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART6Months", ReportUtils.map(artCohortLibrary.netCohortMonths(6), "onDate=${onOrBefore}"));
		cd.setCompositionString("hadAncVisitPregnantOrLactatingHivInfected AND onART6Months");
		return cd;
	}

	/**
	 * Number of deliveries with partographs accurately filled during the review period
	 * @return CohortDefinition
	 */
	public CohortDefinition numberOfDeliveriesWithPartographsAccuratelyFilled(int months) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new DeliveriesWithFullPartographsCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("onOrAfter", months);
		return cd;
	}

	/**
	 * Number of deliveries in the facility during the review period
	 * @return CohortDefinition
	 */
	public CohortDefinition numberOfDeliveriesInTheFacilityDuringTheReviewPeriod(int months) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new AllDeliveriesOnOrAfterMonthsCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("onOrAfter", months);
		return cd;
	}

	/**
	 * infants exposed to HIV
	 * This is checked when a child is enrolled through mchcs enrollment form
	 */
	public CohortDefinition exposedInfants() {
		return commonCohorts.hasObs(Dictionary.getConcept(Metadata.Concept.CHILDS_CURRENT_HIV_STATUS), Dictionary.getConcept(Metadata.Concept.EXPOSURE_TO_HIV));
	}
	 /* Number of HIV-infected pregnant or lactating women on ART for at least 6 months who have VL < 1,000 copies on their most recent VL result
	 * @return CohortDefinition
	 */
	public CohortDefinition numberOfHivInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWhoHaveVlLess1000CopiesOnTheirMostRecentVlResult() {

		//find the <1000 copies of recent obs
		NumericObsCohortDefinition cdVlLess1000 = new NumericObsCohortDefinition();
		cdVlLess1000.setName("Less than 1000 Copies");
		cdVlLess1000.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cdVlLess1000.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		cdVlLess1000.setOperator1(RangeComparator.LESS_THAN);
		cdVlLess1000.setValue1(1000.0);
		cdVlLess1000.setTimeModifier(PatientSetService.TimeModifier.LAST);


		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("pregnant and on art 6 months ago");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("onART6Months", ReportUtils.map(artCohortLibrary.netCohortMonths(6), "onDate=${onOrBefore}"));
		cd.addSearch("vlLess1000", ReportUtils.map(cdVlLess1000, "onOrBefore=${onOrBefore}"));
		cd.addSearch("pregnant", ReportUtils.map(pregnant(), "onDate=${onOrBefore}"));
		cd.setCompositionString("onART6Months AND vlLess1000 AND pregnant");
		return cd;
	}

	/**
	 * Number of HIV-infected pregnant or lactating women on ART for at least 6 months with a VL result not older than 6 months from the end of the review period.
	 * @return CohortDefinition
	 */
	public CohortDefinition numberOfHivInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWithVlResultNotOlderThan6MonthsFromTheEndOfTheReviewPeriod() {

		//find vl copies of recent obs
		NumericObsCohortDefinition cdVlLess1000 = new NumericObsCohortDefinition();
		cdVlLess1000.setName("vl Copies");
		cdVlLess1000.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cdVlLess1000.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cdVlLess1000.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		cdVlLess1000.setTimeModifier(PatientSetService.TimeModifier.LAST);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("pregnant and on art 6 months ago");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("pregnant", ReportUtils.map(pregnant(), "onDate=${onOrBefore}"));
		cd.addSearch("vlLess1000", ReportUtils.map(cdVlLess1000, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("onART6Months", ReportUtils.map(artCohortLibrary.netCohortMonths(6), "onDate=${onOrBefore}"));
		cd.setCompositionString("onART6Months AND vlLess1000 AND pregnant");
		return cd;

	}

	/**
	 *
	 * @param weeks
	 * @return
	 */
	public CohortDefinition numberOfInfantsWhoReceivedDNAPCRObs(int weeks) {
		CalculationCohortDefinition dnaPcrInfants = new CalculationCohortDefinition(new InfantsDNAPCRCalculation());
		dnaPcrInfants.addParameter(new Parameter("onDate", "On Date", Date.class));
		dnaPcrInfants.addCalculationParameter("durationAfterBirth", weeks);
		return dnaPcrInfants;
	}

	/**
	 * Number of exposed infants who received dna-pcr test after birth
	 * @return CohortDefinition
	 */
	public CohortDefinition numberOfExposedInfantsWhoReceivedDNAPCRTest(int weeks) {

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("exposed infants with dna pcr test results");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("exposedInfants", ReportUtils.map(exposedInfants(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("dnaPcrInfants", ReportUtils.map(numberOfInfantsWhoReceivedDNAPCRObs(weeks), "onDate=${onOrBefore}"));
		cd.setCompositionString("exposedInfants AND dnaPcrInfants");
		return cd;
	}

	/**
	 * Number of HIV exposed infants that are on exclusive breast feeding at age 6 months
	 * @return CohortDefinition
	 */
	public CohortDefinition heiInfantsOnExclusiveBreastFeedingAtAge6Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of HIV exposed infants that are on exclusive breast feeding at age 6 months");
		cd.addParameter(new Parameter("OnOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("OnOrAfter", "After Date", Date.class));
		cd.addSearch("exposedInfants", ReportUtils.map(exposedInfants(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("exclusiveFeeding", ReportUtils.map(mchcsCohortLibrary.exclusiveBreastFeeding(), "onOrAfter=${OnOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageAt6Months", ReportUtils.map(mchcsCohortLibrary.ageAt6Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("exposedInfants AND exclusiveFeeding AND ageAt6Months");
		return cd;
	}

	/**
	 * Number of HIV-exposed infants identified HIV positive by 18 months of age
	 * @return CohortDefinition
	 */
	public CohortDefinition heiIdentifiedHivPositiveBy18MonthsOfAge() {
		Concept discontinuation = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept hivPositive18Months = Dictionary.getConcept(Dictionary.HIV_POSITIVE);
		Concept resultOfHivTesting = Dictionary.getConcept(Dictionary.RESULT_OF_HIV_TEST);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of HIV-exposed infants identified HIV positive by 18 months of age");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("exposedInfants", ReportUtils.map(exposedInfants(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("hivPositiveBy18Months", ReportUtils.map(commonCohorts.hasObs(discontinuation, hivPositive18Months), "onOrAfter=${OnOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivPositive", ReportUtils.map(commonCohorts.hasObs(resultOfHivTesting, positive), "onOrAfter=${OnOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("exposedInfants AND (hivPositiveBy18Months OR hivPositive)");
		return  cd;
	}

	/**
	 * Number of HEI in cohorts who turned 12 months during the 6 months review period
	 * @return CohortDefinition
	 */
	public CohortDefinition heiCohortWhoTurnedXMonthsDuringXMonthsReviewPeriod(int turnedMonths, int reviewMonths) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new HEICohortsXMonthsDuringReviewCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("turnedMonths", turnedMonths);
		cd.addCalculationParameter("reviewMonths", reviewMonths);
		return cd;
	}

	/**
	 * patients who had a particular form filled during a given period of time
	 */
	public CohortDefinition patientsWithForms(Form form){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setFormList(Arrays.asList(form));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		return cd;
	}

	/**
	 * HEI patients between 0 and 18 months in follow-up at a facility
	 */
	public CohortDefinition heiPatientsInFollowUp(){
		AgeCohortDefinition ageCohort = new AgeCohortDefinition();
		ageCohort.setMaxAgeUnit(DurationUnit.MONTHS);
		ageCohort.setMinAgeUnit(DurationUnit.DAYS);
		ageCohort.setMaxAge(18);
		ageCohort.setMinAge(0);
		ageCohort.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("heiPatients0To18Months", ReportUtils.map(heiPatients0To18Months(), "onOrBefore=${onOrBefore}"));
		cd.addSearch("inFollowUp", ReportUtils.map(patientsWithForms(MetadataUtils.existing(Form.class, MchMetadata._Form.MCHCS_FOLLOW_UP)), "onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("heiPatients0To18Months AND inFollowUp");
		return cd;
	}

	/**
	 * Number of infants seen in facility during review period whose mother/guardian also have documented visit on same day during review period
	 * @return CohortDefinition
	 */
	public CohortDefinition numberOfInfantsSeenInFacilityDuringReviewPeriodWhoseMotherOrGuardianAlsoHaveDocumentedVisitOnSameDayDuringReviewPeriod(int reviewPeriod) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new InfantMotherOrGuardianPairVisitsCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("reviewPeriod", reviewPeriod);
		return cd;
	}

	/**
	 * HEI patients between 0 and 18 months  at a facility
	 */
	public CohortDefinition heiPatients0To18Months(){
		AgeCohortDefinition ageCohort = new AgeCohortDefinition();
		ageCohort.setMaxAgeUnit(DurationUnit.MONTHS);
		ageCohort.setMinAgeUnit(DurationUnit.DAYS);
		ageCohort.setMaxAge(18);
		ageCohort.setMinAge(0);
		ageCohort.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addSearch("Infants", ReportUtils.map(ageCohort, "effectiveDate=${onOrBefore}"));
		cd.addSearch("exposedPatients", ReportUtils.map(exposedInfants(), "onOrBefore=${onOrBefore}"));
		cd.setCompositionString("Infants AND exposedPatients");
		return cd;
	}
}
