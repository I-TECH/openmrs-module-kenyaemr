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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculate the date of enrollment into HIV Program
 */
public class DateOfEnrollmentCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {
		Set<Integer> transferinPatients = CalculationUtils.patientsThatPass(calculate(new IsTransferInCalculation(), cohort, context));
		CalculationResultMap enrolledHere = Calculations.firstEncounter(MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT), cohort, context);
		CalculationResultMap dateEnrolledIntoHivFromFacility = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_ENROLLED_IN_HIV_CARE), cohort, context);

		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date enrollmentDate = null;
			Encounter encounter = EmrCalculationUtils.encounterResultForPatient(enrolledHere, ptId);
			Obs dateHivStarted = EmrCalculationUtils.obsResultForPatient(dateEnrolledIntoHivFromFacility, ptId);
			if((encounter != null) && (!(transferinPatients.contains(ptId)))){
				enrollmentDate = encounter.getEncounterDatetime();
			}
			if((transferinPatients.contains(ptId)) && (dateHivStarted != null)) {
				enrollmentDate = dateHivStarted.getValueDate();
			}
			result.put(ptId, new SimpleResult(enrollmentDate, this));
		}

		return  result;
	}
}
