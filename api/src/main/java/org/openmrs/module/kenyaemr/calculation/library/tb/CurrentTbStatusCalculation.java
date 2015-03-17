package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the current status of TB patient
 */
public class CurrentTbStatusCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		CalculationResultMap tbStatus = Calculations.lastObs(tbDiseaseStatus, cohort, context);

		for(Integer ptId:cohort) {
			boolean value = false;
			Concept tbStatusValue = EmrCalculationUtils.codedObsResultForPatient(tbStatus, ptId);
			if(tbStatusValue != null && tbStatusValue.equals(Context.getConceptService().getConceptByUuid("1662AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
				value = true;
			}
			ret.put(ptId, new SimpleResult(value, this));
		}

		return ret;
	}
}
