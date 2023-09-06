/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.MissedAppointmentsDuringPeriodCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.evaluator.EncounterQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Evaluator for patients who have missed their appointments during a reporting period
 */
@Handler(supports = {MissedAppointmentsDuringPeriodCohortDefinition.class})
public class MissedAppointmentsDuringPeriodCohortDefinitionEvaluator implements EncounterQueryEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
    @Override
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);

		if (definition == null)
			return null;

		String qry="select encounter_id\n" +
				"from (select fup.encounter_id,fup.patient_id, honoredVisit.patient_id honored_appt, honoured_refill.patient_id as honouredRefill\n" +
				"      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
				"               left join kenyaemr_etl.etl_patient_hiv_followup honoredVisit\n" +
				"                         on honoredVisit.patient_id = fup.patient_id and\n" +
				"                            honoredVisit.next_appointment_date = fup.visit_date and honoredVisit.visit_date > fup.visit_date\n" +
				"               left join kenyaemr_etl.etl_art_fast_track honoured_refill\n" +
				"                         on honoured_refill.patient_id = fup.patient_id and honoured_refill.visit_date = fup.refill_date and honoured_refill.visit_date > fup.visit_date\n" +
				"               join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
				"               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
				"      where date(fup.next_appointment_date) between date(:startDate) and date(:endDate)\n" +
				"         or date(fup.refill_date) between date(:startDate) and date(:endDate)\n" +
				"      group by fup.encounter_id, fup.patient_id\n" +
				"      having honored_appt is null\n" +
				"         and honouredRefill is null) mApp;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("endDate", endDate);
		builder.addParameter("startDate", startDate);
		builder.append(qry);

		List<Integer> results = evaluationService.evaluateToList(builder, Integer.class, context);
		queryResult.getMemberIds().addAll(results);
		return queryResult;
    }

}
