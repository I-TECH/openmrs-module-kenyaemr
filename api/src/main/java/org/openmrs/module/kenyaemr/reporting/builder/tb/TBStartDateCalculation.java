package org.openmrs.module.kenyaemr.reporting.builder.tb;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientLastEncounterDateCalculation;

import java.util.*;

/**
 * Calculates the date a patient started TB
 */
public class TBStartDateCalculation extends AbstractPatientCalculation {
    private static int TB_TREATMENT_DURATION = 8;

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
        CalculationResultMap tbStatus = Calculations.allObs(tbDiseaseStatus, cohort, context);

        //find the date from the TB enrollment form if available
        CalculationResultMap tbEnrollmentTbStartDate = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), cohort, context);


        for (Integer ptId : cohort) {
            Date tbStartDateFromTbEnrollmentForm = null;
            List<Obs> listOfTbTreatmentObs = new ArrayList<Obs>();

            ListResult tbStatusObsResults = (ListResult) tbStatus.get(ptId);

            //get the evaluation date
            Calendar cal = Calendar.getInstance();
            cal.setTime(context.getNow());
            cal.add(Calendar.MONTH, -TB_TREATMENT_DURATION);
            Date lowerLimitDate = cal.getTime();

            if(tbStatusObsResults != null) {
                List<Obs> extractedObs = CalculationUtils.extractResultValues(tbStatusObsResults);
                if(!(extractedObs.isEmpty())) {
                    for(Obs obs : extractedObs){
                        if(obs.getObsDatetime().before(context.getNow()) && obs.getObsDatetime().after(lowerLimitDate) && obs.getValueCoded().equals(Dictionary.getConcept("1662AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                            listOfTbTreatmentObs.add(obs);
                        }
                    }
                }
            }

            Obs tbStartDateFromTbEnrollmentFormObs = EmrCalculationUtils.obsResultForPatient(tbEnrollmentTbStartDate, ptId);

            if(tbStartDateFromTbEnrollmentFormObs != null && tbStartDateFromTbEnrollmentFormObs.getObsDatetime().before(context.getNow())){
                tbStartDateFromTbEnrollmentForm = tbStartDateFromTbEnrollmentFormObs.getValueDatetime();
            }
            else if (!(listOfTbTreatmentObs.isEmpty())){
                tbStartDateFromTbEnrollmentForm = listOfTbTreatmentObs.get(0).getObsDatetime();
            }

            ret.put(ptId, new SimpleResult(tbStartDateFromTbEnrollmentForm, this));

        }

        return ret;
    }
}
