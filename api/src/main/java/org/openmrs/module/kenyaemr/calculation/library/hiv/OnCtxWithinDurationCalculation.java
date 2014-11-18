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

		Date endDate = context.getNow(); // this is the end of the reporting period

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
		CalculationResultMap medOrdersObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);
		CalculationResultMap medDuration = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_DURATION), cohort, context);
		CalculationResultMap medDurationunits = Calculations.lastObs(Dictionary.getConcept(Dictionary.DURATION_UNITS), cohort, context);
		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

		//get the drug components dispensed
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean onCtxOnDuration = false;

			if (inHivProgram.contains(ptId)) {
				Obs ctxMedsObs = EmrCalculationUtils.obsResultForPatient(medOrdersObss, ptId);
				Obs ctxDurationObs = EmrCalculationUtils.obsResultForPatient(medDuration, ptId);
				Obs ctxDurationUnits = EmrCalculationUtils.obsResultForPatient(medDurationunits, ptId);
				if(ctxMedsObs != null && ctxDurationObs != null && ctxDurationUnits != null) {
					if(ctxMedsObs.getValueCoded().equals(ctx) && ctxMedsObs.getObsGroup().equals(ctxDurationObs.getObsGroup()) && ctxMedsObs.getObsGroup().equals(ctxDurationUnits.getObsGroup()) ) {
						Integer durationMonthsDays = Integer.parseInt(ctxDurationObs.getValueNumeric().toString().trim().split("\\.")[0]);

						Date obsDate = ctxMedsObs.getObsDatetime();
						String durationUnits = ctxDurationUnits.getValueCoded().getName().getName().trim();
						Calendar cal = Calendar.getInstance();
						cal.setTime(obsDate);
						if(durationUnits.equals("MONTHS")){
							cal.add(Calendar.MONTH, durationMonthsDays);
						}
						if(durationUnits.equals("DAYS")) {
							cal.add(Calendar.DATE, durationMonthsDays);
						}
						if(durationUnits.equals("WEEKS")) {
							cal.add(Calendar.DATE, (durationMonthsDays * 7));
						}
						if(durationUnits.equals("YEARS")) {
							cal.add(Calendar.YEAR, durationMonthsDays);
						}
						Date nextRefilldate = cal.getTime();
						if(nextRefilldate.compareTo(endDate) >= 0) {
							onCtxOnDuration = true;
						}
					}

				}
			}
			if(ltfu.contains(ptId)){
				onCtxOnDuration = false;
			}

			ret.put(ptId, new BooleanResult(onCtxOnDuration, this));

		}
		return ret;
	}
}
