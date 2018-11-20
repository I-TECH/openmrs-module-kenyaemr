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

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.EligibleForArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a mother is HIV+ but is not on ART. Calculation returns true if mother
 * is alive, enrolled in the MCH program, gestation is greater than 14 weeks, is HIV+ and was
 * not indicated as being on ART in the last encounter.
 */
public class NotOnArtCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

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

		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

		// Get all patients who are alive and in MCH-MS program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, alive, context);

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_STATUS), inMchmsProgram, context);
		CalculationResultMap artStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY), inMchmsProgram, context);
		CalculationResultMap lmpObss = Calculations.firstObs(Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD), inMchmsProgram, context);
		Set<Integer> onART = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));
		Set<Integer> eligibleForArt = CalculationUtils.patientsThatPass(calculate(new EligibleForArtCalculation(), cohort, context));

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean notOnArt = false;
			if (inMchmsProgram.contains(ptId) && !(onART.contains(ptId)) && !(eligibleForArt.contains(ptId))) {
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
