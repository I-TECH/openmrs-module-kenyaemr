package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient is a transfer out based on the status
 */
public class IsTransferOutCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Concept reasonForDiscontinue = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);

		CalculationResultMap discontinuationStatus = Calculations.lastObs(reasonForDiscontinue, cohort, context);
		Set<Integer> transferOutDate = CalculationUtils.patientsThatPass(calculate(new TransferOutDateCalculation(), cohort, context));

		CalculationResultMap result = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean isTransferOut = false;

			Concept status = EmrCalculationUtils.codedObsResultForPatient(discontinuationStatus, ptId);

			if (((status != null) && (status.equals(Dictionary.getConcept(Dictionary.TRANSFERRED_OUT)))) || transferOutDate.contains(ptId)) {
				isTransferOut = true;
			}
			result.put(ptId, new BooleanResult(isTransferOut, this, context));
		}

		return result;
	}
}
