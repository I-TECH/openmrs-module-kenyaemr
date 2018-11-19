/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
