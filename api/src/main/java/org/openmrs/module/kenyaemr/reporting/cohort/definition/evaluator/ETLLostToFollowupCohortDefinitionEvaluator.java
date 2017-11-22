package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLLostToFollowupCohortDefinition;
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
 * Evaluator for patients who have missed their appointments for more than 180 days
 */
@Handler(supports = {ETLLostToFollowupCohortDefinition.class})
public class ETLLostToFollowupCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		ETLLostToFollowupCohortDefinition definition = (ETLLostToFollowupCohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		Cohort newCohort = new Cohort();
		String qry=" select  e.patient_id\n" +
				"from (\n" +
				"select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
				" max(fup.visit_date) as latest_vis_date,\n" +
				" mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
				" max(d.visit_date) as date_discontinued,\n" +
				" d.patient_id as disc_patient\n" +
				"from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
				"join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
				"join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
				"-- ensure those discontinued are catered for\n" +
				"left outer JOIN\n" +
				"(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
				"where date(visit_date) <= curdate()  and program_name='HIV'\n" +
				"group by patient_id -- check if this line is necessary\n" +
				") d on d.patient_id = fup.patient_id\n" +
				"where fup.visit_date <= curdate()\n" +
				"group by patient_id\n" +
				"--  we may need to filter lost to follow-up using this\n" +
				"having (\n" +
				"(((date(latest_tca) < curdate()) and (date(latest_vis_date) < date(latest_tca))) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and datediff(curdate(), date(latest_tca)) > 90)\n" +
				"-- drop missd completely\n" +
				") e;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		return new EvaluatedCohort(newCohort, definition, context);
    }

}
