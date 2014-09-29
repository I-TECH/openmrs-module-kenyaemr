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
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
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
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
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
	 *
	 */
	public CohortDefinition numberOfDeliveriesInTheFacilityDuringTheReviewPeriod(int months) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new AllDeliveriesOnOrAfterMonthsCalculation());
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("onOrAfter", months);
		return cd;
	}
}
