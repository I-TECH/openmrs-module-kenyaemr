/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.log4j.Level;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;

public class ScheduledVisitOnDayCalculation extends AbstractPatientCalculation {
	private static Logger logger = Logger.getLogger(ScheduledVisitOnDayCalculation.class.getName());
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {
//		Date date = (Date) parameterValues.get("date");
//		if (date == null) {
//			date = new Date();
//		}
		
		if(parameterValues != null){
			logger.info("Parameter Values is NOT null");
			logger.info(parameterValues.toString());
		}else{
			
			
			logger.info("Parameter Values is null");
		}
		Date date = new Date();

		Date startOfDay = DateUtil.getStartOfDay(date);
		Date endOfDay = DateUtil.getEndOfDay(date);
		Concept returnVisitDate = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
		//EncounterType tbFollowup = MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_CONSULTATION);

		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(returnVisitDate);
		cd.setOperator1(RangeComparator.GREATER_EQUAL);
		cd.setValue1(startOfDay);
		cd.setOperator2(RangeComparator.LESS_EQUAL);
		cd.setValue2(endOfDay);
		//cd.setEncounterTypeList(Collections.singletonList(tbFollowup));

		EvaluatedCohort withScheduledVisit = CalculationUtils.evaluateWithReporting(cd, cohort, null, context);

		CalculationResultMap ret = new CalculationResultMap();
		
		for (Integer ptId : cohort) {
			ret.put(ptId, new BooleanResult(withScheduledVisit.contains(ptId), this));
		}
		
		return ret;

	}

}
