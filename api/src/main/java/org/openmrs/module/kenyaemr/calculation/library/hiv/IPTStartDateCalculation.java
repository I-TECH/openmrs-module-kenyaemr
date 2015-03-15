package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate the date when IPT was started
 */
public class IPTStartDateCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap iptStartDateMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), cohort, context);

		for(Integer ptId:cohort) {
			Date iptStartDate = null;

			Obs fastObs = EmrCalculationUtils.obsResultForPatient(iptStartDateMap, ptId);
			if(fastObs != null && fastObs.getConcept().equals(Dictionary.YES)) {
				iptStartDate = fastObs.getObsDatetime();
			}

			ret.put(ptId, new SimpleResult(iptStartDate, this));
		}

		return ret;
	}
}
