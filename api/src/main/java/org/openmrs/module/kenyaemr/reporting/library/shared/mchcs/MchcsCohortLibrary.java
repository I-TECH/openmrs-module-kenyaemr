/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.mchcs;


import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.NeverTakenCtxOrDapsoneCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

/**
 * Library of MCH-CS related cohort definitions
*/
@Component
public class MchcsCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	@Autowired
	private HivCohortLibrary hivCohortLibrary;


	/**
	 * Infants who have taken pcr test between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrWithinMonths() {

		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept detected = Dictionary.getConcept(Dictionary.DETECTED);
		Concept equivocal = Dictionary.getConcept(Dictionary.EQUIVOCAL);
		Concept inhibitory = Dictionary.getConcept(Dictionary.INHIBITORY);
		Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		return commonCohorts.hasObs(pcrTest,detected,equivocal,inhibitory,poorSampleQuality);
	}

	/**
	 * Infants who have taken initial pcr test between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrInitialTest() {
		Concept contexualStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS);
		Concept initial = Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL);
		return commonCohorts.hasObs(contexualStatus,initial);
	}

	/**
	 * Infants aged 2 months and below ${effectiveDate}
	 * @return the cohort definition
	 */
	public CohortDefinition age2Months() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children with 2 months of age");
		age.setMaxAge(2);
		age.setMaxAgeUnit(DurationUnit.MONTHS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}

	/**
	 * Infants who have taken initial pcr test and aged 2 months and below between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition  pcrInitialWithin2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrWithinMonths", ReportUtils.map(pcrWithinMonths(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pcrInitialTest", ReportUtils.map(pcrInitialTest(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("paeds2Months", ReportUtils.map(age2Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("pcrWithinMonths AND pcrInitialTest AND paeds2Months");
		return cd;
	}

	/**
	 * Infants aged  between 3 and 8  months ${effectiveDate}
	 * @return the cohort definition
	 */
	public CohortDefinition ageBetween3And8Months() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children Between 3 and 8 Months");
		age.setMinAge(3);
		age.setMaxAge(8);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		age.setMaxAgeUnit(DurationUnit.MONTHS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}

	/**
	 * Infants who have taken initial pcr test and aged between 3 and 8 months  between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrInitialBetween3To8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrWithinMonths", ReportUtils.map(pcrWithinMonths(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pcrInitialTest", ReportUtils.map(pcrInitialTest(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageBetween3And8Months", ReportUtils.map(ageBetween3And8Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("pcrWithinMonths AND pcrInitialTest AND ageBetween3And8Months");
		return cd;
	}

	/**
	 * Infants who have taken serology antibody test  between ${onOrAfter} and ${onOrBefore}
	 * @return  the cohort definition
	 */
	public CohortDefinition serologyAntBodyTest() {
		Concept hivRapidTest = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
		Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);
		Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		return commonCohorts.hasObs(hivRapidTest,negative,poorSampleQuality,positive);
	}

	/**
	 * Infants aged  between 9 and 12  months ${effectiveDate}
	 * @return the cohort definition
	 */
	public CohortDefinition ageBetween9And12Months() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children Between 9 and 12 Months");
		age.setMinAge(9);
		age.setMaxAge(12);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		age.setMaxAgeUnit(DurationUnit.MONTHS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}

	/**
	 * Infants who have taken initial serology antibody  test and aged between 9 and 12 months  between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition serologyAntBodyTestBetween9And12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("serologyAntBodyTest", ReportUtils.map(serologyAntBodyTest(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageBetween9And12Months", ReportUtils.map(ageBetween9And12Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("serologyAntBodyTest AND ageBetween9And12Months");
		return cd;
	}

	/**
	 * Infants who have taken initial pcr test and aged between 9 and 12 months  between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrBetween9And12MonthsAge() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrWithinMonths", ReportUtils.map(pcrWithinMonths(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pcrInitialTest", ReportUtils.map(pcrInitialTest(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageBetween9And12Months", ReportUtils.map(ageBetween9And12Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("pcrWithinMonths AND pcrInitialTest  AND ageBetween9And12Months");
		return cd;
	}

	/**
	 * Total HEI tested by the 12 month between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition totalHeitestedBy12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrInitialWithin2Months", ReportUtils.map(pcrInitialWithin2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pcrInitialBetween3To8Months", ReportUtils.map(pcrInitialBetween3To8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("serologyAntBodyTestBetween9And12Months", ReportUtils.map(serologyAntBodyTestBetween9And12Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("pcrInitialWithin2Months OR pcrInitialBetween3To8Months OR serologyAntBodyTestBetween9And12Months");
		return cd;
	}

	/**
	 * The confirmed test context status between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition detectedConfirmedStatus() {
		Concept testContextStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS) ;
		Concept detectedConfirmedStatus = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);
		return commonCohorts.hasObs(testContextStatus,detectedConfirmedStatus);
	}

	/**
	 * Infant patients who have taken pcr test and their status is positive between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrConfirmedPositive2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrWithinMonths",ReportUtils.map(pcrWithinMonths(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("detectedConfirmedStatus",ReportUtils.map(detectedConfirmedStatus(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("paeds2Months", ReportUtils.map(age2Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("pcrWithinMonths AND detectedConfirmedStatus AND paeds2Months");
		return cd;
	}

	/**
	 * Infant patients who have taken pcr test and their status is positive and are aged between 3 and 8 months between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrConfirmedPositiveBetween3To8Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrWithinMonths",ReportUtils.map(pcrWithinMonths(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("detectedConfirmedStatus",ReportUtils.map(detectedConfirmedStatus(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("paeds3and8Months", ReportUtils.map(ageBetween3And8Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("pcrWithinMonths AND detectedConfirmedStatus AND paeds3and8Months");
		return cd;
	}

	/**
	 * Infant patients who have taken pcr test and their status is positive and are aged between 9 and 12 months between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrConfirmedPositiveBetween9To12Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrWithinMonths",ReportUtils.map(pcrWithinMonths(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("detectedConfirmedStatus",ReportUtils.map(detectedConfirmedStatus(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("paeds9and12Months", ReportUtils.map(ageBetween9And12Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("pcrWithinMonths AND detectedConfirmedStatus AND paeds9and12Months");
		return cd;
	}

	/**
	 * Total HEI patients who have taken pcr test and their status is positive  between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition pcrTotalConfirmedPositive() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrConfirmedPositive2Months", ReportUtils.map(pcrConfirmedPositive2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pcrConfirmedPositiveBetween3To8Months",ReportUtils.map(pcrConfirmedPositiveBetween3To8Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pcrConfirmedPositiveBetween9To12Months", ReportUtils.map(pcrConfirmedPositiveBetween9To12Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("pcrConfirmedPositive2Months OR pcrConfirmedPositiveBetween3To8Months OR pcrConfirmedPositiveBetween9To12Months");
		return cd;
	}

	/**
	 * Infants aged 6 months and above ${effectiveDate}
	 * @return the cohort definition
	 */
	public CohortDefinition ageAt6Months() {
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("Children with 6 months of age");
		age.setMinAge(6);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		age.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
		return age;
	}

	/**
	 * Infants who are exclusively breastfed between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition exclusiveBreastFeeding() {
		Concept infantFeedingMethod = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		Concept exclusiveBreastFeeding = Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY);
		return commonCohorts.hasObs(infantFeedingMethod,exclusiveBreastFeeding);
	}

	/**
	 * Infants who are on exclusive replacement feeding between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition exclusiveReplacementFeeding() {
		Concept infantFeedingMethod = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		Concept exclusiveReplacement = Dictionary.getConcept(Dictionary.REPLACEMENT_FEEDING);
		return commonCohorts.hasObs(infantFeedingMethod,exclusiveReplacement);
	}

	/**
	 * Infants who are on mixed feeding between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition mixedFeeding() {
		Concept infantFeedingMethod = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		Concept mixedFeeding = Dictionary.getConcept(Dictionary.MIXED_FEEDING);
		return commonCohorts.hasObs(infantFeedingMethod,mixedFeeding);
	}

	/**
	 * Infants who are on exclusively breast feeding age 6 months between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition exclusiveBreastFeedingAtSixMonths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("exclusiveBreastFeeding",ReportUtils.map(exclusiveBreastFeeding(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageAt6Months", ReportUtils.map(ageAt6Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("exclusiveBreastFeeding AND ageAt6Months");
		return cd;
	}

	/**
	 * Infants who are on exclusive replacement breast feeding age 6 months between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition exclusiveReplacementFeedingAtSixMonths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("exclusiveReplacementFeeding",ReportUtils.map(exclusiveReplacementFeeding(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageAt6Months", ReportUtils.map(ageAt6Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("exclusiveReplacementFeeding AND ageAt6Months");
		return cd;
	}

	/**
	 * Infants who are on mixed feeding age 6 months between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition mixedFeedingAtSixMonths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("mixedFeeding",ReportUtils.map(mixedFeeding(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageAt6Months", ReportUtils.map(ageAt6Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("mixedFeeding AND ageAt6Months");
		return cd;
	}

	/**
	 * Total HEI patients feeding aged 6 between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition totalExposedAgedSixMoths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("exclusiveBreastFeedingAtSixMonths",ReportUtils.map(exclusiveBreastFeedingAtSixMonths(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("exclusiveReplacementFeedingAtSixMonths",ReportUtils.map(exclusiveReplacementFeedingAtSixMonths(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("mixedFeedingAtSixMonths",ReportUtils.map(mixedFeedingAtSixMonths(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("exclusiveBreastFeedingAtSixMonths OR exclusiveReplacementFeedingAtSixMonths OR mixedFeedingAtSixMonths");
		return cd;
	}

	/**
	 * Mothers who are on treatment and breast feeding between ${onOrAfter} and ${onOrBefore}
	 * @return  the cohort definition
	 */
	public CohortDefinition motherOnTreatmentAndBreastFeeding() {
		Concept motherOnTreatmentAndBreatFeeding = Dictionary.getConcept(Dictionary.MOTHER_ON_ANTIRETROVIRAL_DRUGS_AND_BREASTFEEDING);
		Concept breastfeeding = Dictionary.getConcept(Dictionary.YES);
		return commonCohorts.hasObs(motherOnTreatmentAndBreatFeeding,breastfeeding);
	}

	/**
	 * Mothers who are on treatment and NOT breast feeding between ${onOrAfter} and ${onOrBefore}
	 * @return  the cohort definition
	 */
	public CohortDefinition motherOnTreatmentAndNotBreastFeeding() {
		Concept motherOnTreatmentAndBreatFeeding = Dictionary.getConcept(Dictionary.MOTHER_ON_ANTIRETROVIRAL_DRUGS_AND_BREASTFEEDING);
		Concept notBreastfeeding = Dictionary.getConcept(Dictionary.NO);
		return commonCohorts.hasObs(motherOnTreatmentAndBreatFeeding,notBreastfeeding);
	}

	/**
	 * Mothers who are on treatment and never know if they are breastfeeding between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition motherOnTreatmentAndNotBreastFeedingUnknown() {
		Concept motherOnTreatmentAndBreatFeeding = Dictionary.getConcept(Dictionary.MOTHER_ON_ANTIRETROVIRAL_DRUGS_AND_BREASTFEEDING);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);
		return commonCohorts.hasObs(motherOnTreatmentAndBreatFeeding,unknown);
	}

	/**
	 * Total mothers who are on treatment and breast feeding between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition totalBreastFeedingMotherOnTreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("motherOnTreatmentAndBreastFeeding",ReportUtils.map(motherOnTreatmentAndBreastFeeding(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("motherOnTreatmentAndNotBreastFeeding",ReportUtils.map(motherOnTreatmentAndNotBreastFeeding(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("motherOnTreatmentAndNotBreastFeedingUnknown",ReportUtils.map(motherOnTreatmentAndNotBreastFeedingUnknown(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("motherOnTreatmentAndNotBreastFeedingUnknown OR motherOnTreatmentAndNotBreastFeeding OR motherOnTreatmentAndNotBreastFeedingUnknown");
		return cd;
	}

	/**
	 * Total number of HEI between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition hivExposedInfants() {
		Concept childHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
		return commonCohorts.hasObs(childHivStatus,hivExposed);
	}

	/**
	 * Total number of HEI  within 2 months between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition  hivExposedInfantsWithin2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("hivExposedInfants",ReportUtils.map(hivExposedInfants(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("ageWithin2Months", ReportUtils.map(age2Months(), "effectiveDate=${onOrBefore}"));
		cd.setCompositionString("hivExposedInfants AND ageWithin2Months");
		return cd;
	}

	/**
	 * Total number of HEI eligible for CTX between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition hivExposedInfantsEligibleForCTX() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new NeverTakenCtxOrDapsoneCalculation());
		cd.setName("Infants who have been given CTX");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}

	/**
	 * Total number of HEI eligible for CTX within 2 months between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition hivExposedInfantsWithin2MonthsAndEligibleForCTX() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("hivExposedInfantsWithin2Months",ReportUtils.map(hivExposedInfantsWithin2Months(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("hivExposedInfantsEligibleForCTX",ReportUtils.map(hivCohortLibrary.onCtxProphylaxis(),"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("hivExposedInfantsWithin2Months AND hivExposedInfantsEligibleForCTX");
		return cd;
	}
	
	/**
	 * Total number of scheduled visits in the CWC between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition scheduledVisitsInCWC() {		
		Concept tca = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE); 
		
		DateObsCohortDefinition scheduledVisits = new DateObsCohortDefinition();		

		ArrayList<EncounterType> cwcEncounterTypes = new ArrayList<EncounterType>();
		
		cwcEncounterTypes.add(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_ENROLLMENT));
		cwcEncounterTypes.add(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION));
		cwcEncounterTypes.add(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_IMMUNIZATION));

		scheduledVisits.setName("scheduled visits in CWC");
		scheduledVisits.addParameter(new Parameter("onOrAfter", "After date", Date.class));
		scheduledVisits.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
		scheduledVisits.setTimeModifier(BaseObsCohortDefinition.TimeModifier.ANY);
		scheduledVisits.setEncounterTypeList(cwcEncounterTypes);
		scheduledVisits.setQuestion(tca);		
		
		return scheduledVisits;

	}
}

