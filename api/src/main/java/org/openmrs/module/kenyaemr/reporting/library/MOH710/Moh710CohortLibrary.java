/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.MOH710;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Moh710CohortLibrary {


	public Moh710CohortLibrary() {
		// TODO Auto-generated constructor stub
	}

	/*
	 */
/**
 * Infants aged  less than 12 months ${effectiveDate}
 * @return the cohort definition
 *//*

	public CohortDefinition ageLessThan12months() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children aged less than 12 Months");
		age.setMinAge(0);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		age.setMaxAge(11);
		age.setMaxAgeUnit(DurationUnit.MONTHS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}

	*/
/**
 * Children aged 1 year and above
 * @return the cohort definition
 *//*

	public CohortDefinition age1YearAndAbove() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children aged 1 year and above");
		age.setMinAge(1);
		age.setMinAgeUnit(DurationUnit.YEARS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}

	*/
/**
 * Children aged 18 to 24 Months
 * @return the cohort definition
 *//*

	public CohortDefinition age18To24Months() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children aged Btwn 18 and 24 Months");
		age.setMinAge(18);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		age.setMaxAge(24);
		age.setMaxAgeUnit(DurationUnit.MONTHS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}


	*/
/**
 * Children aged 1 year and above
 * @return the cohort definition
 *//*

	public CohortDefinition ageOver2Years() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children Over 2 years");
		age.setMinAge(25);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}

	*/
/**
 * Children who have been given BCG vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition givenBcgVaccine() {

		Concept bcg = Dictionary.getConcept(Dictionary.BACILLE_CAMILE_GUERIN_VACCINATION);
		Concept immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(immunizations, bcg);

	}

	*/
/**
 * Children who have been given OPV vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition givenOpvVaccine() {

		Concept OPV = Dictionary.getConcept(Dictionary.POLIO_VACCINATION_ORAL);
		Concept immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(immunizations, OPV);

	}

	*/
/**
 * Children who have been given IPV vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition givenIpv() {

		Concept ipv = Dictionary.getConcept(Dictionary.POLIO_VACCINATION_INACTIVATED);
		Concept immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(immunizations, ipv);

	}

	*/
/**
 * Children who have been given DPT/Hep+HiB1 vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition givenDptHepHibVaccine() {

		Concept dptHebHib = Dictionary.getConcept(Dictionary.DIPHTHERIA_TETANUS_AND_PERTUSSIS_VACCINATION);
		Concept Immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(Immunizations, dptHebHib);

	}

	*/
/**
 * Children who have been given Pneumococcal vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition givenPneumococcalVaccine() {

		Concept pneumococcal = Dictionary.getConcept(Dictionary.PNEUMOCOCCAL_CONJUGATE_VACCINE);
		Concept immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(immunizations, pneumococcal);

	}

	*/
/**
 * Children who have been given Rota vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition givenRotaVaccine() {

		Concept rota = Dictionary.getConcept(Dictionary.ROTA_VIRUS_VACCINE);
		Concept immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(immunizations, rota);

	}

	*/
/**
 * Children who have been given Vitamin A Supplement
 * @return the cohort definition
 *//*

	public CohortDefinition givenVitaminASupplement() {

		Concept vitaminA = Dictionary.getConcept(Dictionary.VITAMIN_A);
		Concept immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(immunizations, vitaminA);

	}

	*/
/**
 * Children who have been given Yellow Fever vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition givenYellowFeverVaccine() {

		Concept yellowFever = Dictionary.getConcept(Dictionary.YELLOW_FEVER_VACCINE);
		Concept immunizations = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		return commonCohorts.hasObs(immunizations, yellowFever);

	}

	*/
/**
 * Children who have been Fully Immunized
 * @return the cohort definition
 *//*

	public CohortDefinition fullyImmunizedChildren() {

		Concept fullyImmunizedChild = Dictionary.getConcept(Dictionary.FULLY_IMMUNIZED_CHILD);
		Concept yes = Dictionary.getConcept(Dictionary.YES);

		return commonCohorts.hasObs(fullyImmunizedChild, yes);

	}

	*/
/**
 * Children who have been given ROTA virus vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition everGivenRotaVirusVaccine(Integer sequenceNumber) {

		CalculationCohortDefinition childrenGivenRotaVaccine = new CalculationCohortDefinition(new ChildrenGivenVaccineCalculation());
		childrenGivenRotaVaccine.setName("Children Given Rota Vaccine");
		childrenGivenRotaVaccine.addCalculationParameter("sequenceNumber", sequenceNumber);
		childrenGivenRotaVaccine.addCalculationParameter("vaccine", Dictionary.ROTA_VIRUS_VACCINE);
		return childrenGivenRotaVaccine;

	}

	*/
/**
 * Children who have been given Pneumococcal vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition everGivenPneumococcalVaccine(Integer sequenceNumber) {

		CalculationCohortDefinition childrenGivenPneumococcalVaccine = new CalculationCohortDefinition(new ChildrenGivenVaccineCalculation());
		childrenGivenPneumococcalVaccine.setName("Children Given Pneumococcal Vaccine");
		childrenGivenPneumococcalVaccine.addCalculationParameter("sequenceNumber", sequenceNumber);
		childrenGivenPneumococcalVaccine.addCalculationParameter("vaccine", Dictionary.PNEUMOCOCCAL_CONJUGATE_VACCINE);
		return childrenGivenPneumococcalVaccine;

	}

	*/
/**
 * Children who have been given Dpt-Hep-Hib vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition everGivenDptHepHibVaccine(Integer sequenceNumber) {

		CalculationCohortDefinition childrenGivenDptHepHibVaccine = new CalculationCohortDefinition(new ChildrenGivenVaccineCalculation());
		childrenGivenDptHepHibVaccine.setName("Children Given Dpt-Hep-Hib Vaccine");
		childrenGivenDptHepHibVaccine.addCalculationParameter("sequenceNumber", sequenceNumber);
		childrenGivenDptHepHibVaccine.addCalculationParameter("vaccine", Dictionary.DIPHTHERIA_TETANUS_AND_PERTUSSIS_VACCINATION);
		return childrenGivenDptHepHibVaccine;

	}

	*/
/**
 * Children who have been given Oral Polio vaccine (OPV)
 * @return the cohort definition
 *//*

	public CohortDefinition everGivenOPV(Integer sequenceNumber) {

		CalculationCohortDefinition childrenGivenPolioVaccine = new CalculationCohortDefinition(new ChildrenGivenVaccineCalculation());
		childrenGivenPolioVaccine.setName("Children Given Polio Vaccine");
		childrenGivenPolioVaccine.addCalculationParameter("sequenceNumber", sequenceNumber);
		childrenGivenPolioVaccine.addCalculationParameter("vaccine", Dictionary.POLIO_VACCINATION_ORAL);
		return childrenGivenPolioVaccine;

	}

	*/
/**
 * Children who have been given Measles Rubella vaccine
 * @return the cohort definition
 *//*

	public CohortDefinition everGivenMeaslesRubellaVaccine(Integer sequenceNumber) {

		CalculationCohortDefinition childrenGivenMeaslesRubellaVaccine = new CalculationCohortDefinition(new ChildrenGivenVaccineCalculation());
		childrenGivenMeaslesRubellaVaccine.setName("Children Given Measles Rubella Vaccine");
		childrenGivenMeaslesRubellaVaccine.addCalculationParameter("sequenceNumber", sequenceNumber);
		childrenGivenMeaslesRubellaVaccine.addCalculationParameter("vaccine", Dictionary.MEASLES_RUBELLA_VACCINE);
		return childrenGivenMeaslesRubellaVaccine;

	}


	*/
/**
 * Children who have been given Vitamin A
 * @return the cohort definition
 *//*

	public CohortDefinition everGivenVitaminA(Integer sequenceNumber) {

		CalculationCohortDefinition childrenGivenVitaminA = new CalculationCohortDefinition(new ChildrenGivenVaccineCalculation());
		childrenGivenVitaminA.setName("Children Given Measles Rubella Vaccine");
		childrenGivenVitaminA.addCalculationParameter("sequenceNumber", sequenceNumber);
		childrenGivenVitaminA.addCalculationParameter("vaccine", Dictionary.VITAMIN_A);
		return childrenGivenVitaminA;

	}
	*/
/**
 * Children who were given ROTA Virus vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * Below 1 years
 * @return the cohort definition
 *//*

	public CohortDefinition givenRotaVirusVaccineAndAgedLessThan1Year(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenRotaVirusVaccine", ReportUtils.map(everGivenRotaVirusVaccine(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenRotaVirusVaccine AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given ROTA Virus vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition givenRotaVirusVaccineAndAged1YearAndAbove(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenRotaVirusVaccine", ReportUtils.map(everGivenRotaVirusVaccine(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenRotaVirusVaccine AND Age1YearAndAbove AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given OPV between dates ${onOrAfter} and ${onOrBefore} and are aged
 * Below 1 years
 * @return the cohort definition
 *//*

	public CohortDefinition givenOPVAndAgedLessThan1Year(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenOPV", ReportUtils.map(everGivenOPV(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenOPV AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given OPV between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition givenOPVAndAged1YearAndAbove(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenOPV", ReportUtils.map(everGivenOPV(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenOPV AND Age1YearAndAbove AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Dpt-Hep-Hib between dates ${onOrAfter} and ${onOrBefore} and are aged
 * Below 1 years
 * @return the cohort definition
 *//*

	public CohortDefinition givenDptHepHibAndAgedLessThan1Year(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenDptHepHib", ReportUtils.map(everGivenDptHepHibVaccine(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenDptHepHib AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Dpt-Hep-Hib between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition givenDptHepHibAndAged1YearAndAbove(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenDptHepHib", ReportUtils.map(everGivenDptHepHibVaccine(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenDptHepHib AND Age1YearAndAbove AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Pneumococcal between dates ${onOrAfter} and ${onOrBefore} and are aged
 * Below 1 years
 * @return the cohort definition
 *//*

	public CohortDefinition givenPneumococcalAndAgedLessThan1Year(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenPneumococcal", ReportUtils.map(everGivenPneumococcalVaccine(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenPneumococcal AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Pneumococcal between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition givenPneumococcalAndAged1YearAndAbove(Integer sequenceNumber){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenPneumococcal", ReportUtils.map(everGivenPneumococcalVaccine(sequenceNumber)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenPneumococcal AND Age1YearAndAbove AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Measles Rubella 1 between dates ${onOrAfter} and ${onOrBefore} and are aged
 * Below 1 years
 * @return the cohort definition
 *//*

	public CohortDefinition givenMeaslesRubellaVaccine1AndAgedLessThan1Year(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenMeaslesRubella", ReportUtils.map(everGivenMeaslesRubellaVaccine(1)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenMeaslesRubella AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Measles Rubella between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition givenMeaslesRubellaVaccine1AndAged1YearAndAbove(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenMeaslesRubella", ReportUtils.map(everGivenMeaslesRubellaVaccine(1)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenMeaslesRubella AND Age1YearAndAbove AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given BCG vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * less than 1 year
 * @return the cohort definition
 *//*

	public CohortDefinition givenBcgVaccineAndAgedLessThan1Year(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenBcgVaccine", ReportUtils.map(givenBcgVaccine()));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenBcgVaccine AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given BCG vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition givenBcgVaccineAndAged1YearAndAbove(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenBcgVaccine", ReportUtils.map(givenBcgVaccine()));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenBcgVaccine AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given IPV vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * less than 1 year
 * @return the cohort definition
 *//*

	public CohortDefinition givenIpvAndAgedLessThan1Year(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenIpv", ReportUtils.map(givenIpv()));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenIpv AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given IPV vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition givenIpvAndAged1YearAndAbove(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenIpv", ReportUtils.map(givenIpv()));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenIpv AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Yellow Fever vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * less than 1 year
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenYellowFeverVaccineAndAgedLessThan1Year(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenYellowFeverVaccine", ReportUtils.map(givenYellowFeverVaccine()));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenYellowFeverVaccine AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Yellow Fever vaccine between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenYellowFeverVaccineAndAged1YearAndAbove(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenYellowFeverVaccine", ReportUtils.map(givenYellowFeverVaccine()));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenYellowFeverVaccine AND Age1YearAndAbove AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Measles Rubella vaccine between dates ${onOrAfter} and ${onOrBefore}
 * and are aged 18 to 24 Months
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenMeaslesRubellaVaccine2AndAged18To24Months(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenMeaslesRubellaVaccine", ReportUtils.map(everGivenMeaslesRubellaVaccine(2)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age18To24Months", ReportUtils.map(age18To24Months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenMeaslesRubellaVaccine AND Age18To24Months AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Measles Rubella vaccine between dates ${onOrAfter} and ${onOrBefore}
 * and are aged Over 2 years
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenMeaslesRubellaVaccine2AndAgedOver2Years(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenMeaslesRubellaVaccine", ReportUtils.map(everGivenMeaslesRubellaVaccine(2)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeOver2Years", ReportUtils.map(ageOver2Years(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenMeaslesRubellaVaccine AND AgeOver2Years AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were Fully Immunized between dates ${onOrAfter} and ${onOrBefore} and are aged
 * less than 1 year
 * @return the cohort definition
 *//*

	public CohortDefinition fullyImmunizedAndAgedLessThan1Year(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("FullyImmunized", ReportUtils.map(fullyImmunizedChildren()));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("FullyImmunized AND AgeLessThan1Year");

		return cd;

	}

	*/
/**
 * Children who were Fully Immunized between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @return the cohort definition
 *//*

	public CohortDefinition fullyImmunizedAndAged1YearAndAbove(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("FullyImmunized", ReportUtils.map(fullyImmunizedChildren()));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("FullyImmunized AND Age1YearAndAbove");

		return cd;

	}


	*/
/**
 * Children who were given Vitamin A at 6 Months between dates ${onOrAfter} and ${onOrBefore} and are aged
 * less than 1 year
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenVitAAt6MAndAgedLessThan1Year(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenVitAAt6M", ReportUtils.map(everGivenVitaminA(1)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("AgeLessThan1Year", ReportUtils.map(ageLessThan12months(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenVitAAt6M AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Vitamin A at 6 Months between dates ${onOrAfter} and ${onOrBefore} and are aged
 * 1 year or above
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenVitAAt6MAndAged1YearAndAbove(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenVitAAt6M", ReportUtils.map(everGivenVitaminA(1)));
		cd.addSearch("VaccinatedBetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("Age1YearAndAbove", ReportUtils.map(age1YearAndAbove(), "effectiveDate=${onOrBefore}"));

		cd.setCompositionString("GivenVitAAt6M AND AgeLessThan1Year AND VaccinatedBetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Vitamin A at 12 months between dates ${onOrAfter} and ${onOrBefore}
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenVitAAt12Months(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenVitAAt12M", ReportUtils.map(everGivenVitaminA(2)));
		cd.addSearch("GivenVitaminABetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));

		cd.setCompositionString("GivenVitAAt12M AND GivenVitaminABetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Vitamin A at 18 months between dates ${onOrAfter} and ${onOrBefore}
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenVitAAt18Months(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenVitAAt18M", ReportUtils.map(everGivenVitaminA(3)));
		cd.addSearch("GivenVitaminABetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));

		cd.setCompositionString("GivenVitAAt18M AND GivenVitaminABetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Vitamin A at 2 Years between dates ${onOrAfter} and ${onOrBefore}
 * @param sequenceNumber
 * @return the cohort definition
 *//*

	public CohortDefinition givenVitAAt2Years(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenVitAAt2Years", ReportUtils.map(everGivenVitaminA(4)));
		cd.addSearch("GivenVitaminABetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));

		cd.setCompositionString("GivenVitAAt2Years AND GivenVitaminABetweenDates");

		return cd;

	}

	*/
/**
 * Children who were given Vitamin A at between 24 and 60 Months between dates ${onOrAfter} and ${onOrBefore}
 * @return the cohort definition
 *//*

	public CohortDefinition givenVitAAt2To5Years(){

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter","From Date",Date.class));
		cd.addParameter(new Parameter("onOrBefore","To Date",Date.class));
		cd.addSearch("GivenVitAAt2Years", ReportUtils.map(everGivenVitaminA(4)));
		cd.addSearch("GivenVitAAtOver2To5Years", ReportUtils.map(everGivenVitaminA(5)));
		cd.addSearch("GivenVitaminABetweenDates", ReportUtils.map(vaccinatedBetweenDates(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));

		cd.setCompositionString("GivenVitAAt2Years AND GivenVitAAtOver2To5Years AND GivenVitaminABetweenDates");

		return cd;

	}

	*/

	/**
	 * Children who were vaccinated between dates ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 *//*

	public CohortDefinition vaccinatedBetweenDates() {
		Concept immunizationsConcept = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);

		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("Vaccinated between dates");
		cd.setQuestion(immunizationsConcept);
		cd.setTimeModifier(TimeModifier.ANY);
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		return cd;
	}
*/

	//Queries for MOH710
	/*Given BCG*/
	public CohortDefinition givenBCGVaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.BCG) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("BCG");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given BCG");

		return cd;
	}

	/*Given OPV at birth*/
	public CohortDefinition givenOPVCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.OPV_birth) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("OPV-0");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given OPV at birth");

		return cd;
	}

	/*Given OPV 1*/
	public CohortDefinition givenOPV1Cl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.OPV_1) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("OPV-1");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given OPV 1");

		return cd;
	}

	/*Given OPV 2*/
	public CohortDefinition givenOPV2Cl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.OPV_2) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("OPV-2");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given OPV 2");

		return cd;
	}

	/*Given OPV 3*/
	public CohortDefinition givenOPV3Cl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.OPV_3) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("OPV-3");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given OPV 3");

		return cd;
	}

	/*Given IPV*/
	public CohortDefinition givenIpvCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.IPV) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("IPV");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given IPV");

		return cd;
	}

	/*Given Dpt-Hep-Hib 1*/
	public CohortDefinition givenDptHepHibVaccine1Cl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.DPT_Hep_B_Hib_1) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("DHH-1");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Dpt-Hep-Hib 1");

		return cd;
	}

	/*Given Dpt-Hep-Hib 2*/
	public CohortDefinition givenDptHepHibVaccine2Cl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.DPT_Hep_B_Hib_2) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("DHH-2");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Dpt-Hep-Hib 2");

		return cd;
	}

	/*Given Dpt-Hep-Hib 3*/
	public CohortDefinition givenDptHepHibVaccine3Cl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.DPT_Hep_B_Hib_3) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("DHH-3");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Dpt-Hep-Hib 3");

		return cd;
	}

	/*Given Pneumococcal 1*/
	public CohortDefinition givenPneumococcal1VaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.PCV_10_1) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("PCV-1");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Pneumococcal 1");

		return cd;
	}

	/*Given Pneumococcal 2*/
	public CohortDefinition givenPneumococcal2VaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.PCV_10_2) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("PCV-2");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Pneumococcal 2");

		return cd;
	}

	/*Given Pneumococcal 3*/
	public CohortDefinition givenPneumococcal3VaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.PCV_10_3) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("PCV-3");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Pneumococcal 3");

		return cd;
	}

	/*Given Rota 1 vaccine*/
	public CohortDefinition givenRota1VirusVaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.ROTA_1) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("ROTA-1");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Rota 1 vaccine");

		return cd;
	}

	/*Given Rota 2 vaccine*/
	public CohortDefinition givenRota2VirusVaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.ROTA_2) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("ROTA-2");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Rota 2 vaccine");

		return cd;
	}

	/*Given Rota 3 vaccine*/
	public CohortDefinition givenRota3VirusVaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.ROTA_3) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("ROTA-3");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Rota 3 vaccine");

		return cd;
	}

	/*Given Vitamin A at 6 Months*/
	public CohortDefinition givenVitAAt6MAgeCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.VitaminA_6_months) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("VA6M");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Vitamin A at 6 Months");

		return cd;
	}

	/*Given Yellow Fever vaccine*/
	public CohortDefinition givenYellowFeverVaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id,i.Measles_rubella_1 from kenyaemr_etl.etl_hei_immunization i where date(i.Yellow_fever) between date(:startDate) and date(:endDate);";

		cd.setName("YF");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Yellow Fever vaccine");

		return cd;
	}

	/*Given Measles-Rubella 1 vaccine*/
	public CohortDefinition givenMeaslesRubella1VaccineCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.Measles_rubella_1) between date(:startDate) and date(:endDate);";

		cd.setName("MR-1");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Measles-Rubella 1 vaccine");

		return cd;
	}

	/*Fully immunized child*/
	public CohortDefinition fullyImmunizedCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.fully_immunized) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("FIC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Fully immunized child");

		return cd;
	}

	/*Given Vitamin A at 1 years (200,000IU)*/
	public CohortDefinition givenVitAAt12MonthsCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.VitaminA_1_yr) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("VA-1Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Vitamin A at 1 years (200,000IU)");

		return cd;
	}

	/*Given Vitamin A at 2 years to 5 years (200,000IU)*/
	public CohortDefinition givenVitAAt18MonthsCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.VitaminA_1_and_half_yr) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("VA-2Y-5Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Vitamin A at 2 years to 5 years (200,000IU)");

		return cd;
	}

	/*Given Measles - Rubella 2(at 1 1/2 - 2 years)*/
	public CohortDefinition givenVitAAt2To5YearsCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.VitaminA_2_to_5_yr) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("MR-2-1.5Y>2Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Measles - Rubella 2(at 1 1/2 - 2 years)");

		return cd;
	}

	/*Measles - Rubella 2(at 1 1/2 - 2 years)*/
	public CohortDefinition givenMeaslesRubella2VaccineAge18To24MonthsCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.Measles_rubella_2) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("MR-2-1.5Y>2Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given Measles - Rubella 2(at 1 1/2 - 2 years)");

		return cd;
	}

	/*Measles-Rubella 2 Above 2 years*/
	public CohortDefinition givenMeaslesRubellaVaccine2AndAgedOver2YearsCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_hei_immunization i where date(i.Measles_rubella_2) between date(:startDate) and date(:endDate) group by i.patient_id;";

		cd.setName("MR-2->2Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Measles-Rubella 2 Above 2 years");

		return cd;
	}

	/*Tetanus Toxoid for pregnant women first dose*/
	public CohortDefinition givenTTXFirstDoseCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = ";";

		cd.setName("TTX_FPW-1st");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Tetanus Toxoid for pregnant women first dose");

		return cd;
	}

	/*Tetanus Toxoid for pregnant women second dose*/
	public CohortDefinition givenTTXSecondDoseCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = ";";

		cd.setName("TTX_FPW-2nd");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Tetanus Toxoid for pregnant women second dose");

		return cd;
	}

	/*Tetanus Toxoid plus(Booster) for pregnant women*/
	public CohortDefinition givenTTXPlusCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = ";";

		cd.setName("TTX_FPW-TTPLUS");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Tetanus Toxoid plus(Booster) for pregnant women");

		return cd;
	}

	/*Given Vitamin A supplement 2 -5 years (200,000 IU)*/
	public CohortDefinition givenVitASupplementalCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = ";";

		cd.setName("VA-2-5Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("2 -5 years (200,000 IU)");

		return cd;
	}

	/*Vitamin A Supplemental Lactating Mothers(200,000 IU)*/
	public CohortDefinition givenVitASupplementalLacCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = ";";

		cd.setName("VA-LAC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Vitamin A Supplemental Lactating Mothers(200,000 IU)");

		return cd;
	}

	/*Issued with LLITN in this Visit (under 1 year)*/
	public CohortDefinition givenLLITNCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = ";";

		cd.setName("LLITN-LT1Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Issued with LLITN in this Visit (under 1 year)");

		return cd;
	}

	/*Squint/White Eye Reflection under 1 year*/
	public CohortDefinition squintWhiteEyeReflectionCl() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sqlQuery = ";";

		cd.setName("SER-<1Y");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Squint/White Eye Reflection under 1 year");

		return cd;
	}
}
