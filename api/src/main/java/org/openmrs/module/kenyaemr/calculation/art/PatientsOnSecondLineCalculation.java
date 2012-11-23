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
package org.openmrs.module.kenyaemr.calculation.art;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenHistory;

public class PatientsOnSecondLineCalculation extends BaseKenyaEmrCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext ctx) {
        Concept arvSet = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
        Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);

        Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(lastProgramEnrollment(hivProgram, cohort, ctx));
        Set<Integer> alive = alivePatients(cohort, ctx);
        // TODO avoid the call to RegimenHistory.forPatient inside the loop

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean changed = false;
            if (inHivProgram.contains(ptId) && alive.contains(ptId)) {
                RegimenHistory history = RegimenHistory.forPatient(Context.getPatientService().getPatient(ptId), arvSet);
                List<RegimenChange> changes = history.getChanges();
                for (RegimenChange regimenChange : changes) {
                    if (regimenChange.getStopped() != null && ((regimenChange.getChangeReasonsNonCoded().size() > 0) || (regimenChange.getChangeReasons().size() > 0))) {
                        changed = true;
                    }
                }
            }
            ret.put(ptId, new SimpleResult(changed, this, ctx));
        }
        return ret;
    }

    @Override
    public String getShortMessage() {
        return "Patients on Second Line";
    }

}
