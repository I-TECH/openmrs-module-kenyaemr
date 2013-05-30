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

package org.openmrs.module.kenyaemr.calculation.tb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

/**
 * Calculates whether patients are in the TB program
 */
public class TbInProgramCalculation extends BaseEmrCalculation {

    @Override
    public String getName() {
        return "Patients in TB Program";
    }

	@Override
	public String[] getTags() {
		return new String[] { "tb" };
	}

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext context) {

		Program tbProgram = Metadata.getProgram(Metadata.TB_PROGRAM);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inTbProgram = CalculationUtils.patientsThatPass(lastProgramEnrollment(tbProgram, alive, context));

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean inProgram = false;

			// Is patient alive and in the TB program
            if (inTbProgram.contains(ptId)) {
                inProgram = true;
            }
            ret.put(ptId, new SimpleResult(inProgram, this, context));
        }

        return ret;
    }
}