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

import java.util.List;

/**
 * Evaluator for patients for HTS Register - linkage and referral
 */
@Handler(supports = {HTSClientsLinkageRegisterCohortDefinition.class})
public class HTSLinkageRegisterCohortDefinitionEvaluator implements EncounterQueryEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);

		String qry = "SELECT l.encounter_id from kenyaemr_etl.etl_hts_referral_and_linkage l \n" +
				"inner join patient pt on pt.patient_id=l.patient_id and pt.voided=0 inner join kenyaemr_etl.etl_hts_test t on t.patient_id=l.patient_id and t.test_type=1 and t.final_test_result='Positive' and t.visit_date <=l.visit_date and t.voided=0\n" +
				"inner join kenyaemr_etl.etl_hts_test c on c.patient_id=l.patient_id and c.test_type=2 and c.final_test_result='Positive' and c.voided=0 and c.visit_date <=l.visit_date inner join person p on p.person_id=l.patient_id and p.voided=0 where l.voided=0\n" +
				"order by l.patient_id";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);

		List<Integer> results = evaluationService.evaluateToList(builder, Integer.class, context);
		queryResult.getMemberIds().addAll(results);
		return queryResult;
	}

}
