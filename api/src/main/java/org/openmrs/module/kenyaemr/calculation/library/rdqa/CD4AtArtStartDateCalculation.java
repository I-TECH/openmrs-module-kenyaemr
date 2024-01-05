/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.rdqa;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Returns cd4 at art start date
 */
public class CD4AtArtStartDateCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap dataForOtherCalculation = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap questionEvaluationData = evaluateQuestion(cohort, parameterValues, context);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptid : cohort) {
			Double ans = null;
			SimpleResult baseResult = (SimpleResult) dataForOtherCalculation.get(ptid);

			ListResult result = (ListResult) questionEvaluationData.get(ptid);
			List<Obs> obs = CalculationUtils.extractResultValues(result);

			if (baseResult !=null) {
				Date baseDate = (Date) baseResult.getValue();

				for (Obs o : obs) {
					if (baseDate != null) {
						if (obs.size() > 0 && o != null  ) {

							if (setCalendarTime(o.getObsDatetime()).equals(setCalendarTime(baseDate))) {
								ans = o.getValueNumeric();
							}
						}
					}
				}
			}
			ret.put(ptid, new SimpleResult(ans, this));

		}

		return ret;
	}

	private CalculationResultMap evaluateQuestion(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context){
		Concept cd4Concept = Context.getConceptService().getConceptByUuid("5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		ObsForPersonDataDefinition definition = new ObsForPersonDataDefinition("CD4 tests", TimeQualifier.ANY, cd4Concept, null, null);
		return CalculationUtils.evaluateWithReporting(definition, cohort, parameterValues, null, context);
	}

	private Calendar setCalendarTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		return cal;

	}

}
