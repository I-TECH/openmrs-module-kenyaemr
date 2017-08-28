package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsLinkageRegisterCohortDefinition;
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
@Handler(supports = {HTSClientsLinkageRegisterCohortDefinition.class})
public class HTSLinkageRegisterCohortDefinitionEvaluator implements EncounterQueryEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);

		String qry = "select encounter_id from (\n" +
				"select e.patient_id, e.encounter_id, e.encounter_datetime, pp.patient_id as internalEnrollment, l.contactStatus from \n" +
				"encounter e \n" +
				"inner join\n" +
				"(select encounter_type_id id, name from encounter_type where uuid=\"9c0a7a57-62ff-4f75-babe-5835b0e921b7\") et on et.id = e.encounter_type\n" +
				"left outer join patient_program pp on pp.patient_id = e.patient_id\n" +
				"left outer join (\n" +
				"select e.patient_id, e.encounter_id, \n" +
				"max(if(o.concept_id=164181 and o.value_coded=1650, 'Phone', if(o.concept_id=164181 and o.value_coded=162186, 'Physical', \"\"))) as contactType,\n" +
				"max(if(o.concept_id=164849 and o.value_coded=1065, 'Contacted and Linked', if(o.concept_id=164849 and o.value_coded=1066, 'Contacted but not linked', \"\"))) as contactStatus,\n" +
				"max(if(o.concept_id=162724,o.value_text, \"\")) as facilityLinkedTo,\n" +
				"max(if(o.concept_id=1473,o.value_text, \"\")) as providerHandedTo,\n" +
				"max(if(o.concept_id=162053,o.value_numeric, \"\")) as upnProvided\n" +
				"from encounter e\n" +
				"inner join form f on f.form_id = e.form_id and f.uuid = \"050a7f12-5c52-4cad-8834-863695af335d\"\n" +
				"inner join obs o on o.encounter_id = e.encounter_id and o.concept_id in (164181, 164849, 162724, 162053, 1473)\n" +
				"group by e.patient_id\n" +
				") l on l.encounter_id = e.encounter_id \n" +
				") t\n" +
				"where internalEnrollment is not null or contactStatus is not null; ";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);

		List<Integer> results = evaluationService.evaluateToList(builder, Integer.class, context);
		queryResult.getMemberIds().addAll(results);
		return queryResult;
	}

}
