/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.moh711;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Indicators specific to the MOH711 report
 */
@Component
public class Moh711IndicatorLibrary {

	@Autowired
	private Moh711CohortLibrary moh711Cohorts;

	/**
	 * No.of New ANC Clients (First ANC visit)
	 * @return the indicator
	 */
	public CohortIndicator noOfNewANCClients() {
		return cohortIndicator("Number of new ANC clients (First ANC Visit)", map(moh711Cohorts.noOfANCClients(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of revisiting ANC Clients
	 * @return the indicator
	 */
	public CohortIndicator noOfANCClientsRevisits() {
		return cohortIndicator("Number of revisiting ANC clients", ReportUtils.map(moh711Cohorts.noOfANCClientsRevisits(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of Clients given IPT (1st dose)
	 */
	public CohortIndicator noOfANCClientsGivenIPT1stDose() {
		return cohortIndicator("No.of Clients given IPT (1st dose)", ReportUtils.map(moh711Cohorts.noOfANCClientsGivenIPT1stDose(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of Clients given IPT (2nd dose)
	 */
	public CohortIndicator noOfANCClientsGivenIPT2ndDose() {
		return cohortIndicator("No.of Clients given IPT (2nd dose)", ReportUtils.map(moh711Cohorts.noOfANCClientsGivenIPT2ndDose(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 *No.of Clients given IPT (3rd dose)
	 * @return
	 */
	public CohortIndicator noOfANCClientsGivenIPT3rdDose() {
		return cohortIndicator("No.of Clients given IPT (3rd dose)", ReportUtils.map(moh711Cohorts.noOfANCClientsGivenIPT3rdDose(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of Clients with Hb < 11 g/dl
	 */
	public CohortIndicator noOfANCClientsLowHB() {
		return cohortIndicator("No.of Clients with Hb < 11 g/dl", ReportUtils.map(moh711Cohorts.noOfANCClientsLowHB(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of Clients completed 4 Antenatal Visits
	 */
	public CohortIndicator ancClientsCompleted4Visits() {
		return cohortIndicator("No.of Clients completed 4 Antenatal Visits", ReportUtils.map(moh711Cohorts.ancClientsCompleted4Visits(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.LLINs distributed to under 1 year
	 */
	public CohortIndicator distributedLLINsUnder1Year() {
		return cohortIndicator("No.of Clients completed 4 Antenatal Visits", ReportUtils.map(moh711Cohorts.distributedLLINsUnder1Year(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of LLINs distributed to ANC clients
	 */
	public CohortIndicator distributedLLINsToANCClients() {
		return cohortIndicator("No.of LLINs distributed to ANC clients", ReportUtils.map(moh711Cohorts.distributedLLINsToANCClients(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of clients Tested for Syphilis
	 */
	public CohortIndicator ancClientsTestedForSyphillis() {
		return cohortIndicator("No.of clients Tested for Syphilis", ReportUtils.map(moh711Cohorts.ancClientsTestedForSyphillis(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of clients Tested Positive for Syphilis
	 */
	public CohortIndicator ancClientsTestedSyphillisPositive() {
		return cohortIndicator("No.of clients Tested Positive for Syphilis", ReportUtils.map(moh711Cohorts.ancClientsTestedSyphillisPositive(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *Total women done breast examination
	 */
	public CohortIndicator breastExaminationDone() {
		return cohortIndicator("Total women done breast examination", ReportUtils.map(moh711Cohorts.breastExaminationDone(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of Women presenting with pregnancy at 1ST ANC in the First Trimeseter(<= 12 Weeks)
	 * @return
	 */
	public CohortIndicator presentingPregnancy1stANC1stTrimester() {
		return cohortIndicator("No.of youth (20-24 years) presenting with pregnancy at 1st ANC Visit", ReportUtils.map(moh711Cohorts.presentingPregnancy1stANC1stTrimester(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of clients issued with Iron
	 * @return
	 */
	public CohortIndicator ancClientsIssuedWithIron() {
		return cohortIndicator("No.of clients issued with Iron", ReportUtils.map(moh711Cohorts.ancClientsIssuedWithIron(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of clients issued with Folic
	 * @return
	 */
	public CohortIndicator ancClientsIssuedWithFolic() {
		return cohortIndicator("No.of clients issued with Folic", ReportUtils.map(moh711Cohorts.ancClientsIssuedWithFolic(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of clients issued with Combined Ferrous Folate
	 * @return
	 */
	public CohortIndicator ancClientsIssuedWithFerrousFolic() {
		return cohortIndicator("No.of clients issued with Combined Ferrous Folate", ReportUtils.map(moh711Cohorts.ancClientsIssuedWithFerrousFolic(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of pregnant women presenting in ANC with complication associated with FGM
	 * @return
	 */
	public CohortIndicator ancClientsWithFGMRelatedComplications() {
		return cohortIndicator("No.of pregnant women presenting in ANC with complication associated with FGM", ReportUtils.map(moh711Cohorts.ancClientsWithFGMRelatedComplications(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.screened for cacx during ANC
	 */
	public CohortIndicator cacxScreened() {
		return cohortIndicator("No.screened for cacx during ANC", map(moh711Cohorts.cacxScreened(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.screened for cacx VIA/VILI positive
	 */
	public CohortIndicator viaViliPositive() {
		return cohortIndicator("No.screened for cacx during ANC", map(moh711Cohorts.viaViliPositive(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.screened for cacx HPV positive
	 */
	public CohortIndicator hpvPositive() {
		return cohortIndicator("No.screened for cacx during ANC", map(moh711Cohorts.hpvPositive(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.screened for cacx has suspicious cancer lessions
	 */
	public CohortIndicator suspiciousCancerLessions() {
		return cohortIndicator("No.screened for cacx during ANC", map(moh711Cohorts.suspiciousCancerLessions(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.screened for cacx treated using cryotherapy method
	 */
	public CohortIndicator treatedUsingCyrotherapy() {
		return cohortIndicator("No.screened for cacx during ANC", map(moh711Cohorts.treatedUsingCyrotherapy(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.screened for cacx treated using LEEP method
	 */
	public CohortIndicator treatedUsingLEEP() {
		return cohortIndicator("No.screened for cacx during ANC", map(moh711Cohorts.treatedUsingLEEP(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No. HIV Positive mothers screened for cacx
	 */
	public CohortIndicator cacxScreenedAndHIVPositive() {
		return cohortIndicator("No. HIV Positive mothers screened for cacx", map(moh711Cohorts.cacxScreenedAndHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of New PNC Clients (First ANC visit)
	 * @return the indicator
	 */
	public CohortIndicator noOfNewPNCClients() {
		return cohortIndicator("Number of new ANC clients (First ANC Visit)", map(moh711Cohorts.noOfFirstPNCClients(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Mothers received PostParturm care within 48 hrs
	 * @return
	 */
	public CohortIndicator motherPPCWithin48hrs() {
		return cohortIndicator("Mothers received PostParturm care within 48 hrs", ReportUtils.map(moh711Cohorts.motherPPCWithin48hrs(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Mothers received PostParturm care btw 3 days and 6 weeks
	 * @return
	 */
	public CohortIndicator motherPPCbtw3And42Days() {
		return cohortIndicator("Mothers received PostParturm care btw 3 days and 6 weeks", ReportUtils.map(moh711Cohorts.motherPPCbtw3And42Days(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Mothers received PostParturm care after 6 weeks
	 * @return
	 */
	public CohortIndicator motherPPCAfter6weeks() {
		return cohortIndicator("Mothers received PostParturm care after 6 weeks", ReportUtils.map(moh711Cohorts.motherPPCAfter6weeks(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Babies received PostParturm care within 48 hrs
	 * @return
	 */
	public CohortIndicator babyPPCWithin48hrs() {
		return cohortIndicator("Babies received PostParturm care within 48 hrs", ReportUtils.map(moh711Cohorts.babyPPCWithin48hrs(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Babies received PostParturm care btw 3 days and 6 weeks
	 * @return
	 */
	public CohortIndicator babyPPCbtw3And42Days() {
		return cohortIndicator("Babies received PostParturm care btw 3 days and 6 weeks", ReportUtils.map(moh711Cohorts.babyPPCbtw3And42Days(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Babies received PostParturm care after 6 weeks
	 * @return
	 */
	public CohortIndicator babyPPCAfter6weeks() {
		return cohortIndicator("Babies received PostParturm care after 6 weeks", ReportUtils.map(moh711Cohorts.babyPPCAfter6weeks(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of revisiting ANC Clients
	 * @return the indicator
	 */
	public CohortIndicator noOfPNCClientsRevisits() {
		return cohortIndicator("Number of revisiting ANC clients", ReportUtils.map(moh711Cohorts.noOfRevisitingPNCClients(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 *No.of Fistula cases during PNC
	 */
	public CohortIndicator noOfFistulaCasesPNC() {
		return cohortIndicator("No.of Fistula cases during PNC", map(moh711Cohorts.noOfFistulaCasesPNC(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.referred from Community for PNC services
	 */
	public CohortIndicator noReferredFromCommunityForPNC() {
		return cohortIndicator("No.referred from Community for PNC services", map(moh711Cohorts.noReferredFromCommunityForPNC(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.Screened for Pap smear
	 * @return the indicator
	 */
	public CohortIndicator cacxScreenedWithMethod(String conceptName, Integer conceptId) {
		return cohortIndicator("No.Screened for Pap smear", map(moh711Cohorts.cacxScreenedWithMethodAtANC(conceptName,conceptId), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Normal Deliveries
	 * @return the indicator
	 */
	public CohortIndicator normalDelivery(Integer mode) {
		return cohortIndicator("Normal Deliveries", ReportUtils.map(moh711Cohorts.deliveryMethod(mode), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Caesarean Section
	 * @return the indicator
	 */
	public CohortIndicator caesareanSection(Integer mode) {
		return cohortIndicator("Caesarean Sections", ReportUtils.map(moh711Cohorts.deliveryMethod(mode), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Breech Delivery
	 * @return the indicator
	 */
	public CohortIndicator breechDelivery(Integer mode) {
		return cohortIndicator("Breech Delivery", ReportUtils.map(moh711Cohorts.deliveryMethod(mode),"startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Assisted Vaginal Deliveries (Vacuum Extraction)
	 * @return the indicator
	 */
	public CohortIndicator assistedVaginalDelivery(Integer mode) {
		return cohortIndicator("Assisted Vaginal Deliveries (Vacuum Extraction)", ReportUtils.map(moh711Cohorts.deliveryMethod(mode), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Number of Live births
	 * @return the indicator
	 */
	public CohortIndicator liveBirths() {
		return cohortIndicator("Live Births", ReportUtils.map(moh711Cohorts.liveBirths(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No. of Low birth weight Babies (below 2500 grams)
	 * @return
	 */
	public CohortIndicator lowBirthWeight() {
		return cohortIndicator("No. of Low birth weight Babies (below 2500 grams)", ReportUtils.map(moh711Cohorts.lowBirthWeight(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No. of births with deformities
	 * @return
	 */
	public CohortIndicator deformities() {
		return cohortIndicator("No. of births with deformoties", ReportUtils.map(moh711Cohorts.deformities(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No. of neonates given  Vit "K"
	 * @return
	 */
	public CohortIndicator givenVitaminK() {
		return cohortIndicator("No. of births with deformoties", ReportUtils.map(moh711Cohorts.givenVitaminK(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No. of babies applied chlorhexidine for cord care
	 * @return
	 */
	public CohortIndicator chlorhexidineForCordCaregiven() {
		return cohortIndicator("No.of babies applied chlorhexidine for cord care", ReportUtils.map(moh711Cohorts.chlorhexidineForCordCaregiven(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No of neonates 0 -28 days put on Continous Positive Airway Pressure(CPAP)
	 * @return
	 */
	public CohortIndicator continousPositiveAirwayPressureAt0To28Days() {
		return cohortIndicator("No of neonates 0 -28 days put on Continous Positive Airway Pressure(CPAP)", ReportUtils.map(moh711Cohorts.continousPositiveAirwayPressureAt0To28Days(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No. of babies given tetracycline at birth
	 * @return
	 */
	public CohortIndicator givenTetracyclineAtBirth() {
		return cohortIndicator("No. of babies given tetracycline at birth", ReportUtils.map(moh711Cohorts.givenTetracyclineAtBirth(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Pre-Term babies
	 * @return
	 */
	public CohortIndicator preTermBabies() {
		return cohortIndicator("Pre-Term babies", ReportUtils.map(moh711Cohorts.preTermBabies(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of babies discharged alive
	 * @return
	 */
	public CohortIndicator dischargedAlive() {
		return cohortIndicator("No. of babies discharged alive", ReportUtils.map(moh711Cohorts.dischargedAlive(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No. of Infants intiatied on breastfeeding within 1 hour after birth
	 * @return
	 */
	public CohortIndicator initiatedBFWithinOneHour() {
		return cohortIndicator("No. of Infants intiatied on breastfeeding within 1 hour after birth", ReportUtils.map(moh711Cohorts.initiatedBFWithinOneHour(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Total Deliveries from HIV+ mother
	 * @return
	 */
	public CohortIndicator deliveryFromHIVPosMother() {
		return cohortIndicator("Total Deliveries from HIV+ mother", ReportUtils.map(moh711Cohorts.deliveryFromHIVPosMother(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Perinatal Deaths - Fresh still birth
	 * @return
	 */
	public CohortIndicator perinatalFreshStillBirth() {
		return cohortIndicator("Perinatal Deaths - Fresh still birth", ReportUtils.map(moh711Cohorts.perinatalFreshStillBirth(), "startDate=${startDate},endDate=${endDate}"));
	}
		/**
	 * Perinatal Deaths - Macerated still birth
	 * @return
	 */
	public CohortIndicator perinatalMaceratedStillBirth() {
		return cohortIndicator("Perinatal Deaths - Macerated still birth", ReportUtils.map(moh711Cohorts.perinatalMaceratedStillBirth(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Perinatal Deaths - Death 0-7 days
	 * @return
	 */
	public CohortIndicator perinatalDeathWithin0To7Days() {
		return cohortIndicator("Perinatal Deaths - Death 0-7 days", ReportUtils.map(moh711Cohorts.perinatalDeathWithin0To7Days(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Perinatal Deaths - Death 0-28 days
	 * @return
	 */
	public CohortIndicator perinatalDeathWithin0To28Days() {
		return cohortIndicator("Perinatal Deaths - Death 0-28 days", ReportUtils.map(moh711Cohorts.perinatalDeathWithin0To28Days(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Maternal Deaths
	 * @return
	 */
	public CohortIndicator maternalDeath() {
		return cohortIndicator("Maternal Deaths", ReportUtils.map(moh711Cohorts.maternalDeath(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Maternal deaths audited within 7 days
	 * @return
	 */
	public CohortIndicator maternalDeathAuditedWithin7Days() {
		return cohortIndicator("Maternal deaths audited within 7 days", ReportUtils.map(moh711Cohorts.maternalDeathAuditedWithin7Days(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Ante Partum Haemorrhage(APH)
	 * @return
	 */
	public CohortIndicator antePartumHaemorrhage(Integer motherCondition) {
		return cohortIndicator("Ante Partum Haemorrhage(APH)", ReportUtils.map(moh711Cohorts.antePartumHaemorrhage(motherCondition), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Post Partum Haemorrhage(PPH)
	 * @return
	 */
	public CohortIndicator postPartumHaemorrhage(Integer motherCondition) {
		return cohortIndicator("Post Partum Haemorrhage(PPH)", ReportUtils.map(moh711Cohorts.postPartumHaemorrhage(motherCondition), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Eclampsia
	 * @return
	 */
	public CohortIndicator eclampsia(Integer motherCondition) {
		return cohortIndicator("Eclampsia", ReportUtils.map(moh711Cohorts.eclampsia(motherCondition), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Ruptured Uterus
	 * @return
	 */
	public CohortIndicator rupturedUterus(Integer motherCondition) {
		return cohortIndicator("Ruptured Uterus", ReportUtils.map(moh711Cohorts.rupturedUterus(motherCondition), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Obstructed Labour
	 * @return
	 */
	public CohortIndicator obstructedLabour(Integer motherCondition) {
		return cohortIndicator("Obstructed Labour", ReportUtils.map(moh711Cohorts.obstructedLabour(motherCondition), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Sepsis
	 * @return
	 */
	public CohortIndicator sepsis(Integer motherCondition) {
		return cohortIndicator("Sepsis", ReportUtils.map(moh711Cohorts.sepsis(motherCondition), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Normal Weight for Age
	 * @return
	 */
	public CohortIndicator normalWeightForAge() {
		return cohortIndicator("Normal Weight for Age", ReportUtils.map(moh711Cohorts.normalWeightForAge(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Underweight
	 * @return
	 */
	public CohortIndicator underWeight() {
		return cohortIndicator("Underweight", ReportUtils.map(moh711Cohorts.underWeight(), "startDate=${startDate},endDate=${endDate}"));
	}


	/**
	 * Severe Underweight
	 * @return
	 */
	public CohortIndicator severeUnderWeight() {
		return cohortIndicator("Severe Underweight", ReportUtils.map(moh711Cohorts.severeUnderWeight(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Overweight
	 * @return
	 */
	public CohortIndicator overweight() {
		return cohortIndicator("Overweight", ReportUtils.map(moh711Cohorts.overweight(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Obese
	 * @return
	 */
	public CohortIndicator obese() {
		return cohortIndicator("Obese", ReportUtils.map(moh711Cohorts.obese(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * MUAC_Normal(Green)
	 * @return
	 */
	public CohortIndicator normalMUAC() {
		return cohortIndicator("MUAC_Normal(Green)", ReportUtils.map(moh711Cohorts.normalMUAC(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * MUAC_Moderate(Yellow)
	 * @return
	 */
	public CohortIndicator moderateMUAC() {
		return cohortIndicator("MUAC_Moderate(Yellow)", ReportUtils.map(moh711Cohorts.moderateMUAC(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * MUAC_Severe(Red)
	 * @return
	 */
	public CohortIndicator severeMUAC() {
		return cohortIndicator("MUAC_Severe(Red)", ReportUtils.map(moh711Cohorts.severeMUAC(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Stunted
	 * @return
	 */
	public CohortIndicator stuntedGrowth() {
		return cohortIndicator("Stunted", ReportUtils.map(moh711Cohorts.stuntedGrowth(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * New_Enrollment
	 * @return
	 */
	public CohortIndicator newlyEnrolledMchs() {
		return cohortIndicator("New_Enrollment", ReportUtils.map(moh711Cohorts.newlyEnrolledMchs(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Followup_type_Kwashiorkor
	 * @return
	 */
	public CohortIndicator kwashiorkor() {
		return cohortIndicator("Followup_type_Kwashiorkor", ReportUtils.map(moh711Cohorts.kwashiorkor(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Followup_type_Marasmus
	 * @return
	 */
	public CohortIndicator marasmus() {
		return cohortIndicator("Followup_type_Marasmus", ReportUtils.map(moh711Cohorts.marasmus(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Exclusive_breast_feeding
	 * @return
	 */
	public CohortIndicator exclusiveBreastFeeding() {
		return cohortIndicator("Exclusive_breast_feeding", ReportUtils.map(moh711Cohorts.exclusiveBreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Dewormed
	 * @return
	 */
	public CohortIndicator dewormed() {
		return cohortIndicator("Dewormed", ReportUtils.map(moh711Cohorts.dewormed(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * MNPs_Supplementation
	 * @return
	 */
	public CohortIndicator mnpsSupplementation() {
		return cohortIndicator("MNPs_Supplementation", ReportUtils.map(moh711Cohorts.mnpsSupplementation(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Child Mortality
	 * @return
	 */
	public CohortIndicator childrenDiscontinuationReasonDied() {
		return cohortIndicator("Child Mortality", ReportUtils.map(moh711Cohorts.childrenDiscontinuationReasonDied(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Children_With_Disability_Any_Form
	 * @return
	 */
	public CohortIndicator childrenWithDisability() {
		return cohortIndicator("Children_With_Disability_Any_Form", ReportUtils.map(moh711Cohorts.childrenWithDisability(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Children_with_delayed_developmental_milestones
	 * @return
	 */
	public CohortIndicator childrenWithDelayedDevelopmentalMilestones() {
		return cohortIndicator("Children_with_delayed_developmental_milestones", ReportUtils.map(moh711Cohorts.childrenWithDelayedDevelopmentalMilestones(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Total Number of people screened
	 * @return
	 */
	public CohortIndicator clientTbScreening() {
		return cohortIndicator("Total Number of people screened", ReportUtils.map(moh711Cohorts.clientTbScreening(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Total Number of presumptive TB cases
	 * @return
	 */
	public CohortIndicator clientWithPresumptiveTb() {
		return cohortIndicator("Total Number of presumptive TB cases", ReportUtils.map(moh711Cohorts.clientWithPresumptiveTb(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Total Number already on TB treatment
	 * @return
	 */
	public CohortIndicator clientonTbTreatment() {
		return cohortIndicator("Total Number already on TB treatment", ReportUtils.map(moh711Cohorts.clientOnTbTreatment(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Total Number of people not screened
	 * @return
	 */
	public CohortIndicator clientTbNotScreened() {
		return cohortIndicator("Total Number of people not screened", ReportUtils.map(moh711Cohorts.clientsNotScreenedForTB(), "startDate=${startDate},endDate=${endDate}"));
	}
}