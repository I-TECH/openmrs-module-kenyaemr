package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLMissedAppointmentsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsLinkageRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsTestedPositiveNotLinkedCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.evaluator.EncounterQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for patients for HTS Register - linkage and referral: those who tested positive but not linked
 */
@Handler(supports = {HTSClientsTestedPositiveNotLinkedCohortDefinition.class})
public class HTSClientsTestedPositiveNotLinkedCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		HTSClientsTestedPositiveNotLinkedCohortDefinition definition = (HTSClientsTestedPositiveNotLinkedCohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		Cohort newCohort = new Cohort();
		String qry=" SELECT clientTested from (\n" +
				"SELECT t.patient_id clientTested, l.patient_id hasLinkageRecord, l.tracing_status, p.patient_id enrolledInHiv\n" +
				"from kenyaemr_etl.etl_hts_test t\n" +
				"left join patient_program p on p.patient_id = t.patient_id and p.voided=0 and p.program_id not in (select program_id from program where uuid='dfdc6d40-2f2f-463d-ba90-cc97350441a8')\n" +
				"left join \n" +
				"(select l.patient_id, mid(max(concat(l.visit_date, l.tracing_status)), 11) tracing_status  \n" +
				"from kenyaemr_etl.etl_hts_referral_and_linkage l \n" +
				"where l.voided=0\n" +
				"group by l.patient_id\n" +
				") l on l.patient_id=t.patient_id\n" +
				"where t.final_test_result = 'Positive' and t.voided = 0 and t.test_type=2 and datediff(curdate(), t.visit_date) div 365.25 < 1\n" +
				") a \n" +
				"where (hasLinkageRecord is null and enrolledInHiv is null) \n" +
				"or (tracing_status = 'Contacted but not linked' and enrolledInHiv is null) \n" +
				"or (tracing_status is null and enrolledInHiv is null);";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		return new EvaluatedCohort(newCohort, definition, context);
	}

}
