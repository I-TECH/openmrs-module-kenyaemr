package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLCurrentOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinition;
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
@Handler(supports = {ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinition.class})
public class ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinition definition = (ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry=" select distinct patient_id \n" +
				"from kenyaemr_etl.etl_laboratory_extract \n" +
				"where (visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate) \n" +
				"and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302));";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("endDate", endDate);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

		newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}
