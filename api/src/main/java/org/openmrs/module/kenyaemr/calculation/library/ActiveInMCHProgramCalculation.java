/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.Metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dev on 9/24/16.
 */
public class ActiveInMCHProgramCalculation extends AbstractPatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("program", Context.getProgramWorkflowService().getProgramByUuid(Metadata.Program.MCH_CS));
        CalculationResultMap mch_csProgram = new InProgramCalculation().evaluate(cohort, params, Context.getService(PatientCalculationService.class).createCalculationContext());

        params.put("program", Context.getProgramWorkflowService().getProgramByUuid(Metadata.Program.MCH_MS));
        CalculationResultMap mch_msProgram = new InProgramCalculation().evaluate(cohort, params, Context.getService(PatientCalculationService.class).createCalculationContext());

        for(Integer ptId: cohort){
                if ((mch_csProgram.get(ptId) != null && (Boolean) mch_csProgram.get(ptId).getValue()) || (mch_msProgram.get(ptId) != null && (Boolean)mch_msProgram.get(ptId).getValue())) {
                    ret.put(ptId, new BooleanResult(true, this));
                } else {
                    ret.put(ptId, new BooleanResult(false, this));
                }
        }

        return ret;
    }
}
