/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateOfDiagnosisCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentArtCalculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculated as IF (HIV registration date - HIV diagnosis date) <=90 THEN “Yes”; ELSE “No”.
 * If HIV diagnosis date is blank, then Timely linkage = “Unknown”
 * Created by codehub on 18/06/15.
 */
public class TimelyLinkageCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        //find the enrollment date into a calculation result map
        CalculationResultMap careEnrollment = calculate(new DateOfEnrollmentArtCalculation(), cohort, context);
        //find date of diagnosis into a result map
        CalculationResultMap diagnosisDate = calculate(new DateOfDiagnosisCalculation(), cohort, context);

        for(Integer ptId:cohort) {
            Integer diff = null;

            Date careEnrollmentDate = EmrCalculationUtils.datetimeResultForPatient(careEnrollment, ptId);
            Date diagnoseDate = EmrCalculationUtils.datetimeResultForPatient(diagnosisDate, ptId);

           if(careEnrollmentDate != null && diagnoseDate != null && diagnoseDate.after(careEnrollmentDate)){
               diff = 0;
           }
            else if(careEnrollmentDate != null && diagnoseDate != null) {
                diff = daysSince(diagnoseDate,careEnrollmentDate);
            }
            ret.put(ptId, new SimpleResult(diff, this));

        }
        return  ret;
    }

    private int daysSince(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Math.abs(Days.daysBetween(d1, d2).getDays());
    }

}
