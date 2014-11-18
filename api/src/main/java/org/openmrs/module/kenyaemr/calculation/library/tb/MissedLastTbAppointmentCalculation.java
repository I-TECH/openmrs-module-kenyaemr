package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculates whether patients have missed their last scheduled return visit. Calculation returns true
 * if the patient is alive, enrolled in the TB program, has a scheduled return visit in the past,
 * and hasn't had an encounter since that date
 */
public class MissedLastTbAppointmentCalculation extends BaseEmrCalculation implements PatientCalculation{
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should calculate false for deceased patients
	 * @should calculate false for patients not in TB program
	 * @should calculate false for patients with no return visit date obs
	 * @should calculate false for patients with return visit date obs whose value is in the future
	 * @should calculate false for patients with encounter after return visit date obs value
	 * @should calculate true for patients in TB program with no encounter after return visit date obs value
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> stringObjectMap, PatientCalculationContext context) {

		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
		CalculationResultMap lastReturnDateObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), inTbProgram, context);
		CalculationResultMap lastEncounters = Calculations.lastEncounter(MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_CONSULTATION), inTbProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean missedVisit = false;

			// Is patient alive and in the TB program
			if (inTbProgram.contains(ptId)) {
				Date lastScheduledReturnDate = EmrCalculationUtils.datetimeObsResultForPatient(lastReturnDateObss, ptId);

				// Does patient have a scheduled return visit in the past
				if (lastScheduledReturnDate != null && daysSince(lastScheduledReturnDate, context) > 0) {

					// Has patient returned since
					Encounter lastEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounters, ptId);
					Date lastActualReturnDate = lastEncounter != null ? lastEncounter.getEncounterDatetime() : null;
					missedVisit = lastActualReturnDate == null || lastActualReturnDate.before(lastScheduledReturnDate);
				}
				ret.put(ptId, new SimpleResult(missedVisit, this, context));
			}
		}

		return ret;
	}


}