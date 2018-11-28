/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.*;

/**
 * Created by codehub on 12/3/15.
 */
public class ViralLoadAndLdlCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap numericViralLoadValues = calculate(new LastViralLoadCalculation(), cohort, context);
        CalculationResultMap ldlViralLoadValues = calculate(new LowDetectableViralLoadCalculation(), cohort, context);
        Map<String, Date> viralLoadValues = new HashMap<String, Date>();

        for(Integer ptId:cohort){
            Obs numericVLObs = EmrCalculationUtils.obsResultForPatient(numericViralLoadValues, ptId);
            Obs ldlVLObs = EmrCalculationUtils.obsResultForPatient(ldlViralLoadValues, ptId);
            if(numericVLObs != null && ldlVLObs == null){
                viralLoadValues.put(numericVLObs.getValueNumeric()+" copies/ml",numericVLObs.getObsDatetime());
            }
           if(numericVLObs == null && ldlVLObs != null){
                viralLoadValues.put( "LDL", ldlVLObs.getObsDatetime());
            }
            if(numericVLObs != null && ldlVLObs != null) {
                //find the latest of the 2
                List<Obs> listOfViralLoad = new ArrayList<Obs>();
                listOfViralLoad.add(numericVLObs);
                listOfViralLoad.add(ldlVLObs);

                //since observations are sorted we pick the last one
                Obs lastViralLoadPicked = listOfViralLoad.get(1);
                if(lastViralLoadPicked.getConcept().equals(org.openmrs.module.kenyaemr.Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD))) {
                    viralLoadValues.put(lastViralLoadPicked.getValueNumeric() + " copies/ml", lastViralLoadPicked.getObsDatetime());
                }
                else {
                    viralLoadValues.put("LDL", lastViralLoadPicked.getObsDatetime());
                }

            }
            ret.put(ptId, new SimpleResult(viralLoadValues, this));
        }
        return ret;
    }

}
