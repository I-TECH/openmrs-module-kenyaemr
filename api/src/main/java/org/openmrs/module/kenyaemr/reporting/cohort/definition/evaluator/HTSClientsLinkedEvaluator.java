package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSLinkedClientsCohortDefinition;
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
 * Evaluator for Current on ART
 */
@Handler(supports = {HTSLinkedClientsCohortDefinition.class})
public class HTSClientsLinkedEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		HTSLinkedClientsCohortDefinition definition = (HTSLinkedClientsCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry=" select e.patient_id\n" +
				"from  \n" +
				"encounter e \n" +
				"inner join\n" +
				"(select encounter_type_id id, name from encounter_type where uuid=\"9c0a7a57-62ff-4f75-babe-5835b0e921b7\") et on et.id = e.encounter_type\n" +
				"inner join obs o on o.encounter_id = e.encounter_id and o.voided=0\n" +
				"where o.concept_id = 159811 and o.value_coded = 1065;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);

		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

		newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}
