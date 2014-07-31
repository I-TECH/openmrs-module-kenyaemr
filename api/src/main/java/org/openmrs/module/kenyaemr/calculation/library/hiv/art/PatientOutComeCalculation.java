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
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculate possible patient outcomes at the end of the cohort period
 */
public class PatientOutComeCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		CalculationResultMap programDiscontinuation = Calculations.lastObs(Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION), cohort, context);
		Set<Integer> lostPatients = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
		Set<Integer> alive = Filters.alive(cohort,context);


		//declare possible options that would be displayed
		Concept transferOut = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
		Concept died = Dictionary.getConcept(Dictionary.DIED);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
		   String status = "";
			Concept results = EmrCalculationUtils.codedObsResultForPatient(programDiscontinuation, ptId);

			if(results == null){
				status = "Alive";
			}

			if(lostPatients.contains(ptId)){
				status = "Lost To Follow Up";
			}

			if((results != null) && (results.equals(transferOut))) {
				status = "Transferred Out";
			}

			if(((results != null) && (results.equals(died))) || (!alive.contains(ptId)) ) {
				status = "Dead";
			}
			ret.put(ptId, new SimpleResult(status, this));
		}
		 return  ret;
	}
}
