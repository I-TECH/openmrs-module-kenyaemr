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
package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculate the EDD for those patients in MCHMS program
 */
public class EddEstimateFromMchmsProgramCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

		//find a live patients
		Set<Integer> alive = Filters.alive(cohort, context);
		//only for female patients
		Set<Integer> female = Filters.female(alive, context);
		//get a calculation map of actively enrolled program
		Set<Integer> activeMchmsEnrollment = Filters.inProgram(mchmsProgram, female, context);
		//get LMP obs details
		CalculationResultMap lmp = Calculations.lastObs(Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD), female, context);

		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId:cohort) {
			Date edd = new Date();
			if(activeMchmsEnrollment.contains(ptId)) {
				Obs lmpObs = EmrCalculationUtils.obsResultForPatient(lmp, ptId);
				if(lmpObs != null) {
					edd = CoreUtils.dateAddDays(lmpObs.getValueDate(), 280);
				}
			ret.put(ptId, new SimpleResult(edd, this));
			}
		}
		return ret;
	}
}
