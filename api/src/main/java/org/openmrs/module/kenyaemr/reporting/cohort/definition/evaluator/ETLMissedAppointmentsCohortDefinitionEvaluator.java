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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLMissedAppointmentsCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for patients who have missed their appointments
 */
@Handler(supports = {ETLMissedAppointmentsCohortDefinition.class})
public class ETLMissedAppointmentsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		ETLMissedAppointmentsCohortDefinition definition = (ETLMissedAppointmentsCohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		Cohort newCohort = new Cohort();
		String qry="select t.patient_id\n" +
				"from (select fup.visit_date,\n" +
				"             fup.patient_id,\n" +
				"             max(e.visit_date)                                                                                        as enroll_date,\n" +
				"             greatest(max(e.visit_date),\n" +
				"                      ifnull(max(date(e.transfer_in_date)), '0000-00-00'))                                            as latest_enrolment_date,\n" +
				"             greatest(max(fup.visit_date),\n" +
				"                      ifnull(max(d.visit_date), '0000-00-00'))                                                        as latest_vis_date,\n" +
				"             greatest(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11),\n" +
				"                      ifnull(max(d.visit_date), '0000-00-00'))                                                        as latest_tca,\n" +
				"             greatest(mid(max(concat(fup.visit_date, fup.refill_date)), 11),\n" +
				"                      ifnull(max(d.visit_date), '0000-00-00'))                                                        as refill_tca,\n" +
				"             d.patient_id                                                                                             as disc_patient,\n" +
				"             d.effective_disc_date                                                                                    as effective_disc_date,\n" +
				"             max(d.visit_date)                                                                                        as date_discontinued,\n" +
				"             d.discontinuation_reason,\n" +
				"             de.patient_id                                                                                            as started_on_drugs,\n" +
				"             k.patient_id                                                                                             as fast_track_patient,\n" +
				"             k.latest_fast_track_visit_date\n" +
				"      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
				"               left join (select k.patient_id, max(k.visit_date) as latest_fast_track_visit_date\n" +
				"                          from kenyaemr_etl.etl_art_fast_track k\n" +
				"                          group by k.patient_id) k on fup.patient_id = k.patient_id\n" +
				"               join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
				"               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
				"               left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program = 'HIV' and\n" +
				"                                                                 date(date_started) <= date(curdate())\n" +
				"               left outer JOIN\n" +
				"           (select patient_id,\n" +
				"                   coalesce(date(effective_discontinuation_date), visit_date) visit_date,\n" +
				"                   max(date(effective_discontinuation_date)) as               effective_disc_date,\n" +
				"                   discontinuation_reason\n" +
				"            from kenyaemr_etl.etl_patient_program_discontinuation\n" +
				"            where date(visit_date) <= date(curdate())\n" +
				"              and program_name = 'HIV'\n" +
				"            group by patient_id) d on d.patient_id = fup.patient_id\n" +
				"      where fup.visit_date <= date(curdate())\n" +
				"      group by patient_id\n" +
				"      having (\n" +
				"                     ((timestampdiff(DAY, date(refill_tca), date(curdate())) between 1 and 30 and\n" +
				"                       refill_tca > latest_vis_date and\n" +
				"                       (latest_fast_track_visit_date is null or latest_fast_track_visit_date > date(curdate()))) or\n" +
				"                      (timestampdiff(DAY, date(latest_tca), date(curdate())) between 1 and 30 /*and (latest_vis_date > refill_tca )*/)) and\n" +
				"                     ((date(d.effective_disc_date) > date(curdate()) or\n" +
				"                       date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null)\n" +
				"                     and\n" +
				"                     ((date(latest_vis_date) > date(date_discontinued) and date(latest_tca) > date(date_discontinued) or\n" +
				"                       disc_patient is null)\n" +
				"                         or (date(latest_fast_track_visit_date) > date(date_discontinued) and\n" +
				"                             date(refill_tca) > date(date_discontinued) or disc_patient is null))\n" +
				"                 )) t;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		return new EvaluatedCohort(newCohort, definition, context);
    }

}
