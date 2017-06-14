package org.openmrs.module.kenyaemr.calculation.library.hiv;

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
 * Calculates the current IPT status
 */
public class CurrentIPTStatus extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();

		CalculationResultMap lastIpt = Calculations.lastObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), cohort, context);

		for(Integer ptId: cohort) {
			boolean onINH = false;

			Concept yes = EmrCalculationUtils.codedObsResultForPatient(lastIpt, ptId);
			if (yes != null && yes.equals(Dictionary.YES)) {
				onINH = true;
			}
			ret.put(ptId, new BooleanResult(onINH, this));
		}
		return ret;
	}
}
