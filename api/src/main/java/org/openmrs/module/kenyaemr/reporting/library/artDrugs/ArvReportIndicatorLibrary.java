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

package org.openmrs.module.kenyaemr.reporting.library.artDrugs;

import org.openmrs.Concept;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of ART Drugs related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ArvReportIndicatorLibrary {

	@Autowired
	ArvReportCohortLibrary arvReportCohortLibrary;

	/**
	 * Number of patients having ART regimen
	 * @return indicator for AZT+3TC+NVP
	 */
	public CohortIndicator onRegimenAzt3tcNvp() {

		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept nvp = Dictionary.getConcept(Dictionary.NEVIRAPINE);
		return cohortIndicator("AZT+3TC+NVP", map(arvReportCohortLibrary.onRegimen(Arrays.asList(azt, tc3, nvp)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for AZT+3TC+EFV
	 */
	public CohortIndicator onRegimenAzt3tcEfv() {
		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept efv = Dictionary.getConcept(Dictionary.EFAVIRENZ);
		return  cohortIndicator("AZT+3TC+EFV", map(arvReportCohortLibrary.onRegimen(Arrays.asList(azt, tc3, efv)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for AZT+3TC+ABC
	 */
	public CohortIndicator onRegimenAzt3tcAbc() {
		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		return cohortIndicator("AZT+3TC+ABC", map(arvReportCohortLibrary.onRegimen(Arrays.asList(azt, tc3, abc)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for TDF+3TC+NVP
	 */
	public CohortIndicator onRegimenTdf3tcNvp() {
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept nvp = Dictionary.getConcept(Dictionary.NEVIRAPINE);
		return cohortIndicator("TDF+3TC+NVP", map(arvReportCohortLibrary.onRegimen(Arrays.asList(tdf, tc3, nvp)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for TDF+3TC+EFV
	 */
	public CohortIndicator onRegimenTdf3tcEfv() {
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept efv = Dictionary.getConcept(Dictionary.EFAVIRENZ);
		return cohortIndicator("TDF+3TC+EFV", map(arvReportCohortLibrary.onRegimen(Arrays.asList(tdf, tc3, efv)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for TDF+3TC+AZT
	 */
	public CohortIndicator onRegimenTdf3tcAzt() {
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		return cohortIndicator("TDF+3TC+AZT", map(arvReportCohortLibrary.onRegimen(Arrays.asList(tdf, tc3, azt)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for ABC+3TC+NVP
	 */
	public CohortIndicator onRegimenAbc3tcNvp() {
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept nvp = Dictionary.getConcept(Dictionary.NEVIRAPINE);
		return cohortIndicator("ABC+3TC+NVP", map(arvReportCohortLibrary.onRegimen(Arrays.asList(abc, tc3, nvp)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for ABC+3TC+EFV
	 */
	public CohortIndicator onRegimenAbc3tcEfv() {
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept efv = Dictionary.getConcept(Dictionary.EFAVIRENZ);
		return cohortIndicator("ABC+3TC+EFV", map(arvReportCohortLibrary.onRegimen(Arrays.asList(abc, tc3, efv)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for D4T+3TC+NVP
	 */
	public CohortIndicator onRegimenD4t3tcNvp() {
		Concept d4t = Dictionary.getConcept(Dictionary.STAVUDINE);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept nvp = Dictionary.getConcept(Dictionary.NEVIRAPINE);
		return cohortIndicator("D4T+3TC+NVP", map(arvReportCohortLibrary.onRegimen(Arrays.asList(d4t, tc3, nvp)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for D4T+3TC+EFV
	 */
	public CohortIndicator onRegimenD4t3tcEfv() {
		Concept d4t = Dictionary.getConcept(Dictionary.STAVUDINE);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept efv = Dictionary.getConcept(Dictionary.EFAVIRENZ);
		return cohortIndicator("D4T+3TC+EFV", map(arvReportCohortLibrary.onRegimen(Arrays.asList(d4t, tc3, efv)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for D4T+3TC+ABC
	 */
	public CohortIndicator onRegimenD4t3tcAbc() {
		Concept d4t = Dictionary.getConcept(Dictionary.STAVUDINE);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		return cohortIndicator("D4T+3TC+ABC", map(arvReportCohortLibrary.onRegimen(Arrays.asList(d4t, tc3, abc)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for ABC+3TC+LVP/r
	 */
	public CohortIndicator onRegimenAbc3TcLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		return cohortIndicator("ABC+3TC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(abc,tc3,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for AZT+3TC+LVP/r
	 */
	public CohortIndicator onRegimenAzt3TcLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		return cohortIndicator("AZT+3TC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(azt,tc3,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for TDF+3TC+LVP/r
	 */
	public CohortIndicator onRegimenTdf3TcLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		return cohortIndicator("TDF+3TC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(tdf,tc3,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for TDF+ABC+LVP/r
	 */
	public CohortIndicator onRegimenTdfAbcLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		return cohortIndicator("TDF+ABC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(tdf,abc,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for ABC+DDI+LVP/r
	 */
	public CohortIndicator onRegimenTdfAbcDdiLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		Concept ddi = Dictionary.getConcept(Dictionary.DIDANOSINE);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		return cohortIndicator("ABC+DDI+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(abc,ddi,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for D4T+3TC+LVP/r
	 */
	public CohortIndicator onRegimenD4t3tcLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept d4t = Dictionary.getConcept(Dictionary.STAVUDINE);
		Concept t3c = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		return cohortIndicator("D4T+3TC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(d4t,t3c,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for AZT+TDF+3TC+LVP/r
	 */
	public CohortIndicator onRegimenAztTdf3tcLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		Concept t3c = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		return cohortIndicator("AZT+TDF+3TC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(azt,tdf,t3c,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for ABC+TDF+3TC+LVP/r
	 */
	public CohortIndicator onRegimenAbcTdf3tcLvpR() {
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		Concept t3c = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		return cohortIndicator("ABC+TDF+3TC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(abc,tdf,t3c,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for ETR+RAL+DRV+RIT
	 */
	public CohortIndicator onRegimenEtrRalDrvRit() {
		Concept etr = Dictionary.getConcept(Dictionary.ETRAVIRINE);
		Concept ral = Dictionary.getConcept(Dictionary.Raltegravir);
		Concept drv = Dictionary.getConcept(Dictionary.DARUNAVIR);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		return cohortIndicator("ETR+RAL+DRV+RIT", map(arvReportCohortLibrary.onRegimen(Arrays.asList(etr,ral,drv,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients having ART regimen
	 * @return indicator for ETR+TDF+3TC+LVP/r
	 */
	public CohortIndicator onRegimenEtrTdf3tcLvpR() {
		Concept etr = Dictionary.getConcept(Dictionary.ETRAVIRINE);
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		Concept t3c = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		Concept lvp = Dictionary.getConcept((Dictionary.LOPINAVIR));
		return cohortIndicator("ETR+TDF+3TC+LVP/r", map(arvReportCohortLibrary.onRegimen(Arrays.asList(etr,tdf,t3c,lvp,rit)), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
}
