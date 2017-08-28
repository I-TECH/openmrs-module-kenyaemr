package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.AppointmentsUnscheduledCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsCohortDefinition;
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
@Handler(supports = {HTSClientsCohortDefinition.class})
public class HTSClientsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		HTSClientsCohortDefinition definition = (HTSClientsCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry=" select distinct e.patient_id from \n" +
				"encounter e \n" +
				"inner join form f on f.form_id = e.form_id and f.uuid != \"b08471f6-0892-4bf7-ab2b-bf79797b8ea4\"\n" +
				"inner join\n" +
				"(select encounter_type_id id, name from encounter_type where uuid=\"9c0a7a57-62ff-4f75-babe-5835b0e921b7\") et \n" +
				"on et.id = e.encounter_type\n" +
				"where e.encounter_id not in (select e.encounter_id \n" +
				"from encounter e inner join obs o \n" +
				"on o.encounter_id=e.encounter_id and o.concept_id=162084 and o.value_coded=162082\n" +
				"inner join\n" +
				"(select encounter_type_id id, name from encounter_type where uuid=\"9c0a7a57-62ff-4f75-babe-5835b0e921b7\") et \n" +
				"on et.id = e.encounter_type);";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);

		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

		newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}
