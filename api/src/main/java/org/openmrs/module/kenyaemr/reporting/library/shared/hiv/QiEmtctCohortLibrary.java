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
import org.openmrs.api.PatientSetService;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.kenyacore.report.cohort.definition.DateObsValueBetweenCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PregnantAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.EddEstimateFromMchmsProgramCalculation;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * Library of Quality Improvement cohorts for HIV care patients in MCHMS and MCHCS
 */
@Component
public class QiEmtctCohortLibrary {

	/**
	 * Number of pregnant women attending at least N ANC visits
	 * @return org.openmrs.module.reporting.cohort.definition.CohortDefinition
	 */
	public CohortDefinition patientsAttendingAtLeastAncVisitsAndPregnant(Integer ancVisits) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new PregnantAtArtStartCalculation());
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
}
