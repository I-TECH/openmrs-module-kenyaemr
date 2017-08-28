package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSConfirmationRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSRegisterCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.evaluator.EncounterQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluator for patients for HTS Register
 */
@Handler(supports = {HTSConfirmationRegisterCohortDefinition.class})
public class HTSConfirmationRegisterCohortDefinitionEvaluator implements EncounterQueryEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);

		String qry = "(select distinct e.encounter_id from \n" +
				"encounter e \n" +
				"inner join form f on f.form_id = e.form_id and f.uuid = \"b08471f6-0892-4bf7-ab2b-bf79797b8ea4\"\n" +
				"inner join\n" +
				"(select encounter_type_id id, name from encounter_type where uuid=\"9c0a7a57-62ff-4f75-babe-5835b0e921b7\") et \n" +
				"on et.id = e.encounter_type\n" +
				" )\n" +
				"union\n" +
				"(select e.encounter_id \n" +
				"from encounter e inner join obs o \n" +
				"on o.encounter_id=e.encounter_id and o.concept_id=162084 and o.value_coded=162082\n" +
				"inner join\n" +
				"(select encounter_type_id id, name from encounter_type where uuid=\"9c0a7a57-62ff-4f75-babe-5835b0e921b7\") et \n" +
				"on et.id = e.encounter_type); ";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);

		List<Integer> results = evaluationService.evaluateToList(builder, Integer.class, context);
		queryResult.getMemberIds().addAll(results);
		return queryResult;
	}

}
