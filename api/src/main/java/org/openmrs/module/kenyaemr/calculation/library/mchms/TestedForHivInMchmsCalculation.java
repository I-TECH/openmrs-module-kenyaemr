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

import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the period when a patient was tested for HIV. The period may either be Antenatal, Labor and Delivery or
 * Postnatal. The parameter values received determine which specific period and HIV result to base the calculation on.
 * Both the period and HIV result can be set to "any". The calculation returns true if the patient has a known HIV
 * result, was tested in the period specified by the period parameter and the test known result is as specified in the
 * testResultParameter
 */
public class TestedForHivInMchmsCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		MchMetadata.Stage stage = (MchMetadata.Stage) parameterValues.get("stage");
		Concept result = (Concept) parameterValues.get("result");

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHMS);

		Set<Integer> alivePatients = alivePatients(cohort, context);
		Set<Integer> aliveMchmsPatients = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alivePatients, context));

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(getConcept(Dictionary.HIV_STATUS), aliveMchmsPatients, context);
		CalculationResultMap lastHivTestDateObss = Calculations.lastObs(getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), aliveMchmsPatients, context);
		CalculationResultMap lastDeliveryDateObss = Calculations.lastObs(getConcept(Dictionary.DATE_OF_CONFINEMENT), aliveMchmsPatients, context);

		CalculationResultMap lastEnrollmentEncounters = Calculations.lastEncounter(MetadataUtils.getEncounterType(MchMetadata._EncounterType.MCHMS_ENROLLMENT), cohort, context);

		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			Concept patientsLastHivStatus = EmrCalculationUtils.codedObsResultForPatient(lastHivStatusObss, ptId);
			Date patientsLastHivTestDate = EmrCalculationUtils.datetimeObsResultForPatient(lastHivTestDateObss, ptId);
			CalculationResult lastEnrollmentCalculatioResult = lastEnrollmentEncounters.get(ptId);
			boolean qualified = false;
			if (aliveMchmsPatients.contains(ptId)
					&& (patientsLastHivStatus != null && !patientsLastHivStatus.equals(Dictionary.getConcept(Dictionary.NOT_HIV_TESTED)))
					&& (lastEnrollmentCalculatioResult != null && ((Encounter) lastEnrollmentEncounters.get(ptId).getValue()).getEncounterDatetime().before(patientsLastHivTestDate))) {
				Date enrollmentDate = ((Encounter) lastEnrollmentEncounters.get(ptId).getValue()).getEncounterDatetime();
				Date deliveryDate =  EmrCalculationUtils.datetimeObsResultForPatient(lastDeliveryDateObss, ptId);
	            if (deliveryDate != null && deliveryDate.before(enrollmentDate)) {
					deliveryDate = null;
				}
				qualified = qualifiedByStage(stage, enrollmentDate, patientsLastHivTestDate, deliveryDate)
						&& qualifiedByHivStatus(result, patientsLastHivStatus);
				resultMap.put(ptId, new BooleanResult(qualified, this, context));
			} else {
				resultMap.put(ptId, new BooleanResult(qualified, this, context));
			}
		}

		return resultMap;
	}

	protected boolean qualifiedByHivStatus(Concept requested, Concept found) {
		if (requested == null) {
			return true;
		} else {
			return requested.equals(found);
		}
	}

	protected boolean qualifiedByStage(MchMetadata.Stage stage, Date enrollmentDate, Date testDate, Date deliveryDate) {
		if (stage.equals(MchMetadata.Stage.ANY)) {
			return true;
		} else {
			if (deliveryDate == null) {
				if (stage.equals(MchMetadata.Stage.ANTENATAL)) {
					return true;
				} else {
					return false;
				}
			} else {
				Date lowerLimit = null;
				Date upperLimit = null;
				DateTime beginningOfEnrollmentDate = new DateTime(enrollmentDate).toDateMidnight().toDateTime();
				DateTime beginningOfDeliveryDate = new DateTime(deliveryDate).toDateMidnight().toDateTime();
				DateTime endOfDeliveryDate = beginningOfDeliveryDate.plusDays(1);
				if (stage.equals(MchMetadata.Stage.ANTENATAL)) {
					lowerLimit = beginningOfEnrollmentDate.toDate();
					upperLimit = beginningOfDeliveryDate.plusDays(-2).toDate();
				} else if (stage.equals(MchMetadata.Stage.DELIVERY)) {
					lowerLimit = beginningOfDeliveryDate.plusDays(-2).toDate();
					upperLimit = endOfDeliveryDate.toDate();
				} else if (stage.equals(MchMetadata.Stage.POSTNATAL)) {
					lowerLimit = endOfDeliveryDate.toDate();
					upperLimit = endOfDeliveryDate.plusDays(3).toDate();
				}
				return testDate.after(lowerLimit) && testDate.before(upperLimit);
			}
		}
	}
}
