/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Calculates the list of all possible viral load
 */
public class ViralLoadListCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		Concept HIV_VIRAL_LOAD = Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD);
		CalculationResultMap patientsVL = Calculations.allObs(HIV_VIRAL_LOAD, cohort, context);

		for (Integer ptId : cohort) {

			SortedMap< Date, Double> vlMaps = new TreeMap<Date, Double>();

			ListResult vlObsResult = (ListResult) patientsVL.get(ptId);

			if(vlObsResult != null && !vlObsResult.isEmpty()) {

				List<Obs> viralLoads = CalculationUtils.extractResultValues(vlObsResult);
				List<Obs> targetList = new ArrayList<Obs>();

				if(viralLoads.size() > 1) {
					targetList = viralLoads;
				}
				if(targetList.size() > 3) {
					targetList = viralLoads.subList(viralLoads.size()-3, viralLoads.size());
				}
				for(Obs vlObs : targetList) {
					vlMaps.put( vlObs.getObsDatetime(), vlObs.getValueNumeric());
				}
			}
			if(vlMaps.size() > 1) {
				ret.put(ptId, new SimpleResult(vlMaps, this));
			}

		}
		return ret;
	}
}
