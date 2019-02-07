/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH710;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Moh710IndicatorLibrary {

	@Autowired
	private Moh710CohortLibrary moh710CohortLibrary;
	
/*	public CohortIndicator givenRotaVirusVaccineAgeLessThan1Year(Integer sequenceNumber) {
		
		return cohortIndicator("Given Rota Virus Vaccine 1 Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenRotaVirusVaccineAndAgedLessThan1Year(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenRotaVirusVaccineAge1YearAndAbove(Integer sequenceNumber) {
		
		return cohortIndicator("Given Rota Virus Vaccine 1 Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenRotaVirusVaccineAndAged1YearAndAbove(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}

	public CohortIndicator givenOPVAgeLessThan1Year(Integer sequenceNumber) {
		
		return cohortIndicator("Given OPV Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenOPVAndAgedLessThan1Year(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenOPVAge1YearAndAbove(Integer sequenceNumber) {
		
		return cohortIndicator("Given OPV At birth Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenOPVAndAged1YearAndAbove(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}	
	
	
	public CohortIndicator givenDptHepHibVaccineAgeLessThan1Year(Integer sequenceNumber) {
		
		return cohortIndicator("Given DPT-HEP-HIB Vaccine Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenDptHepHibAndAgedLessThan1Year(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenDptHepHibVaccineAge1YearAndAbove(Integer sequenceNumber) {
		
		return cohortIndicator("Given DPT-HEP-HIB Vaccine Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenDptHepHibAndAged1YearAndAbove(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}
		
	public CohortIndicator givenPneumococcalVaccineAgeLessThan1Year(Integer sequenceNumber) {
		
		return cohortIndicator("Given Pneumococcal Vaccine Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenPneumococcalAndAgedLessThan1Year(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenPneumococcalVaccineAge1YearAndAbove(Integer sequenceNumber) {
		
		return cohortIndicator("Given Pneumococcal Vaccine Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenPneumococcalAndAged1YearAndAbove(sequenceNumber), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}

	public CohortIndicator givenMeaslesRubellaVaccine1AgeLessThan1Year() {
		
		return cohortIndicator("Given Measles-Rubella Vaccine Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenMeaslesRubellaVaccine1AndAgedLessThan1Year(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenMeaslesRubellaVaccine1Age1YearAndAbove() {
		
		return cohortIndicator("Given Measles-Rubella Vaccine Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenMeaslesRubellaVaccine1AndAged1YearAndAbove(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}
	
	public CohortIndicator givenVitAAt6MAgeLessThan1Year() {
		
		return cohortIndicator("Given VitA At 6M Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenVitAAt6MAndAgedLessThan1Year(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenVitAAt6MAge1YearAndAbove() {
		
		return cohortIndicator("Given Vit A At 6M Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenVitAAt6MAndAged1YearAndAbove(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}

	public CohortIndicator givenVitAAt12Months() {
		
		return cohortIndicator("Given Vit A At 12 Months", ReportUtils.map(moh710CohortLibrary.givenVitAAt12Months(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}
	
	public CohortIndicator givenVitAAt18Months() {
		
		return cohortIndicator("Given Vit A At 18 Months", ReportUtils.map(moh710CohortLibrary.givenVitAAt18Months(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}
	
	public CohortIndicator givenVitAAt2To5Years() {
		
		return cohortIndicator("Given Vit A At 2 to 5 years", ReportUtils.map(moh710CohortLibrary.givenVitAAt2To5Years(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}
	
	*//*public CohortIndicator givenBCGVaccine() {
		
		return cohortIndicator("Given BCG Vaccine Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenBcgVaccineAndAgedLessThan1Year(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}*//*
	*//**
	 * Number of patients who tested for HIV in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 * Initial test at ANC  HV02-04
	 *
	 * @return the indicator
	 *//*
	public CohortIndicator initialHIVTestInMchmsAntenatal() {
		return cohortIndicator(null,
				map(moh731Cohorts.initialHIVTestInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	public CohortIndicator givenBCGVaccineAge1YearAndAbove() {
		
		return cohortIndicator("Given BCG Vaccine Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenBcgVaccineAndAged1YearAndAbove(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}

	public CohortIndicator givenIpvAgeLessThan1Year() {
		
		return cohortIndicator("Given IPV Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenIpvAndAgedLessThan1Year(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenIpvAge1YearAndAbove() {
		
		return cohortIndicator("Given IPV Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenIpvAndAged1YearAndAbove(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}
	
	
	public CohortIndicator givenYellowFeverVaccineAgeLessThan1Year() {
		
		return cohortIndicator("Given Yellow Fever Vaccine Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenYellowFeverVaccineAndAgedLessThan1Year(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenYellowFeverVaccineAge1YearAndAbove() {
		
		return cohortIndicator("Given Yellow Fever Vaccine Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.givenYellowFeverVaccineAndAged1YearAndAbove(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}

	public CohortIndicator givenMeaslesRubella2VaccineAge18To24Months() {
		
		return cohortIndicator("Given Measles Rubella Vaccine Age 18 to 24 Months", ReportUtils.map(moh710CohortLibrary.givenMeaslesRubellaVaccine2AndAged18To24Months(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator givenMeaslesRubellaVaccine2AndAgedOver2Years() {
		
		return cohortIndicator("Given Measles Rubella Vaccine Age above 2 years", ReportUtils.map(moh710CohortLibrary.givenMeaslesRubellaVaccine2AndAgedOver2Years(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}
	
	
	public CohortIndicator fullyImmunizedAgeLessThan1Year() {
		
		return cohortIndicator("Fully Immunized Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.fullyImmunizedAndAgedLessThan1Year(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	} 

	public CohortIndicator fullyImmunizedAge1YearAndAbove() {
		
		return cohortIndicator("Fully Immunized Age 1 Year and above", ReportUtils.map(moh710CohortLibrary.fullyImmunizedAndAged1YearAndAbove(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
	}*/
	//Indicator Libraries based on Queries and MOH710 dimensions

	/*Given BCG*/
	public CohortIndicator givenBCGVaccine() {

		return cohortIndicator("Given BCG",map(moh710CohortLibrary.givenBCGVaccineCl(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/*Given OPV*/
	public CohortIndicator givenOPV() {

		return cohortIndicator("Given OPV at Birth",map(moh710CohortLibrary.givenOPVCl(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/*Given OPV1*/
	public CohortIndicator givenOPV1() {

		return cohortIndicator("Given OPV1", map(moh710CohortLibrary.givenOPV1Cl(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/*Given OPV2*/
	public CohortIndicator givenOPV2() {

			return cohortIndicator("Given OPV2",map(moh710CohortLibrary.givenOPV2Cl(), "startDate=${startDate},endDate=${endDate}")
			);
		}

	/*Given OPV3*/
	public CohortIndicator givenOPV3() {

		return cohortIndicator("Given OPV3",map(moh710CohortLibrary.givenOPV3Cl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given IPV*/
	public CohortIndicator givenIpv() {

		return cohortIndicator("Given IPV",map(moh710CohortLibrary.givenIpvCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Dpt-Hep-Hib 1 Vaccine*/
	public CohortIndicator givenDptHepHibVaccine1() {

		return cohortIndicator("Given Dpt-Hep-Hib 1",map(moh710CohortLibrary.givenDptHepHibVaccine1Cl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Given Dpt-Hep-Hib 2 vaccine*/
	public CohortIndicator givenDptHepHibVaccine2() {

		return cohortIndicator("Given Dpt-Hep-Hib 2",map(moh710CohortLibrary.givenDptHepHibVaccine2Cl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Given Dpt-Hep-Hib 3 vaccine*/
	public CohortIndicator givenDptHepHibVaccine3() {

		return cohortIndicator("Given Dpt-Hep-Hib 3",map(moh710CohortLibrary.givenDptHepHibVaccine3Cl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Pneumococcal 1*/
	public CohortIndicator givenPneumococcal1Vaccine() {

		return cohortIndicator("Given Pneumococcal 1",map(moh710CohortLibrary.givenPneumococcal1VaccineCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Pneumococcal 2*/
	public CohortIndicator givenPneumococcal2Vaccine() {

		return cohortIndicator("Given Pneumococcal 2",map(moh710CohortLibrary.givenPneumococcal2VaccineCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Pneumococcal 3*/
	public CohortIndicator givenPneumococcal3Vaccine() {

		return cohortIndicator("Given Pneumococcal 3",map(moh710CohortLibrary.givenPneumococcal3VaccineCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Rota 1*/
	public CohortIndicator givenRota1VirusVaccine() {

		return cohortIndicator("Given Rota 1",map(moh710CohortLibrary.givenRota1VirusVaccineCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Rota 2*/
	public CohortIndicator givenRota2VirusVaccine() {

		return cohortIndicator("Given Rota 2",map(moh710CohortLibrary.givenRota2VirusVaccineCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Vitamin A at 6 Months*/
	public CohortIndicator givenVitAAt6MAge() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenVitAAt6MAgeCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Yellow Fever vaccine*/
	public CohortIndicator givenYellowFeverVaccine() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenYellowFeverVaccineCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Measles-Rubella 1 vaccine*/
	public CohortIndicator givenMeaslesRubella1Vaccine() {

		return cohortIndicator("Given Measles Rubella 1",map(moh710CohortLibrary.givenMeaslesRubella1VaccineCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Fully immunized child*/
	public CohortIndicator fullyImmunized() {

		return cohortIndicator(null,map(moh710CohortLibrary.fullyImmunizedCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}


	/*Given Vitamin A at 1 years (200,000IU)*/
	public CohortIndicator givenVitAAt12Months() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenVitAAt12MonthsCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Vitamin A at 1 1/2 years (200,000IU)*/
	public CohortIndicator givenVitAAt18Months() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenVitAAt18MonthsCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Given Vitamin A at 2 years to 5 years (200,000IU)*/
	public CohortIndicator givenVitAAt2To5Years() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenVitAAt2To5YearsCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Vitamin A Supplemental Lactating Mothers(200,000 IU)*/
	public CohortIndicator givenVitASupplementalLac() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenVitASupplementalLacCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Measles - Rubella 2(at 1 1/2 - 2 years)*/
	public CohortIndicator givenMeaslesRubella2VaccineAge18To24Months() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenMeaslesRubella2VaccineAge18To24MonthsCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Measles-Rubella 2 Above 2 years*/
	public CohortIndicator givenMeaslesRubellaVaccine2AndAgedOver2Years() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenMeaslesRubellaVaccine2AndAgedOver2YearsCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Tetanus Toxoid for pregnant women first dose*/
	public CohortIndicator givenTTXFirstDose() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenTTXFirstDoseCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Tetanus Toxoid for pregnant women second dose*/
	public CohortIndicator givenTTXSecondDose() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenTTXSecondDoseCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Tetanus Toxoid plus(Booster) for pregnant women*/
	public CohortIndicator givenTTXPlus() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenTTXPlusCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}
//Adverse events following immunization

	/*2 -5 years (200,000 IU)*/
	public CohortIndicator givenVitASupplemental() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenVitASupplementalCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Issued with LLITN in this Visit (under 1 year)*/
	public CohortIndicator givenLLITN() {

		return cohortIndicator(null,map(moh710CohortLibrary.givenLLITNCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}

	/*Squint/White Eye Reflection under 1 year*/
		public CohortIndicator squintWhiteEyeReflection() {

		return cohortIndicator(null,map(moh710CohortLibrary.squintWhiteEyeReflectionCl(), "startDate=${startDate},endDate=${endDate}")
			);
	}
}


