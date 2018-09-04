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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
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
 * Calculates whether a mother's HIV status was known or established at enrollment.
 * Calculation returns true if mother is alive, enrolled in the MCH program and has
 * either a +ve or -ve HIV status specified..
 */
public class HivTestedAtEnrollmentCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

		// Get all patients who are alive and in MCH-MS program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, alive, context);

		CalculationResultMap hivStatusObs = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_STATUS), inMchmsProgram, context);
		CalculationResultMap hivTestDateObs = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), inMchmsProgram, context);

		Concept notHivTestedConcept = Dictionary.getConcept(Dictionary.NOT_HIV_TESTED);

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap crm = Calculations.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT), cohort, context);
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean hivTestedAtEnrollment = false;
			if (inMchmsProgram.contains(ptId)) {
				Concept hivStatus = EmrCalculationUtils.codedObsResultForPatient(hivStatusObs, ptId);
				Date hivTestDate = EmrCalculationUtils.datetimeObsResultForPatient(hivTestDateObs, ptId);
				if (hivStatus != null && !hivStatus.equals(notHivTestedConcept)) {
					if (hivTestDate != null) {
						Date enrollmentDate = ((Encounter) crm.get(ptId).getValue()).getDateCreated();
						hivTestedAtEnrollment = (hivTestDate.before(enrollmentDate)
								|| hivTestDate.equals(enrollmentDate));
					}
				}
			}
			ret.put(ptId, new BooleanResult(hivTestedAtEnrollment, this, context));
		}
		return ret;
	}
}
