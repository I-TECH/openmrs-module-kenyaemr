package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
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
 * Calculates the current status of TB patient
 */
public class CurrentTbStatusCalculation extends AbstractPatientCalculation {
	private static int IPT_TREATMENT_DURATION = 8;

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		CalculationResultMap tbStatus = Calculations.allObs(tbDiseaseStatus, cohort, context);
		CalculationResultMap lastEncounterDates = calculate(new PatientLastEncounterDateCalculation(), cohort, context);

		for(Integer ptId:cohort) {
			boolean value = false;

			Date lastEncounterDate = EmrCalculationUtils.datetimeResultForPatient(lastEncounterDates, ptId);
			Calendar cal = Calendar.getInstance();
			cal.setTime(lastEncounterDate);
			cal.add(Calendar.MONTH, -IPT_TREATMENT_DURATION);
			Date lowerLimitDate = cal.getTime();

			List<Obs> tbObsForPatient = CalculationUtils.extractResultValues((ListResult) tbStatus.get(ptId));
			if (tbObsForPatient == null || tbObsForPatient.isEmpty()) {
				value = false;
				ret.put(ptId, new BooleanResult(value, this));
			}
			for (Obs o: tbObsForPatient) {
				if (o.getObsDatetime().before(lastEncounterDate) && o.getObsDatetime().after(lowerLimitDate) && o.getValueCoded().equals(Context.getConceptService().getConceptByUuid("1662AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
					value = true;
					break;
				}
			}

			ret.put(ptId, new SimpleResult(value, this));
		}

		return ret;
	}
}
