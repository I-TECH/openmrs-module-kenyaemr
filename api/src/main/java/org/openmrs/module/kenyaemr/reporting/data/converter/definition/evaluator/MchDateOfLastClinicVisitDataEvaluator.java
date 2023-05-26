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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.MchDateOfLastClinicVisitDataDefinition;
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
 * Evaluates a MchDateOfLastClinicVisitDataDefinition
 */
@Handler(supports = MchDateOfLastClinicVisitDataDefinition.class, order = 50)
public class MchDateOfLastClinicVisitDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "select e.patient_id,\n"
		        + "       greatest(max(e.visit_date),ifnull(a.latest_anc_visit_date, ''), ifnull(m.latest_mat_visit_date, ''),\n"
		        + "                ifnull(p.latest_pnc_visit_date, '')) as latest_visit_date\n"
		        + "from kenyaemr_etl.etl_mch_enrollment e\n"
		        + "         left join(select a.patient_id,\n"
		        + "                          max(a.visit_date)                                                 as latest_anc_visit_date,\n"
		        + "                          mid(max(concat(date(a.visit_date), a.next_appointment_date)), 11) as anc_next_appointment_date\n"
		        + "                   from kenyaemr_etl.etl_mch_antenatal_visit a\n"
		        + "                   where date(a.visit_date) <= date(:endDate)\n"
		        + "                   group by a.patient_id) a on e.patient_id = a.patient_id\n"
		        + "         left join(select m.patient_id, max(m.visit_date) as latest_mat_visit_date\n"
		        + "                   from kenyaemr_etl.etl_mchs_delivery m\n"
		        + "                   where date(m.visit_date) <= date(:endDate)\n"
		        + "                   group by m.patient_id) m on e.patient_id = m.patient_id\n"
		        + "         left join(select p.patient_id,\n"
		        + "                          max(p.visit_date)                                            as latest_pnc_visit_date,\n"
		        + "                          mid(max(concat(date(p.visit_date), p.appointment_date)), 11) as pnc_next_appointment_date\n"
		        + "                   from kenyaemr_etl.etl_mch_postnatal_visit p\n"
		        + "                   where date(p.visit_date) <= date(:endDate)\n"
		        + "                   group by p.patient_id) p on e.patient_id = p.patient_id\n"
		        + "where date(e.visit_date) between date(:startDate) and date(:endDate)\n" + "group by e.patient_id;";
		
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
