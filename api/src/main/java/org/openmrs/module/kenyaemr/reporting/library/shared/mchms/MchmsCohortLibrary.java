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
}