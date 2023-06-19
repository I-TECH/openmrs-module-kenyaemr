/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.pmtct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.MissedHAARTCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for pregnant or breastfeeding mothers who missed HAART
 */
@Handler(supports = { MissedHAARTCohortDefinition.class })
public class MissedHAARTCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	EvaluationService evaluationService;
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		
		MissedHAARTCohortDefinition definition = (MissedHAARTCohortDefinition) cohortDefinition;
		
		if (definition == null)
			return null;
		
		Cohort newCohort = new Cohort();
		
		String qry = "select a.mch_client from (select e.patient_id as mch_client,\n"
		        + "       max(date(e.visit_date)) as latest_enrollment_date,\n"
		        + "       date(x.disc_date) as disc_mch_date,\n"
		        + "       d.patient_id as on_drugs_client,\n"
		        + "       x.patient_id as disc_mch_client,\n"
		        + "       e.hiv_status as hiv_at_mch,\n"
		        + "       t.patient_id as hts_client,\n"
		        + "       h.patient_id as in_hiv_program,\n"
		        + "       h.disc_patient as disc_from_hiv,\n"
		        + "       h.enrollment_date as hiv_enrollment_date,\n"
		        + "       h.disc_date as hiv_disc_date,\n"
		        + "       n.patient_id as anc_client,\n"
		        + "       m.patient_id as maternity_client,\n"
		        + "       p.patient_id as postnatal_client\n"
		        + "from kenyaemr_etl.etl_mch_enrollment e\n"
		        + "         left join (select d.patient_id, d.date_started, d.regimen\n"
		        + "                    from kenyaemr_etl.etl_drug_event d\n"
		        + "                    where d.program = 'HIV') d on e.patient_id = d.patient_id\n"
		        + "         left join (select t.patient_id, t.final_test_result\n"
		        + "                    from kenyaemr_etl.etl_hts_test t\n"
		        + "                    where t.final_test_result = 'Positive') t\n"
		        + "                   on e.patient_id = t.patient_id\n"
		        + "         left join (select h.patient_id,\n"
		        + "                           max(h.visit_date) as enrollment_date,\n"
		        + "                           d.patient_id      as disc_patient,\n"
		        + "                           d.visit_date      as disc_date\n"
		        + "                    from kenyaemr_etl.etl_hiv_enrollment h\n"
		        + "                             left join (select patient_id,\n"
		        + "                                               coalesce(date(effective_discontinuation_date), visit_date) visit_date,\n"
		        + "                                               max(date(effective_discontinuation_date)) as               effective_disc_date\n"
		        + "                                        from kenyaemr_etl.etl_patient_program_discontinuation\n"
		        + "                                        where date(visit_date) <= date(:endDate)\n"
		        + "                                          and program_name = 'HIV'\n"
		        + "                                        group by patient_id) d on h.patient_id = d.patient_id\n"
		        + "                    group by h.patient_id) h\n"
		        + "                   on h.patient_id = e.patient_id\n"
		        + "         left join(select n.patient_id, n.visit_date as anc_visit_date\n"
		        + "                   from kenyaemr_etl.etl_mch_antenatal_visit n\n"
		        + "                   where n.final_test_result = 'Positive') n on e.patient_id = n.patient_id\n"
		        + "         left join(select m.patient_id, m.visit_date as mat_visit_date\n"
		        + "                   from kenyaemr_etl.etl_mchs_delivery m\n"
		        + "                   where m.final_test_result = 'Positive') m on e.patient_id = m.patient_id\n"
		        + "         left join(select p.patient_id, p.visit_date as pnc_visit_date\n"
		        + "                   from kenyaemr_etl.etl_mch_postnatal_visit p\n"
		        + "                   where p.final_test_result = 'Positive') p on e.patient_id = p.patient_id\n"
		        + "         left join (select x.patient_id, max(date(x.visit_date)) as disc_date\n"
		        + "                    from kenyaemr_etl.etl_patient_program_discontinuation x\n"
		        + "                    where x.program_name = 'MCH Mother'\n"
		        + "                    group by x.patient_id) x on e.patient_id = x.patient_id\n"
		        + "where date(e.visit_date) <= date(:endDate) group by e.patient_id)a\n"
		        + "  where (a.disc_mch_client is null or (a.latest_enrollment_date > date(a.disc_mch_date)))\n"
		        + "  and a.on_drugs_client is null\n"
		        + "  and (a.hiv_at_mch = 703 or a.hts_client is not null or\n"
		        + "       (a.in_hiv_program is not null and (a.disc_from_hiv is null or date(a.hiv_enrollment_date) > date(a.hiv_disc_date))) or\n"
		        + "       a.anc_client is not null or\n"
		        + "       a.maternity_client is not null or a.postnatal_client is not null);";
		
		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date startDate = (Date) context.getParameterValue("startDate");
		Date endDate = (Date) context.getParameterValue("endDate");
		builder.addParameter("startDate", startDate);
		builder.addParameter("endDate", endDate);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		
		return new EvaluatedCohort(newCohort, definition, context);
	}
	
}
