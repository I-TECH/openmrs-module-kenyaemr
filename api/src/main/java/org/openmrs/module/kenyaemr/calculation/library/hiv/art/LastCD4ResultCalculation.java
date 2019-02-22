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
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.ui.framework.SimpleObject;

import java.util.Collection;
import java.util.Map;

/**
 * Created by codehub on 12/3/15.
 */
public class LastCD4ResultCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();


        CalculationResultMap cd4CountValues = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap cd4PercentValues = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);



        for(Integer ptId:cohort){
            SimpleObject object = null;
            Obs cd4CountObs = EmrCalculationUtils.obsResultForPatient(cd4CountValues, ptId);
            Obs cd4PercentObs = EmrCalculationUtils.obsResultForPatient(cd4PercentValues, ptId);
            if(cd4CountObs != null && cd4PercentObs == null){
                //viralLoadValues.put(cd4CountObs.getValueNumeric()+" copies/ml",cd4CountObs.getObsDatetime());
                object = SimpleObject.create("lastCD4", cd4CountObs.getValueNumeric(), "lastCD4Date", cd4CountObs.getObsDatetime());
            }
           if(cd4CountObs == null && cd4PercentObs != null){
                //viralLoadValues.put( "LDL", cd4PercentObs.getObsDatetime());
               object = SimpleObject.create("lastCD4", cd4PercentObs.getValueNumeric().intValue() + "%", "lastCD4Date", cd4PercentObs.getObsDatetime());

           }
            if(cd4CountObs != null && cd4PercentObs != null) {
                //find the latest of the 2
                Obs cd4Picked = null;
                if (cd4CountObs.getObsDatetime().after(cd4PercentObs.getObsDatetime())) {
                    cd4Picked = cd4CountObs;
                } else {
                    cd4Picked = cd4PercentObs;
                }

                if(cd4Picked.getConcept().equals(Dictionary.getConcept(Dictionary.CD4_PERCENT))) {
                    //viralLoadValues.put(cd4Picked.getValueNumeric() + " copies/ml", cd4Picked.getObsDatetime());
                    object = SimpleObject.create("lastCD4", cd4PercentObs.getValueNumeric()+ "%", "lastCD4Date", cd4PercentObs.getObsDatetime());
                }
                else {
                    //viralLoadValues.put("LDL", cd4Picked.getObsDatetime());
                    object = SimpleObject.create("lastCD4", cd4CountObs.getValueNumeric(), "lastCD4Date", cd4CountObs.getObsDatetime());
                }

            }
            ret.put(ptId, new SimpleResult(object, this));
        }
        return ret;
    }

}
