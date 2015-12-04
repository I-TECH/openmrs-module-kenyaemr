package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.*;

/**
 * Created by codehub on 12/3/15.
 */
public class ViralLoadAndLdlCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap viralLoad = calculate(new LastViralLoadCalculation(), cohort, context);
        CalculationResultMap ldlViralLoad = calculate(new LowDitactableViralLoadCalculation(), cohort, context);
        Map<String, Date> viralLoadValues = new HashMap<String, Date>();

        for(Integer ptId:cohort){
            Obs viralLoadObs = EmrCalculationUtils.obsResultForPatient(viralLoad, ptId);
            Obs ldlObs = EmrCalculationUtils.obsResultForPatient(ldlViralLoad, ptId);
            if(viralLoadObs != null && ldlObs == null){
                viralLoadValues.put(viralLoadObs.getValueNumeric()+" copies/ml",viralLoadObs.getObsDatetime());
            }
           if(viralLoadObs == null && ldlObs != null){
                viralLoadValues.put( "LDL", ldlObs.getObsDatetime());
            }
            if(viralLoadObs != null && ldlObs != null) {
                //find the latest of the 2
                List<Obs> listOfViralLoad = new ArrayList<Obs>();
                listOfViralLoad.add(viralLoadObs);
                listOfViralLoad.add(ldlObs);

                //since observations are sorted we pick the last one
                Obs lastViralLoadPicked = listOfViralLoad.get(1);
                viralLoadValues.put(lastViralLoadPicked.getValueNumeric()+" copies/ml", lastViralLoadPicked.getObsDatetime());

            }
            ret.put(ptId, new SimpleResult(viralLoadValues, this));
        }
        return ret;
    }

}
