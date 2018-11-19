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

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the date on which an MCH patient or her partner was tested for HIV
 */
public class MchmsHivTestDateCalculation extends AbstractPatientCalculation {

	/**
	 * @should return null for patients who have not tested for HIV
	 * @should return test date for patients who have tested for HIV
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,
										 PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

		// Get all patients who are alive and in MCH-MS program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, alive, context);

		Boolean partner = (params != null && params.containsKey("partner")) ? (Boolean) params.get("partner") : false;
		Concept dateOfHivDiagnosisConcept = partner ? Dictionary.getConcept(Dictionary.DATE_OF_PARTNER_HIV_DIAGNOSIS)
				: Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);

		CalculationResultMap lastHivTestDateObss = Calculations.lastObs(dateOfHivDiagnosisConcept, inMchmsProgram, context);

		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			Date patientsLastHivTestDate = EmrCalculationUtils.datetimeObsResultForPatient(lastHivTestDateObss, ptId);
			if (inMchmsProgram.contains(ptId) && patientsLastHivTestDate != null) {
				resultMap.put(ptId, new SimpleResult(patientsLastHivTestDate, null));
			} else {
				resultMap.put(ptId, null);
			}
		}

		return resultMap;
	}
}