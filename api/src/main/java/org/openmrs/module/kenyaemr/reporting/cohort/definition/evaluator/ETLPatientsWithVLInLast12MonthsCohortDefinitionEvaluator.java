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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLPatientsWithVLInLast12MonthsCohortDefinition;
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
 * Evaluator for Current on ART
 */
@Handler(supports = {ETLPatientsWithVLInLast12MonthsCohortDefinition.class})
public class ETLPatientsWithVLInLast12MonthsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		ETLPatientsWithVLInLast12MonthsCohortDefinition definition = (ETLPatientsWithVLInLast12MonthsCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry="select a.patient_id as patient_id\n" +
				"from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency from (select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
				"     greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
				"     greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
				"     greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
				"     d.patient_id as disc_patient,\n" +
				"     d.effective_disc_date as effective_disc_date,\n" +
				"     max(d.visit_date) as date_discontinued,\n" +
				"     de.patient_id as started_on_drugs,\n" +
				"     de.date_started\n" +
				"from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
				"     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
				"     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
				"     left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(curdate())\n" +
				"     left outer JOIN\n" +
				"       (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
				"        where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
				"        group by patient_id\n" +
				"       ) d on d.patient_id = fup.patient_id\n" +
				"where fup.visit_date <= date(curdate())\n" +
				"group by patient_id\n" +
				"having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
				"  (\n" +
				"      ((timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(curdate()) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
				"        and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
				"      )\n" +
				"  ) order by date_started desc\n" +
				") t\n" +
				"inner join (\n" +
				"select\n" +
				"patient_id,encounter_id,\n" +
				"max(visit_date) as vl_date,\n" +
				"date_sub(curdate() , interval 12 MONTH),\n" +
				"if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
				"mid(max(concat(visit_date,urgency)),11) as urgency\n" +
				"from kenyaemr_etl.etl_laboratory_extract\n" +
				"group by patient_id\n" +
				"having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
				"date_sub(curdate() , interval 12 MONTH) and date(curdate())\n" +
				")vl\n" +
				"on t.patient_id = vl.patient_id)a;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date endDate = (Date)context.getParameterValue("endDate");
		//Date startDate = (Date)context.getParameterValue("startDate");
		//builder.addParameter("startDate", startDate);
		builder.addParameter("endDate", endDate);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

		newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}
