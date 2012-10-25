/*
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
package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

public class DecliningCD4Calculation extends KenyaEmrCalculation {

    @Override
    public String getShortMessage() {
        return "Declining CD4";
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
        Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, cohort, context));
        Set<Integer> alive = alivePatients(cohort, context);
        CalculationResultMap lastCD4 = lastObs(MetadataConstants.CD4_CONCEPT_UUID, cohort, context);
        CalculationResultMap cd4SixMonthsAgo = lastObsAtLeastDaysAgo(MetadataConstants.CD4_CONCEPT_UUID, 180, cohort, context);
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean declining = false;
            Double latestCD4 = 0.0;
            Double sixMonthsAgoCD4 = 0.0;
            if (inHivProgram.contains(ptId) && alive.contains(ptId)) {
                latestCD4 = numericObsResultForPatient(lastCD4, ptId);
                sixMonthsAgoCD4 = numericObsResultForPatient(cd4SixMonthsAgo, ptId);
                if (latestCD4 != null && sixMonthsAgoCD4 != null) {
                    declining = latestCD4 < sixMonthsAgoCD4;
                }
            }
            ret.put(ptId, new SimpleResult(declining, this, context));
        }
        return ret;
    }

}
