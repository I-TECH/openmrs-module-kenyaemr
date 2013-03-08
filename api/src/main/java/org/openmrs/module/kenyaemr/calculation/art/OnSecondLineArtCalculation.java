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

package org.openmrs.module.kenyaemr.calculation.art;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.regimen.*;

/**
 * Calculates whether patients are on second-line ART regimens
 */
public class OnSecondLineArtCalculation extends BaseKenyaEmrCalculation {

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation#getShortMessage()
	 */
	@Override
	public String getShortMessage() {
		return "Patients on Second Line ART";
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation#getSinglePatientMessage()
	 */
	@Override
	public String getSinglePatientMessage() {
		return "On Second Line ART";
	}

	@Override
	public String[] getTags() {
		return new String[] { "hiv" };
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext context) {

		// Get active ART regimen of each patient
		CalculationResultMap patientArvs = calculate(new CurrentArtRegimenCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean onSecondLine = false;
			SimpleResult arvResult = (SimpleResult) patientArvs.get(ptId);

			if (arvResult != null) {
				RegimenOrder currentRegimen = (RegimenOrder) arvResult.getValue();

				List<RegimenDefinition> matchingDefinitions = KenyaEmr.getInstance().getRegimenManager().findDefinitions("ARV", currentRegimen, false);
				for (RegimenDefinition definition : matchingDefinitions) {
					if ("adult-second".equals(definition.getGroup().getCode())) {
						onSecondLine = true;
						break;
					}
				}
			}

			ret.put(ptId, new BooleanResult(onSecondLine, this, context));
		}
		return ret;
    }
}