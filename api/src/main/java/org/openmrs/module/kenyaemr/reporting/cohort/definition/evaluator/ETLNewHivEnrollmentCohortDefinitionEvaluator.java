package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLCurrentOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLNewHivEnrollmentCohortDefinition;
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
@Handler(supports = {ETLNewHivEnrollmentCohortDefinition.class})
public class ETLNewHivEnrollmentCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		ETLNewHivEnrollmentCohortDefinition definition = (ETLNewHivEnrollmentCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry=" select distinct e.patient_id " +
				"from kenyaemr_etl.etl_hiv_enrollment e " +
				"join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
				"where  e.entry_point <> 160563  and transfer_in_date is null " +
				"and date(e.visit_date) between :startDate and :endDate;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("endDate", endDate);
		builder.addParameter("startDate", startDate);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

		newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}
