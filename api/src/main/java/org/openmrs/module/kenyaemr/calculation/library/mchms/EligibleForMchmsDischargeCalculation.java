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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.StablePatientsCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Determines whether a mother is eligible for discharge -- should access the discharge form
 * Criteria:
 * 1.client is alive
 * 2.Should be mchms enrolled
 * 3.Should atleast have a delivery from  dated greater than active enrollment
 */
public class EligibleForMchmsDischargeCalculation extends AbstractPatientCalculation {
	protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {


		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

		Set<Integer> alivePatients = Filters.alive(cohort, context);
		CalculationResultMap activePatientPrograms = Calculations.activeEnrollment(mchmsProgram, alivePatients, context);

		Set<Integer> aliveMchmsPatients = CalculationUtils.patientsThatPass(activePatientPrograms);

		CalculationResultMap lastDeliveryDateObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT), aliveMchmsPatients, context);


		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {

			CalculationResult activePatientProgram = activePatientPrograms.get(ptId);

			boolean eligible = false;

			if (aliveMchmsPatients.contains(ptId) && (activePatientProgram != null)) {

				Date enrollmentDate = ((PatientProgram) activePatientProgram.getValue()).getDateEnrolled();
				Date deliveryDate = EmrCalculationUtils.datetimeObsResultForPatient(lastDeliveryDateObss, ptId);
				//if (deliveryDate != null && deliveryDate.after(enrollmentDate)) { check whether current delivery
				if (deliveryDate != null) {
					eligible = true;
				}
			}
//			log.info("Eligible for discharge ==> "+eligible);
			ret.put(ptId, new BooleanResult(eligible, this, context));
		}

		return ret;
	}
}