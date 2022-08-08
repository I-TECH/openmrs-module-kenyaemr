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

import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculate whether patients are due for a CD4 count. Calculation returns true if if the patient
 * is alive, enrolled in the HIV program, and has not had a CD4 count in the last 180 days
 */
public class NeedsCd4TestCalculation extends AbstractPatientCalculation {

	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,y
	 *
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine whether patients need a CD4
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

		CalculationResultMap lastObsCount = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
		CalculationResultMap lastObsPercent = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);
		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
		CalculationResultMap startedArt = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap medOrdersObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsCD4 = false;
			//check if the ever started ART
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(startedArt, ptId);

			// Is patient alive and in the HIV program
			if(inHivProgram.contains(ptId)) {
				if (artStartDate == null) {

					// Does patient have CD4 or CD4% result in the last X days
					ObsResult r = (ObsResult) lastObsCount.get(ptId);
					ObsResult p = (ObsResult) lastObsPercent.get(ptId);

					Date dateCount = r != null ? r.getDateOfResult() : null;
					Date datePercent = p != null ? p.getDateOfResult() : null;

					Date lastResultDate = CoreUtils.latest(dateCount, datePercent);

					if (lastResultDate == null || (daysSince(lastResultDate, context) > HivConstants.NEEDS_CD4_COUNT_AFTER_DAYS)) {
						needsCD4 = true;
					}

					if (ltfu.contains(ptId)) {
						needsCD4 = false;
					}
				} else if (artStartDate != null) {
					Obs fluconazoleObs = EmrCalculationUtils.obsResultForPatient(medOrdersObss, ptId);

					if (fluconazoleObs != null && fluconazoleObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.FLUCONAZOLE))) {
						// Does patient have CD4 or CD4% result in the last X days
						ObsResult r = (ObsResult) lastObsCount.get(ptId);
						ObsResult p = (ObsResult) lastObsPercent.get(ptId);

						Date dateCount = r != null ? r.getDateOfResult() : null;
						Date datePercent = p != null ? p.getDateOfResult() : null;

						Date lastResultDate = CoreUtils.latest(dateCount, datePercent);

						if (lastResultDate == null || (daysSince(lastResultDate, context) > HivConstants.NEEDS_CD4_COUNT_AFTER_DAYS)) {
							needsCD4 = true;
						}

						if (ltfu.contains(ptId)) {
							needsCD4 = false;
						}
					}
				}
			}
			ret.put(ptId, new BooleanResult(needsCD4, this, context));
		}
		return ret;
	}
}
