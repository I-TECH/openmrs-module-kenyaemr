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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.SimpleObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculate the date of enrollment into HIV Program
 */
public class SecondLastVLCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		ObsService obsService = Context.getObsService();
		PersonService patientService = Context.getPersonService();

		CalculationResultMap ret = new CalculationResultMap();
		List<Concept> vlConcepts = new ArrayList<Concept>();
		vlConcepts.add(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE));
		vlConcepts.add(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));


		for (Integer ptId : cohort) {
			List<Obs> vlObs = obsService.getObservations(
					Collections.singletonList(patientService.getPerson(ptId)),
					null,
					vlConcepts,
					null,
					null,
					null,
					null,
					2,
					null,
					null,
					null,
					false,
					null
					);
			Obs secondLastVL = null;
			SimpleObject object = null;
			if (vlObs != null && vlObs.size() > 0) {
				if (vlObs.size() > 1) {
					secondLastVL = vlObs.get(1);
				} else {
					secondLastVL = vlObs.get(0);
				}

				if(secondLastVL.getConcept().equals(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD))) {
					object = SimpleObject.create("vl", secondLastVL.getValueNumeric(), "vlDate", secondLastVL.getObsDatetime());
				}
				else {
					object = SimpleObject.create("vl", "LDL", "vlDate", secondLastVL.getObsDatetime());
				}
			}
			ret.put(ptId, new SimpleResult(object, this, context));

		}

		return  ret;
	}
}
