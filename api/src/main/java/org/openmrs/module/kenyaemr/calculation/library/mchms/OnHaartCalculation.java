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

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a mother is on HAART. Calculation returns true if mother
 * is alive, enrolled in the MCH program, is HIV+ and indicated in the last MCH
 * encounter to be on HAART.
 */
public class OnHaartCalculation extends AbstractPatientCalculation {

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

		Concept hivPositiveConcept = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept onHaartConcept = Dictionary.getConcept(Dictionary.MOTHER_ON_HAART);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean onHaart = false;
			if (inMchmsProgram.contains(ptId)) {
				Concept lastHivStatus = EmrCalculationUtils.codedObsResultForPatient(lastHivStatusObss, ptId);
				Concept lastArtStatus = EmrCalculationUtils.codedObsResultForPatient(artStatusObss, ptId);
				boolean hivPositive = false;
				if (lastHivStatus != null) {
					hivPositive = lastHivStatus.equals(hivPositiveConcept);
					if (lastArtStatus != null) {
						onHaart = lastArtStatus.equals(onHaartConcept);
					}
				}
				onHaart = hivPositive && onHaart;
			}
			ret.put(ptId, new BooleanResult(onHaart, this, context));
		}
		return ret;
	}
}
