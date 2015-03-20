package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.model.LostToFU;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculates the date a patient was declared lost
 */
public class DateClassifiedLTFUCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Concept reasonForDiscontinuation = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
		CalculationResultMap lastReturnDateObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), inHivProgram, context);
		CalculationResultMap lastProgramDiscontinuation = Calculations.lastObs(reasonForDiscontinuation, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
            LostToFU classifiedLTFU = null;
			// Is patient alive and in the HIV program
			if (inHivProgram.contains(ptId)) {
				Date lastScheduledReturnDate = EmrCalculationUtils.datetimeObsResultForPatient(lastReturnDateObss, ptId);
				Obs discontinuation = EmrCalculationUtils.obsResultForPatient(lastProgramDiscontinuation, ptId);
				if (lastScheduledReturnDate != null) {
					if (daysSince(lastScheduledReturnDate, context) > HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS) {
						if (discontinuation == null) {
							Calendar dateClassified = Calendar.getInstance();
							dateClassified.setTime(lastScheduledReturnDate);
							dateClassified.add(Calendar.DATE, HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS);
                            classifiedLTFU = new LostToFU(true, dateClassified.getTime());
						}
                        else {
                            classifiedLTFU = new LostToFU(false, null);
                        }
					}
				}
			}
			ret.put(ptId, new SimpleResult(classifiedLTFU, this));
		}
		return ret;
	}
}
