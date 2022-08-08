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
import org.openmrs.ui.framework.SimpleObject;

import java.util.Collection;
import java.util.Map;
import java.util.Date;

/**
 * Created by codehub on 12/3/15.
 */
public class DateOfLastViralLoadCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap numericViralLoadValues = calculate(new LastViralLoadCalculation(), cohort, context);
        CalculationResultMap ldlViralLoadValues = calculate(new LowDetectableViralLoadCalculation(), cohort, context);



        for(Integer ptId:cohort){
            Date lastVlDate = null;

            Obs numericVLObs = EmrCalculationUtils.obsResultForPatient(numericViralLoadValues, ptId);
            Obs ldlVLObs = EmrCalculationUtils.obsResultForPatient(ldlViralLoadValues, ptId);
            if(numericVLObs != null && ldlVLObs == null){
                lastVlDate = numericVLObs.getObsDatetime();

            }
           if(numericVLObs == null && ldlVLObs != null){
               lastVlDate = ldlVLObs.getObsDatetime();
           }
            if(numericVLObs != null && ldlVLObs != null) {
                //find the latest of the 2
                Obs lastViralLoadPicked = null;
                if (numericVLObs.getObsDatetime().after(ldlVLObs.getObsDatetime())) {
                    lastViralLoadPicked = numericVLObs;
                } else {
                    lastViralLoadPicked = ldlVLObs;
                }

                if(lastViralLoadPicked.getConcept().equals(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD))) {

                    lastVlDate = numericVLObs.getObsDatetime();
                }
                else {
                    lastVlDate = ldlVLObs.getObsDatetime();
                }

            }
            ret.put(ptId, new SimpleResult(lastVlDate, this,context));
        }
        return ret;
    }

}
