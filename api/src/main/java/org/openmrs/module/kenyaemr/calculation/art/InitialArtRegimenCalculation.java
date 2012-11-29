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

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the initial ART regimen of each patient as a list of drug orders. Returns empty list if patient was never on ART
 */
public class InitialArtRegimenCalculation extends BaseKenyaEmrCalculation {

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation#getShortMessage()
	 */
	@Override
	public String getShortMessage() {
		return "Initial ART Regimen";
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
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		CalculationResultMap ret = firstDrugOrders(arvs, cohort, context);
		CalculationUtils.ensureNullResults(ret, cohort);
		return ret;
	}
}
