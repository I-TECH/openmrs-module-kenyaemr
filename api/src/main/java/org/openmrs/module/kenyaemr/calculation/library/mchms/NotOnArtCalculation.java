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

package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a mother is HIV+ but is not on ART. Calculation returns true if mother
 * is alive, enrolled in the MCH program, gestation is greater than 14 weeks, is HIV+ and was
 * not indicated as being on ART in the last encounter.
 */
public class NotOnArtCalculation extends BaseEmrCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Not on ART";
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHMS);

		// Get all patients who are alive and in MCH-MS program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, alive, context);

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_STATUS), inMchmsProgram, context);
		CalculationResultMap artStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY), inMchmsProgram, context);
		CalculationResultMap lmpObss = Calculations.firstObs(Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD), inMchmsProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean notOnArt = false;
			if (inMchmsProgram.contains(ptId)) {
				Concept lastHivStatus = EmrCalculationUtils.codedObsResultForPatient(lastHivStatusObss, ptId);
				Concept lastArtStatus = EmrCalculationUtils.codedObsResultForPatient(artStatusObss, ptId);
				Date lastLmpDate = EmrCalculationUtils.datetimeObsResultForPatient(lmpObss, ptId);

				boolean hivPositive = false;
				boolean onArt = false;
				if (lastHivStatus != null) {
					hivPositive = lastHivStatus.equals(Dictionary.getConcept(Dictionary.POSITIVE));
					if (lastArtStatus != null) {
						onArt = !lastArtStatus.equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE));
					}
				}
				notOnArt = hivPositive && gestationIsGreaterThan14Weeks(lastLmpDate) && !onArt;
			}
			ret.put(ptId, new BooleanResult(notOnArt, this, context));
		}
		return ret;
	}

	/**
	 * @return true if the given patient's gestation is greater than 14 weeks and false otherwise
	 */
	protected boolean gestationIsGreaterThan14Weeks(Date lmpDate) {
		if (lmpDate != null) {
			Weeks weeks = Weeks.weeksBetween(new DateTime(lmpDate), new DateTime(new Date()));
			if (weeks.getWeeks() > 14) {
				return true;
			}
		}
		return false;
	}
}
