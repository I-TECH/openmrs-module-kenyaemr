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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculate the date of enrollment into HIV Program
 */
public class DateOfEnrollmentHivCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap enrolledHere = Calculations.allEnrollments(hivProgram, cohort, context);

        CalculationResultMap result = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Date enrollmentDate = null;
            ListResult listResult = (ListResult) enrolledHere.get(ptId);
            List<PatientProgram> patientProgram = CalculationUtils.extractResultValues(listResult);
            if(patientProgram.size() > 0){
                for(PatientProgram p: patientProgram) {
                    if(p.getDateEnrolled().before(DateUtil.adjustDate(context.getNow(), 1, DurationUnit.DAYS)) && p.getDateEnrolled().after(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), -1, DurationUnit.DAYS))) {
                        enrollmentDate = p.getDateEnrolled();
                        break;
                    }
                }
            }

            result.put(ptId, new SimpleResult(enrollmentDate, this));
        }

        return  result;
    }
}
