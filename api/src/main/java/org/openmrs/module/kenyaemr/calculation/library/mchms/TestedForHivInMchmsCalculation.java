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
import org.openmrs.PatientProgram;
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
import org.openmrs.util.OpenmrsUtil;

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
		CalculationResultMap activePatientPrograms = Calculations.activeEnrollment(mchmsProgram, alivePatients, context);

		Set<Integer> aliveMchmsPatients = CalculationUtils.patientsThatPass(activePatientPrograms);

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(getConcept(Dictionary.HIV_STATUS), aliveMchmsPatients, context);
		CalculationResultMap lastHivTestDateObss = Calculations.lastObs(getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), aliveMchmsPatients, context);
		CalculationResultMap lastDeliveryDateObss = Calculations.lastObs(getConcept(Dictionary.DATE_OF_CONFINEMENT), aliveMchmsPatients, context);

		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			Concept patientsLastHivStatus = EmrCalculationUtils.codedObsResultForPatient(lastHivStatusObss, ptId);
			Date patientsLastHivTestDate = EmrCalculationUtils.datetimeObsResultForPatient(lastHivTestDateObss, ptId);

			CalculationResult activePatientProgram = activePatientPrograms.get(ptId);

			boolean qualified = false;
			if (aliveMchmsPatients.contains(ptId)
					&& (patientsLastHivStatus != null && !patientsLastHivStatus.equals(Dictionary.getConcept(Dictionary.NOT_HIV_TESTED)))
					&& (activePatientProgram != null)) {
				Date enrollmentDate = ((PatientProgram) activePatientProgram.getValue()).getDateEnrolled();
				Date deliveryDate = EmrCalculationUtils.datetimeObsResultForPatient(lastDeliveryDateObss, ptId);
				if (deliveryDate != null && deliveryDate.before(enrollmentDate)) {
					deliveryDate = null;
				}
				qualified = qualifiedByStage(stage, enrollmentDate, patientsLastHivTestDate, deliveryDate)
						&& (result == null ? true : result.equals(patientsLastHivStatus));
				resultMap.put(ptId, new BooleanResult(qualified, this, context));
			}
			resultMap.put(ptId, new BooleanResult(qualified, this, context));
		}

		return resultMap;
	}

	protected boolean qualifiedByStage(MchMetadata.Stage stage, Date enrollmentDate, Date testDate, Date deliveryDate) {

		Date lowerLimit = null;
		Date upperLimit = null;
		Date beginningOfEnrollmentDate = OpenmrsUtil.firstSecondOfDay(enrollmentDate);
		Date beginningOfDeliveryDate = null;
		Date endOfDeliveryDate = null;

		if (deliveryDate != null) {
			beginningOfDeliveryDate = OpenmrsUtil.firstSecondOfDay(deliveryDate);
			endOfDeliveryDate = new DateTime(beginningOfDeliveryDate).plusDays(1).toDate();
		}

		if (stage.equals(MchMetadata.Stage.ANY)) {
			if (endOfDeliveryDate != null) {
				upperLimit = new DateTime(endOfDeliveryDate).plusDays(3).toDate();
				return upperLimit.after(testDate);
			} else {
				return true;
			}
		} else if (stage.equals(MchMetadata.Stage.BEFORE_ENROLLMENT)) {
			return enrollmentDate.after(testDate);
		} else {
			if (stage.equals(MchMetadata.Stage.AFTER_ENROLLMENT)) {
				if (endOfDeliveryDate != null) {
					upperLimit = new DateTime(endOfDeliveryDate).plusDays(3).toDate();
					return enrollmentDate.before(testDate) && upperLimit.after(testDate);
				} else {
					return enrollmentDate.before(testDate);
				}
			} else {
				if (deliveryDate == null) {
					if (stage.equals(MchMetadata.Stage.ANTENATAL)) {
						return true;
					} else {
						return false;
					}
				} else {
					if (stage.equals(MchMetadata.Stage.ANTENATAL)) {
						lowerLimit = beginningOfEnrollmentDate;
						upperLimit = new DateTime(beginningOfDeliveryDate).plusDays(-2).toDate();
					} else if (stage.equals(MchMetadata.Stage.DELIVERY)) {
						lowerLimit = new DateTime(beginningOfDeliveryDate).plusDays(-2).toDate();
						upperLimit = new DateTime(endOfDeliveryDate).toDate();
					} else if (stage.equals(MchMetadata.Stage.POSTNATAL)) {
						lowerLimit = endOfDeliveryDate;
						upperLimit = new DateTime(endOfDeliveryDate).plusDays(3).toDate();
					}
					return testDate.after(lowerLimit) && testDate.before(upperLimit);
				}
			}
		}
	}
}
