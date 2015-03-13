package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the date when a patient was diagnosed to have TB
 */
public class ScreenedForTbAndDiagnosedCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();

		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseDiagnosed = Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED);
		Concept diseaseSuspect = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		 CalculationResultMap tbStatus = Calculations.lastObs(tbDiseaseStatus, cohort, context);

		for(Integer ptId:cohort) {
			boolean suspect = false;
			Concept suspectOrDiagnosed = EmrCalculationUtils.codedObsResultForPatient(tbStatus, ptId);
			if(suspectOrDiagnosed != null && (suspectOrDiagnosed.equals(diseaseDiagnosed) || suspectOrDiagnosed.equals(diseaseSuspect))) {
				suspect = true;
			}
			ret.put(ptId, new BooleanResult(suspect, this));
		}

		return ret;
	}
}
