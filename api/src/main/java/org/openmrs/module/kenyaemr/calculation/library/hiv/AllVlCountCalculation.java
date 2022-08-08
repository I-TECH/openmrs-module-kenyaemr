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
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.ui.framework.SimpleObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Calculate the date of enrollment into HIV Program
 */
public class AllVlCountCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		ObsService obsService = Context.getObsService();
		PersonService patientService = Context.getPersonService();

		CalculationResultMap ret = new CalculationResultMap();
		List<Concept> vlConcepts = new ArrayList<Concept>();
		vlConcepts.add(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE));
		vlConcepts.add(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
		List<SimpleObject> vlRet = new ArrayList<SimpleObject>();
		DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

		for (Integer ptId : cohort) {
			List<Obs> vlObs = obsService.getObservations(
					Collections.singletonList(patientService.getPerson(ptId)),
					null,
					vlConcepts,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					false,
					null
					);

			for (int i = 0; i < vlObs.size(); ++i) {
				if(vlObs.get(i).getConcept().equals(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD))) {
					vlRet.add(SimpleObject.create(
							"vl", vlObs.get(i).getValueNumeric(),
							"vlDate", dateFormatter.format(vlObs.get(i).getObsDatetime())
					));
				}
				else {
					vlRet.add(SimpleObject.create(
							"vl", "LDL",
							"vlDate", dateFormatter.format(vlObs.get(i).getObsDatetime())
					));
				}
			}

			ret.put(ptId, new SimpleResult(vlRet, this, context));

		}

		return  ret;
	}
}
