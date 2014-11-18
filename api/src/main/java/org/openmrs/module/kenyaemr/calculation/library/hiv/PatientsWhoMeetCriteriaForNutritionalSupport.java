package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates number of patients who meet the criteria for nutritional assessment
 */
public class PatientsWhoMeetCriteriaForNutritionalSupport extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		//get the concepts that are necessary for calculation
		Concept weight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
		Concept height = Dictionary.getConcept(Dictionary.HEIGHT_CM);
		Concept muac = Dictionary.getConcept(Dictionary.MUAC);

		//would only want to deal with a live patients
		Set<Integer> alive = Filters.alive(cohort, context);

		//find the last observations recorded for those concepts
		CalculationResultMap weightMap = Calculations.lastObs(weight, alive, context);
		CalculationResultMap heightMap = Calculations.lastObs(height, alive, context);
		CalculationResultMap muacMap = Calculations.lastObs(muac, alive, context);

		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId:cohort){
			boolean meetCriteria = false;
			Double weightMapValue = EmrCalculationUtils.numericObsResultForPatient(weightMap, ptId);
			Double heightMapValue = EmrCalculationUtils.numericObsResultForPatient(heightMap, ptId);
			Double muacMapValue = EmrCalculationUtils.numericObsResultForPatient(muacMap, ptId);
			double bmi = 0.0;

			if(weightMapValue != null && heightMapValue != null) {
				bmi = bmi(weightMapValue, heightMapValue);
				if(bmi < 18.5){
					meetCriteria = true;
				}
			}
			if(muacMapValue != null && muacMapValue < 23){
					meetCriteria = true;
			}
			ret.put(ptId, new BooleanResult(meetCriteria, this));
		}
		return ret;
	}

	//a method to calculate the BMI
	//needs weight and height
	private Double bmi(double weight, double height){
		double heightM = height / 100;
		return  weight / (heightM * heightM);
	}
}
