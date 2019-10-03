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
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsLinkageRegisterCohortDefinition;
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
 * Evaluator for patients for HTS Register - linkage and referral
 * returns patients who tested positive and either were directly enrolled in HIV program
 * or had referral and linkage form filled with the linkage details
 */
@Handler(supports = {HTSClientsLinkageRegisterCohortDefinition.class})
public class HTSClientsLinkageRegisterCohortDefinitionEvaluator implements EncounterQueryEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);

		String qry = "(SELECT l.encounter_id\n" +
				"from kenyaemr_etl.etl_hts_referral_and_linkage l\n" +
				"inner join kenyaemr_etl.etl_patient_demographics pt on pt.patient_id=l.patient_id and pt.voided=0\n" +
				"inner join kenyaemr_etl.etl_hts_test t on t.patient_id=l.patient_id and t.test_type in(1,2) and t.final_test_result='Positive' and t.visit_date <=l.visit_date and t.voided=0\n" +
				"where l.ccc_number is not null and facility_linked_to is not null and date(l.visit_date) BETWEEN date(:startDate) AND date(:endDate)\n" +
				")\n" +
				"union\n" +
				"( SELECT t.encounter_id\n" +
				"FROM kenyaemr_etl.etl_hts_test t\n" +
				"INNER JOIN kenyaemr_etl.etl_patient_demographics pt ON pt.patient_id=t.patient_id AND pt.voided=0\n" +
				"INNER JOIN kenyaemr_etl.etl_hiv_enrollment e ON e.patient_id=t.patient_id AND e.voided=0\n" +
				"LEFT JOIN kenyaemr_etl.etl_hts_referral_and_linkage l ON l.patient_id=t.patient_id\n" +
				"WHERE t.test_type = 1 AND t.final_test_result='Positive' AND t.voided=0 AND l.patient_id IS NULL\n" +
				"  AND date(e.visit_date) BETWEEN date(:startDate) AND date(:endDate)\n" +
				")";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("endDate", endDate);
		builder.addParameter("startDate", startDate);

		List<Integer> results = evaluationService.evaluateToList(builder, Integer.class, context);
		queryResult.getMemberIds().addAll(results);
		return queryResult;
	}

}
