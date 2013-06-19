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

package org.openmrs.module.kenyaemr.calculation.library.art;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.util.OpenmrsUtil;

/**
 * Calculate whether patients are due for a CD4 count. Calculation returns true if if the patient
 * is alive, enrolled in the HIV program, and has not had a CD4 count in the last 180 days
 */
public class NeedsCd4TestCalculation extends BaseAlertCalculation {

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation#getAlertMessage()
	 */
	@Override
	public String getAlertMessage() {
		return "Due for CD4";
	}
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine whether patients need a CD4
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = Metadata.getProgram(Metadata.HIV_PROGRAM);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(activeEnrollment(hivProgram, alive, context));
		CalculationResultMap lastObs = lastObs(getConcept(Dictionary.CD4_COUNT), cohort, context);
		//get the CD4% CalculationResultMap
		CalculationResultMap lastObsPercent = lastObs(getConcept(Dictionary.CD4_PERCENT), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsCD4 = false;

			// Is patient alive and in the HIV program
			if (inHivProgram.contains(ptId)) {

					// Does patient have CD4 or CD4% result in the last X days
					ObsResult r = (ObsResult) lastObs.get(ptId);
					ObsResult p = (ObsResult) lastObsPercent.get(ptId);
					
					Date dateCount = r != null ? r.getDateOfResult() : null;
					Date datePercent = p != null ? p.getDateOfResult() : null;
					
					Date lastResultDate = CalculationUtils.latestDate(dateCount,datePercent);
					
					if ((r == null && p == null) || (daysSince(lastResultDate, context) > KenyaEmrConstants.NEEDS_CD4_COUNT_AFTER_DAYS)) {
							
						needsCD4 = true;
					}
					
					
				}
				ret.put(ptId, new BooleanResult(needsCD4, this, context));
				
			}
		return ret;
	}
}
