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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 11/4/15.
 */
public class WeightAtArtInitiationCalculation extends AbstractPatientCalculation {

    /**
     * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(Collection, Map, PatientCalculationContext)
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Concept currentWeight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
        CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap weightObss = Calculations.allObs(currentWeight, cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            SimpleResult result = null;
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);
            ListResult weightObsResult = (ListResult) weightObss.get(ptId);

            if (artStartDate != null && weightObsResult != null && !weightObsResult.isEmpty()) {
                List<Obs> weight = CalculationUtils.extractResultValues(weightObsResult);
                Obs lastBeforeArtStart = EmrCalculationUtils.findLastOnOrBefore(weight, artStartDate);

                if (lastBeforeArtStart != null) {
                    Double weightValue = lastBeforeArtStart.getValueNumeric();
                    if (weightValue != null) {
                        result = new SimpleResult(weightValue, this);
                    }
                }
            }

            ret.put(ptId, result);
        }
        return ret;
    }
}