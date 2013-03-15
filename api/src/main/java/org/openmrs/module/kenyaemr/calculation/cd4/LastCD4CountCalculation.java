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

package org.openmrs.module.kenyaemr.calculation.cd4;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the last CD4 count of patients
 */
public class LastCD4CountCalculation extends BaseEmrCalculation {

    @Override
    public String getName() {
        return "Last CD4 Count";
    }

	@Override
	public String[] getTags() {
		return new String[] { "hiv" };
	}

    /**
     * Evaluates the calculation
     * @should calculate null for patients with no recorded CD4 count
	 * @should calculate last CD4 count for all patients with a recorded CD4 count
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		return lastObs(getConcept(MetadataConstants.CD4_CONCEPT_UUID), cohort, context);
    }
}