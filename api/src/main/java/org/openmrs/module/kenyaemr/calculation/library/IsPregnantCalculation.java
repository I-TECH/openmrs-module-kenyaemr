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

package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the recorded pregnancy status of patients
 */
public class IsPregnantCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	@Override
	public String getFlagMessage() {
		return "Pregnant";
	}

    /**
	 * Evaluates the calculation
     * @should calculate null for deceased patients
	 * @should calculate null for patients with no recorded status
	 * @should calculate last recorded pregnancy status for all patients
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);
		EncounterType mchEnrollment = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		EncounterType mchPostNatal = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
		EncounterType mchDiscontinuation = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_DISCONTINUATION);

		Concept yes = Dictionary.getConcept(Dictionary.YES);
		CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), aliveAndFemale, context);
		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap enrollmentMap = Calculations.lastEncounter(mchEnrollment, aliveAndFemale, context);
		CalculationResultMap postNatal = Calculations.lastEncounter(mchPostNatal, aliveAndFemale, context);
		CalculationResultMap discontinuation = Calculations.lastEncounter(mchDiscontinuation, aliveAndFemale, context);
		CalculationResultMap confinementDateMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT), cohort, context);

		for (Integer ptId : cohort) {
			boolean result = false;

			Obs pregStatusObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);
			Encounter encounter = EmrCalculationUtils.encounterResultForPatient(enrollmentMap, ptId);
			Encounter encounterPostNatal = EmrCalculationUtils.encounterResultForPatient(postNatal, ptId);
			Encounter encounterDiscontinuation = EmrCalculationUtils.encounterResultForPatient(discontinuation, ptId);
			Obs confinement = EmrCalculationUtils.obsResultForPatient(confinementDateMap, ptId);

			if (pregStatusObs != null && pregStatusObs.getValueCoded().equals(yes)) {
				result = true;
			}

			if(encounter != null) {
				result = true;
			}
			if(encounter != null && encounterPostNatal != null && encounterPostNatal.getEncounterDatetime().after(encounter.getEncounterDatetime())){
				result = false;
			}

			if(encounter != null && encounterDiscontinuation != null && encounterDiscontinuation.getEncounterDatetime().after(encounter.getEncounterDatetime())){
				result = false;
			}
			if(pregStatusObs !=null && confinement != null && confinement.getValueDatetime().after(pregStatusObs.getObsDatetime())) {
				result = false;
			}

			if(encounter !=null && confinement != null && confinement.getValueDatetime().after(encounter.getEncounterDatetime())) {
				result = false;
			}

			ret.put(ptId, new BooleanResult(result, this));
		}

		return ret;
    }
}