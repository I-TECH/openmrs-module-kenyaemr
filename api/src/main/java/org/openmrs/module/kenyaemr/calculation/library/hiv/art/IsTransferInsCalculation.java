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

package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient is a transfer in
 */
public class IsTransferInsCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);
		Concept transferInStatus = Dictionary.getConcept(Dictionary.TRANSFER_IN);

		//pick the transfer in date if available
		CalculationResultMap transferInDateResults = Calculations.lastObs(transferInDate,inHivProgram,context);
		CalculationResultMap transferInStatusResults = Calculations.lastObs(transferInStatus,inHivProgram,context);

		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean isTransferIn = false;
			//check if the patient is in hiv program
			if (inHivProgram.contains(ptId)) {
				Date date = EmrCalculationUtils.datetimeObsResultForPatient(transferInDateResults, ptId);
				Concept status = EmrCalculationUtils.codedObsResultForPatient(transferInStatusResults, ptId);

				if (((status != null) && (status.equals(Dictionary.getConcept(Dictionary.YES)))) || (date != null)) {
					isTransferIn = true;
				}
			}
			result.put(ptId, new SimpleResult(isTransferIn, this, context));
		}
		return result;
	}
}
