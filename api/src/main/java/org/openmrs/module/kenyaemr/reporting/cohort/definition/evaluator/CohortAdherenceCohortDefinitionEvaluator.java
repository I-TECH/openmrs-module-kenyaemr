package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.CohortAdherenceCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluator for Global fund's cohort analysis report
 */
@Handler(supports = {CohortAdherenceCohortDefinition.class})
public class CohortAdherenceCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private final Log log = LogFactory.getLog(this.getClass());

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		CohortAdherenceCohortDefinition definition = (CohortAdherenceCohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		context.addParameterValue("startDate", context.getParameterValue("startDate"));

		String sql ="select   e.patient_id  " +
				"  from encounter e  " +
				"  inner join person p  " +
				"  on p.person_id=e.patient_id   " +
				"    where e.voided = 0  " +
				"    and p.voided=0   " +
				"    and e.encounter_datetime = (:startDate)  ";



		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(sql);
		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(sqlCohortDefinition, context);

		return new EvaluatedCohort(results, sqlCohortDefinition, context);
	}
  }
