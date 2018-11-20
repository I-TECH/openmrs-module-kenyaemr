package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.maternity;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.MaternityARVProphylaxisToBabyAtMaternityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.MaternityBabyConditionDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PersonDataDefinition
 */
@Handler(supports= MaternityBabyConditionDataDefinition.class, order=50)
public class MaternityBabyConditionDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "       patient_id,\n" +
                "       (case baby_condition when 135436 then \"Macerated Stillbirth\" when 159916 then \"Fresh stillbirth\" when 151849 then \"Liveborn, Unspecified Whether Single, Twin, or Multiple \"\n" +
                "                            when 125872 then \"STILLBIRTH\" when 126127 then \"Spontaneous abortion\"\n" +
                "                            when 164815 then \"Live birth, died before arrival at facility\"\n" +
                "                            when 164816 then \"Live birth, died after arrival or delivery in facility\" else \"\" end) as baby_condition\n" +
                "from kenyaemr_etl.etl_mchs_delivery;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}