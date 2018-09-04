/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.mchms;

import org.openmrs.Concept;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.ArtAssessmentMethod;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.PregnancyStage;
import org.openmrs.module.kenyaemr.calculation.library.mchms.AssessedOnFirstVisitCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.DiscordantCoupleCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.MchmsFirstVisitDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.MchmsHivTestDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.TestedForHivInMchmsCalculation;
import org.openmrs.module.kenyacore.report.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of MCH-MS related cohort definitions
 */
@Component
public class MchmsCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	/**
	 * Patients or patients' male partners tested for HIV within the MCHMS program
	 *
	 * @param stage   the {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 * @param result  the result of the HIV test
	 * @param partner whether the calculation considers the patient herself or her male partner
	 * @return the cohort definition
	 */
	public CohortDefinition testedForHivInMchms(PregnancyStage stage, Concept result, Boolean partner) {

		DateCalculationCohortDefinition dateCd = new DateCalculationCohortDefinition(new MchmsHivTestDateCalculation());
		dateCd.setName("Mothers tested for HIV between dates");
		dateCd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		dateCd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

		if (partner != null) {
			dateCd.addCalculationParameter("partner", partner);
		}

		CalculationCohortDefinition calculationCd = new CalculationCohortDefinition(new TestedForHivInMchmsCalculation());
		calculationCd.setName("Mothers tested for HIV in the MCH program");
		calculationCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		calculationCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));

		if (stage != null) {
			calculationCd.addCalculationParameter("stage", stage);
		}
		if (result != null) {
			calculationCd.addCalculationParameter("result", result);
		}
		if (partner != null) {
			calculationCd.addCalculationParameter("partner", partner);
		}

		CompositionCohortDefinition cohortCd = new CompositionCohortDefinition();
		cohortCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cohortCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cohortCd.addSearch("testedForHivWithinPeriod", ReportUtils.map((CohortDefinition) dateCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.addSearch("testedForHivInMchms", ReportUtils.map((CohortDefinition) calculationCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.setCompositionString("testedForHivWithinPeriod AND testedForHivInMchms");
		return cohortCd;
	}

	/**
	 * @see MchmsCohortLibrary#testedForHivInMchms(org.openmrs.module.kenyaemr.PregnancyStage, org.openmrs.Concept, Boolean)
	 */
	public CohortDefinition testedForHivInMchms(PregnancyStage stage, Concept result) {
		return testedForHivInMchms(stage, result, null);
	}

	/**
	 * Patients or patients' partners who were tested for HIV during either their ANTENATAL or DELIVERY
	 * {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @param partner whether the calculation considers the patient herself or her male partner
	 * @return the cohort definition
	 */
	public CohortDefinition testedDuringAncOrDelivery(Boolean partner) {
		CohortDefinition antenatalPositive = testedForHivInMchms(PregnancyStage.ANTENATAL, Dictionary.getConcept(Dictionary.POSITIVE), partner);
		CohortDefinition antenatalNegative = testedForHivInMchms(PregnancyStage.ANTENATAL, Dictionary.getConcept(Dictionary.NEGATIVE), partner);
		CohortDefinition deliveryPositive = testedForHivInMchms(PregnancyStage.DELIVERY, Dictionary.getConcept(Dictionary.POSITIVE), partner);
		CohortDefinition deliveryNegative = testedForHivInMchms(PregnancyStage.DELIVERY, Dictionary.getConcept(Dictionary.NEGATIVE), partner);

		CompositionCohortDefinition cohortCd = new CompositionCohortDefinition();
		cohortCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cohortCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cohortCd.addSearch("antenatalPositive", ReportUtils.map(antenatalPositive, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.addSearch("antenatalNegative", ReportUtils.map(antenatalNegative, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.addSearch("deliveryPositive", ReportUtils.map(deliveryPositive, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.addSearch("deliveryNegative", ReportUtils.map(deliveryNegative, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.setCompositionString("antenatalPositive OR antenatalNegative OR deliveryPositive OR deliveryNegative");

		return cohortCd;
	}

	/**
	 * MCHMS patients whose HIV status is discordant with that of their male partners
	 *
	 * @return the cohort definition
	 */
	public CohortDefinition discordantCouples() {
		CohortDefinition patientsTestedInAncOrDelivery = testedDuringAncOrDelivery(false);
		CohortDefinition partnersTestedInAncOrDelivery = testedDuringAncOrDelivery(true);

		CompositionCohortDefinition cohortCd = new CompositionCohortDefinition();
		cohortCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cohortCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cohortCd.addSearch("patientsTestedInAncOrDelivery", ReportUtils.map(patientsTestedInAncOrDelivery, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.addSearch("partnersTestedInAncOrDelivery", ReportUtils.map(partnersTestedInAncOrDelivery, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.setCompositionString("patientsTestedInAncOrDelivery OR partnersTestedInAncOrDelivery");

		CalculationCohortDefinition calculationCd = new CalculationCohortDefinition(new DiscordantCoupleCalculation());
		calculationCd.setName("Mothers whose HIV status is discordant with their husbands");
		calculationCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		calculationCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));

		CompositionCohortDefinition cohortCd2 = new CompositionCohortDefinition();
		cohortCd2.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cohortCd2.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cohortCd2.addSearch("cohortCd", ReportUtils.map((CohortDefinition) cohortCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd2.addSearch("calculationCd", ReportUtils.map((CohortDefinition) calculationCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd2.setCompositionString("cohortCd AND calculationCd");

		return cohortCd2;
	}

	public CohortDefinition assessedForArtEligibility(ArtAssessmentMethod artAssessmentMethod) {
		DateCalculationCohortDefinition dateCd = new DateCalculationCohortDefinition(new MchmsFirstVisitDateCalculation());
		dateCd.setName("Mothers whose first MCHMS consultation visit was between dates");
		dateCd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		dateCd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

		CalculationCohortDefinition calculationCd = new CalculationCohortDefinition(new AssessedOnFirstVisitCalculation());
		calculationCd.setName("Mothers who were assessed for ART eligibility during their first MCHMS visit");
		calculationCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		calculationCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));

		if (artAssessmentMethod != null) {
			calculationCd.addCalculationParameter("artAssessmentMethod", artAssessmentMethod);
		}

		CompositionCohortDefinition cohortCd = new CompositionCohortDefinition();
		cohortCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cohortCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cohortCd.addSearch("firstMchmsVisitWithinPeriod", ReportUtils.map((CohortDefinition) dateCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.addSearch("artAssessmentOnFirstVisit", ReportUtils.map((CohortDefinition) calculationCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.setCompositionString("firstMchmsVisitWithinPeriod AND artAssessmentOnFirstVisit");
		return cohortCd;
	}

	public CohortDefinition testedForHivInMchms() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cd.addSearch("antenatal", ReportUtils.map(testedForHivInMchms(PregnancyStage.ANTENATAL, null), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("delivery", ReportUtils.map(testedForHivInMchms(PregnancyStage.DELIVERY, null), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("postnatal", ReportUtils.map(testedForHivInMchms(PregnancyStage.POSTNATAL, null), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("antenatal OR delivery OR postnatal");
		return cd;
	}

	public CohortDefinition testedHivPositiveInMchms() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cd.addSearch("enrollment", ReportUtils.map(testedForHivInMchms(PregnancyStage.BEFORE_ENROLLMENT, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("antenatal", ReportUtils.map(testedForHivInMchms(PregnancyStage.DELIVERY, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("delivery", ReportUtils.map(testedForHivInMchms(PregnancyStage.POSTNATAL, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("postnatal", ReportUtils.map(testedForHivInMchms(PregnancyStage.POSTNATAL, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("enrollment OR antenatal OR delivery OR postnatal");
		return cd;
	}

	public CohortDefinition testedForHivBeforeOrDuringMchms() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cd.addSearch("during", ReportUtils.map(testedForHivInMchms(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("enrollment", ReportUtils.map(testedForHivInMchms(PregnancyStage.BEFORE_ENROLLMENT, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("during OR enrollment");
		return cd;
	}

	public CohortDefinition assessedForArtEligibilityTotal() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cd.addSearch("cd4", ReportUtils.map(assessedForArtEligibility(ArtAssessmentMethod.CD4_COUNT), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("who", ReportUtils.map(assessedForArtEligibility(ArtAssessmentMethod.WHO_STAGING), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("cd4 OR who");
		return cd;
	}
}