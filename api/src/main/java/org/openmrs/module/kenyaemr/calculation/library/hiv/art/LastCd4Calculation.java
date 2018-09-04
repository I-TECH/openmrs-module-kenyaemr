/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

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
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

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
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,
										 PatientCalculationContext context) {

		Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;


		CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

		if(outcomePeriod != null) {
			context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
		}
		CalculationResultMap allCd4s = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Cd4ValueAndDate cd4ValueAndDate = null;
			Date artInitiationDt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
			ListResult listResult = (ListResult) allCd4s.get(ptId);
			List<Obs> allObsList = CalculationUtils.extractResultValues(listResult);
			List<Obs> validListObs = new ArrayList<Obs>();

			if(allObsList.size() > 0 && artInitiationDt != null && outcomePeriod != null) {
				Date outcomeDate = DateUtil.adjustDate(DateUtil.adjustDate(artInitiationDt, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
				Date outcomeDate183DaysBefore = DateUtil.adjustDate(outcomeDate, -184, DurationUnit.DAYS);
				for(Obs obs:allObsList) {
					if(obs.getObsDatetime().before(outcomeDate) && obs.getObsDatetime().after(outcomeDate183DaysBefore)) {
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

	private  Date dateLimit(Date date1, Integer days) {

		return DateUtil.adjustDate(date1, days, DurationUnit.DAYS);
	}
}