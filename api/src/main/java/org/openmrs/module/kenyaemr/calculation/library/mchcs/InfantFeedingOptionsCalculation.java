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

package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculation to list feeding options for infants
 */
public class InfantFeedingOptionsCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchcsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHCS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchcsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchcsProgram, alive, context));

		// Get whether  the child has a feeding option
		CalculationResultMap lastChildFeedingOption = Calculations.lastObs(getConcept(Dictionary.INFANT_FEEDING_METHOD), inMchcsProgram, context);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean hasFeedingOption = false;

			Obs feedingOptions = EmrCalculationUtils.obsResultForPatient(lastChildFeedingOption, ptId);

			if (inMchcsProgram.contains(ptId) && feedingOptions != null) {
				hasFeedingOption = true;
			}
			ret.put(ptId, new BooleanResult(hasFeedingOption, this));
		}
		return ret;
	}

}