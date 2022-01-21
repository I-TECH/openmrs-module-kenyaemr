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
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculate the bmi at last visit
 */
public class BMIAtLastVisitCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap weightMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.WEIGHT_KG), cohort, context);
		CalculationResultMap heightMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.HEIGHT_CM), cohort, context);
		CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), cohort, context);

		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);
		Set<Integer> inMchcsProgram = Filters.inProgram(mchcsProgram, cohort, context);

		PersonService service = Context.getPersonService();


		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {

			Double visitWeight = null;
			Double visitHeight = null;
			String bmiStr = null;

			Obs lastWeightObs = EmrCalculationUtils.obsResultForPatient(weightMap, ptId);
			Obs lastHeightObs = EmrCalculationUtils.obsResultForPatient(heightMap, ptId);
			//find pregnancy obs
			Obs pregnantStatus = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);

			if (lastHeightObs !=null && lastWeightObs != null && service.getPerson(ptId).getAge() > 12 && !(inMchcsProgram.contains(ptId) || (pregnantStatus != null && pregnantStatus.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))))){
				visitHeight = lastHeightObs.getValueNumeric();
				visitWeight = lastWeightObs.getValueNumeric();
				Double bmi = visitWeight / ((visitHeight/100) * (visitHeight/100));
				bmiStr = String.format("%.2f", bmi);
				ret.put(ptId, new SimpleResult(bmiStr, this, context));
			}
		}

		return  ret;
	}
}
