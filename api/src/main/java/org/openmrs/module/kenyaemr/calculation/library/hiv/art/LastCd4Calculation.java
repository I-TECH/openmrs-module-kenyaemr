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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculate the last cd4 count value for a patient
 */
public class LastCd4Calculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		CalculationResultMap allCd4s = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
		CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Cd4ValueAndDate cd4ValueAndDate = null;
			Date artInitiationDt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
			ListResult listResult = (ListResult) allCd4s.get(ptId);
			List<Obs> allObsList = CalculationUtils.extractResultValues(listResult);
			List<Obs> validListObs = new ArrayList<Obs>();

			if(allObsList.size() > 0 && artInitiationDt != null ) {
				for(Obs obs:allObsList) {
					if(obs.getObsDatetime().after(artInitiationDt) && daysBetween(obs.getObsDatetime(), artInitiationDt) <= 183) {
						validListObs.add(obs);
					}
				}

			}

			if(validListObs.size() > 0) {
				cd4ValueAndDate = new Cd4ValueAndDate(validListObs.get(validListObs.size() - 1).getValueNumeric(), validListObs.get(validListObs.size() - 1).getObsDatetime());
			}
			ret.put(ptId, new SimpleResult(cd4ValueAndDate, this));
		}
		return ret;
	}

	private  int daysBetween(Date date1, Date date2) {
		DateTime d1 = new DateTime(date1.getTime());
		DateTime d2 = new DateTime(date2.getTime());
		return Days.daysBetween(d1, d2).getDays();
	}
}