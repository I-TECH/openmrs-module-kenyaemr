/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the last recorded WHO stage of patients
 */
public class LastWhoStageCalculation extends AbstractPatientCalculation {

    /**
     * @should calculate null for patients who have recorded WHO stage
     * @should calculate last recorded WHO stage for all patients
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Concept whoQuestion = Dictionary.getConcept(Dictionary.Initial_World_Health_Organization_HIV_stage);
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap map1 = Calculations.lastObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), cohort, context);
        CalculationResultMap map2 = Calculations.lastObs(whoQuestion, cohort, context);

        if(map1 != null) {
            ret.putAll(map1);
        }
        else if(map2 != null) {
            ret.putAll(map2);
        }
        return ret;
    }
}