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
package org.openmrs.module.kenyaemr.calculation.tb;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;

/**
 * Calculate whether patients are due for a sputum test. Calculation returns
 * true if the patient is alive, and screened for tb, and has cough of any
 * duration probably 2 weeks during the 2 weeks then there should have been no
 * sputum results recorded
 */
public class NeedsSputumCalculation extends BaseAlertCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map,
	 *      org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine whether patients need sputum test
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,
			Map<String, Object> parameterValues,
			PatientCalculationContext context) {

		// get set of patients who are alive
		Set<Integer> alive = alivePatients(cohort, context);

		// get concept for disease suspect
		Concept tbsuspect = Context.getConceptService().getConceptByUuid(
				MetadataConstants.DISEASE_SUSPECTED_CONCEPT_UUID);

		// check if there is any observation recorded per the tuberculosis
		// disease status
		CalculationResultMap lastObsTbDiseaseStatus = lastObs(
				getConcept(MetadataConstants.TUBERCULOSIS_DISEASE_STATUS_CONCEPT_UUID),
				cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsSputum = false;

			// check if a patient is alive
			if (alive.contains(ptId)) {
				// is the patient suspected of TB?
				ObsResult r = (ObsResult) lastObsTbDiseaseStatus.get(ptId);
				if (r != null
						&& (r.getValue().getValueCoded().equals(tbsuspect))) {

					// get the last observation of sputum since tb was suspected
					CalculationResultMap firstObsSinceSuspected = firstObsOnOrAfterDate(
							getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
							r.getDateOfResult(), cohort, context);
					// get the first observation of sputum since the patient was
					// suspected
					ObsResult results = (ObsResult) firstObsSinceSuspected
							.get(ptId);

					if (results == null) {
						needsSputum = true;
					}

				}
				//getting sputum alerts for already enrolled patients
				

			}
			ret.put(ptId, new BooleanResult(needsSputum, this, context));
		}
		return ret;
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation#getAlertMessage()
	 */
	@Override
	public String getAlertMessage() {
		return "Due for Sputum";
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation#getName()
	 */
	@Override
	public String getName() {
		return "Patients Due for Sputum";
	}

	@Override
	public String[] getTags() {
		return new String[] { "alert", "hiv" };
	}

}
