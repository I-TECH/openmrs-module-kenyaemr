/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.openmrs.Encounter;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class MissedLastAppointmentCalculation extends AbstractPatientCalculation{

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should calculate false for deceased patients
	 * @should calculate false for patients with no return visit date obs
	 * @should calculate false for patients with return visit date obs whose value is in the future
	 * @should calculate false for patients with encounter after return visit date obs value
	 * @should calculate true for patients with no encounter after return visit date obs value
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program cwcProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inCwcProgram = Filters.inProgram(cwcProgram, alive, context);

		CalculationResultMap lastReturnDateObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), alive, context);
		CalculationResultMap lastEncounters = Calculations.lastEncounter(null, cohort, context);
		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
		Set<Integer> transferredOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean missedVisit = false;

			// Is patient alive
			if (alive.contains(ptId) && inCwcProgram.contains(ptId) && !(ltfu.contains(ptId)) && !(transferredOut.contains(ptId))) {
				Date lastScheduledReturnDate = EmrCalculationUtils.datetimeObsResultForPatient(lastReturnDateObss, ptId);

				// Does patient have a scheduled return visit in the past
				if (lastScheduledReturnDate != null && EmrCalculationUtils.daysSince(lastScheduledReturnDate, context) > 0) {
					// Has patient returned since
					Encounter lastEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounters, ptId);
					Date lastActualReturnDate = lastEncounter != null ? lastEncounter.getEncounterDatetime() : null;
					missedVisit = lastActualReturnDate == null || lastActualReturnDate.before(lastScheduledReturnDate);
					if(missedVisit && lastEncounter != null && lastEncounter.getEncounterDatetime().after(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), -1, DurationUnit.DAYS)) && lastEncounter.getEncounterDatetime().before(DateUtil.adjustDate(context.getNow(), 1, DurationUnit.DAYS))){
						missedVisit = false;
					}
				}

			}
			ret.put(ptId, new SimpleResult(missedVisit, this, context));
		}
		return ret;
	}

}
