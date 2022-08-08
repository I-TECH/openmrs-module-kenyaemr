/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.ovc;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the partner's name offering ovc services
 */
public class ImplementingPartnerSupportingOVCCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
                                         PatientCalculationContext context) {
		Concept partnerSupporting = Context.getConceptService().getConcept(165347);
		CalculationResultMap currentPartner = Calculations.lastObs(partnerSupporting, cohort, context);
		
		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			String partnerName = null;
			Obs partner = EmrCalculationUtils.obsResultForPatient(currentPartner, ptId);
			if(partner != null) {
				partnerName = partner.getValueText();
			}
			result.put(ptId, new SimpleResult(partnerName, this));
		}
		return result;
	}
}
