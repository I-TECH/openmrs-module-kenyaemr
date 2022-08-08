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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 16/07/15.
 */
public class DaysFromEnrollmentToArtInitiationCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap enrollmentDate = calculate(new DateOfEnrollmentArtCalculation(), cohort, context);
        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for(Integer ptId: cohort){
            Integer days;
            Date dateOfEnrollment = EmrCalculationUtils.datetimeResultForPatient(enrollmentDate, ptId);
            Date dateOfInitialArt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
            if(dateOfEnrollment == null || dateOfInitialArt == null || dateOfEnrollment.after(dateOfInitialArt)){
                days = null;
            }
            else{
                days = daysBetween(dateOfInitialArt,dateOfEnrollment);
            }

            ret.put(ptId, new SimpleResult(days, this));
        }
        return  ret;
    }

    private int daysBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Math.abs(Days.daysBetween(d1, d2).getDays());
    }
}
