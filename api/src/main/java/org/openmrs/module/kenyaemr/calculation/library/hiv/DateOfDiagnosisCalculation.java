package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 18/06/15.
 */
public class DateOfDiagnosisCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        ObsForPersonDataDefinition obs = new ObsForPersonDataDefinition();
        obs.setWhich(TimeQualifier.LAST);
        obs.setQuestion(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS));

        //put the data definition into a result map
        CalculationResultMap diagnosisDate = CalculationUtils.evaluateWithReporting(obs, cohort, parameterValues, null, context);
        for(Integer ptId:cohort) {
            Date dateOfDiagnosis = null;

            Obs date = EmrCalculationUtils.obsResultForPatient(diagnosisDate, ptId);
            if(date != null) {
                dateOfDiagnosis = date.getValueDatetime();
            }

            ret.put(ptId, new SimpleResult(dateOfDiagnosis, this));
        }

        return ret;
    }
}
