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
import org.openmrs.Encounter;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate the latest date of enrollment into HIV Program
 */
public class DateOfLatestEnrollmentHivCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {


        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap lastEnrollmentEncounter = Calculations.lastEncounter(Context.getEncounterService().getEncounterTypeByUuid("de78a6be-bfc5-4634-adc3-5f1a280455cc"),cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:cohort) {
            Date lastEnrollmentDate = null;
             Encounter lastEnrollment = EmrCalculationUtils.encounterResultForPatient(lastEnrollmentEncounter, ptId);

            if(lastEnrollment != null ) {
                lastEnrollmentDate = lastEnrollment.getEncounterDatetime();
            }

            ret.put(ptId, new SimpleResult(lastEnrollmentDate, this));
        }

        return ret;
    }
}
