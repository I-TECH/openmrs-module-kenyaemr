/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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