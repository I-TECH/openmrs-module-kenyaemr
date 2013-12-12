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
import org.joda.time.Weeks;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a mother is HIV+ but is not on ART. Calculation returns true if mother
 * is alive, enrolled in the MCH program, gestation is greater than 14 weeks, is HIV+ and was
 * not indicated as being on ART in the last encounter.
 */
public class NotOnArtCalculation extends BaseEmrCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Not on ART";
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHMS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchmsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alive, context));

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(getConcept(Dictionary.HIV_STATUS), inMchmsProgram, context);
		CalculationResultMap artStatusObss = Calculations.lastObs(getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY), inMchmsProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean notOnArt = false;
			if (inMchmsProgram.contains(ptId)) {
				Concept lastHivStatus = EmrCalculationUtils.codedObsResultForPatient(lastHivStatusObss, ptId);
				Concept lastArtStatus = EmrCalculationUtils.codedObsResultForPatient(artStatusObss, ptId);
				boolean hivPositive = false;
				boolean onArt = false;
				if (lastHivStatus != null) {
					hivPositive = lastHivStatus.equals(Dictionary.getConcept(Dictionary.POSITIVE));
					if (lastArtStatus != null) {
						onArt = !lastArtStatus.equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE));
					}
				}
				notOnArt = hivPositive && gestationIsGreaterThan14Weeks(ptId) && !onArt;
			}
			ret.put(ptId, new BooleanResult(notOnArt, this, context));
		}
		return ret;
	}

	/**
	 * @return true if the given patient's gestation is greater than 14 weeks at enrollment and false otherwise
	 * */
	protected boolean gestationIsGreaterThan14Weeks(Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		EncounterType encounterType = MetadataUtils.getEncounterType(MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		Encounter lastMchEnrollment = EmrUtils.lastEncounter(patient, encounterType);
		Obs lmpObs = EmrUtils.firstObsInEncounter(lastMchEnrollment, Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD));
		if (lmpObs != null) {
			Weeks weeks = Weeks.weeksBetween(new DateTime(lmpObs.getValueDate()), new DateTime(new Date()));
			if (weeks.getWeeks() > 14) {
				return true;
			}
		}
		return false;
	}
}
