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
 * returns the VL result object
 */
public class ViralLoadResultCalculation extends AbstractPatientCalculation {

    private String whichViralLoad;

    public ViralLoadResultCalculation(String whichViralLoad) {
        this.whichViralLoad = whichViralLoad;
    }

    public String getWhichViralLoad() {
        return whichViralLoad;
    }

    public void setWhichViralLoad(String whichViralLoad) {
        this.whichViralLoad = whichViralLoad;
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap numericViralLoadValues = null;
        CalculationResultMap ldlViralLoadValues = null;
        if (whichViralLoad.equals("first")) {
            numericViralLoadValues = Calculations.firstObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
            ldlViralLoadValues = Calculations.firstObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
        } else if (whichViralLoad.equals("last")) {
            numericViralLoadValues = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
            ldlViralLoadValues = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
        }


        for(Integer ptId:cohort){

            SimpleObject object = null;            
            Obs numericVLObs = EmrCalculationUtils.obsResultForPatient(numericViralLoadValues, ptId);
            Obs ldlVLObs = EmrCalculationUtils.obsResultForPatient(ldlViralLoadValues, ptId);
            if(numericVLObs != null && ldlVLObs == null){
                object = SimpleObject.create("vl", String.valueOf(numericVLObs.getValueNumeric().intValue()), "vlDate", numericVLObs.getObsDatetime());
            }
            if(numericVLObs == null && ldlVLObs != null){
                object = SimpleObject.create("vl", "LDL", "vlDate", ldlVLObs.getObsDatetime());

            }
            if(numericVLObs != null && ldlVLObs != null) {
                //find the latest of the 2
                if (whichViralLoad.equals("first")) {
                    Obs firstViralLoadPicked = null;
                    if (numericVLObs.getObsDatetime().after(ldlVLObs.getObsDatetime())) {
                        firstViralLoadPicked = ldlVLObs;
                    } else {
                        firstViralLoadPicked = numericVLObs;
                    }

                    if (firstViralLoadPicked.getConcept().equals(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD))) {
                        object = SimpleObject.create("vl", String.valueOf(firstViralLoadPicked.getValueNumeric().intValue()), "vlDate", numericVLObs.getObsDatetime());
                    } else {
                        object = SimpleObject.create("vl", "LDL", "vlDate", ldlVLObs.getObsDatetime());
                    }
                } else if (whichViralLoad.equals("last")) {
                    Obs lastViralLoadPicked = null;
                    if (numericVLObs.getObsDatetime().after(ldlVLObs.getObsDatetime())) {
                        lastViralLoadPicked = numericVLObs;
                    } else {
                        lastViralLoadPicked = ldlVLObs;
                    }

                    if (lastViralLoadPicked.getConcept().equals(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD))) {
                        object = SimpleObject.create("vl", String.valueOf(lastViralLoadPicked.getValueNumeric().intValue()), "vlDate", numericVLObs.getObsDatetime());
                    } else {
                        object = SimpleObject.create("vl", "LDL", "vlDate", ldlVLObs.getObsDatetime());
                    }
                }

            }
            ret.put(ptId, new SimpleResult(object, this));
        }
        return ret;
        
    }
}
