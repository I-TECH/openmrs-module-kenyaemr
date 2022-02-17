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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastViralLoadCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LowDetectableViralLoadCalculation;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates  patients are have suppressed VL results as last results given
 */
public class SuppressedVLPatientsCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(SuppressedVLPatientsCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap numericViralLoadValues = calculate(new LastViralLoadCalculation(), cohort, context);
        CalculationResultMap ldlViralLoadValues = calculate(new LowDetectableViralLoadCalculation(), cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {

            Boolean suppressed = false;
            Obs numericVLObs = EmrCalculationUtils.obsResultForPatient(numericViralLoadValues, ptId);
            Obs ldlVLObs = EmrCalculationUtils.obsResultForPatient(ldlViralLoadValues, ptId);
            //Has only numeric vl
            if (numericVLObs != null && ldlVLObs == null) {
                if (numericVLObs.getValueNumeric() < 1000) {
                    suppressed = true;
                }
            }
            // Has only LDL
            if (numericVLObs == null && ldlVLObs != null) {
                suppressed = true;

            }
            // Has both LDL and numeric vls
            if (numericVLObs != null && ldlVLObs != null) {
                //find the latest of the 2
                if (numericVLObs.getObsDatetime().after(ldlVLObs.getObsDatetime()) && numericVLObs.getValueNumeric() < 1000) {
                    suppressed = true;
                }
                if (ldlVLObs.getObsDatetime().after(numericVLObs.getObsDatetime())) {
                    suppressed = true;
                }
            }
            ret.put(ptId, new BooleanResult(suppressed, this));
        }
        return ret;
    }
}

