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
package org.openmrs.module.kenyaemr.calculation.library.rdqa;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.CD4VLValueAndDate;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Returns latest CD4 result for patient in care and latest VL for a patient on art
 */
public class LastCD4OrVLResultCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap lastCD4Map = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap lastVLMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {

            Obs lastCd4Obs = EmrCalculationUtils.obsResultForPatient(lastCD4Map, ptId);
            Obs lastVLObs = EmrCalculationUtils.obsResultForPatient(lastVLMap, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
            CD4VLValueAndDate obsResult = new CD4VLValueAndDate();

            /**
             * Check if a patient is started on art and pick the last VL result and only CD4 count in case of no VL
             * CD4 takes precedence for patients in care
             */
            if (artStartDate != null) {
                   if (lastVLObs != null) {
                       obsResult.setConcept("vl");
                       obsResult.setValue(lastVLObs.getValueNumeric());
                       obsResult.setDate(lastVLObs.getObsDatetime());
                   } else if (lastCd4Obs != null) {
                       obsResult.setConcept("cd4");
                       obsResult.setValue(lastCd4Obs.getValueNumeric());
                       obsResult.setDate(lastCd4Obs.getObsDatetime());
                   }

            }   else { //this is for a patient who is in care. CD4 takes precedence
                    if (lastCd4Obs != null) {
                        obsResult.setConcept("cd4");
                        obsResult.setValue(lastCd4Obs.getValueNumeric());
                        obsResult.setDate(lastCd4Obs.getObsDatetime());
                    } else if (lastVLObs != null) {
                        obsResult.setConcept("vl");
                        obsResult.setValue(lastVLObs.getValueNumeric());
                        obsResult.setDate(lastVLObs.getObsDatetime());
                    }
            }

			ret.put(ptId, new SimpleResult(obsResult, this));

		}
		return ret;
	}
}
