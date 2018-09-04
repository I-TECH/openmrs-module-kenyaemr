/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.moh710;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Moh710IndicatorLibrary {

	@Autowired
	private Moh710CohortLibrary moh710CohortLibrary;
	
	public CohortIndicator givenRotaVirusVaccineAgeLessThan1Year(Integer sequenceNumber) {
		
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
	
	public CohortIndicator givenBCGVaccineAgeLessThan1Year() {
		
		return cohortIndicator("Given BCG Vaccine Age Less Than 1 Year", ReportUtils.map(moh710CohortLibrary.givenBcgVaccineAndAgedLessThan1Year(), "onOrAfter=${startDate - 1d},onOrBefore=${endDate + 1d}"));
		
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
		
	}	
}
