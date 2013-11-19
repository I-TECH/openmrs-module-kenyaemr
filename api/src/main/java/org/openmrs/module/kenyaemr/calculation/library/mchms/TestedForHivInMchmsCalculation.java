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

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.PregnancyStage;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the {@link org.openmrs.module.kenyaemr.PregnancyStage} in which a patient or her partner was tested for HIV.
 *
 * @params A map of parameters values specifying the {@link org.openmrs.module.kenyaemr.PregnancyStage}, the HIV result
 * and whether the calculation is intended for the patient herself or for her partner
 *
 * @return
 */
public class TestedForHivInMchmsCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		PregnancyStage stage = (params != null && params.containsKey("stage")) ? (PregnancyStage) params.get("stage") : null;
		Concept result = (params != null && params.containsKey("result")) ? (Concept) params.get("result") : null;
		Boolean partner = (params != null && params.containsKey("partner")) ? (Boolean) params.get("partner") : false;

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHMS);

		Set<Integer> alivePatients = alivePatients(cohort, context);
		CalculationResultMap activePatientPrograms = Calculations.activeEnrollment(mchmsProgram, alivePatients, context);

		Set<Integer> aliveMchmsPatients = CalculationUtils.patientsThatPass(activePatientPrograms);

		Concept hivStatusConcept = partner ? getConcept(Dictionary.PARTNER_HIV_STATUS) : getConcept(Dictionary.HIV_STATUS);
		Concept hivTestDateConcept = partner ? getConcept(Dictionary.DATE_OF_PARTNER_HIV_DIAGNOSIS) : getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(hivStatusConcept, aliveMchmsPatients, context);
		CalculationResultMap lastHivTestDateObss = Calculations.lastObs(hivTestDateConcept, aliveMchmsPatients, context);
		CalculationResultMap lastDeliveryDateObss = Calculations.lastObs(getConcept(Dictionary.DATE_OF_CONFINEMENT), aliveMchmsPatients, context);

		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			Concept patientsLastHivStatus = EmrCalculationUtils.codedObsResultForPatient(lastHivStatusObss, ptId);
			Date patientsLastHivTestDate = EmrCalculationUtils.datetimeObsResultForPatient(lastHivTestDateObss, ptId);

			CalculationResult activePatientProgram = activePatientPrograms.get(ptId);

			boolean qualified = false;
			if (aliveMchmsPatients.contains(ptId)
					&& (patientsLastHivStatus != null &&
					(patientsLastHivStatus.equals(Dictionary.getConcept(Dictionary.POSITIVE))
							|| patientsLastHivStatus.equals(Dictionary.getConcept(Dictionary.NEGATIVE))))
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

	/**
	 * Determines if the a patient is in a given {@link org.openmrs.module.kenyaemr.PregnancyStage} in the MCH-MS
	 * program given their date of enrollment, date of HIV test and date of delivery.
	 *
	 * @return true if the patient is in the specified stage and false otherwise.
	 */
	protected boolean qualifiedByStage(PregnancyStage stage, Date enrollmentDate, Date testDate, Date deliveryDate) {

		Date lowerLimit = null;
		Date upperLimit = null;
		Date beginningOfEnrollmentDate = OpenmrsUtil.firstSecondOfDay(enrollmentDate);
		Date beginningOfDeliveryDate = null;
		Date endOfDeliveryDate = null;

		if (deliveryDate != null) {
			beginningOfDeliveryDate = OpenmrsUtil.firstSecondOfDay(deliveryDate);
			endOfDeliveryDate = CoreUtils.dateAddDays(beginningOfDeliveryDate, 1);
		}

		if (stage == null) {
			if (endOfDeliveryDate != null) {
				upperLimit = CoreUtils.dateAddDays(endOfDeliveryDate, 3);
				return upperLimit.after(testDate);
			} else {
				return true;
			}
		} else if (stage.equals(PregnancyStage.BEFORE_ENROLLMENT)) {
			return enrollmentDate.after(testDate);
		} else {
			if (stage.equals(PregnancyStage.AFTER_ENROLLMENT)) {
				if (endOfDeliveryDate != null) {
					upperLimit = CoreUtils.dateAddDays(endOfDeliveryDate, 3);
					return enrollmentDate.before(testDate) && upperLimit.after(testDate);
				} else {
					return enrollmentDate.before(testDate);
				}
			} else {
				if (deliveryDate == null) {
					if (stage.equals(PregnancyStage.ANTENATAL)) {
						return true;
					} else {
						return false;
					}
				} else {
					if (stage.equals(PregnancyStage.ANTENATAL)) {
						lowerLimit = beginningOfEnrollmentDate;
						upperLimit = CoreUtils.dateAddDays(beginningOfDeliveryDate, -2);
					} else if (stage.equals(PregnancyStage.DELIVERY)) {
						lowerLimit = CoreUtils.dateAddDays(beginningOfDeliveryDate, -2);
						upperLimit = endOfDeliveryDate;
					} else if (stage.equals(PregnancyStage.POSTNATAL)) {
						lowerLimit = endOfDeliveryDate;
						upperLimit = CoreUtils.dateAddDays(endOfDeliveryDate, 3);
					}
					return testDate.after(lowerLimit) && testDate.before(upperLimit);
				}
			}
		}
	}
}
