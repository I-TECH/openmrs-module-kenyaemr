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
import org.openmrs.module.kenyaemr.MetadataConstants;

public class Summary254Calculation extends BaseKenyaEmrCalculation {

	@Override
	public String[] getTags() {
		return new String[] { };
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,Map<String, Object> parameterValues, PatientCalculationContext cxt) {
		
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
        Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(lastProgramEnrollment(hivProgram, cohort, cxt));
        Set<Integer> alive = alivePatients(cohort, cxt);
        
        CalculationResultMap summary = new CalculationResultMap();
        for (Integer ptId : cohort) {
	        if(inHivProgram.contains(ptId) && alive.contains(ptId)){
	        	
	        }
			
			
        }
        return summary;
	}

	@Override
	public String getShortMessage() {
		return "MoH 257 Report for each Patient";
	}

}
