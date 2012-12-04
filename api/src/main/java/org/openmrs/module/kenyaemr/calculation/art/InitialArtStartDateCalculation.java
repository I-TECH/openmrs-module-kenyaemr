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
import java.util.Date;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

/**
 * Calculates the date on which a patient first started ART. Calculation returns the earliest data value for each patient
 * considering all ARV drug orders and ANTIRETROVIRAL_TREATMENT_START_DATE_CONCEPT_UUID obs
 */
public class InitialArtStartDateCalculation extends BaseKenyaEmrCalculation {
	
	/**
	 * @see BaseKenyaEmrCalculation#getShortMessage()
	 */
	@Override
	public String getShortMessage() {
		return "First ART Start Date";
	}

	@Override
	public String[] getTags() {
		return new String[] { "hiv" };
	}
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {

		// Get earliest dates from orders
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		CalculationResultMap earliestOrderDates = earliestStartDates(allDrugOrders(arvs, cohort, context), context);

		// Get dates from obs used when patient is transferred in
		CalculationResultMap obsDates = firstObs(MetadataConstants.ANTIRETROVIRAL_TREATMENT_START_DATE_CONCEPT_UUID, cohort, context);

		// Return the earliest of the two
		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date orderDate = CalculationUtils.datetimeResultForPatient(earliestOrderDates, ptId);
			Date obsDate = CalculationUtils.datetimeObsResultForPatient(obsDates, ptId);
			Date earliest = CalculationUtils.earliestDate(orderDate, obsDate);

			result.put(ptId, earliest == null ? null : new SimpleResult(earliest, null));
		}
		return result;
	}
}