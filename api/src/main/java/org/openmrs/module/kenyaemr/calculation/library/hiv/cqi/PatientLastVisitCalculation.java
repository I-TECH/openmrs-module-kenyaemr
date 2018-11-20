/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.cqi;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.report.data.patient.definition.VisitsForPatientDataDefinition;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.TimeQualifier;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 * Calculates patients with their last visit information
 */
public class PatientLastVisitCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		Calendar calendarSixMonths = Calendar.getInstance();
		calendarSixMonths.setTime(context.getNow());
		calendarSixMonths.add(Calendar.MONTH, -6);

		VisitsForPatientDataDefinition visitsDef = new VisitsForPatientDataDefinition();
		visitsDef.setWhich(TimeQualifier.LAST);
		visitsDef.setStartedOnOrAfter(calendarSixMonths.getTime());
		visitsDef.setStartedOnOrBefore(context.getNow());

		CalculationResultMap visitData = CalculationUtils.evaluateWithReporting(visitsDef, cohort, params, null, context);

		for (Integer ptId: cohort) {
			boolean hadIcfCardCompleted = false;
				SimpleResult result = (SimpleResult) visitData.get(ptId);
				if (result != null) {
					Visit visit = (Visit) result.getValue();
					if (visit.getEncounters() != null) {
						for (Encounter encounter : visit.getEncounters()) {
							if (encounter != null) {
								if (encounter.getEncounterType().equals(MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_SCREENING))) {
									hadIcfCardCompleted = true;
								}
							}
						}
					}
			}
			ret.put(ptId, new BooleanResult(hadIcfCardCompleted, this, context));
		}
		return ret;
	}
}
