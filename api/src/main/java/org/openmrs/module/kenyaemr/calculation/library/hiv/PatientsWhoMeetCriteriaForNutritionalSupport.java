package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

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

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(context.getNow());
		calendar.add(Calendar.MONTH, -6);

		//find the last observations recorded for those concepts
		CalculationResultMap weightMap = Calculations.lastObs(weight, cohort, context);
		CalculationResultMap heightMap = Calculations.lastObs(height, cohort, context);
		CalculationResultMap muacMap = Calculations.lastObs(muac, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId:cohort){
			boolean meetCriteria = false;
			Double weightMapValue = 0.0;
			Double heightMapValue = 0.0;
			Double muacMapValue = 0.0;

			Date weightDate = EmrCalculationUtils.datetimeObsResultForPatient(weightMap, ptId);
			Date heightDate = EmrCalculationUtils.datetimeObsResultForPatient(heightMap, ptId);
			Date muacDate = EmrCalculationUtils.datetimeObsResultForPatient(muacMap, ptId);

			if(weightDate != null && weightDate.after(calendar.getTime()) && weightDate.before(context.getNow())) {
				weightMapValue = EmrCalculationUtils.numericObsResultForPatient(weightMap, ptId);
			}

			if(heightDate != null && heightDate.after(calendar.getTime()) && heightDate.before(context.getNow())) {
				heightMapValue = EmrCalculationUtils.numericObsResultForPatient(heightMap, ptId);
			}

			if(muacDate != null && muacDate.after(calendar.getTime()) && muacDate.before(context.getNow())) {
				muacMapValue = EmrCalculationUtils.numericObsResultForPatient(muacMap, ptId);
			}
			double bmi;

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
