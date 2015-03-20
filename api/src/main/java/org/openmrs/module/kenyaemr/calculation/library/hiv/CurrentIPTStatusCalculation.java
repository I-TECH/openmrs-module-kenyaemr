package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.model.CurrentIPTStatus;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculates the current IPT status
 */
public class CurrentIPTStatusCalculation extends AbstractPatientCalculation {
	private static int IPT_TREATMENT_DURATION = 6;

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap iptObs = Calculations.allObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), cohort, context);

		for(Integer ptId: cohort) {
			boolean onINH = false;
			Date lastEncounterDate = context.getNow();
			Calendar cal = Calendar.getInstance();
			cal.setTime(lastEncounterDate);
			cal.add(Calendar.MONTH, -IPT_TREATMENT_DURATION);
			Date lowerLimitDate = cal.getTime();

			List<Obs> iptObsForPatient = CalculationUtils.extractResultValues((ListResult) iptObs.get(ptId));
			if (iptObsForPatient == null || iptObsForPatient.isEmpty()) {
				onINH = false;
				ret.put(ptId, new SimpleResult(new CurrentIPTStatus(onINH, null), this));
			}
			int counter = 0;
			Date currentTreatmentStartDate = null;
			for (Obs o: iptObsForPatient) {
				counter++;

				if (o.getObsDatetime().before(lastEncounterDate) && o.getObsDatetime().after(lowerLimitDate) && o.getValueCoded().equals(Dictionary.YES)) {
					if (counter == 1) {
						currentTreatmentStartDate = o.getObsDatetime();
					}
					onINH = true;
					break;
				}

			}

			ret.put(ptId, new SimpleResult(new CurrentIPTStatus(onINH, currentTreatmentStartDate), this));
		}
		return ret;
	}
}
