package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculate the number of mothers with the new born reviewed by health provider within 7 to 14 days of birth
 */
public class MotherNewBornPairReviewedCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Set<Integer> female = Filters.female(cohort, context);
		CalculationResultMap deliveryDateMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT),female, context);
		//find post natal encounters
		CalculationResultMap postNatalEncounterType = Calculations.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION), female, context);
		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId : cohort){
			boolean hasNewBornAndReviewed = false;
			//evaluate the deliver date
			Date deliveryDate = EmrCalculationUtils.datetimeObsResultForPatient(deliveryDateMap, ptId);
			if(deliveryDate != null) {
				//only proceed if a patient has one and load their last encounter
				Encounter postNatalEncounter = EmrCalculationUtils.encounterResultForPatient(postNatalEncounterType, ptId);
				//the encounter should exist and should match the form we are using to collect the information
				if(postNatalEncounter != null && postNatalEncounter.getForm().equals(MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_POSTNATAL_VISIT))) {
					//get the encounter date
					Date encounterDate = postNatalEncounter.getEncounterDatetime();
					//the encounter date should appear within 7 to 14 days after birth
					//give a general date of delivery to the calendar instance
					//Get a date 7 days after delivery
					Date dod7DaysAfter = CoreUtils.dateAddDays(deliveryDate, 7);
					//get the date 14 days after the DOD
					Date dod14DaysAfter = CoreUtils.dateAddDays(deliveryDate, 14);
					//check if a post natal encounter occurred between dod7DaysAfter and  dod14DaysAfter
					if((encounterDate.after(dod7DaysAfter) || encounterDate.equals(dod7DaysAfter)) && (encounterDate.before(dod14DaysAfter) || encounterDate.equals(dod14DaysAfter))) {
						hasNewBornAndReviewed = true;
					}
				}
			}
			ret.put(ptId, new BooleanResult(hasNewBornAndReviewed, this));
		}
		return ret;
	}
}
