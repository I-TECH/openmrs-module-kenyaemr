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
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient has a declining CD4 count. Calculation returns true if patient
 * is alive, enrolled in the HIV program and last CD4 count is less than CD4 count from 6 months ago
 */
public class DecliningCd4Calculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Declining CD4";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

		// Get the two CD4 obss for comparison
		CalculationResultMap lastCD4Obss = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_COUNT), inHivProgram, context);
		CalculationResultMap oldCD4Obss = Calculations.lastObsAtLeastDaysAgo(Dictionary.getConcept(Dictionary.CD4_COUNT), HivConstants.DECLINING_CD4_COUNT_ACROSS_DAYS, inHivProgram, context);
		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean declining = false;

			// Is patient alive and in HIV program?
			if (inHivProgram.contains(ptId)) {
				Double lastCD4Count = EmrCalculationUtils.numericObsResultForPatient(lastCD4Obss, ptId);
				Double oldCD4Count = EmrCalculationUtils.numericObsResultForPatient(oldCD4Obss, ptId);

				if (lastCD4Count != null && oldCD4Count != null) {
					declining = lastCD4Count < oldCD4Count;
				}
				if(ltfu.contains(ptId)){
					declining = false;
				}
			}
			ret.put(ptId, new BooleanResult(declining, this, context));
		}
		return ret;
	}
}
