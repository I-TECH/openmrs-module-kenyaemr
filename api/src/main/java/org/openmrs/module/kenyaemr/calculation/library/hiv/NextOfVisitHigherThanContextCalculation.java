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

import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 07/08/15.
 * Checks whether a patient has an appointment date that is higher than the context
 */
public class NextOfVisitHigherThanContextCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap nextAppointmentMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), cohort, context);

        for(Integer ptId: cohort){
            boolean hasNextDateOfVisit = false;

            Obs nextOfVisitObs = EmrCalculationUtils.obsResultForPatient(nextAppointmentMap, ptId);
            if(nextOfVisitObs != null) {
                Date valueDateTime = nextOfVisitObs.getValueDatetime();
                if(valueDateTime != null && (valueDateTime.after(context.getNow()))){
                    hasNextDateOfVisit = true;
                }
            }
            ret.put(ptId, new BooleanResult(hasNextDateOfVisit, this));

        }
        return  ret;
    }
}
