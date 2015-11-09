package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientLastEncounterDateCalculation;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculates the current IPT status
 */
public class CurrentIPTStatus extends AbstractPatientCalculation {
	private static int IPT_TREATMENT_DURATION = 6;

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap iptObs = Calculations.allObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), cohort, context);
		CalculationResultMap lastEncounterDates = calculate(new PatientLastEncounterDateCalculation(), cohort, context);

		for(Integer ptId: cohort) {
			boolean onINH = false;
			Date lastEncounterDate = EmrCalculationUtils.datetimeResultForPatient(lastEncounterDates, ptId);
			Calendar cal = Calendar.getInstance();
			cal.setTime(lastEncounterDate);
			cal.add(Calendar.MONTH, -IPT_TREATMENT_DURATION);
			Date lowerLimitDate = cal.getTime();

			List<Obs> iptObsForPatient = CalculationUtils.extractResultValues((ListResult) iptObs.get(ptId));
			if (iptObsForPatient == null || iptObsForPatient.isEmpty()) {
				onINH = false;
				ret.put(ptId, new BooleanResult(onINH, this));
			}

			for (Obs o: iptObsForPatient) {
				if (o.getObsDatetime().before(lastEncounterDate) && o.getObsDatetime().after(lowerLimitDate) && o.getValueCoded().equals(Dictionary.YES)) {
					onINH = true;
					break;
				}
			}

			ret.put(ptId, new BooleanResult(onINH, this));
		}
		return ret;
	}
}
