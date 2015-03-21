package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
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

        //find the return visit date from the last encounter
        CalculationResultMap resultMap = calculate(new LastReturnVisitDateCalculation(), cohort, context);
        //find lost to follow up patients
        Set<Integer> lostPatients = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
            LostToFU classifiedLTFU = null;
			// Is patient alive and in the HIV program
			if (lostPatients.contains(ptId)) {
				SimpleResult lastScheduledReturnDateResults = (SimpleResult) resultMap.get(ptId);

				if (lastScheduledReturnDateResults != null) {
                    Date lastScheduledReturnDate = (Date) lastScheduledReturnDateResults.getValue();
                    Calendar dateClassified = Calendar.getInstance();
                    dateClassified.setTime(lastScheduledReturnDate);
                    dateClassified.add(Calendar.DATE, HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS);
                    classifiedLTFU = new LostToFU(true, dateClassified.getTime());
                }

			}
            else {
                classifiedLTFU = new LostToFU(false, null);
            }

			ret.put(ptId, new SimpleResult(classifiedLTFU, this));
		}
		return ret;
	}
}
