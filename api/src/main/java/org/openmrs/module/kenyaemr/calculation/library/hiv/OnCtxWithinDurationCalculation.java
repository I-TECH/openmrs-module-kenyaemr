/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients have taken CTX for a period of time based on end of reporting period
 */
public class OnCtxWithinDurationCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		//Date endDate = context.getNow(); // this is the end of the reporting period

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
		CalculationResultMap medOrdersObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);
		CalculationResultMap medDuration = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_DURATION), cohort, context);
		CalculationResultMap medDurationunits = Calculations.lastObs(Dictionary.getConcept(Dictionary.DURATION_UNITS), cohort, context);
		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
		Set<Integer> hasTCA = CalculationUtils.patientsThatPass(calculate(new NextOfVisitHigherThanContextCalculation(), cohort, context));
		CalculationResultMap medicationDispensed = Calculations.lastObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), cohort, context);

		//get the drug components dispensed
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);
		Concept dapson = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept days = Dictionary.getConcept(Dictionary.DAYS);
		Concept months = Dictionary.getConcept(Dictionary.MONTHS);
		Concept weeks = Dictionary.getConcept(Dictionary.WEEKS);
		Concept years = Dictionary.getConcept(Dictionary.YEARS);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean onCtxOnDuration = false;

			if (inHivProgram.contains(ptId)) {
				Obs ctxMedsObs = EmrCalculationUtils.obsResultForPatient(medOrdersObss, ptId);
				Obs ctxDurationObs = EmrCalculationUtils.obsResultForPatient(medDuration, ptId);
				Obs ctxDurationUnits = EmrCalculationUtils.obsResultForPatient(medDurationunits, ptId);
				Obs medicationDispensedObs = EmrCalculationUtils.obsResultForPatient(medicationDispensed, ptId);
				if(ctxMedsObs != null) {
					if(ctxMedsObs.getValueCoded().equals(ctx) || ctxMedsObs.getValueCoded().equals(dapson)) {
						//check if duration and units are given
						Calendar cal = Calendar.getInstance();
						cal.setTime(ctxMedsObs.getObsDatetime());

						if(ctxDurationObs != null && ctxDurationUnits != null && ctxMedsObs.getObsGroup().equals(ctxDurationObs.getObsGroup()) && ctxMedsObs.getObsGroup().equals(ctxDurationUnits.getObsGroup())) {
							Integer durationMonthsDays = Integer.parseInt(ctxDurationObs.getValueNumeric().toString().trim().split("\\.")[0]);

							Date obsDate = ctxMedsObs.getObsDatetime();
							Concept durationUnits = ctxDurationUnits.getValueCoded();
							cal.setTime(obsDate);
							if (durationUnits.equals(months)) {
								cal.add(Calendar.MONTH, durationMonthsDays);
							}
							else if (durationUnits.equals(days)) {
								cal.add(Calendar.DATE, durationMonthsDays);
							}
							else if (durationUnits.equals(weeks)) {
								cal.add(Calendar.DATE, (durationMonthsDays * 7));
							}
							else if (durationUnits.equals(years)) {
								cal.add(Calendar.YEAR, durationMonthsDays);
							}
						}
						Date nextRefilldate = cal.getTime();
						if(nextRefilldate.after(DateUtil.getStartOfMonth(context.getNow()))) {
							onCtxOnDuration = true;
						}
					}

				}

				if(medicationDispensedObs != null && medicationDispensedObs.getValueCoded().equals(yes) && hasTCA.contains(ptId)){
					onCtxOnDuration = true;
				}

				if(ctxMedsObs != null && ctxMedsObs.getValueCoded().equals(ctx) && hasTCA.contains(ptId)){
					onCtxOnDuration = true;
				}

				if(ctxMedsObs != null && ctxMedsObs.getValueCoded().equals(dapson) && hasTCA.contains(ptId)){
					onCtxOnDuration = true;
				}

				if(ltfu.contains(ptId)){
					onCtxOnDuration = false;
				}
			}
			ret.put(ptId, new BooleanResult(onCtxOnDuration, this));

		}
		return ret;
	}
}
