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

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 06/07/15.
 */
public class CurrentArtLineCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap initialArtStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
        Set<Integer> onFirstLine = CalculationUtils.patientsThatPass(calculate(new OnOriginalFirstLineArtCalculation(), cohort, context));
        Set<Integer> onSecondLine = CalculationUtils.patientsThatPass(calculate(new OnSecondLineArtCalculation(), cohort, context));

        for(Integer ptId: cohort) {
            String line = "Other";

            Date initialRegimenDate = EmrCalculationUtils.resultForPatient(initialArtStartDate, ptId);

            if(initialRegimenDate != null){

                if (onFirstLine != null && onFirstLine.contains(ptId)) {
                    line = "1st";
                }
                if(onSecondLine != null  && onSecondLine.contains(ptId)) {
                    line = "2nd";
                }
                if(onFirstLine != null && onSecondLine != null && onFirstLine.contains(ptId) && onSecondLine.contains(ptId)) {
                    line = "2nd";
                }

                ret.put(ptId, new SimpleResult(line, this));
            }

        }

        return ret;
    }
}