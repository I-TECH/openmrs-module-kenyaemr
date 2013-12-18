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

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Calculates the current ART regimen of each patient as a list of drug orders. Returns empty list if patient is not on ART
 */
public class CurrentArtRegimenCalculation extends BaseEmrCalculation {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {
		Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);
		CalculationResultMap currentARVDrugOrders = activeDrugOrders(arvs, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			ListResult patientDrugOrders = (ListResult) currentARVDrugOrders.get(ptId);

			if (patientDrugOrders != null) {
				RegimenOrder regimen = new RegimenOrder(new HashSet<DrugOrder>(CalculationUtils.<DrugOrder>extractResultValues(patientDrugOrders)));
				ret.put(ptId, new SimpleResult(regimen, this, context));
			}
			else {
				ret.put(ptId, null);
			}
		}
		return ret;
	}
}