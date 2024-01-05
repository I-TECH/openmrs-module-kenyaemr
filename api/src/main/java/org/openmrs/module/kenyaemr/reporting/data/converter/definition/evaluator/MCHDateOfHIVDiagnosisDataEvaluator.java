/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.MCHDateOfHIVDiagnosisDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates a DateOfHIVDiagnosisDataDefinition
 */
@Handler(supports = MCHDateOfHIVDiagnosisDataDefinition.class, order = 50)
public class MCHDateOfHIVDiagnosisDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "select c.patient_id, c.date_diagnosed_hiv_positive\n"
		        + "from (select e.patient_id,\n"
		        + "             least(ifnull(date(e.hiv_test_date), '9999-12-31'),\n"
		        + "                   ifnull(date(a.anc_visit_date), '9999-12-31'), ifnull(date(m.mat_visit_date), '9999-12-31'),\n"
		        + "                   ifnull(date(p.pnc_visit_date), '9999-12-31'), ifnull(date(t.visit_date), '9999-12-31'),\n"
		        + "                   ifnull(date(h.date_confirmed_hiv_positive), '9999-12-31')) as date_diagnosed_hiv_positive\n"
		        + "      from kenyaemr_etl.etl_mch_enrollment e\n"
		        + "               left join (select t.patient_id, t.visit_date\n"
		        + "                          from kenyaemr_etl.etl_hts_test t\n"
		        + "                          where t.final_test_result = 'Positive') t\n"
		        + "                         on e.patient_id = t.patient_id\n"
		        + "               left join (select h.patient_id, h.date_confirmed_hiv_positive from kenyaemr_etl.etl_hiv_enrollment h) h\n"
		        + "                         on h.patient_id = e.patient_id\n"
		        + "               left join(select a.patient_id, a.visit_date as anc_visit_date\n"
		        + "                         from kenyaemr_etl.etl_mch_antenatal_visit a\n"
		        + "                         where a.final_test_result = 'Positive') a on e.patient_id = a.patient_id\n"
		        + "               left join(select m.patient_id, m.visit_date as mat_visit_date\n"
		        + "                         from kenyaemr_etl.etl_mchs_delivery m\n"
		        + "                         where m.final_test_result = 'Positive') m on e.patient_id = m.patient_id\n"
		        + "               left join(select p.patient_id, p.visit_date as pnc_visit_date\n"
		        + "                         from kenyaemr_etl.etl_mch_postnatal_visit p\n"
		        + "                         where p.final_test_result = 'Positive') p on e.patient_id = p.patient_id\n"
		        + "      where date(e.visit_date) between date(:startDate) and date(:endDate)\n"
		        + "      group by e.patient_id) c;";
		
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
		Date startDate = (Date) context.getParameterValue("startDate");
		Date endDate = (Date) context.getParameterValue("endDate");
		queryBuilder.addParameter("endDate", endDate);
		queryBuilder.addParameter("startDate", startDate);
		queryBuilder.append(qry);
		Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
		c.setData(data);
		return c;
	}
}
