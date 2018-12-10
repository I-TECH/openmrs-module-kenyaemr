package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPConfirmatoryDataDefinition;
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
@Handler(supports= HEIGivenNVPConfirmatoryDataDefinition.class, order=50)
public class HEIGivenNVPConfirmatoryDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "  f.patient_id,\n" +
                "  (case f.nvp_given when 80586 then \"Yes\" else \"No\" end) as nvp_given_twelve_months\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit f\n" +
                "  INNER JOIN kenyaemr_etl.etl_patient_demographics d ON\n" +
                "                                          d.patient_id = f.patient_id\n" +
                "WHERE f.dna_pcr_result = 703\n" +
                "GROUP BY patient_id";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}