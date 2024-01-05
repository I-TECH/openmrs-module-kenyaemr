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

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentArtCalculation;
import org.openmrs.module.reporting.common.Age;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 11/06/15.
 */
public class AgeAtProgramEnrollmentCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap dateEnrolled = calculate(new DateOfEnrollmentArtCalculation(), cohort, context);
        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:cohort){
            Integer ageAtEnrollment = null;
            Date encounterDate = EmrCalculationUtils.resultForPatient(dateEnrolled, ptId);
            Date birthDate = Context.getPatientService().getPatient(ptId).getBirthdate();

            if (encounterDate != null && birthDate != null){
                ageAtEnrollment = ageInYearsAtDate(birthDate, encounterDate);
            }
            ret.put(ptId, new SimpleResult(ageAtEnrollment, this, context));
        }
        return ret;
    }

    private Integer ageInYearsAtDate(Date birthDate, Date artInitiationDate) {

        Age age = new Age(birthDate, artInitiationDate);

        return age.getFullYears();
    }
}
