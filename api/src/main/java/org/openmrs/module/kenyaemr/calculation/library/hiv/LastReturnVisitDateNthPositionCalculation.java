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
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 10/7/15.
 */
public class LastReturnVisitDateNthPositionCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {

        Concept returnVisitDateConcept = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);

        CalculationResultMap returnVisitDateMap = Calculations.allObs(returnVisitDateConcept, cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){
            Date visitDate = null;
            ListResult listResult = (ListResult) returnVisitDateMap.get(ptId);
            List<Obs> returnVisitDateFromLastDate = CalculationUtils.extractResultValues(listResult);
            if(returnVisitDateFromLastDate.size() == 1){
                visitDate = returnVisitDateFromLastDate.get(0).getValueDatetime();
            }
            else if (returnVisitDateFromLastDate.size() > 1) {
                visitDate = returnVisitDateFromLastDate.get(returnVisitDateFromLastDate.size() - 2).getValueDatetime();
            }

            ret.put(ptId, new SimpleResult(visitDate, this));

        }
        return ret;
    }
}
